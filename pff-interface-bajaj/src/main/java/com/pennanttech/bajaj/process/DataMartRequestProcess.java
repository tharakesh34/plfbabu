package com.pennanttech.bajaj.process;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.pennanttech.bajaj.process.datamart.AddressDetailsDataMart;
import com.pennanttech.bajaj.process.datamart.ApplicantDetailsDataMart;
import com.pennanttech.bajaj.process.datamart.ApplicationDetailsDataMart;
import com.pennanttech.bajaj.process.datamart.CoApplicantDetailsDataMart;
import com.pennanttech.bajaj.process.datamart.DisbursementDataMart;
import com.pennanttech.bajaj.process.datamart.IVRFlexiDataMart;
import com.pennanttech.bajaj.process.datamart.InsuranceDetailsDataMart;
import com.pennanttech.bajaj.process.datamart.LoanDetailDataMart;
import com.pennanttech.bajaj.process.datamart.OpenEcsDetailsDataMart;
import com.pennanttech.bajaj.process.datamart.PropertyDetailsDataMart;
import com.pennanttech.bajaj.process.datamart.SubQDisbDataMart;
import com.pennanttech.pff.core.Literal;

public class DataMartRequestProcess {
	private static final Logger	logger	= Logger.getLogger(DataMartRequestProcess.class);

	private DataSource			dataSource;
	private long				userId;
	private Date				valueDate;
	private Date				appDate;
	private Boolean 			logBatch;

	public DataMartRequestProcess(DataSource dataSource, long userId, Date valueDate, Date appDate,boolean logBatch) {
		this.dataSource = dataSource;
		this.userId = userId;
		this.valueDate = valueDate;
		this.appDate = appDate;
		this.logBatch = logBatch;
	}

	public void importData() {
		logger.debug(Literal.ENTERING);

		DisbursementDataMart disbursementDM = new DisbursementDataMart(dataSource, userId, valueDate, appDate, true);
		disbursementDM.process("DATA_MART_REQUEST");
		disbursementDM.run();
		
		LoanDetailDataMart loanDetailDM = new LoanDetailDataMart(dataSource, userId, valueDate, appDate, true);
		loanDetailDM.process("DATA_MART_REQUEST");
		loanDetailDM.run();
		
		
		InsuranceDetailsDataMart insuranceDM = new InsuranceDetailsDataMart(dataSource, userId, valueDate, appDate, true);
		insuranceDM.process("DATA_MART_REQUEST");
		
		PropertyDetailsDataMart propertyDM = new PropertyDetailsDataMart(dataSource, userId, valueDate, appDate, true);
		propertyDM.process("DATA_MART_REQUEST");
		
		ApplicantDetailsDataMart appDM = new ApplicantDetailsDataMart(dataSource, userId, valueDate, appDate, true);
		appDM.process("DATA_MART_REQUEST");
		
		AddressDetailsDataMart addDM = new AddressDetailsDataMart(dataSource, userId, valueDate, appDate, true);
		addDM.process("DATA_MART_REQUEST");
		
		ApplicationDetailsDataMart apptDm = new ApplicationDetailsDataMart(dataSource, userId, valueDate, appDate, true);
		apptDm.process("DATA_MART_REQUEST");
		
		CoApplicantDetailsDataMart coAppDM = new CoApplicantDetailsDataMart(dataSource, userId, valueDate, appDate, true);
		coAppDM.process("DATA_MART_REQUEST");
		
		OpenEcsDetailsDataMart openEcsDM = new OpenEcsDetailsDataMart(dataSource, userId, valueDate, appDate, true);
		openEcsDM.process("DATA_MART_REQUEST");
		
		SubQDisbDataMart subqDM = new SubQDisbDataMart(dataSource, userId, valueDate, appDate, true);
		subqDM.process("DATA_MART_REQUEST");
		
		IVRFlexiDataMart ivrDM = new IVRFlexiDataMart(dataSource, userId, valueDate, appDate, true);
		ivrDM.process("DATA_MART_REQUEST");
		
		
		logger.debug(Literal.LEAVING);
	}

}
