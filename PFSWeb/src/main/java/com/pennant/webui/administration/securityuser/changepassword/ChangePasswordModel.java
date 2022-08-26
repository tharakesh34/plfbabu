/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : Change***Model.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 13-07-2011 * * Modified Date
 * : 30-07-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.administration.securityuser.changepassword;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zkoss.spring.SpringUtil;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.administration.SecurityUserPasswordsDAO;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennanttech.pennapps.core.resource.Literal;

public class ChangePasswordModel {
	private static final Logger logger = LogManager.getLogger(ChangePasswordModel.class);
	private static SecurityUserPasswordsDAO securityUserPasswordsDAO;
	private static final String DEFAULT_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=[^\\s]+$)(?=.*[!@#$%^&*_-])";

	/**
	 * This method checking whether EncriptedPassword and raw password are same or not by calling PasswordEncoderImpl's
	 * isPasswordValid() method
	 * 
	 * @param oldPassword //encrypted Old password
	 * @param password    //newPassword or oldPassword depend on method call
	 * @return boolean
	 */

	public boolean isPaswordsSame(String encriptedPassword, String password) {
		logger.debug(Literal.ENTERING);

		PasswordEncoder pwdEncoder = (PasswordEncoder) SpringUtil.getBean("passwordEncoder");
		if (!pwdEncoder.matches(password, encriptedPassword)) {
			return false;
		}

		logger.debug(Literal.LEAVING);
		return true;

	}

	/**
	 * This method validates password with conditions 1)Whether it is following Defined pattern or not 2)It checks
	 * whether password contains any three sequence letters in userName
	 * 
	 * @param username
	 * @param password
	 * @return boolean
	 */
	public boolean checkPasswordCriteria(String username, String password) {
		logger.debug(Literal.ENTERING);
		boolean inValid = false;
		String pattern = "";
		int pwdMinLenght = SysParamUtil.getValueAsInt("USR_PWD_MIN_LEN");
		int pwdMaxLenght = SysParamUtil.getValueAsInt("USR_PWD_MAX_LEN");
		pattern = DEFAULT_PATTERN + ".{" + pwdMinLenght + "," + pwdMaxLenght + "})";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(password);
		if (!matcher.matches()) {
			return true;
		}
		if (matcher.matches()) {
			for (String part : getSubstrings(username, 3)) {
				if (StringUtils.containsIgnoreCase(password, part)) {
					return true;
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return inValid;
	}

	/**
	 * This method partition the given user name with length of given partitionSize and returns partition Strings list
	 * 
	 * @param string
	 * @param partitionSize
	 * @return List< String > partsList
	 */
	private List<String> getSubstrings(String string, int partitionSize) {
		logger.debug(Literal.ENTERING);
		List<String> partsList = new ArrayList<String>();
		int len = string.length();
		for (int i = 0; i < len; i += 1) {
			String part = string.substring(i, Math.min(len, i + partitionSize));
			if (part.length() == 3) {
				partsList.add(part);
			}
		}
		logger.debug(Literal.LEAVING);
		return partsList;
	}

	/**
	 * This method do the following. <br>
	 * 1)Selects list of records from SecUserPasswords table as <code>List< SecurityUser > </code> by calling
	 * SecurityUserDAO's getUserRecentPasswords(SecurityUser aSecurityUser) <br>
	 * 2)Compare the newPassword with each SecurityUser Object's UsrPwd property by
	 * calling<code> IsPaswordsSame()</code> <br>
	 * 3)if Password matches <code>@returns true
	 *       <br>  else<br> @return false</Code>
	 * 
	 * @param aSecurityUser (SecurityUser )
	 * @param newPassword   (String)
	 * @return boolean
	 */
	public boolean checkWithPreviousPasswords(SecurityUser aSecurityUser, String newPassword) {
		logger.debug(Literal.ENTERING);

		// Fetch the history of changes for the user.
		List<SecurityUser> secUserList = getSecurityUserPasswordsDAO().getUserPreviousPasswords(aSecurityUser);

		int maxPasswordsCheck = SysParamUtil.getValueAsInt("USR_MAX_PRE_PWDS_CHECK");
		SecurityUser securityUser;

		if (secUserList.size() > 0) {
			// If the changes are less than max check, limit the comparison to the available.
			if (secUserList.size() < maxPasswordsCheck) {
				maxPasswordsCheck = secUserList.size();
			}
			for (int i = 0; i < maxPasswordsCheck; i++) {

				securityUser = secUserList.get(i);
				if (isPaswordsSame(securityUser.getUsrPwd(), newPassword)) {
					return true;
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return false;

	}

	public void setSecurityUserPasswordsDAO(SecurityUserPasswordsDAO securityUserPasswordsDAO) {
		ChangePasswordModel.securityUserPasswordsDAO = securityUserPasswordsDAO;
	}

	public static SecurityUserPasswordsDAO getSecurityUserPasswordsDAO() {
		return securityUserPasswordsDAO;
	}
}
