package com.pennant.pff.file.upload.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;

public class AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(AUploadServiceImpl.class);

	public Workbook getWorkBook(String uploadPath, String fileName, byte[] fileContent) {
		logger.debug(Literal.ENTERING);

		File folder = new File(App.getResourcePath(uploadPath));

		if (!folder.exists()) {
			folder.mkdirs();
		}

		File file = new File(folder.getPath().concat(File.separator).concat(fileName));

		if (file.exists()) {
			file.delete();
		}

		try {
			file.createNewFile();
			FileUtils.writeByteArrayToFile(file, fileContent);
		} catch (IOException e) {
			logger.error(Literal.EXCEPTION, e);
			throw new AppException("", e);
		}

		try (FileInputStream fis = new FileInputStream(file)) {
			logger.debug(Literal.LEAVING);
			if (fileName.toLowerCase().endsWith(".xls")) {
				return new HSSFWorkbook(fis);
			}

			return new XSSFWorkbook(fis);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new AppException("", e);
		}
	}

}
