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
import android.widget.TextView;
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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jong on 2015-03-11.
 */
public class Jong_fileio extends Activity implements SensorEventListener {
    final Calendar c = Calendar.getInstance();
    long starttime;
    long currentTime;
    public int startFlag = 0;
    FileOutputStream outstream;
    OutputStreamWriter writer;
    //센서 값 설정
    float[] acceleration = new float[3];
    float[] rotationRate = new float[3];
    float[] magneticField = new float[3];
    // angular speeds from gyro
    private float[] gyro = new float[3];
    // magnetic field vector
    private float[] magnet = new float[3];

    // accelerometer vector
    private float[] accel = new float[3];
    // accelerometer and magnetometer based rotation matrix
    private float[] rotationMatrix = new float[9];
    // rotation matrix from gyro data
    private float[] gyroMatrix = new float[9];
    // orientation angles from accel and magnet
    private float[] accMagOrientation = new float[3];
    // final orientation angles from sensor fusion
    private float[] fusedOrientation = new float[3];
    // orientation angles from gyro matrix
    private float[] gyroOrientation = new float[3];
    public static final float EPSILON = 0.000000001f;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timestamp;
    private boolean initState = true;

    public static final int TIME_CONSTANT = 30;
    public static final float FILTER_COEFFICIENT = 0.98f;
    private Timer fuseTimer = new Timer();

    EditText setuptime;
    TextView sensorinfo;
    String sdCardPath;
    String contents;
    StringBuffer strbuf;
    int time=0;
    int nowSec=0;
    int periodsec=10;//기본 10초로 설정
    //thread 설정
    private Handler mHandler;
    private Runnable mRunnable;
    private String filename;
    public Button.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.Save://Save button
                    starttime = new Date().getTime();
                    ///////////시작 시간 설정//////////////////
                    nowSec = c.get(Calendar.SECOND);
                    ////////////handler//////////////////////////

                    ////////////파일 저장////////////////////////
                    startFlag = 1;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String currentDateandTime = sdf.format(new Date());
                    filename = currentDateandTime + "SensorData.txt";
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
                    try {
                        FileInputStream fis = openFileInput("test.txt");
                        byte[] data = new byte[fis.available()];
                        while (fis.read(data) != -1) {
                        }
                        fis.close();
                        setuptime.setText(new String(data));
                    } catch (FileNotFoundException fileNotFountError) {
                        setuptime.setText("File Not Found");
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    break;
                case R.id.Loadr://리소스 파일 읽기
                    try {
                        InputStream fres = getResources().openRawResource(R.raw.myfile);
                        InputStreamReader reader = new InputStreamReader(fres, "UTF-8");
                        char[] data = new char[fres.available()];
                        while (reader.read(data) != -1) {
                        }
                        fres.close();
                        setuptime.setText(new String(data));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.Delete://삭제
                    if (deleteFile("test.txt")) {
                        setuptime.setText("성공적으로 삭제되었습니다. \n file Delete sucess");
                    } else {
                        setuptime.setText("파일 삭제 실패 하였습니다. \n Fail to Delete file");
                    }
                    break;
                case R.id.stop:
                    startFlag = 0;


                    Toast.makeText(getApplicationContext(), "파일에 쓰기 성공", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    //센서 설정
    private SensorManager mSensorManager;
    private Sensor mSensor_Acc, mSensor_mag, mSensor_gyro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jong_main);
        /////////////sensorfusionactivity////////////////////////////
        fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(),1000, TIME_CONSTANT);

        /////////////////////////////////////////////////
        setuptime = (EditText) findViewById(R.id.setuptime);
        sensorinfo = (TextView) findViewById(R.id.Sensordataeditview);
        findViewById(R.id.Save).setOnClickListener(clickListener);
        findViewById(R.id.load).setOnClickListener(clickListener);
        findViewById(R.id.Loadr).setOnClickListener(clickListener);
        findViewById(R.id.Delete).setOnClickListener(clickListener);
        findViewById(R.id.stop).setOnClickListener(clickListener);

        ////////////////////////////sd Card//////////////////////////////////////////////////
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            sdCardPath = Environment.MEDIA_UNMOUNTED;
        }

        ////////////////////////sensor setup.///////////////////////
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor_Acc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor_gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensor_mag = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        ////////////////////////timer///////////////////////////////


    }

    ////////////////////////////파일쓰기 함수///////////////////////////////////////
    public void writeToFile(String filename) {
        File file = new File(filename);
        try {
            outstream = new FileOutputStream(file,true);
            writer = new OutputStreamWriter(outstream);
            //    contents=sensorinfo.getText().toString();
            contents=strbuf.toString();
            writer.write("T"+String.valueOf((currentTime-starttime)));

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
            // copy new accelerometer data into accel array and calculate orientation
            System.arraycopy(event.values, 0, accel, 0, 3);
            calculateAccMagOrientation();
        }
        if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE){
            rotationRate[0] = event.values[0];
            rotationRate[1] = event.values[1];
            rotationRate[2] = event.values[2];
            // process gyro data
            gyroFunction(event);
        }
        if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            magneticField[0] = event.values[0];
            magneticField[1] = event.values[1];
            magneticField[2] = event.values[2];
            // copy new magnetometer data into magnet array
            System.arraycopy(event.values, 0, magnet, 0, 3);
        }

        //Calendar calendar = Calendar.getInstance();
        //String date = calendar.getTime().toString();
        strbuf = new StringBuffer();
        //strbuf.append(date+"\n");
        //String str = String.format("%1$tY.%1$tm.%1$td %1$tH:%1$tM:%1$tS.%1$tL", new Date().getTime());
        //currentTime = String.format("%1$tH:%1$tM:%1$tS.%1$tL", new Date().getTime());
        currentTime = new Date().getTime();
        //strbuf.append(currentTime+"\n");
        strbuf.append("\n"+"A"+acceleration[0] + "\n");
        strbuf.append("A"+acceleration[1] + "\n");
        strbuf.append("A"+acceleration[2] + "\n");
        strbuf.append("R"+rotationRate[0] + "\n");
        strbuf.append("R"+rotationRate[1] + "\n");
        strbuf.append("R"+rotationRate[2] + "\n");
        strbuf.append("M"+magneticField[0] + "\n");
        strbuf.append("M"+magneticField[1] + "\n");
        strbuf.append("M"+magneticField[2] + "\n\n");




        float roll2= -(float) ((float) Math.atan((acceleration[0]/Math.sqrt(acceleration[1]*acceleration[1]+acceleration[2]*acceleration[2])))*360/2/3.14);
        float roll= -(float) Math.atan((acceleration[0]/Math.sqrt(acceleration[1]*acceleration[1]+acceleration[2]*acceleration[2])));
        float pitch2 = (float) ((float) Math.atan2(acceleration[2],acceleration[1])*360/2/3.14-90);
        float pitch = (float) ((float) Math.atan2(acceleration[2],acceleration[1])-1.57);
        float yaw= (float) ((float) 360-((float) Math.atan2((magneticField[2]*Math.sin(roll)-magneticField[1]*Math.cos(roll)),(magneticField[0]*Math.cos(pitch)+magneticField[1]*Math.sin(pitch)*Math.sin(roll)+magneticField[2]*Math.sin(pitch)*Math.cos(roll)))*360/2/3.1415926565+180));
        float yaw1= (float) ((float) 6.28-((float) Math.atan2((magneticField[2]*Math.sin(roll)-magneticField[1]*Math.cos(roll)),(magneticField[0]*Math.cos(pitch)+magneticField[1]*Math.sin(pitch)*Math.sin(roll)+magneticField[2]*Math.sin(pitch)*Math.cos(roll)))+3.141592));

        strbuf.append(roll2+"\n"+pitch2+"\n"+yaw+"\n\n");
        strbuf.append((accMagOrientation[2]*180/Math.PI)+"\n"+(accMagOrientation[1]*180/Math.PI)+"\n"+(accMagOrientation[0]*180/Math.PI+"\n"));
        strbuf.append((gyroOrientation[2]*180/Math.PI)+"\n"+(gyroOrientation[1]*180/Math.PI)+"\n"+(gyroOrientation[0]*180/Math.PI));



        sensorinfo.setText(strbuf.toString());
        //////////////////////save//////////////////////////
        if(startFlag==1) {



            writeToFile(filename);
        }

    }


////////////////////////////////////////////////////////////////////////////
public void calculateAccMagOrientation() {
    if(SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)) {
        SensorManager.getOrientation(rotationMatrix, accMagOrientation);
    }
}

    public void gyroFunction(SensorEvent event) {
    // don't start until first accelerometer/magnetometer orientation has been acquired
    if (accMagOrientation == null)
        return;

    // initialisation of the gyroscope based rotation matrix
    if(initState) {
        float[] initMatrix = new float[9];
        initMatrix = getRotationMatrixFromOrientation(accMagOrientation);
        float[] test = new float[3];
        SensorManager.getOrientation(initMatrix, test);
        gyroMatrix = matrixMultiplication(gyroMatrix, initMatrix);
        initState = false;
    }

    // copy the new gyro values into the gyro array
    // convert the raw gyro data into a rotation vector
    float[] deltaVector = new float[4];
    if(timestamp != 0) {
        final float dT = (event.timestamp - timestamp) * NS2S;
        System.arraycopy(event.values, 0, gyro, 0, 3);
        getRotationVectorFromGyro(gyro, deltaVector, dT / 2.0f);
    }

    // measurement done, save current time for next interval
    timestamp = event.timestamp;

    // convert rotation vector into rotation matrix
    float[] deltaMatrix = new float[9];
    SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector);

    // apply the new rotation interval on the gyroscope based rotation matrix
    gyroMatrix = matrixMultiplication(gyroMatrix, deltaMatrix);

    // get the gyroscope based orientation from the rotation matrix
    SensorManager.getOrientation(gyroMatrix, gyroOrientation);
}
    private void getRotationVectorFromGyro(float[] gyroValues, float[] deltaRotationVector,  float timeFactor)
    {
        float[] normValues = new float[3];

        // Calculate the angular speed of the sample
        float omegaMagnitude =(float)Math.sqrt(gyroValues[0] * gyroValues[0] +	gyroValues[1] * gyroValues[1] +	gyroValues[2] * gyroValues[2]);

        // Normalize the rotation vector if it's big enough to get the axis
        if(omegaMagnitude > EPSILON) {
            normValues[0] = gyroValues[0] / omegaMagnitude;
            normValues[1] = gyroValues[1] / omegaMagnitude;
            normValues[2] = gyroValues[2] / omegaMagnitude;
        }

        // Integrate around this axis with the angular speed by the timestep
        // in order to get a delta rotation from this sample over the timestep
        // We will convert this axis-angle representation of the delta rotation
        // into a quaternion before turning it into the rotation matrix.
        float thetaOverTwo = omegaMagnitude * timeFactor;
        float sinThetaOverTwo = (float)Math.sin(thetaOverTwo);
        float cosThetaOverTwo = (float)Math.cos(thetaOverTwo);
        deltaRotationVector[0] = sinThetaOverTwo * normValues[0];
        deltaRotationVector[1] = sinThetaOverTwo * normValues[1];
        deltaRotationVector[2] = sinThetaOverTwo * normValues[2];
        deltaRotationVector[3] = cosThetaOverTwo;
    }
    private float[] getRotationMatrixFromOrientation(float[] o) {
    float[] xM = new float[9];
    float[] yM = new float[9];
    float[] zM = new float[9];

    float sinX = (float)Math.sin(o[1]);
    float cosX = (float)Math.cos(o[1]);
    float sinY = (float)Math.sin(o[2]);
    float cosY = (float)Math.cos(o[2]);
    float sinZ = (float)Math.sin(o[0]);
    float cosZ = (float)Math.cos(o[0]);

    // rotation about x-axis (pitch)
    xM[0] = 1.0f; xM[1] = 0.0f; xM[2] = 0.0f;
    xM[3] = 0.0f; xM[4] = cosX; xM[5] = sinX;
    xM[6] = 0.0f; xM[7] = -sinX; xM[8] = cosX;

    // rotation about y-axis (roll)
    yM[0] = cosY; yM[1] = 0.0f; yM[2] = sinY;
    yM[3] = 0.0f; yM[4] = 1.0f; yM[5] = 0.0f;
    yM[6] = -sinY; yM[7] = 0.0f; yM[8] = cosY;

    // rotation about z-axis (azimuth)
    zM[0] = cosZ; zM[1] = sinZ; zM[2] = 0.0f;
    zM[3] = -sinZ; zM[4] = cosZ; zM[5] = 0.0f;
    zM[6] = 0.0f; zM[7] = 0.0f; zM[8] = 1.0f;

    // rotation order is y, x, z (roll, pitch, azimuth)
    float[] resultMatrix = matrixMultiplication(xM, yM);
    resultMatrix = matrixMultiplication(zM, resultMatrix);
    return resultMatrix;
}
    private float[] matrixMultiplication(float[] A, float[] B) {
        float[] result = new float[9];

        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];

        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];

        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];

        return result;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {

        super.onResume();

        mSensorManager.registerListener(this, mSensor_Acc, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mSensor_mag, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mSensor_gyro, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {

        super.onPause();

        mSensorManager.unregisterListener(this);
    }
    class calculateFusedOrientationTask extends TimerTask {
        public void run() {
            float oneMinusCoeff = 1.0f - FILTER_COEFFICIENT;

            /*
             * Fix for 179� <--> -179� transition problem:
             * Check whether one of the two orientation angles (gyro or accMag) is negative while the other one is positive.
             * If so, add 360� (2 * math.PI) to the negative value, perform the sensor fusion, and remove the 360� from the result
             * if it is greater than 180�. This stabilizes the output in positive-to-negative-transition cases.
             */

            // azimuth
            if (gyroOrientation[0] < -0.5 * Math.PI && accMagOrientation[0] > 0.0) {
                fusedOrientation[0] = (float) (FILTER_COEFFICIENT * (gyroOrientation[0] + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation[0]);
                fusedOrientation[0] -= (fusedOrientation[0] > Math.PI) ? 2.0 * Math.PI : 0;
            }
            else if (accMagOrientation[0] < -0.5 * Math.PI && gyroOrientation[0] > 0.0) {
                fusedOrientation[0] = (float) (FILTER_COEFFICIENT * gyroOrientation[0] + oneMinusCoeff * (accMagOrientation[0] + 2.0 * Math.PI));
                fusedOrientation[0] -= (fusedOrientation[0] > Math.PI)? 2.0 * Math.PI : 0;
            }
            else {
                fusedOrientation[0] = FILTER_COEFFICIENT * gyroOrientation[0] + oneMinusCoeff * accMagOrientation[0];
            }

            // pitch
            if (gyroOrientation[1] < -0.5 * Math.PI && accMagOrientation[1] > 0.0) {
                fusedOrientation[1] = (float) (FILTER_COEFFICIENT * (gyroOrientation[1] + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation[1]);
                fusedOrientation[1] -= (fusedOrientation[1] > Math.PI) ? 2.0 * Math.PI : 0;
            }
            else if (accMagOrientation[1] < -0.5 * Math.PI && gyroOrientation[1] > 0.0) {
                fusedOrientation[1] = (float) (FILTER_COEFFICIENT * gyroOrientation[1] + oneMinusCoeff * (accMagOrientation[1] + 2.0 * Math.PI));
                fusedOrientation[1] -= (fusedOrientation[1] > Math.PI)? 2.0 * Math.PI : 0;
            }
            else {
                fusedOrientation[1] = FILTER_COEFFICIENT * gyroOrientation[1] + oneMinusCoeff * accMagOrientation[1];
            }

            // roll
            if (gyroOrientation[2] < -0.5 * Math.PI && accMagOrientation[2] > 0.0) {
                fusedOrientation[2] = (float) (FILTER_COEFFICIENT * (gyroOrientation[2] + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation[2]);
                fusedOrientation[2] -= (fusedOrientation[2] > Math.PI) ? 2.0 * Math.PI : 0;
            }
            else if (accMagOrientation[2] < -0.5 * Math.PI && gyroOrientation[2] > 0.0) {
                fusedOrientation[2] = (float) (FILTER_COEFFICIENT * gyroOrientation[2] + oneMinusCoeff * (accMagOrientation[2] + 2.0 * Math.PI));
                fusedOrientation[2] -= (fusedOrientation[2] > Math.PI)? 2.0 * Math.PI : 0;
            }
            else {
                fusedOrientation[2] = FILTER_COEFFICIENT * gyroOrientation[2] + oneMinusCoeff * accMagOrientation[2];
            }

            // overwrite gyro matrix and orientation with fused orientation
            // to comensate gyro drift
            gyroMatrix = getRotationMatrixFromOrientation(fusedOrientation);
            System.arraycopy(fusedOrientation, 0, gyroOrientation, 0, 3);

        }
    }
}


