package com.hzy3774.chengyudict;

public class Consts {
	public static final String fileMD5 = "FF78F00EA8D5E4A1C5361E9A229C28EA";
	public static final int bufferSize = 0x400000;
	public static final String assetName = "cydictz";
	public static final String fileName = "cydict";
	public static final String sdDir = "cydict";
	
	/**DB file is ready*/
	public static final int msgFileOk = 0;
	public static final int msgHandleFileStart = 1;
	public static final int msgHandleFileEnd = 2;
	public static final int msgHandleFileError = 3;
	public static final int msgLevelFinish = 4;
	public static final int msgQueryWord = 5;
	public static final int msgQueryRelated = 6;
}
