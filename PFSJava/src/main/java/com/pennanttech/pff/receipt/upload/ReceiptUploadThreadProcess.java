package com.pennanttech.pff.receipt.upload;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.receiptuploadqueue.ReceiptUploadQueuing;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.ReceiptUploadDetailDAO;
import com.pennant.backend.dao.finance.UploadAllocationDetailDAO;
import com.pennant.backend.dao.receiptUpload.ProjectedRUDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.receiptupload.UploadAlloctionDetail;
import com.pennant.backend.service.finance.ReceiptService;
import com.pennant.backend.service.finance.ReceiptUploadHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.ReceiptUploadConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;

public class ReceiptUploadThreadProcess {
	private static Logger logger = LogManager.getLogger(ReceiptUploadThreadProcess.class);

	private static final String QUERY = "Select FinReference, uploadheaderid, uploaddetailid from ReceiptUploadQueuing  Where ThreadID = :ThreadId and Progress = :Progress order by uploaddetailid";

	private DataSource dataSource;
	private ProjectedRUDAO projectedRUDAO;
	private ReceiptUploadDetailDAO receiptUploadDetailDAO;
	private UploadAllocationDetailDAO uploadAllocationDetailDAO;
	private ReceiptService receiptService;
	private LoggedInUser loggedInUser;
	private ReceiptUploadHeaderService receiptUploadHeaderService;

	private NamedParameterJdbcTemplate jdbcTemplate;
	private DataSourceTransactionManager transactionManager;
	private DefaultTransactionDefinition transactionDefinition;

	public ReceiptUploadThreadProcess(DataSource dataSource, ProjectedRUDAO projectedRUDAO,
			ReceiptUploadDetailDAO receiptUploadDetailDAO, ReceiptService receiptService,
			UploadAllocationDetailDAO uploadAllocationDetailDAO, LoggedInUser loggedInUser,
			ReceiptUploadHeaderService receiptUploadHeaderService) {
		super();

		this.dataSource = dataSource;
		this.projectedRUDAO = projectedRUDAO;
		this.receiptUploadDetailDAO = receiptUploadDetailDAO;
		this.receiptService = receiptService;
		this.loggedInUser = loggedInUser;
		this.uploadAllocationDetailDAO = uploadAllocationDetailDAO;
		this.receiptUploadHeaderService = receiptUploadHeaderService;

		initilize();
	}

	public void processesThread(long threadId) {
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Progress", EodConstants.PROGRESS_WAIT);
		source.addValue("ThreadId", threadId);

		jdbcTemplate.query(QUERY, source, new RowCallbackHandler() {
			ReceiptUploadDetail uploadDetail = null;

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				long uploadheaderid = rs.getLong(2);
				long uploaddetailid = rs.getLong(3);

				uploadDetail = receiptUploadDetailDAO.getUploadReceiptDetail(uploadheaderid, uploaddetailid);
				uploadDetail.setLoggedInUser(loggedInUser);

				if (StringUtils.equals(uploadDetail.getAllocationType(), "M")) {
					List<UploadAlloctionDetail> listAllocationDetails = new ArrayList<>();
					listAllocationDetails = uploadAllocationDetailDAO.getUploadedAllocatations(uploaddetailid);
					uploadDetail.setListAllocationDetails(listAllocationDetails);
				}

				processReceipt(uploadDetail);
			}
		});
	}

	private void processReceipt(ReceiptUploadDetail uploadDetail) {
		long uploadheaderid = uploadDetail.getUploadheaderId();
		long uploaddetailid = uploadDetail.getUploadDetailId();

		TransactionStatus transactionStatus = this.transactionManager.getTransaction(transactionDefinition);

		try {
			postExternalReceipt(uploadDetail);

			this.receiptUploadDetailDAO.updateStatus(uploadDetail);

			this.projectedRUDAO.updateStatusQueue(uploadheaderid, uploaddetailid,
					ReceiptUploadConstants.PROGRESS_SUCCESS);

			this.transactionManager.commit(transactionStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			transactionManager.rollback(transactionStatus);

			String error = StringUtils.trimToEmpty(e.getMessage());

			if (error.length() > 1999) {
				error = error.substring(0, 1999);
			}
			updateFailed(uploadheaderid, uploaddetailid, error);

			uploadDetail.setUploadStatus(PennantConstants.UPLOAD_STATUS_FAIL);
			uploadDetail.setReceiptId(0);
			uploadDetail.setReason(error);
			this.receiptUploadDetailDAO.updateStatus(uploadDetail);
		}
	}

	private void postExternalReceipt(ReceiptUploadDetail uploadDetail) {
		FinServiceInstruction fsi = receiptService.buildFinServiceInstruction(uploadDetail, "");
		fsi.setReqType("Post");
		fsi.setReceiptUpload(true);
		fsi.setLoggedInUser(uploadDetail.getLoggedInUser());
		FinanceDetail financeDetail = receiptService.receiptTransaction(fsi, fsi.getReceiptPurpose());

		WSReturnStatus returnStatus = financeDetail.getReturnStatus();
		if (returnStatus != null) {
			uploadDetail.setUploadStatus(PennantConstants.UPLOAD_STATUS_FAIL);

			String code = StringUtils.trimToEmpty(returnStatus.getReturnCode());
			String description = StringUtils.trimToEmpty(returnStatus.getReturnText());

			uploadDetail.setReason(String.format("%s %s %s", code, "-", description));
		} else {
			uploadDetail.setUploadStatus(PennantConstants.UPLOAD_STATUS_SUCCESS);
			uploadDetail.setReason("");
		}
	}

	private void updateFailed(long uploadHeaderId, long uploadDetailId, String errorLog) {
		ReceiptUploadQueuing ruQueuing = new ReceiptUploadQueuing();

		ruQueuing.setUploadHeaderId(uploadHeaderId);
		ruQueuing.setUploadDetailId(uploadDetailId);
		ruQueuing.setEndTime(DateUtility.getSysDate());
		ruQueuing.setErrorLog(errorLog);
		projectedRUDAO.updateFailedQueue(ruQueuing);
	}

	private void initilize() {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		this.transactionManager = new DataSourceTransactionManager(dataSource);
		this.transactionDefinition = new DefaultTransactionDefinition();
		this.transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		this.transactionDefinition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);

		//FIXME: PV change the time to 60 seocnds after code review completed
		this.transactionDefinition.setTimeout(600);

	}
}
