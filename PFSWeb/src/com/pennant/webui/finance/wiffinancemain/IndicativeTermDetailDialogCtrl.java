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
 * FileName    		:  CarLoanDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-10-2011    														*
 *                                                                  						*
 * Modified Date    :  08-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-10-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.wiffinancemain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
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
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FacilityType;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.IndicativeTermDetail;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.PTCKeditor;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.TemplateEngine;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/CarLoanDetail/carLoanDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class IndicativeTermDetailDialogCtrl extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = 5058430665774376406L;
	private final static Logger logger = Logger.getLogger(IndicativeTermDetailDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_IndTermDetailDialog; 	// autowired
	
	protected Textbox rpsnName;						// autowired
	protected ExtendedCombobox rpsnDesg;			// autowired
	protected Textbox custName;						// autowired		
	protected Label custShrtName;					// autowired		
	protected Longbox custId;						// autowired		
	protected ExtendedCombobox facilityType;		// autowired
	protected Textbox pricing;						// autowired
	protected Textbox repayments;					// autowired
	protected Textbox lCPeriod;						// autowired
	protected Textbox usancePeriod;					// autowired
	protected Checkbox securityClean;				// autowired
	protected PTCKeditor securityName;				// autowired
	protected Textbox utilization;					 // autowired
	protected PTCKeditor commission;				 // autowired
	protected PTCKeditor purpose;					 // autowired
	protected PTCKeditor guarantee;				     // autowired
	protected PTCKeditor covenants;				     // autowired
	protected PTCKeditor documentsRequired;		     // autowired
	protected Intbox  tenorYear;                     // autowired
	protected Intbox  tenorMonth;                    // autowired
	protected Textbox tenorDesc;                     // autowired
	protected Combobox transactionType;              // autowired
	protected Textbox agentBank;                     // autowired
	protected Space space_AgentBank;                 // autowired
	protected CurrencyBox totalFacility;             // autowired
	protected ExtendedCombobox totalFacilityCCY;     // autowired
	protected CurrencyBox underWriting;              // autowired
	protected ExtendedCombobox underWritingCCY;      // autowired
	protected CurrencyBox propFinalTake;             // autowired
	protected ExtendedCombobox propFinalTakeCCY;     // autowired
	protected Textbox otherDetails;                  // autowired
	protected Space space_OtherDetails;              // autowired
	protected Row row_totalFacility;                 // autowired
	protected Row row_underWriting;                 // autowired
	protected Row row_propFinalTake;                 // autowired

	protected Label recordStatus; 					// autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected Button   btnGenerateTermSheet;
	
	protected Row row_LCPeriod;
	protected Row row_UsancePeriod;
	
	// For Dynamically calling of this Controller
	private Div toolbar;
	private Object financeMainDialogCtrl;
	private Tabpanel panel = null;
	
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient String oldVar_rpsnName;
	private transient String oldVar_rpsnDesg;
	private transient String oldVar_custName;
	private transient long oldVar_custId;
	private transient String oldVar_facilityType;
	private transient String oldVar_pricing;
	private transient String oldVar_repayments;
	private transient String oldVar_lCPeriod;
	private transient String oldVar_usancePeriod;
	private transient boolean oldVar_securityClean;
	private transient String oldVar_securityName;
	private transient String oldVar_utilization;
	private transient String oldVar_commission;
	private transient String oldVar_purpose;
	private transient String oldVar_guarantee;
	private transient String oldVar_covenants;
	private transient String oldVar_documentsRequired;
	private transient String oldVar_recordStatus;
	
	private transient int oldVar_tenorYear;
	private transient int oldVar_tenorMonth;
	private transient String oldVar_tenorDesc;
	private transient int 	 oldVar_transactionType;
	private transient String  oldVar_agentBank;
	private transient BigDecimal 	oldVar_totalFacility;
	private transient String  oldVar_totalFacilityCCY;
	private transient BigDecimal 	oldVar_underWriting;
	private transient String  oldVar_underWritingCCY;
	private transient BigDecimal 	oldVar_propFinalTake;
	private transient String  oldVar_propFinalTakeCCY;
	private transient String  oldVar_otherDetails;
	
	private boolean notes_Entered = false;
	private transient boolean newFinance;
	
	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_IndicativeTermDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire
	protected South south;
	
	// ServiceDAOs / Domain Classes
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	private transient boolean recSave = false;
	private IndicativeTermDetail indicativeTermDetail;
	private CustomerDetailsService customerDetailsService;
	private CustomerDetails customerDetails = null;
	private String userRole="";
	private List<ValueLabel> transactionTypesList = PennantStaticListUtil.getTransactionTypesList();
	
	/**
	 * default constructor.<br>
	 */
	public IndicativeTermDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CarLoanDetail object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onCreate$window_IndTermDetailDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		try {
  			/* set components visible dependent of the users rights */
			
			if (event.getTarget().getParent() != null) {
				panel = (Tabpanel) event.getTarget().getParent();
			}
			
			//Create the Button Controller. Disable not used buttons during working
			this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, 
					this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);
			
			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			
			// READ OVERHANDED params !
			if (args.containsKey("indicativeTermDetail")) {
				indicativeTermDetail = (IndicativeTermDetail) args.get("indicativeTermDetail");
			} else {
				indicativeTermDetail = new IndicativeTermDetail();
			}
			if (args.containsKey("financeMainDialogCtrl")) {
				this.financeMainDialogCtrl = (Object) args.get("financeMainDialogCtrl");
				
				try {
					financeMainDialogCtrl.getClass().getMethod("setIndicativeTermDetailDialogCtrl", 
							this.getClass()).invoke(financeMainDialogCtrl, this);
				} catch (Exception e) {
					logger.error(e);
				}
				
				setNewFinance(true);
				this.indicativeTermDetail.setWorkflowId(0);
				this.window_IndTermDetailDialog.setTitle("");
				
			}
			
			if (args.containsKey("roleCode")) {
				userRole = args.get("roleCode").toString();
				getUserWorkspace().alocateRoleAuthorities(userRole, "IndicativeTermDetailDialog");
			}
			
			doLoadWorkFlow(this.indicativeTermDetail.isWorkflow(), this.indicativeTermDetail.getWorkflowId(), this.indicativeTermDetail.getNextTaskId());
			/* set components visible dependent of the users rights */
			doCheckRights();
			
			if (isWorkFlowEnabled() && !isNewFinance()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "IndicativeTermDetailDialog");
			}
			
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getIndicativeTermDetail());
		} catch (Exception e) {
			logger.debug("Leaving" + e.getMessage());
			this.window_IndTermDetailDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		// Empty sent any required attributes
		this.rpsnName.setMaxlength(50);
		this.rpsnDesg.setMaxlength(8);
        this.rpsnDesg.setMandatoryStyle(true);
        this.rpsnDesg.setModuleName("GeneralDesignation");
		this.rpsnDesg.setValueColumn("GenDesignation");
		this.rpsnDesg.setDescColumn("GenDesgDesc");
		this.rpsnDesg.setValidateColumns(new String[] { "GenDesignation" });
		this.custName.setMaxlength(100);
		this.facilityType.setMaxlength(8);
        this.facilityType.setMandatoryStyle(true);
        this.facilityType.setModuleName("FacilityType");
		this.facilityType.setValueColumn("FacilityType");
		this.facilityType.setDescColumn("FacilityDesc");
		this.facilityType.setValidateColumns(new String[] { "FacilityType" });
		this.pricing.setMaxlength(200);
		this.repayments.setMaxlength(200);
		this.lCPeriod.setMaxlength(200);
		this.usancePeriod.setMaxlength(200);
		this.utilization.setMaxlength(200);
		this.tenorYear.setMaxlength(4);
		this.tenorMonth.setMaxlength(2);
		this.tenorDesc.setMaxlength(200);
		this.agentBank.setMaxlength(200);
		this.otherDetails.setMaxlength(200);
		this.totalFacility.setMaxlength(18);
		this.totalFacility.setMandatory(true);
		this.totalFacility.setFormat(PennantApplicationUtil.getAmountFormate(getIndicativeTermDetail().getLovDescTotalFacilityFormatter()));
		this.totalFacilityCCY.setMaxlength(3);
		this.totalFacilityCCY.setMandatoryStyle(true);
		this.totalFacilityCCY.setModuleName("Currency");
		this.totalFacilityCCY.setValueColumn("CcyCode");
		this.totalFacilityCCY.setDescColumn("CcyDesc");
		this.totalFacilityCCY.setValidateColumns(new String[] { "CcyCode" });
		this.underWriting.setMaxlength(18);
		this.underWriting.setMandatory(true);
		this.underWriting.setFormat(PennantApplicationUtil.getAmountFormate(getIndicativeTermDetail().getLovDescUnderWritingFormatter()));
		this.underWritingCCY.setMaxlength(3);
		this.underWritingCCY.setMandatoryStyle(true);
		this.underWritingCCY.setModuleName("Currency");
		this.underWritingCCY.setValueColumn("CcyCode");
		this.underWritingCCY.setDescColumn("CcyDesc");
		this.underWritingCCY.setValidateColumns(new String[] { "CcyCode" });
		this.propFinalTake.setMaxlength(18);
		this.propFinalTake.setMandatory(true);
		this.propFinalTake.setFormat(PennantApplicationUtil.getAmountFormate(getIndicativeTermDetail().getLovDescPropFinalTakeFormatter()));
		this.propFinalTakeCCY.setMaxlength(3);
		this.propFinalTakeCCY.setMandatoryStyle(true);
		this.propFinalTakeCCY.setModuleName("Currency");
		this.propFinalTakeCCY.setValueColumn("CcyCode");
		this.propFinalTakeCCY.setDescColumn("CcyDesc");
		this.propFinalTakeCCY.setValidateColumns(new String[] { "CcyCode" });
		
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving");
	}

	public void onFulfill$totalFacilityCCY(Event event) {
		logger.debug("Entering " + event.toString());
		Object dataObject = totalFacilityCCY.getObject();
		if (dataObject instanceof String) {
			this.totalFacilityCCY.setValue(dataObject.toString());
			this.totalFacilityCCY.setDescription("");
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				// To Format Amount based on the currency
				getIndicativeTermDetail().setLovDescTotalFacilityFormatter(details.getCcyEditField());
				this.totalFacility.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
			}
		}
		logger.debug("Leaving " + event.toString());
	}
	
	public void onFulfill$underWritingCCY(Event event) {
		logger.debug("Entering " + event.toString());
		Object dataObject = underWritingCCY.getObject();
		if (dataObject instanceof String) {
			this.underWritingCCY.setValue(dataObject.toString());
			this.underWritingCCY.setDescription("");
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				// To Format Amount based on the currency
				getIndicativeTermDetail().setLovDescUnderWritingFormatter(details.getCcyEditField());
				this.underWriting.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
			}
		}
		logger.debug("Leaving " + event.toString());
	}
	
	public void onFulfill$propFinalTakeCCY(Event event) {
		logger.debug("Entering " + event.toString());
		Object dataObject = propFinalTakeCCY.getObject();
		if (dataObject instanceof String) {
			this.propFinalTakeCCY.setValue(dataObject.toString());
			this.propFinalTakeCCY.setDescription("");
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				// To Format Amount based on the currency
				getIndicativeTermDetail().setLovDescPropFinalTakeFormatter(details.getCcyEditField());
				this.propFinalTake.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
			}
		}
		logger.debug("Leaving " + event.toString());
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
		getUserWorkspace().alocateAuthorities("IndicativeTermDetailDialog",userRole);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_IndicativeTermDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_IndicativeTermDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_IndicativeTermDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_IndicativeTermDetailDialog_btnSave"));
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
	public void onClose$window_IndTermDetailDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_IndTermDetailDialog);
		logger.debug("Leaving" + event.toString());
	}

	
	public void onClick$btnGenerateTermSheet(Event event) throws Exception{
		logger.debug("Entering" + event.toString());
		FinanceDetail detail = null;
		String finDivision = "";
  	    Date date = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);

		try {
			Object object = getFinanceMainDialogCtrl().getClass().getMethod("getFinanceDetail").invoke(financeMainDialogCtrl);
			if (object != null) {
				detail = (FinanceDetail) object;
			}
			if (detail!=null && detail.getFinScheduleData() != null && detail.getFinScheduleData().getFinanceMain() != null) {
				FinanceMain main = detail.getFinScheduleData().getFinanceMain();
				if(main != null){
					
					finDivision = detail.getFinScheduleData().getFinanceType().getFinDivision();
					doWriteComponentsToBean(indicativeTermDetail);
					int formatter = main.getLovDescFinFormatter();

					indicativeTermDetail.setFinAmount(PennantApplicationUtil.amountFormate(main.getFinAmount(), formatter));
					indicativeTermDetail.setFinPurpose(main.getFinPurpose());
					indicativeTermDetail.setTenor(indicativeTermDetail.getTenorYear()+" Years "+indicativeTermDetail.getTenorMonth()+" Months ");
					indicativeTermDetail.setFinCcy(main.getFinCcy());
					indicativeTermDetail.setAppDate(PennantAppUtil.formateDate(date, PennantConstants.dateFormate));
					int tempYear = Integer.parseInt(date.toString().substring(0,4));

					indicativeTermDetail.setAppLastYear(String.valueOf((tempYear-1)));
					indicativeTermDetail.setAppPastYear(String.valueOf((tempYear-2)));

					customerDetails = getCustomerDetailsService().getCustomerDetailsbyIdandPhoneType(main.getCustID(), "FAX");

					if(customerDetails.getCustomerPhoneNumList() != null && customerDetails.getCustomerPhoneNumList().size() > 0){
						indicativeTermDetail.setFax(customerDetails.getCustomerPhoneNumList().get(0).getPhoneNumber()
								== null ? "" : customerDetails.getCustomerPhoneNumList().get(0).getPhoneNumber());
					} else {
						indicativeTermDetail.setFax("");
					}
					if(customerDetails.getAddressList() != null && customerDetails.getAddressList().size() > 0){
						indicativeTermDetail.setCity(customerDetails.getAddressList().get(0).getCustAddrCity() 
								== null ? "" : customerDetails.getAddressList().get(0).getCustAddrCity());
					} else {
						indicativeTermDetail.setCity("");
					}

					if(customerDetails.getAddressList() != null && customerDetails.getAddressList().size() > 0){
						indicativeTermDetail.setCountry(customerDetails.getAddressList().get(0).getCustAddrCountry()
								== null ? "" : customerDetails.getAddressList().get(0).getCustAddrCountry());
					} else {
						indicativeTermDetail.setCountry("");
					}

					if(customerDetails.getAddressList() != null && customerDetails.getAddressList().size() > 0){
						indicativeTermDetail.setPoBox(customerDetails.getAddressList().get(0).getCustAddrZIP() 
								== null ? "" : customerDetails.getAddressList().get(0).getCustAddrZIP());
					} else {
						indicativeTermDetail.setPoBox("");
					}
				}
			}
		} catch (Exception e) {
			logger.debug(e);
			if (e instanceof WrongValuesException) {
				throw e;
			}
		}
		
		try {
			String sheetName = "";
			if(!StringUtils.trimToEmpty(indicativeTermDetail.getUsancePeriod()).equals("") && !StringUtils.trimToEmpty(indicativeTermDetail.getLCPeriod()).equals("")){
				sheetName = "IndicativeTermSheet_LC_Usance.docx";
			}else if(!StringUtils.trimToEmpty(indicativeTermDetail.getUsancePeriod()).equals("")){
				sheetName = "IndicativeTermSheet_Usance.docx";
			}else if(!StringUtils.trimToEmpty(indicativeTermDetail.getLCPeriod()).equals("")){
				sheetName = "IndicativeTermSheet_LC.docx";
			}else{
				sheetName = "IndicativeTermSheet.docx";
			}
			
			if(!StringUtils.trimToEmpty(finDivision).equals("")){
				sheetName = finDivision+"_"+sheetName;
			}
			
			TemplateEngine engine = new TemplateEngine(sheetName);
			String refNo =  detail.getFinScheduleData().getFinanceMain().getFinReference();
			String reportName = refNo + "_TermSheet.docx";
			engine.setTemplate("");
			engine.loadTemplateWithFontSize(11);
			engine.mergeFields(indicativeTermDetail);
			engine.showDocument(this.window_IndTermDetailDialog, reportName, SaveFormat.DOCX);
			engine.close();
			engine = null;
			
		} catch (Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.getMessage());
		}
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
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Event for checking validation for dynamically calling condition
	 * 
	 * @param event
	 */
	@SuppressWarnings({ "unchecked" })
	public void onAssetValidation(Event event) {
		logger.debug("Entering" + event.toString());
		String userAction = "";
		Map<String, Object> map = new HashMap<String, Object>();
		if (event.getData() != null) {
			map = (Map<String, Object>) event.getData();
		}
		if (map.containsKey("userAction")) {
			userAction = (String) map.get("userAction");
		}
		doClearMessage();
		recSave = false;
		if ("Save".equalsIgnoreCase(userAction) && !map.containsKey("agreement")) {
			recSave = true;
		} else {
			doSetValidation();
			doSetLOVValidation();
		}
		doWriteComponentsToBean(getIndicativeTermDetail());
		if (StringUtils.trimToEmpty(getIndicativeTermDetail().getRecordType()).equals("")) {
			getIndicativeTermDetail().setVersion(getIndicativeTermDetail().getVersion() + 1);
			getIndicativeTermDetail().setRecordType(PennantConstants.RECORD_TYPE_NEW);
			getIndicativeTermDetail().setNewRecord(true);
		}
	
		try {
			getFinanceMainDialogCtrl().getClass().getMethod("setIndicativeTermDetail", IndicativeTermDetail.class).invoke(
					getFinanceMainDialogCtrl(), this.getIndicativeTermDetail());
		} catch (Exception e) {
			logger.error(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Event for checking whethter data has been changed before closing
	 * 
	 * @param event
	 * @return
	 * */
	
	public void onAssetClose(Event event) {
		logger.debug("Entering" + event.toString());
			try {
				financeMainDialogCtrl.getClass().getMethod("setAssetDataChanged", Boolean.class).invoke(
						financeMainDialogCtrl, this.isDataChanged());
			} catch (Exception e) {
				logger.error(e);
			}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ GUI Process ++++++++++++++++++ //
	// ++++++++++++++++++++++++++++++++++++++++++++++++ //
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
			logger.debug("isDataChanged : true");
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
			logger.debug("isDataChanged : false");
		}
		if (close) {
			closeDialog(this.window_IndTermDetailDialog, "IndicativeTermDetailDialog");
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
	 * @param indicativeTermDetail
	 *            CarLoanDetail
	 */
	public void doWriteBeanToComponents(IndicativeTermDetail indicativeTermDetail) {
		logger.debug("Entering");
		
		this.rpsnName.setValue(indicativeTermDetail.getRpsnName());
		this.rpsnDesg.setValue(indicativeTermDetail.getRpsnDesg());
		this.rpsnDesg.setDescription(indicativeTermDetail.getLovDescRpsnDesgName());
		this.custName.setValue(indicativeTermDetail.getLovDescCustCIF());
		this.custShrtName.setValue(indicativeTermDetail.getLovDescCustShrtName());
		this.custId.setValue(indicativeTermDetail.getCustId());
		this.facilityType.setValue(indicativeTermDetail.getFacilityType());
		this.pricing.setValue(indicativeTermDetail.getPricing());
		this.repayments.setValue(indicativeTermDetail.getRepayments());
		this.lCPeriod.setValue(indicativeTermDetail.getLCPeriod());
		this.usancePeriod.setValue(indicativeTermDetail.getUsancePeriod());
		this.securityClean.setChecked(indicativeTermDetail.isSecurityClean());
		this.securityName.setValue(indicativeTermDetail.getSecurityName());
		this.utilization.setValue(indicativeTermDetail.getUtilization());
		this.commission.setValue(indicativeTermDetail.getCommission());
		this.purpose.setValue(indicativeTermDetail.getPurpose());
		this.guarantee.setValue(indicativeTermDetail.getGuarantee());
		this.covenants.setValue(indicativeTermDetail.getCovenants());
		this.documentsRequired.setValue(indicativeTermDetail.getDocumentsRequired());
		this.tenorYear.setValue(indicativeTermDetail.getTenorYear());
		this.tenorMonth.setValue(indicativeTermDetail.getTenorMonth());
		this.tenorDesc.setValue(indicativeTermDetail.getTenorDesc());
		onCheckSecurity();
		fillComboBox(this.transactionType, indicativeTermDetail.getTransactionType(),transactionTypesList, "");
		this.agentBank.setValue(indicativeTermDetail.getAgentBank());
		this.otherDetails.setValue(indicativeTermDetail.getOtherDetails());
		this.totalFacility.setValue(PennantAppUtil.formateAmount(indicativeTermDetail.getTotalFacility(), indicativeTermDetail.getLovDescTotalFacilityFormatter()));
		this.totalFacilityCCY.setValue(indicativeTermDetail.getTotalFacilityCCY());
		this.underWriting.setValue(PennantAppUtil.formateAmount(indicativeTermDetail.getUnderWriting(), indicativeTermDetail.getLovDescUnderWritingFormatter()));
		this.underWritingCCY.setValue(indicativeTermDetail.getUnderWritingCCY());
		this.propFinalTake.setValue(PennantAppUtil.formateAmount(indicativeTermDetail.getPropFinalTake(), indicativeTermDetail.getLovDescPropFinalTakeFormatter()));
		this.propFinalTakeCCY.setValue(indicativeTermDetail.getPropFinalTakeCCY());
		
		if (indicativeTermDetail.isNewRecord()) {
			this.facilityType.setDescription("");
			this.totalFacilityCCY.setDescription("");
			this.underWritingCCY.setDescription("");
			this.propFinalTakeCCY.setDescription("");
		} else {
			this.facilityType.setDescription(indicativeTermDetail.getLovDescFacilityType());
			this.totalFacilityCCY.setDescription(indicativeTermDetail.getLovDescTotalFacilityCCYName());
			this.underWritingCCY.setDescription(indicativeTermDetail.getLovDescUnderWritingCCYName());
			this.propFinalTakeCCY.setDescription(indicativeTermDetail.getLovDescPropFinalTakeCCYName());
		}
		doCheckTransactionType();
		this.recordStatus.setValue(indicativeTermDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	
	public void doFillScheduleData(FinanceDetail detail){
		logger.debug("Entering");
		
		FinanceMain main = detail.getFinScheduleData().getFinanceMain();
		
		String rate = PennantApplicationUtil.formatRate(main.getRepayProfitRate().doubleValue(), 2);
		String[] rateFields = new String[]{rate,  PennantAppUtil.getlabelDesc(main.getRepayRateBasis(),
				PennantStaticListUtil.getInterestRateType(true))};
		this.pricing.setValue(Labels.getLabel("label_IndTermDetailDialog_Pricing", rateFields));
		
		String[] descFields = new String[]{String.valueOf(main.getNumberOfTerms()), main.getScheduleMethod(), 
				FrequencyUtil.getFrequencyDetail(main.getRepayFrq()).getFrequencyDescription(),
				DateUtility.formatDate(main.getMaturityDate(), PennantConstants.dateFormate)};
		this.repayments.setValue(Labels.getLabel("label_IndTermDetailDialog_Repayments", descFields));
		
		if(detail.getFinScheduleData().getFinanceType().isFinIsAlwMD()){
			this.utilization.setValue(Labels.getLabel("label_IndTermDetailDialog_Utilization_MultiDisbursement"));
		}else{
			this.utilization.setValue(Labels.getLabel("label_IndTermDetailDialog_Utilization_SingleDisbursement"));
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aIndicativeTermDetail
	 */
	public void doWriteComponentsToBean(IndicativeTermDetail aIndicativeTermDetail) {
		logger.debug("Entering");
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			aIndicativeTermDetail.setRpsnName(this.rpsnName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setLovDescRpsnDesgName(this.rpsnDesg.getDescription());
			aIndicativeTermDetail.setRpsnDesg(this.rpsnDesg.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setLovDescCustShrtName(this.custName.getValue());
			aIndicativeTermDetail.setCustId(this.custId.longValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setLovDescFacilityType(this.facilityType.getDescription());
			aIndicativeTermDetail.setFacilityType(this.facilityType.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setPricing(this.pricing.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setRepayments(this.repayments.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setLCPeriod(this.lCPeriod.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setUsancePeriod(this.usancePeriod.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setSecurityClean(this.securityClean.isChecked());
			aIndicativeTermDetail.setSecurityName(this.securityName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setUtilization(this.utilization.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setCommission(this.commission.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setPurpose(this.purpose.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setCovenants(this.covenants.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setDocumentsRequired(this.documentsRequired.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aIndicativeTermDetail.setGuarantee(this.guarantee.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(!this.tenorYear.isReadonly() && this.tenorYear.intValue() == 0 && this.tenorMonth.intValue() == 0){
				this.tenorYear.setConstraint(new  PTNumberValidator(Labels.getLabel("label_IndTermDetailDialog_tenorYear.value") , true, false));
			} 
			aIndicativeTermDetail.setTenorYear(this.tenorYear.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aIndicativeTermDetail.setTenorMonth(this.tenorMonth.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(!this.tenorDesc.getValue().equals("")){
			aIndicativeTermDetail.setTenorDesc(this.tenorDesc.getValue());
			} else if(!this.tenorDesc.isReadonly()){
				throw new WrongValueException( this.tenorDesc, Labels.getLabel( "FIELD_NO_EMPTY",
						new String[] {Labels.getLabel("label_IndTermDetailDialog_tenorDesc.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (getComboboxValue(this.transactionType).equals("#")) {
				throw new WrongValueException(this.transactionType, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_IndTermDetailDialog_transactionType.value") }));
			}

			aIndicativeTermDetail.setTransactionType(getComboboxValue(this.transactionType));

		} catch (WrongValueException we) {
			wve.add(we);
		}
		

		try {
			aIndicativeTermDetail.setAgentBank(this.agentBank.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
	
		try {
			aIndicativeTermDetail.setOtherDetails(this.otherDetails.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aIndicativeTermDetail.setTotalFacility(PennantAppUtil.unFormateAmount(this.totalFacility.getValue(), getIndicativeTermDetail().getLovDescTotalFacilityFormatter()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aIndicativeTermDetail.setLovDescTotalFacilityCCYName(this.totalFacilityCCY.getDescription());
			aIndicativeTermDetail.setTotalFacilityCCY(this.totalFacilityCCY.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aIndicativeTermDetail.setUnderWriting(PennantAppUtil.unFormateAmount(this.underWriting.getValue(), getIndicativeTermDetail().getLovDescUnderWritingFormatter()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aIndicativeTermDetail.setLovDescUnderWritingCCYName(this.underWritingCCY.getDescription());
			aIndicativeTermDetail.setUnderWritingCCY(this.underWritingCCY.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		

		try {
			aIndicativeTermDetail.setPropFinalTake(PennantAppUtil.unFormateAmount(this.propFinalTake.getValue(), getIndicativeTermDetail().getLovDescPropFinalTakeFormatter()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aIndicativeTermDetail.setLovDescPropFinalTakeCCYName(this.propFinalTakeCCY.getDescription());
			aIndicativeTermDetail.setPropFinalTakeCCY(this.propFinalTakeCCY.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		doRemoveValidation();
		doRemoveLOVValidation();
		if (!recSave) {
			if (wve.size() > 0) {
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = (WrongValueException) wve.get(i);
				}
				if (panel != null) {
					((Tab) panel.getParent().getParent().getFellowIfAny("indicativeTermTab")).setSelected(true);
				}
				throw new WrongValuesException(wvea);
			}
		}
		aIndicativeTermDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param indicativeTermDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(IndicativeTermDetail indicativeTermDetail) throws InterruptedException {
		logger.debug("Entering");
		
		// if aCarLoanDetail == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (indicativeTermDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			indicativeTermDetail = new IndicativeTermDetail();
			setIndicativeTermDetail(indicativeTermDetail);
		} else {
			setIndicativeTermDetail(indicativeTermDetail);
		}
		
		// set ReadOnly mode accordingly if the object is new or not.
		if (indicativeTermDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			
			if (isNewFinance()) {
				doEdit();
			} else if (isWorkFlowEnabled()) {
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
			doWriteBeanToComponents(indicativeTermDetail);
			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			
			if (panel != null) {
				this.toolbar.setVisible(false);
				this.groupboxWf.setVisible(false);
				this.window_IndTermDetailDialog.setHeight((calculateBorderLayoutHeight()-50)+ "px");
				this.south.setHeight("0px");
				panel.appendChild(this.window_IndTermDetailDialog);
			} else {
				setDialog(this.window_IndTermDetailDialog);
			}
		} catch (final Exception e) {
			logger.error(e);
			e.printStackTrace();
			PTMessageUtils.showErrorMessage(e.toString());
			this.window_IndTermDetailDialog.onClose();
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Stores the initial values in member vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");

		this.oldVar_rpsnName = this.rpsnName.getValue();
		this.oldVar_rpsnDesg = this.rpsnDesg.getValue();
		this.oldVar_custName = this.custName.getValue();
		this.oldVar_custId = this.custId.longValue();
		this.oldVar_facilityType = this.facilityType.getValue();
		this.oldVar_pricing = this.pricing.getValue();
		this.oldVar_repayments = this.repayments.getValue();
		this.oldVar_lCPeriod = this.lCPeriod.getValue();
		this.oldVar_usancePeriod = this.usancePeriod.getValue();
		this.oldVar_securityClean = this.securityClean.isChecked();
		this.oldVar_securityName = this.securityName.getValue();
		this.oldVar_utilization = this.utilization.getValue();
		this.oldVar_commission = this.commission.getValue();
		this.oldVar_purpose = this.purpose.getValue();
		this.oldVar_guarantee = this.guarantee.getValue();
		this.oldVar_covenants = this.covenants.getValue();
		this.oldVar_documentsRequired = this.documentsRequired.getValue();
		this.oldVar_tenorYear = this.tenorYear.intValue();
		this.oldVar_tenorMonth = this.tenorMonth.intValue();
		this.oldVar_tenorDesc = this.tenorDesc.getValue();
		this.oldVar_transactionType = this.transactionType.getSelectedIndex();
		this.oldVar_agentBank = this.agentBank.getValue();
		this.oldVar_otherDetails = this.otherDetails.getValue();
		this.oldVar_totalFacility = this.totalFacility.getValue();
		this.oldVar_totalFacilityCCY = this.totalFacilityCCY.getValue();
		this.oldVar_underWriting = this.underWriting.getValue();
		this.oldVar_underWritingCCY = this.underWritingCCY.getValue();
		this.oldVar_propFinalTake = this.propFinalTake.getValue();
		this.oldVar_propFinalTakeCCY = this.propFinalTakeCCY.getValue();
		
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");

		this.rpsnName.setValue(this.oldVar_rpsnName);
		this.rpsnDesg.setValue(this.oldVar_rpsnDesg);
		this.custName.setValue(this.oldVar_custName);
		this.custId.setValue(this.oldVar_custId);
		this.facilityType.setValue(this.oldVar_facilityType);
		this.pricing.setValue(this.oldVar_pricing);
		this.repayments.setValue(this.oldVar_repayments);
		this.lCPeriod.setValue(this.oldVar_lCPeriod);
		this.usancePeriod.setValue(this.oldVar_usancePeriod);
		this.securityClean.setChecked(this.oldVar_securityClean);
		this.securityName.setValue(this.oldVar_securityName);
		this.utilization.setValue(this.oldVar_utilization);
		this.commission.setValue(this.oldVar_commission);
		this.purpose.setValue(this.oldVar_purpose);
		this.guarantee.setValue(this.oldVar_guarantee);
		this.covenants.setValue(this.oldVar_covenants);
		this.documentsRequired.setValue(this.oldVar_documentsRequired);
		this.tenorYear.setValue(this.oldVar_tenorYear);
		this.tenorMonth.setValue(this.oldVar_tenorMonth);
		this.tenorDesc.setValue(this.oldVar_tenorDesc);
		this.transactionType.setSelectedIndex(this.oldVar_transactionType);
		this.agentBank.setValue(this.oldVar_agentBank);
		this.otherDetails.setValue(this.oldVar_otherDetails);
		this.totalFacility.setValue(this.oldVar_totalFacility);
		this.totalFacilityCCY.setValue(this.oldVar_totalFacilityCCY);
		this.underWriting.setValue(this.oldVar_underWriting);
		this.underWritingCCY.setValue(this.oldVar_underWritingCCY);
		this.propFinalTake.setValue(this.oldVar_propFinalTake);
		this.propFinalTakeCCY.setValue(this.oldVar_propFinalTakeCCY);
		
		this.recordStatus.setValue(this.oldVar_recordStatus);
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
		doClearMessage();
		if (this.oldVar_rpsnName != this.rpsnName.getValue()) {
			return true;
		}
		if (this.oldVar_rpsnDesg != this.rpsnDesg.getValue()) {
			return true;
		}
		if (this.oldVar_custName != this.custName.getValue()) {
			return true;
		}
		if (this.oldVar_facilityType != this.facilityType.getValue()) {
			return true;
		}
		if (this.oldVar_pricing != this.pricing.getValue()) {
			return true;
		}
		if (this.oldVar_repayments != this.repayments.getValue()) {
			return true;
		}
		if (this.oldVar_lCPeriod != this.lCPeriod.getValue()) {
			return true;
		}
		if (this.oldVar_usancePeriod != this.usancePeriod.getValue()) {
			return true;
		}
		if (this.oldVar_securityClean != this.securityClean.isChecked()) {
			return true;
		}
		if (this.oldVar_securityName != this.securityName.getValue()) {
			return true;
		}
		if (this.oldVar_utilization != this.utilization.getValue()) {
			return true;
		}
		if (this.oldVar_commission != this.commission.getValue()) {
			return true;
		}
		if (this.oldVar_purpose != this.purpose.getValue()) {
			return true;
		}
		if (this.oldVar_guarantee != this.guarantee.getValue()) {
			return true;
		}
		if (this.oldVar_covenants != this.covenants.getValue()) {
			return true;
		}
		if (this.oldVar_documentsRequired != this.documentsRequired.getValue()) {
			return true;
		}
		if (this.oldVar_tenorYear != this.tenorYear.intValue()) {
			return true;
		}
		if (this.oldVar_tenorMonth != this.tenorMonth.intValue()) {
			return true;
		}
		if (this.oldVar_tenorDesc != this.tenorDesc.getValue()) {
			return true;
		}
		if (this.oldVar_transactionType != this.transactionType.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_agentBank != this.agentBank.getValue()) {
			return true;
		}
		if (this.oldVar_otherDetails != this.otherDetails.getValue()) {
			return true;
		}
		if (this.oldVar_totalFacility != this.totalFacility.getValue()) {
			return true;
		}
		if (this.oldVar_totalFacilityCCY != this.totalFacilityCCY.getValue()) {
			return true;
		}
		if (this.oldVar_underWriting != this.underWriting.getValue()) {
			return true;
		}
		if (this.oldVar_underWritingCCY != this.underWritingCCY.getValue()) {
			return true;
		}
		if (this.oldVar_propFinalTake != this.propFinalTake.getValue()) {
			return true;
		}
		if (this.oldVar_propFinalTakeCCY != this.propFinalTakeCCY.getValue()) {
			return true;
		}
		
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		boolean isTranTypeSyndication = this.transactionType.getSelectedItem().getValue().equals(PennantConstants.FACILITY_TRAN_SYNDIACTION);
		
		if (!this.rpsnName.isReadonly()) {
			this.rpsnName.setConstraint(new PTStringValidator(Labels.getLabel("label_IndTermDetailDialog_RpsnName.value") ,
					PennantRegularExpressions.REGEX_NAME, true));
		}
		if (!this.pricing.isReadonly()) {
			this.pricing.setConstraint(new PTStringValidator(Labels.getLabel("label_IndTermDetailDialog_Pricing.value") ,
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, false));
		}
		if (!this.repayments.isReadonly()) {
			this.repayments.setConstraint(new PTStringValidator(Labels.getLabel("label_IndTermDetailDialog_Repayments.value") ,
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, false));
		}
		if (!this.lCPeriod.isReadonly()) {
			this.lCPeriod.setConstraint(new PTStringValidator(Labels.getLabel("label_IndTermDetailDialog_LCPeriod.value") ,
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, false));
		}
		if (!this.usancePeriod.isReadonly()) {
			this.usancePeriod.setConstraint(new PTStringValidator(Labels.getLabel("label_IndTermDetailDialog_UsancePeriod.value") ,
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, false));
		}
		if (!this.utilization.isReadonly()) {
			this.utilization.setConstraint(new PTStringValidator(Labels.getLabel("label_IndTermDetailDialog_Utilization.value") ,
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, false));
		}
		if (!this.tenorYear.isReadonly()) {
			this.tenorYear.setConstraint(new PTNumberValidator(Labels.getLabel("label_IndTermDetailDialog_tenorYear.value") , false, false));
		}
		if (!this.tenorMonth.isReadonly()) {
			this.tenorMonth.setConstraint(new  PTNumberValidator(Labels.getLabel("label_IndTermDetailDialog_tenorMonth.value") , false, false, 0, 11));
		}
		if (!this.tenorDesc.isReadonly()) {
			this.tenorDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_IndTermDetailDialog_tenorDesc.value") ,
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, false));
		}
		if(isTranTypeSyndication){
			if (!this.totalFacilityCCY.isReadonly()) {
				this.totalFacilityCCY.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
						new String[] { Labels.getLabel("label_IndTermDetailDialog_totalFacilityCCY.value") }));
			}
			if (!this.underWritingCCY.isReadonly()) {
				this.underWritingCCY.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
						new String[] { Labels.getLabel("label_IndTermDetailDialog_underWritingCCY.value") }));
			}
			if (!this.propFinalTakeCCY.isReadonly()) {
				this.propFinalTakeCCY.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
						new String[] { Labels.getLabel("label_IndTermDetailDialog_propFinalTakeCCY.value") }));
			}
			if (!this.totalFacility.isReadonly()) {
				this.totalFacility.setConstraint(new PTDecimalValidator(Labels.getLabel("label_IndTermDetailDialog_totalFacility.value"),
						getIndicativeTermDetail().getLovDescTotalFacilityFormatter(),true,false));
			}
			if (!this.underWriting.isReadonly()) {
				this.underWriting.setConstraint(new PTDecimalValidator(Labels.getLabel("label_IndTermDetailDialog_underWriting.value"),
						getIndicativeTermDetail().getLovDescUnderWritingFormatter(),true,false));
			}
			if (!this.propFinalTake.isReadonly()) {
				this.propFinalTake.setConstraint(new PTDecimalValidator(Labels.getLabel("label_IndTermDetailDialog_propFinalTake.value"),
						getIndicativeTermDetail().getLovDescPropFinalTakeFormatter(),true,false));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.pricing.setConstraint("");
		this.repayments.setConstraint("");
		this.lCPeriod.setConstraint("");
		this.usancePeriod.setConstraint("");
		this.utilization.setConstraint("");
		this.tenorYear.setConstraint("");
		this.tenorMonth.setConstraint("");
		this.tenorDesc.setConstraint("");
		this.agentBank.setConstraint("");
		this.otherDetails.setConstraint("");
		this.totalFacility.setConstraint("");
		this.totalFacilityCCY.setConstraint("");
		this.underWriting.setConstraint("");
		this.underWritingCCY.setConstraint("");
		this.propFinalTake.setConstraint("");
		this.propFinalTakeCCY.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for set constraints of LOV fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.rpsnDesg.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_IndTermDetailDialog_RpsnDesg.value") }));
		this.facilityType.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_IndTermDetailDialog_FacilityType.value") }));
		logger.debug("Leaving");
	}

	/**
	 * Method for remove constraints of LOV fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.rpsnDesg.setConstraint("");
		this.facilityType.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for clear Error messages to Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.rpsnName.setErrorMessage("");
		this.pricing.setErrorMessage("");
		this.repayments.setErrorMessage("");
		this.lCPeriod.setErrorMessage("");
		this.usancePeriod.setErrorMessage("");
		this.utilization.setErrorMessage("");
		this.rpsnDesg.setErrorMessage("");
		this.facilityType.setErrorMessage("");
		this.tenorYear.setErrorMessage("");
		this.tenorMonth.setErrorMessage("");
		this.tenorDesc.setErrorMessage("");
		this.transactionType.setErrorMessage("");
		this.agentBank.setErrorMessage("");
		this.otherDetails.setErrorMessage("");
		this.totalFacility.setErrorMessage("");
		this.totalFacilityCCY.setErrorMessage("");
		this.underWriting.setErrorMessage("");
		this.underWritingCCY.setErrorMessage("");
		this.propFinalTake.setErrorMessage("");
		this.propFinalTakeCCY.setErrorMessage("");
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful update
	private void refreshList() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Deletes a CarLoanDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final IndicativeTermDetail aIndicativeTermDetail = new IndicativeTermDetail();
		BeanUtils.copyProperties(getIndicativeTermDetail(), aIndicativeTermDetail);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aIndicativeTermDetail.getFinReference();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));
		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");
			if (StringUtils.trimToEmpty(aIndicativeTermDetail.getRecordType()).equals("")) {
				aIndicativeTermDetail.setVersion(aIndicativeTermDetail.getVersion() + 1);
				aIndicativeTermDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aIndicativeTermDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aIndicativeTermDetail, tranType)) {
					refreshList();
					closeDialog(this.window_IndTermDetailDialog, "IndicativeTermDetailDialog");
				}
			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CarLoanDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old vars
		doStoreInitValues();
		final IndicativeTermDetail aIndicativeTermDetail = new IndicativeTermDetail();
		setIndicativeTermDetail(aIndicativeTermDetail);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.pricing.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getIndicativeTermDetail().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		this.rpsnName.setReadonly(isReadOnly("IndicativeTermDetailDialog_rpsnName"));
		this.rpsnDesg.setReadonly(isReadOnly("IndicativeTermDetailDialog_rpsnDesg"));
		this.custName.setReadonly(true);
		this.custId.setReadonly(true);
		this.facilityType.setReadonly(isReadOnly("IndicativeTermDetailDialog_facilityType"));
		this.pricing.setReadonly(isReadOnly("IndicativeTermDetailDialog_pricing"));
		this.repayments.setReadonly(isReadOnly("IndicativeTermDetailDialog_repayments"));
		this.lCPeriod.setReadonly(isReadOnly("IndicativeTermDetailDialog_lCPeriod"));
		this.usancePeriod.setReadonly(isReadOnly("IndicativeTermDetailDialog_usancePeriod"));
		this.securityClean.setDisabled(isReadOnly("IndicativeTermDetailDialog_securityClean"));
		this.securityName.setReadonly(isReadOnly("IndicativeTermDetailDialog_securityName"));
		this.utilization.setReadonly(isReadOnly("IndicativeTermDetailDialog_utilization"));
		this.commission.setReadonly(isReadOnly("IndicativeTermDetailDialog_commission"));
		this.purpose.setReadonly(isReadOnly("IndicativeTermDetailDialog_purpose"));
		this.guarantee.setReadonly(isReadOnly("IndicativeTermDetailDialog_guarantee"));
		this.covenants.setReadonly(isReadOnly("IndicativeTermDetailDialog_covenants"));
		this.documentsRequired.setReadonly(isReadOnly("IndicativeTermDetailDialog_documentsRequired"));
		this.tenorYear.setReadonly(isReadOnly("IndicativeTermDetailDialog_tenorYear"));
		this.tenorMonth.setReadonly(isReadOnly("IndicativeTermDetailDialog_tenorMonth"));
		this.tenorDesc.setReadonly(isReadOnly("IndicativeTermDetailDialog_tenorDesc"));
		readOnlyComponent(isReadOnly("IndicativeTermDetailDialog_transactionType"), this.transactionType);
		readOnlyComponent(isReadOnly("IndicativeTermDetailDialog_agentBank"), this.agentBank);
		readOnlyComponent(isReadOnly("IndicativeTermDetailDialog_otherDetails"), this.otherDetails);
		readOnlyComponent(isReadOnly("IndicativeTermDetailDialog_totalFacility"), this.totalFacility);
		readOnlyComponent(isReadOnly("IndicativeTermDetailDialog_totalFacilityCCY"), this.totalFacilityCCY);
		readOnlyComponent(isReadOnly("IndicativeTermDetailDialog_underWriting"), this.underWriting);
		readOnlyComponent(isReadOnly("IndicativeTermDetailDialog_underWritingCCY"), this.underWritingCCY);
		readOnlyComponent(isReadOnly("IndicativeTermDetailDialog_propFinalTake"), this.propFinalTake);
		readOnlyComponent(isReadOnly("IndicativeTermDetailDialog_propFinalTakeCCY"), this.propFinalTakeCCY);
		
	//	this.btnGenerateTermSheet.setDisabled(isReadOnly("IndicativeTermDetailDialog_btnGenerateTermSheet"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.indicativeTermDetail.isNewRecord()) {
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

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || isNewFinance()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.rpsnName.setReadonly(true);
		this.rpsnDesg.setReadonly(true);
		this.custName.setReadonly(true);
		this.custId.setReadonly(true);
		this.facilityType.setReadonly(true);
		this.pricing.setReadonly(true);
		this.repayments.setReadonly(true);
		this.lCPeriod.setReadonly(true);
		this.usancePeriod.setReadonly(true);
		this.securityClean.setDisabled(true);
		this.securityName.setReadonly(true);
		this.utilization.setReadonly(true);
		this.commission.setReadonly(true);
		this.purpose.setReadonly(true);
		this.guarantee.setReadonly(true);
		this.covenants.setReadonly(true);
		this.documentsRequired.setReadonly(true);
		this.tenorYear.setReadonly(true);
		this.tenorMonth.setReadonly(true);
		this.tenorDesc.setReadonly(true);
		this.transactionType.setDisabled(true);
		this.agentBank.setReadonly(true);
		this.otherDetails.setReadonly(true);
		this.totalFacility.setDisabled(true);
		this.totalFacilityCCY.setReadonly(true);
		this.underWriting.setDisabled(true);
		this.underWritingCCY.setReadonly(true);
		this.propFinalTake.setDisabled(true);
		this.propFinalTakeCCY.setReadonly(true);
		
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
		this.rpsnName.setValue("");
		this.rpsnDesg.setValue("");
		this.custName.setValue("");
		this.custId.setValue(new Long(0));
		this.facilityType.setValue("");
		this.facilityType.setDescription("");
		this.pricing.setValue("");
		this.repayments.setValue("");
		this.lCPeriod.setValue("");
		this.usancePeriod.setValue("");
		this.securityClean.setChecked(false);
		this.securityName.setValue("");
		this.utilization.setValue("");
		this.commission.setValue("");
		this.purpose.setValue("");
		this.guarantee.setValue("");
		this.covenants.setValue("");
		this.documentsRequired.setValue("");
		this.tenorYear.setText("");
		this.tenorMonth.setText("");
		this.tenorDesc.setText("");
		this.agentBank.setValue("");
		this.otherDetails.setValue("");
		this.totalFacility.setValue("");
		this.totalFacilityCCY.setValue("");
		this.underWriting.setValue("");
		this.underWritingCCY.setValue("");
		this.propFinalTake.setValue("");
		this.propFinalTakeCCY.setValue("");
		
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final IndicativeTermDetail aIndicativeTermDetail = new IndicativeTermDetail();
		BeanUtils.copyProperties(getIndicativeTermDetail(), aIndicativeTermDetail);
		boolean isNew = false;
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		doSetLOVValidation();
		// fill the CarLoanDetail object with the components data
		doWriteComponentsToBean(aIndicativeTermDetail);
		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here
		isNew = aIndicativeTermDetail.isNew();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aIndicativeTermDetail.getRecordType()).equals("")) {
				aIndicativeTermDetail.setVersion(aIndicativeTermDetail.getVersion() + 1);
				if (isNew) {
					aIndicativeTermDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aIndicativeTermDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aIndicativeTermDetail.setNewRecord(true);
				}
			}
		} else {
			aIndicativeTermDetail.setVersion(aIndicativeTermDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aIndicativeTermDetail, tranType)) {
				refreshList();
				closeDialog(this.window_IndTermDetailDialog, "IndicativeTermDetailDialog");
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
	 * @param aIndicativeTermDetail
	 *            (CarLoanDetail)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(IndicativeTermDetail aIndicativeTermDetail, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";
		aIndicativeTermDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aIndicativeTermDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aIndicativeTermDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());
		if (isWorkFlowEnabled()) {
			
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			aIndicativeTermDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aIndicativeTermDetail.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aIndicativeTermDetail);
				}
				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aIndicativeTermDetail))) {
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
			aIndicativeTermDetail.setTaskId(taskId);
			aIndicativeTermDetail.setNextTaskId(nextTaskId);
			aIndicativeTermDetail.setRoleCode(getRole());
			aIndicativeTermDetail.setNextRoleCode(nextRoleCode);
			auditHeader = getAuditHeader(aIndicativeTermDetail, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId, aIndicativeTermDetail);
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");
				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aIndicativeTermDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aIndicativeTermDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
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
	@SuppressWarnings("unused")
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;
		IndicativeTermDetail aIndicativeTermDetail = (IndicativeTermDetail) auditHeader.getAuditDetail().getModelData();
		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
				} else {
				}
				auditHeader = ErrorControl.showErrorDetails(this.window_IndTermDetailDialog, auditHeader);
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
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++ Search Button Component Events+++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void onFulfill$facilityType(Event event) {
		logger.debug("Entering" + event.toString());
		
		Object dataObject = facilityType.getObject();
		if (dataObject instanceof String) {
			this.facilityType.setValue("");
			this.facilityType.setDescription("");
		} else {
			FacilityType detail = (FacilityType) dataObject;
			if (detail != null) {
				this.facilityType.setValue(detail.getFacilityType());
				this.facilityType.setDescription(detail.getFacilityDesc());
				
				if("L".equals(detail.getFacilityFor())){
					this.row_LCPeriod.setVisible(true);
					this.row_UsancePeriod.setVisible(true);
				}else{
					this.row_LCPeriod.setVisible(false);
					this.row_UsancePeriod.setVisible(false);
					this.lCPeriod.setValue("");
					this.usancePeriod.setValue("");
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onCheck$securityClean(Event event){
		logger.debug("Entering" + event.toString());
		onCheckSecurity();
		logger.debug("Leaving" + event.toString());
	}
	
	private void onCheckSecurity(){
		if(this.securityClean.isChecked()){
			this.securityName.setReadonly(true);
			this.securityName.setValue("");
		}else{
			this.securityName.setReadonly(isReadOnly("IndicativeTermDetailDialog_securityName"));
		}
	}

	public void doCheckTransactionType(){
		logger.debug("Entering");
		doClearMessage();
		boolean isTranTypeSyndiation = this.transactionType.getSelectedItem().getValue().equals(PennantConstants.FACILITY_TRAN_SYNDIACTION);
		if(isTranTypeSyndiation){
			this.row_totalFacility.setVisible(true);
			this.row_underWriting.setVisible(true);
			this.row_propFinalTake.setVisible(true);
		}else{
			this.row_totalFacility.setVisible(false);
			this.row_underWriting.setVisible(false);
			this.row_propFinalTake.setVisible(false);
			this.totalFacility.setValue(BigDecimal.ZERO);
			this.underWriting.setValue(BigDecimal.ZERO);
			this.propFinalTake.setValue(BigDecimal.ZERO);
			this.totalFacilityCCY.setValue("");
			this.totalFacilityCCY.setDescription("");
			this.underWritingCCY.setValue("");
			this.underWritingCCY.setDescription("");
			this.propFinalTakeCCY.setValue("");
			this.propFinalTakeCCY.setDescription("");
		}
		logger.debug("Leaving");
	}
	
	public void onChange$transactionType(Event event){
		logger.debug("Entering" + event.toString());
		doCheckTransactionType();
		logger.debug("Leaving" + event.toString());
	}
	
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	/**
	 * Get Audit Header Details
	 * 
	 * @param aIndicativeTermDetail
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(IndicativeTermDetail aIndicativeTermDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aIndicativeTermDetail.getBefImage(), aIndicativeTermDetail);
		return new AuditHeader(String.valueOf(aIndicativeTermDetail.getFinReference()), null, null, null,
				auditDetail, aIndicativeTermDetail.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_IndTermDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
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

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("CarLoanDetail");
		notes.setReference(String.valueOf(getIndicativeTermDetail().getFinReference()));
		notes.setVersion(getIndicativeTermDetail().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public boolean isNewFinance() {
		return newFinance;
	}
	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public IndicativeTermDetail getIndicativeTermDetail() {
		return indicativeTermDetail;
	}
	public void setIndicativeTermDetail(IndicativeTermDetail indicativeTermDetail) {
		this.indicativeTermDetail = indicativeTermDetail;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}
	
}
