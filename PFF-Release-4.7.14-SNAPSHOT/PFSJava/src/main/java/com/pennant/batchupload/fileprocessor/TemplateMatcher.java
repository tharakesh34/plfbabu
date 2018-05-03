package com.pennant.batchupload.fileprocessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.pennant.batchupload.customexception.ValidationException;
import com.pennanttech.batchupload.util.BatchProcessorUtil;

public class TemplateMatcher {
	private static final Logger logger = Logger.getLogger(TemplateMatcher.class);
	// Uploaded excel 
	private Workbook targetWorkbook = null; // Uploaded excel
	// main template, from resources/BatchUpload_Templates directory
	private Workbook sourceWorkbook = null; 
	 // based on sourceFileName pick excel file from resources/BatchUpload_Templates directory.
	private String sourceFileName = null;
	

	public TemplateMatcher(Workbook targetWorkbook, String sourceFileName) {
		super();
		this.targetWorkbook = targetWorkbook;
		this.sourceFileName = sourceFileName;
		
		inItSourceWorkbook();
	}

	/** to get  sourceWorkbook*/
	private void inItSourceWorkbook() {

		File file = getSourceExcelByusingUrl();
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			if (file.toString().toLowerCase().endsWith(".xls")){
				sourceWorkbook = new HSSFWorkbook(fis);
			} else {
				sourceWorkbook = new XSSFWorkbook(fis);
			}
		} catch (Exception e) {
			logger.error(BatchUploadProcessorConstatnt.EXCEPTION, e);
			throw new RuntimeException(BatchUploadProcessorConstatnt.INIT_SOURCE_WORKBOOK_EX_MSG);	
			}
	}

   /** to pick particular excel from  resources/BatchUpload_Templates directory.*/
 	private File getSourceExcelByusingUrl() {
 		
 		String fileName = sourceFileName.concat(".xls");

 		File batchUploadTemplates = new File(BatchUploadProcessorConstatnt.FILE_PATH);
		if (batchUploadTemplates.exists()) {
			batchUploadTemplates.delete();
		} 
		batchUploadTemplates.mkdirs();
 		InputStream inputStream = null;
		OutputStream outputStream = null;
		File file = new File(batchUploadTemplates.getPath().concat(fileName));
		try {
			file.createNewFile();
			inputStream = this.getClass().getClassLoader().getResourceAsStream(BatchUploadProcessorConstatnt.FILE_PATH+fileName);
			outputStream = new FileOutputStream(file);
			IOUtils.copy(inputStream, outputStream);
			inputStream.close();
			outputStream.close();
		}catch (NullPointerException ne) {
			logger.error(BatchUploadProcessorConstatnt.EXCEPTION, ne);
			throw new RuntimeException(BatchUploadProcessorConstatnt.TEMPLATE_FILE_NOT_FOUND);	
		} catch (Exception e) {
			logger.error(BatchUploadProcessorConstatnt.EXCEPTION, e);
			throw new RuntimeException(BatchUploadProcessorConstatnt.INIT_SOURCE_WORKBOOK_EX_MSG);	
		}
		return file;
	}
	
	/*Compare template with uploaded file*/
	public void doCompair() {

		int numberOfSourceSheets = sourceWorkbook.getNumberOfSheets();
		int numberOfTargetSheets = targetWorkbook.getNumberOfSheets();
		// number of sheet should not be less than template.
		if (numberOfSourceSheets > numberOfTargetSheets) { 
			throw new ValidationException(
					"Uploaded Excel Contains " + numberOfTargetSheets + " Sheets But Required " + numberOfSourceSheets);
		}
		for (int sourcIndex = 0; sourcIndex < numberOfSourceSheets; sourcIndex++) {
			String sourceSheetName = sourceWorkbook.getSheetName(sourcIndex);
			String targetSheetName = targetWorkbook.getSheetName(sourcIndex);
			if (!sourceSheetName.equals(targetSheetName)) {
				int position = sourcIndex+1;
				throw new ValidationException("Could Not Found "+ '"'+sourceSheetName+'"' + " Sheet at "+ position +" Position");
			}
			List<String> sourcekeys = BatchProcessorUtil.getAllKeysByIndex(sourceWorkbook, sourcIndex);
			List<String> targetkeys = BatchProcessorUtil.getAllKeysByIndex(targetWorkbook, sourcIndex);

			sourcekeys.removeAll(targetkeys);

			if (sourcekeys.size() > 0) {
				throw new ValidationException(sourcekeys + " keys are not found in " + targetSheetName + " sheet.");
			}
		}
	}
	

}
