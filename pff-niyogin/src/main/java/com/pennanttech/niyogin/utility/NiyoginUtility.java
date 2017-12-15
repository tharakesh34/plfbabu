package com.pennanttech.niyogin.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
	public static CustomerPhoneNumber getHighPriorityPhone(List<CustomerPhoneNumber> customerPhoneNumList,
			int priority) {
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
	 * Method to fetch customer phone number.
	 * 
	 * @param customerPhoneNumList
	 * @param phoneType
	 * @return
	 */
	public static String getPhoneNumber(List<CustomerPhoneNumber> customerPhoneNumList, String phoneType) {
		String phoneNumber = "";
		if (customerPhoneNumList != null && !customerPhoneNumList.isEmpty()) {
			for (CustomerPhoneNumber phone : customerPhoneNumList) {
				if (StringUtils.equals(phone.getPhoneTypeCode(), phoneType)) {
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
	 * Method to fetch customer document number.
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
					if (document.getCustDocTitle() != null) {
						docNumber = document.getCustDocTitle();
					}
					break;
				}
			}
		}
		return docNumber;
	}

	/**
	 * Method for fetch the customer address.
	 * 
	 * @param addressList
	 * @param addrType
	 * @return
	 */
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

	/**
	 * Method for format the date according to the given pattern.
	 * 
	 * @param inputDate
	 * @param pattern
	 * @return
	 */
	public static String formatDate(Date inputDate, String pattern) {
		String formattedDate = null;
		if (inputDate == null) {
			return null;
		}
		SimpleDateFormat dateFormatter = new SimpleDateFormat(pattern);
		formattedDate = dateFormatter.format(inputDate);
		return formattedDate;
	}

	/**
	 * Method for return the number Of months between two dates
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getMonthsBetween(java.util.Date date1, java.util.Date date2) {

		if (date1 == null || date2 == null) {
			return -1;
		}
		if (date1.before(date2)) {
			java.util.Date temp = date2;
			date2 = date1;
			date1 = temp;
		}
		int years = convert(date1).get(Calendar.YEAR) - convert(date2).get(Calendar.YEAR);
		int months = convert(date1).get(Calendar.MONTH) - convert(date2).get(Calendar.MONTH);
		months += years * 12;
		if (convert(date1).get(Calendar.DATE) < convert(date2).get(Calendar.DATE)) {
			months--;
		}

		return months;
	}

	public static GregorianCalendar convert(java.util.Date date) {
		if (date == null) {
			return null;
		}
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		return gc;
	}

	/**
	 * Count Number of days between Util Dates
	 * 
	 * @param date1
	 *            (Date)
	 * 
	 * @param date2
	 *            (Date)
	 * 
	 * @return int
	 */
	public static int getDaysBetween(java.util.Date date1, java.util.Date date2) {

		if (date1 == null || date2 == null) {
			return -1;
		}
		GregorianCalendar gc1 = convert(date1);
		GregorianCalendar gc2 = convert(date2);
		if (gc1.get(Calendar.YEAR) == gc2.get(Calendar.YEAR)) {
			return Math.abs(gc1.get(Calendar.DAY_OF_YEAR) - gc2.get(Calendar.DAY_OF_YEAR));
		}
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		long days = (time1 - time2) / (1000 * 60 * 60 * 24);

		return Math.abs((int) days);
	}

}
