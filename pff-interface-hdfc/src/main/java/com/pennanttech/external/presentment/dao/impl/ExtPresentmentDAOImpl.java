package com.pennanttech.external.presentment.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.util.RepayConstants;
import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.external.presentment.dao.ExtPresentmentDAO;
import com.pennanttech.external.presentment.model.ExtBounceReason;
import com.pennanttech.external.presentment.model.ExtPresentment;
import com.pennanttech.external.presentment.model.ExtPresentmentData;
import com.pennanttech.external.presentment.model.ExtPresentmentFile;
import com.pennanttech.external.presentment.model.ExtPrmntRespHeader;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

public class ExtPresentmentDAOImpl extends SequenceDao<Presentment> implements ExtPresentmentDAO, InterfaceConstants {
	private static final Logger logger = LogManager.getLogger(ExtPresentmentDAOImpl.class);

	private NamedParameterJdbcTemplate mainNamedJdbcTemplate;
	private NamedParameterJdbcTemplate extNamedJdbcTemplate;

	public ExtPresentmentDAOImpl() {
		super();
	}

	@Override
	public long getSeqNumber(String tableName) {
		setDataSource(extNamedJdbcTemplate.getJdbcTemplate().getDataSource());
		return getNextValue(tableName);
	}

	@Override
	public List<ExtPresentmentFile> getACHPresentmentDetails(PresentmentHeader presentmentHeader) {
		logger.debug(Literal.ENTERING);

		List<ExtPresentmentFile> achPresentmentList = new ArrayList<ExtPresentmentFile>();

		if (presentmentHeader != null) {

			StringBuilder query = new StringBuilder();
			query.append(" SELECT MD.ACCNUMBER,MD.ACCTYPE,MD.ACCHOLDERNAME,MD.MANDATEREF,");
			query.append(" PD.PRESENTMENTAMT,PD.ID,PD.FINREFERENCE,PD.EMINO,");
			query.append(" BK.MICR,BK.IFSC,PD.SCHDATE,PD.PRESENTMENTID,PD.PRESENTMENTREF FROM PRESENTMENTDETAILS pd ");
			query.append(" inner join presentmentheader ph on ph.ID= PD.PRESENTMENTID ");
			query.append(" inner join MANDATES md on md.MANDATEID = pd.MANDATEID ");
			query.append(" inner join Bankbranches bk  on md.Bankbranchid= BK.Bankbranchid  ");
			query.append(" WHERE  ph.ID= ? AND ph.MANDATETYPE = ? AND pd.PRESENTMENTAMT > ?");
			query.append(" AND pd.ExcludeReason = ?");

			logger.debug(Literal.SQL + query.toString());
			mainNamedJdbcTemplate.getJdbcOperations().query(query.toString(), ps -> {
				int i = 1;
				ps.setLong(i++, presentmentHeader.getId());
				ps.setString(i++, presentmentHeader.getMandateType());// FIXME
				ps.setBigDecimal(i++, BigDecimal.ZERO);
				ps.setInt(i++, RepayConstants.PEXC_EMIINCLUDE);
			}, rs -> {
				ExtPresentmentFile details = new ExtPresentmentFile();
				details.setSchDate(rs.getDate("SCHDATE"));
				details.setCustomerName(StringUtils.trimToEmpty(rs.getString("ACCHOLDERNAME")));
				details.setAcType(StringUtils.trimToEmpty(rs.getString("ACCTYPE")));
				details.setBatchReference(StringUtils.trimToEmpty(rs.getString("PRESENTMENTID")));
				details.setAccountNo(StringUtils.trimToEmpty(rs.getString("ACCNUMBER")));
				details.setPresentmentRef(StringUtils.trimToEmpty(rs.getString("PRESENTMENTREF")));// PRESENTMENTREF
				details.setId(rs.getLong("ID"));
				details.setSchAmtDue(rs.getBigDecimal("PRESENTMENTAMT"));
				details.setUtrNumber(StringUtils.trimToEmpty(rs.getString("MANDATEREF")));
				if (rs.getString("IFSC") == null || "".equals(rs.getString("IFSC"))) {
					details.setBankCode(StringUtils.trimToEmpty(rs.getString("MICR")));
				} else {
					details.setBankCode(StringUtils.trimToEmpty(rs.getString("IFSC")));
				}
				achPresentmentList.add(details);
			});
		}
		logger.debug(Literal.LEAVING);
		return achPresentmentList;
	}

	@Override
	public List<ExtPresentmentFile> getSIPresentmentDetails(PresentmentHeader presentmentHeader) {
		logger.debug(Literal.ENTERING);

		List<ExtPresentmentFile> siDetailsList = new ArrayList<ExtPresentmentFile>();

		if (presentmentHeader != null) {

			StringBuilder query = new StringBuilder();
			query.append(" SELECT MD.ACCNUMBER,");
			query.append(" PD.PRESENTMENTAMT,PD.ID,PD.FINREFERENCE,PD.EMINO,FM.NUMBEROFTERMS,");
			query.append(" PD.SCHDATE FROM PRESENTMENTDETAILS pd ");
			query.append(" inner join presentmentheader ph on ph.ID= PD.PRESENTMENTID ");
			query.append(" inner join MANDATES md on md.MANDATEID = pd.MANDATEID ");
			query.append(" inner join FINANCEMAIN fm on FM.FINID= PD.FINID  ");
			query.append(" WHERE  ph.ID= ? AND ph.MANDATETYPE = ? AND pd.PRESENTMENTAMT > ?");
			query.append(" AND pd.ExcludeReason = ?");

			logger.debug(Literal.SQL + query.toString());

			mainNamedJdbcTemplate.getJdbcOperations().query(query.toString(), ps -> {
				int i = 1;
				ps.setLong(i++, presentmentHeader.getId());
				ps.setString(i++, presentmentHeader.getMandateType());// FIXME
				ps.setBigDecimal(i++, BigDecimal.ZERO);
				ps.setInt(i++, RepayConstants.PEXC_EMIINCLUDE);
			}, rs -> {
				ExtPresentmentFile details = new ExtPresentmentFile();
				details.setAccountNo(rs.getString("ACCNUMBER"));
				details.setPresentmentRef(rs.getString("ID"));
				details.setSchAmtDue(rs.getBigDecimal("PRESENTMENTAMT"));
				details.setFinReference(rs.getString("FINREFERENCE"));
				details.setEmiNo(rs.getInt("EMINO"));
				details.setNumberOfTerms(rs.getString("NUMBEROFTERMS"));
				details.setSchDate(rs.getDate("SCHDATE"));
				siDetailsList.add(details);
			});
		}
		logger.debug(Literal.LEAVING);

		return siDetailsList;
	}

	@Override
	public List<ExtPresentmentFile> getSiInternalPresentmentDetails(PresentmentHeader presentmentHeader) {
		logger.debug(Literal.ENTERING);

		String queryStr;

		List<ExtPresentmentFile> siInternalDetailsList = new ArrayList<ExtPresentmentFile>();

		if (presentmentHeader != null) {

			StringBuilder query = new StringBuilder();
			query.append(" SELECT CQ.ACCOUNTNO,");
			query.append(" PD.PRESENTMENTAMT,PD.ID,CQ.CHEQUESERIALNO,PD.FINREFERENCE,PD.EMINO,FM.NUMBEROFTERMS,");
			query.append(" PD.SCHDATE FROM PRESENTMENTDETAILS pd ");
			query.append(" inner join presentmentheader ph on ph.ID= PD.PRESENTMENTID ");
			query.append(" inner join CHEQUEHEADER ch on ch.FINID = pd.FINID ");
			query.append(" inner join FINANCEMAIN fm on FM.FINID= PD.FINID  ");
			query.append(" inner join CHEQUEDETAIL cq on cq.HEADERID = ch.HEADERID ");
			query.append(" WHERE  ph.ID= ? AND ph.MANDATETYPE = ? AND pd.PRESENTMENTAMT > ?");
			query.append(" AND pd.ExcludeReason = ? AND cq.EMIREFNO = pd.EMINO ");

			queryStr = query.toString();

			logger.debug(Literal.SQL + queryStr);

			mainNamedJdbcTemplate.getJdbcOperations().query(queryStr, ps -> {
				int i = 1;
				ps.setLong(i++, presentmentHeader.getId());
				ps.setString(i++, presentmentHeader.getMandateType());// FIXME
				ps.setBigDecimal(i++, BigDecimal.ZERO);
				ps.setInt(i++, RepayConstants.PEXC_EMIINCLUDE);
			}, rs -> {
				ExtPresentmentFile details = new ExtPresentmentFile();
				details.setAccountNo(rs.getString("ACCOUNTNO"));
				details.setPresentmentRef(rs.getString("ID"));// PRESENTMENTREF
				details.setSchAmtDue(rs.getBigDecimal("PRESENTMENTAMT"));
				details.setUtrNumber(rs.getString("CHEQUESERIALNO"));
				details.setFinReference(rs.getString("FINREFERENCE"));
				details.setEmiNo(rs.getInt("EMINO"));
				details.setNumberOfTerms(rs.getString("NUMBEROFTERMS"));
				details.setSchDate(rs.getDate("SCHDATE"));
				siInternalDetailsList.add(details);
			});
		}
		logger.debug(Literal.LEAVING);
		return siInternalDetailsList;
	}

	@Override
	public List<ExtPresentmentFile> getExternalPDCPresentmentDetails(PresentmentHeader presentmentHeader) {
		logger.debug(Literal.ENTERING);
		String queryStr;

		List<ExtPresentmentFile> presentmentFilesList = new ArrayList<ExtPresentmentFile>();

		if (presentmentHeader != null) {

			StringBuilder query = new StringBuilder();
			query.append(
					" SELECT rm.CLUSTERID,rft.FINTYPEDESC,FM.FINBRANCH,rm.BRANCHDESC RBRANCHNAME,PD.FINID,CQ.ACCHOLDERNAME,");
			query.append(" bt.BANKNAME,BK.BANKBRANCHID,BK.BRANCHDESC BBRANCHNAME,");
			query.append(" BK.MICR,PH.ID,CQ.ACCOUNTNO,");
			query.append(" CQ.CHEQUESERIALNO,CQ.CHEQUEDATE,PD.SCHDATE,");
			query.append(" PD.PRESENTMENTAMT,BK.BANKCODE,BK.CITY,pc.PCCITYNAME,bt.BANKSHORTCODE ");
			query.append(" FROM PRESENTMENTDETAILS PD ");
			query.append(" inner join presentmentheader PH on PH.ID= PD.PRESENTMENTID ");
			query.append(" inner join CHEQUEHEADER CH on CH.FINID = PD.FINID ");
			query.append(" inner join FINANCEMAIN FM on FM.FINID= PD.FINID  ");
			query.append(" inner join CHEQUEDETAIL CQ on CQ.HEADERID = CH.HEADERID ");
			query.append(" inner join BANKBRANCHES BK on CQ.BANKBRANCHID = BK.BANKBRANCHID ");
			query.append(" Inner Join RmtProvinceVsCity pc on pc.PcCity = BK.City ");
			query.append(" inner join BMTBankDetail bt on bt.BANKCODE = BK.BANKCODE ");
			query.append(" inner join RMTBRANCHES rm on rm.BRANCHCODE = FM.FINBRANCH ");
			query.append(" inner join RMTFINANCETYPES rft on rft.FINTYPE = FM.FINTYPE ");
			query.append(" WHERE  PH.ID= ? AND PH.MANDATETYPE = ? AND PD.PRESENTMENTAMT > ?");
			query.append(" AND PD.ExcludeReason = ? AND CQ.EMIREFNO = PD.EMINO");

			queryStr = query.toString();

			logger.debug(Literal.SQL + queryStr);

			mainNamedJdbcTemplate.getJdbcOperations().query(queryStr, ps -> {
				int i = 1;
				ps.setLong(i++, presentmentHeader.getId());
				ps.setString(i++, presentmentHeader.getMandateType());// FIXME
				ps.setBigDecimal(i++, BigDecimal.ZERO);
				ps.setInt(i++, RepayConstants.PEXC_EMIINCLUDE);
			}, rs -> {
				ExtPresentmentFile details = new ExtPresentmentFile();
				details.setClusterId(StringUtils.trimToEmpty(rs.getString("CLUSTERID")));
				details.setFinBranchId(rs.getString("FINBRANCH"));
				details.setFinBranchName(rs.getString("RBRANCHNAME"));
				details.setProduct(rs.getString("FINTYPEDESC"));
				details.setAgreementId(rs.getLong("FINID"));
				details.setCustomerName(rs.getString("ACCHOLDERNAME"));
				details.setBankName(rs.getString("BANKNAME"));
				details.setCityId(rs.getString("CITY"));
				details.setCityName(rs.getString("PCCITYNAME"));
				details.setBankId(rs.getString("BANKCODE"));
				details.setBankBranchId(rs.getString("BANKBRANCHID"));
				details.setBankBranchName(rs.getString("BBRANCHNAME"));
				details.setMicr(rs.getString("MICR"));
				details.setBatchReference(String.valueOf(rs.getLong("ID")));
				details.setAccountNo(rs.getString("ACCOUNTNO"));
				details.setChequeSerialNo(rs.getString("CHEQUESERIALNO"));
				details.setChequeDate(rs.getDate("CHEQUEDATE"));
				details.setSchDate(rs.getDate("SCHDATE"));
				details.setSchAmtDue(rs.getBigDecimal("PRESENTMENTAMT"));
				details.setBankCode(rs.getString("BANKSHORTCODE"));

				presentmentFilesList.add(details);
			});
		}
		logger.debug(Literal.LEAVING);
		return presentmentFilesList;
	}

	@Override
	public int saveExternalRecords(List<Presentment> presentments, long headerId) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO Presentment_Resp_Dtls (");
		sql.append("HEADER_ID, PRESENTMENT_REFERENCE, INSTALMENT_NO, AMOUNT_CLEARED , CLEARING_DATE, CLEARING_STATUS,");
		sql.append(" BOUNCE_CODE, BOUNCE_REMARKS, REASON_CODE,");
		sql.append("ACCOUNT_NUMBER, IFSC_CODE, UMRN_NO, MICR_CODE, CHEQUE_SERIAL_NO) values(");
		sql.append("?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

		logger.debug(Literal.SQL + sql.toString());

		return mainNamedJdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(),
				new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int index) throws SQLException {
						// PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });
						Presentment item = presentments.get(index);
						ps.setLong(1, headerId);
						ps.setString(2, item.getAgreementNo());
						ps.setLong(3, item.getEmiNo());
						ps.setBigDecimal(4, item.getChequeAmount());
						ps.setDate(5, (Date) item.getSetilmentDate());
						ps.setString(6, item.getStatus());
						ps.setLong(7, item.getReturnCode());
						ps.setString(8, item.getReturnReason());
						ps.setLong(9, item.getReturnCode());
						ps.setString(10, item.getAccountNo());
						ps.setString(11, item.getIFSC());
						ps.setString(12, item.getUmrnNo());
						ps.setString(13, item.getMicrCode());
						ps.setString(14, item.getChequeSerialNo());

					}

					@Override
					public int getBatchSize() {
						return presentments.size();
					}
				}).length;
	}

	@Override
	public void saveExtPresentment(ExtPresentment extPresentment) {
		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());
		StringBuilder sql = new StringBuilder("INSERT INTO PRMNT_HEADER");
		sql.append(" (MODULE,FILE_NAME,STATUS,FILE_LOCATION, CREATED_DATE,EXTRACTION,ERROR_CODE,ERROR_MESSAGE)");
		sql.append(" VALUES (");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, extPresentment.getModule());
			ps.setString(index++, extPresentment.getFileName());
			ps.setInt(index++, extPresentment.getStatus());
			ps.setString(index++, extPresentment.getFileLocation());
			ps.setTimestamp(index++, curTimeStamp);
			ps.setInt(index++, extPresentment.getExtraction());
			ps.setString(index++, extPresentment.getErrorCode());
			ps.setString(index, extPresentment.getErrorMessage());
		});

	}

	@Override
	public boolean isFileProcessed(String fileName, String moduleName) {
		String sql = "Select count(1) from PRMNT_HEADER Where FILE_NAME= ? AND MODULE = ?";
		logger.debug(Literal.SQL + sql);
		try {
			return extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class, fileName,
					moduleName) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public Presentment getPresenementMandateRecord(long p_id) {
		logger.debug(Literal.ENTERING);

		String sql = "SELECT  PD.ID, PD.PRESENTMENTID, PD.FINID, PD.PRESENTMENTREF, PD.FINREFERENCE, PD.EMINO, "
				+ " BK.BANKCODE, BMT.BANKNAME, "
				+ " BK.BRANCHCODE, BK.BRANCHDESC, PB.PARTNERBANKID, PB.PARTNERBANKNAME, "
				+ " BK.ADDOFBRANCH, MD.ACCNUMBER, BK.IFSC, MD.MANDATEREF,  BK.MICR,  "
				+ " PD.SCHDATE, PD.PRESENTMENTAMT, PD.UTR_NUMBER  FROM PRESENTMENTDETAILS PD "
				+ " INNER JOIN PRESENTMENTHEADER PH ON PH.ID = PD.PRESENTMENTID "
				+ " INNER JOIN PARTNERBANKS PB ON PB.PARTNERBANKID = PH.PARTNERBANKID "
				+ " INNER JOIN MANDATES MD on MD.MANDATEID = PD.MANDATEID  "
				+ " INNER JOIN BANKBRANCHES BK on MD.BANKBRANCHID = BK.BANKBRANCHID  "
				+ " inner join BMTBankDetail bmt on bmt.BANKCODE = BK.BANKCODE  WHERE PD.ID = ?";

		logger.debug(Literal.SQL + sql);
		Object[] parameters = new Object[] { p_id };
		try {
			return mainNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, (rs, rowNum) -> {
				Presentment pres = new Presentment();
				pres.setTxnReference(rs.getLong("ID"));
				pres.setHeaderId(rs.getLong("PRESENTMENTID"));
				pres.setBatchId(rs.getString("PRESENTMENTREF"));
				pres.setAgreementNo(rs.getString("FINREFERENCE"));
				pres.setEmiNo(rs.getLong("EMINO"));
				pres.setBankCode(rs.getString("BANKCODE"));
				pres.setBankName(rs.getString("BANKNAME"));
				pres.setBrCode(rs.getString("BRANCHCODE"));
				pres.setPartnerBankId(rs.getLong("PARTNERBANKID"));
				pres.setPartnerBankName(rs.getString("PARTNERBANKNAME"));
				pres.setBankAddress(rs.getString("ADDOFBRANCH"));
				pres.setAccountNo(rs.getString("ACCNUMBER"));
				pres.setIFSC(rs.getString("IFSC"));
				pres.setUmrnNo(rs.getString("MANDATEREF"));
				pres.setMicrCode(rs.getString("MICR"));
				pres.setPresentationDate(rs.getDate("SCHDATE"));
				pres.setCustomerId(rs.getLong("FINID"));
				pres.setChequeAmount(rs.getBigDecimal("PRESENTMENTAMT"));
				pres.setUtrNumber(rs.getString("UTR_NUMBER"));
				return pres;
			}, parameters);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public Presentment getPresenementPDCRecord(long p_id) {
		logger.debug(Literal.ENTERING);

		String sql = "SELECT  PD.ID, PD.PRESENTMENTID, PD.FINID, PD.PRESENTMENTREF, PD.FINREFERENCE, PD.EMINO, "
				+ " BK.BANKCODE, BMT.BANKNAME, "
				+ " BK.BRANCHCODE, BK.BRANCHDESC, PB.PARTNERBANKID, PB.PARTNERBANKNAME, "
				+ " BK.ADDOFBRANCH, CQ.ACCOUNTNO, BK.IFSC, BK.MICR,  "
				+ " CQ.CHEQUESERIALNO, PD.SCHDATE, PD.PRESENTMENTAMT, PD.UTR_NUMBER  FROM PRESENTMENTDETAILS PD  "
				+ " INNER JOIN PRESENTMENTHEADER PH ON PH.ID = PD.PRESENTMENTID "
				+ " LEFT JOIN CHEQUEHEADER CH on CH.FINID = PD.FINID "
				+ " LEFT JOIN CHEQUEDETAIL CQ on CQ.HEADERID = CH.HEADERID and cq.EMIREFNO = pd.EMINO "
				+ " INNER JOIN BANKBRANCHES BK on CQ.BANKBRANCHID = BK.BANKBRANCHID  "
				+ " INNER join BMTBankDetail bmt on bmt.BANKCODE = BK.BANKCODE "
				+ " INNER JOIN PARTNERBANKS PB ON PB.PARTNERBANKID = PH.PARTNERBANKID  WHERE PD.ID = ?";

		logger.debug(Literal.SQL + sql);
		Object[] parameters = new Object[] { p_id };
		try {
			return mainNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, (rs, rowNum) -> {
				Presentment pres = new Presentment();
				pres.setTxnReference(rs.getLong("ID"));
				pres.setCustomerId(rs.getLong("FINID"));
				pres.setHeaderId(rs.getLong("PRESENTMENTID"));
				pres.setBatchId(rs.getString("PRESENTMENTREF"));
				pres.setAgreementNo(rs.getString("FINREFERENCE"));
				pres.setEmiNo(rs.getLong("EMINO"));
				pres.setBankCode(rs.getString("BANKCODE"));
				pres.setBankName(rs.getString("BANKNAME"));
				pres.setBrCode(rs.getString("BRANCHCODE"));
				pres.setPartnerBankId(rs.getLong("PARTNERBANKID"));
				pres.setPartnerBankName(rs.getString("PARTNERBANKNAME"));
				pres.setBankAddress(rs.getString("ADDOFBRANCH"));
				pres.setAccountNo(rs.getString("ACCOUNTNO"));
				pres.setIFSC(rs.getString("IFSC"));
				pres.setMicrCode(rs.getString("MICR"));
				pres.setChequeSerialNo(rs.getString("CHEQUESERIALNO"));
				pres.setPresentationDate(rs.getDate("SCHDATE"));
				pres.setChequeAmount(rs.getBigDecimal("PRESENTMENTAMT"));
				pres.setUtrNumber(rs.getString("UTR_NUMBER"));
				return pres;
			}, parameters);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public long savePresentment(List<Presentment> presList, long headerId) {

		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());

		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO PRESENTMENT_RESP_DTLS ");
		sql.append(" (HEADER_ID,PRESENTMENT_REFERENCE,FINREFERENCE,");
		sql.append("INSTALMENT_NO,AMOUNT_CLEARED,CLEARING_DATE,");
		sql.append("CLEARING_STATUS,BOUNCE_CODE,BOUNCE_REMARKS,REASON_CODE,");
		sql.append("BANK_CODE,BANK_NAME,BRANCH_CODE,");
		sql.append("PARTNER_BANK_CODE,PARTNER_BANK_NAME,BANK_ADDRESS,");
		sql.append("ACCOUNT_NUMBER,IFSC_CODE,UMRN_NO,MICR_CODE,");
		sql.append("CHEQUE_SERIAL_NO,UTR_NUMBER,FINID,FATECORRECTION)");
		sql.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

		logger.debug(Literal.SQL + sql);

		return mainNamedJdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(),
				new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int index) throws SQLException {
						Presentment pres = presList.get(index);
						int indx = 1;

						ps.setLong(indx++, headerId);
						ps.setString(indx++, pres.getBatchId());
						ps.setString(indx++, pres.getAgreementNo());

						ps.setLong(indx++, pres.getEmiNo());
						ps.setBigDecimal(indx++, pres.getChequeAmount());
						ps.setTimestamp(indx++, curTimeStamp);

						ps.setString(indx++, pres.getStatus());
						ps.setString(indx++, pres.getUtilityCode());
						ps.setString(indx++, pres.getReturnReason());
						ps.setString(indx++, pres.getUtilityCode());

						ps.setString(indx++, pres.getBankCode());
						ps.setString(indx++, pres.getBankName());
						ps.setString(indx++, pres.getBrCode());

						ps.setLong(indx++, pres.getPartnerBankId());
						ps.setString(indx++, pres.getPartnerBankName());
						ps.setString(indx++, pres.getBankAddress());

						ps.setString(indx++, pres.getAccountNo());
						ps.setString(indx++, pres.getIFSC());
						ps.setString(indx++, pres.getUmrnNo());
						ps.setString(indx++, pres.getMicrCode());

						ps.setString(indx++, pres.getChequeSerialNo());
						ps.setString(indx++, pres.getUtrNumber());
						ps.setLong(indx++, pres.getCustomerId());
						ps.setString(indx, "N");
					}

					@Override
					public int getBatchSize() {
						return presList.size();
					}
				}).length;

	}

	@Override
	public boolean isRecordInserted(String refernce, long headeID) {
		String sql = " Select count(1) from PRESENTMENT_RESP_DTLS Where PRESENTMENT_REFERENCE = ? and HEADER_ID=?";
		logger.debug(Literal.SQL + sql);
		try {
			return mainNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class, refernce, headeID) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public void updateFileStatus(long id, long status) {
		StringBuilder sql = new StringBuilder("UPDATE PRMNT_HEADER");
		sql.append(" SET STATUS = ? WHERE ID= ?");

		logger.debug(Literal.SQL + sql.toString());

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, status);
			ps.setLong(index, id);
		});

	}

	@Override
	public boolean isRecordAlreadyInserted(String record, long headerId) {
		String sql = " Select count(1) from PRMNT_DETAILS Where RECORD_DATA = ? AND HEADER_ID = ?";
		logger.debug(Literal.SQL + sql);
		try {
			return extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class, record, headerId) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public void updateFileExtractionStatus(long id, long extraction) {
		StringBuilder sql = new StringBuilder("UPDATE PRMNT_HEADER");
		sql.append(" SET EXTRACTION = ? WHERE ID= ?");

		logger.debug(Literal.SQL + sql.toString());

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, extraction);
			ps.setLong(index, id);
		});

	}

	@Override
	public long getSIPresentmentDetailsCount(PresentmentHeader presentmentHeader) {
		StringBuilder query = new StringBuilder();
		query.append(" SELECT COUNT(PD.ID)");
		query.append(" FROM PRESENTMENTDETAILS pd ");
		query.append(" inner join presentmentheader ph on ph.ID= PD.PRESENTMENTID ");
		query.append(" inner join MANDATES md on md.MANDATEID = pd.MANDATEID ");
		query.append(" inner join FINANCEMAIN fm on FM.FINID= PD.FINID  ");
		query.append(" WHERE  ph.ID= ? AND ph.MANDATETYPE = ? AND pd.PRESENTMENTAMT > ?");
		query.append(" AND pd.ExcludeReason = ? AND pd.Status != ?  ");

		logger.debug(Literal.SQL + query);

		return mainNamedJdbcTemplate.getJdbcOperations().queryForObject(query.toString(), Long.class);
	}

	@Override
	public int saveExternalPresentmentRecordsData(List<ExtPresentmentData> extPresentmentDataList) {
		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO PRMNT_DETAILS (");
		sql.append("HEADER_ID, RECORD_DATA, STATUS, CREATED_DATE)");
		sql.append("values(?,?,?,?)");

		logger.debug(Literal.SQL + sql.toString());

		return extNamedJdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				ExtPresentmentData item = extPresentmentDataList.get(index);
				ps.setLong(1, item.getHeaderId());
				ps.setString(2, item.getRecord());
				ps.setLong(3, item.getStatus());
				ps.setTimestamp(4, curTimeStamp);
			}

			@Override
			public int getBatchSize() {
				return extPresentmentDataList.size();
			}
		}).length;
	}

	@Override
	public void updateExternalPresentmentRecordStatus(long id, long status, String statusCode, String statusMessage) {
		StringBuilder sql = new StringBuilder("UPDATE PRMNT_DETAILS");
		sql.append(" SET STATUS = ?, ERROR_CODE = ?, ERROR_MESSAGE = ? WHERE ID= ? ");

		logger.debug(Literal.SQL + sql.toString());

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, status);
			ps.setString(index++, statusCode);
			ps.setString(index++, statusMessage);
			ps.setLong(index, id);
		});

	}

	public List<ExtBounceReason> fetchBounceReasons() {
		logger.debug(Literal.ENTERING);
		String queryStr;

		List<ExtBounceReason> extBounceReasonsList = new ArrayList<ExtBounceReason>();

		queryStr = "SELECT BOUNCECODE,REASON, RETURNCODE FROM BOUNCEREASONS";

		logger.debug(Literal.SQL + queryStr);

		mainNamedJdbcTemplate.getJdbcOperations().query(queryStr, rs -> {
			ExtBounceReason bounceReasons = new ExtBounceReason();
			bounceReasons.setBounceCode(rs.getString("BOUNCECODE"));
			bounceReasons.setBounceReason(rs.getString("REASON"));
			bounceReasons.setReturnCode(rs.getString("RETURNCODE"));
			extBounceReasonsList.add(bounceReasons);
		});
		logger.debug(Literal.LEAVING);
		return extBounceReasonsList;
	}

	@Override
	public boolean isAnyRecordPending(long headerId) {
		String sql = " Select count(1) from PRMNT_DETAILS Where STATUS = ? AND HEADER_ID = ?";
		logger.debug(Literal.SQL + sql);
		try {
			return extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class, UNPROCESSED,
					headerId) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	public boolean isHeaderFileProcessed(String fileName) {
		String sql = "Select count(1) from Presentment_Resp_Header Where Batch_Name= ?";

		logger.debug(Literal.SQL + sql);

		try {
			return mainNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class, fileName) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public void save(ExtPrmntRespHeader presentmentRespHeader) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO Presentment_Resp_Header");
		sql.append(" (BATCH_NAME, EVENT, TOTAL_RECORDS, PROGRESS)");// PROGRESS column is added newly for
																	// External Interfaces
		sql.append(" VALUES(?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		KeyHolder keyHolder = new GeneratedKeyHolder();

		try {
			mainNamedJdbcTemplate.getJdbcOperations().update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });
					int index = 1;

					ps.setString(index++, presentmentRespHeader.getFileName());
					ps.setString(index++, presentmentRespHeader.getEvent());
					ps.setLong(index++, presentmentRespHeader.getTotalRecords());
					ps.setInt(index, presentmentRespHeader.getProgress());

					return ps;
				}
			}, keyHolder);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		presentmentRespHeader.setHeaderId(keyHolder.getKey().longValue());

	}

	@Override
	public long getHeaderIdIfExist(String fileName) {
		String sql = " Select ID from PRESENTMENT_RESP_HEADER Where BATCH_NAME = ?";
		logger.debug(Literal.SQL + sql);

		try {
			return mainNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, Long.class, fileName);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return 0;
		}
	}

	@Override
	public void updateHeader(ExtPrmntRespHeader presentmentRespHeader) {

		StringBuilder sql = new StringBuilder();

		sql.append("UPDATE Presentment_Resp_Header SET");
		sql.append(" PROGRESS = ? , TOTAL_RECORDS=? WHERE ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		mainNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setInt(index++, presentmentRespHeader.getProgress());
			ps.setLong(index++, presentmentRespHeader.getTotalRecords());
			ps.setLong(index, presentmentRespHeader.getHeaderId());
		});
	}

	@Override
	public void updateHeaderProgress(long headerId, int progress) {

		StringBuilder sql = new StringBuilder();

		sql.append("UPDATE Presentment_Resp_Header SET");
		sql.append(" PROGRESS = ? WHERE ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		mainNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setInt(index++, progress);
			ps.setLong(index, headerId);
		});
	}

	@Override
	public Presentment getPDCStagingPresentmentDetails(long finId, String chequeNo, java.util.Date chequeDate) {

		logger.debug(Literal.ENTERING);

		StringBuilder query = new StringBuilder();

		query.append(" SELECT ");
		query.append(" PD.ID, PD.PRESENTMENTID, PD.FINID, ");
		query.append(" PD.PRESENTMENTREF, PD.FINREFERENCE, PD.EMINO,  ");
		query.append(" BK.BANKCODE, BMT.BANKNAME,BK.BRANCHCODE, ");
		query.append(" BK.BRANCHDESC, PB.PARTNERBANKID, PB.PARTNERBANKNAME,  ");
		query.append(" BK.ADDOFBRANCH, CQ.ACCOUNTNO, BK.IFSC, ");
		query.append(" BK.MICR,CQ.CHEQUESERIALNO, ");
		query.append(" PD.SCHDATE, PD.PRESENTMENTAMT, PD.UTR_NUMBER   ");
		query.append(" FROM PRESENTMENTDETAILS PD   ");
		query.append(" INNER JOIN PRESENTMENTHEADER PH ON PH.ID = PD.PRESENTMENTID  ");
		query.append(" LEFT JOIN CHEQUEHEADER CH on CH.FINID = PD.FINID  ");
		query.append(" LEFT JOIN CHEQUEDETAIL CQ on CQ.HEADERID = CH.HEADERID and cq.EMIREFNO = pd.EMINO ");
		query.append(" INNER JOIN BANKBRANCHES BK on CQ.BANKBRANCHID = BK.BANKBRANCHID ");
		query.append(" INNER JOIN BMTBankDetail BMT on BMT.BANKCODE = BK.BANKCODE ");
		query.append(" INNER JOIN PARTNERBANKS PB ON PB.PARTNERBANKID = PH.PARTNERBANKID ");
		query.append(" WHERE PD.FINID = ? AND CQ.CHEQUESERIALNO = ? AND CQ.CHEQUEDATE = ?");

		String sql = query.toString();

		logger.debug(Literal.SQL + sql);
		Object[] parameters = new Object[] { finId, chequeNo, chequeDate };
		try {
			return mainNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, (rs, rowNum) -> {
				Presentment pres = new Presentment();
				pres.setTxnReference(rs.getLong("ID"));
				pres.setCustomerId(rs.getLong("FINID"));
				pres.setHeaderId(rs.getLong("PRESENTMENTID"));
				pres.setBatchId(rs.getString("PRESENTMENTREF"));
				pres.setAgreementNo(rs.getString("FINREFERENCE"));
				pres.setEmiNo(rs.getLong("EMINO"));
				pres.setBankCode(rs.getString("BANKCODE"));
				pres.setBankName(rs.getString("BANKNAME"));
				pres.setBrCode(rs.getString("BRANCHCODE"));
				pres.setPartnerBankId(rs.getLong("PARTNERBANKID"));
				pres.setPartnerBankName(rs.getString("PARTNERBANKNAME"));
				pres.setBankAddress(rs.getString("ADDOFBRANCH"));
				pres.setAccountNo(rs.getString("ACCOUNTNO"));
				pres.setIFSC(rs.getString("IFSC"));
				pres.setMicrCode(rs.getString("MICR"));
				pres.setChequeSerialNo(rs.getString("CHEQUESERIALNO"));
				pres.setPresentationDate(rs.getDate("SCHDATE"));
				pres.setChequeAmount(rs.getBigDecimal("PRESENTMENTAMT"));
				pres.setUtrNumber(rs.getString("UTR_NUMBER"));
				return pres;
			}, parameters);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public void setExtDataSource(DataSource extDataSource) {
		this.extNamedJdbcTemplate = new NamedParameterJdbcTemplate(extDataSource);
	}

	public void setMainDataSource(DataSource mainDataSource) {
		this.mainNamedJdbcTemplate = new NamedParameterJdbcTemplate(mainDataSource);
	}

	@Override
	public void updateFileExtractionStatusWithError(long id, long extraction, String errCode, String errMessage) {
		StringBuilder sql = new StringBuilder("UPDATE PRMNT_HEADER");
		sql.append(" SET EXTRACTION = ?, ERROR_CODE = ?, ERROR_MESSAGE=? WHERE ID= ?");

		logger.debug(Literal.SQL + sql.toString());

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, extraction);
			ps.setString(index++, errCode);
			ps.setString(index++, errMessage);
			ps.setLong(index, id);
		});

	}

	@Override
	public long savePresentment(Presentment pres, long headerId, String clearingStatus) {

		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());

		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO PRESENTMENT_RESP_DTLS ");
		sql.append(" (HEADER_ID,PRESENTMENT_REFERENCE,FINREFERENCE,");
		sql.append("INSTALMENT_NO,AMOUNT_CLEARED,CLEARING_DATE,");
		sql.append("CLEARING_STATUS,BOUNCE_CODE,BOUNCE_REMARKS,REASON_CODE,");
		sql.append("BANK_CODE,BANK_NAME,BRANCH_CODE,");
		sql.append("PARTNER_BANK_CODE,PARTNER_BANK_NAME,BANK_ADDRESS,");
		sql.append("ACCOUNT_NUMBER,IFSC_CODE,UMRN_NO,MICR_CODE,");
		sql.append("CHEQUE_SERIAL_NO,UTR_NUMBER,FINID,FATECORRECTION)");
		sql.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

		logger.debug(Literal.SQL + sql);

		mainNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, headerId);
			ps.setString(index++, pres.getBatchId());
			ps.setString(index++, pres.getAgreementNo());

			ps.setLong(index++, pres.getEmiNo());
			ps.setBigDecimal(index++, pres.getChequeAmount());
			ps.setTimestamp(index++, curTimeStamp);

			ps.setString(index++, clearingStatus);
			ps.setString(index++, pres.getUtilityCode());
			ps.setString(index++, pres.getReturnReason());
			ps.setString(index++, pres.getUtilityCode());

			ps.setString(index++, pres.getBankCode());
			ps.setString(index++, pres.getBankName());
			ps.setString(index++, pres.getBrCode());

			ps.setLong(index++, pres.getPartnerBankId());
			ps.setString(index++, pres.getPartnerBankName());
			ps.setString(index++, pres.getBankAddress());

			ps.setString(index++, pres.getAccountNo());
			ps.setString(index++, pres.getIFSC());
			ps.setString(index++, pres.getUmrnNo());
			ps.setString(index++, pres.getMicrCode());

			ps.setString(index++, pres.getChequeSerialNo());
			ps.setString(index++, pres.getUtrNumber());
			ps.setLong(index++, pres.getCustomerId());
			ps.setString(index, "N");

		});

		return pres.getTxnReference();
	}

}
