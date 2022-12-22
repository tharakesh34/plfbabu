package com.pennant.pff.fee.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class IMDFeeService {

	private RuleDAO ruleDAO;

	public IMDFeeService() {
		super();
	}

	public ErrorDetail validate(AuditHeader auditHeader) {
		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		List<FinFeeDetail> finFeeDetailActualList = schdData.getFinFeeDetailList();
		AuditDetail auditDetail = auditHeader.getAuditDetail();

		ErrorDetail error = null;
		if (CollectionUtils.isNotEmpty(finFeeDetailActualList)) {
			for (FinFeeDetail finFee : finFeeDetailActualList) {
				if (finFee.getFeeTypeCode().equals("IMD")) {

					List<Rule> ruleList = ruleDAO.getRuleByModuleAndEvent(RuleConstants.MODULE_FEES, "IMDFEE", "");
					if (CollectionUtils.isEmpty(ruleList)) {
						return error;
					}

					Map<String, Object> paramMap = new HashMap<>();
					paramMap.put("fee_IMDPaid", formateAmount(finFee.getPaidAmount(), 2));
					paramMap.put("fee_IMDActualAmount", formateAmount(finFee.getActualAmount(), 2));
					paramMap.put("fm_finType", fm.getFinType());

					BigDecimal result = RuleExecutionUtil.getRuleResult(ruleList.get(0).getSQLRule(), paramMap, null);

					if (result.compareTo(BigDecimal.ONE) == 0) {
						String[] param = new String[1];
						param[0] = "Minimum Paid Amount of " + finFee.getFeeTypeCode() + " of Rs "
								+ formateAmount(finFee.getActualAmount(), 2);
						error = new ErrorDetail("92021", param);

					} else if (result.compareTo(BigDecimal.ONE) > 0) {
						String[] param = new String[4];
						param[0] = "Minimum Paid Amount of ";
						param[1] = finFee.getFeeTypeCode();
						param[2] = " of Rs ";
						param[3] = "" + formateAmount(finFee.getActualAmount(), 2);
						error = new ErrorDetail("21005", param);
					}

					if (error != null) {
						auditDetail.setErrorDetail(error);
						auditHeader.setAuditDetail(auditDetail);
					}
				}
			}

		}

		return error;
	}

	public static BigDecimal formateAmount(BigDecimal amount, int dec) {
		BigDecimal bigDecimal = BigDecimal.ZERO;

		if (amount != null) {
			bigDecimal = amount.divide(new BigDecimal(Math.pow(10, dec)));
		}
		return bigDecimal;
	}

	public RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

}
