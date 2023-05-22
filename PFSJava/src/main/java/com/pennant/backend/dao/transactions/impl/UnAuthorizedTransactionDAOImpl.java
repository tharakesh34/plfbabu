package com.pennant.backend.dao.transactions.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.transactions.UnAuthorizedTransactionDAO;
import com.pennant.backend.model.transactions.UnAuthorizedTransaction;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class UnAuthorizedTransactionDAOImpl extends SequenceDao<UnAuthorizedTransaction>
		implements UnAuthorizedTransactionDAO {

	public UnAuthorizedTransactionDAOImpl() {
		super();
	}

	@Override
	public int save(List<UnAuthorizedTransaction> unAuthTransac) {
		StringBuilder sql = new StringBuilder("Insert Into UnAuthorized_Transactions");
		sql.append("(CustId, FinId, CustCIF, CustShrtName");
		sql.append(", MakerName, FinType, FinReference, Event, BranchCode");
		sql.append(", BranchName, TransactionAmount, NoOfDays, Stage, CurrentRole");
		sql.append(", PreviousRole, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkFlowId");
		sql.append(", Entity, Division, Branch, Product)");
		sql.append(" Values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				UnAuthorizedTransaction uat = unAuthTransac.get(i);
				int index = 1;

				ps.setLong(index++, uat.getCustId());
				ps.setLong(index++, uat.getFinId());
				ps.setString(index++, uat.getCustCIF());
				ps.setString(index++, uat.getCustShrtName());
				ps.setString(index++, uat.getMakerName());
				ps.setString(index++, uat.getFinType());
				ps.setString(index++, uat.getFinReference());
				ps.setString(index++, uat.getEvent());
				ps.setString(index++, uat.getBranchCode());
				ps.setString(index++, uat.getBranchName());
				ps.setBigDecimal(index++, uat.getTransactionAmount());
				ps.setInt(index++, uat.getNoOfDays());
				ps.setString(index++, uat.getStage());
				ps.setString(index++, uat.getCurrentRole());
				ps.setString(index++, uat.getPreviousRole());
				ps.setLong(index++, uat.getLastMntBy());
				ps.setTimestamp(index++, uat.getLastMntOn());
				ps.setString(index++, uat.getRecordStatus());
				ps.setString(index++, uat.getRoleCode());
				ps.setString(index++, uat.getNextRoleCode());
				ps.setString(index++, uat.getTaskId());
				ps.setString(index++, uat.getNextTaskId());
				ps.setString(index++, uat.getRecordType());
				ps.setLong(index++, uat.getWorkflowId());
				ps.setString(index++, uat.getEntity());
				ps.setString(index++, uat.getDivision());
				ps.setString(index++, uat.getBranch());
				ps.setString(index, uat.getProduct());
			}

			@Override
			public int getBatchSize() {
				return unAuthTransac.size();
			}
		}).length;
	}

	@Override
	public void clearData() {
		String sql = "Delete From UnAuthorized_Transactions";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql);
	}

	@Override
	public UnAuthorizedTransaction getTransactions(UnAuthorizedTransaction unauthtransac) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, CustId, FinId, CustCIF, CustShrtName");
		sql.append(", MakerName, FinType, FinReference, Event, BranchCode");
		sql.append(", BranchName, TransactionAmount, NoOfDays, Stage, CurrentRole");
		sql.append(", PreviousRole, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkFlowId");
		sql.append(" From UnAuthorized_Transactions");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				UnAuthorizedTransaction uat = new UnAuthorizedTransaction();

				uat.setId(rs.getLong("ID"));
				uat.setCustId(rs.getLong("CustId"));
				uat.setFinId(rs.getLong("FinId"));
				uat.setCustCIF(rs.getString("CustCIF"));
				uat.setCustShrtName(rs.getString("CustShrtName"));
				uat.setMakerName(rs.getString("MakerName"));
				uat.setFinType(rs.getString("FinType"));
				uat.setFinReference(rs.getString("FinReference"));
				uat.setEvent(rs.getString("Event"));
				uat.setBranchCode(rs.getString("BranchCode"));
				uat.setBranchName(rs.getString("BranchName"));
				uat.setTransactionAmount(rs.getBigDecimal("TransactionAmount"));
				uat.setNoOfDays(rs.getInt("NoOfDays"));
				uat.setStage(rs.getString("Stage"));
				uat.setCurrentRole(rs.getString("CurrentRole"));
				uat.setPreviousRole(rs.getString("PreviousRole"));
				uat.setLastMntBy(rs.getLong("LastMntBy"));
				uat.setLastMntOn(rs.getTimestamp("LastMntOn"));
				uat.setRecordStatus(rs.getString("RecordStatus"));
				uat.setRoleCode(rs.getString("RoleCode"));
				uat.setNextRoleCode(rs.getString("NextRoleCode"));
				uat.setTaskId(rs.getString("TaskId"));
				uat.setNextTaskId(rs.getString("NextTaskId"));
				uat.setRecordType(rs.getString("RecordType"));
				uat.setWorkflowId(rs.getLong("WorkFlowId"));
				return uat;
			}, unauthtransac.getId());
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public List<UnAuthorizedTransaction> getTransactionsReport(String whereClause, List<String> list) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustCIF, CustShrtName, FinType, FinReference, Event");
		sql.append(", LastMntOn, LastMntBy, MakerName, BranchCode, BranchName");
		sql.append(", TransactionAmount, NoOfDays, Stage, CurrentRole, PreviousRole");
		sql.append(", Entity, Division, Branch, Product");
		sql.append(" From UnAuthorized_Transactions");
		sql.append(" Where ");
		sql.append(whereClause);

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			for (String code : list) {
				ps.setString(index++, code);
			}
		}, (rs, rowNum) -> {
			UnAuthorizedTransaction uatr = new UnAuthorizedTransaction();

			uatr.setCustCIF(rs.getString("CustCIF"));
			uatr.setCustShrtName(rs.getString("CustShrtName"));
			uatr.setFinType(rs.getString("FinType"));
			uatr.setFinReference(rs.getString("FinReference"));
			uatr.setEvent(rs.getString("Event"));
			uatr.setLastMntOn(rs.getTimestamp("LastMntOn"));
			uatr.setLastMntBy(rs.getLong("LastMntBy"));
			uatr.setMakerName(rs.getString("MakerName"));
			uatr.setBranchCode(rs.getString("BranchCode"));
			uatr.setBranchName(rs.getString("BranchName"));
			uatr.setTransactionAmount(rs.getBigDecimal("TransactionAmount"));
			uatr.setNoOfDays(rs.getInt("NoOfDays"));
			uatr.setStage(rs.getString("Stage"));
			uatr.setCurrentRole(rs.getString("CurrentRole"));
			uatr.setPreviousRole(rs.getString("PreviousRole"));
			uatr.setEntity(rs.getString("Entity"));
			uatr.setDivision(rs.getString("Division"));
			uatr.setBranch(rs.getString("Branch"));
			uatr.setProduct(rs.getString("Product"));

			return uatr;
		});
	}

	public List<UnAuthorizedTransaction> getFinanceMain() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.FinType, t1.FinReference, t1.LastMntBy, t1.LastMntOn, t1.RoleCode");
		sql.append(", t1.NextRoleCode, t1.RecordStatus, t1.RcdMaintainSts");
		sql.append(", t2.CustCIF, t2.CustShrtName, t3.UsrLogin, t7.BranchCode, t7.BranchDesc");
		sql.append(", t6.EntityCode, t5.DivisionCode, t8.ProductCode, t1.FinAssetValue TransactionAmount");
		sql.append(" From FinanceMain_Temp t1");
		sql.append(" Inner Join Customers t2 on t2.CustID = t1.CustID");
		sql.append(" Inner Join SecUsers t3 on t3.UsrID = t1.LastMntBy");
		sql.append(" Inner Join RMTFinanceTypes t4 On t1.FinType = t4.FinType");
		sql.append(" Inner Join SmtDivisionDetail t5 On t5.DivisionCode = t4.FinDivision");
		sql.append(" Inner Join Entity t6 on t6.EntityCode = t5.EntityCode");
		sql.append(" Inner Join RMTBranches t7 on t7.BranchCode = t1.FinBranch");
		sql.append(" Inner Join BMTProduct t8 on t8.ProductCode = t1.FinCategory");
		sql.append(" Where t1.RecordStatus not in (?, ?)");
		sql.append(" and t1.FinReference in (Select FinReference From FinanceMain)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, PennantConstants.RCD_STATUS_REJECTED);
			ps.setString(2, PennantConstants.RCD_STATUS_CANCELLED);

		}, new UnAuthorizedTransactionRM());

	}

	public List<UnAuthorizedTransaction> getFinReceiptHeader() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.FinType, t1.Reference FinReference, t1.RcdMaintainSts, t1.LastMntOn, t1.LastMntBy");
		sql.append(", t1.RecordStatus, t1.RoleCode, t1.NextRoleCode, t3.CustCIF, t3.CustShrtName");
		sql.append(", t4.UsrLogin, t8.BranchCode, t8.BranchDesc");
		sql.append(", t7.EntityCode, t6.DivisionCode, t9.ProductCode, t1.ReceiptAmount TransactionAmount");
		sql.append(" From FinReceiptHeader_Temp t1");
		sql.append(" Inner Join FinanceMain t2 on t2.FinReference = t1.Reference");
		sql.append(" Inner Join Customers t3 on t3.CustID = t2.custid");
		sql.append(" Inner Join SecUsers t4 on t4.UsrID = t1.LastMntBy");
		sql.append(" Inner Join RMTFinanceTypes t5 On t5.FinType = t2.FinType");
		sql.append(" Inner Join SmtDivisionDetail t6 On t6.DivisionCode = t5.FinDivision");
		sql.append(" Inner Join Entity t7 on t7.EntityCode = t6.EntityCode");
		sql.append(" Inner Join RMTBranches t8 on t8.BranchCode = t2.FinBranch");
		sql.append(" Inner Join BMTProduct t9 on t9.ProductCode = t2.FinCategory");
		sql.append(" Where t1.RecordStatus not in (?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, PennantConstants.RCD_STATUS_REJECTED);
			ps.setString(2, PennantConstants.RCD_STATUS_CANCELLED);

		}, new UnAuthorizedTransactionRM());
	}

	public List<UnAuthorizedTransaction> getManualAdvise() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.FinReference, t1.LastMntOn, t1.LastMntBy, t1.RecordStatus");
		sql.append(", t1.RoleCode, t1.NextRoleCode, t2.FinType, t2.RcdMaintainSts");
		sql.append(", t2.FinType,t3.CustCIF, t3.CustShrtName, t4.UsrLogin");
		sql.append(", t8.BranchCode, t8.BranchDesc");
		sql.append(", t7.EntityCode, t6.DivisionCode, t9.ProductCode, t1.AdviseAmount TransactionAmount");
		sql.append(" From ManualAdvise_Temp t1");
		sql.append(" Inner Join financemain t2 on t2.finreference = t1.finreference");
		sql.append(" Inner Join Customers t3 on t3.CustID = t2.custid");
		sql.append(" Inner Join SecUsers t4 on t4.UsrID = t1.LastMntBy");
		sql.append(" Inner Join RMTFinanceTypes t5 On t5.FinType = t2.FinType");
		sql.append(" Inner Join SmtDivisionDetail t6 On t6.DivisionCode = t5.FinDivision");
		sql.append(" Inner Join Entity t7 on t7.EntityCode = t6.EntityCode");
		sql.append(" Inner Join RMTBranches t8 on t8.BranchCode = t2.FinBranch");
		sql.append(" Inner Join BMTProduct t9 on t9.ProductCode = t2.FinCategory");
		sql.append(" Where t1.RecordStatus not in (?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, PennantConstants.RCD_STATUS_REJECTED);
			ps.setString(2, PennantConstants.RCD_STATUS_CANCELLED);

		}, new UnAuthorizedTransactionRM());
	}

	public List<UnAuthorizedTransaction> getPaymentInstruction() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.LastMntOn, t1.LastMntBy, t1.RecordStatus, t1.ROLECODE, t1.NEXTROLECODE");
		sql.append(", t1.paymentid, t2.CustCIF, t2.CustShrtName, t3.finreference, t3.FinType");
		sql.append(", t3.RcdMaintainSts, t4.UsrLogin, t8.BranchCode, t8.BranchDesc");
		sql.append(", t7.EntityCode, t6.DivisionCode, t9.ProductCode, t1.paymentAmount TransactionAmount");
		sql.append(" From PaymentInstructions_Temp t1");
		sql.append(" Inner Join PaymentHeader_Temp ph on ph.paymentid = t1.paymentid");
		sql.append(" Inner Join FinanceMain t3 on t3.finreference = ph.finreference");
		sql.append(" Inner Join Customers t2 on t2.Custid = t3.Custid");
		sql.append(" Inner Join SecUsers t4 on t4.UsrID = t1.LastMntBy");
		sql.append(" Inner Join RMTFinanceTypes t5 On t5.FinType = t3.FinType");
		sql.append(" Inner Join SmtDivisionDetail t6 On t6.DivisionCode = t5.FinDivision");
		sql.append(" Inner Join Entity t7 on t7.EntityCode = t6.EntityCode");
		sql.append(" Inner Join RMTBranches t8 on t8.BranchCode = t3.FinBranch");
		sql.append(" Inner Join BMTProduct t9 on t9.ProductCode = t3.FinCategory");
		sql.append(" Where t1.RecordStatus not in (?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, PennantConstants.RCD_STATUS_REJECTED);
			ps.setString(2, PennantConstants.RCD_STATUS_CANCELLED);

		}, new UnAuthorizedTransactionRM());
	}

	public List<UnAuthorizedTransaction> getJVPosting() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.Reference FinReference, t1.LastMntOn, t1.LastMntBy, t1.RecordStatus, t1.RoleCode");
		sql.append(", t1.NextRoleCode, t2.FinType, t2.RcdMaintainSts, t3.CustCIF, t3.CustShrtName");
		sql.append(", t4.UsrLogin, t8.BranchCode, t8.BranchDesc");
		sql.append(
				", t7.EntityCode, t6.DivisionCode, t9.ProductCode, (TotDebitsByBatchCCY + TotCreditsByBatchCCY) TransactionAmount");
		sql.append(" From JVPostings_Temp t1");
		sql.append(" Inner Join FinanceMain t2 on t2.FinReference = t1.Reference");
		sql.append(" Inner Join Customers t3 on t3.CustID = t2.CustID");
		sql.append(" Inner Join SecUsers t4 on t4.UsrID = t1.LastMntBy");
		sql.append(" Inner Join RMTFinanceTypes t5 On t5.FinType = t2.FinType");
		sql.append(" Inner Join SmtDivisionDetail t6 On t6.DivisionCode = t5.FinDivision");
		sql.append(" Inner Join Entity t7 on t7.EntityCode = t6.EntityCode");
		sql.append(" Inner Join RMTBranches t8 on t8.BranchCode = t2.FinBranch");
		sql.append(" Inner Join BMTProduct t9 on t9.ProductCode = t2.FinCategory");
		sql.append(" Where t1.RecordStatus not in (?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, PennantConstants.RCD_STATUS_REJECTED);
			ps.setString(2, PennantConstants.RCD_STATUS_CANCELLED);

		}, new UnAuthorizedTransactionRM());
	}

	public List<UnAuthorizedTransaction> getHoldDisbursement() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.FinReference, t1.LastMntOn, t1.LastMntBy, t1.RecordStatus, t1.RoleCode");
		sql.append(", t1.NextRoleCode, t2.FinType, t2.RcdMaintainSts, t3.CustCIF, t3.CustShrtName");
		sql.append(", t4.UsrLogin, t8.BranchCode, t8.BranchDesc");
		sql.append(", t7.EntityCode, t6.DivisionCode, t9.ProductCode, 0 TransactionAmount");
		sql.append(" From HoldDisbursement_Temp t1");
		sql.append(" Inner Join FinanceMain t2 on t2.FinReference = t1.FinReference");
		sql.append(" Inner Join Customers t3 on t3.CustID = t2.CustID");
		sql.append(" Inner Join SecUsers t4 on t4.UsrID = t1.LastMntBy");
		sql.append(" Inner Join RMTFinanceTypes t5 On t5.FinType = t2.FinType");
		sql.append(" Inner Join SmtDivisionDetail t6 On t6.DivisionCode = t5.FinDivision");
		sql.append(" Inner Join Entity t7 on t7.EntityCode = t6.EntityCode");
		sql.append(" Inner Join RMTBranches t8 on t8.BranchCode = t2.FinBranch");
		sql.append(" Inner Join BMTProduct t9 on t9.ProductCode = t2.FinCategory");
		sql.append(" Where t1.RecordStatus not in (?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, PennantConstants.RCD_STATUS_REJECTED);
			ps.setString(2, PennantConstants.RCD_STATUS_CANCELLED);

		}, new UnAuthorizedTransactionRM());
	}

	public List<UnAuthorizedTransaction> getUploadHeader() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.LastMntOn, t1.LastMntBy, t1.RecordStatus, t1.RoleCode, t1.NextRoleCode");
		sql.append(", t3.FinType, t3.FinReference, t3.RcdMaintainSts, t4.CustCIF, t4.CustShrtName");
		sql.append(", t5.UsrLogin, t9.BranchCode, t9.BranchDesc");
		sql.append(", t8.EntityCode, t7.DivisionCode, t10.ProductCode, 0 TransactionAmount");
		sql.append(" From UploadHeader_Temp t1");
		sql.append(" Inner Join miscpostinguploads t2 on t2.UploadID = t1.UploadID");
		sql.append(" Inner Join FinanceMain t3 on t3.FinReference = t2.Reference");
		sql.append(" Inner Join Customers t4 on t4.CustID = t3.CustID");
		sql.append(" Inner Join SecUsers t5 on t5.UsrID = t1.LastMntBy");
		sql.append(" Inner Join RMTFinanceTypes t6 On t6.FinType = t3.FinType");
		sql.append(" Inner Join SmtDivisionDetail t7 On t7.DivisionCode = t6.FinDivision");
		sql.append(" Inner Join Entity t8 on t8.EntityCode = t7.EntityCode");
		sql.append(" Inner Join RMTBranches t9 on t9.BranchCode = t3.FinBranch");
		sql.append(" Inner Join BMTProduct t10 on t10.ProductCode = t3.FinCategory");
		sql.append(" Where t1.RecordStatus not in (?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, PennantConstants.RCD_STATUS_REJECTED);
			ps.setString(2, PennantConstants.RCD_STATUS_CANCELLED);

		}, new UnAuthorizedTransactionRM());
	}

	public List<UnAuthorizedTransaction> getFeeWaiverDetail() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" t1.FinReference, t1.LastMntOn, t1.LastMntBy, t1.RecordStatus");
		sql.append(", t1.RoleCode, t1.NextRoleCode, t2.FinType, t2.RcdMaintainSts");
		sql.append(", t3.CustCIF, t3.CustShrtName, t4.UsrLogin, t8.BranchCode, t8.BranchDesc");
		sql.append(", t7.EntityCode, t6.DivisionCode, t9.ProductCode, 0 TransactionAmount");
		sql.append(" From FeeWaiverHeader_Temp t1");
		sql.append(" Inner Join FinanceMain t2 on t2.FinReference = t1.FinReference");
		sql.append(" Inner Join Customers t3 on t3.CustID = t2.CustID");
		sql.append(" Inner Join SecUsers t4 on t4.UsrID = t1.LastMntBy");
		sql.append(" Inner Join RMTFinanceTypes t5 On t5.FinType = t2.FinType");
		sql.append(" Inner Join SmtDivisionDetail t6 On t6.DivisionCode = t5.FinDivision");
		sql.append(" Inner Join Entity t7 on t7.EntityCode = t6.EntityCode");
		sql.append(" Inner Join RMTBranches t8 on t8.BranchCode = t2.FinBranch");
		sql.append(" Inner Join BMTProduct t9 on t9.ProductCode = t2.FinCategory");
		sql.append(" Where t1.RecordStatus not in (?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, PennantConstants.RCD_STATUS_REJECTED);
			ps.setString(2, PennantConstants.RCD_STATUS_CANCELLED);

		}, new UnAuthorizedTransactionRM());
	}

	private class UnAuthorizedTransactionRM implements RowMapper<UnAuthorizedTransaction> {
		public UnAuthorizedTransactionRM() {
			super();
		}

		@Override
		public UnAuthorizedTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
			UnAuthorizedTransaction uats = new UnAuthorizedTransaction();

			uats.setCustCIF(rs.getString("CustCIF"));
			uats.setCustShrtName(rs.getString("CustShrtName"));
			uats.setFinType(rs.getString("FinType"));
			uats.setFinReference(rs.getString("FinReference"));
			uats.setEvent(rs.getString("RcdMaintainSts"));
			uats.setLastMntOn(rs.getTimestamp("LastMntOn"));
			uats.setLastMntBy(rs.getLong("LastMntBy"));
			uats.setMakerName(rs.getString("UsrLogin"));
			uats.setStage(rs.getString("RecordStatus"));
			uats.setCurrentRole(rs.getString("NextRoleCode"));
			uats.setPreviousRole(rs.getString("RoleCode"));
			uats.setBranchCode(rs.getString("BranchCode"));
			uats.setBranchName(rs.getString("BranchDesc"));
			uats.setEntity(rs.getString("EntityCode"));
			uats.setDivision(rs.getString("DivisionCode"));
			uats.setProduct(rs.getString("ProductCode"));
			uats.setTransactionAmount(rs.getBigDecimal("TransactionAmount"));

			return uats;
		}
	}

}
