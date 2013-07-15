package com.pennant.equation.process;

import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.coreinterface.vo.CoreBankAccountDetail;
import com.pennant.equation.util.AS400Util;

public class NewAccountProcess extends GenericProcess{

	private static Logger logger = Logger.getLogger(NewAccountProcess.class);

	/**
	 * This method Creates the Account for the given CustomerID(CIF) and Account Details data
	 * by calling Equation Program PTACS01R.
	 * 
	 * @Param accountDetail
	 * @Return CoreBankAccountDetail
	 */
	public CoreBankAccountDetail createNewAccount(CoreBankAccountDetail accountDetail) throws Exception{	
		logger.debug("Entering");

		AS400Util as400util = null;
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFGSA";		
		
		try{	
			as400util = AS400Util.getAs400Util();
			as400 = as400util.getAs400();
			
			pcmlDoc = new ProgramCallDocument(as400, pcml);
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 
			pcmlDoc.setValue(pcml + ".@ERPRM", " ");
			
			pcmlDoc.setValue(pcml + ".@REQDTA.DSRESCUSTID", accountDetail.getCustCIF()); 				//Customer Number
			pcmlDoc.setValue(pcml + ".@REQDTA.Branch", accountDetail.getAcBranch()); 					// Account Branch
			pcmlDoc.setValue(pcml + ".@REQDTA.Currency", accountDetail.getAcCcy());					// Account Currency
			pcmlDoc.setValue(pcml + ".@REQDTA.AcType", accountDetail.getAcType()); 					// Account Type
						
			logger.debug(" Before PCML Call");
			as400util.callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");
			
			System.out.println(pcmlDoc.getValue(pcml + ".@ERCOD").toString());
			System.out.println(pcmlDoc.getValue(pcml + ".@ERPRM").toString());
			
			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {
				accountDetail.setAccountNumber(getString(pcmlDoc, pcml, ".@RSPDTA.AccountNumber")); 	//Account Number
			} else {
				logger.info("Account Details Not found");	
				throw new AccountNotFoundException(pcmlDoc.getValue(pcml + ".@ERPRM").toString());
			}

		}catch(Exception e)	{			
			logger.error("Exception " + e);
			e.printStackTrace();
			throw new AccountNotFoundException(e);
			
		}finally{
			as400.disconnectAllServices();
		}

		logger.debug("Leaving");
		return accountDetail;
	}
	
	

}
