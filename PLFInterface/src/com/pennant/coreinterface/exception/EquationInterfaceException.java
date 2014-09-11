package com.pennant.coreinterface.exception;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.pennant.equation.util.DateUtility;

public class EquationInterfaceException extends Exception {

	private static final long serialVersionUID = 1L;
	public String message;
	public String data = null;

	public EquationInterfaceException() {
		super();
	}

	public EquationInterfaceException(Exception e) {
		writeLog(e);
		data = super.getMessage();
	}

	public String getMessage() {
		System.out.println("message : " + message);
		return message;
	}
	
	public EquationInterfaceException(String messsage) {
		super(messsage);
	}

	public String getMsgData() {
		return this.data;
	}

	/**
	 * This method writes Exception raised with time in any Bean Component to a
	 * external file with name KastleLog.txt
	 * 
	 * @Param Exception Return void
	 */
	public synchronized void writeLog(Exception e) {
		File file = null;
		FileOutputStream outputStream = null;
		PrintStream printStream = null;
		
		try {
			file = new File("EquationInterfacelog.txt");
			if (file.exists()) {
				outputStream = new FileOutputStream("EquationInterfacelog.txt", true);
				printStream = new PrintStream(outputStream);
			} else {
				outputStream = new FileOutputStream(file);
				printStream = new PrintStream(outputStream);
			}

			printStream.println(DateUtility.getTodayDateTime());
			e.printStackTrace();
			System.setErr(printStream);
			printStream.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}
