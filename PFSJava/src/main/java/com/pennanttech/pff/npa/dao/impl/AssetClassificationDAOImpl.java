package com.pennanttech.pff.npa.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.npa.dao.AssetClassificationDAO;
import com.pennanttech.pff.npa.model.AssetClassification;
import com.pennanttech.pff.provision.model.NpaProvisionStage;

public class AssetClassificationDAOImpl extends SequenceDao<AssetClassification> implements AssetClassificationDAO {
	private static Logger logger = LogManager.getLogger(AssetClassificationDAOImpl.class);

	public AssetClassificationDAOImpl() {
		super();
	}

	@Override
	public void clearStage() {
		jdbcOperations.update("Truncate table NPA_PROVISION_STAGE");
	}

	@Override
	public void saveStage(List<NpaProvisionStage> list) {
		StringBuilder sql = new StringBuilder("Insert Into Npa_Provision_Stage (");
		sql.append("EodDate, EntityCode");
		sql.append(", CustID, CustCategoryCode, FinType, Product, FinCcy, FinBranch, FinID, FinReference");
		sql.append(", FinAssetValue, FinCurrAssetValue, OsPrincipal, OsProfit, FuturePrincipal, OdPrincipal, OdProfit");
		sql.append(", TotPriBal, TotPriPaid, TotPftPaid, TotPftAccrued, AmzTillLBDate, TillDateSchdPri");
		sql.append(", PastDueDays, PastDueDate, EffFinID, EffFinReference, LinkedLoan");
		sql.append(")");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				NpaProvisionStage item = list.get(i);

				int index = 1;

				ps.setDate(index++, JdbcUtil.getDate(item.getEodDate()));
				ps.setString(index++, item.getEntityCode());
				ps.setLong(index++, item.getCustID());
				ps.setString(index++, item.getCustCategoryCode());
				ps.setString(index++, item.getFinType());
				ps.setString(index++, item.getProduct());
				ps.setString(index++, item.getFinCcy());
				ps.setString(index++, item.getFinBranch());
				ps.setObject(index++, item.getFinID());
				ps.setString(index++, item.getFinReference());
				ps.setBigDecimal(index++, item.getFinAssetValue());
				ps.setBigDecimal(index++, item.getFinCurrAssetValue());
				ps.setBigDecimal(index++, item.getOsPrincipal());
				ps.setBigDecimal(index++, item.getOsProfit());
				ps.setBigDecimal(index++, item.getFuturePrincipal());
				ps.setBigDecimal(index++, item.getOdPrincipal());
				ps.setBigDecimal(index++, item.getOdProfit());
				ps.setBigDecimal(index++, item.getTotPriBal());
				ps.setBigDecimal(index++, item.getTotPriPaid());
				ps.setBigDecimal(index++, item.getTotPftPaid());
				ps.setBigDecimal(index++, item.getTotPftAccrued());
				ps.setBigDecimal(index++, item.getAmzTillLBDate());
				ps.setBigDecimal(index++, item.getTillDateSchdPri());
				ps.setInt(index++, item.getPastDueDays());
				ps.setDate(index++, JdbcUtil.getDate(item.getPastDueDate()));
				ps.setObject(index++, item.getEffFinID());
				ps.setString(index++, item.getEffFinReference());
				ps.setBoolean(index, item.isLinkedLoan());
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
	}

	@Override
	public void deleteSnapshots(Date appDate) {
		jdbcOperations.update("Delete From Npa_Loan_Info_SnapShot Where AppDate = ?", appDate);
	}

	@Override
	public void createSnapShots(Date appDate, Long finID, Long effFinID) {
		StringBuilder sql = new StringBuilder("Insert Into Npa_Loan_Info_SnapShot(");
		sql.append("LinkID, AppDate, CustID, FinID, FinReference, BusinessDate");
		sql.append(", PastDueDays, PastDueDate , EffPastDueDays, EffPastDueDate");
		sql.append(", NpaPastDueDays, NpaPastDueDate, EffNpaPastDueDays, EffNpaPastDueDate");
		sql.append(", NpaClassID, EffNpaClassID, NpaStage, EffNpaStage");
		sql.append(", FinIsActive, LinkedTranID, EffFinID, EffFinReference, ManualClassification");
		sql.append(", CreatedOn, LastMntOn");
		sql.append(")");
		sql.append(" Select");
		sql.append(" ID, ?, CustID, FinID, FinReference, BusinessDate");
		sql.append(", PastDueDays, PastDueDate, EffPastDueDays, EffPastDueDate");
		sql.append(", NpaPastDueDays, NpaPastDueDate, EffNpaPastDueDays, EffNpaPastDueDate");
		sql.append(", NpaClassID, EffNpaClassID, NpaStage, EffNpaStage");
		sql.append(", FinIsActive, LinkedTranID, EffFinID, EffFinReference, ManualClassification");
		sql.append(", CreatedOn, LastMntOn");
		sql.append(" From Npa_Loan_Info");

		if (finID != null) {
			sql.append(" Where FinID = ?");
		}

		if (effFinID != null) {
			sql.append(" Or EffFinID = ?");
		}

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			ps.setDate(1, JdbcUtil.getDate(appDate));

			if (finID != null) {
				ps.setObject(2, finID);
			}

			if (effFinID != null) {
				ps.setObject(3, effFinID);
			}
		});
	}

	@Override
	public void deleteQueue() {
		jdbcOperations.update("truncate table Asset_Classification_Queue");
	}

	@Override
	public long prepareQueue() {
		String sql = "Insert Into Asset_Classification_Queue(ID, FinID) Select row_number() over(order by FinID) ID, FinID From (Select distinct FinID From Npa_Provision_Stage) T";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.update(sql);
	}

	@Override
	public long getQueueCount() {
		String sql = "Select Coalesce(count(ID), 0) From Asset_Classification_Queue where Progress = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Long.class, EodConstants.PROGRESS_WAIT);
		} catch (DataAccessException dae) {
			//
		}

		return 0;
	}

	@Override
	public int updateThreadID(long from, long to, int threadId) {
		String sql = "Update Asset_Classification_Queue Set ThreadId = ? Where Id > ? and Id <= ?  and ThreadId = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.update(sql, threadId, from, to, 0);
		} catch (DataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
		}

		return 0;
	}

	@Override
	public void updateProgress(long finID, int progressInProcess) {
		String sql = null;
		if (progressInProcess == EodConstants.PROGRESS_IN_PROCESS) {
			sql = "Update Asset_Classification_Queue Set Progress = ?, StartTime = ? Where FinID = ? and Progress = ?";

			logger.debug(Literal.SQL + sql);

			this.jdbcOperations.update(sql, ps -> {
				ps.setInt(1, progressInProcess);
				ps.setDate(2, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setLong(3, finID);
				ps.setInt(4, EodConstants.PROGRESS_WAIT);
			});
		} else if (progressInProcess == EodConstants.PROGRESS_SUCCESS) {
			sql = "Update Asset_Classification_Queue Set EndTime = ?, Progress = ? where FinID = ?";

			logger.debug(Literal.SQL + sql);

			this.jdbcOperations.update(sql, ps -> {
				ps.setDate(1, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setInt(2, EodConstants.PROGRESS_SUCCESS);
				ps.setLong(3, finID);
			});
		} else if (progressInProcess == EodConstants.PROGRESS_FAILED) {
			sql = "Update Asset_Classification_Queue Set EndTime = ?, ThreadId = ?, Progress = ? Where FinID = ?";

			logger.debug(Literal.SQL + sql);

			this.jdbcOperations.update(sql, ps -> {
				ps.setDate(1, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setInt(2, 0);
				ps.setInt(3, EodConstants.PROGRESS_WAIT);
				ps.setLong(4, finID);
			});
		}
	}

	@Override
	public AssetClassification getClassification(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" npa.Id, nps.CustId, nps.FinID, nps.FinReference, nps.EODDate, nps.EntityCode");
		sql.append(", nps.PastDueDays, nps.PastDueDate, npa.NpaPastDueDays, npa.NpaPastDueDate, npa.NpaStage");
		sql.append(" From Npa_Provision_Stage nps");
		sql.append(" Left Join Npa_Loan_Info npa On npa.FinID = nps.FinID");
		sql.append(" Where nps.FinID = ? and nps.LinkedLoan = ?");

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				AssetClassification item = new AssetClassification();

				item.setId(JdbcUtil.getLong(rs.getObject("Id")));
				item.setCustID(rs.getLong("CustID"));
				item.setFinID(JdbcUtil.getLong(rs.getObject("FinID")));
				item.setFinReference(rs.getString("FinReference"));
				item.setEodDate(rs.getDate("EODDate"));
				item.setEntityCode(rs.getString("EntityCode"));
				item.setPastDueDays(rs.getInt("PastDueDays"));
				item.setPastDueDate(rs.getDate("PastDueDate"));
				item.setNpaPastDueDays(rs.getInt("NpaPastDueDays"));
				item.setNpaPastDueDate(rs.getDate("NpaPastDueDate"));
				item.setNpaStage(rs.getBoolean("NpaStage"));

				return item;
			}, finID, 0);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return null;
	}

	@Override
	public long getCustId(long finID) {
		String sql = "Select CustId From Npa_Provision_Stage Where FinID = ? and LinkedLoan = ?";

		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Long.class, finID, 0);
	}

	@Override
	public List<FinanceMain> getPrimaryLoans(long custID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.CustID, fm.FinID, fm.FinReference, fm.FinType, fm.FinBranch");
		sql.append(", fm.FinCcy, fm.WriteoffLoan, c.CustCtgCode, ft.FinCategory, e.EntityCode");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join Customers c on c.CustID = fm.CustID");
		sql.append(" Inner Join RMTFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" Inner Join SMTDivisionDetail dd ON dd.DivisionCode = ft.FinDivision");
		sql.append(" Inner Join Entity e on e.EntityCode = dd.EntityCode");
		sql.append(" Where fm.CustID = ? and fm.FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setCustID(rs.getLong("CustID"));
			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));
			fm.setLovDescCustCtgCode(rs.getString("CustCtgCode"));
			fm.setFinCategory(rs.getString("FinCategory"));
			fm.setEntityCode(rs.getString("EntityCode"));

			return fm;
		}, custID, 1);
	}

	@Override
	public List<FinanceMain> getCoApplicantLoans(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.CustID, fm.FinID, fm.FinReference, fm.FinType, fm.FinBranch, fm.FinCcy");
		sql.append(", fm.WriteoffLoan, c.CustCtgCode, ft.FinCategory, e.EntityCode");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join Customers c on c.CustId = fm.CustId");
		sql.append(" Inner Join RMTFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" Inner Join SMTDivisionDetail dd ON dd.DivisionCode = ft.FinDivision");
		sql.append(" Inner Join Entity e on e.EntityCode = dd.EntityCode");
		sql.append(" Where fm.CustId in (");
		sql.append(" Select c.CustID");
		sql.append(" From FinJointAccountDetails ca");
		sql.append(" Inner Join FinanceMain fm on fm.FinReference = ca.FinReference");
		sql.append(" Inner Join Customers c on c.CustCIF = ca.CustCIF");
		sql.append(" Where fm.FinID = ?");
		sql.append(" ) and fm.FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setCustID(rs.getLong("CustID"));
			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));
			fm.setLovDescCustCtgCode(rs.getString("CustCtgCode"));
			fm.setFinCategory(rs.getString("FinCategory"));
			fm.setEntityCode(rs.getString("EntityCode"));

			return fm;
		}, finID, 1);
	}

	@Override
	public List<FinanceMain> getGuarantorLoans(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.CustID, fm.FinID, fm.FinReference, fm.FinType, fm.FinBranch, fm.FinCcy");
		sql.append(", fm.WriteoffLoan, c.CustCtgCode, ft.FinCategory, e.EntityCode");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join Customers c on c.CustId = fm.CustId");
		sql.append(" Inner Join RMTFinanceTypes ft on ft.FinType = fm.FinType");
		sql.append(" Inner Join SMTDivisionDetail dd ON dd.DivisionCode = ft.FinDivision");
		sql.append(" Inner Join Entity e on e.EntityCode = dd.EntityCode");
		sql.append(" Where fm.CustId in (");
		sql.append(" Select c.CustID");
		sql.append(" From FinGuarantorsDetails gd");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = gd.FinID");
		sql.append(" Inner Join Customers c on c.CustCIF = gd.GuarantorCIF");
		sql.append(" Where fm.FinID = ?");
		sql.append(" ) and fm.FinIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			FinanceMain fm = new FinanceMain();

			fm.setCustID(rs.getLong("CustID"));
			fm.setFinID(rs.getLong("FinID"));
			fm.setFinReference(rs.getString("FinReference"));
			fm.setFinType(rs.getString("FinType"));
			fm.setFinBranch(rs.getString("FinBranch"));
			fm.setFinCcy(rs.getString("FinCcy"));
			fm.setWriteoffLoan(rs.getBoolean("WriteoffLoan"));
			fm.setLovDescCustCtgCode(rs.getString("CustCtgCode"));
			fm.setFinCategory(rs.getString("FinCategory"));
			fm.setEntityCode(rs.getString("EntityCode"));

			return fm;
		}, finID, 1);
	}

	@Override
	public List<NpaProvisionStage> getPastDueInfoFromStage(Set<String> finReferences) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, PastDueDays, PastDueDate From Npa_Provision_Stage");
		sql.append(" Where FinReference in (");
		sql.append(finReferences.stream().map(e -> "?").collect(Collectors.joining(", ")));
		sql.append(") and LinkedLoan = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.query(sql.toString(), ps -> {

			int index = 1;

			for (String finReference : finReferences) {
				ps.setString(index++, finReference);
			}

			ps.setInt(index++, 0);

		}, (rs, rowNum) -> {
			NpaProvisionStage nps = new NpaProvisionStage();

			nps.setFinID(JdbcUtil.getLong(rs.getObject("FinID")));
			nps.setFinReference(rs.getString("FinReference"));
			nps.setPastDueDays(rs.getInt("PastDueDays"));
			nps.setPastDueDate(rs.getDate("PastDueDate"));

			return nps;

		});
	}

	@Override
	public void save(AssetClassification ac) {
		StringBuilder sql = new StringBuilder("Insert Into Npa_Loan_Info(");
		sql.append("CustID, FinID, FinReference, BusinessDate, PastDueDays, PastDueDate");
		sql.append(", NpaClassID, NpaPastDueDays, NpaPastDueDate, NpaStage");
		sql.append(", EffPastDueDays, EffNpaPastDueDays, EffNpaStage, FinIsActive");
		sql.append(", CreatedOn, LastMntOn");
		sql.append(")");
		sql.append(" Values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, ac.getCustID());
				ps.setObject(index++, ac.getFinID());
				ps.setString(index++, ac.getFinReference());
				ps.setDate(index++, JdbcUtil.getDate(ac.getEodDate()));
				ps.setInt(index++, ac.getPastDueDays());
				ps.setDate(index++, JdbcUtil.getDate(ac.getPastDueDate()));
				ps.setLong(index++, ac.getNpaClassID());
				ps.setInt(index++, ac.getNpaPastDueDays());
				ps.setDate(index++, JdbcUtil.getDate(ac.getNpaPastDueDate()));
				ps.setBoolean(index++, ac.isNpaStage());
				ps.setInt(index++, 0);
				ps.setInt(index++, 0);
				ps.setBoolean(index++, false);
				ps.setBoolean(index++, true);

				ps.setTimestamp(index++, ac.getCreatedOn());
				ps.setTimestamp(index++, ac.getLastMntOn());
			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	@Override
	public void update(AssetClassification ac) {
		StringBuilder sql = new StringBuilder("Update Npa_Loan_Info");
		sql.append(" Set BusinessDate = ?, PastDueDays = ?, PastDueDate = ?");
		sql.append(", NpaPastDueDays = ?, NpaPastDueDate = ?, NpaStage = ?, NpaClassID = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setDate(index++, JdbcUtil.getDate(ac.getEodDate()));
				ps.setInt(index++, ac.getPastDueDays());
				ps.setDate(index++, JdbcUtil.getDate(ac.getPastDueDate()));
				ps.setInt(index++, ac.getNpaPastDueDays());
				ps.setDate(index++, JdbcUtil.getDate(ac.getNpaPastDueDate()));
				ps.setBoolean(index++, ac.isNpaStage());
				ps.setLong(index++, ac.getNpaClassID());

				ps.setLong(index++, ac.getId());
			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	@Override
	public List<AssetClassification> getClassifications(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" nps.FinID, nps.EffFinID, npa.FinReference, nps.EffFinReference, nps.EntityCode, nps.LinkedLoan");
		sql.append(", npa.PastDueDays, npa.NpaPastDueDays");
		sql.append(" From Npa_Provision_Stage nps");
		sql.append(" Inner Join Npa_Loan_Info npa on npa.FinID = nps.EffFinID");
		sql.append(" Where nps.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			AssetClassification item = new AssetClassification();

			item.setFinID(JdbcUtil.getLong(rs.getObject("FinID")));
			item.setEffFinID(JdbcUtil.getLong(rs.getObject("EffFinID")));
			item.setFinReference(rs.getString("FinReference"));
			item.setEffFinReference(rs.getString("EffFinReference"));
			item.setEntityCode(rs.getString("EntityCode"));
			item.setLinkedLoan(rs.getBoolean("LinkedLoan"));
			item.setPastDueDays(rs.getInt("PastDueDays"));
			item.setNpaPastDueDays(rs.getInt("NpaPastDueDays"));

			return item;
		}, finID);

	}

	@Override
	public AssetClassification getNpaDetails(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinID, FinReference, EffFinID, EffFinReference, FinIsActive");
		sql.append(", PastDueDays, PastDueDate, EffPastDueDays, EffPastDueDate");
		sql.append(", NpaPastDueDays, NpaPastDueDate, EffNpaPastDueDays, EffNpaPastDueDate");
		sql.append(", NpaClassID, EffNpaClassID");
		sql.append(", NpaStage, EffNpaStage");
		sql.append(", BusinessDate, LinkedTranID");
		sql.append(" From Npa_Loan_Info");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				AssetClassification ac = new AssetClassification();

				ac.setId(rs.getLong("Id"));
				ac.setFinID(JdbcUtil.getLong(rs.getObject("FinID")));
				ac.setFinReference(rs.getString("FinReference"));
				ac.setEffFinID(JdbcUtil.getLong(rs.getObject("EffFinID")));
				ac.setEffFinReference(rs.getString("EffFinReference"));
				ac.setFinIsActive(rs.getBoolean("FinIsActive"));
				ac.setPastDueDays(rs.getInt("PastDueDays"));
				ac.setPastDueDate(rs.getDate("PastDueDate"));
				ac.setEffPastDueDays(rs.getInt("EffPastDueDays"));
				ac.setEffPastDueDate(rs.getDate("EffPastDueDate"));
				ac.setNpaPastDueDays(rs.getInt("NpaPastDueDays"));
				ac.setNpaPastDueDate(rs.getDate("NpaPastDueDate"));
				ac.setEffNpaPastDueDays(rs.getInt("EffNpaPastDueDays"));
				ac.setEffNpaPastDueDate(rs.getDate("EffNpaPastDueDate"));
				ac.setNpaClassID(JdbcUtil.getLong(rs.getObject("NpaClassID")));
				ac.setEffNpaClassID(JdbcUtil.getLong(rs.getObject("EffNpaClassID")));
				ac.setNpaStage(rs.getBoolean("NpaStage"));
				ac.setEffNpaStage(rs.getBoolean("EffNpaStage"));
				ac.setEodDate(rs.getDate("BusinessDate"));
				ac.setLinkedTranID(JdbcUtil.getLong(rs.getObject("LinkedTranID")));

				return ac;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}
		return null;
	}

	@Override
	public AssetClassification getLoanInfo(long finID) {
		StringBuilder sql = new StringBuilder("Select s.EodDate, s.EntityCode");
		sql.append(", s.CustID, s.CustCategoryCode, s.FinType, s.FinCCy, s.Product, s.FinBranch");
		sql.append(", s.FinID, s.FinReference, s.FinAssetValue, s.FinCurrAssetValue");
		sql.append(", s.OsPrincipal, s.OsProfit, s.FuturePrincipal, OdPrincipal, OdProfit");
		sql.append(", s.TotPriBal, s.TotPriPaid, s.TotPftPaid, s.TotPftAccrued, s.AmzTillLBDate, s.TillDateSchdPri");
		sql.append(" From Npa_Provision_Stage s");
		sql.append(" Where s.FinID = ? and LinkedLoan = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
			AssetClassification item = new AssetClassification();

			item.setEodDate(rs.getDate("EodDate"));
			item.setEntityCode(rs.getString("EntityCode"));
			item.setCustID(rs.getLong("CustID"));
			item.setCustCategoryCode(rs.getString("CustCategoryCode"));
			item.setFinType(rs.getString("FinType"));
			item.setFinCcy(rs.getString("FinCcy"));
			item.setProduct(rs.getString("Product"));
			item.setFinBranch(rs.getString("FinBranch"));
			item.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
			item.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
			item.setOsPrincipal(rs.getBigDecimal("OsPrincipal"));
			item.setOsProfit(rs.getBigDecimal("OsProfit"));
			item.setFuturePrincipal(rs.getBigDecimal("FuturePrincipal"));
			item.setOdPrincipal(rs.getBigDecimal("OdPrincipal"));
			item.setOdProfit(rs.getBigDecimal("OdProfit"));
			item.setTotPriBal(rs.getBigDecimal("TotPriBal"));
			item.setTotPriPaid(rs.getBigDecimal("TotPriPaid"));
			item.setTotPftPaid(rs.getBigDecimal("TotPftPaid"));
			item.setTotPftAccrued(rs.getBigDecimal("TotPftAccrued"));
			item.setAmzTillLBDate(rs.getBigDecimal("AmzTillLBDate"));
			item.setTillDateSchdPri(rs.getBigDecimal("TillDateSchdPri"));

			return item;
		}, finID, 0);
	}

	@Override
	public void updateClassification(AssetClassification ac) {
		StringBuilder sql = new StringBuilder("Update Npa_Loan_Info Set");
		sql.append(" EffFinID = ?, EffFinReference = ?, FinIsActive = ?");
		sql.append(", PastDueDays = ?, PastDueDate = ?, EffPastDueDays = ?, EffPastDueDate = ?");
		sql.append(", NpaPastDueDays = ?, NpaPastDueDate = ?, EffNpaPastDueDays = ?, EffNpaPastDueDate = ?");
		sql.append(", NpaClassID = ?, EffNpaClassID = ?, NpaStage = ?, EFfNpaStage = ?");
		sql.append(", LinkedTranID = ?, LastMntOn = ?");
		sql.append(" Where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setObject(index++, ac.getEffFinID());
			ps.setString(index++, ac.getEffFinReference());
			ps.setBoolean(index++, ac.isFinIsActive());
			ps.setInt(index++, ac.getPastDueDays());
			ps.setDate(index++, JdbcUtil.getDate(ac.getPastDueDate()));
			ps.setInt(index++, ac.getEffPastDueDays());
			ps.setDate(index++, JdbcUtil.getDate(ac.getEffPastDueDate()));
			ps.setInt(index++, ac.getNpaPastDueDays());
			ps.setDate(index++, JdbcUtil.getDate(ac.getNpaPastDueDate()));
			ps.setInt(index++, ac.getEffNpaPastDueDays());
			ps.setDate(index++, JdbcUtil.getDate(ac.getEffNpaPastDueDate()));
			ps.setObject(index++, ac.getNpaClassID());
			ps.setObject(index++, ac.getEffNpaClassID());
			ps.setBoolean(index++, ac.isNpaStage());
			ps.setBoolean(index++, ac.isEffNpaStage());
			ps.setObject(index++, ac.getLinkedTranID());
			ps.setTimestamp(index++, new Timestamp(System.currentTimeMillis()));

			ps.setLong(index++, ac.getId());

		});
	}

	@Override
	public AssetClassification getAssetClassification(long finID) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" np.FinID, np.FinReference, np.PastDueDays, np.PastDueDate, np.NpaPastDueDays, np.NpaPastDueDate");
		sql.append(", acc.Code NpaClassCode, acc.Description NpaClassDesc");
		sql.append(", ascc.Code NpaSubClassCode, ascc.Description NpaSubClassDesc");
		sql.append(", np.NpaStage, np.EffFinID");
		sql.append(", np.EffFinReference, np.EffPastDueDays, np.EffPastDueDate");
		sql.append(", np.EffNpaPastDueDays, np.EffNpaPastDueDate");
		sql.append(", eacc.Code EffNpaClassCode, eacc.Description EffNpaClassDesc");
		sql.append(", eascc.Code EffNpaSubClassCode, eascc.Description EffNpaSubClassDesc");
		sql.append(", np.EffNpaStage, enp.FinIsActive");
		sql.append(" From Npa_Loan_Info np");
		sql.append(" Inner Join Npa_Loan_Info enp on enp.FinReference = np.EffFinReference");
		sql.append(" Inner Join Asset_Class_Setup_Details acsd on acsd.ID = np.NpaClassID");
		sql.append(" Inner Join Asset_Class_Codes acc on acc.ID = acsd.ClassID");
		sql.append(" Inner Join Asset_Sub_Class_Codes ascc on ascc.ID = acsd.SubClassID");
		sql.append(" Inner Join Asset_Class_Setup_Details eacsd on eacsd.ID = np.EffNpaClassID");
		sql.append(" Inner Join Asset_Class_Codes eacc on eacc.ID = eacsd.ClassID");
		sql.append(" Inner Join Asset_Sub_Class_Codes eascc on eascc.ID = eacsd.SubClassID");
		sql.append(" Where np.FinID = ?");

		logger.debug(Literal.SQL + sql.toString());
		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				AssetClassification npa = new AssetClassification();

				npa.setFinID(rs.getLong("FinID"));
				npa.setFinReference(rs.getString("FinReference"));
				npa.setPastDueDays(rs.getInt("PastDueDays"));
				npa.setPastDueDate(rs.getDate("PastDueDate"));
				npa.setNpaPastDueDays(rs.getInt("NpaPastDueDays"));
				npa.setNpaPastDueDate(rs.getDate("NpaPastDueDate"));
				npa.setNpaClassCode(rs.getString("NpaClassCode"));
				npa.setNpaClassDesc(rs.getString("NpaClassDesc"));
				npa.setNpaSubClassCode(rs.getString("NpaSubClassCode"));
				npa.setNpaSubClassDesc(rs.getString("NpaSubClassDesc"));
				npa.setNpaStage(rs.getBoolean("NpaStage"));
				npa.setEffFinID(rs.getLong("EffFinID"));
				npa.setEffFinReference(rs.getString("EffFinReference"));
				npa.setEffPastDueDays(rs.getInt("EffPastDueDays"));
				npa.setEffPastDueDate(rs.getDate("EffPastDueDate"));
				npa.setEffNpaPastDueDays(rs.getInt("EffNpaPastDueDays"));
				npa.setEffNpaPastDueDate(rs.getDate("EffNpaPastDueDate"));
				npa.setEffNpaClassCode(rs.getString("EffNpaClassCode"));
				npa.setEffNpaClassDesc(rs.getString("EffNpaClassDesc"));
				npa.setEffNpaSubClassCode(rs.getString("EffNpaSubClassCode"));
				npa.setEffNpaSubClassDesc(rs.getString("EffNpaSubClassDesc"));
				npa.setEffNpaStage(rs.getBoolean("EffNpaStage"));
				npa.setFinIsActive(rs.getBoolean("FinIsActive"));

				return npa;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return new AssetClassification();
	}

	@Override
	public AssetClassification getNpaClassification(long finID) {
		StringBuilder sql = new StringBuilder("Select FinID, FinReference");
		sql.append(", PastDueDays, PastDueDate, NpaPastDueDays, NpaPastDueDate, NpaClassId, NpaStage, FinIsActive");
		sql.append(" From Npa_Loan_Info");
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				AssetClassification item = new AssetClassification();

				item.setFinID(JdbcUtil.getLong(rs.getObject("FinID")));
				item.setFinReference(rs.getString("FinReference"));
				item.setPastDueDays(rs.getInt("PastDueDays"));
				item.setPastDueDate(rs.getDate("PastDueDate"));
				item.setNpaPastDueDays(rs.getInt("NpaPastDueDays"));
				item.setNpaPastDueDate(rs.getDate("NpaPastDueDate"));
				item.setNpaClassID(JdbcUtil.getLong(rs.getObject("NpaClassId")));
				item.setNpaStage(rs.getBoolean("NpaStage"));
				item.setFinIsActive(rs.getBoolean("FinIsActive"));

				return item;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}
		return new AssetClassification();
	}

	@Override
	public boolean isEffNpaStage(long finID) {
		String sql = "Select EffNpaStage From Npa_Loan_Info Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql, Boolean.class, finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return false;
	}

	@Override
	public String getNpaRepayHierarchy(long finID) {
		StringBuilder sql = new StringBuilder("Select npa.EffNpaStage, h.RepayHierarchy");
		sql.append(" From Npa_Loan_Info npa");
		sql.append(" Inner Join Asset_Class_Setup_Details acsd on acsd.id = npa.NpaclassId");
		sql.append(" Inner Join Asset_Class_Setup_Header h on h.id = acsd.SetupID");
		sql.append(" Where npa.FinID =  ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				String repayHierarchy = "";
				if (rs.getBoolean("EffNpaStage")) {
					repayHierarchy = rs.getString("RepayHierarchy");
				}
				return repayHierarchy;
			}, finID);
		} catch (EmptyResultDataAccessException e) {
			//
		}

		return "";
	}

	@Override
	public void deleteLinkedLoansForES(long finID) {
		String sql = "Delete From Npa_Provision_Stage Where FinID = ? and LinkedLoan = ?";
		jdbcOperations.update(sql, finID, 1);
	}

	@Override
	public String getEntityCodeFromStage(long finID) {
		String sql = "Select EntityCode From Npa_Provision_Stage Where FinID = ? and LinkedLoan = ?";
		return jdbcOperations.queryForObject(sql, String.class, finID, 0);
	}

	@Override
	public void updatePastDuesForES(long finID) {
		String sql = "Update Npa_Provision_Stage Set PastDueDays = ?, PastDueDate = ? Where FinID = ?";
		jdbcOperations.update(sql, 0, null, finID);

		sql = "Update Npa_Loan_Info Set FinIsActive = ? Where EffFinID = ?";
		jdbcOperations.update(sql, 0, finID);
	}

	public boolean checkDependency(long npaClassID) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select Count(NpaClassID) From Npa_Loan_Info");
		sql.append(" Where NpaClassID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> rs.getInt(1), npaClassID) > 0;
	}

}