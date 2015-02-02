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
 *											    											*
 * FileName    		:  WIRateChangeDialogCtrl.java                          	            * 	  
 *                                                                    			    		*
 * Author      		:  PENNANT TECHONOLOGIES              				    				*
 *                                                                  			    		*
 * Creation Date    :  05-10-2011    							    						*
 *                                                                  			    		*
 * Modified Date    :  05-10-2011    							    						*
 *                                                                  			    		*
 * Description 		:                                             			    			*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-10-2011       Pennant	                 0.1                                        	* 
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

package com.pennant.webui.finance.additional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.RateValidator;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

public class RateChangeDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -4578996988245614938L;
	private final static Logger logger = Logger.getLogger(RateChangeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_RateChangeDialog; 					// autowired
	protected Decimalbox rateChange; 							// autowired
	protected Combobox cbRateChangeFromDate;  					// autowired
	protected Combobox cbRateChangeToDate;  					// autowired
	protected Textbox baseRate;  								// autoWired
	protected Textbox splRate;  								// autoWired
	protected Decimalbox marginRate; 							// autowired
	protected Datebox tillDate; 	 						    // autowired
	protected Combobox cbReCalType; 							// autowired
	protected Decimalbox effectiveRate; 						//autowired
	protected Button btnSearchWIBaseRate; 						// autoWire
	protected Button btnSearchWISplRate; 						// autoWire
	protected Row baseRateRow;	 								// autoWire
	protected Row splRateRow; 									// autoWire
	protected Row marginRateRow; 								// autoWire
	protected Row row_effectiveRate; 								// autoWire
	protected Row rateAmountRow; 								// autoWire
	protected Row tillDateRow; 									// autoWire

	// not auto wired vars
	private FinScheduleData finScheduleData; 					// overhanded per param
	private FinanceScheduleDetail financeScheduleDetail; 		// overhanded per param
	private transient ScheduleDetailDialogCtrl financeMainDialogCtrl;
	
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient BigDecimal oldVar_rateChange;
	private transient String oldVar_rateChangeFromDate;
	private transient String oldVar_rateChangeToDate;
	private transient String oldVar_reCalType;
	private transient String oldVar_baseRate;
	private transient String oldVar_splRate;
	private transient BigDecimal oldVar_marginRate;
	Calendar calender=Calendar.getInstance();
	private transient boolean validationOn;
	final List<ValueLabel>	      recalTypes	              = PennantStaticListUtil.getSchCalCodes();

	/**
	 * default constructor.<br>
	 */
	public RateChangeDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FinanceMain object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RateChangeDialog(Event event) throws Exception {
		logger.debug(event.toString());

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("finScheduleData")) {
			this.finScheduleData = (FinScheduleData) args.get("finScheduleData");
			setFinScheduleData(this.finScheduleData);
		} else {
			setFinScheduleData(null);
		}
		
		if (args.containsKey("financeScheduleDetail")) {
			this.setFinanceScheduleDetail((FinanceScheduleDetail) args.get("financeScheduleDetail"));
			setFinanceScheduleDetail(this.financeScheduleDetail);
		} else {
			setFinanceScheduleDetail(null);
		}

		// READ OVERHANDED params !
		// we get the WIFFinanceMainDialogCtrl controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete WIFFinanceMain here.
		if (args.containsKey("financeMainDialogCtrl")) {
			setFinanceMainDialogCtrl((ScheduleDetailDialogCtrl) args.get("financeMainDialogCtrl"));
		} else {
			setFinanceMainDialogCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinScheduleData());
		this.window_RateChangeDialog.doModal();
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinanceScheduleDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinScheduleData aFinScheduleData) throws InterruptedException {
		logger.debug("Entering");
		if (aFinScheduleData == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aFinScheduleData = new FinScheduleData();

			setFinScheduleData(aFinScheduleData);
		} else {
			setFinScheduleData(aFinScheduleData);
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinScheduleData);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_RateChangeDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.rateChange.setMaxlength(13);
		this.rateChange.setFormat(PennantConstants.rateFormate9);
		this.rateChange.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.rateChange.setScale(9);
		this.tillDate.setFormat(PennantConstants.dateFormat);
		this.baseRate.setMaxlength(8);
		this.splRate.setMaxlength(8);
		this.marginRate.setMaxlength(13);
		this.marginRate.setFormat(PennantConstants.rateFormate9);
		this.marginRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.marginRate.setScale(9);
		this.effectiveRate.setMaxlength(13);
		this.effectiveRate.setFormat(PennantConstants.rateFormate9);
		this.effectiveRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.effectiveRate.setScale(9);
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.rateChange.setReadonly(true);
		this.cbRateChangeFromDate.setReadonly(true);
		this.cbRateChangeToDate.setReadonly(true);
		this.cbReCalType.setReadonly(true);
		this.btnSearchWIBaseRate.setDisabled(true);
		this.btnSearchWISplRate.setDisabled(true);
		this.tillDate.setReadonly(true);
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
		this.oldVar_rateChange = this.rateChange.getValue();
		this.oldVar_rateChangeFromDate = this.cbRateChangeFromDate.getValue();
		this.oldVar_rateChangeToDate = this.cbRateChangeToDate.getValue();
		this.oldVar_reCalType = this.cbReCalType.getValue();
		this.oldVar_baseRate = this.baseRate.getValue();
		this.oldVar_splRate = this.splRate.getValue();
		this.oldVar_marginRate = this.marginRate.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            FinanceMain
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");
		FinanceMain aFinanceMain = aFinSchData.getFinanceMain();
		if(getFinanceScheduleDetail()!=null && getFinanceScheduleDetail().getBaseRate()!=null) {
				this.baseRateRow.setVisible(true);
				this.splRateRow.setVisible(true);
				this.marginRateRow.setVisible(true);
				this.row_effectiveRate.setVisible(true);
		} else if(((aFinanceMain.getGraceBaseRate()!=null) && 
				(getFinanceScheduleDetail()!=null && 
						(getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.GRACE) ||
						 getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.GRACE_END)))) ||
						((aFinanceMain.getRepayBaseRate()!=null) && (getFinanceScheduleDetail()!=null && 
						(getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.REPAY)||
						(getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.GRACE_END)))))){
			this.baseRateRow.setVisible(true);
			this.splRateRow.setVisible(true);
			this.marginRateRow.setVisible(true);
			this.row_effectiveRate.setVisible(true);
			this.marginRate.setValue(aFinanceMain.getGrcMargin());
		} else if(getFinanceScheduleDetail()==null && (aFinanceMain.getRepayBaseRate()!=null)){
			this.baseRateRow.setVisible(true);
			this.splRateRow.setVisible(true);
			this.marginRateRow.setVisible(true);
			this.row_effectiveRate.setVisible(true);
			this.marginRate.setValue(aFinanceMain.getRepayMargin());
		}else {
			this.rateChange.setVisible(true);
			this.rateAmountRow.setVisible(true);
		}

		this.cbRateChangeFromDate.setVisible(true);
		fillSchFromDates(this.cbRateChangeFromDate,
				aFinSchData.getFinanceScheduleDetails());
		this.cbRateChangeToDate.setVisible(true);
		if(getFinanceScheduleDetail() != null ) {
			fillSchToDates(this.cbRateChangeToDate,
					aFinSchData.getFinanceScheduleDetails(), getFinanceScheduleDetail().getSchDate() );
		}else {
			fillSchToDates(this.cbRateChangeToDate,
					aFinSchData.getFinanceScheduleDetails(), aFinanceMain.getFinStartDate() );
		}

		// check the values and set in respective fields.
		// If schedule detail is not null i.e. existing one
		if (getFinanceScheduleDetail() != null) {
			if(getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.GRACE) ||
					getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.GRACE_END)){
				if(getFinanceScheduleDetail().getBaseRate()!=null){
					this.baseRate.setValue(getFinanceScheduleDetail().getBaseRate());
					this.splRate.setValue(getFinanceScheduleDetail().getSplRate());
					this.marginRate.setValue(getFinanceScheduleDetail().getMrgRate());
					RateDetail rateDetail = RateUtil.rates(getFinanceScheduleDetail().getBaseRate(),
							getFinanceScheduleDetail().getSplRate(), getFinanceScheduleDetail().getMrgRate());
					this.effectiveRate.setValue(rateDetail.getNetRefRateLoan());
				}else {
					this.rateChange.setValue(getFinanceScheduleDetail().getActRate());
					this.effectiveRate.setValue(getFinanceScheduleDetail().getActRate());
				}
			}else if((getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.REPAY)) ||
					(getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.GRACE_END))) {
				if(getFinanceScheduleDetail().getBaseRate()!=null) {
					this.baseRate.setValue(getFinanceScheduleDetail().getBaseRate());
					this.splRate.setValue(getFinanceScheduleDetail().getSplRate());
					this.marginRate.setValue(getFinanceScheduleDetail().getMrgRate());
					RateDetail rateDetail = RateUtil.rates(getFinanceScheduleDetail().getBaseRate(),
							getFinanceScheduleDetail().getSplRate(), getFinanceScheduleDetail().getMrgRate());
					this.effectiveRate.setValue(rateDetail.getNetRefRateLoan());
				}else {
					this.rateChange.setValue(getFinanceScheduleDetail().getActRate());
					this.effectiveRate.setValue(getFinanceScheduleDetail().getActRate());
				}
			}
		}

		//Check if schedule header is null or not and set the recal type fields.
		if(aFinSchData.getFinanceMain() != null ) {
			fillComboBox(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType(), recalTypes, ",TILLDATE,ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,");
		}else {
			fillComboBox(this.cbReCalType, "", recalTypes, ",TILLDATE,ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,");
		}
		logger.debug("Leaving");
	}

	/** To fill schedule dates */
	public void fillSchFromDates(Combobox dateCombobox,
			List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");
		this.cbRateChangeFromDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		
		boolean includedPrvSchTerm  = false;
		
		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				//Not Review Date
				if (!curSchd.isRvwOnSchDate() ) {
					continue;
				}

				//Profit Paid (Partial/Full) or Principal Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0 || curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					this.cbRateChangeFromDate.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					dateCombobox.appendChild(comboitem);
					dateCombobox.setSelectedItem(comboitem);
					includedPrvSchTerm = false;
					continue;
				}

				//Schedule Date Passed last review date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayRvwDate())) {
					continue;
				}

				//Schedule Date Passed last repay date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayDate())) {
					continue;
				}
				
				/*//Schedule Date Passed last capitalize date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayCpzDate())) {
					continue;
				}*/

				//Profit repayment on frequency is TRUE
				if (getFinScheduleData().getFinanceMain().isFinRepayPftOnFrq()) {
					if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayPftDate())) {
						continue;
					}
				}
				
				if((i-1 > 0) && !includedPrvSchTerm){
					
					FinanceScheduleDetail prvSchd = financeScheduleDetails.get(i-1);
					
					comboitem = new Comboitem();
					comboitem.setLabel(PennantAppUtil.formateDate(prvSchd.getSchDate(), PennantConstants.dateFormate)+" "+prvSchd.getSpecifier());
					comboitem.setValue(prvSchd.getSchDate());
					comboitem.setAttribute("fromSpecifier",prvSchd.getSpecifier());
					dateCombobox.appendChild(comboitem);
					
					includedPrvSchTerm = true;
				}
				
				if(i==0){
					includedPrvSchTerm = true;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(PennantAppUtil.formateDate(curSchd.getSchDate(), PennantConstants.dateFormate)+" "+curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				comboitem.setAttribute("fromSpecifier",curSchd.getSpecifier());

				if (getFinanceScheduleDetail() != null) {
					dateCombobox.appendChild(comboitem);
					if(curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate())==0) {
						dateCombobox.setSelectedItem(comboitem);
					}
				} else { 
					dateCombobox.appendChild(comboitem);
				}
			}
		}
		logger.debug("Leaving");
	}

	/** To fill schedule dates in todate combo */
	public void fillSchToDates(Combobox dateCombobox,
			List<FinanceScheduleDetail> financeScheduleDetails, Date fillAfter) {
		logger.debug("Entering");
		this.cbRateChangeToDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		Date maturityDate = getFinScheduleData().getFinanceMain().getMaturityDate();

		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				//Not Review Date
				if (!curSchd.isRvwOnSchDate()) {
					if (curSchd.getSchDate().compareTo(maturityDate)!=0) {
						continue;	
					}
				}

				//Profit Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				//Principal Paid (Partial/Full)
				if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				//Schedule Date Passed last review date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayRvwDate())) {
					continue;
				}

				//Schedule Date Passed last repay date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayDate())) {
					continue;
				}

				/*//Schedule Date Passed last capitalize date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayCpzDate())) {
					continue;
				}*/

				//Profit repayment on frequency is TRUE
				if (getFinScheduleData().getFinanceMain().isFinRepayPftOnFrq()) {
					if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayPftDate())) {
						continue;
					}
				}

				comboitem = new Comboitem();
				comboitem.setLabel(PennantAppUtil.formateDate(curSchd.getSchDate(), PennantConstants.dateFormate)+" "+curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				comboitem.setAttribute("toSpecifier",curSchd.getSpecifier());
				if (getFinanceScheduleDetail() != null &&  curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate()) >= 0) {
					dateCombobox.appendChild(comboitem);
					if(curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate())==0) {
						dateCombobox.setSelectedItem(comboitem);
					}
				} else if(curSchd.getSchDate().compareTo(fillAfter) > 0) {
					dateCombobox.appendChild(comboitem);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 * @throws InterruptedException 
	 */
	public void doWriteComponentsToBean() throws InterruptedException {
		logger.debug("Entering");
		//doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			this.baseRate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.splRate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.marginRate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(!this.rateChange.isVisible() && this.rateChange.getValue()==null){
				this.rateChange.setValue(BigDecimal.ZERO);
			}else {
				this.rateChange.getValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isValidComboValue(
					this.cbRateChangeFromDate,
					Labels.getLabel("label_RateChangeDialog_FromDate.value"))) {
				getFinScheduleData().getFinanceMain().setEventFromDate((Date)this.cbRateChangeFromDate.getSelectedItem().getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(
					this.cbRateChangeToDate,
					Labels.getLabel("label_RateChangeDialog_ToDate.value"))
					&& this.cbRateChangeFromDate.getSelectedIndex() != 0) {
				if (((Date) this.cbRateChangeToDate.getSelectedItem()
						.getValue())
						.compareTo((Date) this.cbRateChangeFromDate
								.getSelectedItem().getValue()) < 0) {
					throw new WrongValueException(
							this.cbRateChangeToDate,
							Labels.getLabel(
									"DATE_ALLOWED_AFTER",
									new String[]{
											Labels.getLabel("label_RateChangeDialog_ToDate.value"),
											Labels.getLabel("label_RateChangeDialog_FromDate.value")}));
				} else {
					getFinScheduleData().getFinanceMain().setEventToDate((Date)this.cbRateChangeToDate.getSelectedItem().getValue());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try{
			if (isValidComboValue(
					this.cbReCalType,
					Labels.getLabel("label_RateChangeDialog_RecalType.value"))
					&& this.cbReCalType.getSelectedIndex() != 0) {
				getFinScheduleData().getFinanceMain().setRecalType(this.cbReCalType.getSelectedItem().getValue().toString());

			}
		}catch (WrongValueException we) {
			wve.add(we);
		}
		if(this.tillDateRow.isVisible()){
			try {
				if(this.tillDate.getValue().compareTo(
						getFinScheduleData().getFinanceMain().getFinStartDate())<0 ||
						this.tillDate.getValue().compareTo(getFinScheduleData().getFinanceMain().getMaturityDate())>0){
					throw new WrongValueException(
							this.tillDate,
							Labels.getLabel(
									"DATE_RANGE",
									new String[]{
											Labels.getLabel("label_RateChangeDialog_TillDate.value"),
											PennantAppUtil.formateDate(getFinScheduleData().getFinanceMain().getFinStartDate(),
													PennantConstants.dateFormate),
													PennantAppUtil.formateDate(getFinScheduleData().getFinanceMain().getMaturityDate(),
															PennantConstants.dateFormate)
									}));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		setFinScheduleData(ScheduleCalculator.changeRate(getFinScheduleData(),this.baseRate.getValue(),
				this.splRate.getValue(), this.marginRate.getValue()==null? BigDecimal.ZERO:this.marginRate.getValue(),
				this.rateChange.getValue()==null? BigDecimal.ZERO:this.rateChange.getValue(), true));
		
		//Show Error Details in Schedule Maintainance
		if(getFinScheduleData().getErrorDetails() != null && !getFinScheduleData().getErrorDetails().isEmpty()){
			PTMessageUtils.showErrorMessage(getFinScheduleData().getErrorDetails().get(0));
			getFinScheduleData().getErrorDetails().clear();
		}else{
			getFinScheduleData().setSchduleGenerated(true);

			if(getFinanceMainDialogCtrl()!=null){
				getFinanceMainDialogCtrl().doFillScheduleList(getFinScheduleData());
			}
			this.window_RateChangeDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (this.rateChange.isVisible()) {
			this.rateChange.setConstraint(new PTStringValidator(Labels.getLabel("label_RateChangeDialog_Rate.value"),null,true));
		}
		if (this.marginRateRow.isVisible()) {
			this.marginRate.setConstraint(new RateValidator(13, 9, 
							Labels.getLabel("label_RateChangeDialog_MarginRate.value")));
		}
		if(this.baseRateRow.isVisible()) {
			this.baseRate.setConstraint(new PTStringValidator(Labels.getLabel("label_RateChangeDialog_BaseRate.value"),null,true));
		}
		if (this.tillDate.isVisible()) {
			this.tillDate.setConstraint(new PTDateValidator(Labels.getLabel("label_RateChangeDialog_TillDate.value"),true));
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method to clear error messages
	 * 
	 * */
	private void doClearMessage() {
		logger.debug("Entering");
		setValidationOn(false);
		this.baseRate.clearErrorMessage();
		this.rateChange.clearErrorMessage();
		this.tillDate.clearErrorMessage();
		this.cbRateChangeFromDate.clearErrorMessage();
		this.cbRateChangeToDate.clearErrorMessage();
		this.cbReCalType.clearErrorMessage();
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnAddReviewRate(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if(getFinanceScheduleDetail()!=null){
			if(isDataChanged()){
				doSave();
			}else{
				PTMessageUtils.showErrorMessage("No Data has been changed.");
				// TODO close dialog if message not needed.
				//this.window_WIApplyChangeDialog.onClose();
			}
		}else{
			doSave();
		}
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
		doClose();
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when close event is occurred. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * */
	public void onClose(Event event)throws InterruptedException{
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
		
	}

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
		doClearMessage();
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

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
			logger.debug("isDataChanged : false");
		}

		if (close) {
			this.window_RateChangeDialog.onClose();
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
		logger.debug("Entering");

		if (this.oldVar_rateChange != this.rateChange.getValue()) {
			return true;
		}
		if (this.oldVar_rateChangeFromDate != this.cbRateChangeFromDate
				.getValue()) {
			return true;
		}
		if (this.oldVar_rateChangeToDate != this.cbRateChangeToDate
				.getValue()) {
			return true;
		}
		if (this.oldVar_reCalType != this.cbReCalType
				.getValue()) {
			return true;
		}
		if (this.oldVar_baseRate != this.baseRate.getValue()) {
			return true;
		}
		if (this.oldVar_splRate != this.splRate.getValue()) {
			return true;
		}
		if(this.oldVar_marginRate != this.marginRate.getValue()) {
			return true;
		}
		return false;
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		doSetValidation();
		doWriteComponentsToBean();
		logger.debug("Leaving");
	}

	/** To get the BaseRateCode LOV List From RMTBaseRateCodes Table 
	 * @throws InterruptedException */
	public void onClick$btnSearchWIBaseRate(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_RateChangeDialog, "BaseRateCode");
		if (dataObject instanceof String) {
			this.baseRate.setValue(dataObject.toString());
		} else {
			BaseRateCode details = (BaseRateCode) dataObject;
			if (details != null) {
				this.baseRate.setValue(details.getBRType());
			}
			RateDetail rateDetail = RateUtil.rates(this.baseRate.getValue(),
					this.splRate.getValue(), this.marginRate.getValue()==null?BigDecimal.ZERO:this.marginRate.getValue());
			if(rateDetail.getErrorDetails() == null){
				this.effectiveRate.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(),2));
			} else {
				PTMessageUtils.showErrorMessage(ErrorUtil.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
				this.baseRate.setValue("");
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/** To get the SplRateCode LOV List From RMTSplRateCodes Table
	 * 
	 * @throws ParseException 
	 * @throws InterruptedException */

	public void onClick$btnSearchWISplRate(Event event) throws ParseException, InterruptedException {
		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_RateChangeDialog, "SplRateCode");
		if (dataObject instanceof String) {
			this.splRate.setValue(dataObject.toString());
		} else {
			SplRateCode details = (SplRateCode) dataObject;
			if (details != null) {
				this.splRate.setValue(details.getSRType());
			}
			RateDetail rateDetail = RateUtil.rates(this.baseRate.getValue(),
					this.splRate.getValue(),this.marginRate.getValue()==null?BigDecimal.ZERO:this.marginRate.getValue());
			if(rateDetail.getErrorDetails() == null){
				this.effectiveRate.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(),2));
			} else {
				PTMessageUtils.showErrorMessage(ErrorUtil.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
				this.splRate.setValue("");
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	//Enable till date field if the selected recalculation type is TIIDATE
	public void onChange$cbReCalType(Event event) {
		logger.debug("Entering" + event.toString());
		this.tillDate.clearErrorMessage();
		if(this.cbReCalType.getSelectedItem().getValue().toString()
				.equals(CalculationConstants.RPYCHG_TILLDATE)){
			this.tillDateRow.setVisible(true);
		}else {
			this.tillDate.setText("");
			this.tillDateRow.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/** To calculate the effective rate value including margin rate.*/
	public void onChange$marginRate(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		RateDetail rateDetail = RateUtil.rates(this.baseRate.getValue(),
				this.splRate.getValue(),this.marginRate.getValue()==null?BigDecimal.ZERO:this.marginRate.getValue());
		if(rateDetail.getErrorDetails() == null){
			this.effectiveRate.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(),2));
		} else {
			PTMessageUtils.showErrorMessage(ErrorUtil.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			this.splRate.setValue("");
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/** To fill todates based on selected from date*/
	public void onChange$cbRateChangeFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		
		this.baseRate.setValue("");
		this.splRate.setValue("");
		this.effectiveRate.setValue(BigDecimal.ZERO);
		fillComboBox(this.cbReCalType, "", recalTypes, ",TILLDATE,ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,");
		if (isValidComboValue(this.cbRateChangeFromDate,Labels.getLabel("label_RateChangeDialog_FromDate.value"))) {
			this.cbRateChangeToDate.getItems().clear();
			String frSpecifier = this.cbRateChangeFromDate.getSelectedItem().getAttribute("fromSpecifier").toString();
			fillSchToDates(this.cbRateChangeToDate, getFinScheduleData().getFinanceScheduleDetails(),
					(Date) this.cbRateChangeFromDate.getSelectedItem().getValue());
			if(frSpecifier.equals(CalculationConstants.GRACE) ||
					frSpecifier.equals(CalculationConstants.GRACE_END)) {
				if(getFinScheduleData().getFinanceMain().getGraceBaseRate()!=null) {
					this.baseRate.setValue(getFinScheduleData().getFinanceMain().getGraceBaseRate());
					this.splRate.setValue(getFinScheduleData().getFinanceMain().getGraceSpecialRate());
					RateDetail rateDetail = RateUtil.rates(getFinScheduleData().getFinanceMain().getGraceBaseRate(),
							getFinScheduleData().getFinanceMain().getGraceSpecialRate(), BigDecimal.ZERO);
					this.effectiveRate.setValue(rateDetail.getNetRefRateLoan());
				}
				
				if(frSpecifier.equals(CalculationConstants.GRACE) && 
						getFinScheduleData().getFinanceMain().getGrcSchdMthd().equals(CalculationConstants.NOPAY)){
					
					fillComboBox(this.cbReCalType, "", recalTypes, ",CURPRD,TILLDATE,ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,");
				}
				
			}else if((frSpecifier.equals(CalculationConstants.REPAY)) ||
					(frSpecifier.equals(CalculationConstants.GRACE_END))) {
				if(getFinScheduleData().getFinanceMain().getRepayBaseRate()!=null) {
					this.baseRate.setValue(getFinScheduleData().getFinanceMain().getRepayBaseRate());
					this.splRate.setValue(getFinScheduleData().getFinanceMain().getRepaySpecialRate());
					RateDetail rateDetail = RateUtil.rates(getFinScheduleData().getFinanceMain().getRepayBaseRate(),
							getFinScheduleData().getFinanceMain().getRepaySpecialRate(), BigDecimal.ZERO);
					this.effectiveRate.setValue(rateDetail.getNetRefRateLoan());
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}
	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public FinanceScheduleDetail getFinanceScheduleDetail() {
		return financeScheduleDetail;
	}
	public void setFinanceScheduleDetail(FinanceScheduleDetail financeScheduleDetail) {
		this.financeScheduleDetail = financeScheduleDetail;
	}

	public boolean isValidationOn() {
		return validationOn;
	}
	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public ScheduleDetailDialogCtrl getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(ScheduleDetailDialogCtrl financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}
	
}
