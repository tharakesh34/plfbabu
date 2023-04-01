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
 * * FileName : JointAccountDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-09-2013 * *
 * Modified Date : 10-09-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-09-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.jointaccountdetail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.extension.CustomerExtension;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.ScreenCTL;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.service.sampling.SamplingService;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/JointAccountDetail/jointAccountDetailDialog.zul file.
 */
public class JointAccountDetailDialogCtrl extends GFCBaseCtrl<JointAccountDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(JointAccountDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_MasterJointAccountDetailDialog;
	protected Row row0;
	protected Label label_CustCIF;
	protected Hbox hbox_CustCIF;
	protected Space space_CustCIF;
	protected Textbox custCIF;
	protected Longbox custID;
	protected Button viewCustInfo;
	protected Button btn_NewCust;
	protected Button btn_EditCust;
	protected Label label_CustCIFName;
	protected Hbox hbox_CustCIFName;
	protected Space space_CustCIFName;
	protected Textbox custCIFName;
	protected Row row1;
	protected Label label_IncludeRepay;
	protected Hbox hbox_IncludeRepay;
	protected Space space_IncludeRepay;
	protected Checkbox includeRepay;
	protected Label label_RepayAccountId;
	protected AccountSelectionBox repayAccountId;

	protected Row row2;
	protected Label label_CustCIFStatus;
	protected Hbox hbox_CustCIFStatus;
	protected Space space_CustCIFStatus;
	protected Textbox custCIFStatus;
	protected Label label_CustCIFWorstStatus;
	protected Hbox hbox_CustCIFWorstStatus;
	protected Space space_CustCIFWorstStatus;
	protected Textbox custCIFWorstStatus;

	protected Row row3;
	protected Label label_CatOfCoApplicant;
	protected Hbox hbox_CatOfCoApplicant;
	protected Space space_CatOfCoApplicant;
	protected Combobox catOfCoApplicant;

	protected Checkbox includeIncome;

	protected Checkbox authoritySignatory;
	protected Intbox sequence;
	protected Hbox hbox_Sequence;
	protected Label label_Sequence;

	protected Label recordType;
	protected Groupbox gb_statusDetails;
	private boolean enqModule = false;
	private int index;
	// Exposure List fields
	protected Groupbox gb_JointAccountPrimaryJoint;
	protected Groupbox gb_JointAccountSecondaryJoint;
	protected Groupbox gb_JointAccountGuarantorJoint;
	protected Listbox listBox_JointAccountPrimary;
	protected Listbox listBox_JointAccountSecondary;
	protected Listbox listBox_JointAccountGuarantor;
	protected Groupbox gb_EmptyExposure;
	private FinanceExposure sumPrimaryDetails = null;
	private FinanceExposure sumSecondaryDetails = null;
	private FinanceExposure sumGurantorDetails = null;
	// not auto wired vars
	private com.pennant.webui.finance.financemain.JointAccountDetailDialogCtrl finJointAccountCtrl;
	private FinanceMain financeMain;
	private FinanceDetail financeDetail = null;
	private JointAccountDetail jointAccountDetail;
	private List<JointAccountDetail> jointAccountDetailList; // overhanded per
	// param
	private String moduleType = "";
	private transient JointAccountDetailListCtrl jointAccountDetailListCtrl; // overhanded per param
	protected Button btnSearchCustCIF;

	// ServiceDAOs / Domain Classes
	private transient JointAccountDetailService jointAccountDetailService;
	private transient PagedListService pagedListService;
	private boolean newRecord = false;
	private boolean newContributor = false;
	private List<FinanceExposure> primaryList = null;
	private List<FinanceExposure> secoundaryList = null;
	private List<FinanceExposure> guarantorList = null;
	BigDecimal totfinAmt = BigDecimal.ZERO;
	BigDecimal totCurrentAmt = BigDecimal.ZERO;
	BigDecimal totDueAmt = BigDecimal.ZERO;
	long recordCount = 0;
	String primaryCustId;
	int ccyEditField = 0;
	String finCcy = "";
	private String cif[] = null;
	private String coapplicantCif[] = null;
	Customer customer = null;
	private int baseCcyDecFormat = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);
	private LovFieldDetail lovFieldDetail = PennantAppUtil.getcoApplicants();
	@Autowired
	CustomerDetailsService customerDetailsService;
	@Autowired
	private SamplingService samplingService;
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	public String newCustCIF;
	public String applicationNo;
	public String leadId;
	private List<JointAccountDetail> tempJointAccountDetailList = null;

	/**
	 * default constructor.<br>
	 */
	public JointAccountDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "JointAccountDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected JointAccountDetail object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_MasterJointAccountDetailDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_MasterJointAccountDetailDialog);

		try {

			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}
			if (arguments.containsKey("moduleType")) {
				moduleType = (String) arguments.get("moduleType");
			}

			if (arguments.containsKey("jointAccountDetailList")) {
				tempJointAccountDetailList = (List<JointAccountDetail>) arguments.get("jointAccountDetailList");
			}
			// READ OVERHANDED params !
			if (arguments.containsKey("jointAccountDetail")) {
				this.jointAccountDetail = (JointAccountDetail) arguments.get("jointAccountDetail");
				JointAccountDetail befImage = new JointAccountDetail();
				if (this.jointAccountDetail.getBefImage() == null) {
					if (jointAccountDetail.getId() < 0) {
						BeanUtils.copyProperties(this.jointAccountDetail, befImage);
					} else if (CollectionUtils.isNotEmpty(tempJointAccountDetailList)) {
						for (JointAccountDetail jointAccount : tempJointAccountDetailList) {
							if (jointAccount.getId() == this.jointAccountDetail.getId()) {
								BeanUtils.copyProperties(jointAccount, befImage);
							}
						}
					}
					this.jointAccountDetail.setBefImage(befImage);
				}
				setNewContributor(true);
				setJointAccountDetail(this.jointAccountDetail);
			} else {
				setJointAccountDetail(null);
			}

			if (arguments.containsKey("index")) {
				this.index = (Integer) arguments.get("index");
			}

			if (arguments.containsKey("ccy")) {
				this.finCcy = (String) arguments.get("ccy");
			}

			if (arguments.containsKey("ccDecimal")) {
				this.ccyEditField = (Integer) arguments.get("ccDecimal");
			}

			if (arguments.containsKey("filter")) {
				this.cif = (String[]) arguments.get("filter");
			}
			if (arguments.containsKey("coAppFilter")) {
				this.coapplicantCif = (String[]) arguments.get("coAppFilter");
			}
			if (arguments.containsKey("finJointAccountCtrl")) {
				setFinanceMainDialogCtrl((com.pennant.webui.finance.financemain.JointAccountDetailDialogCtrl) arguments
						.get("finJointAccountCtrl"));
				setNewContributor(true);
				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.jointAccountDetail.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					setRole((String) arguments.get("roleCode"));
					getUserWorkspace().allocateRoleAuthorities(getRole(), "JointAccountDetailDialog");
				}
			}

			if (arguments.containsKey("financeMain")) {
				setFinanceMain((FinanceMain) arguments.get("financeMain"));
			}

			if (arguments.containsKey("financeDetail")) {
				this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
				if (financeDetail != null && financeDetail.getSampling() != null) {
					financeDetail.getSampling().getExcludeIncome().clear();
					financeDetail.getSampling().getIncludeIncome().clear();
				}
			}

			if (arguments.containsKey("primaryCustID")) {
				primaryCustId = (String) arguments.get("primaryCustID");
			}

			if (arguments.containsKey("applicationNo")) {
				applicationNo = (String) arguments.get("applicationNo");
			}

			if (arguments.containsKey("leadId")) {
				leadId = (String) arguments.get("leadId");
			}

			doLoadWorkFlow(this.jointAccountDetail.isWorkflow(), this.jointAccountDetail.getWorkflowId(),
					this.jointAccountDetail.getNextTaskId());
			if (isWorkFlowEnabled() && !enqModule && !isNewContributor()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "JointAccountDetailDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the jointAccountDetailListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete jointAccountDetail here.
			if (arguments.containsKey("jointAccountDetailListCtrl")) {
				setJointAccountDetailListCtrl((JointAccountDetailListCtrl) arguments.get("jointAccountDetailListCtrl"));
			} else {
				setJointAccountDetailListCtrl(null);
			}

			this.listBox_JointAccountPrimary.setHeight(((this.borderLayoutHeight - 120 - 220) / 1) + "px");
			this.listBox_JointAccountSecondary.setHeight(((this.borderLayoutHeight - 120 - 220) / 1) + "px");
			this.listBox_JointAccountGuarantor.setHeight(((this.borderLayoutHeight - 120 - 220) / 1) + "px");

			primaryList = getJointAccountDetailService().getPrimaryExposureList(getJointAccountDetail());
			secoundaryList = getJointAccountDetailService().getSecondaryExposureList(getJointAccountDetail());
			guarantorList = getJointAccountDetailService().getGuarantorExposureList(getJointAccountDetail());
			sumPrimaryDetails = getJointAccountDetailService().getExposureSummaryDetail(primaryList);
			sumSecondaryDetails = getJointAccountDetailService().getExposureSummaryDetail(secoundaryList);
			sumGurantorDetails = getJointAccountDetailService().getExposureSummaryDetail(guarantorList);

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getJointAccountDetail());

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
		doWriteBeanToComponents(this.jointAccountDetail.getBefImage());
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
		MessageUtil.showHelpWindow(event, window_MasterJointAccountDetailDialog);
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
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_MasterJointAccountDetailDialog, auditHeader);
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
		logger.debug("Entering" + event.toString());
		try {
			ScreenCTL.displayNotes(getNotes("JointAccountDetail",
					String.valueOf(getJointAccountDetail().getJointAccountId()), getJointAccountDetail().getVersion()),
					this);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$custCIF(Event event) {
		logger.debug(Literal.ENTERING);

		this.custCIF.clearErrorMessage();

		List<Filter> list = new ArrayList<>();
		if (cif != null) {
			list.add(new Filter("CustCIF", cif, Filter.OP_NOT_IN));
		}

		customer = (Customer) PennantAppUtil.getCustomerObject(this.custCIF.getValue(), list);

		if (customer == null) {
			this.custID.setValue(Long.valueOf(0));
			this.custCIFName.setValue("");
			this.primaryList = null;
			this.secoundaryList = null;
			this.guarantorList = null;

			this.listBox_JointAccountPrimary.getItems().clear();
			this.listBox_JointAccountSecondary.getItems().clear();
			this.listBox_JointAccountGuarantor.getItems().clear();

			setVisibleGrid();
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID",
					new String[] { Labels.getLabel("label_JointAccountDetailDialog_CustCIF.value") }));
		} else {
			this.custCIF.setValue(customer.getCustCIF());
			this.custID.setValue(customer.getCustID());
			this.custCIFName.setValue(customer.getCustShrtName());
			if (!(StringUtils.isEmpty(customer.getCustCoreBank())) && (customer.getCustCoreBank() != null)) {
				this.row1.setVisible(false);
			} else {
				this.row1.setVisible(false);
				this.includeRepay.setChecked(false);
				this.repayAccountId.setValue("");
			}
		}

		setCustomerDetails(customer);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		this.row1.setVisible(false);
		Clients.clearWrongValue(this.btnSearchCustCIF);
		doSearchCustomerCIF();
		logger.debug("Leaving");
	}

	/**
	 * To fill customer data
	 */
	private void setCustomersData() {
		if (this.custCIF.getValue() == null || this.custCIF.getValue().isEmpty()) {
			this.custCIFName.setValue("");
			this.gb_JointAccountPrimaryJoint.setVisible(false);
			this.gb_JointAccountSecondaryJoint.setVisible(false);
			this.gb_JointAccountGuarantorJoint.setVisible(false);
		} else {
			customer = getCustomer(this.custCIF.getValue());
			if (customer != null) {
				this.custCIF.setValue(customer.getCustCIF());
				this.custCIFName.setValue(customer.getCustShrtName());
				this.custID.setValue(customer.getCustID());
			}
			if (customer.getCustCoreBank() != null && StringUtils.isNotEmpty(customer.getCustCoreBank())) {
				this.row1.setVisible(false);
			} else {
				this.row1.setVisible(false);
				this.includeRepay.setChecked(false);
				this.repayAccountId.setValue("");
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
		logger.debug("Leaving");

		return customer;
	}

	/**
	 * Method for Showing Customer Search Window
	 */
	private void doSearchCustomerCIF() {
		logger.debug(Literal.ENTERING);

		Set<String> cifSet = new HashSet<>();

		for (int i = 0; i < this.cif.length; i++) {
			cifSet.add(this.cif[i]);
		}

		for (int i = 0; i < this.coapplicantCif.length; i++) {
			cifSet.add(this.coapplicantCif[i]);
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
		this.custCIF.clearErrorMessage();
		this.custCIFSearchObject = newSearchObject;

		Customer customer = (Customer) nCustomer;
		if (customer != null) {
			this.custCIF.setValue(customer.getCustCIF());
			setCustomersData();
		} else {
			this.custCIF.setValue("");
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
		if (customer == null) {
			this.primaryList = null;
			this.secoundaryList = null;
			this.guarantorList = null;

			return;
		}

		BigDecimal currentExpoSure = BigDecimal.ZERO;
		JointAccountDetail jad = getJointAccountDetail();
		jad.setCustCIF(customer.getCustCIF());

		this.primaryList = jointAccountDetailService.getPrimaryExposureList(jad);
		currentExpoSure = jointAccountDetailService.doFillExposureDetails(this.primaryList, jad);
		jad.setPrimaryExposure(String.valueOf(currentExpoSure));

		this.secoundaryList = jointAccountDetailService.getSecondaryExposureList(jad);
		currentExpoSure = jointAccountDetailService.doFillExposureDetails(this.secoundaryList, jad);
		jad.setSecondaryExposure(String.valueOf(currentExpoSure));

		this.guarantorList = jointAccountDetailService.getGuarantorExposureList(jad);
		currentExpoSure = jointAccountDetailService.doFillExposureDetails(this.guarantorList, jad);
		jad.setGuarantorExposure(String.valueOf(currentExpoSure));

		this.sumPrimaryDetails = jointAccountDetailService.getExposureSummaryDetail(primaryList);
		this.sumSecondaryDetails = jointAccountDetailService.getExposureSummaryDetail(secoundaryList);
		this.sumGurantorDetails = jointAccountDetailService.getExposureSummaryDetail(guarantorList);

		jad.setCustomerIncomeList(jointAccountDetailService.getJointAccountIncomeList(customer.getCustID()));
		jad.setCustomerExtLiabilityList(jointAccountDetailService.getJointExtLiabilityByCustomer(customer.getCustID()));
		jad.setCustFinanceExposureList(jointAccountDetailService.getJointCustFinanceExposureByCustomer(customer));

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
	}

	/**
	 * @param event Event for Create a new customer
	 */
	public void onClick$btn_NewCust(Event event) {
		logger.debug("Entering" + event.toString());
		Map<String, Object> map = getDefaultArguments();
		map.put("jointAccountDetailDialogCtrl", this);
		map.put("fromLoan", true);
		map.put("coAppFilter", cif);

		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CoreCustomerSelect.zul", null, map);
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btn_EditCust(Event event) {
		logger.debug("Entering" + event.toString());

		if (this.custID.longValue() <= 0) {
			MessageUtil.showError("Please select any customer.");
			return;
		}
		final CustomerDetails customerDetails = customerDetailsService.getCustById(this.custID.longValue());

		doShowDialogPage(customerDetails);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Build the Customer Dialog Window with Existing Core banking Data
	 */
	public void buildDialogWindow(CustomerDetails customerDetails, boolean newRecord) {
		logger.debug("Entering");
		if (customerDetails != null) {
			if (isWorkFlowEnabled() && customerDetails.getCustomer().getWorkflowId() == 0) {
				customerDetails.getCustomer().setWorkflowId(getWorkFlowId());
			}
			if (newRecord) {
				// create a new Customer object, We GET it from the backEnd.
				// CustomerDetails aCustomerDetails =
				// getCustomerDetailsService().getNewCustomer(false);
				Customer customerlov = customerDetailsService.fetchCustomerDetails(customerDetails.getCustomer());
				customerDetails.setCustomer(customerlov);
				customerDetailsService.setCustomerDetails(customerDetails);
			}
			doShowDialogPage(customerDetails);
		}
		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param customerDetails The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CustomerDetails customerDetails) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		String pageName = PennantAppUtil.getCustomerPageName();
		arg.put("customerDetails", customerDetails);
		arg.put("isNewCustCret", true);
		arg.put("jointAccountDetailDialogCtrl", this);
		arg.put("newRecord", customerDetails.getCustomer().isNewRecord());
		arg.put("fromLoan", true);
		arg.put("coAppFilter", cif);
		arg.put("applicationNo", applicationNo);
		arg.put("leadId", leadId);
		if (financeMain != null && StringUtils.isNotEmpty(financeMain.getFinReference())) {
			arg.put("finReference", financeMain.getFinReference());
		} else if (jointAccountDetail != null && StringUtils.isNotEmpty(jointAccountDetail.getFinReference())) {
			arg.put("finReference", jointAccountDetail.getFinReference());
		} else {
			arg.put("finReference", "");
		}
		arg.put("finMain", financeMain);
		try {
			Executions.createComponents(pageName, null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	public void onClick$viewCustInfo(Event event) {
		if ((!this.custCIF.isDisabled()) && (this.custID.getValue() == null || this.custID.getValue() == 0)) {
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID",
					new String[] { Labels.getLabel("label_JointAccountDetailDialog_CustCIF.value") }));
		}
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			if (ImplementationConstants.CO_APP_ENQ_SAME_AS_CUST_ENQ) {
				CustomerDetails customerDetails = customerDetailsService.getCustomerDetailsById(this.custID.longValue(),
						true, "_AView");
				String pageName = PennantAppUtil.getCustomerPageName();

				map.put("customerDetails", customerDetails);
				map.put("newRecord", false);
				map.put("isEnqProcess", true);
				map.put("CustomerEnq", true);
				Executions.createComponents(pageName, null, map);
			} else {
				map.put("custCIF", this.custCIF.getValue());
				customer = (Customer) PennantAppUtil.getCustomerObject(this.custCIF.getValue(), null);
				map.put("custid", customer.getCustID());
				map.put("jointcustid", this.custCIF.getValue());

				if (getFinanceMain() != null && StringUtils.isNotEmpty(getFinanceMain().getFinReference())) {
					map.put("finFormatter", CurrencyUtil.getFormat(getFinanceMain().getFinCcy()));
					map.put("finReference", getFinanceMain().getFinReference());
				}
				map.put("finance", true);
				if (StringUtils.equals(customer.getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/FinCustomerDetailsEnq.zul",
							this.window_MasterJointAccountDetailDialog, map);
				} else {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/Enquiry/CustomerSummary.zul",
							this.window_MasterJointAccountDetailDialog, map);
				}
			}
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
		}
	}

	public void onCheck$includeRepay(Event event) {
		logger.debug("Entering" + event.toString());
		// doCheckIncludeRepay();
		logger.debug("Leaving" + event.toString());
	}

	public void doCheckIncludeRepay() {
		if (includeRepay.isChecked()) {
			this.repayAccountId.setButtonVisible(true);
			this.repayAccountId.setMandatoryStyle(true);
			this.repayAccountId.setValue("");
		} else {
			this.repayAccountId.setValue("");
			this.repayAccountId.setButtonVisible(false);
			this.repayAccountId.setMandatoryStyle(false);
			this.repayAccountId.setErrorMessage("");
			this.repayAccountId.setConstraint("");
		}
	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aJointAccountDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(JointAccountDetail aJointAccountDetail) throws InterruptedException {
		logger.debug("Entering");
		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
			this.hbox_Sequence.setVisible(false);
			this.label_Sequence.setVisible(false);

		} else {
			if (isNewContributor()) {
				doEdit();
			} else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
			if (aJointAccountDetail.getSequence() == 0) {
				this.hbox_Sequence.setVisible(false);
				this.label_Sequence.setVisible(false);
			}
		}
		try {
			doFillPrimaryExposureDetails(this.primaryList);
			doFillSecoundaryExposureDetails(this.secoundaryList);
			doFillGuarantorExposureDetails(this.guarantorList);

			setVisibleGrid();

			// fill the components with the data
			doWriteBeanToComponents(aJointAccountDetail);
			// set ReadOnly mode accordingly if the object is new or not.
			// displayComponents(ScreenCTL.getMode(enqModule,isWorkFlowEnabled(),aJointAccountDetail.isNewRecord()));
			// doCheckIncludeRepay();
			// setDialog(DialogType.EMBEDDED);
			this.window_MasterJointAccountDetailDialog.setWidth("90%");
			this.window_MasterJointAccountDetailDialog.setHeight("90%");
			if (isNewContributor()) {
				this.groupboxWf.setVisible(false);
				this.window_MasterJointAccountDetailDialog.doModal();
			} else {
				setDialog(DialogType.EMBEDDED);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private void setVisibleGrid() {
		if (this.primaryList == null || this.primaryList.size() == 0) {
			this.gb_JointAccountPrimaryJoint.setVisible(false);
		} else {
			this.gb_JointAccountPrimaryJoint.setVisible(true);
		}

		if (this.secoundaryList == null || this.secoundaryList.size() == 0) {
			this.gb_JointAccountSecondaryJoint.setVisible(false);
		} else {
			this.gb_JointAccountSecondaryJoint.setVisible(true);
		}

		if (this.guarantorList == null || this.guarantorList.size() == 0) {
			this.gb_JointAccountGuarantorJoint.setVisible(false);
		} else {
			this.gb_JointAccountGuarantorJoint.setVisible(true);
		}

		if ((this.primaryList == null || this.primaryList.size() == 0)
				&& (this.secoundaryList == null || this.secoundaryList.size() == 0)
				&& (this.guarantorList == null || this.guarantorList.size() == 0)) {

			if (getJointAccountDetail().getCustCIF() != null) {
				gb_EmptyExposure.setVisible(true);
			} else {
				gb_EmptyExposure.setVisible(false);
			}
		}
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (isNewRecord()) {
			// this.btnSearchCustCIF.setDisabled(false);
			if (isNewContributor()) {
				this.btnCancel.setVisible(false);
			}
		} else {
			this.custCIF.setDisabled(true);
			this.btnCancel.setVisible(true);
		}
		readOnlyComponent(isReadOnly("JointAccountDetailDialog_catOfCoApplicant"), this.catOfCoApplicant);
		readOnlyComponent(isReadOnly("JointAccountDetailDialog_authoritySignatory"), this.authoritySignatory);
		readOnlyComponent(isReadOnly("JointAccountDetailDialog_includeIncome"), this.includeIncome);
		getRole();
		if (SysParamUtil.isAllowed(SMTParameterConstants.COAPP_CUST_CREATE)) {
			readOnlyComponent(isReadOnly("button_JointAccountDetailDialog_btnCreateCustomer"), this.btn_NewCust);
			readOnlyComponent(isReadOnly("button_JointAccountDetailDialog_btnEditCustomer"), this.btn_EditCust);
		} else {
			this.btn_NewCust.setVisible(false);
			this.btn_EditCust.setVisible(false);
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.jointAccountDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (newContributor) {
				if ("ENQ".equals(this.moduleType)) {
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
					this.viewCustInfo.setVisible(false);
					this.btn_NewCust.setVisible(false);
					this.btn_EditCust.setVisible(false);
					authoritySignatory.setDisabled(true);
					includeIncome.setDisabled(true);
					catOfCoApplicant.setDisabled(true);
				} else if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newContributor);
					// this.btnSave.setVisible(false);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	@Override
	public boolean isReadOnly(String componentName) {
		return getUserWorkspace().isReadOnly(componentName);
	}

	public void doReadOnly() {
		logger.debug("Entering");
		this.custCIF.setReadonly(true);
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
			getUserWorkspace().allocateAuthorities("JointAccountDetailDialog", getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_JointAccountDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_JointAccountDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_JointAccountDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_JointAccountDetailDialog_btnSave"));
			this.btnSearchCustCIF
					.setVisible(getUserWorkspace().isAllowed("button_JointAccountDetailDialog_btnSearchCustCIF"));
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.custCIFName.setMaxlength(200);
		this.custCIFName.setReadonly(true);
		if (!enqModule) {
			/*
			 * this.repayAccountId.setAccountDetails(
			 * getFinanceMainDialogCtrl().getFinanceDetail().getFinScheduleData().getFinanceType().getFinType(),
			 * AccountConstants.FinanceAccount_REPY, finCcy);
			 */
			this.repayAccountId.setFormatter(CurrencyUtil.getFormat(
					getFinanceMainDialogCtrl().getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));
			this.repayAccountId.setBranchCode(
					getFinanceMainDialogCtrl().getFinanceDetail().getFinScheduleData().getFinanceMain().getFinBranch());
		}
		if (!isNewRecord()) {
			this.row1.setVisible(false);
			this.row2.setVisible(true);
			// this.btnSearchCustCIF.setVisible(false);
			this.custCIF.setReadonly(false);
			this.gb_JointAccountPrimaryJoint.setVisible(true);
			this.gb_JointAccountSecondaryJoint.setVisible(true);
			this.gb_JointAccountGuarantorJoint.setVisible(true);
		}
		setStatusDetails(gb_statusDetails, groupboxWf, south, enqModule);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aJointAccountDetail JointAccountDetail
	 */
	public void doWriteBeanToComponents(JointAccountDetail aJointAccountDetail) {
		logger.debug("Entering");
		this.custCIF.setValue(aJointAccountDetail.getCustCIF());
		this.custID.setValue(aJointAccountDetail.getCustID());
		this.includeRepay.setChecked(aJointAccountDetail.isIncludeRepay());
		this.repayAccountId
				.setValue(PennantApplicationUtil.formatAccountNumber(aJointAccountDetail.getRepayAccountId()));
		this.custCIFName.setValue(aJointAccountDetail.getLovDescCIFName());
		this.custCIFStatus.setValue(aJointAccountDetail.getStatus());
		this.custCIFWorstStatus.setValue(aJointAccountDetail.getWorstStatus());

		String defaultCoApp = StringUtils.trimToNull(aJointAccountDetail.getCatOfcoApplicant());
		String fldCode = StringUtils.trimToNull(lovFieldDetail.getFieldCodeValue());
		if (defaultCoApp == null && fldCode != null) {
			defaultCoApp = lovFieldDetail.getFieldCodeValue();
		}

		fillComboBox(this.catOfCoApplicant, defaultCoApp, lovFieldDetail.getValueLabelList(), "");
		this.recordStatus.setValue(aJointAccountDetail.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aJointAccountDetail.getRecordType()));
		this.authoritySignatory.setChecked(aJointAccountDetail.isAuthoritySignatory());
		this.sequence.setValue(aJointAccountDetail.getSequence());
		this.includeIncome.setChecked(aJointAccountDetail.isIncludeIncome());
		logger.debug("Leaving");
	}

	// ================Primary Exposure Details
	public void doFillPrimaryExposureDetails(List<FinanceExposure> primaryExposureList) {
		logger.debug("Entering");
		this.listBox_JointAccountPrimary.getItems().clear();
		if (primaryExposureList != null) {
			recordCount = primaryExposureList.size();
			for (FinanceExposure primaryExposure : primaryExposureList) {
				Listitem listitem = new Listitem();
				Listcell listcell;
				listcell = new Listcell(primaryExposure.getFinType());
				listitem.appendChild(listcell);
				listcell = new Listcell(primaryExposure.getFinReference());
				listitem.appendChild(listcell);
				listcell = new Listcell(DateUtil.formatToLongDate(primaryExposure.getFinStartDate()));
				listitem.appendChild(listcell);
				listcell = new Listcell(DateUtil.formatToLongDate(primaryExposure.getMaturityDate()));
				listitem.appendChild(listcell);
				listcell = new Listcell(primaryExposure.getFinCCY());
				listitem.appendChild(listcell);
				listcell = new Listcell(PennantApplicationUtil.amountFormate(primaryExposure.getFinanceAmt(),
						primaryExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				listcell = new Listcell(PennantApplicationUtil
						.amountFormate(primaryExposure.getCurrentExpoSureinBaseCCY(), baseCcyDecFormat));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				listcell = new Listcell();

				if (primaryExposure.isOverdue()) {
					listcell.setLabel(PennantConstants.YES);
				} else {
					listcell.setLabel(PennantConstants.NO);
				}

				listitem.appendChild(listcell);
				listcell = new Listcell(primaryExposure.getPastdueDays());
				listitem.appendChild(listcell);
				listcell = new Listcell(
						PennantApplicationUtil.amountFormate(primaryExposure.getOverdueAmtBaseCCY(), baseCcyDecFormat));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);
				listitem.setAttribute("data", primaryExposure);
				this.listBox_JointAccountPrimary.appendChild(listitem);
			}
			String footerStyle1 = "text-align:right; font-weight:bold;";

			Listitem item = new Listitem();
			Listcell lc = null;

			lc = new Listcell("TOTAL");
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle("text-align:left; font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(recordCount + "");
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

			lc = new Listcell(PennantApplicationUtil.amountFormate(this.sumPrimaryDetails.getCurrentExpoSureinBaseCCY(),
					baseCcyDecFormat));
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

			lc = new Listcell(PennantApplicationUtil.amountFormate(this.sumPrimaryDetails.getOverdueAmtBaseCCY(),
					baseCcyDecFormat));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			item.setParent(this.listBox_JointAccountPrimary);
		}
		logger.debug("Leaving");
	}

	public void doFillSecoundaryExposureDetails(List<FinanceExposure> secondaryExposureList) {
		logger.debug("Entering");
		this.listBox_JointAccountSecondary.getItems().clear();
		if (secondaryExposureList != null) {
			recordCount = secondaryExposureList.size();
			for (FinanceExposure secondaryExposure : secondaryExposureList) {
				Listitem listitem = new Listitem();
				Listcell listcell;

				listcell = new Listcell(secondaryExposure.getFinType());
				listitem.appendChild(listcell);

				listcell = new Listcell(secondaryExposure.getFinReference());
				listitem.appendChild(listcell);

				listcell = new Listcell(DateUtil.formatToLongDate(secondaryExposure.getFinStartDate()));
				listitem.appendChild(listcell);

				listcell = new Listcell(DateUtil.formatToLongDate(secondaryExposure.getMaturityDate()));
				listitem.appendChild(listcell);

				listcell = new Listcell(secondaryExposure.getFinCCY());
				listitem.appendChild(listcell);

				listcell = new Listcell(PennantApplicationUtil.amountFormate(secondaryExposure.getFinanceAmt(),
						secondaryExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);

				listcell = new Listcell(PennantApplicationUtil
						.amountFormate(secondaryExposure.getCurrentExpoSureinBaseCCY(), baseCcyDecFormat));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);

				listcell = new Listcell();
				if (secondaryExposure.isOverdue()) {
					listcell.setLabel(PennantConstants.YES);
				} else {
					listcell.setLabel(PennantConstants.NO);
				}
				listitem.appendChild(listcell);

				listcell = new Listcell(secondaryExposure.getPastdueDays());
				listitem.appendChild(listcell);

				listcell = new Listcell(PennantApplicationUtil.amountFormate(secondaryExposure.getOverdueAmtBaseCCY(),
						baseCcyDecFormat));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);

				listcell = new Listcell(secondaryExposure.getCustCif());
				listitem.appendChild(listcell);

				listitem.setAttribute("data", secondaryExposure);
				this.listBox_JointAccountSecondary.appendChild(listitem);
			}
			String footerStyle1 = "text-align:right; font-weight:bold;";

			Listitem item = new Listitem();
			Listcell lc = null;

			lc = new Listcell("TOTAL");
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle("text-align:left; font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(recordCount + "");
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

			lc = new Listcell(PennantApplicationUtil
					.amountFormate(this.sumSecondaryDetails.getCurrentExpoSureinBaseCCY(), baseCcyDecFormat));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(this.sumSecondaryDetails.getOverdueAmtBaseCCY(),
					baseCcyDecFormat));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			this.listBox_JointAccountSecondary.appendChild(item);
		}
		logger.debug("Leaving");
	}

	public void doFillGuarantorExposureDetails(List<FinanceExposure> guarantorExposureList) {
		logger.debug("Entering");
		this.listBox_JointAccountGuarantor.getItems().clear();
		if (guarantorExposureList != null) {
			recordCount = guarantorExposureList.size();
			for (FinanceExposure guarantorExposure : guarantorExposureList) {
				Listitem listitem = new Listitem();
				Listcell listcell;

				listcell = new Listcell(guarantorExposure.getFinType());
				listitem.appendChild(listcell);

				listcell = new Listcell(guarantorExposure.getFinReference());
				listitem.appendChild(listcell);

				listcell = new Listcell(DateUtil.formatToLongDate(guarantorExposure.getFinStartDate()));
				listitem.appendChild(listcell);

				listcell = new Listcell(DateUtil.formatToLongDate(guarantorExposure.getMaturityDate()));
				listitem.appendChild(listcell);

				listcell = new Listcell(guarantorExposure.getFinCCY());
				listitem.appendChild(listcell);

				listcell = new Listcell(PennantApplicationUtil.amountFormate(guarantorExposure.getFinanceAmt(),
						guarantorExposure.getCcyEditField()));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);

				listcell = new Listcell(PennantApplicationUtil
						.amountFormate(guarantorExposure.getCurrentExpoSureinBaseCCY(), baseCcyDecFormat));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);

				listcell = new Listcell();
				if (guarantorExposure.isOverdue()) {
					listcell.setLabel(PennantConstants.YES);
				} else {
					listcell.setLabel(PennantConstants.NO);
				}
				listitem.appendChild(listcell);

				listcell = new Listcell(guarantorExposure.getPastdueDays());
				listitem.appendChild(listcell);

				listcell = new Listcell(PennantApplicationUtil.amountFormate(guarantorExposure.getOverdueAmtBaseCCY(),
						baseCcyDecFormat));
				listcell.setStyle("text-align:right");
				listitem.appendChild(listcell);

				listcell = new Listcell(guarantorExposure.getCustCif());
				listitem.appendChild(listcell);

				listitem.setAttribute("data", guarantorExposure);
				this.listBox_JointAccountGuarantor.appendChild(listitem);
			}
			String footerStyle1 = "text-align:right; font-weight:bold;";

			Listitem item = new Listitem();
			Listcell lc = null;

			lc = new Listcell("TOTAL");
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle("text-align:left; font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(recordCount + "");
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

			lc = new Listcell(PennantApplicationUtil
					.amountFormate(this.sumGurantorDetails.getCurrentExpoSureinBaseCCY(), baseCcyDecFormat));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(this.sumGurantorDetails.getOverdueAmtBaseCCY(),
					baseCcyDecFormat));
			lc.setSclass("highlighted_List_Cell");
			lc.setStyle(footerStyle1);
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			lc = new Listcell();
			lc.setSclass("highlighted_List_Cell");
			lc.setParent(item);

			this.listBox_JointAccountGuarantor.appendChild(item);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aJointAccountDetail
	 */
	public void doWriteComponentsToBean(JointAccountDetail aJointAccountDetail) {
		logger.debug("Entering");
		doSetLOVValidation();
		if (!this.includeRepay.isChecked()) {
			this.repayAccountId.setErrorMessage("");
		}
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		// Cust C I F
		try {
			aJointAccountDetail.setCustCIF(this.custCIF.getValue());
			if ((!this.custCIF.isDisabled()) && this.custID.getValue() == 0) {
				wve.add(new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("label_JointAccountDetailDialog_CustCIF.value") })));
			}
			aJointAccountDetail.setCustID(this.custID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// cust CIF Name
		try {
			aJointAccountDetail.setLovDescCIFName(this.custCIFName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Include Repay
		try {
			aJointAccountDetail.setIncludeRepay(this.includeRepay.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Repay Account Id
		try {
			aJointAccountDetail
					.setRepayAccountId(PennantApplicationUtil.unFormatAccountNumber(this.repayAccountId.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// catOfCoApplicant
		try {
			aJointAccountDetail.setCatOfcoApplicant(this.catOfCoApplicant.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aJointAccountDetail.setAuthoritySignatory(this.authoritySignatory.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aJointAccountDetail.setSequence(this.sequence.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aJointAccountDetail.setIncludeIncome(this.includeIncome.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();
		doRemoveLOVValidation();
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		// setting the customer details while save
		aJointAccountDetail.setCustomerDetails(customerDetailsService.getCustById(aJointAccountDetail.getCustID()));
		aJointAccountDetail.setRecordStatus(this.recordStatus.getValue());
		setJointAccountDetail(aJointAccountDetail);

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		if (this.repayAccountId.isVisible() && this.repayAccountId.getValue().isEmpty()) {
			this.repayAccountId.setConstraint(new PTStringValidator(
					Labels.getLabel("label_JointAccountDetailDialog_RepayAccountId.value"), null, true));
		}

		if (!this.sequence.isReadonly() && this.authoritySignatory.isChecked()) {
			this.sequence.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_JointAccountDetailDialog_Sequence.value"), true, false, 1, 9));
		}

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.custCIF.setConstraint("");
		this.repayAccountId.setConstraint("");
		this.catOfCoApplicant.setConstraint("");
		this.sequence.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		// Cust C I F
		if (!btnSearchCustCIF.isDisabled()) {
			this.custCIF.setConstraint(
					new PTStringValidator(Labels.getLabel("label_JointAccountDetailDialog_CustCIF.value"), null, true));
		}
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveLOVValidation() {
		this.custCIFName.setConstraint("");
		this.custCIF.setConstraint("");
		this.catOfCoApplicant.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.setErrorMessage("");
		this.custCIFName.setErrorMessage("");
		this.repayAccountId.setErrorMessage("");
		this.catOfCoApplicant.setErrorMessage("");
		logger.debug("Leaving");
	}

	protected boolean doCustomDelete(final JointAccountDetail aJointAccountDetail, String tranType) {
		if (isNewContributor()) {
			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = newJointAccountProcess(aJointAccountDetail, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_MasterJointAccountDetailDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getFinanceMainDialogCtrl().doFillJointDetails(this.jointAccountDetailList);
				return true;
			}
		}

		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final JointAccountDetail aJointAccountDetail = new JointAccountDetail();
		BeanUtils.copyProperties(getJointAccountDetail(), aJointAccountDetail);

		doDelete(aJointAccountDetail.getCustCIF(), aJointAccountDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.custCIF.setValue("");
		this.custCIFName.setValue("");
		this.includeRepay.setChecked(false);
		this.repayAccountId.setValue("");
		this.catOfCoApplicant.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final JointAccountDetail aJointAccountDetail = new JointAccountDetail();
		BeanUtils.copyProperties(getJointAccountDetail(), aJointAccountDetail);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the DocumentDetails object with the components data
		doWriteComponentsToBean(aJointAccountDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		isNew = aJointAccountDetail.isNewRecord();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aJointAccountDetail.getRecordType())) {
				aJointAccountDetail.setVersion(aJointAccountDetail.getVersion() + 1);
				if (isNew) {
					aJointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aJointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aJointAccountDetail.setNewRecord(true);
				}
			}
		} else {
			if (isNewContributor()) {
				if (isNewRecord()) {
					aJointAccountDetail.setVersion(1);
					aJointAccountDetail.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
				if (StringUtils.isBlank(aJointAccountDetail.getRecordType())) {
					aJointAccountDetail.setVersion(aJointAccountDetail.getVersion() + 1);
					aJointAccountDetail.setRecordType(PennantConstants.RCD_UPD);
					aJointAccountDetail.setNewRecord(true);
				}
				if (aJointAccountDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aJointAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}
			} else {
				aJointAccountDetail.setVersion(aJointAccountDetail.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}
		// save it to database
		try {
			if (isNewContributor()) {
				AuditHeader auditHeader = newJointAccountProcess(aJointAccountDetail, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_MasterJointAccountDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					if (financeDetail != null && financeDetail.getSampling() != null) {
						samplingService.reCalculate(financeDetail);
					}
					getFinanceMainDialogCtrl().doFillJointDetails(this.jointAccountDetailList);
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
	private AuditHeader newJointAccountProcess(JointAccountDetail aJointAccountDetail, String tranType) {
		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aJointAccountDetail, tranType);
		jointAccountDetailList = new ArrayList<JointAccountDetail>();
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = aJointAccountDetail.getCustCIF();
		valueParm[1] = aJointAccountDetail.getLovDescCIFName();
		errParm[0] = PennantJavaUtil.getLabel("label_CustCIF") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_JointCustCIf") + ":" + valueParm[1];
		// Checks whether jointAccount custCIF is same as actual custCIF
		if (getFinanceMain() != null) {
			if (StringUtils.trimToEmpty(primaryCustId).equals(aJointAccountDetail.getCustCIF())) {
				auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
						getUserWorkspace().getUserLanguage()));
			}
		}
		if (getFinanceMainDialogCtrl().getJointAccountDetailList() != null
				&& getFinanceMainDialogCtrl().getJointAccountDetailList().size() > 0) {
			for (int i = 0; i < getFinanceMainDialogCtrl().getJointAccountDetailList().size(); i++) {
				JointAccountDetail jointAccountDetail = getFinanceMainDialogCtrl().getJointAccountDetailList().get(i);
				if (jointAccountDetail.getCustCIF().equals(aJointAccountDetail.getCustCIF())) { // Both
					// Current and Existing list rating same
					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					} else if (index != i) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
					}
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aJointAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aJointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							jointAccountDetailList.add(aJointAccountDetail);
						} else if (aJointAccountDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aJointAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aJointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							jointAccountDetailList.add(aJointAccountDetail);
						} else if (aJointAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
						}
					} else {
						jointAccountDetailList.add(jointAccountDetail);
					}
				} else if (jointAccountDetail.getSequence() == aJointAccountDetail.getSequence()
						&& aJointAccountDetail.getSequence() != 0) {
					String[] valueParam = new String[1];
					String[] errParam = new String[1];
					valueParam[0] = String.valueOf(aJointAccountDetail.getSequence());
					errParam[0] = PennantJavaUtil.getLabel("label_JointSequence") + ":" + valueParam[0];
					if (isNewRecord()) {

						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParam, valueParam),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					} else if (index != i) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParam, valueParam),
								getUserWorkspace().getUserLanguage()));
					}
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aJointAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aJointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							jointAccountDetailList.add(aJointAccountDetail);
						} else if (aJointAccountDetail.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aJointAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aJointAccountDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							jointAccountDetailList.add(aJointAccountDetail);
						} else if (aJointAccountDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
						}
					} else {
						jointAccountDetailList.add(jointAccountDetail);
					}

				} else {
					jointAccountDetailList.add(jointAccountDetail);
				}
			}
		}
		if (PennantConstants.TRAN_UPD.equals(tranType)) {
			this.jointAccountDetailList.remove(index);
			this.jointAccountDetailList.add(jointAccountDetail);
			recordAdded = true;
		}
		if (!recordAdded) {
			jointAccountDetailList.add(aJointAccountDetail);
		}
		return auditHeader;
	}

	// WorkFlow Components

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(JointAccountDetail aJointAccountDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aJointAccountDetail.getBefImage(), aJointAccountDetail);
		return new AuditHeader(String.valueOf(aJointAccountDetail.getJointAccountId()), null, null, null, auditDetail,
				aJointAccountDetail.getUserDetails(), getOverideMap());
	}

	public void onCheck$authoritySignatory(Event event) {
		logger.debug("Entering" + event.toString());
		doCheckAuthoritySignatory();
		logger.debug("Leaving" + event.toString());
	}

	public void doCheckAuthoritySignatory() {
		if (authoritySignatory.isChecked()) {
			this.hbox_Sequence.setVisible(true);
			this.label_Sequence.setVisible(true);
		} else {
			this.hbox_Sequence.setVisible(false);
			this.label_Sequence.setVisible(false);
			this.sequence.setValue(0);
		}
	}

	public void onCheck$includeIncome(Event event) {
		logger.debug(Literal.ENTERING);
		Sampling sampling = financeDetail.getSampling();
		if (sampling != null) {
			sampling.getIncludeIncome().clear();
			sampling.getExcludeIncome().clear();
			if (includeIncome.isChecked() && !this.jointAccountDetail.isNewRecord()) {
				sampling.getIncludeIncome().add(this.jointAccountDetail.getCustID());
			} else if (!includeIncome.isChecked() && !this.jointAccountDetail.isNewRecord()) {
				sampling.getExcludeIncome().add(this.jointAccountDetail.getCustID());
			}
			financeDetail.setSampling(sampling);
		}
		logger.debug(Literal.LEAVING);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	public JointAccountDetail getJointAccountDetail() {
		return this.jointAccountDetail;
	}

	public void setJointAccountDetail(JointAccountDetail jointAccountDetail) {
		this.jointAccountDetail = jointAccountDetail;
	}

	public JointAccountDetailService getJointAccountDetailService() {
		return jointAccountDetailService;
	}

	public void setJointAccountDetailService(JointAccountDetailService jointAccountDetailService) {
		this.jointAccountDetailService = jointAccountDetailService;
	}

	public void setJointAccountDetailListCtrl(JointAccountDetailListCtrl jointAccountDetailListCtrl) {
		this.jointAccountDetailListCtrl = jointAccountDetailListCtrl;
	}

	public JointAccountDetailListCtrl getJointAccountDetailListCtrl() {
		return this.jointAccountDetailListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public List<JointAccountDetail> getJointAccountDetailList() {
		return jointAccountDetailList;
	}

	public void setJointAccountDetailList(List<JointAccountDetail> jointAccountDetailList) {
		this.jointAccountDetailList = jointAccountDetailList;
	}

	public boolean isNewContributor() {
		return newContributor;
	}

	public void setNewContributor(boolean newContributor) {
		this.newContributor = newContributor;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public com.pennant.webui.finance.financemain.JointAccountDetailDialogCtrl getFinanceMainDialogCtrl() {
		return finJointAccountCtrl;
	}

	public void setFinanceMainDialogCtrl(
			com.pennant.webui.finance.financemain.JointAccountDetailDialogCtrl finJointAccountCtrl) {
		this.finJointAccountCtrl = finJointAccountCtrl;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	private AccountsService accountsService;

	public AccountsService getAccountsService() {
		return accountsService;
	}

	public void setAccountsService(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	public String getNewCustCIF() {
		return newCustCIF;
	}

	public void setNewCustCIF(String newCustCIF) {
		this.custCIF.setValue(newCustCIF);
		setCustomersData();
	}

}
