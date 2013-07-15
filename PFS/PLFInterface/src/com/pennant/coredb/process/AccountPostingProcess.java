package com.pennant.coredb.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.coredb.dao.CoreDBDAO;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.coreinterface.vo.AccountPostingTemp;
import com.pennant.coreinterface.vo.CoreBankAccountPosting;

public class AccountPostingProcess {

	private static Logger logger = Logger .getLogger(AccountPostingProcess.class);

	private CoreDBDAO coreDBDao;


	/**
	 * Method for Fecthing account detail Numbers depends on Parameter key
	 * fields
	 * 
	 * @param coreAcct
	 * @return
	 * @throws AccountNotFoundException
	 */
	public List<CoreBankAccountPosting> doFillPostingDetails(List<CoreBankAccountPosting> accountPostings, String finBranch, String createNow) throws AccountNotFoundException {
		logger.debug("Entering");
		
		List<AccountPostingTemp> accPostingTempList = new ArrayList<AccountPostingTemp>();
		AccountPostingTemp accPostingTemp = null;
		
		try{
			accountPostings = this.coreDBDao.validateAccount(accountPostings);			
						
			for(CoreBankAccountPosting item :accountPostings) {
				accPostingTemp = new AccountPostingTemp();
				copyDetails(item, accPostingTemp);
				accPostingTempList.add(accPostingTemp);
			}
			
			this.coreDBDao.saveAccountPostings(accPostingTempList);
			this.coreDBDao.executeAccPosting(accPostingTempList);
			
			accountPostings = this.coreDBDao.fetchAccountPostingForFin(accountPostings);
			
			
			

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new AccountNotFoundException(e.getMessage());
		}

		logger.debug("Leaving");
		return accountPostings;
	}

	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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



	public CoreDBDAO getCoreDBDao() {
		return coreDBDao;
	}

	public void setCoreDBDao(CoreDBDAO coreDBDao) {
		this.coreDBDao = coreDBDao;
	}
	
}
