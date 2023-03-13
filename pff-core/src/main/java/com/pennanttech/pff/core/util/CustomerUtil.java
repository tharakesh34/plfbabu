package com.pennanttech.pff.core.util;

import org.apache.commons.lang.StringUtils;

import com.pennant.backend.model.customermasters.Customer;

public class CustomerUtil {

	private CustomerUtil() {
		super();
	}

	public static String getCustomerFullName(Customer customer) {
		StringBuilder custFullName = new StringBuilder();

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
			custFullName.append(" ");
			custFullName.append(mName);
		}

		if (StringUtils.isNotBlank(lName)) {
			custFullName.append(" ");
			custFullName.append(lName);
		}

		return custFullName.toString();
	}

}
