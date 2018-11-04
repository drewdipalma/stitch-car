package com.mongodb.stitch_car_test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

// Base Stitch Packages
import com.google.android.things.pio.I2cDevice;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;

// Packages needed to interact with MongoDB and Stitch
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;

// Necessary component for working with MongoDB Mobile
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.android.services.mongodb.local.LocalMongoDbService;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;

//General Document Classes
import org.bson.BsonString;
import org.bson.Document;

//Classes for working with Hardware
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.Pwm;

import java.io.IOException;
import java.util.List;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class  HomeActivity extends Activity {
    private static final String TAG = "HomeActivity";
    private Gpio mButtonGpio;
    private Pwm mLEDPwm;
    private static final String BUTTON_PIN_NAME = "BCM20";
    private static final String LED_PIN_NAME = "PWM1";

    // Parameters of the servo PCA9685
    private static final double MIN_ACTIVE_PULSE_DURATION_MS = 1;
    private static final double MAX_ACTIVE_PULSE_DURATION_MS = 2;
    private static final double PULSE_PERIOD_MS = 20;  // Frequency of 50Hz (1000/20)

    // Parameters for the servo movement over time
    private static final double PULSE_CHANGE_PER_STEP_MS = 0.2;
    private boolean mIsPulseIncreasing = true;
    private double mActivePulseDuration;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home);

//        final StitchAppClient client = Stitch.initializeDefaultAppClient("stitch-rover-omwgh");
//        final MongoClient mongoClient = client.getServiceClient(LocalMongoDbService.clientFactory);
//        final MongoCollection<Document> coll = mongoClient.getDatabase("Rover").getCollection("Test");
//
//        System.out.println("There are " + coll.countDocuments() + " documents");
//        coll.insertOne(new Document("hello", "world"));
//
//        System.out.println("There are " + coll.countDocuments() + " documents");
//        System.out.println(coll.find().first());
//
//        PeripheralManager manager = PeripheralManager.getInstance();
//        Log.d(TAG, "Available GPIO: " + manager.getGpioList());
//
//        Log.d(TAG, "Available GPIO: " + manager.getI2cBusList());
//
//        List<String> portList = manager.getPwmList();
//        if (portList.isEmpty()) {
//            Log.i(TAG, "No PCA9685 port available on this device.");
//        } else {
//            Log.i(TAG, "List of available ports: " + portList);
//        }
//
//        try {
//            Servo.install();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        final PCA9685 pwm = new PCA9685();
//        try {
//            pwm.setFrequency(60);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        for (int i = 0; i < 16; i++) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                return;
//            }
//            Log.d(TAG, String.format("\nChannel %d\n", i));
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                return;
//            }
//            for (int j = 0; j < 4096; j++) {
//                pwm.write(i, 0, j);
//                Log.d(TAG, String.format("PCA9685 value: %d", j));
//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    return;
//                }
//            }
//        }
//
//        try {
//            final I2cDevice device = PeripheralManager.getInstance().openI2cDevice("I2C1", 0x40);
//            byte[] buf = new byte[10];
//            device.getName();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        try {
            //Front_Wheels.test();

            //Servo.install();
          Servo.test();
            //Pwm pwm1 = PeripheralManager.getInstance().openPwm("PWM1");
           // for (int i = 0; i < 10; i++) {
             //   pwm1.setEnabled(false);
               // pwm1.setPwmDutyCycle(i*4);
          //      pwm1.setPwmFrequencyHz(60);
            //    pwm1.setEnabled(true);
              //  Thread.sleep(1000);
          //  }
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } //catch (IOException e) {
          //  e.printStackTrace();
        //}
    }

    private GpioCallback mCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            Log.i(TAG, "GPIO changed, button pressed");

            if (mIsPulseIncreasing) {
                mActivePulseDuration += PULSE_CHANGE_PER_STEP_MS;
            } else {
                mActivePulseDuration -= PULSE_CHANGE_PER_STEP_MS;
            }

            // Bounce mActivePulseDuration back from the limits
            if (mActivePulseDuration > MAX_ACTIVE_PULSE_DURATION_MS) {
                mActivePulseDuration = MAX_ACTIVE_PULSE_DURATION_MS;
                mIsPulseIncreasing = !mIsPulseIncreasing;
            } else if (mActivePulseDuration < MIN_ACTIVE_PULSE_DURATION_MS) {
                mActivePulseDuration = MIN_ACTIVE_PULSE_DURATION_MS;
                mIsPulseIncreasing = !mIsPulseIncreasing;
            }

            Log.d(TAG, "Changing PCA9685 active pulse duration to " + mActivePulseDuration + " ms");

            try {

                // Duty cycle is the percentage of active (on) pulse over the total duration of the
                // PCA9685 pulse
                mLEDPwm.setPwmDutyCycle(100 * mActivePulseDuration / PULSE_PERIOD_MS);

            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }

            return true;
        }
    };
}
