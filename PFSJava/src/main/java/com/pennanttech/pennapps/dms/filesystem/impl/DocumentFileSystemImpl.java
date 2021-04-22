package com.pennanttech.pennapps.dms.filesystem.impl;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.net.FTPUtil;
import com.pennanttech.pennapps.core.net.Protocol;
import com.pennanttech.pennapps.core.net.SystemUtils;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.dms.DMSProperties;
import com.pennanttech.pennapps.dms.DMSProtocol;
import com.pennanttech.pennapps.dms.DMSStorage;
import com.pennanttech.pennapps.dms.filesystem.DocumentFileSystem;
import com.pennanttech.pennapps.dms.model.DMSQueue;
import com.pennanttech.pff.external.ExternalDocumentFileService;
import com.pennanttech.service.AmazonS3Bucket;

public class DocumentFileSystemImpl implements DocumentFileSystem {
	private static Logger logger = LogManager.getLogger(DocumentFileSystemImpl.class);
	private String host = null;
	private String port = "0";
	private String username = null;
	private String password = null;
	private String privateKey = null;
	private String regionName = null;
	private String accessKey = null;
	private String secretKey = null;
	private String sseAlgorithm = null;
	private boolean flag = true;

	@Autowired(required = false)
	private ExternalDocumentFileService externalDocumentManagementService;

	@Override
	public String store(DMSQueue dmsQueue) {
		logger.debug("Writing Document to FS...");

		if (DMSStorage.FS == DMSStorage.getStorage(App.getProperty(DMSProperties.STORAGE))) {
			if (externalDocumentManagementService == null) {
				storeDocIntoFileSystems(dmsQueue);
				return dmsQueue.getDocUri();
			}
			try {
				dmsQueue.setDocUri(externalDocumentManagementService.store(dmsQueue));
				return dmsQueue.getDocUri();
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
				dmsQueue.setErrorCode("DMS99");
				dmsQueue.setErrorDesc(e.getMessage());
			} finally {
				int incre = dmsQueue.getAttemptNum() + 1;
				dmsQueue.setAttemptNum(incre);
				if (dmsQueue.getAttemptNum() >= 5) {
					dmsQueue.setProcessingFlag(-1);
				} else {
					dmsQueue.setProcessingFlag(0);
				}
			}
		} else if (DMSStorage.EXTERNAL == DMSStorage.getStorage(App.getProperty(DMSProperties.STORAGE))) {
			dmsQueue.setErrorCode("");
			dmsQueue.setErrorDesc("");

		}
		logger.debug(Literal.LEAVING);
		return dmsQueue.getDocUri();
	}
	
	@Override
	public byte[] retrive(String docURI) {
		logger.debug(Literal.ENTERING);
		if (DMSStorage.FS == DMSStorage.getStorage(App.getProperty(DMSProperties.STORAGE))) {
			return retriveDocFromFileSystem(docURI);
		} else {
			DMSQueue dmsQueue = new DMSQueue();
			dmsQueue.setDocUri(docURI);
			dmsQueue = externalDocumentManagementService.retrieve(dmsQueue);
			return dmsQueue.getDocImage();
		}
	}

	@Override
	public DMSQueue retriveDMS(String docURI) {
		logger.debug(Literal.ENTERING);

		DMSQueue dmsQueue = new DMSQueue();
		if (externalDocumentManagementService == null) {
			dmsQueue.setDocUri(docURI);
			byte[] docImage = retriveDocFromFileSystem(docURI);
			dmsQueue.setDocImage(docImage);
			return dmsQueue;
		} else {
			dmsQueue.setDocUri(docURI);
			dmsQueue = externalDocumentManagementService.retrieve(dmsQueue);
			return dmsQueue;
		}
	}

	private void storeDocIntoFileSystems(DMSQueue dmsQueue) {
		logger.debug(Literal.ENTERING);
		String filePrefix = createFolderStructure(dmsQueue);
		String fileName = createFileName(dmsQueue);

		DMSProtocol dmsProtocol = DMSProtocol.getProtocol(App.getProperty(DMSProperties.PROTOCOL));
		if (flag) {
			getAuthorizationDetails(dmsProtocol);
		}
		dmsQueue.setErrorCode("");
		dmsQueue.setErrorDesc("");

		if (dmsProtocol == null) {
			storeDocumentToFolder(dmsQueue, filePrefix, fileName);
		} else if (dmsProtocol == DMSProtocol.FTP || dmsProtocol == DMSProtocol.SFTP) {
			storeDocumentToFTP(dmsQueue, filePrefix, fileName);
		} else if (dmsProtocol == DMSProtocol.AMAZON_S3) {
			storeDocumentToS3(dmsQueue, filePrefix, fileName);
		}
		logger.debug(Literal.LEAVING);
	}

	protected String createFileName(DMSQueue dmsQueue) {
		logger.debug(Literal.ENTERING);
		StringBuilder fileName = new StringBuilder();
		fileName.append(dmsQueue.getDocManagerID());
		fileName.append("_");
		fileName.append(StringUtils.trimToEmpty(dmsQueue.getDocName()));
		logger.debug(Literal.LEAVING);
		return fileName.toString();

	}

	protected String createFolderStructure(DMSQueue dmsQueue) {
		logger.debug(Literal.ENTERING);
		StringBuilder path = new StringBuilder();
		path.append(File.separator);
		path.append(dmsQueue.getCustId());
		path.append(File.separator);

		DMSModule module = dmsQueue.getModule();
		DMSModule subModule = dmsQueue.getSubModule();

		path.append(DMSModule.getModule(module));
		path.append(File.separator);
		if (module == DMSModule.FINANCE && subModule != DMSModule.FINANCE) {
			String dmsSubModule = DMSModule.getModule(dmsQueue.getSubModule());
			if (StringUtils.isNotEmpty(dmsSubModule)) {
				path.append(dmsSubModule);
				path.append(File.separator);
			}
		}
		logger.debug(Literal.LEAVING);
		return path.toString();
	}

	private byte[] retriveDocFromFileSystem(String docURI) {
		logger.debug(Literal.ENTERING);
		DMSProtocol dmsProtocol = DMSProtocol.getProtocol(App.getProperty(DMSProperties.PROTOCOL));
		String filePath = null;
		if (flag) {
			getAuthorizationDetails(dmsProtocol);
		}
		if (dmsProtocol == DMSProtocol.AMAZON_S3) {
			filePath = SystemUtils.separatorsToSystem("/".concat(docURI));
		} else {
			filePath = SystemUtils.separatorsToSystem(getFSRoot().concat(docURI));
		}
		if (dmsProtocol == null) {
			logger.debug(Literal.LEAVING);
			return FTPUtil.readBytes(filePath);
		} else if (dmsProtocol == DMSProtocol.FTP) {

			if (StringUtils.isBlank(port)) {
				logger.debug(Literal.LEAVING);
				return FTPUtil.readBytesFromFTP(host, username, password, filePath);
			} else {
				logger.debug(Literal.LEAVING);
				return FTPUtil.readBytesFromFTP(Protocol.FTP, host, port, username, password, filePath);
			}

		} else if (dmsProtocol == DMSProtocol.SFTP) {
			if (StringUtils.isBlank(privateKey)) {
				logger.debug(Literal.LEAVING);
				return FTPUtil.readBytesFromFTP(Protocol.SFTP, host, port, username, password, filePath);
			} else {
				logger.debug(Literal.LEAVING);
				return FTPUtil.readBytesFromFTP(host, port, username, password, privateKey, filePath);
			}
		} else if (dmsProtocol == DMSProtocol.AMAZON_S3) {
			return retreiveDocumentFromS3(filePath);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	private byte[] retreiveDocumentFromS3(String filePath) {
		AmazonS3Bucket bucket = null;
		String bucketName = getFSRoot();
		if (host != null && port != null) {
			bucket = new AmazonS3Bucket(regionName, bucketName, accessKey, secretKey, host, Integer.valueOf(port));
		} else {
			bucket = new AmazonS3Bucket(regionName, bucketName, accessKey, secretKey);
		}
		bucket.setSseAlgorithm(sseAlgorithm);
		if (filePath.startsWith("/") || filePath.startsWith(File.separator)) {
			filePath = filePath.substring(1);
		}
		try {
			return bucket.getObject(filePath);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return null;
	}

	private void getAuthorizationDetails(DMSProtocol dmsProtocol) {
		if (dmsProtocol == DMSProtocol.FTP || dmsProtocol == DMSProtocol.SFTP) {
			host = App.getProperty(DMSProperties.FTP_HOST);
			port = App.getProperty(DMSProperties.FTP_PORT);
			username = App.getProperty(DMSProperties.FTP_USERNAME);
			password = App.getProperty(DMSProperties.FTP_PASSWORD);
			privateKey = App.getProperty(DMSProperties.FTP_PRIVATEKEY);
		} else if (dmsProtocol == DMSProtocol.AMAZON_S3) {
			host = App.getProperty(DMSProperties.AMAZON_S3_PROXY_DOMAIN);
			port = App.getProperty(DMSProperties.AMAZON_S3_PROXY_PORT);
			regionName = App.getProperty(DMSProperties.AMAZON_S3_REGION_NAME);
			accessKey = App.getProperty(DMSProperties.AMAZON_S3_ACCESS_KEY);
			secretKey = App.getProperty(DMSProperties.AMAZON_S3_SCECRET_KEY);
			sseAlgorithm = App.getProperty(DMSProperties.SSE_ALGORITHM);
		}
		flag = false;
	}

	private void storeDocumentToFolder(DMSQueue dmsQueue, String filePrefix, String fileName) {
		logger.debug(Literal.ENTERING);
		String fsRoot = getFSRoot();
		try {
			fsRoot = fsRoot.concat(filePrefix);
			FTPUtil.writeBytes(fsRoot, fileName, dmsQueue.getDocImage());
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			dmsQueue.setErrorCode("DMS99");
			dmsQueue.setErrorDesc(e.getMessage());
		} finally {
			setDmsStatus(dmsQueue, filePrefix, fileName);
		}
		logger.debug(Literal.LEAVING);
	}

	private void setDmsStatus(DMSQueue dmsQueue, String filePrefix, String fileName) {
		logger.debug(Literal.ENTERING);
		if (dmsQueue.getErrorCode() != "" && dmsQueue.getErrorDesc() != "") {
			if (dmsQueue.getAttemptNum() >= 3) {
				dmsQueue.setProcessingFlag(-1);
			} else {
				dmsQueue.setProcessingFlag(0);
			}
		} else {
			dmsQueue.setDocUri(filePrefix.concat(fileName));
			dmsQueue.setProcessingFlag(1);
		}
		dmsQueue.setAttemptNum(dmsQueue.getAttemptNum() + 1);
		logger.debug(Literal.LEAVING);
	}

	private void storeDocumentToFTP(DMSQueue dmsQueue, String filePrefix, String fileName) {
		logger.debug(Literal.ENTERING);
		String fsRoot = getFSRoot();
		fsRoot = fsRoot.concat(filePrefix);

		try {
			DMSProtocol dmsProtocol = DMSProtocol.getProtocol(App.getProperty(DMSProperties.PROTOCOL));

			byte[] fileContent = dmsQueue.getDocImage();
			if (dmsProtocol == DMSProtocol.FTP) {
				if (StringUtils.isNotBlank(port)) {
					FTPUtil.writeBytesToFTP(Protocol.FTP, host, port, username, password, fsRoot, fileName,
							fileContent);
				} else {
					FTPUtil.writeBytesToFTP(host, username, password, fsRoot, fileName, fileContent);
				}
			} else if (dmsProtocol == DMSProtocol.SFTP) {
				if (StringUtils.isNotBlank(privateKey)) {
					FTPUtil.writeBytesToFTP(host, port, username, password, privateKey, fsRoot, fileName, fileContent);
				} else {
					FTPUtil.writeBytesToFTP(Protocol.SFTP, host, port, username, password, fsRoot, fileName,
							fileContent);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			dmsQueue.setErrorCode("DMS99");
			dmsQueue.setErrorDesc(e.getMessage());
		} finally {
			setDmsStatus(dmsQueue, filePrefix, fileName);
		}
		logger.debug(Literal.LEAVING);
	}

	private void storeDocumentToS3(DMSQueue dmsQueue, String filePrefix, String fileName) {
		logger.debug(Literal.ENTERING);
		String bucketName = getFSRoot();

		try {
			AmazonS3Bucket bucket = null;
			if (host != null && port != null) {
				bucket = new AmazonS3Bucket(regionName, bucketName, accessKey, secretKey, host, Integer.valueOf(port));
			} else {
				bucket = new AmazonS3Bucket(regionName, bucketName, accessKey, secretKey);
			}
			bucket.setSseAlgorithm(sseAlgorithm);

			filePrefix = FilenameUtils.separatorsToUnix(filePrefix);
			if (filePrefix.startsWith("/") || filePrefix.startsWith(File.separator)) {
				filePrefix = filePrefix.substring(1);
			}
			if (!filePrefix.endsWith("/")) {
				filePrefix.concat("/");
			}
			String filePath = filePrefix.concat(fileName);

			bucket.putObject(dmsQueue.getDocImage(), filePath);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			dmsQueue.setErrorCode("DMS99");
			dmsQueue.setErrorDesc(e.getMessage());
		} finally {
			setDmsStatus(dmsQueue, filePrefix, fileName);
		}
		logger.debug(Literal.LEAVING);
	}

	private String getFSRoot() {
		logger.debug(Literal.ENTERING);
		String defaultDir = App.getProperty(DMSProperties.ROOT);

		if (defaultDir == null) {
			throw new InterfaceException("DMS01", DMSProperties.ROOT + " cannot be blank.");
		}
		logger.debug(Literal.LEAVING);
		return defaultDir;
	}

	public void setDocumentManagementService(ExternalDocumentFileService documentManagementService) {
		this.externalDocumentManagementService = documentManagementService;
	}

}
