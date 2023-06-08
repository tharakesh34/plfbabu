package com.pennanttech.external.gst.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennanttech.external.app.constants.InterfaceConstants;
import com.pennanttech.external.gst.model.GSTCompDetail;
import com.pennanttech.external.gst.model.GSTCompHeader;
import com.pennanttech.external.gst.model.GSTInvoiceDetail;
import com.pennanttech.external.gst.model.GSTRequestDetail;
import com.pennanttech.external.gst.model.GSTVoucherDetails;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtGSTDaoImpl extends SequenceDao<Object> implements ExtGSTDao, InterfaceConstants {

	private static final Logger logger = LogManager.getLogger(ExtGSTDaoImpl.class);

	private NamedParameterJdbcTemplate mainNamedJdbcTemplate;
	private NamedParameterJdbcTemplate extNamedJdbcTemplate;

	public ExtGSTDaoImpl() {
		super();
	}

	@Override
	public long getSeqNumber(String tableName) {
		setDataSource(extNamedJdbcTemplate.getJdbcTemplate().getDataSource());
		return getNextValue(tableName);
	}

	@Override
	public void extractDetailsFromForGstCalculation() {

		String sqlQuery = "INSERT INTO GST_VOUCHER_DETAILS(FINREFERENCE,AMOUNT_TYPE,REFERENCE_FIELD1, "
				+ " REFERENCE_FIELD2,REFERENCE_AMOUNT,ACTUAL_AMOUNT,CREATED_DATE) "
				+ " SELECT FINREFERENCE,AMOUNT_TYPE,REFERENCE_FIELD1, "
				+ " REFERENCE_FIELD2,REFERENCE_AMOUNT,ACTUAL_AMOUNT,CREATED_DATE FROM "
				+ " (select  frh.reference FINREFERENCE,'FEE' AMOUNT_TYPE,ffr.feeid REFERENCE_FIELD1, "
				+ " ffr.receiptid  REFERENCE_FIELD2,ffr.paidamount REFERENCE_AMOUNT,frh.receiptamount ACTUAL_AMOUNT, "
				+ " to_date(substr(sysdate,1,10),'YYYY-MM-DD' ) AS CREATED_DATE  " + "   from finfeereceipts ffr  "
				+ " inner join finfeedetail ffd on ffr.FEEID=ffd.FEEID  "
				+ " inner join finreceiptheader frh on ffr.receiptid=frh.receiptid "
				+ " where ffr.paidamount >0 and ffd.TAXAPPLICABLE=1) ";
		logger.debug(Literal.SQL + sqlQuery);
		mainNamedJdbcTemplate.getJdbcOperations().update(sqlQuery);

		String sqlQueryManualAdvice = " INSERT INTO GST_VOUCHER_DETAILS(FINREFERENCE,AMOUNT_TYPE,REFERENCE_FIELD1, "
				+ " REFERENCE_FIELD2,REFERENCE_AMOUNT,ACTUAL_AMOUNT,CREATED_DATE) "
				+ "  SELECT FINREFERENCE,AMOUNT_TYPE,REFERENCE_FIELD1, "
				+ " REFERENCE_FIELD2,REFERENCE_AMOUNT,ACTUAL_AMOUNT,CREATED_DATE FROM "
				+ "   (select mad.FINREFERENCE FINREFERENCE,'ADVISE' AMOUNTTYPE, MOVEMENTID REFERENCE_FIELD1, "
				+ "   madm.RECEIPTID REFERENCE_FIELD2,madm.PAIDAMOUNT REFERENCE_AMOUNT,frh.receiptamount ACTUAL_AMOUNT, "
				+ "   to_date(substr(sysdate,1,10),'YYYY-MM-DD' ) AS CREATED_DATE "
				+ "   from manualadvisemovements madm "
				+ "   inner join manualadvise mad on madm.ADVISEID=mad.ADVISEID "
				+ "   inner join finreceiptheader frh on madm.RECEIPTID=frh.receiptid)";
		logger.debug(Literal.SQL + sqlQueryManualAdvice);
		mainNamedJdbcTemplate.getJdbcOperations().update(sqlQueryManualAdvice);
	}

	public void setExtDataSource(DataSource extDataSource) {
		this.extNamedJdbcTemplate = new NamedParameterJdbcTemplate(extDataSource);
	}

	public void setMainDataSource(DataSource mainDataSource) {
		this.mainNamedJdbcTemplate = new NamedParameterJdbcTemplate(mainDataSource);
	}

	@Override
	public void saveExtractedDetailsToRequestTable() {
		String sql = "";
		sql = "INSERT INTO GST_REQUEST_DETAIL (REQUESTTYPE,CUSTOMERID,ACCOUNTID,GSTIN,HSN,"
				+ " TRANSACTIONPRICEDCHARGE,CHARGEINCLUSIVEOFTAX,TRANSACTIONDATE,TRANSACTIONUID,"
				+ " SOURCESYSTEM,ACCOUNTEXEMPT,BRANCHEXEMPT,SERVICECHARGEEXEMPT,TRANSACTIONEXEMPT,"
				+ " RELATEDENTITY,TAXSPLITINSOURCESYSTEM,USERCFIELD2,REQUESTDATE,CUSTOMERNAME,CESSAPPLICABLE) "
				+ " (SELECT 'P' AS REQUESTTYPE,FM.CUSTID,GVD.FINREFERENCE,NULL,'997113', "
				+ " GVD.REFERENCE_AMOUNT,'Y',(SELECT TO_CHAR(TO_DATE(SYSPARMVALUE,'YYYY-MM-DD'),'YYYY-MM-DD')FROM SMTPARAMETERS "
				+ " where SYSPARMCODE='APP_DATE') AS TRANSACTIONDATE,GVD.GST_VOUCHER_ID, 'PLF','N','N','N','N', "
				+ " 'N','N','LEA',(SELECT TO_CHAR(TO_DATE(SYSPARMVALUE,'YYYY-MM-DD'),'YYYY-MM-DD')FROM SMTPARAMETERS where "
				+ " SYSPARMCODE='APP_DATE') AS REQUESTDATE,CU.CUSTSHRTNAME,'N' FROM GST_VOUCHER_DETAILS GVD "
				+ " INNER JOIN FINANCEMAIN FM ON GVD.FINREFERENCE = FM.FINREFERENCE "
				+ " INNER JOIN CUSTOMERS CU ON FM.CUSTID = CU.CUSTID  WHERE GVD.PROCESSED =? AND GVD.REQ_FILE_ID = ?)";

		logger.debug(Literal.SQL + sql);

		mainNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			ps.setInt(1, UNPROCESSED);// PROCESSED =?
			ps.setInt(2, UNPROCESSED);// REQ_FILE_ID =?
		});
	}

	@Override
	public List<GSTRequestDetail> fetchRecords(int status) {
		logger.debug(Literal.ENTERING);

		List<GSTRequestDetail> gstRequestList = new ArrayList<GSTRequestDetail>();

		StringBuilder query = new StringBuilder();
		query.append(" SELECT * FROM GST_REQUEST_DETAIL WHERE STATUS = ? ");

		logger.debug(Literal.SQL + query.toString());

		mainNamedJdbcTemplate.getJdbcOperations().query(query.toString(), ps -> {
			ps.setInt(1, status);
		}, rs -> {
			GSTRequestDetail detail = new GSTRequestDetail();
			detail.setRequestType(rs.getString("REQUESTTYPE"));
			detail.setCustomerId(rs.getString("CUSTOMERID"));
			detail.setAccountId(rs.getString("ACCOUNTID"));
			detail.setGstin(rs.getString("GSTIN"));
			detail.setHsn(rs.getString("HSN"));
			detail.setTransactionPricedCharge(rs.getBigDecimal("TRANSACTIONPRICEDCHARGE"));
			detail.setChargeInclusiveOfTax(rs.getString("CHARGEINCLUSIVEOFTAX"));
			detail.setTransactionDate(rs.getDate("TRANSACTIONDATE"));
			detail.setTransactionUid(rs.getLong("TRANSACTIONUID"));
			detail.setSourceSystem(rs.getString("SOURCESYSTEM"));
			detail.setAccountExempt(rs.getString("ACCOUNTEXEMPT"));
			detail.setBranchExempt(rs.getString("BRANCHEXEMPT"));
			detail.setServiceChargeExempt(rs.getString("SERVICECHARGEEXEMPT"));
			detail.setTransactionExempt(rs.getString("TRANSACTIONEXEMPT"));
			detail.setRelatedEntity(rs.getString("RELATEDENTITY"));
			detail.setTaxSplitInsourceSystem(rs.getString("TAXSPLITINSOURCESYSTEM"));
			detail.setUsercField2(rs.getString("USERCFIELD2"));
			detail.setRequestDate(rs.getDate("REQUESTDATE"));
			detail.setCustomerName(rs.getString("CUSTOMERNAME"));
			detail.setCessApplicable(rs.getString("CESSAPPLICABLE"));
			gstRequestList.add(detail);
		});
		logger.debug(Literal.LEAVING);

		return gstRequestList;
	}

	@Override
	public boolean isFileProcessed(String respFileName) {
		logger.debug(Literal.ENTERING);
		String sql = "Select count(1) from GSTCOMPHEADER Where FILE_NAME= ?";
		logger.debug(Literal.SQL + sql);
		try {
			return extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class, respFileName) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public void saveResponseFile(GSTCompHeader compHeader) {
		logger.info(Literal.ENTERING);
		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());
		StringBuilder sql = new StringBuilder("INSERT INTO GSTCOMPHEADER");
		sql.append(" (FILE_NAME,FILE_LOCATION,STATUS,EXTRACTION,CREATED_DATE,ERROR_CODE,ERROR_MESSAGE)");
		sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, compHeader.getFileName());
			ps.setString(index++, compHeader.getFileLocation());
			ps.setLong(index++, compHeader.getStatus());
			ps.setLong(index++, compHeader.getExtraction());
			ps.setTimestamp(index++, curTimeStamp);
			ps.setString(index++, compHeader.getErrorCode());
			ps.setString(index, compHeader.getErrorMessage());

		});

	}

	@Override
	public void updateFileStatus(long id, int status) {
		StringBuilder sql = new StringBuilder("UPDATE GSTCOMPHEADER");
		sql.append(" SET STATUS = ? WHERE ID= ?");

		logger.debug(Literal.SQL + sql.toString());

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, status);
			ps.setLong(index, id);
		});

	}

	@Override
	public int saveExtGSTCompRecordsData(List<GSTCompDetail> compDetails) {

		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO GSTCOMPDETAILS (");
		sql.append("HEADER_ID, RECORD_DATA, STATUS, CREATED_DATE)");
		sql.append("values(?,?,?,?)");

		logger.debug(Literal.SQL + sql.toString());

		return extNamedJdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				GSTCompDetail item = compDetails.get(index);
				ps.setLong(1, item.getHeaderId());
				ps.setString(2, item.getRecord());
				ps.setLong(3, item.getStatus());
				ps.setTimestamp(4, curTimeStamp);
			}

			@Override
			public int getBatchSize() {
				return compDetails.size();
			}
		}).length;

	}

	@Override
	public void updateGSTRecordDetailStatus(GSTCompDetail detail) {
		StringBuilder sql = new StringBuilder("UPDATE GSTCOMPDETAILS");
		sql.append(" SET STATUS = ?,GST_VOUCHER_ID=?, ERROR_CODE = ?, ERROR_MESSAGE = ? WHERE ID= ? ");

		logger.debug(Literal.SQL + sql.toString());

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, detail.getStatus());
			ps.setLong(index++, detail.getGstVoucherId());
			ps.setString(index++, detail.getErrorCode());
			ps.setString(index++, detail.getErrorMessage());
			ps.setLong(index, detail.getId());
		});

	}

	@Override
	public long saveGSTRequestFileData(String fileName, String fileLocation) {
		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO GSTCOMPREQUESTFILE (");
		sql.append("FILE_NAME, FILE_LOCATION, CREATED_DATE)");
		sql.append("values(?,?,?)");

		logger.debug(Literal.SQL + sql.toString());
		KeyHolder keyHolder = new GeneratedKeyHolder();

		extNamedJdbcTemplate.getJdbcOperations().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });
				int index = 1;
				ps.setString(index++, fileName);
				ps.setString(index++, fileLocation);
				ps.setTimestamp(index, curTimeStamp);

				return ps;
			}
		}, keyHolder);

		Number key = keyHolder.getKey();

		if (key == null) {
			return 0;
		}

		return key.longValue();
	}

	@Override
	public int updateGSTVoucherWithReqHeaderId(List<Long> txnUidList, long headerId) {

		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE GST_VOUCHER_DETAILS  SET REQ_FILE_ID=? WHERE GST_VOUCHER_ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return mainNamedJdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(),
				new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int index) throws SQLException {
						long txnUid = txnUidList.get(index);
						ps.setLong(1, headerId);
						ps.setLong(2, txnUid);
					}

					@Override
					public int getBatchSize() {
						return txnUidList.size();
					}
				}).length;

	}

	@Override
	public GSTVoucherDetails fetchVoucherDetails(long transactionUID) {
		logger.debug(Literal.ENTERING);
		String sql = "Select GST_VOUCHER_ID,FINREFERENCE,AMOUNT_TYPE,ACTUAL_AMOUNT,"
				+ "REFERENCE_AMOUNT,REFERENCE_FIELD1,REFERENCE_FIELD2 "
				+ " from GST_VOUCHER_DETAILS Where GST_VOUCHER_ID= ?";

		logger.debug(Literal.SQL.concat(sql.toString()));

		return mainNamedJdbcTemplate.getJdbcOperations().queryForObject(sql.toString(), (rs, rowNum) -> {
			GSTVoucherDetails gvd = new GSTVoucherDetails();

			gvd.setGstVoucherId(rs.getLong("GST_VOUCHER_ID"));
			gvd.setFinreference(rs.getString("FINREFERENCE"));
			gvd.setAmountType(rs.getString("AMOUNT_TYPE"));
			gvd.setActualAmount(rs.getBigDecimal("ACTUAL_AMOUNT"));
			gvd.setReferenceAmount(rs.getBigDecimal("REFERENCE_AMOUNT"));
			gvd.setReferenceField1(rs.getLong("REFERENCE_FIELD1"));
			gvd.setReferenceField2(rs.getLong("REFERENCE_FIELD2"));

			return gvd;
		}, transactionUID);
	}

	@Override
	public long saveGSTInvoiceDetails(GSTInvoiceDetail gstInvoiceDetail) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO GST_INVOICE_DETAILS (CUSTOMER_NAME,CUSTOMER_ADDRESS,CURRENT_GSTIN,"
				+ "LOAN_BRANCH_ADDRESS,GSTIN,TRANSACTION_DATE, INVOICE_NUMBER,CHARGE_DESCRIPTION,CHARGE_AMOUNT,"
				+ "CGST_RATE,CGST_AMOUNT,SGST_RATE, SGST_AMOUNT,IGST_RATE,IGST_AMOUNT,"
				+ "UGST_RATE,UGST_AMOUNT,CESS_AMOUNT, POP,POS,CIN,PAN,SAC,WEBSITE_ADDRESS,"
				+ "EMAILID,REG_BANK_ADDRESS,DISCLAIMER)"
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

		logger.debug(Literal.SQL + sql.toString());
		KeyHolder keyHolder = new GeneratedKeyHolder();

		mainNamedJdbcTemplate.getJdbcOperations().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });
				int index = 1;
				ps.setString(index++, gstInvoiceDetail.getCustomerName());
				ps.setString(index++, gstInvoiceDetail.getCustomerAddress());
				ps.setString(index++, gstInvoiceDetail.getCurrentGstin());
				ps.setString(index++, gstInvoiceDetail.getLoanBranchAddress());
				ps.setString(index++, gstInvoiceDetail.getGstin());
				ps.setDate(index++, (Date) gstInvoiceDetail.getTransactionDate());
				ps.setString(index++, gstInvoiceDetail.getInvoiceNumber());
				ps.setString(index++, gstInvoiceDetail.getChargeDescription());
				ps.setBigDecimal(index++, gstInvoiceDetail.getChargeAmount());
				ps.setBigDecimal(index++, gstInvoiceDetail.getCgstRate());
				ps.setBigDecimal(index++, gstInvoiceDetail.getCgstAmount());
				ps.setBigDecimal(index++, gstInvoiceDetail.getSgstRate());
				ps.setBigDecimal(index++, gstInvoiceDetail.getSgstAmount());
				ps.setBigDecimal(index++, gstInvoiceDetail.getIgstRate());
				ps.setBigDecimal(index++, gstInvoiceDetail.getIgstAmount());
				ps.setBigDecimal(index++, gstInvoiceDetail.getUgstRate());
				ps.setBigDecimal(index++, gstInvoiceDetail.getUgstAmount());
				ps.setBigDecimal(index++, gstInvoiceDetail.getCessAmount());
				ps.setString(index++, gstInvoiceDetail.getPop());
				ps.setString(index++, gstInvoiceDetail.getPos());
				ps.setString(index++, gstInvoiceDetail.getCin());
				ps.setString(index++, gstInvoiceDetail.getPan());
				ps.setString(index++, gstInvoiceDetail.getSac());
				ps.setString(index++, gstInvoiceDetail.getWebsiteAddress());
				ps.setString(index++, gstInvoiceDetail.getEmailId());
				ps.setString(index++, gstInvoiceDetail.getRegBankAddress());
				ps.setString(index, gstInvoiceDetail.getDisclaimer());

				return ps;
			}
		}, keyHolder);

		Number key = keyHolder.getKey();

		if (key == null) {
			return 0;
		}

		return key.longValue();
	}
}
