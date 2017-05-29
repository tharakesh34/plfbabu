package com.pennant.equation.util;

import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400ConnectionPool;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.data.ProgramCallDocument;

public class HostConnection {
	private static Logger logger = Logger.getLogger(HostConnection.class);
	private static AS400ConnectionPool connectionPool = new AS400ConnectionPool();
	
	
	private String serverName;
	private String unitName;
	private String userName;
	private String password;

	/**
	 * Method for return Host Connection Object
	 * 
	 * @return
	 */
	public HostConnection() {
		connectionPool.setMaxConnections(10);
		connectionPool.setCleanupInterval(10*60*1000);
		connectionPool.setMaxInactivity(60*1000);
		connectionPool.setMaxLifetime(10*60*60*1000);
	}

	/**
	 * Method for Establishing connection for HOST
	 * @throws Exception 
	 */
	@SuppressWarnings("unused")
	public AS400 getConnection() throws Exception {
		logger.debug("Entering");
		boolean returnFlag = false;
		AS400Message[] messages = null;
		Object object = null;
		AS400 as400=null;
		boolean newConnection = false;
		try {
			int i = 0;
			while (i <= 100){
				// New AS400 Object
				as400 = connectionPool.getConnection(serverName, userName, password);
				
				if(as400 != null){
 					break;
				}
			}

			//CommandCall commandCall = new CommandCall(as400);
			//commandCall.run("ADDLIBLE LIB(PFFLIB)");
			//if(newConnection){
			String pcml = "SETPFFENV";
			ProgramCallDocument programCallDocument = new ProgramCallDocument(as400, pcml);
			programCallDocument.setValue("SETPFFENV.returnCode", getUnitName());
			//	programCallDocument.serialize();
			callAPI(programCallDocument, pcml);
			//}
			logger.debug("AFTER PROGRAM CALLL DOCUMENT utm83c");
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;

		}finally{
			if(as400 != null) { 
				closeConnection(as400);
			}
		}
		logger.debug("Leaving");
		return as400;
	}

	/**
	 * Method for Calling Interface program
	 * 
	 * @param programCallDocument
	 * @param api
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public void callAPI(ProgramCallDocument programCallDocument, String api) throws Exception {
		logger.debug("Entering");
		boolean returnFlag = false;
		String messageId = null;
		String messageDesc = null;
		AS400Message[] messages = null;
		returnFlag = programCallDocument.callProgram(api);
		if (!returnFlag) {
			messages = programCallDocument.getMessageList(api);
			if (messages != null) {
				for (int m = 0; m < messages.length; m++) {
					messageId = messages[m].getID();
					messageDesc = messages[m].getText();
					throw new Exception("Could not call" + api);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Dis-Connect a connection
	 */
	public void closeConnection(AS400 as400) {
		logger.debug("Entering");
		if(as400 != null){
			connectionPool.returnConnectionToPool(as400);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Dis-Connect a connection
	 */
	public void closeAllConnection() {
		logger.debug("Entering");
		connectionPool.close();
		logger.debug("Leaving");
	}

	
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	

	
}
