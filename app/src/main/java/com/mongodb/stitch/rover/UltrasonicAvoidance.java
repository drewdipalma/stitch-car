package com.mongodb.stitch.rover;


import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

import static java.lang.Boolean.TRUE;

// Translation of the Ultrasonic Avoidance class (Ultrasonic_Avoidance.py)
// https://github.com/sunfounder/SunFounder_Ultrasonic_Avoidance/blob/5d8d62b73f8ecf83460097d0f943741853563cc2/Ultrasonic_Avoidance.py
public class UltrasonicAvoidance {

    public long timeout = 50;
    public String channel;

    public PeripheralManager manager = PeripheralManager.getInstance();
    public Gpio mGpio;


    UltrasonicAvoidance(String channel) throws IOException {
        this.channel = channel;
        this.mGpio = manager.openGpio(channel);
    }

    public int distance() throws IOException, InterruptedException {
        long pulse_end = 0;
        long pulse_start = 0;

        mGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
        Thread.sleep(10);
        mGpio.setActiveType(Gpio.ACTIVE_HIGH);
        Thread.sleep(1);
        mGpio.setActiveType(Gpio.ACTIVE_LOW);
        mGpio.setDirection(Gpio.DIRECTION_IN);

        long start = System.currentTimeMillis();

        while(mGpio.getValue() == false){
            pulse_start = System.currentTimeMillis();
            if(pulse_start - start > timeout){
                return -1;
            }
        }

        while(mGpio.getValue() == true){
            pulse_end =  System.currentTimeMillis();
            if(pulse_start - start > timeout){
                return -1;
            }
        }

        if(pulse_start != 0 && pulse_end != 0){
            long pulse_duration = pulse_end - pulse_start;
            int distance = (int) (pulse_duration * 100 * 343.0 /2);

            if (distance >= 0){
                return distance;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }


    public int getDistance() throws IOException, InterruptedException {
        int mount = 5;
        int sum = 0;

        for(int i = 0; i < mount; i++){
            sum += distance();
        }

        return sum/mount;
    }

    public int lessThan(int alarmGate) throws IOException, InterruptedException {
        int dis = getDistance();

        if(dis >=0 && dis <= alarmGate){
            return 1;
        } else if(dis > alarmGate){
            return 0;
        } else {
            return -1;
        }
    }

    public static void test() throws IOException, InterruptedException {
        UltrasonicAvoidance UA = new UltrasonicAvoidance("BCM17");
        int threshold = 10;
        String TAG = "UA TESTING";

        while(TRUE) {
            int distance = UA.getDistance();
            int status = UA.lessThan(threshold);

            if (distance != -1) {
                Log.d(TAG, String.format("Distance %x and Statuc %x", distance, status));
            } else {
            }

            Thread.sleep(1);
        }
    }

}
