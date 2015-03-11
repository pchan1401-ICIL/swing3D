package com.example.brb_lab.swing3d;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
public class ChanMain extends Activity
{
    private int showRange;
    private MyRenderer mRenderer;
    private MyGLSurfaceView mGLView;
    private int isRun = 0;
    static int RUNNING = 1;
    static int STOPPED = 0;
    private boolean breaker = false;
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

        radioButton1.setOnClickListener(new View.OnClickListener()  //when Rotate model
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

        radioButton2.setOnClickListener(new View.OnClickListener()  //when Rotate model
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
                Intent mFileListView = new Intent(getApplicationContext(),FileListViewActivity.class);
                startActivityForResult(mFileListView, 1);
            }
        });

        Button button2 = (Button) findViewById(R.id.button2);  //when Next Point
        button2.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                        MoreLine();
                        break;
                }
                return false;
            }
        });

        Button button3 = (Button) findViewById(R.id.button3);   //when Previous Point
        button3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        LessLine();
                        break;
                }
                return false;
            }
        });

        final Button button4 = (Button) findViewById(R.id.button4);   //when Automatically run or stop
        button4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(seekBar1.getMax() != seekBar1.getProgress())
                {
                    if (isRun == STOPPED) {
                        button4.setText("Stop");
                    } else if (isRun == RUNNING) {
                        button4.setText("Start");
                    }
                }

                autoRun();
            }

        });

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()           //SeekBar View
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                showRange=(seekBar1.getProgress()+1)*3;                 //change showRange to show how much show the line
                mRenderer.DrawTo(showRange);
                mGLView.requestRender();
                if(seekBar1.getProgress() == seekBar1.getMax())
                {
                    button4.setText("Start");
                }
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
        if (showRange < mRenderer.getLineLength() - 2) {
            seekBar1.setProgress(seekBar1.getProgress() + 1);
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
                if(isRun == STOPPED)
                {
                    isRun = RUNNING;
                    while (seekBar1.getProgress() < seekBar1.getMax()) {
                        if (seekBar1.getMax() == 0 || breaker) {
                            breakerOff();
                            break;
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandler.post(new Runnable() {
                            public void run() {
                                seekBar1.setProgress(seekBar1.getProgress() + 1);
                            }
                        });
                    }
                    isRun = STOPPED;
                }
                else if(isRun == RUNNING)
                {
                    mHandler.post(new Runnable() {
                        public void run() {
                            breakerOn();
                        }
                    });
                }
            }
        }).start();
    }
    public boolean breakerOn()
    {
        breaker = true;
        return breaker;
    }

    public boolean breakerOff()
    {
        breaker = false;
        return breaker;
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
                try {
                    String fileName = data.getStringExtra("file_name");

                    DataIOThread dataRead = new DataIOThread(fileName);
                    readDataInit(dataRead);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            break;
        }
    }
}