package com.pennanttech.pennapps.pff.finsampling.service;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.finsampling.dao.FinSamplingDAO;
import com.pennanttech.pennapps.pff.sampling.dao.SamplingDAO;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.sampling.Decision;
import com.pennanttech.pff.service.sampling.SamplingService;

public class FinSamplingServiceImpl implements FinSamplingService {
	private static final Logger logger = LogManager.getLogger(FinSamplingServiceImpl.class);

	@Autowired
	private FinSamplingDAO finSamplingDAO;
	@Autowired
	private SamplingDAO samplingDAO;
	@Autowired
	private SamplingService samplingService;

	@Override
	public AuditDetail saveOrUpdate(FinanceDetail financeDetail, String auditTranType) {
		logger.debug(Literal.ENTERING);
		Sampling sampling = financeDetail.getSampling();
		String[] fields = PennantJavaUtil.getFieldDetails(sampling, sampling.getExcludeFields());
		finSamplingDAO.updateSampling(sampling, TableType.MAIN_TAB);
		if (sampling.getDecision() == Decision.RESUBMIT.getKey()
				&& !samplingService.isExist(sampling.getKeyReference(), "_Temp")) {
			samplingDAO.save(sampling, TableType.TEMP_TAB);

			for (CollateralSetup collateralSetup : sampling.getCollSetupList()) {
				finSamplingDAO.saveCollateral(sampling.getId(), collateralSetup.getCollateralType());
			}

		}

		finSamplingDAO.saveOrUpdateRemarks(sampling, TableType.MAIN_TAB);
		// finSamplingDAO.updateCollateralRemarks(sampling, TableType.MAIN_TAB);
		logger.debug(Literal.LEAVING);
		return new AuditDetail(auditTranType, 1, fields[0], fields[1], sampling.getBefImage(), sampling);
	}

}
