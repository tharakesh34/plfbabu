package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.CashBackDetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.feetype.FeeType;
import com.pennant.backend.model.finance.CashBackDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.service.finance.CashBackProcessService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;

public class CashBackProcessServiceImpl implements CashBackProcessService {
	private static final Logger logger = Logger.getLogger(CashBackProcessServiceImpl.class);

	private FeeTypeDAO feeTypeDAO;
	private ManualAdviseService manualAdviseService;
	private CashBackDetailDAO cashBackDetailDAO;

	/**
	 * Method for creating cashback records against scheme structure definition on Loan creation
	 * 
	 * @param finMain
	 */
	@Override
	public void createCashBackAdvice(FinanceMain finMain, Promotion promotion, Date appDate) {
		logger.debug(Literal.ENTERING);

		List<CashBackDetail> cashBackDetailList = new ArrayList<>();
		ManualAdvise manualAdvise = null;

		switch (promotion.getCbPyt()) {

		case PennantConstants.DBD_AND_MBD_SEPARATELY:

			boolean dbdProcAvail = false;
			BigDecimal dbdAmount = null;

			if (promotion.isDbd() && !promotion.isDbdRtnd()) {

				dbdAmount = finMain.getFinAmount().multiply(promotion.getDbdPerc())
						.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);

				// Payable Advise creation against cash back amount in Hold Status
				// Hold is for to avoid usage in screens till Settlement process completed
				manualAdvise = cashBackDueCreation(finMain, promotion.getDbdFeeTypId(), dbdAmount, appDate);

				// Logging Cash back process details for future usage
				cashBackDetailList.add(prepareCashbackLog(finMain, manualAdvise, "DBD"));

				dbdProcAvail = true;
			}

			if (promotion.isMbd() && !promotion.isMbdRtnd()) {
				BigDecimal mbdAmount = BigDecimal.ZERO;

				// If DBD Available balance amount from Subvention will be MDB CashBack
				// Total CashBack should not cross Subvention amount always
				if (dbdProcAvail) {
					mbdAmount = finMain.getSvAmount().subtract(dbdAmount);
				} else {
					mbdAmount = finMain.getSvAmount();
				}
				
				// Payable Advise creation against cash back amount in Hold Status
				// Hold is for to avoid usage in screens till Settlement process completed
				manualAdvise = cashBackDueCreation(finMain, promotion.getMbdFeeTypId(), mbdAmount, appDate);

				// Logging Cash back process details for future usage
				cashBackDetailList.add(prepareCashbackLog(finMain, manualAdvise, "MBD"));
			}

			break;
		case PennantConstants.DBD_AND_MBD_TOGETHER:

			// Payable Advise creation against cash back amount in Hold Status
			// Hold is for to avoid usage in screens till Settlement process completed
			manualAdvise = cashBackDueCreation(finMain, promotion.getDbdAndMbdFeeTypId(), finMain.getSvAmount(),
					appDate);

			// Logging Cash back process details for future usage
			cashBackDetailList.add(prepareCashbackLog(finMain, manualAdvise, "DBMBD"));
			break;

		default:
			break;
		}

		// Saving the all cashBack details
		if (CollectionUtils.isNotEmpty(cashBackDetailList)) {
			cashBackDetailDAO.save(cashBackDetailList);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Preparation of Cahsback Log details
	 * 
	 * @param finMain
	 * @param manualAdvise
	 * @param type
	 * @return
	 */
	private CashBackDetail prepareCashbackLog(FinanceMain finMain, ManualAdvise manualAdvise, String type) {
		logger.debug(Literal.ENTERING);

		CashBackDetail cbDetail = new CashBackDetail();
		cbDetail.setFinReference(finMain.getFinReference());
		cbDetail.setType(type);
		cbDetail.setAdviseId(manualAdvise.getAdviseID());
		cbDetail.setAmount(manualAdvise.getAdviseAmount());
		cbDetail.setRefunded(false);

		logger.debug(Literal.LEAVING);
		return cbDetail;
	}

	/**
	 * Method for create Payable advice against Cashback amount under Cashback type
	 * 
	 * @param finMain
	 * @param feeId
	 * @param cashbackAmount
	 * @return
	 */
	private ManualAdvise cashBackDueCreation(FinanceMain finMain, long feeId, BigDecimal cashbackAmount, Date appDate) {
		logger.debug(Literal.ENTERING);

		ManualAdvise manualAdvise = new ManualAdvise();
		manualAdvise.setAdviseID(manualAdviseService.getNewAdviseID());
		manualAdvise.setFinReference(finMain.getFinReference());
		manualAdvise.setAdviseType(FinanceConstants.MANUAL_ADVISE_PAYABLE);
		manualAdvise.setAdviseAmount(cashbackAmount);
		manualAdvise.setBalanceAmt(manualAdvise.getAdviseAmount());
		manualAdvise.setHoldDue(true);
		manualAdvise.setFinSource(UploadConstants.FINSOURCE_ID_CD_UPLOAD);

		FeeType cbFeeType = feeTypeDAO.getFeeTypeById(feeId, "_AView");
		com.pennant.backend.model.finance.FeeType modelFeeType = new com.pennant.backend.model.finance.FeeType();
		BeanUtils.copyProperties(cbFeeType, modelFeeType);
		manualAdvise.setFeeType(modelFeeType);
		manualAdvise.setFeeTypeCode(cbFeeType.getFeeTypeCode());
		manualAdvise.setFeeTypeID(cbFeeType.getFeeTypeID());

		manualAdvise.setValueDate(appDate);
		manualAdvise.setPostDate(appDate);
		manualAdvise.setVersion(1);
		manualAdvise.setLastMntBy(finMain.getLastMntBy());
		manualAdvise.setLastMntOn(finMain.getLastMntOn());
		manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		manualAdvise.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		manualAdvise.setNewRecord(true);
		manualAdvise.setUserDetails(finMain.getUserDetails());

		// Save Cashback Advice
		manualAdviseService.doApprove(getAuditHeader(manualAdvise, PennantConstants.TRAN_WF));

		logger.debug(Literal.LEAVING);
		return manualAdvise;
	}

	private AuditHeader getAuditHeader(ManualAdvise aManualAdvise, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aManualAdvise.getBefImage(), aManualAdvise);
		return new AuditHeader(String.valueOf(aManualAdvise.getAdviseID()), String.valueOf(aManualAdvise.getAdviseID()),
				null, null, auditDetail, aManualAdvise.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}

	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

	public void setCashBackDetailDAO(CashBackDetailDAO cashBackDetailDAO) {
		this.cashBackDetailDAO = cashBackDetailDAO;
	}

}
