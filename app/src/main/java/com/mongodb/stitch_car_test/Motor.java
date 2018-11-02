package com.mongodb.stitch_car_test;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Pwm;

import java.io.IOException;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

//
// https://github.com/sunfounder/SunFounder_PiCar/blob/master/picar/SunFounder_TB6612/TB6612.py
public class Motor {
    /*   Motor driver class
    Set direction_channel to the GPIO channel which connect to MA,
    Set motor_B to the GPIO channel which connect to MB,
    Both GPIO channel use BCM numbering;
    Set pwm_channel to the PCA9685 channel which connect to PWMA,
    Set pwm_B to the PCA9685 channel which connect to PWMB;
    PCA9685 channel using PCA9685, Set pwm_address to your address, if is not 0x40
    Set debug to True to print out debug informations.
    */

<<<<<<< HEAD
    public Pwm mPwm;
=======
    public PCA9685 pwm;
>>>>>>> help
    public Boolean offset;
    public Boolean forward_offset;
    public Boolean backward_offset;
    public String direction_channel;
    public int speed;
    public PeripheralManager manager = PeripheralManager.getInstance();
    public Gpio mGpio;


<<<<<<< HEAD
    Motor(String direction_channel, Pwm mPwm, Boolean offset) throws IOException {
        //Init a motor on giving direction channel and PWM channel.'''
=======
    Motor(String direction_channel, PCA9685 pwm, Boolean offset) throws IOException {
        //Init a motor on giving direction channel and PCA9685 channel.'''
>>>>>>> help
        this.direction_channel = direction_channel;
        this.mPwm = mPwm;
        if(offset != null){
            this.offset = offset;
            this.forward_offset = offset;
        } else {
            this.offset = TRUE;
            this.forward_offset = TRUE;
        }

        this.backward_offset = !forward_offset;
        this.speed = 0;

        this.mGpio = manager.openGpio(direction_channel);
        mGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
    }

    public int getSpeed(){return speed;}

    public void setSpeed(int speed) throws IOException, InterruptedException {
        //Set Speed to a given value
        if(speed< 0 || speed > 101){
            throw new java.lang.Error("Speed must range from 0 to 100");
        }

<<<<<<< HEAD
        if(mPwm == null) {
            throw new java.lang.Error("PWM not callable");
=======
        if(pwm == null) {
            throw new java.lang.Error("PCA9685 not callable");
>>>>>>> help
        }

        this.speed = speed;

<<<<<<< HEAD
        try {
            mPwm.setPwmFrequencyHz(speed);
        } catch (IOException e) {
            e.printStackTrace();
        }
=======
        pwm.setFrequency(speed);
>>>>>>> help
    }

    public void forward() throws IOException, InterruptedException {
        mGpio.setValue(forward_offset);
        //This feels redundant?
        setSpeed(this.speed);
    }

    public void backward() throws IOException, InterruptedException {
        mGpio.setValue(forward_offset);
        //This feels redundant?
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

<<<<<<< HEAD
    public Pwm getPWM(){return mPwm;}

    public void setPwm(Pwm mPwm){this.mPwm = mPwm;}

    //Should this be a separate class like 'MotorTest'?
    public void test() throws IOException, InterruptedException {

        /*
        SunFounder TB6612

        Connect MA to BCM17
        Connect MB to BCM18
        Connect PWMA to BCM27
        Connect PWMB to BCM12
        */

        int i;
        int delay = 50;

        // Originally, this code set-up GPIO out access on BCM27/BCM22 (though top comment seems to imply 12?)
        // GPIO.setup((27, 22), GPIO.OUT);
        // a = GPIO.PWM(27, 60);
        // b = GPIO.PWM(22, 60);

        Pwm mPwmA = manager.openPwm("PMW0");
        Pwm mPwmB = manager.openPwm("PMW1");
        String dirA = "BCM23";
        String dirB = "BCM24";

        //Enable Pwm A
        mPwmA.setPwmFrequencyHz(60);
        mPwmA.setPwmDutyCycle(0);
        mPwmA.setEnabled(true);

        //Enable Pwm B
        mPwmB.setPwmFrequencyHz(60);
        mPwmB.setPwmDutyCycle(0);
        mPwmB.setEnabled(true);


        //Functions which change the Duty cycle, not sure how these are to be handled, my work up stack and then come back to these?
        //def a_speed(value)
        //a.ChangeDutyCycle(value);

        //def b_speed(value)
        //b.ChangeDutyCycle(value);

        Motor motorA = new Motor(dirA, mPwmA, null);
        Motor motorB = new Motor(dirB, mPwmB, null);

        motorA.forward();

        for(i = 0; i < 101; i++){
            motorA.setSpeed(i);
            Thread.sleep(delay);
        }

        for(i = 100; i > -1 ; i--){
            motorA.setSpeed(i);
            Thread.sleep(delay);
        }

        motorA.backward();

        for(i = 0; i < 101; i++){
            motorA.setSpeed(i);
            Thread.sleep(delay);
        }

        for(i = 100; i > -1 ; i--){
            motorA.setSpeed(i);
            Thread.sleep(delay);
        }

        motorB.forward();

        for(i = 0; i < 101; i++){
            motorB.setSpeed(i);
            Thread.sleep(delay);
        }

        for(i = 100; i > -1 ; i--){
            motorB.setSpeed(i);
            Thread.sleep(delay);
        }

        motorB.backward();

        for(i = 0; i < 101; i++){
            motorB.setSpeed(i);
            Thread.sleep(delay);
        }

        for(i = 100; i > -1 ; i--){
            motorB.setSpeed(i);
            Thread.sleep(delay);
        }
    }
=======
    public PCA9685 getPWM(){return pwm;}

    public void setPWM(PCA9685 pwm){this.pwm = pwm;}

    //Should this be a separate class like 'MotorTest'?
//    public void test() throws IOException, InterruptedException {
//
//        /*
//        SunFounder TB6612
//
//        Connect MA to BCM17
//        Connect MB to BCM18
//        Connect PWMA to BCM27
//        Connect PWMB to BCM12
//        */
//
//        int i;
//        int delay = 50;
//
//        //Set-up GPIO out access on BCM27/BCM22
//        GPIO.setup((27, 22), GPIO.OUT);
//
//        a = GPIO.PCA9685(27, 60);
//        b = GPIO.PCA9685(22, 60);
//
//        a.start(0);
//        b.start(0);
//
//        def a_speed(value):
//        a.ChangeDutyCycle(value);
//
//        def b_speed(value):
//        b.ChangeDutyCycle(value);
//
//        Motor motorA = new Motor(23);
//        Motor motorB = new Motor(24);
//
//        motorA.pwm = a_speed;
//        motorB.pwm = b_speed;
//
//
//
//        motorA.forward();
//
//        for(i = 0; i < 101; i++){
//            motorA.setSpeed(i);
//            Thread.sleep(50);
//        }
//
//        for(i = 100; i > -1 ; i--){
//            motorA.setSpeed(i);
//            Thread.sleep(50);
//        }
//
//        motorA.backward();
//
//        for(i = 0; i < 101; i++){
//            motorA.setSpeed(i);
//            Thread.sleep(50);
//        }
//
//        for(i = 100; i > -1 ; i--){
//            motorA.setSpeed(i);
//            Thread.sleep(50);
//        }
//
//        motorB.forward();
//
//        for(i = 0; i < 101; i++){
//            motorB.setSpeed(i);
//            Thread.sleep(50);
//        }
//
//        for(i = 100; i > -1 ; i--){
//            motorB.setSpeed(i);
//            Thread.sleep(50);
//        }
//
//        motorB.backward();
//
//        for(i = 0; i < 101; i++){
//            motorB.setSpeed(i);
//            Thread.sleep(50);
//        }
//
//        for(i = 100; i > -1 ; i--){
//            motorB.setSpeed(i);
//            Thread.sleep(50);
//        }
//    }
>>>>>>> help

}