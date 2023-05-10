package com.pennant.pff.customer.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.applicationmaster.PinCodeDAO;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.dao.smtmasters.CountryDAO;
import com.pennant.backend.dao.systemmasters.ProvinceDAO;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bulkAddressUpload.CustomerKycDetail;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.customermasters.CustomerAddresService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.customer.exception.CustomerDetailsUploadError;
import com.pennanttech.pff.core.RequestSource;

public class CustomerAddressUpload extends KycDetailsUploadServiceImpl {
	@Autowired
	private CustomerAddresService customerAddresService;
	@Autowired
	private CustomerAddresDAO customerAddresDAO;
	@Autowired
	private PinCodeDAO pinCodeDAO;
	@Autowired
	private ProvinceDAO provinceDAO;
	@Autowired
	private CountryDAO countryDAO;

	public CustomerAddressUpload() {
		super();
	}

	public void process(CustomerKycDetail detail) {
		CustomerAddres address = prepareCA(detail);

		Long custID = detail.getReferenceID();

		CustomerAddres addrExis = customerAddresDAO.getCustomerAddresById(custID, detail.getCustAddrType(), "");

		boolean sameaddress = false;
		boolean samepriority = false;

		int version = 0;
		int curPriority = detail.getCustAddrPriority();

		if (addrExis != null) {
			sameaddress = true;
			version = addrExis.getVersion();
			if (addrExis.getCustAddrPriority() == address.getCustAddrPriority()) {
				samepriority = true;
			}
		} else {
			List<CustomerAddres> list = customerAddresDAO.getCustomerAddresById(custID, curPriority);
			samepriority = CollectionUtils.isNotEmpty(list);
			if (CollectionUtils.isNotEmpty(list)) {
				version = list.get(list.size() - 1).getVersion();
				sameaddress = list.stream().anyMatch(l1 -> l1.getCustAddrType().equals(address.getCustAddrType()));
			}
		}

		try {
			if (sameaddress && samepriority) {
				processSAandSP(address, version);
			}

			if (sameaddress && !samepriority) {
				processSAandNSP(detail, address, addrExis);
				if (EodConstants.PROGRESS_FAILED == detail.getProgress()) {
					return;
				}
			}

			if (!sameaddress && samepriority) {
				processNSAandSP(address, custID, curPriority);
			}

			if (!sameaddress && !samepriority) {
				processNSAandNSP(address);
			}
		} catch (Exception e) {
			setError(detail, ERR_CODE, getErrorMessage(e));
			kycDetailsUploadDAO.update(detail);
		}
	}

	public void validate(CustomerKycDetail detail) {
		String addressType = detail.getCustAddrType();

		if (customerAddresDAO.getAddrTypeCount(addressType) <= 0) {
			setError(detail, "90701", "AddrType");
			return;
		}

		Long pinCodeId = detail.getPinCodeId();
		String custAddrZIP = detail.getCustAddrZIP();

		if (pinCodeId != null && pinCodeId < 0) {
			setError(detail, "91121", "PinCodeID", "0");
			return;
		}

		PinCode pincode = validateZIP(detail, pinCodeId, custAddrZIP);

		if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
			return;
		}

		if (pincode != null) {
			validateProvinceAndCity(detail, custAddrZIP, pincode);
		}

		if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
			return;
		}

		if (!(detail.getCustAddrPriority() >= 1 && detail.getCustAddrPriority() <= 5)) {
			setError(detail, "90114", String.valueOf(detail.getCustAddrPriority()));
			return;

		}

		if ((StringUtils.isNotBlank(custAddrZIP)) && (custAddrZIP.length() < 3 || custAddrZIP.length() > 6)) {
			setError(detail, "65031", "pinCode", "2 digits", "7 digits");
			return;
		}
	}

	private PinCode validateZIP(CustomerKycDetail detail, Long pinCodeId, String custAddrZIP) {
		PinCode pincode = null;

		if (StringUtils.isNotBlank(custAddrZIP) && (pinCodeId != null)) {
			pincode = pinCodeDAO.getPinCodeById(pinCodeId, "");
			if (pincode == null) {
				setError(detail, "RU0040", "PinCodeId " + pinCodeId);
				return pincode;
			}

			if (!pincode.getPinCode().equals(custAddrZIP)) {
				setError(detail, "99017", "PinCode " + custAddrZIP, "PinCodeId " + pinCodeId);
				return pincode;
			}
		} else {
			if (StringUtils.isNotBlank(custAddrZIP) && (pinCodeId == null)) {
				switch (pinCodeDAO.getPinCodeCount(custAddrZIP, "")) {
				case 0:
					setError(detail, "RU0040", "PinCode " + custAddrZIP);
					break;
				case 1:
					pincode = pinCodeDAO.getPinCode(custAddrZIP);

					if (!pincode.isActive()) {
						setError(detail, "81004", "PinCode :", custAddrZIP);
						break;
					}

					if (!pincode.isServiceable()) {
						setError(detail, "81005", "PinCode :" + custAddrZIP);
						break;
					}

					detail.setPinCodeId(pincode.getPinCodeId());
					break;
				default:
					setError(detail, "51004", "PinCodeId");
					return null;
				}
			} else if (pinCodeId != null && StringUtils.isBlank(custAddrZIP)) {
				pincode = pinCodeDAO.getPinCodeById(pinCodeId);

				if (pincode == null) {
					setError(detail, "RU0040", "PinCodeId" + String.valueOf(pinCodeId));
					return pincode;
				}

				if (!pincode.isActive()) {
					setError(detail, "81004", "PinCode :", custAddrZIP);
					return pincode;
				}

				if (!pincode.isServiceable()) {
					setError(detail, "81005", "PinCode :" + custAddrZIP);
					return pincode;
				}

				detail.setCustAddrZIP(pincode.getPinCode());
			}
		}

		return pincode;
	}

	private void validateProvinceAndCity(CustomerKycDetail detail, String custAddrZIP, PinCode pincode) {
		String custAddrCountry = detail.getCustAddrCountry();
		String custAddrCity = detail.getCustAddrCity();
		String custAddrProvince = detail.getCustAddrProvince();

		if (StringUtils.isNotBlank(custAddrCountry) && !custAddrCountry.equalsIgnoreCase(pincode.getpCCountry())) {
			setError(detail, "90701", custAddrCountry, custAddrZIP);
			return;
		}

		if (isNotActiveCountry(custAddrCountry)) {
			setError(detail, "81004", "Country :", custAddrCountry);
			return;
		}

		detail.setCustAddrCountry(pincode.getpCCountry());

		Province province = provinceDAO.getProvinceById(custAddrCountry, pincode.getpCProvince(), "");

		if (province != null && StringUtils.isNotBlank(custAddrProvince)
				&& !custAddrProvince.equalsIgnoreCase(province.getCPProvince())) {
			setError(detail, "90701", custAddrProvince, custAddrZIP);
			return;
		}

		if (!province.iscPIsActive()) {
			setError(detail, "81004", "province :", custAddrProvince);
			return;
		}

		detail.setCustAddrProvince(pincode.getpCProvince());

		if (StringUtils.isNotBlank(custAddrCity) && !custAddrCity.equalsIgnoreCase(pincode.getCity())) {
			setError(detail, "90701", custAddrCity, custAddrZIP);
			return;
		}

		detail.setCustAddrCity(pincode.getCity());
	}

	private boolean isNotActiveCountry(String custAddrCountry) {
		return !countryDAO.isActiveCountry(custAddrCountry);
	}

	private CustomerAddres prepareCA(CustomerKycDetail detail) {
		CustomerAddres address = new CustomerAddres();

		address.setCustID(detail.getReferenceID());
		address.setCustAddrType(detail.getCustAddrType());
		address.setCustAddrPriority(detail.getCustAddrPriority());
		address.setCustAddrLine3(detail.getCustAddrLine3());
		address.setCustAddrHNbr(detail.getCustAddrHNbr());
		address.setCustFlatNbr(detail.getCustFlatNbr());
		address.setCustAddrStreet(detail.getCustAddrStreet());
		address.setCustAddrLine1(detail.getCustAddrLine1());
		address.setCustAddrLine2(detail.getCustAddrLine2());
		address.setCustAddrCity(detail.getCustAddrCity());
		address.setCustAddrLine4(detail.getCustAddrLine4());
		address.setCustDistrict(detail.getCustDistrict());
		address.setCustAddrProvince(detail.getCustAddrProvince());
		address.setCustAddrCountry(detail.getCustAddrCountry());
		address.setCustAddrZIP(detail.getCustAddrZIP());
		address.setSourceId(detail.getSource());
		address.setPinCodeId(detail.getPinCodeId());

		return address;
	}

	private void processSAandSP(CustomerAddres address, int version) {
		address.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		address.setVersion(version + 1);
		address.setSourceId(RequestSource.UPLOAD.name());

		customerAddresService.doApprove(getAuditHeader(address, PennantConstants.TRAN_WF));
	}

	private void processSAandNSP(CustomerKycDetail detail, CustomerAddres ca, CustomerAddres addrExis) {
		Integer highPriority = Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH);
		int curPriority = detail.getCustAddrPriority();
		Long custID = detail.getReferenceID();

		if (highPriority == addrExis.getCustAddrPriority() && highPriority != ca.getCustAddrPriority()) {
			setError(detail, CustomerDetailsUploadError.KYC_ADD_09);
			return;
		}

		if (highPriority == addrExis.getCustAddrPriority()) {
			return;
		}

		if (highPriority == detail.getCustAddrPriority()) {
			customerAddresDAO.getCustomerAddresById(custID, curPriority).forEach(ad -> updateAsLow(ad));
		}

		deleteExisting(addrExis);
		create(ca);
	}

	private void processNSAandSP(CustomerAddres address, Long custID, int curPriority) {
		Integer highPriority = Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH);

		if (highPriority == address.getCustAddrPriority()) {
			customerAddresDAO.getCustomerAddresById(custID, curPriority).forEach(ad -> updateAsLow(ad));
		}

		create(address);
	}

	private void processNSAandNSP(CustomerAddres address) {
		if (Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH) == address.getCustAddrPriority()) {
			return;
		}

		create(address);
	}

	private void updateAsLow(CustomerAddres address) {
		address.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		address.setCustAddrPriority(Integer.valueOf(PennantConstants.KYC_PRIORITY_LOW));
		address.setVersion(address.getVersion() + 1);
		address.setSourceId(RequestSource.UPLOAD.name());

		customerAddresService.doApprove(getAuditHeader(address, PennantConstants.TRAN_WF));
	}

	private void deleteExisting(CustomerAddres adress) {
		adress.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		adress.setSourceId(RequestSource.UPLOAD.name());

		customerAddresService.doApprove(getAuditHeader(adress, PennantConstants.TRAN_WF));
	}

	private void create(CustomerAddres address) {
		address.setRecordType(PennantConstants.RECORD_TYPE_NEW);

		customerAddresService.doApprove(getAuditHeader(address, PennantConstants.TRAN_WF));
	}

	private AuditHeader getAuditHeader(CustomerAddres ca, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, ca.getBefImage(), ca);
		return new AuditHeader(String.valueOf(ca.getCustID()), String.valueOf(ca.getCustID()), null, null, auditDetail,
				ca.getUserDetails(), null);
	}

}
