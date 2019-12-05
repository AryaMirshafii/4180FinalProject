package com.serverless.SensorFiles;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import com.serverless.dal.Sensor;
import com.serverless.dal.SensorData;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

public class PutSensorHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse>{
    private final Logger logger = Logger.getLogger(this.getClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {


        try {
            // get the 'pathParameters' from input
            Map<String,String> pathParameters =  (Map<String,String>)input.get("pathParameters");
            String sensorId = pathParameters.get("id");

            // get the Product by id
            Sensor sensor = new Sensor().get(sensorId);

            // send the response back
            if (sensor != null) {


                JsonNode body = new ObjectMapper().readTree((String) input.get("body"));

                Double humidity = body.get("currentHumidity") == null? sensor.getCurrentHumidity() :body.get("currentHumidity").asDouble();
                Double temperature = body.get("currentTemperature") == null? sensor.getCurrentTemperature():body.get("currentTemperature").asDouble();
                Double accelerationX = body.get("currentAccelerationX")== null? sensor.getCurrentAccelerationX() :body.get("currentAccelerationX").asDouble();
                Double accelerationY = body.get("currentAccelerationY")== null? sensor.getCurrentAccelerationY() :body.get("currentAccelerationY").asDouble();
                Double accelerationZ = body.get("currentAccelerationZ")== null? sensor.getCurrentAccelerationZ() :body.get("currentAccelerationZ").asDouble();
                Double hallEffect = body.get("currentHallEffect") == null? sensor.getCurrentHallEffect() :body.get("currentHallEffect").asDouble();

                boolean motionDetected = body.get("motionDetected") == null? sensor.getMotionDetected() :body.get("motionDetected").asBoolean();


                String timeStamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());

                if(!humidity.isNaN() && !humidity.isInfinite() && sensor.getCurrentHumidity() != humidity){
                    sensor.setCurrentHumidity(humidity);


                    /*
                    SensorData data = new SensorData();
                    data.setSensorName(sensor.getName());
                    data.setSensorId(sensorId);
                    data.setTimeStamp(timeStamp);
                    data.setDataType("humidity");
                    data.setData(humidity);
                    data.save(data);

                     */

                }

                if(!temperature.isNaN() && !temperature.isInfinite() && sensor.getCurrentTemperature() != temperature){
                    sensor.setCurrentTemperature(temperature);

                    /*
                    SensorData data = new SensorData();
                    data.setSensorName(sensor.getName());
                    data.setSensorId(sensorId);
                    data.setTimeStamp(timeStamp);
                    data.setDataType("temperature");
                    data.setData(humidity);
                    data.save(data);

                     */

                }

                if(!accelerationX.isNaN() && !accelerationX.isInfinite() && sensor.getCurrentAccelerationX() != accelerationX){
                    sensor.setCurrentAccelerationX(accelerationX);

                    /*
                    SensorData data = new SensorData();
                    data.setSensorName(sensor.getName());
                    data.setSensorId(sensorId);
                    data.setTimeStamp(timeStamp);
                    data.setDataType("accelerationX");
                    data.setData(humidity);
                    data.save(data);

                     */
                }


                if(!accelerationY.isNaN() && !accelerationY.isInfinite() && sensor.getCurrentAccelerationY() != accelerationY){
                    sensor.setCurrentAccelerationY(accelerationY);

                    /*
                    SensorData data = new SensorData();
                    data.setSensorName(sensor.getName());
                    data.setSensorId(sensorId);
                    data.setTimeStamp(timeStamp);
                    data.setDataType("accelerationY");
                    data.setData(accelerationX);
                    data.save(data);
                     */

                }

                if(!accelerationZ.isNaN() && !accelerationZ.isInfinite() && sensor.getCurrentAccelerationZ() != accelerationZ){
                    sensor.setCurrentAccelerationZ(accelerationZ);

                    /*
                    SensorData data = new SensorData();
                    data.setSensorName(sensor.getName());
                    data.setSensorId(sensorId);
                    data.setTimeStamp(timeStamp);
                    data.setDataType("accelerationZ");
                    data.setData(accelerationZ);
                    data.save(data);

                     */
                }

                if(!hallEffect.isNaN() && !hallEffect.isInfinite() && sensor.getCurrentHallEffect() != hallEffect){
                    sensor.setCurrentHallEffect(hallEffect);

                    /*
                    SensorData data = new SensorData();
                    data.setSensorName(sensor.getName());
                    data.setSensorId(sensorId);
                    data.setTimeStamp(timeStamp);
                    data.setDataType("halleffect");
                    data.setData(humidity);
                    data.save(data);

                     */

                }

                sensor.setMotionDetected(motionDetected);
                sensor.save(sensor);


                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .setObjectBody(sensor)
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            } else {
                return ApiGatewayResponse.builder()
                        .setStatusCode(404)
                        .setObjectBody("Sensor with id: '" + sensorId + "' not found.")
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            }
        } catch (Exception ex) {
            logger.error("Error in retrieving sensor: " + ex);

            // send the error response back
            Response responseBody = new Response("Error in retrieving sensor:  " + ex.toString(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
        }
    }

}
