package com.pennant.backend.dao.impl;

import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.TATDetailDAO;
import com.pennant.backend.model.finance.TATDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class TATDetailDAOImpl extends SequenceDao<TATDetail> implements TATDetailDAO {
	private static Logger logger = LogManager.getLogger(TATDetailDAOImpl.class);

	public TATDetailDAOImpl() {
		super();
	}

	@Override
	public TATDetail getTATDetail(String reference, String rolecode) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" T1.Reference, T1.RoleCode, T1.SerialNo, T1.TATStartTime");
		sql.append(" From TATDetails T1");
		sql.append(" Inner Join (SELECT Reference, MAX(SerialNo) SerialNo, RoleCode");
		sql.append(" From TATDetails Where Reference = ? and RoleCode = ?");
		sql.append(" group by Reference, RoleCode) T2 ON T1.Reference = T2.Reference");
		sql.append(" and T1.RoleCode = T2.RoleCode and T1.SerialNo = T2.SerialNo");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				TATDetail tat = new TATDetail();

				tat.setReference(rs.getString("Reference"));
				tat.setRoleCode(rs.getString("RoleCode"));
				tat.setSerialNo(rs.getLong("SerialNo"));
				tat.settATStartTime(rs.getTimestamp("TATStartTime"));

				return tat;
			}, reference, rolecode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void save(TATDetail tat) {
		tat.setSerialNo(getNextValue("SeqTATDetails"));

		StringBuilder sql = new StringBuilder("Insert Into TatDetails");
		sql.append(" (Module, Reference, SerialNo, RoleCode, TATStartTime, TATEndTime, FinType)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, tat.getModule());
				ps.setString(index++, tat.getReference());
				ps.setLong(index++, tat.getSerialNo());
				ps.setString(index++, tat.getRoleCode());
				ps.setTimestamp(index++, tat.gettATStartTime());
				ps.setTimestamp(index++, tat.gettATEndTime());
				ps.setString(index, tat.getFinType());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	public void update(TATDetail aTatDetail) {
		logger.debug(Literal.ENTERING);

		TATDetail tatDetail = getTATDetail(aTatDetail.getReference(), aTatDetail.getRoleCode());
		Timestamp startTime = tatDetail.gettATStartTime();

		tatDetail.settATEndTime(aTatDetail.gettATEndTime());
		tatDetail.settATStartTime(startTime == null ? aTatDetail.gettATStartTime() : startTime);
		tatDetail.setTriggerTime(aTatDetail.getTriggerTime());

		StringBuilder sql = new StringBuilder(" Update TATDetails");
		sql.append(" Set TatStartTime = ?, TATEndTime = ?, TriggerTime = ?");
		sql.append(" Where Reference = ? and SerialNo = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setTimestamp(index++, startTime);
			ps.setTimestamp(index++, tatDetail.gettATEndTime());
			ps.setTimestamp(index++, tatDetail.getTriggerTime());

			ps.setString(index++, aTatDetail.getReference());
			ps.setLong(index, aTatDetail.getSerialNo());
		});

		logger.debug(Literal.LEAVING);
	}
}
