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
 * * FileName : GuarantorDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-09-2013 * *
 * Modified Date : 10-09-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-09-2013 Pennant 0.1 * * 10-09-2013 Pennant 0.2 PSD 127030 formating Value * For Aadhar Number * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.guarantordetail;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
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

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.GuarantorDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.pff.extension.CustomerExtension;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.finance.financemain.JointAccountDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.ScreenCTL;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.MediaUtil;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/GuarantorDetail/guarantorDetailDialog.zul file.
 */
public class GuarantorDetailDialogCtrl extends GFCBaseCtrl<GuarantorDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(GuarantorDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_GuarantorDetailDialog;
	protected Row row0;
	protected Label label_FinReference;
	protected Hlayout hlayout_FinReference;
	protected Space space_FinReference;
	// protected Textbox finReference;
	protected Label label_BankCustomer;
	protected Hlayout hlayout_BankCustomer;
	protected Space space_BankCustomer;
	protected Checkbox bankCustomer;
	protected Row row1;
	protected Label label_GuarantorCIF;
	protected Hlayout hlayout_GuarantorCIF;
	protected Space space_GuarantorCIF;
	protected Textbox guarantorCIF;
	protected Button viewCustInfo;
	protected Label label_GuarantorIDType;
	protected Hlayout hlayout_GuarantorIDType;
	protected Space space_GuarantorIDType;
	protected Combobox guarantorIDType;
	protected Row row2;
	// protected Label label_GuarantorIDNumber;
	protected Hlayout hlayout_GuarantorIDNumber;
	protected Space space_GuarantorIDNumber;
	protected Textbox guarantorIDNumber;
	protected Label label_Name;
	protected Hlayout hlayout_Name;
	protected Space space_Name;
	protected Textbox guarantorCIFName;
	protected Row row3;
	protected Label label_GuranteePercentage;
	protected Hlayout hlayout_GuranteePercentage;
	protected Space space_GuranteePercentage;
	protected Decimalbox guranteePercentage;
	protected Label label_MobileNo;
	protected Hlayout hlayout_MobileNo;
	protected Space space_MobileNo;
	protected Textbox mobileNo;
	// protected Textbox phoneCountryCode;
	// protected Textbox phoneAreaCode;
	protected Row row4;
	protected Label label_EmailId;
	protected Hlayout hlayout_EmailId;
	protected Space space_EmailId;
	protected Textbox emailId;
	protected Label label_GuarantorProof;
	protected Hlayout hlayout_GuarantorProof;
	protected Space space_GuarantorProof;
	private byte[] guarantorProofContent;
	protected Row row5;
	protected Row row6;
	protected Label label_Remarks;
	protected Hlayout hlayout_Remarks;
	protected Space space_Remarks;
	protected Textbox remarks;
	protected Textbox status;
	protected Textbox worstStatus;
	protected Textbox guarantorProofName;
	protected Label recordType;
	protected Groupbox gb_statusDetails;
	protected Groupbox gb_basicDetails;
	protected Groupbox gb_GurantorsPrimaryExposure;
	protected Groupbox gb_GurantorsSecoundaryExposure;
	protected Groupbox gb_GurantorsExposure;

	// Address Components
	protected Textbox addrHNbr; // autoWired
	protected Space space_addrHNbr; // autoWired
	protected Textbox flatNbr; // autoWired
	protected Textbox addrStreet; // autoWired
	protected Space space_addrStreet; // autoWired
	protected Textbox addrLine1; // autoWired
	protected Textbox addrLine2; // autoWired
	protected Textbox poBox; // autoWired
	protected Space space_poBox; // autoWired
	protected ExtendedCombobox addrCity; // autoWired
	protected ExtendedCombobox addrProvince; // autoWired
	protected ExtendedCombobox addrCountry; // autoWired
	protected ExtendedCombobox addrPIN; // autoWired
	protected Textbox cityName; // autoWired

	protected Row row7;
	protected Space space_GenderCode;
	protected Combobox guarantorGenderCode;

	private boolean enqModule = false;
	private int index;
	// not auto wired vars
	private GuarantorDetail guarantorDetail; // overhanded per param
	private transient GuarantorDetailListCtrl guarantorDetailListCtrl; // overhanded per param

	protected Button btnSearchGuarantorCIF;
	protected Button btnUploadGuarantorProof;

	// ServiceDAOs / Domain Classes
	private transient GuarantorDetailService guarantorDetailService;
	private transient static PagedListService pagedListService;
	private List<ValueLabel> listGuarantorIDType = PennantAppUtil.getIdentityType();
	private boolean newRecord = false;
	private boolean newGuarantor = false;
	private JointAccountDetailDialogCtrl finJointAccountCtrl;
	private List<GuarantorDetail> guarantorDetailDetailList; // overhanded per
	// param
	private List<FinanceExposure> primaryList = null;
	private List<FinanceExposure> secoundaryList = null;
	private List<FinanceExposure> guarantorList = null;
	private String moduleType = "";
	protected Listbox listBoxGurantorsPrimaryExposure;
	protected Listbox listBoxGurantorsSecoundaryExposure;
	protected Listbox listBoxGurantorsExposure;
	BigDecimal totfinAmt = BigDecimal.ZERO;
	BigDecimal totCurrentAmt = BigDecimal.ZERO;
	BigDecimal totDueAmt = BigDecimal.ZERO;
	long recordCount = 0;
	String primaryCustId;
	int ccDecimal = 0;
	private String cif[] = null;
	Customer customer = null;
	private FinanceMain financeMain;
	private BigDecimal totSharePerc;
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	private boolean isEnqProcess = false;
	private boolean finsumryGurnatorEnq = false;

	/**
	 * default constructor.<br>
	 */
	public GuarantorDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "GuarantorDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected GuarantorDetail object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_GuarantorDetailDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_GuarantorDetailDialog);

		try {

			if (PennantConstants.CITY_FREETEXT) {
				this.addrCity.setVisible(false);
				this.cityName.setVisible(true);
			} else {
				this.addrCity.setVisible(true);
				this.cityName.setVisible(false);
			}
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			if (arguments.containsKey("moduleType")) {
				moduleType = (String) arguments.get("moduleType");
			}

			if (arguments.containsKey("totSharePerc")) {
				this.totSharePerc = (BigDecimal) arguments.get("totSharePerc");
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("guarantorDetail")) {
				this.guarantorDetail = (GuarantorDetail) arguments.get("guarantorDetail");
				GuarantorDetail befImage = new GuarantorDetail();
				if (this.guarantorDetail.getBefImage() == null) {
					BeanUtils.copyProperties(this.guarantorDetail, befImage);
					this.guarantorDetail.setBefImage(befImage);
				}
				setGuarantorDetail(this.guarantorDetail);
			} else {
				setGuarantorDetail(null);
			}
			if (arguments.containsKey("index")) {
				this.index = (Integer) arguments.get("index");
			}
			if (arguments.containsKey("ccDecimal")) {
				this.ccDecimal = (Integer) arguments.get("ccDecimal");
			}
			if (arguments.containsKey("filter")) {
				this.cif = (String[]) arguments.get("filter");
			}
			if (arguments.containsKey("finJointAccountCtrl")) {
				setFinanceMainDialogCtrl((JointAccountDetailDialogCtrl) arguments.get("finJointAccountCtrl"));
				setNewGuarantor(true);
				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.guarantorDetail.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					setRole((String) arguments.get("roleCode"));
					getUserWorkspace().allocateRoleAuthorities(getRole(), "GuarantorDetailDialog");
				}
			}
			if (arguments.containsKey("primaryCustID")) {
				primaryCustId = (String) arguments.get("primaryCustID");
			}
			if (arguments.containsKey("financeMain")) {
				setFinanceMain((FinanceMain) arguments.get("financeMain"));
			}

			doLoadWorkFlow(this.guarantorDetail.isWorkflow(), this.guarantorDetail.getWorkflowId(),
					this.guarantorDetail.getNextTaskId());
			if (isWorkFlowEnabled() && !enqModule && !isNewGuarantor()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "GuarantorDetailDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the guarantorsDetailListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete guarantorsDetail here.
			if (arguments.containsKey("guarantorDetailListCtrl")) {
				setGuarantorDetailListCtrl((GuarantorDetailListCtrl) arguments.get("guarantorDetailListCtrl"));
			} else {
				setGuarantorDetailListCtrl(null);
			}
			this.window_GuarantorDetailDialog.setHeight(this.borderLayoutHeight - 152 + "px");// 425px
			// this.finDocumentPdfView.setHeight(this.borderLayoutHeight - 152+
			// "px");// 425px
			// set Field Properties
			if (getGuarantorDetail().isBankCustomer()) {
				primaryList = getGuarantorDetailService().getPrimaryExposureList(getGuarantorDetail());
				secoundaryList = getGuarantorDetailService().getSecondaryExposureList(getGuarantorDetail());
				guarantorList = getGuarantorDetailService().getGuarantorExposureList(getGuarantorDetail());
			}
			if (enqiryModule) {
				this.moduleType = PennantConstants.MODULETYPE_ENQ;
			}
			if (arguments.containsKey("isEnqProcess")) {
				isEnqProcess = (Boolean) arguments.get("isEnqProcess");
				this.moduleType = PennantConstants.MODULETYPE_ENQ;
			}
			if (arguments.containsKey("CustomerEnq")) {
				setNewRecord(false);
				setNewGuarantor(false);
			}
			if (arguments.containsKey("finsumryGurnatorEnq")) {
				finsumryGurnatorEnq = (Boolean) arguments.get("finsumryGurnatorEnq");
			}
			doSetFieldProperties();
			doShowDialog(getGuarantorDetail());
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
		logger.debug("Entering" + event.toString());
		// displayComponents(ScreenCTL.SCRN_GNEDT);
		doEdit();
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
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doWriteBeanToComponents(this.guarantorDetail.getBefImage());
		// displayComponents(ScreenCTL.SCRN_GNINT);
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
		MessageUtil.showHelpWindow(event, window_GuarantorDetailDialog);
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
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug("Entering" + event.toString());
		try {
			ScreenCTL.displayNotes(getNotes("GuarantorDetail", String.valueOf(getGuarantorDetail().getGuarantorId()),
					getGuarantorDetail().getVersion()), this);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchGuarantorCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		customer = null;
		doClearMessage();
		doRemoveValidation();
		Clients.clearWrongValue(this.btnSearchGuarantorCIF);
		doSearchCustomerCIF();
	}

	/**
	 * do Fill customer data when select a cif
	 */
	private void setCustomerData() {
		if (this.guarantorCIF.getValue() == null || this.guarantorCIF.getValue().isEmpty()) {
			this.guarantorCIFName.setValue("");
			this.guarantorIDNumber.setValue("");
			this.mobileNo.setValue("");
			this.emailId.setValue("");
			this.guarantorProofName.setValue("");
			this.gb_GurantorsPrimaryExposure.setVisible(false);
			this.gb_GurantorsSecoundaryExposure.setVisible(false);
			this.gb_GurantorsExposure.setVisible(false);
		} else {
			customer = getCustomer(this.guarantorCIF.getValue());
			if (customer != null) {
				this.guarantorCIF.setValue(customer.getCustCIF());
				this.guarantorCIFName.setValue(customer.getCustShrtName());
				this.guarantorIDNumber.setValue(customer.getCustCRCPR());
				this.mobileNo.setValue(customer.getPhoneNumber());
				this.emailId.setValue(customer.getEmailID());
				dosetCustAddress(customer.getCustID());
			}
		}
		setCustomerDetails(customer);
	}

	/*
	 * Method to get the Customer Address Details when CustID is Entered
	 */

	public Customer getCustomer(String custCIF) {
		logger.debug("Entering");
		Customer customer = null;
		JdbcSearchObject<Customer> searchObject = new JdbcSearchObject<Customer>(Customer.class);
		searchObject.addTabelName("Customers_AEView");
		searchObject.addFilterEqual("CustCIF", custCIF);
		List<Customer> customers = pagedListService.getBySearchObject(searchObject);
		if (customers != null && !customers.isEmpty()) {
			return customers.get(0);
		}
		setCustomerData();
		logger.debug("Leaving");

		return customer;
	}

	/**
	 * Method for Showing Customer Search Window
	 */
	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING);

		Set<String> cifSet = new HashSet<>();

		for (int i = 0; i < this.cif.length; i++) {
			cifSet.add(this.cif[i]);
		}

		StringBuilder filter = new StringBuilder();

		for (String cif : cifSet) {
			if (filter.length() > 0) {
				filter.append(", ");
			}

			filter.append("'");
			filter.append(cif);
			filter.append("'");
		}

		String whereClause = " CustCIF not in (".concat(filter.toString()).concat(")");

		if (CustomerExtension.CUST_CORE_BANK_ID) {
			whereClause = "CustCoreBank not in (Select CustCoreBank From Customers Where CustCIF in ("
					.concat(filter.toString()).concat("))");
		}

		Map<String, Object> map = getDefaultArguments();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		map.put("whereClause", whereClause);

		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for setting Customer Details on Search Filters
	 * 
	 * @param nCustomer
	 * @param newSearchObject
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		this.guarantorCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.guarantorCIF.setValue(customer.getCustCIF());
			this.guarantorCIFName.setValue(customer.getCustShrtName());
			this.guarantorIDNumber.setValue(customer.getCustCRCPR());
			this.mobileNo.setValue(customer.getPhoneNumber());
			this.emailId.setValue(customer.getEmailID());
			dosetCustAddress(customer.getCustID());
			setCustomerDetails(customer);
		} else {
			this.guarantorCIF.setValue("");
		}
		logger.debug("Leaving ");
	}

	public Map<String, Object> getDefaultArguments() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("guarantorDetailDialogCtrl", this);
		map.put("moduleCode", moduleCode);
		return map;
	}

	public void setCustomerDetails(Customer customer) {
		if (customer != null) {
			getGuarantorDetail().setGuarantorCIF(customer.getCustCIF());
			getGuarantorDetail().setCustID(customer.getCustID());
			getGuarantorDetail().setStatus(customer.getLovDescCustStsName());
			getGuarantorDetail().setWorstStatus(getGuarantorDetailService().getWorstStaus(customer.getCustID()));
			this.primaryList = getGuarantorDetailService().getPrimaryExposureList(getGuarantorDetail());
			this.secoundaryList = getGuarantorDetailService().getSecondaryExposureList(getGuarantorDetail());
			this.guarantorList = getGuarantorDetailService().getGuarantorExposureList(getGuarantorDetail());

			if (this.primaryList != null) {
				doFillPrimaryExposureDetails(this.primaryList);
			}

			if (this.secoundaryList != null) {
				doFillSecoundaryExposureDetails(this.secoundaryList);
			}

			if (this.guarantorList != null) {
				doFillGuarantorExposureDetails(this.guarantorList);
			}

			setVisibleGrid();
		} else {
			this.primaryList = null;
			this.secoundaryList = null;
			this.guarantorList = null;
		}
	}

	public void onClick$viewCustInfo(Event event) {
		if ((!this.btnSearchGuarantorCIF.isDisabled()) && StringUtils.isEmpty(this.guarantorCIF.getValue())) {
			throw new WrongValueException(this.guarantorCIF, Labels.getLabel("FIELD_NO_INVALID",
					new String[] { Labels.getLabel("label_GuarantorDetailDialog_GuarantorCIF/ID.value") }));
		}
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("custCIF", this.guarantorCIF.getValue());

			customer = (Customer) PennantAppUtil.getCustomerObject(this.guarantorCIF.getValue(), null);
			map.put("custid", customer.getCustID());
			map.put("custShrtName", customer.getCustShrtName());

			if (getFinanceMain() != null && StringUtils.isNotEmpty(getFinanceMain().getFinReference())) {
				map.put("finFormatter", CurrencyUtil.getFormat(getFinanceMain().getFinCcy()));
				map.put("finReference", getFinanceMain().getFinReference());
			}
			map.put("finance", true);
			if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/FinCustomerDetailsEnq.zul",
						this.window_GuarantorDetailDialog, map);
			} else {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Enquiry/CustomerSummary.zul",
						this.window_GuarantorDetailDialog, map);
			}
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
		}
	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aGuarantorDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(GuarantorDetail aGuarantorDetail) throws InterruptedException {
		logger.debug("Entering");
		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.bankCustomer.focus();
		} else {
			if (isNewGuarantor()) {
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
			doFillPrimaryExposureDetails(this.primaryList);
			doFillSecoundaryExposureDetails(this.secoundaryList);
			doFillGuarantorExposureDetails(this.guarantorList);
			setVisibleGrid();

			// fill the components with the data
			doWriteBeanToComponents(aGuarantorDetail);
			// set ReadOnly mode accordingly if the object is new or not.
			// displayComponents(ScreenCTL.getMode(enqModule,isWorkFlowEnabled(),aGuarantorDetail.isNewRecord()));
			onCheck$bankCustomer(new Event("onCheck$bankCustomer"));
			if (this.primaryList == null || this.primaryList.size() == 0) {
				this.gb_GurantorsPrimaryExposure.setVisible(false);
			}
			if (this.secoundaryList == null || this.secoundaryList.size() == 0) {
				this.gb_GurantorsSecoundaryExposure.setVisible(false);
			}
			if (this.guarantorList == null || this.guarantorList.size() == 0) {
				this.gb_GurantorsExposure.setVisible(false);
			}
			this.window_GuarantorDetailDialog.setHeight("90%");
			this.window_GuarantorDetailDialog.setWidth("90%");
			if (finsumryGurnatorEnq) {
				dofinSummaryReadOnly();
				this.window_GuarantorDetailDialog.doModal();
			}
			if (isNewGuarantor()) {
				this.groupboxWf.setVisible(false);
				this.window_GuarantorDetailDialog.doModal();
			} else {
				if (!finsumryGurnatorEnq) {
					setDialog(DialogType.EMBEDDED);
				}
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (isNewRecord()) {
			if (isNewGuarantor()) {
				this.btnCancel.setVisible(false);
			}
		} else {
			this.btnCancel.setVisible(true);
			if (!enqModule) {
				this.guranteePercentage.setReadonly(isReadOnly("GuarantorDetailDialog_GuranteePercentage"));
			} else {
				this.guranteePercentage.setReadonly(true);
				this.viewCustInfo.setVisible(false);
			}

		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.guarantorDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (newGuarantor) {
				if ("ENQ".equals(this.moduleType)) {
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				} else if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newGuarantor);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewGuarantor()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
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
		// Address Details
		if (finsumryGurnatorEnq) {
			this.addrHNbr.setReadonly(true);
			this.flatNbr.setReadonly(true);
			this.addrStreet.setReadonly(true);
			this.addrLine1.setReadonly(true);
			this.addrLine2.setReadonly(true);
			this.poBox.setReadonly(true);
			this.addrCountry.setReadonly(true);
			this.addrProvince.setReadonly(true);
			this.addrCity.setReadonly(true);
			this.cityName.setReadonly(true);
			this.addrPIN.setReadonly(true);
		}

		logger.debug("Leaving");
	}

	public void readOnlyExposureFields(boolean exposure) {
		if (exposure) {
			if (getGuarantorDetail().isBankCustomer()) {
				this.gb_GurantorsPrimaryExposure.setVisible(true);
				this.gb_GurantorsSecoundaryExposure.setVisible(true);
				this.gb_GurantorsExposure.setVisible(true);
			} else {
				this.gb_GurantorsPrimaryExposure.setVisible(false);
				this.gb_GurantorsSecoundaryExposure.setVisible(false);
				this.gb_GurantorsExposure.setVisible(false);
			}
			this.row0.setVisible(false);
			this.row5.setVisible(true);
			this.row4.setVisible(false);
			this.row6.setVisible(true);
			// this.btnSearchGuarantorCIF.setVisible(false);
		} else {
			this.gb_GurantorsPrimaryExposure.setVisible(false);
			this.gb_GurantorsSecoundaryExposure.setVisible(false);
			this.gb_GurantorsExposure.setVisible(false);
			this.row0.setVisible(true);
			this.row5.setVisible(true);
			this.row4.setVisible(true);
			this.row6.setVisible(false);
			// this.btnSearchGuarantorCIF.setVisible(true);
			this.emailId.setValue("");
			this.mobileNo.setValue("");
			this.guarantorCIF.setValue("");
			this.guarantorIDNumber.setValue("");
			this.guarantorCIFName.setValue("");
			this.remarks.setValue("");
			this.guarantorProofName.setValue("");

			// Address Details
			this.addrHNbr.setValue("");
			this.flatNbr.setValue("");
			this.addrStreet.setValue("");
			this.addrLine1.setValue("");
			this.addrLine2.setValue("");
			this.poBox.setValue("");
			this.addrCountry.setValue("");
			this.addrProvince.setValue("");
			this.addrCity.setValue("");
			this.cityName.setValue("");
			this.addrPIN.setValue("");
			// Address Details
			if (finsumryGurnatorEnq) {
				this.addrHNbr.setReadonly(true);
				this.flatNbr.setReadonly(true);
				this.addrStreet.setReadonly(true);
				this.addrLine1.setReadonly(true);
				this.addrLine2.setReadonly(true);
				this.poBox.setReadonly(true);
				this.addrCountry.setReadonly(true);
				this.addrProvince.setReadonly(true);
				this.addrCity.setReadonly(true);
				this.cityName.setReadonly(true);
				this.addrPIN.setReadonly(true);
			}
		}
	}

	// Helpers

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		if (!enqModule) {
			getUserWorkspace().allocateAuthorities("GuarantorDetailDialog", getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_GuarantorDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_GuarantorDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_GuarantorDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_GuarantorDetailDialog_btnSave"));
			this.btnSearchGuarantorCIF
					.setVisible(getUserWorkspace().isAllowed("button_GuarantorDetailDialog_btnSearchGuarantorCIF"));
		}
		/* create the Button Controller. Disable not used buttons during working */
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		// this.finReference.setMaxlength(20);
		this.guarantorIDNumber.setMaxlength(20);
		this.guarantorCIFName.setMaxlength(100);
		this.guranteePercentage.setMaxlength(5);
		this.guranteePercentage.setFormat(PennantConstants.rateFormate2);
		this.guranteePercentage.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.guranteePercentage.setScale(2);
		this.mobileNo.setMaxlength(13);
		this.emailId.setMaxlength(200);
		this.remarks.setMaxlength(500);
		this.guarantorProofName.setMaxlength(500);
		this.worstStatus.setMaxlength(100);

		this.addrHNbr.setMaxlength(50);
		this.flatNbr.setMaxlength(50);
		this.addrStreet.setMaxlength(50);
		this.addrLine1.setMaxlength(50);
		this.addrLine2.setMaxlength(50);
		this.poBox.setMaxlength(8);

		this.addrCountry.setMaxlength(2);
		this.addrCountry.setTextBoxWidth(121);
		this.addrCountry.setSpacing("2px");
		this.addrCountry.setMandatoryStyle(true);
		this.addrCountry.setModuleName("Country");
		this.addrCountry.setValueColumn("CountryCode");
		this.addrCountry.setDescColumn("CountryDesc");
		this.addrCountry.setValidateColumns(new String[] { "CountryCode" });

		this.addrProvince.setMaxlength(8);
		this.addrProvince.setTextBoxWidth(121);
		this.addrCountry.setSpacing("2px");
		this.addrProvince.setMandatoryStyle(true);
		this.addrProvince.setModuleName("Province");
		this.addrProvince.setValueColumn("CPProvince");
		this.addrProvince.setDescColumn("CPProvinceName");
		this.addrProvince.setValidateColumns(new String[] { "CPProvince" });

		this.addrCity.setMaxlength(8);
		this.addrCity.setTextBoxWidth(121);
		this.addrCountry.setSpacing("2px");
		this.addrCity.setMandatoryStyle(false);
		this.addrCity.setModuleName("City");
		this.addrCity.setValueColumn("PCCity");
		this.addrCity.setDescColumn("PCCityName");
		this.addrCity.setValidateColumns(new String[] { "PCCity" });
		this.cityName.setMaxlength(8);

		this.addrPIN.setMaxlength(50);
		this.addrPIN.setTextBoxWidth(121);
		this.addrPIN.setMandatoryStyle(true);
		this.addrPIN.setModuleName("PinCode");
		this.addrPIN.setValueColumn("PinCodeId");
		this.addrPIN.setDescColumn("AreaName");
		this.addrPIN.setValueType(DataType.LONG);
		this.addrPIN.setValidateColumns(new String[] { "PinCodeId" });
		this.addrPIN.setInputAllowed(false);

		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aGuarantorDetail GuarantorDetail
	 */
	public void doWriteBeanToComponents(GuarantorDetail aGuarantorDetail) {
		logger.debug("Entering");
		// this.finReference.setValue(aGuarantorDetail.getFinReference());
		this.bankCustomer.setChecked(aGuarantorDetail.isBankCustomer());
		if (!aGuarantorDetail.isBankCustomer()) {
			fillComboBox(this.guarantorIDType, aGuarantorDetail.getGuarantorIDType(), listGuarantorIDType, "");
			fillComboBox(this.guarantorGenderCode, aGuarantorDetail.getGuarantorGenderCode(),
					PennantAppUtil.getGenderCodes(), "");
		} else {
			fillComboBox(this.guarantorIDType, PennantConstants.List_Select, listGuarantorIDType, "");
		}
		this.guarantorCIF.setValue(aGuarantorDetail.getGuarantorCIF());
		this.guarantorCIFName.setValue(aGuarantorDetail.getGuarantorCIFName());
		this.guarantorIDNumber.setValue(aGuarantorDetail.getGuarantorIDNumber());
		this.guranteePercentage.setValue(aGuarantorDetail.getGuranteePercentage());
		this.mobileNo.setValue(aGuarantorDetail.getMobileNo());
		this.emailId.setValue(aGuarantorDetail.getEmailId());
		this.guarantorProofContent = aGuarantorDetail.getGuarantorProof();
		this.guarantorProofName.setValue(aGuarantorDetail.getGuarantorProofName());
		this.remarks.setValue(aGuarantorDetail.getRemarks());
		this.status.setValue(aGuarantorDetail.getStatus());
		this.worstStatus.setValue(aGuarantorDetail.getWorstStatus());

		if (!aGuarantorDetail.isBankCustomer()) {
			this.addrHNbr.setValue(aGuarantorDetail.getAddrHNbr());
			this.flatNbr.setValue(aGuarantorDetail.getFlatNbr());
			this.addrStreet.setValue(aGuarantorDetail.getAddrStreet());
			this.addrLine1.setValue(aGuarantorDetail.getAddrLine1());
			this.addrLine2.setValue(aGuarantorDetail.getAddrLine2());
			this.poBox.setValue(aGuarantorDetail.getPOBox());
			this.addrCountry.setValue(aGuarantorDetail.getAddrCountry());
			this.addrProvince.setValue(aGuarantorDetail.getAddrProvince());
			this.addrCity.setValue(aGuarantorDetail.getAddrCity());
			this.addrPIN.setValue(aGuarantorDetail.getAddrZIP());
			this.addrPIN.setDescription(aGuarantorDetail.getLovDescAddrZip());
			this.addrCountry.setDescription(aGuarantorDetail.getLovDescAddrCountryName());
			this.addrProvince.setDescription(aGuarantorDetail.getLovDescAddrProvinceName());
			this.addrCity.setDescription(aGuarantorDetail.getLovDescAddrCityName());
			this.cityName.setValue(aGuarantorDetail.getAddrCity());
		} else {
			dosetCustAddress(getCustData(aGuarantorDetail.getGuarantorCIF()));
		}
		this.recordStatus.setValue(aGuarantorDetail.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aGuarantorDetail.getRecordType()));

		getguarantorIdNumber();

		if (!StringUtils.isEmpty(this.addrCountry.getValue())) {
			Filter[] filter = new Filter[1];
			filter[0] = new Filter("CPCountry", this.addrCountry.getValue(), Filter.OP_EQUAL);
			this.addrCountry.setFilters(filter);
		}

		if (!StringUtils.isEmpty(this.addrProvince.getValue())) {
			Filter[] filter = new Filter[1];
			filter[0] = new Filter("CPProvince", this.addrProvince.getValue(), Filter.OP_EQUAL);
			this.addrProvince.setFilters(filter);
		}

		if (!StringUtils.isEmpty(this.addrCity.getValue())) {
			Filter[] filter = new Filter[1];
			filter[0] = new Filter("PCCity", this.addrCity.getValue(), Filter.OP_EQUAL);
			this.addrCity.setFilters(filter);
		}

		if (!StringUtils.isEmpty(this.addrPIN.getValue())) {
			Filter[] filter = new Filter[1];
			filter[0] = new Filter("PinCode", this.addrPIN.getValue(), Filter.OP_EQUAL);
			this.addrPIN.setFilters(filter);
		}
		logger.debug("Leaving");
	}

	String toCcy = SysParamUtil.getAppCurrency();
	int DFT_CURR_EDIT_FIELD = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);

	// ================Primary Exposure Details
	public void doFillPrimaryExposureDetails(List<FinanceExposure> primaryExposureList) {
		logger.debug("Entering");
		this.listBoxGurantorsPrimaryExposure.getItems().clear();
		if (primaryExposureList != null) {

			BigDecimal totFinaceAmout = BigDecimal.ZERO;
			BigDecimal totCurrentExposer = BigDecimal.ZERO;
			BigDecimal totOverDueAmount = BigDecimal.ZERO;

			recordCount = primaryExposureList.size();
			for (FinanceExposure primaryExposure : primaryExposureList) {
				Listitem listitem = new Listitem();
				Listcell listcell;
				listcell = new Listcell(primaryExposure.getFinType());
				listitem.appendChild(listcell);
				listcell = new Listcell(primaryExposure.getFinReference());
				listitem.appendChild(listcell);
				listcell = new Listcell(DateUtility.formatToLongDate(primaryExposure.getFinStartDate()));
				listitem.appendChild(listcell);
				listcell = new Listcell(DateUtility.formatToLongDate(primaryExposure.getMaturityDate()));
				listitem.appendChild(listcell);
				listcell = new Listcell(primaryExposure.getFinCCY());
				listitem.appendChild(listcell);
				totFinaceAmout = totFinaceAmout.add(primaryExposure.getFinanceAmt());
				listcell = new Listcell(PennantApplicationUtil.amountFormate(primaryExposure.getFinanceAmt(),
						primaryExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				BigDecimal currentExpoSure = CalculationUtil.getConvertedAmount(primaryExposure.getFinCCY(), toCcy,
						primaryExposure.getCurrentExpoSure());
				totCurrentExposer = totCurrentExposer.add(currentExpoSure);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(primaryExposure.getCurrentExpoSure(),
						primaryExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				listcell = new Listcell();
				if (primaryExposure.getOverdueAmt() != null
						&& primaryExposure.getOverdueAmt().compareTo(BigDecimal.ZERO) != 0) {
					listcell.setLabel(PennantConstants.YES);
				} else {
					listcell.setLabel(PennantConstants.NO);
				}
				listitem.appendChild(listcell);
				listcell = new Listcell(primaryExposure.getPastdueDays());
				listitem.appendChild(listcell);
				BigDecimal overdueAmt = CalculationUtil.getConvertedAmount(primaryExposure.getFinCCY(), toCcy,
						primaryExposure.getOverdueAmt());
				totOverDueAmount = totOverDueAmount.add(overdueAmt);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(primaryExposure.getOverdueAmt(),
						primaryExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				listitem.setAttribute("data", primaryExposure);
				this.listBoxGurantorsPrimaryExposure.appendChild(listitem);
			}
			String footerStyle1 = "text-align:right; font-weight:bold;";

			Listitem item = new Listitem();
			Listcell lc = null;

			lc = new Listcell("TOTAL");
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle("text-align:left; font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(Long.toString(recordCount));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totCurrentExposer, DFT_CURR_EDIT_FIELD));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");

			lc = new Listcell(PennantApplicationUtil.amountFormate(totOverDueAmount, DFT_CURR_EDIT_FIELD));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);
			item.setParent(this.listBoxGurantorsPrimaryExposure);
			getGuarantorDetail().setPrimaryExposure(String.valueOf(totCurrentExposer));
		}
		logger.debug("Leaving");
	}

	public void doFillSecoundaryExposureDetails(List<FinanceExposure> secondaryExposureList) {
		logger.debug("Entering");
		this.listBoxGurantorsSecoundaryExposure.getItems().clear();
		if (secondaryExposureList != null) {
			BigDecimal totFinaceAmout = BigDecimal.ZERO;
			BigDecimal totCurrentExposer = BigDecimal.ZERO;
			BigDecimal totOverDueAmount = BigDecimal.ZERO;

			recordCount = secondaryExposureList.size();
			for (FinanceExposure secondaryExposure : secondaryExposureList) {
				Listitem listitem = new Listitem();
				Listcell listcell;

				listcell = new Listcell(secondaryExposure.getFinType());
				listitem.appendChild(listcell);

				listcell = new Listcell(secondaryExposure.getFinReference());
				listitem.appendChild(listcell);

				listcell = new Listcell(DateUtility.formatToLongDate(secondaryExposure.getFinStartDate()));
				listitem.appendChild(listcell);

				listcell = new Listcell(DateUtility.formatToLongDate(secondaryExposure.getMaturityDate()));
				listitem.appendChild(listcell);

				listcell = new Listcell(secondaryExposure.getFinCCY());
				listitem.appendChild(listcell);

				totFinaceAmout = totFinaceAmout.add(secondaryExposure.getFinanceAmt());
				listcell = new Listcell(PennantApplicationUtil.amountFormate(secondaryExposure.getFinanceAmt(),
						secondaryExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);

				BigDecimal currentExpoSure = CalculationUtil.getConvertedAmount(secondaryExposure.getFinCCY(), toCcy,
						secondaryExposure.getCurrentExpoSure());
				totCurrentExposer = totCurrentExposer.add(currentExpoSure);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(secondaryExposure.getCurrentExpoSure(),
						secondaryExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);

				listcell = new Listcell();
				if (secondaryExposure.getOverdueAmt() != null
						&& secondaryExposure.getOverdueAmt().compareTo(BigDecimal.ZERO) != 0) {
					listcell.setLabel(PennantConstants.YES);
				} else {
					listcell.setLabel(PennantConstants.NO);
				}
				listitem.appendChild(listcell);

				listcell = new Listcell(secondaryExposure.getPastdueDays());
				listitem.appendChild(listcell);

				BigDecimal overdueAmt = CalculationUtil.getConvertedAmount(secondaryExposure.getFinCCY(), toCcy,
						secondaryExposure.getOverdueAmt());
				totOverDueAmount = totOverDueAmount.add(overdueAmt);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(secondaryExposure.getOverdueAmt(),
						secondaryExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);

				listcell = new Listcell(secondaryExposure.getCustCif());
				listitem.appendChild(listcell);

				listitem.setAttribute("data", secondaryExposure);
				this.listBoxGurantorsSecoundaryExposure.appendChild(listitem);
			}
			String footerStyle1 = "text-align:right; font-weight:bold;";

			Listitem item = new Listitem();
			Listcell lc = null;

			lc = new Listcell("TOTAL");
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle("text-align:left; font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(Long.toString(recordCount));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totCurrentExposer, DFT_CURR_EDIT_FIELD));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totOverDueAmount, DFT_CURR_EDIT_FIELD));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			getGuarantorDetail().setSecondaryExposure(String.valueOf(totCurrentExposer));
			this.listBoxGurantorsSecoundaryExposure.appendChild(item);
		}
		logger.debug("Leaving");
	}

	public void doFillGuarantorExposureDetails(List<FinanceExposure> guarantorExposureList) {
		logger.debug("Entering");
		this.listBoxGurantorsExposure.getItems().clear();
		if (guarantorExposureList != null) {
			BigDecimal totFinaceAmout = BigDecimal.ZERO;
			BigDecimal totCurrentExposer = BigDecimal.ZERO;
			BigDecimal totOverDueAmount = BigDecimal.ZERO;

			recordCount = guarantorExposureList.size();
			for (FinanceExposure guarantorExposure : guarantorExposureList) {
				Listitem listitem = new Listitem();
				Listcell listcell;

				listcell = new Listcell(guarantorExposure.getFinType());
				listitem.appendChild(listcell);

				listcell = new Listcell(guarantorExposure.getFinReference());
				listitem.appendChild(listcell);

				listcell = new Listcell(DateUtility.formatToLongDate(guarantorExposure.getFinStartDate()));
				listitem.appendChild(listcell);

				listcell = new Listcell(DateUtility.formatToLongDate(guarantorExposure.getMaturityDate()));
				listitem.appendChild(listcell);

				listcell = new Listcell(guarantorExposure.getFinCCY());
				listitem.appendChild(listcell);

				totFinaceAmout = totFinaceAmout.add(guarantorExposure.getFinanceAmt());
				listcell = new Listcell(PennantApplicationUtil.amountFormate(guarantorExposure.getFinanceAmt(),
						guarantorExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);

				BigDecimal currentExpoSure = CalculationUtil.getConvertedAmount(guarantorExposure.getFinCCY(), toCcy,
						guarantorExposure.getCurrentExpoSure());
				totCurrentExposer = totCurrentExposer.add(currentExpoSure);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(guarantorExposure.getCurrentExpoSure(),
						guarantorExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);

				listcell = new Listcell();
				if (guarantorExposure.getOverdueAmt() != null
						&& guarantorExposure.getOverdueAmt().compareTo(BigDecimal.ZERO) != 0) {
					listcell.setLabel(PennantConstants.YES);
				} else {
					listcell.setLabel(PennantConstants.NO);
				}
				listitem.appendChild(listcell);

				listcell = new Listcell(guarantorExposure.getPastdueDays());
				listitem.appendChild(listcell);

				BigDecimal overdueAmt = CalculationUtil.getConvertedAmount(guarantorExposure.getFinCCY(), toCcy,
						guarantorExposure.getOverdueAmt());
				totOverDueAmount = totOverDueAmount.add(overdueAmt);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(guarantorExposure.getOverdueAmt(),
						guarantorExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);

				listcell = new Listcell(guarantorExposure.getCustCif());
				listitem.appendChild(listcell);

				listitem.setAttribute("data", guarantorExposure);
				this.listBoxGurantorsExposure.appendChild(listitem);
			}
			String footerStyle1 = "text-align:right; font-weight:bold;";

			Listitem item = new Listitem();
			Listcell lc = null;

			lc = new Listcell("TOTAL");
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle("text-align:left; font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(Long.toString(recordCount));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totCurrentExposer, DFT_CURR_EDIT_FIELD));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totOverDueAmount, DFT_CURR_EDIT_FIELD));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			getGuarantorDetail().setGuarantorExposure(String.valueOf(totCurrentExposer));
			this.listBoxGurantorsExposure.appendChild(item);
		}
		logger.debug("Leaving");
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
			ErrorControl.showErrorControl(this.window_GuarantorDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Uploading Proof Details File
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void onUpload$btnUploadGuarantorProof(UploadEvent event) throws IOException {
		logger.debug("Entering" + event.toString());
		Media media = event.getMedia();

		List<DocType> allowed = new ArrayList<>();
		allowed.add(DocType.PDF);
		allowed.add(DocType.JPG);
		allowed.add(DocType.JPEG);
		allowed.add(DocType.PNG);
		allowed.add(DocType.MSG);
		allowed.add(DocType.DOC);
		allowed.add(DocType.DOCX);
		allowed.add(DocType.XLS);
		allowed.add(DocType.XLSX);
		allowed.add(DocType.ZIP);
		allowed.add(DocType.Z7);
		allowed.add(DocType.RAR);
		allowed.add(DocType.TXT);

		if (!MediaUtil.isValid(media, allowed)) {
			MessageUtil.showError(Labels.getLabel("UnSupported_Document_V2"));
			return;
		}

		this.guarantorProofName.setValue(media.getName());
		this.guarantorProofContent = IOUtils.toByteArray(media.getStreamData());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aGuarantorDetail
	 */
	public void doWriteComponentsToBean(GuarantorDetail aGuarantorDetail) {
		logger.debug("Entering");
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Bank Customer
		try {
			aGuarantorDetail.setBankCustomer(this.bankCustomer.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Guarantor CIF
		try {
			aGuarantorDetail.setGuarantorCIFName(this.guarantorCIFName.getValue());
			aGuarantorDetail.setGuarantorCIF(this.guarantorCIF.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// ID Type
		try {
			String strGuarantorIDType = null;
			if (this.guarantorIDType.getSelectedItem() != null) {
				strGuarantorIDType = this.guarantorIDType.getSelectedItem().getValue().toString();
			}
			if (strGuarantorIDType != null && !PennantConstants.List_Select.equals(strGuarantorIDType)) {
				aGuarantorDetail.setGuarantorIDType(strGuarantorIDType);
				aGuarantorDetail.setGuarantorIDTypeName(this.guarantorIDType.getSelectedItem().getLabel());
			} else {
				aGuarantorDetail.setGuarantorIDType(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// ID Number
		try {
			setGurantorIDNumProp();
			getguarantorIdNumber();
			if (!this.bankCustomer.isChecked()) {
				aGuarantorDetail.setGuarantorIDNumber(this.guarantorIDNumber.getValue());
			}
			if (this.guarantorIDType.getSelectedIndex() != 0) {
				if (this.guarantorIDType.getSelectedItem().getValue().toString().equals(PennantConstants.CPRCODE)) {
					// ### 10-05-2018 - Development PSD 127030
					aGuarantorDetail.setGuarantorIDNumber(this.guarantorIDNumber.getValue());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Name
		try {
			aGuarantorDetail.setGuarantorCIFName(this.guarantorCIFName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Percentage
		try {
			if (this.guranteePercentage.getValue() != null) {
				aGuarantorDetail.setGuranteePercentage(this.guranteePercentage.getValue());
			} else {
				aGuarantorDetail.setGuranteePercentage(BigDecimal.ZERO);
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.guranteePercentage.isReadonly()
					&& this.guranteePercentage.getValue().compareTo(BigDecimal.ZERO) <= 0) {
				throw new WrongValueException(this.guranteePercentage, Labels.getLabel("NUMBER_MINVALUE",
						new String[] { Labels.getLabel("label_GuarantorDetailDialog_GuranteePercentage.value"), "0" }));
			}
			if (this.guranteePercentage.getValue() != null && this.guranteePercentage.intValue() != 0) {
				if ((this.totSharePerc.add(this.guranteePercentage.getValue())).compareTo(new BigDecimal(100)) > 0) {
					BigDecimal availableSharePerc = new BigDecimal(100).subtract(this.totSharePerc);
					throw new WrongValueException(this.guranteePercentage,
							Labels.getLabel("Total_Percentage",
									new String[] {
											Labels.getLabel("label_GuarantorDetailDialog_GuranteePercentage.value"),
											availableSharePerc.toString() }));
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Mobile No
		try {
			aGuarantorDetail.setMobileNo(this.mobileNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Email Id
		try {
			aGuarantorDetail.setEmailId(this.emailId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Proof
		try {
			aGuarantorDetail.setGuarantorProof(guarantorProofContent);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Proof Name
		try {
			/*
			 * if (!this.bankCustomer.isChecked() && (StringUtils.isBlank(this.guarantorProofName.getValue()) ||
			 * this.guarantorProofContent == null)) { throw new WrongValueException(this.guarantorProofName,
			 * Labels.getLabel("MUST_BE_UPLOADED", new String[] {
			 * Labels.getLabel("label_GuarantorDetailDialog_GuarantorProof.value")})); }
			 */
			aGuarantorDetail.setGuarantorProofName(this.guarantorProofName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Remarks
		try {
			aGuarantorDetail.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// city name and addrcity

		try {
			if (PennantConstants.CITY_FREETEXT) {
				aGuarantorDetail.setAddrCity(this.cityName.getValue());
			} else {
				aGuarantorDetail.setLovDescAddrCityName(this.addrCity.getDescription());
				aGuarantorDetail.setAddrCity(this.addrCity.getValidatedValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Address Details
		if (!this.bankCustomer.isChecked()) {
			try {
				aGuarantorDetail.setAddrHNbr(this.addrHNbr.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aGuarantorDetail.setFlatNbr(this.flatNbr.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aGuarantorDetail.setAddrStreet(this.addrStreet.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aGuarantorDetail.setAddrLine1(this.addrLine1.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aGuarantorDetail.setAddrLine2(this.addrLine2.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aGuarantorDetail.setPOBox(this.poBox.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aGuarantorDetail.setLovDescAddrCountryName(getLovDescription(this.addrCountry.getDescription()));
				aGuarantorDetail.setAddrCountry(this.addrCountry.getValidatedValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aGuarantorDetail.setLovDescAddrProvinceName(this.addrProvince.getDescription());
				aGuarantorDetail.setAddrProvince(this.addrProvince.getValidatedValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {

				if (PennantConstants.CITY_FREETEXT) {
					aGuarantorDetail.setAddrCity(StringUtils.trimToNull(this.cityName.getValue()));
				} else {
					aGuarantorDetail.setLovDescAddrCityName(StringUtils.trimToNull(this.addrCity.getDescription()));
					aGuarantorDetail.setAddrCity(StringUtils.trimToNull(this.addrCity.getValidatedValue()));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aGuarantorDetail.setLovDescAddrZip(this.addrPIN.getDescription());
				aGuarantorDetail.setAddrZIP(this.addrPIN.getValidatedValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if ("#".equals(getComboboxValue(this.guarantorGenderCode))) {
					if (!this.guarantorGenderCode.isDisabled()) {
						throw new WrongValueException(this.guarantorGenderCode, Labels.getLabel("STATIC_INVALID",
								new String[] { Labels.getLabel("label_GuarantorDetailDialog_GenderCode.value") }));
					} else {
						aGuarantorDetail.setGuarantorGenderCode(null);
					}
				} else {
					aGuarantorDetail.setGuarantorGenderCode(getComboboxValue(this.guarantorGenderCode));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			if (customer != null) {
				aGuarantorDetail.setCustID(this.customer.getCustID());
			}
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
		aGuarantorDetail.setRecordStatus(this.recordStatus.getValue());
		setGuarantorDetail(aGuarantorDetail);
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		doClearMessage();
		if (this.bankCustomer.isChecked() && this.guarantorCIF.getValue().isEmpty()) {
			this.guarantorCIF.setConstraint(
					new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_GuarantorCIF.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true, true));
		}
		// Percentage
		if (!this.guranteePercentage.isReadonly()) {
			this.guranteePercentage.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_GuarantorDetailDialog_GuranteePercentage.value"), 2, true, false, 100));
		}
		if (!this.bankCustomer.isChecked()) {
			// ID Type
			if (this.guarantorIDType.isReadonly()) {
				this.guarantorIDType.setConstraint(new StaticListValidator(listGuarantorIDType,
						Labels.getLabel("label_GuarantorDetailDialog_GuarantorIDType.value")));
			}
			if (!this.guarantorIDNumber.isReadonly()) {

				if (StringUtils.equals(this.guarantorIDType.getSelectedItem().getValue().toString(),
						PennantConstants.CPRCODE)) {
					this.guarantorIDNumber.setConstraint(new PTStringValidator(
							Labels.getLabel("label_GuarantorDetailDialog_GuarantorIDNumber.value"),
							PennantRegularExpressions.REGEX_AADHAR_NUMBER, true));

				} else if (StringUtils.equals(this.guarantorIDType.getSelectedItem().getValue().toString(),
						PennantConstants.PANNUMBER)) {
					if (this.guarantorIDNumber.getConstraint() != null) {
						this.guarantorIDNumber.setConstraint("");
					}

					this.guarantorIDNumber.setConstraint(new PTStringValidator(
							Labels.getLabel("label_GuarantorDetailDialog_GuarantorIDNumber.value"),
							PennantRegularExpressions.REGEX_PANNUMBER, true));
				} else {
					this.guarantorIDNumber.setConstraint(new PTStringValidator(
							Labels.getLabel("label_GuarantorDetailDialog_GuarantorIDNumber.value"), null, true));
				}

			}
			// Name
			if (!this.guarantorCIFName.isReadonly()) {
				this.guarantorCIFName
						.setConstraint(new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_Name.value"),
								PennantRegularExpressions.REGEX_ACCOUNT_HOLDER_NAME, true));
			}
			// Mobile No
			/*
			 * if (!this.mobileNo.isReadonly()) { this.mobileNo.setConstraint(new
			 * SimpleConstraint(PennantRegularExpressions.MOBILE_REGEX, Labels.getLabel("MAND_FIELD_PHONENUM", new
			 * String[] { Labels.getLabel("label_GuarantorDetailDialog_MobileNo.value") }))); }
			 */
			if (!this.mobileNo.isReadonly()) {
				this.mobileNo.setConstraint(
						new PTMobileNumberValidator(Labels.getLabel("label_GuarantorDetailDialog_MobileNo.value"), true,
								PennantRegularExpressions.REGEX_MOBILE));
			}
			// Email Id
			if (!this.emailId.isReadonly()) {
				this.emailId.setConstraint(
						new PTEmailValidator(Labels.getLabel("label_GuarantorDetailDialog_EmailId.value"),
								ImplementationConstants.GUARANTOR_EMAIL_MANDATORY));
			}

			if (!this.addrHNbr.isReadonly()) {
				this.addrHNbr.setConstraint(
						new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_AddrHNbr.value"),
								PennantRegularExpressions.REGEX_ADDRESS, true));
			}

			if (!this.flatNbr.isReadonly()) {
				this.flatNbr.setConstraint(
						new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_FlatNbr.value"),
								PennantRegularExpressions.REGEX_ADDRESS, false));
			}

			boolean addressConstraint = false;
			if (StringUtils.isBlank(this.addrStreet.getValue()) && StringUtils.isBlank(this.addrLine1.getValue())
					&& StringUtils.isBlank(this.addrLine2.getValue())) {
				addressConstraint = true;
			}
			if (!this.addrStreet.isReadonly() && addressConstraint) {
				this.addrStreet.setConstraint(
						new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_AddrStreet.value"),
								PennantRegularExpressions.REGEX_ADDRESS, true));
			}

			if (!this.addrLine1.isReadonly() && addressConstraint) {
				this.addrLine1.setConstraint(
						new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_AddrLine1.value"),
								PennantRegularExpressions.REGEX_ADDRESS, false));
			}

			if (!this.addrLine2.isReadonly() && addressConstraint) {
				this.addrLine2.setConstraint(
						new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_AddrLine2.value"),
								PennantRegularExpressions.REGEX_ADDRESS, false));
			}

			if (!this.poBox.isReadonly()) {
				this.poBox
						.setConstraint(new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_POBox.value"),
								PennantRegularExpressions.REGEX_NUMERIC, false));
			}

			if (PennantConstants.CITY_FREETEXT) {
				if (!this.cityName.isReadonly()) {
					this.cityName.setConstraint(
							new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_CityName.value"),
									PennantRegularExpressions.REGEX_NAME, false));
				}
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		// this.finReference.setConstraint("");
		this.guarantorIDType.setConstraint("");
		this.guarantorIDNumber.setConstraint("");
		this.guarantorCIF.setConstraint("");
		this.guarantorCIFName.setConstraint("");
		this.guranteePercentage.setConstraint("");
		this.mobileNo.setConstraint("");
		this.emailId.setConstraint("");
		this.guarantorProofName.setConstraint("");
		this.guarantorGenderCode.setConstraint("");

		this.addrHNbr.setConstraint("");
		this.flatNbr.setConstraint("");
		this.addrStreet.setConstraint("");
		this.addrLine1.setConstraint("");
		this.addrLine2.setConstraint("");
		this.poBox.setConstraint("");
		this.cityName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		// Guarantor CIF
		if (!btnSearchGuarantorCIF.isVisible()) {
			this.guarantorCIFName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_Name.value"), null, true));
		}
		this.addrCountry.setConstraint(new PTStringValidator(
				Labels.getLabel("label_GuarantorDetailDialog_AddrCountry.value"), null, true, true));

		this.addrProvince.setConstraint(new PTStringValidator(
				Labels.getLabel("label_GuarantorDetailDialog_AddrProvince.value"), null, true, true));

		if (!PennantConstants.CITY_FREETEXT) {
			this.addrCity.setConstraint(new PTStringValidator(
					Labels.getLabel("label_GuarantorDetailDialog_AddrCity.value"), null, false, true));
		}
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveLOVValidation() {
		this.guarantorCIFName.setConstraint("");
		this.addrCountry.setConstraint("");
		this.addrProvince.setConstraint("");
		this.addrCity.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		// this.finReference.setErrorMessage("");
		this.guarantorCIFName.setErrorMessage("");
		this.guarantorCIF.clearErrorMessage();
		this.guarantorCIF.setErrorMessage("");
		this.guarantorIDType.setErrorMessage("");
		this.guarantorIDNumber.setErrorMessage("");
		this.guranteePercentage.setErrorMessage("");
		this.mobileNo.setErrorMessage("");
		this.emailId.setErrorMessage("");
		this.guarantorProofName.setErrorMessage("");
		this.guarantorGenderCode.setErrorMessage("");
		this.remarks.setErrorMessage("");
		this.addrHNbr.setErrorMessage("");
		this.flatNbr.setErrorMessage("");
		this.addrStreet.setErrorMessage("");
		this.addrLine1.setErrorMessage("");
		this.addrLine2.setErrorMessage("");
		this.poBox.setErrorMessage("");
		this.addrPIN.setErrorMessage("");
		this.addrCountry.setErrorMessage("");
		this.addrProvince.setErrorMessage("");
		this.addrCity.setErrorMessage("");
		this.addrPIN.setErrorMessage("");
		this.cityName.setErrorMessage("");
		logger.debug("Leaving");
	}

	public void onCheck$bankCustomer(Event event) {
		logger.debug("Entering");

		doClearMessage();
		doRemoveValidation();
		if (this.bankCustomer.isChecked()) {
			this.guarantorIDType.setDisabled(true);
			this.guarantorCIF.setReadonly(true);
			this.btnSearchGuarantorCIF
					.setVisible(getUserWorkspace().isAllowed("button_GuarantorDetailDialog_btnSearchGuarantorCIF"));
			this.guarantorIDNumber.setDisabled(true);
			this.guarantorIDNumber.setReadonly(true);
			this.guarantorCIFName.setReadonly(true);
			this.mobileNo.setReadonly(true);
			this.emailId.setReadonly(true);
			this.btnUploadGuarantorProof.setVisible(false);
			this.hlayout_GuarantorCIF.setVisible(true);
			this.hlayout_GuarantorIDNumber.setVisible(false);
			this.space_GuarantorCIF.setVisible(true);
			this.space_GuranteePercentage.setVisible(true);
			fillComboBox(this.guarantorIDType, PennantConstants.List_Select, listGuarantorIDType, "");
			this.space_GuarantorIDType.setSclass("");
			this.space_GuarantorIDNumber.setSclass("");
			this.space_Name.setSclass("");
			this.space_MobileNo.setSclass("");
			this.space_EmailId.setSclass("");
			this.space_GuarantorProof.setSclass("");
			this.guarantorProofName.setValue("");
			this.guarantorProofContent = null;

			this.row7.setVisible(false);
			this.guarantorGenderCode.setDisabled(true);
			fillComboBox(this.guarantorGenderCode, PennantConstants.List_Select, PennantAppUtil.getGenderCodes(), "");

			// Address details
			this.addrHNbr.setReadonly(true);
			this.space_addrHNbr.setSclass("");
			this.flatNbr.setReadonly(true);
			this.addrStreet.setReadonly(true);
			this.space_addrStreet.setSclass("");
			this.addrLine1.setReadonly(true);
			this.addrLine2.setReadonly(true);
			this.poBox.setReadonly(true);
			this.space_poBox.setSclass("");
			this.addrCountry.setReadonly(true);
			this.addrProvince.setReadonly(true);
			this.addrCity.setReadonly(true);
			this.addrPIN.setReadonly(true);
			this.cityName.setReadonly(true);
			this.space_GuarantorCIF.setSclass(PennantConstants.mandateSclass);
			this.space_GuarantorIDNumber.setSclass(PennantConstants.mandateSclass);

		} else {
			this.guranteePercentage.setReadonly(isReadOnly("GuarantorDetailDialog_GuranteePercentage"));
			this.guarantorIDType.setDisabled(isReadOnly("GuarantorDetailDialog_GuarantorIDType"));
			this.guarantorCIF.setReadonly(isReadOnly("GuarantorDetailDialog_GuarantorCIF"));
			this.btnSearchGuarantorCIF.setVisible(false);
			this.guarantorIDNumber.setDisabled(isReadOnly("GuarantorDetailDialog_GuarantorIDNumber"));
			this.guarantorIDNumber.setReadonly(isReadOnly("GuarantorDetailDialog_GuarantorIDNumber"));
			this.guarantorCIFName.setReadonly(isReadOnly("GuarantorDetailDialog_GuarantorCIFName"));
			this.mobileNo.setReadonly(isReadOnly("GuarantorDetailDialog_MobileNo"));
			this.emailId.setReadonly(isReadOnly("GuarantorDetailDialog_EmailId"));
			this.btnUploadGuarantorProof.setVisible(true);
			this.hlayout_GuarantorCIF.setVisible(false);
			this.hlayout_GuarantorIDNumber.setVisible(true);
			this.space_GuarantorIDType.setVisible(true);
			this.space_GuarantorIDNumber.setVisible(true);
			this.space_Name.setVisible(true);
			this.space_MobileNo.setVisible(true);
			this.space_EmailId.setVisible(true);
			this.space_GuarantorProof.setVisible(true);
			this.space_GuarantorCIF.setVisible(true);
			this.space_GuranteePercentage.setVisible(true);
			this.space_GuarantorIDType.setSclass(PennantConstants.mandateSclass);
			this.space_GuarantorIDNumber.setSclass(PennantConstants.mandateSclass);
			if (ImplementationConstants.GUARANTOR_EMAIL_MANDATORY) {
				this.space_EmailId.setSclass(PennantConstants.mandateSclass);
			} else {
				this.space_EmailId.setSclass("");
			}
			this.space_Name.setSclass(PennantConstants.mandateSclass);
			this.space_MobileNo.setSclass(PennantConstants.mandateSclass);
			// this.space_GuarantorProof.setSclass(PennantConstants.mandateSclass);

			this.addrHNbr.setReadonly(isReadOnly("GuarantorDetailDialog_addrHNbr"));
			this.flatNbr.setReadonly(isReadOnly("GuarantorDetailDialog_flatNbr"));
			this.addrStreet.setReadonly(isReadOnly("GuarantorDetailDialog_addrStreet"));
			this.addrLine1.setReadonly(isReadOnly("GuarantorDetailDialog_addrLine1"));
			this.addrLine2.setReadonly(isReadOnly("GuarantorDetailDialog_addrLine2"));
			this.poBox.setReadonly(isReadOnly("GuarantorDetailDialog_poBox"));
			this.addrCountry.setReadonly(isReadOnly("GuarantorDetailDialog_addrCountry"));
			this.addrProvince.setReadonly(isReadOnly("GuarantorDetailDialog_addrProvince"));
			this.addrCity.setReadonly(isReadOnly("GuarantorDetailDialog_addrCity"));
			this.cityName.setReadonly(isReadOnly("GuarantorDetailDialog_addrCity"));
			this.addrPIN.setReadonly(isReadOnly("GuarantorDetailDialog_addrZIP"));
			this.space_addrHNbr.setSclass(PennantConstants.mandateSclass);
			this.space_addrStreet.setSclass(PennantConstants.mandateSclass);
			this.addrCountry.setMandatoryStyle(true);
			this.addrProvince.setMandatoryStyle(true);
			this.addrCity.setMandatoryStyle(false);
			this.space_GuarantorCIF.setSclass("");
			this.space_GuarantorIDNumber.setSclass("");

			this.row7.setVisible(true);
			this.space_GenderCode.setSclass(PennantConstants.mandateSclass);
			this.guarantorGenderCode.setDisabled(isReadOnly("GuarantorDetailDialog_GuarantorGenderCode"));
		}
		if (!isNewRecord()) {
			readOnlyExposureFields(true);
			this.guarantorCIF.setReadonly(true);
			// this.guarantorCIFName.setReadonly(true);
			this.guarantorIDNumber.setReadonly(true);
			this.guarantorIDType.setDisabled(true);
			this.guarantorProofName.setReadonly(true);
			// this.mobileNo.setReadonly(true);
			// this.emailId.setReadonly(true);
		} else {
			readOnlyExposureFields(false);
		}

		if (enqModule) {
			this.btnSearchGuarantorCIF.setVisible(false);
			this.guarantorCIFName.setReadonly(true);
			this.mobileNo.setReadonly(true);
			this.emailId.setReadonly(true);
			this.guranteePercentage.setReadonly(true);
			this.guarantorGenderCode.setDisabled(true);
			this.addrHNbr.setReadonly(!isReadOnly("GuarantorDetailDialog_addrHNbr"));
			this.flatNbr.setReadonly(!isReadOnly("GuarantorDetailDialog_flatNbr"));
			this.addrStreet.setReadonly(!isReadOnly("GuarantorDetailDialog_addrStreet"));
			this.addrLine1.setReadonly(!isReadOnly("GuarantorDetailDialog_addrLine1"));
			this.addrLine2.setReadonly(!isReadOnly("GuarantorDetailDialog_addrLine2"));
			this.poBox.setReadonly(!isReadOnly("GuarantorDetailDialog_poBox"));
			this.addrCountry.setReadonly(!isReadOnly("GuarantorDetailDialog_addrCountry"));
			this.addrProvince.setReadonly(!isReadOnly("GuarantorDetailDialog_addrProvince"));
			this.addrCity.setReadonly(!isReadOnly("GuarantorDetailDialog_addrCity"));
			this.cityName.setReadonly(!isReadOnly("GuarantorDetailDialog_addrCity"));
			this.addrPIN.setReadonly(!isReadOnly("GuarantorDetailDialog_addrZIP"));
		}
		logger.debug("Leaving");
	}

	public void onChange$guarantorIDType(Event event) {
		logger.debug("Entering" + event.toString());
		this.guarantorIDNumber.setErrorMessage("");
		this.guarantorIDNumber.setValue("");
		this.space_GuarantorIDNumber.setSclass(PennantConstants.mandateSclass);
		if (!StringUtils.equals(this.guarantorIDType.getSelectedItem().getValue().toString(),
				PennantConstants.NOT_AVAILABLE)) {
			this.guarantorIDNumber.setConstraint("");
		} else {
			this.space_GuarantorIDNumber.setSclass("");
		}
		logger.debug("Leaving" + event.toString());
	}

	private void setGurantorIDNumProp() {
		if (!this.guarantorIDType.getSelectedItem().getValue().equals(PennantConstants.List_Select)) {
			if (this.guarantorIDType.getSelectedItem().getValue().toString().equals(PennantConstants.CPRCODE)) {
				this.guarantorIDNumber.setMaxlength(20);
				this.guarantorIDNumber.setConstraint(
						new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_GuarantorCIF/ID.value"),
								PennantRegularExpressions.REGEX_AADHAR_NUMBER, true));
			} else if (this.guarantorIDType.getSelectedItem().getValue().toString()
					.equals(PennantConstants.PANNUMBER)) {
				this.guarantorIDNumber.setConstraint(
						new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_GuarantorCIF/ID.value"),
								PennantRegularExpressions.REGEX_PANNUMBER, true));
			} else {
				this.guarantorIDNumber.setMaxlength(20);
				this.guarantorIDNumber.setConstraint(
						new PTStringValidator(Labels.getLabel("label_GuarantorDetailDialog_GuarantorIDNumber.value"),
								PennantRegularExpressions.REGEX_ALPHANUM, true));
			}
		}
	}

	protected boolean doCustomDelete(final GuarantorDetail aGuarantorDetail, String tranType) {
		if (isNewGuarantor()) {
			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = newGuarantorDetailProcess(aGuarantorDetail, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_GuarantorDetailDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getFinanceMainDialogCtrl().doFillGurantorsDetails(this.guarantorDetailDetailList);
				return true;
			}
		}
		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final GuarantorDetail aGuarantorDetail = new GuarantorDetail();
		BeanUtils.copyProperties(getGuarantorDetail(), aGuarantorDetail);

		final String keyReference = (aGuarantorDetail.isBankCustomer()
				? Labels.getLabel("label_GuarantorDetailDialog_GuarantorCIF/ID.value") + " : "
						+ aGuarantorDetail.getGuarantorCIF()
				: Labels.getLabel("label_GuarantorDetailDialog_GuarantorIDType.value") + " : "
						+ aGuarantorDetail.getGuarantorIDType());

		doDelete(keyReference, aGuarantorDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.guarantorCIF.setValue("");
		this.guarantorCIFName.setValue("");
		this.guarantorIDType.setSelectedIndex(0);
		this.guarantorGenderCode.setSelectedIndex(0);
		this.guarantorIDNumber.setValue("");
		this.guarantorCIFName.setValue("");
		this.guranteePercentage.setValue("0");
		this.mobileNo.setValue("");
		this.emailId.setValue("");
		this.guarantorProofName.setValue("");
		this.addrHNbr.setValue("");
		this.flatNbr.setValue("");
		this.addrStreet.setValue("");
		this.addrLine1.setValue("");
		this.addrLine2.setValue("");
		this.poBox.setValue("");
		this.addrCountry.setValue("");
		this.addrCountry.setDescription("");
		this.addrProvince.setValue("");
		this.addrProvince.setDescription("");
		this.addrCity.setValue("");
		this.addrCity.setDescription("");
		this.addrPIN.setValue("");
		this.remarks.setValue("");
		this.cityName.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final GuarantorDetail aGuarantorDetail = new GuarantorDetail();
		BeanUtils.copyProperties(getGuarantorDetail(), aGuarantorDetail);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the DocumentDetails object with the components data
		doWriteComponentsToBean(aGuarantorDetail);
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		isNew = aGuarantorDetail.isNewRecord();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aGuarantorDetail.getRecordType())) {
				aGuarantorDetail.setVersion(aGuarantorDetail.getVersion() + 1);
				if (isNew) {
					aGuarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aGuarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aGuarantorDetail.setNewRecord(true);
				}
			}
		} else {
			if (isNewGuarantor()) {
				if (isNewRecord()) {
					aGuarantorDetail.setVersion(1);
					aGuarantorDetail.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
				if (StringUtils.isBlank(aGuarantorDetail.getRecordType())) {
					aGuarantorDetail.setVersion(aGuarantorDetail.getVersion() + 1);
					aGuarantorDetail.setRecordType(PennantConstants.RCD_UPD);
					aGuarantorDetail.setNewRecord(true);
				}
				if (aGuarantorDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aGuarantorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aGuarantorDetail.setVersion(aGuarantorDetail.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}
		// save it to database
		try {
			if (isNewGuarantor()) {
				AuditHeader auditHeader = newGuarantorDetailProcess(aGuarantorDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_GuarantorDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getFinanceMainDialogCtrl().doFillGurantorsDetails(this.guarantorDetailDetailList);
					closeDialog();
				}
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
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
	private AuditHeader newGuarantorDetailProcess(GuarantorDetail aGuarantorDetail, String tranType) {
		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aGuarantorDetail, tranType);
		guarantorDetailDetailList = new ArrayList<GuarantorDetail>();
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = aGuarantorDetail.getFinReference();

		boolean dupicateRecord = false;

		if (aGuarantorDetail.isBankCustomer()) {
			valueParm[1] = aGuarantorDetail.getGuarantorCIF();
		} else {
			if (aGuarantorDetail.getGuarantorIDType().equals(PennantConstants.CPRCODE)) {
				valueParm[1] = PennantApplicationUtil.formatEIDNumber(aGuarantorDetail.getGuarantorIDNumber());
			} else {
				valueParm[1] = aGuarantorDetail.getGuarantorIDNumber();
			}
		}

		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_GuarantorCIF") + ":" + valueParm[1];
		// Checks whether jointAccount custCIF is same as actual custCIF
		if (StringUtils.isNotBlank(aGuarantorDetail.getGuarantorCIF())
				&& StringUtils.trimToEmpty(primaryCustId).equals(aGuarantorDetail.getGuarantorCIF())) {
			auditHeader.setErrorDetails(
					ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
							getUserWorkspace().getUserLanguage()));
		}

		List<GuarantorDetail> guarantorDetailList = getFinanceMainDialogCtrl().getGuarantorDetailList();
		if (guarantorDetailList != null && !guarantorDetailList.isEmpty()) {
			for (GuarantorDetail guarantorDetail : guarantorDetailList) {
				if (!(aGuarantorDetail.isBankCustomer()) && guarantorDetail.isBankCustomer()) {
					if (guarantorDetail.getGuarantorCIF().equals(aGuarantorDetail.getGuarantorIDNumber())) {
						dupicateRecord = true;
					}
				} else if (aGuarantorDetail.isBankCustomer()) {
					if (StringUtils.trimToEmpty(guarantorDetail.getGuarantorCIF())
							.equals(aGuarantorDetail.getGuarantorCIF())) {
						dupicateRecord = true;
					}
				} else if (StringUtils.trimToEmpty(guarantorDetail.getGuarantorIDTypeName())
						.equals(StringUtils.trimToEmpty(aGuarantorDetail.getGuarantorIDTypeName()))) {
					if (guarantorDetail.getGuarantorIDNumber().equals(aGuarantorDetail.getGuarantorIDNumber())) {
						dupicateRecord = true;
					}
				}
				if (dupicateRecord) {
					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aGuarantorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aGuarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							guarantorDetailDetailList.add(aGuarantorDetail);
						} else if (aGuarantorDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aGuarantorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aGuarantorDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							guarantorDetailDetailList.add(aGuarantorDetail);
						} else if (aGuarantorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
						}
					} else {
						guarantorDetailDetailList.add(guarantorDetail);
					}
				} else {
					guarantorDetailDetailList.add(guarantorDetail);
				}
				dupicateRecord = false;
			}
		}
		if (PennantConstants.TRAN_UPD.equals(tranType)) {
			this.guarantorDetailDetailList.remove(index);
			this.guarantorDetailDetailList.add(guarantorDetail);
			recordAdded = true;
		}
		if (!recordAdded) {
			guarantorDetailDetailList.add(aGuarantorDetail);
		}
		return auditHeader;
	}

	// WorkFlow Components

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(GuarantorDetail aGuarantorDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aGuarantorDetail.getBefImage(), aGuarantorDetail);
		return new AuditHeader(String.valueOf(aGuarantorDetail.getGuarantorId()), null, null, null, auditDetail,
				aGuarantorDetail.getUserDetails(), getOverideMap());
	}

	private void setVisibleGrid() {
		if (this.primaryList == null || this.primaryList.size() == 0) {
			this.gb_GurantorsPrimaryExposure.setVisible(false);
		} else {
			this.gb_GurantorsPrimaryExposure.setVisible(true);
		}

		if (this.secoundaryList == null || this.secoundaryList.size() == 0) {
			this.gb_GurantorsSecoundaryExposure.setVisible(false);
		} else {
			this.gb_GurantorsSecoundaryExposure.setVisible(true);
		}

		if (this.guarantorList == null || this.guarantorList.size() == 0) {
			this.gb_GurantorsExposure.setVisible(false);
		} else {
			this.gb_GurantorsExposure.setVisible(true);
		}

	}

	/**
	 * OnChange IdNumber Calling the Method To set EIDNumber Format
	 */
	public void onChange$guarantorIDNumber(Event event) {
		logger.debug("Entering" + event.toString());
		getguarantorIdNumber();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to Set The Format For EIDNumber
	 */
	public void getguarantorIdNumber() {
		logger.debug("Entering");
		if (this.guarantorIDType.getSelectedIndex() != 0) {
			if (this.guarantorIDType.getSelectedItem().getValue().toString().equals(PennantConstants.CPRCODE)) {
				// ### 10-05-2018 - Start- Development PSD 127030
				this.guarantorIDNumber.setValue(this.guarantorIDNumber.getValue());
			} else {
				this.guarantorIDNumber.setValue(this.guarantorIDNumber.getValue());
			}

		}
		logger.debug("Leaving");
	}

	private String getLovDescription(String value) {
		value = StringUtils.trimToEmpty(value);

		try {
			value = StringUtils.split(value, "-", 2)[1];
		} catch (Exception e) {
			//
		}

		return value;
	}

	/*
	 * Method to get the Customer Address Details when CustID is Entered
	 */

	public CustomerAddres getCustAddress(long custID) {
		logger.debug("Entering");
		CustomerAddres customerAddress = null;
		JdbcSearchObject<CustomerAddres> searchObject = new JdbcSearchObject<CustomerAddres>(CustomerAddres.class);
		searchObject.addTabelName("CustomerAddresses_View");
		searchObject.addFilterEqual("CustID", custID);
		List<CustomerAddres> custAddress = pagedListService.getBySearchObject(searchObject);
		if (custAddress != null && !custAddress.isEmpty()) {
			return custAddress.get(0);
		}
		logger.debug("Leaving");

		return customerAddress;
	}

	public void onFulfill$addrCountry(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = addrCountry.getObject();
		if (dataObject instanceof String) {
			this.addrProvince.setValue("");
			this.addrProvince.setDescription("");
			this.addrCity.setValue("");
			this.addrCity.setDescription("");
			this.addrPIN.setValue("");
			this.addrPIN.setDescription("");
		} else {
			Country country = (Country) dataObject;
			if (country != null) {

				String countryCode = country.getCountryCode();

				fillProvinceDetails(countryCode);
				fillCitydetails(countryCode, null);
				fillPindetails(null, null, countryCode);

			} else {
				fillProvinceDetails(null);
				fillCitydetails(null, null);
				fillPindetails(null, null, null);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$addrProvince(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = addrProvince.getObject();
		if (dataObject == null) {
			fillCitydetails(this.addrCountry.getValue(), null);
			fillPindetails(null, null, this.addrCountry.getValue());
		} else if (dataObject instanceof String) {
			this.addrCity.setValue("");
			this.addrCity.setDescription("");
			this.addrPIN.setValue("");
			this.addrPIN.setDescription("");
		} else if (dataObject instanceof Province) {
			Province province = (Province) dataObject;
			this.addrProvince.setErrorMessage("");
			String state = this.addrProvince.getValue();
			String countryCode = province.getCPCountry();

			this.addrCity.setValue(countryCode);
			fillCitydetails(countryCode, state);
			fillPindetails(null, state, countryCode);

		}

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$addrCity(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = addrCity.getObject();
		if (dataObject instanceof String) {
			this.addrCity.setValue(String.valueOf(dataObject));
			this.addrCity.setDescription("");
			this.addrPIN.setValue("");
			this.addrPIN.setDescription("");
		} else {
			City city = (City) dataObject;
			if (city != null) {
				this.addrCity.setErrorMessage("");

				this.addrProvince.setValue(city.getPCProvince());
				this.addrProvince.setDescription(city.getLovDescPCProvinceName());
				this.addrCountry.setValue(city.getPCCountry());
				this.addrCountry.setDescription(city.getLovDescPCCountryName());

				String cityValue = this.addrCity.getValue();
				String countryCode = city.getPCCountry();
				String state = city.getPCProvince();

				fillPindetails(cityValue, state, countryCode);
			} else {
				fillPindetails(null, this.addrProvince.getValue(), this.addrCountry.getValue());
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$addrPIN(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = addrPIN.getObject();
		if (dataObject instanceof String) {
			this.addrPIN.setValue("");
			this.addrPIN.setDescription("");

		} else {
			PinCode pinCode = (PinCode) dataObject;
			if (pinCode != null) {
				this.addrCity.setValue(pinCode.getCity());
				this.addrCity.setDescription(pinCode.getPCCityName());
				this.addrProvince.setValue(pinCode.getPCProvince());
				this.addrProvince.setDescription(pinCode.getLovDescPCProvinceName());
				this.addrCountry.setValue(pinCode.getpCCountry());
				this.addrCountry.setDescription(pinCode.getLovDescPCCountryName());

				this.addrCity.setErrorMessage("");
				this.addrProvince.setErrorMessage("");
				this.addrPIN.setErrorMessage("");
				this.addrPIN.setAttribute("pinCodeId", pinCode.getPinCodeId());
				this.addrPIN.setValue(pinCode.getPinCode());
			}
		}

		logger.debug("Leaving");
	}

	private void fillProvinceDetails(String country) {
		this.addrProvince.setValue("");
		this.addrProvince.setObject(null);
		this.addrProvince.setDescription("");
		this.addrProvince.setErrorMessage("");

		Filter[] filters = new Filter[2];

		if (!StringUtils.isEmpty(country)) {
			filters[0] = new Filter("CPCountry", country, Filter.OP_EQUAL);
		}
		filters[1] = new Filter("CPIsActive", 1, Filter.OP_EQUAL);

		this.addrProvince.setFilters(filters);
	}

	private void fillCitydetails(String country, String state) {
		logger.debug("Entering");

		this.addrCity.setValue("");
		this.addrCity.setObject(null);
		this.addrCity.setDescription("");
		this.addrCity.setErrorMessage("");

		Filter[] filters = new Filter[3];

		if (!StringUtils.isEmpty(country)) {
			filters[0] = new Filter("PCCountry", country, Filter.OP_EQUAL);
		}

		if (!StringUtils.isEmpty(state)) {

			filters[1] = new Filter("PCProvince", state, Filter.OP_EQUAL);
		}

		filters[2] = new Filter("CITYISACTIVE", 1, Filter.OP_EQUAL);
		this.addrCity.setFilters(filters);

		logger.debug("Leaving");
	}

	public void fillPindetails(String city, String state, String country) {
		logger.debug("Entering");

		this.addrPIN.setValue("");
		this.addrPIN.setObject(null);
		this.addrPIN.setDescription("");
		this.addrPIN.setErrorMessage("");

		Filter[] filters = new Filter[4];

		if (!StringUtils.isEmpty(city)) {
			filters[0] = new Filter("City", city, Filter.OP_EQUAL);
		}

		if (!StringUtils.isEmpty(state)) {
			filters[1] = new Filter("PCProvince", state, Filter.OP_EQUAL);
		}

		if (!StringUtils.isEmpty(country)) {
			filters[2] = new Filter("PCCountry", country, Filter.OP_EQUAL);
		}

		filters[3] = new Filter("Active", 1, Filter.OP_EQUAL);

		this.addrPIN.setFilters(filters);

		logger.debug(Literal.LEAVING);
	}

	public void dosetCustAddress(long custID) {
		CustomerAddres customerAddress = getCustAddress(custID);
		if (customerAddress != null) {
			this.addrHNbr.setValue(customerAddress.getCustAddrHNbr());
			this.addrStreet.setValue(customerAddress.getCustAddrStreet());
			this.addrCity.setValue(customerAddress.getCustAddrCity(), customerAddress.getLovDescCustAddrCityName());
			this.addrCountry.setValue(customerAddress.getCustAddrCountry(),
					customerAddress.getLovDescCustAddrCountryName());
			this.addrProvince.setValue(customerAddress.getCustAddrProvince(),
					customerAddress.getLovDescCustAddrProvinceName());
			this.addrLine1.setValue(customerAddress.getCustAddrLine1());
			this.addrLine2.setValue(customerAddress.getCustAddrLine2());
			this.poBox.setValue(customerAddress.getCustPOBox());
			this.flatNbr.setValue(customerAddress.getCustFlatNbr());
			this.addrPIN.setValue(customerAddress.getCustAddrZIP(), customerAddress.getLovDescCustAddrZip());

		}
	}

	/*
	 * Method to get the CustID From Customers
	 */

	public long getCustData(String CustCif) {
		logger.debug("Entering");
		long custID = 0;
		JdbcSearchObject<Customer> searchObject = new JdbcSearchObject<Customer>(Customer.class);
		searchObject.addTabelName("Customers");
		searchObject.addField("CustID");
		searchObject.addFilterEqual("CustCIF", CustCif);
		List<Customer> custData = pagedListService.getBySearchObject(searchObject);
		if (custData != null && !custData.isEmpty()) {
			return custData.get(0).getCustID();
		}
		logger.debug("Leaving");

		return custID;
	}

	public void dofinSummaryReadOnly() {
		logger.debug("Entering");

		// Address Details
		if (finsumryGurnatorEnq) {
			this.addrHNbr.setReadonly(true);
			this.flatNbr.setReadonly(true);
			this.addrStreet.setReadonly(true);
			this.addrLine1.setReadonly(true);
			this.addrLine2.setReadonly(true);
			this.poBox.setReadonly(true);
			this.addrCountry.setReadonly(true);
			this.addrProvince.setReadonly(true);
			this.addrCity.setReadonly(true);
			this.cityName.setReadonly(true);
			this.addrPIN.setReadonly(true);
		}

		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	public GuarantorDetail getGuarantorDetail() {
		return this.guarantorDetail;
	}

	public void setGuarantorDetail(GuarantorDetail guarantorDetail) {
		this.guarantorDetail = guarantorDetail;
	}

	public void setGuarantorDetailService(GuarantorDetailService guarantorDetailService) {
		this.guarantorDetailService = guarantorDetailService;
	}

	public GuarantorDetailService getGuarantorDetailService() {
		return this.guarantorDetailService;
	}

	public void setGuarantorDetailListCtrl(GuarantorDetailListCtrl guarantorDetailListCtrl) {
		this.guarantorDetailListCtrl = guarantorDetailListCtrl;
	}

	public GuarantorDetailListCtrl getGuarantorDetailListCtrl() {
		return this.guarantorDetailListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public JointAccountDetailDialogCtrl getFinanceMainDialogCtrl() {
		return finJointAccountCtrl;
	}

	public void setFinanceMainDialogCtrl(JointAccountDetailDialogCtrl finJointAccountCtrl) {
		this.finJointAccountCtrl = finJointAccountCtrl;
	}

	public boolean isNewGuarantor() {
		return newGuarantor;
	}

	public void setNewGuarantor(boolean newGuarantor) {
		this.newGuarantor = newGuarantor;
	}

	public List<GuarantorDetail> getGuarantorDetailDetailList() {
		return guarantorDetailDetailList;
	}

	public void setGuarantorDetailDetailList(List<GuarantorDetail> guarantorDetailDetailList) {
		this.guarantorDetailDetailList = guarantorDetailDetailList;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

}
