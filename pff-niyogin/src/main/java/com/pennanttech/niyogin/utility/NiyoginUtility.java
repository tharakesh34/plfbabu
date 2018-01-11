package com.pennanttech.niyogin.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
	 * Method for sort the given CustomerEMailList based on their Priority High to Low.
	 * 
	 * @param list
	 */
	public static void sortCustomerEmail(List<CustomerEMail> list) {
		if (list != null && !list.isEmpty()) {
			Collections.sort(list, new Comparator<CustomerEMail>() {
				@Override
				public int compare(CustomerEMail detail1, CustomerEMail detail2) {
					return detail2.getCustEMailPriority() - detail1.getCustEMailPriority();
				}
			});
		}
	}


	/**
	 * Method for sort the given CustomerPhoneNumberList based on their Priority High to Low.
	 * 
	 * @param list
	 */
	public static void sortCustomerPhoneNumber(List<CustomerPhoneNumber> list) {
		if (list != null && !list.isEmpty()) {
			Collections.sort(list, new Comparator<CustomerPhoneNumber>() {
				@Override
				public int compare(CustomerPhoneNumber detail1, CustomerPhoneNumber detail2) {
					return detail2.getPhoneTypePriority() - detail1.getPhoneTypePriority();
				}
			});
		}
	}

	/**
	 * Method for sort the given CustomerAddresList based on their Priority High to Low.
	 * 
	 * @param list
	 */
	public static void sortCustomerAddres(List<CustomerAddres> list) {
		if (list != null && !list.isEmpty()) {
			Collections.sort(list, new Comparator<CustomerAddres>() {
				@Override
				public int compare(CustomerAddres detail1, CustomerAddres detail2) {
					return detail2.getCustAddrPriority() - detail1.getCustAddrPriority();
				}
			});
		}
	}
	
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

	public static String getPhoneNumber(List<CustomerPhoneNumber> phoneList) {
		String phoneNumber = "";
		if (phoneList != null && !phoneList.isEmpty()) {
			if (phoneList.size() > 1) {
				NiyoginUtility.sortCustomerPhoneNumber(phoneList);
			}
			CustomerPhoneNumber phone = phoneList.get(0);
			phoneNumber = phone.getPhoneNumber();
		}
		return phoneNumber;
	}
	
	public static CustomerPhoneNumber getPhone(List<CustomerPhoneNumber> phoneList) {
		CustomerPhoneNumber phone = new CustomerPhoneNumber();
		if (phoneList != null && !phoneList.isEmpty()) {
			if (phoneList.size() > 1) {
				NiyoginUtility.sortCustomerPhoneNumber(phoneList);
			}
			phone = phoneList.get(0);
		}
		return phone;
	}
	
	public static String getEmail(List<CustomerEMail> customerEMailList) {
		String emailId = "";
		if (customerEMailList != null && !customerEMailList.isEmpty()) {
			if (customerEMailList.size() > 1) {
				NiyoginUtility.sortCustomerEmail(customerEMailList);
			}
			CustomerEMail email = customerEMailList.get(0);
			emailId = email.getCustEMail();
		}
		return emailId;
	}
	
	public static CustomerAddres getAddress(List<CustomerAddres> addressList) {
		CustomerAddres address = new CustomerAddres();
		if (addressList != null && !addressList.isEmpty()) {
			if (addressList.size() > 1) {
				NiyoginUtility.sortCustomerAddres(addressList);
			}
			address = addressList.get(0);
		}
		return address;
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

	public static String formatRate(double value, int decPos) {
		StringBuffer sb = new StringBuffer("###,###,######");

		if (value != 0) {
			String subString = String.valueOf(value).substring(String.valueOf(value).indexOf('.'));
			if (!subString.contains("E")) {
				
				if (decPos > 0) {
					sb.append('.');
					for (int i = 0; i < decPos; i++) {
						sb.append('0');
					}
				}
				
				java.text.DecimalFormat df = new java.text.DecimalFormat();
				df.applyPattern(sb.toString());
				String returnResult = df.format(value);
				returnResult = returnResult.replaceAll("[0]*$", "");
				if(returnResult.endsWith(".")){
					returnResult = returnResult + "00";
				}else if(returnResult.contains(".") && returnResult.substring(returnResult.indexOf('.')+1).length() == 1){
					returnResult = returnResult + "0";
				}
				
				if(returnResult.startsWith(".")){
					returnResult = "0"+returnResult;
				}
				return returnResult;
			} else {
				
				String actValue = String.valueOf(value).substring(0,String.valueOf(value).indexOf('.'));
				int powValue = Integer.parseInt(String.valueOf(value).substring(String.valueOf(value).indexOf('E')+1));
				
				String string = "0.";
				if(powValue < 0){
					powValue = 0-powValue;
					if(powValue > 0){
						for (int i = 0; i < powValue-1; i++) {
							string = string.concat("0");
						}
					}
				}
				
				string += actValue;
				return string;
			}
		} else {
			String string = "0.";
			for (int i = 0; i < 2; i++) {
				string =string.concat("0");
			}
			return string;

		}
	}
}
