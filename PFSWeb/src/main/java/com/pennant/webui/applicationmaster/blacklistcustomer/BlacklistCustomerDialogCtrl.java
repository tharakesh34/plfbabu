package com.pennant.webui.applicationmaster.blacklistcustomer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.blacklist.NegativeReasoncodes;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.applicationmaster.BlacklistCustomerService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class BlacklistCustomerDialogCtrl extends GFCBaseCtrl<BlackListCustomers> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(BlacklistCustomerDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BlacklistCustomerDialog; // autoWired

	protected Textbox custCIF; // autoWired
	protected Datebox custDOB; // autoWired
	protected Textbox custFName; // autoWired
	protected Textbox custLName; // autoWired
	protected Textbox custSName; // autoWired
	protected Textbox custEID; // autoWired
	protected Textbox custAadhar; // autoWired
	protected Textbox custCin; // autoWired
	protected Textbox reasonCode; // autoWired
	protected Button btnReasonCode; // autoWired
	// protected Textbox custPassport; // autoWired
	// protected Textbox custMobileNum; // autoWired
	// protected Textbox phoneCountryCode; // autoWired
	// protected Textbox phoneAreaCode; // autoWired
	protected Textbox custMobileNum; // autoWired
	protected Textbox source; // autoWired
	protected ExtendedCombobox employer; // autoWired
	protected ExtendedCombobox custNationality;
	protected Checkbox custIsActive;
	protected Combobox custCtgType;
	protected Space space_CustSName;
	protected Space space_CustFName;
	protected Space space_CustLName;
	protected Space space_CustCin;
	protected Space space_Gender;
	protected Label label_BlacklistCustomerDialog_CustDOB;

	private transient boolean validationOn;
	private boolean isRetailCustomer = false;
	private boolean isCorp = false;
	private final List<ValueLabel> custCtgCodeList = PennantAppUtil.getcustCtgCodeList();
	private final ArrayList<ValueLabel> custGenderCodeList = PennantAppUtil.getGenderCodes();

	// not auto wired Var's
	private BlackListCustomers blacklistCustomer;
	private transient BlacklistCustomerListCtrl blacklistCustomerListCtrl; // overHanded

	// ServiceDAOs / Domain Classes
	private transient BlacklistCustomerService blacklistCustomerService;
	private List<NegativeReasoncodes> negativeReasoncodes = null;

	protected Combobox custGenderCode;
	protected ExtendedCombobox custAddrType;
	protected Textbox custVid; // autoWired
	protected Textbox custDl; // autoWired
	protected Textbox ProductAppliedInFi; // autoWired
	protected Textbox CustForgedDocumentType; // autoWired
	protected Textbox custPassport; // autoWired
	protected ExtendedCombobox custAddrZIP; // autoWired
	protected Textbox custAddrStreet; // autoWired
	protected ExtendedCombobox custAddrCity; // autoWired
	protected ExtendedCombobox custAddrProvince; // autoWired
	protected ExtendedCombobox custAddrCountry; // autoWired
	protected Textbox custAddrHNbr; // autoWired
	protected Textbox custCmd; // autoWired
	public boolean validateAllDetails = true;
	protected ExtendedCombobox branch;
	protected Datebox additionalField0;
	protected Textbox additionalField1;
	protected Textbox additionalField2;
	protected Textbox additionalField3;
	protected Textbox additionalField4;
	protected Textbox additionalField5;
	protected Textbox additionalField6;
	protected Textbox additionalField7;
	protected Textbox additionalField8;
	protected Textbox additionalField9;
	protected Textbox additionalField10;
	protected Textbox additionalField11;
	protected Textbox additionalField12;
	protected Textbox additionalField13;
	protected Textbox additionalField14;
	protected Textbox address;
	private boolean enqiryModule;
	private String moduleCode;

	/**
	 * default constructor.<br>
	 */
	public BlacklistCustomerDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BlacklistCustomerDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Currency object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_BlacklistCustomerDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_BlacklistCustomerDialog);

		try {
			// READ OVERHANDED parameters !
			if (arguments.containsKey("blackListCustomer")) {
				this.blacklistCustomer = (BlackListCustomers) arguments.get("blackListCustomer");
				BlackListCustomers befImage = new BlackListCustomers();
				BeanUtils.copyProperties(this.blacklistCustomer, befImage);
				this.blacklistCustomer.setBefImage(befImage);

				setBlacklistCustomer(this.blacklistCustomer);
			} else {
				setBlacklistCustomer(null);
			}

			doLoadWorkFlow(this.blacklistCustomer.isWorkflow(), this.blacklistCustomer.getWorkflowId(),
					this.blacklistCustomer.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), super.pageRightName);
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED parameters !
			// we get the currencyListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete currency here.
			if (arguments.containsKey("blacklistCustomerListCtrl")) {
				setBlacklistCustomerListCtrl((BlacklistCustomerListCtrl) arguments.get("blacklistCustomerListCtrl"));
			} else {
				setBlacklistCustomerListCtrl(null);
			}

			if (arguments.containsKey("enqiryModule")) {
				enqiryModule = (boolean) arguments.get("enqiryModule");
			}
			if (arguments.containsKey("moduleCode")) {
				moduleCode = (String) arguments.get("moduleCode");
			}
			if (PennantConstants.BLACKLISTCUSTOMER.equals(moduleCode) && blacklistCustomer != null) {
				setBlacklistCustomer(blacklistCustomerService.getApprovedBlacklistById(blacklistCustomer.getCustCIF()));
			}
			// set Field Properties
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(getBlacklistCustomer());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_BlacklistCustomerDialog.onClose();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		// Empty sent any required attributes
		this.custCIF.setMaxlength(6);
		this.custDOB.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.custFName.setMaxlength(350);
		this.custLName.setMaxlength(350);
		this.custEID.setMaxlength(12);
		// this.custPassport.setMaxlength(20);
		this.custMobileNum.setMaxlength(10);
		this.custAadhar.setMaxlength(12);
		this.reasonCode.setDisabled(true);
		this.reasonCode.setMaxlength(100);
		this.custCin.setMaxlength(21);
		this.custSName.setMaxlength(100);
		this.source.setMaxlength(10);

		// Employer ExtendedCombobox
		this.employer.setInputAllowed(true);
		this.employer.setModuleName("EmployerDetail");
		this.employer.setValueColumn("EmpIndustry");
		this.employer.setDescColumn("EmpName");
		this.employer.setValidateColumns(new String[] { "EmpIndustry" });
		this.employer.setMaxlength(8);

		// custNationality ExtendedCombobox
		this.custNationality.setInputAllowed(true);
		this.custNationality.setMaxlength(3);
		this.custNationality.setModuleName("NationalityCode");
		this.custNationality.setValueColumn("NationalityCode");
		this.custNationality.setDescColumn("NationalityDesc");
		this.custNationality.setValidateColumns(new String[] { "NationalityCode" });

		this.custVid.setMaxlength(20);
		this.custDl.setMaxlength(20);
		this.ProductAppliedInFi.setMaxlength(100);
		this.CustForgedDocumentType.setMaxlength(100);
		this.custCmd.setMaxlength(8);
		this.custAddrStreet.setMaxlength(20);
		this.custAddrHNbr.setMaxlength(10);
		this.custCmd.setMaxlength(2000);
		this.custPassport.setMaxlength(8);

		this.custAddrZIP.setProperties("PinCode", "PinCode", "AreaName", false, 10);
		this.custAddrZIP.setValidateColumns(new String[] { "PinCode" });

		this.custAddrCountry.setProperties("Country", "CountryCode", "CountryDesc", false, 2);
		this.custAddrCountry.setValidateColumns(new String[] { "CountryCode" });

		this.custAddrProvince.setProperties("Province", "CPProvince", "CPProvinceName", false, 10);
		this.custAddrProvince.setValidateColumns(new String[] { "CPProvince" });

		this.custAddrCity.setProperties("City", "PCCity", "PCCityName", false, 10);
		this.custAddrCity.setValidateColumns(new String[] { "PCCity" });

		this.custAddrType.setProperties("AddressType", "AddrTypeCode", "AddrTypeDesc", false, 10);
		this.custAddrType.setValidateColumns(new String[] { "AddrTypeCode" });

		this.branch.setMaxlength(LengthConstants.LEN_BRANCH);
		this.branch.setModuleName("Branch");
		this.branch.setValueColumn("BranchCode");
		this.branch.setDescColumn("BranchDesc");
		this.branch.setValidateColumns(new String[] { "BranchCode" });

		this.additionalField0.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.additionalField1.setMaxlength(100);
		this.additionalField2.setMaxlength(100);
		this.additionalField3.setMaxlength(100);
		this.additionalField4.setMaxlength(100);
		this.additionalField5.setMaxlength(100);
		this.additionalField6.setMaxlength(100);
		this.additionalField7.setMaxlength(100);
		this.additionalField8.setMaxlength(100);
		this.additionalField9.setMaxlength(100);
		this.additionalField10.setMaxlength(100);
		this.additionalField11.setMaxlength(100);
		this.additionalField12.setMaxlength(100);
		this.additionalField13.setMaxlength(100);
		this.additionalField14.setMaxlength(100);

		this.address.setMaxlength(1800);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving ");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		if (!enqiryModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_blacklistCustomerList_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BlacklistCustomerDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BlacklistCustomerDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BlacklistCustomerDialog_btnSave"));
			this.btnCancel.setVisible(false);
		}
		logger.debug("Leaving ");
	}

	public void onChange$custCtgType(Event event) {
		doRemoveValidation();
		doClearMessage();
		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(getComboboxValue(custCtgType))) {
			setValuesForRetailCust();
		} else {
			setValuesForCorpCust();
		}
	}

	private void setValuesForCorpCust() {
		this.employer.setReadonly(true);
		this.employer.getButton().setDisabled(true);
		this.employer.setValue("");
		this.employer.setDescription("");
		this.custSName.setReadonly(isReadOnly("BlacklistCustomerDialog_CustSName"));
		this.custFName.setValue("");
		this.space_CustSName.setSclass("mandatory");
		this.custCin.setReadonly(isReadOnly("BlacklistCustomerDialog_CustCin"));
		this.custFName.setReadonly(true);
		this.space_CustFName.setSclass("");
		this.custLName.setReadonly(true);
		this.custLName.setValue("");
		this.space_CustLName.setSclass("");
		this.custAadhar.setReadonly(true);
		isRetailCustomer = false;
		isCorp = true;
		this.space_CustCin.setSclass("mandatory");
		label_BlacklistCustomerDialog_CustDOB
				.setValue(Labels.getLabel("label_BlacklistCustomerDialog_CustIncorp.value"));
		this.custGenderCode.setDisabled(true);
		fillComboBox(this.custGenderCode, PennantConstants.List_Select, custGenderCodeList, "");
		this.space_Gender.setSclass("");
	}

	private void setValuesForRetailCust() {
		this.custSName.setValue("");
		this.custSName.setReadonly(true);
		this.space_CustSName.setSclass("");
		this.custCin.setValue("");
		this.custCin.setReadonly(true);
		this.employer.setReadonly(isReadOnly("BlacklistCustomerDialog_Employer"));
		this.employer.getButton().setDisabled(false);
		this.custFName.setReadonly(isReadOnly("BlacklistCustomerDialog_CustFName"));
		this.space_CustFName.setSclass("mandatory");
		this.custLName.setReadonly(isReadOnly("BlacklistCustomerDialog_CustLName"));
		this.space_CustLName.setSclass("mandatory");
		this.space_CustCin.setSclass("");
		this.custAadhar.setReadonly(isReadOnly("BlacklistCustomerDialog_CustAadhaar"));
		isRetailCustomer = true;
		label_BlacklistCustomerDialog_CustDOB.setValue(Labels.getLabel("label_BlacklistCustomerDialog_CustDOB.value"));
		this.custGenderCode.setDisabled(false);
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
		MessageUtil.showHelpWindow(event, window_BlacklistCustomerDialog);
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
		logger.debug("Entering ");
		doWriteBeanToComponents(this.blacklistCustomer.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCurrency Currency
	 */
	public void doWriteBeanToComponents(BlackListCustomers aBlackListCustomers) {
		logger.debug("Entering ");

		this.custCIF.setValue(aBlackListCustomers.getCustCIF());
		this.custDOB.setValue(aBlackListCustomers.getCustDOB());
		this.custFName.setValue(aBlackListCustomers.getCustFName());
		fillComboBox(this.custCtgType, aBlackListCustomers.getCustCtgCode(), custCtgCodeList, "");
		fillComboBox(this.custGenderCode, aBlackListCustomers.getGender(), custGenderCodeList, "");

		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(getComboboxValue(this.custCtgType))) {
			setValuesForRetailCust();
		} else if (PennantConstants.PFF_CUSTCTG_CORP.equals(getComboboxValue(this.custCtgType))) {
			setValuesForCorpCust();
		}

		this.custLName.setValue(aBlackListCustomers.getCustLName());
		this.custEID.setValue(PennantApplicationUtil.formatEIDNumber(aBlackListCustomers.getCustCRCPR()));
		// this.custPassport.setValue(aBlackListCustomers.getCustPassportNo());
		this.custMobileNum.setValue(aBlackListCustomers.getMobileNumber());
		this.custNationality.setValue(aBlackListCustomers.getCustNationality());
		this.custNationality.setDescription(aBlackListCustomers.getLovDescNationalityDesc());
		this.custCin.setValue(aBlackListCustomers.getCustCin());
		this.custAadhar.setValue(aBlackListCustomers.getCustAadhaar());
		this.custAddrType.setValue(StringUtils.trimToEmpty(aBlackListCustomers.getAddressType()),
				StringUtils.trimToEmpty(aBlackListCustomers.getLovDescCustAddrTypeName()));
		this.custAddrCountry.setValue(StringUtils.trimToEmpty(aBlackListCustomers.getCountry()),
				StringUtils.trimToEmpty(aBlackListCustomers.getLovDescCustAddrCountryName()));
		this.custAddrProvince.setValue(StringUtils.trimToEmpty(aBlackListCustomers.getState()),
				StringUtils.trimToEmpty(aBlackListCustomers.getLovDescCustAddrProvinceName()));
		this.custAddrCity.setValue(StringUtils.trimToEmpty(aBlackListCustomers.getCity()),
				StringUtils.trimToEmpty(aBlackListCustomers.getLovDescCustAddrCityName()));
		this.custAddrZIP.setValue(StringUtils.trimToEmpty(aBlackListCustomers.getPincode()),
				StringUtils.trimToEmpty(aBlackListCustomers.getLovDescCustAddrZip()));
		this.custVid.setValue(aBlackListCustomers.getVid());
		this.custDl.setValue(aBlackListCustomers.getDl());
		this.ProductAppliedInFi.setValue(aBlackListCustomers.getProduct_Applied_In_Other_FI());
		this.CustForgedDocumentType.setValue(aBlackListCustomers.getForged_Document_Type());
		this.custPassport.setValue(aBlackListCustomers.getCustPassportNo());
		this.custAddrStreet.setValue(aBlackListCustomers.getStreet());
		this.custAddrHNbr.setValue(aBlackListCustomers.getHouseNumber());
		this.custCmd.setValue(aBlackListCustomers.getRemarks());
		this.custSName.setValue(aBlackListCustomers.getCustCompName());
		this.branch.setValue(StringUtils.trimToEmpty(aBlackListCustomers.getBranch()),
				StringUtils.trimToEmpty(aBlackListCustomers.getLovDescCustBranch()));
		this.additionalField0.setValue(aBlackListCustomers.getAdditionalField0());
		this.additionalField1.setValue(aBlackListCustomers.getAdditionalField1());
		this.additionalField2.setValue(aBlackListCustomers.getAdditionalField2());
		this.additionalField3.setValue(aBlackListCustomers.getAdditionalField3());
		this.additionalField4.setValue(aBlackListCustomers.getAdditionalField4());
		this.additionalField5.setValue(aBlackListCustomers.getAdditionalField5());
		this.additionalField6.setValue(aBlackListCustomers.getAdditionalField6());
		this.additionalField7.setValue(aBlackListCustomers.getAdditionalField7());
		this.additionalField8.setValue(aBlackListCustomers.getAdditionalField8());
		this.additionalField9.setValue(aBlackListCustomers.getAdditionalField9());
		this.additionalField10.setValue(aBlackListCustomers.getAdditionalField10());
		this.additionalField11.setValue(aBlackListCustomers.getAdditionalField11());
		this.additionalField12.setValue(aBlackListCustomers.getAdditionalField12());
		this.additionalField13.setValue(aBlackListCustomers.getAdditionalField13());
		this.additionalField14.setValue(aBlackListCustomers.getAdditionalField14());
		this.address.setValue(aBlackListCustomers.getAddress());
		if (!aBlackListCustomers.isNewRecord()) {
			this.employer.setValue(StringUtils.trimToEmpty(aBlackListCustomers.getEmpIndustry()),
					StringUtils.trimToEmpty(aBlackListCustomers.getLovDescEmpName()));
			this.employer.setAttribute("EmpId", aBlackListCustomers.getEmployer());
		}

		this.recordStatus.setValue(aBlackListCustomers.getRecordStatus());
		this.custIsActive.setChecked(aBlackListCustomers.isCustIsActive());
		this.reasonCode.setValue(aBlackListCustomers.getReasonCode());
		this.source.setValue(aBlackListCustomers.getSource());

		if (aBlackListCustomers.isNewRecord()
				|| (aBlackListCustomers.getRecordType() != null ? aBlackListCustomers.getRecordType() : "")
						.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.custIsActive.setChecked(true);
			this.custIsActive.setDisabled(true);

			if (CollectionUtils.isNotEmpty(aBlackListCustomers.getNegativeReasoncodeList())) {
				doFillNegativeReasoncodes(aBlackListCustomers.getNegativeReasoncodeList());
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCurrency
	 */
	public void doWriteComponentsToBean(BlackListCustomers finBlacklistCust) {
		logger.debug("Entering ");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			finBlacklistCust.setReasonCode(this.reasonCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setSource(this.source.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setCustCIF(this.custCIF.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setCustDOB(this.custDOB.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setCustFName(this.custFName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finBlacklistCust.setCustLName(this.custLName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finBlacklistCust.setCustCompName(this.custSName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finBlacklistCust.setCustCRCPR((this.custEID.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finBlacklistCust.setCustAadhaar(this.custAadhar.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finBlacklistCust.setCustCtgCode((this.custCtgType.getSelectedItem().getValue().toString()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setCustCin(this.custCin.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setMobileNumber(this.custMobileNum.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setCustNationality(this.custNationality.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setLovDescNationalityDesc(this.custNationality.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setAddressType(this.custAddrType.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setPincode(this.custAddrZIP.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setCity(this.custAddrCity.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setState(this.custAddrProvince.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setCountry(this.custAddrCountry.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.custGenderCode.isDisabled()
					&& PennantConstants.List_Select.equals(getComboboxValue(this.custGenderCode))) {
				if (isRetailCustomer && validateAllDetails && this.custGenderCode.isVisible()
						&& !this.custGenderCode.isDisabled()) {
					throw new WrongValueException(this.custGenderCode, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_CustomerBlackListDialog_CustGenderCode.value") }));
				} else {
					finBlacklistCust.setGender(null);
				}
			} else {
				finBlacklistCust.setGender(getComboboxValue(this.custGenderCode));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setVid(this.custVid.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setDl(this.custDl.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setProduct_Applied_In_Other_FI(this.ProductAppliedInFi.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setForged_Document_Type(this.CustForgedDocumentType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setStreet(this.custAddrStreet.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setHouseNumber(this.custAddrHNbr.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setRemarks(this.custCmd.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finBlacklistCust.setCustPassportNo(this.custPassport.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.employer.getValidatedValue();
			Object object = this.employer.getAttribute("EmpId");
			if (object != null) {
				finBlacklistCust.setEmployer((Long.parseLong(object.toString())));
			} else {
				finBlacklistCust.setEmployer(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setLovDescEmpName(this.employer.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setCustIsActive(this.custIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setBranch(this.branch.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setAdditionalField0(this.additionalField0.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setAdditionalField1(this.additionalField1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setAdditionalField2(this.additionalField2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setAdditionalField3(this.additionalField3.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setAdditionalField4(this.additionalField4.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setAdditionalField5(this.additionalField5.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setAdditionalField6(this.additionalField6.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setAdditionalField7(this.additionalField7.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setAdditionalField8(this.additionalField8.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finBlacklistCust.setAdditionalField9(this.additionalField9.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setAdditionalField10(this.additionalField10.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setAdditionalField11(this.additionalField11.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setAdditionalField12(this.additionalField12.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setAdditionalField13(this.additionalField13.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setAddress(this.address.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finBlacklistCust.setAdditionalField14(this.additionalField14.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		finBlacklistCust.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving ");
	}

	public void onChange$custEID(Event event) {
		logger.debug("Entering");
		this.custEID.setValue(PennantApplicationUtil.formatEIDNumber(this.custEID.getValue()));
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCurrency
	 * @throws Exception
	 */
	public void doShowDialog(BlackListCustomers aFinBlacklistCust) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFinBlacklistCust.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			this.custCIF.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aFinBlacklistCust.getRecordType())) {
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
			doWriteBeanToComponents(aFinBlacklistCust);
			if (PennantConstants.BLACKLISTCUSTOMER.equals(moduleCode) && enqiryModule) {
				this.btnCtrl.setBtnStatus_Enquiry();
				doReadOnly();
				setDialog(DialogType.MODAL);
			} else {
				setDialog(DialogType.EMBEDDED);
			}
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_BlacklistCustomerDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving ");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		Date appStartDate = SysParamUtil.getAppDate();
		Date startDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");
		setValidationOn(true);

		if (!this.custCIF.isReadonly()) {
			this.custCIF
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_CustCIF.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.custDOB.isReadonly()) {
			this.custDOB
					.setConstraint(new PTDateValidator(Labels.getLabel("label_BlacklistCustomerDialog_CustDOB.value"),
							false, startDate, appStartDate, false));
		}

		if (!this.custAddrHNbr.isReadonly()) {
			this.custAddrHNbr.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerBlackListDialog_CustAddrHNbr.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
		}
		if (!this.custPassport.isReadonly()) {
			this.custPassport.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_CustPassport.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
		}
		if (!this.custVid.isReadonly()) {
			this.custVid
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_CustVid.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
		}
		if (!this.custDl.isReadonly()) {
			this.custDl
					.setConstraint(new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_CustDl.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
		}
		if (!this.custAddrStreet.isReadonly()) {
			this.custAddrStreet.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerBlackListDialog_CustAddrStreet.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
		}

		if (!this.ProductAppliedInFi.isReadonly()) {
			this.ProductAppliedInFi.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_ProductAppliedInFi.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}

		if (!this.CustForgedDocumentType.isReadonly()) {
			this.CustForgedDocumentType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_CustForgedDocumentType.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}
		if (!this.custAadhar.isReadonly()) {
			this.custAadhar.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_CustAadhar.value"),
							PennantRegularExpressions.REGEX_AADHAR_NUMBER, false));
		}
		if (!this.custMobileNum.isReadonly()) {
			this.custMobileNum.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_CustMobileNum.value"),
							PennantRegularExpressions.REGEX_NUMERIC, false));
		}

		if (isRetailCustomer) {
			if (!this.custFName.isReadonly()) {
				this.custFName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_CustFName.value"),
								PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_CHAR, true));
			}
			if (!this.custLName.isReadonly()) {
				this.custLName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_CustLName.value"),
								PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_CHAR, true));
			}
		} else {
			if (!this.custFName.isReadonly()) {
				this.custFName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_CustFName.value"),
								PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_CHAR, true));
			}
			if (!this.custLName.isReadonly()) {
				this.custLName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_CustLName.value"),
								PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_CHAR, true));
			}

			if (!this.custSName.isReadonly()) {
				this.custSName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_CustSName.value"),
								PennantRegularExpressions.REGEX_NAME, true));
			}

			if (isCorp) {
				if (!this.custCin.isReadonly()) {
					this.custCin.setConstraint(
							new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_CustCin.value"),
									PennantRegularExpressions.REGEX_ALPHANUM, true));
				}
			}

		}

		if (!this.custEID.isReadonly()) {
			if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
				this.custEID.setConstraint(
						new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_CustEID.value"),
								PennantRegularExpressions.REGEX_PASSPORT, false));
			} else {
				this.custEID.setConstraint(
						new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_CustEID.value"),
								PennantRegularExpressions.REGEX_PANNUMBER, false));
			}

		}

		if (!this.custCtgType.isDisabled()) {
			this.custCtgType.setConstraint(new StaticListValidator(custCtgCodeList,
					Labels.getLabel("label_BlacklistCustomerDialog_CustCtgType.value")));
		}

		if (!this.custMobileNum.isReadonly()) {
			this.custMobileNum.setConstraint(new PTMobileNumberValidator(
					Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneNumber.value"), false));
		}
		if (!this.custNationality.isReadonly()) {
			this.custNationality.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_CustNationality.value"),
							PennantRegularExpressions.REGEX_ALPHA, false));
		}
		if (!this.employer.isReadonly()) {
			this.employer.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_Employer.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, false));
		}

		if (!this.reasonCode.isReadonly()) {
			this.reasonCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_BlacklistCustomerDialog_ReasonCode.value"), null, false));
		}

		if (!this.source.isReadonly()) {
			this.source.setConstraint(
					new PTStringValidator(Labels.getLabel("label_BlacklistCustomerDialog_Source.value"), null, false));
		}

		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");

		setValidationOn(false);

		this.reasonCode.setConstraint("");
		this.custCIF.setConstraint("");
		this.custDOB.setConstraint("");
		this.custFName.setConstraint("");
		this.custLName.setConstraint("");
		this.custEID.setConstraint("");
		this.custMobileNum.setConstraint("");
		this.custNationality.setConstraint("");
		this.employer.setConstraint("");
		this.custSName.setConstraint("");
		this.custCin.setConstraint("");
		this.custGenderCode.setConstraint("");
		this.custAddrType.setConstraint("");
		this.custVid.setConstraint("");
		this.custDl.setConstraint("");
		this.ProductAppliedInFi.setConstraint("");
		this.CustForgedDocumentType.setConstraint("");
		this.custPassport.setConstraint("");
		this.custAddrZIP.setConstraint("");
		this.custAddrStreet.setConstraint("");
		this.custAddrCity.setConstraint("");
		this.custAddrProvince.setConstraint("");
		this.custAddrCountry.setConstraint("");
		this.custAddrHNbr.setConstraint("");
		this.custCmd.setConstraint("");
		this.source.setConstraint("");
		this.address.setConstraint("");
		this.custAadhar.setConstraint("");

		logger.debug("Leaving ");
	}

	/**
	 * Removes the Validation by setting the accordingly constraints to the LOVfields.
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.custNationality.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");

		this.custCIF.setErrorMessage("");
		this.custDOB.setErrorMessage("");
		this.custFName.setErrorMessage("");
		this.custLName.setErrorMessage("");
		this.custEID.setErrorMessage("");
		this.custMobileNum.setErrorMessage("");
		this.custNationality.setErrorMessage("");
		this.employer.setErrorMessage("");
		this.custSName.setErrorMessage("");
		this.custCin.setErrorMessage("");
		this.custGenderCode.setErrorMessage("");
		this.custAddrType.setErrorMessage("");
		this.custVid.setErrorMessage("");
		this.custDl.setErrorMessage("");
		this.ProductAppliedInFi.setErrorMessage("");
		this.CustForgedDocumentType.setErrorMessage("");
		this.custPassport.setErrorMessage("");
		this.custAddrZIP.setErrorMessage("");
		this.custAddrStreet.setErrorMessage("");
		this.custAddrCity.setErrorMessage("");
		this.custAddrProvince.setErrorMessage("");
		this.custAddrCountry.setErrorMessage("");
		this.custAddrHNbr.setErrorMessage("");
		this.custCmd.setErrorMessage("");
		this.address.setErrorMessage("");
		this.custAadhar.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getBlacklistCustomerListCtrl().search();
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final BlackListCustomers aBlackListCustomers = new BlackListCustomers();
		BeanUtils.copyProperties(getBlacklistCustomer(), aBlackListCustomers);

		doDelete(Labels.getLabel("label_BlacklistCustomerDialog_CustCIF.value") + " : "
				+ aBlackListCustomers.getCustCIF(), aBlackListCustomers);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");

		if (getBlacklistCustomer().isNewRecord()) {
			this.custCIF.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.custCIF.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.custDOB.setDisabled(isReadOnly("BlacklistCustomerDialog_CustDOB"));
		this.custFName.setReadonly(isReadOnly("BlacklistCustomerDialog_CustFName"));
		this.custLName.setReadonly(isReadOnly("BlacklistCustomerDialog_CustLName"));
		this.custEID.setReadonly(isReadOnly("BlacklistCustomerDialog_CustEID"));
		this.custMobileNum.setReadonly(isReadOnly("BlacklistCustomerDialog_CustMobileNum"));
		this.custNationality.setReadonly(isReadOnly("BlacklistCustomerDialog_CustNationality"));
		this.employer.setReadonly(isReadOnly("BlacklistCustomerDialog_Employer"));
		this.custIsActive.setDisabled(isReadOnly("BlacklistCustomerDialog_CustIsActive"));
		this.custSName.setReadonly(isReadOnly("BlacklistCustomerDialog_CustSName"));
		this.custCin.setReadonly(isReadOnly("BlacklistCustomerDialog_CustEID"));
		this.custAadhar.setReadonly(isReadOnly("BlacklistCustomerDialog_CustAadhaar"));

		this.custAddrType.setReadonly(isReadOnly("BlacklistCustomerDialog_CustAddrType"));
		this.custVid.setReadonly(isReadOnly("BlacklistCustomerDialog_CustVid"));
		this.custDl.setReadonly(isReadOnly("BlacklistCustomerDialog_CustDl"));
		this.ProductAppliedInFi.setReadonly(isReadOnly("BlacklistCustomerDialog_CustProductAppliedInFi"));
		this.CustForgedDocumentType.setReadonly(isReadOnly("BlacklistCustomerDialog_CustForgedDocumentType"));
		this.custPassport.setReadonly(isReadOnly("BlacklistCustomerDialog_CustPassport"));
		this.custAddrZIP.setReadonly(isReadOnly("BlacklistCustomerDialog_CustAddrZIP"));
		this.custAddrStreet.setReadonly(isReadOnly("BlacklistCustomerDialog_CustAddrStreet"));
		this.custAddrCity.setReadonly(isReadOnly("BlacklistCustomerDialog_CustAddrCity"));
		this.custAddrProvince.setReadonly(isReadOnly("BlacklistCustomerDialog_CustProvince"));
		this.custAddrCountry.setReadonly(isReadOnly("BlacklistCustomerDialog_CustAddrCountry"));
		this.custAddrHNbr.setReadonly(isReadOnly("BlacklistCustomerDialog_CustAddrHNbr"));
		this.custCmd.setReadonly(isReadOnly("BlacklistCustomerDialog_CustCmd"));

		readOnlyComponent(isReadOnly("BlacklistCustomerDialog_CustCtgCode"), this.custCtgType);
		readOnlyComponent(isReadOnly("BlacklistCustomerDialog_ReasonCode"), this.btnReasonCode);
		readOnlyComponent(isReadOnly("BlacklistCustomerDialog_custGenderCode"), this.custGenderCode);
		this.source.setReadonly(isReadOnly("BlacklistCustomerDialog_Source"));

		this.branch.setReadonly(isReadOnly("BlacklistCustomerDialog_Branch"));
		this.additionalField0.setDisabled(isReadOnly("BlacklistCustomerDialog_AdditionalDateField"));
		this.additionalField1.setReadonly(isReadOnly("BlacklistCustomerDialog_AdditionalField1"));
		this.additionalField2.setReadonly(isReadOnly("BlacklistCustomerDialog_AdditionalField2"));
		this.additionalField3.setReadonly(isReadOnly("BlacklistCustomerDialog_AdditionalField3"));
		this.additionalField4.setReadonly(isReadOnly("BlacklistCustomerDialog_AdditionalField4"));
		this.additionalField5.setReadonly(isReadOnly("BlacklistCustomerDialog_AdditionalField5"));
		this.additionalField6.setReadonly(isReadOnly("BlacklistCustomerDialog_AdditionalField6"));
		this.additionalField7.setReadonly(isReadOnly("BlacklistCustomerDialog_AdditionalField7"));
		this.additionalField8.setReadonly(isReadOnly("BlacklistCustomerDialog_AdditionalField8"));
		this.additionalField9.setReadonly(isReadOnly("BlacklistCustomerDialog_AdditionalField9"));
		this.additionalField10.setReadonly(isReadOnly("BlacklistCustomerDialog_AdditionalField10"));
		this.additionalField11.setReadonly(isReadOnly("BlacklistCustomerDialog_AdditionalField11"));
		this.additionalField12.setReadonly(isReadOnly("BlacklistCustomerDialog_AdditionalField12"));
		this.additionalField13.setReadonly(isReadOnly("BlacklistCustomerDialog_AdditionalField13"));
		this.additionalField14.setReadonly(isReadOnly("BlacklistCustomerDialog_AdditionalField14"));
		this.address.setReadonly(isReadOnly("BlacklistCustomerDialog_Address"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.blacklistCustomer.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		this.custCtgType.setDisabled(true);
		this.custCIF.setReadonly(true);
		this.custDOB.setDisabled(true);
		this.custFName.setReadonly(true);
		this.custLName.setReadonly(true);
		this.custEID.setReadonly(true);
		this.custMobileNum.setReadonly(true);
		this.custNationality.setReadonly(true);
		this.employer.setReadonly(true);
		this.custIsActive.setDisabled(true);
		this.reasonCode.setDisabled(true);
		this.custGenderCode.setDisabled(true);
		this.custAddrType.setReadonly(true);
		this.custVid.setReadonly(true);
		this.custDl.setReadonly(true);
		this.ProductAppliedInFi.setReadonly(true);
		this.CustForgedDocumentType.setReadonly(true);
		this.custPassport.setReadonly(true);
		this.custAddrZIP.setReadonly(true);
		this.custAddrStreet.setReadonly(true);
		this.custAddrCity.setReadonly(true);
		this.custAddrProvince.setReadonly(true);
		this.custAddrCountry.setReadonly(true);
		this.custAddrHNbr.setReadonly(true);
		this.custCmd.setReadonly(true);
		this.source.setReadonly(true);
		this.branch.setReadonly(true);
		this.additionalField0.setDisabled(true);
		this.additionalField1.setReadonly(true);
		this.additionalField2.setReadonly(true);
		this.additionalField3.setReadonly(true);
		this.additionalField4.setReadonly(true);
		this.additionalField5.setReadonly(true);
		this.additionalField6.setReadonly(true);
		this.additionalField7.setReadonly(true);
		this.additionalField8.setReadonly(true);
		this.additionalField9.setReadonly(true);
		this.additionalField10.setReadonly(true);
		this.additionalField11.setReadonly(true);
		this.additionalField12.setReadonly(true);
		this.additionalField13.setReadonly(true);
		this.address.setReadonly(true);
		this.btnReasonCode.setDisabled(true);
		this.custAadhar.setReadonly(true);
		this.custCin.setReadonly(true);
		this.custSName.setReadonly(true);
		this.additionalField14.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering ");
		// remove validation, if there are a save before

		this.custCIF.setValue("");
		this.custDOB.setText("");
		this.custFName.setValue("");
		this.custLName.setValue("");
		this.custEID.setText("");
		this.custMobileNum.setValue("");
		this.custNationality.setValue("");
		this.employer.setValue("");
		this.custIsActive.setChecked(false);
		this.reasonCode.setValue("");
		this.custGenderCode.setValue("");
		this.custAddrType.setValue("");
		this.custVid.setValue("");
		this.custDl.setValue("");
		this.ProductAppliedInFi.setValue("");
		this.CustForgedDocumentType.setValue("");
		this.custPassport.setValue("");
		this.custAddrZIP.setValue("");
		this.custAddrStreet.setValue("");
		this.custAddrCity.setValue("");
		this.custAddrProvince.setValue("");
		this.custAddrCountry.setValue("");
		this.custAddrHNbr.setValue("");
		this.custCmd.setValue("");
		this.custAadhar.setValue("");
		this.address.setValue("");

		logger.debug("Leaving ");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		final BlackListCustomers aFinBlklistCust = new BlackListCustomers();
		BeanUtils.copyProperties(getBlacklistCustomer(), aFinBlklistCust);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doClearMessage();
		doSetValidation();
		// fill the Currency object with the components data
		doWriteComponentsToBean(aFinBlklistCust);
		assignNegativeReasoncodes();

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		if (CollectionUtils.isNotEmpty(getNegativeReasoncodes())) {
			aFinBlklistCust.setNegativeReasoncodeList(getNegativeReasoncodes());
		} else {
			aFinBlklistCust.setNegativeReasoncodeList(null);
		}

		isNew = aFinBlklistCust.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinBlklistCust.getRecordType())) {
				aFinBlklistCust.setVersion(aFinBlklistCust.getVersion() + 1);
				if (isNew) {
					aFinBlklistCust.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinBlklistCust.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinBlklistCust.setNewRecord(true);
				}
			}
		} else {
			aFinBlklistCust.setVersion(aFinBlklistCust.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aFinBlklistCust, tranType)) {
				refreshList();
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
	 * @param aCurrency (Currency)
	 * 
	 * @param tranType  (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(BlackListCustomers aFinBlacklistCust, String tranType) {
		logger.debug("Entering ");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		aFinBlacklistCust.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinBlacklistCust.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinBlacklistCust.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aFinBlacklistCust.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinBlacklistCust.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinBlacklistCust);
				}

				if (isNotesMandatory(taskId, aFinBlacklistCust)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
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

			aFinBlacklistCust.setTaskId(taskId);
			aFinBlacklistCust.setNextTaskId(nextTaskId);
			aFinBlacklistCust.setRoleCode(getRole());
			aFinBlacklistCust.setNextRoleCode(nextRoleCode);

			if (CollectionUtils.isNotEmpty(aFinBlacklistCust.getNegativeReasoncodeList())) {
				for (NegativeReasoncodes details : aFinBlacklistCust.getNegativeReasoncodeList()) {
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setUserDetails(getUserWorkspace().getLoggedInUser());
					details.setRecordStatus(aFinBlacklistCust.getRecordStatus());
					details.setWorkflowId(aFinBlacklistCust.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aFinBlacklistCust.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aFinBlacklistCust.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			auditHeader = getAuditHeader(aFinBlacklistCust, tranType);

			String operationRefs = getServiceOperations(taskId, aFinBlacklistCust);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinBlacklistCust, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinBlacklistCust, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving ");
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
		logger.debug("Entering ");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		BlackListCustomers aFinBlklistCust = (BlackListCustomers) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getBlacklistCustomerService().delete(auditHeader);

						deleteNotes = true;
					} else {
						auditHeader = getBlacklistCustomerService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getBlacklistCustomerService().doApprove(auditHeader);

						if (aFinBlklistCust.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getBlacklistCustomerService().doReject(auditHeader);
						if (aFinBlklistCust.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_BlacklistCustomerDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(this.window_BlacklistCustomerDialog, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.blacklistCustomer), true);
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
		logger.debug("Leaving ");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aBlackListCustomers
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(BlackListCustomers aBlackListCustomers, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBlackListCustomers.getBefImage(), aBlackListCustomers);
		return new AuditHeader(String.valueOf(aBlackListCustomers.getId()), null, null, null, auditDetail,
				aBlackListCustomers.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_BlacklistCustomerDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	public void onFulfill$employer(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = employer.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.employer.setValue("");
			this.employer.setDescription("");
			this.employer.setAttribute("EmpId", null);
		} else {
			EmployerDetail details = (EmployerDetail) dataObject;
			if (details != null) {
				this.employer.setAttribute("EmpId", details.getId());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnReasonCode(Event event) {
		logger.debug(Literal.ENTERING);
		this.reasonCode.setMaxlength(100);
		this.reasonCode.setErrorMessage("");

		Filter[] reasonCategoryFilters = new Filter[1];
		reasonCategoryFilters[0] = new Filter("REASONCATEGORYCODE", "NGTVELST", Filter.OP_EQUAL);
		Object dataObject = MultiSelectionSearchListBox.show(this.window_BlacklistCustomerDialog, "ReasonCode",
				this.reasonCode.getValue(), reasonCategoryFilters);
		if (dataObject instanceof String) {
			this.reasonCode.setValue(dataObject.toString());
		} else {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> details = (HashMap<String, Object>) dataObject;
			if (details != null) {
				String tempflagcode = "";
				List<String> flagKeys = new ArrayList<>(details.keySet());
				for (int i = 0; i < flagKeys.size(); i++) {
					if (StringUtils.isEmpty(flagKeys.get(i))) {
						continue;
					}
					if (i == 0) {
						tempflagcode = flagKeys.get(i);
					} else {
						tempflagcode = tempflagcode + "," + flagKeys.get(i);
					}
				}
				this.reasonCode.setValue(tempflagcode);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void doFillNegativeReasoncodes(List<NegativeReasoncodes> negativeReasoncodes) {
		logger.debug(Literal.LEAVING);

		setNegativeReasoncodes(negativeReasoncodes);
		if (negativeReasoncodes == null || negativeReasoncodes.isEmpty()) {
			return;
		}

		String tempflagcode = "";

		for (NegativeReasoncodes reasoncodes : negativeReasoncodes) {
			if (StringUtils.isEmpty(tempflagcode)) {
				tempflagcode = reasoncodes.getReasonId().toString();
			} else {
				tempflagcode = tempflagcode.concat(",").concat(reasoncodes.getReasonId().toString());
			}
		}
		this.reasonCode.setValue(tempflagcode);

		if (StringUtils.equals(blacklistCustomer.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
			this.btnReasonCode.setDisabled(true);
		}

		logger.debug(Literal.LEAVING);
	}

	private void assignNegativeReasoncodes() {

		logger.debug("Entering");

		this.negativeReasoncodes = new ArrayList<>();
		List<String> reasonIdList = Arrays.asList(this.reasonCode.getValue().split(","));

		for (String reasonId : reasonIdList) {

			if (StringUtils.isEmpty(reasonId)) {
				continue;
			}

			NegativeReasoncodes mapping = new NegativeReasoncodes();
			mapping.setReasonId(Long.parseLong(reasonId));
			mapping.setNewRecord(true);
			mapping.setVersion(1);
			mapping.setRecordType(PennantConstants.RCD_ADD);

			this.negativeReasoncodes.add(mapping);
		}
	}

	/**
	 * on fulfill custAddrProvince
	 * 
	 * @param event
	 */

	public void onFulfill$custAddrProvince(Event event) {
		logger.debug("Entering");

		Object dataObject = custAddrProvince.getObject();
		String pcProvince = null;
		if (dataObject instanceof String) {
			fillPindetails(null, null);
		} else {
			Province province = (Province) dataObject;
			if (province == null) {
				fillPindetails(null, null);
			}
			if (province != null) {
				this.custAddrProvince.setErrorMessage("");
				pcProvince = this.custAddrProvince.getValue();
				fillPindetails(null, pcProvince);
			}
		}

		this.custAddrCity.setObject("");
		this.custAddrZIP.setObject("");
		this.custAddrCity.setValue("");
		this.custAddrCity.setDescription("");
		this.custAddrZIP.setValue("");
		this.custAddrZIP.setDescription("");
		fillCitydetails(pcProvince);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * based on state param ,city will be filtered
	 * 
	 * @param state
	 */
	private void fillCitydetails(String state) {
		logger.debug("Entering");

		this.custAddrCity.setModuleName("City");
		this.custAddrCity.setValueColumn("PCCity");
		this.custAddrCity.setDescColumn("PCCityName");
		this.custAddrCity.setValidateColumns(new String[] { "PCCity" });
		Filter[] filters = new Filter[2];

		if (state == null) {
			filters[0] = new Filter("PCProvince", null, Filter.OP_NOT_EQUAL);
		} else {
			filters[0] = new Filter("PCProvince", state, Filter.OP_EQUAL);
		}

		filters[1] = new Filter("CITYISACTIVE", 1, Filter.OP_EQUAL);
		this.custAddrCity.setFilters(filters);

		logger.debug("Leaving");
	}

	/**
	 * onFulfill custAddrCity
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$custAddrCity(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = custAddrCity.getObject();

		String cityValue = null;
		if (dataObject instanceof String) {
			this.custAddrCity.setValue("");
			this.custAddrCity.setDescription("");
			fillPindetails(null, null);
		} else {
			City city = (City) dataObject;
			if (city != null) {
				this.custAddrCity.setErrorMessage("");
				this.custAddrProvince.setErrorMessage("");

				this.custAddrProvince.setValue(city.getPCProvince());
				this.custAddrProvince.setDescription(city.getLovDescPCProvinceName());
				cityValue = this.custAddrCity.getValue();
			}
		}

		fillPindetails(cityValue, this.custAddrProvince.getValue());

		this.custAddrZIP.setObject("");
		this.custAddrZIP.setValue("");
		this.custAddrZIP.setDescription("");

		Filter[] filters = null;
		if (StringUtils.isNotBlank(custAddrProvince.getValue())) {
			filters = new Filter[2];
			filters[1] = new Filter("CITYISACTIVE", 1, Filter.OP_EQUAL);
			filters[0] = new Filter("PCProvince", custAddrProvince.getValue(), Filter.OP_EQUAL);
		} else {
			filters = new Filter[1];
			filters[0] = new Filter("CITYISACTIVE", 1, Filter.OP_EQUAL);
		}

		this.custAddrCity.setFilters(filters);

		logger.debug("Leaving");
	}

	/**
	 * based on param values,custaddrzip is filtered
	 * 
	 * @param cityValue
	 * @param provice
	 */

	private void fillPindetails(String cityValue, String provice) {
		logger.debug("Entering");

		this.custAddrZIP.setModuleName("PinCode");
		this.custAddrZIP.setValueColumn("PinCode");
		this.custAddrZIP.setDescColumn("AreaName");
		this.custAddrZIP.setValidateColumns(new String[] { "PinCode" });
		Filter[] filters = new Filter[2];

		if (cityValue != null) {
			filters[0] = new Filter("City", cityValue, Filter.OP_EQUAL);
		} else if (provice != null && !provice.isEmpty()) {
			filters[0] = new Filter("PCProvince", provice, Filter.OP_EQUAL);
		} else {
			filters[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}

		filters[1] = new Filter("Active", 1, Filter.OP_EQUAL);
		this.custAddrZIP.setFilters(filters);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * onFulfill custAddrZip.based on custAddrZip,custAddrCity and custAddrprovince will auto populate
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$custAddrZIP(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = custAddrZIP.getObject();
		if (dataObject instanceof String) {
			this.custAddrZIP.setValue("");
			this.custAddrZIP.setDescription("");
		} else {
			PinCode pinCode = (PinCode) dataObject;
			if (pinCode != null) {
				this.custAddrCity.setValue(pinCode.getCity());
				this.custAddrCity.setDescription(pinCode.getPCCityName());
				this.custAddrProvince.setValue(pinCode.getPCProvince());
				this.custAddrProvince.setDescription(pinCode.getLovDescPCProvinceName());
				this.custAddrCountry.setValue(pinCode.getpCCountry());
				this.custAddrCountry.setDescription(pinCode.getLovDescPCCountryName());

				this.custAddrCity.setErrorMessage("");
				this.custAddrProvince.setErrorMessage("");
				this.custAddrZIP.setErrorMessage("");
			}
		}

		logger.debug("Leaving");
	}

	public void onFulfill$custAddrCountry(Event event) {
		logger.debug("Entering");

		Object dataObject = custAddrCountry.getObject();
		String pcCountry = null;
		if (dataObject instanceof String) {
			fillPindetails(null, null);
		} else {
			Country country = (Country) dataObject;
			if (country == null) {
				fillPindetails(null, null);
			}
			if (country != null) {
				this.custAddrCountry.setErrorMessage("");
				pcCountry = this.custAddrCountry.getValue();
				fillPindetails(null, pcCountry);
			}
		}
		this.custAddrProvince.setObject("");
		this.custAddrProvince.setValue("");
		this.custAddrProvince.setDescription("");
		this.custAddrCity.setObject("");
		this.custAddrZIP.setObject("");
		this.custAddrCity.setValue("");
		this.custAddrCity.setDescription("");
		this.custAddrZIP.setValue("");
		this.custAddrZIP.setDescription("");
		fillProvincedetails(pcCountry);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * based on state param ,city will be filtered
	 * 
	 * @param state
	 */
	private void fillProvincedetails(String state) {
		logger.debug("Entering");

		this.custAddrProvince.setModuleName("Province");
		this.custAddrProvince.setValueColumn("CPProvince");
		this.custAddrProvince.setDescColumn("CPProvinceName");
		this.custAddrProvince.setValidateColumns(new String[] { "CPProvince" });
		Filter[] filters = new Filter[1];

		if (state == null) {
			filters[0] = new Filter("CPCountry", null, Filter.OP_NOT_EQUAL);
		} else {
			filters[0] = new Filter("CPCountry", state, Filter.OP_EQUAL);
		}
		this.custAddrProvince.setFilters(filters);

		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.blacklistCustomer);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.blacklistCustomer.getCustCIF());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public BlackListCustomers getBlacklistCustomer() {
		return blacklistCustomer;
	}

	public void setBlacklistCustomer(BlackListCustomers blacklistCustomer) {
		this.blacklistCustomer = blacklistCustomer;
	}

	public BlacklistCustomerListCtrl getBlacklistCustomerListCtrl() {
		return blacklistCustomerListCtrl;
	}

	public void setBlacklistCustomerListCtrl(BlacklistCustomerListCtrl blacklistCustomerListCtrl) {
		this.blacklistCustomerListCtrl = blacklistCustomerListCtrl;
	}

	public BlacklistCustomerService getBlacklistCustomerService() {
		return blacklistCustomerService;
	}

	public void setBlacklistCustomerService(BlacklistCustomerService blacklistCustomerService) {
		this.blacklistCustomerService = blacklistCustomerService;
	}

	public List<NegativeReasoncodes> getNegativeReasoncodes() {
		return negativeReasoncodes;
	}

	public void setNegativeReasoncodes(List<NegativeReasoncodes> negativeReasoncodes) {
		this.negativeReasoncodes = negativeReasoncodes;
	}

}
