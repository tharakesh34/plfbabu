package com.pennanttech.pff.external.gst;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.GSTInvoiceTxnDAO;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.GSTInvoiceTxn;
import com.pennant.backend.model.finance.SeqGSTInvoice;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

public class GSTInvoiceGeneratorServiceImpl extends SequenceDao<DocumentDetails> implements GSTInvoiceGeneratorService {
	private GSTInvoiceTxnDAO gstInvoiceTxnDAO;

	private DataSourceTransactionManager transManager;
	private DefaultTransactionDefinition transDef;

	/**
	 * generate Invoice number
	 */
	@Override
	public void generateInvoice() {
		List<GSTInvoiceTxn> gstInvoiceList = this.gstInvoiceTxnDAO.getGSTInvoiceTxnList();

		if (CollectionUtils.isEmpty(gstInvoiceList)) {
			return;
		}

		for (GSTInvoiceTxn gstInvoiceTxn : gstInvoiceList) {
			String gsTIN = StringUtils.trimToNull(gstInvoiceTxn.getCompany_GSTIN());
			String invoiceType = StringUtils.trimToNull(gstInvoiceTxn.getInvoiceType());
			String companyCode = gstInvoiceTxn.getCompanyCode();

			if (gsTIN == null && invoiceType == null) {
				continue;
			}

			SeqGSTInvoice seqGSTInvoice = new SeqGSTInvoice();
			seqGSTInvoice.setTransactionType(invoiceType);

			String entityCode = "";

			if (StringUtils.isNotBlank(companyCode)) {
				if (companyCode.length() >= 2) {
					entityCode = companyCode.substring(0, 2);
				} else {
					entityCode = companyCode;
				}
			}
			String gstStateCode = gsTIN.substring(0, 2);
			String appDate = SysParamUtil.getAppDate(DateFormat.SHORT_DATE);

			String[] dateformat = appDate.split("/");
			String month = dateformat[1];
			String year = dateformat[2].substring(2, 4);

			seqGSTInvoice.setGstStateCode(gstStateCode);

			TransactionStatus txnStatus = null;
			try {

				long seqNo = this.gstInvoiceTxnDAO.getSeqNoFromSeqGSTInvoice(seqGSTInvoice);
				seqNo = seqNo + 1;

				String invoiceNo = entityCode + gstStateCode + month + year + invoiceType
						+ StringUtils.leftPad(String.valueOf(seqNo), 7, "0");

				gstInvoiceTxn.setInvoiceNo(invoiceNo);
				seqGSTInvoice.setSeqNo(seqNo);

				txnStatus = transManager.getTransaction(transDef);
				this.gstInvoiceTxnDAO.updateGSTInvoiceNo(gstInvoiceTxn);

				this.gstInvoiceTxnDAO.updateSeqGSTInvoice(seqGSTInvoice);

				transManager.commit(txnStatus);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
				if (txnStatus != null) {
					transManager.rollback(txnStatus);
				}
			}
		}
	}

	public void setGstInvoiceTxnDAO(GSTInvoiceTxnDAO gstInvoiceTxnDAO) {
		this.gstInvoiceTxnDAO = gstInvoiceTxnDAO;
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.transManager = new DataSourceTransactionManager(dataSource);
		this.transDef = new DefaultTransactionDefinition();
		this.transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		this.transDef.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		this.transDef.setTimeout(60);
	}

}