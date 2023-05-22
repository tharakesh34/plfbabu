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
 * * FileName : DirectorDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-12-2011 * *
 * Modified Date : 01-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.customermasters.directordetail;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerDocumentService;
import com.pennant.backend.service.customermasters.DirectorDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.customermasters.customer.CustomerViewDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/DirectorDetail/directorDetailDialog.zul file.
 */
public class DirectorDetailDialogCtrl extends GFCBaseCtrl<DirectorDetail> {
	private static final long serialVersionUID = -3436424948986683205L;
	private static final Logger logger = LogManager.getLogger(DirectorDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DirectorDetailDialog; // autowired
	protected Longbox custID; // autowired
	protected Longbox directorID; // autowired
	protected Textbox firstName; // autowired
	protected Textbox lastName; // autowired
	protected Textbox shortName; // autowired
	protected Decimalbox sharePerc; // autowired
	protected Space space_SharePerc; // autowired
	protected Combobox custGenderCode; // autowired
	protected Combobox custSalutationCode; // autowired
	protected Textbox custAddrHNbr; // autowired
	protected Textbox custFlatNbr; // autowired
	protected Textbox custAddrStreet; // autowired
	protected Textbox custAddrLine1; // autowired
	protected Textbox custAddrLine2; // autowired
	protected Textbox custPOBox; // autowired
	protected ExtendedCombobox custAddrCity; // autowired
	protected ExtendedCombobox custAddrProvince; // autowired
	protected ExtendedCombobox custAddrCountry; // autowired
	protected Textbox custAddrZIP; // autowired
	protected Textbox custAddrPhone; // autowired
	protected Datebox custAddrFrom; // autowired
	protected Textbox custCIF; // autowired
	protected Label custShrtName; // autowired
	protected Checkbox shareholder; // autowired
	protected Checkbox director; // autowired
	protected ExtendedCombobox designation; // autowired
	protected ExtendedCombobox idType; // autowired
	protected Space space_idReference; // autowired
	protected Textbox idReference; // autowired
	protected ExtendedCombobox nationality; // autowired
	protected Datebox dob; // autowired
	private transient CustomerDocumentService customerDocumentService;
	protected Checkbox shareholderCustomer; // autowired
	protected Label label_DirectorDetailDialog_ShareholderCif; // autowired
	protected Space space_ShareHolderCif; // autowired
	protected Longbox shareHolderCustID; // autowired
	protected Textbox shareHolderCustCIF; // autowired
	protected Button btnSearchPRShareHolderCustid;
	protected Label shareHolderCustShrtName;

	public CustomerDocumentService getCustomerDocumentService() {
		return customerDocumentService;
	}

	public void setCustomerDocumentService(CustomerDocumentService customerDocumentService) {
		this.customerDocumentService = customerDocumentService;
	}

	// not auto wired vars
	private DirectorDetail directorDetail; // overhanded per param
	private transient DirectorDetailListCtrl directorDetailListCtrl; // overhanded per param

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient DirectorDetailService directorDetailService;
	private transient PagedListService pagedListService;
	private transient CustomerSelectCtrl customerSelectCtrl;

	private boolean newRecord = false;
	private boolean newCustomer = false;
	private CustomerDialogCtrl customerDialogCtrl;
	private CustomerViewDialogCtrl customerViewDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject;
	protected JdbcSearchObject<Customer> newShareHolderSearchObject;
	protected Button btnSearchPRCustid;

	private List<DirectorDetail> directorDetailList;
	private List<ValueLabel> genderCodes = PennantAppUtil.getGenderCodes();
	private String sCustGenderCode;
	private String sCustAddrCountry;
	private String sCustAddrProvince;
	protected Row Row_Gender;

	private BigDecimal totSharePerc;
	private String userRole = "";
	private boolean isEnquiry = false;
	private boolean isFinanceProcess = false;
	private boolean workflow = false;

	/**
	 * default constructor.<br>
	 */
	public DirectorDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DirectorDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected DirectorDetail object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_DirectorDetailDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_DirectorDetailDialog);

		if (arguments.containsKey("directorDetail")) {
			this.directorDetail = (DirectorDetail) arguments.get("directorDetail");
			DirectorDetail befImage = new DirectorDetail();
			BeanUtils.copyProperties(this.directorDetail, befImage);
			this.directorDetail.setBefImage(befImage);

			setDirectorDetail(this.directorDetail);
		} else {
			setDirectorDetail(null);
		}

		if (arguments.containsKey("totSharePerc")) {
			this.totSharePerc = (BigDecimal) arguments.get("totSharePerc");
		}

		if (arguments.containsKey("isEnquiry")) {
			isEnquiry = (Boolean) arguments.get("isEnquiry");
		}

		if (getDirectorDetail().isNewRecord()) {
			setNewRecord(true);
		}

		if (arguments.containsKey("customerDialogCtrl")) {

			setCustomerDialogCtrl((CustomerDialogCtrl) arguments.get("customerDialogCtrl"));
			setNewCustomer(true);

			if (arguments.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}
			this.directorDetail.setWorkflowId(0);
		}
		if (arguments.containsKey("customerViewDialogCtrl")) {

			setCustomerViewDialogCtrl((CustomerViewDialogCtrl) arguments.get("customerViewDialogCtrl"));
			setNewCustomer(true);

			if (arguments.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}
			this.directorDetail.setWorkflowId(0);
		}

		if (arguments.containsKey("isFinanceProcess")) {
			isFinanceProcess = (Boolean) arguments.get("isFinanceProcess");
		}

		if (arguments.containsKey("fromLoan")) {
			isFinanceProcess = (Boolean) arguments.get("fromLoan");
		}

		if (getCustomerDialogCtrl() != null && !isFinanceProcess) {
			workflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
		}

		doLoadWorkFlow(this.directorDetail.isWorkflow(), this.directorDetail.getWorkflowId(),
				this.directorDetail.getNextTaskId());

		if (arguments.containsKey("roleCode")) {
			userRole = (String) arguments.get("roleCode");
			getUserWorkspace().allocateRoleAuthorities(userRole, "DirectorDetailDialog");
		}
		/* set components visible dependent of the users rights */
		doCheckRights();

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "DirectorDetailDialog");
		}

		// READ OVERHANDED params !
		// we get the directorDetailListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete directorDetail here.
		if (arguments.containsKey("directorDetailListCtrl")) {
			setDirectorDetailListCtrl((DirectorDetailListCtrl) arguments.get("directorDetailListCtrl"));
		} else {
			setDirectorDetailListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getDirectorDetail());

		// Calling SelectCtrl For proper selection of Customer
		/*
		 * if(isNewRecord() & !isNewCustomer()){ onload(); }
		 */
		logger.debug("Leaving" + event.toString());
	}

	private void doCheckEnquiry() {
		if (isEnquiry) {
			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);
			this.help.setVisible(false);
			doReadOnly();
		}
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.custID.setMaxlength(19);
		this.shareHolderCustID.setMaxlength(19);
		this.firstName.setMaxlength(50);
		this.lastName.setMaxlength(50);
		this.shortName.setMaxlength(50);
		this.custAddrHNbr.setMaxlength(50);
		this.custFlatNbr.setMaxlength(50);
		this.custAddrStreet.setMaxlength(50);
		this.custAddrLine1.setMaxlength(50);
		this.custAddrLine2.setMaxlength(50);
		this.custPOBox.setMaxlength(8);

		this.custAddrCity.setMaxlength(8);
		this.custAddrCity.setMandatoryStyle(true);
		this.custAddrCity.setModuleName("City");
		this.custAddrCity.setValueColumn("PCCity");
		this.custAddrCity.setDescColumn("PCCityName");
		this.custAddrCity.setValidateColumns(new String[] { "PCCity" });

		this.custAddrProvince.setMaxlength(8);
		this.custAddrProvince.setMandatoryStyle(true);
		this.custAddrProvince.setModuleName("Province");
		this.custAddrProvince.setValueColumn("CPProvince");
		this.custAddrProvince.setDescColumn("CPProvinceName");
		this.custAddrProvince.setValidateColumns(new String[] { "CPProvince" });

		this.custAddrCountry.setMaxlength(2);
		this.custAddrCountry.setMandatoryStyle(true);
		this.custAddrCountry.setModuleName("Country");
		this.custAddrCountry.setValueColumn("CountryCode");
		this.custAddrCountry.setDescColumn("CountryDesc");
		this.custAddrCountry.setValidateColumns(new String[] { "CountryCode" });

		this.custAddrZIP.setMaxlength(10);
		this.custAddrPhone.setMaxlength(10);
		this.custAddrFrom.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.designation.setMaxlength(8);
		this.designation.setReadonly(true);
		this.designation.setButtonDisabled(true);
		this.designation.setMaxlength(8);
		this.designation.setModuleName("Designation");
		this.designation.setValueColumn("DesgCode");
		this.designation.setDescColumn("DesgDesc");
		this.designation.setValidateColumns(new String[] { "DesgCode" });

		this.idType.setMaxlength(50);
		this.idType.setTextBoxWidth(110);
		this.idType.setModuleName("CustDocumentType");
		this.idType.setValueColumn("DocTypeCode");
		this.idType.setDescColumn("DocTypeDesc");
		this.idType.setValidateColumns(new String[] { "DocTypeCode" });

		this.idReference.setMaxlength(35);

		this.nationality.setMaxlength(2);
		this.nationality.setMandatoryStyle(false);
		this.nationality.setModuleName("NationalityCode");
		this.nationality.setValueColumn("NationalityCode");
		this.nationality.setDescColumn("NationalityDesc");
		this.nationality.setValidateColumns(new String[] { "NationalityCode" });

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
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("DirectorDetailDialog", userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DirectorDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DirectorDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DirectorDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DirectorDetailDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_DirectorDetailDialog);
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
		doWriteBeanToComponents(this.directorDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aDirectorDetail DirectorDetail
	 */
	public void doWriteBeanToComponents(DirectorDetail aDirectorDetail) {
		logger.debug("Entering");
		fillComboBox(this.custGenderCode, aDirectorDetail.getCustGenderCode(), genderCodes, "");
		fillComboBox(this.custSalutationCode, aDirectorDetail.getCustSalutationCode(),
				PennantAppUtil.getSalutationCodes(aDirectorDetail.getCustGenderCode()), "");
		if (aDirectorDetail.getCustID() != Long.MIN_VALUE) {
			this.custID.setValue(aDirectorDetail.getCustID());
		}
		if (aDirectorDetail.getShareHolderCustID() != Long.MIN_VALUE) {
			this.shareHolderCustID.setValue(aDirectorDetail.getShareHolderCustID());
		}
		this.firstName.setValue(aDirectorDetail.getFirstName());
		this.lastName.setValue(aDirectorDetail.getLastName());
		this.shortName.setValue(StringUtils.trimToEmpty(aDirectorDetail.getShortName()));
		this.custAddrHNbr.setValue(aDirectorDetail.getCustAddrHNbr());
		this.custFlatNbr.setValue(aDirectorDetail.getCustFlatNbr());
		this.custAddrStreet.setValue(aDirectorDetail.getCustAddrStreet());
		this.custAddrLine1.setValue(aDirectorDetail.getCustAddrLine1());
		this.custAddrLine2.setValue(aDirectorDetail.getCustAddrLine2());
		this.custPOBox.setValue(aDirectorDetail.getCustPOBox());
		this.custAddrCity.setValue(aDirectorDetail.getCustAddrCity());
		this.custAddrProvince.setValue(aDirectorDetail.getCustAddrProvince());
		this.custAddrCountry.setValue(StringUtils.trimToEmpty(aDirectorDetail.getCustAddrCountry()));
		this.custAddrZIP.setValue(aDirectorDetail.getCustAddrZIP());
		this.custAddrPhone.setValue(aDirectorDetail.getCustAddrPhone());
		this.custAddrFrom.setValue(aDirectorDetail.getCustAddrFrom());
		this.custCIF.setValue(
				aDirectorDetail.getLovDescCustCIF() == null ? "" : aDirectorDetail.getLovDescCustCIF().trim());
		this.shareHolderCustCIF.setValue(aDirectorDetail.getLovDescShareHolderCustCIF() == null ? ""
				: aDirectorDetail.getLovDescShareHolderCustCIF().trim());
		this.custShrtName.setValue(aDirectorDetail.getLovDescCustShrtName() == null ? ""
				: aDirectorDetail.getLovDescCustShrtName().trim());
		this.shareHolderCustShrtName.setValue(aDirectorDetail.getLovShareHolderCustShrtName() == null ? ""
				: aDirectorDetail.getLovShareHolderCustShrtName().trim());
		this.idType.setValue(StringUtils.trimToEmpty(aDirectorDetail.getIdType()));
		this.idReference.setValue(StringUtils.trimToEmpty(aDirectorDetail.getIdReference()));
		this.nationality.setValue(StringUtils.trimToEmpty(aDirectorDetail.getNationality()));
		this.dob.setValue(aDirectorDetail.getDob());
		this.shareholder.setChecked(aDirectorDetail.isShareholder());
		this.shareholderCustomer.setChecked(aDirectorDetail.isShareholderCustomer());
		this.director.setChecked(aDirectorDetail.isDirector());
		if (this.shareholder.isChecked()) {
			this.sharePerc.setReadonly(false);
			this.sharePerc.setValue(aDirectorDetail.getSharePerc());
		} else {
			this.sharePerc.setReadonly(true);
			this.sharePerc.setValue(BigDecimal.ZERO);
		}
		if (this.director.isChecked()) {
			this.designation.setReadonly(isReadOnly("DirectorDetailDialog_designation"));
			this.designation.setButtonDisabled(isReadOnly("DirectorDetailDialog_designation"));
			this.designation.setValue(aDirectorDetail.getDesignation());
			this.designation.setDescription(aDirectorDetail.getLovDescDesignationName());
		} else {
			this.designation.setReadonly(true);
			this.designation.setButtonDisabled(true);
			this.designation.setValue("");
			this.designation.setDescription("");
		}

		if (this.shareholderCustomer.isChecked()) {
			this.label_DirectorDetailDialog_ShareholderCif.setVisible(true);
			this.shareHolderCustCIF.setVisible(true);
			this.btnSearchPRShareHolderCustid.setVisible(true);
			this.shareHolderCustShrtName.setVisible(true);
			this.space_ShareHolderCif.setVisible(true);
		} else {
			this.label_DirectorDetailDialog_ShareholderCif.setVisible(false);
			this.shareHolderCustCIF.setVisible(false);
			this.btnSearchPRShareHolderCustid.setVisible(false);
			this.custShrtName.setVisible(false);
			this.space_ShareHolderCif.setVisible(false);
		}

		if (isNewRecord()) {
			this.custAddrCity.setDescription("");
			this.custAddrProvince.setDescription("");
			this.custAddrCountry.setDescription("");
			this.designation.setDescription("");
			this.nationality.setDescription("");
			this.idType.setDescription("");
		} else {
			this.custAddrCity.setDescription(aDirectorDetail.getLovDescCustAddrCityName());
			this.custAddrProvince.setDescription(aDirectorDetail.getLovDescCustAddrProvinceName());
			this.custAddrCountry.setDescription(aDirectorDetail.getLovDescCustAddrCountryName());
			this.nationality.setDescription(aDirectorDetail.getLovDescNationalityName());
			this.idType.setDescription(aDirectorDetail.getLovDescCustDocCategoryName());
		}

		this.custAddrProvince
				.setFilters(new Filter[] { new Filter("CPCountry", this.custAddrCountry.getValue(), Filter.OP_EQUAL) });
		this.custAddrCity
				.setFilters(new Filter[] { new Filter("PCCountry", this.custAddrCountry.getValue(), Filter.OP_EQUAL),
						new Filter("PCProvince", this.custAddrProvince.getValue(), Filter.OP_EQUAL) });
		if (StringUtils.isNotEmpty(aDirectorDetail.getIdType()) && aDirectorDetail.isIdReferenceMand()) {
			this.space_idReference.setSclass(PennantConstants.mandateSclass);
		} else {
			this.space_idReference.setSclass("");
		}
		this.recordStatus.setValue(aDirectorDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aDirectorDetail
	 */
	public void doWriteComponentsToBean(DirectorDetail aDirectorDetail) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aDirectorDetail.setCustID(this.custID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDirectorDetail.setShareHolderCustID(this.shareHolderCustID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDirectorDetail.setLovDescShareHolderCustCIF(this.shareHolderCustCIF.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDirectorDetail.setLovShareHolderCustShrtName(this.shareHolderCustShrtName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aDirectorDetail.setFirstName(this.firstName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDirectorDetail.setLastName(this.lastName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDirectorDetail.setShortName(this.shortName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("#".equals(getComboboxValue(this.custGenderCode))) {
				aDirectorDetail.setCustGenderCode("");
			} else {
				aDirectorDetail.setCustGenderCode(getComboboxValue(this.custGenderCode));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("#".equals(getComboboxValue(this.custSalutationCode))) {
				aDirectorDetail.setCustSalutationCode("");
			} else {
				aDirectorDetail.setCustSalutationCode(getComboboxValue(this.custSalutationCode));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ((!this.shareholder.isDisabled() || !this.director.isDisabled())
					&& (!this.shareholder.isChecked() && !this.director.isChecked())) {
				throw new WrongValueException(this.shareholder,
						Labels.getLabel("label_DirectorDetailDialog_ShareOrDirector.value"));
			}
			if (this.shareholder.isChecked() && !this.sharePerc.isReadonly()
					&& this.sharePerc.getValue().compareTo(BigDecimal.ZERO) <= 0) {
				throw new WrongValueException(this.sharePerc, Labels.getLabel("NUMBER_MINVALUE",
						new String[] { Labels.getLabel("label_DirectorDetailDialog_SharePerc.value"), "0" }));
			}
			if (this.sharePerc.getValue() != null && this.sharePerc.intValue() != 0) {
				if ((this.totSharePerc.add(this.sharePerc.getValue())).compareTo(new BigDecimal(100)) > 0) {
					BigDecimal availableSharePerc = new BigDecimal(100).subtract(this.totSharePerc);
					throw new WrongValueException(this.sharePerc,
							Labels.getLabel("Total_Percentage",
									new String[] { Labels.getLabel("label_DirectorDetailDialog_SharePerc.value"),
											availableSharePerc.toString() }));
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDirectorDetail.setLovDescCustAddrCityName(this.custAddrCity.getDescription());
			aDirectorDetail.setCustAddrCity(this.custAddrCity.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDirectorDetail.setLovDescCustAddrProvinceName(this.custAddrProvince.getDescription());
			aDirectorDetail.setCustAddrProvince(this.custAddrProvince.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDirectorDetail.setLovDescCustAddrCountryName(this.custAddrCountry.getDescription());
			aDirectorDetail.setCustAddrCountry(this.custAddrCountry.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDirectorDetail.setCustAddrZIP(this.custAddrZIP.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDirectorDetail.setCustAddrPhone(this.custAddrPhone.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDirectorDetail.setCustAddrHNbr(this.custAddrHNbr.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDirectorDetail.setCustFlatNbr(this.custFlatNbr.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDirectorDetail.setCustAddrStreet(this.custAddrStreet.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDirectorDetail.setCustAddrLine1(this.custAddrLine1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDirectorDetail.setCustAddrLine2(this.custAddrLine2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aDirectorDetail.setCustPOBox(this.custPOBox.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.custAddrFrom.getValue() != null) {
				Date appDate = SysParamUtil.getAppDate();
				if (appDate.compareTo(this.custAddrFrom.getValue()) != 1) {
					throw new WrongValueException(this.custAddrFrom, Labels.getLabel("NUMBER_MAXVALUE", new String[] {
							Labels.getLabel("label_DirectorDetailDialog_CustAddrFrom.value"), "Application Date" }));
				}
				aDirectorDetail.setCustAddrFrom(new Timestamp(this.custAddrFrom.getValue().getTime()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aDirectorDetail.setShareholder(this.shareholder.isChecked());
		aDirectorDetail.setShareholderCustomer(this.shareholderCustomer.isChecked());
		aDirectorDetail.setDirector(this.director.isChecked());

		try {
			aDirectorDetail.setLovDescDesignationName(this.designation.getDescription());
			aDirectorDetail.setDesignation(this.designation.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aDirectorDetail.setSharePerc(this.sharePerc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aDirectorDetail.setLovDescCustDocCategoryName(this.idType.getDescription());
			aDirectorDetail.setIdType(this.idType.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aDirectorDetail.setIdReference(this.idReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aDirectorDetail.setLovDescNationalityName(this.nationality.getDescription());
			aDirectorDetail.setNationality(this.nationality.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aDirectorDetail.setDob(this.dob.getValue());
			if (this.dob.getValue() != null) {
				if (DateUtil.compare(this.dob.getValue(), SysParamUtil.getAppDate()) != -1) {
					throw new WrongValueException(this.dob, Labels.getLabel("DATE_FUTURE_TODAY",
							new String[] { Labels.getLabel("label_DirectorDetailDialog_DOB.value") }));
				}
			}
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

		aDirectorDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aDirectorDetail
	 */
	public void doShowDialog(DirectorDetail aDirectorDetail) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aDirectorDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
			this.shareHolderCustCIF.focus();
		} else {
			this.firstName.focus();
			if (isNewCustomer()) {
				doEdit();
			} else if (isWorkFlowEnabled()) {
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
			doWriteBeanToComponents(aDirectorDetail);

			doCheckEnquiry();

			if (isNewCustomer()) {
				this.window_DirectorDetailDialog.setHeight("530px");
				this.window_DirectorDetailDialog.setWidth("85%");
				this.groupboxWf.setVisible(false);
				this.window_DirectorDetailDialog.doModal();
			} else {
				this.window_DirectorDetailDialog.setWidth("100%");
				this.window_DirectorDetailDialog.setHeight("100%");
				setDialog(DialogType.EMBEDDED);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		clearShareDirector();
		if (!this.custID.isReadonly()) {
			this.custCIF.setConstraint(
					new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_CustID.value"), null, true));
		}
		if (this.shareholderCustomer.isChecked()) {
			if (!this.shareHolderCustID.isReadonly()) {
				this.shareHolderCustCIF.setConstraint(new PTStringValidator(
						Labels.getLabel("label_DirectorDetailDialog_shareHolderCustCIF.value"), null, true));
			}
		}
		if (!this.firstName.isReadonly()) {
			this.firstName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_FirstName.value"),
							PennantRegularExpressions.REGEX_CUST_NAME, false));
		}
		if (!this.lastName.isReadonly()) {
			this.lastName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_LastName.value"),
							PennantRegularExpressions.REGEX_CUST_NAME, false));
		}
		if (!this.shortName.isReadonly()) {
			this.shortName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_ShortName.value"),
							PennantRegularExpressions.REGEX_CUST_NAME, false));
		}
		if (!this.firstName.isReadonly() && !this.lastName.isReadonly() && !this.shortName.isReadonly()) {
			if (StringUtils.isBlank(this.firstName.getValue()) && StringUtils.isBlank(this.lastName.getValue())
					&& StringUtils.isBlank(this.shortName.getValue())) {

				this.shortName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_AnyName.value"),
								PennantRegularExpressions.REGEX_CUST_NAME, true));
			}
		}
		if (this.shareholder.isChecked()) {
			if (!this.sharePerc.isReadonly() && !this.sharePerc.isDisabled()) {
				this.sharePerc.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_DirectorDetailDialog_SharePerc.value"), 2, true, false));
			}
		}
		if (this.director.isChecked()) {
			if (!this.designation.isReadonly()) {
				this.designation.setConstraint(new PTStringValidator(
						Labels.getLabel("label_DirectorDetailDialog_Designation.value"), null, true, true));
			}
		}
		if (!this.custAddrHNbr.isReadonly()) {
			this.custAddrHNbr.setConstraint(
					new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_CustAddrHNbr.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.custFlatNbr.isReadonly()) {
			this.custFlatNbr.setConstraint(
					new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_CustFlatNbr.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.custAddrStreet.isReadonly()) {
			this.custAddrStreet.setConstraint(
					new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_CustAddrStreet.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.custAddrLine1.isReadonly()) {
			this.custAddrLine1.setConstraint(
					new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_CustAddrLine1.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.custAddrLine2.isReadonly()) {
			this.custAddrLine2.setConstraint(
					new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_CustAddrLine2.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.custPOBox.isReadonly()) {
			this.custPOBox
					.setConstraint(new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_CustPOBox.value"),
							PennantRegularExpressions.REGEX_NUMERIC, false));
		}
		if (!this.custAddrZIP.isReadonly()) {
			this.custAddrZIP.setConstraint(
					new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_CustAddrZIP.value"),
							PennantRegularExpressions.REGEX_ZIP, false));
		}

		if (!this.custAddrPhone.isReadonly()) {
			this.custAddrPhone.setConstraint(new PTMobileNumberValidator(
					Labels.getLabel("label_DirectorDetailDialog_CustAddrPhone.value"), false));
		}
		if (!this.idReference.isReadonly()) {
			this.idReference.setConstraint(
					new PTStringValidator(Labels.getLabel("label_DirectorDetailDialog_IDReference.value"),
							PennantRegularExpressions.REGEX_ALPHANUM,
							StringUtils.equals(PennantConstants.mandateSclass, this.space_idReference.getSclass())));
		}

		if (!this.idType.isReadonly()) {

			// TODO:Need To move HardCoded values into constants.
			String value = this.idType.getValue();
			if (StringUtils.isNotBlank(value)) {
				String masterDocType = customerDocumentService.getDocTypeByMasterDefByCode("DOC_TYPE", value);
				String regex = PennantRegularExpressions.REGEX_ALPHANUM_CODE;
				if (StringUtils.equalsIgnoreCase(PennantConstants.PANNUMBER, masterDocType)) {
					regex = PennantRegularExpressions.REGEX_PANNUMBER;
				} else if (StringUtils.equalsIgnoreCase(PennantConstants.CPRCODE, masterDocType)) {
					regex = PennantRegularExpressions.REGEX_AADHAR_NUMBER;
				}
				if (StringUtils.isNotBlank(regex)) {
					idReference.setConstraint(new PTStringValidator(
							Labels.getLabel("label_DirectorDetailDialog_IDReference.value"), regex,
							StringUtils.equals(PennantConstants.mandateSclass, this.space_idReference.getSclass())));
				}
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
		this.custCIF.setConstraint("");
		this.shareHolderCustCIF.setConstraint("");
		this.firstName.setConstraint("");
		this.lastName.setConstraint("");
		this.shortName.setConstraint("");
		this.custAddrHNbr.setConstraint("");
		this.custFlatNbr.setConstraint("");
		this.custAddrStreet.setConstraint("");
		this.custAddrLine1.setConstraint("");
		this.custAddrLine2.setConstraint("");
		this.custPOBox.setConstraint("");
		this.custAddrZIP.setConstraint("");
		this.custAddrPhone.setConstraint("");
		this.custAddrFrom.setConstraint("");
		this.designation.setConstraint("");
		this.idType.setConstraint("");
		this.idReference.setConstraint("");
		this.nationality.setConstraint("");
		this.dob.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.custAddrCountry.setConstraint(new PTStringValidator(
				Labels.getLabel("label_DirectorDetailDialog_CustAddrCountry.value"), null, true, true));
		this.custAddrProvince.setConstraint(new PTStringValidator(
				Labels.getLabel("label_DirectorDetailDialog_CustAddrProvince.value"), null, true, true));
		this.custAddrCity.setConstraint(new PTStringValidator(
				Labels.getLabel("label_DirectorDetailDialog_CustAddrCity.value"), null, true, true));
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.custAddrCity.setConstraint("");
		this.custAddrProvince.setConstraint("");
		this.custAddrCountry.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.sharePerc.setErrorMessage("");
		this.custCIF.setErrorMessage("");
		this.shareHolderCustCIF.setErrorMessage("");
		this.firstName.setErrorMessage("");
		this.lastName.setErrorMessage("");
		this.shortName.setErrorMessage("");
		this.custGenderCode.setErrorMessage("");
		this.custSalutationCode.setErrorMessage("");
		this.custAddrHNbr.setErrorMessage("");
		this.custFlatNbr.setErrorMessage("");
		this.custAddrStreet.setErrorMessage("");
		this.custAddrLine1.setErrorMessage("");
		this.custAddrLine2.setErrorMessage("");
		this.custPOBox.setErrorMessage("");
		this.custAddrCity.setErrorMessage("");
		this.custAddrProvince.setErrorMessage("");
		this.custAddrCountry.setErrorMessage("");
		this.custAddrZIP.setErrorMessage("");
		this.custAddrPhone.setErrorMessage("");
		this.custAddrFrom.setErrorMessage("");
		this.designation.setErrorMessage("");
		this.idType.setErrorMessage("");
		this.idReference.setErrorMessage("");
		this.nationality.setErrorMessage("");
		this.dob.setErrorMessage("");
		logger.debug("Leaving");

	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getDirectorDetailListCtrl().search();
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final DirectorDetail aDirectorDetail = new DirectorDetail();
		BeanUtils.copyProperties(getDirectorDetail(), aDirectorDetail);

		String name = "";
		if (StringUtils.isNotBlank(aDirectorDetail.getShortName())) {
			name = aDirectorDetail.getShortName();
		} else {
			name = aDirectorDetail.getFirstName() + "  " + aDirectorDetail.getLastName();
		}

		final String keyReference = Labels.getLabel("label_DirectorDetailDialog_ShortName.value") + " : " + name;

		doDelete(keyReference, aDirectorDetail);

		logger.debug(Literal.LEAVING);
	}

	protected void onDoDelete(final DirectorDetail aDirectorDetail) {
		String tranType = PennantConstants.TRAN_WF;

		if (StringUtils.isBlank(aDirectorDetail.getRecordType())) {
			aDirectorDetail.setVersion(aDirectorDetail.getVersion() + 1);
			aDirectorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			if (!isFinanceProcess && getCustomerDialogCtrl() != null
					&& getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()) {
				aDirectorDetail.setNewRecord(true);
			}
			if (isWorkFlowEnabled()) {
				aDirectorDetail.setNewRecord(true);
				tranType = PennantConstants.TRAN_WF;
			} else {
				tranType = PennantConstants.TRAN_DEL;
			}
		}

		try {
			if (isNewCustomer()) {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newDirectorProcess(aDirectorDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_DirectorDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getCustomerDialogCtrl().doFillCustomerDirectory(this.directorDetailList);
					closeDialog();
				}

			} else if (doProcess(aDirectorDetail, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			showMessage(e);
		}
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (isNewRecord()) {
			if (isNewCustomer()) {
				this.btnCancel.setVisible(false);
				this.btnSearchPRCustid.setVisible(false);
				// this.btnSearchPRShareHolderCustid.setVisible(false);
			} else {
				this.btnSearchPRCustid.setVisible(true);
				// this.btnSearchPRShareHolderCustid.setVisible(true);
			}
			this.custSalutationCode.setDisabled(true);

		} else {
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.btnSearchPRShareHolderCustid.setVisible(false);
			this.custSalutationCode.setDisabled(isReadOnly("DirectorDetailDialog_custSalutationCode"));
			this.custAddrCity.setReadonly(isReadOnly("DirectorDetailDialog_custAddrCity"));
			this.custAddrProvince.setReadonly(isReadOnly("DirectorDetailDialog_custAddrProvince"));
		}

		this.custCIF.setReadonly(true);
		this.shareHolderCustCIF.setReadonly(true);
		this.shortName.setReadonly(isReadOnly("DirectorDetailDialog_shortName"));
		this.firstName.setReadonly(isReadOnly("DirectorDetailDialog_firstName"));
		this.lastName.setReadonly(isReadOnly("DirectorDetailDialog_lastName"));
		this.custGenderCode.setDisabled(isReadOnly("DirectorDetailDialog_custGenderCode"));
		this.custAddrHNbr.setReadonly(isReadOnly("DirectorDetailDialog_custAddrHNbr"));
		this.custFlatNbr.setReadonly(isReadOnly("DirectorDetailDialog_custFlatNbr"));
		this.custAddrStreet.setReadonly(isReadOnly("DirectorDetailDialog_custAddrStreet"));
		this.custAddrLine1.setReadonly(isReadOnly("DirectorDetailDialog_custAddrLine1"));
		this.custAddrLine2.setReadonly(isReadOnly("DirectorDetailDialog_custAddrLine2"));
		this.custPOBox.setReadonly(isReadOnly("DirectorDetailDialog_custPOBox"));
		this.custAddrCountry.setReadonly(isReadOnly("DirectorDetailDialog_custAddrCountry"));
		this.custAddrCountry.setMandatoryStyle(!(isReadOnly("DirectorDetailDialog_custAddrCountry")));
		this.custAddrZIP.setReadonly(isReadOnly("DirectorDetailDialog_custAddrZIP"));
		this.custAddrPhone.setReadonly(isReadOnly("DirectorDetailDialog_custAddrPhone"));
		this.custAddrFrom.setDisabled(isReadOnly("DirectorDetailDialog_custAddrFrom"));
		this.sharePerc.setReadonly(isReadOnly("DirectorDetailDialog_sharePerc"));
		this.shareholder.setDisabled(isReadOnly("DirectorDetailDialog_shareholder"));
		this.shareholderCustomer.setDisabled(isReadOnly("DirectorDetailDialog_shareholderCustomer"));
		this.director.setDisabled(isReadOnly("DirectorDetailDialog_director"));
		this.designation.setReadonly(isReadOnly("DirectorDetailDialog_designation"));
		this.idType.setReadonly(isReadOnly("DirectorDetailDialog_idType"));
		this.idReference.setReadonly(isReadOnly("DirectorDetailDialog_idReference"));
		this.nationality.setReadonly(isReadOnly("DirectorDetailDialog_nationality"));
		this.dob.setDisabled(isReadOnly("DirectorDetailDialog_dob"));
		this.btnSearchPRShareHolderCustid.setDisabled(isReadOnly("DirectorDetailDialog_ShareHolderCustCIF"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.directorDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (newCustomer) {
				if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newCustomer);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	// Check Rights for Each Component
	public boolean isReadOnly(String componentName) {
		boolean isCustomerWorkflow = false;
		if (getCustomerDialogCtrl() != null) {
			isCustomerWorkflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
		}
		if (isWorkFlowEnabled() || isCustomerWorkflow) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.custCIF.setReadonly(true);
		this.shareHolderCustCIF.setReadonly(true);
		this.btnSearchPRCustid.setDisabled(true);
		this.firstName.setReadonly(true);
		this.lastName.setReadonly(true);
		this.shortName.setReadonly(true);
		this.custGenderCode.setDisabled(true);
		this.custSalutationCode.setDisabled(true);
		this.custAddrHNbr.setReadonly(true);
		this.custFlatNbr.setReadonly(true);
		this.custAddrStreet.setReadonly(true);
		this.custAddrLine1.setReadonly(true);
		this.custAddrLine2.setReadonly(true);
		this.custPOBox.setReadonly(true);
		this.custAddrCity.setReadonly(true);
		this.custAddrProvince.setReadonly(true);
		this.custAddrCountry.setReadonly(true);
		this.custAddrZIP.setReadonly(true);
		this.custAddrPhone.setReadonly(true);
		this.custAddrFrom.setDisabled(true);
		this.shareholder.setDisabled(true);
		this.shareholderCustomer.setDisabled(true);
		this.director.setDisabled(true);
		this.designation.setReadonly(true);
		this.idType.setReadonly(true);
		this.idReference.setReadonly(true);
		this.nationality.setReadonly(true);
		this.dob.setDisabled(true);
		this.sharePerc.setDisabled(true);

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
		this.sharePerc.setValue("");
		this.custCIF.setValue("");
		this.shareHolderCustCIF.setValue("");
		this.custShrtName.setValue("");
		this.shareHolderCustShrtName.setValue("");
		this.firstName.setValue("");
		this.lastName.setValue("");
		this.shortName.setValue("");
		this.custGenderCode.setValue("");
		this.custSalutationCode.setValue("");
		this.custSalutationCode.setValue("");
		this.custAddrHNbr.setValue("");
		this.custFlatNbr.setValue("");
		this.custAddrStreet.setValue("");
		this.custAddrLine1.setValue("");
		this.custAddrLine2.setValue("");
		this.custPOBox.setValue("");
		this.custAddrCity.setValue("");
		this.custAddrCity.setDescription("");
		this.custAddrProvince.setValue("");
		this.custAddrProvince.setDescription("");
		this.custAddrCountry.setValue("");
		this.custAddrCountry.setDescription("");
		this.custAddrZIP.setValue("");
		this.custAddrPhone.setValue("");
		this.custAddrFrom.setText("");
		this.shareholder.setChecked(false);
		this.shareholderCustomer.setChecked(false);
		this.director.setChecked(false);
		this.designation.setValue("");
		this.idType.setValue("");
		this.idType.setDescription("");
		this.idReference.setValue("");
		this.nationality.setValue("");
		this.nationality.setDescription("");
		this.dob.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final DirectorDetail aDirectorDetail = new DirectorDetail();
		BeanUtils.copyProperties(getDirectorDetail(), aDirectorDetail);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the DirectorDetail object with the components data
		doWriteComponentsToBean(aDirectorDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aDirectorDetail.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aDirectorDetail.getRecordType())) {
				aDirectorDetail.setVersion(aDirectorDetail.getVersion() + 1);
				if (isNew) {
					aDirectorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aDirectorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aDirectorDetail.setNewRecord(true);
				}
			}
		} else {
			if (isNewCustomer()) {
				if (isNewRecord()) {
					aDirectorDetail.setVersion(1);
					aDirectorDetail.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
					if (workflow && !isFinanceProcess && StringUtils.isBlank(aDirectorDetail.getRecordType())) {
						aDirectorDetail.setNewRecord(true);
					}
				}

				if (StringUtils.isBlank(aDirectorDetail.getRecordType())) {
					aDirectorDetail.setVersion(aDirectorDetail.getVersion() + 1);
					aDirectorDetail.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aDirectorDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aDirectorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}

			} else {
				aDirectorDetail.setVersion(aDirectorDetail.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			if (isNewCustomer()) {
				AuditHeader auditHeader = newDirectorProcess(aDirectorDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_DirectorDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getCustomerDialogCtrl().doFillCustomerDirectory(this.directorDetailList);
					// send the data back to customer
					closeDialog();
				}
			} else if (doProcess(aDirectorDetail, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newDirectorProcess(DirectorDetail aDirectorDetail, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aDirectorDetail, tranType);
		directorDetailList = new ArrayList<DirectorDetail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aDirectorDetail.getLovDescCustCIF());
		String name = "";
		if (StringUtils.isNotBlank(aDirectorDetail.getShortName())) {
			name = aDirectorDetail.getShortName();
		} else {
			name = aDirectorDetail.getFirstName() + "  " + aDirectorDetail.getLastName();
		}
		valueParm[1] = String.valueOf(name);

		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":" + valueParm[0] + " , ";
		errParm[1] = PennantJavaUtil.getLabel("label_DirectorDetailDialog_ShortName.value") + ":" + valueParm[1];

		if (getCustomerDialogCtrl().getDirectorList() != null && getCustomerDialogCtrl().getDirectorList().size() > 0) {
			for (int i = 0; i < getCustomerDialogCtrl().getDirectorList().size(); i++) {
				DirectorDetail directorDetail = getCustomerDialogCtrl().getDirectorList().get(i);

				if (aDirectorDetail.getId() == directorDetail.getId()) { // Both Current and Existing list director same
					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aDirectorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aDirectorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							directorDetailList.add(aDirectorDetail);
						} else if (aDirectorDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aDirectorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aDirectorDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							directorDetailList.add(aDirectorDetail);
						} else if (aDirectorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getCustomerDirectorList()
									.size(); j++) {
								DirectorDetail director = getCustomerDialogCtrl().getCustomerDetails()
										.getCustomerDirectorList().get(j);
								if (director.getCustID() == aDirectorDetail.getCustID()
										&& director.getId() == aDirectorDetail.getId()) {
									directorDetailList.add(director);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							directorDetailList.add(directorDetail);
						}
					}
				} else {
					directorDetailList.add(directorDetail);
				}
			}
		}
		if (!recordAdded) {
			directorDetailList.add(aDirectorDetail);
		}
		return auditHeader;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aDirectorDetail (DirectorDetail)
	 * 
	 * @param tranType        (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(DirectorDetail aDirectorDetail, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aDirectorDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aDirectorDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDirectorDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aDirectorDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aDirectorDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aDirectorDetail);
				}

				if (isNotesMandatory(taskId, aDirectorDetail)) {
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

			aDirectorDetail.setTaskId(taskId);
			aDirectorDetail.setNextTaskId(nextTaskId);
			aDirectorDetail.setRoleCode(getRole());
			aDirectorDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aDirectorDetail, tranType);

			String operationRefs = getServiceOperations(taskId, aDirectorDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aDirectorDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aDirectorDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		DirectorDetail aDirectorDetail = (DirectorDetail) auditHeader.getAuditDetail().getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getDirectorDetailService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getDirectorDetailService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getDirectorDetailService().doApprove(auditHeader);

					if (aDirectorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getDirectorDetailService().doReject(auditHeader);
					if (aDirectorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_DirectorDetailDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_DirectorDetailDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.directorDetail), true);
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
		logger.debug("return Value:" + processCompleted);
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	// Search Button Component Events

	public void onSelect$custGenderCode(Event event) {
		logger.debug("Entering");
		if (!StringUtils.trimToEmpty(sCustGenderCode).equals(this.custGenderCode.getValue())) {
			this.custSalutationCode.setValue("");
		}
		if (StringUtils.isNotEmpty(this.custGenderCode.getValue())) {
			this.custSalutationCode.setDisabled(false);
		} else {
			this.custSalutationCode.setDisabled(true);
		}
		sCustGenderCode = this.custGenderCode.getValue();
		String genderCodeTemp = this.custGenderCode.getSelectedItem().getValue().toString();
		fillComboBox(this.custSalutationCode, this.custSalutationCode.getValue(),
				PennantAppUtil.getSalutationCodes(genderCodeTemp), "");
		logger.debug("Leaving");
	}

	public void onFulfill$custAddrProvince(Event event) {
		logger.debug("Entering" + event.toString());
		onFulfillProvince();
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfillProvince() {
		logger.debug(Literal.ENTERING);
		this.custAddrCity.setErrorMessage("");
		if (!StringUtils.trimToEmpty(sCustAddrProvince).equals(this.custAddrProvince.getValue())) {
			this.custAddrCity.setValue("");
			this.custAddrCity.setDescription("");
			this.custAddrCity.setFocus(true);
		}
		sCustAddrProvince = this.custAddrProvince.getValue();
		Filter[] filtersCity = new Filter[2];
		filtersCity[0] = new Filter("PCCountry", this.custAddrCountry.getValue(), Filter.OP_EQUAL);
		filtersCity[1] = new Filter("PCProvince", this.custAddrProvince.getValue(), Filter.OP_EQUAL);
		this.custAddrCity.setFilters(filtersCity);
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$custAddrCountry(Event event) {
		logger.debug("Entering" + event.toString());
		onFulfillCountry();
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfillCountry() {
		logger.debug(Literal.ENTERING);
		this.custAddrProvince.setErrorMessage("");
		this.custAddrCity.setErrorMessage("");
		if (!StringUtils.trimToEmpty(sCustAddrCountry).equals(this.custAddrCountry.getValue())) {
			this.custAddrProvince.setValue("");
			this.custAddrCity.setValue("");
			this.custAddrProvince.setDescription("");
			this.custAddrCity.setDescription("");
		}
		sCustAddrCountry = this.custAddrCountry.getValue();
		Filter[] filtersProvince = new Filter[1];
		filtersProvince[0] = new Filter("CPCountry", this.custAddrCountry.getValue(), Filter.OP_EQUAL);
		this.custAddrProvince.setFilters(filtersProvince);
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$designation(Event event) {
		logger.debug("Entering");
		clearShareDirector();
		Object dataObject = designation.getObject();
		if (dataObject instanceof String) {
			//
		} else {
			Designation details = (Designation) dataObject;
			if (details != null) {
				this.director.setChecked(true);
				isDirectorChecked(true);
			}
		}
		logger.debug("Leaving");
	}

	public void onFulfill$idType(Event event) {
		logger.debug("Entering" + event.toString());
		this.idReference.setErrorMessage("");
		this.space_idReference.setSclass("");
		Object dataObject = idType.getObject();
		if (dataObject instanceof String) {
			this.idType.setValue(dataObject.toString());
			this.idType.setDescription("");
			this.space_idReference.setSclass("");
		} else {
			DocumentType details = (DocumentType) dataObject;
			if (details != null) {
				this.idType.setValue(details.getDocTypeCode());
				this.idType.setDescription(details.getDocTypeDesc());
				this.space_idReference.setSclass(PennantConstants.mandateSclass);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$sharePerc(Event event) {
		logger.debug("Entering");
		if (this.sharePerc.getValue() != null) {
			clearShareDirector();
		}
		logger.debug("Leaving");
	}

	public void clearShareDirector() {
		doClearMessage();
		Clients.clearWrongValue(this.shortName);
		Clients.clearWrongValue(this.shareholder);
		Clients.clearWrongValue(this.shareholderCustomer);
		Clients.clearWrongValue(this.sharePerc);
		Clients.clearWrongValue(this.designation);
		this.sharePerc.setConstraint("");
		this.designation.setConstraint("");
		this.shortName.setConstraint("");
	}

	/**
	 * Method for Calling list Of existed Customers
	 * 
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchPRCustid(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Calling list Of existed Customers
	 * 
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchPRShareHolderCustid(Event event)
			throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering" + event.toString());
		onloadShareHolderCustomer();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 * 
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onload() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("custCtgType", PennantConstants.PFF_CUSTCTG_CORP);
		map.put("searchObject", this.newSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		final Customer aCustomer = (Customer) nCustomer;
		// PSD # 156134 commented the below code because main applicant details are overriding with share holder
		// details.
		// this.custID.setValue(aCustomer.getCustID());
		// this.custCIF.setValue(aCustomer.getCustCIF().trim());
		// this.custShrtName.setValue(aCustomer.getCustShrtName());
		if (this.shareholderCustomer.isChecked()) {
			this.shareHolderCustID.setValue(aCustomer.getCustID());
			this.shareHolderCustCIF.setValue(aCustomer.getCustCIF().trim());
			this.shareHolderCustShrtName.setValue(aCustomer.getCustShrtName());
			if (PennantConstants.PFF_CUSTCTG_CORP.equals(aCustomer.getCustCtgCode())
					|| PennantConstants.PFF_CUSTCTG_SME.equals(aCustomer.getCustCtgCode())) {
				this.shortName.setValue(aCustomer.getCustShrtName());
				this.nationality.setValue(aCustomer.getCustNationality());
				this.nationality.setDescription(aCustomer.getLovDescCustNationalityName());
				this.dob.setValue(aCustomer.getCustDOB());
				this.custAddrCountry.setValue(aCustomer.getCustAddrCountry());
				this.custAddrCountry.setDescription(aCustomer.getLovDescCustAddrCountry());
				onFulfillCountry();
				this.custAddrProvince.setValue(aCustomer.getCustAddrProvince());
				this.custAddrProvince.setDescription(aCustomer.getLovDescCustAddrProvince());
				onFulfillProvince();
				this.custAddrCity.setValue(aCustomer.getCustAddrCity());
				this.custAddrCity.setDescription(aCustomer.getLovDescCustAddrCity());
			} else if (PennantConstants.PFF_CUSTCTG_INDIV.equals(aCustomer.getCustCtgCode())) {
				this.firstName.setValue(aCustomer.getCustFName());
				this.lastName.setValue(aCustomer.getCustLName());
				this.nationality.setValue(aCustomer.getCustNationality());
				this.nationality.setDescription(aCustomer.getLovDescCustNationalityName());
				this.dob.setValue(aCustomer.getCustDOB());
				this.custAddrCountry.setValue(aCustomer.getCustAddrCountry());
				this.custAddrCountry.setDescription(aCustomer.getLovDescCustAddrCountry());
				onFulfillCountry();
				this.custAddrProvince.setValue(aCustomer.getCustAddrProvince());
				this.custAddrProvince.setDescription(aCustomer.getLovDescCustAddrProvince());
				onFulfillProvince();
				this.custAddrCity.setValue(aCustomer.getCustAddrCity());
				this.custAddrCity.setDescription(aCustomer.getLovDescCustAddrCity());
			}
		}

		this.newSearchObject = newSearchObject;
		logger.debug("Leaving");
	}

	/**
	 * To load the customerSelect filter dialog
	 * 
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onloadShareHolderCustomer() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");

		List<Filter> filterList = new ArrayList<>();
		filterList.add(new Filter("CustCIF", this.custCIF.getValue(), Filter.OP_NOT_EQUAL));
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("custCtgType", PennantConstants.PFF_CUSTCTG_CORP);
		map.put("searchObject", this.newShareHolderSearchObject);
		map.put("filtersList", filterList);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetShareHolderCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		final Customer aCustomer = (Customer) nCustomer;
		this.shareHolderCustID.setValue(aCustomer.getCustID());
		this.shareHolderCustCIF.setValue(aCustomer.getCustCIF().trim());
		this.shareHolderCustShrtName.setValue(aCustomer.getCustShrtName());
		this.newSearchObject = newSearchObject;
		logger.debug("Leaving");
	}

	private void isShareHolderChecked(boolean isShareHolder) {
		logger.debug("Entering");
		if (isShareHolder) {
			this.space_SharePerc.setSclass("mandatory");
			this.sharePerc.setReadonly(false);
			this.sharePerc.setValue(this.sharePerc.getValue());
		} else {
			this.space_SharePerc.setSclass("");
			this.sharePerc.setReadonly(true);
			this.sharePerc.setValue(BigDecimal.ZERO);
		}
		logger.debug("Leaving");
	}

	private void isDirectorChecked(boolean isDirector) {
		logger.debug("Entering");
		if (isDirector) {
			this.designation.setMandatoryStyle(true);
			this.designation.setButtonDisabled(isReadOnly("DirectorDetailDialog_designation"));
			this.designation.setReadonly(isReadOnly("DirectorDetailDialog_designation"));
			this.designation.setValue(this.designation.getValue(), this.designation.getDescription());
		} else {
			this.designation.setMandatoryStyle(false);
			this.designation.setButtonDisabled(true);
			this.designation.setReadonly(true);
			this.designation.setValue("", "");
		}
		logger.debug("Leaving");
	}

	public void onCheck$shareholder(Event event) {
		logger.debug("Entering");
		Clients.clearWrongValue(this.shareholder);
		Clients.clearWrongValue(this.sharePerc);
		isShareHolderChecked(this.shareholder.isChecked());
		logger.debug("Leaving");
	}

	public void onCheck$director(Event event) {
		logger.debug("Entering");
		Clients.clearWrongValue(this.shareholder);
		this.designation.setErrorMessage("");
		isDirectorChecked(this.director.isChecked());
		logger.debug("Leaving");
	}

	public void onCheck$shareholderCustomer(Event event) {
		logger.debug("Entering");
		if (this.shareholderCustomer.isChecked()) {
			this.label_DirectorDetailDialog_ShareholderCif.setVisible(true);
			this.shareHolderCustCIF.setVisible(true);
			this.btnSearchPRShareHolderCustid.setVisible(true);
			this.shareHolderCustShrtName.setVisible(true);
			this.space_ShareHolderCif.setVisible(true);
		} else {
			this.label_DirectorDetailDialog_ShareholderCif.setVisible(false);
			this.shareHolderCustCIF.setVisible(false);
			this.btnSearchPRShareHolderCustid.setVisible(false);
			this.shareHolderCustShrtName.setVisible(false);
			this.space_ShareHolderCif.setVisible(false);
			this.shareHolderCustCIF.setValue("");
			this.shareHolderCustShrtName.setValue("");
		}

		logger.debug("Leaving");
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(DirectorDetail aDirectorDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aDirectorDetail.getBefImage(), aDirectorDetail);
		return new AuditHeader(String.valueOf(aDirectorDetail.getDirectorId()),
				String.valueOf(aDirectorDetail.getCustID()), null, null, auditDetail, aDirectorDetail.getUserDetails(),
				getOverideMap());

	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_DirectorDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.directorDetail);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.directorDetail.getDirectorId());
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

	public DirectorDetail getDirectorDetail() {
		return this.directorDetail;
	}

	public void setDirectorDetail(DirectorDetail directorDetail) {
		this.directorDetail = directorDetail;
	}

	public void setDirectorDetailService(DirectorDetailService directorDetailService) {
		this.directorDetailService = directorDetailService;
	}

	public DirectorDetailService getDirectorDetailService() {
		return this.directorDetailService;
	}

	public void setDirectorDetailListCtrl(DirectorDetailListCtrl directorDetailListCtrl) {
		this.directorDetailListCtrl = directorDetailListCtrl;
	}

	public DirectorDetailListCtrl getDirectorDetailListCtrl() {
		return this.directorDetailListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setCustomerSelectCtrl(CustomerSelectCtrl customerSelectctrl) {
		this.customerSelectCtrl = customerSelectctrl;
	}

	public CustomerSelectCtrl getCustomerSelectCtrl() {
		return customerSelectCtrl;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

	public CustomerViewDialogCtrl getCustomerViewDialogCtrl() {
		return customerViewDialogCtrl;
	}

	public void setCustomerViewDialogCtrl(CustomerViewDialogCtrl customerViewDialogCtrl) {
		this.customerViewDialogCtrl = customerViewDialogCtrl;
	}

}
