package com.pennant.pff.customer.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.customermasters.CustomerPhoneNumberDAO;
import com.pennant.backend.dao.systemmasters.PhoneTypeDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bulkaddressupload.CustomerKycDetail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.systemmasters.PhoneType;
import com.pennant.backend.service.customermasters.CustomerPhoneNumberService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.customer.exception.CustomerDetailsUploadError;
import com.pennanttech.pff.core.RequestSource;

public class CustomerPhoneNumberUpload extends KycDetailsUploadServiceImpl {

	@Autowired
	protected CustomerPhoneNumberService customerPhoneNumberService;
	@Autowired
	protected PhoneTypeDAO phoneTypeDAO;
	@Autowired
	protected CustomerPhoneNumberDAO customerPhoneNumberDAO;

	public CustomerPhoneNumberUpload() {
		super();
	}

	protected void process(CustomerKycDetail detail) {
		CustomerPhoneNumber phone = prepareCP(detail);

		int curPriority = detail.getPhoneTypePriority();
		Long custID = detail.getReferenceID();
		String typeCode = detail.getPhoneTypeCode();

		CustomerPhoneNumber phoneExis = customerPhoneNumberDAO.getCustomerPhoneNumberByID(custID, typeCode, "");

		boolean samePhoneType = false;
		boolean samePriority = false;
		int version = 0;

		if (phoneExis != null) {
			samePhoneType = true;
			version = phoneExis.getVersion();
			if (phoneExis.getPhoneTypePriority() == phone.getPhoneTypePriority()) {
				samePriority = true;
			}
		} else {
			List<CustomerPhoneNumber> list = customerPhoneNumberDAO.getCustomerPhoneNumberByID(custID, curPriority);
			samePriority = CollectionUtils.isNotEmpty(list);
			if (CollectionUtils.isNotEmpty(list)) {
				version = list.get(list.size() - 1).getVersion();
				samePhoneType = list.stream().anyMatch(l1 -> l1.getPhoneTypeCode().equals(phone.getPhoneTypeCode()));
			}
		}

		try {
			if (samePhoneType && samePriority) {
				processSPTandSP(phone, version);
			}

			if (samePhoneType && !samePriority) {
				proessSPTandNSP(detail, phone, phoneExis);
				if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
					return;
				}
			}

			if (!samePhoneType && samePriority) {
				proessNSPTandSP(detail, phone);
			}

			if (!samePhoneType && !samePriority) {
				proessNSPTandNSP(phone);
			}
		} catch (Exception e) {
			setError(detail, "9999", getErrorMessage(e));
			kycDetailsUploadDAO.update(detail);
		}
	}

	public void validate(CustomerKycDetail detail) {
		if (StringUtils.isEmpty(detail.getPhoneTypeCode())) {
			return;
		}

		PhoneType phoneType = phoneTypeDAO.getPhoneTypeById(detail.getPhoneTypeCode(), "");
		if (phoneType != null) {
			String regex = phoneType.getPhoneTypeRegex();
			if (regex != null) {
				if (!(detail.getPhoneNumber().matches(regex))) {
					setError(detail, "90346", regex);
					return;
				}
			}
		}

		if (customerPhoneNumberDAO.getPhoneTypeCodeCount(detail.getPhoneTypeCode()) <= 0) {
			setError(detail, "90701", "PhoneType", detail.getPhoneTypeCode());
			return;
		}

		if (!(detail.getPhoneTypePriority() >= 1 && detail.getPhoneTypePriority() <= 5)) {
			setError(detail, "90115", String.valueOf(detail.getPhoneTypePriority()));
		}
	}

	private CustomerPhoneNumber prepareCP(CustomerKycDetail detail) {
		CustomerPhoneNumber phone = new CustomerPhoneNumber();

		phone.setPhoneNumber(detail.getPhoneNumber());
		phone.setPhoneTypeCode(detail.getPhoneTypeCode());
		phone.setPhoneTypePriority(detail.getPhoneTypePriority());
		phone.setSourceId(detail.getSource());
		phone.setPhoneCustID(detail.getReferenceID());

		return phone;
	}

	private void processSPTandSP(CustomerPhoneNumber phone, int version) {
		phone.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		phone.setVersion(version + 1);
		phone.setSourceId(RequestSource.UPLOAD.name());

		customerPhoneNumberService.doApprove(getAuditHeader(phone, PennantConstants.TRAN_WF));
	}

	private void proessSPTandNSP(CustomerKycDetail detail, CustomerPhoneNumber phone, CustomerPhoneNumber phoneExis) {
		int curPriority = detail.getPhoneTypePriority();
		Long custID = detail.getReferenceID();

		Integer veryHigh = Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH);

		if (veryHigh == phoneExis.getPhoneTypePriority() && veryHigh != phone.getPhoneTypePriority()) {
			setError(detail, CustomerDetailsUploadError.KYC_PHONE_05);
			return;
		}

		if (veryHigh == phoneExis.getPhoneTypePriority()) {
			return;
		}

		if (veryHigh == detail.getPhoneTypePriority()) {
			customerPhoneNumberDAO.getCustomerPhoneNumberByID(custID, curPriority).forEach(ph -> updateAsLow(ph));
		}

		deleteExisting(phoneExis);
		create(phone);
	}

	private void proessNSPTandSP(CustomerKycDetail detail, CustomerPhoneNumber phone) {
		Integer veryHigh = Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH);

		Long custID = detail.getReferenceID();
		int priority = detail.getPhoneTypePriority();

		if (veryHigh == detail.getPhoneTypePriority()) {
			customerPhoneNumberDAO.getCustomerPhoneNumberByID(custID, priority).forEach(ph -> updateAsLow(ph));
		}
		create(phone);
	}

	private void proessNSPTandNSP(CustomerPhoneNumber phone) {
		if (Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH) == phone.getPhoneTypePriority()) {
			return;
		}

		create(phone);
	}

	private void updateAsLow(CustomerPhoneNumber phone) {
		phone.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		phone.setPhoneTypePriority(Integer.valueOf(PennantConstants.KYC_PRIORITY_LOW));
		phone.setVersion(phone.getVersion() + 1);
		phone.setSourceId(RequestSource.UPLOAD.name());

		customerPhoneNumberService.doApprove(getAuditHeader(phone, PennantConstants.TRAN_WF));
	}

	private void deleteExisting(CustomerPhoneNumber phone) {
		phone.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		phone.setSourceId(RequestSource.UPLOAD.name());

		customerPhoneNumberService.doApprove(getAuditHeader(phone, PennantConstants.TRAN_WF));
	}

	private void create(CustomerPhoneNumber cpn) {
		cpn.setRecordType(PennantConstants.RECORD_TYPE_NEW);

		customerPhoneNumberService.doApprove(getAuditHeader(cpn, PennantConstants.TRAN_WF));
	}

	private AuditHeader getAuditHeader(CustomerPhoneNumber cpn, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, cpn.getBefImage(), cpn);
		return new AuditHeader(String.valueOf(cpn.getPhoneCustID()), String.valueOf(cpn.getPhoneCustID()), null, null,
				auditDetail, cpn.getUserDetails(), null);
	}

}
