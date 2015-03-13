package com.example.brb_lab.swing3d;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jong on 2015-03-11.
 */
public class Jong_fileio extends Activity implements SensorEventListener {
    //thread 설정
    private Handler mHandler;
    private Runnable mRunnable;
    private String filename;
    FileOutputStream outstream;
    OutputStreamWriter writer;
    public int startFlag=0;


    //센서 값 설정
    float[] acceleration = new float[3];
    float[] rotationRate = new float[3];
    float[] magneticField = new float[3];
    //센서 설정
    private SensorManager mSensorManager;
    private Sensor mSensor_Acc,mSensor_mag,mSensor_gyro,mSensor_test;


    EditText setuptime;
    EditText sensorinfo;
    String sdCardPath;
    String contents;
    StringBuffer strbuf;
    int time=0;
    final Calendar c = Calendar.getInstance();
    int nowSec=0;
    int periodsec=10;//기본 10초로 설정


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jong_main);
        /////////////time setup////////////////////////////

        /////////////////////////////////////////////////
        setuptime = (EditText) findViewById(R.id.setuptime);
        sensorinfo =(EditText)findViewById(R.id.Sensordataeditview);
        findViewById(R.id.Save).setOnClickListener(clickListener);
        findViewById(R.id.load).setOnClickListener(clickListener);
        findViewById(R.id.Loadr).setOnClickListener(clickListener);
        findViewById(R.id.Delete).setOnClickListener(clickListener);
        findViewById(R.id.stop).setOnClickListener(clickListener);

        ////////////////////////////sd Card//////////////////////////////////////////////////
        String ext= Environment.getExternalStorageState();
        if(ext.equals(Environment.MEDIA_MOUNTED)){
            sdCardPath=Environment.getExternalStorageDirectory().getAbsolutePath();
        }else{
            sdCardPath=Environment.MEDIA_UNMOUNTED;
        }

        ////////////////////////sensor setup.///////////////////////
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensor_Acc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor_gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensor_mag = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensor_test = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        ////////////////////////timer///////////////////////////////


    }


    public Button.OnClickListener clickListener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.Save://Save button
                    ///////////시작 시간 설정//////////////////
                    nowSec = c.get(Calendar.SECOND);
                    ////////////handler//////////////////////////

                    ////////////파일 저장////////////////////////
                    startFlag=1;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String currentDateandTime=sdf.format(new Date());
                    filename = currentDateandTime+"test.txt";
                    filename = "test.txt";
                    String dir = sdCardPath + "/jonghwi/";
                    File temp = new File(dir);
                    if (!temp.exists()) {
                        temp.mkdirs();
                    }
                    filename = dir + filename;
                    //writeToFile(filename);

                    /*
                    filename = "test.txt";
                    String dir = sdCardPath + "/jonghwi/";
                    File temp = new File(dir);
                    if (!temp.exists()) {
                        temp.mkdirs();
                    }
                    filename = dir + filename;
                    //writeToFile(filename);

                     writeToFile(filename);
*/



                    ///////////////////////////////////////////////////////////////////////////

                    break;
                case R.id.load://파일 읽기
                    try{
                        FileInputStream fis = openFileInput("test.txt");
                        byte[] data = new byte[fis.available()];
                        while (fis.read(data) != -1){}
                        fis.close();
                        setuptime.setText(new String(data));
                    }catch (FileNotFoundException fileNotFountError) {
                        setuptime.setText("File Not Found");
                    }catch(IOException ioe){
                        ioe.printStackTrace();
                    }
                    break;
                case R.id.Loadr://리소스 파일 읽기
                    try{
                        InputStream fres = getResources().openRawResource(R.raw.myfile);
                        InputStreamReader reader = new InputStreamReader(fres,"UTF-8");
                        char[] data = new char[fres.available()];
                        while(reader.read(data) !=-1){}
                        fres.close();
                        setuptime.setText(new String(data));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    break;
                case R.id.Delete://삭제
                    if(deleteFile("test.txt")){
                        setuptime.setText("성공적으로 삭제되었습니다. \n file Delete sucess");
                    }else{
                        setuptime.setText("파일 삭제 실패 하였습니다. \n Fail to Delete file");
                    }
                    break;
                case R.id.stop:
                    startFlag=0;


                    Toast.makeText(getApplicationContext(), "파일에 쓰기 성공", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    ////////////////////////////파일쓰기 함수///////////////////////////////////////
    public void writeToFile(String filename) {
        File file = new File(filename);
        try {
            outstream = new FileOutputStream(file,true);
            writer = new OutputStreamWriter(outstream);
            //    contents=sensorinfo.getText().toString();
            contents=strbuf.toString();

            writer.write(contents);
            writer.flush();
            writer.close();


        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(this, "파일에 쓰기 실패", Toast.LENGTH_LONG).show();
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /////////////////////센서가 변할때마다 활동하는 함수////////////////////
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            acceleration[0] = event.values[0];
            acceleration[1] = event.values[1];
            acceleration[2] = event.values[2];
        }
        if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE){
            rotationRate[0] = event.values[0];
            rotationRate[1] = event.values[1];
            rotationRate[2] = event.values[2];
        }
        if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            magneticField[0] = event.values[0];
            magneticField[1] = event.values[1];
            magneticField[2] = event.values[2];
        }

        Calendar calendar = Calendar.getInstance();
        String date = calendar.getTime().toString();
        strbuf = new StringBuffer();
        strbuf.append(date+"\n\n");
        strbuf.append("Accelometer Sensor Value" + "\n");
        strbuf.append(acceleration[0] + "\n");
        strbuf.append(acceleration[1] + "\n");
        strbuf.append(acceleration[2] + "\n\n");
        strbuf.append("Gyroscope Sensor Value" + "\n");
        strbuf.append(rotationRate[0] + "\n");
        strbuf.append(rotationRate[1] + "\n");
        strbuf.append(rotationRate[2] + "\n\n");
        strbuf.append("Magnetic Sensor Value" + "\n");
        strbuf.append(magneticField[0] + "\n");
        strbuf.append(magneticField[1] + "\n");
        strbuf.append(magneticField[2] + "\n\n");
        sensorinfo.setText(strbuf.toString());

        //////////////////////save//////////////////////////
        if(startFlag==1) {


            writeToFile(filename);
        }

    }


////////////////////////////////////////////////////////////////////////////


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {

        super.onResume();

        mSensorManager.registerListener(this, mSensor_Acc, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mSensor_mag, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mSensor_gyro, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {

        super.onPause();

        mSensorManager.unregisterListener(this);
    }
}


