package com.pennanttech.external.silien.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.silien.dao.ExtLienMarkingDAO;
import com.pennanttech.external.silien.model.LienMarkDetail;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class LienMarkingService implements InterfaceConstants {
	private static final Logger logger = LogManager.getLogger(LienMarkingService.class);

	private static final String FETCH_QUERY = " SELECT * FROM LOAN_SILIEN WHERE STATUS = ? AND CREATED_DATE < TO_DATE(?, 'YYYY-MM-DD')";

	private ExtLienMarkingDAO externalLienMarkingDAO;
	private DataSource dataSource;

	public void processLienRecords() {
		logger.debug(Literal.ENTERING);
		try {
			fetchAllLoansWithSI();
			verifyLoanRepaymentTypeAndProcessLien();
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void fetchAllLoansWithSI() {
		logger.debug(Literal.ENTERING);
		externalLienMarkingDAO.processAllLoansWithSIAndSave(LIEN_MARK, STATE_ACTIVE);
		logger.debug(Literal.LEAVING);
	}

	private void verifyLoanRepaymentTypeAndProcessLien() {
		logger.debug(Literal.ENTERING);

		JdbcCursorItemReader<LienMarkDetail> cursorItemReader = new JdbcCursorItemReader<LienMarkDetail>();
		cursorItemReader.setDataSource(dataSource);
		cursorItemReader.setFetchSize(10);
		cursorItemReader.setSql(FETCH_QUERY);
		cursorItemReader.setRowMapper(new RowMapper<LienMarkDetail>() {
			@Override
			public LienMarkDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
				LienMarkDetail lienMarkDetail = new LienMarkDetail();
				lienMarkDetail.setAccNumber(rs.getString("ACCNUMBER"));
				lienMarkDetail.setCustId(rs.getLong("CUSTID"));
				lienMarkDetail.setFinId(rs.getLong("FINID"));
				lienMarkDetail.setLienMark(rs.getString("LIEN_MARK"));
				lienMarkDetail.setStatus(rs.getInt("STATUS"));
				return lienMarkDetail;
			}
		});

		cursorItemReader.setPreparedStatementSetter(new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, STATE_ACTIVE);
				ps.setDate(2, JdbcUtil.getDate(DateUtil.getSysDate()));
			}
		});

		ExecutionContext executionContext = new ExecutionContext();
		cursorItemReader.open(executionContext);

		LienMarkDetail lienMarkDetail;
		try {
			while ((lienMarkDetail = cursorItemReader.read()) != null) {

				String repaymentMode = externalLienMarkingDAO.fetchRepaymentMode(lienMarkDetail);

				// Check if repayment method is not SI, make record as Lien Removal
				// Repayment mode has been changed for this loan, so we update the table marking as lien removal
				if (!CONFIG_SI_REQ.equals(repaymentMode)) {
					lienMarkDetail.setLienMark(LIEN_REMOVE);
					externalLienMarkingDAO.updateLienMarkRecord(lienMarkDetail);
				}

				long retCount = externalLienMarkingDAO.verifyLoanWithLienMarking(lienMarkDetail.getAccNumber(), "Y");

				String LIEN_UPDATE = "";

				// If retCount > 0 , then the Account is having lien mark in other loan with same repayment mode.
				if (retCount > 0) {
					// Mark Lien: If accnumber is found in table, update or insert the lien status as Y
					LIEN_UPDATE = LIEN_MARK;
				} else {
					// Remove Lien: If accnumber is found in table, update or insert the lien status as N
					LIEN_UPDATE = LIEN_REMOVE;
				}

				externalLienMarkingDAO.insertOrUpdateLienMarkStatusRecord(lienMarkDetail.getAccNumber(), LIEN_UPDATE,
						0);

			}
			cursorItemReader.close();
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void setExternalLienMarkingDAO(ExtLienMarkingDAO externalLienMarkingDAO) {
		this.externalLienMarkingDAO = externalLienMarkingDAO;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
