package com.pennant.backend.dao.cibil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.app.util.DateUtility;
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
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.model.cibil.CibilFileInfo;
import com.pennanttech.pff.model.cibil.CibilMemberDetail;

public class CIBILDAOImpl extends BasicDao<Object> implements CIBILDAO {
	private static Logger logger = Logger.getLogger(CIBILDAOImpl.class);

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
			sql.append(" select custShrtName, CustSalutationCode, CustFName, CustMName, CustLName, custDOB");
			sql.append(", custGenderCode from customers where CUSTID = :CUSTID");
		} else {
			sql.append(" select distinct c.custid, c.custdftbranch, c.custfname, c.custmname");
			sql.append(", c.custlname, c.custshrtname, c.custtradelicencenum, c.custdob, custcob");
			sql.append(", c.custcrcpr");
			sql.append(", lcm.code legalconstitution, bcm.code businesscategory, cc.custctgtype lovDescCustCtgType");
			sql.append(" from customers c ");
			sql.append(" inner join bmtcustcategories cc on cc.custctgcode = c.custctgcode");
			sql.append(" left join cibil_legal_const_mapping lcm on lcm.Cust_Type_Code = c.custtypecode");
			sql.append(" and lcm.segment_type =:CORP");
			sql.append(" left join cibil_legal_constitution lc on lc.Code = lcm.Code");
			sql.append(" and lc.Segment_Type=lcm.Segment_Type");
			sql.append(" left join cibil_business_catgry_mapping bcm on bcm.category = c.custctgcode");
			sql.append(" and bcm.segment_type =:CORP ");
			sql.append(" left join cibil_business_category bc on bc.code = bcm.code");
			sql.append(" and bc.Segment_Type=bcm.Segment_Type");
			sql.append(" left join cibil_industry_type_mapping bit on bit.industry = c.custindustry");
			sql.append(" where c.CUSTID = :CUSTID");
		}

		paramMap.addValue("CUSTID", customerId);
		paramMap.addValue("CORP", "CORP");

		RowMapper<Customer> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Customer.class);

		return this.jdbcTemplate.queryForObject(sql.toString(), paramMap, rowMapper);
	}

	@Override
	public List<CustomerDocument> getCustomerDocuments(long customerId, String bureauType) {
		logger.trace(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(bureauType)) {
			sql.append("select dt.code custdoccategory, custdoctitle, custdocissuedon, custdocexpdate");
			sql.append(" from customerdocuments doc");
			sql.append(" inner join cibil_document_types_mapping dm on dm.doctypecode = doc.custdoccategory");
			sql.append(" inner join cibil_document_types dt on dt.code = dm.code");
		} else {
			sql.append("select custdoccategory, custdoctitle, custdocissuedon, custdocexpdate");
			sql.append(" from customerdocuments doc");
		}
		sql.append(" where custid = :custid");

		paramMap.addValue("custid", customerId);

		RowMapper<CustomerDocument> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerDocument.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), paramMap, rowMapper);
		} catch (Exception e) {
			logger.trace(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}

	@Override
	public List<CustomerPhoneNumber> getCustomerPhoneNumbers(long customerId, String bureauType) {
		logger.trace(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		
		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(bureauType)) {
			sql.append(" select cpt.code phonetypecode, cp.phonenumber, phoneareacode, phonetypepriority");
			sql.append(" from customerphonenumbers cp");
			sql.append(" left join cibil_phone_types_mapping pm on pm.phonetypecode=cp.phonetypecode");
			sql.append(" left join cibil_phone_types cpt on cpt.code = pm.code");
		} else {
			sql.append(" select phonetypecode, cp.phonenumber, phoneareacode, phonetypepriority");
			sql.append(" from customerphonenumbers cp");
		}
		sql.append(" where phonecustid = :phonecustid");

		paramMap.addValue("phonecustid", customerId);

		RowMapper<CustomerPhoneNumber> rowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(CustomerPhoneNumber.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), paramMap, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}

	@Override
	public List<CustomerEMail> getCustomerEmails(long customerId) {
		logger.trace(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		sql.append(" select CustEMail from CUSTOMEREMAILS");
		sql.append(" where CUSTID = :CUSTID");

		paramMap.addValue("CUSTID", customerId);

		RowMapper<CustomerEMail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerEMail.class);

		return this.jdbcTemplate.query(sql.toString(), paramMap, rowMapper);
	}

	@Override
	public List<CustomerAddres> getCustomerAddres(long customerId, String segmentType) {
		logger.trace(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append(" select cat.code CustAddrType, CustAddrHNbr, CustFlatNbr, CustAddrStreet,");
		sql.append(" CustAddrLine1, CustAddrLine2, sm.code CustAddrProvince, CustAddrZIP");
		sql.append(" from CustomerAddresses ca");
		sql.append(" left join cibil_address_types_mapping am on am.address_type = ca.custaddrtype");
		sql.append(" and am.segment_type = :segment_type");		
		sql.append(" left join cibil_address_types cat on cat.code = am.Code and cat.segment_type = am.segment_type");
		sql.append(" left join cibil_states_mapping sm on sm.CPPROVINCE = ca.CUSTADDRPROVINCE ");
		sql.append(" where CUSTID = :CUSTID");

		paramMap.addValue("CUSTID", customerId);
		paramMap.addValue("segment_type", segmentType);

		RowMapper<CustomerAddres> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CustomerAddres.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), paramMap, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}
	
	@Override
	public FinanceEnquiry getFinanceSummary(long customerId, String finReference) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		sql.append(" select  custid, finreference, finstartdate, finapproveddate, latestrpydate,");
		sql.append("  curoddays as oddays, closingstatus, ");
		sql.append("  future_schedule_prin, instalment_due, instalment_paid, bounce_due, bounce_paid, ");
		sql.append(
				"  late_payment_penalty_due, late_payment_penalty_paid, total_pri_schd, total_pri_paid, total_pft_schd, ");
		sql.append("  total_pft_paid, excess_amount, excess_amt_paid, ");
		sql.append("  ownership, fintype, finassetvalue, custincome");
		sql.append("  from cibil_customer_loans_view cs");
		sql.append("  where cs.finreference = :finreference and custid = :custid");

		paramMap.addValue("finreference", finReference);
		paramMap.addValue("custid", customerId);

		RowMapper<FinanceEnquiry> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceEnquiry.class);
		return this.jdbcTemplate.queryForObject(sql.toString(), paramMap, rowMapper);
	}


	@Override
	public List<FinanceEnquiry> getFinanceSummary(long customerId) {
		logger.trace(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select * from cibil_customer_loans_view cs");
		sql.append(
				" inner join cibil_customer_extract cce on cce.finreference = cs.finreference and cs.custid = cce.custid");
		sql.append(" where cs.custid = :custid");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("custid", customerId);

		RowMapper<FinanceEnquiry> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinanceEnquiry.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), paramMap, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}

	@Override
	public void logFileInfoException(long id, String finReference, String reason) {
		logger.trace(Literal.ENTERING);

		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Insert Into CIBIL_FILE_INFO_LOG");
		sql.append(" (ID, FINREFERENCE, REASON, STATUS)");
		sql.append(" Values(:ID, :FINREFERENCE, :REASON, :STATUS)");

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
		DataEngineStatus dataStatus = null;
		RowMapper<DataEngineStatus> rowMapper = null;
		StringBuilder sql = null;

		sql = new StringBuilder("Select ID, TOTAL_RECORDS, PROCESSED_RECORDS, SUCCESS_RECORDS, FAILED_RECORDS,");
		sql.append(" REMARKS, START_TIME, END_TIME from CIBIL_FILE_INFO");
		sql.append(" where Id = (Select MAX(Id) from CIBIL_FILE_INFO)");

		rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DataEngineStatus.class);

		try {
			dataStatus = jdbcTemplate.queryForObject(sql.toString(), new MapSqlParameterSource(), rowMapper);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (dataStatus != null) {
			List<DataEngineLog> list = getExceptions(dataStatus.getId());
			if (list != null && !list.isEmpty()) {
				dataStatus.setDataEngineLogList(list);
			}
		} else {
			dataStatus = new DataEngineStatus();
		}

		return dataStatus;
	}

	public List<DataEngineLog> getExceptions(long Id) {
		RowMapper<DataEngineLog> rowMapper = null;
		MapSqlParameterSource parameterMap = null;
		StringBuilder sql = null;

		sql = new StringBuilder("Select * from CIBIL_FILE_INFO_LOG where ID = :ID");

		parameterMap = new MapSqlParameterSource();
		parameterMap.addValue("ID", Id);

		rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(DataEngineLog.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), parameterMap, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

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
		RowMapper<EventProperties> rowMapper = null;
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		StringBuilder sql = null;
		try {
			sql = new StringBuilder("SELECT DEP.* FROM DATA_ENGINE_EVENT_PROPERTIES DEP");
			sql.append(" INNER JOIN DATA_ENGINE_CONFIG DC ON DC.ID = DEP.CONFIG_ID");
			sql.append("  Where DC.NAME = :NAME AND DEP.STORAGE_TYPE = :STORAGE_TYPE");

			rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(EventProperties.class);
			parameterSource.addValue("NAME", configName);
			parameterSource.addValue("STORAGE_TYPE", eventType);

			logger.debug(Literal.LEAVING);

			return jdbcTemplate.queryForObject(sql.toString(), parameterSource, rowMapper);

		} catch (Exception e) {
			logger.warn("Configuration details not available for " + configName);
		}

		return null;
	}

	@Override
	public CibilMemberDetail getMemberDetails(String bureauType) {

		RowMapper<CibilMemberDetail> rowMapper = null;
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		StringBuilder sql = null;
		try {
			sql = new StringBuilder();
			sql.append("select * from cibil_member_details where segment_Type =:segment_Type");

			rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CibilMemberDetail.class);
			parameterSource.addValue("segment_Type", bureauType);
			return jdbcTemplate.queryForObject(sql.toString(), parameterSource, rowMapper);

		} catch (Exception e) {
			throw new AppException(bureauType + " Member Details not configured");
		}

	}

	@Override
	public void logFileInfo(CibilFileInfo fileInfo) {
		logger.trace(Literal.ENTERING);

		final KeyHolder keyHolder = new GeneratedKeyHolder();

		StringBuilder sql = new StringBuilder("insert into cibil_file_info");
		sql.append(" (File_Name, Member_Id, Member_Short_Name, Member_Password, CreatedOn, Status, File_Location");
		sql.append(", Start_Time, Segment_Type)");
		sql.append(" Values(:File_Name, :Member_Id, :Member_Short_Name, :Member_Password, :CreatedOn, :Status,");
		sql.append(":File_Location, :Start_Time, :Segment_Type)");

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		CibilMemberDetail memberDetail = fileInfo.getCibilMemberDetail();

		paramMap.addValue("Member_Id", memberDetail.getMemberId());
		paramMap.addValue("File_Name", fileInfo.getFileName());

		paramMap.addValue("Member_Short_Name", memberDetail.getMemberShortName());
		paramMap.addValue("Member_Password", memberDetail.getMemberPassword());
		paramMap.addValue("CreatedOn", DateUtility.getAppDate());
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
	}

	@Override
	public long extractCustomers(String segmentType) throws Exception {
		logger.trace(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		sql.append("INSERT INTO CIBIL_CUSTOMER_EXTRACT");
		if (PennantConstants.PFF_CUSTCTG_INDIV.equals(segmentType)) {
			sql.append(" SELECT CUSTID, FINREFERENCE, OWNERSHIP, LATESTRPYDATE, :CUSTTYPECTG");
			sql.append(" FROM CIBIL_CUSTOMER_EXTARCT_VIEW");
			sql.append(" WHERE LATESTRPYDATE >= :LATESTRPYDATE ");
		} else {
			sql.append(" SELECT C.CUSTID, FM.FINREFERENCE, 0, LATESTRPYDATE, :CUSTTYPECTG");
			sql.append(" FROM FINANCEMAIN FM");
			sql.append(" INNER JOIN FINPFTDETAILS FP ON FP.FINREFERENCE = FM.FINREFERENCE");
			sql.append(" INNER JOIN CUSTOMERS C ON C.CUSTID = FM.CUSTID");
			sql.append(" INNER JOIN RMTCUSTTYPES CT ON CT.CUSTTYPECODE = C.CUSTTYPECODE");
			sql.append(" AND CT.CUSTTYPECTG = :CUSTTYPECTG  WHERE LATESTRPYDATE >= :LATESTRPYDATE ");
		}

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("LATESTRPYDATE", DateUtil.addMonths(DateUtility.getAppDate(), -36));
		paramMap.addValue("CUSTTYPECTG", segmentType);
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
		sql.append(" set Status = :Status , Total_Records = :TotalRecords, Processed_Records = :ProcessedRecords,");
		sql.append(" SUCCESS_RECORDS = :SuccessCount, Failed_Records = :FailedCount, Remarks = :Remarks,");
		sql.append("End_Time = :EndTime where ID = :ID");

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

	}

	@Override
	public List<FinODDetails> getFinODDetails(String finReference, String finCCY) {
		logger.trace(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" Select FinODSchdDate, UDF_CONVERTCURRENCY(FinCurODAmt, :FinCCY, :INR) FinCurODAmt, FinCurODDays");
		sql.append(" From FinODDetails od");
		sql.append(" Where FinReference =:FinReference and FinCurODAmt >:FinCurODAmt");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("FinReference", finReference);
		parameterSource.addValue("FinCurODAmt", 0);
		parameterSource.addValue("FinCCY", finCCY);
		parameterSource.addValue("INR", "INR");

		RowMapper<FinODDetails> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), parameterSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}

	@Override
	public List<CollateralSetup> getCollateralDetails(String finReference, String segmentType) {
		logger.trace(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();

		sql.append(" Select cs.bankvaluation, ccy.ccynumber collateralccy, collateral_type collateraltype");
		sql.append(" From collateralassignment ca");
		sql.append(" inner join cibil_customer_extract cce on cce.finreference = ca.reference");
		sql.append(" inner join collateralsetup cs on cs.collateralref = ca.collateralref");
		sql.append(" inner join rmtcurrencies ccy on ccy.ccycode = cs.collateralccy");
		sql.append(" inner join collateralstructure ce on ce.collateraltype=cs.collateraltype");
		sql.append(" left join cibil_collateral_types_mapping cctm on cctm.collateral_type = cs.collateraltype");
		sql.append(" and cctm.segment_type= :segment_type");
		sql.append(" left join cibil_collateral_types cct on cct.code = cctm.code and cct.segment_type=:segment_type");
		sql.append(" where cce.finreference =:FinReference and cce.segment_type =:segment_type");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("FinReference", finReference);
		parameterSource.addValue("segment_type", segmentType);

		RowMapper<CollateralSetup> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(CollateralSetup.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), parameterSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}

	@Override
	public List<ChequeDetail> getChequeBounceStatus(String finReference) {
		logger.trace(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();

		sql.append(" select bouncedate chequeBounceDate, receiptamount amount, rd.chequeacno chequeNumber,");
		sql.append(" null chequeBounceDate, br.reason chequeBounceReason");
		sql.append(" from finreceiptheader rh");
		sql.append(" inner join finreceiptdetail rd on rd.receiptid = rh.receiptid");
		sql.append(" inner join manualadvise ma on ma.receiptid = rh.receiptid");
		sql.append(" inner join bouncereasons br on br.bounceid = ma.bounceid");
		sql.append(" where receiptmode=:ReceiptMode and receiptmodestatus= :ReceiptModeStatus");
		sql.append(" and br.bouncecode in (:bouncecode)");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("FinReference", finReference);
		parameterSource.addValue("ReceiptMode", " CHEQUE");
		parameterSource.addValue("ReceiptModeStatus", "B");
		parameterSource.addValue("bouncecode", Arrays.asList("41, 403"));

		RowMapper<ChequeDetail> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ChequeDetail.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), parameterSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}

	@Override
	public List<Long> getGuarantorsDetails(String finRefrence) {
		logger.trace(Literal.ENTERING);
		StringBuilder sql = new StringBuilder();

		sql.append(" select distinct c.custId from finguarantorsdetails gd");
		sql.append(" inner join Financemain fm on fm.finreference = gd.finreference");
		sql.append(" inner join customers c on c.custcif = gd.guarantorcif ");
		sql.append(" where bankCustomer = :bankCustomer");

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("FinRefrence", finRefrence);
		parameterSource.addValue("bankCustomer", 1);

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcTemplate.queryForList(sql.toString(), parameterSource, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}

	@Override
	public long getotalRecords(String segmentType) {
		String sql = "select count(distinct custid) from cibil_customer_extract where segment_type = :segment_type";
		
		logger.trace(Literal.SQL + sql.toString());
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("segment_type", segmentType);
				
		return this.jdbcTemplate.queryForObject(sql, paramMap, Long.class);
	}
}