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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.google.common.io.Files;
import com.pennant.app.util.SysParamUtil;
import com.pennanttech.external.EODExtractionsHook;
import com.pennanttech.external.app.config.dao.ExternalDao;
import com.pennanttech.external.app.config.model.FileInterfaceConfig;
import com.pennanttech.external.app.constants.ExtIntfConfigConstants;
import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.app.util.FileInterfaceConfigUtil;
import com.pennanttech.external.app.util.FileTransferUtil;
import com.pennanttech.external.ucic.service.ExtUcicDataExtractor;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;

public class EODExtractionsService implements EODExtractionsHook, InterfaceConstants, ExtIntfConfigConstants {

	private static final Logger logger = LogManager.getLogger(EODExtractionsService.class);

	private ExtUcicDataExtractor extUcicExtractData;

	private ExternalDao externalDao;

	@Override
	public void processUCICExtraction() {
		logger.debug(Literal.ENTERING);

		if (extUcicExtractData != null) {
			extUcicExtractData.processUCIC();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void processALMReportExtarction() {
		externalDao.executeSP(SP_ALM_REPORT);
	}

	@Override
	public void processBaselOneExtarction() {
		externalDao.executeSP(SP_BASEL_ONE);
	}

	@Override
	public void processExtRBIADFExtarction() {
		externalDao.executeSP(SP_RBIADF);

	}

	@Override
	public void processFinconGLExtraction() {
		Date appDate = SysParamUtil.getAppDate();
		MapSqlParameterSource appDateAsinPram = new MapSqlParameterSource();
		appDateAsinPram.addValue("exe_postdate", appDate);
		String finconSPStatus = externalDao.executeSP(SP_FINCON_GL, appDateAsinPram);

		if (!"SUCCESS".equals(finconSPStatus)) {
			logger.debug("EXT_FINCONGL: SP extraction failed.");
			return;
		}

		// file writing
		FileInterfaceConfig finconGLConfig = FileInterfaceConfigUtil.getFIConfig(CONFIG_FINCONGL);

		String fileName = getFileName(appDate, finconGLConfig);

		MapSqlParameterSource inPrams = new MapSqlParameterSource();
		inPrams.addValue("aFileName", fileName);
		String status = externalDao.executeSP(SP_FINCON_WRITE_FILE, inPrams);

		if (!"SUCCESS".equals(status)) {
			logger.debug("EXT_FINCONGL: Fincon File writing SP failed.");
			return;
		}

		// check the location of the app server

		String baseFilePath = App.getResourcePath(finconGLConfig.getFileLocation());

		if (baseFilePath == null || "".equals(baseFilePath)) {
			return;
		}

		// Fetch request file from DB Server location to local and the upload it in client SFTP
		FileInterfaceConfig dbServerConfig = FileInterfaceConfigUtil.getFIConfig("PLF_DB_SERVER");

		if (dbServerConfig == null) {
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

	private String getFileName(Date appDate, FileInterfaceConfig finconGLConfig) {
		long fileSeq = externalDao.getSeqNumber(SEQ_FINCON_GL);

		String fileSeqName = StringUtils.leftPad(String.valueOf(fileSeq), 4, "0");
		String fileName = finconGLConfig.getFilePrepend()
				+ new SimpleDateFormat(finconGLConfig.getDateFormat()).format(appDate) + fileSeqName
				+ finconGLConfig.getFileExtension();
		return fileName;
	}

	private void fileBackup(FileInterfaceConfig finconGLconf, File mainFile) {
		logger.debug(Literal.ENTERING);

		String localBkpLocation = finconGLconf.getFileLocalBackupLocation();
		if (localBkpLocation == null || "".equals(localBkpLocation)) {
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
	@Qualifier(value = "extUcicExtractData")
	public void setExtUcicExtractData(ExtUcicDataExtractor extUcicExtractData) {
		this.extUcicExtractData = extUcicExtractData;
	}

	public void setExternalDao(ExternalDao externalDao) {
		this.externalDao = externalDao;
	}

}
