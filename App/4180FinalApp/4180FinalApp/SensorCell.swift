//
//  SensorCell.swift
//  4180FinalApp
//
//  Created by Arya Mirshafii on 12/1/19.
//  Copyright © 2019 Arya Mirshafii. All rights reserved.
//

import Foundation
import UIKit

class SensorCell: UITableViewCell {
    
    @IBOutlet weak var SerialLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.backgroundColor = UIColor.black
        self.contentView.backgroundColor = UIColor.black
        
        
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
       
    }
}
