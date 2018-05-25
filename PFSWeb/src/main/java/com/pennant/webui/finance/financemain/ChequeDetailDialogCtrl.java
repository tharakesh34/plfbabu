/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  ChequeDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-11-2017    														*
 *                                                                  						*
 * Modified Date    :  27-11-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-11-2017       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.North;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.pdc.ChequeHeaderService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.pdc.chequeheader.ChequeHeaderListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/pdc/ChequeDetail/chequeDetailDialog.zul file. <br>
 */
public class ChequeDetailDialogCtrl extends GFCBaseCtrl<ChequeHeader> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ChequeDetailDialogCtrl.class);
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ChequeDetailDialog;
	protected North north_Id;

	protected Combobox chequeType;
	protected Intbox totNoOfCheques;
	protected CurrencyBox totAmount;
	protected CurrencyBox amount;
	protected ExtendedCombobox bankBranchID;
	protected Textbox bankName;
	protected Textbox city;
	protected Label cityName;
	protected Textbox micr;
	protected Textbox ifsc;
	protected Textbox accNumber;
	protected Intbox chequeSerialNo;
	protected Intbox noOfCheques;
	protected Groupbox finBasicdetails;
	protected Listbox listBoxChequeDetail;
	protected Label label_ChequeType;
	protected Combobox chequeStatus;
	protected Combobox accountType;
	protected Textbox accHolderName;

	protected Grid grid_chequeDetails;
	protected Grid grid_NumbOfChqs;
	protected Button btnGen;

	private ChequeDetail chequeDetail;
	private boolean fromLoan = false;
	private ChequeHeader chequeHeader;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private Object financeMainDialogCtrl = null;
	private final List<ValueLabel> chequeTypeList = PennantStaticListUtil.getChequeTypes();
	private final List<ValueLabel> chequeStatusList = PennantStaticListUtil.getChequeStatusList();
	private final List<ValueLabel> accTypeList = PennantStaticListUtil.getChequeAccTypeList();
	private FinanceDetail financeDetail;
	private List<ChequeDetail> chequeDetailList;
	private Tab parenttab = null;
	private int accNoLength;
	private int ccyEditField = PennantConstants.defaultCCYDecPos;
	private String ccy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);
	private BankDetailService bankDetailService;
	private List<FinanceScheduleDetail> financeSchedules = new ArrayList<>();
	private List<ChequeDetail> chequeDocuments = new ArrayList<>();
	private boolean isPDC;
	private boolean onclickGenBtn = false;

	private ChequeHeaderListCtrl chequeHeaderListCtrl;
	private ChequeHeaderService chequeHeaderService;

	/**
	 * default constructor.<br>
	 */
	public ChequeDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ChequeDetailDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.chequeHeader.getHeaderID()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_ChequeDetailDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ChequeDetailDialog);

		try {
			if (arguments.containsKey("chequeHeader")) {
				this.chequeHeader = (ChequeHeader) arguments.get("chequeHeader");
				setChequeHeader(chequeHeader);
			}

			if (arguments.containsKey("fromLoan")) {
				fromLoan = (Boolean) arguments.get("fromLoan");
			}

			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
			}

			if (arguments.containsKey("finHeaderList")) {
				appendFinBasicDetails((ArrayList<Object>) arguments.get("finHeaderList"));
			} else {
				setFinanceDetail(chequeHeaderService.getFinanceDetailById(getChequeHeader().getFinReference()));
				appendFinBasicDetails(getFinBasicDetails(getFinanceDetail()));
			}

			if (fromLoan) {
				if (arguments.containsKey("financeDetail")) {
					setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
					if (getFinanceDetail().getChequeHeader() != null) {
						setChequeHeader(getFinanceDetail().getChequeHeader());
					}
				}
				if (arguments.containsKey("financeMainDialogCtrl")) {
					setFinanceMainDialogCtrl(arguments.get("financeMainDialogCtrl"));
				}

				if (arguments.containsKey("tab")) {
					parenttab = (Tab) arguments.get("tab");
				}
				setFinanceSchedules(getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails());
				this.ccy = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy();
				this.ccyEditField = CurrencyUtil.getFormat(ccy);
			} else {
				this.chequeHeaderListCtrl = (ChequeHeaderListCtrl) arguments.get("chequeHeaderListCtrl");
				setFinanceSchedules((List<FinanceScheduleDetail>) arguments.get("financeSchedules"));
				// Render the page and display the data.
				doLoadWorkFlow(this.chequeHeader.isWorkflow(), this.chequeHeader.getWorkflowId(), this.chequeHeader.getNextTaskId());

				if (isWorkFlowEnabled()) {
					if (!enqiryModule) {
						this.userAction = setListRecordStatus(this.userAction);
					}
					getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
				} else {
					getUserWorkspace().allocateAuthorities(this.pageRightName, null);
				}
				
				if(getChequeHeader() != null)  {
					List<ChequeDetail> chequeDetails = getChequeHeader().getChequeDetailList();
					if (chequeDetails != null && !chequeDetails.isEmpty()) {
						ChequeDetail chequeDetail = chequeDetails.get(0);
						this.ccy = chequeDetail.getChequeCcy();
						this.ccyEditField = CurrencyUtil.getFormat(ccy);
					}
				}
			}
			if (arguments.containsKey("roleCode")) {
				getUserWorkspace().allocateRoleAuthorities(arguments.get("roleCode").toString(), "ChequeHeaderDialog");
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(getChequeHeader());
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
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

		this.bankBranchID.setModuleName("BankBranch");
		this.bankBranchID.setMandatoryStyle(true);
		this.bankBranchID.setValueColumn("BranchCode");
		this.bankBranchID.setDescColumn("BankName");
		this.bankBranchID.setDisplayStyle(2);
		this.bankBranchID.setValidateColumns(new String[] { "BranchCode" });
		this.bankBranchID.setTextBoxWidth(180);
		
		this.chequeType.setSclass(PennantConstants.mandateSclass);
		this.chequeSerialNo.setMaxlength(6);
		this.accNumber.setMaxlength(50);
		this.noOfCheques.setMaxlength(2);
		this.accHolderName.setMaxlength(200);
		this.totAmount.setProperties(false, ccyEditField);
		this.totAmount.setReadonly(true);
		this.amount.setProperties(false, ccyEditField);
		this.amount.setTextBoxWidth(200);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(false);
		this.btnEdit.setVisible(false);
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ChequeDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws ParseException
	 */
	public void onClick$btnSave(Event event) throws ParseException {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.chequeHeader);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		chequeHeaderListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.chequeHeader.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws ParseException
	 */
	public void doSave() throws ParseException {
		logger.debug(Literal.ENTERING);

		final ChequeHeader aChequeHeader = new ChequeHeader();
		BeanUtils.copyProperties(this.chequeHeader, aChequeHeader);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aChequeHeader, false);

		aChequeHeader.setRecordStatus(this.recordStatus.getValue());
		isNew = aChequeHeader.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aChequeHeader.getRecordType())) {
				aChequeHeader.setVersion(aChequeHeader.getVersion() + 1);
				if (isNew) {
					aChequeHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aChequeHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aChequeHeader.setNewRecord(true);
				}
			}
		} else {
			aChequeHeader.setVersion(aChequeHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		try {
			if (doProcess(aChequeHeader, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a ChequeHeader object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final ChequeHeader aChequeHeader = new ChequeHeader();
		BeanUtils.copyProperties(this.chequeHeader, aChequeHeader);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aChequeHeader.getHeaderID();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aChequeHeader.getRecordType()).equals("")) {
				aChequeHeader.setVersion(aChequeHeader.getVersion() + 1);
				aChequeHeader.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aChequeHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aChequeHeader.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aChequeHeader.getNextTaskId(),
							aChequeHeader);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aChequeHeader, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(ChequeHeader aChequeHeader, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aChequeHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aChequeHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aChequeHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aChequeHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aChequeHeader.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aChequeHeader);
				}
				if (isNotesMandatory(taskId, aChequeHeader)) {
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
			aChequeHeader.setTaskId(taskId);
			aChequeHeader.setNextTaskId(nextTaskId);
			aChequeHeader.setRoleCode(getRole());
			aChequeHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aChequeHeader, tranType);
			String operationRefs = getServiceOperations(taskId, aChequeHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aChequeHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aChequeHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		ChequeHeader aChequeHeader = (ChequeHeader) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = chequeHeaderService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = chequeHeaderService.saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = chequeHeaderService.doApprove(auditHeader);
						if (aChequeHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = chequeHeaderService.doReject(auditHeader);
						if (aChequeHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ChequeDetailDialog, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(this.window_ChequeDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.chequeHeader), true);
					}
				}
				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		setOverideMap(auditHeader.getOverideMap());
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	public void onFulfill$bankBranchID(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		Object dataObject = this.bankBranchID.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.bankName.setValue("");
			this.city.setValue("");
			this.micr.setValue("");
			this.ifsc.setValue("");
			this.cityName.setValue("");
		} else {
			BankBranch details = (BankBranch) dataObject;
			if (details != null) {
				this.bankBranchID.setAttribute("bankBranchDetails", details);
				this.bankName.setValue(details.getBankName());
				this.micr.setValue(details.getMICR());
				this.ifsc.setValue(details.getIFSC());
				this.city.setValue(details.getCity());
				this.cityName.setValue(details.getPCCityName());
				if (StringUtils.isNotBlank(details.getBankName())) {
					this.accNoLength = bankDetailService.getAccNoLengthByCode(details.getBankCode());
				}
				if (accNoLength!=0) {
					this.accNumber.setMaxlength(accNoLength);	
				}else{
					this.accNumber.setMaxlength(LengthConstants.LEN_ACCOUNT);	
				}
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param chequeDetail
	 * 
	 */
	public void doWriteBeanToComponents(ChequeHeader aChequeHeader) {
		logger.debug(Literal.ENTERING);

		fillComboBox(this.chequeType, "", chequeTypeList, "");
		if (fromLoan) {
			fillComboBox(this.chequeStatus, PennantConstants.CHEQUESTATUS_NEW, chequeStatusList, "");
		} else {
			fillComboBox(this.chequeStatus, "", chequeStatusList, "");
		}
		fillComboBox(this.accountType, "", accTypeList, "");

		List<ChequeDetail> chequeDetails = aChequeHeader.getChequeDetailList();
		if (chequeDetails != null && !chequeDetails.isEmpty()) {
			ChequeDetail chequeDetail = chequeDetails.get(0);

			fillComboBox(this.chequeType, chequeDetail.getChequeType(), chequeTypeList, "");
			fillComboBox(this.chequeStatus, chequeDetail.getChequeStatus(), chequeStatusList, "");
			fillComboBox(this.accountType, chequeDetail.getAccountType(), accTypeList, "");
			this.accHolderName.setValue(chequeDetail.getAccHolderName());
			this.accNumber.setValue(chequeDetail.getAccountNo());
			this.chequeSerialNo.setValue(chequeDetail.getChequeSerialNo());
			this.amount.setValue(PennantApplicationUtil.formateAmount(chequeDetail.getAmount(), ccyEditField));
			this.noOfCheques.setValue(aChequeHeader.getNoOfCheques());

			BankBranch details = new BankBranch();
			if (details != null) {
				this.bankBranchID.setAttribute("bankBranchDetails", details);
				details.setBankCode(chequeDetail.getBranchCode());
				details.setBankBranchID(chequeDetail.getBankBranchID());
				details.setBankName(chequeDetail.getBankName());
				details.setMICR(chequeDetail.getMicr());
				details.setIFSC(chequeDetail.getIfsc());
				details.setCity(chequeDetail.getCity());
			}
			this.bankBranchID.setValue(chequeDetail.getBranchCode());
			this.bankBranchID.setDescription(chequeDetail.getBankName());
			this.bankName.setValue(chequeDetail.getBankName());
			this.micr.setValue(chequeDetail.getMicr());
			this.ifsc.setValue(chequeDetail.getIfsc());
			this.city.setValue(chequeDetail.getCity());
		}

		this.totNoOfCheques.setValue(aChequeHeader.getNoOfCheques());
		this.totAmount.setValue(PennantApplicationUtil.formateAmount(aChequeHeader.getTotalAmount(), ccyEditField));

		doFillChequeDetails(listBoxChequeDetail, aChequeHeader.getChequeDetailList());
		this.recordStatus.setValue(aChequeHeader.getRecordStatus());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aChequeDetail
	 * @throws ParseException
	 */
	public ArrayList<WrongValueException> doWriteComponentsToBean(ChequeHeader chequeHeader, boolean isGenarate) throws ParseException {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Total noOfCheques
		try {
			chequeHeader.setNoOfCheques(this.totNoOfCheques.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Total Cheque Amount
		try {
			chequeHeader
					.setTotalAmount(PennantAppUtil.unFormateAmount(this.totAmount.getValidateValue(), ccyEditField));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// cheque Type
		try {
			this.chequeType.getValue().toString();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Active
		try {
			chequeHeader.setActive(true);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// bankBranchID
		try {
			this.bankBranchID.getValidatedValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Amount
		try {
			this.amount.getValidateValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// accNumber
		try {
			this.accNumber.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// chequeSerialNo
		try {
			this.chequeSerialNo.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// noOfCheques
		try {
			this.noOfCheques.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Validating the child details
		if (!isGenarate) {
			ArrayList<WrongValueException> exceptions = doPrepareList(this.listBoxChequeDetail, chequeHeader);
			if (!exceptions.isEmpty()) {
				wve.addAll(exceptions);
			}
			if (wve.isEmpty()) {
				// validate existing data
				validateChequeDetails(chequeHeader.getChequeDetailList(), true);
			}
		}
		doRemoveValidation();
		doRemoveLOVValidation();
		if (!wve.isEmpty() && parenttab != null) {
			parenttab.setSelected(true);
		}
		showErrorDetails(wve);

		logger.debug(Literal.LEAVING);
		return wve;
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param chequeDetail
	 *            The entity that need to be render.
	 */
	public void doShowDialog(ChequeHeader chequeHeader) {
		logger.debug(Literal.LEAVING);

		if (chequeHeader != null && !chequeHeader.isNewRecord()) {
			if (StringUtils.equals(chequeHeader.getRecordStatus(), PennantConstants.RCD_STATUS_SUBMITTED)) {
				readOnlyComponent(true, this.chequeStatus);
				readOnlyComponent(true, this.chequeType);
				readOnlyComponent(true, this.totAmount);
				readOnlyComponent(true, this.totNoOfCheques);
				readOnlyComponent(true, this.bankBranchID);
				readOnlyComponent(true, this.chequeSerialNo);
				readOnlyComponent(true, this.accNumber);
				readOnlyComponent(true, this.amount);
				readOnlyComponent(true, this.noOfCheques);
				readOnlyComponent(true, this.btnGen);
				readOnlyComponent(true, this.accountType);
				readOnlyComponent(true, this.accHolderName);
			}
		} else {
			if (fromLoan) {
				readOnlyComponent(true, this.chequeStatus);
			} else {
				readOnlyComponent(false, this.chequeStatus);
			}
		}
		doWriteBeanToComponents(chequeHeader);
		try {
			// fill the components with the data
			if (fromLoan) {
				try {
					getFinanceMainDialogCtrl().getClass().getMethod("setChequeDetailDialogCtrl", this.getClass()).invoke(getFinanceMainDialogCtrl(), this);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
				if (parenttab != null) {
					boolean isChqCaptureReq = financeDetail.getFinScheduleData().getFinanceType().isChequeCaptureReq();
					FinanceMain aFinanceMain = financeDetail.getFinScheduleData().getFinanceMain();
					
					if (isChqCaptureReq) {
						checkTabDisplay(financeDetail, aFinanceMain.getFinRepayMethod(), false);
					} else if (chequeHeader.getChequeDetailList() != null	&& !chequeHeader.getChequeDetailList().isEmpty()) {
						checkTabDisplay(financeDetail, aFinanceMain.getFinRepayMethod(), true);
					}
				}
			} else {
				this.north_Id.setVisible(true);
				setDialog(DialogType.EMBEDDED);
			}
			this.btnSave.setVisible(true);
			int listBoxHeight = this.grid_chequeDetails.getRows().getVisibleItemCount() + this.grid_NumbOfChqs.getRows().getVisibleItemCount() + 5;
			//in maintenance there is no tabs so decrease the height.
			if (!fromLoan) {
				listBoxHeight--;
			}
			this.listBoxChequeDetail.setHeight(getListBoxHeight(listBoxHeight));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		// cheque Type
		if (!this.chequeType.isDisabled() && onclickGenBtn) {
			this.chequeType.setConstraint(new StaticListValidator(chequeTypeList,
					Labels.getLabel("label_ChequeDetailDialog_ChequeType.value")));
		}

		if (isPDC || onclickGenBtn ||!fromLoan) {
			// Number of cheques
			if (!this.totNoOfCheques.isReadonly()) {
				this.totNoOfCheques.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_ChequeDetailDialog_NoOfCheques.value"), true, false));
			}
			// Total Amount
			if (!this.totAmount.isReadonly()) {
				this.totAmount.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_ChequeDetailDialog_Amount.value"), ccyEditField, true, false));
			}
			
			//Cheque Type
			if (!this.chequeType.isDisabled()) {
				this.chequeType.setConstraint(new PTListValidator(Labels.getLabel("label_ChequeDetailDialog_ChequeType.value"), chequeTypeList, true));
			}
			
			// Bank Branch ID
			if (!this.bankBranchID.isReadonly()) {
				this.bankBranchID.setConstraint(new PTStringValidator(
						Labels.getLabel("label_ChequeDetailDialog_BankBranchID.value"), null, true, true));
			}
			// Amount Cheque Detail
			if (!this.amount.isReadonly()) {
				this.amount.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_ChequeDetailDialog_Amount.value"), ccyEditField, true, false));
			}
			// Account Number
			if (!this.accNumber.isReadonly()) {
				this.accNumber.setConstraint(
						new PTStringValidator(Labels.getLabel("label_ChequeDetailDialog_AccNumber.value"),
								PennantRegularExpressions.REGEX_ACCOUNTNUMBER, true, this.accNoLength));
			}
			// Cheque Serial number
			if (!this.chequeSerialNo.isReadonly()) {
				this.chequeSerialNo.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_ChequeDetailDialog_ChequeSerialNo.value"), true, false));
			}
			// Amount Cheque Detail
			if (!this.noOfCheques.isReadonly()) {
				this.noOfCheques.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_ChequeDetailDialog_NoOfChequesCalc.value"), true, false));
			}
			//if the user not interested to generate cheques in after getting the validation.
			if (onclickGenBtn) {
				onclickGenBtn = false;
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		Clients.clearWrongValue(this.totNoOfCheques);
		Clients.clearWrongValue(this.noOfCheques);
		this.totNoOfCheques.setConstraint("");
		this.totAmount.setConstraint("");
		this.bankBranchID.setConstraint("");
		this.accNumber.setConstraint("");
		this.chequeSerialNo.setConstraint("");
		this.amount.setConstraint("");
		this.noOfCheques.setConstraint("");

		this.totNoOfCheques.clearErrorMessage();
		this.totAmount.clearErrorMessage();
		this.bankBranchID.clearErrorMessage();
		this.accNumber.clearErrorMessage();
		this.chequeSerialNo.clearErrorMessage();
		this.amount.clearErrorMessage();
		this.noOfCheques.clearErrorMessage();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog
	 * controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(ArrayList<Object> finHeaderList) {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			if (finHeaderList != null) {
				map.put("finHeaderList", finHeaderList);
			}
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (getChequeHeader().isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.bankBranchID);
			readOnlyComponent(false, this.chequeSerialNo);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.bankBranchID);
			readOnlyComponent(true, this.chequeSerialNo);
		}
		readOnlyComponent(isReadOnly("ChequeDetailDialog_Amount"), this.totAmount);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.chequeDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();
		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			if (parenttab != null) {
				parenttab.setSelected(true);
			}
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.bankBranchID);
		readOnlyComponent(true, this.chequeSerialNo);
		readOnlyComponent(true, this.totAmount);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
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

		this.bankBranchID.setValue("");
		this.totAmount.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws ParseException
	 */
	public void doSave_PDC(FinanceDetail financeDetail, String finReference) throws ParseException {
		logger.debug(Literal.ENTERING);

		final ChequeHeader aChequeHeader = new ChequeHeader();
		BeanUtils.copyProperties(getChequeHeader(), aChequeHeader);
		boolean isNew = false;
		String rcdStatus = financeDetail.getFinScheduleData().getFinanceMain().getRecordStatus();

		
		doRemoveValidation();
		doSetValidation();
		ArrayList<WrongValueException> wve = doWriteComponentsToBean(aChequeHeader,false);
		if (!wve.isEmpty() && parenttab != null) {
			parenttab.setSelected(true);
		} else {
			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			if (FinanceConstants.REPAYMTH_PDC.equals(financeMain.getFinRepayMethod())) {
				int noOfSchedules = getFinanceSchedules().size();
				noOfSchedules = noOfSchedules-1;
				int noOfPDCCheques = SysParamUtil.getValueAsInt(SMTParameterConstants.NUMBEROF_PDC_CHEQUES);
				int number;
				if (noOfSchedules >= noOfPDCCheques) {
					number = noOfPDCCheques;
				} else {
					number = noOfSchedules;
				}
				if (this.totNoOfCheques.intValue() > number) {
					parenttab.setSelected(true);
					throw new WrongValueException(this.totNoOfCheques,
							Labels.getLabel("NUMBER_MINVALUE_EQ",
									new String[] { Labels.getLabel("label_ChequeDetailDialog_NoOfCheques.value"),
											String.valueOf(number) }));
				}
			} else if (FinanceConstants.REPAYMTH_UDC.equals(financeMain.getFinRepayMethod())) {
				int noOfUndateCHeques = SysParamUtil.getValueAsInt(SMTParameterConstants.NUMBEROF_UNDATED_CHEQUES);
				if (this.totNoOfCheques.intValue() < noOfUndateCHeques) {
					parenttab.setSelected(true);
					throw new WrongValueException(this.totNoOfCheques,
							Labels.getLabel("NUMBER_MINVALUE_EQ",
									new String[] { Labels.getLabel("label_ChequeDetailDialog_NoOfCheques.value"),
											String.valueOf(noOfUndateCHeques) }));
				}

			}

		}
		showErrorDetails(wve);

		isNew = aChequeHeader.isNew();

		if (StringUtils.isBlank(aChequeHeader.getRecordType())) {
			aChequeHeader.setVersion(aChequeHeader.getVersion() + 1);
			aChequeHeader.setRecordStatus(rcdStatus);
			if (isNew) {
				aChequeHeader.setNewRecord(true);
				aChequeHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			} else {
				aChequeHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			}
		}
		aChequeHeader.setFinReference(finReference);
		aChequeHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
		aChequeHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aChequeHeader.setUserDetails(getUserWorkspace().getLoggedInUser());
		aChequeHeader.setTaskId(getTaskId());
		aChequeHeader.setNextTaskId(getNextTaskId());
		aChequeHeader.setRoleCode(getRole());
		aChequeHeader.setNextRoleCode(getNextRoleCode());
		for (ChequeDetail chequeDetail : aChequeHeader.getChequeDetailList()) {
			chequeDetail.setVersion(aChequeHeader.getVersion() + 1);
			chequeDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
			chequeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			chequeDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
			chequeDetail.setTaskId(getTaskId());
			chequeDetail.setNextTaskId(getNextTaskId());
			chequeDetail.setRoleCode(getRole());
			chequeDetail.setNextRoleCode(getNextRoleCode());
			chequeDetail.setRecordStatus(rcdStatus);
		}

		logger.debug(Literal.LEAVING);
		financeDetail.setChequeHeader(aChequeHeader);
	}

	public void onClick$btnGen(Event event) throws ParseException {
		logger.debug(Literal.ENTERING);
		doRemoveValidation();

		onclickGenBtn = true;
		doSetValidation();
		this.amount.getActualValue();
		doWriteComponentsToBean(new ChequeHeader(), true);

		// method to validate
		if (StringUtils.trimToNull(this.bankBranchID.getValue()) != null) {
			List<ChequeDetail> chequeDetails = new ArrayList<>();

			int chequeSerialNum = this.chequeSerialNo.intValue();
			int numberofCheques = this.noOfCheques.getValue();
			int prvsNoOfCheques = this.totNoOfCheques.getValue();
			BigDecimal totalChequeAmt = this.totAmount.getActualValue();
			String chequeType = this.chequeType.getSelectedItem().getValue().toString();
			
			for (int i = 0; i < numberofCheques; i++) {
				ChequeDetail cheqDetails = new ChequeDetail();

				cheqDetails.setAccountNo(this.accNumber.getValue());
				cheqDetails.setChequeSerialNo(chequeSerialNum);
				chequeSerialNum++;
				
				Object bankBranch = this.bankBranchID.getAttribute("bankBranchDetails");
				if (bankBranch != null) {
					BankBranch branch=  (BankBranch)bankBranch;
					cheqDetails.setBankBranchID(branch.getBankBranchID());
					cheqDetails.setBranchCode(branch.getBranchCode());
				}
				cheqDetails.setBankName(this.bankName.getValue());
				cheqDetails.setAccountNo(this.accNumber.getValue());
				cheqDetails.setAmount(PennantApplicationUtil.unFormateAmount(this.amount.getActualValue(), ccyEditField));
				totalChequeAmt = totalChequeAmt.add(this.amount.getActualValue());
				cheqDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				cheqDetails.setNewRecord(true);
				cheqDetails.setActive(true);
				cheqDetails.setChequeType(chequeType);
				cheqDetails.setAccHolderName(this.accHolderName.getValue());
				cheqDetails.setAccountType(this.accountType.getSelectedItem().getValue().toString());
				cheqDetails.setChequeStatus(this.chequeStatus.getSelectedItem().getValue().toString());

				chequeDetails.add(cheqDetails);
			}

			// validate existing data
			validateChequeDetails(chequeDetails, false);
			
			this.totNoOfCheques.setValue(prvsNoOfCheques + numberofCheques);
			this.totAmount.setValue(totalChequeAmt);
			
			doFillChequeDetails(this.listBoxChequeDetail, chequeDetails);
			logger.debug(Literal.LEAVING);
		}
	}

	private void doFillChequeDetails(Listbox listBoxChequeDetail, List<ChequeDetail> chequeDetails) {
		boolean isReadOnly = PennantConstants.RCD_STATUS_SUBMITTED.equals(getChequeHeader().getRecordStatus()) ;
		
		if (chequeDetails != null && chequeDetails.size() > 0) {
			for (ChequeDetail chequeDetail : chequeDetails) {

				Listitem listitem = new Listitem();
				listitem.setAttribute("data", chequeDetail);
				Listcell listcell;

				// ChequeType
				listcell = new Listcell(String.format(chequeDetail.getChequeType()));
				listcell.setParent(listitem);

				// ChequeSerialNo
				listcell = new Listcell(String.format("%06d", chequeDetail.getChequeSerialNo()));
				listcell.setParent(listitem);

				// Bank branch id
				listcell = new Listcell();
				ExtendedCombobox bankBranchID = new ExtendedCombobox();
				bankBranchID.setModuleName("BankBranch");
				bankBranchID.setReadonly(true);
				bankBranchID.setValueColumn("BranchCode");
				bankBranchID.setDescColumn("BankName");
				bankBranchID.setDisplayStyle(2);
				bankBranchID.setValidateColumns(new String[] { "BranchCode" });
				bankBranchID.setWidth("100px");
				
				bankBranchID.setValue(chequeDetail.getBranchCode());
				bankBranchID.setDescription(chequeDetail.getBankName());
				listcell.appendChild(bankBranchID);
				listcell.setParent(listitem);
				

				// AccountNo
				listcell = new Listcell(chequeDetail.getAccountNo());
				listcell.setParent(listitem);

				// Emi ref
				listcell = new Listcell();
				Combobox emiReference = getCombobox("1");
				Combobox emi = getCombobox(String.valueOf(chequeDetail.geteMIRefNo()));
				emiReference.setValue(emi.getSelectedItem().getLabel());
				if (!MandateConstants.TYPE_PDC.equals(chequeDetail.getChequeType())) {
					readOnlyComponent(true, emiReference);
				} else {
					readOnlyComponent(isReadOnly, emiReference);
				}
				emiReference.setWidth("100px");
				emi.setWidth("100px");
				listcell.appendChild(emiReference);
				listcell.setParent(listitem);

				// Amount
				listcell = new Listcell();
				CurrencyBox emiAmount = new CurrencyBox();
				emiAmount.setProperties(false, ccyEditField);
				emiAmount.setValue(PennantApplicationUtil.formateAmount(chequeDetail.getAmount(), ccyEditField));
				emiAmount.setTextBoxWidth(100);
				readOnlyComponent(isReadOnly, emiAmount);
				listcell.appendChild(emiAmount);
				listcell.setParent(listitem);

				// Delete action
				listcell = new Listcell();
				Button delButton = new Button(Labels.getLabel("ChequeDetailDialog_Delete"));
				Object[] deleteItem = new Object[1];
				deleteItem[0] = listitem;
				readOnlyComponent(isReadOnly, delButton);
				listcell.appendChild(delButton);
				listcell.setParent(listitem);

				// Upload image action
				listcell = new Listcell();
				Button uploadButton = new Button(Labels.getLabel("ChequeDetailDialog_Upload"));
				Object[] uploadItem = new Object[1];
				uploadItem[0] = listitem;
				readOnlyComponent(isReadOnly, uploadButton);
				listcell.appendChild(uploadButton);
				listcell.setParent(listitem);

				// ChequeStatus listCell 8
				listcell = new Listcell();
				Combobox chequeStatus = getChequeStatusComboBox(chequeDetail.getChequeStatus());
				chequeStatus.setWidth("100px");
				readOnlyComponent(isReadOnly, chequeStatus);
				listcell.appendChild(chequeStatus);
				listcell.setParent(listitem);

				// AccountType listCell 09
				Combobox accTypecmbbox = new Combobox();
				accTypecmbbox.setWidth("130px");
				fillComboBox(accTypecmbbox, chequeDetail.getAccountType(), accTypeList, "");
				readOnlyComponent(isReadOnly, accTypecmbbox);
				listcell = new Listcell();
				listcell.appendChild(accTypecmbbox);
				listcell.setParent(listitem);

				// AccHolderName listCell 10
				listcell = new Listcell(chequeDetail.getAccHolderName());
				listcell.setParent(listitem);
				listBoxChequeDetail.appendChild(listitem);

				List<Object> list = new ArrayList<Object>(11);
				list.add(chequeDetail); 
				list.add(emiAmount);  
				list.add(deleteItem); 
				list.add(getComboboxValue(emiReference));  
				list.add(chequeStatus); 

				emiAmount.addForward("onFulfill", this.window_ChequeDetailDialog, "onFulfill$EmiAmount", list);
				delButton.addForward("onClick", this.window_ChequeDetailDialog, "onClickDeleteButton", list);
				uploadButton.addForward("onClick", this.window_ChequeDetailDialog, "onClickUploadButton", list);
				emiReference.addForward("onChange", this.window_ChequeDetailDialog, "onChangeEmiDate", list);
				chequeStatus.addForward("onChange", this.window_ChequeDetailDialog, "onChangeChequeStatus", list);
			}
		}
	}
	/**
	 * Method for validating
	 * 
	 * @param chequeDetails
	 * @param validate
	 * @throws ParseException
	 */
	private void validateChequeDetails(List<ChequeDetail> chequeDetails, boolean validate) throws ParseException {
		logger.debug(Literal.ENTERING);

		for (Listitem listitem : listBoxChequeDetail.getItems()) {
			List<Listcell> list = listitem.getChildren();
			
			Listcell chequeType = list.get(0);
			if (!validate) {
				for (ChequeDetail chequeDetail : chequeDetails) {

					Listcell chkSerial = list.get(1);
					Listcell extListCell = list.get(2);
					ExtendedCombobox extendedCombobox = (ExtendedCombobox) extListCell.getFirstChild();

					// validate cheque serial number
					if (!StringUtils.equals(chequeDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
						if (StringUtils.equals(chkSerial.getLabel().toString(), String.format("%06d", chequeDetail.getChequeSerialNo()))) {
							if (fromLoan) {
								parenttab.setSelected(true);
							}
							throw new WrongValueException(extendedCombobox, Labels.getLabel("ChequeDetailDialog_ChkSerial_Exists"));
						}
					}
				}
			} else if (StringUtils.equals(FinanceConstants.REPAYMTH_PDC, chequeType.getLabel())) {
				// Validation of Cheque EMI Reference.
				Listcell emiDateLc = list.get(4);
				Combobox emiComboBox = (Combobox) emiDateLc.getFirstChild();

				int emiRefNumCnt = 0;
				for (ChequeDetail chequeDetail : chequeDetails) {
					if(PennantConstants.RCD_STATUS_CANCELLED.equals(chequeDetail.getRecordStatus())){
						continue;
					}
					String emiRefNum = getComboboxValue(emiComboBox);
					if (StringUtils.isNumeric(emiRefNum) &&  (Integer.parseInt(emiRefNum) == chequeDetail.geteMIRefNo())) {
						emiRefNumCnt++;
						if (emiRefNumCnt > 1) {
							if (fromLoan) {
								parenttab.setSelected(true);
							}
							throw new WrongValueException(emiComboBox, Labels.getLabel("ChequeDetailDialog_ChkEMIRef_Exists"));
						}
					}
				}

				// Validation of Cheque EMI Amount.
				Combobox comboItem = getCombobox(getComboboxValue(emiComboBox));
				Date emiDate = DateUtility.parse(comboItem.getSelectedItem().getLabel(), PennantConstants.dateFormat);
				if (getFinanceSchedules() != null) {
					List<FinanceScheduleDetail> schedules = getFinanceSchedules();
					Listcell emiAmountLc = list.get(5);
					CurrencyBox emiAmount = (CurrencyBox) emiAmountLc.getFirstChild();

					for (FinanceScheduleDetail detail : schedules) {
						if (DateUtility.compare(emiDate, detail.getSchDate()) == 0) {

							BigDecimal repayAmount = PennantApplicationUtil.formateAmount(detail.getRepayAmount(), CurrencyUtil.getFormat(detail.getFinCcy()));
							if (repayAmount.compareTo(emiAmount.getActualValue()) != 0) {
								if (fromLoan) {
									parenttab.setSelected(true);
								}
								throw new WrongValueException(emiAmount, Labels.getLabel("ChequeDetailDialog_EMI_Amount"));
							}
						}
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$EmiAmount(Event event) {
		logger.debug(Literal.ENTERING);

		BigDecimal totalChequeAmt = BigDecimal.ZERO;
		int noOfCheques = 0;

		@SuppressWarnings("unchecked")
		List<Object> list1 = (List<Object>) event.getData();
		ChequeDetail chequeDetail = (ChequeDetail) list1.get(0);
		CurrencyBox emiAmount1 = (CurrencyBox) list1.get(1);

		for (Listitem listitem : listBoxChequeDetail.getItems()) {
			List<Listcell> list = listitem.getChildren();
			Listcell emiAmtLc = list.get(5);
			CurrencyBox emiAmount = (CurrencyBox) emiAmtLc.getFirstChild();
			if (!emiAmount.isReadonly()) {
				if (emiAmount.getActualValue() == null || emiAmount.getActualValue().compareTo(BigDecimal.ZERO) < 0) {
					emiAmount.setValue(BigDecimal.ZERO);
				}
				totalChequeAmt = totalChequeAmt.add(emiAmount.getActualValue());
				noOfCheques++;
			}
		}

		chequeDetail.setAmount(PennantAppUtil.unFormateAmount(emiAmount1.getActualValue(), ccyEditField));

		this.totAmount.setValue(totalChequeAmt);
		this.totNoOfCheques.setValue(noOfCheques);

		if (StringUtils.isBlank(chequeDetail.getRecordType())) {
			chequeDetail.setNewRecord(true);
			chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onChangeEmiDate(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		@SuppressWarnings("unchecked")
		List<Object> list1 = (List<Object>) event.getData();
		ChequeDetail chequeDetail = (ChequeDetail) list1.get(0);
	 	if (StringUtils.isBlank(chequeDetail.getRecordType())) {
			chequeDetail.setNewRecord(true);
			chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onChangeChequeStatus(Event event) {
		@SuppressWarnings("unchecked")
		List<Object> list1 = (List<Object>) event.getData();
		ChequeDetail chequeDetail = (ChequeDetail) list1.get(0);
		Combobox chequeStatus = (Combobox) list1.get(4);
		chequeDetail.setChequeStatus(chequeStatus.getSelectedItem().getValue().toString());

		if (StringUtils.isBlank(chequeDetail.getRecordType())) {
			chequeDetail.setNewRecord(true);
			chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		}
	}

	/**
	 * Method for prepare generated cheque details
	 * 
	 * @param listbox
	 * @param chequeHeader
	 * @throws ParseException
	 */
	private ArrayList<WrongValueException> doPrepareList(Listbox listbox, ChequeHeader chequeHeader) throws ParseException {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		ChequeDetail chequeDetail = null;
		boolean newRecord = false;
		List<ChequeDetail> oldList = chequeHeader.getChequeDetailList();

		if (listbox.getItems().isEmpty() && isPDC) {
			wve.add(new WrongValueException(this.noOfCheques, Labels.getLabel("ChequeDetailDialog_Generate_Mand")));
		}

		for (Listitem listitem : listbox.getItems()) {
			
			List<Listcell> list = listitem.getChildren();
			Listcell chequeType = list.get(0);
			Listcell chequeSerialNo = list.get(1);

			chequeDetail = getObject(Integer.valueOf(chequeSerialNo.getLabel()), oldList);
			if (chequeDetail == null) {
				newRecord = true;
				chequeDetail = new ChequeDetail();
				chequeDetail.setNewRecord(true);
				chequeDetail.setRecordType(PennantConstants.RCD_ADD);
			} else {
				if (chequeDetail.isUpload()) {//FIXME
					chequeDetail.setNewRecord(true);
					chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			}
			chequeDetail.setChequeType(chequeType.getLabel());
			chequeDetail.setChequeSerialNo(Integer.valueOf(chequeSerialNo.getLabel()));
			
			Object bankBranch = this.bankBranchID.getAttribute("bankBranchDetails");
			if (bankBranch != null) {
				BankBranch branch=  (BankBranch)bankBranch;
				chequeDetail.setBankBranchID(branch.getBankBranchID());
				chequeDetail.setBankName(branch.getBankName());
			}
			
			Listcell accNo = list.get(3);
			chequeDetail.setAccountNo(accNo.getLabel());

			Listcell emiLc = list.get(4);
			Combobox emi = (Combobox) emiLc.getFirstChild();
			emi.clearErrorMessage();
			if (!StringUtils.equals(getComboboxValue(emi), PennantConstants.List_Select)) {
				String emiRefNum = emi.getSelectedItem().getValue().toString();
				if (StringUtils.isNumeric(emiRefNum)) {
					chequeDetail.seteMIRefNo(Integer.parseInt(emiRefNum));
				} else {
					chequeDetail.seteMIRefNo(0);
				}
				chequeDetail.setChequeDate(DateUtil.parse(emi.getSelectedItem().getLabel(), DateFormat.SHORT_DATE.getPattern()));
			} else {
				if (StringUtils.equals(chequeDetail.getChequeType(), FinanceConstants.REPAYMTH_PDC)) {
					wve.add(new WrongValueException(emi, Labels.getLabel("ChequeDetailDialog_EMI_Mand")));
				} else {
					chequeDetail.seteMIRefNo(0);
				}
			}

			Listcell amount = list.get(5);
			CurrencyBox emiAmount = (CurrencyBox) amount.getFirstChild();
			emiAmount.clearErrorMessage();
			BigDecimal chequeAmt = PennantAppUtil.unFormateAmount(emiAmount.getActualValue(), ccyEditField);
			chequeDetail.setAmount(chequeAmt);
			chequeDetail.setChequeCcy(this.ccy);

			Listcell chequeStatusLC = list.get(8);
			Combobox chequeStatus = (Combobox) chequeStatusLC.getFirstChild();
			chequeDetail.setChequeStatus(chequeStatus.getSelectedItem().getValue().toString());

			Listcell accountTypeLc = list.get(9);
			Combobox accountType = (Combobox) accountTypeLc.getFirstChild();
			chequeDetail.setAccountType(accountType.getSelectedItem().getValue().toString());

			Listcell accHolderName = list.get(10);
			chequeDetail.setAccHolderName(accHolderName.getLabel());

			if (newRecord) { // only for new records
				for (ChequeDetail document : getChequeDocuments()) {
					if (document.getChequeSerialNo() == chequeDetail.getChequeSerialNo()) {
						chequeDetail.setDocImage(document.getDocImage());
						chequeDetail.setDocumentName(document.getDocumentName());
					}
				}
				oldList.add(chequeDetail);
				chequeDetail.setActive(true);
				chequeDetail.setStatus(PennantConstants.RECORD_TYPE_NEW);
			}
		}
		chequeHeader.setChequeDetailList(oldList);
		logger.debug(Literal.LEAVING);
		return wve;
	}

	private ChequeDetail getObject(int serialNo, List<ChequeDetail> chequeDetailList) {
		if (chequeDetailList != null && chequeDetailList.size() > 0) {
			for (ChequeDetail chequeDetail : chequeDetailList) {
				if (chequeDetail.getChequeSerialNo() == serialNo) {
					return chequeDetail;
				}
			}
		}
		return null;
	}

	public void onClickDeleteButton(ForwardEvent event) {
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record");
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) event.getData();
			ChequeDetail chequeDetail = (ChequeDetail) list.get(0);
			Object[] rvddata = (Object[]) list.get(2);
			Listitem listitem = (Listitem) rvddata[0];

			this.listBoxChequeDetail.removeItemAt(listitem.getIndex());
			if (chequeDetail != null && !chequeDetail.isNew()) {
				chequeDetail.setActive(false);
				chequeDetail.setRecordStatus(PennantConstants.RCD_STATUS_CANCELLED);
				if (fromLoan) {
					chequeDetail.setRecordType(PennantConstants.RCD_DEL);
				} else {
					chequeDetail.setRecordType(PennantConstants.RCD_UPD);
				}
			}
			onFulfill$EmiAmount(event);
		}
	}

	public void onClickUploadButton(ForwardEvent event) {
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) event.getData();
		ChequeDetail chequeDetail = (ChequeDetail) list.get(0);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ChequeDetailDialogCtrl", this);
		map.put("chequeDetail", chequeDetail);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/PDC/ChequeDetailDocumentDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	private Combobox getCombobox(String emiNumber) {
		Combobox combobox = new Combobox();
		combobox.setSclass(PennantConstants.mandateSclass);
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);
		for (FinanceScheduleDetail valueLabel : getFinanceSchedules()) {
			if (valueLabel.isRepayOnSchDate() || valueLabel.isPftOnSchDate()) {
				comboitem = new Comboitem();
				comboitem.setValue(valueLabel.getInstNumber());
				comboitem.setLabel(DateUtility.formatToShortDate(valueLabel.getSchDate()));
				combobox.appendChild(comboitem);
				if (String.valueOf(valueLabel.getInstNumber()).equals(String.valueOf(emiNumber))) {
					combobox.setSelectedItem(comboitem);
				}
			}
		}
		return combobox;
	}

	private Combobox getChequeStatusComboBox(String chqStatus) {
		Combobox combobox = new Combobox();
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		if (fromLoan) {
			combobox.setDisabled(true);
		}
		fillComboBox(combobox, chqStatus, chequeStatusList, "");
		return combobox;
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getFinBasicDetails(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		FinanceMain main = financeDetail.getFinScheduleData().getFinanceMain();
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, main.getFinType());
		arrayList.add(1, main.getFinCcy());
		arrayList.add(2, main.getScheduleMethod());
		arrayList.add(3, main.getFinReference());
		arrayList.add(4, main.getProfitDaysBasis());
		arrayList.add(5, main.getGrcPeriodEndDate());
		arrayList.add(6, main.isAllowGrcPeriod());
		if (StringUtils.isNotEmpty(financeDetail.getFinScheduleData().getFinanceType().getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, financeDetail.getFinScheduleData().getFinanceType().getFinCategory());
		arrayList.add(9, financeDetail.getCustomerDetails().getCustomer().getCustShrtName());
		arrayList.add(10, financeDetail.getFinScheduleData().getFinanceMain().isNewRecord());
		arrayList.add(11, "");
		logger.debug(Literal.LEAVING);
		return arrayList;
	}

	/**
	 * Method to define whether the Cheque tab is visible or not. it is
	 * displayed based only when the loan configuration allow Repayments method
	 * contains PDC or Cheque Capture Required is true. and also if the finance
	 * previously having chequeheader details then also the tab is visible.
	 * 
	 * @param financeDetail
	 * @param isContainPrvsCheques
	 * @param finRepayMethod
	 */
	public void checkTabDisplay(FinanceDetail financeDetail, String mandateType, boolean isContainPrvsCheques) {
		logger.debug(Literal.ENTERING);
		boolean isChqCaptureReq = financeDetail.getFinScheduleData().getFinanceType().isChequeCaptureReq();

		this.parenttab.setVisible(false);
		if (isChqCaptureReq || isContainPrvsCheques || MandateConstants.TYPE_PDC.equals(mandateType)) {
			this.parenttab.setVisible(true);
			if (MandateConstants.TYPE_PDC.equals(mandateType)) {
				fillComboBox(this.chequeType, mandateType, chequeTypeList, "");
			} else {
				fillComboBox(this.chequeType, FinanceConstants.REPAYMTH_UDC, chequeTypeList, "");
			}
			isPDC(mandateType);
		} else {
			this.parenttab.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onChange$chequeType(Event event) {
		logger.debug(Literal.ENTERING);

		String chqType = getComboboxValue(this.chequeType);
		isPDC(chqType);

		logger.debug(Literal.LEAVING);
	}

	private void isPDC(String chqType) {
		if (StringUtils.equals(chqType, FinanceConstants.REPAYMTH_PDC)) {
			isPDC = true;
		} else {
			isPDC = false;
		}
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(ChequeHeader aChequeHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aChequeHeader.getBefImage(), aChequeHeader);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aChequeHeader.getUserDetails(),
				getOverideMap());
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public ChequeDetail getChequeDetail() {
		return chequeDetail;
	}

	public void setChequeDetail(ChequeDetail chequeDetail) {
		this.chequeDetail = chequeDetail;
	}

	public ChequeHeader getChequeHeader() {
		return chequeHeader;
	}

	public void setChequeHeader(ChequeHeader chequeHeader) {
		this.chequeHeader = chequeHeader;
	}

	public List<ChequeDetail> getChequeDetailList() {
		return chequeDetailList;
	}

	public void setChequeDetailList(List<ChequeDetail> chequeDetailList) {
		this.chequeDetailList = chequeDetailList;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

	public void setChequeHeaderService(ChequeHeaderService chequeHeaderService) {
		this.chequeHeaderService = chequeHeaderService;
	}

	public List<ChequeDetail> getChequeDocuments() {
		return chequeDocuments;
	}

	public void setChequeDocuments(List<ChequeDetail> chequeDocuments) {
		this.chequeDocuments = chequeDocuments;
	}

	public List<FinanceScheduleDetail> getFinanceSchedules() {
		return financeSchedules;
	}

	public void setFinanceSchedules(List<FinanceScheduleDetail> financeSchedules) {
		this.financeSchedules = financeSchedules;
	}
	
}
