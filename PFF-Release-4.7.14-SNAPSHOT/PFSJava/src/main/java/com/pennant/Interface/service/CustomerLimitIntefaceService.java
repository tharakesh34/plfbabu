package com.pennant.Interface.service;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.limits.ClosedFacilityDetail;
import com.pennant.backend.model.limits.FinanceLimitProcess;
import com.pennant.backend.model.limits.LimitDetail;
import com.pennant.backend.model.limits.LimitUtilization;
import com.pennant.coreinterface.model.CustomerLimit;
import com.pennanttech.pennapps.core.InterfaceException;

public interface CustomerLimitIntefaceService {
	
	List<CustomerLimit> fetchLimitDetails(CustomerLimit customerLimit) throws InterfaceException;

	List<CustomerLimit> fetchLimitEnquiryDetails(CustomerLimit customerLimit) throws InterfaceException;

	Map<String, Object> fetchCustLimitEnqList(int pageNo, int pageSize) throws InterfaceException;

	List<CustomerLimit> fetchGroupLimitDetails(CustomerLimit availLimit) throws InterfaceException;

	LimitDetail getLimitDetail(String limitRef, String branchCode) throws InterfaceException;

	LimitUtilization doPredealCheck(LimitUtilization limitUtilReq) throws InterfaceException;

	LimitUtilization doReserveUtilization(LimitUtilization limitUtilReq) throws InterfaceException;

	LimitUtilization doOverrideAndReserveUtil(LimitUtilization limitUtilReq) throws InterfaceException;

	LimitUtilization doConfirmReservation(LimitUtilization limitUtilReq) throws InterfaceException;

	LimitUtilization doCancelReservation(LimitUtilization limitUtilReq) throws InterfaceException;

	LimitUtilization doCancelUtilization(LimitUtilization limitUtilReq) throws InterfaceException;

	void saveFinLimitUtil(FinanceLimitProcess finLimitProcess);

	void saveOrUpdate(LimitDetail limitDetail);

	FinanceLimitProcess getLimitUtilDetails(FinanceLimitProcess financeLimitProcess);

	LimitDetail getCustomerLimitDetails(String limitRef);

	List<ClosedFacilityDetail> fetchClosedFacilityDetails();

	boolean saveClosedFacilityDetails(List<ClosedFacilityDetail> proClFacilityList);

	void updateClosedFacilityStatus(List<ClosedFacilityDetail> proClFacilityList);

	LimitUtilization doLimitAmendment(LimitUtilization limitUtilReq) throws InterfaceException;
}
