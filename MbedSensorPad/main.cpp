/**
 * XBee Example Test
 * A test application that demonstrates the ability
 * of transmitting serial data via an XBee module with
 * an mbed microprocesor.
 * By: Vlad Cazan
 * Date: Tuesday, September 29th 2009
 */

#include "mbed.h"
#include "LSM9DS1.h"
#include "HTU21D.h"
#include "ultrasonic.h"

Serial xbee1(p9, p10); //Creates a variable for serial comunication through pin 9 and 10

DigitalOut rst1(p11); //Digital reset for the XBee, 200ns for reset

DigitalOut myled(LED3);//Create variable for Led 3 on the mbed
DigitalOut myled2(LED4);//Create variable for Led 4 on the mbed

Serial pc(USBTX, USBRX);//Opens up serial communication through the USB port via the computer


LSM9DS1 imu(p28, p27, 0xD6, 0x3C);

HTU21D temphumid(p28, p27); //Temp humid sensor || SDA, SCL
                                     
int humidity;

float tempC, tempF;

AnalogIn LM61(p15);                                      


int main() {
    rst1 = 0; //Set reset pin to 0
    myled = 0;//Set LED3 to 0
    myled2= 0;//Set LED4 to 0
    wait_ms(1);//Wait at least one millisecond
    rst1 = 1;//Set reset pin to 1
    wait_ms(1);//Wait another millisecond
    imu.begin();
     
    if (!imu.begin()) {
        pc.printf("Failed to communicate with LSM9DS1.\n");
    }
    imu.calibrate();
    
    while (1) {//Neverending Loop
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
        xbee1.printf("AX:%f\n\r", accelX);
        xbee1.printf("AY:%f\n\r", accelY);
        xbee1.printf("AZ:%f\n\r", accelZ);
        xbee1.printf("T:%f \n\r", tempF);
        xbee1.printf("H:%d \n\r", humidity);
        
        
       
        
        
        pc.printf("AX:%f\n\r", accelX);
        pc.printf("AY:%f\n\r", accelY);
        pc.printf("AZ:%f\n\r", accelZ);
        pc.printf("T:%f \n\r", tempF);
        pc.printf("H:%d \n\r", humidity);
        
        
        
        
    }
}

