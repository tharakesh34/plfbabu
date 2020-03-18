package com.pennanttech.pennapps.dms.test;

import org.apache.commons.io.FilenameUtils;

public class DMSTest {
	private static String ftpHostName = "ftp.pennapps.net";
	private static String ftpPort = "21";
	private static String ftpUsername = "clix@pennapps.net";
	private static String ftpPassword = "clix@123";

	public static void main(String[] args) {
		
		String fileName="/FTPbyte/customer/344/abc.png";
		System.out.println(FilenameUtils.getFullPathNoEndSeparator(fileName));
		/*try {
			byte[] fileData = FileUtils.readFileToByteArray(new File("C:/Users/swamy.p/Desktop/images/Aadhaar.png"));
			FTPUtil.writeBytesToFTP(Protocol.FTP, ftpHostName, ftpPort, ftpUsername, ftpPassword,
					"/opt/dms", "/FTPbyte/customer/344/abc.png", fileData);

		} catch (IOException e) {
			e.printStackTrace();
		}*/

	}

}
