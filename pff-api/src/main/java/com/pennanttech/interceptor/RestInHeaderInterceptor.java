package com.pennanttech.interceptor;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.smtmasters.PFSParameterService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.ws.exception.ServiceException;
import com.pennant.ws.exception.ServiceExceptionDetails;
import com.pennanttech.extension.api.APIServices;
import com.pennanttech.framework.security.core.User;
import com.pennanttech.framework.security.core.service.UserService;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.auth.model.ServerAuthentication;
import com.pennanttech.ws.auth.model.UserAuthentication;
import com.pennanttech.ws.auth.service.ServerAuthService;
import com.pennanttech.ws.auth.service.UserAuthService;
import com.pennanttech.ws.log.model.APILogDetail;

/**
 * This is REST services interceptor to read all the HTTP headers and set the details in the APIHeader bean object. The
 * APIHeader bean object can be used in the service class and used to return the same header parameters in the response.
 * 
 * @author pennant
 *
 */
public class RestInHeaderInterceptor extends AbstractPhaseInterceptor<Message> {

	Logger logger = LogManager.getLogger(RestInHeaderInterceptor.class);

	// Static variables
	public static final String CHANNEL_USER = "user";
	public static final String CHANNEL_SERVER = "device";
	public static long TOKEN_EXPIRY = 300000;
	SimpleDateFormat dateFormat = new SimpleDateFormat(PennantConstants.APIDateFormatter);
	// private variables
	@Autowired
	private PasswordEncoder passwordEncoder;
	private UserAuthService userAuthService;
	private ServerAuthService serverAuthService;
	private UserService userService;
	@Autowired
	private PFSParameterService systemParameterService;

	/*
	 * Constructor
	 */
	public RestInHeaderInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	/***** Public Methods *****/

	@Override
	public void handleMessage(Message message) throws Fault {
		logger.debug(Literal.ENTERING);
		Map<String, String> additionalInfo = new HashMap<>();
		String authCredentials = null;

		@SuppressWarnings("unchecked") // read the HTTP header details
		Map<String, List<String>> headerMAP = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);

		APILogDetail apiLogDetail = (APILogDetail) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_LOG_KEY);
		APIHeader header = new APIHeader();

		// set the request header details in the APIHEader object.
		try {
			if (apiLogDetail == null) {
				apiLogDetail = new APILogDetail();
			}
			// Validate Header Data
			validateHeaderDetails(headerMAP, header, message);

			// Set the IP address from the request object.
			apiLogDetail.setClientIP(header.getIpAddress());
			for (String key : headerMAP.keySet()) {
				switch (key.toUpperCase()) {
				case APIHeader.API_AUTHORIZATION:
					authCredentials = headerMAP.get(key).toString();
					apiLogDetail.setAuthKey(authCredentials);
					break;
				// if service name is there in HTTP header, set it in APIHeader.
				case APIHeader.API_SERVICENAME:
					String serviceName = headerMAP.get(key).toString().replace("[", "").replace("]", "");

					if (!APIServices.isAllowed(serviceName)) {
						getErrorDetails(APIConstants.RES_SERVICE_NOT_FOUND, new String[] { serviceName });
					}

					header.setServiceName(serviceName);
					break;
				// if service version is there in HTTP header, set it in
				// APIHeader.
				case APIHeader.API_SERVICEVERSION:
					header.setServiceVersion(headerMAP.get(key).toString().replace("[", "").replace("]", ""));
					apiLogDetail.setServiceVersion(Integer.parseInt(header.getServiceVersion()));
					break;
				// if entityId is there in HTTP header, set it in APIHeader.
				case APIHeader.API_ENTITYID:
					header.setEntityId(headerMAP.get(key).toString().replace("[", "").replace("]", ""));
					apiLogDetail.setEntityId(header.getEntityId());
					break;
				// if language is there in HTTP header, set it in APIHeader.
				case APIHeader.API_LANGUAGE:
					header.setLanguage(headerMAP.get(key).toString().replace("[", "").replace("]", ""));
					apiLogDetail.setLanguage(header.getLanguage());
					break;
				// if messageId is there in HTTP header, set it in APIHeader.
				case APIHeader.API_MESSAGEID:
					header.setMessageId(headerMAP.get(key.toLowerCase()).toString().replace("[", "").replace("]", ""));
					apiLogDetail.setMessageId(header.getMessageId());
					break;
				// if service version is there in HTTP header, set it in
				// APIHeader.
				case APIHeader.API_REQ_TIME:
					try {
						String sample = headerMAP.get(key).toString().replace("[", "").replace("]", "");
						Date reqTime = dateFormat.parse(sample);
						header.setRequestTime(reqTime);
						apiLogDetail.setHeaderReqTime(new Timestamp(header.getRequestTime().getTime()));
					} catch (Exception e) {
						logger.error(Literal.EXCEPTION, e);
					}
					break;
				// if Channel is there in HTTP header, set it in APIHeader.
				case APIHeader.API_CHANNEL:
					header.setChannel(headerMAP.get(key).toString().replace("[", "").replace("]", ""));
					apiLogDetail.setChannel(header.getChannel());
					break;
				// all other header details are added in additional info map.
				default:
					additionalInfo.put(key, headerMAP.get(key).toString().replace("[", "").replace("]", ""));
					break;
				}
			}

			header.setSeqId(apiLogDetail.getSeqId());
			header.setAdditionalInfo(additionalInfo);
			message.getExchange().put(APIHeader.API_HEADER_KEY, header);

			if (StringUtils.isBlank(apiLogDetail.getAuthKey())) {
				apiLogDetail.setAuthKey(header.getSecurityInfo());
			}
			logger.debug(Literal.ENTERING);

		} catch (UnsupportedEncodingException e1) { // Invalid request URL
			logger.error(Literal.EXCEPTION, e1);
			getErrorDetails("9999", null);

		} catch (Fault e) { // any validation errors from application
			if (StringUtils.isBlank(apiLogDetail.getAuthKey())) {
				apiLogDetail.setAuthKey(header.getSecurityInfo());
			}
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	/****** Private Methods ********/

	/**
	 * Method for validate the header fields in the request.
	 * 
	 * @param headerMap
	 * @param header
	 * @param message
	 * @throws UnsupportedEncodingException
	 */
	private void validateHeaderDetails(Map<String, List<String>> headerMap, APIHeader header, Message message)
			throws UnsupportedEncodingException {
		logger.debug(Literal.ENTERING);
		APILogDetail apiLogDetail = (APILogDetail) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_LOG_KEY);
		boolean isHeaderContainAuthKey = false;
		boolean isHeaderContainMsgId = false;
		boolean isHeaderContainEntityId = false;
		boolean isHeaderContainSrvcVersion = false;
		boolean isHeaderContainSrvcName = false;
		boolean isHeaderContainReqTime = false;
		boolean isHeaderContainLanguage = false;
		for (String key : headerMap.keySet()) {
			// throw error in case of empty credentials
			if (APIHeader.API_AUTHORIZATION.equalsIgnoreCase(key)) {
				isHeaderContainAuthKey = true;
				String authCredentials = headerMap.get(key).toString();
				validateAuthDetails(authCredentials, header, message);

			} else if (APIHeader.API_MESSAGEID.equalsIgnoreCase(key)) {
				isHeaderContainMsgId = true;
				String messageID = headerMap.get(key).toString().replace("[", "").replace("]", "");
				if (messageID.length() > 200) {
					getErrorDetails("92010", new String[] { APIHeader.API_MESSAGEID });
				}
			} else if (APIHeader.API_ENTITYID.equalsIgnoreCase(key)) {
				isHeaderContainEntityId = true;
			} else if (APIHeader.API_SERVICEVERSION.equalsIgnoreCase(key)) {
				isHeaderContainSrvcVersion = true;
				String srvcVersion = headerMap.get(key).toString().replace("[", "").replace("]", "");
				try {
					Integer.parseInt(srvcVersion);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
					getErrorDetails("92008", new String[] { APIHeader.API_SERVICEVERSION, "NUMBER" });
				}

			} else if (APIHeader.API_SERVICENAME.equalsIgnoreCase(key)) {
				isHeaderContainSrvcName = true;
				String serviceName = headerMap.get(key).toString().replace("[", "").replace("]", "");
				// In Case Of Get or Delete calls ServiceName in Header should be equal to one of the Last two Values in
				// the URL.
				if (apiLogDetail.getServiceName().contains("/")) {
					String[] serviceNameArray = apiLogDetail.getServiceName().split("/");
					if (!StringUtils.equalsIgnoreCase(serviceNameArray[0], serviceName)
							&& !StringUtils.equalsIgnoreCase(serviceNameArray[1], serviceName)) {
						getErrorDetails("92007", new String[] { APIHeader.API_SERVICENAME });
					}
				} else if (!StringUtils.equalsIgnoreCase(apiLogDetail.getServiceName(), serviceName)) {
					getErrorDetails("92007", new String[] { APIHeader.API_SERVICENAME });
				}

			} else if (APIHeader.API_REQ_TIME.equalsIgnoreCase(key)) {
				isHeaderContainReqTime = true;
				String requestTime = headerMap.get(key).toString().replace("[", "").replace("]", "");
				try {
					dateFormat.parse(requestTime);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
					getErrorDetails("92008",
							new String[] { APIHeader.API_REQ_TIME, PennantConstants.APIDateFormatter });

				}
			} else if (APIHeader.API_LANGUAGE.equalsIgnoreCase(key)) {
				isHeaderContainLanguage = true;
			}
		}
		if (!isHeaderContainAuthKey) {
			getErrorDetails("92001", null);
		}
		if (!isHeaderContainMsgId) {
			getErrorDetails("92006", new String[] { APIHeader.API_MESSAGEID });
		}
		if (!isHeaderContainEntityId) {
			getErrorDetails("92006", new String[] { APIHeader.API_ENTITYID });
		}
		if (!isHeaderContainSrvcVersion) {
			getErrorDetails("92006", new String[] { APIHeader.API_SERVICEVERSION });
		}
		if (!isHeaderContainSrvcName) {
			getErrorDetails("92006", new String[] { APIHeader.API_SERVICENAME });
		}
		if (!isHeaderContainReqTime) {
			getErrorDetails("92006", new String[] { APIHeader.API_REQ_TIME });
		}
		if (!isHeaderContainLanguage) {
			getErrorDetails("92006", new String[] { APIHeader.API_LANGUAGE });
		}
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Method for validate the Authentication details.
	 * 
	 * @param authCredentials
	 * @param header
	 * @param message
	 * @throws UnsupportedEncodingException
	 */
	private void validateAuthDetails(String authCredentials, APIHeader header, Message message)
			throws UnsupportedEncodingException {
		logger.debug(Literal.ENTERING);

		ServletRequest request = (ServletRequest) message.get("HTTP.REQUEST");
		// Set the IP address from the request object.
		String IP_ADDRESS = request.getRemoteAddr();
		header.setIpAddress(IP_ADDRESS);

		// remove special char.'s
		final String encodedUserPassword = authCredentials.replace(" ", "").replace("[", "").replace("]", "");

		// read channel, userid and/or token from the Authorization key.
		byte[] decodedBytes = Base64.decode(encodedUserPassword.getBytes());
		String headerAuthDetails = new String(decodedBytes, "UTF-8");

		// userId or Token based on channel
		String userName = "";
		String password = "";
		String channel = "";
		final StringTokenizer tokenizer = new StringTokenizer(headerAuthDetails, ":");
		// first token will be channelId
		channel = tokenizer.nextToken();
		header.setChannelId(channel);

		// Read the next token and based on the channel it can be user name or token.
		if (tokenizer.hasMoreTokens()) {
			userName = tokenizer.nextToken();
			// Get next token for user authentication.
			if (tokenizer.hasMoreTokens()) {
				password = tokenizer.nextToken();
			}
		}
		// If the channel is from user and token.
		if (CHANNEL_USER.equals(channel) && !userName.equals("") && password.equals("")) {

			WebAuthenticationDetails authDetails = new WebAuthenticationDetails((HttpServletRequest) request);
			long flag = validateUserSession(userName, authDetails);
			if (flag == 0) {
				getErrorDetails("92003", null);
			} else if (flag != 1) {
				getErrorDetails("92004", null);
			}
			// if the channel is user and has token.
		} else if (CHANNEL_USER.equals(channel) && !userName.equals("") && !password.equals("")) {

			WebAuthenticationDetails authDetails = new WebAuthenticationDetails((HttpServletRequest) request);
			String userLogin = authenticate(userName, password, authDetails);
			// if valid user
			String Token = RandomStringUtils.random(8, true, true);
			PFSParameter pFSParameter = systemParameterService.getApprovedPFSParameterById("WS_TOKENEXPPERIOD");
			if (pFSParameter != null) {
				if (pFSParameter.getSysParmValue() != null) {
					TOKEN_EXPIRY = Long.valueOf(pFSParameter.getSysParmValue());
				}
			}
			Timestamp expiry = new Timestamp(System.currentTimeMillis() + TOKEN_EXPIRY);
			String tokenId = createSession(userLogin, Token, expiry);
			header.setUserId(userLogin);
			header.setSecurityInfo(tokenId);
		} else if (CHANNEL_SERVER.equals(channel) && !userName.equals("")) { // if channel is server with token.

			WebAuthenticationDetails authDetails = new WebAuthenticationDetails((HttpServletRequest) request);
			// if is not a valid token then raises Error
			validateServerToken(userName, IP_ADDRESS, authDetails);
		} else {
			// if invalid channel

			getErrorDetails("92001", null);
		}
		logger.debug(Literal.LEAVING);
	}

	/*
	 * Method to get the error details based on the errorCode
	 */
	private void getErrorDetails(String errorCode, String[] valueParm) {

		ServiceExceptionDetails[] serviceExceptionDetailsArray = new ServiceExceptionDetails[1];
		ServiceExceptionDetails serviceExceptionDetails = new ServiceExceptionDetails();
		serviceExceptionDetails.setFaultCode(errorCode);
		// serviceExceptionDetails.setFaultMessage(errorDetailService.getErrorDetailById(errorCode).getErrorMessage());
		ErrorDetail erroDetail = ErrorUtil.getErrorDetail(new ErrorDetail(errorCode, valueParm));

		if (erroDetail != null) {
			serviceExceptionDetails.setFaultMessage(erroDetail.getError());
		}
		// Get the error details from authentication and throw the Fault details
		serviceExceptionDetailsArray[0] = serviceExceptionDetails;
		throw new Fault(new ServiceException(serviceExceptionDetailsArray));
	}

	/*
	 * Method to validate the channel server details
	 */
	private String validateServerToken(String token, String IPAddress, WebAuthenticationDetails authDetails) {
		logger.debug(Literal.ENTERING);

		ServerAuthentication webServiceServerSecurity = getServerAuthService().validateServer(token, IPAddress);

		if (webServiceServerSecurity == null) {
			getErrorDetails("92003", null);
		}

		String serverIP = webServiceServerSecurity.getIpAddress();
		if (StringUtils.equalsIgnoreCase(IPAddress, serverIP)) {
			if (StringUtils.isNotBlank(webServiceServerSecurity.getUsrLogin())) {
				SecurityUser userLoginDetails = getUserService().getUserByLogin(webServiceServerSecurity.getUsrLogin());
				this.setContext(userLoginDetails, authDetails);
			}
			return webServiceServerSecurity.getUsrLogin();
		} else {
			getErrorDetails("92003", null);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/*
	 * Methiod to authenticate the user details and set the user details in Spring context.
	 */
	private String authenticate(String userId, String password, WebAuthenticationDetails authDetails) {

		SecurityUser userLoginDetails = getUserService().getUserByLogin(userId);
		if (userLoginDetails == null) {
			getErrorDetails("92002", null);
		}
		try {
			if (isPasswordValid(userLoginDetails.getUsrPwd(), password)) {
				this.setContext(userLoginDetails, authDetails);
				return userLoginDetails.getUsrLogin();
			} else {
				getErrorDetails("92002", null);
			}
		} catch (WebServiceException we) {
			getErrorDetails("9999", null);
			logger.error(Literal.EXCEPTION, we);
			throw we;
		} catch (Fault e) { // any validation errors from application
			throw e;
		} catch (Exception e) {
			getErrorDetails("9999", null);
			logger.error(Literal.EXCEPTION, e);
		}
		return null;
	}

	/*
	 * Method to create new token for the user session with expiry period.
	 */
	private String createSession(String userLogin, String tokenId, Timestamp expiry) {
		logger.debug(Literal.ENTERING);

		UserAuthentication webServiceAuthanticastion = new UserAuthentication();
		webServiceAuthanticastion.setTokenId(tokenId);
		webServiceAuthanticastion.setExpiry(expiry);
		webServiceAuthanticastion.setUsrLogin(userLogin);

		logger.debug(Literal.LEAVING);
		return getUserAuthService().createSession(webServiceAuthanticastion);
	}

	/**
	 * Validates the authorization token.
	 * 
	 * @param encPass
	 * @param rawPass
	 * @return
	 */
	private boolean isPasswordValid(String userPassword, String loginPassword) throws WebServiceException {
		PasswordEncoder encoderImpl = null;

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

	/*
	 * Method to validate the user session with the token. Extend the session timeout on every request.
	 */
	private long validateUserSession(String token, WebAuthenticationDetails authDetails) {
		logger.debug(Literal.ENTERING);
		Timestamp expiry = new Timestamp(System.currentTimeMillis());
		UserAuthentication userAuthDetail = getUserAuthService().validateSession(token);

		if (userAuthDetail == null) {
			return -1;
		}

		if (StringUtils.isNotBlank(userAuthDetail.getUsrLogin())) {
			SecurityUser userLoginDetails = getUserService().getUserByLogin(userAuthDetail.getUsrLogin());
			this.setContext(userLoginDetails, authDetails);
		}
		if (expiry.getTime() <= userAuthDetail.getExpiry().getTime()) {
			PFSParameter pFSParameter = systemParameterService.getApprovedPFSParameterById("WS_TOKENEXPPERIOD");
			if (pFSParameter.getSysParmValue() != null) {
				TOKEN_EXPIRY = Long.valueOf(pFSParameter.getSysParmValue());
			}
			expiry = new Timestamp(System.currentTimeMillis() + TOKEN_EXPIRY);
			update(token, expiry);
			return 1;
		}

		logger.debug(Literal.LEAVING);
		return 0;
	}

	/*
	 * Update the token details in the database.
	 */
	private void update(String tokenId, Timestamp expiry) {
		UserAuthentication webServiceAuthanticastion = new UserAuthentication();
		webServiceAuthanticastion.setTokenId(tokenId);
		webServiceAuthanticastion.setExpiry(expiry);

		getUserAuthService().updateSession(webServiceAuthanticastion);
	}

	/*
	 * Method to add the user details in Spring context for usage in the application.
	 */
	private void setContext(SecurityUser userLoginDetails, WebAuthenticationDetails authDetails) {
		List<SecurityRole> securityRole = new ArrayList<>();
		Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();

		User userDetails = new User(userLoginDetails, grantedAuthorities, securityRole);
		UsernamePasswordAuthenticationToken currentUser = new UsernamePasswordAuthenticationToken(userDetails,
				userDetails.getPassword(), userDetails.getAuthorities());
		currentUser.setDetails(authDetails);

		SecurityContextHolder.getContext().setAuthentication(currentUser);
	}

	/**** SETTER/GETTERS ****/

	public UserAuthService getUserAuthService() {
		return userAuthService;
	}

	@Autowired
	public void setUserAuthService(UserAuthService userAuthService) {
		this.userAuthService = userAuthService;
	}

	public ServerAuthService getServerAuthService() {
		return serverAuthService;
	}

	@Autowired
	public void setServerAuthService(ServerAuthService serverAuthService) {
		this.serverAuthService = serverAuthService;
	}

	public UserService getUserService() {
		return userService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
