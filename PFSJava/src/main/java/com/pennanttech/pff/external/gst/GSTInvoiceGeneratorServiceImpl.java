package com.pennanttech.pff.external.gst;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.backend.dao.finance.GSTInvoiceTxnDAO;
import com.pennant.backend.model.applicationmaster.TaxDetail;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.GSTInvoiceTxn;
import com.pennant.backend.model.finance.SeqGSTInvoice;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.systemmasters.ProvinceService;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class GSTInvoiceGeneratorServiceImpl extends SequenceDao<DocumentDetails> implements GSTInvoiceGeneratorService {
	private static final Logger logger = LogManager.getLogger(GSTInvoiceGeneratorServiceImpl.class);
	private GSTInvoiceTxnDAO gstInvoiceTxnDAO;
	private ProvinceService provinceService;

	private DataSourceTransactionManager transManager;
	private DefaultTransactionDefinition transDef;

	/**
	 * generate Invoice number
	 */
	@Override
	public void generateInvoice() {
		logger.debug(Literal.ENTERING);

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

			String entityCode = "";

			if (StringUtils.isNotBlank(companyCode)) {
				if (companyCode.length() >= 2) {
					entityCode = companyCode.substring(0, 2);
				} else {
					entityCode = companyCode;
				}
			}
			String gstStateCode = gsTIN.substring(0, 2);

			Date invoiceDate = gstInvoiceTxn.getInvoiceDate();

			String month = String.valueOf(DateUtil.getMonth(invoiceDate));
			month = StringUtils.leftPad(month, 2, "0");
			String year = String.valueOf(DateUtil.getYear(invoiceDate));
			year = year.substring(2);

			TransactionStatus txnStatus = null;
			SeqGSTInvoice temp = null;

			try {
				SeqGSTInvoice seqGSTInvoice = new SeqGSTInvoice();
				seqGSTInvoice.setTransactionType(invoiceType);
				seqGSTInvoice.setStateCode(gstStateCode);
				seqGSTInvoice.setEntityCode(entityCode);

				temp = this.gstInvoiceTxnDAO.getSeqNoFromSeqGSTInvoice(seqGSTInvoice);
				if (temp == null) {
					createGSTSequence(gsTIN, entityCode);
				}

				temp = this.gstInvoiceTxnDAO.getSeqNoFromSeqGSTInvoice(seqGSTInvoice);

				String monthYear = temp.getMonthYear();
				long seqNo = temp.getSeqNo();

				String currentMontYear = DateUtil.format(invoiceDate, "MMyy");

				if (!monthYear.equals(currentMontYear)) {
					seqNo = 0;
					temp.setMonthYear(currentMontYear);
				} else {
					temp.setMonthYear(null);
				}

				seqNo = seqNo + 1;
				temp.setSeqNo(seqNo);

				String strSeqNo = StringUtils.leftPad(String.valueOf(seqNo), 7, "0");
				StringBuilder invoiceNo = new StringBuilder(entityCode);
				invoiceNo.append(gstStateCode);
				invoiceNo.append(month);
				invoiceNo.append(year);
				invoiceNo.append(invoiceType);
				invoiceNo.append(strSeqNo);

				gstInvoiceTxn.setInvoiceNo(invoiceNo.toString());
				seqGSTInvoice.setSeqNo(seqNo);

				txnStatus = transManager.getTransaction(transDef);

				this.gstInvoiceTxnDAO.updateGSTInvoiceNo(gstInvoiceTxn);
				this.gstInvoiceTxnDAO.updateSeqGSTInvoice(temp);

				transManager.commit(txnStatus);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
				if (txnStatus != null) {
					transManager.rollback(txnStatus);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void createGSTSequence(String gsTIN, String entityCode) {
		Province province = new Province();
		province.setTaxStateCode(gsTIN);

		TaxDetail td1 = new TaxDetail();
		td1.setEntityCode(entityCode);

		TaxDetail td2 = new TaxDetail();
		td2.setEntityCode(entityCode);

		TaxDetail td3 = new TaxDetail();
		td3.setEntityCode(entityCode);

		TaxDetail td4 = new TaxDetail();
		td4.setEntityCode(entityCode);

		province.getTaxDetailList().add(td1);
		province.getTaxDetailList().add(td2);
		province.getTaxDetailList().add(td3);
		province.getTaxDetailList().add(td4);

		TransactionStatus txnStatus = transManager.getTransaction(transDef);
		try {
			provinceService.saveSeqGstInvoice(province);

			transManager.commit(txnStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			if (txnStatus != null) {
				transManager.rollback(txnStatus);
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

	public ProvinceService getProvinceService() {
		return provinceService;
	}

	public void setProvinceService(ProvinceService provinceService) {
		this.provinceService = provinceService;
	}

}