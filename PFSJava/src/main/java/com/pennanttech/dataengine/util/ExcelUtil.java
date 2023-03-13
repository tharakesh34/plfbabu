package com.pennanttech.dataengine.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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
import org.zkoss.zul.Filedownload;

import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pff.file.UploadContants;

public class ExcelUtil {
	private ExcelUtil() {
		super();
	}

	public static void isValidFile(String fileName, int length, String regularExp) {
		if (StringUtils.trimToNull(fileName) == null) {
			throw new AppException("File is empty, please select a valid file and try again.");
		}

		if (StringUtils.trimToEmpty(fileName).length() > length) {
			throw new AppException(String.format(" %s file name should not exceed %s characters.", fileName, length));
		}

		if (!fileName.toString().matches(regularExp)) {
			throw new AppException(String.format(
					" %s file name should not contain special characters, Allowed special charaters are space,dot and underScore.",
					fileName));
		}
	}

	public static File writeFile(String uploadPath, String fileName, byte[] fileContent) {
		File folder = new File(uploadPath);

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
			throw new AppException(String.format("Unable to create a %s file in %s location", fileName, uploadPath), e);
		}

		return file;
	}

	public static Workbook getWorkBook(File file) {
		String fileName = file.getName();

		try (FileInputStream fis = new FileInputStream(file)) {
			if (fileName.toLowerCase().endsWith(".xls")) {
				return new HSSFWorkbook(fis);
			}

			return new XSSFWorkbook(fis);
		} catch (Exception e) {
			throw new AppException(String.format("Unable to load the %s file", fileName), e);
		}
	}

	public static List<String> getHeaders(Workbook workBook, int sheetIndex, int rowIndex) {
		List<String> rowValues = new ArrayList<>();
		Sheet sheet = workBook.getSheetAt(sheetIndex);

		Row row = sheet.getRow(rowIndex);

		FormulaEvaluator evaluator = getFormulaEvaluator(workBook);

		for (Cell cell : row) {
			evaluator.evaluate(cell);
			rowValues.add(new DataFormatter().formatCellValue(cell, evaluator).trim());
		}

		return rowValues;
	}

	public static void basicValidations(Workbook workBook, int sheetIndex, int minNoOfRows, int rowIndex,
			List<String> headers) {
		Sheet sheet = workBook.getSheetAt(sheetIndex);

		if (sheet.getPhysicalNumberOfRows() <= minNoOfRows) {
			throw new AppException("Uploaded File does not contains any Data, Please verify uploaded file.");
		}

		int maxRecords = UploadContants.EXCEL_MAX_ALLOWED_RECORDS;
		if (sheet.getPhysicalNumberOfRows() > maxRecords + 1) {
			throw new AppException(
					String.format("The number of records in the file should be less than or equal to %s", maxRecords));
		}

		List<String> fsHeaderKeys = getHeaders(workBook, sheetIndex, rowIndex);

		Set<String> headerSet = new HashSet<>();

		for (String key : fsHeaderKeys) {
			if (!headers.contains(key)) {
				throw new AppException("Uploaded File does not contains proper Data, Please verify uploaded file.");
			}

			if (!headerSet.add(key)) {
				throw new AppException(String.format("%s has duplicate reference in Header Sheet", key));
			}
		}
	}

	private static FormulaEvaluator getFormulaEvaluator(Workbook workBook) {
		if (workBook instanceof HSSFWorkbook) {
			return new HSSFFormulaEvaluator((HSSFWorkbook) workBook);
		} else {
			return new XSSFFormulaEvaluator((XSSFWorkbook) workBook);
		}
	}

	public static void backUpFile(File file) {
		File backupFile = new File(file.getParent() + "/BackUp");

		if (!backupFile.exists()) {
			backupFile.mkdir();
		}

		try {
			FileUtils.copyFile(file, new File(backupFile.getPath().concat(File.separator).concat(file.getName())));
		} catch (IOException e) {
			//
		}

		if (file.exists()) {
			if (!file.delete()) {
				file.deleteOnExit();
			}
		}
	}

	public static void downloadTemplate(String path, String templateName, DocType docType) {
		File file = new File(path.concat(File.separator).concat(templateName));

		try {
			Filedownload.save(file, docType.getContentType());
		} catch (FileNotFoundException e) {
			throw new AppException(
					String.format("%s template is not available in %s location, please contact system administrator",
							templateName, path));
		}
	}

	public static Workbook getExcelWriterBook(String fileName) {
		if (fileName.toLowerCase().endsWith(".xls")) {
			return new HSSFWorkbook();
		}

		return new XSSFWorkbook();
	}

	public static Sheet getExcelSheet(Workbook workbook, String sheetName) {
		String worksheet = StringUtils.trimToNull(sheetName);
		if (worksheet != null) {
			return workbook.createSheet(worksheet);
		}

		return workbook.createSheet();
	}

	public static void createHeader(Sheet sheet, List<String> excelHeaders, int headerIndex) {
		Row row = sheet.createRow(headerIndex);

		int index = -1;
		for (String header : excelHeaders) {
			Cell cell = row.createCell(++index);
			cell.setCellValue(header);
		}
	}

	/**
	 * Creates a new header row at the specified row number with the values.
	 * 
	 * @param sheet  The sheet within which the header row to be created.
	 * @param rowNum The row number the header row represents.
	 * @param values The array of header row values.
	 */
	public static void createRow(Sheet sheet, int rowNum, String... values) {
		// Create a new row within the sheet.
		Row row = sheet.createRow(rowNum);

		// Create new cells within the row for the values supplied.
		Cell cell;
		int columnNum = -1;

		for (String value : values) {
			cell = row.createCell(++columnNum);
			cell.setCellValue(value);
		}
	}

	public static void addCellValue(Row row, int valueIndex, String value) {
		Cell cell = row.createCell(++valueIndex);
		cell.setCellValue(value);
	}
}
