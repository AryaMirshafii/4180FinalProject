//
//  ViewController.swift
//  4180FinalApp
//
//  Created by Arya Mirshafii on 11/14/19.
//  Copyright © 2019 Arya Mirshafii. All rights reserved.
//

import UIKit
import Foundation
//
//  BluetoothHandler
//  headingCar
//
//  Created by Arya Mirshafii on 3/7/18.
//  Copyright © 2018 georgiaTechEngineeringForInnovationGroup. All rights reserved.
//
import UIKit
import SwiftyJSON
import SwiftyRequest

class ViewController: UIViewController {

    var sensor:Sensor?
    
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var temperatureLabel: UILabel!

    @IBOutlet weak var humidityLabel: UILabel!
    
    @IBOutlet weak var accelerationXLabel: UILabel!
    @IBOutlet weak var accelerationYLabel: UILabel!
    @IBOutlet weak var accelerationZLabel: UILabel!
    @IBOutlet weak var motionLabel: UILabel!
    
    var endpoints: Endpoints = Endpoints();
    
    
    var originalColor = UIColor.gray
    override func viewDidLoad()
    {
        super.viewDidLoad()

        originalColor = motionLabel.textColor
        setLabels()
        
    }
    
    
    @IBAction func refresh(_ sender: Any) {
        guard let url = URL(string: endpoints.SENSOR_URL + "/" + sensor!.id) else { return }
        
        let session = URLSession.shared
        session.dataTask(with: url) { (data, response, error) in
            
            
            if let data = data {
                //print(data)
                do {
                    
                    let json = try JSON(data: data)
                    
                    if let temp = Double(json["currentTemperature"].rawString()!){
                        print(temp)
                        self.sensor!.currentTemperature = temp;
                    }
                    
                    if let humidity = Double(json["currentHumidity"].rawString()!){
                        
                        self.sensor!.currentHumidity = humidity;
                    }
                    
                    if let accelerationX = Double(json["currentAccelerationX"].rawString()!){
                        
                        self.sensor!.currentAccelerationX = accelerationX;
                    }
                    
                    if let accelerationY = Double(json["currentAccelerationY"].rawString()!){
                        
                        self.sensor!.currentAccelerationY = accelerationY;
                    }
                    
                    if let accelerationZ = Double(json["currentAccelerationZ"].rawString()!){
                        
                        self.sensor!.currentAccelerationZ = accelerationZ;
                    }
                    
                    if let hallEffect = Double(json["currentHallEffect"].rawString()!){
                        
                        self.sensor!.currentHallEffect = hallEffect;
                    }
                    
                    let motionDetected = json["motionDetected"].rawString()
                        
                    self.sensor!.motionDetected = motionDetected?.boolValue;
                    
                    DispatchQueue.main.async {
                        self.setLabels();
                    }
                    //print(jsonData.array![0]["currentAcceleration"])
                } catch {
                    print(error)
                }
                
            }
        }.resume()
        
    }
    
    func setLabels(){
        if let sensorLoaded = sensor{
            nameLabel.text = "Name: " + sensorLoaded.name;
            temperatureLabel.text = "Temperature: " + String(sensorLoaded.currentTemperature);
            accelerationXLabel.text = "Accel X: " + String(sensorLoaded.currentAccelerationX) + " M/S"
            accelerationYLabel.text = "Accel Y: " + String(sensorLoaded.currentAccelerationY) + " M/S"
            accelerationZLabel.text = "Accel Z: " + String(sensorLoaded.currentAccelerationZ) + " M/S"
            humidityLabel.text = "Humidity: " + String(sensorLoaded.currentHumidity);
            
            if(sensorLoaded.motionDetected){
                motionLabel.text = "Motion Detected!"
                motionLabel.textColor = UIColor.red
            }else{
                 motionLabel.text = "No Motion Detected!"
                motionLabel.textColor = originalColor
            }
        }
    }
    

}

