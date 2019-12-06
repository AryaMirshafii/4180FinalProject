#define SERIALTERMINAL      "/dev/ttyO4"
#define NUMBER_OF_CLOUD_POSTS 100
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <termios.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <iostream>
#include <fstream>
#include <unistd.h>
#include <cstdint>
#include <string>
#include <cstring>
#include <thread>
#include <curl/curl.h>
#include <string>
#include <chrono>

// UART Settings Configurations
//
// @args fd file descriptor
// @args speed baudrate
int set_interface_attribs(int fd, int speed)
{
    struct termios tty;

    if (tcgetattr(fd, &tty) < 0) {
        printf("Error from tcgetattr: %s\n", strerror(errno));
        return -1;
    }

    cfsetospeed(&tty, (speed_t)speed);
    cfsetispeed(&tty, (speed_t)speed);

    tty.c_cflag |= CLOCAL | CREAD;
    tty.c_cflag &= ~CSIZE;
    tty.c_cflag |= CS8;         /* 8-bit characters */
    tty.c_cflag &= ~PARENB;     /* no parity bit */
    tty.c_cflag &= ~CSTOPB;     /* only need 1 stop bit */
    tty.c_cflag &= ~CRTSCTS;    /* no hardware flowcontrol */

    tty.c_lflag |= ICANON | ISIG;  /* canonical input */
    tty.c_lflag &= ~(ECHO | ECHOE | ECHONL | IEXTEN);

    tty.c_iflag &= ~IGNCR;  /* preserve carriage return */
    tty.c_iflag &= ~INPCK;
    tty.c_iflag &= ~(INLCR | ICRNL | IUCLC | IMAXBEL);
    tty.c_iflag &= ~(IXON | IXOFF | IXANY);   /* no SW flowcontrol */

    tty.c_oflag &= ~OPOST;

    tty.c_cc[VEOL] = 0;
    tty.c_cc[VEOL2] = 0;
    tty.c_cc[VEOF] = 0x04;

    if (tcsetattr(fd, TCSANOW, &tty) != 0) {
        printf("Error from tcsetattr: %s\n", strerror(errno));
        return -1;
    }
    return 0;
}

int posts = 0;
bool stillPosting = true;

// Server URL
char* requestUrl = "https://5gzljxwdwk.execute-api.us-east-1.amazonaws.com/dev/sensors/da7be3e3-0829-4837-9c4e-fba3fa8e5fe0";

//  Data to send
volatile float accelerationX = 0.00000;
volatile float accelerationY = 0.00000;
volatile float accelerationZ = 0.00000;
volatile int postTemp = 0;
volatile int humidity = 0;

//Webserver doesnt detect booleans in json with bools so use strings "true" or "false"
std::string motionDetected = "false";

// alertXBee()
//
// Toggles Reset on the XBee
//
// Allows it to start sending and receiving data
//
// Toggles to high value, active high
//
void alertXBee()
{
    std::cout << "Toggling XBee \n" << '\n';
    system("config-pin -a p2.03 out");
    std::ofstream myfile;
    myfile.open ("/sys/class/gpio/gpio23/value");
    myfile << "0";
    myfile.close();
    sleep(1);
    myfile.open ("/sys/class/gpio/gpio23/value");
    myfile << "1";
    myfile.close();
    sleep(1);
}
// stopCommXBee()
//
// Set XBee's reset to 0 (original state)
//
// stops communication to other XBee
//

void stopCommXBee()
{
    std::cout << "Ending Communication with XBee \n";
    system("config-pin -a p2.03 out");
    std::ofstream myfile;
    myfile.open ("/sys/class/gpio/gpio23/value");
    myfile << "0";
    myfile.close();
}

// readSensorData()
//
// Thread 1
//
// Reads Sensor data over UART
//
//
void readSensorData(const char *portname)
{
    std::cout << "Starting to Read Sensor Data \n";
    int fd;
    int wlen;

    fd = open(portname, O_RDWR | O_NOCTTY | O_SYNC);
    if (fd < 0)
    {
        printf("Error opening %s: %s\n", portname, strerror(errno));
    }
    /*baudrate 9600, 8 bits, no parity, 1 stop bit */
    set_interface_attribs(fd, B9600);

    /* simple canonical input */
    do {
        unsigned char buf[81];
        unsigned char *p;
        int rdlen;
        rdlen = read(fd, buf, sizeof(buf) - 1);
        if (rdlen > 0) {
            buf[rdlen] = 0;
            // Set a delimitor
            for (p = buf; rdlen-- > 0; p++) {
                if (*p < ' ')
                    *p = ':';   /* replace any control chars */
            }
            //std::cout << buf << '\n';
            if(buf[0] == ':')
            {
                // Use in strtok function
                const char delim[] = ":";
                // Acceleration in X direction
                if((buf[1] == 'A') && (buf[2] == 'X'))
                {
                    int j = 0;
                    // Parse Data
                    char *ptr = strtok((char *) buf, delim);
                    while(j++ < 2)
                    {
                        if (j)
                        {
                            accelerationX = strtof(ptr, NULL);
                        }
                        printf("'%s'\n", ptr);
                        ptr = strtok(NULL, delim);
                    }
                }
                // Acceleration in Y direction
                else if ((buf[1] == 'A') && (buf[2] == 'Y'))
                {
                    int j = 0;
                    // Parse Data
                    char *ptr = strtok((char *) buf, delim);
                    while(j++ < 2)
                    {
                        if (j)
                        {
                            accelerationY = strtof(ptr, NULL);
                        }
                        printf("'%s'\n", ptr);
                        ptr = strtok(NULL, delim);
                    }
                }
                // Acceleration in Z direction
                else if ((buf[1] == 'A') && (buf[2] == 'Z'))
                {
                    int j  = 0;
                    // Parse Data
                    char *ptr = strtok((char *) buf, delim);
                    while(j++ < 2)
                    {
                        if (j)
                        {
                            accelerationZ = strtof(ptr, NULL);
                        }
                        printf("'%s'\n", ptr);
                        ptr = strtok(NULL, delim);
                    }
                }
                // Temperature Reading
                else if (buf[1] == 'T')
                {
                    int j  = 0;
                    char *ptr = strtok((char *) buf, delim);
                    while(j++ <2)
                    {
                        if (j)
                        {
                            postTemp = atoi(ptr);
                        }
                        printf("'%s'\n", ptr);
                        ptr = strtok(NULL, delim);
                    }
                }
                else if (buf[1] == 'H')
                {
                    int j  = 0;
                    char *ptr = strtok((char *) buf, delim);
                    while(j++ <2)
                    {
                        if (j)
                        {
                            humidity = atoi(ptr);
                        }
                        printf("'%s'\n", ptr);
                        ptr = strtok(NULL, delim);
                    }
                }

            }
        }
        else if (rdlen < 0)
        {
                printf("Error from read: %d: %s\n", rdlen, strerror(errno));
                break;
        }
        else
        {  /* rdlen == 0 */
                printf("Nothing read. EOF?\n");
                break;
         }
            /* repeat read */
        } while (stillPosting);
}


// postSensorData()
//
// Thread 2
//
// Sends Sensor data to AWS cloud server
//
// Sends temperature, acceleration, humdity, 
// and motion detection
//
//
void postSensorData()
{
    std::cout << "Starting to Post Sensor Data \n";
    CURL *curl;
    CURLcode res;

    curl_global_init(CURL_GLOBAL_ALL);

    curl = curl_easy_init();
    if(curl) {
        curl_easy_setopt(curl, CURLOPT_URL, requestUrl);

        char buff[320];
        while(posts++ < NUMBER_OF_CLOUD_POSTS)
        {
            //Create JSON
            snprintf(buff, sizeof(buff),"{\"currentTemperature\":\"%d\",\"currentAccelerationX\":\"%f\",\"currentAccelerationY\":\"%f\",\"currentAccelerationZ\":\"%f\", \"currentHumidity\":\"%d\", \"motionDetected\":\"%s\"}",postTemp, accelerationX, accelerationY, accelerationZ, humidity,motionDetected.c_str());

            curl_easy_setopt(curl, CURLOPT_POSTFIELDS, buff);

            //Set headers
            struct curl_slist *headers = NULL;
            headers = curl_slist_append(headers, "Accept: application/json");
            headers = curl_slist_append(headers, "Content-Type: application/json");
            headers = curl_slist_append(headers, "charsets: utf-8");

            //Set the request type to put
            curl_easy_setopt(curl, CURLOPT_CUSTOMREQUEST, "PUT");
            curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);

            res = curl_easy_perform(curl);

            if(res != CURLE_OK)
                fprintf(stderr, "curl_easy_perform() failed: %s\n",
                    curl_easy_strerror(res));
            std::this_thread::sleep_for(std::chrono::seconds(1));
        }
        curl_easy_cleanup(curl);
    }
    stillPosting = false;
    std::cout << "Stopping posting \n";
    curl_global_cleanup();
}

// main()
//
// returns 0 on success
//
int main ()
{
    alertXBee();
    std::thread receivingThread(readSensorData, SERIALTERMINAL);
    std::thread postingThread(postSensorData);
    postingThread.join();
    receivingThread.join();
    stopCommXBee();
    return 0;
}
