//
//  ViewController.swift
//  KeepSleep
//
//  Created by Arya Mirshafii on 11/13/19.
//  Copyright © 2019 Arya Mirshafii. All rights reserved.
//
/*
 APp needs to turn LED on or off
 And white noise on or off
 White 
 
 */

import UIKit

class ViewController: UIViewController {

    let buttonRadius = 15;
    let borderWidth = 5;
    
    @IBOutlet weak var lightOffButton: UIButton!
    @IBOutlet weak var lightOnButton: UIButton!
    @IBOutlet weak var soundOnButton: UIButton!
    @IBOutlet weak var soundOffButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        configureUI();
    }
    
    func configureUI(){
        lightOffButton.layer.cornerRadius = CGFloat(buttonRadius);
        lightOnButton.layer.cornerRadius = CGFloat(buttonRadius);
        soundOnButton.layer.cornerRadius = CGFloat(buttonRadius);
        soundOffButton.layer.cornerRadius = CGFloat(buttonRadius);
        
    }
    
    

    @IBAction func turnLightOn(_ sender: Any) {
        
    
        lightOnButton.layer.borderWidth = CGFloat(borderWidth);
        lightOnButton.layer.borderColor = UIColor(red:0.95, green:0.15, blue:0.15, alpha:1.0).cgColor;
        lightOffButton.layer.borderWidth = 0;
        
    }
    
    @IBAction func turnLightOff(_ sender: Any) {
        lightOffButton.layer.borderWidth = CGFloat(borderWidth);
        lightOffButton.layer.borderColor = UIColor(red:0.95, green:0.15, blue:0.15, alpha:1.0).cgColor;
        lightOnButton.layer.borderWidth = 0;
    }
    
    
    @IBAction func turnSoundOn(_ sender: Any) {
        soundOnButton.layer.borderWidth = CGFloat(borderWidth);
        soundOnButton.layer.borderColor = UIColor(red:0.95, green:0.15, blue:0.15, alpha:1.0).cgColor;
        soundOffButton.layer.borderWidth = 0;
    }
    
    @IBAction func turnSoundOff(_ sender: Any) {
        soundOffButton.layer.borderWidth = CGFloat(borderWidth);
        soundOffButton.layer.borderColor = UIColor(red:0.95, green:0.15, blue:0.15, alpha:1.0).cgColor;
        soundOnButton.layer.borderWidth = 0;
    }
}

