package com.pennant.backend.endofday.tasklet;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.backend.model.customerqueuing.CustomerGroupQueuing;
import com.pennant.backend.service.limitservice.LimitRebuild;
import com.pennant.backend.util.BatchUtil;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerGroupQueuingDAO;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;

public class LimitCustomerGroupsUpdate implements Tasklet {
	private Logger logger = LogManager.getLogger(LimitCustomerGroupsUpdate.class);

	private CustomerGroupQueuingDAO customerGroupQueuingDAO;
	private CustomerQueuingDAO customerQueuingDAO;
	private LimitRebuild limitRebuild;
	private PlatformTransactionManager transactionManager;

	public LimitCustomerGroupsUpdate() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date valueDate = EODUtil.getDate("APP_VALUEDATE", context);

		logger.info("START Limit Customer Groups On {}", valueDate);
		BatchUtil.setExecutionStatus(context, StepUtil.CUSTOMER_GROUP_LIMITS_UPDATE);

		/* Get the Customer Groups list */
		List<CustomerGroupQueuing> customerGroupIdList = this.customerGroupQueuingDAO.getCustomerGroupsList();

		if (CollectionUtils.isEmpty(customerGroupIdList)) {
			return RepeatStatus.FINISHED;
		}

		StepUtil.CUSTOMER_GROUP_LIMITS_UPDATE.setTotalRecords(customerGroupIdList.size());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		int processedRecords = 0;
		int failureRecords = 0;
		for (CustomerGroupQueuing customerGroupQueuing : customerGroupIdList) {
			long custGroupId = customerGroupQueuing.getGroupId();
			StepUtil.CUSTOMER_GROUP_LIMITS_UPDATE.setProcessedRecords(++processedRecords);

			try {
				customerGroupQueuing.setProgress(EodConstants.PROGRESS_IN_PROCESS);
				this.customerGroupQueuingDAO.updateProgress(customerGroupQueuing);
				this.customerQueuingDAO.insertCustomerQueueing(custGroupId, true);

				/* Begin transaction */
				txStatus = transactionManager.getTransaction(txDef);

				/* Limit Customer Group Rebuild */
				this.limitRebuild.processCustomerGroupRebuild(custGroupId, false, false);

				this.customerGroupQueuingDAO.updateStatus(custGroupId, EodConstants.PROGRESS_SUCCESS);
				this.customerQueuingDAO.updateCustomerQueuingStatus(custGroupId, EodConstants.PROGRESS_SUCCESS);

				/* Commit */
				transactionManager.commit(txStatus);
			} catch (Exception e) {
				StepUtil.CUSTOMER_GROUP_LIMITS_UPDATE.setFailedRecords(++failureRecords);
				logError(e);
				transactionManager.rollback(txStatus);
				updateFailed(custGroupId);
			}
		}

		logger.info("COMPLETE Limit Customer Groups On  {}", valueDate);

		return RepeatStatus.FINISHED;
	}

	public void updateFailed(long groupId) {
		CustomerGroupQueuing customerGroupQueuing = new CustomerGroupQueuing();
		customerGroupQueuing.setGroupId(groupId);
		customerGroupQueuing.setEndTime(DateUtil.getSysDate());
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

	@Autowired
	public void setLimitRebuild(LimitRebuild limitRebuild) {
		this.limitRebuild = limitRebuild;
	}

	@Autowired
	public void setCustomerGroupQueuingDAO(CustomerGroupQueuingDAO customerGroupQueuingDAO) {
		this.customerGroupQueuingDAO = customerGroupQueuingDAO;
	}

	@Autowired
	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

	@Autowired
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
