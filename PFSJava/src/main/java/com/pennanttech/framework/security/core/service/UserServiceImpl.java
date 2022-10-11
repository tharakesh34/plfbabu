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
 *
 * FileName : UserServiceImpl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 04-08-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennanttech.framework.security.core.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.SecLoginlogDAO;
import com.pennant.backend.dao.UserDAO;
import com.pennant.backend.dao.administration.SecurityRightDAO;
import com.pennant.backend.model.SecLoginlog;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennanttech.pennapps.core.security.user.AuthenticationError;
import com.pennanttech.pennapps.core.security.user.UserAuthenticationException;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

public class UserServiceImpl implements UserService {

	private UserDAO userDAO;
	private SecurityRightDAO securityRightDAO;
	private SecLoginlogDAO secLoginlogDAO;

	public UserServiceImpl() {
		super();
	}

	public SecurityUser getNewUser() {
		return userDAO.getNewSecUser();
	}

	public SecurityUser getUserByLogin(String userName) {
		userName = StringUtils.upperCase(userName);
		SecurityUser user = userDAO.getUserByLogin(userName);

		if (user == null) {
			throw new UserAuthenticationException(AuthenticationError.USER_NOT_FOUND);
		} else if (!user.isUsrEnabled()) {
			throw new UserAuthenticationException(AuthenticationError.ACCOUN_DISABLED);
		} else if (user.isUsrAcLocked()) {
			throw new UserAuthenticationException(AuthenticationError.ACCOUN_LOCKED);
		} else if (user.isDeleted()) {
			throw new UserAuthenticationException(AuthenticationError.ACCOUN_LOCKED);
		}

		Date date = DateUtil.getSysDate();
		String strTime = DateUtil.format(date, DateFormat.LONG_TIME);
		date = DateUtil.parse(strTime, DateFormat.LONG_TIME);

		Date expiredDate = user.getUsrAcExpDt();

		if ((expiredDate != null && expiredDate.before(DateUtil.getSysDate()))) {
			throw new UserAuthenticationException(AuthenticationError.ACCOUN_EXPIRED);
		}

		Date signonFrom = user.getUsrCanSignonFrom();
		if (signonFrom != null && date.compareTo(signonFrom) < 0) {
			String strSignOnFrom = DateUtil.format(signonFrom, DateFormat.LONG_TIME);
			throw new UserAuthenticationException(AuthenticationError.LOGIN_BEFORE, strSignOnFrom);
		}

		Date signOnTo = user.getUsrCanSignonTo();
		if (signOnTo != null && date.compareTo(signOnTo) > 0) {
			String strSignOnTo = DateUtil.format(signOnTo, DateFormat.LONG_TIME);
			throw new UserAuthenticationException(AuthenticationError.LOGIN_AFTER, strSignOnTo);
		}
		return user;

	}

	@Override
	public SecurityUser getSecurityUserByLogin(String username) {
		return userDAO.getSecurityUserByLogin(username.toUpperCase());
	}

	public SecurityUser getUserByLogin(long userId) {
		return userDAO.getUserByLogin(userId);
	}

	public List<SecurityUser> getUserLikeLastname(String value) {
		return userDAO.getUserLikeLastname(value);
	}

	public List<SecurityUser> getUserLikeLoginname(String value) {
		return userDAO.getUserLikeLogin(value);
	}

	public List<SecurityUser> getUserLikeEmail(String value) {
		return userDAO.getUserLikeEmail(value);
	}

	public List<SecurityUser> getUserListByLogin(String userName) {
		return userDAO.getUserListByLogin(userName);
	}

	public int getCountAllSecUser() {
		return userDAO.getCountAllSecUser();
	}

	public Collection<SecurityRight> getMenuRightsByUser(SecurityUser user) {
		return securityRightDAO.getMenuRightsByUser(user);

	}

	public Collection<SecurityRight> getPageRights(SecurityRight secRight) {
		return securityRightDAO.getPageRights(secRight);
	}

	public List<SecurityRole> getUserRolesByUserID(long userID) {
		return userDAO.getUserRolesByUserID(userID);
	}

	@Override
	public long logLoginAttempt(SecLoginlog logingLog) {
		return this.secLoginlogDAO.saveLog(logingLog);
	}

	@Override
	public void updateLoginStatus(long userId) {
		userDAO.updateLoginStatus(userId);
	}

	@Override
	public void updateInvalidTries(String userLogin, String disableReason) {
		userDAO.updateInvalidTries(userLogin, disableReason);
	}

	@Override
	public void logLogOut(long loginId) {
		secLoginlogDAO.logLogOut(loginId);
	}

	@Autowired
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Autowired
	public void setSecurityRightDAO(SecurityRightDAO securityRightDAO) {
		this.securityRightDAO = securityRightDAO;
	}

	@Autowired
	public void setSecLoginlogDAO(SecLoginlogDAO secLoginlogDAO) {
		this.secLoginlogDAO = secLoginlogDAO;
	}

}
