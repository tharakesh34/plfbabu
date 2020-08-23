package com.pennant.webui.customermasters.customer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.BeanUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Progressmeter;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.NotesDAO;
import com.pennant.backend.dao.approvalstatusenquiry.ApprovalStatusEnquiryDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.crm.CrmLeadDetails;
import com.pennant.backend.model.crm.ProductOfferDetails;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.insurance.InsuranceDetails;
import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.backend.model.reports.ReportFilterFields;
import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerOffersService;
import com.pennant.backend.service.customermasters.DirectorDetailService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.util.StaticListUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * This is the controller class for the /customer.zul file.
 */
public class CustomerViewDialogCtrl extends GFCBaseCtrl<CustomerDetails> {
	private static final long serialVersionUID = 9031340167587772517L;
	private static final Logger logger = Logger.getLogger(CustomerViewDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerDialogg;
	protected Label custCIF;
	protected Label custPhoneNumber;
	protected Label custDftBranchDesc;
	protected Label custCRCPR;
	protected Label custGenderCodeDesc;
	protected Label custDOB;
	protected Label custDOBDOI;
	protected Label custLng;
	protected Label motherMaidenName;
	protected Label address1;
	protected Hbox statusBar;
	protected Label customerTitle;
	protected Label corpCustCIF;
	protected Label corpcustDOBDOI;
	protected Label corpcustLngg;
	protected Label corpcountryincorp;
	protected Label corpaddress1;
	protected Box retails;
	protected Label custCIF2;
	protected Label custCoreBank;
	protected Label custCtgCode;
	protected Label custDftBranch;
	protected Label custDftBranchDesc2;
	protected Label custBaseCcy;
	protected Label custBaseCcyDesc;
	protected Label custTypeCode;
	protected Label custTypeCodeDesc;
	protected Label custFirstName;
	protected Label custMiddleName;
	protected Label custLastName;
	protected Label fatherMaidenName;
	protected Label custShrtNamee;
	protected Label custLngg;
	protected Label custLnggDesc;
	protected Label custDOBB;
	protected Label custArabicName;
	protected Label custNationality;
	protected Label custNationalitydesc;
	protected Label custRO1;
	protected Label custRO1Desc;
	protected Label custSalutationCode;
	protected Label noOfDependents;
	protected Label custMaritalStsDesc;
	protected Label target;
	protected Image salariedCustomer;
	protected Label custCRCPR2;
	protected Label custCOB;
	protected Label custCOBDesc;
	protected Label custSectorDesc;
	protected Label custSector;
	protected Image custIsStaff;
	protected Label custSts;
	protected Label custRelatedParty;
	protected Label custSegment;
	protected Label custGroupId;
	protected Label custGroupIdDesc;
	protected Label custIndustryDesc;
	protected Label custIndustry;
	protected Label custStaffID;
	protected Label custDSA;
	protected Label custDSACode;
	protected Label custDSADept;
	protected Label custRiskCountry;
	protected Label custRiskCountryDesc;
	protected Label custParentCountry;
	protected Label custParentCountryDesc;
	protected Label custSubSector;
	protected Label custSubSegment;
	protected Label custGenderCodeDescc;
	protected Label retail_applicationNo;
	protected Box Corporates;
	protected Label custCIFF2;
	protected Label custCoreBankk;
	protected Label custCtgCodee;
	protected Label custDftBranchh;
	protected Label custDftBranchDescc2;
	protected Label custBaseCcyy;
	protected Label custBaseCcyDescc;
	protected Label custTypeCodee;
	protected Label custTypeCodeDescc;
	protected Label custArabicNamee;
	protected Label custNationalityy;
	protected Label custNationalitydescc;
	protected Label custROO1;
	protected Label custRO1Descc;
	protected Label custCOOB;
	protected Label custCOBDescc;
	protected Label custSectorr;
	protected Label custSectorDescc;
	protected Label custIndustryDescc;
	protected Label custGroupIdd;
	protected Label custGroupIdDescc;
	protected Label custSubSectorr;
	protected Label corpcustLng;
	protected Label corpcustLngDesc;
	protected Label custCRCPR3;
	protected Label custIndustryy;
	protected Label corp_applicationNo;
	protected Label corpcustDOBB;
	protected Component parent = null;
	protected Hbox hbox_empDetails;
	protected Listbox listBoxCustomerEmploymentDetail;
	protected Listbox listBoxloanApprovalDetails;
	protected Listheader listheader_CustEmp_RecordStatus;
	protected Listheader listheader_CustEmp_RecordType;
	private List<CustomerEmploymentDetail> customerEmploymentDetailList = new ArrayList<CustomerEmploymentDetail>();
	protected Label recordStatus4;
	protected Image customerPic4;
	protected Label custShrtName4;
	private Progressmeter basicProgress;
	//private Progressmeter basicProgress1;
	protected Listbox listBoxCustomerDirectory;
	protected Label custShrtName3;
	protected Label recordStatus3;
	protected Label recordType1;
	protected Groupbox gb_information;
	protected Space space_CustArabicName;
	protected Checkbox salaryTransferred;
	protected Image customerPic3;
	protected Image leftBar;
	protected Uppercasebox eidNumber;
	protected Label label_CustomerDialog_EIDNumber;
	protected Textbox custTradeLicenceNum;
	protected Tabpanels tabpanels;
	protected Tabs tabsIndexCenter;
	protected String moduleName;
	/* West Div Images */
	protected Image imgbasicDetails;
	protected Image imgadditionalDetails;
	protected Image imgkycDetails;
	protected Image imgfinancialDetails;
	protected Image imgbankingDetails;
	protected Image imghelp;
	protected Image imgloanDetails;
	protected Image imgcustomerSummary;
	protected Image imgcollateralDetails;
	protected Image imgvasDetails;
	protected Image imgpendingLoanDetails;
	protected Image imgshareHolderDetails;
	protected Label genderDesc;
	private Div customerSumary;
	private Div loanDetails;
	private Div pendingLoanDetails;
	private Div collateralDetails;
	private Div vasDetails;
	private Div gb_additionalDetails;
	private Div gb_basicDetails;
	private Div gb_kycDetails;
	private Div gb_financialDetails;
	private Div gb_help;
	private Div gb_bankingDetails;
	private Div shareHolder;
	protected Label recordStatus1;
	protected Image customerPic;
	protected Vlayout borderStyle;
	protected Label custShrtName2;
	protected Image customerPic1;
	protected Label custShrtName;
	protected Image corpDND;
	protected Image retailDND;
	protected Label religion;
	protected Label caste;
	protected Label employmentType;
	protected Label religionDesc;
	protected Label casteDesc;
	protected Label employmentTypeDesc;
	protected Button crmRequest;
	protected Button downloadFaq;
	protected Button fetchCustOffers;
	private String userRole = "";
	/** Customer Employer Fields **/
	protected ExtendedCombobox empName;
	protected Label age;
	protected Label exp;

	protected Listbox listBoxCustomerDocuments;
	private List<CustomerDocument> customerDocumentDetailList = new ArrayList<CustomerDocument>();

	protected Button btnNew_CustomerAddress;
	protected Listbox listBoxCustomerAddress;
	private List<CustomerAddres> customerAddressDetailList = new ArrayList<CustomerAddres>();

	protected Button btnNew_CustomerPhoneNumber;
	protected Listbox listBoxCustomerPhoneNumbers;
	private List<CustomerPhoneNumber> customerPhoneNumberDetailList = new ArrayList<CustomerPhoneNumber>();
	protected Listheader listheader_CustPhone_RecordStatus;
	protected Listheader listheader_CustPhone_RecordType;
	private List<CustomerPhoneNumber> phoneNumberList = new ArrayList<CustomerPhoneNumber>();

	protected Listbox listBoxCustomerEmails;
	private List<CustomerEMail> customerEmailDetailList = new ArrayList<CustomerEMail>();

	protected Listbox listBoxCustomerBankInformation;
	private List<CustomerBankInfo> customerBankInfoDetailList = new ArrayList<CustomerBankInfo>();

	protected Listbox listBoxCustomerChequeInformation;
	private List<CustomerChequeInfo> customerChequeInfoDetailList = new ArrayList<CustomerChequeInfo>();

	protected Listbox listBoxCustomerFinExposure;

	private List<ValueLabel> countryList;
	private List<ValueLabel> docTypeList;

	protected Listbox listBoxCustomerExternalLiability;
	private List<CustomerExtLiability> customerExtLiabilityDetailList = new ArrayList<CustomerExtLiability>();

	protected Listbox listBoxCustomerLoanDetails;
	protected Listbox listBoxCustomerVasDetails;
	protected Listbox listBoxCustomerCollateralDetails;
	protected Listbox listBoxDownloadsS;
	protected Listbox listBoxCustomerOffers;
	protected Listheader listheader_JointCust;

	// Customer Employment List
	protected Row row_EmploymentDetails;
	protected Button btnNew_CustomerEmploymentDetail;
	// Customer Income details List
	protected Listbox listBoxCustomerIncome;
	protected Listheader listheader_CustInc_RecordStatus;
	protected Listheader listheader_CustInc_RecordType;
	private List<CustomerIncome> incomeList = new ArrayList<CustomerIncome>();

	private CustomerDetails customerDetails;
	private transient CustomerListCtrl customerListCtrl;

	// Declaration of Service(s) & DAO(s)
	private transient CustomerDetailsService customerDetailsService;
	private int ccyFormatter = 0;
	private int old_ccyFormatter = 0;
	private String moduleType = "";
	protected Div divKeyDetails;
	protected Grid grid_KYCDetails;
	protected Grid grid_BankDetails;

	private boolean isRetailCustomer = false;
	private boolean isCustPhotoAvail = false;

	// Customer Directory details List
	protected Button btnNew_DirectorDetail;
	protected Listheader listheader_CustDirector_RecordStatus;
	protected Listheader listheader_CustDirector_RecordType;
	private List<DirectorDetail> directorList = new ArrayList<DirectorDetail>();
	protected Label label_CustomerDialog_CustNationality;
	private transient DirectorDetailService directorDetailService;
	Date appDate = DateUtility.getAppDate();
	Date startDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");

	private boolean isFinanceProcess = false;
	private boolean isNotFinanceProcess = false;
	private boolean isEnqProcess = false;

	private List<CustomerBankInfo> CustomerBankInfoList;
	private Object financeMainDialogCtrl;
	private String module = "";
	private static Map<String, String> cibilIdTypes = new HashMap<>();
	private static Map<String, String> cibilAddrCategory = StaticListUtil.getCibilAddrCategory();
	private static Map<String, String> cibilResidenceCode = StaticListUtil.getCibilResidenceCode();
	private static Map<String, String> cibilPhoneTypes = new HashMap<>();
	private static Map<String, String> cibilOccupationTypes = StaticListUtil.getCibilOccupationCode();
	private static Map<String, String> cibilloanTypes = new HashMap<>();
	private Map<String, Configuration> TEMPLATES = new HashMap<String, Configuration>();
	private ExtendedFieldCtrl extendedFieldCtrl = null;
	protected ApprovalStatusEnquiryDAO approvalStatusEnquiryDAO;
	protected NotesDAO notesDAO;

	/** Customer LoanDetails ListHeaders **/
	protected Listheader listheader_SOA;
	protected Listheader listheader_NOC;
	protected Listheader listheader_ForeClosure;
	protected Listheader listheader_InterestCertificate;
	protected Listheader listheader_DPD;
	protected Listheader listheader_GSTInvoice;
	protected Listheader listheader_CreditNote;
	protected Listheader listheader_Cibil;
	//Get SystemParameter 
	String pageExt = SysParamUtil.getValueAsString("CUST_DIALOG_EXT");

	/** New Labels For HL MLOD **/
	protected Label entityType;
	protected Label labelEntityType;
	protected Label otherReligion;
	protected Label labelOtherReligion;
	protected Label labelOtherCaste;
	protected Label otherCaste;
	protected Label residentialStatus;
	protected Label labelResidentialStatus;
	protected Label labelQualifiaction;
	protected Label qualifiaction;
	protected Label natureOfBusiness;
	protected Label labelNatureOfBusiness;
	protected Label labelProfession;
	protected Label profession;
	protected Label cKYCRef;
	protected Label labelCKYCRef;
	private float progressPerc;
	private DMSService dMSService;

	/**
	 * default constructor.<br>
	 */
	public CustomerViewDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CustomerDialogg(Event event) throws Exception {
		logger.debug("Entering");

		setPageComponents(window_CustomerDialogg);

		try {

			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
			}

			if (arguments.containsKey("customerDetails")) {
				customerDetails = (CustomerDetails) arguments.get("customerDetails");
				CustomerDetails befImage = new CustomerDetails();
				BeanUtils.copyProperties(customerDetails, befImage);
				customerDetails.setBefImage(befImage);
			}

			if (arguments.containsKey("module")) {
				module = (String) arguments.get("module");
			}

			if (module.equals("360")) {
				moduleType = PennantConstants.MODULETYPE_ENQ;
			}

			Customer customer = customerDetails.getCustomer();
			ccyFormatter = CurrencyUtil.getFormat(customer.getCustBaseCcy());
			old_ccyFormatter = ccyFormatter;

			if (isFinanceProcess || isEnqProcess) {
				if (arguments.containsKey("roleCode")) {
					setRole((String) arguments.get("roleCode"));
					getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"),
							"Customer_ViewDialog");
				}
			} else {
				if (PennantConstants.MODULETYPE_ENQ.equals(moduleType) || isNotFinanceProcess) {
					doLoadWorkFlow(false, customer.getWorkflowId(), customer.getNextTaskId());
				} else {
					doLoadWorkFlow(customer.isWorkflow(), customer.getWorkflowId(), customer.getNextTaskId());
				}
			}

			// READ OVERHANDED params !
			// we get the customerListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or delete customer here.
			if (arguments.containsKey("customerListCtrl")) {
				customerListCtrl = (CustomerListCtrl) arguments.get("customerListCtrl");
			}

			if (arguments.containsKey("module")) {
				module = (String) arguments.get("module");
			}

			if (StringUtils.isNotEmpty(customerDetails.getCustomer().getCustCtgCode()) && StringUtils
					.equals(customerDetails.getCustomer().getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
				isRetailCustomer = true;
			}
			doCheckRights();
			doShowDialog(customerDetails);
			if (arguments.containsKey("ProspectCustomerEnq")) {
				window_CustomerDialogg.doModal();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			closeDialog();
		}

		logger.debug("Leaving");
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		getUserWorkspace().allocateAuthorities("Customer_ViewDialog", userRole);
		crmRequest.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_RaiseARequest"));
		downloadFaq.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_FAQ"));
		fetchCustOffers.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_FetchCustomerOffers"));
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCustomer
	 * @throws Exception
	 */
	public void doShowDialog(CustomerDetails aCustomerDetails) throws Exception {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aCustomerDetails.isNewRecord()) {
			btnCtrl.setInitNew();
		} else {
			if (isWorkFlowEnabled()) {
			} else {
				btnCtrl.setInitNew();
			}
		}
		try {
			doWriteBeanToComponents(aCustomerDetails);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			window_CustomerDialogg.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomer
	 *            Customer
	 * @throws IOException
	 */

	public void doWriteBeanToComponents(CustomerDetails aCustomerDetails) throws IOException {
		logger.debug("Entering");
		int i = 0;
		Customer aCustomer = aCustomerDetails.getCustomer();

		if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_INDIV, customerDetails.getCustomer().getCustCtgCode())) {
			retails.setVisible(true);
			Corporates.setVisible(false);

			custCIF2.setValue(aCustomer.getCustCIF());
			if (aCustomer.getCustCIF() == null) {
				custCIF2.setStyle("color:orange; font:12px");
				custCIF2.setValue("- - - - - - - - -");
			}

			custShrtNamee.setValue(aCustomer.getCustShrtName());
			if (aCustomer.getCustShrtName() == null) {
				custShrtName.setStyle("color:orange; font:12px");
				custShrtName.setValue("- - - - - - - - -");
			}

			custFirstName.setValue(StringUtils.trimToEmpty(aCustomer.getCustFName()));
			if (aCustomer.getCustFName() == null) {
				custFirstName.setStyle("color:orange; font:12px");
				custFirstName.setValue("- - - - - - - - -");
			}

			custCoreBank.setValue(aCustomer.getCustCoreBank());
			if (StringUtils.isEmpty(aCustomer.getCustCoreBank())) {
				custCoreBank.setStyle("color:orange; font:12px");
				custCoreBank.setValue("- - - - - - - - -");
			}
			custCtgCode.setValue(aCustomer.getCustCtgCode());
			if (aCustomer.getCustSegment() != null) {
				i++;
			}
			custDftBranch.setValue(aCustomer.getCustDftBranch());
			if (aCustomer.getCustDftBranch() != null) {
				i++;
			}
			custDftBranch.setValue(aCustomer.getCustDftBranch() + ", ");
			custDftBranchDesc.setValue(aCustomer.getLovDescCustDftBranchName());
			custDftBranchDesc2.setValue(aCustomer.getLovDescCustDftBranchName());
			custDftBranchDesc2.setStyle("color:orange;");
			custBaseCcy.setValue(aCustomer.getCustBaseCcy() + ", ");
			custBaseCcyDesc.setValue(CurrencyUtil.getCcyDesc(aCustomer.getCustBaseCcy()));
			custBaseCcyDesc.setStyle("color:orange;");
			if (aCustomer.getCustBaseCcy() != null) {
				i++;
			}
			custTypeCode.setValue(aCustomer.getCustTypeCode() + ", ");
			if (aCustomer.getCustSegment() != null) {
				i++;
			}
			custTypeCodeDesc.setValue(aCustomer.getLovDescCustTypeCodeName());
			custTypeCodeDesc.setStyle("color:orange;");
			custMiddleName.setValue(StringUtils.trimToEmpty(aCustomer.getCustMName()));
			if (StringUtils.isEmpty(aCustomer.getCustMName())) {
				custMiddleName.setStyle("color:orange;");
				custMiddleName.setValue("- - - - - - - - -");
			}
			custLastName.setValue(StringUtils.trimToEmpty(aCustomer.getCustLName()));
			if (aCustomer.getCustShrtName() != null) {
				i++;
			}
			custShrtName2.setValue(aCustomer.getCustShrtName());
			custSts.setValue(aCustomer.getCustSts());
			if (aCustomer.getCustSts() == null) {
				custSts.setStyle("color:orange;");
				custSts.setValue("- - - - - - - - -");
			}
			custArabicName.setValue(aCustomer.getCustShrtNameLclLng());
			if (StringUtils.isEmpty(aCustomer.getCustShrtNameLclLng())) {
				custArabicName.setStyle("color:orange; font:12px");
				custArabicName.setValue("- - - - - - - - -");
			}
			custNationality.setValue(aCustomer.getCustNationality() + ", ");
			custNationalitydesc.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustNationalityName()));
			custNationalitydesc.setStyle("color:orange;");
			custRO1.setValue(aCustomer.getCustRO1() + ", ");
			if (aCustomer.getCustRO1() != 0) {
				i++;
			}
			custRO1Desc.setValue(aCustomer.getLovDescCustRO1Name());
			custRO1Desc.setStyle("color:orange;");
			custSalutationCode.setValue(aCustomer.getCustSalutationCode());
			if (aCustomer.getCustSalutationCode() != null) {
				i++;
			}
			if (aCustomer.getCustSalutationCode() == null) {
				custSalutationCode.setStyle("color:orange; font:12px");
				custSalutationCode.setValue("- - - - - - - - -");
			}
			noOfDependents.setValue(String.valueOf(aCustomer.getNoOfDependents()));
			custMaritalStsDesc.setValue(aCustomer.getLovDescCustMaritalStsName());
			if (aCustomer.getLovDescCustMaritalStsName() != null) {
				i++;
			}
			if (aCustomer.getLovDescCustMaritalStsName() == null) {
				custMaritalStsDesc.setStyle("color:orange; font:12px");
				custMaritalStsDesc.setValue("- - - - - - - - -");
			}
			target.setValue(aCustomer.getLovDescTargetName());
			if (StringUtils.isEmpty(aCustomer.getLovDescTargetName())) {
				target.setStyle("color:orange; font:12px");
				target.setValue("- - - - - - - - -");
			}
			if (aCustomer.isSalariedCustomer() == true) {
				salariedCustomer.setSrc("images/icons/customerenquiry/activecheck.png");
			} else {
				salariedCustomer.setSrc("images/icons/customerenquiry/inactivecheck.png");
			}
			custCOB.setValue(aCustomer.getCustCOB() + ", ");
			custCOBDesc.setValue(aCustomer.getLovDescCustCOBName());
			custCOBDesc.setStyle("color:orange;");
			custSector.setValue(aCustomer.getCustSector() + ", ");
			if (aCustomer.getCustSector() != null) {
				i++;
			}
			custSectorDesc.setValue(aCustomer.getLovDescCustSectorName());
			custSectorDesc.setStyle("color:orange;");
			custIndustryDesc.setValue(aCustomer.getLovDescCustIndustryName());
			custIndustryDesc.setStyle("color:orange;");
			if (aCustomer.getLovDescCustIndustryName() == null) {
				custIndustryDesc.setStyle("color:orange; font:12px");
				custIndustryDesc.setValue("- - - - - - - - -");
			}
			custIndustry.setValue(aCustomer.getCustIndustry() + ", ");
			custSegment.setValue(StringUtils.trimToEmpty(aCustomer.getCustSegment()));
			if (aCustomer.getCustSegment() == null) {
				custSegment.setStyle("color:orange;");
				custSegment.setValue("- - - - - - - - -");
			}
			if (aCustomer.getLovDescCustIndustryName() != null) {
				i++;
			}
			custRelatedParty.setValue(aCustomer.getCustAddlVar83());
			if (StringUtils.isEmpty(aCustomer.getCustAddlVar83())) {
				custRelatedParty.setStyle("color:orange; font:12px");
				custRelatedParty.setValue("- - - - - - - - -");
			}
			if (aCustomer.getCustGroupID() != 0) {
				custGroupId.setValue(String.valueOf(aCustomer.getCustGroupID()) + ", ");
				custGroupIdDesc.setValue(StringUtils.trimToEmpty(aCustomer.getLovDesccustGroupIDName()));
				custGroupIdDesc.setStyle("color:orange;");
			} else {
				custGroupId.setStyle("color:orange; font:12px");
				custGroupId.setValue("- - - - - - - - -");
			}
			if (aCustomer.isCustIsStaff() == true) {
				custIsStaff.setSrc("images/icons/customerenquiry/activecheck.png");
			} else {
				custIsStaff.setSrc("images/icons/customerenquiry/inactivecheck.png");
			}
			custStaffID.setValue(aCustomer.getCustStaffID());
			if (StringUtils.isEmpty(aCustomer.getCustStaffID())) {
				custStaffID.setStyle("color:orange; font:12px");
				custStaffID.setValue("- - - - - - - - -");
			}
			custDSA.setValue(aCustomer.getCustDSA());
			if (StringUtils.isEmpty(aCustomer.getCustDSA())) {
				custDSA.setStyle("color:orange; font:12px");
				custDSA.setValue("- - - - - - - - -");
			}
			custRiskCountry.setValue(aCustomer.getCustRiskCountry() + ", ");
			custRiskCountryDesc.setValue(aCustomer.getLovDescCustRiskCountryName());
			custRiskCountryDesc.setStyle("color:orange;");
			custDSACode.setValue(aCustomer.getCustDSADept() + ", ");
			custDSADept.setValue(aCustomer.getLovDescCustDSADeptName());
			custDSADept.setStyle("color:orange;");
			if (StringUtils.isEmpty(aCustomer.getLovDescCustDSADeptName())) {
				custDSADept.setStyle("color:orange; font:12px");
				custDSADept.setValue("- - - - - - - - -");
			}
			custParentCountry.setValue(aCustomer.getCustParentCountry() + ", ");
			custParentCountryDesc.setValue(aCustomer.getLovDescCustParentCountryName());
			custParentCountryDesc.setStyle("color:orange;");
			custSubSector.setValue(aCustomer.getCustSubSector());
			if (aCustomer.getCustSubSector() == null) {
				custSubSector.setStyle("color:orange; font:12px");
				custSubSector.setValue("- - - - - - - - -");
			}
			custSubSegment.setValue(aCustomer.getCustSubSegment());
			if (aCustomer.getCustSubSegment() == null) {
				custSubSegment.setStyle("color:orange; font:12px");
				custSubSegment.setValue("- - - - - - - - -");
			}

			custDOB.setValue(DateUtility.format(aCustomer.getCustDOB(), "dd/MM/yyyy"));
			if (aCustomer.getCustDOB() != null) {
				i++;
			}
			custDOBB.setValue(DateUtility.format(aCustomer.getCustDOB(), "dd/MM/yyyy"));
			if (aCustomer.getCustDOB() != null) {
				i++;
			}
			fatherMaidenName.setValue(aCustomer.getCustMotherMaiden());
			if (aCustomer.getCustMotherMaiden() == null) {
				fatherMaidenName.setStyle("color:orange; font:12px");
			}
			if (aCustomer.getCustMotherMaiden() != null) {
				i++;
			}

			for (CustomerPhoneNumber customerPhoneNumber : aCustomerDetails.getCustomerPhoneNumList()) {
				custPhoneNumber.setValue(
						customerPhoneNumber.getPhoneNumber() == null ? "" : customerPhoneNumber.getPhoneNumber());
			}

			custCRCPR.setValue(aCustomer.getCustCRCPR());
			if (StringUtils.isEmpty(aCustomer.getCustCRCPR())) {
				custCRCPR.setStyle("color:orange; font:12px");
				custCRCPR.setValue("- - - - - - - - -");
			}

			if (aCustomer.getCustCRCPR() != null) {
				i++;
			}
			custCRCPR2.setValue(aCustomer.getCustCRCPR());
			if (StringUtils.isEmpty(aCustomer.getCustCRCPR())) {
				custCRCPR2.setStyle("color:orange; font:12px");
				custCRCPR2.setValue("- - - - - - - - -");
			}
			if (aCustomer.getLovDescCustLngName() != null) {
				i++;
			}
			custLngg.setValue(aCustomer.getCustLng());
			if (aCustomer.getCustLng() != null) {
				i++;
			}
			custLngg.setValue(aCustomer.getCustLng() + ", ");
			custLnggDesc.setValue(aCustomer.getLovDescCustLngName());
			custLnggDesc.setStyle("color:orange; font:12px");
			custGenderCodeDesc.setValue(aCustomer.getLovDescCustGenderCodeName());
			custGenderCodeDesc.setStyle("padding-left:74px");
			if (aCustomer.getLovDescCustGenderCodeName() != null) {
				i++;
			}
			custGenderCodeDescc.setValue(aCustomer.getLovDescCustGenderCodeName());
			if (!StringUtils.isEmpty(aCustomer.getApplicationNo())) {
				retail_applicationNo.setValue(aCustomer.getApplicationNo());
			} else {
				retail_applicationNo.setStyle("color:orange; font:12px");
				retail_applicationNo.setValue("- - - - - - - - -");
			}
			if (aCustomer.getLovDescCustGenderCodeName() != null) {
				i++;
			}
			religion.setValue(aCustomer.getReligionCode() + ", ");
			if (aCustomer.getReligionCode() == null) {
				religion.setStyle("color:orange; font:12px");
				religion.setValue("- - - - ");
			}
			religionDesc.setValue(aCustomer.getReligionDesc());
			religionDesc.setStyle("color:orange;");
			caste.setValue(aCustomer.getCasteCode() + ", ");
			if (aCustomer.getCasteCode() == null) {
				caste.setStyle("color:orange; font:12px");
				caste.setValue("- - - -");
			}
			casteDesc.setValue(aCustomer.getCasteDesc());
			casteDesc.setStyle("color:orange;");
			if (aCustomer.isDnd() == true) {
				retailDND.setSrc("images/icons/customerenquiry/activecheck.png");
			} else {
				retailDND.setSrc("images/icons/customerenquiry/inactivecheck.png");
			}
			employmentType.setValue(aCustomer.getSubCategory());
			if (pageExt != null) {
				List<ValueLabel> residentialStsList = PennantStaticListUtil.getResidentialStsList();
				for (ValueLabel valueLabel : residentialStsList) {
					if ((valueLabel.getValue().equals(aCustomer.getCustResidentialSts()))) {
						residentialStatus.setValue(valueLabel.getLabel());
						break;
					}
				}
				if (StringUtils.isEmpty(residentialStatus.getValue())) {
					residentialStatus.setStyle("color:orange; font:12px");
					residentialStatus.setValue("- - - - - - - - -");
				}
				if (!StringUtils.isEmpty(aCustomer.getOtherReligion())) {
					otherReligion.setValue(aCustomer.getOtherReligion());
				} else {
					otherReligion.setStyle("color:orange; font:12px");
					otherReligion.setValue("- - - - - - - - -");
				}
				if (!StringUtils.isEmpty(aCustomer.getOtherCaste())) {
					otherCaste.setValue(aCustomer.getOtherCaste());
				} else {
					otherCaste.setStyle("color:orange; font:12px");
					otherCaste.setValue("- - - - - - - - -");
				}
				List<ValueLabel> natureofBusinessList = PennantStaticListUtil.getNatureofBusinessList();
				for (ValueLabel valueLabel : natureofBusinessList) {
					if ((valueLabel.getValue().equals(aCustomer.getNatureOfBusiness()))) {
						natureOfBusiness.setValue(valueLabel.getLabel());
						break;
					}
				}
				if (StringUtils.isEmpty(natureOfBusiness.getValue())) {
					natureOfBusiness.setStyle("color:orange; font:12px");
					natureOfBusiness.setValue("- - - - - - - - -");
				}
				if (!StringUtils.isEmpty(aCustomer.getQualification())) {
					qualifiaction.setValue(aCustomer.getQualification());
				} else {
					qualifiaction.setStyle("color:orange; font:12px");
					qualifiaction.setValue("- - - - - - - - -");
				}
				if (!StringUtils.isEmpty(aCustomer.getLovDescCustProfessionName())) {
					profession.setValue(aCustomer.getLovDescCustProfessionName());
				} else {
					profession.setStyle("color:orange; font:12px");
					profession.setValue("- - - - - - - - -");
				}
				if (!StringUtils.isEmpty(aCustomer.getCkycOrRefNo())) {
					cKYCRef.setValue(aCustomer.getCkycOrRefNo());
				} else {
					cKYCRef.setStyle("color:orange; font:12px");
					cKYCRef.setValue("- - - - - - - - -");
				}
				labelNatureOfBusiness.setVisible(true);
				labelOtherCaste.setVisible(true);
				labelProfession.setVisible(true);
				labelQualifiaction.setVisible(true);
				labelOtherReligion.setVisible(true);
				labelResidentialStatus.setVisible(true);
				residentialStatus.setVisible(true);
				otherReligion.setVisible(true);
				otherCaste.setVisible(true);
				natureOfBusiness.setVisible(true);
				qualifiaction.setVisible(true);
				profession.setVisible(true);
				cKYCRef.setVisible(true);
				labelCKYCRef.setVisible(true);
			}

			getAddressDetails(aCustomerDetails.getAddressList());
			doFillCustomerPhoneNumberDetails(aCustomerDetails.getCustomerPhoneNumList());

			AMedia amedia = null;
			for (CustomerDocument customerDocument : aCustomerDetails.getCustomerDocumentsList()) {
				if (customerDocument.getCustDocCategory().equalsIgnoreCase(PennantConstants.DOC_TYPE_CODE_PHOTO)) {
					if (customerDocument.getCustDocImage() == null) {
						if (customerDocument.getDocRefId() != Long.MIN_VALUE) {
							customerDocument.setCustDocImage(dMSService.getById(customerDocument.getDocRefId()));
						}
					}
					amedia = new AMedia(customerDocument.getCustDocName(), null, null,
							customerDocument.getCustDocImage());
					BufferedImage img = ImageIO.read(new ByteArrayInputStream(customerDocument.getCustDocImage()));
					customerPic1.setContent(img);
					isCustPhotoAvail = true;
					break;
				}
			}

			if (isRetailCustomer) {
				shareHolder.setVisible(false);
				hbox_empDetails.setVisible(true);
				listBoxCustomerEmploymentDetail.setVisible(true);
				imgshareHolderDetails.setVisible(false);
				genderDesc.setValue(Labels.getLabel("label_CustomerDialog_CustGenderCode.value"));
			} else {
				shareHolder.setVisible(true);
				imgshareHolderDetails.setVisible(true);
				hbox_empDetails.setVisible(false);
				listBoxCustomerEmploymentDetail.setVisible(false);
				genderDesc.setValue(Labels.getLabel("label_CustomerDialog_CustTypeCode.value"));
			}
			doFillDocumentDetails(aCustomerDetails.getCustomerDocumentsList());
			doFillCustomerAddressDetails(aCustomerDetails.getAddressList());
			doFillCustomerEmploymentDetail(aCustomerDetails.getEmploymentDetailsList());
			doFillCustomerEmailDetails(aCustomerDetails.getCustomerEMailList());
			doFillCustomerIncome(aCustomerDetails.getCustomerIncomeList());
			doFillCustomerPhoneNumberDetails(aCustomerDetails.getCustomerPhoneNumList());

			doFillCustomerBankInfoDetails(aCustomerDetails.getCustomerBankInfoList());
			doFillCustomerChequeInfoDetails(aCustomerDetails.getCustomerChequeInfoList());
			doFillCustomerExtLiabilityDetails(aCustomerDetails.getCustomerExtLiabilityList());
			doFillCustFinanceExposureDetails(aCustomerDetails.getCustFinanceExposureList());
			doFillCustomerDirectory(aCustomerDetails.getCustomerDirectorList());
			doSetShareHoldersDesignationCode(aCustomerDetails.getCustomerDirectorList());
			appendExtendedFieldDetails(aCustomerDetails);
			doFillCustomerLoanDetails(aCustomerDetails.getFinanceMainList());
			doFillCustomerVASDetails(aCustomerDetails.getVasRecordingList());
			doFillCustomerCollateralDetails(aCustomerDetails.getCollateraldetailList());
			doFillCustomerloanApprovalDetails(aCustomerDetails.getCustomerFinanceDetailList());

		} else if (StringUtils.equals(PennantConstants.PFF_CUSTCTG_CORP, customerDetails.getCustomer().getCustCtgCode())
				|| StringUtils.equals(PennantConstants.PFF_CUSTCTG_SME,
						customerDetails.getCustomer().getCustCtgCode())) {
			Corporates.setVisible(true);
			retails.setVisible(false);
			custCIFF2.setValue(aCustomer.getCustCIF());
			custCoreBankk.setValue(aCustomer.getCustCoreBank());
			if (StringUtils.isEmpty(aCustomer.getCustCoreBank())) {
				custCoreBankk.setStyle("color:orange; font:12px");
				custCoreBankk.setValue("- - - - - - - - -");
			}
			custCRCPR.setValue(aCustomer.getCustCRCPR());
			if (StringUtils.isEmpty(aCustomer.getCustCRCPR())) {
				custCRCPR.setStyle("color:orange; font:12px");
				custCRCPR.setValue("- - - - - - - - -");
			}

			if (aCustomer.getCustCRCPR() != null) {
				i++;
			}
			custCtgCodee.setValue(aCustomer.getCustCtgCode());
			if (aCustomer.getCustSegment() != null) {
				i++;
			}
			custDftBranchh.setValue(aCustomer.getCustDftBranch());
			if (aCustomer.getCustDftBranch() != null) {
				i++;
			}
			if (!StringUtils.isEmpty(aCustomer.getApplicationNo())) {
				corp_applicationNo.setValue(aCustomer.getApplicationNo());
			} else {
				corp_applicationNo.setStyle("color:orange; font:12px");
				corp_applicationNo.setValue("- - - - - - - - -");
			}
			custDftBranchh.setValue(aCustomer.getCustDftBranch() + ", ");
			custDftBranchDesc.setValue(aCustomer.getLovDescCustDftBranchName());
			custDftBranchDescc2.setValue(aCustomer.getLovDescCustDftBranchName());
			custDftBranchDescc2.setStyle("color:orange;");
			custBaseCcyy.setValue(aCustomer.getCustBaseCcy() + ", ");
			custBaseCcyDescc.setValue(CurrencyUtil.getCcyDesc(aCustomer.getCustBaseCcy()));
			custBaseCcyDescc.setStyle("color:orange;");
			if (aCustomer.getCustBaseCcy() != null) {
				i++;
			}
			custTypeCodee.setValue(aCustomer.getCustTypeCode() + ", ");
			if (aCustomer.getCustSegment() != null) {
				i++;
			}
			custTypeCodeDescc.setValue(aCustomer.getLovDescCustTypeCodeName());
			custTypeCodeDescc.setStyle("color:orange;");
			if (aCustomer.getCustShrtName() != null) {
				i++;
			}
			custShrtName2.setValue(aCustomer.getCustShrtName());
			custArabicNamee.setValue(aCustomer.getCustShrtName());

			if (aCustomer.getCustShrtName() == null) {
				custArabicNamee.setStyle("color:orange; font:12px");
				custArabicNamee.setValue("- - - - - - - - -");
			}
			custNationalityy.setValue(aCustomer.getCustNationality() + ", ");
			custNationalitydescc.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustNationalityName()));
			custNationalitydescc.setStyle("color:orange;");
			custROO1.setValue(aCustomer.getCustRO1() + ", ");
			if (aCustomer.getCustRO1() != 0) {
				i++;
			}
			custRO1Descc.setValue(aCustomer.getLovDescCustRO1Name());
			custCOOB.setValue(aCustomer.getCustCOB() + ", ");
			custCOBDescc.setValue(aCustomer.getLovDescCustCOBName());
			custCOBDescc.setStyle("color:orange;");
			custSectorr.setValue(aCustomer.getCustSector() + ", ");
			if (aCustomer.getCustSector() != null) {
				i++;
			}
			custSectorDescc.setValue(aCustomer.getLovDescCustSectorName());
			custSectorDescc.setStyle("color:orange;");
			custIndustryDescc.setValue(aCustomer.getLovDescCustIndustryName());
			custIndustryDescc.setStyle("color:orange;");
			if (aCustomer.getLovDescCustIndustryName() != null) {
				i++;
			}
			custIndustryy.setValue(aCustomer.getCustIndustry());
			if (aCustomer.getCustGroupID() != 0) {
				custGroupIdd.setValue(String.valueOf(aCustomer.getCustGroupID()) + ", ");
				custGroupIdDescc.setValue(StringUtils.trimToEmpty(aCustomer.getLovDesccustGroupIDName()));
				custGroupIdDescc.setStyle("color:orange;");
			} else {
				custGroupIdd.setStyle("color:orange; font:12px");
				custGroupIdd.setValue("- - - - - - - - -");
			}
			custSubSectorr.setValue(aCustomer.getCustSubSector());
			if (StringUtils.isEmpty(aCustomer.getCustSubSector())) {
				custSubSectorr.setStyle("color:orange;");
				custSubSectorr.setValue("- - - - - - - - -");
			}
			custDOB.setValue(DateUtility.format(aCustomer.getCustDOB(), "dd/MM/yyyy"));

			if (aCustomer.getCustDOB() != null) {
				i++;
			}
			corpcustDOBB.setValue(DateUtility.format(aCustomer.getCustDOB(), "dd/MM/yyyy"));
			if (aCustomer.getCustDOB() != null) {
				i++;
			}
			if (aCustomer.getCustMotherMaiden() != null) {
				i++;
			}

			for (CustomerPhoneNumber customerPhoneNumber : aCustomerDetails.getCustomerPhoneNumList()) {

				custPhoneNumber.setValue(
						customerPhoneNumber.getPhoneNumber() == null ? "" : customerPhoneNumber.getPhoneNumber());

			}

			if (aCustomer.getCustCRCPR() != null) {
				i++;
			}
			custCRCPR2.setValue(aCustomer.getCustCRCPR());
			if (StringUtils.isEmpty(aCustomer.getCustCRCPR())) {
				custCRCPR2.setStyle("color:orange; font:12px");
				custCRCPR2.setValue("- - - - - - - - -");
			}
			custCRCPR3.setValue(aCustomer.getCustCRCPR());
			if (StringUtils.isEmpty(aCustomer.getCustCRCPR())) {
				custCRCPR3.setStyle("color:orange; font:12px");
				custCRCPR3.setValue("- - - - - - - - -");
			}
			if (aCustomer.getLovDescCustLngName() != null) {
				i++;
			}
			custLngg.setValue(aCustomer.getCustLng());
			if (aCustomer.getCustLng() != null) {
				i++;
			}
			corpcustLng.setValue(aCustomer.getCustLng() + ", ");
			corpcustLngDesc.setValue(aCustomer.getLovDescCustLngName());
			corpcustLngDesc.setStyle("color:orange;");
			custGenderCodeDesc.setValue(aCustomer.getLovDescCustTypeCodeName());
			custGenderCodeDesc.setStyle("padding-left:20px");
			if (aCustomer.getLovDescCustGenderCodeName() != null) {
				i++;
			}
			if (aCustomer.isDnd() == true) {
				corpDND.setSrc("images/icons/customerenquiry/activecheck.png");
			} else {
				corpDND.setSrc("images/icons/customerenquiry/inactivecheck.png");
			}
			if (pageExt != null) {
				entityType.setValue(aCustomer.getEntityType());
				entityType.setVisible(true);
				if (StringUtils.isEmpty(aCustomer.getEntityType())) {
					entityType.setStyle("color:orange; font:12px");
					entityType.setValue("- - - - - - - - -");
				}
			}
			getAddressDetails(aCustomerDetails.getAddressList());
			doFillCustomerPhoneNumberDetails(aCustomerDetails.getCustomerPhoneNumList());

			String s = StringUtils.isNotBlank(aCustomer.getRecordType()) ? " for " + aCustomer.getRecordType() : "";

			AMedia amedia = null;
			for (CustomerDocument customerDocument : aCustomerDetails.getCustomerDocumentsList()) {
				if (customerDocument.getCustDocCategory().equalsIgnoreCase(PennantConstants.DOC_TYPE_CODE_PHOTO)) {
					if (customerDocument.getCustDocImage() == null) {
						if (customerDocument.getDocRefId() != Long.MIN_VALUE) {
							customerDocument.setCustDocImage(dMSService.getById(customerDocument.getDocRefId()));
						}
					}
					amedia = new AMedia(customerDocument.getCustDocName(), null, null,
							customerDocument.getCustDocImage());
					BufferedImage img = ImageIO.read(new ByteArrayInputStream(customerDocument.getCustDocImage()));
					isCustPhotoAvail = true;
					break;
				}
			}

			if (isRetailCustomer) {
				shareHolder.setVisible(false);
				imgshareHolderDetails.setVisible(false);
				hbox_empDetails.setVisible(true);
				listBoxCustomerEmploymentDetail.setVisible(true);
				genderDesc.setValue(Labels.getLabel("label_CustomerDialog_CustGenderCode.value"));
			} else {
				shareHolder.setVisible(true);
				imgshareHolderDetails.setVisible(true);
				hbox_empDetails.setVisible(false);
				listBoxCustomerEmploymentDetail.setVisible(false);
				genderDesc.setValue(Labels.getLabel("label_CustomerDialog_CustTypeCode.value"));
			}

			doFillDocumentDetails(aCustomerDetails.getCustomerDocumentsList());
			doFillCustomerAddressDetails(aCustomerDetails.getAddressList());
			doFillCustomerEmploymentDetail(aCustomerDetails.getEmploymentDetailsList());
			doFillCustomerEmailDetails(aCustomerDetails.getCustomerEMailList());
			doFillCustomerIncome(aCustomerDetails.getCustomerIncomeList());
			doFillCustomerPhoneNumberDetails(aCustomerDetails.getCustomerPhoneNumList());

			doFillCustomerBankInfoDetails(aCustomerDetails.getCustomerBankInfoList());
			doFillCustomerChequeInfoDetails(aCustomerDetails.getCustomerChequeInfoList());
			doFillCustomerExtLiabilityDetails(aCustomerDetails.getCustomerExtLiabilityList());
			doFillCustFinanceExposureDetails(aCustomerDetails.getCustFinanceExposureList());
			doFillCustomerDirectory(aCustomerDetails.getCustomerDirectorList());
			doSetShareHoldersDesignationCode(aCustomerDetails.getCustomerDirectorList());
			doFillCustomerLoanDetails(aCustomerDetails.getFinanceMainList());
			doFillCustomerVASDetails(aCustomerDetails.getVasRecordingList());
			doFillCustomerCollateralDetails(aCustomerDetails.getCollateraldetailList());
			doFillCustomerloanApprovalDetails(aCustomerDetails.getCustomerFinanceDetailList());
			appendExtendedFieldDetails(aCustomerDetails);

		}
		progressPerc = ((i * 100) / 20);
		String popupMsg = String.valueOf(progressPerc).concat(Labels.getLabel("label_profilePercentage"));
		basicProgress.setValue((i * 100) / 20);
		basicProgress.setStyle("image-height: 5px;");
		basicProgress.setTooltiptext(popupMsg);

		// Display default image for the photo.

		if (!isCustPhotoAvail) {
			if (StringUtils.isEmpty(aCustomer.getLovDescCustGenderCodeName())
					|| "male".equalsIgnoreCase(aCustomer.getLovDescCustGenderCodeName())) {
				customerPic1.setSrc("images/icons/customerenquiry/male.png");
			} else {
				customerPic1.setSrc("images/icons/customerenquiry/female.png");
			}
		}
		//Piramal change to hide the employment details section based on system param
		String empSectionReq = SysParamUtil.getValueAsString(SMTParameterConstants.CUST_EMPLOYEEMENTDETAILS_REQUIRED);
		if (PennantConstants.NO.equals(empSectionReq)) {
			hbox_empDetails.setVisible(false);
			listBoxCustomerEmploymentDetail.setVisible(false);
		} else if (PennantConstants.YES.equals(empSectionReq)) {
			hbox_empDetails.setVisible(true);
			listBoxCustomerEmploymentDetail.setVisible(true);
		}
		doFillDownload(prepareList());
		logger.debug("Leaving");
	}

	public void doFillCustomerEmploymentDetail(List<CustomerEmploymentDetail> custEmploymentDetails) {
		logger.debug("Entering");
		if (custEmploymentDetails != null && !custEmploymentDetails.isEmpty()) {
			customerEmploymentDetailList = custEmploymentDetails;
			for (CustomerEmploymentDetail customerEmploymentDetail : custEmploymentDetails) {
				customerEmploymentDetail.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(customerEmploymentDetail.getLovDescCustCIF());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(customerEmploymentDetail.getLovDesccustEmpName());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(customerEmploymentDetail.getLovDescCustEmpDesgName());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(customerEmploymentDetail.getLovDescCustEmpDeptName());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(customerEmploymentDetail.getLovDescCustEmpTypeName());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);

				item.setAttribute("id", customerEmploymentDetail.getCustID());
				item.setAttribute("empName", customerEmploymentDetail.getCustEmpName());

				item.setAttribute("custEmpId", customerEmploymentDetail.getCustEmpId());
				item.setAttribute("data", customerEmploymentDetail);

				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerEmploymentDetailItemDoubleClicked");
				this.listBoxCustomerEmploymentDetail.appendChild(item);
			}
			customerEmploymentDetailList = custEmploymentDetails;
		}

	}

	private boolean isDeleteRecord(String rcdType) {
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, rcdType)
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, rcdType)) {
			return true;
		}
		return false;
	}

	public boolean getCurrentEmployerExist(CustomerEmploymentDetail custEmpDetail) {
		boolean isCurrentEmp = false;
		for (CustomerEmploymentDetail customerEmploymentDetail : customerEmploymentDetailList) {
			if (customerEmploymentDetail.getCustEmpName() != custEmpDetail.getCustEmpName()
					&& customerEmploymentDetail.isCurrentEmployer()) {
				isCurrentEmp = true;
				return isCurrentEmp;
			}
		}
		return isCurrentEmp;
	}

	public void onCustomerEmploymentDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerEmploymentDetail.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerEmploymentDetail customerEmploymentDetail = (CustomerEmploymentDetail) item
					.getAttribute("data");
			if (isDeleteRecord(customerEmploymentDetail.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("customerEmploymentDetail", customerEmploymentDetail);
				map.put("customerViewDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("moduleType", this.moduleType);
				map.put("currentEmployer", getCurrentEmployerExist(customerEmploymentDetail));
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/CustomerEmploymentDetail/CustomerEmploymentDetailDialog.zul",
							window_CustomerDialogg, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onCustomerDocumentItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerDocuments.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerDocument customerDocument = (CustomerDocument) item.getAttribute("data");
			if (isDeleteRecord(customerDocument.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				if (customerDocument.getCustDocImage() == null) {
					if (customerDocument.getDocRefId() != Long.MIN_VALUE) {
						customerDocument.setCustDocImage(dMSService.getById(customerDocument.getDocRefId()));
					} /*
						 * else if (StringUtils.isNotBlank(customerDocument.getDocUri())) { try { // Fetch document from
						 * interface String custCif = this.custCIF2.getValue(); DocumentDetails detail =
						 * externalDocumentManager.getExternalDocument( customerDocument.getCustDocName(),
						 * customerDocument.getDocUri(), custCif); if (detail != null && detail.getDocImage() != null) {
						 * customerDocument.setCustDocImage(detail.getDocImage());
						 * customerDocument.setCustDocName(detail.getDocName()); } } catch (InterfaceException e) {
						 * MessageUtil.showError(e); } }
						 */
				}
				customerDocument.setLovDescCustCIF(this.custCIF2.getValue());
				map.put("customerDocument", customerDocument);
				map.put("customerViewDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("moduleType", this.moduleType);
				map.put("isRetailCustomer", isRetailCustomer);
				if (getFinanceMainDialogCtrl() != null) {
					map.put("financeMainDialogCtrl", getFinanceMainDialogCtrl());
				}
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul", null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onCustomerAddressItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerAddress.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerAddres customerAddress = (CustomerAddres) item.getAttribute("data");
			if (isDeleteRecord(customerAddress.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				customerAddress.setLovDescCustCIF(this.custCIF2.getValue());
				map.put("customerAddres", customerAddress);
				map.put("customerViewDialogCtrl", this);
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/CustomerAddres/CustomerAddresDialog.zul", null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onCustomerPhoneNumberItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerPhoneNumbers.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerPhoneNumber customerPhoneNumber = (CustomerPhoneNumber) item.getAttribute("data");
			if (isDeleteRecord(customerPhoneNumber.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				customerPhoneNumber.setLovDescCustCIF(this.custCIF2.getValue());
				map.put("customerPhoneNumber", customerPhoneNumber);
				map.put("customerViewDialogCtrl", this);
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/CustomerPhoneNumber/CustomerPhoneNumberDialog.zul", null,
							map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onCustomerEmailAddressItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerEmails.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerEMail customerEmail = (CustomerEMail) item.getAttribute("data");
			if (isDeleteRecord(customerEmail.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				customerEmail.setLovDescCustCIF(this.custCIF2.getValue());
				map.put("customerEMail", customerEmail);
				map.put("customerViewDialogCtrl", this);
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerEMail/CustomerEMailDialog.zul",
							null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onCustomerBankInfoItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerBankInformation.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerBankInfo custBankInfo = (CustomerBankInfo) item.getAttribute("data");
			if (isDeleteRecord(custBankInfo.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				custBankInfo.setLovDescCustCIF(this.custCIF2.getValue());
				map.put("customerBankInfo", custBankInfo);
				map.put("customerViewDialogCtrl", this);
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
				map.put("CustomerBankInfoList", CustomerBankInfoList);
				map.put("customer360", true);
				map.put("retailCustomer",
						StringUtils.equals(this.custCtgCode.getValue(), PennantConstants.PFF_CUSTCTG_INDIV));
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerBankInfoDialog.zul",
							null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onCustomerChequeInfoItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerChequeInformation.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerChequeInfo custChequeInfo = (CustomerChequeInfo) item.getAttribute("data");
			if (isDeleteRecord(custChequeInfo.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				custChequeInfo.setLovDescCustCIF(this.custCIF2.getValue());
				map.put("customerChequeInfo", custChequeInfo);
				map.put("finFormatter", ccyFormatter);
				map.put("customerViewDialogCtrl", this);
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerChequeInfoDialog.zul",
							null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onCustomerExtLiabilityItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerExternalLiability.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerExtLiability externalLiability = (CustomerExtLiability) item.getAttribute("data");
			if (isDeleteRecord(externalLiability.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				externalLiability.setCustCif(this.custCIF2.getValue());
				map.put("externalLiability", externalLiability);
				map.put("finFormatter", ccyFormatter);
				map.put("customerViewDialogCtrl", this);
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
				map.put("customer360", true);
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/Customer/CustomerExtLiabilityDialog.zul", null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onCustomerIncomeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerIncome.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerIncome customerIncome = (CustomerIncome) item.getAttribute("data");
			if (isDeleteRecord(customerIncome.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("customerIncome", customerIncome);
				map.put("customerViewDialogCtrl", this);
				map.put("ccyFormatter", ccyFormatter);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/CustomerIncome/CustomerIncomeDialog.zul", null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onDirectorDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerDirectory.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final DirectorDetail directorDetail = (DirectorDetail) item.getAttribute("data");
			if (isDeleteRecord(directorDetail.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("directorDetail", directorDetail);
				map.put("customerViewDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
				BigDecimal totSharePerc = BigDecimal.ZERO;
				if (directorDetail.getSharePerc() != null) {
					totSharePerc = getTotSharePerc().subtract(directorDetail.getSharePerc());
				}
				map.put("totSharePerc", totSharePerc);
				if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
					map.put("isEnquiry", true);
				}
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/DirectorDetail/DirectorDetailDialog.zul", null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	public BigDecimal getTotSharePerc() {
		BigDecimal totSharePerc = BigDecimal.ZERO;
		for (DirectorDetail directorDetail : getDirectorList()) {
			if (directorDetail.getSharePerc() != null) {
				totSharePerc = totSharePerc.add(directorDetail.getSharePerc());
			}
		}
		return totSharePerc;
	}

	public void doFillCustomerIncome(List<CustomerIncome> incomes) {
		logger.debug("Entering");
		setIncomeList(incomes);
		createIncomeGroupList(incomes);
		logger.debug("Leaving");
	}

	private void createIncomeGroupList(List<CustomerIncome> incomes) {
		if (incomes != null && !incomes.isEmpty()) {
			BigDecimal totIncome = BigDecimal.ZERO;
			BigDecimal totExpense = BigDecimal.ZERO;
			Map<String, List<CustomerIncome>> incomeMap = new HashMap<String, List<CustomerIncome>>();
			Map<String, List<CustomerIncome>> expenseMap = new HashMap<String, List<CustomerIncome>>();
			for (CustomerIncome customerIncome : incomes) {
				String category = StringUtils.trimToEmpty(customerIncome.getCategory());
				if (customerIncome.getIncomeExpense().equals(PennantConstants.INCOME)) {
					totIncome = totIncome.add(customerIncome.getIncome());
					if (incomeMap.containsKey(category)) {
						incomeMap.get(category).add(customerIncome);
					} else {
						ArrayList<CustomerIncome> list = new ArrayList<CustomerIncome>();
						list.add(customerIncome);
						incomeMap.put(category, list);
					}
				} else {
					totExpense = totExpense.add(customerIncome.getIncome());
					if (expenseMap.containsKey(category)) {
						expenseMap.get(category).add(customerIncome);
					} else {
						ArrayList<CustomerIncome> list = new ArrayList<CustomerIncome>();
						list.add(customerIncome);
						expenseMap.put(category, list);
					}
				}
			}
			renderIncomeExpense(incomeMap, totIncome, expenseMap, totExpense, ccyFormatter);
		}
	}

	private void renderIncomeExpense(Map<String, List<CustomerIncome>> incomeMap, BigDecimal totIncome,
			Map<String, List<CustomerIncome>> expenseMap, BigDecimal totExpense, int ccyFormatter) {
		this.listBoxCustomerIncome.getItems().clear();
		Listitem item;
		Listcell cell;
		Listgroup group;
		Checkbox cb;
		if (incomeMap != null) {
			for (String category : incomeMap.keySet()) {
				List<CustomerIncome> list = incomeMap.get(category);
				if (list != null && list.size() > 0) {
					group = new Listgroup();
					cell = new Listcell(list.get(0).getIncomeExpense() + "-" + list.get(0).getCategoryDesc());
					cell.setStyle("font-size: 14px;");
					cell.setParent(group);
					this.listBoxCustomerIncome.appendChild(group);
					BigDecimal total = BigDecimal.ZERO;
					for (CustomerIncome customerIncome : list) {
						item = new Listitem();
						cell = new Listcell("");
						cell.setParent(item);
						cell = new Listcell(customerIncome.getIncomeTypeDesc());
						cell.setStyle("font-size:14px;font-weight: normal;");
						cell.setParent(item);
						total = total.add(customerIncome.getIncome());
						cell = new Listcell(
								PennantApplicationUtil.amountFormate(customerIncome.getIncome(), ccyFormatter));
						cell.setStyle("text-align:right; font-size: 14px;");
						cell.setParent(item);
						cell = new Listcell();
						cb = new Checkbox();
						cb.setDisabled(true);
						cb.setChecked(customerIncome.isJointCust());
						cb.setParent(cell);
						cell.setParent(item);

						item.setAttribute("data", customerIncome);
						ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerIncomeItemDoubleClicked");
						this.listBoxCustomerIncome.appendChild(item);
					}
					item = new Listitem();
					cell = new Listcell("Total");
					cell.setStyle("cursor:default;font-size:14px;font-weight: normal;;");
					cell.setParent(item);
					cell = new Listcell(PennantApplicationUtil.amountFormate(total, ccyFormatter));
					cell.setSpan(2);
					cell.setStyle("font-size:14px;font-weight: normal;; text-align:right;cursor:default");
					cell.setParent(item);
					cell = new Listcell();
					cell.setSpan(3);
					cell.setStyle("cursor:default: font-size:14px;font-weight: normal;;");
					cell.setParent(item);
					this.listBoxCustomerIncome.appendChild(item);
				}
			}
			item = new Listitem();
			cell = new Listcell("Gross Income");
			cell.setStyle("cursor:default; font-size:14px;font-weight: normal;;");
			cell.setParent(item);
			cell = new Listcell(PennantApplicationUtil.amountFormate(totIncome, ccyFormatter));
			cell.setSpan(2);
			cell.setStyle("font-size:14px;font-weight: normal;; text-align:right;cursor:default");
			cell.setParent(item);
			cell = new Listcell();
			cell.setSpan(3);
			cell.setStyle("cursor:default; font-size:14px;font-weight: normal;;");
			cell.setParent(item);
			this.listBoxCustomerIncome.appendChild(item);
		}
		if (expenseMap != null) {
			for (String category : expenseMap.keySet()) {
				List<CustomerIncome> list = expenseMap.get(category);
				if (list != null) {
					group = new Listgroup();
					group.setHeight("50px");
					cell = new Listcell(list.get(0).getIncomeExpense() + "-" + list.get(0).getCategoryDesc());
					cell.setParent(group);
					this.listBoxCustomerIncome.appendChild(group);
					BigDecimal total = BigDecimal.ZERO;
					for (CustomerIncome customerIncome : list) {
						item = new Listitem();
						cell = new Listcell("");
						cell.setParent(item);
						cell = new Listcell(customerIncome.getIncomeTypeDesc());
						cell.setStyle("font-size:14px;font-weight: normal;;");
						cell.setParent(item);
						total = total.add(customerIncome.getIncome());
						cell = new Listcell(
								PennantApplicationUtil.amountFormate(customerIncome.getIncome(), ccyFormatter));
						cell.setStyle("text-align:right; font-size:14px;font-weight: normal;;");
						cell.setParent(item);
						cell = new Listcell();
						cb = new Checkbox();
						cb.setDisabled(true);
						cb.setChecked(customerIncome.isJointCust());
						cb.setParent(cell);
						cell.setParent(item);

						item.setAttribute("data", customerIncome);
						ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerIncomeItemDoubleClicked");
						this.listBoxCustomerIncome.appendChild(item);
					}
					item = new Listitem();
					cell = new Listcell("Total");
					cell.setStyle("cursor:default; font-size:14px;font-weight: normal;;");
					cell.setParent(item);
					cell = new Listcell(PennantApplicationUtil.amountFormate(total, ccyFormatter));
					cell.setSpan(2);
					cell.setStyle("font-size:14px;font-weight: normal;;text-align:right;cursor:default");
					cell.setParent(item);
					cell = new Listcell();
					cell.setSpan(2);
					cell.setParent(item);
					cell = new Listcell();
					cell.setStyle("cursor:default");
					cell.setParent(item);
					this.listBoxCustomerIncome.appendChild(item);
				}
			}
			item = new Listitem();
			cell = new Listcell("Gross Expense");
			cell.setStyle("cursor:default; font-size:14px;font-weight: normal;;");
			cell.setParent(item);
			cell = new Listcell(PennantApplicationUtil.amountFormate(totExpense, ccyFormatter));
			cell.setSpan(2);
			cell.setStyle("text-align:right;cursor:default;  font-size:14px;font-weight: normal;;");
			cell.setParent(item);
			cell = new Listcell();
			cell.setSpan(3);
			cell.setStyle("cursor:default");
			cell.setParent(item);
			this.listBoxCustomerIncome.appendChild(item);
		}
		item = new Listitem();
		cell = new Listcell("Net Income");
		cell.setStyle("font-size:14px;font-weight: normal;;");
		cell.setParent(item);
		cell = new Listcell(PennantApplicationUtil.amountFormate(totIncome.subtract(totExpense), ccyFormatter));
		cell.setSpan(2);
		cell.setStyle("text-align:right; font-size:14px;font-weight: normal;;");
		cell.setParent(item);
		cell = new Listcell();
		cell.setSpan(3);
		cell.setStyle("cursor:default");
		cell.setParent(item);
		this.listBoxCustomerIncome.appendChild(item);

	}

	public void doFillDocumentDetails(List<CustomerDocument> custDocumentDetails) {
		logger.debug("Entering");
		listBoxCustomerDocuments.getItems().clear();
		if (custDocumentDetails != null) {
			if (!custDocumentDetails.isEmpty()) {
				List<ValueLabel> docTypeList = null;
				for (CustomerDocument customerDocument : custDocumentDetails) {
					Listitem item = new Listitem();
					Listcell lc;
					if (StringUtils.equals(customerDocument.getCustDocCategory(),
							customerDocument.getLovDescCustDocCategory())) {
						if (docTypeList == null) {
							docTypeList = PennantAppUtil.getCustomerDocumentTypesList();
						}
						String desc = PennantApplicationUtil.getLabelDesc(customerDocument.getCustDocCategory(),
								docTypeList);
						customerDocument.setLovDescCustDocCategory(desc);
						lc = new Listcell(desc);
					} else {
						lc = new Listcell(customerDocument.getLovDescCustDocCategory());
					}
					lc.setStyle("font-size: 14px");
					lc.setParent(item);
					if (StringUtils.equals(customerDocument.getCustDocCategory(), PennantConstants.CPRCODE)) {
						lc = new Listcell(PennantApplicationUtil.formatEIDNumber(customerDocument.getCustDocTitle()));
					} else {
						lc = new Listcell(customerDocument.getCustDocTitle());
					}
					lc.setStyle("font-size: 14px");
					lc.setParent(item);
					lc = new Listcell(customerDocument.getLovDescCustDocIssuedCountry());
					lc.setStyle("font-size: 14px");
					lc.setParent(item);
					lc = new Listcell(customerDocument.getCustDocSysName());
					lc.setStyle("font-size: 14px");
					lc.setParent(item);
					lc = new Listcell(DateUtility.formatToLongDate(customerDocument.getCustDocIssuedOn()));
					lc.setStyle("font-size: 14px");
					lc.setParent(item);
					lc = new Listcell(DateUtility.formatToLongDate(customerDocument.getCustDocExpDate()));
					lc.setStyle("font-size: 14px");
					lc.setParent(item);
					item.setAttribute("data", customerDocument);
					ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerDocumentItemDoubleClicked");
					listBoxCustomerDocuments.appendChild(item);
				}
			}
			customerDocumentDetailList = custDocumentDetails;
		}
		logger.debug("Leaving");
	}

	public void getAddressDetails(List<CustomerAddres> customerAddresDetails) {
		logger.debug("Entering");
		if (customerAddresDetails != null && !customerAddresDetails.isEmpty()) {
			for (CustomerAddres customerAddress : customerAddresDetails) {
				if (customerAddress.getCustAddrPriority() == 5) {
					// this.address1.setValue(customerAddress.getLovDescCustAddrCityName());
				}
			}
		}
		customerAddressDetailList = customerAddresDetails;
	}

	public void doFillCustomerAddressDetails(List<CustomerAddres> customerAddresDetails) {
		logger.debug("Entering");
		this.listBoxCustomerAddress.getItems().clear();
		if (customerAddresDetails != null) {
			for (CustomerAddres customerAddress : customerAddresDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(customerAddress.getLovDescCustAddrTypeName());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				if (PennantConstants.CITY_FREETEXT) {
					lc = new Listcell(customerAddress.getCustAddrCity());
					lc.setStyle("font-size:14px;font-weight: normal;");
					lc.setParent(item);
				} else {
					lc = new Listcell(customerAddress.getLovDescCustAddrCityName());
					lc.setStyle("font-size:14px;font-weight: normal;");
					lc.setParent(item);
				}
				item.setAttribute("data", customerAddress);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerAddressItemDoubleClicked");
				this.listBoxCustomerAddress.appendChild(item);

			}
			customerAddressDetailList = customerAddresDetails;
		}
		logger.debug("Leaving");
	}

	public void doFillCustomerEmailDetails(List<CustomerEMail> customerEmailDetails) {
		logger.debug("Entering");
		this.listBoxCustomerEmails.getItems().clear();
		if (customerEmailDetails != null) {
			for (CustomerEMail customerEMail : customerEmailDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(customerEMail.getLovDescCustEMailTypeCode());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(customerEMail.getCustEMail());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);

				item.setAttribute("data", customerEMail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerEmailAddressItemDoubleClicked");
				this.listBoxCustomerEmails.appendChild(item);
			}
			customerEmailDetailList = customerEmailDetails;
		}
		logger.debug("Leaving");
	}

	public void doFillCustomerPhoneNumberDetails(List<CustomerPhoneNumber> customerPhoneNumDetails) {
		logger.debug("Entering");
		this.listBoxCustomerPhoneNumbers.getItems().clear();
		if (customerPhoneNumDetails != null) {
			for (CustomerPhoneNumber customerPhoneNumber : customerPhoneNumDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(StringUtils.trimToEmpty(customerPhoneNumber.getPhoneTypeCode()));
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(customerPhoneNumber.getPhoneNumber());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);

				item.setAttribute("data", customerPhoneNumber);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerPhoneNumberItemDoubleClicked");
				this.listBoxCustomerPhoneNumbers.appendChild(item);
			}
			customerPhoneNumberDetailList = customerPhoneNumDetails;
		}
		logger.debug("Leaving");
	}

	public void doFillCustomerBankInfoDetails(List<CustomerBankInfo> customerBankInfoDetails) {
		logger.debug("Entering");
		CustomerBankInfoList = customerBankInfoDetails;
		this.listBoxCustomerBankInformation.getItems().clear();
		if (customerBankInfoDetails != null) {
			for (CustomerBankInfo custBankInfo : customerBankInfoDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(custBankInfo.getLovDescBankName());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(custBankInfo.getAccountNumber());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(custBankInfo.getLovDescAccountType());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);

				item.setAttribute("data", custBankInfo);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerBankInfoItemDoubleClicked");
				this.listBoxCustomerBankInformation.appendChild(item);
			}
			customerBankInfoDetailList = customerBankInfoDetails;
		}
		logger.debug("Leaving");
	}

	public void doFillCustomerChequeInfoDetails(List<CustomerChequeInfo> customerChequeInfoDetails) {
		logger.debug("Entering");
		this.listBoxCustomerChequeInformation.getItems().clear();
		if (customerChequeInfoDetails != null) {
			for (CustomerChequeInfo custChequeInfo : customerChequeInfoDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(DateUtility.format(custChequeInfo.getMonthYear(), PennantConstants.monthYearFormat));
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(
						PennantApplicationUtil.amountFormate(custChequeInfo.getTotChequePayment(), ccyFormatter));
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(custChequeInfo.getSalary(), ccyFormatter));
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(
						PennantApplicationUtil.amountFormate(custChequeInfo.getReturnChequeAmt(), ccyFormatter));
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(String.valueOf(custChequeInfo.getReturnChequeCount()));
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);

				item.setAttribute("data", custChequeInfo);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerChequeInfoItemDoubleClicked");
				this.listBoxCustomerChequeInformation.appendChild(item);

			}
			customerChequeInfoDetailList = customerChequeInfoDetails;
		}
		logger.debug("Leaving");
	}

	public void doFillCustomerExtLiabilityDetails(List<CustomerExtLiability> customerExtLiabilityDetails) {
		logger.debug("Entering");
		this.listBoxCustomerExternalLiability.getItems().clear();

		BigDecimal originalAmount = BigDecimal.ZERO;
		BigDecimal instalmentAmount = BigDecimal.ZERO;
		BigDecimal outStandingBal = BigDecimal.ZERO;

		if (customerExtLiabilityDetails != null) {
			for (CustomerExtLiability custExtLiability : customerExtLiabilityDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				if (custExtLiability.getFinDate() == null) {
					lc = new Listcell();
					lc.setStyle("font-size: 14px");
				} else {
					lc = new Listcell(DateUtility.formatToLongDate(custExtLiability.getFinDate()));
					lc.setStyle("font-size: 14px");
				}
				lc.setParent(item);
				lc = new Listcell(custExtLiability.getFinTypeDesc());
				lc.setStyle("font-size: 14px");
				lc.setParent(item);
				lc = new Listcell(custExtLiability.getLoanBankName());
				lc.setStyle("font-size: 14px");
				lc.setParent(item);
				originalAmount = originalAmount.add(custExtLiability.getOriginalAmount() == null ? BigDecimal.ZERO
						: custExtLiability.getOriginalAmount());
				lc = new Listcell(
						PennantApplicationUtil.amountFormate(custExtLiability.getOriginalAmount(), ccyFormatter));
				lc.setStyle("font-size: 14px; text-align:left;");
				lc.setParent(item);
				instalmentAmount = instalmentAmount.add(custExtLiability.getInstalmentAmount() == null ? BigDecimal.ZERO
						: custExtLiability.getInstalmentAmount());
				lc = new Listcell(
						PennantApplicationUtil.amountFormate(custExtLiability.getInstalmentAmount(), ccyFormatter));
				lc.setStyle("font-size: 14px; text-align:left;");
				lc.setParent(item);
				outStandingBal = outStandingBal.add(custExtLiability.getOutstandingBalance() == null ? BigDecimal.ZERO
						: custExtLiability.getOutstandingBalance());
				lc = new Listcell(
						PennantApplicationUtil.amountFormate(custExtLiability.getOutstandingBalance(), ccyFormatter));
				lc.setStyle("font-size: 14px; text-align:left;");
				lc.setParent(item);
				lc = new Listcell(custExtLiability.getCustStatusDesc());
				lc.setStyle("font-size: 14px");
				lc.setParent(item);

				item.setAttribute("data", custExtLiability);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerExtLiabilityItemDoubleClicked");
				this.listBoxCustomerExternalLiability.appendChild(item);

			}
			// add summary list item
			customerExtLiabilityDetailList = customerExtLiabilityDetails;
		}
		logger.debug("Leaving");
	}

	public void doFillCustFinanceExposureDetails(List<FinanceEnquiry> custFinanceExposureDetails) {
		logger.debug("Entering");
		this.listBoxCustomerFinExposure.getItems().clear();
		if (custFinanceExposureDetails != null) {
			for (FinanceEnquiry finEnquiry : custFinanceExposureDetails) {

				int format = CurrencyUtil.getFormat(finEnquiry.getFinCcy());
				Listitem item = new Listitem();
				Listcell lc = new Listcell(DateUtility.formatToLongDate(finEnquiry.getFinStartDate()));
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getLovDescFinTypeName());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinReference());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);

				BigDecimal totAmt = finEnquiry.getFinCurrAssetValue()
						.add(finEnquiry.getFeeChargeAmt().add(finEnquiry.getInsuranceAmt()));
				lc = new Listcell(PennantApplicationUtil.amountFormate(totAmt, format));
				lc.setStyle("font-size:14px;font-weight: normal;; text-align:left;");
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(finEnquiry.getMaxInstAmount(), format));
				lc.setStyle("font-size:14px;font-weight: normal;; text-align:left;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil
						.amountFormate(totAmt.subtract(finEnquiry.getFinRepaymentAmount()), format));
				lc.setStyle("font-size:14px;font-weight: normal;; text-align:left;");
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinStatus());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				this.listBoxCustomerFinExposure.appendChild(item);

			}
		}

		logger.debug("Leaving");
	}

	public void doFillCustomerDirectory(List<DirectorDetail> customerDirectory) {
		logger.debug("Entering");

		this.listBoxCustomerDirectory.getItems().clear();
		if (customerDirectory != null && customerDirectory.size() > 0) {
			directorList = customerDirectory;
			for (DirectorDetail directorDetail : customerDirectory) {
				Listitem item = new Listitem();
				if (item instanceof Listgroup) {
					item.appendChild(new Listcell(String.valueOf(directorDetail.getLovDescCustCIF())));
				} else if (item instanceof Listgroupfoot) {
					Listcell cell = new Listcell("");
					cell.setSpan(6);
					item.appendChild(cell);
				} else {
					String name = "";
					if (StringUtils.isNotBlank(directorDetail.getShortName())) {
						name = directorDetail.getShortName();
					} else if (StringUtils.isNotBlank(directorDetail.getFirstName())
							|| StringUtils.isNotBlank(directorDetail.getLastName())) {
						name = (directorDetail.getFirstName() == null ? " " : directorDetail.getFirstName()) + "  "
								+ (directorDetail.getLastName() == null ? " " : directorDetail.getLastName());
					}
					if (StringUtils.trimToEmpty(directorDetail.getCustAddrCountry())
							.equals(StringUtils.trimToEmpty(directorDetail.getLovDescCustAddrCountryName()))) {
						String desc = PennantApplicationUtil.getLabelDesc(directorDetail.getCustAddrCountry(),
								countryList);
						directorDetail.setLovDescCustAddrCountryName(desc);
					}
					Listcell lc = new Listcell(name);
					lc.setStyle("font-size: 14px");
					lc.setParent(item);
					if (StringUtils.isNotBlank(directorDetail.getLovDescCustAddrCountryName())) {
						lc = new Listcell(directorDetail.getCustAddrCountry() + " - "
								+ directorDetail.getLovDescCustAddrCountryName());
					} else {
						lc = new Listcell(directorDetail.getCustAddrCountry());
					}
					lc.setStyle("font-size: 14px");
					lc.setParent(item);
					if (directorDetail.getSharePerc() != null) {
						lc = new Listcell(String.valueOf(directorDetail.getSharePerc().doubleValue()));
						lc.setStyle("font-size: 14px");
						lc.setParent(item);
					}

					if (StringUtils.isNotBlank(directorDetail.getLovDescCustDocCategoryName())) {
						lc = new Listcell(
								directorDetail.getIdType() + " - " + directorDetail.getLovDescCustDocCategoryName());
					} else {
						lc = new Listcell(directorDetail.getIdType());
					}
					lc.setStyle("font-size: 14px");
					lc.setParent(item);
					lc = new Listcell(directorDetail.getIdReference());
					lc.setStyle("font-size: 14px");
					lc.setParent(item);
					if (StringUtils.isNotBlank(directorDetail.getLovDescNationalityName())) {
						lc = new Listcell(
								directorDetail.getNationality() + " - " + directorDetail.getLovDescNationalityName());
					} else {
						lc = new Listcell(directorDetail.getNationality());
					}
					lc.setStyle("font-size: 14px");
					lc.setParent(item);

					item.setAttribute("directorId", directorDetail.getDirectorId());
					item.setAttribute("custID", directorDetail.getCustID());
					item.setAttribute("data", directorDetail);
					ComponentsCtrl.applyForward(item, "onDoubleClick=onDirectorDetailItemDoubleClicked");
				}
				this.listBoxCustomerDirectory.appendChild(item);
			}
		}

		logger.debug("Leaving");

	}

	public List<DirectorDetail> getDirectorList() {
		return directorList;
	}

	public void setDirectorList(List<DirectorDetail> directorList) {
		this.directorList = directorList;
	}

	public void doSetShareHoldersDesignationCode(List<DirectorDetail> directorList) {
		logger.debug("Entering");
		if (directorList != null) {
			for (DirectorDetail directorDetail : directorList) {
				if (StringUtils.isBlank(directorDetail.getDesignation())
						&& StringUtils.isNotBlank(directorDetail.getLovDescDesignationName())) {
					Designation designation = PennantAppUtil
							.getDesignationDetails(directorDetail.getLovDescDesignationName());
					if (designation != null) {
						directorDetail.setDesignation(designation.getDesgCode());
					}
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setDirectorDetailService(DirectorDetailService directorDetailService) {
		this.directorDetailService = directorDetailService;
	}

	public List<CustomerIncome> getIncomeList() {
		return incomeList;
	}

	public void setIncomeList(List<CustomerIncome> incomeList) {
		this.incomeList = incomeList;
	}

	public CustomerDetails getCustomerDetails() {
		return customerDetails;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	/* ADDITIONAL DETAILS */

	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug(Literal.ENTERING);
		String tabName = "";
		if (StringUtils.equals(AssetConstants.UNIQUE_ID_JOINTGUARANTOR, moduleID)) {
			tabName = Labels.getLabel("tab_Co-borrower&Gurantors");
		} else if (StringUtils.equals(AssetConstants.UNIQUE_ID_ADDITIONALFIELDS, moduleID)) {
			tabName = getCustomerDetails().getExtendedFieldHeader().getTabHeading();
		} else {
			tabName = Labels.getLabel("tab_label_" + moduleID);
		}
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanels);
		tabpanel.setHeight("100%");
		logger.debug(Literal.LEAVING);
	}

	private void appendExtendedFieldDetails(CustomerDetails aCustomerDetails) {
		logger.debug("Entering");

		try {
			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = extendedFieldCtrl.getExtendedFieldHeader(
					ExtendedFieldConstants.MODULE_CUSTOMER, aCustomerDetails.getCustomer().getCustCtgCode());

			if (extendedFieldHeader == null) {
				return;
			}

			ExtendedFieldRender extendedFieldRender = extendedFieldCtrl
					.getExtendedFieldRender(aCustomerDetails.getCustomer().getCustCIF());

			this.extendedFieldCtrl.createTab(tabsIndexCenter, tabpanels, "20px");
			Tab tab = getTab("Tab" + extendedFieldHeader.getModuleName() + extendedFieldHeader.getSubModuleName());
			tab.setStyle(
					"padding-left:5px;padding-right:5px; border-radius: 7px;box-shadow: 1px 1px 1px 1px #e6e6e6;font-family : Verdana; font-size:16px; font-weight :Bold; color: #003d66;");
			tab.setWidth("2.5%");
			tab.setHeight("50px");
			aCustomerDetails.setExtendedFieldHeader(extendedFieldHeader);
			aCustomerDetails.setExtendedFieldRender(extendedFieldRender);

			if (aCustomerDetails.getBefImage() != null) {
				aCustomerDetails.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				aCustomerDetails.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}
			extendedFieldCtrl.setCcyFormat(2);
			extendedFieldCtrl.setReadOnly(true);
			extendedFieldCtrl.setWindow(this.window_CustomerDialogg);
			extendedFieldCtrl.setTabHeight(borderLayoutHeight - 90);
			// for getting rights in ExtendeFieldGenerator these two fields required.
			extendedFieldCtrl.setUserWorkspace(getUserWorkspace());
			extendedFieldCtrl.setUserRole(getRole());

			extendedFieldCtrl.render();
		} catch (Exception e) {
			logger.error("Exception", e);

		}

		logger.debug("Leaving");
	}

	public void doFillCustomerLoanDetails(List<FinanceMain> customerLoanDetails) {
		logger.debug("Entering");
		this.listBoxCustomerLoanDetails.getItems().clear();
		if (customerLoanDetails != null) {
			for (FinanceMain financeMain : customerLoanDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(financeMain.getFinReference());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(financeMain.getFinType());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				if (financeMain.getProductCategory() == null || financeMain.getProductCategory().isEmpty()) {
					lc = new Listcell("------------");
					lc.setStyle("color: #f39a36; font-size: 14px;");
				} else {
					lc = new Listcell(financeMain.getProductCategory().toString());
					lc.setStyle("font-size: 14px");
				}
				lc.setParent(item);
				lc = new Listcell(financeMain.getFinCcy());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(DateUtility.formatToLongDate(financeMain.getMaturityDate()));
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				if (financeMain.getFinBranch() == null || financeMain.getFinBranch().isEmpty()) {
					lc = new Listcell("------------");
					lc.setStyle("color: #f39a36; font-size: 14px;");
				} else {
					lc = new Listcell(financeMain.getFinBranch().toString());
					lc.setStyle("font-size: 14px");
				}
				lc.setParent(item);
				if (financeMain.getFinAmount() == null || financeMain.getFinAmount() == BigDecimal.ZERO) {
					lc = new Listcell("------------");
					lc.setStyle("color: #f39a36; font-size: 14px;");
				} else {
					lc = new Listcell(PennantApplicationUtil.amountFormate(financeMain.getFinAmount(), ccyFormatter));
					lc.setStyle("font-size: 14px");
				}
				lc.setParent(item);
				String closingStatus = "";
				FinanceEnquiry financeEnquiryDetails = null;
				List<FinanceEnquiry> customerFinances = customerDetails.getCustomerFinances();
				if (customerFinances != null) {
					for (FinanceEnquiry financeEnquiry : customerFinances) {
						if (StringUtils.equals(financeEnquiry.getFinReference(), financeMain.getFinReference())) {
							item.setAttribute("financeEnquiry", financeEnquiry);
							financeEnquiryDetails = financeEnquiry;
							if (FinanceConstants.CLOSE_STATUS_MATURED.equals(financeEnquiry.getClosingStatus())) {
								closingStatus = "Normal";
							} else if (FinanceConstants.CLOSE_STATUS_CANCELLED
									.equals(financeEnquiry.getClosingStatus())) {
								closingStatus = "Cancelled";
							} else if (FinanceConstants.CLOSE_STATUS_WRITEOFF
									.equals(financeEnquiry.getClosingStatus())) {
								closingStatus = "Written-Off";
							} else if (FinanceConstants.CLOSE_STATUS_EARLYSETTLE
									.equals(financeEnquiry.getClosingStatus())) {
								closingStatus = "Settled";
							}
							break;
						}
					}
				}
				String status = null;
				if (financeMain.isFinIsActive()) {
					status = "Active";
				} else {
					status = "Matured" + " - " + closingStatus;
				}
				lc = new Listcell(status);
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);

				lc = new Listcell();
				lc.setStyle("font-size:14px;font-weight: normal;");
				Button soa = new Button("Action");
				soa.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_Soa"));
				soa.addForward("onClick", self, "onClick_SOA");
				soa.setAttribute("financeMain", financeMain);
				lc.appendChild(soa);
				lc.setParent(item);
				listheader_SOA.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_Soa"));

				lc = new Listcell();
				lc.setStyle("font-size:14px;font-weight: normal;");
				Button noc = new Button("Action");
				noc.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_Noc"));
				noc.addForward("onClick", self, "onClick_NOC");
				noc.setAttribute("financeMain", financeMain);
				lc.appendChild(noc);
				lc.setParent(item);
				listheader_NOC.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_Noc"));
				if (financeMain.isFinIsActive()) {
					noc.setDisabled(true);
				}
				lc = new Listcell();
				lc.setStyle("font-size:14px;font-weight: normal;");
				Button foreClosure = new Button("Action");
				foreClosure.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_ForeClosure"));
				foreClosure.addForward("onClick", self, "onClick_foreClosure");
				foreClosure.setAttribute("financeMain", financeMain);
				lc.appendChild(foreClosure);
				lc.setParent(item);
				listheader_ForeClosure
						.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_ForeClosure"));
				lc = new Listcell();
				lc.setStyle("font-size:14px;font-weight: normal;");
				Button interestCertficate = new Button("Action");
				interestCertficate
						.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_InterestCertificate"));
				interestCertficate.addForward("onClick", self, "onClick_interestCertficate");
				interestCertficate.setAttribute("financeMain", financeMain);
				lc.appendChild(interestCertficate);
				lc.setParent(item);
				listheader_InterestCertificate
						.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_InterestCertificate"));

				lc = new Listcell();
				lc.setStyle("font-size:14px;font-weight: normal;");
				Button dpd = new Button("Action");
				dpd.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_Dpd"));
				dpd.addForward("onClick", self, "onClick_DPD");
				dpd.setAttribute("financeMain", financeMain);
				dpd.setAttribute("financeEnquiry", financeEnquiryDetails);
				lc.appendChild(dpd);
				lc.setParent(item);
				listheader_DPD.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_Dpd"));

				lc = new Listcell();
				lc.setStyle("font-size:14px;font-weight: normal;");
				Button gst = new Button("Action");
				gst.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_GstInvoice"));
				gst.addForward("onClick", self, "onClick_gst");
				gst.setAttribute("financeMain", financeMain);
				lc.appendChild(gst);
				lc.setParent(item);
				listheader_GSTInvoice.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_GstInvoice"));

				lc = new Listcell();
				lc.setStyle("font-size:14px;font-weight: normal;");
				Button creditNote = new Button("Action");
				creditNote.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_CreditNote"));
				creditNote.addForward("onClick", self, "onClick_creditNote");
				creditNote.setAttribute("financeMain", financeMain);
				lc.appendChild(creditNote);
				lc.setParent(item);
				listheader_CreditNote.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_CreditNote"));

				lc = new Listcell();
				lc.setStyle("font-size:14px;font-weight: normal;");
				Button cibil = new Button("Cibil");
				cibil.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_Cibil"));
				cibil.addForward("onClick", self, "onClickviewCibil");
				cibil.setAttribute("financeMain", financeMain);
				lc.appendChild(cibil);
				lc.setParent(item);
				listheader_Cibil.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_Cibil"));

				item.setAttribute("data", financeMain);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerLoanDetailsItemDoubleClicked");
				this.listBoxCustomerLoanDetails.appendChild(item);

			}
		}

	}

	public void onClick_SOA(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Button soa = (Button) event.getOrigin().getTarget();
		FinanceMain financeMain = (FinanceMain) soa.getAttribute("financeMain");
		Map<String, Object> arg = getDefaultArguments();
		arg.put("financeReference", financeMain.getFinReference());
		arg.put("finStartDate", financeMain.getFinStartDate());
		arg.put("dialogWindow", window_CustomerDialogg);
		arg.put("customer360", true);
		try {
			Executions.createComponents("/WEB-INF/pages/Reports/SOAReportGenerationDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);

	}

	public void onClick_NOC(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Button noc = (Button) event.getOrigin().getTarget();
		FinanceMain financeMain = (FinanceMain) noc.getAttribute("financeMain");
		Map<String, Object> arg = getDefaultArguments();
		arg.put("financeReference", financeMain.getFinReference());
		ReportConfiguration reportConfiguration = null;
		try {
			reportConfiguration = getReportConfiguration("menu_Item_NoObjectionCertificate");
		} catch (Exception e1) {

			e1.printStackTrace();
		}
		arg.put("ReportConfiguration", reportConfiguration);
		arg.put("dialogWindowName", reportConfiguration.getReportHeading());
		arg.put("dialogWindow", window_CustomerDialogg);
		arg.put("customer360", true);
		try {
			Executions.createComponents("/WEB-INF/pages/Reports/ReportGenerationPromptDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);

	}

	private ReportConfiguration getReportConfiguration(String reportMenuCode) throws Exception {
		ReportConfiguration aReportConfiguration = null;
		logger.debug("Entering");
		JdbcSearchObject<ReportConfiguration> searchObj = null;
		List<ReportConfiguration> listReportConfiguration = null;
		try {
			// ++ create the searchObject and initialize sorting ++//
			searchObj = new JdbcSearchObject<ReportConfiguration>(ReportConfiguration.class);
			searchObj.addTabelName("REPORTCONFIGURATION");
			searchObj.addFilter(new Filter("MENUITEMCODE", reportMenuCode, Filter.OP_EQUAL));

			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

			listReportConfiguration = pagedListService.getBySearchObject(searchObj);

			if (!listReportConfiguration.isEmpty()) {
				aReportConfiguration = listReportConfiguration.get(0);
				if (aReportConfiguration != null) {
					JdbcSearchObject<ReportFilterFields> filtersSearchObj = new JdbcSearchObject<ReportFilterFields>(
							ReportFilterFields.class);
					filtersSearchObj.addTabelName("REPORTFILTERFIELDS");
					filtersSearchObj
							.addFilter(new Filter("reportID", aReportConfiguration.getReportID(), Filter.OP_EQUAL));
					filtersSearchObj.addSort("SEQORDER", false);
					List<ReportFilterFields> listReportFilterFields = pagedListService
							.getBySearchObject(filtersSearchObj);
					aReportConfiguration.setListReportFieldsDetails(listReportFilterFields);
				}
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			searchObj = null;
			listReportConfiguration = null;
		}
		logger.debug("Leaving");
		return aReportConfiguration;
	}

	public void onClick_foreClosure(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Button soa = (Button) event.getOrigin().getTarget();
		FinanceMain financeMain = (FinanceMain) soa.getAttribute("financeMain");
		Map<String, Object> arg = getDefaultArguments();
		arg.put("financeReference", financeMain.getFinReference());
		ReportConfiguration reportConfiguration = null;
		try {
			reportConfiguration = getReportConfiguration("menu_Item_ForeclosureTerminationReport");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		arg.put("ReportConfiguration", reportConfiguration);
		arg.put("dialogWindowName", reportConfiguration.getReportHeading());
		arg.put("dialogWindow", window_CustomerDialogg);
		arg.put("customer360", true);
		try {
			Executions.createComponents("/WEB-INF/pages/Reports/ReportGenerationPromptDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);

	}

	public void onClick_interestCertficate(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Button soa = (Button) event.getOrigin().getTarget();
		FinanceMain financeMain = (FinanceMain) soa.getAttribute("financeMain");
		Map<String, Object> arg = getDefaultArguments();
		arg.put("financeMain", financeMain);
		arg.put("module", "interest");
		arg.put("dialogWindow", window_CustomerDialogg);
		arg.put("customer360", true);
		try {
			Executions.createComponents("/WEB-INF/pages/Reports/InterestCertificateGenerationDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);

	}

	public void onClick_DPD(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Button DPD = (Button) event.getOrigin().getTarget();
		FinanceEnquiry financeEnquiry = (FinanceEnquiry) DPD.getAttribute("financeEnquiry");
		if (financeEnquiry != null) {
			Map<String, Object> map = getDefaultArguments();
			map.put("financeEnquiry", financeEnquiry);
			map.put("enquiryType", "DPDENQ");
			map.put("fromApproved", true);
			map.put("childDialog", true);
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul",
						null, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug(Literal.LEAVING);

	}

	public void onClick_gst(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Button soa = (Button) event.getOrigin().getTarget();
		FinanceMain financeMain = (FinanceMain) soa.getAttribute("financeMain");
		Map<String, Object> arg = getDefaultArguments();
		arg.put("financeReference", financeMain.getFinReference());
		arg.put("data", customerDetails.getCustomer());
		ReportConfiguration reportConfiguration = null;
		try {
			reportConfiguration = getReportConfiguration("menu_Item_GST_InvoiceReport");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		arg.put("ReportConfiguration", reportConfiguration);
		arg.put("dialogWindowName", reportConfiguration.getReportHeading());
		arg.put("dialogWindow", window_CustomerDialogg);
		arg.put("customer360", true);
		try {
			Executions.createComponents("/WEB-INF/pages/Reports/ReportGenerationPromptDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);

	}

	public void onClick_creditNote(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Button soa = (Button) event.getOrigin().getTarget();
		FinanceMain financeMain = (FinanceMain) soa.getAttribute("financeMain");
		Map<String, Object> arg = getDefaultArguments();
		arg.put("financeReference", financeMain.getFinReference());
		arg.put("data", customerDetails.getCustomer());
		ReportConfiguration reportConfiguration = null;
		try {
			reportConfiguration = getReportConfiguration("menu_Item_CreditReport");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		arg.put("ReportConfiguration", reportConfiguration);
		arg.put("dialogWindowName", reportConfiguration.getReportHeading());
		arg.put("dialogWindow", window_CustomerDialogg);
		arg.put("customer360", true);
		try {
			Executions.createComponents("/WEB-INF/pages/Reports/ReportGenerationPromptDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);

	}

	/*
	 * public void onClickviewCibil(ForwardEvent event) throws IOException { logger.debug(Literal.ENTERING); boolean
	 * reportExit = false; Button cibil = (Button) event.getOrigin().getTarget(); FinanceMain financeMain =
	 * (FinanceMain) cibil.getAttribute("financeMain"); List<InterfaceServiceDetails> interfaceDetails =
	 * getInterfaceDetailService().getInterfaceDetailsServiceName( financeMain.getFinReference(),
	 * InterfaceServiceDetails.class, BhflInterfaceConstants.cibilServiceName); if
	 * (CollectionUtils.isNotEmpty(interfaceDetails)) { for (InterfaceServiceDetails interfaceServiceDetails :
	 * interfaceDetails) { if (interfaceServiceDetails.getCif().equals(customerDetails.getCustomer().getCustCIF()) &&
	 * interfaceServiceDetails.getCibilType().equals("Primary")) { InterfaceServiceDetails interfaceServiceDts =
	 * interfaceServiceDetails; String jsonResponse = null; String reportType = null; String servicename =
	 * BhflInterfaceConstants.cibilServiceName; String path = App.getResourcePath("config", "CIBILRawViewTemplate.FTL");
	 * File ftlFile = new File(path); StringTemplateLoader loader = new StringTemplateLoader(); byte[] cibilRawFile =
	 * FileUtils.readFileToByteArray(ftlFile); loader.putTemplate("CIBILRawViewTemplate.FTL", new String(cibilRawFile));
	 * 
	 * Configuration config = new Configuration(); config.setClassForTemplateLoading(InterfaceDetailDialogCtrl.class,
	 * "CIBILRawViewTemplate.FTL"); config.setTemplateLoader(loader); config.setDefaultEncoding("UTF-8");
	 * config.setLocale(Locale.getDefault());
	 * config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	 * TEMPLATES.put("CIBILRawViewTemplate.FTL", config); String result = null; JSONObject json = null;
	 * 
	 * try { JSONParser parser = new JSONParser(); if (StringUtils.isNotEmpty(interfaceServiceDts.getResponse())) { json
	 * = new JSONObject(); json = (JSONObject) parser.parse(interfaceServiceDts.getResponse());
	 * 
	 * json = processJsonForCibilRawReport(json); } } catch (Exception e) { logger.error("Exception", e); } try { if
	 * (json != null) { result = FreeMarkerTemplateUtils
	 * .processTemplateIntoString(getTemplate("CIBILRawViewTemplate.FTL"), json); HashMap<String, Object> detailMap =
	 * new HashMap<String, Object>(); detailMap.put("reportData", result); detailMap.put("reportName",
	 * "CibilRawReport"); detailMap.put("mediaFormat", "html");
	 * 
	 * Executions.createComponents( "/WEB-INF/pages/InterfaceDetails/InterfaceReportsDialog.zul",
	 * window_CustomerDialogg, detailMap); reportExit = true; } } catch (Exception e) { MessageUtil.showError(e); }
	 * logger.debug(Literal.LEAVING); } } } if (!reportExit) { MessageUtil.showMessage("Cibil Details Not Found"); }
	 * 
	 * }
	 */

	private Template getTemplate(String templateName) throws Exception {
		Configuration config = null;
		config = TEMPLATES.get(templateName);

		if (config == null) {
			throw new Exception("Template not found for the name " + templateName);
		}

		return config.getTemplate(templateName);
	}

	@SuppressWarnings("unchecked")
	private JSONObject processJsonForCibilRawReport(JSONObject json) throws ParseException {
		logger.debug(Literal.ENTERING);
		System.out.println(json);
		int loanCount = 0;
		int enquiryCount = 0;
		if (null != json) {
			logger.debug("Cibil Respone from BHFL" + json.toString());
			Object accountObject = json.get("accountDetail");
			if (null != accountObject && accountObject instanceof JSONArray) {
				JSONArray accountArray = (JSONArray) accountObject;
				loanCount = accountArray.size();
			}
			Object enquiryObject = json.get("enquiryDetails");
			if (null != enquiryObject && enquiryObject instanceof JSONArray) {
				JSONArray enquiryArray = (JSONArray) enquiryObject;
				if (null != enquiryArray) {
					for (Object enquiry : enquiryArray) {
						if (null != enquiry && (enquiry instanceof JSONObject)) {
							JSONObject object = (JSONObject) enquiry;
							object.put("enquiryPurpose", cibilloanTypes.get((String) object.get("enquiryPurpose")));
							String dateOFEnquiry = (String) object.get("dateOFEnquiry");

							if (StringUtils.isNotEmpty(dateOFEnquiry)) {
								Date dateEnquiry = new SimpleDateFormat("ddMMyyyy").parse(dateOFEnquiry);
								dateOFEnquiry = (new SimpleDateFormat("dd/MM/yyyy").format(dateEnquiry));
								object.put("dateOFEnquiry", dateOFEnquiry);

							}
						}
					}
				}
				enquiryCount = enquiryArray.size();
			}

			Object consumerIdentityObject = json.get("consumerIdentity");
			if (null != consumerIdentityObject && consumerIdentityObject instanceof JSONArray) {
				JSONArray consumerIdentityArray = (JSONArray) consumerIdentityObject;
				if (null != consumerIdentityArray) {
					for (Object id : consumerIdentityArray) {
						if (null != id && (id instanceof JSONObject)) {
							JSONObject object = (JSONObject) id;
							String idCode = (String) object.get("idType");
							if (MapUtils.isNotEmpty(cibilIdTypes) && StringUtils.isNotBlank(idCode)) {
								object.put("idType", cibilIdTypes.get(idCode));
							}
						}
					}
				}
			}

			Object consumerAddressObject = json.get("consumerAddress");
			if (null != consumerAddressObject && consumerAddressObject instanceof JSONArray) {
				JSONArray consumerAddressArray = (JSONArray) consumerAddressObject;
				if (null != consumerAddressArray) {
					for (Object id : consumerAddressArray) {
						if (null != id && (id instanceof JSONObject)) {
							JSONObject object = (JSONObject) id;
							String adressCategory = (String) object.get("addressCategory");
							if (MapUtils.isNotEmpty(cibilAddrCategory) && StringUtils.isNotBlank(adressCategory)) {
								object.put("addressCategory", cibilAddrCategory.get(adressCategory));
							}
							String residenceCode = (String) object.get("residenceCode");
							if (MapUtils.isNotEmpty(cibilResidenceCode) && StringUtils.isNotBlank(residenceCode)) {
								object.put("residenceCode", cibilResidenceCode.get(residenceCode));
							}
							String dateReported = (String) object.get("dateReported");
							if (StringUtils.isNotEmpty(dateReported)) {
								Date dateReport = new SimpleDateFormat("ddMMyyyy").parse(dateReported);
								dateReported = (new SimpleDateFormat("dd/MM/yyyy").format(dateReport));
								object.put("dateReported", dateReported);

							}
						}
					}
				}
			}

			Object cibilHeader = json.get("cibilHeader");
			if (null != cibilHeader && cibilHeader instanceof JSONObject) {
				JSONObject cibilHeaderJson = (JSONObject) json.get("cibilHeader");
				String dateProcess = (String) cibilHeaderJson.get("dateProcessed");
				String timeProcess = (String) cibilHeaderJson.get("timeProcessed");

				if (StringUtils.isNotEmpty(dateProcess)) {
					Date startdate = new SimpleDateFormat("ddMMyyyy").parse(dateProcess);
					dateProcess = (new SimpleDateFormat("dd/MM/yyyy").format(startdate));
					cibilHeaderJson.put("dateProcessed", dateProcess);

				}
				if (StringUtils.isNotEmpty(timeProcess)) {
					Date time = new SimpleDateFormat("HHmmss").parse(timeProcess);
					timeProcess = (new SimpleDateFormat("HH:mm:ss").format(time));
					cibilHeaderJson.put("timeProcessed", timeProcess);
				}
			}

			Object consumerTelephone = json.get("consumerTelephone");
			if (null != consumerTelephone && consumerTelephone instanceof JSONArray) {
				JSONArray consumerTelephoneArray = (JSONArray) consumerTelephone;
				if (null != consumerTelephoneArray) {
					for (Object telePhone : consumerTelephoneArray) {
						if (null != telePhone && (telePhone instanceof JSONObject)) {
							JSONObject telephoneJson = (JSONObject) telePhone;
							if (MapUtils.isNotEmpty(cibilPhoneTypes)
									&& StringUtils.isNotBlank((String) telephoneJson.get("telType"))) {
								telephoneJson.put("telType",
										cibilPhoneTypes.get((String) telephoneJson.get("telType")));
							}
						}
					}
				}
			}
		}
		Object employment = json.get("employment");
		if (null != employment && (employment instanceof JSONObject)) {
			JSONObject employmentJson = (JSONObject) employment;
			if (MapUtils.isNotEmpty(cibilOccupationTypes)
					&& StringUtils.isNotBlank((String) employmentJson.get("occupationCode"))) {
				employmentJson.put("occupationCode",
						cibilOccupationTypes.get((String) employmentJson.get("occupationCode")));
			}
		}
		Object accountDetail = json.get("accountDetail");
		if (null != accountDetail && (accountDetail instanceof JSONArray)) {
			JSONArray accountDetailArray = (JSONArray) accountDetail;
			if (null != accountDetailArray) {
				for (Object accountDtl : accountDetailArray) {
					if (null != accountDtl && (accountDtl instanceof JSONObject)) {
						JSONObject accountDtlJson = (JSONObject) accountDtl;
						if (MapUtils.isNotEmpty(cibilloanTypes)
								&& StringUtils.isNotBlank((String) accountDtlJson.get("accountType"))) {
							accountDtlJson.put("accountType",
									cibilloanTypes.get((String) accountDtlJson.get("accountType")));
						}
						String paymentHistoryEndDate = (String) accountDtlJson.get("paymentHistoryEndDate");
						if (StringUtils.isNotEmpty(paymentHistoryEndDate)) {
							Date pyamentEndDate = new SimpleDateFormat("ddMMyyyy").parse(paymentHistoryEndDate);
							paymentHistoryEndDate = (new SimpleDateFormat("dd/MM/yyyy").format(pyamentEndDate));
							accountDtlJson.put("paymentHistoryEndDate", paymentHistoryEndDate);

						}
						String paymentHistoryStartDate = (String) accountDtlJson.get("paymentHistoryStartDate");
						if (StringUtils.isNotEmpty(paymentHistoryStartDate)) {
							Date pyamentStartDate = new SimpleDateFormat("ddMMyyyy").parse(paymentHistoryStartDate);
							paymentHistoryStartDate = (new SimpleDateFormat("dd/MM/yyyy").format(pyamentStartDate));
							accountDtlJson.put("paymentHistoryStartDate", paymentHistoryStartDate);

						}
						String dateReportedAndCert = (String) accountDtlJson.get("dateReportedAndCert");
						if (StringUtils.isNotEmpty(dateReportedAndCert)) {
							Date dateReport = new SimpleDateFormat("ddMMyyyy").parse(dateReportedAndCert);
							dateReportedAndCert = (new SimpleDateFormat("dd/MM/yyyy").format(dateReport));
							accountDtlJson.put("dateReportedAndCert", dateReportedAndCert);

						}
						String dateClosed = (String) accountDtlJson.get("dateClosed");
						if (StringUtils.isNotEmpty(dateClosed)) {
							Date dateClose = new SimpleDateFormat("ddMMyyyy").parse(dateClosed);
							dateClosed = (new SimpleDateFormat("dd/MM/yyyy").format(dateClose));
							accountDtlJson.put("dateClosed", dateClosed);

						}
						String dateLastPayment = (String) accountDtlJson.get("dateLastPayment");
						if (StringUtils.isNotEmpty(dateLastPayment)) {
							Date lastPaymentDate = new SimpleDateFormat("ddMMyyyy").parse(dateLastPayment);
							dateLastPayment = (new SimpleDateFormat("dd/MM/yyyy").format(lastPaymentDate));
							accountDtlJson.put("dateLastPayment", dateLastPayment);

						}
						String dateOpenedOrDisbursed = (String) accountDtlJson.get("dateOpenedOrDisbursed");

						if (StringUtils.isNotEmpty(dateOpenedOrDisbursed)) {
							Date openendDate = new SimpleDateFormat("ddMMyyyy").parse(dateOpenedOrDisbursed);
							dateOpenedOrDisbursed = (new SimpleDateFormat("dd/MM/yyyy").format(openendDate));
							accountDtlJson.put("dateOpenedOrDisbursed", dateOpenedOrDisbursed);

						}

					}

				}
			}

		}

		json.put("loanCount", loanCount);
		json.put("enquiryCount", enquiryCount);
		logger.debug("Processed Cibil Respone" + json.toString());
		logger.debug(Literal.LEAVING);

		return json;
	}

	/*
	 * private void doPopulateCibilDetails() { if (MapUtils.isEmpty(cibilIdTypes)) { cibilIdTypes =
	 * bhflCibilRequestService.loadCibilIdTypes(); } if (MapUtils.isEmpty(cibilPhoneTypes)) { cibilPhoneTypes =
	 * bhflCibilRequestService.loadCibilPhoneTypes(); } if (MapUtils.isEmpty(cibilloanTypes)) { cibilloanTypes =
	 * bhflCibilRequestService.loadCibilLoanTypes(); } } protected Map<String, Object> getDefaultArguments() {
	 * HashMap<String, Object> aruments = new HashMap<>();
	 * 
	 * aruments.put("moduleCode", moduleCode); aruments.put("enqiryModule", enqiryModule);
	 * 
	 * return aruments; }
	 */

	public void doFillCustomerCollateralDetails(List<CollateralSetup> customercollateralDetails) {
		logger.debug("Entering");
		this.listBoxCustomerCollateralDetails.getItems().clear();
		if (customercollateralDetails != null) {
			for (CollateralSetup collateralSetup : customercollateralDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(collateralSetup.getCollateralRef());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(collateralSetup.getCollateralType());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(collateralSetup.getCollateralCcy());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(DateUtility.formatToLongDate(collateralSetup.getExpiryDate()));
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(DateUtility.formatToLongDate(collateralSetup.getNextReviewDate()));
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(
						PennantApplicationUtil.amountFormate(collateralSetup.getCollateralValue(), ccyFormatter));
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(
						PennantApplicationUtil.amountFormate(collateralSetup.getBankValuation(), ccyFormatter));
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				item.setAttribute("data", collateralSetup);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerCollateralItemDoubleClicked");
				this.listBoxCustomerCollateralDetails.appendChild(item);

			}
		}

	}

	public void onCustomerCollateralItemDoubleClicked(Event event) throws Exception {

		Listitem selectedItem = this.listBoxCustomerCollateralDetails.getSelectedItem();
		CollateralSetup collateralSetup = (CollateralSetup) selectedItem.getAttribute("data");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("collateralSetup", collateralSetup);
		arg.put("module", "E");
		try {
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralSetupDialog.zul", null,
					arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	public void doFillCustomerVASDetails(List<VASRecording> customerVASDetails) {
		logger.debug("Entering");
		this.listBoxCustomerVasDetails.getItems().clear();
		if (customerVASDetails != null) {
			for (VASRecording vasRecording : customerVASDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(vasRecording.getProductCode());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(vasRecording.getPostingAgainst());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(vasRecording.getVasReference());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(vasRecording.getFeePaymentMode());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(vasRecording.getVasStatus());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				item.setAttribute("data", vasRecording);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerVasItemDoubleClicked");
				this.listBoxCustomerVasDetails.appendChild(item);

			}
		}

	}

	public void onCustomerVasItemDoubleClicked(Event event) throws Exception {

		Listitem selectedItem = this.listBoxCustomerVasDetails.getSelectedItem();
		VASRecording vasRecording = (VASRecording) selectedItem.getAttribute("data");
		InsuranceDetails insuranceDetails = new InsuranceDetails();
		insuranceDetails.setReference(vasRecording.getVasReference());
		Map<String, Object> arg = getDefaultArguments();
		arg.put("insuranceDetails", insuranceDetails);
		arg.put("userActivityLog", true);
		try {
			Executions.createComponents("/WEB-INF/pages/Insurance/InsuranceEnquiryDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	public void doFillCustomerloanApprovalDetails(List<CustomerFinanceDetail> customerFinanceDetail) {
		logger.debug("Entering");
		this.listBoxloanApprovalDetails.getItems().clear();
		if (customerFinanceDetail != null) {
			for (CustomerFinanceDetail customerFinance : customerFinanceDetail) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(customerFinance.getFinReference());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(customerFinance.getFinTypeDesc());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell();//customerFinance.getLovDescFinDivision()
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(customerFinance.getFinCcy());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(customerFinance.getFinAmount(),
						CurrencyUtil.getFormat(customerFinance.getFinCcy())));
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(DateUtility.formatToLongDate(customerFinance.getFinStartDate()));
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(customerFinance.getNextRoleDesc());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(customerFinance.getNextRoleCode());
				lc.setStyle("font-size:14px;font-weight: normal;;font-weight: normal;");
				lc.setParent(item);
				item.setAttribute("data", customerFinance);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerLoanApprovalDetailsDoubleClicked");
				this.listBoxloanApprovalDetails.appendChild(item);

			}

		}
	}

	public void onCustomerLoanApprovalDetailsDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");
		Listitem selectedItem = listBoxloanApprovalDetails.getSelectedItem();
		CustomerFinanceDetail customerFinanceDetail = (CustomerFinanceDetail) selectedItem.getAttribute("data");

		String finReference = customerFinanceDetail.getFinReference();
		customerFinanceDetail.setAuditTransactionsList(
				getApprovalStatusEnquiryDAO().getFinTransactionsList(finReference, false, false, null));
		customerFinanceDetail.setNotesList(getNotesDAO().getNotesListAsc(getNotes(finReference, "financeMain")));
		HashMap<String, Object> arg = new HashMap<String, Object>();
		arg.put("customerFinanceDetail", customerFinanceDetail);
		arg.put("facility", false);
		arg.put("userActivityLog", true);
		arg.put("customer360", true);
		try {
			Executions.createComponents(
					"/WEB-INF/pages/FinanceEnquiry/FinApprovalStsInquiry/FinApprovalStsInquiryDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	protected Map<String, Object> getDefaultArguments() {
		HashMap<String, Object> aruments = new HashMap<>();

		aruments.put("moduleCode", moduleCode);
		aruments.put("enqiryModule", enqiryModule);

		return aruments;
	}

	public void onCustomerLoanDetailsItemDoubleClicked(Event event) throws Exception {
		Listitem selectedItem = this.listBoxCustomerLoanDetails.getSelectedItem();
		FinanceEnquiry financeEnquiry = (FinanceEnquiry) selectedItem.getAttribute("financeEnquiry");

		Map<String, Object> map = getDefaultArguments();
		map.put("financeEnquiry", financeEnquiry);
		map.put("enquiryType", "FINENQ");
		map.put("fromApproved", true);
		map.put("childDialog", true);
		map.put("customer360", true);
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	private Notes getNotes(String finReference, String moduleName) {
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName(moduleName);
		notes.setReference(finReference);
		notes.setVersion(0);
		logger.debug("Leaving ");
		return notes;
	}

	public void onClick$imgbasicDetails(Event event) throws Exception {
		logger.debug("Entering");
		Clients.scrollIntoView(gb_basicDetails);
		this.customerTitle.setValue("Customer View");
		statusBar.setVisible(false);
		logger.debug("Leaving");
	}

	public void onClick$imgkycDetails(Event event) throws Exception {
		logger.debug("Entering");
		Clients.scrollIntoView(gb_kycDetails);
		Clients.scrollIntoView(gb_kycDetails);
		this.customerTitle.setValue("Customer View");
		statusBar.setVisible(false);
		logger.debug("Leaving");
	}

	public void onClick$imgfinancialDetails(Event event) throws Exception {
		logger.debug("Entering");
		Clients.scrollIntoView(gb_financialDetails);
		Clients.scrollIntoView(gb_financialDetails);
		this.customerTitle.setValue("Customer View");
		statusBar.setVisible(false);
		logger.debug("Leaving");
	}

	public void onClick$imgshareHolderDetails(Event event) throws Exception {
		logger.debug("Entering");
		Clients.scrollIntoView(shareHolder);
		Clients.scrollIntoView(shareHolder);
		statusBar.setVisible(false);
		this.customerTitle.setValue("Customer View");
		logger.debug("Leaving");
	}

	public void onClick$imgbankingDetails(Event event) throws Exception {
		logger.debug("Entering");
		Clients.scrollIntoView(gb_bankingDetails);
		Clients.scrollIntoView(gb_bankingDetails);
		this.customerTitle.setValue("Customer View");
		statusBar.setVisible(false);
		logger.debug("Leaving");
	}

	public void onClick$imghelp(Event event) throws Exception {
		logger.debug("Entering");
		Clients.scrollIntoView(gb_help);
		Clients.scrollIntoView(gb_help);
		this.customerTitle.setValue("Customer View");
		statusBar.setVisible(false);
		logger.debug("Leaving");
	}

	public void onClick$imgadditionalDetails(Event event) throws Exception {
		logger.debug("Entering");
		Clients.scrollIntoView(gb_additionalDetails);
		Clients.scrollIntoView(gb_additionalDetails);
		this.customerTitle.setValue("Customer View");
		statusBar.setVisible(false);
		logger.debug("Leaving");
	}

	public void onClick$imgcustomerSummary(Event event) throws Exception {
		logger.debug("Entering");
		Clients.scrollIntoView(customerSumary);
		Clients.scrollIntoView(customerSumary);
		customerTitle.setValue("Customer Summary");
		statusBar.setVisible(false);
		logger.debug("Leaving");
	}

	public void onClick$imgloanDetails(Event event) throws Exception {
		logger.debug("Entering");
		Clients.scrollIntoView(loanDetails);
		Clients.scrollIntoView(loanDetails);
		this.customerTitle.setValue("Customer Summary");
		statusBar.setVisible(false);
		logger.debug("Leaving");
	}

	public void onClick$imgcollateralDetails(Event event) throws Exception {
		logger.debug("Entering");
		Clients.scrollIntoView(collateralDetails);
		Clients.scrollIntoView(collateralDetails);
		this.customerTitle.setValue("Customer Summary");
		statusBar.setVisible(false);
		logger.debug("Leaving");
	}

	public void onClick$imgvasDetails(Event event) throws Exception {
		logger.debug("Entering");
		Clients.scrollIntoView(vasDetails);
		Clients.scrollIntoView(vasDetails);
		this.customerTitle.setValue("Customer Summary");
		statusBar.setVisible(false);
		logger.debug("Leaving");
	}

	public void onClick$imgpendingLoanDetails(Event event) throws Exception {
		logger.debug("Entering");
		Clients.scrollIntoView(pendingLoanDetails);
		Clients.scrollIntoView(pendingLoanDetails);
		this.customerTitle.setValue("Customer Summary");
		statusBar.setVisible(false);
		logger.debug("Leaving");
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(id);
	}

	public void doFillDownload(Map<String, String> customerFinanceDetail) {
		logger.debug("Entering");
		this.listBoxDownloadsS.getItems().clear();
		Iterator<Map.Entry<String, String>> itr = customerFinanceDetail.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, String> entry = itr.next();
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(entry.getValue());
			lc.setStyle("font-size:14px;font-weight: normal;");
			lc.setParent(item);
			Button download = new Button("Download");
			download.setVisible(getUserWorkspace().isAllowed("button_CustomerViewDialog_Download_ClaimForms"));
			download.addForward("onClick", self, "onClick_download");
			download.setAttribute("data", entry);
			lc = new Listcell();
			lc.setStyle("font-size:14px;font-weight: normal;");
			lc.appendChild(download);
			lc.setParent(item);
			item.setAttribute("data", entry);
			this.listBoxDownloadsS.appendChild(item);
		}

	}

	public void doFillCustomerOffers() {
		logger.debug("Entering");

		CustomerOffersService custservice = new CustomerOffersService();
		CrmLeadDetails processRequest = custservice.processRequest(customerDetails.getCustomer().getCustCIF());
		this.listBoxCustomerOffers.getItems().clear();
		if (null != processRequest && processRequest.getLeadDetails() != null) {
			ArrayList<ProductOfferDetails> productOfferDetails = processRequest.getLeadDetails()
					.getProductOfferDetails();
			for (ProductOfferDetails productOfferdts : productOfferDetails) {
				Listitem item = new Listitem();
				item.setHeight("50px");
				Listcell lc;
				lc = new Listcell(productOfferdts.getExistingLAN());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(productOfferdts.getOfferProduct());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(productOfferdts.getExtCustSeg());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(productOfferdts.getOfferAmount().toString());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(DateUtility.formatToLongDate(productOfferdts.getOfferDate()));
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(productOfferdts.getStatus());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				lc = new Listcell(productOfferdts.getHoldReason());
				lc.setStyle("font-size:14px;font-weight: normal;");
				lc.setParent(item);
				this.listBoxCustomerOffers.appendChild(item);
			}
		}

	}

	public Map<String, String> prepareList() {
		Map<String, String> map = new HashMap<>();
		map.put("BCF", "BALIC Claim Form for Critical_illness");
		map.put("DCF", "Dealth Claim Form ");
		map.put("FGICF", "FGI CI Claim Form ");
		map.put("FGNCF", "Future General New Claim Form ");
		map.put("HDFCF", "HDFC  Claim Form ");
		map.put("CDCICF", "Checklist for Dealth/Critical illness Claims");
		return map;
	}

	public void onClick$downloadFaq(Event event) {
		try {
			String path = PathUtil.getPath(PathUtil.CUSTOMER_FAQ);
			File faq = new File(path);
			FileInputStream input = new FileInputStream(faq);
			byte[] byteArray = null;
			try {
				byteArray = IOUtils.toByteArray(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (byteArray != null) {
				HashMap<String, Object> auditMap = new HashMap<String, Object>(4);
				auditMap.put("reportBuffer", byteArray);
				auditMap.put("dialogWindow", window_CustomerDialogg);
				Executions.createComponents("/WEB-INF/pages/Reports/ReportView.zul", null, auditMap);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void onClick$crmRequest(Event event) {
		Map<String, Object> arg = new HashMap<>();
		arg.put("customerDetails", customerDetails);
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CrmDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	public void onClick$fetchCustOffers(Event event) {
		doFillCustomerOffers();
	}

	public void onClick_download(ForwardEvent event) {

		Button soa = (Button) event.getOrigin().getTarget();

		Map.Entry<String, String> entry = (Entry<String, String>) soa.getAttribute("data");
		String value = entry.getKey();
		switch (value) {
		case "BCF":
			try {
				String path = PathUtil.getPath(PathUtil.CUSTOMER_BALIC_CLAIM_FORM_FOR_CRITICAL_ILLNESS);
				Filedownload.save(new File(path), "text/plain");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case "DCF":
			try {
				String path = PathUtil.getPath(PathUtil.CUSTOMER_DEALTH_CLAIM_FORM);
				Filedownload.save(new File(path), "text/plain");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case "FGICF":
			try {
				String path = PathUtil.getPath(PathUtil.CUSTOMER_FGI_CI_Claim_Form);
				Filedownload.save(new File(path), "text/plain");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case "FGNCF":
			try {
				String path = PathUtil.getPath(PathUtil.CUSTOMER_FUTURE_GENERAL_NEW_CLAIM_FORM);
				Filedownload.save(new File(path), "text/plain");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case "HDFCF":
			try {
				String path = PathUtil.getPath(PathUtil.CUSTOMER_HDFC_CLAIM_FORM);
				Filedownload.save(new File(path), "text/plain");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case "CDCICF":
			try {
				String path = PathUtil.getPath(PathUtil.CUSTOMER_CHECKLIST_FOR_DEALTHCRITICAL_FORM);
				Filedownload.save(new File(path), "text/plain");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	public ApprovalStatusEnquiryDAO getApprovalStatusEnquiryDAO() {
		return approvalStatusEnquiryDAO;
	}

	public void setApprovalStatusEnquiryDAO(ApprovalStatusEnquiryDAO approvalStatusEnquiryDAO) {
		this.approvalStatusEnquiryDAO = approvalStatusEnquiryDAO;
	}

	public NotesDAO getNotesDAO() {
		return notesDAO;
	}

	public void setNotesDAO(NotesDAO notesDAO) {
		this.notesDAO = notesDAO;
	}

	public void setdMSService(DMSService dMSService) {
		this.dMSService = dMSService;
	}
}