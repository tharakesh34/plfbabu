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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  CustomerMaintenanceDialogCtrl.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  22-04-2011    
 *                                                                  
 * Modified Date    :  22-04-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2011       Pennant	                 0.1                                         * 
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
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
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
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
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.staticparms.Language;
import com.pennant.backend.model.systemmasters.BlackListReasonCode;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Department;
import com.pennant.backend.model.systemmasters.DispatchMode;
import com.pennant.backend.model.systemmasters.EmpStsCode;
import com.pennant.backend.model.systemmasters.Gender;
import com.pennant.backend.model.systemmasters.GroupStatusCode;
import com.pennant.backend.model.systemmasters.Industry;
import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennant.backend.model.systemmasters.NationalityCode;
import com.pennant.backend.model.systemmasters.Profession;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.model.systemmasters.Segment;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.model.systemmasters.SubSegment;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/Customer/customerMaintenanceDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class CustomerMaintenanceDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 8699477059338878791L;
	private final static Logger logger = Logger.getLogger(CustomerMaintenanceDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerMaintenanceDialog; 		// autoWired

	//Basic Details Tab-->1.Key Details
	protected Longbox 		custID; 					// autowired
	protected Textbox 		custCIF; 					// autowired
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
	protected Row 			row_localLngFM;						
	protected Row 			row_localLngLS;
	
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
	protected Row 			row_localLngCorpCustCS;
	
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
	
	//Other Details Tab-->1.Segmentation Details
	protected Textbox 		custCtgCode; 				// autowired
	protected Textbox 		custSector; 				// autowired
	protected Textbox 		custSubSector; 				// autowired
	protected Textbox 		custIndustry; 				// autowired
	protected Textbox 		custSegment; 				// autowired
	protected Textbox 		custSubSegment; 			// autowired
	
	//Other Details Tab-->2.Identity Details
	protected Textbox 		custParentCountry; 			// autowired
	protected Textbox 		custResdCountry; 			// autowired
	protected Textbox 		custRiskCountry; 			// autowired
	protected Textbox 		custNationality; 			// autowired
	
	//Other Details Tab-->3.Non-Financial RelationShip Details
	protected Textbox 		custDSA; 					// autowired
	protected Textbox 		custDSADept; 				// autowired
	protected Textbox 		custRO1; 					// autowired
	protected Textbox 		custRO2; 					// autowired
	protected Textbox 		custReferedBy; 				// autowired

	//Other Details Tab-->4.Statement Details	
	protected Textbox 		custStmtFrq; 				// autowired
	protected Checkbox 		custIsStmtCombined; 		// autowired
	protected Datebox 		custStmtLastDate; 			// autowired
	protected Datebox 		custStmtNextDate; 			// autowired
	protected Textbox 		custStmtDispatchMode; 		// autowired
	
	//Additional Details Tab
	protected Textbox 		custAddlVar81; 				// autowired
	protected Textbox 		custAddlVar82; 				// autowired
	protected Textbox 		custAddlVar83; 				// autowired
	protected Textbox 		custAddlVar84; 				// autowired
	protected Textbox 		custAddlVar85; 				// autowired
	protected Textbox 		custAddlVar86; 				// autowired
	protected Textbox 		custAddlVar87; 				// autowired
	protected Textbox 		custAddlVar88; 				// autowired
	protected Textbox 		custAddlVar89; 				// autowired
	protected Datebox 		custAddlDate1; 				// autowired
	protected Datebox 		custAddlDate2; 				// autowired
	protected Datebox 		custAddlDate3; 				// autowired
	protected Datebox 		custAddlDate4; 				// autowired
	protected Datebox 		custAddlDate5; 				// autowired
	protected Textbox 		custAddlVar1; 				// autowired
	protected Textbox 		custAddlVar2; 				// autowired
	protected Textbox 		custAddlVar3; 				// autowired
	protected Textbox 		custAddlVar4; 				// autowired
	protected Textbox 		custAddlVar5; 				// autowired
	protected Textbox 		custAddlVar6; 				// autowired
	protected Textbox 		custAddlVar7; 				// autowired
	protected Textbox 		custAddlVar8; 				// autowired
	protected Textbox 		custAddlVar9; 				// autowired
	protected Textbox 		custAddlVar10;				// autowired
	protected Textbox 		custAddlVar11; 				// autowired
	protected Decimalbox 	custAddlDec1; 				// autowired
	protected Decimalbox 	custAddlDec2; 				// autowired
	protected Decimalbox 	custAddlDec3; 				// autowired
	protected Decimalbox 	custAddlDec4; 				// autowired
	protected Decimalbox 	custAddlDec5; 				// autowired
	protected Intbox 		custAddlInt1;				// autowired
	protected Intbox 		custAddlInt2; 				// autowired
	protected Intbox 		custAddlInt3; 				// autowired
	protected Intbox 		custAddlInt4; 				// autowired
	protected Intbox 		custAddlInt5; 				// autowired

	// Space Id's Checking for Mandatory or not
	protected Space space_closedDate;
	protected Space space_custStsChdDate;
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
	
	protected Label 		recordStatus; // autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	

	// not autoWired variables
	private Customer customer; 												// over handed per parameter
	private transient CustomerMaintenanceListCtrl customerMaintenanceListCtrl;// overHanded per parameters
	String parms[] = new String[4];

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialize.

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
	
	//Other Details Tab-->1.Segmentation Details
	private transient String oldVar_custCtgCode;
	private transient String oldVar_custIndustry;
	private transient String oldVar_custSector;
	private transient String oldVar_custSubSector;
	private transient String oldVar_custSegment;
	private transient String oldVar_custSubSegment;
	
	//Other Details Tab-->2.Identity Details
	private transient String oldVar_custParentCountry;
	private transient String oldVar_custResdCountry;
	private transient String oldVar_custRiskCountry;
	private transient String oldVar_custNationality;
	
	//Other Details Tab-->3.Non-Financial RelationShip Details
	private transient String oldVar_custDSA;
	private transient String oldVar_custDSADept;
	private transient String oldVar_custRO1;
	private transient String oldVar_custRO2;	
	private transient String oldVar_custReferedBy;
	
	//Other Details Tab-->4.Statement Details	
	private transient String oldVar_custStmtFrq;
	private transient boolean oldVar_custIsStmtCombined;
	private transient Date oldVar_custStmtLastDate;
	private transient Date oldVar_custStmtNextDate;
	private transient String oldVar_custStmtDispatchMode;

	//Additional Details Tab
	private transient String oldVar_custAddlVar81;
	private transient String oldVar_custAddlVar82;
	private transient String oldVar_custAddlVar83;
	private transient String oldVar_custAddlVar84;
	private transient String oldVar_custAddlVar85;
	private transient String oldVar_custAddlVar86;
	private transient String oldVar_custAddlVar87;
	private transient String oldVar_custAddlVar88;
	private transient String oldVar_custAddlVar89;
	private transient Date oldVar_custAddlDate1;
	private transient Date oldVar_custAddlDate2;
	private transient Date oldVar_custAddlDate3;
	private transient Date oldVar_custAddlDate4;
	private transient Date oldVar_custAddlDate5;
	private transient String oldVar_custAddlVar1;
	private transient String oldVar_custAddlVar2;
	private transient String oldVar_custAddlVar3;
	private transient String oldVar_custAddlVar4;
	private transient String oldVar_custAddlVar5;
	private transient String oldVar_custAddlVar6;
	private transient String oldVar_custAddlVar7;
	private transient String oldVar_custAddlVar8;
	private transient String oldVar_custAddlVar9;
	private transient String oldVar_custAddlVar10;
	private transient String oldVar_custAddlVar11;
	private transient double oldVar_custAddlDec1;
	private transient double oldVar_custAddlDec2;
	private transient double oldVar_custAddlDec3;
	private transient double oldVar_custAddlDec4;
	private transient double oldVar_custAddlDec5;
	private transient int oldVar_custAddlInt1;
	private transient int oldVar_custAddlInt2;
	private transient int oldVar_custAddlInt3;
	private transient int oldVar_custAddlInt4;
	private transient int oldVar_custAddlInt5;
	private transient boolean validationOn;
	private boolean notes_Entered=false;

	private transient String CUSTCIF_REGEX;// Customer CIF Regexion Declaration

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerMaintenanceDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 				// autoWired
	protected Button btnEdit; 				// autoWired
	protected Button btnDelete; 			// autoWired
	protected Button btnSave; 				// autoWired
	protected Button btnCancel; 			// autoWired
	protected Button btnClose; 				// autoWired
	protected Button btnHelp; 				// autoWired
	protected Button btnNotes; 				// autoWired

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
	
	// Customer Tabs Declaration
	private Tab basicDetails;
    private Tab genericInformation;
	private Tab otherDetails;
	private Tab additionalDetails;
	
	// ServiceDAOs / Domain Classes
	private transient CustomerService customerService;
	private String module="";

	Date endDate=(Date) SystemParameterDetails.getSystemParameterValue("APP_DFT_END_DATE");
	Date startDate = (Date)SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE");
	Date appStartDate=(Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
	/**
	 * default constructor.<br>
	 */
	public CustomerMaintenanceDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Customer object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerMaintenanceDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, 
				true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, 
				this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("customer")) {
			this.customer = (Customer) args.get("customer");
			Customer befImage = new Customer();
			BeanUtils.copyProperties(this.customer, befImage);
			this.customer.setBefImage(befImage);
			setCustomer(this.customer);
		} else {
			setCustomer(null);
		}
		
		if (args.containsKey("moduleType")) {
			this.module = (String) args.get("moduleType");
		} 
		
		if("ENQ".equals(this.module)){
			doLoadWorkFlow(false,this.customer.getWorkflowId(), this.customer.getNextTaskId());
		}else{
			doLoadWorkFlow(this.customer.isWorkflow(),this.customer.getWorkflowId(),
					this.customer.getNextTaskId());
		}

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CustomerMaintenanceDialog");
		}

		if (args.containsKey("customerMaintenanceListCtrl")) {
			setCustomerMaintenaceListCtrl((CustomerMaintenanceListCtrl) args.get(
					"customerMaintenanceListCtrl"));
		} else {
			setCustomerMaintenaceListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomer());
		logger.debug("Leaving" + event.toString());
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
		
		//Other Details Tab-->1.Segmentation Details
		this.custCtgCode.setMaxlength(8);
		this.custIndustry.setMaxlength(8);
		this.custSector.setMaxlength(8);
		this.custSubSector.setMaxlength(8);
		this.custSegment.setMaxlength(8);
		this.custSubSegment.setMaxlength(8);
		
		//Other Details Tab-->2.Identity Details
		this.custParentCountry.setMaxlength(2);
		this.custResdCountry.setMaxlength(2);
		this.custRiskCountry.setMaxlength(2);
		this.custNationality.setMaxlength(2);
		
		//Other Details Tab-->3.Non-Financial RelationShip Details
		this.custDSA.setMaxlength(8);
		this.custDSADept.setMaxlength(8);
		this.custRO1.setMaxlength(8);
		this.custRO2.setMaxlength(8);
		this.custReferedBy.setMaxlength(50);

		//Other Details Tab-->4.Statement Details	
		this.custStmtFrq.setMaxlength(8);
		this.custStmtNextDate.setFormat(PennantConstants.dateFormat);
		this.custStmtLastDate.setFormat(PennantConstants.dateFormat);
		this.custStmtDispatchMode.setMaxlength(2);
		
		//Additional Details Tab
		this.custAddlVar81.setMaxlength(8);
		this.custAddlVar82.setMaxlength(8);
		this.custAddlVar83.setMaxlength(8);
		this.custAddlVar84.setMaxlength(8);
		this.custAddlVar85.setMaxlength(8);
		this.custAddlVar86.setMaxlength(8);
		this.custAddlVar87.setMaxlength(8);
		this.custAddlVar88.setMaxlength(8);
		this.custAddlVar89.setMaxlength(8);
		this.custAddlVar1.setMaxlength(50);
		this.custAddlVar2.setMaxlength(50);
		this.custAddlVar3.setMaxlength(50);
		this.custAddlVar4.setMaxlength(50);
		this.custAddlVar5.setMaxlength(50);
		this.custAddlVar6.setMaxlength(50);
		this.custAddlVar7.setMaxlength(50);
		this.custAddlVar8.setMaxlength(50);
		this.custAddlVar9.setMaxlength(50);
		this.custAddlVar10.setMaxlength(100);
		this.custAddlVar11.setMaxlength(100);
		this.custAddlDec1.setMaxlength(13);
		this.custAddlDec1.setFormat(PennantApplicationUtil.getAmountFormate(9));
		this.custAddlDec1.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.custAddlDec1.setScale(9);
		this.custAddlDec2.setMaxlength(13);
		this.custAddlDec2.setFormat(PennantApplicationUtil.getAmountFormate(9));
		this.custAddlDec2.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.custAddlDec2.setScale(9);
		this.custAddlDec3.setMaxlength(13);
		this.custAddlDec3.setFormat(PennantApplicationUtil.getAmountFormate(9));
		this.custAddlDec3.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.custAddlDec3.setScale(9);
		this.custAddlDec4.setMaxlength(13);
		this.custAddlDec4.setFormat(PennantApplicationUtil.getAmountFormate(9));
		this.custAddlDec4.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.custAddlDec4.setScale(9);
		this.custAddlDec5.setMaxlength(13);
		this.custAddlDec5.setFormat(PennantApplicationUtil.getAmountFormate(9));
		this.custAddlDec5.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.custAddlDec5.setScale(9);
		this.custAddlInt1.setMaxlength(21);
		this.custAddlInt2.setMaxlength(21);
		this.custAddlInt3.setMaxlength(21);
		this.custAddlInt4.setMaxlength(21);
		this.custAddlInt5.setMaxlength(21);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
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
		getUserWorkspace().alocateAuthorities("CustomerMaintenanceDialog");

		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerMaintenanceDialog_btnEdit"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerMaintenanceDialog_btnSave"));
		this.btnCancel.setVisible(false);
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
	public void onClose$window_CustomerMaintenanceDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
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
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		// remember the old variables
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
		PTMessageUtils.showHelpWindow(event, window_CustomerMaintenanceDialog);
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
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
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
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		boolean close = true;

		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, 
					MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("Data Changed(): false");
		}
		if(close){
			closeDialog(this.window_CustomerMaintenanceDialog, "Customer");
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
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setBtnStatus_Save();
		this.btnEdit.setVisible(true);
		//this.btnNew.setVisible(true);
		this.btnDelete.setVisible(false);
		this.btnCancel.setVisible(false);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomer
	 *            CustomerMaintenance
	 */
	public void doWriteBeanToComponents(Customer aCustomer) {
		logger.debug("Entering");
		
		//Basic Details Tab-->1.Key Details
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(StringUtils.trimToEmpty(aCustomer.getCustCIF()));
		this.custCoreBank.setValue(aCustomer.getCustCoreBank());
		this.custTypeCode.setValue(aCustomer.getCustTypeCode());
		this.custDftBranch.setValue(aCustomer.getCustDftBranch());
		this.custGroupID.setValue(aCustomer.getCustGroupID());
		this.custBaseCcy.setValue(aCustomer.getCustBaseCcy());
		this.custLng.setValue(aCustomer.getCustLng());
		
		//Basic Details Tab-->2.Personal Details(Retail Customer)
		if(aCustomer.getLovDescCustCtgType().equals("I")){
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
		if(aCustomer.getLovDescCustCtgType().equals("C")){
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
		
		//Other Details Tab-->1.Segmentation Details
		this.custCtgCode.setValue(aCustomer.getCustCtgCode());
		this.custIndustry.setValue(aCustomer.getCustIndustry());
		this.custSector.setValue(aCustomer.getCustSector());
		this.custSubSector.setValue(aCustomer.getCustSubSector());
		this.custSegment.setValue(aCustomer.getCustSegment());
		this.custSubSegment.setValue(aCustomer.getCustSubSegment());
		
		//Other Details Tab-->2.Identity Details
		this.custParentCountry.setValue(aCustomer.getCustParentCountry());
		this.custResdCountry.setValue(aCustomer.getCustResdCountry());
		this.custRiskCountry.setValue(aCustomer.getCustRiskCountry());
		this.custNationality.setValue(aCustomer.getCustNationality());
		
		//Other Details Tab-->3.Non-Financial RelationShip Details
		this.custReferedBy.setValue(aCustomer.getCustReferedBy());
		this.custDSA.setValue(aCustomer.getCustDSA());
		this.custDSADept.setValue(aCustomer.getCustDSADept());
		this.custRO1.setValue(aCustomer.getCustRO1());
		this.custRO2.setValue(StringUtils.trimToNull(aCustomer.getCustRO2()));
		
		//Other Details Tab-->4.Statement Details
		this.custStmtFrq.setValue(aCustomer.getCustStmtFrq());
		if("ENQ".equals(this.module)){
			fillFrqCode(this.custStmtFrqCode, aCustomer.getCustStmtFrq(),true);
			fillFrqMth(this.custStmtFrqMth, aCustomer.getCustStmtFrq(),true);
			fillFrqDay(this.custStmtFrqDays, aCustomer.getCustStmtFrq(),true);
		}else{
			fillFrqCode(this.custStmtFrqCode, aCustomer.getCustStmtFrq(),
					isReadOnly("CustomerDialog_custStmtFrq"));
			fillFrqMth(this.custStmtFrqMth, aCustomer.getCustStmtFrq(),
					isReadOnly("CustomerDialog_custStmtFrq"));
			fillFrqDay(this.custStmtFrqDays, aCustomer.getCustStmtFrq(),
					isReadOnly("CustomerDialog_custStmtFrq"));
		}
		this.custIsStmtCombined.setChecked(aCustomer.isCustIsStmtCombined());
		this.custStmtLastDate.setValue(aCustomer.getCustStmtLastDate());
		this.custStmtNextDate.setValue(aCustomer.getCustStmtNextDate());
		this.custStmtDispatchMode.setValue(aCustomer.getCustStmtDispatchMode());
		
		//Additional Details Tab
		this.custAddlVar81.setValue(aCustomer.getCustAddlVar81());
		this.custAddlVar82.setValue(aCustomer.getCustAddlVar82());
		this.custAddlVar83.setValue(aCustomer.getCustAddlVar83());
		this.custAddlVar84.setValue(aCustomer.getCustAddlVar84());
		this.custAddlVar85.setValue(aCustomer.getCustAddlVar85());
		this.custAddlVar86.setValue(aCustomer.getCustAddlVar86());
		this.custAddlVar87.setValue(aCustomer.getCustAddlVar87());
		this.custAddlVar88.setValue(aCustomer.getCustAddlVar88());
		this.custAddlVar89.setValue(aCustomer.getCustAddlVar89());
		this.custAddlDate1.setValue(aCustomer.getCustAddlDate1());
		this.custAddlDate2.setValue(aCustomer.getCustAddlDate2());
		this.custAddlDate3.setValue(aCustomer.getCustAddlDate3());
		this.custAddlDate4.setValue(aCustomer.getCustAddlDate4());
		this.custAddlDate5.setValue(aCustomer.getCustAddlDate5());
		this.custAddlVar1.setValue(aCustomer.getCustAddlVar1());
		this.custAddlVar2.setValue(aCustomer.getCustAddlVar2());
		this.custAddlVar3.setValue(aCustomer.getCustAddlVar3());
		this.custAddlVar4.setValue(aCustomer.getCustAddlVar4());
		this.custAddlVar5.setValue(aCustomer.getCustAddlVar5());
		this.custAddlVar6.setValue(aCustomer.getCustAddlVar6());
		this.custAddlVar7.setValue(aCustomer.getCustAddlVar7());
		this.custAddlVar8.setValue(aCustomer.getCustAddlVar8());
		this.custAddlVar9.setValue(aCustomer.getCustAddlVar9());
		this.custAddlVar10.setValue(aCustomer.getCustAddlVar10());
		this.custAddlVar11.setValue(aCustomer.getCustAddlVar11());
		this.custAddlDec1.setValue(PennantAppUtil.formateAmount(new BigDecimal(
				aCustomer.getCustAddlDec1()), 9));
		this.custAddlDec2.setValue(PennantAppUtil.formateAmount(new BigDecimal(
				aCustomer.getCustAddlDec2()), 9));
		this.custAddlDec3.setValue(PennantAppUtil.formateAmount(new BigDecimal(
				aCustomer.getCustAddlDec3()), 9));
		this.custAddlDec4.setValue(PennantAppUtil.formateAmount(new BigDecimal(
				aCustomer.getCustAddlDec4()), 9));
		this.custAddlDec5.setValue(PennantAppUtil.formateAmount(new BigDecimal(
				aCustomer.getCustAddlDec5()), 9));
		this.custAddlInt1.setValue(aCustomer.getCustAddlInt1());
		this.custAddlInt2.setValue(aCustomer.getCustAddlInt2());
		this.custAddlInt3.setValue(aCustomer.getCustAddlInt3());
		this.custAddlInt4.setValue(aCustomer.getCustAddlInt4());
		this.custAddlInt5.setValue(aCustomer.getCustAddlInt5());

		if(aCustomer.isNewRecord()){
			this.lovDescCustTypeCodeName.setValue("");
			this.lovDescCustDftBranchName.setValue("");
			this.lovDesccustGroupIDName.setValue("");
			this.lovDescCustBaseCcyName.setValue("");
			this.lovDescCustLngName.setValue("");
			this.lovDescCustGenderCodeName.setValue("");
			this.lovDescCustSalutationCodeName.setValue("");
			this.lovDescCustCOBName.setValue("");
			this.lovDescCustProfessionName.setValue("");
			this.lovDescCustMaritalStsName.setValue("");
			
			this.lovDescCustEmpStsName.setValue("");
			this.lovDescCustBLRsnCodeName.setValue("");
			this.lovDescCustRejectedRsnName.setValue("");
			
			this.lovDescCustCtgCodeName.setValue("");
			this.lovDescCustSectorName.setValue("");
			this.lovDescCustSubSectorName.setValue("");
			this.lovDescCustIndustryName.setValue("");
			this.lovDescCustSegmentName.setValue("");
			this.lovDescCustSubSegmentName.setValue("");
			this.lovDescCustResdCountryName.setValue("");
			this.lovDescCustNationalityName.setValue("");
			this.lovDescCustDSADeptName.setValue("");
			this.lovDescCustRO1Name.setValue("");
			this.lovDescCustRO2Name.setValue("");
			this.lovDescDispatchModeDescName.setValue("");
		}else{
			//Basic Details Tab ----->1.Key Details: LOV Fields
			this.lovDescCustTypeCodeName.setValue(aCustomer.getLovDescCustTypeCodeName() == null ? "":
				aCustomer.getCustTypeCode()+"-"+aCustomer.getLovDescCustTypeCodeName());
			
			this.lovDescCustDftBranchName.setValue(aCustomer.getLovDescCustDftBranchName() == null ? "":
				aCustomer.getCustDftBranch()+"-"+aCustomer.getLovDescCustDftBranchName());
			
			this.lovDesccustGroupIDName.setValue(aCustomer.getCustGroupID() == 0 ? "" : aCustomer
					.getCustGroupID()+ "-"+ aCustomer.getLovDesccustGroupIDName());
			
			this.lovDescCustBaseCcyName.setValue(aCustomer.getLovDescCustBaseCcyName() == null ? "":
				aCustomer.getCustBaseCcy()+ "-"+aCustomer.getLovDescCustBaseCcyName());
			
			this.lovDescCustLngName.setValue(aCustomer.getLovDescCustLngName() == null ? "":
				 aCustomer.getCustLng()+ "-"+ aCustomer.getLovDescCustLngName());
				
			//Basic Details Tab-->2.Personal Details(Retail Customer)
			this.lovDescCustGenderCodeName.setValue(aCustomer.getLovDescCustGenderCodeName() == null ? "":
				aCustomer.getCustGenderCode()+"-"+aCustomer.getLovDescCustGenderCodeName());
			
			this.lovDescCustSalutationCodeName.setValue(aCustomer.getLovDescCustSalutationCodeName() == null ? "":
				aCustomer.getCustSalutationCode()+"-"+aCustomer.getLovDescCustSalutationCodeName());
			
			this.lovDescCustCOBName.setValue(aCustomer.getLovDescCustCOBName() == null ? "":
				aCustomer.getCustCOB()+ "-"+aCustomer.getLovDescCustCOBName());
			
			this.lovDescCustProfessionName.setValue(aCustomer.getLovDescCustProfessionName() == null ? "":
				aCustomer.getCustProfession()+ "-"+ aCustomer.getLovDescCustProfessionName());
			
			this.lovDescCustMaritalStsName.setValue(aCustomer.getLovDescCustMaritalStsName() == null ? "":
				aCustomer.getCustMaritalSts()+ "-"+ aCustomer.getLovDescCustMaritalStsName());
			
			//Basic Details Tab-->3.Organization Details(Corporate Customer)
			this.lovDescCorpCustCOBName.setValue(aCustomer.getLovDescCustCOBName() == null ? "":
				aCustomer.getCustCOB()+ "-"+aCustomer.getLovDescCustCOBName());
			
			//Generic Information Tab-->1.General Details
			
			this.lovDescCustStsName.setValue(aCustomer.getLovDescCustStsName() == null ? "":
				aCustomer.getCustSts()+ "-"+ aCustomer.getLovDescCustStsName());
			
			this.lovDescCustGroupStsName.setValue(aCustomer.getLovDescCustGroupStsName() == null ? "":
				aCustomer.getCustGroupSts()+ "-"+ aCustomer.getLovDescCustGroupStsName());
			
			this.lovDescCustEmpStsName.setValue(aCustomer.getLovDescCustEmpStsName() == null ? "":
				aCustomer.getCustEmpSts()+ "-"+ aCustomer.getLovDescCustEmpStsName());
			
			this.lovDescCustBLRsnCodeName.setValue(aCustomer.getLovDescCustBLRsnCodeName() == null ? "" : 
				aCustomer.getCustBLRsnCode() + "-"+ aCustomer.getLovDescCustBLRsnCodeName());
			
			this.lovDescCustRejectedRsnName.setValue(aCustomer.getLovDescCustRejectedRsnName() == null ? "" : 
				aCustomer.getCustRejectedRsn()+ "-"+ aCustomer.getLovDescCustRejectedRsnName());
			
			//Other Details Tab-->1.Segmentation Details
			
			this.lovDescCustCtgCodeName.setValue(aCustomer.getCustCtgCode()+"-"+
					aCustomer.getLovDescCustCtgCodeName());
			
			this.lovDescCustIndustryName.setValue(aCustomer.getLovDescCustIndustryName() == null ? "" : 
				aCustomer.getCustIndustry()+ "-"+ aCustomer.getLovDescCustIndustryName());
			
			this.lovDescCustSectorName.setValue(aCustomer.getLovDescCustSectorName() == null ? "" : 
				aCustomer.getCustSector()+ "-"+ aCustomer.getLovDescCustSectorName());
			
			this.lovDescCustSubSectorName.setValue(aCustomer.getLovDescCustSubSectorName() == null ? "" : 
				aCustomer.getCustSubSector() + "-"+ aCustomer.getLovDescCustSubSectorName());
		
			this.lovDescCustSegmentName.setValue(aCustomer.getLovDescCustSegmentName() == null  ? "" : 
				aCustomer.getCustSegment()+ "-"+ aCustomer.getLovDescCustSegmentName());
		
			this.lovDescCustSubSegmentName.setValue(aCustomer.getLovDescCustSubSegmentName() == null  ? "" :
				aCustomer.getCustSubSegment()+ "-"+ aCustomer.getLovDescCustSubSegmentName());
			
			//Other Details Tab-->2.Identity Details
		
			this.lovDescCustResdCountryName.setValue(aCustomer.getLovDescCustResdCountryName() == null  ? "" : 
				aCustomer.getCustResdCountry()+ "-"+aCustomer.getLovDescCustResdCountryName());
			
			this.lovDescCustNationalityName.setValue(aCustomer.getLovDescCustNationalityName() == null  ? "" : 
				aCustomer.getCustNationality()+ "-"+ aCustomer.getLovDescCustNationalityName());
			
			//Other Details Tab-->3.Non-Financial RelationShip Details
			
			this.lovDescCustDSADeptName.setValue(aCustomer.getLovDescCustDSADeptName() == null ? "" : 
				aCustomer.getCustDSADept()+ "-"+ aCustomer.getLovDescCustDSADeptName());
			
			this.lovDescCustRO1Name.setValue(aCustomer.getLovDescCustRO1Name() == null  ? "" : 
				aCustomer.getCustRO1()+ "-"+ aCustomer.getLovDescCustRO1Name());
			
			this.lovDescCustRO2Name.setValue(aCustomer.getLovDescCustRO2Name() == null  ? "" : 
				aCustomer.getCustRO2()+ "-"+ aCustomer.getLovDescCustRO2Name());
			
			//Other Details Tab-->4.Statement Details
			
			this.lovDescDispatchModeDescName.setValue(aCustomer.getLovDescDispatchModeDescName() == null ? "" : 
				aCustomer.getCustStmtDispatchMode()+ "-"+ aCustomer.getLovDescDispatchModeDescName());
		}
		this.recordStatus.setValue(aCustomer.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomer
	 */
	public void doWriteComponentsToBean(Customer aCustomer) {
		logger.debug("Entering");
		doSetLOVValidation();

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
			if (this.row_localLngFM.isVisible()) {
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
			if (this.row_localLngCorpCustCS.isVisible()) {
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
				aCustomer.setCustDOB(new Timestamp(this.custDateOfIncorporation.getValue().getTime()));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aCustomer.setCustTradeLicenceExpiry(this.custTradeLicenceExpiry.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		
		//For Retail Customer Details
		if(this.row_retailPPT.isVisible() && this.row_retailVisa.isVisible() && 
				this.row_custStaff.isVisible() && this.row_EmpSts.isVisible()){
			
			try {
				aCustomer.setCustPassportNo(this.custPassportNo.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aCustomer.setCustPassportExpiry(this.custPassportExpiry.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aCustomer.setCustVisaNum(this.custVisaNum.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aCustomer.setCustVisaExpiry(this.custVisaExpiry.getValue());
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
				aCustomer.setLovDescCustEmpStsName(this.lovDescCustEmpStsName.getValue());
				aCustomer.setCustEmpSts(StringUtils.trimToNull(this.custEmpSts.getValue()));
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
						new String[] { Labels.getLabel("label_CustomerMaintenanceDialog_CustFirstBusinessDate.value"),SystemParameterDetails
							.getSystemParameterValue("APP_DFT_START_DATE").toString() }));
				}
			}
			aCustomer.setCustFirstBusinessDate(this.custFirstBusinessDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		showErrorDetails(wve, genericInformation);
		// END TAB GENERIC INFORMATION

		// START TAB OTHER DETAILS
		
		//Other Details Tab-->1.Segmentation Details
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
		
		//Other Details Tab-->2.Identity Details
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

		//Other Details Tab-->3.Non-Financial RelationShip Details
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
						new String[] { Labels.getLabel("label_CustomerMaintenanceDialog_CustRO2.value") }));
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

		//Other Details Tab-->4.Statement Details
		this.custStmtFrqCode.setErrorMessage("");
		this.custStmtFrqMth.setErrorMessage("");
		this.custStmtFrqDays.setErrorMessage("");
		String frqCode = (String) this.custStmtFrqCode.getSelectedItem().getValue();
		String frqMth = (String) this.custStmtFrqMth.getSelectedItem().getValue();
		String frqDay = (String) this.custStmtFrqDays.getSelectedItem().getValue();

		boolean frqValid = true;

		try {
			if (frqCode == null || frqCode.equalsIgnoreCase("#")) {
				throw new WrongValueException( this.custStmtFrqCode,Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_CustomerMaintenanceDialog_CustStmtFrq.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
			frqValid = false;
		}

		try {
			if (frqMth == null || frqMth.equalsIgnoreCase("#")) {
				throw new WrongValueException(this.custStmtFrqMth,Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_CustomerMaintenanceDialog_custStmtFrqMth.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
			frqValid = false;
		}

		try {
			if (frqDay == null || frqDay.equalsIgnoreCase("#")) {
				throw new WrongValueException(this.custStmtFrqDays,Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_CustomerMaintenanceDialog_CustStmtFrqDay.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
			frqValid = false;
		}

		if (frqValid) {
			aCustomer.setCustStmtFrq(this.custStmtFrq.getValue());
		}

		try {
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
		showErrorDetails(wve, otherDetails);
		// END TAB OTHER DETAILS
		
		// START TAB ADDITIONAL DETAILS

		try {
			aCustomer.setCustAddlVar81(this.custAddlVar81.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar82(this.custAddlVar82.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar83(this.custAddlVar83.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar84(this.custAddlVar84.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar85(this.custAddlVar85.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar86(this.custAddlVar86.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar87(this.custAddlVar87.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar88(this.custAddlVar88.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar89(this.custAddlVar89.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlDate1(this.custAddlDate1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlDate2(this.custAddlDate2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlDate3(this.custAddlDate3.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlDate4(this.custAddlDate4.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlDate5(this.custAddlDate5.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar1(this.custAddlVar1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar2(this.custAddlVar2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar3(this.custAddlVar3.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar4(this.custAddlVar4.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar5(this.custAddlVar5.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar6(this.custAddlVar6.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar7(this.custAddlVar7.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar8(this.custAddlVar8.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar9(this.custAddlVar9.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar10(this.custAddlVar10.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlVar11(this.custAddlVar11.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custAddlDec1.getValue() != null) {
				aCustomer.setCustAddlDec1(PennantAppUtil.unFormateAmount(
						this.custAddlDec1.getValue(), 9).doubleValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custAddlDec2.getValue() != null) {
				aCustomer.setCustAddlDec2(PennantAppUtil.unFormateAmount(
						this.custAddlDec2.getValue(), 9).doubleValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custAddlDec3.getValue() != null) {
				aCustomer.setCustAddlDec3(PennantAppUtil.unFormateAmount(
						this.custAddlDec3.getValue(), 9).doubleValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custAddlDec4.getValue() != null) {
				aCustomer.setCustAddlDec4(PennantAppUtil.unFormateAmount(
						this.custAddlDec4.getValue(), 9).doubleValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custAddlDec5.getValue() != null) {
				aCustomer.setCustAddlDec5(PennantAppUtil.unFormateAmount(
						this.custAddlDec5.getValue(), 9).doubleValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlInt1(this.custAddlInt1.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlInt2(this.custAddlInt2.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlInt3(this.custAddlInt3.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlInt4(this.custAddlInt4.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustAddlInt5(this.custAddlInt5.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		showErrorDetails(wve, additionalDetails);
		aCustomer.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occurred
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
	public void doShowDialog(Customer aCustomer) throws InterruptedException {
		logger.debug("Entering");
		// if aCustomer == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aCustomer == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aCustomer = getCustomerService().getNewCustomer();
			aCustomer.setNewRecord(true);
			setCustomer(aCustomer);
		} else {
			setCustomer(aCustomer);
			customer.setNewRecord(false);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCustomer.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.btnCancel.setVisible(false);
			// setFocus
			this.custCoreBank.focus();
		} else {
			if (isWorkFlowEnabled()) {
				if (!StringUtils.trimToEmpty(aCustomer.getRecordType()).equals("")) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				this.btnCancel.setVisible(false);
				this.btnEdit.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCustomer);
			doCheckValidations();
			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CustomerMaintenanceDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member variables. <br>
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
		
		//Other Details Tab-->1.Segmentation Details
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
		
		//Other Details Tab-->2.Identity Details
		this.oldVar_custParentCountry = this.custParentCountry.getValue();
		this.oldVar_custResdCountry = this.custResdCountry.getValue();
		this.oldVar_lovDescCustResdCountryName = this.lovDescCustResdCountryName.getValue();
		this.oldVar_custRiskCountry = this.custRiskCountry.getValue();
		this.oldVar_custNationality = this.custNationality.getValue();
		this.oldVar_lovDescCustNationalityName = this.lovDescCustNationalityName.getValue();
		
		//Other Details Tab-->3.Non-Financial RelationShip Details
		this.oldVar_custDSA = this.custDSA.getValue();
		this.oldVar_custDSADept = this.custDSADept.getValue();
		this.oldVar_lovDescCustDSADeptName = this.lovDescCustDSADeptName.getValue();
		this.oldVar_custRO1 = this.custRO1.getValue();
		this.oldVar_lovDescCustRO1Name = this.lovDescCustRO1Name.getValue();
		this.oldVar_custRO2 = this.custRO2.getValue();
		this.oldVar_lovDescCustRO2Name = this.lovDescCustRO2Name.getValue();
		this.oldVar_custReferedBy = this.custReferedBy.getValue();

		//Other Details Tab-->4.Statement Details
		this.oldVar_custStmtFrq = this.custStmtFrq.getValue();
		this.oldVar_custIsStmtCombined = this.custIsStmtCombined.isChecked();
		this.oldVar_custStmtLastDate = this.custStmtLastDate.getValue();
		this.oldVar_custStmtNextDate = this.custStmtNextDate.getValue();
		this.oldVar_custStmtDispatchMode = this.custStmtDispatchMode.getValue();
		this.oldVar_lovDescDispatchModeDescName = this.lovDescDispatchModeDescName.getValue();
		
		//Additional Details Tab
		this.oldVar_custAddlVar81 = this.custAddlVar81.getValue();
		this.oldVar_custAddlVar82 = this.custAddlVar82.getValue();
		this.oldVar_custAddlVar83 = this.custAddlVar83.getValue();
		this.oldVar_custAddlVar84 = this.custAddlVar84.getValue();
		this.oldVar_custAddlVar85 = this.custAddlVar85.getValue();
		this.oldVar_custAddlVar86 = this.custAddlVar86.getValue();
		this.oldVar_custAddlVar87 = this.custAddlVar87.getValue();
		this.oldVar_custAddlVar88 = this.custAddlVar88.getValue();
		this.oldVar_custAddlVar89 = this.custAddlVar89.getValue();
		this.oldVar_custAddlDate1 = this.custAddlDate1.getValue();
		this.oldVar_custAddlDate2 = this.custAddlDate2.getValue();
		this.oldVar_custAddlDate3 = this.custAddlDate3.getValue();
		this.oldVar_custAddlDate4 = this.custAddlDate4.getValue();
		this.oldVar_custAddlDate5 = this.custAddlDate5.getValue();
		this.oldVar_custAddlVar1 = this.custAddlVar1.getValue();
		this.oldVar_custAddlVar2 = this.custAddlVar2.getValue();
		this.oldVar_custAddlVar3 = this.custAddlVar3.getValue();
		this.oldVar_custAddlVar4 = this.custAddlVar4.getValue();
		this.oldVar_custAddlVar5 = this.custAddlVar5.getValue();
		this.oldVar_custAddlVar6 = this.custAddlVar6.getValue();
		this.oldVar_custAddlVar7 = this.custAddlVar7.getValue();
		this.oldVar_custAddlVar8 = this.custAddlVar8.getValue();
		this.oldVar_custAddlVar9 = this.custAddlVar9.getValue();
		this.oldVar_custAddlVar10 = this.custAddlVar10.getValue();
		this.oldVar_custAddlVar11 = this.custAddlVar11.getValue();
		this.oldVar_custAddlDec1 = this.custAddlDec1.doubleValue();
		this.oldVar_custAddlDec2 = this.custAddlDec2.doubleValue();
		this.oldVar_custAddlDec3 = this.custAddlDec3.doubleValue();
		this.oldVar_custAddlDec4 = this.custAddlDec4.doubleValue();
		this.oldVar_custAddlDec5 = this.custAddlDec5.doubleValue();
		this.oldVar_custAddlInt1 = this.custAddlInt1.intValue();
		this.oldVar_custAddlInt2 = this.custAddlInt2.intValue();
		this.oldVar_custAddlInt3 = this.custAddlInt3.intValue();
		this.oldVar_custAddlInt4 = this.custAddlInt4.intValue();
		this.oldVar_custAddlInt5 = this.custAddlInt5.intValue();
		
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member variables. <br>
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
		
		//Other Details Tab-->1.Segmentation Details
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
		
		//Other Details Tab-->2.Identity Details
		this.custParentCountry.setValue(this.oldVar_custParentCountry);
		this.custResdCountry.setValue(this.oldVar_custResdCountry);
		this.lovDescCustResdCountryName.setValue(this.oldVar_lovDescCustResdCountryName);
		this.custRiskCountry.setValue(this.oldVar_custRiskCountry);
		this.custNationality.setValue(this.oldVar_custNationality);
		this.lovDescCustNationalityName.setValue(this.oldVar_lovDescCustNationalityName);
		
		//Other Details Tab-->3.Non-Financial RelationShip Details
		this.custDSA.setValue(this.oldVar_custDSA);
		this.custDSADept.setValue(this.oldVar_custDSADept);
		this.lovDescCustDSADeptName.setValue(this.oldVar_lovDescCustDSADeptName);
		this.custRO1.setValue(this.oldVar_custRO1);
		this.lovDescCustRO1Name.setValue(this.oldVar_lovDescCustRO1Name);
		this.custRO2.setValue(this.oldVar_custRO2);
		this.lovDescCustRO2Name.setValue(this.oldVar_lovDescCustRO2Name);
		this.custReferedBy.setValue(this.oldVar_custReferedBy);
		
		//Other Details Tab-->4.Statement Details
		this.custStmtFrq.setValue(this.oldVar_custStmtFrq);
		this.custIsStmtCombined.setChecked(this.oldVar_custIsStmtCombined);
		this.custStmtLastDate.setValue(this.oldVar_custStmtLastDate);
		this.custStmtNextDate.setValue(this.oldVar_custStmtNextDate);
		this.custStmtDispatchMode.setValue(this.oldVar_custStmtDispatchMode);
		this.lovDescDispatchModeDescName.setValue(this.oldVar_lovDescDispatchModeDescName);
		
		//Additional Details Tab
		this.custAddlVar81.setValue(this.oldVar_custAddlVar81);
		this.custAddlVar82.setValue(this.oldVar_custAddlVar82);
		this.custAddlVar83.setValue(this.oldVar_custAddlVar83);
		this.custAddlVar84.setValue(this.oldVar_custAddlVar84);
		this.custAddlVar85.setValue(this.oldVar_custAddlVar85);
		this.custAddlVar86.setValue(this.oldVar_custAddlVar86);
		this.custAddlVar87.setValue(this.oldVar_custAddlVar87);
		this.custAddlVar88.setValue(this.oldVar_custAddlVar88);
		this.custAddlVar89.setValue(this.oldVar_custAddlVar89);
		this.custAddlDate1.setValue(this.oldVar_custAddlDate1);
		this.custAddlDate2.setValue(this.oldVar_custAddlDate2);
		this.custAddlDate3.setValue(this.oldVar_custAddlDate3);
		this.custAddlDate4.setValue(this.oldVar_custAddlDate4);
		this.custAddlDate5.setValue(this.oldVar_custAddlDate5);
		this.custAddlVar1.setValue(this.oldVar_custAddlVar1);
		this.custAddlVar2.setValue(this.oldVar_custAddlVar2);
		this.custAddlVar3.setValue(this.oldVar_custAddlVar3);
		this.custAddlVar4.setValue(this.oldVar_custAddlVar4);
		this.custAddlVar5.setValue(this.oldVar_custAddlVar5);
		this.custAddlVar6.setValue(this.oldVar_custAddlVar6);
		this.custAddlVar7.setValue(this.oldVar_custAddlVar7);
		this.custAddlVar8.setValue(this.oldVar_custAddlVar8);
		this.custAddlVar9.setValue(this.oldVar_custAddlVar9);
		this.custAddlVar10.setValue(this.oldVar_custAddlVar10);
		this.custAddlVar11.setValue(this.oldVar_custAddlVar11);
		this.custAddlDec1.setValue(new BigDecimal(this.oldVar_custAddlDec1));
		this.custAddlDec2.setValue(new BigDecimal(this.oldVar_custAddlDec2));
		this.custAddlDec3.setValue(new BigDecimal(this.oldVar_custAddlDec3));
		this.custAddlDec4.setValue(new BigDecimal(this.oldVar_custAddlDec4));
		this.custAddlDec5.setValue(new BigDecimal(this.oldVar_custAddlDec5));
		this.custAddlInt1.setValue(this.oldVar_custAddlInt1);
		this.custAddlInt2.setValue(this.oldVar_custAddlInt2);
		this.custAddlInt3.setValue(this.oldVar_custAddlInt3);
		this.custAddlInt4.setValue(this.oldVar_custAddlInt4);
		this.custAddlInt5.setValue(this.oldVar_custAddlInt5);

		if(isWorkFlowEnabled()){
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
			String oldCustDOB = "";
			String newCustDOB = "";
			if (this.oldVar_custDOB != null) {
				oldCustDOB = DateUtility.formatDate(this.oldVar_custDOB,PennantConstants.dateFormat);
			}
			if (this.custDOB.getValue() != null) {
				newCustDOB = DateUtility.formatDate(this.custDOB.getValue(),PennantConstants.dateFormat);
			}
			if (!StringUtils.trimToEmpty(oldCustDOB).equals(StringUtils.trimToEmpty(newCustDOB))) {
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
		String oldCustClosedOn = "";
		String newCustClosedOn = "";
		if (this.oldVar_custClosedOn != null) {
			oldCustClosedOn = DateUtility.formatDate(this.oldVar_custClosedOn,
					PennantConstants.dateFormat);
		}
		if (this.custClosedOn.getValue() != null) {
			newCustClosedOn = DateUtility.formatDate(
					this.custClosedOn.getValue(), PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldCustClosedOn).equals(
				StringUtils.trimToEmpty(newCustClosedOn))) {
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
			String oldCustDateOfIncorporation = "";
			String newCustDateOfIncorporation = "";
			if (this.oldVar_custDateOfIncorporation != null) {
				oldCustDateOfIncorporation = DateUtility.formatDate(
						this.oldVar_custDateOfIncorporation,PennantConstants.dateFormat);
			}
			if (this.custDateOfIncorporation.getValue() != null) {
				newCustDateOfIncorporation = DateUtility.formatDate(
						this.custDateOfIncorporation.getValue(),PennantConstants.dateFormat);
			}
			if (!StringUtils.trimToEmpty(oldCustDateOfIncorporation).equals(
					StringUtils.trimToEmpty(newCustDateOfIncorporation))) {
				return true;
			}
		}
		String oldCustFirstBusinessDate = "";
		String newCustFirstBusinessDate = "";
		if (this.oldVar_custFirstBusinessDate != null) {
			oldCustFirstBusinessDate = DateUtility.formatDate(
					this.oldVar_custFirstBusinessDate, PennantConstants.dateFormat);
		}
		if (this.custFirstBusinessDate.getValue() != null) {
			newCustFirstBusinessDate = DateUtility.formatDate(
					this.custFirstBusinessDate.getValue(), PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldCustFirstBusinessDate).equals(
				StringUtils.trimToEmpty(newCustFirstBusinessDate))) {
			return true;
		}
		
		//Other Details Tab-->1.Segmentation Details
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
		
		//Other Details Tab-->2.Identity Details
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
		
		//Other Details Tab-->3.Non-Financial RelationShip Details
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
		
		//Other Details Tab-->4.Statement Details
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
		
		//Additional Details Tab
		if (this.oldVar_custAddlVar81 != this.custAddlVar81.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlVar82 != this.custAddlVar82.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlVar83 != this.custAddlVar83.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlVar84 != this.custAddlVar84.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlVar85 != this.custAddlVar85.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlVar86 != this.custAddlVar86.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlVar87 != this.custAddlVar87.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlVar88 != this.custAddlVar88.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlVar89 != this.custAddlVar89.getValue()) {
			return true;
		}
		String oldCustAddlDate1 = "";
		String newCustAddlDate1 = "";
		if (this.oldVar_custAddlDate1 != null) {
			oldCustAddlDate1 = DateUtility.formatDate(this.oldVar_custAddlDate1,
					PennantConstants.dateFormat);
		}
		if (this.custAddlDate1.getValue() != null) {
			newCustAddlDate1 = DateUtility.formatDate(
					this.custAddlDate1.getValue(), PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldCustAddlDate1).equals(
				StringUtils.trimToEmpty(newCustAddlDate1))) {
			return true;
		}
		String oldCustAddlDate2 = "";
		String newCustAddlDate2 = "";
		if (this.oldVar_custAddlDate2 != null) {
			oldCustAddlDate2 = DateUtility.formatDate(this.oldVar_custAddlDate2,
					PennantConstants.dateFormat);
		}
		if (this.custAddlDate2.getValue() != null) {
			newCustAddlDate2 = DateUtility.formatDate(
					this.custAddlDate2.getValue(), PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldCustAddlDate2).equals(
				StringUtils.trimToEmpty(newCustAddlDate2))) {
			return true;
		}
		String oldCustAddlDate3 = "";
		String newCustAddlDate3 = "";
		if (this.oldVar_custAddlDate3 != null) {
			oldCustAddlDate3 = DateUtility.formatDate(this.oldVar_custAddlDate3,
					PennantConstants.dateFormat);
		}
		if (this.custAddlDate3.getValue() != null) {
			newCustAddlDate3 = DateUtility.formatDate(
					this.custAddlDate3.getValue(), PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldCustAddlDate3).equals(
				StringUtils.trimToEmpty(newCustAddlDate3))) {
			return true;
		}
		String oldCustAddlDate4 = "";
		String newCustAddlDate4 = "";
		if (this.oldVar_custAddlDate4 != null) {
			oldCustAddlDate4 = DateUtility.formatDate(this.oldVar_custAddlDate4,
					PennantConstants.dateFormat);
		}
		if (this.custAddlDate4.getValue() != null) {
			newCustAddlDate4 = DateUtility.formatDate(
					this.custAddlDate4.getValue(), PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldCustAddlDate4).equals(
				StringUtils.trimToEmpty(newCustAddlDate4))) {
			return true;
		}
		String oldCustAddlDate5 = "";
		String newCustAddlDate5 = "";
		if (this.oldVar_custAddlDate5 != null) {
			oldCustAddlDate5 = DateUtility.formatDate(this.oldVar_custAddlDate5,
					PennantConstants.dateFormat);
		}
		if (this.custAddlDate5.getValue() != null) {
			newCustAddlDate5 = DateUtility.formatDate(
					this.custAddlDate5.getValue(), PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldCustAddlDate5).equals(
				StringUtils.trimToEmpty(newCustAddlDate5))) {
			return true;
		}
		if (this.oldVar_custAddlVar1 != this.custAddlVar1.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlVar2 != this.custAddlVar2.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlVar3 != this.custAddlVar3.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlVar4 != this.custAddlVar4.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlVar5 != this.custAddlVar5.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlVar6 != this.custAddlVar6.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlVar7 != this.custAddlVar7.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlVar8 != this.custAddlVar8.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlVar9 != this.custAddlVar9.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlVar10 != this.custAddlVar10.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlVar11 != this.custAddlVar11.getValue()) {
			return true;
		}
		if (this.oldVar_custAddlDec1 != this.custAddlDec1.doubleValue()) {
			return true;
		}
		if (this.oldVar_custAddlDec2 != this.custAddlDec2.doubleValue()) {
			return true;
		}
		if (this.oldVar_custAddlDec3 != this.custAddlDec3.doubleValue()) {
			return true;
		}
		if (this.oldVar_custAddlDec4 != this.custAddlDec4.doubleValue()) {
			return true;
		}
		if (this.oldVar_custAddlDec5 != this.custAddlDec5.doubleValue()) {
			return true;
		}
		if (this.oldVar_custAddlInt1 != this.custAddlInt1.intValue()) {
			return true;
		}
		if (this.oldVar_custAddlInt2 != this.custAddlInt2.intValue()) {
			return true;
		}
		if (this.oldVar_custAddlInt3 != this.custAddlInt3.intValue()) {
			return true;
		}
		if (this.oldVar_custAddlInt4 != this.custAddlInt4.intValue()) {
			return true;
		}
		if (this.oldVar_custAddlInt5 != this.custAddlInt5.intValue()) {
			return true;
		}
		return false;
	
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		doClearErrorMessage();

		//Basic Details Tab-->1.Key Details
		if (!this.custCIF.isReadonly()) {
			this.custCIF.setConstraint(new SimpleConstraint(this.CUSTCIF_REGEX,Labels.getLabel(
				"MAND_FIELD_ALLOWED_CHARS",new String[] {Labels.getLabel(
						"label_CustomerMaintenanceDialog_CustCIF.value"),parms[0], parms[1] })));
		}
		if (!this.custCoreBank.isReadonly()) {
			this.custCoreBank.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustCoreBank.value"),null,true));
		}
		
		//Basic Details Tab-->2.Personal Details(Retail Customer)
		if (this.gb_personalDetails.isVisible()) {
			if (!this.custFName.isReadonly()) {
				this.custFName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustFName.value"), 
						PennantRegularExpressions.REGEX_NAME, true));
			}
			if (!this.custMName.isReadonly()) {
				this.custMName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustMName.value"), PennantRegularExpressions.REGEX_NAME, false));
			}
			if (!this.custLName.isReadonly()) {
				this.custLName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustFName.value"), 
						PennantRegularExpressions.REGEX_NAME, true));
			}
			if (!this.custShrtName.isReadonly()) {
				this.custShrtName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustShrtName.value"), 
						PennantRegularExpressions.REGEX_NAME, true));
			}
			if (!this.custPOB.isReadonly() && !this.custPOB.isDisabled()) {
				this.custPOB.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustPOB.value"), 
						PennantRegularExpressions.REGEX_NAME, true));
			}
			if (!this.custDOB.isReadonly() && !this.custDOB.isDisabled()) {
				this.custDOB.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustDOB.value"),true,startDate,appStartDate,false));
			}
			if (!this.custMotherMaiden.isReadonly()) {
				this.custMotherMaiden.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustMotherMaiden.value"), 
						PennantRegularExpressions.REGEX_NAME, true));
			}
			if (!(this.custLng.getValue().equals(PennantConstants.default_Language))
					&& this.row_localLngFM.isVisible()) {

				this.custFNameLclLng.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustFNameLclLng.value"),
						PennantRegularExpressions.REGEX_NAME, true));

				if (!(this.custMName.getValue().equals(""))) {
					this.custMNameLclLng.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustMNameLclLng.value"),
							PennantRegularExpressions.REGEX_NAME, true));
				}

				this.custLNameLclLng.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustLNameLclLng.value"), 
						PennantRegularExpressions.REGEX_NAME, true));

				this.custShrtNameLclLng.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustShrtNameLclLng.value"), 
						PennantRegularExpressions.REGEX_NAME, true));

			}
		} else if(this.gb_corporateCustomerPersonalDetails.isVisible()){
			//Basic Details Tab-->3.Organization Details(Corporate Customer)
			if (!this.corpCustOrgName.isReadonly()) {
				this.corpCustOrgName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustLNameLclLng.value"), 
						PennantRegularExpressions.REGEX_NAME, true));
			}
			if (!this.corpCustShrtName.isReadonly()) {
				this.corpCustShrtName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustShrtNameLclLng.value"), 
						PennantRegularExpressions.REGEX_NAME, true));
			}
			if (!this.corpCustPOB.isReadonly()) {
				this.corpCustPOB.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CorpCustPOB.value"),
						PennantRegularExpressions.REGEX_NAME, true));
			}
			if (!(this.custLng.getValue().equals(PennantConstants.default_Language))
					&& this.row_localLngCorpCustCS.isVisible()) {
				this.corpCustOrgNameLclLng.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustOrgNameLclLng.value"), 
						PennantRegularExpressions.REGEX_NAME, true));

				this.corpCustShrtNameLclLng.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustShrtNameLclLng.value"), 
						PennantRegularExpressions.REGEX_NAME, true));

			}
		}

		// Generic Information Tab
		if(this.row_retailPPT.isVisible() && this.row_retailVisa.isVisible() && 
				this.row_custStaff.isVisible() && this.row_EmpSts.isVisible()){
			//For Retail Customer
			if(getCustomer().getLovDescCustCtgType().equals("I")){
				if (!this.custPassportNo.isReadonly()) {
					this.custPassportNo.setConstraint(new SimpleConstraint(PennantConstants.PPT_VISA_REGEX,
						Labels.getLabel("MAND_FIELD_CHAR_NUMBER",new String[] { Labels.getLabel(
								"label_CustomerMaintenanceDialog_CustPassportNo.value") })));
				}
				if (!this.custPassportExpiry.isDisabled() && !(custPassportNo.getValue().equals(""))) {
					this.custPassportExpiry.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustPassportExpiryDate.value"),true,appStartDate,endDate,false));
				}
				if (!this.custVisaNum.isReadonly()) {
					this.custVisaNum.setConstraint(new SimpleConstraint(PennantConstants.PPT_VISA_REGEX,
						Labels.getLabel("MAND_FIELD_CHAR_NUMBER",new String[] { Labels.getLabel(
								"label_CustomerMaintenanceDialog_CustVisaNum.value") })));
				}
				if (!this.custVisaExpiry.isDisabled() && !this.custVisaNum.getValue().equals("")) {
					this.custVisaExpiry.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustVisaExpiryDate.value"),true,appStartDate,endDate,false));
				}
				if (this.custIsStaff.isChecked()) {
					this.custStaffID.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustStaffID.value"), 
							PennantRegularExpressions.REGEX_ALPHANUM, true));
				}
			}
			
		}else if(this.row_corpTL.isVisible() && this.row_corpTLED.isVisible()){
			if(getCustomer().getLovDescCustCtgType().equals("C")){
				if (!this.custTradeLicenceNum.isReadonly()) {
					this.custTradeLicenceNum.setConstraint(new SimpleConstraint(PennantConstants.TRADE_LICENSE_REGEX,
						Labels.getLabel("MAND_FIELD_CHAR_NUMBER",new String[] { Labels.getLabel(
								"label_CustomerMaintenanceDialog_CustTradeLicenceNum.value") })));
				}
				if (!this.custTradeLicenceExpiry.isReadonly() && !this.custTradeLicenceNum.getValue().equals("")) {
					this.custTradeLicenceExpiry.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustTradeLicenceExpiryDate.value"),true,appStartDate,endDate,false));
				}
				if (!this.custDateOfIncorporation.isReadonly()) {
					this.custDateOfIncorporation.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustDateOfIncorporation.value"),true,startDate,appStartDate,false));
				}
			}
		}
		if (!this.custIsActive.isChecked()) {
			this.custInactiveReason.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustInactiveReason.value"), 
					PennantRegularExpressions.REGEX_NAME, true));
		}
		if (custIsClosed.isChecked()) {
			this.custClosedOn.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustClosedOn.value"),true));
		}
		if (custIsBlackListed.isChecked()) {
			this.lovDescCustBLRsnCodeName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustBLRsnCode.value"),null,true));
		}
		if (custIsRejected.isChecked()) {
			this.lovDescCustRejectedRsnName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustRejectedRsn.value"),null,true));
		}
		
		// Preferential Details Tab
		if (!this.custDSA.isReadonly()) {
			this.custDSA.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustDSA.value"), 
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.custReferedBy.isReadonly() && !this.custReferedBy.getValue().equals("")) {
			this.custReferedBy.setConstraint(new SimpleConstraint(this.CUSTCIF_REGEX,Labels.getLabel(
				"FIELD_ALLOWED_CHARS",new String[] {Labels.getLabel(
						"label_CustomerMaintenanceDialog_CustReferedBy.value"),parms[0], parms[1] })));
		}
		if (this.custStmtNextDate.getValue() != null) {
			this.custStmtNextDate.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustStmtNextDate.value"),true,appStartDate,endDate,false));
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
		this.custAddlVar81.setConstraint("");
		this.custAddlVar82.setConstraint("");
		this.custAddlVar83.setConstraint("");
		this.custAddlVar84.setConstraint("");
		this.custAddlVar85.setConstraint("");
		this.custAddlVar86.setConstraint("");
		this.custAddlVar87.setConstraint("");
		this.custAddlVar88.setConstraint("");
		this.custAddlVar89.setConstraint("");
		this.custAddlDate1.setConstraint("");
		this.custAddlDate2.setConstraint("");
		this.custAddlDate3.setConstraint("");
		this.custAddlDate4.setConstraint("");
		this.custAddlDate5.setConstraint("");
		this.custAddlVar1.setConstraint("");
		this.custAddlVar2.setConstraint("");
		this.custAddlVar3.setConstraint("");
		this.custAddlVar4.setConstraint("");
		this.custAddlVar5.setConstraint("");
		this.custAddlVar6.setConstraint("");
		this.custAddlVar7.setConstraint("");
		this.custAddlVar8.setConstraint("");
		this.custAddlVar9.setConstraint("");
		this.custAddlVar10.setConstraint("");
		this.custAddlVar11.setConstraint("");
		this.custAddlDec1.setConstraint("");
		this.custAddlDec2.setConstraint("");
		this.custAddlDec3.setConstraint("");
		this.custAddlDec4.setConstraint("");
		this.custAddlDec5.setConstraint("");
		this.custAddlInt1.setConstraint("");
		this.custAddlInt2.setConstraint("");
		this.custAddlInt3.setConstraint("");
		this.custAddlInt4.setConstraint("");
		this.custAddlInt5.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Method to set LOV validation on
	 * */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		
		//Basic Details Tab-->1.Key Details
		this.lovDescCustTypeCodeName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustTypeCode.value"),null,true));

		this.lovDescCustDftBranchName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustDftBranch.value"),null,true));

		this.lovDescCustBaseCcyName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustBaseCcy.value"),null,true));

		this.lovDescCustLngName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustLng.value"),null,true));
		
		if (this.gb_personalDetails.isVisible()) {
			//Basic Details Tab-->2.Personal Details(Retail Customer)

			this.lovDescCustGenderCodeName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustGenderCode.value"),null,true));

			this.lovDescCustSalutationCodeName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustSalutationCode.value"),null,true));

			this.lovDescCustCOBName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustCOB.value"),null,true));

			this.lovDescCustMaritalStsName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustMaritalSts.value"),null,true));

			this.lovDescCustProfessionName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustProfession.value") ,null,true));

		} else if (this.gb_corporateCustomerPersonalDetails.isVisible()) {
			//Basic Details Tab-->3.Organization Details(Corporate Customer)
			this.lovDescCorpCustCOBName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CorpCustCOB.value"),null,true));
		}
		
		//Generic Details Tab
		if (getCustomer().getLovDescCustCtgType().equals("I")) {
			this.lovDescCustEmpStsName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustEmpSts.value"),null,true));
		}
		if(this.custGroupID.getValue() != 0){
			this.lovDescCustGroupStsName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustGroupSts.value"),null,true));
		}

		//Other Details Tab-->1.Segmentation Details
		this.lovDescCustCtgCodeName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustCtgCode.value"),null,true));

		this.lovDescCustSectorName.setConstraint(new PTStringValidator( Labels.getLabel("label_CustomerMaintenanceDialog_CustSector.value"),null,true));

		this.lovDescCustSubSectorName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustSubSector.value"),null,true));
		
		this.lovDescCustIndustryName.setConstraint(new PTStringValidator( Labels.getLabel("label_CustomerMaintenanceDialog_CustIndustry.value"),null,true));

		this.lovDescCustSegmentName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustSegment.value"),null,true));

		this.lovDescCustSubSegmentName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustSubSegment.value"),null,true));
		
		//Other Details Tab -->2.Identity Details
		this.lovDescCustResdCountryName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustResdCountry.value"),null,true));

		this.lovDescCustNationalityName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustNationality.value"),null,true));

		//Other Details Tab --> 3.Non-Financial RelationShip Details
		this.lovDescCustDSADeptName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustDSADept.value"),null,true));

		this.lovDescCustRO1Name.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerMaintenanceDialog_CustRO1.value"),null,true));
		
		logger.debug("Leaving");
	}
	/**
	 * Method to remove LOV validation
	 * */
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
		
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearErrorMessage() {
		logger.debug("Entering");
		
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
		this.corpCustOrgName.setErrorMessage("");
		this.custAddlVar81.setErrorMessage("");
		this.custAddlVar82.setErrorMessage("");
		this.custAddlVar83.setErrorMessage("");
		this.custAddlVar84.setErrorMessage("");
		this.custAddlVar85.setErrorMessage("");
		this.custAddlVar86.setErrorMessage("");
		this.custAddlVar87.setErrorMessage("");
		this.custAddlVar88.setErrorMessage("");
		this.custAddlVar89.setErrorMessage("");
		this.custAddlVar1.setErrorMessage("");
		this.custAddlVar2.setErrorMessage("");
		this.custAddlVar3.setErrorMessage("");
		this.custAddlVar4.setErrorMessage("");
		this.custAddlVar5.setErrorMessage("");
		this.custAddlVar6.setErrorMessage("");
		this.custAddlVar7.setErrorMessage("");
		this.custAddlVar8.setErrorMessage("");
		this.custAddlVar9.setErrorMessage("");
		this.custAddlVar10.setErrorMessage("");
		this.custAddlVar11.setErrorMessage("");
		this.custAddlDec1.setErrorMessage("");
		this.custAddlDec1.setErrorMessage("");
		this.custAddlDec1.setErrorMessage("");
		this.custAddlDec1.setErrorMessage("");
		this.custAddlDec2.setErrorMessage("");
		this.custAddlDec2.setErrorMessage("");
		this.custAddlDec2.setErrorMessage("");
		this.custAddlDec2.setErrorMessage("");
		this.custAddlDec3.setErrorMessage("");
		this.custAddlDec3.setErrorMessage("");
		this.custAddlDec3.setErrorMessage("");
		this.custAddlDec3.setErrorMessage("");
		this.custAddlDec4.setErrorMessage("");
		this.custAddlDec4.setErrorMessage("");
		this.custAddlDec4.setErrorMessage("");
		this.custAddlDec4.setErrorMessage("");
		this.custAddlDec5.setErrorMessage("");
		this.custAddlDec5.setErrorMessage("");
		this.custAddlDec5.setErrorMessage("");
		this.custAddlDec5.setErrorMessage("");
		this.custAddlInt1.setErrorMessage("");
		this.custAddlInt2.setErrorMessage("");
		this.custAddlInt3.setErrorMessage("");
		this.custAddlInt4.setErrorMessage("");
		this.custAddlInt5.setErrorMessage("");

		logger.debug("Leaving");
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a Customer object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final Customer aCustomer = new Customer();
		BeanUtils.copyProperties(getCustomer(), aCustomer);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") 
									+ "\n\n --> " + aCustomer.getCustID();
		
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, 
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomer.getRecordType()).equals("")){
				aCustomer.setVersion(aCustomer.getVersion()+1);
				aCustomer.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aCustomer.setNewRecord(true);

				if (isWorkFlowEnabled()) {
					aCustomer.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			
			try {
				if (doProcess(aCustomer, tranType)) {
					refreshList();
					closeDialog(this.window_CustomerMaintenanceDialog, "Customer");
				}
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
		
		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new Customer() in the frontEnd.
		// we get it from the backEnd.
		final Customer aCustomer = getCustomerService().getNewCustomer();
		aCustomer.setCustID(getCustomer().getCustID());
		aCustomer.setNewRecord(true);
		setCustomer(aCustomer);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_Edit();
		// setFocus
		this.custCIF.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getCustomer().isNewRecord()){
			this.btnCancel.setVisible(false);
		}else{
			this.btnCancel.setVisible(true);
		}
		this.custCIF.setReadonly(true);
		this.custCoreBank.setReadonly(true);
		this.custDOB.setDisabled(true);
		
		if(getCustomer().getLovDescCustCtgType().equalsIgnoreCase("C")){
			
			this.gb_personalDetails.setVisible(false);
			this.gb_corporateCustomerPersonalDetails.setVisible(true);
			this.row_retailPPT.setVisible(false);
			this.row_retailVisa.setVisible(false);
			this.row_EmpSts.setVisible(false);
			this.row_custStaff.setVisible(false);
			this.row_corpTL.setVisible(true);
			this.row_corpTLED.setVisible(true);
			
		}else if(getCustomer().getLovDescCustCtgType().equalsIgnoreCase("I")){

			this.gb_personalDetails.setVisible(true);
			this.gb_corporateCustomerPersonalDetails.setVisible(false);
			this.row_retailPPT.setVisible(true);
			this.row_retailVisa.setVisible(true);
			this.row_EmpSts.setVisible(true);
			this.row_custStaff.setVisible(true);
			this.row_corpTL.setVisible(false);
			this.row_corpTLED.setVisible(false);
		}
		
		this.custFName.setReadonly(isReadOnly("CustomerMaintenanceDialog_custFName"));
		this.custMName.setReadonly(isReadOnly("CustomerMaintenanceDialog_custMName"));
		this.custLName.setReadonly(isReadOnly("CustomerMaintenanceDialog_custLName"));
		this.custShrtName.setReadonly(isReadOnly("CustomerMaintenanceDialog_custShrtName"));
		this.custPOB.setReadonly(isReadOnly("CustomerMaintenanceDialog_custPOB"));
		this.custMotherMaiden.setReadonly(isReadOnly("CustomerMaintenanceDialog_custMotherMaiden"));
		
		this.custPassportNo.setReadonly(isReadOnly("CustomerMaintenanceDialog_custPassportNo"));
		this.custPassportExpiry.setDisabled(isReadOnly("CustomerMaintenanceDialog_custPassportExpiry"));
		this.custVisaNum.setReadonly(isReadOnly("CustomerMaintenanceDialog_custVisaNum"));
		this.custVisaExpiry.setDisabled(isReadOnly("CustomerMaintenanceDialog_custVisaExpiry"));
		this.custTradeLicenceNum.setReadonly(isReadOnly("CustomerMaintenanceDialog_custTradeLicenceNum"));
		this.custTradeLicenceExpiry.setDisabled(isReadOnly("CustomerMaintenanceDialog_custTradeLicenceExpiry"));
		this.custDateOfIncorporation.setDisabled(isReadOnly("CustomerMaintenanceDialog_custDateOfIncorporation"));
		this.custIsStaff.setDisabled(isReadOnly("CustomerMaintenanceDialog_custIsStaff"));
		this.custStaffID.setReadonly(isReadOnly("CustomerMaintenanceDialog_custStaffID"));
		this.custIsBlackListed.setDisabled(isReadOnly("CustomerMaintenanceDialog_custIsBlackListed"));
		this.custIsRejected.setDisabled(isReadOnly("CustomerMaintenanceDialog_custIsRejected"));

		this.custDSA.setReadonly(isReadOnly("CustomerMaintenanceDialog_custDSA"));
		this.custReferedBy.setReadonly(isReadOnly("CustomerMaintenanceDialog_custReferedBy"));
		
		this.custAddlVar81.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar81"));
		this.custAddlVar82.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar82"));
		this.custAddlVar83.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar83"));
		this.custAddlVar84.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar84"));
		this.custAddlVar85.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar85"));
		this.custAddlVar86.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar86"));
		this.custAddlVar87.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar87"));
		this.custAddlVar88.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar88"));
		this.custAddlVar89.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar89"));
		this.custAddlDate1.setDisabled(isReadOnly("CustomerMaintenanceDialog_custAddlDate1"));
		this.custAddlDate2.setDisabled(isReadOnly("CustomerMaintenanceDialog_custAddlDate2"));
		this.custAddlDate3.setDisabled(isReadOnly("CustomerMaintenanceDialog_custAddlDate3"));
		this.custAddlDate4.setDisabled(isReadOnly("CustomerMaintenanceDialog_custAddlDate4"));
		this.custAddlDate5.setDisabled(isReadOnly("CustomerMaintenanceDialog_custAddlDate5"));
		this.custAddlVar1.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar1"));
		this.custAddlVar2.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar2"));
		this.custAddlVar3.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar3"));
		this.custAddlVar4.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar4"));
		this.custAddlVar5.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar5"));
		this.custAddlVar6.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar6"));
		this.custAddlVar7.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar7"));
		this.custAddlVar8.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar8"));
		this.custAddlVar9.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar9"));
		this.custAddlVar10.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar10"));
		this.custAddlVar11.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlVar11"));
		this.custAddlDec1.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlDec1"));
		this.custAddlDec2.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlDec2"));
		this.custAddlDec3.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlDec3"));
		this.custAddlDec4.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlDec4"));
		this.custAddlDec5.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlDec5"));
		this.custAddlInt1.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlInt1"));
		this.custAddlInt2.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlInt2"));
		this.custAddlInt3.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlInt3"));
		this.custAddlInt4.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlInt4"));
		this.custAddlInt5.setReadonly(isReadOnly("CustomerMaintenanceDialog_custAddlInt5"));

		this.btnSearchCustTypeCode.setDisabled(isReadOnly("CustomerMaintenanceDialog_custTypeCode"));
		this.btnSearchCustDftBranch.setDisabled(isReadOnly("CustomerMaintenanceDialog_custDftBranch"));
		this.btnSearchcustGroupID.setDisabled(isReadOnly("CustomerMaintenanceDialog_custGroupID"));
		this.btnSearchCustBaseCcy.setDisabled(isReadOnly("CustomerMaintenanceDialog_custBaseCcy"));
		this.btnSearchCustLng.setDisabled(isReadOnly("CustomerMaintenanceDialog_custLng"));
		this.btnSearchCustGenderCode.setDisabled(isReadOnly("CustomerMaintenanceDialog_custGenderCode"));
		this.btnSearchCustSalutationCode.setDisabled(isReadOnly("CustomerMaintenanceDialog_custSalutationCode"));
		this.btnSearchCustCOB.setDisabled(isReadOnly("CustomerMaintenanceDialog_custCOB"));
		this.btnSearchCustProfession.setDisabled(isReadOnly("CustomerMaintenanceDialog_custProfession"));
		this.btnSearchCustMaritalSts.setDisabled(isReadOnly("CustomerMaintenanceDialog_custMaritalSts"));
		this.btnSearchCustEmpSts.setDisabled(isReadOnly("CustomerMaintenanceDialog_custEmpSts"));
		this.btnSearchCustCtgCode.setDisabled(isReadOnly("CustomerMaintenanceDialog_custCtgCode"));
		this.btnSearchCustSector.setDisabled(isReadOnly("CustomerMaintenanceDialog_custSector"));
		this.btnSearchCustSubSector.setDisabled(isReadOnly("CustomerMaintenanceDialog_custSubSector"));
		this.btnSearchCustIndustry.setDisabled(isReadOnly("CustomerMaintenanceDialog_custIndustry"));
		this.btnSearchCustSubSegment.setDisabled(isReadOnly("CustomerMaintenanceDialog_custSubSegment"));
		this.btnSearchCustSubSegment.setDisabled(isReadOnly("CustomerMaintenanceDialog_custSubSegment"));
		this.btnSearchCustResdCountry.setDisabled(isReadOnly("CustomerMaintenanceDialog_custResdCountry"));
		this.btnSearchCustNationality.setDisabled(isReadOnly("CustomerMaintenanceDialog_custNationality"));
		this.btnSearchCustDSADept.setDisabled(isReadOnly("CustomerMaintenanceDialog_custDSADept"));
		this.btnSearchCustRO1.setDisabled(isReadOnly("CustomerMaintenanceDialog_custRO1"));
		this.btnSearchCustRO2.setDisabled(isReadOnly("CustomerMaintenanceDialog_custRO2"));
		this.custStmtFrqCode.setDisabled(isReadOnly("CustomerMaintenanceDialog_custStmtFrq"));
		this.custStmtFrqMth.setDisabled(isReadOnly("CustomerMaintenanceDialog_custStmtFrq"));
		this.custStmtFrqDays.setDisabled(isReadOnly("CustomerMaintenanceDialog_custStmtFrqDay"));
		this.btnSearchCustStmtDispatchMode.setDisabled(isReadOnly("CustomerMaintenanceDialog_custStmtDispatchMode"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.customer.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
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
		this.custCoreBank.setReadonly(true);
		this.btnSearchCustTypeCode.setVisible(false);
		this.btnSearchCustDftBranch.setVisible(false);
		this.custGroupID.setReadonly(true);
		this.btnSearchcustGroupID.setVisible(false);
		this.btnSearchCustBaseCcy.setVisible(false);
		this.btnSearchCustLng.setVisible(false);
		this.btnSearchCustGenderCode.setVisible(false);
		this.btnSearchCustSalutationCode.setVisible(false);
		this.custFName.setReadonly(true);
		this.custMName.setReadonly(true);
		this.custLName.setReadonly(true);
		this.custShrtName.setReadonly(true);
		this.custDOB.setDisabled(true);
		this.custPOB.setReadonly(true);
		this.btnSearchCustCOB.setVisible(false);
		this.custMotherMaiden.setReadonly(true);
		this.btnSearchCustProfession.setVisible(false);
		this.btnSearchCustMaritalSts.setVisible(false);

		this.btnSearchCustSts.setVisible(false);
		this.custPassportNo.setReadonly(true);
		this.custPassportExpiry.setDisabled(true);
		this.custVisaNum.setReadonly(true);
		this.custVisaExpiry.setDisabled(true);
		this.custTradeLicenceNum.setReadonly(true);
		this.custTradeLicenceExpiry.setDisabled(true);
		this.custDateOfIncorporation.setDisabled(true);
		this.btnSearchCustEmpSts.setVisible(false);
		this.custIsStaff.setDisabled(true);
		this.custStaffID.setReadonly(true);
		this.custIsBlackListed.setDisabled(true);
		this.btnSearchCustBLRsnCode.setVisible(false);
		this.custIsRejected.setDisabled(true);
		this.btnSearchCustRejectedRsn.setVisible(false);
		this.custFirstBusinessDate.setDisabled(true);
		
		this.btnSearchCustCtgCode.setVisible(false);
		this.btnSearchCustSector.setVisible(false);
		this.btnSearchCustSubSector.setVisible(false);
		this.btnSearchCustIndustry.setVisible(false);
		this.btnSearchCustSegment.setVisible(false);
		this.btnSearchCustSubSegment.setVisible(false);
		this.btnSearchCustResdCountry.setVisible(false);
		this.btnSearchCustNationality.setVisible(false);
		this.custDSA.setReadonly(true);
		this.btnSearchCustDSADept.setVisible(false);
		this.btnSearchCustRO1.setVisible(false);
		this.btnSearchCustRO2.setVisible(false);
		this.custReferedBy.setReadonly(true);
		this.custStmtFrqCode.setDisabled(true);
		this.custStmtFrqMth.setDisabled(true);
		this.custStmtFrqDays.setDisabled(true);
		this.custIsStmtCombined.setDisabled(true);
		this.custStmtNextDate.setDisabled(true);
		this.btnSearchCustStmtDispatchMode.setVisible(false);
		
		this.custIsActive.setDisabled(true);
		this.custIsBlackListed.setDisabled(true);
		this.custIsBlocked.setDisabled(true);
		this.custIsClosed.setDisabled(true);
		this.custIsDecease.setDisabled(true);
		this.custIsDelinquent.setDisabled(true);
		this.custIsDormant.setDisabled(true);
		this.custIsStaff.setDisabled(true);
		
		this.custAddlVar81.setReadonly(true);
		this.custAddlVar82.setReadonly(true);
		this.custAddlVar83.setReadonly(true);
		this.custAddlVar84.setReadonly(true);
		this.custAddlVar85.setReadonly(true);
		this.custAddlVar86.setReadonly(true);
		this.custAddlVar87.setReadonly(true);
		this.custAddlVar88.setReadonly(true);
		this.custAddlVar89.setReadonly(true);
		this.custAddlDate1.setDisabled(true);
		this.custAddlDate2.setDisabled(true);
		this.custAddlDate3.setDisabled(true);
		this.custAddlDate4.setDisabled(true);
		this.custAddlDate5.setDisabled(true);
		this.custAddlVar1.setReadonly(true);
		this.custAddlVar2.setReadonly(true);
		this.custAddlVar3.setReadonly(true);
		this.custAddlVar4.setReadonly(true);
		this.custAddlVar5.setReadonly(true);
		this.custAddlVar6.setReadonly(true);
		this.custAddlVar7.setReadonly(true);
		this.custAddlVar8.setReadonly(true);
		this.custAddlVar9.setReadonly(true);
		this.custAddlVar10.setReadonly(true);
		this.custAddlVar11.setReadonly(true);
		this.custAddlDec1.setReadonly(true);
		this.custAddlDec2.setReadonly(true);
		this.custAddlDec3.setReadonly(true);
		this.custAddlDec4.setReadonly(true);
		this.custAddlDec5.setReadonly(true);
		this.custAddlInt1.setReadonly(true);
		this.custAddlInt2.setReadonly(true);
		this.custAddlInt3.setReadonly(true);
		this.custAddlInt4.setReadonly(true);
		this.custAddlInt5.setReadonly(true);

		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if(isWorkFlowEnabled()){
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
		doRemoveValidation();

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
		this.custAddlVar81.setValue("");
		this.custAddlVar82.setValue("");
		this.custAddlVar83.setValue("");
		this.custAddlVar84.setValue("");
		this.custAddlVar85.setValue("");
		this.custAddlVar86.setValue("");
		this.custAddlVar87.setValue("");
		this.custAddlVar88.setValue("");
		this.custAddlVar89.setValue("");
		this.custAddlDate1.setText("");
		this.custAddlDate2.setText("");
		this.custAddlDate3.setText("");
		this.custAddlDate4.setText("");
		this.custAddlDate5.setText("");
		this.custAddlVar1.setValue("");
		this.custAddlVar2.setValue("");
		this.custAddlVar3.setValue("");
		this.custAddlVar4.setValue("");
		this.custAddlVar5.setValue("");
		this.custAddlVar6.setValue("");
		this.custAddlVar7.setValue("");
		this.custAddlVar8.setValue("");
		this.custAddlVar9.setValue("");
		this.custAddlVar10.setValue("");
		this.custAddlVar11.setValue("");
		this.custAddlDec1.setValue("");
		this.custAddlDec2.setValue("");
		this.custAddlDec3.setValue("");
		this.custAddlDec4.setValue("");
		this.custAddlDec5.setValue("");
		this.custAddlInt1.setText("");
		this.custAddlInt2.setText("");
		this.custAddlInt3.setText("");
		this.custAddlInt4.setText("");
		this.custAddlInt5.setText("");

		logger.debug("Leaving");

	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final Customer aCustomer = new Customer();
		BeanUtils.copyProperties(getCustomer(), aCustomer);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		
		// fill the Customer object with the components data
		doWriteComponentsToBean(aCustomer);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aCustomer.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCustomer.getRecordType()).equals("")) {
				aCustomer.setVersion(aCustomer.getVersion() + 1);
				if (isNew) {
					aCustomer.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
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
			if (doProcess(aCustomer, tranType)) {
				refreshList();
				closeDialog(this.window_CustomerMaintenanceDialog, "Customer");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomer
	 *            (Customer)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(Customer aCustomer, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCustomer.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCustomer.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomer.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCustomer.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomer.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId,aCustomer);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow()
						.getAuditingReq(taskId, aCustomer))) {
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
			}

			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode = getWorkFlow().firstTask.owner;
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aCustomer.setTaskId(taskId);
			aCustomer.setNextTaskId(nextTaskId);
			aCustomer.setRoleCode(getRole());
			aCustomer.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustomer, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCustomer);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustomer,PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCustomer, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Customer aCustomer = (Customer) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getCustomerService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCustomerService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getCustomerService().doApprove(auditHeader);

						if (aCustomer.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCustomerService().doReject(auditHeader);

						if (aCustomer.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_CustomerMaintenanceDialog, 
								auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerMaintenanceDialog, 
						auditHeader);
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
	// ++++++++++++ Search Button Component Events+++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void onClick$btnSearchCustTypeCode(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "CustomerType");
		if (dataObject instanceof String) {
			this.custTypeCode.setValue(dataObject.toString());
			this.lovDescCustTypeCodeName.setValue("");
		} else {
			CustomerType details = (CustomerType) dataObject;
			if (details != null) {
				this.custTypeCode.setValue(details.getCustTypeCode());
				this.lovDescCustTypeCodeName.setValue(details.getCustTypeCode()+ "-" + details.getCustTypeDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustDftBranch(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "Branch");
		if (dataObject instanceof String) {
			this.custDftBranch.setValue(dataObject.toString());
			this.lovDescCustDftBranchName.setValue("");
		} else {
			Branch details = (Branch) dataObject;
			if (details != null) {
				this.custDftBranch.setValue(details.getBranchCode());
				this.lovDescCustDftBranchName.setValue(details.getBranchCode()+ "-" + details.getBranchDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchcustGroupID(Event event) {

		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "CustomerGroup");
		if (dataObject instanceof String) {
			this.custGroupID.setValue(new Long(0));
			this.lovDesccustGroupIDName.setValue("");
		} else {
			CustomerGroup details = (CustomerGroup) dataObject;
			if (details != null) {
				this.custGroupID.setValue(Long.valueOf(details.getCustGrpID()));
				this.lovDesccustGroupIDName.setValue(details.getCustGrpID()+ "-" + details.getCustGrpDesc());
			}
		}
		if(this.custGroupID.longValue() == 0){
			this.space_custGroupSts.setStyle("background-color:white;");
			this.lovDescCustGroupStsName.setValue("");
			this.btnSearchCustGroupSts.setVisible(false);
		}else{
			this.space_custGroupSts.setStyle("background-color:red;");
			this.lovDescCustGroupStsName.setValue("");
			this.btnSearchCustGroupSts.setVisible(true);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustBaseCcy(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "Currency");
		if (dataObject instanceof String) {
			this.custBaseCcy.setValue(dataObject.toString());
			this.lovDescCustBaseCcyName.setValue("");
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				this.custBaseCcy.setValue(details.getCcyCode());
				this.lovDescCustBaseCcyName.setValue(details.getCcyCode() + "-"+ details.getCcyDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustLng(Event event) {

		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerMaintenanceDialog, "Language");
		if (dataObject instanceof String) {
			this.custLng.setValue(SystemParameterDetails.getSystemParameterValue("APP_LNG").toString());
			this.lovDescCustLngName.setValue(SystemParameterDetails.getSystemParameterValue("APP_LNG").toString()
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
		} else if (getCustomer().getLovDescCustCtgType().equals("I")) {
			row_localLngFM.setVisible(true);
			row_localLngLS.setVisible(true);
		} else if (getCustomer().getLovDescCustCtgType().equals("C")) {
			row_localLngCorpCustCS.setVisible(true);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustGenderCode(Event event) {
		logger.debug("Entering");
		String sCustGender = this.custGenderCode.getValue();
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "Gender");
		if (dataObject instanceof String) {
			this.custGenderCode.setValue(dataObject.toString());
			this.lovDescCustGenderCodeName.setValue("");
		} else {
			Gender details = (Gender) dataObject;
			if (details != null) {
				this.custGenderCode.setValue(details.getGenderCode());
				this.lovDescCustGenderCodeName.setValue(details.getGenderCode()+ "-" + details.getGenderDesc());
			}
		}
		if (!StringUtils.trimToEmpty(sCustGender).equals(this.custGenderCode.getValue())) {
			this.custSalutationCode.setValue("");
			this.lovDescCustSalutationCodeName.setValue("");
			this.btnSearchCustSalutationCode.setDisabled(false);
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
				this.window_CustomerMaintenanceDialog, "Salutation", filters);
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
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "Country");
		if (dataObject instanceof String) {
			this.custCOB.setValue(dataObject.toString());
			this.lovDescCustCOBName.setValue("");
		} else {
			Country details = (Country) dataObject;
			if (details != null) {
				this.custCOB.setValue(details.getCountryCode());
				this.lovDescCustCOBName.setValue(details.getCountryCode() + "-"+ details.getCountryDesc());
				if (!this.custCOB.getValue().equals("")&& this.custCOB.getValue().equals(SystemParameterDetails
								.getSystemParameterValue("CURR_SYSTEM_COUNTRY"))) {
				} 
			}
		}
		logger.debug("Leaving");
	}
	
	public void onClick$btnSearchCorpCustCOB(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "Country");
		if (dataObject instanceof String) {
			this.corpCustCOB.setValue(dataObject.toString());
			this.lovDescCorpCustCOBName.setValue("");
		} else {
			Country details = (Country) dataObject;
			if (details != null) {
				this.corpCustCOB.setValue(details.getCountryCode());
				this.lovDescCorpCustCOBName.setValue(details.getCountryCode() + "-"+ details.getCountryDesc());
				if (!this.corpCustCOB.getValue().equals("")&& this.corpCustCOB.getValue().equals(SystemParameterDetails
								.getSystemParameterValue("CURR_SYSTEM_COUNTRY"))) {
				} 
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustProfession(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "Profession");
		if (dataObject instanceof String) {
			this.custProfession.setValue(dataObject.toString());
			this.lovDescCustProfessionName.setValue("");
		} else {
			Profession details = (Profession) dataObject;
			if (details != null) {
				this.custProfession.setValue(details.getProfessionCode());
				this.lovDescCustProfessionName.setValue(details.getProfessionCode()+ "-"+ details.getProfessionDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustMaritalSts(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "MaritalStatusCode");
		if (dataObject instanceof String) {
			this.custMaritalSts.setValue(dataObject.toString());
			this.lovDescCustMaritalStsName.setValue("");
		} else {
			MaritalStatusCode details = (MaritalStatusCode) dataObject;
			if (details != null) {
				this.custMaritalSts.setValue(details.getMaritalStsCode());
				this.lovDescCustMaritalStsName.setValue(details.getMaritalStsCode()+ "-"+ details.getMaritalStsDesc());
			}
		}
		logger.debug("Leaving");
	}
	
	public void onClick$btnSearchCustSts(Event event){

		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog,"CustomerStatusCode");
		if (dataObject instanceof String){
			this.custSts.setValue(dataObject.toString());
			this.lovDescCustStsName.setValue("");
		}else{
			CustomerStatusCode details= (CustomerStatusCode) dataObject;
			if (details != null) {
				this.custSts.setValue(details.getCustStsCode());
				this.lovDescCustStsName.setValue(details.getCustStsCode()+"-"+details.getCustStsDescription());
			}
		}
		
		if(!this.custSts.getValue().equals("") && !this.custSts.getValue().equals("ACTIVE")
				&& !this.oldVar_custSts.equals(this.custSts.getValue())){
			
			this.custStsChgDate.setDisabled(false);
			this.space_custStsChdDate.setStyle("background-color:red");
		}
	}
	
	public void onClick$btnSearchCustGroupSts(Event event){

		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog,"GroupStatusCode");
		if (dataObject instanceof String){
			this.custGroupSts.setValue(PennantConstants.List_Select);
			this.lovDescCustGroupStsName.setValue("");
		}else{
			GroupStatusCode details= (GroupStatusCode) dataObject;
			if (details != null) {
				this.custGroupSts.setValue(details.getGrpStsCode());
				this.lovDescCustGroupStsName.setValue(details.getGrpStsCode()+"-"+details.getGrpStsDescription());
			}
		}
	}

	public void onClick$btnSearchCustEmpSts(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "EmpStsCode");
		if (dataObject instanceof String) {
			this.custEmpSts.setValue(dataObject.toString());
			this.lovDescCustEmpStsName.setValue("");
		} else {
			EmpStsCode details = (EmpStsCode) dataObject;
			if (details != null) {
				this.custEmpSts.setValue(details.getEmpStsCode());
				this.lovDescCustEmpStsName.setValue(details.getEmpStsCode()+ "-" + details.getEmpStsDesc());
			}
		}
		logger.debug("Leaving");
	}
	
	public void onClick$btnSearchCustBLRsnCode(Event event) {

		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerMaintenanceDialog, "BlackListReasonCode");
		if (dataObject instanceof String) {
			this.custBLRsnCode.setValue("");
			this.lovDescCustBLRsnCodeName.setValue("");
		} else {
			BlackListReasonCode details = (BlackListReasonCode) dataObject;
			if (details != null) {
				this.custBLRsnCode.setValue(details.getBLRsnCode());
				this.lovDescCustBLRsnCodeName.setValue(details.getBLRsnCode()+ "-" + details.getBLRsnDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustRejectedRsn(Event event) {

		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "RejectDetail");
		if (dataObject instanceof String) {
			this.custRejectedRsn.setValue("");
			this.lovDescCustRejectedRsnName.setValue("");
		} else {
			RejectDetail details = (RejectDetail) dataObject;
			if (details != null) {
				this.custRejectedRsn.setValue(details.getRejectCode());
				this.lovDescCustRejectedRsnName.setValue(details.getRejectCode() + "-" + details.getRejectDesc());
			}
		}
		logger.debug("Leaving");
	}
	
	public void onClick$btnSearchCustCtgCode(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "CustomerCategory");
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
	
	public void onClick$btnSearchCustSector(Event event) {
		logger.debug("Entering");
		String sCustSector = this.custSector.getValue();

		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "Sector");
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
				this.window_CustomerMaintenanceDialog, "SubSector", filters);
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
	
	public void onClick$btnSearchCustIndustry(Event event) {
		logger.debug("Entering");
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("SubSectorCode", this.custSubSector.getValue(),Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "Industry", filters);
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
	
	public void onClick$btnSearchCustSegment(Event event) {
		logger.debug("Entering");
		String sCustSegment = this.custSegment.getValue();

		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "Segment");
		if (dataObject instanceof String) {
			this.custSegment.setValue(dataObject.toString());
			this.lovDescCustSegmentName.setValue("");
		} else {
			Segment details = (Segment) dataObject;
			if (details != null) {
				this.custSegment.setValue(details.getSegmentCode());
				this.lovDescCustSegmentName.setValue(details.getSegmentCode()+ "-" + details.getSegmentDesc());
			}
		}
		if (!StringUtils.trimToEmpty(sCustSegment).equals(this.custSegment.getValue())) {
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
		filters[0] = new Filter("SegmentCode", this.custSegment.getValue(),Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "SubSegment", filters);
		if (dataObject instanceof String) {
			this.custSubSegment.setValue(dataObject.toString());
			this.lovDescCustSubSegmentName.setValue("");
		} else {
			SubSegment details = (SubSegment) dataObject;
			if (details != null) {
				this.custSubSegment.setValue(details.getSubSegmentCode());
				this.lovDescCustSubSegmentName.setValue(details.getSubSegmentCode()+ "-"+ details.getSubSegmentDesc());
			}
		}
		logger.debug("Leaving");
	}
	
	public void onClick$btnSearchCustResdCountry(Event event) {

		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "Country");
		if (dataObject instanceof String) {
			this.custResdCountry.setValue(dataObject.toString());
			this.lovDescCustResdCountryName.setValue("");
		} else {
			Country details = (Country) dataObject;
			if (details != null) {
				this.custResdCountry.setValue(details.getCountryCode());
				this.lovDescCustResdCountryName.setValue(details.getCountryCode() + "-" + details.getCountryDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustNationality(Event event) {

		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "NationalityCode");
		if (dataObject instanceof String) {
			this.custNationality.setValue(dataObject.toString());
			this.lovDescCustNationalityName.setValue("");
		} else {
			NationalityCode details = (NationalityCode) dataObject;
			if (details != null) {
				this.custNationality.setValue(details.getNationalityCode());
				this.lovDescCustNationalityName.setValue(details.getNationalityCode()+ "-"+ details.getNationalityDesc());
			}
		}
		logger.debug("Leaving");
	}
	
	public void onClick$btnSearchCustDSADept(Event event) {
		
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "Department");
		if (dataObject instanceof String) {
			this.custDSADept.setValue(dataObject.toString());
			this.lovDescCustDSADeptName.setValue("");
		} else {
			Department details = (Department) dataObject;
			if (details != null) {
				this.custDSADept.setValue(details.getDeptCode());
				this.lovDescCustDSADeptName.setValue(details.getDeptCode()+ "-" + details.getDeptDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustRO1(Event event) {
		
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "RelationshipOfficer");
		if (dataObject instanceof String) {
			this.custRO1.setValue(dataObject.toString());
			this.lovDescCustRO1Name.setValue("");
		} else {
			RelationshipOfficer details = (RelationshipOfficer) dataObject;
			if (details != null) {
				this.custRO1.setValue(details.getROfficerCode());
				this.lovDescCustRO1Name.setValue(details.getROfficerCode()
						+ "-" + details.getROfficerDesc());
				this.lovDescCustRO2Name.clearErrorMessage();
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustRO2(Event event) {
		
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "RelationshipOfficer");
		if (dataObject instanceof String) {
			this.custRO2.setValue("");
			this.lovDescCustRO2Name.setValue("");
		} else {
			RelationshipOfficer details = (RelationshipOfficer) dataObject;
			if (details != null) {
				this.custRO2.setValue(details.getROfficerCode());
				this.lovDescCustRO2Name.setValue(details.getROfficerCode()+ "-" + details.getROfficerDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCustStmtDispatchMode(Event event) {
		
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerMaintenanceDialog, "DispatchMode");
		if (dataObject instanceof String) {
			this.custStmtDispatchMode.setValue(dataObject.toString());
			this.lovDescDispatchModeDescName.setValue("");
		} else {
			DispatchMode details = (DispatchMode) dataObject;
			if (details != null) {
				this.custStmtDispatchMode.setValue(details.getDispatchModeCode());
				this.lovDescDispatchModeDescName.setValue(details.getDispatchModeCode()+ "-"+ details.getDispatchModeDesc());
			}
		}
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
	
	public void onCheck$custIsActive(Event event){
		if(custIsActive.isChecked()){
			custInactiveReason.setValue("");
			custInactiveReason.setReadonly(true);
			space_inactiveReason.setStyle("background-color:white");
		}else{
			custInactiveReason.setReadonly(false);
			space_inactiveReason.setStyle("background-color:red");
		}
	}
	
	public void onCheck$custIsClosed(Event event){
		if(custIsClosed.isChecked()){
			custClosedOn.setDisabled(false);
			space_closedDate.setStyle("background-color:red");
		}else{
			custClosedOn.setDisabled(true);
			this.custClosedOn.setText("");
			space_closedDate.setStyle("background-color:white");
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ OnBlur TextBox Events +++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
	 * Method to set the value in Customer PassportNo field to upper case. *
	 * 
	 * @param event
	 */
	public void onBlur$custPassportNo(Event event) {
		passportNoChange();
	}

	/**
	 * Method to set the value in Customer VisaNo field to upper case. *
	 * 
	 * @param event
	 */
	public void onBlur$custVisaNum(Event event) {
		visaNoChange();
	}
	
	/**
	 * Check Whether DateOfBirth field Entered or Not
	 * 
	 */
	private void dobCheck() {
		logger.debug("Entering");
		this.custIsMinor.setChecked(false);
		if (this.custDOB.getValue() != null) {
			int age = 0;
			age = DateUtility.getYearsBetween(this.custDOB.getValue(),new Date());
			if (age < ((BigDecimal) SystemParameterDetails.getSystemParameterValue("MINOR_AGE")).intValue()) {
				this.custIsMinor.setChecked(true);
			}
			this.custIsMinor.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Generate the Customer Details in the CustomerMaintenanceDialogCtrl and set Fields by
	 * using Events on Loading & in Editing<br>
	 * 
	 * CheckBox Events and Field Blur events
	 */
	public void doCheckValidations() {
		logger.debug("Entering");
		dobCheck();
		isActiveCheck();
		staffIDCheck();
		blackListCustomerCheck();
		rejectedListCustomerCheck();
		passportNoChange();
		visaNoChange();
		customerCategoryCheck();
		logger.debug("Leaving");
	}
	
	/**
	 * Method for checking Customer Status states
	 */
	private void isActiveCheck(){
		
		if(custIsActive.isChecked()){
			custInactiveReason.setValue("");
			custInactiveReason.setReadonly(true);
			space_inactiveReason.setStyle("background-color:white");
		}else{
			if("ENQ".equals(this.module)){
				custInactiveReason.setReadonly(true);
			}else{
				custInactiveReason.setReadonly(false);
			}
			space_inactiveReason.setStyle("background-color:red");
		}
		if(custIsClosed.isChecked() && !"ENQ".equals(this.module)){
			custClosedOn.setDisabled(false);
			space_closedDate.setStyle("background-color:red");
		}else{
			custClosedOn.setDisabled(true);
			space_closedDate.setStyle("background-color:white");
		}
	}
	/**
	 * Check Whether Customer is related to Staff of the Bank or Not
	 * 
	 */
	public void staffIDCheck() {
		logger.debug("Entering");
		if (this.custIsStaff.isChecked() && !"ENQ".equals(this.module)) {
			this.custStaffID.setReadonly(isReadOnly("CustomerMaintenanceDialog_custStaffID"));
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
		if (this.custIsBlackListed.isChecked() && !"ENQ".equals(this.module)) {
			this.custBLRsnCode.setReadonly(isReadOnly("CustomerMaintenanceDialog_custBLRsnCode"));
			this.btnSearchCustBLRsnCode.setDisabled(isReadOnly("CustomerMaintenanceDialog_custBLRsnCode"));
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
		if (this.custIsRejected.isChecked() && !"ENQ".equals(this.module)) {
			this.custRejectedRsn.setReadonly(isReadOnly("CustomerMaintenanceDialog_custRejectedRsn"));
			this.btnSearchCustRejectedRsn.setDisabled(isReadOnly("CustomerMaintenanceDialog_custRejectedRsn"));
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
			if("ENQ".equals(this.module)){
				this.custPassportExpiry.setDisabled(true);
			}else{
				this.custPassportExpiry.setDisabled(isReadOnly("CustomerMaintenanceDialog_custPassportExpiry"));				
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
			if("ENQ".equals(this.module)){
				this.custVisaExpiry.setDisabled(true);
				custPassportExpiry.setDisabled(true);
			}else{
				this.custVisaExpiry.setDisabled(isReadOnly("CustomerMaintenanceDialog_custVisaExpiry"));
				custPassportExpiry.setDisabled(isReadOnly("CustomerMaintenanceDialog_custPassportExpiry"));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to check customer category.
	 * */
	public void customerCategoryCheck() {
		logger.debug("Entering");
		
		if (getCustomer().getLovDescCustCtgType().equals("I")) {
			
			if (this.custLng.getValue().equals(PennantConstants.default_Language)) {
				row_localLngFM.setVisible(false);
				row_localLngLS.setVisible(false);
			} else {
				row_localLngFM.setVisible(true);
				row_localLngLS.setVisible(true);
			}
		} else if(getCustomer().getLovDescCustCtgType().equals("C")){
			
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
		if(this.custGroupID.longValue() == 0){
			this.space_custGroupSts.setStyle("background-color:white");
			this.lovDescCustGroupStsName.setValue("");
			this.btnSearchCustGroupSts.setVisible(false);
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++OnSelect ComboBox Events++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void onSelect$custStmtFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		String StmtFrqCode = validateCombobox(this.custStmtFrqCode);
		onSelectFrqCode(StmtFrqCode, this.custStmtFrqCode, this.custStmtFrqMth,
				this.custStmtFrqDays, this.custStmtFrq,isReadOnly("CustomerMaintenanceDialog_custStmtFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$custStmtFrqMth(Event event) {
		logger.debug("Entering" + event.toString());
		String StmtFrqCode = validateCombobox(this.custStmtFrqCode);
		String StmtFrqMonth = validateCombobox(this.custStmtFrqMth);
		onSelectFrqMth(StmtFrqCode, StmtFrqMonth, this.custStmtFrqMth,
				this.custStmtFrqDays, this.custStmtFrq,isReadOnly("CustomerMaintenanceDialog_custStmtFrq"));

		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$custStmtFrqDays(Event event) {
		logger.debug("Entering" + event.toString());
		onSelectFrqDay(custStmtFrqCode, custStmtFrqMth, custStmtFrqDays, this.custStmtFrq);
		logger.debug("Leaving" + event.toString());
	}

	/** To get the ComboBox selected value */
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
	 * @param aCustomerIncome
	 * @param tranType
	 * @return AuditHeader
	 */	
	private AuditHeader getAuditHeader(Customer aCustomer, String tranType){
		
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aCustomer.getBefImage(), aCustomer);
		return new AuditHeader(String.valueOf(aCustomer.getId()), null,
				null, null, auditDetail, aCustomer.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e){
		logger.debug("Entering");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails("",e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_CustomerMaintenanceDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(e);
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
		Notes notes = new Notes();
		notes.setModuleName("Customer");
		notes.setReference(String.valueOf(getCustomer().getCustID()));
		notes.setVersion(getCustomer().getVersion());

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
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful updation
	private void refreshList() {
		final JdbcSearchObject<Customer> soCustomer = getCustomerMaintenanceListCtrl().getSearchObj();
		getCustomerMaintenanceListCtrl().pagingCustomerMaintenanceList.setActivePage(0);
		getCustomerMaintenanceListCtrl().getPagedListWrapper().setSearchObject(soCustomer);
		if (getCustomerMaintenanceListCtrl().listBoxCustomer != null) {
			getCustomerMaintenanceListCtrl().listBoxCustomer.getListModel();
		}
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("Customer");
		notes.setReference(String.valueOf(getCustomer().getCustID()));
		notes.setVersion(getCustomer().getVersion());
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

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}
	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public void setCustomerMaintenaceListCtrl(CustomerMaintenanceListCtrl customerMaintenanceListCtrl) {
		this.customerMaintenanceListCtrl = customerMaintenanceListCtrl;
	}
	public CustomerMaintenanceListCtrl getCustomerMaintenanceListCtrl() {
		return customerMaintenanceListCtrl;
	}

}
