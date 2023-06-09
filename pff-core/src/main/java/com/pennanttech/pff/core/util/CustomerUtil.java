package com.pennanttech.pff.core.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.util.PennantConstants;

public class CustomerUtil {

	private CustomerUtil() {
		super();
	}

	public static String getCustomerFullName(Customer customer) {
		StringBuilder custFullName = new StringBuilder();

		if (PennantConstants.PFF_CUSTCTG_CORP.equals(customer.getCustCtgCode())
				|| PennantConstants.PFF_CUSTCTG_SME.equals(customer.getCustCtgCode())) {
			custFullName.append(customer.getCustShrtName());
			return custFullName.toString();
		}

		String salutation = StringUtils.trimToEmpty(customer.getCustSalutationCode());
		String fName = StringUtils.trimToEmpty(customer.getCustFName());
		String mName = StringUtils.trimToEmpty(customer.getCustMName());
		String lName = StringUtils.trimToEmpty(customer.getCustLName());

		if (StringUtils.isNotBlank(salutation)) {
			custFullName.append(StringUtils.trimToEmpty(salutation));
		}

		if (StringUtils.isNotBlank(fName)) {
			custFullName.append(fName);
		}

		if (StringUtils.isNotBlank(mName)) {
			if (StringUtils.isNotBlank(fName) && custFullName.length() > 0) {
				custFullName.append(" ");
			}
			custFullName.append(mName);
		}

		if (StringUtils.isNotBlank(lName)) {
			custFullName.append(" ");
			custFullName.append(lName);
		}

		return custFullName.toString();
	}

	public static String getCustomerFullAddress(List<CustomerAddres> ca) {
		StringBuilder address = new StringBuilder();

		for (CustomerAddres add : ca) {

			if (add.getCustAddrPriority() != 5) {
				continue;
			}

			String custHouseNumber = StringUtils.trimToEmpty(add.getCustAddrHNbr());
			String custStreet = StringUtils.trimToEmpty(add.getCustAddrStreet());
			String custLocality = StringUtils.trimToEmpty(add.getCustAddrLine2());
			String custLandMark = StringUtils.trimToEmpty(add.getCustAddrLine1());
			String custCity = StringUtils.trimToEmpty(add.getLovDescCustAddrCityName());
			String custState = StringUtils.trimToEmpty(add.getLovDescCustAddrProvinceName());
			String custAddrCountry = StringUtils.trimToEmpty(add.getLovDescCustAddrCountryName());
			String custZip = StringUtils.trimToEmpty(add.getCustAddrZIP());

			if (StringUtils.isNotBlank(custHouseNumber)) {
				address.append(Labels.getLabel("label_CustomerAddresDialog_CustAddrHNbr.value") + ":");
				address.append(custHouseNumber);
			}

			if (StringUtils.isNotBlank(custStreet)) {
				if (address.length() > 0) {
					address.append(", ");
				}
				address.append(Labels.getLabel("label_CustomerAddresDialog_CustAddrStreet.value") + ":");
				address.append(custStreet);
			}

			if (StringUtils.isNotBlank(custLocality)) {
				if (address.length() > 0) {
					address.append(", ");
				}
				address.append(Labels.getLabel("label_CustomerAddresDialog_CustAddrLine2.value") + ":");
				address.append(custLocality);
			}

			if (StringUtils.isNotBlank(custLandMark)) {
				if (address.length() > 0) {
					address.append(", ");
				}
				address.append(Labels.getLabel("label_CustomerAddresDialog_CustAddrLine1.value") + ": ");
				address.append(custLandMark);
			}

			if (StringUtils.isNotBlank(custCity)) {
				if (address.length() > 0) {
					address.append(", ");
				}
				address.append(custCity);
			}

			if (StringUtils.isNotBlank(custState)) {
				if (address.length() > 0) {
					address.append(", ");
				}
				address.append(custState);
			}

			if (StringUtils.isNotBlank(custAddrCountry)) {
				if (address.length() > 0) {
					address.append(", ");
				}
				address.append(custAddrCountry);
			}

			if (StringUtils.isNotBlank(custZip)) {
				if (address.length() > 0) {
					address.append(", ");
				}
				address.append(custZip);
			}

		}

		return address.toString();

	}

}
