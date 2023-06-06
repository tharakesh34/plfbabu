package com.pennanttech.external.app.util;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.pennapps.core.net.FTPClient;
import com.pennanttech.pennapps.core.net.FTPUtil;
import com.pennanttech.pennapps.core.net.Protocol;
import com.pennanttech.pennapps.core.resource.Literal;

public class FileTransferUtil {
	private static final Logger logger = LogManager.getLogger(FileTransferUtil.class);

	FileInterfaceConfig fic;

	public FileTransferUtil(FileInterfaceConfig fic) {
		this.fic = fic;
	}

	public void uploadToSFTP(String localPath, String fileName) {
		logger.debug(Literal.ENTERING);
		FTPUtil.upload(Protocol.SFTP, fic.getHostName(), String.valueOf(fic.getPort()), fic.getAccessKey(),
				fic.getSecretKey(), fic.getFileSftpLocation(), localPath, fileName);
		logger.debug(Literal.LEAVING);
	}

	public void backupToSFTP(String localPath, String fileName) {
		logger.debug(Literal.ENTERING);
		FTPUtil.upload(Protocol.SFTP, fic.getHostName(), String.valueOf(fic.getPort()), fic.getAccessKey(),
				fic.getSecretKey(), fic.getFileBackupLocation(), localPath, fileName);
		logger.debug(Literal.LEAVING);
	}

	public void downloadFromSFTP(String fileNameToDownload, String localFolderPathToDownload) {
		logger.debug(Literal.ENTERING);
		FTPUtil.downlod(Protocol.SFTP, fic.getHostName(), String.valueOf(fic.getPort()), fic.getAccessKey(),
				fic.getSecretKey(), fic.getFileSftpLocation(), localFolderPathToDownload, fileNameToDownload);
		logger.debug(Literal.LEAVING);
	}

	public void deleteFileFromSFTP(String fileName) {
		logger.debug(Literal.ENTERING);
		FTPUtil.delete(Protocol.SFTP, fic.getHostName(), String.valueOf(fic.getPort()), fic.getAccessKey(),
				fic.getSecretKey(), fic.getFileSftpLocation(), fileName);
		logger.debug(Literal.LEAVING);
	}

	public List<String> fetchFileNamesListFromSFTP() {
		FTPClient sftpClient = FTPUtil.getSFTPClient(fic.getHostName(), String.valueOf(fic.getPort()),
				fic.getAccessKey(), fic.getSecretKey());
		return sftpClient.listFiles(fic.getFileSftpLocation());
	}

}
