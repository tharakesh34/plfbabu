package com.pennanttech.batchupload.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.pennant.batchupload.customexception.ValidationException;
import com.pennant.batchupload.fileprocessor.BatchUploadProcessorConstatnt;
import com.pennant.batchupload.model.FaultDetails;

public class BatchProcessorUtil {
	private static final Logger logger = Logger.getLogger(BatchProcessorUtil.class);

	/**
	 * return all keys of a sheet
	 * 
	 * @param sheetIndex
	 *            index of required sheet.
	 */
	public static List<String> getAllKeysByIndex(Workbook workbook , int sheetIndex) {
		List<String> keys = new ArrayList<String>();
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		Row headings = sheet.getRow(0);
		if (headings == null) {
			throw new ValidationException(BatchUploadProcessorConstatnt.INVALID_FILE_MSG);
		}
		for (Cell cell : headings) {
			if (cell.toString().trim().equals("")) {
				throw new ValidationException(BatchUploadProcessorConstatnt.HEADER_KEY);
			}
			keys.add(cell.toString().replace("\"", "").trim());
		}
		return keys;
	}

	/** util method convert String to boolean */
	public static boolean boolFormater(String input) {
		if (input.equalsIgnoreCase(BatchUploadProcessorConstatnt.TRUE)) {
			return Boolean.valueOf(BatchUploadProcessorConstatnt.TRUE);
		} else {
			return Boolean.valueOf(BatchUploadProcessorConstatnt.FALSE);
		}
	}
	
	/** util method accepting payload and converting to list of FaultDetails */
	public static List<FaultDetails> convertJsonArrayToList(String payload)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objMapper = new ObjectMapper();

		List<FaultDetails> faultDetailsList = objMapper.readValue(payload, new TypeReference<List<FaultDetails>>() {
		});
		return faultDetailsList;
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
			DateFormat originalFormat = new SimpleDateFormat(BatchUploadProcessorConstatnt.SAMPLE_DATE_FRMT_OLD, Locale.ENGLISH);
			DateFormat targetFormat = new SimpleDateFormat(BatchUploadProcessorConstatnt.SAMPLE_DATE_FRMT);
			date1 = originalFormat.parse(date);
			formattedDate = targetFormat.format(date1);
		} catch (ParseException e) {
			logger.error(BatchUploadProcessorConstatnt.EXCEPTION, e);
			throw new ValidationException(BatchUploadProcessorConstatnt.DATE_EXCEPTION);
		}
		return formattedDate;
	}
}
