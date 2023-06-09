package com.pennanttech.external.app.util;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.config.model.FileTransferConfig;
import com.pennanttech.pennapps.core.net.FTPClient;
import com.pennanttech.pennapps.core.net.FTPUtil;
import com.pennanttech.pennapps.core.net.Protocol;
import com.pennanttech.pennapps.core.resource.Literal;

public class FileTransferUtil {
	private static final Logger logger = LogManager.getLogger(FileTransferUtil.class);

	FileTransferConfig fic;

	public FileTransferUtil(FileInterfaceConfig fic) {
		fic.setFileTransferConfig(FileTransferConfigUtil.getFIConfig(fic.getFicNames()));
		this.fic = fic.getFileTransferConfig();

	}

	public void uploadToSFTP(String localPath, String fileName) {
		logger.debug(Literal.ENTERING);
		FTPUtil.upload(Protocol.SFTP, fic.getHostName(), String.valueOf(fic.getPort()), fic.getAccessKey(),
				fic.getSecretKey(), fic.getSftpLocation(), localPath, fileName);
		logger.debug(Literal.LEAVING);
	}

	public void backupToSFTP(String localPath, String fileName) {
		logger.debug(Literal.ENTERING);
		FTPUtil.upload(Protocol.SFTP, fic.getHostName(), String.valueOf(fic.getPort()), fic.getAccessKey(),
				fic.getSecretKey(), fic.getSftpBackupLocation(), localPath, fileName);
		logger.debug(Literal.LEAVING);
	}

	public void downloadFromSFTP(String fileNameToDownload, String localFolderPathToDownload) {
		logger.debug(Literal.ENTERING);
		FTPUtil.downlod(Protocol.SFTP, fic.getHostName(), String.valueOf(fic.getPort()), fic.getAccessKey(),
				fic.getSecretKey(), fic.getSftpLocation(), localFolderPathToDownload, fileNameToDownload);
		logger.debug(Literal.LEAVING);
	}

	public void deleteFileFromSFTP(String fileName) {
		logger.debug(Literal.ENTERING);
		FTPUtil.delete(Protocol.SFTP, fic.getHostName(), String.valueOf(fic.getPort()), fic.getAccessKey(),
				fic.getSecretKey(), fic.getSftpLocation(), fileName);
		logger.debug(Literal.LEAVING);
	}

	public List<String> fetchFileNamesListFromSFTP() {
		FTPClient sftpClient = FTPUtil.getSFTPClient(fic.getHostName(), String.valueOf(fic.getPort()),
				fic.getAccessKey(), fic.getSecretKey());
		return sftpClient.listFiles(fic.getSftpLocation());
	}

}
