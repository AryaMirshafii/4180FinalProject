//
//  SensorTableView.swift
//  4180FinalApp
//
//  Created by Arya Mirshafii on 12/1/19.
//  Copyright © 2019 Arya Mirshafii. All rights reserved.
//

import Foundation
import UIKit
import SwiftyJSON

import SwiftyRequest


extension String {

    var boolValue: Bool {
        return (self as NSString).boolValue
    }
}

class SensorTableView: UITableViewController, UISearchBarDelegate {
    
    var sectionTitles = [String]()
    var searchText = " "
    var isSearching = false
    
    var endpoints: Endpoints = Endpoints();
    
    var sensors:[Sensor] = [];
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setUI();
    
        loadData();
        
    }
    
    
    func setUI(){
        self.tableView.backgroundColor = .black
        
    }
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 80
    }
    
    
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        
        return sensors.count
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
            
        self.tableView.reloadData()
    }
    
    func loadData(){
        guard let url = URL(string: endpoints.SENSOR_URL) else { return }
        
        let session = URLSession.shared
        session.dataTask(with: url) { (data, response, error) in
            
            
            if let data = data {
                //print(data)
                do {
                    
                    let jsonData = try JSON(data: data)
                    
                    for json in jsonData.array!{
                        //print(json["currentHumidity"].rawString()!)
                        let sensor:Sensor = Sensor(id: json["id"].string!, name: json["name"].string!);
                        if let temp = Double(json["currentTemperature"].rawString()!){
                            print(temp)
                            sensor.currentTemperature = temp;
                        }
                        
                        if let humidity = Double(json["currentHumidity"].rawString()!){
                            
                            sensor.currentHumidity = humidity;
                        }
                        
                        if let accelerationX = Double(json["currentAccelerationX"].rawString()!){
                            
                            sensor.currentAccelerationX = accelerationX;
                        }
                        
                        if let accelerationY = Double(json["currentAccelerationY"].rawString()!){
                            
                            sensor.currentAccelerationY = accelerationY;
                        }
                        
                        if let accelerationZ = Double(json["currentAccelerationZ"].rawString()!){
                            
                            sensor.currentAccelerationZ = accelerationZ;
                        }
                        
                        if let hallEffect = Double(json["currentHallEffect"].rawString()!){
                            
                            sensor.currentHallEffect = hallEffect;
                        }
                        
                        let motionDetected = json["motionDetected"].rawString() 
                            
                        sensor.motionDetected = motionDetected?.boolValue;
                        
                        
                    
                        self.sensors.append(sensor)
                    }
                    DispatchQueue.main.async {
                        for sensor in self.sensors{
                            print(sensor.toString());
                        }
                        self.tableView.reloadData()
                    }
                    //print(jsonData.array![0]["currentAcceleration"])
                } catch {
                    print(error)
                }
                
            }
        }.resume()
    }

    
    
    var selectedCell:Int = 0;
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        NSLog("You selected cell number: \(indexPath.row)!")
        selectedCell = indexPath.row
        self.performSegue(withIdentifier: "showSensor", sender: self)
    
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showSensor" {
            if let destinationVC = segue.destination as? ViewController {
                destinationVC.sensor = sensors[selectedCell]
            }
        }
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let sensor = sensors[indexPath.row]
        
        let cellIdentifier = "sensorCell"
        guard let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as? SensorCell  else {
            fatalError("The dequeued cell is not an instance of AlbumTableCell.")
        }
        
        cell.SerialLabel.text = sensor.name
        
        return cell;
    }
    

    
    
    
    override func tableView(_ tableView: UITableView, sectionForSectionIndexTitle title: String, at index: Int) -> Int {
        guard let index = sectionTitles.index(of: title) else {
            
            return -1
        }
        
        return index
        
    }
    
    override func sectionIndexTitles(for tableView: UITableView) -> [String]? {
        
        return sectionTitles
        
    }
    
    
    override func scrollViewDidScroll(_ scrollView: UIScrollView) {
        
    }
    
    
    
    
    
    
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        
    
    }
    
    
    
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        
        if searchText.isEmpty {
            //self.addView.frame = CGRect(x: 0, y: 0, width: 357, height: 194)
            isSearching = false
        
            self.tableView.rowHeight = 80
            self.tableView.reloadData()
            
            
            
        }else {
            
            
            isSearching = true

            
        }
        
        if searchBar.text == nil || searchBar.text == ""
        {
            
            searchBar.perform(#selector(self.resignFirstResponder), with: nil, afterDelay: 0.1)
        }
        
        
    }
    
    
}
