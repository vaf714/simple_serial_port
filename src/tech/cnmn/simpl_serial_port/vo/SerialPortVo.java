package tech.cnmn.simpl_serial_port.vo;

import gnu.io.SerialPort;
import tech.cnmn.simpl_serial_port.exception.*;
import tech.cnmn.simpl_serial_port.listener.PortListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

/**
 * Created by zsc on 2017/11/30.
 */
public class SerialPortVo {

    private String name;

    private SerialPort serialPort;

    public SerialPortVo(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 发送数据到串口
     * @param data
     * @throws SendDataToSerialPortFailure
     * @throws SerialPortOutputStreamCloseFailure
     */
    public void sendData(byte[] data) throws SendDataToSerialPortFailure, SerialPortOutputStreamCloseFailure {
        OutputStream out = null;
        try {
            out = serialPort.getOutputStream();
            out.write(data);
            out.flush();
        } catch (IOException e) {
            throw new SendDataToSerialPortFailure();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                throw new SerialPortOutputStreamCloseFailure();
            }
        }

    }

    /**
     * 从串口读取数据
     * @return
     * @throws ReadDataFromSerialPortFailure
     * @throws SerialPortInputStreamCloseFailure
     */
    public byte[] readData() throws ReadDataFromSerialPortFailure, SerialPortInputStreamCloseFailure {
        InputStream in = null;
        byte[] bytes = null;
        try {
            in = serialPort.getInputStream();
            int buffLength = in.available(); // 获取buffer里的数据长度
            while (buffLength != 0) {
                bytes = new byte[buffLength]; // 初始化byte数组为buffer中数据的长度
                in.read(bytes);
                buffLength = in.available();
            }
        } catch (IOException e) {
            throw new ReadDataFromSerialPortFailure();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                throw new SerialPortInputStreamCloseFailure();
            }
        }
        return bytes;

    }

    /**
     * 绑定监听器
     * @param listener
     * @param sleepTime
     * @throws TooManyListeners
     */
    public void bindListener(String name, PortListener listener, int sleepTime) throws TooManyListeners {
        try {
            //设置名称
            this.name = name;
            //设置监听器
            listener.setSerialPort(this);
            listener.setSleepTime(sleepTime);
            // 给串口添加监听器
            serialPort.addEventListener(listener);
            // 设置当有数据到达时唤醒监听接收线程
            serialPort.notifyOnDataAvailable(true);
            // 设置当通信中断时唤醒中断线程
            serialPort.notifyOnBreakInterrupt(true);
        } catch (TooManyListenersException e) {
            throw new TooManyListeners();
        }
    }

    /**
     * 关闭串口
     */
    public void closePort() {
        if (serialPort != null) {
            serialPort.close();
        }
    }
}
