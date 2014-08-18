package com.alipay.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class HashUtils {

	public static MessageDigest messageDigest;
	static {
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
		} catch (NoSuchAlgorithmException e) {
			// impossible
		}
	}

	public static long timeKey() {
		return Long.MAX_VALUE - System.currentTimeMillis();
	}

	public static long fromTimeKey(long timeKey) {
		return Long.MAX_VALUE - timeKey;

	}

	public static String md5(String string) {
		return md5(string.getBytes());
	}

	public static String md5(byte[] bytes) {
		messageDigest.update(bytes);
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		byte[] tmp = messageDigest.digest();
		char[] buffer = new char[32];
		int k = 0;
		for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
			// 转换成 16 进制字符的转换
			byte byte0 = tmp[i]; // 取第 i 个字节
			buffer[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
			// >>> 为逻辑右移，将符号位一起右移
			buffer[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
		}
		return new String(buffer);
	}

	public static long time33(String str) {
		int len = str.length();
		long hash = 0;
		for (int i = 0; i < len; i++)
			// (hash << 5) + hash 相当于 hash * 33
			hash = (hash << 5) + hash + (int) str.charAt(i);
		return hash;
	}

	private static final int IP;
	private static short counter = (short) 0;
	private static final int JVM = (int) (System.currentTimeMillis() >>> 8);
	// private final static String sep = "";
	static {
		int ipadd = 0;
		try {
			ipadd = NetUtils.iptoInt(InetAddress.getLocalHost().getAddress());
		} catch (UnknownHostException e) {
			ipadd = 0;
		}
		IP = ipadd;
	}

	protected static String format(int intval) {
		String formatted = Integer.toHexString(intval);
		StringBuffer buf = new StringBuffer("00000000");
		buf.replace(8 - formatted.length(), 8, formatted);
		return buf.toString();
	}

	protected static String format(short shortval) {
		String formatted = Integer.toHexString(shortval);
		StringBuffer buf = new StringBuffer("0000");
		buf.replace(4 - formatted.length(), 4, formatted);
		return buf.toString();
	}

	protected static short getCount() {
		synchronized (HashUtils.class) {
			if (counter < 0)
				counter = 0;
			return counter++;
		}
	}

	public static String uuid() {
		return new StringBuffer(32).append(format(IP)).append(format(JVM)).append(format((short) (System.currentTimeMillis() >>> 32)))
				.append(format((int) System.currentTimeMillis())).append(format(getCount())).toString();
	}
	public static String systemUuid() {
		return UUID.randomUUID().toString().replaceAll("\\-", "");
	}

}
