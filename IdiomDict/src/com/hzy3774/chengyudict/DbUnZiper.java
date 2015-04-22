package com.hzy3774.chengyudict;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

import android.content.Context;
import android.os.Handler;

import com.hu.andun7z.AndUn7z;

public class DbUnZiper {

	Handler handler = null;
	String appPath;
	String filePath;
	Context context = null;

	public DbUnZiper(Handler handler, Context context, String appPath) {
		this.handler = handler;
		this.appPath = appPath;
		this.filePath = appPath + Consts.fileName;
		this.context = context;
	}

	public void start() {
		File file = new File(filePath);
		if (file.exists() && MD5(file).equalsIgnoreCase(Consts.fileMD5)) {
			handler.sendEmptyMessage(Consts.msgFileOk);
		} else {
			handler.sendEmptyMessage(Consts.msgHandleFileStart);
			new Thread() {
				@Override
				public void run() {
					try {
						if (!moveAsset()) { // move from assets
							handler.sendEmptyMessage(Consts.msgHandleFileError);
							return;
						}
						AndUn7z.extract7z(appPath + Consts.assetName, appPath); // extract
						new File(appPath + Consts.assetName).delete(); // delete
					} catch (Exception e) {
						handler.sendEmptyMessage(Consts.msgHandleFileError);
						e.printStackTrace();
					}
					handler.sendEmptyMessage(Consts.msgHandleFileEnd);
				}
			}.start();
		}
	}

	private boolean moveAsset() {
		try {
			InputStream inputStream = context.getAssets().open(Consts.assetName);
			FileOutputStream fileOutputStream = new FileOutputStream(appPath + Consts.assetName);
			int length = -1;
			byte[] buffer = new byte[Consts.bufferSize];
			while ((length = inputStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, length);
			}
			fileOutputStream.flush();
			fileOutputStream.close();
			inputStream.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private String MD5(File file) { // 获取文件的MD5值
		MessageDigest messageDigest = null;
		FileInputStream fileInStream = null;
		byte buffer[] = new byte[Consts.bufferSize];
		int length = -1;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			fileInStream = new FileInputStream(file);
			while ((length = fileInStream.read(buffer, 0, Consts.bufferSize)) != -1) {
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
}
