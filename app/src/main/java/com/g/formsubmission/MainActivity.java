package com.g.formsubmission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import static com.g.formsubmission.MainActivity.DataAttributes.AADHAAR_DATA_TAG;
import static com.g.formsubmission.MainActivity.DataAttributes.AADHAR_NAME_ATTR;
import static com.g.formsubmission.MainActivity.DataAttributes.AADHAR_UID_ATTR;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    DatabaseHelper db;

    private EditText inputName, inputEmail, inputMobile,inputIdno;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutMobile,inputLayoutIdno;
    private Button btnSave,btnScan,btn_view;
    private Spinner id_spinner;
    private String[] types = new String[]{"Select ID Type","Aadhar Card","PAN","Passport"};
    private IntentIntegrator qrScan;
    String id_typ,uid,name;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    boolean isgranted = false;
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    isgranted = true;
                }
                else
                {
                    isgranted = false;
                }
                return;
            }
            default:
                isgranted = false;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},MY_PERMISSIONS_REQUEST_CAMERA);
        }
       /* Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=20.5666,45.345"));
        startActivity(intent);*/
       /* Uri gmmIntentUri = Uri.parse("google.navigation:q=birla temple");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);*/
        startActivity(new Intent(MainActivity.this,CameraOverlayExampe.class));
        finish();
        id_spinner = (Spinner)findViewById(R.id.type_spinner);
        id_spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        id_spinner.setAdapter(dataAdapter);
        inputLayoutIdno = (TextInputLayout) findViewById(R.id.input_layout_id_no);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutMobile = (TextInputLayout) findViewById(R.id.input_layout_mobile);
        inputIdno = (EditText) findViewById(R.id.input_id_no);
        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputMobile = (EditText) findViewById(R.id.input_mobile);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnScan = (Button)findViewById(R.id.btn_scan);
        btn_view = (Button)findViewById(R.id.view_forms);
        qrScan = new IntentIntegrator(this);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(isgranted)
                {
                    qrScan.initiateScan();
                }
                else
                {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},MY_PERMISSIONS_REQUEST_CAMERA);
                }
            }
        });

        inputIdno.addTextChangedListener(new MyTextWatcher(inputIdno));
        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputMobile.addTextChangedListener(new MyTextWatcher(inputMobile));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.getAllForms().size()==0)
                {
                    Toast.makeText(getApplication(),"No Forms Found",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    startActivity(new Intent(MainActivity.this,ListActivity.class));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            if (result.getContents() == null)
            {
                Toast.makeText(this, "Adhaar Card Result Not Found", Toast.LENGTH_LONG).show();
            }
            else
            {
                try
                {
                    Log.e("contents",result.getContents().toString());
                    //we have a result
                    String scanContent = result.getContents();
                    String scanFormat = result.getFormatName();
                    // process received data
                    processScannedData(scanContent);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to get Details", Toast.LENGTH_LONG).show();
                }
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void submitForm()
    {
        if (!validateIdtyp()) {
            return;
        }

        if (!validateIdno()) {
            return;
        }
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validateMobile()) {
            return;
        }
        FormModel fm  = new FormModel();
        fm.set_id_type(id_typ);
        fm.set_id_no(inputIdno.getText().toString().trim());
        fm.set_name(inputName.getText().toString().trim());
        fm.set_email_id(inputEmail.getText().toString().trim());
        fm.set_mobile_number(inputMobile.getText().toString().trim());
       if(db.addForm(fm)>=1)
       {
           Toast.makeText(getApplicationContext(), "Form Submitted Successfully", Toast.LENGTH_SHORT).show();
       }
        else
       {
           Toast.makeText(getApplicationContext(), "Unable to Insert", Toast.LENGTH_SHORT).show();
       }

    }

    private boolean validateIdtyp()
    {
        if (id_spinner.getSelectedItemPosition()==0)
        {
            Toast.makeText(getApplicationContext(),"Please Select ID Type",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateIdno()
    {
        if (inputIdno.getText().toString().trim().isEmpty()) {
            inputLayoutIdno.setError(getString(R.string.err_msg_id_no));
            requestFocus(inputIdno);
            return false;
        } else {
            inputLayoutIdno.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateMobile() {
        if (inputMobile.getText().toString().trim().isEmpty() || inputMobile.getText().toString().trim().length()!=10) {
            inputLayoutMobile.setError(getString(R.string.err_msg_mobile));
            requestFocus(inputMobile);
            return false;
        } else {
            inputLayoutMobile.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        id_typ = types[position];
        if(position == 1)
        {
            btnScan.setVisibility(View.VISIBLE);
            inputIdno.setText("");
            inputName.setText("");
        }
        else
        {
            btnScan.setVisibility(View.GONE);
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    protected void processScannedData(String scanData1) {
        XmlPullParserFactory pullParserFactory;

        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            String scanData = scanData1.substring(40);//used to delete malformed xml docs headers
            XmlPullParser parser = pullParserFactory.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(scanData1));

            // parse the XML
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT)
                {
                    Log.d("scanner", "start document");
                }
                else if (eventType == XmlPullParser.START_TAG && AADHAAR_DATA_TAG.equals(parser.getName()))
                {
                    uid = parser.getAttributeValue(null, AADHAR_UID_ATTR);
                    inputIdno.setText(uid);
                    name = parser.getAttributeValue(null, AADHAR_NAME_ATTR);
                    inputName.setText(name);

                } else if (eventType == XmlPullParser.END_TAG)
                {
                    Log.d("scanner", "End tag " + parser.getName());

                } else if (eventType == XmlPullParser.TEXT) {
                    Log.d("scanner", "Text " + parser.getText());

                }
                // update eventType
                eventType = parser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplication(),"Unable To scan data",Toast.LENGTH_SHORT).show();
            inputLayoutIdno.setEnabled(true);
            inputLayoutName.setEnabled(true);
        }
    }

        private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_id_no:
                    validateIdno();
                    break;
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_mobile:
                    validateMobile();
                    break;
            }
        }
    }
    public class DataAttributes {
        // xml attributes of aadhar card QR code xml response
        public static final String AADHAAR_DATA_TAG = "PrintLetterBarcodeData",
                AADHAR_UID_ATTR = "uid",
                AADHAR_NAME_ATTR = "name";
    }
}
