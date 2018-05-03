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
 * FileName    		:  DisbursementDetailDialogCtrl.java                                                   * 	  
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/DisbursementDetailDialog.zul file.
 */
public class DisbursementDetailDialogCtrl extends GFCBaseCtrl<FinanceDisbursement> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(DisbursementDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_DisbursementDetailDialog; 			// autoWired
	protected Borderlayout  borderlayoutDisbursementDetail;				// autoWired

	//Finance Document Details Tab
	protected Label 		disb_finType; 								// autoWired
	protected Label 		disb_schMethod; 							// autoWired
	protected Label 		disb_finCcy; 								// autoWired
	protected Label 		disb_profitDaysBasis; 						// autoWired
	protected Label 		disb_noOfTerms; 							// autoWired
	protected Label 		disb_grcEndDate; 							// autoWired	
	protected Label 		disb_startDate;								// autoWired	
	protected Label 		disb_maturityDate;							// autoWired	
	protected Label 		label_DisbursementDetailDialog_GrcEndDate;	// autoWired	
	protected Label 		label_DisbursementDetailDialog_FinType;		// autoWired	
	protected Decimalbox 	disb_expenses;								// autoWired	
	protected Decimalbox 	disb_totalBilling;							// autoWired	
	protected Decimalbox 	disb_consultFee;							// autoWired	
	protected Decimalbox 	disb_totalCost;								// autoWired	

	protected Button 		btnAddExpense;								// autoWired
	protected Button 		btnAddContractAdv;							// autoWired
	protected Button 		btnAddBilling;								// autoWired
	protected Button 		btnAddConsultingFee;						// autoWired
	protected Button 		btnAddContributor;						// autoWired
	protected Listbox 		listBoxDisbursementDetail;					// autoWired
	protected Listbox 		listBoxContributorDetails;					// autoWired

	private List<FinanceDisbursement> disbursementDetails = new ArrayList<FinanceDisbursement>();
	private List<FinanceDisbursement> oldvar_disbursementDetails = new ArrayList<FinanceDisbursement>();

	private IstisnaFinanceMainDialogCtrl financeMainDialogCtrl = null;
	private FinScheduleData finScheduleData = null;
	private FinanceDetail financeDetail = null;
	
	private FinBasicDetailsCtrl  finBasicDetailsCtrl;
	protected Groupbox finBasicdetails;

	private String currency = "";
	private int ccyFormat = 0;
	private Date startDate = null;
	private Date grcEndDate = null;

	private Map<Long, BigDecimal> netAdvDueDetailMap = null;
	private Map<Long, BigDecimal> netRetDueDetailMap = null;

	private List<ContractorAssetDetail> contractorAssetDetails = new ArrayList<ContractorAssetDetail>();
	private List<ContractorAssetDetail> oldvar_contractorAssetDetails = new ArrayList<ContractorAssetDetail>();

	/**
	 * default constructor.<br>
	 */
	public DisbursementDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceMainDialog";
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
	public void onCreate$window_DisbursementDetailDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_DisbursementDetailDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
			setFinanceDetail(financeDetail);
			setFinScheduleData(financeDetail.getFinScheduleData());
			setDisbursementDetails(financeDetail.getFinScheduleData().getDisbursementDetails());
			setOldvar_disbursementDetails(financeDetail.getFinScheduleData().getDisbursementDetails());

			setContractorAssetDetails(financeDetail.getContractorAssetDetails());
			setOldvar_contractorAssetDetails(financeDetail.getContractorAssetDetails());
		}

		if (arguments.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (IstisnaFinanceMainDialogCtrl) arguments.get("financeMainDialogCtrl");
		}

		if (arguments.containsKey("roleCode")) {
			setRole((String) arguments.get("roleCode"));
		}
		
		if (arguments.containsKey("ccyFormatter")) {
			ccyFormat = (Integer) arguments.get("ccyFormatter");
		}

		doCheckRights();
		doShowDialog();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	@SuppressWarnings("rawtypes")
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");

		try {

			// append finance basic details 
			appendFinBasicDetails();

			// fill the components with the data
			if (getDisbursementDetails() != null && getDisbursementDetails().size() > 0) {
				doFillDisbursementDetails(getDisbursementDetails());
			}

			if (getContractorAssetDetails() != null && getContractorAssetDetails().size() > 0) {
				doFillContractorDetails(getContractorAssetDetails());
			}
			
			if (getFinanceMainDialogCtrl() != null) {
				try {
					Class[] paramType = { Class.forName("com.pennant.webui.finance.financemain.DisbursementDetailDialogCtrl") };
					Object[] stringParameter = {this};
					if (financeMainDialogCtrl.getClass().getMethod("setDisbursementDetailDialogCtrl", paramType) != null) {
						financeMainDialogCtrl.getClass().getMethod("setDisbursementDetailDialogCtrl", paramType).invoke(financeMainDialogCtrl, stringParameter);
					}

				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}

			getBorderLayoutHeight();
			this.window_DisbursementDetailDialog.setHeight(this.borderLayoutHeight-80+"px");

		} catch (Exception e) {
			MessageUtil.showError(e);
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

		getUserWorkspace().allocateAuthorities("FinanceMainDialog",getRole());

		this.btnAddExpense.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnAddExpense"));
		this.btnAddContractAdv.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnAddContractAdv"));
		this.btnAddBilling.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnAddBilling"));
		this.btnAddConsultingFee.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnAddConsultingF"));
		this.btnAddContributor.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnAddContributor"));

		logger.debug("Leaving");
	}

	// New Button & Double Click Events for Finance Disbursement List

	// Finance Disbursement Details for Expense
	public void onClick$btnAddExpense(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		FinanceDisbursement disbursement = new FinanceDisbursement();
		disbursement.setDisbType("E");

		createNewDisbursement(disbursement,  new ContractorAssetDetail());
		logger.debug("Leaving" + event.toString());
	}

	// Finance Disbursement Details Contractor Advance
	public void onClick$btnAddContractAdv(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		if(this.listBoxContributorDetails.getSelectedItem() == null) {
			MessageUtil.showError("Please select Contractor");
		} else {
			FinanceDisbursement disbursement = new FinanceDisbursement();
			disbursement.setDisbType("A");

			createNewDisbursement(disbursement,(ContractorAssetDetail) this.listBoxContributorDetails.getSelectedItem().getAttribute("data"));
		}
		logger.debug("Leaving" + event.toString());
	}

	// Finance Disbursement Details Consultancy Fee
	public void onClick$btnAddConsultingFee(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		FinanceDisbursement disbursement = new FinanceDisbursement();
		disbursement.setDisbType("C");
		if(this.listBoxContributorDetails.getSelectedItem() == null) {
			MessageUtil.showError("Please select Contractor");
		} else {     
			createNewDisbursement(disbursement, (ContractorAssetDetail) this.listBoxContributorDetails.getSelectedItem().getAttribute("data"));
		}
		logger.debug("Leaving" + event.toString());
	}

	// Finance Disbursement Details for Billing
	public void onClick$btnAddBilling(Event event) throws Exception{
		logger.debug("Entering" + event.toString());
		if(this.listBoxContributorDetails.getSelectedItem() == null) {
			MessageUtil.showError("Please select Contractor");
		} else {
			FinanceDisbursement disbursement = new FinanceDisbursement();
			disbursement.setDisbType("B");

			createNewDisbursement(disbursement,(ContractorAssetDetail) this.listBoxContributorDetails.getSelectedItem().getAttribute("data"));
		}
		logger.debug("Leaving" + event.toString());
	}


	@SuppressWarnings("unchecked")
	public void createNewDisbursement(FinanceDisbursement disbursement, ContractorAssetDetail contractorAssetDetail) throws Exception {
		logger.debug("Entering");

		if(getFinanceMainDialogCtrl() != null){
			try {

				if (financeMainDialogCtrl.getClass().getMethod("doValidateFinDetail") != null) {
					List<Object> list = (List<Object>) financeMainDialogCtrl.getClass().getMethod("doValidateFinDetail").invoke(financeMainDialogCtrl);
					if(list != null){
						currency = (String) list.get(0);
						startDate = (Date) list.get(1);
						grcEndDate = (Date) list.get(2);
					}
				}

			} catch (Exception e) {
				if(e.getCause().getClass().equals(WrongValuesException.class)){
					throw e;	
				}
				logger.error("Exception: ", e);
			}
		}

		disbursement.setNewRecord(true);
		disbursement.setDisbDisbursed(true);
		disbursement.setDisbIsActive(true);
		disbursement.setDisbRetPaid(BigDecimal.ZERO);
		disbursement.setRetPaidDate(null);
		disbursement.setWorkflowId(0);
		disbursement.setVersion(1);		

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeDisbursement", disbursement);
		map.put("disbursementDetailDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		map.put("currency", currency);
		map.put("startDate", startDate);
		map.put("grcEndDate", grcEndDate);
		map.put("ContractorAssetDetail", contractorAssetDetail);
		map.put("ContractorAssetDetails", contractorAssetDetails);
		map.put("FinanceDetail", getFinanceDetail());

		try {
			Executions.createComponents(getZULPath(disbursement.getDisbType()), window_DisbursementDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void doFillDisbursementDetails(List<FinanceDisbursement> disbursementDetails) {
		logger.debug("Entering");

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		BigDecimal endingBal = BigDecimal.ZERO; 
		BigDecimal istisnaExp = BigDecimal.ZERO; 
		BigDecimal totBillingAmt = BigDecimal.ZERO; 
		BigDecimal conslFee = BigDecimal.ZERO; 
		BigDecimal totIstisnaCost = BigDecimal.ZERO; 
		netAdvDueDetailMap= new HashMap<Long, BigDecimal>();
		netRetDueDetailMap= new HashMap<Long, BigDecimal>();

		this.listBoxDisbursementDetail.setSizedByContent(true);
		this.listBoxDisbursementDetail.getItems().clear();
		setDisbursementDetails(sortDisbDetails(disbursementDetails));
		this.listBoxDisbursementDetail.setHeight("100px");
		
		Date startDate = null;

		for (FinanceDisbursement disburse : disbursementDetails) {
			
			if(startDate == null){
				startDate = disburse.getDisbDate();
			}

			BigDecimal netadv = BigDecimal.ZERO;
			if(disburse.getContractorId() != 0 && netAdvDueDetailMap.containsKey(disburse.getContractorId())){
				netadv = netAdvDueDetailMap.get(disburse.getContractorId());
			}

			if("A".equals(disburse.getDisbType())){
				netadv = netadv.add(disburse.getDisbAmount());
			}else if("B".equals(disburse.getDisbType())){
				netadv = netadv.subtract(disburse.getDisbClaim());
				if(netadv.compareTo(BigDecimal.ZERO) < 0){
					disburse.setDisbAmount(netadv.negate());
					netadv = BigDecimal.ZERO;
				}else{
					disburse.setDisbAmount(netadv);
				}
			}else if("C".equals(disburse.getDisbType())){
				netadv = netadv.add(disburse.getDisbAmount());
			}

			//Reset calculation for Retention Amount
			if(disburse.getDisbAmount().compareTo(disburse.getDisbRetAmount())< 0){
				disburse.setDisbRetAmount(disburse.getDisbAmount());
			} 

			if(disburse.getContractorId() != 0 ){
				netAdvDueDetailMap.put(disburse.getContractorId(), netadv);
				disburse.setNetAdvDue(netadv);
			}
			endingBal = endingBal.add(disburse.getDisbAmount());

			BigDecimal netRet = BigDecimal.ZERO;
			if(disburse.getContractorId() != 0 && netRetDueDetailMap.containsKey(disburse.getContractorId())){
				netRet = netRetDueDetailMap.get(disburse.getContractorId());
				netRetDueDetailMap.put(disburse.getContractorId(), netRet.add(disburse.getDisbRetAmount()));
				disburse.setNetRetDue(netRet.add(disburse.getDisbRetAmount()));
			}

			Listitem listitem = new Listitem();
			Listcell listcell;
			listcell = new Listcell(DateUtility.formatToLongDate(disburse.getDisbDate()));
			listitem.appendChild(listcell);
			listcell = new Listcell(Labels.getLabel("label_DisbursementDetail_"+disburse.getDisbType()));
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantAppUtil.amountFormate(disburse.getDisbAmount(),formatter));
			listcell.setStyle("text-align:right;");
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantAppUtil.amountFormate(disburse.getDisbClaim(),formatter));
			listcell.setStyle("text-align:right;");
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantApplicationUtil.formatAccountNumber(disburse.getDisbAccountId()));
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantAppUtil.amountFormate(endingBal,formatter));
			listcell.setStyle("text-align:right;");
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantAppUtil.amountFormate(disburse.getDisbRetAmount(),formatter));
			listcell.setStyle("text-align:right;");
			listitem.appendChild(listcell);
			listcell = new Listcell(disburse.getDisbRemarks());
			listitem.appendChild(listcell);
			listitem.setAttribute("data", disburse);
			ComponentsCtrl.applyForward(listitem, "onDoubleClick=onDisbursementItemDoubleClicked");
			this.listBoxDisbursementDetail.appendChild(listitem);

			//Amounts Calculation

			if("B".equals(disburse.getDisbType())){
				totBillingAmt = totBillingAmt.add(disburse.getDisbClaim());
			}else if("C".equals(disburse.getDisbType())){
				conslFee = conslFee.add(disburse.getDisbAmount());
			}else if("E".equals(disburse.getDisbType())){
				istisnaExp = istisnaExp.add(disburse.getDisbAmount());
			}

			totIstisnaCost = totIstisnaCost.add(disburse.getDisbAmount());	
		}
		
		int size = disbursementDetails.size();
		if(size > 2){
			this.listBoxDisbursementDetail.setHeight((size+1) * 26 +"px");
		}

		//Amount Labels Reset with Amounts
		this.disb_totalCost.setValue(PennantAppUtil.formateAmount(totIstisnaCost,formatter));
		this.disb_consultFee.setValue(PennantAppUtil.formateAmount(conslFee,formatter));
		this.disb_totalBilling.setValue(PennantAppUtil.formateAmount(totBillingAmt,formatter));
		this.disb_expenses.setValue(PennantAppUtil.formateAmount(istisnaExp,formatter));

		if (getFinanceMainDialogCtrl() != null) {
			try {
				if (financeMainDialogCtrl.getClass().getMethod("setFinAmount", BigDecimal.class, Date.class) != null) {
					financeMainDialogCtrl.getClass().getMethod("setFinAmount", BigDecimal.class, Date.class).invoke(
							financeMainDialogCtrl, totIstisnaCost,startDate);
				}
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		}

		logger.debug("Leaving");
	}

	@SuppressWarnings("unchecked")
	public void onDisbursementItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxDisbursementDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceDisbursement disbursement = (FinanceDisbursement) item.getAttribute("data");

			if (StringUtils.trimToEmpty(disbursement.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {

				if(getFinanceMainDialogCtrl() != null){
					try {

						if (financeMainDialogCtrl.getClass().getMethod("doValidateFinDetail") != null) {
							List<Object> list = (List<Object>) financeMainDialogCtrl.getClass().getMethod("doValidateFinDetail").invoke(financeMainDialogCtrl);
							if(list != null){
								currency = (String) list.get(0);
								startDate = (Date) list.get(1);
								grcEndDate = (Date) list.get(2);
							}
						}

					} catch (Exception e) {
						if(e.getCause().getClass().equals(WrongValuesException.class)){
							throw e;	
						}
						logger.error("Exception: ", e);
					}
				}

				ContractorAssetDetail aContractorAssetDetail = null;
				for (ContractorAssetDetail contractorAssetDetail : contractorAssetDetails) {
					if(contractorAssetDetail.getContractorId() == disbursement.getContractorId()){
						aContractorAssetDetail = contractorAssetDetail;
						break;
					}
				}

				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("financeDisbursement", disbursement);
				map.put("disbursementDetailDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("currency", currency);
				map.put("startDate", startDate);
				map.put("grcEndDate", grcEndDate);
				map.put("ContractorAssetDetail", aContractorAssetDetail);
				map.put("ContractorAssetDetails", contractorAssetDetails);
				map.put("FinanceDetail", getFinanceDetail());

				try {
					Executions.createComponents(getZULPath(disbursement.getDisbType()), window_DisbursementDetailDialog, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// Contractor Details
	public void onClick$btnAddContributor(Event event) throws Exception{
		logger.debug("Entering" + event.toString());
		ContractorAssetDetail contractorAssetDetail = new ContractorAssetDetail();
		contractorAssetDetail.setFinReference(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
		List<ContractorAssetDetail> assetDetails = getContractorAssetDetails();
		
		long contractorId = 0;
		if(assetDetails != null && !assetDetails.isEmpty()){
			for (ContractorAssetDetail detail : assetDetails) {
				if(detail.getContractorId() > contractorId){
					contractorId = detail.getContractorId();
				}
			}
		}

		contractorAssetDetail.setContractorId(contractorId + 1);
		createNewContractor(contractorAssetDetail);
		logger.debug("Leaving" + event.toString());
	}

	public void createNewContractor(ContractorAssetDetail contractorAssetDetail) throws Exception {
		logger.debug("Entering");

		if(getFinanceMainDialogCtrl() != null){
			
			if(getFinanceMainDialogCtrl() != null){
				try {

					if (financeMainDialogCtrl.getClass().getMethod("doValidateFinDetail") != null) {
						@SuppressWarnings("unchecked")
						List<Object> list = (List<Object>) financeMainDialogCtrl.getClass().getMethod("doValidateFinDetail").invoke(financeMainDialogCtrl);
						if(list != null){
							currency = (String) list.get(0);
							startDate = (Date) list.get(1);
							grcEndDate = (Date) list.get(2);
						}
					}

				} catch (Exception e) {
					if(e.getCause().getClass().equals(WrongValuesException.class)){
						throw e;	
					}
					MessageUtil.showError(e);
				}
			}

			contractorAssetDetail.setNewRecord(true);

			contractorAssetDetail.setWorkflowId(0);
			contractorAssetDetail.setVersion(1);		

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("contractorAssetDetail", contractorAssetDetail);
			map.put("disbursementDetailDialogCtrl", this);
			map.put("financeMainDialogCtrl", financeMainDialogCtrl);
			map.put("newRecord", "true");
			map.put("roleCode", getRole());
			map.put("startDate", startDate);
			map.put("grcEndDate", grcEndDate);
			//map.put("ccyFormatter", getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());

			try {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceContractor/ContractorAssetDetailDialog.zul", window_DisbursementDetailDialog, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
			logger.debug("Leaving");
		}
	}
	
	public void onContractroDetailItemDoubleClicked(Event event) throws InterruptedException {
		Listitem listitem = this.listBoxContributorDetails.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			
			final ContractorAssetDetail contractorAssetDetail = (ContractorAssetDetail) listitem.getAttribute("data");
			if (StringUtils.trimToEmpty(contractorAssetDetail.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));
			} else {

				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("contractorAssetDetail", contractorAssetDetail);
				map.put("disbursementDetailDialogCtrl", this);
				map.put("financeMainDialogCtrl", financeMainDialogCtrl);
				map.put("roleCode", getRole());
				map.put("startDate", startDate);
				map.put("grcEndDate", grcEndDate);
				
				// call the ZUL-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceContractor/ContractorAssetDetailDialog.zul", window_DisbursementDetailDialog, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
	}

	public void doFillContractorDetails(List<ContractorAssetDetail> contractorAssetDetails) {
		this.listBoxContributorDetails.getItems().clear();
		this.listBoxContributorDetails.setHeight("100px");
		if (contractorAssetDetails != null) {		
			setContractorAssetDetails(contractorAssetDetails);
			for (ContractorAssetDetail contrAsstDtl : contractorAssetDetails) {

				double totClaimAmt = PennantApplicationUtil.formateAmount(contrAsstDtl.getTotClaimAmt(), ccyFormat).doubleValue();
				double assetValue = PennantApplicationUtil.formateAmount(contrAsstDtl.getAssetValue(), ccyFormat).doubleValue();

				BigDecimal amount = BigDecimal.valueOf((totClaimAmt / assetValue) * 10000);
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(contrAsstDtl.getContractorName());
				lc.setParent(item);
				lc = new Listcell(StringUtils.isBlank(contrAsstDtl.getLovDescCustCIF()) ? "" : 
					contrAsstDtl.getLovDescCustCIF() +"-"+contrAsstDtl.getLovDescCustShrtName());
				lc.setParent(item);
				lc = new Listcell(contrAsstDtl.getAssetDesc());
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(contrAsstDtl.getAssetValue(), ccyFormat));
				lc.setStyle("text-align:right");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(amount, 2));

				lc.setParent(item);
				lc = new Listcell(contrAsstDtl.getRecordType());
				lc.setParent(item);
				
				if(PennantConstants.RECORD_TYPE_CAN.equals(contrAsstDtl.getRecordType())){
					item.setDisabled(true);
				}else{
					item.setAttribute("data", contrAsstDtl);
					ComponentsCtrl.applyForward(item, "onDoubleClick=onContractroDetailItemDoubleClicked");
				}
				this.listBoxContributorDetails.appendChild(item);
			}
			
			int size = contractorAssetDetails.size();
			if(size > 2){
				this.listBoxContributorDetails.setHeight((size+1) * 26 +"px");
			}

		}
	}
	
	private String getZULPath(String disbType){
		logger.debug("Entering");

		String zulPath = "";
		if("A".equals(disbType)){
			zulPath = "/WEB-INF/pages/Finance/FinanceBilling/IstisnaContractorAdvanceDialog.zul";
		}else if("B".equals(disbType)){
			zulPath = "/WEB-INF/pages/Finance/FinanceBilling/IstisnaBillingDialog.zul";
		}else if("C".equals(disbType)){
			zulPath = "/WEB-INF/pages/Finance/FinanceBilling/IstisnaConsultingFeeDialog.zul";
		}else if("E".equals(disbType)){
			zulPath = "/WEB-INF/pages/Finance/FinanceBilling/IstisnaExpensesDialog.zul";
		}
		logger.debug("Leaving");
		return zulPath;
	}

	public List<FinanceDisbursement> sortDisbDetails(
			List<FinanceDisbursement> financeDisbursement) {

		if (financeDisbursement != null && financeDisbursement.size() > 0) {
			Collections.sort(financeDisbursement, new Comparator<FinanceDisbursement>() {
				@Override
				public int compare(FinanceDisbursement detail1, FinanceDisbursement detail2) {
					
					int compareValue = DateUtility.compare(detail1.getDisbDate(), detail2.getDisbDate());
					if (compareValue < 1) {
						return -1;
					}else if(compareValue == 0) {
						if(detail1.getDisbType().compareTo(detail2.getDisbType()) > 0) {
							return 1;
						}
					}
					return 0;
				}
			});
		}

		return financeDisbursement;
	}

	public boolean validateContractorAssetDetails(FinanceDetail aFinanceDetail,Tab tab) throws InterruptedException {
		logger.debug("Entering");
		
		List<ContractorAssetDetail> assetDetails = getContractorAssetDetails();

		if(assetDetails == null || assetDetails.isEmpty()){
			if(tab != null){
				tab.setSelected(true);
			}
			MessageUtil.showError(Labels.getLabel("label_ContractorList_Empty"));
			return false;
		}
		
		boolean isValid = true;
		for (ContractorAssetDetail contractorAssetDetail : contractorAssetDetails) {
			if (!PennantConstants.RECORD_TYPE_DEL.equals(contractorAssetDetail.getRecordType()) && !PennantConstants.RECORD_TYPE_CAN.equals(contractorAssetDetail.getRecordType())) {
				double amount = ((contractorAssetDetail.getTotClaimAmt().doubleValue()) /contractorAssetDetail.getAssetValue().doubleValue()) * 100;

				contractorAssetDetail.setLovDescClaimPercent(PennantApplicationUtil.unFormateAmount(BigDecimal.valueOf(amount), 2));

				if (contractorAssetDetail.getLovDescClaimPercent() != null) {
					if (contractorAssetDetail.getLovDescClaimPercent().compareTo(new BigDecimal(10000)) != 0) {
						logger.debug("Leaving");
						isValid = false;
						break;
					}
				}
			}
		}
		if(!isValid) {
			if(tab != null){
				tab.setSelected(true);
			}
			MessageUtil.showError(Labels.getLabel("label_ContractorBilling_NotComplete"));
			return false;
		}
		logger.debug("Leaving");
		aFinanceDetail.setContractorAssetDetails(assetDetails);
		return true;
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
		this.disb_noOfTerms.setValue(String.valueOf(getFinScheduleData().getFinanceMain().getNumberOfTerms() + 
				getFinScheduleData().getFinanceMain().getGraceTerms()));
		this.disb_maturityDate.setValue(DateUtility.formatToLongDate(getFinScheduleData().getFinanceMain().getMaturityDate()));
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public IstisnaFinanceMainDialogCtrl getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(IstisnaFinanceMainDialogCtrl financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}
	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public List<FinanceDisbursement> getDisbursementDetails() {
		return disbursementDetails;
	}
	public void setDisbursementDetails(List<FinanceDisbursement> disbursementDetails) {
		this.disbursementDetails = disbursementDetails;
	}

	public void setOldvar_disbursementDetails(
			List<FinanceDisbursement> oldvarDisbursementDetails) {
		this.oldvar_disbursementDetails = oldvarDisbursementDetails;
	}

	public List<FinanceDisbursement> getOldvar_disbursementDetails() {
		return oldvar_disbursementDetails;
	}

	public List<ContractorAssetDetail> getContractorAssetDetails() {
		return contractorAssetDetails;
	}

	public void setContractorAssetDetails(
			List<ContractorAssetDetail> contractorAssetDetails) {
		this.contractorAssetDetails = contractorAssetDetails;
	}

	public List<ContractorAssetDetail> getOldvar_contractorAssetDetails() {
		return oldvar_contractorAssetDetails;
	}

	public void setOldvar_contractorAssetDetails(
			List<ContractorAssetDetail> oldvarContractorAssetDetails) {
		this.oldvar_contractorAssetDetails = oldvarContractorAssetDetails;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}
}
