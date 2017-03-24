package com.pennant.equation.process;

import java.math.BigDecimal;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ConnectionPoolException;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.equation.util.GenericProcess;
import com.pennant.equation.util.HostConnection;

public class ManagerChequeProcess extends GenericProcess{

	private static Logger logger = Logger.getLogger(ManagerChequeProcess.class);

	private HostConnection hostConnection;

	/**
	 * Method for validate the Cheque Number
	 * @param accountNum
	 * @param chequeNum
	 * @throws AccountNotFoundException
	 */
	public void validateChequeNumber(String accountNum, String chequeNum) throws AccountNotFoundException {
		logger.debug("Entering");

		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTCHQVAL";// Validate the Cheque number

		try {

			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			pcmlDoc.setValue(pcml + ".@REQDTA.dsReqChqAcc", accountNum);// Nostro Account Number
			pcmlDoc.setValue(pcml + ".@REQDTA.dsReqChqNo", chequeNum);// Cheque Number

			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
			pcmlDoc.setValue(pcml + ".@ERPRM", ""); 	

			logger.debug(" Before PCML Call");
			getHostConnection().callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");

			if (!("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString()))) { 

				logger.info(pcmlDoc.getValue(pcml + ".@ERPRM").toString());				
				throw new AccountNotFoundException(pcmlDoc.getValue(pcml + ".@ERPRM").toString());
			}

		} catch (ConnectionPoolException e){
			logger.error("Exception: ", e);
			throw new AccountNotFoundException("Host Connection Failed.. Please contact administrator ");
		}catch (AccountNotFoundException e) {
			logger.error("Exception: ", e);
			//throw new AccountNotFoundException(e.getErrorMsg());AIB
			throw new AccountNotFoundException(e.getMessage());
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw new AccountNotFoundException(e.getMessage());
		} finally {			
			this.hostConnection.closeConnection(as400);
		}
		logger.debug("Leaving");
	}
	/**
	 * @param accountNum
	 * @param chequeNo
	 * @param chqAmount
	 * @param draftCcy
	 * @return
	 * @throws AccountNotFoundException
	 */
	public String addStopOrder(String accountNum, String chequeNo, BigDecimal chqAmount, String draftCcy) throws AccountNotFoundException {
		logger.debug("Entering");

		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFAST";// Add Stop Order Reference
		String stopOrderRef = "";

		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			pcmlDoc.setValue(pcml + ".@REQDTA.dsReqAccNum", accountNum);// Account Number
			pcmlDoc.setValue(pcml + ".@REQDTA.dsReqchqNo", chequeNo);// Cheque Number
			pcmlDoc.setValue(pcml + ".@REQDTA.dsReqAMT", chqAmount);// Cheque Amount
			pcmlDoc.setValue(pcml + ".@REQDTA.dsReqStCcy", draftCcy);// Currency

			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
			pcmlDoc.setValue(pcml + ".@ERPRM", ""); 	

			logger.debug(" Before PCML Call");
			getHostConnection().callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");

			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) { 
				stopOrderRef = pcmlDoc.getValue(pcml + ".@RSPDTA.dsRspStopRef").toString();
			}else{
				logger.info(pcmlDoc.getValue(pcml + ".@ERPRM").toString());				
				throw new AccountNotFoundException(pcmlDoc.getValue(pcml + ".@ERPRM").toString());
			}

		} catch (ConnectionPoolException e){
			logger.error("Exception: ", e);
			throw new AccountNotFoundException("Host Connection Failed.. Please contact administrator ");
		}catch (AccountNotFoundException e) {
			logger.error("Exception: ", e);
			//throw new AccountNotFoundException(e.getErrorMsg());AIB
			throw new AccountNotFoundException(e.getMessage());
		}catch (Exception e) {

			logger.error("Exception: ", e);
			throw new AccountNotFoundException(e.getMessage());
		} finally {			
			this.hostConnection.closeConnection(as400);
		}
		logger.debug("Leaving");
		return stopOrderRef;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setHostConnection(HostConnection hostConnection) {
		this.hostConnection = hostConnection;
	}
	public HostConnection getHostConnection() {
		return hostConnection;
	}
}
