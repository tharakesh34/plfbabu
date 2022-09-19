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
 * * FileName : LenderDataImportCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-12-2018 * * Modified
 * Date : * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-12-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennanttech.lenderupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.jdbc.BadSqlGrammarException;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.amazonaws.util.CollectionUtils;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lenderdataupload.LenderDataUpload;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.finance.UploadHeaderService;
import com.pennant.backend.service.lenderupload.LenderDataService;
import com.pennant.backend.service.lenderupload.LenderDataUploadService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/LenderUpload/LenderDataUpload.zul<br>
 * ************************************************************<br>
 * 
 */
public class LenderDataImportCtrl extends GFCBaseListCtrl<LenderDataUpload> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LenderDataImportCtrl.class);

	protected Window window_LenderDataImportList;
	protected Borderlayout borderLayout_LenderDataImportList;
	protected Paging pagingLenderDataImportList;
	protected Listbox lenderlistBox;
	protected Textbox fileName;
	protected Button btnUpload;
	protected Button btndownload;
	protected Button btnSave;
	protected Button btnRefresh;
	protected Label totalCount;
	protected Label successCount;
	protected Label failedCount;
	protected Grid statusGrid;

	private final String uploadLoaction = "/opt/pennant/external";
	private File file;
	private Workbook workbook = null;
	private DataFormatter objDefaultFormat = new DataFormatter();// for cell value formating
	private FormulaEvaluator objFormulaEvaluator = null; // for cell value formating

	private UploadHeaderService uploadHeaderService;
	private LenderDataUploadService lenderDataUploadService;
	private FinanceMainDAO financeMainDAO;
	private LenderDataService lenderDataService;
	private ExtendedFieldHeaderDAO extendedFieldHeaderDAO;
	private ExtendedFieldDetailDAO extendedFieldDetailDAO;

	/**
	 * default constructor.<br>
	 */
	public LenderDataImportCtrl() {
		super();
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_LenderDataImportList(Event event) {

		// Set the page level components.
		setPageComponents(window_LenderDataImportList, borderLayout_LenderDataImportList, lenderlistBox,
				pagingLenderDataImportList);
		this.btnUpload.setVisible(true);

		// Lender the page and display the data.
		doRenderPage();
	}

	/**
	 * when the "Upload" button is clicked. <br>
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onUpload$btnUpload(UploadEvent event) throws IOException {
		logger.debug(Literal.ENTERING);
		fileName.setText("");
		Media media = event.getMedia();

		btndownload.setVisible(false);
		if (!MediaUtil.isExcel(media)) {
			MessageUtil.showError(Labels.getLabel("upload_document_invalid", new String[] { "excel" }));
			media = null;
			return;
		}

		this.btnUpload.setDisabled(false);
		fileName.setText(media.getName());
		String fName = media.getName();
		writeFile(media, fName);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSave(Event event) {
		if (StringUtils.isBlank(fileName.getValue())) {
			MessageUtil.showError("Please upload a excel file");
		} else {
			boolean fileExists = uploadHeaderService.isFileNameExist(fileName.getValue());
			if (fileExists) {
				MessageUtil.showError("File with " + fileName.getValue() + " already exists. ");
				return;
			}
			doProcessLenders(fileName.getValue());
		}
	}

	/**
	 * when the "Process" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btndownload(Event event) {
		logger.debug("Entering" + event.toString());
		String whereCond = "and FILENAME in (" + "'" + fileName.getValue() + "'" + ")";
		StringBuilder searchCriteriaDesc = new StringBuilder(" ");
		searchCriteriaDesc.append("File Name is " + fileName.getValue());

		String userName = getUserWorkspace().getLoggedInUser().getFullName();
		try {
			ReportsUtil.generateReport(userName, "LenderDataUpload", whereCond, searchCriteriaDesc);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnRefresh(Event event) {
		logger.debug(Literal.ENTERING);

		doResetData();

		logger.debug(Literal.LEAVING);
	}

	private void doResetData() {
		logger.debug(Literal.ENTERING);

		this.fileName.setText("");

		this.workbook = null;
		this.objDefaultFormat = new DataFormatter();// for cell value formating
		this.objFormulaEvaluator = null; // for cell value formating

		this.statusGrid.setVisible(false);
		this.btndownload.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	private void writeFile(Media media, String fName) throws IOException {
		logger.debug(Literal.ENTERING);
		File parent = new File(uploadLoaction);

		if (!parent.exists()) {
			parent.mkdirs();
		}
		file = new File(parent.getPath().concat(File.separator).concat(media.getName()));
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		FileUtils.writeByteArrayToFile(file, media.getByteData());
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			if (file.toString().toLowerCase().endsWith(".xls")) {
				workbook = new HSSFWorkbook(fis);
			} else {
				workbook = new XSSFWorkbook(fis);
			}
		} catch (Exception e) {
			MessageUtil.showError("contact admin");
			logger.error(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * reading whole excel and calling other methods to prepare LenderData object and Save the LenderData .
	 * 
	 * @return String
	 */
	public void doProcessLenders(String fName) {
		logger.debug("Entering");
		List<ExtendedFieldHeader> extendedFieldHeader = null;
		List<LenderDataUpload> lenderDatas = new ArrayList<>();
		StringBuilder remarks = null;
		int rowCount = 0;
		boolean emptyExcel = true;
		long uploadHeaderId = Long.MIN_VALUE;
		int totalCount = 0;
		int successCount = 0;
		int failedCount = 0;

		extendedFieldHeader = extendedFieldHeaderDAO
				.getExtFieldHeaderListByModuleName(ExtendedFieldConstants.MODULE_LOAN, FinServiceEvent.ORG, "");

		if (CollectionUtils.isNullOrEmpty(extendedFieldHeader)) {
			MessageUtil.showError("Extended Field configuration not available with module: "
					+ ExtendedFieldConstants.MODULE_LOAN + " and Event: " + FinServiceEvent.ORG);
			return;
		}
		if (this.workbook instanceof HSSFWorkbook) {
			this.objFormulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) this.workbook);
		} else if (this.workbook instanceof XSSFWorkbook) {
			this.objFormulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) this.workbook);
		}

		List<String> keys = getAllValuesOfRowByIndex(workbook, 0, 0);
		if (!keys.contains("Lender Id")) {
			MessageUtil.showError("Invalid format,");
			return;
		}
		Sheet sheet = workbook.getSheetAt(0);

		int noOfRows = sheet.getPhysicalNumberOfRows();

		if (noOfRows == 0) {
			MessageUtil.showError("File is empty.");
		} else {

			Iterator<Row> rows = sheet.iterator();

			// read the excel rows
			while (rows.hasNext()) {
				remarks = new StringBuilder();
				Row row = rows.next();
				int rowIndex = row.getRowNum();
				// skip the header rows
				if (rowIndex > 0) {
					rowCount++;
					emptyExcel = false;
					// prepare the row data
					List<String> rowValue = getAllValuesOfRowByIndex(workbook, 0, rowIndex);

					// validate excel sheet empty or null
					if (CollectionUtils.isNullOrEmpty(rowValue)) {
						MessageUtil.showError("Invalid Lender data,");
						return;
					} else {// to validate against space value
						int count = 0;
						for (String value : rowValue) {
							if (StringUtils.equals(value, "")) {
								count++;
							}
						}
						if (count >= 2) {
							MessageUtil.showError("Invalid Lender data,");
							return;
						}
					}

					LenderDataUpload lenderData = new LenderDataUpload();
					lenderData.setFinReference(rowValue.get(0));
					if (StringUtils.isNotBlank(rowValue.get(1))) {
						lenderData.setLenderId(rowValue.get(1));
					}
					lenderData.setVersion(1);
					lenderData.setLastMntBy(SessionUserDetails.getLogiedInUser().getUserId());
					lenderData.setLastMntOn(new Timestamp(System.currentTimeMillis()));

					// save the header table data
					if (rowIndex == 1) {
						uploadHeaderId = saveUploadHeaderDetails(fName);
					}

					// validate Lender Data
					remarks = validateLenderData(lenderData, extendedFieldHeader, remarks);

					// validation failed log the Lender data
					if (remarks.length() > 0) {
						lenderData.setReason(remarks.toString());
						LenderDataUpload lenderDataStatus = prepareLenderStatus(lenderData, fName, false,
								remarks.toString());
						lenderDataStatus.setUploadHeaderId(uploadHeaderId);
						failedCount++;
						lenderDataUploadService.save(lenderDataStatus);
						continue;
					}

					// store success LenderData in list
					lenderDatas.add(lenderData);
				}
			}
		}

		// process success validated LenderData
		if (rowCount >= (noOfRows - 1) && lenderDatas.size() > 0) {
			int countRecord = 0;
			String type = "_Temp";
			boolean isExist = false;
			FinanceMain financeMain = null;
			remarks = new StringBuilder();
			for (LenderDataUpload dataUpload : lenderDatas) {
				if (dataUpload.getReason() == null) {
					financeMain = financeMainDAO.getFinanceMainByRef(dataUpload.getFinReference(), "_View", false);
					String tableName = getTableName(ExtendedFieldConstants.MODULE_LOAN, financeMain.getFinCategory(),
							FinServiceEvent.ORG);
					isExist = lenderDataService.isLenderExist(dataUpload.getFinReference(), tableName, type);
					if (!isExist) {
						type = "";
						isExist = lenderDataService.isLenderExist(dataUpload.getFinReference(), tableName, type);
					}
					if (isExist) {
						try {
							countRecord = lenderDataService.update(dataUpload, tableName, type);
							if (countRecord <= 0) {
								remarks.append("Invalid FinReference,");
								LenderDataUpload lenderDataStatus = prepareLenderStatus(dataUpload, fName, false,
										remarks.toString());
								lenderDataStatus.setUploadHeaderId(uploadHeaderId);
								failedCount++;
								lenderDataUploadService.save(lenderDataStatus);
								continue;
							}

							// logging for Success Lender Upload
							LenderDataUpload lenderDataStatus = prepareLenderStatus(dataUpload, fName, true, "Success");
							lenderDataStatus.setUploadHeaderId(uploadHeaderId);
							successCount++;
							lenderDataUploadService.save(lenderDataStatus);
							continue;
						} catch (BadSqlGrammarException e) {
							logger.warn(e);
							remarks.append("Column LenderID does not exist");
							LenderDataUpload lenderDataStatus = prepareLenderStatus(dataUpload, fName, false,
									remarks.toString());
							lenderDataStatus.setUploadHeaderId(uploadHeaderId);
							failedCount++;
							lenderDataUploadService.save(lenderDataStatus);
							continue;
						}
					} else {
						remarks.append("Lender Data does not exist with FinReference: " + dataUpload.getFinReference());
						LenderDataUpload lenderDataStatus = prepareLenderStatus(dataUpload, fName, false,
								remarks.toString());
						lenderDataStatus.setUploadHeaderId(uploadHeaderId);
						failedCount++;
						lenderDataUploadService.save(lenderDataStatus);
						continue;
					}
				}
			}
		}
		if (emptyExcel) {
			MessageUtil.showError("Lender data not available");
			return;
		}

		// upload header update Data
		this.successCount.setValue(String.valueOf(successCount));
		this.failedCount.setValue(String.valueOf(failedCount));
		totalCount = successCount + failedCount;
		this.totalCount.setValue(String.valueOf(totalCount));
		UploadHeader uploadHeader = new UploadHeader();
		uploadHeader.setSuccessCount(successCount);
		uploadHeader.setFailedCount(failedCount);
		uploadHeader.setTotalRecords(totalCount);
		uploadHeader.setUploadId(uploadHeaderId);
		uploadHeaderService.updateRecord(uploadHeader);
		statusGrid.setVisible(true);
		if (failedCount > 0) {
			MessageUtil.showMessage("Lender data upload successful with failed count: " + failedCount);
		} else {
			MessageUtil.showMessage("Lender data upload successful");
		}
		btndownload.setVisible(true);
		logger.debug(Literal.LEAVING);
	}

	private StringBuilder validateLenderData(LenderDataUpload dataUpload, List<ExtendedFieldHeader> extendedFieldHeader,
			StringBuilder remarks) {
		logger.debug("Entering");

		ExtendedFieldDetail extendedFieldDetail = null;
		boolean isValidRef = true;

		FinanceMain fm = null;

		if (StringUtils.isNotBlank(dataUpload.getFinReference())) {
			fm = financeMainDAO.getFinanceMain(dataUpload.getFinReference());
			if (fm == null) {
				isValidRef = false;
				remarks.append("invalid finReference,");
			}
		} else {
			isValidRef = false;
			remarks.append("FinReference is Mandatory,");
		}

		if (!isValidRef) {
			return remarks;
		}

		boolean availeExtnFields = false;
		for (ExtendedFieldHeader header : extendedFieldHeader) {
			if (StringUtils.equals(header.getSubModuleName(), fm.getFinCategory())) {
				availeExtnFields = true;
				extendedFieldDetail = extendedFieldDetailDAO.getExtendedFieldDetailById(header.getModuleId(),
						App.getProperty("extendedfields"), "");
				if (extendedFieldDetail == null) {
					remarks.append("lenderId id not configured");
				} else {
					if (StringUtils.isNotBlank(dataUpload.getLenderId())) {
						if (dataUpload.getLenderId().length() > extendedFieldDetail.getFieldLength()) {
							remarks.append(
									"lenderId length should not greater than " + extendedFieldDetail.getFieldLength());
						}

						if (StringUtils.isNotBlank(extendedFieldDetail.getFieldConstraint())) {
							if (PennantRegularExpressions
									.getRegexMapper(extendedFieldDetail.getFieldConstraint()) != null) {
								Pattern pattern = Pattern.compile(PennantRegularExpressions
										.getRegexMapper(extendedFieldDetail.getFieldConstraint()));
								Matcher matcher = pattern.matcher(dataUpload.getLenderId());
								if (matcher.matches() == false) {
									remarks.append("Invalid data for lenderId");
								}
							}
						}
					}
				}
				break;
			}
		}

		if (!availeExtnFields) {
			remarks.append("loan reference extended configuration not found");
		}

		logger.debug("Leaving");
		return remarks;
	}

	private String getTableName(String moduleName, String prodCategory, String event) {
		StringBuilder tableName = new StringBuilder();
		tableName.append(moduleName);
		tableName.append("_");
		tableName.append(prodCategory);
		if (event != null) {
			tableName.append("_");
			tableName.append(StringUtils.trimToEmpty(PennantStaticListUtil.getFinEventCode(event)));
		}
		tableName.append("_ED");

		return tableName.toString();
	}

	// prepare Lender data status
	private LenderDataUpload prepareLenderStatus(LenderDataUpload lenderData, String fileName, boolean status,
			String reason) {
		logger.debug(Literal.ENTERING);

		LenderDataUpload lenderDataStatus = new LenderDataUpload();
		lenderDataStatus.setLenderId(lenderData.getLenderId());
		lenderDataStatus.setFinReference(lenderData.getFinReference());
		lenderDataStatus.setReason(reason);
		lenderDataStatus.setStatus(status);

		logger.debug(Literal.LEAVING);
		return lenderDataStatus;
	}

	/**
	 * Reading excel sheet data and converting it's data to String from each cell
	 * 
	 * @param workbook
	 * @param sheetIndex
	 * @param rowindex
	 * 
	 * @return List
	 */

	public List<String> getAllValuesOfRowByIndex(Workbook workbook, int sheetIndex, int rowindex) {
		logger.debug(Literal.ENTERING);
		List<String> keys = new ArrayList<String>();
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		Row headings = sheet.getRow(rowindex);

		for (Cell cell : headings) {
			objFormulaEvaluator.evaluate(cell);
			String cellValueStr = objDefaultFormat.formatCellValue(cell, objFormulaEvaluator);
			keys.add(cellValueStr.trim());
		}
		logger.debug(Literal.LEAVING);
		return keys;
	}

	private long saveUploadHeaderDetails(String fileName) {
		logger.debug(Literal.ENTERING);
		UploadHeader uploadHeader = new UploadHeader();
		uploadHeader.setFileName(fileName);
		uploadHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		uploadHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		uploadHeader.setTransactionDate(SysParamUtil.getAppDate());
		uploadHeader.setModule("LenderDataUpload");
		uploadHeader.setVersion(1);
		logger.debug(Literal.LEAVING);
		return uploadHeaderService.save(uploadHeader);
	}

	public void setUploadHeaderService(UploadHeaderService uploadHeaderService) {
		this.uploadHeaderService = uploadHeaderService;
	}

	public void setLenderDataUploadService(LenderDataUploadService lenderDataUploadService) {
		this.lenderDataUploadService = lenderDataUploadService;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setLenderDataService(LenderDataService lenderDataService) {
		this.lenderDataService = lenderDataService;
	}

	public void setExtendedFieldHeaderDAO(ExtendedFieldHeaderDAO extendedFieldHeaderDAO) {
		this.extendedFieldHeaderDAO = extendedFieldHeaderDAO;
	}

	public void setExtendedFieldDetailDAO(ExtendedFieldDetailDAO extendedFieldDetailDAO) {
		this.extendedFieldDetailDAO = extendedFieldDetailDAO;
	}

}