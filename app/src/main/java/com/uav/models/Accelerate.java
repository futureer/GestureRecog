package com.uav.models;

/**加速度模型
 * Created by future on 2015/5/11 0011.
 */


public class Accelerate {

    public float x;
    public float y;
    public float z;
    double len;

    public Accelerate(float[] values) {
        this.x = values[0];
        this.y = values[1];
        this.z = values[2];
        len = Math.sqrt(x*x + y*y + z*z);
    }

    public Accelerate(float x,float y,float z){
        this.x = x;
        this.y = y;
        this.z = z;
        len = Math.sqrt(x*x + y*y + z*z);
    }

    public static double cosDistance(Accelerate a1, Accelerate a2) {
        return (a1.x * a2.x + a1.y * a2.y +  a1.z * a2.z)/
                (a1.len * a2.len);
    }

    public String toString() {
        return x + " " + y + " " + z;
    }
}
