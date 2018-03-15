package com.pennanttech.interceptor;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
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
import javax.ws.rs.core.Response;
import javax.xml.ws.WebServiceException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.log4j.Logger;
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
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.errordetail.ErrorDetailService;
import com.pennant.backend.service.smtmasters.PFSParameterService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.ws.exception.ServiceException;
import com.pennant.ws.exception.ServiceExceptionDetails;
import com.pennanttech.framework.security.core.User;
import com.pennanttech.framework.security.core.service.UserService;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.util.APIConstants;
import com.pennanttech.util.APILogDetailDAO;
import com.pennanttech.ws.auth.model.ServerAuthentication;
import com.pennanttech.ws.auth.model.UserAuthentication;
import com.pennanttech.ws.auth.service.ServerAuthService;
import com.pennanttech.ws.auth.service.UserAuthService;
import com.pennanttech.ws.log.model.APILogDetail;

/**
 * This is REST services interceptor to read all the HTTP headers and set the details in the 
 * APIHeader bean object. The APIHeader bean object can be used in the service class and 
 * used to return the same header parameters in the response.
 * 
 * @author pennant
 *
 */
public class RestInHeaderInterceptor extends AbstractPhaseInterceptor<Message> {
	
	Logger logger = Logger.getLogger(RestInHeaderInterceptor.class);

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
	private ErrorDetailService errorDetailService;
	private UserService userService;
	@Autowired
	private PFSParameterService systemParameterService;

	private APILogDetailDAO		apiLogDetailDAO;

	/*
	 * Constructor
	 */
	public RestInHeaderInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	/***** Public Methods *****/
	
	@Override
	public void handleMessage(Message message) throws Fault {

		Map<String, String> additionalInfo = new HashMap<String, String>();
		String authCredentials = null;
		String IP_ADDRESS = null;
		boolean isRequestTimeGiven = false;
		int serviceVersion = 0;
		
		@SuppressWarnings("unchecked") // read the HTTP header details
		Map<String, List<String>> list = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);
		APIHeader header = new APIHeader();

		// set the request header details in the APIHEader object.
		try {
			//Validate Header Data
			validateHeaderDetails(list, header, message);
			
			// Set the IP address from the request object.
			IP_ADDRESS = header.getIpAddress();
			for (String key : list.keySet()) {
				if (APIHeader.API_AUTHORIZATION.equalsIgnoreCase(key)) {
					authCredentials = list.get(key).toString();
				} else
				// if service name is there in HTTP header, set it in APIHeader.
				if (APIHeader.API_SERVICENAME.equalsIgnoreCase(key)) {
					header.setServiceName(list.get(key).toString().replace("[", "").replace("]", ""));
				} else
				// if service version is there in HTTP header, set it in APIHeader.
				if (APIHeader.API_SERVICEVERSION.equalsIgnoreCase(key)) {
					serviceVersion = Integer.parseInt(header.getServiceVersion());
				} else
				// if entityId is there in HTTP header, set it in APIHeader.
				if (APIHeader.API_ENTITYID.equalsIgnoreCase(key)) {
					header.setEntityId(list.get(key).toString().replace("[", "").replace("]", ""));
				} else
				// if language is there in HTTP header, set it in APIHeader.
				if (APIHeader.API_LANGUAGE.equalsIgnoreCase(key)) {
					header.setLanguage(list.get(key).toString().replace("[", "").replace("]", ""));
				} else
				// if messageId is there in HTTP header, set it in APIHeader.
				if (APIHeader.API_MESSAGEID.equalsIgnoreCase(key)) {
					header.setMessageId(list.get(key).toString().replace("[", "").replace("]", ""));
				}else
				// if service version is there in HTTP header, set it in APIHeader.
				if (APIHeader.API_REQ_TIME.equalsIgnoreCase(key)) {
					isRequestTimeGiven = true;
					try {
						String sample = list.get(key).toString().replace("[", "").replace("]", "");
						Date e = dateFormat.parse(sample);
						header.setRequestTime(e);
					} catch (Exception e) {
						logger.error("Exception", e);
						//TODO:Ganesh if it not a valid RequestTime
						header.setRequestTime(new Timestamp(System.currentTimeMillis()));
						
					}
				} else {
					// all other header details are added in additional info map.
					additionalInfo.put(key, list.get(key).toString().replace("[", "").replace("]", ""));
				}
			}
			if (!isRequestTimeGiven) {
				header.setRequestTime(new Timestamp(System.currentTimeMillis()));
			}
			header.setAdditionalInfo(additionalInfo);
			message.getExchange().put(APIHeader.API_HEADER_KEY, header);

			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			APILogDetail apiLogDetail = (APILogDetail) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_LOG_KEY);
			if (apiLogDetail != null) {
				if (StringUtils.isNotBlank(authCredentials)) {
					apiLogDetail.setAuthKey(authCredentials);
				} else {
					if (reqHeaderDetails != null)
						apiLogDetail.setAuthKey(reqHeaderDetails.getSecurityInfo());
				}
				if (reqHeaderDetails != null) {
					apiLogDetail.setClientIP(reqHeaderDetails.getIpAddress());
					apiLogDetail.setMessageId(reqHeaderDetails.getMessageId());
					apiLogDetail.setEntityId(reqHeaderDetails.getEntityId());
					apiLogDetail.setLanguage(reqHeaderDetails.getLanguage());
					apiLogDetail.setServiceVersion(serviceVersion);
					apiLogDetail.setHeaderReqTime(new Timestamp(reqHeaderDetails.getRequestTime().getTime()));
				}
			}
			//if given messageId is notBlack then check the messageId is already processed or not.
			if (StringUtils.isNotBlank(apiLogDetail.getMessageId())) {
				validateMessageId(message, header, apiLogDetail);
			}
		
			logger.debug("Leaving");

		} catch (UnsupportedEncodingException e1) {  // Invalid request URL
			logger.error("Exception:", e1);
			getErrorDetails("9999");
			
		} catch (SQLException se) { // Server side exceptions
			logger.error("Exception:", se);
			getErrorDetails("9999");
			
		} catch (Fault e) { // any validation errors from application
			APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY);
			APILogDetail apiLogDetail = (APILogDetail) PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_LOG_KEY);
			if (apiLogDetail != null) {
				if (StringUtils.isNotBlank(authCredentials)) {
					apiLogDetail.setAuthKey(authCredentials);
				} else {
					if (reqHeaderDetails != null)
						apiLogDetail.setAuthKey(reqHeaderDetails.getSecurityInfo());
				}
				apiLogDetail.setClientIP(IP_ADDRESS);
			}
			logger.error("Exception:", e);
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
	 * @throws SQLException
	 */
	private void validateHeaderDetails(Map<String, List<String>> headerMap, APIHeader header, Message message)
			throws UnsupportedEncodingException, SQLException {
		logger.debug(Literal.ENTERING);
		boolean isHeaderContainsAuth = false;
		boolean isHeaderContainsMsgId = false;
		for (String key : headerMap.keySet()) {
			if (APIHeader.API_AUTHORIZATION.equalsIgnoreCase(key)) {
				String authCredentials = headerMap.get(key).toString();
				if (authCredentials != null) {
					isHeaderContainsAuth = true;
					validateAuthDetails(authCredentials, header, message);
				}

			} else if (APIHeader.API_MESSAGEID.equalsIgnoreCase(key)) {
				String msgId = headerMap.get(key).toString().replace("[", "").replace("]", "");
				if (StringUtils.isNotBlank(msgId)) {
					isHeaderContainsMsgId = true;
				}

			} else if (APIHeader.API_SERVICEVERSION.equalsIgnoreCase(key)) {
				String serviceVersion = headerMap.get(key).toString().replace("[", "").replace("]", "");
				if (StringUtils.isNotBlank(serviceVersion)) {
					try {
						Integer.parseInt(serviceVersion);
						header.setServiceVersion(serviceVersion);
					} catch (Exception e) {
						logger.error("Exception: ", e);
						//TODO:Ganesh In case of serviceVersion not a Integer 
						header.setServiceVersion("0");
					}
				}
			}
		}
		// throw error code in case of empty credentials
		if (!isHeaderContainsAuth) {
			getErrorDetails("92001");
		}
		// throw error code in case of empty MessageId
		if (!isHeaderContainsMsgId) {
			//TODO:Uncomment This if messageID is Mandatory
			//getErrorDetails("92005");
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
	 * @throws SQLException
	 */
	private void validateAuthDetails(String authCredentials, APIHeader header, Message message)
			throws UnsupportedEncodingException, SQLException {
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
			// if userid then read the password.
			if (tokenizer.hasMoreTokens()) {
				password = tokenizer.nextToken();
			}
		}
		// If the channel is from user and token.
		if (CHANNEL_USER.equals(channel) && !userName.equals("") && password.equals("")) {

			WebAuthenticationDetails authDetails = new WebAuthenticationDetails((HttpServletRequest) request);
			long flag = validateUserSession(userName, authDetails);
			if (flag == 0) {
				getErrorDetails("92003");
			} else if (flag != 1) {
				getErrorDetails("92004");
			}
			// if the channel is user and has password.
		} else if (CHANNEL_USER.equals(channel) && !userName.equals("") && !password.equals("")) {

			WebAuthenticationDetails authDetails = new WebAuthenticationDetails((HttpServletRequest) request);
			String userLogin = authenticate(userName, password, authDetails);
			//if valid user
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
			//if is not a valid token then raises Error
			validateServerToken(userName, IP_ADDRESS, authDetails);
		} else {
			//if invalid channel
			getErrorDetails("92001");
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for validate the given messageID weather it is already processed or not. if it is already processed then
	 * sets the previous response as current response in message.
	 * 
	 * @param message
	 * @param header
	 * @param apiLogDetail
	 */
	private void validateMessageId(Message message, APIHeader header, APILogDetail apiLogDetail) {
		logger.debug(Literal.ENTERING);
		APILogDetail previousApiLogDetail = getLogMessageById(header.getMessageId());
		if (previousApiLogDetail != null) {
			//if the given messageId is already processed then sets the previous response as current response.
			//conflict response code is 409.
			Response response = null;
			response = Response.status(Response.Status.CONFLICT).entity(previousApiLogDetail.getResponse()).build();
			//put the previous response in message and set the header return code and desc.
			message.getExchange().put(Response.class, response);
			header.setReturnCode(APIConstants.RES_DUPLICATE_MSDID_CODE);
			header.setReturnDesc(APIConstants.RES_DUPLICATE_MSDID);
			//for logging purpose.
			apiLogDetail.setReference(previousApiLogDetail.getReference());
			apiLogDetail.setKeyFields(previousApiLogDetail.getKeyFields());
			apiLogDetail.setStatusCode(APIConstants.RES_DUPLICATE_MSDID_CODE);
			apiLogDetail.setError(previousApiLogDetail.getError());
			apiLogDetail.setKeyFields(previousApiLogDetail.getKeyFields());
			apiLogDetail.setProcessed(false);
		} else {
			apiLogDetail.setProcessed(true);
		}
		logger.debug(Literal.LEAVING);
	}
	
	/*
	 * Method to get the error details based on the errorCode
	 */
	private void getErrorDetails(String errorCode) {
		
		ServiceExceptionDetails serviceExceptionDetailsArray[] = new ServiceExceptionDetails[1];
		ServiceExceptionDetails serviceExceptionDetails = new ServiceExceptionDetails();
		serviceExceptionDetails.setFaultCode(errorCode);
		// serviceExceptionDetails.setFaultMessage(errorDetailService.getErrorDetailById(errorCode).getErrorMessage());
		ErrorDetail erroDetail = errorDetailService.getErrorDetailById(errorCode);
		
		if (erroDetail != null) {
			serviceExceptionDetails.setFaultMessage(erroDetail.getMessage());
		}
		
		// Get the error details from authentication and throw the Fault details
		serviceExceptionDetailsArray[0] = serviceExceptionDetails;
		throw new Fault(new ServiceException(serviceExceptionDetailsArray));
	}

	/*
	 * Method to validate the channel server details                          
	 */
	private String validateServerToken(String token, String IPAddress, WebAuthenticationDetails authDetails)
			throws SQLException {
		logger.debug("Entering");

		ServerAuthentication webServiceServerSecurity = getServerAuthService().validateServer(token, IPAddress);

		if (webServiceServerSecurity == null) {
			getErrorDetails("92003");
		}

		String serverIP = webServiceServerSecurity.getIpAddress();
		if (StringUtils.equalsIgnoreCase(IPAddress, serverIP)) {
			if(StringUtils.isNotBlank(webServiceServerSecurity.getUsrLogin())){
			SecurityUser userLoginDetails = getUserService().getUserByLogin(webServiceServerSecurity.getUsrLogin());
			this.setContext(userLoginDetails, authDetails);
			}
			return webServiceServerSecurity.getUsrLogin();
		} else {
			getErrorDetails("92003");
		}

		logger.debug("Leaving");
		return null;
	}

	/*
	 * Methiod to authenticate the user details and set the user details in Spring context.
	 */
	private String authenticate(String userId, String password, WebAuthenticationDetails authDetails) {

		SecurityUser userLoginDetails = getUserService().getUserByLogin(userId);
		if (userLoginDetails == null) {
			getErrorDetails("92002");
		}
		try {
			if (isPasswordValid(userLoginDetails.getUsrPwd(), password)) {
				this.setContext(userLoginDetails, authDetails);
				return userLoginDetails.getUsrLogin();
			} else {
				getErrorDetails("92002");
			}
		} catch (WebServiceException we) {
			getErrorDetails("9999");
			logger.error("Exception: ", we);
			throw we;
		} catch (Fault e) { // any validation errors from application
			throw e;
		} catch (Exception e) {
			getErrorDetails("9999");
			logger.error("Exception: ", e);
		}
		return null;
	}

	/*
	 * Method to create new token for the user session with expiry period.
	 */
	private String createSession(String userLogin, String tokenId, Timestamp expiry) throws SQLException {
		logger.debug("Entering");

		UserAuthentication webServiceAuthanticastion = new UserAuthentication();
		webServiceAuthanticastion.setTokenId(tokenId);
		webServiceAuthanticastion.setExpiry(expiry);
		webServiceAuthanticastion.setUsrLogin(userLogin);

		logger.debug("Leaving");
		return getUserAuthService().createSession(webServiceAuthanticastion);
	}

	/**
	 * Method to decrypt the password if requied and validate against the actual password.
	 * 
	 * Validating password userPassword = dbPassword ,loginPassword = password given by user
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

		if (encoderImpl.matches(loginPassword,userPassword)) {
			return true;
		}
		return false;
	}

	/*
	 * Method to validate the user session with the token. Extend the session timeout on every request.
	 */
	private long validateUserSession(String token, WebAuthenticationDetails authDetails) throws SQLException {
		logger.debug("Entering");
		Timestamp expiry = new Timestamp(System.currentTimeMillis());
		UserAuthentication userAuthDetail = getUserAuthService().validateSession(token);

		if (userAuthDetail == null) {
			return -1;
		}
		
		if (userAuthDetail != null) {
			if(StringUtils.isNotBlank(userAuthDetail.getUsrLogin())){
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
		}

		logger.debug("Leaving");
		return 0;
	}

	/*
	 * Update the token details in the database.
	 */
	private void update(String tokenId, Timestamp expiry) throws SQLException {
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

	/**
	 * Method for check the API log table, based on the given messageId if record found return the latest record
	 * response otherwise return null.
	 * 
	 * @return apiLogDetail
	 */
	private APILogDetail getLogMessageById(String messageId) {
		return apiLogDetailDAO.getLogByMessageId(messageId);
	}
	
	/****   SETTER/GETTERS ****/
	
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

	public ErrorDetailService getErrorDetailService() {
		return errorDetailService;
	}

	@Autowired
	public void setErrorDetailService(ErrorDetailService errorDetailService) {
		this.errorDetailService = errorDetailService;
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
	
	@Autowired
	public void setApiLogDetailDAO(APILogDetailDAO apiLogDetailDAO) {
		this.apiLogDetailDAO = apiLogDetailDAO;
	}

}
