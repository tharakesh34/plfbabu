package com.pennant.backend.service.finance.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

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
	@Autowired
	private VASRecordingDAO vasRecordingDAO;

	public PricingDetailServiceImpl() {
		super();
	}

	/**
	 * It fetches the records from RMTFinanceType_View and other details
	 * 
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public FinanceType getFinanceTypeById(String finType) {
		logger.debug(Literal.ENTERING);

		FinanceType financeType = getFinanceTypeDAO().getFinanceTypeByID(finType, "_View");

		if (financeType != null) {
			financeType.setFinTypeFeesList(getFinTypeFeesDAO().getFinTypeFeesList(finType,
					AccountingEvent.ADDDBSP, "_AView", true, FinanceConstants.MODULEID_FINTYPE));
			financeType.setFinTypeVASProductsList(getFinTypeVASProductsDAO().getVASProductsByFinType(finType, "_View"));

		}
		logger.debug(Literal.LEAVING);

		return financeType;
	}

	@Override
	public List<FinFeeDetail> getFinFeeDetailById(String finReference, boolean isWIF, String type) {
		logger.debug("Entering");
		List<FinFeeDetail> finFeeDetails = getFinFeeDetailDAO().getFinFeeDetailByFinRef(finReference, isWIF, type);
		logger.debug("Leaving");
		return finFeeDetails;
	}

	@Override
	public List<VASRecording> getVASRecordingsByLinkRef(String finReference, String type) {
		logger.debug("Entering");
		List<VASRecording> vASRecordings = getVasRecordingDAO().getVASRecordingsByLinkRef(finReference, type);
		logger.debug("Leaving");
		return vASRecordings;
	}

	@Override
	public List<FinanceMain> getFinanceMains(String finReference, String type) {
		logger.debug("Entering");
		List<FinanceMain> finMains = getFinanceMainDAO().getFinanceByInvReference(finReference, type);
		logger.debug("Leaving");
		return finMains;
	}

	@Override
	public String getConfiguredTopUpFintype(String finType) {
		return pricingDetailDAO.getConfiguredTopUpFinType(finType);
	}

	@Override
	public List<String> getInvestmentRefifAny(String finReference, String type) {
		return getFinanceMainDAO().getInvestmentFinRef(finReference, type);
	}

	@Override
	public List<String> getParentRefifAny(String finReference, String type) {
		return getFinanceMainDAO().getParentRefifAny(finReference, type, false);
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public PricingDetailDAO getPricingDetailDAO() {
		return pricingDetailDAO;
	}

	public void setPricingDetailDAO(PricingDetailDAO pricingDetailDAO) {
		this.pricingDetailDAO = pricingDetailDAO;
	}

	public FinTypeFeesDAO getFinTypeFeesDAO() {
		return finTypeFeesDAO;
	}

	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}

	@Override
	public List<FinTypeVASProducts> getVASProductsByFinType(String finType) {
		return getFinTypeVASProductsDAO().getVASProductsByFinType(finType, "_View");
	}

	public FinTypeVASProductsDAO getFinTypeVASProductsDAO() {
		return finTypeVASProductsDAO;
	}

	public void setFinTypeVASProductsDAO(FinTypeVASProductsDAO finTypeVASProductsDAO) {
		this.finTypeVASProductsDAO = finTypeVASProductsDAO;
	}

	public FinFeeDetailDAO getFinFeeDetailDAO() {
		return finFeeDetailDAO;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public VASRecordingDAO getVasRecordingDAO() {
		return vasRecordingDAO;
	}

	public void setVasRecordingDAO(VASRecordingDAO vasRecordingDAO) {
		this.vasRecordingDAO = vasRecordingDAO;
	}

}
