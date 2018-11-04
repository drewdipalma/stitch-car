package com.mongodb.stitch_car_test;
/*
 * Copyright 2018-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.util.Log;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Translation of PCA9685 class (PCA9685.py)
// https://github.com/sunfounder/SunFounder_PCA9685/blob/4f3023c28c6a8e10ec1d6d9811b997757fb1e27a/PCA9685.py

/**
 * A PCA9685 control class for PCA9685.
 */
public class PCA9685 implements Closeable {
    private static final String TAG = PCA9685.class.getSimpleName();

    private static final int MODE1 = 0x00;
    private static final int MODE2 = 0x01;
    private static final int SUBADDR1 = 0x02;
    private static final int SUBADDR2 = 0x03;
    private static final int SUBADDR3 = 0x04;
    private static final int PRESCALE = 0xFE;
    private static final int LED0_ON_L = 0x06;
    private static final int LED0_ON_H = 0x07;
    private static final int LED0_OFF_L = 0x08;
    private static final int LED0_OFF_H = 0x09;
    private static final int ALL_LED_ON_L = 0xFA;
    private static final int ALL_LED_ON_H = 0xFB;
    private static final int ALL_LED_OFF_L = 0xFC;
    private static final int ALL_LED_OFF_H = 0xFD;

    private static final int RESTART = 0x80;
    private static final int SLEEP = 0x10;
    private static final int ALLCALL = 0x01;
    private static final int INVRT = 0x10;
    private static final int OUTDRV = 0x04;

    private static final List<String> RPI_REVISION_0 = Collections.singletonList("900092");
    private static final List<String> RPI_REVISION_1_MODULE_B = Arrays.asList("Beta", "0002", "0003", "0004", "0005", "0006", "000d", "000e", "000f");
    private static final List<String> RPI_REVISION_1_MODULE_A = Arrays.asList("0007", "0008", "0009");
    private static final List<String> RPI_REVISION_1_MODULE_BP = Arrays.asList("0010", "0013");
    private static final List<String> RPI_REVISION_1_MODULE_AP = Collections.singletonList("0012");
    private static final List<String> RPI_REVISION_2_MODULE_B = Arrays.asList("a01041", "a21041");
    private static final List<String> RPI_REVISION_3_MODULE_B = Arrays.asList("a02082", "a22082");
    private static final List<String> RPI_REVISION_3_MODULE_BP = Collections.singletonList("a020d3");

    private static final String DEBUG_INFO = "DEBUG \"PCA9685.py\":";

    private static int getBusNumber() {
        final String revision = getPiRevision();
        switch (revision) {
            case "0":
                return 0;
            case "1 Module B":
                return 0;
            case "1 Module A":
                return 0;
            case "1 Module B+":
                return 1;
            case "1 Module A+":
                return 0;
            case "2 Module B'":
                return 1;
            case "3 Module B":
                return 1;
            case "3 Module B+":
                return 1;
            default:
                throw new IllegalStateException("unknown pi revision " + revision);
        }
    }

    /**
     * Gets the version number of the Raspberry Pi board
     # Courtesy quick2wire-python-api
     # https://github.com/quick2wire/quick2wire-python-api
     # Updated revision info from: http://elinux.org/RPi_HardwareHistory#Board_Revision_History
     */
    private static String getPiRevision() {
        final File cpuInfoFile = new File("/proc/cpuinfo");
        final BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(cpuInfoFile));
        } catch (final FileNotFoundException e) {
            throw new IllegalStateException("expected /proc/cpuinfo to exist");
        }
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("Revision")) {
                    continue;
                }
                final String rev = line.substring(11);
                if (RPI_REVISION_0.contains(rev)) {
                    return "0";
                } else if (RPI_REVISION_1_MODULE_B.contains(rev)) {
                    return "1 Module B";
                } else if (RPI_REVISION_1_MODULE_A.contains(rev)) {
                    return "1 Module A";
                } else if (RPI_REVISION_1_MODULE_BP.contains(rev)) {
                    return "1 Module B+";
                } else if (RPI_REVISION_1_MODULE_AP.contains(rev)) {
                    return "1 Module A+";
                } else if (RPI_REVISION_2_MODULE_B.contains(rev)) {
                    return "2 Module B";
                } else if (RPI_REVISION_3_MODULE_B.contains(rev)) {
                    return "3 Module B";
                } else if (RPI_REVISION_3_MODULE_BP.contains(rev)) {
                    return "3 Module B+";
                } else {
                    throw new IllegalStateException("unknown revision " + rev);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        throw new IllegalStateException("expected to find revision");
    }

    private boolean debug = true;
    private final int busNumber;
    private final int address;
    private final I2cDevice bus;
    private int frequency;

    private static Map<Integer, I2cDevice> busToDevice = new HashMap<>();

    public PCA9685(final Integer busNumber, final Integer address) {
        if (busNumber != null){
            this.busNumber = busNumber;
        } else {
            this.busNumber = getBusNumber();
        }

        final String busName;
        switch (this.busNumber) {
            case 0:
                busName = "I2C0";
                break;
            case 1:
                busName = "I2C1";
                break;
            default:
                throw new IllegalStateException(String.format("do not know how to map bus number %d to name", this.busNumber));
        }

        if (address != null){
            this.address = address;
        } else {
            this.address = 0x40;
        }
        if (busToDevice.containsKey(busNumber)) {
            this.bus = busToDevice.get(busNumber);
        } else {
            final PeripheralManager manager = PeripheralManager.getInstance();
            try{
                this.bus = manager.openI2cDevice(busName, this.address);
                busToDevice.put(busNumber, bus);
            } catch (IOException e) {
                throw new IllegalStateException(String.format("expected to be able to open I2C device (busNumber=%x, address=%x)", this.busNumber, this.address), e);
            }
        }
    }

    public PCA9685() {
        this(getBusNumber(), 0x40);
    }

    /**
     * Init the class with bus number and address
     * @throws InterruptedException
     */
    public void setup() throws InterruptedException {
        if (debug) {
            Log.d(TAG, DEBUG_INFO + " Resetting PCA9685 MODE1 (without SLEEP) and MODE2");
        }

        writeAllValue(0, 0);
        writeByteData(MODE2, (byte) OUTDRV);
        writeByteData(MODE1, (byte) ALLCALL);
        Thread.sleep(5);

        byte mode1 = readByteData(MODE1);
        mode1 = (byte) (mode1 & ~SLEEP);
        writeByteData(MODE1, mode1);
        Thread.sleep(5);
    }

    /**
     * Write data to I2C with address
     * @param reg
     * @param value
     */
    public void writeByteData(final int reg, final byte value) {
        if (debug) {
            Log.d(TAG, String.format("Writing value %x to %x", value, reg));
        }

        try {
            this.bus.writeRegByte(reg, value);
        } catch (IOException e) {
            e.printStackTrace();
            checkI2C();
            throw new IllegalStateException("failed to write byte");
        }
    }

    public byte readByteData(final int reg) {
        byte data = 0;

        //if (debug) {
        //  Log.d(TAG, String.format("Reading value from %x", reg));
        //}

        try {
            data = this.bus.readRegByte(reg);
        } catch (final IOException e) {
            e.printStackTrace();
            checkI2C();
            throw new IllegalStateException("failed to read byte");
        }
        return data;
    }

    public void checkI2C() {
        final int busNumber = getBusNumber();
        Log.d(TAG, "\nYour Pi revision is: " + getPiRevision());
        Log.d(TAG, "I2C bus number is: " + busNumber);
        Log.d(TAG, "Checking I2C device:");
        // TODO: Implement
        throw new UnsupportedOperationException();
    }

    public int getFrequency() {
        return frequency;
    }

    /**
     * Set PCA9685 frequency
     * @param freq
     * @throws InterruptedException
     */
    public void setFrequency(final int freq) throws InterruptedException {
        if (debug) {
            Log.d(TAG, String.format("Setting frequency to %d", freq));
        }

        float prescaleValue = 25000000.0f;
        prescaleValue /= 4096.0;
        prescaleValue /= (float) freq;
        prescaleValue -= 1.0;

        if (debug) {
            Log.d(TAG, String.format("Setting PCA9685 frequency to %d Hz", freq));
            Log.d(TAG, String.format("Estimated pre-scale: %d", (int) prescaleValue));
        }
        double prescale = Math.floor(prescaleValue + 0.5);
        if (debug) {
            Log.d(TAG, String.format("Final pre-scale: %d", (int) prescale));
        }

        byte oldMode = readByteData(MODE1);
        byte newMode = (byte) ((oldMode & 0x7F) | 0x10);
        writeByteData(MODE1, newMode);
        writeByteData(PRESCALE, (byte) Math.floor(prescale));
        writeByteData(MODE1, oldMode);
        Thread.sleep(5);
        writeByteData(MODE1, (byte) (oldMode | 0x80));
    }

    /**
     * Set on and off value on specific channel
     * @param channel
     * @param on
     * @param off
     */
    public void write(final int channel, final int on, final int off) {
        if (debug) {
            Log.d(TAG, String.format("Set channel \"%d\" to value \"%d\"", channel, off));
        }
        writeByteData(LED0_ON_L + 4 * channel, (byte) (on & 0xFF));
        writeByteData(LED0_ON_H + 4 * channel, (byte) (on >> 8));
        writeByteData(LED0_OFF_L + 4 * channel, (byte)(off & 0xFF));
        writeByteData(LED0_OFF_H + 4 * channel, (byte) (off >> 8));
    }

    /**
     * Set on and off value on all channel
     * @param on
     * @param off
     */
    public void writeAllValue(final int on, final int off){
        if (debug) {
            Log.d(TAG, String.format("Set all channel to value %d", off));
        }
        writeByteData(ALL_LED_ON_L, (byte) (on & 0xFF));
        writeByteData(ALL_LED_ON_H, (byte) (on >> 8));
        writeByteData(ALL_LED_OFF_L, (byte) (off & 0xFF));
        writeByteData(ALL_LED_OFF_H, (byte) (off >> 8));
    }

    /**
     * To map the value from arange to another
     * @param x
     * @param inMin
     * @param inMax
     * @param outMin
     * @param outMax
     * @return
     */
    public static int map(final int x, final int inMin, final int inMax, final int outMin, final int outMax) {
        return (x - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }

    public boolean isDebug() {
        return debug;
    }

    /**
     * Set if debug information shows
     * @param debug
     */
    public void setDebug(final boolean debug) {
        this.debug = debug;
        if (debug) {
            Log.d(TAG, "Set debug on");
        } else {
            Log.d(TAG, "Set debug off");
        }
    }

    @Override
    public void close() throws IOException {
        this.bus.close();
    }
}
