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

// Translation of Back Wheels class (back_wheels.py)
// https://github.com/sunfounder/SunFounder_PiCar/blob/master/picar/back_wheels.py

import java.io.Closeable;
import java.io.IOException;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

class BackWheels implements Closeable {
  public String MotorA = "BCM17";
  public String MotorB = "BCM27";

  public int PWMA = 4;
  public int PWMB = 5;

  public PCA9685 motorPwmA;
  public PCA9685 motorPwmB;

  public Motor leftwheel;
  public Motor rightwheel;

  public Boolean caliForwardA;
  public Boolean caliForwardB;

  public int speed;

  BackWheels() throws IOException, InterruptedException {
    // Initialize the direction channel and pwm channel

    this.motorPwmA = new PCA9685();
    this.motorPwmB = new PCA9685();

    this.leftwheel = new Motor(MotorA, PWMA, motorPwmA, FALSE);
    this.rightwheel = new Motor(MotorB, PWMB, motorPwmB, FALSE);

    setSpeed(0);
  }

  public void forward() throws IOException, InterruptedException {
    leftwheel.forward();
    rightwheel.forward();
  }

  public void backward() throws IOException, InterruptedException {
    leftwheel.backward();
    rightwheel.backward();
  }

  public void stop() throws IOException, InterruptedException {
    leftwheel.stop();
    rightwheel.stop();
  }

  public int getSpeed() {
    return speed;
  }

  public void setSpeed(int speed) throws IOException, InterruptedException {
    this.speed = speed;
    leftwheel.setSpeed(speed);
    rightwheel.setSpeed(speed);
  }

  public void ready() throws IOException, InterruptedException {
    leftwheel.setOffset(TRUE);
    rightwheel.setOffset(TRUE);
    stop();
  }

  public void calibration() throws IOException, InterruptedException {
    // Get the front wheels to the calibration position.

    setSpeed(50);
    forward();
    this.caliForwardA = TRUE;
    this.caliForwardB = TRUE;
  }

  public void caliLeft() throws IOException, InterruptedException {
    // Reverse the left wheels forward direction in calibration

    caliForwardA = TRUE;
    leftwheel.setOffset(caliForwardA);
    forward();
  }


  public void caliRight() throws IOException, InterruptedException {
    // Reverse the left wheels forward direction in calibration

    caliForwardA = TRUE;
    leftwheel.setOffset(caliForwardA);
    forward();
  }

  @Override
  public void close() throws IOException {
    leftwheel.close();
    rightwheel.close();
  }

  public static void test() throws IOException, InterruptedException {
    BackWheels backWheels = new BackWheels();
    long DELAY = 10;
    int i;

    try {
      backWheels.forward();

      for (i = 0; i < 100; i++) {
        backWheels.setSpeed(i);
        Thread.sleep(DELAY);
      }

      for (i = 100; i > 0; i--) {
        backWheels.setSpeed(i);
        Thread.sleep(DELAY);
      }

      backWheels.backward();

      for (i = 0; i < 100; i++) {
        backWheels.setSpeed(i);
        Thread.sleep(DELAY);
      }

      for (i = 100; i > 0; i--) {
        backWheels.setSpeed(i);
        Thread.sleep(DELAY);
      }

      backWheels.stop();

    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }


  }
}
