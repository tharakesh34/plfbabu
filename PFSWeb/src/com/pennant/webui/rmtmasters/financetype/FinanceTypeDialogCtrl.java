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
 *                       																	*
 * FileName      :  FinanceTypeDialogCtrl.java                                              *    
 *                                                                          				*
 * Author        :  PENNANT TECHONOLOGIES                       							*
 *                                                                        					*
 * Creation Date    :  30-06-2011                  											*
 *                                                                        					*
 * Modified Date    :  30-06-2011                  											*
 *                                                                        					*
 * Description   :                                                    						*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant                  0.1                                            * 
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

package com.pennant.webui.rmtmasters.financetype;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import org.zkoss.zk.ui.Path;
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
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rmtmasters.FinTypeAccount;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.ProductAsset;
import com.pennant.backend.model.systemmasters.DivisionDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.PercentageValidator;
import com.pennant.util.Constraint.RateValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the 
 * /WEB-INF/pages/SolutionFactory/FinanceType/financeTypeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinanceTypeDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 4493449538614654801L;
	private final static Logger logger = Logger.getLogger(FinanceTypeDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ 
	 * All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file 
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer. 
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	
	protected Window 				window_FinanceTypeDialog; 			// autoWired
	
	//Basic Details Tab
	protected Uppercasebox 			finType; 							// autoWired
	protected Textbox 				finTypeDesc; 						// autoWired
	protected ExtendedCombobox 		finCcy; 							// autoWired
	protected Combobox 				cbfinDaysCalType; 					// autoWired
	protected ExtendedCombobox 		finAcType; 							// autoWired
	protected Checkbox 				finIsOpenNewFinAc; 					// autoWired
	protected CurrencyBox 			finMinAmount; 						// autoWired
	protected CurrencyBox 			finMaxAmount;	 					// autoWired
	protected Combobox 				cbfinProductType; 					// autoWired
	protected Combobox 				cbfinAssetType; 					// autoWired
	protected Checkbox 				finIsDwPayRequired; 				// autoWired
	protected Decimalbox 			finMinDownPayAmount; 				// autoWired
	protected Checkbox 				finIsGenRef; 						// autoWired
	protected Checkbox 				fInIsAlwGrace; 						// autoWired
	protected Checkbox 				finIsAlwMD; 						// autoWired
	protected Checkbox 				finDepreciationReq; 				// autoWired
	protected Checkbox 				finCommitmentReq; 					// autoWired
	protected Checkbox 				finCommitmentOvrride; 				// autoWired   
	protected Checkbox 				limitRequired; 						// autoWired
	protected Checkbox 				overrideLimit; 						// autoWired
	protected Checkbox 				allowRIAInvestment; 				// autoWired
	protected Checkbox 				allowParllelFinance; 				// autoWired
	protected ExtendedCombobox 		finDivision; 						// autoWired
	protected Checkbox 				finIsActive; 						// autoWired
	
	//Grace Period Schedule Details Tab  
	protected Combobox 				cbfinGrcRateType; 					// autoWired
	protected Decimalbox 			finGrcIntRate; 						// autoWired
	protected ExtendedCombobox 		finGrcBaseRate; 					// autoWired
	protected ExtendedCombobox 		finGrcSplRate; 						// autoWired
	protected Decimalbox 			finGrcMargin; 						// autoWired
	protected Label 				labe_GrcEffectiveRate; 				// autoWired
	protected Textbox 				finGrcDftIntFrq; 					// autoWired
	protected Combobox 				cbfinGrcDftIntFrqCode; 				// autoWired
	protected Combobox 				cbfinGrcDftIntFrqMth; 				// autoWired
	protected Combobox 				cbfinGrcDftIntFrqDays; 				// autoWired
	protected Checkbox 				finIsAlwGrcRepay; 					// autoWired
	protected Combobox 				finGrcSchdMthd; 					// autoWired
	protected Checkbox 				finGrcIsIntCpz; 					// autoWired
	protected Textbox 				finGrcCpzFrq; 						// autoWired
	protected Combobox 				cbfinGrcCpzFrqCode; 				// autoWired
	protected Combobox 				cbfinGrcCpzFrqMth; 					// autoWired
	protected Combobox 				cbfinGrcCpzFrqDays; 				// autoWired
	protected Checkbox 				finGrcIsRvwAlw; 					// autoWired
	protected Textbox 				finGrcRvwFrq; 						// autoWired
	protected Combobox 				cbfinGrcRvwFrqCode; 				// autoWired
	protected Combobox 				cbfinGrcRvwFrqMth; 					// autoWired
	protected Combobox 				cbfinGrcRvwFrqDays; 				// autoWired
	protected Combobox 				cbfinGrcRvwRateApplFor; 			// autoWired
	private Checkbox 				finGrcAlwIndRate; 					// autoWired
	private Textbox 				finGrcIndBaseRate; 					// autoWired
	protected Textbox 				lovDescFinGrcIndBaseRateName;		// autoWired
	protected Button 				btnSearchFinGrcIndBaseRate; 		// autoWired
	protected Checkbox 				finIsIntCpzAtGrcEnd; 				// autoWired

	//Repay Schedule Details Tab 
	protected Combobox 				cbfinRateType; 						// autoWired
	protected Decimalbox 			finIntRate; 						// autoWired
	protected ExtendedCombobox 		finBaseRate; 						// autoWired
	protected ExtendedCombobox 		finSplRate; 						// autoWired
	protected Decimalbox 			finMargin; 							// autoWired
	protected Label 				labe_EffectiveRate; 				// autoWired
	protected Checkbox 				finFrqrepayment;					// autoWired
	protected Textbox 				finDftIntFrq; 						// autoWired
	protected Combobox 				cbfinDftIntFrqCode; 				// autoWired
	protected Combobox 				cbfinDftIntFrqMth; 					// autoWired
	protected Combobox 				cbfinDftIntFrqDays; 				// autoWired
	protected Checkbox 				finRepayPftOnFrq; 					// autoWired
	protected Textbox 				finRpyFrq; 							// autoWired
	protected Combobox 				cbfinRpyFrqCode; 					// autoWired
	protected Combobox 				cbfinRpyFrqMth; 					// autoWired
	protected Combobox 				cbfinRpyFrqDays; 					// autoWired
	protected Combobox 				cbfinSchdMthd; 						// autoWired
	protected Checkbox 				finIsIntCpz; 						// autoWired
	protected Textbox 				finCpzFrq; 							// autoWired
	protected Combobox 				cbfinCpzFrqCode; 					// autoWired
	protected Combobox 				cbfinCpzFrqMth; 					// autoWired
	protected Combobox 				cbfinCpzFrqDays; 					// autoWired
	protected Checkbox 				finIsRvwAlw; 						// autoWired
	protected Textbox 				finRvwFrq; 							// autoWired
	protected Combobox 				cbfinRvwFrqCode; 					// autoWired
	protected Combobox 				cbfinRvwFrqMth; 					// autoWired
	protected Combobox 				cbfinRvwFrqDays; 					// autoWired
	protected Combobox 				cbfinRvwRateApplFor; 				// autoWired
	protected Combobox 				cbfinSchCalCodeOnRvw; 				// autoWired
	private Checkbox 				finAlwIndRate; 						// autoWired
	private ExtendedCombobox 		finIndBaseRate; 					// autoWired
	protected Intbox 				finMinTerm; 						// autoWired
	protected Intbox 				finMaxTerm; 						// autoWired
	protected Intbox 				finDftTerms;	 					// autoWired
	protected Combobox 				cbfinRepayMethod; 					// autoWired
	protected Checkbox 				finIsAlwPartialRpy; 				// autoWired
	protected Intbox 				finODRpyTries; 						// autoWired
	protected Checkbox 				finIsAlwDifferment; 				// autoWired
	protected Intbox 				finMaxDifferment; 					// autoWired
	protected Checkbox 				finIsAlwFrqDifferment; 				// autoWired
	protected Intbox 				finMaxFrqDifferment; 				// autoWired	
	protected Combobox 				cbFinScheduleOn; 					// autoWired
	protected Checkbox 				finPftUnChanged; 					// autoWired
	
	protected Div 					repayDetailDiv; 					// autoWired
	
	//Overdue Penalty Details
	protected Checkbox 				applyODPenalty; 					// autoWired
	protected Checkbox 				oDIncGrcDays; 						// autoWired
	protected Combobox 				oDChargeType; 						// autoWired
	protected Intbox 				oDGraceDays; 						// autoWired
	protected Combobox 				oDChargeCalOn; 						// autoWired
	protected Decimalbox 			oDChargeAmtOrPerc; 					// autoWired
	protected Checkbox 				oDAllowWaiver; 						// autoWired
	protected Decimalbox 			oDMaxWaiverPerc; 					// autoWired
	
	protected Space 				space_oDChargeAmtOrPerc;			// autoWired
	protected Space 				space_oDMaxWaiverPerc;				// autoWired
	
	//Accounting SetUp Details Tab 
	protected ExtendedCombobox 		finAEAddDsbOD; 						// autoWired
	protected ExtendedCombobox 		finAEAddDsbFD; 						// autoWired
	protected ExtendedCombobox 		finAEAddDsbFDA; 					// autoWired
	protected ExtendedCombobox 		finAEAmzNorm; 						// autoWired
	protected ExtendedCombobox 		finAEAmzSusp; 						// autoWired
	protected ExtendedCombobox 		finAEToNoAmz; 						// autoWired
	protected ExtendedCombobox 		finToAmz; 							// autoWired
	protected ExtendedCombobox 		finMAmz; 							// autoWired
	protected ExtendedCombobox 		finAERateChg; 						// autoWired
	protected ExtendedCombobox 		finAERepay; 						// autoWired
	protected ExtendedCombobox 		finLatePayRule; 					// autoWired
	protected ExtendedCombobox 		finInstDate; 						// autoWired
	protected ExtendedCombobox 		finAEWriteOff; 						// autoWired
	protected ExtendedCombobox 		finAEWriteOffBK; 					// autoWired
	protected ExtendedCombobox 		finAEGraceEnd; 						// autoWired
	protected ExtendedCombobox 		finProvision; 						// autoWired
	protected ExtendedCombobox 		finSchdChange; 						// autoWired
	protected ExtendedCombobox 		finDepreciationRule; 				// autoWired
	protected ExtendedCombobox 		finDeffreq; 						// autoWired
	protected ExtendedCombobox 		finDefRepay; 						// autoWired
	protected ExtendedCombobox 		finAECapitalize; 					// autoWired
	protected ExtendedCombobox 		finAEProgClaim; 					// autoWired
	protected ExtendedCombobox 		finAEMaturity; 						// autoWired

	protected Row					row_ProgCliamEvent;					// autoWired

	// Stepping Details
	protected Checkbox 				stepFinance; 					// autoWired
	protected Checkbox 				steppingMandatory; 		        // autoWired
	protected Checkbox 				allowManualSteps; 			    // autoWired
	protected Combobox              dftStepPolicy;	                // autoWired
	protected Space                 sp_alwdStepPolices;             // autoWired
	protected Space                 sp_dftStepPolicy;               // autoWired
	protected Textbox               stepPolicyCode;                 // autoWired
	protected Textbox               lovDescStepPolicyCodename;      // autoWired
	protected Groupbox              gb_SteppingDetails;             // autoWired
	protected Label                 label_FinanceTypeDialog_AllowedStepPolicies; // autoWired
	protected Hbox                  hbox_alwdStepPolicies;                       // autoWired
	protected Row                   row_isSteppingMandatory;                     // autoWired
	protected Row                   row_allowManualSteps;                        // autoWired
	protected Button                btnSearchStepPolicy;
	
	//Other
	protected Label 				recordStatus; 						// autoWired
	protected Radiogroup 			userAction;							// autoWired
	protected Groupbox 				groupboxWf;							// autoWired

	protected Space 				space_FinRvwRateApplFor; 			// autoWired
	protected Space 				space_FinGrcRvwRateApplFor; 		// autoWired
	protected Space 				space_cbfinSchCalCodeOnRvw; 		// autoWired
	protected Space 				space_FinMinDownPayAmount; 			// autoWired
	protected Space 				space_finGrcSchdMthd; 				// autoWired
	protected Space 				space_finGrcIndBaseRate; 			// autoWired
	
	// ========= Hidden Fields
	protected ExtendedCombobox 		pftPayAcType; 						// autoWired
	protected Textbox 				finBankContingentAcType; 			// autoWired
	protected Textbox 				finContingentAcType; 				// autoWired
	protected ExtendedCombobox 		finSuspAcType; 						// autoWired
	protected ExtendedCombobox 		finProvisionAcType; 				// autoWired
	protected Checkbox 				finIsOpenPftPayAcc; 				// autoWired
	protected Textbox 				finDftStmtFrq; 						// autoWired
	protected Combobox 				cbfinDftStmtFrqCode; 				// autoWired
	protected Combobox 				cbfinDftStmtFrqMth; 				// autoWired
	protected Combobox 				cbfinDftStmtFrqDays; 				// autoWired
	protected Intbox 				finHistRetension; 					// autoWired
	protected Checkbox 				finCollateralReq; 					// autoWired
	protected Textbox 				finDepreciationFrq; 				// autoWired
	protected Combobox 				cbfinDepreciationCode; 				// autoWired
	protected Combobox 				cbfinDepreciationMth; 				// autoWired
	protected Combobox 				cbfinDepreciationDays; 				// autoWired
	protected Checkbox 				finCollateralOvrride; 				// autoWired
	protected Decimalbox 			fInGrcMinRate; 						// autoWired
	protected Decimalbox 			finGrcMaxRate; 						// autoWired
	protected Combobox 				cbFinGrcScheduleOn; 				// autoWired
	protected Checkbox 				finGrcAlwRateChgAnyDate; 			// autoWired
	protected Decimalbox 			fInMinRate; 						// autoWired
	protected Decimalbox 			finMaxRate; 						// autoWired
	protected Checkbox 				finAlwRateChangeAnyDate; 			// autoWired
	protected Checkbox 				finIsAlwEarlyRpy; 					// autoWired
	protected Checkbox 				finIsAlwEarlySettle; 				// autoWired
	protected Textbox 				finAEEarlyPay; 						// autoWired
	protected Textbox 				lovDescFinAEEarlyPayName;			// autoWired
	protected ExtendedCombobox 		finAEEarlySettle; 					// autoWired
	protected Button 				btnSearchFinBankContingentAcType; 	// autoWired
	protected Textbox 				lovDescFinBankContingentAcTypeName;	// autoWired
	protected Button 				btnSearchFinContingentAcType; 		// autoWired
	protected Textbox 				lovDescFinContingentAcTypeName;		// autoWired
	
	protected Label 				label_FinanceTypeSearch_FinCapitalize;
	//==============

	/*
	 * overHanded per parameters old value Var's for edit mode. that we can
	 * check if something on the values are edited since the last initialized.
	 */
	//Basic Details Tab  
	private transient String 		oldVar_finType;
	private transient String 		oldVar_finTypeDesc;
	private transient String 		oldVar_finCcy;
	private transient String 		oldVar_finDivision;
	private transient int 			oldVar_finDaysCalType;
	private transient String 		oldVar_finAcType;
	private transient boolean 		oldVar_finIsOpenNewFinAc;
	private transient BigDecimal 	oldVar_finMinAmount;
	private transient BigDecimal 	oldVar_finMaxAmount;
	private transient boolean 		oldVar_finIsDwPayRequired;
	private transient BigDecimal 	oldVar_finMinDownPayAmount;
	private transient boolean 		oldVar_finIsGenRef;
	private transient boolean 		oldVar_fInIsAlwGrace;
	private transient boolean 		oldVar_finIsAlwMD;
	private transient boolean 		oldVar_finDepreciationReq;
	private transient boolean 		oldVar_finCommitmentReq;
	private transient boolean 		oldVar_finCommitmentOvrride;
	private transient boolean 		oldVar_limitRequired;
	private transient boolean 		oldVar_overrideLimit;
	private transient boolean 		oldVar_allowRIAInvestment;
	private transient boolean 		oldVar_allowParllelFinance;
	private transient boolean 		oldVar_finIsActive;

	//Grace Period Details Tab  
	private transient int 			oldVar_finGrcRateType;
	private transient BigDecimal 	oldVar_finGrcIntRate;
	private transient String 		oldVar_finGrcBaseRate;
	private transient String 		oldVar_finGrcSplRate;
	private transient BigDecimal 	oldVar_finGrcMargin;
	private transient String 		oldVar_finGrcDftIntFrq;
	private transient boolean 		oldVar_finIsAlwGrcRepay;
	private transient int 			oldVar_finGrcSchdMthd;
	private transient boolean 		oldVar_finGrcIsIntCpz;
	private transient String 		oldVar_finGrcCpzFrq;
	private transient boolean 		oldVar_finGrcIsRvwAlw;
	private transient String 		oldVar_finGrcRvwFrq;
	private transient boolean 		oldVar_FinGrcAlwIndRate;
	private transient String 		oldVar_lovDescFinGrcIndBaseRateName;
	private transient boolean 		oldVar_finIsIntCpzAtGrcEnd;

	//Repay Period Details Tab 
	private transient int 			oldVar_finRateType;
	private transient BigDecimal 	oldVar_finIntRate;
	private transient String 		oldVar_finBaseRate;
	private transient String 		oldVar_finSplRate;
	private transient BigDecimal 	oldVar_finMargin;
	private transient String 		oldVar_finDftIntFrq;
	private transient boolean 		oldVar_finRepayPftOnFrq;
	private transient String 		oldVar_finRpyFrq;
	private transient int 			oldVar_finSchdMthd;
	private transient boolean 		oldVar_finIsIntCpz;
	private transient String 		oldVar_finCpzFrq;
	private transient boolean 		oldVar_finIsRvwAlw;
	private transient String 		oldVar_finRvwFrq;
	private transient int 			oldVar_finSchCalCodeOnRvw;
	private transient boolean 		oldVar_FinAlwIndRate;
	private transient int 			oldVar_finMinTerm;
	private transient int 			oldVar_finMaxTerm;
	private transient int 			oldVar_finDftTerms;
	private transient int 			oldVar_finRepayMethod;
	private transient boolean 		oldVar_finIsAlwPartialRpy;
	private transient int 			oldVar_finODRpyTries;
	private transient boolean 		oldVar_finIsAlwDifferment;
	private transient int 			oldVar_finMaxDifferment;
	private transient boolean 		oldVar_finIsAlwFrqDifferment;
	private transient int 			oldVar_finMaxFrqDifferment;
	private transient boolean 		oldVar_finPftUnChanged;
	
	private transient boolean 		oldVar_applyODPenalty;
	private transient boolean 		oldVar_oDIncGrcDays;
	private transient String 		oldVar_oDChargeType;
	private transient int	 		oldVar_oDGraceDays;
	private transient String 		oldVar_oDChargeCalOn;
	private transient BigDecimal 	oldVar_oDChargeAmtOrPerc;
	private transient boolean 		oldVar_oDAllowWaiver;
	private transient BigDecimal	oldVar_oDMaxWaiverPerc;

	//Accounting Set Details Tab  
	private transient String 		oldVar_finAEAddDsbOD;
	private transient String 		oldVar_finAEAddDsbFD;
	private transient String 		oldVar_finAEAddDsbFDA;
	private transient String 		oldVar_finAEAmzNorm;
	private transient String 		oldVar_finAEAmzSusp;
	private transient String 		oldVar_finAEToNoAmz;
	private transient String 		oldVar_finToAmz;
	private transient String 		oldVar_finMAmz;
	private transient String 		oldVar_finAERateChg;
	private transient String 		oldVar_finAERepay;
	private transient String 		oldVar_finLatePayRule;
	private transient String 		oldVar_FinInstDateName;
	private transient String 		oldVar_finAEWriteOff;
	private transient String 		oldVar_finAEWriteOffBK;
	private transient String 		oldVar_finAEGraceEnd;
	private transient String 		oldVar_FinProvisionName;
	private transient String 		oldVar_FinSchdChange;
	private transient String 		oldVar_finDeffreq;
	private transient String 		oldVar_finDefRepay;
	private transient String 		oldVar_FinAECapitalize;
	private transient String 		oldVar_FinAEProgClaim;
	private transient String 		oldVar_FinAEMaturity;

	private transient boolean 		oldVar_stepFinance; 					
	private transient String        oldVar_alwdStepPolices;                
	private transient boolean 		oldVar_steppingMandatory; 		    
	private transient boolean 		oldVar_allowManualSteps; 			   
	private transient String 		oldVar_dftStepPolicy; 			   
	
	
	//other	
	private transient String 		oldVar_recordStatus;

	//Hidden
	private transient boolean 		oldVar_finCollateralReq;
	private transient String 		oldVar_finDepreciationFrq;
	private transient BigDecimal 	oldVar_finGrcMaxRate;
	private transient BigDecimal 	oldVar_fInGrcMinRate;
	private transient int 			oldVar_finHistRetension;
	private transient String 		oldVar_pftPayAcType;
	private transient String 		oldVar_finContingentAcType;
	private transient String 		oldVar_finSuspAcType;
	private transient String 		oldVar_finProvisionAcType;
	private transient String 		oldVar_finBankContingentAcType;
	private transient String 		oldVar_lovDescFinContingentAcTypeName;
	private transient String 		oldVar_lovDescFinBankCtngAcTypeName;
	private transient BigDecimal 	oldVar_fInMinRate;
	private transient BigDecimal 	oldVar_finMaxRate;
	private transient String 		oldVar_finDftStmtFrq;
	private transient boolean 		oldVar_finCollateralOvrride;
	private transient boolean 		oldVar_finIsOpenPftPayAcc;
	private transient boolean 		oldVar_finIsAlwEarlyRpy;
	private transient boolean 		oldVar_finIsAlwEarlySettle;
	private transient String 		oldVar_finAEEarlySettle;
	private transient String 		oldVar_finAEEarlyPay;
	private transient String		oldVar_lovDescFinAEEarlyPayName;
	private transient boolean 		oldVar_finAlwRateChangeAnyDate;
	private transient boolean 		oldVar_finGrcAlwRateChgAnyDate;
	//==============

	// not auto wired Var's
	private FinanceType financeType; // overHanded per parameters
	private transient FinanceTypeListCtrl financeTypeListCtrl;
	// new Variables
	private int countRows = PennantConstants.listGridSize;
	private transient boolean validationOn;
	private boolean notes_Entered = false;
	private boolean validate = false;
	protected boolean isCopyProcess = false;
	Calendar calender = Calendar.getInstance();
	private transient AccountingSet accSet = new AccountingSet();

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_FinanceTypeDialog_"; // autoWire
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autoWire
	protected Button btnEdit; // autoWire
	protected Button btnDelete; // autoWire
	protected Button btnSave; // autoWire
	protected Button btnCancel; // autoWire
	protected Button btnClose; // autoWire
	protected Button btnHelp; // autoWire
	protected Button btnNotes; // autoWire
	protected Button btnCopyTo;

	// ServiceDAOs / Domain Classes
	private transient FinanceTypeService financeTypeService;
	private transient PagedListService pagedListService;

	private Tab basicDetails; // autoWired
	private Tab gracePeriod; // autoWired
	private Tab repayment; // autoWired
	private Tab accountingEvent; // autoWired
	private Tab finTypeAccountDetails; // autoWired
	private Tab extendedDetails;    // autoWired

	private final List<ValueLabel> pftDays = PennantAppUtil.getProfitDaysBasis();
	private final List<ValueLabel> RvwRateAppPeriods = PennantStaticListUtil.getReviewRateAppliedPeriods();
	private final List<ValueLabel> schMthds = PennantAppUtil.getScheduleMethod();
	private final List<ValueLabel> schPftRateType = PennantStaticListUtil.getInterestRateType(true);
	private final List<ValueLabel> rpyMthd = PennantAppUtil.getRepayMethods();
	private final List<ValueLabel> scCalCode = PennantStaticListUtil.getSchCalCodes();
	private final List<ValueLabel> scheduleOn = PennantStaticListUtil.getScheduleOn();
	private final List<ValueLabel> stepPolicies = PennantAppUtil.getStepPoliciesList();

	private int borderLayoutHeight = 0;
	
    protected Button  btnNew_FinTypeAccount;
	protected Listbox listBoxFinTypeAccounts;
	private List<FinTypeAccount> finTypeAccountList = new ArrayList<FinTypeAccount>();

	/** default constructor.<br> */
	public FinanceTypeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FinanceType object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceTypeDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel,
		        this.btnClose, this.btnNotes);

		/* get the params map that are overHanded by creation. */
		final Map<String, Object> args = getCreationArgsMap(event);

		/* READ OVERHANDED params ! */
		if (args.containsKey("financeType")) {
			this.financeType = (FinanceType) args.get("financeType");
			FinanceType befImage = new FinanceType("");
			BeanUtils.copyProperties(this.financeType, befImage);
			this.financeType.setBefImage(befImage);
			setFinanceType(this.financeType);
		} else {
			setFinanceType(null);
		}
		if (args.containsKey("isCopyProcess")) {
			this.isCopyProcess = (Boolean) args.get("isCopyProcess");
		}
		doLoadWorkFlow(this.financeType.isWorkflow(), this.financeType.getWorkflowId(), this.financeType.getNextTaskId());
		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "FinanceTypeDialog");
		}
		/*
		 * READ OVERHANDED params ! we get the financeTypeListWindow controller. So we have access to it and can
		 * synchronize the shown data when we do insert, edit or delete financeType here.
		 */
		if (args.containsKey("financeTypeListCtrl")) {
			setFinanceTypeListCtrl((FinanceTypeListCtrl) args.get("financeTypeListCtrl"));
		} else {
			setFinanceTypeListCtrl(null);
		}
		
		this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight"))
		.getValue().intValue()- PennantConstants.borderlayoutMainNorth;
		
		this.repayDetailDiv.setHeight(this.borderLayoutHeight - 100+ "px");// 425px
		this.listBoxFinTypeAccounts.setHeight(this.borderLayoutHeight - 145 + "px");
		
		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinanceType());
		logger.debug("Leaving" + event.toString());
	}

	/** Set the properties of the fields, like maxLength.<br> */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.finType.setMaxlength(8);
		this.finTypeDesc.setMaxlength(50);
		
		this.finCcy.setMaxlength(3);
		this.finCcy.setMandatoryStyle(true);
		this.finCcy.setModuleName("Currency");
		this.finCcy.setValueColumn("CcyCode");
		this.finCcy.setDescColumn("CcyDesc");
		this.finCcy.setValidateColumns(new String[] { "CcyCode" });
		
		this.finAcType.setMaxlength(8);
		this.finAcType.setMandatoryStyle(true);
		this.finAcType.setModuleName("AccountType");
		this.finAcType.setValueColumn("AcType");
		this.finAcType.setDescColumn("AcTypeDesc");
		this.finAcType.setValidateColumns(new String[] { "AcType" });
		Filter[] finAcTypeFilters = new Filter[2];
		finAcTypeFilters[0] = new Filter("AcPurpose", "F", Filter.OP_EQUAL);
		finAcTypeFilters[1] = new Filter("internalAc", "0", Filter.OP_EQUAL);
		this.finAcType.setFilters(finAcTypeFilters);
		
		this.pftPayAcType.setMaxlength(8);
		this.pftPayAcType.setMandatoryStyle(true);
		this.pftPayAcType.setModuleName("AccountType");
		this.pftPayAcType.setValueColumn("AcType");
		this.pftPayAcType.setDescColumn("AcTypeDesc");
		this.pftPayAcType.setValidateColumns(new String[] { "AcType" });
		Filter[] pftPayAcTypeFilters = new Filter[2];
		pftPayAcTypeFilters[0] = new Filter("AcPurpose", "U", Filter.OP_EQUAL);
		pftPayAcTypeFilters[1] = new Filter("internalAc", "0", Filter.OP_EQUAL);
		this.pftPayAcType.setFilters(pftPayAcTypeFilters);
		
		this.finSuspAcType.setMaxlength(8);
		this.finSuspAcType.setMandatoryStyle(true);
		this.finSuspAcType.setModuleName("AccountType");
		this.finSuspAcType.setValueColumn("AcType");
		this.finSuspAcType.setDescColumn("AcTypeDesc");
		this.finSuspAcType.setValidateColumns(new String[] { "AcType" });
		Filter[] finSuspAcTypeFilters = new Filter[2];
		finSuspAcTypeFilters[0] = new Filter("AcPurpose", "S", Filter.OP_EQUAL);
		finSuspAcTypeFilters[1] = new Filter("internalAc", "0", Filter.OP_EQUAL);
		this.finSuspAcType.setFilters(finSuspAcTypeFilters);
		
		this.finProvisionAcType.setMaxlength(8);
		this.finProvisionAcType.setMandatoryStyle(true);
		this.finProvisionAcType.setModuleName("AccountType");
		this.finProvisionAcType.setValueColumn("AcType");
		this.finProvisionAcType.setDescColumn("AcTypeDesc");
		this.finProvisionAcType.setValidateColumns(new String[] { "AcType" });
		Filter[] finProvisionAcTypeFilters = new Filter[2];
		finProvisionAcTypeFilters[0] = new Filter("AcPurpose", "P", Filter.OP_EQUAL);
		finProvisionAcTypeFilters[1] = new Filter("internalAc", "0", Filter.OP_EQUAL);
		this.finProvisionAcType.setFilters(finProvisionAcTypeFilters);
		
		this.finDivision.setMaxlength(8);
		this.finDivision.setMandatoryStyle(true);
		this.finDivision.setModuleName("DivisionDetail");
		this.finDivision.setValueColumn("DivisionCode");
		this.finDivision.setDescColumn("DivisionCodeDesc");
		this.finDivision.setValidateColumns(new String[] { "DivisionCode" });
		
		this.finGrcBaseRate.setMaxlength(8);
		this.finGrcBaseRate.setModuleName("BaseRateCode");
		this.finGrcBaseRate.setValueColumn("BRType");
		this.finGrcBaseRate.setDescColumn("BRTypeDesc");
		this.finGrcBaseRate.setValidateColumns(new String[] { "BRType" });
		
		this.finGrcSplRate.setMaxlength(8);
		this.finGrcSplRate.setModuleName("SplRateCode");
		this.finGrcSplRate.setValueColumn("SRType");
		this.finGrcSplRate.setDescColumn("SRTypeDesc");
		this.finGrcSplRate.setValidateColumns(new String[] { "SRType" });
		
		this.finBaseRate.setMaxlength(8);
		this.finBaseRate.setModuleName("BaseRateCode");
		this.finBaseRate.setValueColumn("BRType");
		this.finBaseRate.setDescColumn("BRTypeDesc");
		this.finBaseRate.setValidateColumns(new String[] { "BRType" });
		
		this.finSplRate.setMaxlength(8);
		this.finSplRate.setModuleName("SplRateCode");
		this.finSplRate.setValueColumn("SRType");
		this.finSplRate.setDescColumn("SRTypeDesc");
		this.finSplRate.setValidateColumns(new String[] { "SRType" });
		
		this.finIndBaseRate.setMaxlength(8);
		this.finIndBaseRate.setModuleName("BaseRateCode");
		this.finIndBaseRate.setValueColumn("BRType");
		this.finIndBaseRate.setDescColumn("BRTypeDesc");
		this.finIndBaseRate.setValidateColumns(new String[] { "BRType" });

		
		this.finAEAddDsbOD.setInputAllowed(false);
		this.finAEAddDsbOD.setDisplayStyle(3);
		this.finAEAddDsbOD.setMandatoryStyle(true);
		this.finAEAddDsbOD.setModuleName("AccountingSet");
		this.finAEAddDsbOD.setValueColumn("EventCode");
		this.finAEAddDsbOD.setDescColumn("AccountSetCode");
		this.finAEAddDsbOD.setValidateColumns(new String[] { "EventCode" });
		this.finAEAddDsbOD.setFilters(getFiltersByCheckingRIA("EventCode", "ADDDBSP", Filter.OP_LIKE));
		
		this.finAEAddDsbFD.setInputAllowed(false);
		this.finAEAddDsbFD.setDisplayStyle(3);
		this.finAEAddDsbFD.setMandatoryStyle(true);
		this.finAEAddDsbFD.setModuleName("AccountingSet");
		this.finAEAddDsbFD.setValueColumn("EventCode");
		this.finAEAddDsbFD.setDescColumn("AccountSetCode");
		this.finAEAddDsbFD.setValidateColumns(new String[] { "EventCode" });
		this.finAEAddDsbFD.setFilters(getFiltersByCheckingRIA("EventCode", "ADDDBSF", Filter.OP_LIKE));
		
		this.finAEAddDsbFDA.setInputAllowed(false);
		this.finAEAddDsbFDA.setDisplayStyle(3);
		this.finAEAddDsbFDA.setMandatoryStyle(true);
		this.finAEAddDsbFDA.setModuleName("AccountingSet");
		this.finAEAddDsbFDA.setValueColumn("EventCode");
		this.finAEAddDsbFDA.setDescColumn("AccountSetCode");
		this.finAEAddDsbFDA.setValidateColumns(new String[] { "EventCode" });
		this.finAEAddDsbFDA.setFilters(getFiltersByCheckingRIA("EventCode", "ADDDBSN", Filter.OP_LIKE));
		
		this.finMAmz.setInputAllowed(false);
		this.finMAmz.setDisplayStyle(3);
		this.finMAmz.setMandatoryStyle(true);
		this.finMAmz.setModuleName("AccountingSet");
		this.finMAmz.setValueColumn("EventCode");
		this.finMAmz.setDescColumn("AccountSetCode");
		this.finMAmz.setValidateColumns(new String[] { "EventCode" });
		this.finMAmz.setFilters(getFiltersByCheckingRIA("EventCode", "AMZ_MON", Filter.OP_LIKE));
		
		this.finAEAmzNorm.setInputAllowed(false);
		this.finAEAmzNorm.setDisplayStyle(3);
		this.finAEAmzNorm.setMandatoryStyle(true);
		this.finAEAmzNorm.setModuleName("AccountingSet");
		this.finAEAmzNorm.setValueColumn("EventCode");
		this.finAEAmzNorm.setDescColumn("AccountSetCode");
		this.finAEAmzNorm.setValidateColumns(new String[] { "EventCode" });
		this.finAEAmzNorm.setFilters(getFiltersByCheckingRIA("EventCode", "AMZ", Filter.OP_LIKE));
		
		this.finAEAmzSusp.setInputAllowed(false);
		this.finAEAmzSusp.setDisplayStyle(3);
		this.finAEAmzSusp.setMandatoryStyle(true);
		this.finAEAmzSusp.setModuleName("AccountingSet");
		this.finAEAmzSusp.setValueColumn("EventCode");
		this.finAEAmzSusp.setDescColumn("AccountSetCode");
		this.finAEAmzSusp.setValidateColumns(new String[] { "EventCode" });
		this.finAEAmzSusp.setFilters(getFiltersByCheckingRIA("EventCode", "AMZSUSP", Filter.OP_LIKE));
		
		this.finAEToNoAmz.setInputAllowed(false);
		this.finAEToNoAmz.setDisplayStyle(3);
		this.finAEToNoAmz.setMandatoryStyle(true);
		this.finAEToNoAmz.setModuleName("AccountingSet");
		this.finAEToNoAmz.setValueColumn("EventCode");
		this.finAEToNoAmz.setDescColumn("AccountSetCode");
		this.finAEToNoAmz.setValidateColumns(new String[] { "EventCode" });
		this.finAEToNoAmz.setFilters(getFiltersByCheckingRIA("EventCode", "M_NONAMZ", Filter.OP_LIKE));
		
		this.finToAmz.setInputAllowed(false);
		this.finToAmz.setDisplayStyle(3);
		this.finToAmz.setMandatoryStyle(true);
		this.finToAmz.setModuleName("AccountingSet");
		this.finToAmz.setValueColumn("EventCode");
		this.finToAmz.setDescColumn("AccountSetCode");
		this.finToAmz.setValidateColumns(new String[] { "EventCode" });
		this.finToAmz.setFilters(getFiltersByCheckingRIA("EventCode", "M_AMZ", Filter.OP_LIKE));
		
		this.finAERateChg.setInputAllowed(false);
		this.finAERateChg.setDisplayStyle(3);
		this.finAERateChg.setMandatoryStyle(true);
		this.finAERateChg.setModuleName("AccountingSet");
		this.finAERateChg.setValueColumn("EventCode");
		this.finAERateChg.setDescColumn("AccountSetCode");
		this.finAERateChg.setValidateColumns(new String[] { "EventCode" });
		this.finAERateChg.setFilters(getFiltersByCheckingRIA("EventCode", "RATCHG", Filter.OP_LIKE));
		
		this.finAERepay.setInputAllowed(false);
		this.finAERepay.setDisplayStyle(3);
		this.finAERepay.setMandatoryStyle(true);
		this.finAERepay.setModuleName("AccountingSet");
		this.finAERepay.setValueColumn("EventCode");
		this.finAERepay.setDescColumn("AccountSetCode");
		this.finAERepay.setValidateColumns(new String[] { "EventCode" });
		this.finAERepay.setFilters(getFiltersByCheckingRIA("EventCode", "REPAY", Filter.OP_LIKE));
		
		this.finLatePayRule.setInputAllowed(false);
		this.finLatePayRule.setDisplayStyle(3);
		this.finLatePayRule.setMandatoryStyle(true);
		this.finLatePayRule.setModuleName("AccountingSet");
		this.finLatePayRule.setValueColumn("EventCode");
		this.finLatePayRule.setDescColumn("AccountSetCode");
		this.finLatePayRule.setValidateColumns(new String[] { "EventCode" });
		this.finLatePayRule.setFilters(getFiltersByCheckingRIA("EventCode", "LATEPAY", Filter.OP_LIKE));
		
		this.finInstDate.setInputAllowed(false);
		this.finInstDate.setDisplayStyle(3);
		this.finInstDate.setMandatoryStyle(true);
		this.finInstDate.setModuleName("AccountingSet");
		this.finInstDate.setValueColumn("EventCode");
		this.finInstDate.setDescColumn("AccountSetCode");
		this.finInstDate.setValidateColumns(new String[] { "EventCode" });
		this.finInstDate.setFilters(getFiltersByCheckingRIA("EventCode", "INSTDATE", Filter.OP_LIKE));
		
		this.finAEGraceEnd.setInputAllowed(false);
		this.finAEGraceEnd.setDisplayStyle(3);
		this.finAEGraceEnd.setMandatoryStyle(true);
		this.finAEGraceEnd.setModuleName("AccountingSet");
		this.finAEGraceEnd.setValueColumn("EventCode");
		this.finAEGraceEnd.setDescColumn("AccountSetCode");
		this.finAEGraceEnd.setValidateColumns(new String[] { "EventCode" });
		this.finAEGraceEnd.setFilters(getFiltersByCheckingRIA("EventCode", "GRACEEND", Filter.OP_LIKE));
		
		this.finProvision.setInputAllowed(false);
		this.finProvision.setDisplayStyle(3);
		this.finProvision.setMandatoryStyle(true);
		this.finProvision.setModuleName("AccountingSet");
		this.finProvision.setValueColumn("EventCode");
		this.finProvision.setDescColumn("AccountSetCode");
		this.finProvision.setValidateColumns(new String[] { "EventCode" });
		this.finProvision.setFilters(new Filter[]{new Filter("EventCode", "PROVSN", Filter.OP_LIKE)});
		
		this.finSchdChange.setInputAllowed(false);
		this.finSchdChange.setDisplayStyle(3);
		this.finSchdChange.setMandatoryStyle(true);
		this.finSchdChange.setModuleName("AccountingSet");
		this.finSchdChange.setValueColumn("EventCode");
		this.finSchdChange.setDescColumn("AccountSetCode");
		this.finSchdChange.setValidateColumns(new String[] { "EventCode" });
		this.finSchdChange.setFilters(getFiltersByCheckingRIA("EventCode", "SCDCHG", Filter.OP_LIKE));
		
		this.finDepreciationRule.setInputAllowed(false);
		this.finDepreciationRule.setDisplayStyle(3);
		this.finDepreciationRule.setMandatoryStyle(true);
		this.finDepreciationRule.setModuleName("AccountingSet");
		this.finDepreciationRule.setValueColumn("EventCode");
		this.finDepreciationRule.setDescColumn("AccountSetCode");
		this.finDepreciationRule.setValidateColumns(new String[] { "EventCode" });
		this.finDepreciationRule.setFilters(getFiltersByCheckingRIA("EventCode", "DPRCIATE", Filter.OP_LIKE));
		
		this.finDeffreq.setInputAllowed(false);
		this.finDeffreq.setDisplayStyle(3);
		this.finDeffreq.setMandatoryStyle(true);
		this.finDeffreq.setModuleName("AccountingSet");
		this.finDeffreq.setValueColumn("EventCode");
		this.finDeffreq.setDescColumn("AccountSetCode");
		this.finDeffreq.setValidateColumns(new String[] { "EventCode" });
		this.finDeffreq.setFilters(getFiltersByCheckingRIA("EventCode", "DEFFRQ", Filter.OP_LIKE));
		
		this.finDefRepay.setInputAllowed(false);
		this.finDefRepay.setDisplayStyle(3);
		this.finDefRepay.setMandatoryStyle(true);
		this.finDefRepay.setModuleName("AccountingSet");
		this.finDefRepay.setValueColumn("EventCode");
		this.finDefRepay.setDescColumn("AccountSetCode");
		this.finDefRepay.setValidateColumns(new String[] { "EventCode" });
		this.finDefRepay.setFilters(getFiltersByCheckingRIA("EventCode", "DEFRPY", Filter.OP_LIKE));
		
		this.finAECapitalize.setInputAllowed(false);
		this.finAECapitalize.setDisplayStyle(3);
		this.finAECapitalize.setMandatoryStyle(true);
		this.finAECapitalize.setModuleName("AccountingSet");
		this.finAECapitalize.setValueColumn("EventCode");
		this.finAECapitalize.setDescColumn("AccountSetCode");
		this.finAECapitalize.setValidateColumns(new String[] { "EventCode" });
		this.finAECapitalize.setFilters(getFiltersByCheckingRIA("EventCode", "COMPOUND", Filter.OP_LIKE));
		
		this.finAEWriteOff.setInputAllowed(false);
		this.finAEWriteOff.setDisplayStyle(3);
		this.finAEWriteOff.setMandatoryStyle(true);
		this.finAEWriteOff.setModuleName("AccountingSet");
		this.finAEWriteOff.setValueColumn("EventCode");
		this.finAEWriteOff.setDescColumn("AccountSetCode");
		this.finAEWriteOff.setValidateColumns(new String[] { "EventCode" });
		this.finAEWriteOff.setFilters(getFiltersByCheckingRIA("EventCode", "WRITEOFF", Filter.OP_LIKE));
		
		this.finAEWriteOffBK.setInputAllowed(false);
		this.finAEWriteOffBK.setDisplayStyle(3);
		this.finAEWriteOffBK.setMandatoryStyle(true);
		this.finAEWriteOffBK.setModuleName("AccountingSet");
		this.finAEWriteOffBK.setValueColumn("EventCode");
		this.finAEWriteOffBK.setDescColumn("AccountSetCode");
		this.finAEWriteOffBK.setValidateColumns(new String[] { "EventCode" });
		this.finAEWriteOffBK.setFilters(getFiltersByCheckingRIA("EventCode", "WRITEBK", Filter.OP_LIKE));
		
		this.finAEMaturity.setInputAllowed(false);
		this.finAEMaturity.setDisplayStyle(3);
		this.finAEMaturity.setMandatoryStyle(true);
		this.finAEMaturity.setModuleName("AccountingSet");
		this.finAEMaturity.setValueColumn("EventCode");
		this.finAEMaturity.setDescColumn("AccountSetCode");
		this.finAEMaturity.setValidateColumns(new String[] { "EventCode" });
		this.finAEMaturity.setFilters(getFiltersByCheckingRIA("EventCode", "MATURITY", Filter.OP_EQUAL));
		
		this.finAEProgClaim.setInputAllowed(false);
		this.finAEProgClaim.setDisplayStyle(3);
		this.finAEProgClaim.setMandatoryStyle(true);
		this.finAEProgClaim.setModuleName("AccountingSet");
		this.finAEProgClaim.setValueColumn("EventCode");
		this.finAEProgClaim.setDescColumn("AccountSetCode");
		this.finAEProgClaim.setValidateColumns(new String[] { "EventCode" });
		this.finAEProgClaim.setFilters(getFiltersByCheckingRIA("EventCode", "PRGCLAIM", Filter.OP_EQUAL));
		
		this.finAEEarlySettle.setInputAllowed(false);
		this.finAEEarlySettle.setDisplayStyle(3);
		this.finAEEarlySettle.setMandatoryStyle(true);
		this.finAEEarlySettle.setModuleName("AccountingSet");
		this.finAEEarlySettle.setValueColumn("EventCode");
		this.finAEEarlySettle.setDescColumn("AccountSetCode");
		this.finAEEarlySettle.setValidateColumns(new String[] { "EventCode" });
		this.finAEEarlySettle.setFilters(getFiltersByCheckingRIA("EventCode", "EARLYSTL", Filter.OP_EQUAL));

		
		this.finContingentAcType.setMaxlength(8);
		this.finBankContingentAcType.setMaxlength(8);
		this.finMaxAmount.setMaxlength(18);
		this.finMaxAmount.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceType().getLovDescFinFormetter()));
		this.finMinAmount.setMaxlength(18);
		this.finMinAmount.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceType().getLovDescFinFormetter()));
		this.finHistRetension.setMaxlength(3);

		this.finIntRate.setMaxlength(13);
		this.finIntRate.setFormat(PennantConstants.rateFormate9);
		this.finIntRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.finIntRate.setScale(9);
		this.fInMinRate.setMaxlength(13);
		this.fInMinRate.setFormat(PennantConstants.rateFormate9);
		this.fInMinRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.fInMinRate.setScale(9);
		this.finMaxRate.setMaxlength(13);
		this.finMaxRate.setFormat(PennantConstants.rateFormate9);
		this.finMaxRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.finMaxRate.setScale(9);
		this.finGrcIntRate.setMaxlength(13);
		this.finGrcIntRate.setFormat(PennantConstants.rateFormate9);
		this.finGrcIntRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.finGrcIntRate.setScale(9);
		this.fInGrcMinRate.setMaxlength(13);
		this.fInGrcMinRate.setFormat(PennantConstants.rateFormate9);
		this.fInGrcMinRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.fInGrcMinRate.setScale(9);
		this.finGrcMaxRate.setMaxlength(13);
		this.finGrcMaxRate.setFormat(PennantConstants.rateFormate9);
		this.finGrcMaxRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.finGrcMaxRate.setScale(9);
		this.finMinTerm.setMaxlength(3);
		this.finMaxTerm.setMaxlength(3);
		this.finDftTerms.setMaxlength(3);
		this.finODRpyTries.setMaxlength(3);
		this.finAEEarlyPay.setMaxlength(8);
		this.finGrcIndBaseRate.setMaxlength(8);
		
		//overdue Penalty Details
		this.oDGraceDays.setMaxlength(3);
		this.oDChargeAmtOrPerc.setMaxlength(15);
		this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceType().getLovDescFinFormetter()));
		this.oDMaxWaiverPerc.setMaxlength(6);
		this.oDMaxWaiverPerc.setFormat(PennantApplicationUtil.getAmountFormate(2));
		
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("FinanceTypeDialog");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinanceTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceTypeDialog_btnSave"));
		this.btnCancel.setVisible(false);
		this.btnCopyTo.setVisible(getUserWorkspace().isAllowed("button_FinanceTypeDialog_btnCopyTo"));
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
	public void onClose$window_FinanceTypeDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_FinanceTypeDialog);
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
	 */
	private void doClose() throws InterruptedException {
		doClearMessages();
		logger.debug("Entering doClose()");
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
			closeDialog(this.window_FinanceTypeDialog, "FinanceTypeDialog");
		}
		logger.debug("Leaving doClose()");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 */
	private void doCancel() {
		logger.debug("Entering doCancel()");
		doResetInitValues();
		doClearMessages();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving doCancel()");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceType
	 *            FinanceType
	 */

	public void doWriteBeanToComponents(FinanceType aFinanceType) {
		logger.debug("Entering");
		//================= Tab 1	
		this.finType.setValue(aFinanceType.getFinType());
		this.finTypeDesc.setValue(aFinanceType.getFinTypeDesc());
		this.finCcy.setValue(aFinanceType.getFinCcy());
		if (aFinanceType.isNewRecord()) {
			this.finCcy.setDescription("");
			this.finAcType.setDescription("");
			this.finSuspAcType.setDescription("");
			this.pftPayAcType.setDescription("");
			this.finProvisionAcType.setDescription("");
			this.finDivision.setDescription("");
		} else {
			this.finCcy.setDescription(aFinanceType.getLovDescFinCcyName());
			this.finAcType.setDescription(aFinanceType.getLovDescFinAcTypeName());
			this.finSuspAcType.setDescription(aFinanceType.getLovDescFinSuspAcTypeName());
			this.pftPayAcType.setDescription(aFinanceType.getLovDescPftPayAcTypeName());
			this.finProvisionAcType.setDescription(aFinanceType.getLovDescFinProvisionAcTypeName());
			this.finDivision.setDescription(aFinanceType.getLovDescFinDivisionName());
		}
		fillComboBox(this.cbfinDaysCalType, aFinanceType.getFinDaysCalType(), pftDays, "");
		this.finAcType.setValue(aFinanceType.getFinAcType());
		this.finIsOpenNewFinAc.setChecked(aFinanceType.isFinIsOpenNewFinAc());
		this.finContingentAcType.setValue(aFinanceType.getFinContingentAcType());
		if (aFinanceType.getLovDescFinContingentAcTypeName() != null) {
			this.lovDescFinContingentAcTypeName.setValue(aFinanceType.getFinContingentAcType() + "-" + aFinanceType.getLovDescFinContingentAcTypeName());
		}
		this.finSuspAcType.setValue(aFinanceType.getFinSuspAcType());
		this.finProvisionAcType.setValue(aFinanceType.getFinProvisionAcType());
		this.finDivision.setValue(aFinanceType.getFinDivision());
		this.pftPayAcType.setValue(aFinanceType.getPftPayAcType());
		//+++++++++++++++++++++++++++++   Hidden     ++++++++++++++++++++++++//
		this.finBankContingentAcType.setValue(aFinanceType.getFinBankContingentAcType());
		if (aFinanceType.getLovDescFinBankContingentAcTypeName() != null) {
			this.lovDescFinBankContingentAcTypeName.setValue(aFinanceType.getFinBankContingentAcType() + "-" + aFinanceType.getLovDescFinBankContingentAcTypeName());
		}
		this.finIsOpenPftPayAcc.setChecked(aFinanceType.isFinIsOpenPftPayAcc());
		//+++++++++++++++++++++++++++++++++++++++++++++++++++++//
		this.finMinAmount.setValue(PennantAppUtil.formateAmount(aFinanceType.getFinMinAmount(), getFinanceType().getLovDescFinFormetter()));
		this.finMaxAmount.setValue(PennantAppUtil.formateAmount(aFinanceType.getFinMaxAmount(), getFinanceType().getLovDescFinFormetter()));
		doFillProductType(this.cbfinProductType, aFinanceType.getFinCategory());
		doFillAssestType(this.cbfinAssetType, String.valueOf(aFinanceType.getFinAssetType()),aFinanceType.getFinCategory());
		this.finIsDwPayRequired.setChecked(aFinanceType.isFinIsDwPayRequired());
		this.finMinDownPayAmount.setValue(aFinanceType.getFinMinDownPayAmount());
		this.finIsGenRef.setChecked(aFinanceType.isFinIsGenRef());
		this.fInIsAlwGrace.setChecked(aFinanceType.isFInIsAlwGrace());
		this.finIsAlwMD.setChecked(aFinanceType.isFinIsAlwMD());
		this.finDepreciationReq.setChecked(aFinanceType.isFinDepreciationReq());
		this.finCommitmentReq.setChecked(aFinanceType.isFinCommitmentReq());
		this.finCommitmentOvrride.setChecked(aFinanceType.isFinCommitmentOvrride());
		doCheckBoxChecked(this.finCommitmentReq.isChecked(), this.finCommitmentOvrride);
		this.limitRequired.setChecked(aFinanceType.isLimitRequired());
		this.overrideLimit.setChecked(aFinanceType.isOverrideLimit());
		doCheckBoxChecked(this.limitRequired.isChecked(), this.overrideLimit);
		this.allowRIAInvestment.setChecked(aFinanceType.isAllowRIAInvestment());
		//this.allowParllelFinance.setChecked(aFinanceType.isAllowParllelFinance());
		this.allowParllelFinance.setChecked(false); // FIXME
		this.finIsActive.setChecked(aFinanceType.isFinIsActive());

		doCheckRIA(aFinanceType.getFinCategory());
		//doCheckFinAEProgClaim(aFinanceType.getLovDescProductCodeName());
		doCheckFinAEMaturity(aFinanceType.getFinCategory());
		checkFinisDownPayreq();
		//================= Tab 2
		fillComboBox(this.cbfinGrcRateType, aFinanceType.getFinGrcRateType(), schPftRateType, ",C,");
		this.finGrcIntRate.setValue(aFinanceType.getFinGrcIntRate());
		this.finGrcBaseRate.setValue(aFinanceType.getFinGrcBaseRate());
		if (aFinanceType.getLovDescFinBaseRateName() != null) {
			this.finBaseRate.setDescription(aFinanceType.getLovDescFinBaseRateName());
		}
		this.finGrcSplRate.setValue(aFinanceType.getFinGrcSplRate());
		if (aFinanceType.getLovDescFinSplRateName() != null) {
			this.finSplRate.setDescription(aFinanceType.getLovDescFinSplRateName());
		}
		this.finGrcMargin.setValue(aFinanceType.getFinGrcMargin());
		this.finGrcDftIntFrq.setValue(aFinanceType.getFinGrcDftIntFrq());
		fillFrqCode(this.cbfinGrcDftIntFrqCode, aFinanceType.getFinGrcDftIntFrq(), isReadOnly("FinanceTypeDialog_finGrcDftIntFrq"));
		fillFrqMth(this.cbfinGrcDftIntFrqMth, aFinanceType.getFinGrcDftIntFrq(), isReadOnly("FinanceTypeDialog_finGrcDftIntFrq"));
		fillFrqDay(this.cbfinGrcDftIntFrqDays, aFinanceType.getFinGrcDftIntFrq(), isReadOnly("FinanceTypeDialog_finGrcDftIntFrq"));
		this.finIsAlwGrcRepay.setChecked(aFinanceType.isFinIsAlwGrcRepay());
		fillComboBox(this.finGrcSchdMthd, aFinanceType.getFinGrcSchdMthd(), schMthds, ",EQUAL,PRI,PRI_PFT,");
		this.finGrcIsIntCpz.setChecked(aFinanceType.isFinGrcIsIntCpz());
		this.finGrcCpzFrq.setValue(aFinanceType.getFinGrcCpzFrq());
		fillFrqCode(this.cbfinGrcCpzFrqCode, aFinanceType.getFinGrcCpzFrq(), isReadOnly("FinanceTypeDialog_finGrcCpzFrq"));
		fillFrqMth(this.cbfinGrcCpzFrqMth, aFinanceType.getFinGrcCpzFrq(), isReadOnly("FinanceTypeDialog_finGrcCpzFrq"));
		fillFrqDay(this.cbfinGrcCpzFrqDays, aFinanceType.getFinGrcCpzFrq(), isReadOnly("FinanceTypeDialog_finGrcCpzFrq"));
		this.finGrcIsRvwAlw.setChecked(aFinanceType.isFinGrcIsRvwAlw());
		this.finGrcRvwFrq.setValue(aFinanceType.getFinGrcRvwFrq());
		fillFrqCode(this.cbfinGrcRvwFrqCode, aFinanceType.getFinGrcRvwFrq(), isReadOnly("FinanceTypeDialog_finGrcRvwFrq"));
		fillFrqMth(this.cbfinGrcRvwFrqMth, aFinanceType.getFinGrcRvwFrq(), isReadOnly("FinanceTypeDialog_finGrcRvwFrq"));
		fillFrqDay(this.cbfinGrcRvwFrqDays, aFinanceType.getFinGrcRvwFrq(), isReadOnly("FinanceTypeDialog_finGrcRvwFrq"));
		fillComboBox(this.cbfinGrcRvwRateApplFor, aFinanceType.getFinGrcRvwRateApplFor(), RvwRateAppPeriods, "");
		this.finGrcAlwIndRate.setChecked(aFinanceType.isFinGrcAlwIndRate());
		this.finGrcIndBaseRate.setValue(aFinanceType.getFinGrcIndBaseRate());
		this.finIsIntCpzAtGrcEnd.setChecked(aFinanceType.isFinIsIntCpzAtGrcEnd());

		doCheckGraceReview();
		doCheckGrcPftCpzFrq();
		doDisableGrcSchdMtd();
		doCheckRateType(cbfinGrcRateType, true,false);

		if (aFinanceType.getLovDescFinGrcIndBaseRateName() != null) {
			this.lovDescFinGrcIndBaseRateName.setValue(aFinanceType.getFinGrcIndBaseRate() + "-" + aFinanceType.getLovDescFinGrcIndBaseRateName());
		}
		//================= Tab 3
		fillComboBox(this.cbfinRateType, aFinanceType.getFinRateType(), schPftRateType, "");
		this.finIntRate.setValue(aFinanceType.getFinIntRate());
		this.finBaseRate.setValue(aFinanceType.getFinBaseRate());
		if (aFinanceType.getLovDescFinGrcBaseRateName() != null) {
			this.finGrcBaseRate.setDescription(aFinanceType.getLovDescFinGrcBaseRateName());
		}
		this.finSplRate.setValue(aFinanceType.getFinSplRate());
		if (aFinanceType.getLovDescFinGrcSplRateName() != null) {
			this.finGrcSplRate.setDescription(aFinanceType.getLovDescFinGrcSplRateName());
		}
		this.finMargin.setValue(aFinanceType.getFinMargin());
		this.finFrqrepayment.setChecked(aFinanceType.isFinFrEqrepayment());
		this.finDftIntFrq.setValue(aFinanceType.getFinDftIntFrq());
		fillFrqCode(this.cbfinDftIntFrqCode, aFinanceType.getFinDftIntFrq(), isReadOnly("FinanceTypeDialog_finDftIntFrq"));
		fillFrqMth(this.cbfinDftIntFrqMth, aFinanceType.getFinDftIntFrq(), isReadOnly("FinanceTypeDialog_finDftIntFrq"));
		fillFrqDay(this.cbfinDftIntFrqDays, aFinanceType.getFinDftIntFrq(), isReadOnly("FinanceTypeDialog_finDftIntFrq"));
		this.finRepayPftOnFrq.setChecked(aFinanceType.isFinRepayPftOnFrq());
		this.finRpyFrq.setValue(aFinanceType.getFinRpyFrq());
		fillFrqCode(this.cbfinRpyFrqCode, aFinanceType.getFinRpyFrq(), isReadOnly("FinanceTypeDialog_finRpyFrq"));
		fillFrqMth(this.cbfinRpyFrqMth, aFinanceType.getFinRpyFrq(), isReadOnly("FinanceTypeDialog_finRpyFrq"));
		fillFrqDay(this.cbfinRpyFrqDays, aFinanceType.getFinRpyFrq(), isReadOnly("FinanceTypeDialog_finRpyFrq"));
		fillComboBox(this.cbfinSchdMthd, aFinanceType.getFinSchdMthd(), schMthds, ",NO_PAY,GRCNDPAY,");
		this.finIsIntCpz.setChecked(aFinanceType.isFinIsIntCpz());
		this.finCpzFrq.setValue(aFinanceType.getFinCpzFrq());
		fillFrqCode(this.cbfinCpzFrqCode, aFinanceType.getFinCpzFrq(), isReadOnly("FinanceTypeDialog_finCpzFrq"));
		fillFrqMth(this.cbfinCpzFrqMth, aFinanceType.getFinCpzFrq(), isReadOnly("FinanceTypeDialog_finCpzFrq"));
		fillFrqDay(this.cbfinCpzFrqDays, aFinanceType.getFinCpzFrq(), isReadOnly("FinanceTypeDialog_finCpzFrq"));
		this.finIsRvwAlw.setChecked(aFinanceType.isFinIsRvwAlw());
		this.finRvwFrq.setValue(aFinanceType.getFinRvwFrq());
		fillFrqCode(this.cbfinRvwFrqCode, aFinanceType.getFinRvwFrq(), isReadOnly("FinanceTypeDialog_finRvwFrq"));
		fillFrqMth(this.cbfinRvwFrqMth, aFinanceType.getFinRvwFrq(), isReadOnly("FinanceTypeDialog_finRvwFrq"));
		fillFrqDay(this.cbfinRvwFrqDays, aFinanceType.getFinRvwFrq(), isReadOnly("FinanceTypeDialog_finRvwFrq"));
		fillComboBox(this.cbfinRvwRateApplFor, aFinanceType.getFinRvwRateApplFor(), RvwRateAppPeriods, "");
		fillComboBox(this.cbfinSchCalCodeOnRvw, aFinanceType.getFinSchCalCodeOnRvw(), scCalCode, ",TILLDATE,ADDTERM,ADDLAST,ADJTERMS,");
		this.finAlwIndRate.setChecked(aFinanceType.isFinAlwIndRate());
		if (aFinanceType.getLovDescFinIndBaseRateName() != null) {
			this.finIndBaseRate.setDescription(aFinanceType.getLovDescFinIndBaseRateName());
		}
		this.finIndBaseRate.setValue(aFinanceType.getFinIndBaseRate());
		this.finMinTerm.setValue(aFinanceType.getFinMinTerm());
		this.finMaxTerm.setValue(aFinanceType.getFinMaxTerm());
		this.finDftTerms.setValue(aFinanceType.getFinDftTerms());
		fillComboBox(this.cbfinRepayMethod, aFinanceType.getFInRepayMethod(), rpyMthd, "");
		this.finIsAlwPartialRpy.setChecked(aFinanceType.isFinIsAlwPartialRpy());
		this.finODRpyTries.setValue(aFinanceType.getFinODRpyTries());
		this.finIsAlwDifferment.setChecked(aFinanceType.isFinIsAlwDifferment());
		this.finMaxDifferment.setValue(aFinanceType.getFinMaxDifferment());
		doDisableOrEnableDifferments(aFinanceType.isFinIsAlwDifferment(), this.finMaxDifferment, isReadOnly("FinanceTypeDialog_finMaxDifferment"));
		this.finIsAlwFrqDifferment.setChecked(aFinanceType.isFinIsAlwFrqDifferment());
		this.finMaxFrqDifferment.setValue(aFinanceType.getFinMaxFrqDifferment());
		doDisableOrEnableDifferments(aFinanceType.isFinIsAlwFrqDifferment(), this.finMaxFrqDifferment, isReadOnly("FinanceTypeDialog_finMaxFrqDifferment"));
		fillComboBox(this.cbFinScheduleOn, aFinanceType.getFinScheduleOn(), scheduleOn, "");
		this.finPftUnChanged.setChecked(aFinanceType.isFinPftUnChanged());
		doCheckPftCpzFrq();
		doCheckRpyDefferment();
		doCheckFrqDefferment();
		doCheckRateType(cbfinRateType, false,false);
		this.finAlwIndRate.setChecked(aFinanceType.isFinAlwIndRate());
		doCheckRepayIndRate();

		//Overdue Penalty Details
		this.applyODPenalty.setChecked(aFinanceType.isApplyODPenalty());
		this.oDIncGrcDays.setChecked(aFinanceType.isODIncGrcDays());
		fillComboBox(this.oDChargeCalOn, aFinanceType.getODChargeCalOn(), PennantStaticListUtil.getODCCalculatedOn(), "");
		this.oDGraceDays.setValue(aFinanceType.getODGraceDays());
		fillComboBox(this.oDChargeType, aFinanceType.getODChargeType(), PennantStaticListUtil.getODCChargeType(), "");
		if(getComboboxValue(this.oDChargeType).equals(PennantConstants.FLAT)){
			this.oDChargeAmtOrPerc.setValue(PennantAppUtil.formateAmount(aFinanceType.getODChargeAmtOrPerc(), 
					aFinanceType.getLovDescFinFormetter()));
		}else if(PennantConstants.PERCONETIME.equals(getComboboxValue(this.oDChargeType)) || 
				PennantConstants.PERCONDUEDAYS.equals(getComboboxValue(this.oDChargeType))){
			this.oDChargeAmtOrPerc.setValue(PennantAppUtil.formateAmount(aFinanceType.getODChargeAmtOrPerc(), 2));
		}
		this.oDAllowWaiver.setChecked(aFinanceType.isODAllowWaiver());
		this.oDMaxWaiverPerc.setValue(aFinanceType.getODMaxWaiverPerc());
		
		//================= Tab 4
		this.finAEAddDsbOD.setValue(aFinanceType.getFinAEAddDsbOD());
		if (aFinanceType.getLovDescFinAEAddDsbODName() != null) {
			this.finAEAddDsbOD.setDescription(aFinanceType.getLovDescEVFinAEAddDsbODName() + "-" + aFinanceType.getLovDescFinAEAddDsbODName());
		} else {
			if (aFinanceType.getLovDescAERule().containsKey("ADDDBSP")) {
				accSet = aFinanceType.getLovDescAERule().get("ADDDBSP");
				this.finAEAddDsbOD.setValue(accSet.getStringaERuleId());
				this.finAEAddDsbOD.setDescription(accSet.getAccountSetCode() + "-" +accSet.getAccountSetCodeName());
			}else{
				this.finAEAddDsbOD.setDescription("");
			}
		}
		this.finAEAddDsbFD.setValue(aFinanceType.getFinAEAddDsbFD());
		if (aFinanceType.getLovDescFinAEAddDsbFDName() != null) {
			this.finAEAddDsbFD.setDescription(aFinanceType.getLovDescEVFinAEAddDsbFDName() + "-" + aFinanceType.getLovDescFinAEAddDsbFDName());
		} else {
			if (aFinanceType.getLovDescAERule().containsKey("ADDDBSF")) {
				accSet = aFinanceType.getLovDescAERule().get("ADDDBSF");
				this.finAEAddDsbFD.setValue(accSet.getStringaERuleId());
				this.finAEAddDsbFD.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finAEAddDsbFD.setDescription("");
			}
		}
		this.finAEAddDsbFDA.setValue(aFinanceType.getFinAEAddDsbFDA());
		if (aFinanceType.getLovDescFinAEAddDsbFDAName() != null) {
			this.finAEAddDsbFDA.setDescription(aFinanceType.getLovDescEVFinAEAddDsbFDAName() + "-" + aFinanceType.getLovDescFinAEAddDsbFDAName());
		} else {
			if (aFinanceType.getLovDescAERule().containsKey("ADDDBSN")) {
				accSet = aFinanceType.getLovDescAERule().get("ADDDBSN");
				this.finAEAddDsbFDA.setValue(accSet.getStringaERuleId());
				this.finAEAddDsbFDA.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finAEAddDsbFDA.setDescription("");
			}
		}
		this.finAEAmzNorm.setValue(aFinanceType.getFinAEAmzNorm());
		if (aFinanceType.getLovDescFinAEAmzNormName() != null) {
			this.finAEAmzNorm.setDescription(aFinanceType.getLovDescEVFinAEAmzNormName() + "-" + aFinanceType.getLovDescFinAEAmzNormName());
		} else {
			if (aFinanceType.getLovDescAERule().containsKey("AMZ")) {
				accSet = aFinanceType.getLovDescAERule().get("AMZ");
				this.finAEAmzNorm.setValue(accSet.getStringaERuleId());
				this.finAEAmzNorm.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finAEAmzNorm.setDescription("");
			}
		}
		this.finAEAmzSusp.setValue(aFinanceType.getFinAEAmzSusp());
		if (aFinanceType.getLovDescFinAEAmzSuspName() != null) {
			this.finAEAmzSusp.setDescription(aFinanceType.getLovDescEVFinAEAmzSuspName() + "-" + aFinanceType.getLovDescFinAEAmzSuspName());
		} else {
			if (aFinanceType.getLovDescAERule().containsKey("AMZSUSP")) {
				accSet = aFinanceType.getLovDescAERule().get("AMZSUSP");
				this.finAEAmzSusp.setValue(accSet.getStringaERuleId());
				this.finAEAmzSusp.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finAEAmzSusp.setDescription("");
			}
		}
		this.finAEToNoAmz.setValue(aFinanceType.getFinAEToNoAmz());
		if (aFinanceType.getLovDescFinAEToNoAmzName() != null) {
			this.finAEToNoAmz.setDescription(aFinanceType.getLovDescEVFinAEToNoAmzName() + "-" + aFinanceType.getLovDescFinAEToNoAmzName());
		} else {
			if (aFinanceType.getLovDescAERule().containsKey("M_NONAMZ")) {
				accSet = aFinanceType.getLovDescAERule().get("M_NONAMZ");
				this.finAEToNoAmz.setValue(accSet.getStringaERuleId());
				this.finAEToNoAmz.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finAEToNoAmz.setDescription("");
			}
		}

		this.finToAmz.setValue(aFinanceType.getFinToAmz());
		if (aFinanceType.getLovDescFinToAmzName() != null) {
			this.finToAmz.setDescription(aFinanceType.getLovDescEVFinToAmzName() + "-" + aFinanceType.getLovDescFinToAmzName());
		} else {
			if (aFinanceType.getLovDescAERule().containsKey("M_AMZ")) {
				accSet = aFinanceType.getLovDescAERule().get("M_AMZ");
				this.finToAmz.setValue(accSet.getStringaERuleId());
				this.finToAmz.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finToAmz.setDescription("");
			}
		}
		
		this.finMAmz.setValue(aFinanceType.getFinAEMAmz());
		if (aFinanceType.getLovDescFinMAmzName() != null) {
			this.finMAmz.setDescription(aFinanceType.getLovDescEVFinMAmzName() + "-" + aFinanceType.getLovDescFinMAmzName());
		} else {
			if (aFinanceType.getLovDescAERule().containsKey("AMZ_MON")) {
				accSet = aFinanceType.getLovDescAERule().get("AMZ_MON");
				this.finMAmz.setValue(accSet.getStringaERuleId());
				this.finMAmz.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finMAmz.setDescription("");
			}
		}
		
		this.finAERateChg.setValue(aFinanceType.getFinAERateChg());
		if (aFinanceType.getLovDescFinAERateChgName() != null) {
			this.finAERateChg.setDescription(aFinanceType.getLovDescEVFinAERateChgName() + "-" + aFinanceType.getLovDescFinAERateChgName());
		} else {
			if (aFinanceType.getLovDescAERule().containsKey("RATCHG")) {
				accSet = aFinanceType.getLovDescAERule().get("RATCHG");
				this.finAERateChg.setValue(accSet.getStringaERuleId());
				this.finAERateChg.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finAERateChg.setDescription("");
			}
		}
		this.finAERepay.setValue(aFinanceType.getFinAERepay());
		if (aFinanceType.getLovDescFinAERepayName() != null) {
			this.finAERepay.setDescription(aFinanceType.getLovDescEVFinAERepayName() + "-" + aFinanceType.getLovDescFinAERepayName());
		} else {
			if (aFinanceType.getLovDescAERule().containsKey("REPAY")) {
				accSet = aFinanceType.getLovDescAERule().get("REPAY");
				this.finAERepay.setValue(accSet.getStringaERuleId());
				this.finAERepay.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finAERepay.setDescription("");
			}
		}

		this.finLatePayRule.setValue(aFinanceType.getFinLatePayRule());
		if (aFinanceType.getLovDescFinLatePayRuleName() != null) {
			this.finLatePayRule.setDescription(aFinanceType.getLovDescFinLatePayRuleName() + "-" + aFinanceType.getLovDescEVFinLatePayRuleName());
		} else {
			if (aFinanceType.getLovDescAERule().containsKey("LATEPAY")) {
				accSet = aFinanceType.getLovDescAERule().get("LATEPAY");
				this.finLatePayRule.setValue(accSet.getStringaERuleId());
				this.finLatePayRule.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finLatePayRule.setDescription("");
			}
		}
		this.finInstDate.setValue(aFinanceType.getFinInstDate());
		if (aFinanceType.getLovDescEVFinInstDateName() != null) {
			this.finInstDate.setDescription(aFinanceType.getLovDescFinInstDateName() + "-" + aFinanceType.getLovDescEVFinInstDateName());
		} else {
			if (getFinanceType().getLovDescAERule().containsKey("INSTDATE")) {
				accSet = getFinanceType().getLovDescAERule().get("INSTDATE");
				this.finInstDate.setValue(accSet.getStringaERuleId());
				this.finInstDate.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finInstDate.setDescription("");
			}
		}
		this.finAEWriteOff.setValue(aFinanceType.getFinAEWriteOff());
		if (aFinanceType.getLovDescFinAEWriteOffName() != null) {
			this.finAEWriteOff.setDescription(aFinanceType.getLovDescEVFinAEWriteOffName() + "-" + aFinanceType.getLovDescFinAEWriteOffName());
		} else {
			if (aFinanceType.getLovDescAERule().containsKey("WRITEOFF")) {
				accSet = aFinanceType.getLovDescAERule().get("WRITEOFF");
				this.finAEWriteOff.setValue(accSet.getStringaERuleId());
				this.finAEWriteOff.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finAEWriteOff.setDescription("");
			}
		}
		this.finAEWriteOffBK.setValue(aFinanceType.getFinAEWriteOffBK());
		if (aFinanceType.getLovDescFinAEWriteOffBKName() != null) {
			this.finAEWriteOffBK.setDescription(aFinanceType.getLovDescEVFinAEWriteOffBKName() + "-" + aFinanceType.getLovDescFinAEWriteOffBKName());
		} else {
			if (aFinanceType.getLovDescAERule().containsKey("WRITEBK")) {
				accSet = aFinanceType.getLovDescAERule().get("WRITEBK");
				this.finAEWriteOffBK.setValue(accSet.getStringaERuleId());
				this.finAEWriteOffBK.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finAEWriteOffBK.setDescription("");
			}
		}
		this.finAEGraceEnd.setValue(aFinanceType.getFinAEGraceEnd());
		if (aFinanceType.getLovDescFinAEGraceEndName() != null) {
			this.finAEGraceEnd.setDescription(aFinanceType.getLovDescEVFinAEGraceEndName() + "-" + aFinanceType.getLovDescFinAEGraceEndName());
		} else {
			if (aFinanceType.getLovDescAERule().containsKey("GRACEEND")) {
				accSet = aFinanceType.getLovDescAERule().get("GRACEEND");
				this.finAEGraceEnd.setValue(accSet.getStringaERuleId());
				this.finAEGraceEnd.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finAEGraceEnd.setDescription("");
			}
		}
		this.finProvision.setValue(aFinanceType.getFinProvision());
		if (aFinanceType.getLovDescEVFinProvisionName() != null) {
			this.finProvision.setDescription(aFinanceType.getLovDescFinProvisionName() + "-" + aFinanceType.getLovDescEVFinProvisionName());
		} else {
			if (getFinanceType().getLovDescAERule().containsKey("PROVSN")) {
				accSet = getFinanceType().getLovDescAERule().get("PROVSN");
				this.finProvision.setValue(accSet.getStringaERuleId());
				this.finProvision.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finProvision.setDescription("");
			}
		}
		this.finSchdChange.setValue(aFinanceType.getFinSchdChange());
		if (aFinanceType.getLovDescEVFinSchdChangeName() != null) {
			this.finSchdChange.setDescription(aFinanceType.getLovDescFinSchdChangeName() + "-" + aFinanceType.getLovDescEVFinSchdChangeName());
		} else {
			if (getFinanceType().getLovDescAERule().containsKey("SCDCHG")) {
				accSet = getFinanceType().getLovDescAERule().get("SCDCHG");
				this.finSchdChange.setValue(accSet.getStringaERuleId());
				this.finSchdChange.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finSchdChange.setDescription("");
			}
		}
		this.finDepreciationRule.setValue(aFinanceType.getFinDepreciationRule());
		if (aFinanceType.getLovDescEVFinDepreciationRuleName() != null) {
			this.finDepreciationRule.setDescription(aFinanceType.getLovDescFinDepreciationRuleName() + "-" + aFinanceType.getLovDescEVFinDepreciationRuleName());
		} else {
			if (getFinanceType().getLovDescAERule().containsKey("DPRCIATE")) {
				accSet = getFinanceType().getLovDescAERule().get("DPRCIATE");
				this.finDepreciationRule.setValue(accSet.getStringaERuleId());
				this.finDepreciationRule.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finDepreciationRule.setDescription("");
			}
		}
		this.finDeffreq.setValue(aFinanceType.getFinDeffreq());
		if (aFinanceType.getLovDescFinDeffreqName() != null) {
			this.finDeffreq.setDescription(aFinanceType.getLovDescFinDeffreqName() + "-" + aFinanceType.getLovDescEVFinDeffreqName());
		} else {
			if (aFinanceType.getLovDescAERule().containsKey("DEFFRQ")) {
				accSet = aFinanceType.getLovDescAERule().get("DEFFRQ");
				this.finDeffreq.setValue(accSet.getStringaERuleId());
				this.finDeffreq.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finDeffreq.setDescription("");
			}
		}
		this.finDefRepay.setValue(aFinanceType.getFinDefRepay());
		if (aFinanceType.getLovDescFinDefRepayName() != null) {
			this.finDefRepay.setDescription(aFinanceType.getLovDescFinDefRepayName() + "-" + aFinanceType.getLovDescEVFinDefRepayName());
		} else {
			if (aFinanceType.getLovDescAERule().containsKey("DEFRPY")) {
				accSet = aFinanceType.getLovDescAERule().get("DEFRPY");
				this.finDefRepay.setValue(accSet.getStringaERuleId());
				this.finDefRepay.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finDefRepay.setDescription("");
			}
		}
		this.finAECapitalize.setValue(aFinanceType.getFinAECapitalize());
		if (aFinanceType.getLovDescEVFinAECapitalizeName() != null) {
			this.finAECapitalize.setDescription(aFinanceType.getLovDescFinAECapitalizeName() + "-" + aFinanceType.getLovDescEVFinAECapitalizeName());
		} else {
			if (getFinanceType().getLovDescAERule().containsKey("COMPOUND")) {
				accSet = getFinanceType().getLovDescAERule().get("COMPOUND");
				this.finAECapitalize.setValue(accSet.getStringaERuleId());
				this.finAECapitalize.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finAECapitalize.setDescription("");
			}
		}
		this.finAEProgClaim.setValue(aFinanceType.getFinAEProgClaim());
		if (aFinanceType.getLovDescEVFinAEProgClaimName() != null) {
			this.finAEProgClaim.setDescription(aFinanceType.getLovDescFinAEProgClaimName() + "-" + aFinanceType.getLovDescEVFinAEProgClaimName());
		} else {
			if (getFinanceType().getLovDescAERule().containsKey("PRGCLAIM")) {
				accSet = getFinanceType().getLovDescAERule().get("PRGCLAIM");
				this.finAEProgClaim.setValue(accSet.getStringaERuleId());
				this.finAEProgClaim.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finAEProgClaim.setDescription("");
			}
		}
		
		this.finAEMaturity.setValue(aFinanceType.getFinAEMaturity());
		if (aFinanceType.getLovDescEVFinAEMaturityName() != null) {
			this.finAEMaturity.setDescription(aFinanceType.getLovDescFinAEMaturityName() + "-" + aFinanceType.getLovDescEVFinAEMaturityName());
		} else {
			if (getFinanceType().getLovDescAERule().containsKey("MATURITY")) {
				accSet = getFinanceType().getLovDescAERule().get("MATURITY");
				this.finAEMaturity.setValue(accSet.getStringaERuleId());
				this.finAEMaturity.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finAEMaturity.setDescription("");
			}
		}
		
		// Stepping Details
		this.stepFinance.setChecked(aFinanceType.isStepFinance());
		this.steppingMandatory.setChecked(aFinanceType.isSteppingMandatory());
		this.allowManualSteps.setChecked(aFinanceType.isAlwManualSteps());
		fillComboBox(this.dftStepPolicy, StringUtils.trimToEmpty(aFinanceType.getDftStepPolicy()), this.stepPolicies, "");
		this.lovDescStepPolicyCodename.setValue(aFinanceType.getAlwdStepPolicies());
		
		//======================  Hidden Fields
		this.finDftStmtFrq.setValue(aFinanceType.getFinDftStmtFrq());
		fillFrqCode(this.cbfinDftStmtFrqCode, aFinanceType.getFinDftStmtFrq(), isReadOnly("FinanceTypeDialog_finDftStmtFrq"));
		fillFrqMth(this.cbfinDftStmtFrqMth, aFinanceType.getFinDftStmtFrq(), isReadOnly("FinanceTypeDialog_finDftStmtFrq"));
		fillFrqDay(this.cbfinDftStmtFrqDays, aFinanceType.getFinDftStmtFrq(), isReadOnly("FinanceTypeDialog_finDftStmtFrq"));
		this.finAlwRateChangeAnyDate.setChecked(aFinanceType.isFinAlwRateChangeAnyDate());
		this.finGrcAlwRateChgAnyDate.setChecked(aFinanceType.isFinGrcAlwRateChgAnyDate());
		this.fInMinRate.setValue(aFinanceType.getFInMinRate());
		this.finMaxRate.setValue(aFinanceType.getFinMaxRate());
		fillComboBox(this.cbFinGrcScheduleOn, aFinanceType.getFinGrcScheduleOn(), scheduleOn, "");
	
		this.fInGrcMinRate.setValue(aFinanceType.getFInGrcMinRate());
		this.finGrcMaxRate.setValue(aFinanceType.getFinGrcMaxRate());
		this.finCollateralReq.setChecked(aFinanceType.isFinCollateralReq());
		this.finCollateralOvrride.setChecked(aFinanceType.isFinCollateralOvrride());
		doCheckBoxChecked(this.finCollateralReq.isChecked(), this.finCollateralOvrride);
		this.finIsAlwEarlyRpy.setChecked(aFinanceType.isFinIsAlwEarlyRpy());
		this.finIsAlwEarlySettle.setChecked(aFinanceType.isFinIsAlwEarlySettle());
		this.finDepreciationFrq.setValue(aFinanceType.getFinDepreciationFrq());
		fillFrqCode(this.cbfinDepreciationCode, aFinanceType.getFinDepreciationFrq(), isReadOnly("FinanceTypeDialog_finGrcRvwFrq"));
		fillFrqMth(this.cbfinDepreciationMth, aFinanceType.getFinDepreciationFrq(), isReadOnly("FinanceTypeDialog_finGrcRvwFrq"));
		fillFrqDay(this.cbfinDepreciationDays, aFinanceType.getFinDepreciationFrq(), isReadOnly("FinanceTypeDialog_finGrcRvwFrq"));
		this.finHistRetension.setValue(aFinanceType.getFinHistRetension());
		this.finAEEarlyPay.setValue(aFinanceType.getFinAEEarlyPay());
		this.finAEEarlySettle.setValue(aFinanceType.getFinAEEarlySettle());
		if (aFinanceType.getLovDescFinAEEarlyPayName() != null) {
			this.lovDescFinAEEarlyPayName.setValue(aFinanceType.getLovDescEVFinAEEarlyPayName() + "-" + aFinanceType.getLovDescFinAEEarlyPayName());
		}
		if (aFinanceType.getLovDescFinAEEarlySettleName() != null) {
			this.finAEEarlySettle.setDescription(aFinanceType.getLovDescEVFinAEEarlySettleName() + "-" + aFinanceType.getLovDescFinAEEarlySettleName());
		} else {
			if (aFinanceType.getLovDescAERule().containsKey("EARLYSTL")) {
				accSet = aFinanceType.getLovDescAERule().get("EARLYSTL");
				this.finAEEarlySettle.setValue(accSet.getStringaERuleId());
				this.finAEEarlySettle.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			}else{
				this.finAEEarlySettle.setDescription("");
			}
		}
		doCheckMandFinAEAddDisbFDA();
		
		if (aFinanceType.isNewRecord()) {
			// Select manual repay by default.
			this.cbfinRepayMethod.setSelectedIndex(2);
			this.finIsActive.setChecked(true);
			this.finIsOpenNewFinAc.setChecked(true);
			this.finIsAlwPartialRpy.setChecked(true);
			if (isCopyProcess) {
				setRateLabels(aFinanceType);
			}
		} else {
			setRateLabels(aFinanceType);
		}
		
		//======== Tab5
		
		doFillCustAccountTypes(aFinanceType.getFinTypeAccounts());
		
		this.recordStatus.setValue(aFinanceType.getRecordStatus());

		logger.debug("Leaving doWriteBeanToComponents()");
	}

	private void setRateLabels(FinanceType aFinanceType) {
		// To Set Default Values in new mode
		this.finSplRate.setValue(aFinanceType.getFinSplRate());
		if (aFinanceType.getFinBaseRate() != null || aFinanceType.getFinSplRate() != null) {
			this.labe_EffectiveRate.setValue(String.valueOf(rates(aFinanceType.getFinBaseRate(), aFinanceType.getFinSplRate(), aFinanceType.getFinMargin())));

		}
		this.finGrcSplRate.setValue(aFinanceType.getFinGrcSplRate());
		if (aFinanceType.getFinGrcBaseRate() != null || aFinanceType.getFinGrcSplRate() != null) {
			this.labe_GrcEffectiveRate.setValue(String.valueOf(rates(aFinanceType.getFinGrcBaseRate(), aFinanceType.getFinGrcSplRate(), aFinanceType.getFinGrcMargin())));
		}
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceType
	 */

	public void doWriteComponentsToBean(FinanceType aFinanceType) {
		logger.debug("Entering doWriteComponentsToBean()");

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// +++++++++++++ Start of  tab 1 ++++++++++++//
		try {
			aFinanceType.setFinType(this.finType.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinTypeDesc(this.finTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinCcyName(this.finCcy.getDescription());
			aFinanceType.setFinCcy(this.finCcy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (validate && getComboboxValue(this.cbfinDaysCalType).equals("#")) {
				throw new WrongValueException(this.cbfinDaysCalType, Labels.getLabel("STATIC_INVALID",
				        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinDaysCalType.value") }));
			}
			aFinanceType.setFinDaysCalType(getComboboxValue(this.cbfinDaysCalType));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setLovDescFinAcTypeName(this.finAcType.getDescription());
			aFinanceType.setFinAcType(this.finAcType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsOpenNewFinAc(this.finIsOpenNewFinAc.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescPftPayAcTypeName(this.pftPayAcType.getDescription());
			aFinanceType.setPftPayAcType(this.pftPayAcType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setLovDescFinSuspAcTypeName(this.finSuspAcType.getDescription());
			aFinanceType.setFinSuspAcType(this.finSuspAcType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinProvisionAcTypeName(this.finProvisionAcType.getDescription());
			aFinanceType.setFinProvisionAcType(this.finProvisionAcType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinDivisionName(this.finDivision.getDescription());
			aFinanceType.setFinDivision(this.finDivision.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinMaxAmount(PennantAppUtil.unFormateAmount(this.finMaxAmount.getValue(), getFinanceType().getLovDescFinFormetter()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinMinAmount(PennantAppUtil.unFormateAmount(this.finMinAmount.getValue(), getFinanceType().getLovDescFinFormetter()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.cbfinProductType.getSelectedItem() != null || !this.cbfinProductType.getSelectedItem().getValue().equals("#")) {
				aFinanceType.setFinCategory(this.cbfinProductType.getSelectedItem().getValue().toString());
			} else {
				throw new WrongValueException(this.cbfinProductType, Labels.getLabel("STATIC_INVALID",
				        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinProductType.Value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.cbfinAssetType.getSelectedItem() != null && !this.cbfinAssetType.getSelectedItem().getValue().equals("#")) {
				aFinanceType.setFinAssetType(Long.parseLong(this.cbfinAssetType.getSelectedItem().getValue().toString()));
			} else {
				throw new WrongValueException(this.cbfinAssetType,
				        Labels.getLabel("STATIC_INVALID", new String[] { Labels.getLabel("label_FinanceTypeDialog_FinAssetType.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsDwPayRequired(this.finIsDwPayRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinMinDownPayAmount(this.finMinDownPayAmount.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsGenRef(this.finIsGenRef.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setFInIsAlwGrace(this.fInIsAlwGrace.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsAlwMD(this.finIsAlwMD.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinDepreciationReq(this.finDepreciationReq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinCommitmentReq(this.finCommitmentReq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinCommitmentOvrride(this.finCommitmentOvrride.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLimitRequired(this.limitRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setOverrideLimit(this.overrideLimit.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setAllowRIAInvestment(this.allowRIAInvestment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setAllowParllelFinance(this.allowParllelFinance.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setFinIsActive(this.finIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// To check finMaxAmount has higher value than the finMinAmount
		try {
			mustBeHigher(finMaxAmount, finMinAmount, "label_FinanceTypeDialog_FinMaxAmount.value", "label_FinanceTypeDialog_FinMinAmount.value");
		} catch (WrongValueException we) {
			wve.add(we);
		}

		showErrorDetails(wve, basicDetails);

		// +++++++++++ End of tab 1 +++++++++++++++++//

		// ++++++++++++++++ Start of  tab 2 +++++++++++++++++//
		if (!this.gracePeriod.isDisabled()) {
			try {
				// Field is foreign key so it should be non empty
				if (validate && getComboboxValue(this.cbfinGrcRateType).equals("#")) {
					throw new WrongValueException(this.cbfinGrcRateType, Labels.getLabel("STATIC_INVALID",
					        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinGrcRateType.value") }));
				}
				aFinanceType.setFinGrcRateType(getComboboxValue(this.cbfinGrcRateType));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				/*
				 * to check mutually exclusive values i.e Grace base rate code and Grace profit rate
				 */
				if (this.finGrcIntRate.getValue() != null) {
					if ((this.finGrcIntRate.getValue().intValue() > 0) && (!this.finGrcBaseRate.getDescription().equals(""))) {
						throw new WrongValueException(this.finGrcIntRate, Labels.getLabel("EITHER_OR",
						        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinGrcBaseRate.value"), Labels.getLabel("label_FinanceTypeDialog_FinGrcIntRate.value") }));
					}
					aFinanceType.setFinGrcIntRate(this.finGrcIntRate.getValue());
				} else {
					aFinanceType.setFinGrcIntRate(BigDecimal.ZERO);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				// Field is foreign key and not a mandatory value so it should be either null or non empty
				aFinanceType.setLovDescFinGrcBaseRateName(this.finGrcBaseRate.getDescription());
				aFinanceType.setFinGrcBaseRate(this.finGrcBaseRate.getValue().equals("") ? null : this.finGrcBaseRate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				//Field is foreign key and not a mandatory value so it should be either null or non empty
				aFinanceType.setLovDescFinGrcSplRateName(this.finGrcSplRate.getDescription());
				aFinanceType.setFinGrcSplRate(this.finGrcSplRate.getValue().equals("") ? null : this.finGrcSplRate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setFinGrcMargin(this.finGrcMargin.getValue() == null ? BigDecimal.ZERO : this.finGrcMargin.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				// to Check frequency code and frequency month
				if (!getComboboxValue(this.cbfinGrcDftIntFrqCode).equals("#") && getComboboxValue(this.cbfinGrcDftIntFrqMth).equals("#")) {
					throw new WrongValueException(this.cbfinGrcDftIntFrqMth, Labels.getLabel("FIELD_NO_EMPTY",
					        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinGrcDftIntFrqMth.value") }));
				}
				aFinanceType.setFinGrcDftIntFrq(this.finGrcDftIntFrq.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				// to Check frequency month and frequency day
				if (!getComboboxValue(this.cbfinGrcDftIntFrqMth).equals("#") && getComboboxValue(this.cbfinGrcDftIntFrqDays).equals("#") && !this.cbfinGrcDftIntFrqDays.isDisabled()) {
					throw new WrongValueException(this.cbfinGrcDftIntFrqDays, Labels.getLabel("FIELD_NO_EMPTY",
					        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinGrcDftIntFrqDay.value") }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setFinIsAlwGrcRepay(this.finIsAlwGrcRepay.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.finIsAlwGrcRepay.isChecked() && getComboboxValue(this.finGrcSchdMthd).equals("#")) {
					throw new WrongValueException(this.finGrcSchdMthd, Labels.getLabel("STATIC_INVALID",
					        new String[] { Labels.getLabel("label_FinanceTypeDialog_FFinGrcSchdMthd.value") }));
				}
				aFinanceType.setFinGrcSchdMthd(getComboboxValue(this.finGrcSchdMthd));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setFinGrcIsIntCpz(this.finGrcIsIntCpz.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				// to Check frequency code and frequency month
				if (!getComboboxValue(this.cbfinGrcCpzFrqCode).equals("#") && getComboboxValue(this.cbfinGrcCpzFrqMth).equals("#")) {
					throw new WrongValueException(this.cbfinGrcCpzFrqMth, Labels.getLabel("FIELD_NO_EMPTY",
					        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinGrcCpzFrqMth.value") }));
				}
				aFinanceType.setFinGrcCpzFrq(this.finGrcCpzFrq.getValue() == null ? "" : this.finGrcCpzFrq.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				// to Check frequency month and frequency day
				if (!getComboboxValue(this.cbfinGrcCpzFrqMth).equals("#") && getComboboxValue(this.cbfinGrcCpzFrqDays).equals("#") && !this.cbfinGrcCpzFrqDays.isDisabled()) {
					throw new WrongValueException(this.cbfinGrcCpzFrqDays, Labels.getLabel("FIELD_NO_EMPTY",
					        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinGrcCpzFrqDay.value") }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setFinGrcIsRvwAlw(this.finGrcIsRvwAlw.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				// to Check frequency code and frequency month
				if (!getComboboxValue(this.cbfinGrcRvwFrqCode).equals("#") && getComboboxValue(this.cbfinGrcRvwFrqMth).equals("#")) {
					throw new WrongValueException(this.cbfinGrcRvwFrqMth, Labels.getLabel("FIELD_NO_EMPTY",
					        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinGrcRvwFrqMth.value") }));
				}
				aFinanceType.setFinGrcRvwFrq(this.finGrcRvwFrq.getValue() == null ? "" : this.finGrcRvwFrq.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				// to Check frequency month and frequency day
				if (!getComboboxValue(this.cbfinGrcRvwFrqMth).equals("#") && getComboboxValue(this.cbfinGrcRvwFrqDays).equals("#") && !this.cbfinGrcRvwFrqDays.isDisabled()) {
					throw new WrongValueException(this.cbfinGrcRvwFrqDays, Labels.getLabel("FIELD_NO_EMPTY",
					        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinGrcRvwFrqDay.value") }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				this.cbfinGrcRvwRateApplFor.clearErrorMessage();
				if (validate && this.finGrcIsRvwAlw.isChecked() && getComboboxValue(this.cbfinGrcRvwRateApplFor).equals("#")) {
					throw new WrongValueException(this.cbfinGrcRvwRateApplFor, Labels.getLabel("STATIC_INVALID",
					        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinGrcRvwRateApplFor.value") }));
				}
				aFinanceType.setFinGrcRvwRateApplFor(getComboboxValue(this.cbfinGrcRvwRateApplFor));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setFinGrcAlwIndRate(this.finGrcAlwIndRate.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				//Field is foreign key and not a mandatory value so it should be either null or non empty
				aFinanceType.setLovDescFinGrcIndBaseRateName(this.lovDescFinGrcIndBaseRateName.getValue());
				aFinanceType.setFinGrcIndBaseRate(this.finGrcIndBaseRate.getValue().equals("") ? null : this.finGrcIndBaseRate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aFinanceType.setFinIsIntCpzAtGrcEnd(this.finIsIntCpzAtGrcEnd.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (!this.gracePeriod.isDisabled()) {
					mustBeHigher(finGrcMaxRate, fInGrcMinRate, "label_FinanceTypeDialog_FinGrcMaxRate.value", "label_FinanceTypeDialog_FInGrcMinRate.value");
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

		}
		showErrorDetails(wve, gracePeriod);

		// ++++++++++++++++++ End of tab 2 ++++++++++++++++++++//

		// +++++++++++ Start tab 3+++++++++++++++++//
		try {
			// Field is foreign key so it should be non empty
			if (validate && getComboboxValue(this.cbfinRateType).equals("#")) {
				throw new WrongValueException(this.cbfinRateType, Labels.getLabel("STATIC_INVALID", new String[] { Labels.getLabel("label_FinanceTypeDialog_FinRateType.value") }));
			} else {
				aFinanceType.setFinRateType(getComboboxValue(this.cbfinRateType));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			// To check mutually exclusive values i.e base rate code and profit rate
			if (this.finIntRate.getValue() != null) {
				if ((this.finIntRate.getValue().intValue() > 0) && (!this.finBaseRate.getDescription().equals(""))) {
					throw new WrongValueException(this.finIntRate, Labels.getLabel("EITHER_OR",
					        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinBaseRate.value"), Labels.getLabel("label_FinanceTypeDialog_FinIntRate.value") }));
				}
				aFinanceType.setFinIntRate(this.finIntRate.getValue());
			} else {
				aFinanceType.setFinIntRate(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			// Field is foreign key and not a mandatory value so it should be either null or non empty
			aFinanceType.setLovDescFinBaseRateName(this.finBaseRate.getDescription());
			aFinanceType.setFinBaseRate(this.finBaseRate.getValue().equals("") ? null : this.finBaseRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// Field is foreign key and not a mandatory value so it should be either null or non empty
			aFinanceType.setLovDescFinSplRateName(this.finSplRate.getDescription());
			aFinanceType.setFinSplRate(this.finSplRate.getValue().equals("") ? null : this.finSplRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinMargin(this.finMargin.getValue() == null ? BigDecimal.ZERO : this.finMargin.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinFrEqrepayment(this.finFrqrepayment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency code and frequency month
			if ((!getComboboxValue(this.cbfinDftIntFrqCode).equals("#")) && (getComboboxValue(this.cbfinDftIntFrqMth).equals("#"))) {
				throw new WrongValueException(this.cbfinDftIntFrqMth, Labels.getLabel("STATIC_INVALID",
				        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinDftIntFrqMth.value") }));
			}
			aFinanceType.setFinDftIntFrq(this.finDftIntFrq.getValue() == null ? "" : this.finDftIntFrq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency month and frequency day
			if ((!getComboboxValue(this.cbfinDftIntFrqMth).equals("#")) && (getComboboxValue(this.cbfinDftIntFrqDays).equals("#")) && !this.cbfinDftIntFrqDays.isDisabled()) {
				throw new WrongValueException(this.cbfinDftIntFrqDays, Labels.getLabel("STATIC_INVALID",
				        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinDftIntFrqDay.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinRepayPftOnFrq(this.finRepayPftOnFrq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency code and frequency month
			if (!getComboboxValue(this.cbfinRpyFrqCode).equals("#") && getComboboxValue(this.cbfinRpyFrqMth).equals("#")) {
				throw new WrongValueException(this.cbfinRpyFrqMth,
				        Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FinanceTypeDialog_FinRpyFrqMth.value") }));
			}
			aFinanceType.setFinRpyFrq(this.finRpyFrq.getValue() == null ? "" : this.finRpyFrq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency month and frequency day
			if (!getComboboxValue(this.cbfinRpyFrqMth).equals("#") && getComboboxValue(this.cbfinRpyFrqDays).equals("#") && !this.cbfinRpyFrqDays.isDisabled()) {
				throw new WrongValueException(this.cbfinRpyFrqDays, Labels.getLabel("FIELD_NO_EMPTY",
				        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinRpyFrqDay.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (validate && getComboboxValue(this.cbfinSchdMthd).equals("#")) {
				throw new WrongValueException(this.cbfinSchdMthd, Labels.getLabel("STATIC_INVALID", new String[] { Labels.getLabel("label_FinanceTypeDialog_FinSchdMthd.value") }));
			}
			aFinanceType.setFinSchdMthd(getComboboxValue(this.cbfinSchdMthd));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsIntCpz(this.finIsIntCpz.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			// to Check frequency code and frequency month
			if ((!getComboboxValue(this.cbfinCpzFrqCode).equals("#")) && (getComboboxValue(this.cbfinCpzFrqMth).equals("#"))) {
				throw new WrongValueException(this.cbfinCpzFrqMth,
				        Labels.getLabel("STATIC_INVALID", new String[] { Labels.getLabel("label_FinanceTypeDialog_FinCpzFrqMth.value") }));
			}
			aFinanceType.setFinCpzFrq(this.finCpzFrq.getValue() == null ? "" : this.finCpzFrq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency month and frequency day
			if ((!getComboboxValue(this.cbfinCpzFrqMth).equals("#")) && (getComboboxValue(this.cbfinCpzFrqDays).equals("#")) && !this.cbfinCpzFrqDays.isDisabled()) {
				throw new WrongValueException(this.cbfinCpzFrqDays, Labels.getLabel("STATIC_INVALID",
				        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinCpzFrqDay.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsRvwAlw(this.finIsRvwAlw.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency code and frequency month
			if (!getComboboxValue(this.cbfinRvwFrqCode).equals("#") && getComboboxValue(this.cbfinRvwFrqMth).equals("#")) {
				throw new WrongValueException(this.cbfinRvwFrqMth,
				        Labels.getLabel("STATIC_INVALID", new String[] { Labels.getLabel("label_FinanceTypeDialog_FinRvwFrqMth.value") }));
			}
			aFinanceType.setFinRvwFrq(this.finRvwFrq.getValue() == null ? "" : this.finRvwFrq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency month and frequency day
			if (!getComboboxValue(this.cbfinRvwFrqMth).equals("#") && getComboboxValue(this.cbfinRvwFrqDays).equals("#") && !this.cbfinRvwFrqDays.isDisabled()) {
				throw new WrongValueException(this.cbfinRvwFrqDays, Labels.getLabel("STATIC_INVALID",
				        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinRvwFrqDay.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.cbfinRvwRateApplFor.clearErrorMessage();
			if (validate && this.finIsRvwAlw.isChecked() && getComboboxValue(this.cbfinRvwRateApplFor).equals("#")) {
				throw new WrongValueException(this.cbfinRvwRateApplFor, Labels.getLabel("STATIC_INVALID",
				        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinRvwRateApplFor.value") }));
			}
			aFinanceType.setFinRvwRateApplFor(getComboboxValue(this.cbfinRvwRateApplFor));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.cbfinSchCalCodeOnRvw.clearErrorMessage();
			if (validate && aFinanceType.isFinIsRvwAlw() && getComboboxValue(this.cbfinSchCalCodeOnRvw).equals("#")) {
				throw new WrongValueException(this.cbfinSchCalCodeOnRvw, Labels.getLabel("STATIC_INVALID",
				        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinSchCalCodeOnRvw.value") }));
			}
			aFinanceType.setFinSchCalCodeOnRvw(getComboboxValue(this.cbfinSchCalCodeOnRvw));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinAlwIndRate(this.finAlwIndRate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			//Field is foreign key and not a mandatory value so it should be either null or non empty
			aFinanceType.setLovDescFinIndBaseRateName(this.finIndBaseRate.getDescription());
			aFinanceType.setFinIndBaseRate(this.finIndBaseRate.getValue().equals("") ? null : this.finIndBaseRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinMinTerm(this.finMinTerm.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinMaxTerm(this.finMaxTerm.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinDftTerms(this.finDftTerms.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (validate && getComboboxValue(this.cbfinRepayMethod).equals("#")) {
				aFinanceType.setFInRepayMethod(getComboboxValue(this.cbfinRepayMethod));
			} else {
				aFinanceType.setFInRepayMethod(getComboboxValue(this.cbfinRepayMethod));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsAlwPartialRpy(this.finIsAlwPartialRpy.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinODRpyTries(this.finODRpyTries.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setFinIsAlwDifferment(this.finIsAlwDifferment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.finIsAlwDifferment.isChecked() && (this.finMaxDifferment.getValue() == null || this.finMaxDifferment.getValue() <= 0)) {
				throw new WrongValueException(this.finMaxDifferment, Labels.getLabel("FIELD_IS_GREATER",
				        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinIsMaxDifferment.value"), "0" }));
			}
			aFinanceType.setFinMaxDifferment(this.finMaxDifferment.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsAlwFrqDifferment(this.finIsAlwFrqDifferment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.finIsAlwFrqDifferment.isChecked() && (this.finMaxFrqDifferment.getValue() == null || this.finMaxFrqDifferment.getValue() <= 0)) {
				throw new WrongValueException(this.finMaxFrqDifferment, Labels.getLabel("FIELD_IS_GREATER",
				        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinIsMaxFrqDifferment.value"), "0" }));
			}
			aFinanceType.setFinMaxFrqDifferment(this.finMaxFrqDifferment.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// To check finMaxTerms has higher value than the finMinTerms
		try {
			if (aFinanceType.getFinMinTerm() != 0 && aFinanceType.getFinMaxTerm() < aFinanceType.getFinMinTerm()) {
				throw new WrongValueException(this.finMaxTerm, Labels.getLabel("FIELD_IS_EQUAL_OR_GREATER", new String[] { Labels.getLabel("label_FinanceTypeSearch_FinMaxTerm.value"),
				        Labels.getLabel("label_FinanceTypeSearch_FinMinTerm.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (validate && getComboboxValue(this.cbFinScheduleOn).equals("#")) {
				throw new WrongValueException(this.cbFinScheduleOn, Labels.getLabel("STATIC_INVALID",
				        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinScheduleOn.value") }));
			}
			aFinanceType.setFinScheduleOn(getComboboxValue(this.cbFinScheduleOn));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			//aFinanceType.setFinPftUnChanged(this.finPftUnChanged.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (this.finDepreciationReq.isChecked() && getComboboxValue(this.cbfinDepreciationCode).equals("#")) {
				throw new WrongValueException(this.cbfinDepreciationCode, Labels.getLabel("STATIC_INVALID",
				        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinDftStmtFrq.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency code and frequency month
			if ((!getComboboxValue(this.cbfinDepreciationCode).equals("#")) && (getComboboxValue(this.cbfinDepreciationMth).equals("#"))) {
				throw new WrongValueException(this.cbfinDepreciationMth, Labels.getLabel("STATIC_INVALID",
				        new String[] { Labels.getLabel("label_FinanceTypeDialog_finDftStmtFrqMth.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency month and frequency day
			if ((!getComboboxValue(this.cbfinDepreciationMth).equals("#")) && (getComboboxValue(this.cbfinDepreciationDays).equals("#") && !this.cbfinDftStmtFrqDays.isDisabled())) {
				throw new WrongValueException(this.cbfinDepreciationDays, Labels.getLabel("STATIC_INVALID",
				        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinDftStmtFrqDay.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinDepreciationFrq(this.finDepreciationFrq.getValue() == null ? "" : this.finDepreciationFrq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//showErrorDetails(wve, scheduleProfit);
		showErrorDetails(wve, repayment);

		// ++++++++++++++++ End of Tab 3 ++++++++++++++++++++//

		// ++++++++++++++++ Start of Tab 4 ++++++++++++//

		try {
			aFinanceType.setLovDescFinAEAddDsbODName(this.finAEAddDsbOD.getDescription());
			aFinanceType.setFinAEAddDsbOD(this.finAEAddDsbOD.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinAEAddDsbFDName(this.finAEAddDsbFD.getDescription());//
			aFinanceType.setFinAEAddDsbFD(this.finAEAddDsbFD.getValue());//
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinAEAddDsbFDAName(this.finAEAddDsbFDA.getDescription());
			aFinanceType.setFinAEAddDsbFDA(this.finAEAddDsbFDA.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setLovDescFinAEAmzNormName(this.finAEAmzNorm.getDescription());
			aFinanceType.setFinAEAmzNorm(this.finAEAmzNorm.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinAEAmzSuspName(this.finAEAmzSusp.getDescription());
			aFinanceType.setFinAEAmzSusp(this.finAEAmzSusp.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setLovDescFinAEToNoAmzName(this.finAEToNoAmz.getDescription());
			aFinanceType.setFinAEToNoAmz(this.finAEToNoAmz.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setLovDescFinToAmzName(this.finToAmz.getDescription());
			aFinanceType.setFinToAmz(this.finToAmz.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinanceType.setLovDescFinMAmzName(this.finMAmz.getDescription());
			aFinanceType.setFinAEMAmz(this.finMAmz.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setLovDescFinAERateChgName(this.finAERateChg.getDescription());
			aFinanceType.setFinAERateChg(this.finAERateChg.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setLovDescFinAERepayName(this.finAERepay.getDescription());
			aFinanceType.setFinAERepay(this.finAERepay.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setLovDescFinLatePayRuleName(this.finLatePayRule.getDescription());
			aFinanceType.setFinLatePayRule(this.finLatePayRule.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setLovDescFinInstDateName(this.finInstDate.getDescription());
			aFinanceType.setFinInstDate(this.finInstDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinAEWriteOffName(this.finAEWriteOff.getDescription());
			aFinanceType.setFinAEWriteOff(this.finAEWriteOff.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinAEWriteOffBKName(this.finAEWriteOffBK.getDescription());
			aFinanceType.setFinAEWriteOffBK(this.finAEWriteOffBK.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinAEGraceEndName(this.finAEGraceEnd.getDescription());
			aFinanceType.setFinAEGraceEnd(this.finAEGraceEnd.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setLovDescFinProvisionName(this.finProvision.getDescription());
			aFinanceType.setFinProvision(this.finProvision.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinSchdChangeName(this.finSchdChange.getDescription());
			aFinanceType.setFinSchdChange(this.finSchdChange.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinDepreciationRuleName(this.finDepreciationRule.getDescription());
			aFinanceType.setFinDepreciationRule(this.finDepreciationRule.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinDeffreqName(this.finDeffreq.getDescription());
			aFinanceType.setFinDeffreq(this.finDeffreq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinDefRepayName(this.finDefRepay.getDescription());
			aFinanceType.setFinDefRepay(this.finDefRepay.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinAECapitalizeName(this.finAECapitalize.getDescription());
			aFinanceType.setFinAECapitalize(this.finAECapitalize.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinAEProgClaimName(this.finAEProgClaim.getDescription());
			aFinanceType.setFinAEProgClaim(this.finAEProgClaim.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinAEMaturityName(this.finAEMaturity.getDescription());
			aFinanceType.setFinAEMaturity(this.finAEMaturity.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setLovDescFinAEEarlySettleName(this.finAEEarlySettle.getDescription());
			aFinanceType.setFinAEEarlySettle(this.finAEEarlySettle.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		showErrorDetails(wve, accountingEvent);

		// ++++++++++++++ End of Tab 4 +++++++++++++++++++//

		// ++++++++++++++ Start of Tab 6 +++++++++++++++++++//
		//Overdue Penalty Details
		try {
			aFinanceType.setApplyODPenalty(this.applyODPenalty.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setODIncGrcDays(this.oDIncGrcDays.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.applyODPenalty.isChecked() && getComboboxValue(this.oDChargeType).equals("#")) {
				throw new WrongValueException(this.oDChargeType, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_ODChargeType.value") }));
			}			
			aFinanceType.setODChargeType(getComboboxValue(this.oDChargeType));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setODGraceDays(this.oDGraceDays.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.applyODPenalty.isChecked() && getComboboxValue(this.oDChargeCalOn).equals("#")) {
				throw new WrongValueException(this.oDChargeCalOn, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_ODChargeCalOn.value") }));
			}	
			aFinanceType.setODChargeCalOn(getComboboxValue(this.oDChargeCalOn));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.applyODPenalty.isChecked() && !getComboboxValue(this.oDChargeType).equals(PennantConstants.List_Select)){
				if((PennantConstants.PERCONETIME.equals(getComboboxValue(this.oDChargeType)) || 
						PennantConstants.PERCONDUEDAYS.equals(getComboboxValue(this.oDChargeType))) && 
						this.oDChargeAmtOrPerc.getValue().compareTo(new BigDecimal(100)) > 0) {
					throw new WrongValueException(this.oDChargeAmtOrPerc, Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
							new String[] { Labels.getLabel("label_FinanceTypeDialog_ODChargeAmtOrPerc.value") }));
				}
			}
			
			if(getComboboxValue(this.oDChargeType).equals(PennantConstants.FLAT)){
				aFinanceType.setODChargeAmtOrPerc(PennantAppUtil.unFormateAmount(this.oDChargeAmtOrPerc.getValue(),
						getFinanceType().getLovDescFinFormetter()));
			}else if(PennantConstants.PERCONETIME.equals(getComboboxValue(this.oDChargeType)) || 
					PennantConstants.PERCONDUEDAYS.equals(getComboboxValue(this.oDChargeType))){
				aFinanceType.setODChargeAmtOrPerc(PennantAppUtil.unFormateAmount(this.oDChargeAmtOrPerc.getValue(),2));
			}
			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setODAllowWaiver(this.oDAllowWaiver.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.oDAllowWaiver.isChecked() && this.oDMaxWaiverPerc.getValue().compareTo(new BigDecimal(100)) > 0){
				throw new WrongValueException(this.oDMaxWaiverPerc, Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_ODMaxWaiver.value") }));
			}
			aFinanceType.setODMaxWaiverPerc(this.oDMaxWaiverPerc.getValue() == null ? BigDecimal.ZERO : this.oDMaxWaiverPerc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Stepping Details
		if (this.gb_SteppingDetails.isVisible() && this.stepFinance.isChecked()) {
			  aFinanceType.setStepFinance(this.stepFinance.isChecked());
			  aFinanceType.setSteppingMandatory(this.steppingMandatory.isChecked());
			  aFinanceType.setAlwManualSteps(this.allowManualSteps.isChecked());
			try {
				if (!this.allowManualSteps.isChecked()) {
					if (StringUtils.trimToEmpty(this.lovDescStepPolicyCodename.getValue()).equals("")) {
						throw new WrongValueException(this.btnSearchStepPolicy,
								Labels.getLabel("STATIC_INVALID",new String[] { Labels.getLabel("label_FinanceTypeDialog_AllowedStepPolicies.value") }));
					}
				}
				aFinanceType.setAlwdStepPolicies(this.lovDescStepPolicyCodename.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (!this.dftStepPolicy.getSelectedItem().getValue().toString().equals("#")) {
					aFinanceType.setDftStepPolicy(this.dftStepPolicy.getSelectedItem().getValue().toString());
				} else {
					if (!this.allowManualSteps.isChecked()) {
						throw new WrongValueException(this.dftStepPolicy,Labels.getLabel("STATIC_INVALID",
										new String[] { Labels.getLabel("label_FinanceTypeDialog_dftStepPolicy.value") }));
					} 
					aFinanceType.setDftStepPolicy(this.dftStepPolicy.getSelectedItem().getValue().toString());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			 aFinanceType.setStepFinance(false);
			 aFinanceType.setSteppingMandatory(false);
			 aFinanceType.setAlwManualSteps(false);
			 aFinanceType.setAlwdStepPolicies(null);
			 aFinanceType.setDftStepPolicy(null);
		}
			
		//showErrorDetails(wve, extendedDetails);
		showErrorDetails(wve, extendedDetails);
		// ++++++++++++++ End of Tab 6 +++++++++++++++++++//
		
		//Not visible fields		

		try {
			aFinanceType.setLovDescFinBankContingentAcTypeName(this.lovDescFinBankContingentAcTypeName.getValue());
			aFinanceType.setFinBankContingentAcType(this.finBankContingentAcType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLovDescFinContingentAcTypeName(this.lovDescFinContingentAcTypeName.getValue());
			aFinanceType.setFinContingentAcType(this.finContingentAcType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsOpenPftPayAcc(this.finIsOpenPftPayAcc.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency code and frequency month
			if ((!getComboboxValue(this.cbfinDftStmtFrqCode).equals("#")) && (getComboboxValue(this.cbfinDftStmtFrqMth).equals("#"))) {
				throw new WrongValueException(this.cbfinDftStmtFrqMth, Labels.getLabel("STATIC_INVALID",
				        new String[] { Labels.getLabel("label_FinanceTypeDialog_finDftStmtFrqMth.value") }));
			}
			aFinanceType.setFinDftStmtFrq(this.finDftStmtFrq.getValue() == null ? "" : this.finDftStmtFrq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency month and frequency day
			if ((!getComboboxValue(this.cbfinDftStmtFrqMth).equals("#")) && (getComboboxValue(this.cbfinDftStmtFrqDays).equals("#") && !this.cbfinDftStmtFrqDays.isDisabled())) {
				throw new WrongValueException(this.cbfinDftStmtFrqDays, Labels.getLabel("STATIC_INVALID",
				        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinDftStmtFrqDay.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setFinHistRetension(this.finHistRetension.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setFinCollateralReq(this.finCollateralReq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinCollateralOvrride(this.finCollateralOvrride.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//NON VISIBLE FILEDS

		try {
			aFinanceType.setFInGrcMinRate(this.fInGrcMinRate.getValue() == null ? BigDecimal.ZERO : this.fInGrcMinRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinGrcMaxRate(this.finGrcMaxRate.getValue() == null ? BigDecimal.ZERO : this.finGrcMaxRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (getComboboxValue(this.cbFinGrcScheduleOn).equals("#")) {
				throw new WrongValueException(this.cbFinGrcScheduleOn, Labels.getLabel("STATIC_INVALID",
				        new String[] { Labels.getLabel("label_FinanceTypeDialog_FinGrcScheduleOn.value") }));
			}
			aFinanceType.setFinGrcScheduleOn(getComboboxValue(this.cbFinGrcScheduleOn));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinGrcAlwRateChgAnyDate(this.finGrcAlwRateChgAnyDate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//Not visible Fields
	
		try {
			aFinanceType.setFInMinRate(this.fInMinRate.getValue() == null ? BigDecimal.ZERO : this.fInMinRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinMaxRate(this.finMaxRate.getValue() == null ? BigDecimal.ZERO : this.finMaxRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			mustBeHigher(finMaxRate, fInMinRate, "label_FinanceTypeDialog_FinMaxRate.value", "label_FinanceTypeDialog_FInMinRate.value");
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setFinAlwRateChangeAnyDate(this.finAlwRateChangeAnyDate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsAlwEarlyRpy(this.finIsAlwEarlyRpy.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsAlwEarlySettle(this.finIsAlwEarlySettle.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Non Visible Fields
		try {
			aFinanceType.setLovDescFinAEEarlyPayName(this.lovDescFinAEEarlyPayName.getValue());
			aFinanceType.setFinAEEarlyPay(this.finAEEarlyPay.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (validate && (this.listBoxFinTypeAccounts.getItems() == null || this.listBoxFinTypeAccounts.getItems().isEmpty())) {
				throw new WrongValueException(this.listBoxFinTypeAccounts, Labels.getLabel("tab_FinanceTypeDialog_FinTypeAccountDetails.value")+" Must Be Entered ");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		showErrorDetails(wve, finTypeAccountDetails);
		
		aFinanceType.setFinTypeAccounts(getFinTypeAccountList());
		doRemoveValidation();
		doRemoveLOVValidation();
		aFinanceType.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");

	}

	// For Tab Wise validations
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");
		if (wve.size() > 0) {
			tab.setSelected(true);
			doRemoveValidation();
			doRemoveLOVValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");

	}

	/**
	 * Opens the Dialog window modal. It checks if the dialog opens with a new or existing object and set the readOnly
	 * mode accordingly.
	 * 
	 * @param aFinanceType
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceType aFinanceType) throws InterruptedException {
		logger.debug("Entering");

		/*
		 * if aFinanceType == null then we opened the Dialog without arguments
		 * for a given entity, so we get a new Object().
		 */
		if (aFinanceType == null) {
			aFinanceType = getFinanceTypeService().getNewFinanceType();
			setFinanceType(aFinanceType);
		} else {
			setFinanceType(aFinanceType);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFinanceType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finType.focus();
		} else {
			this.finTypeDesc.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinanceType);
			setSteppingFieldsVisibility(aFinanceType.isStepFinance());
			doStoreInitValues();
			dodisableGracePeriod();
			doDisableDepreciationDFrq(aFinanceType.isFinDepreciationReq(), isReadOnly("FinanceTypeDialog_FinDepreciationFrq"));
			if (getFinanceType().isNewRecord() || getFinanceType().getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				this.finIsActive.setChecked(true);
				this.finIsActive.setDisabled(true);
			}
			if (getFinanceType().isNewRecord()) {
				this.finODRpyTries.setValue(-1);
				setDefaultValues();
			}
			onCheckODPenalty(false);
			doStoreInitValues();
			setDialog(this.window_FinanceTypeDialog);

		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/** Stores the init values in member Var's. <br> */
	private void doStoreInitValues() {
		logger.debug("Entering");

		this.oldVar_finType = this.finType.getValue();
		this.oldVar_finTypeDesc = this.finTypeDesc.getValue();
		this.oldVar_finCcy = this.finCcy.getValue();
		this.oldVar_finDivision = this.finDivision.getValue();
		this.oldVar_finAcType = this.finAcType.getValue();
		this.oldVar_pftPayAcType = this.pftPayAcType.getValue();
		this.oldVar_finContingentAcType = this.finContingentAcType.getValue();
		this.oldVar_lovDescFinContingentAcTypeName = this.lovDescFinContingentAcTypeName.getValue();
		this.oldVar_finSuspAcType = this.finSuspAcType.getValue();
		this.oldVar_finBankContingentAcType = this.finBankContingentAcType.getValue();
		this.oldVar_lovDescFinBankCtngAcTypeName = this.lovDescFinBankContingentAcTypeName.getValue();
		this.oldVar_finProvisionAcType = this.finProvisionAcType.getValue();
		this.oldVar_finIsGenRef = this.finIsGenRef.isChecked();
		this.oldVar_finMaxAmount = this.finMaxAmount.getValue();
		this.oldVar_finMinAmount = this.finMinAmount.getValue();
		this.oldVar_finIsOpenNewFinAc = this.finIsOpenNewFinAc.isChecked();
		this.oldVar_finDftStmtFrq = this.finDftStmtFrq.getValue();
		this.oldVar_finIsAlwMD = this.finIsAlwMD.isChecked();
		this.oldVar_finIsOpenPftPayAcc = this.finIsOpenPftPayAcc.isChecked();
		this.oldVar_finSchdMthd = this.cbfinSchdMthd.getSelectedIndex();
		this.oldVar_finDaysCalType = this.cbfinDaysCalType.getSelectedIndex();
		this.oldVar_finRateType = this.cbfinRateType.getSelectedIndex();
		this.oldVar_finGrcRateType = this.cbfinGrcRateType.getSelectedIndex();
		this.oldVar_finRepayMethod = this.cbfinRepayMethod.getSelectedIndex();
		this.oldVar_finSchCalCodeOnRvw = this.cbfinSchCalCodeOnRvw.getSelectedIndex();
		this.oldVar_fInIsAlwGrace = this.fInIsAlwGrace.isChecked();
		this.oldVar_finHistRetension = this.finHistRetension.intValue();
		this.oldVar_finBaseRate = this.finBaseRate.getValue();
		this.oldVar_finSplRate = this.finSplRate.getValue();
		this.oldVar_finAlwRateChangeAnyDate = this.finAlwRateChangeAnyDate.isChecked();
		this.oldVar_finGrcAlwRateChgAnyDate = this.finGrcAlwRateChgAnyDate.isChecked();
		this.oldVar_finIsIntCpzAtGrcEnd = this.finIsIntCpzAtGrcEnd.isChecked();
		this.oldVar_finIsDwPayRequired = this.finIsDwPayRequired.isChecked();
		this.oldVar_finMinDownPayAmount = this.finMinDownPayAmount.getValue();
		this.oldVar_finDeffreq = this.finDeffreq.getValue();
		this.oldVar_finDefRepay = this.finDefRepay.getValue();
		this.oldVar_finIntRate = this.finIntRate.getValue();
		this.oldVar_fInMinRate = this.fInMinRate.getValue();
		this.oldVar_finMaxRate = this.finMaxRate.getValue();
		this.oldVar_finDftIntFrq = this.finDftIntFrq.getValue();
		this.oldVar_finIsIntCpz = this.finIsIntCpz.isChecked();
		this.oldVar_finCpzFrq = this.finCpzFrq.getValue();
		this.oldVar_finIsRvwAlw = this.finIsRvwAlw.isChecked();
		this.oldVar_finRepayPftOnFrq = this.finRepayPftOnFrq.isChecked();
		this.oldVar_finRvwFrq = this.finRvwFrq.getValue();
		this.oldVar_finGrcBaseRate = this.finGrcBaseRate.getValue();
		this.oldVar_finGrcSplRate = this.finGrcSplRate.getValue();
		this.oldVar_finGrcIntRate = this.finGrcIntRate.getValue();
		this.oldVar_fInGrcMinRate = this.fInGrcMinRate.getValue();
		this.oldVar_finGrcMaxRate = this.finGrcMaxRate.getValue();
		this.oldVar_finGrcDftIntFrq = this.finGrcDftIntFrq.getValue();
		this.oldVar_finGrcIsIntCpz = this.finGrcIsIntCpz.isChecked();
		this.oldVar_finGrcCpzFrq = this.finGrcCpzFrq.getValue();
		this.oldVar_finGrcIsRvwAlw = this.finGrcIsRvwAlw.isChecked();
		this.oldVar_finGrcRvwFrq = this.finGrcRvwFrq.getValue();
		this.oldVar_finMinTerm = this.finMinTerm.intValue();
		this.oldVar_finMaxTerm = this.finMaxTerm.intValue();
		this.oldVar_finDftTerms = this.finDftTerms.intValue();
		this.oldVar_finRpyFrq = this.finRpyFrq.getValue();
		this.oldVar_finIsAlwPartialRpy = this.finIsAlwPartialRpy.isChecked();
		this.oldVar_finIsAlwDifferment = this.finIsAlwDifferment.isChecked();
		this.oldVar_finMaxDifferment = this.finMaxDifferment.getValue();
		this.oldVar_finIsAlwFrqDifferment = this.finIsAlwFrqDifferment.isChecked();
		this.oldVar_finPftUnChanged = this.finPftUnChanged.isChecked();
		this.oldVar_finMaxFrqDifferment = this.finMaxFrqDifferment.getValue();
		this.oldVar_finIsAlwEarlyRpy = this.finIsAlwEarlyRpy.isChecked();
		this.oldVar_finIsAlwEarlySettle = this.finIsAlwEarlySettle.isChecked();
		this.oldVar_finODRpyTries = this.finODRpyTries.intValue();
		this.oldVar_finAEAddDsbOD = this.finAEAddDsbOD.getValue();
		this.oldVar_finAEAddDsbFD = this.finAEAddDsbFD.getValue();
		this.oldVar_finAEAddDsbFDA = this.finAEAddDsbFDA.getValue();
		this.oldVar_finAEAmzNorm = this.finAEAmzNorm.getValue();
		this.oldVar_finAEAmzSusp = this.finAEAmzSusp.getValue();
		this.oldVar_finAEToNoAmz = this.finAEToNoAmz.getValue();
		this.oldVar_finToAmz = this.finToAmz.getValue();
		this.oldVar_finMAmz = this.finMAmz.getValue();
		this.oldVar_finAERateChg = this.finAERateChg.getValue();
		this.oldVar_finAERepay = this.finAERepay.getValue();
		this.oldVar_finLatePayRule = this.finLatePayRule.getValue();
		this.oldVar_finAEEarlyPay = this.finAEEarlyPay.getValue();
		this.oldVar_lovDescFinAEEarlyPayName = this.lovDescFinAEEarlyPayName.getValue();
		this.oldVar_finAEEarlySettle = this.finAEEarlySettle.getValue();
		this.oldVar_finAEWriteOff = this.finAEWriteOff.getValue();
		this.oldVar_finAEWriteOffBK = this.finAEWriteOffBK.getValue();
		this.oldVar_finAEGraceEnd = this.finAEGraceEnd.getValue();
		this.oldVar_FinProvisionName = this.finProvision.getValue();
		this.oldVar_FinInstDateName = this.finInstDate.getValue();
		this.oldVar_FinSchdChange = this.finSchdChange.getValue();
		this.oldVar_FinAECapitalize = this.finAECapitalize.getValue();
		this.oldVar_FinAEProgClaim = this.finAEProgClaim.getValue();
		this.oldVar_FinAEMaturity = this.finAEMaturity.getValue();
		this.oldVar_finIsActive = this.finIsActive.isChecked();
		this.oldVar_allowRIAInvestment = this.allowRIAInvestment.isChecked();
		this.oldVar_allowParllelFinance = this.allowParllelFinance.isChecked();
		this.oldVar_overrideLimit = this.overrideLimit.isChecked();
		this.oldVar_finCollateralOvrride = this.finCollateralOvrride.isChecked();
		this.oldVar_finCommitmentOvrride = this.finCommitmentOvrride.isChecked();
		this.oldVar_limitRequired = this.limitRequired.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		this.oldVar_finMargin = this.finMargin.getValue();
		this.oldVar_finGrcMargin = this.finGrcMargin.getValue();
		this.oldVar_finGrcSchdMthd = this.finGrcSchdMthd.getSelectedIndex();
		this.oldVar_finIsAlwGrcRepay = this.finIsAlwGrcRepay.isChecked();
		this.oldVar_finCommitmentReq = this.finCommitmentReq.isChecked();
		this.oldVar_finCollateralReq = this.finCollateralReq.isChecked();
		this.oldVar_finDepreciationReq = this.finDepreciationReq.isChecked();
		this.oldVar_finDepreciationFrq = this.finDepreciationFrq.getValue();
		this.oldVar_lovDescFinGrcIndBaseRateName = this.lovDescFinGrcIndBaseRateName.getValue();
		this.oldVar_FinAlwIndRate = this.finAlwIndRate.isChecked();
		this.oldVar_FinGrcAlwIndRate = this.finGrcAlwIndRate.isChecked();
		
		//Overdue Penalty Details
		this.oldVar_applyODPenalty = this.applyODPenalty.isChecked();
		this.oldVar_oDIncGrcDays = this.oDIncGrcDays.isChecked();
		this.oldVar_oDChargeType = getComboboxValue(this.oDChargeType);
		this.oldVar_oDGraceDays = this.oDGraceDays.intValue();
		this.oldVar_oDChargeCalOn = getComboboxValue(this.oDChargeCalOn);
		this.oldVar_oDChargeAmtOrPerc = this.oDChargeAmtOrPerc.getValue();
		this.oldVar_oDAllowWaiver = oDAllowWaiver.isChecked();
		this.oldVar_oDMaxWaiverPerc = this.oDMaxWaiverPerc.getValue();
		
		this.oldVar_stepFinance = this.stepFinance.isChecked();
		this.oldVar_alwdStepPolices = this.lovDescStepPolicyCodename.getValue();
		this.oldVar_steppingMandatory = this.steppingMandatory.isChecked();
		this.oldVar_allowManualSteps = this.allowManualSteps.isChecked();
		this.oldVar_dftStepPolicy = this.dftStepPolicy.getSelectedItem().getValue().toString();
		
		logger.debug("Leaving");
	}

	/** Resets the init values from member Var's. <br> */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.finType.setValue(this.oldVar_finType);
		this.finTypeDesc.setValue(this.oldVar_finTypeDesc);
		this.finCcy.setValue(this.oldVar_finCcy);
		this.finDivision.setValue(this.oldVar_finDivision);
		this.finAcType.setValue(this.oldVar_finAcType);
		this.pftPayAcType.setValue(this.oldVar_pftPayAcType);
		this.finContingentAcType.setValue(this.oldVar_finContingentAcType);
		this.lovDescFinContingentAcTypeName.setValue(this.oldVar_lovDescFinContingentAcTypeName);
		this.finSuspAcType.setValue(this.oldVar_finSuspAcType);
		this.finBankContingentAcType.setValue(this.oldVar_finBankContingentAcType);
		this.lovDescFinBankContingentAcTypeName.setValue(this.oldVar_lovDescFinBankCtngAcTypeName);
		this.finProvisionAcType.setValue(this.oldVar_finProvisionAcType);
		this.finIsGenRef.setChecked(this.oldVar_finIsGenRef);
		this.finMaxAmount.setValue(this.oldVar_finMaxAmount);
		this.finMinAmount.setValue(this.oldVar_finMinAmount);
		this.finIsOpenNewFinAc.setChecked(this.oldVar_finIsOpenNewFinAc);
		this.finDftStmtFrq.setValue(this.oldVar_finDftStmtFrq);
		this.finIsAlwMD.setChecked(this.oldVar_finIsAlwMD);
		this.finIsOpenPftPayAcc.setChecked(this.oldVar_finIsOpenPftPayAcc);
		this.fInIsAlwGrace.setChecked(this.oldVar_fInIsAlwGrace);
		this.finHistRetension.setValue(this.oldVar_finHistRetension);
		this.finBaseRate.setValue(this.oldVar_finBaseRate);
		this.finSplRate.setValue(this.oldVar_finSplRate);
		this.finAlwRateChangeAnyDate.setChecked(this.oldVar_finAlwRateChangeAnyDate);
		this.finGrcAlwRateChgAnyDate.setChecked(this.oldVar_finGrcAlwRateChgAnyDate);
		this.finIsIntCpzAtGrcEnd.setChecked(this.oldVar_finIsIntCpzAtGrcEnd);
		this.finIsDwPayRequired.setChecked(this.oldVar_finIsDwPayRequired);
		this.finMinDownPayAmount.setValue(this.oldVar_finMinDownPayAmount);
		this.cbfinSchdMthd.setSelectedIndex(this.oldVar_finSchdMthd);
		this.cbfinDaysCalType.setSelectedIndex(this.oldVar_finDaysCalType);
		this.cbfinGrcRateType.setSelectedIndex(this.oldVar_finGrcRateType);
		this.cbfinRateType.setSelectedIndex(this.oldVar_finRateType);
		this.cbfinRepayMethod.setSelectedIndex(this.oldVar_finRepayMethod);
		this.cbfinSchCalCodeOnRvw.setSelectedIndex(this.oldVar_finSchCalCodeOnRvw);
		this.finIntRate.setValue(this.oldVar_finIntRate);
		this.fInMinRate.setValue(this.oldVar_fInMinRate);
		this.finMaxRate.setValue(this.oldVar_finMaxRate);
		this.finDftIntFrq.setValue(this.oldVar_finDftIntFrq);
		this.finIsIntCpz.setChecked(this.oldVar_finIsIntCpz);
		this.finCpzFrq.setValue(this.oldVar_finCpzFrq);
		this.finIsRvwAlw.setChecked(this.oldVar_finIsRvwAlw);
		this.finRepayPftOnFrq.setChecked(this.oldVar_finRepayPftOnFrq);
		this.finRvwFrq.setValue(this.oldVar_finRvwFrq);
		this.finGrcBaseRate.setValue(this.oldVar_finGrcBaseRate);
		this.finGrcSplRate.setValue(this.oldVar_finGrcSplRate);
		this.finGrcIntRate.setValue(this.oldVar_finGrcIntRate);
		this.fInGrcMinRate.setValue(this.oldVar_fInGrcMinRate);
		this.finGrcMaxRate.setValue(this.oldVar_finGrcMaxRate);
		this.finGrcDftIntFrq.setValue(this.oldVar_finGrcDftIntFrq);
		this.finGrcIsIntCpz.setChecked(this.oldVar_finGrcIsIntCpz);
		this.finGrcCpzFrq.setValue(this.oldVar_finGrcCpzFrq);
		this.finGrcIsRvwAlw.setChecked(this.oldVar_finGrcIsRvwAlw);
		this.finGrcRvwFrq.setValue(this.oldVar_finGrcRvwFrq);
		this.finMinTerm.setValue(this.oldVar_finMinTerm);
		this.finMaxTerm.setValue(this.oldVar_finMaxTerm);
		this.finDftTerms.setValue(this.oldVar_finDftTerms);
		this.finRpyFrq.setValue(this.oldVar_finRpyFrq);
		this.finIsAlwPartialRpy.setChecked(this.oldVar_finIsAlwPartialRpy);
		this.finIsAlwDifferment.setChecked(this.oldVar_finIsAlwDifferment);
		this.finMaxDifferment.setValue(this.oldVar_finMaxDifferment);
		this.finIsAlwFrqDifferment.setChecked(this.oldVar_finIsAlwFrqDifferment);
		this.finPftUnChanged.setChecked(this.oldVar_finPftUnChanged);
		this.finMaxFrqDifferment.setValue(this.oldVar_finMaxFrqDifferment);
		this.finIsAlwEarlyRpy.setChecked(this.oldVar_finIsAlwEarlyRpy);
		this.finIsAlwEarlySettle.setChecked(this.oldVar_finIsAlwEarlySettle);
		this.finODRpyTries.setValue(this.oldVar_finODRpyTries);
		this.finAEAddDsbOD.setValue(this.oldVar_finAEAddDsbOD);
		this.finAEAddDsbFD.setValue(this.oldVar_finAEAddDsbFD);
		this.finAEAddDsbFDA.setValue(this.oldVar_finAEAddDsbFDA);
		this.finAEAmzNorm.setValue(this.oldVar_finAEAmzNorm);
		this.finAEAmzSusp.setValue(this.oldVar_finAEAmzSusp);
		this.finAEToNoAmz.setValue(this.oldVar_finAEToNoAmz);
		this.finToAmz.setValue(this.oldVar_finToAmz);
		this.finMAmz.setValue(this.oldVar_finMAmz);
		this.finAERateChg.setValue(this.oldVar_finAERateChg);
		this.finLatePayRule.setValue(this.oldVar_finLatePayRule);
		this.finAERepay.setValue(this.oldVar_finAERepay);
		this.finAEEarlyPay.setValue(this.oldVar_finAEEarlyPay);
		this.lovDescFinAEEarlyPayName.setValue(this.oldVar_lovDescFinAEEarlyPayName);
		this.finAEEarlySettle.setValue(this.oldVar_finAEEarlySettle);
		this.finAEWriteOff.setValue(this.oldVar_finAEWriteOff);
		this.finAEWriteOffBK.setValue(this.oldVar_finAEWriteOffBK);
		this.finAEGraceEnd.setValue(this.oldVar_finAEGraceEnd);
		this.finProvision.setValue(this.oldVar_FinProvisionName);
		this.finInstDate.setValue(this.oldVar_FinInstDateName);
		this.finSchdChange.setValue(this.oldVar_FinSchdChange);
		this.finAECapitalize.setValue(this.oldVar_FinAECapitalize);
		this.finAEProgClaim.setValue(this.oldVar_FinAEProgClaim);
		this.finAEMaturity.setValue(this.oldVar_FinAEMaturity);
		this.finIsActive.setChecked(this.oldVar_finIsActive);
		this.allowRIAInvestment.setChecked(this.oldVar_allowRIAInvestment);
		this.allowParllelFinance.setChecked(this.oldVar_allowParllelFinance);
		this.overrideLimit.setChecked(this.oldVar_overrideLimit);
		this.finCollateralOvrride.setChecked(this.oldVar_finCollateralOvrride);
		this.finCommitmentOvrride.setChecked(this.oldVar_finCommitmentOvrride);
		this.limitRequired.setChecked(this.oldVar_limitRequired);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		this.finDeffreq.setValue(this.oldVar_finDeffreq);
		this.finDefRepay.setValue(this.oldVar_finDefRepay);
		this.finGrcSchdMthd.setSelectedIndex(this.oldVar_finGrcSchdMthd);
		this.finIsAlwGrcRepay.setChecked(this.oldVar_finIsAlwGrcRepay);
		this.lovDescFinGrcIndBaseRateName.setValue(this.oldVar_lovDescFinGrcIndBaseRateName);
		this.finGrcAlwIndRate.setChecked(this.oldVar_FinGrcAlwIndRate);
		this.finAlwIndRate.setChecked(this.oldVar_FinAlwIndRate);
		this.finMargin.setValue(this.oldVar_finMargin);
		this.finGrcMargin.setValue(this.oldVar_finGrcMargin);
		this.finCommitmentReq.setChecked(this.oldVar_finCommitmentReq);
		this.finCollateralReq.setChecked(this.oldVar_finCollateralReq);
		this.finDepreciationReq.setChecked(this.oldVar_finDepreciationReq);
		this.finDepreciationFrq.setValue(this.oldVar_finDepreciationFrq);
		
		//Overdue Penalty Details
		this.applyODPenalty.setChecked(this.oldVar_applyODPenalty);
		this.oDIncGrcDays.setChecked(this.oldVar_oDIncGrcDays);
		fillComboBox(this.oDChargeType, this.oldVar_oDChargeType, PennantStaticListUtil.getODCChargeType(), "");
		this.oDGraceDays.setValue(this.oldVar_oDGraceDays);
		fillComboBox(this.oDChargeCalOn, this.oldVar_oDChargeCalOn, PennantStaticListUtil.getODCChargeType(), "");
		this.oDChargeAmtOrPerc.setValue(this.oldVar_oDChargeAmtOrPerc);
		this.oDAllowWaiver.setChecked(this.oldVar_oDAllowWaiver);
		this.oDMaxWaiverPerc.setValue(this.oldVar_oDMaxWaiverPerc);
		
		this.stepFinance.setChecked(this.oldVar_stepFinance);
		this.lovDescStepPolicyCodename.setValue(this.oldVar_alwdStepPolices);;
		this.steppingMandatory.setChecked(this.oldVar_steppingMandatory);
		this.allowManualSteps.setChecked(this.oldVar_allowManualSteps);
        fillComboBox(this.dftStepPolicy, this.oldVar_dftStepPolicy, this.stepPolicies, "");
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

		// To clear the Error Messages
		doClearMessages();

		if (this.oldVar_finType != this.finType.getValue()) {
			return true;
		}
		if (this.oldVar_finTypeDesc != this.finTypeDesc.getValue()) {
			return true;
		}
		if (this.oldVar_finCcy != this.finCcy.getValue()) {
			return true;
		}
		if (this.oldVar_finDivision != this.finDivision.getValue()) {
			return true;
		}
		if (this.oldVar_finAcType != this.finAcType.getValue()) {
			return true;
		}
		if (this.oldVar_pftPayAcType != this.pftPayAcType.getValue()) {
			return true;
		}
		if (this.oldVar_finContingentAcType != this.finContingentAcType.getValue()) {
			return true;
		}
		if (this.oldVar_finSuspAcType != this.finSuspAcType.getValue()) {
			return true;
		}
		if (this.oldVar_finBankContingentAcType != this.finBankContingentAcType.getValue()) {
			return true;
		}
		if (this.oldVar_finProvisionAcType != this.finProvisionAcType.getValue()) {
			return true;
		}
		if (this.oldVar_finIsGenRef != this.finIsGenRef.isChecked()) {
			return true;
		}
		if (this.oldVar_finMaxAmount != this.finMaxAmount.getValue()) {
			return true;
		}
		if (this.oldVar_finMinAmount != this.finMinAmount.getValue()) {
			return true;
		}
		if (this.oldVar_finIsOpenNewFinAc != this.finIsOpenNewFinAc.isChecked()) {
			return true;
		}
		if (this.oldVar_finDftStmtFrq != this.finDftStmtFrq.getValue()) {
			return true;
		}
		if (this.oldVar_finIsAlwMD != this.finIsAlwMD.isChecked()) {
			return true;
		}
		if (this.oldVar_finIsOpenPftPayAcc != this.finIsOpenPftPayAcc.isChecked()) {
			return true;
		}
		if (this.oldVar_fInIsAlwGrace != this.fInIsAlwGrace.isChecked()) {
			return true;
		}
		if (this.oldVar_finHistRetension != this.finHistRetension.intValue()) {
			return true;
		}
		if (this.oldVar_finRateType != this.cbfinRateType.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_finBaseRate != this.finBaseRate.getValue()) {
			return true;
		}
		if (this.oldVar_finSplRate != this.finSplRate.getValue()) {
			return true;
		}
		if (this.oldVar_finAlwRateChangeAnyDate != this.finAlwRateChangeAnyDate.isChecked()) {
			return true;
		}
		if (this.oldVar_finGrcAlwRateChgAnyDate != this.finGrcAlwRateChgAnyDate.isChecked()) {
			return true;
		}
		if (this.oldVar_finIsIntCpzAtGrcEnd != this.finIsIntCpzAtGrcEnd.isChecked()) {
			return true;
		}
		if (this.oldVar_finIntRate != this.finIntRate.getValue()) {
			return true;
		}
		if (this.oldVar_fInMinRate != this.fInMinRate.getValue()) {
			return true;
		}
		if (this.oldVar_finMaxRate != this.finMaxRate.getValue()) {
			return true;
		}
		if (this.oldVar_finDftIntFrq != this.finDftIntFrq.getValue()) {
			return true;
		}

		if (this.oldVar_finIsIntCpz != this.finIsIntCpz.isChecked()) {
			return true;
		}
		if (this.oldVar_finCpzFrq != this.finCpzFrq.getValue()) {
			return true;
		}
		if (this.oldVar_finIsRvwAlw != this.finIsRvwAlw.isChecked()) {
			return true;
		}
		if (this.oldVar_finRepayPftOnFrq != this.finRepayPftOnFrq.isChecked()) {
			return true;
		}
		if (this.oldVar_finRvwFrq != this.finRvwFrq.getValue()) {
			return true;
		}
		if (this.oldVar_finMargin != this.finMargin.getValue()) {
			return true;
		}
		if (this.oldVar_finGrcMargin != this.finGrcMargin.getValue()) {
			return true;
		}
		if (this.oldVar_finGrcRateType != this.cbfinGrcRateType.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_finGrcBaseRate != this.finGrcBaseRate.getValue()) {
			return true;
		}
		if (this.oldVar_finGrcSplRate != this.finGrcSplRate.getValue()) {
			return true;
		}
		if (this.oldVar_finGrcDftIntFrq != this.finGrcDftIntFrq.getValue()) {
			return true;
		}
		if (this.oldVar_finGrcIsIntCpz != this.finGrcIsIntCpz.isChecked()) {
			return true;
		}
		if (this.oldVar_finGrcCpzFrq != this.finGrcCpzFrq.getValue()) {
			return true;
		}
		if (this.oldVar_finGrcIsRvwAlw != this.finGrcIsRvwAlw.isChecked()) {
			return true;
		}
		if (this.oldVar_finGrcRvwFrq != this.finGrcRvwFrq.getValue()) {
			return true;
		}
		if (this.oldVar_finMinTerm != this.finMinTerm.intValue()) {
			return true;
		}
		if (this.oldVar_finMaxTerm != this.finMaxTerm.intValue()) {
			return true;
		}
		if (this.oldVar_finDftTerms != this.finDftTerms.intValue()) {
			return true;
		}
		if (this.oldVar_finRpyFrq != this.finRpyFrq.getValue()) {
			return true;
		}
		if (this.oldVar_finRepayMethod != this.cbfinRepayMethod.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_finIsAlwPartialRpy != this.finIsAlwPartialRpy.isChecked()) {
			return true;
		}
		if (this.oldVar_finIsAlwDifferment != this.finIsAlwDifferment.isChecked()) {
			return true;
		}
		if (this.oldVar_finMaxDifferment != (this.finMaxDifferment.getValue() == null ? 0 : this.finMaxDifferment.getValue())) {
			return true;
		}
		if (this.oldVar_finIsAlwFrqDifferment != this.finIsAlwFrqDifferment.isChecked()) {
			return true;
		}
		if (this.oldVar_finPftUnChanged != this.finPftUnChanged.isChecked()) {
			return true;
		}
		if (this.oldVar_finMaxFrqDifferment != (this.finMaxFrqDifferment.getValue() == null ? 0 : this.finMaxFrqDifferment.getValue())) {
			return true;
		}
		if (this.oldVar_finIsAlwEarlyRpy != this.finIsAlwEarlyRpy.isChecked()) {
			return true;
		}
		if (this.oldVar_finIsAlwEarlySettle != this.finIsAlwEarlySettle.isChecked()) {
			return true;
		}
		if (!this.oldVar_finLatePayRule.equals(this.finLatePayRule.getValue())) {
			return true;
		}
		if (!this.oldVar_finAEAddDsbOD.equals(this.finAEAddDsbOD.getValue())) {
			return true;
		}
		if (!this.oldVar_finAEAddDsbFD.equals(this.finAEAddDsbFD.getValue())) {
			return true;
		}
		if (!this.oldVar_finAEAddDsbFDA.equals(this.finAEAddDsbFDA.getValue())) {
			return true;
		}
		if (!this.oldVar_finAEAmzNorm.equals(this.finAEAmzNorm.getValue())) {
			return true;
		}
		if (!this.oldVar_finAEAmzSusp.equals(this.finAEAmzSusp.getValue())) {
			return true;
		}
		if (!this.oldVar_finAEToNoAmz.equals(this.finAEToNoAmz.getValue())) {
			return true;
		}
		if (!this.oldVar_finToAmz.equals(this.finToAmz.getValue())) {
			return true;
		}
		if (!this.oldVar_finMAmz.equals(this.finMAmz.getValue())) {
			return true;
		}
		if (!this.oldVar_finAERateChg.equals(this.finAERateChg.getValue())) {
			return true;
		}
		if (!this.oldVar_finAERepay.equals(this.finAERepay.getValue())) {
			return true;
		}
		if (this.oldVar_finAEEarlyPay != this.finAEEarlyPay.getValue()) {
			return true;
		}
		if (!this.oldVar_finAEEarlySettle.equals(this.finAEEarlySettle.getValue())) {
			return true;
		}
		if (!this.oldVar_finAEWriteOff.equals(this.finAEWriteOff.getValue())) {
			return true;
		}
		if (!this.oldVar_finAEWriteOffBK.equals(this.finAEWriteOffBK.getValue())) {
			return true;
		}
		if (!this.oldVar_finAEGraceEnd.equals(this.finAEGraceEnd.getValue())) {
			return true;
		}
		if (!this.oldVar_FinProvisionName.equals(this.finProvision.getValue())) {
			return true;
		}
		if (!this.oldVar_FinInstDateName.equals(this.finInstDate.getValue())) {
			return true;
		}
		if (!this.oldVar_FinSchdChange.equals(this.finSchdChange.getValue())) {
			return true;
		}
		if (!this.oldVar_FinAECapitalize.equals(this.finAECapitalize.getValue())) {
			return true;
		}
		if (!this.oldVar_FinAEProgClaim.equals(this.finAEProgClaim.getValue())) {
			return true;
		}
		if (!this.oldVar_FinAEMaturity.equals(this.finAEMaturity.getValue())) {
			return true;
		}
		if (!this.oldVar_finDeffreq.equals(this.finDeffreq.getValue())) {
			return true;
		}
		if (!this.oldVar_finDefRepay.equals(this.finDefRepay.getValue())) {
			return true;
		}
		if (this.oldVar_finGrcSchdMthd != this.finGrcSchdMthd.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_finIsAlwGrcRepay != this.finIsAlwGrcRepay.isChecked()) {
			return true;
		}
		if (!getFinanceType().isNewRecord()) {
			if (this.oldVar_finIsActive != this.finIsActive.isChecked()) {
				return true;
			}
			if (this.oldVar_finGrcIntRate != this.finGrcIntRate.getValue()) {
				return true;
			}
			if (this.oldVar_fInGrcMinRate != this.fInGrcMinRate.getValue()) {
				return true;
			}
			if (this.oldVar_finGrcMaxRate != this.finGrcMaxRate.getValue()) {
				return true;
			}
			if (this.oldVar_finODRpyTries != this.finODRpyTries.intValue()) {
				return true;
			}
		}
		// Fee Charges list comparison

		if (oldVar_finIsDwPayRequired != this.finIsDwPayRequired.isChecked()) {
			return true;
		}
		if (oldVar_finMinDownPayAmount != this.finMinDownPayAmount.getValue()) {
			return true;
		}
		if (this.oldVar_finSchdMthd != this.cbfinSchdMthd.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_finDaysCalType != this.cbfinDaysCalType.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_finSchCalCodeOnRvw != this.cbfinSchCalCodeOnRvw.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_finCommitmentReq != this.finCommitmentReq.isChecked()) {
			return true;
		}
		if (this.oldVar_finCollateralReq != this.finCollateralReq.isChecked()) {
			return true;
		}
		if (this.oldVar_finDepreciationReq != this.finDepreciationReq.isChecked()) {
			return true;
		}
		if (this.oldVar_finDepreciationFrq != this.finDepreciationFrq.getValue()) {
			return true;
		}
		if (this.oldVar_lovDescFinGrcIndBaseRateName != this.lovDescFinGrcIndBaseRateName.getValue()) {
			return true;
		}
		if (this.oldVar_allowRIAInvestment != this.allowRIAInvestment.isChecked()) {
			return true;
		}
		if (this.oldVar_allowParllelFinance != this.allowParllelFinance.isChecked()) {
			return true;
		}
		if (this.oldVar_overrideLimit != this.overrideLimit.isChecked()) {
			return true;
		}
		if (this.oldVar_finCollateralOvrride != this.finCollateralOvrride.isChecked()) {
			return true;
		}
		if (this.oldVar_finCommitmentOvrride != this.finCommitmentOvrride.isChecked()) {
			return true;
		}
		if (this.oldVar_limitRequired != this.limitRequired.isChecked()) {
			return true;
		}
		
		//Overdue Penalty Details
		
		if (this.oldVar_applyODPenalty != this.applyODPenalty.isChecked()) {
			return true;
		}
		if (this.oldVar_oDIncGrcDays != this.oDIncGrcDays.isChecked()) {
			return true;
		}
		if (this.oldVar_oDChargeType != getComboboxValue(this.oDChargeType)) {
			return true;
		}
		if (this.oldVar_oDGraceDays != this.oDGraceDays.intValue()) {
			return true;
		}
		if (this.oldVar_oDChargeCalOn != getComboboxValue(this.oDChargeCalOn)) {
			return true;
		}
		if (this.oldVar_oDChargeAmtOrPerc != this.oDChargeAmtOrPerc.getValue()) {
			return true;
		}
		if (this.oldVar_oDAllowWaiver != this.oDAllowWaiver.isChecked()) {
			return true;
		}
		if (this.oldVar_oDMaxWaiverPerc != this.oDMaxWaiverPerc.getValue()) {
			return true;
		}

		// Stepping Details
		if (this.oldVar_stepFinance != this.stepFinance.isChecked()) {
			return true;
		}
		if (this.oldVar_alwdStepPolices != this.lovDescStepPolicyCodename.getValue()) {
			return true;
		}
		if (this.oldVar_steppingMandatory != this.steppingMandatory.isChecked()) {
			return true;
		}
		if (this.oldVar_allowManualSteps != this.allowManualSteps.isChecked()) {
			return true;
		}
		if (this.oldVar_dftStepPolicy != this.dftStepPolicy.getSelectedItem().getValue().toString()) {
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

		// ++++++++++++ Basic Details tab +++++++++++++++++++//
		if (!this.finType.isReadonly()) {
			this.finType.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinType.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		
		if (!this.finTypeDesc.isReadonly()) {
			this.finTypeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinTypeDesc.value"), PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		
		if (!this.finMinDownPayAmount.isDisabled() && this.finIsDwPayRequired.isChecked()) {
			this.finMinDownPayAmount.setConstraint(new PercentageValidator(5, 2, Labels.getLabel("label_FinanceTypeDialog_FinMinDownPayAmount.value"), true));
		}

		/*
		 * To Check Whether it is save or submit if save no validation else it should validate
		 */
		// ++++++ Schedule Profit tab ++++++++++++++//
		if (!this.finIntRate.isReadonly()) {
			this.finIntRate.setConstraint(new RateValidator(13, 9, Labels.getLabel("label_FinanceTypeDialog_FinIntRate.value")));
		}
		
		if (!this.fInMinRate.isReadonly()) {
			this.fInMinRate.setConstraint(new RateValidator(13, 9, Labels.getLabel("label_FinanceTypeDialog_FInMinRate.value")));
		}
		
		if (!this.finMaxRate.isReadonly()) {
			this.finMaxRate.setConstraint(new RateValidator(13, 9, Labels.getLabel("label_FinanceTypeDialog_FinMaxRate.value")));
		}

	/*	if (!this.finMargin.isReadonly()) {
			this.finMargin.setConstraint(new RateValidator(13, 9, Labels.getLabel("label_FinanceTypeDialog_FinMargin.value")));
		}*/

		// +++++++++ Grace Period tab+++++++++++++++//
		// TO Check whether the tab is Not Disable
		if (!this.gracePeriod.isDisabled()) {
			if (!this.finGrcIntRate.isReadonly()) {
				this.finGrcIntRate.setConstraint(new RateValidator(13, 9, Labels.getLabel("label_FinanceTypeDialog_FinGrcIntRate.value")));
			}
			if (!this.fInGrcMinRate.isReadonly()) {
				this.fInGrcMinRate.setConstraint(new RateValidator(13, 9, Labels.getLabel("label_FinanceTypeDialog_FInGrcMinRate.value")));
			}
			if (!this.finGrcMaxRate.isReadonly()) {
				this.finGrcMaxRate.setConstraint(new RateValidator(13, 9, Labels.getLabel("label_FinanceTypeDialog_FinGrcMaxRate.value")));
			}
			if (!this.finGrcMargin.isReadonly()) {
				this.finGrcMargin.setConstraint(new RateValidator(13, 9, Labels.getLabel("label_FinanceTypeDialog_FinGrcMargin.value")));
			}
		}
		if (validate) {
/*			if (!this.finMaxAmount.isReadonly() && this.finMaxAmount.getValue().compareTo(BigDecimal.ZERO) != 0 ) {
				this.finMaxAmount.setConstraint(new AmountValidator(18, 0, Labels.getLabel("label_FinanceTypeDialog_FinMaxAmount.value")));
			}
			if (!this.finMinAmount.isReadonly() &&  this.finMinAmount.getValue().compareTo(BigDecimal.ZERO) != 0 ) {
				this.finMinAmount.setConstraint(new AmountValidator(18, 0, Labels.getLabel("label_FinanceTypeDialog_FinMinAmount.value")));
			}*/
			if (!this.finHistRetension.isReadonly()) {
				this.finHistRetension.setConstraint(new IntValidator(3, Labels.getLabel("label_FinanceTypeDialog_FinHistRetension.value")));
			}
		} else {
			/*if (!this.finMaxAmount.isReadonly() && this.finMaxAmount.getValue() != null && this.finMaxAmount.getValue().intValue() != 0) {
				this.finMaxAmount.setConstraint(new AmountValidator(18, 0, Labels.getLabel("label_FinanceTypeDialog_FinMaxAmount.value")));
			}
			if (!this.finMinAmount.isReadonly() && this.finMinAmount.getValue() != null && this.finMinAmount.getValue().intValue() != 0) {
				this.finMinAmount.setConstraint(new AmountValidator(18, 0, Labels.getLabel("label_FinanceTypeDialog_FinMinAmount.value")));
			}*/
		}

		logger.debug("Leaving");
	}

	/** Disables the Validation by setting empty constraints. */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finType.setConstraint("");
		this.finTypeDesc.setConstraint("");
		this.finMaxAmount.setConstraint("");
		this.finMinAmount.setConstraint("");
		this.finDftStmtFrq.setConstraint("");
		this.finHistRetension.setConstraint("");
		this.finIntRate.setConstraint("");
		this.fInMinRate.setConstraint("");
		this.finMaxRate.setConstraint("");
		this.finDftIntFrq.setConstraint("");
		this.finCpzFrq.setConstraint("");
		this.finRvwFrq.setConstraint("");
		this.finGrcIntRate.setConstraint("");
		this.fInGrcMinRate.setConstraint("");
		this.finGrcMaxRate.setConstraint("");
		this.finGrcDftIntFrq.setConstraint("");
		this.finGrcCpzFrq.setConstraint("");
		this.finGrcRvwFrq.setConstraint("");
		this.finMinTerm.setConstraint("");
		this.finMaxTerm.setConstraint("");
		this.finDftTerms.setConstraint("");
		this.finRpyFrq.setConstraint("");
		this.finODRpyTries.setConstraint("");
		logger.debug("Leaving");
	}

	/** Set Validations for LOV Fields */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		// +++++++ Basic Details Tab +++++++++++++//

		this.finCcy.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinCcy.value"), null, true));
		
		if (validate) {

			this.finAcType.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinAcType.value"), null, true));

			this.pftPayAcType.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_PftPayAcType.value"), null, true));
					
			this.finSuspAcType.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinSuspAcType.value"), null, true));
					
			this.finProvisionAcType.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinProvisionAcType.value"), null, true));
			
			this.finDivision.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinDivision.value"), null, true));
			
			/*
			this.lovDescFinContingentAcTypeName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
					new String[] { Labels.getLabel("label_FinanceTypeDialog_FinContingentAcType.value") }));
					
			this.lovDescFinBankContingentAcTypeName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_FinanceTypeDialog_FinBankContingentAcType.value") }));	

			this.lovDescFinInstDateName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_FinanceTypeDialog_FinInstDate.value") }));*/

			// ++++++++++ Accounting Event tab ++++++++++++++//

			this.finAEAddDsbOD.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinAEAddDsbOD.value"), null, true));

			this.finAEAddDsbFD.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinAEAddDsbFD.value"), null, true));

			if(!StringUtils.trimToEmpty(this.finAEAddDsbOD.getValue()).equals("") && 
					!StringUtils.trimToEmpty(this.finAEAddDsbFD.getValue()).equals("")){
				if(!this.finAEAddDsbOD.getValue().equals(this.finAEAddDsbFD.getValue())){
					this.finAEAddDsbFDA.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinAEAddDsbFDA.value"), null, true));
				}
			}
			
			this.finAEAmzNorm.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinAEAmzNorm.value"), null, true));

			this.finAEAmzSusp.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinAEAmzSusp.value"), null, true));

			/*this.lovDescFinAEToNoAmzName.setConstraint("NO EMPTY:"
			        + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FinanceTypeDialog_FinAEToNoAmz.value") }));

			this.lovDescFinToAmzName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
					new String[] { Labels.getLabel("label_FinanceTypeDialog_FinToAmz.value") }));
			
				this.lovDescFinMAmzName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
						new String[] { Labels.getLabel("label_FinanceTypeDialog_FinMAmz.value") })); */

			this.finAERateChg.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinAEIncPft.value"), null, true));

			this.finAERepay.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinAERepay.value"), null, true));

			this.finAEEarlySettle.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[] { Labels.getLabel("label_FinanceTypeDialog_FinAEEarlySettle.value") }));
			
			/*this.lovDescFinAEEarlyPayName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
					new String[] { Labels.getLabel("label_FinanceTypeDialog_FinAEEarlyPay.value") }));*/

			/*this.lovDescFinAEWriteOffName.setConstraint("NO EMPTY:"
			        + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FinanceTypeDialog_FinAEWriteOff.value") }));
			
			this.lovDescFinAEWriteOffBKName.setConstraint("NO EMPTY:"
					+ Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FinanceTypeDialog_FinAEWriteOffBK.value") }));
			
			this.lovDescFinAEGraceEndName.setConstraint("NO EMPTY:"
					+ Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FinanceTypeDialog_FinAEGraceEnd.value") }));*/

			this.finLatePayRule.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeSearch_FinLatePayRule.value"), null, true));

			/*if (!this.btnSearchFinDeffreq.isDisabled()) {
				this.lovDescFinDeffreqName.setConstraint("NO EMPTY:"
				        + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FinanceTypeDialog_FinDeffreq.value") }));
			}

			this.lovDescFinAECapitalizeName.setConstraint("NO EMPTY:"
			        + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FinanceTypeSearch_FinCapitalize.value") }));*/

			if (!this.finAEProgClaim.isReadonly()) {
				//this.lovDescFinAEProgClaimName.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinAEProgClaim.value"), null, true));
			}
			this.finSchdChange.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinSchdChange.value"), null, true));

			this.finProvision.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinProvision.value"), null, true));

			if (!this.finDepreciationRule.isReadonly()) {
				this.finDepreciationRule.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinDepreciationRule.value"), null, true));
			}

			/*if (!this.btnSearchFinDefRepay.isDisabled()) {
				this.lovDescFinDefRepayName.setConstraint("NO EMPTY:"
				        + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FinanceTypeDialog_FinDefRepay.value") }));
			}*/

			//Indicative rates
			if (this.finGrcAlwIndRate.isChecked() && !this.btnSearchFinGrcIndBaseRate.isDisabled()) {
				this.lovDescFinGrcIndBaseRateName.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinGrcIndBaseRate.value"), null, true));
			}
			if (this.finAlwIndRate.isChecked() && !this.finIndBaseRate.isReadonly()) {
				this.finIndBaseRate.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinIndBaseRate.value"), null, true));
			}
		}
		logger.debug("Leaving");
	}

	/** Remove validations for LOV Fields */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.finCcy.setConstraint("");
		this.finDivision.setConstraint("");
		this.finAcType.setConstraint("");
		this.pftPayAcType.setConstraint("");
		this.lovDescFinContingentAcTypeName.setConstraint("");
		this.finSuspAcType.setConstraint("");
		this.lovDescFinBankContingentAcTypeName.setConstraint("");
		this.finProvisionAcType.setConstraint("");
		this.finInstDate.setConstraint("");
		this.finBaseRate.setConstraint("");
		this.finSplRate.setConstraint("");
		this.finGrcBaseRate.setConstraint("");
		this.finGrcSplRate.setConstraint("");
		this.finAEAddDsbOD.setConstraint("");
		this.finAEAddDsbFD.setConstraint("");
		this.finAEAddDsbFDA.setConstraint("");
		this.finAEAmzNorm.setConstraint("");
		this.finAEAmzSusp.setConstraint("");
		this.finAEToNoAmz.setConstraint("");
		this.finToAmz.setConstraint("");
		this.finMAmz.setConstraint("");
		this.finAERateChg.setConstraint("");
		this.finAERepay.setConstraint("");
		this.lovDescFinAEEarlyPayName.setConstraint("");
		this.finAEEarlySettle.setConstraint("");
		this.finAEWriteOff.setConstraint("");
		this.finAEWriteOffBK.setConstraint("");
		this.finAEGraceEnd.setConstraint("");
		this.lovDescFinGrcIndBaseRateName.setConstraint("");
		this.finIndBaseRate.setConstraint("");
		this.finDepreciationRule.setErrorMessage("");

		this.finDefRepay.setConstraint("");
		this.finDeffreq.setConstraint("");
		this.finLatePayRule.setConstraint("");
		this.finSchdChange.setConstraint("");
		this.finProvision.setConstraint("");
		this.finAECapitalize.setConstraint("");
		this.finAEProgClaim.setConstraint("");
		
		//Overdue Penalty Details
		this.oDChargeCalOn.setConstraint("");
		this.oDChargeType.setConstraint("");
		this.oDChargeAmtOrPerc.setConstraint("");
		this.oDMaxWaiverPerc.setConstraint("");
		
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a FinanceType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final FinanceType aFinanceType = new FinanceType("");
		BeanUtils.copyProperties(getFinanceType(), aFinanceType);
		int prvselc = this.userAction.getSelectedIndex();
		for (int i = 0; i < this.userAction.getItems().size(); i++) {
			Radio radio = (Radio) this.userAction.getItems().get(i);
			if (radio.getLabel().trim().equalsIgnoreCase("Submit")) {
				radio.setSelected(true);
			}
		}
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aFinanceType.getFinType();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aFinanceType.getRecordType()).equals("")) {
				aFinanceType.setVersion(aFinanceType.getVersion() + 1);
				aFinanceType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aFinanceType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aFinanceType, tranType)) {
					refreshList();
					closeDialog(this.window_FinanceTypeDialog, "FinanceTypeDialog");
				}

			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}

		} else {
			this.userAction.setSelectedIndex(prvselc);
		}
		logger.debug("Leaving");
	}

	/** Create a new FinanceType object. <br> */
	private void doNew() {
		logger.debug("Entering doNew()");

		// remember the old Var's
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		/*
		 * we don't create a new FinanceType() in the frontEnd. we get it from the backEnd.
		 */
		final FinanceType aFinanceType = getFinanceTypeService().getNewFinanceType();
		aFinanceType.setNewRecord(true);
		setFinanceType(aFinanceType);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.finType.focus();
		logger.debug("Leaving doNew()");
	}

	/**
	 * Set the components for edit mode. <br>
	 * MSTGRP1_MAKER
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getFinanceType().isNewRecord()) {
			this.finType.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.finIsOpenNewFinAc.setChecked(true);

			this.finHistRetension.setValue(12);
			this.btnCopyTo.setDisabled(true);
			this.btnCopyTo.setVisible(false);
		} else {
			this.finType.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		//Tab 1
		this.finTypeDesc.setReadonly(isReadOnly("FinanceTypeDialog_finTypeDesc"));
		this.finCcy.setReadonly(isReadOnly("FinanceTypeDialog_finCcy"));
		this.finDivision.setReadonly(isReadOnly("FinanceTypeDialog_finDivision"));
		this.cbfinDaysCalType.setDisabled(isReadOnly("FinanceTypeDialog_finDaysCalType"));
		this.finAcType.setReadonly(isReadOnly("FinanceTypeDialog_finAcType"));
		this.finIsOpenNewFinAc.setDisabled(isReadOnly("FinanceTypeDialog_finIsOpenNewFinAc"));
		this.finMinAmount.setReadonly(isReadOnly("FinanceTypeDialog_finMinAmount"));
		this.finMaxAmount.setReadonly(isReadOnly("FinanceTypeDialog_finMaxAmount"));

		this.finIsDwPayRequired.setDisabled(isReadOnly("FinanceTypeDialog_finIsDwPayRequired"));
		this.finMinDownPayAmount.setDisabled(isReadOnly("FinanceTypeDialog_finMinDownPayAmount"));
		this.finIsGenRef.setDisabled(isReadOnly("FinanceTypeDialog_finIsGenRef"));
		this.fInIsAlwGrace.setDisabled(isReadOnly("FinanceTypeDialog_fInIsAlwGrace"));
		this.finIsAlwMD.setDisabled(isReadOnly("FinanceTypeDialog_finIsAlwMD"));
		this.finDepreciationReq.setDisabled(isReadOnly("FinanceTypeDialog_FinDepreciationReq"));
		this.finCommitmentReq.setDisabled(isReadOnly("FinanceTypeDialog_FinCommitmentReq"));
		this.finCommitmentOvrride.setDisabled(isReadOnly("FinanceTypeDialog_finCommitmentOvrride"));
		this.limitRequired.setDisabled(isReadOnly("FinanceTypeDialog_limitRequired"));
		this.overrideLimit.setDisabled(isReadOnly("FinanceTypeDialog_overrideLimit"));
		this.allowRIAInvestment.setDisabled(isReadOnly("FinanceTypeDialog_allowRIAInvestment"));
		this.allowParllelFinance.setDisabled(isReadOnly("FinanceTypeDialog_allowParllelFinance"));
		this.finIsActive.setDisabled(isReadOnly("FinanceTypeDialog_finIsActive"));

		//Hidden
		this.pftPayAcType.setReadonly(isReadOnly("FinanceTypeDialog_pftPayAcType"));
		this.btnSearchFinContingentAcType.setDisabled(isReadOnly("FinanceTypeDialog_finContingentAcType"));
		this.finSuspAcType.setReadonly(isReadOnly("FinanceTypeDialog_finSuspAcType"));
		this.btnSearchFinBankContingentAcType.setDisabled(isReadOnly("FinanceTypeDialog_finBankContingentAcType"));
		this.finProvisionAcType.setReadonly(isReadOnly("FinanceTypeDialog_finProvisionAcType"));
		//Tab 2
		this.cbfinGrcRateType.setDisabled(isReadOnly("FinanceTypeDialog_finGrcRateType"));
		this.finGrcIntRate.setReadonly(isReadOnly("FinanceTypeDialog_finGrcIntRate"));
		this.finGrcBaseRate.setReadonly(isReadOnly("FinanceTypeDialog_finGrcBaseRate"));
		this.finGrcSplRate.setReadonly(isReadOnly("FinanceTypeDialog_finGrcSplRate"));
		this.finMargin.setDisabled(isReadOnly("FinanceTypeDialog_FinMargin"));
		this.finGrcDftIntFrq.setDisabled(isReadOnly("FinanceTypeDialog_finGrcDftIntFrq"));
		this.cbfinGrcDftIntFrqCode.setDisabled(isReadOnly("FinanceTypeDialog_finGrcDftIntFrq"));
		this.cbfinGrcDftIntFrqMth.setDisabled(isReadOnly("FinanceTypeDialog_finGrcDftIntFrq"));
		this.cbfinGrcDftIntFrqDays.setDisabled(isReadOnly("FinanceTypeDialog_finGrcDftIntFrq"));
		this.finIsAlwGrcRepay.setDisabled(isReadOnly("FinanceTypeDialog_FinIsAlwGrcRepay"));
		this.finGrcSchdMthd.setDisabled(isReadOnly("FinanceTypeDialog_FinGrcSchdMthd"));
		this.finGrcIsIntCpz.setDisabled(isReadOnly("FinanceTypeDialog_finGrcIsIntCpz"));
		this.finGrcCpzFrq.setDisabled(isReadOnly("FinanceTypeDialog_finGrcCpzFrq"));
		this.cbfinGrcCpzFrqCode.setDisabled(isReadOnly("FinanceTypeDialog_finGrcCpzFrq"));
		this.cbfinGrcCpzFrqMth.setDisabled(isReadOnly("FinanceTypeDialog_finGrcCpzFrq"));
		this.cbfinGrcCpzFrqDays.setDisabled(isReadOnly("FinanceTypeDialog_finGrcCpzFrq"));
		this.finGrcIsRvwAlw.setDisabled(isReadOnly("FinanceTypeDialog_finGrcIsRvwAlw"));
		this.finGrcRvwFrq.setDisabled(isReadOnly("FinanceTypeDialog_finGrcRvwFrq"));
		this.cbfinGrcRvwFrqCode.setDisabled(isReadOnly("FinanceTypeDialog_finGrcRvwFrq"));
		this.cbfinGrcRvwFrqMth.setDisabled(isReadOnly("FinanceTypeDialog_finGrcRvwFrq"));
		this.cbfinGrcRvwFrqDays.setDisabled(isReadOnly("FinanceTypeDialog_finGrcRvwFrq"));
		this.cbfinGrcRvwRateApplFor.setDisabled(isReadOnly("FinanceTypeDialog_finGrcRvwRateApplFor"));
		//Tab 3
		this.cbFinScheduleOn.setDisabled(isReadOnly("FinanceTypeDialog_FinScheduleOn"));
		this.finPftUnChanged.setDisabled(isReadOnly("FinanceTypeDialog_FinPftUnChanged"));
		//======================
		this.finDftStmtFrq.setDisabled(isReadOnly("FinanceTypeDialog_finDftStmtFrq"));

		this.finIsOpenPftPayAcc.setDisabled(isReadOnly("FinanceTypeDialog_finIsOpenPftPayAcc"));
		this.cbfinSchdMthd.setDisabled(isReadOnly("FinanceTypeDialog_finSchdMthd"));
		this.cbfinSchCalCodeOnRvw.setDisabled(isReadOnly("FinanceTypeDialog_finSchCalCodeOnRvw"));
		this.finHistRetension.setReadonly(isReadOnly("FinanceTypeDialog_finHistRetension"));
		this.cbfinRateType.setDisabled(isReadOnly("FinanceTypeDialog_finRateType"));
		this.finBaseRate.setReadonly(isReadOnly("FinanceTypeDialog_finBaseRate"));
		this.finSplRate.setReadonly(isReadOnly("FinanceTypeDialog_finSplRate"));
		this.cbfinRvwRateApplFor.setDisabled(isReadOnly("FinanceTypeDialog_finRvwRateApplFor"));

		this.finAlwRateChangeAnyDate.setDisabled(isReadOnly("FinanceTypeDialog_finAlwRateChangeAnyDate"));
		this.finGrcAlwRateChgAnyDate.setDisabled(isReadOnly("FinanceTypeDialog_finGrcAlwRateChgAnyDate"));
		this.finIsIntCpzAtGrcEnd.setDisabled(isReadOnly("FinanceTypeDialog_finIsIntCpzAtGrcEnd"));
		this.finIntRate.setReadonly(isReadOnly("FinanceTypeDialog_finIntRate"));
		this.fInMinRate.setReadonly(isReadOnly("FinanceTypeDialog_fInMinRate"));
		this.finMaxRate.setReadonly(isReadOnly("FinanceTypeDialog_finMaxRate"));
		this.finDftIntFrq.setDisabled(isReadOnly("FinanceTypeDialog_finDftIntFrq"));
		this.finIsIntCpz.setDisabled(isReadOnly("FinanceTypeDialog_finIsIntCpz"));
		this.finCpzFrq.setDisabled(isReadOnly("FinanceTypeDialog_finCpzFrq"));
		this.finIsRvwAlw.setDisabled(isReadOnly("FinanceTypeDialog_finIsRvwAlw"));
		this.finRepayPftOnFrq.setDisabled(isReadOnly("FinanceTypeDialog_finRepayPftOnFrq"));
		this.finRvwFrq.setDisabled(isReadOnly("FinanceTypeDialog_finRvwFrq"));
		this.fInGrcMinRate.setReadonly(isReadOnly("FinanceTypeDialog_fInGrcMinRate"));
		this.finGrcMaxRate.setReadonly(isReadOnly("FinanceTypeDialog_finGrcMaxRate"));

		this.finMinTerm.setReadonly(isReadOnly("FinanceTypeDialog_finMinTerm"));
		this.finMaxTerm.setReadonly(isReadOnly("FinanceTypeDialog_finMaxTerm"));
		this.finDftTerms.setReadonly(isReadOnly("FinanceTypeDialog_finDftTerms"));
		this.finRpyFrq.setDisabled(isReadOnly("FinanceTypeDialog_finRpyFrq"));
		this.cbfinRepayMethod.setDisabled(isReadOnly("FinanceTypeDialog_fInRepayMethod"));
		this.finIsAlwPartialRpy.setDisabled(isReadOnly("FinanceTypeDialog_finIsAlwPartialRpy"));
		this.finIsAlwDifferment.setDisabled(isReadOnly("FinanceTypeDialog_finIsAlwDifferment"));
		this.finMaxDifferment.setDisabled(isReadOnly("FinanceTypeDialog_finMaxDifferment"));
		this.finIsAlwFrqDifferment.setDisabled(isReadOnly("FinanceTypeDialog_finIsAlwFrqDifferment"));
		this.finMaxFrqDifferment.setDisabled(isReadOnly("FinanceTypeDialog_finMaxFrqDifferment"));
		this.finIsAlwEarlyRpy.setDisabled(isReadOnly("FinanceTypeDialog_finIsAlwEarlyRpy"));
		this.finIsAlwEarlySettle.setDisabled(isReadOnly("FinanceTypeDialog_finIsAlwEarlySettle"));
		this.finODRpyTries.setReadonly(isReadOnly("FinanceTypeDialog_finODRpyTries"));
		this.finAEAddDsbOD.setReadonly(isReadOnly("FinanceTypeDialog_finAEAddDsbOD"));
		this.finAEAddDsbFD.setReadonly(isReadOnly("FinanceTypeDialog_finAEAddDsbFD"));
		this.finAEAddDsbFDA.setReadonly(isReadOnly("FinanceTypeDialog_finAEAddDsbFDA"));
		this.finAEAmzNorm.setReadonly(isReadOnly("FinanceTypeDialog_finAEAmzNorm"));
		this.finAEAmzSusp.setReadonly(isReadOnly("FinanceTypeDialog_finAEAmzSusp"));
		this.finAEToNoAmz.setReadonly(isReadOnly("FinanceTypeDialog_finAEToNoAmz"));
		this.finToAmz.setReadonly(isReadOnly("FinanceTypeDialog_finToAmz"));
		this.finMAmz.setReadonly(isReadOnly("FinanceTypeDialog_finMAmz"));
		this.finAERateChg.setReadonly(isReadOnly("FinanceTypeDialog_finAEIncPft"));
		this.finAERepay.setReadonly(isReadOnly("FinanceTypeDialog_finAERepay"));
		this.finAEEarlySettle.setReadonly(isReadOnly("FinanceTypeDialog_finAEEarlySettle"));
		this.finAEWriteOff.setReadonly(isReadOnly("FinanceTypeDialog_finAEWriteOff"));
		this.finAEWriteOffBK.setReadonly(isReadOnly("FinanceTypeDialog_finAEWriteOffBK"));
		this.finAEGraceEnd.setReadonly(isReadOnly("FinanceTypeDialog_finAEGraceEnd"));
		this.finSchdChange.setReadonly(isReadOnly("FinanceTypeDialog_finSchdChange"));
		this.finProvision.setReadonly(isReadOnly("FinanceTypeDialog_finProvision"));
		this.finAECapitalize.setReadonly(isReadOnly("FinanceTypeDialog_finAECapitalize"));
		this.finAEProgClaim.setReadonly(isReadOnly("FinanceTypeDialog_finAEProgClaim"));
		this.finAEMaturity.setReadonly(isReadOnly("FinanceTypeDialog_finAEMaturity"));
		this.finCollateralOvrride.setDisabled(isReadOnly("FinanceTypeDialog_finCollateralOvrride"));

		this.cbfinDftStmtFrqCode.setDisabled(isReadOnly("FinanceTypeDialog_finDftStmtFrq"));
		this.cbfinDftStmtFrqMth.setDisabled(isReadOnly("FinanceTypeDialog_finDftStmtFrq"));
		this.cbfinDftStmtFrqDays.setDisabled(isReadOnly("FinanceTypeDialog_finDftStmtFrq"));

		this.cbfinDftIntFrqCode.setDisabled(isReadOnly("FinanceTypeDialog_finDftIntFrq"));
		this.cbfinDftIntFrqMth.setDisabled(isReadOnly("FinanceTypeDialog_finDftIntFrq"));
		this.cbfinDftIntFrqDays.setDisabled(isReadOnly("FinanceTypeDialog_finDftIntFrq"));

		this.cbfinCpzFrqCode.setDisabled(isReadOnly("FinanceTypeDialog_finCpzFrq"));
		this.cbfinCpzFrqMth.setDisabled(isReadOnly("FinanceTypeDialog_finCpzFrq"));
		this.cbfinCpzFrqDays.setDisabled(isReadOnly("FinanceTypeDialog_finCpzFrq"));

		this.cbfinRvwFrqCode.setDisabled(isReadOnly("FinanceTypeDialog_finRvwFrq"));
		this.cbfinRvwFrqMth.setDisabled(isReadOnly("FinanceTypeDialog_finRvwFrq"));
		this.cbfinRvwFrqDays.setDisabled(isReadOnly("FinanceTypeDialog_finRvwFrq"));

		this.cbfinRpyFrqCode.setDisabled(isReadOnly("FinanceTypeDialog_finRpyFrq"));
		this.cbfinRpyFrqMth.setDisabled(isReadOnly("FinanceTypeDialog_finRpyFrq"));
		this.cbfinRpyFrqDays.setDisabled(isReadOnly("FinanceTypeDialog_finRpyFrq"));

		this.finFrqrepayment.setDisabled(isReadOnly("FinanceTypeDialog_finFrEqrepayment"));

		this.cbfinAssetType.setDisabled(isReadOnly("FinanceTypeDialog_finAssetType"));
		this.cbfinProductType.setDisabled(isReadOnly("FinanceTypeDialog_finAssetType"));

		this.finDeffreq.setReadonly(isReadOnly("FinanceTypeDialog_FinDeffreq"));
		this.finDefRepay.setReadonly(isReadOnly("FinanceTypeDialog_FinDefRepay"));
		this.finLatePayRule.setReadonly(isReadOnly("FinanceTypeDialog_finLatePayRule"));
		this.finDepreciationRule.setReadonly(isReadOnly("FinanceTypeDialog_finDepreciation"));

		this.finGrcMargin.setDisabled(isReadOnly("FinanceTypeDialog_FinGrcMargin"));
		this.cbFinGrcScheduleOn.setDisabled(isReadOnly("FinanceTypeDialog_FinGrcScheduleOn"));
		this.finCollateralReq.setDisabled(isReadOnly("FinanceTypeDialog_FinCollateralReq"));
		this.finInstDate.setReadonly(isReadOnly("FinanceTypeDialog_finInstDate"));
		this.cbfinDepreciationCode.setDisabled(isReadOnly("FinanceTypeDialog_FinDepreciationFrq"));
		this.cbfinDepreciationMth.setDisabled(isReadOnly("FinanceTypeDialog_FinDepreciationFrq"));
		this.cbfinDepreciationDays.setDisabled(isReadOnly("FinanceTypeDialog_FinDepreciationFrq"));
		this.finGrcAlwIndRate.setDisabled(isReadOnly("FinanceTypeDialog_FinAlwIndRate"));
		this.btnSearchFinGrcIndBaseRate.setDisabled(isReadOnly("FinanceTypeDialog_FinGrcIndRate"));
		this.finAlwIndRate.setDisabled(isReadOnly("FinanceTypeDialog_FinGrcAlwIndRate"));
		this.finIndBaseRate.setReadonly(isReadOnly("FinanceTypeDialog_FinIndRate"));
		
		this.btnNew_FinTypeAccount.setVisible(!isReadOnly("button_FinanceTypeDialog_btnNew_FinTypeAccount"));
		//Overdue Penalty Details
		this.applyODPenalty.setDisabled(isReadOnly("FinanceTypeDialog_applyODPenalty"));
		
		//Stepping Details
		this.stepFinance.setDisabled(isReadOnly("FinanceTypeDialog_stepFinance"));
		this.btnSearchStepPolicy.setDisabled(isReadOnly("FinanceTypeDialog_alwdStepPolicies"));
		this.steppingMandatory.setDisabled(isReadOnly("FinanceTypeDialog_steppingMandatory"));
		this.allowManualSteps.setDisabled(isReadOnly("FinanceTypeDialog_allowManualSteps"));
		this.dftStepPolicy.setDisabled(isReadOnly("FinanceTypeDialog_dftStepPolicy"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.financeType.isNewRecord()) {
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

	// Method for refreshing the list after successful updation
	private void refreshList() {
		logger.debug("Entering");
		getFinanceTypeListCtrl().findSearchObject();
		if (getFinanceTypeListCtrl().listBoxFinanceType != null) {
			getFinanceTypeListCtrl().listBoxFinanceType.getListModel();
		}
		logger.debug("Leaving");
	}

	/** Set the components to ReadOnly. <br> */
	public void doReadOnly() {
		logger.debug("Entering");
		this.finType.setReadonly(true);
		this.finTypeDesc.setReadonly(true);
		this.finCcy.setReadonly(true);
		this.finDivision.setReadonly(true);
		this.finAcType.setReadonly(true);
		this.pftPayAcType.setReadonly(true);
		this.btnSearchFinContingentAcType.setDisabled(true);
		this.finSuspAcType.setReadonly(true);
		this.finIsGenRef.setDisabled(true);
		this.finMaxAmount.setReadonly(true);
		this.finMinAmount.setReadonly(true);
		this.finIsOpenNewFinAc.setDisabled(true);
		this.finDftStmtFrq.setDisabled(true);
		this.finIsAlwMD.setDisabled(true);
		this.fInIsAlwGrace.setDisabled(true);
		this.finHistRetension.setReadonly(true);
		this.cbfinRateType.setDisabled(true);
		this.finBaseRate.setReadonly(true);
		this.finSplRate.setReadonly(true);
		this.cbfinRvwRateApplFor.setDisabled(true);
		this.cbfinGrcRvwRateApplFor.setDisabled(true);
		this.finAlwRateChangeAnyDate.setDisabled(true);
		this.finGrcAlwRateChgAnyDate.setDisabled(true);
		this.finIsIntCpzAtGrcEnd.setDisabled(true);
		this.finMinDownPayAmount.setDisabled(true);
		this.cbfinSchdMthd.setDisabled(true);
		this.cbfinDaysCalType.setDisabled(true);
		this.cbfinGrcRateType.setDisabled(true);
		this.cbfinSchCalCodeOnRvw.setDisabled(true);
		this.finIntRate.setReadonly(true);
		this.fInMinRate.setReadonly(true);
		this.finMaxRate.setReadonly(true);
		this.finDftIntFrq.setDisabled(true);
		this.finIsIntCpz.setDisabled(true);
		this.finCpzFrq.setDisabled(true);
		this.finIsRvwAlw.setDisabled(true);
		this.finRepayPftOnFrq.setDisabled(true);
		this.finRvwFrq.setDisabled(true);
		this.finGrcBaseRate.setReadonly(true);
		this.finGrcSplRate.setReadonly(true);
		this.finGrcIntRate.setReadonly(true);
		this.fInGrcMinRate.setReadonly(true);
		this.finGrcMaxRate.setReadonly(true);
		this.finGrcDftIntFrq.setDisabled(true);
		this.finGrcIsIntCpz.setDisabled(true);
		this.finGrcCpzFrq.setDisabled(true);
		this.finGrcIsRvwAlw.setDisabled(true);
		this.finGrcRvwFrq.setDisabled(true);
		this.finMinTerm.setReadonly(true);
		this.finMaxTerm.setReadonly(true);
		this.finDftTerms.setReadonly(true);
		this.finRpyFrq.setDisabled(true);
		this.cbfinRepayMethod.setDisabled(true);
		this.finIsAlwPartialRpy.setDisabled(true);
		this.finIsAlwDifferment.setDisabled(true);
		this.finMaxDifferment.setDisabled(true);
		this.finIsAlwFrqDifferment.setDisabled(true);
		this.finMaxFrqDifferment.setDisabled(true);
		this.finPftUnChanged.setDisabled(true);
		this.finIsAlwEarlyRpy.setDisabled(true);
		this.finIsAlwEarlySettle.setDisabled(true);
		this.finODRpyTries.setReadonly(true);
		this.finAEAddDsbOD.setReadonly(true);
		this.finAEAddDsbFD.setReadonly(true);
		this.finAEAddDsbFDA.setReadonly(true);
		this.finAEAmzNorm.setReadonly(true);
		this.finAEAmzSusp.setReadonly(true);
		this.finAEToNoAmz.setReadonly(true);
		this.finToAmz.setReadonly(true);
		this.finMAmz.setReadonly(true);
		this.finAERateChg.setReadonly(true);

		this.finAERepay.setReadonly(true);
		this.finAEEarlySettle.setReadonly(true);
		this.finAEWriteOff.setReadonly(true);
		this.finAEWriteOffBK.setReadonly(true);
		this.finAEGraceEnd.setReadonly(true);
		this.finIsActive.setDisabled(true);
		this.allowRIAInvestment.setDisabled(true);
		this.allowParllelFinance.setDisabled(true);
		this.overrideLimit.setDisabled(true);

		this.finCollateralOvrride.setDisabled(true);
		this.finCommitmentOvrride.setDisabled(true);
		this.limitRequired.setDisabled(true);

		this.cbfinDftStmtFrqCode.setDisabled(true);
		this.cbfinDftStmtFrqMth.setDisabled(true);
		this.cbfinDftStmtFrqDays.setDisabled(true);
		this.cbfinDftIntFrqCode.setDisabled(true);
		this.cbfinDftIntFrqMth.setDisabled(true);
		this.cbfinDftIntFrqDays.setDisabled(true);
		this.cbfinCpzFrqCode.setDisabled(true);
		this.cbfinCpzFrqMth.setDisabled(true);
		this.cbfinCpzFrqDays.setDisabled(true);
		this.cbfinRvwFrqCode.setDisabled(true);
		this.cbfinRvwFrqMth.setDisabled(true);
		this.cbfinRvwFrqDays.setDisabled(true);
		this.cbfinGrcDftIntFrqCode.setDisabled(true);
		this.cbfinGrcDftIntFrqMth.setDisabled(true);
		this.cbfinGrcDftIntFrqDays.setDisabled(true);
		this.cbfinGrcCpzFrqCode.setDisabled(true);
		this.cbfinGrcCpzFrqMth.setDisabled(true);
		this.cbfinGrcCpzFrqDays.setDisabled(true);
		this.cbfinGrcRvwFrqCode.setDisabled(true);
		this.cbfinGrcRvwFrqMth.setDisabled(true);
		this.cbfinGrcRvwFrqDays.setDisabled(true);
		this.cbfinRpyFrqCode.setDisabled(true);
		this.cbfinRpyFrqMth.setDisabled(true);
		this.cbfinRpyFrqDays.setDisabled(true);
		
		//Overdue Penalty Details
		this.applyODPenalty.setDisabled(true);
		this.oDIncGrcDays.setDisabled(true);
		this.oDChargeType.setDisabled(true);
		this.oDGraceDays.setReadonly(true);
		this.oDChargeCalOn.setDisabled(true);
		this.oDChargeAmtOrPerc.setDisabled(true);
		this.oDAllowWaiver.setDisabled(true);
		this.oDMaxWaiverPerc.setDisabled(true);
		
		// Stepping Details
		this.stepFinance.setDisabled(true);
		this.steppingMandatory.setDisabled(true);
		this.btnSearchStepPolicy.setDisabled(true);
		this.allowManualSteps.setDisabled(true);
		this.dftStepPolicy.setDisabled(true);
		
		this.btnNew_FinTypeAccount.setVisible(false);
		
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

	/** Clears the components values. <br> */
	public void doClear() {
		logger.debug("Entering");

		// remove validation, if there are a save before
		this.finType.setValue("");
		this.finTypeDesc.setValue("");
		this.finCcy.setValue("");
		this.finCcy.setDescription("");
		this.finDivision.setValue("");
		this.finDivision.setDescription("");
		this.finAcType.setValue("");
		this.finAcType.setDescription(""); 
		this.pftPayAcType.setValue("");
		this.pftPayAcType.setDescription("");
		this.finContingentAcType.setValue("");
		this.lovDescFinContingentAcTypeName.setValue("");
		this.finSuspAcType.setValue("");
		this.finSuspAcType.setDescription("");
		this.finBankContingentAcType.setValue("");
		this.lovDescFinBankContingentAcTypeName.setValue("");
		this.finProvisionAcType.setValue("");
		this.finProvisionAcType.setDescription("");
		this.finIsGenRef.setChecked(false);
		this.finMaxAmount.setValue("");
		this.finMinAmount.setValue("");
		this.finDftStmtFrq.setValue("");
		this.finIsAlwMD.setChecked(false);
		this.finIsOpenPftPayAcc.setChecked(false);
		this.fInIsAlwGrace.setChecked(false);
		this.finHistRetension.setText("");
		this.finBaseRate.setValue("");
		this.finBaseRate.setDescription("");
		this.finAlwRateChangeAnyDate.setChecked(false);
		this.finGrcAlwRateChgAnyDate.setChecked(false);
		this.finIsIntCpzAtGrcEnd.setChecked(false);
		this.finMinDownPayAmount.setValue("");
		this.cbfinSchdMthd.setSelectedIndex(0);
		this.cbfinDaysCalType.setSelectedIndex(0);
		this.cbfinGrcRateType.setSelectedIndex(0);
		this.cbfinRateType.setSelectedIndex(0);
		this.cbfinSchCalCodeOnRvw.setSelectedIndex(0);
		this.finSplRate.setValue("");
		this.finIntRate.setValue("");
		this.fInMinRate.setValue("");
		this.finMaxRate.setValue("");
		this.finDftIntFrq.setValue("");
		this.finIsIntCpz.setChecked(false);
		this.finCpzFrq.setValue("");
		this.finIsRvwAlw.setChecked(false);
		this.finRepayPftOnFrq.setChecked(false);
		this.finRvwFrq.setValue("");
		this.finGrcBaseRate.setValue("");
		this.finGrcBaseRate.setDescription("");
		this.finGrcSplRate.setValue("");
		this.finGrcSplRate.setDescription("");
		this.finGrcIntRate.setValue("");
		this.fInGrcMinRate.setValue("");
		this.finGrcMaxRate.setValue("");
		this.finGrcDftIntFrq.setValue("");
		this.finGrcIsIntCpz.setChecked(false);
		this.finGrcCpzFrq.setValue("");
		this.finGrcIsRvwAlw.setChecked(false);
		this.finGrcRvwFrq.setValue("");
		this.finMinTerm.setText("");
		this.finMaxTerm.setText("");
		this.finDftTerms.setText("");
		this.finRpyFrq.setValue("");
		this.cbfinRepayMethod.setSelectedIndex(0);
		this.finIsAlwDifferment.setChecked(false);
		this.finMaxDifferment.setValue(0);
		this.finIsAlwFrqDifferment.setChecked(false);
		this.finPftUnChanged.setChecked(false);
		this.finMaxFrqDifferment.setValue(0);
		this.finIsAlwEarlyRpy.setChecked(false);
		this.finIsAlwEarlySettle.setChecked(false);
		this.finODRpyTries.setText("");
		this.finAEAddDsbOD.setValue("");
		this.finAEAddDsbOD.setDescription("");
		this.finAEAddDsbFD.setValue("");
		this.finAEAddDsbFD.setDescription("");
		this.finAEAddDsbFDA.setValue("");
		this.finAEAddDsbFDA.setDescription("");
		this.finAEAmzNorm.setValue("");
		this.finAEAmzNorm.setDescription("");
		this.finAEAmzSusp.setValue("");
		this.finAEAmzSusp.setDescription("");
		this.finAEToNoAmz.setValue("");
		this.finAEToNoAmz.setDescription("");
		this.finToAmz.setValue("");
		this.finToAmz.setDescription("");
		this.finMAmz.setValue("");
		this.finMAmz.setDescription("");
		this.finAERateChg.setValue("");
		this.finAERateChg.setDescription("");
		this.finAERepay.setValue("");
		this.finAERepay.setDescription("");
		this.finAEEarlyPay.setValue("");
		this.lovDescFinAEEarlyPayName.setValue("");
		this.finAEEarlySettle.setValue("");
		this.finAEEarlySettle.setDescription("");
		this.finAEWriteOff.setValue("");
		this.finAEWriteOffBK.setValue("");
		this.finAEGraceEnd.setValue("");
		this.finProvision.setValue("");
		this.finInstDate.setValue("");
		this.finSchdChange.setValue("");
		this.finAECapitalize.setValue("");
		this.finAEProgClaim.setValue("");
		this.finAEMaturity.setValue("");
		this.finAEWriteOff.setDescription("");
		this.finAEWriteOffBK.setDescription("");
		this.finAEGraceEnd.setDescription("");
		
		//Overdue Penalty Details
		this.applyODPenalty.setChecked(false);
		this.oDIncGrcDays.setChecked(false);
		this.oDChargeType.setSelectedIndex(0);
		this.oDGraceDays.setValue(0);
		this.oDChargeCalOn.setSelectedIndex(0);
		this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
		this.oDAllowWaiver.setChecked(false);
		this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
		this.oDChargeAmtOrPerc.setDisabled(true);
		this.oDMaxWaiverPerc.setDisabled(true);
		
		// Stepping Details
		this.stepFinance.setChecked(false);
		this.lovDescStepPolicyCodename.setValue("");
		this.steppingMandatory.setChecked(false);
		this.allowManualSteps.setChecked(false);
		
		logger.debug("Leaving");

	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		
		final FinanceType aFinanceType = new FinanceType("");
		BeanUtils.copyProperties(getFinanceType(), aFinanceType);
		boolean isNew = false;
		if ("Submit".equalsIgnoreCase(userAction.getSelectedItem().getLabel())) {
			validate = true;// Stop validations in save mode
		} else {
			validate = false;// Stop validations in save mode
		}
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doClearMessages();
		doSetValidation();
		// fill the FinanceType object with the components data
		doWriteComponentsToBean(aFinanceType);

		isNew = aFinanceType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aFinanceType.getRecordType()).equals("")) {
				aFinanceType.setVersion(aFinanceType.getVersion() + 1);
				if (isNew) {
					aFinanceType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinanceType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceType.setNewRecord(true);
				}
			}
		} else {
			aFinanceType.setVersion(aFinanceType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aFinanceType, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_FinanceTypeDialog, "FinanceTypeDialog");
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
	 * @param aFinanceType
	 *            (FinanceType)
	 * @param tranType
	 *            (String)
	 * @return boolean
	 */
	private boolean doProcess(FinanceType aFinanceType, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aFinanceType.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aFinanceType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinanceType.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			aFinanceType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinanceType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aFinanceType);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aFinanceType))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
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

			aFinanceType.setTaskId(taskId);
			aFinanceType.setNextTaskId(nextTaskId);
			aFinanceType.setRoleCode(getRole());
			aFinanceType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinanceType, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aFinanceType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinanceType, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aFinanceType, tranType);
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
	 * @param method
	 *            (String)
	 * @return boolean
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		FinanceType afinanceType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getFinanceTypeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getFinanceTypeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getFinanceTypeService().doApprove(auditHeader);
						if (afinanceType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getFinanceTypeService().doReject(auditHeader);
						if (afinanceType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {

						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinanceTypeDialog, auditHeader);
						return processCompleted;

					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_FinanceTypeDialog, auditHeader);
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

	/**
	 * To get the currency LOV List From RMTCurrencies Table And Amount is formatted based on the currency
	 */
	public void onFulfill$finCcy(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finCcy.getObject();
		if (dataObject instanceof String) {
			this.finCcy.setValue(dataObject.toString());
			this.finCcy.setDescription("");
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				this.finCcy.setValue(details.getCcyCode());
				this.finCcy.setDescription(details.getCcyDesc());
				fillComboBox(this.cbfinDaysCalType, details.getCcyDrRateBasisCode(), pftDays, "");
				// To Format Amount based on the currency
				getFinanceType().setLovDescFinFormetter(details.getCcyEditField());
				this.finMaxAmount.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceType().getLovDescFinFormetter()));
				this.finMinAmount.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceType().getLovDescFinFormetter()));
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onFulfill$finDivision(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finDivision.getObject();
		if (dataObject instanceof String) {
			this.finDivision.setValue(dataObject.toString());
			this.finDivision.setDescription("");
		} else {
			DivisionDetail details = (DivisionDetail) dataObject;
			if (details != null) {
				this.finDivision.setValue(details.getDivisionCode());
				this.finDivision.setDescription(details.getDivisionCodeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountType LOV List From RMTAccountTypes Table filter is applied to get non internal account and it's
	 * purpose is movement
	 */

	public void onFulfill$finAcType(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = finAcType.getObject();
		if (dataObject instanceof String) {
			this.finAcType.setValue(dataObject.toString());
			this.finAcType.setDescription("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				this.finAcType.setValue(details.getAcType());
				this.finAcType.setDescription(details.getAcTypeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountType LOV List From RMTAccountTypes Table filter is applied to get non internal account and it's
	 * purpose is movement
	 */

	public void onFulfill$pftPayAcType(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = pftPayAcType.getObject();
		if (dataObject instanceof String) {
			this.pftPayAcType.setValue(dataObject.toString());
			this.pftPayAcType.setDescription("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				this.pftPayAcType.setValue(details.getAcType());
				this.pftPayAcType.setDescription(details.getAcTypeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountType LOV List From RMTAccountTypes Table filter is applied to get only an Customer account and
	 * it's purpose is movement and it is a Contingent account
	 */

	public void onClick$btnSearchFinContingentAcType(Event event) {
		logger.debug("Entering" + event.toString());
		Filter[] filters = new Filter[2];
		filters[0] = new Filter("AcPurpose", "C", Filter.OP_EQUAL);
		filters[1] = new Filter("internalAc", "0", Filter.OP_EQUAL);

		
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceTypeDialog, "AccountType", filters);
		if (dataObject instanceof String) {
			this.finContingentAcType.setValue(dataObject.toString());
			this.lovDescFinContingentAcTypeName.setValue("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				this.finContingentAcType.setValue(details.getAcType());
				this.lovDescFinContingentAcTypeName.setValue(details.getAcType() + "-" + details.getAcTypeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * To get the AccountType LOV List From RMTAccountTypes Table filter is applied to get only an Internal account and
	 * it's purpose is movement and it is a Suspense account
	 */
	public void onFulfill$finSuspAcType(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finSuspAcType.getObject();
		if (dataObject instanceof String) {
			this.finSuspAcType.setValue(dataObject.toString());
			this.finSuspAcType.setDescription("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				this.finSuspAcType.setValue(details.getAcType());
				this.finSuspAcType.setDescription(details.getAcTypeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountType LOV List From RMTAccountTypes Table filter is applied to get only an internal account and
	 * it's purpose is movement and it is a Contingent account
	 */
	public void onClick$btnSearchFinBankContingentAcType(Event event) {
		logger.debug("Entering" + event.toString());
		Filter[] filters = new Filter[2];
		filters[0] = new Filter("AcPurpose", "C", Filter.OP_EQUAL);
		filters[1] = new Filter("internalAc", "1", Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceTypeDialog, "AccountType", filters);
		if (dataObject instanceof String) {
			this.finBankContingentAcType.setValue(dataObject.toString());
			this.lovDescFinBankContingentAcTypeName.setValue("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				this.finBankContingentAcType.setValue(details.getAcType());
				this.lovDescFinBankContingentAcTypeName.setValue(details.getAcType() + "-" + details.getAcTypeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountType LOV List From RMTAccountTypes Table filter is applied to get only an internal account and
	 * it's purpose is movement and it is a Provision account
	 */
	public void onFulfill$finProvisionAcType(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finProvisionAcType.getObject();
		if (dataObject instanceof String) {
			this.finProvisionAcType.setValue(dataObject.toString());
			this.finProvisionAcType.setDescription("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				this.finProvisionAcType.setValue(details.getAcType());
				this.finProvisionAcType.setDescription(details.getAcTypeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/** To get the BaseRateCode LOV List From RMTBaseRateCodes Table */
	public void onFulfill$finBaseRate(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finBaseRate.getObject();
		if (dataObject instanceof String) {
			this.finBaseRate.setValue(dataObject.toString());
			this.finBaseRate.setDescription("");
			this.labe_EffectiveRate.setValue("");
		} else {
			BaseRateCode details = (BaseRateCode) dataObject;
			if (details != null) {
				this.finBaseRate.setValue(details.getBRType());
				this.finBaseRate.setDescription(details.getBRTypeDesc());
			}
			this.labe_EffectiveRate.setValue(String.valueOf(rates(this.finBaseRate.getValue(), this.finSplRate.getValue(), getDCBValue(this.finMargin))));
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the SplRateCode LOV List From RMTSplRateCodes Table
	 * 
	 * @throws ParseException
	 */

	public void onFulfill$finSplRate(Event event) throws ParseException {
		logger.debug("Entering" + event.toString());
		Object dataObject = finSplRate.getObject();
		if (dataObject instanceof String) {
			this.finSplRate.setValue(dataObject.toString());
			this.finSplRate.setDescription("");
			this.labe_EffectiveRate.setValue("");
		} else {
			SplRateCode details = (SplRateCode) dataObject;
			if (details != null) {
				this.finSplRate.setValue(details.getSRType());
				this.finSplRate.setDescription(details.getSRTypeDesc());
			}

		}
		if (!this.finBaseRate.getDescription().equals("")) {
			this.labe_EffectiveRate.setValue(String.valueOf(rates(this.finBaseRate.getValue(), this.finSplRate.getValue(), getDCBValue(this.finMargin))));

		}
		logger.debug("Leaving" + event.toString());
	}

	/** To get the BaseRateCode LOV List From RMTBaseRateCodes Table */
	public void onFulfill$finGrcBaseRate(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finGrcBaseRate.getObject();
		if (dataObject instanceof String) {
			this.finGrcBaseRate.setValue(dataObject.toString());
			this.finGrcBaseRate.setDescription("");
			this.labe_GrcEffectiveRate.setValue("");
		} else {
			BaseRateCode details = (BaseRateCode) dataObject;
			if (details != null) {
				this.finGrcBaseRate.setValue(details.getBRType());
				this.finGrcBaseRate.setDescription(details.getBRTypeDesc());
			}
			this.labe_GrcEffectiveRate.setValue(String.valueOf(rates(this.finGrcBaseRate.getValue(), this.finGrcSplRate.getValue(), getDCBValue(this.finGrcMargin))));
		}

		logger.debug("Leaving" + event.toString());
	}

	/** To get the SplRateCode LOV List From RMTSplRateCodes Table */
	public void onFulfill$finGrcSplRate(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finGrcSplRate.getObject();
		if (dataObject instanceof String) {
			this.finGrcSplRate.setValue(dataObject.toString());
			this.finGrcSplRate.setDescription("");
			this.labe_GrcEffectiveRate.setValue("");
		} else {
			SplRateCode details = (SplRateCode) dataObject;
			if (details != null) {
				this.finGrcSplRate.setValue(details.getSRType());
				this.finGrcSplRate.setDescription(details.getSRTypeDesc());

			}
		}
		if (!this.finGrcBaseRate.getDescription().equals("")) {
			this.labe_GrcEffectiveRate.setValue(String.valueOf(rates(this.finGrcBaseRate.getValue(), this.finGrcSplRate.getValue(), getDCBValue(this.finGrcMargin))));
		}
		logger.debug("Leaving" + event.toString());
	}

	private Filter[] getFiltersByCheckingRIA(String property, Object value, int operator){
		Filter[] filter =  new Filter[2];
		if (this.allowRIAInvestment.isChecked()) {
			filter[0] = new Filter(property,value,operator);
			filter[1] = new Filter("EntryByInvestment", "1", Filter.OP_EQUAL);
        }else{
        	filter[0] = new Filter(property,value,operator);
			filter[1] = new Filter("EntryByInvestment", "1", Filter.OP_NOT_EQUAL);
        }
		return filter;
	}
	
	
	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where EventCode=ADDDBSP
	 */

	public void onFulfill$finAEAddDsbOD(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finAEAddDsbOD.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("ADDDBSP")) {
				accSet = getFinanceType().getLovDescAERule().get("ADDDBSP");
				this.finAEAddDsbOD.setValue(accSet.getStringaERuleId());
				this.finAEAddDsbOD.setDescription(accSet.getAccountSetCode() + "-" +accSet.getAccountSetCodeName());
			} else {
				this.finAEAddDsbOD.setValue(null);
				this.finAEAddDsbOD.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finAEAddDsbOD.setValue(String.valueOf(details.getAccountSetid()));
				this.finAEAddDsbOD.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		
		doCheckMandFinAEAddDisbFDA();
		
		logger.debug("Leaving" + event.toString());
	}
	

	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where EventCode=ADDDBSF
	 */
	public void onFulfill$finAEAddDsbFD(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finAEAddDsbFD.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("ADDDBSF")) {
				accSet = getFinanceType().getLovDescAERule().get("ADDDBSF");
				this.finAEAddDsbFD.setValue(accSet.getStringaERuleId());
				this.finAEAddDsbFD.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finAEAddDsbFD.setValue(null);
				this.finAEAddDsbFD.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finAEAddDsbFD.setValue(String.valueOf(details.getAccountSetid()));
				this.finAEAddDsbFD.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		
		doCheckMandFinAEAddDisbFDA();
		logger.debug("Leaving" + event.toString());
	}
	
	private void doCheckMandFinAEAddDisbFDA(){
		if(!StringUtils.trimToEmpty(this.finAEAddDsbOD.getValue()).equals("") && 
				!StringUtils.trimToEmpty(this.finAEAddDsbFD.getValue()).equals("")){
			if(!this.finAEAddDsbOD.getValue().equals(this.finAEAddDsbFD.getValue())){
				this.finAEAddDsbFDA.setMandatoryStyle(true);
			}else{
				this.finAEAddDsbFDA.setMandatoryStyle(false);
			}
		}
	}

	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where EventCode=ADDDBSN
	 */

	public void onFulfill$finAEAddDsbFDA(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finAEAddDsbFDA.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("ADDDBSN")) {
				accSet = getFinanceType().getLovDescAERule().get("ADDDBSN");
				this.finAEAddDsbFDA.setValue(accSet.getStringaERuleId());
				this.finAEAddDsbFDA.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finAEAddDsbFDA.setValue(null);
				this.finAEAddDsbFDA.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finAEAddDsbFDA.setValue(String.valueOf(details.getAccountSetid()));
				this.finAEAddDsbFDA.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where EventCode=AMZ
	 */

	public void onFulfill$finAEAmzNorm(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finAEAmzNorm.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("AMZ")) {
				accSet = getFinanceType().getLovDescAERule().get("AMZ");
				this.finAEAmzNorm.setValue(accSet.getStringaERuleId());
				this.finAEAmzNorm.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finAEAmzNorm.setValue(null);
				this.finAEAmzNorm.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finAEAmzNorm.setValue(String.valueOf(details.getAccountSetid()));
				this.finAEAmzNorm.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where EventCode=AMZSUSP
	 */

	public void onFulfill$finAEAmzSusp(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = finAEAmzSusp.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("AMZSUSP")) {
				accSet = getFinanceType().getLovDescAERule().get("AMZSUSP");
				this.finAEAmzSusp.setValue(accSet.getStringaERuleId());
				this.finAEAmzSusp.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finAEAmzSusp.setValue(null);
				this.finAEAmzSusp.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finAEAmzSusp.setValue(String.valueOf(details.getAccountSetid()));
				this.finAEAmzSusp.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where
	 * EventCode=M_NONAMZ
	 */

	public void onFulfill$finAEToNoAmz(Event event) {
		logger.debug("Entering" + event.toString());
		logger.debug(event.toString());

		Object dataObject = finAEToNoAmz.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("M_NONAMZ")) {
				accSet = getFinanceType().getLovDescAERule().get("M_NONAMZ");
				this.finAEToNoAmz.setValue(accSet.getStringaERuleId());
				this.finAEToNoAmz.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finAEToNoAmz.setValue(null);
				this.finAEToNoAmz.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finAEToNoAmz.setValue(String.valueOf(details.getAccountSetid()));
				this.finAEToNoAmz.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where EventCode=M_AMZ
	 */

	public void onFulfill$finToAmz(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = finToAmz.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("M_AMZ")) {
				accSet = getFinanceType().getLovDescAERule().get("M_AMZ");
				this.finToAmz.setValue(accSet.getStringaERuleId());
				this.finToAmz.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finToAmz.setValue(null);
				this.finToAmz.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finToAmz.setValue(String.valueOf(details.getAccountSetid()));
				this.finToAmz.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where EventCode=M_AMZ
	 */
	
	public void onFulfill$finMAmz(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finMAmz.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("AMZ_MON")) {
				accSet = getFinanceType().getLovDescAERule().get("AMZ_MON");
				this.finMAmz.setValue(accSet.getStringaERuleId());
				this.finMAmz.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finMAmz.setValue(null);
				this.finMAmz.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finMAmz.setValue(String.valueOf(details.getAccountSetid()));
				this.finMAmz.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where EventCode=INCPFT
	 */
	public void onFulfill$finAERateChg(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finAERateChg.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("RATCHG")) {
				accSet = getFinanceType().getLovDescAERule().get("RATCHG");
				this.finAERateChg.setValue(accSet.getStringaERuleId());
				this.finAERateChg.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finAERateChg.setValue(null);
				this.finAERateChg.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finAERateChg.setValue(String.valueOf(details.getAccountSetid()));
				this.finAERateChg.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where EventCode=REPAY
	 */

	public void onFulfill$finAERepay(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finAERepay.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("REPAY")) {
				accSet = getFinanceType().getLovDescAERule().get("REPAY");
				this.finAERepay.setValue(accSet.getStringaERuleId());
				this.finAERepay.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finAERepay.setValue(null);
				this.finAERepay.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finAERepay.setValue(String.valueOf(details.getAccountSetid()));
				this.finAERepay.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where EventCode=ADDDBSP
	 */

	public void onFulfill$finLatePayRule(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finLatePayRule.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("LATEPAY")) {
				accSet = getFinanceType().getLovDescAERule().get("LATEPAY");
				this.finLatePayRule.setValue(accSet.getStringaERuleId());
				this.finLatePayRule.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finLatePayRule.setValue(null);
				this.finLatePayRule.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finLatePayRule.setValue(String.valueOf(details.getAccountSetid()));
				this.finLatePayRule.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where
	 * EventCode=EARLYSTL
	 */

	public void onFulfill$finAEEarlySettle(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finAEEarlySettle.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("EARLYSTL")) {
				accSet = getFinanceType().getLovDescAERule().get("EARLYSTL");
				this.finAEEarlySettle.setValue(accSet.getStringaERuleId());
				this.finAEEarlySettle.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finAEEarlySettle.setValue(null);
				this.finAEEarlySettle.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finAEEarlySettle.setValue(String.valueOf(details.getAccountSetid()));
				this.finAEEarlySettle.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where
	 * EventCode=WRITEOFF
	 */

	public void onFulfill$finAEWriteOff(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finAEWriteOff.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("WRITEOFF")) {
				accSet = getFinanceType().getLovDescAERule().get("WRITEOFF");
				this.finAEWriteOff.setValue(accSet.getStringaERuleId());
				this.finAEWriteOff.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finAEWriteOff.setValue(null);
				this.finAEWriteOff.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finAEWriteOff.setValue(String.valueOf(details.getAccountSetid()));
				this.finAEWriteOff.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onFulfill$finAEWriteOffBK(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finAEWriteOffBK.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("WRITEBK")) {
				accSet = getFinanceType().getLovDescAERule().get("WRITEBK");
				this.finAEWriteOffBK.setValue(accSet.getStringaERuleId());
				this.finAEWriteOffBK.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finAEWriteOffBK.setValue(null);
				this.finAEWriteOffBK.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finAEWriteOffBK.setValue(String.valueOf(details.getAccountSetid()));
				this.finAEWriteOffBK.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onFulfill$finAEGraceEnd(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finAEGraceEnd.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("GRACEEND")) {
				accSet = getFinanceType().getLovDescAERule().get("GRACEEND");
				this.finAEGraceEnd.setValue(accSet.getStringaERuleId());
				this.finAEGraceEnd.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finAEGraceEnd.setValue(null);
				this.finAEGraceEnd.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finAEGraceEnd.setValue(String.valueOf(details.getAccountSetid()));
				this.finAEGraceEnd.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where
	 * EventCode=SCDCHG
	 */
	public void onFulfill$finSchdChange(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finSchdChange.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("SCDCHG")) {
				accSet = getFinanceType().getLovDescAERule().get("SCDCHG");
				this.finSchdChange.setValue(accSet.getStringaERuleId());
				this.finSchdChange.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finSchdChange.setValue(null);
				this.finSchdChange.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finSchdChange.setValue(String.valueOf(details.getAccountSetid()));
				this.finSchdChange.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where
	 * EventCode=
	 */
	public void onFulfill$finProvision(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finProvision.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("SCDCHG")) {
				accSet = getFinanceType().getLovDescAERule().get("SCDCHG");
				this.finProvision.setValue(accSet.getStringaERuleId());
				this.finProvision.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finProvision.setValue(null);
				this.finProvision.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finProvision.setValue(String.valueOf(details.getAccountSetid()));
				this.finProvision.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$finAECapitalize(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finAECapitalize.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("COMPOUND")) {
				accSet = getFinanceType().getLovDescAERule().get("COMPOUND");
				this.finAECapitalize.setValue(accSet.getStringaERuleId());
				this.finAECapitalize.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finAECapitalize.setValue(null);
				this.finAECapitalize.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finAECapitalize.setValue(String.valueOf(details.getAccountSetid()));
				this.finAECapitalize.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$finAEProgClaim(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finAEProgClaim.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("PRGCLAIM")) {
				accSet = getFinanceType().getLovDescAERule().get("PRGCLAIM");
				this.finAEProgClaim.setValue(accSet.getStringaERuleId());
				this.finAEProgClaim.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finAEProgClaim.setValue(null);
				this.finAEProgClaim.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finAEProgClaim.setValue(String.valueOf(details.getAccountSetid()));
				this.finAEProgClaim.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onFulfill$finAEMaturity(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finAEMaturity.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("MATURITY")) {
				accSet = getFinanceType().getLovDescAERule().get("MATURITY");
				this.finAEMaturity.setValue(accSet.getStringaERuleId());
				this.finAEMaturity.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finAEMaturity.setValue(null);
				this.finAEMaturity.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finAEMaturity.setValue(String.valueOf(details.getAccountSetid()));
				this.finAEMaturity.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$finDepreciationRule(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finDepreciationRule.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("DPRCIATE")) {
				accSet = getFinanceType().getLovDescAERule().get("DPRCIATE");
				this.finDepreciationRule.setValue(accSet.getStringaERuleId());
				this.finDepreciationRule.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finDepreciationRule.setValue(null);
				this.finDepreciationRule.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finDepreciationRule.setValue(String.valueOf(details.getAccountSetid()));
				this.finDepreciationRule.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where
	 * EventCode=PROVSN
	 */

	public void onFulfill$finDeffreq(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finDeffreq.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("DEFFRQ")) {
				accSet = getFinanceType().getLovDescAERule().get("DEFFRQ");
				this.finDeffreq.setValue(accSet.getStringaERuleId());
				this.finDeffreq.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finDeffreq.setValue(null);
				this.finDeffreq.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finDeffreq.setValue(String.valueOf(details.getAccountSetid()));
				this.finDeffreq.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where
	 * EventCode=DEFRPY
	 */

	public void onFulfill$finDefRepay(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finDefRepay.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("DEFRPY")) {
				accSet = getFinanceType().getLovDescAERule().get("DEFRPY");
				this.finDefRepay.setValue(accSet.getStringaERuleId());
				this.finDefRepay.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finDefRepay.setValue(null);
				this.finDefRepay.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finDefRepay.setValue(String.valueOf(details.getAccountSetid()));
				this.finDefRepay.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where
	 * EventCode=INSTDATE
	 */

	public void onFulfill$finInstDate(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finInstDate.getObject();
		if (dataObject instanceof String) {
			if (getFinanceType().getLovDescAERule().containsKey("INSTDATE")) {
				accSet = getFinanceType().getLovDescAERule().get("INSTDATE");
				this.finInstDate.setValue(accSet.getStringaERuleId());
				this.finInstDate.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
			} else {
				this.finInstDate.setValue(null);
				this.finInstDate.setDescription("");
			}
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finInstDate.setValue(String.valueOf(details.getAccountSetid()));
				this.finInstDate.setDescription(details.getAccountSetCode() + "-" + details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/** To get the BaseRateCode LOV List From RMTBaseRateCodes Table */
	public void onClick$btnSearchFinGrcIndBaseRate(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceTypeDialog, "BaseRateCode");
		if (dataObject instanceof String) {
			this.finGrcIndBaseRate.setValue(dataObject.toString());
			this.lovDescFinGrcIndBaseRateName.setValue("");
		} else {
			BaseRateCode details = (BaseRateCode) dataObject;
			if (details != null) {
				this.finGrcIndBaseRate.setValue(details.getBRType());
				this.lovDescFinGrcIndBaseRateName.setValue(details.getBRType() + "-" + details.getBRTypeDesc());
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/** To get the BaseRateCode LOV List From RMTBaseRateCodes Table */
	public void onFulfill$finIndBaseRate(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finIndBaseRate.getObject();
		if (dataObject instanceof String) {
			this.finIndBaseRate.setValue(dataObject.toString());
			this.finIndBaseRate.setDescription("");
		} else {
			BaseRateCode details = (BaseRateCode) dataObject;
			if (details != null) {
				this.finIndBaseRate.setValue(details.getBRType());
				this.finIndBaseRate.setDescription(details.getBRTypeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	//	++++++++++++++++++++++++++++++
	//	++++++++++ Tab 1++++++++++++
	//	+++++++++++++++++++++++++++++

	public void onCheck$fInIsAlwGrace(Event event) {
		logger.debug("Entering" + event.toString());
		dodisableGracePeriod();
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$cbfinProductType(Event event) {
		if (this.cbfinProductType.getSelectedItem() != null) {
			doFillAssestType(this.cbfinAssetType, "", cbfinProductType.getSelectedItem().getValue().toString());
			doCheckRIA(cbfinProductType.getSelectedItem().getValue().toString());
			//doCheckFinAEProgClaim(cbfinProductType.getSelectedItem().getValue().toString());
			doCheckFinAEMaturity(cbfinProductType.getSelectedItem().getValue().toString());
		}
	}

	public void onCheck$finDepreciationReq() {
		doDisableDepreciationDFrq(this.finDepreciationReq.isChecked(), isReadOnly("FinanceTypeDialog_FinDepreciationFrq"));
		this.cbfinDepreciationCode.setSelectedIndex(0);
		this.cbfinDepreciationMth.setSelectedIndex(0);
		this.cbfinDepreciationDays.setSelectedIndex(0);
		this.finDepreciationFrq.setValue("");

		if (getFinanceType().getLovDescAERule().containsKey("DPRCIATE")) {
			accSet = getFinanceType().getLovDescAERule().get("DPRCIATE");
			this.finDepreciationRule.setValue(accSet.getStringaERuleId());
			this.finDepreciationRule.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
		} else {
			this.finDepreciationRule.setValue("");
			this.finDepreciationRule.setDescription("");
		}
	}

	public void onCheck$finIsDwPayRequired(Event event) {
		logger.debug("Entering" + event.toString());
		this.finMinDownPayAmount.setValue(BigDecimal.ZERO);
		checkFinisDownPayreq();
		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$finCommitmentReq(Event event) {
		logger.debug("Entering");
		doCheckBoxChecked(this.finCommitmentReq.isChecked(), this.finCommitmentOvrride);
		logger.debug("Leaving");
	}

	public void onCheck$limitRequired(Event event) {
		logger.debug("Entering");
		doCheckBoxChecked(this.limitRequired.isChecked(), this.overrideLimit);
		logger.debug("Leaving");
	}

	private void checkFinisDownPayreq() {
		logger.debug("Entering");

		if (this.finIsDwPayRequired.isChecked()) {
			this.finMinDownPayAmount.setDisabled(isReadOnly("FinanceTypeDialog_finIsDwPayRequired"));
			this.space_FinMinDownPayAmount.setStyle("background-color:white");
		} else {
			this.finMinDownPayAmount.clearErrorMessage();
			this.finMinDownPayAmount.setConstraint("");
			this.finMinDownPayAmount.setValue(BigDecimal.ZERO);
			this.finMinDownPayAmount.setDisabled(true);
			this.space_FinMinDownPayAmount.setStyle("background-color:white");
		}
		logger.debug("Leaving");
	}

	/**
	 * To disable Grace period tab Used twice in the page onCreatedWindow and onCheck Events. a boolean if condition is
	 * applied on doSetValidations and doWriteComponentstoBean to Stop validation when Disabled
	 */
	private void dodisableGracePeriod() {
		logger.debug("Leaving ");
		if (this.fInIsAlwGrace.isChecked()) {
			this.cbfinGrcRateType.setSelectedIndex(this.oldVar_finGrcRateType);
			this.finGrcBaseRate.setValue(this.oldVar_finGrcBaseRate);
			this.finGrcSplRate.setValue(this.oldVar_finGrcSplRate);
			this.finGrcIntRate.setValue(this.oldVar_finGrcIntRate);
			this.fInGrcMinRate.setValue(this.oldVar_fInGrcMinRate);
			this.finGrcMaxRate.setValue(this.oldVar_finGrcMaxRate);
			this.gracePeriod.setDisabled(false);
			this.finIsAlwGrcRepay.setChecked(this.oldVar_finIsAlwGrcRepay);
			//this.gracePeriod.setVisible(true);
		} else {
			this.cbfinGrcRateType.setSelectedIndex(0);
			this.finGrcBaseRate.setValue(null);
			this.finGrcBaseRate.setDescription("");
			this.finGrcSplRate.setValue(null);
			this.finGrcSplRate.setDescription("");
			this.finGrcIntRate.setValue("0");
			this.fInGrcMinRate.setValue("0");
			this.finGrcMaxRate.setValue("0");
			this.finIsAlwGrcRepay.setChecked(false);
			this.finGrcSchdMthd.setSelectedIndex(0);
			this.gracePeriod.setDisabled(true);
			//this.gracePeriod.setVisible(false);
		}
		logger.debug("Leaving ");
	}

	private void doFillProductType(Combobox codeCombobox, String value) {
		logger.debug("Entering");
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<Product> searchObject = new JdbcSearchObject<Product>(Product.class);
		searchObject.addSort("ProductCode", false);
		List<Product> appList = pagedListService.getBySearchObject(searchObject);
		codeCombobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		codeCombobox.appendChild(comboitem);
		codeCombobox.setSelectedItem(comboitem);
		for (Product product : appList) {
			comboitem = new Comboitem();
			comboitem.setValue(product.getProductCode());
			comboitem.setLabel(product.getProductDesc());
			codeCombobox.appendChild(comboitem);
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(product.getProductCode()))) {
				codeCombobox.setSelectedItem(comboitem);
			}
		}
		logger.debug("Leaving");

	}

	private void doFillAssestType(Combobox codeCombobox, String value, String product) {
		logger.debug("Entering");
		JdbcSearchObject<ProductAsset> searchObject = new JdbcSearchObject<ProductAsset>(ProductAsset.class);
		searchObject.addFilter(new Filter("ProductCode", product, Filter.OP_EQUAL));
		searchObject.addSort("AssetCode", false);
		List<ProductAsset> appList = getPagedListService().getBySearchObject(searchObject);
		codeCombobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		codeCombobox.appendChild(comboitem);
		codeCombobox.setSelectedItem(comboitem);
		for (ProductAsset productAsset : appList) {
			comboitem = new Comboitem();
			comboitem.setValue(productAsset.getAssetID());
			comboitem.setLabel(productAsset.getAssetCode()+"-"+productAsset.getAssetDesc());
			codeCombobox.appendChild(comboitem);
			if (StringUtils.trimToEmpty(value).equals(String.valueOf(productAsset.getAssetID()))) {
				codeCombobox.setSelectedItem(comboitem);
			}
		}

		logger.debug("Leaving");
	}

	private void doDisableDepreciationDFrq(boolean isChecked, boolean isallowed) {
		if (isChecked && !isallowed) {
			this.cbfinDepreciationCode.setDisabled(false);
			this.cbfinDepreciationMth.setDisabled(false);
			this.cbfinDepreciationDays.setDisabled(false);
		} else {
			this.cbfinDepreciationCode.setDisabled(true);
			this.cbfinDepreciationMth.setDisabled(true);
			this.cbfinDepreciationDays.setDisabled(true);
		}
		if (this.finDepreciationReq.isChecked()) {
			this.finDepreciationRule.setMandatoryStyle(true);
			this.finDepreciationRule.setReadonly(isReadOnly("FinanceTypeDialog_finDepreciation"));
		} else {
			this.finDepreciationRule.setMandatoryStyle(false);
			this.finDepreciationRule.setReadonly(true);
		}
	}

	private void doCheckRIA(String value) {
		// FIXME
		//M_ this.allowRIAInvestment.setDisabled(true);
		//this.allowParllelFinance.setDisabled(true);
		this.finAEProgClaim.setMandatoryStyle(false);
		if (!StringUtils.trimToEmpty(value).equals("")) {
			if (PennantConstants.FINANCE_PRODUCT_MUDARABA.equals(value)) {
				//this.allowRIAInvestment.setChecked(true);
				this.allowParllelFinance.setDisabled(true);
			} else if (PennantConstants.FINANCE_PRODUCT_SALAM.equals(value) || PennantConstants.FINANCE_PRODUCT_ISTISNA.equals(value)) {
				//this.allowParllelFinance.setDisabled(isReadOnly("FinanceTypeDialog_allowParllelFinance"));
				//this.allowRIAInvestment.setChecked(false);
				this.finAEProgClaim.setMandatoryStyle(true);
			} else {
				//this.allowRIAInvestment.setChecked(false);
				//this.allowParllelFinance.setChecked(false);
			}
		}
	}

	/*private void doCheckFinAEProgClaim(String value) {
		if (!StringUtils.trimToEmpty(value).equals("") && PennantConstants.FINANCE_PRODUCT_ISTISNA.equals(value)) {
			this.space_finAEProgClaim.setStyle("background-color:red");
			this.btnSearchFinAEProgClaim.setDisabled(isReadOnly("FinanceTypeDialog_finAEProgClaim"));
			this.row_ProgCliamEvent.setVisible(true);
		} else {
			this.space_finAEProgClaim.setStyle("background-color:white");
			this.btnSearchFinAEProgClaim.setDisabled(true);
			this.row_ProgCliamEvent.setVisible(false);
			this.finAEProgClaim.setValue("");
			this.lovDescFinAEProgClaimName.setValue("");
		}
	}*/
	
	private void doCheckFinAEMaturity(String value) {
		if (!StringUtils.trimToEmpty(value).equals("") && PennantConstants.FINANCE_PRODUCT_ISTISNA.equals(value)) {
			this.finAEMaturity.setReadonly(isReadOnly("FinanceTypeDialog_finAEMaturity"));
			this.row_ProgCliamEvent.setVisible(true);
		} else if (!StringUtils.trimToEmpty(value).equals("") && PennantConstants.FINANCE_PRODUCT_SUKUK.equals(value)) {
			this.label_FinanceTypeSearch_FinCapitalize.setValue(Labels.getLabel("label_FinanceTypeSearch_FinCompound.value"));
		} else {
			this.finAEMaturity.setReadonly(true);
			this.row_ProgCliamEvent.setVisible(false);
			this.finAEMaturity.setValue("");
			this.finAEMaturity.setDescription("");
		}
	}

	//	++++++++++++++++++++++++++++++
	//	++++++++++ Tab 2++++++++++++
	//	+++++++++++++++++++++++++++++
	public void onChange$finGrcMargin(Event event) {
		logger.debug("Entering onChange$finGrcMargin()");
		logger.debug("Entering" + event.toString());	
		if(finGrcMargin.getValue() == null) {
			finGrcMargin.setValue(BigDecimal.ZERO);
		}
		this.labe_GrcEffectiveRate.setValue(String.valueOf(rates(this.finGrcBaseRate.getValue(), this.finGrcSplRate.getValue(), getDCBValue(this.finGrcMargin))));
		logger.debug("Leaving onChange$finGrcMargin()");
	}

	public void onCheck$finGrcIsIntCpz(Event event) {
		logger.debug("Entering" + event.toString());
		doCheckGrcPftCpzFrq();
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$cbfinGrcRateType(Event event) {
		if (this.cbfinGrcRateType.getSelectedItem() != null) {
			this.finGrcBaseRate.setValue("");
			this.finGrcSplRate.setValue("");
			this.finGrcMargin.setText("");
			doCheckRateType(this.cbfinGrcRateType, true,true);
		}
	}

	public void onChange$cbfinRateType(Event event) {
		if (this.cbfinRateType.getSelectedItem() != null) {
			this.finBaseRate.setValue("");
			this.finSplRate.setValue("");
			this.finMargin.setText("");
			doCheckRateType(this.cbfinRateType, false,true);
		}
	}

	public void onCheck$finIsIntCpz(Event event) {
		logger.debug("Entering" + event.toString());
		doCheckPftCpzFrq();
		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$finIsRvwAlw(Event event) {
		logger.debug("Entering" + event.toString());
		doCheckRpeayReview(true);
		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$finAlwIndRate(Event event) {
		logger.debug("Entering" + event.toString());
		doCheckRepayIndRate();
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$cbfinSchdMthd(Event event) {
		logger.debug("Entering" + event.toString());
		if (!getComboboxValue(cbfinRateType).equals("M")) {
			this.finIndBaseRate.setValue("");
			this.finIndBaseRate.setDescription("");
			this.finIndBaseRate.setReadonly(true);
			if (!getComboboxValue(this.cbfinSchdMthd).equals(CalculationConstants.PFT)) {
				this.finAlwIndRate.setDisabled(true);
				this.finAlwIndRate.setChecked(false);
			} else if (this.finIsRvwAlw.isChecked()) {
				this.finAlwIndRate.setDisabled(isReadOnly("FinanceTypeDialog_FinGrcAlwIndRate"));
			}
			if (this.finIsRvwAlw.isChecked()) {
				if (getComboboxValue(this.cbfinSchdMthd).equals(CalculationConstants.PRI_PFT) || getComboboxValue(this.cbfinSchdMthd).equals(CalculationConstants.PFT)) {
					// Schedule Calculation Codes
					fillComboBox(this.cbfinSchCalCodeOnRvw, "TILLMDT", scCalCode, "");
					this.cbfinSchCalCodeOnRvw.setDisabled(true);
				} else {
					// Schedule Calculation Codes
					fillComboBox(this.cbfinSchCalCodeOnRvw, "", scCalCode, ",TILLDATE,ADDTERM,ADDLAST,ADJTERMS,");
					this.cbfinSchCalCodeOnRvw.setDisabled(false);
				}
			}
		}
		
		if (getComboboxValue(this.cbfinSchdMthd).equals(CalculationConstants.EQUAL)) {
			this.gb_SteppingDetails.setVisible(true);
		} else {
			this.gb_SteppingDetails.setVisible(false);
		}
		setStepFinanceDetails();
		logger.debug("Leaving" + event.toString());
	}
	

	/** To Enable or Disable GracePeriod Tab Profit Capitalize Frequency. */
	private void doCheckGrcPftCpzFrq() {
		logger.debug("Entering ");
		if (this.finGrcIsIntCpz.isChecked()) {
			if (!isReadOnly("FinanceTypeDialog_finGrcIsIntCpz")) {
				this.cbfinGrcCpzFrqCode.setDisabled(false);
				this.cbfinGrcCpzFrqDays.setDisabled(false);
				this.cbfinGrcCpzFrqMth.setDisabled(false);
			}
		} else {
			this.finGrcCpzFrq.setValue("");
			this.cbfinGrcCpzFrqCode.setSelectedIndex(0);
			this.cbfinGrcCpzFrqCode.setDisabled(true);
			this.cbfinGrcCpzFrqDays.setSelectedIndex(0);
			this.cbfinGrcCpzFrqDays.setDisabled(true);
			this.cbfinGrcCpzFrqMth.setSelectedIndex(0);
			this.cbfinGrcCpzFrqMth.setDisabled(true);

		}
		logger.debug("Leaving");
	}

	/** To Enable or Disable Schedule Tab Review Frequency. */
	private void doCheckRpeayReview(boolean checkAction) {
		logger.debug("Entering");
		if (this.finIsRvwAlw.isChecked()) {
			if (!isReadOnly("FinanceTypeDialog_finIsRvwAlw")) {
				this.cbfinRvwFrqCode.setDisabled(false);
				this.cbfinRvwFrqMth.setDisabled(false);
				this.cbfinRvwFrqDays.setDisabled(false);
				this.cbfinRvwRateApplFor.setDisabled(false);
				this.cbfinSchCalCodeOnRvw.setDisabled(false);
				this.space_cbfinSchCalCodeOnRvw.setSclass("mandatory");
				if (this.cbfinSchdMthd.getSelectedItem().getValue().equals(CalculationConstants.PRI_PFT)
				        || this.cbfinSchdMthd.getSelectedItem().getValue().equals(CalculationConstants.PFT)) {
					fillComboBox(this.cbfinSchCalCodeOnRvw, "TILLMDT", scCalCode, "");
					this.cbfinSchCalCodeOnRvw.setDisabled(true);
					this.space_cbfinSchCalCodeOnRvw.setSclass("none");
				}
				this.space_FinRvwRateApplFor.setSclass("mandatory");

				if (getComboboxValue(this.cbfinSchdMthd).equals(CalculationConstants.PFT)) {
					this.finAlwIndRate.setDisabled(isReadOnly("FinanceTypeDialog_FinGrcAlwIndRate"));
				} else {
					this.finAlwIndRate.setDisabled(true);
				}
			}
		} else {
			if(checkAction){
				this.finRvwFrq.setValue("");
				this.cbfinRvwFrqCode.setSelectedIndex(0);
				this.cbfinRvwFrqMth.setSelectedIndex(0);
				this.cbfinRvwFrqDays.setSelectedIndex(0);
				this.cbfinRvwRateApplFor.setSelectedIndex(0);
				this.cbfinSchCalCodeOnRvw.setSelectedIndex(0);
				this.finAlwIndRate.setChecked(false);
				this.finIndBaseRate.setValue("");
				this.finIndBaseRate.setDescription("");
			}
			this.space_FinRvwRateApplFor.setSclass("none");
			this.space_cbfinSchCalCodeOnRvw.setSclass("none");
			this.cbfinRvwFrqCode.setDisabled(true);
			this.cbfinRvwFrqMth.setDisabled(true);
			this.cbfinRvwFrqDays.setDisabled(true);
			this.cbfinRvwRateApplFor.setDisabled(true);
			this.cbfinSchCalCodeOnRvw.setDisabled(true);
			this.finAlwIndRate.setDisabled(true);
			this.finIndBaseRate.setReadonly(true);
		}
		this.finIndBaseRate.setMandatoryStyle(false);
		logger.debug("Leaving");
	}

	private void doCheckRepayIndRate() {
		if (this.finAlwIndRate.isChecked()) {
			this.finIndBaseRate.setMandatoryStyle(true);
			this.finIndBaseRate.setReadonly(isReadOnly("FinanceTypeDialog_FinIndRate"));
		} else {
			this.finIndBaseRate.setValue("");
			this.finIndBaseRate.setMandatoryStyle(false);
			this.finIndBaseRate.setDescription("");
			this.finIndBaseRate.setReadonly(true);
		}
	}
/*
 * Method for setting default values for Step Finance Details
 */
	public void setStepFinanceDetails(){
		logger.debug("Entering ");
		this.stepFinance.setChecked(getFinanceType().isStepFinance());
		this.steppingMandatory.setChecked(getFinanceType().isSteppingMandatory());
		this.allowManualSteps.setChecked(getFinanceType().isAlwManualSteps());
		fillComboBox(this.dftStepPolicy, StringUtils.trimToEmpty(getFinanceType().getDftStepPolicy()), this.stepPolicies, "");
		this.lovDescStepPolicyCodename.setValue(getFinanceType().getAlwdStepPolicies());
		logger.debug("Leaving ");
	}
	
	/** To Enable or Disable Schedule Tab Profit Capitalize Frequency. */
	private void doCheckPftCpzFrq() {
		logger.debug("Entering");
		if (this.finIsIntCpz.isChecked()) {
			if (!isReadOnly("FinanceTypeDialog_finGrcIsIntCpz")) {
				this.cbfinCpzFrqCode.setDisabled(false);
				this.cbfinCpzFrqMth.setDisabled(false);
				this.cbfinCpzFrqDays.setDisabled(false);
			}
		} else {
			this.cbfinCpzFrqCode.setSelectedIndex(0);
			this.cbfinCpzFrqCode.setDisabled(true);
			this.cbfinCpzFrqMth.setSelectedIndex(0);
			this.cbfinCpzFrqMth.setDisabled(true);
			this.cbfinCpzFrqDays.setSelectedIndex(0);
			this.cbfinCpzFrqDays.setDisabled(true);
			this.finCpzFrq.setValue("");
		}
		logger.debug("Leaving");
	}

	private void doCheckRateType(Combobox combobox, boolean isGrc, boolean checkAction) {
		logger.debug("Entering");
		String value = getComboboxValue(combobox);
		//grace
		if (isGrc) {
			if ("R".equals(value)) {
				this.finGrcBaseRate.setReadonly(isReadOnly("FinanceTypeDialog_finGrcBaseRate"));
				this.finGrcSplRate.setReadonly(isReadOnly("FinanceTypeDialog_finGrcSplRate"));

				this.finGrcIntRate.setReadonly(isReadOnly("FinanceTypeDialog_finGrcIntRate"));
				this.finGrcMargin.setReadonly(isReadOnly("FinanceTypeDialog_FinGrcMargin"));

				// Rate review
				this.finGrcIsRvwAlw.setDisabled(isReadOnly("FinanceTypeDialog_finGrcIsRvwAlw"));
				doCheckGraceReview();
				
				if(checkAction){
					//Indicative rate is mandatory
					this.finGrcAlwIndRate.setChecked(false);
				}
				
				doCheckGrcAlwIndRate();

			} else if ("F".equals(value) || "C".equals(value)) {
				this.finGrcBaseRate.setReadonly(true);
				this.finGrcSplRate.setReadonly(true);
				
				if(checkAction){
					this.finGrcBaseRate.setDescription("");
					this.finGrcSplRate.setDescription("");
					this.labe_GrcEffectiveRate.setValue("");
				}

				this.finGrcIntRate.setReadonly(isReadOnly("FinanceTypeDialog_finGrcIntRate"));
				this.finGrcMargin.setReadonly(true);

				// Rate review
				this.finGrcIsRvwAlw.setDisabled(isReadOnly("FinanceTypeDialog_finGrcIsRvwAlw"));
				doCheckGraceReview();
				
				if(checkAction){
					//Indicative rate is mandatory
					this.finGrcAlwIndRate.setChecked(false);
				}
				doCheckGrcAlwIndRate();

			} else {
				this.finGrcBaseRate.setReadonly(true);
				this.finGrcSplRate.setReadonly(true);
				
				if(checkAction){
					this.finGrcBaseRate.setDescription("");
					this.finGrcSplRate.setDescription("");
					this.labe_GrcEffectiveRate.setValue("");
				}

				this.finGrcIntRate.setReadonly(true);
				this.finGrcMargin.setReadonly(true);
				
				if(checkAction){
					//No Rate review
					this.finGrcIsRvwAlw.setChecked(false);
				}
				doCheckGraceReview();
				this.finGrcIsRvwAlw.setDisabled(true);
				//Indicative rate is mandatory
				this.finGrcAlwIndRate.setChecked(true);
				doCheckGrcAlwIndRate();
			}

		} else {
			//repayment
			if ("R".equals(value)) {
				this.finBaseRate.setReadonly(isReadOnly("FinanceTypeDialog_finBaseRate"));
				this.finSplRate.setReadonly(isReadOnly("FinanceTypeDialog_finSplRate"));
				this.finIntRate.setReadonly(isReadOnly("FinanceTypeDialog_finIntRate"));
				this.finMargin.setReadonly(isReadOnly("FinanceTypeDialog_FinMargin"));
				//No Rate review
				this.finIsRvwAlw.setDisabled(isReadOnly("FinanceTypeDialog_finIsRvwAlw"));
				doCheckRpeayReview(checkAction);
				
				if(checkAction){
					//Indicative rate is mandatory
					this.finAlwIndRate.setChecked(false);
				}
				doCheckRepayIndRate();

			} else if ("F".equals(value) || "C".equals(value)) {
				this.finBaseRate.setReadonly(true);
				this.finSplRate.setReadonly(true);
				if(checkAction){
					this.finBaseRate.setDescription("");
					this.finSplRate.setDescription("");
					this.labe_EffectiveRate.setValue("");
				}

				this.finIntRate.setReadonly(isReadOnly("FinanceTypeDialog_finIntRate"));
				this.finMargin.setReadonly(true);

				//No Rate review
				this.finIsRvwAlw.setDisabled(isReadOnly("FinanceTypeDialog_finIsRvwAlw"));
				doCheckRpeayReview(checkAction);
				
				if(checkAction){
					//Indicative rate is mandatory
					this.finAlwIndRate.setChecked(false);
				}
				doCheckRepayIndRate();

			} else {
				this.finBaseRate.setReadonly(true);
				this.finSplRate.setReadonly(true);
				if(checkAction){
					this.finBaseRate.setDescription("");
					this.finSplRate.setDescription("");
					this.labe_EffectiveRate.setValue("");
				}

				this.finIntRate.setReadonly(true);
				this.finMargin.setReadonly(true);
				
				if(checkAction){
					//No Rate review
					this.finIsRvwAlw.setChecked(false);
				}
				
				doCheckRpeayReview(checkAction);
				this.finIsRvwAlw.setDisabled(true);
				
				//Indicative rate is mandatory
				this.finAlwIndRate.setChecked(true);
				doCheckRepayIndRate();

			}
		}

		logger.debug("Leaving");
	}
	
	//	++++++++++++++++++++++++++++++
	//	++++++++++ Tab 3++++++++++++
	//	+++++++++++++++++++++++++++++
	
	public void onCheck$applyODPenalty(Event event){
		logger.debug("Entering" + event.toString());
		onCheckODPenalty(true);
		logger.debug("Leaving" + event.toString());
	}
	
	private void onCheckODPenalty(boolean checkAction){
		if(this.applyODPenalty.isChecked()){
			
			this.oDIncGrcDays.setDisabled(isReadOnly("FinanceTypeDialog_oDIncGrcDays"));
			this.oDChargeType.setDisabled(isReadOnly("FinanceTypeDialog_oDChargeType"));
			this.oDGraceDays.setReadonly(isReadOnly("FinanceTypeDialog_oDGraceDays"));
			this.oDChargeCalOn.setDisabled(isReadOnly("FinanceTypeDialog_oDChargeCalOn"));
			this.oDAllowWaiver.setDisabled(isReadOnly("FinanceTypeDialog_oDAllowWaiver"));
			
			if(checkAction){
				this.oDChargeAmtOrPerc.setDisabled(true);
				this.oDMaxWaiverPerc.setDisabled(true);
			}else{
				onChangeODChargeType(false);
				onCheckODWaiver(false);
			}
			
		}else{
			this.oDIncGrcDays.setDisabled(true);
			this.oDChargeType.setDisabled(true);
			this.oDGraceDays.setReadonly(true);
			this.oDChargeCalOn.setDisabled(true);
			this.oDChargeAmtOrPerc.setDisabled(true);
			this.oDAllowWaiver.setDisabled(true);
			this.oDMaxWaiverPerc.setDisabled(true);
			
			checkAction = true;
		}
		
		if(checkAction){
			this.oDIncGrcDays.setChecked(false);
			this.oDChargeType.setSelectedIndex(0);
			this.oDGraceDays.setValue(0);
			this.oDChargeCalOn.setSelectedIndex(0);
			this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
			this.oDAllowWaiver.setChecked(false);
			this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
		}
	}
	
	public void onChange$oDChargeType(Event event){
		logger.debug("Entering" + event.toString());
		onChangeODChargeType(true);
		logger.debug("Leaving" + event.toString());
	}
	
	private void onChangeODChargeType(boolean changeAction){
		if(changeAction){
			this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
		}
		this.space_oDChargeAmtOrPerc.setSclass("mandatory");
		if (getComboboxValue(this.oDChargeType).equals(PennantConstants.List_Select)) {
			this.oDChargeAmtOrPerc.setDisabled(true);
			this.space_oDChargeAmtOrPerc.setSclass("");
		}else if (getComboboxValue(this.oDChargeType).equals(PennantConstants.FLAT)) {
			this.oDChargeAmtOrPerc.setDisabled(isReadOnly("FinanceTypeDialog_oDChargeAmtOrPerc"));
			this.oDChargeAmtOrPerc.setMaxlength(15);
			this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceType().getLovDescFinFormetter()));
		} else {
			this.oDChargeAmtOrPerc.setDisabled(isReadOnly("FinanceTypeDialog_oDChargeAmtOrPerc"));
			this.oDChargeAmtOrPerc.setMaxlength(6);
			this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(2));
		}
	}
	
	public void onCheck$oDAllowWaiver(Event event){
		logger.debug("Entering" + event.toString());
		onCheckODWaiver(true);
		logger.debug("Leaving" + event.toString());
	}
	
	private void onCheckODWaiver(boolean checkAction) {
		if(checkAction){
			this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
		}
		if (this.oDAllowWaiver.isChecked()) {
			this.space_oDMaxWaiverPerc.setSclass("mandatory");
			this.oDMaxWaiverPerc.setDisabled(isReadOnly("FinanceTypeDialog_oDMaxWaiverPerc"));
		}else {
			this.oDMaxWaiverPerc.setDisabled(true);
			this.space_oDMaxWaiverPerc.setSclass("");
		}
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

	public FinanceType getFinanceType() {
		return this.financeType;
	}

	public void setFinanceType(FinanceType financeType) {
		this.financeType = financeType;
	}

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	public FinanceTypeService getFinanceTypeService() {
		return this.financeTypeService;
	}

	public void setFinanceTypeListCtrl(FinanceTypeListCtrl financeTypeListCtrl) {
		this.financeTypeListCtrl = financeTypeListCtrl;
	}

	public FinanceTypeListCtrl getFinanceTypeListCtrl() {
		return this.financeTypeListCtrl;
	}

	public PagedListService getPagedListService() {
		pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aFinanceType
	 *            (FinanceType)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(FinanceType aFinanceType, String tranType) {
		logger.debug("Entering getAuditHeader()");

		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceType.getBefImage(), aFinanceType);
		logger.debug("Leaving getAuditHeader()");
		return new AuditHeader(String.valueOf(aFinanceType.getId()), null, null, null, auditDetail, aFinanceType.getUserDetails(), getOverideMap());

	}

	// To Show Error messages
	private void showMessage(Exception e) {
		logger.debug("Entering showMessage()");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinanceTypeDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving showMessage()");
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	/** To get Note Dialog on clicking the button note */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		final HashMap map = new HashMap();
		map.put("notes", getNotes());
		map.put("control", this);
		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("FinanceType");
		notes.setReference(getFinanceType().getFinType());
		notes.setVersion(getFinanceType().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	public int getCountRows() {
		return countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

	public void onChange$finMargin(Event event) {
		logger.debug("Entering onChange$finMargin()");
		logger.debug("Entering" + event.toString());
		if(finMargin.getValue() == null) {
			finMargin.setValue(BigDecimal.ZERO);
		}
		this.labe_EffectiveRate.setValue(String.valueOf(rates(this.finBaseRate.getValue(), this.finSplRate.getValue(), getDCBValue(this.finMargin))));
		logger.debug("Leaving onChange$finMargin()");
	}

	public void onCheck$finGrcIsRvwAlw(Event event) {
		logger.debug("Entering" + event.toString());
		doCheckGraceReview();
		logger.debug("Leaving" + event.toString());
	}

	/** To Disable Grace Period Tab Profit Review Frequency Used Twice */
	private void doCheckGraceReview() {
		logger.debug("Entering doDisableGrcRVFrequency()");
		if (this.finGrcIsRvwAlw.isChecked()) {
			if (!isReadOnly("FinanceTypeDialog_finGrcIsRvwAlw")) {
				this.cbfinGrcRvwFrqCode.setDisabled(false);
				this.cbfinGrcRvwFrqMth.setDisabled(false);
				this.cbfinGrcRvwFrqDays.setDisabled(false);
				this.cbfinGrcRvwRateApplFor.setDisabled(false);
				this.space_FinGrcRvwRateApplFor.setSclass("mandatory");
				this.finGrcAlwIndRate.setDisabled(false);
			}
		} else {
			this.finGrcRvwFrq.setValue("");
			this.space_FinGrcRvwRateApplFor.setSclass("none");
			this.cbfinGrcRvwFrqCode.setSelectedIndex(0);
			this.cbfinGrcRvwFrqCode.setDisabled(true);
			this.cbfinGrcRvwFrqMth.setSelectedIndex(0);
			this.cbfinGrcRvwFrqMth.setDisabled(true);
			this.cbfinGrcRvwFrqDays.setSelectedIndex(0);
			this.cbfinGrcRvwFrqDays.setDisabled(true);
			this.cbfinGrcRvwRateApplFor.setSelectedIndex(0);
			this.cbfinGrcRvwRateApplFor.setDisabled(true);
			this.finGrcAlwIndRate.setChecked(false);
			this.finGrcAlwIndRate.setDisabled(true);
			this.finGrcIndBaseRate.setValue("");
			this.lovDescFinGrcIndBaseRateName.setValue("");
			this.btnSearchFinGrcIndBaseRate.setDisabled(true);
		}
		this.space_finGrcIndBaseRate.setStyle("background-color:white");
		logger.debug("Leaving doDisableGrcRVFrequency()");
	}

	public void onCheck$finGrcAlwIndRate(Event event) {
		logger.debug("Entering" + event.toString());
		doCheckGrcAlwIndRate();
		logger.debug("Leaving" + event.toString());
	}

	private void doCheckGrcAlwIndRate() {
		if (this.finGrcAlwIndRate.isChecked()) {
			this.space_finGrcIndBaseRate.setStyle("background:red;");
			this.btnSearchFinGrcIndBaseRate.setDisabled(isReadOnly("FinanceTypeDialog_FinGrcIndRate"));
		} else {
			this.finGrcIndBaseRate.setValue("");
			this.lovDescFinGrcIndBaseRateName.setValue("");
			this.space_finGrcIndBaseRate.setStyle("background:white;");
			this.btnSearchFinGrcIndBaseRate.setDisabled(true);
		}
	}

	public void onCheck$finIsAlwGrcRepay(Event event) {
		logger.debug("Entering" + event.toString());
		this.finGrcSchdMthd.setSelectedIndex(0);
		doDisableGrcSchdMtd();
		logger.debug("Leaving" + event.toString());
	}

	private void doDisableGrcSchdMtd() {
		if (this.finIsAlwGrcRepay.isChecked()) {
			this.space_finGrcSchdMthd.setStyle("background:red;");
			this.finGrcSchdMthd.setDisabled(isReadOnly("FinanceTypeDialog_FinGrcSchdMthd"));
		} else {
			this.space_finGrcSchdMthd.setStyle("background:white;");
			this.finGrcSchdMthd.setDisabled(true);
		}
	}

	/** method to check rate type in grace tab */

	public void onCheck$finIsAlwDifferment(Event event) {
		logger.debug("Entering onCheck$finIsAlwDifferment()");
		doDisableOrEnableDifferments(this.finIsAlwDifferment.isChecked(), this.finMaxDifferment, isReadOnly("FinanceTypeDialog_finMaxDifferment"));

		if (getFinanceType().getLovDescAERule().containsKey("DEFRPY")) {
			accSet = getFinanceType().getLovDescAERule().get("DEFRPY");
			this.finDefRepay.setValue(accSet.getStringaERuleId());
			this.finDefRepay.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
		} else {
			this.finDefRepay.setValue("");
			this.finDefRepay.setDescription("");
		}
		doCheckRpyDefferment();
		logger.debug("Leaving onCheck$finIsAlwDifferment()");

	}

	public void onCheck$finIsAlwFrqDifferment(Event event) {
		logger.debug("Entering onCheck$finIsAlwDifferment()");
		doDisableOrEnableDifferments(this.finIsAlwFrqDifferment.isChecked(), this.finMaxFrqDifferment, isReadOnly("FinanceTypeDialog_finMaxFrqDifferment"));

		if (getFinanceType().getLovDescAERule().containsKey("DEFFRQ")) {
			accSet = getFinanceType().getLovDescAERule().get("DEFFRQ");
			this.finDeffreq.setValue(accSet.getStringaERuleId());
			this.finDeffreq.setDescription(accSet.getAccountSetCode() + "-" + accSet.getAccountSetCodeName());
		} else {
			this.finDeffreq.setValue("");
			this.finDeffreq.setDescription("");
		}
		doCheckFrqDefferment();
		logger.debug("Leaving onCheck$finIsAlwDifferment()");
	}

	private void doCheckFrqDefferment() {
		if (this.finIsAlwFrqDifferment.isChecked()) {
			this.finDeffreq.setMandatoryStyle(false);
			this.finDeffreq.setReadonly(isReadOnly("FinanceTypeDialog_FinDeffreq"));
		} else {
			this.finDeffreq.setMandatoryStyle(false);
			this.finDeffreq.setReadonly(true);
		}
	}

	private void doCheckRpyDefferment() {
		if (this.finIsAlwDifferment.isChecked()) {
			this.finDefRepay.setMandatoryStyle(false);
			this.finDefRepay.setReadonly(isReadOnly("FinanceTypeDialog_FinDefRepay"));
		} else {
			this.finDefRepay.setMandatoryStyle(false);
			this.finDefRepay.setReadonly(true);
		}
	}

	private void doDisableOrEnableDifferments(boolean isAllow, Intbox intbox, boolean isReadOnly) {
		logger.debug("Entering");
		intbox.setReadonly(isReadOnly);
		if (!isAllow) {
			intbox.setValue(0);
			intbox.setReadonly(true);
		}
		logger.debug("Leaving");
	}

	/** To Check the user action based on the result removes the error messages; */
	public void onCheck$userAction(Event event) {
		logger.debug("Entering" + event.toString());
		if ("Save".equals(userAction.getSelectedItem().getLabel())) {
			doClearMessages();
		}
		logger.debug("Leaving" + event.toString());
	}

	/** TO clear all error messages */
	private void doClearMessages() {
		logger.debug("Entering");
		// Basic Tab
		this.finType.clearErrorMessage();
		this.finTypeDesc.clearErrorMessage();
		this.finCcy.clearErrorMessage();
		this.finDivision.clearErrorMessage();
		this.cbfinDaysCalType.clearErrorMessage();
		this.finAcType.clearErrorMessage();
		this.pftPayAcType.clearErrorMessage();
		this.lovDescFinContingentAcTypeName.clearErrorMessage();
		this.finSuspAcType.clearErrorMessage();
		this.finMaxAmount.clearErrorMessage();
		this.finMinAmount.clearErrorMessage();
		this.cbfinDftStmtFrqCode.clearErrorMessage();
		this.finDftStmtFrq.clearErrorMessage();
		this.cbfinDftStmtFrqMth.clearErrorMessage();
		this.cbfinDftStmtFrqDays.clearErrorMessage();
		this.finHistRetension.clearErrorMessage();
		this.cbfinDftStmtFrqCode.clearErrorMessage();
		this.cbfinDftStmtFrqMth.clearErrorMessage();
		this.cbfinDftStmtFrqDays.clearErrorMessage();
		this.cbfinSchdMthd.clearErrorMessage();
		// Scheduling Tab
		this.cbfinRateType.clearErrorMessage();
		this.finBaseRate.clearErrorMessage();
		this.finSplRate.clearErrorMessage();
		this.finIntRate.clearErrorMessage();
		this.fInMinRate.clearErrorMessage();
		this.finMaxRate.clearErrorMessage();
		this.cbfinDftIntFrqCode.clearErrorMessage();
		this.cbfinDftIntFrqMth.clearErrorMessage();
		this.cbfinDftIntFrqDays.clearErrorMessage();
		this.cbfinCpzFrqCode.clearErrorMessage();
		this.cbfinCpzFrqMth.clearErrorMessage();
		this.cbfinCpzFrqDays.clearErrorMessage();
		this.cbfinRvwFrqCode.clearErrorMessage();
		this.cbfinRvwFrqMth.clearErrorMessage();
		this.finMargin.clearErrorMessage();
		this.cbfinRvwFrqDays.clearErrorMessage();
		// Grace Tab
		this.cbfinGrcRateType.clearErrorMessage();
		this.finGrcBaseRate.clearErrorMessage();
		this.finGrcSplRate.clearErrorMessage();
		this.finGrcIntRate.clearErrorMessage();
		this.fInGrcMinRate.clearErrorMessage();
		this.finGrcMaxRate.clearErrorMessage();
		this.cbfinGrcDftIntFrqCode.clearErrorMessage();
		this.cbfinGrcDftIntFrqMth.clearErrorMessage();
		this.cbfinGrcDftIntFrqDays.clearErrorMessage();
		this.cbfinGrcCpzFrqCode.clearErrorMessage();
		this.cbfinGrcCpzFrqMth.clearErrorMessage();
		this.cbfinGrcCpzFrqDays.clearErrorMessage();
		this.cbfinGrcRvwFrqCode.clearErrorMessage();
		this.cbfinGrcRvwFrqMth.clearErrorMessage();
		this.cbfinGrcRvwFrqDays.clearErrorMessage();
		this.finGrcMargin.clearErrorMessage();
		// Repayments Tab
		this.finMinTerm.clearErrorMessage();
		this.finMaxTerm.clearErrorMessage();
		this.finDftTerms.clearErrorMessage();
		this.cbfinRpyFrqCode.clearErrorMessage();
		this.cbfinRpyFrqMth.clearErrorMessage();
		this.cbfinRpyFrqDays.clearErrorMessage();
		this.cbfinRepayMethod.clearErrorMessage();
		this.finODRpyTries.clearErrorMessage();
		// Accounting Tab
		this.finAEAddDsbOD.clearErrorMessage();
		this.finAEAddDsbFD.clearErrorMessage();
		this.finAEAddDsbFDA.clearErrorMessage();
		this.finAEAmzNorm.clearErrorMessage();
		this.finAEAmzSusp.clearErrorMessage();
		this.finAEToNoAmz.clearErrorMessage();
		this.finToAmz.clearErrorMessage();
		this.finMAmz.clearErrorMessage();
		this.finAERateChg.clearErrorMessage();
		this.finAERepay.clearErrorMessage();
		this.lovDescFinAEEarlyPayName.clearErrorMessage();
		this.finAEEarlySettle.clearErrorMessage();
		this.finAEWriteOff.clearErrorMessage();
		this.finAEWriteOffBK.clearErrorMessage();
		this.finAEGraceEnd.clearErrorMessage();
		this.finDepreciationRule.clearErrorMessage();
		this.finDeffreq.clearErrorMessage();
		this.finDefRepay.clearErrorMessage();
		this.finMinDownPayAmount.clearErrorMessage();
		
		this.finDftIntFrq.clearErrorMessage();
		this.finCpzFrq.clearErrorMessage();
		this.finRvwFrq.clearErrorMessage();
		this.finGrcDftIntFrq.clearErrorMessage();
		this.finGrcCpzFrq.clearErrorMessage();
		this.finGrcRvwFrq.clearErrorMessage();
		this.finRpyFrq.clearErrorMessage();
		this.finLatePayRule.clearErrorMessage();
		this.finSchdChange.clearErrorMessage();
		this.finProvision.clearErrorMessage();
		this.finAECapitalize.clearErrorMessage();
		this.finAEProgClaim.clearErrorMessage();
		
		// OverDue Details
		this.oDChargeCalOn.clearErrorMessage();
		this.oDChargeType.clearErrorMessage();
		this.oDChargeAmtOrPerc.clearErrorMessage();
		this.oDMaxWaiverPerc.clearErrorMessage();
		
		// Stepping Details
		this.lovDescStepPolicyCodename.clearErrorMessage();
		this.dftStepPolicy.clearErrorMessage();
		
		logger.debug("Leaving");
	}

	public void onClick$btnCopyTo(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose();
		Events.postEvent("onClick$button_FinanceTypeList_NewFinanceType", financeTypeListCtrl.window_FinanceTypeList, getFinanceType());
		logger.debug("Leaving" + event.toString());
	}

	// ====================//
	// ====Utilities=======//
	// ====================//

	/**
	 * To check the higher of the give two decimal boxes
	 * 
	 * @param Decimalbox
	 *            ,Decimal box,String,String
	 * @throws WrongValueException
	 */
	private void mustBeHigher(Decimalbox maxvalue, Decimalbox minvalue, String maxlabel, String minlabel) {
		logger.debug("Entering");
		if ((maxvalue.getValue() != null) && (minvalue.getValue() != null) && (maxvalue.getValue().compareTo(BigDecimal.ZERO) != 0)) {
			if (maxvalue.getValue().compareTo(minvalue.getValue()) != 1) {
				throw new WrongValueException(maxvalue, Labels.getLabel("FIELD_IS_GREATER", new String[] { Labels.getLabel(maxlabel), Labels.getLabel(minlabel) }));
			}
		}
		logger.debug("Leaving");
	}
	private void mustBeHigher(CurrencyBox maxvalue, CurrencyBox minvalue, String maxlabel, String minlabel) {
		logger.debug("Entering");
		if ((maxvalue.getValue() != null) && (minvalue.getValue() != null) && (maxvalue.getValue().compareTo(BigDecimal.ZERO) != 0)) {
			if (maxvalue.getValue().compareTo(minvalue.getValue()) != 1) {
				throw new WrongValueException(maxvalue, Labels.getLabel("FIELD_IS_GREATER", new String[] { Labels.getLabel(maxlabel), Labels.getLabel(minlabel) }));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * To avoid null from getting the value of decimal box
	 *  <br> IN PaymentDialogCtrl.java
	 * @param decimalbox
	 * @return  BigDecimal 
	 */
	private BigDecimal getDCBValue(Decimalbox decimalbox) {
		if (decimalbox.getValue() != null) {
			return decimalbox.getValue();
		}
		return BigDecimal.ZERO;
	}

	private BigDecimal rates(String baseRateCode, String splRateCode, BigDecimal margin) {
		logger.debug("Entering");
		RateDetail rate = new RateDetail();
		rate.setBaseRateCode(baseRateCode);
		rate.setSplRateCode(splRateCode);
		rate.setMargin(margin);
		rate.setValueDate((Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR));
		RateDetail rateDetail = RateUtil.getRefRate(rate);
		if (rateDetail != null && rateDetail.getNetRefRateLoan() != null) {
			logger.debug("Leaving");
			return rateDetail.getNetRefRateLoan();
		} else {
			logger.debug("Leaving");
			return BigDecimal.ZERO;
		}
	}

	private void doCheckBoxChecked(boolean checked, Checkbox checkbox) {
		if (checked) {
			checkbox.setDisabled(isReadOnly("FinanceTypeDialog_overrideLimit"));
		} else {
			checkbox.setDisabled(true);
			checkbox.setChecked(false);
		}
	}

	/**
	 * To set Default values when new record
	 *  <br> IN FinanceTypeDialogCtrl.java  
	 */
	private void setDefaultValues() {
		logger.debug("Entering");
		//this.pftPayAcType.setValue("");
		this.finBankContingentAcType.setValue("");
		this.finContingentAcType.setValue("");
		//this.finSuspAcType.setValue("");
		//this.finProvisionAcType.setValue("");
		this.finIsOpenPftPayAcc.setValue(false);
		this.finDftStmtFrq.setValue("Y1231");
		this.finHistRetension.setValue(12);
		this.finCollateralReq.setValue(false);
		this.finCollateralOvrride.setValue(false);
		this.finDepreciationFrq.setValue("M0031");
		this.fInGrcMinRate.setValue(BigDecimal.ZERO);
		this.finGrcMaxRate.setValue(BigDecimal.ZERO);
		this.cbFinGrcScheduleOn.setSelectedIndex(2);
		this.finGrcAlwRateChgAnyDate.setValue(false);
		this.finGrcAlwIndRate.setValue(false);
		this.finGrcIndBaseRate.setValue("");
		this.fInMinRate.setValue(BigDecimal.ZERO);
		this.finMaxRate.setValue(BigDecimal.ZERO);
		this.finAlwRateChangeAnyDate.setValue(false);
		this.finIsAlwEarlyRpy.setValue(true);
		this.finIsAlwEarlySettle.setValue(true);
		this.finAEEarlyPay.setValue("");
		//this.finAEEarlySettle.setValue("");
		logger.debug("Leaving ");
	}

	//============================================================= Frequencies ============================================//	
	/* Tab 2 */
	public void onSelect$cbfinGrcDftIntFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = getComboboxValue(this.cbfinGrcDftIntFrqCode);
		onSelectFrqCode(stmtFrqCode, this.cbfinGrcDftIntFrqCode, this.cbfinGrcDftIntFrqMth, this.cbfinGrcDftIntFrqDays, this.finGrcDftIntFrq,
		        isReadOnly("FinanceTypeDialog_finGrcDftIntFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinGrcDftIntFrqMth(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = getComboboxValue(this.cbfinGrcDftIntFrqCode);
		String stmtFrqMonth = getComboboxValue(this.cbfinGrcDftIntFrqMth);
		onSelectFrqMth(stmtFrqCode, stmtFrqMonth, this.cbfinDftIntFrqMth, this.cbfinGrcDftIntFrqDays, this.finGrcDftIntFrq, isReadOnly("FinanceTypeDialog_finGrcDftIntFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinGrcDftIntFrqDays(Event event) {
		logger.debug("Entering" + event.toString());
		onSelectFrqDay(cbfinGrcDftIntFrqCode, cbfinGrcDftIntFrqMth, cbfinGrcDftIntFrqDays, this.finGrcDftIntFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinGrcCpzFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = getComboboxValue(this.cbfinGrcCpzFrqCode);
		onSelectFrqCode(stmtFrqCode, this.cbfinGrcCpzFrqCode, this.cbfinGrcCpzFrqMth, this.cbfinGrcCpzFrqDays, this.finGrcCpzFrq, isReadOnly("FinanceTypeDialog_finGrcCpzFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinGrcCpzFrqMth(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = getComboboxValue(this.cbfinGrcCpzFrqCode);
		String stmtFrqMonth = getComboboxValue(this.cbfinGrcCpzFrqMth);
		onSelectFrqMth(stmtFrqCode, stmtFrqMonth, this.cbfinGrcCpzFrqMth, this.cbfinGrcCpzFrqDays, this.finGrcCpzFrq, isReadOnly("FinanceTypeDialog_finGrcCpzFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinGrcCpzFrqDays(Event event) {
		logger.debug("Entering" + event.toString());
		onSelectFrqDay(cbfinGrcCpzFrqCode, cbfinGrcCpzFrqMth, cbfinGrcCpzFrqDays, this.finGrcCpzFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinGrcRvwFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = getComboboxValue(this.cbfinGrcRvwFrqCode);
		onSelectFrqCode(stmtFrqCode, this.cbfinGrcRvwFrqCode, this.cbfinGrcRvwFrqMth, this.cbfinGrcRvwFrqDays, this.finGrcRvwFrq, isReadOnly("FinanceTypeDialog_finGrcRvwFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinGrcRvwFrqMth(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = getComboboxValue(this.cbfinGrcRvwFrqCode);
		String stmtFrqMonth = getComboboxValue(this.cbfinGrcRvwFrqMth);
		onSelectFrqMth(stmtFrqCode, stmtFrqMonth, this.cbfinGrcRvwFrqMth, this.cbfinGrcRvwFrqDays, this.finGrcRvwFrq, isReadOnly("FinanceTypeDialog_finGrcRvwFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinGrcRvwFrqDays(Event event) {
		logger.debug("Entering" + event.toString());
		onSelectFrqDay(cbfinGrcRvwFrqCode, cbfinGrcRvwFrqMth, cbfinGrcRvwFrqDays, this.finGrcRvwFrq);
		logger.debug("Leaving" + event.toString());
	}

	/* Tab 3 */
	public void onSelect$cbfinDftIntFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = getComboboxValue(this.cbfinDftIntFrqCode);
		onSelectFrqCode(stmtFrqCode, this.cbfinDftIntFrqCode, this.cbfinDftIntFrqMth, this.cbfinDftIntFrqDays, this.finDftIntFrq, isReadOnly("FinanceTypeDialog_finDftIntFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinDftIntFrqMth(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = getComboboxValue(this.cbfinDftIntFrqCode);
		String stmtFrqMonth = getComboboxValue(this.cbfinDftIntFrqMth);
		onSelectFrqMth(stmtFrqCode, stmtFrqMonth, this.cbfinDftIntFrqMth, this.cbfinDftIntFrqDays, this.finDftIntFrq, isReadOnly("FinanceTypeDialog_finDftIntFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinDftIntFrqDays(Event event) {
		logger.debug("Entering" + event.toString());
		onSelectFrqDay(cbfinDftIntFrqCode, cbfinDftIntFrqMth, cbfinDftIntFrqDays, this.finDftIntFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinRpyFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = getComboboxValue(this.cbfinRpyFrqCode);
		onSelectFrqCode(stmtFrqCode, this.cbfinRpyFrqCode, this.cbfinRpyFrqMth, this.cbfinRpyFrqDays, this.finRpyFrq, isReadOnly("FinanceTypeDialog_finRpyFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinRpyFrqMth(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = getComboboxValue(this.cbfinRpyFrqCode);
		String stmtFrqMonth = getComboboxValue(this.cbfinRpyFrqMth);
		onSelectFrqMth(stmtFrqCode, stmtFrqMonth, this.cbfinRpyFrqMth, this.cbfinRpyFrqDays, this.finRpyFrq, isReadOnly("FinanceTypeDialog_finRpyFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinRpyFrqDays(Event event) {
		logger.debug("Entering" + event.toString());
		onSelectFrqDay(cbfinRpyFrqCode, cbfinRpyFrqMth, cbfinRpyFrqDays, this.finRpyFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinCpzFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = getComboboxValue(this.cbfinCpzFrqCode);
		onSelectFrqCode(stmtFrqCode, this.cbfinCpzFrqCode, this.cbfinCpzFrqMth, this.cbfinCpzFrqDays, this.finCpzFrq, isReadOnly("FinanceTypeDialog_finCpzFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinCpzFrqMth(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = getComboboxValue(this.cbfinCpzFrqCode);
		String stmtFrqMonth = getComboboxValue(this.cbfinCpzFrqMth);
		onSelectFrqMth(stmtFrqCode, stmtFrqMonth, this.cbfinCpzFrqMth, this.cbfinCpzFrqDays, this.finCpzFrq, isReadOnly("FinanceTypeDialog_finCpzFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinCpzFrqDays(Event event) {
		logger.debug("Entering" + event.toString());
		onSelectFrqDay(cbfinCpzFrqCode, cbfinCpzFrqMth, cbfinCpzFrqDays, this.finCpzFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinRvwFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = getComboboxValue(this.cbfinRvwFrqCode);
		onSelectFrqCode(stmtFrqCode, this.cbfinRvwFrqCode, this.cbfinRvwFrqMth, this.cbfinRvwFrqDays, this.finRvwFrq, isReadOnly("FinanceTypeDialog_finRvwFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinRvwFrqMth(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = getComboboxValue(this.cbfinRvwFrqCode);
		String stmtFrqMonth = getComboboxValue(this.cbfinRvwFrqMth);
		onSelectFrqMth(stmtFrqCode, stmtFrqMonth, this.cbfinRvwFrqMth, this.cbfinRvwFrqDays, this.finRvwFrq, isReadOnly("FinanceTypeDialog_finRvwFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinRvwFrqDays(Event event) {
		logger.debug("Entering" + event.toString());
		onSelectFrqDay(cbfinRvwFrqCode, cbfinRvwFrqMth, cbfinRvwFrqDays, this.finRvwFrq);
		logger.debug("Leaving" + event.toString());
	}

	//=============================================== Hidden =========================================================//
	public void onSelect$cbfinDftStmtFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = getComboboxValue(this.cbfinDftStmtFrqCode);
		onSelectFrqCode(stmtFrqCode, this.cbfinDftStmtFrqCode, this.cbfinDftStmtFrqMth, this.cbfinDftStmtFrqDays, this.finDftStmtFrq, isReadOnly("FinanceTypeDialog_finDftStmtFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinDftStmtFrqMth(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = getComboboxValue(this.cbfinDftStmtFrqCode);
		String stmtFrqMonth = getComboboxValue(this.cbfinDftStmtFrqMth);
		onSelectFrqMth(stmtFrqCode, stmtFrqMonth, this.cbfinDftStmtFrqMth, this.cbfinDftStmtFrqDays, this.finDftStmtFrq, isReadOnly("FinanceTypeDialog_finDftStmtFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinDftStmtFrqDays(Event event) {
		logger.debug("Entering" + event.toString());
		onSelectFrqDay(cbfinDftStmtFrqCode, cbfinDftStmtFrqMth, cbfinDftStmtFrqDays, finDftStmtFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinDepreciationCode(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = getComboboxValue(this.cbfinDepreciationCode);
		onSelectFrqCode(stmtFrqCode, this.cbfinDepreciationCode, this.cbfinDepreciationMth, this.cbfinDepreciationDays, this.finDepreciationFrq,
		        isReadOnly("FinanceTypeDialog_finGrcCpzFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinDepreciationMth(Event event) {
		logger.debug("Entering" + event.toString());
		String stmtFrqCode = getComboboxValue(this.cbfinDepreciationCode);
		String stmtFrqMonth = getComboboxValue(this.cbfinDepreciationMth);
		onSelectFrqMth(stmtFrqCode, stmtFrqMonth, this.cbfinDepreciationMth, this.cbfinDepreciationDays, this.finDepreciationFrq, isReadOnly("FinanceTypeDialog_finGrcCpzFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbfinDepreciationDays(Event event) {
		logger.debug("Entering" + event.toString());
		onSelectFrqDay(cbfinDepreciationCode, cbfinDepreciationMth, cbfinDepreciationDays, this.finDepreciationFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$finCollateralReq(Event event) {
		logger.debug("Entering");
		doCheckBoxChecked(this.finCollateralReq.isChecked(), this.finCollateralOvrride);
		logger.debug("Leaving");
	}
	
	
	public void onClick$btnNew_FinTypeAccount(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Clients.clearWrongValue(this.listBoxFinTypeAccounts);

		// create a new IncomeExpenseDetail object, We GET it from the backEnd.
		final FinTypeAccount aFinTypeAccount = getFinanceTypeService().getNewFinTypeAccount();
		aFinTypeAccount.setFinType(this.finType.getValue());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finTypeAccount", aFinTypeAccount);
		map.put("financeTypeDialogCtrl", this);
		map.put("role", getRole());
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/FinTypeAccountDialog.zul", null, map);
		} catch (final Exception e) {
			e.printStackTrace();
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onFinTypeAccountItemDoubleClicked(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Listitem item = (Listitem) event.getOrigin().getTarget();
		FinTypeAccount itemdata = (FinTypeAccount) item.getAttribute("data");
		if (!StringUtils.trimToEmpty(itemdata.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			itemdata.setNewRecord(false);
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("finTypeAccount", itemdata);
			map.put("financeTypeDialogCtrl", this);
			map.put("role", getRole());
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/FinTypeAccountDialog.zul", null, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void doFillCustAccountTypes(List<FinTypeAccount> finTypeAccount) {
		logger.debug("Entering");
		try {
			if (finTypeAccount != null) {
				setFinTypeAccountList(finTypeAccount);
				fillCustAccountTypes(finTypeAccount);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
	}

	private void fillCustAccountTypes(List<FinTypeAccount> finTypeAccounts) {
		this.listBoxFinTypeAccounts.getItems().clear();
		for (FinTypeAccount finTypeAccount : finTypeAccounts) {
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(finTypeAccount.getFinCcy());
			lc.setParent(item);
			lc = new Listcell(PennantStaticListUtil.getlabelDesc(finTypeAccount.getEvent(), PennantStaticListUtil.getAccountEventsList()));
			lc.setParent(item);
			lc = new Listcell();
			Checkbox checkbox=new Checkbox();
			checkbox.setChecked(finTypeAccount.isAlwManualEntry());	
			checkbox.setDisabled(true);
			checkbox.setParent(lc);
			lc.setParent(item);
			lc = new Listcell();
			Checkbox isAlwCustAcc=new Checkbox();
			isAlwCustAcc.setChecked(finTypeAccount.isAlwCustomerAccount());	
			isAlwCustAcc.setDisabled(true);
			isAlwCustAcc.setParent(lc);
			lc.setParent(item);
			lc = new Listcell(finTypeAccount.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(finTypeAccount.getRecordType());
			lc.setParent(item);
			item.setAttribute("data", finTypeAccount);
		    ComponentsCtrl.applyForward(item, "onDoubleClick=onFinTypeAccountItemDoubleClicked");
			this.listBoxFinTypeAccounts.appendChild(item);
		}
	}
	

	public List<FinTypeAccount> getFinTypeAccountList() {
		return finTypeAccountList;
	}

	public void setFinTypeAccountList(List<FinTypeAccount> finTypeAccountList) {
		this.finTypeAccountList = finTypeAccountList;
	}

	/*
	 * onCheck Event For stepFinance
	 */
	public void onCheck$stepFinance(Event event){
		logger.debug("Entering : "+event.toString());  
		 setSteppingFieldsVisibility(this.stepFinance.isChecked());
	    logger.debug("Leaving : "+event.toString());  
	}
	
	
	/*
	 * onCheck Event For allowManualSteps
	 */
	public void onCheck$allowManualSteps(Event event) {
		logger.debug("Entering : " + event.toString());
		String sClass = "";
		if (!this.allowManualSteps.isChecked()) {
			sClass = "mandatory";
		}
		this.sp_alwdStepPolices.setSclass(sClass);
		this.sp_dftStepPolicy.setSclass(sClass);

		logger.debug("Leaving : " + event.toString());
	}
	
	/*
	 * onChange Event For Combobox defaultPolicie
	 */
	public void onChange$dftStepPolicy(Event event) {
		logger.debug("Entering : " + event.toString());
		
		if(!StringUtils.trimToEmpty(this.lovDescStepPolicyCodename.getValue()).equals("")){
			String polices = StringUtils.trimToEmpty(this.lovDescStepPolicyCodename.getValue());
			List<String> policyCodesList = Arrays.asList(polices.split(","));
			for(String policyCode : policyCodesList){
				if(!policyCode.equals(this.dftStepPolicy.getSelectedItem().getValue().toString()) && !policyCodesList.contains(this.dftStepPolicy.getSelectedItem().getValue().toString())){
					polices = polices+","+this.dftStepPolicy.getSelectedItem().getValue().toString();
					break;
				}
			}
			this.lovDescStepPolicyCodename.setValue(polices);
		} else {
			this.lovDescStepPolicyCodename.setValue(this.dftStepPolicy.getSelectedItem().getValue().toString());
		}
		
		logger.debug("Leaving : " + event.toString());
	}

	
  public void setSteppingFieldsVisibility(boolean isVisible){
		logger.debug("Entering");
		
		if (getComboboxValue(this.cbfinSchdMthd).equals(CalculationConstants.EQUAL)) {
			this.gb_SteppingDetails.setVisible(true);
		} else {
			this.gb_SteppingDetails.setVisible(false);
		}
		 this.label_FinanceTypeDialog_AllowedStepPolicies.setVisible(isVisible);
		 this.hbox_alwdStepPolicies.setVisible(isVisible);        
		 this.row_isSteppingMandatory.setVisible(isVisible);    
		 this.row_allowManualSteps.setVisible(isVisible); 
		 String sClass="";
		 if(!this.allowManualSteps.isChecked()){
			 sClass="mandatory";
		 }
		 this.sp_alwdStepPolices.setSclass(sClass);
		 this.sp_dftStepPolicy.setSclass(sClass);
		logger.debug("Leaving");
	 }
	
	public void onClick$btnSearchStepPolicy(Event event){
		logger.debug("Entering  "+event.toString());
		
		Textbox txtbx = (Textbox) btnSearchStepPolicy.getPreviousSibling();
		String selectedValues= (String) MultiSelectionSearchListBox.show(this.window_FinanceTypeDialog, "StepPolicyHeader",txtbx.getValue(),new Filter[]{});
      
		txtbx.setValue(selectedValues);        
        if(!StringUtils.trimToEmpty(selectedValues).equals("")){
        	List<String> polociesList = Arrays.asList(selectedValues.split(","));
        	if(!polociesList.contains(this.dftStepPolicy.getSelectedItem().getValue().toString())){
        		fillComboBox(this.dftStepPolicy, "", this.stepPolicies, "");
        	}
        } else {
        	fillComboBox(this.dftStepPolicy, "", this.stepPolicies, "");
        }
    	logger.debug("Leaving  "+event.toString());
	}

}
