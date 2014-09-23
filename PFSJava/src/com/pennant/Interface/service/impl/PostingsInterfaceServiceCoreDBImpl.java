package com.pennant.Interface.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.Interface.service.PostingsInterfaceService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.coreinterface.model.CoreBankAccountPosting;
import com.pennant.coreinterface.service.AccountPostingProcess;

public class PostingsInterfaceServiceCoreDBImpl implements PostingsInterfaceService{
	
	private static Logger logger = Logger.getLogger(PostingsInterfaceServiceCoreDBImpl.class);
	protected AccountPostingProcess accountPostingProcess;
	protected Map<Integer,String> descMap = null;

	/**
	 * Method for Fetch Account detail depends on Parameter key fields
	 * @param coreAcct
	 * @return
	 * @throws AccountNotFoundException
	 */
	public List<ReturnDataSet> doFillPostingDetails(List<ReturnDataSet> setDetails,String finBranch,
			long linkTransId, String createNow) throws AccountNotFoundException  {
		logger.debug("Entering");
				
		ReturnDataSet dataSet = null;
		List<CoreBankAccountPosting> coreBankPostingDetails = new ArrayList<CoreBankAccountPosting>(setDetails.size());
		CoreBankAccountPosting accountPosting = null;
		descMap = new HashMap<Integer, String>(setDetails.size());
		for (int i = 0; i < setDetails.size(); i++) {
			dataSet = setDetails.get(i);
			accountPosting = new CoreBankAccountPosting();
			accountPosting.setLinkedTranId(String.valueOf(linkTransId));
			accountPosting.setFinReference(dataSet.getFinReference());
			accountPosting.setFinEvent(dataSet.getFinEvent());
			accountPosting.setFinType(dataSet.getFinType());
			accountPosting.setPostingDate(DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR).toString()));
			accountPosting.setValueDate(dataSet.getValueDate());
			accountPosting.setCustCIF(dataSet.getCustCIF());
			accountPosting.setAcBranch(dataSet.getFinBranch());
			accountPosting.setAcCcy(dataSet.getAcCcy());
			
			if("DISB".equals(dataSet.getAccountType())) {
				accountPosting.setAcType("DI");
			} else if("REPAY".equals(dataSet.getAccountType())){ //TODO
				accountPosting.setAcType("RE");
			}else {
				accountPosting.setAcType(dataSet.getAccountType());
			}
			

			accountPosting.setCreateNew(dataSet.getFlagCreateNew());
			accountPosting.setInternalAc(dataSet.getInternalAc());
			accountPosting.setCreateIfNF(dataSet.getFlagCreateIfNF());
			accountPosting.setTransOrderId(dataSet.getTranOrderId());
			accountPosting.setTranCode(dataSet.getTranCode());
			accountPosting.setRevTranCode(dataSet.getRevTranCode());
			accountPosting.setDrOrCr(dataSet.getDrOrCr());
			accountPosting.setShadow(dataSet.isShadowPosting()?"Y":"N");
			accountPosting.setAccount(dataSet.getAccount());
			accountPosting.setPostAmount(dataSet.getPostAmount());
			descMap.put(dataSet.getTranOrder(), dataSet.getTranDesc());
			coreBankPostingDetails.add(accountPosting);
		}
		
		//Connecting to CoreBanking Interface
		coreBankPostingDetails = getAccountPostingProcess().doFillPostingDetails(coreBankPostingDetails, finBranch,createNow);
		
		//Fill the Account data using Core Banking Object
		List<ReturnDataSet> dataSetList = new ArrayList<ReturnDataSet>(coreBankPostingDetails.size());
		for (int i = 0; i < coreBankPostingDetails.size(); i++) {
			dataSet = new ReturnDataSet();
			CoreBankAccountPosting detail = coreBankPostingDetails.get(i);

			dataSet.setLinkedTranId(Long.valueOf(detail.getLinkedTranId()));
			dataSet.setPostref(detail.getAcBranch()+"-"+detail.getAcType()+"-"+detail.getAcCcy());
			dataSet.setPostingId(detail.getFinReference()+DateUtility.formatDate(new Date(), "yyyyMMddHHmmss")+
					StringUtils.leftPad(String.valueOf((long)((new Random()).nextDouble()*10000L)).trim(), 4,"0"));
			dataSet.setFinReference(detail.getFinReference());
			dataSet.setFinEvent(detail.getFinEvent());
			dataSet.setFinType(detail.getFinType());
			dataSet.setValueDate(detail.getValueDate());
			dataSet.setCustCIF(detail.getCustCIF());
			dataSet.setFinBranch(detail.getAcBranch());
			dataSet.setAcCcy(detail.getAcCcy());
			dataSet.setAccountType(detail.getAcType());
			
			if("DI".equals(detail.getAcType())) {
				dataSet.setAccountType("DISB");
			} else if("RE".equals(detail.getAcType())){ //TODO
				dataSet.setAccountType("REPAY");
			}else if("Y".equals(detail.getInternalAc())) {
				dataSet.setAccountType(detail.getAcSPCode());
			}
			
			
			dataSet.setFlagCreateNew(detail.getCreateNew());
			dataSet.setInternalAc(detail.getInternalAc());
			dataSet.setFlagCreateIfNF(detail.getCreateIfNF());
			if(descMap.containsKey(detail.getTransOrderId())){
				dataSet.setTranDesc(descMap.get(detail.getTransOrderId()));
			}
			dataSet.setTranCode(detail.getTranCode());
			dataSet.setRevTranCode(detail.getRevTranCode());
			dataSet.setDrOrCr(detail.getDrOrCr());
			dataSet.setShadowPosting(detail.getShadow().equals("Y")?true:false);
			dataSet.setAccount(detail.getAccount());
			dataSet.setPostAmount(detail.getPostAmount()); 	
			dataSet.setPostStatus(StringUtils.trimToEmpty(detail.getPostStatus())); 		
			dataSet.setErrorId(StringUtils.trimToEmpty(detail.getErrorId())); 	
			dataSet.setErrorMsg(StringUtils.trimToEmpty(detail.getErrorMsg())); 	
			dataSetList.add(dataSet);
		}
		descMap = null;
		logger.debug("Leaving");
		return dataSetList;
	}

	@Override
    public List<ReturnDataSet> doAccrualPosting(List<ReturnDataSet> list, Date valueDate,
            String postBranch, long linkedTranId, String createNow, String isDummy)
            throws AccountNotFoundException {
	    // TODO Auto-generated method stub
	    return null;
    }
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public AccountPostingProcess getAccountPostingProcess() {
    	return accountPostingProcess;
    }
	public void setAccountPostingProcess(AccountPostingProcess accountPostingProcess) {
    	this.accountPostingProcess = accountPostingProcess;
    }

}
