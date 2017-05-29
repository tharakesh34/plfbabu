package com.pennant.webui.solutionfactory.extendedfielddetail;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.service.solutionfactory.ExtendedFieldDetailService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.pagging.PagedListWrapper;

public class ExtendedFieldDialogCtrl extends GFCBaseCtrl<ExtendedFieldDetail>{
	private static final long						serialVersionUID			= -3249715883200188080L;
	private final static Logger						logger						= Logger.getLogger(ExtendedFieldDialogCtrl.class);

	protected Window								window_ExtendedFieldDialog;	
	protected Label									moduleDesc;														
	protected Label									subModuleDesc;																	
	protected Longbox								moduleId;																		
	protected Textbox								tabHeading;																	
	protected Radiogroup							numberOfColumns;																
	protected Radio									radio_column1;																	
	protected Radio									radio_column2;																	
	protected Grid									grid_basicDetails;																

	private transient boolean						validationOn;

	private ExtendedFieldDetail						extendedFieldDetail;
	private ExtendedFieldHeader						extendedFieldHeader;
	private transient ExtendedFieldDetailListCtrl	extendedFieldDetailListCtrl;
	private transient ExtendedFieldDetailService	extendedFieldDetailService;
	private boolean									newRecord					= false;

	protected Button								btnNew_FieldDet;
	protected Paging								pagingFieldDetList;
	protected Listbox								listBoxFieldDet;
	protected Component								parentTabPanel				= null;
	protected Div									toolbar						= null;
	protected Object								dialogCtrl					= null;
	protected boolean								firstTaskRole				= false;
	protected int									maxSeqNo					= 0;

	private List<ExtendedFieldDetail>				extendedFieldDetailsList	= new ArrayList<ExtendedFieldDetail>();
	private PagedListWrapper<ExtendedFieldDetail>	extendedFieldPagedListWrapper;

	public ExtendedFieldDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ExtendedFieldDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected ExtendedFieldDetail
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ExtendedFieldDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ExtendedFieldDialog);
		try{
			setExtendedFieldPagedListWrapper();

			// READ OVERHANDED params !
			if (arguments.containsKey("extendedFieldHeader")) {
				this.extendedFieldHeader = (ExtendedFieldHeader) arguments.get("extendedFieldHeader");
				ExtendedFieldHeader befImage = new ExtendedFieldHeader();
				BeanUtils.copyProperties(this.extendedFieldHeader, befImage);
				this.extendedFieldHeader.setBefImage(befImage);
				setExtendedFieldHeader(this.extendedFieldHeader);
			} else {
				setExtendedFieldHeader(null);
			}

			if (event.getTarget().getParent() != null) {
				parentTabPanel = event.getTarget().getParent();
			} 
			
			if (parentTabPanel != null) {
				if (arguments.containsKey("roleCode")) {
					String roleCode = (String) arguments.get("roleCode");
					setRole(roleCode);
				}
			}
			
			// READ OVERHANDED params !
			// we get the extendedFieldDetailListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete extendedFieldDetail here.
			if (arguments.containsKey("extendedFieldDetailListCtrl")) {
				setExtendedFieldDetailListCtrl((ExtendedFieldDetailListCtrl) arguments.get("extendedFieldDetailListCtrl"));
			} else {
				setExtendedFieldDetailListCtrl(null);
			}
			
			boolean actionRenderReq = true;
			if (arguments.containsKey("dialogCtrl")) {
				this.dialogCtrl = (Object) arguments.get("dialogCtrl");
				try {
					dialogCtrl.getClass().getMethod("setExtendedFieldDialogCtrl", this.getClass()).invoke(dialogCtrl, this);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				//this.extendedFieldHeader.setWorkflowId(0);
				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				if (arguments.containsKey("moduleName")) {
					this.extendedFieldHeader.setModuleName((String)arguments.get("moduleName"));
				}
				if (arguments.containsKey("firstTaskRole")) {
					this.firstTaskRole = (boolean) arguments.get("firstTaskRole");
				}
				actionRenderReq = false;
				getUserWorkspace().allocateRoleAuthorities(getRole(), "ExtendedFieldDialog");
			}
			
			doLoadWorkFlow(this.extendedFieldHeader.isWorkflow(), this.extendedFieldHeader.getWorkflowId(), this.extendedFieldHeader.getNextTaskId());

			if (isWorkFlowEnabled() && actionRenderReq) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "ExtendedFieldDialog");
			}
			
			this.listBoxFieldDet.setHeight(getListBoxHeight(this.grid_basicDetails.getRows().getVisibleItemCount()+6));
			this.pagingFieldDetList.setPageSize(getListRows()+1);
			this.pagingFieldDetList.setDetailed(true);
			
			// set Field Properties
			doSetFieldProperties();
			
			/* set components visible dependent of the users rights */
			doCheckRights();
			
			doShowDialog(getExtendedFieldHeader());
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e);
			window_ExtendedFieldDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes

		this.tabHeading.setMaxlength(20);
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		logger.debug("Leaving");
	}
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("ExtendedFieldDialog",getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDialog_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDialog_btnSave"));
		this.btnDelete.setVisible(false);
		this.btnCancel.setVisible(false);
		this.btnNew_FieldDet.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDialog_FD_btnNew"));

		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_ExtendedFieldDialog);
		logger.debug("Leaving" + event.toString());
	}


	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
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
		doWriteBeanToComponents(this.extendedFieldHeader.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aExtendedFieldDetail
	 *            ExtendedFieldDetail
	 */
	public void doWriteBeanToComponents(ExtendedFieldHeader aExtendedFieldHeader) {
		logger.debug("Entering");

		this.moduleId.setValue(aExtendedFieldHeader.getModuleId());
		if (parentTabPanel != null) {
			this.moduleDesc.setValue(aExtendedFieldHeader.getModuleName());
			this.subModuleDesc.setValue(aExtendedFieldHeader.getSubModuleName());
		} else {
			this.moduleDesc.setValue(Labels.getLabel("label_ExtendedField_" + aExtendedFieldHeader.getModuleName()));
			this.subModuleDesc.setValue(Labels.getLabel("label_ExtendedField_" + aExtendedFieldHeader.getSubModuleName()));
		}
		this.tabHeading.setValue(aExtendedFieldHeader.getTabHeading());
		for (int i = 0; i < numberOfColumns.getItemCount(); i++) {
			if (this.numberOfColumns.getItemAtIndex(i).getValue().equals(aExtendedFieldHeader.getNumberOfColumns() == null ? "" : 
				aExtendedFieldHeader.getNumberOfColumns().trim())) {
				this.numberOfColumns.setSelectedIndex(i);
			}
		}
		
		// Adding Default Columns for Extended field Detail List (Number of units & Unit Price)
		if(aExtendedFieldHeader.isNewRecord()){

			if(aExtendedFieldHeader.getExtendedFieldDetails() == null){
				aExtendedFieldHeader.setExtendedFieldDetails(new ArrayList<ExtendedFieldDetail>());
			}

			//TODO: Modify dynamic from static
			ExtendedFieldDetail unitCount = new ExtendedFieldDetail();
			unitCount.setFieldName("NOOFUNITS");
			unitCount.setFieldLabel("Number of Units");
			unitCount.setFieldType(ExtendedFieldConstants.FIELDTYPE_INT);
			unitCount.setFieldLength(3);
			unitCount.setFieldSeqOrder(10);
			unitCount.setFieldMandatory(true);
			unitCount.setRecordType(PennantConstants.RCD_ADD);
			unitCount.setVersion(1);

			ExtendedFieldDetail unitPrice = new ExtendedFieldDetail();
			unitPrice.setFieldName("UNITPRICE");
			unitPrice.setFieldLabel("Unit Price");
			unitPrice.setFieldType(ExtendedFieldConstants.FIELDTYPE_AMOUNT);
			unitPrice.setFieldLength(18);
			unitPrice.setFieldSeqOrder(20);
			unitPrice.setFieldMandatory(true);
			unitPrice.setRecordType(PennantConstants.RCD_ADD);
			unitPrice.setVersion(1);
			
			aExtendedFieldHeader.getExtendedFieldDetails().add(unitCount);
			aExtendedFieldHeader.getExtendedFieldDetails().add(unitPrice);
			
		}
		
		// Extended Fields Rendering
		doFillFieldsList(aExtendedFieldHeader.getExtendedFieldDetails());
		
		this.recordStatus.setValue(aExtendedFieldHeader.getRecordStatus());
		logger.debug("Leaving");
	}

	public void doWriteComponentsToBean(ExtendedFieldHeader aExtendedFieldHeader, Tab tab) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aExtendedFieldHeader.setTabHeading(this.tabHeading.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aExtendedFieldHeader.setNumberOfColumns(this.numberOfColumns.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//adding the list to bean
		aExtendedFieldHeader.setExtendedFieldDetails(this.extendedFieldDetailsList);
		doRemoveValidation();
		doRemoveLOVValidation();
		
		if (parentTabPanel != null) {
			showErrorDetails(wve, tab);
		} else {
			if (wve.size() > 0) {
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = (WrongValueException) wve.get(i);
				}
				throw new WrongValuesException(wvea);
			}
		}
		aExtendedFieldHeader.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}
	
	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab extendedFieldsTab) {
		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			if (extendedFieldsTab != null) {
				extendedFieldsTab.setSelected(true);
			}
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
	}
		
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aExtendedFieldDetail
	 * @throws Exception
	 */
	public void doShowDialog(ExtendedFieldHeader aExtendedFieldHeader) throws Exception {
		logger.debug("Entering");

		// if aExtendedFieldDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aExtendedFieldHeader != null) {
			setExtendedFieldHeader(aExtendedFieldHeader);
		}

		if (isWorkFlowEnabled()) {
			if (StringUtils.isNotBlank(aExtendedFieldHeader.getRecordType())) {
				this.btnNotes.setVisible(true);
			}
			doEdit();
		} else {
			this.btnCtrl.setInitEdit();
			doReadOnly();
			btnCancel.setVisible(false);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aExtendedFieldHeader);
			if (parentTabPanel != null) {
				this.toolbar.setVisible(false);
				this.groupboxWf.setVisible(false);
				this.window_ExtendedFieldDialog.setHeight(borderLayoutHeight - 75 + "px");
				parentTabPanel.appendChild(this.window_ExtendedFieldDialog);
			} else {
				setDialog(DialogType.EMBEDDED);
			}
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ExtendedFieldDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}
	
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		 if (!this.tabHeading.isReadonly()) {
			this.tabHeading.setConstraint(new PTStringValidator(Labels.getLabel("label_ExtendedFieldDialog_TabHeading.value"),null,false));
		} 
		logger.debug("Leaving");
	}
	
	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.tabHeading.setConstraint("");
		logger.debug("Leaving");
	}
	
	/**
	 * Deletes a ExtendedFieldDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		
		final ExtendedFieldHeader aExtendedFieldHeader = new ExtendedFieldHeader();
		BeanUtils.copyProperties(getExtendedFieldHeader(), aExtendedFieldHeader);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "+
							Labels.getLabel("label_ExtendedFieldDialog_ModuleId.value")+" : "+ aExtendedFieldHeader.getModuleId();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true);

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.isBlank(aExtendedFieldHeader.getRecordType())) {
				aExtendedFieldHeader.setVersion(aExtendedFieldHeader.getVersion() + 1);
				aExtendedFieldHeader.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aExtendedFieldHeader.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aExtendedFieldHeader, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				MessageUtil.showErrorMessage(e);
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getExtendedFieldHeader().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		this.tabHeading.setReadonly(isReadOnly("ExtendedFieldDialog_tabHeading"));
		this.radio_column1.setDisabled(isReadOnly("ExtendedFieldDialog_tabHeading"));
		this.radio_column2.setDisabled(isReadOnly("ExtendedFieldDialog_tabHeading"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.extendedFieldHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
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
	 * Clears the components values. <br>
	 */
	public void doClear() {
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		
		final ExtendedFieldHeader aExtendedFieldHeader = new ExtendedFieldHeader();
		BeanUtils.copyProperties(getExtendedFieldHeader(), aExtendedFieldHeader);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doClearMessage();
		doSetValidation();
		// fill the ExtendedFieldDetail object with the components data
		doWriteComponentsToBean(aExtendedFieldHeader, null);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aExtendedFieldHeader.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aExtendedFieldHeader.getRecordType())) {
				aExtendedFieldHeader.setVersion(aExtendedFieldHeader.getVersion() + 1);
				if (isNew) {
					aExtendedFieldHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aExtendedFieldHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aExtendedFieldHeader.setNewRecord(true);
				}
			}
		} else {
			aExtendedFieldHeader.setVersion(aExtendedFieldHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aExtendedFieldHeader, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (Exception e) {
			doFillFieldsList(getExtendedFieldDetailsList());
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e.getMessage());
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(ExtendedFieldHeader aExtendedFieldHeader, String tranType) {
		logger.debug("Entering");
		
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aExtendedFieldHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aExtendedFieldHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aExtendedFieldHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aExtendedFieldHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aExtendedFieldHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aExtendedFieldHeader);
				}

				if (isNotesMandatory(taskId, aExtendedFieldHeader)) {
					try {
						if (!notesEntered) {
							MessageUtil.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error("Exception: ", e);
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

			aExtendedFieldHeader.setTaskId(taskId);
			aExtendedFieldHeader.setNextTaskId(nextTaskId);
			aExtendedFieldHeader.setRoleCode(getRole());
			aExtendedFieldHeader.setNextRoleCode(nextRoleCode);

			List<ExtendedFieldDetail> tempEFDetail = new ArrayList<ExtendedFieldDetail>();
			for (ExtendedFieldDetail aEFDetail : aExtendedFieldHeader.getExtendedFieldDetails()) {
				aEFDetail.setTaskId(taskId);
				aEFDetail.setNextTaskId(nextTaskId);
				aEFDetail.setRoleCode(getRole());
				aEFDetail.setNextRoleCode(nextRoleCode);
				aEFDetail.setNewRecord(aExtendedFieldHeader.isNewRecord());
				aEFDetail.setUserDetails(aExtendedFieldHeader.getUserDetails());
				aEFDetail.setWorkflowId(aExtendedFieldHeader.getWorkflowId());
				tempEFDetail.add(aEFDetail);
			}
			aExtendedFieldHeader.setExtendedFieldDetails(tempEFDetail);
			auditHeader = getAuditHeader(aExtendedFieldHeader, tranType);

			String operationRefs = getServiceOperations(taskId, aExtendedFieldHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aExtendedFieldHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aExtendedFieldHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		ExtendedFieldHeader aExtendedFieldHeader = (ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getExtendedFieldDetailService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getExtendedFieldDetailService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getExtendedFieldDetailService().doApprove(auditHeader);

						if (aExtendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getExtendedFieldDetailService().doReject(auditHeader);
						if (aExtendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ExtendedFieldDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ExtendedFieldDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.extendedFieldHeader), true);
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
	
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.extendedFieldHeader);
	}
	
	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aHolidayMaster
	 *            (HolidayMaster)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(ExtendedFieldHeader aExtendedFieldHeader, String tranType) {
		String[] fields = PennantJavaUtil.getFieldDetails(aExtendedFieldHeader,"extendedFieldDetails");
		AuditDetail auditDetail = new AuditDetail(tranType, 1,fields[0],fields[1],aExtendedFieldHeader.getBefImage(), aExtendedFieldHeader);

		return new AuditHeader(String.valueOf(aExtendedFieldHeader.getId()), null,
				null, null, auditDetail, aExtendedFieldHeader.getUserDetails(),
				getOverideMap());
	}

	/**
	 * To show the Message
	 * 
	 * @throws Exception
	 * 
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_ExtendedFieldDialog,
					auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving ");
	}

	
	@Override
	protected String getReference() {
		return String.valueOf(this.extendedFieldHeader.getModuleId());
	}

	@Override
	protected void doClearMessage() {
		this.tabHeading.setErrorMessage("");
	}

	public void onClick$btnNew_FieldDet(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		ExtendedFieldDetail aExtendedFieldDetail = new ExtendedFieldDetail();
		aExtendedFieldDetail.setModuleId(this.moduleId.intValue());
		aExtendedFieldDetail.setNewRecord(true);
		aExtendedFieldDetail.setWorkflowId(0);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("extendedFieldDetail", aExtendedFieldDetail);
		map.put("extendedFieldDialogCtrl", this);
		map.put("newRecord", true);
		map.put("maxSeqNo", maxSeqNo);
		map.put("roleCode", getRole());

		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldDetailDialog.zul", window_ExtendedFieldDialog,map);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e);
		}
		logger.debug("Leaving" + event.toString());
	}


	public void onExtendedFieldItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxFieldDet.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final ExtendedFieldDetail extendedFieldDetail = (ExtendedFieldDetail) item.getAttribute("data");

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("extendedFieldDetail", extendedFieldDetail);
			map.put("extendedFieldDialogCtrl", this);
			map.put("roleCode", getRole());
			map.put("firstTaskRole", this.firstTaskRole);
			map.put("layoutDesign", numberOfColumns.getSelectedItem().getValue());
			// call the zul-file with the parameters packed in a map
			try {
				Executions.createComponents(
						"/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldDetailDialog.zul",
						window_ExtendedFieldDialog, map);
			} catch (Exception e) {
				logger.error("Exception: Opening window", e);
				MessageUtil.showErrorMessage(e);
			}

		}
		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// *********        Field Detail List		   **********//
	// ******************************************************//

	/**
	 * Generate the Extended Field Details List in the Extended FieldDialogCtrl and set the list in the listBoxFieldDet
	 * listbox by using Pagination
	 */
	public void doFillFieldsList(List<ExtendedFieldDetail> extendedFieldDetailsList) {
		logger.debug("Entering");
		if (extendedFieldDetailsList == null) {
			this.extendedFieldDetailsList = new ArrayList<ExtendedFieldDetail>();
		} else {
			this.extendedFieldDetailsList = extendedFieldDetailsList;
		}
		this.pagingFieldDetList.setDetailed(true);
		this.pagingFieldDetList.setActivePage(0);
		setTableName(this.extendedFieldDetailsList);
		setExtendedFieldDetailsList(this.extendedFieldDetailsList);
		getExtendedFieldPagedListWrapper().initList(this.extendedFieldDetailsList, listBoxFieldDet, pagingFieldDetList);
		this.listBoxFieldDet.setItemRenderer(new ExtendedFieldListItemRenderer());
		
		// Details of Fields for Pre & Post validations
		List<String> fieldNameList = new ArrayList<>();
		for (int i = 0; i < this.extendedFieldDetailsList.size(); i++) {
			
			if(maxSeqNo < extendedFieldDetailsList.get(i).getFieldSeqOrder()){
				maxSeqNo = extendedFieldDetailsList.get(i).getFieldSeqOrder();
			}
			
			if (!StringUtils.equals(this.extendedFieldDetailsList.get(i).getRecordType(),PennantConstants.RECORD_TYPE_DEL) && 
					!StringUtils.equals(this.extendedFieldDetailsList.get(i).getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				fieldNameList.add(this.extendedFieldDetailsList.get(i).getFieldName());
			}
		}
		
		if(this.dialogCtrl != null){
			try {
				dialogCtrl.getClass().getMethod("setFieldNames", List.class).invoke(dialogCtrl, fieldNameList);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		}
		logger.debug("Leaving");
	}

	//Getting the table name from map
	private void setTableName(List<ExtendedFieldDetail> extendedFieldDetails){
		for(ExtendedFieldDetail efd:extendedFieldDetails){
			if (efd.getLovDescModuleName() == null) {
				efd.setLovDescModuleName(getExtendedFieldHeader().getModuleName());
			}
			
			if (getExtendedFieldHeader().getSubModuleName() == null) {
				getExtendedFieldHeader().setSubModuleName(this.subModuleDesc.getValue());
			}
			
			if (efd.getLovDescSubModuleName() == null) {
				efd.setLovDescSubModuleName(getExtendedFieldHeader().getSubModuleName());
			}
			
			
			String tableName = getExtendedFieldHeader().getModuleName();
			tableName = tableName.concat("_").concat(getExtendedFieldHeader().getSubModuleName()).concat("_ED");
			efd.setLovDescSubModuleName(tableName);//PennantStaticListUtil.getModuleName(efd)
		}
	}
	
	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getExtendedFieldDetailListCtrl().search();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public ExtendedFieldDetail getExtendedFieldDetail() {
		return extendedFieldDetail;
	}

	public void setExtendedFieldDetail(ExtendedFieldDetail extendedFieldDetail) {
		this.extendedFieldDetail = extendedFieldDetail;
	}

	public ExtendedFieldDetailListCtrl getExtendedFieldDetailListCtrl() {
		return extendedFieldDetailListCtrl;
	}

	public void setExtendedFieldDetailListCtrl(ExtendedFieldDetailListCtrl extendedFieldDetailListCtrl) {
		this.extendedFieldDetailListCtrl = extendedFieldDetailListCtrl;
	}

	public ExtendedFieldDetailService getExtendedFieldDetailService() {
		return extendedFieldDetailService;
	}

	public void setExtendedFieldDetailService(ExtendedFieldDetailService extendedFieldDetailService) {
		this.extendedFieldDetailService = extendedFieldDetailService;
	}

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public List<ExtendedFieldDetail> getExtendedFieldDetailsList() {
		return extendedFieldDetailsList;
	}

	public void setExtendedFieldDetailsList(List<ExtendedFieldDetail> extendedFieldDetailsList) {
		this.extendedFieldDetailsList = extendedFieldDetailsList;
	}

	@SuppressWarnings("unchecked")
	public void setExtendedFieldPagedListWrapper() {
		if (this.extendedFieldPagedListWrapper == null) {
			this.extendedFieldPagedListWrapper = (PagedListWrapper<ExtendedFieldDetail>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

	public PagedListWrapper<ExtendedFieldDetail> getExtendedFieldPagedListWrapper() {
		return extendedFieldPagedListWrapper;
	}

	public ExtendedFieldHeader getExtendedFieldHeader() {
		return extendedFieldHeader;
	}

	public void setExtendedFieldHeader(ExtendedFieldHeader extendedFieldHeader) {
		this.extendedFieldHeader = extendedFieldHeader;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}


	public class ExtendedFieldListItemRenderer implements ListitemRenderer<ExtendedFieldDetail>, Serializable {
		
		private static final long serialVersionUID = 6321996138703133595L;

		public ExtendedFieldListItemRenderer() {
			
		}
		
		@Override
		public void render(Listitem item, ExtendedFieldDetail detail,int count) throws Exception {
			
			Listcell lc;
			lc = new Listcell(detail.getFieldName());
			lc.setParent(item);
			lc = new Listcell(detail.getFieldLabel());
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.getlabelDesc(detail.getFieldType(), PennantStaticListUtil.getFieldType()));
			lc.setParent(item);
			lc = new Listcell(String.valueOf(detail.getFieldSeqOrder()));
			lc.setParent(item);
			lc = new Listcell();
			Checkbox unique = new Checkbox();
			unique.setChecked(detail.isFieldUnique());
			unique.setDisabled(true);
			lc.appendChild(unique);
			lc.setParent(item);
			lc = new Listcell();
			Checkbox mandatory = new Checkbox();
			mandatory.setChecked(detail.isFieldMandatory());
			mandatory.setDisabled(true);
			lc.appendChild(mandatory);
			lc.setParent(item);
			lc = new Listcell(detail.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
			lc.setParent(item);
			
			item.setAttribute("data", detail);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onExtendedFieldItemDoubleClicked");
		}
	}


	public ExtendedFieldHeader doSave_ExtendedFields(Tab tab) {
		logger.debug("Entering");
		doWriteComponentsToBean(extendedFieldHeader, tab);
		if (StringUtils.isBlank(extendedFieldHeader.getRecordType())) {
			extendedFieldHeader.setVersion(extendedFieldHeader.getVersion() + 1);
			extendedFieldHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			extendedFieldHeader.setNewRecord(true);
		}
		extendedFieldHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		extendedFieldHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		extendedFieldHeader.setUserDetails(getUserWorkspace().getLoggedInUser());
		logger.debug("Leaving");
		return extendedFieldHeader;
	}

	/**
	 * Method for Setting Basic Details on header
	 * @param module
	 * @param subModule
	 */
	public void doSetBasicDetail(String module, String subModule, String subModuledesc) {
		this.moduleDesc.setValue(module);
		this.subModuleDesc.setValue(subModule);
		if(StringUtils.isNotEmpty(subModuledesc)){
			this.subModuleDesc.setValue(subModule+" - "+subModuledesc);
		}
		if (StringUtils.trimToNull(this.tabHeading.getValue()) == null) { 
			this.tabHeading.setValue(subModuledesc);
		}
	}
}
