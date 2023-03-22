package com.pennanttech.extrenal.ucic.dao.impl;

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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennanttech.external.constants.InterfaceConstants;
import com.pennanttech.extrenal.ucic.dao.ExtUcicDao;
import com.pennanttech.extrenal.ucic.model.ExtCustAddress;
import com.pennanttech.extrenal.ucic.model.ExtCustDoc;
import com.pennanttech.extrenal.ucic.model.ExtCustEmail;
import com.pennanttech.extrenal.ucic.model.ExtCustPhones;
import com.pennanttech.extrenal.ucic.model.ExtUcicCust;
import com.pennanttech.extrenal.ucic.model.ExtUcicData;
import com.pennanttech.extrenal.ucic.model.ExtUcicFinDetails;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class ExtUcicDaoImpl extends SequenceDao<ExtUcicCust> implements ExtUcicDao {
	private static final Logger logger = LogManager.getLogger(ExtUcicDaoImpl.class);
	private NamedParameterJdbcTemplate extNamedJdbcTemplate;

	public ExtUcicDaoImpl() {
		super();
	}

	@Override
	public long getSeqNumber(String tableName) {
		setDataSource(extNamedJdbcTemplate.getJdbcTemplate().getDataSource());
		return getNextValue(tableName);
	}

	@Override
	public void saveHistory(ExtUcicCust customer, java.util.Date latestMntDate, java.util.Date systemDate) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO UCIC_CUSTOMERS_HISTORY (");
		sql.append(" CUSTID,CUSTCIF,CUSTCOREBANK,SOURCE_SYSTEM,CUSTCTGCODE, ");
		sql.append(" CUSTOMER_TYPE,SUBCATEGORY,CUSTSHRTNAME,CUSTDOB, ");
		sql.append(" ACCNUMBER,CUSTMOTHERMAIDEN,CREATION_DATE,");
		sql.append(" CLOSINGSTATUS,CLOSEDDATE, ");
		sql.append(" INSERT_UPDATE_FLAG,COMPANYNAME,FINREFERENCE, ");
		sql.append(" EMAIL1,EMAIL2,EMAIL3, ");
		sql.append(" MOBILE1,MOBILE2,MOBILE3,LANDLINE1,LANDLINE2,LANDLINE3, ");
		sql.append(" ADDR1TYPE,ADDR1LINE1,ADDR1LINE2,ADDR1LINE3,ADDR1LINE4,ADDR1CITY, ADDR1STATE,ADDR1PIN, ");
		sql.append(" ADDR2TYPE,ADDR2LINE1,ADDR2LINE2,ADDR2LINE3,ADDR2LINE4,ADDR2CITY, ADDR2STATE,ADDR2PIN, ");
		sql.append(" ADDR3TYPE,ADDR3LINE1,ADDR3LINE2,ADDR3LINE3,ADDR3LINE4,ADDR3CITY, ADDR3STATE,ADDR3PIN, ");
		sql.append(" PAN, AADHAAR,VOTER_ID, PASSPORT,DRIVING_LICENCE, ");
		sql.append(" LASTMNTON1,LASTMNTON2,PROCESS_DATE )");
		sql.append(" VALUES(?, ?, ?, ?, ?, ?, ?, ?,");
		sql.append("  ?, ?, ?, ?, ?, ?, ?, ? , ?, ?,");
		sql.append("  ?, ?, ?, ?, ?, ?, ?, ? , ?, ?,");
		sql.append("  ?, ?, ?, ?, ?, ?, ?, ? , ?, ?,");
		sql.append("  ?, ?, ?, ?, ?, ?, ?, ? , ?, ?,");
		sql.append("  ?, ?, ?, ?, ?, ?, ?, ? , ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, customer.getCustId());
				ps.setString(index++, customer.getCustCif());
				ps.setString(index++, customer.getCustCoreBank());
				ps.setString(index++, customer.getSourceSystem());
				ps.setString(index++, customer.getCustCtgCode());
				ps.setString(index++, customer.getCustomertype());
				ps.setString(index++, customer.getSubCategory());
				ps.setString(index++, customer.getCustShrtName());
				ps.setDate(index++, (Date) customer.getCustDob());
				ps.setString(index++, customer.getAccNumber());
				ps.setString(index++, customer.getCustMotherMaiden());
				ps.setDate(index++, (Date) customer.getCreationDate());
				ps.setString(index++, customer.getClosingStatus());
				ps.setDate(index++, (Date) customer.getCloseDate());
				ps.setString(index++, customer.getInsertUpdateFlag());
				ps.setString(index++, customer.getCompanyName());
				ps.setString(index++, customer.getFinreference());

				ps.setString(index++, customer.getEmail1());
				ps.setString(index++, customer.getEmail2());
				ps.setString(index++, customer.getEmail3());

				ps.setString(index++, customer.getMobile1());
				ps.setString(index++, customer.getMobile2());
				ps.setString(index++, customer.getMobile3());
				ps.setString(index++, customer.getLandLine1());
				ps.setString(index++, customer.getLandLine2());
				ps.setString(index++, customer.getLandLine3());

				ps.setString(index++, customer.getAddr1Type());
				ps.setString(index++, customer.getAddr1Line1());
				ps.setString(index++, customer.getAddr1Line2());
				ps.setString(index++, customer.getAddr1Line3());
				ps.setString(index++, customer.getAddr1Line4());
				ps.setString(index++, customer.getAddr1City());
				ps.setString(index++, customer.getAddr1State());
				ps.setString(index++, customer.getAddr1Pin());

				ps.setString(index++, customer.getAddr2Type());
				ps.setString(index++, customer.getAddr2Line1());
				ps.setString(index++, customer.getAddr2Line2());
				ps.setString(index++, customer.getAddr2Line3());
				ps.setString(index++, customer.getAddr2Line4());
				ps.setString(index++, customer.getAddr2City());
				ps.setString(index++, customer.getAddr2State());
				ps.setString(index++, customer.getAddr2Pin());

				ps.setString(index++, customer.getAddr3Type());
				ps.setString(index++, customer.getAddr3Line1());
				ps.setString(index++, customer.getAddr3Line2());
				ps.setString(index++, customer.getAddr3Line3());
				ps.setString(index++, customer.getAddr3Line4());
				ps.setString(index++, customer.getAddr3City());
				ps.setString(index++, customer.getAddr3State());
				ps.setString(index++, customer.getAddr3Pin());

				ps.setString(index++, customer.getPan());
				ps.setString(index++, customer.getAadhaar());
				ps.setString(index++, customer.getVoterId());
				ps.setString(index++, customer.getPassport());
				ps.setString(index++, customer.getDrivingLicence());
				ps.setDate(index++, (Date) customer.getLastMntOn());
				ps.setDate(index++, (Date) latestMntDate);
				ps.setDate(index, (Date) systemDate);
			});
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public ExtUcicCust fetchRecord(long finId) {
		logger.debug(Literal.ENTERING);
		String sql = "SELECT CUSTID,CUSTCIF,FINREFERENCE,CUSTCOREBANK,SOURCE_SYSTEM,CUSTCTGCODE,CUSTOMER_TYPE,SUBCATEGORY,"
				+ " CUSTSHRTNAME,CUSTDOB,CLOSEDDATE,CLOSINGSTATUS, CUSTMOTHERMAIDEN,LASTMNTON,ACCNUMBER,"
				+ " COMPANYNAME,EMAIL1,EMAIL2,EMAIL3,MOBILE1,MOBILE2,LANDLINE1,LANDLINE2,LANDLINE3,MOBILE3,PAN,AADHAAR,VOTER_ID,DRIVING_LICENCE,PASSPORT,"
				+ " ADDR1TYPE ,ADDR1LINE1,ADDR1LINE2,ADDR1LINE3,ADDR1LINE4,ADDR1CITY,ADDR1STATE,ADDR1PIN,"
				+ " ADDR2TYPE ,ADDR2LINE1,ADDR2LINE2,ADDR2LINE3,ADDR2LINE4,ADDR2CITY,ADDR2STATE,ADDR2PIN,"
				+ " ADDR3TYPE ,ADDR3LINE1,ADDR3LINE2,ADDR3LINE3,ADDR3LINE4,ADDR3CITY,ADDR3STATE,ADDR3PIN,"
				+ " INSERT_UPDATE_FLAG FROM UCIC_CUSTOMERS WHERE FINID = ?";

		logger.debug(Literal.SQL + sql);
		Object[] parameters = new Object[] { finId };
		try {
			return extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, (rs, rowNum) -> {
				ExtUcicCust customer = new ExtUcicCust();
				customer.setCustId(rs.getLong("CUSTID"));
				customer.setCustCif(rs.getString("CUSTCIF"));
				customer.setInsertUpdateFlag(rs.getString("INSERT_UPDATE_FLAG"));
				customer.setFinreference(rs.getString("FINREFERENCE"));
				customer.setCustCoreBank(rs.getString("CUSTCOREBANK"));
				customer.setSourceSystem(rs.getString("SOURCE_SYSTEM"));
				customer.setCustCtgCode(StringUtils.stripToEmpty(rs.getString("CUSTCTGCODE")));
				customer.setCustomertype(StringUtils.stripToEmpty(rs.getString("CUSTOMER_TYPE")));
				customer.setSubCategory(StringUtils.stripToEmpty(rs.getString("SUBCATEGORY")));
				customer.setCustShrtName(StringUtils.stripToEmpty(rs.getString("CUSTSHRTNAME")));
				customer.setCustDob(rs.getDate("CUSTDOB"));
				customer.setCloseDate(rs.getDate("CLOSEDDATE"));
				customer.setClosingStatus(StringUtils.stripToEmpty(rs.getString("CLOSINGSTATUS")));
				customer.setCustMotherMaiden(StringUtils.stripToEmpty(rs.getString("CUSTMOTHERMAIDEN")));
				customer.setLastMntOn(rs.getDate("LASTMNTON"));
				customer.setAccNumber(StringUtils.stripToEmpty(rs.getString("ACCNUMBER")));
				customer.setCompanyName(StringUtils.stripToEmpty(rs.getString("COMPANYNAME")));

				customer.setEmail1(StringUtils.stripToEmpty(rs.getString("Email1")));
				customer.setEmail2(StringUtils.stripToEmpty(rs.getString("Email2")));
				customer.setEmail3(StringUtils.stripToEmpty(rs.getString("Email3")));

				customer.setEmail1(StringUtils.stripToEmpty(rs.getString("Mobile1")));
				customer.setEmail2(StringUtils.stripToEmpty(rs.getString("Mobile2")));
				customer.setEmail3(StringUtils.stripToEmpty(rs.getString("Mobile3")));
				customer.setEmail1(StringUtils.stripToEmpty(rs.getString("LandLine1")));
				customer.setEmail2(StringUtils.stripToEmpty(rs.getString("LandLine2")));
				customer.setEmail3(StringUtils.stripToEmpty(rs.getString("LandLine3")));

				customer.setPan(StringUtils.stripToEmpty(rs.getString("PAN")));
				customer.setAadhaar(StringUtils.stripToEmpty(rs.getString("AADHAAR")));
				customer.setVoterId(StringUtils.stripToEmpty(rs.getString("VOTER_ID")));
				customer.setDrivingLicence(StringUtils.stripToEmpty(rs.getString("DRIVING_LICENCE")));
				customer.setPassport(StringUtils.stripToEmpty(rs.getString("PASSPORT")));

				customer.setAddr1Type(StringUtils.stripToEmpty(rs.getString("ADDR1TYPE")));
				customer.setAddr1Line1(StringUtils.stripToEmpty(rs.getString("ADDR1LINE1")));
				customer.setAddr1Line2(StringUtils.stripToEmpty(rs.getString("ADDR1LINE2")));
				customer.setAddr1Line3(StringUtils.stripToEmpty(rs.getString("ADDR1LINE3")));
				customer.setAddr1Line4(StringUtils.stripToEmpty(rs.getString("ADDR1LINE4")));
				customer.setAddr1City(StringUtils.stripToEmpty(rs.getString("ADDR1CITY")));
				customer.setAddr1State(StringUtils.stripToEmpty(rs.getString("ADDR1STATE")));
				customer.setAddr1Pin(StringUtils.stripToEmpty(rs.getString("ADDR1PIN")));

				customer.setAddr2Type(StringUtils.stripToEmpty(rs.getString("ADDR2TYPE")));
				customer.setAddr2Line1(StringUtils.stripToEmpty(rs.getString("ADDR2LINE1")));
				customer.setAddr2Line2(StringUtils.stripToEmpty(rs.getString("ADDR2LINE2")));
				customer.setAddr2Line3(StringUtils.stripToEmpty(rs.getString("ADDR2LINE3")));
				customer.setAddr2Line4(StringUtils.stripToEmpty(rs.getString("ADDR2LINE4")));
				customer.setAddr2City(StringUtils.stripToEmpty(rs.getString("ADDR2CITY")));
				customer.setAddr2State(StringUtils.stripToEmpty(rs.getString("ADDR2STATE")));
				customer.setAddr2Pin(StringUtils.stripToEmpty(rs.getString("ADDR2PIN")));

				customer.setAddr3Type(StringUtils.stripToEmpty(rs.getString("ADDR3TYPE")));
				customer.setAddr3Line1(StringUtils.stripToEmpty(rs.getString("ADDR3LINE1")));
				customer.setAddr3Line2(StringUtils.stripToEmpty(rs.getString("ADDR3LINE2")));
				customer.setAddr3Line3(StringUtils.stripToEmpty(rs.getString("ADDR3LINE3")));
				customer.setAddr3Line4(StringUtils.stripToEmpty(rs.getString("ADDR3LINE4")));
				customer.setAddr3City(StringUtils.stripToEmpty(rs.getString("ADDR3CITY")));
				customer.setAddr3State(StringUtils.stripToEmpty(rs.getString("ADDR3STATE")));
				customer.setAddr3Pin(StringUtils.stripToEmpty(rs.getString("ADDR3PIN")));

				return customer;
			}, parameters);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void insertRecord(ExtUcicCust customer) {
		logger.debug(Literal.ENTERING);

		String sql = "INSERT INTO UCIC_CUSTOMERS "
				+ " ( CUSTID,FINID,CUSTCIF,FINREFERENCE,CUSTCOREBANK,SOURCE_SYSTEM,CUSTCTGCODE,CUSTOMER_TYPE,SUBCATEGORY,"
				+ "	 CUSTSHRTNAME,CUSTDOB,CLOSEDDATE,CLOSINGSTATUS, CUSTMOTHERMAIDEN,LASTMNTON,ACCNUMBER,"
				+ "	 COMPANYNAME,EMAIL1,EMAIL2,EMAIL3,MOBILE1,MOBILE2,MOBILE3,LANDLINE1,LANDLINE2,LANDLINE3,"
				+ "  PAN,AADHAAR,VOTER_ID,PASSPORT,DRIVING_LICENCE,"
				+ "	 ADDR1TYPE ,ADDR1LINE1,ADDR1LINE2,ADDR1LINE3,ADDR1LINE4,ADDR1CITY,ADDR1STATE,ADDR1PIN,"
				+ "	 ADDR2TYPE ,ADDR2LINE1,ADDR2LINE2,ADDR2LINE3,ADDR2LINE4,ADDR2CITY,ADDR2STATE,ADDR2PIN,"
				+ "	 ADDR3TYPE ,ADDR3LINE1,ADDR3LINE2,ADDR3LINE3,ADDR3LINE4,ADDR3CITY,ADDR3STATE,ADDR3PIN,"
				+ "	 INSERT_UPDATE_FLAG,CREATION_DATE,FILE_STATUS,PROCESS_FLAG )  VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
				+ "  ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		logger.debug(Literal.SQL + sql);

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, customer.getCustId());
			ps.setLong(index++, customer.getFinId());
			ps.setString(index++, customer.getCustCif());
			ps.setString(index++, customer.getFinreference());
			ps.setString(index++, customer.getCustCoreBank());
			ps.setString(index++, customer.getSourceSystem());
			ps.setString(index++, customer.getCustCtgCode());
			ps.setString(index++, customer.getCustomertype());
			ps.setString(index++, customer.getCompanyName());
			ps.setString(index++, customer.getCustShrtName());
			ps.setDate(index++, (Date) customer.getCustDob());
			ps.setDate(index++, (Date) customer.getCloseDate());
			ps.setString(index++, "");// customer.getCustBlockDesc() // TODO DISCUSS
			ps.setString(index++, customer.getCustMotherMaiden());
			ps.setDate(index++, (Date) customer.getLastMntOn());
			ps.setString(index++, customer.getAccNumber());
			ps.setString(index++, customer.getCompanyName());

			ps.setString(index++, customer.getEmail1());
			ps.setString(index++, customer.getEmail2());
			ps.setString(index++, customer.getEmail3());

			ps.setString(index++, customer.getMobile1());
			ps.setString(index++, customer.getMobile2());
			ps.setString(index++, customer.getMobile3());
			ps.setString(index++, customer.getLandLine1());
			ps.setString(index++, customer.getLandLine2());
			ps.setString(index++, customer.getLandLine3());

			ps.setString(index++, customer.getPan());
			ps.setString(index++, customer.getAadhaar());
			ps.setString(index++, customer.getVoterId());
			ps.setString(index++, customer.getPassport());
			ps.setString(index++, customer.getDrivingLicence());

			ps.setString(index++, customer.getAddr1Type());
			ps.setString(index++, customer.getAddr1Line1());
			ps.setString(index++, customer.getAddr1Line2());
			ps.setString(index++, customer.getAddr1Line3());
			ps.setString(index++, customer.getAddr1Line4());
			ps.setString(index++, customer.getAddr1City());
			ps.setString(index++, customer.getAddr1State());
			ps.setString(index++, customer.getAddr1Pin());

			ps.setString(index++, customer.getAddr2Type());
			ps.setString(index++, customer.getAddr2Line1());
			ps.setString(index++, customer.getAddr2Line2());
			ps.setString(index++, customer.getAddr2Line3());
			ps.setString(index++, customer.getAddr2Line4());
			ps.setString(index++, customer.getAddr2City());
			ps.setString(index++, customer.getAddr2State());
			ps.setString(index++, customer.getAddr2Pin());

			ps.setString(index++, customer.getAddr3Type());
			ps.setString(index++, customer.getAddr3Line1());
			ps.setString(index++, customer.getAddr3Line2());
			ps.setString(index++, customer.getAddr3Line3());
			ps.setString(index++, customer.getAddr3Line4());
			ps.setString(index++, customer.getAddr3City());
			ps.setString(index++, customer.getAddr3State());
			ps.setString(index++, customer.getAddr3Pin());

			ps.setString(index++, customer.getInsertUpdateFlag());
			ps.setDate(index++, (Date) customer.getCreationDate());
			ps.setLong(index++, customer.getFileStatus());
			ps.setLong(index, customer.getProgressFlag());

		});
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<ExtUcicCust> fetchCustomersForCoreBankId(int fileStatus, int process_flag) {
		logger.debug(Literal.ENTERING);

		String sql = "SELECT CUSTID,CUSTCIF,FINREFERENCE,CUSTCOREBANK,SOURCE_SYSTEM,CUSTCTGCODE,CUSTOMER_TYPE,SUBCATEGORY,"
				+ " CUSTSHRTNAME,CUSTDOB,CLOSEDDATE,CLOSINGSTATUS, CUSTMOTHERMAIDEN,LASTMNTON,ACCNUMBER,"
				+ " COMPANYNAME,EMAIL1,EMAIL2,EMAIL3,MOBILE1,MOBILE2,LANDLINE1,LANDLINE2,LANDLINE3,MOBILE3,PAN,AADHAAR,VOTER_ID,DRIVING_LICENCE,PASSPORT,"
				+ " ADDR1TYPE ,ADDR1LINE1,ADDR1LINE2,ADDR1LINE3,ADDR1LINE4,ADDR1CITY,ADDR1STATE,ADDR1PIN,"
				+ " ADDR2TYPE ,ADDR2LINE1,ADDR2LINE2,ADDR2LINE3,ADDR2LINE4,ADDR2CITY,ADDR2STATE,ADDR2PIN,"
				+ " ADDR3TYPE ,ADDR3LINE1,ADDR3LINE2,ADDR3LINE3,ADDR3LINE4,ADDR3CITY,ADDR3STATE,ADDR3PIN,"
				+ " INSERT_UPDATE_FLAG FROM UCIC_CUSTOMERS WHERE FILE_STATUS = ? AND PROCESS_FLAG = ?";

		logger.debug(Literal.SQL + sql);

		List<ExtUcicCust> customersList = new ArrayList<ExtUcicCust>();
		extNamedJdbcTemplate.getJdbcOperations().query(sql, ps -> {
			int i = 1;
			ps.setLong(i++, fileStatus);
			ps.setLong(i, process_flag);
		}, rs -> {
			ExtUcicCust customer = new ExtUcicCust();
			customer.setCustId(rs.getLong("CUSTID"));
			customer.setCustCif(rs.getString("CUSTCIF"));
			customer.setInsertUpdateFlag(rs.getString("INSERT_UPDATE_FLAG"));
			customer.setFinreference(rs.getString("FINREFERENCE"));
			customer.setCustCoreBank(rs.getString("CUSTCOREBANK"));
			customer.setSourceSystem(rs.getString("SOURCE_SYSTEM"));
			customer.setCustCtgCode(StringUtils.stripToEmpty(rs.getString("CUSTCTGCODE")));
			customer.setCustomertype(StringUtils.stripToEmpty(rs.getString("CUSTOMER_TYPE")));
			customer.setSubCategory(StringUtils.stripToEmpty(rs.getString("SUBCATEGORY")));
			customer.setCustShrtName(StringUtils.stripToEmpty(rs.getString("CUSTSHRTNAME")));
			customer.setCustDob(rs.getDate("CUSTDOB"));
			customer.setCloseDate(rs.getDate("CLOSEDDATE"));
			customer.setClosingStatus(StringUtils.stripToEmpty(rs.getString("CLOSINGSTATUS")));
			customer.setCustMotherMaiden(StringUtils.stripToEmpty(rs.getString("CUSTMOTHERMAIDEN")));
			customer.setLastMntOn(rs.getDate("LASTMNTON"));
			customer.setAccNumber(StringUtils.stripToEmpty(rs.getString("ACCNUMBER")));
			customer.setCompanyName(StringUtils.stripToEmpty(rs.getString("COMPANYNAME")));

			customer.setEmail1(StringUtils.stripToEmpty(rs.getString("EMAIL1")));
			customer.setEmail2(StringUtils.stripToEmpty(rs.getString("EMAIL2")));
			customer.setEmail3(StringUtils.stripToEmpty(rs.getString("EMAIL3")));

			customer.setMobile1(StringUtils.stripToEmpty(rs.getString("MOBILE1")));
			customer.setMobile2(StringUtils.stripToEmpty(rs.getString("MOBILE2")));
			customer.setMobile3(StringUtils.stripToEmpty(rs.getString("MOBILE3")));

			customer.setLandLine1(StringUtils.stripToEmpty(rs.getString("LANDLINE1")));
			customer.setLandLine2(StringUtils.stripToEmpty(rs.getString("LANDLINE2")));
			customer.setLandLine3(StringUtils.stripToEmpty(rs.getString("LANDLINE3")));

			customer.setPan(StringUtils.stripToEmpty(rs.getString("PAN")));
			customer.setAadhaar(StringUtils.stripToEmpty(rs.getString("AADHAAR")));
			customer.setVoterId(StringUtils.stripToEmpty(rs.getString("VOTER_ID")));
			customer.setDrivingLicence(StringUtils.stripToEmpty(rs.getString("DRIVING_LICENCE")));
			customer.setPassport(StringUtils.stripToEmpty(rs.getString("PASSPORT")));

			customer.setAddr1Type(StringUtils.stripToEmpty(rs.getString("ADDR1TYPE")));
			customer.setAddr1Line1(StringUtils.stripToEmpty(rs.getString("ADDR1LINE1")));
			customer.setAddr1Line2(StringUtils.stripToEmpty(rs.getString("ADDR1LINE2")));
			customer.setAddr1Line3(StringUtils.stripToEmpty(rs.getString("ADDR1LINE3")));
			customer.setAddr1Line4(StringUtils.stripToEmpty(rs.getString("ADDR1LINE4")));
			customer.setAddr1City(StringUtils.stripToEmpty(rs.getString("ADDR1CITY")));
			customer.setAddr1State(StringUtils.stripToEmpty(rs.getString("ADDR1STATE")));
			customer.setAddr1Pin(StringUtils.stripToEmpty(rs.getString("ADDR1PIN")));

			customer.setAddr2Type(StringUtils.stripToEmpty(rs.getString("ADDR2TYPE")));
			customer.setAddr2Line1(StringUtils.stripToEmpty(rs.getString("ADDR2LINE1")));
			customer.setAddr2Line2(StringUtils.stripToEmpty(rs.getString("ADDR2LINE2")));
			customer.setAddr2Line3(StringUtils.stripToEmpty(rs.getString("ADDR2LINE3")));
			customer.setAddr2Line4(StringUtils.stripToEmpty(rs.getString("ADDR2LINE4")));
			customer.setAddr2City(StringUtils.stripToEmpty(rs.getString("ADDR2CITY")));
			customer.setAddr2State(StringUtils.stripToEmpty(rs.getString("ADDR2STATE")));
			customer.setAddr2Pin(StringUtils.stripToEmpty(rs.getString("ADDR2PIN")));

			customer.setAddr3Type(StringUtils.stripToEmpty(rs.getString("ADDR3TYPE")));
			customer.setAddr3Line1(StringUtils.stripToEmpty(rs.getString("ADDR3LINE1")));
			customer.setAddr3Line2(StringUtils.stripToEmpty(rs.getString("ADDR3LINE2")));
			customer.setAddr3Line3(StringUtils.stripToEmpty(rs.getString("ADDR3LINE3")));
			customer.setAddr3Line4(StringUtils.stripToEmpty(rs.getString("ADDR3LINE4")));
			customer.setAddr3City(StringUtils.stripToEmpty(rs.getString("ADDR3CITY")));
			customer.setAddr3State(StringUtils.stripToEmpty(rs.getString("ADDR3STATE")));
			customer.setAddr3Pin(StringUtils.stripToEmpty(rs.getString("ADDR3PIN")));
			customersList.add(customer);
		});

		logger.debug(Literal.LEAVING);
		return customersList;
	}

	@Override
	public void updateRecordProcessingFlagAndFileStatus(ExtUcicCust customer, int process_flag, int file_status) {
		logger.debug(Literal.ENTERING);
		String queryStr = "UPDATE UCIC_CUSTOMERS SET PROCESS_FLAG = ?,FILE_STATUS = ? WHERE CUSTID = ? AND FINREFERENCE = ?";
		logger.debug(Literal.SQL + queryStr);
		extNamedJdbcTemplate.getJdbcOperations().update(queryStr.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, process_flag);
			ps.setLong(index++, file_status);
			ps.setLong(index++, customer.getCustId());
			ps.setString(index, customer.getFinreference());
		});
		logger.debug(Literal.LEAVING);

	}

	@Override
	public boolean isFileProcessed(String fileName) {
		String sql = "Select count(1) from UCIC_RESP_FILES Where FILE_NAME= ?";
		logger.debug(Literal.SQL + sql);
		try {
			return extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class, fileName) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public void saveResponseFile(String fileName, String fileLocation, int fileStatus, int extractionStatus,
			String errorCode, String errorMessage) {
		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());
		StringBuilder sql = new StringBuilder("INSERT INTO UCIC_RESP_FILES");
		sql.append(" (FILE_NAME,FILE_LOCATION,STATUS,EXTRACTION, CREATED_DATE,ERROR_CODE,ERROR_MESSAGE)");
		sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, fileName);
			ps.setString(index++, fileLocation);
			ps.setLong(index++, fileStatus);
			ps.setLong(index++, extractionStatus);
			ps.setTimestamp(index++, curTimeStamp);
			ps.setString(index++, errorCode);
			ps.setString(index, errorMessage);
		});

	}

	@Override
	public void updateResponseFileProcessingFlag(long id, int status) {
		String sql = "UPDATE UCIC_RESP_FILES SET STATUS = ? Where ID= ? ";
		logger.debug(Literal.SQL + sql);
		extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setInt(index++, status);
			ps.setLong(index, id);

		});
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateResponseFileExtractionFlag(long id, int status, int extraction) {
		String sql = "UPDATE UCIC_RESP_FILES SET STATUS = ?, EXTRACTION = ? Where ID= ? ";
		logger.debug(Literal.SQL + sql);
		extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setInt(index++, status);
			ps.setInt(index++, extraction);
			ps.setLong(index, id);

		});
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateFileRecordProcessingFlag(long header_id, String custid, int status) {
		String sql = "UPDATE UCIC_RESP_FILE_DATA SET STATUS = ? Where HEADER_ID= ? AND CUSTID = ?";
		logger.debug(Literal.SQL + sql);
		extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setInt(index++, status);
			ps.setLong(index++, header_id);
			ps.setString(index, custid);

		});
		logger.debug(Literal.LEAVING);
	}

	@Override
	public int saveResponseFileRecordsData(List<ExtUcicData> extUcicDatas) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO UCIC_RESP_FILE_DATA (");
		sql.append("HEADER_ID, UCIC_ID, CUSTID,PROCESS_FLAG, ACK_SENT,STATUS, PROCESS_DESC)");
		sql.append("values(?,?,?,?,?,?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		return extNamedJdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				ExtUcicData item = extUcicDatas.get(index);
				ps.setLong(1, item.getId());
				ps.setString(2, item.getUcicId());
				ps.setString(3, item.getCustId());
				ps.setLong(4, 0);
				ps.setLong(5, 0);
				ps.setLong(6, 0);
				ps.setString(7, "");
			}

			@Override
			public int getBatchSize() {
				return extUcicDatas.size();
			}
		}).length;
	}

	@Override
	public void updateUcicIdInMain(String custId, String ucicId) {
		logger.debug(Literal.ENTERING);
		String sql = "UPDATE UCIC_CUSTOMERS SET CUSTCOREBANK=? Where CUSTID = ?";
		logger.debug(Literal.SQL + sql);
		extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setString(index++, ucicId);
			ps.setLong(index, Long.parseLong(custId));

		});
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateUcicIdInCustomers(String custId, String ucicId) {
		logger.debug(Literal.ENTERING);
		String sql = "UPDATE CUSTOMERS SET CUSTCOREBANK=? Where CUSTID = ?";
		logger.debug(Literal.SQL + sql);
		extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setString(index++, ucicId);
			ps.setLong(index, Long.parseLong(custId));

		});
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<ExtUcicData> fetchListOfAckRecords(int status, int ack_status) {
		logger.debug(Literal.ENTERING);

		String sql = "SELECT HEADER_ID,CUSTID,UCIC_ID,PROCESS_FLAG,PROCESS_DESC FROM UCIC_RESP_FILE_DATA WHERE STATUS = ? AND ACK_SENT = ?";

		logger.debug(Literal.SQL + sql);

		List<ExtUcicData> recordsList = new ArrayList<ExtUcicData>();
		extNamedJdbcTemplate.getJdbcOperations().query(sql, ps -> {
			int i = 1;
			ps.setLong(i++, status);
			ps.setLong(i, ack_status);
		}, rs -> {
			ExtUcicData extUcicData = new ExtUcicData();
			extUcicData.setId(rs.getLong("HEADER_ID"));
			extUcicData.setCustId(rs.getString("CUSTID"));
			extUcicData.setUcicId(rs.getString("UCIC_ID"));
			extUcicData.setProcessStatus(rs.getInt("PROCESS_FLAG"));
			extUcicData.setProcessDesc(StringUtils.stripToEmpty(rs.getString("PROCESS_DESC")));
			recordsList.add(extUcicData);
		});

		logger.debug(Literal.LEAVING);
		return recordsList;
	}

	@Override
	public int updateAckFileRecordsStatus(List<ExtUcicData> extUcicDatas) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE UCIC_RESP_FILE_DATA SET ACK_SENT=? WHERE CUSTID = ? AND HEADER_ID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return extNamedJdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				ExtUcicData item = extUcicDatas.get(index);
				ps.setLong(1, InterfaceConstants.ACK_SENT);
				ps.setString(2, item.getCustId());
				ps.setLong(3, item.getId());
			}

			@Override
			public int getBatchSize() {
				return extUcicDatas.size();
			}
		}).length;

	}

	// ------------------------------------------------------------------------------------------------------------------
	@Override
	public boolean setLoanAccNumber(ExtUcicCust customer, String mandateType) {
		logger.debug(Literal.ENTERING);
		String sql = "SELECT MD.ACCNUMBER FROM MANDATES MD "
				+ "	INNER JOIN FINANCEMAIN FM ON  FM.MANDATEID = MD.MANDATEID "
				+ "	WHERE MD.MANDATETYPE = ? AND MD.CUSTID = ? AND FM.FINID = ?";

		logger.debug(Literal.SQL + sql);
		Object[] parameters = new Object[] { mandateType, customer.getCustId(), customer.getFinId() };
		try {
			logger.debug(Literal.LEAVING);
			extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, (rs, rowNum) -> {
				customer.setAccNumber(StringUtils.stripToEmpty(rs.getString("ACCNUMBER")));
				return true;
			}, parameters);

		} catch (Exception e) {
			customer.setAccNumber("");
			return false;
		}
		return false;
	}

	@Override
	public boolean setCustEmployerName(ExtUcicCust customer, int currentEmployer) {

		logger.debug(Literal.ENTERING);
		String sql = "SELECT COMPANYNAME FROM CUSTOMEREMPDETAILS WHERE CURRENTEMPLOYER = ? AND CUSTID = ?";

		logger.debug(Literal.SQL + sql);
		Object[] parameters = new Object[] { currentEmployer, customer.getCustId() };
		try {
			logger.debug(Literal.LEAVING);
			extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, (rs, rowNum) -> {
				customer.setCompanyName(StringUtils.stripToEmpty(rs.getString("COMPANYNAME")));
				return true;
			}, parameters);

		} catch (Exception e) {
			customer.setCompanyName("");
			return false;
		}
		return false;

	}

	@Override
	public List<ExtCustEmail> getCustEmails(ExtUcicCust customer) {
		logger.debug(Literal.ENTERING);
		String queryStr = " Select CUSTEMAIL,CUSTEMAILPRIORITY from customerEmails where  CUSTID = ?";
		logger.debug(Literal.SQL + queryStr);

		List<ExtCustEmail> custEmails = new ArrayList<ExtCustEmail>();

		try {
			extNamedJdbcTemplate.getJdbcOperations().query(queryStr, ps -> {
				int i = 1;
				ps.setLong(i, customer.getCustId());
			}, rs -> {
				ExtCustEmail custEmail = new ExtCustEmail();
				custEmail.setEmail(rs.getString("CUSTEMAIL"));
				custEmail.setPriority(rs.getInt("CUSTEMAILPRIORITY"));
				custEmails.add(custEmail);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug(Literal.LEAVING);
		return custEmails;
	}

	@Override
	public List<ExtCustPhones> getCustPhones(ExtUcicCust customer) {
		logger.debug(Literal.ENTERING);
		String queryStr = " SELECT PhoneCustId,PHONENUMBER,PHONETYPEPRIORITY FROM customerPhonenumbers WHERE PhoneCustId = ?";
		logger.debug(Literal.SQL + queryStr);

		List<ExtCustPhones> custPhones = new ArrayList<ExtCustPhones>();

		try {
			extNamedJdbcTemplate.getJdbcOperations().query(queryStr, ps -> {
				int i = 1;
				ps.setLong(i, customer.getCustId());
			}, rs -> {
				ExtCustPhones custPhone = new ExtCustPhones();
				custPhone.setMobile(rs.getString("PHONENUMBER"));
				custPhone.setPriority(rs.getInt("PHONETYPEPRIORITY"));
				custPhones.add(custPhone);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug(Literal.LEAVING);
		return custPhones;
	}

	@Override
	public List<ExtCustAddress> getCustAddress(ExtUcicCust customer) {

		logger.debug(Literal.ENTERING);
		String queryStr = " Select CUSTADDRTYPE,CUSTADDRLINE1,CUSTADDRLINE2,CUSTADDRLINE3,"
				+ "CUSTADDRLINE4,CUSTADDRCITY,CUSTADDRPROVINCE,CUSTADDRZIP,CUSTADDRPRIORITY"
				+ "	FROM customerAddresses where  CUSTID = ?";
		logger.debug(Literal.SQL + queryStr);

		List<ExtCustAddress> custAddresses = new ArrayList<ExtCustAddress>();

		try {
			extNamedJdbcTemplate.getJdbcOperations().query(queryStr, ps -> {
				int i = 1;
				ps.setLong(i, customer.getCustId());
			}, rs -> {
				ExtCustAddress address = new ExtCustAddress();
				address.setAddrType(rs.getString("CUSTADDRTYPE"));
				address.setAddrLine1(rs.getString("CUSTADDRLINE1"));
				address.setAddrLine2(rs.getString("CUSTADDRLINE2"));
				address.setAddrLine3(rs.getString("CUSTADDRLINE3"));
				address.setAddrLine4(rs.getString("CUSTADDRLINE4"));
				address.setAddrCity(rs.getString("CUSTADDRCITY"));
				address.setAddrState(rs.getString("CUSTADDRPROVINCE"));
				address.setAddrPin(rs.getString("CUSTADDRZIP"));
				address.setPriority(rs.getInt("CUSTADDRPRIORITY"));
				custAddresses.add(address);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug(Literal.LEAVING);
		return custAddresses;
	}

	@Override
	public boolean isRecordExist(long finId) {
		String sql = "Select count(1) from UCIC_CUSTOMERS Where FINID = ?";
		logger.debug(Literal.SQL + sql);
		try {
			return extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class, finId) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}

	}

	@Override
	public void deleteRecord(long finId) {
		logger.debug(Literal.ENTERING);
		String queryStr = "DELETE FROM UCIC_CUSTOMERS  WHERE FINID = ?";
		logger.debug(Literal.SQL + queryStr);
		extNamedJdbcTemplate.getJdbcOperations().update(queryStr.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, finId);
		});
		logger.debug(Literal.LEAVING);

	}

	@Override
	public List<ExtCustDoc> getCustDocs(ExtUcicCust customer) {
		logger.debug(Literal.ENTERING);
		String queryStr = " SELECT CUSTDOCCATEGORY,CUSTDOCTITLE FROM CUSTOMERDOCUMENTS WHERE CUSTID = ? ";
		logger.debug(Literal.SQL + queryStr);

		List<ExtCustDoc> custDocsList = new ArrayList<ExtCustDoc>();

		try {
			extNamedJdbcTemplate.getJdbcOperations().query(queryStr, ps -> {
				int i = 1;
				ps.setLong(i, customer.getCustId());
			}, rs -> {
				ExtCustDoc doc = new ExtCustDoc();
				doc.setDocCategory(rs.getString("CUSTDOCCATEGORY"));
				doc.setDocTitle(rs.getString("CUSTDOCTITLE"));
				custDocsList.add(doc);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug(Literal.LEAVING);
		return custDocsList;
	}

	@Override
	public void updateFileRecordProcessingFlagAndStatus(long header_id, String custid, int status, int processFlag,
			String processDesc, int ackStatus) {
		logger.debug(Literal.ENTERING);
		String sql = "UPDATE UCIC_RESP_FILE_DATA SET STATUS=?, PROCESS_FLAG = ?, PROCESS_DESC=?, ACK_SENT = ? Where CUSTID = ? AND HEADER_ID = ?";
		logger.debug(Literal.SQL + sql);
		extNamedJdbcTemplate.getJdbcOperations().update(sql, ps -> {
			int index = 1;
			ps.setLong(index++, status);
			ps.setLong(index++, processFlag);
			ps.setString(index++, processDesc);
			ps.setLong(index++, ackStatus);
			ps.setString(index++, custid);
			ps.setLong(index, header_id);
		});
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<ExtUcicData> getCustBasedOnUcicId(String ucicId) {
		logger.debug(Literal.ENTERING);
		String queryStr = " SELECT CUSTID FROM CUSTOMERS WHERE CUSTCOREBANK = ? ";
		logger.debug(Literal.SQL + queryStr);

		List<ExtUcicData> ucicDatas = new ArrayList<ExtUcicData>();

		try {
			extNamedJdbcTemplate.getJdbcOperations().query(queryStr, ps -> {
				int i = 1;
				ps.setString(i, ucicId);
			}, rs -> {
				ExtUcicData doc = new ExtUcicData();
				doc.setCustId(rs.getString("CUSTID"));
				ucicDatas.add(doc);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug(Literal.LEAVING);
		return ucicDatas;
	}

	@Override
	public String getExistingUcicIc(String custId) {
		String sql = " SELECT CUSTCOREBANK FROM CUSTOMERS WHERE CUSTID = ? ";

		try {
			return extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, String.class, custId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}
		return null;
	}

	@Override
	public boolean isCustomerInMakerStage(String custId) {
		String sql = "Select count(1) from CUSTOMERS_TEMP Where CUSTID = ?";
		logger.debug(Literal.SQL + sql);
		try {
			return extNamedJdbcTemplate.getJdbcOperations().queryForObject(sql, Integer.class, custId) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public List<ExtUcicCust> fetchCustomersForWeeklyFile(int fileStatus) {
		logger.debug(Literal.ENTERING);

		String sql = "SELECT SOURCE_SYSTEM,CUSTID,CUSTISBLOCKED,CUSTCOREBANK,PAN,AADHAAR,"
				+ " CUSTCTGCODE,CUSTOMER_TYPE,CUSTDOB,CUSTSHRTNAME,MOBILE1,EMAIL1,"
				+ " LASTMNTON FROM UCIC_CUSTOMERS WHERE FILE_STATUS = ?";

		logger.debug(Literal.SQL + sql);

		List<ExtUcicCust> customersList = new ArrayList<ExtUcicCust>();
		extNamedJdbcTemplate.getJdbcOperations().query(sql, ps -> {
			int i = 1;
			ps.setLong(i, fileStatus);
		}, rs -> {
			ExtUcicCust customer = new ExtUcicCust();

			customer.setSourceSystem(rs.getString("SOURCE_SYSTEM"));
			customer.setCustId(rs.getLong("CUSTID"));
			customer.setCustIsBlocked(rs.getString("CUSTISBLOCKED"));
			customer.setCustCoreBank(rs.getString("CUSTCOREBANK"));
			customer.setCustCtgCode(StringUtils.stripToEmpty(rs.getString("CUSTCTGCODE")));
			customer.setCustomertype(StringUtils.stripToEmpty(rs.getString("CUSTOMER_TYPE")));
			customer.setCustShrtName(StringUtils.stripToEmpty(rs.getString("CUSTSHRTNAME")));
			customer.setCustDob(rs.getDate("CUSTDOB"));
			customer.setLastMntOn(rs.getDate("LASTMNTON"));

			customer.setPan(StringUtils.stripToEmpty(rs.getString("PAN")));
			customer.setAadhaar(StringUtils.stripToEmpty(rs.getString("AADHAAR")));

			customer.setEmail1(rs.getString("EMAIL1"));
			customer.setMobile1(StringUtils.stripToEmpty(rs.getString("MOBILE1")));

			customersList.add(customer);
		});

		logger.debug(Literal.LEAVING);
		return customersList;
	}

	public void setExtDataSource(DataSource extDataSource) {
		this.extNamedJdbcTemplate = new NamedParameterJdbcTemplate(extDataSource);
	}

	@Override
	public List<ExtUcicFinDetails> getCustFinDetailsByCustId(long custId) {
		logger.debug(Literal.ENTERING);

		String sql = "SELECT CUSTID,FINID,FINREFERENCE,CLOSINGSTATUS,CLOSEDDATE,LASTMNTON "
				+ " FROM FINANCEMAIN WHERE CUSTID = ?";

		logger.debug(Literal.SQL + sql);

		List<ExtUcicFinDetails> customersFinList = new ArrayList<ExtUcicFinDetails>();
		extNamedJdbcTemplate.getJdbcOperations().query(sql, ps -> {
			int i = 1;
			ps.setLong(i, custId);
		}, rs -> {
			ExtUcicFinDetails finDetails = new ExtUcicFinDetails();
			finDetails.setFinId(rs.getLong("FINID"));
			finDetails.setCustId(rs.getLong("CUSTID"));
			finDetails.setFinreference(rs.getString("FINREFERENCE"));
			finDetails.setClosingStatus(rs.getString("CLOSINGSTATUS"));
			finDetails.setClosedDate(rs.getDate("CLOSEDDATE"));
			finDetails.setLastmntOn(rs.getDate("LASTMNTON"));
			customersFinList.add(finDetails);
		});

		logger.debug(Literal.LEAVING);
		return customersFinList;
	}

	@Override
	public List<ExtUcicFinDetails> getCustomerFinDetailsByCustCif(String custCif) {
		logger.debug(Literal.ENTERING);
		String sql = "SELECT FJ.FINID,FJ.FINREFERENCE,FM.CLOSEDDATE,FM.CLOSINGSTATUS,FM.LASTMNTON FROM FINJOINTACCOUNTDETAILS FJ "
				+ " INNER JOIN FINANCEMAIN FM ON FJ.FINID = FM.FINID WHERE FJ.CUSTCIF = ?";
		logger.debug(Literal.SQL + sql);

		List<ExtUcicFinDetails> ucicJointAccounts = new ArrayList<ExtUcicFinDetails>();
		extNamedJdbcTemplate.getJdbcOperations().query(sql, ps -> {
			int i = 1;
			ps.setString(i, custCif);
		}, rs -> {
			ExtUcicFinDetails finDetails = new ExtUcicFinDetails();
			finDetails.setFinId(rs.getLong("FINID"));
			finDetails.setFinreference(rs.getString("FINREFERENCE"));
			finDetails.setClosingStatus(rs.getString("CLOSINGSTATUS"));
			finDetails.setClosedDate(rs.getDate("CLOSEDDATE"));
			finDetails.setLastmntOn(rs.getDate("LASTMNTON"));
			ucicJointAccounts.add(finDetails);
		});

		logger.debug(Literal.LEAVING);
		return ucicJointAccounts;
	}

	@Override
	public List<ExtUcicFinDetails> getCustomerFinDetailsWithGuarantorCif(String custCif) {
		logger.debug(Literal.ENTERING);

		String sql = " SELECT FJ.FINID,FJ.FINREFERENCE,FM.CLOSEDDATE,FM.CLOSINGSTATUS,FM.LASTMNTON FROM FINGUARANTORSDETAILS FJ "
				+ " INNER JOIN FINANCEMAIN FM ON FJ.FINID = FM.FINID WHERE FJ.GUARANTORCIF = ?";

		logger.debug(Literal.SQL + sql);

		List<ExtUcicFinDetails> extUcicGuarantors = new ArrayList<ExtUcicFinDetails>();
		extNamedJdbcTemplate.getJdbcOperations().query(sql, ps -> {
			int i = 1;
			ps.setString(i, custCif);
		}, rs -> {
			ExtUcicFinDetails finDetails = new ExtUcicFinDetails();
			finDetails.setFinId(rs.getLong("FINID"));
			finDetails.setFinreference(rs.getString("FINREFERENCE"));
			finDetails.setClosingStatus(rs.getString("CLOSINGSTATUS"));
			finDetails.setClosedDate(rs.getDate("CLOSEDDATE"));
			finDetails.setLastmntOn(rs.getDate("LASTMNTON"));
			extUcicGuarantors.add(finDetails);
		});

		logger.debug(Literal.LEAVING);
		return extUcicGuarantors;
	}

}
