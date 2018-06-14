package com.pennanttech.pennapps.pff.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConvertCsvToJson {

	private Workbook			workbook				= null;
	private FormulaEvaluator	objFormulaEvaluator		= null;						// for cell value formating
	public static final String	SEPARETOR				= "_";
	private DataFormatter		objDefaultFormat		= new DataFormatter();		// for cell value formating
	public static final String	TRUE					= "true";
	public static final String	FALSE					= "false";
	public static final String	_ID						= "_id";
	public static final String	NA						= "NA";
	public static final String	A						= "A";
	public static final String	ROOTKEY					= "<ROOT>_id";
	public static final String	SAMPLE_DATE_FRMT		= "yyyy-MM-dd'T'hh:mm:ss";
	public static final String	SAMPLE_DATE_FRMT_OLD	= "dd-MMM-yyyy";
	public static final String	VAS						= "vas";
	public static final String	EXT_DETAILS				= "extendedDetails";

	public static void main(String args[]) {
		Pattern pattern = Pattern.compile(",");
		try {
			File file = new File("C:\\pennant\\PFF\\NIYOGIN_POSTINGS26022018173440.xlsx");
			BufferedReader in = new BufferedReader(new FileReader(file));
			ConvertCsvToJson convert = new ConvertCsvToJson();
			convert.doProcess(file, file.getName());
			
			/*
			 * List<PostingsDownload> players = in.lines().skip(1).map(line -> { String[] x = pattern.split(line); try {
			 * return new PostingsDownload(Long.parseLong(x[0]), x[1], x[2], x[3], x[4], x[5], new BigDecimal(x[6]),
			 * x[7]); } catch (NumberFormatException e) { // TODO Auto-generated catch block e.printStackTrace(); }
			 * return null; }) .collect(Collectors.toList()); ObjectMapper mapper = new ObjectMapper();
			 * mapper.enable(SerializationFeature.INDENT_OUTPUT); mapper.writeValue(System.out, players);
			 */
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * entry point of program, reading whole excel and calling other methods to prepare jsonObject.
	 * 
	 * 
	 */
	public JSONArray doProcess(File file, String sourceFileName) throws Exception {
		
		JSONArray jsonArray = new JSONArray();

		try {
		inIt(file);

		List<String> keys = getAllKeysByIndex(workbook, 0);
		Sheet sheet = workbook.getSheetAt(0);
		
		Iterator<Row> rows = sheet.iterator();
		while (rows.hasNext()) {
			JSONObject finalRequestJson = new JSONObject();
			JSONObject jsonForextendedField = new JSONObject();
			Row row = rows.next();
			int rowIndex = row.getRowNum();
			int cellIndex = 0;
			Iterator<Cell> cellIterator = row.cellIterator();
			// to check how many cells are blank in a row.
			List<Integer> emptyCellList = new ArrayList<Integer>();
			cellRenderer: while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				int columnIndex = cell.getColumnIndex() + 1;
				// skipping header and other column value which is not inside header
				if (rowIndex > 0 && columnIndex <= keys.size()) {
					// parent value shouldn't be blank in hierarchy.
					if (cell.toString().equals("") && keys.get(cellIndex).contains(SEPARETOR)) {
						finalRequestJson = new JSONObject(); // resting
						emptyCellList.add(1);
					} else { // parent key cell is not blank
						emptyCellList.add(0);
						objFormulaEvaluator.evaluate(cell);
						String cellValueStr = objDefaultFormat.formatCellValue(cell, objFormulaEvaluator);
						boolean isMappingFound = doCompare(keys.get(cellIndex),
								getValueByColumnType(cell, cellValueStr), finalRequestJson, jsonForextendedField);
						if (!isMappingFound) { // no mapping found for cell value
							break cellRenderer;
						}
						cellIndex++;
					}
				}
			}
			// checking finalRequestJson having value or not
			if (isJsonObjectValueEmpty(finalRequestJson)) {
				finalRequestJson = new JSONObject(); // resting
			}
			jsonArray.put(finalRequestJson);
			System.out.println("my final result = " + finalRequestJson);
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("my final result in Array = " + jsonArray);
		return jsonArray;
	}

	private void inIt(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			if (file.toString().toLowerCase().endsWith(".xls")) {
				workbook = new HSSFWorkbook(fis);
				objFormulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
			} else {
				workbook = new XSSFWorkbook();
				objFormulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
				workbook = new XSSFWorkbook(fis);
			}
		} catch (Exception e) {
			throw new RuntimeException("");
		}
	}

	/**
	 * return all keys of a sheet
	 * 
	 * @param sheetIndex
	 *            index of required sheet.
	 */
	private List<String> getAllKeysByIndex(Workbook workbook, int sheetIndex) {
		List<String> keys = new ArrayList<String>();
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		Row headings = sheet.getRow(0);
		if (headings == null) {
			//throw new ValidationException("");
		}
		for (Cell cell : headings) {
			if (cell.toString().trim().equals("")) {
				//throw new ValidationException("");
			}
			keys.add(cell.toString().replace("\"", "").trim());
		}
		return keys;
	}

	/** deciding cell type based on column format */
	public Object getValueByColumnType(Cell cell, String value) {
		Object result = null;
		if (value.equalsIgnoreCase(TRUE) || value.equalsIgnoreCase(FALSE)) {
			result = boolFormater(value);
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC && DateUtil.isCellDateFormatted(cell)) {
			result = dateFormater(cell.toString());
		} else {
			result = value;
		}
		return result;
	}

	/**
	 * Accepting single cell key and value and comparing with other sheet data
	 * 
	 * @param key
	 *            sheet key
	 * @param value
	 *            row value
	 * @param jsonForextendedField
	 * @param flag
	 * @finalRequestJson append prepared json to this.
	 */
	private boolean doCompare(String key, Object value, JSONObject finalRequestJson, JSONObject jsonForextendedField)
			throws Exception {
		int numberOfSheet = workbook.getNumberOfSheets();
		JSONObject nestedJSONObjects = new JSONObject();
		Map<String, String> keyTypeMap = new HashMap<>();
		boolean isMappingFound = false;
		for (int sheetIndex = 1; sheetIndex < numberOfSheet; sheetIndex++) {
			String sheetName = workbook.getSheetAt(sheetIndex).getSheetName().replace("\"", "");
			List<String> KeyList = getAllKeysByIndex(workbook, sheetIndex);
			// key is a array or object
			if (KeyList.contains(sheetName + _ID)) {
				keyTypeMap.put(sheetName, A);
			} else {
				keyTypeMap.put(sheetName, NA);
			}

			if (KeyList.contains(key) && key.contains("_")) {
				int keyIndex = KeyList.indexOf(key);
				List<Map<String, Object>> singleSheetMappedRows = getAllMappingRowsOfSheet(sheetIndex, keyIndex, value);

				if (!singleSheetMappedRows.isEmpty() && singleSheetMappedRows.size() > 0) {
					isMappingFound = true;
					if (ROOTKEY.equals(key)) { // if key is <ROOT>_id
						prepareJsonForParent(sheetName, singleSheetMappedRows, keyTypeMap.get(sheetName),
								finalRequestJson);
					} else {// if key is other than <ROOT>_id (EX:- "financeSchedule"_id)
						nestedJSONObjects = prepareNestedJSONObjects(sheetName, singleSheetMappedRows,
								nestedJSONObjects, keyTypeMap.get(sheetName));
					}
				}
				// multi sheet other than parent keys(EX: categoryCode, defaultBranch, baseCurrency in customer)
			} else if (!key.contains("_")) {
				isMappingFound = true;
				finalRequestJson.put(key, value);
			}
			// for tree structure object (only for vas->extendedDetails->extendedFields)
			if (sheetName.contains(".")) {
				String[] sName = sheetName.split("\\.");
				keyTypeMap.put(sName[2], A);
				sName[0] = VAS; // hard coded
				sName[1] = EXT_DETAILS; // hard coded
				JSONArray jarray = new JSONArray();
				List<Map<String, Object>> singleSheetMappedRows = getAllMappingRowsOfSheet(sheetIndex, 0, value);

				for (int i = sName.length - 1; i > 0; i--) {
					if (i == sName.length - 1) {
						JSONObject ForextendedField = new JSONObject();
						ForextendedField = prepareNestedJSONObjects(sName[i], singleSheetMappedRows, ForextendedField,
								keyTypeMap.get(sName[i]));
						jarray.put(ForextendedField);
					} else {
						jsonForextendedField.put(sName[i], jarray);
					}
				}
			}
		}
		if (!key.contains(SEPARETOR) && numberOfSheet == 1) { // for single sheet
			isMappingFound = true;
			finalRequestJson.put(key, value);
		}
		// Preparing json for other than <ROOT>_id(EX:finaceSchedule_id)
		if (!ROOTKEY.equals(key) && key.contains(SEPARETOR) && nestedJSONObjects.length() > 0) {
			finalRequestJson.put(key.split(SEPARETOR)[0], nestedJSONObjects);
		}
		return isMappingFound;
	}

	/** checking before calling api whether json object contains any value or not */
	private boolean isJsonObjectValueEmpty(JSONObject finalRequestJson) {
		Iterator<?> keys = finalRequestJson.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			String value;
			try {
				value = String.valueOf(finalRequestJson.get(key));
				if (value != null && !"".equals(value.trim())) {
					return false;
				}
			} catch (JSONException e) {
			}
		}
		return true;
	}

	/** util method convert String to boolean */
	private boolean boolFormater(String input) {
		if (input.equalsIgnoreCase(TRUE)) {
			return Boolean.valueOf(TRUE);
		} else {
			return Boolean.valueOf(FALSE);
		}
	}

	/**
	 * formating date field
	 * 
	 * @param date
	 * 
	 * @return String
	 */
	public static String dateFormater(String date) {
		Date date1 = null;
		String formattedDate = "";
		try {
			DateFormat originalFormat = new SimpleDateFormat(SAMPLE_DATE_FRMT_OLD, Locale.ENGLISH);
			DateFormat targetFormat = new SimpleDateFormat(SAMPLE_DATE_FRMT);
			date1 = originalFormat.parse(date);
			formattedDate = targetFormat.format(date1);
		} catch (ParseException e) {

		}
		return formattedDate;
	}

	/**
	 * method will return list of all mapping values
	 * 
	 * @param sheetIndex
	 *            index of the sheet
	 * @param keyIndex
	 *            index of the key
	 * 
	 * @return List List of mapping values.
	 */
	public List<Map<String, Object>> getAllMappingRowsOfSheet(int sheetIndex, int keyIndex, Object value) {
		List<Map<String, Object>> allMappedRowsOfSheet = new ArrayList<>();
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		Iterator<Row> rows = sheet.iterator();
		List<String> keyList = getAllKeysByIndex(workbook, sheetIndex);
		while (rows.hasNext()) {
			Row row = rows.next();
			if (row != null && row.getRowNum() > 0) {
				for (Cell cell : row) {// cell.
					int columnIndex = cell.getColumnIndex() + 1;
					if (columnIndex <= keyList.size()) {
						objFormulaEvaluator.evaluate(cell);
						String cellString = objDefaultFormat.formatCellValue(cell, objFormulaEvaluator).trim();
						if (cell.getColumnIndex() == keyIndex && cellString.equals(value.toString().trim())) {
							Map<String, Object> rowMap = new HashMap<>();

							for (int j = 0; j < keyList.size(); j++) {
								if (!keyList.get(j).toString().contains("_")) {
									objFormulaEvaluator.evaluate(row.getCell(j));
									String cellValueStr = objDefaultFormat.formatCellValue(row.getCell(j),
											objFormulaEvaluator);
									rowMap.put(keyList.get(j), getValueByColumnType(row.getCell(j), cellValueStr));
								}
							}
							if (!rowMap.isEmpty() && rowMap.size() > 0) {
								allMappedRowsOfSheet.add(rowMap);
							}
						}
					}
				}
			}
		}

		return allMappedRowsOfSheet;
	}

	/** Prepare jsonObject for column <ROOT>_id */
	private void prepareJsonForParent(String key, List<Map<String, Object>> singleSheetMappedRows, String originalKey,
			JSONObject finalRequestJson) {
		try {
			if (originalKey.equals(NA)) { // its a jsonObject
				finalRequestJson.put(key, listOfMapToJson(singleSheetMappedRows));
			} else {// its a jsonArray
				finalRequestJson.put(key, singleSheetMappedRows);
			}
		} catch (Exception e) {

		}
	}

	/**
	 * util method convert list to json
	 * 
	 * @param prepairedLsitOfmap
	 *            mapped list
	 * @return JSONObject
	 */
	private JSONObject listOfMapToJson(List<Map<String, Object>> prepairedLsitOfmap) {
		JSONObject jsonObject = new JSONObject();
		for (Map<String, Object> map : prepairedLsitOfmap) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				try {
					jsonObject.put(entry.getKey(), entry.getValue());
				} catch (JSONException e) {

				}
			}
		}
		return jsonObject;
	}

	/** Prepare jsonObject for column other than <ROOT>_id (EX:finaceSchedule_id) */
	private JSONObject prepareNestedJSONObjects(String key, List<Map<String, Object>> singleSheetMappedRows,
			JSONObject nestedJSONObjects, String originalKey) throws Exception {
		if (originalKey.equals(NA)) { // its a json object
			nestedJSONObjects.put(key, listOfMapToJson(singleSheetMappedRows));
		} else { // its a jsonArray
			nestedJSONObjects.put(key, singleSheetMappedRows);
		}
		return nestedJSONObjects;
	}
}
