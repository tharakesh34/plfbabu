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
 * * FileName : CustomerDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified
 * Date : 27-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * 09-05-2018 Vinay 0.2 Extended Details tab changes for * Customer Enquiry menu based on
 * rights * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.customermasters.customer;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.North;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.MasterDefUtil;
import com.pennant.app.util.MasterDefUtil.DocType;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.masters.MasterDefDAO;
import com.pennant.backend.model.MasterDef;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.customermasters.CustCardSales;
import com.pennant.backend.model.customermasters.CustEmployeeDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerGST;
import com.pennant.backend.model.customermasters.CustomerGSTDetails;
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.customermasters.GSTDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.systemmasters.Caste;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Department;
import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.model.systemmasters.EmpStsCode;
import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennant.backend.model.systemmasters.EmploymentType;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.model.systemmasters.PhoneType;
import com.pennant.backend.model.systemmasters.Religion;
import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.model.systemmasters.SubSegment;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.DirectorDetailService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.systemmasters.CustTypePANMappingService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.pff.document.DocVerificationUtil;
import com.pennant.pff.document.model.DocVerificationHeader;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.collateral.collateralsetup.CollateralBasicDetailsCtrl;
import com.pennant.webui.customermasters.customer.model.CustomerRatinglistItemRenderer;
import com.pennant.webui.customermasters.customeremail.CustomerEmailInlineEditCtrl;
import com.pennant.webui.customermasters.customeremploymentdetail.model.CustomerEmploymentDetailListModelItemRenderer;
import com.pennant.webui.customermasters.customerphonenumber.CustomerPhoneNumberInLineEditCtrl;
import com.pennant.webui.customermasters.directordetail.model.DirectorDetailListModelItemRenderer;
import com.pennant.webui.dedup.dedupparm.FetchBlackListCustomerAdditionalDetails;
import com.pennant.webui.dedup.dedupparm.FetchCustomerDedupDetails;
import com.pennant.webui.dedup.dedupparm.FetchDedupDetails;
import com.pennant.webui.dedup.dedupparm.FetchFinCustomerDedupDetails;
import com.pennant.webui.dedup.dedupparm.ShowBlackListDetailBox;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennant.webui.finance.jointaccountdetail.JointAccountDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.CreditInformation;
import com.pennanttech.pff.external.Crm;
import com.pennanttech.pff.external.FinnovService;
import com.pennanttech.webui.verification.FieldVerificationDialogCtrl;
import com.pennanttech.webui.verification.LVerificationCtrl;
import com.pennanttech.webui.verification.RCUVerificationDialogCtrl;
import com.pennapps.core.util.ObjectUtil;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/Customer/customerDialog.zul file.
 */
public class CustomerDialogCtrl extends GFCBaseCtrl<CustomerDetails> {
	private static final long serialVersionUID = 9031340167587772517L;
	private static final Logger logger = LogManager.getLogger(CustomerDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerDialog; // autowired

	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected North north; // autowired
	protected South south; // autowired
	protected Textbox custCIF; // autowired
	protected Textbox custCoreBank; // autowired
	protected Combobox custSalutationCode; // autowired
	protected Textbox custShrtName; // autowired
	protected Textbox custFirstName; // autowired
	protected Textbox custMiddleName; // autowired
	protected Textbox custLastName; // autowired
	protected Combobox residentialStatus;
	protected Textbox custLocalLngName; // autowired
	protected Space space_CustLocalLngName; // autowired
	protected Textbox motherMaidenName; // autowired
	protected ExtendedCombobox custLng; // autowired
	protected Textbox custSts; // autowired
	protected ExtendedCombobox custSector; // autowired
	protected ExtendedCombobox custIndustry; // autowired
	protected ExtendedCombobox custSegment; // autowired
	protected Label label_CustSegment; // autowired
	protected ExtendedCombobox custCOB; // autowired
	protected ExtendedCombobox custNationality; // autowired
	protected Combobox custMaritalSts; // autowired
	protected Datebox custDOB; // autowired
	protected Combobox custGenderCode; // autowired
	protected Intbox noOfDependents; // autowired
	protected ExtendedCombobox target; // autowired
	protected ExtendedCombobox custCtgCode; // autowired
	protected Checkbox salaryTransferred; // autowiredOdoed
	protected ExtendedCombobox custDftBranch; // autowired
	protected ExtendedCombobox custTypeCode; // autowired
	protected ExtendedCombobox custBaseCcy; // autowired
	protected Checkbox salariedCustomer; // autowired
	protected ExtendedCombobox custRO1; // autowired
	protected Uppercasebox eidNumber; // autowired
	protected Space space_EidNumber; // autowired
	protected Label label_CustomerDialog_EIDNumber; // autowired
	protected Label label_CustomerDialog_EIDName;
	protected Textbox custTradeLicenceNum; // autowired
	protected Textbox custRelatedParty; // autowired
	protected ExtendedCombobox custGroupId; // autowired
	protected Checkbox custIsStaff; // autowired
	protected Textbox custStaffID; // autowired
	protected Textbox custDSACode; // autowired
	protected ExtendedCombobox custDSADept; // autowired
	protected ExtendedCombobox custRiskCountry; // autowired
	protected ExtendedCombobox custParentCountry; // autowired
	protected ExtendedCombobox custSubSector; // autowired
	protected ExtendedCombobox custSubSegment; // autowired
	protected Uppercasebox applicationNo; // autowired
	protected Checkbox dnd;
	protected Checkbox vip;

	protected Hbox hbox_CustOtherCaste;
	protected Hbox hbox_CustOtherReligion;
	protected Hbox hbox_natureOfBusiness;
	protected Hbox hbox_entityType;
	/** Customer Employer Fields **/
	protected ExtendedCombobox empStatus; // autowired
	protected ExtendedCombobox empSector; // autowired
	protected ExtendedCombobox profession; // autowired
	protected ExtendedCombobox empName; // autowired
	protected Hbox hbox_empNameOther; // autowired
	protected Label label_empNameOther; // autowired
	protected Textbox empNameOther; // autowired
	protected Datebox empFrom; // autowired
	protected ExtendedCombobox empDesg; // autowired
	protected ExtendedCombobox empDept; // autowired
	protected CurrencyBox monthlyIncome; // autowired
	protected ExtendedCombobox otherIncome; // autowired
	protected CurrencyBox additionalIncome; // autowired
	protected Label age; // autowired
	protected Label exp; // autowired

	protected Label label_CustomerDialog_CustShrtName;
	protected Label label_CustomerDialog_SalaryTransfered;
	protected Label label_CustomerDialog_EmpSector;
	protected Label label_CustomerDialog_Profession;
	protected Label label_CustomerDialog_EmpFrom;
	protected Label label_CustomerDialog_MonthlyIncome;
	protected Label label_CustomerDialog_CustCOB;
	protected Label label_CustomerDialog_Target;
	protected Label label_LocalLngName;
	protected Label label_CustSubSegment;
	protected Label label_CustRelatedParty;
	protected Label label_CustomerDialog_CustLastName;
	protected Label label_CustomerDialog_motherMaidenName;
	protected Label label_CustomerDialog_CustReligion;
	protected Label label_CustomerDialog_CustCaste;
	protected Label label_CustomerDialog_EmploymentType;
	protected Label label_CustomerDialog_subCategory;
	protected Hbox hbox_EmploymentType;
	protected Hbox hbox_CustRelatedParty;
	protected Label label_CustGroupId;
	protected Row row_FirstMiddleName;
	protected Row row_GenderSalutation;
	protected Row row_MartialDependents;
	protected Row row_EmpName;
	protected Row row_DesgDept;
	protected Row row_custTradeLicenceNum;
	protected Row rowCustEmpSts;
	protected Row rowCustCRCPR;
	protected Row row_custStatus;
	protected Row row_custStaff;
	protected Row row_custDSA;
	protected Row row_custCountry;
	protected Row row_custSub;
	protected Row row_residentialsts;
	protected Groupbox gp_CustEmployeeDetails;
	protected Hbox hbox_SalariedCustomer;
	protected Hbox hbox_CustLastName;
	protected Hbox hbox_motherMaidenName;
	protected Hbox hbox_CustReligion;
	protected Hbox hbox_CustCaste;
	protected Space space_CustShrtName;
	protected Space space_cust_LName;
	protected Row row_qualification;

	protected Tab basicDetails;
	protected Tab tabkYCDetails;
	protected Tab tabbankDetails;
	protected Tab tabCardSaleDetails;
	protected Tab tabGstDetails;
	protected Tab tabCustGstDetails;

	protected Button btnNew_CustomerDocuments;
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

	protected Button btnNew_CustomerEmail;
	protected Listbox listBoxCustomerEmails;
	private List<CustomerEMail> customerEmailDetailList = new ArrayList<CustomerEMail>();

	protected Button btnNew_BankInformation;
	protected Listbox listBoxCustomerBankInformation;
	private List<CustomerBankInfo> customerBankInfoDetailList = new ArrayList<CustomerBankInfo>();

	protected Button btnNew_ChequeInformation;
	protected Listbox listBoxCustomerChequeInformation;
	private List<CustomerChequeInfo> customerChequeInfoDetailList = new ArrayList<CustomerChequeInfo>();

	protected Listbox listBoxCustomerFinExposure;

	protected Button btnNew_ExternalLiability;
	protected Groupbox gp_ExternalLiability;
	protected Listbox listBoxCustomerExternalLiability;
	private List<CustomerExtLiability> customerExtLiabilityDetailList = new ArrayList<>();

	protected Button btnNew_CardSalesInformation;
	protected Groupbox gp_CardSalesInformation;
	protected Listbox listBoxCustomerCardSalesInformation;
	private List<CustCardSales> customerCardSales = new ArrayList<CustCardSales>();

	protected Listheader listheader_JointCust;

	// Customer ratings List
	protected Button btnNew_CustomerRatings;
	protected Listbox listBoxCustomerRating;
	protected Listheader listheader_CustRating_RecordStatus;
	protected Listheader listheader_CustRating_RecordType;
	private List<CustomerRating> ratingsList = new ArrayList<CustomerRating>();

	// Customer Employment List
	protected Row row_EmploymentDetails;
	protected Button btnNew_CustomerEmploymentDetail;
	protected Listbox listBoxCustomerEmploymentDetail;
	protected Listheader listheader_CustEmp_RecordStatus;
	protected Listheader listheader_CustEmp_RecordType;
	private List<CustomerEmploymentDetail> customerEmploymentDetailList = new ArrayList<CustomerEmploymentDetail>();
	private List<CustomerEmploymentDetail> oldVar_EmploymentDetailsList = new ArrayList<CustomerEmploymentDetail>();

	// Customer Income details List
	protected Button btnNew_CustomerIncome;
	protected Listbox listBoxCustomerIncome;
	protected Listheader listheader_CustInc_RecordStatus;
	protected Listheader listheader_CustInc_RecordType;
	private List<CustomerIncome> incomeList = new ArrayList<CustomerIncome>();

	// Customer Gst Details List
	protected Button btnNew_CustomerGSTDetails;
	protected Listbox listBoxCustomerGst;
	private List<CustomerGST> customerGstList = new ArrayList<CustomerGST>();

	// GST details for customer
	protected Button btnNew_GSTDetails;
	protected Listbox listBoxCustomerGstDetails;
	private List<GSTDetail> gstDetailsList = new ArrayList<>();

	private transient String oldVar_empStatus;
	private CustomerDetails customerDetails; // overhanded per param
	private transient CustomerListCtrl customerListCtrl; // overhanded per param

	private transient boolean validationOn;

	protected Groupbox gb_keyDetails; // autowired
	protected Groupbox gb_incomeDetails; // autowired
	protected Groupbox gb_rating; // autowired
	protected Groupbox gb_directorDetails; // autowired

	protected Tabpanel tp_basicDetails;
	protected Tabpanel tp_KYCDetails;
	protected Tabpanel tp_Financials;
	protected Tabpanel tp_directorDetails;
	protected Tabpanel tp_BankDetails;
	protected Tabpanel tp_CardSales;

	protected Tabpanel tp_gstDetails;
	protected Tabpanel tp_custGstDetails;

	protected Groupbox gb_Action;
	protected Groupbox gb_statusDetails;
	String parms[] = new String[4];

	private int countRows = PennantConstants.listGridSize;
	protected Tab tabfinancial;

	protected Label label_CustomerDialog_CustDOB;

	// Declaration of Service(s) & DAO(s)
	private transient CustomerDetailsService customerDetailsService;
	private transient DedupParmService dedupParmService;
	private int ccyFormatter = 0;
	private int old_ccyFormatter = 0;
	private String moduleType = "";
	protected Div divKeyDetails;
	protected Grid grid_KYCDetails;
	protected Grid grid_BankDetails;
	protected Grid grid_CardSales;

	private boolean isRetailCustomer = false;
	private boolean isSMECustomer = false;
	private String empAlocType = "";

	protected Tab directorDetails;
	// Customer Directory details List
	protected Button btnNew_DirectorDetail;
	protected Listbox listBoxCustomerDirectory;
	protected Listheader listheader_CustDirector_RecordStatus;
	protected Listheader listheader_CustDirector_RecordType;
	private List<DirectorDetail> directorList = new ArrayList<DirectorDetail>();
	protected Label label_CustomerDialog_CustNationality;
	private transient DirectorDetailService directorDetailService;
	Date appDate = SysParamUtil.getAppDate();
	Date startDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");

	private String empStatus_Temp = "";
	private String empName_Temp = "";
	private String custBaseCcy_Temp = "";
	// Used for get the credit review details in loan basic details
	private String empType = "";

	private FinanceDetail financedetail;
	private Object financeMainDialogCtrl;
	private Object promotionPickListCtrl;
	private Tabpanel panel = null;
	private Groupbox groupbox = null;
	private boolean newFinance = false;
	private boolean isFinanceProcess = false;
	private boolean isNotFinanceProcess = false;
	private boolean isEnqProcess = false;
	private boolean isFirstTask = false;
	private boolean isPromotionPickProcess = false;
	private boolean isFromCustomer = false;
	private boolean fromLoan = false;

	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private CollateralBasicDetailsCtrl collateralBasicDetailsCtrl;
	private LVerificationCtrl lVerificationCtrl;
	private RCUVerificationDialogCtrl rcuVerificationDialogCtrl;
	private FieldVerificationDialogCtrl fieldVerificationDialogCtrl;
	protected Groupbox finBasicdetails;

	public boolean validateAllDetails = true;
	public boolean validateCustDocs = true;
	private String moduleName;
	private List<CustomerBankInfo> CustomerBankInfoList;
	private List<CustCardSales> CustomerCardSalesInfoList;

	protected Grid grid_ExtendedDetails;
	protected Combobox subCategory;
	protected ExtendedCombobox religion;
	protected ExtendedCombobox caste;

	// customerGST Detaisl;
	protected Textbox custId;
	protected Textbox gstNumber;
	protected Combobox frequencyType;
	protected Combobox frequency;

	// Extended fields
	private ExtendedFieldCtrl extendedFieldCtrl = null;

	String primaryIdRegex = null;
	String primaryIdLabel;
	boolean primaryIdMandatory = false;
	Map<String, Configuration> TEMPLATES = new HashMap<String, Configuration>();

	@Autowired(required = false)
	private FinnovService finnovService;

	private boolean marginDeviation = false;
	@Autowired(required = false)
	private Crm crm;

	@Autowired(required = false)
	private CreditInformation creditInformation;
	// external implementation of the CIBIL service
	private CreditInformation custCreditInformation;
	protected Button btn_GenerateCibil;
	private boolean isNewCustCret = false;
	private JointAccountDetailDialogCtrl jointAccountDetailDialogCtrl;
	private boolean dedupCheckReq = false;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private DMSService dMSService;
	private String usrAction = null;

	boolean isPanMandatory = ImplementationConstants.RETAIL_CUST_PAN_MANDATORY;

	protected Textbox otherReligion; // autowired
	protected Textbox otherCaste;
	protected Space space_OtherReligion;
	protected Label label_OtherReligion;
	protected Space space_OtherCaste;
	protected Label label_OtherCaste;
	protected Textbox ckycOrRefNo;
	protected Combobox natureOfBusiness;
	protected Space space_natureOfBusiness;
	protected Label label_natureOfBusiness;
	protected Combobox entityType;
	protected Space space_entitytype;
	protected Label label_entityType;
	protected Label label_CustomerDialog_CustQualification;
	protected Label label_CustomerDialog_CustIndustry;
	// protected Label label_CustomerDialog_ResidentialStatus;
	// protected Space space_Residential;
	protected Hbox hbox_SubCategory;

	protected ExtendedCombobox custQualification; // autowired
	protected ExtendedCombobox custFlags; // autowired
	// protected Combobox custResidentialStstus; // autowired
	private String natOfBusiness = "";
	protected Space space_applicationNo;
	protected Label label_CustomerDialog_ApplicationNo;
	protected Label label_CustomerDialog_CKYCOrReferenceNo;
	protected Label label_CustomerDialog_CustTypeCode;
	protected Label label_CustomerDialog_CustDftBranch;

	protected Listbox listBoxCustomerIncomeInLineEdit;
	private IncomeAndExpenseCtrl incomeAndExpenseCtrl;

	// Phonenumbers Listbox inline editing
	protected Listbox listBoxCustomerPhoneNumbersInlineEdit;
	private CustomerPhoneNumberInLineEditCtrl customerPhoneNumberInLineEditCtrl;
	// Emails Listbox inline editing
	protected Listbox listBoxCustomerEmailsInlineEdit;
	protected CustomerEmailInlineEditCtrl customerEmailInlineEditCtrl;
	private transient CustTypePANMappingService custTypePANMappingService;
	FinanceMain financeMain = null;
	private String cif[] = null;
	private String finReference = null;
	// Setting mandatory based on system parameter.
	protected Space spaceSubCategory;
	protected Button btnUploadExternalLiability;
	protected Button btnDownloadExternalLiability;
	private MasterDefDAO masterDefDAO;
	protected CustomerExtLiabilityUploadDialogCtrl customerExtLiabilityUploadDialogCtrl;

	public String dmsApplicationNo;
	public String leadId;

	private MasterDef masterDef;
	private boolean isKYCverified = true;

	/**
	 * default constructor.<br>
	 */
	public CustomerDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CustomerDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerDialog);

		try {

			if (event.getTarget().getParent() != null) {
				if (event.getTarget().getParent() instanceof Tabpanel) {
					panel = (Tabpanel) event.getTarget().getParent();
				} else if (event.getTarget().getParent() instanceof Groupbox) {
					groupbox = (Groupbox) event.getTarget().getParent();
				}
			}

			if (arguments.containsKey("customerDetails")) {
				this.customerDetails = (CustomerDetails) arguments.get("customerDetails");
				CustomerDetails befImage = new CustomerDetails();
				BeanUtils.copyProperties(this.customerDetails, befImage);
				this.customerDetails.setBefImage(befImage);
				setCustomerDetails(this.customerDetails);
				newFinance = true;
			} else {
				setCustomerDetails(null);
			}
			if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl(arguments.get("financeMainDialogCtrl"));
				this.window_CustomerDialog.setTitle("");
				newFinance = true;
			}
			if (arguments.containsKey("promotionPickListCtrl")) {
				setPromotionPickListCtrl(arguments.get("promotionPickListCtrl"));
				this.window_CustomerDialog.setTitle("");
				isPromotionPickProcess = true;
			}
			if (arguments.containsKey("isEnqProcess")) {
				isEnqProcess = (Boolean) arguments.get("isEnqProcess");
				this.moduleType = PennantConstants.MODULETYPE_ENQ;
			}
			if (arguments.containsKey("isFirstTask")) {
				isFirstTask = (Boolean) arguments.get("isFirstTask");
			}
			if (arguments.containsKey("isFromCustomer")) {
				isFromCustomer = (Boolean) arguments.get("isFromCustomer");
			}
			if (arguments.containsKey("fromLoan")) {
				fromLoan = (Boolean) arguments.get("fromLoan");
			}
			if (arguments.containsKey("isNotFinanceProcess")) {
				isNotFinanceProcess = (Boolean) arguments.get("isNotFinanceProcess");
			}

			if (arguments.containsKey("usrAction")) {
				usrAction = (String) arguments.get("usrAction");
			}

			if (arguments.containsKey("moduleName")) {
				this.moduleName = (String) arguments.get("moduleName");
			}
			if (arguments.containsKey("isNewCustCret")) {
				this.isNewCustCret = (boolean) arguments.get("isNewCustCret");
			}
			if (arguments.containsKey("jointAccountDetailDialogCtrl")) {
				this.jointAccountDetailDialogCtrl = (JointAccountDetailDialogCtrl) arguments
						.get("jointAccountDetailDialogCtrl");
			}
			if (arguments.containsKey("coAppFilter")) {
				this.cif = (String[]) arguments.get("coAppFilter");
			}
			if (arguments.containsKey("financeDetail")) {
				setFinancedetail((FinanceDetail) arguments.get("financeDetail"));
				isFinanceProcess = true;
				if (getFinancedetail() != null) {
					setCustomerDetails(getFinancedetail().getCustomerDetails());
					FinanceMain financeMain = getFinancedetail().getFinScheduleData().getFinanceMain();
					getFinancedetail().getCustomerDetails().getCustomer().setWorkflowId(financeMain.getWorkflowId());
				}
			} else if (arguments.containsKey("finMain")) {
				financeMain = (FinanceMain) arguments.get("finMain");
			}
			if (fromLoan) {
				isFinanceProcess = fromLoan;
			}
			// append finance basic details
			if (arguments.containsKey("finHeaderList")) {
				appendFinBasicDetails((ArrayList<Object>) arguments.get("finHeaderList"));
			} else {
				this.finBasicdetails.setZclass("null");
			}
			if (enqiryModule) {
				this.moduleType = PennantConstants.MODULETYPE_ENQ;
			}
			Customer customer = getCustomerDetails().getCustomer();
			ccyFormatter = CurrencyUtil.getFormat(customer.getCustBaseCcy());
			old_ccyFormatter = ccyFormatter;

			if (isFinanceProcess || isEnqProcess || isNewCustCret) {
				if (arguments.containsKey("roleCode")) {
					setRole((String) arguments.get("roleCode"));
					getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"), "CustomerDialog");
				}
			} else {
				if (PennantConstants.MODULETYPE_ENQ.equals(moduleType) || isNotFinanceProcess) {
					doLoadWorkFlow(false, customer.getWorkflowId(), customer.getNextTaskId());
				} else {
					doLoadWorkFlow(customer.isWorkflow(), customer.getWorkflowId(), customer.getNextTaskId());
				}
				if (isWorkFlowEnabled()) {
					this.userAction = setListRecordStatus(this.userAction);
					getUserWorkspace().allocateRoleAuthorities(getRole(), "CustomerDialog");
				}
			}

			if (arguments.containsKey("finReference")) {
				finReference = (String) arguments.get("finReference");
			}

			// READ OVERHANDED params !
			// we get the customerListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete customer here.
			if (arguments.containsKey("customerListCtrl")) {
				setCustomerListCtrl((CustomerListCtrl) arguments.get("customerListCtrl"));
			} else {
				setCustomerListCtrl(null);
			}

			if (StringUtils.isNotEmpty(this.customerDetails.getCustomer().getCustCtgCode()) && StringUtils
					.equals(this.customerDetails.getCustomer().getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
				isRetailCustomer = true;
			}
			if (StringUtils.isNotEmpty(this.customerDetails.getCustomer().getCustCtgCode()) && StringUtils
					.equals(this.customerDetails.getCustomer().getCustCtgCode(), PennantConstants.PFF_CUSTCTG_SME)) {
				isSMECustomer = true;
			}

			/* set components visible dependent of the users rights */
			if (!isNotFinanceProcess || isNewCustCret) {
				doCheckRights();
			}

			// set Field Properties
			doSetFieldProperties();
			// Height Setting
			if ((isFinanceProcess || isNotFinanceProcess || isNewCustCret) && !isPromotionPickProcess) {
				int divKycHeight = this.borderLayoutHeight - 80;
				int semiBorderlayoutHeights = divKycHeight / 2;
				if (isRetailCustomer) {

					this.tp_basicDetails.setHeight(borderLayoutHeight - 195 + "px");
					this.tp_KYCDetails.setHeight(borderLayoutHeight - 195 + "px");
					this.tp_Financials.setHeight(borderLayoutHeight - 195 + "px");
					this.tp_BankDetails.setHeight(borderLayoutHeight - 195 + "px");
					this.tp_directorDetails.setHeight(borderLayoutHeight - 195 + "px");
					if (this.tp_CardSales.isVisible()) {
						this.tp_CardSales.setHeight(borderLayoutHeight - 195 + "px");
					}
					this.listBoxCustomerGst.setHeight(semiBorderlayoutHeights - 90 + "px");
				} else {
					this.divKeyDetails.setHeight(borderLayoutHeight - 240 + "px");
					this.listBoxCustomerRating.setHeight(semiBorderlayoutHeights - 130 + "px");
					this.grid_KYCDetails.setHeight(borderLayoutHeight - 220 + "px");
					this.listBoxCustomerGst.setHeight(semiBorderlayoutHeights - 125 + "px");
				}
				this.listBoxCustomerGst.setHeight(borderLayoutHeight - 240 + "px");
				this.listBoxCustomerIncome.setHeight(borderLayoutHeight - 240 + "px");
				this.gb_directorDetails.setHeight(borderLayoutHeight - 220 + "px");
				this.listBoxCustomerDirectory.setHeight(borderLayoutHeight - 220 + "px");
				this.grid_BankDetails.setHeight(borderLayoutHeight - 220 + "px");
				this.grid_CardSales.setHeight(borderLayoutHeight - 220 + "px");
				this.listBoxCustomerCardSalesInformation.setHeight(semiBorderlayoutHeights - 125 + "px");
			} else {
				int divKycHeight = this.borderLayoutHeight - 80;
				int borderlayoutHeights = divKycHeight / 2;
				if (isRetailCustomer) {
					this.tp_basicDetails.setHeight(borderLayoutHeight - 90 + "px");
					// this.divKeyDetails.setHeight(borderLayoutHeight - 130 +
					// "px");
				} else {
					this.divKeyDetails.setHeight(borderLayoutHeight - 50 + "px");
					this.listBoxCustomerRating.setHeight(this.borderLayoutHeight - 330 + "px");
				}

				this.tp_gstDetails.setHeight(borderLayoutHeight - 90 + "px");
				this.tp_KYCDetails.setHeight(borderLayoutHeight - 90 + "px");
				this.tp_Financials.setHeight(borderLayoutHeight - 90 + "px");
				this.tp_BankDetails.setHeight(borderLayoutHeight - 90 + "px");
				this.tp_directorDetails.setHeight(borderLayoutHeight - 90 + "px");
				if (this.tp_CardSales.isVisible()) {
					this.tp_CardSales.setHeight(borderLayoutHeight - 90 + "px");
				}

				this.listBoxCustomerGst.setHeight(borderLayoutHeight - (isRetailCustomer ? 132 : 90) + "px");

				this.listBoxCustomerIncome.setHeight(borderLayoutHeight - 132 + "px");
				this.gb_directorDetails.setHeight(borderLayoutHeight - 40 + "px");
				this.listBoxCustomerDirectory.setHeight(borderLayoutHeight - 40 + "px");

				this.listBoxCustomerCardSalesInformation.setHeight(borderlayoutHeights - 130 + "px");
			}

			if (arguments.containsKey("applicationNo")) {
				dmsApplicationNo = (String) arguments.get("applicationNo");
			}

			if (arguments.containsKey("leadId")) {
				leadId = (String) arguments.get("leadId");
			}

			doShowDialog(this.customerDetails);
			if (arguments.containsKey("ProspectCustomerEnq")) {
				this.window_CustomerDialog.doModal();
			}
			if (arguments.containsKey("CustomerEnq")) {
				this.north.setVisible(true);
				this.window_CustomerDialog.setWidth("100%");
				this.window_CustomerDialog.setHeight("90%");
				this.window_CustomerDialog.doModal();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			closeDialog();
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.custCoreBank.setMaxlength(50);
		if (isRetailCustomer) {
			this.custShrtName.setMaxlength(200);
		} else {
			// Ticket id:130260----> For Non Individual borrower, customer name
			// length
			// and Account holder name length in Mandate required 100 insted of
			// 50.

			this.custShrtName.setMaxlength(100);

		}
		if (isSMECustomer) {

		}
		this.custFirstName.setMaxlength(50);
		this.custMiddleName.setMaxlength(50);
		this.custLastName.setMaxlength(50);
		this.custLocalLngName.setMaxlength(50);
		this.motherMaidenName.setMaxlength(50);
		this.custTradeLicenceNum.setMaxlength(50);
		this.custRelatedParty.setMaxlength(200);

		this.custCtgCode.setMaxlength(8);
		this.custCtgCode.setTextBoxWidth(151);
		this.custCtgCode.setMandatoryStyle(true);
		this.custCtgCode.setModuleName("CustomerCategory");
		this.custCtgCode.setValueColumn("CustCtgCode");
		this.custCtgCode.setDescColumn("CustCtgDesc");
		this.custCtgCode.setValidateColumns(new String[] { "CustCtgCode" });

		this.custDftBranch.setMaxlength(LengthConstants.LEN_BRANCH);
		this.custDftBranch.setMandatoryStyle(true);
		this.custDftBranch.setModuleName("Branch");
		this.custDftBranch.setValueColumn("BranchCode");
		this.custDftBranch.setDescColumn("BranchDesc");
		this.custDftBranch.setValidateColumns(new String[] { "BranchCode" });

		this.custTypeCode.setMaxlength(8);
		this.custTypeCode.setMandatoryStyle(true);
		this.custTypeCode.setModuleName("CustomerType");
		this.custTypeCode.setValueColumn("CustTypeCode");
		this.custTypeCode.setDescColumn("CustTypeDesc");
		this.custTypeCode.setValidateColumns(new String[] { "CustTypeCode" });

		this.custBaseCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.custBaseCcy.setTextBoxWidth(121);
		this.custBaseCcy.setMandatoryStyle(true);
		this.custBaseCcy.setModuleName("Currency");
		this.custBaseCcy.setValueColumn("CcyCode");
		this.custBaseCcy.setDescColumn("CcyDesc");
		this.custBaseCcy.setValidateColumns(new String[] { "CcyCode" });

		this.custDOB.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.custNationality.setMaxlength(8);
		this.custNationality.setTextBoxWidth(121);
		this.custNationality.setMandatoryStyle(true);
		this.custNationality.setModuleName("NationalityCode");
		this.custNationality.setValueColumn("NationalityCode");
		this.custNationality.setDescColumn("NationalityDesc");
		this.custNationality.setValidateColumns(new String[] { "NationalityCode" });

		this.custLng.setMaxlength(2);
		this.custLng.setTextBoxWidth(121);
		this.custLng.setMandatoryStyle(true);
		this.custLng.setModuleName("Language");
		this.custLng.setValueColumn("LngCode");
		this.custLng.setDescColumn("LngDesc");
		this.custLng.setValidateColumns(new String[] { "LngCode" });

		this.custSts.setReadonly(true);
		this.ckycOrRefNo.setReadonly(true);

		this.custSector.setMaxlength(8);
		this.custSector.setTextBoxWidth(121);
		if (!isReadOnly("CustomerDialog_custSector")) {
			this.custSector.setMandatoryStyle(true);
		}
		this.custSector.setModuleName("Sector");
		this.custSector.setValueColumn("SectorCode");
		this.custSector.setDescColumn("SectorDesc");
		this.custSector.setValidateColumns(new String[] { "SectorCode" });

		this.custIndustry.setMaxlength(8);
		this.custIndustry.setTextBoxWidth(121);
		if (!isReadOnly("CustomerDialog_custSector")) {
			this.custIndustry.setMandatoryStyle(true);
		}
		this.custIndustry.setModuleName("Industry");
		this.custIndustry.setValueColumn("IndustryCode");
		this.custIndustry.setDescColumn("IndustryDesc");
		this.custIndustry.setValidateColumns(new String[] { "IndustryCode" });

		this.custCOB.setMaxlength(2);
		this.custCOB.setTextBoxWidth(121);
		this.custCOB.setMandatoryStyle(true);
		this.custCOB.setModuleName("Country");
		this.custCOB.setValueColumn("CountryCode");
		this.custCOB.setDescColumn("CountryDesc");
		this.custCOB.setValidateColumns(new String[] { "CountryCode" });

		this.target.setMaxlength(8);
		this.target.setTextBoxWidth(121);
		this.target.setMandatoryStyle(false);
		this.target.setModuleName("TargetDetail");
		this.target.setValueColumn("TargetCode");
		this.target.setDescColumn("TargetDesc");
		this.target.setValidateColumns(new String[] { "TargetCode" });

		this.caste.setMaxlength(8);
		this.caste.setMandatoryStyle(true);
		this.caste.setModuleName("Caste");
		this.caste.setValueColumn("CasteCode");
		this.caste.setDescColumn("CasteDesc");
		this.caste.setValidateColumns(new String[] { "CasteCode" });

		this.religion.setMaxlength(8);
		this.religion.setMandatoryStyle(false);
		this.religion.setModuleName("Religion");
		this.religion.setValueColumn("ReligionCode");
		this.religion.setDescColumn("ReligionDesc");
		this.religion.setValidateColumns(new String[] { "ReligionCode" });

		this.eidNumber.setMaxlength(LengthConstants.LEN_EID);

		// Customer Employee Field Properties
		this.empStatus.setMaxlength(8);
		this.empStatus.setTextBoxWidth(121);
		this.empStatus.setMandatoryStyle(true);
		this.empStatus.setModuleName("EmpStsCode");
		this.empStatus.setValueColumn("EmpStsCode");
		this.empStatus.setDescColumn("EmpStsDesc");
		this.empStatus.setValidateColumns(new String[] { "EmpStsCode" });

		this.empSector.setMaxlength(8);
		this.empSector.setTextBoxWidth(120);
		this.empSector.setModuleName("EmploymentType");
		this.empSector.setValueColumn("EmpType");
		this.empSector.setDescColumn("EmpTypeDesc");
		this.empSector.setValidateColumns(new String[] { "EmpType" });

		this.profession.setMaxlength(8);
		this.profession.setMandatoryStyle(false);
		this.profession.setModuleName("Profession");
		this.profession.setValueColumn("ProfessionCode");
		this.profession.setDescColumn("ProfessionDesc");
		this.profession.setValidateColumns(new String[] { "ProfessionCode" });

		this.custQualification.setMaxlength(8);
		this.custQualification.setMandatoryStyle(false);
		this.custQualification.setModuleName("Qualification");
		this.custQualification.setValueColumn("Code");
		this.custQualification.setDescColumn("Description");
		this.custQualification.setValidateColumns(new String[] { "Code" });

		this.monthlyIncome.setFormat(PennantApplicationUtil.getAmountFormate(old_ccyFormatter));
		this.monthlyIncome.setScale(old_ccyFormatter);

		this.additionalIncome.setFormat(PennantApplicationUtil.getAmountFormate(old_ccyFormatter));
		this.additionalIncome.setScale(old_ccyFormatter);

		this.empFrom.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.empName.setInputAllowed(false);
		this.empName.setDisplayStyle(3);
		this.empName.setTextBoxWidth(250);
		this.empName.setMandatoryStyle(true);
		this.empName.setModuleName("EmployerDetail");
		this.empName.setValueColumn("EmployerId");
		this.empName.setDescColumn("EmpName");
		this.empName.setValidateColumns(new String[] { "EmployerId" });

		this.empDesg.setMaxlength(50);
		this.empDesg.setTextBoxWidth(121);
		this.empDesg.setMandatoryStyle(true);
		this.empDesg.setModuleName("Designation");
		this.empDesg.setValueColumn("DesgCode");
		this.empDesg.setDescColumn("DesgDesc");
		this.empDesg.setValidateColumns(new String[] { "DesgCode" });

		this.empDept.setMaxlength(8);
		this.empDept.setTextBoxWidth(121);
		this.empDept.setMandatoryStyle(true);
		this.empDept.setModuleName("Department");
		this.empDept.setValueColumn("DeptCode");
		this.empDept.setDescColumn("DeptDesc");
		this.empDept.setValidateColumns(new String[] { "DeptCode" });

		this.otherIncome.setMaxlength(8);
		this.otherIncome.setTextBoxWidth(121);
		this.otherIncome.setModuleName("IncomeType");
		this.otherIncome.setValueColumn("IncomeTypeCode");
		this.otherIncome.setDescColumn("IncomeTypeDesc");
		this.otherIncome.setValidateColumns(new String[] { "IncomeTypeCode" });
		this.otherIncome
				.setFilters(new Filter[] { new Filter("IncomeExpense", PennantConstants.INCOME, Filter.OP_EQUAL) });

		this.custGroupId.setMaxlength(8);
		this.custGroupId.setTextBoxWidth(121);
		this.custGroupId.setMandatoryStyle(false);
		this.custGroupId.setModuleName("CustomerGroup");
		this.custGroupId.setValueColumn("CustGrpCode");
		this.custGroupId.setDescColumn("CustGrpDesc");
		this.custGroupId.setValidateColumns(new String[] { "CustGrpCode" });

		this.custStaffID.setMaxlength(8);
		this.custDSACode.setMaxlength(8);

		this.custDSADept.setMaxlength(8);
		this.custDSADept.setMandatoryStyle(false);
		this.custDSADept.setModuleName("Department");
		this.custDSADept.setValueColumn("DeptCode");
		this.custDSADept.setDescColumn("DeptDesc");
		this.custDSADept.setValidateColumns(new String[] { "DeptCode" });

		this.custRiskCountry.setMaxlength(2);
		this.custRiskCountry.setMandatoryStyle(false);
		this.custRiskCountry.setModuleName("Country");
		this.custRiskCountry.setValueColumn("CountryCode");
		this.custRiskCountry.setDescColumn("CountryDesc");
		this.custRiskCountry.setValidateColumns(new String[] { "CountryCode" });

		this.custParentCountry.setMaxlength(2);
		this.custParentCountry.setMandatoryStyle(false);
		this.custParentCountry.setModuleName("Country");
		this.custParentCountry.setValueColumn("CountryCode");
		this.custParentCountry.setDescColumn("CountryDesc");
		this.custParentCountry.setValidateColumns(new String[] { "CountryCode" });

		this.custSubSector.setMaxlength(8);
		this.custSubSector.setMandatoryStyle(false);
		this.custSubSector.setModuleName("SubSector");
		this.custSubSector.setValueColumn("SubSectorCode");
		this.custSubSector.setDescColumn("SubSectorDesc");
		this.custSubSector.setValidateColumns(new String[] { "SubSectorCode" });

		this.custSubSegment.setMaxlength(8);
		this.custSubSegment.setMandatoryStyle(false);
		this.custSubSegment.setModuleName("SubSegment");
		this.custSubSegment.setValueColumn("SubSegmentCode");
		this.custSubSegment.setDescColumn("SubSegmentDesc");
		this.custSubSegment.setValidateColumns(new String[] { "SubSegmentCode" });

		this.custSegment.setMaxlength(8);
		this.custSegment.setMandatoryStyle(false);
		this.custSegment.setModuleName("Segment");
		this.custSegment.setValueColumn("SegmentCode");
		this.custSegment.setDescColumn("SegmentDesc");
		this.custSegment.setValidateColumns(new String[] { "SegmentCode" });

		this.custRO1.setTextBoxWidth(121);
		if (!isReadOnly("CustomerDialog_custRO1")) {
			this.custRO1.setMandatoryStyle(true);
		}
		this.custRO1.setModuleName("SourceOfficer");
		this.custRO1.setValueColumn("DealerName");
		this.custRO1.setDescColumn("DealerCity");
		this.custRO1.setValidateColumns(new String[] { "DealerName" });

		this.applicationNo.setMaxlength(LengthConstants.LEN_REF);

		if (SysParamUtil.isAllowed("CUST_GST_TAB_REQUIRED")) {
			this.tabGstDetails.setVisible(true);
		}

		if (ImplementationConstants.ALLOW_GST_DETAILS) {
			this.tabCustGstDetails.setVisible(true);
			this.tp_custGstDetails.setVisible(true);
		}

		if (isWorkFlowEnabled()) {
			this.gb_Action.setVisible(true);
		} else {
			this.gb_Action.setVisible(false);
		}

		if (SysParamUtil.isAllowed(SMTParameterConstants.CUST_CARD_SALES_REQ)) {
			this.tp_CardSales.setVisible(true);
			this.tabCardSaleDetails.setVisible(true);
		} else {
			this.tp_CardSales.setVisible(false);
			this.tabCardSaleDetails.setVisible(false);
		}
		this.ckycOrRefNo.setMaxlength(14);
		this.otherCaste.setMaxlength(20);
		this.otherReligion.setMaxlength(20);
		this.otherCaste.setReadonly(true);
		this.otherReligion.setReadonly(true);
		// Customer Income and expense Inline Edit
		this.listBoxCustomerIncome.setVisible(false);
		this.listBoxCustomerIncomeInLineEdit.setVisible(true);

		// PhoneNumbers Inline Edit
		this.listBoxCustomerPhoneNumbers.setVisible(false);
		this.listBoxCustomerPhoneNumbersInlineEdit.setVisible(true);

		// Email Inline Edit
		this.listBoxCustomerEmails.setVisible(false);
		this.listBoxCustomerEmailsInlineEdit.setVisible(true);

		// setting visible false for new customer
		this.btnUploadExternalLiability.setVisible(false);
		this.btnDownloadExternalLiability.setVisible(false);
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
		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnSave"));

		// Customer related List Buttons
		this.btnNew_CustomerRatings
				.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_NewCustomerRatings"));
		this.btnNew_CustomerEmploymentDetail
				.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_NewCustomerAddress"));
		this.btnNew_CustomerIncome.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_NewCustomerIncome"));
		this.btnNew_DirectorDetail
				.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_NewCustomerDirectorDetails"));

		this.btnNew_CustomerDocuments
				.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_NewCustomerDocuments"));
		this.btnNew_CustomerAddress
				.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_NewCustomerAddress"));
		this.btnNew_CustomerPhoneNumber
				.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_NewCustomerPhoneNumbers"));
		this.btnNew_CustomerEmail.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_NewCustomerEmail"));
		this.btnNew_BankInformation
				.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_NewBankInformation"));
		this.btnNew_ChequeInformation
				.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_NewChequeInformation"));
		this.btnNew_ExternalLiability
				.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_NewExternalLiability"));
		this.btnNew_CardSalesInformation
				.setVisible(getUserWorkspace().isAllowed("CustomerCardSalesInfo_NewCardDetails"));
		this.btnNew_CustomerGSTDetails
				.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_NewCustomerGstDetails"));
		this.btnNew_GSTDetails.setVisible(getUserWorkspace().isAllowed("btnNew_CustomerDialog_GSTDetails"));
		validateCustDocs = getUserWorkspace().isAllowed("button_CustomerDialog_NewCustomerDocuments");
		this.btnUploadExternalLiability
				.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnUploadExternalLiability"));
		this.btnDownloadExternalLiability
				.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnDownloadExternalLiability"));
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		try {
			doSave();
		} catch (AppException e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, window_CustomerDialog);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnDelete(Event event) {
		logger.debug(Literal.ENTERING);
		try {
			doDelete();
		} catch (InterfaceException e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
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
		doClose(this.btnSave.isVisible());

	}

	protected void doPostClose() {
		if (extendedFieldCtrl != null && customerDetails.getExtendedFieldHeader() != null) {
			extendedFieldCtrl.deAllocateAuthorities();
		}
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.customerDetails.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomer Customer
	 */

	public void doWriteBeanToComponents(CustomerDetails aCustomerDetails) {
		logger.debug("Entering");
		Customer aCustomer = aCustomerDetails.getCustomer();

		// Data filling for Gender, Salutation, Marital Status Combobox
		fillComboBox(this.custGenderCode, aCustomer.getCustGenderCode(), PennantAppUtil.getGenderCodes(), "");
		fillComboBox(this.custSalutationCode, aCustomer.getCustSalutationCode(),
				PennantAppUtil.getSalutationCodes(aCustomer.getCustGenderCode()), "");
		fillComboBox(this.custMaritalSts, aCustomer.getCustMaritalSts(),
				PennantAppUtil.getMaritalStsTypes(aCustomer.getCustGenderCode()), "");

		this.target.setValue(aCustomer.getCustAddlVar82());
		this.custCIF.setValue(StringUtils.trimToEmpty(aCustomer.getCustCIF()));
		this.custCoreBank.setValue(aCustomer.getCustCoreBank());
		this.custFirstName.setValue(StringUtils.trimToEmpty(aCustomer.getCustFName()));
		this.custMiddleName.setValue(StringUtils.trimToEmpty(aCustomer.getCustMName()));
		this.custLastName.setValue(StringUtils.trimToEmpty(aCustomer.getCustLName()));
		this.applicationNo.setValue(aCustomer.getApplicationNo());
		if (isRetailCustomer) {
			this.custShrtName.setValue(PennantApplicationUtil.getFullName(aCustomer.getCustFName(),
					aCustomer.getCustMName(), aCustomer.getCustLName()));

			this.grid_ExtendedDetails.setVisible(true);

			StringBuilder subCategoryExcludeFields = new StringBuilder();
			subCategoryExcludeFields.append(",".concat(PennantConstants.SUBCATEGORY_DOMESTIC).concat(","));
			subCategoryExcludeFields.append(PennantConstants.SUBCATEGORY_NRI.concat(","));
			// Sub categories
			fillComboBox(this.subCategory, aCustomer.getSubCategory(), PennantStaticListUtil.getSubCategoriesList(),
					subCategoryExcludeFields.toString());

			fillComboBox(this.natureOfBusiness, aCustomer.getNatureOfBusiness(),
					PennantStaticListUtil.getNatureofBusinessList(), "");
			fillComboBox(this.entityType, aCustomer.getEntityType(), PennantStaticListUtil.getEntityTypeList(), "");
			setEmpType(aCustomer.getSubCategory());
			if (!aCustomer.isNewRecord()) {
				// Caste
				this.caste.setValue(StringUtils.trimToEmpty(aCustomer.getCasteCode()),
						StringUtils.trimToEmpty(aCustomer.getCasteDesc()));
				this.caste.setAttribute("CastId", aCustomer.getCasteId());

				// Religion
				this.religion.setValue(StringUtils.trimToEmpty(aCustomer.getReligionCode()),
						StringUtils.trimToEmpty(aCustomer.getReligionDesc()));
				this.religion.setAttribute("ReligionId", aCustomer.getReligionId());
			}
		} else {
			this.custShrtName.setValue(StringUtils.trimToEmpty(aCustomer.getCustShrtName()));
			this.grid_ExtendedDetails.setVisible(false);
			fillComboBox(this.entityType, aCustomer.getEntityType(), PennantStaticListUtil.getEntityTypeList(), "");
		}

		this.custLocalLngName.setValue(StringUtils.trimToEmpty(aCustomer.getCustShrtNameLclLng()));
		this.motherMaidenName.setValue(StringUtils.trimToEmpty(aCustomer.getCustMotherMaiden()));
		this.custCtgCode.setValue(aCustomer.getCustCtgCode());
		this.custDftBranch.setValue(aCustomer.getCustDftBranch());
		this.custTypeCode.setValue(aCustomer.getCustTypeCode());
		this.custBaseCcy.setValue(aCustomer.getCustBaseCcy());
		this.custNationality.setValue(aCustomer.getCustNationality());
		this.custLng.setValue(aCustomer.getCustLng());
		this.custSts.setValue(aCustomer.getCustSts());
		this.custSector.setValue(aCustomer.getCustSector());
		this.custIndustry.setValue(aCustomer.getCustIndustry());
		this.custCOB.setValue(aCustomer.getCustCOB());
		this.custDOB.setValue(aCustomer.getCustDOB());
		this.noOfDependents.setValue(aCustomer.getNoOfDependents());
		this.label_CustomerDialog_EIDName.setValue(StringUtils.trimToEmpty(aCustomer.getPrimaryIdName()));

		if (!StringUtils.isEmpty(aCustomer.getPrimaryIdName()) && StringUtils.isEmpty(aCustomer.getCustShrtName())) {
			renderCustFullName(aCustomer.getPrimaryIdName());
		}

		this.custRO1.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustRO1Name()), "");// FIXME
		this.custRO1.setAttribute("DealerId", aCustomer.getCustRO1());

		this.custTradeLicenceNum.setValue(aCustomer.getCustTradeLicenceNum());
		this.custRelatedParty.setValue(StringUtils.trimToEmpty(aCustomer.getCustAddlVar83()));
		this.custIsStaff.setChecked(aCustomer.isCustIsStaff());
		this.custDSACode.setValue(aCustomer.getCustDSA());
		this.custDSADept.setValue(aCustomer.getCustDSADept());
		this.custRiskCountry.setValue(aCustomer.getCustRiskCountry());
		this.custParentCountry.setValue(aCustomer.getCustParentCountry());

		this.custDSADept.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustDSADeptName()));
		this.custParentCountry.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustParentCountryName()));
		this.custRiskCountry.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustRiskCountryName()));
		this.custCtgCode.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustCtgCodeName()));
		this.custDftBranch.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustDftBranchName()));
		this.custTypeCode.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustTypeCodeName()));
		this.custBaseCcy.setDescription(CurrencyUtil.getCcyDesc(aCustomer.getCustBaseCcy()));
		this.custNationality.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustNationalityName()));
		this.custLng.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustLngName()));
		this.custSector.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustSectorName()));
		this.custIndustry.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustIndustryName()));
		this.custCOB.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustCOBName()));
		this.target.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescTargetName()));
		this.salariedCustomer.setChecked(aCustomer.isSalariedCustomer());

		if (!aCustomer.isNewRecord()) {
			this.custGroupId.setAttribute("CustGroupId", aCustomer.getCustGroupID());
			if (StringUtils.isNotEmpty(StringUtils.trimToEmpty(aCustomer.getLovDescCustGroupCode()))) {
				this.custGroupId.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustGroupCode()),
						StringUtils.trimToEmpty(aCustomer.getLovDesccustGroupIDName()));
			} else {
				onFulfillCustGroupId();
			}
			fillComboBox(this.residentialStatus, aCustomer.getResidentialStatus(),
					PennantStaticListUtil.getResidentialStsList(), ",MN,PIO,");
		} else {
			fillComboBox(this.residentialStatus, PennantConstants.RESIDENT,
					PennantStaticListUtil.getResidentialStsList(), ",MN,PIO,");
		}
		this.dnd.setChecked(aCustomer.isDnd());
		this.vip.setChecked(aCustomer.isVip());

		doSetSegmentCode(aCustomer.getCustTypeCode());
		aCustomer.getCustCtgCode();
		doSetCustTypeFilters(aCustomer.getCustCtgCode());
		if (StringUtils.isEmpty(this.custSegment.getValue())
				|| PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
			this.custSubSegment.setReadonly(true);
			this.custSubSegment.setButtonDisabled(true);
			this.custSubSegment.setValue("");
			this.custSubSegment.setDescription("");

		} else if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
			this.custSubSegment.setReadonly(true);
			this.custSubSegment.setButtonDisabled(true);
			this.custSubSegment.setValue(aCustomer.getCustSubSegment());
			this.custSubSegment.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustSubSegmentName()));
		} else {
			this.custSubSegment.setReadonly(isReadOnly("CustomerDialog_custSubSegment"));
			this.custSubSegment.setButtonDisabled(isReadOnly("CustomerDialog_custSubSegment"));
			this.custSubSegment.setValue(aCustomer.getCustSubSegment());
			this.custSubSegment.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustSubSegmentName()));
		}

		if (StringUtils.isEmpty(this.custSector.getValue())) {
			this.custSubSector.setReadonly(true);
			this.custSubSector.setButtonDisabled(true);
			this.custSubSector.setValue("");
			this.custSubSector.setDescription("");
		} else if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
			this.custSubSector.setReadonly(true);
			this.custSubSector.setButtonDisabled(true);
			this.custSubSector.setValue(aCustomer.getCustSubSector());
			this.custSubSector.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustSubSectorName()));
		} else {
			this.custSubSector
					.setFilters(new Filter[] { new Filter("SectorCode", aCustomer.getCustSector(), Filter.OP_EQUAL) });
			this.custSubSector.setReadonly(isReadOnly("CustomerDialog_custIndustry"));
			this.custSubSector.setButtonDisabled(isReadOnly("CustomerDialog_custIndustry"));
			this.custSubSector.setValue(aCustomer.getCustSubSector());
			this.custSubSector.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustSubSectorName()));
		}

		if (this.custIsStaff.isChecked()) {
			this.custStaffID.setValue(aCustomer.getCustStaffID());
			this.custStaffID.setReadonly(false);
		} else {
			this.custStaffID.setReadonly(true);
			this.custStaffID.setValue("");
		}

		// Set Customer Employee Details
		CustEmployeeDetail custEmployeeDetail = aCustomerDetails.getCustEmployeeDetail();
		if (custEmployeeDetail == null) {
			custEmployeeDetail = new CustEmployeeDetail();
			aCustomerDetails.setCustEmployeeDetail(custEmployeeDetail);
		}
		empStatus_Temp = custEmployeeDetail.getEmpStatus();
		this.empStatus.setValue(custEmployeeDetail.getEmpStatus());
		this.empStatus.setDescription(custEmployeeDetail.getLovDescEmpStatus());
		this.empSector.setValue(custEmployeeDetail.getEmpSector());
		this.empSector.setDescription(custEmployeeDetail.getLovDescEmpSector());
		this.profession.setValue(custEmployeeDetail.getProfession());
		this.profession.setDescription(custEmployeeDetail.getLovDescProfession());
		this.empStatus.setValue(custEmployeeDetail.getEmpStatus());
		this.empStatus.setDescription(custEmployeeDetail.getLovDescEmpStatus());
		this.empName.setValue(String.valueOf(custEmployeeDetail.getEmpName()));
		this.empName.setDescription(custEmployeeDetail.getLovDescEmpName());
		this.empAlocType = custEmployeeDetail.getEmpAlocType();
		this.empNameOther.setValue(custEmployeeDetail.getEmpNameForOthers());
		this.empFrom.setValue(custEmployeeDetail.getEmpFrom());
		this.empDesg.setValue(custEmployeeDetail.getEmpDesg());
		this.empDesg.setDescription(custEmployeeDetail.getLovDescEmpDesg());
		this.empDept.setValue(custEmployeeDetail.getEmpDept());
		this.empDept.setDescription(custEmployeeDetail.getLovDescEmpDept());
		this.monthlyIncome
				.setValue(PennantApplicationUtil.formateAmount(custEmployeeDetail.getMonthlyIncome(), ccyFormatter));
		this.otherIncome.setValue(custEmployeeDetail.getOtherIncome());
		this.otherIncome.setDescription(custEmployeeDetail.getLovDescOtherIncome());
		this.additionalIncome
				.setValue(PennantApplicationUtil.formateAmount(custEmployeeDetail.getAdditionalIncome(), ccyFormatter));
		setMandatoryIDNumber(aCustomer.getCustCRCPR());
		doSetEmpStatusProperties(custEmployeeDetail.getEmpStatus());
		if (StringUtils.trimToEmpty(this.empName.getDescription())
				.equalsIgnoreCase(PennantConstants.EmploymentName_OTHERS)) {
			this.hbox_empNameOther.setVisible(true);
			this.label_empNameOther.setVisible(true);
		} else {
			this.hbox_empNameOther.setVisible(false);
			this.label_empNameOther.setVisible(false);
		}

		empName_Temp = this.empName.getValue();
		custBaseCcy_Temp = this.custBaseCcy.getValue();
		// new fields
		// this.custFlags.setValue(aCustomer.getCustFlag(), StringUtils.trimToEmpty(aCustomer.getLovDescCustFlag()));
		this.profession.setValue(aCustomer.getCustProfession(), aCustomer.getLovDescCustProfessionName());
		this.custQualification.setValue(aCustomer.getQualification(), aCustomer.getLovDescQualification());

		if (PennantConstants.OTHER.equalsIgnoreCase(aCustomer.getCasteCode())) {
			this.space_OtherCaste.setSclass(PennantConstants.mandateSclass);
			this.otherCaste.setValue(aCustomer.getOtherCaste());
		} else { // Othercaste setreadonly True if caste is not Other
			this.otherCaste.setReadonly(true);
		}

		if (PennantConstants.OTHER.equalsIgnoreCase(aCustomer.getReligionCode())) {
			this.space_OtherReligion.setSclass(PennantConstants.mandateSclass);
			this.otherReligion.setValue(aCustomer.getOtherReligion());
		} else { // OtherReligion setreadonly True if Religion is not Other
			this.otherReligion.setReadonly(true);
		}
		this.custSegment.setValue(aCustomer.getCustSegment(), aCustomer.getLovDescCustSegmentName());
		onChangeEmploymentType();

		// Filling KYC Details
		doFillCustomerEmploymentDetail(aCustomerDetails.getEmploymentDetailsList());
		doFillCustomerIncome(aCustomerDetails.getCustomerIncomeList());
		doFillCustomerRatings(aCustomerDetails.getRatingsList());
		doFillDocumentDetails(aCustomerDetails.getCustomerDocumentsList());
		doFillCustomerAddressDetails(aCustomerDetails.getAddressList());
		doFillCustomerPhoneNumberDetails(aCustomerDetails.getCustomerPhoneNumList());
		doFillCustomerEmailDetails(aCustomerDetails.getCustomerEMailList());
		// Filling Banking Details
		doFillCustomerBankInfoDetails(aCustomerDetails.getCustomerBankInfoList());
		doFillCustomerChequeInfoDetails(aCustomerDetails.getCustomerChequeInfoList());
		doFillCustomerExtLiabilityDetails(aCustomerDetails.getCustomerExtLiabilityList());
		doFillCustFinanceExposureDetails(aCustomerDetails.getCustFinanceExposureList());
		doFillCustomerCardSalesInfoDetails(aCustomerDetails.getCustCardSales());

		// customer gst details
		doFillCustomerGstDetails(aCustomerDetails.getCustomerGstList());
		// multiple gst numbers added for one customer with different state codes
		doFillGstDetails(aCustomerDetails.getGstDetailsList());
		// Extended Field Details
		appendExtendedFieldDetails(aCustomerDetails);
		// Set Income values only for GHF
		updateIncomeValue();

		if (!StringUtils.equals(PennantConstants.PFF_CUSTCTG_INDIV, customerDetails.getCustomer().getCustCtgCode())) {
			doFillCustomerDirectory(aCustomerDetails.getCustomerDirectorList());
			doSetShareHoldersDesignationCode(aCustomerDetails.getCustomerDirectorList());
		}

		processDateDiff(this.custDOB.getValue(), this.age);
		processDateDiff(this.empFrom.getValue(), this.exp);

		this.recordStatus.setValue(aCustomer.getRecordStatus());

		if (customerDetails.isNewRecord()) {
			aCustomer.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			aCustomer.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
		}

		logger.debug("Leaving");
	}

	/**
	 * This method is for append extended field details
	 */
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

			this.extendedFieldCtrl.createTab(tabsIndexCenter, tabpanelsBoxIndexCenter, "424px", true);
			aCustomerDetails.setExtendedFieldHeader(extendedFieldHeader);
			aCustomerDetails.setExtendedFieldRender(extendedFieldRender);

			if (aCustomerDetails.getBefImage() != null) {
				aCustomerDetails.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				aCustomerDetails.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}
			extendedFieldCtrl.setCcyFormat(2);
			extendedFieldCtrl.setReadOnly(enqiryModule);
			// extendedFieldCtrl.setReadOnly(isReadOnly("CustomerDialog_custFirstName"));
			extendedFieldCtrl.setWindow(this.window_CustomerDialog);

			if (isFinanceProcess || fromLoan) {
				extendedFieldCtrl.setTabHeight(borderLayoutHeight - 220);
			} else {
				extendedFieldCtrl.setTabHeight(borderLayoutHeight - 90);
			} // for getting rights in ExtendeFieldGenerator these two fields
				// required.
			extendedFieldCtrl.setUserWorkspace(getUserWorkspace());
			extendedFieldCtrl.setUserRole(getRole());

			extendedFieldCtrl.setExtendedFieldDetailsService(getExtendedFieldDetailsService());

			extendedFieldCtrl.render();
		} catch (Exception e) {
			logger.error("Exception", e);
		}

		logger.debug("Leaving");
	}

	private void updateIncomeValue() {
		Map<String, Object> map = incomeAndExpenseCtrl.calculateTotal(this.listBoxCustomerIncomeInLineEdit,
				ccyFormatter);

		extendedFieldCtrl.setValues(map);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomer
	 */
	public void doWriteComponentsToBean(CustomerDetails aCustomerDetails, Tab custTab) {
		logger.debug("Entering");
		doClearErrorMessage();
		doSetValidation();
		doSetLOVValidation();
		Customer aCustomer = aCustomerDetails.getCustomer();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomer.setCustCIF(this.custCIF.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustCoreBank(this.custCoreBank.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustFName(this.custFirstName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustMName(this.custMiddleName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustLName(this.custLastName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setApplicationNo(this.applicationNo.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isRetailCustomer) {
				aCustomer.setCustShrtName(PennantApplicationUtil.getFullName(this.custFirstName.getValue(),
						this.custMiddleName.getValue(), this.custLastName.getValue()));
			} else {
				aCustomer.setCustShrtName(this.custShrtName.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustShrtNameLclLng(this.custLocalLngName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustMotherMaiden(this.motherMaidenName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustCtgCodeName(this.custCtgCode.getDescription());
			aCustomer.setCustCtgCode(StringUtils.trimToNull(this.custCtgCode.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustDftBranchName(this.custDftBranch.getDescription());
			aCustomer.setCustDftBranch(StringUtils.trimToNull(this.custDftBranch.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustTypeCodeName(this.custTypeCode.getDescription());
			aCustomer.setCustTypeCode(StringUtils.trimToNull(this.custTypeCode.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setCustBaseCcy(StringUtils.trimToNull(this.custBaseCcy.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setLovDescCustNationalityName(this.custNationality.getDescription());
			aCustomer.setCustNationality(StringUtils.trimToNull(this.custNationality.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setLovDescCustLngName(this.custLng.getDescription());
			aCustomer.setCustLng(StringUtils.trimToNull(this.custLng.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setCustSts(this.custSts.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setLovDescCustSectorName(this.custSector.getDescription());
			aCustomer.setCustSector(StringUtils.trimToNull(this.custSector.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setLovDescCustIndustryName(this.custIndustry.getDescription());
			aCustomer.setCustIndustry(StringUtils.trimToNull(this.custIndustry.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setLovDescCustSegmentName(this.custSegment.getDescription());
			aCustomer.setCustSegment(StringUtils.trimToNull(this.custSegment.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setLovDescCustCOBName(this.custCOB.getDescription());
			aCustomer.setCustCOB(StringUtils.trimToNull(this.custCOB.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setLovDescCustRO1Name(this.custRO1.getValidatedValue());
			Object object = this.custRO1.getAttribute("DealerId");
			if (object != null) {
				aCustomer.setCustRO1(Long.parseLong(object.toString()));
			} else {
				aCustomer.setCustRO1(0);
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setCustDOB(this.custDOB.getValue());
			aCustomer.setCustomerAge(processDateDiff(this.custDOB.getValue(), this.age));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("#".equals(getComboboxValue(this.custSalutationCode))) {
				if (isRetailCustomer && validateAllDetails && this.custSalutationCode.isVisible()
						&& !this.custSalutationCode.isDisabled()) {
					throw new WrongValueException(this.custSalutationCode, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_CustomerDialog_CustSalutationCode.value") }));
				} else {
					aCustomer.setCustSalutationCode(null);
				}
			} else {
				aCustomer.setCustSalutationCode(getComboboxValue(this.custSalutationCode));
				aCustomer.setLovDescCustSalutationCodeName(this.custSalutationCode.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setCustAddlVar82(StringUtils.trimToNull(this.target.getValidatedValue()));
			aCustomer.setLovDescTargetName(this.target.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("#".equals(getComboboxValue(this.custGenderCode))) {
				if (isRetailCustomer && validateAllDetails && this.custGenderCode.isVisible()
						&& !this.custGenderCode.isDisabled()) {
					throw new WrongValueException(this.custGenderCode, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_CustomerDialog_CustGenderCode.value") }));
				} else {
					aCustomer.setCustGenderCode(null);
				}
			} else {
				aCustomer.setCustGenderCode(getComboboxValue(this.custGenderCode));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ("#".equals(getComboboxValue(this.custMaritalSts))) {
				if (isRetailCustomer && validateAllDetails && this.custMaritalSts.isVisible()
						&& !this.custMaritalSts.isDisabled()) {
					throw new WrongValueException(this.custMaritalSts, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_CustomerDialog_CustMaritalSts.value") }));
				} else {
					aCustomer.setCustMaritalSts(null);
				}
			} else {
				aCustomer.setCustMaritalSts(getComboboxValue(this.custMaritalSts));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setNoOfDependents(this.noOfDependents.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setSalariedCustomer(this.salariedCustomer.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setDnd(this.dnd.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setVip(this.vip.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isRetailCustomer) {
				aCustomer.setCustCRCPR(PennantApplicationUtil.unFormatEIDNumber(this.eidNumber.getValue()));
				aCustomer.setCustFName(this.custFirstName.getValue());
				aCustomer.setCustLName(this.custLastName.getValue());
				aCustomer.setCustMName(this.custMiddleName.getValue());
			} else if (this.eidNumber.getValue().isEmpty()) {
				aCustomer.setCustCRCPR(null);
			} else {
				aCustomer.setCustCRCPR(this.eidNumber.getValue());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.isNotBlank(aCustomer.getCustCRCPR()) && !this.eidNumber.isReadonly()
					&& StringUtils.isNotBlank(aCustomer.getCustTypeCode())) {

				String panFourthLetter = StringUtils.substring(aCustomer.getCustCRCPR(), 3, 4);
				/*
				 * if (!custTypePANMappingService.isValidPANLetter(aCustomer.getCustTypeCode(),
				 * aCustomer.getCustCtgCode(), panFourthLetter)) { throw new WrongValueException(this.eidNumber,
				 * aCustomer.getCustCRCPR() + Labels.getLabel("label_CustTypePANMapping_panValidation.value")); }
				 */
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setCustTradeLicenceNum(this.custTradeLicenceNum.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar83(this.custRelatedParty.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setLovDesccustGroupIDName(this.custGroupId.getDescription());
			aCustomer.setLovDescCustGroupCode(this.custGroupId.getValue());
			this.custGroupId.getValidatedValue();
			Object object = this.custGroupId.getAttribute("CustGroupId");

			if (object != null && !this.custGroupId.getValidatedValue().isEmpty()) {
				aCustomer.setCustGroupID(Long.parseLong(object.toString()));
			} else {
				aCustomer.setCustGroupID(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setCustIsStaff(this.custIsStaff.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setCustStaffID(this.custStaffID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustDSA(this.custDSACode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setLovDescCustDSADeptName(this.custDSADept.getDescription());
			aCustomer.setCustDSADept(StringUtils.trimToNull(this.custDSADept.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setLovDescCustParentCountryName(this.custParentCountry.getDescription());
			aCustomer.setCustParentCountry(StringUtils.trimToNull(this.custParentCountry.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setLovDescCustRiskCountryName(this.custRiskCountry.getDescription());
			aCustomer.setCustRiskCountry(StringUtils.trimToNull(this.custRiskCountry.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setLovDescCustSubSectorName(this.custSubSector.getDescription());
			aCustomer.setCustSubSector(StringUtils.trimToNull(this.custSubSector.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setLovDescCustSubSegmentName(this.custSubSegment.getDescription());
			aCustomer.setCustSubSegment(StringUtils.trimToNull(this.custSubSegment.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.grid_ExtendedDetails.isVisible()) {
			// Caste
			try {
				aCustomer.setCasteDesc(this.caste.getDescription());
				aCustomer.setCasteCode(this.caste.getValue());
				this.caste.getValidatedValue();
				Object object = this.caste.getAttribute("CastId");
				if (object != null) {
					aCustomer.setCasteId(Long.parseLong(object.toString()));
				} else {
					aCustomer.setCasteId(0);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			// Religion
			try {
				aCustomer.setReligionDesc(this.religion.getDescription());
				aCustomer.setReligionCode(this.religion.getValue());
				this.religion.getValidatedValue();
				Object object = this.religion.getAttribute("ReligionId");
				if (object != null) {
					aCustomer.setReligionId(Long.parseLong(object.toString()));
				} else {
					aCustomer.setReligionId(0);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aCustomer.setSubCategory(getComboboxValue(this.subCategory));
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			aCustomer.setCasteId(0);
			aCustomer.setReligionId(0);
			aCustomer.setSubCategory(null);
		}
		// new fields added as per the HL Product
		if (isRetailCustomer) {
			/*
			 * try { aCustomer.setCustFlag(StringUtils.trimToNull(this.custFlags.getValue())); } catch
			 * (WrongValueException we) { wve.add(we); }
			 */

			/*
			 * try { if ("#".equals(getComboboxValue(this.custResidentialStstus))) { if (validateAllDetails &&
			 * this.custResidentialStstus.isVisible() && !this.custResidentialStstus.isDisabled()) { throw new
			 * WrongValueException(this.custResidentialStstus, Labels.getLabel("STATIC_INVALID", new String[] {
			 * Labels.getLabel("label_CustomerDialog_ResidentialStstus.value") })); } } else {
			 * aCustomer.setCustResidentialSts(getComboboxValue(this.custResidentialStstus)); } } catch
			 * (WrongValueException we) { wve.add(we); }
			 */

			try {
				aCustomer.setNatureOfBusiness(getComboboxValue(this.natureOfBusiness));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aCustomer.setCustProfession(StringUtils.trimToNull(this.profession.getValidatedValue()));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aCustomer.setQualification(StringUtils.trimToNull(this.custQualification.getValidatedValue()));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aCustomer.setOtherCaste(this.otherCaste.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aCustomer.setOtherReligion(this.otherReligion.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		// corporate customer
		try {
			if (!isRetailCustomer) {
				aCustomer.setEntityType(getComboboxValue(this.entityType));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setResidentialStatus(getComboboxValue(this.residentialStatus));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		this.basicDetails.setSelected(true);
		showErrorDetails(wve, custTab, basicDetails);

		doSetValidation();
		doSetLOVValidation();
		// Below nullified fields are not using in screen
		// aCustomer.setCustSubSector(null);
		aCustomer.setCustEmpSts(null);

		// Set Customer Employee Details
		CustEmployeeDetail custEmployeeDetail = aCustomerDetails.getCustEmployeeDetail();
		if (!ImplementationConstants.ALLOW_MULTIPLE_EMPLOYMENTS) {
			if (isRetailCustomer) {
				if (custEmployeeDetail == null) {
					custEmployeeDetail = new CustEmployeeDetail();
				}
				try {
					custEmployeeDetail.setLovDescEmpStatus(this.empStatus.getDescription());
					custEmployeeDetail.setEmpStatus(StringUtils.trimToNull(this.empStatus.getValidatedValue()));
					aCustomer.setCustEmpSts(StringUtils.trimToNull(this.empStatus.getValidatedValue()));
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					custEmployeeDetail.setLovDescEmpSector(this.empSector.getDescription());
					custEmployeeDetail.setEmpSector(StringUtils.trimToNull(this.empSector.getValidatedValue()));
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					custEmployeeDetail.setLovDescProfession(this.profession.getDescription());
					custEmployeeDetail.setProfession(StringUtils.trimToNull(this.profession.getValidatedValue()));
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					custEmployeeDetail.setLovDescEmpName(this.empName.getDescription());
					custEmployeeDetail.setEmpName(StringUtils.isEmpty(this.empName.getValidatedValue()) ? 0
							: Long.parseLong(this.empName.getValue()));
					custEmployeeDetail.setEmpAlocType(this.empAlocType);
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					custEmployeeDetail.setEmpNameForOthers(this.empNameOther.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					if (this.empFrom.getValue() != null && this.gp_CustEmployeeDetails.isVisible()) {
						if (!this.custDOB.isDisabled() && this.custDOB.getValue() != null
								&& !this.empFrom.getValue().after(this.custDOB.getValue())) {
							throw new WrongValueException(this.empFrom, Labels.getLabel("DATE_ALLOWED_AFTER",
									new String[] { getEmpFromLabel(), isRetailCustomer
											? Labels.getLabel("label_CustomerDialog_CustDOB.value")
											: Labels.getLabel("label_CustomerDialog_CustDateOfIncorporation.value") }));
						}
						custEmployeeDetail.setEmpFrom(this.empFrom.getValue());
					} else {
						custEmployeeDetail.setEmpFrom(null);
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					custEmployeeDetail.setLovDescEmpDesg(this.empDesg.getDescription());
					custEmployeeDetail.setEmpDesg(StringUtils.trimToNull(this.empDesg.getValidatedValue()));
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					custEmployeeDetail.setLovDescEmpDept(this.empDept.getDescription());
					custEmployeeDetail.setEmpDept(StringUtils.trimToNull(this.empDept.getValidatedValue()));
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					custEmployeeDetail.setMonthlyIncome(
							PennantApplicationUtil.unFormateAmount(this.monthlyIncome.getActualValue(), ccyFormatter));
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					custEmployeeDetail.setLovDescOtherIncome(this.otherIncome.getDescription());
					custEmployeeDetail.setOtherIncome(StringUtils.trimToNull(this.otherIncome.getValidatedValue()));
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					custEmployeeDetail.setAdditionalIncome(PennantApplicationUtil
							.unFormateAmount(this.additionalIncome.getActualValue(), ccyFormatter));
				} catch (WrongValueException we) {
					wve.add(we);
				}
			}
		}
		this.tabkYCDetails.setSelected(true);
		showErrorDetails(wve, custTab, tabkYCDetails);
		if (ImplementationConstants.ALLOW_MULTIPLE_EMPLOYMENTS) {
			aCustomer.setCustTotalIncome(getCustTotIncomeExp(true));
		} else {
			aCustomer.setCustTotalIncome(
					custEmployeeDetail.getMonthlyIncome().add(custEmployeeDetail.getAdditionalIncome()));
		}
		aCustomer.setCustTotalExpense(getCustTotExpense().add(getCustTotIncomeExp(false)));

		aCustomer.setCustPassportNo(getCustDocID(PennantConstants.PASSPORT));

		if (StringUtils.isBlank(aCustomer.getCustSourceID())) {
			aCustomer.setCustSourceID(App.CODE);
		}

		// Extended Field validations
		extendedFieldCtrl.setParentTab(custTab);
		if (aCustomerDetails.getExtendedFieldHeader() != null) {
			aCustomerDetails.setExtendedFieldRender(extendedFieldCtrl.save(true, aCustomerDetails));
		}

		@SuppressWarnings("rawtypes")
		Map<String, List> customerPhoneNumbers = customerPhoneNumberInLineEditCtrl
				.prepareCustomerPhoneNumberData(aCustomerDetails, this.listBoxCustomerPhoneNumbersInlineEdit);
		if (customerPhoneNumbers.get("errorList") != null) {
			@SuppressWarnings("unchecked")
			List<WrongValueException> errorlist = (List<WrongValueException>) customerPhoneNumbers.get("errorList");
			showErrorDetails(errorlist, custTab, tabkYCDetails);
		}
		if (customerPhoneNumbers.get("customerPhoneNumbers") != null) {
			@SuppressWarnings("unchecked")
			List<CustomerPhoneNumber> customerPhoneNumberList = customerPhoneNumbers.get("customerPhoneNumbers");
			setCustomerPhoneNumberDetailList(customerPhoneNumberList);
		}

		aCustomer.setPhoneNumber(getMobileNumber());

		// In line Edit functionality for Customer Emails
		// HL change for Emails
		@SuppressWarnings("rawtypes")
		Map<String, List> customerEmails = customerEmailInlineEditCtrl.prepareCustomerEmailData(aCustomerDetails,
				this.listBoxCustomerEmailsInlineEdit);
		if (customerEmails.get("errorList") != null) {
			@SuppressWarnings("unchecked")
			ArrayList<WrongValueException> errorlist = (ArrayList<WrongValueException>) customerEmails.get("errorList");
			showErrorDetails(errorlist, custTab, tabkYCDetails);
		}
		if (customerEmails.get("customerEMails") != null) {
			@SuppressWarnings("unchecked")
			List<CustomerEMail> customerEMailList = customerEmails.get("customerEMails");
			setCustomerEmailDetailList(customerEMailList);
		}

		// In line Edit functionality for Customer Income Expense
		// HL change for financial
		@SuppressWarnings("rawtypes")
		Map<String, List> customerIncomes = incomeAndExpenseCtrl.prepareCustomerIncomeExpenseData(aCustomerDetails,
				this.listBoxCustomerIncomeInLineEdit, ccyFormatter,
				PennantConstants.MODULETYPE_ENQ.equals(this.moduleType));
		if (customerIncomes.get("errorList") != null) {
			@SuppressWarnings("unchecked")
			ArrayList<WrongValueException> errorlist = (ArrayList<WrongValueException>) customerIncomes
					.get("errorList");
			showErrorDetails(errorlist, custTab, tabfinancial);
		}
		if (customerIncomes.get("customerIncomes") != null) {
			@SuppressWarnings("unchecked")
			List<CustomerIncome> customerIncomeList = customerIncomes.get("customerIncomes");
			setIncomeList(customerIncomeList);
			if (ImplementationConstants.CUSTOMER_PAN_VALIDATION_STOP) {
				Map<String, Object> mapValues = customerDetails.getExtendedFieldRender().getMapValues();
				if (isFromCustomer) {
					usrAction = userAction.getSelectedItem().getValue().toString();
				}
				if (!usrAction.equals(PennantConstants.RCD_STATUS_CANCELLED)
						&& !usrAction.equals(PennantConstants.RCD_STATUS_REJECTED)
						&& !usrAction.equals(PennantConstants.RCD_STATUS_RESUBMITTED)) {
					if (mapValues.get("UCIC") != null && !mapValues.get("UCIC").equals("")) {
						String reference = extendedFieldDetailsService.getUCICNumber(
								aCustomerDetails.getExtendedFieldHeader().getSubModuleName(), mapValues.get("UCIC"));
						if (reference != null
								&& (!(aCustomerDetails.getCustomer().getCustCIF().equalsIgnoreCase(reference)))) {
							throw new AppException(
									"UCIC : " + mapValues.get("UCIC") + " already exists for CustCIF : " + reference);
						}
					}
				}

			}
		}

		// Set KYC details
		aCustomerDetails.setCustomer(ObjectUtil.clone(aCustomer));
		aCustomerDetails.setCustEmployeeDetail(ObjectUtil.clone(custEmployeeDetail));
		aCustomerDetails.setEmploymentDetailsList(ObjectUtil.clone(this.customerEmploymentDetailList));
		aCustomerDetails.setCustomerIncomeList(ObjectUtil.clone(this.incomeList));
		aCustomerDetails.setRatingsList(ObjectUtil.clone(this.ratingsList));
		aCustomerDetails.setCustomerDocumentsList(ObjectUtil.clone(this.customerDocumentDetailList));
		aCustomerDetails.setAddressList(ObjectUtil.clone(this.customerAddressDetailList));
		aCustomerDetails.setCustomerPhoneNumList(ObjectUtil.clone(this.customerPhoneNumberDetailList));
		aCustomerDetails.setCustomerEMailList(ObjectUtil.clone(this.customerEmailDetailList));
		// Set Banking details
		aCustomerDetails.setCustomerBankInfoList(ObjectUtil.clone(this.customerBankInfoDetailList));
		aCustomerDetails.setCustomerChequeInfoList(ObjectUtil.clone(this.customerChequeInfoDetailList));
		aCustomerDetails.setCustomerExtLiabilityList(ObjectUtil.clone(this.customerExtLiabilityDetailList));
		aCustomerDetails.setCustCardSales(ObjectUtil.clone(this.customerCardSales));
		// Custome Gst Details list
		aCustomerDetails.setCustomerGstList(ObjectUtil.clone(this.customerGstList));

		if (CollectionUtils.isNotEmpty(getGstDetailsList())) {
			for (GSTDetail detail : getGstDetailsList()) {
				Checkbox gstDefault = (Checkbox) this.listBoxCustomerGstDetails
						.getFellowIfAny("GstState_" + detail.getStateCode());
				if (gstDefault != null) {
					detail.setDefaultGST(gstDefault.isChecked());
					if (StringUtils.isEmpty(detail.getRecordType())) {
						detail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						detail.setVersion(detail.getVersion() + 1);
						detail.setNewRecord(true);
					}
				}
			}
		}
		aCustomerDetails.setGstDetailsList(getGstDetailsList());

		if (this.directorDetails.isVisible()) {
			aCustomerDetails.setCustomerDirectorList(this.directorList);
		}
		aCustomer.setMarginDeviation(marginDeviation);

		logger.debug("Leaving");
	}

	private BigDecimal getCustTotExpense() {
		logger.debug("Entering");
		BigDecimal custTotExpense = BigDecimal.ZERO;
		/*
		 * if (this.customerExtLiabilityDetailList != null && !this.customerExtLiabilityDetailList.isEmpty()) { for
		 * (CustomerExtLiability cusExtLiability : this.customerExtLiabilityDetailList) { if
		 * (!isDeleteRecord(cusExtLiability.getRecordType())) { custTotExpense =
		 * custTotExpense.add(cusExtLiability.getInstalmentAmount()); } } }
		 */
		logger.debug("Leaving");
		return custTotExpense;
	}

	private BigDecimal getCustTotIncomeExp(boolean isIncome) {
		logger.debug("Entering");
		BigDecimal custTotIncomeExp = BigDecimal.ZERO;
		if (this.incomeList != null && !this.incomeList.isEmpty()) {
			for (CustomerIncome custIncome : this.incomeList) {
				if (!isDeleteRecord(custIncome.getRecordType())) {
					if (isIncome) {
						if (StringUtils.equals(PennantConstants.INCOME, custIncome.getIncomeExpense())) {
							custTotIncomeExp = custTotIncomeExp.add(custIncome.getCalculatedAmount());
						}
					} else {
						if (StringUtils.equals(PennantConstants.EXPENSE, custIncome.getIncomeExpense())) {
							custTotIncomeExp = custTotIncomeExp.add(custIncome.getCalculatedAmount());
						}
					}
				}
			}
		}
		logger.debug("Leaving");
		return custTotIncomeExp;
	}

	private String getCustDocID(String docIDType) {
		logger.debug("Entering");
		if (this.customerDocumentDetailList != null && !this.customerDocumentDetailList.isEmpty()) {
			for (CustomerDocument document : this.customerDocumentDetailList) {
				if (StringUtils.trimToEmpty(document.getCustDocCategory()).equals(docIDType)
						&& !isDeleteRecord(document.getRecordType())) {
					return document.getCustDocTitle();
				}
			}
		}
		logger.debug("Leaving");
		return "";
	}

	private String getMobileNumber() {
		logger.debug("Entering");
		if (this.customerPhoneNumberDetailList != null && !this.customerPhoneNumberDetailList.isEmpty()) {
			for (CustomerPhoneNumber phoneNumber : this.customerPhoneNumberDetailList) {
				if (String.valueOf(phoneNumber.getPhoneTypePriority())
						.equals(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
					return PennantApplicationUtil.formatPhoneNumber(phoneNumber.getPhoneCountryCode(),
							phoneNumber.getPhoneAreaCode(), phoneNumber.getPhoneNumber());
				}
			}
		}
		logger.debug("Leaving");
		return "";
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(List<WrongValueException> wve, Tab parentTab, Tab childTab) {
		logger.debug("Entering");
		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			if (parentTab != null) {
				parentTab.setSelected(true);
			}
			childTab.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				Clients.scrollIntoView(wvea[i].getComponent());
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCustomer
	 */
	public void doShowDialog(CustomerDetails aCustomerDetails) {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aCustomerDetails.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
			} else {
				this.btnCtrl.setInitNew();
				if (!isNewCustCret) {
					this.btnDelete.setVisible(true);
				}
			}
			doEdit();
			if (!PennantConstants.RECORD_TYPE_NEW.equals(aCustomerDetails.getCustomer().getRecordType())) {
				this.btnUploadExternalLiability
						.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnUploadExternalLiability"));
				this.btnDownloadExternalLiability
						.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnDownloadExternalLiability"));
			}
		}
		this.custCIF.focus();

		if (isRetailCustomer) {
			this.label_CustomerDialog_CustNationality
					.setValue(Labels.getLabel("label_FinanceCustomerList_CustNationality.value"));
		} else {
			this.label_CustomerDialog_CustNationality
					.setValue(Labels.getLabel("label_CustomerDialog_CustNationality.value"));
		}

		try {
			doWriteBeanToComponents(aCustomerDetails);
			doResetFeeVariables();
			doCheckCibil();
			doSetCategoryProperties();
			doCheckEnquiry();

			this.btnCancel.setVisible(false);
			if (isFinanceProcess || isNotFinanceProcess || isEnqProcess || isNewCustCret) {
				this.north.setVisible(false);
				this.south.setVisible(false);
				try {
					if (financeMainDialogCtrl != null) {
						financeMainDialogCtrl.getClass().getMethod("setCustomerDialogCtrl", this.getClass())
								.invoke(financeMainDialogCtrl, this);
					}
					if (promotionPickListCtrl != null) {
						promotionPickListCtrl.getClass().getMethod("setCustomerDialogCtrl", this.getClass())
								.invoke(promotionPickListCtrl, this);
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				try {
					if (panel != null) {
						this.window_CustomerDialog.setHeight(borderLayoutHeight - 75 + "px");
						panel.appendChild(this.window_CustomerDialog);
					}
					if (groupbox != null) {
						this.window_CustomerDialog.setHeight(borderLayoutHeight - 75 + "px");
						groupbox.appendChild(this.window_CustomerDialog);
					}
					if (isNewCustCret) {
						this.north.setVisible(true);
						this.window_CustomerDialog.setHeight(borderLayoutHeight - 75 + "px");
						this.window_CustomerDialog.doModal();
					}
				} catch (UiException e) {
					logger.error("Exception: ", e);
					this.window_CustomerDialog.onClose();
				} catch (Exception e) {
					throw e;
				}
			} else {
				this.north.setVisible(true);
				this.south.setVisible(true);
				if (!PennantConstants.MODULETYPE_ENQ.equals(moduleType)) {
					setDialog(DialogType.EMBEDDED);
				} else {
					setDialog(DialogType.EMBEDDED);
				}

			}
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CustomerDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	private void doCheckCibil() {
		logger.debug(Literal.ENTERING);

		String roles = SysParamUtil.getValueAsString(SMTParameterConstants.ALLOW_CIBIL_VALIDATION_RULE);
		roles = StringUtils.trimToEmpty(roles);
		if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_CIBIL_REQUEST)) {
			String status = getCustomerDetails().getCustomer().getRecordStatus();
			if (fromLoan) {
				if ((roles.contains(getRole()) && isRetailCustomer)
						|| (getFinancedetail() != null && getFinancedetail().isNewRecord() && isRetailCustomer)) {
					this.btn_GenerateCibil.setVisible(true);
				}
			} else if (status != null && isFromCustomer) {
				if (isRetailCustomer && status.equals("Approved")) {
					this.btn_GenerateCibil.setVisible(true);
				} else {
					this.btn_GenerateCibil.setVisible(false);
				}
			}
		} else {
			this.btn_GenerateCibil.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetCategoryProperties() {
		logger.debug("Entering");
		if (isRetailCustomer) {
			this.row_FirstMiddleName.setVisible(true);
			this.label_CustomerDialog_CustLastName.setVisible(true);
			this.label_CustomerDialog_motherMaidenName.setVisible(true);
			this.hbox_CustLastName.setVisible(true);
			this.hbox_motherMaidenName.setVisible(true);
			this.row_GenderSalutation.setVisible(true);
			this.row_MartialDependents.setVisible(true);
			this.row_custDSA.setVisible(true);
			this.label_CustSubSegment.setVisible(true);
			this.custSubSegment.setVisible(true);
			this.label_CustomerDialog_CustReligion.setVisible(true);
			this.label_CustomerDialog_CustCaste.setVisible(true);
			this.label_CustomerDialog_subCategory.setVisible(true);
			this.hbox_SubCategory.setVisible(true);
			this.hbox_CustReligion.setVisible(true);
			this.hbox_CustCaste.setVisible(true);
			this.subCategory.setVisible(true);
			this.row_custStaff.setVisible(true);
			this.row_custCountry.setVisible(true);
			this.hbox_SalariedCustomer.setVisible(true);
			this.salariedCustomer.setVisible(true);
			this.label_OtherReligion.setVisible(true);
			this.label_OtherCaste.setVisible(true);
			this.hbox_CustOtherCaste.setVisible(true);
			this.hbox_CustOtherReligion.setVisible(true);
			// this.row_natueOfBusiness.setVisible(true);
			this.row_residentialsts.setVisible(true);
			this.row_qualification.setVisible(true);
			this.label_CustomerDialog_CustQualification.setVisible(true);
			// this.label_CustomerDialog_CustFlags.setVisible(true);
			this.custQualification.setVisible(true);
			// this.custFlags.setVisible(true);
			this.custIndustry.setVisible(true);
			this.natureOfBusiness.setVisible(true);
			// this.label_natureOfBusiness.setVisible(true);
			this.label_entityType.setVisible(false);
			this.hbox_entityType.setVisible(false);
			this.entityType.setVisible(false);
			this.space_entitytype.setVisible(false);
			this.hbox_CustReligion.setVisible(true);
			this.label_CustomerDialog_Profession.setVisible(true);
			this.profession.setVisible(true);
			if (ImplementationConstants.ALLOW_MULTIPLE_EMPLOYMENTS) {
				this.row_EmploymentDetails.setVisible(true);
				this.gp_CustEmployeeDetails.setVisible(false);
			} else {
				this.row_EmploymentDetails.setVisible(false);
				this.gp_CustEmployeeDetails.setVisible(true);
			}

			if (SysParamUtil.isAllowed(SMTParameterConstants.CUST_LASTNAME_MANDATORY)) {
				this.space_cust_LName.setSclass(PennantConstants.mandateSclass);
			}

			this.space_CustShrtName.setSclass("");
			this.label_CustomerDialog_SalaryTransfered.setVisible(true);
			// this.label_CustomerDialog_EIDNumber.setValue(Labels.getLabel("label_CoreCustomerDialog_PrimaryID.value"));
			this.label_CustomerDialog_CustShrtName.setValue(Labels.getLabel("label_CustomerDialog_CustShrtName.value"));
			this.label_CustomerDialog_CustCOB.setValue(Labels.getLabel("label_CustomerDialog_CustCOB.value"));
			this.directorDetails.setVisible(false);
			this.gb_rating.setVisible(false);
			this.label_LocalLngName.setVisible(true);

			if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_GST_RETAIL_CUSTOMER)) {
				this.space_CustLocalLngName.setSclass(PennantConstants.mandateSclass);
			} else {
				this.space_CustLocalLngName.setSclass("");
			}
			this.custLocalLngName.setVisible(true);
			this.tabfinancial.setVisible(ImplementationConstants.ALLOW_CUSTOMER_INCOMES);
			this.label_CustRelatedParty.setVisible(true);
			this.hbox_CustRelatedParty.setVisible(true);
			this.label_CustSegment.setVisible(true);
			this.custSegment.setVisible(true);
			this.label_CustSubSegment.setVisible(true);
			this.custSubSegment.setVisible(true);
			// this.label_CustomerDialog_CustFlags.setVisible(true);
			// this.custFlags.setVisible(true);

			// label
			this.row_custStatus.setVisible(true);
			this.label_CustomerDialog_Target.setVisible(true);
			this.target.setVisible(true);
			this.target.setMandatoryStyle(false);
			this.label_CustomerDialog_CustIndustry.setVisible(true);
			// this.label_CustomerDialog_ResidentialStatus.setVisible(true);
			// this.space_Residential.setVisible(true);
			// this.custResidentialStstus.setVisible(true);
			row_EmploymentDetails.setVisible(ImplementationConstants.SHOW_CUST_EMP_DETAILS);
			this.space_applicationNo.setVisible(true);
			this.label_CustomerDialog_ApplicationNo.setVisible(true);
			this.applicationNo.setVisible(true);
		} else {
			this.row_FirstMiddleName.setVisible(false);
			this.label_CustomerDialog_CustLastName.setVisible(false);
			this.label_CustomerDialog_motherMaidenName.setVisible(false);
			this.hbox_CustLastName.setVisible(false);
			this.hbox_motherMaidenName.setVisible(false);
			this.label_CustomerDialog_CustReligion.setVisible(false);
			this.label_CustomerDialog_CustCaste.setVisible(false);
			this.hbox_CustReligion.setVisible(false);
			this.hbox_CustCaste.setVisible(false);
			this.label_CustomerDialog_subCategory.setVisible(false);
			this.subCategory.setVisible(false);
			this.row_GenderSalutation.setVisible(false);
			this.row_MartialDependents.setVisible(false);
			this.row_custDSA.setVisible(false);
			this.row_custStaff.setVisible(false);
			this.label_CustSubSegment.setVisible(false);
			this.custSubSegment.setVisible(false);
			this.row_custCountry.setVisible(false);
			this.hbox_SalariedCustomer.setVisible(false);
			this.salariedCustomer.setVisible(false);
			this.space_CustShrtName.setSclass(PennantConstants.mandateSclass);
			this.label_CustomerDialog_SalaryTransfered.setVisible(false);
			this.label_CustomerDialog_CustShrtName.setValue(Labels.getLabel("label_CustomerDialog_CustomerName.value"));
			this.label_CustomerDialog_CustDOB
					.setValue(Labels.getLabel("label_CustomerDialog_CustDateOfIncorporation.value"));
			// this.label_CustomerDialog_EIDNumber.setValue(Labels.getLabel("label_CoreCustomerDialog_TradeLicenseNumber.value"));
			this.label_CustomerDialog_CustCOB.setValue(Labels.getLabel("label_CustomerDialog_CustCOI.value"));
			this.gb_rating.setVisible(getUserWorkspace().isAllowed("CustomerDialog_ShowCustomerRatings"));
			this.gp_CustEmployeeDetails.setVisible(false);
			this.row_EmploymentDetails.setVisible(false);
			this.label_LocalLngName.setVisible(false);
			this.space_CustLocalLngName.setSclass("");
			this.custLocalLngName.setVisible(false);
			directorDetails.setVisible(ImplementationConstants.SHOW_CUST_SHARE_HOLDER_DETAILS);
			this.label_CustRelatedParty.setVisible(false);
			this.hbox_CustRelatedParty.setVisible(false);
			this.label_CustSegment.setVisible(false);
			this.custSegment.setVisible(false);

			this.row_custStatus.setVisible(false);
			this.label_CustomerDialog_Target.setVisible(false);
			this.target.setVisible(false);
			this.target.setMandatoryStyle(false);
			// this.label_CustomerDialog_CustFlags.setVisible(false);
			// this.custFlags.setVisible(false);
			this.label_OtherReligion.setVisible(false);
			this.label_OtherCaste.setVisible(false);
			this.hbox_CustOtherCaste.setVisible(false);
			this.hbox_CustOtherReligion.setVisible(false);
			// this.label_natureOfBusiness.setVisible(false);
			this.label_entityType.setVisible(true);
			this.hbox_entityType.setVisible(true);
			this.entityType.setVisible(true);
			this.space_entitytype.setVisible(true);
			this.natureOfBusiness.setVisible(false);
			// this.custResidentialStstus.setVisible(false);
			// this.row_natueOfBusiness.setVisible(false);
			this.label_CustSubSegment.setVisible(false);
			this.custSubSegment.setVisible(false);
			this.label_CustomerDialog_CustQualification.setVisible(false);
			// this.label_CustomerDialog_CustFlags.setVisible(false);
			this.custQualification.setVisible(false);
			// this.custFlags.setVisible(false);
			this.label_CustomerDialog_Profession.setVisible(false);
			this.profession.setVisible(false);
			this.custIndustry.setVisible(true);
			this.label_CustomerDialog_CustIndustry.setVisible(true);
			// this.custRO1.setMandatoryStyle(false);
			// this.label_CustomerDialog_ResidentialStatus.setVisible(false);
			// this.space_Residential.setVisible(false);
			this.hbox_SubCategory.setVisible(false);
			this.row_qualification.setVisible(false);
			this.label_CustomerDialog_CKYCOrReferenceNo.setVisible(false);
			this.ckycOrRefNo.setVisible(false);

			boolean tabVisable = false;
			String value = null;
			try {
				value = SysParamUtil.getValueAsString("CUSTOMER_CORP_FINANCE_TAB_REQ");
			} catch (Exception e) {
			}

			if (StringUtils.equals(PennantConstants.YES, value) && ImplementationConstants.ALLOW_CUSTOMER_INCOMES) {
				tabVisable = true;
			}
			this.tabfinancial.setVisible(tabVisable);
		}

		Map<String, String> attributes = PennantApplicationUtil
				.getPrimaryIdAttributes(customerDetails.getCustomer().getCustCtgCode());

		primaryIdLabel = attributes.get("LABEL");
		if (isNewCustCret && ImplementationConstants.COAPP_PANNUMBER_NON_MANDATORY) {
			primaryIdMandatory = false;
		} else {
			primaryIdMandatory = Boolean.valueOf(attributes.get("MANDATORY"));
		}
		primaryIdRegex = attributes.get("REGEX");
		int maxLength = Integer.valueOf(attributes.get("LENGTH"));

		label_CustomerDialog_EIDNumber.setValue(Labels.getLabel(primaryIdLabel));
		if (!ImplementationConstants.CUSTOMER_PAN_VALIDATION_STOP) {
			space_EidNumber.setSclass(primaryIdMandatory ? PennantConstants.mandateSclass : "");
			if (isRetailCustomer && !isPanMandatory) {
				space_EidNumber.setSclass(primaryIdMandatory ? PennantConstants.NONE : "");
			}
		}
		eidNumber.setSclass(PennantConstants.mandateSclass);
		eidNumber.setMaxlength(maxLength);

		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType) || isNotFinanceProcess) {
			// Buttons
			this.btnDelete.setVisible(false);
			this.btnEdit.setVisible(false);
			this.btnSave.setVisible(false);

			this.btnNew_CustomerRatings.setVisible(false);
			this.btnNew_CustomerEmploymentDetail.setVisible(false);
			this.btnNew_CustomerAddress.setVisible(false);
			this.btnNew_CustomerIncome.setVisible(false);
			this.btnNew_CustomerDocuments.setVisible(false);
			this.btnNew_DirectorDetail.setVisible(false);
			this.btnNew_BankInformation.setVisible(false);
			this.btnNew_ChequeInformation.setVisible(false);
			this.btnNew_ExternalLiability.setVisible(false);
			this.btnNew_CustomerPhoneNumber.setVisible(false);
			this.btnNew_CustomerEmail.setVisible(false);
			this.btnNew_CustomerGSTDetails.setVisible(false);
			// for eid read only in enquiry
			this.eidNumber.setReadonly(true);
			this.btnNew_CardSalesInformation.setVisible(false);
		} else if (StringUtils.isNotEmpty(getCustomerDetails().getCustomer().getCustCoreBank())) {
			if (StringUtils.isEmpty(this.custFirstName.getValue())) {
				this.custFirstName.setReadonly(isReadOnly("CustomerDialog_custFirstName"));
			}
			if (StringUtils.isEmpty(this.custMiddleName.getValue())) {
				this.custMiddleName.setReadonly(isReadOnly("CustomerDialog_custMiddleName"));
			}
			if (StringUtils.isEmpty(this.custLastName.getValue())) {
				this.custLastName.setReadonly(isReadOnly("CustomerDialog_custLastName"));
			}
			if (StringUtils.isEmpty(this.custLocalLngName.getValue())) {
				this.custLocalLngName.setReadonly(isReadOnly("CustomerDialog_custLocalLngName"));
			}
			if (StringUtils.isEmpty(this.otherCaste.getValue())) {
				this.otherCaste.setReadonly(isReadOnly("CustomerDialog_otherCaste"));
			}
			if (StringUtils.isEmpty(this.otherReligion.getValue())) {
				this.otherReligion.setReadonly(isReadOnly("CustomerDialog_otherReligion"));
			}
			if (StringUtils.isEmpty(this.motherMaidenName.getValue())) {
				this.motherMaidenName.setReadonly(isReadOnly("CustomerDialog_custMotherMaiden"));
			}
			if (StringUtils.isEmpty(this.custDftBranch.getValue())) {
				this.custDftBranch.setReadonly(isReadOnly("CustomerDialog_custDftBranch"));
				if (!this.custDftBranch.isReadonly()) {
					this.custDftBranch.setMandatoryStyle(true);
				}
			}
			if (StringUtils.isEmpty(this.custBaseCcy.getValue())) {
				this.custBaseCcy.setReadonly(isReadOnly("CustomerDialog_custBaseCcy"));
				if (!this.custBaseCcy.isReadonly()) {
					this.custBaseCcy.setMandatoryStyle(true);
				}
			}
			if (StringUtils.isEmpty(this.custTypeCode.getValue())) {
				this.custTypeCode.setReadonly(isReadOnly("CustomerDialog_custTypeCode"));
				if (!this.custTypeCode.isReadonly()) {
					this.custTypeCode.setMandatoryStyle(true);
				}
			}
			if (StringUtils.isEmpty(this.custNationality.getValue())) {
				this.custNationality.setReadonly(isReadOnly("CustomerDialog_custNationality"));
				if (!this.custNationality.isReadonly()) {
					this.custNationality.setMandatoryStyle(true);
				}
			}
			if (StringUtils.isEmpty(this.custQualification.getValue())) {
				this.custQualification.setReadonly(isReadOnly("CustomerDialog_qualification"));
				if (!this.custQualification.isReadonly()) {
					this.custQualification.setMandatoryStyle(false);
				}
			}
			if (StringUtils.isEmpty(this.custLng.getValue())) {
				this.custLng.setReadonly(isReadOnly("CustomerDialog_custLng"));
				if (!this.custLng.isReadonly()) {
					this.custLng.setMandatoryStyle(true);
				}
			}
			if (StringUtils.isEmpty(this.custSts.getValue())) {
				this.custSts.setReadonly(true);
			}
			if (StringUtils.isEmpty(this.custSector.getValue())) {
				this.custSector.setReadonly(isReadOnly("CustomerDialog_custSector"));
				if (!this.custSector.isReadonly()) {
					this.custSector.setMandatoryStyle(true);
				}
			}
			if (StringUtils.isEmpty(this.custIndustry.getValue())) {
				this.custIndustry.setReadonly(isReadOnly("CustomerDialog_custIndustry"));
				if (!this.custIndustry.isReadonly()) {
					this.custIndustry.setMandatoryStyle(true);
				}
			}
			if (StringUtils.isEmpty(this.custSegment.getValue())) {
				this.custSegment.setReadonly(true);
				if (!this.custSegment.isReadonly()) {
					this.custSegment.setMandatoryStyle(false);
				}
			}
			if (StringUtils.isEmpty(this.custCOB.getValue())) {
				this.custCOB.setReadonly(isReadOnly("CustomerDialog_custCOB"));
				if (!this.custCOB.isReadonly()) {
					this.custCOB.setMandatoryStyle(true);
				}
			}
			if ("#".equals(getComboboxValue(this.custGenderCode))) {
				this.custGenderCode.setDisabled(isReadOnly("CustomerDialog_custGenderCode"));
			}
			if ("#".equals(getComboboxValue(this.custSalutationCode))) {
				this.custSalutationCode.setDisabled(isReadOnly("CustomerDialog_custSalutationCode"));
			}
			if (this.custDOB.getValue() == null) {
				this.custDOB.setDisabled(isReadOnly("CustomerDialog_custDOB"));
			}
			if (StringUtils.isEmpty(this.target.getValue())) {
				this.target.setReadonly(isReadOnly("CustomerDialog_target"));
				if (!this.target.isReadonly()) {
					this.target.setMandatoryStyle(false);
				}
			}
			if (StringUtils.isEmpty(this.custRO1.getValue())) {
				this.custRO1.setReadonly(isReadOnly("CustomerDialog_custRO1"));
				if (!this.custRO1.isReadonly()) {
					this.custRO1.setMandatoryStyle(true);
				}
			}
			if (this.empFrom.getValue() == null) {
				this.empFrom.setDisabled(isReadOnly("CustomerDialog_empFrom"));
			}
			if (StringUtils.isEmpty(this.eidNumber.getValue())) {
				this.eidNumber.setReadonly(isReadOnly("CustomerDialog_custCRCPR"));
			}
			if (StringUtils.isEmpty(this.custTradeLicenceNum.getValue())) {
				this.custTradeLicenceNum.setReadonly(isReadOnly("CustomerDialog_custTradeLicenceNum"));
			}
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		boolean isMandValidate = validateAllDetails;

		// below fields are always mandatory

		if (!this.eidNumber.isReadonly()) {
			// ### 01-05-2018 ToolApp ID : #360
			if (!ImplementationConstants.CUSTOMER_PAN_VALIDATION_STOP) {
				this.eidNumber.setConstraint(
						new PTStringValidator(Labels.getLabel(primaryIdLabel), primaryIdRegex, primaryIdMandatory));
			}
			if (ImplementationConstants.CUSTOMER_PAN_VALIDATION_STOP
					&& StringUtils.isNotEmpty(this.eidNumber.getText())) {
				this.eidNumber.setConstraint(
						new PTStringValidator(Labels.getLabel(primaryIdLabel), primaryIdRegex, primaryIdMandatory));
			}
			if (isRetailCustomer && !isPanMandatory) {
				this.eidNumber
						.setConstraint(new PTStringValidator(Labels.getLabel(primaryIdLabel), primaryIdRegex, false));
			}
		}

		if (!this.applicationNo.isReadonly()) {
			this.applicationNo
					.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_ApplicationNo.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, false));
		}
		if (!this.otherReligion.isReadonly() && PennantConstants.OTHER.equals(this.religion.getValue())) {
			this.otherReligion
					.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_OtherReligion.value"),
							PennantRegularExpressions.REGEX_ALPHA_SPACE, true));
		}
		if (!this.otherCaste.isReadonly() && PennantConstants.OTHER.equals(this.caste.getValue())) {
			this.otherCaste
					.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_OtherCaste.value"),
							PennantRegularExpressions.REGEX_ALPHA_SPACE, true));
		}
		if (!this.ckycOrRefNo.isReadonly()) {
			this.ckycOrRefNo.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerDialog_CKYCOrReferenceNo.value"),
							PennantRegularExpressions.REGEX_ALPHA_SPACE, true));
		}

		// below fields are conditional mandatory

		if (isRetailCustomer) {
			if (!this.custFirstName.isReadonly()) {
				this.custFirstName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustFirstName.value"),
								PennantRegularExpressions.REGEX_CUST_NAME, isMandValidate));
			}
			if (!this.custMiddleName.isReadonly()) {
				this.custMiddleName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustMiddleName.value"),
								PennantRegularExpressions.REGEX_CUST_NAME, false));
			}
			if (SysParamUtil.isAllowed(SMTParameterConstants.CUST_LASTNAME_MANDATORY)) {
				if (!this.custLastName.isReadonly()) {
					this.custLastName.setConstraint(
							new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustLastName.value"),
									PennantRegularExpressions.REGEX_CUST_NAME, isMandValidate));
				}
			}
			if (!this.motherMaidenName.isReadonly()) {
				this.motherMaidenName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustMotherMaiden.value"),
								PennantRegularExpressions.REGEX_CUST_NAME, isMandValidate));
			}

			if (!this.custLocalLngName.isReadonly()) {
				this.custLocalLngName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustLocalLngName.value"),
								PennantRegularExpressions.REGEX_CUST_NAME, isMandValidate));
			}
			if (!this.caste.isReadonly()) {
				this.caste.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_Caste.value"),
						PennantRegularExpressions.REGEX_CUST_NAME,
						SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_GST_RETAIL_CUSTOMER)));
			}
			if (!this.religion.isReadonly()) {
				this.religion
						.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_Religion.value"),
								PennantRegularExpressions.REGEX_ALPHA_SPACE, false));
			}

		} else {
			if (!this.custShrtName.isReadonly()) {
				if (StringUtils.equals(this.customerDetails.getCustomer().getCustCtgCode(),
						PennantConstants.PFF_CUSTCTG_CORP)
						|| StringUtils.equals(this.customerDetails.getCustomer().getCustCtgCode(),
								PennantConstants.PFF_CUSTCTG_SME)) {
					this.custShrtName.setConstraint(
							new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustomerName.value"),
									PennantRegularExpressions.REGEX_CORP_CUST_NAME, true));
				} else {
					this.custShrtName.setConstraint(
							new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustomerName.value"),
									PennantRegularExpressions.REGEX_RETAIL_CUST_NAME, true));
				}

			}
		}

		if (!this.custDOB.isDisabled()) {
			if (isRetailCustomer) {
				this.custDOB.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerDialog_CustDOB.value"),
						isMandValidate, startDate, appDate, false));
			} else {
				this.custDOB.setConstraint(
						new PTDateValidator(Labels.getLabel("label_CustomerDialog_CustDateOfIncorporation.value"),
								isMandValidate, startDate, appDate, false));
			}
		}

		if (!this.custStaffID.isReadonly()) {
			this.custStaffID
					.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustSaffID.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, false));
		}

		if (!this.custDSACode.isReadonly()) {
			this.custDSACode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustDSACode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, false));
		}

		// Employee
		if (isRetailCustomer && this.gp_CustEmployeeDetails.isVisible()) {
			if (!this.empFrom.isDisabled()) {
				if (StringUtils.trimToEmpty(this.empStatus.getValue())
						.equalsIgnoreCase(PennantConstants.CUSTEMPSTS_SELFEMP)) {
					this.empFrom.setConstraint(
							new PTDateValidator(Labels.getLabel("label_CustomerDialog_ProfessionStartDate.value"),
									isMandValidate, startDate, appDate, false));
				} else if (StringUtils.trimToEmpty(this.empStatus.getValue())
						.equalsIgnoreCase(PennantConstants.CUSTEMPSTS_SME)) {
					this.empFrom.setConstraint(
							new PTDateValidator(Labels.getLabel("label_CustomerDialog_BusinessStartDate.value"),
									isMandValidate, startDate, appDate, false));
				} else {
					this.empFrom
							.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerDialog_EmpFrom.value"),
									isMandValidate, startDate, appDate, false));
				}
			}
			if (!this.monthlyIncome.isReadonly()) {
				if (StringUtils.trimToEmpty(this.empStatus.getValue())
						.equalsIgnoreCase(PennantConstants.CUSTEMPSTS_SELFEMP)) {
					this.monthlyIncome.setConstraint(new PTDecimalValidator(
							Labels.getLabel("label_CustomerDialog_MonthlyProfessionIncome.value"), ccyFormatter,
							isMandValidate, false));
				} else if (StringUtils.trimToEmpty(this.empStatus.getValue())
						.equalsIgnoreCase(PennantConstants.CUSTEMPSTS_SME)) {
					this.monthlyIncome.setConstraint(
							new PTDecimalValidator(Labels.getLabel("label_CustomerDialog_AvgMonthlyTurnover.value"),
									ccyFormatter, isMandValidate, false));
				} else {
					this.monthlyIncome.setConstraint(
							new PTDecimalValidator(Labels.getLabel("label_CustomerDialog_MonthlyIncome.value"),
									ccyFormatter, isMandValidate, false));
				}
			}
			if (!this.additionalIncome.isReadonly()) {
				if (StringUtils.isBlank(this.otherIncome.getValue())) {
					this.additionalIncome.setConstraint(
							new PTDecimalValidator(Labels.getLabel("label_CustomerDialog_AdditionalIncome.value"),
									ccyFormatter, false, false));
				} else {
					this.additionalIncome.setConstraint(
							new PTDecimalValidator(Labels.getLabel("label_CustomerDialog_AdditionalIncome.value"),
									ccyFormatter, isMandValidate, false));
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the LOVfields.
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		boolean isMandValidate = validateAllDetails;
		boolean nonWorking = !(PennantConstants.EMPLOYMENTTYPE_NONWORKING.equals(this.subCategory.getValue()));

		// below fields are always mandatory

		if (this.custCtgCode.isButtonVisible()) {
			this.custCtgCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustCtgCode.value"), null, true, true));
		}
		if (!this.custTypeCode.isReadonly()) {
			this.custTypeCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerDialog_CustTypeCode.value"), null, true, true));
		}
		if (!this.custNationality.isReadonly()) {
			this.custNationality.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerDialog_CustNationality.value"), null, true, true));
		}
		if (!this.custQualification.isReadonly()) {
			// PSD#163298 Issue addressed for mandatory validations While Resubmitting.
			this.custQualification.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerDialog_CustQualification.value"), null, false, true));
		}
		if (!this.custSector.isReadonly() && nonWorking) {
			// PSD#163298 Issue addressed for mandatory validations While Resubmitting.
			this.custSector.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerDialog_CustSector.value"), null, isMandValidate, true));
		}
		if (!this.custIndustry.isReadonly() && this.custIndustry.isVisible() && nonWorking) {
			// PSD#163298 Issue addressed for mandatory validations While Resubmitting.
			this.custIndustry.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerDialog_CustIndustry.value"), null, isMandValidate, true));
		}
		if (!this.custCOB.isReadonly()) {
			this.custCOB.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustCOB.value"), null, true, true));
		}
		if (!this.custBaseCcy.isReadonly()) {
			this.custBaseCcy.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustBaseCcy.value"), null, true, true));
		}

		// below fields are conditional mandatory

		if (!this.custDftBranch.isReadonly()) {
			this.custDftBranch.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerDialog_CustDftBranch.value"), null, isMandValidate, true));
		}
		if (!this.custLng.isReadonly()) {
			this.custLng.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustLng.value"),
					null, isMandValidate, true));
		}
		if (!this.custSts.isReadonly()) {
			this.custSts.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustSts.value"), null, false, true));
		}
		if (!this.custSegment.isReadonly() && this.custSegment.isVisible() && nonWorking) {
			// PSD#163298 Issue addressed for mandatory validations While Resubmitting.
			this.custSegment.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerDialog_CustSegment.value"), null, false, true));
		}

		if (isRetailCustomer) {
			if (!this.custSalutationCode.isReadonly()) {
				this.custSalutationCode.setConstraint(new PTStringValidator(
						Labels.getLabel("label_CustomerDialog_CustSalutationCode.value"), null, isMandValidate));
			}
			this.custGenderCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerDialog_CustGenderCode.value"), null, isMandValidate));

			if (!this.custMaritalSts.isReadonly()) {
				this.custMaritalSts.setConstraint(new PTStringValidator(
						Labels.getLabel("label_CustomerDialog_CustMaritalSts.value"), null, isMandValidate));
			}

		}
		if (!this.custRO1.isReadonly()) {
			this.custRO1.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustRO1.value"),
					null, isMandValidate ? this.custRO1.isMandatory() : false, true));
		}
		if (this.target.isVisible() && !this.target.isReadonly()) {
			this.target.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerDialog_Target.value"), null, false, true));
		}
		if (!this.custGroupId.isReadonly()) {
			this.custGroupId.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerDialog_CustGroupID.value"), null, false, true));
		}
		if (!this.custDSADept.isReadonly()) {
			this.custDSADept.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerDialog_CustDSADeptCode.value"), null, false, true));
		}

		if (!this.custRiskCountry.isReadonly()) {
			this.custRiskCountry.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerDialog_CustRiskCountry.value"), null, false, true));
		}

		if (!this.custParentCountry.isReadonly()) {
			this.custParentCountry.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerDialog_CustParentCountry.value"), null, false, true));
		}

		if (!this.custSubSector.isReadonly()) {
			this.custSubSector.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerDialog_CustSubSector.value"), null, false, true));
		}

		if (!this.custSubSegment.isReadonly()) {
			this.custSubSegment.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerDialog_CustSubSegment.value"), null, false, true));
		}

		// Employee
		if (isRetailCustomer && this.gp_CustEmployeeDetails.isVisible()) {
			if (!this.empStatus.isReadonly()) {
				this.empStatus.setConstraint(new PTStringValidator(
						Labels.getLabel("label_CustomerDialog_EmpStatus.value"), null, true, true));
			}
			if (this.row_EmpName.isVisible() && this.empName.isButtonVisible()) {
				this.empName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_EmpName.value"),
						null, isMandValidate, true));
			}
			if (this.hbox_empNameOther.isVisible() && !this.empNameOther.isReadonly()) {
				this.empNameOther.setConstraint(new PTStringValidator(
						Labels.getLabel("label_CustomerDialog_EmpNameOther.value"), null, isMandValidate, false));
			}
			if (row_DesgDept.isVisible()) {
				if (!this.empDesg.isReadonly()) {
					this.empDesg.setConstraint(new PTStringValidator(
							Labels.getLabel("label_CustomerDialog_EmpDesg.value"), null, isMandValidate, true));
				}
				if (!this.empDept.isReadonly()) {
					this.empDept.setConstraint(new PTStringValidator(
							Labels.getLabel("label_CustomerDialog_EmpDept.value"), null, isMandValidate, true));
				}
			}
			if (this.profession.isVisible() && !this.profession.isReadonly()) {
				this.profession.setConstraint(new PTStringValidator(
						Labels.getLabel("label_CustomerDialog_Profession.value"), null, isMandValidate, true));
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
		this.custCoreBank.setConstraint("");
		this.custShrtName.setConstraint("");
		this.custFirstName.setConstraint("");
		this.custMiddleName.setConstraint("");
		this.custLastName.setConstraint("");
		this.custLocalLngName.setConstraint("");
		this.motherMaidenName.setConstraint("");
		this.custDOB.setConstraint("");
		this.eidNumber.setConstraint("");
		this.empFrom.setConstraint("");
		this.monthlyIncome.setConstraint("");
		this.additionalIncome.setConstraint("");
		this.custTradeLicenceNum.setConstraint("");
		this.custGroupId.setConstraint("");
		this.custStaffID.setConstraint("");
		this.custDSACode.setConstraint("");
		this.custDSADept.setConstraint("");
		this.custParentCountry.setConstraint("");
		this.custRiskCountry.setConstraint("");
		this.custSubSector.setConstraint("");
		this.custSubSegment.setConstraint("");
		this.otherCaste.setConstraint("");
		this.otherReligion.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Removes the Validation by setting the accordingly constraints to the LOVfields.
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.custTypeCode.setConstraint("");
		this.custDftBranch.setConstraint("");
		this.custBaseCcy.setConstraint("");
		this.custGenderCode.setConstraint("");
		this.custSalutationCode.setConstraint("");
		this.custCtgCode.setConstraint("");
		this.custNationality.setConstraint("");
		this.custLng.setConstraint("");
		this.custSts.setConstraint("");
		this.custSector.setConstraint("");
		this.custIndustry.setConstraint("");
		this.custSegment.setConstraint("");
		this.custSubSegment.setConstraint("");
		this.custCOB.setConstraint("");
		this.custMaritalSts.setConstraint("");
		this.target.setConstraint("");
		this.custRO1.setConstraint("");
		this.empStatus.setConstraint("");
		this.empName.setConstraint("");
		this.empNameOther.setConstraint("");
		this.empDesg.setConstraint("");
		this.empDept.setConstraint("");
		this.profession.setConstraint("");
		this.custGroupId.setConstraint("");
		this.custStaffID.setConstraint("");
		this.custDSACode.setConstraint("");
		this.custDSADept.setConstraint("");
		this.custParentCountry.setConstraint("");
		this.custRiskCountry.setConstraint("");
		this.custSubSector.setConstraint("");
		this.custSubSegment.setConstraint("");
		this.natureOfBusiness.setConstraint("");
		// this.custResidentialStstus.setConstraint("");
		this.custQualification.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	public void doClearErrorMessage() {
		logger.debug("Enterring");
		this.custCIF.setErrorMessage("");
		this.custCoreBank.setErrorMessage("");
		this.custCtgCode.setErrorMessage("");
		this.custTypeCode.setErrorMessage("");
		this.custSalutationCode.setErrorMessage("");
		this.custShrtName.setErrorMessage("");
		this.custFirstName.setErrorMessage("");
		this.custMiddleName.setErrorMessage("");
		Clients.clearWrongValue(this.custMiddleName);
		this.custLastName.setErrorMessage("");
		this.custLocalLngName.setErrorMessage("");
		this.motherMaidenName.setErrorMessage("");
		this.custDftBranch.setErrorMessage("");
		this.custGenderCode.setErrorMessage("");
		this.custDOB.setErrorMessage("");
		this.custBaseCcy.setErrorMessage("");
		this.custNationality.setErrorMessage("");
		this.custLng.setErrorMessage("");
		this.custSts.setErrorMessage("");
		this.custSector.setErrorMessage("");
		this.custIndustry.setErrorMessage("");
		this.custSegment.setErrorMessage("");
		this.custCOB.setErrorMessage("");
		this.custMaritalSts.setErrorMessage("");
		this.custRO1.setErrorMessage("");
		this.target.setErrorMessage("");
		this.eidNumber.setErrorMessage("");
		this.custTradeLicenceNum.setErrorMessage("");
		this.empStatus.setErrorMessage("");
		this.empName.setErrorMessage("");
		this.empNameOther.setErrorMessage("");
		this.empDesg.setErrorMessage("");
		this.empDept.setErrorMessage("");
		this.empFrom.setErrorMessage("");
		this.monthlyIncome.setErrorMessage("");
		this.additionalIncome.setErrorMessage("");
		this.profession.setErrorMessage("");
		this.custGroupId.setErrorMessage("");
		this.custStaffID.setErrorMessage("");
		this.custDSACode.setErrorMessage("");
		this.custDSADept.setErrorMessage("");
		this.custParentCountry.setErrorMessage("");
		this.custRiskCountry.setErrorMessage("");
		this.custSubSector.setErrorMessage("");
		this.custSubSegment.setErrorMessage("");

		this.subCategory.setErrorMessage("");
		this.caste.setErrorMessage("");
		this.religion.setErrorMessage("");
		this.natureOfBusiness.setErrorMessage("");
		this.entityType.setErrorMessage("");
		// this.custFlags.setErrorMessage("");
		// this.custResidentialStstus.setErrorMessage("");
		this.custQualification.setErrorMessage("");

		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getCustomerListCtrl().refreshList();
	}

	private void doDelete() throws InterfaceException {
		logger.debug(Literal.ENTERING);

		CustomerDetails aCustomerDetails = ObjectUtil.clone(getCustomerDetails());
		Customer aCustomer = aCustomerDetails.getCustomer();

		final String keyReference = Labels.getLabel("label_CustomerDialog_CustCIF.value") + " : "
				+ aCustomer.getCustCIF();

		doDelete(keyReference, aCustomerDetails);

		logger.debug(Literal.LEAVING);
	}

	protected void onDoDelete(CustomerDetails aCustomerDetails) {
		String tranType = PennantConstants.TRAN_WF;
		Customer aCustomer = aCustomerDetails.getCustomer();
		CustEmployeeDetail custEmployeeDetail = aCustomerDetails.getCustEmployeeDetail();

		if (StringUtils.isBlank(aCustomer.getRecordType())) {
			aCustomer.setVersion(aCustomer.getVersion() + 1);
			aCustomer.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			custEmployeeDetail.setVersion(custEmployeeDetail.getVersion() + 1);
			custEmployeeDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			if (isWorkFlowEnabled()) {
				aCustomer.setNewRecord(true);
				custEmployeeDetail.setNewRecord(true);
				tranType = PennantConstants.TRAN_WF;
			} else {
				tranType = PennantConstants.TRAN_DEL;
			}
		}
		if (!isRetailCustomer || ImplementationConstants.ALLOW_MULTIPLE_EMPLOYMENTS) {
			aCustomerDetails.setCustEmployeeDetail(null);
		}
		try {
			aCustomerDetails.setCustomer(aCustomer);
			if (doProcess(aCustomerDetails, tranType)) {
				refreshList();
				closeDialog();
			}
			logger.debug(" Calling doDelete method completed Successfully ");
		} catch (DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (PennantConstants.MODULETYPE_ENQ.equals(moduleType) || isNotFinanceProcess) {
			doReadOnly();
		} else {
			this.custCoreBank.setReadonly(true);
			this.custCtgCode.setReadonly(true);
			this.custRelatedParty.setReadonly(true);

			if (!isReadOnly("CustomerDialog_coreBankID")) {
				Customer aCustomer = this.customerDetails.getCustomer();
				if (aCustomer.isNewRecord()) {
					this.custCoreBank.setReadonly(false);
				}
				if (PennantConstants.RECORD_TYPE_NEW.equals(aCustomer.getRecordType())) {
					this.custCoreBank.setReadonly(false);
				}
			}
			if (StringUtils.isBlank(getCustomerDetails().getCustomer().getCustCoreBank())
					|| ImplementationConstants.ALLOW_CUSTOMER_MAINTENANCE) {
				if (isRetailCustomer) {
					this.custShrtName.setReadonly(true);
				} else {
					this.custShrtName.setReadonly(isReadOnly("CustomerDialog_custShrtName"));
				}
				this.custFirstName.setReadonly(isReadOnly("CustomerDialog_custFirstName"));
				this.custMiddleName.setReadonly(isReadOnly("CustomerDialog_custMiddleName"));
				this.custLastName.setReadonly(isReadOnly("CustomerDialog_custLastName"));
				this.custLocalLngName.setReadonly(isReadOnly("CustomerDialog_custLocalLngName"));
				this.motherMaidenName.setReadonly(isReadOnly("CustomerDialog_custMotherMaiden"));
				this.custDftBranch.setReadonly(isReadOnly("CustomerDialog_custDftBranch"));
				this.custBaseCcy.setReadonly(isReadOnly("CustomerDialog_custBaseCcy"));
				this.custTypeCode.setReadonly(isReadOnly("CustomerDialog_custTypeCode"));
				this.custNationality.setReadonly(isReadOnly("CustomerDialog_custNationality"));
				this.custLng.setReadonly(isReadOnly("CustomerDialog_custLng"));
				this.custSts.setReadonly(true);// isReadOnly("CustomerDialog_custSts")
				this.custSector.setReadonly(isReadOnly("CustomerDialog_custSector"));
				this.custIndustry.setReadonly(isReadOnly("CustomerDialog_custIndustry"));
				this.custCOB.setReadonly(isReadOnly("CustomerDialog_custCOB"));
				this.custSalutationCode.setDisabled(isReadOnly("CustomerDialog_custSalutationCode"));
				this.custDOB.setDisabled(isReadOnly("CustomerDialog_custDOB"));
				this.custGenderCode.setDisabled(isReadOnly("CustomerDialog_custGenderCode"));
				this.target.setReadonly(isReadOnly("CustomerDialog_target"));
				this.custCtgCode.setReadonly(true); // Not allowing user to
				// modify this field
				this.salariedCustomer.setDisabled(isReadOnly("CustomerDialog_salariedCustomer"));
				this.custRO1.setReadonly(isReadOnly("CustomerDialog_custRO1"));
				this.empFrom.setDisabled(isReadOnly("CustomerDialog_empFrom"));
				this.eidNumber.setReadonly(isReadOnly("CustomerDialog_custCRCPR"));
				this.custTradeLicenceNum.setReadonly(isReadOnly("CustomerDialog_custTradeLicenceNum"));
				this.custSegment.setReadonly(true);// CustomerDialog_custSegment
				this.custGroupId.setReadonly(isReadOnly("CustomerDialog_custGroupID"));
				this.custIsStaff.setDisabled(isReadOnly("CustomerDialog_custIsStaff"));
				this.dnd.setDisabled(isReadOnly("CustomerDialog_dnd"));
				this.custStaffID.setReadonly(isReadOnly("CustomerDialog_custStaffID"));
				this.custDSACode.setReadonly(isReadOnly("CustomerDialog_custDSACode"));
				this.custDSADept.setReadonly(isReadOnly("CustomerDialog_custDSADept"));
				this.custParentCountry.setReadonly(isReadOnly("CustomerDialog_custParentCountry"));
				this.custRiskCountry.setReadonly(isReadOnly("CustomerDialog_custRiskCountry"));
				this.custSubSector.setReadonly(isReadOnly("CustomerDialog_custIndustry"));
				this.custSubSegment.setReadonly(isReadOnly("CustomerDialog_custSubSegment"));
				this.applicationNo.setReadonly(isReadOnly("CustomerDialog_applicationNo"));
				this.vip.setDisabled(isReadOnly("CustomerDialog_vip"));

				readOnlyComponent(isReadOnly("CustomerDialog_cast"), this.caste);
				readOnlyComponent(isReadOnly("CustomerDialog_religion"), this.religion);
				readOnlyComponent(isReadOnly("CustomerDialog_subCategory"), this.subCategory);
				this.natureOfBusiness.setDisabled(isReadOnly("CustomerDialog_natureOfBusiness"));
				this.entityType.setDisabled(isReadOnly("CustomerDialog_entityType"));
				// this.custResidentialStstus.setDisabled(isReadOnly("CustomerDialog_custResidentialSts"));
				this.custSegment.setReadonly(isReadOnly("CustomerDialog_custGroupID"));
				// this.custFlags.setReadonly(isReadOnly("CustomerDialog_custFlags"));
				this.otherReligion.setReadonly(isReadOnly("CustomerDialog_otherReligion"));
				this.otherCaste.setReadonly(isReadOnly("CustomerDialog_otherCaste"));
				this.custQualification.setReadonly(isReadOnly("CustomerDialog_qualification"));
				this.ckycOrRefNo.setReadonly(isReadOnly("CustomerDialog_ckycOrRefNo"));
			} else {
				this.custShrtName.setReadonly(true);
				this.custFirstName.setReadonly(true);
				this.custMiddleName.setReadonly(true);
				this.custLastName.setReadonly(true);
				this.custLocalLngName.setReadonly(true);
				this.motherMaidenName.setReadonly(true);
				this.custDftBranch.setReadonly(true);
				this.custBaseCcy.setReadonly(true);
				this.custTypeCode.setReadonly(true);
				this.custNationality.setReadonly(true);
				this.custLng.setReadonly(true);
				this.custSts.setReadonly(true);
				this.custSector.setReadonly(true);
				this.custIndustry.setReadonly(true);
				this.custCOB.setReadonly(true);
				this.custSalutationCode.setDisabled(true);
				this.custDOB.setDisabled(true);
				this.custGenderCode.setDisabled(true);
				this.target.setReadonly(true);
				this.custCtgCode.setReadonly(true);
				this.salariedCustomer.setDisabled(true);
				this.custRO1.setReadonly(true);
				this.empFrom.setDisabled(true);
				this.eidNumber.setReadonly(true);
				this.custSegment.setReadonly(true);// CustomerDialog_custSegment
				this.custTradeLicenceNum.setReadonly(true);
				this.ckycOrRefNo.setReadonly(true);
				this.otherReligion.setReadonly(true);
				this.otherCaste.setReadonly(true);
				this.natureOfBusiness.setDisabled(true);
				this.entityType.setDisabled(true);
				this.custSubSegment.setReadonly(true);
				// this.custFlags.setReadonly(true);
				// this.custResidentialStstus.setDisabled(true);
				this.custQualification.setReadonly(true);

			}
			// Employee Details
			this.empStatus.setReadonly(isReadOnly("CustomerDialog_empStatus"));
			this.empSector.setReadonly(isReadOnly("CustomerDialog_empSector"));
			this.profession.setReadonly(isReadOnly("CustomerDialog_profession"));
			this.empName.setReadonly(isReadOnly("CustomerDialog_empName"));
			this.empNameOther.setReadonly(isReadOnly("CustomerDialog_empNameOther"));
			this.empDesg.setReadonly(isReadOnly("CustomerDialog_empDesg"));
			this.empDept.setReadonly(isReadOnly("CustomerDialog_empDept"));
			this.monthlyIncome.setReadonly(isReadOnly("CustomerDialog_monthlyIncome"));
			this.otherIncome.setReadonly(isReadOnly("CustomerDialog_otherIncome"));
			this.additionalIncome.setReadonly(isReadOnly("CustomerDialog_additionalIncome"));
			this.noOfDependents.setReadonly(isReadOnly("CustomerDialog_noOfDependents"));
			this.custMaritalSts.setDisabled(isReadOnly("CustomerDialog_custMaritalSts"));
			this.residentialStatus.setDisabled(isReadOnly("CustomerDialog_residentialStatus"));

			if (isRetailCustomer) {
				// readOnlyComponent(isReadOnly("CustomerDialog_btn_GenerateCibil"), this.btn_GenerateCibil);
			}

			if (!isFinanceProcess && isWorkFlowEnabled()) {
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(false);
				}
				if (this.customerDetails.isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.custCIF.setReadonly(true);
		this.custCoreBank.setReadonly(true);
		this.custCtgCode.setReadonly(true);

		this.custCoreBank.setReadonly(true);
		this.custCtgCode.setReadonly(true);

		this.custShrtName.setReadonly(true);
		this.custFirstName.setReadonly(true);
		this.custMiddleName.setReadonly(true);
		this.custLastName.setReadonly(true);
		this.custLocalLngName.setReadonly(true);
		this.motherMaidenName.setReadonly(true);
		this.custDftBranch.setReadonly(true);
		this.custBaseCcy.setReadonly(true);
		this.custTypeCode.setReadonly(true);
		this.custNationality.setReadonly(true);
		this.custLng.setReadonly(true);
		this.custSts.setReadonly(true);
		this.custSector.setReadonly(true);
		this.custIndustry.setReadonly(true);
		this.custSegment.setReadonly(true);
		this.custCOB.setReadonly(true);
		this.custMaritalSts.setDisabled(true);
		this.residentialStatus.setDisabled(true);
		this.custSalutationCode.setDisabled(true);
		this.custDOB.setDisabled(true);
		this.custGenderCode.setDisabled(true);
		this.noOfDependents.setReadonly(true);
		this.target.setReadonly(true);
		this.custCtgCode.setReadonly(true); // Not allowing user to modify this
		// field
		this.salariedCustomer.setDisabled(true);
		this.custRO1.setReadonly(true);
		this.custTradeLicenceNum.setReadonly(true);
		this.custRelatedParty.setReadonly(true);
		this.custGroupId.setReadonly(true);
		this.custIsStaff.setDisabled(true);
		this.dnd.setDisabled(true);
		this.custStaffID.setReadonly(true);
		this.custDSACode.setReadonly(true);
		this.custDSADept.setReadonly(true);
		this.custRiskCountry.setReadonly(true);
		this.custParentCountry.setReadonly(true);
		this.custSubSector.setReadonly(true);
		this.custSubSegment.setReadonly(true);
		this.applicationNo.setReadonly(true);
		this.otherCaste.setReadonly(true);
		this.otherReligion.setReadonly(true);
		this.ckycOrRefNo.setReadonly(true);
		// this.custFlags.setReadonly(true);
		// this.custResidentialStstus.setDisabled(true);
		this.natureOfBusiness.setDisabled(true);
		this.entityType.setDisabled(true);
		this.vip.setDisabled(true);

		// Employee Details
		this.empStatus.setReadonly(true);
		this.empSector.setReadonly(true);
		this.profession.setReadonly(true);
		this.empName.setReadonly(true);
		this.empNameOther.setReadonly(true);
		this.empFrom.setDisabled(true);
		this.empDesg.setReadonly(true);
		this.empDept.setReadonly(true);
		this.monthlyIncome.setReadonly(true);
		this.otherIncome.setReadonly(true);
		this.additionalIncome.setReadonly(true);

		this.btnNew_CustomerRatings.setVisible(false);
		this.btnNew_CustomerEmploymentDetail.setVisible(false);
		this.btnNew_CustomerAddress.setVisible(false);
		this.btnNew_CustomerIncome.setVisible(false);
		this.btnNew_CustomerDocuments.setVisible(false);
		this.btnNew_DirectorDetail.setVisible(false);
		this.btnNew_BankInformation.setVisible(false);
		this.btnNew_ChequeInformation.setVisible(false);
		this.btnNew_ExternalLiability.setVisible(false);
		this.btnNew_CardSalesInformation.setVisible(false);
		this.custQualification.setReadonly(true);

		this.btnNew_CustomerGSTDetails.setVisible(false);
		this.custMaritalSts.setDisabled(true);
		this.noOfDependents.setReadonly(true);

		readOnlyComponent(true, this.caste);
		readOnlyComponent(true, this.religion);
		readOnlyComponent(true, this.subCategory);
		readOnlyComponent(true, this.btn_GenerateCibil);
		readOnlyComponent(true, this.btnUploadExternalLiability);
		readOnlyComponent(true, this.btnDownloadExternalLiability);

		if (!isFinanceProcess) {
			if (isWorkFlowEnabled()) {
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(true);
				}
			}
			if (isWorkFlowEnabled()) {
				this.recordStatus.setValue("");
				this.userAction.setSelectedIndex(0);
			}
		}
		logger.debug("Leaving");
	}

	@Override
	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	public void onClick$btn_GenerateCibil(Event event) {
		onCibilButtonClick();

	}

	private void onCibilButtonClick() {
		doWriteComponentsToBean(customerDetails, null);

		if (!validateAddressDetails(this.tabkYCDetails)) {
			return;
		}
		if (!validatePhoneDetails(this.tabkYCDetails)) {
			return;
		}
		if (!validateEmailDetails(this.tabkYCDetails)) {
			return;
		}
		if (!validateCustomerDocuments(this.tabkYCDetails)) {
			return;
		}

		customerDetails.setCibilExecuted(true);
		FinanceMain main = null;
		if (getFinancedetail() != null && getFinancedetail().getFinScheduleData() != null) {
			main = getFinancedetail().getFinScheduleData().getFinanceMain();
		} else {
			main = financeMain;
		}

		try {
			customerDetails = getCreditInformation().procesCreditEnquiry(customerDetails, main, false);
		} catch (InterfaceException ie) {
			MessageUtil.showError(ie.getErrorMessage());
			return;
		}

		if (customerDetails.isCibilExecuted()) {
			// show confirmation
			// if yes then re-reun with override
			if (customerDetails.isCibilALreadyRun()) {
				if (customerDetails.isReInitiateCibil()) {
					final String msg = Labels.getLabel("Cibil.Already_Processed") + "\n\n --> "
							+ Labels.getLabel("label_CustomerDialog_CustCIF.value") + " : "
							+ customerDetails.getCustomer().getCustCIF();
					final FinanceMain fm = main;
					MessageUtil.confirm(msg, evnt -> {
						if (Messagebox.ON_YES.equals(evnt.getName())) {
							customerDetails = getCreditInformation().procesCreditEnquiry(customerDetails, fm, true);
							extendedFieldCtrl.setValues(customerDetails.getExtendedFieldRender().getMapValues());

						}
					});
				} else {
					MessageUtil.showMessage(Labels.getLabel("Cibil_ReInit_Meg.value") + " "
							+ SysParamUtil.getValueAsInt("CIBIL_REINTI_DAYS") + " days.");
				}
			} else {
				if (extendedFieldCtrl != null && customerDetails.getExtendedFieldRender() != null) {
					extendedFieldCtrl.setValues(customerDetails.getExtendedFieldRender().getMapValues());
				}
			}
			MessageUtil.showMessage("CIBIL Enquiry Completed.");
			doFillDocumentDetails(customerDetails.getCustomerDocumentsList());
		} else {
			String actualError = "";
			if (customerDetails.getActualError() != null) {
				actualError = customerDetails.getActualError();
			}
			MessageUtil.showError(actualError);
		}

	}

	public CreditInformation getCreditInformation() {
		return this.custCreditInformation == null ? this.creditInformation : this.custCreditInformation;
	}

	@Autowired(required = false)
	@Qualifier(value = "creditInformation")
	public void setCreditInformation(CreditInformation creditInformation) {
		this.creditInformation = creditInformation;
	}

	@Autowired(required = false)
	@Qualifier(value = "customCreditInformation")
	public void setCustCreditInformation(CreditInformation custCreditInformation) {
		this.custCreditInformation = custCreditInformation;
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.custCIF.setValue("");
		this.custCoreBank.setValue("");
		this.custCtgCode.setValue("");
		this.custCtgCode.setDescription("");
		this.custTypeCode.setValue("");
		this.custTypeCode.setDescription("");
		this.custSalutationCode.setValue("");
		this.custShrtName.setValue("");
		this.custDftBranch.setValue("");
		this.custDftBranch.setDescription("");
		this.custGenderCode.setValue("");
		this.custDOB.setText("");
		this.custBaseCcy.setValue("");
		this.custBaseCcy.setDescription("");
		this.custNationality.setValue("");
		this.custNationality.setDescription("");
		this.custLng.setValue("");
		this.custLng.setDescription("");
		this.custSts.setValue("");
		this.custSector.setValue("");
		this.custSector.setDescription("");
		this.custIndustry.setValue("");
		this.custIndustry.setDescription("");
		this.custSegment.setValue("");
		this.custSegment.setDescription("");
		this.custCOB.setValue("");
		this.custCOB.setDescription("");
		this.custMaritalSts.setValue("");
		this.custFirstName.setValue("");
		this.custMiddleName.setValue("");
		this.custLastName.setValue("");
		this.custLocalLngName.setValue("");
		this.motherMaidenName.setValue("");
		this.custRO1.setValue("");
		this.custRO1.setDescription("");
		this.eidNumber.setValue("");
		this.custTradeLicenceNum.setValue("");
		this.empStatus.setValue("");
		this.empName.setValue("");
		this.empNameOther.setValue("");
		this.empDesg.setValue("");
		this.empDept.setValue("");
		this.empFrom.setText("");
		this.monthlyIncome.setValue("");
		this.additionalIncome.setValue("");
		this.profession.setValue("");
		this.target.setValue("", "");
		this.custGroupId.setValue("");
		this.custGroupId.setDescription("");
		this.custStaffID.setValue("");
		this.custDSACode.setValue("");
		this.custDSADept.setValue("");
		this.custParentCountry.setValue("");
		this.custRiskCountry.setValue("");
		this.custSubSector.setValue("");
		this.custSubSegment.setValue("");
		this.custDSADept.setDescription("");
		this.custParentCountry.setDescription("");
		this.custRiskCountry.setDescription("");
		this.custSubSector.setDescription("");
		this.custSubSegment.setDescription("");

		this.subCategory.setSelectedIndex(0);
		this.caste.setValue("");
		this.caste.setDescription("");
		this.religion.setDescription("");
		this.natureOfBusiness.setSelectedIndex(0);
		// this.custResidentialStstus.setSelectedIndex(0);
		this.entityType.setSelectedIndex(0);
		// this.custFlags.setDescription("");
		// this.custResidentialStstus.setValue("");
		this.custQualification.setValue("");
		this.custQualification.setDescription("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table.
	 */
	public void doSave() {
		logger.debug("Entering");

		CustomerDetails aCustomerDetails = ObjectUtil.clone(getCustomerDetails());
		boolean isNew = false;
		Customer aCustomer = aCustomerDetails.getCustomer();

		// fill the Customer object with the components data
		doWriteComponentsToBean(aCustomerDetails, null);
		aCustomer = aCustomerDetails.getCustomer();

		// Calling the Customer Dedup and showing the List of dedup customers in
		// screen and user will select any one of them and proceed.
		if (aCustomerDetails.isNewRecord() && !dedupCheckReq) {
			try {
				List<CustomerDedup> customerDedupList = dedupParmService.getDedupCustomerDetails(aCustomerDetails, null,
						null);
				if (customerDedupList != null) {
					logger.debug("CustomerDedupList Found: " + customerDedupList.size());
					// call the ZUL-file with the parameters packed in a map
					if (CollectionUtils.isNotEmpty(customerDedupList)) {
						this.customerDetails.setCustomerDedupList(customerDedupList);
						final Map<String, Object> map = new HashMap<String, Object>();
						map.put("parentWindow", window_CustomerDialog);
						map.put("customerDetails", this.customerDetails);
						map.put("CustomerDialogCtrl", this);
						map.put("isFromCustomer", true);

						Executions.createComponents("/WEB-INF/pages/Finance/CustomerDedUp/CustomerDedupDialog.zul",
								null, map);
						return;
					} else {
						logger.debug("No dedup list found.");
						if (MessageUtil.confirm("No dedup list found.",
								MessageUtil.CANCEL | MessageUtil.OVERIDE) == MessageUtil.CANCEL) {
							return;
						}
					}
				}
			} catch (InterfaceException e) {
				logger.debug(Literal.EXCEPTION, e);
				StringBuilder msg = new StringBuilder();
				if (e != null) {
					msg = msg.append(e.getErrorCode());
					msg = msg.append(e.getMessage());
				}
				if (MessageUtil.confirm(msg.toString(),
						MessageUtil.CANCEL | MessageUtil.OVERIDE) == MessageUtil.CANCEL) {
					return;
				}
			}
		}

		// verify pan validated or not
		if (StringUtils.isNotEmpty(this.eidNumber.getValue()) && this.masterDef != null
				&& this.masterDef.isProceedException()) {
			if (!this.isKYCverified) {
				MessageUtil.showError(this.masterDef.getKeyType() + " Number Must Be Verifed.");
			}
		}
		// validate customer PhoneNumber types
		if (!validatePhoneTypes(aCustomerDetails.getCustomerPhoneNumList())) {
			this.tabkYCDetails.setSelected(true);
			return;
		}
		// validate customer Email types
		if (!validateEmailTypes(aCustomerDetails.getCustomerEMailList())) {
			this.tabkYCDetails.setSelected(true);
			return;
		}
		if (validateCustDocs && !validateCustomerDocuments(aCustomer, null)) {
			return;
		}

		if (!isNewCustCret && (StringUtils.equals("Submit", userAction.getSelectedItem().getLabel())
				|| StringUtils.equals("Approve", userAction.getSelectedItem().getLabel()))) {
			if (!validateAddressDetails(this.tabkYCDetails)) {
				return;
			}
			if (!validatePhoneDetails(this.tabkYCDetails)) {
				return;
			}
			if (!validateEmailDetails(this.tabkYCDetails)) {
				return;
			}
			if (isRetailCustomer && !validateEmployemntDetails(this.tabkYCDetails)) {
				return;
			}

		} else {
			if (!validateAddressDetails(this.tabkYCDetails)) {
				return;
			}
			if (!validatePhoneDetails(this.tabkYCDetails)) {
				return;
			}
			if (!validateEmailDetails(this.tabkYCDetails)) {
				return;
			}
			if (isRetailCustomer && !validateEmployemntDetails(this.tabkYCDetails)) {
				return;
			}
		}
		// validate customer income and expense types
		if (!validateIncomeTypes(aCustomerDetails.getCustomerIncomeList())) {
			this.tabfinancial.setSelected(true);
			return;
		}
		// Black list
		if (ImplementationConstants.DEDUP_BLACKLIST_COAPP && financeMain != null) {
			doCheckBlackList(aCustomerDetails);
			if (aCustomerDetails.isBlackListReq()) {
				return;
			}
		}

		// GST Detail validation
		int defaultCount = 0;
		if (CollectionUtils.isNotEmpty(aCustomerDetails.getGstDetailsList())) {
			for (GSTDetail detail : aCustomerDetails.getGstDetailsList()) {
				if (detail.isDefaultGST()) {
					defaultCount++;
				}
			}
		}
		if (defaultCount > 1) {
			String msg = Labels.getLabel("CustomerGST_Default");
			MessageUtil.showError(msg);
			return;
		}

		CustEmployeeDetail custEmployeeDetail = aCustomerDetails.getCustEmployeeDetail();
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		isNew = aCustomerDetails.isNewRecord();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomer.getRecordType())) {
				aCustomer.setVersion(aCustomer.getVersion() + 1);
				custEmployeeDetail.setVersion(custEmployeeDetail.getVersion() + 1);
				if (isNew) {
					aCustomer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomerDetails.setNewRecord(true);
					aCustomer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomer.setNewRecord(true);
				}
			}
		} else {
			aCustomer.setVersion(aCustomer.getVersion() + 1);
			custEmployeeDetail.setVersion(custEmployeeDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		custEmployeeDetail.setNewRecord(aCustomer.isNewRecord());
		custEmployeeDetail.setRecordType(aCustomer.getRecordType());
		custEmployeeDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		custEmployeeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		custEmployeeDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (!isRetailCustomer || ImplementationConstants.ALLOW_MULTIPLE_EMPLOYMENTS) {
			aCustomerDetails.setCustEmployeeDetail(null);
		}

		// save it to database
		try {
			aCustomerDetails.setCustomer(aCustomer);
			// Customer Dedup Process Check
			boolean processCompleted = doCustomerDedupe(aCustomerDetails);
			if (!processCompleted) {
				return;
			} else {
				if (aCustomerDetails.getCustomerDedupList() != null
						&& !aCustomerDetails.getCustomerDedupList().isEmpty()) {
					CustomerDedup dedup = aCustomerDetails.getCustomerDedupList().get(0);
					if (dedup != null) {
						aCustomerDetails.getCustomer().setCustCoreBank(dedup.getCustCoreBank());
					}
					logger.debug("Posidex Id:" + dedup.getCustCoreBank());
				}
			}

			// in case of no match found from posidex the same message has to be
			// shown for the user
			if (aCustomerDetails.getReturnStatus() != null && aCustomerDetails.getReturnStatus().getReturnText() != null
					&& StringUtils.equalsIgnoreCase(aCustomerDetails.getReturnStatus().getReturnText(), "No Match")) {
				MessageUtil.showMessage(Labels.getLabel("Label_Dedupe_NoMatch"));
			}

			if (doProcess(aCustomerDetails, tranType)) {
				// ExtendedFields Rights Deallocation.
				if (extendedFieldCtrl != null && customerDetails.getExtendedFieldHeader() != null) {
					extendedFieldCtrl.deAllocateAuthorities();
				}
				// User Notification for Role Identification
				if (StringUtils.isBlank(aCustomer.getNextTaskId())) {
					aCustomer.setNextRoleCode("");
				}

				String rcdStatus = "";
				if (isNewCustCret) {
					rcdStatus = PennantConstants.RCD_STATUS_APPROVED;
				} else {
					rcdStatus = aCustomer.getRecordStatus();
				}
				String msg = PennantApplicationUtil.getSavingStatus(aCustomer.getRoleCode(),
						aCustomer.getNextRoleCode(), aCustomer.getCustCIF(), " Customer ", rcdStatus);
				Clients.showNotification(msg, "info", null, null, -1);
				if (!isNewCustCret) {
					refreshList();
				}
				closeDialog();
				if (this.jointAccountDetailDialogCtrl != null) {
					this.jointAccountDetailDialogCtrl.setNewCustCIF(aCustomer.getCustCIF());
				}
			}
			logger.debug(" Calling doSave method completed Successfully");
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private void doCheckBlackList(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);

		String curLoginUser = getUserWorkspace().getLoggedInUser().getUserName();
		BlackListCustomers balckListData = setBlackListCustomerData(customerDetails, customerDetails.getCustCIF());
		List<BlackListCustomers> blackList = getDedupParmService().fetchBlackListCustomers(getRole(), "", balckListData,
				curLoginUser);
		int userAction = -1;
		ShowBlackListDetailBox details = null;
		if (CollectionUtils.isNotEmpty(blackList)) {
			String BLACKLIST_FIELDS = "";
			if (ImplementationConstants.ALLOW_SIMILARITY && App.DATABASE == Database.POSTGRES) {
				BLACKLIST_FIELDS = "custCIF,custDOB,custFName,custCRCPR,"
						+ "custPassportNo,mobileNumber,custNationality,employer,address,custAadhaar,watchListRule,override,overridenby";
			}

			Object dataObject = ShowBlackListDetailBox.show(window_CustomerDialog, blackList, BLACKLIST_FIELDS,
					balckListData, "");
			details = (ShowBlackListDetailBox) dataObject;

			if (details != null) {
				logger.debug("The User Action is " + details.getUserAction());
				userAction = details.getUserAction();
			}
		}
		/**
		 * userAction represents Clean or Blacklisted actions if user click on Clean button userAction = 1 if user click
		 * on Blacklisted button userAction = 0 if no customer found as a blacklist customer then userAction = -1
		 */
		if (userAction == 0) {
			customerDetails.setBlackListReq(true);
		} else {
			customerDetails.setBlackListReq(false);
		}

		logger.debug(Literal.LEAVING);
	}

	private BlackListCustomers setBlackListCustomerData(CustomerDetails customerDetails, String finReference) {
		logger.debug("Entering");

		Customer customer = null;
		String mobileNumber = "";
		StringBuilder custAddress = new StringBuilder("");

		if (customerDetails.getCustomer() != null) {
			customer = customerDetails.getCustomer();
			if (customerDetails.getCustomerPhoneNumList() != null) {
				for (CustomerPhoneNumber custPhone : customerDetails.getCustomerPhoneNumList()) {
					if (custPhone.getPhoneTypeCode().equals(PennantConstants.PHONETYPE_MOBILE)) {
						mobileNumber = PennantApplicationUtil.formatPhoneNumber(custPhone.getPhoneCountryCode(),
								custPhone.getPhoneAreaCode(), custPhone.getPhoneNumber());
						break;
					}
				}
			}
		}

		if (customerDetails.getCustomer() != null) {
			customer = customerDetails.getCustomer();
			if (customerDetails.getAddressList() != null) {
				for (CustomerAddres address : customerDetails.getAddressList()) {
					if (address.getCustAddrPriority() == Integer.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
						custAddress.append(address.getCustAddrHNbr()).append(", ");
						custAddress.append(address.getCustAddrStreet()).append(", ");

						if (ImplementationConstants.CUSTOM_BLACKLIST_PARAMS) {
							custAddress.append(StringUtils.isNotEmpty(address.getCustAddrLine2())
									? address.getCustAddrLine2().concat(", ")
									: "");
							custAddress.append(StringUtils.isNotEmpty(address.getCustAddrLine1())
									? address.getCustAddrLine1().concat(", ")
									: "");
							custAddress.append(address.getLovDescCustAddrCityName()).append(", ");
							custAddress.append(address.getLovDescCustAddrProvinceName()).append(", ");
							custAddress.append(address.getLovDescCustAddrCountryName()).append(", ");
							custAddress.append(address.getCustAddrZIP());
						} else {
							custAddress.append(address.getCustAddrCity()).append(", ");
							custAddress.append(address.getCustAddrProvince()).append(", ");
							custAddress.append(address.getCustAddrCountry());
						}
						break;
					}
				}
			}
		}

		// CustData to Black List
		BlackListCustomers blackListCustomer = null;
		if (customer != null) {
			blackListCustomer = new BlackListCustomers();
			blackListCustomer.setCustCIF(customer.getCustCIF());
			blackListCustomer.setCustShrtName(customer.getCustShrtName());
			blackListCustomer.setCustFName(customer.getCustFName());
			blackListCustomer.setCustLName(customer.getCustLName());
			blackListCustomer.setCustCRCPR(customer.getCustCRCPR());
			blackListCustomer.setCustPassportNo(customer.getCustPassportNo());
			blackListCustomer.setMobileNumber(mobileNumber);
			blackListCustomer.setCustNationality(customer.getCustNationality());
			blackListCustomer.setCustDOB(customer.getCustDOB());
			blackListCustomer.setCustCtgCode(customer.getCustCtgCode());
			blackListCustomer.setFinReference(finReference);

			blackListCustomer.setLikeCustFName(
					blackListCustomer.getCustFName() != null ? "%" + blackListCustomer.getCustFName() + "%" : "");
			blackListCustomer.setLikeCustLName(
					blackListCustomer.getCustLName() != null ? "%" + blackListCustomer.getCustLName() + "%" : "");
			// setting additional details data
			blackListCustomer = FetchBlackListCustomerAdditionalDetails.doSetCustDataToBlackList(customer,
					blackListCustomer);
		}

		if (blackListCustomer != null) {
			blackListCustomer.setAddress(custAddress.toString());
		}

		// setting the customer documents data
		String aadharCode = masterDefDAO.getMasterCode(PennantConstants.DOC_TYPE, DocType.AADHAAR.name());
		String passPortCode = masterDefDAO.getMasterCode(PennantConstants.DOC_TYPE, DocType.PASSPORT.name());
		String voterIdCode = masterDefDAO.getMasterCode(PennantConstants.DOC_TYPE, DocType.VOTER_ID.name());
		String drivingLicenseCode = masterDefDAO.getMasterCode(PennantConstants.DOC_TYPE,
				DocType.DRIVING_LICENCE.name());
		String panCode = masterDefDAO.getMasterCode(PennantConstants.DOC_TYPE, DocType.PAN.name());
		if (customerDetails != null && customerDetails.getCustomerDocumentsList() != null) {
			for (CustomerDocument document : customerDetails.getCustomerDocumentsList()) {
				if (StringUtils.equals(aadharCode, document.getCustDocCategory())) { // Aadhar
					blackListCustomer.setCustAadhaar(document.getCustDocTitle());
				} else if (StringUtils.equals(passPortCode, document.getCustDocCategory())) { // Passport
					blackListCustomer.setCustPassportNo(document.getCustDocTitle());
				} else if (StringUtils.equals(drivingLicenseCode, document.getCustDocCategory())) {// Driving
																									// License
					blackListCustomer.setDl(document.getCustDocTitle());
				} else if (StringUtils.equals(voterIdCode, document.getCustDocCategory())) {// VoterId
					blackListCustomer.setVid(document.getCustDocTitle());
				} else if (StringUtils.equals(panCode, document.getCustDocCategory())) {// PAN
					blackListCustomer.setCustCRCPR(document.getCustDocTitle());
				}

			}
		}

		logger.debug("Leaving");
		return blackListCustomer;

	}

	private boolean doCustomerDedupe(CustomerDetails customerDetails) {
		logger.debug("Entering");

		String corebank = customerDetails.getCustomer().getCustCoreBank();

		// If Core Bank ID is Exists then Customer is already existed in Core
		// Banking System
		if ("Y".equalsIgnoreCase(SysParamUtil.getValueAsString("POSIDEX_DEDUP_REQD"))) {
			if (StringUtils.equals("Submit", userAction.getSelectedItem().getLabel())
					&& StringUtils.isBlank(corebank)) {
				String curLoginUser = getUserWorkspace().getUserDetails().getSecurityUser().getUsrLogin();
				customerDetails = FetchFinCustomerDedupDetails.getFinCustomerDedup(getRole(),
						SysParamUtil.getValueAsString("FINONE_DEF_FINTYPE"), customerDetails.getCustomer().getCustCIF(),
						customerDetails, this.window_CustomerDialog, curLoginUser);

				if (customerDetails.getCustomer().isDedupFound() && !customerDetails.getCustomer().isSkipDedup()) {
					return false;
				} else {
					return true;
				}
			}
		}

		logger.debug("Leaving");
		return true;
	}

	private String getServiceTasks(String taskId, Customer aCustomer, String finishedTasks) {
		String serviceTasks;
		serviceTasks = getServiceOperations(taskId, aCustomer);
		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");
			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		return serviceTasks;
	}

	private void setNextTaskDetails(String taskId, Customer aCustomer) {
		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(aCustomer.getNextTaskId());
		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}
		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, aCustomer);
		}
		// Set the role codes for the next tasks
		String nextRoleCode = "";
		if (StringUtils.isBlank(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");
			if (nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode + "," + getTaskOwner(nextTasks[i]);
					} else {
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				}
			} else {
				nextRoleCode = getTaskOwner(nextTaskId);
			}
		}
		aCustomer.setTaskId(taskId);
		aCustomer.setNextTaskId(nextTaskId);
		aCustomer.setRoleCode(getRole());
		aCustomer.setNextRoleCode(nextRoleCode);
	}

	protected boolean doProcess(CustomerDetails aCustomerDetails, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		Customer aCustomer = aCustomerDetails.getCustomer();
		aCustomer.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCustomer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomer.setUserDetails(getUserWorkspace().getLoggedInUser());
		aCustomerDetails.setCustID(aCustomer.getCustID());
		aCustomerDetails.setCustomer(aCustomer);
		aCustomerDetails.setUserDetails(getUserWorkspace().getLoggedInUser());

		// Extended Field details
		if (aCustomerDetails.getExtendedFieldRender() != null) {
			int seqNo = 0;
			ExtendedFieldRender details = aCustomerDetails.getExtendedFieldRender();
			details.setReference(aCustomerDetails.getCustomer().getCustCIF());
			details.setSeqNo(++seqNo);
			details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			details.setRecordStatus(aCustomerDetails.getCustomer().getRecordStatus());

			if (!isNewCustCret) {
				details.setRecordType(aCustomerDetails.getCustomer().getRecordType());
				details.setNewRecord(aCustomerDetails.getCustomer().isNewRecord());
			} else {
				if (StringUtils.isEmpty(aCustomerDetails.getCustomer().getRecordType())
						&& StringUtils.isEmpty(details.getRecordType())) {
					if (aCustomerDetails.getCustomer().isNewRecord()) {
						details.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						details.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}
					details.setNewRecord(aCustomerDetails.getCustomer().isNewRecord());
				}
			}

			details.setVersion(aCustomerDetails.getCustomer().getVersion());
			details.setWorkflowId(aCustomerDetails.getCustomer().getWorkflowId());
			details.setTaskId(taskId);
			details.setNextTaskId(nextTaskId);
			details.setRoleCode(getRole());
			details.setNextRoleCode(nextRoleCode);
			if (PennantConstants.RECORD_TYPE_DEL.equals(aCustomerDetails.getCustomer().getRecordType())) {
				if (StringUtils.trimToNull(details.getRecordType()) == null) {
					details.setRecordType(aCustomerDetails.getCustomer().getRecordType());
					details.setNewRecord(true);
				}
			}
		}

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			aCustomer.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			// Check whether required auditing notes entered or not
			if (isNotesMandatory(taskId, aCustomer)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			// ### 19-06-2018 PSD 127035:TO handle service level validations
			// before calling service Tasks
			auditHeader = getAuditHeader(aCustomerDetails, PennantConstants.TRAN_WF);
			auditHeader = customerDetailsService.preValidate(auditHeader);
			int retValue = PennantConstants.porcessOVERIDE;
			while (retValue == PennantConstants.porcessOVERIDE) {
				boolean procesCompleted = false;
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE) {
					procesCompleted = true;
				}
				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
				setOverideMap(auditHeader.getOverideMap());
				if (!procesCompleted) {
					return false;
				}
			}
			// ### 19-06-2018 - End

			boolean alwExtCustDedup = SysParamUtil.isAllowed(SMTParameterConstants.EXTERNAL_CUSTOMER_DEDUP);

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, aCustomer, finishedTasks);
			auditHeader = getAuditHeader(aCustomerDetails, PennantConstants.TRAN_WF);
			while (!"".equals(serviceTasks)) {
				String method = serviceTasks.split(";")[0];
				if ("doDdeDedup".equals(method) || "doVerifierDedup".equals(method)
						|| "doApproverDedup".equals(method)) {
					if ((alwExtCustDedup
							&& (PennantConstants.RCD_STATUS_SUBMITTED.equals(aCustomer.getRecordStatus())
									&& PennantConstants.RECORD_TYPE_NEW.equals(aCustomer.getRecordType()))
							&& StringUtils.trimToNull(aCustomer.getCustCoreBank()) == null) || !alwExtCustDedup) {
						CustomerDetails tCustomerDetails = (CustomerDetails) auditHeader.getAuditDetail()
								.getModelData();
						String curLoginUser = getUserWorkspace().getUserDetails().getSecurityUser().getUsrLogin();
						tCustomerDetails = FetchCustomerDedupDetails.getCustomerDedup(getRole(), tCustomerDetails,
								this.window_CustomerDialog, curLoginUser, "");
						if (tCustomerDetails.getCustomer().isDedupFound()
								&& !tCustomerDetails.getCustomer().isSkipDedup()) {
							processCompleted = false;
						} else {
							processCompleted = true;
						}
						auditHeader.getAuditDetail().setModelData(tCustomerDetails);
					}
				} else {
					CustomerDetails tCustomerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
					tCustomerDetails.setCustomer(aCustomer);
					setNextTaskDetails(taskId, tCustomerDetails.getCustomer());
					auditHeader.getAuditDetail().setModelData(tCustomerDetails);
					processCompleted = doSaveProcess(auditHeader, method);
				}
				if (!processCompleted) {
					break;
				}
				finishedTasks += (method + ";");
				serviceTasks = getServiceTasks(taskId, aCustomer, finishedTasks);
			}
			// Check Dedup if Prospect Customer
			// &&
			// StringUtils.trimToEmpty(aCustomerDetails.getCustomer().getRecordType()).equals("")
			if (processCompleted && !aCustomerDetails.getCustomer().isSkipDedup()) {
				if (StringUtils.isBlank(aCustomerDetails.getCustomer().getCustCoreBank())) {
					aCustomerDetails = FetchDedupDetails.getCustomerDedup(getRole(), aCustomerDetails,
							this.window_CustomerDialog);
					if (aCustomerDetails.getCustomer().isDedupFound()
							&& !aCustomerDetails.getCustomer().isSkipDedup()) {
						processCompleted = false;
					} else {
						processCompleted = true;
					}
				}
			}
			// Check whether to proceed further or not
			String nextTaskId = "";
			Object object = auditHeader.getAuditDetail().getModelData();
			if (object instanceof CustomerDetails) {
				CustomerDetails tCustomerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
				nextTaskId = getNextTaskIds(taskId, tCustomerDetails.getCustomer());
			} else {
				nextTaskId = getNextTaskIds(taskId, object);
			}
			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}
			// Proceed further to save the details in workflow
			if (processCompleted) {
				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, aCustomer);
					aCustomerDetails.setCustomer(aCustomer);
					auditHeader = getAuditHeader(aCustomerDetails, tranType);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}
		} else {
			// checking customer dedupe from loan origination
			String curLoginUser = getUserWorkspace().getUserDetails().getSecurityUser().getUsrLogin();
			aCustomerDetails = FetchCustomerDedupDetails.getCustomerDedup(getRole(), aCustomerDetails,
					this.window_CustomerDialog, curLoginUser, "");
			if (aCustomerDetails.getCustomer().isDedupFound() && !aCustomerDetails.getCustomer().isSkipDedup()) {
				return false;
			}
			auditHeader = getAuditHeader(aCustomerDetails, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		CustomerDetails aCustomerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
		Customer aCustomer = aCustomerDetails.getCustomer();
		boolean deleteNotes = false;
		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getCustomerDetailsService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getCustomerDetailsService().saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					aCustomer.setApprovedOn(new Timestamp(System.currentTimeMillis()));
					aCustomer.setApprovedBy(getUserWorkspace().getLoggedInUser().getUserId());
					auditHeader = getCustomerDetailsService().doApprove(auditHeader);
					if (aCustomer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getCustomerDetailsService().doReject(auditHeader);
					if (aCustomer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doFinnov)) {
					if (finnovService != null) {
						auditHeader = finnovService.getFinnovReport(auditHeader);
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_notifyCrm)) {
					if (crm != null && "Y".equals(SysParamUtil.getValueAsString("EXT_CRM_INT_ENABLED"))) {
						if (StringUtils.isEmpty(aCustomerDetails.getCustomer().getCustCoreBank())) {
							customerDetails = crm.create(aCustomerDetails);
						}
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_CustomerDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_CustomerDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
				if (deleteNotes) {
					deleteNotes(getNotes(), true);
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

	public void doSetCustomerData(CustomerDetails customerDetails) {
		doClearErrorMessage();
		doRemoveValidation();
		doRemoveLOVValidation();
		setCustomerDetails(customerDetails);
		doWriteBeanToComponents(customerDetails);
	}

	public void doSave_CustomerDetail(FinanceDetail aFinanceDetail, boolean validatePhoneNum) {
		doSave_CustomerDetail(aFinanceDetail, null, validatePhoneNum);
	}

	/**
	 * This method set the customer details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 */
	public boolean doSave_CustomerDetail(FinanceDetail aFinanceDetail, Tab tab, boolean validateChildDetails) {
		logger.debug("Entering ");
		if (getCustomerDetails() != null) {
			CustomerDetails aCustomerDetails = ObjectUtil.clone(getCustomerDetails());
			boolean isNew = false;
			Customer aCustomer = aCustomerDetails.getCustomer();
			aCustomer.setWorkflowId(0);
			aCustomer.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			aCustomer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aCustomer.setUserDetails(getUserWorkspace().getLoggedInUser());
			// Write the additional validations as per below example
			// get the selected branch object from the listbox
			// Do data level validations here
			isNew = aCustomerDetails.isNewRecord();
			aCustomer.setNewRecord(isNew);
			if (StringUtils.isBlank(aCustomer.getRecordType())) {
				if (isNew) {
					aCustomer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			}

			CustEmployeeDetail custEmployeeDetail = aCustomerDetails.getCustEmployeeDetail();
			custEmployeeDetail.setNewRecord(aCustomer.isNewRecord());
			custEmployeeDetail.setRecordType(aCustomer.getRecordType());
			custEmployeeDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			custEmployeeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			custEmployeeDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

			if (aFinanceDetail.getUserAction() != null) {
				if ("Save".equalsIgnoreCase(aFinanceDetail.getUserAction())
						|| "Cancel".equalsIgnoreCase(aFinanceDetail.getUserAction())
						|| aFinanceDetail.getUserAction().contains("Reject")
						|| aFinanceDetail.getUserAction().contains("Resubmit")) {
					validateAllDetails = false;
				} else {
					validateAllDetails = true;
				}
			} else {
				validateAllDetails = true;
			}
			doWriteComponentsToBean(aCustomerDetails, tab);

			// Extended Field details
			if (aCustomerDetails.getExtendedFieldRender() != null) {
				int seqNo = 0;
				ExtendedFieldRender details = aCustomerDetails.getExtendedFieldRender();
				details.setSeqNo(++seqNo);
				details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			}

			if (validateChildDetails) {
				// validate customer PhoneNumber types
				if (!validatePhoneTypes(aCustomerDetails.getCustomerPhoneNumList())) {
					this.tabkYCDetails.setSelected(true);
					return false;
				}
				// validate customer Email types
				if (!validateEmailTypes(aCustomerDetails.getCustomerEMailList())) {
					this.tabkYCDetails.setSelected(true);
					return false;
				}
				// To throw validations at Loan level Retail Customer based on ImplementationConstant
				if ("Save".equalsIgnoreCase(aFinanceDetail.getUserAction()) && isRetailCustomer && !isPanMandatory
						&& StringUtils.isBlank(this.eidNumber.getValue()) && validateCustDocs
						&& !validateCustomerDocuments(aCustomer, tab)) {
					return false;
				}
				if (validateCustDocs && validateAllDetails && !validateCustomerDocuments(aCustomer, tab)) {
					return false;
				}
				if (!validateAddressDetails(tab)) {
					return false;
				}
				if (!validatePhoneDetails(tab)) {
					return false;
				}
				if (!validateEmailDetails(tab)) {
					return false;
				}
				if (isRetailCustomer && !validateEmployemntDetails(tab)) {
					return false;
				}
				if (this.btnNew_CustomerPhoneNumber.isVisible() && validateAllDetails) {
					boolean isphonenum = false;
					boolean iscustNationality = false;
					boolean iscustemployeestatus = false;

					String msg = null;
					if (!isPhoneNumberExist()) {
						isphonenum = true;
						msg = Labels.getLabel("CustomerPhoneNumber_NoEmpty");
					} else if (!doCheckCustNationality()) {
						iscustNationality = true;
						msg = Labels.getLabel("CustomerHomeNumber_NoEmpty");
					} else if (!doCheckCustEmployeeStatus()) {
						iscustemployeestatus = true;
						msg = Labels.getLabel("CustomerOfficeNumber_NoEmpty");
					}
					if (isphonenum || iscustNationality || iscustemployeestatus) {
						if (tab != null) {
							tab.setSelected(true);
							this.tabkYCDetails.setSelected(true);
						}

						MessageUtil.showError(msg);

						return false;
					}
				}
			}

			// verify pan validated or not
			if (StringUtils.isNotEmpty(this.eidNumber.getValue()) && this.masterDef != null
					&& this.masterDef.isProceedException()) {
				if (!this.isKYCverified) {
					MessageUtil.showError(this.masterDef.getKeyType() + " Number Must Be Verifed.");
					return false;
				}
			}

			if (!isRetailCustomer || ImplementationConstants.ALLOW_MULTIPLE_EMPLOYMENTS) {
				aCustomerDetails.setCustEmployeeDetail(null);
			}
			// validate customer income and expense types
			if (!validateIncomeTypes(aCustomerDetails.getCustomerIncomeList())) {
				this.tabfinancial.setSelected(true);
				return false;
			}
			// checking customer dedupe from loan origination
			if (validateAllDetails && validateChildDetails) {
				tab.setSelected(true);
				CustomerDetails tCustomerDetails = aCustomerDetails;
				String finType = aFinanceDetail.getFinScheduleData().getFinanceMain().getFinType();
				String curLoginUser = getUserWorkspace().getUserDetails().getSecurityUser().getUsrLogin();
				tCustomerDetails = FetchCustomerDedupDetails.getCustomerDedupDetails(getRole(), tCustomerDetails,
						this.window_CustomerDialog, curLoginUser, finType);
				// When user Clicking on Close Button returns same page.
				if (tCustomerDetails.getCustomer().isDedupFound() && !tCustomerDetails.getCustomer().isSkipDedup()) {
					return false;
				}
			}
			FinanceMain financeMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
			financeMain.setCustEmpType(aCustomer.getSubCategory());

			aFinanceDetail.setCustomerDetails(aCustomerDetails);
		}
		logger.debug("Leaving ");
		return true;
	}

	private boolean validateEmailDetails(Tab tab) {
		logger.debug("Entering");
		boolean isMandAddExist = false;
		if (CollectionUtils.isEmpty(customerEmailDetailList)) {
			return !isMandAddExist;
		} else {
			for (CustomerEMail custEmail : this.customerEmailDetailList) {
				if (StringUtils.equals(PennantConstants.KYC_PRIORITY_VERY_HIGH,
						String.valueOf(custEmail.getCustEMailPriority()))) {
					isMandAddExist = true;
					break;
				}
			}

			if (!isMandAddExist) {
				this.tabkYCDetails.setSelected(true);
				if (tab != null) {
					tab.setSelected(true);
				}
				this.tabkYCDetails.setSelected(true);
				String msg = Labels.getLabel("CustomerEmail_High_Priority");
				MessageUtil.showError(msg);
			}
		}

		logger.debug("Leaving");
		return isMandAddExist;
	}

	private boolean validateCustomerDocuments(Tab tab) {
		logger.debug("Entering");
		boolean isMandAddExist = false;
		if (!this.customerDocumentDetailList.isEmpty()) {
			isMandAddExist = true;
		}

		if (!isMandAddExist) {
			this.tabkYCDetails.setSelected(true);
			if (tab != null) {
				tab.setSelected(true);
			}
			this.tabkYCDetails.setSelected(true);
			String msg = Labels.getLabel("CustomerDocuments_Required");
			MessageUtil.showError(msg);
		}

		logger.debug("Leaving");
		return isMandAddExist;
	}

	private boolean validateAddressDetails(Tab custTab) {
		logger.debug("Entering");
		boolean isMandAddExist = false;
		if (this.customerAddressDetailList != null && !this.customerAddressDetailList.isEmpty()) {
			for (CustomerAddres custAddres : this.customerAddressDetailList) {

				if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
					if (isRetailCustomer) {
						if (StringUtils.equalsIgnoreCase(SysParamUtil.getValueAsString("CURRENT_RES_ADDRESS"),
								custAddres.getCustAddrType())) {
							isMandAddExist = true;
							break;
						}
					} else {
						if (StringUtils.equalsIgnoreCase(PennantConstants.ADDRESS_TYPE_OFFICE,
								custAddres.getCustAddrType())) {
							isMandAddExist = true;
							break;
						}
					}
				} else {
					if (StringUtils.equals(PennantConstants.KYC_PRIORITY_VERY_HIGH,
							String.valueOf(custAddres.getCustAddrPriority()))
							&& !StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, custAddres.getRecordType())) {
						isMandAddExist = true;
						break;
					}

				}

			}
		}
		if (!isMandAddExist) {
			this.tabkYCDetails.setSelected(true);
			if (custTab != null) {
				custTab.setSelected(true);
			}
			this.tabkYCDetails.setSelected(true);
			if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
				String msg = isRetailCustomer ? Labels.getLabel("CustomerResidenceAddress_NoEmpty")
						: Labels.getLabel("CustomerOfficeAddress_NoEmpty");
				MessageUtil.showError(msg);
			} else {
				String msg = Labels.getLabel("CustomerResidenceAddress_High_Priority");
				MessageUtil.showError(msg);
			}
		}
		logger.debug("Leaving");
		return isMandAddExist;
	}

	private boolean validatePhoneDetails(Tab custTab) {
		logger.debug("Entering");
		boolean isMandAddExist = false;
		if (this.customerPhoneNumberDetailList != null && !this.customerPhoneNumberDetailList.isEmpty()) {
			for (CustomerPhoneNumber customerPhone : this.customerPhoneNumberDetailList) {
				if (StringUtils.equals(PennantConstants.KYC_PRIORITY_VERY_HIGH,
						String.valueOf(customerPhone.getPhoneTypePriority()))
						&& !StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, customerPhone.getRecordType())) {
					isMandAddExist = true;
					break;
				}
			}

		}
		if (!isMandAddExist) {

			this.tabkYCDetails.setSelected(true);
			if (custTab != null) {
				custTab.setSelected(true);
			}
			this.tabkYCDetails.setSelected(true);

			String msg = Labels.getLabel("CustomerPhoneNumber_High_Priority");
			MessageUtil.showError(msg);

		}
		logger.debug("Leaving");

		return isMandAddExist;
	}

	private boolean validateEmployemntDetails(Tab custTab) {
		logger.debug("Entering");
		boolean isvalidEmployment = true;
		if (this.customerEmploymentDetailList != null && !this.customerEmploymentDetailList.isEmpty()) {
			for (CustomerEmploymentDetail custEmployment : this.customerEmploymentDetailList) {
				if (custEmployment.getCustEmpFrom() != null && this.custDOB.getValue() != null
						&& custEmployment.getCustEmpFrom().before(this.custDOB.getValue())) {
					isvalidEmployment = false;
					break;
				}
			}

			if (!isvalidEmployment) {
				this.tabkYCDetails.setSelected(true);
				if (custTab != null) {
					custTab.setSelected(true);
				}
				this.tabkYCDetails.setSelected(true);

				String msg = Labels.getLabel("CustomerEmployment_NotBefore_DOB");
				MessageUtil.showError(msg);

			}
		}
		return isvalidEmployment;
	}

	private boolean isPhoneNumberExist() {
		logger.debug("Entering ");
		if (this.customerPhoneNumberDetailList == null || this.customerPhoneNumberDetailList.isEmpty()) {
			return false;
		}
		boolean phNumExist = false;
		for (CustomerPhoneNumber custPhoneNumber : customerPhoneNumberDetailList) {
			if (StringUtils.equals(custPhoneNumber.getRecordType(), PennantConstants.RECORD_TYPE_DEL)
					|| StringUtils.equals(custPhoneNumber.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				phNumExist = false;
			} else {
				return true;
			}
		}
		logger.debug("Leaving ");
		return phNumExist;
	}

	public boolean doCheckCustNationality() {
		if (ImplementationConstants.INDIAN_IMPLEMENTATION) {
			return true;
		}
		boolean homePhoneExist = false;
		if (!StringUtils.equals(NotificationConstants.TEMPLATE_FOR_AE, this.custNationality.getValidatedValue())) {
			for (CustomerPhoneNumber custPhoneNumber : customerPhoneNumberDetailList) {
				if (PennantConstants.HOME_PHONE.equals(custPhoneNumber.getPhoneTypeCode())) {
					homePhoneExist = true;
				}
			}
		} else {
			homePhoneExist = true;
		}
		return homePhoneExist;
	}

	public boolean doCheckCustEmployeeStatus() {
		boolean officePhoneExist = false;
		if (StringUtils.equals(PennantConstants.CUSTEMPSTS_EMPLOYED, this.empStatus.getValidatedValue())) {
			for (CustomerPhoneNumber custPhoneNumber : customerPhoneNumberDetailList) {
				if (PennantConstants.OFFICE_PHONE.equals(custPhoneNumber.getPhoneTypeCode())) {
					officePhoneExist = true;
				}
			}
		} else {
			officePhoneExist = true;
		}
		return officePhoneExist;
	}

	public boolean validateCustomerDocuments(Customer aCustomer, Tab custTab) {
		logger.debug("Entering");
		boolean isMandateIDDocExist = false;
		if (this.customerDocumentDetailList != null && !this.customerDocumentDetailList.isEmpty()) {
			for (CustomerDocument custDocument : this.customerDocumentDetailList) {
				if (!isPanMandatory && StringUtils.isBlank(this.eidNumber.getValue())
						&& (PennantConstants.FORM60.equals(custDocument.getCustDocCategory())
								&& (PennantConstants.RECORD_TYPE_DEL.equals(custDocument.getRecordType())
										|| PennantConstants.RECORD_TYPE_CAN.equals(custDocument.getRecordType())))) {
					MessageUtil.showError(Labels.getLabel("Cannot_Delete_Form60_UnlessPAN_Captured"));
					return false;
				}
				if (custDocument.isDocIssueDateMand() && custDocument.getCustDocIssuedOn() == null) {
					doShowValidationMessage(custTab, 3, custDocument.getLovDescCustDocCategory());
					return false;
				}
				if (custDocument.isLovDescdocExpDateIsMand() && custDocument.getCustDocExpDate() == null) {
					doShowValidationMessage(custTab, 5, custDocument.getLovDescCustDocCategory());
					return false;
				}
				if (StringUtils.equals(MasterDefUtil.getDocCode(DocType.AADHAAR), custDocument.getCustDocCategory())) {
					if (isRetailCustomer && !this.custDOB.isDisabled() && this.custDOB.getValue() != null
							&& custDocument.getCustDocIssuedOn() != null
							&& custDocument.getCustDocIssuedOn().before(this.custDOB.getValue())) {
						doShowValidationMessage(custTab, 1, custDocument.getLovDescCustDocCategory());
						return false;
					}
				} else if (StringUtils.equals(MasterDefUtil.getDocCode(DocType.PAN),
						custDocument.getCustDocCategory())) {
					if (!this.custDOB.isDisabled() && this.custDOB.getValue() != null
							&& custDocument.getCustDocIssuedOn() != null
							&& custDocument.getCustDocIssuedOn().before(this.custDOB.getValue())) {
						doShowValidationMessage(custTab, 1, custDocument.getLovDescCustDocCategory());
						return false;
					}
				}
				if (StringUtils.equals(MasterDefUtil.getDocCode(DocType.PAN), custDocument.getCustDocCategory())) {
					isMandateIDDocExist = true;
					if (StringUtils.isNotBlank(this.eidNumber.getValue())) {
						if (!StringUtils.equals(this.eidNumber.getValue(), custDocument.getCustDocTitle())) {
							doShowValidationMessage(custTab, 2, custDocument.getLovDescCustDocCategory() + " Number");
							return false;
						}
					} else {
						// PAN number should not display when PAN number is not captured and pan card document is
						// deleted and saved
						if (!isPanMandatory && StringUtils.isBlank(aCustomer.getCustCRCPR()) && isRetailCustomer
								&& !PennantConstants.RECORD_TYPE_CAN.equals(custDocument.getRecordType())
								&& !PennantConstants.RECORD_TYPE_DEL.equals(custDocument.getRecordType())) {
							aCustomer.setCustCRCPR(custDocument.getCustDocTitle());
						}
					}
				}
				if (!isRetailCustomer
						&& StringUtils.equals(PennantConstants.TRADELICENSE, custDocument.getCustDocCategory())) {
					if (!this.custDOB.isDisabled() && this.custDOB.getValue() != null
							&& custDocument.getCustDocIssuedOn() != null
							&& DateUtil.compare(custDocument.getCustDocIssuedOn(), this.custDOB.getValue()) != 0) {
						doShowValidationMessage(custTab, 6, custDocument.getLovDescCustDocCategory());
						return false;
					}
				}

			}
		}

		if (CollectionUtils.isNotEmpty(customerDocumentDetailList) && !isPanMandatory) {
			boolean anyMatch = customerDocumentDetailList.stream()
					.anyMatch(docType -> docType.getCustDocCategory().equals(PennantConstants.FORM60)
							|| docType.getCustDocCategory().equals(PennantConstants.CPRCODE));
			if (StringUtils.isBlank(this.eidNumber.getValue()) && !anyMatch) {
				MessageUtil.showError(Labels.getLabel("Either_PAN_FORM60_AADHAAR_Mandatory"));
				return false;
			}
		} else if (StringUtils.isBlank(this.eidNumber.getValue())) {
			MessageUtil.showError(Labels.getLabel("Either_PAN_FORM60_AADHAAR_Mandatory"));
			return false;
		}

		if (!StringUtils.isBlank(aCustomer.getCustCRCPR()) && !isMandateIDDocExist && validateAllDetails) {
			/*
			 * doShowValidationMessage(custTab, 4, isRetailCustomer ? PennantConstants.PANNUMBER :
			 * PennantConstants.PANNUMBER); return false;
			 */
		}
		logger.debug("Leaving");
		return true;
	}

	private void doShowValidationMessage(Tab tab, int validationNo, String value) {
		String msg = "";
		this.tabkYCDetails.setSelected(true);
		if (tab != null) {
			tab.setSelected(true);
		}
		this.tabkYCDetails.setSelected(true);
		switch (validationNo) {
		case 1:
			String field = isRetailCustomer ? Labels.getLabel("label_CustomerDialog_CustDOB.value")
					: Labels.getLabel("label_CustomerDialog_CustDateOfIncorporation.value");
			msg = Labels.getLabel("DATE_NOT_BEFORE",
					new String[] { Labels.getLabel("label_CustomerDocumentDialog_CustDocType.value") + " : " + value
							+ " " + Labels.getLabel("label_CustomerDocumentDialog_CustDocIssuedOn.value"), field });
			break;
		case 2:
			msg = Labels.getLabel("EIDNumber_NotEqual",
					new String[] {
							isRetailCustomer ? Labels.getLabel("label_CustomerDialog_EIDNumber.value")
									: Labels.getLabel("label_CustomerDialog_TradeLicenseNumber.value"),
							Labels.getLabel("PersonalDetails"), value, Labels.getLabel("DocumentDetails") });
			break;
		case 3:
			msg = Labels.getLabel("DATE_NO_EMPTY",
					new String[] { Labels.getLabel("label_CustomerDocumentDialog_CustDocType.value") + " : " + value
							+ " , " + Labels.getLabel("label_CustomerDocumentDialog_CustDocIssuedOn.value") });
			break;
		case 4:
			msg = Labels.getLabel("CustomerDocuments_NoEmpty",
					new String[] { PennantApplicationUtil.getLabelDesc(value, PennantAppUtil.getDocumentTypes()) });
			break;
		case 5:
			msg = Labels.getLabel("DATE_NO_EMPTY",
					new String[] { Labels.getLabel("label_CustomerDocumentDialog_CustDocType.value") + " : " + value
							+ " , " + Labels.getLabel("label_CustomerDocumentDialog_CustDocExpDate.value") });
			break;
		case 6:
			msg = Labels.getLabel("EIDNumber_NotEqual",
					new String[] { Labels.getLabel("label_CustomerDialog_CustDateOfIncorporation.value"),
							Labels.getLabel("PersonalDetails"), value, Labels.getLabel("DocumentDetails") });
			break;

		case 7:
			msg = Labels.getLabel("EIDNumber_NotEqual",
					new String[] {
							isRetailCustomer ? Labels.getLabel("label_CoreCustomerDialog_PrimaryID_Retl.value")
									: Labels.getLabel("label_CustomerDialog_TradeLicenseNumber.value"),
							Labels.getLabel("PersonalDetails"), value, Labels.getLabel("DocumentDetails") });
			break;
		}

		MessageUtil.showError(msg);

	}

	/** ------------------------------------------------- **/
	/** -------------FEE DETAILS VALIDATION-------------- **/
	/** ------------------------------------------------- **/

	public void doValidateFeeDetails(Tab custDetailTab) {

		if (isRetailCustomer) {
			ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
			if (!this.empStatus.isReadonly() && this.gp_CustEmployeeDetails.isVisible()) {
				this.empStatus.setConstraint(new PTStringValidator(
						Labels.getLabel("label_CustomerDialog_EmpStatus.value"), null, true, true));
			}

			try {
				this.empStatus.getValidatedValue();
			} catch (WrongValueException we) {
				wve.add(we);
			}

			if (wve.isEmpty()) {
				this.empStatus.setConstraint("");
				doResetFeeVariables();
			} else {
				this.tabkYCDetails.setSelected(true);
				showErrorDetails(wve, custDetailTab, tabkYCDetails);
			}
		}
	}

	private void doResetFeeVariables() {
		this.oldVar_empStatus = this.empStatus.getValue();
	}

	public boolean isFeeDataModified() {
		if (!this.oldVar_empStatus.equals(this.empStatus.getValue())) {
			return true;
		}
		return false;
	}

	/**
	 * Salutation codes will be populated based on the selected gender code.
	 * 
	 * @param event
	 */
	public void onSelect$custGenderCode(Event event) {
		logger.debug(Literal.ENTERING);

		String code = custGenderCode.getSelectedItem().getValue();
		custSalutationCode.setValue("");
		custMaritalSts.setValue("");

		if (PennantConstants.List_Select.equals(code)) {
			custSalutationCode.setValue("");
			custMaritalSts.setValue("");
			custSalutationCode.setDisabled(true);
			custMaritalSts.setDisabled(true);
			return;
		}

		custSalutationCode.setDisabled(false);
		custMaritalSts.setDisabled(false);
		fillComboBox(custSalutationCode, "", PennantAppUtil.getSalutationCodes(code), "");
		fillComboBox(custMaritalSts, "", PennantAppUtil.getMaritalStsTypes(code), "");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Salutation codes will be populated based on the selected gender code.
	 * 
	 * @param event
	 */
	public void onSelect$custSalutationCode(Event event) {
		logger.debug(Literal.ENTERING);

		String code = custGenderCode.getSelectedItem().getValue();
		custMaritalSts.setValue("");

		if (PennantConstants.List_Select.equals(code)) {
			custMaritalSts.setValue("");
			return;
		}

		fillComboBox(custMaritalSts, "", PennantAppUtil.getMaritalStsTypes(code), "");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Based Customer Type Code selection Fill the Segment value
	 */
	public void onFulfill$custTypeCode(Event event) {
		logger.debug("Entering");
		Object dataObject = custTypeCode.getObject();
		this.custSegment.setValue("");
		this.custSegment.setDescription("");
		this.custSubSegment.setValue("");
		this.custSubSegment.setDescription("");
		this.custSubSegment.setReadonly(true);
		this.custSubSegment.setButtonDisabled(true);
		if (dataObject instanceof String) {
			this.custTypeCode.setValue("");
			this.custTypeCode.setDescription("");
		} else {
			CustomerType details = (CustomerType) dataObject;
			if (details != null) {
				this.custTypeCode.setValue(details.getCustTypeCode());
				this.custTypeCode.setDescription(details.getCustTypeDesc());
				doSetSegmentCode(details.getCustTypeCode());
			}
		}
		logger.debug("Leaving");
	}

	public void onFulfill$custRO1(Event event) {
		logger.debug("Entering");

		Object dataObject = custRO1.getObject();
		if (dataObject instanceof String) {
			this.custRO1.setValue(dataObject.toString());
			this.custRO1.setDescription("");
		} else {
			VehicleDealer details = (VehicleDealer) dataObject;
			if (details != null) {
				this.custRO1.setAttribute("DealerId", details.getDealerId());
				// this.custRO1.setDescription(details.getDealerName());
			}
		}
	}

	/**
	 * Based Customer Type Code selection Fill the custParentCountry value
	 * 
	 */
	public void onFulfill$custParentCountry(Event event) {
		logger.debug("Entering");
		Object dataObject = custParentCountry.getObject();
		if (dataObject instanceof String) {
			this.custParentCountry.setValue("");
			this.custParentCountry.setDescription("");
		} else {
			Country details = (Country) dataObject;
			if (details != null) {
				this.custParentCountry.setValue(String.valueOf(details.getCountryCode()));
				this.custParentCountry.setDescription(details.getCountryDesc());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Based Customer Type Code selection Fill the custSubSegment value
	 * 
	 */
	public void onFulfill$custSubSegment(Event event) {
		logger.debug("Entering");
		Object dataObject = custSubSegment.getObject();
		if (dataObject instanceof String) {
			this.custSubSegment.setValue("");
			this.custSubSegment.setDescription("");
		} else {
			SubSegment details = (SubSegment) dataObject;
			if (details != null) {
				this.custSubSegment.setValue(details.getSubSegmentCode());
				this.custSubSegment.setDescription(details.getSubSegmentDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onFulfill$custSegment(Event event) {
		logger.debug("Entering");
		if (PennantConstants.SEP.equalsIgnoreCase(custSegment.getValue())) {
			profession.setMandatoryStyle(true);
		} else {
			profession.setMandatoryStyle(false);
		}
	}

	/*
	 * Object dataObject = custSubSegment.getObject(); if (dataObject instanceof String) {
	 * this.custSubSegment.setValue(""); this.custSubSegment.setDescription(""); } else { SubSegment details =
	 * (SubSegment) dataObject; if (details != null) { this.custSubSegment.setValue(details.getSubSegmentCode());
	 * this.custSubSegment.setDescription(details.getSubSegmentDesc()); } } logger.debug("Leaving"); }
	 */
	/**
	 * Based Customer Type Code selection Fill the custSubSector value
	 * 
	 */
	public void onFulfill$custSubSector(Event event) {
		logger.debug("Entering");
		Object dataObject = this.custSubSector.getObject();
		if (dataObject instanceof String) {
			this.custSubSector.setValue("");
			this.custSubSector.setDescription("");
		} else {
			SubSector details = (SubSector) dataObject;
			if (details != null) {
				this.custSubSector.setValue(String.valueOf(details.getSubSectorCode()));
				this.custSubSector.setDescription(details.getSubSectorDesc());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Based Customer Type Code selection Fill the custRiskCountry value
	 * 
	 */
	public void onFulfill$custRiskCountry(Event event) {
		logger.debug("Entering");
		Object dataObject = custRiskCountry.getObject();
		if (dataObject instanceof String) {
			this.custRiskCountry.setValue("");
			this.custRiskCountry.setDescription("");
		} else {
			Country details = (Country) dataObject;
			if (details != null) {
				this.custRiskCountry.setValue(String.valueOf(details.getCountryCode()));
				this.custRiskCountry.setDescription(details.getCountryDesc());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Based Customer Type Code selection Fill the custDSADept value
	 * 
	 */
	public void onFulfill$custDSADept(Event event) {
		logger.debug("Entering");
		Object dataObject = custDSADept.getObject();
		if (dataObject instanceof String) {
			this.custDSADept.setValue("");
			this.custDSADept.setDescription("");
		} else {
			Department details = (Department) dataObject;
			if (details != null) {
				this.custDSADept.setValue(String.valueOf(details.getDeptCode()));
				this.custDSADept.setDescription(details.getDeptDesc());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Based Customer Type Code selection Fill the custGroupId value
	 * 
	 */
	public void onFulfill$custGroupId(Event event) {
		logger.debug("Entering");
		Object dataObject = custGroupId.getObject();
		if (dataObject instanceof String) {
			this.custGroupId.setValue(dataObject.toString());
			this.custGroupId.setDescription("");
		} else {
			CustomerGroup details = (CustomerGroup) dataObject;
			if (details != null) {
				this.custGroupId.setAttribute("CustGroupId", details.getCustGrpID());
			}
		}
		logger.debug("Leaving");
	}

	public void onFulfillCustGroupId() {
		if (StringUtils.isEmpty(this.custGroupId.getAttribute("CustGroupId").toString())) {
			return;
		}

		CustomerGroup custGrp = (CustomerGroup) this.custGroupId.getObject();
		if (custGrp == null) {
			return;
		}

		Search search = new Search(CustomerGroup.class);
		search.addFilterEqual("CustGrpCode", custGrp.getCustGrpCode());

		SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
		custGrp = (CustomerGroup) searchProcessor.getResults(search).get(0);

		this.custGroupId.setValue(custGrp.getCustGrpCode());
		this.custGroupId.setDescription(custGrp.getCustGrpDesc());

	}

	/**
	 * Based Caste Code selection Fill the casteId value
	 * 
	 */
	public void onFulfill$caste(Event event) {
		logger.debug("Entering");
		Object dataObject = caste.getObject();
		if (dataObject instanceof String) {
			this.caste.setValue(dataObject.toString());
			this.caste.setDescription("");
		} else {
			Caste details = (Caste) dataObject;
			if (details != null) {
				this.caste.setAttribute("CastId", details.getCasteId());
			}
		}
		if (PennantConstants.OTHER.equalsIgnoreCase(this.caste.getValue())) {
			this.space_OtherCaste.setSclass(PennantConstants.mandateSclass);
			this.otherCaste.setReadonly(false);
		} else {
			this.space_OtherCaste.setSclass("");
			this.otherCaste.setConstraint("");
			this.otherCaste.setErrorMessage("");
			this.otherCaste.setValue("");
			this.otherCaste.setReadonly(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Based Caste Code selection Fill the casteId value
	 * 
	 */
	public void onFulfill$religion(Event event) {
		logger.debug("Entering");
		Object dataObject = religion.getObject();
		if (dataObject instanceof String) {
			this.religion.setValue(dataObject.toString());
			this.religion.setDescription("");
		} else {
			Religion details = (Religion) dataObject;
			if (details != null) {
				this.religion.setAttribute("ReligionId", details.getReligionId());
			}
		}
		if (PennantConstants.OTHER.equalsIgnoreCase(this.religion.getValue())) {
			this.space_OtherReligion.setSclass(PennantConstants.mandateSclass);
			this.otherReligion.setReadonly(false);
		} else {
			this.space_OtherReligion.setSclass("");
			this.otherReligion.setConstraint("");
			this.otherReligion.setErrorMessage("");
			this.otherReligion.setValue("");
			this.otherReligion.setReadonly(true);
		}
		logger.debug("Leaving");
	}

	public void onFulfill$custSector(Event event) {
		logger.debug("Entering");
		Object dataObject = custSector.getObject();
		this.custSubSector.setValue("");
		this.custSubSector.setDescription("");
		this.custSubSector.setObject("");
		this.custSubSector.setReadonly(true);
		this.custSubSector.setButtonDisabled(true);
		if (dataObject instanceof String) {
			this.custSector.setValue(dataObject.toString());
			this.custSector.setDescription("");
		} else {
			Sector details = (Sector) dataObject;
			if (details != null) {
				this.custSector.setValue(details.getSectorCode());
				this.custSector.setDescription(details.getSectorDesc());
				this.custSubSector.setReadonly(false);
				this.custSubSector.setButtonDisabled(false);
				this.custSubSector.setFilters(
						new Filter[] { new Filter("SectorCode", this.custSector.getValue(), Filter.OP_EQUAL) });
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Fetch Segment Code Based select on Customer Type Code. Customer Type Code matched to sub segment Code
	 * 
	 * @param subSegmentcode
	 * 
	 */
	private void doSetSegmentCode(String subSegmentcode) {
		logger.debug("Entering");
		SubSegment segmentDetails = PennantAppUtil.getSegmentDetails(subSegmentcode);
		this.custSegment.setValue("");
		this.custSegment.setDescription("");
		this.custSubSegment.setValue("");
		this.custSubSegment.setDescription("");
		if (segmentDetails != null) {
			this.custSegment.setValue(segmentDetails.getSegmentCode());
			this.custSegment.setDescription(segmentDetails.getLovDescSegmentCodeName());
			this.custSegment.setReadonly(true);
			this.custSubSegment.setReadonly(false);
			this.custSubSegment.setButtonDisabled(false);
			this.custSubSegment.setFilters(
					new Filter[] { new Filter("SegmentCode", this.custSegment.getValue(), Filter.OP_EQUAL) });
		}
		logger.debug("Leaving");
	}

	public void onFulfill$custCtgCode(Event event) {
		logger.debug("Entering");
		Object dataObject = custCtgCode.getObject();
		if (dataObject instanceof String) {
			this.custCtgCode.setValue(dataObject.toString());
			this.custCtgCode.setDescription("");
		} else {
			CustomerCategory details = (CustomerCategory) dataObject;
			if (details != null) {
				this.custCtgCode.setValue(details.getCustCtgCode());
				this.custCtgCode.setDescription(details.getCustCtgDesc());
				getCustomerDetails().getCustomer().setLovDescCustCtgType(details.getCustCtgType());
			}
		}
		logger.debug("Leaving");
	}

	public void onCheck$custIsStaff(Event event) {
		logger.debug("Entering");
		if (this.custIsStaff.isChecked()) {
			this.custStaffID.setValue(this.custStaffID.getValue());
			this.custStaffID.setReadonly(false);
		} else {
			this.custStaffID.setReadonly(true);
			this.custStaffID.setValue("");
		}
	}

	public void onChange$custFirstName(Event event) {
		logger.debug("Entering");
		this.custShrtName.setValue(PennantApplicationUtil.getFullName(this.custFirstName.getValue(),
				this.custMiddleName.getValue(), this.custLastName.getValue()));
		logger.debug("Leaving");
	}

	public void onChange$custMiddleName(Event event) {
		logger.debug("Entering");
		this.custShrtName.setValue(PennantApplicationUtil.getFullName(this.custFirstName.getValue(),
				this.custMiddleName.getValue(), this.custLastName.getValue()));
		logger.debug("Leaving");
	}

	public void onChange$custLastName(Event event) {
		logger.debug("Entering");
		this.custShrtName.setValue(PennantApplicationUtil.getFullName(this.custFirstName.getValue(),
				this.custMiddleName.getValue(), this.custLastName.getValue()));
		logger.debug("Leaving");
	}

	public String getCustIDNumber(String idType) {
		logger.debug("Entering");
		String idNumber = "";
		try {
			if (StringUtils.equalsIgnoreCase(MasterDefUtil.getDocCode(DocType.AADHAAR), idType)) {
				if (isRetailCustomer) {
					if (!this.eidNumber.isReadonly()) {
						this.eidNumber.setConstraint(
								new PTStringValidator(Labels.getLabel("label_CustomerDialog_EIDNumber.value"),
										PennantRegularExpressions.REGEX_AADHAR_NUMBER, true));
					}
					try {
						idNumber = this.eidNumber.getValue();
					} catch (WrongValueException we) {
						logger.error("Exception", we);
						idNumber = "";
					}
					this.eidNumber.setConstraint("");
				}
			} else if (StringUtils.equalsIgnoreCase(MasterDefUtil.getDocCode(DocType.PASSPORT), idType)) {
				if (getCustomerDetails() != null && getCustomerDetails().getCustomer() != null) {
					idNumber = getCustomerDetails().getCustomer().getCustPassportNo();
				}
			} else if (PennantConstants.TRADELICENSE.equalsIgnoreCase(idType)) {
				if (!isRetailCustomer) {
					if (!this.eidNumber.isReadonly()) {
						this.eidNumber.setConstraint(
								new PTStringValidator(Labels.getLabel("label_CustomerDialog_TradeLicenseNumber.value"),
										PennantRegularExpressions.REGEX_TRADELICENSE, true));
					}
					try {
						idNumber = this.eidNumber.getValue();
					} catch (WrongValueException we) {
						logger.error("Exception", we);
						idNumber = "";
					}
					this.eidNumber.setConstraint("");
				}
				// ### 01-05-2018 TuleApp ID : #360
			} else if (SysParamUtil.getValueAsString("CUST_PRIMARY_ID_RETL_DOC_TYPE").equalsIgnoreCase(idType)) {
				try {
					idNumber = this.eidNumber.getValue();
				} catch (WrongValueException we) {
					logger.error("Exception", we);
					idNumber = "";
				}
				this.eidNumber.setConstraint("");

			}
			// ### 01-05-2018 TuleApp ID : #360
			else if (SysParamUtil.getValueAsString("CUST_PRIMARY_ID_CORP_DOC_TYPE").equalsIgnoreCase(idType)) {
				try {
					idNumber = this.eidNumber.getValue();
				} catch (WrongValueException we) {
					logger.error("Exception", we);
					idNumber = "";
				}
				this.eidNumber.setConstraint("");

			}
			if (StringUtils.isEmpty(idNumber)) {
				idNumber = getCustDocID(idType);
			}
		} catch (Exception e) {
			logger.error("Exception", e);
			idNumber = "";
		}
		logger.debug("Leaving");
		return idNumber;
	}

	public Date getcustIncorpDate() {
		return this.custDOB.getValue();
	}

	public void setMandatoryIDNumber(String eidNumber) {
		logger.debug("Entering");
		if (StringUtils.isBlank(eidNumber)) {
			this.eidNumber.setValue(null);
		} else {
			this.eidNumber.setValue(eidNumber);
		}
		logger.debug("Leaving");
	}

	public void setCustDob(Date dateofInc) {
		logger.debug("Entering");
		this.custDOB.setValue(dateofInc);
		logger.debug("Leaving");
	}

	public void onFulfill$empName(Event event) {
		logger.debug("Entering");

		Object dataObject = empName.getObject();
		if (dataObject instanceof String) {
			this.empName.setValue(dataObject.toString());
			this.empName.setDescription("");
			this.empAlocType = "";
		} else {
			EmployerDetail details = (EmployerDetail) dataObject;
			if (details != null) {
				this.empName.setValue(String.valueOf(details.getEmployerId()));
				this.empName.setDescription(details.getEmpName());

				this.empSector.setValue(details.getEmpIndustry());
				this.empSector.setDescription(details.getLovDescIndustryDesc());
				this.empAlocType = details.getEmpAlocationType();
			}
		}

		if (StringUtils.equals(this.empName.getDescription(), PennantConstants.EmploymentName_OTHERS)) {
			this.hbox_empNameOther.setVisible(true);
			this.label_empNameOther.setVisible(true);
		} else {
			this.hbox_empNameOther.setVisible(false);
			this.label_empNameOther.setVisible(false);
		}
		if (!StringUtils.equals(this.empName.getValue(), empName_Temp)) {
			this.empNameOther.setValue("");
		}
		empName_Temp = this.empName.getValue();
		logger.debug("Leaving");
	}

	public void onFulfill$empSector(Event event) {
		logger.debug("Entering");
		Object dataObject = this.empSector.getObject();
		if (dataObject instanceof String) {
			this.empName.setFilters(null);
			this.empName.setValue("", "");
			this.empName.setObject("");
		} else {
			EmploymentType empType = (EmploymentType) dataObject;
			if (empType != null) {
				this.empName.setValue("", "");
				this.empName.setObject("");
				Filter[] filters = new Filter[1];
				filters[0] = new Filter("EmpIndustry", this.empSector.getValue(), Filter.OP_EQUAL);
				this.empName.setFilters(null);
				this.empName.setFilters(filters);
			}
		}

		logger.debug("Leaving");
	}

	public void onFulfill$empStatus(Event event) {
		logger.debug("Entering");

		Object dataObject = empStatus.getObject();
		if (dataObject instanceof String) {
			this.empStatus.setValue(dataObject.toString());
			this.empStatus.setDescription("");
		} else {
			EmpStsCode details = (EmpStsCode) dataObject;
			if (details != null) {
				this.empStatus.setValue(details.getEmpStsCode());
				this.empStatus.setDescription(details.getEmpStsDesc());
			}
		}
		if (!StringUtils.equals(this.empStatus.getValue(), empStatus_Temp)) {
			doClearEmpDetails();
			this.empFrom.setDisabled(isReadOnly("CustomerDialog_empFrom"));
		}
		empStatus_Temp = this.empStatus.getValue();

		doSetEmpStatusProperties(this.empStatus.getValue());
		logger.debug("Leaving");
	}

	public void onFulfill$custBaseCcy(Event event) {
		logger.debug("Entering");
		Object dataObject = custBaseCcy.getObject();
		if (dataObject instanceof String) {
			this.custBaseCcy.setValue(dataObject.toString());
			this.custBaseCcy.setDescription("");
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				this.custBaseCcy.setValue(details.getCcyCode());
				this.custBaseCcy.setDescription(details.getCcyDesc());
				this.ccyFormatter = details.getCcyEditField();
			}
		}
		if (StringUtils.isNotEmpty(this.custBaseCcy.getValue())
				&& !StringUtils.equals(this.custBaseCcy.getValue(), custBaseCcy_Temp)) {
			MessageUtil
					.showMessage(Labels.getLabel("label_CurrencyChange", new String[] { getCurrencyAlertMessage() }));
			doSetCurrencyFieldProperties();
		}
		custBaseCcy_Temp = this.custBaseCcy.getValue();
		logger.debug("Leaving");
	}

	private String getCurrencyAlertMessage() {
		return " \n 1) " + Labels.getLabel("gp_CustEmployeeDetails") + " \n 2) "
				+ Labels.getLabel("gp_CustomerChequeInfoDetails") + " \n 3) "
				+ Labels.getLabel("gp_ExternalLiabilityDetails");
	}

	public void onFulfill$otherIncome(Event event) {
		logger.debug("Entering");
		this.additionalIncome.setErrorMessage("");
		if (StringUtils.isBlank(this.otherIncome.getValue())) {
			this.additionalIncome.setMandatory(false);
		} else {
			this.additionalIncome.setMandatory(true);
		}
		logger.debug("Leaving");
	}

	private void doSetCurrencyFieldProperties() {
		logger.debug("Entering");
		this.monthlyIncome.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.additionalIncome.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		if (getCustomerChequeInfoDetailList() != null && !getCustomerChequeInfoDetailList().isEmpty()) {
			for (CustomerChequeInfo customerChequeInfo : getCustomerChequeInfoDetailList()) {
				if (StringUtils.isBlank(customerChequeInfo.getRecordType())) {
					customerChequeInfo.setVersion(customerChequeInfo.getVersion() + 1);
					customerChequeInfo.setRecordType(PennantConstants.RCD_UPD);
				}
				customerChequeInfo.setSalary(PennantApplicationUtil.unFormateAmount(
						PennantApplicationUtil.formateAmount(customerChequeInfo.getSalary(), old_ccyFormatter),
						ccyFormatter));
				customerChequeInfo.setReturnChequeAmt(PennantApplicationUtil.unFormateAmount(
						PennantApplicationUtil.formateAmount(customerChequeInfo.getReturnChequeAmt(), old_ccyFormatter),
						ccyFormatter));
				customerChequeInfo.setTotChequePayment(PennantApplicationUtil.unFormateAmount(PennantApplicationUtil
						.formateAmount(customerChequeInfo.getTotChequePayment(), old_ccyFormatter), ccyFormatter));
			}
			doFillCustomerChequeInfoDetails(getCustomerChequeInfoDetailList());
		}
		if (getCustomerExtLiabilityDetailList() != null && !getCustomerExtLiabilityDetailList().isEmpty()) {
			for (CustomerExtLiability liability : getCustomerExtLiabilityDetailList()) {
				if (StringUtils.isBlank(liability.getRecordType())) {
					liability.setVersion(liability.getVersion() + 1);
					liability.setRecordType(PennantConstants.RCD_UPD);
				}
				liability.setOriginalAmount(PennantApplicationUtil.unFormateAmount(
						PennantApplicationUtil.formateAmount(liability.getOriginalAmount(), old_ccyFormatter),
						ccyFormatter));
				liability.setInstalmentAmount(PennantApplicationUtil.unFormateAmount(
						PennantApplicationUtil.formateAmount(liability.getInstalmentAmount(), old_ccyFormatter),
						ccyFormatter));
				liability.setOutstandingBalance(PennantApplicationUtil.unFormateAmount(
						PennantApplicationUtil.formateAmount(liability.getOutstandingBalance(), old_ccyFormatter),
						ccyFormatter));
			}
			doFillCustomerExtLiabilityDetails(getCustomerExtLiabilityDetailList());
		}
		old_ccyFormatter = ccyFormatter;
		logger.debug("Leaving");
	}

	private void doSetEmpStatusProperties(String status) {
		logger.debug("Entering");
		this.empStatus.setTextBoxWidth(121);
		this.empSector.setTextBoxWidth(120);
		this.otherIncome.setTextBoxWidth(121);
		this.empDept.setTextBoxWidth(121);
		this.empDesg.setTextBoxWidth(121);
		if (StringUtils.isNotEmpty(this.empStatus.getValue())) {
			this.monthlyIncome.setMandatory(true);
		} else {
			this.monthlyIncome.setMandatory(false);
		}
		if (StringUtils.equals(this.empStatus.getValue(), PennantConstants.CUSTEMPSTS_SELFEMP)) {
			// make profession visible true
			this.label_CustomerDialog_EmpFrom
					.setValue(Labels.getLabel("label_CustomerDialog_ProfessionStartDate.value"));
			this.label_CustomerDialog_MonthlyIncome
					.setValue(Labels.getLabel("label_CustomerDialog_MonthlyProfessionIncome.value"));
			this.label_CustomerDialog_EmpSector.setValue(Labels.getLabel("label_CustomerDialog_Profession.value"));
			this.label_CustomerDialog_EmpSector.setVisible(false);
			this.empSector.setVisible(false);
			this.label_CustomerDialog_Profession.setVisible(true);
			this.profession.setVisible(true);
			this.row_EmpName.setVisible(false);
			this.row_DesgDept.setVisible(false);
		} else if (StringUtils.equals(this.empStatus.getValue(), PennantConstants.CUSTEMPSTS_SME)) {
			this.label_CustomerDialog_EmpFrom.setValue(Labels.getLabel("label_CustomerDialog_BusinessStartDate.value"));
			this.label_CustomerDialog_MonthlyIncome
					.setValue(Labels.getLabel("label_CustomerDialog_AvgMonthlyTurnover.value"));
			this.label_CustomerDialog_EmpSector.setValue(Labels.getLabel("label_CustomerDialog_SMESector.value"));
			this.label_CustomerDialog_Profession.setVisible(false);
			this.label_CustomerDialog_EmpSector.setVisible(true);
			this.empSector.setVisible(true);
			this.profession.setVisible(false);
			this.row_EmpName.setVisible(false);
			this.row_DesgDept.setVisible(false);
		} else {
			this.label_CustomerDialog_EmpFrom.setValue(Labels.getLabel("label_CustomerDialog_EmpFrom.value"));
			this.label_CustomerDialog_MonthlyIncome
					.setValue(Labels.getLabel("label_CustomerDialog_MonthlyIncome.value"));
			this.label_CustomerDialog_EmpSector.setValue(Labels.getLabel("label_CustomerDialog_EmpSector.value"));
			this.label_CustomerDialog_Profession.setVisible(false);
			this.label_CustomerDialog_EmpSector.setVisible(true);
			this.empSector.setVisible(true);
			this.profession.setVisible(false);
			this.row_EmpName.setVisible(true);
			this.row_DesgDept.setVisible(true);
		}
		if (StringUtils.equals(this.empName.getDescription(), PennantConstants.EmploymentName_OTHERS)) {
			this.hbox_empNameOther.setVisible(true);
			this.label_empNameOther.setVisible(true);
		} else {
			this.hbox_empNameOther.setVisible(false);
			this.label_empNameOther.setVisible(false);
		}
		if (StringUtils.isEmpty(this.otherIncome.getValue())) {
			this.additionalIncome.setMandatory(false);
		} else {
			this.additionalIncome.setMandatory(true);
		}
		logger.debug("Leaving");
	}

	private String getEmpFromLabel() {
		if (StringUtils.equals(this.empStatus.getValue(), PennantConstants.CUSTEMPSTS_SELFEMP)) {
			return Labels.getLabel("label_CustomerDialog_ProfessionStartDate.value");
		} else if (StringUtils.equals(this.empStatus.getValue(), PennantConstants.CUSTEMPSTS_SME)) {
			return Labels.getLabel("label_CustomerDialog_BusinessStartDate.value");
		} else {
			return Labels.getLabel("label_CustomerDialog_EmpFrom.value");
		}
	}

	private void doClearEmpDetails() {
		logger.debug("Entering");

		doClearErrorMessage();
		doRemoveValidation();
		doRemoveLOVValidation();

		this.empSector.setValue("", "");
		this.empSector.setObject("");
		this.profession.setValue("", "");
		this.profession.setObject("");
		this.empName.setValue("", "");
		this.empName.setObject("");
		this.empNameOther.setValue("");
		this.empFrom.setText("");
		this.exp.setVisible(false);
		this.empDesg.setValue("", "");
		this.empDesg.setObject("");
		this.empDept.setValue("", "");
		this.empDept.setObject("");
		this.monthlyIncome.setValue(BigDecimal.ZERO);
		this.otherIncome.setValue("", "");
		this.otherIncome.setObject("");
		this.additionalIncome.setValue(BigDecimal.ZERO);
		logger.debug("Leaving");
	}

	// ********************************************************************//
	// ***** New Button & Double Click Events for Customer Rating List ****//
	// ********************************************************************//
	public void onClick$btnNew_CustomerRatings(Event event) {
		logger.debug("Entering");
		CustomerRating customerRating = new CustomerRating();
		customerRating.setNewRecord(true);
		customerRating.setWorkflowId(0);
		customerRating.setCustID(getCustomerDetails().getCustID());
		customerRating.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		customerRating.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerRating", customerRating);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerRating/CustomerRatingDialog.zul",
					window_CustomerDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void onCustomerRatingItemDoubleClicked(Event event) {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerRating.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerRating customerRating = (CustomerRating) item.getAttribute("data");
			if (isDeleteRecord(customerRating.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("customerRating", customerRating);
				map.put("customerDialogCtrl", this);
				map.put("roleCode", getRole());
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/CustomerRating/CustomerRatingDialog.zul",
							window_CustomerDialog, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	// ********************************************************************//
	// *** New Button & Double Click Events for Customer Employmnet List **//
	// ********************************************************************//
	public void onClick$btnNew_CustomerEmploymentDetail(Event event) {
		logger.debug("Entering");
		CustomerEmploymentDetail customerEmploymentDetail = new CustomerEmploymentDetail();
		customerEmploymentDetail.setNewRecord(true);
		customerEmploymentDetail.setWorkflowId(0);
		customerEmploymentDetail.setCustID(getCustomerDetails().getCustID());
		customerEmploymentDetail.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		customerEmploymentDetail.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerEmploymentDetail", customerEmploymentDetail);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("isFinanceProcess", isFinanceProcess);
		map.put("roleCode", getRole());
		map.put("currentEmployer", getCurrentEmployerExist(customerEmploymentDetail));
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerEmploymentDetail/CustomerEmploymentDetailDialog.zul",
					window_CustomerDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	// ********************************************************************//
	// *** New Button for Customer GST Details **//
	// ********************************************************************//

	public void onClick$btnNew_CustomerGSTDetails(Event event) {
		logger.debug("Entering");
		CustomerGST customerGST = new CustomerGST();
		customerGST.setNewRecord(true);
		customerGST.setWorkflowId(0);
		customerGST.setCustId(getCustomerDetails().getCustID());
		customerGST.setCustCif(getCustomerDetails().getCustomer().getCustCIF());
		customerGST.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		customerGST.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerGst", customerGST);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("finFormatter", ccyFormatter);
		map.put("isFinanceProcess", isFinanceProcess);
		map.put("CustomerGstList", customerGstList);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerGstDetailsDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void doFillCustomerGstDetails(List<CustomerGST> customerGstDetails) {
		logger.debug("Entering");
		renderGstInfoDetails(customerGstDetails);
		logger.debug("Leaving");
	}

	private void renderGstInfoDetails(List<CustomerGST> customerGstDetails) {
		logger.debug("Entering");
		this.listBoxCustomerGst.getItems().clear();
		Listitem item;
		Listcell cell;
		Listgroup gstInfogroup;
		if (customerGstDetails != null) {
			for (CustomerGST customerGST : customerGstDetails) {
				gstInfogroup = new Listgroup();
				cell = new Listcell(customerGST.getGstNumber());
				cell.setParent(gstInfogroup);
				cell = new Listcell(customerGST.getFrequencytype());
				cell.setParent(gstInfogroup);
				cell = new Listcell(String.valueOf(customerGST.getRecordType()));
				cell.setParent(gstInfogroup);
				this.listBoxCustomerGst.appendChild(gstInfogroup);
				item = new Listitem();
				cell = new Listcell("Frequency");
				cell.setStyle("font-weight:bold;cursor:default");
				cell.setParent(item);
				cell = new Listcell("Financial Year");
				cell.setStyle("font-weight:bold;cursor:default");
				cell.setParent(item);
				cell = new Listcell("Amount");
				cell.setStyle("font-weight:bold;cursor:default");
				cell.setParent(item);
				this.listBoxCustomerGst.appendChild(item);
				for (CustomerGSTDetails customerGSTDetailstemp : customerGST.getCustomerGSTDetailslist()) {
					item = new Listitem();
					cell = new Listcell(customerGSTDetailstemp.getFrequancy());
					cell.setParent(item);
					cell = new Listcell(customerGSTDetailstemp.getFinancialYear());
					cell.setParent(item);
					cell = new Listcell(PennantApplicationUtil.amountFormate((customerGSTDetailstemp.getSalAmount()),
							PennantConstants.defaultCCYDecPos));
					cell.setParent(item);
					this.listBoxCustomerGst.appendChild(item);
					item.setAttribute("data", customerGST);
					ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerGstDetailsItemDoubleClicked");
				}
			}
			setCustomerGstList(customerGstDetails);

		}

	}

	public void onCustomerGstDetailsItemDoubleClicked(Event event) {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerGst.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerGST customerGST = (CustomerGST) item.getAttribute("data");

			customerGST.setCustId(getCustomerDetails().getCustID());
			customerGST.setCustCif(getCustomerDetails().getCustomer().getCustCIF());
			customerGST.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
			customerGST.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());

			if (isDeleteRecord(customerGST.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("customerDialogCtrl", this);
				map.put("customerGst", customerGST);
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
				map.put("CustomerGstList", customerGstList);
				map.put("fromDouble", true);
				map.put("retailCustomer",
						StringUtils.equals(this.custCtgCode.getValue(), PennantConstants.PFF_CUSTCTG_INDIV));
				try {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerGstDetailsDialog.zul",
							null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
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

	public void onCustomerEmploymentDetailItemDoubleClicked(Event event) {
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
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("customerEmploymentDetail", customerEmploymentDetail);
				map.put("customerDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("moduleType", this.moduleType);
				map.put("currentEmployer", getCurrentEmployerExist(customerEmploymentDetail));
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/CustomerEmploymentDetail/CustomerEmploymentDetailDialog.zul",
							window_CustomerDialog, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	// ********************************************************************//
	// ***** New Button & Double Click Events for CustomerAddress List ****//
	// ********************************************************************//
	public void onClick$btnNew_DirectorDetail(Event event) {
		logger.debug("Entering");
		final DirectorDetail directorDetail = getDirectorDetailService().getNewDirectorDetail();
		directorDetail.setWorkflowId(0);
		directorDetail.setCustID(getCustomerDetails().getCustID());
		// directorDetail.setDirectorId(this.listBoxCustomerDirectory.getItemCount()
		// == 0 ? 1 : this.listBoxCustomerDirectory.getItemCount() + 1);
		directorDetail.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		directorDetail.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("directorDetail", directorDetail);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		map.put("totSharePerc", getTotSharePerc());
		map.put("isFinanceProcess", isFinanceProcess);
		map.put("fromLoan", fromLoan);
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/DirectorDetail/DirectorDetailDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void onDirectorDetailItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerDirectory.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final DirectorDetail directorDetail = (DirectorDetail) item.getAttribute("data");
			if (isDeleteRecord(directorDetail.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("directorDetail", directorDetail);
				map.put("customerDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("fromLoan", fromLoan);
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
		logger.debug(Literal.LEAVING);
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

	// ********************************************************************//
	// ***** New Button & Double Click Events for Customer Income List ****//
	// ********************************************************************//
	public void onClick$btnNew_CustomerIncome(Event event) {
		logger.debug("Entering");

		reallocateRights("CustomerDialog_custIncomeType");
		CustomerIncome customerIncome = new CustomerIncome();
		customerIncome.setNewRecord(true);
		customerIncome.setWorkflowId(0);
		customerIncome.setRecordType(PennantConstants.RCD_ADD);
		incomeAndExpenseCtrl.doFillIncomeAndExpense(customerIncome, this.listBoxCustomerIncomeInLineEdit, ccyFormatter,
				true, PennantConstants.MODULETYPE_ENQ.equals(this.moduleType));
		logger.debug("Leaving");
	}

	public void onCustomerIncomeItemDoubleClicked(Event event) {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerIncome.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerIncome customerIncome = (CustomerIncome) item.getAttribute("data");
			if (isDeleteRecord(customerIncome.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("customerIncome", customerIncome);
				map.put("customerDialogCtrl", this);
				map.put("ccyFormatter", ccyFormatter);
				map.put("isFinanceProcess", isFinanceProcess);
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

	// ******************************************************//
	// ********* Customer Related Lists Refreshing **********//
	// ******************************************************//
	/**
	 * Generate the Customer Rating Details List in the CustomerDialogCtrl and set the list in the listBoxCustomerRating
	 * listbox by using Pagination
	 */
	public void doFillCustomerRatings(List<CustomerRating> customerRatings) {
		logger.debug("Entering");
		if (customerRatings != null && !customerRatings.isEmpty()) {
			setRatingsList(customerRatings);
			ListModelList<CustomerRating> listModelList = new ListModelList<CustomerRating>(customerRatings);
			this.listBoxCustomerRating.setModel(listModelList);
			this.listBoxCustomerRating.setItemRenderer(new CustomerRatinglistItemRenderer());
		}
		logger.debug("Leaving");
	}

	public void doFillCustomerEmploymentDetail(List<CustomerEmploymentDetail> custEmploymentDetails) {
		logger.debug("Entering");
		this.listBoxCustomerEmploymentDetail.getItems().clear();
		if (custEmploymentDetails != null && !custEmploymentDetails.isEmpty()) {
			setCustomerEmploymentDetailList(custEmploymentDetails);
			for (CustomerEmploymentDetail customerEmploymentDetail : custEmploymentDetails) {
				customerEmploymentDetail.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
			}

			ListModelList<CustomerEmploymentDetail> listModelList = new ListModelList<CustomerEmploymentDetail>(
					custEmploymentDetails);
			this.listBoxCustomerEmploymentDetail.setModel(listModelList);
			this.listBoxCustomerEmploymentDetail.setItemRenderer(new CustomerEmploymentDetailListModelItemRenderer());
		}
	}

	// ****************** Child Details *******************//

	// ********************************************************************//
	// ***** New Button & Double Click Events for Customer Income List ****//
	// ********************************************************************//
	public void onClick$btnNew_CustomerDocuments(Event event) {
		logger.debug("Entering");
		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setNewRecord(true);
		customerDocument.setWorkflowId(0);
		customerDocument.setCustID(getCustomerDetails().getCustID());
		customerDocument.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		customerDocument.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerDocument", customerDocument);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("isFinanceProcess", isFinanceProcess);
		map.put("roleCode", getRole());
		map.put("isRetailCustomer", isRetailCustomer);
		map.put("finReference", finReference);
		map.put("dmsApplicationNo", dmsApplicationNo);
		map.put("leadId", leadId);

		if (isNewCustCret) {
			map.put("isNewCustCret", isNewCustCret);
		}
		if (getFinanceMainDialogCtrl() != null) {
			map.put("financeMainDialogCtrl", getFinanceMainDialogCtrl());
		}
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul",
					null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void onCustomerDocumentItemDoubleClicked(Event event) {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerDocuments.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerDocument cd = (CustomerDocument) item.getAttribute("data");
			if (isDeleteRecord(cd.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				String docName = cd.getCustDocName();
				String docUri = cd.getDocUri();
				Long docRefId = cd.getDocRefId();
				if (StringUtils.isNotBlank(docUri)) {
					DocumentDetails dd = dMSService.getExternalDocument(this.custCIF.getValue(), docName, docUri);
					cd.setCustDocImage(dd.getDocImage());
					cd.setCustDocName(dd.getDocName());
				} else {
					if (cd.getCustDocImage() == null) {
						if (docRefId != null && docRefId != Long.MIN_VALUE) {
							cd.setCustDocImage(dMSService.getById(docRefId));
						}
					}
				}

				cd.setLovDescCustCIF(this.custCIF.getValue());
				cd.setLovDescCustShrtName(this.custShrtName.getValue());
				map.put("customerDocument", cd);
				map.put("customerDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("moduleType", this.moduleType);
				map.put("enqiryModule", this.isEnqProcess);
				map.put("isRetailCustomer", isRetailCustomer);
				map.put("finReference", finReference);
				map.put("dmsApplicationNo", dmsApplicationNo);
				map.put("leadId", leadId);
				if (getFinanceMainDialogCtrl() != null) {
					map.put("financeMainDialogCtrl", getFinanceMainDialogCtrl());
				}
				if (isNewCustCret) {
					map.put("isNewCustCret", isNewCustCret);
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

	public void doFillDocumentDetails(List<CustomerDocument> custDocumentDetails) {
		logger.debug("Entering");
		this.listBoxCustomerDocuments.getItems().clear();
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
					lc.setParent(item);
					lc = new Listcell(customerDocument.getCustDocTitle());
					lc.setParent(item);
					lc = new Listcell(customerDocument.getLovDescCustDocIssuedCountry());
					lc.setParent(item);
					lc = new Listcell(customerDocument.getCustDocSysName());
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(customerDocument.getCustDocIssuedOn()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(customerDocument.getCustDocExpDate()));
					lc.setParent(item);
					lc = new Listcell(customerDocument.getRecordStatus());
					lc.setParent(item);
					lc = new Listcell(PennantJavaUtil.getLabel(customerDocument.getRecordType()));
					lc.setParent(item);

					item.setAttribute("data", customerDocument);
					ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerDocumentItemDoubleClicked");
					this.listBoxCustomerDocuments.appendChild(item);
				}

				if (rcuVerificationDialogCtrl != null) {
					rcuVerificationDialogCtrl.addCustomerDocuments(custDocumentDetails);
				}

				if (lVerificationCtrl != null) {
					lVerificationCtrl.addCustomerDocuments(custDocumentDetails);
				}
			}

			setCustomerDocumentDetailList(custDocumentDetails);
		}
		logger.debug("Leaving");
	}

	// ********************************************************************//
	// ***** New Button & Double Click Events for CustomerAddress List ****//
	// ********************************************************************//
	public void onClick$btnNew_CustomerAddress(Event event) {
		logger.debug("Entering");
		CustomerAddres customerAddres = new CustomerAddres();
		List<String> custCIFs = new ArrayList<String>();
		customerAddres.setNewRecord(true);
		customerAddres.setWorkflowId(0);
		customerAddres.setCustID(getCustomerDetails().getCustID());
		customerAddres.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		customerAddres.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		String priority = SysParamUtil.getValueAsString(SMTParameterConstants.DEFAULT_KYC_PRIORITY);
		if (StringUtils.isNotBlank(priority)) {
			customerAddres.setCustAddrPriority(Integer.parseInt(priority));
		}

		Country defaultCountry = PennantApplicationUtil.getDefaultCounty();
		customerAddres.setCustAddrCountry(defaultCountry.getCountryCode());
		customerAddres.setLovDescCustAddrCountryName(defaultCountry.getCountryDesc());

		if (financeMainDialogCtrl != null && financeMainDialogCtrl instanceof FinanceMainBaseCtrl) {
			com.pennant.webui.finance.financemain.JointAccountDetailDialogCtrl financeJointAccountDetailDialogCtrl = ((FinanceMainBaseCtrl) financeMainDialogCtrl)
					.getJointAccountDetailDialogCtrl();
			if (financeJointAccountDetailDialogCtrl != null) {
				List<Customer> jointAccountCustomers = financeJointAccountDetailDialogCtrl.getJointAccountCustomers();
				custCIFs = doPrepareJointAccountCustIdList(jointAccountCustomers);
			}
			custCIFs.add(this.custCIF.getValue());
		} else if (fromLoan && jointAccountDetailDialogCtrl != null) {
			Collections.addAll(custCIFs, cif);
		}

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerAddres", customerAddres);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("isFinanceProcess", isFinanceProcess);
		map.put("roleCode", getRole());
		map.put("custCIFs", custCIFs);
		map.put("fromLoan", fromLoan);
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerAddres/CustomerAddresDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private List<String> doPrepareJointAccountCustIdList(List<Customer> jointAccountCustomers) {
		List<String> custId = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(jointAccountCustomers)) {
			for (Customer customer : jointAccountCustomers) {
				custId.add(customer.getCustCIF());
			}
		}
		return custId;
	}

	public void onCustomerAddressItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerAddress.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerAddres customerAddress = (CustomerAddres) item.getAttribute("data");
			if (isDeleteRecord(customerAddress.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				customerAddress.setLovDescCustCIF(this.custCIF.getValue());
				customerAddress.setLovDescCustShrtName(this.custShrtName.getValue());
				map.put("customerAddres", customerAddress);
				map.put("customerDialogCtrl", this);
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
		logger.debug(Literal.LEAVING);
	}

	public void doFillCustomerAddressDetails(List<CustomerAddres> customerAddresDetails) {
		logger.debug("Entering");
		this.listBoxCustomerAddress.getItems().clear();
		if (customerAddresDetails != null) {
			for (CustomerAddres customerAddress : customerAddresDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(customerAddress.getLovDescCustAddrTypeName());
				lc.setParent(item);
				if (PennantConstants.CITY_FREETEXT) {
					lc = new Listcell(customerAddress.getCustAddrCity());
					lc.setParent(item);
				} else {
					lc = new Listcell(customerAddress.getLovDescCustAddrCityName());
					lc.setParent(item);
				}
				lc = new Listcell(customerAddress.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(customerAddress.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", customerAddress);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerAddressItemDoubleClicked");
				this.listBoxCustomerAddress.appendChild(item);

			}
			setCustomerAddressDetailList(customerAddresDetails);
			if (fieldVerificationDialogCtrl != null) {
				fieldVerificationDialogCtrl.addCustomerAddresses(customerAddresDetails, true);
			}
		}

		logger.debug("Leaving");
	}

	// ********************************************************************//
	// ** New Button & Double Click Events for CustomerPhoneNumbers List **//
	// ********************************************************************//
	public void onClick$btnNew_CustomerPhoneNumber(Event event) {
		logger.debug(Literal.ENTERING);

		CustomerPhoneNumber customerPhoneNumber = new CustomerPhoneNumber();
		customerPhoneNumber.setNewRecord(true);
		customerPhoneNumber.setWorkflowId(0);
		customerPhoneNumber.setPhoneCustID(getCustomerDetails().getCustID());
		customerPhoneNumber.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		customerPhoneNumber.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		String priority = SysParamUtil.getValueAsString(SMTParameterConstants.DEFAULT_KYC_PRIORITY);
		if (StringUtils.isNotBlank(priority)) {
			customerPhoneNumber.setPhoneTypePriority(Integer.parseInt(priority));
		}
		reallocateRights("CustomerDialog_custPhoneNumber");
		customerPhoneNumber.setRecordType(PennantConstants.RCD_ADD);
		customerPhoneNumberInLineEditCtrl.doFillPhoneNumbers(customerPhoneNumber,
				this.listBoxCustomerPhoneNumbersInlineEdit, isFinanceProcess,
				PennantConstants.MODULETYPE_ENQ.equals(this.moduleType));

		logger.debug(Literal.LEAVING);
	}

	public void onCustomerPhoneNumberItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerPhoneNumbers.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerPhoneNumber customerPhoneNumber = (CustomerPhoneNumber) item.getAttribute("data");
			if (isDeleteRecord(customerPhoneNumber.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				customerPhoneNumber.setLovDescCustCIF(this.custCIF.getValue());
				customerPhoneNumber.setLovDescCustShrtName(this.custShrtName.getValue());
				map.put("customerPhoneNumber", customerPhoneNumber);
				map.put("customerDialogCtrl", this);
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
		logger.debug(Literal.LEAVING);
	}

	public void doFillCustomerPhoneNumberDetails(List<CustomerPhoneNumber> customerPhoneNumDetails) {
		logger.debug("Entering");
		this.listBoxCustomerPhoneNumbers.getItems().clear();
		customerPhoneNumberInLineEditCtrl.doRenderPhoneNumberList(customerPhoneNumDetails,
				listBoxCustomerPhoneNumbersInlineEdit, this.custCIF.getValue(), isFinanceProcess,
				PennantConstants.MODULETYPE_ENQ.equals(this.moduleType));
		logger.debug("Leaving");
	}

	// ********************************************************************//
	// ++ New Button & Double Click Events for CustomerEmailAddress List ++//
	// ********************************************************************//
	public void onClick$btnNew_CustomerEmail(Event event) {
		logger.debug("Entering");
		CustomerEMail customerEMail = new CustomerEMail();
		customerEMail.setNewRecord(true);
		customerEMail.setWorkflowId(0);
		customerEMail.setCustID(getCustomerDetails().getCustID());
		customerEMail.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		customerEMail.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		String priority = SysParamUtil.getValueAsString(SMTParameterConstants.DEFAULT_KYC_PRIORITY);
		if (StringUtils.isNotBlank(priority)) {
			customerEMail.setCustEMailPriority(Integer.parseInt(priority));
		}

		reallocateRights("CustomerDialog_custEmail");
		customerEMail.setRecordType(PennantConstants.RCD_ADD);
		customerEmailInlineEditCtrl.doFillEmails(customerEMail, this.listBoxCustomerEmailsInlineEdit, isFinanceProcess,
				PennantConstants.MODULETYPE_ENQ.equals(this.moduleType));

		logger.debug("Leaving");
	}

	public void onCustomerEmailAddressItemDoubleClicked(Event event) {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerEmails.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerEMail customerEmail = (CustomerEMail) item.getAttribute("data");
			if (isDeleteRecord(customerEmail.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				customerEmail.setLovDescCustCIF(this.custCIF.getValue());
				customerEmail.setLovDescCustShrtName(this.custShrtName.getValue());
				map.put("customerEMail", customerEmail);
				map.put("customerDialogCtrl", this);
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

	public void doFillCustomerEmailDetails(List<CustomerEMail> customerEmailDetails) {
		logger.debug("Entering");
		this.listBoxCustomerEmails.getItems().clear();

		customerEmailInlineEditCtrl.doRenderEmailsList(customerEmailDetails, listBoxCustomerEmailsInlineEdit,
				this.custCIF.getValue(), isFinanceProcess, PennantConstants.MODULETYPE_ENQ.equals(this.moduleType));

		logger.debug("Leaving");
	}

	// ********************************************************************//
	// ++ New Button & Double Click Events for Customer Bank Information List
	// ++//
	// ********************************************************************//
	public void onClick$btnNew_BankInformation(Event event) {
		logger.debug("Entering");
		CustomerBankInfo custBankInfo = new CustomerBankInfo();
		Customer customer = getCustomerDetails().getCustomer();
		custBankInfo.setNewRecord(true);
		custBankInfo.setWorkflowId(0);
		custBankInfo.setCustID(getCustomerDetails().getCustID());
		custBankInfo.setLovDescCustCIF(customer.getCustCIF());
		custBankInfo.setLovDescCustShrtName(customer.getCustShrtName());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerBankInfo", custBankInfo);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("isFinanceProcess", isFinanceProcess);
		map.put("roleCode", getRole());
		map.put("CustomerBankInfoList", CustomerBankInfoList);
		map.put("retailCustomer", StringUtils.equals(this.custCtgCode.getValue(), PennantConstants.PFF_CUSTCTG_INDIV));
		map.put("fromLoan", fromLoan);
		map.put("empType", customer.getSubCategory());
		if (financeMain != null) {
			map.put("finAmount", financeMain.getFinAmount());
			map.put("tenor", financeMain.getNumberOfTerms());
			map.put("finReference", financeMain.getFinReference());
		} else {
			map.put("finReference", finReference);
		}
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerBankInfoDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void onCustomerBankInfoItemDoubleClicked(Event event) {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerBankInformation.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerBankInfo custBankInfo = (CustomerBankInfo) item.getAttribute("data");
			if (isDeleteRecord(custBankInfo.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				Customer customer = getCustomerDetails().getCustomer();
				custBankInfo.setLovDescCustCIF(this.custCIF.getValue());
				custBankInfo.setLovDescCustShrtName(this.custShrtName.getValue());
				map.put("customerBankInfo", custBankInfo);
				map.put("customerDialogCtrl", this);
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
				map.put("CustomerBankInfoList", CustomerBankInfoList);
				map.put("retailCustomer",
						StringUtils.equals(this.custCtgCode.getValue(), PennantConstants.PFF_CUSTCTG_INDIV));
				map.put("fromLoan", fromLoan);
				map.put("empType", customer.getSubCategory());
				if (financeMain != null) {
					map.put("finAmount", financeMain.getFinAmount());
					map.put("tenor", financeMain.getNumberOfTerms());
					map.put("finReference", financeMain.getFinReference());
				} else {
					map.put("finReference", finReference);
				}
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

	public void doFillCustomerBankInfoDetails(List<CustomerBankInfo> customerBankInfoDetails) {
		logger.debug("Entering");
		CustomerBankInfoList = customerBankInfoDetails;
		this.listBoxCustomerBankInformation.getItems().clear();
		if (customerBankInfoDetails != null) {
			for (CustomerBankInfo custBankInfo : customerBankInfoDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(custBankInfo.getLovDescBankName());
				lc.setParent(item);
				lc = new Listcell(custBankInfo.getAccountNumber());
				lc.setParent(item);
				lc = new Listcell(custBankInfo.getLovDescAccountType());
				lc.setParent(item);
				lc = new Listcell(custBankInfo.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(custBankInfo.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", custBankInfo);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerBankInfoItemDoubleClicked");
				this.listBoxCustomerBankInformation.appendChild(item);
			}
			setCustomerBankInfoDetailList(customerBankInfoDetails);
		}
		logger.debug("Leaving");
	}

	public void doFillCustomerCardSalesInfoDetails(List<CustCardSales> customerCardSalesInfoDetails) {
		logger.debug("Entering");
		CustomerCardSalesInfoList = customerCardSalesInfoDetails;
		this.listBoxCustomerCardSalesInformation.getItems().clear();
		if (customerCardSalesInfoDetails != null) {
			for (CustCardSales custCardSalesInfo : customerCardSalesInfoDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(custCardSalesInfo.getMerchantId());
				lc.setParent(item);
				lc = new Listcell(custCardSalesInfo.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(custCardSalesInfo.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", custCardSalesInfo);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerCardSalesInfoItemDoubleClicked");
				this.listBoxCustomerCardSalesInformation.appendChild(item);
			}
			setCustomerCardSales(customerCardSalesInfoDetails);
		}
		logger.debug("Leaving");
	}

	// ********************************************************************//
	// ++ New Button & Double Click Events for Cheque Information List ++//
	// ********************************************************************//
	public void onClick$btnNew_ChequeInformation(Event event) {
		logger.debug("Entering");
		CustomerChequeInfo custChequeInfo = new CustomerChequeInfo();
		custChequeInfo.setNewRecord(true);
		custChequeInfo.setWorkflowId(0);
		custChequeInfo.setCustID(getCustomerDetails().getCustID());
		custChequeInfo.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		custChequeInfo.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		custChequeInfo.setChequeSeq(getChequeSeq());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerChequeInfo", custChequeInfo);
		map.put("finFormatter", ccyFormatter);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("isFinanceProcess", isFinanceProcess);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerChequeInfoDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void onCustomerChequeInfoItemDoubleClicked(Event event) {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerChequeInformation.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerChequeInfo custChequeInfo = (CustomerChequeInfo) item.getAttribute("data");
			if (isDeleteRecord(custChequeInfo.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				custChequeInfo.setLovDescCustCIF(this.custCIF.getValue());
				custChequeInfo.setLovDescCustShrtName(this.custShrtName.getValue());
				map.put("customerChequeInfo", custChequeInfo);
				map.put("finFormatter", ccyFormatter);
				map.put("customerDialogCtrl", this);
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

	public void doFillCustomerChequeInfoDetails(List<CustomerChequeInfo> customerChequeInfoDetails) {
		logger.debug("Entering");
		this.listBoxCustomerChequeInformation.getItems().clear();
		if (customerChequeInfoDetails != null) {
			for (CustomerChequeInfo custChequeInfo : customerChequeInfoDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(DateUtil.format(custChequeInfo.getMonthYear(), PennantConstants.monthYearFormat));
				lc.setParent(item);
				lc = new Listcell(
						PennantApplicationUtil.amountFormate(custChequeInfo.getTotChequePayment(), ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(custChequeInfo.getSalary(), ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(
						PennantApplicationUtil.amountFormate(custChequeInfo.getReturnChequeAmt(), ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(String.valueOf(custChequeInfo.getReturnChequeCount()));
				lc.setParent(item);
				lc = new Listcell(custChequeInfo.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(custChequeInfo.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", custChequeInfo);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerChequeInfoItemDoubleClicked");
				this.listBoxCustomerChequeInformation.appendChild(item);

			}
			setCustomerChequeInfoDetailList(customerChequeInfoDetails);
		}
		logger.debug("Leaving");
	}

	// ********************************************************************//
	// ++ New Button & Double Click Events for Cheque Information List ++//
	// ********************************************************************//
	public void onClick$btnNew_ExternalLiability(Event event) {
		logger.debug("Entering");
		CustomerExtLiability externalLiability = new CustomerExtLiability();
		externalLiability.setNewRecord(true);
		externalLiability.setWorkflowId(0);
		externalLiability.setCustId(getCustomerDetails().getCustID());
		externalLiability.setCustCif(getCustomerDetails().getCustomer().getCustCIF());
		externalLiability.setCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		externalLiability.setSeqNo(getLiabilitySeq());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("externalLiability", externalLiability);
		map.put("finFormatter", ccyFormatter);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("isFinanceProcess", isFinanceProcess);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerExtLiabilityDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void onCustomerExtLiabilityItemDoubleClicked(Event event) {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerExternalLiability.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerExtLiability externalLiability = (CustomerExtLiability) item.getAttribute("data");
			if (isDeleteRecord(externalLiability.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				externalLiability.setCustCif(this.custCIF.getValue());
				externalLiability.setCustShrtName(this.custShrtName.getValue());
				map.put("externalLiability", externalLiability);
				map.put("finFormatter", ccyFormatter);
				map.put("customerDialogCtrl", this);
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
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

	public void doFillCustomerExtLiabilityDetails(List<CustomerExtLiability> customerExtLiabilityDetails) {
		logger.debug("Entering");
		this.listBoxCustomerExternalLiability.getItems().clear();

		BigDecimal tolatOriginalAmount = BigDecimal.ZERO;
		BigDecimal totalInstalmentAmount = BigDecimal.ZERO;
		BigDecimal totalOutstandingBalance = BigDecimal.ZERO;

		BigDecimal originalAmount;
		BigDecimal instalmentAmount;
		BigDecimal outstandingBalance;

		if (customerExtLiabilityDetails != null) {
			for (CustomerExtLiability custExtLiability : customerExtLiabilityDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				if (custExtLiability.getFinDate() == null) {
					lc = new Listcell();
				} else {
					lc = new Listcell(DateUtil.formatToLongDate(custExtLiability.getFinDate()));
				}
				lc.setParent(item);
				lc = new Listcell(custExtLiability.getFinTypeDesc());
				lc.setParent(item);
				lc = new Listcell(custExtLiability.getLoanBankName());
				lc.setParent(item);

				// Original Amount
				originalAmount = custExtLiability.getOriginalAmount();
				if (originalAmount == null) {
					originalAmount = BigDecimal.ZERO;
				}
				tolatOriginalAmount = tolatOriginalAmount.add(originalAmount);
				lc = new Listcell(PennantApplicationUtil.amountFormate(originalAmount, ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Installment Amount
				instalmentAmount = custExtLiability.getInstalmentAmount();
				if (instalmentAmount == null) {
					instalmentAmount = BigDecimal.ZERO;
				}
				totalInstalmentAmount = totalInstalmentAmount.add(instalmentAmount);
				lc = new Listcell(PennantApplicationUtil.amountFormate(instalmentAmount, ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Outstanding Balance
				outstandingBalance = custExtLiability.getOutstandingBalance();
				if (outstandingBalance == null) {
					outstandingBalance = BigDecimal.ZERO;
				}
				totalOutstandingBalance = totalOutstandingBalance.add(outstandingBalance);
				lc = new Listcell(PennantApplicationUtil.amountFormate(outstandingBalance, ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				lc = new Listcell(custExtLiability.getCustStatusDesc());
				lc.setParent(item);
				lc = new Listcell(custExtLiability.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(custExtLiability.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", custExtLiability);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerExtLiabilityItemDoubleClicked");
				this.listBoxCustomerExternalLiability.appendChild(item);

			}
			// add summary list item
			if (this.listBoxCustomerExternalLiability.getItems() != null
					&& !this.listBoxCustomerExternalLiability.getItems().isEmpty()) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(Labels.getLabel("label_CustomerExtLiabilityDialog_Totals.value"));
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(tolatOriginalAmount, ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(totalInstalmentAmount, ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(totalOutstandingBalance, ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				item.setAttribute("data", "");
				this.listBoxCustomerExternalLiability.appendChild(item);
			}
			setCustomerExtLiabilityDetailList(customerExtLiabilityDetails);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnNew_CardSalesInformation(Event event) {
		logger.debug("Entering");
		CustCardSales custCardSales = new CustCardSales();
		custCardSales.setNewRecord(true);
		custCardSales.setWorkflowId(0);
		custCardSales.setCustID(getCustomerDetails().getCustID());
		custCardSales.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		custCardSales.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("customerCardSales", custCardSales);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("isFinanceProcess", isFinanceProcess);
		map.put("roleCode", getRole());
		map.put("CustomerCardSalesInfoList", CustomerCardSalesInfoList);
		map.put("retailCustomer", StringUtils.equals(this.custCtgCode.getValue(), PennantConstants.PFF_CUSTCTG_INDIV));
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerCardSalesDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void onCustomerCardSalesInfoItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerCardSalesInformation.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustCardSales custBankInfo = (CustCardSales) item.getAttribute("data");
			if (isDeleteRecord(custBankInfo.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				custBankInfo.setLovDescCustCIF(this.custCIF.getValue());
				custBankInfo.setLovDescCustShrtName(this.custShrtName.getValue());
				map.put("customerCardSales", custBankInfo);
				map.put("customerDialogCtrl", this);
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
				map.put("CustomerCardSalesInfoList", CustomerCardSalesInfoList);
				map.put("retailCustomer",
						StringUtils.equals(this.custCtgCode.getValue(), PennantConstants.PFF_CUSTCTG_INDIV));
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerCardSalesDialog.zul",
							null, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void doFillCustFinanceExposureDetails(List<FinanceEnquiry> custFinanceExposureDetails) {
		logger.debug("Entering");
		this.listBoxCustomerFinExposure.getItems().clear();
		if (custFinanceExposureDetails != null) {
			for (FinanceEnquiry finEnquiry : custFinanceExposureDetails) {

				int format = CurrencyUtil.getFormat(finEnquiry.getFinCcy());
				Listitem item = new Listitem();
				Listcell lc = new Listcell(DateUtil.formatToLongDate(finEnquiry.getFinStartDate()));
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getLovDescFinTypeName());
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinReference());
				lc.setParent(item);

				BigDecimal totAmt = finEnquiry.getFinCurrAssetValue().add(finEnquiry.getFeeChargeAmt());
				lc = new Listcell(PennantApplicationUtil.amountFormate(totAmt, format));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(finEnquiry.getMaxInstAmount(), format));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil
						.amountFormate(totAmt.subtract(finEnquiry.getFinRepaymentAmount()), format));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(finEnquiry.isFinIsActive() ? "Active" : "In Active");
				lc.setParent(item);
				lc = new Listcell(
						finEnquiry.getCustomerType() == null ? "Main Applicant" : finEnquiry.getCustomerType());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				this.listBoxCustomerFinExposure.appendChild(item);

			}
		}
		logger.debug("Leaving");
	}

	public int getChequeSeq() {
		int idNumber = 0;
		if (getCustomerChequeInfoDetailList() != null && !getCustomerChequeInfoDetailList().isEmpty()) {
			for (CustomerChequeInfo customerChequeInfo : getCustomerChequeInfoDetailList()) {
				int tempId = customerChequeInfo.getChequeSeq();
				if (tempId > idNumber) {
					idNumber = tempId;
				}
			}
		}
		return idNumber + 1;
	}

	public int getLiabilitySeq() {
		int idNumber = 0;
		if (getCustomerExtLiabilityDetailList() != null && !getCustomerExtLiabilityDetailList().isEmpty()) {
			for (CustomerExtLiability customerExtLiability : getCustomerExtLiabilityDetailList()) {
				int tempId = customerExtLiability.getSeqNo();
				if (tempId > idNumber) {
					idNumber = tempId;
				}
			}
		}
		return idNumber + 1;
	}

	/**
	 * Generate the Customer Address Details List in the CustomerDialogCtrl and set the list in the
	 * listBoxCustomerAddress listbox by using Pagination
	 */
	public void doFillCustomerDirectory(List<DirectorDetail> customerDirectory) {
		logger.debug("Entering");
		this.listBoxCustomerDirectory.getItems().clear();
		if (customerDirectory != null && customerDirectory.size() > 0) {
			setDirectorList(customerDirectory);
			ListModelList<DirectorDetail> listModelList = new ListModelList<DirectorDetail>(customerDirectory);
			this.listBoxCustomerDirectory.setModel(listModelList);
			this.listBoxCustomerDirectory.setItemRenderer(new DirectorDetailListModelItemRenderer(
					PennantAppUtil.getCustomerDocumentTypesList(), PennantAppUtil.getCustomerCountryTypesList()));
		}
		logger.debug("Leaving");
	}

	/**
	 * Generate the Customer Income Details List in the CustomerDialogCtrl and set the list in the listBoxCustomerIncome
	 * listbox by using Pagination
	 */
	protected Listbox incomeSummary;

	public void doFillCustomerIncome(List<CustomerIncome> incomes) {
		logger.debug("Entering");
		setIncomeList(incomes);
		incomeAndExpenseCtrl.doRenderIncomeList(incomes, this.listBoxCustomerIncomeInLineEdit, ccyFormatter,
				PennantConstants.MODULETYPE_ENQ.equals(this.moduleType));

		logger.debug("Leaving");
	}
	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerDetails
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerDetails aCustomerDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerDetails.getBefImage(), aCustomerDetails);
		return new AuditHeader(String.valueOf(aCustomerDetails.getCustID()),
				String.valueOf(aCustomerDetails.getCustID()), null, null, auditDetail,
				aCustomerDetails.getUserDetails(), getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for retrieving NotesDetails
	 */
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("Customer");
		notes.setReference(String.valueOf(getCustomerDetails().getCustID()));
		notes.setVersion(getCustomerDetails().getCustomer().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	public void onClickExtbtnVIEWREPORT() throws IOException {
		logger.debug(Literal.ENTERING);

		customerDetails.getCustomer().getCustCIF();
		String path = App.getResourcePath("config", "CIBILFinalTemplate.FTL");
		File ftlFile = new File(path);
		StringTemplateLoader loader = new StringTemplateLoader();
		byte[] cibildata = FileUtils.readFileToByteArray(ftlFile);
		loader.putTemplate("CIBILFinalTemplate.FTL", new String(cibildata));

		Configuration config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		config.setClassForTemplateLoading(CustomerDialogCtrl.class, "CIBILFinalTemplate.FTL");
		config.setTemplateLoader(loader);
		config.setDefaultEncoding("UTF-8");
		config.setLocale(Locale.getDefault());
		config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		TEMPLATES.put("CIBILFinalTemplate.FTL", config);

		String result = null;
		String response = null;
		Map<String, Object> mapValues = getCustomerDetails().getExtendedFieldRender().getMapValues();
		if (mapValues != null && mapValues.containsKey("JsonResponse") && mapValues.get("JsonResponse") != null) {
			response = (String) mapValues.get("JsonResponse");
		} else {
			StringBuilder tableName = new StringBuilder();
			tableName.append(InterfaceConstants.MODULE_CUSTOMER);
			tableName.append("_");
			tableName.append(getCustomerDetails().getCustomer().getCustCtgCode());
			tableName.append("_ED");
			response = customerDetailsService.getExternalCibilResponse(getCustomerDetails().getCustomer().getCustCIF(),
					tableName.toString());
		}
		JSONObject json = null;

		try {
			JSONParser parser = new JSONParser();
			if (response != null) {
				json = new JSONObject();
				json = (JSONObject) parser.parse(response);
			}
		} catch (Exception e) {
			logger.error("Exception", e);
		}

		try {
			if (json != null) {
				result = FreeMarkerTemplateUtils.processTemplateIntoString(getTemplate("CIBILFinalTemplate.FTL"), json);
				Map<String, Object> detailMap = new HashMap<String, Object>();
				detailMap.put("reportData", result);
				detailMap.put("isCibil", true);
				detailMap.put("reportName", "CIBIL");

				Executions.createComponents("/WEB-INF/pages/Cibil/CibilReportView.zul", window_CustomerDialog,
						detailMap);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private Template getTemplate(String templateName) throws IOException {
		Configuration config = null;
		config = TEMPLATES.get(templateName);

		if (config == null) {
			throw new AppException("The required template not found.");
		}

		return config.getTemplate(templateName);
	}

	/**
	 * Method for Resetting Employment Status based on Salaried Customer or Not on Check
	 * 
	 * @param isSalaried
	 * @return
	 */
	public boolean setEmpStatusOnSalCust(Tab custTab) {
		if (salariedCustomer.isChecked() && StringUtils.isNotEmpty(empStatus.getValue()) && !this.empStatus.isReadonly()
				&& !StringUtils.equalsIgnoreCase(empStatus.getValue(), PennantConstants.CUSTEMPSTS_EMPLOYED)
				&& !StringUtils.equalsIgnoreCase(empStatus.getDescription(), PennantConstants.CUSTEMPSTS_EMPLOYED)) {
			String msg = Labels.getLabel("label_STLCustomer",
					new String[] { PennantConstants.CUSTEMPSTS_EMPLOYED, Labels.getLabel("label_Salaried") });
			custTab.setSelected(true);
			this.tabkYCDetails.setSelected(true);
			MessageUtil.showError(msg);
			return false;
		}
		return true;
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(ArrayList<Object> finHeaderList) {
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", finHeaderList);
			map.put("moduleName", moduleName);
			if (isFinanceProcess) {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul",
						this.finBasicdetails, map);
			} else {
				Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralBasicDetails.zul",
						this.finBasicdetails, map);
			}
		} catch (Exception e) {
			logger.debug(e);
		}

	}

	/**
	 * On Click
	 * 
	 * @param event
	 */
	public void onOpen$custGenderCode(Event event) {
		logger.debug(Literal.ENTERING);
		custGenderCode.clearErrorMessage();
		fillComboBox(this.custGenderCode, getComboboxValue(this.custGenderCode), PennantAppUtil.getGenderCodes(), "");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * On Click
	 * 
	 * @param event
	 */
	public void onOpen$custSalutationCode(Event event) {
		logger.debug(Literal.ENTERING);
		this.custSalutationCode.clearErrorMessage();
		fillComboBox(this.custSalutationCode, getComboboxValue(this.custSalutationCode),
				PennantAppUtil.getSalutationCodes(getComboboxValue(this.custGenderCode)), "");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * On Click
	 * 
	 * @param event
	 */
	public void onOpen$custMaritalSts(Event event) {
		logger.debug(Literal.ENTERING);
		this.custMaritalSts.clearErrorMessage();
		fillComboBox(this.custMaritalSts, getComboboxValue(this.custMaritalSts),
				PennantAppUtil.getMaritalStsTypes(getComboboxValue(this.custGenderCode)), "");
		logger.debug(Literal.LEAVING);
	}

	public void setComboBoxValue(Combobox combobox, String value, String label) {
		logger.debug("Entering fillComboBox()");
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		if (StringUtils.isEmpty(value)) {
			comboitem.setValue("#");
			comboitem.setLabel(Labels.getLabel("Combo.Select"));
		} else {
			comboitem.setValue(value);
			comboitem.setLabel(StringUtils.isEmpty(label) ? value : label);
		}
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);
		logger.debug("Leaving fillComboBox()");
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		if (isFinanceProcess) {
			getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
		} else {
			getCollateralBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
		}
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

	public void setCustomerListCtrl(CustomerListCtrl customerListCtrl) {
		this.customerListCtrl = customerListCtrl;
	}

	public CustomerListCtrl getCustomerListCtrl() {
		return this.customerListCtrl;
	}

	// Paged List Wrapper Declarations For Customer Related List
	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

	public int getCountRows() {
		return countRows;
	}

	public void setCustomerDetails(CustomerDetails customerDetails) {
		this.customerDetails = customerDetails;
	}

	public CustomerDetails getCustomerDetails() {
		return customerDetails;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public DedupParmService getDedupParmService() {
		return dedupParmService;
	}

	public void setDedupParmService(DedupParmService dedupParmService) {
		this.dedupParmService = dedupParmService;
	}

	// Customer Related List
	public List<CustomerRating> getRatingsList() {
		return ratingsList;
	}

	public void setRatingsList(List<CustomerRating> ratingsList) {
		this.ratingsList = ratingsList;
	}

	public List<CustomerPhoneNumber> getPhoneNumberList() {
		return phoneNumberList;
	}

	public void setPhoneNumberList(List<CustomerPhoneNumber> phoneNumberList) {
		this.phoneNumberList = phoneNumberList;
	}

	public List<CustomerIncome> getIncomeList() {
		return incomeList;
	}

	public void setIncomeList(List<CustomerIncome> incomeList) {
		this.incomeList = incomeList;
	}

	public void setCustomerEmploymentDetailList(List<CustomerEmploymentDetail> employmentDetailsList) {
		this.customerEmploymentDetailList = employmentDetailsList;
	}

	public List<CustomerEmploymentDetail> getCustomerEmploymentDetailList() {
		return customerEmploymentDetailList;
	}

	public void setOldVar_EmploymentDetailsList(List<CustomerEmploymentDetail> oldVar_EmploymentDetailsList) {
		this.oldVar_EmploymentDetailsList = oldVar_EmploymentDetailsList;
	}

	public List<CustomerEmploymentDetail> getOldVar_EmploymentDetailsList() {
		return oldVar_EmploymentDetailsList;
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

	private void doSetCustTypeFilters(String custCtgType) {
		if (StringUtils.isNotBlank(custCtgType)) {
			Filter filter[] = new Filter[1];
			filter[0] = new Filter("CustTypeCtg", custCtgType, Filter.OP_EQUAL);
			this.custTypeCode.setFilters(filter);
		}
	}

	private boolean isDeleteRecord(String rcdType) {
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, rcdType)
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, rcdType)) {
			return true;
		}
		return false;
	}

	public void onChange$custDOB(Event event) {
		logger.debug(Literal.ENTERING);
		processDateDiff(this.custDOB.getValue(), this.age);
		logger.debug(Literal.LEAVING);
	}

	public void onChange$empFrom(Event event) {
		logger.debug(Literal.ENTERING);
		processDateDiff(this.empFrom.getValue(), this.exp);
		logger.debug(Literal.LEAVING);
	}

	public void onChange$eidNumber(Event event) {
		if (MasterDefUtil.isValidationReq(DocType.PAN)) {
			CustomerDetails aCustomerDetails = getCustomerDetails();
			this.label_CustomerDialog_EIDName.setValue(" ");
			aCustomerDetails.getCustomer().setCustCRCPR(this.eidNumber.getValue());
			String roles = SysParamUtil.getValueAsString(SMTParameterConstants.ALLOW_PAN_VALIDATION_RULE);
			roles = StringUtils.trimToEmpty(roles);
			if (roles.contains(getRole()) || getCustomerDetails().getCustomer().isNewRecord() || isFirstTask) {
				setPrimaryAccountDetails(aCustomerDetails);
			}
		}
	}

	private void setPrimaryAccountDetails(CustomerDetails aCustomerDetails) {
		logger.debug(Literal.ENTERING);

		Customer customer = aCustomerDetails.getCustomer();
		String panNumber = customer.getCustCRCPR();

		if (StringUtils.isBlank(panNumber) || !MasterDefUtil.isValidationReq(MasterDefUtil.DocType.PAN)) {
			logger.debug(Literal.LEAVING);
			return;
		}

		validatePAN(this.eidNumber.getValue());
		logger.debug(Literal.LEAVING);
	}

	private String validatePAN(String panNumber) {
		String primaryIdName = null;
		this.masterDef = MasterDefUtil.getMasterDefByType(DocType.PAN);

		if (this.masterDef == null || !this.masterDef.isValidationReq()) {
			return primaryIdName;
		}

		DocVerificationHeader header = new DocVerificationHeader();
		header.setDocNumber(panNumber);
		header.setCustCif(this.custCIF.getValue());

		if (!DocVerificationUtil.isVerified(panNumber, DocType.PAN)) {
			ErrorDetail err = DocVerificationUtil.doValidatePAN(header, true);

			if (err != null) {
				this.isKYCverified = false;
				MessageUtil.showMessage(err.getMessage());
			} else {
				this.isKYCverified = true;
				primaryIdName = header.getDocVerificationDetail().getFullName();
				MessageUtil.showMessage(String.format("%s PAN validation successfull.", primaryIdName));
			}
		} else {
			String msg = Labels.getLabel("lable_Document_reverification.value", new Object[] { "PAN Number" });

			MessageUtil.confirm(msg, evnt -> {
				if (Messagebox.ON_YES.equals(evnt.getName())) {
					ErrorDetail err = DocVerificationUtil.doValidatePAN(header, true);

					if (err != null) {
						this.isKYCverified = false;
						MessageUtil.showMessage(err.getMessage());
					} else {
						this.isKYCverified = true;
						String fullName = header.getDocVerificationDetail().getFullName();
						MessageUtil.showMessage(String.format("%s PAN validation successfull.", fullName));
					}
				}
			});
		}

		if (header.getDocVerificationDetail() != null) {
			primaryIdName = header.getDocVerificationDetail().getFullName();

			if (isRetailCustomer) {
				this.custFirstName.setValue(header.getDocVerificationDetail().getFName());
				this.custLastName.setValue(header.getDocVerificationDetail().getLName());
				this.custMiddleName.setValue(header.getDocVerificationDetail().getMName());
				this.custShrtName.setValue(primaryIdName);
			} else {
				this.custShrtName.setValue(StringUtils.trimToEmpty(header.getDocVerificationDetail().getLName()));
			}
		}

		return primaryIdName;
	}

	private BigDecimal processDateDiff(Date fromDate, Label displayComp) {
		BigDecimal dateDiff = BigDecimal.ZERO;
		dateDiff.setScale(2);
		if (fromDate == null) {
			displayComp.setValue("");
			displayComp.setVisible(false);
			return dateDiff;
		}

		int years = 0;
		int month = 0;
		if (fromDate.compareTo(appDate) < 0) {
			int months = DateUtil.getMonthsBetween(appDate, fromDate);
			years = months / 12;
			month = months % 12;
			dateDiff = new BigDecimal(months % 12);
			dateDiff = dateDiff.divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN);
		}
		if (years == 0 && month == 0) {
			displayComp.setVisible(false);
			dateDiff = BigDecimal.ZERO;
		} else {
			dateDiff = dateDiff.add(new BigDecimal(years));
			String dateDiffValue = (years == 0 ? "" : years + " " + (years == 1 ? "Year" + " " : "Years" + " ")) + month
					+ " " + (month == 1 ? "Month" : "Months");
			displayComp.setValue(dateDiffValue);
			displayComp.setVisible(true);
		}
		return dateDiff;
	}

	public void setCustCoreBankid(String custCoreBankId) {
		this.custCoreBank.setValue(custCoreBankId);
		this.customerDetails.setCustCoreBank(custCoreBankId);
		try {
			dedupCheckReq = true;
			doSave();
		} catch (InterfaceException e) {
			dedupCheckReq = false;
			MessageUtil.showError(e);
		}
	}

	public void doFillGstDetails(List<GSTDetail> detailsList) {
		gstDetailsList = detailsList;

		logger.debug(Literal.ENTERING);
		this.listBoxCustomerGstDetails.getItems().clear();

		if (detailsList != null) {
			for (GSTDetail gstDetail : detailsList) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(StringUtils.trimToEmpty(gstDetail.getGstNumber()));
				lc.setParent(item);
				lc = new Listcell(gstDetail.getAddress());
				lc.setParent(item);
				lc = new Listcell(gstDetail.getCityCode());
				lc.setParent(item);
				lc = new Listcell(gstDetail.getStateCode());
				lc.setParent(item);
				lc = new Listcell(gstDetail.getPinCode());
				lc.setParent(item);
				lc = new Listcell();
				final Checkbox cbDefault = new Checkbox();
				cbDefault.setId("GstState_" + gstDetail.getStateCode());
				cbDefault.setParent(lc);
				cbDefault.setChecked(gstDetail.isDefaultGST());
				cbDefault.setDisabled(
						this.enqiryModule || !getUserWorkspace().isAllowed("btnNew_CustomerDialog_GSTDetails"));
				if (!cbDefault.isDisabled()) {
					cbDefault.addForward("onCheckGstDefault", this.window_CustomerDialog, "onCheckDefault", gstDetail);
				}

				lc.appendChild(cbDefault);
				lc.setParent(item);
				lc = new Listcell();
				final Checkbox cbGSTIN = new Checkbox();
				cbGSTIN.setDisabled(true);
				cbGSTIN.setChecked(gstDetail.isTin());
				lc.appendChild(cbGSTIN);
				lc.setParent(item);
				lc = new Listcell();
				final Checkbox cbGSTInName = new Checkbox();
				cbGSTInName.setDisabled(true);
				cbGSTInName.setChecked(gstDetail.isTinName());
				lc.appendChild(cbGSTInName);
				lc.setParent(item);
				lc = new Listcell();
				final Checkbox cbGSTInAddress = new Checkbox();
				cbGSTInAddress.setDisabled(true);
				cbGSTInAddress.setChecked(gstDetail.isTinAddress());
				lc.appendChild(cbGSTInAddress);
				lc.setParent(item);
				item.setAttribute("data", gstDetail);

				ComponentsCtrl.applyForward(item, "onDoubleClick=onGstDetailsItemDoubleClicked");
				this.listBoxCustomerGstDetails.appendChild(item);
			}
			setGstDetailsList(detailsList);
		}
		logger.debug(Literal.LEAVING);

	}

	public void onClick$btnNew_GSTDetails(Event event) {
		logger.debug(Literal.ENTERING);

		GSTDetail gstDetails = new GSTDetail();
		gstDetails.setNewRecord(true);
		gstDetails.setWorkflowId(0);
		gstDetails.setCustID(getCustomerDetails().getCustID());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("gstDetails", gstDetails);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("finFormatter", ccyFormatter);
		map.put("isFinanceProcess", isFinanceProcess);
		map.put("gstDetailsList", gstDetailsList);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/GSTDetailDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onGstDetailsItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerGstDetails.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final GSTDetail gstDetail = (GSTDetail) item.getAttribute("data");
			if (isDeleteRecord(gstDetail.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final Map<String, Object> map = new HashMap<>();
				map.put("gstDetails", gstDetail);
				map.put("customerDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("isFinanceProcess", isFinanceProcess);
				map.put("moduleType", this.moduleType);
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/GSTDetailDialog.zul",
							window_CustomerDialog, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onCheckGstDefault(Event event) {
		logger.debug(Literal.ENTERING);

		GSTDetail detail = (GSTDetail) event.getData();
		this.listBoxCustomerGstDetails.getFellowIfAny("GstState_" + detail.getStateCode());

		logger.debug(Literal.LEAVING);
	}

	public String getCustcrcpr() {
		return this.eidNumber.getValue();
	}

	public String getCustomerShortName() {
		return this.custShrtName.getValue();
	}

	public DirectorDetailService getDirectorDetailService() {
		return directorDetailService;
	}

	public void setDirectorDetailService(DirectorDetailService directorDetailService) {
		this.directorDetailService = directorDetailService;
	}

	public List<CustomerDocument> getCustomerDocumentDetailList() {
		return customerDocumentDetailList;
	}

	public void setCustomerDocumentDetailList(List<CustomerDocument> customerDocumentDetailList) {
		this.customerDocumentDetailList = customerDocumentDetailList;
	}

	public List<CustomerAddres> getCustomerAddressDetailList() {
		return customerAddressDetailList;
	}

	public void setCustomerAddressDetailList(List<CustomerAddres> customerAddressDetailList) {
		this.customerAddressDetailList = customerAddressDetailList;
	}

	public List<CustomerPhoneNumber> getCustomerPhoneNumberDetailList() {
		return customerPhoneNumberDetailList;
	}

	public void setCustomerPhoneNumberDetailList(List<CustomerPhoneNumber> customerPhoneNumberDetailList) {
		this.customerPhoneNumberDetailList = customerPhoneNumberDetailList;
	}

	public List<CustomerEMail> getCustomerEmailDetailList() {
		return customerEmailDetailList;
	}

	public void setCustomerEmailDetailList(List<CustomerEMail> customerEmailDetailList) {
		this.customerEmailDetailList = customerEmailDetailList;
	}

	public List<CustomerBankInfo> getCustomerBankInfoDetailList() {
		return customerBankInfoDetailList;
	}

	public void setCustomerBankInfoDetailList(List<CustomerBankInfo> customerBankInfoDetailList) {
		this.customerBankInfoDetailList = customerBankInfoDetailList;
	}

	public List<CustomerChequeInfo> getCustomerChequeInfoDetailList() {
		return customerChequeInfoDetailList;
	}

	public void setCustomerChequeInfoDetailList(List<CustomerChequeInfo> customerChequeInfoDetailList) {
		this.customerChequeInfoDetailList = customerChequeInfoDetailList;
	}

	public List<CustomerExtLiability> getCustomerExtLiabilityDetailList() {
		return customerExtLiabilityDetailList;
	}

	public void setCustomerExtLiabilityDetailList(List<CustomerExtLiability> customerExtLiabilityDetailList) {
		this.customerExtLiabilityDetailList = customerExtLiabilityDetailList;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public boolean isNewFinance() {
		return newFinance;
	}

	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public FinanceDetail getFinancedetail() {
		return financedetail;
	}

	public void setFinancedetail(FinanceDetail financedetail) {
		this.financedetail = financedetail;
	}

	public Object getPromotionPickListCtrl() {
		return promotionPickListCtrl;
	}

	public void setPromotionPickListCtrl(Object promotionPickListCtrl) {
		this.promotionPickListCtrl = promotionPickListCtrl;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public boolean isRetailCustomer() {
		return isRetailCustomer;
	}

	public void setRetailCustomer(boolean isRetailCustomer) {
		this.isRetailCustomer = isRetailCustomer;
	}

	public CollateralBasicDetailsCtrl getCollateralBasicDetailsCtrl() {
		return collateralBasicDetailsCtrl;
	}

	public void setCollateralBasicDetailsCtrl(CollateralBasicDetailsCtrl collateralBasicDetailsCtrl) {
		this.collateralBasicDetailsCtrl = collateralBasicDetailsCtrl;
	}

	public void setRcuVerificationDialogCtrl(RCUVerificationDialogCtrl rcuVerificationDialogCtrl) {
		this.rcuVerificationDialogCtrl = rcuVerificationDialogCtrl;
	}

	public void setlVerificationCtrl(LVerificationCtrl lVerificationCtrl) {
		this.lVerificationCtrl = lVerificationCtrl;
	}

	public FieldVerificationDialogCtrl getFieldVerificationDialogCtrl() {
		return fieldVerificationDialogCtrl;
	}

	public void setFieldVerificationDialogCtrl(FieldVerificationDialogCtrl fieldVerificationDialogCtrl) {
		this.fieldVerificationDialogCtrl = fieldVerificationDialogCtrl;
	}

	public List<CustomerBankInfo> getCustomerBankInfoList() {
		return CustomerBankInfoList;
	}

	public void setCustomerBankInfoList(List<CustomerBankInfo> customerBankInfoList) {
		CustomerBankInfoList = customerBankInfoList;
	}

	public List<CustCardSales> getCustomerCardSales() {
		return customerCardSales;
	}

	public void setCustomerCardSales(List<CustCardSales> customerCardSales) {
		this.customerCardSales = customerCardSales;
	}

	public List<CustomerGST> getCustomerGstList() {
		return customerGstList;
	}

	public void setCustomerGstList(List<CustomerGST> customerGstList) {
		this.customerGstList = customerGstList;
	}

	public ExtendedFieldDetailsService getExtendedFieldDetailsService() {
		return extendedFieldDetailsService;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public String getNatOfBusiness() {
		return natOfBusiness;
	}

	public void setNatOfBusiness(String natOfBusiness) {
		this.natOfBusiness = natOfBusiness;
	}

	public String getEmpType() {
		return empType;
	}

	public void setEmpType(String empType) {
		this.empType = empType;
	}

	public void onChange$natureOfBusiness(Event event) {
		logger.debug(Literal.ENTERING);
		String natureOfBusiness = getComboboxValue(this.natureOfBusiness);
		setNatOfBusiness(natureOfBusiness);
	}

	public void onChange$entityType(Event event) {
		logger.debug(Literal.ENTERING);
		String natureOfBusiness = getComboboxValue(this.entityType);
		setNatOfBusiness(natureOfBusiness);
	}

	public void onFulfillIncomeAmount(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Object[] object = (Object[]) event.getData();
		Listcell calculatedAmtCell = (Listcell) object[0];
		Listcell marginCell = (Listcell) object[1];
		// getting the margin from marginCell
		Decimalbox margin = (Decimalbox) marginCell.getChildren().get(0).getLastChild();
		// getting the income from marginCell
		CurrencyBox incomeBox = (CurrencyBox) event.getOrigin().getTarget();

		BigDecimal calculatedAmt = incomeAndExpenseCtrl.getCalculatedAmount(margin.getValue(),
				incomeBox.getActualValue());

		calculatedAmtCell.setLabel(String.valueOf(calculatedAmt));
		updateIncomeValue();
		logger.debug(Literal.LEAVING);
	}

	public void onChangeMargin(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Object[] object = (Object[]) event.getData();
		Listcell calculatedAmtCell = (Listcell) object[0];
		Listcell incomeCell = (Listcell) object[1];

		Decimalbox margin = (Decimalbox) event.getOrigin().getTarget();
		CurrencyBox income = (CurrencyBox) incomeCell.getChildren().get(0);

		BigDecimal calculatedAmt = incomeAndExpenseCtrl.getCalculatedAmount(margin.getValue(), income.getActualValue());

		calculatedAmtCell.setLabel(String.valueOf(calculatedAmt));
		updateIncomeValue();
		logger.debug(Literal.LEAVING);
	}

	public void onFulfillCustIncomeType(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Object[] object = (Object[]) event.getData();
		Listcell calculatedAmtCell = (Listcell) object[0];
		Listcell marginCell = (Listcell) object[1];
		Decimalbox margin = (Decimalbox) marginCell.getChildren().get(0).getLastChild();
		ExtendedCombobox extendedCombobox = (ExtendedCombobox) event.getOrigin().getTarget();
		if (extendedCombobox.getObject() != null) {
			IncomeType incomeType = (IncomeType) extendedCombobox.getObject();
			calculatedAmtCell.setLabel(incomeType.getIncomeExpense());
			// setting the margin value from income type
			margin.setValue(PennantApplicationUtil.formateAmount(incomeType.getMargin(), ccyFormatter));
			Events.postEvent("onChange", margin, null);
			extendedCombobox.setErrorMessage("");
		} else {
			calculatedAmtCell.setLabel("");
			margin.setValue(BigDecimal.ZERO);
		}
		updateIncomeValue();
		logger.debug(Literal.LEAVING);
	}

	public void onClickFinancialButtonDelete(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Listitem item = (Listitem) event.getData();
		incomeAndExpenseCtrl.doDelete(this.listBoxCustomerIncomeInLineEdit, item, ccyFormatter, isFinanceProcess);
		updateIncomeValue();
		logger.debug(Literal.LEAVING);
	}

	public void onChange$subCategory(Event event) {

		/*
		 * String employmentType = getComboboxValue(this.subCategory); setEmpType(employmentType); if
		 * (financeMainDialogCtrl != null && financeMainDialogCtrl instanceof FinanceMainBaseCtrl) { FinanceMainBaseCtrl
		 * financeMainBaseCtrl = (FinanceMainBaseCtrl) financeMainDialogCtrl;
		 * financeMainBaseCtrl.appendCreditReviewDetailSummaryTab(true); }
		 * 
		 */
		onChangeEmploymentType();
	}

	private void onChangeEmploymentType() {
		if (PennantConstants.EMPLOYMENTTYPE_NONWORKING.equals(subCategory.getValue())) {
			this.natureOfBusiness.setValue(Labels.getLabel("Combo.Select"));
			this.custIndustry.setValue("");
			this.custIndustry.setMandatoryStyle(false);
			this.custSegment.setValue("");
			this.custSegment.setMandatoryStyle(false);
			this.custSubSegment.setValue("");
			this.custSector.setValue("");
			this.custSector.setMandatoryStyle(false);
			this.custSubSector.setValue("");
			this.custIsStaff.setValue("");
			this.custStaffID.setValue("");
		} else {
			fillComboBox(this.natureOfBusiness, customerDetails.getCustomer().getNatureOfBusiness(),
					PennantStaticListUtil.getNatureofBusinessList(), "");
			this.custIndustry.setValue(customerDetails.getCustomer().getCustIndustry());
			this.custIndustry.setDescription(customerDetails.getCustomer().getLovDescCustIndustryName());
			this.custIndustry.setMandatoryStyle(true);
			this.custSegment.setValue(customerDetails.getCustomer().getCustSegment());
			this.custSegment.setDescription(customerDetails.getCustomer().getLovDescCustSegmentName());
			this.custSegment.setMandatoryStyle(false);
			this.custSubSegment.setValue(customerDetails.getCustomer().getCustSubSegment());
			this.custSector.setValue(customerDetails.getCustomer().getCustSector());
			this.custSector
					.setDescription(StringUtils.trimToEmpty(customerDetails.getCustomer().getLovDescCustSectorName()));
			this.custSector.setMandatoryStyle(true);
			if (!StringUtils.isEmpty(this.custSector.getValue())) {
				this.custSubSector.setValue(customerDetails.getCustomer().getCustSubSector());
				this.custSubSector.setDescription((customerDetails.getCustomer().getLovDescCustSubSectorName()));
			}
			this.custIsStaff.setValue(customerDetails.getCustomer().isCustIsStaff());
			this.custStaffID.setValue(customerDetails.getCustomer().getCustStaffID());
		}
	}

	public HashMap<String, Object> getExtendedFieldDetails() {
		HashMap<String, Object> extFieldDetails = new HashMap<>();

		if (extendedFieldCtrl != null) {
			Window window = extendedFieldCtrl.getWindow();

			Textbox employerName = null;
			String empName = "";
			try {
				if (window != null && window.getFellow("ad_EMPLOYERNAME") instanceof Textbox) {
					employerName = (Textbox) window.getFellow("ad_EMPLOYERNAME");
					if (employerName != null) {
						empName = employerName.getValue();
					}
				}
			} catch (Exception e) {
				logger.debug(Literal.EXCEPTION, e);
			}
			extFieldDetails.put("empName", empName);
		}
		return extFieldDetails;
	}

	public void onClickPhoneNumberButtonDelete(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Listitem item = (Listitem) event.getData();
		customerPhoneNumberInLineEditCtrl.doDelete(this.listBoxCustomerPhoneNumbersInlineEdit, item, isFinanceProcess);
		logger.debug(Literal.LEAVING);
	}

	public void setIncomeAndExpenseCtrl(IncomeAndExpenseCtrl incomeAndExpenseCtrl) {
		this.incomeAndExpenseCtrl = incomeAndExpenseCtrl;
	}

	/**
	 * This method will valiate the duplicate income and expense types
	 * 
	 * @param customerIncomes
	 */
	private boolean validateIncomeTypes(List<CustomerIncome> customerIncomes) {
		final Set<String> incometypes = new HashSet<String>();
		if (CollectionUtils.isNotEmpty(customerIncomes)) {
			for (CustomerIncome customerIncome : customerIncomes) {
				// skipping the delete or cancel records
				if (!PennantConstants.RECORD_TYPE_CAN.equals(customerIncome.getRecordType())
						&& !PennantConstants.RECORD_TYPE_DEL.equals(customerIncome.getRecordType())) {
					String incomeExpense = StringUtils.trimToEmpty(customerIncome.getIncomeExpense());
					String category = StringUtils.trimToEmpty(customerIncome.getCategory());
					String incomeType = StringUtils.trimToEmpty(customerIncome.getIncomeType());
					String key = new StringBuilder(incomeExpense).append(category).append(incomeType).toString();
					if (!incometypes.add(key)) {
						String errormsg = Labels.getLabel("label_IncomeTypeDialog_IncomeExpense.value") + ": "
								+ incomeExpense + ", " + Labels.getLabel("label_IncomeTypeDialog_Category.value") + ": "
								+ category + ", " + Labels.getLabel("label_IncomeTypeDialog_IncomeTypeCode.value")
								+ ": " + incomeType + " " + Labels.getLabel("label_IncomeType_Error");
						MessageUtil.showError(errormsg);
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * This method will validate the duplicate PhoneNumber types
	 * 
	 * @param customerPhoneNumbers
	 */
	private boolean validatePhoneTypes(List<CustomerPhoneNumber> customerPhoneNumbers) {
		final Set<String> phoneTypes = new HashSet<String>();
		int count = 0;
		if (CollectionUtils.isNotEmpty(customerPhoneNumbers)) {
			for (CustomerPhoneNumber customerPhoneNumber : customerPhoneNumbers) {
				// skipping the delete or cancel records
				if (!PennantConstants.RECORD_TYPE_CAN.equals(customerPhoneNumber.getRecordType())
						&& !PennantConstants.RECORD_TYPE_DEL.equals(customerPhoneNumber.getRecordType())) {
					String phoneType = StringUtils.trimToEmpty(customerPhoneNumber.getLovDescPhoneTypeCodeName());

					// validating phone types
					if (!phoneTypes.add(phoneType)) {
						String errormsg = Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneTypeCode.value") + ": "
								+ phoneType + " " + Labels.getLabel("label_IncomeType_Error");
						MessageUtil.showError(errormsg);
						return false;
					}
					// allowing only one veryHigh priority
					if (customerPhoneNumber.getPhoneTypePriority() == Integer
							.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
						count += 1;
					}
					if (count > 1) {
						String errormsg = Labels.getLabel("label_CustomerPhoneNumberDialog_CustPhonePriority.value")
								+ ": " + Labels.getLabel("label_EmailPriority_VeryHigh") + " "
								+ Labels.getLabel("label_IncomeType_Error") + ": "
								+ Labels.getLabel("label_Type_MOBILE");
						MessageUtil.showError(errormsg);
						return false;
					}
				}
			}
		}
		return true;
	}

	public void onFulfillCustPhoneType(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Object[] object = (Object[]) event.getData();
		Listcell phoneNumberCell = (Listcell) object[0];
		Textbox phoneNumber = (Textbox) phoneNumberCell.getChildren().get(0).getLastChild();
		ExtendedCombobox extendedCombobox = (ExtendedCombobox) event.getOrigin().getTarget();
		if (extendedCombobox.getObject() != null) {
			PhoneType phoneType = (PhoneType) extendedCombobox.getObject();
			extendedCombobox.setAttribute("regex", phoneType.getPhoneTypeRegex());
			int setFieldLength = customerPhoneNumberInLineEditCtrl.dosetFieldLength(phoneType.getPhoneTypeRegex());
			phoneNumber.setMaxlength(setFieldLength);
			phoneNumber.setValue("");
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method will validate the duplicate Email types
	 * 
	 * @param customerEMails
	 */
	private boolean validateEmailTypes(List<CustomerEMail> customerEMails) {
		final Set<String> emailTypes = new HashSet<String>();
		int count = 0;
		if (CollectionUtils.isNotEmpty(customerEMails)) {
			for (CustomerEMail customerEMail : customerEMails) {
				// skipping the delete or cancel records
				if (!PennantConstants.RECORD_TYPE_CAN.equals(customerEMail.getRecordType())
						&& !PennantConstants.RECORD_TYPE_DEL.equals(customerEMail.getRecordType())) {
					String emailType = StringUtils.trimToEmpty(customerEMail.getLovDescCustEMailTypeCode());

					// validating Email types
					if (!emailTypes.add(emailType)) {
						String errormsg = Labels.getLabel("listheader_CustEMailTypeCode.label") + ": " + emailType + " "
								+ Labels.getLabel("label_IncomeType_Error");
						MessageUtil.showError(errormsg);
						return false;
					}
					// allowing only one veryHigh priority
					if (customerEMail.getCustEMailPriority() == Integer
							.parseInt(PennantConstants.KYC_PRIORITY_VERY_HIGH)) {
						count += 1;
					}
					if (count > 1) {
						String errormsg = Labels.getLabel("listheader_CustEMailPriority.label") + ": "
								+ Labels.getLabel("label_EmailPriority_VeryHigh") + " "
								+ Labels.getLabel("label_IncomeType_Error") + ": "
								+ Labels.getLabel("label_Type_EMAIL");
						MessageUtil.showError(errormsg);
						return false;
					}
				}
			}
		}
		return true;
	}

	public void onClickEmailButtonDelete(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Listitem item = (Listitem) event.getData();
		customerEmailInlineEditCtrl.doDelete(this.listBoxCustomerEmailsInlineEdit, item, isFinanceProcess);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "Upload" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnUploadExternalLiability(Event event) {
		logger.debug(Literal.ENTERING);
		try {
			HashMap<String, Object> aruments = new HashMap<>();
			aruments.put("moduleCode", moduleCode);
			aruments.put("enqiryModule", enqiryModule);
			aruments.put("custId", getCustomerDetails().getCustID());
			aruments.put("customerDialogCtrl", this);
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerExtLiabilityUploadDialog.zul",
					null, aruments);
		} catch (InterfaceException e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "Upload" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnDownloadExternalLiability(Event event) {
		logger.debug(Literal.ENTERING);
		try {
			customerExtLiabilityUploadDialogCtrl.downloadExternalLiability(getCustomerExtLiabilityDetailList());
		} catch (InterfaceException e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	// PSD#157199 Customer Dialog rights are deallocating when creating new co-applicant in loan
	// New method created to allocate rights again.
	private void reallocateRights(String rightName) {

		if (isFinanceProcess && StringUtils.isNotBlank(rightName)
				&& !getUserWorkspace().getGrantedAuthoritySet().contains(rightName)
				&& StringUtils.isNotBlank(getRole())) {
			getUserWorkspace().allocateRoleAuthorities(getRole(), "CustomerDialog");
		}
	}

	public void renderCustFullName(String fullName) {

		String[] names = fullName.split(" ");

		this.custFirstName.setValue(names[0]);
		if (names.length == 3) {
			this.custMiddleName.setValue(names[1]);
			this.custLastName.setValue(names[2]);
		} else if (names.length > 3) {
			this.custLastName.setValue(names[names.length - 1]);
			StringBuilder mName = new StringBuilder("");
			for (int i = 1; i < names.length - 1; i++) {
				mName.append(names[i]).append(" ");
			}
			this.custMiddleName.setValue(mName.toString());
		} else if (names.length > 1) {
			this.custLastName.setValue(names[1]);
		}
	}

	public void setCustomerPhoneNumberInLineEditCtrl(
			CustomerPhoneNumberInLineEditCtrl customerPhoneNumberInLineEditCtrl) {
		this.customerPhoneNumberInLineEditCtrl = customerPhoneNumberInLineEditCtrl;
	}

	public void setCustomerEmailInlineEditCtrl(CustomerEmailInlineEditCtrl customerEmailInlineEdit) {
		this.customerEmailInlineEditCtrl = customerEmailInlineEdit;
	}

	public CustTypePANMappingService getCustTypePANMappingService() {
		return custTypePANMappingService;
	}

	public void setCustTypePANMappingService(CustTypePANMappingService custTypePANMappingService) {
		this.custTypePANMappingService = custTypePANMappingService;
	}

	public void setdMSService(DMSService dMSService) {
		this.dMSService = dMSService;
	}

	public ExtendedFieldRender getExtendedDetails() {
		return extendedFieldCtrl.save(true);
	}

	public ExtendedFieldCtrl getExtendedFieldCtrl() {
		return extendedFieldCtrl;
	}

	public void setExtendedFieldCtrl(ExtendedFieldCtrl extendedFieldCtrl) {
		this.extendedFieldCtrl = extendedFieldCtrl;
	}

	public String getSubCategory() {
		if (this.subCategory.isVisible()) {
			return getComboboxValue(this.subCategory);
		}
		return "";
	}

	public MasterDefDAO getMasterDefDAO() {
		return masterDefDAO;
	}

	public void setMasterDefDAO(MasterDefDAO masterDefDAO) {
		this.masterDefDAO = masterDefDAO;
	}

	public CustomerExtLiabilityUploadDialogCtrl getCustomerExtLiabilityUploadDialogCtrl() {
		return customerExtLiabilityUploadDialogCtrl;
	}

	public void setCustomerExtLiabilityUploadDialogCtrl(
			CustomerExtLiabilityUploadDialogCtrl customerExtLiabilityUploadDialogCtrl) {
		this.customerExtLiabilityUploadDialogCtrl = customerExtLiabilityUploadDialogCtrl;
	}

	public void setGstDetailsList(List<GSTDetail> gstDetailsList) {
		this.gstDetailsList = gstDetailsList;
	}

	public List<GSTDetail> getGstDetailsList() {
		return gstDetailsList;
	}

}