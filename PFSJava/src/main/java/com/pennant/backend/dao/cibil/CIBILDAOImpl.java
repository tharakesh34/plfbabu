package com.pennant.backend.dao.cibil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.dataengine.model.DataEngineLog;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.model.cibil.CibilFileInfo;
import com.pennanttech.pff.model.cibil.CibilMemberDetail;

public class CIBILDAOImpl extends BasicDao<Object> implements CIBILDAO {
	private static Logger logger = LogManager.getLogger(CIBILDAOImpl.class);

	@Override
	public CustomerDetails getCustomerDetails(long customerId) {
		logger.trace(Literal.ENTERING);
		CustomerDetails customer = new CustomerDetails();

		try {
			customer.setCustomer(getCustomer(customerId, PennantConstants.PFF_CUSTCTG_INDIV));
			customer.setCustomerDocumentsList(getCustomerDocuments(customerId, PennantConstants.PFF_CUSTCTG_INDIV));
			customer.setCustomerPhoneNumList(getCustomerPhoneNumbers(customerId, PennantConstants.PFF_CUSTCTG_INDIV));
			customer.setCustomerEMailList(getCustomerEmails(customerId));
			customer.setAddressList(getCustomerAddres(customerId, PennantConstants.PFF_CUSTCTG_INDIV));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			customer = null;
		}

		return customer;
	}

	@Override
	public Customer getCustomer(long customerId, String bureauType) {
		logger.trace(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();

		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(bureauType)) {
			sql.append("select custshrtname");
			sql.append(", custsalutationcode");
			sql.append(", custfname");
			sql.append(", custmname");
			sql.append(", custlname");
			sql.append(", custdob");
			sql.append(", custgendercode");
			sql.append(" from customers where custid = :custid");
		} else {
			sql.append(" select distinct c.custid");
			sql.append(", c.custdftbranch");
			sql.append(", c.custfname");
			sql.append(", c.custmname");
			sql.append(", c.custlname");
			sql.append(", c.custshrtname");
			sql.append(", c.custtradelicencenum");
			sql.append(", c.custdob");
			sql.append(", custcob");
			sql.append(", custgendercode");
			sql.append(", c.custcrcpr");
			sql.append(", c.custsalutationcode");
			sql.append(", lcm.code legalconstitution");
			sql.append(", bcm.code businesscategory");
			sql.append(", cc.custctgtype lovdesccustctgtype");
			sql.append(" from customers c ");
			sql.append(" inner join bmtcustcategories cc on cc.custctgcode = c.custctgcode");
			sql.append(" left join cibil_legal_const_mapping lcm on lcm.cust_type_code = c.custtypecode");
			sql.append(" and lcm.segment_type =:CORP");
			sql.append(" left join cibil_legal_constitution lc on lc.code = lcm.code");
			sql.append(" and lc.segment_type=lcm.segment_type");
			sql.append(" left join cibil_business_catgry_mapping bcm on bcm.category = c.custctgcode");
			sql.append(" and bcm.segment_type =:CORP ");
			sql.append(" left join cibil_business_category bc on bc.code = bcm.code");
			sql.append(" and bc.segment_type=bcm.segment_type");
			sql.append(" left join cibil_industry_type_mapping bit on bit.industry = c.custindustry");
			sql.append(" where c.custid = :custid");
		}

		paramMap.addValue("custid", customerId);
		paramMap.addValue("CORP", "CORP");

		return this.jdbcTemplate.queryForObject(sql.toString(), paramMap, new RowMapper<Customer>() {
			@Override
			public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
				Customer customer = new Customer();

				if (PennantConstants.PFF_CUSTCTG_INDIV.equals(bureauType)) {
					customer.setCustShrtName(rs.getString("custshrtname"));
					customer.setCustSalutationCode(rs.getString("custsalutationcode"));
					customer.setCustFName(rs.getString("custfname"));
					customer.setCustMName(rs.getString("custmname"));
					customer.setCustLName(rs.getString("custlname"));
					customer.setCustDOB(rs.getDate("custdob"));
					customer.setCustGenderCode(rs.getString("custgendercode"));
				} else {
					customer.setCustShrtName(rs.getString("custshrtname"));
					customer.setCustDftBranch(rs.getString("custdftbranch"));
					customer.setCustFName(rs.getString("custfname"));
					customer.setCustMName(rs.getString("custmname"));
					customer.setCustLName(rs.getString("custlname"));
					customer.setCustShrtName(rs.getString("custshrtname"));
					customer.setCustTradeLicenceNum(rs.getString("custtradelicencenum"));
					customer.setCustDOB(rs.getDate("custdob"));
					customer.setCustCOB(rs.getString("custcob"));
					customer.setCustGenderCode(rs.getString("custgendercode"));
					customer.setCustCRCPR(rs.getString("custcrcpr"));
					customer.setCustSalutationCode(rs.getString("custsalutationcode"));
					customer.setLegalconstitution(rs.getString("legalconstitution"));
					customer.setBusinesscategory(rs.getString("businesscategory"));
					customer.setLovDescCustCtgType(rs.getString("lovdesccustctgtype"));
				}

				return customer;
			}
		});
	}

	@Override
	public List<CustomerDocument> getCustomerDocuments(long customerId, String bureauType) {
		logger.trace(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(bureauType)) {
			sql.append("select dt.code custdoccategory");
			sql.append(", custdoctitle");
			sql.append(", custdocissuedon");
			sql.append(", custdocexpdate");
			sql.append(" from customerdocuments doc");
			sql.append(" inner join cibil_document_types_mapping dm on dm.doctypecode = doc.custdoccategory");
			sql.append(" inner join cibil_document_types dt on dt.code = dm.code");
		} else {
			sql.append("select custdoccategory");
			sql.append(", custdoctitle");
			sql.append(", custdocissuedon");
			sql.append(", custdocexpdate");
			sql.append(" from customerdocuments doc");
		}
		sql.append(" where custid = :custid");

		paramMap.addValue("custid", customerId);

		return this.jdbcTemplate.query(sql.toString(), paramMap, new RowMapper<CustomerDocument>() {
			@Override
			public CustomerDocument mapRow(ResultSet rs, int rowNum) throws SQLException {
				CustomerDocument customerDocument = new CustomerDocument();
				if (PennantConstants.PFF_CUSTCTG_INDIV.equals(bureauType)) {
					customerDocument.setCustDocCategory(rs.getString("custdoccategory"));
					customerDocument.setCustDocTitle(rs.getString("custdoctitle"));
					customerDocument.setCustDocIssuedOn(rs.getDate("custdocissuedon"));
					customerDocument.setCustDocExpDate(rs.getDate("custdocexpdate"));
				} else {
					customerDocument.setCustDocCategory(rs.getString("custdoccategory"));
					customerDocument.setCustDocTitle(rs.getString("custdoctitle"));
					customerDocument.setCustDocIssuedOn(rs.getDate("custdocissuedon"));
					customerDocument.setCustDocExpDate(rs.getDate("custdocexpdate"));
				}

				return customerDocument;
			}
		});

	}

	@Override
	public List<CustomerPhoneNumber> getCustomerPhoneNumbers(long customerId, String bureauType) {
		logger.trace(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(bureauType)) {
			sql.append("select cpt.code phonetypecode");
			sql.append(", cp.phonenumber");
			sql.append(", phoneareacode");
			sql.append(", phonetypepriority");
			sql.append(" from customerphonenumbers cp");
			sql.append(" left join cibil_phone_types_mapping pm on pm.phonetypecode=cp.phonetypecode");
			sql.append(" left join cibil_phone_types cpt on cpt.code = pm.code");
		} else {
			sql.append("select phonetypecode");
			sql.append(", cp.phonenumber");
			sql.append(", phoneareacode");
			sql.append(", phonetypepriority");
			sql.append(" from customerphonenumbers cp");
		}
		sql.append(" where phonecustid = :phonecustid");
		paramMap.addValue("phonecustid", customerId);
		return this.jdbcTemplate.query(sql.toString(), paramMap, new RowMapper<CustomerPhoneNumber>() {
			@Override
			public CustomerPhoneNumber mapRow(ResultSet rs, int rowNum) throws SQLException {
				CustomerPhoneNumber custPhone = new CustomerPhoneNumber();
				if (PennantConstants.PFF_CUSTCTG_INDIV.equals(bureauType)) {
					custPhone.setPhoneTypeCode(rs.getString("phonetypecode"));
					custPhone.setPhoneNumber(rs.getString("phonenumber"));
					custPhone.setPhoneAreaCode(rs.getString("phoneareacode"));
					custPhone.setPhoneTypePriority(rs.getInt("phonetypepriority"));
				} else {
					custPhone.setPhoneTypeCode(rs.getString("phonetypecode"));
					custPhone.setPhoneNumber(rs.getString("phonenumber"));
					custPhone.setPhoneAreaCode(rs.getString("phoneareacode"));
					custPhone.setPhoneTypePriority(rs.getInt("phonetypepriority"));
				}
				return custPhone;
			}
		});

	}

	@Override
	public List<CustomerEMail> getCustomerEmails(long customerId) {
		logger.trace(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append("select CustEMail from CUSTOMEREMAILS");
		sql.append(" where CUSTID = :CUSTID");
		paramMap.addValue("CUSTID", customerId);
		return this.jdbcTemplate.query(sql.toString(), paramMap, new RowMapper<CustomerEMail>() {
			@Override
			public CustomerEMail mapRow(ResultSet rs, int rowNum) throws SQLException {
				CustomerEMail custEmail = new CustomerEMail();
				custEmail.setCustEMail(rs.getString("CustEMail"));
				return custEmail;
			}

		});

	}

	@Override
	public List<CustomerAddres> getCustomerAddres(long customerId, String segmentType) {
		logger.trace(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append("select cat.code CustAddrType");
		sql.append(", CustAddrHNbr");
		sql.append(", CustFlatNbr");
		sql.append(", CustAddrStreet");
		sql.append(", CustDistrict");
		sql.append(", pvc.pccityname CustAddrcity");
		sql.append(", CustAddrLine1");
		sql.append(", CustAddrLine2");
		sql.append(", sm.code CustAddrProvince");
		sql.append(", sm.description LovDescCustAddrProvinceName");
		sql.append(", CustAddrZIP");
		sql.append(", CustAddrCountry");
		sql.append(" from CustomerAddresses ca");
		sql.append(" left join cibil_address_types_mapping am on am.address_type = ca.custaddrtype");
		sql.append(" and am.segment_type = :segment_type");
		sql.append(" left join cibil_address_types cat on cat.code = am.Code and cat.segment_type = am.segment_type");
		sql.append(" left join cibil_states_mapping sm on sm.CPPROVINCE = ca.CUSTADDRPROVINCE");
		sql.append(" and sm.segment_type  = am.segment_type");
		sql.append(" left join RMTProvinceVsCity pvc on pvc.PCCITY=ca.CustAddrcity");
		sql.append(" where CUSTID = :CUSTID");

		if (!PennantConstants.PFF_CUSTCTG_INDIV.equals(segmentType)) {
			sql.append(" and custAddrPriority = :custAddrPriority");
		}

		paramMap.addValue("CUSTID", customerId);
		paramMap.addValue("segment_type", segmentType);
		paramMap.addValue("custAddrPriority", PennantConstants.KYC_PRIORITY_VERY_HIGH, Types.INTEGER);

		return this.jdbcTemplate.query(sql.toString(), paramMap, new RowMapper<CustomerAddres>() {
			@Override
			public CustomerAddres mapRow(ResultSet rs, int rowNum) throws SQLException {
				CustomerAddres customerAddres = new CustomerAddres();
				customerAddres.setCustAddrType(rs.getString("CustAddrType"));
				customerAddres.setCustAddrHNbr(rs.getString("CustAddrHNbr"));
				customerAddres.setCustFlatNbr(rs.getString("CustFlatNbr"));
				customerAddres.setCustAddrStreet(rs.getString("CustAddrStreet"));
				customerAddres.setCustDistrict(rs.getString("CustDistrict"));
				customerAddres.setCustAddrCity(rs.getString("CustAddrcity"));
				customerAddres.setCustAddrLine1(rs.getString("CustAddrLine1"));
				customerAddres.setCustAddrLine2(rs.getString("CustAddrLine2"));
				customerAddres.setCustAddrProvince(rs.getString("CustAddrProvince"));
				customerAddres.setCustAddrZIP(rs.getString("CustAddrZIP"));
				customerAddres.setCustAddrCountry(rs.getString("CustAddrCountry"));

				return customerAddres;
			}
		});
	}

	@Override
	public FinanceEnquiry getFinanceSummary(long customerId, String finReference, String segmentType) {
		logger.trace(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append("select * from cibil_customer_loans_view cs");
		sql.append(" where cs.finreference = :finreference");
		sql.append(" and custid = :custid");
		sql.append(" and cs.segment_type = :segment_type");

		paramMap.addValue("finreference", finReference);
		paramMap.addValue("custid", customerId);
		paramMap.addValue("segment_type", segmentType);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), paramMap, new RowMapper<FinanceEnquiry>() {
				@Override
				public FinanceEnquiry mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceEnquiry finEnqy = new FinanceEnquiry();
					finEnqy.setFinType(rs.getString("FINTYPE"));
					finEnqy.setFinReference(rs.getString("FINREFERENCE"));
					finEnqy.setFinStartDate(rs.getDate("FINSTARTDATE"));
					finEnqy.setFinApprovedDate(rs.getDate("FINAPPROVEDDATE"));
					finEnqy.setLatestRpyDate(rs.getDate("LATESTRPYDATE"));
					finEnqy.setRepayFrq(rs.getString("REPAYFRQ"));
					finEnqy.setFinAssetValue(rs.getBigDecimal("FINASSETVALUE"));
					finEnqy.setFutureSchedulePrin(rs.getBigDecimal("FUTURE_SCHEDULE_PRIN"));
					finEnqy.setInstalmentDue(rs.getBigDecimal("INSTALMENT_DUE"));
					finEnqy.setInstalmentPaid(rs.getBigDecimal("INSTALMENT_PAID"));
					finEnqy.setBounceDue(rs.getBigDecimal("BOUNCE_DUE"));
					finEnqy.setBouncePaid(rs.getBigDecimal("BOUNCE_PAID"));
					finEnqy.setLatePaymentPenaltyDue(rs.getBigDecimal("LATE_PAYMENT_PENALTY_DUE"));
					finEnqy.setLatePaymentPenaltyPaid(rs.getBigDecimal("LATE_PAYMENT_PENALTY_PAID"));
					finEnqy.setTotalPriSchd(rs.getBigDecimal("TOTAL_PRI_SCHD"));
					finEnqy.setTotalPriPaid(rs.getBigDecimal("TOTAL_PRI_PAID"));
					finEnqy.setTotalPftSchd(rs.getBigDecimal("TOTAL_PFT_SCHD"));
					finEnqy.setTotalPftPaid(rs.getBigDecimal("TOTAL_PFT_PAID"));
					finEnqy.setExcessAmount(rs.getBigDecimal("EXCESS_AMOUNT"));
					finEnqy.setExcessAmtPaid(rs.getBigDecimal("EXCESS_AMT_PAID"));
					finEnqy.setCurODDays(rs.getInt("CURODDAYS"));
					finEnqy.setClosingStatus(rs.getString("CLOSINGSTATUS"));
					finEnqy.setOwnership(rs.getString("OWNERSHIP"));
					finEnqy.setNumberOfTerms(rs.getInt("NUMBEROFTERMS"));
					finEnqy.setSvAmount(rs.getBigDecimal("CUSTINCOME"));

					return finEnqy;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Loan details not availabe for the specified Custome Id {}, FinRegerence {}, segmentType {}",
					customerId, finReference, segmentType);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.trace(Literal.LEAVING);
		return null;
	}

	@Override
	public List<FinanceEnquiry> getFinanceSummary(long customerId, String segmentType) {
		logger.trace(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select * from cibil_customer_loans_view cs");
		sql.append(" inner join cibil_customer_extract cce on cce.finreference = cs.finreference");
		sql.append(" and cs.custid = cce.custid");
		sql.append(" where cs.custid = :custid");
		sql.append(" and cs.segment_type = :segment_type");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("custid", customerId);
		paramMap.addValue("segment_type", segmentType);

		try {
			return this.jdbcTemplate.query(sql.toString(), paramMap, new RowMapper<FinanceEnquiry>() {
				@Override
				public FinanceEnquiry mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinanceEnquiry finEnqy = new FinanceEnquiry();
					finEnqy.setFinType(rs.getString("FINTYPE"));
					finEnqy.setFinReference(rs.getString("FINREFERENCE"));
					finEnqy.setFinStartDate(rs.getDate("FINSTARTDATE"));
					finEnqy.setFinApprovedDate(rs.getDate("FINAPPROVEDDATE"));
					finEnqy.setLatestRpyDate(rs.getDate("LATESTRPYDATE"));
					finEnqy.setRepayFrq(rs.getString("REPAYFRQ"));
					finEnqy.setFinAssetValue(rs.getBigDecimal("FINASSETVALUE"));
					finEnqy.setFutureSchedulePrin(rs.getBigDecimal("FUTURE_SCHEDULE_PRIN"));
					finEnqy.setInstalmentDue(rs.getBigDecimal("INSTALMENT_DUE"));
					finEnqy.setInstalmentPaid(rs.getBigDecimal("INSTALMENT_PAID"));
					finEnqy.setBounceDue(rs.getBigDecimal("BOUNCE_DUE"));
					finEnqy.setBouncePaid(rs.getBigDecimal("BOUNCE_PAID"));
					finEnqy.setLatePaymentPenaltyDue(rs.getBigDecimal("LATE_PAYMENT_PENALTY_DUE"));
					finEnqy.setLatePaymentPenaltyPaid(rs.getBigDecimal("LATE_PAYMENT_PENALTY_PAID"));
					finEnqy.setTotalPriSchd(rs.getBigDecimal("TOTAL_PRI_SCHD"));
					finEnqy.setTotalPriPaid(rs.getBigDecimal("TOTAL_PRI_PAID"));
					finEnqy.setTotalPftSchd(rs.getBigDecimal("TOTAL_PFT_SCHD"));
					finEnqy.setTotalPftPaid(rs.getBigDecimal("TOTAL_PFT_PAID"));
					finEnqy.setExcessAmount(rs.getBigDecimal("EXCESS_AMOUNT"));
					finEnqy.setExcessAmtPaid(rs.getBigDecimal("EXCESS_AMT_PAID"));
					finEnqy.setCurODDays(rs.getInt("CURODDAYS"));
					finEnqy.setClosingStatus(rs.getString("CLOSINGSTATUS"));
					finEnqy.setOwnership(rs.getString("OWNERSHIP"));
					finEnqy.setNumberOfTerms(rs.getInt("NUMBEROFTERMS"));
					finEnqy.setSvAmount(rs.getBigDecimal("CUSTINCOME"));
					finEnqy.setMaturityDate(rs.getDate("MATURITYDATE"));
					return finEnqy;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.trace(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public void logFileInfoException(long id, String finReference, String reason) {
		logger.trace(Literal.ENTERING);

		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Insert Into CIBIL_FILE_INFO_LOG");
		sql.append(" (ID");
		sql.append(", FINREFERENCE");
		sql.append(", REASON");
		sql.append(", STATUS)");
		sql.append(" Values(:ID");
		sql.append(", :FINREFERENCE");
		sql.append(", :REASON");
		sql.append(", :STATUS)");

		paramMap.addValue("ID", id);
		paramMap.addValue("FINREFERENCE", finReference);
		paramMap.addValue("REASON", reason);
		paramMap.addValue("STATUS", "F");

		try {
			this.jdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.trace(Literal.LEAVING);

	}

	@Override
	public DataEngineStatus getLatestExecution() {
		logger.trace(Literal.ENTERING);
		DataEngineStatus dataStatus = new DataEngineStatus();

		StringBuilder sql = null;

		sql = new StringBuilder("Select ID");
		sql.append(", TOTAL_RECORDS");
		sql.append(", PROCESSED_RECORDS");
		sql.append(", SUCCESS_RECORDS");
		sql.append(", FAILED_RECORDS,");
		sql.append(" REMARKS");
		sql.append(", START_TIME");
		sql.append(", END_TIME");
		sql.append(" from CIBIL_FILE_INFO");
		sql.append(" where Id = (Select MAX(Id) from CIBIL_FILE_INFO)");

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), new MapSqlParameterSource(),
					new RowMapper<DataEngineStatus>() {
						@Override
						public DataEngineStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
							dataStatus.setId(rs.getInt("ID"));
							dataStatus.setTotalRecords(rs.getInt("TOTAL_RECORDS"));
							dataStatus.setProcessedRecords(rs.getInt("PROCESSED_RECORDS"));
							dataStatus.setSuccessRecords(rs.getInt("SUCCESS_RECORDS"));
							dataStatus.setFailedRecords(rs.getInt("FAILED_RECORDS"));
							dataStatus.setRemarks(rs.getString("REMARKS"));
							dataStatus.setStartTime(rs.getDate("START_TIME"));
							dataStatus.setEndTime(rs.getDate("END_TIME"));
							if (dataStatus != null) {
								List<DataEngineLog> list = getExceptions(dataStatus.getId());
								if (list != null && !list.isEmpty()) {
									dataStatus.setDataEngineLogList(list);
								}
							}
							return dataStatus;
						}
					});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.trace(Literal.LEAVING);
		return dataStatus;
	}

	public List<DataEngineLog> getExceptions(long Id) {
		logger.trace(Literal.ENTERING);
		MapSqlParameterSource parameterMap = null;
		StringBuilder sql = null;
		sql = new StringBuilder("Select * from CIBIL_FILE_INFO_LOG");
		sql.append(" where ID = :ID");
		parameterMap = new MapSqlParameterSource();
		parameterMap.addValue("ID", Id);
		try {
			return this.jdbcTemplate.query(sql.toString(), parameterMap, new RowMapper<DataEngineLog>() {
				@Override
				public DataEngineLog mapRow(ResultSet rs, int rowNum) throws SQLException {
					DataEngineLog dataEngLog = new DataEngineLog();
					dataEngLog.setKeyId(rs.getString("FINREFERENCE"));
					dataEngLog.setReason(rs.getString("REASON"));
					dataEngLog.setStatus(rs.getString("STATUS"));
					return dataEngLog;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.trace(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public void deleteDetails() {
		logger.debug(Literal.ENTERING);
		try {
			jdbcTemplate.update("TRUNCATE TABLE CIBIL_CUSTOMER_EXTRACT", new MapSqlParameterSource());
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public EventProperties getEventProperties(String configName, String eventType) {
		logger.trace(Literal.ENTERING);
		EventProperties evntProrts = new EventProperties();
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		StringBuilder sql = null;
		sql = new StringBuilder("SELECT DEP.* FROM DATA_ENGINE_EVENT_PROPERTIES DEP");
		sql.append(" INNER JOIN DATA_ENGINE_CONFIG DC ON DC.ID = DEP.CONFIG_ID");
		sql.append(" Where DC.NAME = :NAME");
		sql.append(" AND DEP.STORAGE_TYPE = :STORAGE_TYPE");
		parameterSource.addValue("NAME", configName);
		parameterSource.addValue("STORAGE_TYPE", eventType);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, new RowMapper<EventProperties>() {
				@Override
				public EventProperties mapRow(ResultSet rs, int rowNum) throws SQLException {
					evntProrts.setStorageType(rs.getString("STORAGE_TYPE"));
					evntProrts.setRegionName(rs.getString("REGION_NAME"));
					evntProrts.setBucketName(rs.getString("BUCKET_NAME"));
					evntProrts.setAccessKey(rs.getString("ACCESS_KEY"));
					evntProrts.setSecretKey(rs.getString("SECRET_KEY"));
					evntProrts.setPrefix(rs.getString("PREFIX"));
					evntProrts.setSseAlgorithm(rs.getString("SSE_ALGORITHM"));
					evntProrts.setHostName(rs.getString("HOST_NAME"));
					evntProrts.setPort(rs.getString("PORT"));
					evntProrts.setPrivateKey(rs.getString("PRIVATE_KEY"));

					return evntProrts;

				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
			logger.warn("Configuration details not available for " + configName);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.trace(Literal.LEAVING);
		return null;
	}

	@Override
	public CibilMemberDetail getMemberDetails(String bureauType) {
		logger.trace(Literal.ENTERING);
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		StringBuilder sql = null;
		sql = new StringBuilder();
		sql.append("select * from cibil_member_details");
		sql.append(" where segment_Type =:segment_Type");

		parameterSource.addValue("segment_Type", bureauType);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), parameterSource,
					new RowMapper<CibilMemberDetail>() {
						@Override
						public CibilMemberDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
							CibilMemberDetail cibilMemberDetail = new CibilMemberDetail();
							cibilMemberDetail.setSegmentType(rs.getString("SEGMENT_TYPE"));
							cibilMemberDetail.setMemberCode(rs.getString("MEMBER_CODE"));
							cibilMemberDetail.setMemberId(rs.getString("MEMBER_ID"));
							cibilMemberDetail.setPreviousMemberId(rs.getString("PREVIOUS_MEMBER_ID"));
							cibilMemberDetail.setMemberShortName(rs.getString("MEMBER_SHORT_NAME"));
							cibilMemberDetail.setMemberPassword(rs.getString("MEMBER_PASSWORD"));
							cibilMemberDetail.setFilePath(rs.getString("FILE_PATH"));
							cibilMemberDetail.setFileFormate(rs.getString("FILE_FORMATE"));
							return cibilMemberDetail;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.trace(Literal.LEAVING);
		return null;
	}

	@Override
	public void logFileInfo(CibilFileInfo fileInfo) {
		logger.trace(Literal.ENTERING);

		final KeyHolder keyHolder = new GeneratedKeyHolder();
		StringBuilder sql = new StringBuilder("insert into cibil_file_info");
		sql.append(" (File_Name");
		sql.append(", Member_Id");
		sql.append(", Member_Short_Name");
		sql.append(", Member_Password");
		sql.append(", CreatedOn");
		sql.append(", Status");
		sql.append(", File_Location");
		sql.append(", Start_Time");
		sql.append(", Segment_Type)");
		sql.append(" Values");
		sql.append(" (:File_Name");
		sql.append(", :Member_Id");
		sql.append(", :Member_Short_Name");
		sql.append(", :Member_Password");
		sql.append(", :CreatedOn");
		sql.append(", :Status");
		sql.append(", :File_Location");
		sql.append(", :Start_Time");
		sql.append(", :Segment_Type)");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		CibilMemberDetail memberDetail = fileInfo.getCibilMemberDetail();

		paramMap.addValue("Member_Id", memberDetail.getMemberId());
		paramMap.addValue("File_Name", fileInfo.getFileName());

		paramMap.addValue("Member_Short_Name", memberDetail.getMemberShortName());
		paramMap.addValue("Member_Password", memberDetail.getMemberPassword());
		paramMap.addValue("CreatedOn", SysParamUtil.getAppDate());
		paramMap.addValue("Status", "I");
		paramMap.addValue("File_Location", memberDetail.getFilePath());
		paramMap.addValue("Start_Time", DateUtil.getSysDate());
		paramMap.addValue("Segment_Type", memberDetail.getSegmentType());
		try {
			this.jdbcTemplate.update(sql.toString(), paramMap, keyHolder, new String[] { "id" });
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		fileInfo.setId(keyHolder.getKey().longValue());
		logger.trace(Literal.LEAVING);
	}

	@Override
	public long extractCustomers(String segmentType) throws Exception {
		logger.trace(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO CIBIL_CUSTOMER_EXTRACT");
		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(segmentType)) {
			sql.append(" SELECT CUSTID");
			sql.append(", FINREFERENCE");
			sql.append(", OWNERSHIP");
			sql.append(", LATESTRPYDATE");
			sql.append(", :CUSTTYPECTG");
			sql.append(" FROM CIBIL_CUSTOMER_EXTARCT_VIEW");
			sql.append(" WHERE LATESTRPYDATE >= :LATESTRPYDATE ");
		} else {
			sql.append(" SELECT C.CUSTID");
			sql.append(", FM.FINREFERENCE");
			sql.append(", 0, LATESTRPYDATE");
			sql.append(", :CUSTTYPECTG");
			sql.append(" FROM FINANCEMAIN FM");
			sql.append(" INNER JOIN FINPFTDETAILS FP ON FP.FINREFERENCE = FM.FINREFERENCE");
			sql.append(" INNER JOIN CUSTOMERS C ON C.CUSTID = FM.CUSTID");
			sql.append(" INNER JOIN RMTCUSTTYPES CT ON CT.CUSTTYPECODE = C.CUSTTYPECODE AND CT.CUSTTYPECTG <> :INDIV");
			sql.append(" WHERE LATESTRPYDATE >= :LATESTRPYDATE ");
		}
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("LATESTRPYDATE", DateUtil.addMonths(SysParamUtil.getAppDate(), -36));
		paramMap.addValue("CUSTTYPECTG", segmentType);
		paramMap.addValue("INDIV", PennantConstants.PFF_CUSTCTG_INDIV);
		paramMap.addValue("LATESTRPYDATE_T", LocalDateTime.MIN);
		try {
			return jdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			throw new Exception(String.format("Unable Extarct %s CIBIL Data", segmentType));
		}

	}

	@Override
	public void updateFileStatus(CibilFileInfo fileInfo) {
		logger.trace(Literal.ENTERING);

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder("update Cibil_File_Info");
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
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.trace(Literal.LEAVING);
	}

	@Override
	public List<FinODDetails> getFinODDetails(String finReference, String finCCY) {
		logger.trace(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select FinODSchdDate");
		sql.append(", UDF_CONVERTCURRENCY(FinCurODAmt, :FinCCY, :INR) FinCurODAmt");
		sql.append(", FinCurODDays");
		sql.append(" From FinODDetails od");
		sql.append(" Where FinReference =:FinReference");
		sql.append(" and FinCurODAmt >:FinCurODAmt");
		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("FinReference", finReference);
		parameterSource.addValue("FinCurODAmt", 0);
		parameterSource.addValue("FinCCY", "INR");
		parameterSource.addValue("INR", "INR");
		try {
			return this.jdbcTemplate.query(sql.toString(), parameterSource, new RowMapper<FinODDetails>() {
				@Override
				public FinODDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
					FinODDetails finODDtl = new FinODDetails();
					finODDtl.setFinODSchdDate(rs.getDate("FinODSchdDate"));
					finODDtl.setFinCurODAmt(rs.getBigDecimal("FinCurODAmt"));
					finODDtl.setFinCurODDays(rs.getInt("FinCurODDays"));
					return finODDtl;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.trace(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public List<CollateralSetup> getCollateralDetails(String finReference, String segmentType) {
		logger.trace(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		sql.append("Select cs.bankvaluation");
		sql.append(", ccy.ccynumber collateralccy");
		sql.append(", collateral_type collateraltype");
		sql.append(" From collateralassignment ca");
		sql.append(" inner join cibil_customer_extract cce on cce.finreference = ca.reference");
		sql.append(" inner join collateralsetup cs on cs.collateralref = ca.collateralref");
		sql.append(" inner join rmtcurrencies ccy on ccy.ccycode = cs.collateralccy");
		sql.append(" inner join collateralstructure ce on ce.collateraltype=cs.collateraltype");
		sql.append(" left join cibil_collateral_types_mapping cctm on cctm.collateral_type = cs.collateraltype");
		sql.append(" and cctm.segment_type= :segment_type");
		sql.append(" left join cibil_collateral_types cct on cct.code = cctm.code and cct.segment_type=:segment_type");
		sql.append(" where cce.finreference =:FinReference");
		sql.append(" and cce.segment_type =:segment_type");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("FinReference", finReference);
		parameterSource.addValue("segment_type", segmentType);
		try {
			return this.jdbcTemplate.query(sql.toString(), parameterSource, new RowMapper<CollateralSetup>() {
				@Override
				public CollateralSetup mapRow(ResultSet rs, int rowNum) throws SQLException {
					CollateralSetup colltflStp = new CollateralSetup();
					colltflStp.setBankValuation(rs.getBigDecimal("bankvaluation"));
					colltflStp.setCollateralCcy(rs.getString("collateralccy"));
					colltflStp.setCollateralType(rs.getString("collateraltype"));
					return colltflStp;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.trace(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public List<ChequeDetail> getChequeBounceStatus(String finReference) {
		logger.trace(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();

		sql.append("select bouncedate chequeBounceDate");
		sql.append(", receiptamount amount");
		sql.append(", rd.chequeacno chequeNumber");
		sql.append(", null chequeBounceDate");
		sql.append(", br.reason chequeBounceReason");
		sql.append(" from finreceiptheader rh");
		sql.append(" inner join finreceiptdetail rd on rd.receiptid = rh.receiptid");
		sql.append(" inner join manualadvise ma on ma.receiptid = rh.receiptid");
		sql.append(" inner join bouncereasons br on br.bounceid = ma.bounceid");
		sql.append(" where receiptmode=:ReceiptMode");
		sql.append(" and receiptmodestatus= :ReceiptModeStatus");
		sql.append(" and br.bouncecode in (:bouncecode)");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("FinReference", finReference);
		parameterSource.addValue("ReceiptMode", " CHEQUE");
		parameterSource.addValue("ReceiptModeStatus", "B");
		parameterSource.addValue("bouncecode", Arrays.asList("41, 403"));
		try {
			return this.jdbcTemplate.query(sql.toString(), parameterSource, new RowMapper<ChequeDetail>() {
				@Override
				public ChequeDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
					ChequeDetail chqueDtl = new ChequeDetail();
					chqueDtl.setChequeBounceDate(rs.getDate("chequeBounceDate"));
					chqueDtl.setAmount(rs.getBigDecimal("amount"));
					chqueDtl.setChequeNumber(rs.getString("chequeNumber"));
					chqueDtl.setChequeBounceDate(rs.getDate("chequeBounceDate"));
					chqueDtl.setAmount(rs.getBigDecimal("chequeBounceReason"));
					return chqueDtl;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.trace(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public List<Long> getGuarantorsDetails(String finRefrence, boolean isBankCustomers) {
		logger.trace(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();

		if (isBankCustomers) {
			sql.append("select distinct c.custId");
			sql.append(" from finguarantorsdetails gd");
		} else {
			sql.append("select guarantorid c.custId");
			sql.append(" from finguarantorsdetails gd");
		}
		sql.append(" inner join Financemain fm on fm.finreference = gd.finreference");
		sql.append(" inner join customers c on c.custcif = gd.guarantorcif");
		sql.append(" where fm.finreference = :finreference");
		sql.append(" and bankCustomer = :bankCustomer");

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("finreference", finRefrence);

		if (isBankCustomers) {
			parameterSource.addValue("bankCustomer", 1);
		} else {
			parameterSource.addValue("bankCustomer", 0);
		}

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcTemplate.query(sql.toString(), parameterSource, new RowMapper<Long>() {
				@Override
				public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getLong("custId");

				}
			});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.trace(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public long getotalRecords(String segmentType) {
		logger.trace(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select count(custid) from (select distinct custid");
		sql.append(" from cibil_customer_extract");
		sql.append(" where segment_type = :segment_type) t");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("segment_type", segmentType);

		return this.jdbcTemplate.queryForObject(sql.toString(), paramMap, Long.class);
	}

	@Override
	public Customer getExternalCustomer(Long customerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CustomerAddres> getExternalCustomerAddres(Long custId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CustomerPhoneNumber> getExternalCustomerPhoneNumbers(Long custId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CustomerDocument> getExternalCustomerDocuments(Long custId) {
		// TODO Auto-generated method stub
		return null;
	}
}