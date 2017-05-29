package com.pennanttech.framework.component.dataengine;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Messagebox;

import com.pennant.backend.model.ValueLabel;


public class ConfigUtil {

	// Data Import
	public static final String MICROSOFTEXCEL = "Excel";		
	public static final String FIXEDWIDTH = "FixedWidth";
	public static final String DELIMITED = "Delimited";		
	public static final String CLIENT_FILE_LOCATION = "C";
	public static final String SERVER_FILE_LOCATION = "S";
	public static final String AUTO_FILE_LOCATION = "A";
	public static final String REGEXP_FIXEDWIDTH="/^[0-9]{1,3}\\|{1}[0-9]{1,3}$/: Please enter in correct format. Example: 01|04 ";

	//Sequence Types
	public static final String SEQ_TYPE_NOTAPPLICABLE = "N";
	public static final String SEQ_TYPE_AUTOINCREMENT = "A";
	public static final String SEQ_TYPE_IDENTITY = "I";
	public static final String SEQ_TYPE_SEQOBJECT = "S";
	public static final String SEQ_TYPE_SEQTABLE = "T";
	public static final String SEQ_TYPE_MAX = "M";
	
	/**
	 * Shows a multiline ErrorMessage.<br>
	 * 
	 * @param e
	 * @throws InterruptedException
	 */
	public static void showErrorMessage(String e) throws InterruptedException {
		final String title = Labels.getLabel("message.Error");
		Messagebox.show(e, title,Messagebox.OK, "ERROR");
	}

	public static void showMessage(String message) throws InterruptedException {
		final String title = Labels.getLabel("message.Information");
		//Messagebox.doSetTemplate();
		Messagebox.show(message, title, Messagebox.OK, "INFORMATION");
	}


	public static List<ValueLabel>  getTextQualifiers(){

		List<ValueLabel> textQualifiersList = new ArrayList<ValueLabel>();
		textQualifiersList.add(new ValueLabel("'", " ' "));
		textQualifiersList.add(new ValueLabel("\"", " \" "));

		return textQualifiersList;	 
	}

	public static List<ValueLabel>  getRowDelimiters(){

		List<ValueLabel> rowDelimitersList = new ArrayList<ValueLabel>();
		rowDelimitersList.add(new ValueLabel("CRLF", "{CR}{LF}"));
		rowDelimitersList.add(new ValueLabel("CR", "{CR}"));
		rowDelimitersList.add(new ValueLabel("LF", "{LF}"));

		return rowDelimitersList;	 
	}

	public static List<ValueLabel>  getColumnDelimiters(){
		List<ValueLabel> columnDelimitersList = new ArrayList<ValueLabel>();
		columnDelimitersList.add(new ValueLabel(";", "Semicolon {;}"));
		columnDelimitersList.add(new ValueLabel(":", "Colon {:}"));
		columnDelimitersList.add(new ValueLabel(",", "Comma {,}"));
		columnDelimitersList.add(new ValueLabel("t", "Tab {t}"));
		columnDelimitersList.add(new ValueLabel("|", "Vertical bar {|}"));
		return columnDelimitersList;	 
	}

	public static List<ValueLabel>  getFileUploadLocations(){

		List<ValueLabel> fileUploadLocationsList = new ArrayList<ValueLabel>();
		fileUploadLocationsList.add(new ValueLabel("C", "Client"));
		fileUploadLocationsList.add(new ValueLabel("S", "Server"));
		fileUploadLocationsList.add(new ValueLabel("A", "Auto"));

		return fileUploadLocationsList;	 
	}

	public static List<ValueLabel>  getDerivedFromValues(){

		List<ValueLabel> derivedFromValuesList = new ArrayList<ValueLabel>();
		derivedFromValuesList.add(new ValueLabel("SYS_DATE", "System Date"));
		derivedFromValuesList.add(new ValueLabel("USR_ID", "User Id"));
		derivedFromValuesList.add(new ValueLabel("File_NAME", "File Name"));
		derivedFromValuesList.add(new ValueLabel("VALUE_DATE", "Value Date"));

		return derivedFromValuesList;	 
	}

	public static List<ValueLabel>  getFlatFileExtentions(){

		List<ValueLabel> flatFileExtentionsList = new ArrayList<ValueLabel>();
		flatFileExtentionsList.add(new ValueLabel("csv", "CSV"));
		flatFileExtentionsList.add(new ValueLabel("txt", "TXT"));

		return flatFileExtentionsList;	 
	}

	public static List<ValueLabel>  getExcelFileExtentions(){

		List<ValueLabel> excelFileExtentionsList = new ArrayList<ValueLabel>();
		excelFileExtentionsList.add(new ValueLabel("xls", "XLS"));
		excelFileExtentionsList.add(new ValueLabel("xlsx", "XLSX"));

		return excelFileExtentionsList;	 
	}

	public static List<ValueLabel>  getSeqTypes(){

		List<ValueLabel> seqTypesList = new ArrayList<ValueLabel>();
		seqTypesList.add(new ValueLabel(SEQ_TYPE_NOTAPPLICABLE, "Not Applicable"));
		seqTypesList.add(new ValueLabel(SEQ_TYPE_AUTOINCREMENT, "Auto Increment"));
		seqTypesList.add(new ValueLabel(SEQ_TYPE_IDENTITY, "Identity"));
		seqTypesList.add(new ValueLabel(SEQ_TYPE_SEQOBJECT, "Seq Object"));
		seqTypesList.add(new ValueLabel(SEQ_TYPE_SEQTABLE, "Seq Table"));
		seqTypesList.add(new ValueLabel(SEQ_TYPE_MAX, "Max"));

		return seqTypesList;	 
	}

	public static List<ValueLabel>  getDateFormats(){

		List<ValueLabel> dateFormatsList = new ArrayList<ValueLabel>();
		dateFormatsList.add(new ValueLabel("ddmmyyyy", "ddmmyyyy"));

		return dateFormatsList;	 
	}
	
	public static List<String>  getFileExtentions(){

		List<String> fileExtentionsList = new ArrayList<String>();
		fileExtentionsList.add("xls");
		fileExtentionsList.add("xlsx");
		fileExtentionsList.add("csv");
		fileExtentionsList.add("txt");
		fileExtentionsList.add("doc");
		fileExtentionsList.add("htm");
		fileExtentionsList.add("html");
		fileExtentionsList.add("gif");
		fileExtentionsList.add("jpg");
		fileExtentionsList.add("exe");
		fileExtentionsList.add("pdf");

		return fileExtentionsList;	 
	}
}
