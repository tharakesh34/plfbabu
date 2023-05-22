package com.pennant.eod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.RepayConstants;
import com.pennant.eod.beans.PaymentRecoveryDetail;
import com.pennant.eod.beans.PaymentRecoveryHeader;
import com.pennant.eod.dao.PaymentRecoveryDetailDAO;
import com.pennant.eod.dao.PaymentRecoveryHeaderDAO;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;

public class PaymentRecoveryService {

	private static Logger logger = LogManager.getLogger(PaymentRecoveryService.class);

	private DataSource dataSource;
	private PaymentRecoveryHeaderDAO paymentRecoveryHeaderDAO;
	private PaymentRecoveryDetailDAO paymentRecoveryDetailDAO;

	public PaymentRecoveryService() {
		super();
	}

	public void save(List<ReturnDataSet> listoftransactions, FinRepayQueue finRepayQueue, PaymentRecoveryHeader header,
			boolean negateCounter) {

		int priority = 0;

		if (negateCounter) {
			priority = header.getNegativeCounter();
		} else {
			priority = header.getPositiveCounter();
		}

		List<PaymentRecoveryDetail> list = new ArrayList<PaymentRecoveryDetail>();

		for (ReturnDataSet dataSet : listoftransactions) {

			if (dataSet.getPostAmount().compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}

			/*
			 * only T24 records should be considered. derived record's will not be considered since those will be merged
			 * with actual order in file preparation
			 */
			if (!dataSet.getPostToSys().equals("T") || dataSet.getDerivedTranOrder() != 0) {
				continue;
			}

			// to calculate the number of the records in the file
			String ref = dataSet.getFinReference() + "/" + dataSet.getFinEvent() + "/" + dataSet.getTransOrder() + "/"
					+ DateUtil.format(dataSet.getValueDate(), BatchFileUtil.DATE_FORMAT_YMd);

			// get derived entry with the matching transaction order
			ReturnDataSet otherleg = getMatchingEntry(dataSet.getTransOrder(), listoftransactions);

			PaymentRecoveryDetail detail = new PaymentRecoveryDetail();
			detail.setBatchRefNumber(header.getBatchRefNumber());

			detail.setTransactionReference(ref);
			// debit details
			detail.setScheduleDate(dataSet.getValueDate());
			detail.setPaymentAmount(dataSet.getPostAmount());
			detail.setDebitCurrency(dataSet.getAcCcy());
			detail.setPrimaryDebitAccount(dataSet.getAccount());
			// Credit details
			detail.setCreditAccount(otherleg.getAccount());
			detail.setCreditCurrency(otherleg.getAcCcy());

			detail.setFinanceReference(dataSet.getFinReference());
			detail.setCustomerReference(dataSet.getCustCIF());
			detail.setTransactionPurpose(dataSet.getTranDesc());
			detail.setFinanceBranch(dataSet.getPostBranch());
			detail.setFinanceType(dataSet.getFinType());
			detail.setFinancePurpose(dataSet.getFinPurpose());
			if (negateCounter) {
				detail.setPriority(--priority);
			} else {
				detail.setPriority(++priority);
			}

			detail.setFinEvent(dataSet.getFinEvent());
			detail.setFinRpyFor(dataSet.getFinRpyFor());
			list.add(detail);
		}

		if (negateCounter) {
			header.setNegativeCounter(priority);
		} else {
			header.setPositiveCounter(priority);
		}

		getPaymentRecoveryDetailDAO().save(list);

	}

	public void save(PaymentRecoveryHeader header) {
		getPaymentRecoveryHeaderDAO().save(header);
	}

	public void updateTotal(PaymentRecoveryHeader header) {
		getPaymentRecoveryHeaderDAO().updateCount(header);
	}

	/**
	 * @param finRepay
	 * @return
	 */
	public BigDecimal getTotalPaid(FinRepayQueue finRepay, String bathRef) {

		String rpyMethod = ImplementationConstants.REPAY_HIERARCHY_METHOD;

		BigDecimal totalpaidAmount = BigDecimal.ZERO;

		if (rpyMethod.equals(RepayConstants.REPAY_HIERARCHY_FIPCS)
				|| rpyMethod.equals(RepayConstants.REPAY_HIERARCHY_FPICS)) {

			totalpaidAmount = getRepayPaid(finRepay.getFinReference(), finRepay.getRpyDate(), AccountingEvent.REPAY,
					bathRef);

			totalpaidAmount = totalpaidAmount.add(getPenaltyPaidSeparately(finRepay, finRepay.getRpyDate(), bathRef));

		} else {
			totalpaidAmount = getRepayPaid(finRepay.getFinReference(), finRepay.getRpyDate(), null, bathRef);
		}

		return totalpaidAmount;
	}

	public void moveDataToLog(String batchRefNumber) {

		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("BatchRefNumber", batchRefNumber);

		String moveheader = "INSERT  PaymentRecoveryHeader_Log SELECT *  FROM PaymentRecoveryHeader WHERE BatchRefNumber=:BatchRefNumber ";
		String moveDetails = "INSERT  PaymentRecoveryDetail_Log SELECT *  FROM PaymentRecoveryDetail WHERE BatchRefNumber=:BatchRefNumber ";
		String deleteDetails = "DELETE  FROM PaymentRecoveryDetail WHERE BatchRefNumber=:BatchRefNumber ";
		String deleteheader = "DELETE FROM  PaymentRecoveryHeader WHERE BatchRefNumber=:BatchRefNumber ";

		jdbcTemplate.update(moveheader, source);
		jdbcTemplate.update(moveDetails, source);
		jdbcTemplate.update(deleteDetails, source);
		jdbcTemplate.update(deleteheader, source);
	}

	/**
	 * @param repayQueue
	 * @param schdDate
	 * @return
	 */
	private BigDecimal getPenaltyPaidSeparately(FinRepayQueue repayQueue, Date schdDate, String bathRef) {

		BigDecimal actulapnalty = BigDecimal.ZERO;

		BigDecimal totalPaidbyCustomer = getValue(getTotalPaidByCustomer(repayQueue, bathRef));

		BigDecimal totalbalancebyCustomer = getValue(getTotalDueAmtByCustomer(repayQueue));

		BigDecimal penaltypaidBalance = totalPaidbyCustomer.subtract(totalbalancebyCustomer);

		if (penaltypaidBalance.compareTo(BigDecimal.ZERO) > 0) {

			BigDecimal totalPenaltypaidnow = getValue(getTotalPenaltyPaidTillNow(repayQueue));

			actulapnalty = penaltypaidBalance.subtract(totalPenaltypaidnow);

			if (actulapnalty.compareTo(BigDecimal.ZERO) <= 0) {
				return BigDecimal.ZERO;
			} else {
				return actulapnalty;
			}

		} else {
			penaltypaidBalance = BigDecimal.ZERO;
		}

		return actulapnalty;
	}

	/**
	 * @param finRef
	 * @param schdDate
	 * @param event
	 * @return
	 */
	private BigDecimal getRepayPaid(String finRef, Date schdDate, String event, String bathRef) {

		List<PaymentRecoveryDetail> recovery = getPaymentRecoveryDetailDAO().getPaymentRecoveryByid(bathRef, finRef,
				schdDate, event);

		return getPaidAmount(recovery);
	}

	/**
	 * @param finMain
	 * @return
	 */
	private BigDecimal getTotalPaidByCustomer(FinRepayQueue finMain, String bathRef) {

		List<PaymentRecoveryDetail> list = getPaymentRecoveryDetailDAO().getPaymentRecoveryByCustomer(bathRef,
				String.valueOf(finMain.getCustomerID()));

		return getPaidAmount(list);

	}

	/**
	 * @param finMain
	 * @return
	 */
	private BigDecimal getTotalDueAmtByCustomer(FinRepayQueue finMain) {

		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustomerID", finMain.getCustomerID());

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select SUM(SchdPftBal+SchdPriBal) from FinRpyQueue where CustomerID=:CustomerID ");
		BigDecimal tobepaid = jdbcTemplate.queryForObject(selectSql.toString(), source, BigDecimal.class);

		return tobepaid;
	}

	/**
	 * @param finMain
	 * @return
	 */
	private BigDecimal getTotalPenaltyPaidTillNow(FinRepayQueue finMain) {

		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustomerID", finMain.getCustomerID());

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" select SUM(PenaltyPayNow) from FinRpyQueue where CustomerID=:CustomerID ");
		BigDecimal tobepaid = jdbcTemplate.queryForObject(selectSql.toString(), source, BigDecimal.class);

		return tobepaid;
	}

	/**
	 * @param list
	 * @return
	 */
	private BigDecimal getPaidAmount(List<PaymentRecoveryDetail> list) {

		BigDecimal totalbyCustomer = BigDecimal.ZERO;

		if (list != null && !list.isEmpty()) {
			for (PaymentRecoveryDetail paymentRecoveryDetail : list) {

				BigDecimal postAmt = BigDecimal.ZERO;

				if (paymentRecoveryDetail.getPrimaryAcDebitAmt() != null) {
					postAmt = paymentRecoveryDetail.getPrimaryAcDebitAmt();
				}

				if (!StringUtils.trimToEmpty(paymentRecoveryDetail.getSecondaryAcDebitAmt()).equals("")) {
					String[] secondaryAmt = paymentRecoveryDetail.getSecondaryAcDebitAmt().split(";");
					for (String string : secondaryAmt) {
						postAmt = postAmt.add(new BigDecimal(string));
					}
				}
				totalbyCustomer = totalbyCustomer.add(postAmt);
			}
		}

		return totalbyCustomer;
	}

	/**
	 * @param transOrder
	 * @param listoftransactions
	 * @return
	 */
	public ReturnDataSet getMatchingEntry(int transOrder, List<ReturnDataSet> listoftransactions) {
		logger.debug(" Entering ");

		for (ReturnDataSet returnDataSet : listoftransactions) {
			if (returnDataSet.getDerivedTranOrder() == transOrder) {
				return returnDataSet;
			}
		}

		logger.debug(" Leaving ");
		return null;
	}

	/**
	 * @param value
	 * @return
	 */
	private BigDecimal getValue(BigDecimal value) {
		if (value == null) {
			return BigDecimal.ZERO;
		} else {
			return value;
		}
	}

	public PaymentRecoveryHeaderDAO getPaymentRecoveryHeaderDAO() {
		return paymentRecoveryHeaderDAO;
	}

	public void setPaymentRecoveryHeaderDAO(PaymentRecoveryHeaderDAO paymentRecoveryHeaderDAO) {
		this.paymentRecoveryHeaderDAO = paymentRecoveryHeaderDAO;
	}

	public PaymentRecoveryDetailDAO getPaymentRecoveryDetailDAO() {
		return paymentRecoveryDetailDAO;
	}

	public void setPaymentRecoveryDetailDAO(PaymentRecoveryDetailDAO paymentRecoveryDetailDAO) {
		this.paymentRecoveryDetailDAO = paymentRecoveryDetailDAO;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
