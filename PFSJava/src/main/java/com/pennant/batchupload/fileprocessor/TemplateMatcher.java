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
	 // based on url pick excel file from resources/BatchUpload_Templates directory.
	private String apiUrl = null;
	

	public TemplateMatcher(Workbook targetWorkbook, String apiUrl) {
		super();
		this.targetWorkbook = targetWorkbook;
		this.apiUrl = apiUrl;
		
		inItSourceWorkbook();
	}

	/** to get  sourceWorkbook*/
	private void inItSourceWorkbook() {

		File file = getExceluByusingUrl();
		
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
 	private File getExceluByusingUrl() {
 		
 		String fileName = "";
 		final String filePath = "BatchUploadTemplates/";
 		 for (ApiType excelType : ApiType.values()){
 			 if(apiUrl.contains(excelType.toString())){
 				fileName = excelType.toString().concat(".xls");
 				break;
 			 }
 		 }
 		 File batchUploadTemplates = new File(filePath);
		if (batchUploadTemplates.exists()) {
			batchUploadTemplates.delete();
		} 
		batchUploadTemplates.mkdirs();
 		InputStream inputStream = null;
		OutputStream outputStream = null;
		File file = new File(batchUploadTemplates.getPath().concat(fileName));
		try {
			file.createNewFile();
			inputStream = this.getClass().getClassLoader().getResourceAsStream(filePath+fileName);
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

		if (numberOfSourceSheets > numberOfTargetSheets) { // number of sheet should not be less than template.
			throw new ValidationException(
					"Number of sheets contains " + numberOfTargetSheets + " required " + numberOfSourceSheets);
		}

		for (int sourcIndex = 0; sourcIndex < numberOfSourceSheets; sourcIndex++) {
			String sourceSheetName = sourceWorkbook.getSheetName(sourcIndex);
			String targetSheetName = targetWorkbook.getSheetName(sourcIndex);
			if (!sourceSheetName.equals(targetSheetName)) {
				throw new ValidationException(sourceSheetName + BatchUploadProcessorConstatnt.INVALID_SHEET_MSG);
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
