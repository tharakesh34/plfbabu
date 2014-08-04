package com.pennant.equation.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ConnectionPoolException;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.coreinterface.exception.EquationInterfaceException;
import com.pennant.coreinterface.vo.AccountBalance;
import com.pennant.coreinterface.vo.CoreBankAccountDetail;
import com.pennant.equation.util.HostConnection;

public class AccountProcess extends GenericProcess{

	private static Logger logger = Logger.getLogger(AccountProcess.class);

	private HostConnection hostConnection;

	/**
	 * Method for Fecthing List of account details depends on Parameter key fields
	 * @param coreAcct
	 * @return
	 * @throws AccountNotFoundException
	 */
	public List<CoreBankAccountDetail> fetchAccountDetails(CoreBankAccountDetail coreAcct) throws AccountNotFoundException {
		logger.debug("Entering");

		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFGAD2";//get List of Account Details

		int[] indices = new int[1]; // Indices for access array value
		int dsRspCount;              // Number of records returned 
		List<CoreBankAccountDetail> accountList = new ArrayList<CoreBankAccountDetail>();

		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			pcmlDoc.setValue(pcml + ".@REQDTA.DSREQCURRENCY", coreAcct.getAcCcy()); 	// Account Currency
			pcmlDoc.setValue(pcml + ".@REQDTA.DSREQCUSTID", coreAcct.getCustCIF()); 	// Customer Number
			pcmlDoc.setValue(pcml + ".@REQDTA.DSREQACTYPES", coreAcct.getAcType()); 	// Account Types
			pcmlDoc.setValue(pcml + ".@REQDTA.DSREQDIVISION", coreAcct.getDivision()); 	// Division

			pcmlDoc.setValue(pcml + ".@RSPDTA.DSRSPRCDCNT", 0); 		
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
			pcmlDoc.setValue(pcml + ".@ERPRM", ""); 	

			logger.debug(" Before PCML Call");
			getHostConnection().callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");

			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {			

				dsRspCount=Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.DSRSPRCDCNT").toString());

				CoreBankAccountDetail account = null;
				for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
					account = new CoreBankAccountDetail();

					account.setCustShrtName(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSAACNAME",indices).toString());	//Account Short Name
					account.setAcType(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSAACTYPE",indices).toString()); 		//Account Type
					account.setAccountNumber(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSAACNUMBER",indices).toString()); //Account Number
					account.setAcCcy(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSAACCCY",indices).toString()); //Account Number
					account.setAmountSign(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSAACBalS",indices).toString()); 				//Account Sign
					account.setAcBal(new BigDecimal(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DSAACBal",indices).toString()));		//Account Balance
					accountList.add(account);
				}

			}else{
				logger.info(pcmlDoc.getValue(pcml + ".@ERPRM").toString());				
				throw new AccountNotFoundException(pcmlDoc.getValue(pcml + ".@ERPRM").toString());
			}

		} catch (ConnectionPoolException e){
			logger.error("Exception " + e);
			throw new AccountNotFoundException("Host Connection Failed.. Please contact administrator ");
		} catch (AccountNotFoundException e){
			logger.error("Exception " + e);
			throw e;
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw new AccountNotFoundException(e.getMessage());
		} finally {	
			//this.hostConnection.closeConnection(as400);
		}

		logger.debug("Leaving");
		return accountList;
	}

	/**
	 * Method for Fecthing account detail Numbers depends on Parameter key fields
	 * @param coreAcct
	 * @return
	 * @throws AccountNotFoundException
	 */
	public List<CoreBankAccountDetail> fetchAccount(List<CoreBankAccountDetail> bankAccountDetails,
			String createNow ) throws AccountNotFoundException {
		logger.debug("Entering");

		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFFAN";

		int[] indices = new int[1]; // Indices for access array value
		int dsRspCount;              // Number of records returned 
		List<CoreBankAccountDetail> coreBankAccountDetails = new ArrayList<CoreBankAccountDetail>(bankAccountDetails.size());

		try {

			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			pcmlDoc.setValue(pcml + ".@REQDTA.@NOREQ", bankAccountDetails.size()); 		
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
			pcmlDoc.setValue(pcml + ".@ERPRM", ""); 	
			pcmlDoc.setValue(pcml + ".@REQDTA.CreateNow", indices, createNow); 							// Is create Ac now

			for (indices[0] = 0; indices[0] < bankAccountDetails.size(); indices[0]++){

				CoreBankAccountDetail coreAcct = bankAccountDetails.get(indices[0]);  
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.TranOrder",indices, coreAcct.getTransOrder());		// Transaction Order
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.Branch",indices, coreAcct.getAcBranch());			// Account Branch
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.Currency",indices, coreAcct.getAcCcy());			// Account Currency
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CustId", indices, coreAcct.getCustCIF()); 			// Customer Number
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CreateNew", indices, coreAcct.getCreateNew()); 	// Is create New Ac 
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CreateIfNF", indices, coreAcct.getCreateIfNF()); 	// Create New If not Found
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.InternalAc", indices, coreAcct.getInternalAc()); 	// Is Internal Ac
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.AcType", indices, StringUtils.rightPad(coreAcct.getAcType(), 8));// Account Type
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.AccountNumber", indices, StringUtils.rightPad(StringUtils.trimToEmpty(coreAcct.getAccountNumber()), 13));// Account Number

			}
			pcmlDoc.setValue(pcml + ".@RSPDTA.@NORES", 0); 	

			logger.debug(" Before PCML Call");
			getHostConnection().callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");

			System.out.println(pcmlDoc.getValue(pcml + ".@ERCOD").toString());
			System.out.println(pcmlDoc.getValue(pcml + ".@ERPRM").toString());

			CoreBankAccountDetail account = null;
			dsRspCount=Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.@NORES").toString());
			indices = new int[1]; // Indices for access array value

			for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
				account = new CoreBankAccountDetail();

				account.setAccountNumber(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.AccountNumber",indices).toString()); 	//Account Number
				account.setCustCIF(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.CustId",indices).toString()); 			//Customer CIF
				account.setAcCcy(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.Currency",indices).toString()); 			//Account Currency
				account.setAcType(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.AcType",indices).toString()); 			//Account Type
				account.setAcBranch(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.Branch",indices).toString()); 			//Account Branch
				account.setTransOrder(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.TranOrder",indices).toString()); 	//Trans Order
				account.setCreateNew(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.CreateNew",indices).toString()); 		//Create New Ac w/o save
				account.setCreateIfNF(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.CreateIfNF",indices).toString()); 	//Create New Account w/o save if Not found
				account.setInternalAc(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.InternalAc",indices).toString()); 	//Internal Account
				account.setOpenStatus(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.OpenSts",indices).toString()); 		//Open Status
				account.setErrorCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.@IERCOD",indices).toString()); 		//Error Code
				account.setErrorMessage(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.@IERPRM",indices).toString()); 	//Error Message

				coreBankAccountDetails.add(account);
			}

			//Main Error Code in response checking
			if (!("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString()) || "".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString()))) {	
				logger.error(pcmlDoc.getValue(pcml + ".@ERPRM").toString());				
			}
		} catch (ConnectionPoolException e){
			logger.error("Exception " + e);
			throw new AccountNotFoundException("Host Connection Failed.. Please contact administrator ");
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw new AccountNotFoundException(e.getMessage());
		} finally {			
			this.hostConnection.closeConnection(as400);
		}

		logger.debug("Leaving");
		return coreBankAccountDetails;
	}

	/**
	 * Method for Fetch Account Balance Amount
	 * @param coreAcct
	 * @param newConnection
	 * @return
	 * @throws AccountNotFoundException
	 */
	public CoreBankAccountDetail fetchAccountAvailableBal(CoreBankAccountDetail coreAcct) throws AccountNotFoundException {
		logger.debug("Entering");
		List<CoreBankAccountDetail> coreAcctList = new ArrayList<CoreBankAccountDetail>();
		coreAcctList.add(coreAcct);
		coreAcctList = fetchAccountsListAvailableBal(coreAcctList, false);
		logger.debug("Leaving");
		return coreAcctList.get(0);
	}

	/**
	 * Method for Fetch Account Balance Amount
	 * @param coreAcct
	 * @param newConnection
	 * @return
	 * @throws AccountNotFoundException
	 */
	public List<CoreBankAccountDetail> fetchAccountsListAvailableBal(List<CoreBankAccountDetail> coreAcctList, boolean isCcyCheck) throws AccountNotFoundException {
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFFAB";//Get Funding Account Balance
		List<CoreBankAccountDetail> accountList  = null;
		int[] indices = new int[1]; 	// Indices for access array value

		try {

			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			CoreBankAccountDetail coreAcct = null;
			pcmlDoc.setValue(pcml + ".@REQDTA.@NOREQ", coreAcctList.size());// Account Number
			for (indices[0] = 0; indices[0] < coreAcctList.size(); indices[0]++){
				coreAcct = coreAcctList.get(indices[0]);
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.AccountNumber",indices, coreAcct.getAccountNumber());// Account Number
			}
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
			pcmlDoc.setValue(pcml + ".@ERPRM", ""); 	

			logger.debug(" Before PCML Call");
			getHostConnection().callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");

			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {			
				indices = new int[1]; // Indices for access array value
				CoreBankAccountDetail account = null;
				accountList  = new ArrayList<CoreBankAccountDetail>();
				int dsRspCount = Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.@NORES").toString());            	// Number of records returned 
				for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
					account = new CoreBankAccountDetail();
					account.setAccountNumber(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.AccountNumber",indices).toString()); //Account Number
					account.setAcCcy((pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.AccountCcy",indices).toString())); //Account Type
					
					if(!isCcyCheck){
						account.setAcType((pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.AccountType",indices).toString())); //Account Type
						account.setAcShrtName((pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.AccountName",indices).toString())); //Account Type
						account.setAmountSign(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.AmountSign",indices).toString()); 				//Account Sign
						account.setAcBal(new BigDecimal(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.AccountBalance",indices).toString()));		//Account Balance
					}
					accountList.add(account);
				}
			}else{
				logger.info(pcmlDoc.getValue(pcml + ".@ERPRM").toString());				
				throw new AccountNotFoundException(pcmlDoc.getValue(pcml + ".@ERPRM").toString());
			}

		} catch (ConnectionPoolException e){
			logger.error("Exception " + e);
			throw new AccountNotFoundException("Host Connection Failed.. Please contact administrator ");
		}catch (AccountNotFoundException e) {
			logger.error("Exception " + e);
			throw new AccountNotFoundException(e.getErrorMsg());
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw new AccountNotFoundException(e.getMessage());
		} finally {			
			this.hostConnection.closeConnection(as400);
		}
		logger.debug("Leaving");
		return accountList;

	}

	/**
	 * Method for Removing HOlds on Accounts Before Repayment Process Execution
	 * @return
	 * @throws EquationInterfaceException 
	 */
	public int removeAccountHolds() throws EquationInterfaceException {
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
				throw new EquationInterfaceException(pcmlDoc.getValue(pcml + ".@ERPRM").toString());
			}

		} catch (ConnectionPoolException e){
			logger.error("Exception " + e);
			throw new AccountNotFoundException("Host Connection Failed.. Please contact administrator ");
		} catch (Exception e) {
			logger.error("Exception " + e);
			throw new EquationInterfaceException(e.getMessage());
		} finally {			
			this.hostConnection.closeConnection(as400);
		}
		logger.debug("Leaving");
		return dsRspCount;

	}
	
	/**
	 * Method for Processing Holding on Repay Account Details
	 * @param accountslIst
	 * @return
	 * @throws EquationInterfaceException 
	 */
	public List<AccountBalance> addAccountHolds(List<AccountBalance> accountslIst) throws EquationInterfaceException {
		logger.debug("Entering");
		
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFAAH";//Add Account Holds
		int[] indices = new int[1];
		AccountBalance acBal = null;
		int acListSize = accountslIst.size();

		try {

			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			pcmlDoc.setValue(pcml + ".@REQDTA.@NOREQ", acListSize);// Account Number
			for (indices[0] = 0; indices[0] < accountslIst.size(); indices[0]++){
				acBal = accountslIst.get(indices[0]);
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
					acBal = accountslIst.get(indices[0]);
					acBal.setAcHoldStatus(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.AcHoldStatus",indices).toString()); //Account Holding status
					acBal.setStatusDesc((pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.StatusDesc",indices).toString())); //Status Reason for Fail
				}
			}

		}  catch (ConnectionPoolException e){
			logger.error("Exception " + e);
			throw new AccountNotFoundException("Host Connection Failed.. Please contact administrator ");
		}catch (Exception e) {
			logger.error("Exception " + e);
			throw new EquationInterfaceException(e.getMessage());
		} finally {			
			this.hostConnection.closeConnection(as400);
		}
		logger.debug("Leaving");
		return accountslIst;
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
