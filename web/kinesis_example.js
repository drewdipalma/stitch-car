exports = function(event){
 const awsService = context.services.get('aws');
try{
   awsService.kinesis().PutRecord({
     Data: JSON.stringify(event.fullDocument),
     StreamName: "stitchStream",
     PartitionKey: "1"
      }).then(function(response) {
        return response;
      });
}
catch(error){
  console.log(JSON.parse(error));
}
};
