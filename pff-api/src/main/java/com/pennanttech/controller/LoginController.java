package com.pennanttech.controller;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pennant.app.util.APIHeader;
import com.pennant.backend.dao.sessionvalidation.SessionValidationDAO;
import com.pennant.backend.model.SecLoginlog;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.sessionvalidation.SessionValidation;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.framework.security.core.service.UserService;
import com.pennanttech.ws.model.login.LoginRequest;
import com.pennanttech.ws.model.login.LoginResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class LoginController {

	private final static Logger logger = Logger.getLogger(LoginController.class);

	private SessionValidationDAO sessionValidationDAO;
	private transient UserService userService;

	public LoginResponse validateLogin(LoginRequest loginRequest) {
		logger.debug("ENTERING");
		LoginResponse response = new LoginResponse();

		try {
			SecurityUser user = userService.getUserByLogin(StringUtils.upperCase(loginRequest.getUsrName()));
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
					.get(APIHeader.API_HEADER_KEY);
			if (user != null && isPasswordValid(user.getUsrPwd(), loginRequest.getUserPwd())) {
				saveLoginLog(loginRequest.getUsrName(), null);
				saveSession(user.getUsrID(), user.getUsrLogin(), loginRequest.getRegistrationId());
				response.setUserRights(getRightsByUser(user));
				response.setUserId(String.valueOf(user.getUsrID()));
				response.setRoleCode(user.getNextRoleCode());
				response.setUsertoken(reqHeaderDetails.getSecurityInfo());
				response.setLastLoginOn(user.getLastLoginOn());
				response.setLastFailLoginOn(user.getLastFailLoginOn());
				response.setStatus("Success");
				response.setUserBranch(user.getLovDescUsrBranchCodeName());
			} else {
				saveLoginLog(loginRequest.getUsrName(), "Bad Credentials");
				response.setStatus("Failed");
			}

			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		} catch (Exception e) {
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
		}

		logger.debug("LEAVING");
		return response;
	}

	private boolean isPasswordValid(String userPassword, String loginPassword) throws ServiceException {
		PasswordEncoder encoderImpl = new BCryptPasswordEncoder();
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		if (passwordEncoder instanceof BCryptPasswordEncoder) {
			encoderImpl = (BCryptPasswordEncoder) passwordEncoder;
		} else {
			encoderImpl = (NoOpPasswordEncoder) passwordEncoder;
		}

		if (StringUtils.equals(userPassword, loginPassword)) {
			return true;
		}

		if (encoderImpl.matches(loginPassword, userPassword)) {
			return true;
		}
		return false;
	}

	private void saveLoginLog(String user, String loginError) {
		SecLoginlog secLoginlog = new SecLoginlog();

		secLoginlog.setLoginLogID(Long.MIN_VALUE);
		secLoginlog.setLoginUsrLogin(user);
		secLoginlog.setLoginTime(new Timestamp(System.currentTimeMillis()));
		secLoginlog.setLoginIP("");
		secLoginlog.setLoginBrowserType("APP");
		secLoginlog.setLoginSessionID("");
		secLoginlog.setLoginError(loginError);

		if (loginError == null) {
			secLoginlog.setLoginStsID(1);
		} else {
			secLoginlog.setLoginStsID(0);
		}

		userService.logLoginAttempt(secLoginlog);
	}

	private void saveSession(long userId, String userLogin, String registrationId) {
		SessionValidation sessionValidation = new SessionValidation();
		sessionValidation.setEntityCode(userLogin);
		sessionValidation.setAgentId(userId);
		sessionValidation.setRegistrationId(registrationId);

		if (sessionValidationDAO.getSessionById(sessionValidation.getAgentId()) == null) {
			sessionValidationDAO.save(sessionValidation);
		} else {
			sessionValidationDAO.update(sessionValidation);
		}
	}

	private ArrayList<String> getRightsByUser(SecurityUser user) {
		ArrayList<String> userRights = new ArrayList<String>();
		// Collection<SecurityRight> rights =
		// userService.getAppRightsByUser(user);
		/*
		 * for (SecurityRight rgt:rights){ userRights.add(rgt.getRightName()); }
		 */
		return userRights;
	}

	public void setSessionValidationDAO(SessionValidationDAO sessionValidationDAO) {
		this.sessionValidationDAO = sessionValidationDAO;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}