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
 * FileName    		:  FinCovenantTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinCovenantTypeService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/FinanceMain/FinCovenantTypeList.zul file.
 */
public class CovenantDetailListCtrl extends GFCBaseCtrl<FinanceMain> {
	private static final long serialVersionUID = 4157448822555239535L;
	private static final Logger logger = Logger.getLogger(CovenantDetailListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinCovenantTypeList;
	
	protected Button btnNew_NewFinCovenantType;

	protected Listbox listBoxFinCovenantType;
	
	// For Dynamically calling of this Controller
	private FinanceDetail financedetail;
	private FinanceMain financeMain;
	private Object financeMainDialogCtrl;
	private Component parent = null;
	private Tab parentTab = null;
	private List<FinCovenantType> finCovenantTypesDetailList = new ArrayList<FinCovenantType>();
	private int ccyFormat=0;
	private transient boolean recSave = false;
	private String roleCode = "";
	private boolean isEnquiry = false;
	private transient boolean newFinance;
	protected Groupbox finBasicdetails;
	private FinBasicDetailsCtrl  finBasicDetailsCtrl;
	private String allowedRoles;
	private FinCovenantType finCovenantTypes;
	private transient FinCovenantTypeService finCovenantTypeService;
	protected transient FinanceSelectCtrl	financeSelectCtrl	= null;
	private CustomerDetailsService customerDetailsService;
	private FinanceDetailService financeDetailService;
	
	private boolean covenantDetail=true;
	/**
	 * default constructor.<br>
	 */
	public CovenantDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinanceMain";
		super.pageRightName = "FinCovenantTypeList";
	}

	// Component Events
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected CovenantType object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinCovenantTypeList(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinCovenantTypeList);

		try {
			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
			}
			// Get the required arguments.
			this.finCovenantTypes = (FinCovenantType) arguments.get("finCovenantTypes");
			

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
			}
			
			if (arguments.containsKey("financeSelectCtrl")) {
				setFinanceSelectCtrl((FinanceSelectCtrl) arguments.get("financeSelectCtrl"));
			}
			if (arguments.containsKey("financeMain")) {
				setFinanceMain((FinanceMain) arguments.get("financeMain"));
			}

			
			if (arguments.containsKey("financeDetail")) {
				setFinancedetail((FinanceDetail) arguments.get("financeDetail"));
				if (getFinancedetail()!=null) {
					if(getFinancedetail().getCovenantTypeList() != null){
						setFinCovenantTypeDetailList(getFinancedetail().getCovenantTypeList());
					}
				}
			}
			// READ OVERHANDED params !
			if (arguments.containsKey("finCovenantTypes")) {
				this.finCovenantTypes = (FinCovenantType) arguments.get("finCovenantTypes");
				FinanceMain befImage = new FinanceMain();
				setFinancedetail(getFinancedetail());
				Cloner cloner = new Cloner();
				befImage = cloner.deepClone(getFinancedetail().getFinScheduleData().getFinanceMain());
				getFinancedetail().getFinScheduleData().getFinanceMain()
						.setBefImage(befImage);
			}
			
			FinanceMain financeMain = getFinancedetail().getFinScheduleData().getFinanceMain();
			// Render the page and display the data.
			doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if(!enqiryModule){
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(),this.pageRightName);
			}else{
				getUserWorkspace().allocateAuthorities(this.pageRightName,null);
			}
			
			if (arguments.containsKey("allowedRoles")) {
				allowedRoles=(String) arguments.get("allowedRoles");
			}
			
			doEdit();
			doCheckRights();
			doSetFieldProperties();
			doShowDialog();
			if (StringUtils.isNotBlank(financeMain.getRecordType())) {
				this.btnNotes.setVisible(true);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception 
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSave();
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws Exception
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		Cloner cloner = new Cloner();
		aFinanceDetail = cloner.deepClone(getFinancedetail());

		boolean isNew = false;
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		
		if(this.userAction.getSelectedItem() != null &&
				!this.userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_RESUBMITTED) &&
				!this.userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_FINALIZED) &&
				!this.userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_APPROVED) &&
				!this.userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_CANCELLED) &&
				!this.userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_REJECTED)){
			recSave = true;
		}
		
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aFinanceMain.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinanceMain.getRecordType())) {
				aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
				if (isNew) {
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceMain.setNewRecord(true);
				}
			}
		} else {
			aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		if(aFinanceDetail !=null){
			aFinanceDetail.setCovenantTypeList(finCovenantTypesDetailList);
		}
		/*for (FinCovenantType finCovenantType : aFinanceDetail.getCovenantTypeList()) {
			finCovenantType.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
			finCovenantType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			finCovenantType.setUserDetails(getUserWorkspace().getLoggedInUser());
			
		}*/

		// save it to database
		try {
			aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
			if (doProcess(aFinanceDetail, tranType)) {
				if (getFinanceSelectCtrl() != null) {
					refreshList();
				}
				// do Close the Dialog window
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
	 * @param aFinanceDetail
	 *            (AccountingSet)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * @throws Exception 
	 */
	private boolean doProcess(FinanceDetail aFinanceDetail, String tranType) throws Exception, InterfaceException {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		String nextRoleCode = "";

		afinanceMain.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			Map<String, String> baseRoleMap = new HashMap<>();
			afinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(afinanceMain.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, afinanceMain);
				}

				if (isNotesMandatory(taskId, afinanceMain)) {
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
			afinanceMain.setLovDescBaseRoleCodeMap(baseRoleMap);
			afinanceMain.setTaskId(taskId);
			afinanceMain.setNextTaskId(nextTaskId);
			afinanceMain.setRoleCode(getRole());
			afinanceMain.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinanceDetail, tranType);

			String operationRefs = getServiceOperations(taskId, afinanceMain);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinanceDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinanceDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinanceDetail aFinanceDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceDetail.getBefImage(), aFinanceDetail);
		return new AuditHeader(String.valueOf(aFinanceDetail.getFinScheduleData().getFinReference()), 
				null, null, null, auditDetail, aFinanceDetail.getUserDetails(), getOverideMap());
	}
	
	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InterfaceException 
	 * @throws JaxenException 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterruptedException, InterfaceException, IllegalAccessException, InvocationTargetException, JaxenException {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinanceDetail afinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getFinanceDetailService().delete(auditHeader,false);
						deleteNotes = true;
					} else {
						auditHeader = getFinanceDetailService().saveOrUpdate(auditHeader, false);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getFinanceDetailService().doApprove(auditHeader,false);

						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getFinanceDetailService().doReject(auditHeader,false);
						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinCovenantTypeList, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_FinCovenantTypeList, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.finCovenantTypes), true);
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

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
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
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}
	
	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.financedetail.getFinScheduleData().getFinanceMain());
	}
	@Override
	public String getReference() {
		return this.financedetail.getFinReference() + "";
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		setStatusDetails();
		logger.debug("Leaving");
	}
		
	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}
	
	
	private void doCheckRights() {
		logger.debug("Entering");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinCovenantTypeList_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinCovenantTypeList_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinCovenantTypeList_btnSave"));
		this.btnNew_NewFinCovenantType.setVisible(getUserWorkspace().isAllowed("FinCovenantTypeList_NewFinCovenantTypeDetail"));
		this.btnDelete.setVisible(false);
		this.btnCancel.setVisible(false);
		logger.debug("leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
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
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.finCovenantTypes.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");
	
		try {
			
			logger.debug("Entering");

			// set ReadOnly mode accordingly if the object is new or not.
			if (finCovenantTypes.isNew()) {
				this.btnCtrl.setInitNew();
				doEdit();
			} else {
				if (isWorkFlowEnabled()) {
					if (StringUtils.isNotBlank(finCovenantTypes.getRecordType())) {
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
			appendFinBasicDetails(getFinancedetail().getFinScheduleData().getFinanceMain());
			doCheckEnquiry();
			doWriteBeanToComponents();

			this.listBoxFinCovenantType.setHeight(borderLayoutHeight - 226 +"px");
			if (parent != null) {
				this.window_FinCovenantTypeList.setHeight(borderLayoutHeight-75+"px");
				parent.appendChild(this.window_FinCovenantTypeList);
			}else{
				setDialog(DialogType.EMBEDDED);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
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
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
	}
	
	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}
	
	

	
	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param commodityHeader
	 *            
	 */
	public void doWriteBeanToComponents() {
		logger.debug("Entering ");
		
		doFillFinCovenantTypeDetails(getFinCovenantTypeDetailList());
		
		logger.debug("Leaving ");
	}
	
	private void doCheckEnquiry() {
		if(isEnquiry){
			this.btnNew_NewFinCovenantType.setVisible(false);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onCovenantTypeValidation(Event event){
		logger.debug("Entering" + event.toString());

		String userAction = "";
		FinanceDetail finDetail = null;
		Map<String,Object> map = new HashMap<String,Object>();
		if(event.getData() != null){
			map = (Map<String, Object>) event.getData();
		}

		if(map.containsKey("userAction")){
			userAction = (String) map.get("userAction");
		}
		
		if(map.containsKey("financeDetail")){
			finDetail = (FinanceDetail) map.get("financeDetail");
		}
		
		recSave = false;
		if("Save".equalsIgnoreCase(userAction) || "Cancel".equalsIgnoreCase(userAction)
				|| "Reject".equalsIgnoreCase(userAction) || "Resubmit".equalsIgnoreCase(userAction)){
			recSave = true;
		}
		doClearMessage();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if(!recSave){
				FinanceMain main = null;
				if (getFinanceMainDialogCtrl() != null) {
					try {
						if (financeMainDialogCtrl.getClass().getMethod("getFinanceMain") != null) {
							Object object = financeMainDialogCtrl.getClass().getMethod("getFinanceMain").invoke(financeMainDialogCtrl);
							if (object != null) {
								main = (FinanceMain) object;
							}
						}
					} catch (Exception e) {
						logger.error("Exception: ", e);
					}
				}
				if (this.listBoxFinCovenantType.getItems() != null && !this.listBoxFinCovenantType.getItems().isEmpty()) {
					if (main != null && main.getFinAmount() != null) {
						
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		showErrorDetails(wve);
		
		if(finDetail !=null){
			finDetail.setCovenantTypeList(finCovenantTypesDetailList);
		}
		logger.debug("Leaving" + event.toString());
	}

	
	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");

			if(parentTab != null){
				parentTab.setSelected(true);
			}

			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnNew_NewFinCovenantType(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Clients.clearWrongValue(this.btnNew_NewFinCovenantType);
		
		final FinCovenantType aFinCovenantType = new FinCovenantType();
		aFinCovenantType.setFinReference(financedetail.getFinScheduleData().getFinReference());
		aFinCovenantType.setNewRecord(true);
		aFinCovenantType.setWorkflowId(0);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finCovenantTypes", aFinCovenantType);
		map.put("ccyFormatter", ccyFormat);
		map.put("covenantDetailListCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", roleCode);
		map.put("allowedRoles", allowedRoles);
		map.put("financeDetail", getFinancedetail());
		map.put("covenantDetail", covenantDetail);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/CovenantDetailDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onFinCovenantTypeItemDoubleClicked(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Clients.clearWrongValue(this.btnNew_NewFinCovenantType);

		Listitem listitem = this.listBoxFinCovenantType.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final FinCovenantType aFinCovenantType = (FinCovenantType) listitem.getAttribute("data");
			if (isDeleteRecord(aFinCovenantType.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			}else{
				aFinCovenantType.setNewRecord(false);

				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finCovenantTypes", aFinCovenantType);
				map.put("ccyFormatter", ccyFormat);
				map.put("covenantDetailListCtrl", this);
				map.put("roleCode", roleCode);
				map.put("enqModule", isEnquiry);
				map.put("allowedRoles", allowedRoles);
				map.put("financeDetail", getFinancedetail());

				// call the ZUL-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/CovenantDetailDialog.zul", null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void doFillFinCovenantTypeDetails(List<FinCovenantType> finAdvancePayDetails) {
		logger.debug("Entering");
		this.listBoxFinCovenantType.getItems().clear();
		setFinCovenantTypeDetailList(finAdvancePayDetails);
		if (finAdvancePayDetails != null && !finAdvancePayDetails.isEmpty()) {
			for (FinCovenantType detail : finAdvancePayDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(detail.getCovenantTypeDesc());
				lc.setParent(item);
				lc = new Listcell(detail.getMandRoleDesc());
				lc.setParent(item);
				Checkbox cb = new Checkbox();
				cb.setDisabled(true);
				cb.setChecked(detail.isAlwWaiver());
				lc = new Listcell();
				cb.setParent(lc);
				lc.setParent(item);
				cb = new Checkbox();
				cb.setDisabled(true);
				cb.setChecked(detail.isAlwPostpone());
				lc = new Listcell();
				cb.setParent(lc);
				lc.setParent(item);
				lc = new Listcell(detail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", detail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onFinCovenantTypeItemDoubleClicked");
				this.listBoxFinCovenantType.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	
	
	
	private boolean isDeleteRecord(String rcdType){
		if(StringUtils.equals(PennantConstants.RECORD_TYPE_CAN,rcdType) || 
				StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, rcdType)){
			return true;
		}
		return false;
	}
	
	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(FinanceMain aFinanceMain) {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this );
			map.put("finHeaderList", getHeaderBasicDetails(getFinanceMain()));
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul",this.finBasicdetails, map);
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
		Customer aCustomer= new Customer();
		aCustomer=getCustomerDetailsService().getCustomerShrtName(aFinanceMain.getCustID());
		arrayList.add(0, aFinanceMain.getFinType());
		arrayList.add(1, aFinanceMain.getFinCcy());
		arrayList.add(2, aFinanceMain.getScheduleMethod());
		arrayList.add(3, aFinanceMain.getFinReference());
		arrayList.add(4, aFinanceMain.getProfitDaysBasis());
		arrayList.add(5, null);
		arrayList.add(6, false);
		arrayList.add(7, false);
		arrayList.add(8, null);
		arrayList.add(9, aCustomer.getCustShrtName());
		arrayList.add(10, true);
		arrayList.add(11, null);
		return arrayList;
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//


	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}
	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public List<FinCovenantType> getFinCovenantTypeDetailList() {
		return finCovenantTypesDetailList;
	}
	public void setFinCovenantTypeDetailList(
			List<FinCovenantType> finCovenantTypesDetailList) {
		this.finCovenantTypesDetailList = finCovenantTypesDetailList;
	}

	public boolean isNewFinance() {
		return newFinance;
	}
	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public void setFinancedetail(FinanceDetail financedetail) {
		this.financedetail = financedetail;
	}
	public FinanceDetail getFinancedetail() {
		return financedetail;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}
	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}
	
	public FinCovenantType getFinCovenantTypes() {
		return finCovenantTypes;
	}

	public void setFinCovenantTypes(FinCovenantType finCovenantTypes) {
		this.finCovenantTypes = finCovenantTypes;
	}

	public FinCovenantTypeService getFinCovenantTypeService() {
		return finCovenantTypeService;
	}

	public void setFinCovenantTypeService(FinCovenantTypeService finCovenantTypeService) {
		this.finCovenantTypeService = finCovenantTypeService;
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

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

}
