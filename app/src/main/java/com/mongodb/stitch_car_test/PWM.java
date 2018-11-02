package com.mongodb.stitch_car_test;

import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import org.bson.Document;

import java.io.IOException;

// Translation of PWM class (PCA9685.py)
// https://github.com/sunfounder/SunFounder_PCA9685/blob/4f3023c28c6a8e10ec1d6d9811b997757fb1e27a/PCA9685.py

class PWM {
    private static final String TAG = "PWM_CLASS";
    private I2cDevice mDevice;

    public static final int MODE1 = 0x00;
    public static final int MODE2 = 0x01;
    public static final int PRESCALE = 0xFE;
    public static final int LED0_ON_L = 0x06;
    public static final int LED0_ON_H = 0x07;
    public static final int LED0_OFF_L = 0x08;
    public static final int LED0_OFF_H = 0x09;
    public static final int ALL_LED_ON_L = 0xFA;
    public static final int ALL_LED_ON_H = 0xFB;
    public static final int ALL_LED_OFF_L = 0xFC;
    public static final int ALL_LED_OFF_H = 0xFD;

    public static final byte SLEEP = 0x10;
    public static final byte ALLCALL = 0x01;
    public static final byte OUTDRV = 0x04;

    public final String bus_name;
    public final int address;
    public int frequency = 60;

    /* Constructs a PWM class from a  document. */
    PWM(String bus_name, Integer address) {
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
    }

    public void setup() throws IOException {
        try {
            openDevice();

            mDevice.writeRegByte(ALL_LED_ON_L, (byte) 0);
            mDevice.writeRegByte(ALL_LED_ON_H, (byte) 0);
            mDevice.writeRegByte(ALL_LED_OFF_L, (byte) 0);
            mDevice.writeRegByte(ALL_LED_OFF_H, (byte) 0);

            mDevice.writeRegByte(MODE2, OUTDRV);
            mDevice.writeRegByte(MODE1, ALLCALL);
            Thread.sleep(5);

            byte mode1 = mDevice.readRegByte(MODE1);
            mode1 = (byte) (mode1 & ~SLEEP);
            mDevice.writeRegByte(MODE1, mode1);
            Thread.sleep(5);

            closeDevice();
        } catch (IOException e) {
            Log.w(TAG, "Error on PeripheralIO API", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setFrequency(60);
    }

    public int getFrequency() {return frequency;}

    public void setFrequency(int freq) throws IOException {
        //Set PWM frequency

        setFrequency(freq);
        double prescale_value = 25000000.0;
        prescale_value /= 4096.0;
        prescale_value /= (float) freq;
        prescale_value -= 1.0;

        double prescale = Math.floor(prescale_value + 0.5);

        try {
            openDevice();
            byte old_mode = mDevice.readRegByte(MODE1);
            byte new_mode = (byte) ((old_mode & 0x7F) | ((byte) 0x10));
            mDevice.writeRegByte(MODE1, new_mode);
            mDevice.writeRegByte(PRESCALE, (byte) Math.floor(prescale));
            mDevice.writeRegByte(MODE1, old_mode);

            Thread.sleep(5);

            mDevice.writeRegByte(MODE1, (byte) (old_mode | 0x80));

            closeDevice();
        } catch (IOException e) {
            Log.w(TAG, "Error on PeripheralIO API", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void write(int channel, int on, int off) {
        //Set on and off value on specific channel
        try {
            openDevice();

            mDevice.writeRegByte(LED0_ON_L + 4 * channel, (byte) (on & 0xFF));
            mDevice.writeRegByte(LED0_ON_H + 4 * channel, (byte) (on >> 8));
            mDevice.writeRegByte(LED0_OFF_L + 4 * channel, (byte) (off & 0xFF));
            mDevice.writeRegByte(LED0_OFF_H + 4 * channel, (byte) (off >> 8));

            closeDevice();
        } catch (IOException e) {
            Log.w(TAG, "Error on PeripheralIO API", e);
        }
    }

    public void write_all_value(byte on, byte off){
        //Set on and off value on all channel
        try {
            openDevice();

            mDevice.writeRegByte(ALL_LED_ON_L, (byte) (on & 0xFF));
            mDevice.writeRegByte(ALL_LED_ON_H, (byte)(on >> 8));
            mDevice.writeRegByte(ALL_LED_OFF_L, (byte) (off & 0xFF));
            mDevice.writeRegByte(ALL_LED_OFF_H, (byte) (off >> 8));

            closeDevice();
        } catch (IOException e) {
            Log.w(TAG, "Error on PeripheralIO API", e);
        }
    }

    public void openDevice() throws IOException {
        PeripheralManager manager = PeripheralManager.getInstance();
        try{
            mDevice = manager.openI2cDevice(bus_name, address);
        } catch (IOException e) {
            Log.w(TAG, "Unable to open I2C device", e);
        }
    }

    public void closeDevice() throws IOException {
        if (mDevice != null) {
            try {
                mDevice.close();
                mDevice = null;
            } catch (IOException e) {
                Log.w(TAG, "Unable to close I2C device", e);
            }
        }
    }

    public int map (int x, int in_min, int in_max, int out_min, int out_max){
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

}
