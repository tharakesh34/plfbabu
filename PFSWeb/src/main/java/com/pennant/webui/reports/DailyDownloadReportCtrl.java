/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : DialyDownLoadsReportCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 16-11-2012 * *
 * Modified Date : 16-11-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 16-11-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.reports;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkmax.zul.Filedownload;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.PathUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.reports.ReportsMonthEndConfiguration;
import com.pennant.backend.service.reports.ReportConfigurationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

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

public class DailyDownloadReportCtrl extends GFCBaseListCtrl<ReportsMonthEndConfiguration> {
	private static final long serialVersionUID = 4678287540046204660L;

	protected Window window_DialyDownloadReport;
	protected Combobox moduleName;
	protected Combobox reportName;
	protected Button button_ExportToExcel;
	protected Button button_ExportToZIP;
	protected Panelchildren pc_StatusList;
	protected Vbox vbox_statusList;

	// Unused Fields
	protected Datebox fromDate;
	protected Datebox toDate;

	private List<ValueLabel> moduleNameList = null;
	private List<ValueLabel> reportNameList = new ArrayList<ValueLabel>();
	private ReportConfigurationService reportConfigurationService;

	public DailyDownloadReportCtrl() {
		super();
	}

	/**
	 * OnCreate window
	 * 
	 * @param event
	 */
	public void onCreate$window_DialyDownloadReport(Event event) {
		logger.debug("Entering" + event.toString());

		try {
			/*
			 * this.fromDate.setValue(DateUtility.today()); this.toDate.setValue(DateUtility.today());
			 */
			setModuleNamesList();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/*
	 * Setting the list of ModuleNames
	 */
	private void setModuleNamesList() {
		logger.debug("Entering ");
		moduleNameList = getReportConfigurationService().getMonthEndReportGrpCodes();
		fillComboBox(this.moduleName, "", moduleNameList, "");
		fillComboBox(this.reportName, "", reportNameList, "");
		logger.debug("Leaving ");
	}

	/**
	 * Method for changing Report Name List as per module group Name selection
	 * 
	 * @param event
	 */
	public void onChange$moduleName(Event event) {
		logger.debug("Entering" + event.toString());
		String grpCode = "";
		reportNameList = new ArrayList<ValueLabel>(1);
		if (this.moduleName.getSelectedIndex() != 0) {
			grpCode = this.moduleName.getSelectedItem().getValue().toString();
			reportNameList = getReportConfigurationService().getReportListByGrpCode(grpCode);
		}
		fillComboBox(this.reportName, "", reportNameList, "");
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method reads the components
	 * 
	 * @return
	 */
	private void verifyValidation(boolean isZIPDownload) {
		logger.debug("Entering");

		this.moduleName.setConstraint("");
		this.reportName.setConstraint("");
		this.moduleName.setErrorMessage("");
		this.reportName.setErrorMessage("");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {

			if (!this.moduleName.isDisabled() && this.moduleName.getSelectedIndex() == 0) {
				throw new WrongValueException(moduleName, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_DialyDownLoadsReport_GroupName.value") }));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (!isZIPDownload) {
			try {

				if (!this.reportName.isDisabled() && this.reportName.getSelectedIndex() == 0) {
					throw new WrongValueException(reportName, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_DialyDownLoadsReport_Reportname.value") }));
				}

			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		/*
		 * try {
		 * 
		 * if (fromDate.getValue() == null ) { throw new WrongValueException(fromDate,
		 * Labels.getLabel("label_AuditReport_FromDate.value")); } if (toDate.getValue() == null ) { throw new
		 * WrongValueException(toDate, Labels.getLabel("label_AuditReport_ToDate.value")); } if
		 * (fromDate.getValue().after(toDate.getValue())) { throw new WrongValueException(fromDate,
		 * Labels.getLabel("label_AuditReport_FromDate.NotGreater")); } if
		 * (fromDate.getValue().compareTo(DateUtility.today())>=0){ throw new WrongValueException(fromDate,
		 * Labels.getLabel("label_AuditReport_FromDate.Lessthan")); } map.put("FromDate",
		 * DateUtility.getDate(DateUtility.formatUtilDate(fromDate.getValue(), PennantConstants.dateFormat))); } catch
		 * (WrongValueException we) { wve.add(we); }
		 * 
		 * try { if (toDate.getValue() == null ) { throw new WrongValueException(toDate,
		 * Labels.getLabel("label_AuditReport_ToDate.value")); } if
		 * (toDate.getValue().compareTo(DateUtility.today())>=0){ throw new WrongValueException(toDate,
		 * Labels.getLabel("label_AuditReport_ToDate.Lessthan")); } map.put("ToDate",
		 * DateUtility.getDate(DateUtility.formatUtilDate(toDate.getValue(), PennantConstants.dateFormat))); } catch
		 * (WrongValueException we) { wve.add(we); }
		 */

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving :");
	}

	/**
	 * Method for Exporting Reports in Excel Format
	 * 
	 * @param event
	 * @throws SQLException
	 */
	public void onClick$button_ExportToExcel(Event event) throws SQLException {
		logger.debug("Entering" + event.toString());

		verifyValidation(false);
		String folderPath = this.moduleName.getSelectedItem().getValue().toString().trim();
		String reportName = this.reportName.getSelectedItem().getValue().toString().trim();
		String reportDesc = this.reportName.getSelectedItem().getLabel().trim();

		Connection connection = null;
		DataSource dataSourceObj = null;

		try {

			dataSourceObj = (DataSource) SpringUtil.getBean("dataSource");
			connection = dataSourceObj.getConnection();

			generateExcelReport(folderPath, reportName, reportDesc, false, "", connection);

		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			if (connection != null) {
				connection.close();
			}
			connection = null;
			dataSourceObj = null;
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Exporting Reports in Excel Format
	 * 
	 * @param event
	 * @throws SQLException
	 */
	public void onClick$button_ExportToZIP(Event event) throws SQLException {
		logger.debug("Entering" + event.toString());
		verifyValidation(true);
		ExportExcelFilesToZIP();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Generating Excel Format Report
	 * 
	 * @param reportSrc
	 * @return
	 * @throws InterruptedException
	 * @throws SQLException
	 */
	private boolean generateExcelReport(String folderPath, String reportName, String reportDesc, boolean bulkReportProc,
			String zipFolderPath, Connection con) throws InterruptedException, SQLException {
		logger.debug("Entering");

		String reportSrc = PathUtil.getPath(PathUtil.REPORTS_ENDOFMONTH) + "/" + folderPath + "/" + reportName
				+ ".jasper";
		Map<String, Object> reportArgumentsMap = new HashMap<String, Object>(5);
		reportArgumentsMap.put("userName", getUserWorkspace().getLoggedInUser().getUserName());
		reportArgumentsMap.put("reportGeneratedBy", Labels.getLabel("Reports_footer_ReportGeneratedBy.lable"));
		reportArgumentsMap.put("whereCondition", "");
		reportArgumentsMap.put("organizationLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));
		reportArgumentsMap.put("signimage", PathUtil.getPath(PathUtil.REPORTS_IMAGE_SIGN));
		reportArgumentsMap.put("productLogo", PathUtil.getPath(PathUtil.REPORTS_IMAGE_PRODUCT));

		File file = null;
		try {

			file = new File(reportSrc);
			if (file.exists()) {

				// use swap virtualizer by default
				int maxSize = 250;
				JRSwapFile swapFile = new JRSwapFile(System.getProperty("java.io.tmpdir"), 250, 250);
				JRAbstractLRUVirtualizer virtualizer = new JRSwapFileVirtualizer(maxSize, swapFile, true);
				reportArgumentsMap.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

				if (StringUtils.trimToEmpty(reportDesc).toLowerCase().endsWith(".pdf")) {
					if (bulkReportProc) {
						JasperPrint jasperPrint = JasperFillManager.fillReport(reportSrc, reportArgumentsMap, con);

						String outputFileName = zipFolderPath + File.separator + reportName + ".pdf";
						File outputFile = new File(outputFileName);
						// If File Already exist in Folder Delete it for Regeneration with New Data
						if (outputFile.exists()) {
							outputFile.delete();
						}
						JRPdfExporter pdfExporter = new JRPdfExporter();
						pdfExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
						pdfExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFileName);
						pdfExporter.exportReport();
						outputFile = null;
					} else {
						byte[] buf = null;
						buf = JasperRunManager.runReportToPdf(reportSrc, reportArgumentsMap, con);
						Filedownload.save(new AMedia(reportName, "pdf", "application/pdf", buf));
					}
				} else {

					ByteArrayOutputStream outputStream = null;

					// String printfileName = JasperFillManager.fillReportToFile(reportSrc, reportArgumentsMap, con);
					JasperPrint jasperPrint = JasperFillManager.fillReport(reportSrc, reportArgumentsMap, con);

					// set virtualizer read only to optimize performance. must be set after print object has been
					// generated
					if (virtualizer != null) {
						virtualizer.setReadOnly(true);
					}

					JRXlsExporter excelExporter = new JRXlsExporter();
					excelExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

					// excelExporter.setParameter(JRExporterParameter.INPUT_FILE_NAME,printfileName);
					excelExporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
							Boolean.FALSE);
					excelExporter.setParameter(JRXlsExporterParameter.IS_IMAGE_BORDER_FIX_ENABLED, Boolean.TRUE);
					excelExporter.setParameter(JRXlsExporterParameter.MAXIMUM_ROWS_PER_SHEET, Integer.decode("200000"));

					if (bulkReportProc) {

						String outputFileName = zipFolderPath + File.separator + reportName + ".xls";
						File outputFile = new File(outputFileName);
						// If File Already exist in Folder Delete it for Regeneration with New Data
						if (outputFile.exists()) {
							outputFile.delete();
						}
						outputFile = null;
						excelExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFileName);
					} else {
						outputStream = new ByteArrayOutputStream();
						excelExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
					}
					excelExporter.exportReport();

					// Excel Download to local System Directly
					if (!bulkReportProc) {
						Filedownload.save(
								new AMedia(reportName, "xls", "application/vnd.ms-excel", outputStream.toByteArray()));
						outputStream = null;
					}

					excelExporter = null;
					jasperPrint = null;
				}
				if (virtualizer != null) {
					virtualizer.cleanup();
				}

			} else {
				MessageUtil.showError(Labels.getLabel("label_Error_ReportNotImplementedYet.vlaue"));
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
			if (!bulkReportProc) {
				MessageUtil.showError("Error in Configuring the " + reportName + " report");
			}
			return false;
		} finally {
			reportSrc = null;
			file = null;
			reportArgumentsMap = null;
		}
		logger.debug("Leaving");
		return true;
	}

	/**
	 * This Method Imports reports with As Excel
	 * 
	 * @param event
	 * @throws SQLException
	 */
	private void ExportExcelFilesToZIP() throws SQLException {
		logger.debug("Entering");

		File folder = null;
		String tempFolderLocation = PathUtil.getPath(PathUtil.REPORTS_EOMDOWNLOAD_FOLDER);
		String folderPath = this.moduleName.getSelectedItem().getValue().toString().trim();
		String fileLocation = tempFolderLocation + "/" + folderPath + "_"
				+ DateUtil.format(DateUtil.getSysDate(), PennantConstants.DBDateFormat) + "_"
				+ getUserWorkspace().getLoggedInUser().getUserName().toUpperCase();
		fileLocation = tempFolderLocation + "/" + folderPath + "_"
				+ DateUtil.format(DateUtil.getSysDate(), PennantConstants.DBDateFormat) + "_"
				+ getUserWorkspace().getLoggedInUser().getUserName().toUpperCase();

		File directory = new File(fileLocation);
		directory.deleteOnExit();
		if (!directory.exists()) {
			directory.mkdir();
		}

		this.vbox_statusList.getChildren().clear();
		String reportName = "";
		String reportDesc = "";

		Connection connection = null;
		DataSource dataSourceObj = null;

		try {

			dataSourceObj = (DataSource) SpringUtil.getBean("dataSource");
			connection = dataSourceObj.getConnection();

			for (int i = 0; i < reportNameList.size(); i++) {
				reportName = reportNameList.get(i).getValue().trim();
				reportDesc = reportNameList.get(i).getLabel().trim();
				boolean isGenerateExcel = generateExcelReport(folderPath, reportName, reportDesc, true,
						directory.getAbsolutePath(), connection);

				if (isGenerateExcel) {
					appendChild(Labels.getLabel("labels_DownLoadSuccess.value"), reportDesc, true);
				} else {
					appendChild(Labels.getLabel("labels_DownLoadFail.value"), reportDesc, false);
				}
				this.vbox_statusList.invalidate();
			}

			createZIPFile(fileLocation);
			folder = new File(fileLocation + ".zip");
			Filedownload.save(folder, "application/*");

		} catch (Exception e) {
			appendChild("Download fail ", reportName, false);
			logger.error("Exception: ", e);
		} finally {
			// Delete File after Creating ZIP
			deleteFile(new File(fileLocation));
			directory = null;
			folder = null;

			if (connection != null) {
				connection.close();
			}
			connection = null;
			dataSourceObj = null;

		}
		logger.debug("Leaving");
	}

	/**
	 * This method takes source file and create .zip file of source file
	 * 
	 * @param fileLocation
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void createZIPFile(String fileLocation) throws FileNotFoundException, IOException {
		logger.debug("Entering");

		File inFolder = null;
		ZipOutputStream out = null;
		FileInputStream in = null;

		try {

			inFolder = new File(fileLocation);
			out = new ZipOutputStream(new FileOutputStream(fileLocation + ".zip"));

			byte[] data = new byte[1000];
			String files[] = inFolder.list();

			for (int i = 0; i < files.length; i++) {

				in = new FileInputStream(inFolder.getPath() + "/" + files[i]);
				out.putNextEntry(new ZipEntry(files[i]));
				int count;

				while ((count = in.read(data, 0, 1000)) != -1) {
					out.write(data, 0, count);
				}
				out.closeEntry();
			}

			files = null;
			data = null;

		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			out.flush();
			out.close();
			in.close();
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Deleting Folder having Files
	 * 
	 * @param file
	 */
	private void deleteFile(File file) {
		logger.debug("Entering");

		try {
			if (file.exists()) {
				if (file.isDirectory()) {
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						files[i].delete();
					}
					if (file.list().length == 0) {
						file.delete();
					} else {
						deleteFile(file);
					}
				} else {
					// if file, then delete it
					file.delete();
				}
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} finally {
			file = null;
		}
		logger.debug("Leaving");
	}

	public void appendChild(String value, String label, boolean isSucess) {
		Label statuslabel = new Label(value + " " + label);
		if (isSucess) {
			statuslabel.setStyle("Color:Green");
		} else {
			statuslabel.setStyle("Color:Red");
		}
		this.vbox_statusList.appendChild(statuslabel);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public ReportConfigurationService getReportConfigurationService() {
		return reportConfigurationService;
	}

	public void setReportConfigurationService(ReportConfigurationService reportConfigurationService) {
		this.reportConfigurationService = reportConfigurationService;
	}

}
