package com.serverless.SensorDataFiles;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverless.ApiGatewayResponse;
import com.serverless.Response;
import com.serverless.dal.Sensor;
import com.serverless.dal.SensorData;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreateSensorDataHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Logger logger = Logger.getLogger(this.getClass());

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

      try {
          // get the 'body' from input
          JsonNode body = new ObjectMapper().readTree((String) input.get("body"));

          // create the Product object for post


		  /**
		   * private String id;
		   *
		   *     private String sensorId;
		   *     private String sensorSerial;
		   *     private String timeStamp;
		   *     private String dataType;
		   *     private double data;
		   */

		  if(body.get("dataType")== null){
			  Response responseBody = new Response("Please provide a data type for your data",input);
			  return ApiGatewayResponse.builder()
					  .setStatusCode(500)
					  .setObjectBody(responseBody)
					  .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
					  .build();
		  }

		  if(body.get("timeStamp")== null){
			  Response responseBody = new Response("Please provide time stamp for your data",input);
			  return ApiGatewayResponse.builder()
					  .setStatusCode(500)
					  .setObjectBody(responseBody)
					  .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
					  .build();
		  }

		  if(body.get("data")== null){
			  Response responseBody = new Response("Data field is nullr",input);
			  return ApiGatewayResponse.builder()
					  .setStatusCode(500)
					  .setObjectBody(responseBody)
					  .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
					  .build();
		  }

		  SensorData sensorData = new SensorData();


		  String sensorId = body.get("sensorId")== null? null:body.get("sensorId").toString();
		  String sensorName = body.get("sensorName")== null? null:body.get("sensorName").toString();
		  String timeStamp = body.get("timeStamp")== null? null:body.get("timeStamp").toString();
		  String dataType = body.get("dataType")== null? null:body.get("dataType").toString();
		  Double data = body.get("data") == null? 0:body.get("data").asDouble();

		  sensorId = sensorId.trim();
		  sensorId = sensorId.replaceAll("\\\\", "");

		  sensorName = sensorName.trim();
		  sensorName = sensorName.replaceAll("\\\\", "");

		  timeStamp = timeStamp.trim();
		  timeStamp = timeStamp.replaceAll("\\\\", "");

		  sensorData.setSensorId(sensorId);
		  sensorData.setSensorName(sensorName);
		  sensorData.setTimeStamp(timeStamp);
		  sensorData.setDataType(dataType);
		  sensorData.setData(data);

		  sensorData.save(sensorData);

          return ApiGatewayResponse.builder()
      				.setStatusCode(200)
      				.setObjectBody(sensorData)
      				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
      				.build();

      } catch (Exception ex) {
          logger.error("Error in saving sensor data: " + ex);

          // send the error response back
		  String stackTrace = Stream
				  .of(ex.getStackTrace())
				  .map(StackTraceElement::toString)
				  .collect(Collectors.joining("\n"));
    			Response responseBody = new Response("Error in saving sensorData!: " + stackTrace, input);
    			return ApiGatewayResponse.builder()
    					.setStatusCode(500)
    					.setObjectBody(responseBody)
    					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    					.build();
      }
	}
}
