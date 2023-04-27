package com.pennant.pff.customer.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.KycDetailsUploadDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.GuarantorDetailDAO;
import com.pennant.backend.dao.finance.JointAccountDetailDAO;
import com.pennant.backend.model.bulkAddressUpload.CustomerKycDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.customer.exception.CustomerDetailsUploadError;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.RequestSource;

public class KycDetailsUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(KycDetailsUploadServiceImpl.class);

	@Autowired
	protected KycDetailsUploadDAO kycDetailsUploadDAO;
	@Autowired
	private FinanceMainDAO financeMainDAO;
	@Autowired
	private CustomerDAO customerDAO;
	@Autowired
	private ValidateRecord kycDetailsUploadValidateRecord;
	@Autowired
	private GuarantorDetailDAO guarantorDetailDAO;
	@Autowired
	private JointAccountDetailDAO jointAccountDetailDAO;
	@Autowired
	private CustomerAddressUpload customerAddressUpload;
	@Autowired
	private CustomerPhoneNumberUpload customerPhoneNumberUpload;
	@Autowired
	private CustomerEmailUpload customerEmailUpload;

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		logger.debug(Literal.ENTERING);

		new Thread(() -> {
			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
					TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				List<CustomerKycDetail> details = kycDetailsUploadDAO.loadRecordData(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				try {
					for (CustomerKycDetail detail : details) {
						doValidate(header, detail);

						if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
							failRecords++;
							continue;
						}

						detail.setUserDetails(header.getUserDetails());
						detail.setSource(RequestSource.UPLOAD.name());

						if (detail.getCustAddrPriority() != 0) {
							customerAddressUpload.process(detail);
							if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
								failRecords++;
								continue;
							}
						}

						if (detail.getPhoneTypePriority() != 0) {
							customerPhoneNumberUpload.process(detail);
							if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
								failRecords++;
								continue;
							}
						}

						if (detail.getCustEMailPriority() != 0) {
							customerEmailUpload.process(detail);
							if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
								failRecords++;
								continue;
							}
						}

						sucessRecords++;
						detail.setProgress(EodConstants.PROGRESS_SUCCESS);
						detail.setErrorCode("");
						detail.setErrorDesc("");
					}

					txStatus = transactionManager.getTransaction(txDef);
					kycDetailsUploadDAO.update(details);
					transactionManager.commit(txStatus);
				} catch (Exception e) {
					logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}
				} finally {
					txStatus = null;
				}

				header.setSuccessRecords(sucessRecords);
				header.setFailureRecords(failRecords);

				StringBuilder remarks = new StringBuilder("Process Completed");

				if (failRecords > 0) {
					remarks.append(" with exceptions, ");
				}

				remarks.append(" Total Records : ").append(header.getTotalRecords());
				remarks.append(" Success Records : ").append(sucessRecords);
				remarks.append(" Failed Records : ").append(failRecords);
			}

			try {
				txStatus = transactionManager.getTransaction(txDef);

				updateHeader(headers, true);

				transactionManager.commit(txStatus);
			} catch (Exception e) {
				logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

				if (txStatus != null) {
					transactionManager.rollback(txStatus);
				}
			} finally {
				txStatus = null;
			}

		}).start();

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;
		try {
			txStatus = transactionManager.getTransaction(txDef);

			kycDetailsUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

			headers.forEach(h1 -> h1.setRemarks(ERR_DESC));
			updateHeader(headers, false);

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
		}
	}

	@Override
	public boolean isInProgress(Long headerID, Object... args) {
		return kycDetailsUploadDAO.isInProgress((String) args[0], headerID);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		CustomerKycDetail detail = null;

		if (object instanceof CustomerKycDetail) {
			detail = (CustomerKycDetail) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		String reference = detail.getReference();

		logger.info("Validating the Data for the reference {}", reference);

		detail.setHeaderId(header.getId());

		if (StringUtils.isBlank(reference)) {
			setError(detail, CustomerDetailsUploadError.KYC_CUST_01);
			return;
		}

		long custId = customerDAO.getCustIDByCIF(reference);

		if (custId == 0) {
			setError(detail, CustomerDetailsUploadError.KYC_CUST_02);
			return;
		}

		Long finID = financeMainDAO.getFinID(detail.getFinReference());

		if (finID == null) {
			setError(detail, CustomerDetailsUploadError.KYC_ADD_02);
			return;
		}

		if (kycDetailsUploadDAO.isInMaintanance(reference)) {
			setError(detail, CustomerDetailsUploadError.CUST_MNTS_01, reference);
			return;
		}

		if (kycDetailsUploadDAO.isInLoanQueue(custId)) {
			setError(detail, CustomerDetailsUploadError.CUST_MNTS_02, reference);
			return;
		}

		if (kycDetailsUploadDAO.isInReceiptQueue(custId)) {
			setError(detail, CustomerDetailsUploadError.CUST_MNTS_03, detail.getFinReference());
			return;
		}

		detail.setReferenceID(custId);

		FinanceMain fm = financeMainDAO.getFinanceMain(detail.getFinReference(), header.getEntityCode());

		if (fm == null) {
			setError(detail, CustomerDetailsUploadError.KYC_ADD_02);
			return;
		}

		if (!fm.isFinIsActive()) {
			setError(detail, CustomerDetailsUploadError.KYC_ADD_03);
			return;
		}

		if (!(fm.getCustID() == custId || guarantorDetailDAO.isGuarantor(finID, reference)
				|| jointAccountDetailDAO.isCoApplicant(finID, reference))) {
			setError(detail, CustomerDetailsUploadError.KYC_FIN_01);
			return;
		}

		validateMandatory(detail);

		if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
			return;
		}

		validateNonMandatory(detail);

		if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
			return;
		}

		if (StringUtils.isNotEmpty(detail.getCustAddrType())) {
			customerAddressUpload.validate(detail);

			if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
				return;
			}
		} else if (StringUtils.isNotEmpty(detail.getCustAddrZIP()) || detail.getCustAddrPriority() != 0) {
			setError(detail, CustomerDetailsUploadError.KYC_ADD_10);
			return;
		}

		if (detail.getPhoneTypeCode() != null) {
			customerPhoneNumberUpload.validate(detail);
			if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
				return;
			}
		} else if (StringUtils.isNotEmpty(detail.getPhoneNumber()) || detail.getPhoneTypePriority() != 0) {
			setError(detail, CustomerDetailsUploadError.KYC_PHONE_04);
			return;
		}

		if (detail.getCustEMailTypeCode() != null) {
			customerEmailUpload.validate(detail);
			if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
				return;
			}
		} else if (StringUtils.isNotEmpty(detail.getCustEMail()) || detail.getCustEMailPriority() != 0) {
			setError(detail, CustomerDetailsUploadError.KYC_MAIL_03);
			return;
		}

		detail.setProgress(EodConstants.PROGRESS_SUCCESS);
		detail.setErrorCode("");
		detail.setErrorDesc("");
	}

	private void validateMandatory(CustomerKycDetail detail) {
		if (StringUtils.isNotEmpty(detail.getCustAddrType())) {
			if (StringUtils.isEmpty(detail.getCustAddrType())) {
				setError(detail, CustomerDetailsUploadError.KYC_ADD_04);
				return;
			}

			if (detail.getCustAddrPriority() == 0) {
				setError(detail, CustomerDetailsUploadError.KYC_ADD_04);
				return;
			}

			if (StringUtils.isEmpty(detail.getCustAddrHNbr())) {
				setError(detail, CustomerDetailsUploadError.KYC_ADD_05);
				return;
			}

			if (StringUtils.isEmpty(detail.getCustAddrStreet())) {
				setError(detail, CustomerDetailsUploadError.KYC_ADD_06);
				return;
			}

			if (StringUtils.isEmpty(detail.getCustAddrZIP())) {
				setError(detail, CustomerDetailsUploadError.KYC_ADD_07);
				return;
			}
		}

		if (StringUtils.isNotEmpty(detail.getPhoneTypeCode())) {
			if (StringUtils.isEmpty(detail.getPhoneNumber())) {
				setError(detail, CustomerDetailsUploadError.KYC_PHONE_01);
				return;
			}

			if (detail.getPhoneTypePriority() == 0) {
				setError(detail, CustomerDetailsUploadError.KYC_PHONE_02);
				return;
			}
		}

		if (StringUtils.isNotEmpty(detail.getCustEMailTypeCode())) {
			if (StringUtils.isEmpty(detail.getCustEMail())) {
				setError(detail, CustomerDetailsUploadError.KYC_MAIL_01);
				return;
			}

			if (detail.getCustEMailPriority() == 0) {
				setError(detail, CustomerDetailsUploadError.KYC_MAIL_02);
			}
		}
	}

	private void validateNonMandatory(CustomerKycDetail detail) {
		if (getLength(detail.getCustAddrLine3()) > 50) {
			setError(detail, CustomerDetailsUploadError.KYC_ADD_11, " Care Of");
			return;
		}

		if (getLength(detail.getCustAddrHNbr()) > 50) {
			setError(detail, CustomerDetailsUploadError.KYC_ADD_11, "House/Building No");
			return;
		}

		if (getLength(detail.getCustFlatNbr()) > 50) {
			setError(detail, CustomerDetailsUploadError.KYC_ADD_11, "Flat No");
			return;
		}

		if (getLength(detail.getCustAddrStreet()) > 50) {
			setError(detail, CustomerDetailsUploadError.KYC_ADD_11, "Street");
			return;
		}

		if (getLength(detail.getCustAddrLine1()) > 50) {
			setError(detail, CustomerDetailsUploadError.KYC_ADD_11, "Landmark");
			return;
		}

		if (getLength(detail.getCustAddrLine2()) > 50) {
			setError(detail, CustomerDetailsUploadError.KYC_ADD_11, "Locality");
			return;
		}

		if (getLength(detail.getCustAddrLine4()) > 50) {
			setError(detail, CustomerDetailsUploadError.KYC_ADD_11, " Sub District");
			return;
		}

		if (getLength(detail.getCustDistrict()) > 50) {
			setError(detail, CustomerDetailsUploadError.KYC_ADD_11, "District");
			return;
		}
	}

	@Override
	public String getSqlQuery() {
		return kycDetailsUploadDAO.getSqlQuery();
	}

	@Override
	public ValidateRecord getValidateRecord() {
		return kycDetailsUploadValidateRecord;
	}

	protected void setError(CustomerKycDetail detail, String code, String... parms) {
		ErrorDetail errorDetail = ErrorUtil.getErrorDetail(new ErrorDetail(code, parms));

		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(errorDetail.getCode());
		detail.setErrorDesc(errorDetail.getError());
	}

	protected void setError(CustomerKycDetail detail, CustomerDetailsUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	protected void setError(CustomerKycDetail detail, CustomerDetailsUploadError error, String arg) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description().concat(arg));
	}

	private int getLength(String str) {
		return str == null ? 0 : str.length();
	}
}
