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
 * * FileName : ProvinceDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * * Modified
 * Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.systemmasters.province;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.TaxDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.systemmasters.ProvinceService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennapps.core.util.ObjectUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SystemMaster/Province/provinceDialog.zul file.
 */
public class ProvinceDialogCtrl extends GFCBaseCtrl<Province> {
	private static final long serialVersionUID = 8900134469414443671L;
	private static final Logger logger = LogManager.getLogger(ProvinceDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ProvinceDialog;
	protected ExtendedCombobox cPCountry;
	protected Uppercasebox cPProvince;
	protected Textbox cPProvinceName;
	protected Checkbox systemDefault;
	protected Textbox bankRefNo;
	protected Checkbox cPIsActive; // autoWired
	protected Checkbox taxExempted; // autoWired
	protected Checkbox unionTerritory; // autoWired
	protected Textbox taxStateCode; // autoWired
	protected Checkbox taxAvailable; // autoWired
	protected Textbox businessArea; // autoWired
	protected Tab tab_gstdetails; // autoWired
	protected Tab tab_basicDetails; // autoWired
	protected Groupbox gb_basicDetails; // autoWired
	protected Listbox listBoxTaxDetails; // autoWired
	private List<TaxDetail> taxMappingDetailList = null;
	private String old_BusineesArea = "";

	// not auto wired variables
	private Province province; // overHanded per parameter
	private transient ProvinceListCtrl provinceListCtrl; // overHanded per
															// parameter
	private List<TaxDetail> taxDetailList = new ArrayList<TaxDetail>();

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient ProvinceService provinceService;
	private Country sysDefaultCountry;
	protected Button btnNew_gstDetails;
	protected Row row_taxAvailable;
	protected Space space_taxStateCode;
	protected Space space_businessArea;

	/**
	 * default constructor.<br>
	 */
	public ProvinceDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ProvinceDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Province object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_ProvinceDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ProvinceDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("province")) {
				this.province = (Province) arguments.get("province");
				Province befImage = new Province();
				BeanUtils.copyProperties(this.province, befImage);
				this.province.setBefImage(befImage);

				setProvince(this.province);
			} else {
				setProvince(null);
			}

			doLoadWorkFlow(this.province.isWorkflow(), this.province.getWorkflowId(), this.province.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "ProvinceDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the provinceListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete province here.
			ComponentsCtrl.applyForward(tab_gstdetails, ("onSelect=" + "onSelectTab"));
			if (arguments.containsKey("provinceListCtrl")) {
				setProvinceListCtrl((ProvinceListCtrl) arguments.get("provinceListCtrl"));
			} else {
				setProvinceListCtrl(null);
			}
			setCountrySystemDefault();
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getProvince());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ProvinceDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.cPCountry.setMaxlength(2);
		this.cPProvince.setMaxlength(8);
		this.cPProvinceName.setMaxlength(50);
		this.bankRefNo.setMaxlength(20);
		this.taxStateCode.setMaxlength(2);
		this.businessArea.setMaxlength(100);

		this.cPCountry.setMandatoryStyle(true);
		this.cPCountry.setModuleName("Country");
		this.cPCountry.setValueColumn("CountryCode");
		this.cPCountry.setDescColumn("CountryDesc");
		this.cPCountry.setValidateColumns(new String[] { "CountryCode" });

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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ProvinceDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ProvinceDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ProvinceDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ProvinceDialog_btnSave"));
		this.btnNew_gstDetails.setVisible(getUserWorkspace().isAllowed("button_ProvinceDialog_btnNew_gstDetails"));

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
		doCheckSystemDefault();
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
		MessageUtil.showHelpWindow(event, window_ProvinceDialog);
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
		doWriteBeanToComponents(this.province.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aProvince Province
	 */
	public void doWriteBeanToComponents(Province aProvince) {
		logger.debug("Entering");
		this.cPCountry.setValue(aProvince.getCPCountry());
		this.cPProvince.setValue(aProvince.getCPProvince());
		this.cPProvinceName.setValue(aProvince.getCPProvinceName());
		this.systemDefault.setChecked(aProvince.isSystemDefault());
		this.bankRefNo.setValue(aProvince.getBankRefNo());
		this.cPIsActive.setChecked(aProvince.iscPIsActive());
		this.taxExempted.setChecked(aProvince.isTaxExempted());
		this.unionTerritory.setChecked(aProvince.isUnionTerritory());
		this.taxStateCode.setValue(aProvince.getTaxStateCode());
		this.taxAvailable.setChecked(aProvince.isTaxAvailable());
		this.businessArea.setValue(aProvince.getBusinessArea());

		old_BusineesArea = aProvince.getBusinessArea();

		// Reneder GSTIN Mapping
		doFillGSTINMappingDetails(aProvince.getTaxDetailList());

		if (aProvince.isNewRecord()) {
			this.cPCountry.setDescription("");
			this.tab_gstdetails.setVisible(false);
		} else {
			this.cPCountry.setDescription(aProvince.getLovDescCPCountryName());
			this.tab_gstdetails.setVisible(true);
			this.listBoxTaxDetails.setHeight(this.borderLayoutHeight - 125 + "px");
		}
		if (aProvince.isNewRecord() || (aProvince.getRecordType() != null ? aProvince.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.cPIsActive.setChecked(true);
			this.cPIsActive.setDisabled(true);
		}

		checkTaxAvailable();
		checkTaxExempted();

		this.recordStatus.setValue(aProvince.getRecordStatus());
		logger.debug("Leaving");
	}

	// Mapping details like new button , list diaplay and double click.....

	// Reneder the list
	public void doFillGSTINMappingDetails(List<TaxDetail> taxMappingDetailList) {
		logger.debug("Entering");

		this.listBoxTaxDetails.getItems().clear();
		setTaxDetailList(taxMappingDetailList);

		if (taxMappingDetailList != null && !taxMappingDetailList.isEmpty()) {
			for (TaxDetail taxMappingDetail : taxMappingDetailList) {
				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(taxMappingDetail.getCountryName());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getProvinceName());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getEntityDesc());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getTaxCode());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getPinCode());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getCityName());
				lc.setParent(item);
				lc = new Listcell(taxMappingDetail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(taxMappingDetail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", taxMappingDetail);

				ComponentsCtrl.applyForward(item, "onDoubleClick=onTaxDetailItemDoubleClicked");
				this.listBoxTaxDetails.appendChild(item);
			}
		}

		logger.debug("Leaving");
	}

	// Double click GSTIN Mapping Details list
	public void onTaxDetailItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());

		final Listitem item = this.listBoxTaxDetails.getSelectedItem();
		if (item != null) {
			final TaxDetail taxDetail = (TaxDetail) item.getAttribute("data");

			if (!StringUtils.trimToEmpty(taxDetail.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
				taxDetail.setNewRecord(false);
			}

			if (StringUtils.equalsIgnoreCase(taxDetail.getRecordType(), PennantConstants.RECORD_TYPE_CAN)
					|| StringUtils.equalsIgnoreCase(taxDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				doShowDialogPage(taxDetail);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aProvince
	 */
	public void doWriteComponentsToBean(Province aProvince) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aProvince.setLovDescCPCountryName(this.cPCountry.getDescription());
			aProvince.setCPCountry(this.cPCountry.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProvince.setCPProvince(this.cPProvince.getValue().toUpperCase());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProvince.setCPProvinceName(this.cPProvinceName.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aProvince.setSystemDefault(this.systemDefault.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProvince.setBankRefNo(this.bankRefNo.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProvince.setcPIsActive(this.cPIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProvince.setTaxExempted(this.taxExempted.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProvince.setUnionTerritory(this.unionTerritory.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.taxExempted.isChecked()) {
				aProvince.setTaxStateCode(this.taxStateCode.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aProvince.setTaxAvailable(this.taxAvailable.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.taxExempted.isChecked()) {
				aProvince.setBusinessArea(this.businessArea.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		aProvince.setTaxDetailList(ObjectUtil.clone(getTaxDetailList()));
		doRemoveValidation();

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			this.tab_basicDetails.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (comp instanceof HtmlBasedComponent) {
						Clients.scrollIntoView(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}

		aProvince.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onSelectTab(Event event) {
		doSetValidation();
		doWriteComponentsToBean(getProvince());
		if (this.cPCountry.getValue() != null && this.cPProvince.getValue() != null && this.taxAvailable.isChecked()) {
			this.btnNew_gstDetails.setVisible(getUserWorkspace().isAllowed("button_ProvinceDialog_btnNew_gstDetails"));
		} else {
			this.btnNew_gstDetails.setVisible(false);
		}
	}

	public void onClick$btnNew_gstDetails(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		TaxDetail taxdetail = new TaxDetail();
		taxdetail.setNewRecord(true);
		taxdetail.setWorkflowId(getWorkFlowId());
		taxdetail.setCountry(this.cPCountry.getValue());
		taxdetail.setCountryName(this.cPCountry.getDescription());
		taxdetail.setStateCode(this.cPProvince.getValue());
		taxdetail.setProvinceName(this.cPProvinceName.getValue());

		// Display the dialog page.
		doShowDialogPage(taxdetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param taxdetail The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(TaxDetail taxdetail) {
		logger.debug(Literal.ENTERING);

		boolean rightsAvailable = getUserWorkspace().isAllowed("button_ProvinceDialog_btnNew_gstDetails");
		Map<String, Object> arg = new HashMap<String, Object>();
		arg.put("moduleCode", moduleCode);
		arg.put("enqiryModule", !rightsAvailable);
		arg.put("taxdetail", taxdetail);
		arg.put("provinceDialogCtrl", this);
		arg.put("roleCode", getRole());
		arg.put("taxStateCode", this.taxStateCode.getValue());

		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/TaxDetail/TaxDetailDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aProvince
	 */
	public void doShowDialog(Province aProvince) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aProvince.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.cPCountry.focus();
		} else {
			this.cPProvinceName.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aProvince.getRecordType())) {
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
			doWriteBeanToComponents(aProvince);

			if (aProvince.isNewRecord() || isWorkFlowEnabled()) {
				doCheckSystemDefault();
			}

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ProvinceDialog.onClose();
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

		if (!this.cPProvince.isReadonly()) {
			this.cPProvince
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ProvinceDialog_CPProvince.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.cPProvinceName.isReadonly()) {
			this.cPProvinceName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ProvinceDialog_CPProvinceName.value"),
							PennantRegularExpressions.REGEX_NAME, true));
		}
		if (!this.cPCountry.isReadonly()) {
			this.cPCountry.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ProvinceDialog_CPCountry.value"), null, true, true));
		}
		if (!this.bankRefNo.isReadonly()) {
			this.bankRefNo.setConstraint(new PTStringValidator(Labels.getLabel("label_ProvinceDialog_BankRefNo.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_CODE, false));
		}
		if (!this.taxExempted.isChecked()) {
			if (!this.taxStateCode.isReadonly()) {
				this.taxStateCode
						.setConstraint(new PTStringValidator(Labels.getLabel("label_ProvinceDialog_TaxStateCode.value"),
								PennantRegularExpressions.REGEX_ALPHANUM, true, 2, 2));
			}
		}
		if (!this.taxExempted.isChecked()) {
			if (!this.businessArea.isReadonly()) {
				this.businessArea.setConstraint(
						new PTStringValidator(Labels.getLabel("label_ProvinceDialog_BusinessArea.value"), null, true));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.cPProvince.setConstraint("");
		this.cPProvinceName.setConstraint("");
		this.cPCountry.setConstraint("");
		this.bankRefNo.setConstraint("");
		this.taxStateCode.setConstraint("");
		this.businessArea.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");

		this.cPProvince.setErrorMessage("");
		this.cPProvinceName.setErrorMessage("");
		this.cPCountry.setErrorMessage("");
		this.bankRefNo.setErrorMessage("");
		this.taxStateCode.setErrorMessage("");
		this.businessArea.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getProvinceListCtrl().search();
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final Province aProvince = new Province();
		BeanUtils.copyProperties(getProvince(), aProvince);

		String keyReference = Labels.getLabel("label_ProvinceDialog_CPCountry.value") + " : " + aProvince.getCPCountry()
				+ "," + Labels.getLabel("label_ProvinceDialog_CPProvince.value") + " : " + aProvince.getCPProvince();

		doDelete(keyReference, aProvince);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getProvince().isNewRecord()) {
			this.cPCountry.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.cPProvince.setReadonly(false);
			this.cPProvinceName.setReadonly(false);
			this.taxStateCode.setReadonly(isReadOnly("ProvinceDialog_taxStateCode"));
			// this.taxAvailable.setDisabled(true);
		} else {
			this.cPCountry.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.cPProvince.setReadonly(true);
			this.cPProvinceName.setReadonly(true);
			this.taxStateCode.setReadonly(true);
			// this.taxAvailable.setDisabled(isReadOnly("ProvinceDialog_taxAvailable"));
		}

		this.bankRefNo.setReadonly(isReadOnly("ProvinceDialog_BankRefNo"));
		this.cPIsActive.setDisabled(isReadOnly("ProvinceDialog_CPIsActive"));
		this.taxExempted.setDisabled(isReadOnly("ProvinceDialog_taxExempted"));
		this.unionTerritory.setDisabled(isReadOnly("ProvinceDialog_unionTerritory"));
		this.businessArea.setReadonly(isReadOnly("ProvinceDialog_businessArea"));

		if (StringUtils.equals(PennantConstants.RCD_STATUS_APPROVED, this.province.getRecordStatus())) {
			this.taxAvailable.setDisabled(isReadOnly("ProvinceDialog_taxAvailable"));
		} else {
			this.taxAvailable.setDisabled(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.province.isNewRecord()) {
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
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.cPCountry.setReadonly(true);
		this.cPProvince.setReadonly(true);
		this.cPProvinceName.setReadonly(true);
		this.bankRefNo.setReadonly(true);
		this.systemDefault.setDisabled(true);
		this.cPIsActive.setDisabled(true);
		this.taxExempted.setDisabled(true);
		this.unionTerritory.setDisabled(true);
		this.taxStateCode.setDisabled(true);
		this.businessArea.setDisabled(true);
		this.taxAvailable.setDisabled(true);

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
		this.cPCountry.setValue("");
		this.cPCountry.setDescription("");
		this.cPProvince.setValue("");
		this.cPProvinceName.setValue("");
		this.bankRefNo.setValue("");
		this.cPIsActive.setChecked(false);
		this.taxExempted.setChecked(false);
		this.unionTerritory.setChecked(false);
		this.taxStateCode.setValue("");
		this.taxAvailable.setChecked(false);
		this.businessArea.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final Province aProvince = new Province();
		BeanUtils.copyProperties(getProvince(), aProvince);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		String recordStatus = userAction.getSelectedItem().getValue();

		if (!StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_CANCELLED)
				&& !StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_REJECTED)
				&& !StringUtils.equals(aProvince.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
			doSetValidation();
		}
		// fill the Province object with the components data
		doWriteComponentsToBean(aProvince);

		if (this.userAction.getSelectedItem() != null
				&& ("save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
						|| this.userAction.getSelectedItem().getLabel().contains("Submit"))) {

			if (!StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_CANCELLED)
					&& !StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_REJECTED)
					&& !StringUtils.equals(aProvince.getRecordType(), PennantConstants.RECORD_TYPE_DEL)
					&& this.listBoxTaxDetails.getItems().size() <= 0 && this.row_taxAvailable.isVisible()
					&& this.taxAvailable.isChecked()) {
				MessageUtil.showError(Labels.getLabel("label_GstinMap"));
				return;
			} else if (this.taxAvailable.isChecked() && getTaxDetailList() != null) {
				int count = 0;
				for (TaxDetail taxDet : getTaxDetailList()) {
					if (!StringUtils.equals(taxDet.getRecordType(), PennantConstants.RECORD_TYPE_CAN)
							&& !StringUtils.equals(taxDet.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
						count++;
					}
				}
				if (!aProvince.isNewRecord()
						&& StringUtils.equals(PennantConstants.RCD_STATUS_APPROVED, this.province.getRecordStatus())) {
					if (count == 0) {
						MessageUtil.showError(Labels.getLabel("label_GstinMap"));
						return;
					} else if (count > 1) {
						MessageUtil.showError(Labels.getLabel("label_Max_GstinMap"));
						return;
					}
				}
			}
		}

		// Write the additional validations as per below example get the
		// selected branch object from the listbox Do data
		// level validations here

		isNew = aProvince.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aProvince.getRecordType())) {
				aProvince.setVersion(aProvince.getVersion() + 1);
				if (isNew) {
					aProvince.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aProvince.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aProvince.setNewRecord(true);
				}
			}
		} else {
			aProvince.setVersion(aProvince.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aProvince, tranType)) {
				refreshList();
				// Close the Existing Dialog
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
	 * @param aProvince (Province)
	 * 
	 * @param tranType  (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(Province aProvince, String tranType) {
		logger.debug("Leaving");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aProvince.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aProvince.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aProvince.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aProvince.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aProvince.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aProvince);
				}

				if (isNotesMandatory(taskId, aProvince)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (!StringUtils.isBlank(nextTaskId)) {
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

			aProvince.setTaskId(taskId);
			aProvince.setNextTaskId(nextTaskId);
			aProvince.setRoleCode(getRole());
			aProvince.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aProvince, tranType);

			String operationRefs = getServiceOperations(taskId, aProvince);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aProvince, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aProvince, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Province aProvince = (Province) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getProvinceService().delete(auditHeader);

					deleteNotes = true;
				} else {
					auditHeader = getProvinceService().saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getProvinceService().doApprove(auditHeader);

					if (aProvince.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getProvinceService().doReject(auditHeader);
					if (aProvince.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ProvinceDialog, auditHeader);
					logger.debug("Leaving");
					return processCompleted;
				}
			}

			retValue = ErrorControl.showErrorControl(this.window_ProvinceDialog, auditHeader);

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.province), true);
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
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Details

	/**
	 * Get Audit Header Details
	 * 
	 * @param aProvince (Province)
	 * @param tranType  (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(Province aProvince, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aProvince.getBefImage(), aProvince);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aProvince.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.province);
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getProvince().getCPCountry() + PennantConstants.KEY_SEPERATOR + getProvince().getCPProvince();
	}

	public void setCountrySystemDefault() {
		sysDefaultCountry = PennantApplicationUtil.getDefaultCounty();
	}

	public void onFulfill$cPCountry(Event event) {
		logger.debug("Entering");
		doCheckSystemDefault();
		logger.debug("Leaving");
	}

	public void onChange$businessArea(Event event) {
		logger.debug("Entering");

		String businessAreaValue = this.businessArea.getValue();
		if (StringUtils.isNotBlank(businessAreaValue)) {
			if (!StringUtils.equals(old_BusineesArea, businessAreaValue)) {
				boolean businessAreaExist = this.provinceService.getBusinessAreaExist(businessAreaValue, "_View");
				if (businessAreaExist) {
					this.businessArea.setErrorMessage(
							"Already Exist" + " " + Labels.getLabel("label_ProvinceDialog_BusinessArea.value"));
				}
			}
		}

		logger.debug("Leaving");
	}

	public void onCheck$taxAvailable(Event event) {
		logger.debug("Enteing");

		if (this.taxAvailable.isChecked()) {
			if (!this.province.isNewRecord()) {
				this.tab_gstdetails.setVisible(true);
			}
		} else {
			this.tab_gstdetails.setVisible(false);
			List<TaxDetail> taxDetailList = new ArrayList<TaxDetail>();
			if (CollectionUtils.isNotEmpty(getTaxDetailList())) {
				for (TaxDetail taxDet : getTaxDetailList()) {
					if (!taxDet.isNewRecord()) {
						if (StringUtils.isBlank(taxDet.getRecordType())) {
							taxDet.setNewRecord(true);
						}
						taxDet.setRecordType(PennantConstants.RECORD_TYPE_DEL);
						taxDetailList.add(taxDet);
					}
				}
			}

			doFillGSTINMappingDetails(taxDetailList);
		}

		logger.debug("Leaving");
	}

	public void onCheck$taxExempted(Event event) {
		logger.debug("Entering" + event.toString());
		if (this.taxExempted.isChecked()) {
			this.taxStateCode.setErrorMessage("");
			this.businessArea.setErrorMessage("");
			this.space_taxStateCode.setSclass("");
			this.space_businessArea.setSclass("");
			this.taxStateCode.setReadonly(true);
			this.businessArea.setReadonly(true);
			this.businessArea.setValue("");
			this.taxStateCode.setValue("");
		} else {
			this.taxStateCode.setReadonly(false);
			this.businessArea.setReadonly(false);
			this.space_taxStateCode.setSclass(PennantConstants.mandateSclass);
			this.space_businessArea.setSclass(PennantConstants.mandateSclass);

		}
		logger.debug("Leaving" + event.toString());
	}

	private void checkTaxExempted() {
		if (this.taxExempted.isChecked()) {
			this.businessArea.setReadonly(true);
			this.space_taxStateCode.setSclass("");
			this.space_businessArea.setSclass("");
			this.businessArea.setValue("");
			this.taxStateCode.setValue("");

		}

	}

	private void checkTaxAvailable() {
		logger.debug("Enteing");

		this.tab_gstdetails.setVisible(false);
		if (this.taxAvailable.isChecked()) {
			if (!this.province.isNewRecord()) {
				this.tab_gstdetails.setVisible(true);
			}
		}

		logger.debug("Leaving");
	}

	public void doCheckSystemDefault() {
		logger.debug("Entering");
		if (StringUtils.isNotBlank(this.cPCountry.getValue())) {
			if (sysDefaultCountry != null && sysDefaultCountry.getCountryCode().equals(this.cPCountry.getValue())) {
				this.systemDefault.setDisabled(isReadOnly("ProvinceDialog_systemDefault"));
			} else {
				this.systemDefault.setDisabled(true);
				this.systemDefault.setChecked(false);
			}
		} else {
			this.systemDefault.setDisabled(true);
			this.systemDefault.setChecked(false);
		}
		logger.debug("Entering");
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

	public Province getProvince() {
		return this.province;
	}

	public void setProvince(Province province) {
		this.province = province;
	}

	public void setProvinceService(ProvinceService provinceService) {
		this.provinceService = provinceService;
	}

	public ProvinceService getProvinceService() {
		return this.provinceService;
	}

	public void setProvinceListCtrl(ProvinceListCtrl provinceListCtrl) {
		this.provinceListCtrl = provinceListCtrl;
	}

	public ProvinceListCtrl getProvinceListCtrl() {
		return this.provinceListCtrl;
	}

	public List<TaxDetail> getTaxDetailList() {
		return taxDetailList;
	}

	public void setTaxDetailList(List<TaxDetail> taxDetailList) {
		this.taxDetailList = taxDetailList;
	}

	public List<TaxDetail> getTaxMappingDetailList() {
		return taxMappingDetailList;
	}

	public void setTaxMappingDetailList(List<TaxDetail> taxMappingDetailList) {
		this.taxMappingDetailList = taxMappingDetailList;
	}

}
