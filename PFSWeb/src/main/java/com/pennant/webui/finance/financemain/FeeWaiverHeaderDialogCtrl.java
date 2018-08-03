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
 *																							*
 * FileName    		:  FeeWaiverHeader.java                                 		        * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-11-2017    														*
 *                                                                  						*
 * Modified Date    :  			    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-11-2017       Pennant	                 0.1                                            * 
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FeeWaiverHeaderService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/FeeWaiverHeaderDialog.zul file.
 */
public class FeeWaiverHeaderDialogCtrl extends GFCBaseCtrl<FeeWaiverHeader> {

	private static final long serialVersionUID = -6945930303723518608L;
	private static final Logger logger = Logger.getLogger(FeeWaiverHeaderDialogCtrl.class);

	protected Window window_feeWaiverHeaderDialog;

	protected Textbox remarks;
	protected Datebox postingDate;
	protected Datebox valueDate;
	protected Checkbox select;
	protected Listheader listheader_Select;

	protected Groupbox finBasicdetails;

	protected Listbox listFeeWaiverDetails;

	private FinanceDetail financeDetail;
	private FinanceMain financeMain;
	protected transient FinanceSelectCtrl financeSelectCtrl = null;
	private FeeWaiverHeader feeWaiverHeader;
	private transient FeeWaiverHeaderService feeWaiverHeaderService;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;

	private Object financeMainDialogCtrl;

	private List<FeeWaiverDetail> feeWaiverDetails = new ArrayList<FeeWaiverDetail>();

	// private transient boolean recSave = false;
	private boolean isEnquiry = false;
	protected String moduleDefiner = "";
	protected String menuItemRightName = null;
	protected Decimalbox crrWaivedAmt = null;

	private int ccyFormatter = 0;
	private BigDecimal totCurrWaivedAmt = BigDecimal.ZERO;

	/**
	 * listheader_Select default constructor.<br>
	 */
	public FeeWaiverHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FeeWaiverHeaderDialog";
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
	public void onCreate$window_feeWaiverHeaderDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_feeWaiverHeaderDialog);

		try {

			// READ OVERHANDED parameters !
			if (arguments.containsKey("financeSelectCtrl")) {
				setFinanceSelectCtrl((FinanceSelectCtrl) arguments.get("financeSelectCtrl"));
				this.financeMainDialogCtrl = (Object) arguments.get("financeSelectCtrl");
			}

			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			if (arguments.containsKey("menuItemRightName")) {
				menuItemRightName = (String) arguments.get("menuItemRightName");
			}

			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
				this.financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			}

			if (arguments.containsKey("isEnquiry")) {
				isEnquiry = (Boolean) arguments.get("isEnquiry");
			}

			if (arguments.containsKey("feeWaiverHeader")) {
				setFeeWaiverHeader((FeeWaiverHeader) arguments.get("feeWaiverHeader"));
				this.feeWaiverHeader = getFeeWaiverHeader();
			}

			if (this.feeWaiverHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			FeeWaiverHeader feeWaiverHeader = new FeeWaiverHeader();
			BeanUtils.copyProperties(this.feeWaiverHeader, feeWaiverHeader);
			this.feeWaiverHeader.setBefImage(feeWaiverHeader);

			// Render the page and display the data.
			doLoadWorkFlow(this.feeWaiverHeader.isWorkflow(), this.feeWaiverHeader.getWorkflowId(),
					this.feeWaiverHeader.getNextTaskId());

			if (isWorkFlowEnabled()) {
				String recStatus = StringUtils.trimToEmpty(feeWaiverHeader.getRecordStatus());
				if (recStatus.equals(PennantConstants.RCD_STATUS_REJECTED)) {
					this.userAction = setRejectRecordStatus(this.userAction);
				} else {
					this.userAction = setListRecordStatus(this.userAction);
					getUserWorkspace().allocateMenuRoleAuthorities(getRole(), this.pageRightName, menuItemRightName);
				}
			} else {
				this.south.setHeight("0px");
			}
			this.listFeeWaiverDetails.setHeight(borderLayoutHeight - 210 + "px");
			ccyFormatter = CurrencyUtil.getFormat(this.financeMain.getFinCcy());

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.feeWaiverHeader);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		setStatusDetails();
		this.remarks.setMaxlength(500);
		this.postingDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		logger.debug("Leaving");
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole(), menuItemRightName);

		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FeeWaiverHeaderDialog_btnEdit"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(!getUserWorkspace().isAllowed("button_FeeWaiverHeaderDialog_btnSave"));
		this.btnCancel.setVisible(false);

		// Schedule related buttons
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");

		doWriteBeanToComponents(this.feeWaiverHeader.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFeeWaiverHeader
	 */
	public void doWriteBeanToComponents(FeeWaiverHeader aFeeWaiverHeader) {
		logger.debug("Entering");

		appendFinBasicDetails(this.financeMain);

		if (aFeeWaiverHeader.isNewRecord()) {
			this.postingDate.setValue(DateUtility.getAppDate());
			this.valueDate.setValue(DateUtility.getAppDate());
		} else {
			this.postingDate.setValue(aFeeWaiverHeader.getPostingDate());
			this.valueDate.setValue(aFeeWaiverHeader.getValueDate());
		}

		this.remarks.setValue(aFeeWaiverHeader.getRemarks());

		if (isEnquiry) {
			setFeeWaiverDetails(aFeeWaiverHeader.getFeeWaiverDetails());
			doFillFeeWaiverDetails(aFeeWaiverHeader.getFeeWaiverDetails());
		} else {
			doFillFeeWaiverDetails(aFeeWaiverHeader);
		}
		this.recordStatus.setValue(aFeeWaiverHeader.getRecordStatus());

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFeeWaiverHeader
	 */
	public void doWriteComponentsToBean(FeeWaiverHeader aFeeWaiverHeader) {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		aFeeWaiverHeader.setFinReference(this.financeMain.getFinReference());
		aFeeWaiverHeader.setRemarks(this.remarks.getValue());
		aFeeWaiverHeader.setEvent(this.moduleDefiner);
		savePaymentDetails(aFeeWaiverHeader);
		aFeeWaiverHeader.setRecordStatus(this.recordStatus.getValue());
		if (aFeeWaiverHeader.isNewRecord()) {
			for (FeeWaiverDetail waiver : aFeeWaiverHeader.getFeeWaiverDetails()) {
				if (waiver.getFeeTypeCode().equals(RepayConstants.ALLOCATION_ODC)
						|| waiver.getFeeTypeCode().equals(RepayConstants.ALLOCATION_LPFT)) {
					waiver.setAdviseId(0);
				}
			}
		}

		// Value Date
		try {
			if (this.valueDate.getValue() != null) {
				aFeeWaiverHeader.setValueDate(new Timestamp(this.valueDate.getValue().getTime()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.postingDate.getValue() != null) {
				aFeeWaiverHeader.setPostingDate(new Timestamp(this.postingDate.getValue().getTime()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	private void savePaymentDetails(FeeWaiverHeader aFeeWaiverHeader) {
		logger.debug("Entering");

		List<FeeWaiverDetail> list = new ArrayList<FeeWaiverDetail>();
		if (aFeeWaiverHeader.isNewRecord()) {
			for (FeeWaiverDetail detail : getFeeWaiverDetails()) {
				if (detail.getCurrWaiverAmount() != null
						&& (BigDecimal.ZERO.compareTo(detail.getCurrWaiverAmount()) == 0)) {
					continue;
				}
				detail.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				detail.setNewRecord(true);
				list.add(detail);
			}
		} else {
			for (FeeWaiverDetail detail : getFeeWaiverDetails()) {
				if (detail.isNewRecord()) {
					if (detail.getCurrWaiverAmount() != null
							&& (BigDecimal.ZERO.compareTo(detail.getCurrWaiverAmount()) == 0)) {
						continue;
					}
					detail.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
					detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					detail.setNewRecord(true);
					list.add(detail);
				} else {
					if (detail.getCurrWaiverAmount() != null
							&& (BigDecimal.ZERO.compareTo(detail.getCurrWaiverAmount()) == 0)) {
						detail.setRecordStatus(PennantConstants.RCD_STATUS_CANCELLED);
						detail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
						detail.setNewRecord(false);
						list.add(detail);
					} else {
						detail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
						detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
						detail.setNewRecord(false);
						list.add(detail);
					}
				}
			}
		}
		aFeeWaiverHeader.setFeeWaiverDetails(list);
		logger.debug("Leaving");
	}

	private void doSetValidation() {
		logger.debug("Entering ");

		Label totCurrWaived = null;
		// Remarks Validation
		if (!this.remarks.isReadonly()) {
			this.remarks.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FeeWaiverHeaderDialog_Remarks.value"), null, false));
		}

		// Value Date
		if (!this.valueDate.isReadonly()) {
			this.valueDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_feeWaiverHeaderDialog_ValueDate.value"), true));
		}

		if (this.listFeeWaiverDetails != null && this.listFeeWaiverDetails.getItems().size() > 0) {

			for (int i = 0; i < listFeeWaiverDetails.getItems().size(); i++) {
				List<Listcell> listCells = listFeeWaiverDetails.getItems().get(i).getChildren();

				if (listFeeWaiverDetails.getItemCount() - i == 1) {
					Listcell totCurrWaivedCell = listCells.get(6);
					totCurrWaived = (Label) totCurrWaivedCell.getChildren().get(0);
					break;
				}
				Listcell balanceAmtCell = listCells.get(5);
				Listcell currWaivedAmtCell = listCells.get(6);
				Decimalbox currWaivedAmt = (Decimalbox) currWaivedAmtCell.getChildren().get(0);
				Label balanceAmt = (Label) balanceAmtCell.getChildren().get(0);
				Clients.clearWrongValue(currWaivedAmt);

				if (PennantAppUtil.unFormateAmount(balanceAmt.getValue(), ccyFormatter)
						.compareTo(currWaivedAmt.getValue()) == -1) {
					throw new WrongValueException(currWaivedAmt,
							Labels.getLabel("label_FeeWaiverHeaderDialog_currWaiverAmountErrorMsg.value"));
				}
			}
		}
		
		
		if(PennantAppUtil.unFormateAmount(totCurrWaived.getValue(),ccyFormatter).compareTo(BigDecimal.ZERO)==0){
			throw new WrongValueException(totCurrWaived,
					Labels.getLabel("label_FeeWaiverHeaderDialog_TotalCurrWaivedAmt.value"));
		}

		logger.debug("Leaving ");
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aFeeWaiverHeader
	 *            The entity that need to be render.
	 */
	public void doShowDialog(FeeWaiverHeader aFeeWaiverHeader) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFeeWaiverHeader.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aFeeWaiverHeader.getRecordType())) {
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
		doWriteBeanToComponents(aFeeWaiverHeader);
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 * 
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.feeWaiverHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		if (getWorkFlow().firstTaskOwner().equals(getRole())) {
			this.remarks.setReadonly(false);
			this.postingDate.setDisabled(false);;
			this.valueDate.setDisabled(false);
		} else {
			this.remarks.setReadonly(true);
			this.postingDate.setDisabled(true);
			this.valueDate.setDisabled(true);
		}

		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 */
	public void doSave() {
		logger.debug("Entering");

		FeeWaiverHeader aFeeWaiverHeader = new FeeWaiverHeader();
		Cloner cloner = new Cloner();
		aFeeWaiverHeader = cloner.deepClone(getFeeWaiverHeader());
		doSetValidation();
		doWriteComponentsToBean(aFeeWaiverHeader);

		boolean isNew;
		isNew = aFeeWaiverHeader.isNew();
		String tranType;

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFeeWaiverHeader.getRecordType())) {
				aFeeWaiverHeader.setVersion(aFeeWaiverHeader.getVersion() + 1);
				if (isNew) {
					aFeeWaiverHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFeeWaiverHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFeeWaiverHeader.setNewRecord(true);
				}
			}
		} else {
			aFeeWaiverHeader.setVersion(aFeeWaiverHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aFeeWaiverHeader, tranType)) {
				// List Detail Refreshment
				refreshList();

				// Confirmation message
				String msg = PennantApplicationUtil.getSavingStatus(aFeeWaiverHeader.getRoleCode(),
						aFeeWaiverHeader.getNextRoleCode(), aFeeWaiverHeader.getFinReference() + "",
						" Covenant Details ", aFeeWaiverHeader.getRecordStatus());
				if (StringUtils.equals(aFeeWaiverHeader.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
					msg = " Covenant Detail with Reference " + aFeeWaiverHeader.getFinReference()
							+ " Approved Succesfully.";
				}
				Clients.showNotification(msg, "info", null, null, -1);

				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aFeeWaiverHeader
	 * @param tranType
	 *            (String)
	 * @return boolean
	 */
	private boolean doProcess(FeeWaiverHeader aFeeWaiverHeader, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aFeeWaiverHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFeeWaiverHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFeeWaiverHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aFeeWaiverHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFeeWaiverHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFeeWaiverHeader);
				}

				if (isNotesMandatory(taskId, aFeeWaiverHeader)) {
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

			aFeeWaiverHeader.setTaskId(taskId);
			aFeeWaiverHeader.setNextTaskId(nextTaskId);
			aFeeWaiverHeader.setRoleCode(getRole());
			aFeeWaiverHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFeeWaiverHeader, tranType);
			String operationRefs = getServiceOperations(taskId, aFeeWaiverHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFeeWaiverHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFeeWaiverHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * @param method
	 * 
	 * @return boolean
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AuditHeader aAuditHeader = auditHeader;
		FeeWaiverHeader aFeeWaiverHeader = (FeeWaiverHeader) aAuditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						aAuditHeader = feeWaiverHeaderService.delete(aAuditHeader);
						deleteNotes = true;
					} else {
						aAuditHeader = feeWaiverHeaderService.saveOrUpdate(aAuditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						aAuditHeader = feeWaiverHeaderService.doApprove(aAuditHeader);

						if (aFeeWaiverHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						aAuditHeader = feeWaiverHeaderService.doReject(aAuditHeader);

						if (aFeeWaiverHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						aAuditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_feeWaiverHeaderDialog, aAuditHeader);
						return processCompleted;
					}
				}

				aAuditHeader = ErrorControl.showErrorDetails(this.window_feeWaiverHeaderDialog, aAuditHeader);
				retValue = aAuditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.feeWaiverHeader), true);
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
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aFeeWaiverHeader
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(FeeWaiverHeader aFeeWaiverHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFeeWaiverHeader.getBefImage(), aFeeWaiverHeader);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aFeeWaiverHeader.getUserDetails(),
				getOverideMap());
	}

	private void doFillFeeWaiverDetails(FeeWaiverHeader aFeeWaiverHeader) {
		logger.debug(Literal.ENTERING);

		if (aFeeWaiverHeader.isNewRecord()) {
			for (FeeWaiverDetail detail : aFeeWaiverHeader.getFeeWaiverDetails()) {
				if (detail.getBalanceAmount() != null && BigDecimal.ZERO.compareTo(detail.getBalanceAmount()) == -1) {

					getFeeWaiverDetails().add(detail);
				}
			}
		} else {
			updatePaybleAmounts(aFeeWaiverHeader.getFeeWaiverDetails());
		}
		doFillFeeWaiverDetails(getFeeWaiverDetails());
		logger.debug(Literal.LEAVING);
	}

	// Update the latest balance amount..
	private void updatePaybleAmounts(List<FeeWaiverDetail> oldList) {
		logger.debug(Literal.ENTERING);

		for (FeeWaiverDetail newDetail : oldList) {
			if (BigDecimal.ZERO.compareTo(newDetail.getBalanceAmount()) == -1) {
				getFeeWaiverDetails().add(newDetail);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Fill Fee Waiver Details To list
	 * 
	 * @param feeWaiverDetails
	 */
	public void doFillFeeWaiverDetails(List<FeeWaiverDetail> feeWaiverDetails) {
		logger.debug("Entering");

		this.listFeeWaiverDetails.getItems().clear();
		boolean isReadOnly = true;

		if (getWorkFlow().firstTaskOwner().equals(getRole())) {
			isReadOnly = false;
		}

		BigDecimal totReceivableAmt = BigDecimal.ZERO;
		BigDecimal totReceivedAmt = BigDecimal.ZERO;
		BigDecimal totWaivedAmt = BigDecimal.ZERO;
		BigDecimal totBalanceAmt = BigDecimal.ZERO;
		setFeeWaiverDetails(feeWaiverDetails);
		if (feeWaiverDetails != null && !feeWaiverDetails.isEmpty()) {
			for (FeeWaiverDetail detail : feeWaiverDetails) {
				Listitem item;
				Listcell lc;
				item = new Listitem();
				lc = new Listcell();
				Checkbox selected = new Checkbox();
				selected.setTabindex(-1);
				selected.setChecked(false);
				ComponentsCtrl.applyForward(selected, "onCheck=onChecklistItemSelect");
				selected.setParent(lc);
				lc.setParent(item);

				lc = new Listcell(detail.getFeeTypeDesc());
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getReceivableAmount(), ccyFormatter));
				totReceivableAmt = detail.getReceivableAmount().add(totReceivableAmt);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getReceivedAmount(), ccyFormatter));
				totReceivedAmt = detail.getReceivedAmount().add(totReceivedAmt);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getWaivedAmount(), ccyFormatter));
				totWaivedAmt = detail.getWaivedAmount().add(totWaivedAmt);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				lc = new Listcell();
				Label balance = new Label();
				balance.setValue(PennantApplicationUtil.amountFormate(detail.getBalanceAmount(), ccyFormatter));
				totBalanceAmt = detail.getBalanceAmount().add(totBalanceAmt);
				lc.appendChild(balance);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				lc = new Listcell();
				crrWaivedAmt = new Decimalbox();
				crrWaivedAmt.setReadonly(isReadOnly);
				crrWaivedAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
				crrWaivedAmt.setStyle("text-align:right; ");
				crrWaivedAmt.setValue(PennantAppUtil.formateAmount(detail.getCurrWaiverAmount(), ccyFormatter));
				crrWaivedAmt.addForward("onChange", self, "onChangeCurrWaivedAmount");
				crrWaivedAmt.setAttribute("object", detail);
				totCurrWaivedAmt = detail.getCurrWaiverAmount().add(totCurrWaivedAmt);
				lc.appendChild(crrWaivedAmt);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				lc = new Listcell();
				Label netBal = new Label();
				netBal.setValue(PennantApplicationUtil
						.amountFormate(detail.getBalanceAmount().subtract(detail.getCurrWaiverAmount()), ccyFormatter));
				lc.appendChild(netBal);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				this.listFeeWaiverDetails.appendChild(item);
			}

			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell();
			lc.setParent(item);
			lc = new Listcell(" Total ");
			lc.setStyle("font-weight:bold;");
			item.appendChild(lc);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totReceivableAmt, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totReceivedAmt, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totWaivedAmt, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totBalanceAmt, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell();
			Label totCurrWaived = new Label(PennantApplicationUtil.amountFormate(totCurrWaivedAmt, ccyFormatter));
			lc.appendChild(totCurrWaived);
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell();
			lc.setParent(item);

			this.listFeeWaiverDetails.appendChild(item);
		}
		logger.debug("Leaving");
	}

	public void onChangeCurrWaivedAmount(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		int count = 1;

		int listSize = listFeeWaiverDetails.getItemCount();
		BigDecimal balanceAmt = BigDecimal.ZERO;
		Decimalbox currWaivedAmt = (Decimalbox) event.getOrigin().getTarget();
		Clients.clearWrongValue(currWaivedAmt);

		BigDecimal amount = PennantAppUtil.unFormateAmount(currWaivedAmt.getValue(), ccyFormatter);
		FeeWaiverDetail feeWaiverDetail = (FeeWaiverDetail) currWaivedAmt.getAttribute("object");
		for (FeeWaiverDetail detail : getFeeWaiverDetails()) {

			if (feeWaiverDetail.getAdviseId() == detail.getAdviseId()) {
				if ((detail.getBalanceAmount().compareTo(amount)) == -1) {
					throw new WrongValueException(currWaivedAmt,
							Labels.getLabel("label_FeeWaiverHeaderDialog_currWaiverAmountErrorMsg.value"));
				} else if (amount.compareTo(BigDecimal.ZERO) == 0) {
					totCurrWaivedAmt = totCurrWaivedAmt.subtract(detail.getCurrWaiverAmount());
					currWaivedAmt.setValue(BigDecimal.ZERO);
					balanceAmt = detail.getBalanceAmount();
					detail.setCurrWaiverAmount(amount);
					totCurrWaivedAmt = totCurrWaivedAmt.add(amount);
					break;
				} else {
					totCurrWaivedAmt = totCurrWaivedAmt.subtract(detail.getCurrWaiverAmount());
					balanceAmt = detail.getBalanceAmount();
					detail.setCurrWaiverAmount(amount);
					totCurrWaivedAmt = totCurrWaivedAmt.add(amount);
					break;
				}
			}
			count++;
		}
		List<Listcell> listCells = listFeeWaiverDetails.getItems().get(count - 1).getChildren();
		Listcell currWaivedAmtCell = listCells.get(6);
		Listcell netbala = listCells.get(7);
		Decimalbox currWaivedAomount = (Decimalbox) currWaivedAmtCell.getChildren().get(0);
		Label netbal = (Label) netbala.getChildren().get(0);
		if (currWaivedAmt.equals(currWaivedAomount)) {
			netbal.setValue(PennantApplicationUtil.amountFormate(balanceAmt.subtract(amount), ccyFormatter));
		} else {
			currWaivedAmt.setValue(BigDecimal.ZERO);
		}

		List<Listcell> totListCells = listFeeWaiverDetails.getItems().get(listSize - 1).getChildren();
		Listcell totCurrWaivedCell = totListCells.get(6);
		Label totCurrWaived = (Label) totCurrWaivedCell.getChildren().get(0);
		totCurrWaived.setValue(PennantApplicationUtil.amountFormate(totCurrWaivedAmt, ccyFormatter));

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.feeWaiverHeader);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceSelectCtrl().getSearchObj(true);
		getFinanceSelectCtrl().getPagingFinanceList().setActivePage(0);
		getFinanceSelectCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceSelectCtrl().getListBoxFinance() != null) {
			getFinanceSelectCtrl().getListBoxFinance().getListModel();
		}
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(FinanceMain aFinanceMain) {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", getHeaderBasicDetails(this.financeMain));
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}

	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getHeaderBasicDetails(FinanceMain aFinanceMain) {

		ArrayList<Object> arrayList = new ArrayList<Object>();
		Customer customer = getFinanceDetail().getCustomerDetails().getCustomer();
		arrayList.add(0, aFinanceMain.getFinType());
		arrayList.add(1, aFinanceMain.getFinCcy());
		arrayList.add(2, aFinanceMain.getScheduleMethod());
		arrayList.add(3, aFinanceMain.getFinReference());
		arrayList.add(4, aFinanceMain.getProfitDaysBasis());
		arrayList.add(5, null);
		arrayList.add(6, false);
		arrayList.add(7, false);
		arrayList.add(8, null);
		arrayList.add(9, customer == null ? "" : customer.getCustShrtName());
		arrayList.add(10, true);
		arrayList.add(11, null);
		return arrayList;
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.feeWaiverHeader.getWaiverId());
	}

	public FinanceSelectCtrl getFinanceSelectCtrl() {
		return financeSelectCtrl;
	}

	public void setFinanceSelectCtrl(FinanceSelectCtrl financeSelectCtrl) {
		this.financeSelectCtrl = financeSelectCtrl;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public FeeWaiverHeader getFeeWaiverHeader() {
		return feeWaiverHeader;
	}

	public void setFeeWaiverHeader(FeeWaiverHeader feeWaiverHeader) {
		this.feeWaiverHeader = feeWaiverHeader;
	}

	public List<FeeWaiverDetail> getFeeWaiverDetails() {
		return feeWaiverDetails;
	}

	public void setFeeWaiverDetails(List<FeeWaiverDetail> feeWaiverDetails) {
		this.feeWaiverDetails = feeWaiverDetails;
	}

	public FeeWaiverHeaderService getFeeWaiverHeaderService() {
		return feeWaiverHeaderService;
	}

	public void setFeeWaiverHeaderService(FeeWaiverHeaderService feeWaiverHeaderService) {
		this.feeWaiverHeaderService = feeWaiverHeaderService;
	}
}
