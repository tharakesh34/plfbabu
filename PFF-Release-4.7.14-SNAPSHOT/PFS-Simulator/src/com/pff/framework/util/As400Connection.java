package com.pff.framework.util;

import java.sql.Connection;

import javax.sql.PooledConnection;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400JDBCConnectionPoolDataSource;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.CharacterDataArea;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.QSYSObjectPathName;
import com.pff.framework.logging.Log;
import com.pff.framework.logging.LogFactory;

public class As400Connection {

	private static Log LOG = LogFactory.getLog(As400Connection.class);
	private AS400 as400 = null;

	public AS400 getAS400() throws Exception {
		LOG.entering("getAS400()");

		if (as400 == null) {
			try {

				as400 = new AS400(ServiceProperties.getAS400ServerIP());
				setLibrary(as400);

			} catch (Exception e) {
				LOG.info("getAS400() AS400 CONNECTED :False");
				LOG.info("getAS400()-->Exception");

				LOG.error(e.getMessage(), e);
				throw new Exception(e.getMessage());

			}

			try {
				LOG.info("getAS400() AS400 CONNECTED :" + as400.isConnected());
				as400.connectService(AS400.COMMAND);
				LOG.info("getAS400() AS400 CONNECTED :" + as400.isConnected());
			} catch (Exception e) {
				LOG.info("getAS400() AS400 CONNECTED :False");
				LOG.info("getAS400()-->Exception");

				LOG.error(e.getMessage(), e);
				throw new Exception(e.getMessage());
			}

		}


		LOG.exiting("getAS400()", as400);
		return as400;
	}

	public void setLibrary(AS400 as400) throws Exception {
		LOG.entering("setLibrary()");
		boolean isCommandCompleted;
		LOG.info("Before Application Library Addition:"
				+ ServiceProperties.getAS400AppLibrary());

		try {
			CommandCall commandCall = new CommandCall(as400);
			isCommandCompleted = commandCall.run("ADDLIBLE LIB("+ ServiceProperties.getAS400AppLibrary() + ")");
			if (!isCommandCompleted) {
				AS400Message[] messages = commandCall.getMessageList();
				if (messages != null) {
					for (int m = 0; m < messages.length; m++) {String messageId = messages[m].getID();
						if (!"CPF2103".equals(messageId)) {
							String messageDesc = messages[m].getText();
							byte[] messageDetails = messages[m].getSubstitutionData();
							LOG.info("Could not add library :" + m + ":"+ messageId + messageDesc);
							LOG.info("messageDetails :"+ new String(messageDetails));
							throw new Exception("Could not add library");
						}
					}
				}

				LOG.info("After Application Library Addition:"+ ServiceProperties.getAS400AppLibrary() + " "+ isCommandCompleted);
			}

		} catch (Exception e) {
			LOG.info("setLibrary()-->Exception");
			LOG.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
		LOG.exiting("setLibrary()");
	}

	public Connection getAS400Connection(String password) throws Exception {
		LOG.entering("getAS400Connection()");

		Connection connection = null;
		AS400JDBCConnectionPoolDataSource dataSource  = new com.ibm.as400.access.AS400JDBCConnectionPoolDataSource(ServiceProperties.getAS400ServerIP());
		dataSource.setUser(ServiceProperties.getAS400UserId());
		dataSource.setPassword(decryptPropertyValue(password));
		dataSource.setLibraries(ServiceProperties.getAS400AppLibrary());
		PooledConnection pooledConnection = (com.ibm.as400.access.AS400JDBCPooledConnection) dataSource.getPooledConnection();
		connection = pooledConnection.getConnection();

		LOG.exiting("getAS400Connection()");
		return connection;
	}

	public String getDataArea(String dataAreaName) throws Exception {
		LOG.entering("getDataArea()");

		try {
			getAS400();
			QSYSObjectPathName path = new QSYSObjectPathName(ServiceProperties.getAS400AppLibrary(), dataAreaName, "DTAARA");
			CharacterDataArea dataArea = new CharacterDataArea(as400, path.getPath());
			LOG.info(dataAreaName.trim() + " fetch complete");
			LOG.exiting("getDataArea()");
			return dataArea.read();

		} catch (Exception e) {
			LOG.info("getDataArea()-->Exception");
			LOG.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
	}

	public void setDataArea(String dataAreaName, String dataAreaValue) throws Exception {
		LOG.entering("setDataArea()");

		try {
			getAS400();
			QSYSObjectPathName path = new QSYSObjectPathName(ServiceProperties.getAS400AppLibrary(), dataAreaName, "DTAARA");
			CharacterDataArea dataArea = new CharacterDataArea(as400, path.getPath());
			LOG.info(dataAreaName.trim() + " fetch complete");
			LOG.exiting("setDataArea()");
			dataArea.write(dataAreaValue);
		} catch (Exception e) {
			LOG.info("setDataArea()-->Exception");
			LOG.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
	}

	public static String encryptPropertyValue(String actualValue) {
		LOG.entering("encryptPropertyValue()");
		if(ServiceProperties.getAS400PassWordEnc() ){
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword(ServiceProperties.getAS400PassWordCode()); 
			String encryptedPassword = encryptor.encrypt(actualValue);
			LOG.exiting("encryptPropertyValue()");
			return encryptedPassword;
		}
		LOG.exiting("encryptPropertyValue()");
		return actualValue;
	}

	public static String decryptPropertyValue(String encryptedValue) {
		LOG.entering("decryptPropertyValue()");
		if(ServiceProperties.getAS400PassWordEnc() ){
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword(ServiceProperties.getAS400PassWordCode()); 
			String decryptedPropertyValue = encryptor.decrypt(encryptedValue);
			LOG.exiting("decryptPropertyValue()");
			return decryptedPropertyValue;
		}
		LOG.exiting("decryptPropertyValue()");
		return encryptedValue;
	}


	// disconnect AS 400 Services
	public void disConnectAS400() throws Exception {
		LOG.entering("disConnectAS400()");
		try {
			as400.disconnectAllServices();
		} catch (Exception e) {
			LOG.debug("disConnectAS400()-->Exception");
			LOG.error(e.getMessage(), e);
		}
		LOG.exiting("disConnectAS400()");
	}
}
