package com.pennant.pff.fee.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.cronutils.utils.StringUtils;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinFeeReceiptDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.util.RepayConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public class IMDFeeService {

	private RuleDAO ruleDAO;
	private FinFeeReceiptDAO finFeeReceiptDAO;

	public IMDFeeService() {
		super();
	}

	public ErrorDetail validate(AuditHeader auditHeader) {
		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		List<FinFeeDetail> finFeeDetailActualList = schdData.getFinFeeDetailList();
		AuditDetail auditDetail = auditHeader.getAuditDetail();
		String clientName = SysParamUtil.getValueAsString("CLIENT_CODE");

		if (StringUtils.isEmpty(clientName)) {
			clientName = "Habitat";
		}

		String feeType = "IMD";
		if ("Habitat".equals(clientName)) {
			feeType = "PROCFEE";
		} else if ("Save".equals(clientName)) {
			feeType = "IMD";
		}

		ErrorDetail error = null;
		if (CollectionUtils.isNotEmpty(finFeeDetailActualList)) {
			for (FinFeeDetail finFee : finFeeDetailActualList) {
				if (finFee.getFeeTypeCode().equals(feeType)) {

					List<Rule> ruleList = ruleDAO.getRuleByModuleAndEvent(RuleConstants.MODULE_FEES, "IMDFEE", "");
					if (CollectionUtils.isEmpty(ruleList)) {
						return error;
					}

					Map<String, Object> paramMap = new HashMap<>();
					paramMap.put("fee_" + feeType + "Paid", CurrencyUtil.parse(finFee.getPaidAmount(), 2));
					paramMap.put("fee_" + feeType + "ActualAmount", CurrencyUtil.parse(finFee.getActualAmount(), 2));
					paramMap.put("fm_finType", fm.getFinType());

					if (finFee.getPaidAmount().compareTo(BigDecimal.ZERO) == 0
							&& ImplementationConstants.ALLOW_IMD_WITHOUT_REALIZED) {

						List<FinFeeReceipt> finFeeReceipts = this.finFeeReceiptDAO
								.getFinFeeReceiptByFeeType(fm.getFinReference(), finFee.getFeeTypeCode());

						BigDecimal paidAmount = BigDecimal.ZERO;
						for (FinFeeReceipt feeReceipt : finFeeReceipts) {
							if (RepayConstants.PAYTYPE_CHEQUE.equals(feeReceipt.getReceiptType())
									|| RepayConstants.PAYTYPE_DD.equals(feeReceipt.getReceiptType())) {
								paidAmount = paidAmount.add(feeReceipt.getPaidAmount());
							}
						}
						paramMap.put("fee_" + feeType + "Paid", CurrencyUtil.parse(paidAmount, 2));
					}

					BigDecimal result = RuleExecutionUtil.getRuleResult(ruleList.get(0).getSQLRule(), paramMap, null);

					if (result.compareTo(BigDecimal.ONE) == 0) {
						String[] param = new String[1];
						param[0] = "Minimum Amount of " + finFee.getFeeTypeCode() + " fee must paid.";
						error = new ErrorDetail("92021", param);

					} else if (result.compareTo(BigDecimal.ONE) > 0) {
						String[] param = new String[4];
						param[0] = "Minimum Amount of ";
						param[1] = finFee.getFeeTypeCode();
						param[2] = " fee must paid.";
						param[3] = "";
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

	public RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public FinFeeReceiptDAO getFinFeeReceiptDAO() {
		return finFeeReceiptDAO;
	}

	public void setFinFeeReceiptDAO(FinFeeReceiptDAO finFeeReceiptDAO) {
		this.finFeeReceiptDAO = finFeeReceiptDAO;
	}

}
