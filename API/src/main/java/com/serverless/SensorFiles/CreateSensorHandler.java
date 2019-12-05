package com.serverless.SensorFiles;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import com.serverless.dal.Sensor;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Map;

public class CreateSensorHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Logger logger = Logger.getLogger(this.getClass());

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {


      try {
          // get the 'body' from input
          JsonNode body = new ObjectMapper().readTree((String) input.get("body"));

          // create the Product object for post


		  /**
		   * private String id;
		   *     private double temperature;
		   *     private boolean visitorStatus;
		   *     private boolean lightsOn;
		   *     private double latitude;
		   *     private double longitude;
		   */



		  if(body.get("name")== null){
			  Response responseBody = new Response("Please provide a name for your sensor",input);
			  return ApiGatewayResponse.builder()
					  .setStatusCode(500)
					  .setObjectBody(responseBody)
					  .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
					  .build();
		  }


		  String name = body.get("name")== null? null:body.get("name").toString();

		  Sensor sensor = new Sensor();
		  Double humidity = body.get("currentHumidity") == null? 0:body.get("currentHumidity").asDouble();
		  Double temperature = body.get("currentTemperature") == null? 0:body.get("currentTemperature").asDouble();

		  Double accelerationX = body.get("currentAccelerationX")== null? 0:body.get("currentAccelerationX").asDouble();
		  Double accelerationY = body.get("currentAccelerationY")== null? 0:body.get("currentAccelerationY").asDouble();
		  Double accelerationZ = body.get("currentAccelerationZ")== null? 0:body.get("currentAccelerationZ").asDouble();

		  Double hallEffect = body.get("currentHallEffect") == null? 0:body.get("currentHallEffect").asDouble();

		  sensor.setName(name);


		  if(!humidity.isNaN() && !humidity.isInfinite()){
			  sensor.setCurrentHumidity(humidity);
		  }

		  if(!temperature.isNaN() && !temperature.isInfinite()){
			  sensor.setCurrentTemperature(temperature);
		  }

		  if(!accelerationX.isNaN() && !accelerationX.isInfinite()){
			  sensor.setCurrentAccelerationX(accelerationX);
		  }

		  if(!accelerationY.isNaN() && !accelerationY.isInfinite()){
			  sensor.setCurrentAccelerationY(accelerationY);
		  }

		  if(!accelerationZ.isNaN() && !accelerationZ.isInfinite()){
			  sensor.setCurrentAccelerationZ(accelerationZ);
		  }

		  if(!hallEffect.isNaN() && !hallEffect.isInfinite()){
			  sensor.setCurrentHallEffect(hallEffect);
		  }

          sensor.save(sensor);



          return ApiGatewayResponse.builder()
      				.setStatusCode(200)
      				.setObjectBody(sensor)
      				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
      				.build();

      } catch (Exception ex) {
          logger.error("Error in saving sensor: " + ex);

          // send the error response back
    			Response responseBody = new Response("Error in saving sensor: " + ex, input);
    			return ApiGatewayResponse.builder()
    					.setStatusCode(500)
    					.setObjectBody(ex)
    					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    					.build();
      }
	}
}
