package com.pennanttech.pff.process.collection;

import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennanttech.pff.model.CollectionCustomerDetail;

/**
 * Service declaration for methods that depends on <b>CollectionService</b>.<br>
 * 
 */
public class CollectionDataDownloadProcess {
	private NamedParameterJdbcTemplate jdbcTemplate;
	private final static Logger logger = Logger.getLogger(CollectionDataDownloadProcess.class);

	public int processDownload() {
		int count = 0;
		if (cleanData()) {
			count = processCollectionData();
		}

		if (count > 0) {
			processCustomerData();
		}

		return count;
	}

	private boolean cleanData() {
		logger.debug("Entering");

		try {
			String sql = "Truncate TABLE COLLECTION_FINANCEDETAILS ";
			jdbcTemplate.update(sql.toString(),new HashMap<String, Object>() );

			sql = "Truncate TABLE  COLLECTION_CUSTOMERDETAILS";
			jdbcTemplate.update(sql.toString(),new HashMap<String, Object>() );
		} catch (Exception e) {
			System.out.println("Exception e" + e);
			logger.debug("Leaving");
			return false;
		}
		logger.debug("Leaving");

		return true;
	}

	private int processCollectionData() {
		logger.debug("Entering");

		int count = 0;
		StringBuilder selectSql = new StringBuilder(" INSERT INTO collection_financedetails  ");
		selectSql.append(
				" (id,loanreference,custcif,loantype,loantypedesc,currency,productcode,productdesc,branchcode,branchname ,");
		selectSql.append(
				"finstartdate,maturitydate,noinst,nopaidinst,firstrepaydate,firstrepayamount,nschddate,nschdpri,nschdpft,");
		selectSql.append(
				"totoutstandingamt,overduedate,noodinst,curoddays,actualoddays,odprincipal,odprofit,duebucket,penaltypaid	,penaltydue	,");
		selectSql.append(
				"penaltywaived,bouncecharges,finstatus,finstsreason,finworststatus,finactive,recordstatus )");
		selectSql.append("Select * from (SELECT ");
		selectSql.append(" T1.CustId AS Id,T1.FinReference AS LoanReference,T2.CustCIF AS CustCIF,");
		selectSql.append(" T1.FinType As LoanType, T3.FinTypeDesc AS LoanTypeDesc,T1.FinCcy AS Currency,");
		selectSql
				.append(" T1.FinCategory AS ProductCode	,T3.FinTypeDesc AS ProductDesc,T1.FinBranch As BranchCode	,");
		selectSql.append(
				" T4.BranchDesc As BranchDesc,T1.FinStartDate As FinStartDate,T1.MaturityDate As MaturityDate,");
		selectSql.append(" T1.NoInst As NoInt,T1.NoPaidInst As NoPaidInst,T1.FirstRepayDate As FirstRepayDate,");
		selectSql.append(" T1.FirstRepayAmt As FirstRepayAmount,T1.NSchdDate As NSchdDate,T1.NSchdPri  AS NSchdPri,");
		selectSql.append(" T1.NSchdPft As NSchdPft,(T1.TotalPftBal+T1.TotalPriBal) AS TotOustandingAmt,T1.PrvOdDate AS OverdueDate,");
		selectSql.append(" T1.NoOdInst AS NoOdInst,T1.CurODDays AS curODDays,T1.ActualOdDays As ActualOdDays,");
		selectSql.append(
				" T1.ODPrincipal As ODPrincipal,T1.ODProfit As ODProfit,round(T1.CurODDays/30,0) As DueBucket, T1.PenaltyPaid As PenaltyPaid, ");
		selectSql.append(
				" T1.PenaltyDue AS PenaltyDue,T1.PenaltyWaived As PenaltyWaived,(SELECT sum(Adviseamount-paidamount-waivedamount) as bounseAmount  FROM MANUALADVISE WHERE FEETYPEID = 0 and  finreference=T1.Finreference)  As BounceCharge ,T1.FinStatus As FinStatus,");
		selectSql.append(" T1.FinStsReason As FinStsReason ,T1.FinWorstStatus As FinWorstStatus,");
		selectSql.append(" T1.FinIsActive As FinActive	,T1.ClosingStatus As RecordStatus");

		selectSql.append("  FROM FinPftDetails AS T1 ");
		selectSql.append("  INNER JOIN Customers AS T2 ON T1.CustId=T2.CustID");
		selectSql.append("  INNER JOIN RMTFinanceTypes AS T3 on T1.FinType=T3.FinType ");
		selectSql.append("  INNER JOIN RmtBranches AS T4 on  T1.FinBranch=T4.BranchCode ");
		selectSql.append("   WHERE (T1.ODPrincipal+T1.ODProfit) > 0 AND T1.CurODDays >= 1) As collectionFinance");
		
		logger.trace("insertSql: " + selectSql.toString());
		
		count = jdbcTemplate.update(selectSql.toString(), new HashMap<String, Object>());
		logger.debug("Leaving");
		return count;
	}

	private int processCustomerData() {

		StringBuilder selectSql = new StringBuilder(" SELECT ");
		selectSql.append(" DISTINCT custcif  FROM FinPftDetails ");
		selectSql.append("  WHERE (ODPrincipal+ODProfit) > 0 AND CurODDays >= 1 ");
		
		logger.trace("insertSql: " + selectSql.toString());
		
		List<String> list = jdbcTemplate.queryForList(selectSql.toString(), new MapSqlParameterSource(), String.class);

		logger.debug("Number of Customers: " + list.size());
		
		for (String custCIF : list) {

			CollectionCustomerDetail customer = getCustomerByID(custCIF);

			if (customer != null) {

				List<CustomerAddres> customerAddres = getCustomerAddresByCustomer(customer.getCustId());
				if (!customerAddres.isEmpty()) {
					for (CustomerAddres custAdd : customerAddres) {
						if (custAdd.getCustAddrType().equalsIgnoreCase("OFFICE")) {
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

						} else if (custAdd.getCustAddrType().equalsIgnoreCase("REGADD")) {
							customer.setResFlatNbr(custAdd.getCustFlatNbr());
							customer.setResAddrStreet(custAdd.getCustAddrStreet());
							customer.setResAddrLine1(custAdd.getCustAddrLine1());
							customer.setResAddrLine2(custAdd.getCustAddrLine2());
							customer.setResPoBox(custAdd.getCustPOBox());

							customer.setResAddrCity(custAdd.getCustAddrCity());
							customer.setResAddrProvince(custAdd.getCustAddrProvince());
							customer.setResAddrCountry(custAdd.getCustAddrCountry());
							customer.setResAddrZip(custAdd.getCustAddrZIP());

						}

					}
				}

				List<CustomerPhoneNumber> phoneNumbers = getCustomerPhoneNumberByCustomer(customer.getCustId());
				if (!phoneNumbers.isEmpty()) {
					customer.setPhoneNumber1(phoneNumbers.get(0).getPhoneNumber());
					if (phoneNumbers.size() >= 2) {
						customer.setPhoneNumber2(phoneNumbers.get(0).getPhoneNumber());
					}
				}

				List<CustomerEMail> eMails = getCustomerEmailByCustomer(customer.getCustId());
				if (!eMails.isEmpty()) {
					customer.setCustEMail1(eMails.get(0).getCustEMail());

					if (eMails.size() >= 2) {
						customer.setCustEmail2(eMails.get(1).getCustEMail());
					}
				}

				StringBuilder insertSql = new StringBuilder("INSERT INTO COLLECTION_CUSTOMERDETAILS ");
				insertSql.append(
						" Values(:CustCIF, :CustCoreBank, :CustCtgCode, :CustTypeCode, :CustSalutationCode, :CustFName, :CustMName,");
				insertSql.append(
						" :CustLName, :CustShrtName,:CustGenderCode, :CustDOB, :CustMaritalSts, :CustSector, :CustSubSector, :CustIndustry, ");
				insertSql.append(
						" :CustProfession, :CustSegment, :CustSubSegment, :CustDSA, :CustDSADept, :ResAddrHNbr, :ResFlatNbr , :ResAddrStreet,");
				insertSql.append(
						" :ResAddrLine1, :ResAddrLine2, :ResPoBox, :ResAddrCity, :ResAddrProvince, :ResAddrCountry, :ResAddrZip,");
				insertSql.append(
						" :OffAddrHNbr, :OffFlatNbr , :OffAddrStreet, :OffAddrLine1, :OffAddrLine2, :OffPoBox, :OffAddrCity, :OffAddrProvince, ");
				insertSql.append(
						" :OffAddrCountry, :OffAddrZip, :phoneNumber1, :phoneNumber2, :custEMail1, :custEmail2)");
				
				logger.trace("insertSql: " + insertSql.toString());
				
				SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
				jdbcTemplate.update(insertSql.toString(), beanParameters);
			}

			// Fetch the phone numbers based on the order
			// Fetch the email numbers based on the order

			//Insert into new table 
		}

		return 0;
	}

	private CollectionCustomerDetail getCustomerByID(String custCIF) {
		logger.debug("Entering");
		CollectionCustomerDetail customer = new CollectionCustomerDetail();
		customer.setCustCIF(custCIF);

		StringBuilder selectSql = new StringBuilder("SELECT CustID, CustCIF, CustCoreBank, CustCtgCode, CustTypeCode,");
		selectSql.append(" CustSalutationCode, CustFName, CustMName, CustLName, CustShrtName,");
		selectSql.append("  CustGenderCode, CustDOB,CustMaritalSts, CustSector, CustSubSector, CustIndustry,");
		selectSql.append("  CustProfession,CustSegment, CustSubSegment,CustDSA, CustDSADept");
		selectSql.append(" FROM  Customers");
		selectSql.append(" Where CustCIF =:CustCIF");

		logger.trace("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<CollectionCustomerDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CollectionCustomerDetail.class);

		try {
			customer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			customer = null;
		}
		logger.debug("Leaving");
		return customer;
	}

	/**
	 * Method For getting List of Customer related Addresses for Customer
	 */

	private List<CustomerAddres> getCustomerAddresByCustomer(final long custId) {
		logger.debug("Entering");
		CustomerAddres customerAddres = new CustomerAddres();
		customerAddres.setId(custId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT  CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet,");
		selectSql.append(" CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCity, CustAddrProvince,CustAddrPriority,");
		selectSql.append(" CustAddrCountry, CustAddrZIP, CustAddrPhone, CustAddrFrom,TypeOfResidence,CustAddrLine3,CustAddrLine4,CustDistrict");
		selectSql.append(" FROM CustomerAddresses");
		selectSql.append(" Where CustID = :custID order by CustAddrPriority desc");

		logger.trace("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAddres);
		RowMapper<CustomerAddres> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerAddres.class);

		List<CustomerAddres> customerAddresses = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving");
		return customerAddresses;
	}

	private List<CustomerPhoneNumber> getCustomerPhoneNumberByCustomer(final long id) {
		logger.debug("Entering");
		CustomerPhoneNumber customerPhoneNumber = new CustomerPhoneNumber();
		customerPhoneNumber.setPhoneCustID(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT  PhoneCustID, PhoneTypeCode, PhoneCountryCode, PhoneAreaCode, PhoneNumber,PhoneTypePriority");
		selectSql.append(" FROM  CustomerPhoneNumbers");
		selectSql.append(" Where PhoneCustID =:PhoneCustID order by PhoneTypePriority desc");

		logger.trace("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
		RowMapper<CustomerPhoneNumber> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerPhoneNumber.class);

		List<CustomerPhoneNumber> customerPhoneNumbers = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving ");
		return customerPhoneNumbers;
	}

	/**
	 * Method to return the customer email based on given customer id
	 */
	private List<CustomerEMail> getCustomerEmailByCustomer(final long id) {
		logger.debug("Entering");
		CustomerEMail customerEMail = new CustomerEMail();
		customerEMail.setId(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID, CustEMail, CustEMailPriority, CustEMailTypeCode");
		selectSql.append(" FROM  CustomerEMails");
		selectSql.append(" Where CustID = :custID order by CustEMailPriority desc");

		logger.trace("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);
		RowMapper<CustomerEMail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerEMail.class);

		List<CustomerEMail> customerEMails = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);

		logger.debug("Leaving");
		return customerEMails;
	}

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}
