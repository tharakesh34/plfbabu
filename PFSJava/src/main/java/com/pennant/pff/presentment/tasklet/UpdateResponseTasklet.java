package com.pennant.pff.presentment.tasklet;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.pennant.backend.util.RepayConstants;
import com.pennant.pff.presentment.dao.PresentmentDAO;
import com.pennanttech.pennapps.core.resource.Literal;

public class UpdateResponseTasklet implements Tasklet {
	private final Logger logger = LogManager.getLogger(UpdateResponseTasklet.class);

	private PresentmentDAO presentmentDAO;

	public UpdateResponseTasklet(PresentmentDAO presentmentDAO) {
		this.presentmentDAO = presentmentDAO;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		logger.debug(Literal.ENTERING);

		JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();

		Long batchId = jobParameters.getLong("BATCH_ID");
		String responseType = jobParameters.getString("RESPONSE_TYPE");

		synchronized (batchId) {
			List<Long> list = presentmentDAO.getResponseHeadersByBatch(batchId, responseType);

			for (Long headerId : list) {
				updatePresentmentHeader(headerId);
			}
		}

		logger.debug(Literal.LEAVING);
		return RepeatStatus.FINISHED;
	}

	private synchronized void updatePresentmentHeader(long headerId) {
		List<Long> list = presentmentDAO.getPresentmentIdListByRespBatch(headerId);

		if (list.size() == 0) {
			return;
		}

		int totalRecords = 0;
		int successRecords = 0;
		int failedRecords = 0;

		for (Long presentmentDetailID : list) {
			List<String> statusList = presentmentDAO.getStatusByPresentmentHeader(presentmentDetailID);
			int successCount = 0;
			int failedCount = 0;
			int totalCount = statusList.size();

			for (String sts : statusList) {
				if (RepayConstants.PEXC_SUCCESS.equals(sts) || RepayConstants.PEXC_BOUNCE.equals(sts)) {
					successCount++;
				} else if (RepayConstants.PEXC_FAILURE.equals(sts)) {
					failedCount++;
				}
			}

			presentmentDAO.updateHeaderCounts(presentmentDetailID, successCount, failedCount);

			long presentmentId = presentmentDAO.getPresentmentDetailPresenmentId(presentmentDetailID);

			int presentmentSuccessRecords = presentmentDAO.getPresentmentSuccessRecords(presentmentId);

			if (presentmentSuccessRecords == (successCount + failedCount)) {

				presentmentDAO.updateHeaderStatus(presentmentId, RepayConstants.PEXC_RECEIVED);
			}

			totalRecords = totalRecords + totalCount;
			successRecords = successRecords + successCount;
			failedRecords = failedRecords + failedCount;

			String remarks = getRemarks(totalRecords, successRecords, failedRecords);
			String status = "S";

			if (failedRecords > 0) {
				status = "F";
			}

			presentmentDAO.updateResponseHeader(headerId, totalRecords, successRecords, failedRecords, status, remarks);

		}
	}

	private String getRemarks(int totalCount, int successCount, int failedCount) {
		StringBuilder remarks = new StringBuilder();

		if (totalCount > 0) {
			if (failedCount > 0) {
				remarks.append(" Completed with exceptions, total Records: ");
				remarks.append(totalCount);
				remarks.append(", Success: ");
				remarks.append(successCount + ".");
				remarks.append(", Failure: ");
				remarks.append(failedCount + ".");
			} else {
				remarks.append(" Completed successfully, total Records: ");
				remarks.append(totalCount);
				remarks.append(", Success: ");
				remarks.append(successCount + ".");
			}
		}

		return remarks.toString();
	}

}
