package com.uav.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uav.gesture.R;
import com.uav.models.Accelerate;
import com.uav.models.Gesture;
import com.uav.models.Recognize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class GestureActivity extends Activity implements View.OnClickListener {

    private SensorManager sm;

    private Sensor aSensor;

    private TextView pressTv, resultTv;
    private Button startSampleBtn;

    public static final int DID_SAMPLING = 100;
    private ArrayList<Accelerate> samples;
    private static final String TAG = "sensor";
    private boolean isSampling = false;
    private CharSequence pressText;
    private CharSequence releaseText;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTv = (TextView) findViewById(R.id.reco_result_txt);
        pressTv = (TextView) findViewById(R.id.press_txt_reco);
        pressTv.setOnTouchListener(touchListener);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_GAME);
        startSampleBtn = (Button) findViewById(R.id.start_sample_btn);
        startSampleBtn.setOnClickListener(this);

        releaseText = Html.fromHtml("<html><p>按下开始</p><p>记录动作</p></html>");
        pressText = Html.fromHtml("<html><p>松开开始</p><p>识别动作</p></html>");

        //更新显示数据的方法
        samples = new ArrayList<>(90);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Recognize.getInstance(getExternalFilesDir(null).getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "加载训练数据失败，" +
                                    "请先采集手势数据", Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(GestureActivity.this, SamplingActivity.class);
                            startActivity(in);
                        }
                    });

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pressTv.setText(releaseText);
                        pressTv.setBackgroundColor(Color.GREEN);
                        Toast.makeText(getApplicationContext(), "加载了" + Recognize.dataSetCount + "条样本数据",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).start();
    }


    private float[] gravity = {0, -9.88f, 0};
    private float[] motions = new float[3];
    final SensorEventListener myListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent sensorEvent) {

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER && isSampling) {
                for (int i = 0; i < 3; i++) {
                    gravity[i] = (float) (gravity[i] * 0.1 + sensorEvent.values[i] * 0.9);
                    motions[i] = sensorEvent.values[i] - gravity[i];
                }
                samples.add(new Accelerate(motions));
                Log.d("sensor", "GestureActivity sample:" + Arrays.toString(motions));
                Log.d("sensor", "GestureActivity gravity:" + Arrays.toString(gravity));
            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d(TAG, "onAccuracyChanged");
        }

    };


    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isSampling = true;
                    pressTv.setText(pressText);
                    resultTv.setText("监测动作中...");
                    pressTv.setBackgroundColor(Color.DKGRAY);
                    break;
                case MotionEvent.ACTION_UP:
                    pressTv.setText(releaseText);
                    pressTv.setBackgroundColor(Color.GREEN);
                    isSampling = false;
                    resultTv.setText("识别中...");
                    int resultCode;
                    Log.d("sensor", "GestureActivity samples size" + samples.size());
                    if (samples.size() > 30) {
                        resultCode = Recognize.recognize(new Gesture(samples));
                        resultTv.setText("这是No." + resultCode + "手势");
                    } else {
                        resultTv.setText("手势时间太短");
                    }
                    samples.clear();
                    break;
            }
            return true;
        }
    };

  /*  public Handler handler = new Handler() {
        //在这里处理加速度样本，并进行手势识别
        //
        //并进行相应操作
        public void handleMessage(Message msg) {

            startRecoBtn.setClickable(true);
            isSampling = false;
            Recognize.recognize(samples);
            Log.d(TAG, "list:" + samples.toString());

            samples.clear();//识别完后清空样本列表
        }
    };
*/

    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.start_reco_btn:
//                isSampling = true;
//                //startRecoBtn.setClickable(false);
//                break;
            case R.id.start_sample_btn:
                Intent in = new Intent(GestureActivity.this, SamplingActivity.class);
                startActivity(in);
                break;
        }
    }

    public void onPause() {
        sm.unregisterListener(myListener);
        super.onPause();

    }

        /*   //每隔 TIME_INTERVAL 时间取一次样本，一共取20个加速度
       private void sample(float[] motions) {
           currentUpdate = System.currentTimeMillis();
           long timeInterval = currentUpdate - lastUpdate;
           if (timeInterval < TIME_INTERVAL | !isSampling)
               return;
           else if (count > MAX_SAMPLES) {
               Message msg = handler.obtainMessage();
               Log.d(TAG, count + "");
               msg.what = DID_SAMPLING;
               handler.sendMessage(msg);
               return;
           }
           lastUpdate = currentUpdate;
           count++;
           AccelerateSample sample = new AccelerateSample(motions);
           samples.add(sample);
       }
   */

}
