package com.example.brb_lab.swing3d;

import java.io.File;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class FileListViewActivity extends Activity
{
    private ListView listView1;
    private String route = "/sdcard/";
    private File[] mFiles;
    private TextView textView1;
    private int routeNo = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileview);

        listView1 = (ListView) findViewById(R.id.listView1);
        textView1 = (TextView)findViewById(R.id.textView1);

        textView1.setText(route);
        textView1.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30);

        FileProfile();

        listView1.setAdapter(new ListStructureAdaptor(this, mFiles, route));
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(!parent.getAdapter().getItem(position).toString().toLowerCase().contains("."))
                {
                    route += parent.getAdapter().getItem(position).toString() + "/";
                    routeNo++;
                }
                else if(parent.getAdapter().getItem(position).toString() == "...")
                {
                    if (routeNo != 0)
                    {
                        String[] routePart = route.split("/");

                        for (int i = 0; i < routePart.length - 1; i++)
                        {
                            route = "/" + routePart[i].toString();
                        }
                        route += "/";
                        routeNo--;
                    }
                }
                else if(parent.getAdapter().getItem(position).toString().endsWith(".swg"))
                {
                    Intent intent = new Intent();
                    intent.putExtra("file_name",route + parent.getAdapter().getItem(position).toString());

                    setResult(RESULT_OK, intent);

                    finish();
                }
                FileProfile();
                listView1.setAdapter(new ListStructureAdaptor(getBaseContext(), mFiles, route));
                textView1.setText(route);
            }
        });
    }

    protected void FileProfile()
    {
        File fp = new File(route);
        if(fp.exists()==false)
        {
            return;
        }
        mFiles = fp.listFiles();
    }
}

