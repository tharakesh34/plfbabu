package com.pennant.pff.presentment.service;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.presentment.ExtractionJob;
import com.pennant.pff.presentment.dao.PresentmentDAO;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

@Configuration
public class ExtractionService {
	private Logger logger = LogManager.getLogger(ExtractionService.class);

	@Autowired
	private PresentmentDAO presentmentDAO;

	@Autowired
	private DueExtractionConfigService dueExtractionConfigService;

	@Autowired
	private DataSourceTransactionManager transactionManager;

	@Autowired
	private PresentmentEngine presentmentEngine;

	@Autowired
	private ExtractionJob extractionJob;

	public ExtractionService() {
		super();
	}

	public int preparePresentment() {
		logger.debug(Literal.ENTERING);

		extractDueConfig();

		PresentmentHeader ph = new PresentmentHeader();
		ph.setPresentmentType(PennantConstants.PROCESS_PRESENTMENT);
		ph.setAutoExtract(true);

		return prepare(ph);
	}

	public int preparePresentment(PresentmentHeader ph) {
		return prepare(ph);
	}

	private void extractDueConfig() {
		logger.debug(Literal.ENTERING);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus transactionStatus = this.transactionManager.getTransaction(txDef);

		try {
			dueExtractionConfigService.extarctDueConfig();
			transactionManager.commit(transactionStatus);
		} catch (Exception e) {
			transactionManager.rollback(transactionStatus);
			throw new AppException("Unable to extract presentment due configuration", e);
		}

		logger.debug(Literal.LEAVING);
	}

	private int prepare(PresentmentHeader ph) {
		logger.debug(Literal.ENTERING);

		Date appDate = SysParamUtil.getAppDate();
		long batchID = presentmentDAO.createBatch("EXTRACTION", 0);

		ph.setBatchID(batchID);
		ph.setAppDate(appDate);

		int count = 0;

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus transactionStatus = this.transactionManager.getTransaction(txDef);
		try {
			count = presentmentEngine.preparation(ph);
			transactionManager.commit(transactionStatus);
		} catch (DuplicateKeyException e) {
			transactionManager.rollback(transactionStatus);
			throw new ConcurrencyException();
		} catch (Exception e) {
			transactionManager.rollback(transactionStatus);
			throw new AppException("Presentment data preparation failed", e);
		}

		if (count > 0) {
			try {
				presentmentDAO.updateTotalRecords(count, batchID);
				start(ph);
			} catch (Exception e) {
				this.presentmentDAO.deleteBatch(batchID);
				this.presentmentDAO.clearQueue(batchID);
				throw new AppException("Presentment extraction job failed", e);
			}
		} else {
			presentmentDAO.deleteBatch(batchID);
		}

		logger.debug(Literal.LEAVING);
		return count;
	}

	public void start(PresentmentHeader ph) throws Exception {
		logger.debug(Literal.ENTERING);

		Date appDate = SysParamUtil.getAppDate();

		JobParametersBuilder builder = new JobParametersBuilder();

		builder.addLong("BATCH_ID", ph.getBatchID());
		builder.addDate("AppDate", appDate);
		builder.addString("MandateType", ph.getMandateType());
		builder.addString("EmandateSource", ph.getEmandateSource());
		builder.addString("LoanType", ph.getLoanType());
		builder.addString("EntityCode", ph.getEntityCode());
		builder.addString("FinBranch", ph.getFinBranch());
		builder.addDate("FromDate", ph.getFromDate());
		builder.addDate("ToDate", ph.getToDate());
		builder.addDate("DueDate", ph.getDueDate());
		builder.addString("PresentmentType", ph.getPresentmentType());

		if (ph.isAutoExtract()) {
			builder.addString("AUTOMATION", "Y");
		} else {
			builder.addString("AUTOMATION", "N");
		}

		builder.addString("BpiPaidOnInstDate",
				(String) SysParamUtil.getValue(SMTParameterConstants.BPI_PAID_ON_INSTDATE));
		builder.addString("GroupByBank", (String) SysParamUtil.getValue(SMTParameterConstants.GROUP_BATCH_BY_BANK));

		JobParameters jobParameters = builder.toJobParameters();

		try {
			extractionJob.start(jobParameters);
		} catch (Exception e) {
			throw new AppException("Presentment Extraction Job", e);
		}

		logger.debug(Literal.LEAVING);
	}

	public int extractRePresentment(List<Long> list) {
		return extract(list);
	}

	public int extract(List<Long> list) {
		logger.debug(Literal.ENTERING);

		long batchID = presentmentDAO.createBatch("REPRE_EXTR", 0);

		int count = 0;

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus transactionStatus = this.transactionManager.getTransaction(txDef);

		try {
			count = presentmentEngine.preparationForRepresentment(batchID, list);
			transactionManager.commit(transactionStatus);
		} catch (DuplicateKeyException e) {
			transactionManager.rollback(transactionStatus);
			throw new ConcurrencyException();
		} catch (Exception e) {
			transactionManager.rollback(transactionStatus);
			throw new AppException("Presentment extraction failed", e);
		}

		if (count > 0) {
			Date appDate = SysParamUtil.getAppDate();
			PresentmentHeader ph = new PresentmentHeader();
			ph.setPresentmentType(PennantConstants.PROCESS_REPRESENTMENT);
			ph.setBatchID(batchID);
			ph.setAppDate(appDate);
			ph.setAutoExtract(false);

			try {
				start(ph);
			} catch (Exception e) {
				presentmentDAO.clearQueue(batchID);
				throw new AppException("Presentment extraction failed", e);
			}
		}

		logger.debug(Literal.LEAVING);
		return count;
	}
}
