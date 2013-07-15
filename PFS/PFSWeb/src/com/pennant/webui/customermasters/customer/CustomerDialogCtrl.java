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
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.applicationmaster.RejectDetail;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CorporateCustomerDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerBalanceSheet;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPRelation;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.staticparms.Language;
import com.pennant.backend.model.systemmasters.BlackListReasonCode;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Department;
import com.pennant.backend.model.systemmasters.DispatchMode;
import com.pennant.backend.model.systemmasters.EmpStsCode;
import com.pennant.backend.model.systemmasters.EmploymentType;
import com.pennant.backend.model.systemmasters.Gender;
import com.pennant.backend.model.systemmasters.GeneralDepartment;
import com.pennant.backend.model.systemmasters.GeneralDesignation;
import com.pennant.backend.model.systemmasters.GroupStatusCode;
import com.pennant.backend.model.systemmasters.Industry;
import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennant.backend.model.systemmasters.NationalityCode;
import com.pennant.backend.model.systemmasters.Profession;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.model.systemmasters.Segment;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.model.systemmasters.SubSegment;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.customer.model.CustomerAddressListModelItemRenderer;
import com.pennant.webui.customermasters.customer.model.CustomerBalanceSheetListItemRenderer;
import com.pennant.webui.customermasters.customer.model.CustomerDirectorListItemRenderer;
import com.pennant.webui.customermasters.customer.model.CustomerDocumentsListModelItemRenderer;
import com.pennant.webui.customermasters.customer.model.CustomerEmailListModelItemRenderer;
import com.pennant.webui.customermasters.customer.model.CustomerIncomeListItemRenderer;
import com.pennant.webui.customermasters.customer.model.CustomerPRelationListModelItemRenderer;
import com.pennant.webui.customermasters.customer.model.CustomerPhoneNumListModelItemRenderer;
import com.pennant.webui.customermasters.customer.model.CustomerRatinglistItemRenderer;
import com.pennant.webui.dedup.dedupparm.FetchDedupDetails;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

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
	protected Window 		window_CustomerDialog; 		// autowired
	
	//Basic Details Tab-->1.Key Details
	protected Longbox 		custID; 					// autowired
	protected Textbox 		custCIF; 					// autowired
	protected Textbox 		customerCIF; 				// autowired
	protected Textbox 		custCoreBank; 				// autowired
	protected Textbox 		custTypeCode; 				// autowired
	protected Textbox 		custDftBranch; 				// autowired
	protected Longbox 		custGroupID; 				// autowired
	protected Textbox 		custBaseCcy; 				// autowired
	protected Textbox 		custLng; 					// autowired
	
	//Basic Details Tab-->2.Personal Details(Retail Customer)
	protected Textbox 		custGenderCode; 			// autowired
	protected Textbox	 	custSalutationCode; 		// autowired
	protected Textbox 		custFName; 					// autowired
	protected Textbox 		custMName; 					// autowired
	protected Textbox 		custLName; 					// autowired
	protected Textbox 		custShrtName; 				// autowired
	protected Textbox 		custFNameLclLng; 			// autowired
	protected Textbox 		custMNameLclLng; 			// autowired
	protected Textbox 		custLNameLclLng; 			// autowired
	protected Textbox 		custShrtNameLclLng; 		// autowired
	protected Datebox 		custDOB; 					// autowired
	protected Textbox 		custPOB; 					// autowired
	protected Textbox 		custCOB; 					// autowired
	protected Textbox 		custMotherMaiden; 			// autowired
	protected Checkbox 		custIsMinor; 				// autowired
	protected Textbox 		custProfession; 			// autowired
	protected Textbox 		custMaritalSts; 			// autowired
	private Groupbox 		gb_personalDetails;			// autowired
	
	// Local Languages rows Checking for Mandatory or not
	protected Row row_localLngFM;						
	protected Row row_localLngLS;
	
	//Basic Details Tab-->3.Organization Details(Corporate Customer)
	protected Textbox 		corpCustOrgName; 			// autowired
	protected Textbox 		corpCustShrtName; 			// autowired
	protected Textbox 		corpCustOrgNameLclLng; 		// autowired
	protected Textbox 		corpCustShrtNameLclLng; 	// autowired
	protected Textbox 		corpCustPOB; 				// autowired
	protected Textbox 		corpCustCOB; 				// autowired
	protected Button 		btnSearchCorpCustCOB; 		// autowired
	protected Textbox 		lovDescCorpCustCOBName;		// autowired
	private Groupbox 		gb_corporateCustomerPersonalDetails;
	
	// Local Languages rows Checking for Mandatory or not of CorporateCustomer
	protected Row row_localLngCorpCustCS;
	
	//Generic Information Tab-->1.General Details
	protected Textbox 		custSts; 					// autowired
	protected Datebox 		custStsChgDate; 			// autowired
	protected Textbox 		custGroupSts; 				// autowired
	protected Checkbox 		custIsBlocked; 				// autowired
	protected Checkbox 		custIsActive; 				// autowired
	protected Checkbox 		custIsClosed; 				// autowired
	protected Datebox 		custClosedOn; 				// autowired
	protected Textbox 		custInactiveReason; 		// autowired
	protected Checkbox 		custIsDecease; 				// autowired
	protected Checkbox 		custIsDormant; 				// autowired
	protected Checkbox 		custIsDelinquent; 			// autowired
	protected Datebox 		custFirstBusinessDate; 		// autowired
	protected Textbox 		custPassportNo; 			// autowired
	protected Datebox 		custPassportExpiry;			// autowired
	protected Textbox 		custVisaNum;				// autowired
	protected Datebox 		custVisaExpiry;				// autowired
	protected Textbox 		custTradeLicenceNum;		// autowired
	protected Datebox 		custDateOfIncorporation;	// autowired
	protected Datebox 		custTradeLicenceExpiry;		// autowired
	protected Checkbox 		custIsStaff; 				// autowired
	protected Textbox 		custStaffID; 				// autowired
	protected Textbox 		custEmpSts; 				// autowired
	protected Checkbox 		custIsBlackListed; 			// autowired
	protected Textbox 		custBLRsnCode; 				// autowired
	protected Checkbox 		custIsRejected; 			// autowired
	protected Textbox 		custRejectedRsn; 			// autowired
	
	//Set visible or not depend on Customer Category type
	protected Row 			row_retailPPT;
	protected Row 			row_retailVisa;
	protected Row 			row_corpTL;
	protected Row 			row_custStaff;
	protected Row 			row_corpTLED;
	protected Row 			row_EmpSts;
	
	//Demographic Details Tab-->1.Segmentation Details
	protected Textbox 		custCtgCode; 				// autowired
	protected Textbox 		custSector; 				// autowired
	protected Textbox 		custSubSector; 				// autowired
	protected Textbox 		custIndustry; 				// autowired
	protected Textbox 		custSegment; 				// autowired
	protected Textbox 		custSubSegment; 			// autowired
	private Groupbox 		gb_familyDetails;			// autowired
	
	//KYC Details Tab -->1.Income Details
	protected Groupbox 		gb_incomeDetails;			// autowired
	protected Decimalbox 	custTotalIncome; 			// autowired
	
	//KYC Details Tab-->2.Identity Details
	protected Textbox 		custParentCountry; 			// autowired
	protected Textbox 		custResdCountry; 			// autowired
	protected Textbox 		custRiskCountry; 			// autowired
	protected Textbox 		custNationality; 			// autowired
	
	//Preferential Details Tab-->1.Non-Financial RelationShip Details
	protected Textbox 		custDSA; 					// autowired
	protected Textbox 		custDSADept; 				// autowired
	protected Textbox 		custRO1; 					// autowired
	protected Textbox 		custRO2; 					// autowired
	protected Textbox 		custReferedBy; 				// autowired

	//Preferential Details Tab-->2.Statement Details	
	protected Textbox 		custStmtFrq; 				// autowired
	protected Checkbox 		custIsStmtCombined; 		// autowired
	protected Datebox 		custStmtLastDate; 			// autowired
	protected Datebox 		custStmtNextDate; 			// autowired
	protected Textbox 		custStmtDispatchMode; 		// autowired
	
	// Space Id's Checking for Mandatory or not
	protected Space space_closedDate;
	protected Space space_inactiveReason;
	protected Space space_isStaff;
	protected Space space_blackListReason;
	protected Space space_rejectedReason;
	protected Space space_custGroupSts;
	protected Space space_custTradeLicenceExpiry;
	protected Space space_custPassportExpiry;
	protected Space space_custVisaExpiry;
	protected Space space_custMNameLclLng;
	protected Space space_custMaritalStaus;
	protected Space space_custProfession;
	protected Space space_custMotherMaiden;
	protected Space space_custCOB;
	protected Space space_custPOB;
	protected Space space_custDOB;
	protected Space space_custEmpSts;

	// Customer Employeement details Field Declarations
	protected Textbox 		custEmpName;		// autowired
	protected Datebox 		custEmpFrom;		// autowired
	protected Textbox 		custEmpID;			// autowired
	protected Textbox 		custEmpHNbr;		// autowired
	protected Textbox 		custEmpFlatNbr;		// autowired
	protected Textbox 		custEmpAddrStreet;	// autowired
	protected Textbox 		custEMpAddrLine1;	// autowired
	protected Textbox 		custEMpAddrLine2;	// autowired
	protected Textbox 		custEmpPOBox;		// autowired
	protected Textbox 		custEmpAddrPhone;	// autowired
	protected Textbox 		custEmpDesg;		// autowired
	protected Textbox 		custEmpDept;		// autowired
	protected Textbox 		custEmpType;		// autowired
	protected Textbox 		custEmpAddrCity;	// autowired
	protected Textbox 		custEmpAddrProvince;// autowired
	protected Textbox 		custEmpAddrCountry;	// autowired
	protected Textbox 		custEmpAddrZIP;		// autowired
	
	//Non-Financial and Financial details for Corporate Customer Details
	protected Textbox 	 name; 					// autowired
	protected Textbox 	 phoneNumber; 			// autowired
	protected Textbox 	 phoneNumber1;			// autowired
	protected Textbox 	 emailId; 				// autowired
  	protected Datebox 	 bussCommenceDate; 		// autowired
  	protected Datebox 	 servCommenceDate; 		// autowired
  	protected Datebox 	 bankRelationshipDate; 	// autowired
	protected Decimalbox paidUpCapital; 		// autowired
	protected Decimalbox authorizedCapital; 	// autowired
	protected Decimalbox reservesAndSurPlus; 	// autowired
	protected Decimalbox intangibleAssets; 		// autowired
	protected Decimalbox tangibleNetWorth; 		// autowired
	protected Decimalbox longTermLiabilities; 	// autowired
	protected Decimalbox capitalEmployed; 		// autowired
	protected Decimalbox investments; 			// autowired
	protected Decimalbox nonCurrentAssets; 		// autowired
	protected Decimalbox netWorkingCapital;	 	// autowired
	protected Decimalbox netSales; 				// autowired
	protected Decimalbox otherIncome; 			// autowired
	protected Decimalbox netProfitAfterTax; 	// autowired
	protected Decimalbox depreciation; 			// autowired
	protected Decimalbox cashAccurals; 			// autowired
	protected Decimalbox annualTurnover; 		// autowired
	protected Decimalbox returnOnCapitalEmp; 	// autowired
	protected Decimalbox currentAssets; 		// autowired
	protected Decimalbox currentLiabilities; 	// autowired
	protected Decimalbox currentBookValue; 		// autowired
	protected Decimalbox currentMarketValue; 	// autowired
	protected Decimalbox promotersShare; 		// autowired
	protected Decimalbox associatesShare; 		// autowired
	protected Decimalbox publicShare; 			// autowired
	protected Decimalbox finInstShare; 			// autowired
	protected Decimalbox others; 				// autowired
	
	protected Label 		recordStatus; 		// autowired
	protected Radiogroup 	userAction;
	protected Groupbox	 	gb_Action;
	protected Groupbox	 	gb_statusDetails;
	protected Row 			statusRow;

	private CustomerDetails customerDetails; // overhanded per param
	private transient CustomerListCtrl customerListCtrl; // overhanded per param
	String parms[] = new String[4];

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	
	//Basic Details Tab-->1.Key Details
	private transient long 	 oldVar_custID;
	private transient String oldVar_custCIF;
	private transient String oldVar_custCoreBank;
	private transient String oldVar_custTypeCode;
	private transient String oldVar_custDftBranch;
	private transient long oldVar_custGroupID;
	private transient String oldVar_custBaseCcy;
	private transient String oldVar_custLng;
	
	//Basic Details Tab-->2.Personal Details(Retail Customer) 
	// && 3.Organization Details(Corporate Customer)
	private transient String oldVar_custGenderCode;
	private transient String oldVar_custSalutationCode;
	private transient String oldVar_custFName;
	private transient String oldVar_custMName;
	private transient String oldVar_custLName;
	private transient String oldVar_custShrtName;
	private transient String oldVar_custFNameLclLng;
	private transient String oldVar_custMNameLclLng;
	private transient String oldVar_custLNameLclLng;
	private transient String oldVar_custShrtNameLclLng;
	private transient Date 	 oldVar_custDOB;
	private transient String oldVar_custPOB;
	private transient String oldVar_custCOB;
	private transient String oldVar_custMotherMaiden;
	private transient String oldVar_custProfession;
	private transient String oldVar_custMaritalSts;
	private transient boolean oldVar_custIsMinor;
	
	//Generic Information Tab-->1.General Details
	private transient String oldVar_custSts;
	private transient Date oldVar_custStsChgDate;
	private transient String oldVar_custGroupSts;
	private transient boolean oldVar_custIsBlocked;
	private transient boolean oldVar_custIsActive;
	private transient boolean oldVar_custIsClosed;
	private transient String oldVar_custInactiveReason;
	private transient boolean oldVar_custIsDecease;
	private transient boolean oldVar_custIsDormant;
	private transient boolean oldVar_custIsDelinquent;
	private transient String oldVar_custTradeLicenceNum;
	private transient Date oldVar_custDateOfIncorporation;
	private transient Date oldVar_custTradeLicenceExpiry;
	private transient Date oldVar_custPassportExpiry;
	private transient String oldVar_custVisaNum;
	private transient Date oldVar_custVisaExpiry;
	private transient boolean oldVar_custIsStaff;
	private transient String oldVar_custStaffID;
	private transient String oldVar_custPassportNo;
	private transient Date oldVar_custFirstBusinessDate;
	private transient Date oldVar_custClosedOn;
	private transient String oldVar_custEmpSts;
	private transient boolean oldVar_custIsBlackListed;
	private transient String oldVar_custBLRsnCode;
	private transient boolean oldVar_custIsRejected;
	private transient String oldVar_custRejectedRsn;
	
	//Demographic Details Tab-->1.Segmentation Details
	private transient String oldVar_custCtgCode;
	private transient String oldVar_custIndustry;
	private transient String oldVar_custSector;
	private transient String oldVar_custSubSector;
	private transient String oldVar_custSegment;
	private transient String oldVar_custSubSegment;

	//KYC Details Tab-->1.Income Details
	private transient BigDecimal oldVar_custTotalIncome;
	
	//KYC Details Tab-->2.Identity Details
	private transient String oldVar_custParentCountry;
	private transient String oldVar_custResdCountry;
	private transient String oldVar_custRiskCountry;
	private transient String oldVar_custNationality;
	
	//Preferential Details Tab-->1.Non-Financial RelationShip Details
	private transient String oldVar_custDSA;
	private transient String oldVar_custDSADept;
	private transient String oldVar_custRO1;
	private transient String oldVar_custRO2;	
	private transient String oldVar_custReferedBy;
	
	//Preferential Details Tab-->2.Statement Details	
	private transient String oldVar_custStmtFrq;
	private transient boolean oldVar_custIsStmtCombined;
	private transient Date oldVar_custStmtLastDate;
	private transient Date oldVar_custStmtNextDate;
	private transient String oldVar_custStmtDispatchMode;

	// Customer Employee Old Variable Field Declaration
	private transient String oldVar_custEmpName;
	private transient Date oldVar_custEmpFrom;
	private transient String oldVar_custEmpDesg;
	private transient String oldVar_custEmpDept;
	private transient String oldVar_custEmpID;
	private transient String oldVar_custEmptype;
	private transient String oldVar_custEmpHNbr;
	private transient String oldVar_custEmpFlatNbr;
	private transient String oldVar_custEmpAddrStreet;
	private transient String oldVar_custEmpAddrLine1;
	private transient String oldVar_custEmpAddrLine2;
	private transient String oldVar_custEmpPOBox;
	private transient String oldVar_custEmpAddrPhone;
	private transient String oldVar_custEmpAddrCountry;
	private transient String oldVar_custEmpAddrProvince;
	private transient String oldVar_custEmpAddrCity;
	private transient String oldVar_custEmpAddrZIP;
	
	//Non-Financial and Financial Details for Corporate Customer Details
	private transient String  			oldVar_name;
	private transient String  			oldVar_phoneNumber;
	private transient String  			oldVar_phoneNumber1;
	private transient String  			oldVar_emailId;
	private transient Date  			oldVar_bussCommenceDate;
	private transient Date  			oldVar_servCommenceDate;
	private transient Date  			oldVar_bankRelationshipDate;
	private transient BigDecimal  		oldVar_paidUpCapital;
	private transient BigDecimal  		oldVar_authorizedCapital;
	private transient BigDecimal  		oldVar_reservesAndSurPlus;
	private transient BigDecimal  		oldVar_intangibleAssets;
	private transient BigDecimal  		oldVar_tangibleNetWorth;
	private transient BigDecimal  		oldVar_longTermLiabilities;
	private transient BigDecimal  		oldVar_capitalEmployed;
	private transient BigDecimal  		oldVar_investments;
	private transient BigDecimal  		oldVar_nonCurrentAssets;
	private transient BigDecimal  		oldVar_netWorkingCapital;
	private transient BigDecimal  		oldVar_netSales;
	private transient BigDecimal  		oldVar_otherIncome;
	private transient BigDecimal  		oldVar_netProfitAfterTax;
	private transient BigDecimal  		oldVar_depreciation;
	private transient BigDecimal  		oldVar_cashAccurals;
	private transient BigDecimal  		oldVar_annualTurnover;
	private transient BigDecimal  		oldVar_returnOnCapitalEmp;
	private transient BigDecimal  		oldVar_currentAssets;
	private transient BigDecimal  		oldVar_currentLiabilities;
	private transient BigDecimal  		oldVar_currentBookValue;
	private transient BigDecimal  		oldVar_currentMarketValue;
	private transient BigDecimal  		oldVar_promotersShare;
	private transient BigDecimal  		oldVar_associatesShare;
	private transient BigDecimal  		oldVar_publicShare;
	private transient BigDecimal  		oldVar_finInstShare;
	private transient BigDecimal  		oldVar_others;

	private transient String oldVar_recordStatus;
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
	protected Button btnSearchCustCtgCode; // autowire
	protected Textbox lovDescCustCtgCodeName;
	private transient String oldVar_lovDescCustCtgCodeName;

	protected Button btnSearchCustTypeCode; // autowire
	protected Textbox lovDescCustTypeCodeName;
	private transient String oldVar_lovDescCustTypeCodeName;

	protected Button btnSearchCustSalutationCode; // autowire
	protected Textbox lovDescCustSalutationCodeName;
	private transient String oldVar_lovDescCustSalutationCodeName;

	protected Button btnSearchCustDftBranch; // autowire
	protected Textbox lovDescCustDftBranchName;
	private transient String oldVar_lovDescCustDftBranchName;

	protected Button btnSearchCustGenderCode; // autowire
	protected Textbox lovDescCustGenderCodeName;
	private transient String oldVar_lovDescCustGenderCodeName;

	protected Button btnSearchCustCOB; // autowire
	protected Textbox lovDescCustCOBName;
	private transient String oldVar_lovDescCustCOBName;

	protected Button btnSearchCustDSADept; // autowire
	protected Textbox lovDescCustDSADeptName;
	private transient String oldVar_lovDescCustDSADeptName;

	protected Button btnSearchCustRO1; // autowire
	protected Textbox lovDescCustRO1Name;
	private transient String oldVar_lovDescCustRO1Name;

	protected Button btnSearchCustRO2; // autowire
	protected Textbox lovDescCustRO2Name;
	private transient String oldVar_lovDescCustRO2Name;

	protected Button btnSearchCustSts; // autowire
	protected Textbox lovDescCustStsName;
	private transient String oldVar_lovDescCustStsName;

	protected Button btnSearchCustGroupSts; // autowire
	protected Textbox lovDescCustGroupStsName;
	private transient String oldVar_lovDescCustGroupStsName;

	protected Button btnSearchCustIndustry; // autowire
	protected Textbox lovDescCustIndustryName;
	private transient String oldVar_lovDescCustIndustryName;

	protected Button btnSearchCustSector; // autowire
	protected Textbox lovDescCustSectorName;
	private transient String oldVar_lovDescCustSectorName;

	protected Button btnSearchCustSubSector; // autowire
	protected Textbox lovDescCustSubSectorName;
	private transient String oldVar_lovDescCustSubSectorName;

	protected Button btnSearchCustProfession; // autowire
	protected Textbox lovDescCustProfessionName;
	private transient String oldVar_lovDescCustProfessionName;

	protected Button btnSearchCustMaritalSts; // autowire
	protected Textbox lovDescCustMaritalStsName;
	private transient String oldVar_lovDescCustMaritalStsName;

	protected Button btnSearchCustEmpSts; // autowire
	protected Textbox lovDescCustEmpStsName;
	private transient String oldVar_lovDescCustEmpStsName;

	protected Button btnSearchCustSegment; // autowire
	protected Textbox lovDescCustSegmentName;
	private transient String oldVar_lovDescCustSegmentName;

	protected Button btnSearchCustBaseCcy; // autowire
	protected Textbox lovDescCustBaseCcyName;
	private transient String oldVar_lovDescCustBaseCcyName;

	protected Button btnSearchCustResdCountry; // autowire
	protected Textbox lovDescCustResdCountryName;
	private transient String oldVar_lovDescCustResdCountryName;

	protected Button btnSearchCustNationality; // autowire
	protected Textbox lovDescCustNationalityName;
	private transient String oldVar_lovDescCustNationalityName;

	protected Button btnSearchCustBLRsnCode; // autowire
	protected Textbox lovDescCustBLRsnCodeName;
	private transient String oldVar_lovDescCustBLRsnCodeName;

	protected Button btnSearchCustRejectedRsn; // autowire
	protected Textbox lovDescCustRejectedRsnName;
	private transient String oldVar_lovDescCustRejectedRsnName;

	protected Button btnSearchcustGroupID; // autowire
	protected Textbox lovDesccustGroupIDName;
	private transient String oldVar_lovDesccustGroupIDName;

	protected Button btnSearchCustSubSegment; // autowire
	protected Textbox lovDescCustSubSegmentName;
	private transient String oldVar_lovDescCustSubSegmentName;

	protected Combobox custStmtFrqCode; // autowired
	protected Combobox custStmtFrqMth; // autowired
	protected Combobox custStmtFrqDays; // autowired

	protected Button btnSearchCustStmtDispatchMode; // autowire
	protected Textbox lovDescDispatchModeDescName;
	private transient String oldVar_lovDescDispatchModeDescName;

	protected Button btnSearchCustLng; // autowire
	protected Textbox lovDescCustLngName;
	private transient String oldVar_lovDescCustLngName;

	// Customer Employee Details Search Button Declaration
	protected Button btnSearchCustEmpDesg; // autowire
	protected Textbox lovDescCustEmpDesgName;
	private transient String oldVar_lovDescCustEmpDesgName;

	protected Button btnSearchCustEmpDept; // autowire
	protected Textbox lovDescCustEmpDeptName;
	private transient String oldVar_lovDescCustEmpDeptName;

	protected Button btnSearchCustEmpType; // autowire
	protected Textbox lovDescCustEmpTypeName;
	private transient String oldVar_lovDescCustEmpTypeName;

	protected Button btnSearchCustEmpAddrCity; // autowire
	protected Textbox lovDescCustEmpAddrCityName;
	private transient String oldVar_lovDescCustEmpAddrCityName;

	protected Button btnSearchCustEmpAddrProvince; // autowire
	protected Textbox lovDescCustEmpAddrProvinceName;
	private transient String oldVar_lovDescCustEmpAddrProvinceName;

	protected Button btnSearchCustEmpAddrCountry; // autowire
	protected Textbox lovDescCustEmpAddrCountryName;
	private transient String oldVar_lovDescCustEmpAddrCountryName;

	// Customer ratings List
	protected Button btnNew_CustomerRatings;
	protected Borderlayout borderLayout_CustomerRatingList;
	protected Paging pagingCustomerRatingList;
	protected Listbox listBoxCustomerRating;
	private int countRows = PennantConstants.listGridSize;

	// Customer address details List
	protected Button btnNew_CustomerAddress;
	protected Borderlayout borderLayout_CustomerAddressList;
	protected Paging pagingCustomerAddressList;
	protected Listbox listBoxCustomerAddress;

	// Customer email address details List
	protected Button btnNew_CustomerEmailAddress;
	protected Borderlayout borderLayout_CustomerEmailIdList;
	protected Paging pagingCustomerEmailList;
	protected Listbox listBoxCustomerEmailAddress;

	// Customer Phone Numbers details List
	protected Button btnNew_CustomerPhoneNumbers;
	protected Borderlayout borderLayout_CustomerPhoneNumberList;
	protected Paging pagingCustomerPhoneList;
	protected Listbox listBoxCustomerPhoneNumbers;

	// Customer Documents details List
	protected Button btnNew_CustomerDocuments;
	protected Borderlayout borderLayout_CustomerDocumentList;
	protected Paging pagingCustomerDocumentList;
	protected Listbox listBoxCustomerDocuments;

	// Customer Income details List
	protected Button btnNew_CustomerIncome;
	protected Paging pagingCustomerIncomeList;
	protected Listbox listBoxCustomerIncome;
	protected Listheader listheader_CustInc_CustIncomeType;
	BigDecimal income = new BigDecimal(0);

	// Customer PRelation details List
	protected Button btnNew_CustomerPRelation;
	protected Paging pagingCustomerPRelationList;
	protected Listbox listBoxCustomerPRelation;
	
	// Customer Director details List
	protected Button btnNew_CustomerDirector;
	protected Paging pagingCustomerDirectorList;
	protected Listbox listBoxCustomerDirectors;
	
	// Customer Balanace Sheet details List
	protected Button btnNew_CustomerBalanceSheet;
	protected Paging pagingCustomerBalanceSheetList;
	protected Listbox listBoxCustomerBalanceSheet;

	// CustomerRelated list Declaration
	private List<CustomerRating> ratingsList = new ArrayList<CustomerRating>();
	private List<CustomerAddres> addressList = new ArrayList<CustomerAddres>();
	private List<CustomerEMail> emailList = new ArrayList<CustomerEMail>();
	private List<CustomerPhoneNumber> phoneNumberList = new ArrayList<CustomerPhoneNumber>();
	private List<CustomerDocument> documentsList = new ArrayList<CustomerDocument>();
	private List<CustomerIncome> incomeList = new ArrayList<CustomerIncome>();
	private List<CustomerPRelation> pRelationList = new ArrayList<CustomerPRelation>();
	private List<DirectorDetail> directorsList = new ArrayList<DirectorDetail>();
	private List<CustomerBalanceSheet> balanceSheetList = new ArrayList<CustomerBalanceSheet>();

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private List<CustomerRating> oldVar_RatingsList = new ArrayList<CustomerRating>();
	private List<CustomerAddres> oldVar_AddressList = new ArrayList<CustomerAddres>();
	private List<CustomerEMail> oldVar_EmailList = new ArrayList<CustomerEMail>();
	private List<CustomerPhoneNumber> oldVar_PhoneNumberList = new ArrayList<CustomerPhoneNumber>();
	private List<CustomerDocument> oldVar_DocumentsList = new ArrayList<CustomerDocument>();
	private List<CustomerIncome> oldVar_IncomeList = new ArrayList<CustomerIncome>();
	private List<CustomerPRelation> oldVar_PRelationList = new ArrayList<CustomerPRelation>();
	private List<DirectorDetail> oldVar_DirectorsList = new ArrayList<DirectorDetail>();
	private List<CustomerBalanceSheet> oldVar_BalanceSheetList = new ArrayList<CustomerBalanceSheet>();

	// Customer Related PagedListWrapper Classes Declaration
	private PagedListWrapper<CustomerRating> customerRatingsPagedListWrapper;
	private PagedListWrapper<CustomerAddres> customerAddresPagedListWrapper;
	private PagedListWrapper<CustomerEMail> customerEmailPagedListWrapper;
	private PagedListWrapper<CustomerPhoneNumber> customerPhoneNumberPagedListWrapper;
	private PagedListWrapper<CustomerDocument> customerDocumentsPagedListWrapper;
	private PagedListWrapper<CustomerIncome> customerIncomePagedListWrapper;
	private PagedListWrapper<CustomerPRelation> customerPRelationPagedListWrapper;
	private PagedListWrapper<DirectorDetail> customerDirectorsPagedListWrapper;
	private PagedListWrapper<CustomerBalanceSheet> customerBalanceSheetPagedListWrapper;
	
	// Customer Tabs Declaration
	private Tab basicDetails;
	private Tab genericInformation;
	private Tab demographicDetails;
	private Tab kYCDetails;
	private Tab preferentialDetails;
	private Tab employmentDetails;
	private Tab nonFinancialDetails;
	private Tab financialDetails;

	protected Groupbox gb_employerDetails;

	// Declaration of Service(s) & DAO(s)
	private transient CustomerDetailsService customerDetailsService;
	private transient DedupParmService dedupParmService;
	private transient CustomerEmploymentDetail customerEmploymentDetail;
	private boolean isRecordSaved = false;
	private int ccyFormatter  = 0;
	
	private String moduleType= "";

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

		/* set components visible dependent of the users rights */
		doCheckRights();

		// Set the setter objects for PagedListwrapper classes to Initialize
		setCustomerRatingsPagedListWrapper();
		setCustomerAddresPagedListWrapper();
		setCustomerIncomePagedListWrapper();
		setCustomerPRelationPagedListWrapper();
		setCustomerEmailPagedListWrapper();
		setCustomerPhoneNumberPagedListWrapper();
		setCustomerDocumentsPagedListWrapper();
		setCustomerDirectorsPagedListWrapper();
		setCustomerBalanceSheetPagedListWrapper();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,
				this.btnNotes);

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
		
		if(args.containsKey("moduleType")) {
			this.moduleType = (String)args.get("moduleType");
		}

		Customer customer = getCustomerDetails().getCustomer();
		ccyFormatter = customer.getLovDescCcyFormatter();
		
		if("ENQ".equals(moduleType)) {
			doLoadWorkFlow(false, customer.getWorkflowId(),customer.getNextTaskId());
		} else{
			doLoadWorkFlow(customer.isWorkflow(), customer.getWorkflowId(),customer.getNextTaskId());
		}

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(),"CustomerDialog");
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

		// set Field Properties
		doSetFieldProperties();
		doStoreInitValues();
		doShowDialog(this.customerDetails);
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		//Basic Details Tab-->1.Key Details
		this.custCoreBank.setMaxlength(50);
		this.custTypeCode.setMaxlength(8);
		this.custDftBranch.setMaxlength(8);
		this.custBaseCcy.setMaxlength(3);
		this.custLng.setMaxlength(2);
		
		//Basic Details Tab-->2.Personal Details(Retail Customer)
		this.custGenderCode.setMaxlength(8);
		this.custSalutationCode.setMaxlength(8);
		this.custFName.setMaxlength(50);
		this.custMName.setMaxlength(50);
		this.custLName.setMaxlength(50);
		this.custShrtName.setMaxlength(50);
		this.custFNameLclLng.setMaxlength(50);
		this.custMNameLclLng.setMaxlength(50);
		this.custLNameLclLng.setMaxlength(50);
		this.custShrtNameLclLng.setMaxlength(50);
		this.custDOB.setFormat(PennantConstants.dateFormat);
		this.custPOB.setMaxlength(100);
		this.custCOB.setMaxlength(2);
		this.custMotherMaiden.setMaxlength(50);
		this.custProfession.setMaxlength(8);
		this.custMaritalSts.setMaxlength(8);
		
		//Basic Details Tab-->3.Organization Details(Corporate Customer)
		this.corpCustOrgName.setMaxlength(50);
		this.corpCustShrtName.setMaxlength(50);
		this.corpCustPOB.setMaxlength(100);
		this.corpCustCOB.setMaxlength(2);
		this.corpCustOrgNameLclLng.setMaxlength(50);
		this.corpCustShrtNameLclLng.setMaxlength(50);
		
		//Generic Information Tab-->1.General Details
		this.custPassportNo.setMaxlength(50);
		this.custSts.setMaxlength(8);
		this.custGroupSts.setMaxlength(8);
		this.custTradeLicenceNum.setMaxlength(20);
		this.custPassportExpiry.setFormat(PennantConstants.dateFormat);
		this.custVisaExpiry.setFormat(PennantConstants.dateFormat);
		this.custTradeLicenceExpiry.setFormat(PennantConstants.dateFormat);
		this.custDateOfIncorporation.setFormat(PennantConstants.dateFormat);
		this.custClosedOn.setFormat(PennantConstants.dateFormat);
		this.custVisaNum.setMaxlength(20);
		this.custStaffID.setMaxlength(8);
		this.custEmpSts.setMaxlength(8);
		this.custBLRsnCode.setMaxlength(8);
		this.custRejectedRsn.setMaxlength(8);
		this.custFirstBusinessDate.setFormat(PennantConstants.dateFormat);
		
		//Demographic Details Tab-->1.Segmentation Details
		this.custCtgCode.setMaxlength(8);
		this.custIndustry.setMaxlength(8);
		this.custSector.setMaxlength(8);
		this.custSubSector.setMaxlength(8);
		this.custSegment.setMaxlength(8);
		this.custSubSegment.setMaxlength(8);
		
		//KYC Details Tab--> 1.Income Details
		this.custTotalIncome.setMaxlength(18);
		this.custTotalIncome.setFormat(PennantAppUtil.getAmountFormate(ccyFormatter));
		
		//KYC Details Tab-->2.Identity Details
		this.custParentCountry.setMaxlength(2);
		this.custResdCountry.setMaxlength(2);
		this.custRiskCountry.setMaxlength(2);
		this.custNationality.setMaxlength(2);
		
		//Preferential Details Tab-->1.Non-Financial RelationShip Details
		this.custDSA.setMaxlength(8);
		this.custDSADept.setMaxlength(8);
		this.custRO1.setMaxlength(8);
		this.custRO2.setMaxlength(8);
		this.custReferedBy.setMaxlength(50);

		//Preferential Details Tab-->2.Statement Details	
		this.custStmtFrq.setMaxlength(8);
		this.custStmtNextDate.setFormat(PennantConstants.dateFormat);
		this.custStmtLastDate.setFormat(PennantConstants.dateFormat);
		this.custStmtDispatchMode.setMaxlength(2);
		
		//Customer Employement Details
		this.custEmpName.setMaxlength(50);
		this.custEmpFrom.setFormat(PennantConstants.dateFormat);
		this.custEmpID.setMaxlength(50);
		this.custEmpHNbr.setMaxlength(50);
		this.custEmpFlatNbr.setMaxlength(50);
		this.custEmpAddrStreet.setMaxlength(50);
		this.custEMpAddrLine1.setMaxlength(50);
		this.custEMpAddrLine2.setMaxlength(50);
		this.custEmpPOBox.setMaxlength(50);
		this.custEmpAddrPhone.setMaxlength(50);
		this.custEmpAddrZIP.setMaxlength(6);

		// Set Regexion For Customer CIF Field
		parms[0] = SystemParameterDetails.getSystemParameterValue("CIF_CHAR").toString();
		parms[1] = SystemParameterDetails.getSystemParameterValue("CIF_LENGTH").toString();

		this.CUSTCIF_REGEX = "[" + parms[0] + "]{" + parms[1] + "}";
		this.custCIF.setMaxlength(Integer.parseInt(parms[1]));
		
		//Non-Financial and Financial details for Corporate Customer Details
		this.name.setMaxlength(20);
		this.phoneNumber.setMaxlength(20);
		this.phoneNumber1.setMaxlength(20);
		this.emailId.setMaxlength(100);
	 	this.bussCommenceDate.setFormat(PennantConstants.dateFormat);
	 	this.servCommenceDate.setFormat(PennantConstants.dateFormat);
	 	this.bankRelationshipDate.setFormat(PennantConstants.dateFormat);
	  	this.paidUpCapital.setMaxlength(18);
	  	this.paidUpCapital.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.paidUpCapital.setScale(0);
	  	this.authorizedCapital.setMaxlength(18);
	  	this.authorizedCapital.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.authorizedCapital.setScale(0);
	  	this.reservesAndSurPlus.setMaxlength(18);
	  	this.reservesAndSurPlus.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.reservesAndSurPlus.setScale(0);
	  	this.intangibleAssets.setMaxlength(18);
	  	this.intangibleAssets.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.intangibleAssets.setScale(0);
	  	this.tangibleNetWorth.setMaxlength(18);
	  	this.tangibleNetWorth.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.tangibleNetWorth.setScale(0);
	  	this.longTermLiabilities.setMaxlength(18);
	  	this.longTermLiabilities.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.longTermLiabilities.setScale(0);
	  	this.capitalEmployed.setMaxlength(18);
	  	this.capitalEmployed.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.capitalEmployed.setScale(0);
	  	this.investments.setMaxlength(18);
	  	this.investments.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.investments.setScale(0);
	  	this.nonCurrentAssets.setMaxlength(18);
	  	this.nonCurrentAssets.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.nonCurrentAssets.setScale(0);
	  	this.netWorkingCapital.setMaxlength(18);
	  	this.netWorkingCapital.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.netWorkingCapital.setScale(0);
	  	this.netSales.setMaxlength(18);
	  	this.netSales.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.netSales.setScale(0);
	  	this.otherIncome.setMaxlength(18);
	  	this.otherIncome.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.otherIncome.setScale(0);
	  	this.netProfitAfterTax.setMaxlength(18);
	  	this.netProfitAfterTax.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.netProfitAfterTax.setScale(0);
	  	this.depreciation.setMaxlength(18);
	  	this.depreciation.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.depreciation.setScale(0);
	  	this.cashAccurals.setMaxlength(18);
	  	this.cashAccurals.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.cashAccurals.setScale(0);
	  	this.annualTurnover.setMaxlength(18);
	  	this.annualTurnover.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.annualTurnover.setScale(0);
	  	this.returnOnCapitalEmp.setMaxlength(18);
	  	this.returnOnCapitalEmp.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.returnOnCapitalEmp.setScale(0);
	  	this.currentAssets.setMaxlength(18);
	  	this.currentAssets.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.currentAssets.setScale(0);
	  	this.currentLiabilities.setMaxlength(18);
	  	this.currentLiabilities.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.currentLiabilities.setScale(0);
	  	this.currentBookValue.setMaxlength(18);
	  	this.currentBookValue.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.currentBookValue.setScale(0);
	  	this.currentMarketValue.setMaxlength(18);
	  	this.currentMarketValue.setFormat(PennantAppUtil.getAmountFormate(0));
	  	this.currentMarketValue.setScale(0);
	  	this.promotersShare.setMaxlength(5);
	  	this.promotersShare.setFormat(PennantConstants.rateFormate2);
	  	this.promotersShare.setScale(2);
	  	this.associatesShare.setMaxlength(5);
	  	this.associatesShare.setFormat(PennantConstants.rateFormate2);
	  	this.associatesShare.setScale(2);
	  	this.publicShare.setMaxlength(5);
	  	this.publicShare.setFormat(PennantConstants.rateFormate2);
	  	this.publicShare.setScale(2);
	  	this.finInstShare.setMaxlength(5);
	  	this.finInstShare.setFormat(PennantConstants.rateFormate2);
	  	this.finInstShare.setScale(2);
	  	this.others.setMaxlength(5);
	  	this.others.setFormat(PennantConstants.rateFormate2);
	  	this.others.setScale(2);		

		if (isWorkFlowEnabled()) {
			this.gb_Action.setVisible(true);
			this.statusRow.setVisible(true);
		} else {
			this.gb_Action.setVisible(false);
			this.gb_statusDetails.setVisible(false);
			this.statusRow.setVisible(false);
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
		getUserWorkspace().alocateAuthorities("CustomerDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_CustomerDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_CustomerDialog_btnEdit"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_CustomerDialog_btnSave"));
		this.btnCancel.setVisible(false);

		// Customer related List Buttons
		this.btnNew_CustomerRatings.setVisible(getUserWorkspace().isAllowed(
				"button_CustomerDialog_btnNew_CustomerRatings"));
		this.btnNew_CustomerAddress.setVisible(getUserWorkspace().isAllowed(
				"button_CustomerDialog_btnNew_CustomerAddress"));
		this.btnNew_CustomerIncome.setVisible(getUserWorkspace().isAllowed(
				"button_CustomerDialog_btnNew_CustomerIncome"));
		this.btnNew_CustomerPRelation.setVisible(getUserWorkspace().isAllowed(
				"button_CustomerDialog_btnNew_CustomerPRelation"));
		this.btnNew_CustomerDocuments.setVisible(getUserWorkspace().isAllowed(
				"button_CustomerDialog_btnNew_CustomerDocuments"));
		this.btnNew_CustomerPhoneNumbers.setVisible(getUserWorkspace().isAllowed(
				"button_CustomerDialog_btnNew_CustomerPhoneNumbers"));
		this.btnNew_CustomerEmailAddress.setVisible(getUserWorkspace().isAllowed(
				"button_CustomerDialog_btnNew_CustomerEmailAddress"));
		this.btnNew_CustomerDirector.setVisible(getUserWorkspace().isAllowed(
				"button_CustomerDialog_btnNew_CustomerDirectorDetails"));
		this.btnNew_CustomerBalanceSheet.setVisible(getUserWorkspace().isAllowed(
				"button_CustomerDialog_btnNew_CustomerBalanceSheet"));

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
	public void onClick$btnClose(Event event) throws InterruptedException,ParseException, CustomerNotFoundException {
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
			final String msg = Labels
					.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

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
			closeDialog(this.window_CustomerDialog, "Customer");
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

		//Basic Details Tab-->1.Key Details
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(StringUtils.trimToEmpty(aCustomer.getCustCIF()));
		this.customerCIF.setValue(StringUtils.trimToEmpty(aCustomer.getCustCIF()) +"-"+ StringUtils.trimToEmpty(aCustomer.getCustShrtName()));
		this.custCoreBank.setValue(aCustomer.getCustCoreBank());
		this.custTypeCode.setValue(aCustomer.getCustTypeCode());
		this.custDftBranch.setValue(aCustomer.getCustDftBranch());
		this.custGroupID.setValue(aCustomer.getCustGroupID());
		this.custBaseCcy.setValue(aCustomer.getCustBaseCcy());
		this.custLng.setValue(aCustomer.getCustLng());
		
		//Basic Details Tab-->2.Personal Details(Retail Customer)
		if("I".equalsIgnoreCase(aCustomer.getLovDescCustCtgType())){
			this.custGenderCode.setValue(aCustomer.getCustGenderCode());
			this.custSalutationCode.setValue(StringUtils.trimToNull(aCustomer.getCustSalutationCode()));
			this.custFName.setValue(aCustomer.getCustFName());
			this.custMName.setValue(aCustomer.getCustMName());
			this.custLName.setValue(aCustomer.getCustLName());
			this.custShrtName.setValue(aCustomer.getCustShrtName());
			this.custFNameLclLng.setValue(aCustomer.getCustFNameLclLng());
			this.custMNameLclLng.setValue(aCustomer.getCustMNameLclLng());
			this.custLNameLclLng.setValue(aCustomer.getCustLNameLclLng());
			this.custShrtNameLclLng.setValue(aCustomer.getCustShrtNameLclLng());
			this.custDOB.setValue(aCustomer.getCustDOB());
			this.custPOB.setValue(aCustomer.getCustPOB());
			this.custCOB.setValue(aCustomer.getCustCOB());
			this.custMotherMaiden.setValue(aCustomer.getCustMotherMaiden());
			this.custProfession.setValue(aCustomer.getCustProfession());
			this.custMaritalSts.setValue(aCustomer.getCustMaritalSts());
		}
		//Basic Details Tab-->3.Organization Details(Corporate Customer)
		if("C".equalsIgnoreCase(aCustomer.getLovDescCustCtgType())){
			this.corpCustOrgName.setValue(aCustomer.getCustLName());
			this.corpCustShrtName.setValue(aCustomer.getCustShrtName());
			this.corpCustPOB.setValue(aCustomer.getCustPOB());
			this.corpCustCOB.setValue(aCustomer.getCustCOB());
			this.corpCustOrgNameLclLng.setValue(aCustomer.getCustLNameLclLng());
			this.corpCustShrtNameLclLng.setValue(aCustomer.getCustShrtNameLclLng());
		}
		
		//Generic Information Tab-->1.General Details
		this.custSts.setValue(aCustomer.getCustSts());
		this.custStsChgDate.setValue(aCustomer.getCustStsChgDate());
		this.custGroupSts.setValue(aCustomer.getCustGroupSts());
		this.custIsBlocked.setChecked(aCustomer.isCustIsBlocked());
		this.custIsActive.setChecked(aCustomer.isCustIsActive());
		this.custIsClosed.setChecked(aCustomer.isCustIsClosed());
		this.custClosedOn.setValue(aCustomer.getCustClosedOn());
		this.custInactiveReason.setValue(aCustomer.getCustInactiveReason());
		this.custIsDecease.setChecked(aCustomer.isCustIsDecease());
		this.custIsDormant.setChecked(aCustomer.isCustIsDormant());
		this.custIsDelinquent.setChecked(aCustomer.isCustIsDelinquent());
		this.custTradeLicenceNum.setValue(aCustomer.getCustTradeLicenceNum());
		this.custDateOfIncorporation.setValue(aCustomer.getCustDOB());
		this.custTradeLicenceExpiry.setValue(aCustomer.getCustTradeLicenceExpiry());
		this.custPassportNo.setValue(aCustomer.getCustPassportNo());
		this.custPassportExpiry.setValue(aCustomer.getCustPassportExpiry());
		this.custVisaNum.setValue(aCustomer.getCustVisaNum());
		this.custVisaExpiry.setValue(aCustomer.getCustVisaExpiry());
		this.custIsStaff.setChecked(aCustomer.isCustIsStaff());
		this.custStaffID.setValue(aCustomer.getCustStaffID());
		this.custIsMinor.setChecked(aCustomer.isCustIsMinor());
		this.custIsBlackListed.setChecked(aCustomer.isCustIsBlackListed());
		this.custBLRsnCode.setValue(StringUtils.trimToNull(aCustomer.getCustBLRsnCode()));
		this.custIsRejected.setChecked(aCustomer.isCustIsRejected());
		this.custRejectedRsn.setValue(StringUtils.trimToNull(aCustomer.getCustRejectedRsn()));
		this.custFirstBusinessDate.setValue(aCustomer.getCustFirstBusinessDate());
		this.custEmpSts.setValue(aCustomer.getCustEmpSts());
		
		//Demographic Details Tab-->1.Segmentation Details
		this.custCtgCode.setValue(aCustomer.getCustCtgCode());
		this.custIndustry.setValue(aCustomer.getCustIndustry());
		this.custSector.setValue(aCustomer.getCustSector());
		this.custSubSector.setValue(aCustomer.getCustSubSector());
		this.custSegment.setValue(aCustomer.getCustSegment());
		this.custSubSegment.setValue(aCustomer.getCustSubSegment());
		
		//KYC Details Tab --> 1.Income Details
		this.custTotalIncome.setReadonly(true);
		this.custTotalIncome.setValue(PennantAppUtil.formateAmount(
				aCustomer.getCustTotalIncome(), ccyFormatter));
		
		//KYC Details Tab-->2.Identity Details
		this.custParentCountry.setValue(aCustomer.getCustParentCountry());
		this.custResdCountry.setValue(aCustomer.getCustResdCountry());
		this.custRiskCountry.setValue(aCustomer.getCustRiskCountry());
		this.custNationality.setValue(aCustomer.getCustNationality());
		
		//Preferential Details Tab-->1.Non-Financial RelationShip Details
		this.custReferedBy.setValue(aCustomer.getCustReferedBy());
		this.custDSA.setValue(aCustomer.getCustDSA());
		this.custDSADept.setValue(aCustomer.getCustDSADept());
		this.custRO1.setValue(aCustomer.getCustRO1());
		this.custRO2.setValue(StringUtils.trimToNull(aCustomer.getCustRO2()));
		
		//Preferential Details Tab-->1.Statement Details
		this.custStmtFrq.setValue(aCustomer.getCustStmtFrq());
		if("ENQ".equals(this.moduleType)) {
			fillFrqCode(this.custStmtFrqCode, aCustomer.getCustStmtFrq(), true);
			fillFrqMth(this.custStmtFrqMth, aCustomer.getCustStmtFrq(), true);
			fillFrqDay(this.custStmtFrqDays, aCustomer.getCustStmtFrq(), true);
		} else {
			fillFrqCode(this.custStmtFrqCode, aCustomer.getCustStmtFrq(), isReadOnly("CustomerDialog_custStmtFrq"));
			fillFrqMth(this.custStmtFrqMth, aCustomer.getCustStmtFrq(), isReadOnly("CustomerDialog_custStmtFrq"));
			fillFrqDay(this.custStmtFrqDays, aCustomer.getCustStmtFrq(), isReadOnly("CustomerDialog_custStmtFrq"));
		}
		this.custIsStmtCombined.setChecked(aCustomer.isCustIsStmtCombined());
		this.custStmtLastDate.setValue(aCustomer.getCustStmtLastDate());
		this.custStmtNextDate.setValue(aCustomer.getCustStmtNextDate());
		this.custStmtDispatchMode.setValue(aCustomer.getCustStmtDispatchMode());
		
		//Customer Employment Details
		customerEmploymentDetail = aCustomerDetails.getCustomerEmploymentDetail();
		if (aCustomerDetails.isNewRecord()) {
			customerEmploymentDetail = new CustomerEmploymentDetail();
			customerEmploymentDetail.setNewRecord(aCustomerDetails.isNewRecord());
			customerEmploymentDetail.setCustID(aCustomerDetails.getCustID());
		}

		if (customerEmploymentDetail != null) {
			this.custEmpName.setValue(customerEmploymentDetail.getCustEmpName());
			this.custEmpFrom.setValue(customerEmploymentDetail.getCustEmpFrom());
			this.custEmpDept.setValue(customerEmploymentDetail.getCustEmpDept());
			this.custEmpDesg.setValue(customerEmploymentDetail.getCustEmpDesg());
			this.custEmpID.setValue(customerEmploymentDetail.getCustEmpID());
			this.custEmpType.setValue(customerEmploymentDetail.getCustEmpType());
			this.custEmpHNbr.setValue(customerEmploymentDetail.getCustEmpHNbr());
			this.custEmpFlatNbr.setValue(customerEmploymentDetail.getCustEMpFlatNbr());
			this.custEmpAddrStreet.setValue(customerEmploymentDetail.getCustEmpAddrStreet());
			this.custEMpAddrLine1.setValue(customerEmploymentDetail.getCustEMpAddrLine1());
			this.custEMpAddrLine2.setValue(customerEmploymentDetail.getCustEMpAddrLine2());
			this.custEmpPOBox.setValue(customerEmploymentDetail.getCustEmpPOBox());
			this.custEmpAddrPhone.setValue(customerEmploymentDetail.getCustEmpAddrPhone());
			this.custEmpAddrCountry.setValue(customerEmploymentDetail.getCustEmpAddrCountry());
			this.custEmpAddrProvince.setValue(customerEmploymentDetail.getCustEmpAddrProvince());
			this.custEmpAddrCity.setValue(customerEmploymentDetail.getCustEmpAddrCity());
			this.custEmpAddrZIP.setValue(customerEmploymentDetail.getCustEmpAddrZIP());
		}
		
		//Non-Financial and Financial details for Corporate Customer Details
		CorporateCustomerDetail aCorporateCustomerDetail = null;
		if("C".equalsIgnoreCase(aCustomer.getLovDescCustCtgType())){
			aCorporateCustomerDetail = aCustomerDetails.getCorporateCustomerDetail();
			if(aCorporateCustomerDetail !=null){
				this.name.setValue(aCorporateCustomerDetail.getName());
				this.phoneNumber.setValue(aCorporateCustomerDetail.getPhoneNumber());
				this.phoneNumber1.setValue(aCorporateCustomerDetail.getPhoneNumber1());
				this.emailId.setValue(aCorporateCustomerDetail.getEmailId());
				this.bussCommenceDate.setValue(aCorporateCustomerDetail.getBussCommenceDate());
				this.servCommenceDate.setValue(aCorporateCustomerDetail.getServCommenceDate());
				this.bankRelationshipDate.setValue(aCorporateCustomerDetail.getBankRelationshipDate());
				this.paidUpCapital.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getPaidUpCapital(),0));
				this.authorizedCapital.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getAuthorizedCapital(),0));
				this.reservesAndSurPlus.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getReservesAndSurPlus(),0));
				this.intangibleAssets.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getIntangibleAssets(),0));
				this.tangibleNetWorth.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getTangibleNetWorth(),0));
				this.longTermLiabilities.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getLongTermLiabilities(),0));
				this.capitalEmployed.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getCapitalEmployed(),0));
				this.investments.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getInvestments(),0));
				this.nonCurrentAssets.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getNonCurrentAssets(),0));
				this.netWorkingCapital.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getNetWorkingCapital(),0));
				this.netSales.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getNetSales(),0));
				this.otherIncome.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getOtherIncome(),0));
				this.netProfitAfterTax.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getNetProfitAfterTax(),0));
				this.depreciation.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getDepreciation(),0));
				this.cashAccurals.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getCashAccurals(),0));
				this.annualTurnover.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getAnnualTurnover(),0));
				this.returnOnCapitalEmp.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getReturnOnCapitalEmp(),0));
				this.currentAssets.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getCurrentAssets(),0));
				this.currentLiabilities.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getCurrentLiabilities(),0));
				this.currentBookValue.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getCurrentBookValue(),0));
				this.currentMarketValue.setValue(PennantAppUtil.formateAmount(
						aCorporateCustomerDetail.getCurrentMarketValue(),0));
				this.promotersShare.setValue(aCorporateCustomerDetail.getPromotersShare());
				this.associatesShare.setValue(aCorporateCustomerDetail.getAssociatesShare());
				this.publicShare.setValue(aCorporateCustomerDetail.getPublicShare());
				this.finInstShare.setValue(aCorporateCustomerDetail.getFinInstShare());
				this.others.setValue(aCorporateCustomerDetail.getOthers());
			}
		}
		
		//Customer Related List Rendering 
		doFillCustomerRatings(aCustomerDetails.getRatingsList());
		doFillCustomerAddress(aCustomerDetails.getAddressList());
		doFillCustomerEmail(aCustomerDetails.getCustomerEMailList());
		doFillCustomerPhoneNumbers(aCustomerDetails.getCustomerPhoneNumList());
		doFillCustomerDocuments(aCustomerDetails.getCustomerDocumentsList());
		if("C".equalsIgnoreCase(aCustomer.getLovDescCustCtgType())){
			doFillCustomerDirectors(aCustomerDetails.getDirectorsList());
			doFillCustomerBalanceSheet(aCustomerDetails.getBalanceSheetList());
		}else{
			doFillCustomerPRelations(aCustomerDetails.getCustomerPRelationList());
			doFillCustomerIncome(aCustomerDetails.getCustomerIncomeList());
		}

		//Basic Details Tab ----->1.Key Details: LOV Fields
		this.lovDescCustTypeCodeName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustTypeCodeName()).equals("") ? "":
			aCustomer.getCustTypeCode()+"-"+aCustomer.getLovDescCustTypeCodeName());
		
		this.lovDescCustDftBranchName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustDftBranchName()).equals("") ? "":
			aCustomer.getCustDftBranch()+"-"+aCustomer.getLovDescCustDftBranchName());
		
		this.lovDesccustGroupIDName.setValue(aCustomer.getCustGroupID() == 0 ? "" : aCustomer
				.getCustGroupID()+ "-"+ aCustomer.getLovDesccustGroupIDName());
		
		this.lovDescCustBaseCcyName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustBaseCcyName()).equals("") ? "":
			aCustomer.getCustBaseCcy()+ "-"+aCustomer.getLovDescCustBaseCcyName());
		
		this.lovDescCustLngName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustLngName()).equals("") ? "":
			 aCustomer.getCustLng()+ "-"+ aCustomer.getLovDescCustLngName());
			
		//Basic Details Tab-->2.Personal Details(Retail Customer)
		this.lovDescCustGenderCodeName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustGenderCodeName()).equals("") ? "":
			aCustomer.getCustGenderCode()+"-"+aCustomer.getLovDescCustGenderCodeName());
		
		this.lovDescCustSalutationCodeName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustSalutationCodeName()).equals("") ? "":
			aCustomer.getCustSalutationCode()+"-"+aCustomer.getLovDescCustSalutationCodeName());
		
		this.lovDescCustCOBName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustCOBName()).equals("") ? "":
			aCustomer.getCustCOB()+ "-"+aCustomer.getLovDescCustCOBName());
		
		this.lovDescCustProfessionName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustProfessionName()).equals("") ? "":
			aCustomer.getCustProfession()+ "-"+ aCustomer.getLovDescCustProfessionName());
		
		this.lovDescCustMaritalStsName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustMaritalStsName()).equals("") ? "":
			aCustomer.getCustMaritalSts()+ "-"+ aCustomer.getLovDescCustMaritalStsName());
		
		//Basic Details Tab-->3.Organization Details(Corporate Customer)
		this.lovDescCorpCustCOBName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustCOBName()).equals("") ? "":
			aCustomer.getCustCOB()+ "-"+aCustomer.getLovDescCustCOBName());
		
		//Generic Information Tab-->1.General Details
		
		this.lovDescCustStsName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustStsName()).equals("") ? "":
			aCustomer.getCustSts()+ "-"+ aCustomer.getLovDescCustStsName());
		
		this.lovDescCustGroupStsName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustGroupStsName()).equals("") ? "":
			aCustomer.getCustGroupSts()+ "-"+ aCustomer.getLovDescCustGroupStsName());
		
		this.lovDescCustEmpStsName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustEmpStsName()).equals("") ? "":
			aCustomer.getCustEmpSts()+ "-"+ aCustomer.getLovDescCustEmpStsName());
		
		this.lovDescCustBLRsnCodeName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustBLRsnCodeName()).equals("") ? "" : 
			aCustomer.getCustBLRsnCode() + "-"+ aCustomer.getLovDescCustBLRsnCodeName());
		
		this.lovDescCustRejectedRsnName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustRejectedRsnName()).equals("") ? "" : 
			aCustomer.getCustRejectedRsn()+ "-"+ aCustomer.getLovDescCustRejectedRsnName());
		
		//Demographic Details Tab-->1.Segmentation Details
		
		this.lovDescCustCtgCodeName.setValue(StringUtils.trimToEmpty(aCustomer.getCustCtgCode()).equals("")? "": 
			aCustomer.getCustCtgCode()+"-"+aCustomer.getLovDescCustCtgCodeName());
		
//		this.lovDescCustIndustryName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustIndustryName()).equals("") ? "" : 
//			aCustomer.getCustIndustry()+ "-"+ aCustomer.getLovDescCustIndustryName());
//		
//		this.lovDescCustSectorName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustSectorName()).equals("") ? "" : 
//			aCustomer.getCustSector()+ "-"+ aCustomer.getLovDescCustSectorName());
//		
//		this.lovDescCustSubSectorName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustSubSectorName()).equals("") ? "" : 
//			aCustomer.getCustSubSector() + "-"+ aCustomer.getLovDescCustSubSectorName());
//	
//		this.lovDescCustSegmentName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustSegmentName()).equals("")  ? "" : 
//			aCustomer.getCustSegment()+ "-"+ aCustomer.getLovDescCustSegmentName());
//	
//		this.lovDescCustSubSegmentName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustSubSegmentName()).equals("")  ? "" :
//			aCustomer.getCustSubSegment()+ "-"+ aCustomer.getLovDescCustSubSegmentName());
		
		//KYC Details Tab-->2.Identity Details
	
		this.lovDescCustResdCountryName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustResdCountryName()).equals("")  ? "" : 
			aCustomer.getCustResdCountry()+ "-"+aCustomer.getLovDescCustResdCountryName());
		
		this.lovDescCustNationalityName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustNationalityName()).equals("")  ? "" : 
			aCustomer.getCustNationality()+ "-"+ aCustomer.getLovDescCustNationalityName());
		
		//Preferential Details Tab-->1.Non-Financial RelationShip Details
		
		this.lovDescCustDSADeptName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustDSADeptName()).equals("") ? "" : 
			aCustomer.getCustDSADept()+ "-"+ aCustomer.getLovDescCustDSADeptName());
		
		this.lovDescCustRO1Name.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustRO1Name()).equals("")  ? "" : 
			aCustomer.getCustRO1()+ "-"+ aCustomer.getLovDescCustRO1Name());
		
		this.lovDescCustRO2Name.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescCustRO2Name()).equals("")  ? "" : 
			aCustomer.getCustRO2()+ "-"+ aCustomer.getLovDescCustRO2Name());
		
		//Preferential Details Tab-->2.Statement Details
		
		this.lovDescDispatchModeDescName.setValue(StringUtils.trimToEmpty(aCustomer.getLovDescDispatchModeDescName()).equals("") ? "" : 
			aCustomer.getCustStmtDispatchMode()+ "-"+ aCustomer.getLovDescDispatchModeDescName());

		//Customer Employment Details Tab
		if (customerEmploymentDetail == null) {

			this.lovDescCustEmpDesgName.setValue("");
			this.lovDescCustEmpDeptName.setValue("");
			this.lovDescCustEmpTypeName.setValue("");
			this.lovDescCustEmpAddrCountryName.setValue("");
			this.lovDescCustEmpAddrProvinceName.setValue("");
			this.lovDescCustEmpAddrCityName.setValue("");

		} else if (aCustomer.getCustEmpSts() != null) {

			if (aCustomer.getCustEmpSts().equals("EMPLOY")) {

				this.lovDescCustEmpDesgName.setValue(StringUtils.trimToEmpty(customerEmploymentDetail.getLovDescCustEmpDesgName()).equals("") ? "" : 
					customerEmploymentDetail.getCustEmpDesg()+ "-"+ customerEmploymentDetail.getLovDescCustEmpDesgName());

				this.lovDescCustEmpDeptName.setValue(StringUtils.trimToEmpty(customerEmploymentDetail.getLovDescCustEmpDeptName()).equals("") ? "": 
					customerEmploymentDetail.getCustEmpDept()+ "-"+ customerEmploymentDetail.getLovDescCustEmpDeptName());

				this.lovDescCustEmpTypeName.setValue(StringUtils.trimToEmpty(customerEmploymentDetail.getLovDescCustEmpTypeName()).equals("") ? "" : 
					customerEmploymentDetail.getCustEmpType()+ "-"+ customerEmploymentDetail.getLovDescCustEmpTypeName());

				this.lovDescCustEmpAddrCountryName.setValue(StringUtils.trimToEmpty(customerEmploymentDetail.getLovDescCustEmpAddrCountryName()).equals("") ? "" : 
					customerEmploymentDetail.getCustEmpAddrCountry()+ "-"+ customerEmploymentDetail.getLovDescCustEmpAddrCountryName());

				this.lovDescCustEmpAddrProvinceName.setValue(StringUtils.trimToEmpty(customerEmploymentDetail.getLovDescCustEmpAddrProvinceName()).equals("") ? "" : 
					customerEmploymentDetail.getCustEmpAddrProvince()+ "-" + customerEmploymentDetail.getLovDescCustEmpAddrProvinceName());

				this.lovDescCustEmpAddrCityName.setValue(StringUtils.trimToEmpty(customerEmploymentDetail.getLovDescCustEmpAddrCityName()).equals("") ? "" : 
					customerEmploymentDetail.getCustEmpAddrCity()+ "-" + customerEmploymentDetail.getLovDescCustEmpAddrCityName());

			}
		}
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
		
		Customer aCustomer = aCustomerDetails.getCustomer();
		CustomerEmploymentDetail aCustomerEmploymentDetail = aCustomerDetails.getCustomerEmploymentDetail();
		CorporateCustomerDetail aCorporateCustomerDetail = aCustomerDetails.getCorporateCustomerDetail();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// START TAB BASIC DETAILS
		//Basic Details Tab-->1.Key Details
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
			aCustomer.setLovDescCustTypeCodeName(this.lovDescCustTypeCodeName.getValue());
			aCustomer.setCustTypeCode(this.custTypeCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustDftBranchName(this.lovDescCustDftBranchName.getValue());
			aCustomer.setCustDftBranch(this.custDftBranch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDesccustGroupIDName(this.lovDesccustGroupIDName.getValue());
			aCustomer.setCustGroupID(this.custGroupID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustBaseCcyName(this.lovDescCustBaseCcyName.getValue());
			aCustomer.setCustBaseCcy(this.custBaseCcy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustLngName(this.lovDescCustLngName.getValue());
			aCustomer.setCustLng(this.custLng.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.gb_personalDetails.isVisible()) {
			//Basic Details Tab-->2.Personal Details(Retail Customer)
			try {
				aCustomer.setLovDescCustGenderCodeName(this.lovDescCustGenderCodeName.getValue());
				aCustomer.setCustGenderCode(this.custGenderCode.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aCustomer.setLovDescCustSalutationCodeName(this.lovDescCustSalutationCodeName.getValue());
				aCustomer.setCustSalutationCode(StringUtils.trimToNull(this.custSalutationCode.getValue()));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aCustomer.setCustFName(this.custFName.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aCustomer.setCustMName(this.custMName.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aCustomer.setCustLName(this.custLName.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aCustomer.setCustShrtName(this.custShrtName.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			if (this.row_localLngFM.isVisible() && !isRecordSaved) {
				try {
					aCustomer.setCustFNameLclLng(this.custFNameLclLng.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomer.setCustMNameLclLng(this.custMNameLclLng.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomer.setCustLNameLclLng(this.custLNameLclLng.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomer.setCustShrtNameLclLng(this.custShrtNameLclLng.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
			} 
			try {
				aCustomer.setCustPOB(this.custPOB.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aCustomer.setLovDescCustCOBName(this.lovDescCustCOBName.getValue());
				aCustomer.setCustCOB(this.custCOB.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.custDOB.getValue() != null) {
					if (!this.custDOB.getValue().after((Date) SystemParameterDetails
									.getSystemParameterValue("APP_DFT_START_DATE"))) {
						throw new WrongValueException(this.custDOB,Labels.getLabel("DATE_ALLOWED_AFTER",
							new String[] {Labels.getLabel("label_CustomerDialog_CustDOB.value"),SystemParameterDetails
								.getSystemParameterValue("APP_DFT_START_DATE").toString() }));
					}
					aCustomer.setCustDOB(new Timestamp(this.custDOB.getValue().getTime()));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aCustomer.setCustMotherMaiden(this.custMotherMaiden.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				dobCheck();
				aCustomer.setCustIsMinor(this.custIsMinor.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aCustomer.setLovDescCustMaritalStsName(this.lovDescCustMaritalStsName.getValue());
				aCustomer.setCustMaritalSts(StringUtils.trimToNull(this.custMaritalSts.getValue()));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aCustomer.setLovDescCustProfessionName(this.lovDescCustProfessionName.getValue());
				aCustomer.setCustProfession(StringUtils.trimToNull(this.custProfession.getValue()));
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else if (this.gb_corporateCustomerPersonalDetails.isVisible()) {
			
			//Basic Details Tab-->3.Organization Details(Corporate Customer)
			aCustomer.setCustGenderCode(null);
			aCustomer.setCustSalutationCode(null);
			try {
				aCustomer.setCustLName(this.corpCustOrgName.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aCustomer.setCustShrtName(this.corpCustShrtName.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			if (this.row_localLngCorpCustCS.isVisible() && !isRecordSaved) {
				try {
					aCustomer.setCustLNameLclLng(this.corpCustOrgNameLclLng.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomer.setCustShrtNameLclLng(this.corpCustShrtNameLclLng.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
			}
			try {
				aCustomer.setCustPOB(this.corpCustPOB.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aCustomer.setLovDescCustCOBName(this.lovDescCorpCustCOBName.getValue());
				aCustomer.setCustCOB(this.corpCustCOB.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		showErrorDetails(wve, basicDetails);
		this.custSts.setFocus(true);

		// END TAB BASIC DETAILS

		// START TAB GENERIC INFORMATION
		//Generic Information Tab-->1.General Details
		try {
			aCustomer.setLovDescCustStsName(this.lovDescCustStsName.getValue());
			aCustomer.setCustSts(StringUtils.trimToNull(this.custSts.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custStsChgDate.getValue() != null) {
				aCustomer.setCustStsChgDate(new Timestamp(this.custStsChgDate.getValue().getTime()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustGroupStsName(this.lovDescCustGroupStsName.getValue());
			aCustomer.setCustGroupSts(StringUtils.trimToNull(this.custGroupSts.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustIsBlocked(this.custIsBlocked.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustIsActive(this.custIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustInactiveReason(this.custInactiveReason.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustIsClosed(this.custIsClosed.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustClosedOn(this.custClosedOn.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustIsDecease(this.custIsDecease.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustIsDormant(this.custIsDormant.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustIsDelinquent(this.custIsDelinquent.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//For Corporate Customer Details
		if(this.row_corpTL.isVisible() && this.row_corpTLED.isVisible()){
			try {
				aCustomer.setCustTradeLicenceNum(this.custTradeLicenceNum.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.custDateOfIncorporation.getValue() != null) {
					if (!this.custDateOfIncorporation.getValue().after((Date) SystemParameterDetails
							.getSystemParameterValue("APP_DFT_START_DATE"))) {
						throw new WrongValueException(this.custDateOfIncorporation,Labels.getLabel("DATE_ALLOWED_AFTER",
								new String[] {Labels.getLabel("label_CustomerDialog_CustDateOfIncorporation.value"),SystemParameterDetails
								.getSystemParameterValue("APP_DFT_START_DATE").toString() }));
					}
				}
				aCustomer.setCustDOB(new Timestamp(this.custDateOfIncorporation.getValue().getTime()));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.custTradeLicenceExpiry.getValue() != null) {
					if (!this.custTradeLicenceExpiry.getValue().before((Date) SystemParameterDetails
							.getSystemParameterValue("APP_DFT_END_DATE"))) {
						throw new WrongValueException(this.custTradeLicenceExpiry, Labels.getLabel(
								"DATE_ALLOWED_BEFORE",new String[] {Labels.getLabel("label_CustomerDialog_CustTradeLicenceExpiry.value"),
										SystemParameterDetails.getSystemParameterValue("APP_DFT_END_DATE").toString() }));
					}
				}
				aCustomer.setCustTradeLicenceExpiry(this.custTradeLicenceExpiry.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		
		//For Retail Customer Details
		if(this.row_retailPPT.isVisible() && this.row_custStaff.isVisible() && this.row_EmpSts.isVisible()){
			try {
				aCustomer.setCustPassportNo(this.custPassportNo.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.custPassportExpiry.getValue() != null) {
					if (!this.custPassportExpiry.getValue().before(((Date) SystemParameterDetails
									.getSystemParameterValue("APP_DFT_END_DATE")))) {
						throw new WrongValueException(this.custPassportExpiry,Labels.getLabel("DATE_ALLOWED_BEFORE",
							new String[] {Labels.getLabel("label_CustomerDialog_CustPassportExpiry.value"),SystemParameterDetails
									.getSystemParameterValue("APP_DFT_END_DATE").toString() }));
					}
				}
				aCustomer.setCustPassportExpiry(this.custPassportExpiry.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			if(this.row_retailVisa.isVisible()){
				try {
					aCustomer.setCustVisaNum(this.custVisaNum.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					if (this.custVisaExpiry.getValue() != null) {
						if (!this.custVisaExpiry.getValue().before(((Date) SystemParameterDetails
								.getSystemParameterValue("APP_DFT_END_DATE")))) {
							throw new WrongValueException(this.custVisaExpiry,Labels.getLabel("DATE_ALLOWED_BEFORE",
									new String[] {Labels.getLabel("label_CustomerDialog_CustVisaExpiry.value"),SystemParameterDetails
									.getSystemParameterValue("APP_DFT_END_DATE").toString() }));
						}
					}
					aCustomer.setCustVisaExpiry(this.custVisaExpiry.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
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
				aCustomer.setLovDescCustEmpStsName(this.lovDescCustEmpStsName.getValue());
				aCustomer.setCustEmpSts(StringUtils.trimToEmpty(this.custEmpSts.getValue()));
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		try {
			aCustomer.setCustIsBlackListed(this.custIsBlackListed.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustBLRsnCode(StringUtils.trimToNull(this.custBLRsnCode.getValue()));
			aCustomer.setLovDescCustBLRsnCodeName(this.lovDescCustBLRsnCodeName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustIsRejected(this.custIsRejected.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustRejectedRsn(StringUtils.trimToNull(this.custRejectedRsn.getValue()));
			aCustomer.setLovDescCustRejectedRsnName(this.lovDescCustRejectedRsnName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custFirstBusinessDate.getValue() != null) {
				if (!this.custFirstBusinessDate.getValue().after(((Date) SystemParameterDetails
								.getSystemParameterValue("APP_DFT_START_DATE")))) {
					throw new WrongValueException(this.custFirstBusinessDate,Labels.getLabel("DATE_ALLOWED_AFTER",
						new String[] { Labels.getLabel("label_CustomerDialog_CustFirstBusinessDate.value"),SystemParameterDetails
							.getSystemParameterValue("APP_DFT_START_DATE").toString() }));
				}
			}
			aCustomer.setCustFirstBusinessDate(this.custFirstBusinessDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		showErrorDetails(wve, genericInformation);
		// END TAB GENERIC INFORMATION

		// START TAB DEMOGRAPHIC DETAILS
		
		//Demographic Details Tab-->1.Segmentation Details
		try {
			aCustomer.setLovDescCustCtgCodeName(this.lovDescCustCtgCodeName.getValue());
			aCustomer.setCustCtgCode(this.custCtgCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustSectorName(this.lovDescCustSectorName.getValue());
			aCustomer.setCustSector(this.custSector.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustSubSectorName(this.lovDescCustSubSectorName.getValue());
			aCustomer.setCustSubSector(this.custSubSector.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustIndustryName(this.lovDescCustIndustryName.getValue());
			aCustomer.setCustIndustry(this.custIndustry.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustSegmentName(this.lovDescCustSegmentName.getValue());
			aCustomer.setCustSegment(this.custSegment.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustSubSegmentName(this.lovDescCustSubSegmentName.getValue());
			aCustomer.setCustSubSegment(this.custSubSegment.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		showErrorDetails(wve, demographicDetails);

		// END TAB DEMOGRAPHIC DETAILS

		// START TAB KYC DETAILS

		//KYC Details Tab
		//KYC Details Tab-->1.Income Details
		if(gb_incomeDetails.isVisible()){
			try {
				if (this.custTotalIncome.getValue() != new BigDecimal(0)) {
					aCustomer.setCustTotalIncome(PennantAppUtil.unFormateAmount(
							this.custTotalIncome.getValue(),ccyFormatter));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		
		//KYC Details Tab-->2.Identity Details
		try {
			aCustomer.setCustParentCountry(this.custNationality.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustResdCountryName(this.lovDescCustResdCountryName.getValue());
			aCustomer.setCustResdCountry(this.custResdCountry.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustRiskCountry(this.custNationality.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustNationalityName(this.lovDescCustNationalityName.getValue());
			aCustomer.setCustNationality(this.custNationality.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		aCustomer.setRecordStatus(this.recordStatus.getValue());
		showErrorDetails(wve, kYCDetails);
		// END TAB KYC DETAILS

		// START TAB EMPLOYMENT DETAILS
		//Customer Empployment Details for only Retail Customer
		if(getCustomerDetails().getCustomer().getLovDescCustCtgType().equals("I")){
			
			if (aCustomerEmploymentDetail == null) {
				aCustomerEmploymentDetail = new CustomerEmploymentDetail();
				aCustomerEmploymentDetail.setNewRecord(true);
				aCustomerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			}
			aCustomerEmploymentDetail.setCustID(aCustomer.getCustID());
			
			//Entered Details only , if CustEmpSts='EMPLOY'
			if (this.gb_employerDetails.isVisible()) {
				
				try {
					aCustomerEmploymentDetail.setCustEmpName(this.custEmpName.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					if (this.custEmpFrom.getValue() != null) {
						if (!this.custEmpFrom.getValue().after(((Date) SystemParameterDetails
								.getSystemParameterValue("APP_DFT_START_DATE")))) {
							throw new WrongValueException(this.custEmpFrom,Labels.getLabel("DATE_ALLOWED_AFTER",
									new String[] {Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpFrom.value"),SystemParameterDetails
									.getSystemParameterValue("APP_DFT_START_DATE").toString() }));
						}
						aCustomerEmploymentDetail.setCustEmpFrom(new Timestamp(this.custEmpFrom.getValue().getTime()));
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomerEmploymentDetail.setLovDescCustEmpDesgName(this.lovDescCustEmpDesgName.getValue());
					aCustomerEmploymentDetail.setCustEmpDesg(this.custEmpDesg.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomerEmploymentDetail.setLovDescCustEmpDeptName(this.lovDescCustEmpDeptName.getValue());
					aCustomerEmploymentDetail.setCustEmpDept(this.custEmpDept.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomerEmploymentDetail.setCustEmpID(this.custEmpID.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomerEmploymentDetail.setLovDescCustEmpTypeName(this.lovDescCustEmpTypeName.getValue());
					aCustomerEmploymentDetail.setCustEmpType(this.custEmpType.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomerEmploymentDetail.setCustEmpHNbr(this.custEmpHNbr.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomerEmploymentDetail.setCustEMpFlatNbr(this.custEmpFlatNbr.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomerEmploymentDetail.setCustEmpAddrStreet(this.custEmpAddrStreet.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomerEmploymentDetail.setCustEMpAddrLine1(this.custEMpAddrLine1.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomerEmploymentDetail.setCustEMpAddrLine2(this.custEMpAddrLine2.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomerEmploymentDetail.setCustEmpPOBox(this.custEmpPOBox.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomerEmploymentDetail.setCustEmpAddrPhone(this.custEmpAddrPhone.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomerEmploymentDetail.setLovDescCustEmpAddrCountryName(this.lovDescCustEmpAddrCountryName.getValue());
					aCustomerEmploymentDetail.setCustEmpAddrCountry(this.custEmpAddrCountry.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomerEmploymentDetail.setLovDescCustEmpAddrProvinceName(this.lovDescCustEmpAddrProvinceName.getValue());
					aCustomerEmploymentDetail.setCustEmpAddrProvince(this.custEmpAddrProvince.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomerEmploymentDetail.setLovDescCustEmpAddrCityName(this.lovDescCustEmpAddrCityName.getValue());
					aCustomerEmploymentDetail.setCustEmpAddrCity(this.custEmpAddrCity.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aCustomerEmploymentDetail.setCustEmpAddrZIP(this.custEmpAddrZIP.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
			}
			aCustomerEmploymentDetail.setRecordStatus(this.recordStatus.getValue());
			showErrorDetails(wve, employmentDetails);
		}
		// END TAB EMPLOYMENT DETAILS

		// START TAB PREFERENTIAL DETAILS

		//Preferential Details Tab-->1.Non-Financial RelationShip Details
		try {
			aCustomer.setCustDSA(this.custDSA.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustDSADeptName(this.lovDescCustDSADeptName.getValue());
			aCustomer.setCustDSADept(this.custDSADept.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustRO1Name(this.lovDescCustRO1Name.getValue());
			aCustomer.setCustRO1(this.custRO1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.custRO2.getValue().equals("") && this.custRO1.getValue().equals(this.custRO2.getValue())) {
				throw new WrongValueException(this.lovDescCustRO2Name,Labels.getLabel("FIELD_NOT_SAME",
						new String[] { Labels.getLabel("label_CustomerDialog_CustRO2.value") }));
			} else {
				aCustomer.setLovDescCustRO2Name(this.lovDescCustRO2Name.getValue());
				aCustomer.setCustRO2(StringUtils.trimToNull(this.custRO2.getValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustReferedBy(this.custReferedBy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//Preferential Details Tab-->2.Statement Details
		this.custStmtFrqCode.setErrorMessage("");
		this.custStmtFrqMth.setErrorMessage("");
		this.custStmtFrqDays.setErrorMessage("");
		String frqCode = (String) this.custStmtFrqCode.getSelectedItem().getValue();
		String frqMth = (String) this.custStmtFrqMth.getSelectedItem().getValue();
		String frqDay = (String) this.custStmtFrqDays.getSelectedItem().getValue();

		boolean frqValid = true;

		if (!isRecordSaved) {

			try {
				if (frqCode == null || frqCode.equalsIgnoreCase("#")) {
					throw new WrongValueException( this.custStmtFrqCode,Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_CustomerDialog_CustStmtFrq.value") }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
				frqValid = false;
			}

			try {
				if (frqMth == null || frqMth.equalsIgnoreCase("#")) {
					throw new WrongValueException(this.custStmtFrqMth,Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_CustomerDialog_custStmtFrqMth.value") }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
				frqValid = false;
			}

			try {
				if (frqDay == null || frqDay.equalsIgnoreCase("#")) {
					throw new WrongValueException(this.custStmtFrqDays,Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_CustomerDialog_CustStmtFrqDay.value") }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
				frqValid = false;
			}
		}

		if (frqValid) {
			aCustomer.setCustStmtFrq(this.custStmtFrq.getValue());
		}

		try {
			if (this.custStmtNextDate.getValue() != null) {
				if (!isRecordSaved) {
					if (!this.custStmtNextDate.getValue().before(((Date) SystemParameterDetails
									.getSystemParameterValue("APP_DFT_END_DATE")))) {
						throw new WrongValueException(this.custStmtNextDate,Labels.getLabel("DATE_ALLOWED_BEFORE",
							new String[] {Labels.getLabel("label_CustomerDialog_CustStmtNextDate.value"),SystemParameterDetails
										.getSystemParameterValue("APP_DFT_END_DATE").toString() }));
					}
				}
				aCustomer.setCustStmtNextDate(new Timestamp(this.custStmtNextDate.getValue().getTime()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custStmtLastDate.getValue() != null) {
				aCustomer.setCustStmtLastDate(new Timestamp(this.custStmtLastDate.getValue().getTime()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustIsStmtCombined(this.custIsStmtCombined.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescDispatchModeDescName(this.lovDescDispatchModeDescName.getValue());
			aCustomer.setCustStmtDispatchMode(this.custStmtDispatchMode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		showErrorDetails(wve, preferentialDetails);
		// END TAB PREFERENTIAL DETAILS
		// START TAB NON-FINANCIAL AND FINANCIAL DETAILS
		
		if("C".equalsIgnoreCase(aCustomer.getLovDescCustCtgType())){
			if(aCorporateCustomerDetail == null){
				aCorporateCustomerDetail = new CorporateCustomerDetail();
				aCorporateCustomerDetail.setCustId(aCustomer.getCustID());
				aCorporateCustomerDetail.setNewRecord(true);
				aCorporateCustomerDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			}
			try {
			    aCorporateCustomerDetail.setName(this.name.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
			    aCorporateCustomerDetail.setPhoneNumber(this.phoneNumber.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
			    aCorporateCustomerDetail.setPhoneNumber1(this.phoneNumber1.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
			    aCorporateCustomerDetail.setEmailId(this.emailId.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
		 		aCorporateCustomerDetail.setBussCommenceDate(this.bussCommenceDate.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
		 		aCorporateCustomerDetail.setServCommenceDate(this.servCommenceDate.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
		 		if(this.bankRelationshipDate.getValue()!=null){
		 			aCorporateCustomerDetail.setBankRelationshipDate(this.bankRelationshipDate.getValue());
		 		}	
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			showErrorDetails(wve, nonFinancialDetails);
			try {
				 aCorporateCustomerDetail.setPaidUpCapital(PennantAppUtil.unFormateAmount(
				 			this.paidUpCapital.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				 aCorporateCustomerDetail.setAuthorizedCapital(PennantAppUtil.unFormateAmount(
				 			this.authorizedCapital.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				 aCorporateCustomerDetail.setReservesAndSurPlus(PennantAppUtil.unFormateAmount(
				 			this.reservesAndSurPlus.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				 aCorporateCustomerDetail.setIntangibleAssets(PennantAppUtil.unFormateAmount(
				 			this.intangibleAssets.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				 aCorporateCustomerDetail.setTangibleNetWorth(PennantAppUtil.unFormateAmount(
				 			this.tangibleNetWorth.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				 aCorporateCustomerDetail.setLongTermLiabilities(PennantAppUtil.unFormateAmount(
				 			this.longTermLiabilities.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				 aCorporateCustomerDetail.setCapitalEmployed(PennantAppUtil.unFormateAmount(
				 			this.capitalEmployed.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				 aCorporateCustomerDetail.setInvestments(PennantAppUtil.unFormateAmount(
				 			this.investments.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
			 	aCorporateCustomerDetail.setNonCurrentAssets(PennantAppUtil.unFormateAmount(
				 			this.nonCurrentAssets.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
			 	aCorporateCustomerDetail.setNetWorkingCapital(PennantAppUtil.unFormateAmount(
				 			this.netWorkingCapital.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				 aCorporateCustomerDetail.setNetSales(PennantAppUtil.unFormateAmount(
				 			this.netSales.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
			 	aCorporateCustomerDetail.setOtherIncome(PennantAppUtil.unFormateAmount(
				 			this.otherIncome.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
			 	aCorporateCustomerDetail.setNetProfitAfterTax(PennantAppUtil.unFormateAmount(
				 			this.netProfitAfterTax.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
			 	aCorporateCustomerDetail.setDepreciation(PennantAppUtil.unFormateAmount(
				 			this.depreciation.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
			 	aCorporateCustomerDetail.setCashAccurals(PennantAppUtil.unFormateAmount(
				 			this.cashAccurals.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
			 	aCorporateCustomerDetail.setAnnualTurnover(PennantAppUtil.unFormateAmount(
				 			this.annualTurnover.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
			 	aCorporateCustomerDetail.setReturnOnCapitalEmp(PennantAppUtil.unFormateAmount(
				 			this.returnOnCapitalEmp.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
			 	aCorporateCustomerDetail.setCurrentAssets(PennantAppUtil.unFormateAmount(
				 			this.currentAssets.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
			 	aCorporateCustomerDetail.setCurrentLiabilities(PennantAppUtil.unFormateAmount(
				 			this.currentLiabilities.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
			 	aCorporateCustomerDetail.setCurrentBookValue(PennantAppUtil.unFormateAmount(
				 			this.currentBookValue.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
			 	aCorporateCustomerDetail.setCurrentMarketValue(PennantAppUtil.unFormateAmount(
				 			this.currentMarketValue.getValue(), 0));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			BigDecimal totalSharePercent = new BigDecimal(0);
			boolean percentageCheck = false;
			totalSharePercent = new BigDecimal(this.promotersShare.getValue()==null?"0":this.promotersShare.getValue().toString());
			totalSharePercent = totalSharePercent.add(new BigDecimal(this.associatesShare.getValue()==null?"0":this.associatesShare.getValue().toString()));
			totalSharePercent = totalSharePercent.add(new BigDecimal(this.publicShare.getValue()==null?"0":this.publicShare.getValue().toString()));
			totalSharePercent = totalSharePercent.add(new BigDecimal(this.finInstShare.getValue()==null?"0":this.finInstShare.getValue().toString()));
			totalSharePercent = totalSharePercent.add(new BigDecimal(this.others.getValue()==null?"0":this.others.getValue().toString()));
			if(totalSharePercent.doubleValue() != new Double(100)){
				percentageCheck = true;
			}
			
			try {
				if(percentageCheck){
					throw new WrongValueException(promotersShare, 
							Labels.getLabel("label_CorporateCustomerDetailDialog_TotalShare.value"));
				}	
				aCorporateCustomerDetail.setPromotersShare(this.promotersShare.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				if(percentageCheck){
					throw new WrongValueException(associatesShare, 
							Labels.getLabel("label_CorporateCustomerDetailDialog_TotalShare.value"));
				}	
				aCorporateCustomerDetail.setAssociatesShare(this.associatesShare.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				if(percentageCheck){
					throw new WrongValueException(publicShare, 
							Labels.getLabel("label_CorporateCustomerDetailDialog_TotalShare.value"));
				}
				aCorporateCustomerDetail.setPublicShare(this.publicShare.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				if(percentageCheck){
					throw new WrongValueException(finInstShare, 
							Labels.getLabel("label_CorporateCustomerDetailDialog_TotalShare.value"));
				}
				aCorporateCustomerDetail.setFinInstShare(this.finInstShare.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				if(percentageCheck){
					throw new WrongValueException(others, 
							Labels.getLabel("label_CorporateCustomerDetailDialog_TotalShare.value"));
				}
				aCorporateCustomerDetail.setOthers(this.others.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			aCorporateCustomerDetail.setRecordStatus(this.recordStatus.getValue());
			showErrorDetails(wve, financialDetails);
		}
		// END TAB NON-FINANCIAL AND FINANCIAL DETAILS
		

		// END TAB ADDITIONAL DETAILS
		aCustomerDetails.setCustomer(aCustomer);
		aCustomerDetails.setCustomerEmploymentDetail(aCustomerEmploymentDetail);
		aCustomerDetails.setRatingsList(this.ratingsList);
		aCustomerDetails.setAddressList(this.addressList);
		aCustomerDetails.setCustomerEMailList(this.emailList);
		aCustomerDetails.setCustomerDocumentsList(this.documentsList);
		aCustomerDetails.setCustomerPhoneNumList(this.phoneNumberList);
		if("C".equalsIgnoreCase(aCustomer.getLovDescCustCtgType())){
			aCustomerDetails.setCorporateCustomerDetail(aCorporateCustomerDetail);
			aCustomerDetails.setDirectorsList(this.directorsList);
			aCustomerDetails.setBalanceSheetList(this.balanceSheetList);
		}else{
			aCustomerDetails.setCustomerIncomeList(this.incomeList);
			aCustomerDetails.setCustomerPRelationList(this.pRelationList);
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
	public void doShowDialog(CustomerDetails aCustomerDetails)
			throws InterruptedException {
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
			this.btnSearchCustSubSector.setVisible(false);
			this.btnSearchCustSubSegment.setVisible(false);
			this.btnSearchCustEmpAddrProvince.setVisible(false);
			this.btnSearchCustEmpAddrCity.setVisible(false);
			this.btnSearchCustSalutationCode.setVisible(false);
		} else {
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
				//TODO
				if(!"ENQ".equals(this.moduleType)){
					this.btnSearchCustSubSector.setVisible(true);
					this.btnSearchCustSubSegment.setVisible(true);
					this.btnSearchCustEmpAddrProvince.setVisible(true);
					this.btnSearchCustEmpAddrCity.setVisible(true);
					this.btnSearchCustSalutationCode.setVisible(true);
				}
			}
		}
		this.customerCIF.focus();
		try {
			// fill the components with the data
			doWriteBeanToComponents(aCustomerDetails);
			// checking the event calling validations
			doCheckValidations();
			// stores the inital data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CustomerDialog);
		} catch (final Exception e) {
			e.printStackTrace();
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");

		//Basic Details Tab-->1.Key Details
		this.oldVar_custID = this.custID.longValue();
		this.oldVar_custCIF = this.custCIF.getValue();
		this.oldVar_custCoreBank = this.custCoreBank.getValue();
		this.oldVar_custTypeCode = this.custTypeCode.getValue();
		this.oldVar_lovDescCustTypeCodeName = this.lovDescCustTypeCodeName.getValue();
		this.oldVar_custDftBranch = this.custDftBranch.getValue();
		this.oldVar_lovDescCustDftBranchName = this.lovDescCustDftBranchName.getValue();
		this.oldVar_custGroupID = this.custGroupID.longValue();
		this.oldVar_lovDesccustGroupIDName = this.lovDesccustGroupIDName.getValue();
		this.oldVar_custBaseCcy = this.custBaseCcy.getValue();
		this.oldVar_lovDescCustBaseCcyName = this.lovDescCustBaseCcyName.getValue();
		this.oldVar_custLng = this.custLng.getValue();
		this.oldVar_lovDescCustLngName = this.lovDescCustLngName.getValue();
		
		if (this.gb_personalDetails.isVisible()) {
			//Basic Details Tab-->2.Personal Details(Retail Customer)
			
			this.oldVar_custGenderCode = this.custGenderCode.getValue();
			this.oldVar_lovDescCustGenderCodeName = this.lovDescCustGenderCodeName.getValue();
			this.oldVar_custSalutationCode = this.custSalutationCode.getValue();
			this.oldVar_lovDescCustSalutationCodeName = this.lovDescCustSalutationCodeName.getValue();
			this.oldVar_custFName = this.custFName.getValue();
			this.oldVar_custMName = this.custMName.getValue();
			this.oldVar_custLName = this.custLName.getValue();
			this.oldVar_custShrtName = this.custShrtName.getValue();
			this.oldVar_custFNameLclLng = this.custFNameLclLng.getValue();
			this.oldVar_custMNameLclLng = this.custMNameLclLng.getValue();
			this.oldVar_custLNameLclLng = this.custLNameLclLng.getValue();
			this.oldVar_custShrtNameLclLng = this.custShrtNameLclLng.getValue();
			this.oldVar_custDOB = this.custDOB.getValue();
			this.oldVar_custPOB = this.custPOB.getValue();
			this.oldVar_custCOB = this.custCOB.getValue();
			this.oldVar_lovDescCustCOBName = this.lovDescCustCOBName.getValue();
			this.oldVar_custMotherMaiden = this.custMotherMaiden.getValue();
			this.oldVar_custIsMinor = this.custIsMinor.isChecked();
			this.oldVar_custProfession = this.custProfession.getValue();
			this.oldVar_lovDescCustProfessionName = this.lovDescCustProfessionName.getValue();		
			this.oldVar_custMaritalSts = this.custMaritalSts.getValue();
			this.oldVar_lovDescCustMaritalStsName = this.lovDescCustMaritalStsName.getValue();
			
		} else {
			
			//Basic Details Tab-->3.Organization Details(Corporate Customer)
			this.oldVar_custLName = this.corpCustOrgName.getValue();
			this.oldVar_custShrtName = this.corpCustShrtName.getValue();
			this.oldVar_custLNameLclLng = this.corpCustOrgNameLclLng.getValue();
			this.oldVar_custShrtNameLclLng = this.corpCustShrtNameLclLng.getValue();
			this.oldVar_custPOB = this.corpCustPOB.getValue();
			this.oldVar_custCOB = this.corpCustCOB.getValue();
			this.oldVar_lovDescCustCOBName = this.lovDescCorpCustCOBName.getValue();
			
		}
		
		//Generic Information Tab-->1.General Details
		this.oldVar_custSts = this.custSts.getValue();
		this.oldVar_lovDescCustStsName = this.lovDescCustStsName.getValue();
		this.oldVar_custStsChgDate = this.custStsChgDate.getValue();
		this.oldVar_custGroupSts = this.custGroupSts.getValue();
		this.oldVar_lovDescCustGroupStsName = this.lovDescCustGroupStsName.getValue();
		this.oldVar_custIsBlocked = this.custIsBlocked.isChecked();
		this.oldVar_custIsActive = this.custIsActive.isChecked();
		this.oldVar_custIsClosed = this.custIsClosed.isChecked();
		this.oldVar_custClosedOn = this.custClosedOn.getValue();
		this.oldVar_custInactiveReason = this.custInactiveReason.getValue();
		this.oldVar_custIsDecease = this.custIsDecease.isChecked();
		this.oldVar_custIsDormant = this.custIsDormant.isChecked();
		this.oldVar_custIsDelinquent = this.custIsDelinquent.isChecked();
		this.oldVar_custTradeLicenceNum = this.custTradeLicenceNum.getValue();
		this.oldVar_custDateOfIncorporation = this.custDateOfIncorporation.getValue();
		this.oldVar_custTradeLicenceExpiry = this.custTradeLicenceExpiry.getValue();
		this.oldVar_custPassportNo = this.custPassportNo.getValue();
		this.oldVar_custPassportExpiry = this.custPassportExpiry.getValue();
		this.oldVar_custVisaNum = this.custVisaNum.getValue();
		this.oldVar_custVisaExpiry = this.custVisaExpiry.getValue();
		this.oldVar_custIsStaff = this.custIsStaff.isChecked();
		this.oldVar_custStaffID = this.custStaffID.getValue();
		this.oldVar_custEmpSts = this.custEmpSts.getValue();
		this.oldVar_lovDescCustEmpStsName = this.lovDescCustEmpStsName.getValue();
		this.oldVar_custFirstBusinessDate = this.custFirstBusinessDate.getValue();
		this.oldVar_custIsBlackListed = this.custIsBlackListed.isChecked();
		this.oldVar_custBLRsnCode = this.custBLRsnCode.getValue();
		this.oldVar_custIsRejected = this.custIsRejected.isChecked();
		this.oldVar_custRejectedRsn = this.custRejectedRsn.getValue();
		this.oldVar_lovDescCustBLRsnCodeName = this.lovDescCustBLRsnCodeName.getValue();
		this.oldVar_lovDescCustRejectedRsnName = this.lovDescCustRejectedRsnName.getValue();
		
		//Demographic Details Tab-->1.Segmentation Details
		this.oldVar_custCtgCode = this.custCtgCode.getValue();
		this.oldVar_lovDescCustCtgCodeName = this.lovDescCustCtgCodeName.getValue();
		this.oldVar_custSector = this.custSector.getValue();
		this.oldVar_lovDescCustSectorName = this.lovDescCustSectorName.getValue();
		this.oldVar_custSubSector = this.custSubSector.getValue();
		this.oldVar_lovDescCustSubSectorName = this.lovDescCustSubSectorName.getValue();
		this.oldVar_custIndustry = this.custIndustry.getValue();
		this.oldVar_lovDescCustIndustryName = this.lovDescCustIndustryName.getValue();
		this.oldVar_custSegment = this.custSegment.getValue();
		this.oldVar_lovDescCustSegmentName = this.lovDescCustSegmentName.getValue();
		this.oldVar_custSubSegment = this.custSubSegment.getValue();
		this.oldVar_lovDescCustSubSegmentName = this.lovDescCustSubSegmentName.getValue();

		//KYC Details Tab-->1.Income Details
		this.oldVar_custTotalIncome = this.custTotalIncome.getValue();
		
		//KYC Details Tab-->2.Identity Details
		this.oldVar_custParentCountry = this.custParentCountry.getValue();
		this.oldVar_custResdCountry = this.custResdCountry.getValue();
		this.oldVar_lovDescCustResdCountryName = this.lovDescCustResdCountryName.getValue();
		this.oldVar_custRiskCountry = this.custRiskCountry.getValue();
		this.oldVar_custNationality = this.custNationality.getValue();
		this.oldVar_lovDescCustNationalityName = this.lovDescCustNationalityName.getValue();
		
		//Preferential Details Tab-->1.Non-Financial RelationShip Details
		this.oldVar_custDSA = this.custDSA.getValue();
		this.oldVar_custDSADept = this.custDSADept.getValue();
		this.oldVar_lovDescCustDSADeptName = this.lovDescCustDSADeptName.getValue();
		this.oldVar_custRO1 = this.custRO1.getValue();
		this.oldVar_lovDescCustRO1Name = this.lovDescCustRO1Name.getValue();
		this.oldVar_custRO2 = this.custRO2.getValue();
		this.oldVar_lovDescCustRO2Name = this.lovDescCustRO2Name.getValue();
		this.oldVar_custReferedBy = this.custReferedBy.getValue();

		//Preferential Details Tab-->1.Statement Details
		this.oldVar_custStmtFrq = this.custStmtFrq.getValue();
		this.oldVar_custIsStmtCombined = this.custIsStmtCombined.isChecked();
		this.oldVar_custStmtLastDate = this.custStmtLastDate.getValue();
		this.oldVar_custStmtNextDate = this.custStmtNextDate.getValue();
		this.oldVar_custStmtDispatchMode = this.custStmtDispatchMode.getValue();
		this.oldVar_lovDescDispatchModeDescName = this.lovDescDispatchModeDescName.getValue();
		
		//Customer Employment Details
		this.oldVar_custEmpName = this.custEmpName.getValue();
		this.oldVar_custEmpFrom = this.custEmpFrom.getValue();
		this.oldVar_custEmpDesg = this.custEmpDesg.getValue();
		this.oldVar_lovDescCustEmpDesgName = this.lovDescCustEmpDesgName.getValue();
		this.oldVar_custEmpDept = this.custEmpDept.getValue();
		this.oldVar_lovDescCustEmpDeptName = this.lovDescCustEmpDeptName.getValue();
		this.oldVar_custEmpID = this.custEmpID.getValue();
		this.oldVar_custEmptype = this.custEmpType.getValue();
		this.oldVar_lovDescCustEmpTypeName = this.lovDescCustEmpTypeName.getValue();
		this.oldVar_custEmpHNbr = this.custEmpHNbr.getValue();
		this.oldVar_custEmpFlatNbr = this.custEmpFlatNbr.getValue();
		this.oldVar_custEmpAddrStreet = this.custEmpAddrStreet.getValue();
		this.oldVar_custEmpAddrLine1 = this.custEMpAddrLine1.getValue();
		this.oldVar_custEmpAddrLine2 = this.custEMpAddrLine2.getValue();
		this.oldVar_custEmpPOBox = this.custEmpPOBox.getValue();
		this.oldVar_custEmpAddrPhone = this.custEmpAddrPhone.getValue();
		this.oldVar_custEmpAddrCountry = this.custEmpAddrCountry.getValue();
		this.oldVar_lovDescCustEmpAddrCountryName = this.lovDescCustEmpAddrCountryName.getValue();
		this.oldVar_custEmpAddrProvince = this.custEmpAddrProvince.getValue();
		this.oldVar_lovDescCustEmpAddrProvinceName = this.lovDescCustEmpAddrProvinceName.getValue();
		this.oldVar_custEmpAddrCity = this.custEmpAddrCity.getValue();
		this.oldVar_lovDescCustEmpAddrCityName = this.lovDescCustEmpAddrCityName.getValue();
		this.oldVar_custEmpAddrZIP = this.custEmpAddrZIP.getValue();
		
		//Non-Financial and Financial details for Corporate Customer Details
		this.oldVar_name = this.name.getValue();
		this.oldVar_phoneNumber = this.phoneNumber.getValue();
		this.oldVar_phoneNumber1 = this.phoneNumber1.getValue();
		this.oldVar_emailId = this.emailId.getValue();
		this.oldVar_bussCommenceDate = PennantAppUtil.getTimestamp(this.bussCommenceDate.getValue());	
		this.oldVar_servCommenceDate = PennantAppUtil.getTimestamp(this.servCommenceDate.getValue());	
		this.oldVar_bankRelationshipDate = PennantAppUtil.getTimestamp(this.bankRelationshipDate.getValue());	
		this.oldVar_paidUpCapital = this.paidUpCapital.getValue();
		this.oldVar_authorizedCapital = this.authorizedCapital.getValue();
		this.oldVar_reservesAndSurPlus = this.reservesAndSurPlus.getValue();
		this.oldVar_intangibleAssets = this.intangibleAssets.getValue();
		this.oldVar_tangibleNetWorth = this.tangibleNetWorth.getValue();
		this.oldVar_longTermLiabilities = this.longTermLiabilities.getValue();
		this.oldVar_capitalEmployed = this.capitalEmployed.getValue();
		this.oldVar_investments = this.investments.getValue();
		this.oldVar_nonCurrentAssets = this.nonCurrentAssets.getValue();
		this.oldVar_netWorkingCapital = this.netWorkingCapital.getValue();
		this.oldVar_netSales = this.netSales.getValue();
		this.oldVar_otherIncome = this.otherIncome.getValue();
		this.oldVar_netProfitAfterTax = this.netProfitAfterTax.getValue();
		this.oldVar_depreciation = this.depreciation.getValue();
		this.oldVar_cashAccurals = this.cashAccurals.getValue();
		this.oldVar_annualTurnover = this.annualTurnover.getValue();
		this.oldVar_returnOnCapitalEmp = this.returnOnCapitalEmp.getValue();
		this.oldVar_currentAssets = this.currentAssets.getValue();
		this.oldVar_currentLiabilities = this.currentLiabilities.getValue();
		this.oldVar_currentBookValue = this.currentBookValue.getValue();
		this.oldVar_currentMarketValue = this.currentMarketValue.getValue();
		this.oldVar_promotersShare = this.promotersShare.getValue();
		this.oldVar_associatesShare = this.associatesShare.getValue();
		this.oldVar_publicShare = this.publicShare.getValue();
		this.oldVar_finInstShare = this.finInstShare.getValue();
		this.oldVar_others = this.others.getValue();
		
		//Customer Related List storing
		this.oldVar_RatingsList = this.ratingsList;
		this.oldVar_AddressList = this.addressList;
		this.oldVar_PhoneNumberList = this.phoneNumberList;
		this.oldVar_DocumentsList = this.documentsList;
		this.oldVar_EmailList = this.emailList;
		this.oldVar_IncomeList = this.incomeList;
		this.oldVar_PRelationList = this.pRelationList;
		this.oldVar_DirectorsList = this.directorsList;
		this.oldVar_BalanceSheetList = this.balanceSheetList;
		
		logger.debug("Leaving");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		
		//Basic Details Tab-->1.Key Details
		this.custID.setValue(this.oldVar_custID);
		this.custCIF.setValue(this.oldVar_custCIF);
		this.custCoreBank.setValue(this.oldVar_custCoreBank);
		this.custTypeCode.setValue(this.oldVar_custTypeCode);
		this.lovDescCustTypeCodeName.setValue(this.oldVar_lovDescCustTypeCodeName);
		this.custDftBranch.setValue(this.oldVar_custDftBranch);
		this.lovDescCustDftBranchName.setValue(this.oldVar_lovDescCustDftBranchName);
		this.custGroupID.setValue(this.oldVar_custGroupID);
		this.lovDesccustGroupIDName.setValue(this.oldVar_lovDesccustGroupIDName);
		this.custBaseCcy.setValue(this.oldVar_custBaseCcy);
		this.lovDescCustBaseCcyName.setValue(this.oldVar_lovDescCustBaseCcyName);
		this.custLng.setValue(this.oldVar_custLng);
		this.lovDescCustLngName.setValue(this.oldVar_lovDescCustLngName);

		//Basic Details Tab-->2.Personal Details(Retail Customer)
		if (this.gb_personalDetails.isVisible()) {
			
			this.custGenderCode.setValue(this.oldVar_custGenderCode);
			this.lovDescCustGenderCodeName.setValue(this.oldVar_lovDescCustGenderCodeName);
			this.custSalutationCode.setValue(this.oldVar_custSalutationCode);
			this.lovDescCustSalutationCodeName.setValue(this.oldVar_lovDescCustSalutationCodeName);
			this.custFName.setValue(this.oldVar_custFName);
			this.custMName.setValue(this.oldVar_custMName);
			this.custLName.setValue(this.oldVar_custLName);
			this.custShrtName.setValue(this.oldVar_custShrtName);
			this.custFNameLclLng.setValue(this.oldVar_custFNameLclLng);
			this.custMNameLclLng.setValue(this.oldVar_custMNameLclLng);
			this.custLNameLclLng.setValue(this.oldVar_custLNameLclLng);
			this.custShrtNameLclLng.setValue(this.oldVar_custShrtNameLclLng);
			this.custDOB.setValue(this.oldVar_custDOB);
			this.custPOB.setValue(this.oldVar_custPOB);
			this.custCOB.setValue(this.oldVar_custCOB);
			this.lovDescCustCOBName.setValue(this.oldVar_lovDescCustCOBName);
			this.custMotherMaiden.setValue(this.oldVar_custMotherMaiden);
			this.custIsMinor.setChecked(this.oldVar_custIsMinor);
			this.custProfession.setValue(this.oldVar_custProfession);
			this.lovDescCustProfessionName.setValue(this.oldVar_lovDescCustProfessionName);
			this.custMaritalSts.setValue(this.oldVar_custMaritalSts);
			this.lovDescCustMaritalStsName.setValue(this.oldVar_lovDescCustMaritalStsName);
			
		} else {
			//Basic Details Tab-->3.Organization Details(Corporate Customer)
			
			this.corpCustOrgName.setValue(this.oldVar_custLName);
			this.corpCustShrtName.setValue(this.oldVar_custShrtName);
			this.corpCustOrgNameLclLng.setValue(this.oldVar_custLNameLclLng);
			this.corpCustShrtNameLclLng.setValue(this.oldVar_custShrtNameLclLng);
			this.corpCustPOB.setValue(this.oldVar_custPOB);
			this.corpCustCOB.setValue(this.oldVar_custCOB);
			this.lovDescCorpCustCOBName.setValue(this.oldVar_lovDescCustCOBName);
		}
		
		//Generic Information Tab-->1.General Details
		this.custSts.setValue(this.oldVar_custSts);
		this.lovDescCustStsName.setValue(this.oldVar_lovDescCustStsName);
		this.custStsChgDate.setValue(this.oldVar_custStsChgDate);
		this.custGroupSts.setValue(this.oldVar_custGroupSts);
		this.lovDescCustGroupStsName.setValue(this.oldVar_lovDescCustGroupStsName);
		this.custIsBlocked.setChecked(this.oldVar_custIsBlocked);
		this.custIsActive.setChecked(this.oldVar_custIsActive);
		this.custIsClosed.setChecked(this.oldVar_custIsClosed);
		this.custClosedOn.setValue(this.oldVar_custClosedOn);
		this.custInactiveReason.setValue(this.oldVar_custInactiveReason);
		this.custIsDecease.setChecked(this.oldVar_custIsDecease);
		this.custIsDormant.setChecked(this.oldVar_custIsDormant);
		this.custIsDelinquent.setChecked(this.oldVar_custIsDelinquent);
		this.custTradeLicenceNum.setValue(this.oldVar_custTradeLicenceNum);
		this.custTradeLicenceExpiry.setValue(this.oldVar_custTradeLicenceExpiry);
		this.custDateOfIncorporation.setValue(this.oldVar_custDateOfIncorporation);
		this.custPassportNo.setValue(this.oldVar_custPassportNo);
		this.custPassportExpiry.setValue(this.oldVar_custPassportExpiry);
		this.custVisaNum.setValue(this.oldVar_custVisaNum);
		this.custVisaExpiry.setValue(this.oldVar_custVisaExpiry);
		this.custIsStaff.setChecked(this.oldVar_custIsStaff);
		this.custStaffID.setValue(this.oldVar_custStaffID);
		this.custEmpSts.setValue(this.oldVar_custEmpSts);
		this.lovDescCustEmpStsName.setValue(this.oldVar_lovDescCustEmpStsName);
		this.custIsBlackListed.setChecked(this.oldVar_custIsBlackListed);
		this.custBLRsnCode.setValue(this.oldVar_custBLRsnCode);
		this.custIsRejected.setChecked(this.oldVar_custIsRejected);
		this.custRejectedRsn.setValue(this.oldVar_custRejectedRsn);
		this.lovDescCustBLRsnCodeName.setValue(this.oldVar_lovDescCustBLRsnCodeName);
		this.lovDescCustRejectedRsnName.setValue(this.oldVar_lovDescCustRejectedRsnName);
		this.custFirstBusinessDate.setValue(this.oldVar_custFirstBusinessDate);
		
		//Demographic Details Tab-->1.Segmentation Details
		this.custCtgCode.setValue(this.oldVar_custCtgCode);
		this.lovDescCustCtgCodeName.setValue(this.oldVar_lovDescCustCtgCodeName);
		this.custSector.setValue(this.oldVar_custSector);
		this.lovDescCustSectorName.setValue(this.oldVar_lovDescCustSectorName);
		this.custSubSector.setValue(this.oldVar_custSubSector);
		this.lovDescCustSubSectorName.setValue(this.oldVar_lovDescCustSubSectorName);
		this.custIndustry.setValue(this.oldVar_custIndustry);
		this.lovDescCustIndustryName.setValue(this.oldVar_lovDescCustIndustryName);
		this.custSegment.setValue(this.oldVar_custSegment);
		this.lovDescCustSegmentName.setValue(this.oldVar_lovDescCustSegmentName);
		this.custSubSegment.setValue(this.oldVar_custSubSegment);
		this.lovDescCustSubSegmentName.setValue(this.oldVar_lovDescCustSubSegmentName);
		
		//KYC Details Tab-->1.Income Details
		this.custTotalIncome.setValue(this.oldVar_custTotalIncome);
		
		//KYC Details Tab-->1.Identity Details
		this.custParentCountry.setValue(this.oldVar_custParentCountry);
		this.custResdCountry.setValue(this.oldVar_custResdCountry);
		this.lovDescCustResdCountryName.setValue(this.oldVar_lovDescCustResdCountryName);
		this.custRiskCountry.setValue(this.oldVar_custRiskCountry);
		this.custNationality.setValue(this.oldVar_custNationality);
		this.lovDescCustNationalityName.setValue(this.oldVar_lovDescCustNationalityName);
		
		//Preferential Details Tab-->1.Non-Financial RelationShip Details
		this.custDSA.setValue(this.oldVar_custDSA);
		this.custDSADept.setValue(this.oldVar_custDSADept);
		this.lovDescCustDSADeptName.setValue(this.oldVar_lovDescCustDSADeptName);
		this.custRO1.setValue(this.oldVar_custRO1);
		this.lovDescCustRO1Name.setValue(this.oldVar_lovDescCustRO1Name);
		this.custRO2.setValue(this.oldVar_custRO2);
		this.lovDescCustRO2Name.setValue(this.oldVar_lovDescCustRO2Name);
		this.custReferedBy.setValue(this.oldVar_custReferedBy);
		
		//Preferential Details Tab-->2.Statement Details
		this.custStmtFrq.setValue(this.oldVar_custStmtFrq);
		this.custIsStmtCombined.setChecked(this.oldVar_custIsStmtCombined);
		this.custStmtLastDate.setValue(this.oldVar_custStmtLastDate);
		this.custStmtNextDate.setValue(this.oldVar_custStmtNextDate);
		this.custStmtDispatchMode.setValue(this.oldVar_custStmtDispatchMode);
		this.lovDescDispatchModeDescName.setValue(this.oldVar_lovDescDispatchModeDescName);
		
		this.recordStatus.setValue(this.oldVar_recordStatus);

		// Customer Employee Details
		this.custEmpName.setValue(this.oldVar_custEmpName);
		this.custEmpFrom.setValue(this.oldVar_custEmpFrom);
		this.custEmpDesg.setValue(this.oldVar_custEmpDesg);
		this.lovDescCustEmpDesgName.setValue(this.oldVar_lovDescCustEmpDesgName);
		this.custEmpDept.setValue(this.oldVar_custEmpDept);
		this.lovDescCustEmpDeptName.setValue(this.oldVar_lovDescCustEmpDeptName);
		this.custEmpID.setValue(this.oldVar_custEmpID);
		this.custEmpType.setValue(this.oldVar_custEmptype);
		this.lovDescCustEmpTypeName.setValue(this.oldVar_lovDescCustEmpTypeName);
		this.custEmpHNbr.setValue(this.oldVar_custEmpHNbr);
		this.custEmpFlatNbr.setValue(this.oldVar_custEmpFlatNbr);
		this.custEmpAddrStreet.setValue(this.oldVar_custEmpAddrStreet);
		this.custEMpAddrLine1.setValue(this.oldVar_custEmpAddrLine1);
		this.custEMpAddrLine2.setValue(this.oldVar_custEmpAddrLine2);
		this.custEmpPOBox.setValue(this.oldVar_custEmpPOBox);
		this.custEmpAddrPhone.setValue(this.oldVar_custEmpAddrPhone);
		this.custEmpAddrCountry.setValue(this.oldVar_custEmpAddrCountry);
		this.lovDescCustEmpAddrCountryName.setValue(this.oldVar_lovDescCustEmpAddrCountryName);
		this.custEmpAddrProvince.setValue(this.oldVar_custEmpAddrProvince);
		this.lovDescCustEmpAddrProvinceName.setValue(this.oldVar_lovDescCustEmpAddrProvinceName);
		this.custEmpAddrCity.setValue(this.oldVar_custEmpAddrCity);
		this.lovDescCustEmpAddrCityName.setValue(this.oldVar_lovDescCustEmpAddrCityName);
		this.custEmpAddrZIP.setValue(this.oldVar_custEmpAddrZIP);
		
		//Non-Financial and Financial details for Corporate Customer Details
		this.name.setValue(this.oldVar_name);
		this.phoneNumber.setValue(this.oldVar_phoneNumber);
		this.phoneNumber1.setValue(this.oldVar_phoneNumber1);
		this.emailId.setValue(this.oldVar_emailId);
		this.bussCommenceDate.setValue(this.oldVar_bussCommenceDate);
		this.servCommenceDate.setValue(this.oldVar_servCommenceDate);
		this.bankRelationshipDate.setValue(this.oldVar_bankRelationshipDate);
	  	this.paidUpCapital.setValue(this.oldVar_paidUpCapital);
	  	this.authorizedCapital.setValue(this.oldVar_authorizedCapital);
	  	this.reservesAndSurPlus.setValue(this.oldVar_reservesAndSurPlus);
	  	this.intangibleAssets.setValue(this.oldVar_intangibleAssets);
	  	this.tangibleNetWorth.setValue(this.oldVar_tangibleNetWorth);
	  	this.longTermLiabilities.setValue(this.oldVar_longTermLiabilities);
	  	this.capitalEmployed.setValue(this.oldVar_capitalEmployed);
	  	this.investments.setValue(this.oldVar_investments);
	  	this.nonCurrentAssets.setValue(this.oldVar_nonCurrentAssets);
	  	this.netWorkingCapital.setValue(this.oldVar_netWorkingCapital);
	  	this.netSales.setValue(this.oldVar_netSales);
	  	this.otherIncome.setValue(this.oldVar_otherIncome);
	  	this.netProfitAfterTax.setValue(this.oldVar_netProfitAfterTax);
	  	this.depreciation.setValue(this.oldVar_depreciation);
	  	this.cashAccurals.setValue(this.oldVar_cashAccurals);
	  	this.annualTurnover.setValue(this.oldVar_annualTurnover);
	  	this.returnOnCapitalEmp.setValue(this.oldVar_returnOnCapitalEmp);
	  	this.currentAssets.setValue(this.oldVar_currentAssets);
	  	this.currentLiabilities.setValue(this.oldVar_currentLiabilities);
	  	this.currentBookValue.setValue(this.oldVar_currentBookValue);
	  	this.currentMarketValue.setValue(this.oldVar_currentMarketValue);
	  	this.promotersShare.setValue(this.oldVar_promotersShare);
	  	this.associatesShare.setValue(this.oldVar_associatesShare);
	  	this.publicShare.setValue(this.oldVar_publicShare);
	  	this.finInstShare.setValue(this.oldVar_finInstShare);
	  	this.others.setValue(this.oldVar_others);
	  	
	  	//Customer Related Lists
		this.ratingsList = this.oldVar_RatingsList;
		this.addressList = this.oldVar_AddressList;
		this.phoneNumberList = this.oldVar_PhoneNumberList;
		this.documentsList = this.oldVar_DocumentsList;
		this.emailList = this.oldVar_EmailList;
		this.incomeList = this.oldVar_IncomeList;
		this.pRelationList = this.oldVar_PRelationList;
		this.directorsList = this.oldVar_DirectorsList;
		this.balanceSheetList = this.oldVar_BalanceSheetList;

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
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

		//Basic Details Tab-->1.Key Details
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
		if (this.oldVar_custGroupID != this.custGroupID.longValue()) {
			return true;
		}
		if (this.oldVar_custBaseCcy != this.custBaseCcy.getValue()) {
			return true;
		}
		if (this.oldVar_custLng != this.custLng.getValue()) {
			return true;
		}
		
		if (this.gb_personalDetails.isVisible()) {
			//Basic Details Tab-->2.Personal Details(Retail Customer)
			
			if (this.oldVar_custGenderCode != this.custGenderCode.getValue()) {
				return true;
			}
			if (this.oldVar_custSalutationCode != this.custSalutationCode.getValue()) {
				return true;
			}
			if (this.oldVar_custFName != this.custFName.getValue()) {
				return true;
			}
			if (this.oldVar_custMName != this.custMName.getValue()) {
				return true;
			}
			if (this.oldVar_custLName != this.custLName.getValue()) {
				return true;
			}
			if (this.oldVar_custShrtName != this.custShrtName.getValue()) {
				return true;
			}
			if (this.oldVar_custFNameLclLng != this.custFNameLclLng.getValue()) {
				return true;
			}
			if (this.oldVar_custMNameLclLng != this.custMNameLclLng.getValue()) {
				return true;
			}
			if (this.oldVar_custLNameLclLng != this.custLNameLclLng.getValue()) {
				return true;
			}
			if (this.oldVar_custShrtNameLclLng != this.custShrtNameLclLng.getValue()) {
				return true;
			}
			String old_custDOB = "";
			String new_custDOB = "";
			if (this.oldVar_custDOB != null) {
				old_custDOB = DateUtility.formatDate(this.oldVar_custDOB,PennantConstants.dateFormat);
			}
			if (this.custDOB.getValue() != null) {
				new_custDOB = DateUtility.formatDate(this.custDOB.getValue(),PennantConstants.dateFormat);
			}
			if (!StringUtils.trimToEmpty(old_custDOB).equals(StringUtils.trimToEmpty(new_custDOB))) {
				return true;
			}
			if (this.oldVar_custPOB != this.custPOB.getValue()) {
				return true;
			}
			if (this.oldVar_custCOB != this.custCOB.getValue()) {
				return true;
			}
			if (this.oldVar_custMotherMaiden != this.custMotherMaiden.getValue()) {
				return true;
			}
			if (this.oldVar_custIsMinor != this.custIsMinor.isChecked()) {
				return true;
			}
			if (this.oldVar_custProfession != this.custProfession.getValue()) {
				return true;
			}
			if (this.oldVar_custMaritalSts != this.custMaritalSts.getValue()) {
				return true;
			}
			
		} else {
			
			//Basic Details Tab-->3.Organization Details(Corporate Customer)
			
			if (this.oldVar_custLName != this.corpCustOrgName.getValue()) {
				return true;
			}
			if (this.oldVar_custShrtName != this.corpCustShrtName.getValue()) {
				return true;
			}
			if (this.oldVar_custLNameLclLng != this.corpCustOrgNameLclLng.getValue()) {
				return true;
			}
			if (this.oldVar_custShrtNameLclLng != this.corpCustShrtNameLclLng.getValue()) {
				return true;
			}
			if (this.oldVar_custPOB != this.corpCustPOB.getValue()) {
				return true;
			}
			if (this.oldVar_custCOB != this.corpCustCOB.getValue()) {
				return true;
			}
		}
		
		//Generic Information Tab-->1.General Details
		if (this.oldVar_custSts != this.custSts.getValue()) {
			return true;
		}
		if (this.oldVar_custStsChgDate != this.custStsChgDate.getValue()) {
			return true;
		}
		if (this.oldVar_custGroupSts != this.custGroupSts.getValue()) {
			return true;
		}
		if (this.oldVar_custIsBlocked != this.custIsBlocked.isChecked()) {
			return true;
		}
		if (this.oldVar_custIsActive != this.custIsActive.isChecked()) {
			return true;
		}
		if (this.oldVar_custIsClosed != this.custIsClosed.isChecked()) {
			return true;
		}
		String old_custClosedOn = "";
		String new_custClosedOn = "";
		if (this.oldVar_custClosedOn != null) {
			old_custClosedOn = DateUtility.formatDate(this.oldVar_custClosedOn,
					PennantConstants.dateFormat);
		}
		if (this.custClosedOn.getValue() != null) {
			new_custClosedOn = DateUtility.formatDate(
					this.custClosedOn.getValue(), PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_custClosedOn).equals(
				StringUtils.trimToEmpty(new_custClosedOn))) {
			return true;
		}
		if (this.oldVar_custInactiveReason != this.custInactiveReason.getValue()) {
			return true;
		}
		if (this.oldVar_custIsDecease != this.custIsDecease.isChecked()) {
			return true;
		}
		if (this.oldVar_custIsDormant != this.custIsDormant.isChecked()) {
			return true;
		}
		if (this.oldVar_custIsDelinquent != this.custIsDelinquent.isChecked()) {
			return true;
		}
		if (this.oldVar_custIsBlackListed != this.custIsBlackListed.isChecked()) {
			return true;
		}
		if (this.oldVar_custBLRsnCode != this.custBLRsnCode.getValue()) {
			return true;
		}
		if (this.oldVar_custIsRejected != this.custIsRejected.isChecked()) {
			return true;
		}
		if (this.oldVar_custRejectedRsn != this.custRejectedRsn.getValue()) {
			return true;
		}
		
		if(this.row_retailPPT.isVisible() && this.row_retailVisa.isVisible() && 
				this.row_custStaff.isVisible() && this.row_EmpSts.isVisible()){
			if (this.oldVar_custPassportNo != this.custPassportNo.getValue()) {
				return true;
			}
			if (this.oldVar_custPassportExpiry != this.custPassportExpiry.getValue()) {
				return true;
			}
			if (this.oldVar_custVisaNum != this.custVisaNum.getValue()) {
				return true;
			}
			if (this.oldVar_custVisaExpiry != this.custVisaExpiry.getValue()) {
				return true;
			}
			if (this.oldVar_custIsStaff != this.custIsStaff.isChecked()) {
				return true;
			}
			if (this.oldVar_custStaffID != this.custStaffID.getValue()) {
				return true;
			}
			if (this.oldVar_custEmpSts != this.custEmpSts.getValue()) {
				return true;
			}
		}else if(this.row_corpTL.isVisible() && this.row_corpTLED.isVisible()){
			if (this.oldVar_custTradeLicenceNum != this.custTradeLicenceNum.getValue()) {
				return true;
			}
			if (this.oldVar_custTradeLicenceExpiry != this.custTradeLicenceExpiry.getValue()) {
				return true;
			}
			String old_custDateOfIncorporation = "";
			String new_custDateOfIncorporation = "";
			if (this.oldVar_custDateOfIncorporation != null) {
				old_custDateOfIncorporation = DateUtility.formatDate(
						this.oldVar_custDateOfIncorporation,PennantConstants.dateFormat);
			}
			if (this.custDateOfIncorporation.getValue() != null) {
				new_custDateOfIncorporation = DateUtility.formatDate(
						this.custDateOfIncorporation.getValue(),PennantConstants.dateFormat);
			}
			if (!StringUtils.trimToEmpty(old_custDateOfIncorporation).equals(
					StringUtils.trimToEmpty(new_custDateOfIncorporation))) {
				return true;
			}
		}
		String old_custFirstBusinessDate = "";
		String new_custFirstBusinessDate = "";
		if (this.oldVar_custFirstBusinessDate != null) {
			old_custFirstBusinessDate = DateUtility.formatDate(
					this.oldVar_custFirstBusinessDate, PennantConstants.dateFormat);
		}
		if (this.custFirstBusinessDate.getValue() != null) {
			new_custFirstBusinessDate = DateUtility.formatDate(
					this.custFirstBusinessDate.getValue(), PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_custFirstBusinessDate).equals(
				StringUtils.trimToEmpty(new_custFirstBusinessDate))) {
			return true;
		}
		
		//Demographic Details Tab-->1.Segmentation Details
		if (this.oldVar_custCtgCode != this.custCtgCode.getValue()) {
			return true;
		}
		if (this.oldVar_custSector != this.custSector.getValue()) {
			return true;
		}
		if (this.oldVar_custSubSector != this.custSubSector.getValue()) {
			return true;
		}
		if (this.oldVar_custIndustry != this.custIndustry.getValue()) {
			return true;
		}
		if (this.oldVar_custSegment != this.custSegment.getValue()) {
			return true;
		}
		if (this.oldVar_custSubSegment != this.custSubSegment.getValue()) {
			return true;
		}
		
		//KYC Details Tab-->1.Income Details
		if (this.oldVar_custTotalIncome != this.custTotalIncome.getValue()) {
			return true;
		}
		
		//KYC Details Tab-->1.Identity Details
		if (this.oldVar_custParentCountry != this.custParentCountry.getValue()) {
			return true;
		}
		if (this.oldVar_custResdCountry != this.custResdCountry.getValue()) {
			return true;
		}
		if (this.oldVar_custRiskCountry != this.custRiskCountry.getValue()) {
			return true;
		}
		if (this.oldVar_custNationality != this.custNationality.getValue()) {
			return true;
		}
		
		//Preferential Details Tab-->1.Non-Financial RelationShip Details
		if (this.oldVar_custDSA != this.custDSA.getValue()) {
			return true;
		}
		if (this.oldVar_custDSADept != this.custDSADept.getValue()) {
			return true;
		}
		if (this.oldVar_custRO1 != this.custRO1.getValue()) {
			return true;
		}
		if (this.oldVar_custRO2 != this.custRO2.getValue()) {
			return true;
		}
		if (this.oldVar_custReferedBy != this.custReferedBy.getValue()) {
			return true;
		}
		
		//Preferential Details Tab-->2.Statement Details
		if (this.oldVar_custStmtFrq != this.custStmtFrq.getValue()) {
			return true;
		}
		if (this.oldVar_custIsStmtCombined != this.custIsStmtCombined.isChecked()) {
			return true;
		}
		if (this.oldVar_custStmtLastDate != this.custStmtLastDate.getValue()) {
			return true;
		}
		if (this.oldVar_custStmtNextDate != this.custStmtNextDate.getValue()) {
			return true;
		}
		if (this.oldVar_custStmtDispatchMode != this.custStmtDispatchMode.getValue()) {
			return true;
		}
		
		//Customer Employment Details Tab
		if(this.employmentDetails.isVisible()){
			if (this.oldVar_custEmpName != this.custEmpName.getValue()) {
				return true;
			}
			if (this.oldVar_custEmpFrom != this.custEmpFrom.getValue()) {
				return true;
			}
			if (this.oldVar_custEmpDesg != this.custEmpDesg.getValue()) {
				return true;
			}
			if (this.oldVar_custEmpDept != this.custEmpDept.getValue()) {
				return true;
			}
			if (this.oldVar_custEmpID != this.custEmpID.getValue()) {
				return true;
			}
			if (this.oldVar_custEmptype != this.custEmpType.getValue()) {
				return true;
			}
			if (this.oldVar_custEmpHNbr != this.custEmpHNbr.getValue()) {
				return true;
			}
			if (this.oldVar_custEmpFlatNbr != this.custEmpFlatNbr.getValue()) {
				return true;
			}
			if (this.oldVar_custEmpAddrStreet != this.custEmpAddrStreet.getValue()) {
				return true;
			}
			if (this.oldVar_custEmpAddrLine1 != this.custEMpAddrLine1.getValue()) {
				return true;
			}
			if (this.oldVar_custEmpAddrLine2 != this.custEMpAddrLine2.getValue()) {
				return true;
			}
			if (this.oldVar_custEmpPOBox != this.custEmpPOBox.getValue()) {
				return true;
			}
			if (this.oldVar_custEmpAddrPhone != this.custEmpAddrPhone.getValue()) {
				return true;
			}
			if (this.oldVar_custEmpAddrCountry != this.custEmpAddrCountry.getValue()) {
				return true;
			}
			if (this.oldVar_custEmpAddrProvince != this.custEmpAddrProvince.getValue()) {
				return true;
			}
			if (this.oldVar_custEmpAddrCity != this.custEmpAddrCity.getValue()) {
				return true;
			}
			if (this.oldVar_custEmpAddrZIP != this.custEmpAddrZIP.getValue()) {
				return true;
			}
		}
		//Non-Financial and Financial details for Corporate Customer Details
		if(getCustomerDetails().getCustomer().getLovDescCustCtgType().equalsIgnoreCase("C")){
			if (this.oldVar_name != this.name.getValue()) {
				return true;
			}
			if (this.oldVar_phoneNumber != this.phoneNumber.getValue()) {
				return true;
			}
			if (this.oldVar_phoneNumber1 != this.phoneNumber1.getValue()) {
				return true;
			}
			if (this.oldVar_emailId != this.emailId.getValue()) {
				return true;
			}
			String old_bussCommenceDate = "";
			String new_bussCommenceDate = "";
			if (this.oldVar_bussCommenceDate != null) {
				old_bussCommenceDate = DateUtility.formatDate(this.oldVar_bussCommenceDate,PennantConstants.dateFormat);
			}
			if (this.bussCommenceDate.getValue() != null) {
				new_bussCommenceDate = DateUtility.formatDate(this.bussCommenceDate.getValue(),PennantConstants.dateFormat);
			}
			if (!StringUtils.trimToEmpty(old_bussCommenceDate).equals(StringUtils.trimToEmpty(new_bussCommenceDate))) {
				return true;
			}
			String old_servCommenceDate = "";
			String new_servCommenceDate = "";
			if (this.oldVar_servCommenceDate != null) {
				old_servCommenceDate = DateUtility.formatDate(this.oldVar_servCommenceDate,PennantConstants.dateFormat);
			}
			if (this.servCommenceDate.getValue() != null) {
				new_servCommenceDate = DateUtility.formatDate(this.servCommenceDate.getValue(),PennantConstants.dateFormat);
			}
			if (!StringUtils.trimToEmpty(old_servCommenceDate).equals(StringUtils.trimToEmpty(new_servCommenceDate))) {
				return true;
			}
			String old_bankRelationshipDate = "";
			String new_bankRelationshipDate = "";
			if (this.oldVar_bankRelationshipDate != null) {
				old_bankRelationshipDate = DateUtility.formatDate(this.oldVar_bankRelationshipDate,PennantConstants.dateFormat);
			}
			if (this.bankRelationshipDate.getValue() != null) {
				new_bankRelationshipDate = DateUtility.formatDate(this.bankRelationshipDate.getValue(),PennantConstants.dateFormat);
			}
			if (!StringUtils.trimToEmpty(old_bankRelationshipDate).equals(StringUtils.trimToEmpty(new_bankRelationshipDate))) {
				return true;
			}
			if (this.oldVar_paidUpCapital != this.paidUpCapital.getValue()) {
				return true;
			}
			if (this.oldVar_authorizedCapital != this.authorizedCapital.getValue()) {
				return true;
			}
			if (this.oldVar_reservesAndSurPlus != this.reservesAndSurPlus.getValue()) {
				return true;
			}
			if (this.oldVar_intangibleAssets != this.intangibleAssets.getValue()) {
				return true;
			}
			if (this.oldVar_tangibleNetWorth != this.tangibleNetWorth.getValue()) {
				return true;
			}
			if (this.oldVar_longTermLiabilities != this.longTermLiabilities.getValue()) {
				return true;
			}
			if (this.oldVar_capitalEmployed != this.capitalEmployed.getValue()) {
				return true;
			}
			if (this.oldVar_investments != this.investments.getValue()) {
				return true;
			}
			if (this.oldVar_nonCurrentAssets != this.nonCurrentAssets.getValue()) {
				return true;
			}
			if (this.oldVar_netWorkingCapital != this.netWorkingCapital.getValue()) {
				return true;
			}
			if (this.oldVar_netSales != this.netSales.getValue()) {
				return true;
			}
			if (this.oldVar_otherIncome != this.otherIncome.getValue()) {
				return true;
			}
			if (this.oldVar_netProfitAfterTax != this.netProfitAfterTax.getValue()) {
				return true;
			}
			if (this.oldVar_depreciation != this.depreciation.getValue()) {
				return true;
			}
			if (this.oldVar_cashAccurals != this.cashAccurals.getValue()) {
				return true;
			}
			if (this.oldVar_annualTurnover != this.annualTurnover.getValue()) {
				return true;
			}
			if (this.oldVar_returnOnCapitalEmp != this.returnOnCapitalEmp.getValue()) {
				return true;
			}
			if (this.oldVar_currentAssets != this.currentAssets.getValue()) {
				return true;
			}
			if (this.oldVar_currentLiabilities != this.currentLiabilities.getValue()) {
				return true;
			}
			if (this.oldVar_currentBookValue != this.currentBookValue.getValue()) {
				return true;
			}
			if (this.oldVar_currentMarketValue != this.currentMarketValue.getValue()) {
				return true;
			}
			if (this.oldVar_promotersShare != this.promotersShare.getValue()) {
				return true;
			}
			if (this.oldVar_associatesShare != this.associatesShare.getValue()) {
				return true;
			}
			if (this.oldVar_publicShare != this.publicShare.getValue()) {
				return true;
			}
			if (this.oldVar_finInstShare != this.finInstShare.getValue()) {
				return true;
			}
			if (this.oldVar_others != this.others.getValue()) {
				return true;
			}
		}
		
		
		//Customer Related List
		if (this.oldVar_RatingsList != this.ratingsList) {
			return true;
		}
		if (this.oldVar_AddressList != this.addressList) {
			return true;
		}
		if (this.oldVar_IncomeList != this.incomeList) {
			return true;
		}
		if (this.oldVar_PRelationList != this.pRelationList) {
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
		if (this.oldVar_DirectorsList != this.directorsList) {
			return true;
		}
		if (this.oldVar_BalanceSheetList != this.balanceSheetList) {
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

		//Basic Details Tab-->1.Key Details
		if (!this.custCIF.isReadonly()) {
			this.custCIF.setConstraint(new SimpleConstraint(this.CUSTCIF_REGEX,Labels.getLabel(
				"MAND_FIELD_ALLOWED_CHARS",new String[] {Labels.getLabel(
						"label_CustomerDialog_CustCIF.value"),parms[0], parms[1] })));
		}
		if (!this.custCoreBank.isReadonly()) {
			this.custCoreBank.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_CustomerDialog_CustCoreBank.value") }));
		}
		
		//Basic Details Tab-->2.Personal Details(Retail Customer)
		if (this.gb_personalDetails.isVisible()) {
			if (!this.custFName.isReadonly()) {
				this.custFName.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
					Labels.getLabel("MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustFName.value") })));
			}
			if (!this.custMName.isReadonly()) {
				this.custMName.setConstraint(new SimpleConstraint(PennantConstants.NM_NAME_REGEX,
					Labels.getLabel("FIELD_CHARACTER",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustMName.value") })));
			}
			if (!this.custLName.isReadonly()) {
				this.custLName.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
					Labels.getLabel("MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustLName.value") })));
			}
			if (!this.custShrtName.isReadonly()) {
				this.custShrtName.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
					Labels.getLabel("MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustShrtName.value") })));
			}
			if (!this.custPOB.isReadonly() && !this.custPOB.isDisabled()) {
				this.custPOB.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
					Labels.getLabel("MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustPOB.value") })));
			}
			if (!this.custDOB.isReadonly() && !this.custDOB.isDisabled()) {
				this.custDOB.setConstraint("NO EMPTY,NO TODAY,NO FUTURE:"+ Labels.getLabel(
					"DATE_EMPTY_FUTURE_TODAY",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustDOB.value") }));
			}
			if (!this.custMotherMaiden.isReadonly()) {
				this.custMotherMaiden.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
					Labels.getLabel("MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustMotherMaiden.value") })));
			}
			if (!(this.custLng.getValue().equals(PennantConstants.default_Language))
					&& this.row_localLngFM.isVisible()) {

				this.custFNameLclLng.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
					Labels.getLabel("MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustFNameLclLng.value") })));

				if (!(this.custMName.getValue().equals(""))) {
					this.custMNameLclLng.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
							Labels.getLabel("MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
									"label_CustomerDialog_CustMNameLclLng.value") })));
				}

				this.custLNameLclLng.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
						Labels.getLabel("MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
								"label_CustomerDialog_CustLNameLclLng.value") })));

				this.custShrtNameLclLng.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
						Labels.getLabel("MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
								"label_CustomerDialog_CustShrtNameLclLng.value") })));

			}
		} else if(this.gb_corporateCustomerPersonalDetails.isVisible()){
			//Basic Details Tab-->2.Organization Details(Corporate Customer)
			if (!this.corpCustOrgName.isReadonly()) {
				this.corpCustOrgName.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
					Labels.getLabel("MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustLName.value") })));
			}
			if (!this.corpCustShrtName.isReadonly()) {
				this.corpCustShrtName.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
					Labels.getLabel("MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustShrtName.value") })));
			}
			if (!this.corpCustPOB.isReadonly()) {
				this.corpCustPOB.setConstraint(new SimpleConstraint( PennantConstants.NAME_REGEX,
						Labels.getLabel( "MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
								"label_CustomerDialog_CorpCustPOB.value") })));
			}
			if (!(this.custLng.getValue().equals(PennantConstants.default_Language))
					&& this.row_localLngCorpCustCS.isVisible()) {
				this.corpCustOrgNameLclLng.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
					Labels.getLabel("MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustOrgNameLclLng.value") })));

				this.corpCustShrtNameLclLng.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
					Labels.getLabel("MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustShrtNameLclLng.value") })));

			}
		}

		// Generic Information Tab
		if(this.row_retailPPT.isVisible() && 
				this.row_custStaff.isVisible() && this.row_EmpSts.isVisible()){
			//For Retail Customer
			if(getCustomerDetails().getCustomer().getLovDescCustCtgType().equals("I")){
				if (!this.custPassportNo.isReadonly()) {
					this.custPassportNo.setConstraint(new SimpleConstraint(PennantConstants.PPT_VISA_REGEX,
						Labels.getLabel("MAND_FIELD_CHAR_NUMBER",new String[] { Labels.getLabel(
								"label_CustomerDialog_CustPassportNo.value") })));
				}
				if (!this.custPassportExpiry.isDisabled() && !(custPassportNo.getValue().equals(""))) {
					this.custPassportExpiry.setConstraint("NO EMPTY,NO TODAY,NO PAST:"+ Labels.getLabel(
						"DATE_EMPTY_PAST_TODAY", new String[] { Labels.getLabel(
								"label_CustomerDialog_CustPassportExpiry.value") }));
				}
				if(this.row_retailVisa.isVisible()){
					if (!this.custVisaNum.isReadonly()) {
						this.custVisaNum.setConstraint(new SimpleConstraint(PennantConstants.PPT_VISA_REGEX,
								Labels.getLabel("MAND_FIELD_CHAR_NUMBER",new String[] { Labels.getLabel(
								"label_CustomerDialog_CustVisaNum.value") })));
					}
					if (!this.custVisaExpiry.isDisabled() && !this.custVisaNum.getValue().equals("")) {
						this.custVisaExpiry.setConstraint("NO EMPTY,NO TODAY,NO PAST:" + Labels.getLabel(
								"DATE_EMPTY_PAST_TODAY", new String[] { Labels.getLabel(
								"label_CustomerDialog_CustVisaExpiry.value") }));
					}
				}
				if (this.custIsStaff.isChecked()) {
					this.custStaffID.setConstraint(new SimpleConstraint(PennantConstants.ALPHANUM_REGEX,
						Labels.getLabel("MAND_FIELD_CHAR_NUMBER", new String[] { Labels.getLabel(
								"label_CustomerDialog_CustStaffID.value") })));
				}
			}
			
		}else if(this.row_corpTL.isVisible()){
			if(getCustomerDetails().getCustomer().getLovDescCustCtgType().equals("C")){
				if (!this.custTradeLicenceNum.isReadonly()) {
					this.custTradeLicenceNum.setConstraint(new SimpleConstraint(PennantConstants.TRADE_LICENSE_REGEX,
						Labels.getLabel("MAND_FIELD_CHAR_NUMBER",new String[] { Labels.getLabel(
								"label_CustomerDialog_CustTradeLicenceNum.value") })));
				}
				if (!this.custTradeLicenceExpiry.isReadonly() && !this.custTradeLicenceNum.getValue().equals("")) {
					this.custTradeLicenceExpiry.setConstraint("NO EMPTY,NO TODAY,NO PAST:"+ Labels.getLabel(
						"DATE_EMPTY_PAST_TODAY",new String[] { Labels.getLabel(
								"label_CustomerDialog_CustTradeLicenceExpiry.value") }));
				}
				if (!this.custDateOfIncorporation.isReadonly()) {
					this.custDateOfIncorporation.setConstraint("NO EMPTY,NO TODAY,NO FUTURE:"+ Labels.getLabel(
						"DATE_EMPTY_FUTURE_TODAY",new String[] { Labels.getLabel(
								"label_CustomerDialog_CustDateOfIncorporation.value") }));
				}
			}
		}
		if (!this.custIsActive.isChecked()) {
			this.custInactiveReason.setConstraint(new SimpleConstraint( PennantConstants.NAME_REGEX,
				Labels.getLabel("MAND_FIELD_CHARACTER", new String[] { Labels.getLabel(
						"label_CustomerDialog_CustInactiveReason.value") })));
		}
		if (custIsClosed.isChecked()) {
			this.custClosedOn.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_CustomerDialog_CustClosedOn.value") }));
		}
		if (custIsBlackListed.isChecked()) {
			this.lovDescCustBLRsnCodeName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_CustomerDialog_CustBLRsnCode.value") }));
		}
		if (custIsRejected.isChecked()) {
			this.lovDescCustRejectedRsnName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_CustomerDialog_CustRejectedRsn.value") }));
		}
		
		// EMPLOYMENT Details Tab
		if(getCustomerDetails().getCustomer().getLovDescCustCtgType().equalsIgnoreCase("I")){
			if (this.gb_employerDetails.isVisible()) {
				if (!this.custEmpName.isReadonly()) {
					this.custEmpName.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
							Labels.getLabel("MAND_FIELD_CHARACTER", new String[] { Labels.getLabel(
							"label_CustomerEmploymentDetailDialog_CustEmpName.value") })));
				}
				if (!this.custEmpID.isReadonly()) {
					this.custEmpID.setConstraint(new SimpleConstraint(PennantConstants.ALPHANUM_REGEX,
							Labels.getLabel("MAND_FIELD_CHAR_NUMBER",new String[] { Labels.getLabel(
							"label_CustomerEmploymentDetailDialog_CustEmpID.value") })));
				}
				if (!this.custEmpFrom.isReadonly()) {
					this.custEmpFrom.setConstraint("NO EMPTY,NO TODAY,NO FUTURE:" + Labels.getLabel(
							"DATE_EMPTY_FUTURE_TODAY", new String[] { Labels.getLabel(
							"label_CustomerEmploymentDetailDialog_CustEmpFrom.value") }));
				}
				if (!this.custEmpPOBox.isReadonly()) {
					this.custEmpPOBox.setConstraint(new SimpleConstraint( PennantConstants.NUM_REGEX,
							Labels.getLabel("MAND_NUMBER", new String[] { Labels.getLabel(
							"label_CustomerEmploymentDetailDialog_CustEmpPOBox.value") })));
				}
				if (!this.custEmpAddrZIP.isReadonly()) {
					if (!StringUtils.trimToEmpty(this.custEmpAddrZIP.getValue()).equals("")) {
						this.custEmpAddrZIP.setConstraint(new SimpleConstraint(PennantConstants.ZIP_REGEX,
								Labels.getLabel("MAND_NUMBER", new String[] { Labels.getLabel(
								"label_CustomerEmploymentDetailDialog_CustEmpAddrZIP.value") })));
					}
				}
				if (!this.custEmpAddrPhone.isReadonly()) {
					this.custEmpAddrPhone.setConstraint(new SimpleConstraint( PennantConstants.PH_REGEX,
							Labels.getLabel("MAND_NUMBER",new String[] { Labels.getLabel(
							"label_CustomerEmploymentDetailDialog_CustEmpAddrPhone.value") })));
				}
				boolean addressConstraint = false;
				if (StringUtils.trimToEmpty(this.custEmpHNbr.getValue()).equals("")
						&& StringUtils.trimToEmpty(this.custEmpFlatNbr.getValue()).equals("")
						&& StringUtils.trimToEmpty(this.custEmpAddrStreet.getValue()).equals("")
						&& StringUtils.trimToEmpty(this.custEMpAddrLine1.getValue()).equals("")
						&& StringUtils.trimToEmpty(this.custEMpAddrLine2.getValue()).equals("")) {
					addressConstraint = true;
				}
				if (addressConstraint) {
					this.custEmpHNbr.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_ADDRESS",new String[] { 
							Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpHNbr.value") }));
				}
				if (addressConstraint) {
					this.custEmpFlatNbr.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_ADDRESS",
							new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEMpFlatNbr.value") }));
				}
				if (addressConstraint) {
					this.custEmpAddrStreet.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_ADDRESS",
							new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpAddrStreet.value") }));
				}
				if (addressConstraint) {
					this.custEMpAddrLine1.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_ADDRESS",
							new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEMpAddrLine1.value") }));
				}
			}
		}
		
		// Preferential Details Tab
		if (!this.custDSA.isReadonly()) {
			this.custDSA.setConstraint(new SimpleConstraint(PennantConstants.ALPHANUM_REGEX,Labels.getLabel(
				"MAND_FIELD_CHARACTER",new String[] { Labels.getLabel("label_CustomerDialog_CustDSA.value") })));
		}
		if (!this.custReferedBy.isReadonly() && !this.custReferedBy.getValue().equals("")) {
			this.custReferedBy.setConstraint(new SimpleConstraint(this.CUSTCIF_REGEX,Labels.getLabel(
				"FIELD_ALLOWED_CHARS",new String[] {Labels.getLabel(
						"label_CustomerDialog_CustReferedBy.value"),parms[0], parms[1] })));
		}
		if (this.custStmtNextDate.getValue() != null) {
			this.custStmtNextDate.setConstraint("NO TODAY,NO PAST:"+ Labels.getLabel(
				"DATE_PAST_TODAY",new String[] { Labels.getLabel("label_CustomerDialog_CustStmtNextDate.value") }));
		}
		
		//Non-Financial and Financial details for Corporate Customer Details
		if(getCustomerDetails().getCustomer().getLovDescCustCtgType().equalsIgnoreCase("C")){
			if (!this.name.isReadonly()){
				this.name.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,Labels.getLabel(
						"MAND_FIELD_CHARACTER",new String[] { Labels.getLabel("label_CorporateCustomerDetailDialog_Name.value") })));
			}	
			if (!this.phoneNumber.isReadonly()){
				this.phoneNumber.setConstraint(new SimpleConstraint(PennantConstants.PH_REGEX,Labels.getLabel(
						"MAND_NUMBER",new String[] { Labels.getLabel("label_CorporateCustomerDetailDialog_PhoneNumber.value") })));
			}	
			if (!this.phoneNumber1.isReadonly()){
				if(!StringUtils.trimToEmpty(this.phoneNumber1.getValue()).equals("")){
					this.phoneNumber1.setConstraint(new SimpleConstraint(PennantConstants.PH_REGEX,Labels.getLabel(
							"MAND_NUMBER",new String[] { Labels.getLabel("label_CorporateCustomerDetailDialog_PhoneNumber1.value") })));
				}
			}	
			if (!this.emailId.isReadonly()){
				this.emailId.setConstraint(new SimpleConstraint(PennantConstants.MAIL_REGEX,Labels.getLabel("MAND_FIELD_MAIL",
						new String[] { Labels.getLabel("label_CorporateCustomerDetailDialog_EmailId.value") })));
			}	
			if (!this.bussCommenceDate.isDisabled()){
				this.bussCommenceDate.setConstraint("NO EMPTY,NO TODAY,NO FUTURE:"+ Labels.getLabel("DATE_EMPTY_FUTURE_TODAY",
						new String[] { Labels.getLabel("label_CorporateCustomerDetailDialog_BussCommenceDate.value") }));
			}
			if (!this.servCommenceDate.isDisabled()){
				this.servCommenceDate.setConstraint("NO EMPTY,NO TODAY,NO FUTURE:" + Labels.getLabel("DATE_EMPTY_FUTURE_TODAY",
						new String[]{Labels.getLabel("label_CorporateCustomerDetailDialog_ServCommenceDate.value")}));
			}
			if (!this.bankRelationshipDate.isDisabled()){
				if(this.bankRelationshipDate.getValue() != null){
					this.bankRelationshipDate.setConstraint("NO EMPTY,NO TODAY,NO FUTURE:" + Labels.getLabel("DATE_EMPTY_FUTURE_TODAY",
							new String[]{Labels.getLabel("label_CorporateCustomerDetailDialog_BankRelationshipDate.value")}));
				}
			}
		}
	}

	/**
	 * Method of validation in Saving Mode
	 */
	private void doQDEValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		
		//Basic Details Tab-->1.Key Details
		if (!this.custCIF.isReadonly()) {
			this.custCIF.setConstraint(new SimpleConstraint(this.CUSTCIF_REGEX,Labels.getLabel(
				"MAND_FIELD_ALLOWED_CHARS",new String[] {Labels.getLabel(
						"label_CustomerDialog_CustCIF.value"),parms[0], parms[1] })));
		}
		
		if (!this.custCoreBank.isReadonly()) {
			this.custCoreBank.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_CustomerDialog_CustCoreBank.value") }));
		}
		
		//Basic Details Tab-->2.Personal Details(Retail Customer)
		if (this.gb_personalDetails.isVisible()) {
			if (!this.custFName.isReadonly()) {
				this.custFName.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
					Labels.getLabel("MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustFName.value") })));
			}
			if (!this.custLName.isReadonly()) {
				this.custLName.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
					Labels.getLabel("MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustLName.value") })));
			}
			if (!this.custDOB.isReadonly()) {
				this.custDOB.setConstraint("NO EMPTY,NO TODAY,NO FUTURE:"+ Labels.getLabel(
					"DATE_EMPTY_FUTURE_TODAY",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustDOB.value") }));
			}
		} else if (this.gb_corporateCustomerPersonalDetails.isVisible()) {
			if (!this.corpCustOrgName.isReadonly()) {
				this.corpCustOrgName.setConstraint(new SimpleConstraint(PennantConstants.NAME_REGEX,
					Labels.getLabel("MAND_FIELD_CHARACTER",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustLName.value") })));
			}
		}
		if(getCustomerDetails().getCustomer().getLovDescCustCtgType().equals("I")){
			if(this.row_retailPPT.isVisible() && this.row_retailVisa.isVisible()){
				if (!this.custPassportNo.isReadonly()) {
					this.custPassportNo.setConstraint(new SimpleConstraint(PennantConstants.VISA_REGEX,
							Labels.getLabel("MAND_FIELD_CHAR_NUMBER",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustPassportNo.value") })));
				}
				if (!this.custPassportExpiry.isReadonly() && !this.custPassportNo.getValue().equals("")) {
					this.custPassportExpiry.setConstraint("NO EMPTY,NO TODAY,NO PAST:"+ Labels.getLabel(
							"DATE_EMPTY_PAST_TODAY",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustPassportExpiry.value") }));
				}			
			}
		}else if(getCustomerDetails().getCustomer().getLovDescCustCtgType().equals("C")){
			if(this.row_corpTL.isVisible() && this.row_corpTLED.isVisible()){
				if (!this.custTradeLicenceNum.isReadonly()) {
					this.custTradeLicenceNum.setConstraint(new SimpleConstraint(PennantConstants.TRADE_LICENSE_REGEX,
							Labels.getLabel("MAND_FIELD_CHAR_NUMBER",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustTradeLicenceNum.value") })));
				}
				if(this.custTradeLicenceNum.getConstraint() == null){
					if (!this.custTradeLicenceExpiry.isReadonly() && !this.custTradeLicenceNum.getValue().equals("")) {
						this.custTradeLicenceExpiry.setConstraint("NO EMPTY,NO TODAY,NO PAST:"+ Labels.getLabel(
								"DATE_EMPTY_PAST_TODAY",new String[] { Labels.getLabel(
								"label_CustomerDialog_CustTradeLicenceExpiry.value") }));
					}
				}
				if (!this.custDateOfIncorporation.isReadonly()) {
					this.custDateOfIncorporation.setConstraint("NO EMPTY,NO TODAY,NO FUTURE:"+ Labels.getLabel(
							"DATE_EMPTY_FUTURE_TODAY",new String[] { Labels.getLabel(
							"label_CustomerDialog_CustDateOfIncorporation.value") }));
				}
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
		
		this.custID.setConstraint("");
		this.custCIF.setConstraint("");
		this.custCoreBank.setConstraint("");
		this.custFName.setConstraint("");
		this.custMName.setConstraint("");
		this.custLName.setConstraint("");
		this.custShrtName.setConstraint("");
		this.custFNameLclLng.setConstraint("");
		this.custMNameLclLng.setConstraint("");
		this.custLNameLclLng.setConstraint("");
		this.custShrtNameLclLng.setConstraint("");
		this.custDOB.setConstraint("");
		this.custPOB.setConstraint("");
		this.custPassportNo.setConstraint("");
		this.custMotherMaiden.setConstraint("");
		this.custReferedBy.setConstraint("");
		this.custDSA.setConstraint("");
		this.custGroupID.setConstraint("");
		this.custStsChgDate.setConstraint("");
		this.custInactiveReason.setConstraint("");
		this.custStaffID.setConstraint("");
		this.custTradeLicenceNum.setConstraint("");
		this.custTradeLicenceExpiry.setConstraint("");
		this.custDateOfIncorporation.setConstraint("");
		this.custPassportExpiry.setConstraint("");
		this.custVisaNum.setConstraint("");
		this.custVisaExpiry.setConstraint("");
		this.custSubSegment.setConstraint("");
		this.custBLRsnCode.setConstraint("");
		this.custRejectedRsn.setConstraint("");
		this.custLng.setConstraint("");
		this.custClosedOn.setConstraint("");
		this.custStmtFrq.setConstraint("");
		this.custStmtLastDate.setConstraint("");
		this.custStmtNextDate.setConstraint("");
		this.custFirstBusinessDate.setConstraint("");

		// Customer Employee Details
		this.custEmpName.setConstraint("");
		this.custEmpFrom.setConstraint("");
		this.custEmpID.setConstraint("");
		this.custEmpHNbr.setConstraint("");
		this.custEmpFlatNbr.setConstraint("");
		this.custEmpAddrStreet.setConstraint("");
		this.custEMpAddrLine1.setConstraint("");
		this.custEMpAddrLine2.setConstraint("");
		this.custEmpPOBox.setConstraint("");
		this.custEmpAddrPhone.setConstraint("");
		this.custEmpAddrZIP.setConstraint("");

		// corporate customer
		this.corpCustOrgName.setConstraint("");
		this.corpCustShrtName.setConstraint("");
		this.corpCustPOB.setConstraint("");
	    
	    //Non-Financial and Financial details for Corporate Customer Details
	    this.name.setConstraint("");
		this.phoneNumber.setConstraint("");
		this.phoneNumber1.setConstraint("");
		this.emailId.setConstraint("");
		this.bussCommenceDate.setConstraint("");
		this.servCommenceDate.setConstraint("");
		this.bankRelationshipDate.setConstraint("");
		
		logger.debug("Leaving");
	}

	/**
	 * Removes the Validation by setting the accordingly constraints to the
	 * LOVfields.
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		
		this.lovDescCustTypeCodeName.setConstraint("");
		this.lovDescCustDftBranchName.setConstraint("");
		this.lovDescCustBaseCcyName.setConstraint("");
		this.lovDescCustGenderCodeName.setConstraint("");
		this.lovDescCustSalutationCodeName.setConstraint("");
		this.lovDescCustCOBName.setConstraint("");
		this.lovDescCorpCustCOBName.setConstraint("");
		this.lovDescCustMaritalStsName.setConstraint("");
		this.lovDescCustProfessionName.setConstraint("");
		this.lovDescCustEmpStsName.setConstraint("");
		this.lovDescCustCtgCodeName.setConstraint("");
		this.lovDescCustIndustryName.setConstraint("");
		this.lovDescCustSectorName.setConstraint("");
		this.lovDescCustSubSectorName.setConstraint("");
		this.lovDescCustSegmentName.setConstraint("");
		this.lovDescCustResdCountryName.setConstraint("");
		this.lovDescCustNationalityName.setConstraint("");
		this.lovDescCustDSADeptName.setConstraint("");
		this.lovDescCustRO1Name.setConstraint("");
		this.lovDescCustBLRsnCodeName.setConstraint("");
		this.lovDescCustRejectedRsnName.setConstraint("");
		this.lovDesccustGroupIDName.setConstraint("");
		this.lovDescCustSubSegmentName.setConstraint("");
		this.lovDescCustLngName.setConstraint("");
		this.lovDescCustEmpDesgName.setConstraint("");
		this.lovDescCustEmpDeptName.setConstraint("");
		this.lovDescCustEmpTypeName.setConstraint("");
		this.lovDescCustEmpAddrCityName.setConstraint("");
		this.lovDescCustEmpAddrProvinceName.setConstraint("");
		this.lovDescCustEmpAddrCountryName.setConstraint("");
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
		this.custFName.setErrorMessage("");
		this.custMName.setErrorMessage("");
		this.custLName.setErrorMessage("");
		this.custShrtName.setErrorMessage("");
		this.custFNameLclLng.setErrorMessage("");
		this.custMNameLclLng.setErrorMessage("");
		this.custLNameLclLng.setErrorMessage("");
		this.custShrtNameLclLng.setErrorMessage("");
		this.custDftBranch.setErrorMessage("");
		this.custGenderCode.setErrorMessage("");
		this.custDOB.setErrorMessage("");
		this.custPOB.setErrorMessage("");
		this.custCOB.setErrorMessage("");
		this.custPassportNo.setErrorMessage("");
		this.custMotherMaiden.setErrorMessage("");
		this.custReferedBy.setErrorMessage("");
		this.custDSA.setErrorMessage("");
		this.custDSADept.setErrorMessage("");
		this.custRO1.setErrorMessage("");
		this.custRO2.setErrorMessage("");
		this.custSts.setErrorMessage("");
		this.custGroupSts.setErrorMessage("");
		this.custTradeLicenceNum.setErrorMessage("");
		this.custPassportExpiry.setErrorMessage("");
		this.custVisaExpiry.setErrorMessage("");
		this.custTradeLicenceExpiry.setErrorMessage("");
		this.custDateOfIncorporation.setErrorMessage("");
		this.custClosedOn.setErrorMessage("");
		this.custVisaNum.setErrorMessage("");
		this.custStaffID.setErrorMessage("");
		this.custIndustry.setErrorMessage("");
		this.custSector.setErrorMessage("");
		this.custSubSector.setErrorMessage("");
		this.custProfession.setErrorMessage("");
		this.custTotalIncome.setErrorMessage("");
		this.custMaritalSts.setErrorMessage("");
		this.custEmpSts.setErrorMessage("");
		this.custSegment.setErrorMessage("");
		this.custSubSegment.setErrorMessage("");
		this.custBLRsnCode.setErrorMessage("");
		this.custRejectedRsn.setErrorMessage("");
		this.custBaseCcy.setErrorMessage("");
		this.custLng.setErrorMessage("");
		this.custParentCountry.setErrorMessage("");
		this.custResdCountry.setErrorMessage("");
		this.custRiskCountry.setErrorMessage("");
		this.custNationality.setErrorMessage("");
		this.custStmtFrq.setErrorMessage("");
		this.custStmtNextDate.setErrorMessage("");
		this.custStmtLastDate.setErrorMessage("");
		this.custStmtDispatchMode.setErrorMessage("");
		this.custFirstBusinessDate.setErrorMessage("");
		
		//Employee Details
		this.custEmpName.setErrorMessage("");
		this.custEmpFrom.setErrorMessage("");
		this.custEmpID.setErrorMessage("");
		this.custEmpHNbr.setErrorMessage("");
		this.custEmpFlatNbr.setErrorMessage("");
		this.custEmpAddrStreet.setErrorMessage("");
		this.custEMpAddrLine1.setErrorMessage("");
		this.custEMpAddrLine2.setErrorMessage("");
		this.custEmpPOBox.setErrorMessage("");
		this.custEmpAddrPhone.setErrorMessage("");
		this.custEmpAddrZIP.setErrorMessage("");
		this.corpCustShrtName.setErrorMessage("");
		this.corpCustPOB.setErrorMessage("");
		this.corpCustCOB.setErrorMessage("");
		this.corpCustOrgNameLclLng.setErrorMessage("");
		this.corpCustShrtNameLclLng.setErrorMessage("");
		
		//Non-Financial and Financial details for Corporate Customer Details
		this.name.setErrorMessage("");
		this.phoneNumber.setErrorMessage("");
		this.phoneNumber1.setErrorMessage("");
		this.emailId.setErrorMessage("");
		this.bussCommenceDate.setErrorMessage("");
		this.servCommenceDate.setErrorMessage("");
		this.bankRelationshipDate.setErrorMessage("");
		
		//Clear Error messages to LOV fields 
		this.lovDescCustTypeCodeName.setErrorMessage("");
		this.lovDescCustDftBranchName.setErrorMessage("");
		this.lovDescCustBaseCcyName.setErrorMessage("");
		this.lovDescCustGenderCodeName.setErrorMessage("");
		this.lovDescCustSalutationCodeName.setErrorMessage("");
		this.lovDescCustCOBName.setErrorMessage("");
		this.lovDescCorpCustCOBName.setErrorMessage("");
		this.lovDescCustMaritalStsName.setErrorMessage("");
		this.lovDescCustProfessionName.setErrorMessage("");
		this.lovDescCustEmpStsName.setErrorMessage("");
		this.lovDescCustCtgCodeName.setErrorMessage("");
		this.lovDescCustIndustryName.setErrorMessage("");
		this.lovDescCustSectorName.setErrorMessage("");
		this.lovDescCustSubSectorName.setErrorMessage("");
		this.lovDescCustSegmentName.setErrorMessage("");
		this.lovDescCustResdCountryName.setErrorMessage("");
		this.lovDescCustNationalityName.setErrorMessage("");
		this.lovDescCustDSADeptName.setErrorMessage("");
		this.lovDescCustRO1Name.setErrorMessage("");
		this.lovDescCustBLRsnCodeName.setErrorMessage("");
		this.lovDescCustRejectedRsnName.setErrorMessage("");
		this.lovDesccustGroupIDName.setErrorMessage("");
		this.lovDescCustSubSegmentName.setErrorMessage("");
		this.lovDescCustLngName.setErrorMessage("");
		this.lovDescCustEmpDesgName.setErrorMessage("");
		this.lovDescCustEmpDeptName.setErrorMessage("");
		this.lovDescCustEmpTypeName.setErrorMessage("");
		this.lovDescCustEmpAddrCityName.setErrorMessage("");
		this.lovDescCustEmpAddrProvinceName.setErrorMessage("");
		this.lovDescCustEmpAddrCountryName.setErrorMessage("");
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
		CustomerEmploymentDetail customerEmploymentDetail = null;
		CorporateCustomerDetail corporateCustomerDetail = null;
		if("C".equalsIgnoreCase(aCustomer.getLovDescCustCtgType())){
			corporateCustomerDetail = customerDetails.getCorporateCustomerDetail();
		}else if("I".equalsIgnoreCase(aCustomer.getLovDescCustCtgType())){
			customerEmploymentDetail = customerDetails.getCustomerEmploymentDetail();
		}

		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> " + aCustomer.getCustID();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomer.getRecordType()).equals("")) {
				aCustomer.setVersion(aCustomer.getVersion() + 1);
				aCustomer.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				customerEmploymentDetail.setVersion(customerEmploymentDetail.getVersion() + 1);
				customerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if("C".equalsIgnoreCase(aCustomer.getLovDescCustCtgType())){
					corporateCustomerDetail.setVersion(corporateCustomerDetail.getVersion() + 1);
					corporateCustomerDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				}
				if (isWorkFlowEnabled()) {
					aCustomer.setNewRecord(true);
					customerEmploymentDetail.setNewRecord(true);
					if("C".equalsIgnoreCase(aCustomer.getLovDescCustCtgType())){
						corporateCustomerDetail.setNewRecord(true);
					}
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				aCustomerDetails.setCustomer(aCustomer);
				aCustomerDetails.setCustomerEmploymentDetail(customerEmploymentDetail);
				if("C".equalsIgnoreCase(aCustomer.getLovDescCustCtgType())){
					aCustomerDetails.setCorporateCustomerDetail(corporateCustomerDetail);
				}
				if (doProcess(aCustomerDetails, tranType)) {
					refreshList();
					closeDialog(this.window_CustomerDialog, "Customer");
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
		if (customerDetails.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}
		if (SystemParameterDetails.getSystemParameterValue("CB_CID").equals("CIF") || 
				SystemParameterDetails.getSystemParameterValue("CBI_Active").equals("N")) {
			this.custCoreBank.setReadonly(true);
		} else {
			this.custCoreBank.setReadonly(isReadOnly("CustomerDialog_custCoreBank"));
		}

		if("C".equalsIgnoreCase(getCustomerDetails().getCustomer().getLovDescCustCtgType())){
			this.nonFinancialDetails.setVisible(true);
			this.financialDetails.setVisible(true);
			
			this.gb_personalDetails.setVisible(false);
			this.gb_corporateCustomerPersonalDetails.setVisible(true);
			this.row_retailPPT.setVisible(false);
			this.row_retailVisa.setVisible(false);
			this.row_EmpSts.setVisible(false);
			this.row_custStaff.setVisible(false);
			this.row_corpTL.setVisible(true);
			this.row_corpTLED.setVisible(true);
			this.employmentDetails.setVisible(false);
			this.gb_employerDetails.setVisible(false);
			this.gb_familyDetails.setVisible(false);
			this.gb_incomeDetails.setVisible(false);
			
		}else if("I".equalsIgnoreCase(getCustomerDetails().getCustomer().getLovDescCustCtgType())){
			
				this.gb_personalDetails.setVisible(true);
				this.gb_corporateCustomerPersonalDetails.setVisible(false);
				this.row_retailPPT.setVisible(true);
				this.row_retailVisa.setVisible(true);
				this.row_EmpSts.setVisible(true);
				this.row_custStaff.setVisible(true);
				this.row_corpTL.setVisible(false);
				this.row_corpTLED.setVisible(false);
		}
		// Condition for not allow to change in maintain State
		if (StringUtils.trimToEmpty(getCustomerDetails().getCustomer().getRecordType())
				.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {

			this.btnSearchCustTypeCode.setDisabled(isReadOnly("CustomerDialog_custTypeCode"));
			this.btnSearchCustDftBranch.setDisabled(isReadOnly("CustomerDialog_custDftBranch"));
			this.btnSearchCustBaseCcy.setDisabled(isReadOnly("CustomerDialog_custBaseCcy"));
			this.btnSearchcustGroupID.setDisabled(isReadOnly("CustomerDialog_custGroupID"));
			this.btnSearchCustGenderCode.setDisabled(isReadOnly("CustomerDialog_custGenderCode"));
			this.custStsChgDate.setDisabled(isReadOnly("CustomerDialog_custStsChgDate"));
			this.custIsMinor.setDisabled(isReadOnly("CustomerDialog_custIsMinor"));
		} else {
			this.btnSearchCustTypeCode.setDisabled(false);
			this.btnSearchCustDftBranch.setDisabled(false);
			this.btnSearchCustBaseCcy.setDisabled(false);
			this.btnSearchcustGroupID.setDisabled(false);
			this.btnSearchCustGenderCode.setDisabled(false);
			this.custStsChgDate.setDisabled(false);
			this.custIsMinor.setDisabled(false);
		}
		
		//this.custCIF.setReadonly(isReadOnly("CustomerDialog_custCIF"));//TODO--System generation
		this.custCIF.setReadonly(true);
		this.customerCIF.setReadonly(true);
		this.btnSearchCustCtgCode.setDisabled(isReadOnly("CustomerDialog_custCtgCode"));		
		this.btnSearchCustSalutationCode.setDisabled(isReadOnly("CustomerDialog_custSalutationCode"));
		this.custFName.setReadonly(isReadOnly("CustomerDialog_custFName"));
		this.custMName.setReadonly(isReadOnly("CustomerDialog_custMName"));
		this.custLName.setReadonly(isReadOnly("CustomerDialog_custLName"));
		this.custShrtName.setReadonly(isReadOnly("CustomerDialog_custShrtName"));
		this.custPOB.setReadonly(isReadOnly("CustomerDialog_custPOB"));
		this.btnSearchCustCOB.setDisabled(isReadOnly("CustomerDialog_custCOB"));
		this.corpCustOrgName.setReadonly(isReadOnly("CustomerDialog_custLName"));
		this.corpCustShrtName.setReadonly(isReadOnly("CustomerDialog_custShrtName"));
		this.corpCustPOB.setReadonly(isReadOnly("CustomerDialog_custPOB"));
		this.btnSearchCorpCustCOB.setDisabled(isReadOnly("CustomerDialog_custCOB"));
		this.custFNameLclLng.setReadonly(isReadOnly("CustomerDialog_custFNameLclLng"));
		this.custMNameLclLng.setReadonly(isReadOnly("CustomerDialog_custMNameLclLng"));
		this.custLNameLclLng.setReadonly(isReadOnly("CustomerDialog_custLNameLclLng"));
		this.custShrtNameLclLng.setReadonly(isReadOnly("CustomerDialog_custShrtNameLclLng"));
		this.custDOB.setDisabled(isReadOnly("CustomerDialog_custDOB"));
		this.custDateOfIncorporation.setDisabled(isReadOnly("CustomerDialog_custDOB"));
		this.custPOB.setReadonly(isReadOnly("CustomerDialog_custPOB"));
		this.btnSearchCustCOB.setDisabled(isReadOnly("CustomerDialog_custCOB"));
		this.custPassportNo.setReadonly(isReadOnly("CustomerDialog_custPassportNo"));
		this.custMotherMaiden.setReadonly(isReadOnly("CustomerDialog_custMotherMaiden"));
		this.custTradeLicenceNum.setReadonly(isReadOnly("CustomerDialog_custTradeLicenceNum"));
		this.custTradeLicenceExpiry.setDisabled(isReadOnly("CustomerDialog_custTradeLicenceExpiry"));
		this.custReferedBy.setReadonly(isReadOnly("CustomerDialog_custReferedBy"));
		this.custDSA.setReadonly(isReadOnly("CustomerDialog_custDSA"));
		this.btnSearchCustDSADept.setDisabled(isReadOnly("CustomerDialog_custDSADept"));
		this.btnSearchCustRO1.setDisabled(isReadOnly("CustomerDialog_custRO1"));
		this.btnSearchCustRO2.setDisabled(isReadOnly("CustomerDialog_custRO2"));
		this.btnSearchCustSts.setDisabled(isReadOnly("CustomerDialog_custSts"));
		this.btnSearchCustGroupSts.setDisabled(isReadOnly("CustomerDialog_custGroupSts"));
		this.custIsBlocked.setDisabled(isReadOnly("CustomerDialog_custIsBlocked"));
		this.custIsActive.setDisabled(isReadOnly("CustomerDialog_custIsActive"));
		this.custIsClosed.setDisabled(isReadOnly("CustomerDialog_custIsClosed"));
		this.custInactiveReason.setReadonly(isReadOnly("CustomerDialog_custInactiveReason"));
		this.custIsDecease.setDisabled(isReadOnly("CustomerDialog_custIsDecease"));
		this.custIsDormant.setDisabled(isReadOnly("CustomerDialog_custIsDormant"));
		this.custIsDelinquent.setDisabled(isReadOnly("CustomerDialog_custIsDelinquent"));
		this.custVisaNum.setDisabled(isReadOnly("CustomerDialog_custVisaNum"));
		this.custIsStaff.setDisabled(isReadOnly("CustomerDialog_custIsStaff"));
		this.btnSearchCustIndustry.setDisabled(isReadOnly("CustomerDialog_custIndustry"));
		this.btnSearchCustSector.setDisabled(isReadOnly("CustomerDialog_custSector"));
		this.btnSearchCustSubSector.setDisabled(isReadOnly("CustomerDialog_custSubSector"));
		this.btnSearchCustProfession.setDisabled(isReadOnly("CustomerDialog_custProfession"));
		this.custTotalIncome.setReadonly(isReadOnly("CustomerDialog_custTotalIncome"));
		this.btnSearchCustMaritalSts.setDisabled(isReadOnly("CustomerDialog_custMaritalSts"));
		this.btnSearchCustEmpSts.setDisabled(isReadOnly("CustomerDialog_custEmpSts"));
		this.btnSearchCustSegment.setDisabled(isReadOnly("CustomerDialog_custSegment"));
		this.btnSearchCustSubSegment.setDisabled(isReadOnly("CustomerDialog_custSubSegment"));
		this.custSubSegment.setReadonly(isReadOnly("CustomerDialog_custSubSegment"));
		this.custIsBlackListed.setDisabled(isReadOnly("CustomerDialog_custIsBlackListed"));
		this.custIsRejected.setDisabled(isReadOnly("CustomerDialog_custIsRejected"));
		this.btnSearchCustLng.setDisabled(isReadOnly("CustomerDialog_custLng"));
		this.btnSearchCustResdCountry.setDisabled(isReadOnly("CustomerDialog_custResdCountry"));
		this.btnSearchCustNationality.setDisabled(isReadOnly("CustomerDialog_custNationality"));
		this.custClosedOn.setDisabled(isReadOnly("CustomerDialog_custClosedOn"));
		this.custStmtFrqCode.setDisabled(isReadOnly("CustomerDialog_custStmtFrq"));
		this.custStmtFrqMth.setDisabled(isReadOnly("CustomerDialog_custStmtFrq"));
		this.custStmtFrqDays.setDisabled(isReadOnly("CustomerDialog_custStmtFrqDay"));
		this.custIsStmtCombined.setDisabled(isReadOnly("CustomerDialog_custIsStmtCombined"));
		this.custStmtLastDate.setDisabled(isReadOnly("CustomerDialog_custStmtLastDate"));
		this.custStmtNextDate.setDisabled(isReadOnly("CustomerDialog_custStmtNextDate"));
		this.custStmtDispatchMode.setReadonly(isReadOnly("CustomerDialog_custStmtDispatchMode"));
		this.btnSearchCustStmtDispatchMode.setDisabled(isReadOnly("CustomerDialog_custStmtDispatchMode"));
		this.custFirstBusinessDate.setDisabled(isReadOnly("CustomerDialog_custFirstBusinessDate"));

		this.custEmpName.setReadonly(isReadOnly("CustomerDialog_custEmpName"));
		this.custEmpFrom.setDisabled(isReadOnly("CustomerDialog_custEmpFrom"));
		this.btnSearchCustEmpDesg.setDisabled(isReadOnly("CustomerDialog_custEmpDesg"));
		this.btnSearchCustEmpDept.setDisabled(isReadOnly("CustomerDialog_custEmpDept"));
		this.custEmpID.setReadonly(isReadOnly("CustomerDialog_custEmpID"));
		this.btnSearchCustEmpType.setDisabled(isReadOnly("CustomerDialog_custEmpType"));
		this.custEmpHNbr.setReadonly(isReadOnly("CustomerDialog_custEmpHNbr"));
		this.custEmpFlatNbr.setReadonly(isReadOnly("CustomerDialog_custEmpFlatNbr"));
		this.custEmpAddrStreet.setReadonly(isReadOnly("CustomerDialog_custEmpAddrStreet"));
		this.custEMpAddrLine1.setReadonly(isReadOnly("CustomerDialog_custEmpAddrLine1"));
		this.custEMpAddrLine2.setReadonly(isReadOnly("CustomerDialog_custEmpAddrLine2"));
		this.custEmpPOBox.setReadonly(isReadOnly("CustomerDialog_custEmpPOBox"));
		this.custEmpAddrPhone.setReadonly(isReadOnly("CustomerDialog_custEmpAddrPhone"));
		this.custEmpAddrZIP.setReadonly(isReadOnly("CustomerDialog_custEmpAddrZIP"));
		this.btnSearchCustEmpAddrCountry.setDisabled(isReadOnly("CustomerDialog_custEmpAddrCountry"));
		this.btnSearchCustEmpAddrProvince.setDisabled(isReadOnly("CustomerDialog_custEmpAddrProvince"));
		this.btnSearchCustEmpAddrCity.setDisabled(isReadOnly("CustomerDialog_custEmpAddrCity"));

		//Corporate Customer Details for Non-Financial and Financial Details
		this.name.setReadonly(isReadOnly("CustomerDialog_name"));
		this.phoneNumber.setReadonly(isReadOnly("CustomerDialog_phoneNumber"));
		this.phoneNumber1.setReadonly(isReadOnly("CustomerDialog_phoneNumber1"));
		this.emailId.setReadonly(isReadOnly("CustomerDialog_emailId"));
	 	this.bussCommenceDate.setDisabled(isReadOnly("CustomerDialog_bussCommenceDate"));
	 	this.servCommenceDate.setDisabled(isReadOnly("CustomerDialog_servCommenceDate"));
	 	this.bankRelationshipDate.setDisabled(isReadOnly("CustomerDialog_bankRelationshipDate"));
		this.paidUpCapital.setReadonly(isReadOnly("CustomerDialog_paidUpCapital"));
		this.authorizedCapital.setReadonly(isReadOnly("CustomerDialog_authorizedCapital"));
		this.reservesAndSurPlus.setReadonly(isReadOnly("CustomerDialog_reservesAndSurPlus"));
		this.intangibleAssets.setReadonly(isReadOnly("CustomerDialog_intangibleAssets"));
		this.tangibleNetWorth.setReadonly(isReadOnly("CustomerDialog_tangibleNetWorth"));
		this.longTermLiabilities.setReadonly(isReadOnly("CustomerDialog_longTermLiabilities"));
		this.capitalEmployed.setReadonly(isReadOnly("CustomerDialog_capitalEmployed"));
		this.investments.setReadonly(isReadOnly("CustomerDialog_investments"));
		this.nonCurrentAssets.setReadonly(isReadOnly("CustomerDialog_nonCurrentAssets"));
		this.netWorkingCapital.setReadonly(isReadOnly("CustomerDialog_netWorkingCapital"));
		this.netSales.setReadonly(isReadOnly("CustomerDialog_netSales"));
		this.otherIncome.setReadonly(isReadOnly("CustomerDialog_otherIncome"));
		this.netProfitAfterTax.setReadonly(isReadOnly("CustomerDialog_netProfitAfterTax"));
		this.depreciation.setReadonly(isReadOnly("CustomerDialog_depreciation"));
		this.cashAccurals.setReadonly(isReadOnly("CustomerDialog_cashAccurals"));
		this.annualTurnover.setReadonly(isReadOnly("CustomerDialog_annualTurnover"));
		this.returnOnCapitalEmp.setReadonly(isReadOnly("CustomerDialog_returnOnCapitalEmp"));
		this.currentAssets.setReadonly(isReadOnly("CustomerDialog_currentAssets"));
		this.currentLiabilities.setReadonly(isReadOnly("CustomerDialog_currentLiabilities"));
		this.currentBookValue.setReadonly(isReadOnly("CustomerDialog_currentBookValue"));
		this.currentMarketValue.setReadonly(isReadOnly("CustomerDialog_currentMarketValue"));
		this.promotersShare.setReadonly(isReadOnly("CustomerDialog_promotersShare"));
		this.associatesShare.setReadonly(isReadOnly("CustomerDialog_associatesShare"));
		this.publicShare.setReadonly(isReadOnly("CustomerDialog_publicShare"));
		this.finInstShare.setReadonly(isReadOnly("CustomerDialog_finInstShare"));
		this.others.setReadonly(isReadOnly("CustomerDialog_others"));
		
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerDetails.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
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
		this.btnSearchCustCtgCode.setDisabled(true);
		this.btnSearchCustTypeCode.setDisabled(true);
		this.btnSearchCustSalutationCode.setDisabled(true);
		this.custFName.setReadonly(true);
		this.custMName.setReadonly(true);
		this.custLName.setReadonly(true);
		this.custShrtName.setReadonly(true);

		// corporate customer fields
		this.corpCustOrgName.setReadonly(true);
		this.corpCustShrtName.setReadonly(true);
		this.corpCustPOB.setReadonly(true);
		this.btnSearchCorpCustCOB.setDisabled(true);
		// corporate customer fields ends

		this.custFNameLclLng.setReadonly(true);
		this.custMNameLclLng.setReadonly(true);
		this.custLNameLclLng.setReadonly(true);
		this.custShrtNameLclLng.setReadonly(true);
		this.btnSearchCustDftBranch.setDisabled(true);
		this.btnSearchCustGenderCode.setDisabled(true);
		this.custDOB.setDisabled(true);
		this.custPOB.setReadonly(true);
		this.btnSearchCustCOB.setDisabled(true);
		this.custPassportNo.setReadonly(true);
		this.custMotherMaiden.setReadonly(true);
		this.custIsMinor.setDisabled(true);
		this.custReferedBy.setReadonly(true);
		this.custDSA.setReadonly(true);
		this.btnSearchCustDSADept.setDisabled(true);
		this.btnSearchCustRO1.setDisabled(true);
		this.btnSearchCustRO2.setDisabled(true);
		this.custGroupID.setReadonly(true);
		this.btnSearchcustGroupID.setDisabled(true);
		this.btnSearchCustSts.setDisabled(true);
		this.custStsChgDate.setDisabled(true);
		this.btnSearchCustGroupSts.setDisabled(true);
		this.custIsBlocked.setDisabled(true);
		this.custIsActive.setDisabled(true);
		this.custIsClosed.setDisabled(true);
		this.custInactiveReason.setReadonly(true);
		this.custIsDecease.setDisabled(true);
		this.custIsDormant.setDisabled(true);
		this.custIsDelinquent.setDisabled(true);
		this.custTradeLicenceNum.setReadonly(true);
		this.custTradeLicenceExpiry.setDisabled(true);
		this.custDateOfIncorporation.setDisabled(true);
		this.custPassportExpiry.setDisabled(true);
		this.custVisaNum.setReadonly(true);
		this.custVisaExpiry.setDisabled(true);
		this.custIsStaff.setDisabled(true);
		this.custStaffID.setReadonly(true);
		this.btnSearchCustIndustry.setDisabled(true);
		this.btnSearchCustSector.setDisabled(true);
		this.btnSearchCustSubSector.setDisabled(true);
		this.btnSearchCustProfession.setDisabled(true);
		this.custTotalIncome.setReadonly(true);
		this.btnSearchCustMaritalSts.setDisabled(true);
		this.btnSearchCustEmpSts.setDisabled(true);
		this.btnSearchCustSegment.setDisabled(true);
		this.btnSearchCustSubSegment.setDisabled(true);
		this.custSubSegment.setReadonly(true);
		this.custIsBlackListed.setDisabled(true);
		this.custBLRsnCode.setReadonly(true);
		this.custIsRejected.setDisabled(true);
		this.custRejectedRsn.setReadonly(true);
		this.btnSearchCustBaseCcy.setDisabled(true);
		this.btnSearchCustLng.setDisabled(true);
		this.btnSearchCustResdCountry.setDisabled(true);
		this.btnSearchCustNationality.setDisabled(true);
		this.custClosedOn.setDisabled(true);
		this.custStmtFrqCode.setReadonly(true);
		this.custStmtFrqMth.setReadonly(true);
		this.custIsStmtCombined.setDisabled(true);
		this.custStmtLastDate.setDisabled(true);
		this.custStmtNextDate.setDisabled(true);
		this.custStmtDispatchMode.setReadonly(true);
		this.btnSearchCustStmtDispatchMode.setDisabled(true);
		this.custFirstBusinessDate.setDisabled(true);
		
		this.custEmpName.setReadonly(true);
		this.custEmpFrom.setDisabled(true);
		this.btnSearchCustEmpDept.setDisabled(true);
		this.btnSearchCustEmpDesg.setDisabled(true);
		this.custEmpID.setReadonly(true);
		this.btnSearchCustEmpType.setDisabled(true);
		this.custEmpHNbr.setReadonly(true);
		this.custEmpFlatNbr.setReadonly(true);
		this.custEmpAddrStreet.setReadonly(true);
		this.custEMpAddrLine1.setReadonly(true);
		this.custEMpAddrLine2.setReadonly(true);
		this.custEmpPOBox.setReadonly(true);
		this.custEmpAddrPhone.setReadonly(true);
		this.btnSearchCustEmpAddrCountry.setDisabled(true);
		this.btnSearchCustEmpAddrProvince.setDisabled(true);
		this.btnSearchCustEmpAddrCity.setDisabled(true);
		this.custEmpAddrZIP.setReadonly(true);
		
		//Non-Financial and Financial details for Corporate Customer Details
		this.name.setReadonly(true);
		this.phoneNumber.setReadonly(true);
		this.phoneNumber1.setReadonly(true);
		this.emailId.setReadonly(true);
		this.bussCommenceDate.setDisabled(true);
		this.servCommenceDate.setDisabled(true);
		this.bankRelationshipDate.setDisabled(true);
		this.paidUpCapital.setReadonly(true);
		this.authorizedCapital.setReadonly(true);
		this.reservesAndSurPlus.setReadonly(true);
		this.intangibleAssets.setReadonly(true);
		this.tangibleNetWorth.setReadonly(true);
		this.longTermLiabilities.setReadonly(true);
		this.capitalEmployed.setReadonly(true);
		this.investments.setReadonly(true);
		this.nonCurrentAssets.setReadonly(true);
		this.netWorkingCapital.setReadonly(true);
		this.netSales.setReadonly(true);
		this.otherIncome.setReadonly(true);
		this.netProfitAfterTax.setReadonly(true);
		this.depreciation.setReadonly(true);
		this.cashAccurals.setReadonly(true);
		this.annualTurnover.setReadonly(true);
		this.returnOnCapitalEmp.setReadonly(true);
		this.currentAssets.setReadonly(true);
		this.currentLiabilities.setReadonly(true);
		this.currentBookValue.setReadonly(true);
		this.currentMarketValue.setReadonly(true);
		this.promotersShare.setReadonly(true);
		this.associatesShare.setReadonly(true);
		this.publicShare.setReadonly(true);
		this.finInstShare.setReadonly(true);
		this.others.setReadonly(true);

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
		this.lovDescCustCtgCodeName.setValue("");
		this.custTypeCode.setValue("");
		this.lovDescCustTypeCodeName.setValue("");
		this.custSalutationCode.setValue("");
		this.lovDescCustSalutationCodeName.setValue("");
		this.custFName.setValue("");
		this.custMName.setValue("");
		this.custLName.setValue("");
		this.custShrtName.setValue("");

		// corporate customer fields
		this.corpCustOrgName.setValue("");
		this.corpCustShrtName.setValue("");
		this.corpCustPOB.setValue("");
		this.corpCustCOB.setValue("");
		this.lovDescCorpCustCOBName.setValue("");
		// corporate customer fields ends

		this.custFNameLclLng.setValue("");
		this.custMNameLclLng.setValue("");
		this.custLNameLclLng.setValue("");
		this.custShrtNameLclLng.setValue("");
		this.custDftBranch.setValue("");
		this.lovDescCustDftBranchName.setValue("");
		this.custGenderCode.setValue("");
		this.lovDescCustGenderCodeName.setValue("");
		this.custDOB.setText("");
		this.custPOB.setValue("");
		this.custCOB.setValue("");
		this.lovDescCustCOBName.setValue("");
		this.custPassportNo.setValue("");
		this.custMotherMaiden.setValue("");
		this.custIsMinor.setChecked(false);
		this.custReferedBy.setValue("");
		this.custDSA.setValue("");
		this.custDSADept.setValue("");
		this.lovDescCustDSADeptName.setValue("");
		this.custRO1.setValue("");
		this.lovDescCustRO1Name.setValue("");
		this.custRO2.setValue("");
		this.lovDescCustRO2Name.setValue("");
		this.custGroupID.setValue(new Long(0));
		this.custSts.setValue("");
		this.lovDescCustStsName.setValue("");
		this.custStsChgDate.setText("");
		this.custGroupSts.setValue("");
		this.lovDescCustGroupStsName.setValue("");
		this.custIsBlocked.setChecked(false);
		this.custIsActive.setChecked(false);
		this.custIsClosed.setChecked(false);
		this.custInactiveReason.setValue("");
		this.custIsDecease.setChecked(false);
		this.custIsDormant.setChecked(false);
		this.custIsDelinquent.setChecked(false);
		this.custTradeLicenceNum.setValue("");
		this.custTradeLicenceExpiry.setText("");
		this.custDateOfIncorporation.setText("");
		this.custPassportExpiry.setText("");
		this.custVisaNum.setValue("");
		this.custVisaExpiry.setText("");
		this.custIsStaff.setChecked(false);
		this.custStaffID.setValue("");
		this.custIndustry.setValue("");
		this.lovDescCustIndustryName.setValue("");
		this.custSector.setValue("");
		this.lovDescCustSectorName.setValue("");
		this.custSubSector.setValue("");
		this.lovDescCustSubSectorName.setValue("");
		this.custProfession.setValue("");
		this.lovDescCustProfessionName.setValue("");
		this.custTotalIncome.setValue(PennantAppUtil.formateAmount(
				new BigDecimal(0),ccyFormatter));
		this.custMaritalSts.setValue("");
		this.lovDescCustMaritalStsName.setValue("");
		this.custEmpSts.setValue("");
		this.lovDescCustEmpStsName.setValue("");
		this.custSegment.setValue("");
		this.lovDescCustSegmentName.setValue("");
		this.custSubSegment.setValue("");
		this.custIsBlackListed.setChecked(false);
		this.custBLRsnCode.setValue("");
		this.custIsRejected.setChecked(false);
		this.custRejectedRsn.setValue("");
		this.custBaseCcy.setValue("");
		this.lovDescCustBaseCcyName.setValue("");
		this.custLng.setValue(SystemParameterDetails.getSystemParameterValue("APP_LNG").toString()); 
		this.lovDescCustLngName.setValue(SystemParameterDetails.getSystemParameterValue(
				"APP_LNG").toString()+ "-" + "English");
		this.custParentCountry.setValue("");
		this.custResdCountry.setValue("");
		this.lovDescCustResdCountryName.setValue("");
		this.custRiskCountry.setValue("");
		this.custNationality.setValue("");
		this.lovDescCustNationalityName.setValue("");
		this.custClosedOn.setText("");
		this.custStmtFrq.setValue("");
		clearField(custStmtFrqCode);
		clearField(custStmtFrqMth);
		this.custIsStmtCombined.setChecked(false);
		this.custStmtLastDate.setText("");
		this.custStmtNextDate.setText("");
		this.custStmtDispatchMode.setValue("");
		this.lovDescDispatchModeDescName.setValue("");
		this.custFirstBusinessDate.setText("");
		
		//Non-Financial and Financial details for Corporate Customer Details
		this.name.setValue("");
		this.phoneNumber.setValue("");
		this.phoneNumber1.setValue("");
		this.emailId.setValue("");
		this.bussCommenceDate.setText("");
		this.servCommenceDate.setText("");
		this.bankRelationshipDate.setText("");
		this.paidUpCapital.setValue("");
		this.authorizedCapital.setValue("");
		this.reservesAndSurPlus.setValue("");
		this.intangibleAssets.setValue("");
		this.tangibleNetWorth.setValue("");
		this.longTermLiabilities.setValue("");
		this.capitalEmployed.setValue("");
		this.investments.setValue("");
		this.nonCurrentAssets.setValue("");
		this.netWorkingCapital.setValue("");
		this.netSales.setValue("");
		this.otherIncome.setValue("");
		this.netProfitAfterTax.setValue("");
		this.depreciation.setValue("");
		this.cashAccurals.setValue("");
		this.annualTurnover.setValue("");
		this.returnOnCapitalEmp.setValue("");
		this.currentAssets.setValue("");
		this.currentLiabilities.setValue("");
		this.currentBookValue.setValue("");
		this.currentMarketValue.setValue("");
		this.promotersShare.setValue("");
		this.associatesShare.setValue("");
		this.publicShare.setValue("");
		this.finInstShare.setValue("");
		this.others.setValue("");
		
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
		CustomerEmploymentDetail aCustomerEmploymentDetail = null;
		CorporateCustomerDetail aCorporateCustomerDetail = null;
		if("C".equalsIgnoreCase(aCustomer.getLovDescCustCtgType())){
			aCorporateCustomerDetail = aCustomerDetails.getCorporateCustomerDetail();
		}else if("I".equalsIgnoreCase(aCustomer.getLovDescCustCtgType())){
			aCustomerEmploymentDetail = aCustomerDetails.getCustomerEmploymentDetail();
		}
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		if ("Save".equals(userAction.getSelectedItem().getLabel())) {
			isRecordSaved = true;
			doQDEValidation();
			doSetQDELOVValidation();
		} else if ("Submit".equals(userAction.getSelectedItem().getLabel())) {
			isRecordSaved = false;
			doSetValidation();
			doSetLOVValidation();
		}
		// fill the Customer object with the components data
		doWriteComponentsToBean(aCustomerDetails);
		aCustomer = aCustomerDetails.getCustomer();
		aCustomerEmploymentDetail = aCustomerDetails.getCustomerEmploymentDetail();
		aCorporateCustomerDetail = aCustomerDetails.getCorporateCustomerDetail();
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aCustomerDetails.isNewRecord();

		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCustomer.getRecordType()).equals("")) {
				aCustomer.setVersion(aCustomer.getVersion() + 1);
				aCustomerEmploymentDetail.setVersion(aCustomerEmploymentDetail.getVersion() + 1);
				if(aCorporateCustomerDetail !=null){
					aCorporateCustomerDetail.setVersion(aCorporateCustomerDetail.getVersion() + 1);
				}
				if (isNew) {
					aCustomer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					if(aCustomerEmploymentDetail != null){
						aCustomerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					}
					if(aCorporateCustomerDetail !=null){
						aCorporateCustomerDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					}
				} else {
					aCustomerDetails.setNewRecord(true);
					aCustomer.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomer.setNewRecord(true);
					if(aCustomerEmploymentDetail !=null){
						aCustomerEmploymentDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						aCustomerEmploymentDetail.setNewRecord(true);
					}
					if(aCorporateCustomerDetail !=null){
						aCorporateCustomerDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						aCorporateCustomerDetail.setNewRecord(true);
					}
				}
			}
		} else {
			aCustomer.setVersion(aCustomer.getVersion() + 1);
			if(aCustomerEmploymentDetail !=null){
				aCustomerEmploymentDetail.setVersion(aCustomerEmploymentDetail.getVersion() + 1);
			}
			if(aCorporateCustomerDetail !=null){
				aCorporateCustomerDetail.setVersion(aCorporateCustomerDetail.getVersion() + 1);
			}
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			aCustomerDetails.setCustomer(aCustomer);
			if(aCustomerEmploymentDetail !=null){
				aCustomerDetails.setCustomerEmploymentDetail(aCustomerEmploymentDetail);
			}
			if(aCorporateCustomerDetail !=null){
				aCustomerDetails.setCorporateCustomerDetail(aCorporateCustomerDetail);
			}

			if (doProcess(aCustomerDetails, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_CustomerDialog, "Customer");
			}
			logger.debug(" Calling doSave method completed Successfully");
		} catch (final DataAccessException e) {
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private String getServiceTasks(String taskId, Customer aCustomer,
			String finishedTasks) {
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
						nextRoleCode = nextRoleCode + ","+getWorkFlow().getTaskOwner(nextTasks[i]);
					}else{
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);	
					}
				}
			}else {
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

		CustomerEmploymentDetail customerEmploymentDetail = null;
		if("I".equalsIgnoreCase(aCustomer.getLovDescCustCtgType())){
			customerEmploymentDetail = aCustomerDetails.getCustomerEmploymentDetail();
		}
		if(customerEmploymentDetail != null){
			customerEmploymentDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
			customerEmploymentDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			customerEmploymentDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());
		}
		CorporateCustomerDetail corporateCustomerDetail = null;
		if("C".equalsIgnoreCase(aCustomer.getLovDescCustCtgType())){
			corporateCustomerDetail = aCustomerDetails.getCorporateCustomerDetail();
		}
		if(corporateCustomerDetail !=null){
			corporateCustomerDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
			corporateCustomerDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			corporateCustomerDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());
			aCustomerDetails.setCorporateCustomerDetail(corporateCustomerDetail);
		}
		aCustomerDetails.setCustID(aCustomer.getCustID());
		aCustomerDetails.setCustomerEmploymentDetail(customerEmploymentDetail);
		aCustomerDetails.setCustomer(aCustomer);
		aCustomerDetails.setUserDetails(getUserWorkspace().getLoginUserDetails());
		aCustomerDetails.setCorporateCustomerDetail(corporateCustomerDetail);

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCustomer.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			if(customerEmploymentDetail != null){
				//Upgraded to ZK-6.5.1.1 Added casting to String 	
				customerEmploymentDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			}
			if(corporateCustomerDetail !=null){
				//Upgraded to ZK-6.5.1.1 Added casting to String 	
				corporateCustomerDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			}

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
			String serviceTasks = getServiceTasks(taskId, aCustomer,
					finishedTasks);
			
			auditHeader = getAuditHeader(aCustomerDetails,PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {
				String method = serviceTasks.split(";")[0];

				if ("doDdeDedup".equals(method) || "doVerifierDedup".equals(method) || "doApproverDedup".equals(method) ) {
					CustomerDetails tCustomerDetails=  (CustomerDetails) auditHeader.getAuditDetail().getModelData();
					tCustomerDetails = FetchDedupDetails.getCustomerDedup(getRole(), tCustomerDetails, this.window_CustomerDialog);
					if (tCustomerDetails.getCustomer().isDedupFound()&& !tCustomerDetails.getCustomer().isSkipDedup()) {
						processCompleted = false;
					} else {
						processCompleted = true;
					}
					auditHeader.getAuditDetail().setModelData(tCustomerDetails);
					
				} else {
					CustomerDetails tCustomerDetails=  (CustomerDetails) auditHeader.getAuditDetail().getModelData();
					tCustomerDetails.setCustomer(aCustomer);
					setNextTaskDetails(taskId, tCustomerDetails.getCustomer());
					tCustomerDetails.setCustomerEmploymentDetail(customerEmploymentDetail);
					tCustomerDetails.setCorporateCustomerDetail(corporateCustomerDetail);
					auditHeader.getAuditDetail().setModelData(tCustomerDetails);
					processCompleted = doSaveProcess(auditHeader, method);
				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				serviceTasks = getServiceTasks(taskId, aCustomer, finishedTasks);
			}

			// Check whether to proceed further or not
			CustomerDetails tCustomerDetails=  (CustomerDetails) auditHeader.getAuditDetail().getModelData();
			String nextTaskId = getWorkFlow().getNextTaskIds(taskId, tCustomerDetails.getCustomer());

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in workflow
			if (processCompleted) {
				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, aCustomer);
					aCustomerDetails.setCustomerEmploymentDetail(customerEmploymentDetail);
					aCustomerDetails.setCustomer(aCustomer);
					aCustomerDetails.setCorporateCustomerDetail(corporateCustomerDetail);
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
		CustomerDetails aCustomerDetails = (CustomerDetails) auditHeader
				.getAuditDetail().getModelData();
		Customer aCustomer = aCustomerDetails.getCustomer();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getCustomerDetailsService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCustomerDetailsService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getCustomerDetailsService().doApprove(auditHeader);
						if (aCustomer.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCustomerDetailsService().doReject(auditHeader);
						if (aCustomer.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),null));
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

	public void onClick$btnSearchCustTypeCode(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "CustomerType");
		if (dataObject instanceof String) {
			this.custTypeCode.setValue(dataObject.toString());
			this.lovDescCustTypeCodeName.setValue("");
		} else {
			CustomerType details = (CustomerType) dataObject;
			if (details != null) {
				this.custTypeCode.setValue(details.getCustTypeCode());
				this.lovDescCustTypeCodeName.setValue(details.getCustTypeCode()
						+ "-" + details.getCustTypeDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustDftBranch(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "Branch");
		if (dataObject instanceof String) {
			this.custDftBranch.setValue(dataObject.toString());
			this.lovDescCustDftBranchName.setValue("");
		} else {
			Branch details = (Branch) dataObject;
			if (details != null) {
				this.custDftBranch.setValue(details.getBranchCode());
				this.lovDescCustDftBranchName.setValue(details.getBranchCode()
						+ "-" + details.getBranchDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustGenderCode(Event event) {
		logger.debug("Entering");
		String sCustGender = this.custGenderCode.getValue();
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "Gender");
		if (dataObject instanceof String) {
			this.custGenderCode.setValue(dataObject.toString());
			this.lovDescCustGenderCodeName.setValue("");
		} else {
			Gender details = (Gender) dataObject;
			if (details != null) {
				this.custGenderCode.setValue(details.getGenderCode());
				this.lovDescCustGenderCodeName.setValue(details.getGenderCode()
						+ "-" + details.getGenderDesc());
			}
		}
		if (!StringUtils.trimToEmpty(sCustGender).equals(
				this.custGenderCode.getValue())) {
			this.custSalutationCode.setValue("");
			this.lovDescCustSalutationCodeName.setValue("");
			this.btnSearchCustSalutationCode.setVisible(true);
		}
		if(this.custGenderCode.getValue().equals("")){
			this.btnSearchCustSalutationCode.setVisible(false);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustSalutationCode(Event event) {
		logger.debug("Entering");
		Filter[] filters = new Filter[1];
		
		String alternateGender = "";
		if(this.custGenderCode.getValue().equalsIgnoreCase("MALE")){
			alternateGender="FEMALE";
		}else if(this.custGenderCode.getValue().equalsIgnoreCase("FEMALE")){
			alternateGender = "MALE";
		}
		filters[0] = new Filter("SalutationGenderCode",
				alternateGender, Filter.OP_NOT_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "Salutation", filters);
		if (dataObject instanceof String) {
			this.custSalutationCode.setValue(dataObject.toString());
			this.lovDescCustSalutationCodeName.setValue("");
		} else {
			Salutation details = (Salutation) dataObject;
			if (details != null) {
				this.custSalutationCode.setValue(details.getSalutationCode());
				this.lovDescCustSalutationCodeName.setValue(details.getSalutationCode() + "-"
								+ details.getSaluationDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustCOB(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerDialog, "Country");
		if (dataObject instanceof String) {
			this.custCOB.setValue(dataObject.toString());
			this.lovDescCustCOBName.setValue("");
			this.custVisaNum.setReadonly(false);
		} else {
			Country details = (Country) dataObject;
			if (details != null) {
				this.custCOB.setValue(details.getCountryCode());
				this.lovDescCustCOBName.setValue(details.getCountryCode() + "-"
						+ details.getCountryDesc());
				
				String arr [] = SystemParameterDetails.getSystemParameterValue("NONEED_VISA_COUNTRIES").toString().split(",");
				for(int i=0;i<arr.length;i++){
					if(arr[i].equals(this.custCOB.getValue())){ //If selected country is in list of visa not needed countries make visa invisble.
						this.row_retailVisa.setVisible(false);
						this.custVisaNum.setValue("");
						this.custVisaExpiry.setText("");
						break;
					}else{
						this.row_retailVisa.setVisible(true);
						this.custVisaNum.setValue("");
						this.custVisaExpiry.setText("");
					}
				}
			}
		}
		logger.debug("Leaving");
	}

	// Corporate Customer
	public void onClick$btnSearchCorpCustCOB(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "Country");
		if (dataObject instanceof String) {
			this.corpCustCOB.setValue(dataObject.toString());
			this.lovDescCorpCustCOBName.setValue("");
		} else {
			Country details = (Country) dataObject;
			if (details != null) {
				this.corpCustCOB.setValue(details.getCountryCode());
				this.lovDescCorpCustCOBName.setValue(details.getCountryCode()
						+ "-" + details.getCountryDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustDSADept(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerDialog, "Department");
		if (dataObject instanceof String) {
			this.custDSADept.setValue(dataObject.toString());
			this.lovDescCustDSADeptName.setValue("");
		} else {
			Department details = (Department) dataObject;
			if (details != null) {
				this.custDSADept.setValue(details.getDeptCode());
				this.lovDescCustDSADeptName.setValue(details.getDeptCode()
						+ "-" + details.getDeptDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustRO1(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerDialog, "RelationshipOfficer");
		if (dataObject instanceof String) {
			this.custRO1.setValue(dataObject.toString());
			this.lovDescCustRO1Name.setValue("");
		} else {
			RelationshipOfficer details = (RelationshipOfficer) dataObject;
			if (details != null) {
				this.custRO1.setValue(details.getROfficerCode());
				this.lovDescCustRO1Name.setValue(details.getROfficerCode()
						+ "-" + details.getROfficerDesc());
				this.lovDescCustRO2Name.setErrorMessage("");
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustRO2(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerDialog, "RelationshipOfficer");
		if (dataObject instanceof String) {
			this.custRO2.setValue("");
			this.lovDescCustRO2Name.setValue("");
		} else {
			RelationshipOfficer details = (RelationshipOfficer) dataObject;
			if (details != null) {
				this.custRO2.setValue(details.getROfficerCode());
				this.lovDescCustRO2Name.setValue(details.getROfficerCode()
						+ "-" + details.getROfficerDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustSts(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerDialog, "CustomerStatusCode");
		if (dataObject instanceof String) {
			this.custSts.setValue(dataObject.toString());
			this.lovDescCustStsName.setValue("");
		} else {
			CustomerStatusCode details = (CustomerStatusCode) dataObject;
			if (details != null) {
				this.custSts.setValue(details.getCustStsCode());
				this.lovDescCustStsName.setValue(details.getCustStsCode() + "-"
						+ details.getCustStsDescription());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustGroupSts(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerDialog, "GroupStatusCode");
		if (dataObject instanceof String) {
			this.custGroupSts.setValue("");
			this.lovDescCustGroupStsName.setValue("");
		} else {
			GroupStatusCode details = (GroupStatusCode) dataObject;
			if (details != null) {
				this.custGroupSts.setValue(details.getGrpStsCode());
				this.lovDescCustGroupStsName.setValue(details.getGrpStsCode()
						+ "-" + details.getGrpStsDescription());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustIndustry(Event event) {
		logger.debug("Entering");
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("SubSectorCode", this.custSubSector.getValue(),Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerDialog, "Industry", filters);
		if (dataObject instanceof String) {
			this.custIndustry.setValue(dataObject.toString());
			this.lovDescCustIndustryName.setValue("");
		} else {
			Industry details = (Industry) dataObject;
			if (details != null) {
				this.custIndustry.setValue(details.getIndustryCode());
				this.lovDescCustIndustryName.setValue(details.getIndustryCode()
						+ "-" + details.getIndustryDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustSector(Event event) {
		logger.debug("Entering");
		String sCustSector = this.custSector.getValue();

		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerDialog, "Sector");
		if (dataObject instanceof String) {
			this.custSector.setValue(dataObject.toString());
			this.lovDescCustSectorName.setValue("");
		} else {
			Sector details = (Sector) dataObject;
			if (details != null) {
				this.custSector.setValue(details.getSectorCode());
				this.lovDescCustSectorName.setValue(details.getSectorCode()
						+ "-" + details.getSectorDesc());
			}
		}
		if (!StringUtils.trimToEmpty(sCustSector).equals(
				this.custSector.getValue())) {
			this.custSubSector.setValue("");
			this.lovDescCustSubSectorName.setValue("");
			this.custIndustry.setValue("");
			this.lovDescCustIndustryName.setValue("");
			this.btnSearchCustSubSector.setVisible(true);
			this.btnSearchCustIndustry.setVisible(false);
		}
		if(this.custSector.getValue().equals("")){
			this.btnSearchCustSubSector.setVisible(false);
			this.btnSearchCustIndustry.setVisible(false);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustSubSector(Event event) {
		logger.debug("Entering");
		String sCustSubSector = this.custSubSector.getValue();
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("SectorCode", this.custSector.getValue(), Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "SubSector", filters);
		if (dataObject instanceof String) {
			this.custSubSector.setValue(dataObject.toString());
			this.lovDescCustSubSectorName.setValue("");
		} else {
			SubSector details = (SubSector) dataObject;
			if (details != null) {
				this.custSubSector.setValue(details.getSubSectorCode());
				this.lovDescCustSubSectorName.setValue(details
						.getSubSectorCode() + "-" + details.getSubSectorDesc());
			}
		}
		if (!StringUtils.trimToEmpty(sCustSubSector).equals(this.custSubSector.getValue())) {
			this.custIndustry.setValue("");
			this.lovDescCustIndustryName.setValue("");
			this.btnSearchCustIndustry.setVisible(true);
		}
		if(this.custSubSector.getValue().equals("")){
			this.btnSearchCustIndustry.setVisible(false);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustProfession(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "Profession");
		if (dataObject instanceof String) {
			this.custProfession.setValue(dataObject.toString());
			this.lovDescCustProfessionName.setValue("");
		} else {
			Profession details = (Profession) dataObject;
			if (details != null) {
				this.custProfession.setValue(details.getProfessionCode());
				this.lovDescCustProfessionName.setValue(details.getProfessionCode()
						+ "-"
						+ details.getProfessionDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustMaritalSts(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "MaritalStatusCode");
		if (dataObject instanceof String) {
			this.custMaritalSts.setValue(dataObject.toString());
			this.lovDescCustMaritalStsName.setValue("");
		} else {
			MaritalStatusCode details = (MaritalStatusCode) dataObject;
			if (details != null) {
				this.custMaritalSts.setValue(details.getMaritalStsCode());
				this.lovDescCustMaritalStsName.setValue(details
						.getMaritalStsCode()
						+ "-"
						+ details.getMaritalStsDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustEmpSts(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "EmpStsCode");
		if (dataObject instanceof String) {
			this.custEmpSts.setValue(dataObject.toString());
			this.lovDescCustEmpStsName.setValue("");
			this.gb_employerDetails.setVisible(false);
			this.employmentDetails.setVisible(false);
		} else {
			EmpStsCode details = (EmpStsCode) dataObject;
			if (details != null) {
				this.custEmpSts.setValue(details.getEmpStsCode());
				this.lovDescCustEmpStsName.setValue(details.getEmpStsCode()
						+ "-" + details.getEmpStsDesc());
				if (getCustomerDetails().getCustomer().getLovDescCustCtgType().equals("I")
						&& this.custEmpSts.getValue().equals("EMPLOY")) {
					this.employmentDetails.setVisible(true);
					this.gb_employerDetails.setVisible(true);
				} else {
					this.employmentDetails.setVisible(false);
					this.gb_employerDetails.setVisible(false);
					this.employmentDetails.setVisible(false);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustSegment(Event event) {
		logger.debug("Entering");
		String sCustSegment = this.custSegment.getValue();

		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "Segment");
		if (dataObject instanceof String) {
			this.custSegment.setValue(dataObject.toString());
			this.lovDescCustSegmentName.setValue("");
		} else {
			Segment details = (Segment) dataObject;
			if (details != null) {
				this.custSegment.setValue(details.getSegmentCode());
				this.lovDescCustSegmentName.setValue(details.getSegmentCode()
						+ "-" + details.getSegmentDesc());
			}
		}
		if (!StringUtils.trimToEmpty(sCustSegment).equals(
				this.custSegment.getValue())) {
			this.custSubSegment.setValue("");
			this.lovDescCustSubSegmentName.setValue("");
			this.btnSearchCustSegment.setVisible(true);
		}
		if (this.custSegment.getValue() != "") {
			this.btnSearchCustSubSegment.setVisible(true);
		} else {
			this.btnSearchCustSubSegment.setVisible(false);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustSubSegment(Event event) {
		logger.debug("Entering");
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("SegmentCode", this.custSegment.getValue(),
				Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "SubSegment", filters);
		if (dataObject instanceof String) {
			this.custSubSegment.setValue(dataObject.toString());
			this.lovDescCustSubSegmentName.setValue("");
		} else {
			SubSegment details = (SubSegment) dataObject;
			if (details != null) {
				this.custSubSegment.setValue(details.getSubSegmentCode());
				this.lovDescCustSubSegmentName.setValue(details
						.getSubSegmentCode() + "-" + details.getSubSegmentDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustBaseCcy(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "Currency");
		if (dataObject instanceof String) {
			this.custBaseCcy.setValue(dataObject.toString());
			this.lovDescCustBaseCcyName.setValue("");
			ccyFormatter = 0;
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				this.custBaseCcy.setValue(details.getCcyCode());
				this.lovDescCustBaseCcyName.setValue(details.getCcyCode() + "-"
						+ details.getCcyDesc());
				ccyFormatter = details.getCcyEditField();
			}
		}
		this.custTotalIncome.setFormat(PennantAppUtil.getAmountFormate(ccyFormatter));
		doFillCustomerIncome(getIncomeList());

		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustResdCountry(Event event) {

		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "Country");
		if (dataObject instanceof String) {
			this.custResdCountry.setValue(dataObject.toString());
			this.lovDescCustResdCountryName.setValue("");
		} else {
			Country details = (Country) dataObject;
			if (details != null) {
				this.custResdCountry.setValue(details.getCountryCode());
				this.lovDescCustResdCountryName.setValue(details
						.getCountryCode() + "-" + details.getCountryDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustNationality(Event event) {

		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "NationalityCode");
		if (dataObject instanceof String) {
			this.custNationality.setValue(dataObject.toString());
			this.lovDescCustNationalityName.setValue("");
		} else {
			NationalityCode details = (NationalityCode) dataObject;
			if (details != null) {
				this.custNationality.setValue(details.getNationalityCode());
				this.lovDescCustNationalityName.setValue(details.getNationalityCode()+ "-"+ 
						details.getNationalityDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustBLRsnCode(Event event) {

		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "BlackListReasonCode");
		if (dataObject instanceof String) {
			this.custBLRsnCode.setValue("");
			this.lovDescCustBLRsnCodeName.setValue("");
		} else {
			BlackListReasonCode details = (BlackListReasonCode) dataObject;
			if (details != null) {
				this.custBLRsnCode.setValue(details.getBLRsnCode());
				this.lovDescCustBLRsnCodeName.setValue(details.getBLRsnCode()
						+ "-" + details.getBLRsnDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustRejectedRsn(Event event) {

		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "RejectDetail");
		if (dataObject instanceof String) {
			this.custRejectedRsn.setValue("");
			this.lovDescCustRejectedRsnName.setValue("");
		} else {
			RejectDetail details = (RejectDetail) dataObject;
			if (details != null) {
				this.custRejectedRsn.setValue(details.getRejectCode());
				this.lovDescCustRejectedRsnName.setValue(details
						.getRejectCode() + "-" + details.getRejectDesc());
			}
		}
		logger.debug("Leaving");
	}
	
	public void onClick$btnSearchCustCtgCode(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerDialog, "CustomerCategory");
		if (dataObject instanceof String) {
			this.custCtgCode.setValue(dataObject.toString());
			this.lovDescCustCtgCodeName.setValue("");
		} else {
			CustomerCategory details = (CustomerCategory) dataObject;
			if (details != null) {
				this.custCtgCode.setValue(details.getCustCtgCode());
				this.lovDescCustCtgCodeName.setValue(details.getCustCtgCode()+ "-" + details.getCustCtgDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchcustGroupID(Event event) {

		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "CustomerGroup");
		if (dataObject instanceof String) {
			this.custGroupID.setValue(new Long(0));
			this.lovDesccustGroupIDName.setValue("");
		} else {
			CustomerGroup details = (CustomerGroup) dataObject;
			if (details != null) {
				this.custGroupID.setValue(Long.valueOf(details.getCustGrpID()));
				this.lovDesccustGroupIDName.setValue(details.getCustGrpID()
						+ "-" + details.getCustGrpDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustEmpDept(Event event) {

		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "GeneralDepartment");
		if (dataObject instanceof String) {
			this.custEmpDept.setValue(dataObject.toString());
			this.lovDescCustEmpDeptName.setValue("");
		} else {
			GeneralDepartment details = (GeneralDepartment) dataObject;
			if (details != null) {
				this.custEmpDept.setValue(details.getGenDepartment());
				this.lovDescCustEmpDeptName.setValue(details.getGenDepartment()
						+ "-" + details.getGenDeptDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustEmpType(Event event) {

		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "EmploymentType");
		if (dataObject instanceof String) {
			this.custEmpType.setValue(dataObject.toString());
			this.lovDescCustEmpTypeName.setValue("");
		} else {
			EmploymentType details = (EmploymentType) dataObject;
			if (details != null) {
				this.custEmpType.setValue(details.getEmpType());
				this.lovDescCustEmpTypeName.setValue(details.getEmpType() + "-"
						+ details.getEmpTypeDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustLng(Event event) {

		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "Language");
		if (dataObject instanceof String) {
			this.custLng.setValue(PennantConstants.default_Language);
			this.lovDescCustLngName.setValue(PennantConstants.default_Language
					+ "-" + "English");
		} else {
			Language details = (Language) dataObject;
			if (details != null) {
				this.custLng.setValue(details.getLngCode());
				this.lovDescCustLngName.setValue(details.getLngCode() + "-"
						+ details.getLngDesc());
			}
		}
		if (this.custLng.getValue().equals(PennantConstants.default_Language)) {
			row_localLngFM.setVisible(false);
			row_localLngLS.setVisible(false);
			row_localLngCorpCustCS.setVisible(false);
		} else if (getCustomerDetails().getCustomer().getLovDescCustCtgType().equals("I")) {
			row_localLngFM.setVisible(true);
			row_localLngLS.setVisible(true);
		} else if (getCustomerDetails().getCustomer().getLovDescCustCtgType().equals("C")) {
			row_localLngCorpCustCS.setVisible(true);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustEmpAddrCity(Event event) {
		logger.debug("Entering");
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("PCProvince",
				this.custEmpAddrProvince.getValue(), Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "City", filters);
		if (dataObject instanceof String) {
			this.custEmpAddrCity.setValue(dataObject.toString());
			this.lovDescCustEmpAddrCityName.setValue("");
		} else {
			City details = (City) dataObject;
			if (details != null) {
				this.custEmpAddrCity.setValue(details.getPCCity());
				this.lovDescCustEmpAddrCityName.setValue(details.getPCCity()
						+ "-" + details.getPCCityName());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustEmpAddrProvince(Event event) {
		logger.debug("Entering");
		String sCustEmpAddrProvince = this.custEmpAddrProvince.getValue();
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("CPCountry",
				this.custEmpAddrCountry.getValue(), Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "Province", filters);
		if (dataObject instanceof String) {
			this.custEmpAddrProvince.setValue(dataObject.toString());
			this.lovDescCustEmpAddrProvinceName.setValue("");
		} else {
			Province details = (Province) dataObject;
			if (details != null) {
				this.custEmpAddrProvince.setValue(details.getCPProvince());
				this.lovDescCustEmpAddrProvinceName.setValue(details
						.getCPProvince() + "-" + details.getCPProvinceName());
			}
		}

		if (!StringUtils.trimToEmpty(sCustEmpAddrProvince).equals(
				this.custEmpAddrProvince.getValue())) {
			this.custEmpAddrCity.setValue("");
			this.lovDescCustEmpAddrCityName.setValue("");
		}
		if (!this.custEmpAddrProvince.getValue().equalsIgnoreCase("")) {
			this.btnSearchCustEmpAddrCity.setVisible(true);
		} else {
			this.btnSearchCustEmpAddrCity.setVisible(false);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustEmpDesg(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "GeneralDesignation");
		if (dataObject instanceof String) {
			this.custEmpDesg.setValue(dataObject.toString());
			this.lovDescCustEmpDesgName.setValue("");
		} else {
			GeneralDesignation details = (GeneralDesignation) dataObject;
			if (details != null) {
				this.custEmpDesg.setValue(details.getGenDesignation());
				this.lovDescCustEmpDesgName.setValue(details
						.getGenDesignation() + "-" + details.getGenDesgDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustEmpAddrCountry(Event event) {
		logger.debug("Entering");
		String sCustEmpAddrCountry = this.custEmpAddrCountry.getValue();

		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "Country");
		if (dataObject instanceof String) {
			this.custEmpAddrCountry.setValue(dataObject.toString());
			this.lovDescCustEmpAddrCountryName.setValue("");
		} else {
			Country details = (Country) dataObject;
			if (details != null) {
				this.custEmpAddrCountry.setValue(details.getCountryCode());
				this.lovDescCustEmpAddrCountryName.setValue(details
						.getCountryCode() + "-" + details.getCountryDesc());
			}
		}

		if (!StringUtils.trimToEmpty(sCustEmpAddrCountry).equalsIgnoreCase(
				this.custEmpAddrCountry.getValue())) {
			this.custEmpAddrProvince.setValue("");
			this.lovDescCustEmpAddrProvinceName.setValue("");
			this.lovDescCustEmpAddrCityName.setValue("");
		}
		if (!this.custEmpAddrCountry.getValue().equalsIgnoreCase("")) {
			this.btnSearchCustEmpAddrProvince.setVisible(true);
		} else {
			this.btnSearchCustEmpAddrProvince.setVisible(false);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustStmtDispatchMode(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerDialog, "DispatchMode");
		if (dataObject instanceof String) {
			this.custStmtDispatchMode.setValue(dataObject.toString());
			this.lovDescDispatchModeDescName.setValue("");
		} else {
			DispatchMode details = (DispatchMode) dataObject;
			if (details != null) {
				this.custStmtDispatchMode.setValue(details
						.getDispatchModeCode());
				this.lovDescDispatchModeDescName.setValue(details
						.getDispatchModeCode()
						+ "-"
						+ details.getDispatchModeDesc());
			}
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
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerRating/CustomerRatingDialog.zul",
							window_CustomerDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
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
				map.put("moduleType", this.moduleType);
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/CustomerRating/CustomerRatingDialog.zul",
									window_CustomerDialog, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++ New Button & Double Click Events for CustomerPRelationList+++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public void onClick$btnNew_CustomerPRelation(Event event) throws Exception {
		logger.debug("Entering");
		CustomerPRelation customerPRelation = new CustomerPRelation();
		customerPRelation.setNewRecord(true);
		customerPRelation.setWorkflowId(0);
		customerPRelation.setPRCustID(getCustomerDetails().getCustID());
		customerPRelation.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		customerPRelation.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerPRelation", customerPRelation);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		map.put("isMinor", this.custIsMinor.isChecked());
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerPRelation/CustomerPRelationDialog.zul",
							null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	public void onCustomerPRelationItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerPRelation.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerPRelation customerPRelation = (CustomerPRelation) item.getAttribute("data");
			if (customerPRelation.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("customerPRelation", customerPRelation);
				map.put("customerDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("isMinor", this.custIsMinor.isChecked());
				map.put("moduleType", this.moduleType);

				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/CustomerPRelation/CustomerPRelationDialog.zul",
									null, map);
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
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerAddres/CustomerAddresDialog.zul",
							null, map);
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
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/CustomerAddres/CustomerAddresDialog.zul",
									null, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving");
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
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerEMail/CustomerEMailDialog.zul",
							null, map);
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
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/CustomerEMail/CustomerEMailDialog.zul",
									null, map);
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
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerPhoneNumber/CustomerPhoneNumberDialog.zul",
							null, map);
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
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/CustomerPhoneNumber/CustomerPhoneNumberDialog.zul",
									null, map);
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
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul",
							null, map);
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
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/CustomerDocument/CustomerDocumentDialog.zul",
									null, map);
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
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerIncome/CustomerIncomeDialog.zul",
							null, map);
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

				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/CustomerIncome/CustomerIncomeDialog.zul",
									null, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving");
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++ New Button & Double Click Events for Customer Director List+++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public void onClick$btnNew_CustomerDirector(Event event) throws Exception {
		logger.debug("Entering");
		DirectorDetail directorDetail =new DirectorDetail();
		directorDetail.setNewRecord(true);
		directorDetail.setWorkflowId(0);
		directorDetail.setCustID(getCustomerDetails().getCustID());
		directorDetail.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		directorDetail.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("directorDetail", directorDetail);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/DirectorDetail/DirectorDetailDialog.zul",
							null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	public void onCustomerDirectorItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerDirectors.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final DirectorDetail directorDetail = (DirectorDetail) item.getAttribute("data");

			if (directorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("directorDetail", directorDetail);
				map.put("customerDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
				
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/DirectorDetail/DirectorDetailDialog.zul",
									null,map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// New Button & Double Click Events for Customer Balance Sheet Detail List//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public void onClick$btnNew_CustomerBalanceSheet(Event event) throws Exception {
		logger.debug("Entering");
		CustomerBalanceSheet balanceSheet =new CustomerBalanceSheet();
		balanceSheet.setNewRecord(true);
		balanceSheet.setWorkflowId(0);
		balanceSheet.setCustId(getCustomerDetails().getCustID());
		balanceSheet.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
		balanceSheet.setLovDescCustShrtName(getCustomerDetails().getCustomer().getCustShrtName());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerBalanceSheet", balanceSheet);
		map.put("customerDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CustomerBalanceSheet/CustomerBalanceSheetDialog.zul",
							null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	public void onCustomerBalanceSheetItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxCustomerBalanceSheet.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerBalanceSheet balanceSheet = (CustomerBalanceSheet) item.getAttribute("data");

			if (balanceSheet.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("customerBalanceSheet", balanceSheet);
				map.put("customerDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("moduleType", this.moduleType);
				
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/CustomerMasters/CustomerBalanceSheet/CustomerBalanceSheetDialog.zul",
								window_CustomerDialog,map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
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

		//Basic Details Tab-->1.Key Details
		this.lovDescCustTypeCodeName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
			new String[] { Labels.getLabel("label_CustomerDialog_CustTypeCode.value") }));

		this.lovDescCustDftBranchName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
			new String[] { Labels.getLabel("label_CustomerDialog_CustDftBranch.value") }));

		this.lovDescCustBaseCcyName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY", 
			new String[] { Labels.getLabel("label_CustomerDialog_CustBaseCcy.value") }));

		this.lovDescCustLngName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY", 
				new String[] { Labels.getLabel("label_CustomerDialog_CustLng.value") }));
		
		if (this.gb_personalDetails.isVisible()) {
			//Basic Details Tab-->2.Personal Details(Retail Customer)

			this.lovDescCustGenderCodeName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_CustomerDialog_CustGenderCode.value") }));

			this.lovDescCustSalutationCodeName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_CustomerDialog_CustSalutationCode.value") }));

			this.lovDescCustCOBName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_CustomerDialog_CustCOB.value") }));

			this.lovDescCustMaritalStsName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_CustomerDialog_CustMaritalSts.value") }));

			this.lovDescCustProfessionName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_CustomerDialog_CustProfession.value") }));

		} else if (this.gb_corporateCustomerPersonalDetails.isVisible()) {
			//Basic Details Tab-->3.Organization Details(Corporate Customer)
			this.lovDescCorpCustCOBName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_CustomerDialog_CorpCustCOB.value") }));
		}
		
		//Generic Details Tab
		if (getCustomerDetails().getCustomer().getLovDescCustCtgType().equals("I")) {
			this.lovDescCustEmpStsName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_CustomerDialog_CustEmpSts.value") }));
		}

		//Demographic Details Tab-->1.Segmentation Details
		this.lovDescCustCtgCodeName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY", 
				new String[] { Labels.getLabel("label_CustomerDialog_CustCtgCode.value") }));

		this.lovDescCustSectorName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
				new String[] { Labels.getLabel("label_CustomerDialog_CustSector.value") }));

		this.lovDescCustSubSectorName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_CustomerDialog_CustSubSector.value") }));
		
		this.lovDescCustIndustryName.setConstraint("NO EMPTY:" + Labels.getLabel( "FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_CustomerDialog_CustIndustry.value") }));

		this.lovDescCustSegmentName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
				new String[] { Labels.getLabel("label_CustomerDialog_CustSegment.value") }));

		this.lovDescCustSubSegmentName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_CustomerDialog_CustSubSegment.value") }));
		
		//KYC Details Tab -->2.Identity Details
		this.lovDescCustResdCountryName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_CustomerDialog_CustResdCountry.value") }));

		this.lovDescCustNationalityName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_CustomerDialog_CustNationality.value") }));

		//Preferential Details Tab --> 1.Non-Financial RelationShip Details
		this.lovDescCustDSADeptName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
				new String[] { Labels.getLabel("label_CustomerDialog_CustDSADept.value") }));

		this.lovDescCustRO1Name.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY", 
				new String[] { Labels.getLabel("label_CustomerDialog_CustRO1.value") }));

		if (getCustomerDetails().getCustomer().getLovDescCustCtgType().equals("I")) {

			if(this.custEmpSts.getValue().equalsIgnoreCase("EMPLOY")){

				this.lovDescCustEmpDesgName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpDesg.value") }));

				this.lovDescCustEmpDeptName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpDept.value") }));

				this.lovDescCustEmpTypeName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpType.value") }));

				this.lovDescCustEmpAddrCityName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpAddrCity.value") }));

				this.lovDescCustEmpAddrProvinceName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpAddrProvince.value") }));

				this.lovDescCustEmpAddrCountryName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_CustomerEmploymentDetailDialog_CustEmpAddrCountry.value") }));

			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the QDE
	 * LOVfields.
	 */
	private void doSetQDELOVValidation() {
		logger.debug("Entering");
		this.lovDescCustNationalityName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_CustomerDialog_CustNationality.value") }));
		
		this.lovDescCustBaseCcyName.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY", 
				new String[] { Labels.getLabel("label_CustomerDialog_CustBaseCcy.value") }));
		
		logger.debug("Leaving");
		
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
		this.pagingCustomerRatingList.setPageSize(getCountRows());
		this.pagingCustomerRatingList.setDetailed(true);
		getCustomerRatingsPagedListWrapper().initList(ratingsList,
				this.listBoxCustomerRating, this.pagingCustomerRatingList);
		this.listBoxCustomerRating.setItemRenderer(new CustomerRatinglistItemRenderer());
		logger.debug("Leaving");
	}

	/**
	 * Generate the Customer Address Details List in the CustomerDialogCtrl and
	 * set the list in the listBoxCustomerAddress listbox by using Pagination
	 */
	public void doFillCustomerAddress(List<CustomerAddres> customerAddress) {
		logger.debug("Entering");
		setAddressList(customerAddress);
		this.pagingCustomerAddressList.setPageSize(getCountRows());
		this.pagingCustomerAddressList.setDetailed(true);
		getCustomerAddresPagedListWrapper().initList(addressList,
				this.listBoxCustomerAddress, this.pagingCustomerAddressList);
		this.listBoxCustomerAddress.setItemRenderer(new CustomerAddressListModelItemRenderer());
		logger.debug("Leaving");
	}

	/**
	 * Generate the Customer Email Details List in the CustomerDialogCtrl and
	 * set the list in the listBoxCustomerEmail listbox by using Pagination
	 */
	public void doFillCustomerEmail(List<CustomerEMail> customerEmails) {
		logger.debug("Entering");
		setEmailList(customerEmails);
		this.pagingCustomerEmailList.setPageSize(getCountRows());
		this.pagingCustomerEmailList.setDetailed(true);
		getCustomerEmailPagedListWrapper().initList(emailList,
				this.listBoxCustomerEmailAddress, this.pagingCustomerEmailList);
		this.listBoxCustomerEmailAddress.setItemRenderer(new CustomerEmailListModelItemRenderer());
		logger.debug("Leaving");
	}

	/**
	 * Generate the Customer Phone Details List in the CustomerDialogCtrl and
	 * set the list in the listBoxCustomerPhone listbox by using Pagination
	 */
	public void doFillCustomerPhoneNumbers(
			List<CustomerPhoneNumber> customerPhoneNumbers) {
		logger.debug("Entering");
		setPhoneNumberList(customerPhoneNumbers);
		this.pagingCustomerPhoneList.setPageSize(getCountRows());
		this.pagingCustomerPhoneList.setDetailed(true);
		getCustomerPhoneNumberPagedListWrapper().initList(phoneNumberList,
				this.listBoxCustomerPhoneNumbers, this.pagingCustomerPhoneList);
		this.listBoxCustomerPhoneNumbers.setItemRenderer(new CustomerPhoneNumListModelItemRenderer());
		logger.debug("Leaving");
	}

	/**
	 * Generate the Customer PRelations Details List in the CustomerDialogCtrl
	 * and set the list in the listBoxCustomerPRelation listbox by using
	 * Pagination
	 */
	public void doFillCustomerPRelations(List<CustomerPRelation> pRelations) {
		logger.debug("Entering");
		setpRelationList(pRelations);
		this.pagingCustomerPRelationList.setPageSize(getCountRows());
		this.pagingCustomerPRelationList.setDetailed(true);
		getCustomerPRelationPagedListWrapper().initList(pRelationList, 
				this.listBoxCustomerPRelation,this.pagingCustomerPRelationList);
		this.listBoxCustomerPRelation.setItemRenderer(new CustomerPRelationListModelItemRenderer());
		logger.debug("Leaving");
	}

	/**
	 * Generate the Customer Income Details List in the CustomerDialogCtrl and
	 * set the list in the listBoxCustomerIncome listbox by using Pagination
	 */
	public void doFillCustomerIncome(List<CustomerIncome> incomes) {
		logger.debug("Entering");
		setIncomeList(incomes);
		this.pagingCustomerIncomeList.setPageSize(getCountRows());
		this.pagingCustomerIncomeList.setDetailed(true);
		BigDecimal income = new BigDecimal(0);
		for (int i = 0; i < incomeList.size(); i++) {
			CustomerIncome customerIncome = incomeList.get(i);
			customerIncome.setLovDescCcyEditField(ccyFormatter);
			income = income.add(customerIncome.getCustIncome());
		}
		
		this.custTotalIncome.setValue(PennantAppUtil.formateAmount(
				income, ccyFormatter));
		
		getCustomerIncomePagedListWrapper().initList(incomeList,
				this.listBoxCustomerIncome, this.pagingCustomerIncomeList);
		this.listBoxCustomerIncome.setItemRenderer(new CustomerIncomeListItemRenderer());
		logger.debug("Leaving");
	}

	/**
	 * Generate the Customer Documents List in the CustomerDialogCtrl and set
	 * the list in the listBoxCustomerDocuments listbox by using Pagination
	 */
	public void doFillCustomerDocuments(List<CustomerDocument> documents) {
		logger.debug("Entering");
		setDocumentsList(documents);
		this.pagingCustomerDocumentList.setPageSize(getCountRows());
		this.pagingCustomerDocumentList.setDetailed(true);
		getCustomerDocumentsPagedListWrapper().initList(documentsList,
				this.listBoxCustomerDocuments, this.pagingCustomerDocumentList);
		this.listBoxCustomerDocuments.setItemRenderer(new CustomerDocumentsListModelItemRenderer());
		logger.debug("Leaving");
	}
	
	/**
	 * Generate the Customer Director Details List in the CustomerDialogCtrl and
	 * set the list in the listBoxCustomerDirectors listbox by using Pagination
	 */
	public void doFillCustomerDirectors(List<DirectorDetail> directorDetails) {
		logger.debug("Entering");
		setDirectorsList(directorDetails);
		this.pagingCustomerDirectorList.setPageSize(getCountRows());
		this.pagingCustomerDirectorList.setDetailed(true);
		getCustomerDirectorsPagedListWrapper().initList(directorsList,
				this.listBoxCustomerDirectors, this.pagingCustomerDirectorList);
		this.listBoxCustomerDirectors.setItemRenderer(new CustomerDirectorListItemRenderer());
		logger.debug("Leaving");
	}

	/**
	 * Generate the Customer Rating Details List in the CustomerDialogCtrl and
	 * set the list in the listBoxCustomerRating listbox by using Pagination
	 */
	public void doFillCustomerBalanceSheet(List<CustomerBalanceSheet> balanceSheetDetails) {
		logger.debug("Entering");
		setBalanceSheetList(balanceSheetDetails);
		this.pagingCustomerBalanceSheetList.setPageSize(getCountRows());
		this.pagingCustomerBalanceSheetList.setDetailed(true);
		getCustomerBalanceSheetPagedListWrapper().initList(balanceSheetList,
				this.listBoxCustomerBalanceSheet, this.pagingCustomerBalanceSheetList);
		this.listBoxCustomerBalanceSheet.setItemRenderer(new CustomerBalanceSheetListItemRenderer());
		logger.debug("Leaving");
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++OnCheck CheckBox Events+++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public void onCheck$custIsStaff(Event event) {
		staffIDCheck();
	}

	public void onCheck$custIsBlackListed(Event event) {
		blackListCustomerCheck();
	}

	public void onCheck$custIsRejected(Event event) {
		rejectedListCustomerCheck();
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ OnBlur TextBox Events +++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public void onBlur$custPassportNo(Event event) {
		passportNoChange();
	}

	public void onBlur$custVisaNum(Event event) {
		visaNoChange();
	}

	public void onBlur$custMName(Event event) {
		mNameCheck();
	}

	public void onBlur$custDOB(Event event) {
		dobCheck();
	}

	/**
	 * Generate the Customer Details in the CustomerDialogCtrl and set Fields by
	 * using Events on Loading & in Editing<br>
	 * 
	 * checkbox Events and Field Blur events
	 */
	public void doCheckValidations() {
		logger.debug("Entering");
		this.custIsActive.setChecked(true);
		this.custIsClosed.setChecked(false);
		this.custSts.setValue("ACTIVE");
		this.custGroupSts.setValue("ACTIVE");
		customerCategoryCheck();
		staffIDCheck();
		blackListCustomerCheck();
		rejectedListCustomerCheck();
		passportNoChange();
		visaNoChange();
		mNameCheck();
		dobCheck();
		logger.debug("Leaving");
	}

	/**
	 * Method to check customer category.
	 * */
	public void customerCategoryCheck() {
		logger.debug("Entering");
		
		if (getCustomerDetails().getCustomer().getLovDescCustCtgType().equals("I")) {
			
			String arr [] = SystemParameterDetails.getSystemParameterValue("NONEED_VISA_COUNTRIES").toString().split(",");
			for(int i=0;i<arr.length;i++){
				if(arr[i].equals(this.custCOB.getValue())){ //If selected country is in list of visa not needed countries make visa invisble.
					this.row_retailVisa.setVisible(false);
					break;
				}
			}
			
			if (this.custEmpSts.getValue().equals("EMPLOY")) {
				this.employmentDetails.setVisible(true);
				this.gb_employerDetails.setVisible(true);
			}
			
			if (this.custLng.getValue().equals(PennantConstants.default_Language)) {
				row_localLngFM.setVisible(false);
				row_localLngLS.setVisible(false);
			} else {
				row_localLngFM.setVisible(true);
				row_localLngLS.setVisible(true);
			}
		} else if(getCustomerDetails().getCustomer().getLovDescCustCtgType().equals("C")){
			this.employmentDetails.setVisible(false);
			
			if (this.custLng.getValue().equals(PennantConstants.default_Language)) {
				row_localLngCorpCustCS.setVisible(false);
			} else {
				row_localLngCorpCustCS.setVisible(true);
			}
		}
		
		if(this.custSector.getValue().equals("")){
			this.btnSearchCustSubSector.setVisible(false);
			this.btnSearchCustIndustry.setVisible(false);
		}
		if(this.custSegment.getValue().equals("")){
			this.btnSearchCustSubSegment.setVisible(false);
		}
		if(this.custGenderCode.getValue().equals("")){
			this.btnSearchCustSalutationCode.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * Check Whether Customer is related to Staff of the Bank or Not
	 * 
	 */
	public void staffIDCheck() {
		logger.debug("Entering");
		if (this.custIsStaff.isChecked()) {
			this.custStaffID.setReadonly(isReadOnly("CustomerDialog_custStaffID"));
			this.space_isStaff.setStyle("background-color:red");
		} else {
			this.custStaffID.setValue("");
			this.custStaffID.setReadonly(true);
			this.space_isStaff.setStyle("background-color:white");
		}
		logger.debug("Leaving");
	}

	/**
	 * Check Whether Customer is Black Listed Customer or Not
	 * 
	 */
	public void blackListCustomerCheck() {
		logger.debug("Entering");
		if (this.custIsBlackListed.isChecked()) {
			this.custBLRsnCode.setReadonly(isReadOnly("CustomerDialog_custBLRsnCode"));
			this.btnSearchCustBLRsnCode.setDisabled(isReadOnly("CustomerDialog_custBLRsnCode"));
			this.space_blackListReason.setStyle("background-color:red");
		} else {
			this.custBLRsnCode.setValue("");
			this.lovDescCustBLRsnCodeName.setValue("");
			this.custBLRsnCode.setReadonly(true);
			this.btnSearchCustBLRsnCode.setDisabled(true);
			this.space_blackListReason.setStyle("background-color:white");
		}
		logger.debug("Leaving");
	}

	/**
	 * Check Whether Customer is Rejected Listed Customer or Not
	 * 
	 */
	public void rejectedListCustomerCheck() {
		logger.debug("Entering");
		if (this.custIsRejected.isChecked()) {
			this.custRejectedRsn.setReadonly(isReadOnly("CustomerDialog_custRejectedRsn"));
			this.btnSearchCustRejectedRsn.setDisabled(isReadOnly("CustomerDialog_custRejectedRsn"));
			this.space_rejectedReason.setStyle("background-color:red");
		} else {
			this.custRejectedRsn.setValue("");
			this.lovDescCustRejectedRsnName.setValue("");
			this.custRejectedRsn.setReadonly(true);
			this.btnSearchCustRejectedRsn.setDisabled(true);
			this.space_rejectedReason.setStyle("background-color:white");
		}
		logger.debug("Leaving");
	}

	/**
	 * Check Whether PassPort Number Entered or Not
	 * 
	 */
	public void passportNoChange() {
		logger.debug("Entering");
		if (StringUtils.trimToEmpty(this.custPassportNo.getValue()).equals("")) {
			if (StringUtils.trimToEmpty(this.custVisaNum.getValue()).equals("")) {
				this.space_custPassportExpiry.setStyle("background-color:white");
				this.custPassportExpiry.setDisabled(true);
			}
			this.custPassportExpiry.setText("");
		} else {
			this.space_custPassportExpiry.setStyle("background-color:red");
			if("ENQ".equals(this.moduleType)){
				this.custPassportExpiry.setDisabled(true);
			}else{
				this.custPassportExpiry.setDisabled(isReadOnly("CustomerDialog_custPassportExpiry"));				
			}			
			this.custPassportExpiry.setFocus(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Check Whether VISA Number Entered or Not
	 * 
	 */
	public void visaNoChange() {
		logger.debug("Entering");
		if (StringUtils.trimToEmpty(this.custVisaNum.getValue()).equals("")) {
			this.space_custVisaExpiry.setStyle("background-color:white");
			this.custVisaExpiry.setDisabled(true);
			if (StringUtils.trimToEmpty(this.custPassportNo.getValue()).equals("")) {
				this.space_custPassportExpiry.setStyle("background-color:white");
				custPassportExpiry.setDisabled(true);
			}
			this.custVisaExpiry.setText("");
		} else {
			this.space_custVisaExpiry.setStyle("background-color:red");
			this.custVisaExpiry.setDisabled(isReadOnly("CustomerDialog_custVisaExpiry"));
			custPassportExpiry.setDisabled(isReadOnly("CustomerDialog_custPassportExpiry"));
		}
		logger.debug("Leaving");
	}

	/**
	 * Check Whether Middle Name field Entered or Not
	 * 
	 */
	public void mNameCheck() {
		logger.debug("Entering");
		space_custMNameLclLng.setStyle("background-color:white");
		if (!StringUtils.trimToEmpty(this.custMName.getValue()).equals("")
				&& getCustomerDetails().getCustomer().getLovDescCustCtgType().equals("I")) {
			space_custMNameLclLng.setStyle("background-color:red");
		} 
		logger.debug("Leaving");
	}

	/**
	 * Check Whether DateOfBirth field Entered or Not
	 * 
	 */
	private void dobCheck() {
		logger.debug("Entering");
		this.custIsMinor.setChecked(false);
		if (this.custDOB.getValue() != null) {
			int age = DateUtility.getYearsBetween(this.custDOB.getValue(),new Date());
			if (age < ((BigDecimal) SystemParameterDetails.getSystemParameterValue("MINOR_AGE")).intValue()) {
				this.custIsMinor.setChecked(true);
			}
			this.custIsMinor.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++OnSelect ComboBox Events++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void onSelect$custStmtFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		String StmtFrqCode = validateCombobox(this.custStmtFrqCode);
		onSelectFrqCode(StmtFrqCode, this.custStmtFrqCode, this.custStmtFrqMth, this.custStmtFrqDays, this.custStmtFrq,isReadOnly("CustomerDialog_custStmtFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$custStmtFrqMth(Event event) {
		logger.debug("Entering" + event.toString());
		String StmtFrqCode = validateCombobox(this.custStmtFrqCode);
		String StmtFrqMonth = validateCombobox(this.custStmtFrqMth);
		onSelectFrqMth(StmtFrqCode, StmtFrqMonth, this.custStmtFrqMth,
				this.custStmtFrqDays, this.custStmtFrq,isReadOnly("CustomerDialog_custStmtFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$custStmtFrqDays(Event event) {
		logger.debug("Entering" + event.toString());
		String StmtFrqCode = validateCombobox(this.custStmtFrqCode);
		String StmtFrqMonth = validateCombobox(this.custStmtFrqMth);
		String StmtFrqday = validateCombobox(this.custStmtFrqDays);
		onSelectFrqDay(StmtFrqCode, StmtFrqMonth, StmtFrqday, this.custStmtFrq);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the GroupStatus field Is Mandatory or not depend on GroupID
	 */
	//TODO -- remove if group status is not present
	public void onSelect$genericInformation(Event event) {
		logger.debug("Entering" + event.toString());
		if (this.custGroupID.longValue() != 0) {
			space_custGroupSts.setStyle("background-color:red");
		} else {
			space_custGroupSts.setStyle("background-color:white");
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to set the value in Customer CIF field to upper case, if manually
	 * enterred by user.
	 */
	/*public void onBlur$custCIF(Event event) {
		logger.debug("Entering" + event.toString());
		if (SystemParameterDetails.getSystemParameterValue("CB_CID").equals("CIF")) {
			this.custCoreBank.setValue(this.custCIF.getValue().toUpperCase());
			if (this.lovDescCustTypeCodeName.getValue().equals("")) {
				this.lovDescCustTypeCodeName.setFocus(true);
			}
		}
		logger.debug("Leaving" + event.toString());
	}*/

	/** To get the Combobox selected value */
	private String validateCombobox(Combobox combobox) {
		String comboValue = "";
		if (combobox.getSelectedItem() != null) {
			comboValue = combobox.getSelectedItem().getValue().toString();
		} else {
			combobox.setSelectedIndex(0);
		}
		return comboValue;
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
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aCustomerDetails.getBefImage(), aCustomerDetails);
		return new AuditHeader(String.valueOf(aCustomerDetails.getCustID())
				,String.valueOf(aCustomerDetails.getCustID()), null, 
				null, auditDetail,aCustomerDetails.getUserDetails(), getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetails( PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerDialog,auditHeader);
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

	public CustomerEmploymentDetail getCustomerEmploymentDetail() {
		return customerEmploymentDetail;
	}
	public void setCustomerEmploymentDetail(
			CustomerEmploymentDetail customerEmploymentDetail) {
		this.customerEmploymentDetail = customerEmploymentDetail;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	//Paged List Wrapper Declarations For Customer Related List
	public PagedListWrapper<CustomerRating> getCustomerRatingsPagedListWrapper() {
		return customerRatingsPagedListWrapper;
	}
	@SuppressWarnings("unchecked")
	public void setCustomerRatingsPagedListWrapper() {
		if (this.customerRatingsPagedListWrapper == null) {
			this.customerRatingsPagedListWrapper = (PagedListWrapper<CustomerRating>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

	public PagedListWrapper<CustomerAddres> getCustomerAddresPagedListWrapper() {
		return customerAddresPagedListWrapper;
	}
	@SuppressWarnings("unchecked")
	public void setCustomerAddresPagedListWrapper() {
		if (this.customerAddresPagedListWrapper == null) {
			this.customerAddresPagedListWrapper = (PagedListWrapper<CustomerAddres>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

	public PagedListWrapper<CustomerEMail> getCustomerEmailPagedListWrapper() {
		return customerEmailPagedListWrapper;
	}
	@SuppressWarnings("unchecked")
	public void setCustomerEmailPagedListWrapper() {
		if (this.customerEmailPagedListWrapper == null) {
			this.customerEmailPagedListWrapper = (PagedListWrapper<CustomerEMail>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

	public PagedListWrapper<CustomerPhoneNumber> getCustomerPhoneNumberPagedListWrapper() {
		return customerPhoneNumberPagedListWrapper;
	}
	@SuppressWarnings("unchecked")
	public void setCustomerPhoneNumberPagedListWrapper() {
		if (this.customerPhoneNumberPagedListWrapper == null) {
			this.customerPhoneNumberPagedListWrapper = (PagedListWrapper<CustomerPhoneNumber>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

	public PagedListWrapper<CustomerDocument> getCustomerDocumentsPagedListWrapper() {
		return customerDocumentsPagedListWrapper;
	}
	@SuppressWarnings("unchecked")
	public void setCustomerDocumentsPagedListWrapper() {
		if (this.customerDocumentsPagedListWrapper == null) {
			this.customerDocumentsPagedListWrapper = (PagedListWrapper<CustomerDocument>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}	

	public PagedListWrapper<CustomerIncome> getCustomerIncomePagedListWrapper() {
		return customerIncomePagedListWrapper;
	}
	@SuppressWarnings("unchecked")
	public void setCustomerIncomePagedListWrapper() {
		if (this.customerIncomePagedListWrapper == null) {
			this.customerIncomePagedListWrapper = (PagedListWrapper<CustomerIncome>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

	public PagedListWrapper<CustomerPRelation> getCustomerPRelationPagedListWrapper() {
		return customerPRelationPagedListWrapper;
	}
	@SuppressWarnings("unchecked")
	public void setCustomerPRelationPagedListWrapper() {
		if (this.customerPRelationPagedListWrapper == null) {
			this.customerPRelationPagedListWrapper = (PagedListWrapper<CustomerPRelation>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}	
	
	public PagedListWrapper<DirectorDetail> getCustomerDirectorsPagedListWrapper() {
		return customerDirectorsPagedListWrapper;
	}
	@SuppressWarnings("unchecked")
	public void setCustomerDirectorsPagedListWrapper() {
		if (this.customerDirectorsPagedListWrapper == null) {
			this.customerDirectorsPagedListWrapper = (PagedListWrapper<DirectorDetail>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}
	
	public PagedListWrapper<CustomerBalanceSheet> getCustomerBalanceSheetPagedListWrapper() {
		return customerBalanceSheetPagedListWrapper;
	}
	@SuppressWarnings("unchecked")
	public void setCustomerBalanceSheetPagedListWrapper() {
		if (this.customerBalanceSheetPagedListWrapper == null) {
			this.customerBalanceSheetPagedListWrapper = (PagedListWrapper<CustomerBalanceSheet>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

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

	public void setCustomerDetailsService(
			CustomerDetailsService customerDetailsService) {
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

	//Customer Related List 
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

	public List<CustomerPRelation> getpRelationList() {
		return pRelationList;
	}
	public void setpRelationList(List<CustomerPRelation> pRelationList) {
		this.pRelationList = pRelationList;
	}

	public void setDirectorsList(List<DirectorDetail> directorsList) {
		this.directorsList = directorsList;
	}
	public List<DirectorDetail> getDirectorsList() {
		return directorsList;
	}

	public void setBalanceSheetList(List<CustomerBalanceSheet> balanceSheetList) {
		this.balanceSheetList = balanceSheetList;
	}
	public List<CustomerBalanceSheet> getBalanceSheetList() {
		return balanceSheetList;
	}

}