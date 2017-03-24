package com.pennant.Interface.service;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.limits.ClosedFacilityDetail;
import com.pennant.backend.model.limits.FinanceLimitProcess;
import com.pennant.backend.model.limits.LimitDetail;
import com.pennant.backend.model.limits.LimitUtilization;
import com.pennant.coreinterface.model.CustomerLimit;
import com.pennant.exception.PFFInterfaceException;

public interface CustomerLimitIntefaceService {
	
	List<CustomerLimit> fetchLimitDetails(CustomerLimit customerLimit) throws PFFInterfaceException;

	List<CustomerLimit> fetchLimitEnquiryDetails(CustomerLimit customerLimit) throws PFFInterfaceException;

	Map<String, Object> fetchCustLimitEnqList(int pageNo, int pageSize) throws PFFInterfaceException;

	List<CustomerLimit> fetchGroupLimitDetails(CustomerLimit availLimit) throws PFFInterfaceException;

	LimitDetail getLimitDetail(String limitRef, String branchCode) throws PFFInterfaceException;

	LimitUtilization doPredealCheck(LimitUtilization limitUtilReq) throws PFFInterfaceException;

	LimitUtilization doReserveUtilization(LimitUtilization limitUtilReq) throws PFFInterfaceException;

	LimitUtilization doOverrideAndReserveUtil(LimitUtilization limitUtilReq) throws PFFInterfaceException;

	LimitUtilization doConfirmReservation(LimitUtilization limitUtilReq) throws PFFInterfaceException;

	LimitUtilization doCancelReservation(LimitUtilization limitUtilReq) throws PFFInterfaceException;

	LimitUtilization doCancelUtilization(LimitUtilization limitUtilReq) throws PFFInterfaceException;

	void saveFinLimitUtil(FinanceLimitProcess finLimitProcess);

	void saveOrUpdate(LimitDetail limitDetail);

	FinanceLimitProcess getLimitUtilDetails(FinanceLimitProcess financeLimitProcess);

	LimitDetail getCustomerLimitDetails(String limitRef);

	List<ClosedFacilityDetail> fetchClosedFacilityDetails();

	boolean saveClosedFacilityDetails(List<ClosedFacilityDetail> proClFacilityList);

	void updateClosedFacilityStatus(List<ClosedFacilityDetail> proClFacilityList);

	LimitUtilization doLimitAmendment(LimitUtilization limitUtilReq) throws PFFInterfaceException;
}
