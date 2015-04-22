package com.hzy3774.chengyudict;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
	/**
	 * Get MD5 of a string
	 * 
	 * @param src
	 * @return
	 */
	public static String getStringMD5(String src) {
		MessageDigest messageDigest = null;
		byte[] srcBytes = src.getBytes();
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		messageDigest.update(srcBytes, 0, srcBytes.length);
		BigInteger bigInt = new BigInteger(1, messageDigest.digest());
		return bigInt.toString(16);
	}

	/**
	 * get MD5 of a file
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileMD5(File file) {
		MessageDigest messageDigest = null;
		FileInputStream fileInStream = null;
		byte buffer[] = new byte[1024];
		int length = -1;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			fileInStream = new FileInputStream(file);
			while ((length = fileInStream.read(buffer, 0, 1024)) != -1) {
				messageDigest.update(buffer, 0, length);
			}
			fileInStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, messageDigest.digest());
		return bigInt.toString(16);
	}

	/**
	 * get MD5 of a file with file path
	 * 
	 * @param path
	 * @return
	 */
	public static String getFileMD5(String path) {
		return getFileMD5(new File(path));
	}
}
