package com.pennant.pff.presentment.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.presentment.ExtractionJobManager;
import com.pennant.pff.presentment.dao.PresentmentDAO;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

@Configuration
public class ExtractionService {

	@Autowired
	private PresentmentDAO presentmentDAO;

	@Autowired
	private ExtractionJobManager extractionJobManager;

	@Autowired
	private DueExtractionConfigService dueExtractionConfigService;

	@Autowired
	private DataSourceTransactionManager transactionManager;

	@Autowired
	private PresentmentEngine presentmentEngine;

	public ExtractionService() {
		super();
	}

	public int extractPresentment() {
		extractDueConfig();

		PresentmentHeader ph = new PresentmentHeader();
		ph.setPresentmentType(PennantConstants.PROCESS_PRESENTMENT);

		ph.setAutoExtract(true);

		return extract(ph);
	}

	public int extractPresentment(PresentmentHeader ph) {
		return extract(ph);
	}

	private void extractDueConfig() {
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
	}

	public int extract(PresentmentHeader ph) {
		Date appDate = SysParamUtil.getAppDate();
		long batchID = presentmentDAO.createBatch("EXTRACTOIN");

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
			throw new AppException("Presentment extraction failed", e);
		}

		if (count > 0) {
			try {
				extractionJobManager.extractPresentment(ph);
			} catch (Exception e) {
				presentmentDAO.clearQueue(batchID);
				throw e;
			}
		}

		return count;
	}

}
