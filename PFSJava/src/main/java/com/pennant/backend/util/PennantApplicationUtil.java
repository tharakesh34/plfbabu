package com.pennant.backend.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.zkoss.zkplus.spring.SpringUtil;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.PagedListService;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pff.constants.AccountingEvent;

public class PennantApplicationUtil {
	private static final Logger logger = LogManager.getLogger(PennantApplicationUtil.class);
	private static final SearchProcessor SEARCH_PROCESSOR = getSearchProcessor();
	private static Country defaultCountry = null;

	public static boolean matches(BigDecimal val1, BigDecimal val2) {
		if (val1 == null) {
			return val2 == null;
		}

		if (val2 == null) {
			return false;
		}

		return val1.compareTo(val2) == 0;
	}

	public static boolean matches(Long val1, Long val2) {
		if (val1 == null) {
			return val2 == null;
		}

		if (val2 == null) {
			return false;
		}

		return val1.compareTo(val2) == 0;
	}

	public static BigDecimal unFormateAmount(BigDecimal amount, int dec) {
		if (amount == null) {
			return BigDecimal.ZERO;
		}

		BigInteger bigInteger = amount.multiply(BigDecimal.valueOf(Math.pow(10, dec))).toBigInteger();
		return new BigDecimal(bigInteger);
	}

	public static BigDecimal unFormateAmount(String amount, int dec) {
		if (StringUtils.isEmpty(amount) || StringUtils.isBlank(amount)) {
			return BigDecimal.ZERO;
		}
		BigInteger bigInteger = new BigDecimal(amount.replace(",", "")).multiply(BigDecimal.valueOf(Math.pow(10, dec)))
				.toBigInteger();
		return new BigDecimal(bigInteger);
	}

	public static BigDecimal formateAmount(BigDecimal amount, int decimals) {
		return CurrencyUtil.parse(amount, decimals);
	}

	public static String amountFormate(BigDecimal amount, int dec) {
		return CurrencyUtil.format(amount, dec);
	}

	public static String formatAmount(BigDecimal value, int decPos) {
		return formatAmount(value, decPos, false);
	}

	private static String formatAmount(BigDecimal value, int decPos, boolean debitCreditSymbol) {
		if (value != null && value.compareTo(BigDecimal.ZERO) != 0) {
			DecimalFormat df = new DecimalFormat();

			String format = "";

			if (ImplementationConstants.INDIAN_IMPLEMENTATION) {
				format = "###,###,###,###";// Can be modified for Local Currency format indication
			} else {
				format = "###,###,###,###";
			}

			StringBuilder sb = new StringBuilder(format);
			boolean negSign = false;

			if (decPos > 0) {
				sb.append('.');
				for (int i = 0; i < decPos; i++) {
					sb.append('0');
				}

				if (value.compareTo(BigDecimal.ZERO) == -1) {
					negSign = true;
					value = value.multiply(new BigDecimal("-1"));
				}

				if (negSign) {
					value = value.multiply(new BigDecimal("-1"));
				}
			}

			if (debitCreditSymbol) {
				String s = sb.toString();
				sb.append(" 'Cr';").append(s).append(" 'Dr'");
			}

			df.applyPattern(sb.toString());
			String returnValue = df.format(value);
			if (returnValue.startsWith(".")) {
				returnValue = "0" + returnValue;
			}
			return returnValue;
		} else {
			String string = "0";
			if (decPos > 0) {
				string = ".";
				// Integral part of a component default value requires zero or
				// not. EX: If requires, value will be like 0.00, if not, value
				// will be like .00
				if (getAlwIntegralPartZero()) {
					string = "0.";
				}
				for (int i = 0; i < decPos; i++) {
					string = string.concat("0");
				}
			}
			return string;
		}
	}

	public static String getAmountFormate(int dec) {
		String formateString = PennantConstants.defaultAmountFormate;

		boolean alwIntegralPartZero = getAlwIntegralPartZero();

		if (ImplementationConstants.INDIAN_IMPLEMENTATION) {
			switch (dec) {
			case 0:
				formateString = PennantConstants.in_amountFormate0;
				if (alwIntegralPartZero) {
					formateString = PennantConstants.integralAmtFormat0;
				}
				break;
			case 1:
				formateString = PennantConstants.in_amountFormate1;
				if (alwIntegralPartZero) {
					formateString = PennantConstants.integralAmtFormat1;
				}
				break;
			case 2:
				formateString = PennantConstants.in_amountFormate2;
				if (alwIntegralPartZero) {
					formateString = PennantConstants.integralAmtFormat2;
				}
				break;
			case 3:
				formateString = PennantConstants.in_amountFormate3;
				if (alwIntegralPartZero) {
					formateString = PennantConstants.integralAmtFormat3;
				}
				break;
			case 4:
				formateString = PennantConstants.in_amountFormate4;
				if (alwIntegralPartZero) {
					formateString = PennantConstants.integralAmtFormat4;
				}
				break;
			}

		} else {
			switch (dec) {
			case 0:
				formateString = PennantConstants.amountFormate0;
				if (alwIntegralPartZero) {
					formateString = PennantConstants.integralAmtFormate0;
				}
				break;
			case 1:
				formateString = PennantConstants.amountFormate1;
				if (alwIntegralPartZero) {
					formateString = PennantConstants.integralAmtFormate1;
				}
				break;
			case 2:
				formateString = PennantConstants.amountFormate2;
				if (alwIntegralPartZero) {
					formateString = PennantConstants.integralAmtFormate2;
				}
				break;
			case 3:
				formateString = PennantConstants.amountFormate3;
				if (alwIntegralPartZero) {
					formateString = PennantConstants.integralAmtFormate3;
				}
				break;
			case 4:
				formateString = PennantConstants.amountFormate4;
				if (alwIntegralPartZero) {
					formateString = PennantConstants.integralAmtFormate4;
				}
				break;
			}
		}
		return formateString;
	}

	public static String getRateFormate(int dec) {
		String formateString = PennantConstants.rateFormate2;

		switch (dec) {
		case 2:
			formateString = PennantConstants.rateFormate2;
			break;
		case 3:
			formateString = PennantConstants.rateFormate3;
			break;
		case 4:
			formateString = PennantConstants.rateFormate4;
			break;
		case 5:
			formateString = PennantConstants.rateFormate5;
			break;
		case 6:
			formateString = PennantConstants.rateFormate6;
			break;
		case 7:
			formateString = PennantConstants.rateFormate7;
			break;
		case 8:
			formateString = PennantConstants.rateFormate8;
			break;
		case 9:
			formateString = PennantConstants.rateFormate9;
			break;
		case 10:
			formateString = PennantConstants.rateFormate10;
			break;
		}
		return formateString;
	}

	public static String formatRate(double value, int decPos) {
		StringBuilder sb = new StringBuilder("###,###,######");

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
				if (returnResult.endsWith(".")) {
					returnResult = returnResult + "00";
				} else if (returnResult.contains(".")
						&& returnResult.substring(returnResult.indexOf('.') + 1).length() == 1) {
					returnResult = returnResult + "0";
				}

				if (returnResult.startsWith(".")) {
					returnResult = "0" + returnResult;
				}
				return returnResult;
			} else {

				String actValue = String.valueOf(value).substring(0, String.valueOf(value).indexOf('.'));
				int powValue = Integer
						.parseInt(String.valueOf(value).substring(String.valueOf(value).indexOf('E') + 1));

				String string = "0.";
				if (powValue < 0) {
					powValue = 0 - powValue;
					if (powValue > 0) {
						for (int i = 0; i < powValue - 1; i++) {
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
				string = string.concat("0");
			}
			return string;

		}
	}

	public static String formateLong(long longValue) {
		String pattern = "###,###,###,###";
		java.text.DecimalFormat df = new java.text.DecimalFormat();
		df.applyPattern(pattern);
		return df.format(longValue);
	}

	public static String formateInt(int intValue) {
		String pattern = "###,###,###,###";
		java.text.DecimalFormat df = new java.text.DecimalFormat();
		df.applyPattern(pattern);
		return df.format(intValue);
	}

	public static BigDecimal getPercentageValue(BigDecimal amount, BigDecimal percent) {
		BigDecimal returnAmount = BigDecimal.ZERO;

		if (amount != null) {
			returnAmount = (amount.multiply(unFormateAmount(percent, 2).divide(new BigDecimal(100))))
					.divide(new BigDecimal(100), RoundingMode.HALF_DOWN);
		}
		return returnAmount;
	}

	public static String formateBoolean(int intValue) {

		if (intValue == 1) {
			return String.valueOf(true);
		} else {
			return String.valueOf(false);
		}
	}

	public static String formatAccountNumber(String accountNumber) {
		/*
		 * if (!StringUtils.trimToEmpty(accountNumber).equals("") && accountNumber.length() == 13) { StringBuilder
		 * builder = new StringBuilder(); builder.append(accountNumber.substring(0, 4)); builder.append("-");
		 * builder.append(accountNumber.substring(4, 10)); builder.append("-");
		 * builder.append(accountNumber.substring(10, 13)); return builder.toString(); }
		 */
		return accountNumber;
	}

	public static String unFormatAccountNumber(String accountNumber) {
		/*
		 * if (!StringUtils.trimToEmpty(accountNumber).equals("")) { return
		 * StringUtils.trim(accountNumber.replaceAll("-", "")); }
		 */
		return accountNumber;
	}

	public static String formatPhoneNumber(String countryCode, String areaCode, String phNumber) {
		String phoneNumber = "";
		if (StringUtils.isNotBlank(countryCode) && !"null".equalsIgnoreCase(countryCode)) {
			phoneNumber = countryCode + "-";
		}
		if (StringUtils.isNotBlank(areaCode) && !"null".equalsIgnoreCase(areaCode)) {
			phoneNumber = phoneNumber + areaCode + "-";
		}
		if (StringUtils.isNotBlank(phNumber)) {
			phoneNumber = phoneNumber + phNumber;
		}
		return phoneNumber;
	}

	public static String[] unFormatPhoneNumber(String phoneNumber) {
		String[] phoneNum = new String[3];
		if (StringUtils.isNotBlank(phoneNumber) && phoneNumber.contains("-")) {
			phoneNum = phoneNumber.split("-");
		}
		return phoneNum;
	}

	public static String formatEIDNumber(String num) {
		if (ImplementationConstants.INDIAN_IMPLEMENTATION) {
			return formatSequence(num, new int[] { 4, 4, 4 }, "-");
		} else {
			return formatSequence(num, new int[] { 3, 4, 7, 1 }, "-");
		}

	}

	public static String unFormatEIDNumber(String num) {
		return unFormatSequence(num, "-");
	}

	public static String getSavingStatus(String roleCode, String nextRoleCode, String reference, String moduleCode,
			String recordStatus, String userId) {
		String roleCodeDesc = "";
		if (StringUtils.isBlank(nextRoleCode) || roleCode.equals(nextRoleCode)
				|| StringUtils.trimToEmpty(recordStatus).equalsIgnoreCase(PennantConstants.RCD_STATUS_SAVED)) {
			return moduleCode + " with Reference: " + reference + " " + recordStatus + " successfully.";
		} else {

			JdbcSearchObject<SecurityRole> searchObject = new JdbcSearchObject<SecurityRole>(SecurityRole.class);
			if (nextRoleCode.contains(",")) {
				String roleCodes[] = nextRoleCode.split(",");
				searchObject.addFilterIn("RoleCd", Arrays.asList(roleCodes));
			} else {
				searchObject.addFilterEqual("RoleCd", nextRoleCode);
			}
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			List<SecurityRole> rolesList = pagedListService.getBySearchObject(searchObject);
			if (rolesList != null && !rolesList.isEmpty()) {
				for (SecurityRole securityRole : rolesList) {
					if (StringUtils.isEmpty(roleCodeDesc)) {
						roleCodeDesc = securityRole.getRoleDesc();
					} else {
						roleCodeDesc = roleCodeDesc.concat(" And " + securityRole.getRoleDesc());
					}
				}
			}

			return moduleCode + " with Reference: " + reference + " moved to "
					+ (StringUtils.isBlank(roleCodeDesc) ? "" : roleCodeDesc) + " successfully.";
		}
	}

	public static String getSavingStatus(String roleCode, String nextRoleCode, String reference, String moduleCode,
			String recordStatus) {
		String roleCodeDesc = "";
		if (StringUtils.isBlank(nextRoleCode) || roleCode.equals(nextRoleCode)
				|| StringUtils.trimToEmpty(recordStatus).equalsIgnoreCase(PennantConstants.RCD_STATUS_SAVED)) {
			return moduleCode + " with Reference: " + reference + " " + recordStatus + " successfully.";
		} else {
			JdbcSearchObject<SecurityRole> searchObject = new JdbcSearchObject<SecurityRole>(SecurityRole.class);
			if (nextRoleCode.contains(",")) {
				String roleCodes[] = nextRoleCode.split(",");
				searchObject.addFilterIn("RoleCd", Arrays.asList(roleCodes));
			} else {
				searchObject.addFilterEqual("RoleCd", nextRoleCode);
			}
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			List<SecurityRole> rolesList = pagedListService.getBySearchObject(searchObject);
			if (rolesList != null && !rolesList.isEmpty()) {
				for (SecurityRole securityRole : rolesList) {
					if (StringUtils.isEmpty(roleCodeDesc)) {
						roleCodeDesc = securityRole.getRoleDesc();
					} else {
						roleCodeDesc = roleCodeDesc.concat(" And " + securityRole.getRoleDesc());
					}
				}
			}
			return moduleCode + " with Reference: " + reference + " moved to "
					+ (StringUtils.isBlank(roleCodeDesc) ? "" : roleCodeDesc) + " successfully.";
		}
	}

	public static List<Property> getRoles(String[] roleCodes) {
		List<Property> roles = new ArrayList<>();

		Search search = new Search();
		search.setSearchClass(SecurityRole.class);
		search.addTabelName("SecRoles");
		search.addField("RoleCd");
		search.addField("RoleDesc");
		search.addFilterIn("RoleCd", Arrays.asList(roleCodes));

		List<SecurityRole> securityRoles = SEARCH_PROCESSOR.getResults(search);

		Property property;
		for (SecurityRole role : securityRoles) {
			property = new Property(role.getRoleCd(), role.getRoleDesc());
			roles.add(property);
		}

		return roles;
	}

	public static List<SecurityRole> getRoleCodeDesc(String roleCode) {
		JdbcSearchObject<SecurityRole> searchObject = new JdbcSearchObject<SecurityRole>(SecurityRole.class);
		if (roleCode.contains(",")) {
			String roleCodes[] = roleCode.split(",");
			searchObject.addFilterIn("RoleCd", Arrays.asList(roleCodes));
		} else {
			searchObject.addFilterEqual("RoleCd", roleCode);
		}

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		return pagedListService.getBySearchObject(searchObject);
	}

	public static String getUserDesc(long userID) {
		Search search = new Search(SecurityUser.class);

		search.addField("UsrFName");
		search.addField("UsrMName");
		search.addField("UsrLName");
		search.addFilterEqual("UsrID", userID);

		SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
		List<SecurityUser> usersList = searchProcessor.getResults(search);
		SecurityUser securityUser = usersList.get(0);
		return getFullName(securityUser.getUsrFName(), securityUser.getUsrMName(), securityUser.getUsrLName());
	}

	public static String getWorkFlowType(long workflowID) {
		Search search = new Search(WorkFlowDetails.class);
		search.addFilterEqual("WorkFlowID", workflowID);
		search.addField("WorkFlowType");

		SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
		List<WorkFlowDetails> usersList = searchProcessor.getResults(search);
		return usersList.get(0).getWorkFlowType();
	}

	public static String getFullName(String firstName, String middleName, String lastName) {
		String fullName = "", delimiter = " ";
		if (!StringUtils.isBlank(firstName)) {
			fullName = firstName.trim();
		}
		if (!StringUtils.isBlank(middleName)) {
			if (StringUtils.isEmpty(fullName)) {
				fullName = middleName.trim();
			} else {
				fullName = fullName + delimiter + middleName.trim();
			}
		}
		if (!StringUtils.isBlank(lastName)) {
			if (StringUtils.isEmpty(fullName)) {
				fullName = lastName.trim();
			} else {
				fullName = fullName + delimiter + lastName.trim();
			}
		}
		return fullName;
	}

	public static BigDecimal getDSR(Object dscr) {
		if (dscr == null) {
			dscr = BigDecimal.ZERO;
		} else if (new BigDecimal(dscr.toString()).longValue() > FinanceConstants.CUST_MAX_DSR) {
			dscr = FinanceConstants.CUST_MAX_DSR;
		}
		return new BigDecimal(dscr.toString());
	}

	/**
	 * @param number
	 * @param charSequence
	 * @param delimiter
	 * @return Example : input number = "123456789",charSequence = new int[] {2,4,3}) , delimiter = "-" then output =
	 *         "12-3456-789"
	 */
	public static String formatSequence(String number, int[] charSequence, String delimiter) {
		if (!StringUtils.isBlank(number)) {
			if (charSequence != null && charSequence.length > 0) {
				int maxlength = 0;
				for (int charCount : charSequence) {
					if (charCount <= 0) {
						return number;
					}
					maxlength = maxlength + charCount;
				}
				String unformatedNumber = unFormatSequence(number, delimiter);
				if (unformatedNumber.length() == maxlength) {
					String formatNumber = "";
					int startIndex = 0;
					for (int noOfChars : charSequence) {
						if (StringUtils.isEmpty(formatNumber)) {
							formatNumber = unformatedNumber.substring(startIndex, noOfChars);
						} else {
							formatNumber = formatNumber
									.concat(delimiter + unformatedNumber.substring(startIndex, startIndex + noOfChars));
						}
						startIndex = startIndex + noOfChars;
					}
					return formatNumber;
				}
			}
		}
		return number;
	}

	public static String unFormatSequence(String number, String delimiter) {
		if (StringUtils.isNotBlank(number) && number.contains(delimiter)) {
			number = number.replace(delimiter, "");
		}
		return number;
	}

	/**
	 * Purpose: To fetch description for master data when customer details are retrieved from interface.
	 * 
	 * To fetch the description from master table when the code , module and description field specified. Since method
	 * is used for only fetching the description,it will return the null if not found. Table name is not mandatory and
	 * null handled. All the exception are suppressed.
	 * 
	 * @param moduleName
	 * @param filedName
	 * @param filters
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getDBDescription(String moduleName, String tablename, String filedName, Filter[] filters) {
		try {
			ModuleMapping module = PennantJavaUtil.getModuleMap(moduleName);
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

			JdbcSearchObject searchObject = new JdbcSearchObject(module.getModuleClass());
			searchObject.addField(filedName);

			if (!StringUtils.isBlank(tablename)) {
				searchObject.addTabelName(tablename);
			}

			if (filters != null) {
				for (int i = 0; i < filters.length; i++) {
					searchObject.addFilter(filters[i]);
				}
			}

			String fieldMethod = "get" + filedName;

			List appList = pagedListService.getBySearchObject(searchObject);
			if (appList != null && !appList.isEmpty()) {

				return (String) appList.get(0).getClass().getMethod(fieldMethod).invoke(appList.get(0));
			}
		} catch (Exception e) {
			logger.debug(e);
		}

		return null;
	}

	/**
	 * This method is used to encode the data
	 * 
	 * @param form
	 * @return byte[]
	 */
	public static byte[] encode(byte[] form) {
		logger.debug("Entering");

		byte[] enCodedData = null;

		if (form != null && form.length > 0) {
			Base64 base64 = new Base64();
			enCodedData = base64.encode(form);
		}

		logger.debug("Leaving");
		return enCodedData;
	}

	/**
	 * This method is used to decode the data
	 * 
	 * @param form
	 * @return byte[]
	 */
	public static byte[] decode(byte[] form) {
		logger.debug("Entering");

		byte[] deCodedData = null;

		if (form != null && form.length > 0) {
			Base64 base64 = new Base64();
			deCodedData = base64.decode(form);
		}

		logger.debug("Leaving");
		return deCodedData;
	}

	public static String getSecRoleCodeDesc(String roleCode) {
		logger.debug("Entering");
		Search search = new Search(SecurityRole.class);
		search.addFilterEqual("RoleCd", roleCode);
		search.addField("RoleDesc");

		SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
		List<SecurityRole> securityRolesList = searchProcessor.getResults(search);

		logger.debug("Leaving");
		return securityRolesList.size() > 0 ? securityRolesList.get(0).getRoleDesc() : "";
	}

	public static String getEventCode(Date date) {
		String feeEvent = AccountingEvent.ADDDBSP;

		if (date.after(SysParamUtil.getAppDate())) {
			if (ImplementationConstants.ALLOW_ADDDBSF) {
				feeEvent = AccountingEvent.ADDDBSF;
			}
		}

		return feeEvent;
	}

	public static String getPanNumber(List<CustomerDocument> customerDetails) {
		String pannumber = "";
		if (customerDetails != null && !customerDetails.isEmpty()) {
			String[] pancards = null;
			String panCard = StringUtils.trimToEmpty(SysParamUtil.getValueAsString("PAN_DOC_TYPE"));
			if (panCard.contains(PennantConstants.DELIMITER_COMMA)) {
				pancards = panCard.split(PennantConstants.DELIMITER_COMMA);
			} else {
				pancards = new String[1];
				pancards[0] = panCard;
			}

			if (pancards != null) {
				for (int i = 0; i < pancards.length; i++) {
					for (CustomerDocument customerDocument : customerDetails) {
						if (StringUtils.equals(pancards[i], customerDocument.getCustDocCategory())) {
							pannumber = StringUtils.trimToEmpty(customerDocument.getCustDocTitle());
							return pannumber;
						}
					}
				}
			}
		}
		return pannumber;
	}

	/*
	 * public static byte[] getDocumentImage(long docID) { PagedListService pagedListService = (PagedListService)
	 * SpringUtil.getBean("pagedListService"); JdbcSearchObject<DocumentManager> searchObject = new
	 * JdbcSearchObject<DocumentManager>(DocumentManager.class); searchObject.addFilterEqual("Id", docID);
	 * searchObject.addTabelName("DocumentManager"); searchObject.addField("DocImage"); List<DocumentManager>
	 * documentManagers = pagedListService.getBySearchObject(searchObject); if (documentManagers != null &&
	 * !documentManagers.isEmpty()) { return documentManagers.get(0).getDocImage(); } return null; }
	 */

	/**
	 * Method to get the RightName of ExtendedField Based on Module, SubModule and InputType.
	 * 
	 * @param detail
	 * @return
	 */
	public static String getExtendedFieldRightName(ExtendedFieldDetail detail) {
		logger.trace(Literal.ENTERING);

		String rightName = null;
		if (detail != null) {
			String pageName = detail.getLovDescModuleName() + "_" + detail.getLovDescSubModuleName();
			if (detail.isInputElement()) {
				rightName = pageName + "_" + detail.getFieldName();
			} else {
				rightName = pageName + "_" + detail.getFieldType() + "_" + detail.getFieldName();
			}
		}

		logger.trace(Literal.LEAVING);
		return rightName;
	}

	/**
	 * Method for get the ExtendedFields pageName.
	 * 
	 * @param extendedFieldHeader
	 * @return
	 */
	public static String getExtendedFieldPageName(ExtendedFieldHeader extendedFieldHeader) {
		String pageName = null;
		if (extendedFieldHeader != null) {
			pageName = extendedFieldHeader.getModuleName() + "_" + extendedFieldHeader.getSubModuleName();
		}
		return pageName;
	}

	public static Map<String, String> getPrimaryIdAttributes(String custCategory) {
		Map<String, String> result = new HashMap<>();

		switch (custCategory) {
		case "RETAIL":
			result.put("TYPE", SysParamUtil.getValueAsString("CUST_PRIMARY_ID_RETL"));
			// result.put("LABEL",
			// "label_CoreCustomerDialog_PrimaryID_Retl.value");
			result.put("MANDATORY",
					"Y".equals(SysParamUtil.getValueAsString("CUST_PRIMARY_ID_REQ")) ? "true" : "false");
			result.put("MOBILEMANDATORY", ImplementationConstants.CUST_MOB_MANDATORY ? "true" : "false");
			result.put("REGEX", "REGEX_" + SysParamUtil.getValueAsString("CUST_PRIMARY_ID_RETL") + "_NUMBER");
			break;
		case "CORP":
		case "SME":
			result.put("TYPE", SysParamUtil.getValueAsString("CUST_PRIMARY_ID_CORP"));
			result.put("LABEL", "label_CoreCustomerDialog_PrimaryID_Corp.value");
			result.put("MANDATORY",
					"Y".equals(SysParamUtil.getValueAsString("CUST_PRIMARY_ID_REQ")) ? "true" : "false");
			result.put("MOBILEMANDATORY", ImplementationConstants.CUST_MOB_MANDATORY ? "true" : "false");
			result.put("REGEX", "REGEX_" + SysParamUtil.getValueAsString("CUST_PRIMARY_ID_CORP") + "_NUMBER");
			break;
		default:
			result.put("TYPE", "");
			result.put("LABEL", "label_CoreCustomerDialog_PrimaryID.value");
			result.put("MANDATORY", "false");
			result.put("MOBILEMANDATORY", "false");
			result.put("REGEX", "");
		}

		String type = result.get("TYPE");
		int maxLength = 100;

		if ("PAN".equals(type)) {
			result.put("LABEL", "label_CoreCustomerDialog_PAN.value");
			maxLength = LengthConstants.LEN_PAN;
		} else if ("AADHAAR".equals(type)) {
			result.put("LABEL", "label_CoreCustomerDialog_AADHAR.value");
			maxLength = LengthConstants.LEN_AADHAAR;
		} else if ("EID".equals(type)) {
			result.put("LABEL", "label_CoreCustomerDialog_EID.value");
			maxLength = LengthConstants.LEN_EID;
		}

		result.put("LENGTH", String.valueOf(maxLength));

		return result;
	}

	public static String getLabelDesc(String value, List<ValueLabel> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getValue().equalsIgnoreCase(value)) {
				return list.get(i).getLabel();
			}
		}
		return "";
	}

	public static String getValueDesc(String label, List<ValueLabel> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getLabel().equalsIgnoreCase(label)) {
				return list.get(i).getValue();
			}
		}
		return "";
	}

	public static SearchProcessor getSearchProcessor() {
		return (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
	}

	public static Country getDefaultCounty() {
		if (defaultCountry == null) {
			Search search = new Search(Country.class);
			search.addField("CountryCode");
			search.addFilter(new Filter("SystemDefault", 1, Filter.OP_EQUAL));

			List<Country> countries = SEARCH_PROCESSOR.getResults(search);

			if (CollectionUtils.isNotEmpty(countries)) {
				defaultCountry = (Country) SEARCH_PROCESSOR.getResults(search).get(0);
			}
		}

		if (defaultCountry == null) {
			throw new AppException("Default country not defined, please define default country.");
		}

		return defaultCountry;
	}

	public static String getCashPosition(BigDecimal reOrderLimit, BigDecimal cashPositon, BigDecimal cashLimit) {
		if (reOrderLimit == null) {
			reOrderLimit = new BigDecimal(0);
		}

		if (cashPositon == null) {
			cashPositon = new BigDecimal(0);
		}

		if (cashLimit == null) {
			cashLimit = new BigDecimal(0);
		}

		if (cashPositon.compareTo(reOrderLimit) < 0) {
			return CashManagementConstants.Cash_Position_Low_Desc;
		} else if (cashPositon.compareTo(cashLimit) > 0) {
			return CashManagementConstants.Cash_Position_Excess_Desc;
		} else {
			return CashManagementConstants.Cash_Position_Sufficient_Desc;
		}
	}

	private static boolean getAlwIntegralPartZero() {
		return PennantConstants.YES.equals(ImplementationConstants.ALLOW_AMOIUNT_INTEGRAL_PART);
	}

	public static String getSavingStatus(String roleCode, String nextRoleCode, String reference, String moduleCode,
			String recordStatus, boolean isCancel) {
		String roleCodeDesc = "";
		if (StringUtils.isBlank(nextRoleCode) || roleCode.equals(nextRoleCode)
				|| StringUtils.trimToEmpty(recordStatus).equalsIgnoreCase(PennantConstants.RCD_STATUS_SAVED)) {
			if (isCancel) {
				return moduleCode + " with Reference : " + reference + " cancellation completed successfully.";
			} else {
				return moduleCode + " with Reference : " + reference + " " + recordStatus + " successfully.";
			}
		} else {
			JdbcSearchObject<SecurityRole> searchObject = new JdbcSearchObject<SecurityRole>(SecurityRole.class);
			if (nextRoleCode.contains(",")) {
				String roleCodes[] = nextRoleCode.split(",");
				searchObject.addFilterIn("RoleCd", Arrays.asList(roleCodes));
			} else {
				searchObject.addFilterEqual("RoleCd", nextRoleCode);
			}
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			List<SecurityRole> rolesList = pagedListService.getBySearchObject(searchObject);
			if (rolesList != null && !rolesList.isEmpty()) {
				for (SecurityRole securityRole : rolesList) {
					if (StringUtils.isEmpty(roleCodeDesc)) {
						roleCodeDesc = securityRole.getRoleDesc();
					} else {
						roleCodeDesc = roleCodeDesc.concat(" And " + securityRole.getRoleDesc());
					}
				}
			}
			return moduleCode + " with Reference: " + reference + " moved to "
					+ (StringUtils.isBlank(roleCodeDesc) ? "" : roleCodeDesc) + " successfully.";
		}
	}

	public static RestTemplate getTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		SimpleClientHttpRequestFactory httpRequestFactory = new SimpleClientHttpRequestFactory();
		String proxyUrl = App.getProperty("external.interface.proxy.host");
		String proxyPort = App.getProperty("external.interface.proxy.port");
		String proxyRequired = App.getProperty("portal.proxy.required");
		if (StringUtils.equals(proxyRequired, "true") && StringUtils.isNotEmpty(proxyUrl)
				&& StringUtils.isNotEmpty(proxyPort)) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl, Integer.parseInt(proxyPort)));
			httpRequestFactory.setProxy(proxy);
		}
		restTemplate.setRequestFactory(httpRequestFactory);

		return restTemplate;
	}

	public static Map<String, Object> getExtendedFieldsDataMap(FinanceDetail financeDetail) {
		Map<String, Object> dataMap = new HashMap<>();

		ExtendedFieldRender extendedFieldRender = financeDetail.getExtendedFieldRender();
		if (extendedFieldRender == null || extendedFieldRender.getMapValues() == null) {
			return dataMap;
		}

		Map<String, Object> finextendedfields = extendedFieldRender.getMapValues();

		for (Map.Entry<String, Object> entry : finextendedfields.entrySet()) {
			ExtendedFieldHeader extendedFieldHeader = financeDetail.getExtendedFieldHeader();
			dataMap.put(extendedFieldHeader.getModuleName() + "_" + extendedFieldHeader.getSubModuleName() + "_"
					+ entry.getKey().toUpperCase(), entry.getValue());
		}

		return dataMap;
	}

	public static Map<String, Object> getExtendedFieldsDataMap(CustomerDetails customerDetails) {
		Map<String, Object> dataMap = new HashMap<>();

		ExtendedFieldRender extendedFieldRender = customerDetails.getExtendedFieldRender();
		if (extendedFieldRender == null || extendedFieldRender.getMapValues() == null) {
			return dataMap;
		}

		Map<String, Object> finextendedfields = extendedFieldRender.getMapValues();

		for (Map.Entry<String, Object> entry : finextendedfields.entrySet()) {
			ExtendedFieldHeader extendedFieldHeader = customerDetails.getExtendedFieldHeader();
			dataMap.put(extendedFieldHeader.getModuleName() + "_" + extendedFieldHeader.getSubModuleName() + "_"
					+ entry.getKey().toUpperCase(), entry.getValue());
		}

		return dataMap;
	}

	public static List<String> getActiveFieldCodeList(String fieldCode) {
		List<String> fieldCodeList = new ArrayList<String>();

		Search search = new Search(LovFieldDetail.class);
		search.addSort("FieldCodeValue", false);
		search.addFilter(new Filter("FieldCode", fieldCode, Filter.OP_EQUAL));
		search.addFilter(new Filter("IsActive", true, Filter.OP_EQUAL));
		search.addField("FieldCodeValue");
		search.addField("ValueDesc");

		SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
		List<LovFieldDetail> appList = searchProcessor.getResults(search);
		for (int i = 0; i < appList.size(); i++) {
			String codeValue = String.valueOf(appList.get(i).getFieldCodeValue());
			fieldCodeList.add(codeValue);
		}
		return fieldCodeList;
	}

	public static String getstatus(String roleCode, String nextRoleCode, String certificateNumber, String moduleCode,
			String recordStatus) {

		recordStatus = StringUtils.trimToEmpty(recordStatus);

		if (StringUtils.isBlank(nextRoleCode) || roleCode.equals(nextRoleCode)
				|| recordStatus.equalsIgnoreCase(PennantConstants.RCD_STATUS_SAVED)) {
			return getReturnStatus(certificateNumber, moduleCode, recordStatus);
		}

		JdbcSearchObject<SecurityRole> searchObject = new JdbcSearchObject<>(SecurityRole.class);

		if (nextRoleCode.contains(",")) {
			String roleCodes[] = nextRoleCode.split(",");
			searchObject.addFilterIn("RoleCd", Arrays.asList(roleCodes));
		} else {
			searchObject.addFilterEqual("RoleCd", nextRoleCode);
		}

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		List<SecurityRole> rolesList = pagedListService.getBySearchObject(searchObject);

		StringBuilder roleCodeDesc = new StringBuilder();
		for (SecurityRole securityRole : rolesList) {
			if (!roleCodeDesc.toString().isEmpty()) {
				roleCodeDesc.append(" and ");
			}

			roleCodeDesc.append(securityRole.getRoleDesc());
		}

		return getReturnStatus(certificateNumber, moduleCode, roleCodeDesc.toString());
	}

	private static String getReturnStatus(String reference, String code, String desc) {
		StringBuilder status = new StringBuilder();

		status.append(code).append(" ").append(reference).append(" ");
		status.append(desc).append(" successfully.");

		return status.toString();
	}
}
