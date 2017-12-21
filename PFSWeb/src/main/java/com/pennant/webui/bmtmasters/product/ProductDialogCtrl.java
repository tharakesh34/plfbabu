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
 * FileName    		:  ProductDialogCtrl.java                                               * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-08-2011    														*
 *                                                                  						*
 * Modified Date    :  12-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.bmtmasters.product;

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
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.model.rmtmasters.ProductAsset;
import com.pennant.backend.service.bmtmasters.ProductService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.rmtmasters.productAsset.model.ProductAssetListModelItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.pagging.PagedListWrapper;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/Product/productDialog.zul file.
 */
public class ProductDialogCtrl extends GFCBaseCtrl<ProductAsset> {
	private static final long serialVersionUID = -8421583705358772016L;
	private static final Logger logger = Logger
			.getLogger(ProductDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ProductDialog;

	protected Uppercasebox productCode;
	protected Textbox productDesc;
	protected Combobox productCategory;
	protected Grid grid_Basicdetails;

	protected Row row_ProductCategory;

	// not auto wired variables
	private Product product; // over handed per parameters
	private transient ProductListCtrl productListCtrl; // over handed per
														// parameters

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient ProductService productService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();

	// NEEDED for the ReUse in the SearchWindow

	int listRows;

	// Declaration of listHeaders in listBox of FeeTier
	protected Listheader listheader_ProductAsset_AssetCode;
	protected Listheader listheader_ProductAsset_AssetDesc;
	protected Listheader listheader_ProductAsset_AssetIsActive;
	protected Listheader listheader_ProductAsset_RecordStatus;
	protected Listheader listheader_ProductAsset_RecordType;

	// ProductAsset List
	protected Button btnNew_ProductAsset;
	protected Paging pagingProductAsset;
	protected Listbox listboxProductAsset;

	private List<ProductAsset> productAssetList = new ArrayList<ProductAsset>();
	private PagedListWrapper<ProductAsset> productAssetPagedListWrapper;

	/**
	 * default constructor.<br>
	 */
	public ProductDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ProductDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Product object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ProductDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ProductDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();
			setProductAssetPagedListWrapper();// Initialize pagedListwrapper for
												// rendering ProductAsset list

			if (arguments.containsKey("product")) {
				this.product = (Product) arguments.get("product");
				Product befImage = new Product();
				BeanUtils.copyProperties(this.product, befImage);
				this.product.setBefImage(befImage);
				setProduct(this.product);
			} else {
				setProduct(null);
			}

			doLoadWorkFlow(this.product.isWorkflow(),
					this.product.getWorkflowId(), this.product.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"ProductDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the productListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete product here.
			if (arguments.containsKey("productListCtrl")) {
				setProductListCtrl((ProductListCtrl) arguments
						.get("productListCtrl"));
			} else {
				setProductListCtrl(null);
			}

			// Set the DialogController Height for listBox
			getBorderLayoutHeight();
			grid_Basicdetails.getRows().getVisibleItemCount();
			int dialogHeight = grid_Basicdetails.getRows()
					.getVisibleItemCount() * 20 + 100 + 75;
			int listboxHeight = borderLayoutHeight - dialogHeight;
			listboxProductAsset.setHeight(listboxHeight + "px");
			listRows = Math.round(listboxHeight / 24) - 1;

			this.listheader_ProductAsset_AssetCode
					.setSortAscending(new FieldComparator("assetCode", true));
			this.listheader_ProductAsset_AssetCode
					.setSortDescending(new FieldComparator("assetCode", false));
			this.listheader_ProductAsset_AssetDesc
					.setSortAscending(new FieldComparator("assetDesc", true));
			this.listheader_ProductAsset_AssetDesc
					.setSortDescending(new FieldComparator("assetDesc", false));
			this.listheader_ProductAsset_AssetIsActive
					.setSortAscending(new FieldComparator("assetIsActive", true));
			this.listheader_ProductAsset_AssetIsActive
					.setSortDescending(new FieldComparator("assetIsActive",
							false));

			if (isWorkFlowEnabled()) {
				this.listheader_ProductAsset_RecordStatus
						.setSortAscending(new FieldComparator("recordStatus",
								true));
				this.listheader_ProductAsset_RecordStatus
						.setSortDescending(new FieldComparator("recordStatus",
								false));
				this.listheader_ProductAsset_RecordType
						.setSortAscending(new FieldComparator("recordType",
								true));
				this.listheader_ProductAsset_RecordType
						.setSortDescending(new FieldComparator("recordType",
								false));
			} else {
				this.listheader_ProductAsset_RecordStatus.setVisible(false);
				this.listheader_ProductAsset_RecordType.setVisible(false);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getProduct());
			this.btnDelete.setVisible(false);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ProductDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.productCode.setMaxlength(8);
		this.productDesc.setMaxlength(50);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
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

		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_ProductDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_ProductDialog_btnEdit"));
		this.btnDelete.setVisible(false);// getUserWorkspace().isAllowed("button_ProductDialog_btnDelete")
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_ProductDialog_btnSave"));
		this.btnNew_ProductAsset.setVisible(getUserWorkspace().isAllowed(
				"button_ProductDialog_btnNew_ProductAsset"));
		this.btnCancel.setVisible(false);

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
		MessageUtil.showHelpWindow(event, window_ProductDialog);
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
		doWriteBeanToComponents(this.product.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aProduct
	 *            Product
	 */
	public void doWriteBeanToComponents(Product aProduct) {
		logger.debug("Entering");
		this.productCode.setValue(aProduct.getProductCode());
		this.productDesc.setValue(aProduct.getProductDesc());
		this.recordStatus.setValue(aProduct.getRecordStatus());

		fillComboBox(this.productCategory, aProduct.getProductCategory(), PennantStaticListUtil.getProductCategories(), "");
		doFillProductAsset(aProduct.getProductAssetList());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aProduct
	 */
	public void doWriteComponentsToBean(Product aProduct) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aProduct.setProductCode(this.productCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProduct.setProductDesc(this.productDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aProduct.setProductCategory(this.productCategory.getSelectedItem()
					.getValue().toString());
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

		aProduct.setRecordStatus(this.recordStatus.getValue());
		aProduct.setProductAssetList(this.productAssetList);
		setProduct(aProduct);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aProduct
	 * @throws Exception
	 */
	public void doShowDialog(Product aProduct) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aProduct.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.productCode.focus();
		} else {
			this.productDesc.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aProduct);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ProductDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (!this.productCode.isReadonly()) {
			this.productCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_ProductDialog_ProductCode.value"), null,
					true));
		}
		if (!this.productDesc.isReadonly()) {
			this.productDesc.setConstraint(new PTStringValidator(Labels
					.getLabel("label_ProductDialog_ProductDesc.value"), null,
					true));
		}
		if (!this.productCategory.isDisabled()) {
			this.productCategory
					.setConstraint(new StaticListValidator(
							PennantStaticListUtil.getProductCategories(),
							Labels.getLabel("label_ProductDialog_ProductCategory.value")));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.productCode.setConstraint("");
		this.productDesc.setConstraint("");
		this.productCategory.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.productCode.setErrorMessage("");
		this.productDesc.setErrorMessage("");
		this.productCategory.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getProductListCtrl().search();
	}

	// CRUD operations

	/**
	 * Deletes a Product object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final Product aProduct = new Product();
		BeanUtils.copyProperties(getProduct(), aProduct);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> "
				+ Labels.getLabel("label_ProductDialog_ProductCode.value")
				+ " : " + aProduct.getProductCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			doWriteBeanToComponents(aProduct);

			if (StringUtils.isBlank(aProduct.getRecordType())) {
				aProduct.setVersion(aProduct.getVersion() + 1);
				aProduct.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aProduct.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aProduct, tranType)) {
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

		if (getProduct().isNewRecord()) {
			this.productCode.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.btnNew_ProductAsset.setVisible(false);
		} else {
			this.productCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.productDesc.setReadonly(isReadOnly("ProductDialog_productDesc"));
		this.productCategory
				.setDisabled(isReadOnly("ProductDialog_productCategory"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.product.isNewRecord()) {
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
		this.productCode.setReadonly(true);
		this.productDesc.setReadonly(true);
		this.productCategory.setDisabled(true);

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
		this.productCode.setValue("");
		this.productDesc.setValue("");
		this.productCategory.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final Product aProduct = new Product();
		BeanUtils.copyProperties(getProduct(), aProduct);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the Product object with the components data
		doWriteComponentsToBean(aProduct);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aProduct.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aProduct.getRecordType())) {
				aProduct.setVersion(aProduct.getVersion() + 1);
				if (isNew) {
					aProduct.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aProduct.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aProduct.setNewRecord(true);
				}
			}
		} else {
			aProduct.setVersion(aProduct.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (!aProduct.getRecordType().equals(
					PennantConstants.RECORD_TYPE_DEL)) {
				if (doProcess(aProduct, tranType)) {
					refreshList();
					closeDialog();
				}
			} else {
				MessageUtil.showError(Labels.getLabel("List_Error",
						new String[] { Labels.getLabel("ProductAsset"), Labels.getLabel("Product") }));
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aProduct
	 * @param tranType
	 * @return
	 */
	private boolean doProcess(Product aProduct, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aProduct.setLastMntBy(getUserWorkspace().getLoggedInUser()
				.getUserId());
		aProduct.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aProduct.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aProduct.setRecordStatus(userAction.getSelectedItem().getValue()
					.toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aProduct.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aProduct);
				}

				if (isNotesMandatory(taskId, aProduct)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();

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

			aProduct.setTaskId(taskId);
			aProduct.setNextTaskId(nextTaskId);
			aProduct.setRoleCode(getRole());
			aProduct.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aProduct, tranType);
			String operationRefs = getServiceOperations(taskId, aProduct);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aProduct,
							PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aProduct, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;
		Product product = (Product) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getProductService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getProductService().saveOrUpdate(
								auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getProductService()
								.doApprove(auditHeader);

						if (product.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getProductService().doReject(auditHeader);

						if (product.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels
										.getLabel("InvalidWorkFlowMethod"),
								null));
						retValue = ErrorControl.showErrorControl(
								this.window_ProductDialog, auditHeader);
						return processCompleted;

					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.window_ProductDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
				}

				if (deleteNotes) {
					deleteNotes(getNotes(this.product), true);
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

	// New Button & Double Click Events for Product Asset List

	public void onClick$btnNew_ProductAsset(Event event) throws Exception {
		logger.debug("Entering");

		if (StringUtils.trimToNull(this.productCode.getValue()) != null) {

			final ProductAsset aProductAsset = getProductService()
					.getNewProductAsset();
			aProductAsset.setNewRecord(true);
			aProductAsset.setProductCode(getProduct().getProductCode());

			final HashMap<String, Object> map = new HashMap<String, Object>();
			selectNewProdAssetBtn();
			aProductAsset.setProductCode(this.productCode.getValue());

			map.put("productAsset", aProductAsset);
			map.put("productDialogCtrl", this);
			map.put("roleCode", getRole());

			try {
				Executions
						.createComponents(
								"/WEB-INF/pages/RMTMasters/ProductAsset/ProductAssetDialog.zul",
								window_ProductDialog, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug("Leaving");
	}

	public void onProductAssetItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listboxProductAsset.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final ProductAsset productAsset = (ProductAsset) item
					.getAttribute("data");

			if (productAsset.getRecordStatus().equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_DEL)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else if (StringUtils.isBlank(this.productCode.getValue())) {
				MessageUtil.showError(Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { PennantJavaUtil.getLabel("label_ProductAssetDialog_ProductCode.value") }));
			} else {
				productAsset.setProductCode(this.productCode.getValue());
				productAsset.setNewRecord(false);
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("productAsset", productAsset);
				map.put("productDialogCtrl", this);
				map.put("roleCode", getRole());

				// call the ZUL-file with the parameters packed in a map
				try {
					Executions
							.createComponents(
									"/WEB-INF/pages/RMTMasters/ProductAsset/ProductAssetDialog.zul",
									window_ProductDialog, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * when restricted maximum amount is checked. <br>
	 * 
	 * @param event
	 */
	public void onBlur$productCode(Event event) {
		logger.debug("Entering" + event.toString());
		selectNewProdAssetBtn();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Allow Adding ProductAsset List by button as Visible/not
	 */
	private void selectNewProdAssetBtn() {
		logger.debug("Entering");

		this.btnNew_ProductAsset.setVisible(false);
		if (!(this.productCode.getValue() == null || StringUtils
				.isEmpty(this.productCode.getValue()))) {
			this.btnNew_ProductAsset.setVisible(getUserWorkspace().isAllowed(
					"button_ProductDialog_btnNew_ProductAsset"));
			// this.productCode.setReadonly(true);
		}
		logger.debug("Leaving");
	}

	// Product Asset Lists Refreshing

	/**
	 * Generate the Product Asset Details List in the ProductDialogCtrl and set
	 * the list in the listboxProductAsset listBox by using Pagination
	 */
	public void doFillProductAsset(List<ProductAsset> productAssets) {
		logger.debug("Entering");
		if (productAssets != null) {
			setProductAssetList(productAssets);
			this.pagingProductAsset.setPageSize(listRows);
			this.pagingProductAsset.setDetailed(true);
			getProductAssetPagedListWrapper().initList(productAssetList,
					this.listboxProductAsset, this.pagingProductAsset);
			this.listboxProductAsset
					.setItemRenderer(new ProductAssetListModelItemRenderer());
		}

		logger.debug("Leaving");
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aGender
	 *            (Gender)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(Product aProduct, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aProduct.getBefImage(), aProduct);
		return new AuditHeader(String.valueOf(aProduct.getId()), null, null,
				null, auditDetail, aProduct.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_ProductDialog,
					auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
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
		doShowNotes(this.product);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.product.getProductCode());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public ProductService getProductService() {
		return this.productService;
	}

	public void setProductListCtrl(ProductListCtrl productListCtrl) {
		this.productListCtrl = productListCtrl;
	}

	public ProductListCtrl getProductListCtrl() {
		return this.productListCtrl;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public void setOverideMap(
			HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public PagedListWrapper<ProductAsset> getProductAssetPagedListWrapper() {
		return productAssetPagedListWrapper;
	}

	@SuppressWarnings("unchecked")
	public void setProductAssetPagedListWrapper() {
		if (productAssetPagedListWrapper == null) {
			this.productAssetPagedListWrapper = (PagedListWrapper<ProductAsset>) SpringUtil
					.getBean("pagedListWrapper");
			;
		}
	}

	public List<ProductAsset> getProductAssetList() {
		return productAssetList;
	}

	public void setProductAssetList(List<ProductAsset> productAssetList) {
		this.productAssetList = productAssetList;
	}
}
