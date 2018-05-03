package com.pennant.equation.process.impl;

import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.coreinterface.model.HostEnquiry;
import com.pennant.coreinterface.process.HostStatusEnquiryProcess;
import com.pennant.equation.util.HostConnection;
import com.pennanttech.pennapps.core.InterfaceException;

public class HostStatusEnquiryProcessImpl implements HostStatusEnquiryProcess {

	private static Logger logger = Logger.getLogger(HostStatusEnquiryProcessImpl.class);
	private HostConnection hostConnection;

	public HostStatusEnquiryProcessImpl() {
		super();
	}
	
	/*
	 * Method For getting AS400 Connection Status
	 */
	@Override
	public HostEnquiry getHostStatus() throws InterfaceException {

		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTHPS01R";

		HostEnquiry hostEnquiry = new HostEnquiry();

		try {
			
			as400 = hostConnection.getConnection();
			CommandCall commandCall = new CommandCall(as400);
			commandCall.run("ADDLIBLE LIB(PFFLIB)");

			// Request Data
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			pcmlDoc.setValue(pcml + ".@REQDTA.UnitName", "MGR");
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
			pcmlDoc.setValue(pcml + ".@ERPRM", ""); 	
			
			logger.debug(" Before PCML Call");
			getHostConnection().callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");
			
			// Response Data
			hostEnquiry.setUnitName((String) pcmlDoc.getValue(pcml+".@RSPDTA.DSRSPUNITNAME"));
		    hostEnquiry.setStatusCode((String) pcmlDoc.getValue(pcml+".@RSPDTA.DSRSPSTSCOD"));
			hostEnquiry.setStatusDesc((String) pcmlDoc.getValue(pcml+".@RSPDTA.DSRSPSTSDEC"));
			hostEnquiry.setNextBusDate(String.valueOf(pcmlDoc.getValue(pcml+".@RSPDTA.DSRSPNXTBUSDTE")));
			hostEnquiry.setPrevBusDate(String.valueOf(pcmlDoc.getValue(pcml+".@RSPDTA.DSRSPPRVBUSDTE")));
			hostEnquiry.setCurBusDate(String.valueOf(pcmlDoc.getValue(pcml+".@RSPDTA.DSRSPCURBUSDTE")));
			
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999","Host Connection Failed.. Please contact administrator ");
		} finally {
			this.hostConnection.closeConnection(as400);
		}

		return hostEnquiry;
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
