package com.pennanttech.interfacebajaj.fileextract.service;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.SysParamUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class AbstractFileExtractService {
	private static final Logger logger = LogManager.getLogger(AbstractFileExtractService.class);

	public void backUpFile(File file) {
		try {
			File backup = new File(file.getParent() + "/BackUp");
			if (!backup.exists()) {
				backup.mkdir();
			}
			FileUtils.copyFile(file, new File(backup.getPath() + "/" + file.getName()));
			if (file.exists()) {
				file.delete();
			}
			file.deleteOnExit();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	public ArrayList<File> getFileList(String filePath) {
		ArrayList<File> fileList = new ArrayList<File>();
		File fs = new File(filePath);
		for (File file : fs.listFiles()) {
			if (file.getName().contains(".")) {
				fileList.add(file);
			}
		}
		return fileList;
	}

	public String getLoacation(String pathName) {
		return SysParamUtil.getValueAsString(pathName);
	}

}
