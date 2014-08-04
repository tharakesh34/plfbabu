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


import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/FinanceMainDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class ContributorDetailsDialogCtrl extends GFCBaseListCtrl<FinContributorDetail> implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(ContributorDetailsDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_ContributorDetailsDialog; 				// autoWired


	//Contributor Header Details Tab

	protected Intbox 		minContributors; 						// autoWired
	protected Intbox 		maxContributors; 						// autoWired
	protected Decimalbox 	minContributionAmt; 					// autoWired
	protected Decimalbox 	maxContributionAmt; 					// autoWired
	protected Intbox 		curContributors; 						// autoWired
	protected Decimalbox 	curContributionAmt; 					// autoWired
	protected Decimalbox 	curBankInvest; 							// autoWired
	protected Decimalbox 	avgMudaribRate; 						// autoWired
	protected Checkbox 		alwContributorsToLeave; 				// autoWired
	protected Checkbox 		alwContributorsToJoin; 					// autoWired
	protected Listbox 		listBoxFinContributor; 					// autoWired
	protected Button 		btnNewContributor; 						// autoWired
	protected Button 		btnPrintContributor; 					// autoWired
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

	// old value variables for edit mode. that we can check if something 
	// on the values are edited since the last initialization.

	//Contributor Header Details Tab
	private transient int 			oldVar_minContributors;
	private transient int 			oldVar_maxContributors;
	private transient BigDecimal 	oldVar_minContributionAmt;
	private transient BigDecimal 	oldVar_maxContributionAmt;
	private transient int 			oldVar_curContributors;
	private transient BigDecimal 	oldVar_curContributionAmt;
	private transient BigDecimal 	oldVar_curBankInvest;
	private transient BigDecimal 	oldVar_avgMudaribRate;
	private transient boolean 		oldVar_alwContributorsToLeave;
	private transient boolean 		oldVar_alwContributorsToJoin;

	// not auto wired variables
	private FinanceDetail 			financeDetail = null; 			
	private Object financeMainDialogCtrl = null;
	private FinScheduleData finScheduleData = null;

	private BigDecimal finAmount = BigDecimal.ZERO;
	private BigDecimal downPayment = BigDecimal.ZERO;
	private String finCcy = "";
	private String roleCode = "";

	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();

	/**
	 * default constructor.<br>
	 */
	public ContributorDetailsDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ContributorDetailsDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");
		}

		if (args.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) args.get("financeMainDialogCtrl");
		}

		if (args.containsKey("roleCode")) {
			this.roleCode = (String) args.get("roleCode");
		}

		getBorderLayoutHeight();
		this.listBoxFinContributor.setHeight(this.borderLayoutHeight- 190 - 52+"px");
		this.window_ContributorDetailsDialog.setHeight(this.borderLayoutHeight-80+"px");

		// set Field Properties
		doSetFieldProperties();

		// set Label Properties
		if (financeDetail.getFinScheduleData().getFinanceType().getLovDescProductCodeName().equals(
				PennantConstants.FINANCE_PRODUCT_MUSHARAKA)){
			doSetLabels();
		}

		doShowDialog(this.financeDetail);
		logger.debug("Leaving" + event.toString());
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		//Contributor Header Details
		int formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();

		this.minContributors.setMaxlength(4);
		this.maxContributors.setMaxlength(4);
		this.minContributionAmt.setMaxlength(18);
		this.minContributionAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.maxContributionAmt.setMaxlength(18);
		this.maxContributionAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.curContributors.setMaxlength(4);
		this.curContributionAmt.setMaxlength(18);
		this.curContributionAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.curBankInvest.setMaxlength(18);
		this.curBankInvest.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.avgMudaribRate.setMaxlength(13);
		this.avgMudaribRate.setScale(9);
		this.avgMudaribRate.setFormat(PennantConstants.rateFormate9);
		if (financeDetail.getFinScheduleData().getFinanceType().getLovDescProductCodeName().equals(PennantConstants.FINANCE_PRODUCT_MUSHARAKA)){
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

		int formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		this.finAmount = PennantApplicationUtil.formateAmount(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAmount(),formatter);
		this.downPayment = PennantApplicationUtil.formateAmount(getFinanceDetail().getFinScheduleData().getFinanceMain().getDownPayment(),formatter);
		this.finCcy = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy();

		//Contributor Header Details
		FinContributorHeader finContributorHeader = getFinanceDetail().getFinContributorHeader();
		if(finContributorHeader != null){
			this.minContributors.setValue(finContributorHeader.getMinContributors());
			this.maxContributors.setValue(finContributorHeader.getMaxContributors());

			if (finContributorHeader.isNewRecord()) {
				this.minContributionAmt.setValue(BigDecimal.ZERO);
			} else {
				this.minContributionAmt.setValue(PennantAppUtil.formateAmount(
						finContributorHeader.getMinContributionAmt(),formatter));
			}

			if (finContributorHeader.isNewRecord()) {
				this.maxContributionAmt.setValue(BigDecimal.ZERO);
			} else {
				this.maxContributionAmt.setValue(PennantAppUtil.formateAmount(
						finContributorHeader.getMaxContributionAmt(),formatter));
			}
			this.curContributors.setValue(finContributorHeader.getCurContributors());

			if (finContributorHeader.isNewRecord()) {
				this.curContributionAmt.setValue(BigDecimal.ZERO);
			} else {
				this.curContributionAmt.setValue(PennantAppUtil.formateAmount(
						finContributorHeader.getCurContributionAmt(),formatter));
			}

			if (finContributorHeader.isNewRecord()) {
				this.curBankInvest.setValue(BigDecimal.ZERO);
			} else {
				this.curBankInvest.setValue(PennantAppUtil.formateAmount(
						finContributorHeader.getCurBankInvestment(),formatter));
			}

			if (finContributorHeader.isNewRecord()) {
				this.avgMudaribRate.setValue(BigDecimal.ZERO);
			} else {
				this.avgMudaribRate.setValue(finContributorHeader.getAvgMudaribRate());
			}

			this.alwContributorsToLeave.setChecked(finContributorHeader.isAlwContributorsToLeave());
			this.alwContributorsToJoin.setChecked(finContributorHeader.isAlwContributorsToJoin());
		}
		if(getFinanceDetail().getFinContributorHeader() != null && 
				getFinanceDetail().getFinContributorHeader().getContributorDetailList() != null &&
				getFinanceDetail().getFinContributorHeader().getContributorDetailList().size() > 0){

			doFillFinContributorDetails(getFinanceDetail().getFinContributorHeader().getContributorDetailList(), false);
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

		// if afinanceMain == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (afinanceDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			setFinanceDetail(afinanceDetail);
		} else {
			setFinanceDetail(afinanceDetail);
		}

		this.btnNewContributor.setVisible(getUserWorkspace().isAllowed("FinanceMainDialog_btnNewContributor"));
		// set Read only mode accordingly if the object is new or not.
		if (afinanceDetail.isNewRecord()) {
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				doEdit();
			} else {
				doReadOnly();
			}
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents();

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();

			if (getFinanceMainDialogCtrl() != null) {
				try {
					financeMainDialogCtrl.getClass().getMethod("setContributorDetailsDialogCtrl", this.getClass()).invoke(financeMainDialogCtrl, this);

				} catch (Exception e) {
					logger.error(e);
				}
			}

			getBorderLayoutHeight();
			this.listBoxFinContributor.setHeight(this.borderLayoutHeight- 305 +"px");
			this.window_ContributorDetailsDialog.setHeight(this.borderLayoutHeight-80+"px");

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
	 * Stores the initial values in memory variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		doClearMessage();
		this.oldVar_minContributors = this.minContributors.intValue();
		this.oldVar_maxContributors = this.maxContributors.intValue();
		this.oldVar_minContributionAmt = this.minContributionAmt.getValue();
		this.oldVar_maxContributionAmt = this.maxContributionAmt.getValue();
		this.oldVar_curContributors = this.curContributors.intValue();
		this.oldVar_curContributionAmt = this.curContributionAmt.getValue();
		this.oldVar_curBankInvest = this.curBankInvest.getValue();
		this.oldVar_avgMudaribRate = this.avgMudaribRate.getValue();
		this.oldVar_alwContributorsToLeave = this.alwContributorsToLeave.isChecked();
		this.oldVar_alwContributorsToJoin = this.alwContributorsToJoin.isChecked();

		this.oldVar_ContributorList = this.contributorsList;
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from memory variables. <br>
	 */
	public void doResetInitValues() {
		logger.debug("Entering");

		//Contributor Header Details
		this.minContributors.setValue(this.oldVar_minContributors);
		this.maxContributors.setValue(this.oldVar_maxContributors);
		this.minContributionAmt.setValue(this.oldVar_minContributionAmt);
		this.maxContributionAmt.setValue(this.oldVar_maxContributionAmt);
		this.curContributors.setValue(this.oldVar_curContributors);
		this.curContributionAmt.setValue(this.oldVar_curContributionAmt);
		this.curBankInvest.setValue(this.oldVar_curBankInvest);
		this.avgMudaribRate.setValue(this.oldVar_avgMudaribRate);
		this.alwContributorsToLeave.setChecked(this.oldVar_alwContributorsToLeave);
		this.alwContributorsToJoin.setChecked(this.oldVar_alwContributorsToJoin);
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	public boolean isDataChanged(boolean close) {
		logger.debug("Entering");

		// To clear the Error Messages
		doClearMessage();

		//Contribution Details Tab
		if (this.minContributors.intValue() != this.oldVar_minContributors) {
			return true;
		}
		if (this.maxContributors.intValue() != this.oldVar_maxContributors) {
			return true;
		}
		if (this.minContributionAmt.getValue() != this.oldVar_minContributionAmt) {
			return true;
		}
		if (this.maxContributionAmt.getValue() != this.oldVar_maxContributionAmt) {
			return true;
		}
		if (this.alwContributorsToJoin.isChecked() != this.oldVar_alwContributorsToJoin) {
			return true;
		}
		if (this.alwContributorsToLeave.isChecked() != this.oldVar_alwContributorsToLeave) {
			return true;
		}

		if (this.contributorsList != this.oldVar_ContributorList) {
			return true;
		}

		logger.debug("Leaving");
		return false;
	}


	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		//Contribution Details Tab
		if (!this.minContributors.isReadonly()) {
			this.minContributors.setConstraint(new IntValidator(4,
					Labels.getLabel("label_FinanceMainDialog_MinContributors.value")));
		}

		if (!this.maxContributors.isReadonly()) {
			this.maxContributors.setConstraint(new IntValidator(4,
					Labels.getLabel("label_FinanceMainDialog_MaxContributors.value")));
		}

		if (!this.minContributionAmt.isDisabled()) {
			this.minContributionAmt.setConstraint(new AmountValidator(18, 0,
					Labels.getLabel("label_FinanceMainDialog_MinContributionAmt.value"), false));
		}

		if (!this.maxContributionAmt.isDisabled()) {
			this.maxContributionAmt.setConstraint(new AmountValidator(18, 0, 
					Labels.getLabel("label_FinanceMainDialog_MaxContributionAmt.value"), false));
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
	private void doClearMessage() {
		logger.debug("Entering");
		//FinanceMain Details Tab ---> 1. Basic Details
		this.minContributors.setErrorMessage("");
		this.maxContributors.setErrorMessage("");
		this.minContributionAmt.setErrorMessage("");
		this.maxContributionAmt.setErrorMessage("");

		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("FinanceMainDialog",roleCode);

		//Contribution Header Details
		this.minContributors.setReadonly(isReadOnly("FinanceMainDialog_minContributors"));
		this.maxContributors.setReadonly(isReadOnly("FinanceMainDialog_maxContributors"));
		this.minContributionAmt.setDisabled(isReadOnly("FinanceMainDialog_minContributionAmt"));
		this.maxContributionAmt.setDisabled(isReadOnly("FinanceMainDialog_maxContributionAmt"));
		this.curContributors.setReadonly(true);
		this.curContributionAmt.setDisabled(true);
		this.curBankInvest.setDisabled(true);
		this.avgMudaribRate.setDisabled(true);
		this.alwContributorsToLeave.setDisabled(isReadOnly("FinanceMainDialog_alwContributorsToLeave"));
		this.alwContributorsToJoin.setDisabled(isReadOnly("FinanceMainDialog_alwContributorsToJoin"));

		this.btnNewContributor.setVisible(getUserWorkspace().isAllowed("FinanceMainDialog_btnNewContributor"));
		this.btnPrintContributor.setVisible(false);

		logger.debug("Leaving");
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
				throw new WrongValueException(this.minContributors, Labels.getLabel("NUMBER_MINLIMIT" , 
						new String[]{ Labels.getLabel("label_FinanceMainDialog_MinContributors.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.maxContributors.intValue() != 0 && noOfContributors > this.maxContributors.intValue()){	
				throw new WrongValueException(this.maxContributors, Labels.getLabel("NUMBER_MAXLIMIT" , 
						new String[]{ Labels.getLabel("label_FinanceMainDialog_MaxContributors.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.minContributors.intValue() == 0){
				throw new WrongValueException(this.minContributors, Labels.getLabel("NUMBER_MINVALUE" , 
						new String[]{ Labels.getLabel("label_FinanceMainDialog_MinContributors.value"), " 0 "}));
			}

			header.setMinContributors(this.minContributors.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if(this.maxContributors.intValue() == 0){
				throw new WrongValueException(this.maxContributors, Labels.getLabel("NUMBER_MINVALUE" , 
						new String[]{ Labels.getLabel("label_FinanceMainDialog_MaxContributors.value")," 0 "}));
			}

			header.setMaxContributors(this.maxContributors.intValue());
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
			if(this.minContributionAmt.getValue() != null){	
				BigDecimal totalInvestAmt=BigDecimal.ZERO;
				for(FinContributorDetail items : contributorsList){
					BigDecimal investAmt = PennantAppUtil.unFormateAmount(
							items.getContributorInvest(),-3);
					totalInvestAmt=totalInvestAmt.add(investAmt);
				}
				if(totalInvestAmt.compareTo(this.minContributionAmt.getValue())<0){
					throw new WrongValueException(this.minContributionAmt, Labels.getLabel("AMOUNT_MIN" , 
							new String[]{ Labels.getLabel("label_FinanceMainDialog_MinContributionAmt.value")}));
				}}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if(this.minContributionAmt.getValue() == null || 
					this.minContributionAmt.getValue().compareTo(PennantAppUtil.formateAmount(aFinanceDetail.getFinScheduleData().getFinanceMain().getFinAmount(),aFinanceDetail
							.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()).subtract(PennantAppUtil.formateAmount(aFinanceDetail.getFinScheduleData().getFinanceMain().getDownPayment(),aFinanceDetail
									.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()))) > 0 ){
				throw new WrongValueException(this.minContributionAmt, Labels.getLabel("FIELD_IS_LESSER",
						new String[] { Labels.getLabel("label_FinanceMainDialog_MinContributionAmt.value"),
						Labels.getLabel("label_FinanceMainDialog_FinAmount.value")}));
			}

			header.setMinContributionAmt(PennantAppUtil.unFormateAmount(
					this.minContributionAmt.getValue(), aFinanceDetail
					.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if(this.maxContributionAmt.getValue() == null || 
					this.maxContributionAmt.getValue().compareTo(PennantAppUtil.formateAmount(aFinanceDetail.getFinScheduleData().getFinanceMain().getFinAmount(),aFinanceDetail
							.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()).subtract(PennantAppUtil.formateAmount(aFinanceDetail.getFinScheduleData().getFinanceMain().getDownPayment(),aFinanceDetail
									.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()))) > 0 ){
				throw new WrongValueException(this.maxContributionAmt,  Labels.getLabel("FIELD_IS_LESSER",
						new String[] { Labels.getLabel("label_FinanceMainDialog_MaxContributionAmt.value") ,
						Labels.getLabel("label_FinanceMainDialog_FinAmount.value")}));
			}

			header.setMaxContributionAmt(PennantAppUtil.unFormateAmount(
					this.maxContributionAmt.getValue(), aFinanceDetail
					.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.minContributionAmt.getValue() != null && 
					this.maxContributionAmt.getValue() != null){
				if(this.minContributionAmt.getValue().compareTo(
						this.maxContributionAmt.getValue()) > 0 ){
					throw new WrongValueException(this.minContributionAmt,  Labels.getLabel("FIELD_IS_LESSER",
							new String[] { Labels.getLabel("label_FinanceMainDialog_MinContributionAmt.value"),
							Labels.getLabel("label_FinanceMainDialog_MaxContributionAmt.value")}));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(getContributorsList() == null || getContributorsList().size() == 0){
				throw new WrongValueException(this.listBoxFinContributor,  Labels.getLabel("EMPTY_LIST",
						new String[] { Labels.getLabel("ContributorDetails")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		showErrorDetails(wve, tab);

		header.setCurContributors(this.curContributors.intValue());
		header.setCurContributionAmt(PennantAppUtil.unFormateAmount(
				this.curContributionAmt.getValue()== null ? BigDecimal.ZERO :this.curContributionAmt.getValue() , 
						aFinanceDetail.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));

		header.setCurBankInvestment(PennantAppUtil.unFormateAmount(
				this.curBankInvest.getValue()== null ? BigDecimal.ZERO :this.curBankInvest.getValue(), 
						aFinanceDetail.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));

		header.setAvgMudaribRate(this.avgMudaribRate.getValue());
		header.setAlwContributorsToLeave(this.alwContributorsToLeave.isChecked());
		header.setAlwContributorsToJoin(this.alwContributorsToJoin.isChecked());

		String rcdStatus = aFinanceDetail.getFinScheduleData().getFinanceMain().getRecordStatus();
		if (isWorkFlowEnabled()) {
			if (StringUtils.trimToEmpty(rcdStatus).equals("")) {
				if (aFinanceDetail.isNew()) {
					header.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					header.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					header.setNewRecord(true);
				}
			}
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			header.setRecordStatus(getFinanceDetail().getFinScheduleData().getFinanceMain().getRecordStatus());
		} else {
			header.setVersion(header.getVersion() + 1);
		}

		header.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		header.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		header.setUserDetails(getUserWorkspace().getLoginUserDetails());

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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Creations ++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
			PTMessageUtils.showErrorMessage("Ristricted Investment Details should be entered before printing.");
			return;
		}*/
		logger.debug("Leaving" + event.toString());
	}


	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++ New Button & Double Click Events for Finance Contributor List+++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Method for Creation of New Contributor for RIA Investment
	 */
	public void onClick$btnNewContributor(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		doClearMessage();
		getFinanceDetailData();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if(this.minContributors.intValue() == 0){
				throw new WrongValueException(this.minContributors,  Labels.getLabel("FIELD_NO_ZERO",
						new String[] {Labels.getLabel("label_FinanceMainDialog_MinContributors.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.maxContributors.intValue() == 0){
				throw new WrongValueException(this.maxContributors,  Labels.getLabel("FIELD_NO_ZERO",
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

		try{
			if(this.maxContributionAmt.getValue() == null || 
					this.maxContributionAmt.getValue().compareTo(this.finAmount.subtract(this.downPayment)) > 0 ){
				throw new WrongValueException(this.maxContributionAmt, Labels.getLabel("FIELD_IS_LESSER",
						new String[] { Labels.getLabel("label_FinanceMainDialog_MaxContributionAmt.value"),
						Labels.getLabel("label_FinanceMainDialog_FinAmount.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try{
			if(this.minContributionAmt.getValue() == null || 
					this.minContributionAmt.getValue().compareTo(this.finAmount.subtract(downPayment)) > 0 ){
				throw new WrongValueException(this.minContributionAmt, Labels.getLabel("FIELD_IS_LESSER",
						new String[] { Labels.getLabel("label_FinanceMainDialog_MinContributionAmt.value"),
						Labels.getLabel("label_FinanceMainDialog_FinAmount.value")}));
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

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			doRemoveValidation();
			// groupBox.set
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

		BigDecimal maxAmt = PennantAppUtil.unFormateAmount(this.maxContributionAmt.getValue(),
				getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());

		map.put("balInvestAmount", maxAmt.subtract(curContributionCalAmt == null ? BigDecimal.ZERO : curContributionCalAmt));

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceContributor/FinContributorDetailDialog.zul",
					window_ContributorDetailsDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
				logger.error(e);
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
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finContributorDetail", finContributorDetail);
				map.put("contributorDetailsDialogCtrl", this);
				map.put("roleCode", roleCode);
				map.put("moduleType", "");
				map.put("finCcy", this.finCcy);
				map.put("finAmount", PennantAppUtil.unFormateAmount(this.finAmount, 
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));

				BigDecimal maxAmt = PennantAppUtil.unFormateAmount(this.maxContributionAmt.getValue(),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());

				map.put("balInvestAmount", maxAmt.subtract(curContributionCalAmt).add(finContributorDetail.getContributorInvest()));

				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceContributor/FinContributorDetailDialog.zul",
							window_ContributorDetailsDialog, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
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
		setContributorsList(contributorDetails);
		contributionCalculations(contributorDetails, doCalculations);
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
		if(contributorDetails!= null && contributorDetails.size() > 0){

			Listitem item = null;
			Listcell lc = null;
			curContributionCalAmt = BigDecimal.ZERO;
			int curContributorsCount = 0;
			BigDecimal ttlMudaribInvest = BigDecimal.ZERO;

			BigDecimal finAmt = PennantAppUtil.unFormateAmount(this.finAmount,
					getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());

			for (FinContributorDetail detail : contributorDetails) {

				item = new Listitem();

				lc = new Listcell(detail.getLovDescContributorCIF()+" - "+detail.getContributorName());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(
						detail.getContributorInvest(),detail.getLovDescFinFormatter()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatAccountNumber(detail.getInvestAccount()));
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.formateDate(detail.getInvestDate(), PennantConstants.dateFormate));
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.formateDate(detail.getRecordDate(), PennantConstants.dateFormate));
				lc.setParent(item);

				BigDecimal ttlInvestPerc = (detail.getContributorInvest().divide(finAmt,9,RoundingMode.HALF_DOWN)).multiply(new BigDecimal(100));
				detail.setTotalInvestPerc(ttlInvestPerc);
				lc = new Listcell(ttlInvestPerc.toString());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(detail.getMudaribPerc().toString());
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

			if(doCalculations){

				this.curContributionAmt.setValue(PennantAppUtil.formateAmount(curContributionCalAmt,
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
				this.curContributors.setValue(curContributorsCount);
				this.curBankInvest.setValue(PennantAppUtil.formateAmount(finAmt.subtract(curContributionCalAmt),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
				this.avgMudaribRate.setValue(ttlMudaribInvest.divide(curContributionCalAmt,9,RoundingMode.HALF_DOWN).multiply(new BigDecimal(100)));
			}else{
				curContributionCalAmt = PennantAppUtil.unFormateAmount(this.curContributionAmt.getValue(),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
			}

		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public void setOldVar_ContributorList(List<FinContributorDetail> oldVar_ContributorList) {
		this.oldVar_ContributorList = oldVar_ContributorList;
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

}