package com.pennant.backend.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zkplus.spring.SpringUtil;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.service.PagedListService;
 

public class PennantApplicationUtil {

	
	public static BigDecimal unFormateAmount(BigDecimal amount, int dec) {

		if (amount == null) {
			return BigDecimal.ZERO;
		}
		BigInteger bigInteger = amount.multiply(new BigDecimal(Math.pow(10, dec))).toBigInteger();
		return new BigDecimal(bigInteger);
	}
	
	public static BigDecimal formateAmount(BigDecimal amount, int dec) {
		BigDecimal bigDecimal = BigDecimal.ZERO;

		if (amount != null) {
			bigDecimal = amount.divide(new BigDecimal(Math.pow(10, dec)));
		}
		return bigDecimal;
	}

	public static String amountFormate(BigDecimal amount, int dec) {
		BigDecimal bigDecimal = BigDecimal.ZERO;
		if (amount != null) {
			bigDecimal = amount.divide(new BigDecimal(Math.pow(10, dec)));
		}

		return formatAmount(bigDecimal, dec, false);
	}

	public static String formatAmount(BigDecimal value, int decPos, boolean debitCreditSymbol) {

		if (value != null && value.compareTo(new BigDecimal("0")) != 0) {
			DecimalFormat df = new DecimalFormat();
			StringBuffer sb = new StringBuffer("###,###,###,###");
			boolean negSign = false;

			if (decPos > 0) {
				sb.append('.');
				for (int i = 0; i < decPos; i++) {
					sb.append('0');
				}

				if (value.compareTo(new BigDecimal("0")) == -1) {
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
			return df.format(value).toString();
		} else {
			String string = "0.";
			for (int i = 0; i < decPos; i++) {
				string += "0";
			}
			return string;
		}
	}
	
	public static String getAmountFormate(int dec) {
		String formateString = PennantConstants.defaultAmountFormate;

		switch (dec) {
		case 0:
			formateString = PennantConstants.amountFormate0;
			break;
		case 1:
			formateString = PennantConstants.amountFormate1;
			break;
		case 2:
			formateString = PennantConstants.amountFormate2;
			break;
		case 3:
			formateString = PennantConstants.amountFormate3;
			break;
		case 4:
			formateString = PennantConstants.amountFormate4;
			break;
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
		StringBuffer sb = new StringBuffer("###,###,###,###");

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
				String returnResult = df.format(value).toString();
				returnResult = returnResult.replaceAll("[0]*$", "");
				if(returnResult.endsWith(".")){
					returnResult = returnResult + "00";
				}else if(returnResult.contains(".") && returnResult.substring(returnResult.indexOf('.')+1).length() == 1){
					returnResult = returnResult + "0";
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
							string += "0";
						}
					}
				}
				
				string += actValue;
				return string;
			}
		} else {
			String string = "0.";
			for (int i = 0; i < 2; i++) {
				string += "0";
			}
			return string;

		}
	}

	public static String formateLong(long longValue) {
		StringBuffer sb = new StringBuffer("###,###,###,###");
		java.text.DecimalFormat df = new java.text.DecimalFormat();
		df.applyPattern(sb.toString());
		return df.format(longValue).toString();
	}

	public static String formateInt(int intValue) {

		StringBuffer sb = new StringBuffer("###,###,###,###");
		java.text.DecimalFormat df = new java.text.DecimalFormat();
		df.applyPattern(sb.toString());
		return df.format(intValue).toString();
	}

	public static String formateBoolean(int intValue) {

		if(intValue == 1){
			return String.valueOf(true);
		} else {
			return String.valueOf(false);
		}
	}
	
	public static String getCcyFormate(int minCCY) {
		String formateString = PennantConstants.defaultAmountFormate;
		formateString = getAmountFormate(getCcyDec(minCCY));
		return formateString;
	}

	public static int getCcyDec(int minCCY) {
		int decPos = PennantConstants.defaultCCYDecPos;
		switch (minCCY) {
		case 1:
			decPos = 0;
			break;
		case 10:
			decPos = 1;
			break;
		case 100:
			decPos = 2;
			break;
		case 1000:
			decPos = 3;
			break;
		case 10000:
			decPos = 4;
			break;
		}
		return decPos;
	}

	public static String formateDate(Date date, String dateFormate) {
		String formatedDate = null;
		if (StringUtils.trimToEmpty(dateFormate).equals("")) {
			dateFormate = PennantConstants.dateFormat;
		}

		SimpleDateFormat formatter = new SimpleDateFormat(dateFormate);

		if (date != null) {
			formatedDate = formatter.format(date);
		}

		return formatedDate;

	}

	public static Date getDate(Timestamp timestamp) {
		Date date = null;
		if (timestamp != null) {
			date = new Date(timestamp.getTime());
		}
		return date;
	}

	public static Timestamp getTimestamp(Date date) {
		Timestamp timestamp = null;

		if (date != null) {
			timestamp = new Timestamp(date.getTime());
		}
		return timestamp;
	}

	public static Time getTime(Date date) {
		Time time = null;

		if (date != null) {
			time = new Time(date.getTime());
		}
		return time;
	}

	public static String formatAccountNumber(String accountNumber){
		if (!StringUtils.trimToEmpty(accountNumber).equals("") && accountNumber.length() == 13) {
			StringBuilder builder = new StringBuilder();
			builder.append(accountNumber.substring(0, 4));
			builder.append("-");
			builder.append(accountNumber.substring(4, 10));
			builder.append("-");
			builder.append(accountNumber.substring(10, 13));
			return builder.toString();
		}
		return accountNumber;
	}
	
	public static String unFormatAccountNumber(String accountNumber){
		if (!StringUtils.trimToEmpty(accountNumber).equals("")) {
			return StringUtils.trim(accountNumber.replaceAll("-", ""));
		}
		return accountNumber;
	}
	
	public static String getSavingStatus(String roleCode,String nextRoleCode, String reference, String moduleCode, String recordStatus){
		String roleCodeDesc = "";
		if(StringUtils.trimToEmpty(nextRoleCode).equals("") || roleCode.equals(nextRoleCode) || StringUtils.trimToEmpty(recordStatus).equalsIgnoreCase(PennantConstants.RCD_STATUS_SAVED)){
			return moduleCode + " with Reference: " + reference +" "+ recordStatus + " Successfully.";
		}else{
			JdbcSearchObject<SecurityRole> searchObject = new JdbcSearchObject<SecurityRole>(SecurityRole.class);
			if (nextRoleCode.contains(",")) {
	            String roleCodes[]=nextRoleCode.split(",");
	        	searchObject.addFilterIn("RoleCd",roleCodes);
            }else{
            	searchObject.addFilterEqual("RoleCd", nextRoleCode);
            }
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			List<SecurityRole> rolesList = pagedListService.getBySearchObject(searchObject);
			if (rolesList!=null && !rolesList.isEmpty()) {
				for (SecurityRole securityRole : rolesList) {
					if ("".equals(roleCodeDesc)) {
						roleCodeDesc = securityRole.getRoleDesc();
                    }else{
                    	roleCodeDesc=roleCodeDesc+" And "+securityRole.getRoleDesc();
                    }
                }
            }
			return moduleCode + " with Reference: " + reference + " Moved to " +  (StringUtils.trimToEmpty(roleCodeDesc).equals("") ? "" : roleCodeDesc) + " Successfully.";
		}
 	}
	
	public static List<SecurityRole> getRoleCodeDesc(String roleCode){
			JdbcSearchObject<SecurityRole> searchObject = new JdbcSearchObject<SecurityRole>(SecurityRole.class);
			if (roleCode.contains(",")) {
	            String roleCodes[]=roleCode.split(",");
	        	searchObject.addFilterIn("RoleCd",roleCodes);
            }else{
            	searchObject.addFilterEqual("RoleCd", roleCode);
            }
//			searchObject.addFilterEqual("RoleCd", roleCode);
			
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			List<SecurityRole> rolesList = pagedListService.getBySearchObject(searchObject);
			return rolesList;
 	}

	public static String getUserDesc(long userID){
		JdbcSearchObject<SecurityUser> searchObject = new JdbcSearchObject<SecurityUser>(SecurityUser.class);
		searchObject.addFilterEqual("UsrID", userID);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		List<SecurityUser> usersList = pagedListService.getBySearchObject(searchObject);
		SecurityUser securityUser = usersList.get(0); 
		if(StringUtils.trimToEmpty(securityUser.getUsrMName()).equals("")){
			return securityUser.getUsrFName() + " " + securityUser.getUsrLName();
		}else{
			return securityUser.getUsrFName()+ " " + securityUser.getUsrMName() + " " + securityUser.getUsrLName();
		}
	}
	public static String getWorkFlowType(long workflowID){
		JdbcSearchObject<WorkFlowDetails> searchObject = new JdbcSearchObject<WorkFlowDetails>(WorkFlowDetails.class);
		searchObject.addFilterEqual("WorkFlowID", workflowID);
		searchObject.addField("WorkFlowType");
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		List<WorkFlowDetails> usersList = pagedListService.getBySearchObject(searchObject);
		return usersList.get(0).getWorkFlowType(); 
 	}
 }
