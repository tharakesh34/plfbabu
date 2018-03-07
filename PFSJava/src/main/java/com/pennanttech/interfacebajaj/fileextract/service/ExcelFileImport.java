package com.pennanttech.interfacebajaj.fileextract.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.zkoss.util.media.Media;

import com.pennant.backend.dao.finance.impl.UploadHeaderDAOImpl;

public class ExcelFileImport {

	private static Logger logger = Logger.getLogger(UploadHeaderDAOImpl.class);
	
	private String 		filePath = "";
	private Workbook 	workbook = null;
	private Media 		media    = null;
	private File 		file     = null;
	
	/**
	 * create the object using the Media and Upload location
	 * @param media
	 * @param filePath
	 */
	public ExcelFileImport (Media media, String filePath) {
		this.media = media;
		this.filePath = filePath;
	}

	/**
	 * this method will return the work book for Excel sheet
	 * @return
	 * @throws IOException
	 * @throws DataFormatException
	 */
	public Workbook writeFile() throws IOException, DataFormatException {
		logger.debug("Entering");
		
		File parent = new File(this.filePath);
		
		if (!parent.exists()) {
			parent.mkdirs();
		}
		
		this.file = new File(parent.getPath().concat(File.separator).concat(this.media.getName()));
		
		if (this.file.exists()) {
			this.file.delete();
		}
		
		this.file.createNewFile();
		FileUtils.writeByteArrayToFile(this.file, this.media.getByteData());
		
		try {
			
			FileInputStream fis = new FileInputStream(this.file);
			
			if (this.file.toString().toLowerCase().endsWith(".xls")) {
				this.workbook = new HSSFWorkbook(fis);
			} else {
				this.workbook = new XSSFWorkbook(fis);
			}
			
		} catch (Exception e) {
			logger.error(e);
			throw e;
		} 
		
		logger.debug("Leaving");
		
		return workbook;
	}

	/**
	 * Backup the file
	 * @throws IOException
	 */
	public void backUpFile() throws IOException {
		logger.debug("Entering");
		
		if (file != null) {
			
			File backupFile = new File(this.file.getParent() + "/BackUp");
			
			if (!backupFile.exists()) {
				backupFile.mkdir();
			}
			
			FileUtils.copyFile(this.file, new File(backupFile.getPath().concat(File.separator).concat(this.file.getName())));
			
			if (this.file.exists()) {
				if (!this.file.delete()) {
					this.file.deleteOnExit();
				}
			}
		}
		
		logger.debug("Leaving");
	}
}
