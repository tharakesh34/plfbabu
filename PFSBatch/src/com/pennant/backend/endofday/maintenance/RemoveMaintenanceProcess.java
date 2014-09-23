package com.pennant.backend.endofday.maintenance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.util.PennantConstants;

public class RemoveMaintenanceProcess implements Tasklet {
	private Logger logger = Logger.getLogger(RemoveMaintenanceProcess.class);
	
	private DataSource dataSource;
	
	private Date dateValueDate = null;
	private ExecutionContext stepExecutionContext;

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {	
		
		dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_VALUE).toString());

		logger.debug("START: Finance Maintenance Details Removal for Value Date: "+ dateValueDate);		

		stepExecutionContext = context.getStepContext().getStepExecution().getExecutionContext();	
		stepExecutionContext.put(context.getStepContext().getStepExecution().getId().toString(), dateValueDate);

		Connection connection = null;
		PreparedStatement sqlStatement = null;
		
		try {
			
			//Fetch Connection From DataSource Utils
			connection = DataSourceUtils.doGetConnection(getDataSource());
			
			//=======================================================================//
			//####Remove Child tables(_Temp) for Finance which are in Maintenance####//
			//=======================================================================//
			
			//1. Finance Schedule Details
			sqlStatement = connection.prepareStatement(prepareDeleteQuery("FinScheduleDetails", "FinReference",true, true));
			sqlStatement.executeUpdate();
			
			//2. Finance Disbursement Details
			sqlStatement = connection.prepareStatement(prepareDeleteQuery("FinDisbursementDetails", "FinReference", true, true));
			sqlStatement.executeUpdate();
			
			//3. Finance Deferment Header Details
			sqlStatement = connection.prepareStatement(prepareDeleteQuery("FinDefermentHeader", "FinReference", true, true));
			sqlStatement.executeUpdate();
			
			//4. Finance Deferment Details
			sqlStatement = connection.prepareStatement(prepareDeleteQuery("FinDefermentDetail", "FinReference", true, true));
			sqlStatement.executeUpdate();
			
			//5. Finance Repay Instruction Details
			sqlStatement = connection.prepareStatement(prepareDeleteQuery("FinRepayInstruction", "FinReference", true, true));
			sqlStatement.executeUpdate();
			
			//6. Finance Fee Charge Details
			sqlStatement = connection.prepareStatement(prepareDeleteQuery("FinFeeCharges", "FinReference", true, true));
			sqlStatement.executeUpdate();
			
			//7. Finance Repay Header Details
			sqlStatement = connection.prepareStatement(prepareDeleteQuery("FinRepayHeader", "FinReference", true, true));
			sqlStatement.executeUpdate();
			
			//8. Finance Repay Schedule Details
			sqlStatement = connection.prepareStatement(prepareDeleteQuery("FinRepayScheduleDetail", "FinReference", true, true));
			sqlStatement.executeUpdate();
		
			//9. Finance Provision Details
			sqlStatement = connection.prepareStatement(prepareDeleteQuery("FinProvisions", "FinReference", false, false));
			sqlStatement.executeUpdate();
			
			//10. Finance Suspense Head Details
			sqlStatement = connection.prepareStatement(prepareDeleteQuery("FinSuspHead", "FinReference", false, false));
			sqlStatement.executeUpdate();
			
			//11. Finance Write-off Details
			sqlStatement = connection.prepareStatement(prepareDeleteQuery("FinWriteoffDetail", "FinReference", true, true));
			sqlStatement.executeUpdate();
			
			//12. Finance Document Details
			sqlStatement = connection.prepareStatement(prepareDeleteQuery("DocumentDetails", "ReferenceId", true, true));
			sqlStatement.executeUpdate();
			
			//13. Finance Premium Details
			sqlStatement = connection.prepareStatement(prepareDeleteQuery("FinancePremiumDetail", "FinReference", true, true));
			sqlStatement.executeUpdate();
			
			//14. Finance Contractor Asset Details
			sqlStatement = connection.prepareStatement(prepareDeleteQuery("FinContractorAssetDetails", "FinReference", true, true));
			sqlStatement.executeUpdate();
			
			//15. Finance Main Details
			sqlStatement = connection.prepareStatement(prepareDeleteQuery("FinanceMain", "FinReference", false, true));
			sqlStatement.executeUpdate();
			
		}catch (Exception e) {
			logger.error(e);
			throw e;
		}finally {
			sqlStatement.close();
		}

		logger.debug("END: Finance Maintenance Details Removal for Value Date: "+ dateValueDate);		
		return RepeatStatus.FINISHED;
	}
	
	/**
	 * Method for preparation of Update Query To update 
	 * 
	 * @param updateQuery
	 * @return
	 */
	private String prepareDeleteQuery(String tableName, String refName, boolean isSubTable, boolean incWhereCond) {

		StringBuilder deleteQuery = new StringBuilder(" Delete From "+tableName+"_Temp ");
		if(incWhereCond){
			if(isSubTable){
				deleteQuery.append(" Where ");
				deleteQuery.append(refName);
				deleteQuery.append(" IN (Select FinReference From FinanceMain_Temp ");
				deleteQuery.append(" Where RecordType != 'NEW' AND RcdMaintainSts !='') ");
			}else{
				deleteQuery.append(" Where RecordType != 'NEW' AND RcdMaintainSts !='' ");
			}
		}
		return deleteQuery.toString();

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public DataSource getDataSource() {
		return dataSource;
	}
	
}
