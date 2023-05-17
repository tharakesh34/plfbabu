package com.pennanttech.external.app.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.ftp.FtpClient;
import com.pennanttech.pennapps.core.ftp.SftpClient;

public class ExtSFTPUtil {

	private FileInterfaceConfig externalConfig;

	public ExtSFTPUtil(FileInterfaceConfig externalConfig) {
		this.externalConfig = externalConfig;
	}

	public List<String> getFileListFromSFTP(String remotePath) {
		if (externalConfig == null) {
			return null;
		}
		Session session = null;
		Channel channel = null;
		JSch jsch = new JSch();
		try {
			session = jsch.getSession(externalConfig.getAccessKey(), externalConfig.getHostName(),
					externalConfig.getPort());
			session.setPassword(externalConfig.getSecretKey());
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
			filelist = ((ChannelSftp) channel).ls(remotePath);
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

	public FtpClient getSFTPConnection() {
		if (externalConfig == null) {
			return null;
		}
		FtpClient ftpClient = null;
		String host = externalConfig.getHostName();
		int port = externalConfig.getPort();
		String accessKey = externalConfig.getAccessKey();
		String secretKey = externalConfig.getSecretKey();
		try {
			ftpClient = new SftpClient(host, port, accessKey, secretKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ftpClient;
	}

	public void deleteFile(String filePath) throws IOException {
		new SftpClient(externalConfig.getHostName(), externalConfig.getPort(), externalConfig.getAccessKey(),
				externalConfig.getSecretKey()).deleteFile(filePath);
	}
}
