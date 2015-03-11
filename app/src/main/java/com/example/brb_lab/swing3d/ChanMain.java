package com.example.brb_lab.swing3d;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.VideoView;

import java.io.File;

public class ChanMain extends Activity
{
    static int RUNNING = 1;
    static int STOPPED = 0;
    Handler mHandler = new Handler();
    RadioButton radioButton1;
    RadioButton radioButton2;
    FrameLayout frameLayout1;
    SeekBar seekBar1;
    boolean do_pause = false;
    private int showRange;
    private MyRenderer mRenderer;
    private MyGLSurfaceView mGLView;
    private int isRun = 0;
    private boolean breaker = false;
    ////////////////////////////////CHAO
    private VideoView videoView;
    private Button play;
    private Button pause;

    ///////////////////////////////////////CHAO
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
/////////////////////////////////////////////////////////////CHAO
        play = (Button) findViewById(R.id.play);
        pause = (Button) findViewById(R.id.pause);
        videoView = (VideoView) findViewById(R.id.videoView);
        //editText.setInputType(InputType.TYPE_CLASS_NUMBER);//输入类型为数字
        videoView.setMediaController(null);


        initVideoPath();
/////////////////////////////////////////////////////////////////CHAO
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
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!videoView.isPlaying()) {
                        videoView.resume();
                        videoView.start();
                        button4.setText("Stop");
                } else {
                        videoView.pause();
                        button4.setText("Start");
                    }
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
                if(fromUser)
                {
                    videoView.seekTo(progress*100);
                }
                if (seekBar1.getProgress() == seekBar1.getMax()) {
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

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {

                seekBar1.setMax(videoView.getDuration()/100);
                seekBar1.postDelayed(onEverySecond, 100);
            }
        });


    }

    protected void MoreLine() {
        if (showRange < mRenderer.getLineLength() - 2) {
            seekBar1.setProgress(seekBar1.getProgress() + 1);
        }
    }

    protected void LessLine() {
        if (showRange > 5) {
            seekBar1.setProgress(seekBar1.getProgress() - 1);
        }
    }

    public boolean breakerOn() {
        breaker = true;
        return breaker;
    }

    public boolean breakerOff() {
        breaker = false;
        return breaker;
    }

    private Runnable onEverySecond = new Runnable() {

        @Override
        public void run() {

            if(seekBar1 != null) {
                seekBar1.setProgress(videoView.getCurrentPosition()/100);
            }

            if(videoView.isPlaying()) {
                seekBar1.postDelayed(onEverySecond, 100);
            }

        }
    };

    public void readDataInit(DataIOThread ioThread)
    {
        String data = ioThread.readSwing();

        mRenderer.readButtonTapped(data);
        showRange = mRenderer.getLineLength();
        seekBar1.setMax((showRange - 1) / 3);
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
        switch (requestCode) {
            case 1:
                try {
                    String fileName = data.getStringExtra("file_name");

                    DataIOThread dataRead = new DataIOThread(fileName);
                    readDataInit(dataRead);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    //////////////////////////////////////
    private void initVideoPath() {
        File file = new File(Environment.getExternalStorageDirectory(), "myvideo.mp4");
        videoView.setVideoPath(file.getPath());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.suspend();
        }
    }


    ////////////////////////////////////CHAO
}