package com.pennanttech.pff.branchchange.dao.impl;

import java.util.List;

import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.branchchange.upload.BranchChangeUpload;
import com.pennant.pff.branchchange.dao.BranchMigrationDAO;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class BranchMigrationDAOImpl extends SequenceDao<BranchChangeUpload> implements BranchMigrationDAO {

	@Override
	public void updateFinanceMain(Long finID, String finBranch) {
		String sql = "Update FinanceMain Set FinBranch = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, finBranch);
			ps.setLong(index, finID);
		});
	}

	@Override
	public void updateFinODDetails(Long finID, String finBranch) {
		String sql = "Update FinODDetails Set FinBranch = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, finBranch);
			ps.setLong(index, finID);
		});
	}

	@Override
	public void updateFinPFTDetails(Long finID, String finBranch) {
		String sql = "Update FinPFTDetails Set FinBranch = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, finBranch);
			ps.setLong(index, finID);
		});
	}

	@Override
	public void updateFinRepayDeatils(Long finID, String finBranch) {
		String sql = "Update FinRepayDetails Set FinBranch = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, finBranch);
			ps.setLong(index, finID);
		});
	}

	@Override
	public void updateFinRpyQueue(Long finID, String finBranch) {
		String sql = "Update FinRpyQueue Set Branch = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, finBranch);
			ps.setLong(index, finID);
		});
	}

	@Override
	public void updatePaymentRecoveryDetails(String finReference, String finBranch) {
		String sql = "Update PaymentRecoveryDetail Set FinanceBranch = ? Where FinanceReference = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, finBranch);
			ps.setString(index, finReference);
		});
	}

	@Override
	public void updateFinSuspHead(Long finID, String finBranch) {
		String sql = "Update FinSuspHead Set FinBranch = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, finBranch);
			ps.setLong(index, finID);
		});
	}

	@Override
	public void updateFinSuspDetails(Long finID, String finBranch) {
		String sql = "Update FinSuspDetail Set FinBranch = ? Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, finBranch);
			ps.setLong(index, finID);
		});
	}

	@Override
	public void updateLegalDetails(String finReference, String branch) {
		String sql = "Update LegalDetails Set Branch = ? Where LoanReference = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, branch);
			ps.setString(index, finReference);
		});
	}

	@Override
	public List<Accounts> getAccounts(String finReference, String oldBranch) {
		StringBuilder sql = new StringBuilder("Select atg.GroupCode, acct.AcType, Acc.Acbalance");
		sql.append(" From Accounts acc");
		sql.append(" Inner Join Accounts_By_FinReferences abf on abf.AccountID = acc.ID");
		sql.append(" Inner Join RMTAccountTypes acct on acct.AcType = acc.AcType");
		sql.append(" Inner Join AccountTypeGroup atg on atg.GroupId = acct.AcTypeGrpId");
		sql.append(" Where atg.GroupCode in (?, ?)");
		sql.append(" and abf.FinReference = ? and acc.AcBranch = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {

			ps.setString(1, "ASSET");
			ps.setString(2, "LIABILITY");
			ps.setString(2, finReference);
			ps.setString(2, oldBranch);

		}, (rs, rowNum) -> {
			Accounts acc = new Accounts();

			acc.setGroupCode(rs.getString("GroupCode"));
			acc.setAcType(rs.getString("AcType"));
			acc.setAcBalance(rs.getBigDecimal("Acbalance"));

			return acc;
		});
	}

}