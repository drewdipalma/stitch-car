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

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Rover {

  public static final String ROVERS_DATABASE = "rover";
  public static final String ROVERS_COLLECTION = "rovers";

  private final String id;
  private final ObjectId lastMoveCompleted;
  private final List<Move> moves;

  Rover(
      final String id,
      final ObjectId lastMoveCompleted,
      final List<Move> moves
  ) {
    this.id = id;
    this.lastMoveCompleted = lastMoveCompleted;
    this.moves = moves;
  }

  Rover(final String userId) {
    this(userId, null, Collections.emptyList());
  }

  public Rover(final Rover rover, List<Move> moves) {
    this(rover.getId(), rover.getLastMoveCompleted(), moves);
  }

  public String getId() {
    return id;
  }

  public ObjectId getLastMoveCompleted() {
    return lastMoveCompleted;
  }

  public List<Move> getMoves() {
    return moves;
  }

  static BsonDocument toBsonDocument(final Rover rover) {
    final BsonDocument asDoc = new BsonDocument();
    asDoc.put(Fields.ID, new BsonString(rover.getId()));
    if (rover.getLastMoveCompleted() != null) {
      asDoc.put(Fields.LAST_MOVE_COMPLETED, new BsonObjectId(rover.getLastMoveCompleted()));
    }
    final BsonArray movesArr = new BsonArray();
    for (final Move move : rover.getMoves()) {
      movesArr.add(Move.toBsonDocument(move));
    }
    asDoc.put(Fields.MOVES, movesArr);
    return asDoc;
  }

  static Rover fromBsonDocument(final BsonDocument doc) {
    final ObjectId lastMoveCompleted;
    if (doc.containsKey(Fields.LAST_MOVE_COMPLETED)) {
      lastMoveCompleted = doc.getObjectId(Fields.LAST_MOVE_COMPLETED).getValue();
    } else {
      lastMoveCompleted = null;
    }
    final List<Move> moves;
    if (doc.containsKey(Fields.MOVES)) {
      final BsonArray movesArr = doc.getArray(Fields.MOVES);
      moves = new ArrayList<>(movesArr.size());
      for (final BsonValue moveVal : movesArr) {
        moves.add(Move.fromBsonDocument(moveVal.asDocument()));
      }
    } else {
      moves = Collections.emptyList();
    }
    return new Rover(
        doc.getString(Fields.ID).getValue(),
        lastMoveCompleted,
        moves
    );
  }

  static final class Fields {
    static final String ID = "_id";
    static final String LAST_MOVE_COMPLETED = "lastMoveCompleted";
    static final String MOVES = "moves";
  }

  public static final Codec<Rover> codec = new Codec<Rover>() {

    @Override
    public void encode(
        final BsonWriter writer, final Rover value, final EncoderContext encoderContext) {
      new BsonDocumentCodec().encode(writer, toBsonDocument(value), encoderContext);
    }

    @Override
    public Class<Rover> getEncoderClass() {
      return Rover.class;
    }

    @Override
    public Rover decode(
        final BsonReader reader, final DecoderContext decoderContext) {
      final BsonDocument document = (new BsonDocumentCodec()).decode(reader, decoderContext);
      return fromBsonDocument(document);
    }
  };
}
