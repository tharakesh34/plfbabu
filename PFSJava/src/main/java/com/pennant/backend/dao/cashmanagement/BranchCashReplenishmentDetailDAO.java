package com.pennant.backend.dao.cashmanagement;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.cashmanagement.BranchCashReplenishmentDetail;

public interface BranchCashReplenishmentDetailDAO {

	boolean addReplenishmentDetail(BranchCashReplenishmentDetail branchCashReplenishmentDetail);

	boolean updateReplenishmentDetail(BranchCashReplenishmentDetail branchCashReplenishmentDetail);

	List<BranchCashReplenishmentDetail> getReplenishmentDetailsByProcessId(long processId);

	List<BranchCashReplenishmentDetail> getResponseDetailsByBatchId(long batchId);

	Map<String, Object> getReplenishmentDetailDifference(long batchId);
}
