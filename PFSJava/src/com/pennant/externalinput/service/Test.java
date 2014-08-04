package com.pennant.externalinput.service;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.util.PennantConstants;

public class Test {

	public static void main(String[] args) {
		
		System.out.println(excelReportGeneration());

	}

	public static String excelReportGeneration(){

		try{   
			
			//File sourceFile = new File("C:/PFF/PFSReports/OrgReports/AccrualPostings.jasper");   

			// JasperPrint print = JasperFillManager.fillReport(filename, hm, new JREmptyDataSource());    

			//JasperReport jasperReport= (JasperReport)JRLoader.loadObject(sourceFile);  
			
		  //  JasperCompileManager.compileReportToFile("C:/PFF/PFSReports/OrgReports/Source/AccrualPostings.jrxml", "C:/PFF/PFSReports/OrgReports/AccrualPostings.jasper");  
		    
		    Connection conn = null;  
			 String url = "jdbc:sqlserver://192.168.1.8:1433;database=AIBPFF";  
			 String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";  
			 String userName = "PFSAdmin";  
			 String password = "pfs";  
			 String inputfileName = "C:/PFF/PFSReports/OrgReports/AccrualPostings.jasper";  
			 String outFilenameExcel = "D:\\ReportExcel.xls";  
		    
		    Class.forName(driver).newInstance();  
			conn = DriverManager.getConnection(url , userName, password);  
		    String printfileName = JasperFillManager.fillReportToFile(inputfileName, new HashMap<String, Object>(), conn);  
		    
			File destFile = new File(outFilenameExcel);   
			JRXlsExporter exporter = new JRXlsExporter();   

			exporter.setParameter(JRExporterParameter.INPUT_FILE_NAME,printfileName); 
			exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);  
			exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);  
			exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);  
			exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);  
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outFilenameExcel);   
			exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);   
			exporter.exportReport();  
			/*
			JRXlsExporter exporter = new JRXlsExporter();
            exporter.setParameter(JRExporterParameter.INPUT_FILE_NAME,printfileName);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME,outFilenameExcel);
            exporter.exportReport();*/

		}   
		catch(Throwable t){return "error: " + t.getMessage();}   
		return "XLS Report generated Successfully!";   

	}


	/**
	 * Method for get the Percentage of given value 
	 * @param dividend
	 * @param divider
	 * @return
	 */
	public static BigDecimal getPercentageValue(BigDecimal dividend, BigDecimal divider, boolean isDayBasis){
		if(isDayBasis){
			return ((dividend.multiply(unFormateAmount(divider,2).divide(new BigDecimal(100)))).multiply(
					CalculationUtil.getInterestDays(DateUtility.getUtilDate("02/07/2013", PennantConstants.dateFormat), 
							DateUtility.getUtilDate("05/07/2013", PennantConstants.dateFormat),
							CalculationConstants.IDB_ACT_360))).divide(new BigDecimal(100),RoundingMode.HALF_DOWN);
		}else{
			return (dividend.multiply(unFormateAmount(divider,2).divide(
					new BigDecimal(100)))).divide(new BigDecimal(100),RoundingMode.HALF_DOWN);
		}
	}

	/**
	 * Method for UnFormat the passing Amount Value
	 * @param amount
	 * @param dec
	 * @return
	 */
	public static BigDecimal unFormateAmount(BigDecimal amount, int dec) {
		if (amount == null) {
			return new BigDecimal(0);
		}
		BigInteger bigInteger = amount.multiply(new BigDecimal(Math.pow(10, dec))).toBigInteger();
		return new BigDecimal(bigInteger);
	}



}
