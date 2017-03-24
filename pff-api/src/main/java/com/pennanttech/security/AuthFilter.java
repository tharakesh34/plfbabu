/*package com.pennanttech.security;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.errordetail.ErrorDetail;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.UserService;
import com.pennant.backend.service.errordetail.ErrorDetailService;
import com.pennant.backend.service.smtmasters.PFSParameterService;
import com.pennant.policy.model.UserImpl;
import com.pennanttech.ws.auth.model.ServerAuthentication;
import com.pennanttech.ws.auth.model.UserAuthentication;
import com.pennanttech.ws.auth.service.ServerAuthService;
import com.pennanttech.ws.auth.service.UserAuthService;

public class AuthFilter implements javax.servlet.Filter {
	Logger logger = Logger.getLogger(AuthFilter.class);
	
	public static final String AUTHENTICATION_HEADER = "Authorization";
	public static long TOKENExpiry = 300000;
	public static final String CHANNEL_USER = "user";
	public static final String CHANNEL_SERVER = "device";
	public static final String AUTHENTICATION_CODE = "StatusCode";
	public static final String AUTHENTICATION_STATUS = "StatusDesc";
	@Autowired
	private PasswordEncoder passwordEncoder;

	private UserAuthService userAuthService;

	private ServerAuthService serverAuthService;

	private ErrorDetailService errorDetailService;

	private UserService userService;

	private PFSParameterService pFSParameterService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter) throws IOException,
			ServletException {
		logger.debug("Entering");
		
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			String authCredentials = httpServletRequest.getHeader(AUTHENTICATION_HEADER);
			String IPAddress = httpServletRequest.getRemoteAddr();
			String key = ""; // userId or Token based on channel
			String password = "";
			String channel = "";
			
			if (authCredentials != null) {
				final String encodedUserPassword = authCredentials.replaceFirst(" ", "");
				String authDetails = null;
				try {
					byte[] decodedBytes = Base64.decode(encodedUserPassword.getBytes());
					authDetails = new String(decodedBytes, "UTF-8");

					final StringTokenizer tokenizer = new StringTokenizer(authDetails, ":");
					channel = tokenizer.nextToken();
					if (tokenizer.hasMoreTokens()) 	key = tokenizer.nextToken();
					if (tokenizer.hasMoreTokens()) password = tokenizer.nextToken();
				} catch (Exception e) {
					getErrorDetails(response, "EA006");
				}
			}
			String query = ((HttpServletRequest) request).getQueryString();

			if ((StringUtils.containsIgnoreCase(query, "wsdl") || StringUtils.containsIgnoreCase(query, "_wadl"))
					&& query != null) {
				filter.doFilter(request, response);
			} else {
				if (CHANNEL_USER.equals(channel) &&  !key.equals("") && password.equals("")) {
					Timestamp expiry = new Timestamp(System.currentTimeMillis());
					try {
						long flag = validatesession(key, expiry);
						if (flag == 1) {
							filter.doFilter(request, response);
						} else {
							if (flag == 0) {
								if (response instanceof HttpServletResponse) {
									getErrorDetails(response, "EA006");
								}
							} else {
								getErrorDetails(response, "EA006");
							}
						}
					} catch (SQLException e) {
						logger.error("Exception", e);
						getErrorDetails(response, "E011");
					}
				} else if (CHANNEL_USER.equals(channel) && !key.equals("") && !password.equals("")) {
					WebAuthenticationDetails authDetails = new WebAuthenticationDetails(httpServletRequest);
					long userId = authenticate(key, password, authDetails);
					if (userId > 0) {
						HttpServletResponse httpServletResponse = (HttpServletResponse) response;
						String Token = RandomStringUtils.random(8, true, true);
						PFSParameter pFSParameter = getpFSParameterService().getApprovedPFSParameterById("WS_TOKENEXPPERIOD");
						if (pFSParameter != null) {
							if (pFSParameter.getSysParmValue() != null)
								TOKENExpiry = Long.valueOf(pFSParameter.getSysParmValue());
						}
						Timestamp expiry = new Timestamp(System.currentTimeMillis() + TOKENExpiry);
						try {
							String tokenId = createSession(userId, Token, expiry);
							httpServletResponse.setHeader("Token", tokenId);
							// Process the request
							filter.doFilter(request, response);
						} catch (SQLException e) {
							logger.error("Exception", e);
						}
					} else {
						getErrorDetails(response, "99002");
					}
				} else if (CHANNEL_SERVER.equals(channel) && !key.equals("")) {
						try {
							long userId = validateserver(key, IPAddress);
							if (userId > 0) {
								filter.doFilter(request, response);
							} else {
								if (response instanceof HttpServletResponse) {
									getErrorDetails(response, "EA008");
								}
							}
						} catch (SQLException e) {
							logger.error("Exception", e);
							getErrorDetails(response, "E011"); //
						}
				} else {
					if (response instanceof HttpServletResponse) {
						getErrorDetails(response, "99003");
					}
				}
			}
		} else {
			if (response instanceof HttpServletResponse) {
				HttpServletResponse httpServletResponse = (HttpServletResponse) response;
				httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				httpServletResponse.setHeader(AUTHENTICATION_CODE, "401");
				httpServletResponse.setHeader(AUTHENTICATION_STATUS, "Unauthorized");
			}
		}

		logger.debug("Leaving");
	}

	private void getErrorDetails(ServletResponse response, String errorCode) {
		logger.debug("Entering");
		
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		ErrorDetail errorDetails = getErrorDetailService().getApprovedErrorDetailById(errorCode);
		if (errorDetails != null && errorDetails.getErrorMessage() != null) {
			httpServletResponse.setHeader(AUTHENTICATION_CODE, errorCode);
			httpServletResponse.setHeader(AUTHENTICATION_STATUS, errorDetails.getErrorMessage());
		} else {
			httpServletResponse.setHeader(AUTHENTICATION_CODE, "E011");
			httpServletResponse.setHeader(AUTHENTICATION_STATUS, "UnknownException");
		}
		
		logger.debug("Leaving");
	}

	private String createSession(long userId, String tokenId, Timestamp expiry) throws SQLException {
		logger.debug("Entering");
		
		UserAuthentication webServiceAuthanticastion = new UserAuthentication();
		webServiceAuthanticastion.setTokenId(tokenId);
		webServiceAuthanticastion.setExpiry(expiry);
		webServiceAuthanticastion.setUserLogin(userId);
		
		logger.debug("Leaving");
		return getUserAuthService().createSession(webServiceAuthanticastion);
	}

	private long validatesession(String token, Timestamp expiry) throws SQLException {
		logger.debug("Entering");
		
		UserAuthentication webServiceAuthanticastion = getUserAuthService().validateSession(token, expiry);

		if (webServiceAuthanticastion == null) {
			return -1;
		}
		if (webServiceAuthanticastion != null) {
			if (expiry.getTime() <= webServiceAuthanticastion.getExpiry().getTime()) {
				PFSParameter pFSParameter = getpFSParameterService().getApprovedPFSParameterById("WS_TOKENEXPPERIOD");
				if (pFSParameter.getSysParmValue() != null)
					TOKENExpiry = Long.valueOf(pFSParameter.getSysParmValue());
				expiry = new Timestamp(System.currentTimeMillis() + TOKENExpiry);
				update(token, expiry);
				return 1;

			}
		}
		
		logger.debug("Leaving");
		return 0;
	}

	private void update(String tokenId, Timestamp expiry) throws SQLException {
		UserAuthentication webServiceAuthanticastion = new UserAuthentication();
		webServiceAuthanticastion.setTokenId(tokenId);
		webServiceAuthanticastion.setExpiry(expiry);

		getUserAuthService().updateSession(webServiceAuthanticastion);
	}

	private long validateserver(String token, String IPAddress) throws SQLException {
		logger.debug("Entering");
		
		ServerAuthentication webServiceServerSecurity = getServerAuthService().validateServer(token, IPAddress);

		if (webServiceServerSecurity == null) {
			return -1;
		}

		String serverIP = webServiceServerSecurity.getIpAddress();
		if (StringUtils.equalsIgnoreCase(IPAddress, serverIP)) {
			return 1;
		}
		
		logger.debug("Leaving");
		return 0;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(config
				.getServletContext());
		this.setServerAuthService((ServerAuthService) springContext.getBean("serverAuthService"));
		this.userService = (UserService) springContext.getBean("userService");
		this.userAuthService = (UserAuthService) springContext.getBean("userAuthService");
		this.errorDetailService = (ErrorDetailService) springContext.getBean("errorDetailService");
		this.pFSParameterService = (PFSParameterService) springContext.getBean("pFSParameterService");
	}

	private long authenticate(String userId, String password, WebAuthenticationDetails authDetails) {

		SecurityUser userLoginDetails = getUserService().getUserByLogin(userId);
		if (userLoginDetails == null) {
			return -1;
		}
		try {
			if (isPasswordValid(userLoginDetails.getUsrPwd(), password)) {
				this.setContext(userLoginDetails, authDetails);
				return userLoginDetails.getUsrID();
			}
		} catch (WebServiceException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (Exception e) {
			logger.warn("Exception: ", e);
		}

		return 0;
	}

	private void setContext(SecurityUser userLoginDetails, WebAuthenticationDetails authDetails) {
		List<SecurityRole> securityRole= userService.getUserRolesByUserID(userLoginDetails.getId()); 	 
		Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>() ;

		UserImpl userDetails = new UserImpl(userLoginDetails, grantedAuthorities, securityRole);
		UsernamePasswordAuthenticationToken currentUser = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
		currentUser.setDetails(authDetails);

		SecurityContextHolder.getContext().setAuthentication(currentUser);
	}
	
	*//**
	 * Validating password userPassword = dbPassword ,loginPassword = password given by user
	 * 
	 * @param encPass
	 * @param rawPass
	 * @return
	 *//*
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

		if (encoderImpl.matches(userPassword, loginPassword)) {
			return true;
		}
		return false;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public ErrorDetailService getErrorDetailService() {
		return errorDetailService;
	}

	public void setErrorDetailService(ErrorDetailService errorDetailService) {
		this.errorDetailService = errorDetailService;
	}

	public UserAuthService getUserAuthService() {
		return userAuthService;
	}

	public void setUserAuthService(UserAuthService userAuthService) {
		this.userAuthService = userAuthService;
	}

	public ServerAuthService getServerAuthService() {
		return serverAuthService;
	}

	public void setServerAuthService(ServerAuthService serverAuthService) {
		this.serverAuthService = serverAuthService;
	}

	public PFSParameterService getpFSParameterService() {
		return pFSParameterService;
	}

	public void setpFSParameterService(PFSParameterService pFSParameterService) {
		this.pFSParameterService = pFSParameterService;
	}

}
*/