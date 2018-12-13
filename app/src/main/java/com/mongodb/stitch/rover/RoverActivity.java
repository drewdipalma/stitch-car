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


/**
 * Shell script to simulate network failure:
 * adb shell
 * su
 * while true
 * do
 *   ifconfig wlan0 down
 *   ifconfig eth0 down
 *   sleep 10
 *   ifconfig
 *   ifconfig wlan0 up
 *   ifconfig eth0 up
 *   sleep 30
 * done
 * # This will disconnect you from adb but will keep running.
 * # Easiest way to reset this is to turn the device off and back on.
 */

package com.mongodb.stitch.rover;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.auth.providers.serverapikey.ServerApiKeyCredential;
import com.mongodb.stitch.core.internal.common.BsonUtils;
import com.mongodb.stitch.core.services.mongodb.remote.sync.ChangeEventListener;
import com.mongodb.stitch.core.services.mongodb.remote.sync.ConflictHandler;
import com.mongodb.stitch.core.services.mongodb.remote.sync.DefaultSyncConflictResolvers;
import com.mongodb.stitch.core.services.mongodb.remote.sync.internal.ChangeEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;

/**
 * RoverActivity is the main and only activity for the rover.
 */
public class RoverActivity extends Activity implements ConflictHandler<Rover> {
  private static final String TAG = "RoverActivity";
  private static final int MOVE_LOOP_WAIT_TIME_MS = 250;

  private RemoteMongoCollection<Rover> rovers;
  private RemoteMongoCollection<Document> sensorReadings;
  private BMP085 sensor;

  private String userId;

  public FrontWheels frontWheels;
  public BackWheels backWheels;

  private boolean once = false;

  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);

    if (once) {
      return;
    }
    once = true;

    final StitchAppClient client = Stitch.getDefaultAppClient();
    final RemoteMongoClient mongoClient =
        client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

    rovers = mongoClient.getDatabase(Rover.ROVERS_DATABASE)
        .getCollection(Rover.ROVERS_COLLECTION, Rover.class)
        .withCodecRegistry(CodecRegistries.fromRegistries(
            BsonUtils.DEFAULT_CODEC_REGISTRY,
            CodecRegistries.fromCodecs(Rover.codec)));

    rovers.sync().configure(
        this,
        null,
        (documentId, error) -> Log.e(TAG, error.getLocalizedMessage()));

//    sensorReadings = mongoClient.getDatabase(Rover.ROVERS_DATABASE)
//            .getCollection(Rover.SENSORS_COLLECTION);
//
//    sensorReadings.sync().configure(
//        DefaultSyncConflictResolvers.remoteWins(),
//        new SensorEventListener(),
//        (documentId, error) -> Log.e(TAG, error.getLocalizedMessage()));

    try {
      this.frontWheels = new FrontWheels("I2C1", 0);
      this.backWheels = new BackWheels();
//      sensor = new BMP085(null, null);
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
    }

    doLogin();
  }

  private void doLogin() {
    Stitch.getDefaultAppClient().getAuth().loginWithCredential(
        new ServerApiKeyCredential(getString(R.string.stitch_rover_api_key)))
        .addOnSuccessListener(user -> {
          userId = user.getId();
          invalidateOptionsMenu();
          Toast.makeText(RoverActivity.this, "Logged in", Toast.LENGTH_SHORT).show();

          if (rovers.sync().getSyncedIds().isEmpty()) {
            rovers.sync().insertOne(new Rover(userId));
          }

//          new Thread(() -> {
//            while (true) {
//              final Document sensorDoc = new Document("roverId", userId);
//              sensorDoc.put("reading", sensor.getTemp());
//              sensorDoc.put("timestamp", System.currentTimeMillis());
//              sensorReadings.sync().insertOne(sensorDoc);
//
//              try {
//                Thread.sleep(3000);
//              } catch (InterruptedException e) {
//                e.printStackTrace();
//              }
//            }
//          }).start();
          moveLoop();
        })
        .addOnFailureListener(e -> {
          invalidateOptionsMenu();
          Log.d(TAG, "error logging in", e);
          Toast.makeText(RoverActivity.this, "Failed logging in", Toast.LENGTH_SHORT).show();
        });
  }

  private Document getRoverFilter() {
    return new Document("_id", userId);
  }

  private Document getLatestMoveFilter() {
    return getRoverFilter()
        .append("moves",
            new Document("$exists", true)
                .append("$not", new Document("$size", 0)));
  }

  private void moveLoop() {
    rovers.sync().find(getLatestMoveFilter()).first().addOnSuccessListener(rover -> {
      if (rover == null) {
        try {
          if (backWheels.getSpeed() != 0) {
            backWheels.stop();
          }
        } catch (IOException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        try {
          Thread.sleep(MOVE_LOOP_WAIT_TIME_MS);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        moveLoop();
      } else {
        final Move move = rover.getMoves().get(0);
        doMove(move);
        final Document update = new Document("$pull", new Document("moves",
                new Document("_id", move.getId())));
        rovers.sync().updateOne(getRoverFilter(), update).addOnCompleteListener(task -> {
          if (!task.isSuccessful()) {
            Log.d(TAG, "failed to update rover document", task.getException());
          }
          try {
            Thread.sleep(MOVE_LOOP_WAIT_TIME_MS);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          moveLoop();
        });
      }
    }).addOnFailureListener(e -> Log.d(TAG, "failed to find rover document", e));
  }

  private void doMove(final Move move) {
    Log.i(TAG, "Doing move " + move);
    Toast.makeText(RoverActivity.this, "Doing move " + move, Toast.LENGTH_SHORT).show();
    final int speed = move.getSpeed();
    final int moveLength = 500;

    try {
      frontWheels.turn(move.getAngle());

      if (speed > 0) {
        backWheels.forward();
      } else {
        backWheels.backward();
      }

      backWheels.setSpeed(23 * Math.abs(speed));

      Thread.sleep(moveLength);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Rover resolveConflict(
      final BsonValue documentId,
      final ChangeEvent<Rover> localEvent,
      final ChangeEvent<Rover> remoteEvent
  ) {
    if (localEvent.getFullDocument().getLastMoveCompleted() == null) {
      return remoteEvent.getFullDocument();
    }
    // Given this sync model consists of a single producer and a single consumer, a conflict
    // can only occur when a production and consumption happens at the same "time". That means
    // that there should always be an overlap of moves during a conflict and that the last
    // move completed is always present in the remote. Therefore we should trim all moves up to
    // and including the last completed move.
    final Rover localRover = localEvent.getFullDocument();
    final String lastMoveCompleted = localRover.getLastMoveCompleted();
    final Rover remoteRover = remoteEvent.getFullDocument();
    final List<Move> nextMoves = new ArrayList<>(remoteRover.getMoves().size());
    boolean caughtUp = false;
    for (final Move move : remoteRover.getMoves()) {
      if (move.getId().equals(lastMoveCompleted)) {
        caughtUp = true;
      } else {
        if (caughtUp) {
          nextMoves.add(move);
        }
      }
    }
    return new Rover(localRover, nextMoves);
  }

  private class SensorEventListener implements ChangeEventListener<Document> {
    @Override
    public void onEvent(final BsonValue documentId, final ChangeEvent<Document> event) {
      if (!event.hasUncommittedWrites()
          &&  sensorReadings.sync().getSyncedIds().contains(documentId)) {
        sensorReadings.sync().desyncOne(documentId);
      }
    }
  }
}


