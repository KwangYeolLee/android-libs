package com.rayworld.androidlibs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

public class ByteUtil {
	
	/**
	 * Change int to Byte[] order by little-endian
	 * @param value
	 * @return
	 */
	public static byte[] int2ByteLittle(int value) {
		ByteBuffer buf = ByteBuffer.allocate(Integer.SIZE / 8);
	    buf.putInt(value);
	    buf.order(ByteOrder.LITTLE_ENDIAN);
	    byte[] byteArry = buf.array();
	    return changeByteOrder(byteArry); 
	}
	
	/**
	 * Change int to Byte[] order by big-endian
	 * @param value
	 * @return
	 */
	public static byte[] int2ByteBig(int value) {
		ByteBuffer buf = ByteBuffer.allocate(Integer.SIZE / 8);
	    buf.putInt(value);
	    buf.order(ByteOrder.BIG_ENDIAN);
	    byte[] byteArry = buf.array();
	    return changeByteOrder(byteArry);
	}
	
	/**
	 * Change long to Byte[] order by little-endian
	 * @param value
	 * @return
	 */
	public static byte[] long2ByteLittle(long value) {
		ByteBuffer buf = ByteBuffer.allocate(Long.SIZE / 8);
	    buf.putLong(value);
	    buf.order(ByteOrder.LITTLE_ENDIAN);
	    byte[] byteArry = buf.array();
	    return changeByteOrder(byteArry);
	}
	
	/**
	 * Change long to Byte[] order by big-endian
	 * @param value
	 * @return
	 */
	public static byte[] long2ByteBig(long value) {
		ByteBuffer buf = ByteBuffer.allocate(Long.SIZE / 8);
	    buf.putLong(value);
	    buf.order(ByteOrder.BIG_ENDIAN);
	    byte[] byteArry = buf.array();
	    return changeByteOrder(byteArry);
	}
	
	/**
	 * Change Byte to Byte order by little-endian
	 * @param value
	 * @return
	 */
	public static byte[] byte2ByteLittle(byte value) {
		ByteBuffer buf = ByteBuffer.allocate(Byte.SIZE / 8);
	    buf.put(value);
	    buf.order(ByteOrder.LITTLE_ENDIAN);
	    byte[] byteArry = buf.array();
	    return byteArry;
	}
	
	/**
	 * Change Byte to Byte order by big-endian
	 * @param value
	 * @return
	 */
	public static byte[] byte2ByteBig(byte value) {
		ByteBuffer buf = ByteBuffer.allocate(Byte.SIZE / 8);
	    buf.put(value);
	    buf.order(ByteOrder.BIG_ENDIAN);
	    byte[] byteArry = buf.array();
	    return byteArry;
	}
	
	/**
	 * Change Byte[] to Byte[] order by little-endian
	 * @param value
	 * @return
	 */
	public static byte[] byteArr2ByteArr(byte[] value) {
		ByteBuffer buf = ByteBuffer.allocate(value.length);
	    buf.put(value);
	    buf.order(ByteOrder.LITTLE_ENDIAN);
	    byte[] byteArry = buf.array();
	    return byteArry;	//changeByteOrder(byteArry);
	}
	
	/**
	 * Change Byte Order Big Endian to Little Endian
	 * @param value
	 * @return
	 */
	public static byte[] changeByteOrder(byte[] value) {
	    int idx = value.length;
	    byte[] temp = new byte[idx];
	 
	    for (int i = 0; i < idx; i++) {
	        temp[i] = value[idx - (i + 1)];
	    }
	    return temp;
	}
	
	/**
     * 패킷의 특정구간을 잘라 낼 때 사용.
     * @param src : 바이트배열
     * @param offset : 시작위치
     * @param length : 시작위치부터 길이
     * @return
     */
    public static byte[] getbytes(byte src[], int offset, int length) {
		byte dest[] = new byte[length];
		System.arraycopy(src, offset, dest, 0, length);
		return dest;
	}
    
    /**
     * 문자열 바이트값을 받아서 GB 반환한다(단위 'GB' 붙여서 반환)
     * @param strBytes
     * @return
     */
    public static String getByte2GB(String strBytes) {
    	long bytes = Long.parseLong(strBytes);
    	double gbytes = bytes / 1024.0 / 1024.0 / 1024.0;
    	return String.format(Locale.getDefault(), "%.1fGB", gbytes);
    }

	/**
	 * 문자열 바이트값을 받아서 GB 반환한다(단위 'GB' 붙여서 반환)
	 * @param bytes
	 * @return
	 */
	public static double getByte2GB(long bytes) {
		double gbytes = bytes / 1024.0 / 1024.0 / 1024.0;
		String strGB = String.format(Locale.getDefault(), "%.1f", gbytes);
		return Double.parseDouble(strGB);
	}
    
    /**
	 * 정수값의 비트자리수의 값이 1인지 0인지 확인<br>
	 * 
	 * @param value
	 *            정수값<br>
	 * @param bitDigit
	 *            비트자리수<br>
	 * @return true : 비트자리수의 값이 1<br>
	 *         false : 비트자리수의 값이 0<br>
	 */
	public static boolean integerToBitDigitValue(int value, int bitDigit) {
		int bitVal = ((Double) Math.pow(2, bitDigit)).intValue();
		return (value & bitVal) == bitVal;
	}
}
