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
 * FileName    		:  EtihadCreditBureauDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-10-2011    														*
 *                                                                  						*
 * Modified Date    :  13-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.financemain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.EtihadCreditBureauDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.service.finance.EtihadCreditBureauDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/EtihadCreditBureauDetail/etihadCreditBureauDetailDialog.zul file.
 */
public class EtihadCreditBureauDetailDialogCtrl extends GFCBaseCtrl<EtihadCreditBureauDetail> {
	private static final long serialVersionUID = 3141943554064485540L;
	private static final Logger logger = Logger.getLogger(EtihadCreditBureauDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_EtihadCreditBureauDetailDialog;// autowired
	protected Groupbox 		gb_basicDetails; 			// autowired
	protected Textbox 		finReference; 				// autowired

	protected Textbox 		worstCurrPayDelay; 		    // autowired
	protected Textbox 		worstPayDelay; 			    // autowired
	protected Textbox 		worstStatus; 			    // autowired
	protected Intbox 		bureauScore; 				// autowired
	protected Intbox 		defaultContracts; 			// autowired
	protected CurrencyBox 	totOutstandingAmt; 			// autowired
	protected CurrencyBox 	totOverdueAmt; 			    // autowired
	protected CurrencyBox 	totMonthlyInst; 			// autowired
	protected Textbox    	otherBankFinType; 		    // autowired
	protected Datebox    	oldConStartDate; 		    // autowired
	protected Datebox    	newConStartDate; 		    // autowired
	protected Intbox 		noOfInquiry; 			    // autowired
	protected Intbox 		noOfContractsInst; 			// autowired
	protected Intbox 		noOfContractsNonInst; 	    // autowired
	protected Intbox 		noOfContractsCredit; 	    // autowired

	protected Space         space_bureauScore;		     // autowired
	protected Space         space_defaultContracts;		 // autowired
	protected Space         space_worstCurrPayDelay;	 // autowired
	protected Space         space_worstPayDelay;		 // autowired
	protected Space         space_worstStatus;		     // autowired
	protected Space         space_oldConStartDate;		 // autowired
	protected Space         space_newConStartDate;		 // autowired
	protected Space         space_otherBankFinType;		 // autowired
	protected Space         space_noOfInquiry;		     // autowired
	protected Space         space_noOfContractsInst;	 // autowired
	protected Space         space_noOfContractsNonInst;	 // autowired
	protected Space         space_noOfContractsCredit;	 // autowired

	// not auto wired vars
	private EtihadCreditBureauDetail etihadCreditBureauDetail; 							 // overhanded per param

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient EtihadCreditBureauDetailService etihadCreditBureauDetailService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();

	//For Dynamically calling of this Controller
	private Div toolbar;
	private FinanceDetail financedetail;
	private Object financeMainDialogCtrl;
	private Component parent = null;
	private Tab parentTab = null;
	private Grid grid_basicDetails;

	private transient boolean recSave = false;
	private transient boolean   newFinance;
	public transient int   ccyFormatter = 0;
	protected Groupbox finBasicdetails;
	private FinBasicDetailsCtrl  finBasicDetailsCtrl;

	/**
	 * default constructor.<br>
	 */
	public EtihadCreditBureauDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "EtihadCreditBureauDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected EtihadCreditBureauDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_EtihadCreditBureauDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_EtihadCreditBureauDetailDialog);

		if(event.getTarget().getParent() != null){
			parent = event.getTarget().getParent();
		}

		// READ OVERHANDED params !
		if (arguments.containsKey("etihadCreditBureauDetail")) {
			this.etihadCreditBureauDetail = (EtihadCreditBureauDetail) arguments.get("etihadCreditBureauDetail");
			EtihadCreditBureauDetail befImage = new EtihadCreditBureauDetail();
			BeanUtils.copyProperties(this.etihadCreditBureauDetail, befImage);
			this.etihadCreditBureauDetail.setBefImage(befImage);
			setEtihadCreditBureauDetail(this.etihadCreditBureauDetail);
		} else {
			setEtihadCreditBureauDetail(null);
		}

		if (arguments.containsKey("financeDetail")) {
			setFinancedetail((FinanceDetail) arguments.get("financeDetail"));
			if (getFinancedetail()!=null) {
				setEtihadCreditBureauDetail(getFinancedetail().getEtihadCreditBureauDetail());
			}
		}
		
		if(arguments.containsKey("financeMainDialogCtrl")){
			this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
			try {
					financeMainDialogCtrl.getClass().getMethod("setEtihadCreditBureauDetailDialogCtrl", this.getClass()).invoke(getFinanceMainDialogCtrl(), this);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
			setNewFinance(true);
			this.window_EtihadCreditBureauDetailDialog.setTitle("");
		}

		if(arguments.containsKey("roleCode")){
			setRole((String) arguments.get("roleCode"));
			getUserWorkspace().allocateRoleAuthorities(getRole(), "EtihadCreditBureauDetailDialog");
		}

		if(arguments.containsKey("ccyFormatter")){
			this.ccyFormatter = (Integer)arguments.get("ccyFormatter");
		}		
		
		if (arguments.containsKey("parentTab")) {
			parentTab = (Tab) arguments.get("parentTab");
		}
		
		if (isWorkFlowEnabled() && !isNewFinance()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "EtihadCreditBureauDetailDialog");
		}
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getEtihadCreditBureauDetail());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	public void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.bureauScore.setWidth("171px");
		this.totOutstandingAmt.setWidth("171px");
		this.totOverdueAmt.setWidth("171px");
		this.defaultContracts.setWidth("171px");
		this.totMonthlyInst.setWidth("171px");
		this.worstCurrPayDelay.setWidth("171px");
		this.worstPayDelay.setWidth("171px");
		this.worstStatus.setWidth("171px");
		this.oldConStartDate.setWidth("171px");
		this.newConStartDate.setWidth("171px");
		this.otherBankFinType.setWidth("171px");
		this.noOfInquiry.setWidth("171px");
		this.noOfContractsInst.setWidth("171px");
		this.noOfContractsNonInst.setWidth("171px");
		this.noOfContractsCredit.setWidth("171px");
		
		this.bureauScore.setMaxlength(8);
		this.defaultContracts.setMaxlength(8);
		this.worstCurrPayDelay.setMaxlength(200);
		this.worstPayDelay.setMaxlength(200);
		this.worstStatus.setMaxlength(200);
		this.otherBankFinType.setMaxlength(8);
		this.noOfInquiry.setMaxlength(8);
		this.noOfContractsInst.setMaxlength(8);
		this.noOfContractsNonInst.setMaxlength(8);
		this.noOfContractsCredit.setMaxlength(8);
	
		this.totOutstandingAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.totOutstandingAmt.setScale(ccyFormatter);
		this.totOutstandingAmt.setTextBoxWidth(171);
	
		this.totOverdueAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.totOverdueAmt.setScale(ccyFormatter);
		this.totOverdueAmt.setTextBoxWidth(171);
		
		this.totMonthlyInst.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.totMonthlyInst.setScale(ccyFormatter);
		this.totMonthlyInst.setTextBoxWidth(171);
		
		this.otherBankFinType.setMaxlength(8);
		
		this.oldConStartDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.newConStartDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		
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
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("EtihadCreditBureauDetailDialog", getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_EtihadCreditBureauDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_EtihadCreditBureauDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_EtihadCreditBureauDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_EtihadCreditBureauDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}


	public void doSave_EtihadCreditBureauDetail(FinanceDetail financeDetail,Tab etihadTab,boolean recSave){
		logger.debug("Entering");

		doClearMessage();
		doSetValidation(recSave);	
		doSetLOVValidation();
		EtihadCreditBureauDetail etihadCreditBureauDetail = getEtihadCreditBureauDetail();
		doWriteComponentsToBean(etihadCreditBureauDetail,etihadTab);
		if(StringUtils.isBlank(getEtihadCreditBureauDetail().getRecordType())){
			etihadCreditBureauDetail.setVersion(getEtihadCreditBureauDetail().getVersion() + 1);
			etihadCreditBureauDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			etihadCreditBureauDetail.setNewRecord(true);
		}
		etihadCreditBureauDetail.setFinReference(financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
		etihadCreditBureauDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		etihadCreditBureauDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		etihadCreditBureauDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
		financeDetail.setEtihadCreditBureauDetail(etihadCreditBureauDetail);
		logger.debug("Leaving");
	}

	

	private boolean allowValidation(){
		return !isReadOnly("EtihadCreditBureauDetailDialog_allowValidation");
	}
	
	private void setMandatoryStyle(){
		logger.debug("Entering");
		
		String sclass = "";
		boolean mandate = false;
		if(allowValidation()){
			sclass = "mandatory";
			mandate = true;
		}
		
		this.space_bureauScore.setSclass(sclass);
		this.space_defaultContracts.setSclass(sclass);
		this.space_newConStartDate.setSclass(sclass);
		this.space_noOfContractsCredit.setSclass(sclass);
		this.space_noOfContractsInst.setSclass(sclass);
		this.space_noOfContractsNonInst.setSclass(sclass);
		this.space_noOfInquiry.setSclass(sclass);
		this.space_oldConStartDate.setSclass(sclass);
		this.space_otherBankFinType.setSclass(sclass);
		this.space_worstCurrPayDelay.setSclass(sclass);
		this.space_worstPayDelay.setSclass(sclass);
		this.space_worstStatus.setSclass(sclass);
		this.totOutstandingAmt.setMandatory(mandate);
		this.totOverdueAmt.setMandatory(mandate);
		this.totMonthlyInst.setMandatory(mandate);
		logger.debug("Leaving");
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
		MessageUtil.showHelpWindow(event, window_EtihadCreditBureauDetailDialog);
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
		doWriteBeanToComponents(this.etihadCreditBureauDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aEtihadCreditBureauDetail
	 *            EtihadCreditBureauDetail
	 */
	public void doWriteBeanToComponents(EtihadCreditBureauDetail aEtihadCreditBureauDetail) {
		logger.debug("Entering");
		Date dateValueDate = DateUtility.getAppValueDate();
		this.finReference.setValue(aEtihadCreditBureauDetail.getFinReference());
		this.bureauScore.setValue(aEtihadCreditBureauDetail.getBureauScore());
		this.worstCurrPayDelay.setValue(aEtihadCreditBureauDetail.getWorstCurrPayDelay());
		this.totOutstandingAmt.setValue(PennantAppUtil.formateAmount(aEtihadCreditBureauDetail.getTotOutstandingAmt(), ccyFormatter));
		this.totOverdueAmt.setValue(PennantAppUtil.formateAmount(aEtihadCreditBureauDetail.getTotOverdueAmt(), ccyFormatter));
		this.totMonthlyInst.setValue(PennantAppUtil.formateAmount(aEtihadCreditBureauDetail.getTotMonthlyInst(), ccyFormatter));
		this.worstPayDelay.setValue(aEtihadCreditBureauDetail.getWorstPayDelay());
		this.worstStatus.setValue(aEtihadCreditBureauDetail.getWorstStatus());
		this.defaultContracts.setValue(aEtihadCreditBureauDetail.getDefaultContracts());
		this.otherBankFinType.setValue(aEtihadCreditBureauDetail.getOtherBankFinType());
		if(this.oldConStartDate.getValue()!=null){
			this.oldConStartDate.setValue(aEtihadCreditBureauDetail.getOldConStartDate());
		}else{
			this.oldConStartDate.setValue(DateUtility.addYears(dateValueDate, 5));
		}
		if(this.newConStartDate.getValue()!=null){
			this.newConStartDate.setValue(aEtihadCreditBureauDetail.getNewConStartDate());
		}else{
			this.newConStartDate.setValue(DateUtility.addYears(dateValueDate, 5));
			
		}
		this.otherBankFinType.setValue(aEtihadCreditBureauDetail.getOtherBankFinType());
		this.noOfInquiry.setValue(aEtihadCreditBureauDetail.getNoOfInquiry());
		this.noOfContractsInst.setValue(aEtihadCreditBureauDetail.getNoOfContractsInst());
		this.noOfContractsNonInst.setValue(aEtihadCreditBureauDetail.getNoOfContractsNonInst());
		this.noOfContractsCredit.setValue(aEtihadCreditBureauDetail.getNoOfContractsCredit());
      
		this.recordStatus.setValue(aEtihadCreditBureauDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aEtihadCreditBureauDetail
	 */
	public void doWriteComponentsToBean(EtihadCreditBureauDetail aEtihadCreditBureauDetail,Tab etihadTab) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aEtihadCreditBureauDetail.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aEtihadCreditBureauDetail.setWorstCurrPayDelay(this.worstCurrPayDelay.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aEtihadCreditBureauDetail.setWorstPayDelay(this.worstPayDelay.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEtihadCreditBureauDetail.setWorstStatus(this.worstStatus.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEtihadCreditBureauDetail.setBureauScore(this.bureauScore.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEtihadCreditBureauDetail.setDefaultContracts(this.defaultContracts.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEtihadCreditBureauDetail.setTotOutstandingAmt(PennantAppUtil.unFormateAmount(this.totOutstandingAmt.isReadonly() ? this.totOutstandingAmt.getActualValue() : this.totOutstandingAmt.getValidateValue(), ccyFormatter));			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEtihadCreditBureauDetail.setTotOverdueAmt(PennantAppUtil.unFormateAmount(this.totOverdueAmt.isReadonly() ? this.totOverdueAmt.getActualValue() : this.totOverdueAmt.getValidateValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEtihadCreditBureauDetail.setTotMonthlyInst(PennantAppUtil.unFormateAmount(this.totMonthlyInst.isReadonly() ? this.totMonthlyInst.getActualValue() : this.totMonthlyInst.getValidateValue(), ccyFormatter));			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEtihadCreditBureauDetail.setOldConStartDate(this.oldConStartDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEtihadCreditBureauDetail.setNewConStartDate(this.newConStartDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEtihadCreditBureauDetail.setOtherBankFinType(this.otherBankFinType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEtihadCreditBureauDetail.setNoOfInquiry(this.noOfInquiry.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEtihadCreditBureauDetail.setNoOfContractsInst(this.noOfContractsInst.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEtihadCreditBureauDetail.setNoOfContractsNonInst(this.noOfContractsNonInst.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aEtihadCreditBureauDetail.setNoOfContractsCredit(this.noOfContractsCredit.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		showErrorDetails(wve, etihadTab);
		aEtihadCreditBureauDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab etihadTab) {
		logger.debug("Entering");
		doRemoveValidation();
		doRemoveLOVValidation();
		if(!recSave){
			if (wve.size() > 0) {
				logger.debug("Throwing occured Errors By using WrongValueException");
				if(parentTab != null){
					parentTab.setSelected(true);
				}
				if(etihadTab != null){
					etihadTab.setSelected(true);
				}
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = wve.get(i);
				}
				throw new WrongValuesException(wvea);
			}
		}
		logger.debug("Leaving");
	}
	
	
	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aEtihadCreditBureauDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(EtihadCreditBureauDetail aEtihadCreditBureauDetail) throws InterruptedException {
		logger.debug("Entering");
		getBorderLayoutHeight();
		// append finance basic details 
		appendFinBasicDetails();

		if (aEtihadCreditBureauDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			EtihadCreditBureauDetail etihadCreditBureauDetail = new EtihadCreditBureauDetail();
			etihadCreditBureauDetail.setNewRecord(true);
			aEtihadCreditBureauDetail = etihadCreditBureauDetail;
			setEtihadCreditBureauDetail(etihadCreditBureauDetail);
		} else {
			setEtihadCreditBureauDetail(aEtihadCreditBureauDetail);
		}

		
		// set ReadOnly mode accordingly if the object is new or not.
		if (aEtihadCreditBureauDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit(); 
			// setFocus
			this.worstCurrPayDelay.focus();
		} else {
			this.worstCurrPayDelay.focus();
			if(isNewFinance()){
				doEdit(); 
			}else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			setMandatoryStyle();
			// fill the components with the data
			doWriteBeanToComponents(aEtihadCreditBureauDetail);

			if(parent != null){
				this.toolbar.setVisible(false);
				this.groupboxWf.setVisible(false);
				this.gb_basicDetails.setHeight("100%");
				this.window_EtihadCreditBureauDetailDialog.setHeight(grid_basicDetails.getRows().getVisibleItemCount()*22+250+"px");
				parent.appendChild(this.window_EtihadCreditBureauDetailDialog);
			}else{
				setDialog(DialogType.EMBEDDED);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation(boolean isSaveRecord) {
		logger.debug("Entering");
		setValidationOn(true);
		
		boolean mandate = (!isSaveRecord && allowValidation());

		if (!this.bureauScore.isReadonly()) {
			this.bureauScore.setConstraint(new PTNumberValidator(Labels.getLabel("label_EtihadCreditBureauDetailDialog_BureauScore.value"), mandate, false));
		}
		if (!this.totOutstandingAmt.isReadonly()) {
			this.totOutstandingAmt.setConstraint(new PTDecimalValidator(Labels.getLabel(
					"label_EtihadCreditBureauDetailDialog_TotOutstandingAmt.value"), ccyFormatter, mandate, false));
		}
		if (!this.totOverdueAmt.isReadonly()) {
			this.totOverdueAmt.setConstraint(new PTDecimalValidator(Labels.getLabel(
					"label_EtihadCreditBureauDetailDialog_TotOverdueAmt.value"), ccyFormatter, mandate, false));
		}
		if (!this.totMonthlyInst.isReadonly()) {
			this.totMonthlyInst.setConstraint(new PTDecimalValidator(Labels.getLabel(
					"label_EtihadCreditBureauDetailDialog_TotMonthlyInst.value"), ccyFormatter, mandate, false));
		}
		if (!this.defaultContracts.isReadonly()) {
			this.defaultContracts.setConstraint(new PTNumberValidator(Labels.getLabel("label_EtihadCreditBureauDetailDialog_DefaultContracts.value"), mandate, false));
		}
		if (!this.worstCurrPayDelay.isReadonly()) {
			this.worstCurrPayDelay.setConstraint(new PTStringValidator(Labels.getLabel("label_EtihadCreditBureauDetailDialog_WorstCurrPayDelay.value"), 
					null, mandate));
		}
		if (!this.worstPayDelay.isReadonly()) {
			this.worstPayDelay.setConstraint(new PTStringValidator(Labels.getLabel("label_EtihadCreditBureauDetailDialog_WorstPayDelay.value"), 
					null, mandate));
		}
		if (!this.worstStatus.isReadonly()) {
			this.worstStatus.setConstraint(new PTStringValidator(Labels.getLabel("label_EtihadCreditBureauDetailDialog_WorstStatus.value"), 
					null, mandate));
		}
		if (!this.oldConStartDate.isDisabled()) {
			this.oldConStartDate.setConstraint(new PTDateValidator(Labels.getLabel("label_EtihadCreditBureauDetailDialog_OldConStartDate.value"),mandate));
		}
		if (!this.newConStartDate.isDisabled()) {
			this.newConStartDate.setConstraint(new PTDateValidator(Labels.getLabel("label_EtihadCreditBureauDetailDialog_NewConStartDate.value"),mandate));
		}
		if (!this.otherBankFinType.isReadonly()) {
			this.otherBankFinType.setConstraint(new PTStringValidator(Labels.getLabel("label_EtihadCreditBureauDetailDialog_OtherBankFinType.value"), 
					null, mandate));
		}
		if (!this.noOfInquiry.isReadonly()) {
			this.noOfInquiry.setConstraint(new PTNumberValidator(Labels.getLabel("label_EtihadCreditBureauDetailDialog_NoOfInquiry.value"), mandate, false));
		}
		if (!this.noOfContractsInst.isReadonly()) {
			this.noOfContractsInst.setConstraint(new PTNumberValidator(Labels.getLabel("label_EtihadCreditBureauDetailDialog_NoOfContractsInst.value"), mandate, false));
		}
		if (!this.noOfContractsNonInst.isReadonly()) {
			this.noOfContractsNonInst.setConstraint(new PTNumberValidator(Labels.getLabel("label_EtihadCreditBureauDetailDialog_NoOfContractsNonInst.value"), mandate, false));
		}
		if (!this.noOfContractsCredit.isReadonly()) {
			this.noOfContractsCredit.setConstraint(new PTNumberValidator(Labels.getLabel("label_EtihadCreditBureauDetailDialog_NoOfContractsCredit.value"), mandate, false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finReference.setConstraint("");
		this.worstCurrPayDelay.setConstraint("");
		this.totOutstandingAmt.setConstraint("");
		this.totOverdueAmt.setConstraint("");
		this.totMonthlyInst.setConstraint("");
		this.worstPayDelay.setConstraint("");
		this.worstStatus.setConstraint("");
		this.bureauScore.setConstraint("");
		this.defaultContracts.setConstraint("");
		this.otherBankFinType.setConstraint("");
		this.oldConStartDate.setConstraint("");
		this.newConStartDate.setConstraint("");
		this.noOfInquiry.setConstraint("");
		this.noOfContractsInst.setConstraint("");
		this.noOfContractsNonInst.setConstraint("");
		this.noOfContractsCredit.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Set the constraints to LOV fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");


		logger.debug("Leaving");
	}

	/**
	 * Method for remove constraints to LOV fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	/**
	 * Method for clear the error Messages 
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.finReference.setErrorMessage("");
		this.worstCurrPayDelay.setErrorMessage("");
		this.totOutstandingAmt.setErrorMessage("");
		this.totOverdueAmt.setErrorMessage("");
		this.totMonthlyInst.setErrorMessage("");
		this.worstPayDelay.setErrorMessage("");
		this.worstStatus.setErrorMessage("");
		this.bureauScore.setErrorMessage("");
		this.defaultContracts.setErrorMessage("");
		this.otherBankFinType.setErrorMessage("");
		this.oldConStartDate.setErrorMessage("");
		this.newConStartDate.setErrorMessage("");
		this.noOfInquiry.setErrorMessage("");
		this.noOfContractsInst.setErrorMessage("");
		this.noOfContractsNonInst.setErrorMessage("");
		this.noOfContractsCredit.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void refreshList() {
		logger.debug("Entering");
		
		logger.debug("Leaving");
	}
	
	// CRUD operations

	/**
	 * Deletes a EtihadCreditBureauDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final EtihadCreditBureauDetail aEtihadCreditBureauDetail = new EtihadCreditBureauDetail();
		BeanUtils.copyProperties(getEtihadCreditBureauDetail(), aEtihadCreditBureauDetail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")
		+ "\n\n --> " + aEtihadCreditBureauDetail.getId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aEtihadCreditBureauDetail.getRecordType())) {
				aEtihadCreditBureauDetail.setVersion(aEtihadCreditBureauDetail.getVersion() + 1);
				aEtihadCreditBureauDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aEtihadCreditBureauDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aEtihadCreditBureauDetail, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getEtihadCreditBureauDetail().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		this.finReference.setReadonly(true);
		this.worstCurrPayDelay.setReadonly(isReadOnly("EtihadCreditBureauDetailDialog_worstCurrPayDelay"));
		this.worstPayDelay.setReadonly(isReadOnly("EtihadCreditBureauDetailDialog_worstPayDelay"));
		this.worstStatus.setReadonly(isReadOnly("EtihadCreditBureauDetailDialog_worstStatus"));
		this.bureauScore.setReadonly(isReadOnly("EtihadCreditBureauDetailDialog_bureauScore"));
		this.defaultContracts.setReadonly(isReadOnly("EtihadCreditBureauDetailDialog_defaultContracts"));
		this.totOutstandingAmt.setReadonly(isReadOnly("EtihadCreditBureauDetailDialog_totOutstandingAmt"));
		this.totOverdueAmt.setReadonly(isReadOnly("EtihadCreditBureauDetailDialog_totOverdueAmt"));
		this.totMonthlyInst.setReadonly(isReadOnly("EtihadCreditBureauDetailDialog_totMonthlyInst"));
		this.otherBankFinType.setReadonly(isReadOnly("EtihadCreditBureauDetailDialog_otherBankFinType"));
		this.oldConStartDate.setDisabled(isReadOnly("EtihadCreditBureauDetailDialog_oldConStartDate"));
		this.newConStartDate.setDisabled(isReadOnly("EtihadCreditBureauDetailDialog_newConStartDate"));
		this.noOfInquiry.setReadonly(isReadOnly("EtihadCreditBureauDetailDialog_noOfInquiry"));
		this.noOfContractsInst.setReadonly(isReadOnly("EtihadCreditBureauDetailDialog_noOfContractsInst"));
		this.noOfContractsNonInst.setReadonly(isReadOnly("EtihadCreditBureauDetailDialog_noOfContractsNonInst"));
		this.noOfContractsCredit.setReadonly(isReadOnly("EtihadCreditBureauDetailDialog_noOfContractsCredit"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.etihadCreditBureauDetail.isNewRecord()) {
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

	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled() || isNewFinance()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.finReference.setReadonly(true);
		this.worstCurrPayDelay.setReadonly(true);
		this.totOutstandingAmt.setReadonly(true);
		this.totOverdueAmt.setReadonly(true);
		this.totMonthlyInst.setReadonly(true);
		this.worstPayDelay.setReadonly(true);
		this.worstStatus.setReadonly(true);
		this.bureauScore.setReadonly(true);
		this.defaultContracts.setReadonly(true);
		this.otherBankFinType.setReadonly(true);
		this.oldConStartDate.setDisabled(true);
		this.newConStartDate.setDisabled(true);
		this.noOfInquiry.setReadonly(true);
		this.noOfContractsInst.setReadonly(true);
		this.noOfContractsNonInst.setReadonly(true);
		this.noOfContractsCredit.setReadonly(true);

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
		this.finReference.setText("");
		this.worstCurrPayDelay.setValue("");
		this.totOutstandingAmt.setValue("");
		this.totOverdueAmt.setValue("");
		this.totMonthlyInst.setValue("");
		this.worstPayDelay.setValue("");
		this.worstStatus.setValue("");
		this.bureauScore.setValue(0);
		this.defaultContracts.setValue(0);
		this.otherBankFinType.setValue("");
		this.noOfInquiry.setValue(0);
		this.noOfContractsInst.setValue(0);
		this.noOfContractsNonInst.setValue(0);
		this.noOfContractsCredit.setValue(0);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final EtihadCreditBureauDetail aEtihadCreditBureauDetail = new EtihadCreditBureauDetail();
		BeanUtils.copyProperties(getEtihadCreditBureauDetail(), aEtihadCreditBureauDetail);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation(true);
		doSetLOVValidation();
		// fill the EtihadCreditBureauDetail object with the components data
		doWriteComponentsToBean(aEtihadCreditBureauDetail,null);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aEtihadCreditBureauDetail.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aEtihadCreditBureauDetail.getRecordType())) {
				aEtihadCreditBureauDetail.setVersion(aEtihadCreditBureauDetail.getVersion() + 1);
				if (isNew) {
					aEtihadCreditBureauDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aEtihadCreditBureauDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aEtihadCreditBureauDetail.setNewRecord(true);
				}
			}
		} else {
			aEtihadCreditBureauDetail.setVersion(aEtihadCreditBureauDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aEtihadCreditBureauDetail, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aEtihadCreditBureauDetail (EtihadCreditBureauDetail)
	 * @param tranType (String)
	 * 
	 * @return boolean
	 */
	private boolean doProcess(EtihadCreditBureauDetail aEtihadCreditBureauDetail, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aEtihadCreditBureauDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aEtihadCreditBureauDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aEtihadCreditBureauDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aEtihadCreditBureauDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aEtihadCreditBureauDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aEtihadCreditBureauDetail);
				}

				if (isNotesMandatory(taskId, aEtihadCreditBureauDetail)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
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

			aEtihadCreditBureauDetail.setTaskId(taskId);
			aEtihadCreditBureauDetail.setNextTaskId(nextTaskId);
			aEtihadCreditBureauDetail.setRoleCode(getRole());
			aEtihadCreditBureauDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aEtihadCreditBureauDetail, tranType);

			String operationRefs = getServiceOperations(taskId, aEtihadCreditBureauDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aEtihadCreditBureauDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aEtihadCreditBureauDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * @param method (String)
	 * 
	 * @return boolean
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		EtihadCreditBureauDetail aEtihadCreditBureauDetail = (EtihadCreditBureauDetail) auditHeader.getAuditDetail().getModelData();

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getEtihadCreditBureauDetailService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getEtihadCreditBureauDetailService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getEtihadCreditBureauDetailService().doApprove(auditHeader);

						if (aEtihadCreditBureauDetail.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getEtihadCreditBureauDetailService().doReject(auditHeader);
						if (aEtihadCreditBureauDetail.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(
								this.window_EtihadCreditBureauDetailDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.window_EtihadCreditBureauDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.etihadCreditBureauDetail), true);
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
			logger.error("Exception: ", e);
		}
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}


	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aEtihadCreditBureauDetail
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(EtihadCreditBureauDetail aEtihadCreditBureauDetail,
			String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aEtihadCreditBureauDetail.getBefImage(), aEtihadCreditBureauDetail);
		return new AuditHeader(String.valueOf(aEtihadCreditBureauDetail.getId()),
				null, null, null, auditDetail,
				aEtihadCreditBureauDetail.getUserDetails(), getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_EtihadCreditBureauDetailDialog, auditHeader);
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
		doShowNotes(this.etihadCreditBureauDetail);
	}

	
	@Override
	protected String getReference() {
		return String.valueOf(this.etihadCreditBureauDetail.getId());
	}

	
	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this );
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul",this.finBasicdetails, map);
		} catch (Exception e) {
			logger.debug(e);
		}
		
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
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

	public EtihadCreditBureauDetail getEtihadCreditBureauDetail() {
		return this.etihadCreditBureauDetail;
	}
	public void setEtihadCreditBureauDetail(EtihadCreditBureauDetail etihadCreditBureauDetail) {
		this.etihadCreditBureauDetail = etihadCreditBureauDetail;
	}

	public void setEtihadCreditBureauDetailService(
			EtihadCreditBureauDetailService etihadCreditBureauDetailService) {
		this.etihadCreditBureauDetailService = etihadCreditBureauDetailService;
	}
	public EtihadCreditBureauDetailService getEtihadCreditBureauDetailService() {
		return this.etihadCreditBureauDetailService;
	}

	public void setOverideMap(
			HashMap<String, ArrayList<ErrorDetails>> overideMap) {
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
	
	public FinanceDetail getFinancedetail() {
		return financedetail;
	}
	public void setFinancedetail(FinanceDetail financedetail) {
		this.financedetail = financedetail;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}
	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

}
