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
 * FileName    		:  AddDisbursementDialogCtrl.java                          	            * 	  
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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.AddDisbursementService;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.OverdraftScheduleDetail;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.FinFeeDetailListCtrl;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class AddDisbursementDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = 4583907397986780542L;
	private static final Logger logger = Logger.getLogger(AddDisbursementDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AddDisbursementDialog;
	protected CurrencyBox disbAmount;
	protected Datebox fromDate;
	protected Combobox cbFromDate;
	protected Combobox cbTillDate;
	protected Combobox cbReCalType;
	protected Combobox cbSchdMthd; 
	protected Intbox adjTerms;
	protected Row fromDateRow;
	protected Row tillDateRow;
	protected Row numOfTermsRow;
	protected Row disbursementAccount;
	protected Row row_assetUtilization;
	protected Checkbox alwAssetUtilize;
	protected Textbox disbAcctId;
	protected Space space_disbAcctId;
	protected Button btnSearchDisbAcctId;
	protected Label label_AddDisbursementDialog_TillDate;
	protected Label label_AddDisbursementDialog_TillFromDate;
	protected Row	reCalTypeRow;
	protected Uppercasebox	serviceReqNo;
	protected Textbox		remarks;
	protected Row 	schdMthdRow;	
	
	private Date lastPaidDate = null;
	private BigDecimal grcEndDisbAmount = BigDecimal.ZERO;
	private String moduleDefiner = "";

	// not auto wired vars
	private FinScheduleData finScheduleData; // overhanded per param
	private FinanceScheduleDetail financeScheduleDetail; // overhanded per param
	private transient ScheduleDetailDialogCtrl scheduleDetailDialogCtrl;
	private transient FinFeeDetailListCtrl finFeeDetailListCtrl;
	private AccountInterfaceService accountInterfaceService;
	private AccountsService accountsService;

	private transient boolean validationOn;
	private transient AddDisbursementService addDisbursementService;

	/**
	 * default constructor.<br>
	 */
	public AddDisbursementDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FinanceMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AddDisbursementDialog(Event event)
			throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AddDisbursementDialog);

		try {
			if (arguments.containsKey("finScheduleData")) {
				this.finScheduleData = (FinScheduleData) arguments.get("finScheduleData");
				setFinScheduleData(this.finScheduleData);
			} else {
				setFinScheduleData(null);
			}

			if (arguments.containsKey("financeScheduleDetail")) {
				this.setFinanceScheduleDetail((FinanceScheduleDetail) arguments.get("financeScheduleDetail"));
				setFinanceScheduleDetail(this.financeScheduleDetail);
			} else {
				setFinanceScheduleDetail(null);
			}

			if (arguments.containsKey("isWIF")) {
				this.disbursementAccount.setVisible(false);
			}
			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			// READ OVERHANDED params !
			// we get the WIFFinanceMainDialogCtrl controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete WIFFinanceMain here.
			if (arguments.containsKey("financeMainDialogCtrl")) {
				setScheduleDetailDialogCtrl((ScheduleDetailDialogCtrl) arguments.get("financeMainDialogCtrl"));
			}

			if (arguments.containsKey("feeDetailListCtrl")) {
				setFinFeeDetailListCtrl((FinFeeDetailListCtrl) arguments.get("feeDetailListCtrl"));
			}
			
			
			if(getFinFeeDetailListCtrl() == null){
				this.setFinFeeDetailListCtrl((FinFeeDetailListCtrl) scheduleDetailDialogCtrl.getClass().
						getMethod("getFinFeeDetailListCtrl").invoke(scheduleDetailDialogCtrl));
			}
			
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinScheduleData());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_AddDisbursementDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		int format = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());
		// Empty sent any required attributes
		this.disbAmount.setMandatory(true);
		this.disbAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.disbAmount.setScale(format);
		this.disbAcctId.setMaxlength(20);
		this.fromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.adjTerms.setMaxlength(2);
		this.serviceReqNo.setMaxlength(20);
		this.remarks.setMaxlength(200);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinanceScheduleDetail
	 * @throws Exception
	 */
	private void doShowDialog(FinScheduleData aFinScheduleData) throws Exception {
		logger.debug("Entering");
		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinScheduleData);

			setDialog(DialogType.MODAL);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_AddDisbursementDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            FinanceMain
	 */
	private void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");
		

		if (getFinanceScheduleDetail() != null) {
			this.disbAmount.setValue(PennantAppUtil.formateAmount(getFinanceScheduleDetail().getDisbAmount(), 
					CurrencyUtil.getFormat(aFinSchData.getFinanceMain().getFinCcy())));
			this.fromDate.setValue(getFinanceScheduleDetail().getSchDate());
		}
		
		String excludeFields = ",EQUAL,PRI_PFT,PRI,";
		if(getFinanceScheduleDetail() != null) {
			if(getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_GRACE) ||
					getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_GRACE_END)) {
				fillComboBox(this.cbSchdMthd,getFinanceScheduleDetail().getSchdMethod(), PennantStaticListUtil.getScheduleMethods(), excludeFields);
				this.cbSchdMthd.setDisabled(true);
			}else {
				fillComboBox(this.cbSchdMthd,getFinanceScheduleDetail().getSchdMethod(), PennantStaticListUtil.getScheduleMethods(), ",GRCNDPAY,PFTCAP," );
				this.cbSchdMthd.setDisabled(true);
			}
		}else {
			fillComboBox(this.cbSchdMthd, "", PennantStaticListUtil.getScheduleMethods(), ",GRCNDPAY,PFTCAP,");
			this.cbSchdMthd.setDisabled(true);
		}

		// Check if schedule header is null or not and set the recal type
		// fields.
		if(StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinSchData.getFinanceMain().getProductCategory()) || 
				aFinSchData.getFinanceType().isDeveloperFinance()){
			this.reCalTypeRow.setVisible(false);
			this.fromDateRow.setVisible(false);
			this.schdMthdRow.setVisible(false);
		}else{
			
			String exclRecalTypes = ",CURPRD,ADJTERMS,ADDLAST,STEPPOS,";
			boolean isStepPOS = false;
			if(aFinSchData.getFinanceMain().isStepFinance() && aFinSchData.getFinanceMain().isAllowGrcPeriod() && 
					StringUtils.equals(aFinSchData.getFinanceMain().getStepType(), FinanceConstants.STEPTYPE_PRIBAL) &&
					(StringUtils.equals(aFinSchData.getFinanceMain().getScheduleMethod(), CalculationConstants.SCHMTHD_PRI) ||
					StringUtils.equals(aFinSchData.getFinanceMain().getScheduleMethod(), CalculationConstants.SCHMTHD_PRI_PFT))){
				exclRecalTypes = ",CURPRD,ADJTERMS,ADDLAST,";
				isStepPOS = true;
			}
			
			if(isStepPOS){
				fillComboBox(this.cbReCalType, CalculationConstants.RPYCHG_STEPPOS, PennantStaticListUtil.getDisbCalCodes(),exclRecalTypes);
			}else{
				fillComboBox(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType(), PennantStaticListUtil.getDisbCalCodes(),exclRecalTypes);
			}
			
			if (StringUtils.equals(getFinScheduleData().getFinanceMain().getRecalType(), CalculationConstants.RPYCHG_TILLDATE)) {
				
				fillSchDates(this.cbFromDate, aFinSchData, null);
				fillSchDates(this.cbTillDate, aFinSchData, null);
				this.fromDateRow.setVisible(true);
				this.tillDateRow.setVisible(true);
				this.label_AddDisbursementDialog_TillDate.setValue(Labels.getLabel("label_AddDisbursementDialog_TillDate.value"));
				
			} else if (StringUtils.equals(aFinSchData.getFinanceMain().getRecalType(), CalculationConstants.RPYCHG_TILLMDT) ||
					StringUtils.equals(aFinSchData.getFinanceMain().getRecalType(), CalculationConstants.RPYCHG_ADDRECAL)) {
				
				fillSchDates(cbFromDate, getFinScheduleData(), null);
				this.label_AddDisbursementDialog_TillFromDate.setValue(Labels.getLabel("label_AddDisbursementDialog_CalFromDate.value"));
				this.fromDateRow.setVisible(true);
				this.cbFromDate.setSelectedIndex(0);
			}else{
				fillSchDates(this.cbFromDate, aFinSchData, null);
			}
			this.disbAcctId.setValue(PennantApplicationUtil.formatAccountNumber(aFinSchData.getFinanceMain().getDisbAccountId()));
			changeRecalType();
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterruptedException
	 * @throws WrongValueException
	 */
	private void doWriteComponentsToBean(FinScheduleData aFinScheduleData) throws WrongValueException,
			InterruptedException, IllegalAccessException, InvocationTargetException {
		
		logger.debug("Entering");
		
		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		FinanceMain finMain = aFinScheduleData.getFinanceMain();
		int formatter = CurrencyUtil.getFormat(finMain.getFinCcy());
		boolean isOverdraft = false;
		boolean isDevFinance = false;
		
		if(StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,finMain.getProductCategory())){
			isOverdraft = true;
		}
		if(aFinScheduleData.getFinanceType().isDeveloperFinance()){
			isDevFinance = true;
		}
		
		boolean isValidDate = true;
		Date maturityDate = finMain.getMaturityDate();
		Date recalFrom = finMain.getFinStartDate();
		try {

			// Closing Balance Maturity Date
			int sdSize = aFinScheduleData.getFinanceScheduleDetails().size();
			if(!isOverdraft && !isDevFinance){
				for (int i = sdSize -1; i > 0; i--) {
					FinanceScheduleDetail curSchd = aFinScheduleData.getFinanceScheduleDetails().get(i);
					if(curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) != 0){
						break;
					}
					maturityDate = curSchd.getSchDate();
				}
			}
			if(isDevFinance){
				for (int i = 0; i <= sdSize -1; i++) {
					FinanceScheduleDetail curSchd = aFinScheduleData.getFinanceScheduleDetails().get(i);
					if(DateUtility.compare(curSchd.getSchDate(), this.fromDate.getValue()) <= 0){
						continue;
					}
					recalFrom = curSchd.getSchDate();
					break;
				}
			}
			

			Date appDate = DateUtility.getAppDate();
			if (DateUtility.compare(this.fromDate.getValue(), appDate) < 0 || DateUtility.compare(this.fromDate.getValue(),maturityDate) >= 0) {
				isValidDate = false;
				throw new WrongValueException(this.fromDate, Labels.getLabel("DATE_ALLOWED_MINDATE_EQUAL",
						new String[] { Labels.getLabel("label_AddDisbursementDialog_FromDate.value"),
								DateUtility.formatToLongDate(appDate), DateUtility.formatToLongDate(maturityDate) }));
			}
			if ((DateUtility.compare(this.fromDate.getValue(), lastPaidDate) <= 0 || DateUtility.compare(this.fromDate.getValue(),maturityDate) >= 0) &&
					DateUtility.compare(this.fromDate.getValue(), finMain.getFinStartDate()) != 0) {
				isValidDate = false;
				throw new WrongValueException(this.fromDate, Labels.getLabel("DATE_ALLOWED_RANGE",
						new String[] { Labels.getLabel("label_AddDisbursementDialog_FromDate.value"),
								DateUtility.formatToLongDate(lastPaidDate), DateUtility.formatToLongDate(maturityDate) }));
			}
			finMain.setEventFromDate(this.fromDate.getValue());
			finServiceInstruction.setFromDate(this.fromDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			this.disbAmount.getValidateValue();
			finServiceInstruction.setAmount(PennantAppUtil.unFormateAmount(this.disbAmount.getValidateValue(),formatter));
			
			if(isOverdraft && isValidDate){
				
				// Checking against adding disbursement date available limit
				List<OverdraftScheduleDetail> odSchdDetail = aFinScheduleData.getOverdraftScheduleDetails();
				BigDecimal fromDateAvailLimit = BigDecimal.ZERO;
				if(odSchdDetail!= null && odSchdDetail.size() > 0){
					for(int i = 0;i < odSchdDetail.size(); i++){
						if(odSchdDetail.get(i).getDroplineDate().compareTo(this.fromDate.getValue()) > 0){
							break;
						}
						fromDateAvailLimit = odSchdDetail.get(i).getODLimit();
					}
					
					// Schedule Outstanding amount calculation
					List<FinanceScheduleDetail> schList = aFinScheduleData.getFinanceScheduleDetails();
					BigDecimal closingbal = BigDecimal.ZERO;
					for (int i = 0; i < schList.size(); i++) {
						if(DateUtility.compare(schList.get(i).getSchDate(),this.fromDate.getValue()) > 0){
							break;
						}
						closingbal = schList.get(i).getClosingBalance();
					}
					
					// Actual Available Limit
					fromDateAvailLimit = fromDateAvailLimit.subtract(closingbal);
				}

				// Validating against Available Limit amount
				if(this.disbAmount.getValidateValue().compareTo(PennantAppUtil.formateAmount(fromDateAvailLimit,formatter)) > 0){
					if(fromDateAvailLimit.compareTo(BigDecimal.ZERO) > 0){
						throw new WrongValueException(this.disbAmount.getCcyTextBox(), Labels.getLabel("od_DisbAmount_Validation_RANGE",
								new String[] {PennantApplicationUtil.amountFormate(fromDateAvailLimit,formatter)}));
					}else{
						throw new WrongValueException(this.disbAmount.getCcyTextBox(),
								Labels.getLabel("od_DisbAmount_Validation", new String[]{}));
					}
				}else{
					
					// Checking Total Disbursed amount validate against New disbursement
					BigDecimal prvTotDisbValue = BigDecimal.ZERO;
					for(FinanceDisbursement curDisb:aFinScheduleData.getDisbursementDetails()){
						if(StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus())){
							continue;
						}
						prvTotDisbValue = prvTotDisbValue.add(curDisb.getDisbAmount());
					}
					BigDecimal curTotDisbValue = PennantAppUtil.unFormateAmount(this.disbAmount.getValidateValue(),formatter).add(prvTotDisbValue);
					if(curTotDisbValue.compareTo(finMain.getFinAssetValue()) > 0 && finMain.getFinAssetValue().subtract(prvTotDisbValue).compareTo(BigDecimal.ZERO)>0){
						throw new WrongValueException(this.disbAmount.getCcyTextBox(),Labels.getLabel("od_DisbAmount",
								new String[]{PennantApplicationUtil.amountFormate(finMain.getFinAssetValue(),formatter),
										PennantApplicationUtil.amountFormate(finMain.getFinAssetValue().subtract(prvTotDisbValue),formatter)}));
					}else if(curTotDisbValue.compareTo(finMain.getFinAssetValue()) > 0 && finMain.getFinAssetValue().subtract(prvTotDisbValue).compareTo(BigDecimal.ZERO)<=0){
						throw new WrongValueException(this.disbAmount.getCcyTextBox(),Labels.getLabel("od_DisAmountExceeded",new String[]{}));
					}
				}

				// Checking total Disbursement amounts against available limit to add in Current Disbursement
				List<FinanceScheduleDetail> finSched = aFinScheduleData.getFinanceScheduleDetails();
				BigDecimal avalLimit = finMain.getFinAssetValue();
				for (int i = 1; i < finSched.size() - 1; i++) {

					if(!finSched.get(i).isDisbOnSchDate()){
						continue;
					}else{
						
						List<OverdraftScheduleDetail> odDetail = aFinScheduleData.getOverdraftScheduleDetails();
						for (int j = 0; j < odDetail.size() - 1; j++) {

								if(DateUtility.compare(odDetail.get(j).getDroplineDate(),finSched.get(i).getSchDate()) <= 0
										&& DateUtility.compare(odDetail.get(j+1).getDroplineDate(),finSched.get(i).getSchDate()) > 0){
									avalLimit = odDetail.get(j).getODLimit().subtract(finSched.get(i).getClosingBalance());
									break;
								}
							}
					}
					
					// Available Limit Checking against whole period
					if(avalLimit.compareTo(BigDecimal.ZERO) == 0){
						throw new WrongValueException(this.disbAmount.getCcyTextBox(), Labels.getLabel("od_DisbAmount_Validation", new String[]{}));
					}else if(avalLimit.compareTo(BigDecimal.ZERO) > 0 && 
							(avalLimit.subtract(PennantAppUtil.unFormateAmount(this.disbAmount.getValidateValue(),formatter))).compareTo(BigDecimal.ZERO) < 0){
						throw new WrongValueException(this.disbAmount.getCcyTextBox(), Labels.getLabel("od_DisbAmount_Validation_Maxvalue", 
								new String[]{PennantApplicationUtil.amountFormate(avalLimit,formatter)}));
					}
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isValidComboValue(this.cbSchdMthd, Labels.getLabel("label_AddDisbursementDialog_SchdMthd.value")) && this.cbSchdMthd.getSelectedIndex() != 0) {
				finServiceInstruction.setSchdMethod(getComboboxValue(this.cbSchdMthd));
				//finMain.setRecalSchdMethod(getComboboxValue(this.cbSchdMthd));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.reCalTypeRow.isVisible()){
				if (isValidComboValue(this.cbReCalType, Labels.getLabel("label_AddDisbursementDialog_RecalType.value"))
						&& this.cbReCalType.getSelectedIndex() != 0) {
					finMain.setRecalType(this.cbReCalType.getSelectedItem().getValue().toString());
					finServiceInstruction.setRecalType(getComboboxValue(this.cbReCalType));
				}
			}
			
		}catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.fromDateRow.isVisible()) {
			try {
				if (this.cbFromDate.getSelectedIndex() <= 0) {
					throw new WrongValueException(this.cbFromDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_AddDisbursementDialog_CalFromDate.value") }));
				}
				if (this.fromDate.getValue() != null
						&& ((Date) this.cbFromDate.getSelectedItem().getValue()).compareTo(this.fromDate.getValue()) <= 0) {
					
					throw new WrongValueException(this.cbFromDate, Labels.getLabel("DATE_ALLOWED_AFTER",
							new String[] { Labels.getLabel("label_AddDisbursementDialog_FromDate.value"),
									DateUtility.formatToLongDate(this.fromDate.getValue()) }));
				}
				finServiceInstruction.setRecalFromDate((Date) this.cbFromDate.getSelectedItem().getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (this.tillDateRow.isVisible()) {
			try {
				if (this.cbTillDate.getSelectedIndex() == 0) {
					throw new WrongValueException(this.cbTillDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_AddDisbursementDialog_TillDate.value") }));
				}

				if (this.cbFromDate.getSelectedIndex() > 0
						&& ((Date) this.cbTillDate.getSelectedItem().getValue()).compareTo((Date) this.cbFromDate.getSelectedItem().getValue()) < 0) {
					
					throw new WrongValueException(this.cbTillDate,Labels.getLabel("DATE_ALLOWED_AFTER"
							,new String[] {Labels.getLabel("label_AddDisbursementDialog_TillDate.value"),
							DateUtility.formatToLongDate((Date) this.cbFromDate.getSelectedItem().getValue()) }));
				}
				
				if ((this.fromDate.getValue() != null && 
						((Date) this.cbTillDate.getSelectedItem().getValue()).compareTo(this.fromDate.getValue()) < 0)
						|| (((Date) this.cbTillDate.getSelectedItem().getValue()).compareTo(this.fromDate.getValue()) == 0)) {
					
					throw new WrongValueException(this.cbTillDate, Labels.getLabel("DATE_ALLOWED_AFTER",
							new String[] { Labels.getLabel("label_AddDisbursementDialog_TillDate.value"),
							DateUtility.formatToLongDate((Date) this.fromDate.getValue()) }));
				}
				
				//throw Exception if the selected schedule in To Date is having profit balance     
				if(this.cbTillDate.getSelectedItem().getAttribute("pftBal") != null){
					throw new WrongValueException(this.cbTillDate, Labels.getLabel("Label_finSchdTillDate"));
				}
				finServiceInstruction.setRecalToDate((Date) this.cbTillDate.getSelectedItem().getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		finMain.setAdjTerms(0);
		if (this.numOfTermsRow.isVisible()) {
			try {
				if (this.adjTerms.intValue() <= 0) {
					throw new WrongValueException(this.adjTerms, Labels.getLabel("MUST_BE_ENTERED",
							new String[] { Labels.getLabel("label_ChangeRepaymentDialog_Terms.value") }));
				}
				finMain.setAdjTerms(this.adjTerms.intValue());
				finServiceInstruction.setTerms(this.adjTerms.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		
		try{
			//CHECK IF MULTI DISBURSE ADDED IN SAME DATE
			/*if(StringUtils.trimToEmpty(this.disbAcctId.getValue()).equals("")){
				getFinScheduleData().getFinanceMain().setDisbAccountId(PennantApplicationUtil.unFormatAccountNumber(this.disbAcctId.getValue())); 
			} */
		}catch(WrongValueException we) { 
			wve.add(we); 
		}


		try {
			if (this.alwAssetUtilize.isChecked()) {
				if (grcEndDisbAmount.compareTo(PennantAppUtil.unFormateAmount(this.disbAmount.getActualValue(), formatter)) < 0) {
					throw new WrongValueException(this.disbAmount, Labels.getLabel("NUMBER_MAXVALUE_EQ",
							new String[] {Labels.getLabel("label_AddDisbursementDialog_Amount.value"),
							PennantAppUtil.amountFormate(grcEndDisbAmount, CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy())) }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.size() > 0) {
			doRemoveValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		if (this.alwAssetUtilize.isChecked()) {
			List<FinanceDisbursement> list = aFinScheduleData.getDisbursementDetails();
			for (int i = 0; i < list.size(); i++) {
				FinanceDisbursement disbursement = list.get(i);
				if (this.fromDate.getValue().compareTo(finMain.getGrcPeriodEndDate()) <= 0) {
					if (grcEndDisbAmount.compareTo(finServiceInstruction.getAmount()) == 0) {
						list.remove(i);
						break;
					}
				}
				if (disbursement.getDisbDate().compareTo(finMain.getGrcPeriodEndDate()) == 0
						&& disbursement.getDisbDate().compareTo(finMain.getFinStartDate()) != 0) {
					if (grcEndDisbAmount.compareTo(finServiceInstruction.getAmount()) == 0) {
						list.remove(i);
					} else {
						disbursement.setDisbAmount(disbursement.getDisbAmount().subtract(finServiceInstruction.getAmount()));
					}
					break;
				}
			}
			aFinScheduleData.setDisbursementDetails(list);
		}

		aFinScheduleData.getFinanceMain().setEventToDate(maturityDate);
		finServiceInstruction.setToDate(maturityDate);
		
		finMain.setCurDisbursementAmt(finServiceInstruction.getAmount());
		BigDecimal addingFeeToFinance = BigDecimal.ZERO;
		
		if (getFinFeeDetailListCtrl() != null) {
			getFinFeeDetailListCtrl().doExecuteFeeCharges(true, getFinScheduleData());
		}
		
		if(isOverdraft || isDevFinance){
			finMain.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			finMain.setEventFromDate(this.fromDate.getValue());
			finMain.setRecalFromDate(this.fromDate.getValue());
			finServiceInstruction.setRecalFromDate(this.fromDate.getValue());
			if(isDevFinance){
				finMain.setRecalFromDate(recalFrom);
				finServiceInstruction.setRecalFromDate(recalFrom);
			}
			finMain.setRecalToDate(maturityDate);
			finServiceInstruction.setRecalType(CalculationConstants.RPYCHG_TILLMDT);
			finServiceInstruction.setFromDate(this.fromDate.getValue());
			finServiceInstruction.setRecalToDate(maturityDate);
		}
		
		finServiceInstruction.setFinReference(finMain.getFinReference());
		finServiceInstruction.setFinEvent(FinanceConstants.FINSER_EVENT_ADDDISB);
		finServiceInstruction.setServiceReqNo(this.serviceReqNo.getValue());
		finServiceInstruction.setRemarks(this.remarks.getValue());
		aFinScheduleData.setFinServiceInstruction(finServiceInstruction);
		
		if(this.reCalTypeRow.isVisible()){
			if (this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_TILLMDT) ||
					this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_ADDRECAL)) {
				Date fromDate = (Date) this.cbFromDate.getSelectedItem().getValue();
				finMain.setRecalFromDate(fromDate);
				finMain.setRecalToDate(maturityDate);
				finServiceInstruction.setRecalFromDate(fromDate);
				finServiceInstruction.setRecalToDate(maturityDate);
			} else if (this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_TILLDATE)) {
				finMain.setRecalFromDate((Date) this.cbFromDate.getSelectedItem().getValue());
				finMain.setRecalToDate((Date) this.cbTillDate.getSelectedItem().getValue());
				finServiceInstruction.setRecalFromDate((Date) this.cbFromDate.getSelectedItem().getValue());
				finServiceInstruction.setRecalToDate((Date) this.cbTillDate.getSelectedItem().getValue());

			}
		}
		// Service details calling for Schedule calculation
		aFinScheduleData = addDisbursementService.getAddDisbDetails(aFinScheduleData,
				finServiceInstruction.getAmount(), addingFeeToFinance, this.alwAssetUtilize.isChecked(), moduleDefiner);		
		aFinScheduleData.getFinanceMain().resetRecalculationFields();	
		
		// Show Error Details in Schedule Maintenance
		if (aFinScheduleData.getErrorDetails() != null && !aFinScheduleData.getErrorDetails().isEmpty()) {
			MessageUtil.showError(getFinScheduleData().getErrorDetails().get(0));
			aFinScheduleData.getErrorDetails().clear();
		} else {
			aFinScheduleData.setSchduleGenerated(true);
			if (getScheduleDetailDialogCtrl() != null) {
				getScheduleDetailDialogCtrl().doFillScheduleList(aFinScheduleData);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (this.disbAmount.isVisible()) {
			this.disbAmount.setConstraint(new PTDecimalValidator(Labels.getLabel("label_AddDisbursementDialog_Amount.value"), 0, true, false));
		}
		if (this.disbursementAccount.isVisible()) {
			this.disbAcctId.setConstraint(new PTStringValidator(Labels.getLabel("label_AddDisbursementDialog_DisbAcctId.value"), null, true));
		}
		logger.debug("Leaving");
	}
	
	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws WrongValueException
	 */
	public void onClick$btnAddDisbursement(Event event)
			throws InterruptedException, WrongValueException,
			IllegalAccessException, InvocationTargetException {
		logger.debug("Entering" + event.toString());
		if (getFinanceScheduleDetail() != null) {
			if (isDataChanged()) {
				doSave();
			} else {
				MessageUtil.showError("No Data has been changed.");
			}
		} else {
			doSave();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	/**
	 * The Click event is raised when the Close event is occurred.
	 * 
	 * @param event
	 * 
	 * */
	public void onClose(Event event) {
		doClose(false);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws WrongValueException
	 */
	private void doSave() throws InterruptedException, WrongValueException,
			IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		doSetValidation();
		doWriteComponentsToBean(getFinScheduleData());
		this.window_AddDisbursementDialog.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Method to clear error message
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		setValidationOn(false);
		this.disbAmount.setErrorMessage("");
		this.fromDate.setErrorMessage("");
		this.disbAcctId.setErrorMessage("");
		this.cbReCalType.setErrorMessage("");
		this.adjTerms.setErrorMessage("");
		this.serviceReqNo.setErrorMessage("");
		this.remarks.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method to clear error message
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.disbAmount.setConstraint("");
		this.fromDate.setConstraint("");
		this.disbAcctId.setConstraint("");
		this.cbReCalType.setConstraint("");
		this.adjTerms.setConstraint("");
		logger.debug("Leaving");
	}

	// Enable till date field if the selected recalculation type is TIIDATE
	public void onChange$cbReCalType(Event event) {
		logger.debug("Entering" + event.toString());
		changeRecalType();		
		logger.debug("Leaving" + event.toString());
	}
	
	private void changeRecalType(){
		this.numOfTermsRow.setVisible(false);
		this.tillDateRow.setVisible(false);
		this.fromDateRow.setVisible(false);

		if (this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_TILLDATE)) {
			fillSchDates(cbFromDate, getFinScheduleData(), null);
			fillSchDates(cbTillDate, getFinScheduleData(), null);
			this.cbTillDate.setSelectedIndex(0);
			this.cbFromDate.setSelectedIndex(0);

			this.fromDateRow.setVisible(true);
			this.tillDateRow.setVisible(true);
			this.label_AddDisbursementDialog_TillFromDate.setValue(Labels.getLabel("label_AddDisbursementDialog_CalFromDate.value"));
			this.label_AddDisbursementDialog_TillDate.setValue(Labels.getLabel("label_AddDisbursementDialog_TillDate.value"));
			
		} else if (this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_ADDTERM)
				|| this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_ADDRECAL)) {
			
			this.numOfTermsRow.setVisible(true);
			
			if(this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_ADDRECAL)){
				fillSchDates(cbFromDate, getFinScheduleData(), null);
				this.label_AddDisbursementDialog_TillFromDate.setValue(Labels.getLabel("label_AddDisbursementDialog_CalFromDate.value"));
				this.fromDateRow.setVisible(true);
				this.cbFromDate.setSelectedIndex(0);
			}
			
		} else if (this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_ADJMDT) ||
				StringUtils.equals(this.cbReCalType.getSelectedItem().getValue().toString(),CalculationConstants.RPYCHG_STEPPOS)) {
			// Nothing TO DO
		} else if (this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_TILLMDT)) {
			fillSchDates(cbFromDate, getFinScheduleData(), null);
			this.label_AddDisbursementDialog_TillFromDate.setValue(Labels.getLabel("label_AddDisbursementDialog_CalFromDate.value"));
			this.fromDateRow.setVisible(true);
			// If DEFAULT_TILLMDT is Specified then Set From Date as Readonly based on the Equitas Requirement .
			int value = 1;
			boolean disableDate =false; 
			try {
				value = SysParamUtil.getValueAsInt("DEFAULT_TILLMDT");
				disableDate =true;
			} catch (Exception e) {
				
			}
			readOnlyComponent(disableDate,cbFromDate);
			if(this.cbFromDate.getItemCount()>value+1){
				this.cbFromDate.setSelectedIndex(value);	
			}else{
				readOnlyComponent(false,cbFromDate);
				this.cbFromDate.setSelectedIndex(0);
			}
		
		} 	
	}
	

	/**
	 * Method to allow to utilize asset value difference from grace end date
	 * 
	 * @param event
	 */
	public void onChange$fromDate(ForwardEvent event) {
		logger.debug("Entering" + event.toString());

		if (this.fromDate.getValue() != null && DateUtility.compare(this.fromDate.getValue(), 
				getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()) < 0) {
			
			this.cbSchdMthd.setDisabled(true);
			fillComboBox(this.cbSchdMthd,getFinScheduleData().getFinanceMain().getGrcSchdMthd(), PennantStaticListUtil.getScheduleMethods(), ",EQUAL,PRI_PFT,PRI,");
			
			if ((StringUtils.equalsIgnoreCase(getFinScheduleData().getFinanceType().getFinCategory(),FinanceConstants.PRODUCT_IJARAH) || 
					StringUtils.equalsIgnoreCase(getFinScheduleData().getFinanceType().getFinCategory(), FinanceConstants.PRODUCT_FWIJARAH))
					&& getFinScheduleData().getFinanceType().isFinIsAlwMD() && getFinScheduleData().getFinanceMain().isAllowGrcPeriod()) {

				if (this.fromDate.getValue() != null) {
					if (getFinScheduleData().getFinanceType().isFinIsAlwMD()) {

						List<FinanceDisbursement> list = getFinScheduleData().getDisbursementDetails();
						BigDecimal totDisbAmt = BigDecimal.ZERO;
						for (FinanceDisbursement disbursement : list) {
							totDisbAmt = totDisbAmt.add(disbursement.getDisbAmount());
						}

						grcEndDisbAmount = getFinScheduleData().getFinanceMain().getFinAssetValue().subtract(totDisbAmt);

						if (grcEndDisbAmount.compareTo(BigDecimal.ZERO) > 0) {
							this.row_assetUtilization.setVisible(false);
						} else {
							this.row_assetUtilization.setVisible(false);
							this.alwAssetUtilize.setChecked(false);
						}
					} else {
						this.row_assetUtilization.setVisible(false);
						this.alwAssetUtilize.setChecked(false);
					}
				} else {
					this.row_assetUtilization.setVisible(false);
					this.alwAssetUtilize.setChecked(false);
				}

			//	fillSchDates(cbFromDate, getFinScheduleData());
				//fillSchDates(cbTillDate, getFinScheduleData());
			}
		}else{
			fillComboBox(this.cbSchdMthd, getFinScheduleData().getFinanceMain().getScheduleMethod(), PennantStaticListUtil.getScheduleMethods(), ",GRCNDPAY,PFTCAP,");
			this.cbSchdMthd.setDisabled(true);
		}
		
		// STEP POS Recalculation Type Addition Check
		if (this.fromDate.getValue() != null && DateUtility.compare(this.fromDate.getValue(), 
				getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()) <= 0) {
			
			String exclRecalTypes = ",CURPRD,ADJTERMS,ADDLAST,STEPPOS,";
			boolean isStepPOS = false;
			if(getFinScheduleData().getFinanceMain().isStepFinance() &&  getFinScheduleData().getFinanceMain().isAllowGrcPeriod() && 
					StringUtils.equals(getFinScheduleData().getFinanceMain().getStepType(), FinanceConstants.STEPTYPE_PRIBAL) &&
					(StringUtils.equals(getFinScheduleData().getFinanceMain().getScheduleMethod(), CalculationConstants.SCHMTHD_PRI) ||
					StringUtils.equals(getFinScheduleData().getFinanceMain().getScheduleMethod(), CalculationConstants.SCHMTHD_PRI_PFT))){
				exclRecalTypes = ",CURPRD,ADJTERMS,ADDLAST,";
				isStepPOS = true;
			}
			
			if(isStepPOS){
				fillComboBox(this.cbReCalType, CalculationConstants.RPYCHG_STEPPOS, PennantStaticListUtil.getDisbCalCodes(),exclRecalTypes);
			}else{
				fillComboBox(this.cbReCalType, getFinScheduleData().getFinanceMain().getRecalType(), PennantStaticListUtil.getDisbCalCodes(),exclRecalTypes);
			}

		}else{
			String exclRecalTypes = ",CURPRD,ADJTERMS,ADDLAST,STEPPOS,";
			String value=getFinScheduleData().getFinanceMain().getRecalType();
			if(StringUtils.trimToNull(value)==null && PennantStaticListUtil.getDisbCalCodes().size()==1){
				value = PennantStaticListUtil.getDisbCalCodes().get(0).getValue();
			}
			
			fillComboBox(this.cbReCalType, value, PennantStaticListUtil.getDisbCalCodes(),exclRecalTypes);
		}
		
		fillSchDates(cbFromDate, getFinScheduleData(), null);
		fillSchDates(cbTillDate, getFinScheduleData(), null);	
		
		changeRecalType();
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$cbFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		if(this.cbFromDate.getSelectedIndex() > 0){
			fillSchDates(cbTillDate, getFinScheduleData(), (Date)this.cbFromDate.getSelectedItem().getValue());	
		}
		logger.debug("Leaving" + event.toString());
	}


	/** To fill schedule dates */
	private void fillSchDates(Combobox dateCombobox,
			FinScheduleData scheduleData, Date fillAfter) {
		logger.debug("Entering");

		dateCombobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		if (scheduleData.getFinanceScheduleDetails() != null) {
			boolean checkForLastPaid = true;
			
			List<FinanceScheduleDetail> financeScheduleDetails = scheduleData
					.getFinanceScheduleDetails();
			
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				
				// Check For Last Paid Date
				if (checkForLastPaid) {
					lastPaidDate = curSchd.getSchDate();
				}

				// Profit Paid (Partial/Full) or Principal Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0 || curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					dateCombobox.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					dateCombobox.appendChild(comboitem);
					dateCombobox.setSelectedItem(comboitem);
					continue;
				}

				// Excluding Present generated file Schedule Terms
				if(curSchd.getPresentmentId() > 0){
					dateCombobox.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					dateCombobox.appendChild(comboitem);
					dateCombobox.setSelectedItem(comboitem);
					continue;
				}
				
				checkForLastPaid = false;

				// New Disbursement Date Checking
				if (this.fromDate.getValue() != null
						&& curSchd.getSchDate().compareTo(this.fromDate.getValue()) <= 0) {
					continue;
				}
				Date appDate = DateUtility.getAppDate();
				int allowedDays = SysParamUtil.getValueAsInt("FutureNotAllowedDays_Disb");

				if (allowedDays > 0) {
					Date minValidDate = DateUtility.addDays(appDate, allowedDays);
					if (DateUtility.compare(curSchd.getSchDate(), minValidDate) < 0) {
						continue;
					}
				}				// If maturity Terms, not include in list
				if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}
				
				// Till Date to Date Setting
				if(fillAfter != null && curSchd.getSchDate().compareTo(fillAfter) < 0){
					continue;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtility.formatToLongDate(curSchd.getSchDate()));
				comboitem.setValue(curSchd.getSchDate());
				dateCombobox.appendChild(comboitem);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * when clicks on button "btnSearchDisbAcctId"
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws AccountNotFoundException
	 */
	public void onClick$btnSearchDisbAcctId(Event event)
			throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.disbAcctId.clearErrorMessage();

		if (StringUtils.isNotBlank(getFinScheduleData().getFinanceMain().getLovDescCustCIF())) {
			Object dataObject;

			List<IAccounts> iAccountList = new ArrayList<IAccounts>();
			IAccounts iAccount = new IAccounts();
			iAccount.setAcCcy(getFinScheduleData().getFinanceMain().getFinCcy());
			iAccount.setAcType("");
			iAccount.setDivision(getFinScheduleData().getFinanceType().getFinDivision());
			iAccount.setAcCustCIF(getFinScheduleData().getFinanceMain().getLovDescCustCIF());

			try {
				iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);

				dataObject = ExtendedSearchListBox.show(this.window_AddDisbursementDialog, "Accounts",iAccountList);
				if (dataObject instanceof String) {
					this.disbAcctId.setValue(dataObject.toString());
				} else {
					IAccounts details = (IAccounts) dataObject;

					if (details != null) {
						this.disbAcctId.setValue(PennantApplicationUtil.formatAccountNumber(details.getAccountId()));
					}
				}
			} catch (Exception e) {
				logger.error("Exception: ", e);
				MessageUtil.showError("Account Details not Found!!!");
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public FinanceScheduleDetail getFinanceScheduleDetail() {
		return financeScheduleDetail;
	}

	public void setFinanceScheduleDetail(
			FinanceScheduleDetail financeScheduleDetail) {
		this.financeScheduleDetail = financeScheduleDetail;
	}

	public ScheduleDetailDialogCtrl getScheduleDetailDialogCtrl() {
		return scheduleDetailDialogCtrl;
	}

	public void setScheduleDetailDialogCtrl(
			ScheduleDetailDialogCtrl scheduleDetailDialogCtrl) {
		this.scheduleDetailDialogCtrl = scheduleDetailDialogCtrl;
	}

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}

	public void setAccountInterfaceService(
			AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}

	public AccountsService getAccountsService() {
		return accountsService;
	}

	public void setAccountsService(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	public void setAddDisbursementService(AddDisbursementService addDisbursementService) {
		this.addDisbursementService = addDisbursementService;
	}

	public FinFeeDetailListCtrl getFinFeeDetailListCtrl() {
		return finFeeDetailListCtrl;
	}

	public void setFinFeeDetailListCtrl(FinFeeDetailListCtrl finFeeDetailListCtrl) {
		this.finFeeDetailListCtrl = finFeeDetailListCtrl;
	}
	 
}
