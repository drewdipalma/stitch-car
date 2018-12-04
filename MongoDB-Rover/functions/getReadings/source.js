exports = function(roverId, start, end){
  const mdb = context.services.get('mongodb-atlas');
  const sensors = mdb.db("Rovers").collection("Sensors");
  
  return sensors.find({"id": roverId, "time":{"$gt":start,"$lt":end}})
    .toArray()
    .then(readings => {
     let data = objArray.map(readings => readings.reading);
     return {"Average": data.reduce((a,b) => a + b, 0) / data.length,
        "Min": Math.min(...readings),
        "Max": Math.max(...readings)};
  });
};