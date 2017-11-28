package com.rayworld.androidlibs;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;

/**
 * 로그메시지 기록에 대한 처리를 담당한다
 */
public class NLog {
	private static File mLogFile;
	
	public static void initialize(Context context, File rootDir, String appName) {
		mLogFile = new File(rootDir, appName + ".txt");
	}
	
	public static File getLogFile() {
		return mLogFile;
	}

	public static void reset() {
		mLogFile.delete();
		write("NLog.reset()");
	}

	public static void write(String strMessage) {
		String _strMessage = strMessage;
		if ((strMessage == null) || (strMessage.length() == 0))
			return;
		
		String className = getLastName(new Throwable().getStackTrace()[1].getClassName());
		String methodName = getLastName(new Throwable().getStackTrace()[1].getMethodName());
		int line =new Throwable().getStackTrace()[1].getLineNumber();
		
		StringBuffer sb = new StringBuffer();
		sb.append("[T]" + getCurrentTime() + " ");

		sb.append("[C]" + className + " ");
		sb.append("[M]" + methodName + " ");
		sb.append("[L]" + line + " ");
		sb.append("[m]" + _strMessage + "\n\r");
		
		_strMessage = sb.toString();

		long size = mLogFile.length();
		double sizeMB = size/1024.0/1024.0;
		if(sizeMB>1) {
			reset();
		}
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(mLogFile, true);
			BufferedWriter buw = new BufferedWriter(new OutputStreamWriter(fos, "UTF8"));
			buw.write(_strMessage);
		    buw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static String getLastName(String str) {
		int pos = str.lastIndexOf(".");
		str = str.substring(pos+1, str.length());
		return str;
	}

	@SuppressLint("DefaultLocale")
	private static String getCurrentTime() {
		Calendar calendar = Calendar.getInstance();
		String strTime = String.format("%02d-%02d-%02d %02d:%02d:%02d",
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
		return strTime;
	}
}
