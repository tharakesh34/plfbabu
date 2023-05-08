
/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : ExtendedFieldDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 28-12-2011 * *
 * Modified Date : 28-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 08-03-2011 PENNANT TECHONOLOGIES 0.1 * * * 08-05-2019 Srinivasa Varma 0.2 Development Iteam 81 * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.solutionfactory.extendedfielddetail;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
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
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ExtendedFieldDialogCtrl extends GFCBaseCtrl<ExtendedFieldDetail> {
	private static final long serialVersionUID = -3249715883200188080L;
	private static final Logger logger = LogManager.getLogger(ExtendedFieldDialogCtrl.class);

	protected Window window_ExtendedFieldDialog;
	protected Label moduleDesc;
	protected Label subModuleDesc;
	protected Longbox moduleId;
	protected Textbox tabHeading;
	protected Radiogroup numberOfColumns;
	protected Radio radio_column1;
	protected Radio radio_column2;
	protected Radio radio_column3;
	protected Grid grid_basicDetails;

	private transient boolean validationOn;

	private ExtendedFieldDetail extendedFieldDetail;
	private ExtendedFieldHeader extendedFieldHeader;
	private boolean newRecord = false;

	protected Button btnNew_FieldDet;
	protected Paging pagingFieldDetList;
	protected Listbox listBoxFieldDet;
	protected Component parentTabPanel = null;
	protected Div toolbar = null;
	protected Object dialogCtrl = null;
	protected boolean firstTaskRole = false;
	protected int maxSeqNo = 0;

	private List<ExtendedFieldDetail> extendedFieldDetailsList = new ArrayList<ExtendedFieldDetail>();
	private PagedListWrapper<ExtendedFieldDetail> extendedFieldPagedListWrapper;

	private boolean isMarketableSecurities = false;

	public ExtendedFieldDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ExtendedFieldDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected ExtendedFieldDetail object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_ExtendedFieldDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ExtendedFieldDialog);
		try {
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

			if (arguments.containsKey("isMarketableSecurities")) {
				this.isMarketableSecurities = (boolean) arguments.get("isMarketableSecurities");
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

			boolean actionRenderReq = true;
			if (arguments.containsKey("dialogCtrl")) {
				this.dialogCtrl = (Object) arguments.get("dialogCtrl");
				try {
					dialogCtrl.getClass().getMethod("setExtendedFieldDialogCtrl", this.getClass()).invoke(dialogCtrl,
							this);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				// this.extendedFieldHeader.setWorkflowId(0);
				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				if (arguments.containsKey("moduleName")) {
					this.extendedFieldHeader.setModuleName((String) arguments.get("moduleName"));
				}
				if (arguments.containsKey("firstTaskRole")) {
					this.firstTaskRole = (boolean) arguments.get("firstTaskRole");
				}
				actionRenderReq = false;
				getUserWorkspace().allocateRoleAuthorities(getRole(), "ExtendedFieldDialog");
			}

			doLoadWorkFlow(this.extendedFieldHeader.isWorkflow(), this.extendedFieldHeader.getWorkflowId(),
					this.extendedFieldHeader.getNextTaskId());

			if (isWorkFlowEnabled() && actionRenderReq) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "ExtendedFieldDialog");
			}

			this.listBoxFieldDet
					.setHeight(getListBoxHeight(this.grid_basicDetails.getRows().getVisibleItemCount() + 6));
			this.pagingFieldDetList.setPageSize(getListRows() + 1);
			this.pagingFieldDetList.setDetailed(true);

			// set Field Properties
			doSetFieldProperties();

			/* set components visible dependent of the users rights */
			doCheckRights();

			doShowDialog(getExtendedFieldHeader());
		} catch (Exception e) {
			MessageUtil.showError(e);
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

		getUserWorkspace().allocateAuthorities("ExtendedFieldDialog", getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDialog_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDialog_btnSave"));
		this.btnDelete.setVisible(false);
		this.btnCancel.setVisible(false);
		this.btnNew_FieldDet.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldDialog_FD_btnNew"));

		logger.debug("Leaving");
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
	 * @param event An event sent to the event handler of a component.
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
	 * @param aExtendedFieldDetail ExtendedFieldDetail
	 */
	public void doWriteBeanToComponents(ExtendedFieldHeader aExtendedFieldHeader) {
		logger.debug("Entering");

		this.moduleId.setValue(aExtendedFieldHeader.getModuleId());
		if (parentTabPanel != null) {
			this.moduleDesc.setValue(aExtendedFieldHeader.getModuleName());
			this.subModuleDesc.setValue(aExtendedFieldHeader.getSubModuleName());
		} else {
			this.moduleDesc.setValue(Labels.getLabel("label_ExtendedField_" + aExtendedFieldHeader.getModuleName()));
			this.subModuleDesc
					.setValue(Labels.getLabel("label_ExtendedField_" + aExtendedFieldHeader.getSubModuleName()));
		}
		this.tabHeading.setValue(aExtendedFieldHeader.getTabHeading());
		for (int i = 0; i < numberOfColumns.getItemCount(); i++) {
			if (this.numberOfColumns.getItemAtIndex(i).getValue()
					.equals(aExtendedFieldHeader.getNumberOfColumns() == null ? ""
							: aExtendedFieldHeader.getNumberOfColumns().trim())) {
				this.numberOfColumns.setSelectedIndex(i);
			}
		}

		// Adding Default Columns for Extended field Detail List (Number of units & Unit Price)
		if (aExtendedFieldHeader.isNewRecord()) {

			aExtendedFieldHeader.setExtendedFieldDetails(this.extendedFieldDetailsList);

			if (aExtendedFieldHeader.getExtendedFieldDetails() == null
					|| aExtendedFieldHeader.getExtendedFieldDetails().isEmpty()) {
				aExtendedFieldHeader.setExtendedFieldDetails(new ArrayList<ExtendedFieldDetail>());

				if (StringUtils.equals(aExtendedFieldHeader.getModuleName(), CollateralConstants.MODULE_NAME)
						|| StringUtils.equals(aExtendedFieldHeader.getModuleName(),
								AssetConstants.EXTENDEDFIELDS_MODULE)) {
					// TODO: Modify dynamic from static

					ExtendedFieldDetail unitCount = new ExtendedFieldDetail();
					unitCount.setFieldName("NOOFUNITS");
					unitCount.setFieldLabel("Number of Units");
					unitCount.setFieldType(ExtendedFieldConstants.FIELDTYPE_INT);
					unitCount.setFieldLength(3);
					unitCount.setFieldSeqOrder(10);
					unitCount.setFieldMandatory(true);
					unitCount.setRecordType(PennantConstants.RCD_ADD);
					unitCount.setVersion(1);
					unitCount.setInputElement(true);
					unitCount.setEditable(true);

					ExtendedFieldDetail unitPrice = new ExtendedFieldDetail();
					unitPrice.setFieldName("UNITPRICE");
					unitPrice.setFieldLabel("Unit Price");
					unitPrice.setFieldType(ExtendedFieldConstants.FIELDTYPE_AMOUNT);
					unitPrice.setFieldLength(18);
					unitPrice.setFieldSeqOrder(20);
					unitPrice.setFieldMandatory(true);
					unitPrice.setRecordType(PennantConstants.RCD_ADD);
					unitPrice.setVersion(1);
					unitPrice.setInputElement(true);
					unitPrice.setEditable(true);

					if (this.isMarketableSecurities) {
						ExtendedFieldDetail hsnCode = new ExtendedFieldDetail();
						hsnCode.setFieldName("HSNCODE");
						hsnCode.setFieldLabel("HSN / ISIN Code");
						hsnCode.setFieldType(ExtendedFieldConstants.FIELDTYPE_EXTENDEDCOMBO);
						hsnCode.setFieldLength(20);
						hsnCode.setFieldSeqOrder(50);
						hsnCode.setFieldMandatory(true);
						hsnCode.setRecordType(PennantConstants.RCD_ADD);
						hsnCode.setVersion(1);
						hsnCode.setInputElement(true);
						hsnCode.setFieldList("HSNCodeData");
						hsnCode.setFieldConstraint("HSNCode");
						hsnCode.setEditable(true);
						aExtendedFieldHeader.getExtendedFieldDetails().add(hsnCode);
					}

					aExtendedFieldHeader.getExtendedFieldDetails().add(unitPrice);
					aExtendedFieldHeader.getExtendedFieldDetails().add(unitCount);

				}
			}
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

		// adding the list to bean
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
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aExtendedFieldDetail
	 */
	public void doShowDialog(ExtendedFieldHeader aExtendedFieldHeader) {
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
			// fill thwe components with the data
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
		// TODO
		/*
		 * this.radio_column1.setDisabled(isReadOnly("ExtendedFieldDialog_tabHeading"));
		 * this.radio_column2.setDisabled(isReadOnly("ExtendedFieldDialog_tabHeading"));
		 */

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

	public void onClick$btnNotes(Event event) {
		doShowNotes(this.extendedFieldHeader);
	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.extendedFieldHeader.getModuleId());
	}

	@Override
	protected void doClearMessage() {
		this.tabHeading.setErrorMessage("");
	}

	public void onClick$btnNew_FieldDet(Event event) {
		logger.debug("Entering" + event.toString());

		ExtendedFieldDetail aExtendedFieldDetail = new ExtendedFieldDetail();
		aExtendedFieldDetail.setModuleId(this.moduleId.intValue());
		aExtendedFieldDetail.setNewRecord(true);
		aExtendedFieldDetail.setWorkflowId(0);

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("extendedFieldDetail", aExtendedFieldDetail);
		map.put("extendedFieldDialogCtrl", this);
		map.put("newRecord", true);
		map.put("maxSeqNo", maxSeqNo);
		map.put("roleCode", getRole());
		// ### 08-05-2018 Start Development Iteam 81
		map.put("moduleDesc", getExtendedFieldHeader().getModuleName());
		map.put("module", moduleDesc.getValue());
		map.put("subModuleDesc", getExtendedFieldHeader().getSubModuleName());
		// ### 08-05-2018 End Development Iteam 81
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldDetailDialog.zul",
					window_ExtendedFieldDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onExtendedFieldItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxFieldDet.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final ExtendedFieldDetail extendedFieldDetail = (ExtendedFieldDetail) item.getAttribute("data");

			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("extendedFieldDetail", extendedFieldDetail);
			map.put("extendedFieldDialogCtrl", this);
			map.put("roleCode", getRole());
			map.put("firstTaskRole", this.firstTaskRole);
			map.put("layoutDesign", numberOfColumns.getSelectedItem().getValue());
			// ### 08-05-2018 Start Development Iteam 81
			map.put("moduleDesc", getExtendedFieldHeader().getModuleName());
			map.put("module", moduleDesc.getValue());
			map.put("subModuleDesc", getExtendedFieldHeader().getSubModuleName());
			// ### 08-05-2018 End Development Iteam 81
			// call the zul-file with the parameters packed in a map

			if (!extendedFieldDetail.isVisible()) {
				MessageUtil.showMessage(Labels.getLabel("info.ExtendedDetail_not_editable"));
				return;
			}
			try {
				Executions.createComponents(
						"/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldDetailDialog.zul",
						window_ExtendedFieldDialog, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}

		}
		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ********* Field Detail List **********//
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
		this.extendedFieldHeader.setExtendedFieldDetails(this.extendedFieldDetailsList);
		getExtendedFieldPagedListWrapper().initList(this.extendedFieldDetailsList, listBoxFieldDet, pagingFieldDetList);
		this.listBoxFieldDet.setItemRenderer(new ExtendedFieldListItemRenderer());

		// Details of Fields for Pre & Post validations
		List<String> fieldNameList = new ArrayList<>();
		for (int i = 0; i < this.extendedFieldDetailsList.size(); i++) {

			if (maxSeqNo < extendedFieldDetailsList.get(i).getFieldSeqOrder()) {
				maxSeqNo = extendedFieldDetailsList.get(i).getFieldSeqOrder();
			}

			if (!StringUtils.equals(this.extendedFieldDetailsList.get(i).getRecordType(),
					PennantConstants.RECORD_TYPE_DEL)
					&& !StringUtils.equals(this.extendedFieldDetailsList.get(i).getRecordType(),
							PennantConstants.RECORD_TYPE_CAN)) {
				fieldNameList.add(this.extendedFieldDetailsList.get(i).getFieldName());
			}
		}

		if (this.dialogCtrl != null) {
			try {
				dialogCtrl.getClass().getMethod("setFieldNames", List.class).invoke(dialogCtrl, fieldNameList);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		}
		logger.debug("Leaving");
	}

	// Getting the table name from map
	private void setTableName(List<ExtendedFieldDetail> extendedFieldDetails) {
		for (ExtendedFieldDetail efd : extendedFieldDetails) {
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
			tableName = tableName.concat("_").concat(getExtendedFieldHeader().getSubModuleName());

			if (getExtendedFieldHeader().getEvent() != null) {
				tableName = tableName.concat("_")
						.concat(PennantStaticListUtil.getFinEventCode(getExtendedFieldHeader().getEvent()));
			}
			tableName = tableName.concat("_ED");
			efd.setLovDescTableName(tableName);// PennantStaticListUtil.getModuleName(efd)
		}
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
		    super();
		}

		@Override
		public void render(Listitem item, ExtendedFieldDetail detail, int count) {

			Listcell lc;
			lc = new Listcell(detail.getFieldName());
			lc.setParent(item);
			lc = new Listcell(detail.getFieldLabel());
			lc.setParent(item);
			lc = new Listcell(
					PennantApplicationUtil.getLabelDesc(detail.getFieldType(), PennantStaticListUtil.getFieldType()));
			lc.setParent(item);
			lc = new Listcell(String.valueOf(detail.getFieldSeqOrder()));
			lc.setParent(item);
			lc = new Listcell(detail.getParentTag());
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

			lc = new Listcell();
			Checkbox maintAlwd = new Checkbox();
			maintAlwd.setChecked(detail.isMaintAlwd());
			maintAlwd.setDisabled(true);
			lc.appendChild(maintAlwd);
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
		extendedFieldHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		extendedFieldHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		extendedFieldHeader.setUserDetails(getUserWorkspace().getLoggedInUser());
		logger.debug("Leaving");
		return extendedFieldHeader;
	}

	/**
	 * Method for Setting Basic Details on header
	 * 
	 * @param module
	 * @param subModule
	 */
	public void doSetBasicDetail(String module, String subModule, String subModuledesc) {
		this.moduleDesc.setValue(module);
		this.subModuleDesc.setValue(subModule);
		if (StringUtils.isNotEmpty(subModuledesc)) {
			this.subModuleDesc.setValue(subModule + " - " + subModuledesc);
		}
		if (StringUtils.trimToNull(this.tabHeading.getValue()) == null) {
			this.tabHeading.setValue(subModuledesc);
		}
	}

	public void doSetList(boolean isMarketableSecurities, ExtendedFieldHeader extendedFieldHeader) {
		this.isMarketableSecurities = isMarketableSecurities;
		if (isMarketableSecurities) {
			extendedFieldHeader.setNumberOfColumns("3");
		} else {
			extendedFieldHeader.setNumberOfColumns("2");
		}
		doWriteBeanToComponents(extendedFieldHeader);
	}
}
