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
 * FileName    		:  ProductFinanceTypeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-08-2011    														*
 *                                                                  						*
 * Modified Date    :  13-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.productfinancetype;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.ProductFinanceType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.rmtmasters.ProductFinanceTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RMTMasters/ProductFinanceType/productFinanceTypeDialog.zul
 * file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class ProductFinanceTypeDialogCtrl extends GFCBaseListCtrl<FinanceType>
implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger
	.getLogger(ProductFinanceTypeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_ProductFinanceTypeDialog; // auto wired

	protected Textbox productCode; // auto wired
	protected Textbox finType; // auto wired

	protected Label recordStatus; // auto wired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected Row statusRow;

	// not auto wired vars
	private ProductFinanceType productFinanceType; // over handed per param
	private transient ProductFinanceTypeListCtrl productFinanceTypeListCtrl; // over handed
	// per
	// param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_productCode;
	private transient String oldVar_finType;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_ProductFinanceTypeDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // auto wired
	protected Button btnEdit; // auto wired
	protected Button btnDelete; // auto wired
	protected Button btnSave; // auto wired
	protected Button btnCancel; // auto wired
	protected Button btnClose; // auto wired
	protected Button btnHelp; // auto wired
	protected Button btnNotes; // auto wired

	// ServiceDAOs / Domain Classes
	private transient ProductFinanceTypeService productFinanceTypeService;
	private transient PagedListService pagedListService;

	protected Listbox listbox_ProductFintype;
	protected JdbcSearchObject<FinanceType> searchObj;
	protected Paging pagingProductFintype;

	protected Listbox listbox_ProductFintypeSelected;
	protected Button select;
	protected Button deselect;
	protected Button deselectAll;
	/**
	 * default constructor.<br>
	 */
	public ProductFinanceTypeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected ProductFinanceType
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ProductFinanceTypeDialog(Event event)
	throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,
				this.btnNotes);

		// get the params map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("productFinanceType")) {
			this.productFinanceType = (ProductFinanceType) args
			.get("productFinanceType");
			ProductFinanceType befImage = new ProductFinanceType();
			BeanUtils.copyProperties(this.productFinanceType, befImage);
			this.productFinanceType.setBefImage(befImage);

			setProductFinanceType(this.productFinanceType);
		} else {
			setProductFinanceType(null);
		}

		doLoadWorkFlow(this.productFinanceType.isWorkflow(),
				this.productFinanceType.getWorkflowId(),
				this.productFinanceType.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(),
			"ProductFinanceTypeDialog");
		}

		// READ OVERHANDED params !
		// we get the productFinanceTypeListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete productFinanceType here.
		if (args.containsKey("productFinanceTypeListCtrl")) {
			setProductFinanceTypeListCtrl((ProductFinanceTypeListCtrl) args
					.get("productFinanceTypeListCtrl"));
		} else {
			setProductFinanceTypeListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();

		this.searchObj = new JdbcSearchObject<FinanceType>(FinanceType.class,
				10);
		this.searchObj.addTabelName("RMTFinanceTypes");
		this.searchObj.addSort("FinType", false);
		doShowDialog(getProductFinanceType());
		getPagedListWrapper().init(this.searchObj, this.listbox_ProductFintype,
				this.pagingProductFintype);
		this.pagingProductFintype.setVisible(false);
		this.listbox_ProductFintype
		.setItemRenderer(new financtypeItemRenderer());

		logger.debug("Leaving ");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.productCode.setMaxlength(8);
		this.finType.setMaxlength(50);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(false);
		} else {
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("ProductFinanceTypeDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed(
		"button_ProductFinanceTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
		"button_ProductFinanceTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
		"button_ProductFinanceTypeDialog_btnDelete"));
		this.btnSave.setVisible(true);
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_ProductFinanceTypeDialog(Event event)
	throws Exception {
		logger.debug(event.toString());
		doClose();
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());

		doSave();
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		doEdit();
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_ProductFinanceTypeDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug(event.toString());
		doNew();
		logger.debug("Leaving");
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doDelete();
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
		doCancel();
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
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
		logger.debug("Enterring");
		boolean close = true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels
			.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
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
			closeDialog(this.window_ProductFinanceTypeDialog,
			"ProductFinanceType");
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
	 * @param aProductFinanceType
	 *            ProductFinanceType
	 */
	public void doWriteBeanToComponents(ProductFinanceType aProductFinanceType) {
		logger.debug("Entering");
		this.productCode.setValue(aProductFinanceType.getProductCode());
		this.finType.setValue(aProductFinanceType.getFinType());

		this.recordStatus.setValue(aProductFinanceType.getRecordStatus());

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aProductFinanceType
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(ProductFinanceType aProductFinanceType)
	throws InterruptedException {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aProductFinanceType.setProductCode(this.productCode.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProductFinanceType.setFinType(this.finType.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();
		doRemoveLOVValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aProductFinanceType.setRecordStatus(this.recordStatus.getValue());

		logger.debug("Leaving");

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aProductFinanceType
	 * @throws InterruptedException
	 */
	public void doShowDialog(ProductFinanceType aProductFinanceType)
	throws InterruptedException {
		logger.debug("Entering");

		// if aProductFinanceType == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aProductFinanceType == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			aProductFinanceType = getProductFinanceTypeService()
			.getNewProductFinanceType();

			setProductFinanceType(aProductFinanceType);
		} else {
			setProductFinanceType(aProductFinanceType);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aProductFinanceType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finType.focus();
		} else {
			this.finType.focus();
			if (isWorkFlowEnabled()) {
				if (!StringUtils.trimToEmpty(aProductFinanceType.getRecordType()).equals("")){
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
			doWriteBeanToComponents(aProductFinanceType);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_ProductFinanceTypeDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Enterring");
		this.oldVar_productCode = this.productCode.getValue();
		this.oldVar_finType = this.finType.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Enterring");
		this.productCode.setValue(this.oldVar_productCode);
		this.finType.setValue(this.oldVar_finType);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */

	private boolean isDataChanged() {
		logger.debug("Enterring");
		// To clear the Error Messages
		doClearMessage();
		/*
		 * if (this.oldVar_prdFinId != this.prdFinId.longValue()) { return true;
		 * }
		 */
		if (this.oldVar_productCode != this.productCode.getValue()) {
			return true;
		}
		if (this.oldVar_finType != this.finType.getValue()) {
			return true;
		}

		logger.debug("Leaving isDataChanged()");

		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Enterring");
		setValidationOn(true);

		if (!this.productCode.isReadonly()) {
			this.productCode
			.setConstraint("NO EMPTY:"
					+ Labels.getLabel(
							"FIELD_NO_EMPTY",
							new String[] { Labels
									.getLabel("label_ProductFinanceTypeDialog_ProductCode.value") }));
		}
		if (!this.finType.isReadonly()) {
			this.finType
			.setConstraint("NO EMPTY:"
					+ Labels.getLabel(
							"FIELD_NO_EMPTY",
							new String[] { Labels
									.getLabel("label_ProductFinanceTypeDialog_FinType.value") }));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Enterring");
		setValidationOn(false);
		this.productCode.setConstraint("");
		this.finType.setConstraint("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a ProductFinanceType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Enterring");
		final ProductFinanceType aProductFinanceType = new ProductFinanceType();
		BeanUtils.copyProperties(getProductFinanceType(), aProductFinanceType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
		.getLabel("message.Question.Are_you_sure_to_delete_this_record")
		+ "\n\n --> " + aProductFinanceType.getPrdFinId();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));
		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");
			if (StringUtils.trimToEmpty(aProductFinanceType.getRecordType())
					.equals("")) {
				aProductFinanceType
				.setVersion(aProductFinanceType.getVersion() + 1);
				aProductFinanceType
				.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aProductFinanceType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aProductFinanceType, tranType)) {

					final JdbcSearchObject<ProductFinanceType> soProductFinanceType = getProductFinanceTypeListCtrl()
					.getSearchObj();
					// Set the ListModel
					getProductFinanceTypeListCtrl().getPagedListWrapper()
					.setSearchObject(soProductFinanceType);

					// now synchronize the ProductFinanceType listBox
					final ListModelList lml = (ListModelList) getProductFinanceTypeListCtrl().listBoxProductFinanceType
					.getListModel();

					// Check if the ProductFinanceType object is new or updated
					// -1
					// means that the obj is not in the list, so it's new ..
					if (lml.indexOf(aProductFinanceType) == -1) {
					} else {
						lml.remove(lml.indexOf(aProductFinanceType));
					}
					closeDialog(this.window_ProductFinanceTypeDialog,
					"ProductFinanceType");
				}

			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new ProductFinanceType object. <br>
	 */
	private void doNew() {
		logger.debug("Enterring");

		final ProductFinanceType aProductFinanceType = getProductFinanceTypeService()
		.getNewProductFinanceType();
		setProductFinanceType(aProductFinanceType);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.finType.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Enterring");

		if (getProductFinanceType().isNewRecord()) {
			this.productCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.productCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.productCode.setReadonly(false);
		this.finType.setReadonly(false);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.productFinanceType.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		// remember the old vars
		doStoreInitValues();
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Enterring");
		this.productCode.setReadonly(true);
		this.finType.setReadonly(true);

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
		logger.debug("Enterring");
		// remove validation, if there are a save before

		this.productCode.setValue("");
		this.finType.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Enterring");
		final ProductFinanceType aProductFinanceType = new ProductFinanceType();
		BeanUtils.copyProperties(getProductFinanceType(), aProductFinanceType);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the ProductFinanceType object with the components data
		doWriteComponentsToBean(aProductFinanceType);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aProductFinanceType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aProductFinanceType.getRecordType())
					.equals("")) {
				aProductFinanceType
				.setVersion(aProductFinanceType.getVersion() + 1);
				if (isNew) {
					aProductFinanceType
					.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aProductFinanceType
					.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aProductFinanceType.setNewRecord(true);
				}
			}
		} else {
			aProductFinanceType
			.setVersion(aProductFinanceType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aProductFinanceType, tranType)) {

				// ++ create the searchObject and init sorting ++ //
				final JdbcSearchObject<ProductFinanceType> soProductFinanceType = getProductFinanceTypeListCtrl()
				.getSearchObj();

				// Set the ListModel
				getProductFinanceTypeListCtrl().pagingProductFinanceTypeList
				.setActivePage(0);
				getProductFinanceTypeListCtrl().getPagedListWrapper()
				.setSearchObject(soProductFinanceType);

				// call from cusromerList then synchronize the
				// ProductFinanceType listBox
				if (getProductFinanceTypeListCtrl().listBoxProductFinanceType != null) {
					// now synchronize the ProductFinanceType listBox
					getProductFinanceTypeListCtrl().listBoxProductFinanceType
					.getListModel();
				}

				doReadOnly();
				this.btnCtrl.setBtnStatus_Save();

				// Close the Existing Dialog
				closeDialog(this.window_ProductFinanceTypeDialog,
				"ProductFinanceType");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}

		logger.debug("Leaving");
	}

	private boolean doProcess(ProductFinanceType aProductFinanceType,
			String tranType) {
		logger.debug("Enterring");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";
		aProductFinanceType.setLastMntBy(getUserWorkspace()
				.getLoginUserDetails().getLoginUsrID());
		aProductFinanceType.setLastMntOn(new Timestamp(System
				.currentTimeMillis()));
		aProductFinanceType.setUserDetails(getUserWorkspace()
				.getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aProductFinanceType.setRecordStatus(userAction.getSelectedItem()
					.getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aProductFinanceType
						.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId,
							aProductFinanceType);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow()
						.getAuditingReq(taskId, aProductFinanceType))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels
									.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode = getWorkFlow().firstTask.owner;
			} else {
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

			aProductFinanceType.setTaskId(taskId);
			aProductFinanceType.setNextTaskId(nextTaskId);
			aProductFinanceType.setRoleCode(getRole());
			aProductFinanceType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aProductFinanceType, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,
					aProductFinanceType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aProductFinanceType,
							PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aProductFinanceType, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Enterring");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		ProductFinanceType aProductFinanceType = (ProductFinanceType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes=false;
		
		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getProductFinanceTypeService().delete(
								auditHeader);

						deleteNotes=true;
					} else {
						auditHeader = getProductFinanceTypeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getProductFinanceTypeService().doApprove(
								auditHeader);
						
						if(aProductFinanceType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getProductFinanceTypeService().doReject(
								auditHeader);
						if(aProductFinanceType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_ProductFinanceTypeDialog,
								auditHeader);
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(
						this.window_ProductFinanceTypeDialog, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					
					if(deleteNotes){
						deleteNotes(getNotes(),true);
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public ProductFinanceType getProductFinanceType() {
		return this.productFinanceType;
	}

	public void setProductFinanceType(ProductFinanceType productFinanceType) {
		this.productFinanceType = productFinanceType;
	}

	public void setProductFinanceTypeService(
			ProductFinanceTypeService productFinanceTypeService) {
		this.productFinanceTypeService = productFinanceTypeService;
	}

	public ProductFinanceTypeService getProductFinanceTypeService() {
		return this.productFinanceTypeService;
	}

	public void setProductFinanceTypeListCtrl(
			ProductFinanceTypeListCtrl productFinanceTypeListCtrl) {
		this.productFinanceTypeListCtrl = productFinanceTypeListCtrl;
	}

	public ProductFinanceTypeListCtrl getProductFinanceTypeListCtrl() {
		return this.productFinanceTypeListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	private AuditHeader getAuditHeader(ProductFinanceType aProductFinanceType,
			String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aProductFinanceType.getBefImage(), aProductFinanceType);
		return new AuditHeader(
				String.valueOf(aProductFinanceType.getPrdFinId()), null, null,
				null, auditDetail, aProductFinanceType.getUserDetails(),getOverideMap());
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();

		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_ProductFinanceTypeDialog,
					auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Enterring");
		// logger.debug(event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,
					map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes)
					.equalsIgnoreCase("Y")) {
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

	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("ProductFinanceType");
		notes.setReference(String
				.valueOf(getProductFinanceType().getPrdFinId()));
		notes.setVersion(getProductFinanceType().getVersion());
		return notes;
	}

	private void doClearMessage() {
		logger.debug("Enterring");
		this.productCode.setErrorMessage("");
		this.finType.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * To Render the items in Available financtype List box
	 */
	public class financtypeItemRenderer implements ListitemRenderer<FinanceType>,
	Serializable {
		private static final long serialVersionUID = 2118469590661434900L;
		//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
		public void render(Listitem item, FinanceType fintype, int count) throws Exception {
			logger.debug("Entering rederer");
			//FinanceType fintype = (FinanceType) data;
			Listcell lc;
			lc = new Listcell();
			lc.setLabel(fintype.getFinType());
			lc.setParent(item);
			lc = new Listcell();
			lc.setLabel(fintype.getFinTypeDesc());
			lc.setParent(item);
			item.setAttribute("data", fintype);

		}
	}

	@SuppressWarnings({ "rawtypes" })
	public void onClick$select(Event event) throws Exception {
		logger.debug(event.toString());
		if (this.listbox_ProductFintype.getSelectedCount() != 0) {
			// Messagebox.show(Integer.toString(i));
			Listitem li = new Listitem();// To read List Item
			Set SeletedSet = new HashSet();// To get Selected Items
			SeletedSet = this.listbox_ProductFintype.getSelectedItems();
			Iterator it = SeletedSet.iterator();
			while (it.hasNext()) {
				li = (Listitem) it.next();
				Listcell slecteditem = new Listcell();
				Listcell slecteditemDesc = new Listcell();
				List SelectedRowValues = new ArrayList();// TO get each row
				// Details
				SelectedRowValues = li.getChildren();
				slecteditem = (Listcell) SelectedRowValues.get(0);
				slecteditemDesc = (Listcell) SelectedRowValues.get(1);
				fillListbox(this.listbox_ProductFintypeSelected,
						slecteditem.getLabel(), slecteditemDesc.getLabel());
				// apply the not equal filter for selected values
				this.searchObj.addFilterNotEqual("FinType", li.getLabel());
			}
			getPagedListWrapper().init(this.searchObj,
					this.listbox_ProductFintype, this.pagingProductFintype);
			this.pagingProductFintype.setVisible(false);
			this.listbox_ProductFintype
			.setItemRenderer(new financtypeItemRenderer());

		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onClick$deselect(Event event) throws Exception {
		logger.debug(event.toString());

		if (this.listbox_ProductFintypeSelected.getSelectedCount() != 0) {
			// ////// To Remove Selected item from the List Box
			Listitem li = new Listitem();// To read List Item
			Set sata = new HashSet();// To get Selected Items
			sata = this.listbox_ProductFintypeSelected.getSelectedItems();
			List list = new ArrayList(sata); // Converting Set to ArrayList to
			// Make Concurrent operations
			System.out.println(sata.size());
			Iterator it = list.iterator();
			while (it.hasNext()) {
				li = (Listitem) it.next();
				System.out.println(li.getLabel());
				// / To remove the not equal filter applied in selection
				Filter filter = new Filter("FinType", li.getLabel(),
						Filter.OP_NOT_EQUAL);
				this.searchObj.removeFilter(filter);
				this.listbox_ProductFintypeSelected.removeItemAt(li.getIndex());
			}
			getPagedListWrapper().init(this.searchObj,
					this.listbox_ProductFintype, this.pagingProductFintype);
			this.pagingProductFintype.setVisible(false);
			this.listbox_ProductFintype
			.setItemRenderer(new financtypeItemRenderer());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onClick$deselectAll(Event event) throws InterruptedException {

		if (this.listbox_ProductFintypeSelected.getItemCount() != 0) {

			// ////// To Remove Selected item from the List Box
			Listitem li = new Listitem();// To read List Item
			// Set sata= new HashSet();//To get Selected Items
			// sata=this.listbox_ProductFintypeSelected.getItems();
			List list = new ArrayList();
			list.addAll(this.listbox_ProductFintypeSelected.getItems());
			// Converting Set to ArrayList to Make Concurrent operations
			System.out.println(list.size());
			Iterator it = list.iterator();
			while (it.hasNext()) {
				li = (Listitem) it.next();
				System.out.println(li.getLabel());
				// / To remove the not equal filter applied in selection
				Filter filter = new Filter("FinType", li.getLabel(),
						Filter.OP_NOT_EQUAL);
				this.searchObj.removeFilter(filter);
				this.listbox_ProductFintypeSelected.removeItemAt(li.getIndex());
			}
			getPagedListWrapper().init(this.searchObj,
					this.listbox_ProductFintype, this.pagingProductFintype);
			this.pagingProductFintype.setVisible(false);
			this.listbox_ProductFintype
			.setItemRenderer(new financtypeItemRenderer());
		}

	}

	/**
	 * To fill the List box Values
	 */
	private void fillListbox(Listbox listbox, String value1, String Value2) {
		Listitem item = new Listitem(); // To Create List item
		Listcell lc;
		lc = new Listcell();
		lc.setLabel(value1);
		lc.setParent(item);
		lc = new Listcell();
		lc.setLabel(Value2);
		lc.setParent(item);
		listbox.appendChild(item);
	}

}
