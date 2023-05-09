package com.pennant.backend.dao.cersai.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.cersai.CERSAIDAO;
import com.pennant.backend.model.cersai.CersaiAddCollDetails;
import com.pennant.backend.model.cersai.CersaiAssetOwners;
import com.pennant.backend.model.cersai.CersaiBorrowers;
import com.pennant.backend.model.cersai.CersaiChargeHolder;
import com.pennant.backend.model.cersai.CersaiFileInfo;
import com.pennant.backend.model.cersai.CersaiHeader;
import com.pennant.backend.model.cersai.CersaiImmovableAsset;
import com.pennant.backend.model.cersai.CersaiIntangibleAsset;
import com.pennant.backend.model.cersai.CersaiModifyCollDetails;
import com.pennant.backend.model.cersai.CersaiMovableAsset;
import com.pennant.backend.model.cersai.CersaiSatisfyCollDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.CersaiConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;

public class CERSAIDAOImpl extends SequenceDao<Object> implements CERSAIDAO {

	private static Logger logger = LogManager.getLogger(CERSAIDAOImpl.class);

	@Override
	public List<String> getotalRecords() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" DISTINCT COLLATERALREF FROM COLLATERALSETUP");
		sql.append(" WHERE COLLATERALREF IN ");
		sql.append(" (SELECT CA.COLLATERALREF FROM COLLATERALSETUP CS");
		sql.append(" INNER JOIN COLLATERALASSIGNMENT CA ON CA.COLLATERALREF= CS.COLLATERALREF ");
		sql.append(" WHERE SIID is NULL)");

		logger.debug(Literal.SQL + sql.toString());
		List<String> collateralList = new ArrayList<String>();

		try {
			collateralList = this.jdbcTemplate.queryForList(sql.toString(), new MapSqlParameterSource(), String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			collateralList = new ArrayList<String>();
		}

		logger.debug(Literal.LEAVING);
		return collateralList;
	}

	@Override
	public void updateFileStatus(CersaiFileInfo fileInfo) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder("update Cersai_File_Info");
		sql.append(" set Status = :Status");
		sql.append(", Total_Records = :TotalRecords");
		sql.append(", Processed_Records = :ProcessedRecords");
		sql.append(", SUCCESS_RECORDS = :SuccessCount");
		sql.append(", Failed_Records = :FailedCount");
		sql.append(", Remarks = :Remarks");
		sql.append(", End_Time = :EndTime");
		sql.append(" where ID = :ID");
		String status = fileInfo.getStatus();
		if ("S".equals(status)) {
			paramMap.addValue("Status", "C");
		} else {
			paramMap.addValue("Status", "F");
		}
		paramMap.addValue("TotalRecords", fileInfo.getTotalRecords());
		paramMap.addValue("ProcessedRecords", fileInfo.getProcessedRecords());
		paramMap.addValue("SuccessCount", fileInfo.getSuccessCount());
		paramMap.addValue("FailedCount", fileInfo.getFailedCount());
		paramMap.addValue("Remarks", fileInfo.getRemarks());
		paramMap.addValue("ID", fileInfo.getId());
		paramMap.addValue("EndTime", DateUtil.getSysDate());
		try {
			this.jdbcTemplate.update(sql.toString(), paramMap);
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void logFileInfo(CersaiFileInfo fileInfo) {
		logger.debug(Literal.ENTERING);

		final KeyHolder keyHolder = new GeneratedKeyHolder();
		StringBuilder sql = new StringBuilder("insert into cersai_file_info");
		sql.append(" (FileName");
		sql.append(", CreatedOn");
		sql.append(", Status");
		sql.append(", FileLocation");
		sql.append(", Start_Time");
		sql.append(", segmentType)");
		sql.append(" Values");
		sql.append(" (:File_Name");
		sql.append(", :CreatedOn");
		sql.append(", :Status");
		sql.append(", :File_Location");
		sql.append(", :Start_Time");
		sql.append(", :segmentType)");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		paramMap.addValue("File_Name", fileInfo.getFileName());
		paramMap.addValue("CreatedOn", SysParamUtil.getAppDate());
		paramMap.addValue("Status", "I");
		paramMap.addValue("File_Location", fileInfo.getFileLocation());
		paramMap.addValue("Start_Time", DateUtil.getSysDate());
		paramMap.addValue("segmentType", fileInfo.getDownloadType());
		try {
			this.jdbcTemplate.update(sql.toString(), paramMap, keyHolder, new String[] { "id" });
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		fileInfo.setId(keyHolder.getKey().longValue());
		logger.debug(Literal.LEAVING);
	}

	@Override
	public Long saveHeader(CersaiHeader ch) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		if (ch.getBatchId() == Long.MIN_VALUE) {
			ch.setBatchId(getNextValue("SeqCersaiHeader"));
		}

		sql.append(" Insert Into CERSAI_Header");
		sql.append(" (BatchId, FileHeader, FileType, TotalRecords, FileDate ) ");
		sql.append(" Values ( ?, ?, ?, ?, ?) ");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, ch.getBatchId());
				ps.setString(index++, ch.getFileHeader());
				ps.setString(index++, ch.getFileType());
				ps.setLong(index++, ch.getTotalRecords());
				ps.setDate(index, JdbcUtil.getDate(ch.getFileDate()));

			});
		} catch (DuplicateKeyException e) {
			logger.error(Literal.EXCEPTION, e);
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return ch.getBatchId();
	}

	@Override
	public CersaiHeader getHeaderByBatchId(long batchId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" BatchId, FileHeader, FileType, TotalRecords, FileDate ");
		sql.append(" From CERSAI_Header");
		sql.append(" Where BatchId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				CersaiHeader ch = new CersaiHeader();

				ch.setBatchId(rs.getLong("BatchId"));
				ch.setFileHeader(rs.getString("FileHeader"));
				ch.setFileType(rs.getString("FileType"));
				ch.setTotalRecords(rs.getLong("TotalRecords"));
				ch.setFileDate(rs.getDate("FileDate"));

				return ch;
			}, batchId);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public List<CersaiBorrowers> getBorrowersByCollateralRef(String collateralRef) {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" C.CUSTID,C.CUSTCIF,CUSTTYPECODE,CUSTCRCPR,CUSTSHRTNAME,CUSTCTGCODE,CUSTGENDERCODE,CUSTDOB, ");
		sql.append(" CUSTMOTHERMAIDEN,C.PHONENUMBER,CE.CUSTEMAIL,CUSTADDRLINE1, ");
		sql.append(" CUSTADDRCITY,CUSTADDRPROVINCE,CUSTADDRZIP,CA.CUSTDISTRICT from customers C ");
		sql.append(" INNER JOIN CUSTOMERADDRESSES CA ON CA.CUSTID = C.CUSTID AND CUSTADDRPRIORITY =5 ");
		sql.append(" INNER JOIN CUSTOMEREMAILS CE ON CE.CUSTID = C.CUSTID AND CUSTEMAILPRIORITY = 5 ");
		sql.append(" WHERE C.CUSTID IN( ");
		sql.append(" SELECT DISTINCT CUSTID FROM FINANCEMAIN FM ");
		sql.append(" WHERE FINREFERENCE IN(SELECT REFERENCE FROM COLLATERALASSIGNMENT WHERE COLLATERALREF = ? ) ");
		sql.append(" UNION ALL ");
		sql.append(" SELECT DISTINCT C.CUSTID FROM FINJOINTACCOUNTDETAILS FJ ");
		sql.append(" INNER JOIN CUSTOMERS C ON C.CUSTCIF = FJ.CUSTCIF ");
		sql.append(" WHERE FINREFERENCE IN(SELECT REFERENCE FROM COLLATERALASSIGNMENT WHERE COLLATERALREF = ? )) ");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
				CersaiBorrowers brrDtl = new CersaiBorrowers();

				brrDtl.setCustId(rs.getLong("CUSTID"));
				brrDtl.setCustCif(rs.getString("CUSTCIF"));
				brrDtl.setBorrowerType(rs.getString("CUSTTYPECODE"));
				brrDtl.setCustCtgCode(rs.getString("CUSTCTGCODE"));
				if (StringUtils.equalsIgnoreCase(PennantConstants.PFF_CUSTCTG_INDIV, rs.getString("CUSTCTGCODE"))) {
					brrDtl.setIndividualPan(rs.getString("CUSTCRCPR"));
					brrDtl.setIndividualName(rs.getString("CUSTSHRTNAME"));
					brrDtl.setDob(rs.getDate("CUSTDOB"));
					brrDtl.setGender(rs.getString("CUSTGENDERCODE"));
					brrDtl.setFatherMotherName(rs.getString("CUSTMOTHERMAIDEN"));
					brrDtl.setMobileNo(rs.getLong("PHONENUMBER"));
					brrDtl.setEmail(rs.getString("CUSTEMAIL"));
				} else {
					brrDtl.setBorrowerPAN(rs.getString("CUSTCRCPR"));
					brrDtl.setBorrowerName(rs.getString("CUSTSHRTNAME"));
					brrDtl.setBorrowerRegDate(rs.getDate("CUSTDOB"));
				}
				brrDtl.setAddressLine1(rs.getString("CUSTADDRLINE1"));
				brrDtl.setCity(rs.getString("CUSTADDRCITY"));
				brrDtl.setState(rs.getString("CUSTADDRPROVINCE"));
				brrDtl.setPincode(rs.getLong("CUSTADDRZIP"));
				brrDtl.setDistrict(rs.getString("CUSTDISTRICT"));

				return brrDtl;
			}, collateralRef, collateralRef);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new ArrayList<CersaiBorrowers>();
	}

	@Override
	public List<CersaiAssetOwners> getAssetOwnersByCollateralRef(String collateralRef) {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" C.CUSTID,CUSTCIF,CUSTTYPECODE,CUSTCRCPR,CUSTSHRTNAME,CUSTCTGCODE,CUSTGENDERCODE ");
		sql.append(" ,CUSTDOB, CUSTMOTHERMAIDEN,C.PHONENUMBER,CE.CUSTEMAIL,CUSTADDRLINE1, ");
		sql.append(" CUSTADDRCITY,CUSTADDRPROVINCE,CUSTADDRZIP,CA.CUSTDISTRICT FROM CUSTOMERS C ");
		sql.append(" INNER JOIN CUSTOMERADDRESSES CA ON CA.CUSTID = C.CUSTID AND CUSTADDRPRIORITY =5");
		sql.append(" INNER JOIN CUSTOMEREMAILS CE ON CE.CUSTID = C.CUSTID AND CUSTEMAILPRIORITY =5");
		sql.append(" WHERE C.CUSTID IN(SELECT DISTINCT DEPOSITORID CUSTID FROM COLLATERALSETUP ");
		sql.append(" WHERE COLLATERALREF = ? ");
		sql.append(" UNION ALL ");
		sql.append(" SELECT DISTINCT CUSTOMERID CUSTID FROM COLLATERALCOOWNERS ");
		sql.append(" WHERE COLLATERALREF = ?  AND BANKCUSTOMER = ?) ");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
				CersaiAssetOwners asstOwnDtl = new CersaiAssetOwners();

				asstOwnDtl.setCustId(rs.getLong("CUSTID"));
				asstOwnDtl.setCustCif(rs.getString("CUSTCIF"));
				asstOwnDtl.setAssetOwnerType(rs.getString("CUSTTYPECODE"));
				asstOwnDtl.setCustCtgCode(rs.getString("CUSTCTGCODE"));
				if (StringUtils.equalsIgnoreCase(PennantConstants.PFF_CUSTCTG_INDIV, rs.getString("CUSTCTGCODE"))) {
					asstOwnDtl.setIndividualPan(rs.getString("CUSTCRCPR"));
					asstOwnDtl.setIndividualName(rs.getString("CUSTSHRTNAME"));
					asstOwnDtl.setDob(rs.getDate("CUSTDOB"));
					asstOwnDtl.setGender(rs.getString("CUSTGENDERCODE"));
					asstOwnDtl.setFatherMotherName(rs.getString("CUSTMOTHERMAIDEN"));
					asstOwnDtl.setMobileNo(rs.getLong("PHONENUMBER"));
					asstOwnDtl.setEmail(rs.getString("CUSTEMAIL"));
				} else {
					asstOwnDtl.setAssetOwnerPAN(rs.getString("CUSTCRCPR"));
					asstOwnDtl.setAssetOwnerName(rs.getString("CUSTSHRTNAME"));
					asstOwnDtl.setAssetOwnerRegDate(rs.getDate("CUSTDOB"));
				}
				asstOwnDtl.setAddressLine1(rs.getString("CUSTADDRLINE1"));
				asstOwnDtl.setCity(rs.getString("CUSTADDRCITY"));
				asstOwnDtl.setState(rs.getString("CUSTADDRPROVINCE"));
				asstOwnDtl.setPincode(rs.getLong("CUSTADDRZIP"));
				asstOwnDtl.setDistrict(rs.getString("CUSTDISTRICT"));

				return asstOwnDtl;
			}, collateralRef, collateralRef, BigDecimal.ONE);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new ArrayList<CersaiAssetOwners>();

	}

	@Override
	public List<CersaiAddCollDetails> getCollateralDetailsByRef(String collateralRef) {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" fm.FINASSETVALUE,fm.finreference");
		sql.append(", cs.collateraltype from collateralassignment ca ");
		sql.append(" inner join collateralsetup cs on cs.collateralref= ca.collateralref ");
		sql.append(" inner join Financemain fm on fm.finreference = ca.reference ");
		sql.append(" where cs.collateralref = ? and fm.FINISACTIVE = 1 ");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
				CersaiAddCollDetails collDtl = new CersaiAddCollDetails();

				collDtl.setTotalSecuredAmt(rs.getBigDecimal("FINASSETVALUE"));
				collDtl.setEntityMISToken(rs.getString("FINREFERENCE"));
				collDtl.setCollateralType(rs.getString("collateraltype"));

				return collDtl;
			}, collateralRef);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new ArrayList<CersaiAddCollDetails>();
	}

	@Override
	public void saveCersaiCollateralDetails(List<CersaiAddCollDetails> collDetails) {
		StringBuilder sql = new StringBuilder();

		sql.append(" INSERT Into CERSAI_Collateraldetails");
		sql.append(" (RowType, SerialNumber, NoOfBorrowers, NoOfAssetOwners");
		sql.append(", NOOFCONSORTIUMMEMBERS, SITYPEID, SITYPEOTHERS, FINANCINGTYPEID");
		sql.append(", SICREATIONDATE, TOTALSECUREDAMT,ENTITYMISTOKEN, NARRATION ");
		sql.append(", TYPEOFCHARGE , TPM, BatchRefNumber, BatchId )");
		sql.append(" VALUES( ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					CersaiAddCollDetails collDtl = collDetails.get(i);
					setInsertParameterizedFields(collDtl, ps);
				}

				@Override
				public int getBatchSize() {
					return collDetails.size();
				}
			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

	}

	private void setInsertParameterizedFields(CersaiAddCollDetails collDtl, PreparedStatement ps) throws SQLException {
		int index = 1;

		ps.setString(index++, collDtl.getRowType());
		ps.setLong(index++, collDtl.getSerialNumber());
		ps.setLong(index++, collDtl.getNoOfBrrowers());
		ps.setLong(index++, collDtl.getNoOfAssetOwners());
		ps.setLong(index++, collDtl.getNoOfConsortiumMemebers());
		ps.setLong(index++, JdbcUtil.getLong(collDtl.getSiTypeId()));
		ps.setString(index++, collDtl.getSiTypeOthers());
		ps.setString(index++, collDtl.getFinancingTypeId());
		ps.setDate(index++, JdbcUtil.getDate(collDtl.getSiCreationDate()));
		ps.setBigDecimal(index++, collDtl.getTotalSecuredAmt());
		ps.setString(index++, collDtl.getEntityMISToken());
		ps.setString(index++, collDtl.getNarration());
		ps.setString(index++, collDtl.getTypeOfCharge());
		ps.setBoolean(index++, collDtl.isTpm());
		ps.setString(index++, collDtl.getBatchRefNumber());// Fix me need to auto generate
		ps.setLong(index, collDtl.getBatchId());
	}

	@Override
	public void saveBorrowerDetails(List<CersaiBorrowers> borrowers) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT Into CERSAI_BORROWER");
		sql.append(" (RowType, SerialNumber, BORROWERTYPE, ISASSETOWNER");
		sql.append(", BORROWERUIDTYPE, BORROWERUIDVALUE, BORROWERPAN, BORROWERCKYCNUMBER");
		sql.append(", BORROWERNAME, BORROWERREGDATE, BORROWERREGNUMBER, INDIVIDUALPAN ");
		sql.append(", INDIVIDUALCKYCNUMBER , GENDER, INDIVIDUALNAME, FATHERMOTHERNAME ");
		sql.append(", DOB , MOBILENO, EMAIL, ADDRESSLINE1, ADDRESSLINE2, ADDRESSLINE3 ");
		sql.append(", CITY , DISTRICT, STATE, PINCODE, COUNTRY, Batchid )");
		sql.append(" VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					CersaiBorrowers borrower = borrowers.get(i);
					setInsertParameterizedFields(borrower, ps);
				}

				@Override
				public int getBatchSize() {
					return borrowers.size();
				}
			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

	}

	private void setInsertParameterizedFields(CersaiBorrowers collDtl, PreparedStatement ps) throws SQLException {
		int index = 1;

		ps.setString(index++, collDtl.getRowType());
		ps.setLong(index++, collDtl.getSerialNumber());
		ps.setString(index++, collDtl.getBorrowerType());
		ps.setBoolean(index++, collDtl.isAssetOwner());
		ps.setLong(index++, collDtl.getBorrowerUidType());
		ps.setString(index++, collDtl.getBorrowerUidValue());
		ps.setString(index++, collDtl.getBorrowerPAN());
		ps.setLong(index++, collDtl.getBorrowerCKYC());
		ps.setString(index++, collDtl.getBorrowerName());
		ps.setDate(index++, JdbcUtil.getDate(collDtl.getBorrowerRegDate()));
		ps.setString(index++, collDtl.getBorrowerRegNumber());
		ps.setString(index++, collDtl.getIndividualPan());
		ps.setLong(index++, collDtl.getIndividualCKYC());
		ps.setString(index++, collDtl.getGender());
		ps.setString(index++, collDtl.getIndividualName());
		ps.setString(index++, collDtl.getFatherMotherName());
		ps.setDate(index++, JdbcUtil.getDate(collDtl.getDob()));
		ps.setLong(index++, collDtl.getMobileNo());
		ps.setString(index++, collDtl.getEmail());
		ps.setString(index++, collDtl.getAddressLine1());
		ps.setString(index++, collDtl.getAddressLine2());
		ps.setString(index++, collDtl.getAddressLine3());
		ps.setString(index++, collDtl.getCity());
		ps.setString(index++, collDtl.getDistrict());
		ps.setString(index++, collDtl.getState());
		ps.setLong(index++, collDtl.getPincode());
		ps.setString(index++, collDtl.getCountry());
		ps.setLong(index, collDtl.getBatchId());
	}

	@Override
	public void saveAssetOwnerDetails(List<CersaiAssetOwners> assetOwners) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT Into CERSAI_ASSET_OWNER");
		sql.append(" (RowType, SerialNumber, ASSETOWNERTYPE, ASSETOWNERUIDTYPE");
		sql.append(", ASSETOWNERUIDVALUE, ASSETOWNERPAN, ASSETOWNERCKYCNUMBER, ASSETOWNERNAME");
		sql.append(", ASSETOWNERREGDATE, ASSETOWNERREGNUMBER, INDIVIDUALPAN ");
		sql.append(", INDIVIDUALCKYCNUMBER , GENDER, INDIVIDUALNAME, FATHERMOTHERNAME ");
		sql.append(", DOB , MOBILENUMBER, EMAIL, ADDRESSLINE1, ADDRESSLINE2, ADDRESSLINE3 ");
		sql.append(", CITY , DISTRICT, STATE, PINCODE, COUNTRY, Batchid )");
		sql.append(" VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					CersaiAssetOwners owner = assetOwners.get(i);
					setInsertParameterizedFields(owner, ps);
				}

				@Override
				public int getBatchSize() {
					return assetOwners.size();
				}
			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

	}

	private void setInsertParameterizedFields(CersaiAssetOwners collDtl, PreparedStatement ps) throws SQLException {
		int index = 1;

		ps.setString(index++, collDtl.getRowType());
		ps.setLong(index++, collDtl.getSerialNumber());
		ps.setString(index++, collDtl.getAssetOwnerType());
		ps.setLong(index++, collDtl.getAssetOwnerUidType());
		ps.setString(index++, collDtl.getAssetOwnerUidValue());
		ps.setString(index++, collDtl.getAssetOwnerPAN());
		ps.setLong(index++, collDtl.getAssetOwnerCKYC());
		ps.setString(index++, collDtl.getAssetOwnerName());
		ps.setDate(index++, JdbcUtil.getDate(collDtl.getAssetOwnerRegDate()));
		ps.setString(index++, collDtl.getAssetOwnerRegNumber());
		ps.setString(index++, collDtl.getIndividualPan());
		ps.setLong(index++, collDtl.getIndividualCKYC());
		ps.setString(index++, collDtl.getGender());
		ps.setString(index++, collDtl.getIndividualName());
		ps.setString(index++, collDtl.getFatherMotherName());
		ps.setDate(index++, JdbcUtil.getDate(collDtl.getDob()));
		ps.setLong(index++, collDtl.getMobileNo());
		ps.setString(index++, collDtl.getEmail());
		ps.setString(index++, collDtl.getAddressLine1());
		ps.setString(index++, collDtl.getAddressLine2());
		ps.setString(index++, collDtl.getAddressLine3());
		ps.setString(index++, collDtl.getCity());
		ps.setString(index++, collDtl.getDistrict());
		ps.setString(index++, collDtl.getState());
		ps.setLong(index++, collDtl.getPincode());
		ps.setString(index++, collDtl.getCountry());
		ps.setLong(index, collDtl.getBatchId());
	}

	@Override
	public List<String> getSatisfyingRecords(String downloadType) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("status", CersaiConstants.SATISFIED);

		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" CS.COLLATERALREF FROM COLLATERALSETUP CS ");
		sql.append(" INNER JOIN COLLATERALASSIGNMENT CA ON CA.COLLATERALREF = CS.COLLATERALREF");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = CA.REFERENCE");
		sql.append(" WHERE CS.REGSTATUS != :status and CS.REGISTRATIONDATE IS NOT NULL");
		sql.append(" GROUP BY CS.COLLATERALREF,CS.COLLATERALTYPE ");
		sql.append(" HAVING SUM(FINISACTIVE) = 0 ");

		logger.debug(Literal.SQL + sql.toString());
		List<String> collateralList = new ArrayList<String>();

		try {
			collateralList = this.jdbcTemplate.queryForList(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return collateralList;
	}

	@Override
	public CersaiSatisfyCollDetails getSatisfyCollDetailsByRef(String collateralRef) {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" DISTINCT CA.SIID,CA.ASSETID,COLLATERALREF,MAX(maturitydate) satisfactionDate ");
		sql.append(" FROM COLLATERALASSIGNMENT CA ");
		sql.append(" INNER JOIN FINANCEMAIN FM ON FM.FINREFERENCE = CA.REFERENCE");
		sql.append(" WHERE CA.COLLATERALREF = ? ");
		sql.append(" GROUP BY CA.COLLATERALREF,CA.SIID,CA.ASSETID ");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				CersaiSatisfyCollDetails collDtl = new CersaiSatisfyCollDetails();

				collDtl.setSiId(rs.getLong("SIID"));
				collDtl.setAssetId(rs.getLong("ASSETID"));
				collDtl.setSatisfactionDate(JdbcUtil.getDate(rs.getDate("satisfactionDate")));

				return collDtl;
			}, collateralRef);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	@Override
	public long saveSatisfyCollateral(CersaiSatisfyCollDetails collDetails) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT Into CERSAI_SatisfyColldetails");
		sql.append(" (ROWTYPE, SERIALNUMBER, SI_Id, AssetId, SatisfactionDate");
		sql.append(", ReasonCode, REASONOTHERS,BATCHREFNUMBER ");
		sql.append(", ReasonForDelay, BatchId) ");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ? ");
		sql.append(", ?, ? )");
		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, collDetails.getRowType());
				ps.setLong(index++, collDetails.getSerialNumber());
				ps.setLong(index++, collDetails.getSiId());
				ps.setLong(index++, collDetails.getAssetId());
				ps.setDate(index++, JdbcUtil.getDate(collDetails.getSatisfactionDate()));
				ps.setString(index++, collDetails.getReasonCode());
				ps.setString(index++, collDetails.getReasonOthers());
				ps.setString(index++, collDetails.getBatchRefNumber());
				ps.setString(index++, collDetails.getReasonForDelay());
				ps.setLong(index, collDetails.getBatchId());

			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

	}

	@Override
	public List<Map<String, Object>> getExtendedFieldMap(String reference, String tableName, String type) {
		type = StringUtils.trimToEmpty(type).toLowerCase();

		StringBuilder sql = new StringBuilder();

		sql.append("select * from ");
		sql.append(tableName);
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where reference = :reference order by seqno");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("reference", reference);
		try {
			return this.jdbcTemplate.queryForList(sql.toString(), source);
		} catch (DataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return new ArrayList<>();
		}
	}

	@Override
	public List<FinanceMain> getFinanceByCollateralRef(String collateralRef) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);

		StringBuilder sql = new StringBuilder("SELECT FM.FinReference, FM.FinAssetValue From Financemain FM");
		sql.append(" INNER JOIN CollateralAssignment CA On FM.FinReference = CA.Reference ");
		sql.append(" Where CollateralRef =:CollateralRef");

		logger.debug(Literal.SQL + sql.toString());
		RowMapper<FinanceMain> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceMain.class);
		List<FinanceMain> financeMainList = new ArrayList<FinanceMain>();
		try {
			financeMainList = this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
			return Collections.emptyList();
		}
		logger.debug(Literal.LEAVING);
		return financeMainList;
	}

	@Override
	public CersaiModifyCollDetails getModifyCollDetailsByRef(String collateralRef) {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" DISTINCT CA.SIID,CollateralType FROM COLLATERALASSIGNMENT CA ");
		sql.append(" INNER JOIN COLLATERALSETUP CS ON CS.COLLATERALREF = CA.COLLATERALREF ");
		sql.append(" WHERE CA.COLLATERALREF = ? and siid is NOT NULL");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				CersaiModifyCollDetails collDtl = new CersaiModifyCollDetails();

				collDtl.setSiId(rs.getLong("SIID"));
				collDtl.setCollateralType(rs.getString("CollateralType"));

				return collDtl;
			}, collateralRef);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	@Override
	public long saveModifyCollateral(CersaiModifyCollDetails colDtl) {
		StringBuilder sql = new StringBuilder();

		sql.append(" INSERT Into CERSAI_ModifyColldetails");
		sql.append(" (ROWTYPE, SERIALNUMBER, SI_Id, DOCEXECDATE");
		sql.append(", ModifyType, FINANCINGTYPEID,TYPEOFCHARGE, TOTALSECUREDAMT  ");
		sql.append(", ENTITYMISTOKEN, NARRATION, SITYPEID, ENTITYCODE, OFFICE_CODE ");
		sql.append(", OFFICE_NAME, ADDRESSLINE1, ADDRESSLINE2, ADDRESSLINE3, CITY");
		sql.append(", DISTRICT, STATE, PINCODE, COUNTRY, BATCHREFNUMBER, Batchid) ");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, colDtl.getRowType());
				ps.setLong(index++, colDtl.getSerialNumber());
				ps.setLong(index++, colDtl.getSiId());
				ps.setDate(index++, JdbcUtil.getDate(colDtl.getDocExecuDate()));
				ps.setString(index++, colDtl.getModifyType());
				ps.setString(index++, colDtl.getFinancingTypeId());
				ps.setString(index++, colDtl.getTypeOfCharge());
				ps.setBigDecimal(index++, colDtl.getTotalSecuredAmt());
				ps.setString(index++, colDtl.getEntityMISToken());
				ps.setString(index++, colDtl.getNarration());
				ps.setLong(index++, JdbcUtil.getLong(colDtl.getSiTypeId()));
				ps.setString(index++, colDtl.getEntityCode());
				ps.setString(index++, colDtl.getOfficeCode());
				ps.setString(index++, colDtl.getOfficeName());
				ps.setString(index++, colDtl.getAddressLine1());
				ps.setString(index++, colDtl.getAddressLine2());
				ps.setString(index++, colDtl.getAddressLine3());
				ps.setString(index++, colDtl.getCity());
				ps.setString(index++, colDtl.getDistrict());
				ps.setString(index++, colDtl.getState());
				ps.setLong(index++, colDtl.getPincode());
				ps.setString(index++, colDtl.getCountry());
				ps.setString(index++, colDtl.getBatchRefNumber());
				ps.setLong(index, colDtl.getBatchId());

			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	@Override
	public String generateBatchRef() {
		logger.debug(Literal.ENTERING);

		String trackingId = "Cersai";
		String acctSeq = String.valueOf(getNextValue("SeqCersaiBatchRef"));
		trackingId = StringUtils.rightPad(trackingId, 20 - acctSeq.length(), '0');
		trackingId = trackingId.concat(acctSeq);

		logger.debug(Literal.LEAVING);
		return trackingId;
	}

	@Override
	public String getAssetCategory(Long id) {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" ASSET.DESCRIPTION FROM CERSAI_ASSETCATEGORY ASSET ");
		sql.append(" INNER JOIN CERSAI_SITYPE SI ON SI.ASSETCATEGORYID = ASSET.ID ");
		sql.append(" WHERE SI.ID = ? ");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, id);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return null;
	}

	@Override
	public List<String> getModifyRecords() {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" DISTINCT COLLATERALREF FROM COLLATERALSETUP");
		sql.append(" WHERE COLLATERALREF IN ");
		sql.append(" (SELECT CA.COLLATERALREF FROM COLLATERALSETUP CS");
		sql.append(" INNER JOIN COLLATERALASSIGNMENT CA ON CA.COLLATERALREF= CS.COLLATERALREF ");
		sql.append(" WHERE SIID IS NOT NULL AND Modified = 1)");

		logger.debug(Literal.SQL + sql.toString());
		List<String> collateralList = new ArrayList<String>();

		try {
			collateralList = this.jdbcTemplate.queryForList(sql.toString(), new MapSqlParameterSource(), String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return collateralList;
	}

	@Override
	public String getRemarks(String collateralRef) {
		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" Remarks FROM COLLATERALSETUP");
		sql.append(" WHERE collateralRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), String.class, collateralRef);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public long saveMovableAsset(CersaiMovableAsset cma) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT Into CERSAI_ASSET_MOVABLE");
		sql.append(" (ROWTYPE, ASSETCATEGORYID, ASSETTYPEID, ASSETTYPEOTHERS ");
		sql.append(", ASSETSUBTYPEID, ASSETUNIQUEID,ASSETSERIALNUMBER, ASSETDESCRIPTION ");
		sql.append(", ASSETMAKE, ASSETMODEL, ADDRESSLINE1, ADDRESSLINE2, ADDRESSLINE3 ");
		sql.append(", CITY, DISTRICT, STATE, PINCODE, COUNTRY, Batchid) ");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ? ");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, cma.getRowType());
				ps.setLong(index++, cma.getAssetCategoryId());
				ps.setLong(index++, cma.getAssetTypeId());
				ps.setString(index++, cma.getAssetTypeOthers());
				ps.setLong(index++, cma.getAssetSubTypeId());
				ps.setString(index++, cma.getAssetUniqueId());
				ps.setString(index++, cma.getAssetSerialNumber());
				ps.setString(index++, cma.getAssetDescription());

				ps.setString(index++, cma.getAssetMake());
				ps.setString(index++, cma.getAssetModel());
				ps.setString(index++, cma.getAddressLine1());
				ps.setString(index++, cma.getAddressLine2());
				ps.setString(index++, cma.getAddressLine3());
				ps.setString(index++, cma.getCity());
				ps.setString(index++, cma.getDistrict());
				ps.setString(index++, cma.getState());
				ps.setLong(index++, cma.getPincode());
				ps.setString(index++, cma.getCountry());
				ps.setLong(index, cma.getBatchId());

			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	@Override
	public long saveImmovableAsset(CersaiImmovableAsset cima) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT Into CERSAI_ASSET_IMMOVABLE");
		sql.append(" (ROWTYPE, ASSETCATEGORYID, ASSETTYPEID, ASSETTYPEOTHERS ");
		sql.append(", ASSETSUBTYPEID, ASSETUNIQUEID, ASSETDESCRIPTION, SURVEYNUMBER ");
		sql.append(", PLOTNUMBER, ASSETAREA, ASSETAREAUNIT, HOUSENUMBER, FLOORNUMBER ");
		sql.append(", BUILDINGNAME, PROJECTNAME,STREETNAME, POCKET, LOCALITY ");
		sql.append(", CITY, DISTRICT, STATE, PINCODE, COUNTRY, LATITUDELONGITUDE1 ");
		sql.append(", LATITUDELONGITUDE2, LATITUDELONGITUDE3, LATITUDELONGITUDE4, Batchid) ");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ? ");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?  )");
		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, cima.getRowType());
				ps.setLong(index++, cima.getAssetCategoryId());
				ps.setLong(index++, cima.getAssetTypeId());
				ps.setString(index++, cima.getAssetTypeOthers());
				ps.setLong(index++, cima.getAssetSubTypeId());
				ps.setString(index++, cima.getAssetUniqueId());
				ps.setString(index++, cima.getAssetDescription());
				ps.setString(index++, cima.getSurveyNumber());
				ps.setString(index++, cima.getPlotNumber());
				ps.setBigDecimal(index++, cima.getAssetArea());
				ps.setString(index++, cima.getAssetAreaUnit());
				ps.setString(index++, cima.getHouseNumber());
				ps.setString(index++, cima.getFloorNumber());
				ps.setString(index++, cima.getBuildingName());
				ps.setString(index++, cima.getProjectName());
				ps.setString(index++, cima.getStreetName());
				ps.setString(index++, cima.getPocket());
				ps.setString(index++, cima.getLocality());
				ps.setString(index++, cima.getCity());
				ps.setString(index++, cima.getDistrict());
				ps.setString(index++, cima.getState());
				ps.setLong(index++, cima.getPincode());
				ps.setString(index++, cima.getCountry());
				ps.setLong(index++, cima.getBatchId());
				ps.setString(index++, cima.getLatitudeLongitude1());
				ps.setString(index++, cima.getLatitudeLongitude2());
				ps.setString(index++, cima.getLatitudeLongitude3());
				ps.setString(index, cima.getLatitudeLongitude4());

			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	@Override
	public long saveIntangibleAsset(CersaiIntangibleAsset cia) {
		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT Into CERSAI_ASSET_INTANGIBLE");
		sql.append(" (ROWTYPE, ASSETCATEGORYID, ASSETTYPEID, ASSETTYPEOTHERS ");
		sql.append(", ASSETSUBTYPEID, ASSETUNIQUEID, ASSETSERIALNUMBER, ASSETDESCRIPTION ");
		sql.append(", DIARYNUMBER, ASSETCLASS, ASSETTITLE, PATENTNUMBER, PATENTDATE ");
		sql.append(", LICENSE_NUMBER, LICENSEISSUINGAUTHORITY, LICENSECATEGORY, DESIGNNUMBER ");
		sql.append(", DESIGNCLASS, TRADEMARKAPPNUMBER, TRADEMARKAPPDATE, Batchid) ");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ? ");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, cia.getRowType());
				ps.setLong(index++, cia.getAssetCategoryId());
				ps.setLong(index++, cia.getAssetTypeId());
				ps.setString(index++, cia.getAssetTypeOthers());
				ps.setLong(index++, cia.getAssetSubTypeId());
				ps.setString(index++, cia.getAssetUniqueId());
				ps.setString(index++, cia.getAssetSerialNumber());
				ps.setString(index++, cia.getAssetDescription());
				ps.setString(index++, cia.getDairyNumber());
				ps.setString(index++, cia.getAssetClass());
				ps.setString(index++, cia.getAssetTitle());
				ps.setString(index++, cia.getPatentNumber());
				ps.setDate(index++, JdbcUtil.getDate(cia.getPatentDate()));
				ps.setString(index++, cia.getLicenseNumber());
				ps.setString(index++, cia.getLicenseIssuingAuthority());
				ps.setString(index++, cia.getLicenseCategory());
				ps.setString(index++, cia.getDesignNumber());
				ps.setString(index++, cia.getDesignClass());
				ps.setString(index++, cia.getTradeMarkAppNumber());
				ps.setDate(index++, JdbcUtil.getDate(cia.getTradeMarkAppDate()));
				ps.setLong(index, cia.getBatchId());

			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	@Override
	public void logFileInfoException(long id, String collateralRef, String reason) {
		logger.trace(Literal.ENTERING);

		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Insert Into CERSAI_FILE_INFO_LOG");
		sql.append(" (ID");
		sql.append(", CollateralRef");
		sql.append(", REASON");
		sql.append(", STATUS)");
		sql.append(" Values(:ID");
		sql.append(", :CollateralRef");
		sql.append(", :REASON");
		sql.append(", :STATUS)");

		paramMap.addValue("ID", id);
		paramMap.addValue("CollateralRef", collateralRef);
		paramMap.addValue("REASON", reason);
		paramMap.addValue("STATUS", "F");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcTemplate.update(sql.toString(), paramMap);
		} catch (DuplicateKeyException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.trace(Literal.LEAVING);

	}

	@Override
	public String generateFileSeq() {
		logger.debug(Literal.ENTERING);

		String fileSeq = String.valueOf(getNextValue("SeqCersaiFile"));
		String id = StringUtils.leftPad("", 6 - fileSeq.length(), '0');
		fileSeq = id.concat(fileSeq);

		logger.debug(Literal.LEAVING);

		return fileSeq;
	}

	@Override
	public CersaiChargeHolder getChargeHolderDetails() {

		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" OFFICECODE, OFFICENAME, ADDRESSLINE1, ADDRESSLINE2, ADDRESSLINE3");
		sql.append(", CITY, DISTRICT, STATE, PINCODE, Country, FilePath");
		sql.append(" FROM CERSAI_charge_holderDetails ");

		logger.debug(Literal.SQL + sql.toString());

		RowMapper<CersaiChargeHolder> typeRowMapper = BeanPropertyRowMapper.newInstance(CersaiChargeHolder.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), new MapSqlParameterSource(), typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}

	}

}
