package com.example.brb_lab.swing3d;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;

public class ChanMain extends Activity
{
    private int showRange;
    private int isDataRead = 1;
    static int FROM_DATA_READ = 1;
    static int NOT_FROM_DATA_READ = 0;
    private MyRenderer mRenderer;
    private MyGLSurfaceView mGLView;
    private int findRng = 0;
    Handler mHandler = new Handler();


    RadioButton radioButton1;
    RadioButton radioButton2;
    FrameLayout frameLayout1;
    SeekBar seekBar1;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chan);

        mGLView = new MyGLSurfaceView(this);
        mRenderer = new MyRenderer(this);
        frameLayout1 = (FrameLayout) findViewById(R.id.FrameLayout1);
        mGLView.setRenderer(mRenderer);
        mGLView.setRenderMode(MyGLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mGLView.setPreserveEGLContextOnPause(true);
        frameLayout1.addView(mGLView);

        radioButton1 = (RadioButton)findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton)findViewById(R.id.radioButton2);
        seekBar1 = (SeekBar)findViewById(R.id.seekBar1);
        seekBar1.setMax(0);

        radioButton1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(radioButton1.isChecked())
                {
                    mGLView.setMoveMode(MyGLSurfaceView.ROTATE_BUTTON);
                }
            }
        });

        radioButton2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(radioButton2.isChecked())
                {
                    mGLView.setMoveMode(MyGLSurfaceView.MOVE_BUTTON);
                }
            }
        });

        Button button1 = (Button) findViewById(R.id.button1);  //read file
        button1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                isDataRead = FROM_DATA_READ;
                Intent mFileListView = new Intent(getApplicationContext(),FileListViewActivity.class);
                startActivityForResult(mFileListView, 1);
            }
        });

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MoreLine();
            }
        });

        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LessLine();
            }
        });

        Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                autoRun();
            }
        });

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                showRange=(seekBar1.getProgress()+1)*3;
                mRenderer.DrawTo(showRange);
                mGLView.requestRender();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
    }
    protected void MoreLine()
    {
        if(showRange < mRenderer.getLineLength() - 2)
        {
            seekBar1.setProgress(seekBar1.getProgress()+1);
        }
    }

    protected void LessLine()
    {
        if(showRange > 5)
        {
            seekBar1.setProgress(seekBar1.getProgress()-1);
        }
    }

    public void autoRun()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                seekBar1.setProgress(0);
                while(seekBar1.getProgress() < seekBar1.getMax())
                {
                    if(seekBar1.getMax() == 0)
                    {
                        break;
                    }
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    mHandler.post(new Runnable()
                    {
                        public void run()
                        {
                            seekBar1.setProgress(seekBar1.getProgress()+1);
                        }
                    });
                }
            }
        }).start();
    }

    public void readDataInit(DataIOThread ioThread)
    {
        String data = ioThread.readSwing();

        mRenderer.readButtonTapped(data);
        showRange = mRenderer.getLineLength();
        seekBar1.setMax((showRange - 1)/3 );
        seekBar1.setProgress(seekBar1.getMax());
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mGLView.onPause();
    }

    protected void onResume()
    {
        super.onResume();
        mGLView.onResume();
    }

    protected void onStop()
    {
        super.onStop();
        mGLView.onPause();
    }

    protected void onStart()
    {
        super.onStart();
        mGLView.onResume();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 1:
                String fileName = data.getStringExtra("file_name");

                DataIOThread dataRead = new DataIOThread(fileName);
                readDataInit(dataRead);
            break;
        }
    }
}