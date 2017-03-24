package com.pennant.coreinterface.process;

import java.util.List;
import java.util.Map;

import com.pennant.coreinterface.model.CustomerLimit;
import com.pennant.coreinterface.model.limit.CustomerLimitDetail;
import com.pennant.coreinterface.model.limit.CustomerLimitPosition;
import com.pennant.coreinterface.model.limit.CustomerLimitUtilization;
import com.pennant.exception.PFFInterfaceException;

public interface CustomerLimitProcess {

	Map<String, Object> fetchCustLimitEnqList(int pageNo, int pageSize) throws PFFInterfaceException;

	List<CustomerLimit> fetchLimitDetails(CustomerLimit custLimit) throws PFFInterfaceException;

	CustomerLimitPosition fetchLimitEnqDetails(CustomerLimitPosition custLimitSummary) throws PFFInterfaceException;

	List<CustomerLimit> fetchGroupLimitDetails(CustomerLimit custLimit)	throws PFFInterfaceException;

	CustomerLimitDetail getLimitDetails(String limitRef, String branchCode) throws PFFInterfaceException;
	
	CustomerLimitUtilization doPredealCheck(CustomerLimitUtilization limitUtilReq) throws PFFInterfaceException;

	CustomerLimitUtilization doReserveUtilization(CustomerLimitUtilization limitUtilReq) throws PFFInterfaceException;

	CustomerLimitUtilization doOverrideAndReserveUtil(CustomerLimitUtilization limitUtilReq) throws PFFInterfaceException;

	CustomerLimitUtilization doConfirmReservation(CustomerLimitUtilization limitUtilReq) throws PFFInterfaceException;

	CustomerLimitUtilization doCancelReservation(CustomerLimitUtilization limitUtilReq) throws PFFInterfaceException;

	CustomerLimitUtilization doCancelUtilization(CustomerLimitUtilization limitUtilReq) throws PFFInterfaceException;

	CustomerLimitUtilization doLimitAmendment(CustomerLimitUtilization coreLimitUtilReq) throws PFFInterfaceException;

}
