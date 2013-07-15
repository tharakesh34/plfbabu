package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.FinRepayQueue.FinRepayQueueDAO;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceRepayPriorityDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceRepayPriority;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.service.finance.ManualPaymentService;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public class ManualPaymentServiceImpl implements ManualPaymentService {

	private final static Logger logger = Logger.getLogger(ManualPaymentServiceImpl.class);
	private FinanceRepayPriorityDAO financeRepayPriorityDAO;
	private FinanceRepaymentsDAO	 financeRepaymentsDAO;
	private FinRepayQueueDAO finRepayQueueDAO;
	private FinanceRepayPriority financeRepayPriority = null;
	private RepaymentPostingsUtil repayPostingUtil;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private FinanceTypeDAO financeTypeDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private RepayInstructionDAO repayInstructionDAO; 
	FinRepayQueue finRepayQueue;

	Date dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());
	Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");

	public boolean saveOrUpdate(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails, 
			List<RepayInstruction> repayInstructions, RepayMain repayMain,
			List<RepayScheduleDetail> repaySchdList, boolean schdlReModified) {

		boolean	isPostingSuccess = false;
		try {
			
			//Effected Remodified FinanceMain Details & Schedule List Details update in DataBase
			if(schdlReModified){
				
				//FinanceMain Details updation with Effective Schedule Details
				financeMain.setVersion(financeMain.getVersion()+1);
				getFinanceMainDAO().update(financeMain, "", false);
				
				getFinanceScheduleDetailDAO().deleteByFinReference(financeMain.getFinReference(), "", false);
				HashMap<Date, Integer> mapDateSeq = new HashMap<Date, Integer>();
				// Finance Schedule Details
				for (int i = 0; i < scheduleDetails.size(); i++) {
					FinanceScheduleDetail curschd = scheduleDetails.get(i);
					
					curschd.setLastMntBy(financeMain.getLastMntBy());
					curschd.setFinReference(financeMain.getFinReference());
					
					int seqNo = 0;
					if (mapDateSeq.containsKey(curschd.getSchDate())) {
						seqNo = mapDateSeq.get(curschd.getSchDate());
						mapDateSeq.remove(curschd.getSchDate());
					}
					seqNo = seqNo + 1;
					mapDateSeq.put(curschd.getSchDate(), seqNo);
					curschd.setSchSeq(seqNo);
				}
				getFinanceScheduleDetailDAO().saveList(scheduleDetails, "", false);
				
				getRepayInstructionDAO().deleteByFinReference(financeMain.getFinReference(), "", false);
				//Finance Repay Instruction Details
				for (int i = 0; i < repayInstructions.size(); i++) {
					repayInstructions.get(i).setFinReference(financeMain.getFinReference());
				}
				getRepayInstructionDAO().saveList(repayInstructions, "", false);
			}

			// FETCH Finance type Repayment Priority
			financeRepayPriority = getFinanceRepayPriorityDAO().getFinanceRepayPriorityById(repayMain.getFinType(),"");
			
			//Check Finance is RIA Finance Type or Not
			boolean isRIAFinance = getFinanceTypeDAO().checkRIAFinance(repayMain.getFinType());

			for(int i = 0; i < repaySchdList.size(); i++) {

				finRepayQueue = new FinRepayQueue();
				finRepayQueue.setFinReference(repayMain.getFinReference());
				finRepayQueue.setRpyDate(repaySchdList.get(i).getDefSchdDate());
				finRepayQueue.setFinRpyFor(repaySchdList.get(i).getSchdFor());

				//Get Finance Repay Queue Details if exists
				FinRepayQueue repayQueue = getFinRepayQueueDAO().getFinRepayQueueById(finRepayQueue,"");
				if(repayQueue != null){
					finRepayQueue = repayQueue;
					finRepayQueue.setRcdNotExist(false);
				}else{
					finRepayQueue.setRcdNotExist(true);
					finRepayQueue = doWriteDataToBean(finRepayQueue,repayMain,repaySchdList.get(i));
				}

				finRepayQueue.setRefundAmount(repaySchdList.get(i).getRefundReq());
				isPostingSuccess = getRepayPostingUtil().postingsRepayProcess(financeMain, scheduleDetails, 
						new FinanceProfitDetail(), dateValueDate, finRepayQueue,
						repaySchdList.get(i).getProfitSchdPayNow().add(repaySchdList.get(i).getPrincipalSchdPayNow()),isRIAFinance);

			}
		} catch (AccountNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return isPostingSuccess;
	}

	/**
	 * Method for prepare RepayQueue data
	 * 
	 * @param resultSet
	 * @return
	 */
	private FinRepayQueue doWriteDataToBean(FinRepayQueue finRepayQueue, RepayMain repayMain, RepayScheduleDetail rsd) {
		logger.debug("Entering");

		finRepayQueue.setBranch(repayMain.getFinBranch());
		finRepayQueue.setFinType(repayMain.getFinType());
		finRepayQueue.setCustomerID(repayMain.getCustID());

		if(financeRepayPriority != null){
			finRepayQueue.setFinPriority(financeRepayPriority.getFinPriority());
		}else{
			finRepayQueue.setFinPriority(9999);
		}

		finRepayQueue.setSchdPft(rsd.getProfitSchd());
		finRepayQueue.setSchdPri(rsd.getPrincipalSchd());
		finRepayQueue.setSchdPftBal(rsd.getProfitSchd());
		finRepayQueue.setSchdPriBal(rsd.getPrincipalSchd());

		logger.debug("Leaving");
		return finRepayQueue;
	}

	@Override
	public List<FinanceRepayments> getFinRepayListByFinRef(String finRef, String type) {
		return getFinanceRepaymentsDAO().getFinRepayListByFinRef(finRef, type);
	}

	@Override
	public FinanceProfitDetail getFinProfitDetailsById(String finReference) {
		return getFinanceProfitDetailDAO().getFinProfitDetailsById(finReference);
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public FinanceRepayPriorityDAO getFinanceRepayPriorityDAO() {
		return financeRepayPriorityDAO;
	}
	public void setFinanceRepayPriorityDAO(FinanceRepayPriorityDAO financeRepayPriorityDAO) {
		this.financeRepayPriorityDAO = financeRepayPriorityDAO;
	}

	public RepaymentPostingsUtil getRepayPostingUtil() {
		return repayPostingUtil;
	}
	public void setRepayPostingUtil(RepaymentPostingsUtil repayPostingUtil) {
		this.repayPostingUtil = repayPostingUtil;
	}

	public FinRepayQueueDAO getFinRepayQueueDAO() {
		return finRepayQueueDAO;
	}
	public void setFinRepayQueueDAO(FinRepayQueueDAO finRepayQueueDAO) {
		this.finRepayQueueDAO = finRepayQueueDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}
	public FinanceRepaymentsDAO getFinanceRepaymentsDAO() {
		return financeRepaymentsDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
	    this.financeProfitDetailDAO = financeProfitDetailDAO;
    }
	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
	    return financeProfitDetailDAO;
    }

	public FinanceTypeDAO getFinanceTypeDAO() {
    	return financeTypeDAO;
    }
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
    	this.financeTypeDAO = financeTypeDAO;
    }

	public FinanceMainDAO getFinanceMainDAO() {
    	return financeMainDAO;
    }
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
    	this.financeMainDAO = financeMainDAO;
    }

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
    	return financeScheduleDetailDAO;
    }
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
    	this.financeScheduleDetailDAO = financeScheduleDetailDAO;
    }

	public RepayInstructionDAO getRepayInstructionDAO() {
    	return repayInstructionDAO;
    }
	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
    	this.repayInstructionDAO = repayInstructionDAO;
    }
	
}
