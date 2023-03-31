package com.pennant.backend.service.customermasters.validation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.zkoss.lang.Objects;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.CustomerExtLiabilityDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.ExtLiabilityPaymentdetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.pff.sampling.dao.SamplingDAO;

public class CustomerExtLiabilityValidation {
	private CustomerExtLiabilityDAO customerExtLiabilityDAO;
	protected SamplingDAO samplingDAO;
	private CustomerDAO customerDAO;

	public CustomerExtLiabilityValidation(CustomerExtLiabilityDAO customerExtLiabilityDAO) {
		this.customerExtLiabilityDAO = customerExtLiabilityDAO;
		samplingDAO = (SamplingDAO) SpringBeanUtil.getBean("samplingDAO");
		customerDAO = (CustomerDAO) SpringBeanUtil.getBean("customerDAO");
	}

	public AuditHeader extLiabilityValidation(AuditHeader auditHeader, String method) {
		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), 0, method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> extLiabilityListValidation(List<AuditDetail> auditDetails, long samplingId, String method,
			String usrLanguage) {
		if (CollectionUtils.isNotEmpty(auditDetails)) {
			List<AuditDetail> details = new ArrayList<>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail = validate(auditDetails.get(i), samplingId, method, usrLanguage);
				details.add(auditDetail);
			}
			return details;
		}
		return new ArrayList<>();
	}

	private AuditDetail validate(AuditDetail auditDetail, long samplingId, String method, String usrLanguage) {
		CustomerExtLiability liability = (CustomerExtLiability) auditDetail.getModelData();
		CustomerExtLiability tempLiability = null;

		if ("sampling".equals(liability.getInputSource())) {
			liability.setLinkId(samplingDAO.getLiabilityLinkId(samplingId, liability.getCustId()));
		} else {
			liability.setLinkId(customerExtLiabilityDAO.getLinkId(liability.getCustId()));
		}

		if (liability.isWorkflow()) {
			tempLiability = customerExtLiabilityDAO.getLiability(liability, "_temp", liability.getInputSource());
		}

		CustomerExtLiability beforeLiability = customerExtLiabilityDAO.getLiability(liability, "",
				liability.getInputSource());
		CustomerExtLiability oldLiability = liability.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(liability.getCustCif());
		valueParm[1] = String.valueOf(liability.getSeqNo());

		errParm[0] = App.getLabel("CustomerExtLiability") + " , " + App.getLabel("label_CustCIF") + ":" + valueParm[0]
				+ " and ";
		errParm[1] = App.getLabel("label_LiabilitySeq") + "-" + valueParm[1];

		String errorCode = null;

		if (liability.isNewRecord()) {
			if (!liability.isWorkflow()) {
				if (beforeLiability != null) {
					errorCode = "41001";
					auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, errorCode, errParm, null));
				}
			} else {
				if (liability.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (beforeLiability != null || tempLiability != null) {
						errorCode = "41001";
					}
				} else {
					if (beforeLiability == null || tempLiability != null) {
						errorCode = "41005";
					}
				}
			}
		} else {
			if (!liability.isWorkflow()) {
				if (oldLiability == null) {
					errorCode = "41002";
				} else {
					if (oldLiability != null && !oldLiability.getLastMntOn().equals(beforeLiability.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							errorCode = "41003";
						} else {
							errorCode = "41004";
						}
					}
				}
			} else {

				if (tempLiability == null) {
					errorCode = "41005";
				}

				if (tempLiability != null && oldLiability != null
						&& !oldLiability.getLastMntOn().equals(tempLiability.getLastMntOn())) {
					errorCode = "41005";
				}

			}
		}

		if (errorCode != null) {
			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, errorCode, errParm, null));
		}

		auditDetail.setErrorDetail(screenValidations(liability));

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !liability.isWorkflow()) {
			liability.setBefImage(beforeLiability);
		}

		return auditDetail;
	}

	/**
	 * Method For Screen Level Validations
	 * 
	 * @param auditHeader
	 * @param usrLanguage
	 * @return
	 */
	public ErrorDetail screenValidations(CustomerExtLiability liability) {
		return null;
	}

	/**
	 * Validate CustomerExtLiability.
	 * 
	 * @param customerExtLiability
	 * @return AuditDetail
	 */
	public AuditDetail doValidations(CustomerExtLiability liability) {
		AuditDetail auditDetail = new AuditDetail();
		ErrorDetail errorDetail = new ErrorDetail();

		// validate Master code with PLF system masters
		if (liability.getFinDate().compareTo(SysParamUtil.getAppDate()) >= 0
				|| SysParamUtil.getValueAsDate("APP_DFT_START_DATE").compareTo(liability.getFinDate()) >= 0) {
			String[] valueParm = new String[3];
			valueParm[0] = "FinDate";
			valueParm[1] = DateUtility.format(SysParamUtil.getValueAsDate("APP_DFT_START_DATE"),
					PennantConstants.XMLDateFormat);
			valueParm[2] = DateUtility.format(SysParamUtil.getAppDate(), PennantConstants.XMLDateFormat);
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90318", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
		}

		if (!customerExtLiabilityDAO.isBankExists(liability.getLoanBank())) {
			String[] valueParm = new String[2];
			valueParm[0] = "BankCode";
			valueParm[1] = liability.getLoanBank();
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		}
		if (!customerExtLiabilityDAO.isFinTypeExists(liability.getFinType())) {
			String[] valueParm = new String[2];
			valueParm[0] = "FinType";
			valueParm[1] = liability.getFinType();
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		}

		if (!customerExtLiabilityDAO.isFinStatuExists(liability.getFinStatus())) {
			String[] valueParm = new String[2];
			valueParm[0] = "FinStatus";
			valueParm[1] = liability.getFinStatus();
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm), "EN");
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		}

		if (StringUtils.isNotBlank(String.valueOf(liability.getSource()))) {
			final List<ValueLabel> sourceInfoList = PennantStaticListUtil.getSourceInfoList();
			boolean isSource = false;
			for (ValueLabel source : sourceInfoList) {
				if (liability.getSource() == Integer.valueOf(source.getValue())) {
					isSource = true;
					break;
				}
			}
			if (!isSource) {
				String[] valueParam = new String[2];
				valueParam[0] = "source";
				valueParam[1] = String.valueOf(liability.getSource());
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90224", "", valueParam));
				auditDetail.setErrorDetail(errorDetail);
			}
		}

		if (StringUtils.isNotBlank(String.valueOf(liability.getCheckedBy()))) {
			final List<ValueLabel> trackCheckList = PennantStaticListUtil.getTrackCheckList();
			boolean isCheckedBy = false;
			for (ValueLabel checkedBy : trackCheckList) {
				if (liability.getCheckedBy() == Integer.valueOf(checkedBy.getValue())) {
					isCheckedBy = true;
					break;
				}
			}
			if (!isCheckedBy) {
				String[] valueParam = new String[2];
				valueParam[0] = "checkedBy";
				valueParam[1] = String.valueOf(liability.getCheckedBy());
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90224", "", valueParam));
				auditDetail.setErrorDetail(errorDetail);
			}
		}

		if (liability.getLoanPurpose() == null) {
			String[] valueParam = new String[1];
			valueParam[0] = "End of  Funds";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParam));
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		}

		if (liability.getRepayBank() == null) {
			String[] valueParam = new String[1];
			valueParam[0] = "Repayment";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParam));
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;
		}

		if (liability.getRateOfInterest().compareTo(BigDecimal.ZERO) <= 0) {
			String[] valueParam = new String[2];
			valueParam[0] = "ROI";
			valueParam[1] = "Zero";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParam));
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;

		}

		if (liability.getPrincipalOutstanding().compareTo(BigDecimal.ZERO) <= 0) {
			String[] valueParam = new String[2];
			valueParam[0] = "POS";
			valueParam[1] = "Zero";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParam));
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;

		}

		if (liability.getMob() < 0) {
			String[] valueParam = new String[2];
			valueParam[0] = "Months On Book(MOB)";
			valueParam[1] = "Zero";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParam));
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;

		}

		if (liability.getImputedEmi().compareTo(BigDecimal.ZERO) < 0) {
			String[] valueParam = new String[2];
			valueParam[0] = "ImputedEmI";
			valueParam[1] = "Zero";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParam));
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;

		}

		if (liability.getOverdueAmount().compareTo(BigDecimal.ZERO) < 0) {
			String[] valueParam = new String[2];
			valueParam[0] = "OverdueAmount";
			valueParam[1] = "Zero";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParam));
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;

		}

		if (liability.getCurrentOverDue().compareTo(BigDecimal.ZERO) < 0) {
			String[] valueParam = new String[2];
			valueParam[0] = "CurrentOverDue";
			valueParam[1] = "Zero";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParam));
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;

		}

		if (liability.getNoOfBouncesInSixMonths() < 0) {
			String[] valueParam = new String[2];
			valueParam[0] = "NoOfBouncesInSixMonths";
			valueParam[1] = "Zero";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParam));
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;

		}

		if (liability.getNoOfBouncesInTwelveMonths() < 0) {
			String[] valueParam = new String[2];
			valueParam[0] = "NoOfBouncesInTwelveMonths";
			valueParam[1] = "Zero";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParam));
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;

		}

		if (liability.getBalanceTenure() <= 0) {
			String[] valueParam = new String[2];
			valueParam[0] = "BalanceTenure";
			valueParam[1] = "Zero";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParam));
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;

		}

		if (liability.getOutstandingBalance().compareTo(BigDecimal.ZERO) <= 0) {
			String[] valueParam = new String[2];
			valueParam[0] = "OutstandingBalance";
			valueParam[1] = "Zero";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParam));
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;

		}

		if (liability.getOriginalAmount().compareTo(BigDecimal.ZERO) <= 0) {
			String[] valueParam = new String[2];
			valueParam[0] = "OriginalAmount";
			valueParam[1] = "Zero";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParam));
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;

		}

		if (liability.getBounceInstalments() < 0) {
			String[] valueParam = new String[2];
			valueParam[0] = " Number of Bounces in last 3 months(bounceInstalments)";
			valueParam[1] = "Zero";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91121", "", valueParam));
			auditDetail.setErrorDetail(errorDetail);
			return auditDetail;

		}

		if (StringUtils.isNotBlank(liability.getLoanPurpose())) {
			auditDetail
					.setErrorDetail(validateMasterCode("LoanPurposes", "LoanPurposeCode", liability.getLoanPurpose()));
		}

		if (StringUtils.isNotBlank(liability.getRepayBank())) {
			auditDetail.setErrorDetail(validateMasterCode("BMTBankDetail", "BankCode", liability.getRepayBank()));
		}
		if (liability.getTenure() <= 0) {
			String[] valueParam = new String[1];
			valueParam[0] = "tenure";
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParam));
			auditDetail.setErrorDetail(errorDetail);
		} else {
			if (liability.getExtLiabilitiesPayments().size() != liability.getTenure()) {
				String[] valueParm = new String[2];
				valueParm[0] = "No of instalment Details ";
				valueParm[1] = "Tenure";
				errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90220", "", valueParm));
				auditDetail.setErrorDetail(errorDetail);
			} else {
				for (ExtLiabilityPaymentdetails extLiabilityPaymentdetails : liability.getExtLiabilitiesPayments()) {
					if (StringUtils.isEmpty(extLiabilityPaymentdetails.getEmiType())) {
						String[] valueParam = new String[2];
						valueParam[0] = "EMIType";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParam));
						auditDetail.setErrorDetail(errorDetail);
					}

					if ("#".equals(extLiabilityPaymentdetails.getEmiClearance())) {
						String[] valueParam = new String[2];
						valueParam[0] = "Emi Clearance";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParam));
						auditDetail.setErrorDetail(errorDetail);
					}

					int clearDay = extLiabilityPaymentdetails.getEmiClearedDay();
					if (PennantConstants.CLEARED.equals(extLiabilityPaymentdetails.getEmiClearance())
							&& clearDay <= 0) {
						String[] valueParam = new String[1];
						valueParam[0] = "EmiClearedDay";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("30561", "", valueParam));
						auditDetail.setErrorDetail(errorDetail);
					}

					if (clearDay <= 0 || clearDay > 31) {
						String[] valueParam = new String[3];
						valueParam[0] = "EmiClearedDay";
						valueParam[1] = "1";
						valueParam[2] = "31";
						errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("30567", "", valueParam));
						auditDetail.setErrorDetail(errorDetail);
					}

				}
				Date appDate = SysParamUtil.getAppDate();
				List<ExtLiabilityPaymentdetails> paymentDetails = getPaymentDetails(appDate, liability.getTenure());
				if (CollectionUtils.isNotEmpty(paymentDetails)) {
					for (int i = 0; i < liability.getExtLiabilitiesPayments().size(); i++) {
						int emiCount = 0;
						for (int j = 0; j < paymentDetails.size(); j++) {
							if (liability.getExtLiabilitiesPayments().get(i).getEmiType()
									.equals((paymentDetails.get(j).getEmiType()))) {
								emiCount++;
							}
						}
						if (emiCount == 0) {
							String[] valueParm = new String[2];
							valueParm[0] = "Emi Type";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("91123", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);
						}
					}
					for (ExtLiabilityPaymentdetails extLiabilityPaymentdetails : liability
							.getExtLiabilitiesPayments()) {

						if (!StringUtils.equals(extLiabilityPaymentdetails.getEmiClearance(),
								PennantConstants.WAITING_CLEARANCE)
								&& !StringUtils.equals(extLiabilityPaymentdetails.getEmiClearance(),
										PennantConstants.CLEARED)
								&& !StringUtils.equals(extLiabilityPaymentdetails.getEmiClearance(),
										PennantConstants.BOUNCED)) {
							String[] valueParm = new String[2];
							valueParm[0] = "Emi Clearance";
							errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90501", "", valueParm));
							auditDetail.setErrorDetail(errorDetail);

						}

					}
				}
			}
		}

		auditDetail.setErrorDetail(errorDetail);
		return auditDetail;
	}

	public List<ExtLiabilityPaymentdetails> getPaymentDetails(Date startDate, int noOfMonths) {
		Date dtStartDate = startDate;
		Date dtEndDate = DateUtility.addMonths(dtStartDate, -noOfMonths);
		List<ExtLiabilityPaymentdetails> months = getFrequency(dtStartDate, dtEndDate, noOfMonths);
		return months;
	}

	private List<ExtLiabilityPaymentdetails> getFrequency(final Date startDate, final Date endDate, int noOfMonths) {
		List<ExtLiabilityPaymentdetails> list = new ArrayList<>();
		if (startDate == null || endDate == null) {
			return list;
		}

		Date tempStartDate = (Date) startDate.clone();
		Date tempEndDate = (Date) endDate.clone();

		while (DateUtility.compare(tempStartDate, tempEndDate) > 0) {
			ExtLiabilityPaymentdetails temp = new ExtLiabilityPaymentdetails();
			String key = DateUtil.format(tempStartDate, DateFormat.LONG_MONTH);
			temp.setEmiType(key);
			tempStartDate = DateUtil.addMonths(tempStartDate, -1);
			list.add(temp);
		}

		return list;
	}

	private ErrorDetail validateMasterCode(String tableName, String columnName, Object value) {

		ErrorDetail errorDetail = new ErrorDetail();

		// validate Master code with PLF system masters
		int count = customerDAO.getLookupCount(tableName, columnName, value);
		if (count <= 0) {
			String[] valueParm = new String[2];
			valueParm[0] = columnName;
			valueParm[1] = Objects.toString(value);
			errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm));
		}

		return errorDetail;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

}
