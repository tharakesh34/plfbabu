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
 * FileName    		:  InsuranceTypeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-12-2016    														*
 *                                                                  						*
 * Modified Date    :  19-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-12-2016       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.InsuranceType;

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
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.InsuranceType;
import com.pennant.backend.model.applicationmaster.InsuranceTypeProvider;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinTypeAccount;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.InsuranceTypeService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/applicationmasters/InsuranceType/insuranceTypeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class InsuranceTypeDialogCtrl extends GFCBaseCtrl<InsuranceType> {
	private static final long				serialVersionUID			= 1L;
	private static final Logger				logger						= Logger.getLogger(InsuranceTypeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting by our 'extends GFCBaseCtrl'
	 * GenericForwardComposer. ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window						window_InsuranceTypeDialog;
	protected Row							row0;
	protected Label							label_InsuranceType;
	protected Hlayout						hlayout_InsuranceType;
	protected Space							space_InsuranceType;

	protected Uppercasebox					insuranceTypeCode;
	protected Label							label_InsuranceTypeDesc;
	protected Hlayout						hlayout_InsuranceTypeDesc;
	protected Space							space_InsuranceTypeDesc;

	protected Textbox						insuranceTypeDesc;

	protected Label							recordType;
	protected Groupbox						gb_statusDetails;
	private boolean							enqModule					= false;

	// not auto wired vars
	private InsuranceType					insuranceType;																	// overhanded per param
	private transient InsuranceTypeListCtrl	insuranceTypeListCtrl;															// overhanded
																															// per param
	private boolean							notes_Entered				= false;

	// Button controller for the CRUD buttons
	private transient final String			btnCtroller_ClassPrefix		= "button_InsuranceTypeDialog_";
	protected Button						btnHelp;
	private Button							btnNew_insuranceProvider;
	private Listbox							listBoxInsuranceProvider;
	// ServiceDAOs / Domain Classes
	private transient InsuranceTypeService	insuranceTypeService;
	private transient PagedListService		pagedListService;
	protected Grid							grid_insuranceType;
	private PagedListWrapper<InsuranceType>	insuranceTypetDetailPagedListWrapper;
	private List<InsuranceTypeProvider>		insuranceTypeProviderList	= new ArrayList<InsuranceTypeProvider>();

	/**
	 * default constructor.<br>
	 */
	public InsuranceTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "InsuranceTypeDialog";
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected InsuranceType object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_InsuranceTypeDialog(Event event) throws Exception {
		logger.debug("Entring" + event.toString());
		try {

			// Set the page level components.
			setPageComponents(window_InsuranceTypeDialog);

			// READ OVERHANDED params !
			if (arguments.containsKey("insuranceType")) {
				this.insuranceType = (InsuranceType) arguments.get("insuranceType");
				InsuranceType befImage = new InsuranceType();
				BeanUtils.copyProperties(insuranceType, befImage);
				insuranceType.setBefImage(befImage);

				setInsuranceType(this.insuranceType);
			} else {
				setInsuranceType(null);
			}
			doLoadWorkFlow(this.insuranceType.isWorkflow(), this.insuranceType.getWorkflowId(),
					this.insuranceType.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "InsuranceTypeDialog");
			} else {
				getUserWorkspace().allocateAuthorities("InsuranceTypeDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the insuranceTypeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete insuranceType here.
			if (arguments.containsKey("insuranceTypeListCtrl")) {
				setInsuranceTypeListCtrl((InsuranceTypeListCtrl) arguments.get("insuranceTypeListCtrl"));
			} else {
				setInsuranceTypeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			this.listBoxInsuranceProvider.setHeight(getListBoxHeight(this.grid_insuranceType.getRows()
					.getVisibleItemCount() + 3));
			doShowDialog(getInsuranceType());
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
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

		doWriteBeanToComponents(this.insuranceType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_InsuranceTypeDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_InsuranceTypeDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
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
		doShowNotes(this.insuranceType);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aInsuranceType
	 * @throws InterruptedException
	 */
	public void doShowDialog(InsuranceType aInsuranceType) throws InterruptedException {

		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (aInsuranceType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.insuranceTypeCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.insuranceTypeDesc.focus();
				if (StringUtils.isNotBlank(aInsuranceType.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aInsuranceType);
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_InsuranceTypeDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");

	}

	// Finance Flags Details
	public void onClick$btnNew_insuranceProvider(Event event) throws Exception {
		InsuranceTypeProvider insuranceTypeProvider = null;
		insuranceTypeProvider = new InsuranceTypeProvider();
		insuranceTypeProvider.setInsuranceType(this.insuranceTypeCode.getValue());
		insuranceTypeProvider.setInsuranceTypeDesc(this.insuranceTypeDesc.getValue());
		insuranceTypeProvider.setNewRecord(true);
		insuranceTypeProvider.setWorkflowId(getWorkFlowId());
		
		doShowDialogPage(insuranceTypeProvider);
	}
	
	
	public void onInsuranceTypeProviderItemDoubleClicked(ForwardEvent event) throws InterruptedException {

		logger.debug("Entering" + event.toString());
		Listitem item = (Listitem) event.getOrigin().getTarget();
		InsuranceTypeProvider itemdata = (InsuranceTypeProvider) item.getAttribute("data");
		if (!StringUtils.trimToEmpty(itemdata.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			itemdata.setNewRecord(false);
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("insuranceTypeProvider", itemdata);
			itemdata.setInsuranceTypeDesc(this.insuranceType.getInsuranceTypeDesc());
			map.put("InsuranceTypeDialogCtrl", this);
			map.put("role", getRole());
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/ApplicationMaster/InsuranceType/InsuranceTypeProviderDialog.zul", null,map);
				
				
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving" + event.toString());
	
		
		
	}

	private void doShowDialogPage(InsuranceTypeProvider insuranceTypeProvider) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("insuranceTypeProvider", insuranceTypeProvider);
		map.put("InsuranceTypeDialogCtrl", this);
		map.put("role", getRole());

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/InsuranceType/InsuranceTypeProviderDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");

	}

	public void doFillInsuranceTypeProviders(List<InsuranceTypeProvider> insuranceTypeProviderList) {
		logger.debug("Entering");
		try {
			if (insuranceTypeProviderList != null) {
				setInsuranceTypeProviderList(insuranceTypeProviderList);
				fillInsuranceTypeProviders(insuranceTypeProviderList);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
	}

	private void fillInsuranceTypeProviders(List<InsuranceTypeProvider> insuranceTypeProviderList) {
		this.listBoxInsuranceProvider.getItems().clear();
		for (InsuranceTypeProvider InsTypeProvider : insuranceTypeProviderList) {
			Listitem item = new Listitem();
			Listcell lc;

			lc = new Listcell(InsTypeProvider.getProviderCode());
			lc.setParent(item);
			
			lc = new Listcell(InsTypeProvider.getProviderName());
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.formatRate(InsTypeProvider.getInsuranceRate().doubleValue(),9));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
            if(isWorkFlowEnabled()){
			lc = new Listcell(InsTypeProvider.getRecordStatus());
			lc.setParent(item);

			lc = new Listcell(PennantJavaUtil.getLabel(InsTypeProvider.getRecordType()));
			lc.setParent(item);
            
            }

			item.setAttribute("data", InsTypeProvider);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onInsuranceTypeProviderItemDoubleClicked");
			this.listBoxInsuranceProvider.appendChild(item);
		}
	}

	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_InsuranceTypeDialog_btnNew"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_InsuranceTypeDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_InsuranceTypeDialog_btnSave"));
			this.btnNew_insuranceProvider.setVisible(getUserWorkspace().isAllowed("btnNew_InsuranceTypeDialog_insuranceProvider"));
		}

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.insuranceTypeCode.setMaxlength(8);
		this.insuranceTypeDesc.setMaxlength(50);

		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
		logger.debug("Leaving");
	}

	
	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aInsuranceType
	 *            InsuranceType
	 */
	public void doWriteBeanToComponents(InsuranceType aInsuranceType) {
		logger.debug("Entering");
		this.insuranceTypeCode.setValue(aInsuranceType.getInsuranceType());
		this.insuranceTypeDesc.setValue(aInsuranceType.getInsuranceTypeDesc());
		doFillInsuranceTypeProviders(aInsuranceType.getInsuranceProviders());
		this.recordStatus.setValue(aInsuranceType.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aInsuranceType
	 */
	public void doWriteComponentsToBean(InsuranceType aInsuranceType) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Insurance Type
		try {
			aInsuranceType.setInsuranceType(this.insuranceTypeCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Insurance Type Description
		try {
			aInsuranceType.setInsuranceTypeDesc(this.insuranceTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		// Insurance Type
		if (!this.insuranceTypeCode.isReadonly()) {
			this.insuranceTypeCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_InsuranceTypeDialog_InsuranceType.value"), PennantRegularExpressions.REGEX_ALPHANUM_CODE,
					true));
		}
		// Insurance Type Description
		if (!this.insuranceTypeDesc.isReadonly()) {
			this.insuranceTypeDesc.setConstraint(new PTStringValidator(Labels
					.getLabel("label_InsuranceTypeDialog_InsuranceTypeDesc.value"),
					PennantRegularExpressions.REGEX_NAME, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.insuranceTypeCode.setConstraint("");
		this.insuranceTypeDesc.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		// Insurance Type
		// Insurance Type Description
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.insuranceTypeCode.setErrorMessage("");
		this.insuranceTypeDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getInsuranceTypeListCtrl().search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.insuranceType.getInsuranceType());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		if (!enqModule && isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");

			if (MessageUtil.confirm(msg) == MessageUtil.YES) {
				doSave();
				close = false;
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			closeDialog();
		}

		logger.debug("Leaving");
	}

	/**
	 * Deletes a InsuranceType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final InsuranceType aInsuranceType = new InsuranceType();
		BeanUtils.copyProperties(getInsuranceType(), aInsuranceType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aInsuranceType.getInsuranceType();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aInsuranceType.getRecordType()).equals("")) {
				aInsuranceType.setVersion(aInsuranceType.getVersion() + 1);
				aInsuranceType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aInsuranceType.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aInsuranceType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aInsuranceType.getNextTaskId(),
							aInsuranceType);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aInsuranceType, tranType)) {
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
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getInsuranceType().isNewRecord()) {
			this.insuranceTypeCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.insuranceTypeCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.insuranceTypeDesc.setReadonly(isReadOnly("InsuranceTypeDialog_InsuranceTypeDesc"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.insuranceType.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			//btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.insuranceTypeCode.setReadonly(true);
		this.insuranceTypeDesc.setReadonly(true);
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
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.insuranceTypeCode.setValue("");
		this.insuranceTypeDesc.setValue("");
		logger.debug("Leaving");
	}

	public void onFinTypeAccountItemDoubleClicked(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Listitem item = (Listitem) event.getOrigin().getTarget();
		FinTypeAccount itemdata = (FinTypeAccount) item.getAttribute("data");
		if (!StringUtils.trimToEmpty(itemdata.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			itemdata.setNewRecord(false);
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("finTypeAccount", itemdata);
			map.put("financeTypeDialogCtrl", this);
			map.put("role", getRole());
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/FinTypeAccountDialog.zul",
						null, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final InsuranceType aInsuranceType = new InsuranceType();
		BeanUtils.copyProperties(getInsuranceType(), aInsuranceType);
		boolean isNew = false;
		boolean isprovidersEmpty=false;

		if (isWorkFlowEnabled()) {
			aInsuranceType.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aInsuranceType.getNextTaskId(), aInsuranceType);
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aInsuranceType.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the InsuranceType object with the components data
			doWriteComponentsToBean(aInsuranceType);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aInsuranceType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aInsuranceType.getRecordType()).equals("")) {
				aInsuranceType.setVersion(aInsuranceType.getVersion() + 1);
				if (isNew) {
					aInsuranceType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aInsuranceType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aInsuranceType.setNewRecord(true);
				}
			}
		} else {
			aInsuranceType.setVersion(aInsuranceType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			
			if(isprovidersEmpty){
				throw new WrongValueException(this.btnNew_insuranceProvider,
						Labels.getLabel("label_InsuranceProviderList_AtleastOne_Mandatory"));
			}
			if (doProcess(aInsuranceType, tranType)) {
				// doWriteBeanToComponents(aInsuranceType);
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
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

	private boolean doProcess(InsuranceType aInsuranceType, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aInsuranceType.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aInsuranceType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aInsuranceType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aInsuranceType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aInsuranceType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aInsuranceType);
				}

				if (isNotesMandatory(taskId, aInsuranceType)) {
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
			aInsuranceType.setTaskId(taskId);
			aInsuranceType.setNextTaskId(nextTaskId);
			aInsuranceType.setRoleCode(getRole());
			aInsuranceType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aInsuranceType, tranType);
			String operationRefs = getServiceOperations(taskId, aInsuranceType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aInsuranceType, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aInsuranceType, tranType);
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
		InsuranceType insuranceType = (InsuranceType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getInsuranceTypeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getInsuranceTypeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getInsuranceTypeService().doApprove(auditHeader);

						if (insuranceType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getInsuranceTypeService().doReject(auditHeader);

						if (insuranceType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_InsuranceTypeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_InsuranceTypeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.insuranceType), true);
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(InsuranceType aInsuranceType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aInsuranceType.getBefImage(), aInsuranceType);
		return new AuditHeader(aInsuranceType.getInsuranceType(), null, null, null, auditDetail,
				aInsuranceType.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setInsuranceTypeService(InsuranceTypeService insuranceTypeService) {
		this.insuranceTypeService = insuranceTypeService;
	}

	public InsuranceType getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(InsuranceType insuranceType) {
		this.insuranceType = insuranceType;
	}

	public InsuranceTypeService getInsuranceTypeService() {
		return this.insuranceTypeService;
	}

	public void setInsuranceTypeListCtrl(InsuranceTypeListCtrl insuranceTypeListCtrl) {
		this.insuranceTypeListCtrl = insuranceTypeListCtrl;
	}

	public InsuranceTypeListCtrl getInsuranceTypeListCtrl() {
		return this.insuranceTypeListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public List<InsuranceTypeProvider> getInsuranceTypeProviderList() {
		return insuranceTypeProviderList;
	}

	public void setInsuranceTypeProviderList(List<InsuranceTypeProvider> insuranceTypeProviderList) {
		this.insuranceTypeProviderList = insuranceTypeProviderList;
	}

	public PagedListWrapper<InsuranceType> getInsuranceTypetDetailPagedListWrapper() {
		return insuranceTypetDetailPagedListWrapper;
	}

	@SuppressWarnings("unchecked")
	public void setInsuranceTypetDetailPagedListWrapper(
			PagedListWrapper<InsuranceType> insiranceTypetDetailPagedListWrapper) {
		if (this.insuranceTypetDetailPagedListWrapper == null) {
			this.insuranceTypetDetailPagedListWrapper = (PagedListWrapper<InsuranceType>) SpringUtil
					.getBean("pagedListWrapper");
			;
		}
	}

}
