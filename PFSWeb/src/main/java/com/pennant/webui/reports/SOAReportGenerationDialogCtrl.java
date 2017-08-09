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
 * FileName    		:  SOAReportGenerationDialogCtrl.java                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-09-2012   														*
 *                                                                  						*
 * Modified Date    :  23-09-2012      														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-09-2012         Pennant	                 0.1                                        * 
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.payment.PaymentInstruction;
import com.pennant.backend.model.systemmasters.SOASummaryReport;
import com.pennant.backend.model.systemmasters.SOATransactionReport;
import com.pennant.backend.model.systemmasters.StatementOfAccount;
import com.pennant.backend.service.reports.SOAReportGenerationService;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * This is the controller class for the /WEB-INF/pages/reports/ReportGenerationPromptDialog.zul file.
 */
public class SOAReportGenerationDialogCtrl extends GFCBaseCtrl<StatementOfAccount> {
	private static final long serialVersionUID = 4678287540046204660L;
	private final static Logger logger = Logger.getLogger(SOAReportGenerationDialogCtrl.class);

	protected Window window_SOAReportGenerationDialogCtrl;
	protected ExtendedCombobox	finReference;
	protected Datebox	startDate;
	protected Datebox	endDate;
	
	private StatementOfAccount statementOfAccount = new StatementOfAccount();
	private transient SOAReportGenerationService soaReportGenerationService;	

	public SOAReportGenerationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * On creating Window
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SOAReportGenerationDialogCtrl(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SOAReportGenerationDialogCtrl);

		try {
			doSetFieldProperties();
			this.window_SOAReportGenerationDialogCtrl.doModal();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showError(Labels.getLabel("label_ReportConfiguredError.error"));
			closeDialog();
		}

		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		// Finance Reference
		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDisplayStyle(2);
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finReference.setMandatoryStyle(true);
		this.finReference.setMaxlength(LengthConstants.LEN_REF);
		this.finReference.setTextBoxWidth(143);

		logger.debug("Leaving");
	}
	
	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		
		//Close the current window
		this.window_SOAReportGenerationDialogCtrl.onClose();
		
		//Close the current menu item
		final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");  
		final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter").getFellow("tabBoxIndexCenter");
		tabbox.getSelectedTab().close();
		
		logger.debug(Literal.LEAVING);
	}
	
	public void onClick$btnGenereate(Event event) {
		logger.debug(Literal.ENTERING);
		
		doSetValidation();
		
		doWriteComponentsToBean(this.statementOfAccount);
		
		doShowDialogPage(this.statementOfAccount);
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aStatementOfAccount
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(StatementOfAccount aStatementOfAccount) {
		logger.debug("Entering");

		Map<String, Object> aruments = new HashMap<String, Object>();

		aruments.put("statementOfAccount", aStatementOfAccount);
		aruments.put("moduleCode", moduleCode);
		aruments.put("enqiryModule", enqiryModule);

		try {
			//Executions.createComponents("/WEB-INF/pages/SolutionFactory/Promotion/PromotionDialog.zul", null, aruments);
			//this.window_SOAReportGenerationDialogCtrl.onClose();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}
	
	
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param statementOfAccount
	 */
	public void doWriteComponentsToBean(StatementOfAccount statementOfAccount) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		String finReference = "";
		Date startDate = null;
		Date endDate = null;

		// FinReference
		try {
			finReference = this.finReference.getValue();
			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Start Date
		try {
			startDate = this.startDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// End Date
		try {
			endDate = this.endDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		doRemoveValidation();

		if (wve.isEmpty()) {
			statementOfAccount.setFinReference(finReference);
			statementOfAccount = this.soaReportGenerationService.getSOALoanDetails(finReference);
			FinanceProfitDetail financeProfitDetail = this.soaReportGenerationService.getFinanceProfitDetails(finReference);
			int activeCount;
			int closeCount;
			long custId;
			if (financeProfitDetail != null) {
				custId = financeProfitDetail.getCustId();
				activeCount = this.soaReportGenerationService.getFinanceProfitDetailActiveCount(custId, true);
				closeCount = this.soaReportGenerationService.getFinanceProfitDetailActiveCount(custId, false);
				statementOfAccount.setCustID(custId);
				statementOfAccount.setActiveCnt(activeCount);
				statementOfAccount.setCloseCnt(closeCount);
				statementOfAccount.setTot(activeCount + closeCount);

				StatementOfAccount statementOfAccountCustDetails = this.soaReportGenerationService
						.getSOACustomerDetails(custId);
				if (statementOfAccountCustDetails != null) {
					statementOfAccount.setCustShrtName(statementOfAccountCustDetails.getCustShrtName());
					statementOfAccount.setCustCIF(statementOfAccountCustDetails.getCustCIF());
					statementOfAccount.setCustAddrHNbr(statementOfAccountCustDetails.getCustAddrHNbr());
					statementOfAccount.setCustFlatNbr(statementOfAccountCustDetails.getCustFlatNbr());
					statementOfAccount.setCustAddrStreet(statementOfAccountCustDetails.getCustAddrStreet());
					statementOfAccount.setCustPOBox(statementOfAccountCustDetails.getCustPOBox());
					statementOfAccount.setCustAddrCity(statementOfAccountCustDetails.getCustAddrCity());
					statementOfAccount.setCustAddrProvince(statementOfAccountCustDetails.getCustAddrProvince());
					statementOfAccount.setCustAddrCountry(statementOfAccountCustDetails.getCustAddrCountry());
					statementOfAccount.setPhoneCountryCode(statementOfAccountCustDetails.getPhoneCountryCode());
					statementOfAccount.setPhoneAreaCode(statementOfAccountCustDetails.getPhoneAreaCode());
					statementOfAccount.setPhoneNumber(statementOfAccountCustDetails.getPhoneNumber());
					statementOfAccount.setCustEMail(statementOfAccountCustDetails.getCustEMail());
				}

				StatementOfAccount statementOfAccountProductDetails = this.soaReportGenerationService
						.getSOAProductDetails(financeProfitDetail.getFinBranch(), financeProfitDetail.getFinType());
				if (statementOfAccountProductDetails != null) {
					statementOfAccount.setFinType(statementOfAccountProductDetails.getFinType());
					statementOfAccount.setFinBranch(statementOfAccountProductDetails.getFinBranch());
				}
			}
			
			statementOfAccount.setStartDate(startDate);
			statementOfAccount.setEndDate(endDate);
			
			statementOfAccount.setSoaSummaryReports(getSOASummaryDetails(finReference));
			statementOfAccount.setTransactionReports(getTransactionDetails(finReference));
		} else {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}
	
	/**
	 * to get the Report Transaction Details
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public List<SOASummaryReport> getSOASummaryDetails(String finReference) {
		logger.debug("Entering");
		
		SOASummaryReport soaSummaryReport = null;
		List<SOASummaryReport> soaSummaryReportsList = new ArrayList<SOASummaryReport>();
		
		FinanceMain finMain = this.soaReportGenerationService.getFinanceMain(finReference);
		
		if (finMain != null) {
			List<FinanceScheduleDetail> finSchdDetList = this.soaReportGenerationService.getFinScheduleDetails(finReference);
			FinanceProfitDetail financeProfitDetail = this.soaReportGenerationService.getFinanceProfitDetails(finReference);
			List<ManualAdvise>  manualAdviseList = this.soaReportGenerationService.getManualAdvise(finReference);
			SOASummaryReport  soaSummaryReportOfExcessAmount = this.soaReportGenerationService.getFinExcessAmountOfSummaryReport(finReference);

			BigDecimal due = BigDecimal.ZERO;
			BigDecimal receipt = BigDecimal.ZERO;
			BigDecimal overDue = BigDecimal.ZERO;

			BigDecimal totalProfitSchd = BigDecimal.ZERO;
			BigDecimal totalPrincipalSchd = BigDecimal.ZERO;
			BigDecimal totalFeeschd = BigDecimal.ZERO;
			BigDecimal totalSchdPriPaid = BigDecimal.ZERO;
			BigDecimal totalSchdPftPaid = BigDecimal.ZERO;
			BigDecimal totalSchdfeepaid = BigDecimal.ZERO;

			for (FinanceScheduleDetail finSchdDetail : finSchdDetList) {
				if ((DateUtility.compare(finSchdDetail.getSchDate(), DateUtility.getAppDate()) <= 0)) {
					totalProfitSchd = totalProfitSchd.add(finSchdDetail.getProfitSchd());
					totalPrincipalSchd = totalPrincipalSchd.add(finSchdDetail.getPrincipalSchd());
					totalFeeschd = totalFeeschd.add(finSchdDetail.getFeeSchd());
					totalSchdPriPaid = totalSchdPriPaid.add(finSchdDetail.getSchdPriPaid());
					totalSchdPftPaid = totalSchdPftPaid.add(finSchdDetail.getSchdPftPaid());
					totalSchdfeepaid = totalSchdfeepaid.add(finSchdDetail.getSchdFeePaid());
				}
			}
			
			due = totalProfitSchd.add(totalPrincipalSchd).add(totalFeeschd);
			receipt = totalSchdPriPaid.add(totalSchdPftPaid).add(totalSchdfeepaid);

			if (due.compareTo(receipt) > 0) {
				overDue = due.subtract(receipt);
			} else {
				overDue = BigDecimal.ZERO;
			}

			soaSummaryReport = new SOASummaryReport();
			soaSummaryReport.setComponent("Instalment Amount");
			soaSummaryReport.setDue(due);
			soaSummaryReport.setReceipt(receipt);
			soaSummaryReport.setOverDue(overDue);
			soaSummaryReportsList.add(soaSummaryReport);

			due = totalPrincipalSchd;
			receipt = totalSchdPriPaid;
			if (due.compareTo(receipt) > 0) {
				overDue = due.subtract(receipt);
			} else {
				overDue = BigDecimal.ZERO;
			}

			soaSummaryReport = new SOASummaryReport();
			soaSummaryReport.setComponent("Principal Component");
			soaSummaryReport.setDue(due);
			soaSummaryReport.setReceipt(receipt);
			soaSummaryReport.setOverDue(overDue);
			soaSummaryReportsList.add(soaSummaryReport);

			due = totalProfitSchd;
			receipt = totalSchdPftPaid;
			if (due.compareTo(receipt) > 0) {
				overDue = due.subtract(receipt);
			} else {
				overDue = BigDecimal.ZERO;
			}

			soaSummaryReport = new SOASummaryReport();
			soaSummaryReport.setComponent("Interest Component");
			soaSummaryReport.setDue(due);
			soaSummaryReport.setReceipt(receipt);
			soaSummaryReport.setOverDue(overDue);
			soaSummaryReportsList.add(soaSummaryReport);

			if (financeProfitDetail.getPenaltyDue() == null) {
				due = BigDecimal.ZERO;
			} else {
				due = financeProfitDetail.getPenaltyDue();
			}
			
			if (financeProfitDetail.getPenaltyPaid() == null) {
				receipt = BigDecimal.ZERO;
			} else {
				receipt = financeProfitDetail.getPenaltyPaid();
			}
			
			if (due.compareTo(receipt) > 0) {
				overDue = due.subtract(receipt);
			} else {
				overDue = BigDecimal.ZERO;
			}
			soaSummaryReport = new SOASummaryReport();
			soaSummaryReport.setComponent("Late Payment Penalty");
			soaSummaryReport.setDue(due);
			soaSummaryReport.setReceipt(receipt);
			soaSummaryReport.setOverDue(overDue);
			soaSummaryReportsList.add(soaSummaryReport);
			
			BigDecimal adviseBalanceAmt = BigDecimal.ZERO;
			BigDecimal bounceZeroAdviseAmount = BigDecimal.ZERO;
			BigDecimal bounceGreaterZeroAdviseAmount = BigDecimal.ZERO;
			BigDecimal bounceZeroPaidAmount = BigDecimal.ZERO;
			BigDecimal bounceGreaterZeroPaidAmount = BigDecimal.ZERO;
			
			for (ManualAdvise manualAdvise : manualAdviseList) {
				if (manualAdvise.getAdviseType() == 2) {
					if (manualAdvise.getBalanceAmt() != null) {
						adviseBalanceAmt = adviseBalanceAmt.add(manualAdvise.getBalanceAmt());
					}
				}  
				
				if (manualAdvise.getAdviseType() == 1 && manualAdvise.getBounceID() == 0) {
					if (manualAdvise.getAdviseAmount() != null) {
						bounceZeroAdviseAmount = bounceZeroAdviseAmount.add(manualAdvise.getAdviseAmount());
					}
					
					if (manualAdvise.getPaidAmount() != null) {
						bounceZeroPaidAmount = bounceZeroPaidAmount.add(manualAdvise.getPaidAmount());
					}
				} 
				
				if (manualAdvise.getBounceID() > 0) {
					if (manualAdvise.getAdviseAmount() != null) {
						bounceGreaterZeroAdviseAmount = bounceGreaterZeroAdviseAmount.add(manualAdvise.getAdviseAmount());
					}
					
					if (manualAdvise.getPaidAmount() != null) {
						bounceGreaterZeroPaidAmount = bounceGreaterZeroPaidAmount.add(manualAdvise.getPaidAmount());
					}
				}
			}
			
			due = bounceGreaterZeroAdviseAmount;
			receipt = bounceGreaterZeroPaidAmount;
			
			if (due.compareTo(receipt) > 0) {
				overDue = due.subtract(receipt);
			} else {
				overDue = BigDecimal.ZERO;
			}
			soaSummaryReport = new SOASummaryReport();
			soaSummaryReport.setComponent("Bounce Charges");
			soaSummaryReport.setDue(due);
			soaSummaryReport.setReceipt(receipt);
			soaSummaryReport.setOverDue(overDue);
			soaSummaryReportsList.add(soaSummaryReport);
			
			due = bounceZeroAdviseAmount;
			receipt = bounceZeroPaidAmount;
			
			if (due.compareTo(receipt) > 0) {
				overDue = due.subtract(receipt);
			} else {
				overDue = BigDecimal.ZERO;
			}
			soaSummaryReport = new SOASummaryReport();
			soaSummaryReport.setComponent("Other Receivables");
			soaSummaryReport.setDue(due);
			soaSummaryReport.setReceipt(receipt);
			soaSummaryReport.setOverDue(overDue);
			soaSummaryReportsList.add(soaSummaryReport);
			
			due = adviseBalanceAmt;
			receipt = BigDecimal.ZERO;
			overDue = due.subtract(receipt);
			
			soaSummaryReport = new SOASummaryReport();
			soaSummaryReport.setComponent("Other Payables");
			soaSummaryReport.setDue(due);
			soaSummaryReport.setReceipt(receipt);
			soaSummaryReport.setOverDue(overDue);
			soaSummaryReportsList.add(soaSummaryReport);
			
			soaSummaryReportsList.add(soaSummaryReportOfExcessAmount);
			
			System.out.println();
			for (SOASummaryReport soaSummary : soaSummaryReportsList) {
				System.out.println();
				System.out.println();
				System.out.print(soaSummary.getComponent());
				System.out.print("		" + soaSummary.getDue());
				System.out.print("		" + soaSummary.getReceipt());
				System.out.print("		" + soaSummary.getOverDue());
			}
			System.out.println();
		}
		
		logger.debug("Leaving");
		
		return soaSummaryReportsList;
	}
	
	/**
	 * to get the Report Transaction Details
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public List<SOATransactionReport> getTransactionDetails(String finReference) {
		logger.debug("Entering");
		
		String brokenPeriodEvent = "BKNPRD";
		String advancePayment = "ADVPAY";
		String sattelment = "STLMNT";
		String penality = "PANLTY";
		
		SOATransactionReport soaTransactionReport = null;
		List<SOATransactionReport> soaTransactionReports = new ArrayList<SOATransactionReport>();
		
		FinanceMain finMain = this.soaReportGenerationService.getFinanceMain(finReference);
		
		if (finMain != null) {
			List<FinanceScheduleDetail>  finSchdDetList = this.soaReportGenerationService.getFinScheduleDetails(finReference);
			List<FinAdvancePayments>  finAdvancePaymentsList = this.soaReportGenerationService.getFinAdvancePayments(finReference);
			List<PaymentInstruction>  paymentInstructionsList = this.soaReportGenerationService.getPaymentInstructions(finReference);
			List<FinODDetails>  finODDetailsList = this.soaReportGenerationService.getFinODDetails(finReference);
			List<ManualAdvise>  manualAdviseList = this.soaReportGenerationService.getManualAdvise(finReference);
			List<SOATransactionReport> soaFinFeeScheduleReports = this.soaReportGenerationService.getFinFeeScheduleDetails(finReference);
			
			for (FinanceScheduleDetail finSchdDetail : finSchdDetList) {
				
				if ((DateUtility.compare(finSchdDetail.getSchDate(), DateUtility.getAppDate()) <= 0)) {
					
					if (finSchdDetail.getPartialPaidAmt().compareTo(BigDecimal.ZERO) > 0) {
						
						soaTransactionReport = new SOATransactionReport();
						soaTransactionReport.setEvent(AccountEventConstants.ACCEVENT_EARLYPAY);
						soaTransactionReport.setTransactionDate(finSchdDetail.getSchDate());
						soaTransactionReport.setTransactionAmount(finSchdDetail.getRepayAmount());
						soaTransactionReport.setDrOrCr("Debit");
						
						soaTransactionReports.add(soaTransactionReport);
					}
					
					if (StringUtils.isBlank(finMain.getClosingStatus())
							|| !StringUtils.equalsIgnoreCase(finMain.getClosingStatus(), "C")) {
						
						if (StringUtils.isBlank(finSchdDetail.getBpiOrHoliday())
								|| !StringUtils.equalsIgnoreCase(finSchdDetail.getBpiOrHoliday(), "H")
								|| !StringUtils.equalsIgnoreCase(finSchdDetail.getBpiOrHoliday(), "B")) {
							
							if (finSchdDetail.isDisbOnSchDate()) {
								soaTransactionReport = new SOATransactionReport();
								soaTransactionReport.setEvent(AccountEventConstants.ACCEVENT_ADDDBSP);
								soaTransactionReport.setTransactionDate(finSchdDetail.getSchDate());
								soaTransactionReport.setTransactionAmount(finSchdDetail.getDisbAmount());
								soaTransactionReport.setDrOrCr("Credit");
								soaTransactionReports.add(soaTransactionReport);
							}
						}
						
						if (finSchdDetail.getRepayAmount().compareTo(BigDecimal.ZERO) > 0
								&& StringUtils.equalsIgnoreCase(finSchdDetail.getBpiOrHoliday(), "B")) {
							soaTransactionReport = new SOATransactionReport();
							soaTransactionReport.setEvent(brokenPeriodEvent);
							soaTransactionReport.setTransactionDate(finSchdDetail.getSchDate());
							soaTransactionReport.setTransactionAmount(finSchdDetail.getRepayAmount());
							soaTransactionReport.setDrOrCr("Debit");
							
							soaTransactionReports.add(soaTransactionReport);
						}
					}
					
				}
			}
			
			for (FinAdvancePayments finAdvancePayments : finAdvancePaymentsList) {
				soaTransactionReport = new SOATransactionReport();
				
				soaTransactionReport.setEvent(advancePayment);
				soaTransactionReport.setTransactionDate(finAdvancePayments.getLlDate());
				soaTransactionReport.setTransactionAmount(finAdvancePayments.getAmtToBeReleased());
				soaTransactionReport.setDrOrCr("Debit");
				
				soaTransactionReports.add(soaTransactionReport);
			}
			
			for (PaymentInstruction paymentInstruction : paymentInstructionsList) {
				soaTransactionReport = new SOATransactionReport();
				
				soaTransactionReport.setEvent(sattelment);
				soaTransactionReport.setTransactionDate(paymentInstruction.getPostDate());
				soaTransactionReport.setTransactionAmount(paymentInstruction.getPaymentAmount());
				soaTransactionReport.setDrOrCr("Debit");
				
				soaTransactionReports.add(soaTransactionReport);
			}
			
			if (StringUtils.isBlank(finMain.getClosingStatus())
					|| !StringUtils.equalsIgnoreCase(finMain.getClosingStatus(), "C")) {
				
				for (FinODDetails finODDetails : finODDetailsList) {
					soaTransactionReport = new SOATransactionReport();
					
					soaTransactionReport.setEvent(penality);
					soaTransactionReport.setTransactionDate(finODDetails.getFinODSchdDate());
					soaTransactionReport.setTransactionAmount(finODDetails.getTotPenaltyAmt());
					soaTransactionReport.setDrOrCr("Debit");
					
					soaTransactionReports.add(soaTransactionReport);
				}
			}
			
			//FINFeeScheduleDetails
			soaTransactionReports.addAll(soaFinFeeScheduleReports);
			
			for (ManualAdvise manualAdvise : manualAdviseList) {
				
				if (manualAdvise.getAdviseType() != 2 && manualAdvise.getAdviseAmount().compareTo(BigDecimal.ZERO) > 0) {
					
				}
				
				if (manualAdvise.getFeeTypeID() != 0 && manualAdvise.getFeeTypeID() != Long.MIN_VALUE) {
					
				}
				
			}
			System.out.println();
			for (SOATransactionReport tranReport : soaTransactionReports) {
				System.out.println();
				System.out.println();
				System.out.print(tranReport.getTransactionDate());
				System.out.print("		" + tranReport.getEvent());
				System.out.print("		" + tranReport.getTransactionAmount());
				System.out.print("		" + tranReport.getDrOrCr());
			}
			System.out.println();
		}
		
		logger.debug("Leaving");
		
		return soaTransactionReports;
	}
	
	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		this.finReference.setConstraint("");
		this.startDate.setConstraint("");
		this.endDate.setConstraint("");

		logger.debug("Leaving");
	}
	
	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		this.finReference.setErrorMessage("");
		this.startDate.setErrorMessage("");
		this.endDate.setErrorMessage("");

		logger.debug("Leaving");
	}
	
	
	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		
		doClearMessage();
		doRemoveValidation();
		
		//Finance Type
		this.finReference.setConstraint(new PTStringValidator(Labels.getLabel("label_SOAReportDialog_FinReference.value"), null, true, true));
		
		//Date appStartDate = DateUtility.getAppDate();
		Date appEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
		// Start Date
		if (!this.startDate.isDisabled()) {
			//this.startDate.setConstraint(new PTDateValidator(Labels.getLabel("label_SOAReportDialog_StartDate.value"), true, appStartDate, appEndDate, true));
			this.startDate.setConstraint(new PTDateValidator(Labels.getLabel("label_SOAReportDialog_StartDate.value"), true));
		}
		// end Date
		if (!this.endDate.isDisabled()) {
			try {
				this.startDate.getValue();
				this.endDate.setConstraint(new PTDateValidator(Labels.getLabel("label_SOAReportDialog_EndDate.value"),
						true, this.startDate.getValue(), appEndDate, false));
			} catch (WrongValueException we) {
				this.endDate.setConstraint(new PTDateValidator(Labels.getLabel("label_SOAReportDialog_EndDate.value"),
						true, true, null, false));
			}
		}
		
		logger.debug("Leaving");
	}

	public StatementOfAccount getStatementOfAccount() {
		return statementOfAccount;
	}

	public void setStatementOfAccount(StatementOfAccount statementOfAccount) {
		this.statementOfAccount = statementOfAccount;
	}

	public void setSoaReportGenerationService(SOAReportGenerationService soaReportGenerationService) {
		this.soaReportGenerationService = soaReportGenerationService;
	}
}