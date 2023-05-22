package com.pennant.pff.noc.dao;

import java.util.List;

import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.pff.noc.model.ServiceBranch;
import com.pennant.pff.noc.model.ServiceBranchesLoanType;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pff.core.TableType;

public interface ServiceBranchDAO {

	ServiceBranch getServiceBranch(long id);

	List<ServiceBranch> getServiceBranches(List<String> roleCodes);

	List<ReportListDetail> getPrintServiceBranches(List<String> roleCodes);

	List<ServiceBranch> getResult(ISearch searchFilters);

	void delete(ServiceBranch code, TableType mainTab);

	long save(ServiceBranch sb, TableType type);

	void update(ServiceBranch sb, TableType type);

	boolean isDuplicateKey(String code, TableType tableType);

	void saveLoanType(ServiceBranchesLoanType sb, TableType type);

	void delete(ServiceBranchesLoanType sb, TableType type);

	void updateLoanType(ServiceBranchesLoanType sb, TableType type);

	void deleteBranchLoanTypeById(long id, TableType type);
}