package com.mongodb.stitch.rover;

import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

public class BMP085 {

    public int BMP085_I2CADDR = 0x77;
    String TAG = "Adafruit_BMP.BMP085";

    //Operating Modes
    public int BMP085_ULTRALOWPOWER = 0;
    public int BMP085_STANDARD = 1;
    public int BMP085_HIGHRES = 2;
    public int BMP085_ULTRAHIGHRES = 3;

    //BMP085 Registers
    public int BMP085_CAL_AC1 = 0xAA;
    public int BMP085_CAL_AC2 = 0xAC;
    public int BMP085_CAL_AC3 = 0xAE;
    public int BMP085_CAL_AC4 = 0xB0;
    public int BMP085_CAL_AC5 = 0xB2;
    public int BMP085_CAL_AC6 = 0xB4;
    public int BMP085_CAL_B1 = 0xB6;
    public int BMP085_CAL_B2 = 0xB8;
    public int BMP085_CAL_MB = 0xBA;
    public int BMP085_CAL_MC = 0xBC;
    public int BMP085_CAL_MD = 0xBE;
    public int BMP085_CONTROL = 0xF4;
    public int BMP085_TEMPDATA = 0xF6;
    public int BMP085_PRESSUREDATA = 0xF6;

    // Commands
    public int BMP085_READTEMPCMD       = 0x2E;
    public int BMP085_READPRESSURECMD   = 0x34;

    public int mode = BMP085_STANDARD;
    public int address = BMP085_I2CADDR;

    public int cal_AC1;
    public int cal_AC2;
    public int cal_AC3;
    public int cal_AC4;
    public int cal_AC5;
    public int cal_AC6;
    public int cal_B1;
    public int cal_B2;
    public int cal_MB;
    public int cal_MC;
    public int cal_MD;

    public I2cDevice device;

    public BMP085(Integer mode, Integer address) throws IOException {
        //Check to see if the mode is valid
        if(mode < 0 || mode > 3){
            throw new IllegalArgumentException(String.format("Unexpected mode value \"%d\".", mode));
        }

        if(mode != null){
            this.mode = mode;
        }

        if(address != null){
            this.address = address;
        }

        //Create the I2C Device
        final PeripheralManager manager = PeripheralManager.getInstance();
        this.device = manager.openI2cDevice("I2C1", this.address);

        //Load calibration values.
        loadCalibration();
    }

    public int readBE(int reg) throws IOException {
        short result = device.readRegWord(reg);
        // Byte swap to account for Android Things reading in BE
        return ((result << 8) & 0xFF00) + (result >> 8);
    }

    public void loadCalibration() throws IOException {
        // This originally uses readS16BE (https://github.com/adafruit/Adafruit_Python_GPIO/blob/master/Adafruit_GPIO/I2C.py)
        // Read a signed 16-bit value from the specified register, in big endian byte order.
        // Default calibration values
        // cal_AC1 = 408
        // cal_AC2 = -72
        // cal_AC3 = -14383
        // cal_AC4 = 32741
        // cal_AC5 = 32757
        // cal_AC6 = 23153
        // cal_B1 = 6190
        // cal_B2 = 4
        // cal_MB = -32767
        // cal_MC = -8711
        // cal_MD = 2868

        this.cal_AC1 = readBE(BMP085_CAL_AC1);
        this.cal_AC2 = readBE(BMP085_CAL_AC2);
        this.cal_AC3 = readBE(BMP085_CAL_AC3);
        this.cal_AC4 = readBE(BMP085_CAL_AC4);
        this.cal_AC5 = readBE(BMP085_CAL_AC5);
        this.cal_AC6 = readBE(BMP085_CAL_AC6);
        this.cal_B1 = readBE(BMP085_CAL_B1);
        this.cal_B2 = readBE(BMP085_CAL_B2);
        this.cal_MB = readBE(BMP085_CAL_MB);
        this.cal_MC = readBE(BMP085_CAL_MC);
        this.cal_MD = readBE(BMP085_CAL_MD);

        Log.d(TAG, String.format("AC1 = \"%d\".", cal_AC1));
        Log.d(TAG, String.format("AC2 = \"%d\".", cal_AC2));
        Log.d(TAG, String.format("AC3 = \"%d\".", cal_AC3));
        Log.d(TAG, String.format("AC4 = \"%d\".", cal_AC4));
        Log.d(TAG, String.format("AC5 = \"%d\".", cal_AC5));
        Log.d(TAG, String.format("AC6 = \"%d\".", cal_AC6));
        Log.d(TAG, String.format("B1 = \"%d\".", cal_B1));
        Log.d(TAG, String.format("B2 = \"%d\".", cal_B2));
        Log.d(TAG, String.format("MB = \"%d\".", cal_MB));
        Log.d(TAG, String.format("MC = \"%d\".", cal_MC));
        Log.d(TAG, String.format("MD = \"%d\".", cal_MD));
    }

    public int read_raw_temp() throws IOException, InterruptedException {
        //Reads the raw (uncompensated) temperature from the sensor.
        device.writeRegByte(BMP085_CONTROL, (byte) BMP085_READTEMPCMD);
        Thread.sleep(5);
        int raw = readBE(BMP085_TEMPDATA);
        Log.d(TAG, String.format("Raw Temp = \"%d\".", raw));
        return raw;
    }
}