package com.pennanttech.pff.external.customer;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennanttech.bajaj.services.BajajService;
import com.pennanttech.gcd.GcdCustomer;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.Crm;

public class FinnOneCustomer extends BajajService implements Crm {
	private final Logger logger = Logger.getLogger(getClass());

	private DataSource finOneDataSource;

	@Override
	public CustomerDetails create(CustomerDetails customerDetails) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		customerDetails.setReturnStatus(new WSReturnStatus());

		if (!"Y".equalsIgnoreCase((String) getSMTParameter("GCD_FINONE_PROC_REQD", String.class))) {
			customerDetails.getReturnStatus().setReturnCode(InterfaceConstants.SUCCESS_CODE);
			return customerDetails;
		}

		CustomerProcedure customerproc = new CustomerProcedure(finOneDataSource, "CREATE_CUSTOMER_IN_FINNONE");
		Map<String, Object> params = new LinkedHashMap<>();

		GcdCustomer gcdCustomer = preparegcdCustomer(customerDetails);

		logRequest(gcdCustomer);
		
		if(StringUtils.equals(gcdCustomer.getFinCustId(), null)){			
			params.put("P_FINN_CUSTID", null);
		}else{			
			params.put("P_FINN_CUSTID", Long.parseLong(gcdCustomer.getFinCustId()));
		}

		params.put("P_SOURCE_SYSTEM", "PLF");
		params.put("P_CUSTOMERNAME", gcdCustomer.getCustomerName());
		params.put("P_CONSTID", gcdCustomer.getConstId());
		params.put("P_INDUSTRYID", gcdCustomer.getIndustryId());
		params.put("P_CATEGORYID", gcdCustomer.getCategoryId());
		params.put("P_SPOUSENAME", gcdCustomer.getSpousename());
		params.put("P_INDV_CORP_FLAG", gcdCustomer.getIndvCorpFlag());
		params.put("P_FNAME", gcdCustomer.getfName());
		params.put("P_MNAME", gcdCustomer.getmName());
		params.put("P_LNAME", gcdCustomer.getLname());
		params.put("P_DOB", gcdCustomer.getDOB());
		params.put("P_SEX", gcdCustomer.getSex());
		params.put("P_INCOME_SOURCE", gcdCustomer.getIncomeSource());
		params.put("P_YEARS_CURR_JOB", gcdCustomer.getYearsOfCurrJob());
		params.put("P_COR_DOI", gcdCustomer.getDOI());
		params.put("P_MAKERID", gcdCustomer.getMpAkerId());
		params.put("P_MAKERDATE", gcdCustomer.getMakerDate());
		params.put("P_AUTHID", gcdCustomer.getAuthId());
		params.put("P_AUTHDATE", gcdCustomer.getAuthDate());
		params.put("P_ACCOTYPE", gcdCustomer.getAccType());
		params.put("P_ACCOCATG", gcdCustomer.getApCcocatg());
		params.put("P_DATELASTUPDT", gcdCustomer.getDateLastUpdate());
		params.put("P_NATIONALID", gcdCustomer.getNationalId());
		params.put("P_PASSPORTNO", gcdCustomer.getPassportNo());
		params.put("P_NATIONALITY", gcdCustomer.getNationality());
		params.put("P_PAN_NO", gcdCustomer.getPanNo());
		params.put("P_REGIONID", gcdCustomer.getRegionId());
		params.put("P_BANK_TYPE", gcdCustomer.getBankType());
		params.put("P_ENTITYFLAG", gcdCustomer.getEntityFlag());
		params.put("P_CONTACT_PERSON", gcdCustomer.getContactPerson());
		params.put("P_CUSTSEARCHID", gcdCustomer.getCustSearchId());
		params.put("P_ECONOMIC_SEC_ID", gcdCustomer.getSectorId());
		params.put("P_FRAUD_FLAG", gcdCustomer.getFraudFlag());
		params.put("P_FRAUD_SCORE", gcdCustomer.getFraudScore());
		params.put("P_EMI_CARD_ELIG", gcdCustomer.getEmiCardElig());
		params.put("P_ADDRESS_DTL", gcdCustomer.getAddressDetail());
		params.put("P_BANK_DTL", gcdCustomer.getBankDetail());
		params.put("P_N_NAME", gcdCustomer.getNomineeName());
		params.put("P_N_ADDRESS", gcdCustomer.getNomineeAddress());
		params.put("P_N_RELATION", gcdCustomer.getNomineeRelationship());
		params.put("P_N_FIELD9", gcdCustomer.getField9());
		params.put("P_N_FIELD10", gcdCustomer.getField10());
		params.put("P_INS_UPD_FLAG", gcdCustomer.getInsertUpdateFlag());
		params.put("P_SUCCESS_REJECT", gcdCustomer.getStatusFromFinnOne());
		params.put("P_REJECTION_REASON", gcdCustomer.getRejectionReason());
		if(StringUtils.equals(gcdCustomer.getFinCustId(), null)){			
			params.put("P_FINN_CUST_ID", null);
		}else{			
			params.put("P_FINN_CUST_ID", Long.parseLong(gcdCustomer.getFinCustId()));
		}
		params.put("P_SFDC_CUSTOMERID", gcdCustomer.getSfdcCustomerId());
		if(customerDetails.getCustomer().getBranchRefno()!=null){			
			params.put("P_BRANCHID", Long.parseLong(customerDetails.getCustomer().getBranchRefno()));
		}else{
			params.put("P_BRANCHID", null);
		}
		
		
		StringBuilder builder = new StringBuilder();
		for (Entry<String, Object> input : params.entrySet()) {
			if (builder.length() > 0) {
				builder.append("\n");
			}
			
			logger.debug(input.getKey()+"------->"+input.getValue());
		}
		

		try {
			customerproc.setQueryTimeout((Integer)getSMTParameter("GCD_PROC_TIMEOUT", Integer.class));
			Map<String, Object> output = customerproc.execute(params);

			if (output != null && !output.isEmpty()) {
				if (((String) output.get("P_SUCCESS_REJECT")).equals("R")) {
					gcdCustomer.setStatusFromFinnOne(String.valueOf(output.get("P_SUCCESS_REJECT")));
					gcdCustomer.setRejectionReason(String.valueOf(output.get("P_REJECTION_REASON")));
					updateFailStatus(gcdCustomer);
				} else {
					customerDetails.getCustomer().setCustCoreBank(String.valueOf(output.get("P_FINN_CUST_ID")));
					gcdCustomer.setFinnCustId(String.valueOf(output.get("P_FINN_CUST_ID")));
					gcdCustomer.setStatusFromFinnOne(String.valueOf(output.get("P_SUCCESS_REJECT")));
					gcdCustomer.setRejectionReason(String.valueOf(output.get("P_REJECTION_REASON")));
					updateSuccessStatus(gcdCustomer);
				}

			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
				gcdCustomer.setStatusFromFinnOne("P");
			if(e instanceof QueryTimeoutException){
				gcdCustomer.setRejectionReason("Request Timed Out");
				updateFailStatus(gcdCustomer);
			}else{
				gcdCustomer.setRejectionReason(StringUtils.substring(e.getMessage(), 0,50));
				updateFailStatus(gcdCustomer);
			}
			
		}
		
		logger.debug(Literal.LEAVING);
		return customerDetails;
	}

	@Override
	public CustomerDetails update(CustomerDetails customerDetails) throws InterfaceException {
		return customerDetails;
	}

	public void setFinOneDataSource(DataSource finOneDataSource) {
		this.finOneDataSource = finOneDataSource;
	}

	private GcdCustomer preparegcdCustomer(CustomerDetails customerDetail) {
		logger.debug(Literal.ENTERING);
		Customer customer = customerDetail.getCustomer();

		GcdCustomer gcdCustomer = new GcdCustomer();
		
		if (StringUtils.isEmpty(customerDetail.getCustomer().getCustCoreBank())) {
			gcdCustomer.setInsertUpdateFlag("I");
		} else {
			gcdCustomer.setInsertUpdateFlag("U");
		}

		gcdCustomer.setCustId(customer.getCustID());
		gcdCustomer.setFinCustId(StringUtils.isEmpty(customer.getCustCoreBank()) ? null : customer.getCustCoreBank());
		gcdCustomer.setCustomerName(customer.getCustShrtName());
		gcdCustomer.setConstId(Long.valueOf(customer.getCustTypeCode()));
		gcdCustomer.setIndustryId(Long.parseLong(customer.getCustIndustry()));

		if ("RETAIL".equalsIgnoreCase(customer.getCustCtgCode())) {
			gcdCustomer.setIndvCorpFlag("I");
			gcdCustomer.setDOB(customer.getCustDOB());
				if(customerDetail.getEmploymentDetailsList() != null) {
					for (CustomerEmploymentDetail custEmplymentDetail : customerDetail.getEmploymentDetailsList()) {
						if (custEmplymentDetail.isCurrentEmployer()) {
							gcdCustomer.setYearsOfCurrJob(custEmplymentDetail.getCustEmpFrom());
						}
					}
				}
		} else if ("CORP".equalsIgnoreCase(customer.getCustCtgCode())) {
			gcdCustomer.setIndvCorpFlag("C");
			gcdCustomer.setDOI(customer.getCustDOB());
		}

		gcdCustomer.setfName(StringUtils.isEmpty(customer.getCustFName()) ? null : customer.getCustFName());
		gcdCustomer.setmName(StringUtils.isEmpty(customer.getCustMName()) ? null : customer.getCustMName());
		gcdCustomer.setLname(StringUtils.isEmpty(customer.getCustLName()) ? null : customer.getCustLName());
		gcdCustomer.setSex(customer.getCustGenderCode());
		gcdCustomer.setMakerDate(new Timestamp(System.currentTimeMillis()));
		gcdCustomer.setAuthDate(new Timestamp(System.currentTimeMillis()));
		gcdCustomer
				.setPassportNo(StringUtils.isEmpty(customer.getCustPassportNo()) ? null : customer.getCustPassportNo());
		gcdCustomer.setPanNo(customer.getCustCRCPR());
		gcdCustomer.setCustSearchId(customer.getCustCRCPR());
		gcdCustomer.setSectorId(Long.parseLong(customer.getCustSector()));
		gcdCustomer.setAddressDetail(prepareGcdCustAddress(2, customerDetail));
		gcdCustomer.setFinnCustId(customer.getCustCoreBank());
		gcdCustomer.setSfdcCustomerId(Long.parseLong(customer.getCustCIF()));
		gcdCustomer.setEmiCardElig("0");
		
		
		
		
		logger.debug(Literal.LEAVING);
		return gcdCustomer;
	}

	public String prepareGcdCustAddress(int noOfAddress, CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);
		String addressDetails = "";
		final char separator = '~';
		String phoneNo = "";
		String mobileNo = "";
		String phoneAreaCode = "";
		String eMail = "";
		String flatNumber = "";
		String addressLine1 = "";
		String addressLine2 = "";
		String cityRefNo = "";
		String stateRefNo = "";
		int noOfAddressFlag = 1;
		List<CustomerAddres> custAddressList = customerDetails.getAddressList();
		if (custAddressList != null) {
			Collections.sort(custAddressList, new Comparator<CustomerAddres>() {
				public int compare(CustomerAddres o1, CustomerAddres o2) {
					return (int) (o2.getCustAddrPriority() - o1.getCustAddrPriority());
				}
			});

			for (CustomerAddres address : custAddressList) {
				if (noOfAddress >= noOfAddressFlag) {
					char isPriorityVeryHigh = 'N';
					if (address.getCustAddrPriority() == 5) {
						isPriorityVeryHigh = 'Y';
					}
					phoneNo = "";
					mobileNo = "";
					phoneAreaCode = "";
					eMail = "";

					for (CustomerPhoneNumber phone : customerDetails.getCustomerPhoneNumList()) {
						if (phone.getPhoneTypePriority() == address.getCustAddrPriority()) {
							phoneNo = phone.getPhoneNumber();
							phoneAreaCode = phone.getPhoneAreaCode();
						}
						mobileNo = phone.getPhoneNumber();
					}

					for (CustomerEMail mail : customerDetails.getCustomerEMailList()) {
						if (mail.getCustEMailPriority() == address.getCustAddrPriority()) {
							eMail = mail.getCustEMail();
						}
					}

					if (StringUtils.equals(address.getCustFlatNbr(), null)) {
						flatNumber = "";
					} else {
						flatNumber = address.getCustFlatNbr();
					}

					addressLine1 = address.getCustAddrLine1() == null ? "" : address.getCustAddrLine1();
					addressLine2 = address.getCustAddrLine2() == null ? "" : address.getCustAddrLine2();
					cityRefNo = address.getCityRefNo() == null ? "" : address.getCityRefNo();
					stateRefNo = address.getStateRefNo()==null ? "":address.getStateRefNo();

					addressDetails += address.getCustAddrType() + separator + cityRefNo + separator
							+ address.getLovDescCustAddrCountryName() + separator + stateRefNo
							+ separator + address.getCustAddrZIP() + separator + phoneNo + separator + mobileNo
							+ separator + isPriorityVeryHigh + separator + address.getCustAddrHNbr() + separator
							+ flatNumber + separator + address.getCustAddrStreet() + separator + addressLine1
							+ addressLine2 + separator + StringUtils.trimToEmpty(phoneAreaCode) + separator + separator + eMail + ";";

					noOfAddressFlag++;
				}
			}
		}
		
		logger.debug(Literal.LEAVING);
		return addressDetails;

	}

	public void logRequest(GcdCustomer gcdCustomer) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		
		setRequsetSeq(gcdCustomer);
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" Insert Into GCDCUSTOMERS");
			sql.append(" (CustId, FinCustId, SourceSystem, CustomerName, ConstId, IndustryId, CategoryId, Spousename,");
			sql.append(" IndvCorpFlag, FName, MName, Lname, DOB, Sex,");
			sql.append(
					" IncomeSource, YearsOfCurrJob, DOI, MpAkerId, MakerDate, AuthId, AuthDate, AccType, ApCcocatg,");
			sql.append(" DateLastUpdate, NationalId, PassportNo, Nationality, PanNo, RegionId, BankType, EntityFlag,");
			sql.append(" ContactPerson, CustSearchId, SectorId, FraudFlag, FraudScore, EmiCardElig, AddressDetail,");
			sql.append(" BankDetail, NomineeName, NomineeAddress, NomineeRelationship, Field9, Field10,");
			sql.append(" InsertUpdateFlag, StatusFromFinnOne, RejectionReason, FinnCustId, SfdcCustomerId, BranchId, RequestSeq)");
			sql.append(
					" Values(:CustId, :FinCustId, :SourceSystem, :CustomerName, :ConstId, :IndustryId, :CategoryId, :Spousename,");
			sql.append(" :IndvCorpFlag, :FName, :MName, :Lname, :DOB, :Sex,");
			sql.append(" :IncomeSource, :YearsOfCurrJob, :DOI, :MpAkerId, :MakerDate, :AuthId,");
			sql.append(" :AuthDate, :AccType, :ApCcocatg, :DateLastUpdate, :NationalId, :PassportNo,");
			sql.append(" :Nationality, :PanNo, :RegionId, :BankType, :EntityFlag,");
			sql.append(" :ContactPerson, :CustSearchId, :SectorId, :FraudFlag, :FraudScore,");
			sql.append(
					" :EmiCardElig, :AddressDetail, :BankDetail, :NomineeName, :NomineeAddress, :NomineeRelationship,");
			sql.append(
					" :Field9, :Field10, :InsertUpdateFlag, :StatusFromFinnOne, :RejectionReason, :FinnCustId, :SfdcCustomerId, :BranchId,:RequestSeq)");

			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(gcdCustomer);
			this.namedJdbcTemplate.update(sql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw new InterfaceException("99014", "Unable to log the crate or update customer request");
		}
		logger.debug(Literal.LEAVING);

	}
	/*setting the requestSeq  to the gcdcustomer to maintain sequence if the same customer hit the procedure 
	 * more than one time.
	 */
	private void setRequsetSeq(GcdCustomer gcdCustomer) {
		logger.debug("Entering");
		int count = 0;
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From GCDCUSTOMERS");
		selectSql.append(" Where custId = :custId");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(gcdCustomer);

		try {
			count = this.namedJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.debug(dae);
		}
		if (count > 0) {
			GcdCustomer finoneCust = new GcdCustomer() ;
			finoneCust.setCustId(gcdCustomer.getCustId());
			StringBuilder sql = new StringBuilder("SELECT MAX(requestSeq) requestSeq");
			sql.append(" From GCDCUSTOMERS");
			sql.append(" Where custId = :custId");

			logger.debug("selectSql: " + sql.toString());
			SqlParameterSource beanparms = new BeanPropertySqlParameterSource(finoneCust);
			RowMapper<GcdCustomer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(GcdCustomer.class);

			try {
				finoneCust=this.namedJdbcTemplate.queryForObject(sql.toString(), beanparms, typeRowMapper);
			} catch (EmptyResultDataAccessException dae) {
				logger.debug(dae);
			}
			gcdCustomer.setRequestSeq(finoneCust.getRequestSeq()+1);
		}

		logger.debug("Leaving");
	}

	private void updateFailStatus(GcdCustomer gcdCustomer) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE GCDCUSTOMERS  SET StatusFromFinnOne = :StatusFromFinnOne, RejectionReason = :RejectionReason, IsSuccess = 0");
		sql.append(" Where custId =:custId and RequestSeq =:RequestSeq");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(gcdCustomer);
		this.namedJdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	private void updateSuccessStatus(GcdCustomer gcdCustomer) {
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE GCDCUSTOMERS SET StatusFromFinnOne = :StatusFromFinnOne, RejectionReason = :RejectionReason, FinnCustId = :FinnCustId, IsSuccess = 1");
		sql.append(" Where custId = :custId and RequestSeq =:RequestSeq");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(gcdCustomer);
		this.namedJdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

}
