/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */
/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CustomerDialogCtrl.java                                              * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  27-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.customermasters.customer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
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
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
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
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Department;
import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.model.systemmasters.EmpStsCode;
import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennant.backend.model.systemmasters.EmploymentType;
import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.model.systemmasters.SubSegment;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.DirectorDetailService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.collateral.collateralsetup.CollateralBasicDetailsCtrl;
import com.pennant.webui.customermasters.customer.model.CustomerRatinglistItemRenderer;
import com.pennant.webui.customermasters.customeremploymentdetail.model.CustomerEmploymentDetailListModelItemRenderer;
import com.pennant.webui.customermasters.directordetail.model.DirectorDetailListModelItemRenderer;
import com.pennant.webui.dedup.dedupparm.FetchCustomerDedupDetails;
import com.pennant.webui.dedup.dedupparm.FetchDedupDetails;
import com.pennant.webui.dedup.dedupparm.FetchFinCustomerDedupDetails;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.document.external.ExternalDocumentManager;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/Customer/customerDialog.zul file.
 */
public class CustomerDialogCtrl extends GFCBaseCtrl<CustomerDetails> {
	private static final long					serialVersionUID				= 9031340167587772517L;
	private static final Logger					logger							= Logger
			.getLogger(CustomerDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window							window_CustomerDialog;														// autowired

	protected Tabs								tabsIndexCenter;
	protected Tabpanels							tabpanelsBoxIndexCenter;

	protected North								north;																		// autowired
	protected South								south;																		// autowired
	protected Textbox							custCIF;																	// autowired
	protected Textbox							custCoreBank;																// autowired
	protected Combobox							custSalutationCode;															// autowired
	protected Textbox							custShrtName;																// autowired
	protected Textbox							custFirstName;																// autowired
	protected Textbox							custMiddleName;																// autowired
	protected Textbox							custLastName;																// autowired
	protected Textbox							custArabicName;																// autowired
	protected Space								space_CustArabicName;														// autowired
	protected Textbox							motherMaidenName;															// autowired
	protected ExtendedCombobox					custLng;																	// autowired
	protected Textbox							custSts;																	// autowired
	protected ExtendedCombobox					custSector;																	// autowired
	protected ExtendedCombobox					custIndustry;																// autowired
	protected ExtendedCombobox					custSegment;																// autowired
	protected ExtendedCombobox					custCOB;																	// autowired
	protected ExtendedCombobox					custNationality;															// autowired
	protected Combobox							custMaritalSts;																// autowired
	protected Datebox							custDOB;																	// autowired
	protected Combobox							custGenderCode;																// autowired
	protected Intbox							noOfDependents;																// autowired
	protected ExtendedCombobox					target;																		// autowired
	protected ExtendedCombobox					custCtgCode;																// autowired
	protected Checkbox							salaryTransferred;															// autowiredOdoed
	protected ExtendedCombobox					custDftBranch;																// autowired
	protected ExtendedCombobox					custTypeCode;																// autowired
	protected ExtendedCombobox					custBaseCcy;																// autowired
	protected Checkbox							salariedCustomer;															// autowired
	protected ExtendedCombobox					custRO1;																	// autowired
	protected Uppercasebox						eidNumber;																	// autowired
	protected Label								label_CustomerDialog_EIDNumber;												// autowired
	protected Textbox							custTradeLicenceNum;														// autowired
	protected Textbox							custRelatedParty;															// autowired
	protected ExtendedCombobox					custGroupId;																// autowired
	protected Checkbox							custIsStaff;																// autowired
	protected Textbox							custStaffID;																// autowired
	protected Textbox							custDSACode;																// autowired
	protected ExtendedCombobox					custDSADept;																// autowired
	protected ExtendedCombobox					custRiskCountry;															// autowired
	protected ExtendedCombobox					custParentCountry;															// autowired
	protected ExtendedCombobox					custSubSector;																// autowired
	protected ExtendedCombobox					custSubSegment;																// autowired

	/** Customer Employer Fields **/
	protected ExtendedCombobox					empStatus;																	// autowired
	protected ExtendedCombobox					empSector;																	// autowired
	protected ExtendedCombobox					profession;																	// autowired
	protected ExtendedCombobox					empName;																	// autowired
	protected Hbox								hbox_empNameOther;															// autowired
	protected Label								label_empNameOther;															// autowired
	protected Textbox							empNameOther;																// autowired
	protected Datebox							empFrom;																	// autowired
	protected ExtendedCombobox					empDesg;																	// autowired
	protected ExtendedCombobox					empDept;																	// autowired
	protected CurrencyBox						monthlyIncome;																// autowired
	protected ExtendedCombobox					otherIncome;																// autowired
	protected CurrencyBox						additionalIncome;															// autowired
	protected Label								age;																		// autowired
	protected Label								exp;																		// autowired

	protected Label								label_CustomerDialog_CustShrtName;
	protected Label								label_CustomerDialog_SalaryTransfered;
	protected Label								label_CustomerDialog_EmpSector;
	protected Label								label_CustomerDialog_Profession;
	protected Label								label_CustomerDialog_EmpFrom;
	protected Label								label_CustomerDialog_MonthlyIncome;
	protected Label								label_CustomerDialog_CustCOB;
	protected Label								label_CustomerDialog_Target;
	protected Label								label_ArabicName;
	protected Label								label_CustSubSegment;
	protected Row								row_FirstMiddleName;
	protected Row								row_LastName;
	protected Row								row_GenderSalutation;
	protected Row								row_MartialDependents;
	protected Row								row_EmpName;
	protected Row								row_DesgDept;
	protected Row								row_custTradeLicenceNum;
	protected Row								rowCustEmpSts;
	protected Row								rowCustCRCPR;
	protected Row								row_party_segment;
	protected Row								row_custStatus;
	protected Row								row_custStaff;
	protected Row								row_custDSA;
	protected Row								row_custCountry;
	protected Row								row_custSub;
	protected Groupbox							gp_CustEmployeeDetails;
	protected Hbox								hbox_SalariedCustomer;
	protected Space								space_CustShrtName;

	protected Tab								basicDetails;
	protected Tab								tabkYCDetails;
	protected Tab								tabbankDetails;

	protected Button							btnNew_CustomerDocuments;
	protected Listbox							listBoxCustomerDocuments;
	private List<CustomerDocument>				customerDocumentDetailList		= new ArrayList<CustomerDocument>();

	protected Button							btnNew_CustomerAddress;
	protected Listbox							listBoxCustomerAddress;
	private List<CustomerAddres>				customerAddressDetailList		= new ArrayList<CustomerAddres>();

	protected Button							btnNew_CustomerPhoneNumber;
	protected Listbox							listBoxCustomerPhoneNumbers;
	private List<CustomerPhoneNumber>			customerPhoneNumberDetailList	= new ArrayList<CustomerPhoneNumber>();
	protected Listheader						listheader_CustPhone_RecordStatus;
	protected Listheader						listheader_CustPhone_RecordType;
	private List<CustomerPhoneNumber>			phoneNumberList					= new ArrayList<CustomerPhoneNumber>();

	protected Button							btnNew_CustomerEmail;
	protected Listbox							listBoxCustomerEmails;
	private List<CustomerEMail>					customerEmailDetailList			= new ArrayList<CustomerEMail>();

	protected Button							btnNew_BankInformation;
	protected Groupbox							gp_BankInformationDetail;
	protected Listbox							listBoxCustomerBankInformation;
	private List<CustomerBankInfo>				customerBankInfoDetailList		= new ArrayList<CustomerBankInfo>();

	protected Button							btnNew_ChequeInformation;
	protected Groupbox							gp_ChequeInformation;
	protected Listbox							listBoxCustomerChequeInformation;
	private List<CustomerChequeInfo>			customerChequeInfoDetailList	= new ArrayList<CustomerChequeInfo>();

	protected Listbox							listBoxCustomerFinExposure;

	protected Button							btnNew_ExternalLiability;
	protected Groupbox							gp_ExternalLiability;
	protected Listbox							listBoxCustomerExternalLiability;
	private List<CustomerExtLiability>			customerExtLiabilityDetailList	= new ArrayList<CustomerExtLiability>();

	protected Listheader						listheader_JointCust;

	// Customer ratings List
	protected Button							btnNew_CustomerRatings;
	protected Listbox							listBoxCustomerRating;
	protected Listheader						listheader_CustRating_RecordStatus;
	protected Listheader						listheader_CustRating_RecordType;
	private List<CustomerRating>				ratingsList						= new ArrayList<CustomerRating>();

	// Customer Employment List
	protected Row								row_EmploymentDetails;
	protected Button							btnNew_CustomerEmploymentDetail;
	protected Listbox							listBoxCustomerEmploymentDetail;
	protected Listheader						listheader_CustEmp_RecordStatus;
	protected Listheader						listheader_CustEmp_RecordType;
	private List<CustomerEmploymentDetail>		customerEmploymentDetailList	= new ArrayList<CustomerEmploymentDetail>();
	private List<CustomerEmploymentDetail>		oldVar_EmploymentDetailsList	= new ArrayList<CustomerEmploymentDetail>();

	// Customer Income details List
	protected Button							btnNew_CustomerIncome;
	protected Listbox							listBoxCustomerIncome;
	protected Listheader						listheader_CustInc_RecordStatus;
	protected Listheader						listheader_CustInc_RecordType;
	private List<CustomerIncome>				incomeList						= new ArrayList<CustomerIncome>();

	private transient String					oldVar_empStatus;
	private CustomerDetails						customerDetails;															// overhanded per param
	private transient CustomerListCtrl			customerListCtrl;															// overhanded per param

	private transient boolean					validationOn;

	protected Groupbox							gb_keyDetails;																// autowired
	protected Groupbox							gb_incomeDetails;															// autowired
	protected Groupbox							gb_rating;																	// autowired
	protected Groupbox							gb_directorDetails;															// autowired

	protected Groupbox							gb_Action;
	protected Groupbox							gb_statusDetails;
	String										parms[]							= new String[4];

	private int									countRows						= PennantConstants.listGridSize;
	protected Tab								tabfinancial;

	protected Label								label_CustomerDialog_CustDOB;

	// Declaration of Service(s) & DAO(s)
	private transient CustomerDetailsService	customerDetailsService;
	private transient DedupParmService			dedupParmService;
	private int									ccyFormatter					= 0;
	private int									old_ccyFormatter				= 0;
	private String								moduleType						= "";
	protected Div								divKeyDetails;
	protected Grid								grid_KYCDetails;
	protected Grid								grid_BankDetails;

	private boolean								isRetailCustomer				= false;
	private boolean								isSMECustomer					= false;
	private String								empAlocType						= "";

	protected Tab								directorDetails;
	// Customer Directory details List
	protected Button							btnNew_DirectorDetail;
	protected Listbox							listBoxCustomerDirectory;
	protected Listheader						listheader_CustDirector_RecordStatus;
	protected Listheader						listheader_CustDirector_RecordType;
	private List<DirectorDetail>				directorList					= new ArrayList<DirectorDetail>();
	protected Label								label_CustomerDialog_CustNationality;
	private transient DirectorDetailService		directorDetailService;
	Date										appDate							= DateUtility.getAppDate();
	Date										startDate						= SysParamUtil.getValueAsDate("APP_DFT_START_DATE");

	private String								empStatus_Temp					= "";
	private String								empName_Temp					= "";
	private String								custBaseCcy_Temp				= "";

	private FinanceDetail						financedetail;
	private Object								financeMainDialogCtrl;
	private Object								promotionPickListCtrl;
	private Tabpanel							panel							= null;
	private Groupbox							groupbox						= null;
	private boolean								newFinance						= false;
	private boolean								isFinanceProcess				= false;
	private boolean								isNotFinanceProcess				= false;
	private boolean								isEnqProcess					= false;
	private boolean								isPromotionPickProcess			= false;

	private FinBasicDetailsCtrl					finBasicDetailsCtrl;
	private CollateralBasicDetailsCtrl			collateralBasicDetailsCtrl;
	protected Groupbox							finBasicdetails;

	public boolean								validateAllDetails				= true;
	public boolean								validateCustDocs				= true;
	private String								moduleName;
	private List<CustomerBankInfo>				CustomerBankInfoList;

	//Extended fields
	private ExtendedFieldCtrl					extendedFieldCtrl				= null;
	private ExternalDocumentManager				externalDocumentManager			= null;

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
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CustomerDialog(Event event) throws Exception {
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
			} else {
				setCustomerDetails(null);
			}
			if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((Object) arguments.get("financeMainDialogCtrl"));
				this.window_CustomerDialog.setTitle("");
				newFinance = true;
			}
			if (arguments.containsKey("promotionPickListCtrl")) {
				setPromotionPickListCtrl((Object) arguments.get("promotionPickListCtrl"));
				this.window_CustomerDialog.setTitle("");
				isPromotionPickProcess = true;
			}
			if (arguments.containsKey("isEnqProcess")) {
				isEnqProcess = (Boolean) arguments.get("isEnqProcess");
				this.moduleType = PennantConstants.MODULETYPE_ENQ;
			}
			if (arguments.containsKey("isNotFinanceProcess")) {
				isNotFinanceProcess = (Boolean) arguments.get("isNotFinanceProcess");
			}

			if (arguments.containsKey("moduleName")) {
				this.moduleName = (String) arguments.get("moduleName");
			}
			if (arguments.containsKey("financeDetail")) {
				setFinancedetail((FinanceDetail) arguments.get("financeDetail"));
				isFinanceProcess = true;
				if (getFinancedetail() != null) {
					setCustomerDetails(getFinancedetail().getCustomerDetails());
					FinanceMain financeMain = getFinancedetail().getFinScheduleData().getFinanceMain();
					getFinancedetail().getCustomerDetails().getCustomer().setWorkflowId(financeMain.getWorkflowId());
				}
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

			if (isFinanceProcess || isEnqProcess) {
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
			if (!isNotFinanceProcess) {
				doCheckRights();
			}

			// set Field Properties
			doSetFieldProperties();
			// Height Setting
			if ((isFinanceProcess || isNotFinanceProcess) && !isPromotionPickProcess) {
				int divKycHeight = this.borderLayoutHeight - 80;
				int semiBorderlayoutHeights = divKycHeight / 2;
				if (isRetailCustomer) {
					this.divKeyDetails.setHeight(borderLayoutHeight - 130 + "px");
					this.grid_KYCDetails.setHeight(borderLayoutHeight + "px");
					this.listBoxCustomerEmploymentDetail.setHeight(semiBorderlayoutHeights - 140 + "px");
					this.listBoxCustomerDocuments.setHeight(semiBorderlayoutHeights - 60 + "px");
					this.listBoxCustomerAddress.setHeight(semiBorderlayoutHeights - 90 + "px");
					this.listBoxCustomerPhoneNumbers.setHeight(semiBorderlayoutHeights - 90 + "px");
					this.listBoxCustomerEmails.setHeight(semiBorderlayoutHeights - 90 + "px");
				} else {
					this.divKeyDetails.setHeight(borderLayoutHeight - 130 + "px");
					this.listBoxCustomerRating.setHeight(semiBorderlayoutHeights - 130 + "px");
					this.grid_KYCDetails.setHeight(borderLayoutHeight - 220 + "px");
					this.listBoxCustomerDocuments.setHeight(semiBorderlayoutHeights - 125 + "px");
					this.listBoxCustomerAddress.setHeight(semiBorderlayoutHeights - 125 + "px");
					this.listBoxCustomerPhoneNumbers.setHeight(semiBorderlayoutHeights - 125 + "px");
					this.listBoxCustomerEmails.setHeight(semiBorderlayoutHeights - 125 + "px");
				}
				this.gb_incomeDetails.setHeight(borderLayoutHeight - 220 + "px");
				this.listBoxCustomerIncome.setHeight(borderLayoutHeight - 220 + "px");
				this.gb_directorDetails.setHeight(borderLayoutHeight - 220 + "px");
				this.listBoxCustomerDirectory.setHeight(borderLayoutHeight - 220 + "px");
				this.grid_BankDetails.setHeight(borderLayoutHeight - 220 + "px");
				this.listBoxCustomerBankInformation.setHeight(semiBorderlayoutHeights - 125 + "px");
				this.listBoxCustomerChequeInformation.setHeight(semiBorderlayoutHeights - 125 + "px");
				this.listBoxCustomerFinExposure.setHeight(semiBorderlayoutHeights - 125 + "px");
				this.listBoxCustomerExternalLiability.setHeight(semiBorderlayoutHeights - 125 + "px");
			} else {
				int divKycHeight = this.borderLayoutHeight - 80;
				int borderlayoutHeights = divKycHeight / 2;
				if (isRetailCustomer) {
					this.divKeyDetails.setHeight(borderLayoutHeight - 130 + "px");
				} else {
					this.divKeyDetails.setHeight(borderLayoutHeight - 50 + "px");
					this.listBoxCustomerRating.setHeight(this.borderLayoutHeight - 330 + "px");
				}
				this.listBoxCustomerEmploymentDetail
				.setHeight(borderlayoutHeights - (isRetailCustomer ? 100 : 10) + "px");
				this.listBoxCustomerDocuments.setHeight(borderlayoutHeights - (isRetailCustomer ? 100 : 10) + "px");
				this.listBoxCustomerAddress.setHeight(borderlayoutHeights - (isRetailCustomer ? 145 : 90) + "px");
				this.listBoxCustomerPhoneNumbers.setHeight(borderlayoutHeights - (isRetailCustomer ? 145 : 90) + "px");
				this.listBoxCustomerEmails.setHeight(borderlayoutHeights - (isRetailCustomer ? 145 : 90) + "px");

				this.gb_incomeDetails.setHeight(borderLayoutHeight - 40 + "px");
				this.listBoxCustomerIncome.setHeight(borderLayoutHeight - 40 + "px");
				this.gb_directorDetails.setHeight(borderLayoutHeight - 40 + "px");
				this.listBoxCustomerDirectory.setHeight(borderLayoutHeight - 40 + "px");

				this.listBoxCustomerBankInformation.setHeight(borderlayoutHeights - 130 + "px");
				this.listBoxCustomerChequeInformation.setHeight(borderlayoutHeights - 130 + "px");
				this.listBoxCustomerFinExposure.setHeight(borderlayoutHeights - 130 + "px");
				this.listBoxCustomerExternalLiability.setHeight(borderlayoutHeights - 130 + "px");
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
			this.custShrtName.setMaxlength(50);
		}
		if(isSMECustomer){

		}
		this.custFirstName.setMaxlength(50);
		this.custMiddleName.setMaxlength(50);
		this.custLastName.setMaxlength(50);
		this.custArabicName.setMaxlength(50);
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

		this.custSector.setMaxlength(8);
		this.custSector.setTextBoxWidth(121);
		this.custSector.setMandatoryStyle(true);
		this.custSector.setModuleName("Sector");
		this.custSector.setValueColumn("SectorCode");
		this.custSector.setDescColumn("SectorDesc");
		this.custSector.setValidateColumns(new String[] { "SectorCode" });

		this.custIndustry.setMaxlength(8);
		this.custIndustry.setTextBoxWidth(121);
		this.custIndustry.setMandatoryStyle(true);
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

		this.custRO1.setMaxlength(8);
		this.custRO1.setTextBoxWidth(121);
		this.custRO1.setMandatoryStyle(true);
		this.custRO1.setModuleName("RelationshipOfficer");
		this.custRO1.setValueColumn("ROfficerCode");
		this.custRO1.setDescColumn("ROfficerDesc");
		this.custRO1.setValidateColumns(new String[] { "ROfficerCode" });

		this.target.setMaxlength(8);
		this.target.setTextBoxWidth(121);
		this.target.setMandatoryStyle(false);
		this.target.setModuleName("TargetDetail");
		this.target.setValueColumn("TargetCode");
		this.target.setDescColumn("TargetDesc");
		this.target.setValidateColumns(new String[] { "TargetCode" });

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
		this.profession.setMandatoryStyle(true);
		this.profession.setModuleName("Profession");
		this.profession.setValueColumn("ProfessionCode");
		this.profession.setDescColumn("ProfessionDesc");
		this.profession.setValidateColumns(new String[] { "ProfessionCode" });

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
		this.custGroupId.setValueColumn("CustGrpID");
		this.custGroupId.setDescColumn("CustGrpDesc");
		this.custGroupId.setValidateColumns(new String[] { "CustGrpID" });
		this.custGroupId.setFilters(new Filter[] { new Filter("CustGrpIsActive", "1", Filter.OP_EQUAL) });

		this.custStaffID.setMaxlength(8);
		this.custDSACode.setMaxlength(8);

		this.custDSADept.setMaxlength(8);
		this.custDSADept.setMandatoryStyle(false);
		this.custDSADept.setModuleName("Department");
		this.custDSADept.setValueColumn("DeptCode");
		this.custDSADept.setDescColumn("DeptDesc");
		this.custDSADept.setValidateColumns(new String[] { "DeptCode" });
		this.custDSADept.setFilters(new Filter[] { new Filter("DeptIsActive", "1", Filter.OP_EQUAL) });

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
		if (isWorkFlowEnabled()) {
			this.gb_Action.setVisible(true);
		} else {
			this.gb_Action.setVisible(false);
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
		validateCustDocs = getUserWorkspace().isAllowed("button_CustomerDialog_NewCustomerDocuments");
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws CustomerNotFoundException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException, ParseException {
		logger.debug("Entering" + event.toString());
		try {
			doSave();
		} catch (InterfaceException e) {
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
		MessageUtil.showHelpWindow(event, window_CustomerDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws CustomerNotFoundException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		try {
			doDelete();
		} catch (InterfaceException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Entering" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Entering" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
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
		doWriteBeanToComponents(this.customerDetails.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomer
	 *            Customer
	 */

	public void doWriteBeanToComponents(CustomerDetails aCustomerDetails) {
		logger.debug("Entering");
		Customer aCustomer = aCustomerDetails.getCustomer();
		setComboBoxValue(this.custGenderCode, aCustomer.getCustGenderCode(), aCustomer.getLovDescCustGenderCodeName());
		setComboBoxValue(this.custSalutationCode, aCustomer.getCustSalutationCode(),
				aCustomer.getLovDescCustSalutationCodeName());
		setComboBoxValue(this.custMaritalSts, aCustomer.getCustMaritalSts(), aCustomer.getLovDescCustMaritalStsName());
		this.target.setValue(aCustomer.getCustAddlVar82());
		this.custCIF.setValue(StringUtils.trimToEmpty(aCustomer.getCustCIF()));
		this.custCoreBank.setValue(aCustomer.getCustCoreBank());
		this.custFirstName.setValue(StringUtils.trimToEmpty(aCustomer.getCustFName()));
		this.custMiddleName.setValue(StringUtils.trimToEmpty(aCustomer.getCustMName()));
		this.custLastName.setValue(StringUtils.trimToEmpty(aCustomer.getCustLName()));
		if (isRetailCustomer) {
			this.custShrtName.setValue(PennantApplicationUtil.getFullName(aCustomer.getCustFName(),
					aCustomer.getCustMName(), aCustomer.getCustLName()));
		} else {
			this.custShrtName.setValue(StringUtils.trimToEmpty(aCustomer.getCustShrtName()));
		}
		this.custArabicName.setValue(StringUtils.trimToEmpty(aCustomer.getCustShrtNameLclLng()));
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
		this.custRO1.setValue(aCustomer.getCustRO1());
		this.custTradeLicenceNum.setValue(aCustomer.getCustTradeLicenceNum());
		this.custRelatedParty.setValue(StringUtils.trimToEmpty(aCustomer.getCustAddlVar83()));
		this.custGroupId.setValue(aCustomer.getCustGroupID() == 0 ? "" : String.valueOf(aCustomer.getCustGroupID()));
		this.custIsStaff.setChecked(aCustomer.isCustIsStaff());
		this.custDSACode.setValue(aCustomer.getCustDSA());
		this.custDSADept.setValue(aCustomer.getCustDSADept());
		this.custRiskCountry.setValue(aCustomer.getCustRiskCountry());
		this.custParentCountry.setValue(aCustomer.getCustParentCountry());

		this.custDSADept.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustDSADeptName()));
		this.custParentCountry.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustParentCountryName()));
		this.custRiskCountry.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustRiskCountryName()));
		this.custGroupId.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDesccustGroupIDName()));
		this.custCtgCode.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustCtgCodeName()));
		this.custDftBranch.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustDftBranchName()));
		this.custTypeCode.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustTypeCodeName()));
		this.custBaseCcy.setDescription(CurrencyUtil.getCcyDesc(aCustomer.getCustBaseCcy()));
		this.custNationality.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustNationalityName()));
		this.custLng.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustLngName()));
		this.custSector.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustSectorName()));
		this.custIndustry.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustIndustryName()));
		this.custCOB.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustCOBName()));
		this.custRO1.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustRO1Name()));
		this.target.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescTargetName()));
		this.salariedCustomer.setChecked(aCustomer.isSalariedCustomer());

		doSetSegmentCode(aCustomer.getCustTypeCode());
		aCustomer.getCustCtgCode();
		doSetCustTypeFilters(aCustomer.getLovDescCustCtgType());
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
		this.monthlyIncome.setValue(PennantAppUtil.formateAmount(custEmployeeDetail.getMonthlyIncome(), ccyFormatter));
		this.otherIncome.setValue(custEmployeeDetail.getOtherIncome());
		this.otherIncome.setDescription(custEmployeeDetail.getLovDescOtherIncome());
		this.additionalIncome
		.setValue(PennantAppUtil.formateAmount(custEmployeeDetail.getAdditionalIncome(), ccyFormatter));
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

		// Extended Field Details
		appendExtendedFieldDetails(aCustomerDetails);

		if (!StringUtils.equals(PennantConstants.PFF_CUSTCTG_INDIV, customerDetails.getCustomer().getCustCtgCode())) {
			doFillCustomerDirectory(aCustomerDetails.getCustomerDirectorList());
			doSetShareHoldersDesignationCode(aCustomerDetails.getCustomerDirectorList());
		}

		processDateDiff(this.custDOB.getValue(), this.age);
		processDateDiff(this.empFrom.getValue(), this.exp);

		this.recordStatus.setValue(aCustomer.getRecordStatus());
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

			this.extendedFieldCtrl.createTab(tabsIndexCenter, tabpanelsBoxIndexCenter);
			aCustomerDetails.setExtendedFieldHeader(extendedFieldHeader);
			aCustomerDetails.setExtendedFieldRender(extendedFieldRender);

			if (aCustomerDetails.getBefImage() != null) {
				aCustomerDetails.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				aCustomerDetails.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}
			extendedFieldCtrl.setCcyFormat(2);
			extendedFieldCtrl.setReadOnly(isReadOnly("CustomerDialog_custFirstName"));
			extendedFieldCtrl.setWindow(this.window_CustomerDialog);
			extendedFieldCtrl.render();
		} catch (Exception e) {
			logger.error("Exception", e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomer
	 * @throws ParseException
	 */
	public void doWriteComponentsToBean(CustomerDetails aCustomerDetails, Tab custTab) throws ParseException {
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
			aCustomer.setCustShrtNameLclLng(this.custArabicName.getValue());
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
			aCustomer.setLovDescCustRO1Name(this.custRO1.getDescription());
			aCustomer.setCustRO1(StringUtils.trimToNull(this.custRO1.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setCustDOB(this.custDOB.getValue());
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
			if (isRetailCustomer) {
				aCustomer.setCustCRCPR(PennantApplicationUtil.unFormatEIDNumber(this.eidNumber.getValue()));
			} else if (this.eidNumber.getValue().isEmpty()) {
				aCustomer.setCustCRCPR(null);
			} else {
				aCustomer.setCustCRCPR(this.eidNumber.getValue());
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
			aCustomer.setCustGroupID(StringUtils.isEmpty(this.custGroupId.getValidatedValue()) ? 0
					: Long.parseLong(this.custGroupId.getValue()));
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

		this.basicDetails.setSelected(true);
		showErrorDetails(wve, custTab, basicDetails);

		doSetValidation();
		doSetLOVValidation();
		// Below nullified fields are not using in screen
		//aCustomer.setCustSubSector(null);
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
							PennantAppUtil.unFormateAmount(this.monthlyIncome.getActualValue(), ccyFormatter));
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
					custEmployeeDetail.setAdditionalIncome(
							PennantAppUtil.unFormateAmount(this.additionalIncome.getActualValue(), ccyFormatter));
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
		aCustomer.setPhoneNumber(getMobileNumber());

		if (StringUtils.isBlank(aCustomer.getCustSourceID())) {
			aCustomer.setCustSourceID(App.CODE);
		}

		// Extended Field validations
		extendedFieldCtrl.setParentTab(custTab);
		if(aCustomerDetails.getExtendedFieldHeader() != null) {
			aCustomerDetails.setExtendedFieldRender(extendedFieldCtrl.save());
		}

		// Set KYC details
		Cloner cloner = new Cloner();
		aCustomerDetails.setCustomer(cloner.deepClone(aCustomer));
		aCustomerDetails.setCustEmployeeDetail(cloner.deepClone(custEmployeeDetail));
		aCustomerDetails.setEmploymentDetailsList(cloner.deepClone(this.customerEmploymentDetailList));
		aCustomerDetails.setCustomerIncomeList(cloner.deepClone(this.incomeList));
		aCustomerDetails.setRatingsList(cloner.deepClone(this.ratingsList));
		aCustomerDetails.setCustomerDocumentsList(cloner.deepClone(this.customerDocumentDetailList));
		aCustomerDetails.setAddressList(cloner.deepClone(this.customerAddressDetailList));
		aCustomerDetails.setCustomerPhoneNumList(cloner.deepClone(this.customerPhoneNumberDetailList));
		aCustomerDetails.setCustomerEMailList(cloner.deepClone(this.customerEmailDetailList));
		// Set Banking details
		aCustomerDetails.setCustomerBankInfoList(cloner.deepClone(this.customerBankInfoDetailList));
		aCustomerDetails.setCustomerChequeInfoList(cloner.deepClone(this.customerChequeInfoDetailList));
		aCustomerDetails.setCustomerExtLiabilityList(cloner.deepClone(this.customerExtLiabilityDetailList));
		if (this.directorDetails.isVisible()) {
			aCustomerDetails.setCustomerDirectorList(this.directorList);
		}
		
		logger.debug("Leaving");
	}

	private BigDecimal getCustTotExpense() {
		logger.debug("Entering");
		BigDecimal custTotExpense = BigDecimal.ZERO;
		if (this.customerExtLiabilityDetailList != null && !this.customerExtLiabilityDetailList.isEmpty()) {
			for (CustomerExtLiability cusExtLiability : this.customerExtLiabilityDetailList) {
				if (!isDeleteRecord(cusExtLiability.getRecordType())) {
					custTotExpense = custTotExpense.add(cusExtLiability.getInstalmentAmount());
				}
			}
		}
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
							custTotIncomeExp = custTotIncomeExp.add(custIncome.getCustIncome());
						}
					} else {
						if (StringUtils.equals(PennantConstants.EXPENSE, custIncome.getIncomeExpense())) {
							custTotIncomeExp = custTotIncomeExp.add(custIncome.getCustIncome());
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
				if (PennantConstants.PHONETYPE_MOBILE.equalsIgnoreCase(phoneNumber.getPhoneTypeCode())) {
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
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab parentTab, Tab childTab) {
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
	 * @throws Exception
	 */
	public void doShowDialog(CustomerDetails aCustomerDetails) throws Exception {
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
				this.btnDelete.setVisible(true);
			}
			doEdit();
		}
		this.custCIF.focus();

		try {
			doWriteBeanToComponents(aCustomerDetails);
			doResetFeeVariables();
			doSetCategoryProperties();
			doCheckEnquiry();

			this.btnCancel.setVisible(false);
			if (isFinanceProcess || isNotFinanceProcess || isEnqProcess) {
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

	private void doSetCategoryProperties() {
		logger.debug("Entering");
		if (isRetailCustomer) {
			this.row_FirstMiddleName.setVisible(true);
			this.row_LastName.setVisible(true);
			this.row_GenderSalutation.setVisible(true);
			this.row_MartialDependents.setVisible(true);
			this.row_custDSA.setVisible(true);
			this.label_CustSubSegment.setVisible(true);
			this.custSubSegment.setVisible(true);
			this.row_custStaff.setVisible(true);
			this.row_custCountry.setVisible(true);
			this.hbox_SalariedCustomer.setVisible(true);
			if (ImplementationConstants.ALLOW_MULTIPLE_EMPLOYMENTS) {
				this.row_EmploymentDetails.setVisible(true);
				this.gp_CustEmployeeDetails.setVisible(false);
			} else {
				this.row_EmploymentDetails.setVisible(false);
				this.gp_CustEmployeeDetails.setVisible(true);
			}
			this.space_CustShrtName.setSclass("");
			this.label_CustomerDialog_SalaryTransfered.setVisible(true);
			this.label_CustomerDialog_EIDNumber.setValue(Labels.getLabel("label_CoreCustomerDialog_EIDNumber.value"));
			this.label_CustomerDialog_CustShrtName.setValue(Labels.getLabel("label_CustomerDialog_CustShrtName.value"));
			this.label_CustomerDialog_CustCOB.setValue(Labels.getLabel("label_CustomerDialog_CustCOB.value"));
			this.directorDetails.setVisible(false);
			this.gb_rating.setVisible(false);
			this.label_ArabicName.setVisible(true);
			this.space_CustArabicName.setSclass("");
			this.custArabicName.setVisible(true);
			this.tabfinancial.setVisible(ImplementationConstants.ALLOW_CUSTOMER_INCOMES);
			this.row_party_segment.setVisible(true);
			this.row_custStatus.setVisible(true);
			this.label_CustomerDialog_Target.setVisible(true);
			this.target.setVisible(true);
			this.target.setMandatoryStyle(false);
		} else {
			this.row_FirstMiddleName.setVisible(false);
			this.row_LastName.setVisible(false);
			this.row_GenderSalutation.setVisible(false);
			this.row_MartialDependents.setVisible(false);
			this.row_custDSA.setVisible(false);
			this.row_custStaff.setVisible(false);
			this.label_CustSubSegment.setVisible(false);
			this.custSubSegment.setVisible(false);
			this.row_custCountry.setVisible(false);
			this.hbox_SalariedCustomer.setVisible(false);
			this.space_CustShrtName.setSclass(PennantConstants.mandateSclass);
			this.label_CustomerDialog_SalaryTransfered.setVisible(false);
			this.label_CustomerDialog_CustShrtName.setValue(Labels.getLabel("label_CustomerDialog_CustomerName.value"));
			this.label_CustomerDialog_CustDOB
			.setValue(Labels.getLabel("label_CustomerDialog_CustDateOfIncorporation.value"));
			this.label_CustomerDialog_EIDNumber
			.setValue(Labels.getLabel("label_CoreCustomerDialog_TradeLicenseNumber.value"));
			this.label_CustomerDialog_CustCOB.setValue(Labels.getLabel("label_CustomerDialog_CustCOI.value"));
			this.gb_rating.setVisible(getUserWorkspace().isAllowed("CustomerDialog_ShowCustomerRatings"));
			this.gp_CustEmployeeDetails.setVisible(false);
			this.row_EmploymentDetails.setVisible(false);
			this.label_ArabicName.setVisible(false);
			this.space_CustArabicName.setSclass("");
			this.custArabicName.setVisible(false);
			this.directorDetails.setVisible(ImplementationConstants.ALLOW_CUSTOMER_SHAREHOLDERS);
			this.row_party_segment.setVisible(false);
			this.row_custStatus.setVisible(false);
			this.label_CustomerDialog_Target.setVisible(false);
			this.target.setVisible(false);
			this.target.setMandatoryStyle(false);
			//this.custRO1.setMandatoryStyle(false);
		}

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
			// for eid read only in enquiry
			this.eidNumber.setReadonly(true);
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
			if (StringUtils.isEmpty(this.custArabicName.getValue())) {
				this.custArabicName.setReadonly(isReadOnly("CustomerDialog_custArabicName"));
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
					this.custSegment.setMandatoryStyle(true);
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

			if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
				if (isRetailCustomer) {
					this.eidNumber.setConstraint(
							new PTStringValidator(Labels.getLabel("label_CustomerDialog_TradeLicenseNumber.value"),
									PennantRegularExpressions.REGEX_EIDNUMBER, false));

				} else {
					this.eidNumber.setConstraint(
							new PTStringValidator(Labels.getLabel("label_CustomerDialog_TradeLicenseNumber.value"),
									PennantRegularExpressions.REGEX_TRADELICENSE, false));
				}
			} else {
				this.eidNumber.setConstraint(
						new PTStringValidator(Labels.getLabel("label_CustomerDialog_TradeLicenseNumber.value"),
								PennantRegularExpressions.REGEX_PANNUMBER, false));

			}

		}

		// below fields are conditional mandatory

		if (isRetailCustomer) {
			if (!this.custFirstName.isReadonly()) {
				this.custFirstName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustFirstName.value"),
								PennantRegularExpressions.REGEX_CUST_NAME, isMandValidate));
				this.custFirstName.setConstraint(new PTStringValidator(Labels
						.getLabel("label_CustomerDialog_CustFirstName.value"),
						PennantRegularExpressions.REGEX_ACC_HOLDER_NAME, isMandValidate));
			}
			if (!this.custMiddleName.isReadonly()) {
				this.custMiddleName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustMiddleName.value"),
								PennantRegularExpressions.REGEX_CUST_NAME, false));
				this.custMiddleName.setConstraint(new PTStringValidator(Labels
						.getLabel("label_CustomerDialog_CustMiddleName.value"),
						PennantRegularExpressions.REGEX_ACC_HOLDER_NAME, false));
			}
			if (!this.custLastName.isReadonly()) {
				this.custLastName
				.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustLastName.value"),
						PennantRegularExpressions.REGEX_CUST_NAME, isMandValidate));
				this.custLastName.setConstraint(new PTStringValidator(Labels
						.getLabel("label_CustomerDialog_CustLastName.value"),
						PennantRegularExpressions.REGEX_ACC_HOLDER_NAME, isMandValidate));
			}
			if (!this.motherMaidenName.isReadonly()) {
				this.motherMaidenName.setConstraint(
						new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustMotherMaiden.value"),
								PennantRegularExpressions.REGEX_CUST_NAME, isMandValidate));
				this.motherMaidenName.setConstraint(new PTStringValidator(Labels
						.getLabel("label_CustomerDialog_CustMotherMaiden.value"),
						PennantRegularExpressions.REGEX_ACC_HOLDER_NAME, isMandValidate));
			}
		} else {
			if (!this.custShrtName.isReadonly()) {
				this.custShrtName
				.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustomerName.value"),
						PennantRegularExpressions.REGEX_ACC_HOLDER_NAME, true));
			}
		}
		if (!this.custArabicName.isReadonly()) {
			this.custArabicName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustArabicName.value"), null, false));
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
		if (!this.custSector.isReadonly()) {
			this.custSector.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustSector.value"), null, true, true));
		}
		if (!this.custIndustry.isReadonly()) {
			this.custIndustry.setConstraint(new PTStringValidator(
					Labels.getLabel("label_CustomerDialog_CustIndustry.value"), null, true, true));
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
		this.custArabicName.setConstraint("");
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
		this.custArabicName.setErrorMessage("");
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
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getCustomerListCtrl().search();
	}

	/**
	 * Deletes a Customer object from database.<br>
	 * 
	 * @throws InterruptedException
	 * @throws CustomerNotFoundException
	 */
	private void doDelete() throws InterruptedException, InterfaceException {
		logger.debug("Entering");
		Cloner cloner = new Cloner();
		CustomerDetails aCustomerDetails = new CustomerDetails();
		aCustomerDetails = cloner.deepClone(getCustomerDetails());
		String tranType = PennantConstants.TRAN_WF;
		Customer aCustomer = aCustomerDetails.getCustomer();
		CustEmployeeDetail custEmployeeDetail = aCustomerDetails.getCustEmployeeDetail();

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_CustomerDialog_CustCIF.value") + " : " + aCustomer.getCustCIF();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
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
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	@SuppressWarnings("unused")
	private void doEdit() {
		logger.debug("Entering");

		if (PennantConstants.MODULETYPE_ENQ.equals(moduleType) || isNotFinanceProcess) {
			doReadOnly();
		} else {
			this.custCoreBank.setReadonly(true);
			this.custCtgCode.setReadonly(true);
			this.custRelatedParty.setReadonly(true);
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
				this.custArabicName.setReadonly(isReadOnly("CustomerDialog_custArabicName"));
				this.motherMaidenName.setReadonly(isReadOnly("CustomerDialog_custMotherMaiden"));
				this.custDftBranch.setReadonly(isReadOnly("CustomerDialog_custDftBranch"));
				this.custBaseCcy.setReadonly(isReadOnly("CustomerDialog_custBaseCcy"));
				this.custTypeCode.setReadonly(isReadOnly("CustomerDialog_custTypeCode"));
				this.custNationality.setReadonly(isReadOnly("CustomerDialog_custNationality"));
				this.custLng.setReadonly(isReadOnly("CustomerDialog_custLng"));
				this.custSts.setReadonly(true);//isReadOnly("CustomerDialog_custSts")
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
				this.custStaffID.setReadonly(isReadOnly("CustomerDialog_custStaffID"));
				this.custDSACode.setReadonly(isReadOnly("CustomerDialog_custDSACode"));
				this.custDSADept.setReadonly(isReadOnly("CustomerDialog_custDSADept"));
				this.custParentCountry.setReadonly(isReadOnly("CustomerDialog_custParentCountry"));
				this.custRiskCountry.setReadonly(isReadOnly("CustomerDialog_custRiskCountry"));
				this.custSubSector.setReadonly(isReadOnly("CustomerDialog_custIndustry"));
				this.custSubSegment.setReadonly(isReadOnly("CustomerDialog_custSubSegment"));
			} else {
				this.custShrtName.setReadonly(true);
				this.custFirstName.setReadonly(true);
				this.custMiddleName.setReadonly(true);
				this.custLastName.setReadonly(true);
				this.custArabicName.setReadonly(true);
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
		this.custArabicName.setReadonly(true);
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
		this.custStaffID.setReadonly(true);
		this.custDSACode.setReadonly(true);
		this.custDSADept.setReadonly(true);
		this.custRiskCountry.setReadonly(true);
		this.custParentCountry.setReadonly(true);
		this.custSubSector.setReadonly(true);
		this.custSubSegment.setReadonly(true);

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

		this.custMaritalSts.setDisabled(true);
		this.noOfDependents.setReadonly(true);

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

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
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
		this.custArabicName.setValue("");
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

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws CustomerNotFoundException
	 */
	public void doSave() throws InterruptedException, ParseException, InterfaceException {
		logger.debug("Entering");
		Cloner cloner = new Cloner();
		CustomerDetails aCustomerDetails = new CustomerDetails();
		aCustomerDetails = cloner.deepClone(getCustomerDetails());
		boolean isNew = false;
		Customer aCustomer = aCustomerDetails.getCustomer();

		// fill the Customer object with the components data
		doWriteComponentsToBean(aCustomerDetails, null);
		aCustomer = aCustomerDetails.getCustomer();

		if (validateCustDocs && !validateCustomerDocuments(aCustomer, null)) {
			return;
		}

		if (StringUtils.equals("Submit", userAction.getSelectedItem().getLabel())) {
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
			}else{
				if (aCustomerDetails.getCustomerDedupList() != null
						&& !aCustomerDetails.getCustomerDedupList().isEmpty()) {
					CustomerDedup dedup = aCustomerDetails.getCustomerDedupList().get(0);
					if (dedup != null) {
						aCustomerDetails.getCustomer()
						.setCustCoreBank(dedup.getCustCoreBank());
					}
					logger.debug("Posidex Id:" + dedup.getCustCoreBank());
				}
			}

			if (doProcess(aCustomerDetails, tranType)) {
				refreshList();
				closeDialog();
			}
			logger.debug(" Calling doSave method completed Successfully");
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private boolean doCustomerDedupe(CustomerDetails customerDetails) throws Exception {
		logger.debug("Entering");

		String corebank = customerDetails.getCustomer().getCustCoreBank();

		//If Core Bank ID is Exists then Customer is already existed in Core Banking System
		if ("Y".equalsIgnoreCase(SysParamUtil.getValueAsString("POSIDEX_DEDUP_REQD"))) {
			if (StringUtils.equals("Submit", userAction.getSelectedItem().getLabel())
					&& StringUtils.isBlank(corebank)) {
				String curLoginUser = getUserWorkspace().getUserDetails().getSecurityUser().getUsrLogin();
				customerDetails = FetchFinCustomerDedupDetails.getFinCustomerDedup(getRole(),
						SysParamUtil.getValueAsString("FINONE_DEF_FINTYPE"), "", customerDetails,
						this.window_CustomerDialog, curLoginUser);

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

	private boolean doProcess(CustomerDetails aCustomerDetails, String tranType)
			throws InterfaceException, InterruptedException {
		logger.debug("Entering");
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
			details.setRecordType(aCustomerDetails.getCustomer().getRecordType());
			details.setVersion(aCustomerDetails.getCustomer().getVersion());
			details.setWorkflowId(aCustomerDetails.getCustomer().getWorkflowId());
			details.setTaskId(taskId);
			details.setNextTaskId(nextTaskId);
			details.setRoleCode(getRole());
			details.setNextRoleCode(nextRoleCode);
			details.setNewRecord(aCustomerDetails.getCustomer().isNewRecord());
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
			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, aCustomer, finishedTasks);
			auditHeader = getAuditHeader(aCustomerDetails, PennantConstants.TRAN_WF);
			while (!"".equals(serviceTasks)) {
				String method = serviceTasks.split(";")[0];
				if ("doDdeDedup".equals(method) || "doVerifierDedup".equals(method)
						|| "doApproverDedup".equals(method)) {
					CustomerDetails tCustomerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
					String curLoginUser = getUserWorkspace().getUserDetails().getSecurityUser().getUsrLogin();
					tCustomerDetails = FetchCustomerDedupDetails.getCustomerDedup(getRole(), tCustomerDetails,
							this.window_CustomerDialog, curLoginUser);
					if (tCustomerDetails.getCustomer().isDedupFound()
							&& !tCustomerDetails.getCustomer().isSkipDedup()) {
						processCompleted = false;
					} else {
						processCompleted = true;
					}
					auditHeader.getAuditDetail().setModelData(tCustomerDetails);
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
			if (!aCustomerDetails.getCustomer().isSkipDedup()) {
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
			auditHeader = getAuditHeader(aCustomerDetails, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method)
			throws InterfaceException, InterruptedException {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		CustomerDetails aCustomerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
		Customer aCustomer = aCustomerDetails.getCustomer();
		boolean deleteNotes = false;
		try {
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
						auditHeader = getCustomerDetailsService().doApprove(auditHeader);
						if (aCustomer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCustomerDetailsService().doReject(auditHeader);
						if (aCustomer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
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
		} catch (InterfaceException pfe) {
			MessageUtil.showError(pfe);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	public void doSetCustomerData(CustomerDetails customerDetails) {
		doClearErrorMessage();
		doRemoveValidation();
		doRemoveLOVValidation();
		setCustomerDetails(customerDetails);
		doWriteBeanToComponents(customerDetails);
	}

	public void doSave_CustomerDetail(FinanceDetail aFinanceDetail, boolean validatePhoneNum) throws ParseException {
		doSave_CustomerDetail(aFinanceDetail, null, validatePhoneNum);
	}

	/**
	 * This method set the customer details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 * @throws ParseException
	 */
	public boolean doSave_CustomerDetail(FinanceDetail aFinanceDetail, Tab tab, boolean validateChildDetails)
			throws ParseException {
		logger.debug("Entering ");
		if (getCustomerDetails() != null) {
			Cloner cloner = new Cloner();
			CustomerDetails aCustomerDetails = new CustomerDetails();
			aCustomerDetails = cloner.deepClone(getCustomerDetails());
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
			if (validateChildDetails) {
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
			if (!isRetailCustomer || ImplementationConstants.ALLOW_MULTIPLE_EMPLOYMENTS) {
				aCustomerDetails.setCustEmployeeDetail(null);
			}
			aFinanceDetail.setCustomerDetails(aCustomerDetails);
		}
		logger.debug("Leaving ");
		return true;
	}

	private boolean validateEmailDetails(Tab tab) {
		logger.debug("Entering");
		boolean isMandAddExist = false;
		if (this.customerEmailDetailList.isEmpty()) {
			return !isMandAddExist;
		} else {
			for (CustomerEMail custEmail : this.customerEmailDetailList) {
				if (StringUtils.equals(PennantConstants.EMAILPRIORITY_VeryHigh,
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
					if (StringUtils.equals(PennantConstants.EMAILPRIORITY_VeryHigh,
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
				if (StringUtils.equals(PennantConstants.EMAILPRIORITY_VeryHigh,
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
				if (custDocument.isDocIssueDateMand() && custDocument.getCustDocIssuedOn() == null) {
					doShowValidationMessage(custTab, 3, custDocument.getLovDescCustDocCategory());
					return false;
				}
				if (custDocument.isLovDescdocExpDateIsMand() && custDocument.getCustDocExpDate() == null) {
					doShowValidationMessage(custTab, 5, custDocument.getLovDescCustDocCategory());
					return false;
				}
				if (StringUtils.equals(PennantConstants.CPRCODE, custDocument.getCustDocCategory())) {
					if (isRetailCustomer && !this.custDOB.isDisabled() && this.custDOB.getValue() != null
							&& custDocument.getCustDocIssuedOn() != null
							&& custDocument.getCustDocIssuedOn().before(this.custDOB.getValue())) {
						doShowValidationMessage(custTab, 1, custDocument.getLovDescCustDocCategory());
						return false;
					}
				} else if (StringUtils.equals(PennantConstants.PANNUMBER, custDocument.getCustDocCategory())) {
					if (!this.custDOB.isDisabled() && this.custDOB.getValue() != null
							&& custDocument.getCustDocIssuedOn() != null
							&& custDocument.getCustDocIssuedOn().before(this.custDOB.getValue())) {
						doShowValidationMessage(custTab, 1, custDocument.getLovDescCustDocCategory());
						return false;
					}
				}
				if (StringUtils.equals(PennantConstants.PANNUMBER, custDocument.getCustDocCategory())) {
					isMandateIDDocExist = true;
					if (StringUtils.isNotBlank(this.eidNumber.getValue())) {
						if (!StringUtils.equals(this.eidNumber.getValue(), custDocument.getCustDocTitle())) {
							doShowValidationMessage(custTab, 2, custDocument.getLovDescCustDocCategory() + " Number");
							return false;
						}
					} else {
						aCustomer.setCustCRCPR(custDocument.getCustDocTitle());
					}
				}
				if (!isRetailCustomer
						&& StringUtils.equals(PennantConstants.TRADELICENSE, custDocument.getCustDocCategory())) {
					if (!this.custDOB.isDisabled() && this.custDOB.getValue() != null
							&& custDocument.getCustDocIssuedOn() != null
							&& DateUtility.compare(custDocument.getCustDocIssuedOn(), this.custDOB.getValue()) != 0) {
						doShowValidationMessage(custTab, 6, custDocument.getLovDescCustDocCategory());
						return false;
					}
				}

			}
		}
		if (!StringUtils.isBlank(aCustomer.getCustCRCPR()) && !isMandateIDDocExist && validateAllDetails) {
			/*doShowValidationMessage(custTab, 4,
					isRetailCustomer ? PennantConstants.PANNUMBER : PennantConstants.PANNUMBER);
			return false;*/
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
					new String[] { PennantAppUtil.getlabelDesc(value, PennantAppUtil.getDocumentTypes()) });
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
			this.custGroupId.setValue("");
			this.custGroupId.setDescription("");
		} else {
			CustomerGroup details = (CustomerGroup) dataObject;
			if (details != null) {
				this.custGroupId.setValue(String.valueOf(details.getCustGrpID()));
				this.custGroupId.setDescription(details.getCustGrpDesc());
			}
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

	public void onChange$eidNumber(Event event) {
		logger.debug("Entering");
		if (isRetailCustomer) {
			this.eidNumber.setValue(PennantApplicationUtil.formatEIDNumber(this.eidNumber.getValue()));
		}
		logger.debug("Leaving");
	}

	public String getCustIDNumber(String idType) {
		logger.debug("Entering");
		String idNumber = "";
		try {
			if (PennantConstants.CPRCODE.equalsIgnoreCase(idType)) {
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
			} else if (PennantConstants.PASSPORT.equalsIgnoreCase(idType)) {
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
			} else if (PennantConstants.PANNUMBER.equalsIgnoreCase(idType)) {
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

	public void onFulfill$custBaseCcy(Event event) throws InterruptedException {
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
		String infoMsg = " \n 1) " + Labels.getLabel("gp_CustEmployeeDetails") + " \n 2) "
				+ Labels.getLabel("gp_CustomerChequeInfoDetails") + " \n 3) "
				+ Labels.getLabel("gp_ExternalLiabilityDetails");
		return infoMsg;
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
				customerChequeInfo.setSalary(PennantAppUtil.unFormateAmount(
						PennantAppUtil.formateAmount(customerChequeInfo.getSalary(), old_ccyFormatter), ccyFormatter));
				customerChequeInfo.setReturnChequeAmt(PennantAppUtil.unFormateAmount(
						PennantAppUtil.formateAmount(customerChequeInfo.getReturnChequeAmt(), old_ccyFormatter),
						ccyFormatter));
				customerChequeInfo.setTotChequePayment(PennantAppUtil.unFormateAmount(
						PennantAppUtil.formateAmount(customerChequeInfo.getTotChequePayment(), old_ccyFormatter),
						ccyFormatter));
			}
			doFillCustomerChequeInfoDetails(getCustomerChequeInfoDetailList());
		}
		if (getCustomerExtLiabilityDetailList() != null && !getCustomerExtLiabilityDetailList().isEmpty()) {
			for (CustomerExtLiability customerExtLiability : getCustomerExtLiabilityDetailList()) {
				if (StringUtils.isBlank(customerExtLiability.getRecordType())) {
					customerExtLiability.setVersion(customerExtLiability.getVersion() + 1);
					customerExtLiability.setRecordType(PennantConstants.RCD_UPD);
				}
				customerExtLiability.setOriginalAmount(PennantAppUtil.unFormateAmount(
						PennantAppUtil.formateAmount(customerExtLiability.getOriginalAmount(), old_ccyFormatter),
						ccyFormatter));
				customerExtLiability.setInstalmentAmount(PennantAppUtil.unFormateAmount(
						PennantAppUtil.formateAmount(customerExtLiability.getInstalmentAmount(), old_ccyFormatter),
						ccyFormatter));
				customerExtLiability.setOutStandingBal(PennantAppUtil.unFormateAmount(
						PennantAppUtil.formateAmount(customerExtLiability.getOutStandingBal(), old_ccyFormatter),
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
	public void onClick$btnNew_CustomerRatings(Event event) throws Exception {
		logger.debug("Entering");
		CustomerRating customerRating = new CustomerRating();
		customerRating.setNewRecord(true);
		customerRating.setWorkflowId(0);
		customerRating.setCustID(getCustomerDetails().getCustID());
		customerRating.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		customerRating.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		final HashMap<String, Object> map = new HashMap<String, Object>();
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

	public void onCustomerRatingItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerRating.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerRating customerRating = (CustomerRating) item.getAttribute("data");
			if (isDeleteRecord(customerRating.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
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
	public void onClick$btnNew_CustomerEmploymentDetail(Event event) throws Exception {
		logger.debug("Entering");
		CustomerEmploymentDetail customerEmploymentDetail = new CustomerEmploymentDetail();
		customerEmploymentDetail.setNewRecord(true);
		customerEmploymentDetail.setWorkflowId(0);
		customerEmploymentDetail.setCustID(getCustomerDetails().getCustID());
		customerEmploymentDetail.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		customerEmploymentDetail.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		final HashMap<String, Object> map = new HashMap<String, Object>();
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
	public void onClick$btnNew_DirectorDetail(Event event) throws Exception {
		logger.debug("Entering");
		final DirectorDetail directorDetail = getDirectorDetailService().getNewDirectorDetail();
		directorDetail.setWorkflowId(0);
		directorDetail.setCustID(getCustomerDetails().getCustID());
		// directorDetail.setDirectorId(this.listBoxCustomerDirectory.getItemCount()
		// == 0 ? 1 : this.listBoxCustomerDirectory.getItemCount() + 1);
		directorDetail.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		directorDetail.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("directorDetail", directorDetail);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		map.put("totSharePerc", getTotSharePerc());
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/DirectorDetail/DirectorDetailDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
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
				map.put("customerDialogCtrl", this);
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

	// ********************************************************************//
	// ***** New Button & Double Click Events for Customer Income List ****//
	// ********************************************************************//
	public void onClick$btnNew_CustomerIncome(Event event) throws Exception {
		logger.debug("Entering");
		CustomerIncome customerIncome = new CustomerIncome();
		customerIncome.setNewRecord(true);
		customerIncome.setWorkflowId(0);
		customerIncome.setCustID(getCustomerDetails().getCustID());
		customerIncome.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		customerIncome.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerIncome", customerIncome);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("ccyFormatter", ccyFormatter);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerIncome/CustomerIncomeDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
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
				map.put("customerDialogCtrl", this);
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
	public void onClick$btnNew_CustomerDocuments(Event event) throws Exception {
		logger.debug("Entering");
		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setNewRecord(true);
		customerDocument.setWorkflowId(0);
		customerDocument.setCustID(getCustomerDetails().getCustID());
		customerDocument.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		customerDocument.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerDocument", customerDocument);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("isFinanceProcess", isFinanceProcess);
		map.put("roleCode", getRole());
		map.put("isRetailCustomer", isRetailCustomer);
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
				if(customerDocument.getCustDocImage() == null) {
					if (customerDocument.getDocRefId() != Long.MIN_VALUE) {
						customerDocument.setCustDocImage(PennantAppUtil.getDocumentImage(customerDocument.getDocRefId()));
					} else if(StringUtils.isNotBlank(customerDocument.getDocUri())) {
						try {
							// Fetch document from interface
							DocumentDetails detail = externalDocumentManager.getExternalDocument(customerDocument.getDocUri());
							customerDocument.setCustDocImage(PennantApplicationUtil.decode(detail.getDocImage()));
						} catch (InterfaceException e) {
							MessageUtil.showError(e);
						}
					}
				}
				customerDocument.setLovDescCustCIF(this.custCIF.getValue());
				customerDocument.setLovDescCustShrtName(this.custShrtName.getValue());
				map.put("customerDocument", customerDocument);
				map.put("customerDialogCtrl", this);
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
						String desc = PennantAppUtil.getlabelDesc(customerDocument.getCustDocCategory(), docTypeList);
						customerDocument.setLovDescCustDocCategory(desc);
						lc = new Listcell(desc);
					} else {
						lc = new Listcell(customerDocument.getLovDescCustDocCategory());
					}
					lc.setParent(item);
					if (StringUtils.equals(customerDocument.getCustDocCategory(), PennantConstants.CPRCODE)) {
						lc = new Listcell(PennantApplicationUtil.formatEIDNumber(customerDocument.getCustDocTitle()));
					} else {
						lc = new Listcell(customerDocument.getCustDocTitle());
					}
					lc.setParent(item);
					lc = new Listcell(customerDocument.getLovDescCustDocIssuedCountry());
					lc.setParent(item);
					lc = new Listcell(customerDocument.getCustDocSysName());
					lc.setParent(item);
					lc = new Listcell(DateUtility.formatToLongDate(customerDocument.getCustDocIssuedOn()));
					lc.setParent(item);
					lc = new Listcell(DateUtility.formatToLongDate(customerDocument.getCustDocExpDate()));
					lc.setParent(item);
					lc = new Listcell(customerDocument.getRecordStatus());
					lc.setParent(item);
					lc = new Listcell(PennantJavaUtil.getLabel(customerDocument.getRecordType()));
					lc.setParent(item);
					item.setAttribute("data", customerDocument);
					ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerDocumentItemDoubleClicked");
					this.listBoxCustomerDocuments.appendChild(item);
				}
			}
			setCustomerDocumentDetailList(custDocumentDetails);
		}
		logger.debug("Leaving");
	}

	// ********************************************************************//
	// ***** New Button & Double Click Events for CustomerAddress List ****//
	// ********************************************************************//
	public void onClick$btnNew_CustomerAddress(Event event) throws Exception {
		logger.debug("Entering");
		CustomerAddres customerAddres = new CustomerAddres();
		customerAddres.setNewRecord(true);
		customerAddres.setWorkflowId(0);
		customerAddres.setCustID(getCustomerDetails().getCustID());
		customerAddres.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		customerAddres.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());

		Filter[] countrysystemDefault = new Filter[1];
		countrysystemDefault[0] = new Filter("SystemDefault", "1", Filter.OP_EQUAL);
		Object countryObj = PennantAppUtil.getSystemDefault("Country", "", countrysystemDefault);
		if (countryObj != null) {
			Country country = (Country) countryObj;
			customerAddres.setCustAddrCountry(country.getCountryCode());
			customerAddres.setLovDescCustAddrCountryName(country.getCountryDesc());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerAddres", customerAddres);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("isFinanceProcess", isFinanceProcess);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerAddres/CustomerAddresDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
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
		logger.debug("Leaving");
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
		}
		logger.debug("Leaving");
	}

	// ********************************************************************//
	// ** New Button & Double Click Events for CustomerPhoneNumbers List **//
	// ********************************************************************//
	public void onClick$btnNew_CustomerPhoneNumber(Event event) throws Exception {
		logger.debug("Entering");
		CustomerPhoneNumber customerPhoneNumber = new CustomerPhoneNumber();
		customerPhoneNumber.setNewRecord(true);
		customerPhoneNumber.setWorkflowId(0);
		customerPhoneNumber.setPhoneCustID(getCustomerDetails().getCustID());
		customerPhoneNumber.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		customerPhoneNumber.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerPhoneNumber", customerPhoneNumber);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("isFinanceProcess", isFinanceProcess);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerPhoneNumber/CustomerPhoneNumberDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
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
				lc.setParent(item);
				lc = new Listcell(customerPhoneNumber.getPhoneNumber());
				lc.setParent(item);
				lc = new Listcell(customerPhoneNumber.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(customerPhoneNumber.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", customerPhoneNumber);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerPhoneNumberItemDoubleClicked");
				this.listBoxCustomerPhoneNumbers.appendChild(item);
			}
			setCustomerPhoneNumberDetailList(customerPhoneNumDetails);
		}
		logger.debug("Leaving");
	}

	// ********************************************************************//
	// ++ New Button & Double Click Events for CustomerEmailAddress List ++//
	// ********************************************************************//
	public void onClick$btnNew_CustomerEmail(Event event) throws Exception {
		logger.debug("Entering");
		CustomerEMail customerEMail = new CustomerEMail();
		customerEMail.setNewRecord(true);
		customerEMail.setWorkflowId(0);
		customerEMail.setCustID(getCustomerDetails().getCustID());
		customerEMail.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		customerEMail.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		customerEMail.setCustEMailPriority(Integer.parseInt(PennantConstants.EMAILPRIORITY_Medium));
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerEMail", customerEMail);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("isFinanceProcess", isFinanceProcess);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerEMail/CustomerEMailDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
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
		if (customerEmailDetails != null) {
			for (CustomerEMail customerEMail : customerEmailDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(customerEMail.getLovDescCustCIF());
				lc.setParent(item);
				lc = new Listcell(customerEMail.getLovDescCustEMailTypeCode());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.formateInt(customerEMail.getCustEMailPriority()));
				lc.setParent(item);
				lc = new Listcell(customerEMail.getCustEMail());
				lc.setParent(item);
				lc = new Listcell(customerEMail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(customerEMail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", customerEMail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerEmailAddressItemDoubleClicked");
				this.listBoxCustomerEmails.appendChild(item);
			}
			setCustomerEmailDetailList(customerEmailDetails);
		}
		logger.debug("Leaving");
	}

	// ********************************************************************//
	// ++ New Button & Double Click Events for Customer Bank Information List
	// ++//
	// ********************************************************************//
	public void onClick$btnNew_BankInformation(Event event) throws Exception {
		logger.debug("Entering");
		CustomerBankInfo custBankInfo = new CustomerBankInfo();
		custBankInfo.setNewRecord(true);
		custBankInfo.setWorkflowId(0);
		custBankInfo.setCustID(getCustomerDetails().getCustID());
		custBankInfo.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		custBankInfo.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerBankInfo", custBankInfo);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("isFinanceProcess", isFinanceProcess);
		map.put("roleCode", getRole());
		map.put("CustomerBankInfoList", CustomerBankInfoList);
		map.put("retailCustomer", StringUtils.equals(this.custCtgCode.getValue(), PennantConstants.PFF_CUSTCTG_INDIV));
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerBankInfoDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
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

	// ********************************************************************//
	// ++ New Button & Double Click Events for Cheque Information List ++//
	// ********************************************************************//
	public void onClick$btnNew_ChequeInformation(Event event) throws Exception {
		logger.debug("Entering");
		CustomerChequeInfo custChequeInfo = new CustomerChequeInfo();
		custChequeInfo.setNewRecord(true);
		custChequeInfo.setWorkflowId(0);
		custChequeInfo.setCustID(getCustomerDetails().getCustID());
		custChequeInfo.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		custChequeInfo.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		custChequeInfo.setChequeSeq(getChequeSeq());
		final HashMap<String, Object> map = new HashMap<String, Object>();
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
				lc = new Listcell(
						DateUtility.formateDate(custChequeInfo.getMonthYear(), PennantConstants.monthYearFormat));
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(custChequeInfo.getTotChequePayment(), ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(custChequeInfo.getSalary(), ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(custChequeInfo.getReturnChequeAmt(), ccyFormatter));
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
	public void onClick$btnNew_ExternalLiability(Event event) throws Exception {
		logger.debug("Entering");
		CustomerExtLiability custExtLiability = new CustomerExtLiability();
		custExtLiability.setNewRecord(true);
		custExtLiability.setWorkflowId(0);
		custExtLiability.setCustID(getCustomerDetails().getCustID());
		custExtLiability.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		custExtLiability.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		custExtLiability.setLiabilitySeq(getLiabilitySeq());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerExtLiability", custExtLiability);
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

	public void onCustomerExtLiabilityItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerExternalLiability.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerExtLiability custExtLiability = (CustomerExtLiability) item.getAttribute("data");
			if (isDeleteRecord(custExtLiability.getRecordType())) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				custExtLiability.setLovDescCustCIF(this.custCIF.getValue());
				custExtLiability.setLovDescCustShrtName(this.custShrtName.getValue());
				map.put("customerExtLiability", custExtLiability);
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

		BigDecimal originalAmount = BigDecimal.ZERO;
		BigDecimal instalmentAmount = BigDecimal.ZERO;
		BigDecimal outStandingBal = BigDecimal.ZERO;

		if (customerExtLiabilityDetails != null) {
			for (CustomerExtLiability custExtLiability : customerExtLiabilityDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				if (custExtLiability.getFinDate() == null) {
					lc = new Listcell();
				} else {
					lc = new Listcell(DateUtility.formatToLongDate(custExtLiability.getFinDate()));
				}
				lc.setParent(item);
				lc = new Listcell(custExtLiability.getLovDescFinType());
				lc.setParent(item);
				lc = new Listcell(custExtLiability.getLovDescBankName());
				lc.setParent(item);
				originalAmount = originalAmount.add(custExtLiability.getOriginalAmount() == null ? BigDecimal.ZERO
						: custExtLiability.getOriginalAmount());
				lc = new Listcell(PennantAppUtil.amountFormate(custExtLiability.getOriginalAmount(), ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				instalmentAmount = instalmentAmount.add(custExtLiability.getInstalmentAmount() == null ? BigDecimal.ZERO
						: custExtLiability.getInstalmentAmount());
				lc = new Listcell(PennantAppUtil.amountFormate(custExtLiability.getInstalmentAmount(), ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				outStandingBal = outStandingBal.add(custExtLiability.getOutStandingBal() == null ? BigDecimal.ZERO
						: custExtLiability.getOutStandingBal());
				lc = new Listcell(PennantAppUtil.amountFormate(custExtLiability.getOutStandingBal(), ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(custExtLiability.getLovDescFinStatus());
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
				lc = new Listcell(PennantAppUtil.amountFormate(originalAmount, ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(instalmentAmount, ccyFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(outStandingBal, ccyFormatter));
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

	public void doFillCustFinanceExposureDetails(List<FinanceEnquiry> custFinanceExposureDetails) {
		logger.debug("Entering");
		this.listBoxCustomerFinExposure.getItems().clear();
		if (custFinanceExposureDetails != null) {
			for (FinanceEnquiry finEnquiry : custFinanceExposureDetails) {

				int format = CurrencyUtil.getFormat(finEnquiry.getFinCcy());
				Listitem item = new Listitem();
				Listcell lc = new Listcell(DateUtility.formatToLongDate(finEnquiry.getFinStartDate()));
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getLovDescFinTypeName());
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinReference());
				lc.setParent(item);

				BigDecimal totAmt = finEnquiry.getFinCurrAssetValue()
						.add(finEnquiry.getFeeChargeAmt().add(finEnquiry.getInsuranceAmt()));
				lc = new Listcell(PennantAppUtil.amountFormate(totAmt, format));
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(finEnquiry.getMaxInstAmount(), format));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(totAmt.subtract(finEnquiry.getFinRepaymentAmount()),
						format));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinStatus());
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
				int tempId = customerExtLiability.getLiabilitySeq();
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
					totIncome = totIncome.add(customerIncome.getCustIncome());
					if (incomeMap.containsKey(category)) {
						incomeMap.get(category).add(customerIncome);
					} else {
						ArrayList<CustomerIncome> list = new ArrayList<CustomerIncome>();
						list.add(customerIncome);
						incomeMap.put(category, list);
					}
				} else {
					totExpense = totExpense.add(customerIncome.getCustIncome());
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
					cell = new Listcell(list.get(0).getIncomeExpense() + "-" + list.get(0).getLovDescCategoryName());
					cell.setParent(group);
					this.listBoxCustomerIncome.appendChild(group);
					BigDecimal total = BigDecimal.ZERO;
					for (CustomerIncome customerIncome : list) {
						item = new Listitem();
						cell = new Listcell("");
						cell.setParent(item);
						cell = new Listcell(customerIncome.getLovDescCustIncomeTypeName());
						cell.setParent(item);
						total = total.add(customerIncome.getCustIncome());
						cell = new Listcell(PennantAppUtil.amountFormate(customerIncome.getCustIncome(), ccyFormatter));
						cell.setStyle("text-align:right;");
						cell.setParent(item);
						cell = new Listcell();
						cb = new Checkbox();
						cb.setDisabled(true);
						cb.setChecked(customerIncome.isJointCust());
						cb.setParent(cell);
						cell.setParent(item);
						cell = new Listcell(customerIncome.getRecordStatus());
						cell.setParent(item);
						cell = new Listcell(PennantJavaUtil.getLabel(customerIncome.getRecordType()));
						cell.setParent(item);
						item.setAttribute("data", customerIncome);
						ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerIncomeItemDoubleClicked");
						this.listBoxCustomerIncome.appendChild(item);
					}
					item = new Listitem();
					cell = new Listcell("Total");
					cell.setStyle("font-weight:bold;cursor:default");
					cell.setParent(item);
					cell = new Listcell(PennantAppUtil.amountFormate(total, ccyFormatter));
					cell.setSpan(2);
					cell.setStyle("font-weight:bold; text-align:right;cursor:default");
					cell.setParent(item);
					cell = new Listcell();
					cell.setSpan(3);
					cell.setStyle("cursor:default");
					cell.setParent(item);
					this.listBoxCustomerIncome.appendChild(item);
				}
			}
			item = new Listitem();
			cell = new Listcell("Gross Income");
			cell.setStyle("font-weight:bold;cursor:default");
			cell.setParent(item);
			cell = new Listcell(PennantAppUtil.amountFormate(totIncome, ccyFormatter));
			cell.setSpan(2);
			cell.setStyle("font-weight:bold; text-align:right;cursor:default");
			cell.setParent(item);
			cell = new Listcell();
			cell.setSpan(3);
			cell.setStyle("cursor:default");
			cell.setParent(item);
			this.listBoxCustomerIncome.appendChild(item);
		}
		if (expenseMap != null) {
			for (String category : expenseMap.keySet()) {
				List<CustomerIncome> list = expenseMap.get(category);
				if (list != null) {
					group = new Listgroup();
					cell = new Listcell(list.get(0).getIncomeExpense() + "-" + list.get(0).getLovDescCategoryName());
					cell.setParent(group);
					this.listBoxCustomerIncome.appendChild(group);
					BigDecimal total = BigDecimal.ZERO;
					for (CustomerIncome customerIncome : list) {
						item = new Listitem();
						cell = new Listcell("");
						cell.setParent(item);
						cell = new Listcell(customerIncome.getLovDescCustIncomeTypeName());
						cell.setParent(item);
						total = total.add(customerIncome.getCustIncome());
						cell = new Listcell(PennantAppUtil.amountFormate(customerIncome.getCustIncome(), ccyFormatter));
						cell.setStyle("text-align:right;");
						cell.setParent(item);
						cell = new Listcell();
						cb = new Checkbox();
						cb.setDisabled(true);
						cb.setChecked(customerIncome.isJointCust());
						cb.setParent(cell);
						cell.setParent(item);
						cell = new Listcell(customerIncome.getRecordStatus());
						cell.setParent(item);
						cell = new Listcell(PennantJavaUtil.getLabel(customerIncome.getRecordType()));
						cell.setParent(item);
						item.setAttribute("data", customerIncome);
						ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerIncomeItemDoubleClicked");
						this.listBoxCustomerIncome.appendChild(item);
					}
					item = new Listitem();
					cell = new Listcell("Total");
					cell.setStyle("font-weight:bold;cursor:default");
					cell.setParent(item);
					cell = new Listcell(PennantAppUtil.amountFormate(total, ccyFormatter));
					cell.setSpan(2);
					cell.setStyle("font-weight:bold; text-align:right;cursor:default");
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
			cell.setStyle("font-weight:bold;cursor:default");
			cell.setParent(item);
			cell = new Listcell(PennantAppUtil.amountFormate(totExpense, ccyFormatter));
			cell.setSpan(2);
			cell.setStyle("font-weight:bold; text-align:right;cursor:default");
			cell.setParent(item);
			cell = new Listcell();
			cell.setSpan(3);
			cell.setStyle("cursor:default");
			cell.setParent(item);
			this.listBoxCustomerIncome.appendChild(item);
		}
		item = new Listitem();
		cell = new Listcell("Net Income");
		cell.setStyle("font-weight:bold;");
		cell.setParent(item);
		cell = new Listcell(PennantAppUtil.amountFormate(totIncome.subtract(totExpense), ccyFormatter));
		cell.setSpan(2);
		cell.setStyle("font-weight:bold; text-align:right;");
		cell.setParent(item);
		cell = new Listcell();
		cell.setSpan(3);
		cell.setStyle("cursor:default");
		cell.setParent(item);
		this.listBoxCustomerIncome.appendChild(item);
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
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
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
			final HashMap<String, Object> map = new HashMap<String, Object>();
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
		logger.debug("Entering" + event.toString());
		fillComboBox(this.custGenderCode, getComboboxValue(this.custGenderCode), PennantAppUtil.getGenderCodes(), "");
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Click
	 * 
	 * @param event
	 */
	public void onOpen$custSalutationCode(Event event) {
		logger.debug("Entering" + event.toString());
		fillComboBox(this.custSalutationCode, getComboboxValue(this.custSalutationCode),
				PennantAppUtil.getSalutationCodes(getComboboxValue(this.custGenderCode)), "");
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Click
	 * 
	 * @param event
	 */
	public void onOpen$custMaritalSts(Event event) {
		logger.debug("Entering" + event.toString());
		fillComboBox(this.custMaritalSts, getComboboxValue(this.custMaritalSts),
				PennantAppUtil.getMaritalStsTypes(getComboboxValue(this.custGenderCode)), "");
		logger.debug("Leaving" + event.toString());
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

	public void onChange$custDOB(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		processDateDiff(this.custDOB.getValue(), this.age);
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$empFrom(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		processDateDiff(this.empFrom.getValue(), this.exp);
		logger.debug("Leaving" + event.toString());
	}

	private void processDateDiff(Date fromDate, Label displayComp) {
		if (fromDate == null) {
			displayComp.setValue("");
			displayComp.setVisible(false);
			return;
		}

		int years = 0;
		int month = 0;
		if (fromDate.compareTo(appDate) < 0) {
			int months = DateUtility.getMonthsBetween(appDate, fromDate);
			years = months / 12;
			month = months % 12;
		}
		if (years == 0 && month == 0) {
			displayComp.setVisible(false);
		} else {
			String dateDiffValue = (years == 0 ? "" : years + " " + (years == 1 ? "Year" + " " : "Years" + " ")) + month
					+ " " + (month == 1 ? "Month" : "Months");
			displayComp.setValue(dateDiffValue);
			displayComp.setVisible(true);
		}
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

	public List<CustomerBankInfo> getCustomerBankInfoList() {
		return CustomerBankInfoList;
	}

	public void setCustomerBankInfoList(List<CustomerBankInfo> customerBankInfoList) {
		CustomerBankInfoList = customerBankInfoList;
	}

	public void setExternalDocumentManager(ExternalDocumentManager externalDocumentManager) {
		this.externalDocumentManager = externalDocumentManager;
	}

}