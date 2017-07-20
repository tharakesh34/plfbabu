package com.pennant.backend.service.collateral;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.collateralmark.CollateralMarkDAO;
import com.pennant.backend.model.collateral.FinCollateralMark;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.constants.InterfaceConstants;
import com.pennant.coreinterface.model.collateral.CollateralMark;
import com.pennant.coreinterface.model.collateral.DepositDetail;
import com.pennanttech.pennapps.core.InterfaceException;

public class CollateralMarkProcess {

	private static final Logger logger = Logger.getLogger(CollateralMarkProcess.class);

	public CollateralMarkProcess() {
		//empty constructor
	}
	
	private AccountInterfaceService  accountInterfaceService;
	protected CollateralMarkDAO collateralMarkDAO;


	/**
	 * Method for process collateral data and send Mark request to interface
	 * 
	 * @param finCollateralDetailsList
	 * @throws InterfaceException
	 */
	public CollateralMark markCollateral(List<FinCollaterals> finCollateralDetailsList) throws InterfaceException {
		logger.debug("Entering");

		if(finCollateralDetailsList != null && !finCollateralDetailsList.isEmpty()) {

			CollateralMark collateralMark = processCollateralData(finCollateralDetailsList, FinanceConstants.COLLATERAL_MARK);

			//send Colleteral Marking request
			CollateralMark coltralMarkReq = new CollateralMark();
			BeanUtils.copyProperties(collateralMark, coltralMarkReq);

			CollateralMark colteralMarkRes = getAccountInterfaceService().collateralMarking(coltralMarkReq);

			saveColletralDetails(collateralMark, colteralMarkRes);
			
			return colteralMarkRes;
		}

		logger.debug("Leaving");
		return null;
	}

	/**
	 * Method for process collateral data and send De-Mark request to interface
	 * 
	 * @param finCollateralList
	 * @throws InterfaceException
	 */
	public void deMarkCollateral(List<FinCollaterals> finCollateralList) throws InterfaceException {
		logger.debug("Entering");

		if(finCollateralList != null && !finCollateralList.isEmpty()) {

			FinCollateralMark finCollateralMark = validateCollateralReq(finCollateralList);
			
			if(finCollateralMark != null) {
				CollateralMark collateralMark = processCollateralData(finCollateralList, FinanceConstants.COLLATERAL_DEMARK);

				//send Collateral Marking request
				CollateralMark coltralMarkReq = new CollateralMark();
				BeanUtils.copyProperties(collateralMark, coltralMarkReq);

				CollateralMark colteralMarkRes = getAccountInterfaceService().collateralDeMarking(coltralMarkReq);

				saveColletralDetails(collateralMark, colteralMarkRes);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method to check collateral Marking is success or not
	 * 
	 * @param finCollateralDetailsList
	 * @return
	 */
	private FinCollateralMark validateCollateralReq(List<FinCollaterals> finCollateralDetailsList) {
		logger.debug("Entering");

		for(FinCollaterals finCollaterals: finCollateralDetailsList) {
			return getCollateralMarkDAO().getCollateralById(finCollaterals.getReference());
		}
		logger.debug("Leaving");
		return null;
	}

	/**
	 * 
	 * 
	 * @param finCollateralDetailsList
	 * @param markStatus 
	 * @return
	 */
	private CollateralMark processCollateralData(List<FinCollaterals> finCollateralDetailsList, String markStatus) {
		logger.debug("Entering");

		CollateralMark collateralMark = new CollateralMark();

		if (finCollateralDetailsList != null && !finCollateralDetailsList.isEmpty()) {
			
			collateralMark.setAccountDetail(null);
			List<DepositDetail> depositList = new ArrayList<DepositDetail>();
		
			for (FinCollaterals finCollaterals : finCollateralDetailsList) {
				DepositDetail depositDetail = new DepositDetail();
				
				depositDetail.setDepositID(finCollaterals.getReference());
				depositDetail.setInsAmount(finCollaterals.getValue());
				depositDetail.setBlockingDate(DateUtility.getAppDate());
				depositDetail.setReason(StringUtils.trimToEmpty(finCollaterals.getRemarks()).length() > 65 ? 
						finCollaterals.getRemarks().substring(0, 64) : finCollaterals.getRemarks());

				depositList.add(depositDetail);
				collateralMark.setFinReference(finCollaterals.getFinReference());
			}
			
			collateralMark.setDepositDetail(depositList);
			collateralMark.setStatus(markStatus);
			
			return collateralMark;
		}

		logger.debug("Leaving");
		return collateralMark;
	}

	/**
	 * Method for prepare Collateral request and response data and save
	 * 
	 * @param colateralMarkReq
	 * @param colteralMarkRes
	 */
	private void saveColletralDetails(CollateralMark colateralMarkReq, CollateralMark colteralMarkRes) {
		logger.debug("Enetering");

		if(colateralMarkReq != null && colteralMarkRes != null) {
			
			FinCollateralMark finCollateralMark = new FinCollateralMark();
			
			finCollateralMark.setBranchCode(colateralMarkReq.getBranchCode());
			finCollateralMark.setStatus(colateralMarkReq.getStatus());
			finCollateralMark.setFinReference(colateralMarkReq.getFinReference());

			finCollateralMark.setReferenceNum(colteralMarkRes.getReferenceNum());
			finCollateralMark.setReturnCode(colteralMarkRes.getReturnCode());
			finCollateralMark.setReturnText(colteralMarkRes.getReturnText());

			for(DepositDetail depositDetail: colateralMarkReq.getDepositDetail()) {
				finCollateralMark.setDepositID(depositDetail.getDepositID());
				finCollateralMark.setInsAmount(depositDetail.getInsAmount());
				finCollateralMark.setBlockingDate(depositDetail.getBlockingDate());
				finCollateralMark.setReason(depositDetail.getReason());

				if(StringUtils.equals(colteralMarkRes.getReturnCode(), InterfaceConstants.SUCCESS_CODE)) {
					finCollateralMark.setProcessed(true);
				}
				
				// Save the Collateral Mark or DeMark request and response data
				finCollateralMark.setId(Long.MIN_VALUE);
				getCollateralMarkDAO().save(finCollateralMark);
			}
		}
		logger.debug("Leaving");
	}

	public void deMarkCollateral(FinCollaterals finCollaterals) throws InterfaceException {
		logger.debug("Enetering");
		
		List<FinCollaterals> finCollatList = null;
		if(finCollaterals != null) {
			finCollatList = new ArrayList<FinCollaterals>();
			finCollatList.add(finCollaterals);
		}
		
		// Send Collateral De-Mark Request
		deMarkCollateral(finCollatList);
		logger.debug("Leaving");
	}

	/**
	 * Method for fetching Collateral De-Marking status
	 * @param finReference
	 * @param markStatus 
	 * 
	 * @return boolean
	 */
	public boolean getCollatDeMarkStatus(String finReference, String markStatus) {
		logger.debug("Enetering");
		boolean deMarkSts = false;
		FinCollateralMark finCollatMark = getCollateralMarkDAO().getCollatDeMarkStatus(finReference,markStatus);
		if(finCollatMark != null) {
			deMarkSts = true;
		}
		logger.debug("Leaving");
		return deMarkSts;
	}

	public void doCollateralDemark(List<FinCollateralMark> collatDeMarkList) throws InterfaceException {
		logger.debug("Entering");
		
		if(collatDeMarkList != null && collatDeMarkList.isEmpty()) {
			FinCollaterals finCollateral = new FinCollaterals();
			List<FinCollaterals> list = new ArrayList<FinCollaterals>();
			
			for(FinCollateralMark collaterals: collatDeMarkList) {
				finCollateral.setReference(collaterals.getDepositID());
				finCollateral.setValue(collaterals.getInsAmount());
				
				list.add(finCollateral);
			}
			
			// send Collateral de mark request to middleware
			deMarkCollateral(list);
		}
		
		logger.debug("Leaving");
	}

	public List<FinCollateralMark> getCollateralList(String finReference) {
		return getCollateralMarkDAO().getCollateralList(finReference);
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}

	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}

	public CollateralMarkDAO getCollateralMarkDAO() {
		return collateralMarkDAO;
	}

	public void setCollateralMarkDAO(CollateralMarkDAO collateralMarkDAO) {
		this.collateralMarkDAO = collateralMarkDAO;
	}

}
