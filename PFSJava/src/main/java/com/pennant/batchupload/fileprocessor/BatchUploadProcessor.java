package com.pennant.batchupload.fileprocessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.util.PennantConstants;
import com.pennant.batchupload.customexception.ValidationException;
import com.pennant.batchupload.model.FaultDetails;
import com.pennanttech.batchupload.util.BatchProcessorUtil;

public class BatchUploadProcessor {
	private static final Logger logger = LogManager.getLogger(BatchUploadProcessor.class);

	private String authorization;
	private Workbook workbook = null;
	private File file;
	private String apiUrl = null;
	private String extraHeader = null;
	private String sourceFileName = null;
	private FormulaEvaluator objFormulaEvaluator = null; // for cell value formating
	private DataFormatter objDefaultFormat = new DataFormatter();// for cell value formating
	private String entityId = null;
	private long userId = Long.MIN_VALUE;

	public BatchUploadProcessor(File file, String authorization, String apiUrl, String extraHeader,
			String sourceFileName, String entityId, long userId) {
		this.file = file;
		this.authorization = authorization;
		this.apiUrl = apiUrl;
		this.extraHeader = extraHeader; // it could be blank.
		this.sourceFileName = sourceFileName;
		this.entityId = entityId;
		this.userId = userId;
		inIt(file);
	}

	private void inIt(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			if (file.toString().toLowerCase().endsWith(".xls")) {
				workbook = new HSSFWorkbook(fis);
			} else {
				workbook = new XSSFWorkbook(fis);
			}
		} catch (Exception e) {
			logger.error(BatchUploadProcessorConstatnt.EXCEPTION, e);
			throw new RuntimeException(BatchUploadProcessorConstatnt.INIT_WORKBOOK_EX_MSG);
		}
	}

	/**
	 * entry point of program, reading whole excel and calling other methods to prepare jsonObject.
	 * 
	 * @return String
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public void process() throws JsonParseException, JsonMappingException, IOException {
		logger.debug("Entering");
		// sanity check
		validateFile();
		// deep check
		TemplateMatcher matcher = new TemplateMatcher(workbook, sourceFileName);
		matcher.doCompair();

		List<String> keys = BatchProcessorUtil.getAllKeysByIndex(workbook, 0);
		int parentKeyCount = getNumberOfParentKey(keys);
		Sheet sheet = workbook.getSheetAt(0);

		objFormulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
		Iterator<Row> rows = sheet.iterator();
		List<Object> returnedValue = writeHeader(0);
		Sheet writebleSheet = (Sheet) returnedValue.get(0);
		int lastIndex = (int) returnedValue.get(1);
		while (rows.hasNext()) {
			JSONObject finalRequestJson = new JSONObject();
			JSONObject jsonForextendedField = new JSONObject();
			Row row = rows.next();
			int rowIndex = row.getRowNum();
			Calendar calendar = Calendar.getInstance();

			String messageId = userId + "-" + calendar.getTimeInMillis() + "/" + rowIndex;
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
					if (cell.toString().equals("")
							&& keys.get(cellIndex).contains(BatchUploadProcessorConstatnt.SEPARETOR)) {
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
			// everything is fine just call api and write response back.
			if (finalRequestJson.length() > 0 && cellIndex >= parentKeyCount) {
				if (jsonForextendedField.length() > 0
						&& finalRequestJson.has(BatchUploadProcessorConstatnt.FINANCE_SCHEDULE)) {
					if (finalRequestJson.getJSONObject(BatchUploadProcessorConstatnt.FINANCE_SCHEDULE)
							.has(BatchUploadProcessorConstatnt.VAS)) {
						JSONArray vasArray = finalRequestJson
								.getJSONObject(BatchUploadProcessorConstatnt.FINANCE_SCHEDULE)
								.getJSONArray(BatchUploadProcessorConstatnt.VAS);
						vasArray.put(jsonForextendedField);
					}
				}
				callApiNWriteToSheet(finalRequestJson, writebleSheet, messageId, lastIndex);
				// No mapping found for given value or parent key is/are empty.
			} else if (cellIndex < parentKeyCount && rowIndex > 0) {
				String[] responseText = checkResponse(parentKeyCount, emptyCellList);
				String[] response = new String[4];
				response[0] = responseText[0];
				response[1] = responseText[1];
				response[2] = null;
				response[3] = messageId;
				write(response, writebleSheet, lastIndex);
			}
		}
		logger.debug("Leaving");
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
				logger.error(BatchUploadProcessorConstatnt.EXCEPTION, e);
			}
		}
		return true;
	}

	/** Deciding what should be the response (Blank Or Hard code text) */
	private String[] checkResponse(int parentKeyCount, List<Integer> emptyCellList) {
		logger.debug("Entering");
		String[] response = new String[2];
		response[0] = BatchUploadProcessorConstatnt.CODE_10101;
		response[1] = BatchUploadProcessorConstatnt.NO_MAP_FOUND_MSG;
		if (emptyCellList.size() > 0) {
			// checking occurrences of 1(number of empty cell in a row).
			int occurrences = Collections.frequency(emptyCellList, 1);
			// if all parent key is blank then ignore
			if (parentKeyCount == occurrences) {
				response[0] = null;
				response[1] = null;
				return response;
			}
		} else { // if value found at out of parent key index
			response[0] = null;
			response[1] = null;
			return response;
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * method will return number of parent(key contains '_' is a parent key) key of a sheet
	 * 
	 * @param keyList
	 * 
	 * @return numberOfParentkey
	 */
	private int getNumberOfParentKey(List<String> keyList) {
		int parentKeyCount = 0;
		for (String key : keyList) {
			if (key.contains(BatchUploadProcessorConstatnt.SEPARETOR)) {
				parentKeyCount++;
			}
		}
		return parentKeyCount;
	}

	/**
	 * Accepting single cell key and value and comparing with other sheet data
	 * 
	 * @param key                  sheet key
	 * @param value                row value
	 * @param jsonForextendedField
	 * @param flag
	 * @finalRequestJson append prepared json to this.
	 */
	private boolean doCompare(String key, Object value, JSONObject finalRequestJson, JSONObject jsonForextendedField) {
		logger.debug("Entering");
		int numberOfSheet = workbook.getNumberOfSheets();
		JSONObject nestedJSONObjects = new JSONObject();
		Map<String, String> keyTypeMap = new HashMap<>();
		boolean isMappingFound = false;
		for (int sheetIndex = 1; sheetIndex < numberOfSheet; sheetIndex++) {
			String sheetName = workbook.getSheetAt(sheetIndex).getSheetName().replace("\"", "");
			List<String> KeyList = BatchProcessorUtil.getAllKeysByIndex(workbook, sheetIndex);
			// key is a array or object
			if (KeyList.contains(sheetName + BatchUploadProcessorConstatnt._ID)) {
				keyTypeMap.put(sheetName, BatchUploadProcessorConstatnt.A);
			} else {
				keyTypeMap.put(sheetName, BatchUploadProcessorConstatnt.NA);
			}

			if (KeyList.contains(key) && key.contains("_")) {
				int keyIndex = KeyList.indexOf(key);
				List<Map<String, Object>> singleSheetMappedRows = getAllMappingRowsOfSheet(sheetIndex, keyIndex, value);

				if (!singleSheetMappedRows.isEmpty() && singleSheetMappedRows.size() > 0) {
					isMappingFound = true;
					if (BatchUploadProcessorConstatnt.ROOTKEY.equals(key)) { // if key is <ROOT>_id
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
				keyTypeMap.put(sName[2], BatchUploadProcessorConstatnt.A);
				sName[0] = BatchUploadProcessorConstatnt.VAS; // hard coded
				sName[1] = BatchUploadProcessorConstatnt.EXT_DETAILS; // hard coded
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
		if (!key.contains(BatchUploadProcessorConstatnt.SEPARETOR) && numberOfSheet == 1) { // for single sheet
			isMappingFound = true;
			finalRequestJson.put(key, value);
		}
		// Preparing json for other than <ROOT>_id(EX:finaceSchedule_id)
		if (!BatchUploadProcessorConstatnt.ROOTKEY.equals(key) && key.contains(BatchUploadProcessorConstatnt.SEPARETOR)
				&& nestedJSONObjects.length() > 0) {
			finalRequestJson.put(key.split(BatchUploadProcessorConstatnt.SEPARETOR)[0], nestedJSONObjects);
		}
		logger.debug("Leaving");
		return isMappingFound;
	}

	/** Prepare jsonObject for column <ROOT>_id */
	private void prepareJsonForParent(String key, List<Map<String, Object>> singleSheetMappedRows, String originalKey,
			JSONObject finalRequestJson) {
		try {
			if (originalKey.equals(BatchUploadProcessorConstatnt.NA)) { // its a jsonObject
				finalRequestJson.put(key, listOfMapToJson(singleSheetMappedRows));
			} else {// its a jsonArray
				finalRequestJson.put(key, singleSheetMappedRows);
			}
		} catch (Exception e) {
			logger.error(BatchUploadProcessorConstatnt.EXCEPTION, e);
		}
	}

	/** Prepare jsonObject for column other than <ROOT>_id (EX:finaceSchedule_id) */
	private JSONObject prepareNestedJSONObjects(String key, List<Map<String, Object>> singleSheetMappedRows,
			JSONObject nestedJSONObjects, String originalKey) {
		if (originalKey.equals(BatchUploadProcessorConstatnt.NA)) { // its a json object
			nestedJSONObjects.put(key, listOfMapToJson(singleSheetMappedRows));
		} else { // its a jsonArray
			nestedJSONObjects.put(key, singleSheetMappedRows);
		}
		return nestedJSONObjects;
	}

	/**
	 * method will return list of all mapping values
	 * 
	 * @param sheetIndex index of the sheet
	 * @param keyIndex   index of the key
	 * 
	 * @return List List of mapping values.
	 */
	public List<Map<String, Object>> getAllMappingRowsOfSheet(int sheetIndex, int keyIndex, Object value) {
		logger.debug("Entering");
		List<Map<String, Object>> allMappedRowsOfSheet = new ArrayList<>();
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		Iterator<Row> rows = sheet.iterator();
		List<String> keyList = BatchProcessorUtil.getAllKeysByIndex(workbook, sheetIndex);
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
								if (!keyList.get(j).contains("_")) {
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
		logger.debug("Leaving");
		return allMappedRowsOfSheet;
	}

	/**
	 * util method convert list to json
	 * 
	 * @param prepairedLsitOfmap mapped list
	 * @return JSONObject
	 */
	private JSONObject listOfMapToJson(List<Map<String, Object>> prepairedLsitOfmap) {
		JSONObject jsonObject = new JSONObject();
		for (Map<String, Object> map : prepairedLsitOfmap) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				try {
					jsonObject.put(entry.getKey(), entry.getValue());
				} catch (JSONException e) {
					logger.error(BatchUploadProcessorConstatnt.EXCEPTION, e);
				}
			}
		}
		return jsonObject;
	}

	/** this method will validate uploaded file if Validation fails throws ValidationException */
	public void validateFile() {
		Sheet sheet = workbook.getSheetAt(0);
		if (!sheet.getSheetName().equals("<ROOT>")) {
			throw new ValidationException(BatchUploadProcessorConstatnt.INVALID_FILE_TEMPLATE_MSG);
		}
		if (workbook.getNumberOfSheets() > 1) {
			Row headings = sheet.getRow(0);
			// sheet.getRow(0).getPhysicalNumberOfCells();
			Cell cell = headings.getCell(0);
			if (!BatchUploadProcessorConstatnt.ROOTKEY.equals(cell.getStringCellValue().trim())) {
				throw new ValidationException(BatchUploadProcessorConstatnt.INVALID_FILE_TEMPLATE_MSG);
			}
		}
		List<String> KeyList = BatchProcessorUtil.getAllKeysByIndex(workbook, 0);
		if (KeyList.isEmpty() && KeyList.size() == 0) { // there is no keys in <ROOT> sheet.
			throw new ValidationException(BatchUploadProcessorConstatnt.PARENT_KEYS_FOUND);
		}
		if (getNumberOfRowsInRoot(KeyList.size()) == 0) {// there is no row.
			throw new ValidationException(BatchUploadProcessorConstatnt.SHEET_SHOULD_NOT_EMPTY);
		}
	}

	/**
	 * method to check ROOT sheet(only value rows) is empty or not
	 * 
	 * @param keyList
	 */
	public int getNumberOfRowsInRoot(int keyListSize) {
		Sheet sheet = workbook.getSheetAt(0);
		int rowCount = 0;
		Iterator<Row> rows = sheet.iterator();
		while (rows.hasNext()) {
			Row row = rows.next();

			if (row == null) {
				continue;
			}

			int rowIndex = row.getRowNum();
			if (row != null && rowIndex > 0) {
				for (Cell cell : row) {
					if (cell == null) {
						continue;
					}

					int columnIndex = cell.getColumnIndex() + 1;
					if (cell != null && !cell.toString().trim().equals("") && columnIndex <= keyListSize) {
						rowCount++;
						return rowCount;
					}
				}
			}
		}
		return rowCount;
	}

	/** deciding cell type based on column format */
	public Object getValueByColumnType(Cell cell, String value) {
		Object result = null;
		if (value.equalsIgnoreCase(BatchUploadProcessorConstatnt.TRUE)
				|| value.equalsIgnoreCase(BatchUploadProcessorConstatnt.FALSE)) {
			result = BatchProcessorUtil.boolFormater(value);
		} else if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
			result = BatchProcessorUtil.dateFormater(cell.toString());
		} else {
			result = value;
		}
		return result;
	}

	/**
	 * method will write header to sheet and return the same sheet for further process
	 * 
	 * @param sheetIndex
	 * @return list contains sheet and lastCellIndex
	 */
	private List<Object> writeHeader(int sheetIndex) {
		List<String> Headers = new ArrayList<String>();
		List<Object> responseList = new ArrayList<Object>(2);
		Headers.add(BatchUploadProcessorConstatnt.RESP_CODE);
		Headers.add(BatchUploadProcessorConstatnt.RESP_TEXT);
		if (extraHeader != null) {
			Headers.add(extraHeader);
		}
		CellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.GOLD.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		logger.info("Response writing to :: " + workbook.getSheetAt(sheetIndex).getSheetName());

		Sheet writebleSheet = workbook.getSheetAt(sheetIndex);
		Row row = workbook.getSheetAt(sheetIndex).getRow(0);
		int lastCellIndex = row.getLastCellNum();
		for (int i = 0; i < Headers.size(); i++) {
			Cell cell = row.createCell(lastCellIndex + (i + 1));
			cell.setCellValue(Headers.get(i));
			cell.setCellStyle(style);
		}
		responseList.add(writebleSheet);
		responseList.add(lastCellIndex);
		return responseList;
	}

	/**
	 * method will call appropriate api with given jsonobject and return response back to writing to excel
	 * 
	 * @param json          prepared jsonObject
	 * @param writebleSheet response will write to this field
	 * @param messageId     to pass as input header
	 * @param lastCellIndex cell index where the response will written
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	private synchronized void callApiNWriteToSheet(JSONObject json, Sheet writebleSheet, String messageId,
			int lastCellIndex) throws JsonParseException, JsonMappingException, IOException {
		if (json.length() > 0) {
			String[] response = callApi(json, messageId);
			write(response, writebleSheet, lastCellIndex);
		}
	}

	/**
	 * calling api with jsonobject and returning array as response
	 * 
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	private String[] callApi(JSONObject jsondata, String messageId)
			throws JsonParseException, JsonMappingException, IOException {
		logger.debug("API REQUEST :: " + jsondata.toString());
		String ReturnText = null;
		String ReturnCode = null;
		WebClient client = null;
		String extraHeaderValue = null;
		String[] responseArray = new String[4];
		String headerMessageId = null;
		try {
			client = getClient(apiUrl, messageId);
			Response response = client.post(jsondata.toString());
			String body = response.readEntity(String.class);
			headerMessageId = response.getHeaderString(BatchUploadProcessorConstatnt.MESSAGE_ID);
			if (headerMessageId == null && StringUtils.isBlank(body)) {
				throw new RuntimeException(BatchUploadProcessorConstatnt.UNABLE_TO_PROCESS);
			}
			logger.info("MESSAGEID :: " + headerMessageId + "  API RESPONSE :: " + body);

			if (response.getStatus() == 200 && body != null) {
				JSONObject parentBody = new JSONObject(body);
				if (StringUtils.isNotBlank(extraHeader)) {
					if (!parentBody.isNull(BatchUploadProcessorConstatnt.FIN_REFERENCE)) {
						extraHeaderValue = String.valueOf(parentBody.get(BatchUploadProcessorConstatnt.FIN_REFERENCE));
					} else if (!parentBody.isNull(BatchUploadProcessorConstatnt.MANDATE_ID)) {
						extraHeaderValue = String.valueOf(parentBody.get(BatchUploadProcessorConstatnt.MANDATE_ID));
					} else if (!parentBody.isNull(BatchUploadProcessorConstatnt.WORKFLOW_DESIGN_ID)) {
						extraHeaderValue = String
								.valueOf(parentBody.get(BatchUploadProcessorConstatnt.WORKFLOW_DESIGN_ID));
					} else if (!parentBody.isNull(BatchUploadProcessorConstatnt.LIMIT_Id)) {
						extraHeaderValue = String.valueOf(parentBody.getString(BatchUploadProcessorConstatnt.LIMIT_Id));
					}
				}
				parentBody = parentBody.getJSONObject(BatchUploadProcessorConstatnt.RETURN_STATUS);
				ReturnText = parentBody.getString(BatchUploadProcessorConstatnt.RETURN_TEXT);
				ReturnCode = parentBody.getString(BatchUploadProcessorConstatnt.RETURN_CODE);
			} else if (response.getStatus() == 400 && body != null) {
				if (body.contains(BatchUploadProcessorConstatnt.UNRECOGNIZED_field)) {
					throw new ValidationException(body);
				}
				ReturnText = body;
				ReturnCode = BatchUploadProcessorConstatnt.CODE_09090;
			} else {
				List<FaultDetails> payloads = BatchProcessorUtil.convertJsonArrayToList(body);

				ReturnText = payloads.stream().filter(payload -> Objects.nonNull(payload))
						.map(FaultDetails::getFaultMessage)
						.collect(Collectors.joining(BatchUploadProcessorConstatnt.COMMA_SEP));

				ReturnCode = payloads.stream().filter(payload -> Objects.nonNull(payload))
						.map(FaultDetails::getFaultCode)
						.collect(Collectors.joining(BatchUploadProcessorConstatnt.COMMA_SEP));
			}
		} finally {
			client.close();
			client = null;
		}
		responseArray[0] = ReturnCode;
		responseArray[1] = ReturnText;
		responseArray[2] = extraHeaderValue;
		responseArray[3] = headerMessageId;
		return responseArray;
	}

	/**
	 * util method to get Webclient
	 * 
	 * @param serviceEndPoint url to hit the api
	 * @param messageId       to pass in input header
	 */
	private WebClient getClient(String serviceEndPoint, String messageId) {
		WebClient client = null;
		try {
			client = WebClient.create(serviceEndPoint);
			client.accept(MediaType.APPLICATION_JSON);
			client.type(MediaType.APPLICATION_JSON);
			client.header(BatchUploadProcessorConstatnt.AUTHORIZATION_KEY, authorization);
			client.header(BatchUploadProcessorConstatnt.MESSAGE_ID, messageId);
			String[] values = serviceEndPoint.split("/");
			client.header(BatchUploadProcessorConstatnt.SERVICENAME, values[values.length - 1]);
			client.header(BatchUploadProcessorConstatnt.SERVICEVERSION,
					BatchUploadProcessorConstatnt.SERVICEVERSIONVALUE);
			client.header(BatchUploadProcessorConstatnt.LANGUAGE, BatchUploadProcessorConstatnt.LANGUAGEVALUE);
			client.header(BatchUploadProcessorConstatnt.REQUESTTIME,
					DateUtility.getSysDate(PennantConstants.APIDateFormatter));
			client.header(BatchUploadProcessorConstatnt.ENTITYID, entityId.concat("BU"));
		} catch (Exception e) {
			logger.error(BatchUploadProcessorConstatnt.EXCEPTION, e);
		}
		return client;
	}

	/** writing api response to a sheet */
	private void write(String response[], Sheet writebleSheet, int lastCellIndex) {
		try {
			int cellndex = 1;
			if (response[3] != null) {
				String messageHeader = response[3];
				cellndex = Integer.parseInt(messageHeader.substring(messageHeader.lastIndexOf("/") + 1).trim());
				response = Arrays.copyOf(response, response.length - 1);
			}
			Row row = writebleSheet.getRow(cellndex);
			for (int i = 0; i < response.length; i++) {
				Cell cell = row.createCell(lastCellIndex + (i + 1));
				cell.setCellValue(response[i]);
			}
			FileOutputStream outputStream = new FileOutputStream(file);
			workbook.write(outputStream);
			outputStream.close();
		} catch (Exception e) {
			logger.error(BatchUploadProcessorConstatnt.EXCEPTION, e);
		}
	}

	public String getTime() {
		return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
	}
}
