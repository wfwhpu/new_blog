package com.eumji.zblog.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {
	/**
	 * 获得异常的堆栈信息，以字符串的形式返回
	 * @param integer
	 * @return
	 */
	public static String getExceptionInfo(Throwable e) {
		// TODO Auto-generated method stub
		StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw, true);
	    e.printStackTrace(pw);
	    pw.flush();
	    sw.flush();
		return sw.toString();
	}
}
