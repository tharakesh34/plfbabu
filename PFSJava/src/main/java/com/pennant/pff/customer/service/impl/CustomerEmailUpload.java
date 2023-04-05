package com.pennant.pff.customer.service.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.customermasters.CustomerEMailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bulkAddressUpload.CustomerKycDetail;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.service.customermasters.CustomerEMailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.customer.exception.CustomerDetailsUploadError;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.RequestSource;

public class CustomerEmailUpload extends KycDetailsUploadServiceImpl {

	@Autowired
	private CustomerEMailService customerEMailService;
	@Autowired
	private CustomerEMailDAO customerEMailDAO;

	public CustomerEmailUpload() {
		super();
	}

	public void process(CustomerKycDetail detail) {
		CustomerEMail email = prepareCE(detail);

		boolean sameEMail = false;
		boolean samPriority = false;
		int version = 0;

		Long custID = detail.getReferenceID();
		CustomerEMail existingCust = customerEMailDAO.getCustomerEMailById(custID, detail.getCustEMailTypeCode(), "");

		if (existingCust != null) {
			sameEMail = true;
			version = existingCust.getVersion();
			if (existingCust.getCustEMailPriority() == email.getCustEMailPriority()) {
				samPriority = true;
			}
		} else {
			List<CustomerEMail> list = customerEMailDAO.getCustomerEMailById(custID, detail.getCustEMailPriority());
			samPriority = CollectionUtils.isNotEmpty(list);
			if (CollectionUtils.isNotEmpty(list)) {
				version = list.get(list.size() - 1).getVersion();
				sameEMail = list.stream()
						.anyMatch(l1 -> l1.getCustEMailTypeCode().equals(email.getCustEMailTypeCode()));

			}
		}

		try {
			if (sameEMail && samPriority) {
				processSEandSP(email, version);
			}

			if (sameEMail && !samPriority) {
				processSEandNSP(detail, email, existingCust);
				if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
					return;
				}
			}

			if (!sameEMail && samPriority) {
				processNSEandSP(detail, email);
			}

			if (!sameEMail && !samPriority) {
				processNSEandNSP(email);
			}
		} catch (Exception e) {
			setError(detail, ERR_CODE, getErrorMessage(e));
			kycDetailsUploadDAO.update(detail);
		}
	}

	public void validate(CustomerKycDetail detail) {
		if (detail.getCustEMailTypeCode() != null) {
			if (customerEMailDAO.getEMailTypeCount(detail.getCustEMailTypeCode()) <= 0) {
				setError(detail, "90701", "EMailType", detail.getCustEMailTypeCode());
				return;
			}

			if (!(detail.getCustEMailPriority() >= 1 && detail.getCustEMailPriority() <= 5)) {
				setError(detail, "90110", String.valueOf(detail.getCustEMailPriority()));
				return;
			}

			if (!EmailValidator.getInstance().isValid(detail.getCustEMail())) {
				setError(detail, "90237", detail.getCustEMail());
				return;
			}
		}
	}

	private CustomerEMail prepareCE(CustomerKycDetail detail) {
		CustomerEMail email = new CustomerEMail();

		email.setCustEMailTypeCode(detail.getCustEMailTypeCode());
		email.setCustEMailPriority(detail.getCustEMailPriority());
		email.setCustEMail(detail.getCustEMail());
		email.setSourceId(detail.getSource());
		email.setCustID(detail.getReferenceID());

		return email;
	}

	private void processSEandSP(CustomerEMail email, int version) {
		email.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		email.setVersion(version + 1);
		email.setSourceId(RequestSource.UPLOAD.name());

		customerEMailService.doApprove(getAuditHeader(email, PennantConstants.TRAN_WF));
	}

	private void processSEandNSP(CustomerKycDetail detail, CustomerEMail ce, CustomerEMail emailExis) {
		Long custID = detail.getReferenceID();

		if (Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH) == emailExis.getCustEMailPriority()
				&& Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH) != ce.getCustEMailPriority()) {
			setError(detail, CustomerDetailsUploadError.KYC_MAIL_04);
			return;
		}

		if (Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH) == emailExis.getCustEMailPriority()) {
			return;
		}

		customerEMailDAO.getCustomerEMailById(custID, detail.getCustEMailPriority()).forEach(em -> updateAsLow(em));
		deleteExisting(emailExis);
		create(ce);
	}

	private void processNSEandSP(CustomerKycDetail detail, CustomerEMail email) {
		Long custID = detail.getReferenceID();

		if (Integer.valueOf(PennantConstants.KYC_PRIORITY_LOW) == email.getCustEMailPriority()) {
			return;
		}

		customerEMailDAO.getCustomerEMailById(custID, detail.getCustEMailPriority()).forEach(em -> updateAsLow(em));
		create(email);
	}

	private void processNSEandNSP(CustomerEMail email) {
		if (Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH) == email.getCustEMailPriority()) {
			return;
		}

		create(email);
	}

	private void updateAsLow(CustomerEMail email) {

		email.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		email.setCustEMailPriority(Integer.valueOf(PennantConstants.KYC_PRIORITY_LOW));
		email.setVersion(email.getVersion() + 1);
		email.setSourceId(RequestSource.UPLOAD.name());

		customerEMailService.doApprove(getAuditHeader(email, PennantConstants.TRAN_WF));
	}

	private void deleteExisting(CustomerEMail email) {
		email.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		email.setSourceId(RequestSource.UPLOAD.name());

		customerEMailService.doApprove(getAuditHeader(email, PennantConstants.TRAN_WF));
	}

	private void create(CustomerEMail email) {
		email.setRecordType(PennantConstants.RECORD_TYPE_NEW);

		customerEMailService.doApprove(getAuditHeader(email, PennantConstants.TRAN_WF));
	}

	private AuditHeader getAuditHeader(CustomerEMail ce, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, ce.getBefImage(), ce);
		return new AuditHeader(String.valueOf(ce.getCustID()), String.valueOf(ce.getCustID()), null, null, auditDetail,
				ce.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

}
