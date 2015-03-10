package com.example.brb_lab.swing3d;

import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import android.widget.SeekBar;

public class DataIOThread extends Thread
{
    String mFileName;
    public DataIOThread(String fileName)
    {
        mFileName = fileName;
    }

    protected String readSwing()
    {
        File file = new File(mFileName);
        String outPut = null;
        try
        {
            FileInputStream inputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuffer StrBuf = new StringBuffer();
            String aLine = "";
            while(aLine != null)
            {
                aLine = reader.readLine();
                if(aLine != null)
                {
                    StrBuf.append(aLine + "\n");
                }
            }
            outPut = StrBuf.toString();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return outPut;
    }
}
