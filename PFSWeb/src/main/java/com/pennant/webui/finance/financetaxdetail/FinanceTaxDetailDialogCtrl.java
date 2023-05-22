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
 * * FileName : FinanceTaxDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 17-06-2017 * *
 * Modified Date : 17-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 17-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financetaxdetail;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.North;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.GSTDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.finance.financetaxdetail.GSTINInfo;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.customermasters.GSTDetailService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.service.gstn.validation.GSTNValidationService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.JointAccountDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.web.util.ComponentUtil;

/**
 * This is the controller class for the /WEB-INF/pages/tax/FinanceTaxDetail/financeTaxDetailDialog.zul file. <br>
 */
public class FinanceTaxDetailDialogCtrl extends GFCBaseCtrl<FinanceTaxDetail> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FinanceTaxDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinanceTaxDetailDialog;
	protected Combobox applicableFor;
	protected ExtendedCombobox custRef;
	protected ExtendedCombobox addressDetail;
	protected ExtendedCombobox custTaxNumber;
	protected long taxCustId;
	protected Checkbox taxExempted;
	protected Uppercasebox taxNumber;
	protected Space space_taxNumber;
	protected Textbox addrLine1;
	protected Textbox addrLine2;
	protected Textbox addrLine3;
	protected Textbox addrLine4;
	protected Textbox sezCertificateNo;
	protected Datebox sezValueDate;
	protected Space space_SEZValueDate;

	protected North north_FinTaxDetail;
	Tab parenttab = null;
	protected Groupbox finBasicdetails;

	protected Row row_FinRef;

	protected ExtendedCombobox finReference;
	protected ExtendedCombobox country;
	protected ExtendedCombobox province;
	protected ExtendedCombobox city;
	protected ExtendedCombobox pinCode;
	protected Button btnAddressCopy;

	private FinanceTaxDetail financeTaxDetail;
	private FinanceDetail financeDetail;
	private List<GuarantorDetail> gurantorsDetailList = new ArrayList<GuarantorDetail>();
	private List<JointAccountDetail> jointAccountDetailList = new ArrayList<JointAccountDetail>();
	private List<ValueLabel> listApplicableFor = PennantStaticListUtil.getTaxApplicableFor();

	private transient FinanceTaxDetailListCtrl financeTaxDetailListCtrl;
	JointAccountDetailDialogCtrl jntDialogCtrl = null;
	private transient FinanceTaxDetailService financeTaxDetailService;
	private transient GSTDetailService gstDetailService;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private Object financeMainDialogCtrl = null;

	private boolean fromLoan = false;
	private boolean enquirymode = false;
	private boolean isTaxMand = false;

	private GSTNValidationService gstnValidationService;
	private CustomerAddresDAO customerAddresDAO;
	private long custID;
	private FinanceMainService financeMainService;
	private FinanceMainDAO financeMainDAO;

	/**
	 * default constructor.<br>
	 */
	public FinanceTaxDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceTaxDetailDialog";
	}

	@Override
	protected String getReference() {
		return this.financeTaxDetail.getFinReference();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinanceTaxDetailDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_FinanceTaxDetailDialog);

		try {
			// Get the required arguments.
			this.financeTaxDetail = (FinanceTaxDetail) arguments.get("financeTaxDetail");

			if (arguments.containsKey("fromLoan")) {
				fromLoan = (Boolean) arguments.get("fromLoan");
			} else {
				this.financeTaxDetailListCtrl = (FinanceTaxDetailListCtrl) arguments.get("financeTaxDetailListCtrl");
			}

			if (arguments.containsKey("enquirymode")) {
				enquirymode = (boolean) arguments.get("enquirymode");
			}

			if (arguments.containsKey("custId")) {
				this.custID = (long) arguments.get("custId");
			}

			if (arguments.containsKey("financeDetail")) {
				this.financeDetail = (FinanceDetail) (arguments.get("financeDetail"));
				this.setJointAccountDetailList(financeDetail.getJointAccountDetailList());
				this.setGurantorsDetailList(financeDetail.getGurantorsDetailList());
				this.isTaxMand = this.financeDetail.getFinScheduleData().getFinanceType().isTaxNoMand();
			}

			if (this.financeTaxDetail == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			if (fromLoan) {
				this.row_FinRef.setVisible(false);
				this.north_FinTaxDetail.setVisible(false);
				if (financeTaxDetail == null) {
					this.financeTaxDetail = new FinanceTaxDetail();
					financeTaxDetail.setNewRecord(true);
				}
				this.financeTaxDetail.setWorkflowId(0);

				if (arguments.containsKey("roleCode")) {
					setRole(arguments.get("roleCode").toString());
					getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
				}

				if (arguments.containsKey("tab")) {
					parenttab = (Tab) arguments.get("tab");
				}

				if (arguments.containsKey("finHeaderList")) {
					appendFinBasicDetails((ArrayList<Object>) arguments.get("finHeaderList"));
				} else {
					appendFinBasicDetails(null);
				}

				if (arguments.containsKey("financeMainDialogCtrl")) {
					setFinanceMainDialogCtrl(arguments.get("financeMainDialogCtrl"));
				}
			}

			if (financeMainDialogCtrl != null) {
				try {
					jntDialogCtrl = (JointAccountDetailDialogCtrl) getFinanceMainDialogCtrl().getClass()
							.getMethod("getJointAccountDetailDialogCtrl").invoke(getFinanceMainDialogCtrl());
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					logger.error(e);
				}
			}

			// Store the before image.
			FinanceTaxDetail financeTaxDetail = new FinanceTaxDetail();
			BeanUtils.copyProperties(this.financeTaxDetail, financeTaxDetail);
			this.financeTaxDetail.setBefImage(financeTaxDetail);

			// Render the page and display the data.
			doLoadWorkFlow(this.financeTaxDetail.isWorkflow(), this.financeTaxDetail.getWorkflowId(),
					this.financeTaxDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				if (!fromLoan) {
					getUserWorkspace().allocateAuthorities(this.pageRightName, null);
				} else {
					getUserWorkspace().allocateAuthorities(this.pageRightName, role);
				}
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.financeTaxDetail);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(ArrayList<Object> finHeaderList) {
		logger.debug(Literal.ENTERING);

		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			if (finHeaderList != null) {
				map.put("finHeaderList", finHeaderList);
			}
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.error(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.finReference.setModuleName("FinanceMain");
		this.finReference.setMandatoryStyle(true);
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setDisplayStyle(2);
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finReference.setMaxlength(LengthConstants.LEN_REF);
		this.finReference.setTextBoxWidth(143);

		this.taxNumber.setMaxlength(15);
		this.addrLine1.setMaxlength(300);
		this.addrLine2.setMaxlength(100);
		this.addrLine3.setMaxlength(100);
		this.addrLine4.setMaxlength(100);

		this.country.setModuleName("Country");
		this.country.setValueColumn("CountryCode");
		this.country.setDescColumn("CountryDesc");
		this.country.setValidateColumns(new String[] { "CountryCode" });
		this.country.setMandatoryStyle(true);
		this.country.setTextBoxWidth(143);

		this.province.setModuleName("Province");
		this.province.setValueColumn("CPProvince");
		this.province.setDescColumn("CPProvinceName");
		this.province.setValidateColumns(new String[] { "CPProvince" });
		this.province.setMandatoryStyle(true);
		this.province.setTextBoxWidth(143);

		this.custRef.setModuleName("Customer");
		this.custRef.setValueColumn("CustCIF");
		this.custRef.setDescColumn("CustShrtName");
		this.custRef.setValidateColumns(new String[] { "CustCIF" });
		this.custRef.setMandatoryStyle(true);
		this.custRef.setTextBoxWidth(143);

		this.city.setModuleName("City");
		this.city.setValueColumn("PCCity");
		this.city.setDescColumn("PCCityName");
		this.city.setValidateColumns(new String[] { "PCCity" });
		this.city.setMandatoryStyle(true);
		this.city.setTextBoxWidth(143);

		this.pinCode.setModuleName("PinCode");
		this.pinCode.setValueColumn("PinCodeId");
		this.pinCode.setDescColumn("AreaName");
		this.pinCode.setValueType(DataType.LONG);
		this.pinCode.setValidateColumns(new String[] { "PinCodeId" });
		this.pinCode.setInputAllowed(false);
		this.pinCode.setMandatoryStyle(true);
		this.pinCode.setTextBoxWidth(143);

		this.addressDetail.setModuleName("CustomerAddres");
		this.addressDetail.setValueColumn("CustAddrType");
		this.addressDetail.setDescColumn("CustAddrPriority");
		this.addressDetail.setValidateColumns(new String[] { "CustAddrType" });
		Filter filter1[] = new Filter[1];
		filter1[0] = new Filter("CustId", this.financeTaxDetail.getTaxCustId(), Filter.OP_EQUAL);
		this.addressDetail.setFilters(filter1);

		this.custTaxNumber.setModuleName("GSTDetail");
		this.custTaxNumber.setValueColumn("gstNumber");
		this.custTaxNumber.setDescColumn("gstState");
		this.custTaxNumber.setValidateColumns(new String[] { "gstNumber" });
		Filter filter2[] = new Filter[1];
		filter2[0] = new Filter("CustId", this.financeTaxDetail.getTaxCustId(), Filter.OP_EQUAL);
		this.custTaxNumber.setFilters(filter2);

		this.sezCertificateNo.setMaxlength(100);
		this.sezValueDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		if (isTaxMand) {
			this.space_taxNumber.setSclass(PennantConstants.mandateSclass);
		}

		btnAddressCopy.setVisible(true);
		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	public void onChange$taxNumber(Event event) {
		logger.debug(Literal.ENTERING);
		// GSTIN Validation
		String gSTNNumber = this.taxNumber.getValue();

		if (StringUtils.trimToNull(gSTNNumber) == null || this.taxNumber.isReadonly()
				|| StringUtils.equals(gSTNNumber, this.financeTaxDetail.getBefImage().getTaxNumber())) {
			return;
		}

		if (parenttab != null) {
			parenttab.setSelected(true);
		}

		try {
			GSTINInfo gstinInfo = new GSTINInfo();
			gstinInfo.setgSTNNumber(gSTNNumber);
			gstinInfo.setCif(this.custRef.getValue());
			gstinInfo.setUsrID(getUserWorkspace().getUserId());

			gstinInfo = this.gstnValidationService.validateGSTNNumber(gstinInfo);

			if (gstinInfo != null) {
				StringBuilder msg = new StringBuilder();
				msg.append("\n").append(" GSTIN :").append(gstinInfo.getgSTNNumber());
				msg.append("\n").append(" Name :").append(gstinInfo.getLegelName());
				if (MessageUtil.confirm(msg.toString(), MessageUtil.CANCEL | MessageUtil.OK) == MessageUtil.CANCEL) {
					return;
				}
			}
		} catch (InterfaceException e) {
			MessageUtil.showMessage(e.getErrorCode() + " - " + e.getErrorMessage());
			this.taxNumber.setValue("");
		}
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$finReference(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = finReference.getObject();
		doClear();

		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString(), "");
			setCustCIFFilter(false);
		} else {
			FinanceMain financeMain = (FinanceMain) dataObject;
			long finID = 0;
			if (financeMain != null) {
				finID = financeMain.getFinID();
				String finRef = financeMain.getFinReference();
				this.financeTaxDetail.setFinID(finID);
				this.finReference.setValue(finRef, "");
				this.taxCustId = financeMain.getCustID();
			}
			setJointAccountDetailList(this.financeTaxDetailService.getJointAccountDetailByFinRef(finID, "_AView"));
			setGurantorsDetailList(this.financeTaxDetailService.getGuarantorDetailByFinRef(finID, "_AView"));
		}

		filterCustomerDetails();

		logger.debug(Literal.LEAVING);
	}

	private void filterCustomerDetails() {
		if (this.applicableFor.getSelectedIndex() == 0) {
			this.applicableFor.setSelectedIndex(1);
		}

		setCustomerFilters(this.applicableFor.getSelectedItem().getValue(), false);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceTaxDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceTaxDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinanceTaxDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceTaxDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);

		doSave();

		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);

		doEdit();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);

		MessageUtil.showHelpWindow(event, super.window);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		doDelete();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);

		doCancel();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);

		doClose(this.btnSave.isVisible());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);

		doShowNotes(this.financeTaxDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);

		this.financeTaxDetailListCtrl.search();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.financeTaxDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onChange$applicableFor(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		String applicable = this.applicableFor.getSelectedItem().getValue();
		setCustomerFilters(applicable, true);
		logger.debug(Literal.LEAVING);
	}

	private void setHighPriorityAddress(List<CustomerAddres> addressList) {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isNotBlank(this.custTaxNumber.getValue())) {
			logger.debug(Literal.LEAVING);
			return;
		}

		if (CollectionUtils.isNotEmpty(addressList)) {
			for (CustomerAddres ca : addressList) {
				if (!PennantConstants.KYC_PRIORITY_VERY_HIGH.equals(String.valueOf(ca.getCustAddrPriority()))) {
					continue;
				}

				setReadOnlyOnComp();
				this.addressDetail.setValue(ca.getCustAddrType(), ca.getLovDescCustAddrTypeName());
				this.addrLine1.setValue(ca.getCustAddrHNbr());
				this.country.setValue(ca.getCustAddrCountry(), ca.getLovDescCustAddrCountryName());
				this.province.setValue(ca.getCustAddrProvince(), ca.getLovDescCustAddrProvinceName());
				this.city.setValue(ca.getCustAddrCity(), ca.getLovDescCustAddrCityName());
				this.pinCode.setValue(ca.getCustAddrZIP(), ca.getLovDescCustAddrZip());
				this.pinCode.setAttribute("pinCodeId", ca.getPinCodeId());
				readOnlyComponent(true, this.custTaxNumber);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * flag : To Reset Data in custRef
	 */
	private void setCustCIFFilter(boolean isUserActionOnApplFor) {
		logger.debug("Entering");

		String applicable = this.applicableFor.getSelectedItem().getValue();
		if (isUserActionOnApplFor) {
			this.custRef.setValue("");
			this.custRef.setDescription("");
		}

		this.custRef.setErrorMessage("");
		this.custRef.setConstraint("");

		if (PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER.equals(applicable)) {
			if (isUserActionOnApplFor) {
				clearDataFields();
			}
			Customer customer = null;
			List<CustomerAddres> addressList = null;
			if (fromLoan) {
				customer = this.financeDetail.getCustomerDetails().getCustomer();
				addressList = this.financeDetail.getCustomerDetails().getAddressList();
			} else {
				String finRef = this.finReference.getValue();
				if (StringUtils.isNotBlank(finRef)) {
					long custID = this.financeTaxDetailService.getCustomerIdByFinRef(finRef);
					customer = this.financeTaxDetailService.getCustomerByID(custID);
				}
			}

			if (customer != null) {
				this.custRef.setValue(customer.getCustCIF());
				this.custRef.setDescription(customer.getCustShrtName());
				this.taxCustId = customer.getCustID();
			}

			if (customer != null) {
				this.addressDetail
						.setFilters(new Filter[] { new Filter("CustID", customer.getCustID(), Filter.OP_EQUAL) });
				this.custTaxNumber
						.setFilters(new Filter[] { new Filter("CustId", customer.getCustID(), Filter.OP_EQUAL) });
			}

			boolean setDftGST = false;
			if (isUserActionOnApplFor) {
				GSTDetail gstDetail = gstDetailService.getDefaultGSTDetailById(custID);
				if (gstDetail != null) {
					this.custTaxNumber.setValue(gstDetail.getGstNumber());
					this.custTaxNumber.setDescription(gstDetail.getStateCode());
					this.taxNumber.setValue(gstDetail.getGstNumber());
					this.addrLine1.setValue(gstDetail.getAddressLine1());
					this.addrLine2.setValue(gstDetail.getAddressLine2());
					this.addrLine3.setValue(gstDetail.getAddressLine3());
					this.addrLine4.setValue(gstDetail.getAddressLine4());
					this.country.setValue(gstDetail.getCountryCode(), gstDetail.getCountryName());
					this.province.setValue(gstDetail.getStateCode(), gstDetail.getStateName());
					this.city.setValue(gstDetail.getCityCode(), gstDetail.getCityName());
					this.pinCode.setValue(gstDetail.getPinCode(), gstDetail.getPinCodeName());
					readOnlyAllComponents();
					readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_CustTaxNumber"), this.custTaxNumber);
					setDftGST = true;
				}
			}

			if (isUserActionOnApplFor) {
				setHighPriorityAddress(addressList);
			}

			readOnlyComponent(true, this.custRef);
			if (!setDftGST) {
				setReadOnlyOnComp();
				if (isUserActionOnApplFor) {
					readOnlyComponent(true, this.custTaxNumber);
				}
			}

		} else if (PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT.equals(applicable)) {
			if (isUserActionOnApplFor) {
				clearDataFields();
			}
			List<String> custCIFList = new ArrayList<>();

			if (fromLoan) {
				if (jntDialogCtrl != null) {
					for (Customer customer : jntDialogCtrl.getJointAccountCustomers()) {
						custCIFList.add(customer.getCustCIF());
					}
				}
			} else {
				for (JointAccountDetail jointAccDet : getJointAccountDetailList()) {
					custCIFList.add(jointAccDet.getCustCIF());
				}
			}

			if (custCIFList.isEmpty()) {
				MessageUtil.showError(Labels.getLabel("label_ApplFor_Sel_CoApp_NoCoApp"));
				readOnlyComponent(true, this.custRef);
				readOnlyAllComponents();
				return;
			}

			// set CustomerReference as Filter for finLimitRef
			Filter custRefFilter[] = new Filter[1];
			custRefFilter[0] = new Filter("CustCIF", custCIFList, Filter.OP_IN);
			this.custRef.setFilters(custRefFilter);
			setReadOnlyOnComp();

		} else if (PennantConstants.TAXAPPLICABLEFOR_GUARANTOR.equals(applicable)) {
			List<String> guarantorDetails = new ArrayList<>();

			if (fromLoan) {
				if (jntDialogCtrl != null) {
					for (Customer jntDet : jntDialogCtrl.getGuarantorCustomers()) {
						guarantorDetails.add(jntDet.getCustCIF());
					}
				}
			} else {
				for (GuarantorDetail guarantorDetail : getGurantorsDetailList()) {
					guarantorDetails.add(guarantorDetail.getGuarantorCIF());
				}
			}
			if (guarantorDetails.isEmpty()) {
				MessageUtil.showError(Labels.getLabel("label_ApplFor_Sel_Guarantor_NoGuarantor"));
				readOnlyAllComponents();
				return;
			}

			// set CustomerReference as Filter for finLimitRef
			Filter custRefFilter[] = new Filter[1];
			custRefFilter[0] = new Filter("CustCif", guarantorDetails, Filter.OP_IN);
			this.custRef.setFilters(custRefFilter);
			setReadOnlyOnComp();
		} else {
			readOnlyComponent(true, this.custRef);
			readOnlyAllComponents();
			clearDataFields();
		}

		logger.debug("Leaving");
	}

	public void onFulfill$custRef(Event event) {
		logger.debug("Entering");

		clearDataFields();
		Object dataObject = custRef.getObject();
		if (dataObject instanceof String) {
			this.custRef.setValue(dataObject.toString());
			this.custRef.setDescription(dataObject.toString());
		} else {
			Customer customer = (Customer) dataObject;
			if (customer != null) {
				this.custRef.setValue(customer.getCustCIF(), customer.getCustShrtName());
				this.taxCustId = customer.getCustID();
				GSTDetail gstDetail = gstDetailService.getDefaultGSTDetailById(taxCustId);

				if (gstDetail != null) {
					this.taxNumber.setValue(gstDetail.getGstNumber());
					this.custTaxNumber.setValue(gstDetail.getGstNumber(), gstDetail.getStateCode());
					this.addrLine1.setValue(gstDetail.getAddressLine1());
					this.addrLine2.setValue(gstDetail.getAddressLine2());
					this.addrLine3.setValue(gstDetail.getAddressLine3());
					this.addrLine4.setValue(gstDetail.getAddressLine4());
					this.country.setValue(gstDetail.getCountryCode(), gstDetail.getCountryName());
					this.province.setValue(gstDetail.getStateCode(), gstDetail.getStateName());
					this.city.setValue(gstDetail.getCityCode(), gstDetail.getCityName());
					this.pinCode.setValue(gstDetail.getPinCode(), gstDetail.getPinCodeName());
					if (gstDetail.getPinCodeId() != null) {
						this.pinCode.setAttribute("pinCodeId", gstDetail.getPinCodeId());
					} else {
						this.pinCode.setAttribute("pinCodeId", null);
					}
					readOnlyAllComponents();
					readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_CustTaxNumber"), this.custTaxNumber);
				} else {
					List<CustomerAddres> custAddress = customerAddresDAO
							.getCustomerAddresByCustomer(customer.getCustID(), "_View");
					setHighPriorityAddress(custAddress);
				}
				this.custTaxNumber
						.setFilters(new Filter[] { new Filter("CustID", customer.getCustID(), Filter.OP_EQUAL) });
				this.addressDetail
						.setFilters(new Filter[] { new Filter("CustID", customer.getCustID(), Filter.OP_EQUAL) });
			} else {
				readOnlyComponent(false, this.addressDetail);
				clearDataFields();
			}
		}

		if (null == dataObject) {
			clearDataFields();
			readOnlyAllComponents();
			this.addressDetail.setFilters(new Filter[] { new Filter("CustID", 0, Filter.OP_EQUAL) });
			this.custTaxNumber.setFilters(new Filter[] { new Filter("CustId", 0, Filter.OP_EQUAL) });
		}
		logger.debug(Literal.LEAVING);
	}

	private void clearDataFields() {
		this.taxNumber.setValue("");
		this.custTaxNumber.setValue("", "");
		this.addressDetail.setValue("", "");
		this.addrLine1.setValue("");
		this.addrLine2.setValue("");
		this.addrLine3.setValue("");
		this.addrLine4.setValue("");
		this.country.setValue("", "");
		this.province.setValue("", "");
		this.city.setValue("", "");
		this.pinCode.setValue("", "");
		this.pinCode.setAttribute("pinCodeId", null);
	}

	public void resetData() {
		this.addrLine1.setValue("");
		this.country.setValue("");
		this.country.setDescription("");
		this.province.setValue("");
		this.province.setDescription("");
		this.city.setValue("");
		this.city.setDescription("");
		this.pinCode.setValue("");
		this.pinCode.setDescription("");
		this.addressDetail.setValue("");
		this.addressDetail.setDescription("");
		this.addrLine2.setValue("");
		this.addrLine3.setValue("");
		this.addrLine4.setValue("");
		this.taxNumber.setValue("");
	}

	public void onFulfill$addressDetail(Event event) {
		logger.debug("Entering");

		Object dataObject = addressDetail.getObject();
		if (null == dataObject || StringUtils.equals("", dataObject.toString())) {
			dataObject = null;
		}
		if (dataObject instanceof String) {
			this.addressDetail.setValue(dataObject.toString());
			this.addressDetail.setDescription(dataObject.toString());
		} else {
			CustomerAddres ca = (CustomerAddres) dataObject;
			if (ca != null) {
				this.taxCustId = ca.getCustID();
				if (null != ca) {
					String addrLine1 = getCommaSeperate(ca.getCustAddrHNbr(), ca.getCustFlatNbr(),
							ca.getCustAddrStreet(), ca.getCustAddrLine1());
					this.addrLine1.setValue(addrLine1);
					this.addrLine2.setValue(ca.getCustAddrLine2());
					this.addrLine3.setValue(ca.getCustAddrLine3());
					this.addrLine4.setValue(ca.getCustAddrLine4());
					this.country.setValue(ca.getCustAddrCountry(), ca.getLovDescCustAddrCountryName());
					this.province.setValue(ca.getCustAddrProvince(), ca.getLovDescCustAddrProvinceName());
					this.city.setValue(ca.getCustAddrCity(), ca.getLovDescCustAddrCityName());
					this.pinCode.setValue(ca.getCustAddrZIP(), ca.getLovDescCustAddrZip());
					readOnlyComponent(true, this.custTaxNumber);
					readOnlyComponent(false, this.taxNumber);
				}
			} else {
				readOnlyComponent(false, this.custTaxNumber);
			}
		}
		if (null == dataObject) {
			clearDataFields();
		}
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$custTaxNumber(Event event) {
		logger.debug("Entering");

		Object dataObject = custTaxNumber.getObject();
		if (null == dataObject || StringUtils.equals("", dataObject.toString())) {
			dataObject = null;
		}
		if (dataObject instanceof String) {
			this.custTaxNumber.setValue(dataObject.toString());
			this.custTaxNumber.setDescription(dataObject.toString());
		} else {
			GSTDetail gstDetail = (GSTDetail) dataObject;
			if (gstDetail != null) {
				this.taxCustId = gstDetail.getCustID();
				if (null != gstDetail) {
					this.custTaxNumber.setValue(gstDetail.getGstNumber(), gstDetail.getStateCode());
					this.taxNumber.setValue(gstDetail.getGstNumber());
					this.addrLine1.setValue(gstDetail.getAddressLine1());
					this.addrLine2.setValue(gstDetail.getAddressLine2());
					this.addrLine3.setValue(gstDetail.getAddressLine3());
					this.addrLine4.setValue(gstDetail.getAddressLine4());
					this.country.setValue(gstDetail.getCountryCode(), gstDetail.getCountryName());
					this.province.setValue(gstDetail.getStateCode(), gstDetail.getStateName());
					this.city.setValue(gstDetail.getCityCode(), gstDetail.getCityName());
					this.pinCode.setValue(gstDetail.getPinCode(), gstDetail.getPinCodeName());
					readOnlyAllComponents();
					readOnlyComponent(false, this.custTaxNumber);
				}
			} else {
				setReadOnlyOnComp();
				if (PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER.equals(getComboboxValue(this.applicableFor))) {
					readOnlyComponent(true, this.custRef);
				}
			}
		}

		if (null == dataObject) {
			setReadOnlyOnComp();
			clearDataFields();
			if (PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER.equals(getComboboxValue(this.applicableFor))) {
				readOnlyComponent(true, this.custRef);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void readOnlyAllComponents() {
		readOnlyComponent(true, this.taxExempted);
		readOnlyComponent(true, this.taxNumber);
		readOnlyComponent(true, this.addressDetail);
		readOnlyComponent(true, this.custTaxNumber);
		readOnlyComponent(true, this.addrLine1);
		readOnlyComponent(true, this.addrLine2);
		readOnlyComponent(true, this.addrLine3);
		readOnlyComponent(true, this.addrLine4);
		readOnlyComponent(true, this.city);
		readOnlyComponent(true, this.province);
		readOnlyComponent(true, this.country);
		readOnlyComponent(true, this.pinCode);
		readOnlyComponent(true, this.sezCertificateNo);
		readOnlyComponent(true, this.sezValueDate);
	}

	private String getCommaSeperate(String... values) {
		StringBuilder address = new StringBuilder();

		for (String value : values) {
			value = StringUtils.trimToNull(value);

			if (value == null) {
				continue;
			}

			if (address.length() > 0) {
				address.append(", ");
			}

			address.append(value);
		}
		return address.toString();
	}

	public void onFulfill$country(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = country.getObject();
		String pcProvince = null;
		if (dataObject instanceof String) {
			this.province.setValue("");
			this.province.setDescription("");
			this.city.setValue("");
			this.city.setDescription("");
			this.pinCode.setValue("");
			this.pinCode.setDescription("");
			fillPindetails(null, null);
		} else if (!(dataObject instanceof String)) {
			Country country = (Country) dataObject;
			if (country == null) {
				fillProvinceDetails(null);
			}
			if (country != null) {
				this.province.setErrorMessage("");
				pcProvince = country.getCountryCode();
				fillProvinceDetails(pcProvince);
			} else {
				this.province.setObject("");
				this.city.setObject("");
				this.pinCode.setObject("");
				this.province.setValue("");
				this.province.setDescription("");
				this.city.setValue("");
				this.city.setDescription("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
			}
			fillPindetails(null, null);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$province(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = province.getObject();
		String pcProvince = this.province.getValue();
		if (dataObject instanceof String) {
			this.city.setValue("");
			this.city.setDescription("");
			this.pinCode.setValue("");
			this.pinCode.setDescription("");
			fillPindetails(null, null);
		} else if (!(dataObject instanceof String)) {
			Province province = (Province) dataObject;
			if (province == null) {
				fillPindetails(null, null);
			}
			if (province != null) {
				this.province.setErrorMessage("");
				pcProvince = this.province.getValue();
				this.country.setValue(province.getCPCountry());
				this.country.setDescription(province.getLovDescCPCountryName());
				this.city.setValue("");
				this.city.setDescription("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
				fillCitydetails(pcProvince);
				fillPindetails(null, pcProvince);
			} else {
				this.city.setObject("");
				this.pinCode.setObject("");
				this.city.setValue("");
				this.city.setDescription("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
			}
		}
		fillCitydetails(pcProvince);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * onChanging Branch
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$city(Event event) throws InterruptedException {
		logger.debug("Entering");
		doRemoveValidation();
		doClearMessage();
		Object dataObject = city.getObject();
		String cityValue = null;
		if (!(dataObject instanceof String)) {
			City details = (City) dataObject;
			if (details == null) {
				fillPindetails(null, null);
			}
			if (details != null) {
				this.province.setValue(details.getPCProvince());
				this.province.setDescription(details.getLovDescPCProvinceName());
				this.country.setValue(details.getPCCountry());
				this.country.setDescription(details.getLovDescPCCountryName());
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
				cityValue = details.getPCCity();
				fillPindetails(cityValue, this.province.getValue());
			} else {
				this.city.setObject("");
				this.pinCode.setObject("");
				this.pinCode.setValue("");
				this.pinCode.setDescription("");
				this.province.setErrorMessage("");
				this.country.setErrorMessage("");
				fillPindetails(null, this.province.getValue());
			}
		} else if ("".equals(dataObject)) {
			this.pinCode.setValue("");
			this.pinCode.setDescription("");
			this.province.setObject("");
		}
		logger.debug("Leaving");
	}

	/**
	 * onChanging Branch
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$pinCode(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = pinCode.getObject();
		if (dataObject instanceof String) {

		} else {
			PinCode details = (PinCode) dataObject;

			if (details != null) {
				this.country.setValue(details.getpCCountry());
				this.country.setDescription(details.getLovDescPCCountryName());
				this.city.setValue(details.getCity());
				this.city.setDescription(details.getPCCityName());
				this.province.setValue(details.getPCProvince());
				this.province.setDescription(details.getLovDescPCProvinceName());
				this.city.setErrorMessage("");
				this.province.setErrorMessage("");
				this.country.setErrorMessage("");
				this.pinCode.setAttribute("pinCodeId", details.getPinCodeId());
				this.pinCode.setValue(details.getPinCode());
			}

		}
		Filter[] filters1 = new Filter[1];
		if (this.city.getValue() != null && !this.city.getValue().isEmpty()) {
			filters1[0] = new Filter("City", this.city.getValue(), Filter.OP_EQUAL);
		} else {
			filters1[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}

		this.pinCode.setFilters(filters1);

		logger.debug("Leaving");
	}

	private void fillProvinceDetails(String country) {
		this.province.setMandatoryStyle(true);
		this.province.setModuleName("Province");
		this.province.setValueColumn("CPProvince");
		this.province.setDescColumn("CPProvinceName");
		this.province.setValidateColumns(new String[] { "CPProvince" });

		Filter[] filters1 = new Filter[1];

		if (country == null || country.equals("")) {
			filters1[0] = new Filter("CPCountry", null, Filter.OP_NOT_EQUAL);
		} else {
			filters1[0] = new Filter("CPCountry", country, Filter.OP_EQUAL);
		}

		this.province.setFilters(filters1);
	}

	private void fillCitydetails(String state) {
		logger.debug("Entering");

		this.city.setModuleName("City");
		this.city.setValueColumn("PCCity");
		this.city.setDescColumn("PCCityName");
		this.city.setValidateColumns(new String[] { "PCCity" });
		Filter[] filters1 = new Filter[1];

		if (state == null || state.isEmpty()) {
			filters1[0] = new Filter("PCProvince", null, Filter.OP_NOT_EQUAL);
		} else {
			filters1[0] = new Filter("PCProvince", state, Filter.OP_EQUAL);
		}

		this.city.setFilters(filters1);
	}

	private void fillPindetails(String id, String province) {
		this.pinCode.setModuleName("PinCode");
		this.pinCode.setValueColumn("PinCodeId");
		this.pinCode.setDescColumn("AreaName");
		this.pinCode.setValidateColumns(new String[] { "PinCodeId" });
		Filter[] filters1 = new Filter[1];

		if (id != null) {
			filters1[0] = new Filter("City", id, Filter.OP_EQUAL);
		} else if (province != null && !province.isEmpty()) {
			filters1[0] = new Filter("PCProvince", province, Filter.OP_EQUAL);
		} else {
			filters1[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}

		this.pinCode.setFilters(filters1);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param financeTaxDetail
	 * 
	 */
	public void doWriteBeanToComponents(FinanceTaxDetail aftd) {
		logger.debug(Literal.ENTERING);

		long finID = aftd.getFinID();
		String finRef = aftd.getFinReference();

		this.finReference.setValue(finRef);

		fillComboBox(this.applicableFor, aftd.getApplicableFor(), listApplicableFor, getExcludeFields());

		this.taxCustId = aftd.getTaxCustId();
		if (taxCustId > 0) {
			this.custRef.setValue(aftd.getCustCIF(), aftd.getCustShrtName());
		}
		this.taxExempted.setChecked(aftd.isTaxExempted());
		this.taxNumber.setValue(aftd.getTaxNumber());
		this.addressDetail.setValue(aftd.getAddressDetail());
		if (aftd.isAvailCustTaxNum()) {
			this.custTaxNumber.setValue(aftd.getTaxNumber());
		}
		this.addrLine1.setValue(aftd.getAddrLine1());
		this.addrLine2.setValue(aftd.getAddrLine2());
		this.addrLine3.setValue(aftd.getAddrLine3());
		this.addrLine4.setValue(aftd.getAddrLine4());
		this.city.setValue(aftd.getCity(), aftd.getCityName());
		this.province.setValue(aftd.getProvince(), aftd.getProvinceName());
		this.country.setValue(aftd.getCountry(), aftd.getCountryName());
		this.pinCode.setValue(aftd.getPinCode());
		if (aftd.getPinCodeId() != null) {
			this.pinCode.setAttribute("pinCodeId", aftd.getPinCodeId());
		} else {
			this.pinCode.setAttribute("pinCodeId", null);
		}
		this.sezCertificateNo.setValue(aftd.getSezCertificateNo());
		this.sezValueDate.setValue(aftd.getSezValueDate());

		if (!fromLoan) {
			this.recordStatus.setValue(aftd.getRecordStatus());
			if (!aftd.isNewRecord()) {
				setJointAccountDetailList(this.financeTaxDetailService.getJointAccountDetailByFinRef(finID, "_AView"));
				setGurantorsDetailList(this.financeTaxDetailService.getGuarantorDetailByFinRef(finID, "_AView"));
			}
		}

		if (!enqiryModule) {
			setCustCIFFilter(false);
		}

		this.custRef.setValue(aftd.getCustCIF(), aftd.getCustShrtName());
		custID = aftd.getTaxCustId();
		filterCustomerDetails();

		if (!aftd.isNewRecord()) {
			List<Filter> filters = new ArrayList<>();

			if (this.country.getValue() != null && !this.country.getValue().isEmpty()) {
				Filter filterPin0 = new Filter("PCCountry", this.country.getValue(), Filter.OP_EQUAL);
				filters.add(filterPin0);
			}

			if (this.province.getValue() != null && !this.province.getValue().isEmpty()) {
				Filter filterPin1 = new Filter("PCProvince", this.province.getValue(), Filter.OP_EQUAL);
				filters.add(filterPin1);
			}

			if (this.city.getValue() != null && !this.city.getValue().isEmpty()) {
				Filter filterPin2 = new Filter("City", this.city.getValue(), Filter.OP_EQUAL);
				filters.add(filterPin2);
			}

			Filter[] filterPin = new Filter[filters.size()];
			for (int i = 0; i < filters.size(); i++) {
				filterPin[i] = filters.get(i);
			}
			this.pinCode.setFilters(filterPin);
		}

		logger.debug(Literal.LEAVING);
	}

	private String getExcludeFields() {
		logger.debug(Literal.ENTERING);

		String excludeFields = "";
		if (fromLoan && this.jntDialogCtrl == null) {
			excludeFields = "," + PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT + ","
					+ PennantConstants.TAXAPPLICABLEFOR_GUARANTOR + ",";
		}

		logger.debug(Literal.LEAVING);

		return excludeFields;
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceTaxDetail
	 * @return
	 */
	public ArrayList<WrongValueException> doWriteComponentsToBean(FinanceTaxDetail aFinanceTaxDetail) {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<>();

		// Finance Reference
		try {
			long finid = ComponentUtil.getFinID(this.finReference);
			String finrefrence = this.finReference.getValue();

			if (finid == 0 && StringUtils.isNotEmpty(finrefrence)) {
				finid = financeMainService.getFinID(finrefrence);
			}

			aFinanceTaxDetail.setFinID(finid);
			aFinanceTaxDetail.setFinReference(finrefrence);
			this.financeTaxDetail.setFinID(finid);
			this.financeTaxDetail.setFinReference(finrefrence);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Applicable For
		String strApplicableFor = null;
		try {
			if (this.applicableFor.getSelectedItem() != null) {
				strApplicableFor = this.applicableFor.getSelectedItem().getValue().toString();
			}
			if (strApplicableFor != null && !PennantConstants.List_Select.equals(strApplicableFor)) {
				aFinanceTaxDetail.setApplicableFor(strApplicableFor);
				this.custRef.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinanceTaxDetailDialog_CustRef.value"), null, true, true));
			} else {
				aFinanceTaxDetail.setApplicableFor(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceTaxDetail.setCustCIF(this.custRef.getValue());
			aFinanceTaxDetail.setTaxCustId(this.taxCustId);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Tax Exempted
		try {
			aFinanceTaxDetail.setTaxExempted(this.taxExempted.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Tax Number
		try {
			aFinanceTaxDetail.setTaxNumber(this.taxNumber.getValue());
			this.financeTaxDetail.setTaxNumber(this.taxNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Address Line 1
		try {
			aFinanceTaxDetail.setAddrLine1(this.addrLine1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Address Line 2
		try {
			aFinanceTaxDetail.setAddrLine2(this.addrLine2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Address Line 3
		try {
			aFinanceTaxDetail.setAddrLine3(this.addrLine3.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Address Line 4
		try {
			aFinanceTaxDetail.setAddrLine4(this.addrLine4.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Country
		try {
			aFinanceTaxDetail.setCountry(this.country.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Province
		try {
			aFinanceTaxDetail.setProvince(this.province.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// City
		try {
			aFinanceTaxDetail.setCity(this.city.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Pin Code
		try {
			Object obj = this.pinCode.getAttribute("pinCodeId");
			if (obj != null && !obj.toString().equals("0")) {
				if (!StringUtils.isEmpty(obj.toString())) {
					aFinanceTaxDetail.setPinCodeId(Long.valueOf((obj.toString())));
				}
			} else {
				aFinanceTaxDetail.setPinCodeId(null);
			}
			aFinanceTaxDetail.setPinCode(this.pinCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// SEZ Certificate No
		try {
			aFinanceTaxDetail.setSezCertificateNo(this.sezCertificateNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// SEZ Value Date
		try {
			aFinanceTaxDetail.setSezValueDate(this.sezValueDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Address Details
		try {
			aFinanceTaxDetail.setAddressDetail(this.addressDetail.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.isNotBlank(this.custTaxNumber.getValue())) {
				aFinanceTaxDetail.setAvailCustTaxNum(true);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (!fromLoan) {
			doRemoveValidation();

			if (!wve.isEmpty()) {
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = (WrongValueException) wve.get(i);
				}
				throw new WrongValuesException(wvea);
			}
		}

		aFinanceTaxDetail.setRecordStatus(this.recordStatus.getValue());

		logger.debug(Literal.LEAVING);

		return wve;
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param financeTaxDetail The entity that need to be render.
	 */
	public void doShowDialog(FinanceTaxDetail financeTaxDetail) {
		logger.debug(Literal.LEAVING);

		if (financeTaxDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			this.finReference.setReadonly(true);
			if (isWorkFlowEnabled() && !enqiryModule) {
				if (StringUtils.isNotBlank(financeTaxDetail.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.applicableFor.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
				if (fromLoan && !enqiryModule) {
					doEdit();
				}
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(financeTaxDetail);

		if (fromLoan && !enqiryModule) {
			try {
				getFinanceMainDialogCtrl().getClass().getMethod("setFinanceTaxDetailDialogCtrl", this.getClass())
						.invoke(getFinanceMainDialogCtrl(), this);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
			if (parenttab != null) {
				this.parenttab.setVisible(true);
			}
		} else {
			if (enquirymode) {
				this.window_FinanceTaxDetailDialog.setHeight("80%");
				this.window_FinanceTaxDetailDialog.setWidth("80%");
				this.groupboxWf.setVisible(false);
				this.btnEdit.setVisible(false);
				this.btnDelete.setVisible(false);
				this.window_FinanceTaxDetailDialog.doModal();
			} else {
				setDialog(DialogType.EMBEDDED);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.finReference.isReadonly()) {
			this.finReference.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceTaxDetailDialog_FinReference.value"), null, true, true));
		}

		if (!this.taxNumber.isReadonly()) {
			this.taxNumber.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_TaxNumber.value"),
							PennantRegularExpressions.REGEX_GSTIN, isTaxMand));
		}

		if (!this.addrLine1.isReadonly()) {
			this.addrLine1.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_AddrLine1.value"),
							PennantRegularExpressions.REGEX_ADDRESS, true));
		}

		if (!this.addrLine2.isReadonly()) {
			this.addrLine2.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_AddrLine2.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}

		if (!this.addrLine3.isReadonly()) {
			this.addrLine3.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_AddrLine3.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}

		if (!this.addrLine4.isReadonly()) {
			this.addrLine4.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_AddrLine4.value"),
							PennantRegularExpressions.REGEX_ADDRESS, false));
		}

		if (this.country.isButtonVisible() || !this.country.isReadonly()) {
			this.country.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceTaxDetailDialog_Country.value"), null, true, true));
		}

		if (!this.province.isReadonly()) {
			this.province.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceTaxDetailDialog_Province.value"), null, true, true));
		}

		if (!this.city.isReadonly()) {
			this.city.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_City.value"),
					null, true, true));
		}

		if (!this.pinCode.isReadonly()) {
			this.pinCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceTaxDetailDialog_PinCode.value"), null, true, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.finReference.setConstraint("");
		this.custRef.setConstraint("");
		this.applicableFor.setConstraint("");
		this.taxNumber.setConstraint("");
		this.addrLine1.setConstraint("");
		this.addrLine2.setConstraint("");
		this.addrLine3.setConstraint("");
		this.addrLine4.setConstraint("");
		this.country.setConstraint("");
		this.province.setConstraint("");
		this.city.setConstraint("");
		this.pinCode.setConstraint("");
		this.sezCertificateNo.setConstraint("");
		this.sezValueDate.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		this.custRef.setErrorMessage("");
		this.taxNumber.setErrorMessage("");
		this.addrLine1.setErrorMessage("");
		this.addrLine2.setErrorMessage("");
		this.addrLine3.setErrorMessage("");
		this.addrLine4.setErrorMessage("");
		this.country.setErrorMessage("");
		this.province.setErrorMessage("");
		this.city.setErrorMessage("");
		this.pinCode.setErrorMessage("");
		this.sezValueDate.setErrorMessage("");
		this.sezCertificateNo.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final FinanceTaxDetail aFinanceTaxDetail = new FinanceTaxDetail();
		BeanUtils.copyProperties(this.financeTaxDetail, aFinanceTaxDetail);

		doDelete(aFinanceTaxDetail.getFinReference(), aFinanceTaxDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.financeTaxDetail.isNewRecord()) {
			this.btnCancel.setVisible(false);
			if (fromLoan) {
				readOnlyComponent(true, this.finReference);
			} else {
				readOnlyComponent(false, this.finReference);
			}
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.finReference);
		}

		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_ApplicableFor"), this.applicableFor);
		setReadOnlyOnComp();

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.financeTaxDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	private void setReadOnlyOnComp() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_ApplicableFor"), this.custRef);
		readOnlyComponent(true, this.taxExempted);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_TaxNumber"), this.taxNumber);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_AddressDetail"), this.addressDetail);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_CustTaxNumber"), this.custTaxNumber);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_AddrLine1"), this.addrLine1);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_AddrLine2"), this.addrLine2);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_AddrLine3"), this.addrLine3);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_AddrLine4"), this.addrLine4);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_City"), this.city);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_Province"), this.province);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_Country"), this.country);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_PinCode"), this.pinCode);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_SEZCertificateNumber"), this.sezCertificateNo);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_SEZValueDatee"), this.sezValueDate);

		if (!this.financeTaxDetail.isNewRecord()) {
			if (StringUtils.isNotEmpty(this.addressDetail.getValue())) {
				readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_AddressDetail"), this.addressDetail);
				readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_TaxNumber"), this.taxNumber);
				readOnlyComponent(true, this.custTaxNumber);
			} else if (StringUtils.isNotEmpty(this.custTaxNumber.getValue())) {
				readOnlyComponent(true, this.addressDetail);
				readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_CustTaxNumber"), this.custTaxNumber);
				readOnlyComponent(true, this.taxNumber);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.finReference);
		readOnlyComponent(true, this.applicableFor);
		readOnlyComponent(true, this.custRef);
		readOnlyAllComponents();

		if (isWorkFlowEnabled() && !enqiryModule) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");

		this.finReference.setValue("");
		this.finReference.setDescription("");
		this.custRef.setValue("");
		this.custRef.setDescription("");
		this.applicableFor.setSelectedIndex(0);
		this.taxExempted.setChecked(false);
		this.taxNumber.setValue("");
		this.addrLine1.setValue("");
		this.addrLine2.setValue("");
		this.addrLine3.setValue("");
		this.addrLine4.setValue("");
		this.country.setValue("");
		this.country.setDescription("");
		this.province.setValue("");
		this.province.setDescription("");
		this.city.setValue("");
		this.city.setDescription("");
		this.pinCode.setValue("");
		this.pinCode.setDescription("");
		this.taxCustId = 0;

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");

		final FinanceTaxDetail aFinanceTaxDetail = new FinanceTaxDetail();
		BeanUtils.copyProperties(this.financeTaxDetail, aFinanceTaxDetail);

		boolean isNew = false;

		doClearMessage();
		doSetValidation();
		doWriteComponentsToBean(aFinanceTaxDetail);

		// GSTIN Validation
		String gSTNNumber = this.financeTaxDetail.getTaxNumber();
		if ((StringUtils.trimToNull(gSTNNumber) != null) && !this.taxNumber.isReadonly()
				&& (!StringUtils.equals(gSTNNumber, this.financeTaxDetail.getBefImage().getTaxNumber()))) {
			try {
				GSTINInfo gstinInfo = new GSTINInfo();
				gstinInfo.setgSTNNumber(gSTNNumber);
				gstinInfo.setCif(aFinanceTaxDetail.getCustCIF());
				gstinInfo.setUsrID(getUserWorkspace().getLoggedInUser().getUserId());
				gstinInfo.setUsrLogin(getUserWorkspace().getLoggedInUser().getUserName());
				gstinInfo = this.gstnValidationService.validateGSTNNumber(gstinInfo);

				if (null != gstinInfo) {
					StringBuilder msg = new StringBuilder();
					msg.append(gstinInfo.getStatusCode()).append("_").append(gstinInfo.getStatusDesc());
					msg.append("\n").append(" GSTIN :").append(gstinInfo.getgSTNNumber());
					msg.append("\n").append(" GSTIN Date :").append(gstinInfo.getRegisterDateStr());
					msg.append("\n").append(" Name :").append(gstinInfo.getLegelName());
					msg.append("\n").append(" Type Of Ownership :").append(gstinInfo.getCxdt());

					if (MessageUtil.confirm(msg.toString(),
							MessageUtil.CANCEL | MessageUtil.OK) == MessageUtil.CANCEL) {
						return;
					}
				}
			} catch (InterfaceException e) {
				if (MessageUtil.confirm(e.getErrorCode() + " - " + e.getErrorMessage(),
						MessageUtil.CANCEL | MessageUtil.OVERIDE) == MessageUtil.CANCEL) {
					return;
				}
			}
		}
		isNew = aFinanceTaxDetail.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinanceTaxDetail.getRecordType())) {
				aFinanceTaxDetail.setVersion(aFinanceTaxDetail.getVersion() + 1);
				if (isNew) {
					aFinanceTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinanceTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceTaxDetail.setNewRecord(true);
				}
			}
		} else {
			aFinanceTaxDetail.setVersion(aFinanceTaxDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aFinanceTaxDetail, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	public void onChange$sezCertificateNo(Event event) throws ParseException {

		String value = this.sezCertificateNo.getValue();
		if (StringUtils.isNotEmpty(value)) {
			this.sezValueDate.setSclass(PennantConstants.mandateSclass);
			if (this.sezValueDate.isVisible() && !this.sezValueDate.isReadonly()) {
				this.sezValueDate.setConstraint(new PTDateValidator(
						Labels.getLabel("label_FinanceTaxDetailDialog_SEZCertificateNumber.value"), true));
			}
		} else {
			this.sezValueDate.setSclass("");
			this.sezValueDate.clearErrorMessage();
			this.sezValueDate.setConstraint("");
		}
	}

	/**
	 * used only for Loan Origination
	 * 
	 * @param fd
	 * @param tab
	 * @param recSave
	 */
	public void doSave_Tax(FinanceDetail fd, Tab tab, boolean recSave) {
		logger.debug("Entering");

		doClearMessage();
		if (!recSave && !(StringUtils.equals(this.applicableFor.getSelectedItem().getValue(),
				PennantConstants.List_Select))) {
			doSetValidation();
		}

		ArrayList<WrongValueException> wve = doWriteComponentsToBean(this.financeTaxDetail);
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		this.financeTaxDetail.setFinID(fm.getFinID());
		this.financeTaxDetail.setFinReference(fm.getFinReference());

		if (!wve.isEmpty() && parenttab != null) {
			parenttab.setSelected(true);
		}

		showErrorDetails(wve);

		if (StringUtils.equals(this.applicableFor.getSelectedItem().getValue(), PennantConstants.List_Select)
				&& StringUtils.isNotEmpty(this.financeTaxDetail.getRecordType())) {
			this.financeTaxDetail.setRcdDelReq(true);
		} else {
			this.financeTaxDetail.setRcdDelReq(false);
		}

		if (StringUtils.isBlank(this.financeTaxDetail.getRecordType())) {
			this.financeTaxDetail.setVersion(this.financeTaxDetail.getVersion() + 1);
			this.financeTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			this.financeTaxDetail.setNewRecord(true);
		}

		this.financeTaxDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		this.financeTaxDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		this.financeTaxDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
		fd.setFinanceTaxDetail(this.financeTaxDetail);

		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(FinanceTaxDetail aFinanceTaxDetail, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aFinanceTaxDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinanceTaxDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinanceTaxDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aFinanceTaxDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinanceTaxDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinanceTaxDetail);
				}

				if (isNotesMandatory(taskId, aFinanceTaxDetail)) {
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

			aFinanceTaxDetail.setTaskId(taskId);
			aFinanceTaxDetail.setNextTaskId(nextTaskId);
			aFinanceTaxDetail.setRoleCode(getRole());
			aFinanceTaxDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinanceTaxDetail, tranType);
			String operationRefs = getServiceOperations(taskId, aFinanceTaxDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinanceTaxDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinanceTaxDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving");

		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		FinanceTaxDetail aFinanceTaxDetail = (FinanceTaxDetail) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = financeTaxDetailService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = financeTaxDetailService.saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = financeTaxDetailService.doApprove(auditHeader);

					if (aFinanceTaxDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = financeTaxDetailService.doReject(auditHeader);
					if (aFinanceTaxDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_FinanceTaxDetailDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_FinanceTaxDetailDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.financeTaxDetail), true);
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

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");

		doRemoveValidation();

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			if (parenttab != null) {
				parenttab.setSelected(true);
			}
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(FinanceTaxDetail aFinanceTaxDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceTaxDetail.getBefImage(), aFinanceTaxDetail);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aFinanceTaxDetail.getUserDetails(),
				getOverideMap());
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || fromLoan) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	public void onClick$btnAddressCopy(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = PennantAppUtil.getAddressCopyList(this.window_FinanceTaxDetailDialog, custID);
		if (dataObject instanceof CustomerAddres) {
			CustomerAddres details = (CustomerAddres) dataObject;
			if (details != null) {
				doClearMessage();
				this.country.setValue(details.getCustAddrCountry(), details.getLovDescCustAddrCountryName());
				this.province.setValue(details.getCustAddrProvince(), details.getLovDescCustAddrProvinceName());
				this.city.setValue(details.getCustAddrCity(), details.getLovDescCustAddrCityName());
				this.pinCode.setValue(details.getCustAddrZIP(), details.getLovDescCustAddrZip());
				this.addrLine1.setValue(details.getCustAddrHNbr());
				this.addrLine2.setValue(details.getCustAddrStreet());
				this.addrLine3.setValue(details.getCustAddrLine3());
				this.addrLine4.setValue(details.getCustAddrLine4());
				fillProvinceDetails(details.getCustAddrCountry());
				fillCitydetails(details.getCustAddrProvince());
				fillPindetails(details.getCustAddrCity(), details.getCustAddrProvince());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void setCustomerFilters(String applicableFor, boolean flag) {
		List<String> custCIFList = new ArrayList<>();
		readOnlyComponentChecking();
		readOnlyComponent(true, this.custRef);

		if (flag) {
			custID = 0;
			resetData();
			this.custRef.setValue("");
			this.custRef.setDescription("");
			this.custRef.setErrorMessage("");
			this.custRef.setConstraint("");
		}

		if (PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER.equals(applicableFor)) {
			readOnlyComponent(true, this.custRef);
			Customer customer = null;

			if (fromLoan) {
				customer = this.financeDetail.getCustomerDetails().getCustomer();
			} else {
				Long finID = this.financeTaxDetail.getFinID();
				if (finID != null && finID > 0) {
					FinanceMain fm = this.financeMainDAO.getFinanceDetailsForService(finID, "_View", false);
					customer = this.financeTaxDetailService.getCustomerByID(fm.getCustID());
				}
			}

			if (customer != null) {
				this.custRef.setValue(customer.getCustCIF());
				this.custRef.setDescription(customer.getCustShrtName());
				this.taxCustId = customer.getCustID();
				custID = customer.getCustID();
			}
			readOnlyComponent(true, this.custRef);
		} else if (PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT.equals(applicableFor)) {
			readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_ApplicableFor"), this.custRef);
			if (fromLoan) {
				if (jntDialogCtrl != null) {
					for (Customer customer : jntDialogCtrl.getJointAccountCustomers()) {
						custCIFList.add(customer.getCustCIF());
					}
				}
			} else {
				for (JointAccountDetail jointAccDet : getJointAccountDetailList()) {
					custCIFList.add(jointAccDet.getCustCIF());
				}
			}
		} else if (PennantConstants.TAXAPPLICABLEFOR_GUARANTOR.equals(applicableFor)) {
			readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_ApplicableFor"), this.custRef);
			if (fromLoan) {
				if (jntDialogCtrl != null) {
					for (Customer jntDet : jntDialogCtrl.getGuarantorCustomers()) {
						custCIFList.add(jntDet.getCustCIF());
					}
				}
			} else {
				for (GuarantorDetail guarantorDetail : getGurantorsDetailList()) {
					custCIFList.add(guarantorDetail.getGuarantorCIF());
				}
			}
		} else {

			readOnlyComponent(true, this.custRef);
			readOnlyAllComponents();

			this.taxExempted.setChecked(false);
			this.addrLine1.setValue("");
			this.addrLine2.setValue("");
			this.addrLine3.setValue("");
			this.addrLine4.setValue("");
			this.taxNumber.setValue("");
			this.country.setValue("");
			this.country.setDescription("");
			this.province.setValue("");
			this.province.setDescription("");
			this.city.setValue("");
			this.city.setDescription("");
			this.pinCode.setValue("");
			this.pinCode.setDescription("");
			this.addressDetail.setValue("");
			this.addressDetail.setDescription("");
		}
		// set CustomerReference as Filter for finLimitRef
		Filter custRefFilter[] = new Filter[1];
		if (CollectionUtils.isEmpty(custCIFList)) {
			custCIFList.add("");
		}
		custRefFilter[0] = new Filter("CustCif", custCIFList, Filter.OP_IN);
		this.custRef.setFilters(custRefFilter);

		if (!fromLoan && finReference.getValue().isEmpty()) {
			this.custRef.setFilters(null);
		}
	}

	private void readOnlyComponentChecking() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_ApplicableFor"), this.custRef);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_TaxExempted"), this.taxExempted);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_TaxNumber"), this.taxNumber);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_AddrLine1"), this.addrLine1);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_AddrLine2"), this.addrLine2);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_AddrLine3"), this.addrLine3);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_AddrLine4"), this.addrLine4);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_Country"), this.country);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_Province"), this.province);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_City"), this.city);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_PinCode"), this.pinCode);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_SEZCertificateNumber"), this.sezCertificateNo);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_SEZValueDatee"), this.sezValueDate);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_AddressDetail"), this.addressDetail);
		readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_btnAddressCopy"), this.btnAddressCopy);

		logger.debug(Literal.LEAVING);
	}

	public void setFinanceTaxDetailService(FinanceTaxDetailService financeTaxDetailService) {
		this.financeTaxDetailService = financeTaxDetailService;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public List<GuarantorDetail> getGurantorsDetailList() {
		return gurantorsDetailList;
	}

	public void setGurantorsDetailList(List<GuarantorDetail> gurantorsDetailList) {
		this.gurantorsDetailList = gurantorsDetailList;
	}

	public List<JointAccountDetail> getJointAccountDetailList() {
		return jointAccountDetailList;
	}

	public void setJointAccountDetailList(List<JointAccountDetail> jointAccountDetailList) {
		this.jointAccountDetailList = jointAccountDetailList;
	}

	public GSTNValidationService getGstnValidationService() {
		return gstnValidationService;
	}

	public void setGstnValidationService(GSTNValidationService gstnValidationService) {
		this.gstnValidationService = gstnValidationService;
	}

	public CustomerAddresDAO getCustomerAddresDAO() {
		return customerAddresDAO;
	}

	public void setCustomerAddresDAO(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public void setGstDetailService(GSTDetailService gstDetailService) {
		this.gstDetailService = gstDetailService;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}
