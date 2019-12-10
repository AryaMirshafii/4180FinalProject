Table of Contents
=================

   * [IoT Senors Network](#iot-senors-network)
      * [Intoduction](#intoduction)
      * [Description](#description)
      * [How to Build Project](#how-to-build-project)
         * [Block Diagram](#block-diagram)
         * [Cloud Service](#cloud-service)
         * [Web Service API](#web-service-api)
               * [pom.xml](#pomxml)
               * [serverless.yml](#serverlessyml)
               * [dal](#dal)
               * [SensorDataFiles](#sensordatafiles)
         * [IOS Application](#ios-application)
         * [mBed Sensor Pad](#mbed-sensor-pad)
            * [Wiring Diagram](#wiring-diagram)
               * [Mbed with XBee Board](#mbed-with-xbee-board)
               * [Mbed with TMP36 temperature sensor](#mbed-with-tmp36-temperature-sensor)
               * [Mbed with LSM9DS1 Imu](#mbed-with-lsm9ds1-imu)
               * [Mbed with HTU21D humidity sensor](#mbed-with-htu21d-humidity-sensor)
            * [Building mbed code](#building-mbed-code)
         * [PocketBeagle](#pocketbeagle)
            * [Wiring Diagram`](#wiring-diagram-1)
               * [PocketBeagle with XBee](#pocketbeagle-with-xbee)
            * [Adding WiFi](#adding-wifi)
            * [Connecting the PocketBeagle to Eduroam](#connecting-the-pocketbeagle-to-eduroam)
            * [Building Cloud Communication Program](#building-cloud-communication-program)
      * [Conclusion](#conclusion)
      * [Desired Improvements](#desired-improvements)
      * [Helpful Links/Thanks to:](#helpful-linksthanks-to)
         * [PocketBeagle Communication](#pocketbeagle-communication)

# IoT Senors Network
## Intoduction
IoT or Internet of Things is a major part of today's embedded systems. It relates to the conecpt of connecting devices
over the internet. One of the problems, especially on an enterprise network, is connecting all these devices to wifi is not 
always plausible. Also, using a sub-1GHz rf band could consume less power. 

The first benefit is the one we shall focus on. Connecting the mbed or a small embedded device to the school's enterprise
network is not an easy task. Our IoT Sensor Network provides a solution.
## Description
The IoT Sensors Network is cloud communication done from a Sensor Pad (XBee and a mbed LPC1768 with various sensor attached) 
to the BeagleBone PocketBeagle to the cloud). This cloud data is displayed on an IOS application.

The platforms used were a baremetal mbed, Debian 9.9 "stretch IoT", IOS, and the Amazon Web Services. 
The programming languges used were,
1. C++
2. Swift
## How to Build Project
### Block Diagram
![Diagram](https://github.com/AryaMirshafii/4180FinalProject/blob/master/Images/blockDiagram.png)

### Cloud Service
The API is hosted using Amazon Web Service's API gateway platform. AWS was chosen due to its ease of use, low cost, and high scalability. In order to upload the API to the AWS cloud, the [Serverless Framework](https://serverless.com/) is used to compile the source code into a .jar file and then upload it. The benefit of using serverless is that it handles deployment and scaling by itself, with minimal effort from the user. This is achieved by doing the following: (Note: this is just to upload using Serverless to AWS, and it is assumed that you already have an existing project)


1. Install Serverless using NPM:
```console
npm install -g serverless
```
2. Give serverless your AWS Access Key and Secret Token using:
```console
export AWS_ACCESS_KEY_ID=YOUR_ACCESS_KEY
export AWS_SECRET_ACCESS_KEY=YOUR_SECRET_ACCESS_KEY
```
NOTE: Your keys can be found by going into your AWS console and viewing your IAM user information. It should be noted that these keys can only be viewed once so if you already created an IAM user and you forgot your keys, you will need to regenerate new keys and write them down.

3. If the service is already created on AWS you will need to tear down the existing one by running:
```console
sls remove
```
4. To compile the source code into a .jar file using maven run:
```console
mvn clean install
```
5. Finally, to upload the newly generated .jar to AWS, run:
```console
sls deploy
```

That is all! Serverless handles all of the scaling and once the process is done, serverless will give you a list of API endpoints created which are able to be called using regular HTTP requests.

### Web Service API
The web service API is written using the Java programming language as well as the AWS java SDK. Java was used due to its ease of use and scalability as well as its integrability into Amazon Web Services.

Two very important files in the root directory are:
- pom.xml
- serverless.yml

##### pom.xml
This file defines the depenencies of the project. The build system of choice, Maven in this case, reads the pom.xml and downloads and links the necessary dependencies. An example of adding a dependeny to pom.xml can be as simple as:

```
<dependency>
  <groupId>com.amazonaws</groupId>
  <artifactId>aws-java-sdk-dynamodb</artifactId>
  <version>1.11.119</version>
</dependency>
```
This block of xml adds the dependency for the Java AWS SDK Dynamodb which allows the endpoint to communicate to a Dynamodb database hosted on Amazon Web Services.

##### serverless.yml
This is a swagger file which is part of the OpenAPI standard for defining the basic structure and properties of a web based API. In this project, when the .jar file is uploaded to the cloud, Amazon Web Services parses it to determine the properties for the service being created. This file can also be used by other developers to view the strucutre of the API without having to read through all of the source code. It is not as helpful in this proejct, due to its small size and low complexity, but a .yml file is invaluable in large scale proejcts with hundreds of endpoints and API behaviors.

The names of the databases used in this project are defined as 
custom:
```
  sensorTableName: 'SensorTable'
  dataTableName: 'DataTable'
```
Here, a table is created for storing Sensor information, and another for storing SensorData.
```
provider:
  name: aws
  runtime: java8
  stage: ${opt:stage, 'dev'}
  region: ${opt:region, 'us-east-1'}
  iamRoleStatements:
    - Effect: Allow
      Action:
        - dynamodb:Query
        - dynamodb:Scan
        - dynamodb:GetItem
        - dynamodb:PutItem
        - dynamodb:UpdateItem
        - dynamodb:DeleteItem
      Resource:
        - { "Fn::GetAtt": ["SensorDynamoDBTable", "Arn" ] }
        - { "Fn::GetAtt": ["DataDynamoDBTable", "Arn" ] }
  environment:
    SENSOR_TABLE_NAME: ${self:custom.sensorTableName}
    DATA_TABLE_NAME: ${self:custom.dataTableName}

```
The above lines define the project name, programming language used, AWS regions, and database attributes and capabilities. The environemtn variables SENSOR_TABLE_NAME, and DATA_TABLE_NAME are later used to bind classes/objects to their respsective databases.

Next, endpoints are defined under the functions section:
```
#Sensor Stuff
functions:
  createSensor:
    handler: com.serverless.SensorFiles.CreateSensorHandler
    events:
      - http:
          path: /sensors
          method: post

```
Each item under functions: includes the name of the endpoint, the handler which handles the response and input for the defined endpoint. The createSensor function uses the http protocol which is handled by -http: and the relative paths are set to /sensors. Since creating a sensor requres a POST request, the method paramter is set to post.

Finally, the properties for the databases are defined as follows for each database:
```
resources:
  Resources:
    SensorDynamoDBTable:
      Type: AWS::DynamoDB::Table
      Properties:
        TableName: ${self:custom.sensorTableName}
        AttributeDefinitions:
          - AttributeName: id
            AttributeType: S
        KeySchema:
          - AttributeName: id
            KeyType: HASH
        ProvisionedThroughput:
          ReadCapacityUnits: 1
          WriteCapacityUnits: 1
```

The following code defines database, database type, and sets attributes for the keys used in the database. Keys are used to index the data, which is used by the individual endpoints and can be used to perform analysis. The ProvisionedThroughput property defines the throughput capacity and overall responsiveness for the database. It should be noted that since this project is relatively small scale it is acceptable to use 1 for both ReadCapacityUnits and WriteCapacityUnits. If the system was scaled up, these values would likely need to be changed to 5 or greater in order to reduce system latency.
The directory structure is as follows:
- dal
- SensorDataFiles
- SensorFiles
##### dal
This directory contains the source code for the objects that encapsulate the data being passed throughout the API. The two classes are Sensor, which encapsulates a sensor and SensorData, which encapsulates the data being collected from every sensor. These classes follow a basic java structure but the following tags are necessary to ensure that the created objects are able to be stored in their respective DynamoDB Tables:

The DynamoDBTable tag defines the name of the database in which objects of this class are being stored in.
```
@DynamoDBTable(tableName = "SENSOR_TABLE_NAME")
```

The DynamoDBHashKey tag sets the key for the class to the property of "id". This is so the database knows which data point it should index the objects by.
```
@DynamoDBHashKey(attributeName = "id")
```
Other non-key attributes can be defined using the DynamoDBAttribute tag:
```
@DynamoDBAttribute(attributeName = "name")
```
##### SensorDataFiles
This directory includes the API handlers that pertain to CRUD operations for Sensors. For example, the code to handle the deletion of Sensors can be found in DeleteSensorHandler. This code is pretty self explanitory with a couple of exceptions.

First, in order to be able to handle an HTTP request, the classes need to implement:
```
handleRequest(Map<String, Object> input, Context context)
```
As part of this implementation, the classes need to override the method:
```
public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) 
```
This method handles the actual data passed into the endpoint and returns a response that notifies if the request was properly processed and returns the requested information or if there was an an error.


### IOS Application
The IOS application is written using Swift along with the Xcode IDE and uses the [SwiftyRequest](https://github.com/IBM-Swift/SwiftyRequest) library to generate HTTP requests.

An example of an HTTP request using SwiftyRequest is:
```
guard let url = URL(string: endpoints.SENSOR_URL + "/" + sensor!.id) else { return }
        
        let session = URLSession.shared
        session.dataTask(with: url) { (data, response, error) in
          ..Code to handle the response
        } 
        
```

The app has two major screens:
- TableView - lists all of the Sensors in the network.
- ViewController - shows the information for a given sensor.

TableView:
![TableView](https://github.com/AryaMirshafii/4180FinalProject/blob/master/Images/Table.png)

Sensor View:
![Sensor View](https://github.com/AryaMirshafii/4180FinalProject/blob/master/Images/Sensor.png)

### mBed Sensor Pad
The sensor pad for this project is based on an MBed microcontroller that interfaces with an [XBee board](https://www.sparkfun.com/datasheets/Wireless/Zigbee/XBee-Datasheet.pdf) for sub-1 GHz communication, a [TMP36 analog temperature sensor](https://www.analog.com/media/en/technical-documentation/data-sheets/TMP35_36_37.pdf), an [LSM9DS1 Imu sensor](https://www.st.com/resource/en/datasheet/lsm9ds1.pdf) to collect acceleration(x, y,z) for acceleration calculations and a [HTU21D humidity sensor](https://cdn-shop.adafruit.com/datasheets/1899_HTU21D.pdf) to measure current humidity.

![Sensor Pad](https://github.com/AryaMirshafii/4180FinalProject/blob/master/Images/sensorTag.jpeg)

#### Wiring Diagram

The wiring for the various sensors and peripherals to the MBed can be found below:
##### Mbed with XBee Board 

| Mbed Pin      | XBee         |
| ------------- | ------------- |
| GND  | GND  |
| Vout(3.3V)  | VCC  |
| p9  | DIN  |
| p10  | DOUT  |
| p11  | RESET  |

##### Mbed with TMP36 temperature sensor

| Mbed Pin      | Tmp36         |
| ------------- | ------------- |
| GND  | GND  |
| Vout(3.3V)  | VS  |
| p15  | Vout  |

##### Mbed with LSM9DS1 Imu

| Mbed Pin      | LSM9DS1         |
| ------------- | ------------- |
| GND  | GND  |
| Vout(3.3V)  | VDD  |
| p28   | SDA  |
| p27  | SCL  |

###### Note: this was chained to use the same bus as the HTU21D sensor. Since each sensor had a unique address, this was able to work as expected.

##### Mbed with HTU21D humidity sensor

| Mbed Pin      | HTU21D         |
| ------------- | ------------- |
| GND  | GND  |
| Vout(3.3V)  | 3.3v  |
| p28   | SDA  |
| p27  | SCL  |


#### Building mbed code
The Code for the Mbed Sensor Tag is written in C++ and using the MBed online compiler and is relatively simple. It reads in data from the connected sensors and prints them out using the XBee which behaves as a serial output.

To begin, necessary libraries are imported using include statements:
``` C++
#include "mbed.h"
#include "LSM9DS1.h"
#include "HTU21D.h"
#include "ultrasonic.h"
```

Then, the variables for the XBee, PC Serial, and Sensors is initialzied using 

``` C++
Serial xbee1(p9, p10); //Creates a variable for serial comunication through pin 9 and 10
DigitalOut rst1(p11); //Digital reset for the XBee, 200ns for reset
Serial pc(USBTX, USBRX);//Opens up serial communication through the USB port via the computer
AnalogIn LM61(p15); 
LSM9DS1 imu(p28, p27, 0xD6, 0x3C);
HTU21D temphumid(p28, p27); //Temp humid sensor || SDA, SCL
```

Then, like any other C++ program, the code begins execution in the main() function.

First, the XBee is reset, which is a hardware defined necessity in order to begin communication.

``` C++
 rst1 = 0; //Set reset pin to 0
 myled = 0;//Set LED3 to 0
 myled2= 0;//Set LED4 to 0
 wait_ms(1);//Wait at least one millisecond
 rst1 = 1;//Set reset pin to 1
 wait_ms(1);//Wait another millisecond
 imu.begin();
 
```
Next, the IMU is initialized and calibrated using :
``` C++
 if (!imu.begin()) {
        pc.printf("Failed to communicate with LSM9DS1.\n");
  }
  imu.calibrate();
```

Afterwards, the program begins a continuous while loop where the sensor data is read and printed to the XBee using Serial.

Reading the data:
``` C++
imu.readMag();
imu.readAccel();
imu.readTemp();  
float accelX = imu.calcAccel(imu.ax);
float accelY = imu.calcAccel(imu.ay);
float accelZ = imu.calcAccel(imu.az);
        
tempC = ((LM61*3.3)-0.600)*100.0;
//convert to degrees F
tempF = (9.0*tempC)/5.0 + 32.0;
humidity = temphumid.sample_humid(); 
```

Printing the data to the XBee:

``` C++
xbee1.printf("AX:%f\n\r", accelX);
xbee1.printf("AY:%f\n\r", accelY);
xbee1.printf("AZ:%f\n\r", accelZ);
xbee1.printf("T:%f \n\r", tempF);
xbee1.printf("H:%d \n\r", humidity);
```

### PocketBeagle

![TableView](https://github.com/AryaMirshafii/4180FinalProject/blob/master/Images/PocketBeagleWires.jpg)

#### Wiring Diagram`

##### PocketBeagle with XBee
The PocketBeagle has two sets of pins, P1, P2.

These names of these pins can be found [here](https://github.com/beagleboard/pocketbeagle/wiki/System-Reference-Manual).

This guide will use Header.pin (silkscreen)

| PocketBeagle  | XBee         |
| ------------- | ------------- |
| p1.16 (GND)   | GND  |
| p2.23 (+3.3V) | VCC  |
| p2.07 (U1 TX) | DIN  |
| p2.05 (U1 RX) | DOUT  |
| p2.03 (23)    | RESET  |


| PocketBeagle   | [USB-A Female Breakout](https://www.sparkfun.com/products/12700) |
| -------------  | ------------- |
| p1.16 (GND)    | GND  |
| p1.03 (USB1 EN)| GND  |
| p1.07 (VI) p1.05 (VB) | VCC  |
| p1.11 (USB1+) | D+  |
| p1.09 (USB1-) | D-  |

| PocketBeagle   | Barrel Jack conncted to 5V 2A Powersupply |
| -------------  | ------------- |
| p1.01 (VIN)    | Power pin |
| p1.16 (GND)    | GND |


#### Adding WiFi
* Use a usb hub into the female breakout board and plug a wifi module in. We use a Edimax one that works out of the box
https://www.amazon.com/Edimax-EW-7811Un-150Mbps-Raspberry-Supports/dp/B003MTTJOY

* After sshing or connecting to the PocketBeagle run the command "lsusb" to determine if it is available. Also, if you 
use the Edimax, it will have an internal LED that will light up when active.

* Your DHCP server should automatically assign it an IP adddress. You can find that by looking for "wlan0" when you run
the command "ifconfig." The IP address can be used to ssh into the OS from now on. use
```console
ssh debian@<ip_address_of_pocketbeagle>
```
to login. 
#### Connecting the PocketBeagle to Eduroam
Use the Connman utility that comes with Debian.
1. sftp the eduroam.config file under the PocketBeagleCode directory to the PocketBeagle. Use Windows or Linux because the 
exposed USB network interfaces do not seem to come up with MacOS. Or you can write the code in eduroam.config with Vim or Nano
over serial console. 
On Linux:
```console
user@machine:~$ cd PocketBeagleCode 
```
```console
user@machine:~$ sftp debian@192.168.7.2
> put eduroam.config
> exit
```
Use the password temppwd
2. Connect Serially to the PocketBeagle (use screen or minicom or any other serial utility)
You have to find the device name of the PocketBeagle. On mac and linux is might begin with usbmodem on Windows it will come up
as a Comm devices
```console
user@machine:~$ screen /dev/tty.usbmodem* 115200
```
Login to the PocketBeagle using the username: debian and the password temppwd.
3. Move the eduroam.config file to the directory /var/lib/connman. You may have to do this as root.
```console
debian@machine:~$ cp eduroam.config /var/lib/connman
```
4. Edit the eduroam.config. This can only be done with root privileges. The identity is your institutional email. Passphrase is 
your instituional password. This was initially made for Georgia Tech, so the CACertificate field may have to be changed. Use the 
one that your institional uses for logging in via Linux operating systems. For example, I used the CACertificate from a Georgia 
Tech tutorial for getting on the network for Ubuntu. Contact the IT department at your school for more information. Edit the 
anonymous identity. Use eduroam@domain.edu. Domain is replaced by the domain used in your institutional email. Use Vim, Nano or 
your favorite console text editor. If you were not able to sftp in the network, you can create the file in this directory.
More information on this configuration file can be found [here](https://manpages.debian.org/testing/connman/connman-service.config.5.en.html).
```console
debian@machine:~$ cd /var/lib/connman
debian@machine:~$ sudo vim eduroam.config
```
Now wait for the Eduroam to connect. If it doesn't you may have to restart Connman or start the PocketBeagle, using the button on
the top. Use this command to determine if you are connected. A new network interface will show up wlan0 and it will have a new 
eduroam IP Address assigned. You are now connected to the internet. 
```console
debian@machine:~$ ifconfig
```
Ping an IP Address to test the network.
```console
debian@machine:~$ ping 1.1.1.1
```
Ping google to test if DNS works. If this hanges the turn off IPv6.
```console
debian@machine:~$ ping google.com
```
If this doesn't work run these commands to turn off IPv 6
```console
debian@machine:~$ sudo sysctl -w net.ipv6.conf.all.disable_ipv6=1
debian@machine:~$ sudo sysctl -w net.ipv6.conf.default.disable_ipv6=1
```
if it doesn't connect to eduroam restart and run this command to diagnose what went wrong.
```console
debian@machine:~$ journalctl | grep connmand | less
```
#### Building Cloud Communication Program
1. Configure reset pin
```console
user@machine:~$ ssh debian@<ip_of_pocketbeagle>
user@machine:~$ config-pin -a p2.03 out
user@machine:~$ config-pin -q p2.03
```
2. Build and run code after setting up cloud server and wiring.
```console
user@machine:~$ cd PocketBeagleCode
user@machine:~$ sftp debian@<ip_of_pocketbeagle>
> mkdir PocketBeagleCode
> cd PocketBeagleCode
> put *
> exit
user@machine:~$ ssh debian@<ip_of_pocketbeagle>
user@machine:~$ cd PocketBeagleCode
user@machine:~$ make
user@machine:~$ make run
```
make builds the project. "make run" executes the program. 
## Conclusion
IoT networks work and improve a company or individual's ability capture sensor data from small embedded devices. Our IoT sensor 
network allowed us to capture sensor data and send it over Eduroam, the fastest internet network on campus. We found the other 
network GT-Other to be too slow for our application.
## Desired Improvements
1. Add security to the communication between Sensor Pad and PocketBeagle, also from PocketBeagle to the Web
2. Build a more robust application for cloud communication in C++.
3. Allow more than one sensor pad to be connected to a single PocketBeagle, requires XBee configuration.
4. Figure out how to get IPv6 hostnames resolved.
5. Add more sensors, sensor pads.
6. Use external power
## Helpful Links/Thanks to:
### PocketBeagle Communication
* [Communicating via UART with the XBee](https://stackoverflow.com/questions/6947413/how-to-open-read-and-write-from-serial-port-in-c) This is 
pretty difficult wiht a lot of settings to get correct. Luckily this stackoverflow post worked with little modification. I want to credit the user 
with the top post here.
* [Navigating the PocketBeagle](http://exploringbeaglebone.com/chapter1/)
* [Connecting to Eduroam](https://wiki.archlinux.org/index.php/ConnMan#Connecting_to_eduroam_(802.1X))
