package com.pennant.webui.solutionfactory.extendedfielddetail;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.service.solutionfactory.ExtendedFieldDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.webui.solutionfactory.extendedfielddetail.model.ExtendedFieldListItemRenderer;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;

public class ExtendedFieldDialogCtrl extends GFCBaseListCtrl<ExtendedFieldDetail> implements Serializable{

	private static final long	serialVersionUID	= -3249715883200188080L;
	private final static Logger logger = Logger.getLogger(ExtendedFieldDialogCtrl.class);

	protected Window 		window_ExtendedFieldDialog; // autowired
	protected Label 		moduleDesc;					// autowired
	protected Label 		subModuleDesc;				// autowired
	protected Longbox 		moduleId;					// autowired
	protected Textbox 		tabHeading; 				// autowired
	protected Radiogroup 	numberOfColumns; 			// autowired
	protected Radio 		radio_column1;				// autowired
	protected Radio 		radio_column2;				// autowired
	protected Grid			grid_basicDetails;			// autowired

	protected Label 		recordStatus; 				// autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_ExtendedFieldDialog_";

	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; 	// autowire
	protected Button btnHelp; 	// autowire
	protected Button btnNotes; 	// autowire
	private transient boolean validationOn;

	private ExtendedFieldDetail extendedFieldDetail; 
	private ExtendedFieldHeader extendedFieldHeader;
	private transient ExtendedFieldDetailListCtrl extendedFieldDetailListCtrl;
	private transient ExtendedFieldDetailService extendedFieldDetailService;
	private boolean notes_Entered = false;

	private transient String oldVar_tabHeading;
	private transient String oldVar_numberOfColumns;

	protected Button btnNew_FieldDet;
	protected Paging pagingFieldDetList;
	protected Listbox listBoxFieldDet;

	private List<ExtendedFieldDetail> extendedFieldDetailsList =  new ArrayList<ExtendedFieldDetail>();
	private List<ExtendedFieldDetail> oldVar_ExtendedFieldDetailsList = new ArrayList<ExtendedFieldDetail>();
	private PagedListWrapper<ExtendedFieldDetail> extendedFieldPagedListWrapper;

	public ExtendedFieldDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected ExtendedFieldDetail
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ExtendedFieldDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		try{
			
			/* set components visible dependent of the users rights */
			doCheckRights();

			setExtendedFieldPagedListWrapper();

			/* create the Button Controller. Disable not used buttons during working */
			this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
					this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);

			// READ OVERHANDED params !
			if (args.containsKey("extendedFieldHeader")) {
				this.extendedFieldHeader = (ExtendedFieldHeader) args.get("extendedFieldHeader");
				ExtendedFieldHeader befImage = new ExtendedFieldHeader();
				BeanUtils.copyProperties(this.extendedFieldHeader, befImage);
				this.extendedFieldHeader.setBefImage(befImage);

				setExtendedFieldHeader(this.extendedFieldHeader);
			} else {
				setExtendedFieldHeader(null);
			}

			doLoadWorkFlow(this.extendedFieldHeader.isWorkflow(), this.extendedFieldHeader.getWorkflowId(),
					this.extendedFieldHeader.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "ExtendedFieldDetailDialog");
			}
			
			this.listBoxFieldDet.setHeight(getListBoxHeight(this.grid_basicDetails.getRows().getVisibleItemCount()+4));
			this.pagingFieldDetList.setPageSize(getListRows());
			this.pagingFieldDetList.setDetailed(true);

			// READ OVERHANDED params !
			// we get the extendedFieldDetailListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete extendedFieldDetail here.
			if (args.containsKey("extendedFieldDetailListCtrl")) {
				setExtendedFieldDetailListCtrl((ExtendedFieldDetailListCtrl) args.get("extendedFieldDetailListCtrl"));
			} else {
				setExtendedFieldDetailListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getExtendedFieldHeader());
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
			window_ExtendedFieldDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			this.tabHeading.setMaxlength(20);
		} else {
			this.groupboxWf.setVisible(false);
		}

		logger.debug("Leaving");
	}
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("ExtendedFieldDetailDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDetailDialog_btnEdit"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);
		this.btnNew_FieldDet.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDetailDialog_FD_btnNew"));

		logger.debug("Leaving");
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_ExtendedFieldDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
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
		// remember the old vars
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_ExtendedFieldDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// GUI Process

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		
		boolean close = true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			closeDialog(this.window_ExtendedFieldDialog, "ExtendedFieldDetail");
		}

		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
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
		this.moduleDesc.setValue(Labels.getLabel("label_ExtendedField_"+aExtendedFieldHeader.getModuleName()));
		this.subModuleDesc.setValue(Labels.getLabel("label_ExtendedField_"+aExtendedFieldHeader.getSubModuleName()));
		this.tabHeading.setValue(aExtendedFieldHeader.getTabHeading());
		
		for (int i = 0; i < numberOfColumns.getItemCount(); i++) {
			if (this.numberOfColumns.getItemAtIndex(i).getValue().equals(aExtendedFieldHeader.getNumberOfColumns()==null?"":aExtendedFieldHeader.getNumberOfColumns().trim())) {
				this.numberOfColumns.setSelectedIndex(i);
			}
		}
		doFillFieldsList(aExtendedFieldHeader.getExtendedFieldDetails());
		logger.debug("Leaving");
	}

	public void doWriteComponentsToBean(ExtendedFieldHeader aExtendedFieldHeader) {
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

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aExtendedFieldHeader.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aExtendedFieldDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(ExtendedFieldHeader aExtendedFieldHeader) throws InterruptedException {
		logger.debug("Entering");

		// if aExtendedFieldDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aExtendedFieldHeader != null) {
			setExtendedFieldHeader(aExtendedFieldHeader);
		}


		if (isWorkFlowEnabled()) {
			this.btnNotes.setVisible(true);
			doEdit();
		} else {
			this.btnCtrl.setInitEdit();
			doReadOnly();
			btnCancel.setVisible(false);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aExtendedFieldHeader);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_ExtendedFieldDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_tabHeading = this.tabHeading.getValue();
		this.oldVar_ExtendedFieldDetailsList = getExtendedFieldHeader().getExtendedFieldDetails();
		logger.debug("Leaving");
	}
	
	private void doResetInitValues() {
		logger.debug("Entering");
		
		this.tabHeading.setValue(this.oldVar_tabHeading);
		for (int i = 0; i < numberOfColumns.getItemCount(); i++) {
			if (this.numberOfColumns.getSelectedItem().getValue().equals(this.oldVar_numberOfColumns)) {
				this.numberOfColumns.setSelectedIndex(i);
				break;
			}
			this.numberOfColumns.setSelectedIndex(0);
		}
		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		
		logger.debug("Leaving");
	}
	
	private boolean isDataChanged() {
		// To clear the Error Messages
		doClearMessage();
		
		if (this.oldVar_tabHeading != this.tabHeading.getValue()) {
			return true;
		}
		if (this.oldVar_ExtendedFieldDetailsList != getExtendedFieldHeader().getExtendedFieldDetails()) {
			return true;
		}
		return false;
	}
	
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.tabHeading.isReadonly()) {
			this.tabHeading.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_ExtendedFieldDialog_TabHeading.value") }));
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
		BeanUtils.copyProperties(getExtendedFieldDetail(), aExtendedFieldHeader);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
							+ aExtendedFieldHeader.getModuleId();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aExtendedFieldHeader.getRecordType()).equals("")) {
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
					closeDialog(this.window_ExtendedFieldDialog, "ExtendedFieldHeader");
				}
			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new ExtendedFieldDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old vars
		doStoreInitValues();
		
		final ExtendedFieldDetail aExtendedFieldDetail = getExtendedFieldDetailService().getNewExtendedFieldDetail();
		setExtendedFieldDetail(aExtendedFieldDetail);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

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

		this.tabHeading.setReadonly(isReadOnly("ExtendedFieldDetailDialog_tabHeading"));
		this.radio_column1.setDisabled(isReadOnly("ExtendedFieldDetailDialog_tabHeading"));
		this.radio_column2.setDisabled(isReadOnly("ExtendedFieldDetailDialog_tabHeading"));

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

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++O++++++++++++++
		doSetValidation();
		// fill the ExtendedFieldDetail object with the components data
		doWriteComponentsToBean(aExtendedFieldHeader);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aExtendedFieldHeader.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aExtendedFieldHeader.getRecordType()).equals("")) {
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
				closeDialog(this.window_ExtendedFieldDialog, "ExtendedFieldDetail");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(ExtendedFieldHeader aExtendedFieldHeader, String tranType) {
		logger.debug("Entering");
		
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aExtendedFieldHeader.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aExtendedFieldHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aExtendedFieldHeader.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			aExtendedFieldHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aExtendedFieldHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aExtendedFieldHeader);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,
						aExtendedFieldHeader))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aExtendedFieldHeader.setTaskId(taskId);
			aExtendedFieldHeader.setNextTaskId(nextTaskId);
			aExtendedFieldHeader.setRoleCode(getRole());
			aExtendedFieldHeader.setNextRoleCode(nextRoleCode);

			List<ExtendedFieldDetail> tempEFDetail = new ArrayList<ExtendedFieldDetail>();
			for (int i = 0; i < aExtendedFieldHeader.getExtendedFieldDetails().size(); i++) {
				ExtendedFieldDetail aEFDetail = aExtendedFieldHeader.getExtendedFieldDetails().get(i);
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

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aExtendedFieldHeader);

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

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
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
						deleteNotes(getNotes(), true);
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
			logger.error(e);
			e.printStackTrace();
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// logger.debug(event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
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
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_ExtendedFieldDialog,
					auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving ");
	}

	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("ExtendedFieldDetail");
		notes.setReference(String.valueOf(getExtendedFieldHeader().getModuleId()));
		notes.setVersion(getExtendedFieldHeader().getVersion());
		return notes;
	}

	private void doClearMessage() {
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
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		map.put("layoutDesign", numberOfColumns.getSelectedItem().getValue());


		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldDetailDialog.zul",
					window_ExtendedFieldDialog,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
			map.put("layoutDesign", numberOfColumns.getSelectedItem().getValue());
			// call the zul-file with the parameters packed in a map
			try {
				Executions.createComponents(
						"/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldDetailDialog.zul",
						window_ExtendedFieldDialog, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}

		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++        Field Detail List		   ++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Generate the Extended Field Details List in the Extended FieldDialogCtrl and
	 * set the list in the listBoxFieldDet listbox by using Pagination
	 */
	public void doFillFieldsList(List<ExtendedFieldDetail> extendedFieldDetails) {
		logger.debug("Entering");
		this.extendedFieldDetailsList = extendedFieldDetails;
		this.pagingFieldDetList.setDetailed(true);
		setTableName(this.extendedFieldDetailsList);
		setExtendedFieldDetailsList(extendedFieldDetails);
		getExtendedFieldPagedListWrapper().initList(extendedFieldDetailsList,listBoxFieldDet,pagingFieldDetList);
		this.listBoxFieldDet.setItemRenderer(new ExtendedFieldListItemRenderer());
		logger.debug("Leaving");
	}

	//Getting the table name from map
	private void setTableName(List<ExtendedFieldDetail> extendedFieldDetails){
		for(int i=0;i<extendedFieldDetails.size();i++){
			if(PennantStaticListUtil.getModuleName().containsKey(extendedFieldDetails.get(i).getLovDescModuleName())){
				extendedFieldDetails.get(i).setLovDescSubModuleName(PennantStaticListUtil.getModuleName().get(
						extendedFieldDetails.get(i).getLovDescModuleName()).get(
								extendedFieldDetails.get(i).getLovDescSubModuleName()));

			}
		}
	}

	private void refreshList() {
		final JdbcSearchObject<ExtendedFieldHeader> soExtendedFieldDetail = getExtendedFieldDetailListCtrl().getSearchObj();
		getExtendedFieldDetailListCtrl().pagingExtendedFieldDetailList.setActivePage(0);
		getExtendedFieldDetailListCtrl().getPagedListWrapper().setSearchObject(soExtendedFieldDetail);
		if (getExtendedFieldDetailListCtrl().listBoxExtendedFieldDetail != null) {
			getExtendedFieldDetailListCtrl().listBoxExtendedFieldDetail.getListModel();
		}
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
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
			this.extendedFieldPagedListWrapper = (PagedListWrapper<ExtendedFieldDetail>) SpringUtil.getBean("pagedListWrapper");
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

}
