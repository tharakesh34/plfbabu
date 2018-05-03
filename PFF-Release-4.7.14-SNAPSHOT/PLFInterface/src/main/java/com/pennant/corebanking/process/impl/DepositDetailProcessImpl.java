package com.pennant.corebanking.process.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.deposits.FetchDeposit;
import com.pennant.coreinterface.model.deposits.FetchDepositDetail;
import com.pennant.coreinterface.model.deposits.InvestmentContract;
import com.pennant.coreinterface.process.DepositDetailProcess;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennanttech.pennapps.core.InterfaceException;

public class DepositDetailProcessImpl implements DepositDetailProcess {
	
	private static final Logger logger = Logger.getLogger(DepositDetailProcessImpl.class);
	
	public DepositDetailProcessImpl() {
		super();
	}
	
	@Override
	public FetchDeposit fetchDeposits(FetchDeposit fetchDeposit) throws InterfaceException {
		logger.debug("Entering");
		
		FetchDeposit deposits = new FetchDeposit();
		deposits.setReferenceNum(PFFXmlUtil.getReferenceNumber());
		deposits.setReturnCode("0000");
		deposits.setReturnText("SUCESS");
		deposits.setTimeStamp(System.currentTimeMillis());
		deposits.setCustomerNo(fetchDeposit.getCustomerNo());
		
		// add InvestmentContract List to FetchDeposit
		List<InvestmentContract> invstList = new ArrayList<InvestmentContract>();
		
		InvestmentContract invstmentContract = new InvestmentContract();
		invstmentContract.setInvstContractNo("123456789");
		invstmentContract.setInvstHolderName("Abdul Rajak");
		invstmentContract.setAccountType("SA");
		invstmentContract.setBranchCode("1001");
		invstmentContract.setCurrencyCode("AED");
		invstmentContract.setInvestmentAmount(new BigDecimal(1000000000));
		invstmentContract.setOpenDate(new Date());
		invstmentContract.setMaturityDate(new Date());
		invstmentContract.setDepositTenor(BigDecimal.TEN);
		invstmentContract.setCategoryID("5555");
		
		invstList.add(invstmentContract);

		//second object
		InvestmentContract invstmentContract2 = new InvestmentContract();
		invstmentContract2.setInvstContractNo("7894561213");
		invstmentContract2.setInvstHolderName("");
		invstmentContract2.setAccountType("SA");
		invstmentContract2.setBranchCode("1001");
		invstmentContract2.setCurrencyCode("AED");
		invstmentContract2.setInvestmentAmount(new BigDecimal(1000000000));
		invstmentContract2.setOpenDate(new Date());
		invstmentContract2.setMaturityDate(new Date());
		invstmentContract2.setDepositTenor(BigDecimal.TEN);
		invstmentContract2.setCategoryID("4444");
		
		invstList.add(invstmentContract2);
		
		deposits.setInvstMentContactList(invstList);
		
		logger.debug("Leaving");
		
		return deposits;
	}

	@Override
	public FetchDepositDetail fetchDepositDetails(FetchDepositDetail fetchDepositDetail) throws InterfaceException {
		logger.debug("Entering");
		
		FetchDepositDetail depositDetail = new FetchDepositDetail();
		depositDetail.setReferenceNum(PFFXmlUtil.getReferenceNumber());
		depositDetail.setReturnCode("0000");
		depositDetail.setReturnText("SUCESS");
		depositDetail.setTimeStamp(System.currentTimeMillis());
		
		depositDetail.setCustCIF("PC1234");
		depositDetail.setInvstContractNo(fetchDepositDetail.getInvstContractNo());
		depositDetail.setBranchCode("1001");
		depositDetail.setCurrencyCode("AED");
		depositDetail.setInvstAmount(new BigDecimal(1000000000));
		depositDetail.setOpenDate(new Date());
		depositDetail.setMaturityDate(new Date());
		depositDetail.setAccountType("SA");
		depositDetail.setAccountName("SBACCC");
		depositDetail.setDepositTenor(10);
		depositDetail.setFinalMaturityDate(new Date());
		depositDetail.setAutoRollOverTenor(new BigDecimal(5));
		depositDetail.setTotalReceivedProfit(new BigDecimal(1000));
		depositDetail.setProfitRate(new BigDecimal(5));
		depositDetail.setPrincipleLiquidAccount("PLACC");
		depositDetail.setPftLiquidationAccount("PFLACC");
		depositDetail.setLienBalance(new BigDecimal(1000));
		depositDetail.setLienDesc("Insufficient Balance");
		depositDetail.setStatus("ACT");
		
		logger.debug("Leaving");
		
		return depositDetail;
	}

}
