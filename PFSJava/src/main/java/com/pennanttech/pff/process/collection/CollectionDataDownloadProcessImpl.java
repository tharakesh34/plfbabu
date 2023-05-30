package com.pennanttech.pff.process.collection;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.model.CollectionCustomerDetail;

/**
 * Service declaration for methods that depends on <b>CollectionService</b>.<br>
 * 
 */
public class CollectionDataDownloadProcessImpl implements CollectionDataDownloadProcess {
	private final static Logger logger = LogManager.getLogger(CollectionDataDownloadProcessImpl.class);
	private NamedParameterJdbcTemplate jdbcTemplate;

	public void processDownload() {
		int totalRecords = 0;
		if (cleanData()) {
			totalRecords = processCollectionData();
		}

		if (totalRecords > 0) {
			processCustomerData();
		}
	}

	private boolean cleanData() {
		logger.debug(Literal.ENTERING);
		try {
			jdbcTemplate.getJdbcOperations().update("Truncate TABLE COLLECTION_FINANCEDETAILS");
			jdbcTemplate.getJdbcOperations().update("Truncate TABLE COLLECTION_CUSTOMERDETAILS");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			return false;
		}
		logger.debug(Literal.LEAVING);

		return true;
	}

	private int processCollectionData() {
		logger.debug(Literal.ENTERING);

		int count = 0;
		try {
			StringBuilder sql = new StringBuilder("INSERT INTO collection_financedetails");
			sql.append(" (LoanReference, CustCif, LoanType, LoanTypeDesc, Currency, ProductCode,ProductDesc,");
			sql.append(" BranchCode, Branchname ,FinStartDate,MaturityDate,NoInst,NoPaidInst,FirstRepayDate,");
			sql.append(" FirstRepayAmount,NSchdDate,NSchdPri,NSchdPft,TotOutStandingAmt,OverDueDate,NoodInst,");
			sql.append(" CurodDays,ActualodDays,OdPrincipal,OdProfit,DueBucket,PenaltyPaid,PenaltyDue,PenaltyWaived,");
			sql.append(" BounceCharges,FinStatus,Finstsreason,FinWorstStatus,FinActive,");
			sql.append("RecordStatus,RepayMethod, AppDate)");
			sql.append(" Select * from (SELECT T1.FinReference LoanReference,T2.CustCIF CustCIF,");
			sql.append(" T1.FinType LoanType, T3.FinTypeDesc LoanTypeDesc,T1.FinCcy Currency,");
			sql.append(" T1.FinCategory ProductCode	,T3.FinTypeDesc ProductDesc,T1.FinBranch BranchCode	,");
			sql.append(" T4.BranchDesc BranchDesc,T1.FinStartDate FinStartDate,T1.MaturityDate MaturityDate,");
			sql.append(" T1.NoInst,T1.NoPaidInst NoPaidInst,T1.FirstRepayDate FirstRepayDate,");
			sql.append(" T1.FirstRepayAmt FirstRepayAmount,T1.NSchdDate NSchdDate,T1.NSchdPri  NSchdPri,");
			sql.append(
					" T1.NSchdPft NSchdPft,(T1.TotalPftBal+T1.TotalPriBal) TotOustandingAmt,T1.PrvOdDate OverdueDate,");
			sql.append(" T1.NoOdInst NoOdInst,T1.CurODDays curODDays,T1.ActualOdDays ActualOdDays,");
			sql.append(" T1.ODPrincipal ODPrincipal,T1.ODProfit ODProfit,round(T1.CurODDays/30,0) DueBucket,");
			sql.append(" T1.PenaltyPaid PenaltyPaid, T1.PenaltyDue PenaltyDue,T1.PenaltyWaived PenaltyWaived,");
			sql.append(" (SELECT sum(Adviseamount-paidamount-waivedamount) bounseAmount");
			sql.append(" FROM MANUALADVISE WHERE FEETYPEID = 0");
			sql.append(" and finreference=T1.Finreference group by finreference)  BounceCharge ");
			sql.append(" ,T1.FinStatus FinStatus, T1.FinStsReason FinStsReason, T1.FinWorstStatus FinWorstStatus,");
			sql.append(" T1.FinIsActive FinActive, 'I' RecordStatus, ");
			sql.append(" (select FinRepayMethod from financemain where finreference =T1.Finreference) RepayMethod");

			if (App.DATABASE == Database.POSTGRES) {
				sql.append(", to_timestamp(:AppDate1, 'yyyy-MM-dd') AppDate");
			} else {
				sql.append(", :AppDate AppDate");
			}

			sql.append(" FROM FinPftDetails T1 ");
			sql.append(" INNER JOIN Customers T2 ON T1.CustId=T2.CustID");
			sql.append(" INNER JOIN RMTFinanceTypes T3 on T1.FinType=T3.FinType ");
			sql.append(" INNER JOIN RmtBranches T4 on  T1.FinBranch=T4.BranchCode ");
			sql.append(" WHERE (T1.ODPrincipal+T1.ODProfit) > 0 AND T1.CurODDays >= 1) collectionFinance");

			logger.trace(Literal.SQL + sql.toString());

			MapSqlParameterSource parameterSource = new MapSqlParameterSource();

			Date appDate = SysParamUtil.getAppDate();
			parameterSource.addValue("AppDate1", DateUtil.format(appDate, "yyyy-MM-dd"));
			parameterSource.addValue("AppDate", appDate);
			count = jdbcTemplate.update(sql.toString(), parameterSource);
			logger.debug(Literal.LEAVING);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		return count;
	}

	private int processCustomerData() {
		StringBuilder selectSql = new StringBuilder("select distinct CustCIF FROM FinPftDetails");
		selectSql.append(" where (ODPrincipal + ODProfit) > 0 and CurODDays >= 1");

		logger.trace("insertSql: " + selectSql.toString());

		List<String> list = jdbcTemplate.queryForList(selectSql.toString(), new MapSqlParameterSource(), String.class);
		StepUtil.COLLECTION_DOWNLOAD.setTotalRecords(list.size());

		logger.debug("Number of Customers: " + list.size());
		boolean offAddress = false;
		boolean resAddress = false;
		int processedRecords = 0;
		for (String custCIF : list) {
			StepUtil.COLLECTION_DOWNLOAD.setProcessedRecords(++processedRecords);

			CollectionCustomerDetail customer = getCustomerByID(custCIF);

			if (customer != null) {

				List<CustomerAddres> customerAddres = getCustomerAddresByCustomer(customer.getCustId());
				if (!customerAddres.isEmpty()) {
					for (CustomerAddres custAdd : customerAddres) {
						if (StringUtils.equalsIgnoreCase(App.getProperty("addresstype.office"),
								custAdd.getCustAddrType())) {
							customer.setOffAddrHNbr(custAdd.getCustAddrHNbr());
							customer.setOffFlatNbr(custAdd.getCustFlatNbr());
							customer.setOffAddrStreet(custAdd.getCustAddrStreet());
							customer.setOffAddrLine1(custAdd.getCustAddrLine1());
							customer.setOffAddrLine2(custAdd.getCustAddrLine2());
							customer.setOffPoBox(custAdd.getCustPOBox());

							customer.setOffAddrCity(custAdd.getCustAddrCity());
							customer.setOffAddrProvince(custAdd.getCustAddrProvince());
							customer.setOffAddrCountry(custAdd.getCustAddrCountry());
							customer.setOffAddrZip(custAdd.getCustAddrZIP());
							offAddress = true;
						} else if (StringUtils.equalsIgnoreCase(App.getProperty("addresstype.residence"),
								custAdd.getCustAddrType())) {
							customer.setResFlatNbr(custAdd.getCustFlatNbr());
							customer.setResAddrStreet(custAdd.getCustAddrStreet());
							customer.setResAddrLine1(custAdd.getCustAddrLine1());
							customer.setResAddrLine2(custAdd.getCustAddrLine2());
							customer.setResPoBox(custAdd.getCustPOBox());

							customer.setResAddrCity(custAdd.getCustAddrCity());
							customer.setResAddrProvince(custAdd.getCustAddrProvince());
							customer.setResAddrCountry(custAdd.getCustAddrCountry());
							customer.setResAddrZip(custAdd.getCustAddrZIP());
							resAddress = true;
						}
					}

					if (!offAddress && !resAddress) {
						CustomerAddres resAdd = customerAddres.get(0);
						customer.setResFlatNbr(resAdd.getCustFlatNbr());
						customer.setResAddrStreet(resAdd.getCustAddrStreet());
						customer.setResAddrLine1(resAdd.getCustAddrLine1());
						customer.setResAddrLine2(resAdd.getCustAddrLine2());
						customer.setResPoBox(resAdd.getCustPOBox());

						customer.setResAddrCity(resAdd.getCustAddrCity());
						customer.setResAddrProvince(resAdd.getCustAddrProvince());
						customer.setResAddrCountry(resAdd.getCustAddrCountry());
						customer.setResAddrZip(resAdd.getCustAddrZIP());
						resAddress = true;

						if (customerAddres.size() >= 2) {
							CustomerAddres offAdd = customerAddres.get(1);
							customer.setOffAddrHNbr(offAdd.getCustAddrHNbr());
							customer.setOffFlatNbr(offAdd.getCustFlatNbr());
							customer.setOffAddrStreet(offAdd.getCustAddrStreet());
							customer.setOffAddrLine1(offAdd.getCustAddrLine1());
							customer.setOffAddrLine2(offAdd.getCustAddrLine2());
							customer.setOffPoBox(offAdd.getCustPOBox());

							customer.setOffAddrCity(offAdd.getCustAddrCity());
							customer.setOffAddrProvince(offAdd.getCustAddrProvince());
							customer.setOffAddrCountry(offAdd.getCustAddrCountry());
							customer.setOffAddrZip(offAdd.getCustAddrZIP());
						}
					}
				}

				List<CustomerPhoneNumber> phoneNumbers = getCustomerPhoneNumberByCustomer(customer.getCustId());
				if (!phoneNumbers.isEmpty()) {
					customer.setPhoneNumber1(phoneNumbers.get(0).getPhoneNumber());
					if (phoneNumbers.size() >= 2) {
						customer.setPhoneNumber2(phoneNumbers.get(1).getPhoneNumber());
					}
				}

				List<CustomerEMail> eMails = getCustomerEmailByCustomer(customer.getCustId());
				if (!eMails.isEmpty()) {
					customer.setCustEMail1(eMails.get(0).getCustEMail());

					if (eMails.size() >= 2) {
						customer.setCustEmail2(eMails.get(1).getCustEMail());
					}
				}

				StringBuilder sql = new StringBuilder("INSERT INTO COLLECTION_CUSTOMERDETAILS ");
				sql.append(" (CustCIF, CustCoreBank, CustCtg, CustType, CustSalutationCode");
				sql.append(", CustFName,CustMName, CustLName, CustShrtName, CustGenderCode");
				sql.append(", CustDOB, CustMaritalSts, CustSector, CustSubSector, CustIndustry");
				sql.append(", CustProfession, CustSegment, CustSubSegment, CustDSA, CustDSADept, ResAddrHNbr");
				sql.append(", ResFlatNbr, ResAddrStreet , ResAddrLine1, ResAddrLine2, ResPoBox, ResAddrCity");
				sql.append(", ResAddrProvince, ResAddrCountry, ResAddrZip, OffAddrHNbr, OffFlatNbr");
				sql.append(", OffAddrStreet, OffAddrLine1, OffAddrLine2, OffPoBox, OffAddrCity, OffAddrProvince");
				sql.append(", OffAddrCountry, OffAddrZip,phoneNumber1, phoneNumber2, custEMail1, custEmail2)");
				sql.append(" Values(:CustCIF, :CustCoreBank, :CustCtgCode, :CustTypeCode, :CustSalutationCode");
				sql.append(", :CustFName, :CustMName,:CustLName, :CustShrtName,:CustGenderCode, :CustDOB");
				sql.append(", :CustMaritalSts, :CustSector, :CustSubSector, :CustIndustry, :CustProfession");
				sql.append(", :CustSegment, :CustSubSegment, :CustDSA, :CustDSADept, :ResAddrHNbr, :ResFlatNbr");
				sql.append(", :ResAddrStreet, :ResAddrLine1, :ResAddrLine2, :ResPoBox, :ResAddrCity, :ResAddrProvince");
				sql.append(", :ResAddrCountry, :ResAddrZip,:OffAddrHNbr, :OffFlatNbr, :OffAddrStreet, :OffAddrLine1");
				sql.append(", :OffAddrLine2, :OffPoBox, :OffAddrCity, :OffAddrProvince, :OffAddrCountry, :OffAddrZip");
				sql.append(", :phoneNumber1, :phoneNumber2, :custEMail1, :custEmail2)");
				logger.trace(Literal.SQL + sql.toString());

				SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
				jdbcTemplate.update(sql.toString(), beanParameters);
			}

		}

		return 0;
	}

	private CollectionCustomerDetail getCustomerByID(String custCIF) {
		logger.debug(Literal.ENTERING);
		CollectionCustomerDetail customer = new CollectionCustomerDetail();
		customer.setCustCIF(custCIF);

		StringBuilder sql = new StringBuilder("select CustID, CustCIF, CustCoreBank, CustCtgCode");
		sql.append(", CustSalutationCode, CustFName, CustMName, CustLName, CustShrtName");
		sql.append(", CustTypeCode, CustGenderCode, CustDOB, CustMaritalSts, CustSector, CustSubSector");
		sql.append(", CustIndustry, CustProfession, CustSegment, CustSubSegment, CustDSA, CustDSADept");
		sql.append(" FROM Customers");
		sql.append(" Where CustCIF = :CustCIF");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<CollectionCustomerDetail> typeRowMapper = BeanPropertyRowMapper
				.newInstance(CollectionCustomerDetail.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * Method For getting List of Customer related Addresses for Customer
	 */

	private List<CustomerAddres> getCustomerAddresByCustomer(final long custId) {
		logger.debug(Literal.ENTERING);
		CustomerAddres customerAddres = new CustomerAddres();
		customerAddres.setId(custId);

		StringBuilder sql = new StringBuilder();
		sql.append("select CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet");
		sql.append(", CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCity, CustAddrProvince,CustAddrPriority");
		sql.append(", CustAddrCountry, CustAddrZIP, CustAddrPhone, CustAddrFrom,TypeOfResidence,CustAddrLine3");
		sql.append(", CustAddrLine4, CustDistrict FROM CustomerAddresses");
		sql.append(" Where CustID = :custID order by CustAddrPriority desc");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAddres);
		RowMapper<CustomerAddres> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerAddres.class);

		List<CustomerAddres> customerAddresses = this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		logger.debug(Literal.LEAVING);
		return customerAddresses;
	}

	private List<CustomerPhoneNumber> getCustomerPhoneNumberByCustomer(final long id) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PhoneCustID, PhoneTypeCode, PhoneCountryCode, PhoneAreaCode, PhoneNumber");
		sql.append(", PhoneTypePriority");
		sql.append(" From CustomerPhoneNumbers");
		sql.append(" Where PhoneCustID = ?");

		logger.trace(Literal.SQL + sql.toString());

		List<CustomerPhoneNumber> phList = this.jdbcTemplate.getJdbcOperations().query(sql.toString(),
				ps -> ps.setLong(1, id), (rs, rowNum) -> {
					CustomerPhoneNumber custPhnNumbr = new CustomerPhoneNumber();

					custPhnNumbr.setPhoneCountryCode(rs.getString("PhoneCountryCode"));
					custPhnNumbr.setPhoneAreaCode(rs.getString("PhoneAreaCode"));
					custPhnNumbr.setPhoneTypeCode(rs.getString("PhoneTypeCode"));
					custPhnNumbr.setPhoneNumber(rs.getString("PhoneNumber"));

					return custPhnNumbr;
				});

		return phList.stream()
				.sorted((ph1, ph2) -> Integer.compare(ph2.getPhoneTypePriority(), ph1.getPhoneTypePriority()))
				.collect(Collectors.toList());
	}

	/**
	 * Method to return the customer email based on given customer id
	 */
	private List<CustomerEMail> getCustomerEmailByCustomer(final long id) {
		StringBuilder sql = new StringBuilder();
		sql.append("select CustID, CustEMail, CustEMailPriority, CustEMailTypeCode");
		sql.append(" FROM  CustomerEMails");
		sql.append(" Where CustID = ?");

		logger.trace(Literal.SQL + sql.toString());

		List<CustomerEMail> customerEMails = this.jdbcTemplate.getJdbcOperations().query(sql.toString(),
				ps -> ps.setLong(1, id), (rs, rowNum) -> {
					CustomerEMail custEMail = new CustomerEMail();
					custEMail.setCustID(rs.getLong("CustID"));
					custEMail.setCustEMail(rs.getString("CustEMail"));
					custEMail.setCustEMailPriority(rs.getInt("CustEMailPriority"));
					custEMail.setCustEMailTypeCode(rs.getString("CustEMailTypeCode"));

					return custEMail;
				});

		return customerEMails.stream()
				.sorted((em1, em2) -> em2.getCustEMailTypeCode().compareTo(em1.getCustEMailTypeCode()))
				.collect(Collectors.toList());

	}

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}
