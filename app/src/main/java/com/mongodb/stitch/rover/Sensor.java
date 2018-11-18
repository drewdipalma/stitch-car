package com.mongodb.stitch.rover;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;

public class Sensor implements Closeable {
  private static final String TAG = "Sensor";

  private final int channel;
  private final PCA9685 pca9685;

  private static final int LED0_ON_L = 0x06;
  private static final int LED0_ON_H = 0x07;
  private static final int LED0_OFF_L = 0x08;
  private static final int LED0_OFF_H = 0x09;

  /**
   * Init a sensor on specific channel
   *
   * @param channel
   */
  public Sensor(
      final int channel
  ) throws InterruptedException {

    // Init a servo on specific channel, this offset
    if (channel < 0 || channel > 16) {
      throw new IllegalArgumentException(String.format("Servo channel \"%d\" is not in (0, 15).", channel));
    }

    this.channel = channel;
    this.pca9685 = new PCA9685();
  }

  public byte[] getI2CReading() throws IOException {
    byte[] readArray = new byte[] {pca9685.readByteData(LED0_ON_L + 4 * channel),
        pca9685.readByteData(LED0_ON_H + 4 * channel),
        pca9685.readByteData(LED0_OFF_L + 4 * channel),
        pca9685.readByteData(LED0_OFF_H + 4 * channel)};

    return readArray;
  }

  @Override
  public void close() throws IOException {
    this.pca9685.close();
  }

  public void test() {


  }
}
