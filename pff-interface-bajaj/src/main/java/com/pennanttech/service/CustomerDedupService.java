package com.pennanttech.service;

import java.util.ArrayList;
import java.util.List;

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
import com.pennanttech.model.DedupCustomerDetail;
import com.pennanttech.model.DedupCustomerResponse;

public class CustomerDedupService {

	public CustomerDedupService() {
		super();
	}
	
	
	public DedupeResponse invokeDedup(DedupCustomerDetail  dedupCustomerDetail) throws Exception{
		DedupeResponse response = null;
		
		JSONClient client= new JSONClient();
		try {
			String serviceURL = "http://192.168.1.203:8080/pff-api/services";	// FIXME fetch from configuration
			 response = (DedupeResponse) client.postProcess(serviceURL, "DedupeService", prepareRequest(dedupCustomerDetail), DedupeResponse.class);
			prepareResponse(response);
			
		} catch (Exception e) {
			throw e;
		}
		return response;
	}

	
	private DedupeRequest prepareRequest(DedupCustomerDetail  dedupCustomerDetail){
		boolean mobileSelected=false;
		
		DedupeRequest request = new DedupeRequest();
		//request.setDealId(dealId);
		request.setOrg("CL");
		request.setRequestType("Q");
		request.setDataSource("E");
		
		Customer customer   = dedupCustomerDetail.getCustomer();
		
		request.setFirstName(customer.getCustFName());
		request.setMiddleName(customer.getCustMName());
		request.setLastName(customer.getCustLName());
		
		List<CustomerAddres> listAddres= dedupCustomerDetail.getAddressList();
		List<CustomerPhoneNumber> listPhoneNumbers= dedupCustomerDetail.getCustomerPhoneNumList();
		List<CustomerDocument> listDocuments= dedupCustomerDetail.getCustomerDocumentsList();
		List<CustomerEMail> listCustEmail = dedupCustomerDetail.getCustomerEMailList();
		
		
 		request.setCustomerType(customer.getCustTypeCode());
		request.setFatherName(customer.getCustFNameLclLng());
		//request.setEmployerName(customer.getc);
		
		if("CORP".equalsIgnoreCase(customer.getCustTypeCode())){
			request.setDateOfIncorporation(customer.getCustDOB());
			for (CustomerAddres customerAddres : listAddres) {
				if("OFFICE".equalsIgnoreCase(customer.getCustTypeCode())){
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
					
					if("MOBILE".equalsIgnoreCase(phoneNumber.getPhoneTypeCode()) && !mobileSelected){
						request.setOfficeMobile(phoneNumber.getPhoneAreaCode() + phoneNumber.getPhoneNumber());
						request.setMobile(phoneNumber.getPhoneAreaCode() + phoneNumber.getPhoneNumber());
						mobileSelected=true;
					}
					
					if("WORK".equalsIgnoreCase(phoneNumber.getPhoneTypeCode()) && !mobileSelected){
						request.setOfficeLanLine1(phoneNumber.getPhoneNumber());
						//request.setOfficeLanLine2(phoneNumber.getPhoneNumber());
						request.setOfficeStdCode(phoneNumber.getPhoneAreaCode());
					}

				}
				
			}	
			
			//request.setTanNo(tanNo);
			
			
		}else{
			request.setDateOfBirth(customer.getCustDOB());
			
			for (CustomerAddres customerAddres : listAddres) {
				if("CURRES".equalsIgnoreCase(customer.getCustTypeCode())){
					request.setAddress1(customerAddres.getCustAddrType());
					request.setAddress2(customerAddres.getCustAddrHNbr());
					request.setAddress3(customerAddres.getCustAddrStreet());
					request.setCity(customerAddres.getCustAddrCity());
					request.setPinCode(customerAddres.getCustAddrZIP());
					//request.setArea(area);
					//request.setLandMark(landMark);
					//request.setStayingSince(customerAddres.);
					
					
				}
				
				if("OFFICE".equalsIgnoreCase(customer.getCustTypeCode())){
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
				
				if("MOBILE".equalsIgnoreCase(phoneNumber.getPhoneTypeCode()) && !mobileSelected){
					request.setOfficeMobile(phoneNumber.getPhoneAreaCode() + phoneNumber.getPhoneNumber());
					request.setMobile(phoneNumber.getPhoneAreaCode() + phoneNumber.getPhoneNumber());
					mobileSelected=true;
				}
				

				if("HOME".equalsIgnoreCase(phoneNumber.getPhoneTypeCode()) && !mobileSelected){
					request.setLandLine1(phoneNumber.getPhoneNumber());
					//request.setLandLine2(phoneNumber.getPhoneNumber());
					request.setStdCode(phoneNumber.getPhoneAreaCode());
				}

				if("OFFICE".equalsIgnoreCase(phoneNumber.getPhoneTypeCode()) && !mobileSelected){
					request.setOfficeLanLine1(phoneNumber.getPhoneNumber());
					//request.setOfficeLanLine2(phoneNumber.getPhoneNumber());
					request.setOfficeStdCode(phoneNumber.getPhoneAreaCode());
				}
					
				
			}
			}
		
		
		for (CustomerDocument customerDocument : listDocuments) {
			
			if("11"==customerDocument.getCustDocCategory()){
				request.setDrivingLicense(customerDocument.getCustDocTitle());
			}else  if("2"==customerDocument.getCustDocCategory()){
				request.setPassportNo(customerDocument.getCustDocTitle());
			}else if("3".equalsIgnoreCase(customerDocument.getCustDocCategory())) {
				request.setPanNumber(customerDocument.getCustDocTitle());
			}

			//request.setVoterId(voterId);

			
		}
		
		
		if(listCustEmail != null) {
			for(CustomerEMail email: listCustEmail) {
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
	
	
	private DedupCustomerResponse  prepareResponse(DedupeResponse response){
		DedupCustomerResponse customerResponse= new DedupCustomerResponse(); 
		customerResponse.setResponse(response.getResponseCode());
		List<DedupCustomerDetail> details= new ArrayList<>();
		
		if(response.getErrorDescription()!=null){
			customerResponse.setErrorCode(response.getErrorDescription().getErrorCode());
			customerResponse.setErrorDesc(response.getErrorDescription().getErrorDescription());
		}
		
		for (DemographicDetail detail : response.getDemographicDetails()) {
			details.add(prepareCustomerDetail(detail));
		}
		
		
		return customerResponse;
	}
	
	private DedupCustomerDetail prepareCustomerDetail(DemographicDetail detail){
		
		DedupCustomerDetail customerDetail= null;
		if(detail!=null){
			customerDetail= new DedupCustomerDetail();
			
			if(detail.getCustDGDetails()!=null && !detail.getCustDGDetails().isEmpty()){
				setDGDetails(detail.getCustDGDetails().get(0), customerDetail); 
			}
		}

		List<CustomerAddres> addressList= new ArrayList<CustomerAddres>();
		
		if(detail.getCustAddressDetails()!=null && !detail.getCustAddressDetails().isEmpty()){
			for (CustAddressDetail addressDetail : detail.getCustAddressDetails()) {
				addressList.add(setAddress(addressDetail));
			}
			
			customerDetail.setAddressList(addressList);
		}

		List<CustomerEMail> customerEMailList= new ArrayList<CustomerEMail>();
		
		if(detail.getCustEmailDetails()!=null && !detail.getCustEmailDetails().isEmpty()){
			for (CustEmailDetail emailDetail: detail.getCustEmailDetails()) {
				customerEMailList.add(setEmail(emailDetail));
			}
			
			customerDetail.setCustomerEMailList(customerEMailList);
		}

		List<CustomerPhoneNumber> customerPhoneNumList= new ArrayList<CustomerPhoneNumber>();
		if(detail.getCustContactDetails()!=null && !detail.getCustContactDetails().isEmpty()){
			for (CustContactDetail contactDetail : detail.getCustContactDetails()) {
				customerPhoneNumList.add(setPhoneNumbers(contactDetail));
			}
			customerDetail.setCustomerPhoneNumList(customerPhoneNumList);
		}
		
		return customerDetail;
	}
	
	private DedupCustomerDetail setDGDetails(CustDGDetail dgDetail,DedupCustomerDetail customerDetail){
		
		customerDetail.setCustCIF(dgDetail.getCustomerId());
		Customer customer= new Customer();
		customer.setCustCIF(dgDetail.getCustomerId());
		customer.setCustTypeCode(dgDetail.getCustomerType());
		customer.setCustShrtName(dgDetail.getCustomerName());
		//customer.setCustDOB(dgDetail.getDateOfBirth());
		
		List<CustomerDocument> listDocuments= new ArrayList<CustomerDocument>();
		if(dgDetail.getPanNumber()!=null){
			CustomerDocument custpan = new CustomerDocument();
			custpan.setCustDocCategory("3");
			listDocuments.add(custpan);
		}
		customerDetail.setCustomerDocumentsList(listDocuments);
		return customerDetail;
	}
	
	
	private CustomerAddres setAddress(CustAddressDetail addressDetail){
		CustomerAddres addres= new CustomerAddres();
		addres.setCustAddrType(addressDetail.getAddressType());
		addres.setCustAddrCity(addressDetail.getCity());
		addres.setCustAddrZIP(addressDetail.getPin());
		return addres;
	}
	
	
	private CustomerEMail setEmail(CustEmailDetail detail){
		CustomerEMail  eMail= new CustomerEMail();
		eMail.setCustEMailTypeCode(detail.getEmailType());
		eMail.setCustEMail(detail.getEmailId());
		return eMail;
	}
	
	private CustomerPhoneNumber setPhoneNumbers(CustContactDetail detail){
		CustomerPhoneNumber  phoneNumber= new CustomerPhoneNumber();
		phoneNumber.setPhoneTypeCode(detail.getPhoneType());
		phoneNumber.setPhoneAreaCode(detail.getStdCode());
		phoneNumber.setPhoneNumber(detail.getPhoneNumber());
		
		return phoneNumber;
	}
	
	
}
