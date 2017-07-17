package com.pennant.equation.process.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ibm.as400.access.AS400;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;
import com.pennant.coreinterface.model.CoreBankNewCustomer;
import com.pennant.coreinterface.model.CoreCustomerDedup;
import com.pennant.coreinterface.model.customer.InterfaceCustomer;
import com.pennant.coreinterface.model.customer.InterfaceCustomerDetail;
import com.pennant.coreinterface.process.CustomerCreationProcess;
import com.pennant.equation.util.GenericProcess;
import com.pennant.equation.util.HostConnection;
import com.pennanttech.pennapps.core.InterfaceException;

public class CustomerCreationProcessImpl extends GenericProcess implements CustomerCreationProcess {

	private static Logger logger = Logger.getLogger(CustomerCreationProcessImpl.class);
	private HostConnection hostConnection;

	public CustomerCreationProcessImpl() {
		super();
	}

	@Override
	public String generateNewCIF(CoreBankNewCustomer customer) throws InterfaceException {	
		logger.debug("Entering");

		AS400 as400 = null;
		ProgramCallDocument pcmlDoc = null;
		String pcml = "PFFCIF";		
		String custCIF = "";

		try{	
			as400 = this.hostConnection.getConnection();

			pcmlDoc = new ProgramCallDocument(as400, pcml);
			pcmlDoc.setValue(pcml + ".@ERCOD", "0000"); 
			pcmlDoc.setValue(pcml + ".@ERPRM", "");

			pcmlDoc.setValue(pcml + ".@REQDTA.Operation", customer.getOperation()); 		//Customer Is Block or Add
			pcmlDoc.setValue(pcml + ".@REQDTA.CustCtgType", customer.getCustCtgType()); 	//Customer Category Type
			pcmlDoc.setValue(pcml + ".@REQDTA.FinReference", customer.getFinReference()); 	//Finance Reference

			if("A".equals(customer.getOperation())){
				pcmlDoc.setValue(pcml + ".@REQDTA.CustCIF", customer.getCustCIF()); 		//Customer CIF
				pcmlDoc.setValue(pcml + ".@REQDTA.CustType", customer.getCustType()); 		//Customer Type
				pcmlDoc.setValue(pcml + ".@REQDTA.ShortName", customer.getShortName().length() > 15 ? 
						customer.getShortName().subSequence(0, 14): customer.getShortName()); 	//Short Name
				pcmlDoc.setValue(pcml + ".@REQDTA.Country", customer.getCountry()); 		//Parent Country
				pcmlDoc.setValue(pcml + ".@REQDTA.Branch", customer.getBranch()); 			//Default Branch
				pcmlDoc.setValue(pcml + ".@REQDTA.Currency", customer.getCurrency()); 		//Basic Currency
			}

			logger.debug(" Before PCML Call");
			this.hostConnection.callAPI(pcmlDoc, pcml);
			logger.debug(" After PCML Call");

			if ("0000".equals(pcmlDoc.getValue(pcml + ".@ERCOD").toString())) {	
				custCIF = getString(pcmlDoc, pcml, ".@RSPDTA.CustCIF"); 					//Customer mnemonic
				if(StringUtils.isEmpty(custCIF)){
					throw new InterfaceException("9999","Customer Not Created.");
				}
			} else {
				logger.info("Customer Not Created.");	
				throw new InterfaceException("9999",getString(pcmlDoc, pcml, ".@ERPRM"));
			}

		} catch (InterfaceException e) {
			logger.error("Exception: ", e);	
			throw e;
		} catch (PcmlException e) {
			logger.error("Exception: ", e);	
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}finally{
			this.hostConnection.closeConnection(as400);
		}
		logger.debug("Leaving");
		return custCIF;
	}
	
	/**
	 * Method to fetching customer dedup details from Equation
	 */
	@Override
	public List<CoreCustomerDedup> fetchCustomerDedupDetails(CoreCustomerDedup customerDedup) {
		return null;
	}

	/**
	 * Method to create new customer in Equation
	 */
	@Override
	public String createNewCustomer(InterfaceCustomerDetail customerDetail) throws InterfaceException {
		return null;
	}

	@Override
	public void updateCoreCustomer(InterfaceCustomerDetail interfaceCustomerDetail)	throws InterfaceException {
	}

	@Override
	public String reserveCIF(InterfaceCustomer coreCusomer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String releaseCIF(InterfaceCustomer coreCustomer, String reserveRefNum) throws InterfaceException {
		// TODO Auto-generated method stub
		return null;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public HostConnection getHostConnection() {
		return hostConnection;
	}
	public void setHostConnection(HostConnection hostConnection) {
		this.hostConnection = hostConnection;
	}

}
