package com.pennant.equation.process.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ConnectionPoolException;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.coreinterface.model.CoreBankAccountPosting;
import com.pennant.coreinterface.process.AccountPostingProcess;
import com.pennant.equation.util.DateUtility;
import com.pennant.equation.util.GenericProcess;
import com.pennant.equation.util.HostConnection;
import com.pennanttech.pennapps.core.InterfaceException;

public class AccountPostingProcessImpl extends GenericProcess implements AccountPostingProcess {

	private static Logger logger = Logger.getLogger(AccountPostingProcessImpl.class);
	
	private HostConnection hostConnection;

	public AccountPostingProcessImpl() {
		super();
	}
	
	/**
	 * Method for Fetching account detail Numbers depends on Parameter key fields
	 * @param coreAcct
	 * @return
	 * @throws AccountNotFoundException
	 */
	@Override
	public List<CoreBankAccountPosting> doPostings(List<CoreBankAccountPosting> accountPostings,String postBranch,
			String createNow) throws InterfaceException {
		logger.debug("Entering");
				
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFSFP"; 		// Save Finance Postings
		
		int[] indices = new int[1]; 	// Indices for access array value
		int dsRspCount;              	// Number of records returned 
		List<CoreBankAccountPosting> bankAccountPostings = new ArrayList<CoreBankAccountPosting>(accountPostings.size());
				
		try {
			as400 = this.hostConnection.getConnection();
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			
			pcmlDoc.setValue(pcml + ".@REQDTA.@NOREQ", accountPostings.size()); 		
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
			pcmlDoc.setValue(pcml + ".@ERPRM", ""); 	
			pcmlDoc.setValue(pcml + ".@REQDTA.CreateNow", createNow); 																					   // Is create Ac now
			pcmlDoc.setValue(pcml + ".@REQDTA.LinkTranId", StringUtils.rightPad(StringUtils.trimToEmpty(accountPostings.get(0).getLinkedTranId()), 10));   //Linked Transaction Id
			pcmlDoc.setValue(pcml + ".@REQDTA.ValueDate", DateUtility.formatDate(accountPostings.get(0).getValueDate(),"ddMMyyyy")); 					   //Value Date
			pcmlDoc.setValue(pcml + ".@REQDTA.FinType", StringUtils.rightPad(StringUtils.trimToEmpty(accountPostings.get(0).getFinType()), 8)); 		   //Finance Type
			pcmlDoc.setValue(pcml + ".@REQDTA.PostBrnm", StringUtils.rightPad(StringUtils.trimToEmpty(postBranch),4)); 									   //Finance Branch
			for (indices[0] = 0; indices[0] < accountPostings.size(); indices[0]++){
				CoreBankAccountPosting posting = accountPostings.get(indices[0]);  
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CustId", indices, posting.getCustCIF()); 			// Customer Number
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.Currency",indices, posting.getAcCcy());			// Account Currency
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.AcType", indices, StringUtils.rightPad(posting.getAcType(), 8));// Account Type
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.Branch",indices, StringUtils.trimToEmpty(posting.getAcBranch()));			// Account Branch
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.AccountNumber", indices, StringUtils.rightPad(StringUtils.trimToEmpty(posting.getAccount()), 13));// Account Number
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CreateNew", indices, posting.getCreateNew()); 		// Is create New Ac 
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CreateIfNF", indices, posting.getCreateIfNF()); 	// Create New If not Found
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.InternalAc", indices, posting.getInternalAc()); 	// Is Internal Ac
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.TranCode", indices, StringUtils.rightPad(StringUtils.trimToEmpty(posting.getTranCode()), 8));		//Transaction Code
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.RevTranCode", indices, StringUtils.rightPad(StringUtils.trimToEmpty(posting.getRevTranCode()), 8));//Rev Transaction Code
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.DrOrCr", indices, posting.getDrOrCr());			// Status
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.Shadow", indices, posting.getShadow());			// Shadow Posting
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.Amount", indices,posting.getPostAmount());			// Post Amount
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.TranOrder", indices,posting.getTransOrderId());	// Transaction Order ID
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinEvent", indices,posting.getFinEvent()); 		//Finance Event
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinReference", indices,accountPostings.get(indices[0]).getFinReference()); //Finance Reference
			}
			
			pcmlDoc.setValue(pcml + ".@RSPDTA.@NORES", 0); 	
			logger.debug(" Before PCML Call");
			getHostConnection().callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");

			CoreBankAccountPosting account = null;
			dsRspCount=Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.@NORES").toString());
			indices = new int[1]; // Indices for access array value

			for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
				account = new CoreBankAccountPosting();
				account.setLinkedTranId(pcmlDoc.getValue(pcml + ".@RSPDTA.LinkTranId").toString());			//Linked Transaction Id
				account.setValueDate(DateUtility.getUtilDate(StringUtils.leftPad(StringUtils.trimToEmpty(pcmlDoc.getValue(pcml + ".@RSPDTA.ValueDate").toString()), 8,"0"),"ddMMyyyy"));	//Value Date
				account.setFinType(pcmlDoc.getValue(pcml + ".@RSPDTA.FinType").toString());					//Finance type
				account.setFinReference(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.FinReference",indices).toString());		// Finance reference
				account.setCustCIF(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.CustId",indices).toString()); 	//Customer CIF
				account.setAcCcy(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.Currency",indices).toString()); 	//Account Currency
				account.setAcType(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.AcType",indices).toString()); 	//Account Type
				account.setAcBranch(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.Branch",indices).toString()); 	//Account Branch
				account.setAccount(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.AccountNumber",indices).toString()); 	//Account Number
				account.setCreateNew(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.CreateNew",indices).toString()); 		//Create New Ac w/o save
				account.setCreateIfNF(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.CreateIfNF",indices).toString()); 	//Create New Account w/o save if Not found
				account.setInternalAc(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.InternalAc",indices).toString()); 	//Internal Account
				account.setPostStatus(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.OpenSts",indices).toString()); 		//Post Status
				account.setTranCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.TranCode",indices).toString()); 		//Transaction Code
				account.setRevTranCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.RevTranCode",indices).toString()); 	//Rev Transaction Code
				account.setDrOrCr(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DrOrCr",indices).toString()); 			//Status(D/C)
				account.setShadow(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.Shadow",indices).toString()); 			//Shadow Posting
				account.setPostAmount(new BigDecimal(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.Amount",indices).toString())); //Post Amount
				account.setPostRef(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.PostRef",indices).toString()); //Posting reference
				account.setFinEvent(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.FinEvent",indices).toString()); //Fin Event
				account.setTransOrderId(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.TranOrder",indices).toString()); //Transaction Order ID
				
				account.setErrorId(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.@IERCOD",indices).toString()); 		//Error Code
				account.setErrorMsg(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.@IERPRM",indices).toString()); 	//Error Message
				bankAccountPostings.add(account);
			}
			
		} catch (ConnectionPoolException e){
			logger.error("Exception: ", e);
			throw new InterfaceException("9999","Host Connection Failed.. Please contact administrator ");
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		}  finally {			
				this.hostConnection.closeConnection(as400);
		}
		
		logger.debug("Leaving");
		return bankAccountPostings;
	}
	
	/**
	 * Method for Posting Accrual Details
	 * @param postings
	 * @param valueDate
	 * @param postBranch
	 * @param isDummy
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<CoreBankAccountPosting> doUploadAccruals(List<CoreBankAccountPosting> postings,  Date valueDate, String postBranch, String isDummy)  throws InterfaceException{
		logger.debug("Entering");

		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PTPFFAMZ"; 		// Upload finance profit Details
		int[] indices = new int[1]; 	// Indices for access array value
		int dsRspCount; 	
		List<CoreBankAccountPosting> bankAccountPostings = new ArrayList<CoreBankAccountPosting>(postings.size());

		try {
			as400 = this.hostConnection.getConnection();

			try {
				pcmlDoc = new ProgramCallDocument(as400, pcml);
			}catch (Exception e) {
				logger.error("Exception: ", e);
			}

			pcmlDoc.setValue(pcml + ".@REQDTA.@NOREQ", postings.size()); 	
			pcmlDoc.setValue(pcml + ".@REQDTA.ValueDate", DateUtility.formatDate(valueDate,"ddMMyyyy")); 	
			pcmlDoc.setValue(pcml + ".@REQDTA.PostBrnm", postBranch); 	
			pcmlDoc.setValue(pcml + ".@REQDTA.IsDummy", isDummy); 	
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 	
			pcmlDoc.setValue(pcml + ".@ERPRM", ""); 

			for (indices[0] = 0; indices[0] < postings.size(); indices[0]++){
				CoreBankAccountPosting posting = postings.get(indices[0]); 

				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CustId", indices, posting.getCustCIF()); 			
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.Currency", indices, posting.getAcCcy());
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.AcType", indices, posting.getAcType());
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.Branch", indices, posting.getAcBranch());	
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.AccountNumber", indices, posting.getAccount());	
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CreateNew", indices,posting.getCreateNew() );	
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.CreateIfNF", indices,posting.getCreateIfNF() );	
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.InternalAc", indices,posting.getInternalAc() );	
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.TranOrder", indices,posting.getTransOrderId());	
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.TranCode", indices,posting.getTranCode());	
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.RevTranCode", indices,posting.getRevTranCode());	
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.DrOrCr", indices,posting.getDrOrCr() );	
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.Shadow", indices,posting.getShadow());	
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.Amount", indices,posting.getPostAmount());	
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinEvent", indices,posting.getFinEvent());	
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinReference", indices,posting.getFinReference());	
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.LinkedTxnID", indices,posting.getLinkedTranId());	
				pcmlDoc.setValue(pcml + ".@REQDTA.DEFDTA.FinType", indices,posting.getFinType());	
			}

			pcmlDoc.setValue(pcml + ".@RSPDTA.@NORES", 0); 	
			logger.debug(" Before PCML Call");
			getHostConnection().callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");


			CoreBankAccountPosting account = null;
			dsRspCount=Integer.parseInt(pcmlDoc.getValue(pcml + ".@RSPDTA.@NORES").toString());
			indices = new int[1]; // Indices for access array value

			for (indices[0] = 0; indices[0] < dsRspCount; indices[0]++){
				account = new CoreBankAccountPosting();
				account.setValueDate(DateUtility.getUtilDate(StringUtils.leftPad(StringUtils.trimToEmpty(pcmlDoc.getValue(pcml + ".@RSPDTA.ValueDate").toString()), 8,"0"),"ddMMyyyy"));	//Value Date
				account.setCustCIF(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.CustId",indices).toString()); 	//Customer CIF
				account.setAcCcy(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.Currency",indices).toString()); 	//Account Currency
				account.setAcType(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.AcType",indices).toString()); 	//Account Type
				account.setAcBranch(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.Branch",indices).toString()); 	//Account Branch
				account.setAccount(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.AccountNumber",indices).toString()); 	//Account Number
				account.setCreateNew(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.CreateNew",indices).toString()); 		//Create New Ac w/o save
				account.setCreateIfNF(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.CreateIfNF",indices).toString()); 	//Create New Account w/o save if Not found
				account.setInternalAc(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.InternalAc",indices).toString()); 	//Internal Account
				account.setPostStatus(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.OpenSts",indices).toString()); 		//Post Status
				account.setTransOrderId(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.TranOrder",indices).toString()); //Transaction Order ID
				account.setErrorId(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.@IERCOD",indices).toString()); 		//Error Code
				account.setErrorMsg(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.@IERPRM",indices).toString()); 	//Error Message
				account.setTranCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.TranCode",indices).toString()); 		//Transaction Code
				account.setRevTranCode(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.RevTranCode",indices).toString()); 	//Rev Transaction Code
				account.setDrOrCr(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.DrOrCr",indices).toString()); 			//Status(D/C)
				account.setShadow(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.Shadow",indices).toString()); 			//Shadow Posting
				account.setPostAmount(new BigDecimal(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.Amount",indices).toString())); //Post Amount
				account.setPostRef(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.PostRef",indices).toString()); //Posting reference
				account.setFinEvent(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.FinEvent",indices).toString()); //Fin Event
				account.setFinReference(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.FinReference",indices).toString());		// Finance reference
				account.setLinkedTranId(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.LinkedTxnID",indices).toString());			//Linked Transaction Id
				account.setFinType(pcmlDoc.getValue(pcml + ".@RSPDTA.DETDTA.FinType", indices).toString());					//Finance type

				bankAccountPostings.add(account);
			}
			
		}catch (ConnectionPoolException e){
			logger.error("Exception: ", e);
			throw new InterfaceException("9999","Host Connection Failed.. Please contact administrator ");
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("9999",e.getMessage());
		}  finally {			
			getHostConnection().closeConnection(as400);
		}
		logger.debug("Leaving");
		return bankAccountPostings;
	}

	@Override
	public List<CoreBankAccountPosting> doReversalPostings(List<CoreBankAccountPosting> accountPostings, String postBranch,
			String createNow) throws InterfaceException {
		return new ArrayList<CoreBankAccountPosting>();
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
