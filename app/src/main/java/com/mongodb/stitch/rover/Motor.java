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

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Pwm;

import java.io.Closeable;
import java.io.IOException;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

// Translation of TB6612 class (TB6612.py)
// https://github.com/sunfounder/SunFounder_PiCar/blob/master/picar/SunFounder_TB6612/TB6612.py
public class Motor implements Closeable {
    /*   Motor driver class
    Set direction_channel to the GPIO channel which connect to MA,
    Set motor_B to the GPIO channel which connect to MB,
    Both GPIO channel use BCM numbering;
    Set pwm_channel to the PCA9685 channel which connect to PWMA,
    Set pwm_B to the PCA9685 channel which connect to PWMB;
    PCA9685 channel using PCA9685, Set pwm_address to your address, if is not 0x40
    Set debug to True to print out debug informations.
    */

    public PCA9685 pwm;
    public Boolean offset;
    public Boolean forward_offset;
    public Boolean backward_offset;
    public String direction_channel;
    public int speed;
    public int pwmChannel;
    public PeripheralManager manager = PeripheralManager.getInstance();
    public Gpio mGpio;


    Motor(String direction_channel, int pwmChannel, PCA9685 pwm, Boolean offset) throws IOException, InterruptedException {
        //Init a motor on giving direction channel and PCA9685 channel.'''
        this.direction_channel = direction_channel;
        this.pwmChannel = pwmChannel;
        this.pwm = pwm;
        if(offset != null){
            this.offset = offset;
            this.forward_offset = offset;
        } else {
            this.offset = TRUE;
            this.forward_offset = TRUE;
        }

        this.backward_offset = !forward_offset;
        setSpeed(0);

        this.mGpio = manager.openGpio(direction_channel);
        mGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
    }

    public int getSpeed(){return speed;}

    public void setSpeed(int speed) throws IOException, InterruptedException {
        //Set Speed to a given value
        if(speed< 0 || speed > 101){
            throw new java.lang.Error("Speed must range from 0 to 100");
        }

        if(pwm == null) {
            throw new java.lang.Error("PCA9685 not callable");
        }

        this.speed = speed;
        setPwmValue(speed);
    }

    public void forward() throws IOException, InterruptedException {
        mGpio.setValue(forward_offset);
        setSpeed(this.speed);
    }

    public void backward() throws IOException, InterruptedException {
        mGpio.setValue(backward_offset);
        setSpeed(this.speed);
    }

    public void stop() throws IOException, InterruptedException {
        setSpeed(0);
    }

    public Boolean getOffset() {return offset;}

    public void setOffset(Boolean value){
        //Set forward/backward offset
        if(value != FALSE && value != TRUE){
            throw new java.lang.Error("Offset value must be Bool value");
        }

        this.forward_offset = value;
        this.backward_offset = !this.forward_offset;
    }

    public PCA9685 getPwm(){return pwm;}

    public void setPwm(PCA9685 pwm){this.pwm = pwm;}

    public void setPwmValue(int value){
        int pulse_wide = this.pwm.map(value, 0, 100, 0, 4095);
        pwm.write(pwmChannel, 0, pulse_wide);
    }

    @Override
    public void close() throws IOException {
        pwm.close();
    }

    //Should this be a separate class like 'MotorTest'?
    public static void test() throws IOException, InterruptedException {

        /*
        SunFounder TB6612

        Connect MA to BCM17
        Connect MB to BCM18
        Connect PWMA to BCM27
        Connect PWMB to BCM12
        */

        int i;
        int delay = 50;

        String Motor_A = "BCM17";
        String Motor_B = "BCM27";

        Integer channelA = 4;
        Integer channelB = 5;

        PCA9685 motorPwmA = new PCA9685();
        PCA9685 motorPwmB = new PCA9685();

        Motor left_wheel = new Motor(Motor_A, channelA, motorPwmA,TRUE);
        Motor right_wheel = new Motor(Motor_B,channelB, motorPwmB,TRUE);


        left_wheel.forward();

        for(i = 0; i < 101; i++){
            left_wheel.setSpeed(i);
            Thread.sleep(delay);
        }

        for(i = 100; i > -1 ; i--){
            left_wheel.setSpeed(i);
            Thread.sleep(delay);
        }

        left_wheel.backward();

        for(i = 0; i < 101; i++){
            left_wheel.setSpeed(i);
            Thread.sleep(delay);
        }

        for(i = 100; i > -1 ; i--){
            left_wheel.setSpeed(i);
            Thread.sleep(delay);
        }

        right_wheel.forward();

        for(i = 0; i < 101; i++){
            right_wheel.setSpeed(i);
            Thread.sleep(delay);
        }

        for(i = 100; i > -1 ; i--){
            right_wheel.setSpeed(i);
            Thread.sleep(delay);
        }

        right_wheel.backward();

        for(i = 0; i < 101; i++){
            right_wheel.setSpeed(i);
            Thread.sleep(delay);
        }

        for(i = 100; i > -1 ; i--){
            right_wheel.setSpeed(i);
            Thread.sleep(delay);
        }
    }
}
