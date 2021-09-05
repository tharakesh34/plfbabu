package com.pennant.backend.service.finance.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinTypeVASProductsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.PricingDetailDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.finance.PricingDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;

public class PricingDetailServiceImpl implements PricingDetailService {
	private static Logger logger = LogManager.getLogger(PricingDetailServiceImpl.class);

	private FinanceTypeDAO financeTypeDAO;
	private FinTypeFeesDAO finTypeFeesDAO;
	private PricingDetailDAO pricingDetailDAO;
	private FinTypeVASProductsDAO finTypeVASProductsDAO;
	private FinFeeDetailDAO finFeeDetailDAO;
	private FinanceMainDAO financeMainDAO;
	private VASRecordingDAO vasRecordingDAO;

	public PricingDetailServiceImpl() {
		super();
	}

	@Override
	public FinanceType getFinanceTypeById(String finType) {
		logger.debug(Literal.ENTERING);

		FinanceType financeType = financeTypeDAO.getFinanceTypeByID(finType, "_View");

		if (financeType != null) {
			financeType.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesList(finType, AccountingEvent.ADDDBSP, "_AView",
					true, FinanceConstants.MODULEID_FINTYPE));
			financeType.setFinTypeVASProductsList(finTypeVASProductsDAO.getVASProductsByFinType(finType, "_View"));

		}
		logger.debug(Literal.LEAVING);

		return financeType;
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailById(long finID, boolean isWIF, String type) {
		logger.debug(Literal.ENTERING);
		List<FinFeeDetail> finFeeDetails = finFeeDetailDAO.getFinFeeDetailByFinRef(finID, isWIF, type);
		logger.debug(Literal.LEAVING);
		return finFeeDetails;
	}

	@Override
	public List<VASRecording> getVASRecordingsByLinkRef(String finID, String type) {
		logger.debug(Literal.ENTERING);
		List<VASRecording> vASRecordings = vasRecordingDAO.getVASRecordingsByLinkRef(finID, type);
		logger.debug(Literal.LEAVING);
		return vASRecordings;
	}

	@Override
	public List<FinanceMain> getFinanceMains(long finID, String type) {
		logger.debug(Literal.ENTERING);
		List<FinanceMain> finMains = financeMainDAO.getFinanceByInvReference(finID, type);
		logger.debug(Literal.LEAVING);
		return finMains;
	}

	@Override
	public String getConfiguredTopUpFintype(String finType) {
		return pricingDetailDAO.getConfiguredTopUpFinType(finType);
	}

	@Override
	public List<Long> getInvestmentRefifAny(String investmentRef, String type) {
		return financeMainDAO.getInvestmentFinRef(investmentRef, type);
	}

	@Override
	public List<Long> getParentRefifAny(String parentRef, String type) {
		return financeMainDAO.getParentRefifAny(parentRef, type, false);
	}

	@Override
	public List<FinTypeVASProducts> getVASProductsByFinType(String finType) {
		return finTypeVASProductsDAO.getVASProductsByFinType(finType, "_View");
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}

	public void setPricingDetailDAO(PricingDetailDAO pricingDetailDAO) {
		this.pricingDetailDAO = pricingDetailDAO;
	}

	public void setFinTypeVASProductsDAO(FinTypeVASProductsDAO finTypeVASProductsDAO) {
		this.finTypeVASProductsDAO = finTypeVASProductsDAO;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setVasRecordingDAO(VASRecordingDAO vasRecordingDAO) {
		this.vasRecordingDAO = vasRecordingDAO;
	}

}
