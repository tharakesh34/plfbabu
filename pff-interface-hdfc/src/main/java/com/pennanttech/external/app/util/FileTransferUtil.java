package com.pennanttech.external.app.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.config.model.FileTransferConfig;
import com.pennanttech.pennapps.core.AppException;
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
		// FTPClient sftpClient = FTPUtil.getFTPClient(fic.getHostName(), String.valueOf(fic.getPort()),
		// fic.getAccessKey(), fic.getSecretKey());
		// return sftpClient.listFiles(fic.getSftpLocation());

		if (fic == null) {
			return null;
		}
		Session session = null;
		Channel channel = null;
		JSch jsch = new JSch();
		try {
			session = jsch.getSession(fic.getAccessKey(), fic.getHostName(), fic.getPort());
			session.setPassword(fic.getSecretKey());
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
		} catch (JSchException e1) {
			e1.printStackTrace();
		}
		LsEntry entry = null;
		List<String> fileNames = new ArrayList<String>();
		Vector filelist = null;
		try {
			if (channel != null) {
				filelist = ((ChannelSftp) channel).ls(fic.getSftpLocation());
			}
		} catch (Exception e) {
			throw new AppException(e.getMessage());
		} finally {
			if (session != null) {
				session.disconnect();
			}
			if (channel != null) {
				channel.disconnect();
			}
		}
		for (int i = 0; i < filelist.size(); i++) {
			entry = (LsEntry) filelist.get(i);
			if (StringUtils.isNotEmpty(FilenameUtils.getExtension(entry.getFilename()))
					&& !entry.getFilename().startsWith(".")) {
				fileNames.add(entry.getFilename());
			}
		}
		return fileNames;

	}

}
