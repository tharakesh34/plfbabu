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
 * FileName    		:  AccountMappingDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-04-2017    														*
 *                                                                  						*
 * Modified Date    :  24-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.accountmapping;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.applicationmaster.AccountMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennanttech.pff.core.Literal;
	

/**
 * This is the controller class for the
 * /WEB-INF/pages/applicationmaster/AccountMapping/accountMappingDialog.zul file. <br>
 */
public class AccountMappingDialogCtrl extends GFCBaseCtrl<AccountMapping>{

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(AccountMappingDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AccountMappingDialog; 
	protected Textbox 		account; 
	protected Textbox 		hostAccount; 
	private AccountMapping accountMapping; // overhanded per param
	protected  ExtendedCombobox finType;	
	
	Listbox listBoxAccountMap;
	

	private transient AccountMappingListCtrl accountMappingListCtrl; // overhanded per param
	private transient AccountMappingService accountMappingService;
	

	/**
	 * default constructor.<br>
	 */
	public AccountMappingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "AccountMappingDialog";
	}
	
	@Override
	protected String getReference() {
		StringBuffer referenceBuffer= new StringBuffer(this.accountMapping.getAccount());
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
	public void onCreate$window_AccountMappingDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		
		// Set the page level components.
		setPageComponents(window_AccountMappingDialog);

		doSetFieldProperties();
		
		try {
			// Get the required arguments.
			/*	this.accountMapping = (AccountMapping) arguments.get("accountMapping");
			this.accountMappingListCtrl = (AccountMappingListCtrl) arguments.get("accountMappingListCtrl");

			if (this.accountMapping == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			AccountMapping accountMapping = new AccountMapping();
			BeanUtils.copyProperties(this.accountMapping, accountMapping);
			this.accountMapping.setBefImage(accountMapping);
			
			// Render the page and display the data.
			doLoadWorkFlow(this.accountMapping.isWorkflow(), this.accountMapping.getWorkflowId(),
					this.accountMapping.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateAuthorities(this.pageRightName,getRole());
			}else{
				getUserWorkspace().allocateAuthorities(this.pageRightName,null);
			}

			doCheckRights();
			doShowDialog(this.accountMapping);*/
		} catch (Exception e) {
			logger.error("Exception:", e);
			closeDialog();
			MessageUtil.showError(e.toString());
		}
		
		logger.debug(Literal.LEAVING);
	}


	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		
		// Finance Type
		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType", "LovDescProductCodeDesc", "FinTypeDesc" });
		this.finType.setMandatoryStyle(true);
 		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * 
	 * @param event
	 */
	public void onFulfill$finType(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = finType.getObject();
		
		if (dataObject instanceof String) {
			this.finType.setValue(dataObject.toString());
			this.finType.setDescription("");
		} else {
			FinanceType financeType = (FinanceType) dataObject;
			List<String> subHeadRuleList = new ArrayList<>();
			
			
			List<TransactionEntry> transactionEntries = null;
			if (financeType != null) {
				 transactionEntries = accountMappingService.getTransactionEntriesByFintype(financeType.getFinType());
			}
			
			if(transactionEntries != null){
				for (TransactionEntry transactionEntry : transactionEntries) {
					if(!subHeadRuleList.contains(transactionEntry.getAccountSubHeadRule())) {
						subHeadRuleList.add(transactionEntry.getAccountSubHeadRule());
					}
				}
			}
			
			Map<String, Rule> subHeadMap = accountMappingService.getSubheadRules(subHeadRuleList);
			
		}
	}
	
	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AccountMappingDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AccountMappingDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AccountMappingDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AccountMappingDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
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
	public void onClick$btnDelete(Event event)  throws InterruptedException {
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
		doShowNotes(this.accountMapping);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		accountMappingListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.accountMapping.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}
	
	
	/*private void setAccountMapping(){
		
		FinanceType finType = new FinanceType();
		HashMap<String, Object> executionMap = new HashMap<String, Object>();

		
		
		List<TransactionEntry> transactionEntries = new ArrayList<>();
		for (TransactionEntry transactionEntry : transactionEntries) {
			
			Rule subHeadRule = transactionEntry.getAccountSubHeadRule();
			transactionEntry.getAccountType();
			
			if (subHeadRule.getFields() != null) {
				String[] fields = feeRule.getFields().split(",");
				for(String field : fields) {
					if (!executionMap.containsKey(field)) {
						getRuleExecutionUtil().setExecutionMap(field, objectList, executionMap);
					}
				}
			}
			
			
			
			
			
			
		}
		
		
		
		
		
		
		
		
	}
	*/
		
	




	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param accountMapping
	 * 
	 */
	public void doWriteBeanToComponents(AccountMapping aAccountMapping) {
		logger.debug(Literal.ENTERING);
	
			this.account.setValue(aAccountMapping.getAccount());
			this.hostAccount.setValue(aAccountMapping.getHostAccount());
		
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAccountMapping
	 */
	public void doWriteComponentsToBean(AccountMapping aAccountMapping) {
		logger.debug(Literal.LEAVING);
		
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		//Account
		try {
		    aAccountMapping.setAccount(this.account.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Host Account
		try {
		    aAccountMapping.setHostAccount(this.hostAccount.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		doRemoveValidation();
		doRemoveLOVValidation();
		
		if (!wve.isEmpty()) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param accountMapping
	 *            The entity that need to be render.
	 */
	public void doShowDialog(AccountMapping accountMapping) {
		logger.debug(Literal.LEAVING);

		if (accountMapping.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.account.focus();
		} else {
				this.account.setReadonly(true);

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(accountMapping.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.hostAccount.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(accountMapping);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.account.isReadonly()){
			this.account.setConstraint(new PTStringValidator(Labels.getLabel("label_AccountMappingDialog_Account.value"),PennantRegularExpressions.REGEX_NAME,false));
		}
		if (!this.hostAccount.isReadonly()){
			this.hostAccount.setConstraint(new PTStringValidator(Labels.getLabel("label_AccountMappingDialog_HostAccount.value"),PennantRegularExpressions.REGEX_NAME,false));
		}
	
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);
		
		this.account.setConstraint("");
		this.hostAccount.setConstraint("");
	
	logger.debug(Literal.LEAVING);
	}


	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);
		
		//Account
		//Host Account
		
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
	 * Deletes a AccountMapping object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);
		
		final AccountMapping aAccountMapping = new AccountMapping();
		BeanUtils.copyProperties(this.accountMapping, aAccountMapping);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aAccountMapping.getAccount();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aAccountMapping.getRecordType()).equals("")){
				aAccountMapping.setVersion(aAccountMapping.getVersion()+1);
				aAccountMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aAccountMapping.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aAccountMapping.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aAccountMapping.getNextTaskId(), aAccountMapping);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aAccountMapping,tranType)){
					refreshList();
					closeDialog(); 
				}

			}catch (DataAccessException e){
				logger.error("Exception",  e);
				showErrorMessage(this.window_AccountMappingDialog,e);
			}
			
		}
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);
		
		if (this.accountMapping.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.account);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.account);
			
		}
	
			readOnlyComponent(isReadOnly("AccountMappingDialog_HostAccount"), this.hostAccount);
			
			if (isWorkFlowEnabled()) {
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(false);
				}
				if (this.accountMapping.isNewRecord()) {
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
		 * Set the components to ReadOnly. <br>
		 */
		public void doReadOnly() {
			logger.debug(Literal.LEAVING);
			
	
			readOnlyComponent(true, this.account);
			readOnlyComponent(true, this.hostAccount);

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
				this.account.setValue("");
				this.hostAccount.setValue("");

			logger.debug("Leaving");
		}

		/**
		 * Saves the components to table. <br>
		 */
		public void doSave() {
			logger.debug("Entering");
			final AccountMapping aAccountMapping = new AccountMapping();
			BeanUtils.copyProperties(this.accountMapping, aAccountMapping);
			boolean isNew = false;

			doSetValidation();
			doWriteComponentsToBean(aAccountMapping);

			isNew = aAccountMapping.isNew();
			String tranType = "";

			if (isWorkFlowEnabled()) {
				tranType = PennantConstants.TRAN_WF;
				if (StringUtils.isBlank(aAccountMapping.getRecordType())) {
					aAccountMapping.setVersion(aAccountMapping.getVersion() + 1);
					if (isNew) {
						aAccountMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						aAccountMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						aAccountMapping.setNewRecord(true);
					}
				}
			} else {
				aAccountMapping.setVersion(aAccountMapping.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}

			try {
				if (doProcess(aAccountMapping, tranType)) {
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
		private boolean doProcess(AccountMapping aAccountMapping, String tranType) {
			logger.debug("Entering");
			boolean processCompleted = false;
			AuditHeader auditHeader = null;
			String nextRoleCode = "";

			aAccountMapping.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
			aAccountMapping.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aAccountMapping.setUserDetails(getUserWorkspace().getLoggedInUser());

			if (isWorkFlowEnabled()) {
				String taskId = getTaskId(getRole());
				String nextTaskId = "";
				aAccountMapping.setRecordStatus(userAction.getSelectedItem().getValue().toString());

				if ("Save".equals(userAction.getSelectedItem().getLabel())) {
					nextTaskId = taskId + ";";
				} else {
					nextTaskId = StringUtils.trimToEmpty(aAccountMapping.getNextTaskId());

					nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
					if ("".equals(nextTaskId)) {
						nextTaskId = getNextTaskIds(taskId, aAccountMapping);
					}

					if (isNotesMandatory(taskId, aAccountMapping)) {
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

				aAccountMapping.setTaskId(taskId);
				aAccountMapping.setNextTaskId(nextTaskId);
				aAccountMapping.setRoleCode(getRole());
				aAccountMapping.setNextRoleCode(nextRoleCode);

				auditHeader = getAuditHeader(aAccountMapping, tranType);
				String operationRefs = getServiceOperations(taskId, aAccountMapping);

				if ("".equals(operationRefs)) {
					processCompleted = doSaveProcess(auditHeader, null);
				} else {
					String[] list = operationRefs.split(";");

					for (int i = 0; i < list.length; i++) {
						auditHeader = getAuditHeader(aAccountMapping, PennantConstants.TRAN_WF);
						processCompleted = doSaveProcess(auditHeader, list[i]);
						if (!processCompleted) {
							break;
						}
					}
				}
			} else {
				auditHeader = getAuditHeader(aAccountMapping, tranType);
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
			AccountMapping aAccountMapping = (AccountMapping) auditHeader.getAuditDetail().getModelData();
			boolean deleteNotes = false;

			try {

				while (retValue == PennantConstants.porcessOVERIDE) {

					if (StringUtils.isBlank(method)) {
						if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
							auditHeader = accountMappingService.delete(auditHeader);
							deleteNotes = true;
						} else {
							auditHeader = accountMappingService.saveOrUpdate(auditHeader);
						}

					} else {
						if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
							auditHeader = accountMappingService.doApprove(auditHeader);

							if (aAccountMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
								deleteNotes = true;
							}

						} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
							auditHeader = accountMappingService.doReject(auditHeader);
							if (aAccountMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
								deleteNotes = true;
							}

						} else {
							auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
									.getLabel("InvalidWorkFlowMethod"), null));
							retValue = ErrorControl.showErrorControl(this.window_AccountMappingDialog, auditHeader);
							return processCompleted;
						}
					}

					auditHeader = ErrorControl.showErrorDetails(this.window_AccountMappingDialog, auditHeader);
					retValue = auditHeader.getProcessStatus();

					if (retValue == PennantConstants.porcessCONTINUE) {
						processCompleted = true;

						if (deleteNotes) {
							deleteNotes(getNotes(this.accountMapping), true);
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

		/**
		 * @param aAuthorizedSignatoryRepository
		 * @param tranType
		 * @return
		 */

		private AuditHeader getAuditHeader(AccountMapping aAccountMapping, String tranType) {
			AuditDetail auditDetail = new AuditDetail(tranType, 1, aAccountMapping.getBefImage(), aAccountMapping);
			return new AuditHeader(getReference(), null, null, null, auditDetail, aAccountMapping.getUserDetails(),
					getOverideMap());
		}

		public void setAccountMappingService(AccountMappingService accountMappingService) {
			this.accountMappingService = accountMappingService;
		}
			
}
