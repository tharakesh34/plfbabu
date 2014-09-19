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

import java.io.Serializable;
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
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
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
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.DirectorDetailService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.model.CustomerAddressListModelItemRenderer;
import com.pennant.webui.customermasters.customer.model.CustomerDocumentsListModelItemRenderer;
import com.pennant.webui.customermasters.customer.model.CustomerEmailListModelItemRenderer;
import com.pennant.webui.customermasters.customer.model.CustomerPhoneNumListModelItemRenderer;
import com.pennant.webui.customermasters.customer.model.CustomerRatinglistItemRenderer;
import com.pennant.webui.customermasters.customeremploymentdetail.model.CustomerEmploymentDetailListModelItemRenderer;
import com.pennant.webui.customermasters.directordetail.model.DirectorDetailListModelItemRenderer;
import com.pennant.webui.dedup.dedupparm.FetchDedupDetails;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/Customer/customerDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CustomerDialogCtrl extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = 9031340167587772517L;
	private final static Logger logger = Logger.getLogger(CustomerDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerDialog; 			// autowired
	
	protected Longbox custID; 							// autowired
	protected Textbox custCIF; 							// autowired
	protected Textbox customerCIF; 						// autowired
	protected Textbox custCoreBank; 					// autowired
	protected Textbox custShrtName; 					// autowired
	protected ExtendedCombobox custCtgCode; 			// autowired
	protected ExtendedCombobox custDftBranch; 			// autowired
	protected ExtendedCombobox custTypeCode; 			// autowired
	protected Radiogroup 	custRelation; 			    // autowired
	protected ExtendedCombobox custGroupID; 			// autowired
	protected ExtendedCombobox custBaseCcy; 			// autowired
	protected ExtendedCombobox custRO1; 				// autowired
	protected ExtendedCombobox custRO2; 				// autowired
	protected ExtendedCombobox custSector; 				// autowired
	protected ExtendedCombobox custSubSector; 			// autowired
	protected ExtendedCombobox custEmpSts; 				// autowired
	protected Textbox custCPR; 							// autowired
	protected Textbox custCR1; 							// autowired
	protected Textbox custCR2; 							// autowired
	protected Hbox hboxCustCR;
	protected Label label_CustomerDialog_CustCRCPR;
	protected ExtendedCombobox custNationality; 					// autowired
	protected ExtendedCombobox custRiskCountry; 					// autowired
	protected Datebox custDOB; 							// autowired
	protected ExtendedCombobox custSts; 							// autowired
	protected Combobox custSalutationCode; 				// autowired
	protected Space space_Salutation;
	protected Combobox custGenderCode; 					// autowired
	protected Space space_Gender;
	protected Combobox custMaritalSts; 
	protected Space space_MaritalSts;
	protected Checkbox custIsBlackListed; // autowired
	protected Datebox custBlackListDate; // autowired
	protected Intbox noOfDependents; // autowired
	
	protected Decimalbox custTotalIncome; 				// autowired
	protected Decimalbox custTotalExpense; 				// autowired
	protected Row custJoint;							// autowired
	protected Checkbox custIsJointCust;					// autowired
	protected Textbox custJointCustName;				// autowired
	protected Datebox custJointCustDob;					// autowired
	//New Fields For demo
	protected Textbox custAddlVar81;				// autowired
	protected Combobox custAddlVar82;				// autowired
	protected Checkbox custAddlVar83;				// autowired
	protected Checkbox custAddlVar84;				// autowired
	protected Checkbox custAddlVar85;				// autowired
	
	protected Combobox custAddlVar86;				// autowired
	protected Textbox custAddlVar87;				// autowired
	protected ExtendedCombobox custAddlVar88;				// autowired
	
	protected Textbox custAddlVar1;				// autowired
	protected Textbox custAddlVar2;				// autowired
	protected Textbox custAddlVar3;				// autowired
	protected Textbox custAddlVar4;				// autowired
	protected ExtendedCombobox custParentCountry;				// autowired
	protected ExtendedCombobox custLng;				// autowired
	protected Row rowDualNationUSPerson;
	protected Row rowShrNameMotherName;
	protected Row rowGivenFullName;
	
	protected Listheader listheader_JointCust;
	
	// Customer ratings List
	protected Button btnNew_CustomerRatings;
	protected Listbox listBoxCustomerRating;
	protected Listheader listheader_CustRating_RecordStatus;
	protected Listheader listheader_CustRating_RecordType;
	private List<CustomerRating> ratingsList = new ArrayList<CustomerRating>();
	private List<CustomerRating> oldVar_RatingsList = new ArrayList<CustomerRating>();
	
	// Customer Employment List
	protected Button btnNew_CustomerEmploymentDetail;
	protected Listbox listBoxCustomerEmploymentDetail;
	protected Listheader listheader_CustEmp_RecordStatus;
	protected Listheader listheader_CustEmp_RecordType;
	private List<CustomerEmploymentDetail> customerEmploymentDetailList = new ArrayList<CustomerEmploymentDetail>();
	private List<CustomerEmploymentDetail> oldVar_EmploymentDetailsList = new ArrayList<CustomerEmploymentDetail>();
	
	// Customer Documents details List
	protected Button btnNew_CustomerDocuments;
	protected Listbox listBoxCustomerDocuments;
	protected Listheader listheader_CustDoc_RecordStatus;
	protected Listheader listheader_CustDoc_RecordType;
	private List<CustomerDocument> documentsList = new ArrayList<CustomerDocument>();
	private List<CustomerDocument> oldVar_DocumentsList = new ArrayList<CustomerDocument>();
	
	// Customer address details List
	protected Button btnNew_CustomerAddress;
	protected Listbox listBoxCustomerAddress;
	protected Listheader listheader_CustAddr_RecordStatus;
	protected Listheader listheader_CustAddr_RecordType;
	private List<CustomerAddres> addressList = new ArrayList<CustomerAddres>();
	private List<CustomerAddres> oldVar_AddressList = new ArrayList<CustomerAddres>();
	
	// Customer Phone Numbers details List
	protected Button btnNew_CustomerPhoneNumbers;
	protected Listbox listBoxCustomerPhoneNumbers;
	protected Listheader listheader_CustPhone_RecordStatus;
	protected Listheader listheader_CustPhone_RecordType;
	private List<CustomerPhoneNumber> phoneNumberList = new ArrayList<CustomerPhoneNumber>();
	private List<CustomerPhoneNumber> oldVar_PhoneNumberList = new ArrayList<CustomerPhoneNumber>();
	
	// Customer email address details List
	protected Button btnNew_CustomerEmailAddress;
	protected Listbox listBoxCustomerEmailAddress;
	protected Listheader listheader_CustEmail_RecordStatus;
	protected Listheader listheader_CustEmail_RecordType;
	private List<CustomerEMail> emailList = new ArrayList<CustomerEMail>();
	private List<CustomerEMail> oldVar_EmailList = new ArrayList<CustomerEMail>();
	
	// Customer Income details List
	protected Button btnNew_CustomerIncome;
	protected Listbox listBoxCustomerIncome;
	protected Listheader listheader_CustInc_RecordStatus;
	protected Listheader listheader_CustInc_RecordType;
	private List<CustomerIncome> incomeList = new ArrayList<CustomerIncome>();
	private List<CustomerIncome> oldVar_IncomeList = new ArrayList<CustomerIncome>();
	
	protected Textbox contactPersonName;
	protected Textbox emailID;
	protected Textbox phoneNumber;
	protected Row  row_ContactPersonDetails;
	protected Row  row_PhoneNumber;

	/*
	 * old value vars for edit mode. that we can check if something on the
	 * values are edited since the last init.
	 */
	
	private transient long oldVar_custID;
	private transient String oldVar_custCIF;
	private transient String oldVar_custCoreBank;
	private transient String oldVar_custShrtName;
	private transient String oldVar_custCtgCode;
	private transient String oldVar_custDftBranch;
	private transient String oldVar_custTypeCode;
	private transient long oldVar_custGroupID;
	private transient String oldVar_custBaseCcy;
	private transient String oldVar_custRO1;
	private transient String oldVar_custRO2;
	private transient String oldVar_custSector;
	private transient String oldVar_custSubSector;
	private transient String oldVar_custEmpSts;
	private transient String oldVar_custNationality;
	private transient String oldVar_custRiskCountry;
	private transient Date oldVar_custDOB;
	private transient String oldVar_custSts;
	private transient String oldVar_custSalutationCode;
	private transient String oldVar_custGenderCode;
	private transient String oldVar_custMaritalSts;
	private transient boolean oldVar_custIsBlackListed;
	private transient boolean oldVar_CustIsJointCust;
	private transient String oldVar_CustJointCustName;
	private transient Date oldVar_CustJointCustDob;
	private transient String oldVar_contactPersonName;
	private transient String oldVar_emailID;
	private transient String oldVar_phoneNumber;
	
	
	private CustomerDetails customerDetails; // overhanded per param
	private transient CustomerListCtrl customerListCtrl; // overhanded per param
	
	private transient boolean validationOn;
	private boolean notes_Entered = false;
	private transient String CUSTCIF_REGEX;// Customer CIF Regexion Declaration
	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire
	
	// Search Button Declaration with Field Variables
	private transient String oldVar_lovDescCustCtgCodeName;
	private transient String oldVar_lovDescCustDftBranchName;
	private transient String oldVar_lovDescCustTypeCodeName;
	private transient String oldVar_lovDesccustGroupIDName;
	private transient String oldVar_lovDescCustBaseCcyName;
	private transient String oldVar_lovDescCustRO1Name;
	private transient String oldVar_lovDescCustRO2Name;
	private transient String oldVar_lovDescCustSectorName;
	private transient String oldVar_lovDescCustSubSectorName;
	private transient String oldVar_lovDescCustEmpStsName;
	private transient String oldVar_lovDescCustNationalityName;
	private transient String oldVar_lovDescCustStsName;
	
	// +++++++++++++++++++++++++++++++++++++++++++++++NOT Required
	protected Groupbox gb_incomeDetails; // autowired
	
	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox gb_Action;
	protected Groupbox gb_statusDetails;
	String parms[] = new String[4];
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient BigDecimal oldVar_custTotalIncome;
	private transient String oldVar_recordStatus;
	
	private int countRows = PennantConstants.listGridSize;
	protected Tab basicDetails;
	protected Tab tabfinancial;
	protected Tab tabkYCDetails;
	
	protected Label label_CustomerDialog_CustDOB;
	
	// Declaration of Service(s) & DAO(s)
	private transient CustomerDetailsService customerDetailsService;
	private transient DedupParmService dedupParmService;
	private int ccyFormatter = 0;
	private String moduleType = "";
	protected Div divKycDetails;
	public int borderLayoutHeight = 0;
	
	protected Row row_MartialDependents;
	protected Row row_GenderSalutation;
	protected Row rowCustEmpSts;
	protected Row rowCustCRCPR;
	private boolean corpCustomer=false;
	private String sCustSector;
	private String sCustGender;
	
	protected Tab directorDetails;
	// Customer Directory details List
	protected Button btnNew_DirectorDetail;
	protected Listbox listBoxCustomerDirectory;
	protected Listheader listheader_CustDirector_RecordStatus;
	protected Listheader listheader_CustDirector_RecordType;
	private List<DirectorDetail> directorList = new ArrayList<DirectorDetail>();
	private List<DirectorDetail> oldVar_DirectorList = new ArrayList<DirectorDetail>();
	private List<ValueLabel>	genderCodes	      = PennantAppUtil.getGenderCodes();
	private List<ValueLabel>	maritalStsTypes	      = PennantAppUtil.getMaritalStsTypes();
	protected Label label_CustomerDialog_CustNationality;
	private boolean isCountryBehrain = false;
	private transient DirectorDetailService directorDetailService;
	protected List<ValueLabel> custRelationList = PennantStaticListUtil.getCustRelationList();
	protected List<ValueLabel> targetList = PennantStaticListUtil.getCustTargetValues();
	protected List<ValueLabel> purposeRelation = PennantStaticListUtil.getPurposeOfRelation();
	private boolean isCustRelated = false;
	/**
	 * default constructor.<br>
	 */
	public CustomerDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Customer object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerDialog(Event event) throws Exception {
		logger.debug("Entering");
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		// READ OVERHANDED params !
		if (args.containsKey("customerDetails")) {
			this.customerDetails = (CustomerDetails) args.get("customerDetails");
			CustomerDetails befImage = new CustomerDetails();
			BeanUtils.copyProperties(this.customerDetails, befImage);
			this.customerDetails.setBefImage(befImage);
			setCustomerDetails(this.customerDetails);
		} else {
			setCustomerDetails(null);
		}
		if(StringUtils.trimToEmpty(getCustomerDetails().getCustomer().getCustNationality()).equals(PennantConstants.COUNTRY_BEHRAIN)){
			isCountryBehrain = true; 
		}
		if(StringUtils.trimToEmpty(getCustomerDetails().getCustomer().getCustRelation()).equalsIgnoreCase(PennantConstants.CUSTRELATION_RELATED)){
			isCustRelated = true; 
		}
		if (args.containsKey("moduleType")) {
			this.moduleType = (String) args.get("moduleType");
		}
		Customer customer = getCustomerDetails().getCustomer();
		ccyFormatter = customer.getLovDescCcyFormatter();
		if ("ENQ".equals(moduleType)) {
			doLoadWorkFlow(false, customer.getWorkflowId(), customer.getNextTaskId());
		} else {
			doLoadWorkFlow(customer.isWorkflow(), customer.getWorkflowId(), customer.getNextTaskId());
		}
		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CustomerDialog");
		}
		// READ OVERHANDED params !
		// we get the customerListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customer here.
		if (args.containsKey("customerListCtrl")) {
			setCustomerListCtrl((CustomerListCtrl) args.get("customerListCtrl"));
		} else {
			setCustomerListCtrl(null);
		}
		
		/* set components visible dependent of the users rights */
		doCheckRights();
		// Set the setter objects for PagedListwrapper classes to Initialize
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);
		
		// set Field Properties
		doSetFieldProperties();
		doStoreInitValues();
		// Hegiht Setting
		this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
		this.listBoxCustomerRating.setHeight(this.borderLayoutHeight - 400 + "px");
		this.listBoxCustomerDirectory.setHeight(this.borderLayoutHeight - 400 + "px");
		this.listBoxCustomerIncome.setHeight(this.borderLayoutHeight - 140 + "px");
		int divKycHeight = this.borderLayoutHeight - 80;
		this.divKycDetails.setHeight(divKycHeight + "px");
		int borderlayoutHeights = divKycHeight / 2;
		this.listBoxCustomerEmploymentDetail.setHeight(borderlayoutHeights - 130 + "px");
		this.listBoxCustomerDocuments.setHeight(borderlayoutHeights - 130 + "px");
		this.listBoxCustomerAddress.setHeight(borderlayoutHeights - 145 + "px");
		this.listBoxCustomerPhoneNumbers.setHeight(borderlayoutHeights - 145 + "px");
		this.listBoxCustomerEmailAddress.setHeight(borderlayoutHeights - 145 + "px");
		if (!StringUtils.trimToEmpty(customerDetails.getCustomer().getLovDescCustCtgType()).equals("") && 
				!StringUtils.trimToEmpty(customerDetails.getCustomer().getLovDescCustCtgType()).equals(PennantConstants.CUST_CAT_INDIVIDUAL)) {
			corpCustomer=true;
		}
	
		doShowDialog(this.customerDetails);
		if(!isCountryBehrain){
		setCPRNumberNonBehrain();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		parms[0] = SystemParameterDetails.getSystemParameterValue("CIF_CHAR").toString();
		parms[1] = SystemParameterDetails.getSystemParameterValue("CIF_LENGTH").toString();
		this.CUSTCIF_REGEX = "[" + parms[0] + "]{" + parms[1] + "}";
		this.custCIF.setMaxlength(Integer.parseInt(parms[1]));
		this.custCoreBank.setMaxlength(50);
		this.custShrtName.setMaxlength(50);
		this.custCtgCode.setMaxlength(8);
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
		
		this.custGroupID.setInputAllowed(false);
		this.custGroupID.setDisplayStyle(3);
		this.custGroupID.setModuleName("CustomerGroup");
		this.custGroupID.setValueColumn("CustGrpID");
		this.custGroupID.setDescColumn("CustGrpDesc");
		this.custGroupID.setValidateColumns(new String[] { "CustGrpID" });
		
		this.custBaseCcy.setMaxlength(3);
		this.custBaseCcy.setMandatoryStyle(true);
		this.custBaseCcy.setModuleName("Currency");
		this.custBaseCcy.setValueColumn("CcyCode");
		this.custBaseCcy.setDescColumn("CcyDesc");
		this.custBaseCcy.setValidateColumns(new String[] { "CcyCode" });
		
		this.custRO1.setMaxlength(8);
		this.custRO1.setMandatoryStyle(true);
		this.custRO1.setModuleName("RelationshipOfficer");
		this.custRO1.setValueColumn("ROfficerCode");
		this.custRO1.setDescColumn("ROfficerDesc");
		this.custRO1.setValidateColumns(new String[] { "ROfficerCode" });
		
		this.custRO2.setMaxlength(8);
		this.custRO2.setModuleName("RelationshipOfficer");
		this.custRO2.setValueColumn("ROfficerCode");
		this.custRO2.setDescColumn("ROfficerDesc");
		this.custRO2.setValidateColumns(new String[] { "ROfficerCode" });
		
		this.custSector.setMaxlength(8);
		this.custSector.setMandatoryStyle(true);
		this.custSector.setModuleName("Sector");
		this.custSector.setValueColumn("SectorCode");
		this.custSector.setDescColumn("SectorDesc");
		this.custSector.setValidateColumns(new String[] { "SectorCode" });
		
		this.custSubSector.setMaxlength(8);
		this.custSubSector.setMandatoryStyle(true);
		this.custSubSector.setModuleName("SubSector");
		this.custSubSector.setValueColumn("SubSectorCode");
		this.custSubSector.setDescColumn("SubSectorDesc");
		this.custSubSector.setValidateColumns(new String[] { "SubSectorCode" }); 
		
		this.custEmpSts.setMaxlength(8);
		this.custEmpSts.setMandatoryStyle(true);
		this.custEmpSts.setModuleName("EmpStsCode");
		this.custEmpSts.setValueColumn("EmpStsCode");
		this.custEmpSts.setDescColumn("EmpStsDesc");
		this.custEmpSts.setValidateColumns(new String[] { "EmpStsCode" });
		
		this.custNationality.setMaxlength(2);
		this.custNationality.setMandatoryStyle(true);
		this.custNationality.setModuleName("NationalityCode");
		this.custNationality.setValueColumn("NationalityCode");
		this.custNationality.setDescColumn("NationalityDesc");
		this.custNationality.setValidateColumns(new String[] { "NationalityCode" });
		
		this.custRiskCountry.setMaxlength(2);
		this.custRiskCountry.setMandatoryStyle(true);
		this.custRiskCountry.setModuleName("Country");
		this.custRiskCountry.setValueColumn("CountryCode");
		this.custRiskCountry.setDescColumn("CountryDesc");
		this.custRiskCountry.setValidateColumns(new String[] { "CountryCode" });
		
		this.custDOB.setFormat(PennantConstants.dateFormat);
		this.custBlackListDate.setFormat(PennantConstants.dateFormat);
		
		this.custSts.setMaxlength(8);
		this.custSts.setMandatoryStyle(true);
		this.custSts.setModuleName("CustomerStatusCode");
		this.custSts.setValueColumn("CustStsCode");
		this.custSts.setDescColumn("CustStsDescription");
		this.custSts.setValidateColumns(new String[] { "CustStsCode" });
		
		this.custCPR.setMaxlength(15);
		this.custCR1.setMaxlength(5);
		this.custCR2.setMaxlength(2);
		//++++++++++++++++++++++++++
		this.custTotalIncome.setMaxlength(18);
		this.custTotalIncome.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		
		this.custJointCustName.setMaxlength(50);
		this.custJointCustDob.setFormat(PennantConstants.dateFormat);
		
		this.contactPersonName.setMaxlength(50);
		this.phoneNumber.setMaxlength(11);
		this.emailID.setMaxlength(100);
		
		this.custAddlVar81.setMaxlength(10);
		this.custAddlVar87.setMaxlength(30);	
		this.custAddlVar1.setMaxlength(30);	
		this.custAddlVar2.setMaxlength(30);	
		this.custAddlVar3.setMaxlength(50);	
		this.custAddlVar4.setMaxlength(50);	
		
		this.custAddlVar88.setMaxlength(2);
		this.custAddlVar88.setMandatoryStyle(false);
		this.custAddlVar88.setModuleName("Country");
		this.custAddlVar88.setValueColumn("CountryCode");
		this.custAddlVar88.setDescColumn("CountryDesc");
		this.custAddlVar88.setValidateColumns(new String[] { "CountryCode" });
		
		this.custParentCountry.setMaxlength(2);
		this.custParentCountry.setMandatoryStyle(true);
		this.custParentCountry.setModuleName("Country");
		this.custParentCountry.setValueColumn("CountryCode");
		this.custParentCountry.setDescColumn("CountryDesc");
		this.custParentCountry.setValidateColumns(new String[] { "CountryCode" });
	
		this.custLng.setMaxlength(2);
		this.custLng.setMandatoryStyle(false);
		this.custLng.setModuleName("Language");
		this.custLng.setValueColumn("LngCode");
		this.custLng.setDescColumn("LngDesc");
		this.custLng.setValidateColumns(new String[] { "LngCode" });

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
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CustomerDialog",getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnSave"));
		
		// Customer related List Buttons
		this.btnNew_CustomerRatings.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnNew_CustomerRatings"));
		this.btnNew_CustomerEmploymentDetail.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnNew_CustomerAddress"));
		this.btnNew_CustomerAddress.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnNew_CustomerAddress"));
		this.btnNew_CustomerIncome.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnNew_CustomerIncome"));
		this.btnNew_CustomerDocuments.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnNew_CustomerDocuments"));
		this.btnNew_CustomerPhoneNumbers.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnNew_CustomerPhoneNumbers"));
		this.btnNew_CustomerEmailAddress.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnNew_CustomerEmailAddress"));
		this.btnNew_DirectorDetail.setVisible(getUserWorkspace().isAllowed("button_CustomerDialog_btnNew_CustomerDirectorDetails"));
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_CustomerDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
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
		} catch (CustomerNotFoundException e) {
			logger.error("Customer Not Created...");
			PTMessageUtils.showErrorMessage(e.getErrorMsg());
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
		// remember the old vars
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_CustomerDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
		logger.debug("Entering" + event.toString());
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
		} catch (CustomerNotFoundException e) {
			logger.error("Customer Not Created...");
			PTMessageUtils.showErrorMessage(e.getErrorMsg());
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws CustomerNotFoundException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException, ParseException, CustomerNotFoundException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws CustomerNotFoundException
	 * 
	 */
	private void doClose() throws InterruptedException, ParseException, CustomerNotFoundException {
		logger.debug("Entering");
		boolean close = true;
		if (isDataChanged()) {
			logger.debug("Data Changed(): True");
			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");
			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);
			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("Data Changed(): false");
		}
		if (close) {
			closeDialog(this.window_CustomerDialog, "CustomerDialog");
		}
		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
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
		fillComboBox(this.custGenderCode, aCustomer.getCustGenderCode(), genderCodes, "");
		fillComboBox(this.custSalutationCode, aCustomer.getCustSalutationCode(), PennantAppUtil.getSalutationCodes(aCustomer.getCustGenderCode()), "");
		fillComboBox(this.custMaritalSts, aCustomer.getCustMaritalSts(), maritalStsTypes, "");
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(StringUtils.trimToEmpty(aCustomer.getCustCIF()));
		if (!StringUtils.trimToEmpty(aCustomer.getCustShrtName()).equals("")) {
			this.customerCIF.setValue(StringUtils.trimToEmpty(aCustomer.getCustCIF()) + "-" + StringUtils.trimToEmpty(aCustomer.getCustShrtName()));
		} else {
			this.customerCIF.setValue(StringUtils.trimToEmpty(aCustomer.getCustCIF()));
		}
		this.custCoreBank.setValue(aCustomer.getCustCoreBank());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.custCtgCode.setValue(aCustomer.getCustCtgCode());
		this.custDftBranch.setValue(aCustomer.getCustDftBranch());
		this.custTypeCode.setValue(aCustomer.getCustTypeCode());
		this.custGroupID.setValue(String.valueOf(aCustomer.getCustGroupID()));
		this.custBaseCcy.setValue(aCustomer.getCustBaseCcy());
		this.custRO1.setValue(aCustomer.getCustRO1());
		this.custRO2.setValue(StringUtils.trimToNull(aCustomer.getCustRO2()));
		this.custSector.setValue(aCustomer.getCustSector());
		this.custSubSector.setValue(aCustomer.getCustSubSector());
		this.custEmpSts.setValue(aCustomer.getCustEmpSts());
		
		String custCRCPR = StringUtils.trimToEmpty(aCustomer.getCustCRCPR());
		if(!custCRCPR.equals("")){
			if (corpCustomer && isCountryBehrain) {
				String[] custCR = custCRCPR.split("-");
				if (custCR.length == 2) {
					this.custCR1.setValue(custCR[0]);
					this.custCR2.setValue(custCR[1]);
				}
			} else {
				this.custCPR.setValue(aCustomer.getCustCRCPR());
			}
			
		}else{
			if (aCustomerDetails.getCustomerDocumentsList()!=null && !aCustomerDetails.getCustomerDocumentsList().isEmpty()) {
				if (corpCustomer && isCountryBehrain) {
					for (CustomerDocument customerDocument : aCustomerDetails.getCustomerDocumentsList()) {
						if (customerDocument.getCustDocCategory().equals(PennantConstants.BAHRAINI_CR)) {
							String cr=StringUtils.trimToEmpty(customerDocument.getCustDocTitle());
							getCustomerDetails().getCustomer().setCustCRCPR(cr);
								if (cr.contains("-")) {
									String[] custCR = cr.split("-");
									if (custCR.length == 2 && (custCR[0].length()<=5 && custCR[1].length()<=2)) {
										this.custCR1.setValue(custCR[0]);
										this.custCR2.setValue(custCR[1]);
									}
								}
							break;
						}
					}
				}else{
					for (CustomerDocument customerDocument : aCustomerDetails.getCustomerDocumentsList()) {
						if (customerDocument.getCustDocCategory().equals(PennantConstants.CPRCODE) || 
								customerDocument.getCustDocCategory().equals(PennantConstants.NON_BAHRAINI_INTERNATIONAL_CR) || 
								customerDocument.getCustDocCategory().equals(PennantConstants.BAHRAINI_CR) ) {
							this.custCPR.setValue(StringUtils.trimToEmpty(customerDocument.getCustDocTitle()));
							getCustomerDetails().getCustomer().setCustCRCPR(StringUtils.trimToEmpty(customerDocument.getCustDocTitle()));
							break;
						}
					}
				}
			}
		}
		this.custNationality.setValue(aCustomer.getCustNationality());
		this.custDOB.setValue(aCustomer.getCustDOB());
		this.custRiskCountry.setValue(aCustomer.getCustRiskCountry());
		this.custSts.setValue(aCustomer.getCustSts());
		this.custIsBlackListed.setChecked(aCustomer.isCustIsBlackListed());
		this.custBlackListDate.setValue(aCustomer.getCustBlackListDate());
		this.noOfDependents.setValue(aCustomer.getNoOfDependents());
		this.custIsJointCust.setChecked(aCustomer.isJointCust());
		this.custJointCustName.setValue(aCustomer.getJointCustName());
		this.custJointCustDob.setValue(aCustomer.getJointCustDob());
		
		
		doFillCustomerRatings(aCustomerDetails.getRatingsList());
		doFillCustomerEmploymentDetail(aCustomerDetails.getEmploymentDetailsList());
		doFillCustomerDocuments(aCustomerDetails.getCustomerDocumentsList());
		doFillCustomerAddress(aCustomerDetails.getAddressList());
		doFillCustomerPhoneNumbers(aCustomerDetails.getCustomerPhoneNumList());
		doFillCustomerEmail(aCustomerDetails.getCustomerEMailList());
		doFillCustomerIncome(aCustomerDetails.getCustomerIncomeList());
		
		this.custCtgCode.setDescription(StringUtils.trimToEmpty(aCustomer.getCustCtgCode()).equals("") ? "" : aCustomer.getLovDescCustCtgCodeName());
		this.custDftBranch.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustDftBranchName()).equals("") ? "" : aCustomer.getLovDescCustDftBranchName());
		this.custTypeCode.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustTypeCodeName()).equals("") ? "" : aCustomer.getLovDescCustTypeCodeName());
		this.custGroupID.setDescription(aCustomer.getLovDesccustGroupIDName());
		this.custBaseCcy.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustBaseCcyName()).equals("") ? "" : aCustomer.getLovDescCustBaseCcyName());
		this.custRO1.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustRO1Name()).equals("") ? "" : aCustomer.getLovDescCustRO1Name());
		this.custRO2.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustRO2Name()).equals("") ? "" : aCustomer.getLovDescCustRO2Name());
		this.custSector.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustSectorName()).equals("") ? "" : aCustomer.getLovDescCustSectorName());
		this.custSubSector.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustSubSectorName()).equals("") ? "" : aCustomer.getLovDescCustSubSectorName());
		this.custEmpSts.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustEmpStsName()).equals("") ? "" : aCustomer.getLovDescCustEmpStsName());
		this.custNationality.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustNationalityName()).equals("") ? "" : aCustomer.getLovDescCustNationalityName());
		this.custRiskCountry.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustRiskCountryName()).equals("") ? "" : aCustomer.getLovDescCustRiskCountryName());
		this.custSts.setDescription(StringUtils.trimToEmpty(aCustomer.getLovDescCustStsName()).equals("") ? "" : aCustomer.getLovDescCustStsName());
		this.contactPersonName.setValue(aCustomer.getContactPersonName());
		this.phoneNumber.setValue(aCustomer.getPhoneNumber());
		this.emailID.setValue(aCustomer.getEmailID());
		if(this.custIsJointCust.isChecked()) {
			this.custJoint.setVisible(true);
		} else {
			this.custJoint.setVisible(true);
		}
		
	//** DirectorDetails tab is visible when categoryCode is "BANK" or "CORPORATE"  **//
		if (!StringUtils.trimToEmpty(customerDetails.getCustomer().getLovDescCustCtgType()).equals(PennantConstants.CUST_CAT_INDIVIDUAL)) {
			this.directorDetails.setVisible(true);
			doFillCustomerDirectory(aCustomerDetails.getCustomerDirectorList());
			doSetShareHoldersDesignationCode(aCustomerDetails.getCustomerDirectorList());
		}
		doSetCustTypeFilters(aCustomer.getLovDescCustCtgType());
		this.custSubSector.setFilters(new Filter[]{new Filter("SectorCode", this.custSector.getValue(), Filter.OP_EQUAL)});
		//+++++++++++++++++++++++++++++++++++++++++++++++++++
		this.custTotalIncome.setReadonly(true);
		sCustSector = this.custSector.getValue();
		
		
		this.custAddlVar81.setValue(aCustomer.getCustAddlVar81());
		fillComboBox(this.custAddlVar82, aCustomer.getCustAddlVar82(), targetList, "");
		if (StringUtils.trimToEmpty(aCustomer.getCustAddlVar83()).equals("1")) {
			this.custAddlVar83.setChecked(true);
		}else{
			this.custAddlVar83.setChecked(false);
		}
		if (StringUtils.trimToEmpty(aCustomer.getCustAddlVar84()).equals("1")) {
			this.custAddlVar84.setChecked(true);
		}else{
			this.custAddlVar84.setChecked(false);
		}
		if (StringUtils.trimToEmpty(aCustomer.getCustAddlVar85()).equals("1")) {
			this.custAddlVar85.setChecked(true);
		}else{
			this.custAddlVar85.setChecked(false);
		}
		fillComboBox(this.custAddlVar86, aCustomer.getCustAddlVar86(), purposeRelation, "");
		this.custAddlVar87.setValue(aCustomer.getCustAddlVar87());	
		this.custAddlVar88.setValue(aCustomer.getCustAddlVar88());	
		this.custAddlVar1.setValue(aCustomer.getCustAddlVar1());	
		this.custAddlVar2.setValue(aCustomer.getCustAddlVar2());	
		this.custAddlVar3.setValue(aCustomer.getCustAddlVar3());	
		this.custAddlVar4.setValue(aCustomer.getCustAddlVar4());	
		this.custParentCountry.setValue(StringUtils.trimToEmpty(aCustomer.getCustParentCountry()), StringUtils.trimToEmpty(aCustomer.getLovDescCustParentCountryName()));
		this.custLng.setValue(StringUtils.trimToEmpty(aCustomer.getCustLng()), StringUtils.trimToEmpty(aCustomer.getLovDescCustLngName()));
		
		doSetSubSectorProp();
		doFillCustRelation(aCustomer.getCustRelation());
		this.recordStatus.setValue(aCustomer.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomer
	 * @throws ParseException
	 */
	public void doWriteComponentsToBean(CustomerDetails aCustomerDetails) throws ParseException {
		logger.debug("Entering");
		doSetValidation();
		doSetLOVValidation();
		Customer aCustomer = aCustomerDetails.getCustomer();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			aCustomer.setCustID(this.custID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
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
			aCustomer.setCustRelation(this.custRelation.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDesccustGroupIDName(this.custGroupID.getDescription());
			aCustomer.setCustGroupID(Long.valueOf(this.custGroupID.getValue()));
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
			aCustomer.setLovDescCustRO1Name(this.custRO1.getDescription());
			aCustomer.setCustRO1(StringUtils.trimToNull(this.custRO1.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.custRO2.getValue().equals("") && this.custRO1.getValue().equals(this.custRO2.getValue())) {
				throw new WrongValueException(this.custRO2, Labels.getLabel("FIELD_NOT_SAME", new String[] { Labels.getLabel("label_CustomerDialog_CustRO2.value") }));
			} else {
				aCustomer.setLovDescCustRO2Name(this.custRO2.getDescription());
				aCustomer.setCustRO2(StringUtils.trimToNull(this.custRO2.getValidatedValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustSectorName(this.custSector.getDescription());
			aCustomer.setCustSector(this.custSector.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustSubSectorName(this.custSubSector.getDescription());
			aCustomer.setCustSubSector(this.custSubSector.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustEmpStsName(this.custEmpSts.getDescription());
			aCustomer.setCustEmpSts(StringUtils.trimToNull(this.custEmpSts.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(corpCustomer && isCountryBehrain){
				if(!this.custCR1.isReadonly() && !this.custCR1.isDisabled() && this.custCR1.getValue() == null){
					throw new WrongValueException(this.custCR1, Labels.getLabel("Cust_CR1", 
							new String[] { Labels.getLabel("label_CustomerDialog_CustCR.value") }));
				}else if(!this.custCR2.isReadonly() && !this.custCR2.isDisabled()  && this.custCR2.getValue() == null){
					throw new WrongValueException(this.custCR2, Labels.getLabel("Cust_CR2", 
							new String[] { Labels.getLabel("label_CustomerDialog_CustCR.value") }));
				}
				String custCR = "";
				if(!StringUtils.trim(this.custCR1.getValue()).equals("") && !StringUtils.trim(this.custCR2.getValue()).equals("")){
					custCR = StringUtils.trimToEmpty(this.custCR1.getValue()+"-"+this.custCR2.getValue());
				}
				aCustomer.setCustCRCPR(custCR);
			}else{
				aCustomer.setCustCRCPR(StringUtils.trimToEmpty(this.custCPR.getValue()));
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
			aCustomer.setLovDescCustRiskCountryName(this.custRiskCountry.getDescription());
			aCustomer.setCustRiskCountry(this.custRiskCountry.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custDOB.getValue() != null) {
				if (!this.custDOB.getValue().after((Date) SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE"))) {
					throw new WrongValueException(this.custDOB, Labels.getLabel("DATE_ALLOWED_AFTER", new String[] { Labels.getLabel("label_CustomerDialog_CustDOB.value"), SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE").toString() }));
				}else if (this.custDOB.getValue().after((Date) SystemParameterDetails.getSystemParameterValue("APP_DATE"))) {
					throw new WrongValueException(this.custDOB, Labels.getLabel("DATE_ALLOWED_BEFORE", new String[] { Labels.getLabel("label_CustomerDialog_CustDOB.value"), 
							SystemParameterDetails.getSystemParameterValue("APP_DATE").toString() }));
				}
				aCustomer.setCustDOB(new Timestamp(this.custDOB.getValue().getTime()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustStsName(this.custSts.getDescription());
			aCustomer.setCustSts(StringUtils.trimToNull(this.custSts.getValidatedValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.row_GenderSalutation.isVisible()){
				if(this.space_Salutation.getSclass().equals("mandatory") && getComboboxValue(this.custSalutationCode).equals("#")) {
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
			if(this.row_GenderSalutation.isVisible()){
				if(this.space_Gender.getSclass().equals("mandatory") && getComboboxValue(this.custGenderCode).equals("#")) {
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
			if (this.custTotalIncome.getValue() != new BigDecimal(0)) {
				aCustomer.setCustTotalIncome(PennantAppUtil.unFormateAmount(this.custTotalIncome.getValue(), ccyFormatter));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custTotalExpense.getValue() != new BigDecimal(0)) {
				aCustomer.setCustTotalExpense(PennantAppUtil.unFormateAmount(this.custTotalExpense.getValue(), ccyFormatter));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.row_MartialDependents.isVisible()){
				if(this.space_MaritalSts.getSclass().equals("mandatory") && getComboboxValue(this.custMaritalSts).equals("#")) {
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
			aCustomer.setCustIsBlackListed(this.custIsBlackListed.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustBlackListDate(this.custBlackListDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setNoOfDependents(this.noOfDependents.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomer.setJointCust(this.custIsJointCust.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomer.setJointCustName(this.custJointCustName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomer.setJointCustDob(this.custJointCustDob.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setContactPersonName(this.contactPersonName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setPhoneNumber(this.phoneNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setEmailID(this.emailID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar81(this.custAddlVar81.getValue());
			
			if (this.custAddlVar82.getSelectedItem()!=null && !this.custAddlVar82.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
				aCustomer.setCustAddlVar82(this.custAddlVar82.getSelectedItem().getValue().toString());
			}
			
			if (this.custAddlVar83.isChecked()) {
				aCustomer.setCustAddlVar83("1");
			}else{
				aCustomer.setCustAddlVar83("0");
			}
	
			if (this.custAddlVar84.isChecked()) {
				aCustomer.setCustAddlVar84("1");
			}else{
				aCustomer.setCustAddlVar84("0");
			}
			
			if (this.custAddlVar85.isChecked()) {
				aCustomer.setCustAddlVar85("1");
			}else{
				aCustomer.setCustAddlVar85("0");
			}
			
			
			if (this.custAddlVar86.getSelectedItem()!=null && !this.custAddlVar86.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
				aCustomer.setCustAddlVar86(this.custAddlVar86.getSelectedItem().getValue().toString());
			}
			
			
			aCustomer.setCustAddlVar87(this.custAddlVar87.getValue());	
			aCustomer.setCustAddlVar88(this.custAddlVar88.getValidatedValue());	
			aCustomer.setCustAddlVar1(this.custAddlVar1.getValue());	
			aCustomer.setCustAddlVar2(this.custAddlVar2.getValue());	
			aCustomer.setCustAddlVar3(this.custAddlVar3.getValue());	
			aCustomer.setCustAddlVar4(this.custAddlVar4.getValue());
			aCustomer.setCustParentCountry(StringUtils.trimToNull(this.custParentCountry.getValidatedValue()));
			aCustomer.setCustLng(this.custLng.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		showErrorDetails(wve, basicDetails);
		aCustomerDetails.setCustomer(aCustomer);
		aCustomerDetails.setRatingsList(this.ratingsList);
		aCustomerDetails.setAddressList(this.addressList);
		aCustomerDetails.setCustomerEMailList(this.emailList);
		aCustomerDetails.setCustomerDocumentsList(this.documentsList);
		aCustomerDetails.setCustomerPhoneNumList(this.phoneNumberList);
		aCustomerDetails.setEmploymentDetailsList(this.customerEmploymentDetailList);
		aCustomerDetails.setCustomerIncomeList(this.incomeList);
		if(this.directorDetails.isVisible()){
		aCustomerDetails.setCustomerDirectorList(this.directorList);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");
		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
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

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomer
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerDetails aCustomerDetails) throws InterruptedException {
		logger.debug("Entering");
		// if aCustomer == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aCustomerDetails == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aCustomerDetails = getCustomerDetailsService().getNewCustomer(false);
			setCustomerDetails(aCustomerDetails);
		} else {
			setCustomerDetails(aCustomerDetails);
		}
		// set Readonly mode accordingly if the object is new or not.
		if (aCustomerDetails.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.custSubSector.setReadonly(true);
		} else {
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
			} else {
				this.btnCtrl.setInitNew();
				this.btnDelete.setVisible(true);
			}
			doEdit();
		}
		this.customerCIF.focus();
		
		try {
			doWriteBeanToComponents(aCustomerDetails);
			doCheckCustCRCPR();
			doCheckSubSector();
			doSetJoinCustomer();
			doStoreInitValues();
			doCheckCustomerType();
			doCheckEnquiry();
			
			if (this.custIsJointCust.isChecked()) {
				this.custIsJointCust.setDisabled(true);
			}
			
			this.btnCancel.setVisible(false);
			doSetChildsListHeaders(isWorkFlowEnabled());
			
			setDialog(this.window_CustomerDialog);
		} catch (final Exception e) {
			e.printStackTrace();
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	private void doSetChildsListHeaders(boolean isWorkflow){
		logger.debug("Entering");
		this.listheader_CustRating_RecordStatus.setVisible(isWorkflow);
		this.listheader_CustRating_RecordType.setVisible(isWorkflow);
		this.listheader_CustEmp_RecordStatus.setVisible(isWorkflow);	
		this.listheader_CustEmp_RecordType.setVisible(isWorkflow);	
		this.listheader_CustDoc_RecordStatus.setVisible(isWorkflow);
		this.listheader_CustDoc_RecordType.setVisible(isWorkflow);
		this.listheader_CustAddr_RecordStatus.setVisible(isWorkflow);
		this.listheader_CustAddr_RecordType.setVisible(isWorkflow);
		this.listheader_CustPhone_RecordStatus.setVisible(isWorkflow);
		this.listheader_CustPhone_RecordType.setVisible(isWorkflow);
		this.listheader_CustEmail_RecordStatus.setVisible(isWorkflow);
		this.listheader_CustEmail_RecordType.setVisible(isWorkflow);
		this.listheader_CustInc_RecordStatus.setVisible(isWorkflow);
		this.listheader_CustInc_RecordType.setVisible(isWorkflow);
		this.listheader_CustDirector_RecordStatus.setVisible(isWorkflow);
		this.listheader_CustDirector_RecordType.setVisible(isWorkflow);
		logger.debug("Leaving");
	}
	
	private void doCheckCustCRCPR(){
		
		if (StringUtils.trimToEmpty(customerDetails.getCustomer().getCustCtgCode()).equals(PennantConstants.PFF_CUSTCTG_CORP)) {
			this.basicDetails.setLabel(Labels.getLabel("label_CustomerDialog_CustCtgCorp.value"));
		}else if (StringUtils.trimToEmpty(customerDetails.getCustomer().getCustCtgCode()).equals(PennantConstants.PFF_CUSTCTG_BANK)) {
			this.basicDetails.setLabel(Labels.getLabel("label_CustomerDialog_CustCtgbank.value"));
		}
		
		if (corpCustomer){
//			this.tabfinancial.setVisible(false);
			this.tabkYCDetails.setVisible(false);
			this.row_GenderSalutation.setVisible(false);
			this.row_MartialDependents.setVisible(false);
			this.label_CustomerDialog_CustDOB.setValue(Labels.getLabel("label_CustomerDialog_CustDateOfIncorporation.value"));
			this.rowCustEmpSts.setVisible(false);
			this.rowCustCRCPR.setVisible(true);
			this.custCtgCode.setReadonly(true);
			this.custIsJointCust.setDisabled(true);
			this.custJoint.setVisible(false);
			this.hboxCustCR.setVisible(true);
			this.custCPR.setVisible(false);
			this.label_CustomerDialog_CustCRCPR.setValue(Labels.getLabel("label_CustomerDialog_CustCR.value"));
			this.label_CustomerDialog_CustNationality.setValue(Labels.getLabel("label_CustomerDialog_CustDomicile.value"));
			if(isCountryBehrain){
				if(StringUtils.trimToEmpty(this.custCR1.getValue()).equals("") || StringUtils.trimToEmpty(this.custCR2.getValue()).equals("")){
					this.custCR1.setReadonly(isReadOnly("CustomerDialog_custCRCPR"));
					this.custCR2.setReadonly(isReadOnly("CustomerDialog_custCRCPR"));
				}
			}else{
				if(StringUtils.trimToEmpty(this.custCPR.getValue()).equals("")){
					this.custCPR.setReadonly(isReadOnly("CustomerDialog_custCRCPR"));
				}
			}
			this.row_ContactPersonDetails.setVisible(true);
			this.row_PhoneNumber.setVisible(true);
		}else{
			this.hboxCustCR.setVisible(false);
			this.custCPR.setVisible(true);
			this.label_CustomerDialog_CustCRCPR.setValue(Labels.getLabel("label_CustomerDialog_CustCPR.value"));
			if(StringUtils.trimToEmpty(this.custCPR.getValue()).equals("")){
				this.custCPR.setReadonly(isReadOnly("CustomerDialog_custCRCPR"));
			}
			this.row_ContactPersonDetails.setVisible(true);
			this.row_PhoneNumber.setVisible(true);
		}
	}
	
	private void doCheckEnquiry() {
		if ("ENQ".equals(this.moduleType)) {
			//Buttons
			this.btnDelete.setVisible(false);
			this.btnEdit.setVisible(false);
			this.btnNew_DirectorDetail.setVisible(false);
			//Fields
			this.custSubSector.setReadonly(true);
			this.custCPR.setReadonly(true);
			this.custCR1.setReadonly(true);
			this.custCR2.setReadonly(true);
			this.custIsJointCust.setDisabled(true);
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		// Basic Details Tab-->1.Key Details
		this.oldVar_custID = this.custID.longValue();
		this.oldVar_custCIF = this.custCIF.getValue();
		this.oldVar_custCoreBank = this.custCoreBank.getValue();
		this.oldVar_custShrtName = this.custShrtName.getValue();
		this.oldVar_custCtgCode = this.custCtgCode.getValue();
		this.oldVar_lovDescCustCtgCodeName = this.custCtgCode.getDescription();
		this.oldVar_custDftBranch = this.custDftBranch.getValue();
		this.oldVar_lovDescCustDftBranchName = this.custDftBranch.getDescription();
		this.oldVar_custTypeCode = this.custTypeCode.getValue();
		this.oldVar_lovDescCustTypeCodeName = this.custTypeCode.getDescription();
		this.oldVar_custGroupID = Long.valueOf(this.custGroupID.getValue());
		this.oldVar_lovDesccustGroupIDName = this.custGroupID.getDescription();
		this.oldVar_custBaseCcy = this.custBaseCcy.getValue();
		this.oldVar_lovDescCustBaseCcyName = this.custBaseCcy.getDescription();
		this.oldVar_custRO1 = this.custRO1.getValue();
		this.oldVar_lovDescCustRO1Name = this.custRO1.getDescription();
		this.oldVar_custRO2 = this.custRO2.getValue();
		this.oldVar_lovDescCustRO2Name = this.custRO2.getDescription();
		this.oldVar_custSector = this.custSector.getValue();
		this.oldVar_lovDescCustSectorName = this.custSector.getDescription();
		this.oldVar_custEmpSts = this.custEmpSts.getValue();
		this.oldVar_lovDescCustEmpStsName = this.custEmpSts.getDescription();
		this.oldVar_custSubSector = this.custSubSector.getValue();
		this.oldVar_lovDescCustSubSectorName = this.custSubSector.getDescription();
		this.oldVar_custNationality = this.custNationality.getValue();
		this.oldVar_lovDescCustNationalityName = this.custNationality.getDescription();
		this.oldVar_custRiskCountry = this.custRiskCountry.getValue();
		this.oldVar_custDOB = this.custDOB.getValue();
		this.oldVar_custSts = this.custSts.getValue();
		this.oldVar_lovDescCustStsName = this.custSts.getDescription();
		this.oldVar_custGenderCode = this.custGenderCode.getValue();
		this.oldVar_custSalutationCode = this.custSalutationCode.getValue();
		this.oldVar_custMaritalSts = this.custMaritalSts.getValue();
		this.oldVar_custIsBlackListed = this.custIsBlackListed.isChecked();
		this.oldVar_RatingsList = this.ratingsList;
		this.oldVar_AddressList = this.addressList;
		this.oldVar_PhoneNumberList = this.phoneNumberList;
		this.oldVar_DocumentsList = this.documentsList;
		this.oldVar_EmailList = this.emailList;
		this.oldVar_IncomeList = this.incomeList;
		this.oldVar_DirectorList = this.directorList;
		
		this.oldVar_CustIsJointCust = this.custIsJointCust.isChecked();
		this.oldVar_CustJointCustName = this.custJointCustName.getValue();
		this.oldVar_CustJointCustDob = this.custJointCustDob.getValue();
		this.oldVar_contactPersonName = this.contactPersonName.getValue();
		this.oldVar_phoneNumber = this.phoneNumber.getValue();
		this.oldVar_emailID = this.emailID.getValue();
		//=====================
		this.oldVar_custTotalIncome = this.custTotalIncome.getValue();
		
		

		logger.debug("Leaving");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.custID.setValue(this.oldVar_custID);
		this.custCIF.setValue(this.oldVar_custCIF);
		this.custCoreBank.setValue(this.oldVar_custCoreBank);
		this.custTypeCode.setValue(this.oldVar_custTypeCode);
		this.custTypeCode.setDescription(this.oldVar_lovDescCustTypeCodeName);
		this.custDftBranch.setValue(this.oldVar_custDftBranch);
		this.custDftBranch.setDescription(this.oldVar_lovDescCustDftBranchName);
		this.custGroupID.setValue(String.valueOf(this.oldVar_custGroupID));
		this.custGroupID.setDescription(this.oldVar_lovDesccustGroupIDName);
		this.custBaseCcy.setValue(this.oldVar_custBaseCcy);
		this.custBaseCcy.setDescription(this.oldVar_lovDescCustBaseCcyName);
		this.custGenderCode.setValue(this.oldVar_custGenderCode);
		this.custSalutationCode.setValue(this.oldVar_custSalutationCode);
		this.custShrtName.setValue(this.oldVar_custShrtName);
		this.custDOB.setValue(this.oldVar_custDOB);
		this.custSts.setValue(this.oldVar_custSts);
		this.custSts.setDescription(this.oldVar_lovDescCustStsName);
		this.custEmpSts.setValue(this.oldVar_custEmpSts);
		this.custEmpSts.setDescription(this.oldVar_lovDescCustEmpStsName);
		this.custCtgCode.setValue(this.oldVar_custCtgCode);
		this.custCtgCode.setDescription(this.oldVar_lovDescCustCtgCodeName);
		this.custSector.setValue(this.oldVar_custSector);
		this.custSector.setDescription(this.oldVar_lovDescCustSectorName);
		this.custSubSector.setValue(this.oldVar_custSubSector);
		this.custSubSector.setDescription(this.oldVar_lovDescCustSubSectorName);
		this.custTotalIncome.setValue(this.oldVar_custTotalIncome);
		this.custRiskCountry.setValue(this.oldVar_custRiskCountry);
		this.custNationality.setValue(this.oldVar_custNationality);
		this.custNationality.setDescription(this.oldVar_lovDescCustNationalityName);
		this.custRO1.setValue(this.oldVar_custRO1);
		this.custRO1.setDescription(this.oldVar_lovDescCustRO1Name);
		this.custRO2.setValue(this.oldVar_custRO2);
		this.custRO2.setDescription(this.oldVar_lovDescCustRO2Name);
		this.custMaritalSts.setValue(this.oldVar_custMaritalSts);
		this.custIsBlackListed.setChecked(this.oldVar_custIsBlackListed);
		
		this.custIsJointCust.setChecked(this.oldVar_CustIsJointCust);
		this.custJointCustName.setValue(this.oldVar_CustJointCustName);
		this.custJointCustDob.setValue(this.oldVar_CustJointCustDob);
		
		this.contactPersonName.setValue(this.oldVar_contactPersonName);
		this.phoneNumber.setValue(this.oldVar_phoneNumber);
		this.emailID.setValue(this.oldVar_emailID);
		
		this.recordStatus.setValue(this.oldVar_recordStatus);
		this.ratingsList = this.oldVar_RatingsList;
		this.addressList = this.oldVar_AddressList;
		this.phoneNumberList = this.oldVar_PhoneNumberList;
		this.documentsList = this.oldVar_DocumentsList;
		this.emailList = this.oldVar_EmailList;
		this.incomeList = this.oldVar_IncomeList;
		this.directorList = this.oldVar_DirectorList;
		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	public void setCPRNumberNonBehrain(){
		this.hboxCustCR.setVisible(false);
		this.custCPR.setVisible(true);
		if(!StringUtils.trimToEmpty(this.custCPR.getValue()).equals("")){
		this.custCPR.setReadonly(true);
		}
		if(corpCustomer){
		this.label_CustomerDialog_CustCRCPR.setValue(Labels.getLabel("label_CustomerDialog_CustCR.value"));
		}else{
		this.label_CustomerDialog_CustCRCPR.setValue(Labels.getLabel("label_CustomerDialog_CustCPR.value"));
		}
	}
	
	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		// Remove Error Messages for Fields
		doClearErrorMessage();
		// Basic Details Tab-->1.Key Details
		if (this.oldVar_custID != this.custID.longValue()) {
			return true;
		}
		if (this.oldVar_custCIF != this.custCIF.getValue()) {
			return true;
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
		if (this.oldVar_custGroupID != Long.valueOf(this.custGroupID.getValue())) {
			return true;
		}
		if (this.oldVar_custBaseCcy != this.custBaseCcy.getValue()) {
			return true;
		}

		if (this.oldVar_custEmpSts != this.custEmpSts.getValue()) {
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
		if (this.oldVar_custSts != this.custSts.getValue()) {
			return true;
		}
		if (this.oldVar_custCtgCode != this.custCtgCode.getValue()) {
			return true;
		}
		if (this.oldVar_custSector != this.custSector.getValue()) {
			return true;
		}
		if (this.oldVar_custSubSector != this.custSubSector.getValue()) {
			return true;
		}
		if (this.oldVar_custRiskCountry != this.custRiskCountry.getValue()) {
			return true;
		}
		if (this.oldVar_custNationality != this.custNationality.getValue()) {
			return true;
		}
		if (this.oldVar_custRO1 != this.custRO1.getValue()) {
			return true;
		}
		if (this.oldVar_custRO2 != this.custRO2.getValue()) {
			return true;
		}
		// Customer Related List
		if (this.oldVar_RatingsList != this.ratingsList) {
			return true;
		}
		if (this.oldVar_AddressList != this.addressList) {
			return true;
		}
		if (this.oldVar_IncomeList != this.incomeList) {
			return true;
		}
		if (this.oldVar_EmailList != this.emailList) {
			return true;
		}
		if (this.oldVar_PhoneNumberList != this.phoneNumberList) {
			return true;
		}
		if (this.oldVar_DocumentsList != this.documentsList) {
			return true;
		}
		if (this.oldVar_custMaritalSts != this.custMaritalSts.getValue()) {
			return true;
		}
		if (this.oldVar_custIsBlackListed != this.custIsBlackListed.isChecked()) {
			return true;
		}
		
		//=================
		if (this.oldVar_custTotalIncome != this.custTotalIncome.getValue()) {
			return true;
		}
		
		if (this.oldVar_CustJointCustName != this.custJointCustName.getValue()) {
			return true;
		}
		
		if (this.oldVar_CustJointCustDob != this.custJointCustDob.getValue()) {
			return true;
		}
		
		if (this.oldVar_CustIsJointCust != this.custIsJointCust.isChecked()) {
			return true;
		}
		if (this.oldVar_DirectorList != this.directorList) {
			return true;
		}
		if (this.oldVar_contactPersonName != this.contactPersonName.getValue()) {
			return true;
		}
		if (this.oldVar_phoneNumber != this.phoneNumber.getValue()) {
			return true;
		}
		if (this.oldVar_emailID != this.emailID.getValue()) {
			return true;
		}
		
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		
		doClearErrorMessage();
		setValidationOn(true);
		if (!this.custCIF.isReadonly()) {
			this.custCIF.setConstraint(new SimpleConstraint(this.CUSTCIF_REGEX, Labels.getLabel("MAND_FIELD_ALLOWED_CHARS", new String[] { Labels.getLabel("label_CustomerDialog_CustCIF.value"), parms[0], parms[1] })));
		}
		if (!this.custCoreBank.isReadonly()) {
			this.custCoreBank.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustCoreBank.value"), null, true));
		}
		if (!this.custShrtName.isReadonly()) {
			this.custShrtName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustShrtName.value"),
					PennantRegularExpressions.REGEX_CUST_NAME, true));
		}
		if (!this.custDOB.isReadonly() && !this.custDOB.isDisabled()) {
			if (corpCustomer) {
				this.custDOB.setConstraint("NO EMPTY,NO TODAY,NO FUTURE:" + Labels.getLabel("DATE_EMPTY_FUTURE_TODAY", new String[] { Labels.getLabel("label_CustomerDialog_CustDateOfIncorporation.value") }));
			}else{
				this.custDOB.setConstraint("NO EMPTY,NO TODAY,NO FUTURE:" + Labels.getLabel("DATE_EMPTY_FUTURE_TODAY", new String[] { Labels.getLabel("label_CustomerDialog_CustDOB.value") }));
			}
		}
		if(this.rowCustCRCPR.isVisible()){
			if (isCountryBehrain){
				if(getCustomerDetails().getCustomer().getLovDescCustCtgType().equals(PennantConstants.CUST_CAT_INDIVIDUAL) && 
						(!this.custCPR.isDisabled() && !this.custCPR.isReadonly())){
					this.custCPR.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustCPR.value"),
							PennantRegularExpressions.REGEX_NUMERIC_FL9, true));
				}else{
					if(!this.custCR1.isDisabled() && !this.custCR1.isReadonly()){
						this.custCR1.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustCR.value"),PennantRegularExpressions.REGEX_CR1, true));
						this.custCR2.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustCR.value"),PennantRegularExpressions.REGEX_CR2, true));
					}
				}
			}else{
				if (!this.custCPR.isReadonly() && !this.custCPR.isDisabled()) {
				if(corpCustomer){
					this.custCPR.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CustomerDialog_CustCR.value") }));
				}else{
					this.custCPR.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CustomerDialog_CustCPR.value") }));
				}
				}
			}
		}
		
		if(this.custIsJointCust.isChecked() && !this.custIsJointCust.isDisabled()) {
			this.custJointCustName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CustomerDialog_CustJointCustomerName") }));
			this.custJointCustDob.setConstraint("NO EMPTY,NO TODAY,NO FUTURE:" + Labels.getLabel("DATE_EMPTY_FUTURE_TODAY", new String[] { Labels.getLabel("label_CustomerDialog_CustJointCustDob.value") }));
		}
		if(corpCustomer){	
		if (!this.contactPersonName.isReadonly()) {
			this.contactPersonName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_ContactPersonName.value"),PennantRegularExpressions.REGEX_CUST_NAME, false));
		}
		if (!this.phoneNumber.isReadonly()) {
			this.phoneNumber.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_CustomerDialog_PhoneNumber.value"),false));
		}
		if (!this.emailID.isReadonly()) {
			this.emailID.setConstraint(new PTEmailValidator(Labels.getLabel("label_CustomerDialog_EmailID.value"),false));
		}
		}
		if(isCustRelated && (!this.custGroupID.isButtonDisabled() && this.custGroupID.isButtonVisible())){
			this.custGroupID.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CustomerDialog_CustGroupID.value") }));
		}
		
		if (!this.custAddlVar81.isReadonly()) {
			this.custAddlVar81.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustAddlVar81.value"),PennantRegularExpressions.REGEX_CUST_NAME, false));
		}
		if (!this.custAddlVar1.isReadonly()) {
			this.custAddlVar1.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustAddlVar1.value"),PennantRegularExpressions.REGEX_CUST_NAME, false));
		}
		if (!this.custAddlVar2.isReadonly()) {
			this.custAddlVar2.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustAddlVar2.value"),PennantRegularExpressions.REGEX_CUST_NAME, false));
		}
		if (!this.custAddlVar3.isReadonly()) {
			this.custAddlVar3.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustAddlVar3.value"),PennantRegularExpressions.REGEX_CUST_NAME, false));
		}
		if (!this.custAddlVar4.isReadonly()) {
			this.custAddlVar4.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustAddlVar4.value"),PennantRegularExpressions.REGEX_CUST_NAME, false));
		}
		
		this.custParentCountry.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustParentCountry.value"), null, true));
		
	}

	/**
	 * Method of validation in Saving Mode
	 */
	private void doQDEValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (!this.custCIF.isReadonly()) {
			this.custCIF.setConstraint(new SimpleConstraint(this.CUSTCIF_REGEX, Labels.getLabel("MAND_FIELD_ALLOWED_CHARS", new String[] { Labels.getLabel("label_CustomerDialog_CustCIF.value"), parms[0], parms[1] })));
		}
		if (!this.custCoreBank.isReadonly()) {
			this.custCoreBank.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustCoreBank.value"), null, true));
		}
		if (!this.custShrtName.isReadonly()) {
			this.custShrtName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustShrtName.value"), PennantRegularExpressions.REGEX_NAME, true));
		}
		if (!this.custDOB.isReadonly() && !this.custDOB.isDisabled()) {
			if (corpCustomer) {
				this.custDOB.setConstraint("NO EMPTY,NO TODAY,NO FUTURE:" + Labels.getLabel("DATE_EMPTY_FUTURE_TODAY", new String[] { Labels.getLabel("label_CustomerDialog_CustDateOfIncorporation.value") }));
			}else{
				this.custDOB.setConstraint("NO EMPTY,NO TODAY,NO FUTURE:" + Labels.getLabel("DATE_EMPTY_FUTURE_TODAY", new String[] { Labels.getLabel("label_CustomerDialog_CustDOB.value") }));
			}
		}
		if (this.rowCustCRCPR.isVisible() && isCountryBehrain) {
			if(!corpCustomer && (!this.custCPR.isDisabled() && !this.custCPR.isReadonly())){
				this.custCPR.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CustomerDialog_CustCRCPR.value") }));
			}
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
		this.custCtgCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustCtgCode.value"), null, true));
		}
		this.custDftBranch.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustDftBranch.value"), null, true));
		this.custTypeCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustTypeCode.value"), null, true));
		this.custBaseCcy.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustBaseCcy.value"), null, true));
		this.custRO1.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustRO1.value"), null, true));
		this.custSector.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustSector.value"), null, true));
		if(this.custSubSector.getSpace().getSclass() != null && this.custSubSector.getSpace().getSclass().equals("mandatory")){
		this.custSubSector.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustSubSector.value"), null, true));
		}
		this.custNationality.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustNationality.value"), null, true));
		this.custRiskCountry.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustRiskCountry.value"), null, true));
		
		if (!corpCustomer) {
			if(!this.custSalutationCode.isReadonly()){
				this.custSalutationCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustSalutationCode.value"), null, true));
			}
			this.custEmpSts.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustEmpSts.value"), null, true));
			this.custGenderCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustGenderCode.value"), null, true));
			this.custMaritalSts.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustMaritalSts.value"), null, true));
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the QDE
	 * LOVfields.
	 */
	private void doSetQDELOVValidation() {
		logger.debug("Entering");
		this.custCtgCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustCtgCode.value"), null, true));
		this.custDftBranch.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustDftBranch.value"), null, true));
		this.custTypeCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustTypeCode.value"), null, true));
		this.custBaseCcy.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustBaseCcy.value"), null, true));
		this.custRO1.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustRO1.value"), null, true));
		this.custSector.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustSector.value"), null, true));
		this.custSubSector.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustSubSector.value"), null, true));
		this.custNationality.setConstraint(new PTStringValidator( Labels.getLabel("label_CustomerDialog_CustNationality.value"), null, true));
		this.custRiskCountry.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustRiskCountry.value"), null, true));

		if (!corpCustomer) {
			this.custEmpSts.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustEmpSts.value"), null, true));
			this.custSalutationCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustSalutationCode.value"), null, true));
			this.custGenderCode.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerDialog_CustGenderCode.value"), null, true));
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.custID.setConstraint("");
		this.custCIF.setConstraint("");
		this.custCoreBank.setConstraint("");
		this.custShrtName.setConstraint("");
		this.custDOB.setConstraint("");
		this.custGroupID.setConstraint("");
		this.custJointCustName.setConstraint("");
		this.custJointCustDob.setConstraint("");
		this.contactPersonName.setConstraint("");
		this.phoneNumber.setConstraint("");
		this.emailID.setConstraint("");
		
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
		this.custEmpSts.setConstraint("");
		this.custCtgCode.setConstraint("");
		this.custSector.setConstraint("");
		this.custSubSector.setConstraint("");
		this.custCPR.setConstraint("");
		this.custNationality.setConstraint("");
		this.custRiskCountry.setConstraint("");
		this.custRO1.setConstraint("");
		this.custGroupID.setConstraint("");
		this.custMaritalSts.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearErrorMessage() {
		logger.debug("Enterring");
		this.custCIF.setErrorMessage("");
		this.custCoreBank.setErrorMessage("");
		this.custCtgCode.setErrorMessage("");
		this.custTypeCode.setErrorMessage("");
		this.custSalutationCode.setErrorMessage("");
		this.custShrtName.setErrorMessage("");
		this.custDftBranch.setErrorMessage("");
		this.custGenderCode.setErrorMessage("");
		this.custDOB.setErrorMessage("");
		this.custRO1.setErrorMessage("");
		this.custRO2.setErrorMessage("");
		this.custSts.setErrorMessage("");
		this.custSector.setErrorMessage("");
		this.custSubSector.setErrorMessage("");
		this.custTotalIncome.setErrorMessage("");
		this.custEmpSts.setErrorMessage("");
		this.custBaseCcy.setErrorMessage("");
		this.custRiskCountry.setErrorMessage("");
		this.custNationality.setErrorMessage("");
		this.custDftBranch.setErrorMessage("");
		this.custSalutationCode.setErrorMessage("");
		this.custCtgCode.setErrorMessage("");
		this.custSector.setErrorMessage("");
		this.custRO1.setErrorMessage("");
		this.custGroupID.setErrorMessage("");
		this.custMaritalSts.setErrorMessage("");
		this.custMaritalSts.setErrorMessage("");
		this.custJointCustName.setErrorMessage("");
		this.custJointCustDob.setErrorMessage("");
		this.contactPersonName.setErrorMessage("");
		this.phoneNumber.setErrorMessage("");
		this.emailID.setErrorMessage("");
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful updation
	private void refreshList() {
		final JdbcSearchObject<Customer> soCustomer = getCustomerListCtrl().getSearchObj();
		getCustomerListCtrl().pagingCustomerList.setActivePage(0);
		getCustomerListCtrl().getPagedListWrapper().setSearchObject(soCustomer);
		if (getCustomerListCtrl().listBoxCustomer != null) {
			getCustomerListCtrl().listBoxCustomer.getListModel();
		}
	}

	/**
	 * Deletes a Customer object from database.<br>
	 * 
	 * @throws InterruptedException
	 * @throws CustomerNotFoundException
	 */
	private void doDelete() throws InterruptedException, CustomerNotFoundException {
		logger.debug("Entering");
		final CustomerDetails aCustomerDetails = new CustomerDetails();
		BeanUtils.copyProperties(getCustomerDetails(), aCustomerDetails);
		String tranType = PennantConstants.TRAN_WF;
		Customer aCustomer = aCustomerDetails.getCustomer();
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCustomer.getCustCIF();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));
		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");
			if (StringUtils.trimToEmpty(aCustomer.getRecordType()).equals("")) {
				aCustomer.setVersion(aCustomer.getVersion() + 1);
				aCustomer.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aCustomer.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				aCustomerDetails.setCustomer(aCustomer);
				if (doProcess(aCustomerDetails, tranType)) {
					refreshList();
					closeDialog(this.window_CustomerDialog, "CustomerDialog");
				}
				logger.debug(" Calling doDelete method completed Successfully ");
			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new Customer object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old vars
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new Customer() in the frontend.
		// we get it from the backend.
		final CustomerDetails customerDetails = getCustomerDetailsService().getNewCustomer(false);
		setCustomerDetails(customerDetails);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.customerCIF.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if("ENQ".equals(moduleType)){
			doReadOnly();
		}else{
			if (SystemParameterDetails.getSystemParameterValue("CB_CID").equals("CIF") || SystemParameterDetails.getSystemParameterValue("CBI_Active").equals("N")) {
				this.custCoreBank.setReadonly(true);
			} else {
				this.custCoreBank.setReadonly(isReadOnly("CustomerDialog_custCoreBank"));
			}
			// Condition for not allow to change in maintain State
			this.custDftBranch.setReadonly(isReadOnly("CustomerDialog_custDftBranch"));
			this.custDftBranch.setMandatoryStyle(!isReadOnly("CustomerDialog_custDftBranch"));
			this.custBaseCcy.setReadonly(isReadOnly("CustomerDialog_custBaseCcy"));
			this.custBaseCcy.setMandatoryStyle(!isReadOnly("CustomerDialog_custBaseCcy"));
			this.custGroupID.setReadonly(isReadOnly("CustomerDialog_custGroupID"));
			this.custMaritalSts.setDisabled(isReadOnly("CustomerDialog_custMaritalSts"));
			this.noOfDependents.setDisabled(isReadOnly("CustomerDialog_custMaritalSts"));
			this.custSalutationCode.setDisabled(isReadOnly("CustomerDialog_custSalutationCode"));
			this.custEmpSts.setReadonly(isReadOnly("CustomerDialog_custEmpSts"));

			this.custCIF.setReadonly(isReadOnly("CustomerDialog_custCIF"));
			this.custCIF.setReadonly(true);
			this.customerCIF.setReadonly(true);
			this.custSts.setReadonly(true);
			this.custSts.setMandatoryStyle(false);
			this.custCtgCode.setReadonly(true);
			this.custCR1.setReadonly(true);
			this.custCR2.setReadonly(true);
			this.custCPR.setReadonly(true);
			this.custNationality.setReadonly(true);

			if (StringUtils.trimToEmpty(getCustomerDetails().getCustomer().getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW) || 
					getCustomerDetails().getCustomer().isNewRecord()) {

				this.custShrtName.setReadonly(isReadOnly("CustomerDialog_custShrtName"));
				this.custDOB.setDisabled(isReadOnly("CustomerDialog_custDOB"));

				this.custRO1.setReadonly(isReadOnly("CustomerDialog_custRO1"));
				this.custRO1.setMandatoryStyle(!isReadOnly("CustomerDialog_custRO1"));
				this.custRO2.setReadonly(isReadOnly("CustomerDialog_custRO2"));
				this.custSector.setReadonly(isReadOnly("CustomerDialog_custSector"));
				this.custSector.setMandatoryStyle(!isReadOnly("CustomerDialog_custSector"));
				this.custSubSector.setReadonly(isReadOnly("CustomerDialog_custSubSector"));
				this.custSubSector.setMandatoryStyle(!isReadOnly("CustomerDialog_custSubSector"));
				this.custTotalIncome.setReadonly(isReadOnly("CustomerDialog_custTotalIncome"));
				this.custRiskCountry.setReadonly(isReadOnly("CustomerDialog_custNationality"));
				this.custRiskCountry.setMandatoryStyle(!isReadOnly("CustomerDialog_custNationality"));
				this.custTypeCode.setReadonly(isReadOnly("CustomerDialog_custTypeCode"));
				this.custTypeCode.setMandatoryStyle(!isReadOnly("CustomerDialog_custTypeCode"));
				this.custGenderCode.setDisabled(isReadOnly("CustomerDialog_custGenderCode"));
				this.contactPersonName.setReadonly(isReadOnly("CustomerDialog_contactPersonName"));
				this.phoneNumber.setReadonly(isReadOnly("CustomerDialog_phoneNumber"));
				this.emailID.setReadonly(isReadOnly("CustomerDialog_emailId"));
			}else{
				this.custShrtName.setReadonly(true);
				this.custDOB.setDisabled(true);
				/*			if(corpCustomer){
				this.custCR1.setDisabled(true);
				this.custCR2.setDisabled(true);
			}else{
				this.custCPR.setDisabled(true);
			}*/
				this.custRO1.setReadonly(true);
				this.custRO2.setReadonly(true);
				this.custSts.setReadonly(true);
				this.custSector.setReadonly(true);
				this.custSubSector.setReadonly(true);
				this.custTotalIncome.setReadonly(true);
				this.custNationality.setReadonly(true);
				this.custRiskCountry.setReadonly(true);
				this.custTypeCode.setReadonly(true);
				this.custGenderCode.setDisabled(true);
				/*			this.contactPersonName.setReadonly(true);
			this.phoneNumber.setReadonly(true);
			this.emailID.setReadonly(true);*/
			}

			if (isWorkFlowEnabled()) {
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
		this.custID.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.customerCIF.setReadonly(true);
		this.custCoreBank.setReadonly(true);
		this.custCtgCode.setReadonly(true);
		this.custTypeCode.setReadonly(true);
		this.custSalutationCode.setDisabled(true);
		this.custRiskCountry.setReadonly(true);
		this.custShrtName.setReadonly(true);
		this.custDftBranch.setReadonly(true);
		this.custGenderCode.setDisabled(true);
		this.custDOB.setDisabled(true);
		this.custRO1.setReadonly(true);
		this.custRO2.setReadonly(true);
		this.custGroupID.setReadonly(true);
		this.custGroupID.setReadonly(true);
		this.custSts.setReadonly(true);
		this.custSector.setReadonly(true);
		this.custSubSector.setReadonly(true);
		this.custTotalIncome.setReadonly(true);
		this.custEmpSts.setReadonly(true);
		this.custBaseCcy.setReadonly(true);
		this.custNationality.setReadonly(true);
		this.btnNew_CustomerRatings.setVisible(false);
		this.btnNew_CustomerEmploymentDetail.setVisible(false);
		this.btnNew_CustomerAddress.setVisible(false);
		this.btnNew_CustomerIncome.setVisible(false);
		this.btnNew_CustomerDocuments.setVisible(false);
		this.btnNew_CustomerPhoneNumbers.setVisible(false);
		this.btnNew_CustomerEmailAddress.setVisible(false);
		this.btnNew_DirectorDetail.setVisible(false);
		this.custMaritalSts.setDisabled(true);
		this.custJointCustName.setReadonly(true);
		this.custJointCustDob.setDisabled(true);
		this.noOfDependents.setReadonly(true);
		this.contactPersonName.setReadonly(true);
		this.phoneNumber.setReadonly(true);
		this.emailID.setReadonly(true);
		this.custCPR.setReadonly(true);
		this.custCR1.setReadonly(true);
		this.custCR2.setReadonly(true);
		
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
		this.custID.setText("");
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
		this.custRO1.setValue("");
		this.custRO1.setDescription("");
		this.custRO2.setValue("");
		this.custRO2.setDescription("");
		this.custGroupID.setValue(String.valueOf(new Long(0)));
		this.custSts.setValue("");
		this.custSts.setDescription("");
		this.custSector.setValue("");
		this.custSector.setDescription("");
		this.custSubSector.setValue("");
		this.custSubSector.setDescription("");
		this.custTotalIncome.setValue(PennantAppUtil.formateAmount(new BigDecimal(0), ccyFormatter));
		this.custEmpSts.setValue("");
		this.custEmpSts.setDescription("");
		this.custBaseCcy.setValue("");
		this.custBaseCcy.setDescription("");
		this.custRiskCountry.setValue("");
		this.custNationality.setValue("");
		this.custNationality.setDescription("");
		this.custMaritalSts.setValue("");
		this.contactPersonName.setValue("");
		this.phoneNumber.setValue("");
		this.emailID.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws CustomerNotFoundException
	 */
	public void doSave() throws InterruptedException, ParseException, CustomerNotFoundException {
		logger.debug("Entering");
		final CustomerDetails aCustomerDetails = new CustomerDetails();
		BeanUtils.copyProperties(getCustomerDetails(), aCustomerDetails);
		boolean isNew = false;
		Customer aCustomer = aCustomerDetails.getCustomer();
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (userAction.getSelectedItem() != null) {
			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				doQDEValidation();
				doSetQDELOVValidation();
			} else if ("Submit".equals(userAction.getSelectedItem().getLabel())) {
				doSetValidation();
				doSetLOVValidation();
			}
		}else{
			doQDEValidation();
			doSetQDELOVValidation();
			doSetValidation();
			doSetLOVValidation();
		}
		// fill the Customer object with the components data
		doWriteComponentsToBean(aCustomerDetails);
		aCustomer = aCustomerDetails.getCustomer();
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		isNew = aCustomerDetails.isNewRecord();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCustomer.getRecordType()).equals("")) {
				aCustomer.setVersion(aCustomer.getVersion() + 1);
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
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			aCustomerDetails.setCustomer(aCustomer);
			if (doProcess(aCustomerDetails, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_CustomerDialog, "CustomerDialog");
			}
			logger.debug(" Calling doSave method completed Successfully");
		} catch (final DataAccessException e) {
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private String getServiceTasks(String taskId, Customer aCustomer, String finishedTasks) {
		String serviceTasks;
		serviceTasks = getWorkFlow().getOperationRefs(taskId, aCustomer);
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
			nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCustomer);
		}
		// Set the role codes for the next tasks
		String nextRoleCode = "";
		if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
			nextRoleCode = getWorkFlow().firstTask.owner;
		} else {
			String[] nextTasks = nextTaskId.split(";");
			if (nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode + "," + getWorkFlow().getTaskOwner(nextTasks[i]);
					} else {
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				}
			} else {
				nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
			}
		}
		aCustomer.setTaskId(taskId);
		aCustomer.setNextTaskId(nextTaskId);
		aCustomer.setRoleCode(getRole());
		aCustomer.setNextRoleCode(nextRoleCode);
	}

	private boolean doProcess(CustomerDetails aCustomerDetails, String tranType) throws CustomerNotFoundException {
		logger.debug("Entering");
		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		Customer aCustomer = aCustomerDetails.getCustomer();
		aCustomer.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCustomer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomer.setUserDetails(getUserWorkspace().getLoginUserDetails());
		aCustomerDetails.setCustID(aCustomer.getCustID());
		aCustomerDetails.setCustomer(aCustomer);
		aCustomerDetails.setUserDetails(getUserWorkspace().getLoginUserDetails());
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			// Upgraded to ZK-6.5.1.1 Added casting to String
			aCustomer.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			// Check whether required auditing notes entered or not
			if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aCustomer))) {
				try {
					if (!isNotes_Entered()) {
						PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				} catch (InterruptedException e) {
					logger.error(e);
					e.printStackTrace();
				}
			}
			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, aCustomer, finishedTasks);
			auditHeader = getAuditHeader(aCustomerDetails, PennantConstants.TRAN_WF);
			while (!"".equals(serviceTasks)) {
				String method = serviceTasks.split(";")[0];
				if ("doDdeDedup".equals(method) || "doVerifierDedup".equals(method) || "doApproverDedup".equals(method)) {
					CustomerDetails tCustomerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
					tCustomerDetails = FetchDedupDetails.getCustomerDedup(getRole(), tCustomerDetails, this.window_CustomerDialog);
					if (tCustomerDetails.getCustomer().isDedupFound() && !tCustomerDetails.getCustomer().isSkipDedup()) {
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
			//Check Dedup if Prospect Customer
			//  && StringUtils.trimToEmpty(aCustomerDetails.getCustomer().getRecordType()).equals("")
			if (!aCustomerDetails.getCustomer().isSkipDedup()) {
				if (StringUtils.trimToEmpty(aCustomerDetails.getCustomer().getCustCoreBank()).equals("")) {
					aCustomerDetails = FetchDedupDetails.getCustomerDedup(getRole(), aCustomerDetails, this.window_CustomerDialog);
					if (aCustomerDetails.getCustomer().isDedupFound() && !aCustomerDetails.getCustomer().isSkipDedup()) {
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
				nextTaskId = getWorkFlow().getNextTaskIds(taskId, tCustomerDetails.getCustomer());
			} else {
				nextTaskId = getWorkFlow().getNextTaskIds(taskId, object);
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

	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws CustomerNotFoundException {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		CustomerDetails aCustomerDetails = (CustomerDetails) auditHeader.getAuditDetail().getModelData();
		Customer aCustomer = aCustomerDetails.getCustomer();
		boolean deleteNotes = false;
		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
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
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
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
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ Search Button Events++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//



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


	public void onFulfill$custSector(Event event) {
		logger.debug("Entering");
		doSetSubSectorProp();
		logger.debug("Leaving");
	}
	
	private void doSetSubSectorProp(){
		if (!StringUtils.trimToEmpty(sCustSector).equals(this.custSector.getValue())) {
			this.custSubSector.setValue("");
			this.custSubSector.setDescription("");
			this.custSubSector.setReadonly(isReadOnly("CustomerDialog_custSubSector"));
		}
		sCustSector = this.custSector.getValue();
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("SectorCode", this.custSector.getValue(), Filter.OP_EQUAL);
		custSubSector.setFilters(filters);
		doCheckSubSector();
	}

	public void onFulfill$custBaseCcy(Event event) {
		logger.debug("Entering");
		Object dataObject = custBaseCcy.getObject();
		if (dataObject instanceof String) {
			ccyFormatter = 0;
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				ccyFormatter = details.getCcyEditField();
			}
		}
		this.custTotalIncome.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		doFillCustomerIncome(getIncomeList());
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
		if(this.custCtgCode.getValue() != null && (this.custCtgCode.getValue().equals("BANK") ||
				this.custCtgCode.getValue().equals("CORP"))){
			this.directorDetails.setVisible(true);
			doFillCustomerDirectory(this.directorList);
		}else{
			this.directorDetails.setVisible(false);
		}
		
		logger.debug("Leaving");
	}
	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++ New Button & Double Click Events for Customer Rating List+++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
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
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerRating/CustomerRatingDialog.zul", window_CustomerDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
			if (customerRating.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("customerRating", customerRating);
				map.put("customerDialogCtrl", this);
				map.put("roleCode", getRole());
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerRating/CustomerRatingDialog.zul", window_CustomerDialog, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++ New Button & Double Click Events for Customer Employmnet
	// List+++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
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
		map.put("roleCode", getRole());
		map.put("currentEmployer",getCurrentEmployerExist(customerEmploymentDetail));
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerEmploymentDetail/CustomerEmploymentDetailDialog.zul", window_CustomerDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	
	public boolean getCurrentEmployerExist(CustomerEmploymentDetail custEmpDetail){
		boolean isCurrentEmp = false;
		for (CustomerEmploymentDetail customerEmploymentDetail : customerEmploymentDetailList) {
			if(customerEmploymentDetail.getCustEmpName() != custEmpDetail.getCustEmpName() && customerEmploymentDetail.isCurrentEmployer()){
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
			final CustomerEmploymentDetail customerEmploymentDetail = (CustomerEmploymentDetail) item.getAttribute("data");
			if (customerEmploymentDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("customerEmploymentDetail", customerEmploymentDetail);
				map.put("customerDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
				map.put("currentEmployer",getCurrentEmployerExist(customerEmploymentDetail));
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerEmploymentDetail/CustomerEmploymentDetailDialog.zul", window_CustomerDialog, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving");
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
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
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
				map.put("customerDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++ New Button & Double Click Events for CustomerAddress List+++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public void onClick$btnNew_DirectorDetail(Event event) throws Exception {
		logger.debug("Entering");
		final DirectorDetail directorDetail = getDirectorDetailService().getNewDirectorDetail();
		directorDetail.setWorkflowId(0);
		directorDetail.setCustID(getCustomerDetails().getCustID());
		//directorDetail.setDirectorId(this.listBoxCustomerDirectory.getItemCount() == 0 ? 1 : this.listBoxCustomerDirectory.getItemCount() + 1);
		directorDetail.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		directorDetail.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("directorDetail", directorDetail);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		map.put("totSharePerc",getTotSharePerc());
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/DirectorDetail/DirectorDetailDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
			if (StringUtils.trimToEmpty(directorDetail.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("directorDetail", directorDetail);
				map.put("customerDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
				BigDecimal totSharePerc = BigDecimal.ZERO;
				if(directorDetail.getSharePerc() != null){
				totSharePerc = getTotSharePerc().subtract(directorDetail.getSharePerc());
				}
				map.put("totSharePerc",totSharePerc);
				if ("ENQ".equals(this.moduleType)) {
				map.put("isEnquiry",true);
				}
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/DirectorDetail/DirectorDetailDialog.zul", null, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving");
	}
	
	public BigDecimal getTotSharePerc(){
		BigDecimal totSharePerc = BigDecimal.ZERO;
		for (DirectorDetail directorDetail : getDirectorList()) {
			if(directorDetail.getSharePerc() != null){
			totSharePerc = totSharePerc.add(directorDetail.getSharePerc());
			}
		}
		return totSharePerc;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++ New Button & Double Click Events for CustomerEmailAddress List ++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public void onClick$btnNew_CustomerEmailAddress(Event event) throws Exception {
		logger.debug("Entering");
		CustomerEMail customerEMail = new CustomerEMail();
		customerEMail.setNewRecord(true);
		customerEMail.setWorkflowId(0);
		customerEMail.setCustID(getCustomerDetails().getCustID());
		customerEMail.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		customerEMail.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerEMail", customerEMail);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
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
		final Listitem item = this.listBoxCustomerEmailAddress.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerEMail customerEmail = (CustomerEMail) item.getAttribute("data");
			if (customerEmail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("customerEMail", customerEmail);
				map.put("customerDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++ New Button & Double Click Events for CustomerPhoneNumbers
	// List+++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public void onClick$btnNew_CustomerPhoneNumbers(Event event) throws Exception {
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
		map.put("roleCode", getRole());
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
				map.put("customerDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
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
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
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
				map.put("customerDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++ New Button & Double Click Events for Customer Income List+++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
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
		map.put("jointCust", this.custIsJointCust.isChecked());
		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerIncome/CustomerIncomeDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
			if (customerIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("customerIncome", customerIncome);
				map.put("customerDialogCtrl", this);
				map.put("ccyFormatter", ccyFormatter);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
				map.put("jointCust", this.custIsJointCust.isChecked());
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/CustomerMasters/CustomerIncome/CustomerIncomeDialog.zul", null, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onCheck$custIsJointCust(Event event) throws Exception {
		logger.info("Entering");

		doSetJoinCustomer();

		logger.info("Leaving");
	}

	private void doSetJoinCustomer() {
		if (custIsJointCust.isChecked()) {
			this.custJoint.setVisible(true);
		} else {
			this.custJoint.setVisible(false);
			this.custIsJointCust.setValue(false);
			this.custJointCustDob.setValue(null);
			this.custJointCustName.setValue("");

		}
	}
	
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++ Customer Related Lists Refreshing ++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	/**
	 * Generate the Customer Rating Details List in the CustomerDialogCtrl and
	 * set the list in the listBoxCustomerRating listbox by using Pagination
	 */
	public void doFillCustomerRatings(List<CustomerRating> customerRatings) {
		logger.debug("Entering");
		setRatingsList(customerRatings);
		ListModelList<CustomerRating> listModelList = new ListModelList<CustomerRating>(customerRatings);
		this.listBoxCustomerRating.setModel(listModelList);
		this.listBoxCustomerRating.setItemRenderer(new CustomerRatinglistItemRenderer());
		if(customerRatings != null && customerRatings.size() > 0){
			this.listBoxCustomerRating.setHeight((customerRatings.size()*25)+50+"px");
		}else{
			this.listBoxCustomerRating.setHeight("50px");
		}
		logger.debug("Leaving");
	}

	public void doFillCustomerEmploymentDetail(List<CustomerEmploymentDetail> custEmploymentDetails) {
		logger.debug("Entering");
		setCustomerEmploymentDetailList(custEmploymentDetails);
		ListModelList<CustomerEmploymentDetail> listModelList = new ListModelList<CustomerEmploymentDetail>(custEmploymentDetails);
		this.listBoxCustomerEmploymentDetail.setModel(listModelList);
		this.listBoxCustomerEmploymentDetail.setItemRenderer(new CustomerEmploymentDetailListModelItemRenderer());
		if(custEmploymentDetails != null && custEmploymentDetails.size() > 0){
			this.listBoxCustomerEmploymentDetail.setHeight((custEmploymentDetails.size()*25)+50+"px");
		}else{
			this.listBoxCustomerEmploymentDetail.setHeight("50px");
		}
	}

	/**
	 * Generate the Customer Address Details List in the CustomerDialogCtrl and
	 * set the list in the listBoxCustomerAddress listbox by using Pagination
	 */
	public void doFillCustomerAddress(List<CustomerAddres> customerAddress) {
		logger.debug("Entering");
		setAddressList(customerAddress);
		ListModelList<CustomerAddres> listModelList = new ListModelList<CustomerAddres>(customerAddress);
		this.listBoxCustomerAddress.setModel(listModelList);
		this.listBoxCustomerAddress.setItemRenderer(new CustomerAddressListModelItemRenderer());
		if(customerAddress != null && customerAddress.size() > 0){
			this.listBoxCustomerAddress.setHeight((customerAddress.size()*25)+50+"px");
		}else{
			this.listBoxCustomerAddress.setHeight("50px");
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Generate the Customer Address Details List in the CustomerDialogCtrl and
	 * set the list in the listBoxCustomerAddress listbox by using Pagination
	 */
	public void doFillCustomerDirectory(List<DirectorDetail> customerDirectory) {
		logger.debug("Entering");
		this.listBoxCustomerDirectory.getItems().clear();
		if(customerDirectory != null && customerDirectory.size() > 0){
		setDirectorList(customerDirectory);
		ListModelList<DirectorDetail> listModelList = new ListModelList<DirectorDetail>(customerDirectory);
		this.listBoxCustomerDirectory.setModel(listModelList);
		this.listBoxCustomerDirectory.setItemRenderer(new DirectorDetailListModelItemRenderer());
		this.listBoxCustomerDirectory.setHeight("90%");
		}
		logger.debug("Leaving");
	}

	/**
	 * Generate the Customer Email Details List in the CustomerDialogCtrl and
	 * set the list in the listBoxCustomerEmail listbox by using Pagination
	 */
	public void doFillCustomerEmail(List<CustomerEMail> customerEmails) {
		logger.debug("Entering");
		setEmailList(customerEmails);
		ListModelList<CustomerEMail> listModelList = new ListModelList<CustomerEMail>(customerEmails);
		this.listBoxCustomerEmailAddress.setModel(listModelList);
		this.listBoxCustomerEmailAddress.setItemRenderer(new CustomerEmailListModelItemRenderer());
		if(customerEmails != null && customerEmails.size() > 0){
			this.listBoxCustomerEmailAddress.setHeight((customerEmails.size()*25)+50+"px");
		}else{
			this.listBoxCustomerEmailAddress.setHeight("50px");
		}
		logger.debug("Leaving");
	}

	/**
	 * Generate the Customer Phone Details List in the CustomerDialogCtrl and
	 * set the list in the listBoxCustomerPhone listbox by using Pagination
	 */
	public void doFillCustomerPhoneNumbers(List<CustomerPhoneNumber> customerPhoneNumbers) {
		logger.debug("Entering");
		setPhoneNumberList(customerPhoneNumbers);
		ListModelList<CustomerPhoneNumber> listModelList = new ListModelList<CustomerPhoneNumber>(customerPhoneNumbers);
		this.listBoxCustomerPhoneNumbers.setModel(listModelList);
		this.listBoxCustomerPhoneNumbers.setItemRenderer(new CustomerPhoneNumListModelItemRenderer());
		if(customerPhoneNumbers != null && customerPhoneNumbers.size() > 0){
			this.listBoxCustomerPhoneNumbers.setHeight((customerPhoneNumbers.size()*25)+50+"px");
		}else{
			this.listBoxCustomerPhoneNumbers.setHeight("50px");
		}
		logger.debug("Leaving");
	}

	/**
	 * Generate the Customer Income Details List in the CustomerDialogCtrl and
	 * set the list in the listBoxCustomerIncome listbox by using Pagination
	 */
	protected Listbox incomeSummary;

	public void doFillCustomerIncome(List<CustomerIncome> incomes) {
		logger.debug("Entering");
		setIncomeList(incomes);
		createIncomeGroupList(incomes);
		logger.debug("Leaving");
	}

	private void createIncomeGroupList(List<CustomerIncome> incomes) {
		BigDecimal totIncome = new BigDecimal(0);
		BigDecimal totExpense = new BigDecimal(0);
		Map<String, List<CustomerIncome>> incomeMap = new HashMap<String, List<CustomerIncome>>();
		Map<String, List<CustomerIncome>> expenseMap = new HashMap<String, List<CustomerIncome>>();
		for (CustomerIncome customerIncome : incomes) {
			customerIncome.setLovDescCcyEditField(ccyFormatter);
			String category=StringUtils.trimToEmpty(customerIncome.getCategory());
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

	private void renderIncomeExpense(Map<String, List<CustomerIncome>> incomeMap, BigDecimal totIncome, Map<String, List<CustomerIncome>> expenseMap, BigDecimal totExpense, int ccyFormatter) {
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
					BigDecimal total = new BigDecimal(0);
					for (CustomerIncome customerIncome : list) {
						item = new Listitem();
						cell = new Listcell("");
						cell.setParent(item);
						cell = new Listcell(customerIncome.getLovDescCustIncomeTypeName());
						cell.setParent(item);
						total = total.add(customerIncome.getCustIncome());
						cell = new Listcell(PennantAppUtil.amountFormate(customerIncome.getCustIncome(), customerIncome.getLovDescCcyEditField()));
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
					BigDecimal total = new BigDecimal(0);
					for (CustomerIncome customerIncome : list) {
						item = new Listitem();
						cell = new Listcell("");
						cell.setParent(item);
						cell = new Listcell(customerIncome.getLovDescCustIncomeTypeName());
						cell.setParent(item);
						total = total.add(customerIncome.getCustIncome());
						cell = new Listcell(PennantAppUtil.amountFormate(customerIncome.getCustIncome(), customerIncome.getLovDescCcyEditField()));
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
		this.custTotalIncome.setValue(PennantAppUtil.formateAmount(totIncome, ccyFormatter));
		this.custTotalExpense.setValue(PennantAppUtil.formateAmount(totExpense, ccyFormatter));
	}

	/**
	 * Generate the Customer Documents List in the CustomerDialogCtrl and set
	 * the list in the listBoxCustomerDocuments listbox by using Pagination
	 */
	public void doFillCustomerDocuments(List<CustomerDocument> documents) {
		logger.debug("Entering");
		setDocumentsList(documents);
		ListModelList<CustomerDocument> listModelList = new ListModelList<CustomerDocument>(documents);
		this.listBoxCustomerDocuments.setModel(listModelList);
		this.listBoxCustomerDocuments.setItemRenderer(new CustomerDocumentsListModelItemRenderer());
		if(documents != null && documents.size() > 0){
			this.listBoxCustomerDocuments.setHeight((documents.size()*25)+50+"px");
		}else{
			this.listBoxCustomerDocuments.setHeight("50px");
		}
		logger.debug("Leaving");
	}

	public void onCheck$custRelation(Event event) throws Exception {
		logger.debug("Entering");
		this.custGroupID.setErrorMessage("");
		isCustRelated = this.custRelation.getSelectedItem().getValue().toString().equalsIgnoreCase(PennantConstants.CUSTRELATION_RELATED);
		doSetCustGroupMandatory();
		logger.debug("Leaving");
	}
	
	
	private void doFillCustRelation(String custRelation){
		logger.debug("Entering");
		for (int i=0;i<custRelationList.size();i++) {
			ValueLabel valueLabel = custRelationList.get(i);
			Radio radio = new Radio();
			radio.setId(valueLabel.getValue());
			radio.setValue(valueLabel.getValue());
			radio.setLabel(valueLabel.getLabel());
			if ("ENQ".equals(moduleType)) {
				radio.setDisabled(true);
			}else{
				radio.setDisabled(isReadOnly("CustomerDialog_custGroupID"));
			}	
			this.custRelation.appendChild(radio);
			if(StringUtils.trimToEmpty(custRelation).equalsIgnoreCase(valueLabel.getValue())){
				this.custRelation.setSelectedItem(radio);
			}
		}
		if(this.custRelation.getSelectedItem() == null){
			Radio radio = (Radio)this.custRelation.getFellowIfAny(PennantConstants.CUSTRELATION_NOTRELATED);
			if(radio != null){
				this.custRelation.setSelectedItem(radio);
			}
		}
		doSetCustGroupMandatory();
		logger.debug("Leaving");
}
	
	
	private void doSetCustGroupMandatory(){
		logger.debug("Entering");
		if(isCustRelated){
			this.custGroupID.setMandatoryStyle(true);
		}else{
			this.custGroupID.setMandatoryStyle(false);
		}
		logger.debug("Leaving");
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerDetails
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerDetails aCustomerDetails, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerDetails.getBefImage(), aCustomerDetails);
		return new AuditHeader(String.valueOf(aCustomerDetails.getCustID()), String.valueOf(aCustomerDetails.getCustID()), null, null, auditDetail, aCustomerDetails.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
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
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
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

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
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

	public List<CustomerEMail> getEmailList() {
		return emailList;
	}

	public void setEmailList(List<CustomerEMail> emailList) {
		this.emailList = emailList;
	}

	public List<CustomerAddres> getAddressList() {
		return addressList;
	}

	public void setAddressList(List<CustomerAddres> addressList) {
		this.addressList = addressList;
	}

	public List<CustomerPhoneNumber> getPhoneNumberList() {
		return phoneNumberList;
	}

	public void setPhoneNumberList(List<CustomerPhoneNumber> phoneNumberList) {
		this.phoneNumberList = phoneNumberList;
	}

	public List<CustomerDocument> getDocumentsList() {
		return documentsList;
	}

	public void setDocumentsList(List<CustomerDocument> documentsList) {
		this.documentsList = documentsList;
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
		if(directorList != null){
			for (DirectorDetail directorDetail : directorList) {
				if(StringUtils.trimToEmpty(directorDetail.getDesignation()).equals("") && 
						!StringUtils.trimToEmpty(directorDetail.getLovDescDesignationName()).equals("")){
					Designation designation = PennantAppUtil.getDesignationDetails(directorDetail.getLovDescDesignationName());
					if(designation != null ){
						directorDetail.setDesignation(designation.getDesgCode());
					}
				}
			}
		}
		logger.debug("Leaving");
	}

	private void doCheckSubSector(){
		if (this.custSector.getValue().equals("")) {
			this.custSubSector.setReadonly(true);
			this.custSubSector.setMandatoryStyle(false);
		}else{
			this.custSubSector.setReadonly(isReadOnly("CustomerDialog_custSubSector"));
			this.custSubSector.setMandatoryStyle(!isReadOnly("CustomerDialog_custSubSector"));
		}
	}
	
	private void doSetCustTypeFilters(String custCtgType){
		if (!StringUtils.trimToEmpty(custCtgType).equals("")) {
			Filter filter[]=new Filter[1];
			filter[0]=new Filter("CustTypeCtg", StringUtils.trimToEmpty(custCtgType), Filter.OP_EQUAL);
			this.custTypeCode.setFilters(filter);				
		}
	}
	private void doCheckCustomerType(){
		if (corpCustomer) {
			this.rowDualNationUSPerson.setVisible(false);
			this.rowShrNameMotherName.setVisible(false);
			this.rowGivenFullName.setVisible(false);
		}else{
			this.rowDualNationUSPerson.setVisible(true);
			this.rowShrNameMotherName.setVisible(true);
			this.rowGivenFullName.setVisible(true);
		}
	}
	
	
	public DirectorDetailService getDirectorDetailService() {
		return directorDetailService;
	}

	public void setDirectorDetailService(DirectorDetailService directorDetailService) {
		this.directorDetailService = directorDetailService;
	}
	
}