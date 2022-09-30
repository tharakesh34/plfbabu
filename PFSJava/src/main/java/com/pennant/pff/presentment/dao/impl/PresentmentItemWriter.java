package com.pennant.pff.presentment.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.transaction.TransactionManager;

import com.pennant.pff.presentment.service.PresentmentEngine;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public class PresentmentItemWriter extends BasicDao<PresentmentDetail> implements ItemWriter<PresentmentDetail> {

	private PresentmentEngine presentmentEngine;

	private TransactionManager transactionManager;

	public PresentmentItemWriter(TransactionManager transactionManager, PresentmentEngine presentmentEngine) {
		this.transactionManager = transactionManager;
		this.presentmentEngine = presentmentEngine;
	}

	@Override
	public void write(List<? extends PresentmentDetail> presentments) throws Exception {
		List<PresentmentDetail> list = new ArrayList<>();

		presentments.forEach(pd -> list.add(pd));

		presentmentEngine.save(list);
	}

}
