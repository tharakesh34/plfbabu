package com.pennant.equation.util;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;

public class HostConnection {
	
	private static Logger logger = Logger.getLogger(HostConnection.class);

	private AS400 as400 = null;

	/**
	 * Method for return Host Connection Object
	 * @return
	 */
	public HostConnection() {
		super();
	}

	/**
	 * Method for Establishing connection for HOST
	 */
	@SuppressWarnings("unused")
	public AS400 getConnection() {
		logger.debug("Entering");

		boolean returnFlag = false;
		AS400Message[] messages = null;
		ArrayList<String> arrParameters = null;
		String 		messageId	= null;
		String 		messageDesc	= null;
		Object object = null;

		if(as400==null || !as400.isConnected()){

			try {
				arrParameters = getParameters();

				// New AS400 Object
				if (arrParameters.size() > 0) {
					as400 = new AS400(String.valueOf(arrParameters.get(0)).trim(),
							String.valueOf(arrParameters.get(1)).trim(), String
							.valueOf(arrParameters.get(2)).trim());
				}

				// Add the Equation System Library to the Unit
				CommandCall commandCall = new CommandCall(as400);
				commandCall.run("ADDLIBLE LIB(KAPBASELIB)");

				// KAPUNLIBL
				commandCall.run("KAPUNLIBL UNIT("+ String.valueOf(arrParameters.get(3)).trim() + ") LIBLTYPE(*DFT)");

				// Construct ProgramCallDocument UTM83C Equation Environment
				String strutm83c = new String("utm83c");
				ProgramCallDocument programCallDocument = null;
				programCallDocument = new ProgramCallDocument(as400, strutm83c);

				// Set input parameters.
				programCallDocument.setValue("utm83c.returnCode", " ");

				// Request to call the API
				returnFlag = programCallDocument.callProgram("utm83c");

				// If return code is false, we received messages from the server
				if (!returnFlag) {
					messages = programCallDocument.getMessageList("utm83c");
					if (messages != null) {
						for (int m = 0; m < messages.length; m++) {
							messageId = messages[m].getID();
							messageDesc = messages[m].getText();
							throw new Exception("utm38c Error");
						}
					}
				} else {
					object = programCallDocument.getValue("utm83c.returnCode");
				}

				logger.debug("AFTER PROGRAM CALLL DOCUMENT utm83c");
				commandCall.run("ADDLIBLE LIB(PFFLIB)");

			} catch (AS400SecurityException e) {
				e.printStackTrace();
			} catch (ErrorCompletingRequestException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}catch (PcmlException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		logger.debug("Leaving");
		return this.as400;

	}

	/**
	 * Method for grt the connection parameters
	 * @return
	 */
	public ArrayList<String> getParameters() {
		ArrayList<String> arrParameters = new ArrayList<String>();
		arrParameters.add("192.168.1.5");
		arrParameters.add("S006");
		arrParameters.add("S006");
		arrParameters.add("SAU");
		return arrParameters;
	}

	/**
	 * Method for Calling Interface program
	 * @param programCallDocument
	 * @param api
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public void callAPI(ProgramCallDocument programCallDocument, String api)
			throws Exception {
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
	@SuppressWarnings("static-access")
	public void disConnection() {
		logger.debug("Entering");
		if(this.as400 != null){
			this.as400.disconnectService(as400.COMMAND);
		}
		logger.debug("Leaving");
	}
}
