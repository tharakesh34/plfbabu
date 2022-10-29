package com.pennant.pff.presentment.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.pff.presentment.service.PresentmentEngine;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public class PresentmentItemWriter extends BasicDao<PresentmentDetail> implements ItemWriter<PresentmentDetail> {

	private PresentmentEngine presentmentEngine;

	private DataSourceTransactionManager transactionManager;

	public PresentmentItemWriter(DataSourceTransactionManager transactionManager, PresentmentEngine presentmentEngine) {
		this.transactionManager = transactionManager;
		this.presentmentEngine = presentmentEngine;
	}

	@Override
	public void write(List<? extends PresentmentDetail> presentments) throws Exception {
		List<PresentmentDetail> list = new ArrayList<>();

		presentments.forEach(pd -> list.add(pd));

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus transactionStatus = this.transactionManager.getTransaction(txDef);

		try {
			presentmentEngine.save(list);
			transactionManager.commit(transactionStatus);
		} catch (Exception e) {
			transactionManager.rollback(transactionStatus);
		}

	}

}
