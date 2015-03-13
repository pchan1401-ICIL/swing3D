package com.example.brb_lab.swing3d;

import android.content.Context;
import android.opengl.GLSurfaceView;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer
{
    static int NO_BUTTON = 0;
    static int BUTTON_TAPPED = 1;
    ArrayList<Float> vertexBuff = new ArrayList<Float>();       //Save total Vertex
    private int inputButtonTapped = 0;
    private int readButtonTapped = 0;
    private Context mContext;
    private Line mLine;
    private GroundGL mGround;
    private float yAngle;
    private float xAngle;
    private float mAngle;
    private float mDx;
    private float mDy = -0.5f;
    private float mDz;
    private float posX = 0.0f;
    private float posY = 0.0f;
    private float posZ = 0.0f;
    private float sizeCoef = 1.0f;

    public MyRenderer(Context context)
    {
        mContext = context;
        mLine = new Line();
        mGround = new GroundGL();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config)
    {
        gl.glClearColor(0 / 255.0f, 191.0f / 255.0f, 255.0f / 255.0f, 1.0f);
        vertexBuff = mLine.getVertexes();
    }

    public void onDrawFrame(GL10 gl)
    {
        if(inputButtonTapped == BUTTON_TAPPED)
        {
            mLine.insertVertex(posX, posY, posZ);
            inputButtonTapped = NO_BUTTON;
        }
        else if(readButtonTapped == BUTTON_TAPPED)
        {
            readButtonTapped = NO_BUTTON;
        }
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LESS);
        gl.glLoadIdentity();
        gl.glTranslatef(mDx, mDy, mDz);
        gl.glLineWidth(5.0f);
        gl.glRotatef(xAngle, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(yAngle, 1.0f, 0.0f, 0.0f);
        gl.glScalef(sizeCoef, sizeCoef, sizeCoef);


        try
        {
            mLine.listToArray();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        mGround.draw(gl);
        mLine.draw(gl);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        gl.glViewport(0, 0, width, height);
        vertexBuff = mLine.getVertexes();
    }

    public void inputButtonTapped(float x, float y, float z)
    {
        posX = x;
        posY = y;
        posZ = z;
        inputButtonTapped = 1;
    }

    public ArrayList<Float> readButtonTapped(String data)
    {
        mLine.clear();
        ArrayList<Float> vertexes = new ArrayList<Float>();

        String dataAry[] = data.split("\n");
        for(int i = 0; i < dataAry.length; i++)
        {
            String coord[] = dataAry[i].split("/");
            float x = Float.parseFloat(coord[0].toString());
            float y = Float.parseFloat(coord[1].toString());
            float z = Float.parseFloat(coord[2].toString());
            mLine.insertVertex(x,y,z);
        }
        readButtonTapped = 1;
        vertexBuff = mLine.getVertexes();
        return mLine.getVertexes();
    }

    public void DrawTo(int it)
    {
        ArrayList<Float> iVertex = new ArrayList<Float>();
        try {
            for (int i = 0; i < it; i++) {
                iVertex.add(vertexBuff.get(i));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        mLine.setVertexes(iVertex);
    }

    public int getLineLength()
    {
        return vertexBuff.size();
    }
    public void insertVertexInLine(float x, float y, float z)
    {
        mLine.insertVertex(x,y,z);
    }

    public float getXAngle()
    {
        return xAngle;
    }

    public void setXAngle(float X)
    {
        xAngle = X;
    }

    public float getYAngle() {
        return yAngle;
    }

    public void setYAngle(float Y)
    {
        yAngle = Y;
    }

    public float getAngle()
    {
        return mAngle;
    }

    public void setAngle(float f)
    {
        mAngle = f;
    }

    public float getDx() {
        return mDx;
    }

    public void setDx(float dx)
    {
        mDx = dx;
    }

    public float getDy()
    {
        return mDy;
    }

    public void setDy(float dy)
    {
        mDy = dy;
    }

    public void scaleing(float size)
    {
        sizeCoef = size;
    }
    public float getSizeCoef()
    {
        return sizeCoef;
    }
    public ArrayList<Float> getVertexBuff()
    {
        return vertexBuff;
    }
    public void setVertexBuff(ArrayList<Float> vtex)
    {
        vertexBuff = vtex;
    }



}
