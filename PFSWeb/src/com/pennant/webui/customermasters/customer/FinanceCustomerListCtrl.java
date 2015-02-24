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
 * FileName    		:  FinFinanceCustomerListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.customermasters.CustEmployeeDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.reports.AvailFinance;
import com.pennant.backend.model.systemmasters.EmpStsCode;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/FinanceCustomer/financeFinanceCustomerList.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinanceCustomerListCtrl extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FinanceCustomerListCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinanceCustomerList;

	protected Textbox custCIF; 							// autowired
	protected Textbox custCoreBank; 					// autowired
	protected Combobox custSalutationCode; 				// autowired
	protected Textbox custShrtName; 					// autowired
	protected Textbox custFirstName; 					// autowired
	protected Textbox custMiddleName; 					// autowired
	protected Textbox custLastName; 					// autowired
	protected Textbox custArabicName; 					// autowired
	protected ExtendedCombobox custNationality; 		// autowired
	protected Combobox custMaritalSts;  		        // autowired
	protected Datebox custDOB; 							// autowired
	protected Combobox custGenderCode; 					// autowired
	protected Intbox noOfDependents;                    // autowired
	protected Combobox target; 					        // autowired
	protected ExtendedCombobox custCtgCode; 			// autowired
	protected Checkbox salaryTransferred;               // autowired
	protected ExtendedCombobox custDftBranch; 			// autowired
	protected ExtendedCombobox custTypeCode; 			// autowired
	protected ExtendedCombobox custBaseCcy; 			// autowired
	protected Checkbox salariedCustomer;			    // autowired
	protected Label label_FinanceCustomerList_CustDOB;

	/** Customer Employer Fields**/
	protected ExtendedCombobox empStatus;				// autowired
	protected ExtendedCombobox empSector;				// autowired
	protected ExtendedCombobox profession;				// autowired
	protected ExtendedCombobox empName;				    // autowired
	protected Hbox hbox_empNameOther;				    // autowired
	protected Label label_empNameOther;				    // autowired
	protected Textbox empNameOther;				        // autowired
	protected Datebox empFrom; 							// autowired
	protected ExtendedCombobox empDesg;				    // autowired
	protected ExtendedCombobox empDept;				    // autowired
	protected CurrencyBox monthlyIncome;				// autowired
	protected ExtendedCombobox otherIncome;				// autowired
	protected CurrencyBox additionalIncome;				// autowired
	
	protected Label label_FinanceCustomerList_EmpSector;
	protected Label label_FinanceCustomerList_Profession;
	protected Label label_FinanceCustomerList_EmpFrom;
	protected Label label_FinanceCustomerList_MonthlyIncome;
	protected Row row_EmpName;
	protected Row row_DesgDept;
	
	protected Tab basicDetails;
	protected Tab tabkYCDetails;
	protected Tab tabbankDetails;

	protected Button btnNew_CustomerDocuments;
	protected Listbox listBoxCustomerDocuments;
	private List<CustomerDocument> customerDocumentDetailList = new ArrayList<CustomerDocument>();
	private List<CustomerDocument> oldVar_customerDocumentDetailList = new ArrayList<CustomerDocument>();

	protected Button btnNew_CustomerAddress;
	protected Listbox listBoxCustomerAddress;
	private List<CustomerAddres> customerAddressDetailList = new ArrayList<CustomerAddres>();
	private List<CustomerAddres> oldVar_customerAddressDetailList = new ArrayList<CustomerAddres>();

	protected Button btnNew_CustomerPhoneNumber;
	protected Listbox listBoxCustomerPhoneNumbers;
	private List<CustomerPhoneNumber> customerPhoneNumberDetailList = new ArrayList<CustomerPhoneNumber>();
	private List<CustomerPhoneNumber> oldVar_customerPhoneNumberDetailList = new ArrayList<CustomerPhoneNumber>();

	protected Button btnNew_CustomerEmail;
	protected Listbox listBoxCustomerEmails;
	private List<CustomerEMail> customerEmailDetailList = new ArrayList<CustomerEMail>();
	private List<CustomerEMail> oldVar_customerEmailDetailList = new ArrayList<CustomerEMail>();

	protected Button btnNew_BankInformation;
	protected Listbox listBoxCustomerBankInformation;
	private List<CustomerBankInfo> customerBankInfoDetailList = new ArrayList<CustomerBankInfo>();
	private List<CustomerBankInfo> oldVar_customerBankInfoDetailList = new ArrayList<CustomerBankInfo>();

	protected Button btnNew_ChequeInformation;
	protected Listbox listBoxCustomerChequeInformation;
	private List<CustomerChequeInfo> customerChequeInfoDetailList = new ArrayList<CustomerChequeInfo>();
	private List<CustomerChequeInfo> oldVar_customerChequeInfoDetailList = new ArrayList<CustomerChequeInfo>();

	protected Listbox listBoxCustomerFinExposure;

	protected Button btnNew_ExternalLiability;
	protected Listbox listBoxCustomerExternalLiability;
	private List<CustomerExtLiability> customerExtLiabilityDetailList = new ArrayList<CustomerExtLiability>();
	private List<CustomerExtLiability> oldVar_customerExtLiabilityDetailList = new ArrayList<CustomerExtLiability>();

	private transient String oldVar_custCoreBank;
	private transient String oldVar_custShrtName;
	private transient String oldVar_custFirstName;
	private transient String oldVar_custMiddleName;
	private transient String oldVar_custLastName;
	private transient String oldVar_custArabicName;
	private transient String oldVar_custCtgCode;
	private transient String oldVar_custDftBranch;
	private transient String oldVar_custTypeCode;
	private transient String oldVar_custBaseCcy;
	private transient String oldVar_custNationality;
	private transient Date oldVar_custDOB;
	private transient String oldVar_custSalutationCode;
	private transient String oldVar_custGenderCode;
	private transient String oldVar_custMaritalSts;
	
	private transient String oldVar_empStatus;
	private transient String oldVar_empSector;
	private transient String oldVar_profession;
	private transient String oldVar_empName;
	private transient String oldVar_empNameOther;
	private transient Date oldVar_empFrom;
	private transient String oldVar_empDesg;
	private transient String oldVar_empDept;
	private transient BigDecimal oldVar_monthlyIncome;
	private transient String oldVar_otherIncome;
	private transient BigDecimal oldVar_additionalIncome;
	
	private CustomerDetails customerDetails; // overhanded per param

	// For Dynamically calling of this Controller
	private FinanceDetail financedetail;
	private Object financeMainDialogCtrl;
	private Tabpanel panel = null;

	protected Div divKycDetails;
	public int borderLayoutHeight = 0;
	private boolean newFinance = false;
	private String roleCode = "";
	private boolean isEnquiry = false;
	private transient boolean validationOn;
	private boolean corpCustomer=false;
	private boolean isLocalCountry = false;
	private String sCustGender;
	int finFormatter;
	
	public static final String EmploymentStatus_BUSINESS = "BUSINESS";
	public static final String EmploymentStatus_SELFEMP = "SELFEMP";
	public static final String EmploymentName_OTHERS = "OTHERS";
	
	private String empStatus_Temp = ""; 
	private String empName_Temp = ""; 
	/**
	 * default constructor.<br>
	 */
	public FinanceCustomerListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FinanceCustomer object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceCustomerList(ForwardEvent event) throws Exception {
		logger.debug("Entring" + event.toString());


		try {

			if (event.getTarget().getParent() != null) {
				panel = (Tabpanel) event.getTarget().getParent();
			}

			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			if (args.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((Object) args.get("financeMainDialogCtrl"));
				this.window_FinanceCustomerList.setTitle("");
				newFinance = true;
			}

			if (args.containsKey("roleCode")) {
				getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), "FinanceCustomerList");
				roleCode = (String) args.get("roleCode");
			}

			if (args.containsKey("isEnquiry")) {
				isEnquiry = (Boolean) args.get("isEnquiry");
			}

			if (args.containsKey("financedetail")) {
				setFinancedetail((FinanceDetail) args.get("financedetail"));
				if (getFinancedetail() != null) {
					setCustomerDetails(getFinancedetail().getCustomerDetails());
					FinanceMain financeMain = getFinancedetail().getFinScheduleData().getFinanceMain();
					getFinancedetail().getCustomerDetails().getCustomer().setWorkflowId(financeMain.getWorkflowId());
					finFormatter = getCustomerDetails().getCustomer().getLovDescCcyFormatter();
					if(getCustomerDetails() != null && StringUtils.trimToEmpty(getCustomerDetails().getCustomer().getCustNationality()).equals(PennantConstants.COUNTRY_BEHRAIN)){
						isLocalCountry = true; 
					}
				}
			}

			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
			int divKycHeight = this.borderLayoutHeight - 80;
			this.divKycDetails.setHeight(divKycHeight + "px");
			int borderlayoutHeights = divKycHeight / 2;
			this.listBoxCustomerDocuments.setHeight(borderlayoutHeights - 130 + "px");
			this.listBoxCustomerAddress.setHeight(borderlayoutHeights - 145 + "px");
			this.listBoxCustomerPhoneNumbers.setHeight(borderlayoutHeights - 145 + "px");
			this.listBoxCustomerEmails.setHeight(borderlayoutHeights - 145 + "px");
			this.listBoxCustomerBankInformation.setHeight(borderlayoutHeights - 130 + "px");
			this.listBoxCustomerChequeInformation.setHeight(borderlayoutHeights - 130 + "px");
			this.listBoxCustomerFinExposure.setHeight(borderlayoutHeights - 130 + "px");
			this.listBoxCustomerExternalLiability.setHeight(borderlayoutHeights - 130 + "px");

			doCheckRights();
			doSetFieldProperties();
			doStoreInitValues();
			doShowDialog(getCustomerDetails());
		} catch (Exception e) {
			createException(window_FinanceCustomerList, e);
			logger.error(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.custCoreBank.setMaxlength(50);
		this.custShrtName.setMaxlength(50);
		this.custFirstName.setMaxlength(50);
		this.custMiddleName.setMaxlength(50);
		this.custLastName.setMaxlength(50);
		this.custArabicName.setMaxlength(50);
		this.custCtgCode.setMaxlength(8);
		this.custCtgCode.getTextbox().setWidth("152px");
		this.custCtgCode.setMandatoryStyle(true);
		this.custCtgCode.setModuleName("CustomerCategory");
		this.custCtgCode.setValueColumn("CustCtgCode");
		this.custCtgCode.setDescColumn("CustCtgDesc");
		this.custCtgCode.setValidateColumns(new String[] { "CustCtgCode" });

		this.custDftBranch.setMaxlength(8);
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

		this.custBaseCcy.setMaxlength(3);
		this.custBaseCcy.getTextbox().setWidth("121px");
		this.custBaseCcy.setMandatoryStyle(true);
		this.custBaseCcy.setModuleName("Currency");
		this.custBaseCcy.setValueColumn("CcyCode");
		this.custBaseCcy.setDescColumn("CcyDesc");
		this.custBaseCcy.setValidateColumns(new String[] { "CcyCode" });

		this.custNationality.setMaxlength(2);
		this.custNationality.getTextbox().setWidth("121px");
		this.custNationality.setMandatoryStyle(true);
		this.custNationality.setModuleName("NationalityCode");
		this.custNationality.setValueColumn("NationalityCode");
		this.custNationality.setDescColumn("NationalityDesc");
		this.custNationality.setValidateColumns(new String[] { "NationalityCode" });

		this.custDOB.setFormat(PennantConstants.dateFormat);

		//Customer Employee Field Properties
		this.empStatus.setMaxlength(8);
		this.empStatus.getTextbox().setWidth("121px");
		this.empStatus.setMandatoryStyle(true);
		this.empStatus.setModuleName("EmpStsCode");
		this.empStatus.setValueColumn("EmpStsCode");
		this.empStatus.setDescColumn("EmpStsDesc");
		this.empStatus.setValidateColumns(new String[] { "EmpStsCode" });
		
		this.empSector.setMaxlength(8);
		this.empSector.getTextbox().setWidth("120px");
		this.empSector.setModuleName("Sector");
		this.empSector.setValueColumn("SectorCode");
		this.empSector.setDescColumn("SectorDesc");
		this.empSector.setValidateColumns(new String[] { "SectorCode" });
		
		this.profession.setMaxlength(8);
		this.profession.setMandatoryStyle(true);
		this.profession.setModuleName("Profession");
		this.profession.setValueColumn("ProfessionCode");
		this.profession.setDescColumn("ProfessionDesc");
		this.profession.setValidateColumns(new String[] { "ProfessionCode" });
		
		this.monthlyIncome.setMandatory(true);
		this.monthlyIncome.setMaxlength(18);
		this.monthlyIncome.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));

		this.additionalIncome.setMaxlength(18);
		this.additionalIncome.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		
		this.empFrom.setFormat(PennantConstants.dateFormat);
		
		this.empName.setInputAllowed(false);
		this.empName.setDisplayStyle(3);
		this.empName.getTextbox().setWidth("121px");
		this.empName.setMandatoryStyle(true);
		this.empName.setModuleName("EmployerDetail");
		this.empName.setValueColumn("EmployerId");
		this.empName.setDescColumn("EmpName");
		this.empName.setValidateColumns(new String[] { "EmployerId" });
		
		this.empDesg.setMaxlength(8);
		this.empDesg.getTextbox().setWidth("121px");
		this.empDesg.setMandatoryStyle(true);
		this.empDesg.setModuleName("Designation");
		this.empDesg.setValueColumn("DesgCode");
		this.empDesg.setDescColumn("DesgDesc");
		this.empDesg.setValidateColumns(new String[] { "DesgCode" });
		
		this.empDept.setMaxlength(8);
		this.empDept.getTextbox().setWidth("121px");
		this.empDept.setMandatoryStyle(true);
		this.empDept.setModuleName("Department");
		this.empDept.setValueColumn("DeptCode");
		this.empDept.setDescColumn("DeptDesc");
		this.empDept.setValidateColumns(new String[] { "DeptCode" });
		
		this.otherIncome.setMaxlength(8);
		this.otherIncome.getTextbox().setWidth("121px");
		this.otherIncome.setModuleName("IncomeType");
		this.otherIncome.setValueColumn("IncomeTypeCode");
		this.otherIncome.setDescColumn("IncomeTypeDesc");
		this.otherIncome.setValidateColumns(new String[] { "IncomeTypeCode" });
		this.otherIncome.setFilters(new Filter[]{new Filter("IncomeExpense",PennantConstants.INCOME,Filter.OP_EQUAL)});
		
		logger.debug("Leaving");
	}
	

	private void dowriteBeanToComponents(CustomerDetails aCustomerDetails) {
		logger.debug("Entering");
		Customer aCustomer = aCustomerDetails.getCustomer();
		fillComboBox(this.custGenderCode, aCustomer.getCustGenderCode(), PennantAppUtil.getGenderCodes(), "");
		fillComboBox(this.custSalutationCode, aCustomer.getCustSalutationCode(), PennantAppUtil.getSalutationCodes(aCustomer.getCustGenderCode()), "");
		fillComboBox(this.custMaritalSts, aCustomer.getCustMaritalSts(), PennantAppUtil.getMaritalStsTypes(), "");
		fillComboBox(this.target, aCustomer.getCustAddlVar82(), PennantStaticListUtil.getCustTargetValues(), "");
		this.custCIF.setValue(StringUtils.trimToEmpty(aCustomer.getCustCIF()));
		this.custCoreBank.setValue(aCustomer.getCustCoreBank());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.custFirstName.setValue(aCustomer.getCustFName());
		this.custMiddleName.setValue(aCustomer.getCustMName());
		this.custLastName.setValue(aCustomer.getCustLName());
		this.custArabicName.setValue(aCustomer.getCustShrtNameLclLng());
		this.custCtgCode.setValue(aCustomer.getCustCtgCode());
		this.custDftBranch.setValue(aCustomer.getCustDftBranch());
		this.custTypeCode.setValue(aCustomer.getCustTypeCode());
		this.custBaseCcy.setValue(aCustomer.getCustBaseCcy());

		String custCRCPR = StringUtils.trimToEmpty(aCustomer.getCustCRCPR());
		if (custCRCPR.equals("") && aCustomerDetails.getCustomerDocumentsList()!=null && !aCustomerDetails.getCustomerDocumentsList().isEmpty()) {
			if (corpCustomer && isLocalCountry) {
				for (CustomerDocument customerDocument : aCustomerDetails.getCustomerDocumentsList()) {
					if (customerDocument.getCustDocCategory().equals(PennantConstants.BAHRAINI_CR)) {
						String cr=StringUtils.trimToEmpty(customerDocument.getCustDocTitle());
						getCustomerDetails().getCustomer().setCustCRCPR(cr);
						break;
					}
				}
			}else{
				for (CustomerDocument customerDocument : aCustomerDetails.getCustomerDocumentsList()) {
					if (customerDocument.getCustDocCategory().equals(PennantConstants.CPRCODE) || 
							customerDocument.getCustDocCategory().equals(PennantConstants.NON_BAHRAINI_INTERNATIONAL_CR) || 
							customerDocument.getCustDocCategory().equals(PennantConstants.BAHRAINI_CR) ) {
						getCustomerDetails().getCustomer().setCustCRCPR(StringUtils.trimToEmpty(customerDocument.getCustDocTitle()));
						break;
					}
				}
			}
		}
		this.custNationality.setValue(aCustomer.getCustNationality());
		this.custDOB.setValue(aCustomer.getCustDOB());
		this.noOfDependents.setValue(aCustomer.getNoOfDependents());

		this.custCtgCode.setDescription(StringUtils.trimToEmpty(aCustomer.getCustCtgCode()).equals("") ? "" : aCustomer.getLovDescCustCtgCodeName());
		this.custDftBranch.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustDftBranchName()).equals("") ? "" : aCustomer.getLovDescCustDftBranchName());
		this.custTypeCode.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustTypeCodeName()).equals("") ? "" : aCustomer.getLovDescCustTypeCodeName());
		this.custBaseCcy.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustBaseCcyName()).equals("") ? "" : aCustomer.getLovDescCustBaseCcyName());
		this.custNationality.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustNationalityName()).equals("") ? "" : aCustomer.getLovDescCustNationalityName());
		this.salariedCustomer.setChecked(aCustomer.isSalariedCustomer());

		doSetCustTypeFilters(aCustomer.getLovDescCustCtgType());
		//+++++++++++++++++++++++++++++++++++++++++++++++++++

		//Set Customer Employee Details
		CustEmployeeDetail custEmployeeDetail = aCustomerDetails.getCustEmployeeDetail();
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
		this.empNameOther.setValue(custEmployeeDetail.getEmpNameForOthers());
		this.empFrom.setValue(custEmployeeDetail.getEmpFrom());
		this.empDesg.setValue(custEmployeeDetail.getEmpDesg());
		this.empDesg.setDescription(custEmployeeDetail.getLovDescEmpDesg());
		this.empDept.setValue(custEmployeeDetail.getEmpDept());
		this.empDept.setDescription(custEmployeeDetail.getLovDescEmpDept());
		this.monthlyIncome.setValue(PennantAppUtil.formateAmount(custEmployeeDetail.getMonthlyIncome(),finFormatter));
		this.otherIncome.setValue(custEmployeeDetail.getOtherIncome());
		this.otherIncome.setDescription(custEmployeeDetail.getLovDescOtherIncome());
		this.additionalIncome.setValue(PennantAppUtil.formateAmount(custEmployeeDetail.getAdditionalIncome(),finFormatter));
		
		//Filling KYC Details
		doFillDocumentDetails(aCustomerDetails.getCustomerDocumentsList());
		doFillCustomerAddressDetails(aCustomerDetails.getAddressList());
		doFillCustomerPhoneNumberDetails(aCustomerDetails.getCustomerPhoneNumList());
		doFillCustomerEmailDetails(aCustomerDetails.getCustomerEMailList());
		//Filling Banking Details
		doFillCustomerBankInfoDetails(aCustomerDetails.getCustomerBankInfoList());
		doFillCustomerChequeInfoDetails(aCustomerDetails.getCustomerChequeInfoList());
		doFillCustomerExtLiabilityDetails(aCustomerDetails.getCustomerExtLiabilityList());
		doFillCustFinanceExposureDetails(aCustomerDetails.getCustFinanceExposureList());

		if(StringUtils.trimToEmpty(this.empName.getDescription()).equalsIgnoreCase(EmploymentName_OTHERS)){
			this.hbox_empNameOther.setVisible(true);
			this.label_empNameOther.setVisible(true);
		}else{
			this.hbox_empNameOther.setVisible(false);
			this.label_empNameOther.setVisible(false);
		}
		empName_Temp = this.empName.getValue();
		logger.debug("Leaving");
	}

	public void doWriteComponentsToBean(CustomerDetails aCustomerDetails,Tab custTab) throws ParseException {
		logger.debug("Entering");
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
			aCustomer.setCustShrtName(this.custShrtName.getValue());
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
			aCustomer.setCustShrtNameLclLng(this.custArabicName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustCtgCodeName(this.custCtgCode.getDescription());
			aCustomer.setCustCtgCode(this.custCtgCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustDftBranchName(this.custDftBranch.getDescription());			
			if(this.custDftBranch.getValue().equals("")) {
				wve.add(new WrongValueException(this.custDftBranch, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_CustomerDialog_CustDftBranch.value") })));
			} else {
				aCustomer.setCustDftBranch(this.custDftBranch.getValidatedValue());
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustTypeCodeName(this.custTypeCode.getDescription());
			aCustomer.setCustTypeCode(this.custTypeCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setLovDescCustBaseCcyName(this.custBaseCcy.getDescription());		
			if(this.custBaseCcy.getValue().equals("")) {
				wve.add(new WrongValueException(this.custBaseCcy, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_CustomerDialog_CustBaseCcy.value") })));
			} else {
				aCustomer.setCustBaseCcy(this.custBaseCcy.getValidatedValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setLovDescCustNationalityName(this.custNationality.getDescription());
			aCustomer.setCustNationality(this.custNationality.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setCustDOB(new Timestamp(this.custDOB.getValue().getTime()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.custSalutationCode.isVisible()){
				if(getComboboxValue(this.custSalutationCode).equals("#")) {
					throw new WrongValueException(this.custSalutationCode, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_CustomerDialog_CustSalutationCode.value") }));
				}
				aCustomer.setCustSalutationCode(getComboboxValue(this.custSalutationCode));
			}else{
				aCustomer.setCustSalutationCode(null);
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			if(this.target.isVisible()){
				if(getComboboxValue(this.target).equals("#")) {
					throw new WrongValueException(this.target, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinanceCustomerList_Target.value") }));
				}
				aCustomer.setCustAddlVar82(getComboboxValue(this.target));
			}else{
				aCustomer.setCustAddlVar82(null);

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.custGenderCode.isVisible()){
				if(getComboboxValue(this.custGenderCode).equals("#")) {
					throw new WrongValueException(this.custGenderCode, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_CustomerDialog_CustGenderCode.value") }));
				}
				aCustomer.setCustGenderCode(getComboboxValue(this.custGenderCode));
			}else{
				aCustomer.setCustGenderCode(null);

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.custMaritalSts.isVisible()){
				if(getComboboxValue(this.custMaritalSts).equals("#")) {
					throw new WrongValueException(this.custMaritalSts, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_CustomerDialog_CustMaritalSts.value") }));
				}
				aCustomer.setCustMaritalSts(getComboboxValue(this.custMaritalSts));
			}else{
				aCustomer.setCustMaritalSts(null);
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

		showErrorDetails(wve,custTab,basicDetails);
		
		//Set Customer Employee Details
		
		CustEmployeeDetail custEmployeeDetail = aCustomerDetails.getCustEmployeeDetail();
		try {
			custEmployeeDetail.setLovDescEmpStatus(this.empStatus.getDescription());
			custEmployeeDetail.setEmpStatus(this.empStatus.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			custEmployeeDetail.setLovDescEmpSector(this.empSector.getDescription());
			custEmployeeDetail.setEmpSector(this.empSector.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			custEmployeeDetail.setLovDescProfession(this.profession.getDescription());
			custEmployeeDetail.setProfession(this.profession.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			custEmployeeDetail.setLovDescEmpName(this.empName.getDescription());
			custEmployeeDetail.setEmpName(this.empName.getValidatedValue().equals("")?0:Long.valueOf(this.empName.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			custEmployeeDetail.setEmpNameForOthers(this.empNameOther.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(this.empFrom.getValue() != null){
				if (!this.empFrom.getValue().after(((Date) SystemParameterDetails
						.getSystemParameterValue("APP_DFT_START_DATE")))) {
					throw new WrongValueException(this.empFrom,Labels.getLabel("DATE_ALLOWED_AFTER",
							new String[] {Labels.getLabel("label_FinanceCustomerList_EmpFrom.value"),
							SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE").toString() }));
				}
				if (this.custDOB.getValue() != null && !this.empFrom.getValue().after(this.custDOB.getValue())) {
					throw new WrongValueException(this.empFrom,Labels.getLabel("DATE_ALLOWED_AFTER",
							new String[] {Labels.getLabel("label_FinanceCustomerList_EmpFrom.value"),
							Labels.getLabel("label_FinanceCustomerList_CustDOB.value") }));
				}
				if (this.empFrom.getValue().compareTo(((Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR))) != -1) {
					throw new WrongValueException(this.empFrom, Labels.getLabel("DATE_FUTURE_TODAY", new String[] { Labels.getLabel("label_FinanceCustomerList_EmpFrom.value"), SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE").toString() }));
				}
				custEmployeeDetail.setEmpFrom(this.empFrom.getValue());
			}else{
				custEmployeeDetail.setEmpFrom(null);
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			custEmployeeDetail.setLovDescEmpDesg(this.empDesg.getDescription());
			custEmployeeDetail.setEmpDesg(this.empDesg.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			custEmployeeDetail.setLovDescEmpDept(this.empDept.getDescription());
			custEmployeeDetail.setEmpDept(this.empDept.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			custEmployeeDetail.setMonthlyIncome(PennantAppUtil.unFormateAmount(this.monthlyIncome.getValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			custEmployeeDetail.setLovDescOtherIncome(this.otherIncome.getDescription());
			custEmployeeDetail.setOtherIncome(this.otherIncome.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			custEmployeeDetail.setAdditionalIncome(PennantAppUtil.unFormateAmount(this.additionalIncome.getValue(),finFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		showErrorDetails(wve,custTab,tabkYCDetails);
		
		aCustomerDetails.setCustomer(aCustomer);
		//Set KYC details
		aCustomerDetails.setCustomerDocumentsList(this.customerDocumentDetailList);
		aCustomerDetails.setAddressList(this.customerAddressDetailList);
		aCustomerDetails.setCustomerPhoneNumList(this.customerPhoneNumberDetailList);
		aCustomerDetails.setCustomerEMailList(this.customerEmailDetailList);
		//Set Banking details
		aCustomerDetails.setCustomerBankInfoList(this.customerBankInfoDetailList);
		aCustomerDetails.setCustomerChequeInfoList(this.customerChequeInfoDetailList);
		aCustomerDetails.setCustomerExtLiabilityList(this.customerExtLiabilityDetailList);
		logger.debug("Leaving");
	}


	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab parentTab,Tab childTab) {
		logger.debug("Entering");
		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			parentTab.setSelected(true);
			childTab.setSelected(true);
			doRemoveValidation();
			doRemoveLOVValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	private void doSetCustTypeFilters(String custCtgType){
		if (!StringUtils.trimToEmpty(custCtgType).equals("")) {
			Filter filter[]=new Filter[1];
			filter[0]=new Filter("CustTypeCtg", StringUtils.trimToEmpty(custCtgType), Filter.OP_EQUAL);
			this.custTypeCode.setFilters(filter);				
		}
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		
		Date appStartDate=(Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
		Date startDate = (Date)SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE");

		doClearErrorMessage();
		setValidationOn(true);
		if (!this.custShrtName.isReadonly()) {
			this.custShrtName.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCustomerList_CustShrtName.value"),
					PennantRegularExpressions.REGEX_CUST_NAME, true));
		}
		if (!this.custFirstName.isReadonly()) {
			this.custFirstName.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCustomerList_CustFirstName.value"),
					PennantRegularExpressions.REGEX_CUST_NAME, true));
		}

		if (!this.custArabicName.isReadonly()) {
			this.custArabicName.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCustomerList_CustArabicName.value"),
					PennantRegularExpressions.REGEX_CUST_NAME, true));
		}
		if (!this.custDOB.isReadonly() && !this.custDOB.isDisabled()) {
			if (corpCustomer) {
				this.custDOB.setConstraint(new PTDateValidator(Labels.getLabel("label_FinanceCustomerList_CustDateOfIncorporation.value"),true,startDate,appStartDate,false));
			}else{
				this.custDOB.setConstraint(new PTDateValidator(Labels.getLabel("label_FinanceCustomerList_CustDOB.value"),true,startDate,appStartDate,false));
			}
		}
		//Employee 
		if (!this.empFrom.isReadonly()) {
			this.empFrom.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCustomerList_EmpFrom.value"), null, true));
		}
		if (!this.monthlyIncome.isDisabled()) {
			this.monthlyIncome.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_FinanceCustomerList_MonthlyIncome.value"), false));
		}
		if (!StringUtils.trimToEmpty(this.otherIncome.getValue()).equals("")  &&  !this.additionalIncome.isDisabled()) {
			this.additionalIncome.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_FinanceCustomerList_AdditionalIncome.value"), false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the
	 * LOVfields.
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		if(this.custCtgCode.isButtonVisible()){
			this.custCtgCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCustomerList_CustCtgCode.value"), null, true,true));
		}
		if(!this.custDftBranch.isReadonly()){
			this.custDftBranch.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCustomerList_CustDftBranch.value"), null, true,true));
		}
		if(!this.custTypeCode.isReadonly()){
			this.custTypeCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCustomerList_CustTypeCode.value"), null, true,true));
		}
		if(!this.custBaseCcy.isReadonly()){
			this.custBaseCcy.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCustomerList_CustBaseCcy.value"), null, true,true));
		}
		if(!this.custNationality.isReadonly()){
			this.custNationality.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCustomerList_CustNationality.value"), null, true,true));
		}
		if (!corpCustomer) {
			if(!this.custSalutationCode.isReadonly()){
				this.custSalutationCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCustomerList_CustSalutationCode.value"), null, true));
			}
			this.custGenderCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCustomerList_CustGenderCode.value"), null, true));
			this.custMaritalSts.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCustomerList_CustMaritalSts.value"), null, true));
		}

		//Employee 
		if(!this.empStatus.isReadonly()){
			this.empStatus.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCustomerList_EmpStatus.value"), null, true,true));
		}
		if(!this.empName.getButton().isDisabled()){
			this.empName.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCustomerList_EmpName.value"), null, true,true));
		}
		if(this.hbox_empNameOther.isVisible() && !this.empNameOther.isReadonly()){
			this.empNameOther.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCustomerList_EmpNameOther.value"), null, true,false));
		}
		if(!this.empDesg.isReadonly()){
			this.empDesg.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCustomerList_EmpDesg.value"), null, true,true));
		}
		if(!this.empDept.isReadonly()){
			this.empDept.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCustomerList_EmpDept.value"), null, true,true));
		}
		if(this.profession.isVisible() && !this.profession.isReadonly()){
			this.profession.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCustomerList_Profession.value"), null, true,true));
		}
		logger.debug("Leaving");
	}


	private void doStoreInitValues() {
		logger.debug("Entering");
		if (getCustomerDocumentDetailList()!= null) {
			this.oldVar_customerDocumentDetailList.addAll(getCustomerDocumentDetailList());
		}
		if (getCustomerAddressDetailList()!= null) {
			this.oldVar_customerAddressDetailList.addAll(getCustomerAddressDetailList());
		}
		if (getCustomerPhoneNumberDetailList()!= null) {
			this.oldVar_customerPhoneNumberDetailList.addAll(getCustomerPhoneNumberDetailList());
		}
		if (getCustomerEmailDetailList()!= null) {
			this.oldVar_customerEmailDetailList.addAll(getCustomerEmailDetailList());
		}
		if (getCustomerBankInfoDetailList()!= null) {
			this.oldVar_customerBankInfoDetailList.addAll(getCustomerBankInfoDetailList());
		}
		if (getCustomerChequeInfoDetailList()!= null) {
			this.oldVar_customerChequeInfoDetailList.addAll(getCustomerChequeInfoDetailList());
		}
		if (getCustomerExtLiabilityDetailList()!= null) {
			this.oldVar_customerExtLiabilityDetailList.addAll(getCustomerExtLiabilityDetailList());
		}
		this.oldVar_custShrtName = this.custShrtName.getValue();
		this.oldVar_custCoreBank = this.custCoreBank.getValue();
		this.oldVar_custFirstName = this.custFirstName.getValue();
		this.oldVar_custMiddleName = this.custMiddleName.getValue();
		this.oldVar_custLastName = this.custLastName.getValue();
		this.oldVar_custArabicName = this.custArabicName.getValue();
		this.oldVar_custCtgCode = this.custCtgCode.getValue();
		this.oldVar_custDftBranch = this.custDftBranch.getValue();
		this.oldVar_custTypeCode = this.custTypeCode.getValue();
		this.oldVar_custBaseCcy = this.custBaseCcy.getValue();
		this.oldVar_custNationality = this.custNationality.getValue();
		this.oldVar_custDOB = this.custDOB.getValue();
		this.oldVar_custSalutationCode = this.custSalutationCode.getValue();
		this.oldVar_custGenderCode = this.custGenderCode.getValue();
		this.oldVar_custMaritalSts = this.custMaritalSts.getValue();
		//Employee Details
		this.oldVar_empStatus = this.empStatus.getValue();
		this.oldVar_empSector = this.empSector.getValue();
		this.oldVar_profession = this.profession.getValue();
		this.oldVar_empName = this.empName.getValue();
		this.oldVar_empNameOther = this.empNameOther.getValue();
		this.oldVar_empFrom = this.empFrom.getValue();
		this.oldVar_empDesg = this.empDesg.getValue();
		this.oldVar_empDept = this.empDept.getValue();
		this.oldVar_monthlyIncome = this.monthlyIncome.getValue();
		this.oldVar_otherIncome = this.otherIncome.getValue();
		this.oldVar_additionalIncome = this.additionalIncome.getValue();
		
		logger.debug("leaving");
	}

	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("FinanceCustomerList",roleCode);
		this.btnNew_CustomerDocuments.setVisible(getUserWorkspace().isAllowed("button_FinanceCustomerList_btnNew_CustomerDocuments"));
		this.btnNew_CustomerAddress.setVisible(getUserWorkspace().isAllowed("button_FinanceCustomerList_btnNew_CustomerAddress"));
		this.btnNew_CustomerPhoneNumber.setVisible(getUserWorkspace().isAllowed("button_FinanceCustomerList_btnNew_CustomerPhoneNumber"));
		this.btnNew_CustomerEmail.setVisible(getUserWorkspace().isAllowed("button_FinanceCustomerList_btnNew_CustomerEmail"));
		this.btnNew_BankInformation.setVisible(getUserWorkspace().isAllowed("button_FinanceCustomerList_btnNew_BankInformation"));
		this.btnNew_ChequeInformation.setVisible(getUserWorkspace().isAllowed("button_FinanceCustomerList_btnNew_ChequeInformation"));
		this.btnNew_ExternalLiability.setVisible(getUserWorkspace().isAllowed("button_FinanceCustomerList_btnNew_ExternalLiability"));
		logger.debug("leaving");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerDetails
	 * @throws InterruptedException
	 */
	@SuppressWarnings("rawtypes")
	public void doShowDialog(CustomerDetails aCustomerDetails) throws InterruptedException {
		logger.debug("Entering");
		// if aFinanceCustomer == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aCustomerDetails == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aCustomerDetails = new CustomerDetails();
			setCustomerDetails(aCustomerDetails);
		} else {
			if(aCustomerDetails.getCustomer() == null){
				aCustomerDetails.setCustomer(new Customer());
			}
			setCustomerDetails(aCustomerDetails);
		}
		if(aCustomerDetails.getCustEmployeeDetail() == null){
			CustEmployeeDetail custEmployeeDetail = new CustEmployeeDetail();
			aCustomerDetails.setCustEmployeeDetail(custEmployeeDetail);
		}
		doEdit();
		dowriteBeanToComponents(aCustomerDetails);
		doStoreInitValues();
		doCheckEnquiry();

		try {
			Class[] paramType = {this.getClass() };
			Object[] stringParameter = { this };
			financeMainDialogCtrl.getClass().getMethod("setFinanceCustomerListCtrl", paramType).invoke(financeMainDialogCtrl, stringParameter);
		} catch (Exception e) {
			logger.error(e);
		}

		try {
			// fill the components with the data
			// stores the initial data for comparing if they are changed
			// during user action.
			if (panel != null) {
				this.window_FinanceCustomerList.setHeight(borderLayoutHeight - 75 + "px");
				panel.appendChild(this.window_FinanceCustomerList);
			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}


	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		this.custCoreBank.setReadonly(true);
		this.custCtgCode.setReadonly(true);
		
		this.custShrtName.setReadonly(isReadOnly("FinanceCustomerListCtrl_custShrtName"));
		this.custFirstName.setReadonly(isReadOnly("FinanceCustomerListCtrl_custFirstName"));
		this.custMiddleName.setReadonly(isReadOnly("FinanceCustomerListCtrl_custMiddleName"));
		this.custLastName.setReadonly(isReadOnly("FinanceCustomerListCtrl_custLastName"));
		this.custArabicName.setReadonly(isReadOnly("FinanceCustomerListCtrl_custArabicName"));
		this.custDftBranch.setReadonly(isReadOnly("FinanceCustomerListCtrl_custDftBranch"));
		this.custBaseCcy.setReadonly(isReadOnly("FinanceCustomerListCtrl_custBaseCcy"));
		this.custTypeCode.setReadonly(isReadOnly("FinanceCustomerListCtrl_custTypeCode"));
		this.custNationality.setReadonly(isReadOnly("FinanceCustomerListCtrl_custNationality"));
		this.custMaritalSts.setDisabled(isReadOnly("FinanceCustomerListCtrl_custMaritalSts"));
		this.custSalutationCode.setDisabled(isReadOnly("FinanceCustomerListCtrl_custSalutationCode"));
		this.custDOB.setDisabled(isReadOnly("FinanceCustomerListCtrl_custDOB"));
		this.custGenderCode.setDisabled(isReadOnly("FinanceCustomerListCtrl_custGenderCode"));
		this.noOfDependents.setReadonly(isReadOnly("FinanceCustomerListCtrl_noOfDependents"));
		this.target.setDisabled(isReadOnly("FinanceCustomerListCtrl_target"));
		this.custCtgCode.setReadonly(true); //Not allowing user to modify this field
		this.salariedCustomer.setDisabled(isReadOnly("FinanceCustomerListCtrl_salariedCustomer"));
        //Employee Details
		this.empStatus.setReadonly(isReadOnly("FinanceCustomerListCtrl_empStatus"));
		this.empSector.setReadonly(isReadOnly("FinanceCustomerListCtrl_empSector"));
		this.profession.setReadonly(isReadOnly("FinanceCustomerListCtrl_profession"));
		this.empName.setReadonly(isReadOnly("FinanceCustomerListCtrl_empName"));
		this.empNameOther.setReadonly(isReadOnly("FinanceCustomerListCtrl_empNameOther"));
		this.empFrom.setDisabled(isReadOnly("FinanceCustomerListCtrl_empFrom"));
		this.empDesg.setReadonly(isReadOnly("FinanceCustomerListCtrl_empDesg"));
		this.empDept.setReadonly(isReadOnly("FinanceCustomerListCtrl_empDept"));
		this.monthlyIncome.setReadonly(isReadOnly("FinanceCustomerListCtrl_monthlyIncome"));
		this.otherIncome.setReadonly(isReadOnly("FinanceCustomerListCtrl_otherIncome"));
		this.additionalIncome.setReadonly(isReadOnly("FinanceCustomerListCtrl_additionalIncome"));
		
	}

	private void doCheckEnquiry() {
		if(isEnquiry){
			this.btnNew_CustomerAddress.setVisible(false);
			this.btnNew_CustomerDocuments.setVisible(false);
			this.btnNew_CustomerEmail.setVisible(false);
			this.btnNew_CustomerPhoneNumber.setVisible(false);
			this.btnNew_BankInformation.setVisible(false);
			this.btnNew_ChequeInformation.setVisible(false);
			this.btnNew_ExternalLiability.setVisible(false);
		}
	}

	/**
	 * Event for checking whethter data has been changed before closing
	 * 
	 * @param event
	 * @return
	 * */
	public void onAssetClose(Event event) {
		logger.debug("Entering" + event.toString());
		if (getFinanceMainDialogCtrl() != null) {
			try {
				doClearErrorMessage();
				financeMainDialogCtrl.getClass().getMethod("setAssetDataChanged", Boolean.class).invoke(financeMainDialogCtrl, isDataChanged());
			} catch (Exception e) {
				logger.error(e);
			}
		}
		logger.debug("Leaving" + event.toString());
	}


	private boolean isDataChanged() {
		
		if (oldVar_customerDocumentDetailList != null) {
			if(oldVar_customerDocumentDetailList.size()>0 && customerDocumentDetailList.size() > 0){
				if (oldVar_customerDocumentDetailList != customerDocumentDetailList) {
					return true;
				}
			}
		}
		if (oldVar_customerAddressDetailList != null) {
			if(oldVar_customerAddressDetailList.size()>0 && customerAddressDetailList.size() > 0){
				if (oldVar_customerAddressDetailList != customerAddressDetailList) {
					return true;
				}
			}
		}
		if (oldVar_customerPhoneNumberDetailList != null) {
			if(oldVar_customerPhoneNumberDetailList.size()>0 && customerPhoneNumberDetailList.size() > 0){
				if (oldVar_customerPhoneNumberDetailList != customerPhoneNumberDetailList) {
					return true;
				}
			}
		}
		if (oldVar_customerEmailDetailList != null) {
			if(oldVar_customerEmailDetailList.size()>0 && customerEmailDetailList.size() > 0){
				if (oldVar_customerEmailDetailList != customerEmailDetailList) {
					return true;
				}
			}
		}
		if (oldVar_customerBankInfoDetailList != null) {
			if(oldVar_customerBankInfoDetailList.size()>0 && customerBankInfoDetailList.size() > 0){
				if (oldVar_customerBankInfoDetailList != customerBankInfoDetailList) {
					return true;
				}
			}
		}
		if (oldVar_customerChequeInfoDetailList != null) {
			if(oldVar_customerChequeInfoDetailList.size()>0 && customerChequeInfoDetailList.size() > 0){
				if (oldVar_customerChequeInfoDetailList != customerChequeInfoDetailList) {
					return true;
				}
			}
		}
		if (oldVar_customerExtLiabilityDetailList != null) {
			if(oldVar_customerExtLiabilityDetailList.size()>0 && customerExtLiabilityDetailList.size() > 0){
				if (oldVar_customerExtLiabilityDetailList != customerExtLiabilityDetailList) {
					return true;
				}
			}
		}
		if (this.oldVar_custCoreBank != this.custCoreBank.getValue()) {
			return true;
		}
		if (this.oldVar_custTypeCode != this.custTypeCode.getValue()) {
			return true;
		}
		if (this.oldVar_custDftBranch != this.custDftBranch.getValue()) {
			return true;
		}
		if (this.oldVar_custShrtName != this.custShrtName.getValue()) {
			return true;
		}
		if (this.oldVar_custFirstName != this.custFirstName.getValue()) {
			return true;
		}
		if (this.oldVar_custMiddleName != this.custMiddleName.getValue()) {
			return true;
		}
		if (this.oldVar_custLastName != this.custLastName.getValue()) {
			return true;
		}
		if (this.oldVar_custArabicName != this.custArabicName.getValue()) {
			return true;
		}
		if (this.oldVar_custMaritalSts != this.custMaritalSts.getValue()) {
			return true;
		}
		if (this.oldVar_custBaseCcy != this.custBaseCcy.getValue()) {
			return true;
		}

		if (this.oldVar_custGenderCode != this.custGenderCode.getValue()) {
			return true;
		}
		if (this.oldVar_custSalutationCode != this.custSalutationCode.getValue()) {
			return true;
		}
		if (this.oldVar_custShrtName != this.custShrtName.getValue()) {
			return true;
		}
		String oldCustDOB = "";
		String newCustDOB = "";
		if (this.oldVar_custDOB != null) {
			oldCustDOB = DateUtility.formatDate(this.oldVar_custDOB, PennantConstants.dateFormat);
		}
		if (this.custDOB.getValue() != null) {
			newCustDOB = DateUtility.formatDate(this.custDOB.getValue(), PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldCustDOB).equals(StringUtils.trimToEmpty(newCustDOB))) {
			return true;
		}
		
		if (this.oldVar_custCtgCode != this.custCtgCode.getValue()) {
			return true;
		}
		
		if (this.oldVar_custNationality != this.custNationality.getValue()) {
			return true;
		}
		//Employee Details
		if (this.oldVar_empStatus != this.empStatus.getValue()) {
			return true;
		}
		if (this.oldVar_empSector != this.empSector.getValue()) {
			return true;
		}
		if (this.oldVar_profession != this.profession.getValue()) {
			return true;
		}
		if (this.oldVar_empName != this.empName.getValue()) {
			return true;
		}
		if (this.oldVar_empNameOther != this.empNameOther.getValue()) {
			return true;
		}
		if (this.oldVar_empFrom != this.empFrom.getValue()) {
			return true;
		}
		if (this.oldVar_empDesg != this.empDesg.getValue()) {
			return true;
		}
		if (this.oldVar_empDept != this.empDept.getValue()) {
			return true;
		}
		if (this.oldVar_monthlyIncome != this.monthlyIncome.getValue()) {
			return true;
		}
		if (this.oldVar_otherIncome != this.otherIncome.getValue()) {
			return true;
		}
		if (this.oldVar_additionalIncome != this.additionalIncome.getValue()) {
			return true;
		}
		return false;
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
		this.custDOB.setConstraint("");
		this.empFrom.setConstraint("");
		this.monthlyIncome.setConstraint("");
		this.additionalIncome.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Removes the Validation by setting the accordingly constraints to the
	 * LOVfields.
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
		this.custMaritalSts.setConstraint("");
		this.empStatus.setConstraint("");
		this.empName.setConstraint("");
		this.empNameOther.setConstraint("");
		this.empDesg.setConstraint("");
		this.empDept.setConstraint("");
		this.profession.setConstraint("");
		logger.debug("Leaving");
	}


	private void doClearErrorMessage() {
		logger.debug("Enterring");
		this.custCIF.setErrorMessage("");
		this.custCoreBank.setErrorMessage("");
		this.custCtgCode.setErrorMessage("");
		this.custTypeCode.setErrorMessage("");
		this.custSalutationCode.setErrorMessage("");
		this.custShrtName.setErrorMessage("");
		this.custFirstName.setErrorMessage("");
		this.custMiddleName.setErrorMessage("");
		this.custLastName.setErrorMessage("");
		this.custArabicName.setErrorMessage("");
		this.custDftBranch.setErrorMessage("");
		this.custGenderCode.setErrorMessage("");
		this.custDOB.setErrorMessage("");
		this.custBaseCcy.setErrorMessage("");
		this.custNationality.setErrorMessage("");
		this.custDftBranch.setErrorMessage("");
		this.custSalutationCode.setErrorMessage("");
		this.custCtgCode.setErrorMessage("");
		this.custMaritalSts.setErrorMessage("");
		this.custMaritalSts.setErrorMessage("");
		this.empStatus.setErrorMessage("");
		this.empName.setErrorMessage("");
		this.empNameOther.setErrorMessage("");
		this.empDesg.setErrorMessage("");
		this.empDept.setErrorMessage("");
		this.empFrom.setErrorMessage("");
		this.monthlyIncome.setErrorMessage("");
		this.additionalIncome.setErrorMessage("");
		this.profession.setErrorMessage("");
		logger.debug("Leaving");
	}



	public void onSelect$custGenderCode(Event event){
		logger.debug("Entering");
		if (!StringUtils.trimToEmpty(sCustGender).equals(
				this.custGenderCode.getValue())) {
			this.custSalutationCode.setValue("");
		}
		if (this.custGenderCode.getValue() != "") {
			this.custSalutationCode.setDisabled(false);
		} else {
			this.custSalutationCode.setDisabled(true);
		}
		sCustGender = this.custGenderCode.getValue();
		String genderCodeTemp = this.custGenderCode.getSelectedItem().getValue().toString();
		fillComboBox(this.custSalutationCode, this.custSalutationCode.getValue(), PennantAppUtil.getSalutationCodes(genderCodeTemp), "");
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
	public void onFulfill$empName(Event event) {
		logger.debug("Entering");
		if(StringUtils.trimToEmpty(this.empName.getDescription()).equalsIgnoreCase(EmploymentName_OTHERS)){
			this.hbox_empNameOther.setVisible(true);
			this.label_empNameOther.setVisible(true);
		}else{
			this.hbox_empNameOther.setVisible(false);
			this.label_empNameOther.setVisible(false);
		}
		if(!StringUtils.trimToEmpty(this.empName.getValue()).equalsIgnoreCase(empName_Temp)){
			this.empNameOther.setValue("");
		}
		empName_Temp = this.empName.getValue();
		logger.debug("Leaving");
	}
	

	public void onFulfill$empStatus(Event event) {
		logger.debug("Entering");
		
		Object dataObject = custCtgCode.getObject();
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
		if(!StringUtils.trimToEmpty(this.empStatus.getValue()).equals(empStatus_Temp)){
			doClearEmpDetails();
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
				this.finFormatter = details.getCcyEditField();
				doSetCurrencyFieldProperties();
			}
		}
		logger.debug("Leaving");
	}
	
	public void onFulfill$otherIncome(Event event) {
		logger.debug("Entering");
		if(StringUtils.trimToEmpty(this.otherIncome.getValue()).equals("")){
			this.additionalIncome.setMandatory(false);
		}else{
			this.additionalIncome.setMandatory(true);
		}
		logger.debug("Leaving");
	}
	
	private void doSetCurrencyFieldProperties(){
		logger.debug("Entering");
		this.monthlyIncome.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.additionalIncome.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		if(getCustomerChequeInfoDetailList() != null && !getCustomerChequeInfoDetailList().isEmpty()){
			for (CustomerChequeInfo customerChequeInfo : getCustomerChequeInfoDetailList()) {
				customerChequeInfo.setSalary(PennantAppUtil.unFormateAmount(PennantAppUtil.formateAmount(customerChequeInfo.getSalary(),finFormatter),finFormatter));
				customerChequeInfo.setReturnChequeAmt(PennantAppUtil.unFormateAmount(PennantAppUtil.formateAmount(customerChequeInfo.getReturnChequeAmt(),finFormatter),finFormatter));
				customerChequeInfo.setTotChequePayment(PennantAppUtil.unFormateAmount(PennantAppUtil.formateAmount(customerChequeInfo.getTotChequePayment(),finFormatter),finFormatter));	
			}
			doFillCustomerChequeInfoDetails(getCustomerChequeInfoDetailList());
		}
		logger.debug("Leaving");
	}
	
	
	private void doSetEmpStatusProperties(String status){
		logger.debug("Entering");
		this.empStatus.getTextbox().setWidth("121px");
		this.empSector.getTextbox().setWidth("120px");
		this.otherIncome.getTextbox().setWidth("121px");
		this.empDept.getTextbox().setWidth("121px");
		this.empDesg.getTextbox().setWidth("121px");
		if(StringUtils.trimToEmpty(this.empStatus.getValue()).equalsIgnoreCase(EmploymentStatus_SELFEMP)){
			//make profession visible true
			this.label_FinanceCustomerList_EmpFrom.setValue(Labels.getLabel("label_FinanceCustomerList_ProfessionStartDate.value"));
			this.label_FinanceCustomerList_MonthlyIncome.setValue(Labels.getLabel("label_FinanceCustomerList_MonthlyProfessionIncome.value"));
			this.label_FinanceCustomerList_EmpSector.setValue(Labels.getLabel("label_FinanceCustomerList_Profession.value"));
			this.label_FinanceCustomerList_EmpSector.setVisible(false);
			this.empSector.setVisible(false);
			this.label_FinanceCustomerList_Profession.setVisible(true);
			this.profession.setVisible(true);
			this.row_EmpName.setVisible(false);
			this.row_DesgDept.setVisible(false);
		}
		else if(StringUtils.trimToEmpty(this.empStatus.getValue()).equalsIgnoreCase(EmploymentStatus_BUSINESS)){
			this.label_FinanceCustomerList_EmpFrom.setValue(Labels.getLabel("label_FinanceCustomerList_BusinessStartDate.value"));
			this.label_FinanceCustomerList_MonthlyIncome.setValue(Labels.getLabel("label_FinanceCustomerList_AvgMonthlyTurnover.value"));
			this.label_FinanceCustomerList_EmpSector.setValue(Labels.getLabel("label_FinanceCustomerList_SMESector.value"));
			this.label_FinanceCustomerList_EmpSector.setVisible(true);
			this.empSector.setVisible(true);
			this.profession.setVisible(false);
			this.row_EmpName.setVisible(false);
			this.row_DesgDept.setVisible(false);
		}else{
			this.label_FinanceCustomerList_EmpFrom.setValue(Labels.getLabel("label_FinanceCustomerList_EmpFrom.value"));
			this.label_FinanceCustomerList_MonthlyIncome.setValue(Labels.getLabel("label_FinanceCustomerList_MonthlyIncome.value"));
			this.label_FinanceCustomerList_EmpSector.setValue(Labels.getLabel("label_FinanceCustomerList_EmpSector.value"));
			this.label_FinanceCustomerList_Profession.setVisible(false);
			this.label_FinanceCustomerList_EmpSector.setVisible(true);
			this.empSector.setVisible(true);
			this.profession.setVisible(false);
			this.row_EmpName.setVisible(true);
			this.row_DesgDept.setVisible(true);

		}

		logger.debug("Leaving");
	}
	
	
	private void doClearEmpDetails(){
		logger.debug("Entering");
		this.empSector.setValue("","");
		this.profession.setValue("","");
		this.empName.setValue("","");
		this.empNameOther.setValue("");
		this.empFrom.setText("");
		this.empDesg.setValue("","");
		this.empDept.setValue("","");
		this.monthlyIncome.setValue(BigDecimal.ZERO);
		this.otherIncome.setValue("","");
		this.additionalIncome.setValue(BigDecimal.ZERO);
		logger.debug("Leaving");
	}
	
	// ++++++++++++++++++ Child Details +++++++++++++++++++//

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++ New Button & Double Click Events for Customer Income List+++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
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
		map.put("financeCustomerListCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode",roleCode);
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
			if (customerDocument.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("customerDocument", customerDocument);
				map.put("financeCustomerListCtrl", this);
				map.put("roleCode", roleCode);
				map.put("moduleType","");
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul", null, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving");
	}

	public void doFillDocumentDetails(List<CustomerDocument> custDocumentDetails) {
		this.listBoxCustomerDocuments.getItems().clear();
		if (custDocumentDetails != null) {
			for (CustomerDocument customerDocument : custDocumentDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				if (StringUtils.trimToEmpty(customerDocument.getCustDocCategory()).equals(StringUtils.trimToEmpty(customerDocument.getLovDescCustDocCategory()))) {
					String desc = PennantAppUtil.getlabelDesc(customerDocument.getCustDocCategory(), PennantAppUtil.getCustomerDocumentTypesList());
					customerDocument.setLovDescCustDocCategory(desc);
				}	
				lc = new Listcell(customerDocument.getCustDocCategory() + "-" + customerDocument.getLovDescCustDocCategory());
				lc.setParent(item);
				lc = new Listcell(customerDocument.getCustDocTitle());
				lc.setParent(item);
				lc = new Listcell(customerDocument.getLovDescCustDocIssuedCountry());
				lc.setParent(item); 
				lc = new Listcell(customerDocument.getLovDescCustDocVerifiedBy());
				lc.setParent(item); 
				lc = new Listcell(PennantApplicationUtil.formateDate(customerDocument.getCustDocIssuedOn(),PennantConstants.dateFormate));
				lc.setParent(item); 
				lc = new Listcell(PennantApplicationUtil.formateDate(customerDocument.getCustDocExpDate(),PennantConstants.dateFormate));
				lc.setParent(item); 
				lc = new Listcell(customerDocument.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(customerDocument.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", customerDocument);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerDocumentItemDoubleClicked");
				this.listBoxCustomerDocuments.appendChild(item);
			}
			setCustomerDocumentDetailList(custDocumentDetails);
		}
		if(custDocumentDetails != null && custDocumentDetails.size() > 0){
			this.listBoxCustomerDocuments.setHeight((custDocumentDetails.size()*25)+50+"px");
		}else{
			this.listBoxCustomerDocuments.setHeight("50px");
		}
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++ New Button & Double Click Events for CustomerAddress List+++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public void onClick$btnNew_CustomerAddress(Event event) throws Exception {
		logger.debug("Entering");
		CustomerAddres customerAddres = new CustomerAddres();
		customerAddres.setNewRecord(true);
		customerAddres.setWorkflowId(0);
		customerAddres.setCustID(getCustomerDetails().getCustID());
		customerAddres.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		customerAddres.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerAddres", customerAddres);
		map.put("financeCustomerListCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode",roleCode);
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerAddres/CustomerAddresDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
			if (customerAddress.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("customerAddres", customerAddress);
				map.put("financeCustomerListCtrl", this);
				map.put("roleCode", roleCode);
				map.put("moduleType", "");
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerAddres/CustomerAddresDialog.zul", null, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving");
	}

	public void doFillCustomerAddressDetails(List<CustomerAddres> customerAddresDetails) {
		this.listBoxCustomerAddress.getItems().clear();
		if (customerAddresDetails != null) {
			for (CustomerAddres customerAddress : customerAddresDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(customerAddress.getLovDescCustAddrTypeName());
				lc.setParent(item);
				lc = new Listcell(customerAddress.getLovDescCustAddrCountryName());
				lc.setParent(item);
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
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++ New Button & Double Click Events for CustomerPhoneNumbers
	// List+++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
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
		map.put("financeCustomerListCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode",roleCode);
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerPhoneNumber/CustomerPhoneNumberDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
			if (customerPhoneNumber.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("customerPhoneNumber", customerPhoneNumber);
				map.put("financeCustomerListCtrl", this);
				map.put("roleCode",roleCode);
				map.put("moduleType", "");
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerPhoneNumber/CustomerPhoneNumberDialog.zul", null, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving");
	}


	public void doFillCustomerPhoneNumberDetails(List<CustomerPhoneNumber> customerPhoneNumDetails) {
		this.listBoxCustomerPhoneNumbers.getItems().clear();
		if (customerPhoneNumDetails != null) {
			for (CustomerPhoneNumber customerPhoneNumber : customerPhoneNumDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(StringUtils.trimToEmpty(customerPhoneNumber.getPhoneTypeCode()));
				lc.setParent(item);
				lc = new Listcell(StringUtils.trimToEmpty(customerPhoneNumber.getPhoneCountryCode()));
				lc.setParent(item);
				lc = new Listcell(customerPhoneNumber.getPhoneAreaCode());
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
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++ New Button & Double Click Events for CustomerEmailAddress List ++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public void onClick$btnNew_CustomerEmail(Event event) throws Exception {
		logger.debug("Entering");
		CustomerEMail customerEMail = new CustomerEMail();
		customerEMail.setNewRecord(true);
		customerEMail.setWorkflowId(0);
		customerEMail.setCustID(getCustomerDetails().getCustID());
		customerEMail.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		customerEMail.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerEMail", customerEMail);
		map.put("financeCustomerListCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode",roleCode);
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerEMail/CustomerEMailDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
			if (customerEmail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("customerEMail", customerEmail);
				map.put("financeCustomerListCtrl", this);
				map.put("roleCode",roleCode);
				map.put("moduleType","");
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerEMail/CustomerEMailDialog.zul", null, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving");
	}



	public void doFillCustomerEmailDetails(List<CustomerEMail> customerEmailDetails) {
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
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++ New Button & Double Click Events for Customer Bank Information List ++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
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
		map.put("financeCustomerListCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode",roleCode);
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerBankInfoDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
			if (custBankInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("customerBankInfo", custBankInfo);
				map.put("financeCustomerListCtrl", this);
				map.put("roleCode",roleCode);
				map.put("moduleType","");
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerBankInfoDialog.zul", null, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving");
	}



	public void doFillCustomerBankInfoDetails(List<CustomerBankInfo> customerBankInfoDetails) {
		this.listBoxCustomerBankInformation.getItems().clear();
		if (customerBankInfoDetails != null) {
			for (CustomerBankInfo custBankInfo : customerBankInfoDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(custBankInfo.getBankName());
				lc.setParent(item);
				lc = new Listcell(custBankInfo.getAccountNumber());
				lc.setParent(item);
				lc = new Listcell(custBankInfo.getAccountType());
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
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++ New Button & Double Click Events for Cheque Information List ++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
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
		map.put("finFormatter", finFormatter);
		map.put("financeCustomerListCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode",roleCode);
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerChequeInfoDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
			if (custChequeInfo.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("customerChequeInfo", custChequeInfo);
				map.put("finFormatter",finFormatter);
				map.put("financeCustomerListCtrl", this);
				map.put("roleCode",roleCode);
				map.put("moduleType","");
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerChequeInfoDialog.zul", null, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving");
	}



	public void doFillCustomerChequeInfoDetails(List<CustomerChequeInfo> customerChequeInfoDetails) {
		this.listBoxCustomerChequeInformation.getItems().clear();
		if (customerChequeInfoDetails != null) {
			for (CustomerChequeInfo custChequeInfo : customerChequeInfoDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(PennantApplicationUtil.formateDate(custChequeInfo.getMonthYear(), PennantConstants.dateFormate));
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(custChequeInfo.getTotChequePayment(),finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(custChequeInfo.getSalary(),finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(custChequeInfo.getReturnChequeAmt(),finFormatter));
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
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++ New Button & Double Click Events for Cheque Information List ++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
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
		map.put("finFormatter",finFormatter);
		map.put("financeCustomerListCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode",roleCode);
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerExtLiabilityDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
			if (custExtLiability.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("customerExtLiability", custExtLiability);
				map.put("finFormatter",finFormatter);
				map.put("financeCustomerListCtrl", this);
				map.put("roleCode",roleCode);
				map.put("moduleType","");
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerExtLiabilityDialog.zul", null, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving");
	}


	public void doFillCustomerExtLiabilityDetails(List<CustomerExtLiability> customerExtLiabilityDetails) {
		this.listBoxCustomerExternalLiability.getItems().clear();
		if (customerExtLiabilityDetails != null) {
			for (CustomerExtLiability custExtLiability : customerExtLiabilityDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				if(custExtLiability.getFinDate() == null){
					lc = new Listcell();
				}else{
					lc = new Listcell(PennantApplicationUtil.formateDate(custExtLiability.getFinDate(), PennantConstants.dateFormate));
				}
				lc.setParent(item);
				lc = new Listcell(custExtLiability.getFinType());
				lc.setParent(item);
				lc = new Listcell(custExtLiability.getBankName());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(custExtLiability.getOriginalAmount(),finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(custExtLiability.getInstalmentAmount(),finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(custExtLiability.getOutStandingBal(),finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(custExtLiability.getFinStatus());
				lc.setParent(item);
				lc = new Listcell(custExtLiability.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(custExtLiability.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", custExtLiability);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerExtLiabilityItemDoubleClicked");
				this.listBoxCustomerExternalLiability.appendChild(item);

			}
			setCustomerExtLiabilityDetailList(customerExtLiabilityDetails);
		}
	}

	public void doFillCustFinanceExposureDetails(List<AvailFinance> custFinanceExposureDetails) {
		this.listBoxCustomerFinExposure.getItems().clear();
		if (custFinanceExposureDetails != null) {
			for (AvailFinance availFinance : custFinanceExposureDetails) {
				Listitem item = new Listitem();
				Listcell lc;
				if(availFinance.getStatus() == null){
					lc = new Listcell();
				}else{
					lc = new Listcell(PennantApplicationUtil.formateDate(availFinance.getFinStartDate(), PennantConstants.dateFormate));
				}
				lc.setParent(item);
				String finType="",FinRef="";
				if(availFinance.getFinReference().contains("-")){
					String[] finTypeRef = availFinance.getFinReference().split("-");
					if(finTypeRef.length==2){
						finType = finTypeRef[0];
						FinRef = finTypeRef[1];
					}
				}
				lc = new Listcell(finType);
				lc.setParent(item);
				lc = new Listcell(FinRef);
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(new BigDecimal(availFinance.getDrawnPrinciple()),finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				BigDecimal finAmount = new BigDecimal(availFinance.getFinAmount());
				BigDecimal noInst = new BigDecimal(availFinance.getNoInst());
				BigDecimal instAmt = finAmount.divide(noInst, 0, RoundingMode.HALF_DOWN);
				lc = new Listcell(PennantApplicationUtil.amountFormate(instAmt, finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(new BigDecimal(availFinance.getOutStandingBal()),finFormatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(availFinance.getStatus());
				lc.setParent(item);
				this.listBoxCustomerFinExposure.appendChild(item);

			}
		}
	}
	
	
	public int getChequeSeq(){
		int idNumber = 0;
		if(getCustomerChequeInfoDetailList() != null && !getCustomerChequeInfoDetailList().isEmpty()){
			for (CustomerChequeInfo customerChequeInfo : getCustomerChequeInfoDetailList()) {
				int tempId = Integer.valueOf(customerChequeInfo.getChequeSeq());
				if(tempId > idNumber){
					idNumber = tempId;
				}
			}
		}
		return idNumber+1;
	}

	public int getLiabilitySeq(){
		int idNumber = 0;
		if(getCustomerExtLiabilityDetailList() != null && !getCustomerExtLiabilityDetailList().isEmpty()){
			for (CustomerExtLiability customerExtLiability : getCustomerExtLiabilityDetailList()) {
				int tempId = Integer.valueOf(customerExtLiability.getLiabilitySeq());
				if(tempId > idNumber){
					idNumber = tempId;
				}
			}
		}
		return idNumber+1;
	}

	/**
	 * This method set the guaranteer details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 * @throws ParseException 
	 */
	public void doSave_CustomerDetail(FinanceDetail aFinanceDetail,Tab tab) throws ParseException {
		logger.debug("Entering ");
		if (getCustomerDetails() != null) {
			final CustomerDetails aCustomerDetails = new CustomerDetails();
			BeanUtils.copyProperties(getCustomerDetails(), aCustomerDetails);
			boolean isNew = false;
			Customer aCustomer = aCustomerDetails.getCustomer();
			aCustomer.setWorkflowId(0);
			aCustomer.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
			aCustomer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aCustomer.setUserDetails(getUserWorkspace().getLoginUserDetails());
			// Write the additional validations as per below example
			// get the selected branch object from the listbox
			// Do data level validations here
			isNew = aCustomerDetails.isNewRecord();
			aCustomer.setNewRecord(isNew);
			if (StringUtils.trimToEmpty(aCustomer.getRecordType()).equals("")) {
				if (isNew) {
					aCustomer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			}
			
			CustEmployeeDetail custEmployeeDetail = aCustomerDetails.getCustEmployeeDetail();
			custEmployeeDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
			custEmployeeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			custEmployeeDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());

			if (StringUtils.trimToEmpty(custEmployeeDetail.getRecordType()).equals("")) {
				custEmployeeDetail.setVersion(custEmployeeDetail.getVersion() + 1);
				custEmployeeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				custEmployeeDetail.setNewRecord(true);
			}else{
				custEmployeeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			}
			
			doWriteComponentsToBean(getCustomerDetails(),tab);
			aFinanceDetail.setCustomerDetails(getCustomerDetails());
		}
		logger.debug("Leaving ");
	}



	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//


	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}
	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinancedetail(FinanceDetail financedetail) {
		this.financedetail = financedetail;
	}
	public FinanceDetail getFinancedetail() {
		return financedetail;
	}

	public boolean isNewFinance() {
		return newFinance;
	}
	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public boolean isValidationOn() {
		return validationOn;
	}
	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public CustomerDetails getCustomerDetails() {
		return customerDetails;
	}
	public void setCustomerDetails(CustomerDetails customerDetails) {
		this.customerDetails = customerDetails;
	}

	public List<CustomerDocument> getCustomerDocumentDetailList() {
		return customerDocumentDetailList;
	}
	public void setCustomerDocumentDetailList(
			List<CustomerDocument> customerDocumentDetailList) {
		this.customerDocumentDetailList = customerDocumentDetailList;
	}

	public List<CustomerAddres> getCustomerAddressDetailList() {
		return customerAddressDetailList;
	}
	public void setCustomerAddressDetailList(
			List<CustomerAddres> customerAddressDetailList) {
		this.customerAddressDetailList = customerAddressDetailList;
	}

	public List<CustomerPhoneNumber> getCustomerPhoneNumberDetailList() {
		return customerPhoneNumberDetailList;
	}
	public void setCustomerPhoneNumberDetailList(
			List<CustomerPhoneNumber> customerPhoneNumberDetailList) {
		this.customerPhoneNumberDetailList = customerPhoneNumberDetailList;
	}

	public List<CustomerEMail> getCustomerEmailDetailList() {
		return customerEmailDetailList;
	}
	public void setCustomerEmailDetailList(
			List<CustomerEMail> customerEmailDetailList) {
		this.customerEmailDetailList = customerEmailDetailList;
	}

	public List<CustomerBankInfo> getCustomerBankInfoDetailList() {
		return customerBankInfoDetailList;
	}
	public void setCustomerBankInfoDetailList(
			List<CustomerBankInfo> customerBankInfoDetailList) {
		this.customerBankInfoDetailList = customerBankInfoDetailList;
	}

	public List<CustomerChequeInfo> getCustomerChequeInfoDetailList() {
		return customerChequeInfoDetailList;
	}
	public void setCustomerChequeInfoDetailList(
			List<CustomerChequeInfo> customerChequeInfoDetailList) {
		this.customerChequeInfoDetailList = customerChequeInfoDetailList;
	}

	public List<CustomerExtLiability> getCustomerExtLiabilityDetailList() {
		return customerExtLiabilityDetailList;
	}
	public void setCustomerExtLiabilityDetailList(
			List<CustomerExtLiability> customerExtLiabilityDetailList) {
		this.customerExtLiabilityDetailList = customerExtLiabilityDetailList;
	}

}
