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

package com.mongodb.stitch_car_test;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

class Move {

  private final ObjectId id;
  private final int angle;
  private final int speed;

  Move(final ObjectId id, final int angle, final int speed) {
    this.id = id;
    this.angle = angle;
    this.speed = speed;
  }

  public ObjectId getId() {
    return id;
  }

  public int getAngle() {
    return angle;
  }

  public int getSpeed() {
    return speed;
  }

  static BsonDocument toBsonDocument(final Move rover) {
    final BsonDocument asDoc = new BsonDocument();
    asDoc.put(Fields.ID, new BsonObjectId(rover.getId()));
    asDoc.put(Fields.ANGLE, new BsonInt32(rover.getAngle()));
    asDoc.put(Fields.SPEED, new BsonInt32(rover.getSpeed()));
    return asDoc;
  }

  static Move fromBsonDocument(final BsonDocument doc) {
    return new Move(
        doc.getObjectId(Fields.ID).getValue(),
        doc.getNumber(Fields.ANGLE).intValue(),
        doc.getNumber(Fields.SPEED).intValue()
    );
  }

  static final class Fields {
    static final String ID = "_id";
    static final String ANGLE = "angle";
    static final String SPEED = "speed";
  }

  public static final Codec<Move> codec = new Codec<Move>() {

    @Override
    public void encode(
        final BsonWriter writer, final Move value, final EncoderContext encoderContext) {
      new BsonDocumentCodec().encode(writer, toBsonDocument(value), encoderContext);
    }

    @Override
    public Class<Move> getEncoderClass() {
      return Move.class;
    }

    @Override
    public Move decode(
        final BsonReader reader, final DecoderContext decoderContext) {
      final BsonDocument document = (new BsonDocumentCodec()).decode(reader, decoderContext);
      return fromBsonDocument(document);
    }
  };

  @Override
  public String toString() {
    return "Move{"
        + "id=" + id
        + ", angle=" + angle
        + ", speed=" + speed
        + "}";  }
}
