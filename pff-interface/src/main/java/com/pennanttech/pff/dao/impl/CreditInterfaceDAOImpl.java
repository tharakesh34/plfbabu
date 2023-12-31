package com.pennanttech.pff.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.mchange.util.DuplicateElementException;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.servicetask.ServiceTaskDetail;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennanttech.model.interfacemapping.InterfaceMappingDetails;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.dao.CreditInterfaceDAO;

public class CreditInterfaceDAOImpl extends BasicDao<ExtendedFieldDetail> implements CreditInterfaceDAO {
	private static final Logger logger = LogManager.getLogger(CreditInterfaceDAOImpl.class);

	protected DefaultTransactionDefinition transDef;
	private PlatformTransactionManager transactionManager;

	/**
	 * Method for fetch the ExtendedFieldDetails based on given fieldaNames
	 * 
	 * @param fieldNames
	 * @return extendedFieldDetailList
	 * @throws Exception
	 */
	public List<ExtendedFieldDetail> getExtendedFieldDetailsByFieldName(Set<String> fieldNames) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Select ModuleId, FieldName, FieldType, ");
		sql.append(" FieldLength, FieldPrec, FieldLabel, FieldMandatory, FieldConstraint, ");
		sql.append(" FieldSeqOrder, FieldList, FieldDefaultValue, FieldMinValue, ");
		sql.append(" FieldMaxValue, FieldUnique, MultiLine, ParentTag, InputElement,Editable, ");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, ");
		sql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From ExtendedFieldDetail  WHERE FIELDNAME IN(:fieldNames)");
		paramMap.addValue("fieldNames", fieldNames);
		logger.debug("selectSql: " + sql.toString());

		RowMapper<ExtendedFieldDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(ExtendedFieldDetail.class);
		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), paramMap, typeRowMapper);
	}

	public List<CustomerDetails> getCoApplicants(List<Long> customerIds, String type) throws InterfaceException {
		logger.debug(Literal.ENTERING);
		List<CustomerDetails> customerDetailList = new ArrayList<CustomerDetails>(1);
		List<Customer> customers = getCustomerByID(customerIds, type);

		for (Customer customer : customers) {
			CustomerDetails customerDetails = new CustomerDetails();
			customerDetails.setCustomer(customer);
			customerDetails.setCustID(customer.getCustID());
			customerDetails.setAddressList(getCustomerAddresByCustomer(customer.getCustID(), type));
			customerDetails.setCustomerEMailList(getCustomerEmailByCustomer(customer.getCustID(), type));
			customerDetails.setCustomerDocumentsList(getCustomerDocumentByCustomer(customer.getCustID(), type));
			customerDetails.setCustomerPhoneNumList(getCustomerPhoneNumberById(customer.getCustID(), type));
			customerDetails.setCustomerPhoneNumList(getCustomerPhoneNumberById(customer.getCustID(), type));
			customerDetails.setExtendedFieldHeader(
					getExtendedFieldHeaderByModuleName(InterfaceConstants.MODULE_CUSTOMER, customer.getCustCtgCode()));
			StringBuilder tableName = new StringBuilder("");
			Map<String, Object> extMapValues = null;
			if (customerDetails.getExtendedFieldHeader() != null) {
				ExtendedFieldRender extendedFieldRender = new ExtendedFieldRender();
				extendedFieldRender.setReference(customer.getCustCIF());

				tableName.append(InterfaceConstants.MODULE_CUSTOMER);
				tableName.append("_");
				tableName.append(customer.getCustCtgCode());
				tableName.append("_ED");
				extMapValues = getExtendedField(customer.getCustCIF(), tableName.toString());

				if (extMapValues != null) {
					extendedFieldRender.setSeqNo(Integer.valueOf(extMapValues.get("SeqNo").toString()));
				}
				extendedFieldRender.setMapValues(extMapValues);
				customerDetails.setExtendedFieldRender(extendedFieldRender);
				tableName.setLength(0);
			}
			customerDetailList.add(customerDetails);
		}
		logger.debug(Literal.LEAVING);
		return customerDetailList;

	}

	public List<Customer> getCustomerByID(List<Long> customerIds, String type) throws InterfaceException {
		logger.debug("Entering");
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder selectSql = new StringBuilder("SELECT CustID, CustCIF, CustCoreBank, CustCtgCode, CustTypeCode,");
		selectSql.append(
				" CustSalutationCode, CustFName, CustMName, CustLName, CustShrtName, CustFNameLclLng, CustMNameLclLng,");
		selectSql.append(
				" CustLNameLclLng, CustShrtNameLclLng, CustDftBranch, CustGenderCode, CustDOB, CustPOB, CustCOB,");
		selectSql.append(
				" CustPassportNo, CustMotherMaiden, CustIsMinor, CustReferedBy, CustDSA, CustDSADept, CustRO1, CustRO2,");
		selectSql.append(
				" CustGroupID, CustSts, CustStsChgDate, CustGroupSts, CustIsBlocked, CustIsActive, CustIsClosed,");
		selectSql.append(
				" CustInactiveReason, CustIsDecease, CustIsDormant, CustIsDelinquent, CustIsTradeFinCust, CustIsStaff,");
		selectSql.append(
				" CustTradeLicenceNum , CustTradeLicenceExpiry, CustPassportExpiry, CustVisaNum , CustVisaExpiry,");
		selectSql.append(
				" CustStaffID, CustIndustry, CustSector, CustSubSector, CustProfession, CustTotalIncome, CustMaritalSts,");
		selectSql.append(
				" CustEmpSts, CustSegment, CustSubSegment, CustIsBlackListed, CustBLRsnCode, CustIsRejected, CustRejectedRsn,");
		selectSql.append(
				" CustBaseCcy, CustLng, CustParentCountry, CustResdCountry, CustRiskCountry, CustNationality, CustClosedOn, ");
		selectSql.append(
				"CustStmtFrq, CustIsStmtCombined, CustStmtLastDate, CustStmtNextDate, CustStmtDispatchMode, CustFirstBusinessDate,");
		selectSql.append(
				" CustAddlVar81, CustAddlVar82, CustAddlVar83, CustAddlVar84, CustAddlVar85, CustAddlVar86, CustAddlVar87,");
		selectSql.append(
				" CustAddlVar88, CustAddlVar89, CustAddlDate1, CustAddlDate2, CustAddlDate3, CustAddlDate4, CustAddlDate5,");
		selectSql.append(
				" CustAddlVar1, CustAddlVar2, CustAddlVar3, CustAddlVar4, CustAddlVar5, CustAddlVar6, CustAddlVar7, CustAddlVar8, ");
		selectSql.append(
				" CustAddlVar9, CustAddlVar10, CustAddlVar11, CustAddlDec1, CustAddlDec2, CustAddlDec3, CustAddlDec4, CustAddlDec5,");
		selectSql.append(
				" CustAddlInt1, CustAddlInt2, CustAddlInt3, CustAddlInt4, CustAddlInt5,DedupFound,SkipDedup,CustTotalExpense,CustBlackListDate,NoOfDependents,CustCRCPR,");
		selectSql.append(
				" JointCust, JointCustName, JointCustDob, custRelation, ContactPersonName, EmailID, PhoneNumber, SalariedCustomer, custSuspSts,custSuspDate, custSuspTrigger, ");

		if (type.contains("View")) {
			selectSql.append(
					" lovDescCustTypeCodeName, lovDescCustMaritalStsName, lovDescCustEmpStsName,  lovDescCustStsName,");
			selectSql.append(
					" lovDescCustIndustryName, lovDescCustSectorName, lovDescCustSubSectorName, lovDescCustProfessionName, lovDescCustCOBName ,");
			selectSql.append(
					" lovDescCustSegmentName, lovDescCustNationalityName, lovDescCustGenderCodeName, lovDescCustDSADeptName, lovDescCustRO1Name, ");
			selectSql.append(
					" lovDescCustGroupStsName, lovDescCustDftBranchName, lovDescCustCtgCodeName,lovDescCustCtgType, lovDescCustSalutationCodeName ,");
			selectSql.append(
					" lovDescCustParentCountryName, lovDescCustResdCountryName , lovDescCustRiskCountryName , lovDescCustRO2Name , lovDescCustBLRsnCodeName,");
			selectSql.append(
					" lovDescCustRejectedRsnName, lovDesccustGroupIDName , lovDescCustSubSegmentName, lovDescCustLngName , lovDescDispatchModeDescName");
			selectSql.append(" ,lovDescTargetName,");
		}

		selectSql.append(
				" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  Customers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID IN(:customerIds)");

		logger.debug("selectSql: " + selectSql.toString());
		paramMap.addValue("customerIds", customerIds);
		RowMapper<Customer> typeRowMapper = BeanPropertyRowMapper.newInstance(Customer.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), paramMap, typeRowMapper);
	}

	/**
	 * Method For getting List of Customer related Addresses for Customer
	 */
	public List<CustomerAddres> getCustomerAddresByCustomer(final long custId, String type) {
		logger.debug("Entering");
		CustomerAddres customerAddres = new CustomerAddres();
		customerAddres.setId(custId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID, CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet,");
		selectSql.append(" CustAddrLine1, CustAddrLine2, CustPOBox, CustAddrCity, CustAddrProvince,CustAddrPriority,");
		selectSql.append(
				" CustAddrCountry, CustAddrZIP, CustAddrPhone, CustAddrFrom,TypeOfResidence,CustAddrLine3,CustAddrLine4,CustDistrict,");
		if (type.contains("View")) {
			selectSql.append(" lovDescCustAddrTypeName, lovDescCustAddrCityName,");
			selectSql.append(" lovDescCustAddrProvinceName, lovDescCustAddrCountryName,lovDescCustAddrZip,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM CustomerAddresses");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerAddres);
		RowMapper<CustomerAddres> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerAddres.class);

		List<CustomerAddres> customerAddresses = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving");
		return customerAddresses;
	}

	/**
	 * Method to return the customer email based on given customer id
	 */
	public List<CustomerEMail> getCustomerEmailByCustomer(final long id, String type) {
		logger.debug("Entering");
		CustomerEMail customerEMail = new CustomerEMail();
		customerEMail.setId(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT CustID, CustEMail, CustEMailPriority, CustEMailTypeCode,");
		if (type.contains("View")) {
			selectSql.append(" lovDescCustEMailTypeCode,");
		}
		selectSql.append(" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId ");
		selectSql.append(" FROM  CustomerEMails");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CustID = :custID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerEMail);
		RowMapper<CustomerEMail> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerEMail.class);

		List<CustomerEMail> customerEMails = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);

		logger.debug("Leaving");
		return customerEMails;
	}

	/**
	 * Fetch the Customer PhoneNumber By its CustPhoneId
	 * 
	 * @param id
	 * 
	 * 
	 * @return
	 */
	public List<CustomerPhoneNumber> getCustomerPhoneNumberById(long id, String type) {
		logger.debug("Entering");
		CustomerPhoneNumber customerPhoneNumber = new CustomerPhoneNumber();
		customerPhoneNumber.setPhoneCustID(id);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(
				" SELECT  PhoneCustID, PhoneTypeCode, PhoneCountryCode, PhoneAreaCode, PhoneNumber,PhoneTypePriority,");
		if (type.contains("View")) {
			selectSql.append(" lovDescPhoneTypeCodeName, lovDescPhoneCountryName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" FROM  CustomerPhoneNumbers");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PhoneCustID =:PhoneCustID");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerPhoneNumber);
		RowMapper<CustomerPhoneNumber> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerPhoneNumber.class);

		List<CustomerPhoneNumber> customerPhoneNumbers = this.jdbcTemplate.query(selectSql.toString(), beanParameters,
				typeRowMapper);
		logger.debug("Leaving ");
		return customerPhoneNumbers;
	}

	public List<CustomerDocument> getCustomerDocumentByCustomer(long custId, String type) {
		logger.debug("Entering");
		CustomerDocument customerDocument = new CustomerDocument();
		customerDocument.setCustID(custId);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("SELECT CustID, CustDocType, CustDocTitle, CustDocSysName");
		selectSql.append(", CustDocRcvdOn, CustDocExpDate, CustDocIssuedOn, CustDocIssuedCountry");
		selectSql.append(
				", CustDocIsVerified, CustDocCategory, CustDocName, DocRefId, CustDocVerifiedBy, CustDocIsAcrive");
		selectSql.append(", DocPurpose, DocUri");
		if (type.contains("View")) {
			selectSql.append(", lovDescCustDocCategory, lovDescCustDocIssuedCountry");
			selectSql.append(", DocExpDateIsMand,DocIssueDateMand,DocIdNumMand,");
			selectSql.append(
					" DocIssuedAuthorityMand, DocIsPdfExtRequired, DocIsPasswordProtected, PdfMappingRef, pdfPassWord");
		}
		selectSql.append(", Version, LastMntOn, LastMntBy, RecordStatus");
		selectSql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId");
		selectSql.append(", RecordType, WorkflowId");
		selectSql.append(" FROM CustomerDocuments");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" where CustID = :CustID ");

		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDocument);
		RowMapper<CustomerDocument> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerDocument.class);

		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	public ExtendedFieldHeader getExtendedFieldHeaderByModuleName(final String moduleName, String subModuleName) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;

		source = new MapSqlParameterSource();
		source.addValue("ModuleName", moduleName);
		source.addValue("SubModuleName", subModuleName);

		StringBuilder selectSql = new StringBuilder("Select ModuleId, ModuleName,");
		selectSql.append(" SubModuleName, TabHeading, NumberOfColumns, ");
		selectSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, ");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ");
		selectSql.append(" PreValidationReq, PostValidationReq, PreValidation, PostValidation ");
		selectSql.append(" From ExtendedFieldHeader");
		selectSql.append(" Where ModuleName = :ModuleName AND SubModuleName = :SubModuleName");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<ExtendedFieldHeader> typeRowMapper = BeanPropertyRowMapper.newInstance(ExtendedFieldHeader.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public Map<String, Object> getExtendedField(String reference, String tableName) {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder("Select * from ");
		selectSql.append(tableName);
		selectSql.append(" where  Reference = :Reference ");
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		logger.debug("selectSql: " + selectSql.toString());
		try {
			return this.jdbcTemplate.queryForMap(selectSql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void save(ServiceTaskDetail serviceTaskDetail, String type) {
		logger.debug(Literal.ENTERING);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		// begin transaction
		txStatus = transactionManager.getTransaction(txDef);

		if (serviceTaskDetail.getId() == Long.MIN_VALUE) {
			// serviceTaskDetail.setId(getNextValue("SeqBMTAcademics"));
		}

		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into ServiceTaskDetails ");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (TaskExecutionId, ServiceModule, Reference, ServiceTaskId, ");
		insertSql.append(" ServiceTaskName , UserId, ExecutedTime, Status, Remarks)");
		insertSql.append(" Values( :TaskExecutionId, :ServiceModule, :Reference, :ServiceTaskId,");
		insertSql.append(" :ServiceTaskName , :UserId, :ExecutedTime, :Status, :Remarks)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(serviceTaskDetail);
		try {
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
			// commit
			transactionManager.commit(txStatus);
		} catch (DuplicateElementException dee) {
			logger.error("Exception", dee);
			transactionManager.rollback(txStatus);
			throw dee;
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 */
	@Override
	public List<ServiceTaskDetail> getServiceTaskDetails(String module, String reference, String serviceTaskName) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ServiceModule", module);
		source.addValue("Reference", reference);
		source.addValue("ServiceTaskName", serviceTaskName);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT TaskExecutionId, ServiceModule, Reference, ServiceTaskId,");
		selectSql.append(" ServiceTaskName, UserId, ExecutedTime, Status, Remarks From ServiceTaskDetails");
		selectSql.append(
				" where ServiceModule=:ServiceModule AND Reference=:Reference AND ServiceTaskName=:ServiceTaskName");

		logger.debug(Literal.SQL + selectSql.toString());
		RowMapper<ServiceTaskDetail> typeRowMapper = BeanPropertyRowMapper.newInstance(ServiceTaskDetail.class);

		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	/**
	 * Fetch the Record City details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return City
	 */
	public City getCityDetails(final String pCCountry, String pCProvince, String pCCity, String type) {
		logger.debug(Literal.ENTERING);
		City city = new City();
		city.setPCCountry(pCCountry);
		city.setPCProvince(pCProvince);
		city.setPCCity(pCCity);

		StringBuilder selectSql = new StringBuilder(
				"SELECT PCCountry, PCProvince, PCCity, PCCityName, PCCityClassification, BankRefNo, CityIsActive,");
		if (type.contains("View")) {
			selectSql.append(" LovDescPCProvinceName, LovDescPCCountryName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode,  NextRoleCode,");
		selectSql.append(" TaskId, NextTaskId, RecordType, WorkflowId");
		selectSql.append(" From RMTProvinceVsCity");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where PCCountry =:PCCountry and PCProvince=:PCProvince and PCCity=:PCCity ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(city);
		RowMapper<City> typeRowMapper = BeanPropertyRowMapper.newInstance(City.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void saveExtendedDetails(Map<String, Object> mappedValues, String type, String tableName) {
		logger.debug(Literal.ENTERING);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		// begin transaction
		txStatus = transactionManager.getTransaction(txDef);
		try {
			StringBuilder insertSql = new StringBuilder(" INSERT INTO ");
			insertSql.append(tableName);
			insertSql.append(StringUtils.trimToEmpty(type));

			List<String> list = new ArrayList<String>(mappedValues.keySet());
			String columnames = "";
			String columnValues = "";
			for (int i = 0; i < list.size(); i++) {
				if (i < list.size() - 1) {
					columnames = columnames.concat(list.get(i)).concat(" , ");
					columnValues = columnValues.concat(":").concat(list.get(i)).concat(" , ");
				} else {
					columnames = columnames.concat(list.get(i));
					columnValues = columnValues.concat(":").concat(list.get(i));
				}
			}
			insertSql.append(" (" + columnames + ") values (" + columnValues + ")");
			logger.debug("insertSql: " + insertSql.toString());

			this.jdbcTemplate.update(insertSql.toString(), mappedValues);
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error("Exception", e);
			transactionManager.rollback(txStatus);
			throw e;
		}
		logger.debug("Leaving");

	}

	@Override
	public void updateExtendedDetails(String reference, int seqNo, Map<String, Object> mappedValues, String type,
			String tableName) {
		logger.debug("Entering");

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		// begin transaction
		txStatus = transactionManager.getTransaction(txDef);
		try {
			StringBuilder updateSql = new StringBuilder(" UPDATE ");
			updateSql.append(tableName);
			updateSql.append(StringUtils.trimToEmpty(type));
			List<String> list = new ArrayList<String>(mappedValues.keySet());
			StringBuilder query = new StringBuilder();

			for (int i = 0; i < list.size(); i++) {
				if (i == 0) {
					query.append(" set ").append(list.get(i)).append(" = :").append(list.get(i));
				} else {
					query.append(", ").append(list.get(i)).append(" = :").append(list.get(i));
				}
			}
			updateSql.append(query);
			updateSql.append(" where Reference = :CREDIT_INT_EXT_DETAIL_REFERENCE");
			updateSql.append(" and SeqNo = :CREDIT_INT_EXT_DETAIL_SEQ_NO");

			// Execute the SQL, binding the arguments.
			logger.debug(Literal.SQL + updateSql.toString());
			MapSqlParameterSource paramSource = new MapSqlParameterSource(mappedValues);
			paramSource.addValue("CREDIT_INT_EXT_DETAIL_REFERENCE", reference);
			paramSource.addValue("CREDIT_INT_EXT_DETAIL_SEQ_NO", seqNo);

			this.jdbcTemplate.update(updateSql.toString(), paramSource);
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error("Exception", e);
			transactionManager.rollback(txStatus);
			throw e;
		}
		logger.debug("Leaving");
	}

	@Override
	public List<InterfaceMappingDetails> getInterfaceMappingDetails(String inerfaceReference, String moduleValue) {
		logger.debug("Entering");
		String str = "select T3.interfacevalue,T3.interfacesequence,T3.plfvalue from interface_fields T1 "
				+ "inner join interfacemapping T2 on T2.INTERFACEID=T1.INTERFACEID "
				+ "inner join mastermapping T3 on T3.INTERFACEMAPPINGID=T2.INTERFACEMAPPINGID "
				+ "where T1.interfacename=:inerfaceReference and T1.module=:moduleValue";
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("inerfaceReference", inerfaceReference);
		paramMap.addValue("moduleValue", moduleValue);
		logger.debug("selectSql: " + str.toString());
		logger.debug(Literal.LEAVING);

		RowMapper<InterfaceMappingDetails> typeRowMapper = BeanPropertyRowMapper
				.newInstance(InterfaceMappingDetails.class);
		return (List<InterfaceMappingDetails>) this.jdbcTemplate.query(str, paramMap, typeRowMapper);
	}

	public String getStateCode(String custAddrProvince) {

		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder(
				"select taxstatecode from RMTCOUNTRYVSPROVINCE where CPPROVINCE =:CPPROVINCE");
		MapSqlParameterSource mapParam = new MapSqlParameterSource();
		mapParam.addValue("CPPROVINCE", custAddrProvince);
		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcTemplate.queryForObject(sql.toString(), mapParam, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public String getEnquiryPurpose(String finType) {

		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("select key_Code from master_def where key_Type =:key_Type");
		MapSqlParameterSource mapParam = new MapSqlParameterSource();
		mapParam.addValue("key_Type", finType);
		logger.debug(Literal.SQL + sql.toString());

		try {
			return jdbcTemplate.queryForObject(sql.toString(), mapParam, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
