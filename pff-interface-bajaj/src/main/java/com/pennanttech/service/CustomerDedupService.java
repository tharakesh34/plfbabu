package com.pennanttech.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennanttech.bajaj.model.CustAddressDetail;
import com.pennanttech.bajaj.model.CustContactDetail;
import com.pennanttech.bajaj.model.CustDGDetail;
import com.pennanttech.bajaj.model.CustEmailDetail;
import com.pennanttech.bajaj.model.DedupeRequest;
import com.pennanttech.bajaj.model.DedupeResponse;
import com.pennanttech.bajaj.model.DemographicDetail;
import com.pennanttech.clients.JSONClient;
import com.pennanttech.dataengine.util.DateUtil;
import com.pennanttech.model.DedupCustomerDetail;
import com.pennanttech.model.DedupCustomerResponse;

public class CustomerDedupService {

	private static final Logger logger = Logger.getLogger(CustomerDedupService.class);

	public CustomerDedupService() {
		super();
	}

	private Properties			props			= null;
	private SimpleDateFormat	dateFormater	= new SimpleDateFormat("dd-MMM-yyyy");

	public DedupCustomerResponse invokeDedup(DedupCustomerDetail dedupCustomerDetail) throws Exception {

		String serviceURL = getProperties("posidex");
		DedupCustomerResponse customerResponse = null;
		DedupeResponse response = null;

		JSONClient client = new JSONClient();
		try {
			logger.debug("ServiceURL : " + serviceURL);
			response = (DedupeResponse) client.postProcess(serviceURL, "DedupeService",
					prepareRequest(dedupCustomerDetail), DedupeResponse.class);
			logger.info("Response : " + response.toString());
			customerResponse = prepareResponse(response);
			logger.warn("response-->" + response);
		} catch (Exception exception) {
			logger.error("Error from Dedup Response : " + exception.getMessage(), exception);
			throw exception;
		}
		return customerResponse;
	}

	public String getProperties(String property) throws IOException {
		if (props == null) {
			props = new Properties();
			PathMatchingResourcePatternResolver loader = new PathMatchingResourcePatternResolver();
			Resource[] resources = new Resource[0];
			resources = loader.getResources("classpath:/plf-interface.properties");
			props.load(resources[0].getInputStream());
			return props.getProperty(property);
		}
		return props.getProperty(property);
	}

	private DedupeRequest prepareRequest(DedupCustomerDetail dedupCustomerDetail) {
		boolean mobileSelected = false;

		DedupeRequest request = new DedupeRequest();
		//request.setDealId(dealId);
		request.setOrg("CL");
		request.setRequestType("Q");
		request.setDataSource("E");

		Customer customer = dedupCustomerDetail.getCustomer();

		if ("RETAIL".equalsIgnoreCase(customer.getCustCtgCode())) {
			request.setFirstName(customer.getCustFName());
			request.setMiddleName(customer.getCustMName());
			request.setLastName(customer.getCustLName());
			request.setCustomerType(customer.getCustCtgCode());
			request.setFatherName(customer.getCustFNameLclLng());
		} else {
			request.setFirstName(customer.getCustShrtName());
			request.setCustomerType(customer.getCustCtgCode());
		}
		request.setPanNumber(customer.getCustCRCPR());
		List<CustomerAddres> listAddres = dedupCustomerDetail.getAddressList();
		List<CustomerPhoneNumber> listPhoneNumbers = dedupCustomerDetail.getCustomerPhoneNumList();
		List<CustomerDocument> listDocuments = dedupCustomerDetail.getCustomerDocumentsList();
		List<CustomerEMail> listCustEmail = dedupCustomerDetail.getCustomerEMailList();

		//request.setEmployerName(customer.getc);

		if ("CORP".equalsIgnoreCase(customer.getCustCtgCode())) {
			request.setDateOfIncorporation(dateFormater.format(customer.getCustDOB()));
			for (CustomerAddres customerAddres : listAddres) {
				if ("OFFICE".equalsIgnoreCase(customer.getCustTypeCode())) {
					request.setAddress1(customerAddres.getCustAddrType());
					request.setAddress2(customerAddres.getCustAddrHNbr());
					request.setAddress3(customerAddres.getCustAddrStreet());
					request.setCity(customerAddres.getCustAddrCity());
					request.setPinCode(customerAddres.getCustAddrZIP());
					//request.setArea(area);

					request.setOfficeAddress1(customerAddres.getCustAddrType());
					request.setOfficeAddress2(customerAddres.getCustAddrHNbr());
					request.setOfficeAddress3(customerAddres.getCustAddrStreet());
					request.setOfficeCity(customerAddres.getCustAddrCity());
					request.setOfficePinCode(customerAddres.getCustAddrZIP());
					//request.setOfficeArea(officeArea);
					//request.setOfficelandMark(officelandMark);
					break;
				}

				for (CustomerPhoneNumber phoneNumber : listPhoneNumbers) {

					if ("MOBILE".equalsIgnoreCase(phoneNumber.getPhoneTypeCode()) && !mobileSelected) {
						request.setMobile(phoneNumber.getPhoneNumber());
						mobileSelected = true;
					}

					if ("WORK".equalsIgnoreCase(phoneNumber.getPhoneTypeCode()) && !mobileSelected) {
						request.setOfficeLanLine1(phoneNumber.getPhoneNumber());
						//request.setOfficeLanLine2(phoneNumber.getPhoneNumber());
						request.setOfficeStdCode(phoneNumber.getPhoneAreaCode());
					}

				}

			}

			//request.setTanNo(tanNo);

		} else {
			request.setDateOfBirth(dateFormater.format(customer.getCustDOB()));

			for (CustomerAddres customerAddres : listAddres) {
				if ("CURRES".equalsIgnoreCase(customer.getCustTypeCode())) {
					request.setAddress1(customerAddres.getCustAddrType());
					request.setAddress2(customerAddres.getCustAddrHNbr());
					request.setAddress3(customerAddres.getCustAddrStreet());
					request.setCity(customerAddres.getCustAddrCity());
					request.setPinCode(customerAddres.getCustAddrZIP());
					//request.setArea(area);
					//request.setLandMark(landMark);
					//request.setStayingSince(customerAddres.);

				}

				if ("OFFICE".equalsIgnoreCase(customer.getCustTypeCode())) {
					request.setAddress1(customerAddres.getCustAddrType());
					request.setAddress2(customerAddres.getCustAddrHNbr());
					request.setAddress3(customerAddres.getCustAddrStreet());
					request.setCity(customerAddres.getCustAddrCity());
					request.setPinCode(customerAddres.getCustAddrZIP());
					//request.setArea(area);
					//request.setLandMark(landMark);
				}

			}

			for (CustomerPhoneNumber phoneNumber : listPhoneNumbers) {

				if ("MOBILE".equalsIgnoreCase(phoneNumber.getPhoneTypeCode()) && !mobileSelected) {
					request.setOfficeMobile(phoneNumber.getPhoneNumber());
					request.setMobile(phoneNumber.getPhoneNumber());
					mobileSelected = true;
				}

				if ("HOME".equalsIgnoreCase(phoneNumber.getPhoneTypeCode()) && !mobileSelected) {
					request.setLandLine1(phoneNumber.getPhoneNumber());
					//request.setLandLine2(phoneNumber.getPhoneNumber());
					request.setStdCode(phoneNumber.getPhoneAreaCode());
				}

				if ("OFFICE".equalsIgnoreCase(phoneNumber.getPhoneTypeCode()) && !mobileSelected) {
					request.setOfficeLanLine1(phoneNumber.getPhoneNumber());
					//request.setOfficeLanLine2(phoneNumber.getPhoneNumber());
					request.setOfficeStdCode(phoneNumber.getPhoneAreaCode());
				}
			}
		}

		for (CustomerDocument customerDocument : listDocuments) {

			if ("11" == customerDocument.getCustDocCategory()) {
				request.setDrivingLicense(customerDocument.getCustDocTitle());
			} else if ("02" == customerDocument.getCustDocCategory()) {
				request.setPassportNo(customerDocument.getCustDocTitle());
			} else if ("03".equalsIgnoreCase(customerDocument.getCustDocCategory())) {
				request.setPanNumber(customerDocument.getCustDocTitle());
			}

		}

		if (listCustEmail != null) {
			for (CustomerEMail email : listCustEmail) {
				request.setEmail(email.getCustEMail());
				break;
			}
		}

		request.setAccountNumber(dedupCustomerDetail.getAccountNumber());
		//request.setCreditCardNumber(creditCardNumber);
		request.setCustomerNumber((int) dedupCustomerDetail.getCustID());
		//request.setLanNo(lanNo);
		//request.setLan2(lan2);
		//request.setCustomerSrNo(customerSrNo);

		//request.setApplicationNo(dedupCustomerDetail.getApplicationNo());
		request.setProduct(dedupCustomerDetail.getFinType());

		//request.setBatch(batch);
		//request.setCityClassification(cityClassification);
		//request.setEmploymentBusiness(employmentBusiness);
		//request.setAge(customer.getc);
		//request.setResidentType(residentType);

		//request.setCreditProgram(creditProgram);
		//request.setAssetCategory(assetCategory);
		//request.setFinRequestId(dedupCustomerDetail);
		//request.setMatchProfile(matchProfile);
		//request.setSegment(segment);
		//request.setApplicatntType(applicatntType);

		//request.setOfficeMail(officeMail);
		request.setLoanApplicationNo(dedupCustomerDetail.getApplicationNo());
		request.setCustomerStatus("Y");
		request.setDemoDtl("Y");
		request.setAppscore("Y");

		return request;
	}

	private DedupCustomerResponse prepareResponse(DedupeResponse response) {
		DedupCustomerResponse customerResponse = new DedupCustomerResponse();
		customerResponse.setResponse(response.getResponseCode());
		List<DedupCustomerDetail> details = new ArrayList<>();

		if (response.getErrorDescription() != null) {
			customerResponse.setErrorCode(response.getErrorDescription().getErrorCode());
			customerResponse.setErrorDesc(response.getErrorDescription().getErrorDescription());
		}

		for (DemographicDetail detail : response.getDemographicDetails()) {
			details.add(prepareCustomerDetail(detail));
		}

		customerResponse.setDedupCustomerDetails(details);
		logger.debug(" Dedupe Response Code : " + customerResponse.getErrorCode());
		logger.debug(" Dedupe Response Desc: " + customerResponse.getErrorDesc());
		return customerResponse;
	}

	private DedupCustomerDetail prepareCustomerDetail(DemographicDetail detail) {

		DedupCustomerDetail customerDetail = null;
		if (detail != null) {
			customerDetail = new DedupCustomerDetail();

			//set Demographic details
			if (detail.getCustDGDetails() != null && !detail.getCustDGDetails().isEmpty()) {
				setDGDetails(detail.getCustDGDetails().get(0), customerDetail);
				//get posidex id.
				if (detail.getReportDetails() != null && detail.getReportDetails().getFiller1() != null) {
					logger.debug("posidex id--->" + detail.getReportDetails().getFiller1());
					customerDetail.getCustomer().setCustCoreBank(detail.getReportDetails().getFiller1());
				} else {
					logger.debug("posidex id--->" + customerDetail.getCustCIF());
					customerDetail.getCustomer().setCustCoreBank(customerDetail.getCustCIF());
				}
			}
		}

		List<CustomerAddres> addressList = new ArrayList<CustomerAddres>();

		//Customer Adderess details List
		if (detail.getCustAddressDetails() != null && !detail.getCustAddressDetails().isEmpty()) {
			for (CustAddressDetail addressDetail : detail.getCustAddressDetails()) {
				addressList.add(setAddress(addressDetail));
			}

			customerDetail.setAddressList(addressList);
		}

		List<CustomerEMail> customerEMailList = new ArrayList<CustomerEMail>();

		//E-mails List 
		if (detail.getCustEmailDetails() != null && !detail.getCustEmailDetails().isEmpty()) {
			for (CustEmailDetail emailDetail : detail.getCustEmailDetails()) {
				customerEMailList.add(setEmail(emailDetail));
			}

			customerDetail.setCustomerEMailList(customerEMailList);
		}

		//phone numbers List
		List<CustomerPhoneNumber> customerPhoneNumList = new ArrayList<CustomerPhoneNumber>();
		if (detail.getCustContactDetails() != null && !detail.getCustContactDetails().isEmpty()) {
			for (CustContactDetail contactDetail : detail.getCustContactDetails()) {
				customerPhoneNumList.add(setPhoneNumbers(contactDetail));
			}
			customerDetail.setCustomerPhoneNumList(customerPhoneNumList);
		}

		return customerDetail;
	}

	private DedupCustomerDetail setDGDetails(CustDGDetail dgDetail, DedupCustomerDetail customerDetail) {

		customerDetail.setCustCIF(dgDetail.getCustomerId());
		Customer customer = new Customer();
		customer.setCustCIF(dgDetail.getCustomerId());
		customer.setCustTypeCode(dgDetail.getCustomerType());
		customer.setCustShrtName(dgDetail.getCustomerName());
		customer.setCustCRCPR(dgDetail.getPanNumber());
		try {
			customer.setCustDOB(DateUtil.parse(dgDetail.getDateOfBirth(), "yyyy-MM-dd HH:mm:ss.S"));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		customerDetail.setCustomer(customer);

		List<CustomerDocument> listDocuments = new ArrayList<CustomerDocument>();
		if (dgDetail.getPanNumber() != null) {
			CustomerDocument custpan = new CustomerDocument();
			custpan.setCustDocCategory("03");
			listDocuments.add(custpan);
		}
		customerDetail.setCustomerDocumentsList(listDocuments);
		return customerDetail;
	}

	private CustomerAddres setAddress(CustAddressDetail addressDetail) {
		CustomerAddres addres = new CustomerAddres();
		addres.setCustAddrType(addressDetail.getAddressType());
		addres.setCustAddrCity(addressDetail.getCity());
		addres.setCustAddrZIP(addressDetail.getPin());
		return addres;
	}

	private CustomerEMail setEmail(CustEmailDetail detail) {
		CustomerEMail eMail = new CustomerEMail();
		eMail.setCustEMailTypeCode(detail.getEmailType());
		eMail.setCustEMail(detail.getEmailId());
		return eMail;
	}

	private CustomerPhoneNumber setPhoneNumbers(CustContactDetail detail) {
		CustomerPhoneNumber phoneNumber = new CustomerPhoneNumber();
		phoneNumber.setPhoneTypeCode(detail.getPhoneType());
		phoneNumber.setPhoneAreaCode(detail.getStdCode());
		phoneNumber.setPhoneNumber(detail.getPhoneNumber());

		return phoneNumber;
	}

}
