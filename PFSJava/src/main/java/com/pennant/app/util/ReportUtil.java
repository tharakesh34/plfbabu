package com.pennant.app.util;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Filedownload;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.fill.JRAbstractLRUVirtualizer;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;

public class ReportUtil implements Serializable {
    private static final long serialVersionUID = 7959183655705303553L;
	private static final Logger logger = Logger.getLogger(ReportUtil.class);


	/**
	 * Method for Generating Excel Format Report
	 * @param reportSrc
	 * @return 
	 * @throws InterruptedException
	 * @throws SQLException
	 */
	public boolean generateExcelReport(String folderPath, String reportName, String reportDesc, boolean bulkReportProc, 
			String zipFolderPath, Connection con,Date appDate) throws InterruptedException, SQLException{
		logger.debug("Entering");
		String tempfile = reportName.substring(0, reportName.length() - 4);
		String reportSrc =  folderPath + tempfile+".jasper";
		
		String destinationFile =  folderPath + reportName;
		HashMap<String, Object> reportArgumentsMap = new HashMap<String, Object>(5);
		reportArgumentsMap.put("reportGeneratedBy", Labels.getLabel("Reports_footer_ReportGeneratedBy.lable"));
		reportArgumentsMap.put("whereCondition","");
		reportArgumentsMap.put("organizationLogo",PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));
		reportArgumentsMap.put("signimage",PathUtil.getPath(PathUtil.REPORTS_IMAGE_SIGN));
		reportArgumentsMap.put("client",PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT_DIGITAL));
		reportArgumentsMap.put("productLogo",PathUtil.getPath(PathUtil.REPORTS_IMAGE_PRODUCT));
		reportArgumentsMap.put("chequeDate",appDate);

		File file = null;
		try {			
			
			file = new File(reportSrc) ;
			if(file.exists()){
				
				//use swap virtualizer by default
				int maxSize= 250;
				JRSwapFile swapFile=new JRSwapFile(System.getProperty("java.io.tmpdir"),250,250);
				JRAbstractLRUVirtualizer virtualizer =new JRSwapFileVirtualizer(maxSize,swapFile, true);
				reportArgumentsMap.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
				 
				if(StringUtils.trimToEmpty(reportDesc).toLowerCase().endsWith(".pdf")){
					if(bulkReportProc){
						JasperPrint jasperPrint = JasperFillManager.fillReport(reportSrc, reportArgumentsMap, con);
						
						String outputFileName = zipFolderPath +"\\"+ reportName+".pdf";
						File outputFile = new File(outputFileName);
						//If File Already exist in Folder Delete it for Regeneration with New Data
						if(outputFile.exists()){
							outputFile.delete();
						}		
						JRPdfExporter pdfExporter = new JRPdfExporter();
						pdfExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
						pdfExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFileName);   
						pdfExporter.exportReport();
						outputFile = null;
					}else{	
						byte[] buf = null;
						buf = JasperRunManager.runReportToPdf(reportSrc, reportArgumentsMap,con);
						Filedownload.save(new AMedia(reportName, "pdf", "application/pdf", buf));
					}
				}else{


					//String printfileName = JasperFillManager.fillReportToFile(reportSrc, reportArgumentsMap, con);
					JasperPrint jasperPrint = JasperFillManager.fillReport(reportSrc, reportArgumentsMap, con);

					//set virtualizer read only to optimize performance. must be set after print object has been generated
					virtualizer.setReadOnly(true);

					JRXlsExporter excelExporter = new JRXlsExporter();
					excelExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

					//excelExporter.setParameter(JRExporterParameter.INPUT_FILE_NAME,printfileName); 
					excelExporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);  
					excelExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);  
					excelExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);  
					excelExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.FALSE);  
					excelExporter.setParameter(JRXlsExporterParameter.IS_IMAGE_BORDER_FIX_ENABLED, Boolean.TRUE);
					excelExporter.setParameter(JRXlsExporterParameter.MAXIMUM_ROWS_PER_SHEET, Integer.decode("200000") );
					excelExporter.setParameter(JRXlsExporterParameter.MAXIMUM_ROWS_PER_SHEET, Integer.decode("200000") );

					if(bulkReportProc){

						String outputFileName = zipFolderPath +"\\"+ reportName+".xls";
						File outputFile = new File(outputFileName);
						//If File Already exist in Folder Delete it for Regeneration with New Data
						if(outputFile.exists()){
							outputFile.delete();
						}		
						outputFile = null;
						excelExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFileName);   
					}else{
						excelExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destinationFile);
					}
					excelExporter.exportReport();
					
					excelExporter = null;
					jasperPrint = null;
				}
				if(virtualizer!=null){
					virtualizer.cleanup();
				}
					
			}
			
			/*else{
				MessageUtil.showErrorMessage(Labels.getLabel("label_Error_ReportNotImplementedYet.vlaue"));
			}*/
			
		} catch (Exception e) {
			logger.error("Exception: ", e);
			/*if(!bulkReportProc){
				MessageUtil.showErrorMessage("Error in Configuring the " +reportName+ " report");
			}*/
			return false;
		}finally{
			reportSrc = null;
			file = null;
			reportArgumentsMap = null;
		}
		logger.debug("Leaving");
		return true;
	}
		
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	
}
