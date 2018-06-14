package com.pennanttech.pff.external.customer;

import java.sql.Types;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

public class CustomerProcedure  extends StoredProcedure {
	

	public CustomerProcedure(DataSource dataSource, String procedureName) {
		super(dataSource, procedureName);

		declareParameter(new SqlParameter("P_FINN_CUSTID", Types.NUMERIC));
		declareParameter(new SqlParameter("P_SOURCE_SYSTEM", Types.VARCHAR));
		declareParameter(new SqlParameter("P_CUSTOMERNAME", Types.VARCHAR));
		declareParameter(new SqlParameter("P_CONSTID", Types.NUMERIC));
		declareParameter(new SqlParameter("P_INDUSTRYID", Types.NUMERIC));
		declareParameter(new SqlParameter("P_CATEGORYID", Types.NUMERIC));
		declareParameter(new SqlParameter("P_SPOUSENAME", Types.VARCHAR));
		declareParameter(new SqlParameter("P_INDV_CORP_FLAG", Types.VARCHAR));
		declareParameter(new SqlParameter("P_FNAME", Types.VARCHAR));
		declareParameter(new SqlParameter("P_MNAME", Types.VARCHAR));
		declareParameter(new SqlParameter("P_LNAME", Types.VARCHAR));
		declareParameter(new SqlParameter("P_DOB", Types.DATE));
		declareParameter(new SqlParameter("P_SEX", Types.VARCHAR));
		declareParameter(new SqlParameter("P_INCOME_SOURCE", Types.VARCHAR));
		declareParameter(new SqlParameter("P_YEARS_CURR_JOB", Types.DATE));
		declareParameter(new SqlParameter("P_COR_DOI", Types.DATE));
		declareParameter(new SqlParameter("P_MAKERID", Types.VARCHAR));
		declareParameter(new SqlParameter("P_MAKERDATE", Types.DATE));
		declareParameter(new SqlParameter("P_AUTHID", Types.VARCHAR));
		declareParameter(new SqlParameter("P_AUTHDATE", Types.DATE));
		declareParameter(new SqlParameter("P_ACCOTYPE", Types.VARCHAR));
		declareParameter(new SqlParameter("P_ACCOCATG", Types.VARCHAR));
		declareParameter(new SqlParameter("P_DATELASTUPDT", Types.DATE));
		declareParameter(new SqlParameter("P_NATIONALID", Types.VARCHAR));
		declareParameter(new SqlParameter("P_PASSPORTNO", Types.VARCHAR));
		declareParameter(new SqlParameter("P_NATIONALITY", Types.VARCHAR));
		declareParameter(new SqlParameter("P_PAN_NO", Types.VARCHAR));
		declareParameter(new SqlParameter("P_REGIONID", Types.NUMERIC));
		declareParameter(new SqlParameter("P_BANK_TYPE", Types.VARCHAR));
		declareParameter(new SqlParameter("P_ENTITYFLAG", Types.VARCHAR));
		declareParameter(new SqlParameter("P_CONTACT_PERSON", Types.VARCHAR));
		declareParameter(new SqlParameter("P_CUSTSEARCHID", Types.VARCHAR));
		declareParameter(new SqlParameter("P_ECONOMIC_SEC_ID", Types.NUMERIC));
		declareParameter(new SqlParameter("P_FRAUD_FLAG", Types.VARCHAR));
		declareParameter(new SqlParameter("P_FRAUD_SCORE", Types.NUMERIC));
		declareParameter(new SqlParameter("P_EMI_CARD_ELIG", Types.VARCHAR));
		declareParameter(new SqlParameter("P_ADDRESS_DTL", Types.VARCHAR));
		declareParameter(new SqlParameter("P_BANK_DTL", Types.VARCHAR));
		declareParameter(new SqlParameter("P_N_NAME", Types.VARCHAR));
		declareParameter(new SqlParameter("P_N_ADDRESS", Types.VARCHAR));
		declareParameter(new SqlParameter("P_N_RELATION", Types.VARCHAR));
		declareParameter(new SqlParameter("P_N_FIELD9", Types.VARCHAR));
		declareParameter(new SqlParameter("P_N_FIELD10", Types.VARCHAR));
		declareParameter(new SqlParameter("P_INS_UPD_FLAG", Types.VARCHAR));
		declareParameter(new SqlOutParameter("P_SUCCESS_REJECT", Types.VARCHAR));
		declareParameter(new SqlOutParameter("P_REJECTION_REASON", Types.VARCHAR));
		declareParameter(new SqlInOutParameter("P_FINN_CUST_ID", Types.NUMERIC));
		declareParameter(new SqlParameter("P_SFDC_CUSTOMERID", Types.NUMERIC));
		declareParameter(new SqlParameter("P_BRANCHID", Types.NUMERIC));
	}

}
