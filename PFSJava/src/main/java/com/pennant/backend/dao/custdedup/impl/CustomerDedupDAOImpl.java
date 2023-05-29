package com.pennant.backend.dao.custdedup.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.custdedup.CustomerDedupDAO;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class CustomerDedupDAOImpl extends BasicDao<CustomerDedup> implements CustomerDedupDAO {
	private static Logger logger = LogManager.getLogger(CustomerDedupDAOImpl.class);

	public CustomerDedupDAOImpl() {
		super();
	}

	@Override
	public void saveList(List<CustomerDedup> cdList, String type) {
		StringBuilder sql = new StringBuilder("Insert Into CustomerDedupDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("( FinID, FinReference, CustCIF, CustFName, CustLName, CustShrtName, CustDOB, CustCRCPR");
		sql.append(", CustPassportNo, MobileNumber, CustNationality, DedupRule, Override, OverrideUser");
		sql.append(", Module");
		sql.append(") Values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 1;

				CustomerDedup cd = cdList.get(i);

				ps.setLong(index++, cd.getFinID());
				ps.setString(index++, cd.getFinReference());
				ps.setString(index++, cd.getCustCIF());
				ps.setString(index++, cd.getCustFName());
				ps.setString(index++, cd.getCustLName());
				ps.setString(index++, cd.getCustShrtName());
				ps.setDate(index++, JdbcUtil.getDate(cd.getCustDOB()));
				ps.setString(index++, cd.getCustCRCPR());
				ps.setString(index++, cd.getCustPassportNo());
				ps.setString(index++, cd.getMobileNumber());
				ps.setString(index++, cd.getCustNationality());
				ps.setString(index++, cd.getDedupRule());
				ps.setBoolean(index++, cd.isOverride());
				ps.setString(index++, cd.getOverrideUser());
				ps.setString(index, cd.getModule());
			}

			@Override
			public int getBatchSize() {
				return cdList.size();
			}
		});
	}

	@Override
	public void updateList(List<CustomerDedup> cdList) {
		StringBuilder sql = new StringBuilder("Update CustomerDedupDetail");
		sql.append(" Set CustFName = ?, CustLName = ?, CustShrtName = ?, CustDOB = ?");
		sql.append(", CustCRCPR= ?, CustPassportNo = ?, MobileNumber = ?, CustNationality = ?");
		sql.append(", DedupRule = ?, Override = ?, OverrideUser = ?, Module = ?");
		sql.append(" Where FinID = ? and CustCIF = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				int index = 1;

				CustomerDedup cd = cdList.get(i);

				ps.setString(index++, cd.getCustFName());
				ps.setString(index++, cd.getCustLName());
				ps.setString(index++, cd.getCustShrtName());
				ps.setDate(index++, JdbcUtil.getDate(cd.getCustDOB()));
				ps.setString(index++, cd.getCustCRCPR());
				ps.setString(index++, cd.getCustPassportNo());
				ps.setString(index++, cd.getMobileNumber());
				ps.setString(index++, cd.getCustNationality());
				ps.setString(index++, cd.getDedupRule());
				ps.setBoolean(index++, cd.isOverride());
				ps.setString(index++, cd.getOverrideUser());
				ps.setString(index++, cd.getModule());

				ps.setLong(index++, cd.getFinID());
				ps.setString(index, cd.getCustCIF());
			}

			@Override
			public int getBatchSize() {
				return cdList.size();
			}
		});
	}

	@Override
	public List<CustomerDedup> fetchOverrideCustDedupData(String finReference, String queryCode, String module) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, CustCIF, CustFName, CustLName, CustShrtName");
		sql.append(", CustDOB, CustCRCPR, CustPassportNo, MobileNumber, CustNationality");
		sql.append(", DedupRule , Override , OverrideUser, Module");
		sql.append(" From CustomerDedupDetail");
		sql.append(" Where FinReference = ? and DedupRule like(?) and Module= ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, finReference);
			ps.setString(2, "%" + queryCode + "%");
			ps.setString(3, module);
		}, (rs, i) -> {
			CustomerDedup cd = new CustomerDedup();

			cd.setFinID(rs.getLong("FinID"));
			cd.setFinReference(rs.getString("FinReference"));
			cd.setCustCIF(rs.getString("CustCIF"));
			cd.setCustFName(rs.getString("CustFName"));
			cd.setCustLName(rs.getString("CustLName"));
			cd.setCustShrtName(rs.getString("CustShrtName"));
			cd.setCustDOB(rs.getTimestamp("CustDOB"));
			cd.setCustCRCPR(rs.getString("CustCRCPR"));
			cd.setCustPassportNo(rs.getString("CustPassportNo"));
			cd.setMobileNumber(rs.getString("MobileNumber"));
			cd.setCustNationality(rs.getString("CustNationality"));
			cd.setDedupRule(rs.getString("DedupRule"));
			cd.setOverride(rs.getBoolean("Override"));
			cd.setOverrideUser(rs.getString("OverrideUser"));
			cd.setModule(rs.getString("Module"));

			return cd;
		});
	}

	public List<String> getExtendedField(String subModuleName) {
		if (subModuleName == null) {
			return new ArrayList<>();
		}

		String sql = "Select Count(ModuleName) from ExtendedFieldHeader where ModuleName = ? and SubModuleName = ?";

		logger.debug(Literal.SQL + sql);

		if (this.jdbcOperations.queryForObject(sql, Integer.class, "CUSTOMER", subModuleName) > 0) {
			sql = "Select FieldName From ExtendedFieldDetail Where ModuleId In (Select moduleId From ExtendedFieldHeader where ModuleName = ?  and SubModuleName = ?) and InputElement = ?";

			logger.debug(Literal.SQL + sql);

			return this.jdbcOperations.query(sql, ps -> {
				ps.setString(1, "CUSTOMER");
				ps.setString(2, subModuleName);
				ps.setInt(3, 1);
			}, (rs, rowNum) -> rs.getString(1));
		}

		return new ArrayList<>();
	}

	/**
	 * Fetched the Dedup Fields if Dedup Exist for a Customer
	 *
	 */
	public List<CustomerDedup> fetchCustomerDedupDetails(CustomerDedup dedup, String sqlQuery) {
		StringBuilder sql = getSelectQuery(dedup, false);

		if (!StringUtils.isBlank(sqlQuery)) {
			sql.append(StringUtils.trimToEmpty(sqlQuery));
			sql.append(" and ");
		} else {
			sql.append(" Where");
		}
		sql.append(" CustId != :CustId");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedup);
		BeanPropertyRowMapper<CustomerDedup> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerDedup.class);

		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public void moveData(String finReference, String suffix) {
		/* FIXME : change to FinID Pre-approved(_PA) tables need to remove */
		if (StringUtils.isBlank(suffix)) {
			return;
		}

		try {
			MapSqlParameterSource map = new MapSqlParameterSource();
			map.addValue("FinReference", finReference);

			StringBuilder selectSql = new StringBuilder();
			selectSql.append(" SELECT * FROM CustomerDedupDetail");
			selectSql.append(" WHERE FinReference = :FinReference ");

			RowMapper<CustomerDedup> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerDedup.class);
			List<CustomerDedup> list = this.jdbcTemplate.query(selectSql.toString(), map, typeRowMapper);

			if (list != null && !list.isEmpty()) {
				saveList(list, suffix);
			}

		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public StringBuilder getSelectQuery(CustomerDedup dedup, boolean blackList) {
		StringBuilder sql = new StringBuilder();

		String table = "CUSTOMER_" + dedup.getCustCtgCode() + "_ED";

		List<String> extFields = getExtendedField(dedup.getCustCtgCode());

		sql.append("Select ");
		if (blackList) {
			sql.append(PennantConstants.CUST_DEDUP_LIST_FIELDS);
		} else {
			sql.append(" * ");
		}

		sql.append(" From (");
		sql.append(getQuery(extFields, table, "_Temp"));
		sql.append(" Union All ");
		sql.append(getQuery(extFields, table, ""));
		sql.append(" Where not exists (Select 1 From Customers_Temp Where CustID = t1.CustID )) T ");
		return sql;
	}

	private String getQuery(List<String> extFields, String table, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" Select Distinct t1.CustId, t1.CustCIF, t1.CustFName, t1.CustLName, t1.CustCRCPR");
		sql.append(", cdpp.CustDocTitle CustPassportNo, t1.CustShrtName, t1.CustDOB, t1.CustNationality");
		sql.append(", coalesce (case when t1.PhoneNumber = ' ' then null else t1.PhoneNumber end");
		sql.append(", t7.PhoneNumber) MobileNumber, t1.CustCtgCode, t1.CustDftBranch, t1.CustTypeCode");
		sql.append(", t1.CustSector, t1.CustSubSector, t1.SubCategory, t1.CasteID, t1.ReligionID, t3.CasteCode");
		sql.append(", t3.CasteDesc, t4.ReligionCode, t4.ReligionDesc, t2.CustTypeCtg LovDescCustCtgType");
		sql.append(", cda.CustDocTitle AadharNumber, cdp.CustDocTitle PanNumber, t6.CustEmail, t1.CustMotherMaiden");
		sql.append(", cdv.CustDocTitle VoterID, cdd.CustDocTitle DrivingLicenceNo, t1.CustShrtName CustCompName");
		sql.append(", cad.CustAddrStreet Address1 ");

		StringBuilder extendedFields = filterSqlColumns(extFields, sql);
		sql.append(extendedFields.toString());

		sql.append(" From Customers");
		sql.append(type);
		sql.append(" t1 Left Join RMTCustTypes t2 on t1.CustTypeCode = t2.CustTypeCode");
		sql.append(" Left Join Caste t3 on t1.CasteID = t3.CasteID");
		sql.append(" Left Join Religion t4 on t1.ReligionID = t4.ReligionID");
		sql.append(" Left Join CustomerDocuments cda on cda.CustID = t1.CustID and (cda.custdoccategory = ANY");
		sql.append(getMasterDefQuery("AADHAAR"));
		sql.append(" LEFT JOIN CustomerDocuments cdp ON cdp.custid = t1.custid and (cdp.custdoccategory = ANY");
		sql.append(getMasterDefQuery("PAN"));
		sql.append(" LEFT JOIN CustomerDocuments cdpp ON cdpp.custid = t1.custid and (cdpp.custdoccategory = ANY");
		sql.append(getMasterDefQuery("PASSPORT"));
		sql.append(" LEFT JOIN CustomerDocuments cdv ON cdv.custid = t1.custid and (cdv.custdoccategory = ANY");
		sql.append(getMasterDefQuery("VOTERID"));
		sql.append(" LEFT JOIN CustomerDocuments cdd ON cdd.custid = t1.custid and (cdd.custdoccategory = ANY");
		sql.append(getMasterDefQuery("DLNO"));
		sql.append(" LEFT JOIN  CustomerEmails t6 ON t1.custid = t6.custid AND t6.custemailpriority = 5");
		sql.append(" LEFT JOIN  CustomerPhonenumbers t7 ON t1.custid = t7.phonecustid");
		sql.append(" LEFT JOIN CustomerAddresses cad ON t1.custid= cad.custid AND cad.custaddrpriority = 5");

		if (extendedFields.length() > 0) {
			sql.append(" INNER JOIN ");
			sql.append(table);
			sql.append(type);
			sql.append(" t8 ON t1.custCif = t8.reference");
		}

		return sql.toString();
	}

	private StringBuilder filterSqlColumns(List<String> extendedFields, StringBuilder sql) {
		List<String> extFields = new ArrayList<>();

		if (!extendedFields.isEmpty()) {
			for (String column : extendedFields) {
				if (!sql.toString().toUpperCase().contains(column)) {
					extFields.add(column);
				}
			}
		}

		StringBuilder extendedField = new StringBuilder();
		extFields.forEach(field -> extendedField.append(", t8.").append(field));

		return extendedField;
	}

	private String getMasterDefQuery(String type) {
		StringBuilder sql = new StringBuilder();
		sql.append(" (Select master_def.key_code FROM master_def");
		sql.append(" Where master_def.master_type = 'DOC_TYPE' and master_def.key_type = '");
		sql.append(type);
		sql.append("'))");

		return sql.toString();
	}
}
