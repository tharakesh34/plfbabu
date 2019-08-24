
package com.pennant.backend.dao.ckyc.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.ckyc.CKYCDAO;
import com.pennant.backend.model.cky.CKYCDtl20;
import com.pennant.backend.model.cky.CKYCDtl30;
import com.pennant.backend.model.cky.CKYCDtl60;
import com.pennant.backend.model.cky.CKYCDtl70;
import com.pennant.backend.model.cky.CKYCHeader;
import com.pennant.backend.model.cky.CKYCLog;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class CKYCDAOImpl extends SequenceDao<CKYCHeader> implements CKYCDAO {
	private static Logger logger = Logger.getLogger(CKYCDAOImpl.class);

	public CKYCDAOImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public CKYCDtl20 getDtl20(long custId) {
		logger.debug("Entering");
		CKYCDtl20 ckycdtl20 = new CKYCDtl20();
		ckycdtl20.setCustId(custId);
		StringBuilder selectSql = new StringBuilder("Select ");
		selectSql.append(
				"   custCif, recordtype, lineno, applicationtype, branchcode, nameupdflag, personalupdflag, addrupdflag, contactUpdFlag, remarksupdflag, kycupdflag, identityupdflag,");
		selectSql.append(
				"   relatedupdflag, controlpersonflag, imgUpdFlag, constitutiontype, accholderflag, accholdertype, acctype, ckycNo, custsalutationcode, custfname, custmname, ");
		selectSql.append(
				"  custlname, custfullname, custmaidensalutationcode, custmaidenfname, custmaidenmname, custmaidenlname, custmaidenfullname, fatherorspouse, fathersalutation, ");
		selectSql.append(
				"  fatherorspousefirstname, fatherorspousemiddlename, fatherorspouselastname, custfatherorSpouseFullName, mothersalutationcode, motherFName, motherMName, ");
		selectSql.append(
				" motherLName, motherfullname, custgendercode, custmaritalsts, custnationality, occupationtype, custdob, placeofincorporation, dtCommencementBussiness, countryofincorporation, ");
		selectSql.append(
				" countryofresidenceaspertaxlaw, otherconstidtype, tin, tinissuingcountry, custcrcpr, resstatus, custrestaxjurioutsideindiaflag, juriresidence, juritin, juricob, ");
		selectSql.append(
				" juripob, pmtaddrtype, pmtaddrline1, pmtaddrline2, pmtaddrline3, pmtaddrcity, pmtaddrdistrict, pmtaddrprovince, pmtaddrcountry, pmtaddrzip, pmtPofAddrsubmit, ");
		selectSql.append(
				"  pmtPofAddrsubmit1, pmtaddrsamelocalflag, localaddrtype, localaddrline1, localaddrline2, localaddrline3, localaddrcity, localaddrdistrict, localaddrprovince, ");
		selectSql.append(
				" localaddrcountry, localaddrzip, localPofAddrsubmit, juriaddrsamepmtorlocalflag, juriaddrtype,juriaddrline1, juriaddrline2, juriaddrline3, ");
		selectSql.append(
				" juriaddrcity, juriaddrprovince, juriAddrCountry, juriaddrzip, juriPofAddrsubmit, resphonecode, resphoneno, offphonecode, offphoneno, mobphonecode, ");
		selectSql.append(
				" mobphoneno, faxphonecode, faxphoneno, emailid, remarks, kycdod, kycpod, kycverificationdate, kycdoctype, kycvname, kycvdesignation, kycvbranch, ");
		selectSql.append(
				" kycEMPCode, kycorgname, kycorgcode, noiddetail, norelatedpeople, nocontrollingpersonoutsideind, nolocaladdr, noimg, errorcode, filler1, filler2, filler3, filler4 ");
		selectSql.append(" from ckycdtl20 where custid =:custId ");

		logger.debug("selectSql:" + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ckycdtl20);
		RowMapper<CKYCDtl20> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CKYCDtl20.class);

		try {
			ckycdtl20 = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			ckycdtl20 = null;
		}
		logger.debug("Leaving");
		return ckycdtl20;
	}

	@Override
	public List<CKYCDtl30> getDtls30(long custId) {
		logger.debug("Entering");
		CKYCDtl30 dtl30 = new CKYCDtl30();
		dtl30.setCustId(custId);
		int count = 0;
		StringBuilder selectSql = new StringBuilder("Select  ");
		selectSql.append(" custid, recordtype, lineno, idtype, idno, expdate, idproofsubmit, ");
		selectSql.append(" idvstatus, filler1, filler2, filler3, filler4 ");
		selectSql.append(" from ckycdtl30 where custId =:custId");
		logger.debug("selectSql:" + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dtl30);
		RowMapper<CKYCDtl30> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CKYCDtl30.class);
		List<CKYCDtl30> ckycDtl30 = new ArrayList<>();
		try {
			ckycDtl30 = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(Literal.EXCEPTION + e);
		}
		logger.debug("Leaving");
		return ckycDtl30;
	}

	@Override
	public List<CKYCDtl60> getDtl60(long custId) {
		logger.debug("Entering");
		CKYCDtl60 dtls60 = new CKYCDtl60();
		dtls60.setCustId(custId);
		StringBuilder selectSql = new StringBuilder("Select ");
		selectSql.append(" custid, recordtype, lineno, branchcode, addrType, localaddrline1, localaddrline2, ");
		selectSql.append(
				" localaddrline3, localaddrcity, localaddrdistrict, localaddrprovince, localaddrcountry, localaddrzip, ");
		selectSql.append(
				" localpofaddrsubmit, resphonecode, resphoneno, offphonecode, offphoneno, mobphonecode, mobphoneno, ");
		selectSql.append(" faxphonecode, faxPhoneNo, emailid, addrdod, addrpod, filler1, filler2, filler3, filler4 ");
		selectSql.append(" from ckycdtl60 where custid =:custId");

		logger.debug("selectSql:" + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dtls60);
		RowMapper<CKYCDtl60> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CKYCDtl60.class);
		List<CKYCDtl60> ckycDtl60 = new ArrayList<>();
		try {
			ckycDtl60 = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			// TODO: handle exception
		}
		logger.debug("Leaving");
		return ckycDtl60;

	}

	@Override
	public List<CKYCDtl70> getDtl70(long custId) {
		// TODO Auto-generated method stub
		logger.debug("Entering");
		CKYCDtl70 dtls70 = new CKYCDtl70();
		dtls70.setCustId(custId);
		StringBuilder selectSql = new StringBuilder("Select ");
		selectSql.append(" recordtype, lineno, imgfoldernm, imgtype, gobalorlocal, ");
		selectSql.append(" branchcode, custDocImage, filler1, filler2, filler3, filler4");
		selectSql.append("  from Ckycdtl70 where custId =:custId");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dtls70);
		RowMapper<CKYCDtl70> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CKYCDtl70.class);
		List<CKYCDtl70> ckycDtl70 = new ArrayList<>();
		try {
			ckycDtl70 = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			// TODO: handle exception
		}
		logger.debug("Leaving");
		return ckycDtl70;
	}

	@Override
	public int saveDtl20(CKYCDtl20 dtl20) {
		logger.debug("Entering");
		int count = 0;
		StringBuilder insertSql = new StringBuilder("insert into ckycdtl20");
		insertSql.append(
				" ( custid, custcif, recordtype, lineno, applicationtype, branchcode, nameupdflag, personalupdflag, addrupdflag, contactUpdFlag, remarksupdflag, kycupdflag, identityupdflag,");
		insertSql.append(
				"   relatedupdflag, controlpersonflag, imgUpdFlag, constitutiontype, accholderflag, accholdertype, acctype, ckycNo, custsalutationcode, custfname, custmname, ");
		insertSql.append(
				"  custlname, custfullname, custmaidensalutationcode, custmaidenfname, custmaidenmname, custmaidenlname, custmaidenfullname, fatherorspouse, fatherSalutation, ");
		insertSql.append(
				"  fatherorspousefirstname, fatherorspousemiddlename, fatherorspouselastname, custfatherorSpouseFullName, mothersalutationcode, motherFName, motherMName, ");
		insertSql.append(
				" motherLName, motherfullname, custgendercode, custmaritalsts, custnationality, occupationtype, custdob, placeofincorporation, dtCommencementBussiness, countryofincorporation, ");
		insertSql.append(
				" countryofResidenceasPerTaxLaw, otherconstidtype, tin, tinissuingcountry, custcrcpr, resstatus, custrestaxjurioutsideindiaflag, juriresidence, juritin, juricob, ");
		insertSql.append(
				" juripob, pmtaddrtype, pmtaddrline1, pmtAddrLine2, pmtaddrline3, pmtaddrcity, pmtaddrdistrict, pmtaddrprovince, pmtaddrcountry, pmtaddrzip, pmtPofAddrsubmit, ");
		insertSql.append(
				"  pmtPofAddrsubmit1, pmtaddrsamelocalflag, localaddrtype, localaddrline1, localaddrline2, localaddrline3, localaddrcity, localaddrdistrict, localaddrprovince, ");
		insertSql.append(
				" localaddrcountry, localaddrzip, localPofAddrsubmit, juriaddrsamepmtorlocalflag, juriaddrtype,juriaddrline1, juriaddrline2, juriaddrline3, ");
		insertSql.append(
				" juriaddrcity, juriaddrprovince, juriAddrCountry, juriaddrzip, juriPofAddrsubmit, resphonecode, resphoneno, offphonecode, offphoneno, mobphonecode, ");
		insertSql.append(
				" mobphoneno, faxphonecode, faxphoneno, emailid, remarks, kycdod, kycpod, kycverificationdate, kycdoctype, kycvname, kycvdesignation, kycvbranch, ");
		insertSql.append(
				" kycEMPCode, kycOrgName, kycorgcode, noiddetail, norelatedpeople, nocontrollingpersonoutsideind, nolocaladdr, noimg, errorcode, filler1, filler2, filler3, filler4) ");
		insertSql.append(
				" values ( :custId, :custCif, :recordType, :lineNo, :applicationType, :branchCode, :nameUpdFlag, :personalUpdFlag, :addrUpdFlag, :contactUpdFlag, :remarksUpdFlag, :kycUpdFlag, :identityUpdFlag, ");
		insertSql.append(
				"   :relatedUpdFlag, :controlPersonFlag, :imgUpdFlag, :constitutionType, :accHolderFlag, :accHolderType, :accType, :ckycNo, :custSalutationCode, :custFName, :custMName, ");
		insertSql.append(
				"  :custLName, :custFullName, :custMaidenSalutationCode, :custMaidenFName, :custMaidenMName, :custMaidenLName, :custMaidenFullName, :fatherOrSpouse, :fatherSalutation, ");
		insertSql.append(
				"  :fatherOrSpouseFirstName, :fatherOrSpouseMiddleName, :fatherOrSpouseLastName, :custfatherorSpouseFullName, :motherSalutationCode, :motherFName, :motherMName, ");
		insertSql.append(
				" :motherLName, :MotherFullName, :custGenderCode, :custMaritalsts, :custNationality, :occupationType, :custDob, :placeOfIncorporation, :dtCommencementBussiness, :countryOfIncorporation, ");
		insertSql.append(
				" :countryofResidenceasPerTaxLaw, :OtherConstIdType, :tin, :tinIssuingCountry, :custcrcpr, :resStatus, :custrestaxJurioutsideIndiaFlag, :juriresidence, :juriTin, :juriCob, ");
		insertSql.append(
				" :juriPob, :pmtAddrType, :pmtAddrLine1, :pmtAddrLine2, :pmtAddrLine3, :pmtAddrCity, :pmtAddrDistrict, :pmtAddrProvince, :pmtAddrCountry, :pmtAddrZip, :pmtPofAddrsubmit, ");
		insertSql.append(
				"  :pmtPofAddrsubmit1, :pmtAddrsameLocalFlag, :localAddrType, :localAddrLine1, :localAddrLine2, :localAddrLine3, :localAddrCity, :localAddrDistrict, :localAddrProvince, ");
		insertSql.append(
				" :localAddrCountry, :localAddrZip, :localPofAddrsubmit, :juriaddrsamepmtorLocalFlag, :juriAddrType, :juriAddrLine1, :juriAddrLine2, :juriAddrLine3, ");
		insertSql.append(
				" :juriAddrCity, :juriAddrProvince, :juriAddrCountry, :juriAddrZip, :juriPofAddrsubmit, :resPhoneCode, :resPhoneNo, :offPhoneCode, :offPhoneNo, :mobPhoneCode, ");
		insertSql.append(
				" :mobPhoneNo, :faxPhoneCode, :faxPhoneNo, :emailId, :remarks, :kycDod, :kycPod, :kycVerificationDate, :kycDocType, :kycVName, :kycVDesignation, :kycVBranch, ");
		insertSql.append(
				" :kycEMPCode, :kycOrgName, :kycOrgCode, :noIdDetail, :noRelatedPeople, :noControllingPersonOutsideInd, :noLocalAddr, :noImg, :errorCode, :filler1, :filler2, :filler3, :filler4) ");

		logger.debug("insertSql:" + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dtl20);
		try {
			count = this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			// TODO:handle exception
		}
		logger.debug("Leaving");
		return count;
	}

	@Override
	public int saveDtl30(CKYCDtl30 dtl30) {
		logger.debug("Entering");

		int count = 0;
		StringBuilder insertSql = new StringBuilder("insert into ckycdtl30");
		insertSql.append(" (custid, recordtype, lineno, idtype, idno, expdate, idproofsubmit, ");
		insertSql.append(" idvstatus, filler1, filler2, filler3, filler4) ");
		insertSql.append(" values( :custId, :recordType, :lineNo, :idType, :idNo, :expDate, ");
		insertSql.append("  :idproofsubmit, :idVStatus, :filler1, :filler2, :filler3, :filler4) ");

		logger.debug("insertSql:" + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dtl30);
		try {
			count = this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			// TODO:handle exception
		}
		logger.debug("Leaving");
		return count;
	}

	@Override
	public int saveDtl60(CKYCDtl60 dtl60) {
		logger.debug("Entering");
		int count = 0;
		StringBuilder insertSql = new StringBuilder("insert into ckycdtl60");
		insertSql.append(" ( custid, recordtype, lineno, branchcode, addrType, localaddrline1, localaddrline2, ");
		insertSql.append(
				" localaddrline3, localaddrcity, localaddrdistrict, localaddrprovince, localaddrcountry, localaddrzip, ");
		insertSql.append(
				" localpofaddrsubmit, resphonecode, resphoneno, offphonecode, offphoneno, mobphonecode, mobphoneno, ");
		insertSql.append(" faxphonecode, faxPhoneNo, emailid, addrdod, addrpod, filler1, filler2, filler3, filler4) ");
		insertSql.append(
				" values( :custId, :recordType, :lineNo, :branchCode, :addrType, :localAddrLine1, :localAddrLine2, ");
		insertSql.append(
				" :localAddrLine3, :localAddrCity, :localAddrDistrict, :localAddrProvince, :localAddrCountry, :localAddrZip, ");
		insertSql.append(
				" :localPofAddrSubmit, :resPhoneCode, :resPhoneNo, :offPhoneCode, :offPhoneNo, :mobPhoneCode, :mobPhoneNo, ");
		insertSql.append(
				" :faxPhoneCode, :faxPhoneNo, :emailId, :addrDod, :addrPod , :filler1, :filler2, :filler3, :filler4)");

		logger.debug("insertSql:" + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dtl60);
		try {
			count = this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			// TODO:handle exception
		}
		logger.debug("Leaving");
		return count;

	}

	@Override
	public int saveDtl70(CKYCDtl70 dtl70) {
		// TODO Auto-generated method stub
		logger.debug("Entering");
		int count = 0;
		StringBuilder insertSql = new StringBuilder("insert into ckycdtl70");
		insertSql.append(" ( custId, recordtype, lineno, imgfoldernm, imgtype, gobalorlocal, ");
		insertSql.append(" branchcode, custDocImage, filler1, filler2, filler3, filler4)");
		insertSql.append(" values( :custId, :recordType, :lineNo,:imgFolderNm, :imgType,:gobalOrLocal,");
		insertSql.append(" :branchCode , :custDocImage, :filler1, :filler2, :filler3, :filler4)");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dtl70);

		try {
			count = this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			// TODO:handle exception
		}
		logger.debug("Leaving");
		return count;
	}

	@Override
	public boolean cleanData() {
		logger.debug("Entering");

		try {
			String sqlCkycDtl20 = "Truncate TABLE ckycdtl20 ";
			jdbcTemplate.update(sqlCkycDtl20.toString(), new HashMap<String, Object>());
			String sqlCkycDtl30 = "Truncate TABLE ckycdtl30 ";
			jdbcTemplate.update(sqlCkycDtl30.toString(), new HashMap<String, Object>());
			String sqlCkycDtl60 = "Truncate TABLE ckycdtl60 ";
			jdbcTemplate.update(sqlCkycDtl60.toString(), new HashMap<String, Object>());
			String sqlCkycDtl70 = "Truncate TABLE ckycdtl70 ";
			jdbcTemplate.update(sqlCkycDtl70.toString(), new HashMap<String, Object>());

		} catch (Exception e) {
			System.out.println("Exception e" + e);
			logger.debug("Leaving");
			return false;
		}
		logger.debug("Leaving");

		return true;
	}

	@Override
	public String getCkycNo(long custId) {
		Map<String, Object> mapParam = new HashMap<>();
		String ckycNo = null;
		mapParam.put("custId", custId);
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT ckycNo from CKYCLOG");
		selectSql.append(" Where custId =:custId and ckycNo IS NOT NULL");
		try {
			ckycNo = jdbcTemplate.queryForObject(selectSql.toString(), mapParam, String.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
		}
		logger.debug("Leaving");
		return ckycNo;
	}

	@Override
	public Customer getCustomerDetail(long id) {
		Customer customer = new Customer();
		customer.setId(id);
		StringBuilder selectSql = new StringBuilder("SELECT CustID, CustCIF,custDftBranch,");
		selectSql.append(
				" custFName, custMName, CustLName, custshrtname,custSalutationCode, CUSTMOTHERMAIDEN, custFNameLclLng, custMNameLclLng,");
		selectSql.append(" custLNameLclLng, CUSTGENDERCODE, custMaritalsts, CUSTNATIONALITY, custdob, custcrcpr,");
		selectSql.append("  custtypecode ,lovDescCustProfessionName ,lastMntOn ");
		selectSql.append(" FROM  Customers_aview");
		selectSql.append(" Where CustID =:CustID");

		logger.debug("selectSql:" + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);

		try {
			customer = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION + e);
			customer = null;
		}
		logger.debug("Leaving");
		return customer;
	}

	@Override
	public List<CustomerAddres> getCustomerAddresByCustomer(long custId, String ckycNo) {

		logger.debug("Entering");
		CustomerAddres customerAddres = new CustomerAddres();
		customerAddres.setId(custId);
		if (ckycNo == null) {
			StringBuilder selectSql = new StringBuilder();
			selectSql.append(
					" SELECT   CustAddrType As custAddrType,CustAddrLine1 As custAddrLine1, CustAddrLine2 As custAddrLine1,CustAddrLine3 As custAddrLine1,CustAddrCity As custAddrCity,CustDistrict As custDistrict ,");
			selectSql.append(
					" CustAddrProvince As custAddrProvince,CustAddrCountry As custAddrCountry , CustAddrZIP As custAddrZIP");
			selectSql.append(" FROM CustomerAddresses");
			selectSql.append(" Where CustID = :custID order by custaddrpriority desc");

			logger.trace("selectSql:" + selectSql.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAddres);
			RowMapper<CustomerAddres> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(CustomerAddres.class);
			List<CustomerAddres> customerAddresses = null;
			try {
				customerAddresses = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION + e);
				// TODO: handle exception
			}
			logger.debug("Leaving");
			return customerAddresses;
		} else {
			StringBuilder selectSql = new StringBuilder();
			Map<String, Object> mapParam = new HashMap<>();
			mapParam.put("custId", custId);
			mapParam.put("ckycNo", ckycNo);
			selectSql.append(" SELECT addrlastmnton");
			selectSql.append(" FROM  ckycLog");
			selectSql.append(" Where CustID = :custId and ckycNo =:ckycNo");
			Timestamp ckycAddrLastMnton = null;
			try {
				ckycAddrLastMnton = this.jdbcTemplate.queryForObject(selectSql.toString(), mapParam, Timestamp.class);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION + e);
			}
			List<CustomerAddres> customerAddresses = null;
			if (ckycAddrLastMnton != null) {
				customerAddres.setLastMntOn(ckycAddrLastMnton);
				StringBuilder selectAddr = new StringBuilder();
				selectAddr.append(
						" SELECT   CustAddrType As custAddrType,CustAddrLine1 As custAddrLine1, CustAddrLine2 As custAddrLine1,CustAddrLine3 As custAddrLine1,CustAddrCity As custAddrCity,CustDistrict As custDistrict ,");
				selectAddr.append(
						" CustAddrProvince As custAddrProvince,CustAddrCountry As custAddrCountry , CustAddrZIP As custAddrZIP");
				selectAddr.append(" FROM CustomerAddresses");
				selectAddr.append(" Where CustID = :custID and lastmntOn > :lastMntOn order by custaddrpriority desc");

				logger.trace("selectAddr:" + selectAddr.toString());
				SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAddres);
				RowMapper<CustomerAddres> typeRowMapper = ParameterizedBeanPropertyRowMapper
						.newInstance(CustomerAddres.class);
				try {
					customerAddresses = this.jdbcTemplate.query(selectAddr.toString(), beanParameters, typeRowMapper);
				} catch (Exception e) {
					// TODO: handle exception
					logger.error(Literal.EXCEPTION + e);
				}
				logger.debug("Leaving");

			}
			return customerAddresses;
		}

	}

	@Override
	public List<CustomerPhoneNumber> getCustomerPhoneNumberByCustomer(long custId, String ckycNo) {
		logger.debug("Entering");
		CustomerPhoneNumber customerPhoneNumber = new CustomerPhoneNumber();
		customerPhoneNumber.setPhoneCustID(custId);
		if (ckycNo == null) {
			StringBuilder selectSql = new StringBuilder();
			selectSql.append(
					"SELECT   PhoneTypeCode As phoneTypeCode,PhoneCountryCode As phoneCountryCode,PhoneAreaCode As phoneAreaCode, PhoneNumber As phoneNumber");
			selectSql.append(" FROM  CustomerPhoneNumbers");
			selectSql.append(" Where PhoneCustID =:PhoneCustID order by phonetypepriority desc");

			logger.trace("selectSql:" + selectSql.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
			RowMapper<CustomerPhoneNumber> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(CustomerPhoneNumber.class);
			List<CustomerPhoneNumber> customerPhoneNumbers = null;

			try {
				customerPhoneNumbers = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION + e);
				// TODO: handle exception
			}
			logger.debug("Leaving ");
			return customerPhoneNumbers;
		} else {
			StringBuilder selectSql = new StringBuilder();
			Map<String, Object> mapParam = new HashMap<>();
			mapParam.put("custId", custId);
			mapParam.put("ckycNo", ckycNo);
			selectSql.append(" SELECT phoneLastMntOn");
			selectSql.append(" FROM  ckycLog");
			selectSql.append(" Where CustID = :custId and ckycNo =:ckycNo");
			Timestamp ckycPhoneLastMnt = null;
			try {
				ckycPhoneLastMnt = this.jdbcTemplate.queryForObject(selectSql.toString(), mapParam, Timestamp.class);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION + e);
			}
			List<CustomerPhoneNumber> customerPhoneNumbers = null;
			customerPhoneNumber.setLastMntOn(ckycPhoneLastMnt);
			StringBuilder selectPh = new StringBuilder();
			selectPh.append(
					"SELECT   PhoneTypeCode As phoneTypeCode,PhoneCountryCode As phoneCountryCode,PhoneAreaCode As phoneAreaCode, PhoneNumber As phoneNumber");
			selectPh.append(" FROM  CustomerPhoneNumbers");
			selectPh.append(
					" Where PhoneCustID =:PhoneCustID and lastmnton > :lastMntOn order by phonetypepriority desc");

			logger.trace("selectPh:" + selectPh.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
			RowMapper<CustomerPhoneNumber> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(CustomerPhoneNumber.class);
			try {
				customerPhoneNumbers = this.jdbcTemplate.query(selectPh.toString(), beanParameters, typeRowMapper);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION + e);
				// TODO: handle exception
			}
			logger.debug("Leaving ");
			return customerPhoneNumbers;
		}
	}

	@Override
	public List<CustomerEMail> getCustomerEmailByCustomer(long custId, String ckycNo) {
		logger.debug("Entering");
		CustomerEMail customerEMail = new CustomerEMail();
		customerEMail.setId(custId);
		if (ckycNo == null) {
			StringBuilder selectSql = new StringBuilder();
			selectSql.append(" SELECT CustEMail As custEMail");
			selectSql.append(" FROM  CustomerEMails");
			selectSql.append(" Where CustID = :custID  order by custEMailPriority desc");

			logger.trace("selectSql:" + selectSql.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);
			RowMapper<CustomerEMail> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(CustomerEMail.class);
			List<CustomerEMail> customerEmail = null;
			try {
				customerEmail = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(Literal.EXCEPTION + e);

			}
			logger.debug("Leaving");

			return customerEmail;
		} else {
			StringBuilder selectSql = new StringBuilder();
			Map<String, Object> mapParam = new HashMap<>();
			mapParam.put("custId", custId);
			mapParam.put("ckycNo", ckycNo);
			selectSql.append(" SELECT emailLastMntOn");
			selectSql.append(" FROM  ckycLog");
			selectSql.append(" Where CustID = :custId and ckycNo =:ckycNo");
			Timestamp ckycEmailLastMnt = null;
			try {
				ckycEmailLastMnt = this.jdbcTemplate.queryForObject(selectSql.toString(), mapParam, Timestamp.class);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION + e);

			}
			customerEMail.setLastMntOn(ckycEmailLastMnt);
			StringBuilder selectEmail = new StringBuilder();
			selectEmail.append(" SELECT CustEMail As custEMail");
			selectEmail.append(" FROM  CustomerEMails");
			selectEmail.append(" Where CustID = :custID and lastMntOn > :lastMntOn  order by custEMailPriority desc");

			logger.trace("selectEmail:" + selectEmail.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);
			RowMapper<CustomerEMail> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(CustomerEMail.class);
			List<CustomerEMail> customerEmail = null;
			try {
				customerEmail = this.jdbcTemplate.query(selectEmail.toString(), beanParameters, typeRowMapper);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION + e);
			}
			logger.debug("Leaving");

			return customerEmail;
		}
	}

	@Override
	public List<CustomerDocument> getCustomerId(long custId, String ckycNo) {
		logger.debug("Entering");
		CustomerDocument customerDoc = new CustomerDocument();
		customerDoc.setId(custId);
		if (ckycNo == null) {
			StringBuilder selectSql = new StringBuilder();
			selectSql.append(
					" SELECT D.CustID, D.CustDocRcvdOn, D.CustDocIssuedCountry, D.CustDocIssuedOn, D.lovDescCustDocCategory, D.CustDocSysName,");
			selectSql.append(
					"  D.CustDocTitle , D.CustDocName, D.CustDocExpDate, D.CustDocIsVerified, I.DOCIMAGE AS CUSTDOCIMAGE");
			selectSql.append(" FROM  CustomerDocuments_Aview D, DOCUMENTMANAGER I");
			selectSql.append(" Where CustID = :custID AND D.DOCREFID=I.ID");

			logger.trace("selectSql:" + selectSql.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDoc);
			RowMapper<CustomerDocument> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(CustomerDocument.class);
			List<CustomerDocument> customerDocument = null;
			try {
				customerDocument = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION + e);
			}
			logger.debug("Leaving");

			return customerDocument;
		} else {
			StringBuilder selectSql = new StringBuilder();
			Map<String, Object> mapParam = new HashMap<>();
			mapParam.put("custId", custId);
			mapParam.put("ckycNo", ckycNo);
			selectSql.append(" SELECT docLastMntOn");
			selectSql.append(" FROM  ckycLog");
			selectSql.append(" Where CustID = :custId and ckycNo =:ckycNo");
			Timestamp docLastMntOn = null;
			try {
				docLastMntOn = this.jdbcTemplate.queryForObject(selectSql.toString(), mapParam, Timestamp.class);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION + e);
			}
			customerDoc.setLastMntOn(docLastMntOn);
			StringBuilder selectDoc = new StringBuilder();
			selectDoc.append(
					" SELECT D.CustID, D.CustDocRcvdOn, D.CustDocIssuedCountry, D.CustDocIssuedOn, D.lovDescCustDocCategory, D.CustDocSysName,");
			selectDoc.append(
					"  D.CustDocTitle , D.CustDocName, D.CustDocExpDate, D.CustDocIsVerified, I.DOCIMAGE AS CUSTDOCIMAGE");
			selectDoc.append(" FROM  CustomerDocuments_Aview As D, DOCUMENTMANAGER As I");
			selectDoc.append(" Where CustID = :custID AND D.DOCREFID=I.ID AND D.LASTMNTON > :lastMntOn ");

			logger.trace("selectDoc:" + selectDoc.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDoc);
			RowMapper<CustomerDocument> typeRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(CustomerDocument.class);
			List<CustomerDocument> customerDocument = null;

			try {
				customerDocument = this.jdbcTemplate.query(selectDoc.toString(), beanParameters, typeRowMapper);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION + e);
			}
			logger.debug("Leaving");

			return customerDocument;

		}
	}

	@Override
	public int saveFile(CKYCLog file) {
		logger.debug("Enterning");
		int count = 0;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("custId", file.getCustId());

		StringBuilder selectAddr = new StringBuilder("SELECT LastMntOn");
		selectAddr.append(
				" FROM (SELECT lastmntOn,ROW_NUMBER() OVER(ORDER BY lastmntOn desc) As RowNum   FROM customeraddresses  Where CustID  =:custId ) ");
		selectAddr.append(" As custAddr where rownum=1");
		logger.debug("selectAddr: " + selectAddr.toString());
		Timestamp addrLastMnt = null;
		try {
			addrLastMnt = this.jdbcTemplate.queryForObject(selectAddr.toString(), paramMap, Timestamp.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION + e);
		}
		if (addrLastMnt != null) {
			file.setAddrLastMntOn(addrLastMnt);
		}
		StringBuilder selectPhone = new StringBuilder("SELECT LastMntOn");
		selectPhone.append(
				" FROM (SELECT lastmntOn,ROW_NUMBER() OVER(ORDER BY lastmntOn desc) As RowNum   FROM customerphonenumbers  Where phoneCustID  =:custId ) ");
		selectPhone.append(" As phone where rownum=1");

		logger.debug("selectPhone: " + selectPhone.toString());
		Timestamp phoneLastMnt = null;
		try {
			phoneLastMnt = this.jdbcTemplate.queryForObject(selectPhone.toString(), paramMap, Timestamp.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION + e);
		}
		if (phoneLastMnt != null) {
			file.setPhoneLastMntOn(phoneLastMnt);
		}
		StringBuilder selectEmail = new StringBuilder("SELECT LastMntOn");
		selectEmail.append(
				" FROM (SELECT lastmntOn,ROW_NUMBER() OVER(ORDER BY lastmntOn desc) As RowNum   FROM customeremails  Where custId  =:custId ) ");
		selectEmail.append(" As email where rownum=1");
		logger.debug("selectEmail: " + selectEmail.toString());
		Timestamp emailLastMnt = null;
		try {
			emailLastMnt = this.jdbcTemplate.queryForObject(selectPhone.toString(), paramMap, Timestamp.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION + e);
		}
		if (emailLastMnt != null) {
			file.setEmailLastMntOn(emailLastMnt);
		}

		StringBuilder selectdoc = new StringBuilder("SELECT LastMntOn");
		selectdoc.append(
				" FROM (SELECT lastmntOn,ROW_NUMBER() OVER(ORDER BY lastmntOn desc) As RowNum   FROM Customerdocuments  Where custId  =:custId ) ");
		selectdoc.append(" As doc where rownum=1");
		logger.debug("selectdoc: " + selectdoc.toString());
		Timestamp docLastMnt = null;
		try {
			docLastMnt = this.jdbcTemplate.queryForObject(selectdoc.toString(), paramMap, Timestamp.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION + e);
		}

		if (docLastMnt != null) {
			file.setDocLastMntOn(docLastMnt);
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(
				"INSERT INTO CKYCLOG ( custid, custCif, custsalutationcode, custfname, custmname,  custlname, custfatherName,");
		insertSql.append(" custgendercode, custmaritalsts, custnationality, occupationtype,  custdob, ");
		insertSql.append(" addrLastMntOn, fileName, rowNo, emailLastMntOn,  phoneLastMntOn, docLastMntOn  )  ");
		insertSql.append(
				" values( :custId, :custCif, :custsalutationcode, :custfname, :custmname,  :custlname,  :custfatherName, ");
		insertSql.append(
				" :custgendercode, :custmaritalsts, :custnationality, :occupationtype,  :custdob, :addrLastMntOn, :fileName, :rowNo, ");
		insertSql.append(" :emailLastMntOn, :phoneLastMntOn,  :docLastMntOn )  ");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(file);

		try {
			count = this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
		}

		return count;

	}

	@Override
	public CKYCLog applicantNameFlag(long custId, String ckycNo) {
		logger.debug("Enterning");
		int count = 02;
		CKYCLog ckycNameDetails = new CKYCLog();
		ckycNameDetails.setCustId(custId);
		ckycNameDetails.setCkycNo(ckycNo);

		StringBuilder selectCkyc = new StringBuilder(" SELECT custSalutationCode, custFName, custMName, custLName ");
		selectCkyc.append("  FROM ckycLog  Where custId  =:custId and  ckycNo =:ckycNo ");
		logger.debug("selectCkyc: " + selectCkyc.toString());
		SqlParameterSource beanParametersFlag = new BeanPropertySqlParameterSource(ckycNameDetails);
		RowMapper<CKYCLog> typeRowMapperFlag = ParameterizedBeanPropertyRowMapper.newInstance(CKYCLog.class);
		try {
			ckycNameDetails = this.jdbcTemplate.queryForObject(selectCkyc.toString(), beanParametersFlag,
					typeRowMapperFlag);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			ckycNameDetails = null;
		}
		Customer customer = new Customer();
		customer.setId(custId);
		StringBuilder selectCust = new StringBuilder(
				"SELECT custFName, custMName,custshrtname ,CustLName, custSalutationCode");
		selectCust.append(" FROM  customers");
		selectCust.append(" Where CustID =:CustID");
		logger.debug("selectCust: " + selectCust.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
		try {
			customer = this.jdbcTemplate.queryForObject(selectCust.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			customer = null;
		}
		CKYCLog ckycLog = null;
		if (customer != null) {
			if (!StringUtils.equalsIgnoreCase(customer.getCustSalutationCode(), ckycNameDetails.getCustsalutationcode())
					|| !StringUtils.equalsIgnoreCase(customer.getCustFName(), ckycNameDetails.getCustfname())
					|| !StringUtils.equalsIgnoreCase(customer.getCustMName(), ckycNameDetails.getCustmname())
					|| !StringUtils.equalsIgnoreCase(customer.getCustLName(), ckycNameDetails.getCustlname())) {
				ckycLog = new CKYCLog();
				ckycLog.setCustsalutationcode(customer.getCustSalutationCode());
				ckycLog.setCustfname(customer.getCustFName());
				ckycLog.setCustmname(customer.getCustMName());
				ckycLog.setCustlname(customer.getCustLName());
				ckycLog.setCustFullName(customer.getCustShrtName());
				// updateNameValue(ckycLog);
			}
		}

		return ckycLog;
	}

	@Override
	public CKYCLog personalDetailFlag(long custId, String ckycNo) {
		logger.debug("Enterning");
		int count = 02;
		CKYCLog ckycPersonalDtls = new CKYCLog();
		ckycPersonalDtls.setCustId(custId);
		ckycPersonalDtls.setCkycNo(ckycNo);
		StringBuilder selectPerDtls = new StringBuilder(
				" SELECT custfatherName, custgendercode, custmaritalsts, custnationality, occupationtype,  custdob ");
		selectPerDtls.append(" FROM ckycLog  Where custId  =:custId and  ckycNo =:ckycNo ");
		logger.debug("selectFlag: " + selectPerDtls.toString());
		SqlParameterSource beanParametersFlag = new BeanPropertySqlParameterSource(ckycPersonalDtls);
		RowMapper<CKYCLog> typeRowMapperFlag = ParameterizedBeanPropertyRowMapper.newInstance(CKYCLog.class);
		try {
			ckycPersonalDtls = this.jdbcTemplate.queryForObject(selectPerDtls.toString(), beanParametersFlag,
					typeRowMapperFlag);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			ckycPersonalDtls = null;
		}

		Customer customer = new Customer();
		customer.setId(custId);
		StringBuilder selectCust = new StringBuilder();
		selectCust.append("  Select CUSTMOTHERMAIDEN, CUSTGENDERCODE, custMaritalsts, CUSTNATIONALITY, custdob,");
		selectCust.append("  lovDescCustProfessionName  ");
		selectCust.append(" FROM  Customers_aview where CustID =:CustID");
		CKYCLog ckycFile = null;

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
		try {
			customer = this.jdbcTemplate.queryForObject(selectCust.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			customer = null;
		}
		if (customer != null) {
			String profression = null;
			String maritials = null;
			if (customer.getLovDescCustProfessionName() != null) {
				profression = getCode("CKYCProfession", customer.getLovDescCustProfessionName());
			}
			if (customer.getCustMaritalSts() != null)
				maritials = getCode("CKYCMARITALSTATUS", customer.getCustMaritalSts());

			if (!StringUtils.equalsIgnoreCase(customer.getCustMotherMaiden(), ckycPersonalDtls.getCustfatherName())
					|| !StringUtils.equalsIgnoreCase(customer.getCustGenderCode(), ckycPersonalDtls.getCustgendercode())
					|| !StringUtils.equalsIgnoreCase(maritials, ckycPersonalDtls.getCustmaritalsts())
					|| !StringUtils.equalsIgnoreCase(profression, ckycPersonalDtls.getOccupationtype())
					|| !StringUtils.equalsIgnoreCase(customer.getCustNationality(),
							ckycPersonalDtls.getCustnationality())
					|| !customer.getCustDOB().equals(ckycPersonalDtls.getCustdob())) {
				ckycFile = new CKYCLog();
				ckycFile.setCustfatherName(customer.getCustMotherMaiden());

				ckycFile.setCustgendercode(customer.getCustGenderCode());

				ckycFile.setCustmaritalsts(maritials);

				ckycFile.setOccupationtype(profression);
				ckycFile.setCustnationality(customer.getCustNationality());
				ckycFile.setCustdob(customer.getCustDOB());
			} // updatePersonalValue(ckycFlag);

		}

		return ckycFile;

	}

	@Override
	public int addressDetailFlag(long custId, String ckycNo) {
		int count = 02;
		StringBuilder selectSql = new StringBuilder();
		Map<String, Object> mapParam = new HashMap<>();
		mapParam.put("custId", custId);
		mapParam.put("ckycNo", ckycNo);
		selectSql.append(" SELECT addrlastmnton");
		selectSql.append(" FROM  ckycLog");
		selectSql.append(" Where CustID = :custId and ckycNo =:ckycNo");
		Timestamp ckycAddrLastMnt = null;
		try {
			ckycAddrLastMnt = this.jdbcTemplate.queryForObject(selectSql.toString(), mapParam, Timestamp.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
		}
		StringBuilder selectAddr = new StringBuilder();
		selectAddr.append(" SELECT lastmnton");
		selectAddr.append(
				" FROM (SELECT lastmntOn,ROW_NUMBER() OVER(ORDER BY lastmntOn desc) As RowNum   FROM CustomerAddresses  Where custId  =:custId ) ");
		selectAddr.append(" As addr where rownum=1");
		Timestamp custAddrLastMnt = null;
		try {
			custAddrLastMnt = this.jdbcTemplate.queryForObject(selectAddr.toString(), mapParam, Timestamp.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
		}
		if (custAddrLastMnt.after(ckycAddrLastMnt)) {
			count = 01;
		}
		return count;
	}

	@Override
	public int contactFlag(long custId, String ckycNo) {
		int count = 02;
		Map<String, Object> mapParam = new HashMap<>();
		mapParam.put("custId", custId);
		mapParam.put("ckycNo", ckycNo);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT phonelastmnton");
		selectSql.append(" FROM  ckycLog");
		selectSql.append(" Where CustID = :custId and ckycNo =:ckycNo");

		Timestamp ckycPhoneLastMnt = null;
		try {
			ckycPhoneLastMnt = this.jdbcTemplate.queryForObject(selectSql.toString(), mapParam, Timestamp.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
		}
		StringBuilder selectPhone = new StringBuilder();
		selectPhone.append(" SELECT lastmnton");
		selectPhone.append(
				" FROM (SELECT lastmntOn,ROW_NUMBER() OVER(ORDER BY lastmntOn desc) As RowNum   FROM CustomerPhoneNumbers  Where PhoneCustID  =:custId ) ");
		selectPhone.append(" As phone where rownum=1");
		Timestamp custPhLastMnt = null;
		try {
			custPhLastMnt = this.jdbcTemplate.queryForObject(selectPhone.toString(), mapParam, Timestamp.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION + e);
		}

		if (custPhLastMnt.after(ckycPhoneLastMnt))
			count = 01;

		return count;
	}

	@Override
	public int emailFlag(long custId, String ckycNo) {
		int count = 02;
		Map<String, Object> mapParam = new HashMap<>();
		mapParam.put("custId", custId);
		mapParam.put("ckycNo", ckycNo);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT emaillastmnton");
		selectSql.append(" FROM  ckycLog");
		selectSql.append(" Where CustID = :custId and ckycNo =:ckycNo");

		Timestamp ckycEmailLastMnt = null;
		try {
			ckycEmailLastMnt = this.jdbcTemplate.queryForObject(selectSql.toString(), mapParam, Timestamp.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
		}
		StringBuilder selectEmail = new StringBuilder();
		selectEmail.append(" SELECT lastmnton");
		selectEmail.append(
				" FROM (SELECT lastmntOn,ROW_NUMBER() OVER(ORDER BY lastmntOn desc) As RowNum   FROM CustomerEmails  Where custID  =:custId ) ");
		selectEmail.append(" As email where rownum=1");
		Timestamp custEmailLastMnt = null;
		try {
			custEmailLastMnt = this.jdbcTemplate.queryForObject(selectEmail.toString(), mapParam, Timestamp.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION + e);

		}
		if (custEmailLastMnt.after(ckycEmailLastMnt)) {
			count = 01;
		}
		return count;
	}

	@Override
	public int imgDtlFlag(long custId, String ckycNo) {
		int count = 02;
		StringBuilder selectSql = new StringBuilder();
		Map<String, Object> mapParam = new HashMap<>();
		mapParam.put("custId", custId);
		mapParam.put("ckycNo", ckycNo);
		selectSql.append(" SELECT doclastmnton  ");
		selectSql.append(" FROM  ckycLog");
		selectSql.append(" Where CustID = :custId and ckycNo =:ckycNo");
		Timestamp ckycDocLastMnt = null;
		try {
			ckycDocLastMnt = this.jdbcTemplate.queryForObject(selectSql.toString(), mapParam, Timestamp.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
		}
		StringBuilder selectImg = new StringBuilder();
		selectImg.append(" SELECT lastmnton");
		selectImg.append(
				" FROM (SELECT lastmntOn,ROW_NUMBER() OVER(ORDER BY lastmntOn desc) As RowNum   FROM customerdocuments  Where custId  =:custId ) ");
		selectImg.append(" As doc where rownum=1");
		Timestamp custDocLastMnt = null;
		logger.debug("selectImg" + selectImg.toString());
		try {
			custDocLastMnt = this.jdbcTemplate.queryForObject(selectImg.toString(), mapParam, Timestamp.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
		}

		if (custDocLastMnt.after(ckycDocLastMnt)) {
			count = 01;
		}
		return count;

	}

	@Override
	public String getCode(String masterType, String kyeType) {
		// TODO Auto-generated method stub
		Map<String, Object> mapParam = new HashMap<>();
		String code = "";
		mapParam.put("masterType", masterType);
		mapParam.put("keyType", "%" + kyeType + "%");
		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT key_code");
		selectSql.append(" FROM  master_def");
		selectSql.append(" Where master_type =  :masterType and key_type  Like ( :keyType)");
		logger.debug("selectSql: " + selectSql.toString());

		try {
			code = jdbcTemplate.queryForObject(selectSql.toString(), mapParam, String.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			code = "";
		}
		return code;
	}

	public long getBatchNO() {

		return getNextValue("seqCkycBatchNo");
	}

	@Override
	public int updatNameFlag(CKYCLog file) {
		int count = 0;
		StringBuilder updateSql = new StringBuilder("UPDATE CKYCLOG");
		updateSql.append(" SET fileName =:fileName, rowNo =:rowNo, custsalutationcode =:custsalutationcode, ");
		updateSql.append(" custfname =:custfname, custmname =:custmname, custlname =:custlname");
		updateSql.append(" where custId =:custId and ckycNo =:ckycNo ");

		logger.debug("updateSql:" + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(file);
		try {
			count = this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);

			// TODO:handle exception
		}
		logger.debug("Leaving");
		return count;
	}

	@Override
	public int updatPersonalFlag(CKYCLog file) {
		int count = 0;
		StringBuilder updateSql = new StringBuilder("UPDATE CKYCLOG");
		updateSql.append(
				" SET fileName =:fileName, rowNo =:rowNo, custfatherName =:custfatherName, custdob =:custdob, ");
		updateSql.append(
				" custgendercode =:custgendercode, custmaritalsts =:custmaritalsts, custnationality =:custnationality, occupationtype =:occupationtype ");
		updateSql.append(" where custId =:custId and ckycNo =:ckycNo ");

		logger.debug("updateSql:" + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(file);
		try {
			count = this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);

			// TODO:handle exception
		}
		logger.debug("Leaving");
		return count;
	}

	@Override
	public int addressUpdateFlag(CKYCLog file) {
		int count = 0;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("custId", file.getCustId());

		StringBuilder selectAddr = new StringBuilder("SELECT LastMntOn");
		selectAddr.append(
				" FROM (SELECT lastmntOn,ROW_NUMBER() OVER(ORDER BY lastmntOn desc) As RowNum   FROM customeraddresses  Where CustID  =:custId ) ");
		selectAddr.append(" As custAddr where rownum=1");
		logger.debug("selectAddr: " + selectAddr.toString());
		Timestamp addrLastMnt = null;
		try {
			addrLastMnt = this.jdbcTemplate.queryForObject(selectAddr.toString(), paramMap, Timestamp.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION + e);

		}
		if (addrLastMnt != null) {
			file.setAddrLastMntOn(addrLastMnt);

			StringBuilder updateSql = new StringBuilder("UPDATE CKYCLOG");
			updateSql.append(" SET fileName =:fileName, rowNo =:rowNo, addrLastMntOn  =:addrLastMntOn");
			updateSql.append(" where custId =:custId and ckycNo =:ckycNo  ");

			logger.debug("updateSql:" + updateSql.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(file);
			try {
				count = this.jdbcTemplate.update(updateSql.toString(), beanParameters);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION + e);

				// TODO:handle exception
			}
			logger.debug("Leaving");
		}
		return count;
	}

	@Override
	public int phoneUpdateFlag(CKYCLog file) {
		int count = 0;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("custId", file.getCustId());

		StringBuilder selectPhone = new StringBuilder();
		selectPhone.append(" SELECT lastmnton");
		selectPhone.append(
				" FROM (SELECT lastmntOn,ROW_NUMBER() OVER(ORDER BY lastmntOn desc) As RowNum   FROM CustomerPhoneNumbers  Where PhoneCustID  =:custId ) ");
		selectPhone.append(" As phone where rownum=1");
		logger.debug("selectPhone: " + selectPhone.toString());
		Timestamp phoneLastMnt = null;
		try {
			phoneLastMnt = this.jdbcTemplate.queryForObject(selectPhone.toString(), paramMap, Timestamp.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION + e);

		}
		if (phoneLastMnt != null) {
			file.setPhoneLastMntOn(phoneLastMnt);

			StringBuilder updateSql = new StringBuilder("UPDATE CKYCLOG");
			updateSql.append(" SET fileName =:fileName, rowNo =:rowNo, phoneLastMntOn  =:phoneLastMntOn");
			updateSql.append(" where custId =:custId and ckycNo =:ckycNo  ");

			logger.debug("updateSql:" + updateSql.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(file);
			try {
				count = this.jdbcTemplate.update(updateSql.toString(), beanParameters);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION + e);

				// TODO:handle exception
			}
			logger.debug("Leaving");
		}
		return count;

	}

	@Override
	public int emailUpdateFlag(CKYCLog file) {
		int count = 0;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("custId", file.getCustId());
		StringBuilder selectEmail = new StringBuilder();
		selectEmail.append(" SELECT lastmntOn");
		selectEmail.append(
				" FROM (SELECT lastmntOn,ROW_NUMBER() OVER(ORDER BY lastmntOn desc) As RowNum   FROM CustomerEMails  Where custID  =:custId ) ");
		selectEmail.append(" As email where rownum=1");
		logger.trace("selectEmail:" + selectEmail.toString());
		Timestamp emailLastMnt = null;
		try {
			emailLastMnt = this.jdbcTemplate.queryForObject(selectEmail.toString(), paramMap, Timestamp.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION + e);
		}
		if (emailLastMnt != null) {
			file.setEmailLastMntOn(emailLastMnt);

			StringBuilder updateSql = new StringBuilder("UPDATE CKYCLOG");
			updateSql.append(" SET fileName =:fileName, rowNo =:rowNo, emailLastMntOn  =:emailLastMntOn");
			updateSql.append(" where custId =:custId and ckycNo =:ckycNo  ");

			logger.debug("updateSql:" + updateSql.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(file);
			try {
				count = this.jdbcTemplate.update(updateSql.toString(), beanParameters);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION + e);
			}
			logger.debug("Leaving");
		}
		return count;
	}

	@Override
	public int docUpdateFlag(CKYCLog file) {
		int count = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("custId", file.getCustId());
		StringBuilder selectdoc = new StringBuilder("SELECT LastMntOn");
		selectdoc.append(
				" FROM (SELECT lastmntOn,ROW_NUMBER() OVER(ORDER BY lastmntOn desc) As RowNum   FROM Customerdocuments  Where custId  =:custId ) ");
		selectdoc.append(" As doc where rownum=1");
		logger.debug("selectdoc: " + selectdoc.toString());
		Timestamp docLastMnt = null;
		try {
			docLastMnt = this.jdbcTemplate.queryForObject(selectdoc.toString(), paramMap, Timestamp.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION + e);
		}
		if (docLastMnt != null) {
			file.setDocLastMntOn(docLastMnt);
			StringBuilder updateSql = new StringBuilder("UPDATE CKYCLOG");
			updateSql.append(" SET fileName =:fileName, rowNo =:rowNo, docLastMntOn  =:docLastMntOn");
			updateSql.append(" where custId =:custId and ckycNo =:ckycNo  ");
			logger.debug("updateSql:" + updateSql.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(file);
			try {
				count = this.jdbcTemplate.update(updateSql.toString(), beanParameters);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION + e);
			}
			logger.debug("Leaving");
		}
		return count;
	}

	@Override
	public int updateCkycNo(String ckycNo, String batchNo, String rowNo) {
		CKYCLog ckycLog = new CKYCLog();
		ckycLog.setCkycNo(ckycNo);
		int count = 0;
		int rwNo = 0;
		try {
			String fileName = "%U" + batchNo + ".txt";
			rwNo = Integer.parseInt(rowNo);
			ckycLog.setRowNo(rwNo);
			ckycLog.setFileName(fileName);
			StringBuilder updateSql = new StringBuilder("UPDATE CKYCLOG");
			updateSql.append(" SET ckycNo =:ckycNo");
			updateSql.append(" where rowNo =:rowNo and fileName LIKE ( :fileName) and ckycno is null");

			logger.debug("updateSql:" + updateSql.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(ckycLog);
			count = this.jdbcTemplate.update(updateSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
		}
		logger.debug("Leaving");

		return count;

	}

	@Override
	public List<Customer> getId() {
		Customer customer = new Customer();
		StringBuilder selectSql = new StringBuilder("Select custId ");
		selectSql.append(" from customers WHERE CUSTFNAME IS NOT NULL and (CUSTFNAME = '') IS NOT True");
		logger.debug("selectSql:" + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customer);
		RowMapper<Customer> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);
		List<Customer> customers = new ArrayList<>();
		try {
			customers = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			// TODO: handle exception
		}
		logger.debug("Leaving");
		return customers;

	}

	@Override
	public List<CustomerDocument> getPofAddr(long custId) {
		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setCustID(custId);
		customerDocument.setLovDescCustDocCategory("pan" + "%");

		StringBuilder selectPoA = new StringBuilder(
				" SELECT lovdesccustdoccategory ,custDocIssuedCountry , custDocIssuedOn from customerdocuments_aview ");
		selectPoA.append(" where CustID = :custID  and  lovdesccustdoccategory not like  ( :lovDescCustDocCategory) ");
		selectPoA.append(" order by  CustID desc;");
		logger.debug("selectPoA: " + selectPoA.toString());
		List<CustomerDocument> pofAddr = null;
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		RowMapper<CustomerDocument> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerDocument.class);
		try {
			pofAddr = this.jdbcTemplate.query(selectPoA.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION + e);
			// TODO: handle exception
		}
		return pofAddr;
	}

}