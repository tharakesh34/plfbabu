package com.pennant.mq.process.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.CoreBankAccountDetail;
import com.pennant.coreinterface.model.CoreBankAccountPosting;
import com.pennant.coreinterface.model.accountposting.AccountPostingDetail;
import com.pennant.coreinterface.model.accountposting.SecondaryDebitAccount;
import com.pennant.coreinterface.process.AccountDetailProcess;
import com.pennant.coreinterface.process.AccountPostingProcess;
import com.pennant.mq.processutil.AccountPostingDetailProcess;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennanttech.pennapps.core.InterfaceException;

public class AccountPostingServiceImpl implements AccountPostingProcess {
	private static Logger logger = Logger.getLogger(AccountPostingServiceImpl.class);

	private static final String POSTSTATUS_SUCCESS = "S";
	private static final String POSTSTATUS_REVERSE = "R";
	private static final String POSTSTATUS_NOTPOSTED = "N";
	private static final String POSTSTATUS_TOBECANCELLED = "C";
	private static final String POSTSTATUS_FAILED = "F";
	private static final String SYSTEM_T24 = "T";
	
	private static final String RETURNCODE_SUCCESS = "0000";
	private static final String RETURNCODE_FAILED = "0001";
	
	
	private AccountPostingDetailProcess accPostingProcess;
	private AccountDetailProcess accountDetailProcess;
	
	public AccountPostingServiceImpl() {
		//
	}
	
	/**
	 * Method for Account postings
	 * @param coreAcct
	 * @return
	 * @throws AccountNotFoundException
	 */
	@Override
	public List<CoreBankAccountPosting> doPostings(List<CoreBankAccountPosting> accountPostings,String postBranch,
			String createNow) throws InterfaceException {
		logger.debug("Entering");
		List<CoreBankAccountPosting> coreBankAccountPostings = new ArrayList<>();
		coreBankAccountPostings.addAll(accountPostings);
		boolean isError = false;
		
		// Method to check the Sum of Debit and Credit Amount
	    isError = validateCrandDrAmount(coreBankAccountPostings);
		
		if(isError){
			throw new InterfaceException(RETURNCODE_FAILED, "Credit and Debits not matching."); 		
		}
		
		//Validate the Core Accounts 
		isError = validateAccount(coreBankAccountPostings);

 		if(isError){
			return coreBankAccountPostings;
		}
 		
 		//Call the interface API to post the transactions 
		isError = doSetAccountPostingRequest(coreBankAccountPostings);
		
		//Reverse the transactions in case of error in postings
		if(isError){
			doReversalPostings(coreBankAccountPostings, postBranch, createNow);
			return coreBankAccountPostings;
		}
		
		// If no Errors set posting status as success for all the non core transactions
		for (CoreBankAccountPosting coreBankAccountPosting : coreBankAccountPostings) {
			if(!(StringUtils.equals(coreBankAccountPosting.getPostToSys(), SYSTEM_T24))){
				coreBankAccountPosting.setPostingStatus(POSTSTATUS_SUCCESS,RETURNCODE_SUCCESS, "");
			}
		}
		
		logger.debug("Leaving");
		return coreBankAccountPostings;
	}
	
	/**
	 * Validate the Credit and Debit Amount for total Accounting 
	 * @param coreBankAccountPosting
	 * @return
	 */
	public boolean validateCrandDrAmount(List<CoreBankAccountPosting> coreBankAccountPosting){
		logger.debug("Entering");
		BigDecimal creditPostAmount =BigDecimal.ZERO;
		BigDecimal debitPostAmount = BigDecimal.ZERO;
		for(CoreBankAccountPosting coreBankPosting:coreBankAccountPosting){
			if(coreBankPosting.getDerivedTranOrder()==0){
				if(StringUtils.equals(coreBankPosting.getDrOrCr(), "C")){
				creditPostAmount = coreBankPosting.getPostAmount().add(creditPostAmount);
				}else{
					debitPostAmount = coreBankPosting.getPostAmount().add(debitPostAmount);
				}
			}
		}
		if(creditPostAmount.compareTo(debitPostAmount)==0){
			return false;
		}else{
			return true;
		}
		
	}
	
	/**
	 * Validate the Account Details
	 * @param coreBankAccountPosting
	 * @return
	 * @throws InterfaceException
	 */
	public boolean validateAccount(List<CoreBankAccountPosting> coreBankAccountPosting) throws InterfaceException{
		logger.debug("Entering");
 
		boolean iserror = false;
		List<String> accountList = new ArrayList<>();
		String accountNo;
		CoreBankAccountDetail coreBankAccountDetail = new CoreBankAccountDetail();
		for(CoreBankAccountPosting coreBankPosting: coreBankAccountPosting){
			if(StringUtils.equals(coreBankPosting.getPostToSys(), SYSTEM_T24)){
				if(!accountList.contains(coreBankPosting.getAccount())){
					accountNo =coreBankPosting.getAccount();
					accountList.add(accountNo);
					coreBankAccountDetail.setAccountNumber(accountNo);
					coreBankAccountDetail= getAccountDetailProcess().fetchAccountAvailableBal(coreBankAccountDetail);
					if(!StringUtils.equals(coreBankAccountDetail.getErrorCode(), RETURNCODE_SUCCESS)){
						coreBankPosting.setPostingStatus(POSTSTATUS_FAILED, coreBankAccountDetail.getErrorCode(), coreBankAccountDetail.getErrorMessage());
						iserror = true;
					}else if(StringUtils.equals(coreBankPosting.getDrOrCr(), "D") && (coreBankPosting.getDerivedTranOrder()==0)){ 
						if(coreBankAccountDetail.getAcBal().compareTo(coreBankPosting.getPostAmount())<0){
							coreBankPosting.setPostingStatus(POSTSTATUS_FAILED, RETURNCODE_FAILED, "Insufficient Amount");
							iserror = true;
						} 
					}
				}
			}
		}
		logger.debug("Leaving");
		return iserror;
	}

	
	/**
	 * Call interface postings module to post the accounting transactions 
	 * @param coreBankAccountPosting
	 * @return
	 * @throws InterfaceException
	 */
	public boolean doSetAccountPostingRequest(List<CoreBankAccountPosting> coreBankAccountPosting) throws InterfaceException{
		logger.debug("Entering");
		int i;
		boolean isError = false;
		
		for (i = 0; i < coreBankAccountPosting.size(); i = i + 2) {
			AccountPostingDetail accPostingRequest = new AccountPostingDetail();
			CoreBankAccountPosting transactionEntry1 = coreBankAccountPosting.get(i);
			CoreBankAccountPosting transactionEntry2 = coreBankAccountPosting.get(i + 1);

			//If Posting to Core Bank
			if (StringUtils.equals(transactionEntry1.getPostToSys(), SYSTEM_T24)
					&& StringUtils.equals(transactionEntry2.getPostToSys(),SYSTEM_T24)) {

				if ("C".equals(transactionEntry1.getDrOrCr())) {

					preparePostingDetails(transactionEntry1, transactionEntry2, accPostingRequest);
				} else {

					preparePostingDetails(transactionEntry2, transactionEntry1, accPostingRequest);
				}
				
				AccountPostingDetail accPostingRply = getAccPostingProcess().doFillPostingDetails(accPostingRequest, 
						InterfaceMasterConfigUtil.ACCOUNT_POSTING);

				if (!(RETURNCODE_SUCCESS.equals(accPostingRply.getReturnCode()) || "".equals(accPostingRply.getReturnCode()))) {
					transactionEntry1.setPostStatus(POSTSTATUS_FAILED);
					transactionEntry1.setPostingID(accPostingRply.getHostReferenceNum());
					transactionEntry1.setErrorId(accPostingRply.getReturnCode());
					transactionEntry2.setPostStatus(POSTSTATUS_FAILED);
					transactionEntry2.setPostingID(accPostingRply.getHostReferenceNum());
					transactionEntry2.setErrorId(accPostingRply.getReturnCode());
					isError = true;
					break; 	
				} else {
					transactionEntry1.setPostStatus(POSTSTATUS_SUCCESS);
					transactionEntry1.setPostingID(accPostingRply.getHostReferenceNum());
					transactionEntry1.setErrorId(accPostingRply.getReturnCode());
					transactionEntry2.setPostStatus(POSTSTATUS_SUCCESS);
					transactionEntry2.setPostingID(accPostingRply.getHostReferenceNum());
					transactionEntry2.setErrorId(accPostingRply.getReturnCode());
				}
			} 
		}
		logger.debug("Leaving");
		return isError;
	}	
	
	/**
	 * Prepare Posting Details
	 * 
	 * @param creditPostingDetails
	 * @param debitPostingDetails
	 * @param accPostingRequest
	 */
	public void preparePostingDetails(CoreBankAccountPosting creditPostingDetails,
			CoreBankAccountPosting debitPostingDetails, AccountPostingDetail accPostingRequest) {
		accPostingRequest.setDebitAccountNumber(debitPostingDetails.getAccount());
		accPostingRequest.setDebitCcy(debitPostingDetails.getAcCcy());
		accPostingRequest.setCreditAccountNumber(creditPostingDetails.getAccount());
		accPostingRequest.setCreditCcy(creditPostingDetails.getAcCcy());
		accPostingRequest.setTransactionAmount(creditPostingDetails.getPostAmount());
		accPostingRequest.setTransactionCcy(creditPostingDetails.getAcCcy());
		accPostingRequest.setPaymentMode("AA");
		accPostingRequest.setDealRefNum(creditPostingDetails.getFinReference());
		accPostingRequest.setTransNarration(creditPostingDetails.getTranCode());
		accPostingRequest.setDealPurpose(creditPostingDetails.getFinEvent());//TODO: Finance purpose
		accPostingRequest.setDealType(creditPostingDetails.getFinType());
		
		List<SecondaryDebitAccount> list = new ArrayList<SecondaryDebitAccount>();
		SecondaryDebitAccount debitAccount = new SecondaryDebitAccount();
		debitAccount.setSecondaryDebitAccount(creditPostingDetails.getAccount());// TODO: Secondary Account
		debitAccount.setScheduleDate(creditPostingDetails.getPostingDate());
		debitAccount.setCustCIF(creditPostingDetails.getCustCIF());
		
		// add Secondary debit account to List object
		list.add(debitAccount);
		
		accPostingRequest.setScndDebitAccountList(list);
	}
	
	/**
	 * Method for Reversing the Account postings
	 * @param coreAcct
	 * @return
	 * @throws AccountNotFoundException
	 */
	@Override
	public List<CoreBankAccountPosting> doReversalPostings(List<CoreBankAccountPosting> accountPostings,String postBranch,
			String createNow) throws InterfaceException {
		logger.debug("Entering");

		for (int i = 0; i < accountPostings.size(); i = i + 2) {
			CoreBankAccountPosting transactionEntry1 = accountPostings.get(i);
			CoreBankAccountPosting transactionEntry2 = accountPostings.get(i + 1);
			
			// If Success Postings for Cancellation
			if (StringUtils.equals(transactionEntry1.getPostStatus(), POSTSTATUS_SUCCESS)) {
				// if Core Banking transaction
				if (StringUtils.equals(transactionEntry1.getPostToSys(), SYSTEM_T24)
						&& StringUtils.equals(transactionEntry2.getPostToSys(), SYSTEM_T24)) {
					AccountPostingDetail accPostingRequest = new AccountPostingDetail();

					if ("C".equals(transactionEntry1.getDrOrCr())) {
						
						preparePostingDetails(transactionEntry2, transactionEntry1, accPostingRequest);
					} else {
						
						preparePostingDetails(transactionEntry1, transactionEntry2, accPostingRequest);
					}
					accPostingRequest.setHostReferenceNum(transactionEntry1.getPostingID());
					accPostingRequest.setReturnCode(transactionEntry1.getErrorId());

					// Send Reversal Postings to middleware
					AccountPostingDetail accPostingRply = getAccPostingProcess().doFillPostingDetails(accPostingRequest,
									InterfaceMasterConfigUtil.ACCOUNT_REVERSAL);
					
					if (!(RETURNCODE_SUCCESS.equals(accPostingRply.getReturnCode()) 
							|| "".equals(accPostingRply.getReturnCode()))) {
						
						transactionEntry1.setPostingStatus(POSTSTATUS_TOBECANCELLED, accPostingRply.getReturnCode(),
								"Reversals Failed");
					} else {
						transactionEntry1.setPostingStatus(POSTSTATUS_REVERSE, RETURNCODE_SUCCESS, "");
						transactionEntry1.setPostingID(accPostingRply.getHostReferenceNum());
						transactionEntry2.setPostingStatus(POSTSTATUS_REVERSE, RETURNCODE_SUCCESS, "");
						transactionEntry2.setPostingID(accPostingRply.getHostReferenceNum());
					}
				} else {
					transactionEntry1.setPostingStatus(POSTSTATUS_REVERSE, RETURNCODE_SUCCESS, "");
					transactionEntry2.setPostingStatus(POSTSTATUS_REVERSE, RETURNCODE_SUCCESS, "");

				}
				// For the postings which are not posted due to an exception in
				// previous transactions
			} else if (!StringUtils.equals(transactionEntry1.getPostStatus(), POSTSTATUS_FAILED)) {
				transactionEntry1.setPostingStatus(POSTSTATUS_NOTPOSTED, RETURNCODE_SUCCESS, "");
				transactionEntry2.setPostingStatus(POSTSTATUS_NOTPOSTED, RETURNCODE_SUCCESS, "");
			}
		}
		logger.debug("Leaving");
		return accountPostings;
	}
	/**
	 * Method for Posting Accrual Details
	 * @param postings
	 * @param valueDate
	 * @param postBranch
	 * @param isDummy
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<CoreBankAccountPosting> doUploadAccruals(List<CoreBankAccountPosting> postings,  Date valueDate, String postBranch, String isDummy)  throws InterfaceException{
		logger.debug("Entering");
		
		/*AccountPostingReply accPostingReply = getAccPostingProcess().doFillPostingDetails(null, 
				InterfaceMasterConfigUtil.ACCT_POSTINGS);*/
		
		logger.debug("Leaving");
		return postings;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public AccountPostingDetailProcess getAccPostingProcess() {
		return accPostingProcess;
	}
	public void setAccPostingProcess(AccountPostingDetailProcess accPostingProcess) {
		this.accPostingProcess = accPostingProcess;
	}

	public AccountDetailProcess getAccountDetailProcess() {
		return accountDetailProcess;
	}

	public void setAccountDetailProcess(AccountDetailProcess accountDetailProcess) {
		this.accountDetailProcess = accountDetailProcess;
	}

	
}
