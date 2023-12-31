package com.pennant.pff.customer.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.customermasters.KycDetailsUploadDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.GuarantorDetailDAO;
import com.pennant.backend.dao.finance.JointAccountDetailDAO;
import com.pennant.backend.model.bulkaddressupload.CustomerKycDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.customer.exception.CustomerDetailsUploadError;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class KycDetailsUploadServiceImpl extends AUploadServiceImpl<CustomerKycDetail> {
	private static final Logger logger = LogManager.getLogger(KycDetailsUploadServiceImpl.class);

	@Autowired
	protected KycDetailsUploadDAO kycDetailsUploadDAO;
	@Autowired
	private FinanceMainDAO financeMainDAO;
	@Autowired
	private CustomerDAO customerDAO;
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

	public KycDetailsUploadServiceImpl() {
		super();
	}

	@Override
	protected CustomerKycDetail getDetail(Object object) {
		if (object instanceof CustomerKycDetail detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		logger.debug(Literal.ENTERING);

		new Thread(() -> {
			TransactionStatus txStatus = null;

			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				List<CustomerKycDetail> details = kycDetailsUploadDAO.loadRecordData(header.getId());
				header.getUploadDetails().addAll(details);
				header.setAppDate(appDate);

				try {
					for (CustomerKycDetail detail : details) {
						doValidate(header, detail);

						if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
							continue;
						}

						detail.setUserDetails(header.getUserDetails());
						detail.setSource(RequestSource.UPLOAD.name());

						if (detail.getCustAddrPriority() != 0) {
							customerAddressUpload.process(detail);
							if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
								continue;
							}
						}

						if (detail.getPhoneTypePriority() != 0) {
							customerPhoneNumberUpload.process(detail);
							if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
								continue;
							}
						}

						if (detail.getCustEMailPriority() != 0) {
							customerEmailUpload.process(detail);
							if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
								continue;
							}
						}

						setSuccesStatus(detail);
					}

					txStatus = getTransactionStatus();
					kycDetailsUploadDAO.update(details);
					transactionManager.commit(txStatus);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);

					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}
				} finally {
					txStatus = null;
				}
			}

			try {
				updateHeader(headers, true);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}).start();

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();

		try {
			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(kycDetailsUploadDAO.loadRecordData(h1.getId()));
			});

			kycDetailsUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

			updateHeader(headers, false);

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
		}
	}

	@Override
	public boolean isInProgress(Long headerID, Object... args) {
		return kycDetailsUploadDAO.isInProgress(headerID, (String) args[0]);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		CustomerKycDetail detail = getDetail(object);

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

		List<FinanceMain> fmList = kycDetailsUploadDAO.isInMaintanance(custId);

		if (CollectionUtils.isNotEmpty(fmList)) {
			StringBuilder message = new StringBuilder();

			for (FinanceMain fm : fmList) {
				if (message.length() > 0) {
					message.append(", ");
				}

				String rcdMaintainSts = fm.getRcdMaintainSts();

				if (StringUtils.isEmpty(rcdMaintainSts)) {
					rcdMaintainSts = FinServiceEvent.ORG;
				}

				message.append(fm.getFinReference() + " is in maintainance on  :" + rcdMaintainSts);
			}

			setFailureStatus(detail, "CUST_MNTS_08", message.toString());
			return;
		}

		List<String> references = kycDetailsUploadDAO.getReceiptQueueList(custId);

		if (CollectionUtils.isNotEmpty(references)) {
			setError(detail, CustomerDetailsUploadError.CUST_MNTS_03, references);
			return;
		}

		references = kycDetailsUploadDAO.isInSettlement(custId);

		if (CollectionUtils.isNotEmpty(references)) {
			setError(detail, CustomerDetailsUploadError.CUST_MNTS_06, references);
			return;
		}

		references = kycDetailsUploadDAO.isInlinkingDelinking(custId);

		if (CollectionUtils.isNotEmpty(references)) {
			setError(detail, CustomerDetailsUploadError.CUST_MNTS_07, references);
			return;
		}

		detail.setReferenceID(custId);

		FinanceMain fm = financeMainDAO.getFinanceMain(detail.getFinReference(), header.getEntityCode());

		if (fm == null) {
			setError(detail, CustomerDetailsUploadError.KYC_ADD_02);
			return;
		}

		if (fm.getRcdMaintainSts() != null) {
			setError(detail, CustomerDetailsUploadError.CUST_MNTS_04, fm.getRcdMaintainSts());
			return;
		}

		if (fm.isUnderSettlement()) {
			setError(detail, CustomerDetailsUploadError.CUST_MNTS_05);
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

		setSuccesStatus(detail);
	}

	private void setError(CustomerKycDetail detail, CustomerDetailsUploadError error, List<String> references) {
		StringBuilder message = new StringBuilder();
		for (String ref : references) {
			if (message.length() > 0) {
				message.append(", ");
			}

			message.append(ref);
		}

		setError(detail, error.name(), message.toString());
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
		}
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.CUSTOMER_KYC_DETAILS.name(), this, "KycDetailsUploadHeader");
	}

	@Override
	public String getSqlQuery() {
		return kycDetailsUploadDAO.getSqlQuery();
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		Long headerID = ObjectUtil.valueAsLong(attributes.getParameterMap().get("HEADER_ID"));

		if (headerID == null) {
			return;
		}

		FileUploadHeader header = (FileUploadHeader) attributes.getParameterMap().get("FILE_UPLOAD_HEADER");

		String custCif = ObjectUtil.valueAsString(paramSource.getValue("custCif"));

		String finReference = ObjectUtil.valueAsString(paramSource.getValue("finReference"));

		boolean recordExist = isInProgress(headerID, custCif);

		if (recordExist) {
			throw new AppException("Record is already initiated, unable to proceed.");
		}

		CustomerKycDetail detail = new CustomerKycDetail();
		detail.setHeaderId(headerID);
		detail.setFinReference(finReference);
		detail.setReference(custCif);

		detail.setCustAddrType(ObjectUtil.valueAsString(paramSource.getValue("custAddrType")));
		detail.setCustAddrPriority(ObjectUtil.valueAsInt(paramSource.getValue("custAddrPriority")));
		detail.setCustAddrLine3(ObjectUtil.valueAsString(paramSource.getValue("custAddrLine3")));
		detail.setCustAddrHNbr(ObjectUtil.valueAsString(paramSource.getValue("custAddrHNbr")));
		detail.setCustFlatNbr(ObjectUtil.valueAsString(paramSource.getValue("custFlatNbr")));
		detail.setCustAddrStreet(ObjectUtil.valueAsString(paramSource.getValue("custAddrStreet")));
		detail.setCustAddrLine1(ObjectUtil.valueAsString(paramSource.getValue("custAddrLine1")));
		detail.setCustAddrLine2(ObjectUtil.valueAsString(paramSource.getValue("custAddrLine2")));
		detail.setCustAddrCity(ObjectUtil.valueAsString(paramSource.getValue("custAddrCity")));
		detail.setCustAddrLine4(ObjectUtil.valueAsString(paramSource.getValue("custAddrLine4")));
		detail.setCustDistrict(ObjectUtil.valueAsString(paramSource.getValue("custDistrict")));
		detail.setCustAddrProvince(ObjectUtil.valueAsString(paramSource.getValue("custAddrProvince")));
		detail.setCustAddrCountry(ObjectUtil.valueAsString(paramSource.getValue("custAddrCountry")));
		detail.setCustAddrZIP(ObjectUtil.valueAsString(paramSource.getValue("custAddrZIP")));
		detail.setPhoneTypeCode(ObjectUtil.valueAsString(paramSource.getValue("phoneTypeCode")));
		detail.setPhoneNumber(ObjectUtil.valueAsString(paramSource.getValue("phoneNumber")));
		detail.setPhoneTypePriority(ObjectUtil.valueAsInt(paramSource.getValue("phoneTypePriority")));
		detail.setCustEMailTypeCode(ObjectUtil.valueAsString(paramSource.getValue("custEMailTypeCode")));
		detail.setCustEMail(ObjectUtil.valueAsString(paramSource.getValue("custEMail")));
		detail.setCustEMailPriority(ObjectUtil.valueAsInt(paramSource.getValue("custEMailPriority")));

		doValidate(header, detail);

		updateProcess(header, detail, paramSource);

		header.getUploadDetails().add(detail);

		logger.debug(Literal.LEAVING);
	}

	protected void setError(CustomerKycDetail detail, String code, String... parms) {
		setFailureStatus(detail, ErrorUtil.getErrorDetail(new ErrorDetail(code, parms)));
	}

	protected void setError(CustomerKycDetail detail, CustomerDetailsUploadError error) {
		setFailureStatus(detail, error.name(), error.description());
	}

	protected void setError(CustomerKycDetail detail, CustomerDetailsUploadError error, String arg) {
		setFailureStatus(detail, error.name(), error.description().concat(arg));
	}

	private int getLength(String str) {
		return str == null ? 0 : str.length();
	}
}
