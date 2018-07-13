package com.pennant.app.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.UIDFolder;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.pennant.backend.model.mail.MailTemplate;
import com.pennanttech.pennapps.core.resource.Literal;

public class MailUtility implements Serializable {
    private static final long serialVersionUID = 7959183655705303553L;
	private static final Logger logger = Logger.getLogger(MailUtility.class);

	// Outgoing Mail Properties
	private String outgoingHost;
	private String outgoingPort;
	private boolean outgoingAuth;
	private boolean outgoingDebug;
	private String outgoingUserName;
	private String outgoingPassword;
	private String outgoingEncType;
	private String fromEmail;
	private transient Session outgoingSession = null;
	
	// Incoming Mail Properties
	private String incomingHost;
	private int incomingPort;
	private boolean incomingAuth= true;
	private String incomingUserName;
	private String incomingPassword;
	private String incomingEncType;
	private String incomingProtocol;
	private String incomingFolder;
	private transient Session incomingSession = null;
	
	// Mail Constants
	private static final String ENCRYPTION_TYPE_SSL = "SSL";
	private static final String ENCRYPTION_TYPE_TLS = "TLS";
	
	public MailUtility(){
		super();
	}
	
	public MailUtility(String smtpHost, String smtpPort, boolean auth, boolean debug, String userName, String password, String encType,
				String incomingHost, String incomingPort, String incomingUserName, String incomingPassword, String incomingEncType, 
				String incomingProtocol, String incomingFolder) {
		// Set Outgoing mail server properties
		setOutgoingHost(smtpHost);
		setOutgoingPort(smtpPort);
		setOutgoingDebug(debug);
		setOutgoingAuth(auth);
		setOutgoingUserName(userName);
		setOutgoingPassword(password);
		setOutgoingEncType(encType);
		
		// Set Incoming mail server properties
		setIncomingAuth(true);
		setIncomingHost(incomingHost);
		setIncomingPort(Integer.parseInt(incomingPort));
		setIncomingUserName(incomingUserName);
		setIncomingPassword(incomingPassword);
		setIncomingEncType(incomingEncType);
		setIncomingProtocol(incomingProtocol);
		setIncomingFolder(incomingFolder);		
	}
	
	/**
	 * Method to create a Session Instance for outgoing mails.<br>
	 * Creates session and allows to access mail servers over connections secured using SSL or TLS, <br>
	 * if respective properties mail.smtp.starttls.enable or mail.smtp.ssl.enable set to TRUE. <br>
	 * Use of the STARTTLS command is preferred in cases where the server supports both SSL and non-SSL connections.
	 * @param mailTemplate
	 * @throws Exception
	 * 
	 */
	private void createOutgoingSession() {
		logger.debug(Literal.ENTERING);
		Authenticator authenticator = null;
		if(isOutgoingAuth()){
			authenticator = new Authenticator(getOutgoingUserName(), getOutgoingPassword());
		}

		Properties props = new Properties();
		props.put("mail.smtp.host", getOutgoingHost());
		props.put("mail.smtp.port", getOutgoingPort());
		props.put("mail.smtp.auth", isOutgoingAuth());
		props.put("mail.smtp.debug", isOutgoingDebug());
		
		// Use TLS/SSL to encrypt communication with SMTP server
		if(getOutgoingEncType().equals(ENCRYPTION_TYPE_TLS)){
			//TRUE if supported by the server
			props.put("mail.smtp.starttls.enable",true);
		}else if(getOutgoingEncType().equals(ENCRYPTION_TYPE_SSL)){
			props.put("mail.smtp.ssl.enable",true);		
			props.put("mail.smtp.socketFactory.port", "465"); 
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
			props.put("mail.smtp.socketFactory.fallback", false);
		}
		
		logger.debug(Literal.LEAVING);
 		setOutgoingSession(Session.getInstance(props, authenticator));
	}
	
	private class Authenticator extends javax.mail.Authenticator {
		private PasswordAuthentication authentication;

		public Authenticator(String userName, String password) {
			authentication = new PasswordAuthentication(userName, password);
		}
		
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return authentication;
		}
	}
	
	/***
	 * Method to send mail, by creating a session with the given parameters in mail server. <br>
	 * throws exception if any parameter was not specified.
	 * @param mailTemplate
	 * @param smtpHost
	 * @param smtpPort
	 * @param auth
	 * @param debug
	 * @param userName
	 * @param password
	 * @param encType
	 * @throws Exception
	 */
	public void sendMail(MailTemplate mailTemplate, String smtpHost, String smtpPort,
	        boolean auth, boolean debug, String userName, String password, String encType)
	        throws Exception {
		logger.debug(Literal.ENTERING);
		//Check all parameters was given or not.
		if (StringUtils.isBlank(smtpHost)
		        || StringUtils.isBlank(smtpPort)
		        || StringUtils.isBlank(userName)
		        || StringUtils.isBlank(encType)) {
			throw new Exception("One of the parameter is missing or incorrect");
		}
		// Set Outgoing mail server properties
		setOutgoingHost(smtpHost);
		setOutgoingPort(smtpPort);
		setOutgoingAuth(auth);
		setOutgoingDebug(debug);
		setOutgoingUserName(userName);
		setOutgoingPassword(password);
		setOutgoingEncType(encType);
		// Send Mail
		sendMail(mailTemplate);
		logger.debug(Literal.LEAVING);
	}
	
	
	/**
	 * Method to send Mail with the template format given in parameter.
	 * @param mailTemplate
	 * @throws Exception
	 */
	public void sendMail(MailTemplate mailTemplate) throws Exception {
		logger.debug(Literal.ENTERING);
		Transport transport = null; 
		Session session = null;
		try {
			if(null == mailTemplate){
				throw new Exception("Mail Template was not specified");
			}
			if(null == getOutgoingSession()){
				createOutgoingSession();
			}
			session = getOutgoingSession();
			
			MimeMessage message = new MimeMessage(session);			
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			
			if(fromEmail != null) {
				helper.setFrom(new InternetAddress(fromEmail));
			} else {
				helper.setFrom(new InternetAddress(getOutgoingUserName()));
			}
			
			helper.setSubject(mailTemplate.getEmailSubject());
			helper.setText(mailTemplate.getLovDescFormattedContent(), true);
			helper.setTo(mailTemplate.getLovDescMailId());
			helper.setSentDate(DateUtility.getSysDate());
			if(mailTemplate.getLovDescEmailAttachment() != null){
				helper.addAttachment(mailTemplate.getLovDescAttachmentName(), new ByteArrayResource(mailTemplate.getLovDescEmailAttachment()));
			}
			
			logger.debug("Number of Mails Sending :: "+ mailTemplate.getLovDescMailId().length);
			transport = session.getTransport("smtp");
			transport.connect();
			transport.sendMessage(message, message.getAllRecipients());
			
		} catch(NoSuchProviderException e){
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Provider for the given protocol is not found. Mail sending failed...!");
		} catch (MessagingException e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Mail sending failed...!");
		} finally{
			if(null != transport && transport.isConnected()){
				transport.close();
			}
			if(null != session){
				session = null;
			}
		}
		logger.debug(Literal.LEAVING);
	}
	
	/***
	 * Method to get incoming mail server session created using the given parameters<br>
	 * throws exception if any parameter was not specified. 
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 * @param encType
	 * @param protocol
	 * @param auth
	 * @return Session
	 * @throws Exception
	 */
	public Session getIncomingSession(String host, String port, 
	        String userName, String password, String encType, String protocol)
	        throws Exception {
		logger.debug(Literal.ENTERING);

		//Check all parameters was given or not.
		if (StringUtils.isBlank(host)
		        || StringUtils.isBlank(port)
		        || StringUtils.isBlank(userName)
		        || StringUtils.isBlank(password)
		        || StringUtils.isBlank(encType)
		        || StringUtils.isBlank(protocol)) {
			throw new Exception("One of the parameter is missing or incorrect");
		}

		// Set Outgoing mail server properties
		setIncomingHost(host);
		setIncomingPort(Integer.parseInt(port));
		setIncomingUserName(userName);
		setIncomingPassword(password);
		setIncomingEncType(encType);
		setIncomingAuth(true);
		
		logger.debug(Literal.LEAVING);
		if("POP3".equalsIgnoreCase(protocol)){
			setIncomingSession(getPOP3Session());
		}else {
			setIncomingSession(getIMAPSession());	
		}		
		return getIncomingSession();
	}
	
	/**
	 * Method returns the Session object of POP3 protocol. <br>
	 * Users have to close the session and store in the finally block of implementation method.
	 * @return Store
	 */
	public Session getPOP3Session(){
		logger.debug(Literal.ENTERING);
		Session session = null;
		try {
			Properties props = new Properties();
			props.put("mail.store.protocol", "pop3");
			if(getIncomingEncType().equals(ENCRYPTION_TYPE_SSL)){
				props.put("mail.pop3.ssl.enable", true);
			}
			props.put("mail.pop3.host", getIncomingHost());
			props.put("mail.pop3.port", getIncomingPort());
			
			Authenticator authenticator = null;
			if(isIncomingAuth()){
				authenticator = new Authenticator(getIncomingUserName(), getIncomingPassword());
			}
			session = Session.getInstance(props, authenticator);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return session;
	}
	
	/**
	 * Method returns the Session object of IMAP protocol. <br>
	 * Users have to close the session and store in the finally block of implementation method.
	 * @return Store
	 */
	public Session getIMAPSession(){
		logger.debug(Literal.ENTERING);
		Session session = null;
		try {
			Properties props = new Properties();
			props.put("mail.store.protocol", "imap");
			if(getIncomingEncType().equals(ENCRYPTION_TYPE_SSL)){
				props.put("mail.imap.ssl.enable", true);
			}
			props.put("mail.imap.host", getIncomingHost());
			props.put("mail.imap.port", getIncomingPort());
			
			Authenticator authenticator = null;
			if(isIncomingAuth()){
				authenticator = new Authenticator(getIncomingUserName(), getIncomingPassword());
			}
			session = Session.getInstance(props, authenticator);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return session;
	}
	
	/***
	 * Method to close the given Session object.<br>
	 * If any store in the given session object is open it will close that store also.<br>
	 * 
	 * @param session(Session)
	 * @throws Exception 
	 */
	public void closeSession(Session session) throws Exception{
		logger.debug(Literal.ENTERING);
		if(null != session){
			try {
				Store store = session.getStore();
	            if(store!=null && store.isConnected()) {
	            	store.close();
	            }
            }catch(NoSuchProviderException e){
    			logger.error(Literal.EXCEPTION, e);
    			throw new Exception("Provider for the given protocol is not found. Session not closed...!");
    		} catch (MessagingException e) {
    			logger.error(Literal.EXCEPTION, e);
    			throw new Exception("Session not closed...!");
    		} 
			session = null;
		}
		logger.debug(Literal.LEAVING);
	}
	
	/***
	 * Method to download mail attachments using either IMAP or POP3 protocol into specified path.<br>
	 * If session parameter is not specified then default IMAP protocol session will be created.<br>
	 * If no values specified for the downloadPath or folderName
	 * it will use the default values specified in SMTParameters.<br>
	 * Set unreadMailsOnly parameter to true to read only UNREAD mails,<br>
	 * Updates the message flag as SEEN once download completes.<br> 
	 * Once download completes then closes session, store and folder.<br>
	 * @param isession 
	 * @param downloadPath
	 * @param folderName
	 * @param unreadMailsOnly
	 * @param isIMAP
	 * @return downloadPath
	 * @throws Exception
	 */
	public String downloadAttachment(String downloadPath) throws Exception{
		logger.debug(Literal.ENTERING);
		Session session = null;
		Folder folder = null;
		Store store = null;
		Message[] messages = null;
		boolean imap = false;
		long messageId = Long.MIN_VALUE; 
		try {
			if("POP3".equals(incomingProtocol)){
				session = getPOP3Session();
			}else{
				session = getIMAPSession();
				imap = true;
			}

			store = session.getStore();
			if(!store.isConnected()){
				store.connect();
			}
			folder = store.getFolder(incomingFolder);
			folder.open(Folder.READ_WRITE);
			FlagTerm term = new FlagTerm(new Flags(Flag.SEEN), false);
			messages = folder.search(term);
			
			logger.info("New Message Count: " + messages.length);
			
			for (Message message : messages) {
				if(imap){
					UIDFolder uf = (UIDFolder)folder;
					messageId = uf.getUID(message);
				}
				download(downloadPath, message, messageId);
				message.setFlag(Flag.SEEN, true);
			}
			
		} catch(NoSuchProviderException e){
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Provider for the given protocol is not found. Mail messages reading failed...!");
		} catch (MessagingException e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Mail messages reading failed...!");
		} finally{
			try {
				if (folder != null && folder.isOpen()) {
					folder.close(true);
				}

				if (store != null && store.isConnected()) {
					store.close();
				}
			} catch (MessagingException e) {
				logger.error(Literal.EXCEPTION, e);
			}
			folder = null;
			store = null;
			if (null != session) {
				session = null;
			}
		}
		
		logger.debug(Literal.LEAVING);
		return downloadPath;
	}
	
	private String download(String downloadPath, Message message, long msgId) throws MessagingException, IOException {
		logger.debug(Literal.ENTERING);
		String fileName = null;

		if (null != message.getContentType() && message.getContentType().contains("multipart")) {
			Multipart multipart = (Multipart) message.getContent();

			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);

				if (bodyPart.getDisposition() != null
						&& Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())
						&& StringUtils.isNotBlank(bodyPart.getFileName())) {
					if(msgId==Long.MIN_VALUE){
						fileName = downloadPath +"/"+ bodyPart.getFileName();
					}else {
						fileName = downloadPath +"/"+msgId+"_"+bodyPart.getFileName();
					}

					File file = new File(fileName);
					if (file.exists()) {
						file.delete();
					}

					logger.info("Attachment: " + fileName);

					InputStream stream = bodyPart.getInputStream();
					FileOutputStream outputStream = new FileOutputStream(new File(fileName));

					byte[] buffer = new byte[4096];
					int bytesRead;

					while ((bytesRead = stream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, bytesRead);
					}

					outputStream.close();
					stream.close();

					outputStream = null;
					stream = null;

					break;
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return fileName;
	}
	
	/**
	 * Method to send a Mail.
	 * 
	 * @param to
	 * @param subject
	 * @param emailContent
	 * @param attachmentName
	 * @param attachment
	 * @throws Exception
	 */
	public void sendMail(String[] to, String subject, String emailContent, String attachmentName, byte[] attachment) throws Exception {
		logger.debug(Literal.ENTERING);

		Transport transport = null;
		Session session = null;
		try {
			if (getOutgoingSession() == null) {
				createOutgoingSession();
			}
			session = getOutgoingSession();

			MimeMessage message = new MimeMessage(session);
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(new InternetAddress(getOutgoingUserName()));
			helper.setSubject(subject);
			helper.setText(emailContent, true);
			helper.setTo(to);
			helper.setSentDate(DateUtility.getSysDate());
			if (attachment != null) {
				helper.addAttachment(attachmentName, new ByteArrayResource(attachment));
			}
			transport = session.getTransport("smtp");
			transport.connect();
			transport.sendMessage(message, message.getAllRecipients());
		} catch (NoSuchProviderException e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Provider for the given protocol is not found. Mail sending failed...!");
		} catch (MessagingException e) {
			logger.error(Literal.EXCEPTION, e);
			throw new Exception("Mail sending failed...");
		} finally {
			if (null != transport && transport.isConnected()) {
				transport.close();
			}
			if (null != session) {
				session = null;
			}
		}
		logger.debug(Literal.LEAVING);
	}
	/**
	 * Method to send SMS
	 * @param mailTemplate
	 * @throws Exception
	 */
	public void sendSMS(MailTemplate mailTemplate) throws Exception {
		// To be implemented....
	}
		
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getOutgoingHost() {
		return outgoingHost;
	}

	public void setOutgoingHost(String outgoingHost) {
		this.outgoingHost = outgoingHost;
	}

	public String getOutgoingPort() {
		return outgoingPort;
	}

	public void setOutgoingPort(String outgoingPort) {
		this.outgoingPort = outgoingPort;
	}

	public boolean isOutgoingAuth() {
		return outgoingAuth;
	}

	public void setOutgoingAuth(boolean outgoingAuth) {
		this.outgoingAuth = outgoingAuth;
	}

	public boolean isOutgoingDebug() {
		return outgoingDebug;
	}

	public void setOutgoingDebug(boolean outgoingDebug) {
		this.outgoingDebug = outgoingDebug;
	}

	public String getOutgoingUserName() {
		return outgoingUserName;
	}

	public void setOutgoingUserName(String outgoingUserName) {
		this.outgoingUserName = outgoingUserName;
	}

	public String getOutgoingPassword() {
		return outgoingPassword;
	}

	public void setOutgoingPassword(String outgoingPassword) {
		this.outgoingPassword = outgoingPassword;
	}

	public String getOutgoingEncType() {
		return outgoingEncType;
	}

	public void setOutgoingEncType(String outgoingEncType) {
		this.outgoingEncType = outgoingEncType;
	}
	
	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public Session getOutgoingSession() {
		return outgoingSession;
	}

	public void setOutgoingSession(Session outgoingSession) {
		this.outgoingSession = outgoingSession;
	}

	public String getIncomingHost() {
		return incomingHost;
	}

	public void setIncomingHost(String incomingHost) {
		this.incomingHost = incomingHost;
	}

	public int getIncomingPort() {
		return incomingPort;
	}

	public void setIncomingPort(int incomingPort) {
		this.incomingPort = incomingPort;
	}

	public String getIncomingUserName() {
		return incomingUserName;
	}

	public void setIncomingUserName(String incomingUserName) {
		this.incomingUserName = incomingUserName;
	}

	public String getIncomingPassword() {
		return incomingPassword;
	}

	public void setIncomingPassword(String incomingPassword) {
		this.incomingPassword = incomingPassword;
	}

	public String getIncomingEncType() {
		return incomingEncType;
	}

	public void setIncomingEncType(String incomingEncType) {
		this.incomingEncType = incomingEncType;
	}

	public Session getIncomingSession() {
		return incomingSession;
	}

	public void setIncomingSession(Session incomingSession) {
		this.incomingSession = incomingSession;
	}

	public boolean isIncomingAuth() {
		return incomingAuth;
	}

	public void setIncomingAuth(boolean incomingAuth) {
		this.incomingAuth = incomingAuth;
	}

	public String getIncomingFolder() {
	    return incomingFolder;
    }

	public void setIncomingFolder(String incomingFolder) {
	    this.incomingFolder = incomingFolder;
    }

	public String getIncomingProtocol() {
	    return incomingProtocol;
    }

	public void setIncomingProtocol(String incomingProtocol) {
	    this.incomingProtocol = incomingProtocol;
    }
}
