package tech.cnmn.simpl_serial_port.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by zsc on 2017/3/31.
 */
public class ExceptionUtils {
    /**
     * 异常转成字符串
     * @param t
     * @return
     */
    public static String getTrace(Throwable t) {
        StringWriter stringWriter= new StringWriter();
        PrintWriter writer= new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer= stringWriter.getBuffer();
        return buffer.toString();
    }
}
