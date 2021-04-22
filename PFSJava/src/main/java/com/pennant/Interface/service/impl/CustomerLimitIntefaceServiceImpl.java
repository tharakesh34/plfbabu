package com.pennant.Interface.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaxen.JaxenException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zkplus.spring.SpringUtil;

import com.pennant.Interface.service.CustomerLimitIntefaceService;
import com.pennant.backend.dao.limits.ClosedFacilityDAO;
import com.pennant.backend.dao.limits.LimitInterfaceDAO;
import com.pennant.backend.model.limits.ClosedFacilityDetail;
import com.pennant.backend.model.limits.FinanceLimitProcess;
import com.pennant.backend.model.limits.LimitDetail;
import com.pennant.backend.model.limits.LimitUtilization;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.coreinterface.model.CustomerLimit;
import com.pennant.coreinterface.model.limit.CustomerLimitDetail;
import com.pennant.coreinterface.model.limit.CustomerLimitPosition;
import com.pennant.coreinterface.model.limit.CustomerLimitSummary;
import com.pennant.coreinterface.model.limit.CustomerLimitUtilization;
import com.pennant.coreinterface.process.CustomerLimitProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class CustomerLimitIntefaceServiceImpl implements CustomerLimitIntefaceService {
	private static Logger logger = LogManager.getLogger(CustomerLimitIntefaceServiceImpl.class);

	protected CustomerLimitProcess customerLimitProcess;
	protected LimitInterfaceDAO limitInterfaceDAO;
	protected ClosedFacilityDAO closedFacilityDAO;

	public CustomerLimitIntefaceServiceImpl() {
		super();
	}

	@Override
	public Map<String, Object> fetchCustLimitEnqList(int pageNo, int pageSize) throws InterfaceException {
		logger.debug("Entering");

		Map<String, Object> customerLimitMap = null;
		try {
			if (customerLimitProcess != null) {
				customerLimitMap = customerLimitProcess.fetchCustLimitEnqList(pageNo, pageSize);

				if (customerLimitMap.containsKey("CustLimitList")) {
					@SuppressWarnings("unchecked")
					List<CustomerLimit> list = (List<CustomerLimit>) customerLimitMap.get("CustLimitList");
					List<com.pennant.backend.model.customermasters.CustomerLimit> custLimitList = new ArrayList<com.pennant.backend.model.customermasters.CustomerLimit>();
					com.pennant.backend.model.customermasters.CustomerLimit limit = null;
					for (CustomerLimit customerLimit : list) {

						limit = new com.pennant.backend.model.customermasters.CustomerLimit();
						limit.setCustCIF(customerLimit.getCustMnemonic());
						limit.setCustLocation(customerLimit.getCustLocation());
						limit.setCustShortName(customerLimit.getCustName());
						limit.setLimitCategory(customerLimit.getLimitCategory());
						limit.setCurrency(customerLimit.getLimitCurrency());
						limit.setEarliestExpiryDate(customerLimit.getLimitExpiry());
						limit.setBranch(customerLimit.getLimitBranch());
						limit.setRepeatThousands("Y".equals(customerLimit.getRepeatThousands()) ? true : false);
						limit.setCheckLimit("Y".equals(customerLimit.getCheckLimit()) ? true : false);
						limit.setSeqNum(customerLimit.getSeqNum());

						custLimitList.add(limit);
					}

					customerLimitMap.put("CustLimitList", custLimitList);
				} else {
					customerLimitMap.put("CustLimitList", null);
				}
			}

		} catch (InterfaceException e) {
			throw e;
		}

		logger.debug("Leaving");
		return customerLimitMap;
	}

	@Override
	public List<CustomerLimit> fetchLimitDetails(CustomerLimit custLimit) throws InterfaceException {
		logger.debug("Entering");

		List<CustomerLimit> customerLimits = null;
		try {
			if (customerLimitProcess != null) {
				customerLimits = customerLimitProcess.fetchLimitDetails(custLimit);
			}
		} catch (InterfaceException e) {
			throw e;
		}

		logger.debug("Leaving");
		return customerLimits;
	}

	public static List<CustomerLimitSummary> getCustomerLimitSummary(String customerid) {
		PagedListService service = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<CustomerLimitSummary> searchObject = new JdbcSearchObject<CustomerLimitSummary>(
				CustomerLimitSummary.class);

		searchObject.addFilterEqual("customerid", customerid);
		searchObject.addTabelName("Limit_summary_view");

		return service.getBySearchObject(searchObject);
	}

	@Override
	public List<CustomerLimit> fetchLimitEnquiryDetails(CustomerLimit customerLimit) throws InterfaceException {
		logger.debug("Entering");

		List<CustomerLimit> customerLimits = null;

		try {
			CustomerLimitPosition custLimitPosition = new CustomerLimitPosition();
			custLimitPosition.setCustomerReference(customerLimit.getCustMnemonic());
			custLimitPosition.setBranchCode(customerLimit.getLimitBranch());
			custLimitPosition.setLimitSummary(getCustomerLimitSummary(customerLimit.getCustMnemonic()));

			customerLimits = new ArrayList<CustomerLimit>();
			for (CustomerLimitSummary custLimitSummary : custLimitPosition.getLimitSummary()) {
				CustomerLimit custLimit = new CustomerLimit();

				custLimit.setCustMnemonic(custLimitPosition.getCustRef());
				custLimit.setCustGrpCode(custLimitPosition.getGroupRef());
				custLimit.setLimitBranch(custLimitPosition.getBranchCode());

				custLimit.setLimitCategory(custLimitSummary.getLimitGroup());
				custLimit.setLimitCategoryDesc(custLimitSummary.getLimitItem());
				custLimit.setLimitCurrency(custLimitSummary.getLimitCurrency());
				custLimit.setLimitExpiry(custLimitSummary.getLimitExpiryDate());
				custLimit.setLimitAmount(custLimitSummary.getAppovedAmount());
				custLimit.setRiskAmount(custLimitSummary.getBlocked());
				custLimit.setAvailAmount(custLimitSummary.getBlocked());

				customerLimits.add(custLimit);
			}
		} catch (Exception e) {
			throw e;
		}

		logger.debug("Leaving");
		return customerLimits;
	}

	@Override
	public List<CustomerLimit> fetchGroupLimitDetails(CustomerLimit customerLimit) throws InterfaceException {
		logger.debug("Entering");

		List<CustomerLimit> customerLimits = null;
		try {
			if (customerLimitProcess != null) {
				customerLimits = customerLimitProcess.fetchGroupLimitDetails(customerLimit);
			}

		} catch (InterfaceException e) {
			throw e;
		}

		logger.debug("Leaving");
		return customerLimits;
	}

	/**
	 * Method for fetching the Limit Details from ACP interface through MQ
	 * 
	 * @param limitRef
	 * @param branchCode
	 * @throws InterfaceException
	 */
	@Override
	public LimitDetail getLimitDetail(String limitRef, String branchCode) throws InterfaceException {
		logger.debug("Entering");
		if (customerLimitProcess != null) {
			CustomerLimitDetail coreLimitDetail = customerLimitProcess.getLimitDetails(limitRef, branchCode);
			LimitDetail limitDetail = null;
			if (coreLimitDetail != null) {
				limitDetail = new LimitDetail();
				BeanUtils.copyProperties(coreLimitDetail, limitDetail);
			}

			logger.debug("Leaving");
			return limitDetail;
		}
		return null;
	}

	/**
	 * Method for sending Deal Online Request to ACP interface through MQ
	 * 
	 * @param limitUtilReq
	 * @throws InterfaceException
	 * @throws JaxenException
	 */
	@Override
	public LimitUtilization doPredealCheck(LimitUtilization limitUtilReq) throws InterfaceException {
		logger.debug("Entering");

		if (limitUtilReq == null) {
			return null;
		}
		CustomerLimitUtilization coreLimitUtilReq = new CustomerLimitUtilization();
		BeanUtils.copyProperties(limitUtilReq, coreLimitUtilReq);
		if (customerLimitProcess != null) {
			CustomerLimitUtilization coreLimitUtilReply = customerLimitProcess.doPredealCheck(coreLimitUtilReq);
			LimitUtilization limitUtilization = null;
			if (coreLimitUtilReply != null) {
				limitUtilization = new LimitUtilization();
				BeanUtils.copyProperties(coreLimitUtilReply, limitUtilization);
			}

			logger.debug("Leaving");
			return limitUtilization;
		}
		return null;
	}

	/**
	 * Method for sending Reserve Utilization Request to ACP interface through MQ
	 * 
	 * @param limitUtilReq
	 * @throws InterfaceException
	 * @throws JaxenException
	 */
	@Override
	public LimitUtilization doReserveUtilization(LimitUtilization limitUtilReq) throws InterfaceException {
		logger.debug("Entering");

		if (limitUtilReq == null) {
			return null;
		}
		CustomerLimitUtilization coreLimitUtilReq = new CustomerLimitUtilization();
		BeanUtils.copyProperties(limitUtilReq, coreLimitUtilReq);
		if (customerLimitProcess != null) {
			CustomerLimitUtilization coreLimitUtilReply = customerLimitProcess.doReserveUtilization(coreLimitUtilReq);
			LimitUtilization limitUtilization = null;
			if (coreLimitUtilReply != null) {
				limitUtilization = new LimitUtilization();
				BeanUtils.copyProperties(coreLimitUtilReply, limitUtilization);
			}

			logger.debug("Leaving");
			return limitUtilization;
		}
		return null;
	}

	/**
	 * Method for sending Override&ReserveUtilization Request to ACP interface through MQ
	 * 
	 * @param limitUtilReq
	 * @throws InterfaceException
	 * @throws JaxenException
	 */
	@Override
	public LimitUtilization doOverrideAndReserveUtil(LimitUtilization limitUtilReq) throws InterfaceException {
		logger.debug("Entering");

		if (limitUtilReq == null) {
			return null;
		}
		CustomerLimitUtilization coreLimitUtilReq = new CustomerLimitUtilization();
		BeanUtils.copyProperties(limitUtilReq, coreLimitUtilReq);
		if (customerLimitProcess != null) {
			CustomerLimitUtilization coreLimitUtilReply = customerLimitProcess
					.doOverrideAndReserveUtil(coreLimitUtilReq);
			LimitUtilization limitUtilization = null;
			if (coreLimitUtilReply != null) {
				limitUtilization = new LimitUtilization();
				BeanUtils.copyProperties(coreLimitUtilReply, limitUtilization);
			}

			logger.debug("Leaving");
			return limitUtilization;
		}
		return null;
	}

	/**
	 * Method for sending Confirm Reservation Request to ACP interface through MQ
	 * 
	 * @param limitUtilReq
	 * @throws InterfaceException
	 * @throws JaxenException
	 */
	@Override
	public LimitUtilization doConfirmReservation(LimitUtilization limitUtilReq) throws InterfaceException {
		logger.debug("Entering");

		if (limitUtilReq == null) {
			return null;
		}
		CustomerLimitUtilization coreLimitUtilReq = new CustomerLimitUtilization();
		BeanUtils.copyProperties(limitUtilReq, coreLimitUtilReq);
		if (customerLimitProcess != null) {
			CustomerLimitUtilization coreLimitUtilReply = customerLimitProcess.doConfirmReservation(coreLimitUtilReq);
			LimitUtilization limitUtilization = null;
			if (coreLimitUtilReply != null) {
				limitUtilization = new LimitUtilization();
				BeanUtils.copyProperties(coreLimitUtilReply, limitUtilization);
			}

			logger.debug("Leaving");
			return limitUtilization;
		}
		return null;

	}

	/**
	 * Method for sending Cancel Reservation Request to ACP interface through MQ
	 * 
	 * @param limitUtilReq
	 * @throws InterfaceException
	 * @throws JaxenException
	 */
	@Override
	public LimitUtilization doCancelReservation(LimitUtilization limitUtilReq) throws InterfaceException {
		logger.debug("Entering");

		if (limitUtilReq == null) {
			return null;
		}
		CustomerLimitUtilization coreLimitUtilReq = new CustomerLimitUtilization();
		BeanUtils.copyProperties(limitUtilReq, coreLimitUtilReq);
		if (customerLimitProcess != null) {
			CustomerLimitUtilization coreLimitUtilReply = customerLimitProcess.doCancelReservation(coreLimitUtilReq);
			LimitUtilization limitUtilization = null;
			if (coreLimitUtilReply != null) {
				limitUtilization = new LimitUtilization();
				BeanUtils.copyProperties(coreLimitUtilReply, limitUtilization);
			}

			logger.debug("Leaving");
			return limitUtilization;
		}
		return null;
	}

	/**
	 * Method for sending Cancel Utilization Request to ACP interface through MQ
	 * 
	 * @param limitUtilReq
	 * @throws InterfaceException
	 * @throws JaxenException
	 */
	@Override
	public LimitUtilization doCancelUtilization(LimitUtilization limitUtilReq) throws InterfaceException {
		logger.debug("Entering");

		if (limitUtilReq == null) {
			return null;
		}
		CustomerLimitUtilization coreLimitUtilReq = new CustomerLimitUtilization();
		BeanUtils.copyProperties(limitUtilReq, coreLimitUtilReq);
		if (customerLimitProcess != null) {
			CustomerLimitUtilization coreLimitUtilReply = customerLimitProcess.doCancelUtilization(coreLimitUtilReq);
			LimitUtilization limitUtilization = null;
			if (coreLimitUtilReply != null) {
				limitUtilization = new LimitUtilization();
				BeanUtils.copyProperties(coreLimitUtilReply, limitUtilization);
			}

			logger.debug("Leaving");
			return limitUtilization;
		} else {
			return null;
		}

	}

	/**
	 * Method for sending limit Amendment request to middleware
	 * 
	 * @param limitUtilReq
	 */
	@Override
	public LimitUtilization doLimitAmendment(LimitUtilization limitUtilReq) throws InterfaceException {
		logger.debug("Entering");

		if (limitUtilReq == null) {
			return null;
		}
		CustomerLimitUtilization coreLimitUtilReq = new CustomerLimitUtilization();
		BeanUtils.copyProperties(limitUtilReq, coreLimitUtilReq);
		if (customerLimitProcess != null) {
			CustomerLimitUtilization coreLimitUtilReply = customerLimitProcess.doLimitAmendment(coreLimitUtilReq);
			LimitUtilization limitUtilization = null;
			if (coreLimitUtilReply != null) {
				limitUtilization = new LimitUtilization();
				BeanUtils.copyProperties(coreLimitUtilReply, limitUtilization);
			}

			logger.debug("Leaving");
			return limitUtilization;
		}
		return null;
	}

	/**
	 * 
	 */
	@Override
	public void saveFinLimitUtil(FinanceLimitProcess finLimitProcess) {
		logger.debug("Entering");

		getLimitInterfaceDAO().saveFinLimitUtil(finLimitProcess);

		logger.debug("Leaving");
	}

	/**
	 * Method for save the Customer limit details
	 * 
	 * @param limitDetail
	 */
	@Override
	public void saveOrUpdate(LimitDetail limitDetail) {
		logger.debug("Entering");

		LimitDetail customerLimitDetail = getLimitInterfaceDAO().getCustomerLimitDetails(limitDetail.getLimitRef());

		if (customerLimitDetail == null) {
			getLimitInterfaceDAO().saveCustomerLimitDetails(limitDetail);
		} else {
			getLimitInterfaceDAO().updateCustomerLimitDetails(limitDetail);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method to fetch the LimitUtilization details
	 * 
	 * @param limitUtilReq
	 */
	@Override
	public FinanceLimitProcess getLimitUtilDetails(FinanceLimitProcess financeLimitProcess) {
		logger.debug("Entering");

		return getLimitInterfaceDAO().getLimitUtilDetails(financeLimitProcess);
	}

	@Override
	public LimitDetail getCustomerLimitDetails(String limitRef) {
		logger.debug("Entering");

		return getLimitInterfaceDAO().getCustomerLimitDetails(limitRef);
	}

	/**
	 * Method to fetch Closed facility details from ACP provided schema
	 * 
	 */
	@Override
	public List<ClosedFacilityDetail> fetchClosedFacilityDetails() {
		logger.debug("Entering");

		return getClosedFacilityDAO().fetchClosedFacilityDetails();
	}

	/**
	 * Method for update CLosed Facility Processed and ProcessedDate flags
	 * 
	 * @param proClFacilityList
	 */
	@Override
	public void updateClosedFacilityStatus(List<ClosedFacilityDetail> proClFacilityList) {
		logger.debug("Entering");

		getClosedFacilityDAO().updateClosedFacilityStatus(proClFacilityList);

		logger.debug("Leaving");
	}

	/**
	 * Method for save Closed facility details into PFF
	 * 
	 * @param proClFacilityList
	 * @return boolean
	 */
	@Override
	public boolean saveClosedFacilityDetails(List<ClosedFacilityDetail> proClFacilityList) {
		logger.debug("Entering");

		return getLimitInterfaceDAO().saveClosedFacilityDetails(proClFacilityList);
	}

	/*
	 * public CustomerLimitProcess getCustomerLimitProcess() { return customerLimitProcess; }
	 */

	@Autowired(required = false)
	public void setCustomerLimitProcess(CustomerLimitProcess customerLimitProcess) {
		this.customerLimitProcess = customerLimitProcess;
	}

	public LimitInterfaceDAO getLimitInterfaceDAO() {
		return limitInterfaceDAO;
	}

	public void setLimitInterfaceDAO(LimitInterfaceDAO limitInterfaceDAO) {
		this.limitInterfaceDAO = limitInterfaceDAO;
	}

	public ClosedFacilityDAO getClosedFacilityDAO() {
		return closedFacilityDAO;
	}

	public void setClosedFacilityDAO(ClosedFacilityDAO closedFacilityDAO) {
		this.closedFacilityDAO = closedFacilityDAO;
	}
}
