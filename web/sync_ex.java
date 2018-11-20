// Point to a MongoDB Atlas instance
final RemoteMongoClient remoteMongoClient =
client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

// Point to an Atlas collection
RemoteMongoCollection remoteCollection = remoteMongoClient
.getDatabase("my_db").getCollection("my_collection");

// Configure sync between Atlas and a local database. In this example, conflicts
// are resolved by giving preference to remote changes, a custom conflictHandler
// can also be defined. When updates are applied (locally or remotely) to
// documents that you are syncing, your change handler will be called.
remoteCollection.sync().configure(
DefaultSyncConflictResolvers.remoteWins(),
new changeListener(),
new errorHandler());

// Once you configure a remote collection to sync, you can add one or many
// documents to watch.
remoteCollection.sync().syncOne(documentId);
remoteCollection.sync().syncMany(arrayOfDocumentIds);

// As updates are applied locally or remotely to documents that you are syncing
// over your change handler will be called.
private class changeListener implements ChangeEventListener<Document> {
  @Override
  public void onEvent(final BsonValue documentId, final ChangeEvent<Document> event) {
        // hasUncommittedWrites means that the update has only been applied locally
        if (event.hasUncommittedWrites()) {
              // Custom actions can go here, such as refreshing UI elements
        }
    }
}
