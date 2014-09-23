package com.pennant;

public class TestingUtil {
	public static String fileLocation = "D:/";
	public static String yesrno(boolean bool) {
		String yes = " ";
		if (bool) {
			yes = "Y";
		}
		return yes;
	}
	
	public static String getFileLoc(){
		return fileLocation;
	}
}
