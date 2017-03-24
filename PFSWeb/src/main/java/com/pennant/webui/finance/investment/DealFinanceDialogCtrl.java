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
 * FileName    		:  MurabahaFinanceMainDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.finance.investment;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.Interface.model.IAccounts;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.exception.PFFInterfaceException;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.AccountingDetailDialogCtrl;
import com.pennant.webui.finance.financemain.FeeDetailDialogCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/FinanceMainDialog.zul file.
 */
public class DealFinanceDialogCtrl extends DealFinanceBaseCtrl<InvestmentFinHeader> {
	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(DealFinanceDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_InvestmentDealDialogCtrl; 				
	protected Decimalbox 	finAmount;               						

	private transient boolean validationOn;

	// not auto wired variables
	private DealFinanceListCtrl dealFinanceListCtrl;
	private AccountingDetailDialogCtrl accountingDetailDialogCtrl = null;
	private FeeDetailDialogCtrl feeDetailDialogCtrl = null;

	private AEAmountCodes amountCodes; 	
	private AccountEngineExecution engineExecution;

	private boolean newRecord=false;
	private String eventCode = "";

	/**
	 * default constructor.<br>
	 */
	public DealFinanceDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "InvestmentFinHeaderDialog";
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
	public void onCreate$window_InvestmentDealDialogCtrl(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_InvestmentDealDialogCtrl);

		if (arguments.containsKey("investmentFinHeader")) {
			this.investmentFinHeader = (InvestmentFinHeader) arguments.get("investmentFinHeader");
			financeDetail = this.investmentFinHeader.getFinanceDetail();
			setFinanceDetail(financeDetail);
			InvestmentFinHeader befImage = new InvestmentFinHeader();
			BeanUtils.copyProperties(this.investmentFinHeader, befImage);
			this.investmentFinHeader.setBefImage(befImage);
			setInvestmentFinHeader(this.investmentFinHeader);
		}

		if(arguments.containsKey("DealFinanceListCtrl")) {
			this.dealFinanceListCtrl = (DealFinanceListCtrl) arguments.get("DealFinanceListCtrl");
		}

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		financeMain.setInvestmentRef(getInvestmentFinHeader().getInvestmentRef());
		doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "InvestmentFinHeaderDialog");
		}

		if (arguments.containsKey("eventCode")) {
			eventCode = (String) arguments.get("eventCode");
		}
		
		doSetFieldProperties();
		doCheckRights();
		doShowDialog(getInvestmentFinHeader(), getFinanceDetail());
		logger.debug("Leaving " + event.toString());
	}

	public void doSetFieldProperties(){
		logger.debug("Leaving ");

		FinanceMain aFinanceMain =  getFinanceDetail().getFinScheduleData().getFinanceMain();
		int format = CurrencyUtil.getFormat(aFinanceMain.getFinCcy());
		
		this.disbAcctId.setAccountDetails(aFinanceMain.getFinType(), AccountConstants.FinanceAccount_DISB, aFinanceMain.getFinCcy());
		this.disbAcctId.setFormatter(format);
		this.disbAcctId.setBranchCode(aFinanceMain.getFinBranch());
		this.disbAcctId.setCustCIF(aFinanceMain.getLovDescCustCIF());
		this.disbAcctId.setMandatoryStyle(true);
		
		this.repayAcctId.setAccountDetails(aFinanceMain.getFinType(), AccountConstants.FinanceAccount_REPY, aFinanceMain.getFinCcy());
		this.repayAcctId.setFormatter(format);
		this.repayAcctId.setBranchCode(aFinanceMain.getFinBranch());
		this.repayAcctId.setCustCIF(aFinanceMain.getLovDescCustCIF());
		this.repayAcctId.setMandatoryStyle(true);
		
		logger.debug("Leaving ");
	}
	

	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) {
		logger.debug("Entering " + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_InvestmentDealDialogCtrl, "Customer");
		if (dataObject instanceof String) {
			this.custID.setValue(Long.valueOf(0));
			this.lovDescCustCIF.setValue("");
			this.custShrtName.setValue("");
			this.finBranch.setValue(dataObject.toString());
			this.lovDescFinBranchName.setValue("");
		} else {
			Customer details = (Customer) dataObject;
			if (details != null) {
				this.custID.setValue(details.getCustID());
				this.lovDescCustCIF.setValue(details.getCustCIF());
				this.custShrtName.setValue(details.getCustShrtName());
				this.finBranch.setValue(details.getCustDftBranch());
				this.lovDescFinBranchName.setValue(details.getCustDftBranch()+"-"+details.getLovDescCustDftBranchName());
			}
		}
		logger.debug("Leaving " + event.toString());
	}


	public void onClick$viewCustInfo(Event event){
		logger.debug("Entering " + event.toString());
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("custid", this.custID.longValue());
			map.put("finReference", this.investmentRef.getValue());
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/FinCustomerDetailsEnq.zul", window_InvestmentDealDialogCtrl, map);
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void onChange$lovDescCustCIF(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Customer customer = (Customer)PennantAppUtil.getCustomerObject(this.lovDescCustCIF.getValue(), null);

		if (customer != null) {
			this.custID.setValue(customer.getCustID());
			this.lovDescCustCIF.setValue(customer.getCustCIF());
			this.custShrtName.setValue(customer.getCustShrtName());
			this.finBranch.setValue(customer.getCustDftBranch());
			this.repayAcctId.setCustCIF(customer.getCustCIF());
			this.disbAcctId.setCustCIF(customer.getCustCIF());
			
			this.lovDescFinBranchName.setValue(customer.getCustDftBranch()+"-"+customer.getLovDescCustDftBranchName());
		} else {
			this.custID.setValue(Long.valueOf(0));
			this.repayAcctId.setCustCIF("");
			this.disbAcctId.setCustCIF("");
			this.repayAcctId.setValue("");
			this.disbAcctId.setValue("");
			this.lovDescCustCIF.setValue("");
			this.custShrtName.setValue("");
			this.finBranch.setValue("");
			this.lovDescFinBranchName.setValue("");
			throw new WrongValueException(this.lovDescCustCIF, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_CustID.value") }));
		}

		logger.debug("Leaving" + event.toString());
	}


	public void onChange$ratePerc(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		calculateMaturityAmount();
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$principalAmt(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		calculateMaturityAmount();
		logger.debug("Leaving" + event.toString());
	}

	public void calculateMaturityAmount(){
		logger.debug("Entering");

		BigDecimal 	maturityAmount;
		BigDecimal 	prfBasis;
		BigDecimal 	calMaturityAmount;

		if(this.finAmount.getValue() != null && this.repayProfitRate.getValue().compareTo(BigDecimal.ZERO) == 1){
			InvestmentFinHeader investmentFinHeader = getInvestmentFinHeader();

			Date startDate = investmentFinHeader.getStartDate();
			Date maturityDate = investmentFinHeader.getMaturityDate();
			String pftDaysBasis = investmentFinHeader.getProfitDaysBasis();

			prfBasis = CalculationUtil.getInterestDays(startDate, maturityDate, pftDaysBasis);

			calMaturityAmount = ((this.finAmount.getValue().multiply(this.repayProfitRate.getValue())).
					divide(new BigDecimal(100))).multiply(prfBasis);

			maturityAmount = this.finAmount.getValue().add(calMaturityAmount);

			this.totalRepayAmt.setValue(maturityAmount);
		}
		logger.debug("Leaving");
	}

	/**
	 * when clicks on button "btnSearchRepayAcctId"
	 * 
	 * @param event
	 * @throws InterruptedException 
	 * @throws AccountNotFoundException
	 */
	public void onClick$btnSearchRepayAcctId(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.lovDescCustCIF.clearErrorMessage();
		this.repayAcctId.clearErrorMessage();

		if(StringUtils.isNotBlank(this.lovDescCustCIF.getValue())) {
			Object dataObject;

			List<Accounts> accountList = new ArrayList<Accounts>();
			accountList = getAccountsService().getAccountsByAcPurpose("M");
			String acType = "";
			for (int i = 0; i < accountList.size(); i++) {
				acType = acType + accountList.get(i).getAcType();
			}

			List<IAccounts> iAccountList = new ArrayList<IAccounts>();
			IAccounts iAccount = new IAccounts();
			iAccount.setAcCcy(this.finCcy.getValue());
			iAccount.setAcType(acType);
			iAccount.setDivision(FinanceConstants.FIN_DIVISION_TREASURY);

			iAccount.setAcCustCIF(this.lovDescCustCIF.getValue());
			try {
				iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);
				dataObject = ExtendedSearchListBox.show(this.window_InvestmentDealDialogCtrl, "Accounts", iAccountList);
				if (dataObject instanceof String) {
					this.repayAcctId.setValue(dataObject.toString());
				} else {
					IAccounts details = (IAccounts) dataObject;
					if (details != null) {
						this.repayAcctId.setValue(details.getAccountId());
					}
				}
			} catch (Exception e) {
				logger.error("Exception: ", e);
				Messagebox.show("Account Details not Found!!!", Labels.getLabel("message.Error") , 
						Messagebox.ABORT, Messagebox.ERROR);
			}
		}else {
			throw new WrongValueException(this.lovDescCustCIF,Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_CustID.value") }));
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "btnSearchDisbAcctId"
	 * 
	 * @param event
	 * @throws InterruptedException 
	 * @throws AccountNotFoundException
	 */
	public void onClick$btnSearchDisbAcctId(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		this.lovDescCustCIF.clearErrorMessage();
		this.disbAcctId.clearErrorMessage();
		this.repayAcctId.clearErrorMessage();

		if(StringUtils.isNotBlank(this.lovDescCustCIF.getValue())) {
			Object dataObject;
 

			List<IAccounts> iAccountList = new ArrayList<IAccounts>();
			IAccounts iAccount = new IAccounts();
			iAccount.setAcCcy(this.finCcy.getValue());
			iAccount.setAcType("");
			iAccount.setAcCustCIF(this.lovDescCustCIF.getValue());
			iAccount.setDivision(FinanceConstants.FIN_DIVISION_TREASURY);
			try {
				iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);

				dataObject = ExtendedSearchListBox.show(this.window_InvestmentDealDialogCtrl, "Accounts", iAccountList);
				if (dataObject instanceof String) {
					this.disbAcctId.setValue(dataObject.toString());
				} else {
					IAccounts details = (IAccounts) dataObject;

					if (details != null) {
						this.disbAcctId.setValue(details.getAccountId());

						if(StringUtils.isBlank(this.repayAcctId.getValue())){
							this.repayAcctId.setValue(details.getAccountId());
						}
					}
				}
			} catch (Exception e) {
				logger.error("Exception: ", e);
				Messagebox.show("Account Details not Found!!!", Labels.getLabel("message.Error") , 
						Messagebox.ABORT, Messagebox.ERROR);
			}
		}else {
			throw new WrongValueException(this.lovDescCustCIF,Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_CustID.value") }));
		}

		logger.debug("Leaving " + event.toString());
	}


	public void onSelectAccountingDetail(ForwardEvent event) throws IllegalAccessException, InvocationTargetException, InterruptedException {
		logger.debug("Entering " + event.toString());
		ArrayList<WrongValueException> wl = new ArrayList<WrongValueException>();
		try {
			doWriteComponentsToBean(getFinanceDetail());
		} catch (WrongValueException we) {
			wl.add(we);
			showErrorDetails(wl, financeTypeDetailsTab);
		} finally {
			
		}
		super.onSelectAccountingDetail(event);
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

		getUserWorkspace().allocateAuthorities("InvestmentFinHeaderDialog",getRole());

		this.btnNew.setVisible(false);
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_InvestmentFinHeaderDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_InvestmentFinHeaderDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_InvestmentFinHeaderDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSave();
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
		MessageUtil.showHelpWindow(event, window_InvestmentDealDialogCtrl);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());

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
	 * Deletes a FinContributorDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

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
		doWriteBeanToComponents(this.investmentFinHeader.getBefImage(), financeDetail);
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCity
	 *            
	 */
	public void doWriteBeanToComponents(InvestmentFinHeader aFinHeader, FinanceDetail aFinanceDetail) {
		logger.debug("Entering ");

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		int investFinCCYFormat = CurrencyUtil.getFormat(aFinHeader.getFinCcy());

		this.investmentRef.setValue(aFinHeader.getInvestmentRef());		
		this.finType.setValue(aFinanceMain.getFinType());
		this.dealTcktRef.setValue(aFinanceMain.getFinReference());		
		this.finCcy.setValue(aFinanceMain.getFinCcy());
		this.lovDescFinTypeName.setValue(aFinanceMain.getFinType()+ "-"+ aFinanceMain.getLovDescFinTypeName());
		this.profitDaysBasis.setValue(PennantAppUtil.getlabelDesc(aFinanceMain.getProfitDaysBasis(), PennantStaticListUtil.getProfitDaysBasis()));
		this.custID.setValue(aFinanceMain.getCustID());
		this.lovDescCustCIF.setValue(aFinanceMain.getLovDescCustCIF());
		this.custShrtName.setValue(aFinanceMain.getLovDescCustShrtName());
		this.finBranch.setValue(aFinanceMain.getFinBranch());
		this.lovDescFinBranchName.setValue(aFinanceMain.getFinBranch()+ "-"+ aFinanceMain.getLovDescFinBranchName());
		this.startDate.setValue(aFinanceMain.getFinStartDate());
		this.maturityDate.setValue(aFinanceMain.getMaturityDate());
		this.finAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAmount(), investFinCCYFormat));
		this.totalRepayAmt.setValue(PennantAppUtil.formateAmount(aFinanceMain.getTotalRepayAmt(), investFinCCYFormat));		
		this.repayProfitRate.setValue(PennantApplicationUtil.formatRate(aFinanceMain.getRepayProfitRate().doubleValue(),9));		
		this.disbAcctId.setValue(aFinanceMain.getDisbAccountId());
		this.repayAcctId.setValue(aFinanceMain.getRepayAccountId());
		fillComboBox(this.finRepayMethod, aFinanceMain.getFinRepayMethod(), PennantStaticListUtil.getRepayMethods(), "");

		this.recordStatus.setValue(aFinanceMain.getRecordStatus());

		aFinanceMain.setCurDisbursementAmt(aFinanceMain.getFinAmount());
		aFinanceMain.setFeeChargeAmt(BigDecimal.ZERO);
		aFinanceMain.setInsuranceAmt(BigDecimal.ZERO);

		try {
			doFillTabs(financeDetail);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		} 

		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCity
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InterruptedException 
	 */
	public void doWriteComponentsToBean(FinanceDetail aFinanceDetail) throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering ");
		
		FinScheduleData aFinanceSchData = aFinanceDetail.getFinScheduleData();
		FinanceMain aFinanceMain = aFinanceSchData.getFinanceMain();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			this.disbAcctId.getValidatedValue();
			if(StringUtils.isNotBlank(this.disbAcctId.getValue())){
			aFinanceMain.setDisbAccountId(PennantApplicationUtil.unFormatAccountNumber(this.disbAcctId.getValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.repayAcctId.getValidatedValue();
			if(StringUtils.isNotBlank(this.repayAcctId.getValue())){
				aFinanceMain.setRepayAccountId(PennantApplicationUtil.unFormatAccountNumber(this.repayAcctId.getValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (!this.finRepayMethod.isDisabled() && "#".equals(getComboboxValue(this.finRepayMethod))) {
				throw new WrongValueException(this.finRepayMethod, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_DealFinanceDialog_FinRepayMethod.value") }));
			}
			
			aFinanceMain.setFinRepayMethod(getComboboxValue(this.finRepayMethod));
			
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aFinanceMain.setCurDisbursementAmt(aFinanceMain.getFinAmount());
		aFinanceMain.setGrcPeriodEndDate(DateUtility.getUtilDate(DateUtility.formatDate(aFinanceMain.getGrcPeriodEndDate(),
				PennantConstants.DBDateFormat), PennantConstants.DBDateFormat));
		aFinanceSchData.getFinanceMain().setFeeChargeAmt(BigDecimal.ZERO);
		aFinanceSchData.getFinanceMain().setInsuranceAmt(BigDecimal.ZERO);
		if (getFeeDetailDialogCtrl() != null) {
			try {
				aFinanceSchData = getFeeDetailDialogCtrl().doExecuteFeeCharges(true, false, aFinanceSchData, true,aFinanceMain.getFinStartDate());
			} catch (PFFInterfaceException e) {
				logger.error("Exception: ", e);
			}
		}

		showErrorDetails(wve,financeTypeDetailsTab);
		aFinanceMain.setRecordStatus(this.recordStatus.getValue());

		logger.debug("Leaving ");
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
	public void doShowDialog(InvestmentFinHeader aInvestmentFinHeader,FinanceDetail aFinanceDetail) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else if (isWorkFlowEnabled()) {
			this.btnNotes.setVisible(false);
			doEdit();
		} else {
			this.btnCtrl.setInitEdit();
			doReadOnly();
			btnCancel.setVisible(false);
		}

		try {
			
			// fill the components with the data
			doWriteBeanToComponents(aInvestmentFinHeader,aFinanceDetail);
			setRepayAccMandatory();
			
			setDialog(DialogType.OVERLAPPED);

		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Setting Mandatory Check to Repay Account ID based on Repay Method
	 * @param event
	 */
	public void onChange$finRepayMethod(Event event){
		logger.debug("Entering" + event.toString());
		setRepayAccMandatory();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.disbAcctId.isReadonly()) {
			this.disbAcctId.setConstraint(new PTStringValidator(Labels.getLabel("label_MurabahaFinanceMainDialog_DisbAcctId.value"),null,true));
		}

		if (!this.repayAcctId.isReadonly() && this.repayAcctId.isMandatory()) {
			this.repayAcctId.setConstraint(new PTStringValidator(Labels.getLabel("label_MurabahaFinanceMainDialog_RepayAcctId.value"),null,true));
		}


		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.disbAcctId.setConstraint("");
		this.repayAcctId.setConstraint("");
		this.finRepayMethod.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		readOnlyComponent(true, this.lovDescCustCIF);
		readOnlyComponent(true, this.custID);
		readOnlyComponent(true, this.startDate);
		readOnlyComponent(true, this.maturityDate);
		readOnlyComponent(true, this.profitDaysBasis);
		readOnlyComponent(true, this.disbAcctId);
		readOnlyComponent(true, this.repayAcctId);
		readOnlyComponent(isReadOnly("InvestmentFinHeaderDialog_FinRepayMethod"), this.finRepayMethod);
		
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (getFinanceDetail().isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_New();
			btnCancel.setVisible(true);
		}

		logger.debug("Leaving");
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.disbAcctId.setErrorMessage("");
		this.repayAcctId.setErrorMessage("");
		this.finRepayMethod.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		readOnlyComponent(true, this.lovDescCustCIF);
		readOnlyComponent(true, this.custID);
		readOnlyComponent(true, this.startDate);
		readOnlyComponent(true, this.maturityDate);
		readOnlyComponent(true, this.profitDaysBasis);
		readOnlyComponent(isReadOnly("InvestmentFinHeaderDialog_DisbusMentAccount"), this.disbAcctId);
		readOnlyComponent(isReadOnly("InvestmentFinHeaderDialog_RepaymentMentAccount"), this.repayAcctId);
		readOnlyComponent(isReadOnly("InvestmentFinHeaderDialog_FinRepayMethod"), this.finRepayMethod);

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

		this.investmentRef.setValue("");
		this.finCcy.setValue("");
		this.profitDaysBasis.setValue("");
		this.startDate.setText("");
		this.maturityDate.setText("");
		this.prinInvested.setValue("");
		this.prinMaturity.setText("");
		logger.debug("Leaving");
	}
	
	protected void setRepayAccMandatory(){
		if(this.finRepayMethod.getSelectedIndex() != 0){
			String repayMthd = StringUtils.trimToEmpty(this.finRepayMethod.getSelectedItem().getValue().toString());
			if(repayMthd.equals(FinanceConstants.REPAYMTH_AUTO)){
				this.repayAcctId.setMandatoryStyle(true);
			}else if(repayMthd.equals(FinanceConstants.REPAYMTH_MANUAL)){
				this.repayAcctId.setMandatoryStyle(false);
			}
		}
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");
		
		getTreasuaryFinanceService().setFinanceDetails(getFinanceDetail(), "ACCOUNTING", getRole());
		getTreasuaryFinanceService().setFinanceDetails(getFinanceDetail(), "AGGREMENTS", getRole());
		
		FinanceDetail aFinanceDetail = new FinanceDetail();
		Cloner cloner = new Cloner();
		aFinanceDetail = cloner.deepClone(getFinanceDetail());

		boolean isNew = false;
		
		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		
		// fill the DocumentDetails object with the components data
		doWriteComponentsToBean(aFinanceDetail);
		aFinanceDetail = buildSchedule(aFinanceDetail);
		
		//Service level validations 
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		if (this.userAction.getSelectedItem() != null){	
			aFinanceDetail.setUserAction(this.userAction.getSelectedItem().getLabel());
		} else {
			aFinanceDetail.setUserAction("");
		} 

		if ("Save".equalsIgnoreCase(aFinanceDetail.getUserAction()) ||
				"Confirm".equalsIgnoreCase(aFinanceDetail.getUserAction())) {
			recSave = true;
			aFinanceDetail.setActionSave(true);
		}
		aFinanceDetail.setUserAction(this.userAction.getSelectedItem().getLabel());
		aFinanceDetail.setAccountingEventCode(eventCode);

		// fill the financeMain object with the components data

		String tempRecordStatus = aFinanceMain.getRecordType();
		isNew = aFinanceDetail.isNew();

		//Document Details Saving
		if(getDocumentDetailDialogCtrl() != null){
			aFinanceDetail.setDocumentDetailsList(getDocumentDetailDialogCtrl().getDocumentDetailsList());
		}

		//Finance Asset Loan Details Tab
		doSave_Assets(aFinanceDetail, isNew, tempRecordStatus, true);

		//Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aFinanceDetail);
			if(!validationSuccess){
				return;
			}
		} else {
			aFinanceDetail.setFinanceCheckList(null);
		}

			//Finance Fee Charge Details Tab
		if (getFeeDetailDialogCtrl() != null &&  getFinanceDetail().getFinScheduleData().getFeeRules() != null &&
				getFinanceDetail().getFinScheduleData().getFeeRules().size() > 0) {
			// check if fee & charges rules executed or not
			if (!getFeeDetailDialogCtrl().isFeeChargesExecuted()) {
				MessageUtil.showErrorMessage(Labels.getLabel("label_Finance_Calc_Fee"));
				return;
			}
		}


		if(StringUtils.isBlank(this.lovDescCustCIF.getValue())){
			aFinanceDetail.setStageAccountingList(null);
		}else{

			//Finance Accounting Details Tab
			if (getAccountingDetailDialogCtrl() != null) {
				// check if accounting rules executed or not
				if (!getAccountingDetailDialogCtrl().isAccountingsExecuted()) {
					MessageUtil.showErrorMessage(Labels.getLabel("label_Finance_Calc_Accountings"));
					return;
				}
				if (getAccountingDetailDialogCtrl().getDisbCrSum().compareTo(getAccountingDetailDialogCtrl().getDisbDrSum()) != 0) {
					MessageUtil.showErrorMessage(Labels.getLabel("label_Finance_Acc_NotMatching"));
					return;
				}
			}
		}

		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

		String tranType = "";
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinanceMain.getRecordType())) {
				aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
				if (isNew) {
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceMain.setNewRecord(true);
				}
			}

		} else {
			aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
			if (doProcess(aFinanceDetail, tranType)) {
				if (getDealFinanceListCtrl() != null) {
					refreshList();
				}

				//Customer Notification for Role Identification
				String msg = PennantApplicationUtil.getSavingStatus(aFinanceMain.getRoleCode(),aFinanceMain.getNextRoleCode(), 
						aFinanceMain.getFinReference(),Labels.getLabel("label_TreasuaryFinance_Deal"), aFinanceMain.getRecordStatus());
				Clients.showNotification(msg,  "info", null, null, -1);
				
				//Mail Alert Notification for User
				if(StringUtils.isNotBlank(aFinanceMain.getNextTaskId()) && 
						!StringUtils.trimToEmpty(aFinanceMain.getNextRoleCode()).equals(aFinanceMain.getRoleCode())){
					getMailUtil().sendMail(1, NotificationConstants.TEMPLATE_FOR_AE, aFinanceMain);
				}
				
				closeDialog();
			} 

		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	// WorkFlow Creations

	private String getServiceTasks(String taskId, FinanceMain financeMain, String finishedTasks) {
		logger.debug("Entering");

		String serviceTasks = getServiceOperations(taskId, financeMain);

		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	private void setNextTaskDetails(String taskId, FinanceMain financeMain) {
		logger.debug("Entering");

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(financeMain.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, financeMain);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";

		if ("".equals(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRoleCode += getTaskOwner(nextTasks[i]);
				}
			}
		}

		financeMain.setTaskId(taskId);
		financeMain.setNextTaskId(nextTaskId);
		financeMain.setRoleCode(getRole());
		financeMain.setNextRoleCode(nextRoleCode);

		logger.debug("Leaving");
	}


	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 */
	private boolean doProcess(FinanceDetail aFinanceDetail, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		afinanceMain.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());

		aFinanceDetail.getFinScheduleData().setFinanceMain(afinanceMain);
		aFinanceDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			afinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, afinanceMain, finishedTasks);

			if (isNotesMandatory(taskId, afinanceMain)) {
				try {
					if (!notesEntered) {
						MessageUtil.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				} catch (InterruptedException e) {
					logger.error("Exception: ", e);
				}
			}

			auditHeader = getAuditHeader(aFinanceDetail, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doDedup)) {

				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doBlacklist)) {

				} else if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_CheckLimits)) {

				} else if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckExceptions)) {

				} else if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_doSendNotification)) {


				} else {

					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";"); 
				FinanceDetail tFinanceDetail=  (FinanceDetail) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain(), finishedTasks);

			}

			FinanceDetail tFinanceDetail=  (FinanceDetail) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {
				if (!"".equals(nextTaskId)|| "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());
					doProcess_Assets(tFinanceDetail);
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {
			doProcess_Assets(aFinanceDetail);
			auditHeader = getAuditHeader(aFinanceDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);

		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinanceDetail afinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getTreasuaryFinanceService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getTreasuaryFinanceService().saveOrUpdateDeal(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getTreasuaryFinanceService().doApprove(auditHeader);

						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getTreasuaryFinanceService().doReject(auditHeader);
						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_InvestmentDealDialogCtrl, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_InvestmentDealDialogCtrl, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.financeDetail.getFinScheduleData().getFinanceMain()), true);
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
		} catch (PFFInterfaceException e) {
			logger.error("Exception: ", e);
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	private void refreshList() {
		dealFinanceListCtrl.search();

	}

	/**
	 * Method to invoke data filling method for eligibility tab, Scoring tab,
	 * fee charges tab, accounting tab, agreements tab and additional field
	 * details tab.
	 * 
	 * @param aFinanceDetail
	 * @throws ParseException 
	 * @throws InterruptedException 
	 * 
	 */
	public void doFillTabs(FinanceDetail aFinanceDetail) throws ParseException, InterruptedException {
		logger.debug("Entering");
		
		if (aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails() == null
				|| aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().isEmpty()) {
			buildSchedule(aFinanceDetail);
		}
		
		setFinanceDetail(aFinanceDetail);
		getTreasuaryFinanceService().setFinanceDetails(aFinanceDetail, "COMIDITY", getRole());
		appendAssetDetailTab();
		getTreasuaryFinanceService().setFinanceDetails(aFinanceDetail, "CHECKLIST", getRole());
		appendCheckListDetailTab(true);
		appendRecommendDetailTab();
		appendDocumentDetailTab();
		appendtAgreementsDetailTab(true);
		appendFeeDetailsTab();
		appendAccountingDetailTab(true);
		logger.debug("Leaving");
	}

	/**
	 * Method for Executing Eligibility Details
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	public void onExecuteAccountingDetail(Boolean onLoadProcess) throws InterruptedException, AccountNotFoundException, IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");

		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			this.finAmount.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.lovDescCustCIF.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.finBranch.getValue();
			this.lovDescFinBranchName.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.disbAcctId.getValue();
			if(StringUtils.isNotBlank(this.disbAcctId.getValue())){
				this.repayAcctId.clearErrorMessage();
			}
			if(StringUtils.isBlank(this.repayAcctId.getValue())){
				this.repayAcctId.setValue(this.disbAcctId.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		getAccountingDetailDialogCtrl().getLabel_AccountingDisbCrVal().setValue("");
		getAccountingDetailDialogCtrl().getLabel_AccountingDisbDrVal().setValue("");		
		showErrorDetails(wve, financeTypeDetailsTab);

		//Finance Accounting Details Execution
		executeAccounting(onLoadProcess);
		logger.debug("Leaving");
	} 

	/**
	 * Method for Executing Accounting tab Rules
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 * @throws InterruptedException 
	 * 
	 */
	private void executeAccounting(boolean onLoadProcess) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException, InterruptedException{
		logger.debug("Entering");
		
		List<FinanceScheduleDetail> financeScheduleDetails;
		Map<String, FeeRule> feeRuleDetailsMap = null;

		doWriteComponentsToBean(getFinanceDetail());
		FinScheduleData finScheduleData = getFinanceDetail().getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		financeScheduleDetails 	= finScheduleData.getFinanceScheduleDetails();

		if(getFeeDetailDialogCtrl() != null) {
			feeRuleDetailsMap = getFeeDetailDialogCtrl().getFeeRuleDetailsMap();
		}

		DataSet dataSet = AEAmounts.createDataSet(financeMain, eventCode, financeMain.getFinStartDate(), financeMain.getFinStartDate());
		Date curBDay = DateUtility.getAppDate();
		amountCodes = AEAmounts.procAEAmounts(financeMain, financeScheduleDetails, new FinanceProfitDetail(), curBDay);

		setAmountCodes(amountCodes);

		List<ReturnDataSet> accountingSetEntries = new ArrayList<ReturnDataSet>();
		try {
			accountingSetEntries = getEngineExecution().getAccEngineExecResults(dataSet,  getAmountCodes(), "N", feeRuleDetailsMap ,false, finScheduleData.getFinanceType());
			
		}catch (Exception e) {
			logger.error("Exception: ", e);
		}

		getFinanceDetail().setReturnDataSetList(accountingSetEntries);
		getAccountingDetailDialogCtrl().doFillAccounting(accountingSetEntries);

		logger.debug("Leaving");
	}

	/**
	 * Method to show error details if occurred
	 * 
	 **/
	public void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			doRemoveValidation();
			
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	// WorkFlow Components
	
	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, 
				auditDetail, afinanceDetail.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_InvestmentDealDialogCtrl, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.financeDetail.getFinScheduleData().getFinanceMain());
	}

	
	
	@Override
	protected String getReference() {
		return String.valueOf(this.financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
	}

	
	
	/** To pass Data For Agreement Child Windows
	 * Used in reflection
	 * @return
	 * @throws Exception 
	 */
	public FinanceDetail getAgrFinanceDetails() throws Exception {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		Cloner cloner = new Cloner();
		aFinanceDetail = cloner.deepClone(getFinanceDetail());
		
		boolean isNew = false;
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		doWriteComponentsToBean(aFinanceDetail);
		
		String tempRecordStatus = aFinanceMain.getRecordType();
		isNew = aFinanceDetail.isNew();
		
		//Finance Asset Loan Details Tab
		if (childWindow != null) {
			doSave_Assets(aFinanceDetail, isNew, tempRecordStatus,true);
		}
		
		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here
		
		//Finance CheckList Details Tab
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("financeDetail", aFinanceDetail);
		map.put("agreement", true);
		map.put("userAction", this.userAction.getSelectedItem().getLabel());
		try {
			Events.sendEvent("onChkListValidation", checkListChildWindow, map);
		} catch (Exception e) {
			if (e instanceof WrongValuesException) {
				throw e;
			}
		}

		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
		logger.debug("Leaving");
		return aFinanceDetail;
	}
	
	public FinanceMain getFinanceMain(){
		FinanceMain financeMain=new FinanceMain();
		financeMain.setFinReference(StringUtils.trimToEmpty(this.dealTcktRef.getValue()));
		financeMain.setCustID(this.custID.longValue());
		financeMain.setLovDescCustCIF(this.lovDescCustCIF.getValue());
		financeMain.setLovDescCustShrtName(this.custShrtName.getValue());
		financeMain.setFinCcy(this.finCcy.getValue());
		financeMain.setFinAmount(PennantAppUtil.unFormateAmount(this.finAmount.getValue(),
				CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy())));
		financeMain.setFinStartDate(this.startDate.getValue());
		return financeMain;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public void setFeeDetailDialogCtrl(FeeDetailDialogCtrl feeDetailDialogCtrl) {
		this.feeDetailDialogCtrl = feeDetailDialogCtrl;

		if (this.feeDetailDialogCtrl != null) {
			try {
				this.feeDetailDialogCtrl.doExecuteFeeCharges(true, false,
						getFinanceDetail().getFinScheduleData(), true,
						this.startDate.getValue());
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		}
	}
	public FeeDetailDialogCtrl getFeeDetailDialogCtrl() {
		return feeDetailDialogCtrl;
	}

	public InvestmentFinHeader getInvestmentFinHeader() {
		return investmentFinHeader;
	}
	public void setInvestmentFinHeader(InvestmentFinHeader investmentFinHeader) {
		this.investmentFinHeader = investmentFinHeader;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isValidationOn() {
		return validationOn;
	}
	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public void setAccountingDetailDialogCtrl(AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}
	public AccountingDetailDialogCtrl getAccountingDetailDialogCtrl() {
		return accountingDetailDialogCtrl;
	}

	public AEAmountCodes getAmountCodes() {
		return amountCodes;
	}
	public void setAmountCodes(AEAmountCodes amountCodes) {
		this.amountCodes = amountCodes;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}
	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public void setDealFinanceListCtrl(DealFinanceListCtrl dealFinanceListCtrl) {
		this.dealFinanceListCtrl = dealFinanceListCtrl;
	}
	public DealFinanceListCtrl getDealFinanceListCtrl() {
		return dealFinanceListCtrl;
	}

}