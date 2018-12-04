exports = function(event){
  const awsService = context.services.get('aws');
  const roverId = event.fullDocument.roverId;
  try{
    awsService.kinesis("us-west-2").PutRecord({
      Data: JSON.stringify(event.fullDocument), 
      StreamName: context.values.get("Stream"),
      PartitionKey: context.values.get("Partitions")[roverId]
    }).then(function(response) {
      return response;
    });
    
    console.log("Successfully put the following document into the " +
      context.values.get("Stream") + " Kinesis stream: " + EJSON.stringify(event.fullDocument));
  }catch(error){
    console.log(error);
  }
};