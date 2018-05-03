package com.pennant.corebanking.process.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.corebanking.dao.InterfaceDAO;
import com.pennant.coreinterface.model.AccountPostingTemp;
import com.pennant.coreinterface.model.CoreBankAccountPosting;
import com.pennant.coreinterface.process.AccountPostingProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class AccountPostingProcessImpl extends GenericProcess implements AccountPostingProcess {

	private static Logger logger = Logger.getLogger(AccountPostingProcessImpl.class);
	
	private InterfaceDAO interfaceDAO;

	public AccountPostingProcessImpl() {
		super();
	}
	
	/**
	 * Method for Fetching account detail Numbers depends on Parameter key fields
	 * @param coreAcct
	 * @return
	 * @throws AccountNotFoundException
	 */
	@Override
	public List<CoreBankAccountPosting> doPostings(List<CoreBankAccountPosting> bankAccountPostings,String postBranch,
			String createNow) throws InterfaceException {
		logger.debug("Entering");
			
		List<AccountPostingTemp> accPostingTempList = new ArrayList<AccountPostingTemp>();
		AccountPostingTemp accPostingTemp = null;
		
		try{
			bankAccountPostings = getInterfaceDAO().validateAccount(bankAccountPostings);			
						
			for(CoreBankAccountPosting item :bankAccountPostings) {
				accPostingTemp = new AccountPostingTemp();
				copyDetails(item, accPostingTemp);
				accPostingTempList.add(accPostingTemp);
			}
			
			getInterfaceDAO().saveAccountPostings(accPostingTempList);
			//getInterfaceDAO().executeAccPosting(accPostingTempList);
			
			bankAccountPostings = getInterfaceDAO().fetchAccountPostingForFin(bankAccountPostings);

		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		}
		
		logger.debug("Leaving");
		return bankAccountPostings;
	}
	
	private void copyDetails(CoreBankAccountPosting item, AccountPostingTemp accPostingTemp) {

		accPostingTemp.setReqRefId(item.getReqRefId());
		accPostingTemp.setReqRefSeq(item.getReqRefSeq());
		accPostingTemp.setReqShadow(item.getShadow());
		accPostingTemp.setAccNumber(item.getAccount());
		accPostingTemp.setPostingBranch(item.getAcBranch());
		accPostingTemp.setPostingCcy(item.getAcCcy());

		if(StringUtils.trimToNull(item.getErrorCode()) == null) {
			accPostingTemp.setPostingCode(item.getTranCode());
		} else {
			accPostingTemp.setPostingCode(item.getRevTranCode());
		}

		accPostingTemp.setPostingAmount(item.getPostAmount());
		accPostingTemp.setPostingDate(item.getPostingDate());
		accPostingTemp.setValueDate(item.getValueDate());
		accPostingTemp.setPostingRef(item.getFinReference());
		accPostingTemp.setPostingNar1(item.getFinEvent()+item.getFinType());
		accPostingTemp.setPostingNar2(item.getLinkedTranId());
		accPostingTemp.setPostingNar3("");
		accPostingTemp.setPostingNar4("");

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
	public List<CoreBankAccountPosting> doUploadAccruals(List<CoreBankAccountPosting> postings,  
			Date valueDate, String postBranch, String isDummy)  throws InterfaceException{
		logger.debug("Entering");

		logger.debug("Leaving");
		return new ArrayList<CoreBankAccountPosting>();
	}
	
	@Override
	public List<CoreBankAccountPosting> doReversalPostings(List<CoreBankAccountPosting> accountPostings, String postBranch,
			String createNow) throws InterfaceException {
		return new ArrayList<CoreBankAccountPosting>();
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public InterfaceDAO getInterfaceDAO() {
		return interfaceDAO;
	}
	public void setInterfaceDAO(InterfaceDAO interfaceDAO) {
		this.interfaceDAO = interfaceDAO;
	}

}
