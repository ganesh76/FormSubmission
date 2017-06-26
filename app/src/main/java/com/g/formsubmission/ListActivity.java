package com.g.formsubmission;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    ListView listView;
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        db = new DatabaseHelper(this);
        listView = (ListView)findViewById(R.id.list_view);
        loadItems();
    }

    public void loadItems()
    {
        listView.setAdapter(new FormListAdapter(this,db.getAllForms()));
    }

    class FormListAdapter extends BaseAdapter
    {
        // UI Elements
        TextView id_typ,id_num,name,email,mob;
        private final Context context;
        private final ArrayList<FormModel> values;
        public FormListAdapter(Context context, ArrayList values) {
            this.context = context;
            this.values = values;
        }

        @Override
        public int getCount() {
            return values.size();
        }

        @Override
        public Object getItem(int position) {
            return values.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.aadharcard_list, parent, false);
            id_typ = (TextView) rowView.findViewById(R.id.tv_id_typ);
            id_num = (TextView) rowView.findViewById(R.id.tv_id_num);
            name = (TextView) rowView.findViewById(R.id.tv_name);
            email = (TextView) rowView.findViewById(R.id.tv_email);
            mob = (TextView) rowView.findViewById(R.id.tv_mob_no);
            FormModel fm = values.get(position);
            id_typ.setText(fm.get_id_type());
            id_num.setText(fm.get_id_no());
            name.setText(fm.get_name());
            email.setText(fm.get_email_id());
            mob.setText(fm.get_mobile_number());
            return rowView;
        }
    }
}
