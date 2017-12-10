package tech.cnmn.simpl_serial_port.listener;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.cnmn.simpl_serial_port.vo.SerialPortVo;
import tech.cnmn.simpl_serial_port.utils.ExceptionUtils;

/**
 * Created by zsc on 2017/5/9.
 */
public abstract class PortListener implements SerialPortEventListener {
    final Logger logger = LoggerFactory.getLogger(PortListener.class);
    protected SerialPortVo serialPortVo;
    protected int sleepTime;

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public void setSerialPort(SerialPortVo serialPortVo) {
        this.serialPortVo = serialPortVo;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        switch (serialPortEvent.getEventType()) {
            case SerialPortEvent.BI: // 10 通讯中断
                logger.error("与串口设备通讯中断");
                break;
            case SerialPortEvent.OE: // 7 溢位（溢出）错误
            case SerialPortEvent.FE: // 9 帧错误
            case SerialPortEvent.PE: // 8 奇偶校验错误
            case SerialPortEvent.CD: // 6 载波检测
            case SerialPortEvent.CTS: // 3 清除待发送数据
            case SerialPortEvent.DSR: // 4 待发送数据准备好了
            case SerialPortEvent.RI: // 5 振铃指示
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 输出缓冲区已清空
                break;
            case SerialPortEvent.DATA_AVAILABLE: // 1 串口存在可用数据
                try {
                    // 延时收到字符串一段时间，足够接收所有字节，以免出现字符串隔断
                    Thread.sleep(sleepTime);
                    byte[] data = null;
                    if (serialPortVo.getSerialPort() == null) {
                        logger.error("\n串口对象为空！监听失败！");
                    } else {
                        data = serialPortVo.readData(); // 读取数据，存入字节数组
                        onReceive(data);
                    }
                } catch (Exception e) {
                    logger.error(ExceptionUtils.getTrace(e));
                }
                break;
        }
    }

    abstract public void onReceive(byte[] data);
}
