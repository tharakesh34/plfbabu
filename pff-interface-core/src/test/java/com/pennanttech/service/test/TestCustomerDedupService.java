package com.pennanttech.service.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennanttech.model.DedupCustomerDetail;
import com.pennanttech.model.DedupCustomerResponse;
import com.pennanttech.service.CustomerDedupService;

public class TestCustomerDedupService {

	SimpleDateFormat dateFormater = new SimpleDateFormat("dd-MMM-yyyy");

	@Test(enabled=false)
	public void testRequest() throws ParseException {
		CustomerDedupService service = new CustomerDedupService();
		DedupCustomerDetail customerDetails = new DedupCustomerDetail();
		
		customerDetails.setFinReference("1234567");
		
		
		Customer customer = new Customer();
		customer.setCustFName("SANJEEV");
		customer.setCustLName("GUPTA");
		customerDetails.setFinType("PO");
		customer.setCustDOB(dateFormater.parse("05-JUL-1973"));
		customerDetails.setCustomer(customer);
		List<CustomerAddres> addressList = new ArrayList<>();
		CustomerAddres address1  = new CustomerAddres();
		address1.setCustAddrType("CURRES");
		address1.setCustAddrCity("DELHI");
		address1.setCustAddrZIP("110026");
		
		List<CustomerPhoneNumber> phoneList = new ArrayList<>();
		CustomerPhoneNumber phoneNumber = new CustomerPhoneNumber();
		phoneNumber.setPhoneAreaCode("");
		phoneNumber.setPhoneTypeCode("MOBILE");
		phoneNumber.setPhoneNumber("9313103704");
		phoneList.add(phoneNumber);
		
		List<CustomerDocument> documentList = new ArrayList<>();
		List<CustomerEMail> emailList = new ArrayList<>();
		
		
		customerDetails.setAddressList(addressList);
		customerDetails.setCustomerPhoneNumList(phoneList);
		customerDetails.setCustomerDocumentsList(documentList);
		customerDetails.setCustomerEMailList(emailList);
		
		
		DedupCustomerResponse response = new DedupCustomerResponse();
		
		try {
			response = service.invokeDedup(customerDetails);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(response.toString());
		
		
	}

	@Test(enabled=false)
	public void testCust1()  throws ParseException {
		CustomerDedupService service = new CustomerDedupService();
		DedupCustomerDetail customerDetails = new DedupCustomerDetail();
		
		customerDetails.setFinReference("1234567");
		
		
		Customer customer = new Customer();
		customer.setCustFName("RAMESH");
		customer.setCustLName("DURAIRAJ");
		customerDetails.setFinType("PO");
		customer.setCustDOB(dateFormater.parse("05-JUL-73"));
		customerDetails.setCustomer(customer);
		List<CustomerAddres> addressList = new ArrayList<>();
		CustomerAddres address1  = new CustomerAddres();
		address1.setCustAddrType("CURRES");
		address1.setCustAddrCity("KARUR");
		address1.setCustAddrZIP("638901");
		
		List<CustomerPhoneNumber> phoneList = new ArrayList<>();
		CustomerPhoneNumber phoneNumber = new CustomerPhoneNumber();
		phoneNumber.setPhoneAreaCode("0");
		phoneNumber.setPhoneTypeCode("OFFICE");
		phoneNumber.setPhoneNumber("222404");
		phoneList.add(phoneNumber);
		
		List<CustomerDocument> documentList = new ArrayList<>();
		List<CustomerEMail> emailList = new ArrayList<>();
		
		
		customerDetails.setAddressList(addressList);
		customerDetails.setCustomerPhoneNumList(phoneList);
		customerDetails.setCustomerDocumentsList(documentList);
		customerDetails.setCustomerEMailList(emailList);
		
		
		DedupCustomerResponse response = new DedupCustomerResponse();
		
		try {
			response = service.invokeDedup(customerDetails);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(response.toString());
		
		
	}

	@Test(enabled=false)
	public void testCust2()  throws ParseException {
		CustomerDedupService service = new CustomerDedupService();
		DedupCustomerDetail customerDetails = new DedupCustomerDetail();
		
		customerDetails.setFinReference("1234567");
		
		
		Customer customer = new Customer();
		customer.setCustFName("RAMESH");
		customer.setCustLName("DURAIRAJ");
		customerDetails.setFinType("PO");
		customer.setCustDOB(dateFormater.parse("05-Jul-73"));
		customerDetails.setCustomer(customer);
		List<CustomerAddres> addressList = new ArrayList<>();
		CustomerAddres address1  = new CustomerAddres();
		address1.setCustAddrType("CURRES");
		address1.setCustAddrCity("KARUR");
		address1.setCustAddrZIP("638901");
		
		List<CustomerPhoneNumber> phoneList = new ArrayList<>();
		CustomerPhoneNumber phoneNumber = new CustomerPhoneNumber();
		phoneNumber.setPhoneAreaCode("0");
		phoneNumber.setPhoneTypeCode("OFFICE");
		phoneNumber.setPhoneNumber("222404");
		phoneList.add(phoneNumber);
		
		List<CustomerDocument> documentList = new ArrayList<>();
		List<CustomerEMail> emailList = new ArrayList<>();
		
		
		customerDetails.setAddressList(addressList);
		customerDetails.setCustomerPhoneNumList(phoneList);
		customerDetails.setCustomerDocumentsList(documentList);
		customerDetails.setCustomerEMailList(emailList);
		
		
		DedupCustomerResponse response = new DedupCustomerResponse();
		
		try {
			response = service.invokeDedup(customerDetails);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(response.toString());
		
		
	}
}
