package com.pennanttech.niyogin.utility;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;

public class NiyoginUtility {

	/**
	 * Method to get the High priority email.
	 * 
	 * @param customerEMailList
	 * @param priority
	 * @return String EmailId
	 */
	public static String getHignPriorityEmail(List<CustomerEMail> customerEMailList, int priority) {
		for (CustomerEMail customerEMail : customerEMailList) {
			if (customerEMail.getCustEMailPriority() == priority) {
				return customerEMail.getCustEMail();
			}
		}
		if (priority > 1) {
			getHignPriorityEmail(customerEMailList, priority - 1);
		}
		return null;
	}

	/**
	 * Method to get the High priority PhoneNumeber
	 * 
	 * @param customerPhoneNumList
	 * @param priority
	 * @return String CustomerPhoneNumber
	 */
	public static CustomerPhoneNumber getHighPriorityPhone(List<CustomerPhoneNumber> customerPhoneNumList, int priority) {
		for (CustomerPhoneNumber customerPhoneNumber : customerPhoneNumList) {
			if (customerPhoneNumber.getPhoneTypePriority() == priority) {
				return customerPhoneNumber;
			}
		}
		if (priority > 1) {
			getHighPriorityPhone(customerPhoneNumList, priority - 1);
		}
		return null;
	}

	/**
	 * Method to get the High Priority Address.
	 * 
	 * @param addressList
	 * @param priority
	 * @return CustomerAddres
	 */
	public static CustomerAddres getHighPriorityAddress(List<CustomerAddres> addressList, int priority) {

		for (CustomerAddres customerAddres : addressList) {
			if (customerAddres.getCustAddrPriority() == priority) {
				return customerAddres;
			}
		}
		if (priority > 1) {
			getHighPriorityAddress(addressList, priority - 1);
		}
		return null;
	}

	/**
	 * Method to fetch customer phone number
	 * 
	 * @param customerPhoneNumList
	 * @param phoneType 
	 * @return
	 */
	public static String getPhoneNumber(List<CustomerPhoneNumber> customerPhoneNumList, String phoneType) {
		String phoneNumber = "";
		if (customerPhoneNumList != null && !customerPhoneNumList.isEmpty()) {
			for (CustomerPhoneNumber phone : customerPhoneNumList) {
				if(StringUtils.equals(phone.getPhoneTypeCode(), phoneType)) {
					phoneNumber = phone.getPhoneNumber();
					break;
				} else {
					phoneNumber = phone.getPhoneNumber();
				}
			}
		}
		return phoneNumber;
	}

	/**
	 * Method to fetch customer document number
	 * 
	 * @param documentList
	 * @param docType
	 * @return
	 */
	public static String getDocumentNumber(List<CustomerDocument> documentList, String docType) {
		String docNumber = "";
		if (documentList != null && !documentList.isEmpty()) {
			for (CustomerDocument document : documentList) {
				if (StringUtils.equals(document.getCustDocCategory(), docType)) {
					if(document.getCustDocTitle()!=null){
						docNumber = document.getCustDocTitle();	
					}
					break;
				}
			}
		}
		return docNumber;
	}

	public static CustomerAddres getCustomerAddress(List<CustomerAddres> addressList, String addrType) {
		CustomerAddres customerAddres = new CustomerAddres();
		if (addressList != null && !addressList.isEmpty()) {
			for (CustomerAddres addres : addressList) {
				if (StringUtils.equals(addres.getCustAddrType(), addrType)) {
					customerAddres = addres;
					break;
				} else {
					customerAddres = addres;
				}
			}
		}
		return customerAddres;
	}
}
