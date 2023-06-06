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
package com.pennant.webui.finance.upload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.AssignmentPartner;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.assignmentupload.AssignmentUpload;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.service.finance.UploadHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.interfacebajaj.fileextract.service.ExcelFileImport;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/SystemMaster/UploadHeader/AssignmentUploadDialog.zul file. <br>
 * ************************************************************<br>
 */
public class AssignmentUploadDialogCtrl extends GFCBaseCtrl<UploadHeader> {
	private static final long serialVersionUID = -6945930303723518608L;
	private static final Logger logger = LogManager.getLogger(AssignmentUploadDialogCtrl.class);

	protected Window window_AssignmentUploadDialog;
	protected Button btnBrowse;
	protected Button btndownload;

	protected ExtendedCombobox entityCode;
	protected ExtendedCombobox assignmentPartner;

	protected Textbox txtFileName;
	private UploadHeader uploadHeader;
	private transient UploadListCtrl uploadListCtrl;
	private transient boolean validationOn;
	private transient UploadHeaderService uploadHeaderService;

	private Workbook workbook = null;
	private ExcelFileImport fileImport = null;
	private Media media = null;
	private boolean csvFile = false;
	private static final int totalColumns = 4;
	private final String REPORT_NAME = "AssignmentUploadDetails";

	private Set<String> finReferenceList = new HashSet<String>();
	private boolean errorFlag = false;

	/**
	 * default constructor.<br>
	 */
	public AssignmentUploadDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AssignmentUploadDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_AssignmentUploadDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(this.window_AssignmentUploadDialog);

		try {
			// Get the required arguments.
			this.uploadHeader = (UploadHeader) arguments.get("uploadHeader");
			this.uploadListCtrl = (UploadListCtrl) arguments.get("uploadListCtrl");

			if (this.uploadHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			UploadHeader uploadHeader = new UploadHeader();
			BeanUtils.copyProperties(this.uploadHeader, uploadHeader);
			this.uploadHeader.setBefImage(uploadHeader);

			// Render the page and display the data.
			doLoadWorkFlow(this.uploadHeader.isWorkflow(), this.uploadHeader.getWorkflowId(),
					this.uploadHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.uploadHeader);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.txtFileName.setReadonly(true);
		setStatusDetails();

		this.entityCode.setMandatoryStyle(true);
		this.entityCode.setModuleName("Entity");
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });

		this.assignmentPartner.setMandatoryStyle(true);
		this.assignmentPartner.setModuleName("AssignmentPartner");
		this.assignmentPartner.setValueColumn("Code");
		this.assignmentPartner.setDescColumn("Description");
		this.assignmentPartner.setValidateColumns(new String[] { "Code" });

		Filter[] partnerCodeFilter = new Filter[1];
		partnerCodeFilter[0] = new Filter("Active", "1", Filter.OP_EQUAL);
		this.assignmentPartner.setFilters(partnerCodeFilter);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AssignmentUploadDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AssignmentUploadDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AssignmentUploadDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AssignmentUploadDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) {
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	public void onFulfill$entityCode(Event event) {
		logger.debug("Entering");

		Object dataObject = entityCode.getObject();

		if (dataObject instanceof String) {
			this.entityCode.setValue("", "");
			this.assignmentPartner.setValue("", "");
			this.assignmentPartner.setAttribute("partnerId", null);
			filterAssignmentPartner(null);
		} else {
			Entity entity = (Entity) dataObject;

			if (entity == null) {
				filterAssignmentPartner(null);
			} else {
				this.entityCode.setValue(String.valueOf(entity.getEntityCode()),
						String.valueOf(entity.getEntityDesc()));
				this.assignmentPartner.setValue("", "");
				this.assignmentPartner.setAttribute("partnerId", null);
				filterAssignmentPartner(entity.getEntityCode());
			}

		}

		logger.debug("Leaving");

	}

	private void filterAssignmentPartner(String entityCode) {

		Filter[] partnerFilter = null;
		if (StringUtils.isNotBlank(entityCode)) {
			partnerFilter = new Filter[2];
			partnerFilter[0] = new Filter("Active", "1", Filter.OP_EQUAL);
			partnerFilter[1] = new Filter("EntityCode", entityCode, Filter.OP_EQUAL);
		} else {
			partnerFilter = new Filter[1];
			partnerFilter[0] = new Filter("Active", "1", Filter.OP_EQUAL);
		}

		this.assignmentPartner.setFilters(partnerFilter);
	}

	public void onFulfill$assignmentPartner(Event event) {
		logger.debug("Entering");

		Object dataObject = assignmentPartner.getObject();

		if (dataObject instanceof String) {
			this.assignmentPartner.setValue("");
			this.assignmentPartner.setDescription("");
			this.assignmentPartner.setAttribute("partnerId", null);
		} else {
			AssignmentPartner partner = (AssignmentPartner) dataObject;

			if (partner != null) {
				this.assignmentPartner.setAttribute("partnerId", partner.getId());
				this.assignmentPartner.setValue(String.valueOf(partner.getCode()));
				this.assignmentPartner.setDescription(partner.getDescription());

				this.entityCode.setValue(String.valueOf(partner.getEntityCode()),
						String.valueOf(partner.getEntityCodeName()));

				filterAssignmentPartner(partner.getEntityCode());
			}

		}

		logger.debug("Leaving");

	}

	/**
	 * This Method/Event for getting the uploaded document should be comma separated values and then read the document
	 * and setting the values to the Lead VO and added those vos to the List and it also shows the information about
	 * where we go the wrong data
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onUpload$btnBrowse(UploadEvent event) throws Exception {
		logger.debug(Literal.ENTERING);

		this.txtFileName.setText("");
		this.fileImport = null;

		doRemoveValidation();
		media = event.getMedia();

		if (!MediaUtil.isValid(media, DocType.XLS, DocType.XLSX, DocType.CSV)) {
			MessageUtil.showError(Labels.getLabel("upload_document_invalid", new String[] { "excel or csv" }));
			return;
		}

		String fileName = media.getName();
		String filePath = getFilePath();
		if (MediaUtil.isCsv(media)) {
			csvFile = true;
		} else {
			csvFile = false;
			this.fileImport = new ExcelFileImport(media, filePath);
		}
		this.txtFileName.setText(fileName);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "Process" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btndownload(Event event) {
		logger.debug(Literal.ENTERING);

		List<Object> reportList = new ArrayList<Object>();
		reportList.add(uploadHeader.getAssignmentUploads());

		// Excel file downloading automatically using Jasper Report
		String userName = getUserWorkspace().getLoggedInUser().getFullName();
		ReportsUtil.generateExcel(this.REPORT_NAME, null, reportList, userName);

		if (!this.uploadHeader.isFileDownload()) {
			this.uploadHeaderService.updateFileDownload(this.uploadHeader.getUploadId(), true, "_Temp");
			this.uploadHeader.setFileDownload(true);
		}

		logger.debug(Literal.LEAVING);
	}

	private String getFilePath() {
		String filePath = SysParamUtil.getValueAsString("UPLOAD_FILEPATH");
		filePath = filePath.concat(File.separator).concat("Assignment Upload");
		return filePath;
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.uploadHeader.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param uploadHeader
	 * 
	 */
	public void doWriteBeanToComponents(UploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		this.txtFileName.setValue(uploadHeader.getFileName());
		this.entityCode.setValue(uploadHeader.getEntityCode());

		if (!uploadHeader.isNewRecord()) {
			this.entityCode.setValue(StringUtils.trimToEmpty(uploadHeader.getEntityCode()),
					StringUtils.trimToEmpty(uploadHeader.getEntityDesc()));

			this.assignmentPartner.setValue(StringUtils.trimToEmpty(uploadHeader.getAssignmentPartnerCode()),
					StringUtils.trimToEmpty(uploadHeader.getAssignmentPartnerDesc()));
			if (uploadHeader.getAssignmentPartnerCode() != null) {
				this.assignmentPartner.setAttribute("partnerId", uploadHeader.getAssignmentPartnerId());
			} else {
				this.assignmentPartner.setAttribute("partnerId", 0);
			}

			filterAssignmentPartner(uploadHeader.getEntityCode());
			// Assignment Uploads List
			if (CollectionUtils.isNotEmpty(uploadHeader.getAssignmentUploads())) {
				for (AssignmentUpload assignmentUpload : uploadHeader.getAssignmentUploads()) {
					if (!assignmentUpload.isNewRecord()) {
						AssignmentUpload befImage = new AssignmentUpload();
						BeanUtils.copyProperties(assignmentUpload, befImage);
						assignmentUpload.setBefImage(befImage);
					}
				}
			}
		}

		this.recordStatus.setValue(uploadHeader.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aUploadHeader
	 */
	public void doWriteComponentsToBean(UploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<>();

		// Entity Code
		try {
			uploadHeader.setEntityCode(this.entityCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Assignment Partner Code
		try {
			this.assignmentPartner.getValidatedValue();
			Long partnerId = (Long) this.assignmentPartner.getAttribute("partnerId");
			if (partnerId != null) {
				uploadHeader.setAssignmentPartnerId(partnerId);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// File Name
		try {
			if (StringUtils.trimToNull(this.txtFileName.getValue()) == null) {
				throw new WrongValueException(this.txtFileName, Labels.getLabel("empty_file"));
			} else {
				boolean fileExist = this.uploadHeaderService.isFileNameExist(this.txtFileName.getValue());
				if (fileExist) {
					throw new WrongValueException(this.txtFileName,
							this.txtFileName.getValue() + ": file name already Exist.");
				}
			}
			this.uploadHeader.setFileName(this.txtFileName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finReferenceList = new HashSet<String>();
			errorFlag = false;
			if (uploadHeader.isNewRecord()) {
				if (wve.isEmpty()) {
					uploadHeader.setFileName(this.txtFileName.getValue());
					getAssignmentUploads(uploadHeader);
				}
			} else if (CollectionUtils.isNotEmpty(uploadHeader.getAssignmentUploads())) {
				for (AssignmentUpload assignUpload : uploadHeader.getAssignmentUploads()) {
					uploadHeaderService.validateAssignmentScreenLevel(assignUpload, uploadHeader.getEntityCode());
					if (UploadConstants.REFUND_UPLOAD_STATUS_FAIL.equals(assignUpload.getStatus())) {
						errorFlag = true;
					}
				}
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		try {
			if (wve.isEmpty()) {
				if (CollectionUtils.isEmpty(uploadHeader.getAssignmentUploads())) {
					MessageUtil.showError(Labels.getLabel("empty_file"));
					throw new WrongValueException();
				} else {
					String rcdStatus = userAction.getSelectedItem().getValue().toString();
					if (!PennantConstants.RCD_STATUS_RESUBMITTED.equals(rcdStatus)
							&& !PennantConstants.RCD_STATUS_REJECTED.equals(rcdStatus)
							&& !PennantConstants.RCD_STATUS_CANCELLED.equals(rcdStatus) && this.errorFlag) {
						List<Object> reportList = new ArrayList<Object>();
						reportList.add(uploadHeader.getAssignmentUploads());

						// Excel file downloading automatically using Jasper Report
						String userName = getUserWorkspace().getLoggedInUser().getFullName();
						ReportsUtil.generateExcel(this.REPORT_NAME, null, reportList, userName);
						MessageUtil.showError("Some of the records failed, Please check the downloaded file.");
						throw new WrongValueException();
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		uploadHeader.setRecordStatus(this.recordStatus.getValue());

		logger.debug(Literal.LEAVING);
	}

	private void getAssignmentUploads(UploadHeader uploadHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		if (this.csvFile) {
			// CSV File
			processCSVUploadDetails(uploadHeader);
		} else if (this.fileImport != null) {

			this.workbook = this.fileImport.writeFile();

			if (this.workbook != null) {
				List<String> keys = this.fileImport.getRowValuesByIndex(this.workbook, 0, 0, totalColumns);

				if (!("Loan Reference".equalsIgnoreCase(keys.get(0)) && "Assignment Code".equalsIgnoreCase(keys.get(1))
						&& "Asignment Date".equalsIgnoreCase(keys.get(2))
						&& "Effective Date".equalsIgnoreCase(keys.get(3)))) {
					throw new Exception(
							"The uploaded file could not be recognized. Please upload a valid xls or xlsx file.");
				}

				Sheet sheet = this.workbook.getSheetAt(0);
				int noOfRows = sheet.getPhysicalNumberOfRows();
				int maxRecords = SysParamUtil.getValueAsInt("ASSIGNMENT_UPLOAD_MAX_RECORDS");

				if (noOfRows == 0) {
					MessageUtil.showError("File is empty.");
				} else if (noOfRows <= maxRecords + 1) {
					// Process the records
					processExcelUploadDetails(uploadHeader);
					// Back up File
					this.fileImport.backUpFile();
				} else {
					MessageUtil.showError("File should not contain more than " + maxRecords + " records.");
				}
			}
		} else {
			throw new Exception("The uploaded file could not be recognized. Please upload a valid xls or xlsx file.");
		}

		logger.debug(Literal.LEAVING);
	}

	private void processCSVUploadDetails(UploadHeader uploadHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		String filePath = getFilePath();
		File parent = new File(filePath);

		if (!parent.exists()) {
			parent.mkdirs();
		}

		File file = new File(parent.getPath().concat(File.separator).concat(this.media.getName()));
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		FileUtils.writeByteArrayToFile(file, this.media.getByteData());

		try {
			br = new BufferedReader(new FileReader(file));
			int count = 0;
			List<AssignmentUpload> assignmentUploads = new ArrayList<AssignmentUpload>();
			String dateFormat = DateFormat.LONG_DATE.getPattern();

			while ((line = br.readLine()) != null) {

				List<String> row = Arrays.asList(line.split(cvsSplitBy, totalColumns));

				if (row.size() >= totalColumns) {
					if (count == 0) { // Skip Header row
						if (!("Loan Reference ".equalsIgnoreCase(row.get(0))
								&& "Assignment Code".equalsIgnoreCase(row.get(1))
								&& "Asignment Date".equalsIgnoreCase(row.get(2))
								&& "Effective Date".equalsIgnoreCase(row.get(3)))) {
							throw new Exception(
									"The uploaded file could not be recognized. Please upload valid csv file.");
						}
					} else {
						assignmentUploads
								.add(prepareAssignmentUploadBean(row, dateFormat, uploadHeader.getEntityCode()));
					}
				} else {
					// Failure Case
					AssignmentUpload assignmentUpload = new AssignmentUpload();
					assignmentUpload.setNewRecord(true);
					assignmentUpload.setRecordType(PennantConstants.RCD_ADD);
					assignmentUpload.setVersion(assignmentUpload.getVersion() + 1);
					assignmentUpload.setStatus(UploadConstants.REFUND_UPLOAD_STATUS_FAIL);
					assignmentUpload.setRejectReason("Number of columns not matching.");
					assignmentUploads.add(assignmentUpload);
				}

				count++;
			}
			uploadHeader.setAssignmentUploads(assignmentUploads);
			uploadHeader.setTotalRecords(count - 1);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} finally {
			backUpFile(file);
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private AssignmentUpload prepareAssignmentUploadBean(List<String> row, String dateFormat, String entityCode)
			throws ParseException {
		logger.debug(Literal.ENTERING);

		boolean error = false;
		String reason = "";

		String finRefernce = null;
		long assignmentId = 0;
		Date assignmentDate = null;
		Date effectiveDate = null;
		AssignmentUpload assignmentUpload = new AssignmentUpload();
		assignmentUpload.setNewRecord(true);
		assignmentUpload.setRecordType(PennantConstants.RCD_ADD);
		assignmentUpload.setVersion(assignmentUpload.getVersion() + 1);

		// FinReference
		finRefernce = row.get(0);
		if (StringUtils.isBlank(finRefernce)) {
			reason = "Loan Reference is mandatory.";
			error = true;
		} else {
			if (!finReferenceList.contains(finRefernce)) {
				finReferenceList.add(finRefernce);
			} else {
				reason = "Duplicate Loan Reference.";
				error = true;
			}
		}
		assignmentUpload.setFinReference(finRefernce);

		// Assignment Code (Assignment Id)
		if (StringUtils.isNotBlank(row.get(1))) {
			try {
				assignmentId = Long.parseLong(row.get(1));
			} catch (Exception e) {
				if (error) {
					reason = reason + " Invalid Assignment Code.";
				} else {
					reason = "Invalid Assignment Code.";
				}
				error = true;
				assignmentId = 0;
			}

		}
		assignmentUpload.setAssignmentId(assignmentId);

		// Assignment Date
		try {
			assignmentDate = getUtilDate(row.get(2), dateFormat);
		} catch (Exception e) {
			if (error) {
				reason = reason + " Wrong Assignment Date format.";
			} else {
				error = true;
				reason = "Wrong Assignment Date format.";
			}
		}
		assignmentUpload.setAssignmentDate(assignmentDate);

		// Effective Date
		try {
			effectiveDate = getUtilDate(row.get(3), dateFormat);
		} catch (Exception e) {
			if (error) {
				reason = reason + " Wrong Effective Date format.";
			} else {
				error = true;
				reason = "Wrong Effective Date format.";
			}
		}
		assignmentUpload.setEffectiveDate(effectiveDate);

		if (error) {
			assignmentUpload.setStatus(UploadConstants.REFUND_UPLOAD_STATUS_FAIL);
			assignmentUpload.setRejectReason(reason);
			errorFlag = true;
		} else {
			uploadHeaderService.validateAssignmentScreenLevel(assignmentUpload, entityCode);
			if (UploadConstants.REFUND_UPLOAD_STATUS_FAIL.equals(assignmentUpload.getStatus())) {
				errorFlag = true;
			} else {
				assignmentUpload.setStatus(UploadConstants.REFUND_UPLOAD_STATUS_SUCCESS);
			}
		}

		logger.debug(Literal.LEAVING);

		return assignmentUpload;
	}

	private Date getUtilDate(String date, String format) throws ParseException {

		Date uDate = null;
		SimpleDateFormat df = new SimpleDateFormat(format);

		try {
			if (StringUtils.isBlank(date)) {
				return uDate;
			}

			String[] dateformat = date.split("-");

			if (dateformat.length != 3) {
				throw new ParseException(null, 0);
			}

			String dateValue = dateformat[0];
			String month = dateformat[1];
			String year = dateformat[2];

			boolean leapYear = false;

			if (StringUtils.isBlank(dateValue) || StringUtils.isBlank(month) || StringUtils.isBlank(year)) {
				throw new ParseException(null, 0);
			}

			int dateVal = Integer.parseInt(dateValue);

			if (year.length() == 4) {
				int yearValue = Integer.parseInt(year);
				int rem = yearValue % 4;
				if (rem == 0) {
					leapYear = true;
				}
			} else {
				throw new ParseException(null, 0);
			}

			switch (month.toUpperCase()) {
			case "JAN":
			case "MAR":
			case "MAY":
			case "JUL":
			case "AUG":
			case "OCT":
			case "DEC":
				if (dateVal > 31) {
					throw new ParseException(null, 0);
				}
				break;

			case "FEB":
				if (leapYear) {
					if (dateVal > 29) {
						throw new ParseException(null, 0);
					}
				} else {
					if (dateVal > 28) {
						throw new ParseException(null, 0);
					}
				}
				break;

			case "APR":
			case "JUN":
			case "SEP":
			case "NOV":
				if (dateVal > 30) {
					throw new ParseException(null, 0);
				}
				break;

			default:
				throw new ParseException(null, 0);
			}

			uDate = df.parse(date);

		} catch (ParseException e) {
			throw e;
		}

		return uDate;
	}

	/**
	 * entry point of program, reading whole excel and calling other methods to prepare jsonObject.
	 * 
	 * @return String
	 * @throws Exception
	 */
	private void processExcelUploadDetails(UploadHeader uploadHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		List<AssignmentUpload> assignmentUploads = new ArrayList<AssignmentUpload>();
		Sheet sheet = this.workbook.getSheetAt(0);
		Iterator<Row> rows = sheet.iterator();
		String dateFormat = DateFormat.LONG_DATE.getPattern();

		while (rows.hasNext()) {
			Row row = rows.next();
			int rowIndex = row.getRowNum();
			if (rowIndex > 0) {
				List<String> columns = this.fileImport.getRowValuesByIndex(this.workbook, 0, rowIndex, totalColumns);

				if (CollectionUtils.isNotEmpty(columns)) {

					if (columns.size() >= totalColumns) {
						// Success case
						assignmentUploads
								.add(prepareAssignmentUploadBean(columns, dateFormat, uploadHeader.getEntityCode()));
					} else {
						// Fail Case
						AssignmentUpload assignmentUpload = new AssignmentUpload();
						assignmentUpload.setNewRecord(true);
						assignmentUpload.setRecordType(PennantConstants.RCD_ADD);
						assignmentUpload.setVersion(assignmentUpload.getVersion() + 1);
						assignmentUpload.setStatus(UploadConstants.REFUND_UPLOAD_STATUS_FAIL);
						assignmentUpload.setRejectReason("Number of columns not matching.");
						assignmentUploads.add(assignmentUpload);
					}
				}
			}
		}
		uploadHeader.setTotalRecords(sheet.getPhysicalNumberOfRows() - 1);
		uploadHeader.setAssignmentUploads(assignmentUploads);

		logger.debug(Literal.LEAVING);
	}

	public void backUpFile(File file) throws IOException {
		logger.debug(Literal.ENTERING);

		if (file != null) {

			File backupFile = new File(file.getParent() + "/BackUp");

			if (!backupFile.exists()) {
				backupFile.mkdir();
			}

			FileUtils.copyFile(file, new File(backupFile.getPath().concat(File.separator).concat(file.getName())));

			if (file.exists()) {
				if (!file.delete()) {
					file.deleteOnExit();
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aUploadHeader The entity that need to be render.
	 */
	public void doShowDialog(UploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		// set ReadOnly mode accordingly if the object is new or not.
		if (uploadHeader.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(uploadHeader.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}

		// fill the components with the data
		doWriteBeanToComponents(uploadHeader);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(true);

		if (!this.entityCode.isReadonly()) {
			this.entityCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_AssignmentUploadDialog_EntityCode.value"), null, true, true));
		}

		if (!this.assignmentPartner.isReadonly()) {
			this.assignmentPartner.setConstraint(new PTStringValidator(
					Labels.getLabel("label_AssignmentUploadDialog_AssignmentPartner.value"), null, true, true));
		}

		if (isReadOnly("AssignmentUploadDialog_Filename")) {
			this.txtFileName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_AssignmentUploadDialog_Filename.value"), null, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(false);
		this.assignmentPartner.setConstraint("");
		this.entityCode.setConstraint("");
		this.txtFileName.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.assignmentPartner.setErrorMessage("");
		this.entityCode.setErrorMessage("");
		this.txtFileName.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);

		final UploadHeader entity = new UploadHeader();
		BeanUtils.copyProperties(this.uploadHeader, entity);
		String keyReference = Labels.getLabel("label_AssignmentUploadDialog_Filename.value") + " : "
				+ uploadHeader.getFileName();

		doDelete(keyReference, uploadHeader);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.uploadHeader.isNewRecord()) {
			readOnlyComponent(isReadOnly("AssignmentUploadDialog_EntityCode"), this.entityCode);
			readOnlyComponent(isReadOnly("AssignmentUploadDialog_AssignmentPartner"), this.assignmentPartner);
			readOnlyComponent(isReadOnly("button_AssignmentUploadDialog_btnBrowse"), this.btnBrowse);
			this.btnCancel.setVisible(false);
			this.btndownload.setVisible(false);
		} else {
			this.btnBrowse.setVisible(false);
			readOnlyComponent(true, this.entityCode);
			readOnlyComponent(true, this.assignmentPartner);
			readOnlyComponent(true, this.btnBrowse);
			this.btndownload.setVisible(true);
			this.btnCancel.setVisible(true);
		}

		this.txtFileName.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.uploadHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.entityCode);
		readOnlyComponent(true, this.assignmentPartner);
		readOnlyComponent(true, this.txtFileName);
		readOnlyComponent(true, this.btnBrowse);
		readOnlyComponent(true, this.btndownload);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);

		this.entityCode.setValue("", "");
		this.assignmentPartner.setValue("", "");
		this.txtFileName.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final UploadHeader aUploadHeader = new UploadHeader();
		BeanUtils.copyProperties(this.uploadHeader, aUploadHeader);
		boolean isNew;

		// ************************************************************
		// force validation, if on, than execute by component.getValue()
		// ************************************************************
		doSetValidation();
		// fill the UploadHeader object with the components data
		doWriteComponentsToBean(aUploadHeader);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aUploadHeader.isNewRecord();
		String tranType;

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aUploadHeader.getRecordType())) {
				aUploadHeader.setVersion(aUploadHeader.getVersion() + 1);
				if (isNew) {
					aUploadHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aUploadHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aUploadHeader.setNewRecord(true);
				}
			}
		} else {
			aUploadHeader.setVersion(aUploadHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aUploadHeader, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aUploadHeader (UploadHeader)
	 * 
	 * @param tranType      (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(UploadHeader aUploadHeader, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aUploadHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aUploadHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aUploadHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aUploadHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aUploadHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aUploadHeader);
				}

				if (isNotesMandatory(taskId, aUploadHeader)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aUploadHeader.setTaskId(taskId);
			aUploadHeader.setNextTaskId(nextTaskId);
			aUploadHeader.setRoleCode(getRole());
			aUploadHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aUploadHeader, tranType);
			String operationRefs = getServiceOperations(taskId, aUploadHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aUploadHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aUploadHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AuditHeader aAuditHeader = auditHeader;
		UploadHeader aUploadHeader = (UploadHeader) aAuditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					aAuditHeader = uploadHeaderService.delete(aAuditHeader);
					deleteNotes = true;
				} else {
					aAuditHeader = uploadHeaderService.saveOrUpdate(aAuditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {

					if (!uploadHeader.isFileDownload()
							&& !PennantConstants.RECORD_TYPE_DEL.equals(aUploadHeader.getRecordType())) {
						throw new InterfaceException("Error", "File should be downloaded at least once.");
					}

					aAuditHeader = uploadHeaderService.doApprove(aAuditHeader);
					if (aUploadHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					aAuditHeader = uploadHeaderService.doReject(aAuditHeader);

					if (aUploadHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					aAuditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_AssignmentUploadDialog, aAuditHeader);
					return processCompleted;
				}
			}

			aAuditHeader = ErrorControl.showErrorDetails(this.window_AssignmentUploadDialog, aAuditHeader);
			retValue = aAuditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.uploadHeader), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				aAuditHeader.setOveride(true);
				aAuditHeader.setErrorMessage(null);
				aAuditHeader.setInfoMessage(null);
				aAuditHeader.setOverideMessage(null);
			}
		}

		setOverideMap(aAuditHeader.getOverideMap());

		logger.debug(Literal.LEAVING);

		return processCompleted;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aUploadHeader
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(UploadHeader aUploadHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aUploadHeader.getBefImage(), aUploadHeader);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aUploadHeader.getUserDetails(),
				getOverideMap());
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.uploadHeader);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		uploadListCtrl.search();
	}

	@Override
	protected String getReference() {
		return this.uploadHeader.getFileName();
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public void setUploadHeaderService(UploadHeaderService uploadHeaderService) {
		this.uploadHeaderService = uploadHeaderService;
	}
}
