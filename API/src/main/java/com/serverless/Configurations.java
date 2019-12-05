package com.serverless;

public class Configurations {
    public static final String accessKey = "AKIA6EQ3BDYM6EAQ7VMB";
    public static final String secretKey = "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";
    public static final String platformApplicationArn = "arn:aws:sns:us-east-1:971793047065:SmartRoom";
    public static final String platformApplicationArnForEndpoint = "arn:aws:sns:us-east-1:971793047065:app/APNS_SANDBOX/SmartRoom";
    public static final String iosToken = "3b3380c2a9ad7f5a24db79b9256670c5a2a72942fcca19e98d32aec933525a7e";

    public static String generateIntruderMessage(String roomName){
        return "You have a visitor at room: " + roomName +". Open SmartRoom to see who it is!";
    }
}
