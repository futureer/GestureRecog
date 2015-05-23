package com.uav.activity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uav.gesture.R;
import com.uav.models.Accelerate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 采集数据
 */
public class SamplingActivity extends ActionBarActivity {

    private TextView sampleTxt;
    private SensorManager sensorManager;
    private Sensor sensor;
    private boolean isSampling = false;
    private ArrayList<Accelerate> samples;
    private File sampleDir;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sampling);
        sampleTxt = (TextView) findViewById(R.id.press_txt);
        sampleTxt.setOnTouchListener(touchListener);

        releaseText = Html.fromHtml("<html><p>按下开始</p><p>记录动作</p></html>");
        pressText = Html.fromHtml("<html><p>松开结束</p><p>记录动作</p></html>");
        input = (EditText) findViewById(R.id.input);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(myListener, sensor, SensorManager.SENSOR_DELAY_GAME);

        samples = new ArrayList<>(60);
//        sampleDir = new File(Environment.getExternalStorageDirectory().getPath() + "/GestureSamples/");
//        Log.d("sensor", sampleDir.exists() + "");
//        if (sampleDir.mkdir() || sampleDir.isDirectory()){
//            Log.d("sensor", sampleDir.getPath());
//        }
//
//        Log.d("sensor", "isSDCARDExist:" + isSdCardExist());
    }

    /**
     * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
     *
     * @return
     */
    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    private CharSequence pressText;
    private CharSequence releaseText;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (input.getText().toString().length() == 0 ||input.getText().toString().length() > 2 ) {
                        sampleTxt.setText("输入两位数的手势编号先∠( ᐛ 」∠)＿");
                    } else {
                        sampleTxt.setText(pressText);
                        isSampling = true;
                    }
                    sampleTxt.setBackgroundColor(Color.DKGRAY);
                    break;
                case MotionEvent.ACTION_UP:
                    sampleTxt.setText(releaseText);
                    sampleTxt.setBackgroundColor(Color.GREEN);
                    isSampling = false;
                    if (samples.size() > 30) {
                        writeFile(samples);
                        Toast.makeText(getApplication(), "手势No."
                                + input.getText().toString() + "采集成功", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(getApplication(), "时间太短，请重新采集", Toast.LENGTH_SHORT).show();
                    }
                    samples.clear();
                    break;
            }
            return true;
        }
    };

    private void writeFile(ArrayList<Accelerate> list) {
        String gestureNum = input.getText().toString();

        File file = new File(getExternalFilesDir(null), gestureNum + "_"
                + System.currentTimeMillis() + ".txt");
        Log.d("sensor", file.getPath());


        OutputStream os;
        try {
            os = new FileOutputStream(file, true);
            for (Accelerate sample : list) {
                os.write(sample.toString().getBytes());
                os.write("\r\n".getBytes());
            }
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private float[] gravity = {1,1,1};
    private float[] motions = new float[3];
    final SensorEventListener myListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent sensorEvent) {

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER & isSampling) {
                for (int i = 0; i < 3; i++) {
                    gravity[i] = (float) (gravity[i] * 0.1 + sensorEvent.values[i] * 0.9);
                    motions[i] = sensorEvent.values[i] - gravity[i];
                }

                Log.d("sensor", Arrays.toString(motions));
                samples.add(new Accelerate(motions));
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d("sensor", "onAccuracyChanged:" + accuracy);
        }

    };

    /**
     * 获取 已经存储的样本的数量
     *
     * @return
     */
    private String sampleProgress() {
        File dir = getExternalFilesDir(null);
        File[] sampleFiles = dir.listFiles();
        HashMap<String, Integer> count = new HashMap<>(10);
        for (int i = 0; i < 10; i++) {
            count.put(i + "", 0);
        }
        for (File f : sampleFiles) {
            if (f.getName().endsWith(".txt")) {
                String no = f.getName().substring(0, 1);
                int tmp = count.get(no);
                count.put(no, tmp + 1);
            }
        }
        String trainDataStatis = "当前已经存储的训练数据如下：\n";
        for (String key : count.keySet()) {
            trainDataStatis += "手势" + key + ":" + count.get(key) + "\n";
        }
        Log.d("sensor", trainDataStatis);
        return trainDataStatis;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sampling, menu);
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

    public void onPause() {
        sensorManager.unregisterListener(myListener);
        super.onPause();
    }
}
