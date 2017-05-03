package com.pennanttech.bajaj.services;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.services.disbursement.DisbursementProcess;
import com.pennanttech.pff.core.services.disbursement.DisbursementResponse;

public class DisbursementResponseImpl extends BajajService implements DisbursementResponse {
	private final Logger		logger	= Logger.getLogger(getClass());

	@Autowired
	private DisbursementProcess	disbursementProcess;

	public DisbursementResponseImpl() {
		super();
	}

	@Override
	public void receiveResponse(Object... params) throws Exception {
		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;
		List<FinAdvancePayments> disbursements = null;
		RowMapper<FinAdvancePayments> rowMapper = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT FA.PAYMENTID,FA.FINREFERENCE, FA.LINKEDTRANID, DR.PAYMENT_DATE LLDATE, FA.PAYMENTTYPE, DR.STATUS,");
			sql.append(" FA.BENEFICIARYACCNO, FA.BENEFICIARYNAME, FA.BANKBRANCHID, FA.BANKCODE,");
			sql.append(" FA.PHONECOUNTRYCODE, FA.PHONENUMBER, FA.PHONEAREACODE,");
			sql.append(" DR.CHEQUE_NUMBER LLREFERENCENO, DR.REJECT_REASON REJECTREASON,");
			sql.append(" DR.PAYMENT_DATE CLEARINGDATE, DR.TRANSACTIONREF");
			sql.append(" FROM DISBURSEMENT_REQUESTS DR");
			sql.append(" INNER JOIN FINADVANCEPAYMENTS FA ON FA.PAYMENTID = DR.DISBURSEMENT_ID");
			sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID");
			paramMap = new MapSqlParameterSource();
			paramMap.addValue("RESP_BATCH_ID", params[0]);

			rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinAdvancePayments.class);
			disbursements = namedJdbcTemplate.query(sql.toString(), paramMap, rowMapper);

			for (FinAdvancePayments disbursement : disbursements) {
				try {
					disbursementProcess.process(disbursement);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			sql = null;
			paramMap = null;
			rowMapper = null;
		}
	}
}
