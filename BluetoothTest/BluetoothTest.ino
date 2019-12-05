//Bluetooth TX connected to arduino zero RX
//Bluetooth RX connected to arduino zero TX
//Bluetooth is powered by 3.3v but can handle up to 6. Didnt want to stress it out so I am using 3.3
//Ground is connected as normal
//AT+TYPE1 to pair
//AT+PASS123456 for password


void setup() {

  Serial.begin(9600); //Initialize normal serial 
  Serial1.begin(9600); //Initialize serial for bluetooth
}

void loop() {
  // read from port 1, send to port 0:
  while (Serial1.available()) {
    int inByte = Serial1.read();
    Serial.write(inByte);
  }
  
  

  // read from port 0, send to port 1:
  while (Serial.available()) {
    int inByte = Serial.read();
    Serial1.write(inByte);

  }
  
  
}
