package com.uav.models;

import android.util.Log;

import java.util.ArrayList;

/**
 * 手势模型
 * Created by future on 2015/5/13 0013.
 */
public class Gesture {
    private ArrayList<Accelerate> gestureVector = new ArrayList<Accelerate>(60);
    double len;

    /**
     * 计算若干个加速度组成的两个手势之间的余弦相似度
     *
     * @param gesture1
     * @param gesture2
     * @return
     */
    public static double cosDistance(Gesture gesture1, Gesture gesture2) {
        Log.d("sensor", "Gesture.cosDistance gesture1 length" + gesture1.acceleratorSize());
        Log.d("sensor", "Gesture.cosDistance gesture2 length" + gesture2.acceleratorSize());
        gesture2 = normalize(gesture2,gesture1.acceleratorSize());
        double sum = 0;
        for (int i = 0; i < gesture1.acceleratorSize(); i++) {
            sum += Accelerate.cosDistance(gesture1.getSample(i),
                    gesture2.getSample(i));
        }
        return (-1) * sum / (gesture1.len * gesture2.len);
    }

    public Gesture(ArrayList<Accelerate> gesture) {
        this.gestureVector.addAll(gesture);
        setLen();
    }

    /**
     * 数据加完之后要手动 setLen()一下，提高计算效率
     */
    public Gesture() {
    }


    /**
     * 对两个将要计算相似度的加速度向量进行归一化，使用线性插值
     *
     * @param gesture
     * @return
     */
    private static Gesture normalize(Gesture gesture, int len) {
        Gesture tmpGesture = new Gesture();
        int Nformer = gesture.acceleratorSize();
        for (int i = 0; i < len; i++) {
            int num = (int) Math.floor(i*Nformer/len);
            tmpGesture.add(gesture.getSample(num));
        }
        tmpGesture.setLen();
        return tmpGesture;
    }

    /**
     * 手势向量中加速度样本的个数
     *
     * @return
     */
    public int acceleratorSize() {
        return gestureVector.size();
    }

    public Accelerate getSample(int index) {

        return gestureVector.get(index);
    }

    public int add(Accelerate accelerate) {
        this.gestureVector.add(accelerate);
        return gestureVector.size();
    }

    public void setLen() {
//        Log.d("sensor", gestureVector.get(0).toString());
//        Accelerate b = gestureVector.get(0);
        for (Accelerate a : gestureVector)
            len += a.len;
        len = Math.sqrt(len);
    }

    @Override
    public String toString() {
        return "Gesture{" +
                "len=" + len +
                ", gestureVector=" + gestureVector +
                '}';
    }
}
