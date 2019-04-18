package com.pennant.backend.dao.cashmanagement;

import java.util.List;

import com.pennant.backend.model.cashmanagement.BranchCashReplenishmentHeader;

public interface BranchCashReplenishmentHeaderDAO {

	long getNewRequestId();

	long addReplenishmentHeader(BranchCashReplenishmentHeader branchCashReplenishmentHeader);

	boolean updateReplenishmentHeader(BranchCashReplenishmentHeader branchCashReplenishmentHeader);

	BranchCashReplenishmentHeader getReplenishmentHeaderByProcessId(long processId);

	BranchCashReplenishmentHeader getReplenishmentHeaderByDownLoadFile(String downLoadFileName);

	List<BranchCashReplenishmentHeader> getUnprocessedDownLoads();
}
