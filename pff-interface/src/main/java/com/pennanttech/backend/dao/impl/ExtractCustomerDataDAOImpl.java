package com.pennanttech.backend.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennanttech.backend.dao.ExtractCustomerDataDAO;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.model.CustomerStaging;
import com.pennanttech.pff.model.DownloadHeader;

public class ExtractCustomerDataDAOImpl extends BasicDao<DownloadHeader> implements ExtractCustomerDataDAO {
	private static Logger logger = LogManager.getLogger(ExtractCustomerDataDAOImpl.class);

	private NamedParameterJdbcTemplate portalTemplate;

	public void setPortalDataSource(DataSource dataSource) {
		this.portalTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public long saveDownloadheader(String processType, String downloadCode) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" DOWNLOADHEADER");
		sql.append(" (DOWNLOAD_CODE, DOWNLOAD_APP, PROCESS_TYPE, APP_STATUS, APP_START_TIME)");
		sql.append(" Values(?, ?, ?, ?, ?)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();

			this.portalTemplate.getJdbcOperations().update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "headerid" });
					int index = 1;
					ps.setString(index++, downloadCode);
					ps.setString(index++, "PLF");
					ps.setString(index++, processType);
					ps.setString(index++, "I");
					ps.setDate(index, JdbcUtil.getDate(DateUtil.getSysDate()));

					return ps;
				}
			}, keyHolder);

			return keyHolder.getKey().longValue();
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void updateDownloadheader(long headerId, int count) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Update");
		sql.append(" DOWNLOADHEADER set");
		sql.append(" APP_STATUS= ?, APP_COUNT= ?, APP_END_TIME = ?");
		sql.append(" where HEADERID= ?");

		logger.trace(Literal.SQL + sql.toString());

		this.portalTemplate.getJdbcOperations().update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index++, "P");
				ps.setInt(index++, count);
				ps.setDate(index++, JdbcUtil.getDate(DateUtil.getSysDate()));

				ps.setLong(index, headerId);
			}
		});

	}

	@Override
	public List<Long> getFinApprovedCustomers(Timestamp prevTime, Timestamp curTime) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Distinct CustID FROM FINANCEMAIN t1");
		sql.append(" Where t1.LASTMNTON < ?");
		sql.append(" and (t1.finisActive = ? or (t1.finisactive = ? and");
		sql.append(" t1.CLOSINGSTATUS in (?, ?)))");
		sql.append(" and FINAPPROVEDDATE= ?");

		if (prevTime != null) {
			sql.append(" and t1.LASTMNTON >= ?");
		}

		sql.append(" Union");
		sql.append(" Select Distinct t1.CustID");
		sql.append(" From Customers t1");
		sql.append(" Inner join FinanceMain t2 on t1.custid = t2.custid");
		sql.append(" and (t2.finisActive = ? or (t2.finisactive = ? and");
		sql.append(" t2.CLOSINGSTATUS in (?, ?)))");
		sql.append(" where t1.LASTMNTON < ? and t1.LASTMNTON >= ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;

				ps.setTimestamp(index++, curTime);
				ps.setInt(index++, 1);
				ps.setInt(index++, 0);
				ps.setString(index++, "E");
				ps.setString(index++, "M");
				ps.setDate(index++, JdbcUtil.getDate(SysParamUtil.getAppDate()));

				if (prevTime != null) {
					ps.setTimestamp(index++, prevTime);
				}

				ps.setInt(index++, 1);
				ps.setInt(index++, 0);
				ps.setString(index++, "E");
				ps.setString(index++, "M");
				ps.setTimestamp(index++, curTime);
				ps.setTimestamp(index, prevTime);

			}

		}, new RowMapper<Long>() {
			@Override
			public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getLong("CustID");
			}
		});
	}

	@Override
	public List<Long> getFinClosedCustomers(Timestamp prevTime, Timestamp curTime, String processType) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Distinct CustID");
		sql.append(" From FINANCEMAIN t1");
		sql.append(" Where t1.LASTMNTON < ? and t1.finisActive = ?");
		sql.append(" and t1.CLOSINGSTATUS = ? and CLOSEDDATE = ?");

		if (prevTime != null) {
			sql.append(" and t1.LASTMNTON >= ?");
		}

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;

				ps.setTimestamp(index++, curTime);
				ps.setInt(index++, 0);
				ps.setString(index++, "C");
				ps.setDate(index++, JdbcUtil.getDate(SysParamUtil.getAppDate()));

				if (prevTime != null) {
					ps.setTimestamp(index, prevTime);
				}
			}

		}, new RowMapper<Long>() {
			@Override
			public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getLong("CustID");
			}
		});
	}

	@Override
	public List<Long> getFinActiveCustomers() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Distinct CustID");
		sql.append(" From FINANCEMAIN t1");
		sql.append(" where (t1.finisActive = ? or");
		sql.append(" (t1.finisactive = ? and t1.CLOSINGSTATUS in (?,?)))");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;

				ps.setInt(index++, 1);
				ps.setInt(index++, 0);
				ps.setString(index++, "E");
				ps.setString(index, "M");
			}

		}, new RowMapper<Long>() {
			@Override
			public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getLong("CustID");
			}
		});
	}

	@Override
	public List<Long> extractCustomers(Timestamp curTime) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustId From Customers");
		sql.append(" Where LASTMNTON <= ?");
		sql.append(" and CustID in");
		sql.append(" (Select");
		sql.append(" CustID From FinanceMain t1");
		sql.append(" Where (t1.finisActive = ? or (t1.finisactive = ?");
		sql.append(" and t1.CLOSINGSTATUS in (?,?))))");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;

				ps.setTimestamp(index++, curTime);
				ps.setInt(index++, 1);
				ps.setInt(index++, 0);
				ps.setString(index++, "E");
				ps.setString(index, "M");
			}

		}, new RowMapper<Long>() {
			@Override
			public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getLong("CustID");
			}
		});
	}

	@Override
	public void saveCustomerStaging(CustomerStaging cs) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert Into");
		sql.append(" CUSTOMER_STAGING");
		sql.append(" (HeaderId, Cif, FirstName, MiddleName, LastName, FullName");
		sql.append(", Dob, Gender, CustMaritalSts, CreatedOn, LastMntOn, Salutation");
		sql.append(", SalutationDesc, CustType, CustDftBranch, CustDftBranchName, CustStaffId");
		sql.append(", CustDSA, Pan, AadharNo, Address, Email, AltEmail, PhoneNo, AltPhoneNo");
		sql.append(") Values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			this.portalTemplate.getJdbcOperations().update(sql.toString(), new PreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;

					ps.setLong(index++, cs.getHeaderId());
					ps.setString(index++, cs.getCif());
					ps.setString(index++, cs.getFirstName());
					ps.setString(index++, cs.getMiddleName());
					ps.setString(index++, cs.getLastName());
					ps.setString(index++, cs.getFullName());
					ps.setDate(index++, JdbcUtil.getDate(cs.getDob()));
					ps.setString(index++, cs.getGender());
					ps.setString(index++, cs.getCustMaritalSts());
					ps.setDate(index++, JdbcUtil.getDate(cs.getCreatedOn()));
					ps.setDate(index++, JdbcUtil.getDate(cs.getLastMntOn()));
					ps.setString(index++, cs.getSalutation());
					ps.setString(index++, cs.getSalutationDesc());
					ps.setString(index++, cs.getCustType());
					ps.setString(index++, cs.getCustDftBranch());
					ps.setString(index++, cs.getCustDftBranchName());
					ps.setString(index++, cs.getCustStaffId());
					ps.setString(index++, cs.getCustDSA());
					ps.setString(index++, cs.getPan());
					ps.setString(index++, cs.getAadharNo());
					ps.setString(index++, cs.getAddress());
					ps.setString(index++, cs.getEmail());
					ps.setString(index++, cs.getAltEmail());
					ps.setString(index++, cs.getPhoneNo());
					ps.setString(index, cs.getAltPhoneNo());
				}
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public CustomerStaging getCustomerDetailsById(long custId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" C.CustCIF, C.CustSalutationCode, SC.SALUATIONDESC LovDescCustSalutationCodeName");
		sql.append(", C.CustFName, C.CustMName, C.CustLName, C.CustShrtName, C.CustDOB");
		sql.append(", C.CustGenderCode, G.GENDERDESC LovDescCustGenderCodeName");
		sql.append(", C.CustMaritalSts, MS.MARITALSTSDESC LovDescCustMaritalStsName");
		sql.append(", ct.CUSTTYPEDESC LovDescCustTypeCodeName, BR.BRANCHDESC LovDescCustDftBranchName");
		sql.append(", '' CUSTCREATEDATE, C.LastMntOn, C.CustTypeCode, C.CustDftBranch, C.CustStaffID, C.CustDSA");
		sql.append(" From Customers C");
		sql.append(" Left join BMTSalutations SC on SC.SALUTATIONCODE = C.CUSTSALUTATIONCODE");
		sql.append(" Left join BMTGenders G on G.GENDERCODE = C.CUSTGENDERCODE");
		sql.append(" Left join BMTMaritalStatusCodes MS on MS.MARITALSTSCODE = C.CUSTMARITALSTS");
		sql.append(" Inner join RMTCustTypes ct on ct.CUSTTYPECODE = C.CUSTTYPECODE");
		sql.append(" Inner join RMTBranches BR on BR.BRANCHCODE = C.CUSTDFTBRANCH");
		sql.append(" Where CustID = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new RowMapper<CustomerStaging>() {
				@Override
				public CustomerStaging mapRow(ResultSet rs, int rowNum) throws SQLException {
					CustomerStaging cs = new CustomerStaging();

					cs.setCif(rs.getString("CustCIF"));
					cs.setSalutation(rs.getString("CustSalutationCode"));
					cs.setSalutationDesc(rs.getString("LovDescCustSalutationCodeName"));
					cs.setFirstName(rs.getString("CustFName"));
					cs.setMiddleName(rs.getString("CustMName"));
					cs.setLastName(rs.getString("CustLName"));
					cs.setFullName(rs.getString("CustShrtName"));
					cs.setDob(rs.getTimestamp("CustDOB"));
					cs.setGender(rs.getString("LovDescCustGenderCodeName"));
					cs.setCustMaritalSts(rs.getString("CustMaritalSts"));
					cs.setCustType(rs.getString("CustTypeCode"));
					cs.setCustDftBranch(rs.getString("CustDftBranch"));
					cs.setCustDftBranchName(rs.getString("LovDescCustDftBranchName"));
					cs.setLastMntOn(rs.getTimestamp("LastMntOn"));
					cs.setCustStaffId(rs.getString("CustStaffID"));
					cs.setCustDSA(rs.getString("CustDSA"));
					return cs;
				}
			}, custId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void setCustAddressDetails(long custId, CustomerStaging custData) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustAddrHNbr, CustFlatNbr, CustAddrStreet AddrStreet, CustPOBox");
		sql.append(", CustAddrZIP, CustAddrCity, CustAddrCountry, CustAddrProvince");
		sql.append(", CustAddrLine1, CustAddrLine2, PCCityName, CountryDesc");
		sql.append(", CPProvinceName ProvinceName, '' CUSTADDRDISTRICT, '' DistrictName");
		sql.append(" From ");
		sql.append(getCALogicalView(TableType.AVIEW));
		sql.append(" Where CustID = ? and custAddrPriority = ?");

		logger.trace(Literal.SQL + sql.toString());

		List<String> custAdress = this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setLong(index++, custId);
				ps.setInt(index, 5);
			}
		}, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				StringBuilder adr = new StringBuilder();

				adr.append(StringUtils.trimToEmpty(rs.getString("CustAddrHNbr"))).append(" ");
				adr.append(StringUtils.trimToEmpty(rs.getString("CustFlatNbr"))).append(" ");
				adr.append(StringUtils.trimToEmpty(rs.getString("AddrStreet"))).append(" ");
				adr.append(rs.getString("CustAddrLine1") != null ? rs.getString("CustAddrLine1") : "").append(" ");
				adr.append(rs.getString("CustAddrLine2") != null ? rs.getString("CustAddrLine2") : "").append(" ");
				adr.append(rs.getString("PCCityName") != null ? rs.getString("PCCityName") : "").append(" ");
				adr.append(rs.getString("CountryDesc") != null ? rs.getString("CountryDesc") : "").append(" ");
				adr.append(rs.getString("DistrictName") != null ? rs.getString("DistrictName") : "").append(" ");
				adr.append(rs.getString("ProvinceName") != null ? rs.getString("ProvinceName") : "").append(" ");
				adr.append(rs.getString("CustAddrZIP") != null ? rs.getString("CustAddrZIP") : "").append(" ");
				adr.append(rs.getString("CustPOBox") != null ? rs.getString("CustPOBox") : "").append(" ");
				adr.append(rs.getString("CustAddrCity") != null ? rs.getString("CustAddrCity") : "").append(" ");

				return adr.toString();
			}
		});

		if (custAdress != null && !custAdress.isEmpty()) {
			custData.setAddress(custAdress.get(0));
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void setCustPhoneDetails(long custId, CustomerStaging custData) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" phoneNumber, phoneTypePriority");
		sql.append(" From CUSTOMERPHONENUMBERS");
		sql.append(" Where PhoneCustID = ?");
		sql.append(" order by phoneTypePriority desc");

		logger.trace(Literal.SQL + sql.toString());

		List<CustomerPhoneNumber> cpn = this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setLong(index, custId);
			}
		}, new RowMapper<CustomerPhoneNumber>() {
			@Override
			public CustomerPhoneNumber mapRow(ResultSet rs, int rowNum) throws SQLException {
				CustomerPhoneNumber cpn = new CustomerPhoneNumber();

				cpn.setPhoneNumber(rs.getString("PhoneNumber"));
				cpn.setPhoneTypePriority(rs.getInt("PhoneTypePriority"));

				return cpn;
			}
		});

		cpn.forEach(custPhone -> {
			if (custPhone.getPhoneTypePriority() == 5) {
				custData.setPhoneNo(custPhone.getPhoneNumber());
			} else {
				custData.setAltPhoneNo(custPhone.getPhoneNumber());
			}
		});

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void setCustEmailDetails(long custId, CustomerStaging custData) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" custEMail, custEMailPriority");
		sql.append(" From CUSTOMEREMAILS");
		sql.append(" Where custID = ?");
		sql.append(" order by custEMailPriority desc");

		logger.trace(Literal.SQL + sql.toString());

		List<CustomerEMail> custEmails = this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setLong(index, custId);
			}
		}, new RowMapper<CustomerEMail>() {
			@Override
			public CustomerEMail mapRow(ResultSet rs, int rowNum) throws SQLException {
				CustomerEMail ce = new CustomerEMail();

				ce.setCustEMail(rs.getString("CustEMail"));
				ce.setCustEMailPriority(rs.getInt("CustEMailPriority"));

				return ce;
			}
		});

		custEmails.forEach(custEmail -> {
			if (custEmail.getCustEMailPriority() == 5) {
				custData.setEmail(custEmail.getCustEMail());
			} else {
				custData.setAltEmail(custEmail.getCustEMail());
			}
		});

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void setCustDocDetails(long custId, CustomerStaging custData) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CustDocCategory, CustDocTitle");
		sql.append(" From CustomerDocuments");
		sql.append(" where CustID = ? and CustDocCategory in (?,?)");

		logger.trace(Literal.SQL + sql.toString());

		List<CustomerDocument> custDocs = this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setLong(index++, custId);
				ps.setString(index++, "01");
				ps.setString(index, "03");
			}
		}, new RowMapper<CustomerDocument>() {
			@Override
			public CustomerDocument mapRow(ResultSet rs, int rowNum) throws SQLException {
				CustomerDocument cd = new CustomerDocument();

				cd.setCustDocCategory(rs.getString("CustDocCategory"));
				cd.setCustDocTitle(rs.getString("CustDocTitle"));

				return cd;
			}
		});

		custDocs.forEach(doc -> {
			if ("03".equals(doc.getCustDocCategory())) {
				custData.setPan(doc.getCustDocTitle());
			}
			if ("01".equals(doc.getCustDocCategory())) {
				custData.setAadharNo(doc.getCustDocTitle());
			}
		});

		logger.debug(Literal.LEAVING);
	}

	private String getCAColumns() {
		StringBuilder column = new StringBuilder("SELECT");
		column.append(" T1.CUSTADDRESSID");
		column.append(", T1.CUSTID");
		column.append(", T1.CUSTADDRTYPE");
		column.append(", T2.ADDRTYPEDESC");
		column.append(", T1.CUSTADDRHNBR");
		column.append(", T1.CUSTFLATNBR");
		column.append(", T1.CUSTADDRSTREET");
		column.append(", T1.CUSTADDRLINE1");
		column.append(", T1.CUSTADDRLINE2");
		column.append(", T1.CUSTADDRLINE3");
		column.append(", T1.CUSTADDRLINE4");
		column.append(", T1.CUSTDISTRICT");
		column.append(", T1.CUSTPOBOX");
		column.append(", T1.CUSTADDRCITY");
		column.append(", T1.TYPEOFRESIDENCE");
		column.append(", T3.PCCITYNAME");
		column.append(", T1.CUSTADDRPROVINCE");
		column.append(", T4.CPPROVINCENAME");
		column.append(", T1.CUSTADDRCOUNTRY");
		column.append(", T5.COUNTRYDESC");
		column.append(", T1.CUSTADDRZIP");
		column.append(", T1.CUSTADDRPHONE");
		column.append(", T1.CUSTADDRFROM");
		column.append(", T1.CUSTADDRPRIORITY");
		column.append(", T1.VERSION");
		column.append(", T1.LASTMNTBY");
		column.append(", T1.LASTMNTON");
		column.append(", T1.RECORDSTATUS");
		column.append(", T1.ROLECODE");
		column.append(", T1.NEXTROLECODE");
		column.append(", T1.TASKID");
		column.append(", T1.NEXTTASKID");
		column.append(", T1.RECORDTYPE");
		column.append(", T1.WORKFLOWID");
		column.append(", T6.AREANAME");
		return column.toString();
	}

	private String getCAJoin(TableType tableType) {
		StringBuilder query = new StringBuilder();
		query.append(getCAColumns());
		query.append(" FROM CUSTOMERADDRESSES").append(tableType.getSuffix()).append(" T1");
		query.append(" LEFT JOIN BMTADDRESSTYPES T2 ON T1.CUSTADDRTYPE = T2.ADDRTYPECODE");
		query.append(" LEFT JOIN RMTPROVINCEVSCITY T3 ON T1.CUSTADDRCOUNTRY = T3.PCCOUNTRY");
		query.append(" AND T1.CUSTADDRPROVINCE = T3.PCPROVINCE AND T1.CUSTADDRCITY = T3.PCCITY");
		query.append(" LEFT JOIN RMTCOUNTRYVSPROVINCE T4 ON T1.CUSTADDRCOUNTRY = T4.CPCOUNTRY");
		query.append(" AND T1.CUSTADDRPROVINCE = T4.CPPROVINCE");
		query.append(" LEFT JOIN BMTCOUNTRIES T5 ON T1.CUSTADDRCOUNTRY = T5.COUNTRYCODE");
		query.append(" LEFT JOIN PINCODES T6 ON T1.CUSTADDRZIP = T6.PINCODE");
		return query.toString();
	}

	@Override
	public String getCALogicalView(TableType tableType) {
		if (tableType == TableType.TEMP_TAB || tableType == TableType.MAIN_TAB) {
			return "CUSTOMERADDRESSES" + tableType.getSuffix();
		}

		StringBuilder query = new StringBuilder("(");

		if (tableType == TableType.TVIEW || tableType == TableType.VIEW) {
			query.append(getCAJoin(TableType.TEMP_TAB));

			if (tableType == TableType.VIEW) {
				query.append(" UNION ALL ");
				query.append(getCAJoin(TableType.MAIN_TAB));
				query.append(" WHERE (NOT (EXISTS (SELECT 1 FROM CUSTOMERADDRESSES_TEMP");
				query.append(" WHERE (CUSTOMERADDRESSES_TEMP.CUSTID = T1.CUSTID AND");
				query.append(" CUSTOMERADDRESSES_TEMP.CUSTADDRTYPE = T1.CUSTADDRTYPE))))");
			}
		} else if (tableType == TableType.AVIEW) {
			query.append(getCAJoin(TableType.MAIN_TAB));
		}

		query.append(") T");

		return query.toString();
	}
}
