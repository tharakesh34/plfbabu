package com.pennanttech.external.extractions.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennanttech.external.app.config.dao.ExtStagingDao;
import com.pennanttech.external.extractions.model.AlmExtract;
import com.pennanttech.pennapps.core.resource.Literal;

public class ALMDumpService {
	private static final Logger logger = LogManager.getLogger(ALMDumpService.class);
	private ExtStagingDao extStageDao;

	public void processALM(CustEODEvent custEODEvent) {
		List<FinEODEvent> finEods = custEODEvent.getFinEODEvents();
		for (FinEODEvent finEOD : finEods) {
			FinanceProfitDetail fpd = finEOD.getFinProfitDetail();
			FinanceMain fm = finEOD.getFinanceMain();
			AlmExtract almExtract = new AlmExtract();
			almExtract.setAlmReportType("RD");// DEFAULT
			almExtract.setAlmReportDate(custEODEvent.getEventProperties().getAppDate());
			almExtract.setAccountNumber(fpd.getFinReference());
			almExtract.setAccrualBasis("B");// DEFAULT
			almExtract.setAccruedInterest(fpd.getAmzTillLBD());
			almExtract.setBankNumber("");// FIXME // Fetch from query
			almExtract.setBranch(fpd.getFinBranch());
			almExtract.setCompFreq(0l);// DEFAULT
			almExtract.setCompFreqIncr(0l);// DEFAULT
			almExtract.setCurrencyCode("INR");// DEFAULT

			List<FinExcessAmount> excessAmts = new ArrayList<FinExcessAmount>();
			BigDecimal excessBalance = new BigDecimal("0");
			if (finEOD.getFinExcessAmounts() != null) {
				excessAmts = finEOD.getFinExcessAmounts();
				if (!excessAmts.isEmpty()) {
					for (FinExcessAmount excessAmount : excessAmts) {
						excessBalance = excessBalance.add(excessAmount.getBalanceAmt());
					}
				}
			}

			almExtract.setCurrentBalance((fpd.getTotalPriBal().subtract(excessBalance)));
			almExtract.setDueDate(formatData(fpd.getNSchdDate()));
			almExtract.setInitRate(fm.getRepayProfitRate());
			almExtract.setLifeCeiling(fm.getRepayProfitRate());
			almExtract.setLifeFloor(fm.getRepayProfitRate());
			almExtract.setLoanType(fm.getFinType());
			almExtract.setMaturity(formatData(fm.getMaturityDate()));
			almExtract.setOriginalBalance(fm.getFinAmount());
			almExtract.setOriginalTerm(fpd.getTotalTenor());
			almExtract.setOriginationDate(fm.getFinApprovedDate());
			almExtract.setInstalment(fpd.getNSchdPri().add(fpd.getNSchdPft()));
			almExtract.setPaymentFreq(fpd.getRepayFrq());
			almExtract.setInitPaymentFreq(1);
			almExtract.setPaymentType(7);
			almExtract.setPctOwned(100);
			almExtract.setRateFlag("F");
			almExtract.setRePriceIndex("PLR1");
			almExtract.setDpd(fpd.getCurODDays());
			almExtract.setTotalInterest(fpd.getTotalPftSchd());
			almExtract.setBilledInterest(fpd.getTdSchdPft());
			almExtract.setBilledNotReceivedInterest(fpd.getODProfit());
			almExtract.setBilledNotReceivedPrincipal(fpd.getODPrincipal());
			almExtract.setCustomerName(fm.getCustAcctHolderName());// FIXME
			almExtract.setPretaxirr(new BigDecimal("0"));// TBD
			almExtract.setSchemeId(0l);// TDB
			almExtract.setProfessionCode(fm.getCustEmpType());// FIXME
			almExtract.setBrokerId(0l);// FIXME
			almExtract.setPslctgid(0l);// FIXME
			almExtract.setNpaStageId("");// FIXME
			almExtract.setWeakerSectionDesc("");// FIXME TBD
			extStageDao.saveAlmExtractionDataToTable(almExtract);
		}
	}

	private Date formatData(Date date) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
			String strDate = formatter.format(date);
			return formatter.parse(strDate);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		return null;
	}

	public void setExtStageDao(ExtStagingDao extStageDao) {
		this.extStageDao = extStageDao;
	}

}
