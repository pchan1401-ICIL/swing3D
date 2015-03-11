package com.example.brb_lab.swing3d;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UpperActivity extends Activity {
    public Button.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button1://chanyoung
                    Intent mChanYoung = new Intent(getApplicationContext(), ChanMain.class);
                    startActivity(mChanYoung);
                    break;
                case R.id.button4://jonghwi
                    Intent mJongHwi = new Intent(getApplicationContext(), Jong_fileio.class);
                    startActivity(mJongHwi);
                    break;

            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upper);
        findViewById(R.id.button1).setOnClickListener(clickListener);
        findViewById(R.id.button2).setOnClickListener(clickListener);
        findViewById(R.id.button4).setOnClickListener(clickListener);
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }
}
