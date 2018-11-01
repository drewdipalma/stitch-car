package com.mongodb.stitch_car_test;

import android.util.Log;

import java.io.IOException;

public class Servo {
    // Servo driver class
    public int MIN_PULSE_WIDTH = 600;
    public int MAX_PULSE_WIDTH = 2400;
    public int DEFAULT_PULSE_WIDTH = 1500;
    public int FREQUENCY = 60;

    PWM pwm;

    public int channel;
    public int offset;
    public Boolean lock;
    public String bus_name;
    public int address;
    public int frequency;

    Servo(int channel, Integer offset, Boolean lock, String bus_name, Integer address){
        // Init a servo on specific channel, this offset
        if(channel < 0 || channel > 16){
            throw new java.lang.Error("Servo channel (\"{0}\") out of bounds");
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
            this.lock = Boolean.TRUE;
        }

        if (bus_name != null){
            this.bus_name = bus_name;
        }else{
            this.bus_name = "I2C1";
        }

        if (address != null){
            this.address = address;
        }else{
            this.address = 0x70;
        }

        this.pwm = new PWM(bus_name, address);
        this.frequency = FREQUENCY;

    }

    public void setup() throws IOException {
        this.pwm.setup();
    }

    public int angleToAnalog(int angle){
        // Calculate 12-bit analog value from given angle
        int pulse_wide = pwm.map(angle, 0, 180, MIN_PULSE_WIDTH, MAX_PULSE_WIDTH);
        return (int)((float) pulse_wide / 1000000 * this.frequency * 4096);
    }

    public int getFrequency() {return this.frequency;}

    public void setFrequency(int value) throws IOException {
        this.frequency = value;
        this.pwm.setFrequency(value);
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
        val += this.offset;
        this.pwm.write(this.channel, 0, val);
    }

    public static void test() throws IOException, InterruptedException {
        // Servo driver test on channel 1
        Servo a = new Servo(0, null, null, null, null);
        a.setup();
        int i;

        for(i = 45; i < 135; i += 5){
            Log.i("ServoTest1", "Value: " + i);
            a.write(i);
            Thread.sleep(1000);
        }

        for(i = 135; i > 45; i -= 5){
            Log.i("ServoTest2", "Value: " + i);
            a.write(i);
            Thread.sleep(1000);
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
