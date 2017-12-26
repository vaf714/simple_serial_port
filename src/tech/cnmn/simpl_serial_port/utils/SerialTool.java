package tech.cnmn.simpl_serial_port.utils;

import gnu.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.cnmn.simpl_serial_port.exception.NoSuchPort;
import tech.cnmn.simpl_serial_port.exception.NotASerialPort;
import tech.cnmn.simpl_serial_port.exception.PortInUse;
import tech.cnmn.simpl_serial_port.vo.SerialPortVo;
import tech.cnmn.simpl_serial_port.exception.SerialPortParameterFailure;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by zsc on 2017/5/9.
 * 串口服务类，提供打开、关闭串口，读取、发送串口数据等服务（采用单例设计模式）
 */
public class SerialTool {
	static Logger logger = LoggerFactory.getLogger(SerialTool.class);

	// 私有化SerialTool类的构造方法，不允许其他类生成SerialTool对象
	private SerialTool() {
	}

	/**
	 * 查找所有可用端口
	 *
	 * @return 可用端口名称列表
	 */
	public static final ArrayList<String> findPort() {

		// 获得当前所有可用串口
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();

		ArrayList<String> portNameList = new ArrayList<>();

		// 将可用串口名添加到List并返回该List
		while (portList.hasMoreElements()) {
			String portName = portList.nextElement().getName();
			portNameList.add(portName);
		}
		return portNameList;

	}

	/**
	 * 打开串口
	 *
	 * @param portName
	 *            端口名称
	 * @param baudrate
	 *            波特率
	 * @return 串口对象
	 * @throws SerialPortParameterFailure
	 *             设置串口参数失败
	 * @throws NotASerialPort
	 *             端口指向设备不是串口类型
	 * @throws NoSuchPort
	 *             没有该端口对应的串口设备
	 * @throws PortInUse
	 *             端口已被占用
	 */
	public static final SerialPortVo openPort(String name, String portName, int baudrate)
			throws SerialPortParameterFailure, NotASerialPort, NoSuchPort,
			PortInUse {

		try {

			// 通过端口名识别端口
			CommPortIdentifier portIdentifier = CommPortIdentifier
					.getPortIdentifier(portName);

			// 打开端口，并给端口名字和一个timeout（打开操作的超时时间）
			CommPort commPort = portIdentifier.open(portName, 2000);
			// 判断是不是串口
			if (commPort instanceof SerialPort) {

				SerialPort serialPort = (SerialPort) commPort;
				SerialPortVo serialPortVo = new SerialPortVo(serialPort);
				serialPortVo.setName(name);
				try {
					// 设置一下串口的波特率等参数
					serialPort.setSerialPortParams(baudrate,
							SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);
				} catch (UnsupportedCommOperationException e) {
					throw new SerialPortParameterFailure();
				}
				return serialPortVo;

			} else {
				// 不是串口
				throw new NotASerialPort();
			}
		} catch (NoSuchPortException e1) {
			throw new NoSuchPort();
		} catch (PortInUseException e2) {
			throw new PortInUse();
		}
	}
}
