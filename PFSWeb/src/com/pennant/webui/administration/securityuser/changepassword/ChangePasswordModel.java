/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  ChangePasswordModel.java                                            * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    : 13-07-2011    														*
 *                                                                  						*
 * Modified Date    : 30-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-07-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.administration.securityuser.changepassword;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.administration.SecurityUserPasswordsDAO;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.util.PennantConstants;
import com.pennant.sec.util.PasswordEncoderImpl;

public class ChangePasswordModel {
	private final static Logger logger = Logger	.getLogger(ChangePasswordModel.class);
	private PasswordEncoderImpl pwdencoder = new PasswordEncoderImpl();
	private static SecurityUserPasswordsDAO securityUserPasswordsDAO;

	/**
	 *This method checking whether EncriptedPassword and raw password are same or not by calling PasswordEncoderImpl's
	 *isPasswordValid() method 
	 * @param oldPassword       //encrypted Old password  
	 * @param token             //user token from database           
	 * @param password         //newPassword or oldPassword  depend on method call          
	 * @return boolean
	 */

	public boolean isPaswordsSame(String encriptedPassword, String token, String password) {
		logger.debug("Entering ");
		if (!pwdencoder.isPasswordValid(encriptedPassword, password, token)) {
			return false; //if not same return false
		}
		logger.debug("Leaving ");
		return true;

	}

	/**
	 * This method validates password with conditions
	 *     1)Whether it is following Defined pattern or not
	 *     2)It checks whether password contains any three sequence letters in userName 
	 * 
	 * @param username
	 * @param password
	 * @return boolean
	 */
	public boolean checkPasswordCriteria(String username, String password) {
		logger.debug("Entering ");
		boolean inValid = false;
		String pattern="";
		int pwdMinLenght=Integer.parseInt(SystemParameterDetails.getSystemParameterValue("USR_PWD_MIN_LEN").toString());
		int pwdMaxLenght=Integer.parseInt(SystemParameterDetails.getSystemParameterValue("USR_PWD_MAX_LEN").toString());
		pattern = PennantConstants.PASSWORD_PATTERN+".{"+String.valueOf(pwdMinLenght)+","+String.valueOf(pwdMaxLenght)+"})";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(password);
		if (!matcher.matches()){
			return true;
		} 
		if (matcher.matches()) {
			for (String part : getSubstrings(username, 3)) {
				if (StringUtils.containsIgnoreCase(password, part)) {
					return true;
				}
			}
		}      	
		logger.debug("Leaving ");
		return inValid;
	}

	/**
	 * This method partition the given user name with length of given partitionSize and returns partition Strings list
	 * @param string
	 * @param partitionSize
	 * @return List< String > partsList
	 */
	private List<String> getSubstrings(String string, int partitionSize) {
		logger.debug("Entering ");
		List<String> partsList = new ArrayList<String>();
		int len = string.length();
		for (int i = 0; i < len; i += 1) {
			String part = string.substring(i, Math.min(len, i + partitionSize));
			if (part.length() == 3) {
				partsList.add(part);
			}
		}
		logger.debug("Leaving ");
		return partsList;
	}
	/**
	 * This method do the following.
	 * <br>1)Selects list of records from SecUserPasswords table as <code>List< SecurityUser > </code>
	 * by calling SecurityUserDAO's getUserRecentPasswords(SecurityUser aSecurityUser)
	 * <br>2)Compare the newPassword with each  SecurityUser Object's UsrPwd property by calling<code> IsPaswordsSame()</code>
	 * <br>3)if Password matches <code>@returns true
	 *       <br>  else<br> @return false</Code>
	 * @param aSecurityUser (SecurityUser )
	 * @param  newPassword  (String)
	 * @return boolean 
	 */
	public boolean checkWithPreviousPasswords(SecurityUser aSecurityUser,String newPassword){
		logger.debug("Entering ");
		/*select all previous  passwords for user*/
		List<SecurityUser> secUserList=getSecurityUserPasswordsDAO().getUserPreviousPasswords(aSecurityUser);
		/*maxPasswordsCheck is number that new passwords should not match with how many previous passwords */
		int maxPasswordsCheck=Integer.parseInt(SystemParameterDetails.getSystemParameterValue("USR_MAX_PRE_PWDS_CHECK").toString());
		SecurityUser securityUser;
		/*check only when previous passwords contains for user */
		if(secUserList.size()>0){
			/*if previous passwords are less then "maxPasswordsCheck", compare with only available passwords */
			if(secUserList.size()<maxPasswordsCheck){
				maxPasswordsCheck=secUserList.size();
			}
			for(int i=0;i<maxPasswordsCheck;i++){

				securityUser=secUserList.get(i);
				if(isPaswordsSame(securityUser.getUsrPwd(), securityUser.getUsrToken(), newPassword)){
					return true;
				}
			}
		}
		logger.debug("Leaving ");
		return false;

	}

	public  void setSecurityUserPasswordsDAO(
			SecurityUserPasswordsDAO securityUserPasswordsDAO) {
		ChangePasswordModel.securityUserPasswordsDAO = securityUserPasswordsDAO;
	}

	public static SecurityUserPasswordsDAO getSecurityUserPasswordsDAO() {
		return securityUserPasswordsDAO;
	}


}
