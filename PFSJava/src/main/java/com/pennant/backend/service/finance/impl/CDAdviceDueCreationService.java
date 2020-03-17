package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.feetype.FeeType;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;


@Component(value = "cdAdviceDueCreationService")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CDAdviceDueCreationService {
	private static final Logger logger = Logger.getLogger(CDAdviceDueCreationService.class);

	@Autowired(required = false)
	private FeeTypeService feeTypeService;
	@Autowired(required = false)
	private ManualAdviseService manualAdviseService;
	@Autowired(required = false)
	private ManualAdviseDAO manualAdviseDAO;


	public void prepareAdviceDue(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		
		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		Promotion promotion = financeDetail.getPromotion();
		ManualAdvise manualAdvise = new ManualAdvise();


		switch (promotion.getCbPyt()) {
		case PennantConstants.DBD_AND_MBD_SEPARATELY:

			if (promotion.isDbd() && !promotion.isDbdRtnd()) {
				BigDecimal dbdAmount = finMain.getFinAmount().multiply(promotion.getDbdPerc())
						.divide(new BigDecimal(100));
				manualAdvise.setAdviseAmount(dbdAmount);
				doDueCreation(finMain, promotion.getDbdFeeTypId(), manualAdvise);
			}

			if (promotion.isMbd() && !promotion.isMbdRtnd()) {
				BigDecimal dbdAmount = BigDecimal.ZERO;
				if (promotion.isDbd() && !promotion.isDbdRtnd()) {
					dbdAmount = finMain.getSvAmount().subtract(
							finMain.getFinAmount().multiply(promotion.getDbdPerc()).divide(new BigDecimal(100)));
				} else {
					dbdAmount = finMain.getSvAmount();
				}
				manualAdvise.setAdviseAmount(dbdAmount);
				doDueCreation(finMain, promotion.getMbdFeeTypId(), manualAdvise);
			}

			break;
		case PennantConstants.DBD_AND_MBD_TOGETHER:
			manualAdvise.setAdviseAmount(finMain.getSvAmount());
			doDueCreation(finMain, promotion.getMbdFeeTypId(), manualAdvise);
			break;

		default:
			break;
		}
		logger.debug(Literal.LEAVING);
	}

	private void doDueCreation(FinanceMain finMain, long feeId, ManualAdvise manualAdvise) {
		logger.debug(Literal.ENTERING);

		manualAdvise.setAdviseID(Long.MIN_VALUE);
		FeeType javaFeeType = feeTypeService.getApprovedFeeTypeById(feeId);
		if (manualAdvise.getAdviseID() <= 0) {
			manualAdvise.setAdviseID(manualAdviseDAO.getNewAdviseID());
		}
		com.pennant.backend.model.finance.FeeType modelFeeType = new com.pennant.backend.model.finance.FeeType();
		BeanUtils.copyProperties(javaFeeType, modelFeeType);
		manualAdvise.setFeeType(modelFeeType);
		manualAdvise.setAdviseType(FinanceConstants.MANUAL_ADVISE_PAYABLE);
		manualAdvise.setFinReference(finMain.getFinReference());
		manualAdvise.setFeeTypeCode(javaFeeType.getFeeTypeCode());
		manualAdvise.setFeeTypeID(javaFeeType.getFeeTypeID());
		manualAdvise.setSequence(0);
		manualAdvise.setBounceID(0);
		manualAdvise.setReceiptID(0);
		manualAdvise.setPaidAmount(BigDecimal.ZERO);
		manualAdvise.setWaivedAmount(BigDecimal.ZERO);
		manualAdvise.setValueDate(DateUtility.getAppDate());
		manualAdvise.setPostDate(DateUtility.getAppDate());
		manualAdvise.setReservedAmt(BigDecimal.ZERO);
		manualAdvise.setBalanceAmt(manualAdvise.getAdviseAmount());
		manualAdvise.setVersion(1);
		manualAdvise.setLastMntBy(finMain.getLastMntBy());
		manualAdvise.setLastMntOn(finMain.getLastMntOn());
		manualAdvise.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		manualAdvise.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		manualAdvise.setNewRecord(true);
		manualAdvise.setUserDetails(finMain.getUserDetails());
		manualAdvise.setFinSource(UploadConstants.FINSOURCE_ID_CD_UPLOAD);
		manualAdvise.setFeeType(modelFeeType);
		AuditHeader auditHeader = getAuditHeader(manualAdvise, PennantConstants.TRAN_WF);
		manualAdviseService.doApprove(auditHeader);
		logger.debug(Literal.LEAVING);
	}

	private AuditHeader getAuditHeader(ManualAdvise aManualAdvise, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aManualAdvise.getBefImage(), aManualAdvise);
		return new AuditHeader(String.valueOf(aManualAdvise.getAdviseID()), String.valueOf(aManualAdvise.getAdviseID()),
				null, null, auditDetail, aManualAdvise.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());
	}

	public FeeTypeService getFeeTypeService() {
		return feeTypeService;
	}

	public void setFeeTypeService(FeeTypeService feeTypeService) {
		this.feeTypeService = feeTypeService;
	}

	public ManualAdviseService getManualAdviseService() {
		return manualAdviseService;
	}

	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}
}
