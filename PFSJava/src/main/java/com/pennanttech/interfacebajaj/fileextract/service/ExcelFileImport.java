package com.pennanttech.interfacebajaj.fileextract.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.zkoss.util.media.Media;

import com.pennant.backend.dao.finance.impl.UploadHeaderDAOImpl;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExcelFileImport {

	private static Logger logger = LogManager.getLogger(UploadHeaderDAOImpl.class);

	private String filePath = "";
	private Workbook workbook = null;
	private Media media = null;
	private File file = null;
	private DataFormatter objDefaultFormat = new DataFormatter();// for cell value formating
	private FormulaEvaluator formulaEvaluator = null; // for cell value formating

	/**
	 * create the object using the Media and Upload location
	 * 
	 * @param media
	 * @param filePath
	 */
	public ExcelFileImport(Media media, String filePath) {
		this.media = media;
		this.filePath = filePath;
	}

	/**
	 * this method will return the work book for Excel sheet
	 * 
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

			if (this.workbook instanceof HSSFWorkbook) {
				this.formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) this.workbook);
			} else if (this.workbook instanceof XSSFWorkbook) {
				this.formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) this.workbook);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

		logger.debug("Leaving");

		return workbook;
	}

	/**
	 * Backup the file
	 * 
	 * @throws IOException
	 */
	public void backUpFile() throws IOException {
		logger.debug("Entering");

		if (file != null) {

			File backupFile = new File(this.file.getParent() + "/BackUp");

			if (!backupFile.exists()) {
				backupFile.mkdir();
			}

			FileUtils.copyFile(this.file,
					new File(backupFile.getPath().concat(File.separator).concat(this.file.getName())));

			if (this.file.exists()) {
				if (!this.file.delete()) {
					this.file.deleteOnExit();
				}
			}
		}

		logger.debug("Leaving");
	}

	public List<String> getRowValuesByIndex(Workbook workbook, int sheetIndex, int rowindex, int columns) {

		List<String> rowValues = new ArrayList<String>();
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		Row row = sheet.getRow(rowindex);

		for (int i = 0; i < columns; i++) {
			Cell cell = row.getCell(i);
			String cellValue = this.objDefaultFormat.formatCellValue(cell, this.formulaEvaluator);
			rowValues.add(cellValue.trim());
		}

		return rowValues;
	}

}
