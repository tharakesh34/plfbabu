package com.pennant.coreinterface.process;

import java.util.List;
import java.util.Map;

import com.pennant.coreinterface.model.CustomerLimit;
import com.pennant.coreinterface.model.limit.CustomerLimitDetail;
import com.pennant.coreinterface.model.limit.CustomerLimitPosition;
import com.pennant.coreinterface.model.limit.CustomerLimitUtilization;
import com.pennanttech.pennapps.core.InterfaceException;

public interface CustomerLimitProcess {

	Map<String, Object> fetchCustLimitEnqList(int pageNo, int pageSize) throws InterfaceException;

	List<CustomerLimit> fetchLimitDetails(CustomerLimit custLimit) throws InterfaceException;

	CustomerLimitPosition fetchLimitEnqDetails(CustomerLimitPosition custLimitSummary) throws InterfaceException;

	List<CustomerLimit> fetchGroupLimitDetails(CustomerLimit custLimit)	throws InterfaceException;

	CustomerLimitDetail getLimitDetails(String limitRef, String branchCode) throws InterfaceException;
	
	CustomerLimitUtilization doPredealCheck(CustomerLimitUtilization limitUtilReq) throws InterfaceException;

	CustomerLimitUtilization doReserveUtilization(CustomerLimitUtilization limitUtilReq) throws InterfaceException;

	CustomerLimitUtilization doOverrideAndReserveUtil(CustomerLimitUtilization limitUtilReq) throws InterfaceException;

	CustomerLimitUtilization doConfirmReservation(CustomerLimitUtilization limitUtilReq) throws InterfaceException;

	CustomerLimitUtilization doCancelReservation(CustomerLimitUtilization limitUtilReq) throws InterfaceException;

	CustomerLimitUtilization doCancelUtilization(CustomerLimitUtilization limitUtilReq) throws InterfaceException;

	CustomerLimitUtilization doLimitAmendment(CustomerLimitUtilization coreLimitUtilReq) throws InterfaceException;

}
