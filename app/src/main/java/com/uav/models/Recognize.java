package com.uav.models;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by future on 2015/5/11 0011.
 */
public class Recognize {

    //正常情况下只有8个手势类别
    private static ArrayList<ArrayList<Gesture>> labeledTrainingData;
    public static int dataSetCount = 0;

    public static Recognize getInstance(String dirPath) throws IOException {
        if (null == labeledTrainingData)
            loadFromFile(dirPath);
        if (!isValid())
            throw new IOException("样本数据不足");
        return new Recognize();
    }

    private Recognize() {

    }

    /**
     * 将待分类手势数据与 训练数据挨个比对，找出最符合的一个训练数据所属于的手势
     *
     * @return
     */
    public static int recognize(Gesture gesture) {
        int min_index = 0;
        double min = 1.0;
        ArrayList<ArrayList<Double>> results = new ArrayList<>(labeledTrainingData.size());
        for (int i = 0; i < labeledTrainingData.size(); i++) {
            ArrayList<Double> classResult = new ArrayList<>(labeledTrainingData.get(i).size());
            for (int j = 0; j < labeledTrainingData.get(i).size(); j++) {
                Gesture g = labeledTrainingData.get(i).get(j);
                double temp = Gesture.cosDistance(gesture, g);
                classResult.add(temp);
                if (temp < min) {
                    min = temp;
                    min_index = i;
                }
                Log.d("sensor", "temp cos distance:" + temp + " i = " + i);
            }
            results.add(classResult);
        }
        Log.d("sensor", "min:" + min);
        return min_index;
    }


    private static void loadFromFile(String dirPath) throws IOException {
        File dir = new File(dirPath);
        if (!dir.isDirectory())
            throw new IOException("not a valid directory");

        File[] files = dir.listFiles();

        labeledTrainingData = new ArrayList<>(10);
        for (int k = 0; k < 10; k++) {
            ArrayList<Gesture> list = new ArrayList<>(10);
            labeledTrainingData.add(list);
        }

        for (File f : files) {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String name = f.getName();
            if (!name.endsWith(".txt"))
                continue;

            String line = reader.readLine();
            Gesture g = new Gesture();
            while (line != null) {
                String[] fl = line.split(" ");

                g.add(new Accelerate(Float.parseFloat(fl[0]),
                        Float.parseFloat(fl[1]), Float.parseFloat(fl[2])));
                line = reader.readLine();
            }
            g.setLen();
            labeledTrainingData.get(Integer.parseInt(name.substring(0, 1))).add(g);
            g = null;
            reader.close();
        }


    }

    private static boolean isValid() {
        int count = 0;
        for (ArrayList<Gesture> list : labeledTrainingData) {
            if (list.size() > 0)
                count++;
            dataSetCount += list.size();
            for(Gesture g : list)
                Log.d("sensor", "Recognize.isValid gesture size:" + g.acceleratorSize());
        }

        Log.d("sensor", "load data:" + count);

        return count > 1;
    }

}
