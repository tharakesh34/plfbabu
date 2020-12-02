package com.pennanttech.pff.extension.spreadsheet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennanttech.pennapps.core.jdbc.BasicDao;

public class SpreadSheetDataAccess extends BasicDao<FinCreditReviewDetails> {
	public CreditReviewDetails getCreditReviewDetailsByLoanType(CreditReviewDetails crd) {
		return crd;
	}

	public CreditReviewData getCreditReviewDataByRef(String finReference, CreditReviewDetails crd) {
		return null;
	}

	public List<FinCreditReviewDetails> getFinCreditRevDetailIds(long customerId) {
		FinCreditReviewDetails finCreditReviewDetails = new FinCreditReviewDetails();
		finCreditReviewDetails.setCustomerId(customerId);

		StringBuilder sql = new StringBuilder();
		sql.append("Select DetailId, AuditYear");
		sql.append(" FROM FinCreditReviewDetails_view Where CustomerId= ?");

		List<FinCreditReviewDetails> list = new ArrayList<>();
		try {
			list = jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setLong(1, customerId);

				}
			}, new RowMapper<FinCreditReviewDetails>() {

				@Override
				public FinCreditReviewDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinCreditReviewDetails crd = new FinCreditReviewDetails();
					crd.setDetailId(rs.getLong("DetailId"));
					crd.setAuditYear(rs.getString("AuditYear"));
					return crd;
				}
			});
		} catch (Exception e) {

		}

		return list;
	}
}
