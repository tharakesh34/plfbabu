package com.pennanttech.external.extractions.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.common.io.Files;
import com.pennant.app.util.SysParamUtil;
import com.pennanttech.external.EODExtractionsHook;
import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.FileInterfaceConfigUtil;
import com.pennanttech.external.app.util.FileTransferUtil;
import com.pennanttech.external.extractions.dao.ExtExtractionDao;
import com.pennanttech.external.ucic.service.ExtUcicDataExtractor;
import com.pennanttech.external.ucic.service.ExtUcicRequestFile;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class EODExtractionsService implements EODExtractionsHook, InterfaceConstants, ExtIntfConfigConstants {

	private static final Logger logger = LogManager.getLogger(EODExtractionsService.class);

	private FileInterfaceConfig finconGLConfig;

	private ExtExtractionDao extExtractionDao;

	private ExtUcicDataExtractor extUcicExtractData;

	private ExtUcicRequestFile extUcicRequestFile;

	@Override
	public void processFinconGLExtraction() {
		Date appDate = SysParamUtil.getAppDate();
		String spCall = "{ call " + SP_FINCON_GL + "(?) }";
		String finconSPStatus = extExtractionDao.executeSp(spCall, appDate);
		if (!"SUCCESS".equals(finconSPStatus)) {
			logger.debug("EXT_FINCONGL: SP extraction failed.");
			return;
		}

		// file writing
		finconGLConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_FINCONGL);

		long fileSeq = extExtractionDao.getSeqNumber(SEQ_FINCON_GL);

		String fileSeqName = StringUtils.leftPad(String.valueOf(fileSeq), 4, "0");
		String fileName = finconGLConfig.getFilePrepend()
				+ new SimpleDateFormat(finconGLConfig.getDateFormat()).format(appDate) + fileSeqName
				+ finconGLConfig.getFileExtension();

		// write the file in DB server
		String spQuery = "{ call " + SP_FINCON_WRITE_FILE + "(?) }";
		String status = extExtractionDao.executeSp(spQuery, fileName);

		if (!"SUCCESS".equals(status)) {
			logger.debug("EXT_FINCONGL: Fincon File writing SP failed.");
			return;
		}

		// check the location of the app server

		String baseFilePath = App.getResourcePath(finconGLConfig.getFileLocation());

		if (baseFilePath == null || "".equals(baseFilePath)) {
			logger.debug("Ext_Warning: Local file path not found for config. So returning.");
			return;
		}

		// Fetch request file from DB Server location to local and the upload it in client SFTP
		FileInterfaceConfig dbServerConfig = FileInterfaceConfigUtil.getFIConfig("PLF_DB_SERVER");

		if (dbServerConfig == null) {
			logger.debug("Ext_Warning: DB Server config not found. So returning.");
			return;
		}

		FileTransferUtil fileTransferUtil = new FileTransferUtil(dbServerConfig);
		// Now get remote file to local base location using SERVER config
		try {
			fileTransferUtil.downloadFromSFTP(fileName, baseFilePath);
		} catch (Exception e) {
			logger.debug("Unable to download file from DB Server to local path.");
			return;
		}

		if ("Y".equals(finconGLConfig.getFileTransfer())) {
			// Now upload file to SFTP of client location as per configuration
			File mainFile = new File(baseFilePath + File.separator + fileName);
			FileTransferUtil fTransferUtil = new FileTransferUtil(finconGLConfig);
			fTransferUtil.uploadToSFTP(baseFilePath, fileName);
			fileBackup(finconGLConfig, mainFile);
			mainFile.delete();
		}

	}

	private void processUcicRequest() {
		logger.debug(Literal.ENTERING);
		String custDataExtrctstatus = "";
		try {
			if (extUcicExtractData != null) {
				custDataExtrctstatus = extUcicExtractData.extractCustomerData();
			}
			if (custDataExtrctstatus.equals("SUCCESS")) {
				Date appDate = SysParamUtil.getAppDate();
				if (extUcicRequestFile != null) {
					extUcicRequestFile.processUcicRequestFile(appDate);
				}
			} else {
				logger.debug("Customers data extraction Unsuccessful :" + custDataExtrctstatus);
			}

		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void fileBackup(FileInterfaceConfig finconGLconf, File mainFile) {
		logger.debug(Literal.ENTERING);

		String localBkpLocation = finconGLconf.getFileLocalBackupLocation();
		if (localBkpLocation == null || "".equals(localBkpLocation)) {
			logger.debug("EXT_FINCONGL: Local backup location not configured, so returning.");
			return;
		}

		String localBackupLocation = App.getResourcePath(finconGLconf.getFileLocalBackupLocation());

		File mainFileBkp = new File(localBackupLocation + File.separator + mainFile.getName());

		try {
			Files.copy(mainFile, mainFileBkp);
		} catch (IOException e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug("EXT_FINCONGL:MainFile  backup Successful");
		logger.debug(Literal.LEAVING);
	}

	@Autowired(required = false)
	@Qualifier(value = "extExtractionDao")
	public void setExtExtractionDao(ExtExtractionDao extExtractionDao) {
		this.extExtractionDao = extExtractionDao;
	}

	@Autowired(required = false)
	@Qualifier(value = "extUcicExtractData")
	public void setExtUcicExtractData(ExtUcicDataExtractor extUcicExtractData) {
		this.extUcicExtractData = extUcicExtractData;
	}

	@Autowired(required = false)
	@Qualifier(value = "extUcicRequestFile")
	public void setExtUcicRequestFile(ExtUcicRequestFile extUcicRequestFile) {
		this.extUcicRequestFile = extUcicRequestFile;
	}

	@Override
	public void processUCICExtraction() {
		processUcicRequest();
	}

	@Override
	public void processBaselOneExtarction() {
		String spCall = "{ call " + SP_BASEL_ONE + "() }";
		extExtractionDao.executeSp(spCall);
	}

	@Override
	public void processALMReportExtarction() {
		String spCall = "{ call " + SP_ALM_REPORT + "() }";
		extExtractionDao.executeSp(spCall);
	}

	@Override
	public void processExtRBIADFExtarction() {
		String spCall = "{ call " + SP_RBIADF + "() }";
		extExtractionDao.executeSp(spCall);

	}

}
