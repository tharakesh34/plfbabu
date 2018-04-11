package com.pennant.backend.endofday.tasklet;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.customerqueuing.CustomerGroupQueuing;
import com.pennant.backend.service.limitservice.LimitRebuild;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerGroupQueuingDAO;
import com.pennant.eod.dao.CustomerQueuingDAO;

public class LimitCustomerGroupsUpdate implements Tasklet {
	private Logger					logger	= Logger.getLogger(LimitCustomerGroupsUpdate.class);

	@SuppressWarnings("unused")
	private DataSource			dataSource;
	private CustomerGroupQueuingDAO	customerGroupQueuingDAO;
	private CustomerQueuingDAO customerQueuingDAO;
	private LimitRebuild limitRebuild;
	private PlatformTransactionManager	transactionManager;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getAppValueDate();
		logger.debug("START: Limit Customer Groups On : " + valueDate);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		// get the Customer Groups list
		List<CustomerGroupQueuing> customerGroupIdList = this.customerGroupQueuingDAO.getCustomerGroupsList();

		if (customerGroupIdList != null && !customerGroupIdList.isEmpty()) {

			for (CustomerGroupQueuing customerGroupQueuing : customerGroupIdList) {

				long custGroupId = customerGroupQueuing.getGroupId();

				try {
					customerGroupQueuing.setProgress(EodConstants.PROGRESS_IN_PROCESS);
					this.customerGroupQueuingDAO.updateProgress(customerGroupQueuing);
					this.customerQueuingDAO.insertCustomerQueueing(custGroupId, true);

					// begin transaction
					txStatus = transactionManager.getTransaction(txDef);

					// Limit Customer Group Rebuild
 					this.limitRebuild.processCustomerGroupRebuild(custGroupId, false, false);

					this.customerGroupQueuingDAO.updateStatus(custGroupId, EodConstants.PROGRESS_SUCCESS);
					this.customerQueuingDAO.updateCustomerQueuingStatus(custGroupId, EodConstants.PROGRESS_SUCCESS);

					// commit
					transactionManager.commit(txStatus);
				} catch (Exception e) {
					logError(e);
					transactionManager.rollback(txStatus);
					updateFailed(custGroupId);
				}
			}
		}

		logger.debug("COMPLETE: Limit Customer Groups On :" + valueDate);

		return RepeatStatus.FINISHED;
	}

	public void updateFailed(long groupId) {
		CustomerGroupQueuing customerGroupQueuing = new CustomerGroupQueuing();
		customerGroupQueuing.setGroupId(groupId);
		customerGroupQueuing.setEndTime(DateUtility.getSysDate());
		// reset to "wait", to re run only failed cases.
		customerGroupQueuing.setProgress(EodConstants.PROGRESS_WAIT);
		customerGroupQueuingDAO.updateFailed(customerGroupQueuing);
	}

	private void logError(Exception exp) {
		logger.error("Cause : " + exp.getCause());
		logger.error("Message : " + exp.getMessage());
		logger.error("LocalizedMessage : " + exp.getLocalizedMessage());
		logger.error("StackTrace : ", exp);
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setLimitRebuild(LimitRebuild limitRebuild) {
		this.limitRebuild = limitRebuild;
	}

	public void setCustomerGroupQueuingDAO(CustomerGroupQueuingDAO customerGroupQueuingDAO) {
		this.customerGroupQueuingDAO = customerGroupQueuingDAO;
	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
