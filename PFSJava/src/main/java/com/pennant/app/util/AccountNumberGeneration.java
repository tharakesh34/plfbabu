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
 *
 * FileName    		:  AccountNumberGeneration.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.app.util;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import com.pennant.app.constants.LengthConstants;
import com.pennant.app.model.SeqAccountNumber;
import com.pennant.backend.dao.accounts.AccountsDAO;
import com.pennant.backend.dao.applicationmaster.CurrencyDAO;
import com.pennant.backend.dao.rmtmasters.AccountTypeDAO;
import com.pennant.backend.dao.util.GenerateAccountNumberDAO;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.AppException;

public class AccountNumberGeneration implements Serializable {
	
    private static final long serialVersionUID = 6730313822927151348L;
	private static Logger logger = Logger.getLogger(AccountNumberGeneration.class);
	
	private String acType="";
	private String accBranch="";

	private String ccyNumber ="000";
	private String accountHeade="";
	private String accountNumber="";
	private AccountType accountType;
	
	private static GenerateAccountNumberDAO generateAccountNumberDAO;
	private static AccountTypeDAO accountTypeDAO;
	private static CurrencyDAO currencyDAO;
	private static AccountsDAO accountsDAO;

	public AccountNumberGeneration(){
		super();
	} 

	private AccountNumberGeneration (String acType,String ccyCode,
			String accBranch) throws DataAccessException{
		super();
		logger.debug("Entering");
		logger.debug(acType+"-"+ccyCode+"-"+accBranch);
		this.acType = StringUtils.trimToEmpty(acType);
		this.accBranch=StringUtils.trimToEmpty(accBranch);
		this.accountType = getAccountTypeDAO().getAccountTypeById(this.acType, "");

		if(this.accountType==null){
			String[][] parms= new String[2][1]; 
			parms[1][0] =  acType;
			parms[0][0] = PennantJavaUtil.getLabel("label_AcType");
			ErrorDetail details = getError("41002", parms);
			throw new AppException(details.getError());
		}else{
			this.accountHeade = accountType.getAcHeadCode().trim();
		}

		generateCustomerAccount();	
		logger.debug("Leaving");
	}
	
	public static String genNewAcountNumber(String acType,String ccyCode, String accBranch) {
		return new AccountNumberGeneration(acType, ccyCode, accBranch).getAccountNumber();
	}

	private void generateCustomerAccount(){
		logger.debug("Entering");
		long accountSeq=0;
		boolean newSeq=false;

		SeqAccountNumber seqAccountNumber = new SeqAccountNumber(accBranch, this.accountType.getAcHeadCode(), ccyNumber);
		seqAccountNumber = getGenerateAccountNumberDAO().getSeqAccountNumber(seqAccountNumber,false);

		if(seqAccountNumber==null){
			seqAccountNumber =new SeqAccountNumber(accBranch, this.accountType.getAcHeadCode(), ccyNumber);
			newSeq=true;
		}

		accountSeq =seqAccountNumber.getAccountSeqNo()+1;

		String branch ="";
		String headCode="";

		if(accBranch.length() <= LengthConstants.LEN_BRANCH){
			branch  = StringUtils.leftPad(this.accBranch, this.accBranch.length(), '0');
		}else{
			branch  = this.accBranch.substring(this.accBranch.length()-LengthConstants.LEN_BRANCH);	
		}

		if(this.accountHeade.length() <= LengthConstants.LEN_ACHEADCODE){
			headCode  = StringUtils.leftPad(this.accountHeade, this.accountHeade.length(), '0');
		}else{
			headCode  = this.accountHeade.substring(this.accountHeade.length());
		}

		boolean status=true;
		while(status){
			this.accountNumber =  branch+headCode+StringUtils
			.leftPad(String.valueOf(accountSeq),LengthConstants.LEN_ACCOUNT -(branch.length()+headCode.length()),'0');
			Accounts accounts=getAccountsDAO().getAccountsById(this.accountNumber.trim(),"_View");

			if(accounts!=null){
				accountSeq=accountSeq+1;
			}else{
				status=false;
			}
		}
		
		if(newSeq){
			seqAccountNumber.setAccountSeqNo(accountSeq);
			getGenerateAccountNumberDAO().save(seqAccountNumber);

		}else{
			seqAccountNumber.setAccountSeqNo(accountSeq);
			getGenerateAccountNumberDAO().update(seqAccountNumber);
		}
		logger.debug("Leaving");
	}

	private ErrorDetail  getError(String errorId, String[][] parms){
		return ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD
				, errorId, parms[0],parms[1]), SessionUserDetails.getUserLanguage());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public static GenerateAccountNumberDAO getGenerateAccountNumberDAO() {
		return generateAccountNumberDAO;
	}
	public void setGenerateAccountNumberDAO(GenerateAccountNumberDAO generateAccountNumberDAO) {
		AccountNumberGeneration.generateAccountNumberDAO = generateAccountNumberDAO;
	}

	public static AccountTypeDAO getAccountTypeDAO() {
		return accountTypeDAO;
	}
	public void setAccountTypeDAO(AccountTypeDAO accountTypeDAO) {
		AccountNumberGeneration.accountTypeDAO = accountTypeDAO;
	}

	public static CurrencyDAO getCurrencyDAO() {
		return currencyDAO;
	}
	public void setCurrencyDAO(CurrencyDAO currencyDAO) {
		AccountNumberGeneration.currencyDAO = currencyDAO;
	}

	public  void setAccountsDAO(AccountsDAO accountsDAO) {
		AccountNumberGeneration.accountsDAO = accountsDAO;
	}
	public static AccountsDAO getAccountsDAO() {
		return accountsDAO;
	}
}
