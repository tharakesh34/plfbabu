package com.pennant.webui.sampling;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.customermasters.CustomerAddresService;
import com.pennant.backend.service.customermasters.impl.CustomerDataService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pennapps.pff.sampling.model.SamplingCollateral;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.service.sampling.SamplingService;

public class SamplingDialogCtrl extends GFCBaseCtrl<Sampling> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SamplingDialogCtrl.class);

	protected Window window_SamplingDialog;

	protected Tab samplingDetailsTab;
	protected Listbox listBoxCustomerDetails;
	protected Listbox listBoxCustomerIncomeDetails;
	protected Listbox listBoxObligations;
	protected Listbox listBoxCollaterals;

	protected Button btnNew_CustomerIncome;
	protected Button btnNew_Obligation;

	protected Label loanNo;
	protected Label loanType;
	protected Label branch;
	protected Label loanAmtReq;
	protected Label tenure;
	protected Label samplingDate;
	protected Label roi;
	protected A userActivityLog;

	protected Intbox loanTenure;
	protected Decimalbox interestRate;
	protected Decimalbox foirEligiblity;
	protected Decimalbox finAmtReq;
	protected Decimalbox emiPerLakh;
	protected Decimalbox iirEligibility;
	protected Decimalbox lcrEligibility;
	protected Decimalbox ltvEligibility;
	protected Decimalbox loanEligibility;

	protected Tabbox tabBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected String selectMethodName = "onSelectTab";

	private int ccyFormatter = 0;

	private List<CustomerIncome> incomeList = new ArrayList<>();
	private List<CustomerExtLiability> customerExtLiabilityDetailList = new ArrayList<>();

	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl;
	private transient FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl;
	private Sampling sampling;
	private transient SamplingListCtrl samplingListCtrl;
	private transient CustomerDialogCtrl CustomerDialogCtrl;

	@Autowired
	private transient SamplingService samplingService;
	@Autowired
	private transient CustomerAddresService customerAddresService;
	@Autowired
	private FinanceDetailService financeDetailService;
	@Autowired
	private transient CollateralSetupService collateralSetupService;
	@Autowired
	private CustomerDataService customerDataService;

	private Map<String, ExtendedFieldRender> extFieldRenderList;
	private ExtendedFieldCtrl extendedFieldCtrl = null;
	private Set<String> primaryCustomer = new HashSet<>();
	private Set<String> coApplicantIncomeCustomers = new HashSet<>();
	private Set<String> coApplicantObligationCustomers = new HashSet<>();

	private boolean fromLoanOrg;

	public SamplingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SamplingDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_SamplingDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_SamplingDialog);

		try {
			// Get the required arguments.
			this.sampling = (Sampling) arguments.get("sampling");

			if (arguments.get("samplingListCtrl") != null) {
				this.samplingListCtrl = (SamplingListCtrl) arguments.get("samplingListCtrl");
			}

			if (arguments.get("enqiryModule") != null) {
				enqiryModule = (boolean) arguments.get("enqiryModule");
			}

			if (arguments.get("LOAN_ORG") != null) {
				fromLoanOrg = true;
				enqiryModule = true;
			}

			if (this.sampling == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			ccyFormatter = CurrencyUtil.getFormat(sampling.getFinccy());

			// Store the before image.
			Sampling aSampling = new Sampling();
			BeanUtils.copyProperties(this.sampling, aSampling);
			this.sampling.setBefImage(aSampling);

			// Render the page and display the data.
			doLoadWorkFlow(this.sampling.isWorkflow(), this.sampling.getWorkflowId(), this.sampling.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.sampling);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.loanTenure.setMaxlength(3);

		this.foirEligiblity.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.foirEligiblity.setScale(ccyFormatter);

		this.finAmtReq.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.finAmtReq.setScale(ccyFormatter);

		this.emiPerLakh.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.emiPerLakh.setScale(ccyFormatter);

		this.iirEligibility.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.iirEligibility.setScale(ccyFormatter);

		this.loanEligibility.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.loanEligibility.setScale(ccyFormatter);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SamplingDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SamplingDialog_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SamplingDialog_btnSave"));
		this.btnNew_CustomerIncome
				.setVisible(getUserWorkspace().isAllowed("button_SamplingDialog_btnNewIncomeDetails"));
		this.btnNew_Obligation
				.setVisible(getUserWorkspace().isAllowed("button_SamplingDialog_btnNewObligationDetails"));

		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param sampling The entity that need to be render.
	 */
	public void doShowDialog(Sampling sampling) {
		logger.debug(Literal.LEAVING);

		if (sampling.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(sampling.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
			this.south.setVisible(false);
			this.btnNew_CustomerIncome.setVisible(false);
			this.btnNew_Obligation.setVisible(false);
		}

		doWriteBeanToComponents(sampling);
		if (!fromLoanOrg) {
			setDialog(DialogType.EMBEDDED);
		} else {
			window_SamplingDialog.setHeight("75%");
			setDialog(DialogType.MODAL);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.sampling.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		readOnlyComponent(isReadOnly("SamplingDialog_LoanTenure"), this.loanTenure);
		readOnlyComponent(isReadOnly("SamplingDialog_InterestRate"), this.interestRate);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.sampling.isNewRecord()) {
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

	private void doWriteBeanToComponents(Sampling sampling) {
		logger.debug(Literal.ENTERING);

		doFillCustomers(sampling.getCustomers());
		doFillCollaterals(sampling.getCollaterals());
		doFillCustomerIncome(sampling.getCustomerIncomeList());
		doFillCustomerExtLiabilityDetails(sampling.getCustomerExtLiabilityList());
		doFillExtendedFileds(sampling.getExtFieldRenderList());

		this.loanNo.setValue(sampling.getKeyReference());
		this.loanType.setValue(sampling.getFinType().concat(" - ").concat(sampling.getFinTypeDesc()));
		this.branch.setValue(sampling.getBranchCode().concat(" - ").concat(sampling.getBranchDesc()));
		this.loanAmtReq.setValue(CurrencyUtil.format(sampling.getLoanAmountRequested(), ccyFormatter));
		this.tenure.setValue(String.valueOf(sampling.getNumberOfTerms()));
		this.samplingDate.setValue(DateUtil.format(sampling.getCreatedOn(), DateFormat.SHORT_DATE));

		BigDecimal rate = BigDecimal.ZERO;
		if (sampling.getRepayBaseRate() != null) {
			RateDetail details = RateUtil.rates(sampling.getRepayBaseRate(), sampling.getFinccy(),
					sampling.getRepaySpecialRate(), sampling.getRepayMargin(), sampling.getRepayMinRate(),
					sampling.getRepayMaxRate());
			rate = details.getNetRefRateLoan();
		} else {
			rate = sampling.getRepayProfitRate();
		}
		this.roi.setValue(PennantApplicationUtil.formatRate(rate.doubleValue(), 2));

		this.loanTenure.setValue(sampling.getTenure());
		this.interestRate.setValue(PennantApplicationUtil.formatRate(sampling.getInterestRate().doubleValue(), 2));

		setEligibilityAmounts(sampling);

		calculateEligibility(false);

		this.recordStatus.setValue(sampling.getRecordStatus());

		appendDocumentDetailTab();

		// appendCustomerDetailTab();

		appendCoApplicantDetailTab();

		// appendQueryModuleTab();

		logger.debug(Literal.LEAVING);

	}

	private void setEligibilityAmounts(Sampling sampling) {
		this.finAmtReq.setValue(CurrencyUtil.parse(sampling.getLoanAmountRequested(), ccyFormatter));
		this.foirEligiblity.setValue(CurrencyUtil.parse(sampling.getFoirEligibility(), ccyFormatter));
		this.emiPerLakh.setValue(CurrencyUtil.parse(sampling.getEmi(), ccyFormatter));
		this.iirEligibility.setValue(CurrencyUtil.parse(sampling.getIrrEligibility(), ccyFormatter));
		this.loanEligibility.setValue(CurrencyUtil.parse(sampling.getLoanEligibility(), ccyFormatter));
		this.lcrEligibility.setValue(CurrencyUtil.parse(sampling.getLcrEligibility(), ccyFormatter));
		this.ltvEligibility.setValue(CurrencyUtil.parse(sampling.getLtvEligibility(), ccyFormatter));
	}

	private void doFillCollaterals(List<SamplingCollateral> collSetupList) {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isNotEmpty(collSetupList)) {

			for (SamplingCollateral collateralSetup : collSetupList) {
				Listitem item = new Listitem();

				Listcell lc;

				lc = new Listcell(collateralSetup.getDepositorCif());
				lc.setParent(item);

				lc = new Listcell(collateralSetup.getCollateralRef());
				Space space = new Space();
				space.setSpacing("6px");
				Button collRef = new Button();
				collRef.setImage("/images/icons/more.png");
				collRef.addForward("onClick", self, "onClickCollateralReference", collateralSetup);
				lc.appendChild(space);
				lc.appendChild(collRef);
				lc.setParent(item);

				lc = new Listcell(String.valueOf(collateralSetup.getSeqNo()));
				lc.setParent(item);

				lc = new Listcell(collateralSetup.getCollateralType());
				lc.setParent(item);
				item.setAttribute("data", collateralSetup);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCollateralItemDoubleClicked");

				this.listBoxCollaterals.appendChild(item);
			}
		}

		logger.debug(Literal.LEAVING);

	}

	private void doFillCustomers(List<Customer> customers) {
		if (CollectionUtils.isNotEmpty(customers)) {
			for (Customer customer : customers) {
				Listitem item = new Listitem();

				Listcell lc;

				lc = new Listcell(customer.getCustCIF());
				Space space = new Space();
				space.setSpacing("6px");
				Button collRef = new Button();
				collRef.setImage("/images/icons/more.png");
				collRef.addForward("onClick", self, "onClickCustomerId", customer);
				lc.appendChild(space);
				lc.appendChild(collRef);
				lc.setParent(item);

				lc = new Listcell(customer.getCustShrtName());
				lc.setParent(item);

				if ("1".equals(customer.getCustTypeCode())) {
					lc = new Listcell("primary");
					lc.setParent(item);
				} else {
					lc = new Listcell("Co-Applicant");
					lc.setParent(item);
				}

				lc = new Listcell();
				A addrLink = new A();
				addrLink.setLabel("view");
				addrLink.addForward("onClick", self, "onClickViewAddress", customer);
				addrLink.setStyle("text-decoration:underline;");
				lc.appendChild(addrLink);
				lc.setParent(item);

				lc = new Listcell(customer.getPhoneNumber());
				lc.setParent(item);

				if (Integer.valueOf(customer.getCustTypeCode()) == 1) {
					primaryCustomer.add(customer.getCustCIF());
				} else if (customer.isIncludeIncome()) {
					coApplicantIncomeCustomers.add(customer.getCustCIF());
					coApplicantObligationCustomers.add(customer.getCustCIF());
				} else {
					coApplicantObligationCustomers.add(customer.getCustCIF());
				}
				this.listBoxCustomerDetails.appendChild(item);
			}

		}
	}

	public void onClick$userActivityLog(Event event) {
		logger.debug(Literal.ENTERING);
		doUserActivityLog();
		logger.debug(Literal.LEAVING);
	}

	private void doUserActivityLog() {
		logger.debug(Literal.ENTERING);

		Map<String, Object> map = new LinkedHashMap<>();

		map.put("label_SamplingDialog_LoanNo.value", this.sampling.getKeyReference());
		map.put("label_SamplingDialog_LoanType.value", this.sampling.getFinType());
		map.put("label_SamplingDialog_Branch.value", sampling.getBranchCode());
		map.put("label_SamplingDialog_LoanAmtReq.value", this.sampling.getLoanAmountRequested());
		map.put("label_SamplingDialog_Tenure.value", this.sampling.getNumberOfTerms());
		map.put("label_SamplingDialog_samplingDate.value", this.sampling.getCreatedOn());

		if ("F".equals(sampling.getFinGrcRateType())) {
			map.put("label_SamplingDialog_ROI.value",
					PennantApplicationUtil.formatRate(Double.parseDouble(this.sampling.getRepaySpecialRate()), 2));
		} else {
			map.put("label_SamplingDialog_ROI.value",
					PennantApplicationUtil.formatRate(this.sampling.getRepayMinRate().doubleValue(), 2));
		}

		map.put("moduleCode", "FinanceMain");

		doShowActivityLog(this.sampling.getKeyReference(), map);

		logger.debug("Leaving ");
	}

	/**
	 * View The Collateral Details
	 */
	public void onClickCollateralReference(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		SamplingCollateral collSetup = (SamplingCollateral) event.getData();

		Map<String, Object> map = new HashMap<String, Object>();
		CollateralSetup collateralSetup = collateralSetupService.getCollateralSetupByRef(collSetup.getCollateralRef(),
				"", true);

		if (collateralSetup != null) {
			map.put("collateralSetup", collateralSetup);
			map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralSetupDialog.zul", null,
					map);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * View The Customer Details
	 */
	public void onClickCustomerId(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Customer collSetup = (Customer) event.getData();
		CustomerDetails customerDetails = customerDataService.getCustomerDetailsbyID(collSetup.getCustID(), true,
				"_AView");
		final Map<String, Object> map = new HashMap<String, Object>();
		String pageName = PennantAppUtil.getCustomerPageName();
		map.put("customerDetails", customerDetails);
		map.put("newRecord", false);
		map.put("isEnqProcess", true);
		map.put("CustomerEnq", true);
		map.put("enqiryModule", true);
		map.put("enqModule", true);
		map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
		Executions.createComponents(pageName, null, map);

		logger.debug(Literal.LEAVING);
	}

	public void onClickViewAddress(ForwardEvent event) {
		Customer customer = (Customer) event.getData();
		CustomerAddres address;

		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			address = customerAddresService.getCustomerAddresById(customer.getCustID(), customer.getCustAddlVar1());
			address.setLovDescCustCIF(sampling.getCustCif());
			address.setLovDescCustShrtName(sampling.getCustShrtName());
			map.put("customerAddres", address);
			map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
			Window window = (Window) Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerAddres/CustomerAddresDialog.zul", null, map);
			window.setMode(Window.MODAL);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	public void onChange$loanTenure(Event event) {
		logger.debug(Literal.ENTERING);
		calculateEligibility(true);

		logger.debug(Literal.LEAVING);
	}

	public void onChange$interestRate(Event event) {
		logger.debug(Literal.ENTERING);
		calculateEligibility(true);

		logger.debug(Literal.LEAVING);
	}

	private void calculateEligibility(boolean onChange) {
		if (loanTenure.isDisabled() || interestRate.isDisabled()) {
			return;
		}

		final Sampling aSampling = new Sampling();
		BeanUtils.copyProperties(this.sampling, aSampling);

		if (onChange) {
			doSetValidation();
			doWriteComponentsToBean(aSampling);
		}

		aSampling.setTenure(this.loanTenure.getValue());
		aSampling.setInterestRate(this.interestRate.getValue());
		aSampling.setExtFieldRenderList(getExtFieldRenderList());

		this.samplingService.calculateEligilibity(aSampling);
		setEligibilityAmounts(aSampling);
	}

	public void onCustomerIncomeItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);
		final Listitem item = this.listBoxCustomerIncomeDetails.getSelectedItem();

		if (item != null) {
			final CustomerIncome customerIncome = (CustomerIncome) item.getAttribute("data");
			if (isDeleteRecord(customerIncome.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("customerIncome", customerIncome);
				map.put("samplingDialogCtrl", this);
				map.put("coApplicants", coApplicantIncomeCustomers);
				map.put("ccyFormatter", ccyFormatter);
				map.put("roleCode", getRole());
				map.put("enqiryModule", enqiryModule);
				try {
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/CustomerIncome/CustomerIncomeDialog.zul", null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onCustomerExtLiabilityItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);
		// get the selected external liability object
		final Listitem item = this.listBoxObligations.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerExtLiability externalLiability = (CustomerExtLiability) item.getAttribute("data");
			if (isDeleteRecord(externalLiability.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("externalLiability", externalLiability);
				map.put("finFormatter", ccyFormatter);
				map.put("samplingDialogCtrl", this);
				map.put("coApplicants", coApplicantObligationCustomers);
				map.put("isFinanceProcess", false);
				map.put("roleCode", getRole());
				map.put("enqiryModule", enqiryModule);
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/Customer/CustomerExtLiabilityDialog.zul", null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		this.loanTenure.setReadonly(true);
		this.interestRate.setReadonly(true);
		this.foirEligiblity.setReadonly(true);
		this.finAmtReq.setReadonly(true);
		this.emiPerLakh.setReadonly(true);
		this.iirEligibility.setReadonly(true);
		this.loanEligibility.setReadonly(true);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.loanTenure.setConstraint("");
		this.interestRate.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.loanTenure.isReadonly()) {
			this.loanTenure.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_SamplingDialog_LoanTenure.value"), true, false));
		}

		if (!this.interestRate.isReadonly()) {
			this.interestRate.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_SamplingDialog_InterestRate.value"), 9, true, false, 0, 9999));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param verification
	 */
	public void doWriteComponentsToBean(Sampling sampling) {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<>();

		try {
			sampling.setTenure(this.loanTenure.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			BigDecimal interestRate = this.interestRate.getValue();
			if (interestRate == null) {
				interestRate = BigDecimal.ZERO;
			}

			interestRate = CurrencyUtil.unFormat(interestRate, ccyFormatter);

			sampling.setInterestRate(this.interestRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			BigDecimal loanEligibility = this.loanEligibility.getValue();
			if (loanEligibility == null) {
				loanEligibility = BigDecimal.ZERO;
			}

			loanEligibility = CurrencyUtil.unFormat(loanEligibility, ccyFormatter);
			sampling.setLoanEligibility(loanEligibility);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			BigDecimal foirEligiblity = this.foirEligiblity.getValue();
			if (foirEligiblity == null) {
				foirEligiblity = BigDecimal.ZERO;
			}

			foirEligiblity = CurrencyUtil.unFormat(foirEligiblity, ccyFormatter);
			sampling.setFoirEligibility(foirEligiblity);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			BigDecimal iirEligibility = this.iirEligibility.getValue();
			if (iirEligibility == null) {
				iirEligibility = BigDecimal.ZERO;
			}

			iirEligibility = CurrencyUtil.unFormat(iirEligibility, ccyFormatter);

			sampling.setIrrEligibility(iirEligibility);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			BigDecimal lcrEligibility = this.lcrEligibility.getValue();
			if (lcrEligibility == null) {
				lcrEligibility = BigDecimal.ZERO;
			}

			lcrEligibility = CurrencyUtil.unFormat(lcrEligibility, ccyFormatter);

			sampling.setLcrEligibility(lcrEligibility);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			BigDecimal ltvEligibility = this.ltvEligibility.getValue();
			if (ltvEligibility == null) {
				ltvEligibility = BigDecimal.ZERO;
			}

			ltvEligibility = CurrencyUtil.unFormat(ltvEligibility, ccyFormatter);
			sampling.setLtvEligibility(ltvEligibility);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			BigDecimal emiPerLakh = this.emiPerLakh.getValue();
			if (emiPerLakh == null) {
				emiPerLakh = BigDecimal.ZERO;
			}

			emiPerLakh = CurrencyUtil.unFormat(emiPerLakh, ccyFormatter);

			sampling.setEmi(emiPerLakh);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		sampling.setCustomerIncomeList(this.incomeList);
		sampling.setCustomerExtLiabilityList(this.customerExtLiabilityDetailList);
		sampling.setExtFieldRenderList(getExtFieldRenderList());

		doRemoveValidation();

		showErrorDetails(wve, this.samplingDetailsTab);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to show error details if occurred
	 * 
	 **/
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
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
		logger.debug(Literal.LEAVING);
	}

	public Map<String, Object> getDefaultArguments() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("finHeaderList", getHeaderBasicDetails());
		map.put("isNotFinanceProcess", true);
		map.put("ccyFormatter", ccyFormatter);
		map.put("enquiry", this.enqiryModule);
		map.put("isEditable", !this.enqiryModule);
		map.put("enqiryModule", enqiryModule);
		map.put("enqModule", enqiryModule);
		map.put("moduleName", CollateralConstants.SAMPLING_MODULE);
		return map;
	}

	private ArrayList<Object> getHeaderBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();

		arrayList.add(0, this.sampling.getKeyReference());
		arrayList.add(1, this.sampling.getFinType());
		arrayList.add(2, this.sampling.getFinTypeDesc());
		arrayList.add(3, this.sampling.getBranchCode());
		arrayList.add(4, this.sampling.getBranchDesc());
		arrayList.add(5, CurrencyUtil.format(sampling.getLoanAmountRequested(), ccyFormatter));
		arrayList.add(6, this.sampling.getNumberOfTerms());
		arrayList.add(7, DateUtil.format(this.sampling.getCreatedOn(), DateFormat.SHORT_DATE));

		BigDecimal rate = BigDecimal.ZERO;
		if (sampling.getRepayBaseRate() != null) {
			RateDetail details = RateUtil.rates(sampling.getRepayBaseRate(), sampling.getFinccy(),
					sampling.getRepaySpecialRate(), sampling.getRepayMargin(), sampling.getRepayMinRate(),
					sampling.getRepayMaxRate());
			rate = details.getNetRefRateLoan();
		} else {
			rate = sampling.getRepayProfitRate();
		}
		arrayList.add(8, PennantApplicationUtil.formatRate(rate.doubleValue(), 2));

		return arrayList;
	}

	public int getLiabilitySeq() {
		int idNumber = 0;
		if (getCustomerExtLiabilityDetailList() != null && !getCustomerExtLiabilityDetailList().isEmpty()) {
			for (CustomerExtLiability customerExtLiability : getCustomerExtLiabilityDetailList()) {
				if (customerExtLiability.getCustCif().equals(sampling.getCustCif())) {
					int tempId = customerExtLiability.getSeqNo();
					if (tempId > idNumber) {
						idNumber = tempId;
					}
				}
			}
		}
		return idNumber + 1;
	}

	// New Button Event Customer Income List
	public void onClick$btnNew_CustomerIncome(Event event) {
		logger.debug(Literal.ENTERING);
		CustomerIncome income = new CustomerIncome();
		income.setNewRecord(true);
		income.setWorkflowId(0);
		income.setCustId(this.sampling.getCustId());
		income.setCustCif(this.sampling.getCustCif());
		income.setCustShrtName(this.sampling.getCustShrtName());

		final Map<String, Object> map = new HashMap<>();
		map.put("customerIncome", income);
		map.put("samplingDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("finReference", sampling.getKeyReference());
		map.put("coApplicants", coApplicantIncomeCustomers);
		map.put("ccyFormatter", ccyFormatter);
		map.put("roleCode", getRole());
		map.put("enqiryModule", enqiryModule);
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerIncome/CustomerIncomeDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	// New Button Event For Obligation
	public void onClick$btnNew_Obligation(Event event) {
		logger.debug(Literal.ENTERING);
		CustomerExtLiability laibility = new CustomerExtLiability();
		laibility.setNewRecord(true);
		laibility.setWorkflowId(0);
		laibility.setCustId(this.sampling.getCustId());
		laibility.setCustCif(this.sampling.getCustCif());
		laibility.setCustShrtName(this.sampling.getCustShrtName());
		laibility.setSeqNo(getLiabilitySeq());
		final Map<String, Object> map = new HashMap<>();
		map.put("externalLiability", laibility);
		map.put("finFormatter", ccyFormatter);
		map.put("samplingDialogCtrl", this);
		map.put("coApplicants", coApplicantObligationCustomers);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		map.put("enqiryModule", enqiryModule);
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerExtLiabilityDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Document Details Data in Sampling
	 */
	protected void appendDocumentDetailTab() {
		logger.debug(Literal.ENTERING);
		createTab("DOCUMENTDETAIL", true);
		final Map<String, Object> map = getDefaultArguments();
		map.put("documentDetails", getSampling().getDocuments());
		map.put("module", DocumentCategories.SAMPLING.getKey());
		map.put("enqiryModule", enqiryModule);
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DocumentDetailDialog.zul",
				getTabpanel("DOCUMENTDETAIL"), map);
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Method for Rendering Customer Details Data in Sampling
	 */
	protected void appendCustomerDetailTab() {
		logger.debug(Literal.ENTERING);
		createTab("CUSTOMERDETAIL", true);
		final Map<String, Object> map = getDefaultArguments();
		String pageName = PennantAppUtil.getCustomerPageName();
		map.put("customerDetails", getSampling().getCustomerDetails());
		map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
		map.put("enqiryModule", true);
		Executions.createComponents(pageName, getTabpanel("CUSTOMERDETAIL"), map);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Customer Details Data in Sampling
	 */
	protected void appendCoApplicantDetailTab() {
		logger.debug(Literal.ENTERING);
		createTab("COAPPLICANT", true);

		Long finID = financeDetailService.getFinID(this.sampling.getKeyReference());

		FinScheduleData finScheduleData = financeDetailService.getFinSchDataById(finID, "_View", true);
		final Map<String, Object> map = getDefaultArguments();
		map.put("financeMain", finScheduleData.getFinanceMain());
		map.put("isFinanceProcess", false);
		map.put("enquiry", true);
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/JointAccountDetailDialog.zul",
				getTabpanel("COAPPLICANT"), map);
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Method for Rendering Customer Details Data in Sampling
	 */
	protected void appendQueryModuleTab() {
		logger.debug(Literal.ENTERING);
		createTab("QUERYMODULE", true);
		final Map<String, Object> map = getDefaultArguments();
		map.put("queryDetail", this.sampling.getQueryDetail());
		map.put("sampling", this.sampling);
		Executions.createComponents("/WEB-INF/pages/LoanQuery/QueryDetail/FinQueryDetailList.zul",
				getTabpanel("QUERYMODULE"), map);
		logger.debug(Literal.LEAVING);

	}

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug(Literal.ENTERING);
		String tabName = Labels.getLabel("tab_label_" + moduleID);
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight("100%");
		ComponentsCtrl.applyForward(tab, "onSelect=" + selectMethodName);
		logger.debug(Literal.LEAVING);
	}

	public void doFillCustomerExtLiabilityDetails(List<CustomerExtLiability> customerExtLiabilityDetails) {
		logger.debug(Literal.ENTERING);
		this.listBoxObligations.getItems().clear();

		BigDecimal totalOriginalAmount = BigDecimal.ZERO;
		BigDecimal totalInstalmentAmount = BigDecimal.ZERO;
		BigDecimal totalOutStandingBal = BigDecimal.ZERO;

		BigDecimal originalAmount;
		BigDecimal instalmentAmount;
		BigDecimal outStandingBal;

		if (customerExtLiabilityDetails != null) {
			for (CustomerExtLiability custExtLiability : customerExtLiabilityDetails) {
				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(custExtLiability.getCustCif());
				lc.setParent(item);

				lc = new Listcell(custExtLiability.getCustShrtName());
				lc.setParent(item);

				if (primaryCustomer.contains(custExtLiability.getCustCif())) {
					lc = new Listcell("Primary Customer");
				} else {
					lc = new Listcell("Co Applicant");
				}
				lc.setParent(item);

				if (custExtLiability.getFinDate() == null) {
					lc = new Listcell();
				} else {
					lc = new Listcell(DateUtil.formatToLongDate(custExtLiability.getFinDate()));
				}
				lc.setParent(item);

				lc = new Listcell(custExtLiability.getFinType());
				lc.setTooltip(custExtLiability.getFinTypeDesc());
				lc.setParent(item);

				lc = new Listcell(custExtLiability.getLoanBank());
				lc.setTooltip(custExtLiability.getLoanBankName());
				lc.setParent(item);

				originalAmount = custExtLiability.getOriginalAmount();
				if (originalAmount == null) {
					originalAmount = BigDecimal.ZERO;
				}
				totalOriginalAmount = totalOriginalAmount.add(originalAmount);
				lc = new Listcell(CurrencyUtil.format(originalAmount, ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				instalmentAmount = custExtLiability.getInstalmentAmount();
				if (instalmentAmount == null) {
					instalmentAmount = BigDecimal.ZERO;
				}
				totalInstalmentAmount = totalInstalmentAmount.add(instalmentAmount);
				lc = new Listcell(CurrencyUtil.format(instalmentAmount, ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				outStandingBal = custExtLiability.getOutstandingBalance();
				if (outStandingBal == null) {
					outStandingBal = BigDecimal.ZERO;
				}
				totalOutStandingBal = totalOutStandingBal.add(outStandingBal);
				lc = new Listcell(CurrencyUtil.format(outStandingBal, ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				lc = new Listcell(custExtLiability.getFinStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(custExtLiability.getRecordStatus()));
				lc.setParent(item);

				item.setAttribute("data", custExtLiability);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerExtLiabilityItemDoubleClicked");
				this.listBoxObligations.appendChild(item);

			}
			// add summary list item
			if (this.listBoxObligations.getItems() != null && !this.listBoxObligations.getItems().isEmpty()) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(Labels.getLabel("label_CustomerExtLiabilityDialog_Totals.value"));
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell(CurrencyUtil.format(totalOriginalAmount, ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(CurrencyUtil.format(totalInstalmentAmount, ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(CurrencyUtil.format(totalOutStandingBal, ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				item.setAttribute("data", "");

				sampling.setTotalLiability(totalInstalmentAmount);
				calculateEligibility(false);

				this.listBoxObligations.appendChild(item);
			}
			setCustomerExtLiabilityDetailList(customerExtLiabilityDetails);
		}
		logger.debug(Literal.LEAVING);
	}

	public void doFillCustomerIncome(List<CustomerIncome> incomes) {
		logger.debug(Literal.ENTERING);
		setIncomeList(incomes);
		createIncomeGroupList(incomes);
		logger.debug(Literal.LEAVING);
	}

	private void createIncomeGroupList(List<CustomerIncome> incomes) {
		if (incomes != null && !incomes.isEmpty()) {
			BigDecimal totIncome = BigDecimal.ZERO;
			Map<String, List<CustomerIncome>> incomeMap = new HashMap<>();
			for (CustomerIncome customerIncome : incomes) {
				String category = StringUtils.trimToEmpty(customerIncome.getCategory());
				if (customerIncome.getIncomeExpense().equals(PennantConstants.INCOME)) {
					totIncome = totIncome.add(customerIncome.getCalculatedAmount());
					if (incomeMap.containsKey(category)) {
						incomeMap.get(category).add(customerIncome);
					} else {
						ArrayList<CustomerIncome> list = new ArrayList<>();
						list.add(customerIncome);
						incomeMap.put(category, list);
					}
				}
			}
			renderIncomeExpense(incomeMap, totIncome, ccyFormatter);
		}
	}

	private void renderIncomeExpense(Map<String, List<CustomerIncome>> incomeMap, BigDecimal totIncome,
			int ccyFormatter) {
		this.listBoxCustomerIncomeDetails.getItems().clear();
		Listitem item;
		Listcell cell;
		if (incomeMap != null) {
			BigDecimal totalIncome = BigDecimal.ZERO;
			BigDecimal total = BigDecimal.ZERO;
			for (String category : incomeMap.keySet()) {
				List<CustomerIncome> list = incomeMap.get(category);
				if (CollectionUtils.isNotEmpty(list)) {
					for (CustomerIncome customerIncome : list) {
						item = new Listitem();
						cell = new Listcell(customerIncome.getCustCif());
						cell.setParent(item);

						cell = new Listcell(customerIncome.getCustShrtName());
						cell.setParent(item);
						if (primaryCustomer.contains(customerIncome.getCustCif())) {
							cell = new Listcell("Primary Customer");
						} else {
							cell = new Listcell("Co Applicant");
						}
						cell.setParent(item);

						cell = new Listcell(customerIncome.getIncomeType());
						cell.setParent(item);

						cell = new Listcell(customerIncome.getCategoryDesc());
						cell.setParent(item);

						cell = new Listcell(CurrencyUtil.format(customerIncome.getIncome(), ccyFormatter));
						cell.setStyle("text-align:right;");
						cell.setParent(item);

						totalIncome = totalIncome.add(customerIncome.getIncome());
						cell = new Listcell(CurrencyUtil.format(customerIncome.getMargin(), ccyFormatter));
						cell.setStyle("text-align:right;");
						cell.setParent(item);

						BigDecimal calculatedAmount = customerIncome.getCalculatedAmount();
						cell = new Listcell(CurrencyUtil.format(calculatedAmount, ccyFormatter));
						cell.setStyle("text-align:right;");
						cell.setParent(item);

						total = total.add(calculatedAmount);
						cell = new Listcell(customerIncome.getRecordStatus());
						cell.setParent(item);

						item.setAttribute("data", customerIncome);
						ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerIncomeItemDoubleClicked");
						this.listBoxCustomerIncomeDetails.appendChild(item);
					}

				}
			}

			item = new Listitem();
			cell = new Listcell("Total");
			cell.setStyle("font-weight:bold;cursor:default");
			cell.setParent(item);
			cell = new Listcell(CurrencyUtil.format(totalIncome, ccyFormatter));
			cell.setSpan(5);
			cell.setStyle("font-weight:bold; text-align:right;cursor:default");
			cell.setParent(item);

			cell = new Listcell(CurrencyUtil.format(total, ccyFormatter));
			cell.setSpan(2);
			cell.setStyle("font-weight:bold; text-align:right;cursor:default");
			cell.setParent(item);
			cell = new Listcell();
			cell.setStyle("cursor:default");
			cell.setParent(item);
			this.listBoxCustomerIncomeDetails.appendChild(item);

			item = new Listitem();
			cell = new Listcell("Net Income");
			cell.setStyle("font-weight:bold;");
			cell.setParent(item);
			cell = new Listcell(CurrencyUtil.format(totalIncome, ccyFormatter));
			cell.setSpan(5);
			cell.setStyle("font-weight:bold; text-align:right;");
			cell.setParent(item);

			cell = new Listcell(CurrencyUtil.format(total, ccyFormatter));
			cell.setSpan(2);
			cell.setStyle("font-weight:bold; text-align:right;cursor:default");
			cell.setParent(item);
			cell = new Listcell();
			cell.setStyle("cursor:default");
			cell.setParent(item);

			sampling.setTotalIncome(totIncome);
			calculateEligibility(false);

			this.listBoxCustomerIncomeDetails.appendChild(item);
		}

	}

	public void onCollateralItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		final Listitem item = this.listBoxCollaterals.getSelectedItem();
		if (item != null) {
			final SamplingCollateral collateralSetup = (SamplingCollateral) item.getAttribute("data");
			this.sampling.setCollateral(collateralSetup);
			if (isDeleteRecord(collateralSetup.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<>();

				map.put("sampling", sampling);
				map.put("samplingDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("extFieldRenderList", extFieldRenderList);
				map.put("enqiryModule", enqiryModule);
				try {
					Executions.createComponents("/WEB-INF/pages/Sampling/SamplingExtFieldCaptureDialog.zul", null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug(Literal.LEAVING);

	}

	private boolean isDeleteRecord(String rcdType) {
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, rcdType)
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, rcdType)) {
			return true;
		}
		return false;
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
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
		doShowNotes(this.sampling);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		samplingListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.sampling.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final Sampling sampling = new Sampling();
		BeanUtils.copyProperties(this.sampling, sampling);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(sampling);

		isNew = sampling.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(sampling.getRecordType())) {
				sampling.setVersion(sampling.getVersion() + 1);
				if (isNew) {
					sampling.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					sampling.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					sampling.setNewRecord(true);
				}
			}
		} else {
			sampling.setVersion(sampling.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// Document Details Saving

		if (documentDetailDialogCtrl != null) {
			sampling.setDocuments(documentDetailDialogCtrl.getDocumentDetailsList());
		} else {
			sampling.setDocuments(getSampling().getDocuments());
		}

		try {
			if (doProcess(sampling, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
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
	protected boolean doProcess(Sampling sampling, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		sampling.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		sampling.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		sampling.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			sampling.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(sampling.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, sampling);
				}

				if (isNotesMandatory(taskId, sampling)) {
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

			sampling.setTaskId(taskId);
			sampling.setNextTaskId(nextTaskId);
			sampling.setRoleCode(getRole());
			sampling.setNextRoleCode(nextRoleCode);

			// Extended Field details
			if (sampling.getExtFieldRenderList() != null) {
				Map<String, ExtendedFieldRender> extList = sampling.getExtFieldRenderList();
				for (Entry<String, ExtendedFieldRender> ext : extList.entrySet()) {
					ExtendedFieldRender details = ext.getValue();
					details.setReference(ext.getKey().substring(0, ext.getKey().indexOf("-")));
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(sampling.getRecordStatus());
					details.setRecordType(sampling.getRecordType());
					details.setVersion(sampling.getVersion());
					details.setWorkflowId(sampling.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					details.setNewRecord(sampling.isNewRecord());
					if (PennantConstants.RECORD_TYPE_DEL.equals(sampling.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(sampling.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			// Document Details
			if (sampling.getDocuments() != null && !sampling.getDocuments().isEmpty()) {
				for (DocumentDetails details : sampling.getDocuments()) {
					if (StringUtils.isEmpty(StringUtils.trimToEmpty(details.getRecordType()))) {
						continue;
					}

					details.setReferenceId(String.valueOf(sampling.getId()));
					details.setDocModule(CollateralConstants.SAMPLING_MODULE);
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(sampling.getRecordStatus());
					details.setWorkflowId(sampling.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					details.setFinReference(sampling.getKeyReference());
					details.setCustId(Long.valueOf(sampling.getCustId()));
					details.setCustomerCif(sampling.getCustCif());
					if (PennantConstants.RECORD_TYPE_DEL.equals(sampling.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(sampling.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			if (CollectionUtils.isNotEmpty(sampling.getCollaterals())) {
				List<SamplingCollateral> collList = sampling.getCollaterals();
				Map<String, ExtendedFieldHeader> extFieldHeaderList = new HashMap<>();
				for (SamplingCollateral collateralSetup : collList) {

					extendedFieldCtrl = new ExtendedFieldCtrl();
					ExtendedFieldHeader extendedFieldHeader = extendedFieldCtrl.getExtendedFieldHeader(
							CollateralConstants.MODULE_NAME, collateralSetup.getCollateralType());
					extFieldHeaderList.put(collateralSetup.getCollateralRef(), extendedFieldHeader);
				}
				sampling.setExtFieldHeaderList(extFieldHeaderList);
			}

			auditHeader = getAuditHeader(sampling, tranType);
			String operationRefs = getServiceOperations(taskId, sampling);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(sampling, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(sampling, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Sampling sampling = (Sampling) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = samplingService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = samplingService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = samplingService.doApprove(auditHeader);

					if (sampling.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = samplingService.doReject(auditHeader);
					if (sampling.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					ErrorControl.showErrorControl(this.window_SamplingDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_SamplingDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.sampling), true);
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

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final Sampling entity = new Sampling();
		BeanUtils.copyProperties(this.sampling, entity);

		doDelete(String.valueOf(entity.getId()), entity);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(Sampling sampling, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, sampling.getBefImage(), sampling);
		return new AuditHeader(getReference(), null, null, null, auditDetail, sampling.getUserDetails(),
				getOverideMap());
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.sampling.getId());
	}

	public Sampling getSampling() {
		return sampling;
	}

	public void setSampling(Sampling sampling) {
		this.sampling = sampling;
	}

	public List<CustomerIncome> getIncomeList() {
		return incomeList;
	}

	public void setIncomeList(List<CustomerIncome> incomeList) {
		this.incomeList = incomeList;
	}

	public List<CustomerExtLiability> getCustomerExtLiabilityDetailList() {
		return customerExtLiabilityDetailList;
	}

	public void setCustomerExtLiabilityDetailList(List<CustomerExtLiability> customerExtLiabilityDetailList) {
		this.customerExtLiabilityDetailList = customerExtLiabilityDetailList;
	}

	public DocumentDetailDialogCtrl getDocumentDetailDialogCtrl() {
		return documentDetailDialogCtrl;
	}

	public void setDocumentDetailDialogCtrl(DocumentDetailDialogCtrl documentDetailDialogCtrl) {
		this.documentDetailDialogCtrl = documentDetailDialogCtrl;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return CustomerDialogCtrl;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		CustomerDialogCtrl = customerDialogCtrl;
	}

	public FinanceCheckListReferenceDialogCtrl getFinanceCheckListReferenceDialogCtrl() {
		return financeCheckListReferenceDialogCtrl;
	}

	public void setFinanceCheckListReferenceDialogCtrl(
			FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl) {
		this.financeCheckListReferenceDialogCtrl = financeCheckListReferenceDialogCtrl;
	}

	public Map<String, ExtendedFieldRender> getExtFieldRenderList() {
		return extFieldRenderList;
	}

	public void setExtFieldRenderList(Map<String, ExtendedFieldRender> extFieldRenderList) {
		this.extFieldRenderList = extFieldRenderList;
	}

	public void doFillExtendedFileds(Map<String, ExtendedFieldRender> extFieldRenderList) {
		setExtFieldRenderList(extFieldRenderList);
		calculateEligibility(false);
	}

}
