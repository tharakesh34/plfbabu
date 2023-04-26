package com.pennant.pff.branchchange.dao;

import java.util.List;

import com.pennant.backend.model.accounts.Accounts;

public interface BranchMigrationDAO {

	void updateFinanceMain(Long finID, String finBranch);

	void updateFinODDetails(Long finID, String finBranch);

	void updateFinRepayDeatils(Long finID, String finBranch);

	void updateFinPFTDetails(Long finID, String finBranch);

	void updatePaymentRecoveryDetails(String finReference, String finBranch);

	void updateFinSuspHead(Long finID, String finBranch);

	void updateFinSuspDetails(Long finID, String finBranch);

	void updateLegalDetails(String finReference, String finBranch);

	void updateFinRpyQueue(Long finID, String finBranch);

	List<Accounts> getAccounts(String finReference, String oldBranch);

}
