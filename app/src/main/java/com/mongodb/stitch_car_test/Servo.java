package com.mongodb.stitch_car_test;

import android.util.Log;

import java.io.IOException;

import static java.lang.Boolean.TRUE;

// Translation of PWM class (PCA9685.py)
// https://github.com/sunfounder/SunFounder_PCA9685/blob/4f3023c28c6a8e10ec1d6d9811b997757fb1e27a/PCA9685.py
public class Servo {
    // Servo driver class
    public int MIN_PULSE_WIDTH = 600;
    public int MAX_PULSE_WIDTH = 2400;
    public int DEFAULT_PULSE_WIDTH = 1500;

    PWM pwm;

    public int channel;
    public int offset;
    public Boolean lock;
    public String bus_name;
    public int address;
    public int frequency;

    Servo(int channel, Integer offset, Boolean lock, String bus_name, Integer address) throws IOException {
        // Init a servo on specific channel, this offset
        if(channel < 0 || channel > 16){
            throw new java.lang.Error("Servo channel out of bounds");
        }

        this.channel = channel;

        if (offset != null){
            this.offset = offset;
        }else{
            this.offset = 0;
        }

        if (lock != null){
            this.lock = lock;
        }else{
            this.lock = TRUE;
        }

        if (bus_name != null){
            this.bus_name = bus_name;
        }else{
            this.bus_name = "I2C1";
        }

        if (address != null){
            this.address = address;
        }else{
            this.address = 0x40;
        }

        this.pwm = new PWM(bus_name, address);
        setFrequency(60);

        write(90);
    }

    public void setup() throws IOException {
        pwm.setup();
    }

    public int angleToAnalog(int angle){
        // Calculate 12-bit analog value from given angle
        int pulse_wide = pwm.map(angle, 0, 180, MIN_PULSE_WIDTH, MAX_PULSE_WIDTH);
        int analog_value = (int)((float) pulse_wide / 1000000.0 * frequency * 4096.0);
        Log.i("ServoTest2", "Angle value: " + angle);
        Log.i("ServoTest2", "Pulse value: " + pulse_wide);
        Log.i("ServoTest2", "Analog value: " + analog_value);
        return analog_value;
    }

    public int getFrequency() {return frequency;}

    public void setFrequency(int value) throws IOException {
        setFrequency(value);
        pwm.setFrequency(value);
    }

    public int getOffset() {return this.offset;}

    public void setOffset(int value){this.offset = value;}

    public void write(int angle){
        //Turn the servo to a given angle
        if(lock) {
            if (angle > 180) {
                angle = 180;
            }
            if (angle < 0) {
                angle = 0;
            }
        }else{
            throw new java.lang.Error("Servo turn angle out of bounds");
        }

        int val = angleToAnalog(angle);
        val += offset;
        pwm.write(channel, 0, val);
    }

    public static void test() throws IOException, InterruptedException {
        // Servo driver test on channel 1
        Servo a = new Servo(0, null, null, null, null);
        a.setup();
        int i;

        for(i = 45; i < 135; i += 5){
            Log.i("ServoTest1", "Value: " + i);
            a.write(i);
            Thread.sleep(100);
        }

        for(i = 135; i > 45; i -= 5){
            Log.i("ServoTest2", "Value: " + i);
            a.write(i);
            Thread.sleep(100);
        }

        for(i = 45; i < 91; i += 2){
            Log.i("ServoTest2", "Value: " + i);
            a.write(i);
            Thread.sleep(100);
        }
    }

    public static void install() throws IOException {
        Servo all_servo[] = new Servo[16];

        for(int i = 0; i < 16; i++){
            all_servo[i] = new Servo(i, null,null,null,null);
            all_servo[i].setup();
            all_servo[i].write(90);
        }
    }


}
