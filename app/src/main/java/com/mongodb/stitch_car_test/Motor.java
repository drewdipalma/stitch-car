package com.mongodb.stitch_car_test;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

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
    Set pwm_channel to the PWM channel which connect to PWMA,
    Set pwm_B to the PWM channel which connect to PWMB;
    PWM channel using PCA9685, Set pwm_address to your address, if is not 0x40
    Set debug to True to print out debug informations.
    */

    public PWM pwm;
    public Boolean offset;
    public Boolean forward_offset;
    public Boolean backward_offset;
    public String direction_channel;
    public int speed;
    public PeripheralManager manager = PeripheralManager.getInstance();
    public Gpio mGpio;


    Motor(String direction_channel, PWM pwm, Boolean offset) throws IOException {
        //Init a motor on giving direction channel and PWM channel.'''
        this.direction_channel = direction_channel;
        this.pwm = pwm;
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

    public void setSpeed(int speed) throws IOException {
        //Set Speed to a given value
        if(speed< 0 || speed > 101){
            throw new java.lang.Error("Speed must range from 0 to 100");
        }

        if(pwm == null) {
            throw new java.lang.Error("PWM not callable");
        }

        this.speed = speed;

        try {
            pwm.setFrequency(speed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void forward() throws IOException {
        mGpio.setValue(forward_offset);
        //This feels redundant?
        setSpeed(this.speed);
    }

    public void backward() throws IOException {
        mGpio.setValue(forward_offset);
        //This feels redundant?
        setSpeed(this.speed);
    }

    public void stop() throws IOException {
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

    public PWM getPWM(){return pwm;}

    public void setPWM(PWM pwm){this.pwm = pwm;}

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

        //Set-up GPIO out access on BCM27/BCM22
        GPIO.setup((27, 22), GPIO.OUT);

        a = GPIO.PWM(27, 60);
        b = GPIO.PWM(22, 60);

        a.start(0);
        b.start(0);

        def a_speed(value):
        a.ChangeDutyCycle(value);

        def b_speed(value):
        b.ChangeDutyCycle(value);

        Motor motorA = new Motor(23);
        Motor motorB = new Motor(24);

        motorA.pwm = a_speed;
        motorB.pwm = b_speed;



        motorA.forward();

        for(i = 0; i < 101; i++){
            motorA.setSpeed(i);
            Thread.sleep(50);
        }

        for(i = 100; i > -1 ; i--){
            motorA.setSpeed(i);
            Thread.sleep(50);
        }

        motorA.backward();

        for(i = 0; i < 101; i++){
            motorA.setSpeed(i);
            Thread.sleep(50);
        }

        for(i = 100; i > -1 ; i--){
            motorA.setSpeed(i);
            Thread.sleep(50);
        }

        motorB.forward();

        for(i = 0; i < 101; i++){
            motorB.setSpeed(i);
            Thread.sleep(50);
        }

        for(i = 100; i > -1 ; i--){
            motorB.setSpeed(i);
            Thread.sleep(50);
        }

        motorB.backward();

        for(i = 0; i < 101; i++){
            motorB.setSpeed(i);
            Thread.sleep(50);
        }

        for(i = 100; i > -1 ; i--){
            motorB.setSpeed(i);
            Thread.sleep(50);
        }
    }

}
