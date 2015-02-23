package com.pennant.Interface.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.policecase.PoliceCase;
import com.pennant.backend.model.reports.AvailCustomerDetail;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.coreinterface.model.CoreBankingCustomer;
import com.pennant.coreinterface.model.CorePoliceCase;
import com.pennant.coreinterface.service.CustomerDataProcess;

public class CustomerInterfaceServiceCoreDBImpl implements CustomerInterfaceService{
	
	private static Logger logger = Logger.getLogger(CustomerInterfaceServiceCoreDBImpl.class);
	
	private CustomerDataProcess customerDataProcess;
	
	public Customer fetchCustomerDetails(Customer customer) throws CustomerNotFoundException {
		logger.debug("Entering");

		CoreBankingCustomer coreCust = new CoreBankingCustomer();
		coreCust.setCustomerMnemonic(customer.getCustCIF());

		try {
			coreCust = getCustomerDataProcess().fetchInformation(coreCust);
			
			//Fill the customer data using Core Customer Banking Object
			customer.setCustCoreBank(coreCust.getCustomerMnemonic());
			String[] names  = coreCust.getCustomerFullName().split(" ");
			customer.setCustFName(names[0]);
			if(names.length > 2){
				customer.setCustMName(names[1]);
				customer.setCustLName(names[2]);
			}else{
				if(names.length > 1){
					customer.setCustMName("");
					customer.setCustLName(names[1]);
				}else{
					customer.setCustMName("");
					customer.setCustLName("");
				}
			}			
			customer.setCustShrtName(coreCust.getDefaultAccountShortName());
			customer.setCustTypeCode(coreCust.getCustomerType());
			customer.setCustIsBlocked(coreCust.getCustomerClosed().equals("N")?false:true);
			customer.setCustIsClosed(coreCust.getCustomerClosed().equals("N")?false:true);
			customer.setCustIsDecease(coreCust.getCustomerClosed().equals("N")?false:true);
			customer.setCustIsActive(coreCust.getCustomerInactive().equals("N")?true:false);
			//customer.setCustLng(coreCust.getLanguageCode());
			customer.setCustParentCountry(coreCust.getParentCountry());
			customer.setCustCOB(coreCust.getParentCountry());
			customer.setCustRiskCountry(coreCust.getRiskCountry());
			customer.setCustResdCountry(coreCust.getResidentCountry());
			customer.setCustDftBranch(coreCust.getCustomerBranchMnemonic());
			//customer.setCustGroupSts(coreCust.getGroupStatus());
			//customer.setCustGroupID(coreCust.getGroupName());
			//customer.setCustSegment(coreCust.getSegmentIdentifier());
			customer.setCustSalutationCode(coreCust.getSalutation());
			customer.setCustDOB(coreCust.getCustDOB());
			customer.setCustGenderCode(coreCust.getGenderCode());
			customer.setCustPOB(coreCust.getCustPOB());
			customer.setCustPassportNo(coreCust.getCustPassportNum());
			customer.setCustPassportExpiry(coreCust.getCustPassportExpiry());
			customer.setCustIsMinor(coreCust.getMinor().equals("N")?false:true);
			customer.setCustTradeLicenceNum(coreCust.getTradeLicNumber());
			customer.setCustTradeLicenceExpiry(coreCust.getTradeLicExpiry());
			customer.setCustVisaNum(coreCust.getVisaNumber());
			customer.setCustVisaExpiry(coreCust.getVisaExpiry());
			customer.setCustNationality(coreCust.getNationality());
			
			customer.setNewRecord(true);
			
		/*} catch (CustomerNotFoundException e) {
			logger.error("Exception " + e.getMessage());
			throw e;*/
		} catch (Exception e) {
			logger.error("Exception " + e.getMessage());
			throw new CustomerNotFoundException(e);
		}
		logger.debug("Leaving");
		return customer;
	}
	
	public String generateNewCIF(String operation, Customer customer, String finReference) throws CustomerNotFoundException {
		logger.debug("Entering");

		String custCIF = "";
		/*try {
			//custCIF = getCustomerProcess().generateNewCIF(blockCustomer, custType, finReference);
			
		} catch (CustomerNotFoundException e) {
			logger.error("Exception " + e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Exception " + e.getMessage());
			throw new CustomerNotFoundException(e);
		}*/
		logger.debug("Leaving");
		return custCIF;
	}
	
	@Override
    public AvailCustomerDetail fetchAvailCustDetails(AvailCustomerDetail detail, BigDecimal newExposure, String ccy)
            throws CustomerNotFoundException {
	    // TODO Auto-generated method stub
	    return null;
    }

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setCustomerDataProcess(CustomerDataProcess customerDataProcess) {
	    this.customerDataProcess = customerDataProcess;
    }
	public CustomerDataProcess getCustomerDataProcess() {
	    return customerDataProcess;
    }

	@Override
    public List<BlackListCustomers> fetchBlackListedCustomers(BlackListCustomers customer,
            List<DedupParm> dedupParmList) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
	public List<PoliceCase> fetchPoliceCase(PoliceCase policeCase, List<DedupParm> dedupParmList)
			throws IllegalAccessException, InvocationTargetException {
		CorePoliceCase coreBankPoliceCase = new CorePoliceCase();
		BeanUtils.copyProperties(coreBankPoliceCase, policeCase);
		List<PoliceCase> policeCaseList = new ArrayList<PoliceCase>();
		for (DedupParm dedupParm : dedupParmList) {
			List<CorePoliceCase> list = getCustomerDataProcess().fetchPoliceCustInformation(coreBankPoliceCase, dedupParm.getSQLQuery());

			for (int i = 0; i < list.size(); i++) {
				CorePoliceCase coreBankList = list.get(i);
				PoliceCase policeCaseData = new PoliceCase();
				BeanUtils.copyProperties(policeCaseData, coreBankList);
				policeCaseData.setPoliceCaseRule(dedupParm.getQueryCode());
				policeCaseList.add(policeCaseData);
			}
		}

		return  policeCaseList;
	}


}
