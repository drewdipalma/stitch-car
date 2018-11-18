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

package com.mongodb.stitch.rover;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;

// Translation of PCA9685 class (PCA9685.py)
// https://github.com/sunfounder/SunFounder_PCA9685/blob/4f3023c28c6a8e10ec1d6d9811b997757fb1e27a/PCA9685.py

/**
 * Servo driver class
 */
public class Servo implements Closeable {
  private static final String TAG = Servo.class.getSimpleName();

  private static final int MIN_PULSE_WIDTH = 600;
  private static final int MAX_PULSE_WIDTH = 2400;
  private static final int DEFAULT_PULSE_WIDTH = 1500;

  private static final String DEBUG_INFO = "DEBUG \"Servo.py\"";

  public static boolean DEBUG = true;
  public static int FREQUENCY = 60;

  private final int channel;
  private int offset;
  private final boolean lock;
  private final PCA9685 pca9685;
  private int frequency;

  /**
   * Init a servo on specific channel, this offset
   *
   * @param channel
   * @param offset
   * @param lock
   * @param busNumber
   * @param address
   */
  public Servo(
      final int channel,
      final int offset,
      final boolean lock,
      final Integer busNumber,
      final int address
  ) throws InterruptedException {

    // Init a servo on specific channel, this offset
    if (channel < 0 || channel > 16) {
      throw new IllegalArgumentException(String.format("Servo channel \"%d\" is not in (0, 15).", channel));
    }
    if (DEBUG) {
      Log.d(TAG, "Debug on");
    }
    this.channel = channel;
    this.offset = offset;
    this.lock = lock;

    this.pca9685 = new PCA9685(busNumber, address);
    this.setup();
    setFrequency(FREQUENCY);
    write(90);
  }

  public Servo(final int channel) throws InterruptedException {
    this(channel, 0, true, null, 0x40);
  }

  public void setup() throws InterruptedException {
    pca9685.setup();
  }

  /**
   * Calculate 12-bit analog value from giving angle
   *
   * @param angle
   * @return
   */
  public int angleToAnalog(final int angle) {
    final int pulseWide = PCA9685.map(angle, 0, 180, MIN_PULSE_WIDTH, MAX_PULSE_WIDTH);
    final int analogValue = (int) ((double) pulseWide / 1000000.0 * frequency * 4096.0);
    if (DEBUG) {
      Log.d(TAG, String.format("Angle %d equals analogValue %d", angle, analogValue));
    }
    return analogValue;
  }

  public int getFrequency() {
    return frequency;
  }

  public void setFrequency(final int value) throws InterruptedException {
    this.frequency = value;
    this.pca9685.setFrequency(value);
  }

  public int getOffset() {
    return this.offset;
  }

  public void setOffset(int value) {
    this.offset = value;
    if (DEBUG) {
      Log.d(TAG, String.format("Set offset to %d", this.offset));
    }
  }

  /**
   * Turn the servo to a given angle
   *
   * @param angle
   */
  public void write(int angle) {
    if (lock) {
      if (angle > 180) {
        angle = 180;
      }
      if (angle < 0) {
        angle = 0;
      }
    } else {
      throw new IllegalArgumentException(String.format("Servo \"%d\" turn angle \"%d\" is not in (0, 180)", this.channel, angle));
    }

    int val = angleToAnalog(angle);
    val += offset;
    pca9685.write(this.channel, 0, val);
    if (DEBUG) {
      Log.d(TAG, String.format("Turn angle = %d", angle));
    }
  }

  public boolean isDebug() {
    return DEBUG;
  }

  /**
   * Set if debug information shows
   *
   * @param debug
   */
  public void setDebug(final boolean debug) {
    DEBUG = debug;
    if (DEBUG) {
      Log.d(TAG, "Set debug on");
    } else {
      Log.d(TAG, "Set debug off");
    }
  }

  /**
   * Servo driver test on channel 1
   *
   * @throws InterruptedException
   */
  public static void test() throws InterruptedException {
    final Servo a = new Servo(0);
    for (int i = 0; i < 180; i += 5) {
      Log.d(TAG, Integer.toString(i));
      a.write(i);
      Thread.sleep(100);
    }
    for (int i = 180; i > 0; i -= 5) {
      Log.d(TAG, Integer.toString(i));
      a.write(i);
      Thread.sleep(100);
    }
    for (int i = 0; i < 91; i += 2) {
      Log.d(TAG, Integer.toString(i));
      a.write(i);
      Thread.sleep(50);
    }
  }

  public static void install() throws InterruptedException {
    final Servo allServo[] = new Servo[16];

    for (int i = 0; i < 16; i++) {
      allServo[i] = new Servo(i);
    }
    for (final Servo servo : allServo) {
      servo.setup();
      for (int angle : new int[] {45, 90, 135}) {
        servo.write(angle);
        Thread.sleep(1500);
      }
    }
  }

  @Override
  public void close() throws IOException {
    this.pca9685.close();
  }
}
