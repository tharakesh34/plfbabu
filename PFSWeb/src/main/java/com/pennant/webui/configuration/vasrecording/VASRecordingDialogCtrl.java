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
 * FileName    		:  VASRecordingDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2016    														*
 *                                                                  						*
 * Modified Date    :  02-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2016       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.configuration.vasrecording;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.MailUtil;
import com.pennant.app.util.ReferenceUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerEMailDAO;
import com.pennant.backend.model.ScriptError;
import com.pennant.backend.model.ScriptErrors;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.configuration.VasCustomer;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.collateral.impl.ScriptValidationService;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.component.extendedfields.ExtendedFieldsGenerator;
import com.pennant.core.EventManager;
import com.pennant.core.EventManager.Notify;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.finance.financemain.AgreementDetailDialogCtrl;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.finance.financemain.FinFeeDetailListCtrl;
import com.pennant.webui.finance.financemain.FinVasRecordingDialogCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.solutionfactory.extendedfielddetail.ExtendedFieldRenderDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /WEB-INF/pages/configuration/VASRecording/vASRecordingDialog.zul file. <br>
 */
public class VASRecordingDialogCtrl extends GFCBaseCtrl<VASRecording> {
	private static final long								serialVersionUID		= 1L;
	private static final Logger								logger					= Logger.getLogger(VASRecordingDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting by our 'extends GFCBaseCtrl'
	 * GenericForwardComposer. ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window										window_VASRecordingDialog;
	protected Label											windowTitle;
	protected ExtendedCombobox								productCode;
	protected Combobox										postingAgainst;
	protected Textbox										primaryLinkRef;
	protected Textbox										vasReference;
	protected CurrencyBox									fee;
	protected CurrencyBox									renewalFee;
	protected Combobox										feePaymentMode;
	protected Space											space_FeePaymentMode;
	protected Datebox										valueDate;
	protected Datebox										accrualTillDate;
	protected Datebox										recurringDate;
	protected ExtendedCombobox								dsaId;
	protected ExtendedCombobox								dmaId;
	protected ExtendedCombobox								fulfilOfficerId;
	protected ExtendedCombobox								referralId;
	protected Button										viewInfo;
	protected Button										btnSearchSelection;
	protected Row											row_Vasfee;
	
	private VASRecording									vASRecording;
	private Textbox											enquiryType;
	private transient VASRecordingListCtrl					vASRecordingListCtrl;
	private transient FinVasRecordingDialogCtrl				finVasRecordingDialogCtrl;
	private VASConfiguration								vASConfiguration		= null;
	
	// ServiceDAOs / Domain Classes
	private transient VASRecordingService					vASRecordingService;
	private transient CollateralSetupService				collateralSetupService;
	private transient FinanceDetailService					financeDetailService;
	private transient CustomerDetailsService				customerDetailsService;
	protected JdbcSearchObject<Customer>					custCIFSearchObject;
	private Window											mainWindow				= null;

	protected Tabbox										tabBoxIndexCenter;
	protected Tabs											tabsIndexCenter;
	protected Tabpanels										tabpanelsBoxIndexCenter;
	protected Tab											basicDetailsTab;
	protected Tab											extendedDetailsTab;														//NOt required
	protected Tabpanel										extendedFieldTabpanel;														//NOt required
	protected Component										checkListChildWindow;
	private transient ExtendedFieldRenderDialogCtrl			extendedFieldRenderDialogCtrl;
	private transient DocumentDetailDialogCtrl				documentDetailDialogCtrl;
	private transient AccountingDetailDialogCtrl 			accountingDetailDialogCtrl;
	private transient AgreementDetailDialogCtrl				agreementDetailDialogCtrl;
	private transient FinanceCheckListReferenceDialogCtrl	financeCheckListReferenceDialogCtrl;
	protected String										selectMethodName		= "onSelectTab";
	private String											moduleType				= "";

	private List<FinanceCheckListReference>					vasChecklists			= null;
	private HashMap<Long, Long>								selectedAnsCountMap		= null;
	protected Map<String, Object>							flagTypeDataMap			= new HashMap<String, Object>();
	private ExtendedFieldsGenerator							generator;
	protected Tabpanel										extendedFieldTabPanel;
	private ScriptValidationService							scriptValidationService;
	private String											preValidationScript;
	private String											postValidationScript;
	private ExtendedFieldHeader								extendedFieldHeader;
	private ExtendedFieldRender								extendedFieldRender;
	
	private FinanceReferenceDetailService					financeReferenceDetailService;			
	private CustomerEMailDAO 								customerEMailDAO;		
	private MailUtil 										mailUtil;
	private EventManager 									eventManager;
	
	private boolean 										financeVas = false;
	private boolean 										feeEditable = false;
	private boolean 										newRecord = false;
	private List<VASRecording> 								vasRecordings;
	private List<FinFeeDetail>								finFeeDetailsList = new ArrayList<FinFeeDetail>();
	
	private AccountEngineExecution 							engineExecution;
	private boolean isAccountingExecuted = true;
	private boolean isCancelProcess = false;
	protected Row 											row_VASPaid;
	protected CurrencyBox									paidAmt;
	protected CurrencyBox									waivedAmt;
	
	/**
	 * default constructor.<br>
	 */
	public VASRecordingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "VASRecordingDialog";
		super.moduleCode = "VASRecording";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_VASRecordingDialog(Event event) throws Exception {
		logger.debug("Entring" + event.toString());

		// Set the page level components.
		setPageComponents(window_VASRecordingDialog);

		try {
			
			if (arguments.containsKey("module")) {
				String module = (String) arguments.get("module");
				if (StringUtils.equals("E" , module)) {
					enqiryModule = true;
					moduleType = PennantConstants.MODULETYPE_ENQ;
				}else if (StringUtils.equals("C" , module)) {
					isCancelProcess = true;
				}
			}
			
			// Store the before image.
			if (arguments.containsKey("vASRecording")) {
				this.vASRecording = (VASRecording) arguments.get("vASRecording");
				VASRecording befImage = new VASRecording();
				BeanUtils.copyProperties(this.vASRecording, befImage);
				this.vASRecording.setBefImage(befImage);
				
				setVASRecording(this.vASRecording);
			} else {
				setVASRecording(null);
			}
			
			if(arguments.containsKey("finVasRecordingDialogCtrl")){

				setFinVasRecordingDialogCtrl((FinVasRecordingDialogCtrl) arguments.get("finVasRecordingDialogCtrl"));
				setFinanceVas(true);
				
				if (arguments.containsKey("feeEditable")) {
					setFeeEditable((boolean) arguments.get("feeEditable"));
				} 

				if(arguments.containsKey("newRecord")){
					setNewRecord(true);
					setFeeEditable(true);
				}else{
					setNewRecord(false);
				}
				this.vASRecording.setWorkflowId(0);
				if(arguments.containsKey("roleCode")){
					setRole((String) arguments.get("roleCode"));
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}
			
			// Render the page and display the data.
			if (!enqiryModule) {
				doLoadWorkFlow(this.vASRecording.isWorkflow(), this.vASRecording.getWorkflowId(),
						this.vASRecording.getNextTaskId());
			}

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			} 

			// Get the required arguments.
			if (arguments.containsKey("vASRecordingListCtrl")) {
				setVASRecordingListCtrl((VASRecordingListCtrl) arguments.get("vASRecordingListCtrl"));
			} else {
				setVASRecordingListCtrl(null);
			}
			
			// set Field Properties
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(getVASRecording());
			
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
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
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws InterruptedException
	 * @throws InterfaceException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException, InterfaceException, IllegalAccessException, InvocationTargetException {
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws ScriptException 
	 */
	public void onClick$btnCancel(Event event) throws ParseException, InterruptedException, ScriptException {
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
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.vASRecording);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (this.vASRecording.isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.btnSearchSelection.setDisabled(false);
		} else {
			this.btnCancel.setVisible(true);
			this.btnSearchSelection.setDisabled(true);
		}
		if(isFinanceVas()){
			this.row_VASPaid.setVisible(false);
		}else{
			this.row_VASPaid.setVisible(true);
		}
		this.productCode.setReadonly(true);
		this.postingAgainst.setDisabled(true);
		this.primaryLinkRef.setReadonly(true);
		this.vasReference.setReadonly(true);

		this.feePaymentMode.setDisabled(isReadOnly("VASRecordingDialog_FeePaymentMode"));
		this.valueDate.setDisabled(isReadOnly("VASRecordingDialog_ValueDate"));
		this.dsaId.setReadonly(isReadOnly("VASRecordingDialog_DsaId"));
		this.dmaId.setReadonly(isReadOnly("VASRecordingDialog_DmaId"));
		this.fulfilOfficerId.setReadonly(isReadOnly("VASRecordingDialog_FulfilOfficerId"));
		this.referralId.setReadonly(isReadOnly("VASRecordingDialog_ReferralId"));
		this.waivedAmt.setReadonly(isReadOnly("VASRecordingDialog_WaivedAmt"));
		if(isFinanceVas()){
			this.viewInfo.setVisible(false);
		}else{
			this.viewInfo.setVisible(false);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.vASRecording.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if(isFinanceVas()){
				if(enqiryModule){
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					this.btnCancel.setVisible(false);
				}else if (isNewRecord()){
					this.btnCtrl.setBtnStatus_Edit();
					this.btnCancel.setVisible(false);
				}else{
					//this.btnCtrl.setWFBtnStatus_Edit(isFinanceVas());
					this.btnCancel.setVisible(false);
				}
			}else{
				this.btnCtrl.setBtnStatus_Edit();
				this.btnCancel.setVisible(true);
			}
		}

		logger.debug("Leaving ");
	}

	/**
	 * Deletes a VASConfiguration object from database.<br>
	 * 
	 * @throws InterruptedException
	 * @throws InterfaceException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private void doDelete() throws InterruptedException, InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		
		final VASRecording aVASRecording = new VASRecording();
		BeanUtils.copyProperties(getVASRecording(), aVASRecording);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " +
		Labels.getLabel("label_VASReference")	+" : "+ aVASRecording.getVasReference();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aVASRecording.getRecordType()).equals("")) {
				aVASRecording.setVersion(aVASRecording.getVersion() + 1);
				aVASRecording.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aVASRecording.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aVASRecording.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aVASRecording.getNextTaskId(), aVASRecording);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if(isFinanceVas()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  processVasRecords(aVASRecording,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_VASRecordingDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						getFinVasRecordingDialogCtrl().doFillVasRecordings(this.vasRecordings);
						
						removeFeesFromFeeList(aVASRecording.getVasReference());
						// send the data back to customer
						closeDialog();
					}	

				}else if (doProcess(aVASRecording, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	public void doClearMessage() {
		logger.debug("Entering");
		this.productCode.setErrorMessage("");
		this.postingAgainst.setErrorMessage("");
		this.primaryLinkRef.setErrorMessage("");
		this.vasReference.setErrorMessage("");
		this.fee.setErrorMessage("");
		this.waivedAmt.setErrorMessage("");
		this.paidAmt.setErrorMessage("");
		this.renewalFee.setErrorMessage("");
		this.feePaymentMode.setErrorMessage("");
		this.valueDate.setErrorMessage("");
		this.accrualTillDate.setErrorMessage("");
		this.recurringDate.setErrorMessage("");
		this.dsaId.setErrorMessage("");
		this.dmaId.setErrorMessage("");
		this.fulfilOfficerId.setErrorMessage("");
		this.referralId.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	private void refreshList() {
		final JdbcSearchObject<VASRecording> soVASConfiguration = getVASRecordingListCtrl().getSearchObject();
		getVASRecordingListCtrl().pagingVASRecordingList.setActivePage(0);
		getVASRecordingListCtrl().getPagedListWrapper().setSearchObject(soVASConfiguration);
		if (getVASRecordingListCtrl().listBoxVASRecording != null) {
			getVASRecordingListCtrl().listBoxVASRecording.getListModel();
		}
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws ScriptException 
	 * 
	 */
	private void doCancel() throws ParseException, InterruptedException, ScriptException {
		logger.debug("Entering");

		doWriteBeanToComponents(this.vASRecording.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");

		final VASRecording aVASRecording = new VASRecording();
		BeanUtils.copyProperties(getVASRecording(), aVASRecording);
		boolean isNew = false;

		doClearMessage();
		doSetValidation();
		// fill the FinanceType object with the components data
		doWriteComponentsToBean(aVASRecording, true);
		 
		//Finance CheckList Details Saving
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aVASRecording, false);
			if (!validationSuccess) {
				return;
			}
		} else {
			aVASRecording.setCheckLists(getVASRecording().getCheckLists());
		}

		//Document Details Saving
		if (documentDetailDialogCtrl != null) {
			aVASRecording.setDocuments(documentDetailDialogCtrl.getDocumentDetailsList());
		} else {
			aVASRecording.setDocuments(getVASRecording().getDocuments());
		}

		// Accounting Details Validations
		if (getAccountingDetailDialogCtrl() != null && isAccountingExecuted) {
			MessageUtil.showError(Labels.getLabel("label_Finance_Calc_Accountings"));
			return;
		}
		
		// doStoreInitValues();
		isNew = aVASRecording.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aVASRecording.getRecordType())) {
				aVASRecording.setVersion(aVASRecording.getVersion() + 1);
				if (isNew) {
					aVASRecording.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aVASRecording.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aVASRecording.setNewRecord(true);
				}
			}
		} else {
			
			if(isFinanceVas()){
				if(isNewRecord()){
					aVASRecording.setVersion(1);
					aVASRecording.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}

				if(StringUtils.isBlank(aVASRecording.getRecordType())){
					aVASRecording.setVersion(aVASRecording.getVersion()+1);
					aVASRecording.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aVASRecording.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aVASRecording.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}

			}else{
				aVASRecording.setVersion(aVASRecording.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}
		// save it to database
		try {
			
			if(isFinanceVas()){
				aVASRecording.setFeeAccounting(vASConfiguration.getFeeAccounting());
				aVASRecording.setFinanceProcess(true);
				AuditHeader auditHeader =  processVasRecords(aVASRecording,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_VASRecordingDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getFinVasRecordingDialogCtrl().doFillVasRecordings(this.vasRecordings);
					if(isFeeEditable()){
						FinFeeDetail finFeeDetail = new FinFeeDetail();
						finFeeDetail.setVasReference(aVASRecording.getVasReference());
						finFeeDetail.setOriginationFee(true);
						finFeeDetail.setFinEvent(AccountEventConstants.ACCEVENT_VAS_FEE);
						finFeeDetail.setFeeTypeID(0);
						finFeeDetail.setFeeSeq(0);
						finFeeDetail.setFeeOrder(0);
						finFeeDetail.setCalculatedAmount(aVASRecording.getFee());
						//finFeeDetail.setActualAmount(aVASRecording.getFee());
						finFeeDetail.setCalculationType(PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT);
						//finFeeDetail.setRemainingFee(aVASRecording.getFee());
						finFeeDetail.setFixedAmount(aVASRecording.getFee());
						finFeeDetail.setCalculateOn(PennantConstants.List_Select);
						finFeeDetail.setAlwDeviation(true);
						finFeeDetail.setMaxWaiverPerc(BigDecimal.valueOf(100));
						finFeeDetail.setAlwModifyFee(vASConfiguration.isAllowFeeToModify());
						finFeeDetail.setAlwModifyFeeSchdMthd(true);
						
						finFeeDetail.setTaxComponent("");
						finFeeDetail.setTaxApplicable(false);
						
						finFeeDetail.setActualAmountOriginal(aVASRecording.getFee());
						finFeeDetail.setActualAmountGST(BigDecimal.ZERO);
						finFeeDetail.setActualAmount(aVASRecording.getFee());
						
						finFeeDetail.setRemainingFeeOriginal(aVASRecording.getFee());
						finFeeDetail.setRemainingFeeGST(BigDecimal.ZERO);
						finFeeDetail.setRemainingFee(aVASRecording.getFee());
						
						if(!StringUtils.equals(aVASRecording.getFeePaymentMode(), PennantConstants.List_Select)){
							finFeeDetail.setFeeScheduleMethod(CalculationConstants.REMFEE_PAID_BY_CUSTOMER);
							//finFeeDetail.setPaidAmount(aVASRecording.getFee());
							//finFeeDetail.setRemainingFee(BigDecimal.ZERO);
							
							finFeeDetail.setPaidAmountOriginal(aVASRecording.getFee());
							finFeeDetail.setPaidAmountGST(BigDecimal.ZERO);
							finFeeDetail.setPaidAmount(aVASRecording.getFee());
							
							finFeeDetail.setRemainingFeeOriginal(BigDecimal.ZERO);
							finFeeDetail.setRemainingFeeGST(BigDecimal.ZERO);
							finFeeDetail.setRemainingFee(BigDecimal.ZERO);
						}
						
						finFeeDetail.setNetAmountOriginal(aVASRecording.getFee());
						finFeeDetail.setNetAmountGST(BigDecimal.ZERO);
						finFeeDetail.setNetAmount(aVASRecording.getFee());
						
						finFeeDetail.setRecordType(PennantConstants.RCD_ADD);
						finFeeDetailsList.add(finFeeDetail);
						
						appendFeesToFeeList(finFeeDetail);
						
					}
					aVASRecording.setFinFeeDetailsList(finFeeDetailsList);
					
					// send the data back to customer
					closeDialog();
				}	

			}else if (doProcess(aVASRecording, tranType)) {

				//Mail Alert Notification for Customer/Dealer/Provider...etc
				if(!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())){

					List<String> templateTyeList = new ArrayList<String>();
					templateTyeList.add(NotificationConstants.TEMPLATE_FOR_AE);
					templateTyeList.add(NotificationConstants.TEMPLATE_FOR_CN);

					String moduleDefiner = "";
					List<ValueLabel> referenceIdList = getFinanceReferenceDetailService().getTemplateIdList(aVASRecording.getProductCode(), 
							StringUtils.isEmpty(moduleDefiner) ? FinanceConstants.FINSER_EVENT_ORG : moduleDefiner, getRole(), templateTyeList);

					templateTyeList = null;
					if (!referenceIdList.isEmpty()) {

						boolean isCustomerNotificationExists = false;
						List<Long> notificationIdlist = new ArrayList<Long>();
						for (ValueLabel valueLabel : referenceIdList) {
							notificationIdlist.add(Long.valueOf(valueLabel.getValue()));
							if (NotificationConstants.TEMPLATE_FOR_CN.equals(valueLabel.getLabel())) {
								isCustomerNotificationExists = true;
							}
						}

						// Mail ID details preparation
						Map<String, List<String>> mailIDMap = new HashMap<String, List<String>>();

						List<CustomerEMail> emailList = getCustomerEMailDAO().getCustomerEmailByCustomer(aVASRecording.getVasCustomer().getCustomerId(), "");

						// Customer Email Preparation
						if (isCustomerNotificationExists && emailList != null && !emailList.isEmpty()) {
							List<String> custMailIdList = new ArrayList<String>();
							for (CustomerEMail customerEMail : emailList) {
								custMailIdList.add(customerEMail.getCustEMail());
							}
							if (!custMailIdList.isEmpty()) {
								mailIDMap.put(NotificationConstants.TEMPLATE_FOR_CN, custMailIdList);
							}
						}
						//getMailUtil().sendMail(notificationIdlist, aFinanceDetail, mailIDMap, null);
						try {
							getMailUtil().sendMail(notificationIdlist, null, mailIDMap, null);
						} catch (Exception e) {
							logger.error("Exception: ", e);
						}
					}
				}

				// User Notifications Message/Alert
				try{
					if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel()) && !"Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel()) &&
							!this.userAction.getSelectedItem().getLabel().contains("Reject")) {
						// Send message Notification to Users
						String reference = aVASRecording.getVasReference();
						String nextRoleCodes = aVASRecording.getNextRoleCode();
						if(StringUtils.isNotEmpty(nextRoleCodes)){
							Notify notify = Notify.valueOf("ROLE");
							String[] to=nextRoleCodes.split(",");					
							if(StringUtils.isNotEmpty(reference)){
								if(!PennantConstants.RCD_STATUS_CANCELLED.equalsIgnoreCase(aVASRecording.getRecordStatus())){
									getEventManager().publish(Labels.getLabel("REC_PENDING_MESSAGE")+" with Reference"+":"+reference, notify,to);
								}
							} else {
								getEventManager().publish(Labels.getLabel("REC_PENDING_MESSAGE"), notify,to);
							} 
						}
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				}

				// List Detail Refreshment
				refreshList();

				// Confirmation message
				String msg = PennantApplicationUtil.getSavingStatus(aVASRecording.getRoleCode(),aVASRecording.getNextRoleCode(), 
						aVASRecording.getVasReference(), " Vas ", aVASRecording.getRecordStatus());
				Clients.showNotification(msg,  "info", null, null, -1);

				closeDialog();
			}
		} catch (DataAccessException e) {
			MessageUtil.showError(e);
		} catch (AppException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
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
	 * @throws InterfaceException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * 
	 */

	private boolean doProcess(VASRecording aVASRecording, String tranType)  throws InterfaceException {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aVASRecording.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aVASRecording.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aVASRecording.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aVASRecording.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aVASRecording.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aVASRecording);
				}

				if (isNotesMandatory(taskId, aVASRecording)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
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

			aVASRecording.setTaskId(taskId);
			aVASRecording.setNextTaskId(nextTaskId);
			aVASRecording.setRoleCode(getRole());
			aVASRecording.setNextRoleCode(nextRoleCode);

			//Extended Field details
			if(aVASRecording.getExtendedFieldRender() !=null){
				ExtendedFieldRender details = aVASRecording.getExtendedFieldRender();
				details.setReference(aVASRecording.getVasReference());
				details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				details.setRecordStatus(aVASRecording.getRecordStatus());
				details.setRecordType(aVASRecording.getRecordType());
				details.setVersion(aVASRecording.getVersion());
				details.setWorkflowId(aVASRecording.getWorkflowId());
				details.setTaskId(taskId);
				details.setNextTaskId(nextTaskId);
				details.setRoleCode(getRole());
				details.setNextRoleCode(nextRoleCode);
				details.setNewRecord(aVASRecording.isNewRecord());
				if (PennantConstants.RECORD_TYPE_DEL.equals(aVASRecording.getRecordType())) {
					if (StringUtils.trimToNull(details.getRecordType()) == null) {
						details.setRecordType(aVASRecording.getRecordType());
						details.setNewRecord(true);
					}
				}
			}

			//Document Details
			if (aVASRecording.getDocuments() != null && !aVASRecording.getDocuments().isEmpty()) {
				for (DocumentDetails details : aVASRecording.getDocuments()) {
					if (StringUtils.isEmpty(StringUtils.trimToEmpty(details.getRecordType()))) {
						continue;
					}
					details.setReferenceId(aVASRecording.getVasReference());
					details.setDocModule(VASConsatnts.MODULE_NAME);
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(aVASRecording.getRecordStatus());
					details.setWorkflowId(aVASRecording.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aVASRecording.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aVASRecording.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}
			
			//CheckList details
			if (aVASRecording.getVasCheckLists() != null && !aVASRecording.getVasCheckLists().isEmpty()) {
				for (FinanceCheckListReference details : aVASRecording.getVasCheckLists()) {
					details.setFinReference(aVASRecording.getVasReference());
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(aVASRecording.getRecordStatus());
					details.setWorkflowId(aVASRecording.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aVASRecording.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aVASRecording.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}
			
			auditHeader = getAuditHeader(aVASRecording, tranType);

			String operationRefs = getServiceOperations(taskId, aVASRecording);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aVASRecording, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aVASRecording, tranType);
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
	 * @throws InterfaceException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterfaceException {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		VASRecording aVASRecording = (VASRecording) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getVASRecordingService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getVASRecordingService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getVASRecordingService().doApprove(auditHeader);
						if (aVASRecording.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getVASRecordingService().doReject(auditHeader);
						if (aVASRecording.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_VASRecordingDialog, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(this.window_VASRecordingDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.vASRecording), true);
					}
				}
				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}
	
	/**
	 * Method for Processing VAS Records List on Finance Origination Process
	 * @param aVASRecording
	 * @param tranType
	 * @return
	 */
	private AuditHeader processVasRecords(VASRecording aVASRecording,String tranType){
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(aVASRecording, tranType);
		vasRecordings = new ArrayList<VASRecording>();

		String[] valueParm = new String[1];
		String[] errParm = new String[1];

		valueParm[0] = String.valueOf(aVASRecording.getProductCode());
		errParm[0] = PennantJavaUtil.getLabel("label_VASCode") + ":"+ valueParm[0];

		if(getFinVasRecordingDialogCtrl().getVasRecordings() != null && !getFinVasRecordingDialogCtrl().getVasRecordings().isEmpty()){
			for (int i = 0; i < getFinVasRecordingDialogCtrl().getVasRecordings().size(); i++) {
				VASRecording vasRecording = getFinVasRecordingDialogCtrl().getVasRecordings().get(i);

				// Both Current and Existing list Vas Reference same
				if(StringUtils.equals(vasRecording.getProductCode(),aVASRecording.getProductCode())){ 

					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), 
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if(aVASRecording.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aVASRecording.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							vasRecordings.add(aVASRecording);
						}else if(aVASRecording.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aVASRecording.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aVASRecording.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							vasRecordings.add(aVASRecording);
						}else if(aVASRecording.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getFinVasRecordingDialogCtrl().getVasRecordings().size(); j++) {
								VASRecording recording =  getFinVasRecordingDialogCtrl().getVasRecordings().get(j);
								if(StringUtils.equals(recording.getProductCode(),aVASRecording.getProductCode())){
									vasRecordings.add(recording);
								}
							}
						}
					}else{
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							vasRecordings.add(vasRecording);
						}
					}
				}else{
					vasRecordings.add(vasRecording);
				}
			}
		}
		if(!recordAdded){
			vasRecordings.add(aVASRecording);
		}
		
		//Extended Field details
		if(aVASRecording.getExtendedFieldRender() !=null){
			ExtendedFieldRender details = aVASRecording.getExtendedFieldRender();
			details.setReference(aVASRecording.getVasReference());
			details.setTypeCode(aVASRecording.getProductCode());
			details.setTypeCodeDesc(aVASRecording.getProductDesc());
			details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			details.setRecordStatus(aVASRecording.getRecordStatus());
			details.setRecordType(aVASRecording.getRecordType());
			details.setVersion(aVASRecording.getVersion());
			details.setWorkflowId(aVASRecording.getWorkflowId());
			details.setTaskId(taskId);
			details.setNextTaskId(nextTaskId);
			details.setRoleCode(getRole());
			details.setNextRoleCode(nextRoleCode);
			details.setNewRecord(aVASRecording.isNewRecord());
			if (PennantConstants.RECORD_TYPE_DEL.equals(aVASRecording.getRecordType())) {
				if (StringUtils.trimToNull(details.getRecordType()) == null) {
					details.setRecordType(aVASRecording.getRecordType());
					details.setNewRecord(true);
				}
			}
		}
		
		return auditHeader;
	} 

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(VASRecording aVASRecording, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aVASRecording.getBefImage(), aVASRecording);
		return new AuditHeader(aVASRecording.getVasReference(), null, null, null, auditDetail,
				aVASRecording.getUserDetails(), getOverideMap());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aVASRecording
	 * @throws InterruptedException
	 */
	public void doShowDialog(VASRecording aVASRecording) throws InterruptedException {
		logger.debug("Entering");

		if (aVASRecording.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.fee.focus();
		} else {
			if (isFinanceVas()){
				doEdit();
			}else if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aVASRecording.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				if (isCancelProcess || enqiryModule) {
					doReadOnly();
				} else {
					doEdit();
				}
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
		}
		this.paidAmt.setReadonly(true);
		this.paidAmt.setDisabled(true);
		try {
			// fill the components with the data
			doWriteBeanToComponents(aVASRecording);
			
			if(enqiryModule){
				this.windowTitle.setValue(Labels.getLabel("window_VASRecordingEnquiryDialog.title"));
			}else  if(isCancelProcess){
				this.windowTitle.setValue(Labels.getLabel("window_VASRecordingCancelDialog.title"));
			}

			if(isFinanceVas()){
				
				// Height Calculation
				int height = borderLayoutHeight - 120;
				this.window_VASRecordingDialog.setHeight(height+"px");
				this.window_VASRecordingDialog.setWidth("90%");
				this.groupboxWf.setVisible(false);
				this.window_VASRecordingDialog.doModal() ;
			}else if(enqiryModule){
				this.groupboxWf.setVisible(false);
				setDialog(DialogType.EMBEDDED);
			}else{
				setDialog(DialogType.EMBEDDED);
			}
			
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	protected void doSetValidation() {
		logger.debug("Entering");
		
		if(isCancelProcess || enqiryModule){
			return;
		}

		if (!this.productCode.isButtonDisabled()) {
			this.productCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_VASRecordingDialog_ProductCode.value"), null, true));
		}

		if (!this.primaryLinkRef.isReadonly()) {
			this.primaryLinkRef.setConstraint(new PTStringValidator(Labels
					.getLabel("label_VASRecordingDialog_PrimaryLinkRef.value"), null, true));
		}

		if (!this.fee.isReadonly()) {
			this.fee.setConstraint(new PTDecimalValidator(Labels.getLabel("label_VASRecordingDialog_Fee.value"), 2,
					false, false));
		}

		if (!this.renewalFee.isReadonly()) {
			this.renewalFee.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_VASRecordingDialog_RenewalFee.value"), 2, false, false));
		}

		if (!this.valueDate.isDisabled()) {
			this.valueDate.setConstraint(new PTDateValidator(Labels
					.getLabel("label_VASRecordingDialog_ValueDate.value"), true, SysParamUtil.getValueAsDate(PennantConstants.APP_DFT_START_DATE), 
					DateUtility.getAppDate(), true));
		}
		
		if (!this.accrualTillDate.isDisabled()) {
			this.accrualTillDate.setConstraint(new PTDateValidator(Labels
					.getLabel("label_VASRecordingDialog_AccrualTillDate.value"), true, DateUtility.getAppDate(), 
					SysParamUtil.getValueAsDate("APP_DFT_END_DATE"), true));
		}
		
		if (!this.recurringDate.isDisabled()) {
			this.recurringDate.setConstraint(new PTDateValidator(Labels
					.getLabel("label_VASRecordingDialog_RecurringDate.value"), true, DateUtility.getAppDate(), 
					SysParamUtil.getValueAsDate("APP_DFT_END_DATE"), true));
		}

		if (!this.dsaId.isButtonDisabled()) {
			this.dsaId.setConstraint(new PTStringValidator(Labels.getLabel("label_VASRecordingDialog_DsaId.value"),
					null, false));
		}
		if (!this.dmaId.isButtonDisabled()) {
			this.dmaId.setConstraint(new PTStringValidator(Labels.getLabel("label_VASRecordingDialog_DmaId.value"),
					null, false));
		}
		if (!this.fulfilOfficerId.isButtonDisabled()) {
			this.fulfilOfficerId.setConstraint(new PTStringValidator(Labels
					.getLabel("label_VASRecordingDialog_FulfilOfficerId.value"), null, false));
		}
		if (!this.referralId.isButtonDisabled()) {
			this.referralId.setConstraint(new PTStringValidator(Labels
					.getLabel("label_VASRecordingDialog_ReferralId.value"), null, false));
		}
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aVASConfiguration
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws ScriptException 
	 * 
	 */
	public void doWriteBeanToComponents(VASRecording aVASRecording) throws ParseException, InterruptedException, ScriptException {
		logger.debug("Entering");

		 vASConfiguration = aVASRecording.getVasConfiguration();
		if (aVASRecording.isNewRecord()) {
			this.dsaId.setDescription("");
			this.dmaId.setDescription("");
			this.fulfilOfficerId.setDescription("");
			this.referralId.setDescription("");
			this.valueDate.setValue(DateUtility.getAppDate());
			this.accrualTillDate.setValue(DateUtility.getAppDate());
			this.recurringDate.setValue(DateUtility.getAppDate());
			this.paidAmt.setValue(PennantApplicationUtil.formateAmount(aVASRecording.getFee(), getCcyFormat()));
		} else {
			this.dsaId.setDescription(aVASRecording.getDsaId());
			this.dmaId.setDescription(aVASRecording.getDmaId());
			this.fulfilOfficerId.setDescription(aVASRecording.getFulfilOfficerId());
			this.referralId.setDescription(aVASRecording.getReferralId());
			this.valueDate.setValue(aVASRecording.getValueDate());
			this.accrualTillDate.setValue(aVASRecording.getAccrualTillDate());
			this.recurringDate.setValue(aVASRecording.getRecurringDate());
			this.paidAmt.setValue(PennantApplicationUtil.formateAmount(aVASRecording.getPaidAmt(), getCcyFormat()));
		}
		
		// Product Code
		this.productCode.setValue(aVASRecording.getProductCode());
		this.productCode.setDescription(aVASRecording.getProductDesc());
		
		// Primary Link Reference
		this.primaryLinkRef.setValue(aVASRecording.getPrimaryLinkRef());
		fillComboBox(this.postingAgainst, aVASRecording.getPostingAgainst(),PennantStaticListUtil.getRecAgainstTypes(), "");
		fillComboBox(this.feePaymentMode, aVASRecording.getFeePaymentMode(), PennantStaticListUtil.getFeeTypes(), "");

		//Vas fee
		if(isFinanceVas()){
			if(isFeeEditable() && vASConfiguration.isAllowFeeToModify()){
				this.fee.setReadonly(isReadOnly("VASRecordingDialog_Fee"));
			}else{
				this.fee.setReadonly(true);
			}
		}else{
			if (vASConfiguration.isAllowFeeToModify() && !isCancelProcess && !enqiryModule) {
				this.fee.setReadonly(isReadOnly("VASRecordingDialog_Fee"));
			} else {
				this.fee.setReadonly(true);
			}
		}
		if(!isFinanceVas()){
			this.fee.addForward("onFulfill", this.window_VASRecordingDialog, "onFeeAmountChange");
		}
		this.fee.setValue(PennantApplicationUtil.formateAmount(aVASRecording.getFee(), getCcyFormat()));
		this.waivedAmt.setValue(PennantApplicationUtil.formateAmount(aVASRecording.getWaivedAmt(), getCcyFormat()));
		
		// Payment Mode
		if(isFinanceVas()){
			this.space_FeePaymentMode.setSclass("");
			if(isFeeEditable()){
				this.feePaymentMode.setDisabled(isReadOnly("VASRecordingDialog_FeePaymentMode"));
			}else{
				this.feePaymentMode.setDisabled(true);
			}
		}

		//vasReference
		if (StringUtils.trimToNull(aVASRecording.getVasReference()) == null) {
			this.vasReference.setValue(ReferenceUtil.generateVASRef());
		} else {
			this.vasReference.setValue(aVASRecording.getVasReference());
		}

		// Value Date
		if (aVASRecording.getValueDate() != null) {
			this.valueDate.setValue(aVASRecording.getValueDate());
		}

		// Fee Accrual Till Date
		if (vASConfiguration.isFeeAccrued() && !isCancelProcess && !enqiryModule) {
			this.accrualTillDate.setDisabled(isReadOnly("VASRecordingDialog_AccrualTillDate"));
		} else {
			this.accrualTillDate.setDisabled(true);
		}
		if (aVASRecording.getAccrualTillDate() != null) {
			this.accrualTillDate.setValue(aVASRecording.getAccrualTillDate());
		}

		// Recurring Date
		if (vASConfiguration.isRecurringType() && !isCancelProcess && !enqiryModule) {
			this.recurringDate.setDisabled(isReadOnly("VASRecordingDialog_RecurringDate"));
		} else {
			this.recurringDate.setDisabled(true);
		}
		if (aVASRecording.getRecurringDate() != null) {
			this.recurringDate.setValue(aVASRecording.getRecurringDate());
		}

		//Renewal fee
		if (vASConfiguration.isRecurringType() && !isCancelProcess && !enqiryModule) {
			this.renewalFee.setReadonly(isReadOnly("VASRecordingDialog_Fee"));
		} else {
			this.renewalFee.setReadonly(true);
		}
		this.renewalFee.setValue(PennantApplicationUtil.formateAmount(aVASRecording.getRenewalFee(), getCcyFormat()));

		// Dsa Id
		this.dsaId.setValue(aVASRecording.getDsaId());
		this.dsaId.setDescription(aVASRecording.getDsaIdDesc());

		// Dma Id
		this.dmaId.setValue(aVASRecording.getDmaId());
		this.dmaId.setDescription(aVASRecording.getDmaIdDesc());

		// FulFilOfficer Id
		this.fulfilOfficerId.setValue(aVASRecording.getFulfilOfficerId());
		this.fulfilOfficerId.setDescription(aVASRecording.getFulfilOfficerIdDesc());

		// Referral Id
		this.referralId.setValue(aVASRecording.getReferralId());
		this.referralId.setDescription(aVASRecording.getReferralIdDesc());

		this.recordStatus.setValue(aVASRecording.getRecordStatus());

		//Extended Field Details
		appendExtendedFieldDetails(aVASRecording);

		if (!isFinanceVas()) {
			//Agreements Detail Tab Addition
			if(!enqiryModule && !isCancelProcess){
				appendAgreementsDetailTab(true);

				//CheckList Details Tab Addition
				appendCheckListDetailTab(aVASRecording);
			}

			// Document Detail Tab Addition
			appendDocumentDetailTab();

			//Recommend & Comments Details Tab Addition
			appendRecommendDetailTab(true);

			// Accounting Details Tab Addition
			if(!enqiryModule && !StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole())){
				appendAccountingDetailTab(true);
			}
			
			// Show Posting on Enquiry against Vas Reference
			if(enqiryModule){
				 appendPostingsTab();
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * This method is for append extended field details
	 * 
	 * @throws ScriptException
	 */
	private void appendExtendedFieldDetails(VASRecording aVASRecording) throws ScriptException {
		logger.debug("Entering");

		// Extended Field Details auto population / Rendering into Screen
		generator = new ExtendedFieldsGenerator();
		generator.setWindow(this.window_VASRecordingDialog);
		generator.setTabpanel(extendedFieldTabPanel);
		generator.setRowWidth(220);
		generator.setCcyFormat(getCcyFormat());
		if (enqiryModule || isCancelProcess) {
			generator.setReadOnly(true);
		} else {
			generator.setReadOnly(isReadOnly("VASRecordingDialog_ExtendedFields"));
		}

		VASConfiguration vasConfiguration = aVASRecording.getVasConfiguration();
		setExtendedFieldRender(aVASRecording.getExtendedFieldRender());
		
		//Pre-Validation Checking & Setting Defaults
		Map<String, Object> fieldValuesMap = null;
		if (getExtendedFieldRender() != null && getExtendedFieldRender().getMapValues() != null) {
			fieldValuesMap = aVASRecording.getExtendedFieldRender().getMapValues();
		}

		//setting the pre and post validation scripts
		setPreValidationScript(vasConfiguration.getPreValidation());
		setPostValidationScript(vasConfiguration.getPostValidation());
		setExtendedFieldHeader(vasConfiguration.getExtendedFieldHeader());
		aVASRecording.getRecordType();
		aVASRecording.getRecordStatus();
		if (((isNewRecord() || isFeeEditable()) && isFinanceVas())||(!isFinanceVas()&& aVASRecording.isNewRecord()))  {
			//get pre-validation script if record is new
			String preValidationScript = vasConfiguration.getPreValidation();
			if (StringUtils.isNotEmpty(preValidationScript)) {
				ScriptErrors defaults = getScriptValidationService().setPreValidationDefaults(preValidationScript,
						fieldValuesMap);

				// Initiation of Field Value Map
				if (fieldValuesMap == null) {
					fieldValuesMap = new HashMap<>();
				}

				// Overriding Default values
				List<ScriptError> defaultList = defaults.getAll();
				for (int i = 0; i < defaultList.size(); i++) {
					ScriptError dftKeyValue = defaultList.get(i);

					if (fieldValuesMap.containsKey(dftKeyValue.getProperty())) {
						fieldValuesMap.remove(dftKeyValue.getProperty());
					}
					fieldValuesMap.put(dftKeyValue.getProperty(), dftKeyValue.getValue());
				}
			}
		}
		if (fieldValuesMap != null) {
			generator.setFieldValueMap((HashMap<String, Object>) fieldValuesMap);
		}
		try {
			generator.renderWindow(vasConfiguration.getExtendedFieldHeader(), newRecord);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rendering Joint account and guaranteer Details Data in finance
	 */
	private void appendAgreementsDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");
		boolean createTab = false;
		if (getVASRecording().getAggrements() == null || getVASRecording().getAggrements().isEmpty()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_AGREEMENT) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_AGREEMENT, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_AGREEMENT);
			if (getVASRecording().getAggrements() != null && !getVASRecording().getAggrements().isEmpty()) {
				final HashMap<String, Object> map = getDefaultArguments();
				map.put("agreementList", getVASRecording().getAggrements());
				map.put("financeMainDialogCtrl", this);
				if (enqiryModule || isCancelProcess) {
					map.put("enqModule", true);
				}
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AgreementDetailDialog.zul", getTabpanel(AssetConstants.UNIQUE_ID_AGREEMENT), map);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Preparation of Check List Details Window
	 * 
	 * @param VASRecording
	 * @param finIsNewRecord
	 * @param map
	 */
	private void appendCheckListDetailTab(VASRecording vasRecording) {
		logger.debug("Entering");

		boolean createTab = false;
		if (vasRecording.getCheckLists() != null && !vasRecording.getCheckLists().isEmpty()) {
			if (getTab(AssetConstants.UNIQUE_ID_CHECKLIST) == null) {
				createTab = true;
			}
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_CHECKLIST, false);
		}

		if (vasRecording.getCheckLists() != null && !vasRecording.getCheckLists().isEmpty()) {
			boolean createcheckLsitTab = false;
			for (FinanceReferenceDetail chkList : vasRecording.getCheckLists()) {
				if (chkList.getShowInStage().contains(getRole())) {
					createcheckLsitTab = true;
					break;
				}
				if (chkList.getAllowInputInStage().contains(getRole())) {
					createcheckLsitTab = true;
					break;
				}
			}
			if (createcheckLsitTab) {
				clearTabpanelChildren(AssetConstants.UNIQUE_ID_CHECKLIST);
				final HashMap<String, Object> map = getDefaultArguments();
				map.put("checkList", getVASRecording().getCheckLists());
				map.put("finCheckRefList", getVASRecording().getVasCheckLists());
				map.put("financeMainDialogCtrl", this);
				if (isCancelProcess || enqiryModule) {
					map.put("enqModule", true);
				}
				checkListChildWindow = Executions.createComponents(
						"/WEB-INF/pages/LMTMasters/FinanceCheckListReference/FinanceCheckListReferenceDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_CHECKLIST), map);
				Tab tab = getTab(AssetConstants.UNIQUE_ID_CHECKLIST);
				if (tab != null) {
					tab.setVisible(true);
				}
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Document Details Data in finance
	 */
	private void appendDocumentDetailTab() {
		logger.debug("Entering");
		
		createTab(AssetConstants.UNIQUE_ID_DOCUMENTDETAIL, true);
		
		final HashMap<String, Object> map = getDefaultArguments();
		map.put("documentDetails", getVASRecording().getDocuments());
		map.put("financeMainDialogCtrl", this);
		if (isCancelProcess || enqiryModule) {
			map.put("enqModule", true);
		}
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DocumentDetailDialog.zul",getTabpanel(AssetConstants.UNIQUE_ID_DOCUMENTDETAIL), map);
		logger.debug("Leaving");
	}

	/**
	 * Method for Append Recommend Details Tab
	 * 
	 * @throws InterruptedException
	 */
	private void appendRecommendDetailTab(boolean onLoadProcess) throws InterruptedException {
		logger.debug("Entering");
		if (onLoadProcess) {
			createTab(AssetConstants.UNIQUE_ID_RECOMMENDATIONS, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_RECOMMENDATIONS);
			HashMap<String, Object> map = getDefaultArguments();
			map.put("isFinanceNotes", true);
			map.put("isRecommendMand", false);
			map.put("control", this);
			map.put("notes", getNotes(this.vASRecording));
			if (isCancelProcess || enqiryModule) {
				map.put("enqModule", true);
			}
			try {
				Executions.createComponents("/WEB-INF/pages/notes/notes.zul", getTabpanel(AssetConstants.UNIQUE_ID_RECOMMENDATIONS), map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendAccountingDetailTab(boolean onLoadProcess){
		logger.debug("Entering");
		boolean createTab = false;
		if(getTab(AssetConstants.UNIQUE_ID_ACCOUNTING) == null){
			createTab = true;
		}
		
		if(createTab){
			createTab(AssetConstants.UNIQUE_ID_ACCOUNTING, true);
		}else{
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_ACCOUNTING);
		}
		if (!onLoadProcess) {
			
			final HashMap<String, Object> map = getDefaultArguments();
			map.put("vASRecording", getVASRecording());
			map.put("acSetID", vASConfiguration.getFeeAccounting());
			if (isCancelProcess || enqiryModule) {
				map.put("enqModule", true);
			}
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_ACCOUNTING), map);
			Tab tab = getTab(AssetConstants.UNIQUE_ID_ACCOUNTING);
			if(tab != null){
				tab.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rendering Document Details Data in finance
	 */
	private void appendPostingsTab() {
		logger.debug("Entering");
		
		createTab(AssetConstants.UNIQUE_ID_POSTINGS, true);
		
		final HashMap<String, Object> map = getDefaultArguments();
		map.put("postingDetails", getVASRecording().getReturnDataSetList());
		map.put("dialogCtrl", this);
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/PostingDetailDialog.zul",getTabpanel(AssetConstants.UNIQUE_ID_POSTINGS), map);
		logger.debug("Leaving");
	}

	/**
	 * This method set the check list details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 * @throws Exception
	 */
	private boolean doSave_CheckList(VASRecording vasRecording, boolean isForAgreementGen) throws Exception {
		logger.debug("Entering ");

		boolean validationSuccess = true;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("userAction", this.userAction.getSelectedItem().getLabel());
		map.put("moduleName", VASConsatnts.MODULE_NAME);
		if (isForAgreementGen) {
			map.put("agreement", isForAgreementGen);
		}
		try {
			financeCheckListReferenceDialogCtrl.doSetLabels(getHeaderBasicDetails());
			Events.sendEvent("onChkListValidation", checkListChildWindow, map);
		} catch (Exception e) {
			validationSuccess = false;
			if (e instanceof WrongValuesException) {
				throw e;
			}
		}

		Map<Long, Long> selAnsCountMap = new HashMap<Long, Long>();
		List<FinanceCheckListReference> chkList = getVasChecklists();
		selAnsCountMap = getSelectedAnsCountMap();

		if (chkList != null && chkList.size() >= 0) {
			vasRecording.setVasCheckLists(chkList);
			vasRecording.setSelAnsCountMap(selAnsCountMap);
		}
		logger.debug("Leaving ");
		return validationSuccess;

	}

	/**
	 * When user clicks on button "Search Selection based on posting Against" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchSelection(Event event) throws SuspendNotAllowedException, InterruptedException {

		logger.debug("Entering " + event.toString());
		String postingagainst = this.postingAgainst.getSelectedItem().getValue().toString();
		doSearchSelection(postingagainst);
		logger.debug("Leaving " + event.toString());

	}

	private void doSearchSelection(String stmtType) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		if (VASConsatnts.VASAGAINST_CUSTOMER.equals(stmtType)) {
			final String searchText = this.primaryLinkRef.getValue();
			if (StringUtils.isNotBlank(searchText)) {
				this.custCIFSearchObject = new JdbcSearchObject<Customer>(Customer.class, getListRows());
				custCIFSearchObject.addTabelName("Customers_AEView");
				custCIFSearchObject.addField("CustCIF");
				custCIFSearchObject.addField("CustShrtName");
				custCIFSearchObject.addField("CustDOB");
				custCIFSearchObject.addField("PhoneNumber");
				custCIFSearchObject.addField("CustCRCPR");
				custCIFSearchObject.addField("CustPassportNo");
				custCIFSearchObject.addField("lovDescCustTypeCodeName");
				custCIFSearchObject.addField("CustNationality");
				custCIFSearchObject.addField("CustCtgCode");
				custCIFSearchObject.addWhereClause("CustCIF=" + "'" + searchText + "'");
			} else {
				this.custCIFSearchObject = null;
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("moduleCode", moduleCode);
			map.put("enqiryModule", enqiryModule);
			map.put("DialogCtrl", this);
			map.put("filtertype", "Extended");
			map.put("searchObject", this.custCIFSearchObject);
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		} else if (VASConsatnts.VASAGAINST_FINANCE.equals(stmtType)) {

			final String searchText1 = this.primaryLinkRef.getValue();
			Object dataObject = ExtendedSearchListBox.show(this.window_VASRecordingDialog, "FinanceMain", searchText1);
			if (dataObject instanceof String) {
				this.primaryLinkRef.setValue(dataObject.toString());
			} else {
				FinanceMain details = (FinanceMain) dataObject;
				if (details != null) {
					this.primaryLinkRef.setValue(details.getFinReference());
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "viewInfo" button
	 * 
	 * @param event
	 */
	public void onClick$viewInfo(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		String postingagainst = this.postingAgainst.getSelectedItem().getValue().toString();
		doSearchSlectionInfo(postingagainst);
		logger.debug("Leaving " + event.toString());
	}

	/*
	 * Display the Customer, Loan or collateral details based on postingAgainst type
	 */
	private void doSearchSlectionInfo(String stmtType) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");

		if (this.primaryLinkRef.getValue().equals("")) {
			return;
		}

		HashMap<String, Object> map = new HashMap<String, Object>();
		if (VASConsatnts.VASAGAINST_CUSTOMER.equals(stmtType)) {
			map.put("custid", getVASRecording().getVasCustomer().getCustomerId());
			map.put("finReference", this.vasReference.getValue());
			map.put("finance", true);
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/fincustomerdetailsenq.zul",
					getMainWindow(), map);
		} else if (VASConsatnts.VASAGAINST_FINANCE.equals(stmtType)) {
			FinanceEnquiry aFinanceEnq = new FinanceEnquiry();
			aFinanceEnq.setFinReference(this.primaryLinkRef.getValue());
			map.put("moduleCode", moduleCode);
			map.put("fromApproved", true);
			map.put("childDialog", true);
			map.put("financeEnquiry", aFinanceEnq);
			map.put("VASRecordingDialog", this);
			map.put("enquiryType", this.enquiryType.getValue());
			Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul",
					getMainWindow(), map);
		} else if (VASConsatnts.VASAGAINST_COLLATERAL.equals(stmtType)) {
			CollateralSetup collateralSetup = getCollateralSetupService().getCollateralSetupByRef(
					this.primaryLinkRef.getValue(), "", true);
			map.put("collateralSetup", collateralSetup);
			map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralSetupDialog.zul",
					null, map);
		}
		logger.debug("Leaving");
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		this.primaryLinkRef.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.primaryLinkRef.setValue(customer.getCustCIF());
		} else {
			this.primaryLinkRef.setValue("");
		}
		logger.debug("Leaving ");
	}

	/**
	 * Method for fetching Currency format of selected currency
	 * 
	 * @return
	 */
	public int getCcyFormat() {
		return CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aVASConfiguration
	 * @throws ParseException 
	 */
	public void doWriteComponentsToBean(VASRecording aVASRecording, boolean isSave) throws ParseException {
		logger.debug("Entering");
		// doSetValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Product Code
		try {
			aVASRecording.setProductCode(this.productCode.getValue());
			aVASRecording.setProductDesc(this.productCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Posting Against
		try {
			if (this.postingAgainst.getSelectedItem() != null) {
				aVASRecording.setPostingAgainst(this.postingAgainst.getSelectedItem().getValue().toString());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Fee Payment Mode
		try {
			if (!this.feePaymentMode.isDisabled() && !isFinanceVas()) {
				isValidComboValue(this.feePaymentMode, Labels.getLabel("label_VASRecordingDialog_FeePaymentMode.value"));
			} 
			aVASRecording.setFeePaymentMode(this.feePaymentMode.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Primary Link Reference
		try {
			aVASRecording.setPrimaryLinkRef(this.primaryLinkRef.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		// VAS Reference
		try {
			aVASRecording.setVasReference(this.vasReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// VAS Fee
		try {
			aVASRecording.setFee(PennantAppUtil.unFormateAmount(this.fee.getActualValue(),getCcyFormat()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Paid Amount
		try {
			aVASRecording.setPaidAmt(PennantAppUtil.unFormateAmount(this.paidAmt.getActualValue(), getCcyFormat()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Waived Amount
		try {
			aVASRecording.setWaivedAmt(PennantAppUtil.unFormateAmount(this.waivedAmt.getActualValue(), getCcyFormat()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Renewal Fee  
		try {
			aVASRecording.setRenewalFee(PennantAppUtil.unFormateAmount(this.renewalFee.getActualValue(),getCcyFormat()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Value Date
		try {
			aVASRecording.setValueDate(this.valueDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Accrual Till Date
		try {
			aVASRecording.setAccrualTillDate(this.accrualTillDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Recurring Date
		try {
			aVASRecording.setRecurringDate(this.recurringDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Dsa Id
		try {
			aVASRecording.setDsaId(this.dsaId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Dma Id
		try {
			aVASRecording.setDmaId(this.dmaId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// FulFilOfficer Id
		try {
			aVASRecording.setFulfilOfficerId(this.fulfilOfficerId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Referral Id
		try {
			aVASRecording.setReferralId(this.referralId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Vas Status
		aVASRecording.setVasStatus(isCancelProcess ? "C" : "N");
		
		//Extended field details
		final ExtendedFieldRender aExetendedFieldRender = getExtendedFieldRender();
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		Map<String, Object> map = null;
		try {
			boolean isReadOnly = false;
			if (enqiryModule || isCancelProcess) {
				isReadOnly = true;
			} else {
				isReadOnly = isReadOnly("VASRecordingDialog_ExtendedFields");
			}
			
			map = generator.doSave(getExtendedFieldHeader().getExtendedFieldDetails(), isReadOnly);
			aExetendedFieldRender.setMapValues(map);
		} catch (WrongValuesException wves) {
			WrongValueException[] wvea = wves.getWrongValueExceptions();
			for (int i = 0; i < wvea.length; i++) {
				wve.add(wvea[i]);
			}
		}

		// Basic Details Error Detail
		showErrorDetails(wve, this.basicDetailsTab);
				
		// Post Validations for the Extended fields
		if (!enqiryModule && !isCancelProcess) {
			if(StringUtils.isNotEmpty(getPostValidationScript())){
				ScriptErrors postValidationErrors = getScriptValidationService().getPostValidationErrors(getPostValidationScript(), map);
				// Preparing Wrong Value User UI exceptions
				showErrorDetails(postValidationErrors);
			}
		}
		
		if (aVASRecording.isNew()) {
			aExetendedFieldRender.setSeqNo(1);
		}
		aVASRecording.setExtendedFieldRender(aExetendedFieldRender);
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Showing UI Post validation Errors
	 * @param postValidationErrors
	 */
	public void showErrorDetails(ScriptErrors postValidationErrors) {
		List<ScriptError> errorList = postValidationErrors.getAll();
		if(errorList == null || errorList.isEmpty()){
			return;
		}
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		for (int i = 0; i < errorList.size(); i++) {
			ScriptError error = errorList.get(i);
			
			if(extendedFieldTabPanel.getFellowIfAny("ad_"+error.getProperty()) != null){
				Component component = extendedFieldTabPanel.getFellowIfAny("ad_"+error.getProperty());
				WrongValueException we = new WrongValueException(component, error.getValue());
				wve.add(we);
			}
		}
		
		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if(i == 0){
					Component comp = wvea[i].getComponent();
					if(comp instanceof HtmlBasedComponent){
						Clients.scrollIntoView(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}
	}
	
	
	/**
	 * Method to show error details if occurred
	 * 
	 **/
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		doRemoveValidation();

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if(i == 0){
					Component comp = wvea[i].getComponent();
					if(comp instanceof HtmlBasedComponent){
						Clients.scrollIntoView(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.productCode.setConstraint("");
		this.postingAgainst.setConstraint("");
		this.primaryLinkRef.setConstraint("");
		this.vasReference.setConstraint("");
		this.fee.setConstraint("");
		this.paidAmt.setConstraint("");
		this.waivedAmt.setConstraint("");
		this.renewalFee.setConstraint("");
		this.feePaymentMode.setConstraint("");
		this.valueDate.setConstraint("");
		this.accrualTillDate.setConstraint("");
		this.recurringDate.setConstraint("");
		this.dsaId.setConstraint("");
		this.dmaId.setConstraint("");
		this.fulfilOfficerId.setConstraint("");
		this.referralId.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.productCode.setReadonly(true);
		this.postingAgainst.setDisabled(true);
		this.primaryLinkRef.setReadonly(true);
		this.vasReference.setReadonly(true);
		this.fee.setReadonly(true);
		this.fee.setDisabled(true);
		this.renewalFee.setReadonly(true);
		this.feePaymentMode.setDisabled(true);
		this.valueDate.setDisabled(true);
		this.accrualTillDate.setDisabled(true);
		this.recurringDate.setDisabled(true);
		this.dsaId.setReadonly(true);
		this.dmaId.setReadonly(true);
		this.fulfilOfficerId.setReadonly(true);
		this.referralId.setReadonly(true);
		this.waivedAmt.setReadonly(true);
		this.viewInfo.setVisible(false);
		
		if (isWorkFlowEnabled() && !isCancelProcess) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if(isCancelProcess){
			if (isWorkFlowEnabled()) {
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(false);
				}
				if (this.vASRecording.isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
				}
			}
			this.btnDelete.setVisible(false);
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		if(!enqiryModule){
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_VASRecordingDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_VASRecordingDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_VASRecordingDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_VASRecordingDialog_btnSave"));
			this.btnCancel.setVisible(false);
		}

		logger.debug("Leaving");
	}

	public void onFulfill$productCode(Event event) {
		logger.debug("Entering" + event.toString());
		this.postingAgainst.setConstraint("");
		this.postingAgainst.setErrorMessage("");
		Object dataObject = productCode.getObject();
		this.postingAgainst.setDisabled(true);
		if (dataObject == null || dataObject instanceof String) {
			this.productCode.setValue("");
			this.productCode.setDescription("");
			Comboitem comboitem = new Comboitem();
			comboitem.setValue("#");
			comboitem.setLabel("");
			postingAgainst.appendChild(comboitem);
			postingAgainst.setSelectedItem(comboitem);
			this.primaryLinkRef.setValue("");
		} else {
			VASConfiguration vas = (VASConfiguration) dataObject;
			this.productCode.setValue(vas.getProductCode());
			this.productCode.setDescription("");
			String recAgainst = PennantAppUtil.getVasConfiguration(this.productCode.getValue());
			Comboitem comboitem = new Comboitem();
			comboitem.setValue(recAgainst);
			comboitem.setLabel(recAgainst);
			postingAgainst.appendChild(comboitem);
			postingAgainst.setSelectedItem(comboitem);
			postingAgainst.setReadonly(true);
		}
		logger.debug("Leaving" + event.toString());

	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.accrualTillDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.recurringDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.productCode.setProperties("VASConfiguration", "ProductCode", "ProductDesc", true, 8);
		this.primaryLinkRef.setWidth("180px");
		this.dsaId.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false, 8);
		this.dmaId.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false, 8);
		this.fulfilOfficerId.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false, 8);
		this.referralId.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false, 8);
		this.productCode.setTextBoxWidth(145);
		this.dsaId.setTextBoxWidth(145);
		this.dmaId.setTextBoxWidth(145);
		this.fulfilOfficerId.setTextBoxWidth(145);
		this.referralId.setTextBoxWidth(145);
		this.postingAgainst.setDisabled(true);
		this.fee.setProperties(true, getCcyFormat());
		this.paidAmt.setProperties(false, getCcyFormat());
		this.waivedAmt.setProperties(false, getCcyFormat());
		this.renewalFee.setProperties(false, getCcyFormat());
		this.renewalFee.setWidth("100px");
		logger.debug("Leaving");
	}
	
	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	public HashMap<String, Object> getDefaultArguments() {
		final HashMap<String, Object> map = new HashMap<String, Object>();
	 	map.put("roleCode", getRole());
		map.put("dialogCtrl", this);
		map.put("finHeaderList", getHeaderBasicDetails());
		map.put("isNotFinanceProcess", true);
		map.put("moduleName", VASConsatnts.MODULE_NAME);
		map.put("postAccReq", false);
		return map;
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	private String getIDbyTab(String tabID) {
		return tabID.replace("TAB", "");
	}

	private void clearTabpanelChildren(String id) {
		Tabpanel tabpanel = getTabpanel(id);
		if (tabpanel != null) {
			tabpanel.setStyle("overflow:auto;");
			tabpanel.getChildren().clear();
		}
	}

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug("Entering");
		String tabName = Labels.getLabel("tab_label_" + moduleID);
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight("100%");
		ComponentsCtrl.applyForward(tab, ("onSelect=" + selectMethodName));
		logger.debug("Leaving");
	}

	public void onSelectTab(ForwardEvent event) throws Exception {

		Tab tab = (Tab) event.getOrigin().getTarget();
		logger.debug(tab.getId() + " --> " + "Entering");
		String module = getIDbyTab(tab.getId());
		doRemoveValidation();
		doClearMessage();

		switch (module) {

		case AssetConstants.UNIQUE_ID_AGREEMENT:
			this.doWriteComponentsToBean(getVASRecording(), true);

			if (agreementDetailDialogCtrl != null) {
				agreementDetailDialogCtrl.doSetLabels(getHeaderBasicDetails());
				agreementDetailDialogCtrl.doShowDialog(false);
			} else {
				appendAgreementsDetailTab(false);
			}
			break;

		case AssetConstants.UNIQUE_ID_CHECKLIST:
			this.doWriteComponentsToBean(getVASRecording(), true);
			if (financeCheckListReferenceDialogCtrl != null) {
				financeCheckListReferenceDialogCtrl.doSetLabels(getHeaderBasicDetails());
				financeCheckListReferenceDialogCtrl.doWriteBeanToComponents(getVASRecording().getCheckLists(),
						getVASRecording().getVasCheckLists(), false);
			}
			break;
		case AssetConstants.UNIQUE_ID_DOCUMENTDETAIL:
			if (documentDetailDialogCtrl != null) {
				documentDetailDialogCtrl.doSetLabels(getHeaderBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_ACCOUNTING:
			this.doWriteComponentsToBean(getVASRecording(), true);
			if (accountingDetailDialogCtrl != null) {
				accountingDetailDialogCtrl.doSetLabels(getHeaderBasicDetails());
			}else{
				appendAccountingDetailTab(false);
			}
			isAccountingExecuted = false;
			break;
		case AssetConstants.UNIQUE_ID_RECOMMENDATIONS:
			tab.removeForward(Events.ON_SELECT, (Tab) null, selectMethodName);
			appendRecommendDetailTab(false);
			break;
		default:
			break;
		}

		logger.debug(tab.getId() + " --> " + "Leaving");
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getHeaderBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, VASConsatnts.MODULE_NAME);
		arrayList.add(1, this.productCode.getValue());
		arrayList.add(2, this.vasReference.getValue());
		arrayList.add(3, this.primaryLinkRef.getValue());
		arrayList.add(4, getVASRecording().getVasConfiguration().getProductTypeDesc());
		arrayList.add(5, getVASRecording().getVasConfiguration().getProductCategory());
		return arrayList;
	}

	/**
	 * Method for Fetching Document Details for Checklist processing
	 * 
	 * @return
	 */
	public List<DocumentDetails> getDocumentDetails() {
		if (documentDetailDialogCtrl != null) {
			return documentDetailDialogCtrl.getDocumentDetailsList();
		}
		return new ArrayList<DocumentDetails>();
	}
	
	/**
	 * Method for fetching Customer Basic Details for Document Details processing
	 * @return
	 */
	public List<Object> getCustomerBasicDetails() {

		List<Object> custBasicDetails = null;
		if (vASRecording.getVasCustomer() != null) {
			VasCustomer vasCustomer = vASRecording.getVasCustomer();
			custBasicDetails = new ArrayList<>();
			custBasicDetails.add(vasCustomer.getCustomerId());
			custBasicDetails.add(vasCustomer.getCustCIF());
			custBasicDetails.add(vasCustomer.getCustShrtName());
		}
		return custBasicDetails;
	}
	
	/**
	 * Method for Executing Eligibility Details
	 * 
	 * @throws Exception
	 */
	public void executeAccounting() throws Exception{
		logger.debug("Entering");
		
		List<ReturnDataSet> accountingSetEntries = new ArrayList<ReturnDataSet>();
		if(isCancelProcess){
			accountingSetEntries.addAll(getVASRecording().getReturnDataSetList());
		}else{
			getVASRecording().setFee(PennantAppUtil.unFormateAmount(this.fee.getActualValue(), getCcyFormat()));
			AEEvent aeEvent = new AEEvent();
			aeEvent.setAccountingEvent(AccountEventConstants.ACCEVENT_VAS_FEE);
			AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
			if(amountCodes == null){
				amountCodes = new AEAmountCodes();
			}
			
			// Based on VAS Created Against, details will be captured  
			if(StringUtils.equals(VASConsatnts.VASAGAINST_FINANCE, getVASRecording().getPostingAgainst())){
				FinanceMain financeMain = getFinanceDetailService().getFinanceMainForBatch(getVASRecording().getPrimaryLinkRef());
				amountCodes.setFinType(financeMain.getFinType());
				aeEvent.setBranch(financeMain.getFinBranch());
				aeEvent.setCcy(financeMain.getFinCcy());
				aeEvent.setCustID(financeMain.getCustID());
			}else if(StringUtils.equals(VASConsatnts.VASAGAINST_CUSTOMER, getVASRecording().getPostingAgainst())){
				Customer customer = getCustomerDetailsService().getCustomerByCIF(getVASRecording().getPrimaryLinkRef());
				aeEvent.setBranch(customer.getCustDftBranch());
				aeEvent.setCcy(customer.getCustBaseCcy());
				aeEvent.setCustID(customer.getCustID());
			}else if(StringUtils.equals(VASConsatnts.VASAGAINST_COLLATERAL, getVASRecording().getPostingAgainst())){
				CollateralSetup collateralSetup = getCollateralSetupService().getApprovedCollateralSetupById(
						getVASRecording().getPrimaryLinkRef());
				aeEvent.setCcy(collateralSetup.getCollateralCcy());
				aeEvent.setCustID(collateralSetup.getDepositorId());
			}
			
			aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
			getVASRecording().getDeclaredFieldValues(aeEvent.getDataMap());
			aeEvent.getAcSetIDList().add(vASConfiguration.getFeeAccounting());
			List<ReturnDataSet> returnSetEntries = getEngineExecution().getAccEngineExecResults(aeEvent).getReturnDataSet();
			getVASRecording().setReturnDataSetList(returnSetEntries);
			accountingSetEntries.addAll(returnSetEntries);
		}
		if(accountingDetailDialogCtrl != null){
			accountingDetailDialogCtrl.doFillAccounting(accountingSetEntries);
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Method for Adding fee amounts to Fee List Controller to maintain Single list of fees
	 * @param vasFee
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private void appendFeesToFeeList(FinFeeDetail vasFee) {
		logger.debug("Entering");
		try {
			// Fetch FinanceMain Base Controller
			FinanceMainBaseCtrl mainBaseCtrl = (FinanceMainBaseCtrl) finVasRecordingDialogCtrl.getClass()
					.getMethod("getFinanceMainDialogCtrl").invoke(finVasRecordingDialogCtrl);

			// Fetch Fee List Controller
			FinFeeDetailListCtrl feeDetailListCtrl = (FinFeeDetailListCtrl) mainBaseCtrl.getClass()
					.getMethod("getFinFeeDetailListCtrl").invoke(mainBaseCtrl);
			
			// Rendering newly added VAS fees
			if (feeDetailListCtrl != null) {
				feeDetailListCtrl.renderVASFee(vasFee);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		logger.debug("Leaving");
	}
	
	/**
	 * Method for Adding fee amounts to Fee List Controller to maintain Single list of fees
	 * @param vasFee
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private void removeFeesFromFeeList(String vasReference) {
		logger.debug("Entering");
		try {
			// Fetch FinanceMain Base Controller
			FinanceMainBaseCtrl mainBaseCtrl = (FinanceMainBaseCtrl) finVasRecordingDialogCtrl.getClass()
					.getMethod("getFinanceMainDialogCtrl").invoke(finVasRecordingDialogCtrl);
			
			// Fetch Fee List Controller
			FinFeeDetailListCtrl feeDetailListCtrl = (FinFeeDetailListCtrl) mainBaseCtrl.getClass()
					.getMethod("getFinFeeDetailListCtrl").invoke(mainBaseCtrl);
			
			// Rendering newly added VAS fees
			if (feeDetailListCtrl != null) {
				feeDetailListCtrl.removeVASFee(vasReference);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for action Event of Changing Waived Amount
	 * @param event
	 * @throws Exception
	 */
	public void onFulfill$waivedAmt(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		BigDecimal vasFee = PennantApplicationUtil.unFormateAmount(this.fee.getActualValue(), getCcyFormat());
		BigDecimal waivedAmt = PennantApplicationUtil.unFormateAmount(this.waivedAmt.getActualValue(), getCcyFormat());
		if (waivedAmt.compareTo(vasFee) > 0) {
			this.waivedAmt.setValue(PennantApplicationUtil.formateAmount(vasFee, getCcyFormat()));
		}

		BigDecimal bal = vasFee
				.subtract(PennantApplicationUtil.unFormateAmount(this.waivedAmt.getActualValue(), getCcyFormat()));
		this.paidAmt.setValue(PennantApplicationUtil.formateAmount(bal, getCcyFormat()));
		
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for action Event of Changing VAS FEE Amount
	 * @param event
	 * @throws Exception
	 */
	public void onFeeAmountChange(ForwardEvent event)throws Exception{
		logger.debug("Entering");
		this.paidAmt.setValue(this.fee.getActualValue());
		this.waivedAmt.setValue(BigDecimal.ZERO);
		logger.debug("Leaving");
	}

	/**
	 * Method for fetching Customer Id number for Document Details processing
	 * 
	 * @return
	 */
	public long getCustomerIDNumber() {
		if (vASRecording.getVasCustomer() != null) {
			return vASRecording.getVasCustomer().getCustomerId();
		}
		return 0;
	}
	
	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isFinanceVas()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	@Override
	protected String getReference() {
		return this.vasReference.getValue();
	}

	public VASRecording getVASRecording() {
		return this.vASRecording;
	}

	public void setVASRecording(VASRecording vASRecording) {
		this.vASRecording = vASRecording;
	}

	public void setVASRecordingService(VASRecordingService vASRecordingService) {
		this.vASRecordingService = vASRecordingService;
	}

	public VASRecordingService getVASRecordingService() {
		return this.vASRecordingService;
	}

	public void setVASRecordingListCtrl(VASRecordingListCtrl vASRecordingListCtrl) {
		this.vASRecordingListCtrl = vASRecordingListCtrl;
	}

	public VASRecordingListCtrl getVASRecordingListCtrl() {
		return this.vASRecordingListCtrl;
	}

	public VASConfiguration getvASConfiguration() {
		return vASConfiguration;
	}

	public void setvASConfiguration(VASConfiguration vASConfiguration) {
		this.vASConfiguration = vASConfiguration;
	}

	public Window getMainWindow() {
		return mainWindow;
	}

	public void setMainWindow(Window mainWindow) {
		this.mainWindow = mainWindow;
	}

	public CollateralSetupService getCollateralSetupService() {
		return collateralSetupService;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	public List<FinanceCheckListReference> getVasChecklists() {
		return vasChecklists;
	}

	public void setVasChecklists(List<FinanceCheckListReference> vasChecklists) {
		this.vasChecklists = vasChecklists;
	}

	public HashMap<Long, Long> getSelectedAnsCountMap() {
		return selectedAnsCountMap;
	}

	public void setSelectedAnsCountMap(HashMap<Long, Long> selectedAnsCountMap) {
		this.selectedAnsCountMap = selectedAnsCountMap;
	}

	public AgreementDetailDialogCtrl getAgreementDetailDialogCtrl() {
		return agreementDetailDialogCtrl;
	}

	public void setAgreementDetailDialogCtrl(AgreementDetailDialogCtrl agreementDetailDialogCtrl) {
		this.agreementDetailDialogCtrl = agreementDetailDialogCtrl;
	}

	public ExtendedFieldRenderDialogCtrl getExtendedFieldRenderDialogCtrl() {
		return extendedFieldRenderDialogCtrl;
	}

	public void setExtendedFieldRenderDialogCtrl(ExtendedFieldRenderDialogCtrl extendedFieldRenderDialogCtrl) {
		this.extendedFieldRenderDialogCtrl = extendedFieldRenderDialogCtrl;
	}

	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public FinanceCheckListReferenceDialogCtrl getFinanceCheckListReferenceDialogCtrl() {
		return financeCheckListReferenceDialogCtrl;
	}

	public void setFinanceCheckListReferenceDialogCtrl(
			FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl) {
		this.financeCheckListReferenceDialogCtrl = financeCheckListReferenceDialogCtrl;
	}

	public DocumentDetailDialogCtrl getDocumentDetailDialogCtrl() {
		return documentDetailDialogCtrl;
	}

	public void setDocumentDetailDialogCtrl(DocumentDetailDialogCtrl documentDetailDialogCtrl) {
		this.documentDetailDialogCtrl = documentDetailDialogCtrl;
	}

	public AccountingDetailDialogCtrl getAccountingDetailDialogCtrl() {
		return accountingDetailDialogCtrl;
	}
	public void setAccountingDetailDialogCtrl(AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}

	public ScriptValidationService getScriptValidationService() {
		return scriptValidationService;
	}
	public void setScriptValidationService(ScriptValidationService scriptValidationService) {
		this.scriptValidationService = scriptValidationService;
	}

	public String getPreValidationScript() {
		return preValidationScript;
	}
	public void setPreValidationScript(String preValidationScript) {
		this.preValidationScript = preValidationScript;
	}

	public String getPostValidationScript() {
		return postValidationScript;
	}
	public void setPostValidationScript(String postValidationScript) {
		this.postValidationScript = postValidationScript;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}
	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public ExtendedFieldRender getExtendedFieldRender() {
		return extendedFieldRender;
	}
	public void setExtendedFieldRender(ExtendedFieldRender extendedFieldRender) {
		this.extendedFieldRender = extendedFieldRender;
	}

	public FinanceReferenceDetailService getFinanceReferenceDetailService() {
		return financeReferenceDetailService;
	}
	public void setFinanceReferenceDetailService(FinanceReferenceDetailService financeReferenceDetailService) {
		this.financeReferenceDetailService = financeReferenceDetailService;
	}
	
	public CustomerEMailDAO getCustomerEMailDAO() {
		return customerEMailDAO;
	}
	public void setCustomerEMailDAO(CustomerEMailDAO customerEMailDAO) {
		this.customerEMailDAO = customerEMailDAO;
	}
	
	public MailUtil getMailUtil() {
		return mailUtil;
	}
	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}

	public EventManager getEventManager() {
		return eventManager;
	}
	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	public FinVasRecordingDialogCtrl getFinVasRecordingDialogCtrl() {
		return finVasRecordingDialogCtrl;
	}
	public void setFinVasRecordingDialogCtrl(FinVasRecordingDialogCtrl finVasRecordingDialogCtrl) {
		this.finVasRecordingDialogCtrl = finVasRecordingDialogCtrl;
	}

	public boolean isFinanceVas() {
		return financeVas;
	}
	public void setFinanceVas(boolean financeVas) {
		this.financeVas = financeVas;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isFeeEditable() {
		return feeEditable;
	}
	public void setFeeEditable(boolean feeEditable) {
		this.feeEditable = feeEditable;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}
	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public List<FinFeeDetail> getFinFeeDetailsList() {
		return finFeeDetailsList;
	}
	public void setFinFeeDetailsList(List<FinFeeDetail> finFeeDetailsList) {
		this.finFeeDetailsList = finFeeDetailsList;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}


}