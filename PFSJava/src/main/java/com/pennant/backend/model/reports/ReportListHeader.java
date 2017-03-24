package com.pennant.backend.model.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class ReportListHeader {

	private static Logger logger = Logger.getLogger(ReportListHeader.class);
	private String fieldLabel01;
	private String fieldLabel02;
	private String fieldLabel03;
	private String fieldLabel04;
	private String fieldLabel05;
	private String fieldLabel06;
	private String fieldLabel07;
	private String fieldLabel08;
	private String fieldLabel09;
	private String fieldLabel10;
	private String fieldLabel11;
	private String fieldLabel12;
	private String fieldLabel13;
	private String fieldLabel14;
	private String fieldLabel15;
	
	private List<ReportListDetail> listDetails; 
	
	public void setFiledLabel(String[] fieldLabels) {
		
		try {
			fieldLabel01=fieldLabels[0];
			fieldLabel02=fieldLabels[1];
			fieldLabel03=fieldLabels[2];
			fieldLabel04=fieldLabels[3];
			fieldLabel05=fieldLabels[4];
			fieldLabel06=fieldLabels[5];
			fieldLabel07=fieldLabels[6];
			fieldLabel08=fieldLabels[7];
			fieldLabel09=fieldLabels[8];
			fieldLabel10=fieldLabels[9];
			fieldLabel11=fieldLabels[10];
			fieldLabel12=fieldLabels[11];
			fieldLabel13=fieldLabels[12];
			fieldLabel14=fieldLabels[13];
			fieldLabel15=fieldLabels[14];
		} catch (Exception e) {
			logger.warn("Exception: ", e);
		}
		
	}
	
	public String getfieldLabel01() {
		return fieldLabel01;
	}
	public void setfieldLabel01(String fieldLabel01) {
		this.fieldLabel01 = fieldLabel01;
	}
	public String getfieldLabel02() {
		return fieldLabel02;
	}
	public void setfieldLabel02(String fieldLabel02) {
		this.fieldLabel02 = fieldLabel02;
	}
	public String getfieldLabel03() {
		return fieldLabel03;
	}
	public void setfieldLabel03(String fieldLabel03) {
		this.fieldLabel03 = fieldLabel03;
	}
	public String getfieldLabel04() {
		return fieldLabel04;
	}
	public void setfieldLabel04(String fieldLabel04) {
		this.fieldLabel04 = fieldLabel04;
	}
	public String getfieldLabel05() {
		return fieldLabel05;
	}
	public void setfieldLabel05(String fieldLabel05) {
		this.fieldLabel05 = fieldLabel05;
	}
	public String getfieldLabel06() {
		return fieldLabel06;
	}
	public void setfieldLabel06(String fieldLabel06) {
		this.fieldLabel06 = fieldLabel06;
	}
	public String getfieldLabel07() {
		return fieldLabel07;
	}
	public void setfieldLabel07(String fieldLabel07) {
		this.fieldLabel07 = fieldLabel07;
	}
	public String getfieldLabel08() {
		return fieldLabel08;
	}
	public void setfieldLabel08(String fieldLabel08) {
		this.fieldLabel08 = fieldLabel08;
	}
	public String getfieldLabel09() {
		return fieldLabel09;
	}
	public void setfieldLabel09(String fieldLabel09) {
		this.fieldLabel09 = fieldLabel09;
	}
	public String getfieldLabel10() {
		return fieldLabel10;
	}
	public void setfieldLabel10(String fieldLabel10) {
		this.fieldLabel10 = fieldLabel10;
	}
	public String getfieldLabel11() {
		return fieldLabel11;
	}
	public void setfieldLabel11(String fieldLabel11) {
		this.fieldLabel11 = fieldLabel11;
	}
	public String getfieldLabel12() {
		return fieldLabel12;
	}
	public void setfieldLabel12(String fieldLabel12) {
		this.fieldLabel12 = fieldLabel12;
	}
	public String getfieldLabel13() {
		return fieldLabel13;
	}
	public void setfieldLabel13(String fieldLabel13) {
		this.fieldLabel13 = fieldLabel13;
	}
	public String getfieldLabel14() {
		return fieldLabel14;
	}
	public void setfieldLabel14(String fieldLabel14) {
		this.fieldLabel14 = fieldLabel14;
	}
	public String getfieldLabel15() {
		return fieldLabel15;
	}
	public void setfieldLabel15(String fieldLabel15) {
		this.fieldLabel15 = fieldLabel15;
	}
	public void setListDetails(List<ReportListDetail> listDetails) {
		this.listDetails = listDetails;
	}
	public List<ReportListDetail> getListDetails() {
		return listDetails;
	}

	public static Map<String, Object> getReportListHeader(ReportListHeader reportListHeader) {
		logger.debug("Entering");
		Map<String, Object> parameters = new HashMap<String, Object>();
			for (int i = 0; i < reportListHeader.getClass().getDeclaredFields().length; i++) {
				try {
				parameters.put(reportListHeader.getClass().getDeclaredFields()[i].getName(),
						reportListHeader.getClass().getDeclaredFields()[i].get(reportListHeader));
			} catch (Exception e) {
				logger.error("Exception: ", e);
			} 
		}
		logger.debug("Leaving");
			return parameters;	
	}
}
