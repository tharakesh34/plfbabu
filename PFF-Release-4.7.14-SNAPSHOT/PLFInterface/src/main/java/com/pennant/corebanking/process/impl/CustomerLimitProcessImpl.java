package com.pennant.corebanking.process.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.corebanking.dao.InterfaceDAO;
import com.pennant.coreinterface.model.CustomerLimit;
import com.pennant.coreinterface.model.limit.CustomerLimitDetail;
import com.pennant.coreinterface.model.limit.CustomerLimitPosition;
import com.pennant.coreinterface.model.limit.CustomerLimitSummary;
import com.pennant.coreinterface.model.limit.CustomerLimitUtilization;
import com.pennant.coreinterface.process.CustomerLimitProcess;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennanttech.pennapps.core.InterfaceException;

public class CustomerLimitProcessImpl extends GenericProcess implements CustomerLimitProcess {

	private static Logger logger = Logger.getLogger(CustomerLimitProcessImpl.class);

	private InterfaceDAO interfaceDAO;
	
	public CustomerLimitProcessImpl() {
		super();
	}
	
	/**
	 * Method for Fetching List of Limit Category Customers
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws CustomerLimitProcessException 
	 */
	@Override
	public Map<String, Object> fetchCustLimitEnqList(int pageNo, int pageSize) throws InterfaceException {
		logger.debug("Entering");

		logger.debug("Leaving");
		return null;
	}

	/**
	 * Method for Fetching List of Limit details depends on Parameter key fields
	 * @param CustomerLimit
	 * @return List<CustomerLimit>
	 * @throws 	CustomerLimitProcessException
	 * */
	@Override
	public List<CustomerLimit> fetchLimitDetails(CustomerLimit custLimit) throws InterfaceException {
		logger.debug("Entering");

		logger.debug("Leaving");
		return null;
	}
	
	/**
	 * Method for Fetching List of Limit details depends on Parameter key fields
	 * @param CustomerLimit
	 * @return List<CustomerLimit>
	 * @throws 	CustomerLimitProcessException
	 * */
	@Override
	public CustomerLimitPosition fetchLimitEnqDetails(CustomerLimitPosition custLimitSummary) throws InterfaceException {
		logger.debug("Entering");
		
		CustomerLimitPosition custLimitPosition = new CustomerLimitPosition();
		custLimitPosition.setCustomerReference(custLimitSummary.getCustomerReference());
		custLimitPosition.setGroupRef("GR1234");
		custLimitPosition.setBranchCode("1001");
		
		List<CustomerLimitSummary> summaryList = new ArrayList<CustomerLimitSummary>();
		CustomerLimitSummary limitSummary1 = new CustomerLimitSummary();
		limitSummary1.setLimitReference("LMT12345");
		limitSummary1.setLimitDesc("Customer Limits");
		limitSummary1.setLimitCurrency("AED");
		limitSummary1.setLimitExpiryDate(new Date());
		limitSummary1.setAppovedAmount(new BigDecimal(10000000));
		limitSummary1.setBlocked(new BigDecimal(10000000));
		limitSummary1.setAvailable(new BigDecimal(10000000));
		summaryList.add(limitSummary1);
		
		CustomerLimitSummary limitSummary2 = new CustomerLimitSummary();
		limitSummary2.setLimitReference("LMT999");
		limitSummary2.setLimitDesc("Customer Limits");
		limitSummary2.setLimitCurrency("AED");
		limitSummary2.setLimitExpiryDate(new Date());
		limitSummary2.setAppovedAmount(new BigDecimal(10000000));
		limitSummary2.setBlocked(new BigDecimal(10000000));
		limitSummary2.setAvailable(new BigDecimal(8965245));
		summaryList.add(limitSummary2);
		
		custLimitPosition.setLimitSummary(summaryList);
		logger.debug("Leaving");
		return custLimitPosition;
	}
	
	/**
	 * Method for Fetching List of Group Limit details depends on Parameter key fields
	 * @param CustomerLimit
	 * @return List<CustomerLimit>
	 * @throws 	CustomerLimitProcessException
	 * */
	@Override
	public List<CustomerLimit> fetchGroupLimitDetails(CustomerLimit custLimit) throws InterfaceException {
		logger.debug("Entering");

		logger.debug("Leaving");
		return null;
	}

	/**
	 * Method for fetching the limit details from CoreBank
	 * 
	 */
	@Override
	public CustomerLimitDetail getLimitDetails(String limitRef,	String branchCode) throws InterfaceException {
		
		return getInterfaceDAO().getLimitDetails(limitRef,branchCode);
		
	}

	@Override
	public CustomerLimitUtilization doPredealCheck(CustomerLimitUtilization limitUtilReq) throws InterfaceException {
		CustomerLimitUtilization coreLimitUtilReply=new CustomerLimitUtilization();
		coreLimitUtilReply.setReferenceNum(PFFXmlUtil.getReferenceNumber());
		coreLimitUtilReply.setDealID(limitUtilReq.getDealID());
		coreLimitUtilReply.setCustomerReference(limitUtilReq.getCustomerReference());
		coreLimitUtilReply.setLimitRef(limitUtilReq.getLimitRef());
		//coreLimitUtilReply.setOverrides("GO");
		coreLimitUtilReply.setMsgBreach("NO MsgBreach");
		coreLimitUtilReply.setReturnCode("0000");
		coreLimitUtilReply.setReturnText("GO");
		return coreLimitUtilReply;
	}

	@Override
	public CustomerLimitUtilization doReserveUtilization(CustomerLimitUtilization limitUtilReq) throws InterfaceException {
		CustomerLimitUtilization coreLimitUtilReply=new CustomerLimitUtilization();
		coreLimitUtilReply.setReferenceNum(PFFXmlUtil.getReferenceNumber());
		coreLimitUtilReply.setDealID(limitUtilReq.getDealID());
		coreLimitUtilReply.setCustomerReference(limitUtilReq.getCustomerReference());
		coreLimitUtilReply.setLimitRef(limitUtilReq.getLimitRef());
		coreLimitUtilReply.setResponse("0000");
		coreLimitUtilReply.setErrMsg("SUCESS");
		coreLimitUtilReply.setReturnCode("0000");
		coreLimitUtilReply.setReturnText("SUCESS");
		return coreLimitUtilReply;
	}

	@Override
	public CustomerLimitUtilization doOverrideAndReserveUtil(CustomerLimitUtilization limitUtilReq) throws InterfaceException {
		CustomerLimitUtilization coreLimitUtilReply=new CustomerLimitUtilization();
		coreLimitUtilReply.setReferenceNum(PFFXmlUtil.getReferenceNumber());
		coreLimitUtilReply.setDealID(limitUtilReq.getDealID());
		coreLimitUtilReply.setCustomerReference(limitUtilReq.getCustomerReference());
		coreLimitUtilReply.setLimitRef(limitUtilReq.getLimitRef());
		coreLimitUtilReply.setResponse("0000");
		coreLimitUtilReply.setErrMsg("SUCESS");
		coreLimitUtilReply.setReturnCode("0000");
		coreLimitUtilReply.setReturnText("SUCESS");
		return coreLimitUtilReply;
	}

	@Override
	public CustomerLimitUtilization doConfirmReservation(CustomerLimitUtilization limitUtilReq) throws InterfaceException {
		CustomerLimitUtilization coreLimitUtilReply=new CustomerLimitUtilization();
		coreLimitUtilReply.setReferenceNum(PFFXmlUtil.getReferenceNumber());
		coreLimitUtilReply.setDealID(limitUtilReq.getDealID());
		coreLimitUtilReply.setCustomerReference(limitUtilReq.getCustomerReference());
		coreLimitUtilReply.setLimitRef(limitUtilReq.getLimitRef());
		coreLimitUtilReply.setResponse("0000");
		coreLimitUtilReply.setErrMsg("SUCESS");
		coreLimitUtilReply.setReturnCode("0000");
		coreLimitUtilReply.setReturnText("SUCESS");
		
		if(StringUtils.equals(limitUtilReq.getCustomerReference(), "200003")) {
			coreLimitUtilReply.setResponse("9999");
			coreLimitUtilReply.setErrMsg("Insufficient limit");
			coreLimitUtilReply.setReturnCode("9999");
			coreLimitUtilReply.setReturnText("Insufficient limit");
		}
		
		return coreLimitUtilReply;
	}

	@Override
	public CustomerLimitUtilization doCancelReservation(CustomerLimitUtilization limitUtilReq) throws InterfaceException {
		CustomerLimitUtilization coreLimitUtilReply=new CustomerLimitUtilization();
		coreLimitUtilReply.setReferenceNum(PFFXmlUtil.getReferenceNumber());
		coreLimitUtilReply.setDealID(limitUtilReq.getDealID());
		coreLimitUtilReply.setCustomerReference(limitUtilReq.getCustomerReference());
		coreLimitUtilReply.setLimitRef(limitUtilReq.getLimitRef());
		coreLimitUtilReply.setResponse("0000");
		coreLimitUtilReply.setErrMsg("SUCESS");
		coreLimitUtilReply.setReturnCode("0000");
		coreLimitUtilReply.setReturnText("SUCESS");
		return coreLimitUtilReply;
	}

	@Override
	public CustomerLimitUtilization doCancelUtilization(CustomerLimitUtilization limitUtilReq) throws InterfaceException {
		CustomerLimitUtilization coreLimitUtilReply=new CustomerLimitUtilization();
		coreLimitUtilReply.setReferenceNum(PFFXmlUtil.getReferenceNumber());
		coreLimitUtilReply.setDealID(limitUtilReq.getDealID());
		coreLimitUtilReply.setCustomerReference(limitUtilReq.getCustomerReference());
		coreLimitUtilReply.setLimitRef(limitUtilReq.getLimitRef());
		coreLimitUtilReply.setResponse("0000");
		coreLimitUtilReply.setErrMsg("SUCESS");
		coreLimitUtilReply.setReturnCode("0000");
		coreLimitUtilReply.setReturnText("SUCESS");
		return coreLimitUtilReply;
	}
	
	@Override
	public CustomerLimitUtilization doLimitAmendment(CustomerLimitUtilization limitUtilReq) throws InterfaceException {
		CustomerLimitUtilization coreLimitUtilReply=new CustomerLimitUtilization();
		coreLimitUtilReply.setReferenceNum(PFFXmlUtil.getReferenceNumber());
		coreLimitUtilReply.setDealID(limitUtilReq.getDealID());
		coreLimitUtilReply.setCustomerReference(limitUtilReq.getCustomerReference());
		coreLimitUtilReply.setLimitRef(limitUtilReq.getLimitRef());
		coreLimitUtilReply.setResponse("0000");
		coreLimitUtilReply.setErrMsg("SUCESS");
		coreLimitUtilReply.setReturnCode("0000");
		coreLimitUtilReply.setReturnText("SUCESS");
		return coreLimitUtilReply;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public InterfaceDAO getInterfaceDAO() {
		return interfaceDAO;
	}
	public void setInterfaceDAO(InterfaceDAO interfaceDAO) {
		this.interfaceDAO = interfaceDAO;
	}

}
