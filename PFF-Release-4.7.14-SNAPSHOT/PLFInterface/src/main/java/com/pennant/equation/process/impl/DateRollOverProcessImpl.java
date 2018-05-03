package com.pennant.equation.process.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ConnectionPoolException;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.coreinterface.process.DateRollOverProcess;
import com.pennant.equation.util.HostConnection;
import com.pennanttech.pennapps.core.InterfaceException;

public class DateRollOverProcessImpl implements DateRollOverProcess{
	
	private static Logger logger = Logger.getLogger(DateRollOverProcessImpl.class);
	
	private HostConnection hostConnection;
	
	public DateRollOverProcessImpl() {
		super();
	}
	
	/**
	 * Method for Changing Calendar Days from CoreBanking
	 * @return
	 * @throws EquationInterfaceException 
	 */
	@Override
	public Map<String, String> getCalendarWorkingDays() throws InterfaceException{
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFGBD";//get List of Calendar Working Days
		
		Map<String, String> calendarDaysMap = null ;
		int[] indices = new int[1]; // Indices for access array value
		int dsRspCount;              // Number of records returned 
				
		try {
			
			as400 = this.hostConnection.getConnection();
			
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			
			pcmlDoc.setValue(pcml + ".@REQDTA.Calendar", "C"); 	// Calendar Status
			pcmlDoc.setValue(pcml + ".@RSPDTA.@NORES", 0); 		
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
			pcmlDoc.setValue(pcml + ".@ERPRM", ""); 	

			logger.debug(" Before PCML Call");
			getHostConnection().callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");
			
			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {			
				
				dsRspCount=Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.@NORES").toString());
				
				calendarDaysMap = new HashMap<String, String>();
				for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){

					calendarDaysMap.put(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.Year",indices).toString(),
							pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.Days",indices).toString()); 	
				}
			}else{
				logger.info("Account Details Not found");				
				throw new InterfaceException("9999",pcmlDoc.getValue(pcml + ".@ERPRM").toString());
			}
			
		} catch (ConnectionPoolException e){
			logger.error("Exception: ", e);
			throw new InterfaceException("9999","Host Connection Failed.. Please contact administrator ");
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		} finally{
				this.hostConnection.closeConnection(as400);
		}
		
		logger.debug("Leaving");
		return calendarDaysMap;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setHostConnection(HostConnection hostConnection) {
		this.hostConnection = hostConnection;
	}
	public HostConnection getHostConnection() {
		return hostConnection;
	}

}
