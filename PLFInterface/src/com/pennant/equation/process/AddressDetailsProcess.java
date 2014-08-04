package com.pennant.equation.process;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ConnectionPoolException;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.coreinterface.exception.AddressNotFoundException;
import com.pennant.coreinterface.vo.CorebankingAddres;
import com.pennant.equation.util.AS400Util;

public class AddressDetailsProcess extends GenericProcess {
	private static Logger logger = Logger.getLogger(AccountProcess.class);
	
	/**
	 * This method fetches the Address details for the given customer/account 
	 * by calling Equation Program hzh601.
	 * 
	 * @Param CoreBankingkAddressDetails
	 * @Return CoreBankingkAddressDetails
	 */
	
	public CorebankingAddres fetchAccountAddress(String accountNumber, String addressType) throws AddressNotFoundException{
		
		CorebankingAddres corebankingAddres= new CorebankingAddres();
		corebankingAddres.setCustomerMnemonic("");
		corebankingAddres.setAccountNumber(StringUtils.trimToEmpty(accountNumber));
		corebankingAddres.setAddressType(StringUtils.trimToEmpty(addressType));
		
		return fetchAddress(corebankingAddres);
		
	}
	
	public CorebankingAddres fetchCustomerAddress(String customerMnemonic, String addressType) throws AddressNotFoundException{
		
		CorebankingAddres corebankingAddres= new CorebankingAddres();
		corebankingAddres.setAccountNumber("");
		corebankingAddres.setCustomerMnemonic(customerMnemonic);
		corebankingAddres.setAddressType(StringUtils.trimToEmpty(addressType));
		
		return fetchAddress(corebankingAddres);
		
	}
	
	private CorebankingAddres fetchAddress(CorebankingAddres address) throws AddressNotFoundException{	
		logger.debug("Entering");

		AS400Util as400util = null;
		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "hzh601";	
		
		try{
			
			as400util = AS400Util.getAs400Util();
			as400 = as400util.getAs400();
						
			pcmlDoc = AS400Util.getPCMLDoc(as400, pcml);
			
			pcmlDoc.setValue(pcml + ".DSAB.HZCUS", address.getCustomerMnemonic()); 		// Customer mnemonic
			pcmlDoc.setValue(pcml + ".DSAB.HZAB", address.getAccountBranch()); 			// Account branch
			pcmlDoc.setValue(pcml + ".DSAB.HZAN", address.getAccountBasic()); 			// Basic part of account number
			pcmlDoc.setValue(pcml + ".DSAB.HZAS", address.getAccountSuffix()); 			// Account suffix
			pcmlDoc.setValue(pcml + ".DSAB.HZPRIM", address.getAddressType()); 			// Address type
			
			pcmlDoc.setValue(pcml + ".@EPGM",  "H60EER"); 								// Equation Program Name
			
			logger.debug(" Before PCML Call");
			as400util.callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");
			
			logger.info(pcmlDoc.getValue(pcml + ".@ERCOD").toString());
			logger.info(pcmlDoc.getValue(pcml + ".@ERPRM").toString());

			if ("".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {		
				
				address.setAccountBranch(getString(pcmlDoc, pcml, ".DSAB.HZCSA")); 			//Account Branch
				address.setAddressLine1(getString(pcmlDoc, pcml, ".DSAB.HZNA1")); 			//Address line 1
				address.setAddressLine2(getString(pcmlDoc, pcml, ".DSAB.HZNA2")); 			//Address line 2
				address.setAddressLine3(getString(pcmlDoc, pcml, ".DSAB.HZNA3")); 			//Address line 3
				address.setAddressLine4(getString(pcmlDoc, pcml, ".DSAB.HZNA4")); 			//Address line 4
				address.setZipCode(getString(pcmlDoc, pcml, ".DSAB.HZPZIP")); 				//Zip or postal code
				address.setPhoneNo(getString(pcmlDoc, pcml, ".DSAB.HZPHN")); 				//Telephone number
				address.setFlatNo(getString(pcmlDoc, pcml, ".DSAB.HZFAX")); 				//FAX number
				
			}else{
				logger.info("Account Details Not found");				
				throw new AddressNotFoundException("Address not found");
			}

		} catch (ConnectionPoolException e){
			logger.error("Exception " + e);
			throw new AddressNotFoundException("Host Connection Failed.. Please contact administrator ");
		} catch(Exception e)	{			
			logger.error("Exception " + e);
			e.printStackTrace();
			throw new AddressNotFoundException(e);
			
		}finally{
			as400.disconnectAllServices();
		}

		logger.debug("Leaving");
		return address;
	}

}
