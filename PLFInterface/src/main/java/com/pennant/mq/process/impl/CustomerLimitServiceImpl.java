package com.pennant.mq.process.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import com.pennant.coreinterface.model.CustomerLimit;
import com.pennant.coreinterface.model.limit.CustomerLimitDetail;
import com.pennant.coreinterface.model.limit.CustomerLimitPosition;
import com.pennant.coreinterface.model.limit.CustomerLimitUtilization;
import com.pennant.coreinterface.process.CustomerLimitProcess;
import com.pennant.mq.processutil.CustomerLimitDetailProcess;
import com.pennant.mq.processutil.CustomerLimitPositionProcess;
import com.pennant.mq.processutil.CustomerLimitUtilProcess;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennanttech.pennapps.core.InterfaceException;

public class CustomerLimitServiceImpl implements CustomerLimitProcess {
	private static final Logger logger = Logger.getLogger(CustomerLimitServiceImpl.class);

	private CustomerLimitDetailProcess customerLimitDetailProcess;
	private CustomerLimitPositionProcess customerLimitPositionProcess;
	private CustomerLimitUtilProcess customerLimitUtilProcess;
	
	public CustomerLimitServiceImpl() {
		super();
	}
	
	@Override
	public Map<String, Object> fetchCustLimitEnqList(int pageNo, int pageSize) throws InterfaceException {
		return null;
	}

	@Override
	public List<CustomerLimit> fetchLimitDetails(CustomerLimit custLimit) throws InterfaceException {
		return null;
	}

	@Override
	public CustomerLimitPosition fetchLimitEnqDetails(CustomerLimitPosition custLimitSummary) throws InterfaceException{
		logger.debug("Entering");

		CustomerLimitPosition customerLimitPosition = null;
		try {
			customerLimitPosition = getCustomerLimitPositionProcess().getCustomerLimitSummary(custLimitSummary,
					InterfaceMasterConfigUtil.CUST_LIMIT_SUMMARY);
		} catch(JaxenException jxe) {
			logger.warn("Exception: ", jxe);
			throw new InterfaceException("PTI9008", jxe.getMessage());
		}
		logger.debug("Leaving");

		return customerLimitPosition;
	}

	@Override
	public List<CustomerLimit> fetchGroupLimitDetails(CustomerLimit custLimit) throws InterfaceException {
		return null;
	}

	/********* Excess Interface Connection calls ********/
	

	/**
	 * Get Customer Limit Details by passing the Request to MQ<br>
	 * 
	 * getCustomerLimitDetails method do the following steps.<br>
	 *  1)  Send getLimitDetail Request to MQ<br>
	 *  2)  Receive Response from MQ
	 *  
	 *  @return CustomerLimitDetailReply
	 */
	public CustomerLimitDetail getLimitDetails(String limitRef, String branchCode) throws InterfaceException {
		logger.debug("Entering");

		CustomerLimitDetail limitDetail = new CustomerLimitDetail();
		limitDetail.setLimitRef(limitRef);
		limitDetail.setBranchCode(branchCode);
		CustomerLimitDetail limitDetailReply = getCustomerLimitDetailProcess().getCustomerLimitDetails(limitDetail,	
				InterfaceMasterConfigUtil.CUST_LIMIT_DETAILS);

		logger.debug("Leaving");

		return limitDetailReply;
	}

	/**
	 * Get Customer Limit Position Details by passing the Request to MQ<br>
	 * 
	 * getCustomerLimitPosition method do the following steps.<br>
	 *  1)  Send getLimitPosition Request to MQ<br>
	 *  2)  Receive Response from MQ
	 *  
	 *  @return CustomerLimitPositionReply
	 * @throws JaxenException 
	 */
	public CustomerLimitPosition getCustomerLimitSummary(CustomerLimitPosition limitPositionReq) 
			throws InterfaceException{
		logger.debug("Entering");
		
		CustomerLimitPosition limitPosReply = null;
		try {
			limitPosReply = getCustomerLimitPositionProcess().getCustomerLimitSummary(limitPositionReq, 
					InterfaceMasterConfigUtil.CUST_LIMIT_SUMMARY);
		} catch(JaxenException jxe) {
			logger.warn("Exception: ", jxe);
			throw new InterfaceException("PTI9008", jxe.getMessage());
		}
		logger.debug("Leaving");

		return limitPosReply;
	}
	
	/**
	 * Do Customer PredealCheck by passing the Request to MQ <br>
	 * 
	 * doPredealCheck method do the following steps.<br>
	 *  1)  Send PredealCheck Request to MQ<br>
	 *  2)  Receive GO/NOGO/Error Response from MQ<br>
	 *  Go------doReserve<br>
	 *  NOGO----Show Message Breaches<br>
	 *  Error---Cancel the Service<br>
	 *  
	 *  @return CustomerLimitUtilization
	 * @throws JaxenException 
	 */
	@Override
	public CustomerLimitUtilization doPredealCheck(CustomerLimitUtilization custLimitUtilization) 
			throws InterfaceException {
		logger.debug("Entering");

		CustomerLimitUtilization limitUtilReply = null;
		try {
			limitUtilReply = getCustomerLimitUtilProcess().getLimitUtilizationDetails(custLimitUtilization, 
					InterfaceMasterConfigUtil.DEAL_ONLINE_REQUEST);
		} catch(JaxenException jxe) {
			logger.warn("Exception: ", jxe);
			throw new InterfaceException("PTI9008", jxe.getMessage());
		}
		logger.debug("Leaving");

		return limitUtilReply;
	}

	/**
	 * Reserve the Amount by passing the Request to MQ <br>
	 * 
	 * doReserveUtilization method do the following steps.<br>
	 *  1)  Send Reserve Request to MQ<br>
	 *  2)  Receive Response from MQ
	 *  
	 *  @return CustomerLimitUtilDetail
	 * @throws JaxenException 
	 */
	@Override
	public CustomerLimitUtilization doReserveUtilization(CustomerLimitUtilization custLimitUtilization) 
			throws InterfaceException {
		logger.debug("Entering");

		CustomerLimitUtilization limitUtilReply = null;
		try {
			limitUtilReply = getCustomerLimitUtilProcess().getLimitUtilizationDetails(custLimitUtilization, 
					InterfaceMasterConfigUtil.DEAL_ONLINE_REQUEST);
		} catch(JaxenException jxe) {
			logger.warn("Exception: ", jxe);
			throw new InterfaceException("PTI9008", jxe.getMessage());
		}
		logger.debug("Leaving");

		return limitUtilReply;
	}

	/**
	 * Do Override & Reserve the Amount by passing the Request to MQ <br>
	 * 
	 * doOverrideAndReserveUtil method do the following steps.<br>
	 *  1)  Send Override & Reserve Request to MQ<br>
	 *  2)  Receive Response from MQ
	 *  
	 *  @return CustomerLimitUtilDetail
	 * @throws JaxenException 
	 */
	@Override
	public CustomerLimitUtilization doOverrideAndReserveUtil(CustomerLimitUtilization custLimitUtilReq) 
			throws InterfaceException {
		logger.debug("Entering");

		CustomerLimitUtilization limitUtilReply = null;
		try {
			limitUtilReply = getCustomerLimitUtilProcess().getLimitUtilizationDetails(custLimitUtilReq,
					InterfaceMasterConfigUtil.DEAL_ONLINE_REQUEST);
		} catch(JaxenException jxe) {
			logger.warn("Exception: ", jxe);
			throw new InterfaceException("PTI9008", jxe.getMessage());
		}
		logger.debug("Leaving");

		return limitUtilReply;
	}

	/**
	 * Do Override & Reserve the Amount by passing the Request to MQ <br>
	 * 
	 * doConfirmReservation method do the following steps.<br>
	 *  1)  Send ConfirmReserve Request to MQ<br>
	 *  2)  Receive Response from MQ
	 *  
	 *  @return CustomerLimitUtilDetail
	 * @throws JaxenException 
	 */
	@Override
	public CustomerLimitUtilization doConfirmReservation(CustomerLimitUtilization custLimitUtilReq) 
			throws InterfaceException {
		logger.debug("Entering");

		CustomerLimitUtilization limitUtilReply = null;
		try {
			limitUtilReply = getCustomerLimitUtilProcess().getLimitUtilizationDetails(custLimitUtilReq, 
					InterfaceMasterConfigUtil.DEAL_ONLINE_REQUEST);
		} catch(JaxenException jxe) {
			logger.warn("Exception: ", jxe);
			throw new InterfaceException("PTI9008", jxe.getMessage());
		}
		logger.debug("Leaving");

		return limitUtilReply;
	}

	/**
	 * Cancel the Reserve Amount by passing the Request to MQ <br>
	 * 
	 * doCancelReservation method do the following steps.<br>
	 *  1)  Send CancelReserve Request to MQ<br>
	 *  2)  Receive Response from MQ
	 *  
	 *  @return CustomerLimitUtilDetail
	 * @throws JaxenException 
	 */
	@Override
	public CustomerLimitUtilization doCancelReservation(CustomerLimitUtilization custLimitUtilReq) 
			throws InterfaceException {
		logger.debug("Entering");

		CustomerLimitUtilization limitUtilReply = null;
		try {
			limitUtilReply = getCustomerLimitUtilProcess().getLimitUtilizationDetails(custLimitUtilReq,
					InterfaceMasterConfigUtil.DEAL_ONLINE_REQUEST);
		} catch(JaxenException jxe) {
			logger.warn("Exception: ", jxe);
			throw new InterfaceException("PTI9008", jxe.getMessage());
		}
		logger.debug("Leaving");

		return limitUtilReply;
	}

	/**
	 * Cancel the Utilization Amount by passing the Request to MQ <br>
	 * 
	 * doCancelUtilization method do the following steps.<br>
	 *  1)  Send CancelUtilization Request to MQ<br>
	 *  2)  Receive Response from MQ
	 *  
	 *  @return CustomerLimitUtilDetail
	 * @throws JaxenException 
	 */
	@Override
	public CustomerLimitUtilization doCancelUtilization(CustomerLimitUtilization custLimitUtilReq) 
			throws InterfaceException {
		logger.debug("Entering");

		CustomerLimitUtilization limitUtilReply = null;
		try {
			limitUtilReply = getCustomerLimitUtilProcess().getLimitUtilizationDetails(custLimitUtilReq, 
					InterfaceMasterConfigUtil.DEAL_ONLINE_REQUEST);
		} catch(JaxenException jxe) {
			logger.warn("Exception: ", jxe);
			throw new InterfaceException("PTI9008", jxe.getMessage());
		}
		logger.debug("Leaving");

		return limitUtilReply;
	}
	
	/**
	 * Cancel the Utilization Amount by passing the Request to MQ <br>
	 * 
	 * doCancelUtilization method do the following steps.<br>
	 *  1)  Send CancelUtilization Request to MQ<br>
	 *  2)  Receive Response from MQ
	 *  
	 *  @return CustomerLimitUtilDetail
	 * @throws JaxenException 
	 */
	@Override
	public CustomerLimitUtilization doLimitAmendment(CustomerLimitUtilization custLimitUtilReq) 
			throws InterfaceException {
		logger.debug("Entering");
		
		CustomerLimitUtilization limitUtilReply = null;
		try {
			limitUtilReply = getCustomerLimitUtilProcess().getLimitUtilizationDetails(custLimitUtilReq, 
					InterfaceMasterConfigUtil.DEAL_ONLINE_REQUEST);
		} catch(JaxenException jxe) {
			logger.warn("Exception: ", jxe);
			throw new InterfaceException("PTI9008", jxe.getMessage());
		}
		logger.debug("Leaving");
		
		return limitUtilReply;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public CustomerLimitDetailProcess getCustomerLimitDetailProcess() {
		return customerLimitDetailProcess;
	}
	
	public void setCustomerLimitDetailProcess(
			CustomerLimitDetailProcess customerLimitDetailProcess) {
		this.customerLimitDetailProcess = customerLimitDetailProcess;
	}
	
	public CustomerLimitPositionProcess getCustomerLimitPositionProcess() {
		return customerLimitPositionProcess;
	}
	public void setCustomerLimitPositionProcess(
			CustomerLimitPositionProcess customerLimitPositionProcess) {
		this.customerLimitPositionProcess = customerLimitPositionProcess;
	}

	public CustomerLimitUtilProcess getCustomerLimitUtilProcess() {
		return customerLimitUtilProcess;
	}
	public void setCustomerLimitUtilProcess(CustomerLimitUtilProcess customerLimitUtilProcess) {
		this.customerLimitUtilProcess = customerLimitUtilProcess;
	}
}