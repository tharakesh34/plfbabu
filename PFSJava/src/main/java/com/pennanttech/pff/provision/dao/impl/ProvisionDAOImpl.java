package com.pennanttech.pff.provision.dao.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.provision.dao.ProvisionDAO;
import com.pennanttech.pff.provision.model.Provision;
import com.pennanttech.pff.provision.model.ProvisionRuleData;

public class ProvisionDAOImpl extends SequenceDao<Provision> implements ProvisionDAO {

	@Override
	public void deleteQueue() {
		jdbcOperations.update("Truncate table Provision_Calc_Queue");
	}

	@Override
	public long prepareQueueForSOM() {
		StringBuilder sql = new StringBuilder("Insert Into Provision_Calc_Queue(ID, FinReference)");
		sql.append(" Select row_number() over(order by lp.FinReference) ID, lp.FinReference");
		sql.append(" From Loan_Provisions lp");
		sql.append(" Inner Join FinanceMain fm on fm.FinReference = lp.FinReference and fm.FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.update(sql.toString(), 1);
	}

	@Override
	public long prepareQueueForEOM() {
		String sql = "Insert Into Provision_Calc_Queue(ID, FinReference) Select row_number() over(order by FinReference) ID, FinReference From (Select distinct FinReference From Npa_Provision_Stage) T";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.update(sql);
	}

	@Override
	public long getQueueCount() {
		String sql = "Select count(ID) From Provision_Calc_Queue where Progress = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Long.class, EodConstants.PROGRESS_WAIT);
	}

	@Override
	public int updateThreadID(long from, long to, int threadId) {
		String sql = "Update Provision_Calc_Queue Set ThreadId = ? Where Id > ? and Id <= ?  and ThreadId = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.update(sql, threadId, from, to, 0);
		} catch (DataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
		}

		return 0;
	}

	@Override
	public void updateProgress(String finReference, int progress) {
		String sql = null;
		if (progress == EodConstants.PROGRESS_IN_PROCESS) {
			sql = "Update Provision_Calc_Queue Set Progress = ?, StartTime = ? Where FinReference = ? and Progress = ?";

			logger.debug(Literal.SQL + sql);

			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, progress);
				ps.setDate(2, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setString(3, finReference);
				ps.setInt(4, EodConstants.PROGRESS_WAIT);
			});
		} else if (progress == EodConstants.PROGRESS_SUCCESS) {
			sql = "Update Provision_Calc_Queue Set EndTime = ?, Progress = ? where FinReference = ?";

			logger.debug(Literal.SQL + sql);

			this.jdbcOperations.update(sql, ps -> {
				ps.setDate(1, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setInt(2, EodConstants.PROGRESS_SUCCESS);
				ps.setString(3, finReference);
			});
		} else if (progress == EodConstants.PROGRESS_FAILED) {
			sql = "Update Provision_Calc_Queue Set EndTime = ?, ThreadId = ?, Progress = ? Where FinReference = ?";

			logger.debug(Literal.SQL + sql);

			this.jdbcOperations.update(sql, ps -> {
				ps.setDate(1, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setInt(2, 0);
				ps.setInt(3, EodConstants.PROGRESS_WAIT);
				ps.setString(4, finReference);

			});
		}
	}

	@Override
	public Long getLinkedTranId(String finReference) {
		String sql = "Select LinkedTranID From Loan_Provisions Where FinReference = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	@Override
	public ProvisionRuleData getProvisionData(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" npa.FinID, npa.FinReference, nps.FinType, nps.Product, nps.CustCategoryCode");
		sql.append(", nps.FinAssetValue, nps.FinCurrAssetValue, nps.OsPrincipal, nps.OsProfit, nps.FuturePrincipal");
		sql.append(", nps.OdPrincipal, nps.OdProfit, nps.TotPriBal, nps.TotPriPaid");
		sql.append(", nps.TotPftPaid, nps.TotPftAccrued, nps.AmzTillLBDate, nps.TillDateSchdPri");
		sql.append(", npa.PastDueDays, npa.NpaPastDueDays");
		sql.append(", npa.EffNpaPastDueDays, npa.NpaStage, npa.EffNpaStage");
		sql.append(", npa.NpaClassID, acc.Code NpaClassCode, ascc.Code NpaSubClassCode");
		sql.append(", npa.EffNpaClassID, eacc.Code EffNpaClassCode, eascc.Code EffNpaSubClassCode");
		sql.append(", fm.Restructure RestrutureLoan, 0 repossessedLoan");
		sql.append(", regProvnR.SqlRule RegProvsnRule, intProvnR.SqlRule IntProvsnRule");
		sql.append(", nps.CustID, nps.EntityCode, nps.FinCCY, nps.FinBranch");
		sql.append(", acsd.NpaAge NpaAge, eacsd.NpaAge EffNpaAge");
		sql.append(" From Npa_Loan_Info npa");
		sql.append(" Inner Join Npa_Provision_Stage nps on nps.FinID = npa.FinID");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = nps.FinID");
		sql.append(" Inner Join RmtFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" Left Join Rules regProvnR on regProvnR.RuleID = ft.RegProvRule");
		sql.append(" Left Join Rules intProvnR on intProvnR.RuleID = ft.IntProvRule");
		sql.append(" Inner Join Asset_Class_Setup_Details acsd on acsd.id = npa.NpaClassId");
		sql.append(" Inner Join Asset_Class_Codes acc on acc.id = acsd.classId");
		sql.append(" Inner Join Asset_Sub_Class_Codes ascc on ascc.id = acsd.subClassId");
		sql.append(" Inner Join Asset_Class_Setup_Details eacsd on eacsd.id = npa.EffNpaClassId");
		sql.append(" Inner Join Asset_Class_Codes eacc on eacc.id = eacsd.classId");
		sql.append(" Inner Join Asset_Sub_Class_Codes eascc on eascc.id = eacsd.subClassId");
		sql.append(" Where nps.FinReference = ? and nps.LinkedLoan = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				ProvisionRuleData data = new ProvisionRuleData();

				data.setFinID(rs.getLong("FinID"));
				data.setFinReference(rs.getString("FinReference"));
				data.setFinType(rs.getString("FinType"));
				data.setProductCategory(rs.getString("Product"));
				data.setCustCategory(rs.getString("CustCategoryCode"));
				data.setPastDueDays(rs.getInt("PastDueDays"));
				data.setNpaPastDueDays(rs.getInt("NpaPastDueDays"));
				data.setEffNpaPastDueDays(rs.getInt("EffNpaPastDueDays"));
				data.setNpaStage(rs.getBoolean("NpaStage"));
				data.setEffNpaStage(rs.getBoolean("EffNpaStage"));
				data.setNpaClassID(JdbcUtil.getLong(rs.getObject("NpaClassID")));
				data.setNpaClassCode(rs.getString("NpaClassCode"));
				data.setNpaSubClassCode(rs.getString("NpaSubClassCode"));
				data.setEffNpaClassID(JdbcUtil.getLong(rs.getObject("EffNpaClassID")));
				data.setEffNpaClassCode(rs.getString("EffNpaClassCode"));
				data.setEffNpaSubClassCode(rs.getString("EffNpaSubClassCode"));
				data.setRestrutureLoan(rs.getBoolean("RestrutureLoan"));
				data.setOutstandingprincipal(rs.getBigDecimal("OsPrincipal"));
				data.setOsProfit(rs.getBigDecimal("OsProfit"));
				data.setOdPrincipal(rs.getBigDecimal("OdPrincipal"));
				data.setOdProfit(rs.getBigDecimal("OdProfit"));
				data.setLoanAmount(rs.getBigDecimal("FinAssetValue"));
				data.setDisbursedAmount(rs.getBigDecimal("FinCurrAssetValue"));
				data.setOverdueEMI(rs.getBigDecimal("OdPrincipal").add(rs.getBigDecimal("OdProfit")));
				data.setRegProvsnRule(rs.getString("RegProvsnRule"));
				data.setIntProvsnRule(rs.getString("IntProvsnRule"));
				data.setTotPftAccrued(rs.getBigDecimal("TotPftAccrued"));
				data.setTillDateSchdPri(rs.getBigDecimal("TillDateSchdPri"));
				data.setCustID(rs.getLong("CustID"));
				data.setEntityCode(rs.getString("EntityCode"));
				data.setFinCCY(rs.getString("FinCCY"));
				data.setFinBranch(rs.getString("FinBranch"));
				data.setNpaAge(rs.getInt("NpaAge"));
				data.setEffNpaAge(rs.getInt("EffNpaAge"));

				return data;

			}, finReference, 0);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	@Override
	public Provision getProvision(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinID, FinReference");
		sql.append(", ProvisionDate, ManualProvision, RegProvsnPer, RegProvsnAmt, RegSecProvsnPer");
		sql.append(", RegSecProvsnAmt, RegUnSecProvsnPer, RegUnSecProvsnAmt, TotRegProvsnAmt, IntProvsnPer");
		sql.append(", IntProvsnAmt, IntSecProvsnPer, IntSecProvsnAmt, IntUnSecProvsnPer, IntUnSecProvsnAmt");
		sql.append(", TotIntProvsnAmt, ManProvsnPer, ManProvsnAmt, PastDueDays, NpaAging, EffNpaAging, NpaPastDueDays");
		sql.append(", EffNpaPastDueDays, NpaClassID, EffNpaClassID, OsPrincipal, OSProfit, OdPrincipal, OdProfit");
		sql.append(", ProfitAccruedAndDue, ProfitAccruedAndNotDue, CollateralAmt, InsuranceAmt, LinkedTranId");
		sql.append(", ChgLinkedTranId, Version, CreatedOn");
		sql.append(" From Loan_Provisions");
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				Provision p = new Provision();

				p.setId(rs.getLong("Id"));
				p.setFinID(rs.getLong("FinID"));
				p.setFinReference(rs.getString("FinReference"));
				p.setProvisionDate(rs.getDate("ProvisionDate"));
				p.setManualProvision(rs.getBoolean("ManualProvision"));
				p.setRegProvsnPer(rs.getBigDecimal("RegProvsnPer"));
				p.setRegProvsnAmt(rs.getBigDecimal("RegProvsnAmt"));
				p.setRegSecProvsnPer(rs.getBigDecimal("RegSecProvsnPer"));
				p.setRegSecProvsnAmt(rs.getBigDecimal("RegSecProvsnAmt"));
				p.setRegUnSecProvsnPer(rs.getBigDecimal("RegUnSecProvsnPer"));
				p.setRegUnSecProvsnAmt(rs.getBigDecimal("RegUnSecProvsnAmt"));
				p.setTotRegProvsnAmt(rs.getBigDecimal("TotRegProvsnAmt"));
				p.setIntProvsnPer(rs.getBigDecimal("IntProvsnPer"));
				p.setIntProvsnAmt(rs.getBigDecimal("IntProvsnAmt"));
				p.setIntSecProvsnPer(rs.getBigDecimal("IntSecProvsnPer"));
				p.setIntSecProvsnAmt(rs.getBigDecimal("IntSecProvsnAmt"));
				p.setIntUnSecProvsnPer(rs.getBigDecimal("IntUnSecProvsnPer"));
				p.setIntUnSecProvsnAmt(rs.getBigDecimal("IntUnSecProvsnAmt"));
				p.setTotIntProvsnAmt(rs.getBigDecimal("TotIntProvsnAmt"));
				p.setManProvsnPer(rs.getBigDecimal("ManProvsnPer"));
				p.setManProvsnAmt(rs.getBigDecimal("ManProvsnAmt"));
				p.setPastDueDays(rs.getInt("PastDueDays"));
				p.setNpaAging(rs.getInt("NpaAging"));
				p.setEffNpaAging(rs.getInt("EffNpaAging"));
				p.setNpaPastDueDays(rs.getInt("NpaPastDueDays"));
				p.setEffNpaPastDueDays(rs.getInt("EffNpaPastDueDays"));
				p.setNpaClassID(rs.getLong("NpaClassID"));
				p.setEffNpaClassID(rs.getLong("EffNpaClassID"));
				p.setOsPrincipal(rs.getBigDecimal("OsPrincipal"));
				p.setOsProfit(rs.getBigDecimal("OSProfit"));
				p.setOdPrincipal(rs.getBigDecimal("OdPrincipal"));
				p.setOdProfit(rs.getBigDecimal("OdProfit"));
				p.setTotPftAccrued(rs.getBigDecimal("ProfitAccruedAndDue"));
				p.setTillDateSchdPri(rs.getBigDecimal("ProfitAccruedAndNotDue"));
				p.setCollateralAmt(rs.getBigDecimal("CollateralAmt"));
				p.setInsuranceAmt(rs.getBigDecimal("InsuranceAmt"));
				p.setLinkedTranId(rs.getLong("LinkedTranId"));
				p.setChgLinkedTranId(rs.getLong("ChgLinkedTranId"));
				p.setVersion(rs.getInt("Version"));
				p.setCreatedOn(rs.getTimestamp("CreatedOn"));

				return p;
			}, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	@Override
	public BigDecimal getCollateralValue(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Sum(cs.collateralValue) CollateralValue");
		sql.append(" From CollateralAssignment ca");
		sql.append(" Inner Join CollateralSetup cs ON ca.CollateralRef = cs.CollateralRef");
		sql.append(" Where ca.Reference = ?");
		sql.append(" Group by ca.Reference");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return BigDecimal.ZERO;
	}

	@Override
	public BigDecimal getVasFee(String finReference) {
		String sql = "Selec Sum(Fee, 0) Fee From VASRecording Where PrimaryLinkRef = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, BigDecimal.class, finReference);
		} catch (Exception e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return BigDecimal.ZERO;
	}

	@Override
	public void save(Provision p, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert Into Loan_Provisions");
		sql.append(tableType.getSuffix());
		sql.append(" (");
		if ("_Temp".equals(tableType.getSuffix())) {
			sql.append(" ID, ");
		}

		sql.append(" FinID, FinReference, ProvisionDate, ManualProvision");
		sql.append(", RegProvsnPer, RegProvsnAmt, RegSecProvsnPer, RegSecProvsnAmt, RegUnSecProvsnPer");
		sql.append(", RegUnSecProvsnAmt, TotRegProvsnAmt, IntProvsnPer, IntProvsnAmt");
		sql.append(", IntSecProvsnPer, IntSecProvsnAmt, IntUnSecProvsnPer, IntUnSecProvsnAmt, TotIntProvsnAmt");
		sql.append(", PastDueDays, NpaAging, EffNpaAging, NpaPastDueDays, EffNpaPastDueDays, NpaClassID");
		sql.append(", EffNpaClassID, ManProvsnPer, ManProvsnAmt, OsPrincipal, OSProfit, OdPrincipal, OdProfit");
		sql.append(", ProfitAccruedAndDue, ProfitAccruedAndNotDue, CollateralAmt, InsuranceAmt");
		sql.append(", LinkedTranId, ChgLinkedTranId, Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkFlowId)");
		sql.append(" Values (");

		if ("_Temp".equals(tableType.getSuffix())) {
			sql.append(" ?, ");
		}

		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			if ("_Temp".equals(tableType.getSuffix())) {
				ps.setLong(index++, p.getId());
			}

			ps.setObject(index++, p.getFinID());
			ps.setString(index++, p.getFinReference());
			ps.setDate(index++, JdbcUtil.getDate(p.getProvisionDate()));
			ps.setBoolean(index++, p.isManualProvision());
			ps.setBigDecimal(index++, p.getRegProvsnPer());
			ps.setBigDecimal(index++, p.getRegProvsnAmt());
			ps.setBigDecimal(index++, p.getRegSecProvsnPer());
			ps.setBigDecimal(index++, p.getRegSecProvsnAmt());
			ps.setBigDecimal(index++, p.getRegUnSecProvsnPer());
			ps.setBigDecimal(index++, p.getRegUnSecProvsnAmt());
			ps.setBigDecimal(index++, p.getTotRegProvsnAmt());
			ps.setBigDecimal(index++, p.getIntProvsnPer());
			ps.setBigDecimal(index++, p.getIntProvsnAmt());
			ps.setBigDecimal(index++, p.getIntSecProvsnPer());
			ps.setBigDecimal(index++, p.getIntSecProvsnAmt());
			ps.setBigDecimal(index++, p.getIntUnSecProvsnPer());
			ps.setBigDecimal(index++, p.getIntUnSecProvsnAmt());
			ps.setBigDecimal(index++, p.getTotIntProvsnAmt());
			ps.setInt(index++, p.getPastDueDays());
			ps.setInt(index++, p.getNpaAging());
			ps.setInt(index++, p.getEffNpaAging());
			ps.setInt(index++, p.getNpaPastDueDays());
			ps.setInt(index++, p.getEffNpaPastDueDays());
			ps.setObject(index++, p.getNpaClassID());
			ps.setObject(index++, p.getEffNpaClassID());
			ps.setBigDecimal(index++, p.getManProvsnPer());
			ps.setBigDecimal(index++, p.getManProvsnAmt());
			ps.setBigDecimal(index++, p.getOsPrincipal());
			ps.setBigDecimal(index++, p.getOsProfit());
			ps.setBigDecimal(index++, p.getOdPrincipal());
			ps.setBigDecimal(index++, p.getOdProfit());
			ps.setBigDecimal(index++, p.getTotPftAccrued());
			ps.setBigDecimal(index++, p.getTillDateSchdPri());
			ps.setBigDecimal(index++, p.getCollateralAmt());
			ps.setBigDecimal(index++, p.getInsuranceAmt());
			ps.setObject(index++, p.getLinkedTranId());
			ps.setObject(index++, p.getChgLinkedTranId());
			ps.setInt(index++, p.getVersion());
			ps.setObject(index++, JdbcUtil.getLong(p.getCreatedBy()));
			ps.setTimestamp(index++, p.getCreatedOn());
			ps.setObject(index++, JdbcUtil.getLong(p.getApprovedBy()));
			ps.setTimestamp(index++, p.getApprovedOn());
			ps.setLong(index++, JdbcUtil.getLong(p.getLastMntBy()));
			ps.setTimestamp(index++, p.getLastMntOn());
			ps.setString(index++, p.getRecordStatus());
			ps.setString(index++, p.getRoleCode());
			ps.setString(index++, p.getNextRoleCode());
			ps.setString(index++, p.getTaskId());
			ps.setString(index++, p.getNextTaskId());
			ps.setString(index++, p.getRecordType());
			ps.setLong(index, JdbcUtil.getLong(p.getWorkflowId()));
		});
	}

	@Override
	public void update(Provision p, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update Loan_Provisions");
		sql.append(tableType.getSuffix());
		sql.append(" Set ProvisionDate = ?");

		if (ImplementationConstants.PROVISION_REVERSAL_REQ) {
			sql.append(", RegProvsnPer = ?, RegProvsnAmt =  ?, RegSecProvsnPer = ?, RegSecProvsnAmt = ?");
			sql.append(", RegUnSecProvsnPer = ?, RegUnSecProvsnAmt = ?, TotRegProvsnAmt = ?");
			sql.append(", IntProvsnPer = ?, IntProvsnAmt = ?, IntSecProvsnPer = ?, IntSecProvsnAmt = ?");
			sql.append(", IntUnSecProvsnPer = ?, IntUnSecProvsnAmt = ?, TotIntProvsnAmt = ?");
			sql.append(", ManualProvision = ?, ManProvsnPer = ?, ManProvsnAmt = ?");
		} else {
			sql.append(", RegProvsnPer = ?, RegProvsnAmt = RegProvsnAmt + ?");
			sql.append(", RegSecProvsnPer = ?, RegSecProvsnAmt = RegSecProvsnAmt + ?");
			sql.append(", RegUnSecProvsnPer = ?, RegUnSecProvsnAmt = RegUnSecProvsnAmt + ?");
			sql.append(", TotRegProvsnAmt = TotRegProvsnAmt + ?");
			sql.append(", IntProvsnPer = ?, IntProvsnAmt = IntProvsnAmt + ?");
			sql.append(", IntSecProvsnPer = ?, IntSecProvsnAmt = IntSecProvsnAmt + ?");
			sql.append(", IntUnSecProvsnPer = ?, IntUnSecProvsnAmt = IntUnSecProvsnAmt + ?");
			sql.append(", TotIntProvsnAmt = TotIntProvsnAmt + ?");
			sql.append(", ManualProvision = ?, ManProvsnPer = ?, ManProvsnAmt = ManProvsnAmt + ?");
		}

		sql.append(", PastDueDays = ?, NpaAging = ?, EffNpaAging = ?");
		sql.append(", NpaPastDueDays = ?, EffNpaPastDueDays = ?, NpaClassID = ?, EffNpaClassID = ?");
		sql.append(", OsPrincipal = ?, OSProfit = ?, OdPrincipal = ?, OdProfit = ?");
		sql.append(", ProfitAccruedAndDue = ?, ProfitAccruedAndNotDue = ?");
		sql.append(", CollateralAmt = ?, InsuranceAmt = ?, LinkedTranId = ?, ChgLinkedTranId = ?");
		sql.append(", Version = ?, CreatedBy= ?, CreatedOn = ?, ApprovedBy = ? ");
		sql.append(", ApprovedOn = ?, LastMntBy = ?, LastMntOn= ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId= ?, RecordType = ?, WorkFlowId = ?");
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setDate(index++, JdbcUtil.getDate(p.getProvisionDate()));
			ps.setBigDecimal(index++, p.getRegProvsnPer());
			ps.setBigDecimal(index++, p.getRegProvsnAmt());
			ps.setBigDecimal(index++, p.getRegSecProvsnPer());
			ps.setBigDecimal(index++, p.getRegSecProvsnAmt());
			ps.setBigDecimal(index++, p.getRegUnSecProvsnPer());
			ps.setBigDecimal(index++, p.getRegUnSecProvsnAmt());
			ps.setBigDecimal(index++, p.getTotRegProvsnAmt());
			ps.setBigDecimal(index++, p.getIntProvsnPer());
			ps.setBigDecimal(index++, p.getIntProvsnAmt());
			ps.setBigDecimal(index++, p.getIntSecProvsnPer());
			ps.setBigDecimal(index++, p.getIntSecProvsnAmt());
			ps.setBigDecimal(index++, p.getIntUnSecProvsnPer());
			ps.setBigDecimal(index++, p.getIntUnSecProvsnAmt());
			ps.setBigDecimal(index++, p.getTotIntProvsnAmt());
			ps.setBoolean(index++, p.isManualProvision());
			ps.setBigDecimal(index++, p.getManProvsnPer());
			ps.setBigDecimal(index++, p.getManProvsnAmt());
			ps.setInt(index++, p.getPastDueDays());
			ps.setInt(index++, p.getNpaAging());
			ps.setInt(index++, p.getEffNpaAging());
			ps.setInt(index++, p.getNpaPastDueDays());
			ps.setInt(index++, p.getEffNpaPastDueDays());
			ps.setObject(index++, p.getNpaClassID());
			ps.setObject(index++, p.getEffNpaClassID());
			ps.setBigDecimal(index++, p.getOsPrincipal());
			ps.setBigDecimal(index++, p.getOsProfit());
			ps.setBigDecimal(index++, p.getOdPrincipal());
			ps.setBigDecimal(index++, p.getOdProfit());
			ps.setBigDecimal(index++, p.getTotPftAccrued());
			ps.setBigDecimal(index++, p.getTillDateSchdPri());
			ps.setBigDecimal(index++, p.getCollateralAmt());
			ps.setBigDecimal(index++, p.getInsuranceAmt());
			ps.setObject(index++, p.getLinkedTranId());
			ps.setObject(index++, p.getChgLinkedTranId());
			ps.setInt(index++, p.getVersion());
			ps.setObject(index++, JdbcUtil.getLong(p.getCreatedBy()));
			ps.setTimestamp(index++, p.getCreatedOn());
			ps.setObject(index++, JdbcUtil.getLong(p.getApprovedBy()));
			ps.setTimestamp(index++, p.getApprovedOn());
			ps.setLong(index++, JdbcUtil.getLong(p.getLastMntBy()));
			ps.setTimestamp(index++, p.getLastMntOn());
			ps.setString(index++, p.getRecordStatus());
			ps.setString(index++, p.getRoleCode());
			ps.setString(index++, p.getNextRoleCode());
			ps.setString(index++, p.getTaskId());
			ps.setString(index++, p.getNextTaskId());
			ps.setString(index++, p.getRecordType());
			ps.setLong(index++, JdbcUtil.getLong(p.getWorkflowId()));

			ps.setLong(index, p.getId());
		});
	}

	@Override
	public Provision getProvisionDetail(String finReference) {
		StringBuilder sql = new StringBuilder("Select * From (");
		sql.append(" Select lp.Id, c.CustID, c.CustCIF, c.CustShrtName");
		sql.append(", lp.FinID, lp.FinReference, fm.FinType, fm.FinAssetValue, fm.FinStartDate, fm.MaturityDate");
		sql.append(", lp.OsPrincipal, lp.OSProfit, lp.OdPrincipal, lp.OdProfit");
		sql.append(", lp.ProfitAccruedAndDue, lp.ProfitAccruedAndNotDue");
		sql.append(", lp.RegProvsnPer, lp.RegProvsnAmt, lp.RegSecProvsnPer, lp.RegSecProvsnAmt");
		sql.append(", lp.RegUnSecProvsnPer, lp.RegUnSecProvsnAmt, lp.TotRegProvsnAmt");
		sql.append(", lp.IntProvsnPer, lp.IntProvsnAmt, lp.IntSecProvsnPer, lp.IntSecProvsnAmt");
		sql.append(", lp.IntUnSecProvsnPer, lp.IntUnSecProvsnAmt, lp.TotIntProvsnAmt");
		sql.append(", lp.ManualProvision, lp.ManProvsnPer, lp.ManProvsnAmt");
		sql.append(", lp.Version, lp.CreatedBy");
		sql.append(", lp.CreatedOn, lp.ApprovedBy, lp.ApprovedOn, lp.LastMntBy, lp.LastMntOn");
		sql.append(", lp.RecordStatus, lp.RoleCode, lp.NextRoleCode");
		sql.append(", lp.TaskId, lp.NextTaskId, lp.RecordType, lp.WorkFlowId");
		sql.append(", acc.Code LoanClassification , eacc.Code EffectiveClassification");
		sql.append(", lp.PastDueDays, lp.NpaPastDueDays, lp.EffNpaPastDueDays");
		sql.append(", lp.NpaClassId, lp.EffNpaClassId, lp.ProvisionDate");
		sql.append(" From Loan_Provisions_Temp lp");
		sql.append(" Inner Join FinanceMain fm on fm.FinReference = lp.FinReference");
		sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
		sql.append(" Inner Join Asset_Class_Setup_Details acsd on acsd.ID = lp.NpaClassId");
		sql.append(" Inner Join Asset_Class_Codes acc on acc.ID = acsd.Classid");
		sql.append(" Left Join Asset_Class_Setup_Details eacsd on eacsd.ID = lp.EffNpaClassId");
		sql.append(" Left Join Asset_Class_Codes eacc on eacc.ID = eacsd.ClassId");
		sql.append(" Where lp.FinReference = ?");
		sql.append(" Union All");
		sql.append("  Select lp.Id,c.CustID, c.CustCIF, c.CustShrtName");
		sql.append(", lp.FinID, lp.FinReference, fm.FinType, fm.FinAssetValue, fm.FinStartDate, fm.MaturityDate");
		sql.append(", lp.OsPrincipal, lp.OSProfit, lp.OdPrincipal, lp.OdProfit");
		sql.append(", lp.ProfitAccruedAndDue, lp.ProfitAccruedAndNotDue");
		sql.append(", lp.RegProvsnPer, lp.RegProvsnAmt, lp.RegSecProvsnPer, lp.RegSecProvsnAmt");
		sql.append(", lp.RegUnSecProvsnPer, lp.RegUnSecProvsnAmt, lp.TotRegProvsnAmt");
		sql.append(", lp.IntProvsnPer, lp.IntProvsnAmt, lp.IntSecProvsnPer, lp.IntSecProvsnAmt");
		sql.append(", lp.IntUnSecProvsnPer, lp.IntUnSecProvsnAmt, lp.TotIntProvsnAmt");
		sql.append(", lp.ManualProvision, lp.ManProvsnPer, lp.ManProvsnAmt");
		sql.append(", lp.Version, lp.CreatedBy");
		sql.append(", lp.CreatedOn, lp.ApprovedBy, lp.ApprovedOn, lp.LastMntBy, lp.LastMntOn");
		sql.append(", lp.RecordStatus, lp.RoleCode, lp.NextRoleCode");
		sql.append(", lp.TaskId, lp.NextTaskId, lp.RecordType, lp.WorkFlowId");
		sql.append(", acc.Code LoanClassification , eacc.Code EffectiveClassification");
		sql.append(", lp.PastDueDays, lp.NpaPastDueDays, lp.EffNpaPastDueDays");
		sql.append(", lp.NpaClassId, lp.EffNpaClassId, lp.ProvisionDate");
		sql.append(" From Loan_Provisions lp");
		sql.append("  Inner Join FinanceMain fm on fm.FinReference = lp.FinReference");
		sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
		sql.append(" Inner Join Asset_Class_Setup_Details acsd on acsd.ID = lp.NpaClassId");
		sql.append(" Inner Join Asset_Class_Codes acc on acc.ID = acsd.Classid");
		sql.append(" Left Join Asset_Class_Setup_Details eacsd on eacsd.ID = lp.EffNpaClassId");
		sql.append(" Left Join Asset_Class_Codes eacc on eacc.ID = eacsd.ClassId");
		sql.append(" Where lp.FinReference = ? and Not Exists (Select 1 From Loan_Provisions_Temp Where ID = lp.ID)");
		sql.append(") T ");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				Provision p = new Provision();

				p.setId(rs.getLong("Id"));
				p.setCustID(rs.getLong("CustID"));
				p.setCustCIF(rs.getString("CustCIF"));
				p.setCustShrtName(rs.getString("CustShrtName"));
				p.setFinID(rs.getLong("FinID"));
				p.setFinReference(rs.getString("FinReference"));
				p.setFinType(rs.getString("FinType"));
				p.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
				p.setFinStartDate(rs.getDate("FinStartDate"));
				p.setMaturityDate(rs.getDate("MaturityDate"));
				p.setOsPrincipal(rs.getBigDecimal("OsPrincipal"));
				p.setOsProfit(rs.getBigDecimal("OSProfit"));
				p.setOdPrincipal(rs.getBigDecimal("OdPrincipal"));
				p.setOdProfit(rs.getBigDecimal("OdProfit"));
				p.setTotPftAccrued(rs.getBigDecimal("ProfitAccruedAndDue"));
				p.setTillDateSchdPri(rs.getBigDecimal("ProfitAccruedAndNotDue"));
				p.setRegProvsnPer(rs.getBigDecimal("RegProvsnPer"));
				p.setRegProvsnAmt(rs.getBigDecimal("RegProvsnAmt"));
				p.setRegSecProvsnPer(rs.getBigDecimal("RegSecProvsnPer"));
				p.setRegSecProvsnAmt(rs.getBigDecimal("RegSecProvsnAmt"));
				p.setRegUnSecProvsnPer(rs.getBigDecimal("RegUnSecProvsnPer"));
				p.setRegUnSecProvsnAmt(rs.getBigDecimal("RegUnSecProvsnAmt"));
				p.setTotRegProvsnAmt(rs.getBigDecimal("TotRegProvsnAmt"));
				p.setIntProvsnPer(rs.getBigDecimal("IntProvsnPer"));
				p.setIntProvsnAmt(rs.getBigDecimal("IntProvsnAmt"));
				p.setIntSecProvsnPer(rs.getBigDecimal("IntSecProvsnPer"));
				p.setIntSecProvsnAmt(rs.getBigDecimal("IntSecProvsnAmt"));
				p.setIntUnSecProvsnPer(rs.getBigDecimal("IntUnSecProvsnPer"));
				p.setIntUnSecProvsnAmt(rs.getBigDecimal("IntUnSecProvsnAmt"));
				p.setTotIntProvsnAmt(rs.getBigDecimal("TotIntProvsnAmt"));
				p.setManualProvision(rs.getBoolean("ManualProvision"));
				p.setManProvsnPer(rs.getBigDecimal("ManProvsnPer"));
				p.setManProvsnAmt(rs.getBigDecimal("ManProvsnAmt"));
				p.setLoanClassification(rs.getString("LoanClassification"));
				p.setEffectiveClassification(rs.getString("EffectiveClassification"));
				p.setPastDueDays(rs.getInt("PastDueDays"));
				p.setNpaPastDueDays(rs.getInt("NpaPastDueDays"));
				p.setEffNpaPastDueDays(rs.getInt("EffNpaPastDueDays"));
				p.setNpaClassID(rs.getLong("NpaClassId"));
				p.setEffNpaClassID(rs.getLong("EffNpaClassId"));
				p.setProvisionDate(rs.getTimestamp("ProvisionDate"));
				p.setVersion(rs.getInt("Version"));
				p.setCreatedBy(JdbcUtil.getLong(rs.getObject("CreatedBy")));
				p.setCreatedOn(rs.getTimestamp("CreatedOn"));
				p.setApprovedBy(JdbcUtil.getLong(rs.getObject("ApprovedBy")));
				p.setApprovedOn(rs.getTimestamp("ApprovedOn"));
				p.setLastMntBy(JdbcUtil.getLong(rs.getObject("LastMntBy")));
				p.setLastMntOn(rs.getTimestamp("LastMntOn"));
				p.setRecordStatus(rs.getString("RecordStatus"));
				p.setRoleCode(rs.getString("RoleCode"));
				p.setNextRoleCode(rs.getString("NextRoleCode"));
				p.setTaskId(rs.getString("TaskId"));
				p.setNextTaskId(rs.getString("NextTaskId"));
				p.setRecordType(rs.getString("RecordType"));
				p.setWorkflowId(rs.getLong("WorkFlowId"));

				return p;
			}, finReference, finReference);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	@Override
	public List<Date> getProvisionDates() {
		String sql = "Select distinct ProvisionDate From Loan_Provisions Order By ProvisionDate desc";

		logger.debug(Literal.SQL + sql);

		List<Date> provisionDates = new ArrayList<>();

		this.jdbcOperations.query(sql, (rs, rowNum) -> {
			if (provisionDates.size() > 11) {
				return provisionDates;
			}

			provisionDates.add(rs.getDate(1));
			return provisionDates;
		});

		return provisionDates;
	}

	@Override
	public void delete(String finReference, TableType type) {
		StringBuilder sql = new StringBuilder("Delete From Loan_Provisions");
		sql.append(type.getSuffix());
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setString(1, finReference));
	}

	@Override
	public Provision getProvisionById(long id, TableType tableType) {
		StringBuilder sql = getSelectQuery(tableType);
		sql.append(" Where id = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new ProvisionRowMapper(), id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;

	}

	private StringBuilder getSelectQuery(TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinID, FinReference");
		sql.append(", ProvisionDate, ManualProvision, RegProvsnPer, RegProvsnAmt, RegSecProvsnPer");
		sql.append(", RegSecProvsnAmt, RegUnSecProvsnPer, RegUnSecProvsnAmt, TotRegProvsnAmt, IntProvsnPer");
		sql.append(", IntProvsnAmt, IntSecProvsnPer, IntSecProvsnAmt, IntUnSecProvsnPer, IntUnSecProvsnAmt");
		sql.append(", TotIntProvsnAmt, ManProvsnPer, ManProvsnAmt, PastDueDays, NpaAging, NpaPastDueDays");
		sql.append(", NpaClassID, EffNpaClassID, OsPrincipal, OSProfit, ProfitAccruedAndDue, ProfitAccruedAndNotDue");
		sql.append(", CollateralAmt, InsuranceAmt, LinkedTranId, ChgLinkedTranId");
		sql.append(", Version, CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkFlowId");
		sql.append(" From Loan_Provisions");
		sql.append(tableType.getSuffix());

		return sql;
	}

	private class ProvisionRowMapper implements RowMapper<Provision> {
		@Override
		public Provision mapRow(ResultSet rs, int arg1) throws SQLException {

			Provision p = new Provision();

			p.setId(rs.getLong("Id"));
			p.setFinID(rs.getLong("FinID"));
			p.setFinReference(rs.getString("FinReference"));
			p.setProvisionDate(rs.getDate("ProvisionDate"));
			p.setManualProvision(rs.getBoolean("ManualProvision"));
			p.setRegProvsnPer(rs.getBigDecimal("RegProvsnPer"));
			p.setRegProvsnAmt(rs.getBigDecimal("RegProvsnAmt"));
			p.setRegSecProvsnPer(rs.getBigDecimal("RegSecProvsnPer"));
			p.setRegSecProvsnAmt(rs.getBigDecimal("RegSecProvsnAmt"));
			p.setRegUnSecProvsnPer(rs.getBigDecimal("RegUnSecProvsnPer"));
			p.setRegUnSecProvsnAmt(rs.getBigDecimal("RegUnSecProvsnAmt"));
			p.setTotRegProvsnAmt(rs.getBigDecimal("TotRegProvsnAmt"));
			p.setIntProvsnPer(rs.getBigDecimal("IntProvsnPer"));
			p.setIntProvsnAmt(rs.getBigDecimal("IntProvsnAmt"));
			p.setIntSecProvsnPer(rs.getBigDecimal("IntSecProvsnPer"));
			p.setIntSecProvsnAmt(rs.getBigDecimal("IntSecProvsnAmt"));
			p.setIntUnSecProvsnPer(rs.getBigDecimal("IntUnSecProvsnPer"));
			p.setIntUnSecProvsnAmt(rs.getBigDecimal("IntUnSecProvsnAmt"));
			p.setTotIntProvsnAmt(rs.getBigDecimal("TotIntProvsnAmt"));
			p.setManProvsnPer(rs.getBigDecimal("ManProvsnPer"));
			p.setManProvsnAmt(rs.getBigDecimal("ManProvsnAmt"));
			p.setPastDueDays(rs.getInt("PastDueDays"));
			p.setNpaAging(rs.getInt("NpaAging"));
			p.setNpaPastDueDays(rs.getInt("NpaPastDueDays"));
			p.setNpaClassID(rs.getLong("NpaClassID"));
			p.setEffNpaClassID(rs.getLong("EffNpaClassID"));
			p.setOsPrincipal(rs.getBigDecimal("OsPrincipal"));
			p.setOsProfit(rs.getBigDecimal("OSProfit"));
			p.setTotPftAccrued(rs.getBigDecimal("ProfitAccruedAndDue"));
			p.setTillDateSchdPri(rs.getBigDecimal("ProfitAccruedAndNotDue"));
			p.setCollateralAmt(rs.getBigDecimal("CollateralAmt"));
			p.setInsuranceAmt(rs.getBigDecimal("InsuranceAmt"));
			p.setLinkedTranId(rs.getLong("LinkedTranId"));
			p.setChgLinkedTranId(rs.getLong("ChgLinkedTranId"));
			p.setVersion(rs.getInt("Version"));
			p.setCreatedBy(JdbcUtil.getLong(rs.getObject("CreatedBy")));
			p.setCreatedOn(rs.getTimestamp("CreatedOn"));
			p.setApprovedBy(JdbcUtil.getLong(rs.getObject("ApprovedBy")));
			p.setApprovedOn(rs.getTimestamp("ApprovedOn"));
			p.setLastMntBy(JdbcUtil.getLong(rs.getObject("LastMntBy")));
			p.setLastMntOn(rs.getTimestamp("LastMntOn"));
			p.setRecordStatus(rs.getString("RecordStatus"));
			p.setRoleCode(rs.getString("RoleCode"));
			p.setNextRoleCode(rs.getString("NextRoleCode"));
			p.setTaskId(rs.getString("TaskId"));
			p.setNextTaskId(rs.getString("NextTaskId"));
			p.setRecordType(rs.getString("RecordType"));
			p.setWorkflowId(rs.getLong("WorkFlowId"));

			return p;
		}
	}

}