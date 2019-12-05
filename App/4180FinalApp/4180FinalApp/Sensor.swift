//
//  Sensor.swift
//  4180FinalApp
//
//  Created by Arya Mirshafii on 12/1/19.
//  Copyright © 2019 Arya Mirshafii. All rights reserved.
//

import Foundation


/*
 private String id;
 private String serialNumber;
 private double currentHumidity;
 private double currentTemperature;
 private double currentAcceleration;
 private double currentHallEffect;
 */
class Sensor {
    var id:String;
    var name:String
    
    var currentHumidity:Double!
    var currentTemperature:Double!
    var currentAccelerationX:Double!
    var currentAccelerationY:Double!
    var currentAccelerationZ:Double!
    var currentHallEffect:Double!
    
    var motionDetected: Bool!
    
    init(id: String, name: String) {
        self.id = id;
        self.name = name;
        
        self.name = self.name.replacingOccurrences(of: "\"", with: "")
        
    }
    
    func toString()->String{
        var result =  "Id :" + id + ", CurrentHumidity " + String(currentHumidity ?? 0 )
        + ", currentTemperature " + String(currentTemperature ?? 0)
+ ", currentAccelerationX " + String(currentAccelerationX ?? 0 )
        + ", currentAccelerationY " + String(currentAccelerationY ?? 0 )
            + ", currentAccelerationZ " + String(currentAccelerationZ ?? 0 );
        
        result += ", currentHallEffect " + String(currentHallEffect ?? 0 );
        
        return result;
    }
    
    func getUpdateUrl() ->String{
        let endpoint = Endpoints();
        return endpoint.SENSOR_URL + id
    }
    
}
