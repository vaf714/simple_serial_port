# simple_serial_port  ![](https://img.shields.io/badge/language-java-brightgreen.svg?style=plastic)

> The RXTX project provides implementations of the compatible javax.comm serial communication package API for Windows, Linux, Mac os X, and Solaris operating systems, making it easier for other developers to develop serial applications under such systems. This repository refactors part of the code based on the RXTX package, which can be used to implement java serial communication in a few simple steps. This is not an executable program. It should be used as a dependency library for your project or compiled into a jar package.

[中文](https://github.com/vaf714/simple_serial_port) | English

## Ready
Download the dependency package in [releases](https://github.com/vaf714/simple_serial_port/releases)
1. `rxtx-2.2pre2-bins.zip` is the RXTX open source package, the windows platform configuration method is as follows, please read the official documentation for other platforms.
  - Put `rxtxSerial.dll` in `<system disk>\Windows\System32`
  - Put `rxtxSerial.dll` in `<JAVA_HOME>\jre\bin`
  - Put `RXTXcomm.jar` in `<JAVA_HOME>\jre\lib\ext`
2. Add the `RXTXcomm.jar` and log4j dependent jar packages (within the jar folder) to the project.
3. Add the `simple_serial_port.jar` into your project, or directly import the project source code into your project as a dependent library.
## Use
1. Find available serial ports

```java
ArrayList<String> ports = SerialTool.findPort();
```
2. Open the serial port found above
```java
// The first parameter is used to assign a name to serialPortVo to facilitate identification of different serialPortVo , which can be obtained via serialPortVo.getName()
// Open the previous step to find the first serial port in the serial port collection, which can be changed as needed.
SerialPortVo serialPortVo = SerialTool.openPort("I am the serial port of dtu", ports.get(0), baudRate);
```
3. The new listener inherits the `PortListener` parent class and overrides the `onReceive()` and `onReadException()` methods. When the serial port receives the data, it automatically calls the `onReceive()` method. When the data is received, an exception will be called. `onReadException()` method
```java
public class MyListener extends PortListener {
    @Override
    public void onReceive(byte[] data) {
        String dataStr = new String(data).trim();
        System.out.println("Serial received data:" + dataStr);
    }
    
    @Override
    public void onReadException(Exception e) {
        System.out.println("An exception occurred:" + e.getMessage());
    }
}
```
4. Use the `SerialPortVo` object binding listener returned when opening the serial port
```java
// The third parameter 500 is to wait for 500 ms after the serial port receives the data and then read the serial port data.
/ / Prevent data from reading the serial port does not completely reach the serial port, you can adjust the waiting time according to the size of the data
serialPortVo.bindListener(new MyListener(), 500);
```
5. Send data to the serial port
- Method 1: Outside the listener, send the byte array type data using the `SerialPortVo` object returned when the serial port is opened.
```java
// Use the SerialPortVo object sending method returned by opening the serial port
String reply = "I am sending a message to you outside the listener class";
serialPortVo.sendData(reply.getBytes());
```
- Method 2: In the listener, directly use the listener's internal `SerialPortVo` object to call the data of the byte array type.
```java
public class MyListener extends PortListener {
    @Override
    public void onReceive(byte[] data) {
        String dataStr = new String(data).trim();
        // Call the send method directly using the built-in SerialPortVo object
        String reply = "I received the data you sen";
        serialPortVo.sendData(reply.getBytes());
    }
    
    @Override
    public void onReadException(Exception e) {
        //...
    }
}
```
