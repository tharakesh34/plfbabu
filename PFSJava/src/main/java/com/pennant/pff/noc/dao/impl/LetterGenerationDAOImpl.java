package com.pennant.pff.noc.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.pff.noc.dao.GenerateLetterDAO;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennanttech.pff.receipt.constants.Allocation;

public class LetterGenerationDAOImpl extends SequenceDao<GenerateLetter> implements GenerateLetterDAO {

	public LetterGenerationDAOImpl() {
		super();
	}

	@Override
	public List<GenerateLetter> getResult(ISearch search, List<String> roleCodes) {
		List<Object> value = new ArrayList<>();

		String whereCondition = QueryUtil.buildWhereClause(search, value);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinID, LetterType, Finreference, CustAcctHolderName");
		sql.append(", CustCoreBank, FinBranch, Product");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From (");
		sql.append(getSqlQuery(TableType.TEMP_TAB));
		sql.append(" Union All ");
		sql.append(getSqlQuery(TableType.MAIN_TAB));
		sql.append(" Where not exists (Select 1 From Letter_Generate_Manual_Temp Where Id = gl.Id)) gl");

		if (!StringUtils.isEmpty(whereCondition)) {
			sql.append(whereCondition);
			sql.append(" and (NextRoleCode is null or NextRoleCode = '' or NextRoleCode in (");
		} else {
			sql.append(" Where (NextRoleCode is null or NextRoleCode = '' or NextRoleCode in (");
		}

		sql.append(JdbcUtil.getInCondition(roleCodes));
		sql.append("))");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			for (Object object : value) {
				ps.setObject(++index, object);
			}

			for (String roleCode : roleCodes) {
				ps.setString(++index, roleCode);
			}
		}, new GenerateLetterRM());
	}

	@Override
	public List<ReportListDetail> getPrintLetters(List<String> roleCodes) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Finreference, CustAcctHolderName, CustCoreBank, FinBranch, Product, LetterType");
		sql.append(" From (Select fm.Finreference, cu.Custshrtname, cu.CustCoreBank, fm.FinBranch");
		sql.append(", ft.fintypedesc, LetterType");
		sql.append(" From Letter_Generate_Manual_Temp gl");
		sql.append(" Left Join FinanceMain fm on fm.Finreference = gl.Finreference");
		sql.append(" Left Join RMTFinancetypes ft on ft.fintype = fm.finType");
		sql.append(" Left Join customers cu on cu.custID = fm.CustID");
		sql.append(" Union All ");
		sql.append(" Select fm.Finreference, cu.custshrtname, cu.CustCoreBank, fm.FinBranch");
		sql.append(" lt.fintypedesc, LetterType");
		sql.append(" From Letter_Generate_Manual gl");
		sql.append(" Left Join FinanceMain fm on fm.Finreference = gl.Finreference");
		sql.append(" Left Join RMTFinancetypes ft on ft.fintype = fm.finType");
		sql.append(" Left Join customers cu on cu.custID = fm.CustID");
		sql.append(" Where csb.NextRoleCode is null or csb.NextRoleCode = ? or csb.NextRoleCode in (");
		sql.append(JdbcUtil.getInCondition(roleCodes));
		sql.append(") Order By Code)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, "");
			for (String roleCode : roleCodes) {
				ps.setString(++index, roleCode);
			}
		}, new ReportListRM());

	}

	@Override
	public GenerateLetter getLetter(long id) {
		StringBuilder sql = getSqlQuery(TableType.TEMP_TAB);
		sql.append(" Where gl.Id = ?");
		sql.append(" Union all ");
		sql.append(getSqlQuery(TableType.MAIN_TAB));
		sql.append(" Where gl.Id = ?");
		sql.append(" and not exists (Select 1 From Letter_Generate_Manual_Temp Where Id = gl.Id)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			GenerateLetter csb = this.jdbcOperations.queryForObject(sql.toString(), new GenerateLetterRM(), id, id);
			return csb;
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}

	}

	@Override
	public List<GenerateLetter> getGenerateLetters(List<String> roleCodes) {
		StringBuilder sql = new StringBuilder("select");
		sql.append(" ID, FinID, LetterType, Finreference, CustAcctHolderName");
		sql.append(", CustCoreBank, FinBranch, Product, LetterType");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn");
		sql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From (");
		sql.append(getSqlQuery(TableType.TEMP_TAB));
		sql.append(" Union All ");
		sql.append(getSqlQuery(TableType.MAIN_TAB));
		sql.append(" Where not exists (Select 1 From Letter_Generate_Manual_Temp Where ID = gl.ID)) p");
		sql.append(" Where NextRoleCode is null or NextRoleCode = ? or NextRoleCode in (");
		sql.append(JdbcUtil.getInCondition(roleCodes));
		sql.append(") order by Id");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 0;

			ps.setString(++index, "");
			for (String roleCode : roleCodes) {
				ps.setString(++index, roleCode);
			}
		}, new GenerateLetterRM());

	}

	@Override
	public boolean isReferenceExist(String reference) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select count(FinID) from FinanceMain");
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, reference) > 0;
	}

	private class GenerateLetterRM implements RowMapper<GenerateLetter> {

		private GenerateLetterRM() {
			super();
		}

		@Override
		public GenerateLetter mapRow(ResultSet rs, int rowNum) throws SQLException {
			GenerateLetter sb = new GenerateLetter();

			sb.setId(rs.getLong("ID"));
			sb.setFinID(rs.getLong("FinID"));
			sb.setLetterType(rs.getString("LetterType"));
			sb.setFinReference(rs.getString("Finreference"));
			sb.setCustAcctHolderName(rs.getString("CustAcctHolderName"));
			sb.setCustCoreBank(rs.getString("CustCoreBank"));
			sb.setFinBranch(rs.getString("FinBranch"));
			sb.setProduct(rs.getString("Product"));
			sb.setVersion(rs.getInt("Version"));
			sb.setCreatedBy(rs.getLong("CreatedBy"));
			sb.setCreatedOn(rs.getTimestamp("CreatedOn"));
			sb.setApprovedBy(rs.getLong("ApprovedBy"));
			sb.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			sb.setLastMntBy(rs.getLong("LastMntBy"));
			sb.setLastMntOn(rs.getTimestamp("LastMntOn"));
			sb.setRecordStatus(rs.getString("RecordStatus"));
			sb.setRoleCode(rs.getString("RoleCode"));
			sb.setNextRoleCode(rs.getString("NextRoleCode"));
			sb.setTaskId(rs.getString("TaskId"));
			sb.setNextTaskId(rs.getString("NextTaskId"));
			sb.setRecordType(rs.getString("RecordType"));
			sb.setWorkflowId(rs.getLong("WorkflowId"));

			return sb;
		}
	}

	private StringBuilder getSqlQuery(TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" gl.Id, gl.FinID, gl.lettertype, fm.Finreference, cu.Custshrtname CustAcctHolderName");
		sql.append(", cu.CustCoreBank, fm.FinBranch, ft.fintypedesc Product");
		sql.append(", gl.Version, gl.CreatedBy, gl.CreatedOn, gl.ApprovedBy, gl.ApprovedOn");
		sql.append(", gl.LastMntBy, gl.LastMntOn, gl.RecordStatus, gl.RoleCode");
		sql.append(", gl.NextRoleCode, gl.TaskId, gl.NextTaskId, gl.RecordType, gl.WorkflowId");
		sql.append(" From Letter_Generate_Manual").append(tableType.getSuffix()).append(" gl");
		sql.append(" Left Join FinanceMain fm on fm.FinID = gl.FinID");
		sql.append(" Left Join RMTFinancetypes ft on ft.fintype = fm.finType");
		sql.append(" Left Join customers cu on cu.custID = fm.CustID");

		return sql;
	}

	private class ReportListRM implements RowMapper<ReportListDetail> {

		private ReportListRM() {
			super();
		}

		@Override
		public ReportListDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
			ReportListDetail bc = new ReportListDetail();

			bc.setfieldString01(rs.getString("Finreference"));
			bc.setfieldString02(rs.getString("CustAcctHolderName"));
			bc.setfieldString03(rs.getString("CustCoreBank"));
			bc.setfieldString04(rs.getString("FinBranch"));
			bc.setfieldString04(rs.getString("Product"));
			bc.setfieldString04(rs.getString("LetterType"));

			return bc;
		}
	}

	@Override
	public List<ReceiptAllocationDetail> getPrinAndPftWaiver(String finReference) {
		long id = getEarltSettleReceipt(finReference);
		if (id == 0) {
			return new ArrayList<>();
		}

		String sql = "Select WaivedAmount,AllocationType From  ReceiptAllocationDetail Where ReceiptId = ? and AllocationType in (?, ?, ?, ?)";

		logger.debug(Literal.SQL.concat(sql));

		return jdbcOperations.query(sql, (rs, rowNum) -> {
			ReceiptAllocationDetail ra = new ReceiptAllocationDetail();

			ra.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			ra.setAllocationType(rs.getString("AllocationType"));

			return ra;
		}, id, Allocation.PRI, Allocation.PFT, Allocation.FUT_PRI, Allocation.FUT_PFT);
	}

	private long getEarltSettleReceipt(String reference) {
		String sql = "Select ReceiptId From  FinReceiptHeader Where Reference = ? and ReceiptPurpose = ? and ReceiptModeStatus = ?";

		logger.debug(Literal.SQL.concat(sql));

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, reference, "EarlySettlement", "R");
		} catch (Exception e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	public List<GenerateLetter> getLetterInfo(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" GeneratedDate, ModeOfTransfer, RequestType, LetterType");
		sql.append(", GeneratedBy, ll.ApprovedBy, ll.ApprovedOn");
		sql.append(", Status, Remarks, EmailID, FileName");
		sql.append(", CourierAgency, DispatchDate, DeliveryDate, DeliveryStatus");
		sql.append(", su.UsrLogin ApproverName");
		sql.append(" From Loan_Letters ll");
		sql.append(" Left Join SecUsers su on su.UsrId = ll.ApprovedBy");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			GenerateLetter letter = new GenerateLetter();

			letter.setGeneratedBy(JdbcUtil.getLong(rs.getObject("GeneratedBy")));
			letter.setApprovedBy(JdbcUtil.getLong(rs.getObject("ApprovedBy")));
			letter.setGeneratedDate(rs.getDate("GeneratedDate"));
			letter.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			letter.setModeofTransfer(rs.getString("ModeOfTransfer"));
			letter.setRequestType(rs.getString("RequestType"));
			letter.setLetterType(rs.getString("LetterType"));
			letter.setStatus(rs.getString("Status"));
			letter.setRemarks(rs.getString("Remarks"));
			letter.setEmailID(rs.getString("EmailID"));
			letter.setFileName(rs.getString("FileName"));
			letter.setCourierAgency(rs.getString("CourierAgency"));
			letter.setDispatchDate(rs.getDate("DispatchDate"));
			letter.setDeliveryDate(rs.getDate("DeliveryDate"));
			letter.setDeliveryStatus(rs.getString("DeliveryStatus"));
			letter.setApproverName(rs.getString("ApproverName"));

			return letter;
		}, finID);
	}

	@Override
	public long save(GenerateLetter gl, TableType type) {
		if (gl.getId() == 0 || gl.getId() == Long.MIN_VALUE) {
			gl.setId(getNextValue("SEQ_LOAN_LETTER_MANUAL"));
		}

		StringBuilder sql = new StringBuilder("Insert Into LOAN_LETTER_MANUAL");
		sql.append(type.getSuffix());
		sql.append("(LetterType, FinID");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;

				ps.setString(++index, gl.getLetterType());
				ps.setLong(++index, gl.getFinID());
				ps.setInt(++index, gl.getVersion());
				ps.setLong(++index, gl.getCreatedBy());
				ps.setDate(++index, JdbcUtil.getDate(gl.getCreatedOn()));
				ps.setObject(++index, gl.getApprovedBy());
				ps.setTimestamp(++index, gl.getApprovedOn());
				ps.setLong(++index, gl.getLastMntBy());
				ps.setTimestamp(++index, gl.getLastMntOn());
				ps.setString(++index, gl.getRecordStatus());
				ps.setString(++index, gl.getRoleCode());
				ps.setString(++index, gl.getNextRoleCode());
				ps.setString(++index, gl.getTaskId());
				ps.setString(++index, gl.getNextTaskId());
				ps.setString(++index, gl.getRecordType());
				ps.setLong(++index, gl.getWorkflowId());

			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return gl.getId();
	}

	@Override
	public void update(GenerateLetter gl, TableType type) {
		StringBuilder sql = new StringBuilder("Update Letter_Generate_Manual");
		sql.append(type.getSuffix());
		sql.append(" Set LetterType = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 0;

				ps.setString(++index, gl.getLetterType());
				ps.setInt(++index, gl.getVersion());
				ps.setLong(++index, gl.getLastMntBy());
				ps.setTimestamp(++index, gl.getLastMntOn());
				ps.setString(++index, gl.getRecordStatus());
				ps.setString(++index, gl.getRoleCode());
				ps.setString(++index, gl.getNextRoleCode());
				ps.setString(++index, gl.getTaskId());
				ps.setString(++index, gl.getNextTaskId());
				ps.setString(++index, gl.getRecordType());
				ps.setLong(++index, gl.getWorkflowId());

				ps.setLong(++index, gl.getId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

	}

	@Override
	public void delete(GenerateLetter gl, TableType type) {
		StringBuilder sql = new StringBuilder("Delete From Letter_Generate_Manual");
		sql.append(type.getSuffix());
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			if (this.jdbcOperations.update(sql.toString(), gl.getId()) == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public List<GenerateLetter> getLoanLetterInfo(long finID, String letterType) {
		String sql = "Select DeliveryStatus From LOAN_LETTERS Where FinID = ? and LetterType = ? and Generated = ? order by ID Desc";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.query(sql, (rs, rowNum) -> {
			GenerateLetter fm = new GenerateLetter();

			fm.setDeliveryStatus(rs.getString("DeliveryStatus"));

			return fm;
		}, finID, letterType, 1);
	}

	@Override
	public long getInitiatedLoan(long finID, String letterType) {
		return finID;
	}

	@Override
	public boolean letterIsInQueu(long finID, String letterType) {
		return false;
	}

	@Override
	public void deleteAutoLetterGeneration(long finID, String letterType) {
		//
	}

	@Override
	public String getReasonCode(long finID) {
		return null;
	}

	@Override
	public String getCancelReasons(String reference) {
		return null;
	}

	@Override
	public void updateAutoLetterGeneration(long finID, String letterType) {
		// TODO Auto-generated method stub

	}
}
