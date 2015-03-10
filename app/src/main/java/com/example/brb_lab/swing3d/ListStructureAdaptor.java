package com.example.brb_lab.swing3d;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

public class ListStructureAdaptor extends BaseAdapter
{
    Context mContext;
    String[] mFilesName;
    String mPath;

    public ListStructureAdaptor(Context context,File[] file, String path)
    {
        mContext = context;
        mFilesName = new String[file.length+1];
        mFilesName[0] = "...";
        for(int i = 0; i < file.length; i++)
        {
            mFilesName[i+1] = file[i].getName().toString();
        }

        mPath = path;
    }

    @Override
    public int getCount() {
        return mFilesName.length;
    }

    @Override
    public Object getItem(int position) {
        return mFilesName[position];
    }

    @Override
    public long getItemId(int position) {
        return mFilesName[position].hashCode();
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LinearLayout layout = new LinearLayout(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        layout.setOrientation(LinearLayout.VERTICAL);

        TextView nameView = new TextView(mContext);
        nameView.setText(mFilesName[position]);
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,30);
        layout.addView(nameView,params);
        if(mFilesName[position] != "...")
        {
            TextView pathView = new TextView(mContext);
            pathView.setText(mPath);
            pathView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            layout.addView(pathView, params);
        }
        else
        {
            TextView pathView = new TextView(mContext);
            pathView.setText(" ");
            pathView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            layout.addView(pathView, params);
        }

        return layout;
    }

}

