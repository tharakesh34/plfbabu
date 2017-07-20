package com.pennant.equation.process.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ConnectionPoolException;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.coreinterface.model.AccountBalance;
import com.pennant.coreinterface.model.account.InterfaceAccount;
import com.pennant.coreinterface.model.collateral.CollateralMark;
import com.pennant.coreinterface.process.AccountDataProcess;
import com.pennant.equation.util.GenericProcess;
import com.pennant.equation.util.HostConnection;
import com.pennanttech.pennapps.core.InterfaceException;

public class AccountDataProcessImpl extends GenericProcess implements AccountDataProcess{
	
	private static Logger logger = Logger.getLogger(AccountDataProcessImpl.class);

	private HostConnection hostConnection;

	public AccountDataProcessImpl() {
		super();
	}
	
	/**
	 * Method for Removing Holds on Accounts Before Repayment Process Execution
	 * @return
	 * @throws EquationInterfaceException 
	 */
	@Override
	public int removeAccountHolds() throws InterfaceException {
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFRAH";//Remove Account Holds
		int dsRspCount = 0;

		try {

			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			pcmlDoc.setValue(pcml + ".@REQDTA.REQUEST", "Y");
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
			pcmlDoc.setValue(pcml + ".@ERPRM", ""); 	

			logger.debug(" Before PCML Call");
			getHostConnection().callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");

			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {			
				dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.RSPCOUNT").toString());  
			}else{
				logger.info(pcmlDoc.getValue(pcml + ".@ERPRM").toString());				
				throw new InterfaceException("9999",pcmlDoc.getValue(pcml + ".@ERPRM").toString());
			}

		} catch (ConnectionPoolException e){
			logger.error("Exception: ", e);
			throw new InterfaceException("9999","Host Connection Failed.. Please contact administrator ");
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		} finally {			
			this.hostConnection.closeConnection(as400);
		}
		logger.debug("Leaving");
		return dsRspCount;

	}
	
	/**
	 * Method for Processing Holding on Repay Account Details
	 * @param accountsList
	 * @return
	 * @throws EquationInterfaceException 
	 */
	@Override
	public List<AccountBalance> addAccountHolds(List<AccountBalance> accountsList, String holdType) throws InterfaceException {
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFAAH";//Add Account Holds
		int[] indices = new int[1];
		AccountBalance acBal = null;
		int acListSize = accountsList.size();

		try {

			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			pcmlDoc.setValue(pcml + ".@REQDTA.@NOREQ", acListSize);// Number of Requests
			pcmlDoc.setValue(pcml + ".@REQDTA.HOLDTYPE", holdType);// Hold Type
			for (indices[0] = 0; indices[0] < accountsList.size(); indices[0]++){
				acBal = accountsList.get(indices[0]);
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.RepayAccount",indices, acBal.getRepayAccount());// Account Number
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CurODAmount",indices, acBal.getAccBalance());// Account Balance
			}
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
			pcmlDoc.setValue(pcml + ".@ERPRM", ""); 	

			logger.debug(" Before PCML Call");
			getHostConnection().callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");

			logger.info(pcmlDoc.getValue(pcml + ".@ERPRM").toString());		

			indices = new int[1]; // Indices for access array value
			int dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.@NORES").toString()); // Number of records returned 
			if(acListSize == dsRspCount) {
				for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
					acBal = accountsList.get(indices[0]);
					acBal.setAcHoldStatus(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.AcHoldStatus",indices).toString()); //Account Holding status
					acBal.setStatusDesc(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.StatusDesc",indices).toString()); //Status Reason for Fail
				}
			}

		}  catch (ConnectionPoolException e){
			logger.error("Exception: ", e);
			throw new InterfaceException("9999","Host Connection Failed.. Please contact administrator ");
		}catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		} finally {			
			this.hostConnection.closeConnection(as400);
		}
		logger.debug("Leaving");
		return accountsList;
	}

	
	/**
	 * Method to create new customer Account in Equation
	 */
	@Override
	public InterfaceAccount createAccount(InterfaceAccount accountdetail) throws InterfaceException {
		return null;
	}
	
	
	
	@Override
	public CollateralMark collateralMarking(CollateralMark collateralMark) throws InterfaceException {
		return new CollateralMark();
	}

	@Override
	public CollateralMark collateralDeMarking(CollateralMark collateralMark) throws InterfaceException {
		return new CollateralMark();
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
