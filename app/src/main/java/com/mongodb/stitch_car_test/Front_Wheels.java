package com.mongodb.stitch_car_test;

// Translation of Front Wheels class (front_wheels.py)
// https://github.com/sunfounder/SunFounder_PiCar/blob/master/picar/front_wheels.py


import org.bson.Document;

import java.io.IOException;

import static java.lang.Boolean.TRUE;

public class Front_Wheels {
    //Front wheels control class
    int FRONT_WHEEL_CHANNEL = 0;
    public Integer busNumber;
    public int channel;

    public int minAngle;
    public int maxAngle;
    public int straightAngle;
    public int turningMax;
    public Document angle;
    public Servo wheel;

    public Integer turningOffset;
    public int cali_turning_offset;

    Front_Wheels(Integer busNumber, Integer channel) throws InterruptedException {

        if (busNumber != null){
            this.busNumber = busNumber;
        }else{
            this.busNumber = 1;
        }

        if (channel != null){
            this.channel = channel;
        }else{
            this.channel = FRONT_WHEEL_CHANNEL;
        }

        this.straightAngle = 90;
        setTurningMax(45);

        this.wheel = new Servo(this.channel, 0, TRUE, this.busNumber, 0x40);

        if(turningOffset != null){
            setTurningOffset(turningOffset);
        } else {
            setTurningOffset(0);
        }
    }

    public void turnLeft(){
        wheel.write(angle.getInteger("left"));
    }

    public void turnStraight(){
        wheel.write(angle.getInteger("straight"));
    }

    public void turnRight(){
        wheel.write(angle.getInteger("right"));
    }

    public void turn(int newAngle){
        if( newAngle < angle.getInteger("left")) {
            newAngle = angle.getInteger("left");
        }

        if( newAngle > angle.getInteger("right")) {
            newAngle = angle.getInteger("right");

        }

        wheel.write(newAngle);
    }

    public int getChannel() { return channel;}

    public void setChannel(int chn) { this.channel = chn;}

    public int getTurningMax(){ return this.turningMax;}

    public void setTurningMax(int newAngle){
        this.turningMax = newAngle;
        this.minAngle = straightAngle - newAngle;
        this.maxAngle = straightAngle + newAngle;

        Document angleDoc = new Document();
        angleDoc.put("left", this.minAngle);
        angleDoc.put("straight", this.straightAngle);
        angleDoc.put("right", this.maxAngle);

        this.angle = angleDoc;
    }

    public int getTurningOffset() { return turningOffset;}

    public void setTurningOffset(int value) {
        this.turningOffset = value;
        wheel.setOffset(value);
        turnStraight();
    }

    public void ready() {
        // Get the wheel to a ready position
        wheel.setOffset(getTurningOffset());
        turnStraight();
    }

    public void calibration() {
        //Get the front wheels to the calibration position.
        turnStraight();
        cali_turning_offset = getTurningOffset();
    }

    public void caliLeft() {
        //Calibrate the wheels to left
        cali_turning_offset -= 1;
        wheel.setOffset(cali_turning_offset);
        turnStraight();
    }

    public void caliRight() {
        //Calibrate the wheels to right
        cali_turning_offset += 1;
        wheel.setOffset(cali_turning_offset);
        turnStraight();
    }

    public void caliOk() {
        //Save the calibration value
        //TBD if this is needed
        //self.db.set('turning_offset', self.turning_offset)
    }


    public static void test() throws IOException, InterruptedException {
        Front_Wheels front_wheels = new Front_Wheels(null, null);

        for( int i = 0; i < 10; i++){
            front_wheels.turnLeft();
            Thread.sleep(1000);

            front_wheels.turnStraight();
            Thread.sleep(1000);

            front_wheels.turnRight();
            Thread.sleep(1000);

            front_wheels.turnStraight();
            Thread.sleep(1000);
        }
    }
}
