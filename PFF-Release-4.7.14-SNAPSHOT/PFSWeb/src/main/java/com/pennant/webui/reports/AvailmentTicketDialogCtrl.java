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
package com.pennant.webui.reports;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.North;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.Interface.service.CustomerLimitIntefaceService;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.reports.AvailAccount;
import com.pennant.backend.model.reports.AvailCommitment;
import com.pennant.backend.model.reports.AvailCustomer;
import com.pennant.backend.model.reports.AvailCustomerDetail;
import com.pennant.backend.model.reports.AvailFinance;
import com.pennant.backend.model.reports.AvailLimit;
import com.pennant.backend.model.reports.AvailPastDue;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.rmtmasters.AccountTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.ReportGenerationUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Reports/AvailmentTicketDialog.zul file.
 */
public class AvailmentTicketDialogCtrl extends GFCBaseCtrl<Customer> {
	private static final long serialVersionUID = 9031340167587772517L;
	private static final Logger logger = Logger.getLogger(AvailmentTicketDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AvailmentTicket; 			// autowired
	
	protected Longbox custID; 							// autowired
	protected Label   custShrtName; 					// autowired
	protected Textbox lovDescCustCIF; 					// autoWired
	protected Textbox custDftBranch; 					// autowired
	protected ExtendedCombobox custDftCcy; 						// autowired
	protected CurrencyBox custAmount; 					// autowired
	protected Textbox custTypeCode; 					// autowired
	protected Textbox custGroupID; 						// autowired
	protected Textbox custSubSector; 					// autowired
	protected Textbox custNationality; 					// autowired
	protected Textbox custRiskCountry; 					// autowired
	protected Textbox custSts; 							// autowired
	
	protected Textbox crCommitteeDec; 					// autowired
	protected Textbox extRating; 						// autowired
	protected Textbox clientReq; 						// autowired
	protected Textbox payInstruction; 					// autowired
	protected Textbox crDeptComment; 					// autowired
	protected Longbox tolerance; 						// autowired
	protected Decimalbox toleranceVal; 					// autowired
	
	protected Grid grid_basicDetails;
	protected Groupbox gb_cmtDetails;
	protected Radiogroup numberOfCommits;
	protected Listbox listBoxCmtDetails;
	
	protected Groupbox gb_Action;

	// Declaration of Service(s) & DAO(s)
	private transient CustomerDetailsService customerDetailsService;
	private transient CommitmentService commitmentService;
	private transient FinanceDetailService financeDetailService;
	private CustomerInterfaceService customerInterfaceService;
	private CustomerLimitIntefaceService customerLimitIntefaceService;
	private AccountTypeService accountTypeService;
	
	private Customer customer = null;
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	protected Tabbox         tabbox;
	
	boolean isfinance;
	protected Button btnPrintFromFinance;
	protected Button btnSearchCustCIF;
	protected North north_AvailmentTicket;
	protected Window parentWindow;
	private int ccyformatt=0;
	
	/**
	 * default constructor.<br>
	 */
	public AvailmentTicketDialogCtrl() {
		super();
	}
	
	@Override
	protected void doSetProperties() {
		super.pageRightName = "AvailmentTicketDialog";
	}
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Customer object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_AvailmentTicket(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AvailmentTicket);
		
		tabbox = (Tabbox)event.getTarget().getParent().getParent().getParent().getParent();

		if (arguments.containsKey("finance")) {
			 isfinance = true;
			 doSetCustomer(arguments.get("customer"),(JdbcSearchObject<Customer>) arguments.get("cifSearch"));
		}
		if (arguments.containsKey("window")) {
			parentWindow=(Window) arguments.get("window");
		
		}
		
		if (arguments.containsKey("ctrl")) {
			try {
			Object object=arguments.get("ctrl");
			object.getClass().getMethod("setAvailmentTicketDialogCtrl", this.getClass()).invoke(object, this);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		}
		
		// Hegiht Setting
		
		getBorderLayoutHeight();
		this.listBoxCmtDetails.setHeight(getListBoxHeight(this.grid_basicDetails.getRows().getVisibleItemCount()+1));
		
		doSetFieldProperties();
		doDesignByFinance();
		if (isfinance) {
			this.btnPrintFromFinance.setVisible(true);
			this.north_AvailmentTicket.setVisible(false);
			this.window_AvailmentTicket.setHeight(this.borderLayoutHeight - 80 + "px");
		}else{
			this.btnPrintFromFinance.setVisible(false);
			setDialog(DialogType.EMBEDDED);
		}
		logger.debug("Leaving");
	}

	private void doDesignByFinance() {
		if (isfinance) {
			this.btnSearchCustCIF.setVisible(false);
			this.lovDescCustCIF.setDisabled(true);
		}
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		this.lovDescCustCIF.setMaxlength(6);
		ccyformatt = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);
		this.custAmount.setMandatory(false);
		this.custAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyformatt));
		this.custAmount.setScale(ccyformatt);
		this.custDftCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.custDftCcy.setModuleName("Currency");
		this.custDftCcy.setValueColumn("CcyCode");
		this.custDftCcy.setDescColumn("CcyDesc");
		this.custDftCcy.setValidateColumns(new String[] { "CcyCode" });
		
		if (isWorkFlowEnabled()) {
			this.gb_Action.setVisible(true);
		} else {
			this.gb_Action.setVisible(false);
		}
		logger.debug("Leaving");
	}
	
	public void onFulfill$custDftCcy(Event event) {
		logger.debug("Entering");
		Object object=this.custDftCcy.getObject();
		if (object instanceof Currency) {
			Currency currency=(Currency) object;
			ccyformatt=currency.getCcyEditField();
		}else{
			ccyformatt=	SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);
		}
		this.custAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyformatt));
		this.toleranceVal.setFormat(PennantApplicationUtil.getAmountFormate(ccyformatt));
		logger.debug("Leaving");
	}
	
	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_AvailmentTicket);
		logger.debug("Leaving" + event.toString());
	}
	
	public void onClick$btnPrintFromFinance(Event event) throws InterruptedException, InterfaceException {
		logger.debug("Entering" + event.toString());
		printTicket();
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws CustomerNotFoundException 
	 * @throws CustomerLimitProcessException 
	 */
	public void onClick$btnPrint(Event event) throws InterruptedException, InterfaceException {
		logger.debug("Entering" + event.toString());
		printTicket();
		logger.debug("Leaving" + event.toString());
	}
	
	private void printTicket() throws InterfaceException, InterruptedException{
		logger.debug("Entering");
		if(this.custID.longValue() == 0){
			MessageUtil.showError("Customer must be selected.");
			return;
		}
		AvailCustomer availCustomer = new AvailCustomer();
		availCustomer.setAppDate(DateUtility.getAppDate(DateFormat.LONG_DATE));
		availCustomer.setAppTime(DateUtility.formatDate(DateUtility.getSysDate(),PennantConstants.timeFormat));
		
		availCustomer.setCustCIF(this.lovDescCustCIF.getValue() +" - "+this.custShrtName.getValue());
		availCustomer.setBranch(this.custDftBranch.getValue());
		availCustomer.setCurrency(this.custDftCcy.getValue());
		availCustomer.setAmount(PennantApplicationUtil.amountFormate(PennantApplicationUtil.unFormateAmount(this.custAmount.getActualValue(),ccyformatt), ccyformatt));
		availCustomer.setClaimNat(this.custNationality.getValue());
		availCustomer.setRiskCountry(this.custRiskCountry.getValue());
		availCustomer.setIndustry(this.custSubSector.getValue());
		availCustomer.setCustType(this.custTypeCode.getValue());
		availCustomer.setCustStatus(this.custSts.getValue());
		availCustomer.setCustGroup(this.custGroupID.getValue());
		availCustomer.setCustSatisfactory(this.crCommitteeDec.getValue());
		availCustomer.setExternalRating(this.extRating.getValue());
		availCustomer.setPaymentInstruction(this.payInstruction.getValue());
		availCustomer.setCreditDeptComment(this.crDeptComment.getValue());
		availCustomer.setClientRequest(this.clientReq.getValue());
		availCustomer.setTolerance((this.tolerance.longValue() == 0 ? "000" : this.tolerance.longValue() )+" % "+
				PennantApplicationUtil.amountFormate(PennantApplicationUtil.unFormateAmount(this.toleranceVal.getValue(), ccyformatt), ccyformatt));
		
		//Core Bank Interface Call for Customer Account Details
		AvailCustomerDetail detail = new AvailCustomerDetail();
		detail.setCustCIF(this.lovDescCustCIF.getValue());
		detail.setAcUnclsRequired(false);
		List<Object> list = new ArrayList<Object>();
		
		try {
			
			BigDecimal newExposure = this.custAmount.getActualValue() == null ?  BigDecimal.ZERO : 
				PennantApplicationUtil.unFormateAmount(this.custAmount.getActualValue(),ccyformatt);
			detail = getCustomerInterfaceService().fetchAvailCustDetails(detail, newExposure, this.custDftCcy.getValue());
			
			//Preparation of Commitment Details
			List<AvailCommitment> commitments = getCommitmentDetails(detail.getAvailLimit());
			list.add(commitments);
			
			//Tolerance Value Reset
			if(detail.getAvailLimit() != null){
				
				BigDecimal curExposure = CalculationUtil.getConvertedAmount(this.custDftCcy.getValue(), detail.getAvailLimit().getLimitCcy(), newExposure);
				
				availCustomer.setLimitCcy(detail.getAvailLimit().getLimitCcy());
				availCustomer.setAmountInLimitCcy(PennantApplicationUtil.amountFormate(curExposure,detail.getAvailLimit().getLimitCcyEdit()));
				
				curExposure = curExposure.multiply(new BigDecimal(this.tolerance.longValue())).divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
				availCustomer.setTolerance((this.tolerance.longValue() == 0 ? "000" : StringUtils.leftPad(String.valueOf(this.tolerance.longValue()), 3, '0') )+" % "+
						PennantApplicationUtil.amountFormate(curExposure,  detail.getAvailLimit().getLimitCcyEdit()));
			}
			
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			MessageUtil.showError(Labels.getLabel("label_Error_ReportNotImplementedYet.vlaue"));
			return;
		}
		
		//Fetch Account Type Descriptions and set Back to Accounts List
		List<ValueLabel> acTypeReturnList = null;
		if(detail.getAccTypeList() != null && !detail.getAccTypeList().isEmpty()){
			acTypeReturnList = getAccountTypeService().getAccountTypeDesc(detail.getAccTypeList());
			
			if(acTypeReturnList != null && !acTypeReturnList.isEmpty()){
				
				Map<String, String> acTypeMap = new HashMap<String, String>();
				for (ValueLabel valueLabel : acTypeReturnList) {
					acTypeMap.put(valueLabel.getLabel(), valueLabel.getValue());
				}
				
				for (AvailAccount account : detail.getOffBSAcList()) {
					if(acTypeMap.containsKey(account.getAcType())){
						account.setAccountDesc(acTypeMap.get(StringUtils.trimToEmpty(account.getAcType())));
					}
				}
				for (AvailAccount account : detail.getAcRcvblList()) {
					if(acTypeMap.containsKey(account.getAcType())){
						account.setAccountDesc(acTypeMap.get(StringUtils.trimToEmpty(account.getAcType())));
					}
				}
				for (AvailAccount account : detail.getAcPayblList()) {
					if(acTypeMap.containsKey(account.getAcType())){
						account.setAccountDesc(acTypeMap.get(StringUtils.trimToEmpty(account.getAcType())));
					}
				}
			}
		}else{
			
		}
		
		list.add(detail.getOffBSAcList());
		if(!detail.getOffBSAcList().isEmpty()){
			availCustomer.setOffBSAcFlag("T");
		}
		
		//Fetch Details For Limits based upon Customer Details
		List<AvailLimit> availLimits = new ArrayList<AvailLimit>();
		if(detail.getAvailLimit() != null){
			availCustomer.setLimitFlag("T");
			AvailPastDue pastDue = new AvailPastDue();
			pastDue.setCustID(this.custID.longValue());
			
			pastDue = getCustomerDetailsService().getCustPastDueDetailByCustId(pastDue, detail.getAvailLimit().getLimitCcy());
			AvailLimit availLimit = detail.getAvailLimit();
			
			if(pastDue != null){
				BigDecimal amt = PennantApplicationUtil.unFormateAmount(pastDue.getPastDueAmount(), detail.getAvailLimit().getLimitCcyEdit());
				availLimit.setPastdueAmount(PennantApplicationUtil.amountFormate(amt, detail.getAvailLimit().getLimitCcyEdit()));
				availLimit.setActualDate(DateUtility.formatToLongDate(pastDue.getPastDueFrom()));
				availLimit.setDueDays(String.valueOf(pastDue.getDueDays()));
			}else{
				availLimit.setPastdueAmount(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, detail.getAvailLimit().getLimitCcyEdit()));
				availLimit.setActualDate("");
				availLimit.setDueDays("0");
			}
			
			availLimits.add(detail.getAvailLimit());
		}
		list.add(availLimits);
		
		list.add(detail.getAcRcvblList());
		if(!detail.getAcRcvblList().isEmpty()){
			availCustomer.setAcRcvblFlag("T");
		}
		list.add(detail.getAcPayblList());
		if(!detail.getAcPayblList().isEmpty()){
			availCustomer.setAcPayblFlag("T");
		}
		list.add(detail.getColList());
		if(!detail.getColList().isEmpty()){
			availCustomer.setColtrlFlag("T");
		}
		
		//Reset Account Balances
		availCustomer.setCustActualBal(PennantApplicationUtil.amountFormate(detail.getCustActualBal(),2));
		availCustomer.setCustBlockedBal(PennantApplicationUtil.amountFormate(detail.getCustBlockedBal(),2));
		availCustomer.setCustDeposit(PennantApplicationUtil.amountFormate(detail.getCustDeposit(),2));
		availCustomer.setCustBlockedDeposit(PennantApplicationUtil.amountFormate(detail.getCustBlockedDeposit(),2));
		availCustomer.setTotalCustBal(PennantApplicationUtil.amountFormate(detail.getTotalCustBal(),2));
		availCustomer.setTotalCustBlockedBal(PennantApplicationUtil.amountFormate(detail.getTotalCustBlockedBal(),2));
		
		if (isfinance) {
			ReportGenerationUtil.generateReport("AvailmentTicket", availCustomer, list, true, 1, getUserWorkspace()
					.getLoggedInUser().getFullName(), parentWindow);
		} else {
			ReportGenerationUtil.generateReport("AvailmentTicket", availCustomer, list, true, 1, getUserWorkspace()
					.getLoggedInUser().getFullName(), this.window_AvailmentTicket);
		}

		detail = null;
		logger.debug("Leaving");
	}
	
	private List<AvailCommitment> getCommitmentDetails(AvailLimit availLimit){
		logger.debug("Entering");
		List<AvailCommitment> commitments = new ArrayList<AvailCommitment>();
		String ccy = "";
		int ccyFormatter = 0;
		if(availLimit == null){
			ccy = this.custDftCcy.getValue();
			ccyFormatter = ccyformatt;
		}else{
			ccy = availLimit.getLimitCcy();
			ccyFormatter = availLimit.getLimitCcyEdit();
		}
		if(this.gb_cmtDetails.isVisible() && this.listBoxCmtDetails.getItemCount() > 0){
			
			List<Listitem> items = this.listBoxCmtDetails.getItems();
			boolean isNewDeal = false;
			
			for (Listitem listitem : items) {
				AvailCommitment commitment = (AvailCommitment) listitem.getAttribute("data");
				
				AvailCommitment orgData = new AvailCommitment();
				BeanUtils.copyProperties(commitment, orgData);
				
				if(listitem.getFellowIfAny("cb_"+orgData.getCmtReference()) != null){
					Checkbox checkbox = (Checkbox) listitem.getFellowIfAny("cb_"+orgData.getCmtReference());
					isNewDeal = checkbox.isChecked() ? true : false;
					
					String actualenteredAmt = "";
					if(isNewDeal){
						actualenteredAmt = CalculationUtil.getConvertedAmountASString(this.custDftCcy.getValue(), orgData.getCmtCcy(),
								PennantApplicationUtil.unFormateAmount(this.custAmount.getActualValue(),ccyformatt));
					}
					
					orgData.setNewDeal(checkbox.isChecked() ? "New Deal : "+actualenteredAmt: "");
					
				}
				
				if(listitem.getFellowIfAny("guantor_"+orgData.getCmtReference()) != null){
					Textbox guarantor = (Textbox) listitem.getFellowIfAny("guantor_"+orgData.getCmtReference());
					orgData.setGuarantor(guarantor.getValue());
				}
				
				if(listitem.getFellowIfAny("agent_"+orgData.getCmtReference()) != null){
					Textbox agent = (Textbox) listitem.getFellowIfAny("agent_"+orgData.getCmtReference());
					orgData.setAgent(agent.getValue());
				}
				
				orgData.setCmtAccount(PennantApplicationUtil.formatAccountNumber(orgData.getCmtAccount()));
				orgData.setCmtAmount(PennantApplicationUtil.amountFormate(new BigDecimal(orgData.getCmtAmount()), orgData.getCcyEditField()));
				orgData.setCmtUtilizedAmount(PennantApplicationUtil.amountFormate(new BigDecimal(orgData.getCmtUtilizedAmount()), orgData.getCcyEditField()));
				
				if(isNewDeal){
					
					BigDecimal actualenteredAmt = CalculationUtil.getConvertedAmount(this.custDftCcy.getValue(), orgData.getCmtCcy(),
							PennantApplicationUtil.unFormateAmount(this.custAmount.getActualValue(),ccyformatt));
					
					BigDecimal availAmt = new BigDecimal(orgData.getCmtAvailable()).subtract(actualenteredAmt);
					
					if(availAmt.compareTo(BigDecimal.ZERO) < 0){
						orgData.setNegCmtAvailFlag("T");
					}
					orgData.setCmtAvailable(PennantApplicationUtil.amountFormate(availAmt, orgData.getCcyEditField()));
				}else{
					
					BigDecimal availAmt = new BigDecimal(orgData.getCmtAvailable());
					if(availAmt.compareTo(BigDecimal.ZERO) < 0){
						orgData.setNegCmtAvailFlag("T");
					}
					orgData.setCmtAvailable(PennantApplicationUtil.amountFormate(availAmt, orgData.getCcyEditField()));
				}
				orgData.setRevolving("1".equals(orgData.getRevolving()) ? "Y" : "N");
				if(StringUtils.isBlank(orgData.getCmtExpDate())){
					orgData.setCmtExpDate("Open");
				}else{
					
					Date date = DateUtility.getUtilDate(orgData.getCmtExpDate(),PennantConstants.DBDateTimeFormat);
					if (date.compareTo(DateUtility.getAppDate()) < 0) {
						orgData.setCmtExpFlag("T");
					}
					
					orgData.setCmtExpDate(DateUtility.formatToLongDate(DateUtility.getUtilDate(orgData.getCmtExpDate(),PennantConstants.DBDateTimeFormat)));
				}
				orgData.setCmtNotes(orgData.getCmtNotes().replace("||", "").trim());
				
				//Fetch Finance Details data
				boolean cmtAdded = false;
				BigDecimal totEqvAmt = BigDecimal.ZERO;
				BigDecimal totOSAmt = BigDecimal.ZERO;
				
				List<AvailFinance> availFinances = getFinanceDetailService().getFinanceDetailByCmtRef(orgData.getCmtReference(), 0);
				for (AvailFinance availFinance : availFinances) {
					
					AvailCommitment finCmt = new AvailCommitment();
					BeanUtils.copyProperties(orgData, finCmt);
					//Set the finreference by concating the fintype and finref
					availFinance.setFinReference(availFinance.getFinType().concat(" "+availFinance.getFinReference()));
					availFinance.setFinAmount(availFinance.getFinAmount() + availFinance.getTotalPftSchd());
					
					finCmt.setFinReference(availFinance.getFinReference());
					finCmt.setFinCcy(availFinance.getFinCcy());
					
					int formatter = CurrencyUtil.getFormat(availFinance.getFinCcy());
					 
					finCmt.setFinAmount(PennantApplicationUtil.amountFormate(new BigDecimal(availFinance.getFinAmount()), formatter));
					finCmt.setDrawnPrinciple(PennantApplicationUtil.amountFormate(new BigDecimal(availFinance.getDrawnPrinciple()), formatter));
					
					BigDecimal finamtBHD = (new BigDecimal(availFinance.getDrawnPrinciple()).multiply( CurrencyUtil.getExChangeRate(availFinance.getFinCcy()))).multiply(
							new BigDecimal(Math.pow(10,3 - formatter)));
					finCmt.setFinAmtBHD(PennantApplicationUtil.amountFormate(finamtBHD,3));
					
					
					String outStandBHD = CalculationUtil.getConvertedAmountASString(availFinance.getFinCcy(), ccy, new BigDecimal(availFinance.getOutStandingBal())); 
					finCmt.setOutStandingBal(outStandBHD);
					
					/*BigDecimal finamtBHD = (new BigDecimal(availFinance.getDrawnPrinciple()).multiply(availFinance.getCcySpotRate())).multiply(
							new BigDecimal(Math.pow(10,3 - availFinance.getCcyEditField())));
					finCmt.setFinAmtBHD(PennantApplicationUtil.amountFormate(finamtBHD,3));
					
					BigDecimal outStandBHD = (new BigDecimal(availFinance.getOutStandingBal()).multiply(availFinance.getCcySpotRate())).multiply(
							new BigDecimal(Math.pow(10,3 - availFinance.getCcyEditField())));
					finCmt.setOutStandingBal(PennantApplicationUtil.amountFormate(outStandBHD,3));*/
					finCmt.setLastRepay(availFinance.getLastRepay() == null? "" : DateUtility.formatToLongDate(DateUtility.getUtilDate(availFinance.getLastRepay(),PennantConstants.DBDateTimeFormat)));
					finCmt.setMaturityDate(DateUtility.formatToLongDate(DateUtility.getUtilDate(availFinance.getMaturityDate(),PennantConstants.DBDateTimeFormat)));
					finCmt.setProfitRate(availFinance.getProfitRate());
					finCmt.setRepayFrq(availFinance.getRepayFrq());
					finCmt.setStatus(availFinance.getStatus());
					finCmt.setFinDivision(availFinance.getFinDivision());
					finCmt.setFinDivisionDesc(availFinance.getFinDivisionDesc());
					finCmt.setLimitCcy(ccy);
					totEqvAmt = totEqvAmt.add(finamtBHD);
					totOSAmt = totOSAmt.add(new BigDecimal(outStandBHD.replace(",", "")));
					
					cmtAdded = true;
					commitments.add(finCmt);
				}
				
				if(!cmtAdded){
					commitments.add(orgData);
				}else{
					
					AvailCommitment finCmt = new AvailCommitment();
					BeanUtils.copyProperties(orgData, finCmt);
					
					finCmt.setFinReference("Total");
					finCmt.setFinAmtBHD(PennantApplicationUtil.amountFormate(totEqvAmt,3));
					finCmt.setOutStandingBal(PennantApplicationUtil.amountFormate(PennantApplicationUtil.unFormateAmount(totOSAmt, ccyFormatter),ccyFormatter));
					commitments.add(finCmt);
				}
			}
		}
		
		//Finance Details , which are not Having Commitment
		List<AvailFinance> availFinances = getFinanceDetailService().getFinanceDetailByCmtRef("", this.custID.longValue());
		if(availFinances != null && !availFinances.isEmpty()){
			AvailCommitment commitment = new AvailCommitment();
			commitment.setCmtReference("No Commitment");//Dont change this one -- Depends on Report constant

			BigDecimal totEqvAmt = BigDecimal.ZERO;
			BigDecimal totOSAmt = BigDecimal.ZERO;
			
			for (AvailFinance availFinance : availFinances) {

				AvailCommitment finCmt = new AvailCommitment();
				BeanUtils.copyProperties(commitment, finCmt);
				
				//Set the finreference by concating the fintype and finref
				availFinance.setFinReference(availFinance.getFinType().concat(" "+availFinance.getFinReference()));
				availFinance.setFinAmount(availFinance.getFinAmount() + availFinance.getTotalPftSchd());
				
				int formatter = CurrencyUtil.getFormat(availFinance.getFinCcy());
				
				finCmt.setFinReference(availFinance.getFinReference());
				finCmt.setFinCcy(availFinance.getFinCcy());
				finCmt.setFinAmount(PennantApplicationUtil.amountFormate(new BigDecimal(availFinance.getFinAmount()), formatter));
				finCmt.setDrawnPrinciple(PennantApplicationUtil.amountFormate(new BigDecimal(availFinance.getDrawnPrinciple()), formatter));
				
				BigDecimal finamtBHD = (new BigDecimal(availFinance.getDrawnPrinciple()).multiply( CurrencyUtil.getExChangeRate(availFinance.getFinCcy()))).multiply(
						new BigDecimal(Math.pow(10, 3- formatter)));
				
				finCmt.setFinAmtBHD(PennantApplicationUtil.amountFormate(finamtBHD,3));
				
				BigDecimal outStandBHD = (new BigDecimal(availFinance.getOutStandingBal()).multiply( CurrencyUtil.getExChangeRate(availFinance.getFinCcy()))).multiply(
						new BigDecimal(Math.pow(10,3 - formatter)));
				finCmt.setOutStandingBal(PennantApplicationUtil.amountFormate(outStandBHD,3));
				finCmt.setLastRepay(availFinance.getLastRepay() == null? "" : DateUtility.formatToLongDate(DateUtility.getUtilDate(availFinance.getLastRepay(),PennantConstants.DBDateTimeFormat)));
				finCmt.setMaturityDate(DateUtility.formatToLongDate(DateUtility.getUtilDate(availFinance.getMaturityDate(),PennantConstants.DBDateTimeFormat)));
				finCmt.setProfitRate(availFinance.getProfitRate());
				finCmt.setRepayFrq(availFinance.getRepayFrq());
				finCmt.setStatus(availFinance.getStatus());
				finCmt.setFinDivision(availFinance.getFinDivision());
				finCmt.setFinDivisionDesc(availFinance.getFinDivisionDesc());

				totEqvAmt = totEqvAmt.add(finamtBHD);
				totOSAmt = totOSAmt.add(outStandBHD);

				commitments.add(finCmt);
			}
			
			AvailCommitment finCmt = new AvailCommitment();
			BeanUtils.copyProperties(commitment, finCmt);
			
			finCmt.setFinReference("Total");
			finCmt.setFinAmtBHD(PennantApplicationUtil.amountFormate(totEqvAmt,3));
			finCmt.setOutStandingBal(PennantApplicationUtil.amountFormate(totOSAmt,3));
			commitments.add(finCmt);
		}
		logger.debug("Leaving");
		return commitments;
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		if (doClose(false)) {
			if (tabbox != null) {
				tabbox.getSelectedTab().close();
			}
		}
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.custID.setReadonly(true);
		this.custGroupID.setReadonly(true);
		
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
		this.custTypeCode.setValue("");
		this.custShrtName.setValue("");
		this.custDftBranch.setValue("");
		this.custGroupID.setValue("");
		this.custSts.setValue("");
		this.custSubSector.setValue("");
		this.custRiskCountry.setValue("");
		this.custNationality.setValue("");
		logger.debug("Leaving");
	}
	
	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void onChange$lovDescCustCIF(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		this.lovDescCustCIF.clearErrorMessage();
		this.custShrtName.setValue("");
		customer = (Customer)PennantAppUtil.getCustomerObject(this.lovDescCustCIF.getValue(), null);

		if (customer != null) {
			setCustomerData();
		} else {
			if(!"".equals(this.lovDescCustCIF.getValue())){
				this.custShrtName.setValue("");
				this.custID.setValue(Long.valueOf(0));
				throw new WrongValueException(this.lovDescCustCIF, Labels.getLabel("FIELD_NO_INVALID", 
						new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_CustID.value") }));
			}
		}

		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		this.lovDescCustCIF.clearErrorMessage();
		doSearchCustomerCIF();
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * To load the customerSelect filter dialog
	 * 
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);

		logger.debug("Leaving");
	}
	
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		customer = (Customer) nCustomer;
		this.custCIFSearchObject = newSearchObject;
		if(customer != null){
			setCustomerData();
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Reset Customer Data
	 */
	private void setCustomerData(){
		logger.debug("Entering");
		
		this.custID.setValue(customer.getCustID());
		this.lovDescCustCIF.setValue(String.valueOf(customer.getCustCIF()));
		this.custShrtName.setValue(customer.getCustShrtName());
		this.custID.setValue(customer.getCustID());
		this.custShrtName.setValue(customer.getCustShrtName());
		this.custDftBranch.setValue(StringUtils.isBlank(customer.getLovDescCustDftBranchName()) ? "" : customer.getCustDftBranch() + "-" + customer.getLovDescCustDftBranchName());
		this.custTypeCode.setValue(StringUtils.isBlank(customer.getLovDescCustTypeCodeName()) ? "" : customer.getCustTypeCode() + "-" + customer.getLovDescCustTypeCodeName());
		this.custGroupID.setValue(StringUtils.isBlank(customer.getLovDescCustGroupCode())? "" : customer.getLovDescCustGroupCode() + "-" + customer.getLovDesccustGroupIDName());
		this.custSubSector.setValue(StringUtils.isBlank(customer.getLovDescCustSectorName()) ? "" : customer.getCustSector() + "-" + customer.getLovDescCustSectorName());
		this.custNationality.setValue("");
		this.custRiskCountry.setValue(StringUtils.isBlank(customer.getLovDescCustRiskCountryName()) ? "" : customer.getCustRiskCountry() + "-" + customer.getLovDescCustRiskCountryName());
		this.custSts.setValue(StringUtils.isBlank(customer.getLovDescCustStsName()) ? "" : customer.getCustSts() + "-" + customer.getLovDescCustStsName());
		this.custDftCcy.setValue(StringUtils.trimToEmpty(customer.getCustBaseCcy()),CurrencyUtil.getCcyDesc(customer.getCustBaseCcy()));
     	//this.custDftCcy.setValue(StringUtils.trimToEmpty(customer.getLovDescCustBaseCcyName()).equals("") ? "" : customer.getCustBaseCcy() + "-" + customer.getLovDescCustBaseCcyName());
		
		ccyformatt = CurrencyUtil.getFormat(customer.getCustBaseCcy());
		this.toleranceVal.setFormat(PennantApplicationUtil.getAmountFormate(ccyformatt));
		this.custAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyformatt));
		
		//Fetching Customer Rating Details
		List<CustomerRating> ratingList = getCustomerDetailsService().getCustomerRatingByCustId(customer.getCustID(), "_AView");
		
		String extrating = "";
		if(ratingList != null && !ratingList.isEmpty()){
			for (CustomerRating rating : ratingList) {
				if("AIB".equals(rating.getCustRatingType())){
					this.crCommitteeDec.setValue(rating.getCustRatingCode()+"-"+rating.getLovDesccustRatingCodeDesc());
				}else{
					
					if(!"ABG".equals(rating.getCustRatingType())){
						extrating = extrating + (rating.getCustRatingType() + ":"+rating.getLovDesccustRatingCodeDesc()) +" ; ";
					}
				}
			}
			this.extRating.setValue(extrating);
		}
		
		//Fetch Commitment Details
		List<AvailCommitment> commitmentList = getCommitmentService().getCommitmentListByCustId(customer.getCustID());
		this.listBoxCmtDetails.getItems().clear();
		if(commitmentList != null && !commitmentList.isEmpty()){
			this.gb_cmtDetails.setVisible(true);
			
			//Prepare Rendering for Commitment Details
			renderCommitments(commitmentList);
		}
		
		logger.debug("Leaving");	
	}
	
	/**
	 * Method for Rendering Commitments Details
	 */
	private void renderCommitments(List<AvailCommitment> availCmtlist){
		logger.debug("Entering");
		
		Listitem item = null;
		Listcell lc = null;
		
		for (AvailCommitment availCommitment : availCmtlist) {
			
			item = new Listitem();
			lc = new Listcell(availCommitment.getCmtReference());
			item.appendChild(lc);
			
			lc = new Listcell();
			Radio cb = new Radio();
			cb.setId("cb_"+availCommitment.getCmtReference());
			cb.setRadiogroup(numberOfCommits);
			lc.appendChild(cb);
			item.appendChild(lc);
			
			lc = new Listcell();
			Textbox guantor = new Textbox();
			guantor.setId("guantor_"+availCommitment.getCmtReference());
			lc.appendChild(guantor);
			item.appendChild(lc);
			
			lc = new Listcell();
			Textbox agent = new Textbox();
			agent.setId("agent_"+availCommitment.getCmtReference());
			lc.appendChild(agent);
			item.appendChild(lc);
			
			item.setAttribute("data", availCommitment);
			this.listBoxCmtDetails.appendChild(item);
		}
		logger.debug("Leaving");
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCommitmentService(CommitmentService commitmentService) {
		this.commitmentService = commitmentService;
	}
	public CommitmentService getCommitmentService() {
		return commitmentService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setCustomerInterfaceService(CustomerInterfaceService customerInterfaceService) {
		this.customerInterfaceService = customerInterfaceService;
	}
	public CustomerInterfaceService getCustomerInterfaceService() {
		return customerInterfaceService;
	}

	public void setCustomerLimitIntefaceService(
			CustomerLimitIntefaceService customerLimitIntefaceService) {
		this.customerLimitIntefaceService = customerLimitIntefaceService;
	}

	public CustomerLimitIntefaceService getCustomerLimitIntefaceService() {
		return customerLimitIntefaceService;
	}

	public AccountTypeService getAccountTypeService() {
		return accountTypeService;
	}

	public void setAccountTypeService(AccountTypeService accountTypeService) {
		this.accountTypeService = accountTypeService;
	}
	
}