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
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
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

import com.pennant.ExtendedCombobox;
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
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.pdc.chequeheader.ChequeHeaderListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/pdc/ChequeDetail/chequeDetailDialog.zul file. <br>
 */
public class ChequeDetailDialogCtrl extends GFCBaseCtrl<ChequeHeader> {

	private static final long			serialVersionUID		= 1L;
	private static final Logger			logger					= Logger.getLogger(ChequeDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window					window_ChequeDetailDialog;
	protected North						north_Id;
	protected Combobox					chequeType;
	protected Intbox					noOfCheques;
	protected Decimalbox				amount;
	protected Decimalbox				amountCD;
	protected ExtendedCombobox			bankBranchID;
	protected Textbox					bankName;
	protected Textbox					city;
	protected Label						cityName;
	protected Textbox					micr;
	protected Textbox					ifsc;
	protected Textbox					accNumber;
	protected Intbox					chequeSerialNo;
	protected Intbox					noOfChequesCalc;
	protected Groupbox					finBasicdetails;
	protected Listbox					listBoxChequeDetail;
	protected Button					btnGen;
	protected Label						label_ChequeType;

	private ChequeDetail				chequeDetail;
	private boolean						fromLoan				= false;
	private ChequeHeader				chequeHeader;
	private FinBasicDetailsCtrl			finBasicDetailsCtrl;
	private Object						financeMainDialogCtrl	= null;
	private final List<ValueLabel>		chequeTypeList			= PennantStaticListUtil.getChequeTypes();
	private FinanceDetail				financeDetail;
	private List<ChequeDetail>			chequeDetailList;
	private Tab							parenttab				= null;
	private int							accNoLength;
	private String						curCcyField;
	private BankDetailService			bankDetailService;
	private List<FinanceScheduleDetail>	financeSchedules		= new ArrayList<>();
	private List<ChequeDetail>			chequeDocuments         = new ArrayList<>();
	
	private ChequeHeaderListCtrl		chequeHeaderListCtrl;

	private ChequeHeaderService			chequeHeaderService;
	
	private boolean						isPDC;
	private boolean						onclickGenBtn			= false;

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
	 * The framework calls this event handler when an application requests that the window to be created.
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
				FinanceDetail financedetails = null;
				financedetails = chequeHeaderService.getFinanceDetailById(getChequeHeader().getFinReference());
				appendFinBasicDetails(getFinBasicDetails(financedetails));
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
				this.financeSchedules = getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails();
			} else {
				this.chequeHeaderListCtrl = (ChequeHeaderListCtrl) arguments.get("chequeHeaderListCtrl");
				this.financeSchedules = (List<FinanceScheduleDetail>) arguments.get("financeSchedules");

				// Render the page and display the data.
				doLoadWorkFlow(this.chequeHeader.isWorkflow(), this.chequeHeader.getWorkflowId(),
						this.chequeHeader.getNextTaskId());

				if (isWorkFlowEnabled()) {
					if (!enqiryModule) {
						this.userAction = setListRecordStatus(this.userAction);
					}
					getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
				} else {
					getUserWorkspace().allocateAuthorities(this.pageRightName, null);
				}
			}

			if (arguments.containsKey("curCcyField")) {
				curCcyField = (String) arguments.get("curCcyField");
			}

			if (arguments.containsKey("roleCode")) {
				getUserWorkspace().allocateRoleAuthorities(arguments.get("roleCode").toString(), "ChequeHeaderDialog");
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(getChequeHeader());
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

		this.bankBranchID.setModuleName("BankBranch");
		this.bankBranchID.setMandatoryStyle(true);
		this.bankBranchID.setValueColumn("BranchCode");
		this.bankBranchID.setDescColumn("BankName");
		this.bankBranchID.setDisplayStyle(2);
		this.bankBranchID.setValidateColumns(new String[] { "BranchCode" });
		this.chequeType.setSclass(PennantConstants.mandateSclass);
		this.chequeSerialNo.setMaxlength(6);
		this.accNumber.setMaxlength(15);
		this.noOfChequesCalc.setMaxlength(2);
		this.amount.setMaxlength(18);

		setStatusDetails();

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
	 * The framework calls this event handler when user clicks the delete button.
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
	 * The framework calls this event handler when user clicks the cancel button.
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
		logger.debug("Entering");
		final ChequeHeader aChequeHeader = new ChequeHeader();
		BeanUtils.copyProperties(this.chequeHeader, aChequeHeader);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aChequeHeader);

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
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
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
		logger.debug("Entering");
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

		logger.debug("Leaving");
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
		logger.debug("Entering");
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
			logger.error("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("Leaving");
		return processCompleted;
	}

	public void onFulfill$bankBranchID(Event event) {
		logger.debug("Entering" + event.toString());

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
				this.bankBranchID.setAttribute("bankBranchID", details.getBankBranchID());
				this.bankName.setValue(details.getBankName());
				this.micr.setValue(details.getMICR());
				this.ifsc.setValue(details.getIFSC());
				this.city.setValue(details.getCity());
				this.cityName.setValue(details.getPCCityName());
				this.accNoLength = bankDetailService.getAccNoLengthByCode(details.getBankCode());
			}
		}

		logger.debug("Leaving" + event.toString());
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

		/* create the Button Controller. Disable not used buttons during working */
		/*
		 * this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
		 * this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);
		 */
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param chequeDetail
	 * 
	 */
	public void doWriteBeanToComponents(ChequeHeader aChequeHeader) {
		logger.debug(Literal.ENTERING);

		fillComboBox(this.chequeType, "" , chequeTypeList, "");
		this.noOfCheques.setValue(aChequeHeader.getNoOfCheques());
		this.amount.setValue(PennantApplicationUtil.formateAmount(aChequeHeader.getTotalAmount(), CurrencyUtil.getFormat(curCcyField)));
		doFillChequeDetails(listBoxChequeDetail, aChequeHeader.getChequeDetailList());
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aChequeDetail
	 * @throws ParseException
	 */
	public ArrayList<WrongValueException> doWriteComponentsToBean(ChequeHeader chequeHeader) throws ParseException {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		// cheque Type
		if (!this.chequeType.isDisabled() && chequeHeader.isNew()) {
			try {
				this.chequeType.getValue().toString();
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		// noOfCheques
		try {
			chequeHeader.setNoOfCheques(this.noOfCheques.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// amount
		try {
			chequeHeader.setTotalAmount(PennantAppUtil.unFormateAmount(this.amount.getValue(), 
					CurrencyUtil.getFormat(curCcyField)));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			chequeHeader.setActive(true);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		ArrayList<WrongValueException> exceptions = doPrepareList(this.listBoxChequeDetail, chequeHeader);
		if (!exceptions.isEmpty()) {
			wve.addAll(exceptions);
		}

		if (wve.isEmpty()) {
			// validate existing data
			validateChequeDetails(chequeHeader.getChequeDetailList(), true);
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
				readOnlyComponent(true, this.chequeType);
				readOnlyComponent(true, this.amount);
				readOnlyComponent(true, this.noOfCheques);
				readOnlyComponent(true, this.bankBranchID);
				readOnlyComponent(true, this.chequeSerialNo);
				readOnlyComponent(true, this.accNumber);
				readOnlyComponent(true, this.amountCD);
				readOnlyComponent(true, this.noOfChequesCalc);
				readOnlyComponent(true, this.btnGen);
			}
		}
		/*if (chequeHeader != null && chequeHeader.getChequeType() != null) {
			this.chequeType.setDisabled(true);
		}
		this.chequeType.setDisabled(true);
		this.chequeType.setVisible(false);
		this.label_ChequeType.setVisible(false);*/

		doWriteBeanToComponents(chequeHeader);
		getBorderLayoutHeight();
		try {
			// fill the components with the data
			if (fromLoan) {
				try {
					Class[] paramType = { this.getClass() };
					Object[] stringParameter = { this };
					getFinanceMainDialogCtrl().getClass().getMethod("setChequeDetailDialogCtrl", paramType)
							.invoke(getFinanceMainDialogCtrl(), stringParameter);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				if (parenttab != null) {
					boolean isChqCaptureReq = financeDetail.getFinScheduleData().getFinanceType().isChequeCaptureReq();
					if (isChqCaptureReq) {
						checkTabDisplay(financeDetail, false);
					} else if (chequeHeader.getChequeDetailList() != null
							&& !chequeHeader.getChequeDetailList().isEmpty()) {
						checkTabDisplay(financeDetail, true);
					}
				}
				// Header toolbar not required in origination
			} else {
				this.north_Id.setVisible(true);
				setDialog(DialogType.EMBEDDED);
			}

			this.btnSave.setVisible(true);

			int borderLayout;
			if (fromLoan) {
				borderLayout = 340;
			} else {
				borderLayout = 300;
			}
			this.listBoxChequeDetail.setHeight(this.borderLayoutHeight - borderLayout + "px");
			//this.window_ChequeDetailDialog.setHeight(this.borderLayoutHeight - 80 + "px");

		} catch (Exception e) {
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

		if (isPDC || onclickGenBtn) {
		// Number of cheques
		if (!this.noOfCheques.isReadonly()) {
			this.noOfCheques.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_ChequeDetailDialog_NoOfCheques.value"), true, false));
		}
		// Total Amount
		if (!this.amount.isReadonly()) {
			this.amount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_ChequeDetailDialog_Amount.value"), 2, true, false));
		}
		// Bank Branch ID
		if (!this.bankBranchID.isReadonly()) {
			this.bankBranchID.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ChequeDetailDialog_BankBranchID.value"), null, true));
		}
		// Amount Cheque Detail
		if (!this.amountCD.isReadonly()) {
			this.amountCD.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_ChequeDetailDialog_Amount.value"), 2, true, false));
		}
		// Account Number
		if (!this.accNumber.isReadonly()) {
			this.accNumber
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ChequeDetailDialog_AccNumber.value"),
							PennantRegularExpressions.REGEX_ACCOUNTNUMBER, true, this.accNoLength));
		}
		// Cheque Serial number
		if (!this.chequeSerialNo.isReadonly()) {
			this.chequeSerialNo.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_ChequeDetailDialog_ChequeSerialNo.value"), true, false));
		}
		// Amount Cheque Detail
		if (!this.noOfChequesCalc.isReadonly()) {
			this.noOfChequesCalc.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_ChequeDetailDialog_NoOfChequesCalc.value"), true, false));
		}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.bankBranchID.setConstraint("");
		this.chequeSerialNo.setConstraint("");
		this.amount.setConstraint("");
		this.amountCD.setConstraint("");
		this.noOfCheques.setConstraint("");
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
	 * Clears validation error messages from all the fields of the dialog controller.
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
			logger.debug(e);
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

		readOnlyComponent(isReadOnly("ChequeDetailDialog_Amount"), this.amount);

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
		logger.debug("Entering");
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

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.bankBranchID);
		readOnlyComponent(true, this.chequeSerialNo);
		readOnlyComponent(true, this.amount);

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
		logger.debug("Entering");
		this.bankBranchID.setValue("");
		this.amount.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws ParseException
	 */
	public void doSave_PDC(FinanceDetail financeDetail, String finReference) throws ParseException {
		logger.debug("Entering");
		final ChequeHeader aChequeHeader = new ChequeHeader();
		BeanUtils.copyProperties(getChequeHeader(), aChequeHeader);
		boolean isNew = false;
		doSetValidation();
		String rcdStatus = financeDetail.getFinScheduleData().getFinanceMain().getRecordStatus();

		ArrayList<WrongValueException> wve = doWriteComponentsToBean(aChequeHeader);
		if (!wve.isEmpty() && parenttab != null) {
			parenttab.setSelected(true);
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
		logger.debug("Leaving");

		financeDetail.setChequeHeader(aChequeHeader);
	}

	public void onClick$btnGen(Event event) throws ParseException {
		logger.debug(Literal.ENTERING);
		doRemoveValidation();
		onclickGenBtn = true;
		doSetValidation();
		String chqType = this.chequeType.getSelectedItem().getValue().toString();
		this.accNumber.getValue();
		this.chequeSerialNo.intValue();
		this.amount.getValue();
		this.noOfCheques.getValue();
		this.noOfChequesCalc.getValue();
		this.amountCD.getValue();
		onclickGenBtn = false;
		// method to validate
		if (this.bankBranchID.getValidatedValue() != null && !this.bankBranchID.getValidatedValue().isEmpty()) {
			List<ChequeDetail> chequeDetails = new ArrayList<>();
			int numberofC = this.noOfChequesCalc.getValue();
			int chequStaretr = this.chequeSerialNo.intValue();
			int prvsNoOfChqs = this.noOfCheques.getValue();
			this.chequeDetail = new ChequeDetail();
			BigDecimal totalChequeAmt = this.amount.getValue();
			for (int i = 0; i < numberofC; i++) {
				ChequeDetail cheqDetails = getNewChequedetails();
				cheqDetails.setChequeSerialNo(chequStaretr);
				chequStaretr++;
				Object bankBranchObj = this.bankBranchID.getAttribute("bankBranchID");
				if (bankBranchObj != null) {
					cheqDetails.setBankBranchID(Long.parseLong(bankBranchObj.toString()));
				}
				cheqDetails.setBankBranchIDName(this.bankName.getValue());
				cheqDetails.setAccountNo(this.accNumber.getValue());
				String curField = this.curCcyField;
				cheqDetails.setAmount(PennantApplicationUtil.unFormateAmount(this.amountCD.getValue(),
						CurrencyUtil.getFormat(curField)));
				totalChequeAmt = totalChequeAmt.add(this.amountCD.getValue());
				cheqDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				cheqDetails.setNewRecord(true);
				cheqDetails.setActive(true);
				cheqDetails.setChequeType(chqType);
				chequeDetails.add(cheqDetails);
			}

			// validate existing data
			validateChequeDetails(chequeDetails, false);
			this.noOfCheques.setValue(prvsNoOfChqs + numberofC);
			this.amount.setValue(totalChequeAmt);

			doFillChequeDetails(this.listBoxChequeDetail, chequeDetails);
			logger.debug(Literal.LEAVING);
		}
	}

	private void doFillChequeDetails(Listbox listbox, List<ChequeDetail> chequeDetails) {
		if (chequeDetails != null && chequeDetails.size() > 0) {
			for (ChequeDetail chequeDetail : chequeDetails) {
				boolean readonly = false;

				if (!chequeDetail.isActive() || StringUtils.trimToEmpty(chequeDetail.getRecordType()).equals(PennantConstants.RCD_DEL)) {
					readonly = true;
				}

				Listitem listitem = new Listitem();
				listitem.setAttribute("data", chequeDetail);
				Listcell listcell;

				//ChequeType
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
				bankBranchID.setValue(String.valueOf(chequeDetail.getBankBranchID()));
				bankBranchID.setDescription(chequeDetail.getBankBranchIDName());
				listcell.appendChild(bankBranchID);
				listcell.setParent(listitem);

				// AccountNo
				listcell = new Listcell(chequeDetail.getAccountNo());
				listcell.setParent(listitem);

				// Emi ref
				listcell = new Listcell();
				Combobox emiReference = getCombobox("1");
				Combobox emi = getCombobox(chequeDetail.geteMIRefNo());
				emiReference.setValue(emi.getSelectedItem().getLabel());
				emiReference.setReadonly(readonly);
				readOnlyComponent(readonly, emiReference);
				if(!isPDC){
					emiReference.setDisabled(true);	
				}
				listcell.appendChild(emiReference);
				listcell.setParent(listitem);

				// Amount
				listcell = new Listcell();
				Decimalbox emiAmount = new Decimalbox();
				emiAmount.setFormat(PennantConstants.in_amountFormate2);
				String curField = this.curCcyField;
				emiAmount.setValue(PennantApplicationUtil.formateAmount(chequeDetail.getAmount(),
						CurrencyUtil.getFormat(curField)));
				readOnlyComponent(readonly, emiAmount);
				listcell.appendChild(emiAmount);
				listcell.setParent(listitem);

				// Delete action
				listcell = new Listcell();
				Button delButton = new Button(Labels.getLabel("ChequeDetailDialog_Delete"));
				Object[] deleteItem = new Object[1];
				deleteItem[0] = listitem;
				readOnlyComponent(readonly, delButton);
				listcell.appendChild(delButton);
				listcell.setParent(listitem);
				
				// Upload image action
				listcell = new Listcell();
				Button uploadButton = new Button(Labels.getLabel("ChequeDetailDialog_Upload"));
				Object[] uploadItem = new Object[1];
				uploadItem[0] = listitem;
				readOnlyComponent(readonly, uploadButton);
				listcell.appendChild(uploadButton);
				listcell.setParent(listitem);

				// only to avoid the number format exception while setting the
				// value to bean
				listcell = new Listcell(chequeDetail.getAmount().toString());
				listcell.setParent(listitem);
				listcell.setVisible(false);
				listbox.appendChild(listitem);

				List<Object> list = new ArrayList<Object>(11);
				list.add(chequeDetail); //0
				list.add(emiAmount); //1
				list.add(deleteItem); //2
				list.add(getComboboxValue(emiReference)); //3

				emiAmount.addForward("onChange", this.window_ChequeDetailDialog, "onChangeEmiAmount", list);
				delButton.addForward("onClick", this.window_ChequeDetailDialog, "onClickDeleteButton", list);
				uploadButton.addForward("onClick", this.window_ChequeDetailDialog, "onClickUploadButton", list);
				emiReference.addForward("onChange", this.window_ChequeDetailDialog, "onChangeEmiDate", list);
			}
		}
	}

	// Process for Document uploading
	public void onUpload$btnUploadDoc(UploadEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Media media = event.getMedia();
		//browseDoc(media);
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for validating
	 * 
	 * @param chequeDetails
	 * @param validate
	 * @throws ParseException
	 */
	private void validateChequeDetails(List<ChequeDetail> chequeDetails, boolean validate) throws ParseException {
		for (Listitem listitem : listBoxChequeDetail.getItems()) {
			int emiRefCount = 0;
			for (ChequeDetail chequeDetail : chequeDetails) {
				List<Listcell> list = listitem.getChildren();
				Listcell chkSerial = (Listcell) list.get(1);
				Listcell extListCell = (Listcell) list.get(2);
				ExtendedCombobox extendedCombobox = (ExtendedCombobox) extListCell.getFirstChild();

				// validate cheque serial number
				if (!validate && !StringUtils.equals(chequeDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
					if (StringUtils.equals(String.valueOf(extendedCombobox.getValue()),
							String.valueOf(chequeDetail.getBankBranchID()))
							&& StringUtils.equals(chkSerial.getLabel().toString(),
									String.format("%06d", chequeDetail.getChequeSerialNo()))) {
						if (fromLoan) {
							parenttab.setSelected(true);
						}
						throw new WrongValueException(extendedCombobox,
								Labels.getLabel("ChequeDetailDialog_ChkSerial_Exists"));
					}
				}

				// validate cheque EMI ref no's
				if (validate && !StringUtils.equals(chequeDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
					Listcell emiLc = (Listcell) list.get(4);
					Combobox emi = (Combobox) emiLc.getFirstChild();
					if (StringUtils.equals(getComboboxValue(emi), chequeDetail.geteMIRefNo())) {
						emiRefCount++;
						if (emiRefCount > 1) {
							if (fromLoan) {
								parenttab.setSelected(true);
							}
							throw new WrongValueException(emiLc,
									Labels.getLabel("ChequeDetailDialog_ChkEMIRef_Exists"));
						}
					}
				}

				if (validate) {
					Combobox comboItem = getCombobox(chequeDetail.geteMIRefNo());
					if (StringUtils.equals(chequeDetail.getChequeType(), FinanceConstants.REPAYMTH_PDC)) {
						Date emiDate = DateUtility.parse(comboItem.getSelectedItem().getLabel(), PennantConstants.dateFormat);
						if (getFinanceDetail() != null) {
							List<FinanceScheduleDetail> schedules = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
							for (FinanceScheduleDetail detail : schedules) {
								if (DateUtility.compare(emiDate, detail.getSchDate()) == 0) {
									if (detail.getRepayAmount().compareTo(chequeDetail.getAmount()) != 0) {
										Listcell emiAmountLc = (Listcell) list.get(5);
										if (fromLoan) {
											parenttab.setSelected(true);
										}
										throw new WrongValueException(this.listBoxChequeDetail,Labels.getLabel("ChequeDetailDialog_EMI_Amount"));
									} else {
										break;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void onChangeEmiAmount(Event event) {
		logger.debug(Literal.ENTERING);

		BigDecimal totalChequeAmt = BigDecimal.ZERO;
		int noOfCheques = 0;

		@SuppressWarnings("unchecked")
		List<Object> list1 = (List<Object>) event.getData();
		ChequeDetail chequeDetail = (ChequeDetail) list1.get(0);
		Decimalbox emiAmount1 = (Decimalbox) list1.get(1);

		for (Listitem listitem : listBoxChequeDetail.getItems()) {
			List<Listcell> list = listitem.getChildren();
			Listcell emiAmtLc = (Listcell) list.get(5);
			Decimalbox emiAmount = (Decimalbox) emiAmtLc.getFirstChild();
			if (!emiAmount.isReadonly()) {
				totalChequeAmt = totalChequeAmt.add(emiAmount.getValue());
				noOfCheques++;
			}
		}
		
		chequeDetail.setAmount(
				PennantAppUtil.unFormateAmount(emiAmount1.getValue(), CurrencyUtil.getFormat(this.curCcyField)));
		
		this.amount.setValue(totalChequeAmt);
		this.noOfCheques.setValue(noOfCheques);

		if (StringUtils.isBlank(chequeDetail.getRecordType())) {
			chequeDetail.setNewRecord(true);
			chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		}

		logger.debug(Literal.LEAVING);
	}
	
	public void onChangeEmiDate(Event event) {
		logger.debug(Literal.ENTERING);
		
		@SuppressWarnings("unchecked")
		List<Object> list1 = (List<Object>) event.getData();
		ChequeDetail chequeDetail = (ChequeDetail) list1.get(0);
		String emiDate = (String) list1.get(3);
		chequeDetail.seteMIRefNo(emiDate);
		
		if (StringUtils.isBlank(chequeDetail.getRecordType())) {
			chequeDetail.setNewRecord(true);
			chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		}
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for prepare generated cheque details
	 * 
	 * @param listbox
	 * @param chequeHeader
	 * @throws ParseException
	 */
	private ArrayList<WrongValueException> doPrepareList(Listbox listbox, ChequeHeader chequeHeader)
			throws ParseException {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		ChequeDetail chequeDetail = null;
		boolean newRecord = false;
		List<ChequeDetail> oldList = chequeHeader.getChequeDetailList();

		if (listbox.getItems().isEmpty() && isPDC) {
			wve.add(new WrongValueException(noOfCheques, Labels.getLabel("ChequeDetailDialog_Generate_Mand")));
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
			}
			chequeDetail.setChequeType(chequeType.getLabel());
			chequeDetail.setChequeSerialNo(Integer.valueOf(chequeSerialNo.getLabel()));
			Listcell bankbranchid = list.get(2);
			ExtendedCombobox bankbrachid = (ExtendedCombobox) bankbranchid.getFirstChild();

			chequeDetail.setBankBranchID(Long.valueOf(bankbrachid.getValue()));
			chequeDetail.setBankBranchIDName(bankbrachid.getDescription());

			Listcell accNo = (Listcell) list.get(3);
			chequeDetail.setAccountNo(accNo.getLabel());

			Listcell emiLc = (Listcell) list.get(4);
			Combobox emi = (Combobox) emiLc.getFirstChild();
			if (!StringUtils.equals(getComboboxValue(emi), PennantConstants.List_Select)) {
				chequeDetail.seteMIRefNo(emi.getSelectedItem().getValue().toString());
			}else{
				if (StringUtils.equals(chequeDetail.getChequeType(), FinanceConstants.REPAYMTH_PDC)) {
					wve.add(new WrongValueException(emiLc, Labels.getLabel("ChequeDetailDialog_EMI_Mand")));
				}else{
					chequeDetail.seteMIRefNo(null);
				}
			}
			
			Listcell amount = (Listcell) list.get(5);
			Decimalbox emiAmount = (Decimalbox) amount.getFirstChild();
			String curField = this.curCcyField;
			BigDecimal chequeAmt = PennantAppUtil.unFormateAmount(emiAmount.getValue(),
					CurrencyUtil.getFormat(curField));
			chequeDetail.setAmount(chequeAmt);
			chequeDetail.setChequeCcy(SysParamUtil.getAppCurrency());

			if (newRecord) { //only for new records
				for(ChequeDetail document:getChequeDocuments()) {
					if(document.getChequeSerialNo() == chequeDetail.getChequeSerialNo()) {
						chequeDetail.setDocImage(document.getDocImage());
						chequeDetail.setDocumentName(document.getDocumentName());
					}
				}
				
				oldList.add(chequeDetail);
				chequeDetail.setActive(true);
				chequeDetail.setStatus("NEW");
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
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) event.getData();
		ChequeDetail chequeDetail = (ChequeDetail) list.get(0);
		Object[] rvddata = (Object[]) list.get(2);
		Listitem listitem = (Listitem) rvddata[0];
		
		this.listBoxChequeDetail.removeItemAt(listitem.getIndex());
		if (chequeDetail != null && !chequeDetail.isNew()) {
			chequeDetail.setActive(false);
			chequeDetail.setRecordStatus(PennantConstants.RCD_STATUS_CANCELLED);
			if(fromLoan) {
				chequeDetail.setRecordType(PennantConstants.RCD_DEL);
			} else {
				chequeDetail.setRecordType(PennantConstants.RCD_UPD);
			}
		}
		onChangeEmiAmount(event);
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

	private Combobox getCombobox(String eminumber) {
		List<FinanceScheduleDetail> list = this.financeSchedules;
		Combobox combobox = new Combobox();
		combobox.setSclass(PennantConstants.mandateSclass);
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);
		for (FinanceScheduleDetail valueLabel : list) {
			if (valueLabel.isRepayOnSchDate() || valueLabel.isPftOnSchDate()) {
				comboitem = new Comboitem();
				comboitem.setValue(valueLabel.getInstNumber());
				comboitem.setLabel(DateUtility.formatToShortDate(valueLabel.getSchDate()));
				combobox.appendChild(comboitem);
				if (String.valueOf(valueLabel.getInstNumber()).equals(String.valueOf(eminumber))) {
					combobox.setSelectedItem(comboitem);
				}
			}
		}
		return combobox;
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getFinBasicDetails(FinanceDetail financeDetail) {
		logger.debug(" Entering ");

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
		logger.debug(" Leaving ");
		return arrayList;
	}
	
	private ChequeDetail getNewChequedetails() {
		ChequeDetail chequeDetail = new ChequeDetail();
		chequeDetail.setAccountNo(this.accNumber.getValue());
		return chequeDetail;
	}

	/**
	 * Method to define whether the Cheque tab is visible or not. it is displayed based only when the loan configuration
	 * allow Repayments method contains PDC or Cheque Capture Required is true. and also if the finance previously
	 * having chequeheader details then also the tab is visible.
	 * 
	 * @param financeDetail
	 * @param isContainPrvsCheques
	 * @param finRepayMethod
	 */
	public void checkTabDisplay(FinanceDetail financeDetail, boolean isContainPrvsCheques) {
		logger.debug(Literal.ENTERING);
		boolean isChqCaptureReq = financeDetail.getFinScheduleData().getFinanceType().isChequeCaptureReq();

		if (isChqCaptureReq || isContainPrvsCheques) {
			this.parenttab.setVisible(true);
			String chequetype = "";
			fillComboBox(this.chequeType, chequetype, chequeTypeList, "");
		} else {
			this.parenttab.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onChange$chequeType(Event event) {
		logger.debug(Literal.ENTERING);
		String chqType = getComboboxValue(this.chequeType);
		if (StringUtils.equals(chqType, FinanceConstants.REPAYMTH_PDC)) {
			isPDC = true;
		} else {
			isPDC = false;
		}
		logger.debug(Literal.LEAVING);
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
}
