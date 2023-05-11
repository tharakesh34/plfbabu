package com.pennant.pff.customer.exception;

import java.util.Arrays;
import java.util.List;

public enum CustomerDetailsUploadError {

	KYC_CUST_01("CustCif should not be empty."),

	KYC_CUST_02("Invalid CIF."),

	CUST_MNTS_01("Selected CIF is In maintenance : "),

	CUST_MNTS_02("Selected CIF is In Loan Queue : "),

	CUST_MNTS_03("Receipt is under maintenance for selected Customer's Loan : "),

	CUST_MNTS_04("Selected Loan Reference is In-Maintanance on : "),

	KYC_ADD_01("CustCif should not be empty."),

	KYC_ADD_02("FinReference is not valid."),

	KYC_ADD_03("FinReference is not in active."),

	KYC_ADD_04("Customer Address Priority is Mandatory."),

	KYC_ADD_05("Customer House/Building No is Mandatory."),

	KYC_ADD_06("Customer Street is Mandatory."),

	KYC_ADD_07("Customer Pin Code is Mandatory."),

	KYC_ADD_08("Address Type is Invalid."),

	KYC_ADD_09("One Address with Very High priority is mandatory – Cannot Delete Existing Address."),

	KYC_ADD_10("AddressType is mandatory to Give Address Details."),

	KYC_ADD_11("Max 50 letters allowed for the field: "),

	KYC_PHONE_01("Customer Phone Number is Mandatory."),

	KYC_PHONE_02("Customer Phone Priority is Mandatory."),

	KYC_PHONE_03("Customer Phone Type Code is Invalid"),

	KYC_PHONE_04("PhoneType is Mandatory to Give Phone Number Details."),

	KYC_PHONE_05("One PhoneType with Very High priority is mandatory – Cannot Delete Existing Phone Details"),

	KYC_MAIL_01("Customer Email is Mandatory"),

	KYC_MAIL_02("Customer Email Priority is Mandatory"),

	KYC_MAIL_03("Email Type is Mandatory to Give Email Details"),

	KYC_MAIL_04("One Email with Very High priority is mandatory – Cannot Delete Existing Email Details"),

	KYC_FIN_01("Invalid LAN or CIF");

	private String description;

	private CustomerDetailsUploadError(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}

	public static boolean isValidation(String errorCode) {
		return getError(errorCode) != null;
	}

	private static CustomerDetailsUploadError getError(String errorCode) {
		List<CustomerDetailsUploadError> list = Arrays.asList(CustomerDetailsUploadError.values());

		for (CustomerDetailsUploadError it : list) {
			if (it.name().equals(errorCode)) {
				return it;
			}
		}

		return null;
	}

}
