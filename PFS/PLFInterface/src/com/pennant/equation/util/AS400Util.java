/*
 * Created on Oct 29, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.pennant.equation.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.data.ProgramCallDocument;

/**
 * @author S039
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AS400Util {
	private static Logger logger = Logger.getLogger(AS400Util.class);
	
	private static AS400Util as400util = new AS400Util();
	private static  AS400 as400 = null;
	private static String unit = null;
	
	private AS400Util(){}
	
	public static AS400Util getAs400Util(){		
		return as400util;
	}
	
	public AS400 getAs400() {
		logger.debug("Entering");
		
		 String messageId = null;
		 String messageDesc	= null;
		 Object object = null;
		boolean returnFlag=false;
		AS400Message[] messages = null;
		ArrayList arrParameters = null;
		
		try{

			arrParameters = getParameters();
        	
        	// New AS400 Object
        	if(arrParameters.size() > 0) {
		        as400 = new AS400(String.valueOf(arrParameters.get(0)).trim(),String.valueOf(arrParameters.get(1)).trim(),String.valueOf(arrParameters.get(2)).trim());
		        System.out.println("After Parameters ");
		        //unit   =  unit.trim().substring(unit.length()-3,unit.length());
        	}

			//Add the Equation System Library to the Unit
			CommandCall commandCall = new CommandCall(as400);
			commandCall.run("ADDLIBLE LIB(KAPBASELIB)");
			// KAPUNLIBL
			
			commandCall.run("KAPUNLIBL UNIT("+String.valueOf(arrParameters.get(3)).trim() +") LIBLTYPE(*DFT)");

			// Construct ProgramCallDocument UTM83C Equation Environment
			String strutm83c = new String("utm83c");
			ProgramCallDocument programCallDocument = new ProgramCallDocument(as400, strutm83c);

			// Set input parameters. 
			programCallDocument.setValue("utm83c.returnCode", " ");

			// Request to call the API
			returnFlag = programCallDocument.callProgram("utm83c");
			
//			 If return code is false, we received messages from the server
			if(!returnFlag) {				
				messages = programCallDocument.getMessageList("utm83c");
				if(messages != null) {				
					for (int m = 0; m < messages.length; m++) {
						messageId = messages[m].getID();
						messageDesc = messages[m].getText();
						throw  new Exception("utm38c Error");
					}
				}
			} else {
				object = programCallDocument.getValue("utm83c.returnCode");
			}
			System.out.println("AFTER PROGRAM CALLL DOCUMENT utm83c");
			
			commandCall.run("ADDLIBLE LIB(PFFLIB)");
			

		}catch (Exception e) {
		e.printStackTrace();
		}
		
		logger.debug("Leaving");
		return as400;
	}
	
	
	public void callAPI(ProgramCallDocument programCallDocument, String api) throws Exception {		
		logger.debug("Entering");
		
		boolean 	returnFlag	= false;		
		String 		messageId	= null;
		String 		messageDesc	= null;
		AS400Message[] messages = null;				
		returnFlag = programCallDocument.callProgram(api);
		
		if(!returnFlag) {
			messages = programCallDocument.getMessageList(api);				
			if(messages != null) {				
				for (int m = 0; m < messages.length; m++) {
					messageId = messages[m].getID();
					messageDesc = messages[m].getText();
					throw new Exception("Could not call" +api);
				}
			}				
		}
		logger.debug("Leaving");
	}
	

	public ArrayList getParameters() {

		Connection conEquation = null;
		Statement smtEquation = null;
		ResultSet rsEquation = null; 	
		String strQuery = null;
		ArrayList arrParameters = new ArrayList<String>();
		PreparedStatement psmtEquation = null;
		
		arrParameters.add("192.168.1.5");
		arrParameters.add("S006");
		arrParameters.add("S006");
		arrParameters.add("SAU");
		
	/*Please remove the below comment and comment the above code  */ 
		
/*		try {
			arrParameters = new ArrayList();
			conEquation = Util.getConnection("jdbc/WASSECDSN");
	 		strQuery = "SELECT SERVERIP,USERID,PASSWORD,UNIT FROM SECPCML";

			// Commented by K.Ramesh Babu on 09/11/2008 for AS400 Locking
			//smtEquation = conEquation.createStatement();
			//rsEquation = smtEquation.executeQuery(strQuery);
			// Added by K.Ramesh Babu on 09/11/2008 for AS400 Locking
	 		psmtEquation = conEquation.prepareStatement(strQuery);
	 		rsEquation = psmtEquation.executeQuery();
			while(rsEquation.next()) {
				arrParameters.add(rsEquation.getString("SERVERIP"));
				arrParameters.add(rsEquation.getString("USERID"));
				arrParameters.add(rsEquation.getString("PASSWORD")!=null?new EncryptSrv().decrypt(rsEquation.getString("PASSWORD").trim()):null);
				arrParameters.add(rsEquation.getString("UNIT"));
			}
			System.out.println(String.valueOf(arrParameters.get(0)).trim()+","+String.valueOf(arrParameters.get(1)).trim()+","+String.valueOf(arrParameters.get(2)).trim()+","+String.valueOf(arrParameters.get(3)).trim());
			System.out.println("Inside getParameters 3 ");
		} catch(Exception objException) {
			new KastleException(objException);
		} finally {
			Util.closeResultSet(rsEquation);
			Util.closeStatement(psmtEquation);
			Util.closeDBConnection(conEquation,true);
		}*/
		
		 
		return arrParameters;
	}

	public static ProgramCallDocument getPCMLDoc(AS400 as400 ,String pcml) throws Exception{
		logger.debug("Entering");
		ProgramCallDocument pcmlDoc = null;
		
				
		try {
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			pcmlDoc.setValue(pcml + ".@ERCOD", " "); 
			pcmlDoc.setValue(pcml + ".@ERPRM", " ");
			pcmlDoc.setValue(pcml + ".@BEGIN", "Y"); 		// Equation Program Begin
			pcmlDoc.setValue(pcml + ".DSAIR", " "); 		// Equation Program Begin
			pcmlDoc.setValue(pcml + ".@NOREQ", 0); 			// Equation Program Begin
			pcmlDoc.setValue(pcml + ".@NORET", 0); 			// Equation Program Begin
			pcmlDoc.setValue(pcml + ".@ENQLN", 0); 			// Equation Program Begin
			pcmlDoc.setValue(pcml + ".@FLEN", 0); 			// Equation Program Begin
			pcmlDoc.setValue(pcml + ".@RLEN", 0); 			// Equation Program Begin
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Entering");
		return pcmlDoc;
	}
	
}