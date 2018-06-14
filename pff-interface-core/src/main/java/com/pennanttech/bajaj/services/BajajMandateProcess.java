package com.pennanttech.bajaj.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.transaction.TransactionStatus;

import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.mandate.Mandate;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.InterfaceConstants;
import com.pennanttech.pff.external.mandate.AbstractMandateProcess;

public class BajajMandateProcess extends AbstractMandateProcess {
	private final Logger logger = Logger.getLogger(getClass());

	@Override
	public boolean registerMandate(Mandate mandate) throws Exception {
		logger.info(Literal.ENTERING);

		if ("Y".equalsIgnoreCase((String) getSMTParameter("Mandate_REG_Automatic", String.class))) {
			InterfaceConstants.automaticMandate = "Y";
		} else {
			InterfaceConstants.automaticMandate = "N";
		}

		if (InterfaceConstants.automaticMandate.equals("N")) {
			return false;
		}

		updateRegistrationActiveFlag(mandate.getMandateID());

		if (mandate.getOrgReference() != null && StringUtils.isNotBlank(mandate.getOrgReference())) {
			String finReference = mandate.getOrgReference();
			String applId = StringUtils.substring(finReference, finReference.length() - 7, finReference.length());

			if (StringUtils.isNumeric(applId)) {
				mandate.setApplId(Long.parseLong(applId));
			} else {
				mandate.setApplId(0);
			}
		}

		mandate.setProcessDate(new Timestamp(System.currentTimeMillis()));
		saveMandateForRegistration(mandate);
		logger.info(Literal.LEAVING);
		return true;

	}


	public void updateRegistrationActiveFlag(long mandateID) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder updateSql = new StringBuilder("UPDATE MANDATE_REGISTRATION SET ACTIVE_FLAG = :ACTIVEFLAG");
		updateSql.append(" WHERE MANDATE_ID = :MANDATEID");
		paramMap.addValue("MANDATEID", mandateID);
		paramMap.addValue("ACTIVEFLAG", 0);

		logger.info("updateSql: " + updateSql.toString());
		namedJdbcTemplate.update(updateSql.toString(), paramMap);
		logger.debug(Literal.LEAVING);
	}

	public void saveMandateForRegistration(Mandate mandate) {

		setAdditionalDetails(mandate);
		setDueDate(mandate);
		setEntityCode(mandate);
		setCustid(mandate);

		StringBuilder insertSql = new StringBuilder("Insert Into MANDATE_REGISTRATION");
		insertSql.append(" (APPLID, CUSTOMERID, AGREEMENTNO, DI_DATE, SPONSOR_BANK_CODE, UTILITY_CODE, COMPANYID,");
		insertSql.append(" ACCOUNTTYPE, ACCOUNTNO, BANKNAME, IFSCCODE, AMTINWORDS, ECS_AMOUNT,");
		insertSql.append(" APPFORMNO, MOBILENO, EMAILID, ECSSTARTDATE, ECSENDDATE, ACCONTHOLDER,");
		insertSql.append(" MANDATEBARCODE, CREATIONDATE, MODIFICATIONDATE, MAKERID, PROCESSFLAG, PROCESSDATE, OPENECSFLAG,");
		insertSql.append(" MICRCODE, DEALID, BANK_BRANCH, PRODUCTFLAG, BRANCH,  MACHINE_FLAG,");
		insertSql.append(" MACHINE_FLAG_UPLOAD_DATE, FIRST_DUE_DATE, MANDATE_ID, ACTIVE_FLAG)");
		insertSql.append(" Values(:ApplId, :CustID, :OrgReference, :DiDate, :SponsorBankCode, :UtilityCode, :CompanyId,");
		insertSql.append(" :AccType, :AccNumber, :BankName, :IFSC, :AmountInWords, :MaxLimit,");
		insertSql.append(" :AppFormNo, :PhoneNumber, :EmailId, :StartDate, :ExpiryDate, :AccHolderName,");
		insertSql.append(" :BarCodeNumber, :InputDate, :ModificationDate, :ApprovalID, :ProcessFlag, :ProcessDate,:OpenMandate,");
		insertSql.append(" :MICR, :OrgReference, :BranchDesc, :FinType, :LoanBranch, :MachineFlag,");
		insertSql.append(" :MachineFlagUploadDate, :FirstDueDate, :MandateID, :ActiveFlag)");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(mandate);

		try {
			namedJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
		logger.debug("insertSql: " + insertSql.toString());
	}

	private void setCustid(Mandate mandate) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT CUSTCOREBANK FROM CUSTOMERS C");
		sql.append(" INNER JOIN MANDATES M ON M.CUSTID = C.CUSTID");
		sql.append(" WHERE M.ORGREFERENCE = :FINREFERENCE");

		paramMap.addValue("FINREFERENCE", mandate.getOrgReference());

		namedJdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				mandate.setCustID(rs.getLong("CUSTCOREBANK"));
			}
		});

	}


	private void setEntityCode(Mandate mandate) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		sql.append(" Select T1.FINREFERENCE, T3.ENTITYCODE From FINANCEMAIN_VIEW T1 ");
		sql.append(" INNER JOIN RMTFINANCETYPES T2  on T1.FINTYPE = T2.FINTYPE");
		sql.append(" INNER JOIN SMTDIVISIONDETAIL T3 ON T2.FINDIVISION = T3.DIVISIONCODE");
		sql.append(" WHERE T1.FINREFERENCE = :FINREFERENCE");

		paramMap.addValue("FINREFERENCE", mandate.getOrgReference());

		namedJdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				String entityCode = rs.getString("ENTITYCODE");
				if (StringUtils.isNumeric(entityCode)) {
					mandate.setCompanyId(entityCode);
				} 
			}
		});

	}


	private void setAdditionalDetails(Mandate mandate) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("SELECT FM.FINSTARTDATE, FM.FINTYPE, FM.APPLICATIONNO,");
		sql.append(" CE.CUSTEMAIL, BR.BRANCHDESC FROM FINANCEMAIN FM");
		sql.append(" INNER JOIN CUSTOMERS CUST  ON CUST.CUSTID = FM.CUSTID");
		sql.append(" LEFT JOIN CUSTOMEREMAILS CE ON CE.CUSTID = CUST.CUSTID AND CE.CUSTEMAILPRIORITY = :CUSTEMAILPRIORITY");
		sql.append(" LEFT JOIN RMTBRANCHES BR ON BR.BRANCHCODE = FM.FINBRANCH");
		sql.append(" WHERE FM.FINREFERENCE = :FINREFERENCE");

		paramMap.addValue("FINREFERENCE", mandate.getOrgReference());
		paramMap.addValue("CUSTEMAILPRIORITY", 5);

		namedJdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				if (mandate.getDiDate() == null) {
					mandate.setDiDate(rs.getDate("FinStartDate"));
				}
				if (mandate.getFinType() == null) {
					mandate.setFinType(rs.getString("Fintype"));
				}
				if (mandate.getAppFormNo() == null) {
					mandate.setAppFormNo(rs.getString("ApplicationNo"));
				}
				if(mandate.getEmailId() == null) {
				mandate.setEmailId(rs.getString("CUSTEMAIL"));
				}
				
				if (mandate.getLoanBranch() == null) {
					mandate.setLoanBranch(rs.getString("BRANCHDESC"));
				}
			}
		});
	}
	

	private void setDueDate(Mandate mandate) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder(" SELECT FIRSTREPAYDATE FROM FINPFTDETAILS");
		selectSql.append(" WHERE FINREFERENCE = :FINREFERENCE");

		paramMap.addValue("FINREFERENCE", mandate.getOrgReference());
		namedJdbcTemplate.query(selectSql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				mandate.setFirstDueDate(rs.getDate("FIRSTREPAYDATE"));
			}
		});
	}

	public void updateMandateStatus() throws Exception {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("AC", "AC");
		paramMap.addValue("REASON", null);
		paramMap.addValue("ChangeDate", getAppDate());
		paramMap.addValue("ACTIVE_FLAG", 1);
		paramMap.addValue("MACHINEFLAG", "Y");
		paramMap.addValue("STATUS", "InProcess");

		StringBuilder sql = new StringBuilder();		
		sql.append(" INSERT INTO MANDATESSTATUS");
		sql.append(" SELECT MANDATE_ID, :AC, :REASON, :ChangeDate, Id");
		sql.append(" From MANDATE_REGISTRATION MR");
		sql.append(" INNER JOIN MANDATES M ON M.MANDATEID = MR.MANDATE_ID AND M.STATUS = :STATUS");
		sql.append(" WHERE MACHINE_FLAG = :MACHINEFLAG and ACTIVE_FLAG = :ACTIVE_FLAG");
		
		TransactionStatus txnStatus = null;
		try {
			txnStatus = transManager.getTransaction(transDef);
			namedJdbcTemplate.update(sql.toString(), paramMap);
			
			sql = new StringBuilder();
			sql.append(" UPDATE MANDATES set STATUS = :AC where MANDATEID IN(");
			sql.append(" select MANDATE_ID from MANDATE_REGISTRATION where MACHINE_FLAG = :MACHINEFLAG AND ACTIVE_FLAG = :ACTIVE_FLAG");
			sql.append(" ) and STATUS = :STATUS");

		
			namedJdbcTemplate.update(sql.toString(), paramMap);
			transManager.commit(txnStatus);
		} catch (Exception e) {
			transManager.rollback(txnStatus);
			logger.error(Literal.EXCEPTION, e);
		}

	}
	
	
	@Override 
	protected Mandate getMandateById(final long id) {
		Mandate mandate = super.getMandateById(id);

		if (mandate == null) {
			mandate = getMandateByIdFromAutoRegistration(id);
		}

		return mandate;
	}
	
	private Mandate getMandateByIdFromAutoRegistration(final long id) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT ID RequestID, Mandate_ID MandateID, AGREEMENTNO FinReference, CUSTOMERID,  MICRCODE MICR, ACCOUNTNO AccNumber, OPENECSFLAG lovValue ");
		sql.append(" From MANDATE_REGISTRATION");
		sql.append(" Where Mandate_ID =:MandateID and Machine_flag = :MachineFlag AND active_flag = :Active");
		source = new MapSqlParameterSource();
		source.addValue("MandateID", id);
		source.addValue("MachineFlag", "Y");
		source.addValue("Active", 1);

		RowMapper<Mandate> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Mandate.class);
		try {
			return this.namedJdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (Exception  e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			source = null;
		}
		logger.debug(Literal.LEAVING);
		return null;
	}
	
	@Override
	protected void validateMandate(Mandate respMandate, Mandate mandate, StringBuilder remarks) {

		if (!StringUtils.equals(respMandate.getFinReference(), mandate.getFinReference())) {
			if (remarks.length() > 0) {
				remarks.append(", ");
			}
			remarks.append("Fin Reference Not Matched.");
		}

		// validate the MICR code
		BankBranch bankBranch = getBankBrachByMicr(mandate.getMICR());
		if (bankBranch == null) {
			remarks.append("Invalid MICR Code");
		} else {
			// validate the AccNo length
			if (bankBranch.getAccNoLength() != 0) {
				if (mandate.getAccNumber().length() != bankBranch.getAccNoLength()) {
					remarks.append("Invalid Account Number Length");
				}
			}
		}
	}
	
	
	@Override
	protected void processSecondaryMandate(Mandate respMandate) {

		boolean secondaryMandate = checkSecondaryMandate(respMandate.getMandateID());
		if (secondaryMandate) {
			makeSecondaryMandateInActive(respMandate.getMandateID());
			loanMandateSwapping(respMandate.getFinReference(), respMandate.getMandateID());

		}
	}
	
	@Override
	protected void processSwappedMandate(Mandate respMandate) {

		boolean swappedMandate = checkSwappedMandate(respMandate.getMandateID());
		if (swappedMandate) {
			loanMandateSwapping(respMandate.getFinReference(), respMandate.getMandateID());

		}
	}
	
	@Override
	protected void addCustomParameter(Map<String, Object> parameterMap) {
		
		String entity = (String) parameterMap.get("ENTITY_CODE");
		
		parameterMap.put("ENTITY_CODE", entity + "_MANDATES_");
	}
	
	private void makeSecondaryMandateInActive(long mandateID) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE MANDATES SET ACTIVE = :ACTIVE WHERE  PRIMARYMANDATEID = :MANDATEID");

		paramMap.addValue("MANDATEID", mandateID);
		paramMap.addValue("ACTIVE", 0);

		try {
			namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
	}


	private boolean checkSecondaryMandate(long mandateID) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder("SELECT Count(*) FROM MANDATES");
		selectSql.append(" WHERE PRIMARYMANDATEID = :PRIMARYMANDATEID AND ACTIVE = :ACTIVE");
		paramMap.addValue("PRIMARYMANDATEID", mandateID);
		paramMap.addValue("ACTIVE", 1);

		try {
			if (namedJdbcTemplate.queryForObject(selectSql.toString(), paramMap, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}
	
	
	private boolean checkSwappedMandate(long mandateID) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder selectSql = new StringBuilder("SELECT SWAPISACTIVE  FROM MANDATES");
		selectSql.append(" WHERE MANDATEID = :MANDATEID");
		paramMap.addValue("MANDATEID", mandateID);

		try {
			return namedJdbcTemplate.queryForObject(selectSql.toString(), paramMap, Boolean.class);
		} catch (Exception e) {
			throw e;
		}
	}
	
	private void loanMandateSwapping(String finReference, long mandateId) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder("Update FinanceMain");
		sql.append(" Set MandateID =:MandateID ");
		sql.append(" Where FinReference =:FinReference");

		source.addValue("MandateID", mandateId);
		source.addValue("FinReference", finReference);

		try {
			namedJdbcTemplate.update(sql.toString(), source);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}

		logger.debug("updateSql: " + source.toString());

	}
	
	public BankBranch getBankBrachByMicr(String micr) {
		logger.debug("Entering");

		BankBranch bankBranch = new BankBranch();
		bankBranch.setMICR(micr);
		bankBranch.setActive(true);

		StringBuilder sql = new StringBuilder(
				"Select b.BankCode,bb.micr,bb.branchcode,b.accnolength From BMTBankDetail b ");
		sql.append("Inner Join BankBranches bb on b.Bankcode = bb.bankCode");
		sql.append(" Where MICR =:MICR AND bb.ACTIVE = :Active");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(bankBranch);
		RowMapper<BankBranch> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(BankBranch.class);

		try {
			bankBranch = this.namedJdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			bankBranch = null;
		}

		logger.debug("Leaving");
		return bankBranch;
	}
}
