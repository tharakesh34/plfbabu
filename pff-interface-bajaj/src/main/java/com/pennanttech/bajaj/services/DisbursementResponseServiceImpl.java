package com.pennanttech.bajaj.services;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.process.DisbursementProcess;
import com.pennanttech.pff.core.process.PaymentProcess;
import com.pennanttech.pff.core.services.DisbursementResponseService;

public class DisbursementResponseServiceImpl extends BajajService implements DisbursementResponseService {
	private final Logger		logger	= Logger.getLogger(getClass());

	@Autowired
	private DisbursementProcess	disbursementProcess;
	
	@Autowired
	private PaymentProcess paymentProcess;

	public DisbursementResponseServiceImpl() {
		super();
	}

	@Override
	public void receiveResponse(Object... params) throws Exception {
		logger.debug(Literal.ENTERING);
		
		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;
		
	 	//Disbursements
		List<FinAdvancePayments> disbursements = null;
		RowMapper<FinAdvancePayments> rowMapper = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT FA.PAYMENTID,FA.FINREFERENCE, FA.LINKEDTRANID, DR.PAYMENT_DATE DISBDATE, FA.PAYMENTTYPE, DR.STATUS,");
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
		}  
		
		//Payments..
		List<PaymentInstruction> instructions = null;
		RowMapper<PaymentInstruction> instructionRowMapper = null;
		try { 
			sql = new StringBuilder();
			sql.append(" SELECT PH.FINREFERENCE, PH.LINKEDTRANID, PI.PAYMENTID, PI.BANKBRANCHID, PI.ACCOUNTNO, ");
			sql.append(" PI.ACCTHOLDERNAME, PI.PHONECOUNTRYCODE, PI.PHONENUMBER, PI.PAYMENTINSTRUCTIONID, PI.PAYMENTAMOUNT,");
			sql.append(" PI.PAYMENTAMOUNT, PI.PAYMENTTYPE, DR.STATUS, DR.REJECT_REASON REJECTREASON,");
			sql.append(" DR.PAYMENT_DATE CLEARINGDATE, DR.TRANSACTIONREF");
			sql.append(" FROM DISBURSEMENT_REQUESTS DR");
			sql.append(" INNER JOIN PAYMENTINSTRUCTIONS PI ON PI.PAYMENTINSTRUCTIONID = DR.DISBURSEMENT_ID");
			sql.append(" INNER JOIN PAYMENTHEADER PH ON PH.PAYMENTID = PI.PAYMENTID");
			sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID");
			paramMap = new MapSqlParameterSource();
			paramMap.addValue("RESP_BATCH_ID", params[0]);
			
			instructionRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PaymentInstruction.class);
			instructions = namedJdbcTemplate.query(sql.toString(), paramMap, instructionRowMapper);
			
			for (PaymentInstruction instruction : instructions) {
				try {
					paymentProcess.process(instruction);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} 
		
		logger.debug(Literal.LEAVING);
	}
}
