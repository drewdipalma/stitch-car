package com.mongodb.stitch_car_test;

// Translation of Front Wheels class (back_wheels.py)
// https://github.com/sunfounder/SunFounder_PiCar/blob/master/picar/back_wheels.py

import java.io.IOException;

import static java.lang.Boolean.TRUE;

public class Back_Wheels {
    public String MotorA = "BCM17";
    public String MotorB = "BCM27";

    public int PWMA = 4;
    public int PWMB = 5;

    public Boolean forwardA;
    public Boolean forwardB;

    public PCA9685 pwm;

    public Integer busNumber = 1;
    public String db;

    public Integer address = 0x40;

    Back_Wheels(Integer busNumber, String db) throws IOException {
        // TBD
        // self.db = filedb.fileDB(db=db)

        // Initialize the direction channel and pwm channel
        //Original: self.forward_B = int(self.db.get('forward_B', default_value=1))
        forwardA = TRUE;
        forwardB = TRUE;

        Motor leftWheel = new Motor(MotorA, null, forwardA);
        Motor rightWheel = new Motor(MotorB, null, forwardB);

        pwm = new PCA9685(busNumber, address);

        //setPwmValue(PWMA, 0, pwm);

        //self.pwm.write(self.PWM_A, 0, pulse_wide);

        //def _set_b_pwm(value):
        //pulse_wide = self.pwm.map(value, 0, 100, 0, 4095)
        //self.pwm.write(self.PWM_B, 0, pulse_wide)

       // leftWheel.setPwm(aPwm);
       // rightWheel.setPwm(bPwm);

        setSpeed(0);
    }

   // public void setPwmValue( int pwmNum, int value, PWM pwm){
   //     int pulse_wide = pwm.map(value, 0, 100, 0, 4095);
   //     pwm.write(pwmNum, 0, pulse_wide);
   // }

    public void setSpeed(int speed){

    }
}
