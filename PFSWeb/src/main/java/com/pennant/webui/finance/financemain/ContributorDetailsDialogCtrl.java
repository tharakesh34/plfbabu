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
 * FileName    		:  FinanceMainDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/FinanceMainDialog.zul file.
 */
public class ContributorDetailsDialogCtrl extends GFCBaseCtrl<FinContributorDetail> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(ContributorDetailsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_ContributorDetailsDialog; 				

	//Contributor Header Details Tab

	protected Intbox 		minContributors; 						
	protected Intbox 		maxContributors; 						
	protected CurrencyBox 	minContributionAmt; 					
	protected CurrencyBox 	maxContributionAmt; 					
	protected Intbox 		curContributors; 						
	protected Decimalbox 	curContributionAmt; 					
	protected Decimalbox 	curBankInvest; 							
	protected Decimalbox 	avgMudaribRate; 						
	protected Checkbox 		alwContributorsToLeave; 				
	protected Checkbox 		alwContributorsToJoin; 					
	protected Listbox 		listBoxFinContributor; 					
	protected Button 		btnNewContributor; 						
	protected Button 		btnPrintContributor; 					
	protected BigDecimal 	curContributionCalAmt = null;

	protected Label        label_FinanceMainDialog_MinContributors;
	protected Label        label_FinanceMainDialog_MaxContributors;
	protected Label        label_FinanceMainDialog_MinContributionAmt;
	protected Label        label_FinanceMainDialog_MaxContributionAmt;
	protected Label        label_FinanceMainDialog_CurContributors;
	protected Label        label_FinanceMainDialog_CurContributionAmt;
	protected Label        label_FinanceMainDialog_CurBankInvest;
	protected Label        label_FinanceMainDialog_AvgMudaribRate;
	protected Label        label_FinanceMainDialog_AlwContributorsToLeave;
	protected Label        label_FinanceMainDialog_AlwContributorsToJoin;

	protected Listheader   listheader_ContributorCIF;
	protected Listheader   listheader_ContributorInvest;
	protected Listheader   listheader_InvestDate;
	protected Listheader   listheader_RecordDate;
	protected Listheader   listheader_TotalInvestPerc;
	protected Listheader   listheader_MudaribPerc;
	protected Listheader   listheader_InvestAcc;

	private List<FinContributorDetail> contributorsList = new ArrayList<FinContributorDetail>();
	private List<FinContributorDetail> oldVar_ContributorList = new ArrayList<FinContributorDetail>();

	// not auto wired variables
	private FinanceDetail financeDetail = null; 			
	private Object financeMainDialogCtrl = null;
	private FinScheduleData finScheduleData = null;

	private BigDecimal finAmount = BigDecimal.ZERO;
	private BigDecimal downPayment = BigDecimal.ZERO;
	private String finCcy = "";
	private String roleCode = "";
	private String productCode = "";
	private int formatter = 0;
	
	protected Groupbox finBasicdetails;
	private FinBasicDetailsCtrl  finBasicDetailsCtrl;

	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();

	/**
	 * default constructor.<br>
	 */
	public ContributorDetailsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ContributorDetailsDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ContributorDetailsDialog);

		if (arguments.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
			setFinanceDetail(this.financeDetail);
		}

		if (arguments.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
		}

		if (arguments.containsKey("roleCode")) {
			this.roleCode = (String) arguments.get("roleCode");
		}

		getBorderLayoutHeight();
		this.listBoxFinContributor.setHeight(this.borderLayoutHeight- 210 - 52+"px");
		this.window_ContributorDetailsDialog.setHeight(this.borderLayoutHeight-80+"px");

		// set Field Properties
		doSetFieldProperties();

		// set Label Properties
		productCode =  financeDetail.getFinScheduleData().getFinanceType().getFinCategory();
		if (FinanceConstants.PRODUCT_MUSHARAKA.equals(productCode)){
			doSetLabels();
		}

		doShowDialog(this.financeDetail);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		//Contributor Header Details
		formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		this.minContributors.setMaxlength(4);
		this.maxContributors.setMaxlength(4);
		
		this.minContributionAmt.setMandatory(false);
		this.minContributionAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.minContributionAmt.setScale(formatter);
		this.minContributionAmt.setTextBoxWidth(150);
		
		this.maxContributionAmt.setMandatory(false);
		this.maxContributionAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.maxContributionAmt.setScale(formatter);
		this.maxContributionAmt.setTextBoxWidth(150);
		
		this.curContributors.setMaxlength(4);
		this.curContributionAmt.setMaxlength(18);
		this.curContributionAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.curBankInvest.setMaxlength(18);
		this.curBankInvest.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.avgMudaribRate.setMaxlength(13);
		this.avgMudaribRate.setScale(9);
		this.avgMudaribRate.setFormat(PennantConstants.rateFormate9);
		if (financeDetail.getFinScheduleData().getFinanceType().getFinCategory().equals(FinanceConstants.PRODUCT_MUSHARAKA)){
			this.avgMudaribRate.setVisible(false);
			this.label_FinanceMainDialog_AvgMudaribRate.setVisible(false);
			this.listheader_MudaribPerc.setVisible(false);
		}

		logger.debug("Leaving");
	}

	public void doSetLabels(){
		logger.debug("Entering");
		label_FinanceMainDialog_MinContributors.setValue(Labels.getLabel("label_FinanceMainDialog_MusharakMinContributors.value"));
		label_FinanceMainDialog_MaxContributors.setValue(Labels.getLabel("label_FinanceMainDialog_MusharakMaxContributors.value"));
		label_FinanceMainDialog_MinContributionAmt.setValue(Labels.getLabel("label_FinanceMainDialog_MusharakMinContributionAmt.value"));
		label_FinanceMainDialog_MaxContributionAmt.setValue(Labels.getLabel("label_FinanceMainDialog_MusharakMaxContributionAmt.value"));
		label_FinanceMainDialog_CurContributors.setValue(Labels.getLabel("label_FinanceMainDialog_MusharakCurContributors.value"));
		label_FinanceMainDialog_CurContributionAmt.setValue(Labels.getLabel("label_FinanceMainDialog_MusharakCurContributionAmt.value"));
		label_FinanceMainDialog_CurBankInvest.setValue(Labels.getLabel("label_FinanceMainDialog_MusharakCurBankInvest.value"));
		label_FinanceMainDialog_AvgMudaribRate.setValue(Labels.getLabel("label_FinanceMainDialog_MusharakAvgMudaribRate.value"));
		label_FinanceMainDialog_AlwContributorsToLeave.setValue(Labels.getLabel("label_FinanceMainDialog_MusharakAlwContributorsToLeave.value"));
		label_FinanceMainDialog_AlwContributorsToJoin.setValue(Labels.getLabel("label_FinanceMainDialog_MusharakAlwContributorsToJoin.value"));


		listheader_ContributorCIF.setLabel(Labels.getLabel("listheader_MusharakContributorCIF.label"));
		listheader_InvestAcc.setLabel(Labels.getLabel("listheader_MusharakInvestAcc.label"));
		listheader_ContributorInvest.setLabel(Labels.getLabel("listheader_MusharakContributorInvest.label"));
		listheader_InvestDate.setLabel(Labels.getLabel("listheader_MusharakInvestDate.label"));
		listheader_RecordDate.setLabel(Labels.getLabel("listheader_MusharakRecordDate.label"));
		listheader_TotalInvestPerc.setLabel(Labels.getLabel("listheader_MusharakTotalInvestPerc.label"));
		listheader_MudaribPerc.setLabel(Labels.getLabel("label_FinanceMainDialog_MusharakMinContributors.value"));

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            financeMain
	 * @throws ParseException 
	 */
	public void doWriteBeanToComponents() throws ParseException {
		logger.debug("Entering");

		this.finAmount = PennantApplicationUtil.formateAmount(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAmount(),formatter);
		this.downPayment = PennantApplicationUtil.formateAmount(getFinanceDetail().getFinScheduleData().getFinanceMain().getDownPayment(),formatter);
		this.finCcy = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy();

		//Contributor Header Details
		FinContributorHeader finContributorHeader = getFinanceDetail().getFinContributorHeader();
		if(finContributorHeader != null){
			
			if(finContributorHeader.isNewRecord()) {
				this.minContributors.setValue(0);
				this.maxContributors.setValue(0);
				this.minContributionAmt.setValue(BigDecimal.ZERO);
				this.maxContributionAmt.setValue(BigDecimal.ZERO);
				this.curContributionAmt.setValue(BigDecimal.ZERO);
				this.curBankInvest.setValue(BigDecimal.ZERO);
				this.avgMudaribRate.setValue(BigDecimal.ZERO);
			} else {
				this.minContributors.setValue(finContributorHeader.getMinContributors());
				this.maxContributors.setValue(finContributorHeader.getMaxContributors());
				this.minContributionAmt.setValue(PennantAppUtil.formateAmount(finContributorHeader.getMinContributionAmt(),formatter));
				this.maxContributionAmt.setValue(PennantAppUtil.formateAmount(finContributorHeader.getMaxContributionAmt(),formatter));
				this.curContributionAmt.setValue(PennantAppUtil.formateAmount(finContributorHeader.getCurContributionAmt(),formatter));
				this.curBankInvest.setValue(PennantAppUtil.formateAmount(finContributorHeader.getCurBankInvestment(),formatter));
				this.avgMudaribRate.setValue(finContributorHeader.getAvgMudaribRate());
			}
			
			this.curContributors.setValue(finContributorHeader.getCurContributors());
			this.alwContributorsToLeave.setChecked(finContributorHeader.isAlwContributorsToLeave());
			this.alwContributorsToJoin.setChecked(finContributorHeader.isAlwContributorsToJoin());
		} else {
			this.minContributors.setValue(0);
			this.maxContributors.setValue(0);
			this.minContributionAmt.setValue(BigDecimal.ZERO);
			this.maxContributionAmt.setValue(BigDecimal.ZERO);
			this.curContributionAmt.setValue(BigDecimal.ZERO);
			this.curBankInvest.setValue(BigDecimal.ZERO);
			this.avgMudaribRate.setValue(BigDecimal.ZERO);
		}

		if (getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord() && finAmount.compareTo(BigDecimal.ZERO)>0) {
			doSetFinAmount(finAmount, downPayment);
		}
		if(getFinanceDetail().getFinContributorHeader() != null && 
				getFinanceDetail().getFinContributorHeader().getContributorDetailList() != null &&
				getFinanceDetail().getFinContributorHeader().getContributorDetailList().size() > 0){

			doFillFinContributorDetails(getFinanceDetail().getFinContributorHeader().getContributorDetailList(), false);
		} else {
			doResetContributorDetails();
		}

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceDetail afinanceDetail) throws InterruptedException {
		logger.debug("Entering");
		
        appendFinBasicDetails();
		this.btnNewContributor.setVisible(!isReadOnly("ContributorDialog_btnNewContributor"));
		// set Read only mode accordingly if the object is new or not.
		doEdit();
		try {
			// fill the components with the data
			doWriteBeanToComponents();

			if (getFinanceMainDialogCtrl() != null) {
				try {
					financeMainDialogCtrl.getClass().getMethod("setContributorDetailsDialogCtrl", this.getClass()).invoke(financeMainDialogCtrl, this);

				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}

			getBorderLayoutHeight();
			this.listBoxFinContributor.setHeight(borderLayoutHeight - 150 +"px");
			this.window_ContributorDetailsDialog.setHeight(borderLayoutHeight - 5 +"px");

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		doClearMessage();
		
		//Contribution Details Tab
		if (!this.minContributors.isReadonly()) {
			this.minContributors.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinanceMainDialog_MinContributors.value"),false,false,0,9999));
		}

		if (!this.maxContributors.isReadonly()) {
			this.maxContributors.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinanceMainDialog_MaxContributors.value"),false,false,0,9999));
		}

		if (!this.minContributionAmt.isDisabled()) {
			this.minContributionAmt.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceMainDialog_MinContributionAmt.value"), formatter,	false, false));
		}

		if (!this.maxContributionAmt.isDisabled()) {
			this.maxContributionAmt.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceMainDialog_MaxContributionAmt.value"), formatter,	false, false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	public void doRemoveValidation() {
		logger.debug("Entering");

		//Contribution Header Details Tab
		this.minContributors.setConstraint("");
		this.maxContributors.setConstraint("");
		this.minContributionAmt.setConstraint("");
		this.maxContributionAmt.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method to clear error messages.
	 * */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		//FinanceMain Details Tab ---> 1. Basic Details
		this.minContributors.setErrorMessage("");
		this.maxContributors.setErrorMessage("");
		this.minContributionAmt.setErrorMessage("");
		this.maxContributionAmt.setErrorMessage("");
		Clients.clearWrongValue(this.maxContributionAmt);
		Clients.clearWrongValue(this.minContributionAmt);

		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		getUserWorkspace().allocateRoleAuthorities(roleCode, "ContributorDialog");	

		//Contribution Header Details
		this.minContributors.setReadonly(isReadOnly("ContributorDialog_minContributors"));
		this.maxContributors.setReadonly(isReadOnly("ContributorDialog_maxContributors"));
		this.minContributionAmt.setDisabled(isReadOnly("ContributorDialog_minContributionAmt"));
		this.maxContributionAmt.setDisabled(isReadOnly("ContributorDialog_maxContributionAmt"));
		this.curContributors.setReadonly(true);
		this.curContributionAmt.setDisabled(true);
		this.curBankInvest.setDisabled(true);
		this.avgMudaribRate.setDisabled(true);
		this.alwContributorsToLeave.setDisabled(isReadOnly("ContributorDialog_alwContributorsToLeave"));
		this.alwContributorsToJoin.setDisabled(isReadOnly("ContributorDialog_alwContributorsToJoin"));

		this.btnNewContributor.setVisible(!isReadOnly("ContributorDialog_btnNewContributor"));
		this.btnPrintContributor.setVisible(false);

		logger.debug("Leaving");
	}
	
	public boolean isReadOnly(String componentName){
		return getUserWorkspace().isReadOnly(componentName);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.minContributors.setReadonly(true);
		this.maxContributors.setReadonly(true);
		this.minContributionAmt.setDisabled(true);
		this.maxContributionAmt.setDisabled(true);
		this.curContributors.setReadonly(true);
		this.curContributionAmt.setDisabled(true);
		this.curBankInvest.setDisabled(true);
		this.avgMudaribRate.setDisabled(true);
		this.alwContributorsToLeave.setDisabled(true);
		this.alwContributorsToJoin.setDisabled(true);

		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.minContributors.setValue(0);
		this.maxContributors.setValue(0);
		this.minContributionAmt.setValue("");
		this.maxContributionAmt.setValue("");
		this.curContributors.setValue(0);
		this.curContributionAmt.setValue("");
		this.curBankInvest.setValue("");
		this.avgMudaribRate.setValue("");
		this.alwContributorsToLeave.setChecked(false);
		this.alwContributorsToJoin.setChecked(false);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public FinanceDetail doSaveContributorsDetail(FinanceDetail aFinanceDetail, Tab tab) throws InterruptedException {
		logger.debug("Entering");
		doClearMessage();
		doSetValidation();

		getFinanceDetailData();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		FinContributorHeader header = aFinanceDetail.getFinContributorHeader();

		if (header == null) {
			header = new FinContributorHeader(aFinanceDetail.getFinScheduleData().getFinReference());
			header.setNewRecord(true);
		}else{
			header.setFinReference(aFinanceDetail.getFinScheduleData().getFinReference());
		}

		int noOfContributors=this.contributorsList.size();

		try {
			if(this.minContributors.intValue() != 0 && noOfContributors < this.minContributors.intValue()){	
				throw new WrongValueException(this.minContributors,  Labels.getLabel("NUMBER_MINLIMIT2",
						new String[]{Labels.getLabel("label_FinanceMainDialog_MinContributors.value"), String.valueOf(this.minContributors.intValue())}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.maxContributors.intValue() != 0 && noOfContributors > this.maxContributors.intValue()){	
				throw new WrongValueException(this.maxContributors, Labels.getLabel("NUMBER_MAXLIMIT" , 
						Labels.getLabel("label_FinanceMainDialog_MaxContributors.value")));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.minContributors.intValue() != 0 &&  this.maxContributors.intValue() != 0){
				if(this.minContributors.getValue() > this.maxContributors.getValue()){
					throw new WrongValueException(this.minContributors,  Labels.getLabel("FIELD_IS_LESSER",
							new String[] { Labels.getLabel("label_FinanceMainDialog_MinContributors.value"),
							Labels.getLabel("label_FinanceMainDialog_MaxContributors.value")}));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(this.maxContributors.intValue() < this.contributorsList.size()){
				throw new WrongValueException(this.maxContributors,  Labels.getLabel("FIELD_IS_EQUAL_OR_GREATER",
						new String[]{Labels.getLabel("label_FinanceMainDialog_MaxContributors.value"), String.valueOf(this.contributorsList.size())}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		header.setMinContributors(this.minContributors.intValue());
		header.setMaxContributors(this.maxContributors.intValue());
		BigDecimal totalInvestAmt=BigDecimal.ZERO;
		for(FinContributorDetail items : contributorsList){
			totalInvestAmt=totalInvestAmt.add(PennantAppUtil.formateAmount(items.getContributorInvest(), formatter));
		}

		try {
			if(totalInvestAmt.compareTo(this.minContributionAmt.getValidateValue()) < 0){
				throw new WrongValueException(this.minContributionAmt, Labels.getLabel("NUMBER_MAXVALUE_EQ" , 
						new String[] { Labels.getLabel("label_FinanceMainDialog_MinContributionAmt.value"),
						Labels.getLabel("label_FinanceMainDialog_TotalInvestment.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(totalInvestAmt.compareTo(this.maxContributionAmt.getValidateValue()) > 0){
				throw new WrongValueException(this.maxContributionAmt, Labels.getLabel("NUMBER_MINVALUE_EQ" , 
						new String[] { Labels.getLabel("label_FinanceMainDialog_MaxContributionAmt.value"),
						Labels.getLabel("label_FinanceMainDialog_TotalInvestment.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		String finCategoryCode  = productCode.substring(0, 1)+productCode.substring(1).toLowerCase();
		
		try {
			if(this.minContributionAmt.getActualValue() == null || 
					this.minContributionAmt.getActualValue().compareTo(this.finAmount.subtract(this.downPayment)) > 0 ){
				throw new WrongValueException(this.minContributionAmt, Labels.getLabel("FIELD_IS_LESSER",
						new String[] { Labels.getLabel("label_FinanceMainDialog_MinContributionAmt.value"),
						Labels.getLabel("label_"+finCategoryCode+"FinanceMainDialog_FinAmount.value")}));
			}

			header.setMinContributionAmt(PennantAppUtil.unFormateAmount(this.minContributionAmt.getActualValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if(this.maxContributionAmt.getActualValue() == null || 
					this.maxContributionAmt.getActualValue().compareTo(this.finAmount.subtract(this.downPayment)) > 0 ){
				throw new WrongValueException(this.maxContributionAmt,  Labels.getLabel("FIELD_IS_LESSER",
						new String[] { Labels.getLabel("label_FinanceMainDialog_MaxContributionAmt.value") ,
						Labels.getLabel("label_"+finCategoryCode+"FinanceMainDialog_FinAmount.value")}));
			}

			header.setMaxContributionAmt(PennantAppUtil.unFormateAmount(this.maxContributionAmt.getActualValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.minContributionAmt.getActualValue() != null && this.maxContributionAmt.getActualValue() != null){
				if(this.minContributionAmt.getActualValue().compareTo(this.maxContributionAmt.getActualValue()) > 0 ){
					throw new WrongValueException(this.minContributionAmt,  Labels.getLabel("FIELD_IS_LESSER",
							new String[] { Labels.getLabel("label_FinanceMainDialog_MinContributionAmt.value"),
							Labels.getLabel("label_FinanceMainDialog_MaxContributionAmt.value")}));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.minContributors.intValue() == 0 && this.maxContributors.intValue() == 0 && this.minContributionAmt.getActualValue().compareTo(BigDecimal.ZERO) == 0 &&
					this.maxContributionAmt.getActualValue().compareTo(BigDecimal.ZERO) == 0){
				this.listBoxFinContributor.getItems().clear();
				addBankShareOrTotal(true, PennantAppUtil.unFormateAmount(this.finAmount.subtract(this.downPayment), formatter));
				
				//Adding Totals for Total List
				addBankShareOrTotal(false, PennantAppUtil.unFormateAmount(this.finAmount.subtract(this.downPayment), formatter));
			}
			if(this.listBoxFinContributor.getItems().isEmpty()){
				throw new WrongValueException(this.btnNewContributor,  Labels.getLabel("EMPTY_LIST",Labels.getLabel("ContributorDetails")));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		showErrorDetails(wve, tab);

		header.setCurContributors(this.curContributors.intValue());
		header.setCurContributionAmt(PennantAppUtil.unFormateAmount(
				this.curContributionAmt.getValue()== null ? BigDecimal.ZERO :this.curContributionAmt.getValue() ,formatter));

		header.setCurBankInvestment(PennantAppUtil.unFormateAmount(
				this.curBankInvest.getValue()== null ? BigDecimal.ZERO :this.curBankInvest.getValue(),formatter));

		header.setAvgMudaribRate(this.avgMudaribRate.getValue() == null ? BigDecimal.ZERO :this.avgMudaribRate.getValue());
		header.setAlwContributorsToLeave(this.alwContributorsToLeave.isChecked());
		header.setAlwContributorsToJoin(this.alwContributorsToJoin.isChecked());

		String rcdStatus = aFinanceDetail.getFinScheduleData().getFinanceMain().getRecordStatus();
		if (isWorkFlowEnabled()) {
			if (StringUtils.isBlank(rcdStatus)) {
				if (aFinanceDetail.isNew()) {
					header.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					header.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					header.setNewRecord(true);
				}
			}
			header.setRecordStatus(getFinanceDetail().getFinScheduleData().getFinanceMain().getRecordStatus());
		} else {
			header.setVersion(header.getVersion() + 1);
		}

		header.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		header.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		header.setUserDetails(getUserWorkspace().getLoggedInUser());

		//Finance Contributor Details List
		header.setContributorDetailList(getContributorsList());

		aFinanceDetail.setFinContributorHeader(header);

		logger.debug("Leaving");
		return aFinanceDetail;
	}

	/**
	 * Method to show error details if occurred
	 * 
	 **/
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			doRemoveValidation();
			// groupBox.set
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	// WorkFlow Creations

	/**
	 * Method to store the default values if no values are entered in respective
	 * fields when validate or build schedule buttons are clicked
	 * 
	 * */
	public void doStoreDefaultValues() {
		// calling method to clear the constraints
		logger.debug("Entering");
		doClearMessage();
		logger.debug("Leaving");
	}

	/**
	 * when the "btnPrintRIA" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnPrintContributor(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*if (this.contributorsList.size() > 0) {
			doWriteComponentsToBean(getFinanceDetail());
			ReportGenerationUtil.generateReport("ContributorDetails", getFinanceDetail().getFinContributorHeader(),
					this.contributorsList, true, 1, getUserWorkspace().getUserDetails().getUsername(),
					window_ContributorDetailsDialog);
		} else {
			MessageUtil.showErrorMessage("Ristricted Investment Details should be entered before printing.");
			return;
		}*/
		logger.debug("Leaving" + event.toString());
	}


	// New Button & Double Click Events for Finance Contributor List

	/**
	 * Method for Creation of New Contributor for RIA Investment
	 */
	public void onClick$btnNewContributor(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		doClearMessage();
		getFinanceDetailData();
		
		if(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAmount().equals(BigDecimal.ZERO)){
			getFinanceMainDialogCtrl().getClass().getMethod("validateAssetValue").invoke(getFinanceMainDialogCtrl());			
		}
		
		this.finAmount = PennantApplicationUtil.formateAmount(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAmount(),formatter);
		this.downPayment = PennantApplicationUtil.formateAmount(getFinanceDetail().getFinScheduleData().getFinanceMain().getDownPayment(),formatter);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if(this.minContributors.intValue() == 0){
				throw new WrongValueException(this.minContributors,  Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO",
						new String[] {Labels.getLabel("label_FinanceMainDialog_MinContributors.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.maxContributors.intValue() == 0){
				throw new WrongValueException(this.maxContributors,  Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO",
						new String[] {Labels.getLabel("label_FinanceMainDialog_MaxContributors.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.minContributors.intValue() != 0 &&  this.maxContributors.intValue() != 0){
				if(this.minContributors.getValue() > this.maxContributors.getValue()){
					throw new WrongValueException(this.minContributors,  Labels.getLabel("FIELD_IS_LESSER",
							new String[] { Labels.getLabel("label_FinanceMainDialog_MinContributors.value"),
							Labels.getLabel("label_FinanceMainDialog_MaxContributors.value")}));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		String finCategoryCode  = productCode.substring(0, 1)+productCode.substring(1).toLowerCase();
		try{
			BigDecimal contributionAmt = this.finAmount.subtract(this.downPayment);
			if(this.maxContributionAmt.getValidateValue().compareTo(contributionAmt) > 0 ){
				throw new WrongValueException(this.maxContributionAmt, Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
						new String[] { Labels.getLabel("label_FinanceMainDialog_MaxContributionAmt.value"),
						String.valueOf(contributionAmt)}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try{
			if(this.maxContributionAmt.getActualValue().compareTo(BigDecimal.ZERO) >0
					&& this.maxContributionAmt.getActualValue().compareTo(this.curContributionAmt.getValue()) <= 0){
				throw new WrongValueException(this.maxContributionAmt, Labels.getLabel("NUMBER_MAXLIMIT",
						new String[] { Labels.getLabel("label_FinanceMainDialog_MaxContributionAmt.value"),
						Labels.getLabel("label_"+finCategoryCode+"FinanceMainDialog_FinAmount.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try{
			if(this.minContributionAmt.getValidateValue().compareTo(this.finAmount.subtract(downPayment)) > 0 ){
				throw new WrongValueException(this.minContributionAmt, Labels.getLabel("FIELD_IS_LESSER",
						new String[] { Labels.getLabel("label_FinanceMainDialog_MinContributionAmt.value"),
						Labels.getLabel("label_"+finCategoryCode+"FinanceMainDialog_FinAmount.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.maxContributors.intValue() != 0 && this.contributorsList.size() >= this.maxContributors.intValue()){	
				throw new WrongValueException(this.maxContributors, Labels.getLabel("NUMBER_MAXLIMIT" , 
						new String[]{ Labels.getLabel("label_FinanceMainDialog_MaxContributors.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		doRemoveValidation();
		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		FinContributorDetail finContributorDetail = new FinContributorDetail();
		finContributorDetail.setNewRecord(true);
		finContributorDetail.setWorkflowId(0);
		finContributorDetail.setCustID(Long.MIN_VALUE);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finContributorDetail", finContributorDetail);
		map.put("contributorDetailsDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", roleCode);
		map.put("finCcy", this.finCcy);
		map.put("finAmount", this.finAmount);
		map.put("financeDetail", getFinanceDetail());

		BigDecimal maxAmt = PennantAppUtil.unFormateAmount(this.maxContributionAmt.getActualValue(), formatter);

		map.put("balInvestAmount", maxAmt.subtract(curContributionCalAmt == null ? BigDecimal.ZERO : curContributionCalAmt));

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceContributor/FinContributorDetailDialog.zul",
					window_ContributorDetailsDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$minContributors(Event event){
		logger.debug("Entering" + event.toString());
		
		if(this.minContributors.getValue() == null){
			this.minContributors.setValue(0);
		}
		
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$maxContributors(Event event){
		logger.debug("Entering" + event.toString());
		
		if(this.maxContributors.getValue() == null){
			this.maxContributors.setValue(0);
		}
		
		logger.debug("Leaving" + event.toString());
	}
	
	public void onFulfill$minContributionAmt(Event event){
		logger.debug("Entering" + event.toString());
		
		if(this.minContributionAmt.getValidateValue() == null || 
				minContributionAmt.getActualValue().compareTo(BigDecimal.ZERO) == 0){
			this.minContributionAmt.setValue(BigDecimal.ZERO);
		}
		
		logger.debug("Leaving" + event.toString());
	}
	
	public void onFulfill$maxContributionAmt(Event event){
		logger.debug("Entering" + event.toString());
		
		if(this.maxContributionAmt.getValidateValue() == null || 
				maxContributionAmt.getActualValue().compareTo(BigDecimal.ZERO) == 0){
			getFinanceDetailData();
			this.maxContributionAmt.setValue(this.finAmount);
		}
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Fetching FinanceMain Data
	 */
	@SuppressWarnings("unchecked")
	public void getFinanceDetailData(){
		logger.debug("Entering");

		if (getFinanceMainDialogCtrl() != null) {
			try {
				if (financeMainDialogCtrl.getClass().getMethod("prepareContributor") != null) {
					List<Object> list = (List<Object>) financeMainDialogCtrl.getClass().getMethod("prepareContributor").invoke(financeMainDialogCtrl);

					if(list != null && list.size() > 0){
						this.finAmount = (BigDecimal) list.get(0);
						this.finCcy = (String) list.get(1);
						this.downPayment = (BigDecimal) list.get(2);
					}
				}
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Double Click Event on Contribution Details
	 * @param event
	 * @throws Exception
	 */
	public void onFinContributorItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxFinContributor.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinContributorDetail finContributorDetail = (FinContributorDetail) item.getAttribute("data");

			if (finContributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finContributorDetail", finContributorDetail);
				map.put("contributorDetailsDialogCtrl", this);
				map.put("roleCode", roleCode);
				map.put("moduleType", "");
				map.put("finCcy", this.finCcy);
				map.put("finAmount", PennantAppUtil.unFormateAmount(this.finAmount, formatter));
				map.put("financeDetail", getFinanceDetail());
				BigDecimal maxAmt = PennantAppUtil.unFormateAmount(this.maxContributionAmt.getActualValue(), formatter);

				map.put("balInvestAmount", maxAmt.subtract(curContributionCalAmt).add(finContributorDetail.getContributorInvest()));

				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceContributor/FinContributorDetailDialog.zul",
							window_ContributorDetailsDialog, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Generate the Finance Contributor Details List in the FinanceMainDialogCtrl and
	 * set the list in the listBoxFinContributor 
	 */
	public void doFillFinContributorDetails(List<FinContributorDetail> contributorDetails, boolean doCalculations) {
		logger.debug("Entering");
		getFinanceDetailData();
		setContributorsList(contributorDetails);
		contributionCalculations(contributorDetails, doCalculations);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Resetting List by default to Bank Contribution
	 * @param contributorDetails
	 * @param doCalculations
	 */
	public void doResetContributorDetails() {
		logger.debug("Entering");
		getFinanceDetailData();
		contributionCalculations(getContributorsList(), true);
		logger.debug("Leaving");
	}

	/**
	 * Method for calculations of Contribution Details Amount , Mudarib rate and Total Investments
	 * @param contributorDetails
	 */
	private void contributionCalculations(List<FinContributorDetail> contributorDetails, boolean doCalculations){
		logger.debug("Entering");

		this.listBoxFinContributor.setSizedByContent(true);
		this.listBoxFinContributor.getItems().clear();
		
		//Adding AHB Share value
		BigDecimal finAmt = PennantAppUtil.unFormateAmount(this.finAmount.subtract(this.downPayment),formatter);
		if(finAmt.compareTo(BigDecimal.ZERO) <= 0){
			return;
		}
		addBankShareOrTotal(true, calcBankShare(contributorDetails));
		
		if(contributorDetails!= null && contributorDetails.size() > 0){

			Listitem item = null;
			Listcell lc = null;
			curContributionCalAmt = BigDecimal.ZERO;
			int curContributorsCount = 0;
			BigDecimal ttlMudaribInvest = BigDecimal.ZERO;
			
			int format = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

			for (FinContributorDetail detail : contributorDetails) {

				item = new Listitem();

				lc = new Listcell(detail.getLovDescContributorCIF()+" - "+detail.getContributorName());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(
						detail.getContributorInvest(),format));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatAccountNumber(detail.getInvestAccount()));
				lc.setParent(item);
				lc = new Listcell(DateUtility.formatToLongDate(detail.getInvestDate()));
				lc.setParent(item);
				lc = new Listcell(DateUtility.formatToLongDate(detail.getRecordDate()));
				lc.setParent(item);

				BigDecimal ttlInvestPerc = (detail.getContributorInvest().divide(finAmt,9,RoundingMode.HALF_DOWN)).multiply(new BigDecimal(100));
				detail.setTotalInvestPerc(ttlInvestPerc);
				lc = new Listcell(PennantApplicationUtil.formatRate(ttlInvestPerc.doubleValue(), 2)+" %");
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatRate(detail.getMudaribPerc().doubleValue(), 2)+" %");
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(detail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", detail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onFinContributorItemDoubleClicked");

				this.listBoxFinContributor.appendChild(item);

				if(doCalculations){
					if(!(PennantConstants.RECORD_TYPE_CAN.equals(detail.getRecordType()) || 
							PennantConstants.RECORD_TYPE_DEL.equals(detail.getRecordType()))){

						curContributionCalAmt = curContributionCalAmt.add(detail.getContributorInvest());
						curContributorsCount = curContributorsCount + 1;

						ttlMudaribInvest = ttlMudaribInvest.add(detail.getContributorInvest().multiply(
								detail.getMudaribPerc()).divide(new BigDecimal(100), 2,RoundingMode.HALF_DOWN));
					}
				}
			}

			if(doCalculations && curContributionCalAmt.compareTo(BigDecimal.ZERO)>0){

				this.curContributionAmt.setValue(PennantAppUtil.formateAmount(curContributionCalAmt, formatter));
				this.curContributors.setValue(curContributorsCount);
				this.curBankInvest.setValue(PennantAppUtil.formateAmount(finAmt.subtract(curContributionCalAmt), formatter));
				this.avgMudaribRate.setValue(ttlMudaribInvest.divide(curContributionCalAmt,9,RoundingMode.HALF_DOWN).multiply(new BigDecimal(100)));
			} else {
				curContributionCalAmt = PennantAppUtil.unFormateAmount(this.curContributionAmt.getValue(), formatter);
			}
			
		}
		
		//Adding Totals for Total List
		addBankShareOrTotal(false, finAmt);
		
		logger.debug("Leaving");
	}
	
	private void addBankShareOrTotal(boolean isBankShare, BigDecimal contribution){
		Listitem item = new Listitem();
		
		String label = Labels.getLabel("label_TotalContribution");
		String sClass = "font-weight:bold;";
		String mudaribPer = "";
		
		String rcdDate = "";
		if(isBankShare){
			label = SysParamUtil.getValueAsString("BANK_NAME");
			sClass = "";
			rcdDate = DateUtility.getAppDate(DateFormat.LONG_DATE);
			mudaribPer = "0.00%";
		} else {
			item.setStyle("background-color: #C0EBDF;");
		}

		Listcell lc = new Listcell(label);
		lc.setStyle(sClass);
		lc.setParent(item);
		
		lc = new Listcell(PennantAppUtil.amountFormate(contribution, formatter));
		lc.setStyle(sClass+"text-align:right;");
		lc.setParent(item);
		
		lc = new Listcell();
		lc.setParent(item);
		
		lc = new Listcell();
		lc.setParent(item);
				
		lc = new Listcell(rcdDate);
		lc.setParent(item);
		
		BigDecimal finAmt = PennantAppUtil.unFormateAmount(this.finAmount.subtract(this.downPayment),formatter);
		BigDecimal ttlInvestPerc = (contribution.divide(finAmt,9,RoundingMode.HALF_DOWN)).multiply(new BigDecimal(100));
		lc = new Listcell(PennantApplicationUtil.formatRate(ttlInvestPerc.doubleValue(), 2)+" %");
		lc.setStyle(sClass+"text-align:right;");
		lc.setParent(item);
		
		lc = new Listcell(mudaribPer);
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		
		lc = new Listcell();
		lc.setParent(item);
		
		lc = new Listcell();
		lc.setParent(item);
		this.listBoxFinContributor.appendChild(item);
	}
	
	private BigDecimal calcBankShare(List<FinContributorDetail> contributorDetails){
		
		BigDecimal totContrInvst = BigDecimal.ZERO;
		
		if(contributorDetails!= null && contributorDetails.size() > 0){
			for (FinContributorDetail detail : contributorDetails) {
				totContrInvst = totContrInvst.add(detail.getContributorInvest());
			}
		}
		
		BigDecimal finAmt = PennantAppUtil.unFormateAmount(this.finAmount.subtract(this.downPayment),formatter);
		return finAmt.subtract(totContrInvst);
	}
	
	/**
	 * append Finance Basic detail header to current window
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

	public void doSetFinAmount(BigDecimal finAmount,BigDecimal downpayBank){
		logger.debug("Entering");
		this.maxContributionAmt.setValue(finAmount.subtract(downpayBank));
		logger.debug("Leaving");
	}
	
	
	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setContributorsList(List<FinContributorDetail> contributorsList) {
		this.contributorsList = contributorsList;
	}
	public List<FinContributorDetail> getContributorsList() {
		return contributorsList;
	}

	public void setOldVar_ContributorList(List<FinContributorDetail> oldVarContributorList) {
		this.oldVar_ContributorList = oldVarContributorList;
	}
	public List<FinContributorDetail> getOldVar_ContributorList() {
		return oldVar_ContributorList;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}
	
	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

}