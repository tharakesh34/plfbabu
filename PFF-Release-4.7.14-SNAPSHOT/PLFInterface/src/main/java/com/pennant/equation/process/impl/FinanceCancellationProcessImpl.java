package com.pennant.equation.process.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ConnectionPoolException;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.coreinterface.model.FinanceCancellation;
import com.pennant.coreinterface.process.FinanceCancellationProcess;
import com.pennant.equation.util.GenericProcess;
import com.pennant.equation.util.HostConnection;
import com.pennanttech.pennapps.core.InterfaceException;

public class FinanceCancellationProcessImpl extends GenericProcess implements FinanceCancellationProcess{

	private static Logger	logger	= Logger.getLogger(FinanceCancellationProcessImpl.class);

	private HostConnection	hostConnection;

	public FinanceCancellationProcessImpl() {
		super();
	}
	
	/**
	 * 
	 *  <br> IN FinanceCancellationProcess.java
	 * @param transid
	 * @param postDate
	 * @return List<FinanceCancellation> 
	 * @throws Exception  
	 */
	@Override
	public List<FinanceCancellation> fetchCancelledFinancePostings(String finReference, String linkedTranId) throws InterfaceException{
		logger.debug("Entering");

		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFCPR"; //PCML File name

		int[] indices = new int[1]; // Indices for access array value
		int itemCount; // Number of records returned 
		List<FinanceCancellation> list = new ArrayList<FinanceCancellation>();
		FinanceCancellation item = null;

		try {
			
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			pcmlDoc.setValue(pcml + ".@REQDTA.dsReqFinRef", finReference); // Finance reference
			pcmlDoc.setValue(pcml + ".@REQDTA.dsReqLnkTID", linkedTranId); // Linked Trans Id
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000");
			pcmlDoc.setValue(pcml + ".@ERPRM", "");
			
			logger.debug(" Before PCML Call");
			getHostConnection().callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");
			
			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {
				String dsRspCount=pcmlDoc.getValue(pcml + ".@RSPDTA.dsRspCount").toString();
				String dsRspFinRef=pcmlDoc.getValue(pcml + ".@RSPDTA.dsRspFinRef", indices).toString();
				String dsRspErr=pcmlDoc.getValue(pcml + ".@RSPDTA.dsRspErr", indices).toString();
				String dsRspErrD=pcmlDoc.getValue(pcml + ".@RSPDTA.dsRspErrD", indices).toString();
				itemCount = Integer.parseInt(dsRspCount);
				for (indices[0] = 0; indices[0] < itemCount; indices[0]++) {
					item = new FinanceCancellation();
					item.setDsRspCount(dsRspCount);
					item.setDsRspFinRef(dsRspFinRef);
					item.setDsRspErr(dsRspErr);
					item.setDsRspErrD(dsRspErrD);
					item.setDsRspFinEvent(pcmlDoc.getValue(pcml + ".@RSPDTA.DSArrRsp.dsRspFinEvent", indices).toString());
					item.setDsRspLnkTID(pcmlDoc.getValue(pcml + ".@RSPDTA.DSArrRsp.dsRspLnkTID", indices).toString());
					item.setDsRspPOD(pcmlDoc.getValue(pcml + ".@RSPDTA.DSArrRsp.dsRspPOD", indices).toString());
					item.setDsRspAB(pcmlDoc.getValue(pcml + ".@RSPDTA.DSArrRsp.dsRspAB", indices).toString());
					item.setDsRspAN(pcmlDoc.getValue(pcml + ".@RSPDTA.DSArrRsp.dsRspAN", indices).toString());
					item.setDsRspAS(pcmlDoc.getValue(pcml + ".@RSPDTA.DSArrRsp.dsRspAS", indices).toString());
					item.setDsRspPostRef(pcmlDoc.getValue(pcml + ".@RSPDTA.DSArrRsp.dsRspPostRef", indices).toString());
					item.setDsRspStatus(pcmlDoc.getValue(pcml + ".@RSPDTA.DSArrRsp.dsRspStatus", indices).toString());
					item.setDsRspOrder(pcmlDoc.getValue(pcml + ".@RSPDTA.DSArrRsp.dsRspOrder", indices).toString());
					list.add(item);
				}
			} else {
				throw new InterfaceException("9999",pcmlDoc.getValue(pcml + ".@ERCOD").toString() + " : "+pcmlDoc.getValue(pcml + ".@ERPRM").toString());
			} 
		}catch (ConnectionPoolException e){
			logger.error("Exception: ", e);
			throw new InterfaceException("9999","Host Connection Failed.. Please contact administrator ");
		}  catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		} finally {
			this.hostConnection.closeConnection(as400);
		}

		logger.debug("Leaving");
		return list;
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
