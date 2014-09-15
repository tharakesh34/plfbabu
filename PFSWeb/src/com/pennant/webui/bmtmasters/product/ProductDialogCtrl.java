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
 * FileName    		:  ProductDialogCtrl.java                                                   * 	  
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
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.model.rmtmasters.ProductAsset;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.bmtmasters.ProductService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.webui.rmtmasters.productAsset.model.ProductAssetListModelItemRenderer;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/Product/productDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ProductDialogCtrl extends GFCBaseListCtrl<ProductAsset> implements Serializable {

	private static final long serialVersionUID = -8421583705358772016L;
	private final static Logger logger = Logger.getLogger(ProductDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_ProductDialog; 	// autoWired
	
	protected Uppercasebox  productCode; 			// autoWired
	protected Textbox 		productDesc; 			// autoWired
	protected Grid 			grid_Basicdetails;		// autoWired
	
	protected Label 		recordStatus; 			// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	

	// not auto wired variables
	private Product product; 							// over handed per parameters
	private transient ProductListCtrl productListCtrl; // over handed per parameters

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String oldVar_productCode;
	private transient String oldVar_productDesc;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_ProductDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autoWired
	protected Button btnEdit; 	// autoWired
	protected Button btnDelete; // autoWired
	protected Button btnSave; 	// autoWired
	protected Button btnCancel; // autoWired
	protected Button btnClose; 	// autoWired
	protected Button btnHelp; 	// autoWired
	protected Button btnNotes;  // autoWired

	// ServiceDAOs / Domain Classes
	private transient ProductService productService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();

	// NEEDED for the ReUse in the SearchWindow

	int listRows;

	//Declaration of listHeaders in listBox of FeeTier
	protected Listheader listheader_ProductAsset_AssetCode;
	protected Listheader listheader_ProductAsset_AssetDesc;
	protected Listheader listheader_ProductAsset_AssetIsActive;
	protected Listheader listheader_ProductAsset_RecordStatus;
	protected Listheader listheader_ProductAsset_RecordType;

	// ProductAsset List
	protected Button 		btnNew_ProductAsset;
	protected Paging 		pagingProductAsset;
	protected Listbox 		listboxProductAsset;

	private List<ProductAsset> productAssetList = new ArrayList<ProductAsset>();
	private List<ProductAsset> oldVar_productAssetList = new ArrayList<ProductAsset>();
	private PagedListWrapper<ProductAsset> productAssetPagedListWrapper;

	/**
	 * default constructor.<br>
	 */
	public ProductDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Product object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ProductDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		setProductAssetPagedListWrapper();//Initialize pagedListwrapper for rendering ProductAsset list

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("product")) {
			this.product = (Product) args.get("product");
			Product befImage = new Product();
			BeanUtils.copyProperties(this.product, befImage);
			this.product.setBefImage(befImage);
			setProduct(this.product);
		} else {
			setProduct(null);
		}

		doLoadWorkFlow(this.product.isWorkflow(), this.product.getWorkflowId(),this.product.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(),"ProductDialog");
		}

		// READ OVERHANDED parameters !
		// we get the productListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete product here.
		if (args.containsKey("productListCtrl")) {
			setProductListCtrl((ProductListCtrl) args.get("productListCtrl"));
		} else {
			setProductListCtrl(null);
		}

		//Set the DialogController Height for listBox
		getBorderLayoutHeight();
		grid_Basicdetails.getRows().getVisibleItemCount();
		int dialogHeight =  grid_Basicdetails.getRows().getVisibleItemCount()* 20 + 100 +75; 
		int listboxHeight = borderLayoutHeight-dialogHeight;
		listboxProductAsset.setHeight(listboxHeight+"px");
		listRows = Math.round(listboxHeight/ 24)-1;

		this.listheader_ProductAsset_AssetCode.setSortAscending(new FieldComparator("assetCode", true));
		this.listheader_ProductAsset_AssetCode.setSortDescending(new FieldComparator("assetCode", false));
		this.listheader_ProductAsset_AssetDesc.setSortAscending(new FieldComparator("assetDesc", true));
		this.listheader_ProductAsset_AssetDesc.setSortDescending(new FieldComparator("assetDesc", false));
		this.listheader_ProductAsset_AssetIsActive.setSortAscending(new FieldComparator("assetIsActive", true));
		this.listheader_ProductAsset_AssetIsActive.setSortDescending(new FieldComparator("assetIsActive", false));

		if (isWorkFlowEnabled()){
			this.listheader_ProductAsset_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_ProductAsset_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_ProductAsset_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_ProductAsset_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_ProductAsset_RecordStatus.setVisible(false);
			this.listheader_ProductAsset_RecordType.setVisible(false);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getProduct());
		this.btnDelete.setVisible(false);

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

		getUserWorkspace().alocateAuthorities("ProductDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ProductDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ProductDialog_btnEdit"));
		this.btnDelete.setVisible(false);//getUserWorkspace().isAllowed("button_ProductDialog_btnDelete")
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ProductDialog_btnSave"));
		this.btnNew_ProductAsset.setVisible(getUserWorkspace().isAllowed("button_ProductDialog_btnNew_ProductAsset"));
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
	public void onClose$window_ProductDialog(Event event) throws Exception {
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
		// remember the old variables
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
		PTMessageUtils.showHelpWindow(event, window_ProductDialog);
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++++++++ GUI operations ++++++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,MultiLineMessageBox.QUESTION, true);

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
			closeDialog(this.window_ProductDialog, "Product");
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
	 * @param aProduct
	 *            Product
	 */
	public void doWriteBeanToComponents(Product aProduct) {
		logger.debug("Entering");
		this.productCode.setValue(aProduct.getProductCode());
		this.productDesc.setValue(aProduct.getProductDesc());
		this.recordStatus.setValue(aProduct.getRecordStatus());

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
	 * @throws InterruptedException
	 */
	public void doShowDialog(Product aProduct) throws InterruptedException {
		logger.debug("Entering");

		// if aProduct == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aProduct == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aProduct = getProductService().getNewProduct();
			setProduct(aProduct);
		} else {
			setProduct(aProduct);
		}

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
			// stores the initial data for comparing if they are changed during user action.
			doStoreInitValues();
			setDialog(this.window_ProductDialog);
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
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_productCode = this.productCode.getValue();
		this.oldVar_productDesc = this.productDesc.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		this.oldVar_productAssetList = this.productAssetList;
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.productCode.setValue(this.oldVar_productCode);
		this.productDesc.setValue(this.oldVar_productDesc);
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
		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_productCode != this.productCode.getValue()) {
			return true;
		}
		if (this.oldVar_productDesc != this.productDesc.getValue()) {
			return true;
		}
		if (this.oldVar_productAssetList != this.productAssetList) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (!this.productCode.isReadonly()) {
			this.productCode.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_ProductDialog_ProductCode.value") }));
		}
		if (!this.productDesc.isReadonly()) {
			this.productDesc.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_ProductDialog_ProductDesc.value") }));
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
	private void doClearMessage() {
		logger.debug("Entering");
		this.productCode.setErrorMessage("");
		this.productDesc.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for refreshing the list after successful updating
	 */ 
	private void refreshList(){
		logger.debug("Entering");
		final JdbcSearchObject<Product> soProduct = getProductListCtrl().getSearchObj();
		getProductListCtrl().pagingProductList.setActivePage(0);
		getProductListCtrl().getPagedListWrapper().setSearchObject(soProduct);
		if(getProductListCtrl().listBoxProduct!=null){
			getProductListCtrl().listBoxProduct.getListModel();
		}
		logger.debug("Leaving");
	} 
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")
		+ "\n\n --> " + aProduct.getProductCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			doWriteBeanToComponents(aProduct);

			if (StringUtils.trimToEmpty(aProduct.getRecordType()).equals("")) {
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
					closeDialog(this.window_ProductDialog, "Product");
				}

			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new Product object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old variables
		doStoreInitValues();
		final Product aProduct = getProductService().getNewProduct();
		aProduct.setNewRecord(true);
		setProduct(aProduct);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.productCode.focus();
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

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
			if (StringUtils.trimToEmpty(aProduct.getRecordType()).equals("")) {
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
			if(aProduct.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL) || 
					!(this.productAssetList == null || this.productAssetList.size()==0)){
				if(doProcess(aProduct,tranType)){
					refreshList();
					closeDialog(this.window_ProductDialog, "Product");
				}
			}else{
				PTMessageUtils.showErrorMessage(Labels.getLabel("List_Error",
						new String[]{Labels.getLabel("ProductAsset"),Labels.getLabel("Product")}));
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
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

		aProduct.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aProduct.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aProduct.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aProduct.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aProduct.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aProduct);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aProduct))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}

			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode = getWorkFlow().firstTask.owner;

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

			aProduct.setTaskId(taskId);
			aProduct.setNextTaskId(nextTaskId);
			aProduct.setRoleCode(getRole());
			aProduct.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aProduct, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aProduct);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aProduct,PennantConstants.TRAN_WF);
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
		logger.debug("return value :"+processCompleted);
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

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getProductService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getProductService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getProductService().doApprove(auditHeader);

						if (product.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getProductService().doReject(auditHeader);

						if (product.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_ProductDialog, auditHeader);
						return processCompleted;

					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ProductDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
				}

				if (deleteNotes) {
					deleteNotes(getNotes(), true);
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

		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++ New Button & Double Click Events for Product Asset List+++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void onClick$btnNew_ProductAsset(Event event) throws Exception {
		logger.debug("Entering");

		if(StringUtils.trimToNull(this.productCode.getValue())!= null){

			final ProductAsset aProductAsset =getProductService().getNewProductAsset();
			aProductAsset.setNewRecord(true);
			aProductAsset.setProductCode(getProduct().getProductCode());

			final HashMap<String, Object> map = new HashMap<String, Object>();
			selectNewProdAssetBtn();
			aProductAsset.setProductCode(this.productCode.getValue());

			map.put("productAsset", aProductAsset);
			map.put("productDialogCtrl", this);
			map.put("roleCode", getRole());

			try {
				Executions.createComponents("/WEB-INF/pages/RMTMasters/ProductAsset/ProductAssetDialog.zul",window_ProductDialog,map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
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
			final ProductAsset productAsset = (ProductAsset) item.getAttribute("data");

			if (productAsset.getRecordStatus().equalsIgnoreCase(
					PennantConstants.RECORD_TYPE_DEL)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			}else if(StringUtils.trimToEmpty(this.productCode.getValue()).equals("")){
				PTMessageUtils.showErrorMessage(Labels.getLabel("FIELD_NO_EMPTY", 
						new String[]{PennantJavaUtil.getLabel("label_ProductAssetDialog_ProductCode.value")}));
			}else {
				productAsset.setProductCode(this.productCode.getValue());
				productAsset.setNewRecord(false);
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("productAsset", productAsset);
				map.put("productDialogCtrl", this);
				map.put("roleCode", getRole());

				// call the ZUL-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/RMTMasters/ProductAsset/ProductAssetDialog.zul",window_ProductDialog,map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
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
	public void onBlur$productCode(Event event){
		logger.debug("Entering" + event.toString());
		selectNewProdAssetBtn();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Allow Adding ProductAsset List by button as Visible/not
	 */
	private void selectNewProdAssetBtn(){
		logger.debug("Entering");

		this.btnNew_ProductAsset.setVisible(false);
		if(!(this.productCode.getValue() ==null || this.productCode.getValue().equals(""))){
			this.btnNew_ProductAsset.setVisible(getUserWorkspace().isAllowed("button_ProductDialog_btnNew_ProductAsset"));
			//this.productCode.setReadonly(true);
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++ Product Asset Lists Refreshing ++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Generate the Product Asset Details List in the ProductDialogCtrl and
	 * set the list in the listboxProductAsset listBox by using Pagination
	 */
	public void doFillProductAsset(List<ProductAsset> productAssets) {
		logger.debug("Entering");
		if(productAssets!= null){
			setProductAssetList(productAssets);
			this.pagingProductAsset.setPageSize(listRows);
			this.pagingProductAsset.setDetailed(true);
			getProductAssetPagedListWrapper().initList(productAssetList,this.listboxProductAsset, this.pagingProductAsset);
			this.listboxProductAsset.setItemRenderer(new ProductAssetListModelItemRenderer());
		}

		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
		AuditDetail auditDetail = new AuditDetail(tranType, 1,aProduct.getBefImage(), aProduct);
		return new AuditHeader(String.valueOf(aProduct.getId()), null, null,
				null, auditDetail, aProduct.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_ProductDialog,auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
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
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

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

	// To get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("Product");
		notes.setReference(getProduct().getProductCode());
		notes.setVersion(getProduct().getVersion());
		logger.debug("Leaving");
		return notes;
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

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}
	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public PagedListWrapper<ProductAsset> getProductAssetPagedListWrapper() {
		return productAssetPagedListWrapper;
	}
	@SuppressWarnings("unchecked")
	public void setProductAssetPagedListWrapper() {
		if(productAssetPagedListWrapper == null){
			this.productAssetPagedListWrapper = (PagedListWrapper<ProductAsset>) SpringUtil.getBean("pagedListWrapper");;
		}
	}

	public List<ProductAsset> getProductAssetList() {
		return productAssetList;
	}
	public void setProductAssetList(List<ProductAsset> productAssetList) {
		this.productAssetList = productAssetList;
	}
}
