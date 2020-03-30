package com.pennant.eod.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customerqueuing.CustomerQueuing;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class CustomerQueuingDAOImpl extends BasicDao<CustomerQueuing> implements CustomerQueuingDAO {
	private static Logger logger = Logger.getLogger(CustomerQueuingDAOImpl.class);

	private static final String UPDATE_SQL = "update CustomerQueuing set ThreadId = ? where ThreadId = ?";
	private static final String UPDATE_SQL_RC = "update Top(?) CustomerQueuing set ThreadId = ? where ThreadId = ?";
	private static final String UPDATE_POSTGRES_RC = "update CustomerQueuing set ThreadId = ? where ThreadId = ? AND CustID in (Select CustID From CustomerQueuing Where ThreadId = ? order by CustID LIMIT ?)";
	private static final String UPDATE_ORCL_RC = "update CustomerQueuing set ThreadId = ? where ROWNUM <= ? AND ThreadId = ?";
	private static final String START_CID_RC = "update CustomerQueuing set Progress = ? ,StartTime = ? Where CustID = ? AND Progress = ?";
	private static final String UPDATE_LOANCOUNT = "update CustomerQueuing set ThreadId = ? Where FinRunningCount > ? AND FinRunningCount <= ?  AND ThreadId = ?";

	public CustomerQueuingDAOImpl() {
		super();
	}

	@Override
	public int prepareCustomerQueue(Date date) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO CustomerQueuing");
		sql.append("(CustID, EodDate, THREADID, PROGRESS, LOANEXIST, LimitRebuild, EodProcess)");
		sql.append(" select  distinct CustID, ?, ?, ?, ?, ?, ? FROM FinanceMain where FinIsActive = ?");

		logger.trace(Literal.SQL + sql.toString());

		int financeRecords = this.jdbcTemplate.getJdbcOperations().update(sql.toString(),
				new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						if (App.DATABASE == Database.POSTGRES) {
							ps.setObject(1, LocalDateTime.now());
						} else {
							ps.setDate(1, DateUtil.getSqlDate(DateUtil.getSysDate()));
						}
						ps.setInt(2, 0);
						ps.setInt(3, 0);
						ps.setBoolean(4, true);
						ps.setBoolean(5, false);
						ps.setBoolean(6, true);
						ps.setBoolean(7, true);

					}
				});

		sql = new StringBuilder();
		sql.append("INSERT INTO CustomerQueuing");
		sql.append("(CustID, EodDate, THREADID, PROGRESS, LOANEXIST, LimitRebuild, EodProcess)");
		sql.append(" select distinct CustomerID, ?, ?, ?, ?, ?, ? from LimitHeader lh");
		sql.append(" inner Join LIMITSTRUCTURE ls on ls.StructureCode = lh.LimitStructureCode");
		sql.append(" Where ls.Rebuild = ?");
		sql.append(" and CustomerID not in (Select Distinct CustId from CustomerQueuing) and CustomerID <> ?");

		logger.trace(Literal.SQL + sql.toString());

		int nonFinacerecords = this.jdbcTemplate.getJdbcOperations().update(sql.toString(),
				new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						if (App.DATABASE == Database.POSTGRES) {
							ps.setObject(1, LocalDateTime.now());
						} else {
							ps.setDate(1, DateUtil.getSqlDate(DateUtil.getSysDate()));
						}
						ps.setInt(2, 0);
						ps.setInt(3, 0);
						ps.setBoolean(4, false);
						ps.setBoolean(5, false);
						ps.setBoolean(6, true);
						ps.setInt(7, 1);
						ps.setInt(8, 0);

					}
				});

		return financeRecords + nonFinacerecords;
	}

	@Override
	public int prepareCustomerQueueByLoanCount(Date date) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO CustomerQueuing");
		sql.append("(CustID, EodDate, THREADID, PROGRESS, LOANEXIST, LimitRebuild, EodProcess");
		sql.append(", FinCount, FinRunningCount)");
		sql.append(" select  CustID, ?, ?, ?, ?, ?, ? , FinCount, sum(FinCount)");
		sql.append(" over (order by custid) FinRunningCount FROM");
		sql.append(" (select CustID, count(*) FinCount FROM FinanceMain where FinIsActive = ? group by CustID) c ");

		logger.trace(Literal.SQL + sql.toString());

		int financeRecords = this.jdbcTemplate.getJdbcOperations().update(sql.toString(),
				new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						if (App.DATABASE == Database.POSTGRES) {
							ps.setObject(1, LocalDateTime.now());
						} else {
							ps.setDate(1, DateUtil.getSqlDate(DateUtil.getSysDate()));
						}
						ps.setInt(2, 0);
						ps.setInt(3, 0);
						ps.setBoolean(4, true);
						ps.setBoolean(5, false);
						ps.setBoolean(6, true);
						ps.setBoolean(7, true);
					}
				});

		sql = new StringBuilder();
		sql.append("INSERT INTO CustomerQueuing");
		sql.append("(CustID, EodDate, THREADID, PROGRESS, LOANEXIST, LimitRebuild, EodProcess");
		sql.append(", FinCount, FinRunningCount)");
		sql.append(" select distinct CustomerID, ?, ?, ?, ?, ?, ?, 0, 0 from LimitHeader lh");
		sql.append(" inner Join LIMITSTRUCTURE ls on ls.StructureCode = lh.LimitStructureCode");
		sql.append(" Where ls.Rebuild = ?");
		sql.append(" and CustomerID not in (Select Distinct CustId from CustomerQueuing) and CustomerID <> ?");

		logger.trace(Literal.SQL + sql.toString());

		int nonFinacerecords = this.jdbcTemplate.getJdbcOperations().update(sql.toString(),
				new PreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						if (App.DATABASE == Database.POSTGRES) {
							ps.setObject(1, LocalDateTime.now());
						} else {
							ps.setDate(1, DateUtil.getSqlDate(DateUtil.getSysDate()));
						}
						ps.setInt(2, 0);
						ps.setInt(3, 0);
						ps.setBoolean(4, false);
						ps.setBoolean(5, false);
						ps.setBoolean(6, true);
						ps.setInt(7, 1);
						ps.setInt(8, 0);

					}
				});

		return financeRecords + nonFinacerecords;
	}

	@Override
	public long getCountByProgress() {
		String sql = "select count(CustID) from CustomerQueuing where Progress = ?";
		logger.trace(Literal.SQL + sql);

		long progressCount = this.jdbcTemplate.getJdbcOperations().queryForObject(sql,
				new Object[] { EodConstants.PROGRESS_WAIT }, Long.class);

		return progressCount;
	}

	@Override
	public int getProgressCountByCust(long custID) {
		String sql = "select coalesce(Count(CustID), 0) from CustomerQueuing where CustID = ? and Progress = ?";
		logger.trace(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, new Object[] { custID, EodConstants.PROGRESS_IN_PROCESS },
				Integer.class);
	}

	@Override
	public int updateThreadIDByRowNumber(Date date, long noOfRows, int threadId) {
		try {
			if (noOfRows == 0) {
				logger.trace(Literal.SQL + UPDATE_SQL);
				return this.jdbcOperations.update(UPDATE_SQL, new Object[] { threadId, 0 });
			} else {
				if (App.DATABASE == Database.SQL_SERVER) {
					logger.trace(Literal.SQL + UPDATE_SQL_RC);
					return this.jdbcOperations.update(UPDATE_SQL_RC, new Object[] { noOfRows, threadId, 0 });
				} else if (App.DATABASE == Database.ORACLE) {
					logger.trace(Literal.SQL + UPDATE_ORCL_RC);
					return this.jdbcOperations.update(UPDATE_ORCL_RC, new Object[] { threadId, noOfRows, 0 });
				} else if (App.DATABASE == Database.POSTGRES) {
					logger.trace(Literal.SQL + UPDATE_POSTGRES_RC);
					return this.jdbcOperations.update(UPDATE_POSTGRES_RC, new Object[] { threadId, 0, 0, noOfRows });
				}
			}

		} catch (EmptyResultDataAccessException dae) {
			logger.warn(Literal.EXCEPTION, dae);
		}

		return 0;

	}

	@Override
	public void updateThreadID(Date date, int threadId) {
		String sql = "update CustomerQueuing set ThreadId = ? Where ThreadId = ?";
		logger.trace(Literal.SQL + sql);

		this.jdbcTemplate.getJdbcOperations().update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, threadId);
				ps.setInt(2, 0);
			}
		});
	}

	@Override
	public void updateProgress(CustomerQueuing customerQueuing) {
		String sql = "Update CustomerQueuing set Progress = ?  where CustID = ?";
		logger.trace(Literal.SQL + sql);

		this.jdbcTemplate.getJdbcOperations().update(sql, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, customerQueuing.getProgress());
				ps.setLong(2, customerQueuing.getCustID());

			}
		});

	}

	@Override
	public void update(CustomerQueuing customerQueuing, boolean start) {
		StringBuilder sql = new StringBuilder("Update CustomerQueuing set");
		if (start) {
			sql.append(" StartTime = ?");
		} else {
			sql.append(", EndTime = ?");
		}

		sql.append(", Progress = ? Where CustID = ?");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.getJdbcOperations().update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				if (start) {
					if (App.DATABASE == Database.POSTGRES) {
						ps.setObject(1, customerQueuing.getStartTime());
					} else {
						ps.setDate(1, DateUtil.getSqlDate(customerQueuing.getStartTime()));
					}
				} else {
					if (App.DATABASE == Database.POSTGRES) {
						ps.setObject(1, customerQueuing.getEndTime());
					} else {
						ps.setDate(1, DateUtil.getSqlDate(customerQueuing.getEndTime()));
					}
				}

				ps.setInt(2, customerQueuing.getProgress());
				ps.setLong(3, customerQueuing.getCustID());
			}
		});
	}

	@Override
	public void updateStatus(long custID, int progress) {
		String sql = "update CustomerQueuing set EndTime = ?, Progress = ? where CustID = ?";
		logger.trace(Literal.SQL + sql);

		this.jdbcTemplate.getJdbcOperations().update(sql, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				if (App.DATABASE == Database.POSTGRES) {
					ps.setObject(1, LocalDateTime.now());
				} else {
					ps.setDate(1, DateUtil.getSqlDate(DateUtil.getSysDate()));
				}
				ps.setInt(2, progress);
				ps.setLong(3, custID);
			}
		});
	}

	@Override
	public void updateFailed(CustomerQueuing customerQueuing) {
		String sql = "Update CustomerQueuing set EndTime = ?, ThreadId = ?, Progress = ? Where CustID = ?";
		logger.trace(Literal.SQL + sql);

		this.jdbcTemplate.getJdbcOperations().update(sql, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				if (App.DATABASE == Database.POSTGRES) {
					ps.setObject(1, LocalDateTime.now());
				} else {
					ps.setDate(1, DateUtil.getSqlDate(DateUtil.getSysDate()));
				}
				ps.setInt(2, customerQueuing.getThreadId());
				ps.setInt(3, customerQueuing.getProgress());
				ps.setLong(4, customerQueuing.getCustID());
			}
		});
	}

	@Override
	public void delete() {
		String sql = "truncate TABLE CustomerQueuing";
		logger.trace(Literal.SQL + sql);

		this.jdbcOperations.update(sql);
	}

	@Override
	public void logCustomerQueuing() {
		String sql = "insert into CustomerQueuing_Log select * FROM CustomerQueuing";
		logger.trace(Literal.SQL + sql);
		this.jdbcOperations.update(sql);
	}

	@Override
	public int startEODForCID(long custID) {
		logger.trace(Literal.SQL + START_CID_RC);
		return this.jdbcOperations.update(START_CID_RC, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, EodConstants.PROGRESS_IN_PROCESS);
				if (App.DATABASE == Database.POSTGRES) {
					ps.setObject(2, LocalDateTime.now());
				} else {
					ps.setDate(2, DateUtil.getSqlDate(DateUtil.getSysDate()));
				}
				ps.setLong(3, custID);
				ps.setInt(4, EodConstants.PROGRESS_WAIT);

			}
		});

	}

	@Override
	public List<Customer> getCustForProcess(int threadId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" cu.CustID, CustCIF, CustCoreBank, CustCtgCode, CustTypeCode, CustDftBranch, CustPOB");
		sql.append(", CustCOB, CustGroupID, CustSts, CustStsChgDate, CustIsStaff, CustIndustry, CustSector");
		sql.append(", CustSubSector, CustEmpSts, CustSegment, CustSubSegment, CustParentCountry, CustResdCountry");
		sql.append(", CustRiskCountry, CustNationality, SalariedCustomer, CustSuspSts, CustSuspDate");
		sql.append(", CustSuspTrigger, CustAppDate");
		sql.append(" from  Customers cu");
		sql.append(" inner join CustomerQueuing CQ ON cu.CustID = CQ.CustID");
		sql.append(" where ThreadID = ? and Progress = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setLong(1, threadId);
					ps.setInt(2, EodConstants.PROGRESS_IN_PROCESS);
				}
			}, new RowMapper<Customer>() {
				@Override
				public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
					Customer cu = new Customer();

					cu.setCustID(rs.getLong("CustID"));
					cu.setCustCIF(rs.getString("CustCIF"));
					cu.setCustCoreBank(rs.getString("CustCoreBank"));
					cu.setCustCtgCode(rs.getString("CustCtgCode"));
					cu.setCustTypeCode(rs.getString("CustTypeCode"));
					cu.setCustDftBranch(rs.getString("CustDftBranch"));
					cu.setCustPOB(rs.getString("CustPOB"));
					cu.setCustCOB(rs.getString("CustCOB"));
					cu.setCustGroupID(rs.getLong("CustGroupID"));
					cu.setCustSts(rs.getString("CustSts"));
					cu.setCustStsChgDate(rs.getTimestamp("CustStsChgDate"));
					cu.setCustIsStaff(rs.getBoolean("CustIsStaff"));
					cu.setCustIndustry(rs.getString("CustIndustry"));
					cu.setCustSector(rs.getString("CustSector"));
					cu.setCustSubSector(rs.getString("CustSubSector"));
					cu.setCustEmpSts(rs.getString("CustEmpSts"));
					cu.setCustSegment(rs.getString("CustSegment"));
					cu.setCustSubSegment(rs.getString("CustSubSegment"));
					cu.setCustParentCountry(rs.getString("CustParentCountry"));
					cu.setCustResdCountry(rs.getString("CustResdCountry"));
					cu.setCustRiskCountry(rs.getString("CustRiskCountry"));
					cu.setCustNationality(rs.getString("CustNationality"));
					cu.setSalariedCustomer(rs.getBoolean("SalariedCustomer"));
					cu.setCustSuspSts(rs.getBoolean("CustSuspSts"));
					cu.setCustSuspDate(rs.getTimestamp("CustSuspDate"));
					cu.setCustSuspTrigger(rs.getString("CustSuspTrigger"));
					cu.setCustAppDate(rs.getTimestamp("CustAppDate"));

					return cu;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public int insertCustomerQueueing(long groupId, boolean eodProcess) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Active", true);
		source.addValue("CustGroupId", groupId);

		StringBuilder sql = new StringBuilder();
		sql.append("insert into CustomerQueuing (CustID, EodDate, ThreadId, Progress, LoanExist, StartTime, EndTime");
		sql.append(", LimitRebuild, EodProcess)");
		sql.append(" select distinct CustID, ?, ?, ?, ?, ?, ?, ?, ? FROM Customers Where CustGroupId = ?");
		sql.append(" And CustId NOT IN (Select Distinct CustId from CUSTOMERQUEUING)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					if (App.DATABASE == Database.POSTGRES) {
						ps.setObject(1, SysParamUtil.getAppValueDate());
					} else {
						ps.setDate(1, DateUtil.getSqlDate(SysParamUtil.getAppValueDate()));
					}

					ps.setInt(2, 0);
					ps.setInt(3, EodConstants.PROGRESS_IN_PROCESS);
					ps.setBoolean(4, false);

					if (App.DATABASE == Database.POSTGRES) {
						ps.setObject(5, DateUtility.getSysDate());
					} else {
						ps.setDate(5, DateUtil.getSqlDate(DateUtility.getSysDate()));
					}

					if (App.DATABASE == Database.POSTGRES) {
						ps.setObject(6, DateUtility.getSysDate());
					} else {
						ps.setDate(6, DateUtil.getSqlDate(DateUtility.getSysDate()));
					}

					ps.setBoolean(7, false);
					ps.setBoolean(8, eodProcess);
					ps.setLong(9, groupId);
				}
			});
		} catch (Exception e) {
			throw e;
		}

		sql = new StringBuilder("Update CustomerQueuing set Progress = ?");
		sql.append(" where CustId in (SELECT Distinct CustID FROM Customers");
		sql.append(" where CustGroupId = ? and CustId IN (Select Distinct CustId from CUSTOMERQUEUING))");

		logger.trace(Literal.SQL + sql.toString());
		int count = this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, EodConstants.PROGRESS_IN_PROCESS);
				ps.setLong(9, groupId);
			}
		});

		return count;
	}

	@Override
	public void updateCustomerQueuingStatus(long custGroupId, int progress) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update CustomerQueuing set");
		sql.append(" EndTime = ?, Progress = ?");
		sql.append(" Where CustID in (SELECT Distinct CustID FROM Customers");
		sql.append(" Where CustGroupId = ? And CustId IN (Select Distinct CustId from CustomerQueuing))");

		logger.trace(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setDate(1, JdbcUtil.getDate(DateUtility.getSysDate()));

				if (App.DATABASE == Database.POSTGRES) {
					ps.setObject(1, JdbcUtil.getDate(DateUtility.getSysDate()));
				} else {
					ps.setDate(1, JdbcUtil.getDate(DateUtility.getSysDate()));
				}

				ps.setInt(2, progress);
				ps.setLong(3, custGroupId);
			}
		});

	}

	/**
	 * update the Rebuild flag as true if the structure has been changed.
	 */
	@Override
	public void updateLimitRebuild() {
		StringBuilder sql = new StringBuilder("Update CustomerQueuing set LimitRebuild = ?");
		sql.append(" Where CUSTID in (Select  T1.CUSTOMERID from LimitHeader T1");
		sql.append(" Inner Join LimitStructure T2 on T2.STRUCTURECODE = T1.LIMITSTRUCTURECODE and T2.REBUILD = ?)");

		logger.trace(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;

				ps.setInt(index++, 1);
				ps.setInt(index++, 1);
			}
		});
	}

	/**
	 * Insert into CustomerQueuing for Customer Rebuild
	 */
	@Override
	public void insertCustQueueForRebuild(CustomerQueuing cq) {
		StringBuilder sql = new StringBuilder("insert into");
		sql.append(" CustomerQueuing");
		sql.append("(CustID, EodDate, ThreadId, StartTime, Progress, LoanExist, LimitRebuild, EodProcess");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.trace(Literal.SQL + sql.toString());

		try {
			jdbcTemplate.getJdbcOperations().update(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;

					ps.setLong(index++, JdbcUtil.setLong(cq.getCustID()));

					if (App.DATABASE == Database.POSTGRES) {
						ps.setObject(index++, JdbcUtil.getDate(DateUtility.getSysDate()));
					} else {
						ps.setDate(index++, JdbcUtil.getDate(cq.getEodDate()));
					}

					ps.setInt(index++, cq.getThreadId());

					if (App.DATABASE == Database.POSTGRES) {
						ps.setObject(index++, JdbcUtil.getDate(cq.getStartTime()));
					} else {
						ps.setDate(index++, JdbcUtil.getDate(cq.getStartTime()));
					}

					ps.setInt(index++, cq.getProgress());
					ps.setBoolean(index++, cq.isLoanExist());
					ps.setBoolean(index++, cq.isLimitRebuild());
					ps.setBoolean(index++, cq.isEodProcess());
				}
			});
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Count by Customer ID for Customer Rebuild
	 */
	@Override
	public int getCountByCustId(long custID) {
		String sql = "select coalesce(Count(CustID), 0) from CustomerQueuing where CustID = ?";
		logger.trace(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, new Object[] { custID }, Integer.class);

	}

	/**
	 * Insert into CustomerQueuing_Log after Customer Rebuild
	 */
	@Override
	public void logCustomerQueuingByCustId(long custID) {
		String sql = "insert into CustomerQueuing_Log select * FROM CustomerQueuing Where CustID = ?";
		logger.trace(Literal.SQL + sql);

		this.jdbcOperations.update(sql, new Object[] { custID });
	}

	/**
	 * Delete Customer after customer Rebuild Process
	 */
	@Override
	public void deleteByCustId(long custID) {
		String sql = "Delete From CustomerQueuing Where CustID = ?";
		logger.trace(Literal.SQL + sql);

		this.jdbcOperations.update(sql, new Object[] { custID });
	}

	/**
	 * insert into CustomerQueuing_Log for Customer Group Rebuild
	 */
	@Override
	public void logCustomerQueuingByGrpId(long groupId) {
		StringBuilder sql = new StringBuilder("INSERT INTO CustomerQueuing_Log");
		sql.append(" SELECT * FROM CustomerQueuing");
		sql.append(" where CustID in (SELECT Distinct CustID FROM Customers");
		sql.append(" where CustGroupId = ? and CustId IN (Select Distinct CustId from CustomerQueuing))");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), new Object[] { groupId });
	}

	/**
	 * Delete from CustomerQueuing after Customer Group Rebuild
	 */
	@Override
	public void deleteByGroupId(long groupId) {
		StringBuilder sql = new StringBuilder("Delete From CustomerQueuing");
		sql.append(" Where CustID in (SELECT Distinct CustID FROM Customers");
		sql.append(" Where CustGroupId = ? and CustId IN (Select Distinct CustId from CustomerQueuing))");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), new Object[] { groupId });
	}

	@Override
	public long getCustQueuingCount() {
		String sql = "select count(CustID) from CustomerQueuing";
		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql, Long.class);
	}

	@Override
	public long getLoanCountByProgress() {
		String sql = "select sum(FinCount) from CustomerQueuing where Progress = ?";
		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql, new Object[] { EodConstants.PROGRESS_WAIT }, Long.class);
	}

	@Override
	public int updateThreadIDByLoanCount(Date date, long from, long to, int threadId) {
		try {
			logger.trace(Literal.SQL + UPDATE_LOANCOUNT);
			return this.jdbcOperations.update(UPDATE_LOANCOUNT, new Object[] { threadId, from, to, 0 });
		} catch (EmptyResultDataAccessException dae) {
			logger.error(Literal.EXCEPTION, dae);
		}

		return 0;

	}

}
