package com.g.formsubmission;

/**
 * Created by ganesh on 10-04-2017.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "FormsManager";

    private static final String FORM_CONTACTS = "Forms";

    private static final String FORM_ID = "form_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_MOB_NO = "mobile_number";
    private static final String KEY_EMAIL_ID = "email_id";
    private static final String KEY_ID_NO = "id_number";
    private static final String KEY_ID_TYP = "id_type";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + FORM_CONTACTS + "("
                + FORM_ID + " INTEGER PRIMARY KEY  AUTOINCREMENT," + KEY_ID_TYP + " TEXT,"
                + KEY_ID_NO + " TEXT," +  KEY_NAME + " TEXT,"+  KEY_EMAIL_ID + " TEXT," +  KEY_MOB_NO + " TEXT" +")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FORM_CONTACTS);
        onCreate(db);
    }

    public int  addForm(FormModel form)
    {
        int ret = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_TYP, form.get_id_type());
        values.put(KEY_ID_NO, form.get_id_no());
        values.put(KEY_NAME, form.get_name());
        values.put(KEY_EMAIL_ID, form.get_email_id());
        values.put(KEY_MOB_NO, form.get_mobile_number());
        ret =   (int)db.insert(FORM_CONTACTS, null, values);
        db.close();
        return ret;
    }

    FormModel getFormData(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(FORM_CONTACTS,new String[] { FORM_ID,
                        KEY_ID_TYP, KEY_ID_NO,KEY_NAME,KEY_EMAIL_ID,KEY_MOB_NO }, FORM_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        FormModel form = new FormModel(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5));
        return form;
    }

    public ArrayList<FormModel> getAllForms() {
        ArrayList<FormModel> contactList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + FORM_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                FormModel form = new FormModel();
                form.set_id(Integer.parseInt(cursor.getString(0)));
                form.set_id_type(cursor.getString(1));
                form.set_id_no(cursor.getString(2));
                form.set_name(cursor.getString(3));
                form.set_email_id(cursor.getString(4));
                form.set_mobile_number(cursor.getString(5));
                contactList.add(form);
            } while (cursor.moveToNext());
        }

        return contactList;
    }

    public int getFormsCount()
    {
        String countQuery = "SELECT  * FROM " + FORM_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

}

