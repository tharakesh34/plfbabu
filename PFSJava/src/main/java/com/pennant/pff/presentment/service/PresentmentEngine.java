package com.pennant.pff.presentment.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.FinEODEvent;
import com.pennant.app.core.ReceiptPaymentService;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.ReceiptCalculator;
import com.pennant.app.util.RepaymentPostingsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.mandate.MandateStatusDAO;
import com.pennant.backend.dao.pdc.ChequeDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.eventproperties.service.EventPropertiesService;
import com.pennant.backend.eventproperties.service.impl.EventPropertiesServiceImpl.EventType;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.service.finance.ReceiptCancellationService;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.service.mandate.FinMandateService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.cache.util.FinanceConfigCache;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.eod.cache.BounceConfigCache;
import com.pennant.pff.extension.MandateExtension;
import com.pennant.pff.extension.PresentmentExtension;
import com.pennant.pff.mandate.ChequeSatus;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateStatus;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.pff.presentment.PresentmentStatus;
import com.pennant.pff.presentment.dao.ConsecutiveBounceDAO;
import com.pennant.pff.presentment.dao.DueExtractionConfigDAO;
import com.pennant.pff.presentment.dao.PresentmentDAO;
import com.pennant.pff.presentment.exception.PresentmentError;
import com.pennant.pff.presentment.exception.PresentmentException;
import com.pennanttech.external.ExternalPresentmentHook;
import com.pennanttech.model.presentment.Presentment;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.util.ProductUtil;
import com.pennanttech.pff.external.PresentmentImportProcess;
import com.pennanttech.pff.external.PresentmentRequest;
import com.pennanttech.pff.overdraft.service.OverdrafLoanService;
import com.pennanttech.pff.presentment.model.ConsecutiveBounce;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.presentment.model.PresentmentHeader;
import com.pennattech.pff.receipt.model.ReceiptDTO;

public class PresentmentEngine {
	private final Logger logger = LogManager.getLogger(PresentmentEngine.class);

	public static final String STATUS_SUBMIT = "Submit";
	public static final String STATUS_APPROVE = "Approve";

	/* DAO's */
	private DueExtractionConfigDAO dueExtractionConfigDAO;
	private PresentmentDAO presentmentDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private FinanceMainDAO financeMainDAO;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private CustomerDAO customerDAO;
	private ChequeDetailDAO chequeDetailDAO;
	private PresentmentDetailDAO presentmentDetailDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private FinReceiptDetailDAO finReceiptDetailDAO;
	private FinanceRepaymentsDAO financeRepaymentsDAO;
	private ConsecutiveBounceDAO consecutiveBounceDAO;
	private MandateDAO mandateDAO;
	private MandateStatusDAO mandateStatusDAO;
	private ManualAdviseDAO manualAdviseDAO;

	/* Service's */
	private OverdrafLoanService overdrafLoanService;
	private PresentmentDetailService presentmentDetailService;
	private ReceiptPaymentService receiptPaymentService;
	private ExternalPresentmentHook externalPresentmentHook;
	private PresentmentRequest defaultPresentmentRequest;
	private PresentmentRequest presentmentRequest;
	private PostingsPreparationUtil postingsPreparationUtil;
	private ReceiptCancellationService receiptCancellationService;
	private RepaymentPostingsUtil repaymentPostingsUtil;
	private ReceiptCalculator receiptCalculator;
	private PresentmentImportProcess presentmentImportProcess;
	private FinMandateService finMandateService;
	private EventPropertiesService eventPropertiesService;

	public PresentmentEngine() {
		super();
	}

	/**
	 * This method will prepare the presentment data for the provided parameters. This method will be called from EOD
	 * for auto extraction (only presentment) and from screen for manual extraction (both presentment and
	 * re-presentment.
	 * 
	 * @param header
	 * @return return the total number of records extracted.
	 */
	public int preparation(PresentmentHeader header) {
		logger.debug(Literal.ENTERING);

		Date appDate = header.getAppDate();
		String presentmentType = header.getPresentmentType();
		boolean autoExtract = header.isAutoExtract();
		String finType = header.getLoanType();
		String finBranch = header.getFinBranch();
		String entityCode = header.getEntityCode();
		String emandateSource = header.getEmandateSource();
		String instrumentType = StringUtils.trimToNull(header.getMandateType());
		Date fromDate = header.getFromDate();
		Date toDate = header.getToDate();
		Date dueDate = header.getDueDate();

		StringBuilder info = new StringBuilder();
		info.append("\nBatch-ID: ").append(presentmentType);
		info.append("\nPresentment Type: ").append(presentmentType);
		info.append("\nEMandate Source: ").append(emandateSource);
		info.append("\nInstrument Type: ").append(presentmentType);
		info.append("\nLoan Types: ").append(finType);
		info.append("\nLoan Branch: ").append(entityCode);
		info.append("\nEntity: ").append(finBranch);
		info.append("\nFrom Date: ").append(DateUtil.formatToShortDate(fromDate));
		info.append("\nTo Date: ").append(DateUtil.formatToShortDate(toDate));
		info.append("\nDue Date: ").append(DateUtil.formatToShortDate(dueDate));
		info.append("\nApp Date: ").append(DateUtil.formatToShortDate(appDate));
		info.append("\nAuto Extaction: ").append(autoExtract);

		logger.info(info.toString());

		int count = 0;

		if (autoExtract) {
			Map<String, Date> dueDates = dueExtractionConfigDAO.getDueDates(appDate);

			for (String code : dueDates.keySet()) {
				PresentmentHeader ph = new PresentmentHeader();
				ph.setBatchID(header.getBatchID());
				ph.setAutoExtract(true);

				ph.setPresentmentType(presentmentType);
				ph.setMandateType(code);
				ph.setAppDate(appDate);
				ph.setDueDate(dueDates.get(code));

				count = count + prepareDues(ph);
			}
		} else {
			if (instrumentType != null && !"#".equals(instrumentType)) {
				count = count + prepareDues(header);
			} else {
				for (ValueLabel code : MandateUtil.getInstrumentTypesForBE()) {
					PresentmentHeader ph = new PresentmentHeader();
					ph.setMandateType(header.getMandateType());
					ph.setEmandateSource(header.getEmandateSource());
					ph.setLoanType(header.getLoanType());
					ph.setEntityCode(header.getEntityCode());
					ph.setFinBranch(header.getFinBranch());
					ph.setFromDate(header.getFromDate());
					ph.setToDate(header.getToDate());
					ph.setDueDate(header.getDueDate());
					ph.setBpiPaidOnInstDate(header.isBpiPaidOnInstDate());
					ph.setGroupByBank(header.isGroupByPartnerBank());
					ph.setGroupByPartnerBank(header.isGroupByPartnerBank());

					ph.setBatchID(header.getBatchID());
					ph.setAutoExtract(true);

					ph.setPresentmentType(presentmentType);
					ph.setMandateType(code.getValue());
					ph.setAppDate(appDate);

					count = count + prepareDues(ph);
				}
			}
		}

		if (count == 0) {
			return 0;
		}

		long batchID = header.getBatchID();
		count = count - presentmentDAO.clearSecurityCheque(batchID);

		presentmentDAO.updateToSecurityMandate(batchID);

		presentmentDAO.updatePartnerBankID(batchID);

		logger.debug(Literal.LEAVING);

		return count;
	}

	private int prepareDues(PresentmentHeader ph) {
		logger.debug(Literal.ENTERING);

		long batchID = ph.getBatchID();
		String instrumentType = StringUtils.trimToNull(ph.getMandateType());
		Date fromDate = ph.getFromDate();
		Date toDate = ph.getToDate();
		Date dueDate = ph.getDueDate();
		String presentmentType = ph.getPresentmentType();

		int count = 0;

		if (fromDate != null && toDate != null && "#".equals(instrumentType)) {
			/* From Screen when fromDate, toDate selected */
			count = presentmentDAO.extarct(batchID, fromDate, toDate);
		} else if (fromDate != null && toDate != null && instrumentType != null && !"#".equals(instrumentType)) {
			/* From Screen when fromDate, toDate, instrumentType is selected */
			count = presentmentDAO.extarct(batchID, instrumentType, fromDate, toDate);
		} else if (dueDate != null) {
			/* From EOD for Auto Extraction */
			count = presentmentDAO.extarct(batchID, instrumentType, dueDate, dueDate);
			logger.info("\nInstrument Type {}\nDue Date {}", instrumentType, DateUtil.formatToShortDate(dueDate));
		}

		if (fromDate != null && toDate != null) {
			logger.info("\nInstrument Type {}\nFrom Date {}\nTo Date {}", instrumentType,
					DateUtil.formatToShortDate(fromDate), DateUtil.formatToShortDate(toDate));
		}

		if (count == 0) {
			logger.debug(Literal.LEAVING);
			return 0;
		}

		count = count - presentmentDAO.clearByNoDues(batchID);

		if (count == 0) {
			logger.debug(Literal.LEAVING);
			return 0;
		}

		if (InstrumentType.isIPDC(instrumentType)) {
			presentmentDAO.updateIPDC(batchID);
		}

		/* The below code will be executed when manual extraction only */
		count = count - exludeByParameter(ph);

		if (PennantConstants.PROCESS_PRESENTMENT.equalsIgnoreCase(presentmentType)) {
			count = count - presentmentDAO.clearByExistingRecord(batchID);
		} else {
			count = count - presentmentDAO.clearByRepresentment(batchID);
		}

		logger.debug(Literal.LEAVING);

		return count;
	}

	private int exludeByParameter(PresentmentHeader ph) {
		int count = 0;

		long batchID = ph.getBatchID();
		String finType = ph.getLoanType();
		String finBranch = ph.getFinBranch();
		String entityCode = ph.getEntityCode();
		String emandateSource = ph.getEmandateSource();
		String instrumentType = StringUtils.trimToNull(ph.getMandateType());

		if ((instrumentType != null && !"#".equals(instrumentType)) && !ph.isAutoExtract()) {
			count = count + presentmentDAO.clearByInstrumentType(batchID, instrumentType);
		}

		if (InstrumentType.isEMandate(instrumentType) && StringUtils.isNotEmpty(emandateSource)
				&& !ph.isAutoExtract()) {
			count = count + presentmentDAO.clearByInstrumentType(batchID, instrumentType, emandateSource);
		}

		if (StringUtils.trimToNull(finType) != null) {
			count = count + presentmentDAO.clearByLoanType(batchID, finType);
		}

		if (StringUtils.trimToNull(finBranch) != null) {
			count = count + presentmentDAO.clearByLoanBranch(batchID, finBranch);
		}

		if (StringUtils.trimToNull(entityCode) != null) {
			count = count + presentmentDAO.clearByEntityCode(batchID, entityCode);
		}

		return count;
	}

	public void grouping(ResultSet rs, PresentmentHeader ph) throws SQLException {
		logger.debug(Literal.ENTERING);
		Map<String, Integer> batchMap = presentmentDAO.batchSizeByInstrumentType();

		Map<String, PresentmentHeader> headerMap = new HashMap<>();
		Map<String, Integer> instrumentBatch = new HashMap<>();

		List<PresentmentDetail> list = new ArrayList<>();

		Date appDate = ph.getAppDate();

		Map<String, Date> dueDates = null;

		if (PennantConstants.PROCESS_PRESENTMENT.equals(ph.getPresentmentType())) {
			dueDates = dueExtractionConfigDAO.getDueDates(appDate);
		}

		do {
			PresentmentDetail pd = new PresentmentDetail();

			pd.setId(rs.getLong("ID"));
			pd.setDefSchdDate(JdbcUtil.getDate(rs.getDate("DefSchdDate")));
			pd.setEntityCode(rs.getString("EntityCode"));
			pd.setBankCode(rs.getString("BankCode"));
			pd.setPartnerBankId(JdbcUtil.getLong(rs.getObject("PartnerBankId")));
			pd.setInstrumentType(rs.getString("InstrumentType"));

			String groupKey = grouping(ph, pd);

			PresentmentHeader header = headerMap.get(groupKey);
			if (header == null) {
				header = saveHeader(ph, dueDates, pd);
				headerMap.put(groupKey, header);
			}

			pd.setHeaderId(header.getId());
			pd.setDueDate(header.getDueDate());

			list.add(pd);

			String mandateType = header.getMandateType();

			Integer batchSize = batchMap.get(mandateType);

			if (batchSize != null && batchSize > 0) {
				int headerBatchSize = header.getBatchSize();
				headerBatchSize = headerBatchSize + 1;
				header.setBatchSize(headerBatchSize);

				instrumentBatch.put(mandateType, headerBatchSize);

				if (headerBatchSize == batchSize) {
					instrumentBatch.remove(mandateType);
					headerMap.remove(groupKey);
				}
			}

			if (list.size() == PennantConstants.CHUNK_SIZE) {
				presentmentDAO.updateHeader(list);
				list.clear();
			}
		} while (rs.next());

		if (!list.isEmpty()) {
			presentmentDAO.updateHeader(list);
		}

		logger.debug(Literal.ENTERING);
	}

	public void groupByInclude(long batchID) throws SQLException {
		logger.debug(Literal.ENTERING);

		Map<String, Integer> batchMap = presentmentDAO.batchSizeByInstrumentType();

		List<String> instrumentTypes = presentmentDAO.getInstrumentTypes(batchID);

		List<PresentmentDetail> list = new ArrayList<>();

		for (String instrumentType : instrumentTypes) {
			Map<Long, Integer> headerMap = new LinkedHashMap<>();
			Integer batchSize = batchMap.get(instrumentType);

			presentmentDAO.groupByInclude(batchID, instrumentType, this, headerMap, batchSize, list);

			if (!list.isEmpty()) {
				presentmentDAO.updateHeaderByInclude(list);
				list.clear();
			}
		}

		logger.debug(Literal.ENTERING);
	}

	public void groupByInclude(PresentmentDetail pd, Map<Long, Integer> headerMap, Integer batchSize,
			List<PresentmentDetail> list) {

		if (batchSize == null || batchSize <= 0) {
			return;
		}

		Long headerID = null;

		if (headerMap.size() > 0) {
			headerID = new ArrayList<>(headerMap.keySet()).get(headerMap.size() - 1);
		}

		if (headerID == null) {
			headerID = pd.getHeaderId();
		}

		Integer size = headerMap.get(headerID);

		if (size == null) {
			size = 0;
		}

		if (size < batchSize) {
			size++;
			pd.setHeaderId(headerID);

			headerMap.put(headerID, size);
			list.add(pd);
		}

		if (size == batchSize) {
			headerMap.remove(headerID);
		}

		if (list.size() == PennantConstants.CHUNK_SIZE) {
			presentmentDAO.updateHeaderByInclude(list);
			list.clear();
		}
	}

	private PresentmentHeader saveHeader(PresentmentHeader ph, Map<String, Date> dueDates, PresentmentDetail pd) {
		long batchID = ph.getBatchID();

		String instrumentType = pd.getInstrumentType();
		String presentmentType = ph.getPresentmentType();

		Date fromDate = ph.getFromDate();
		Date toDate = ph.getToDate();
		Date dueDate = null;

		if (fromDate != null && toDate != null) {
			dueDate = toDate;
		} else if (dueDates != null) {
			dueDate = dueDates.get(instrumentType);
		} else {
			dueDate = pd.getDefSchdDate();
		}

		if (fromDate == null) {
			fromDate = dueDate;
		}

		if (toDate == null) {
			toDate = dueDate;
		}

		long headerId = presentmentDAO.getSeqNumber("SeqPresentmentHeader");

		String reference = StringUtils.leftPad(String.valueOf(headerId), 15, "0");

		String ref = instrumentType.concat(reference);

		if (PennantConstants.PROCESS_REPRESENTMENT.equals(presentmentType)) {
			ref = "RE" + ref;
		}

		Long partnerBankId = pd.getPartnerBankId();

		if (partnerBankId == null || partnerBankId <= 0) {
			Presentment pb = presentmentDAO.getPartnerBankId(ph.getLoanType(), ph.getMandateType());

			if (pb == null) {
				pb = new Presentment();
				pb.setPartnerBankId(621L);
			}

			partnerBankId = pb.getPartnerBankId();
			ph.setPartnerBankId(pb.getPartnerBankId());
		}

		PresentmentHeader header = new PresentmentHeader();
		header.setStatus(RepayConstants.PEXC_EXTRACT);
		header.setPresentmentDate(DateUtil.getSysDate());
		header.setId(headerId);
		header.setBatchID(batchID);
		header.setFromDate(fromDate);
		header.setToDate(toDate);
		header.setMandateType(instrumentType);
		header.setPresentmentType(presentmentType);
		header.setSchdate(pd.getDefSchdDate());
		header.setEntityCode(pd.getEntityCode());
		header.setBankCode(pd.getBankCode());
		header.setPartnerBankId(partnerBankId);
		header.setDueDate(dueDate);
		header.setReference(ref);
		header.setdBStatusId(0);
		header.setImportStatusId(0);
		header.setTotalRecords(0);
		header.setProcessedRecords(0);
		header.setSuccessRecords(0);
		header.setFailedRecords(0);

		presentmentDAO.savePresentmentHeader(header);
		return header;
	}

	private String grouping(PresentmentHeader ph, PresentmentDetail pd) {
		StringBuilder group = new StringBuilder();

		if (ph.isGroupByBank() && ph.isGroupByPartnerBank()) {
			group.append(pd.getDefSchdDate());
			group.append(pd.getBankCode());
			group.append(pd.getEntityCode());
			group.append(pd.getPartnerBankId());
			group.append(pd.getInstrumentType());
		} else if (ph.isGroupByBank()) {
			group.append(pd.getDefSchdDate());
			group.append(pd.getBankCode());
			group.append(pd.getEntityCode());
			group.append(pd.getInstrumentType());
		} else if (ph.isGroupByPartnerBank()) {
			group.append(pd.getDefSchdDate());
			group.append(pd.getEntityCode());
			group.append(pd.getPartnerBankId());
			group.append(pd.getInstrumentType());
		} else {
			group.append(pd.getDefSchdDate());
			group.append(pd.getEntityCode());
			group.append(pd.getInstrumentType());
		}

		return group.toString();
	}

	public void grouping(PresentmentHeader ph) {
		logger.debug(Literal.ENTERING);

		long batchID = ph.getBatchID();
		List<PresentmentDetail> list = null;

		if (ph.isGroupByBank() && ph.isGroupByPartnerBank()) {
			list = presentmentDAO.getGroupByPartnerBankAndBank(batchID);
			setHeader(ph, list);
			presentmentDAO.updateHeaderIdByPartnerBank(batchID, list);
		} else if (ph.isGroupByBank()) {
			list = presentmentDAO.getGroupByBank(batchID);
			setHeader(ph, list);
			presentmentDAO.updateHeaderIdByBank(batchID, list);
		} else if (ph.isGroupByPartnerBank()) {
			list = presentmentDAO.getGroupByPartnerBank(batchID);
			setHeader(ph, list);
			presentmentDAO.updateHeaderIdByPartnerBank(batchID, list);
		} else {
			list = presentmentDAO.getGroupByDefault(batchID);
			setHeader(ph, list);
			presentmentDAO.updateHeaderIdByDefault(batchID, list);
		}

		logger.debug(Literal.LEAVING);
	}

	private void setHeader(PresentmentHeader ph, List<PresentmentDetail> list) {
		logger.debug(Literal.ENTERING);

		Date appDate = ph.getAppDate();
		long batchID = ph.getBatchID();

		Map<String, Date> dueDates = null;

		if (PennantConstants.PROCESS_PRESENTMENT.equals(ph.getPresentmentType())) {
			dueDates = dueExtractionConfigDAO.getDueDates(appDate);
		}

		for (PresentmentDetail pd : list) {

			String instrumentType = pd.getInstrumentType();
			String presentmentType = ph.getPresentmentType();

			Date fromDate = ph.getFromDate();
			Date toDate = ph.getToDate();
			Date dueDate = null;

			if (fromDate != null && toDate != null) {
				dueDate = toDate;
			} else if (dueDates != null) {
				dueDate = dueDates.get(instrumentType);
			} else {
				dueDate = pd.getDefSchdDate();
			}

			if (fromDate == null) {
				fromDate = dueDate;
			}

			if (toDate == null) {
				toDate = dueDate;
			}

			PresentmentHeader header = new PresentmentHeader();
			header.setBatchID(batchID);
			header.setFromDate(fromDate);
			header.setToDate(toDate);
			header.setMandateType(instrumentType);
			header.setPresentmentType(presentmentType);

			long headerId = saveHeader(header, pd);

			pd.setDueDate(dueDate);
			pd.setHeaderId(headerId);
		}

		logger.debug(Literal.LEAVING);
	}

	private long saveHeader(PresentmentHeader ph, PresentmentDetail pd) {
		logger.debug(Literal.ENTERING);

		long headerId = presentmentDAO.getSeqNumber("SeqPresentmentHeader");

		String reference = StringUtils.leftPad(String.valueOf(headerId), 15, "0");

		ph.setStatus(RepayConstants.PEXC_EXTRACT);
		ph.setPresentmentDate(DateUtil.getSysDate());
		ph.setId(headerId);
		ph.setSchdate(pd.getDefSchdDate());
		ph.setEntityCode(pd.getEntityCode());
		ph.setBankCode(pd.getBankCode());
		ph.setPartnerBankId(pd.getPartnerBankId());

		if (ph.getPartnerBankId() == null || ph.getPartnerBankId() <= 0) {
			Presentment pb = presentmentDAO.getPartnerBankId(ph.getLoanType(), ph.getMandateType());

			if (pb == null) {
				pb = new Presentment();
				pb.setPartnerBankId(621L);
			}

			ph.setPartnerBankId(pb.getPartnerBankId());
		}

		String ref = ph.getMandateType().concat(reference);

		if (PennantConstants.PROCESS_REPRESENTMENT.equals(ph.getPresentmentType())) {
			ref = "RE" + ref;
		}

		ph.setReference(ref);
		ph.setdBStatusId(0);
		ph.setImportStatusId(0);
		ph.setTotalRecords(0);
		ph.setProcessedRecords(0);
		ph.setSuccessRecords(0);
		ph.setFailedRecords(0);

		presentmentDAO.savePresentmentHeader(ph);

		logger.debug(Literal.LEAVING);
		return headerId;
	}

	public PresentmentDetail extract(long extractionID, PresentmentHeader ph) {
		logger.debug(Literal.ENTERING);

		PresentmentDetail pd = presentmentDAO.getPresentmentDetail(extractionID);

		if (PennantConstants.PROCESS_REPRESENTMENT.equals(ph.getPresentmentType())) {
			Long mandateId = presentmentDAO.getPreviousMandateID(pd.getFinID(), pd.getSchDate());

			if (mandateId != null) {
				pd.setMandateId(mandateId);
			}
		}

		BigDecimal schAmtDue = BigDecimal.ZERO;

		BigDecimal schPriDue = BigDecimal.ZERO;
		schPriDue = schPriDue.add(pd.getPrincipalSchd());
		schPriDue = schPriDue.subtract(pd.getSchdPriPaid());

		BigDecimal schPftDue = BigDecimal.ZERO;
		schPftDue = schPftDue.add(pd.getProfitSchd());
		schPftDue = schPftDue.subtract(pd.getSchdPftPaid());

		BigDecimal schFeeDue = BigDecimal.ZERO;
		schFeeDue = schFeeDue.add(pd.getFeeSchd());
		schFeeDue = schFeeDue.subtract(pd.getSchdFeePaid());

		BigDecimal tDSAmount = BigDecimal.ZERO;
		tDSAmount = tDSAmount.add(pd.gettDSAmount());
		tDSAmount = tDSAmount.subtract(pd.getTdsPaid());

		schAmtDue = schAmtDue.add(schPriDue);
		schAmtDue = schAmtDue.add(schPftDue);
		schAmtDue = schAmtDue.add(schFeeDue);
		schAmtDue = schAmtDue.subtract(tDSAmount);

		String productCategory = pd.getProductCategory();
		if (BigDecimal.ZERO.compareTo(schAmtDue) >= 0) {
			boolean dueExists = true;
			if (ProductUtil.isOverDraft(productCategory) && ph.getAppDate().compareTo(pd.getSchDate()) < 0) {
				dueExists = false;
			}

			if (!dueExists) {
				return pd;
			}
		}

		pd.setStatus(RepayConstants.PEXC_SEND_TO_PRESENTMENT);

		pd.setExcludeReason(RepayConstants.PEXC_EMIINCLUDE);

		pd.setPresentmentAmt(BigDecimal.ZERO);
		pd.setSchAmtDue(schAmtDue);
		pd.setSchPriDue(schPriDue);
		pd.setSchPftDue(schPftDue);
		pd.setSchFeeDue(schFeeDue);
		pd.settDSAmount(tDSAmount);

		pd.setSchInsDue(BigDecimal.ZERO);
		pd.setSchPenaltyDue(BigDecimal.ZERO);
		pd.setAdvanceAmt(BigDecimal.ZERO);
		pd.setAdviseAmt(BigDecimal.ZERO);
		pd.setExcessID(0);
		pd.setReceiptID(0);

		pd.setVersion(0);
		pd.setLastMntBy(ph.getLastMntBy());
		pd.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		pd.setWorkflowId(0);

		if (FinanceConstants.FLAG_HOLDEMI.equals(pd.getBpiOrHoliday())) {
			pd.setOriginalSchDate(pd.getSchDate());
			pd.setSchDate(pd.getDefSchdDate());
		}

		if (pd.getSchAmtDue().compareTo(BigDecimal.ZERO) > 0) {
			pd.setAppDate(ph.getAppDate());
			doCalculations(ph, pd);
		}

		logger.debug(Literal.LEAVING);
		return pd;
	}

	private void doCalculations(PresentmentHeader ph, PresentmentDetail pd) {
		logger.debug(Literal.ENTERING);

		String mandateStatus = pd.getMandateStatus();

		if (InstrumentType.isPDC(pd.getInstrumentType()) || InstrumentType.isIPDC(pd.getInstrumentType())) {
			pd.setMandateId(pd.getChequeId());

			String chequeStatus = pd.getChequeStatus();

			if (!ChequeSatus.NEW.equals(chequeStatus)) {
				int excludeReason = 0;
				switch (chequeStatus) {
				case ChequeSatus.PRESENT:
					excludeReason = RepayConstants.CHEQUESTATUS_PRESENT;
					break;
				case ChequeSatus.REALISE:
					excludeReason = RepayConstants.CHEQUESTATUS_REALISE;
					break;
				case ChequeSatus.REALISED:
					excludeReason = RepayConstants.CHEQUESTATUS_REALISED;
					break;
				default:
					break;
				}
				pd.setExcludeReason(excludeReason);
			}
		} else {
			if (InstrumentType.isECS(pd.getMandateType())) {
				if (MandateStatus.isRejected(mandateStatus)) {
					pd.setExcludeReason(RepayConstants.PEXC_MANDATE_REJECTED);
				} else if (MandateStatus.isHold(mandateStatus)) {
					pd.setExcludeReason(RepayConstants.PEXC_MANDATE_HOLD);
				}
			} else {
				if (MandateStatus.isRejected(mandateStatus)) {
					pd.setExcludeReason(RepayConstants.PEXC_MANDATE_REJECTED);
				} else if (MandateStatus.isHold(mandateStatus)) {
					pd.setExcludeReason(RepayConstants.PEXC_MANDATE_HOLD);
				} else if (!MandateStatus.isApproved(mandateStatus)) {
					pd.setExcludeReason(RepayConstants.PEXC_MANDATE_NOTAPPROV);
				}

				if (pd.getMandateExpiryDate() != null
						&& DateUtil.compare(pd.getDefSchdDate(), pd.getMandateExpiryDate()) > 0) {
					pd.setExcludeReason(RepayConstants.PEXC_MANDATE_EXPIRY);
				}
			}
		}

		if (pd.getDefSchdDate().compareTo(pd.getDueDate()) > 0) {
			pd.setExcludeReason(RepayConstants.PEXC_EMIHOLD);
			return;
		}

		if (PennantConstants.PROCESS_PRESENTMENT.equalsIgnoreCase(ph.getPresentmentType())) {
			processAdvAmounts(ph, pd);
		}

		processEMIInAdvance(pd);

		BigDecimal advAmount = pd.getAdvAdjusted();
		pd.setPresentmentAmt(pd.getPresentmentAmt().subtract(advAmount));

		logger.debug(Literal.LEAVING);
	}

	private void processAdvAmounts(PresentmentHeader ph, PresentmentDetail pd) {
		logger.debug(Literal.ENTERING);

		AdvanceType advanceType = null;
		String amountType = "";

		if (pd.getGrcPeriodEndDate() != null && pd.getSchDate().compareTo(pd.getGrcPeriodEndDate()) <= 0) {
			advanceType = AdvanceType.getType(pd.getGrcAdvType());
		} else {
			advanceType = AdvanceType.getType(pd.getAdvType());
		}

		if (FinanceConstants.FLAG_BPI.equals(pd.getBpiOrHoliday())) {
			if (FinanceConstants.BPI_DISBURSMENT.equals(pd.getBpiTreatment()) && ph.isBpiPaidOnInstDate()) {
				advanceType = AdvanceType.AF;
			}
		}

		if (advanceType == null) {
			logger.debug(Literal.LEAVING);
			return;
		}

		int exculdeReason = 0;
		BigDecimal dueAmt = BigDecimal.ZERO;

		if (advanceType == AdvanceType.AE) {
			if (AdvanceStage.getStage(pd.getAdvStage()) == AdvanceStage.FE) {
				logger.debug(Literal.LEAVING);
				return;
			}
			amountType = RepayConstants.EXAMOUNTTYPE_ADVEMI;
			dueAmt = pd.getSchAmtDue();
			exculdeReason = RepayConstants.PEXC_ADVEMI;
		} else {
			amountType = RepayConstants.EXAMOUNTTYPE_ADVINT;
			dueAmt = pd.getSchPftDue();
			if (pd.getSchPriDue().compareTo(BigDecimal.ZERO) == 0) {
				exculdeReason = RepayConstants.PEXC_ADVINT;
			}
		}

		if (FinanceConstants.FLAG_BPI.equals(pd.getBpiOrHoliday())) {
			if (FinanceConstants.BPI_DISBURSMENT.equals(pd.getBpiTreatment()) && ph.isBpiPaidOnInstDate()) {
				logger.debug(Literal.LEAVING);
				return;
			}
		}

		List<FinExcessAmount> list = finExcessAmountDAO.getExcessAmountsByRefAndType(pd.getFinID(), amountType);

		for (FinExcessAmount finExAmt : list) {
			BigDecimal excessBal = finExAmt.getBalanceAmt();
			BigDecimal adjAmount = BigDecimal.ZERO;

			if (dueAmt.compareTo(BigDecimal.ZERO) > 0) {
				if (excessBal != null && excessBal.compareTo(BigDecimal.ZERO) > 0) {
					if (dueAmt.compareTo(excessBal) >= 0) {
						adjAmount = excessBal;
					} else {
						adjAmount = dueAmt;
					}
				}
			}

			pd.setAdvAdjusted(adjAmount);
			if (adjAmount.compareTo(dueAmt) == 0) {
				pd.setExcludeReason(exculdeReason);
			}

			finExAmt.setReservedAmt(adjAmount);
			BigDecimal amount = finExAmt.getAmount();
			BigDecimal reservedAmt = finExAmt.getReservedAmt();
			BigDecimal utilisedAmt = finExAmt.getUtilisedAmt();
			finExAmt.setBalanceAmt(amount.subtract(reservedAmt).subtract(utilisedAmt));
			pd.getExcessAmountReversal().add(finExAmt);

			FinExcessMovement exMovement = new FinExcessMovement();
			exMovement.setExcessID(finExAmt.getExcessID());
			exMovement.setReceiptID(Long.MIN_VALUE);
			exMovement.setMovementFrom(RepayConstants.PAYTYPE_PRESENTMENT);
			exMovement.setAmount(adjAmount);
			exMovement.setSchDate(pd.getSchDate());
			exMovement.setMovementType("I");
			exMovement.setTranType("I");

			pd.getExcessMovements().add(exMovement);
		}
	}

	private void processEMIInAdvance(PresentmentDetail pd) {
		logger.debug(Literal.ENTERING);

		long finID = pd.getFinID();

		List<FinExcessAmount> list = finExcessAmountDAO.getExcessAmountsByRefAndType(finID, pd.getAppDate(),
				RepayConstants.EXAMOUNTTYPE_EMIINADV);

		BigDecimal dueAmount = pd.getSchAmtDue();

		BigDecimal emiInAdvanceAmount = BigDecimal.ZERO;
		for (FinExcessAmount excess : list) {
			if (dueAmount.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
			BigDecimal excessBal = excess.getBalanceAmt();
			BigDecimal adjAmount = BigDecimal.ZERO;

			if (dueAmount.compareTo(excessBal) > 0) {
				adjAmount = excessBal;
			} else {
				adjAmount = dueAmount;
			}

			dueAmount = dueAmount.subtract(adjAmount);
			emiInAdvanceAmount = emiInAdvanceAmount.add(adjAmount);
			FinExcessMovement exMovement = new FinExcessMovement();
			exMovement.setExcessID(excess.getExcessID());
			exMovement.setReceiptID(pd.getId());
			exMovement.setAmount(adjAmount);
			exMovement.setMovementType("I");
			exMovement.setTranType("R");
			pd.getExcessMovements().add(exMovement);
		}

		BigDecimal advanceAmt = BigDecimal.ZERO;
		if (emiInAdvanceAmount.compareTo(pd.getSchAmtDue()) >= 0) {
			pd.setExcludeReason(RepayConstants.PEXC_EMIINADVANCE);
			pd.setPresentmentAmt(BigDecimal.ZERO);
			pd.setAdvanceAmt(pd.getSchAmtDue());
			pd.setStatus(RepayConstants.PEXC_APPROV);
			advanceAmt = pd.getAdvanceAmt();
		} else {
			advanceAmt = emiInAdvanceAmount;
			pd.setPresentmentAmt(pd.getSchAmtDue().subtract(advanceAmt));
		}

		BigDecimal advAmount = pd.getAdvAdjusted();
		pd.setAdvanceAmt(advanceAmt.add(advAmount));

		logger.debug(Literal.LEAVING);
	}

	public void save(PresentmentDetail pd) {
		logger.debug(Literal.ENTERING);

		List<FinExcessAmount> excess = new ArrayList<>();
		List<FinExcessAmount> excessRevarsal = new ArrayList<>();
		List<FinExcessMovement> excessMovement = new ArrayList<>();
		List<PresentmentDetail> includeList = new ArrayList<>();
		List<FinExcessAmount> emiInAdvance = new ArrayList<>();

		Map<String, PresentmentDetail> odPresentments = new HashMap<>();

		setPresentmentRef(pd);

		if (pd.getExcessAmountReversal() != null) {
			excessRevarsal.addAll(pd.getExcessAmountReversal());
			excessMovement.addAll(pd.getExcessMovements());
		}

		if (pd.getExcessAmount() != null) {
			excess.add(pd.getExcessAmount());
		}
		if (pd.getEmiInAdvance() != null) {
			emiInAdvance.add(pd.getEmiInAdvance());
		}

		if (FinanceConstants.PRODUCT_ODFACILITY.equals(pd.getProductCategory())) {
			odPresentments.put(pd.getFinReference(), pd);
		}

		presentmentDAO.save(pd);

		if (RepayConstants.PEXC_EMIINCLUDE == pd.getExcludeReason()) {
			includeList.add(pd);
		}

		if (ImplementationConstants.OVERDRAFT_REPRESENTMENT_CHARGES_INCLUDE && !odPresentments.isEmpty()) {
			overdrafLoanService.createCharges(odPresentments.values().stream().collect(Collectors.toList()));
		}

		if (FinanceConstants.FLAG_HOLDEMI.equals(pd.getBpiOrHoliday())) {
			pd.setSchDate(pd.getOriginalSchDate());
		}

		try {
			FinanceMain fm = new FinanceMain();
			fm.setFinID(pd.getFinID());
			fm.setSchdVersion(pd.getSchdVersion());

			financeMainDAO.updateSchdVersion(fm, false);

			if (!excess.isEmpty()) {
				finExcessAmountDAO.updateExcessAmtList(excess);
			}

			if (!excessRevarsal.isEmpty()) {
				finExcessAmountDAO.updateExcessReserveList(excessRevarsal);
			}

			if (!excessMovement.isEmpty()) {
				finExcessAmountDAO.saveExcessMovementList(excessMovement);
			}

			if (!emiInAdvance.isEmpty()) {
				finExcessAmountDAO.updateExcessEMIAmount(emiInAdvance, "R");
			}

			if (includeList.isEmpty()) {
				return;
			}

			presentmentDAO.updateSchdWithPresentmentId(includeList);

			if (pd.getRePresentUploadID() != null) {
				presentmentDAO.updateRepresentWithPresentmentId(includeList);
			}

			List<PresentmentDetail> cheques = new ArrayList<>();
			if (InstrumentType.isPDC(pd.getInstrumentType()) || InstrumentType.isIPDC(pd.getInstrumentType())) {
				cheques.add(pd);
			}

			if (!cheques.isEmpty()) {
				chequeDetailDAO.updateChequeStatus(cheques);
			}

		} catch (ConcurrencyException e) {
			pd.setExcludeReason(RepayConstants.PEXC_SCHDVERSION);
			presentmentDAO.updateExcludeReason(pd.getId(), RepayConstants.PEXC_SCHDVERSION);
		}

		logger.debug(Literal.LEAVING);
	}

	public void clearQueue(long batchId) {
		this.presentmentDAO.clearQueue(batchId);
	}

	private int getPresentmentIndex(Date businessDate, List<FinanceScheduleDetail> schedules) {
		for (int i = 0; i < schedules.size(); i++) {
			FinanceScheduleDetail schd = schedules.get(i);

			if (schd.getDefSchdDate().compareTo(businessDate) == 0) {
				return i;
			}
		}

		return 0;
	}

	private void createReceipt(ReceiptDTO receiptDTO, RequestSource requestSource, boolean dueDateCreation) {
		logger.debug(Literal.ENTERING);

		PresentmentDetail pd = receiptDTO.getPresentmentDetail();

		Date businessDate = pd.getAppDate();

		receiptDTO.setRequestSource(requestSource);
		receiptDTO.setCreatePrmntReceipt(dueDateCreation);

		int presentmentIndex = getPresentmentIndex(businessDate, receiptDTO.getSchedules());

		receiptPaymentService.processReceipts(receiptDTO, presentmentIndex);

		logger.debug(Literal.LEAVING);
	}

	public void approve(ReceiptDTO receiptDTO, Map<String, String> bounceForPD) {
		logger.debug(Literal.ENTERING);

		boolean upfronBounceRequired = MapUtils.isNotEmpty(bounceForPD);
		PresentmentDetail pd = receiptDTO.getPresentmentDetail();

		int excludeReason = pd.getExcludeReason();

		if (upfronBounceRequired && (excludeReason != RepayConstants.PEXC_EMIINCLUDE
				|| excludeReason != RepayConstants.PEXC_EMIINADVANCE)) {
			presentmentDAO.approveExludes(pd.getId());
		} else if (excludeReason != RepayConstants.PEXC_EMIINCLUDE
				|| excludeReason != RepayConstants.PEXC_EMIINADVANCE) {
			presentmentDAO.updatePresentmentIdAsZero(pd.getId());
			return;
		}

		if (DateUtil.compare(pd.getAppDate(), pd.getSchDate()) >= 0) {
			createReceipt(receiptDTO, RequestSource.PRMNT_EXT,
					PennantConstants.PROCESS_REPRESENTMENT.equals(pd.getPresentmentType()));
		}

		logger.debug(Literal.LEAVING);
	}

	public void sendToPresentment(PresentmentHeader ph) {
		logger.debug(Literal.ENTERING);

		long headerId = ph.getId();

		List<PresentmentDetail> presements = presentmentDAO.getSendToPresentmentDetails(headerId);

		List<Long> idList = new ArrayList<>();

		for (PresentmentDetail pd : presements) {
			long detailID = pd.getId();
			idList.add(detailID);
		}

		try {
			String presentmentRef = ph.getReference();
			String bankAccNo = ph.getPartnerAcctNumber();

			if (externalPresentmentHook != null && !InstrumentType.isDAS(ph.getMandateType())) {
				externalPresentmentHook.processPresentmentRequest(ph);
			} else {
				getPresentmentRequest().sendReqest(idList, ph.getId(), false, ph.getMandateType(), presentmentRef,
						bankAccNo, null);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		presentmentDAO.updateHeader(headerId);

		logger.debug(Literal.LEAVING);
	}

	public int preparationForRepresentment(long batchID, List<Long> headerId) {
		logger.debug(Literal.ENTERING);

		int count = 0;

		List<PresentmentHeader> list = new ArrayList<>();

		list = presentmentDAO.getpresentmentHeaderList(headerId);

		for (PresentmentHeader ph : list) {
			count = count + presentmentDAO.extract(batchID, ph);

			if (InstrumentType.isIPDC(ph.getMandateType())) {
				presentmentDAO.updateIPDC(batchID);
			}
		}

		count = count - presentmentDAO.clearByNoDues(batchID);

		if (count == 0) {
			logger.debug(Literal.LEAVING);
			return 0;
		}

		count = count - presentmentDAO.clearSecurityCheque(batchID);

		presentmentDAO.updateToSecurityMandate(batchID);

		presentmentDAO.updatePartnerBankID(batchID);

		logger.debug(Literal.LEAVING);
		return count;
	}

	public PresentmentDetail getPresentmenToPost(Long presentmentID) {
		PresentmentDetail pd = presentmentDAO.getPresentmenToPost(presentmentID);
		String receiptType = RepayConstants.RECEIPTTYPE_PRESENTMENT;
		pd.setExcessMovements(finExcessAmountDAO.getExcessMovementList(pd.getId(), receiptType));

		return pd;
	}

	public List<PresentmentHeader> getPresentmenHeaders(Long batchId) {
		return presentmentDAO.getPresentmentHeaders(batchId);
	}

	private void setPresentmentRef(PresentmentDetail pd) {
		StringBuilder sb = new StringBuilder();
		sb.append(pd.getBranchCode());
		sb.append(pd.getFinType());
		sb.append(pd.getInstrumentType());

		long id = presentmentDAO.getNextValue();
		pd.setId(id);

		String reference = sb.toString();
		String presentmentRef = StringUtils.leftPad(String.valueOf(id), 29 - reference.length(), "0");
		pd.setPresentmentRef(reference.concat(presentmentRef));
	}

	// ----------------------------------------------------------------------------//
	// ----------------------------PRESENTMENT_RESPONSE----------------------------//
	// ----------------------------------------------------------------------------//

	public PresentmentDetail getPresentmenForResponse(Long responseID) {
		return presentmentDAO.getPresentmenForResponse(responseID);
	}

	public void processResponse(ReceiptDTO receiptDTO) {
		logger.info(Literal.ENTERING);

		PresentmentDetail pd = receiptDTO.getPresentmentDetail();

		String presentmentReference = pd.getPresentmentRef();

		long finID = pd.getFinID();
		String finReference = StringUtils.trimToEmpty(pd.getFinReference());
		String finType = pd.getFinType();
		String mandateType = pd.getMandateType();
		Long mandateID = pd.getMandateId();
		Long receiptID = pd.getReceiptID();
		Long bcReceiptID = pd.getReceiptID();

		String clearingStatus = pd.getClearingStatus();
		String bounceCode = StringUtils.trimToEmpty(pd.getBounceCode());
		String bounceRemarks = pd.getBounceRemarks();
		boolean finIsActive = pd.isFinisActive();
		String status = RepayConstants.PEXC_SUCCESS;

		String fateCorrection = pd.getFateCorrection();

		if (presentmentImportProcess != null) {
			presentmentReference = presentmentImportProcess.getPresentmentRef(presentmentReference);
			status = presentmentImportProcess.getStatus(status);
			bounceCode = presentmentImportProcess.getReasonCode(bounceCode);
			if ((status == null || status.equals("")) && (bounceCode != null && !bounceCode.equals(""))) {
				status = "F";
			} else if ((status == null || status.equals("")) && (bounceCode == null || bounceCode.equals(""))) {
				status = "S";
			}
		}

		StringBuilder info = new StringBuilder();
		info.append("\nPresement Reference: ").append(presentmentReference);
		info.append("\nFinReference: ").append(finReference);
		info.append("\nFinType: ").append(finType);
		info.append("\nFinIsActive: ").append(finIsActive);
		info.append("\nMandate-Type: ").append(mandateType);
		info.append("\nMandate-ID: ").append(mandateID);
		info.append("\nReceipt-ID: ").append(receiptID);
		info.append("\nPresement Response Status: ").append(clearingStatus);
		info.append("\nBounce Reason Code: ").append(bounceCode);
		info.append("\nBounceRemarks: ").append(bounceRemarks);
		info.append("\nFate Correction: ").append(fateCorrection);

		logger.info(info.toString());

		/* Validations */
		validateResponse(pd);

		if (RepayConstants.PEXC_PAID.equals(clearingStatus)) {
			pd.setStatus(RepayConstants.PEXC_SUCCESS);
		}

		status = pd.getStatus();

		boolean processReceipt = false;
		Long linkedTranId;

		if (PresentmentStatus.BOUNCE.equals(status) && PresentmentStatus.SUCCESS.equals(clearingStatus)
				&& "Y".equals(fateCorrection)) {
			receiptID = 0L;
		}

		if (receiptID == 0) {
			if (!PresentmentExtension.DUE_DATE_RECEIPT_CREATION) {
				if (pd.getPresentmentAmt().compareTo(BigDecimal.ZERO) > 0) {
					pd.setAdvanceAmt(BigDecimal.ZERO);
					createReceipt(receiptDTO, RequestSource.PRMNT_RESP, true);
				}
			} else if (!finIsActive) {
				processReceipt = processInactiveLoan(receiptDTO);
			}

			receiptDTO = prepareReceiptDTO(pd);

			logger.info("Re-loading finance data...");

			receiptID = pd.getReceiptID();
		}

		String checkStatus = null;
		FinReceiptHeader rh = null;

		pd.setBounceID(0);
		pd.setManualAdviseId(null);

		if (RepayConstants.PEXC_SUCCESS.equals(clearingStatus)) {
			if (ImplementationConstants.PRESENTMENT_STAGE_ACCOUNTING_REQ) {
				AEEvent event = doPresentmentStageAccounting(pd);
				linkedTranId = event.getLinkedTranId();
				pd.setLinkedTranId(linkedTranId);
			}

			if (!processReceipt) {
				rh = getFinReceiptHeader(receiptID);
			}

			if (InstrumentType.isPDC(mandateType)) {
				checkStatus = ChequeSatus.REALISED;
			}

			if (PresentmentStatus.BOUNCE.equals(status) && "Y".equals(fateCorrection)) {
				revertBounceCharges(bcReceiptID);
			}
		} else {
			if (InstrumentType.isPDC(mandateType) || InstrumentType.isIPDC(mandateType)) {
				if (StringUtils.trimToNull(pd.getErrorDesc()) == null) {
					checkStatus = ChequeSatus.BOUNCE;
				} else {
					checkStatus = ChequeSatus.FAILED;
				}
			}
		}

		if (RepayConstants.PEXC_SUCCESS.equals(clearingStatus)) {
			updateFinanceDetails(receiptDTO);
			if (rh != null) {
				updateFinReceiptHeader(rh, pd.getAppDate());
			}
			status = RepayConstants.PEXC_SUCCESS;
			pd.setStatus(status);

			if (!InstrumentType.isPDC(mandateType)) {
				unHoldMandate(pd);
			}
		} else {
			status = RepayConstants.PEXC_BOUNCE;
			pd.setStatus(status);

			CustEODEvent custEODEvent = new CustEODEvent();
			custEODEvent.setEodDate(pd.getAppDate());
			custEODEvent.setCustomer(receiptDTO.getCustomer());
			custEODEvent.setEventProperties(pd.getEventProperties());

			FinEODEvent finEODEvent = new FinEODEvent();
			finEODEvent.setFinType(receiptDTO.getFinType());
			finEODEvent.setFinanceMain(receiptDTO.getFinanceMain());
			finEODEvent.setFinProfitDetail(receiptDTO.getProfitDetail());
			finEODEvent.setFinanceScheduleDetails(receiptDTO.getSchedules());
			finEODEvent.setFinODDetails(receiptDTO.getOdDetails());

			custEODEvent.getFinEODEvents().add(finEODEvent);

			if ("N".equals(fateCorrection)) {
				pd = receiptCancellationService.presentmentCancellation(pd, custEODEvent);
			} else {
				if (finIsActive) {
					pd = receiptCancellationService.presentmentCancellation(pd, custEODEvent);
				} else {
					throw new PresentmentException(PresentmentError.PRMNT5011);
				}
			}

			if (StringUtils.trimToNull(pd.getErrorDesc()) != null) {
				pd.setSchdVersion(pd.getSchdVersion() + 1);
				updatePresentmentDetail(pd);
				throw new AppException(pd.getErrorDesc());
			}

			presentmentDetailDAO.updatePresentmentIdAsZero(pd.getId());

			if (!(InstrumentType.isPDC(mandateType) || InstrumentType.isIPDC(mandateType))) {
				holdMandate(pd);
			}
		}

		if ((InstrumentType.isPDC(mandateType) || InstrumentType.isIPDC(mandateType)) && checkStatus != null) {
			presentmentDetailDAO.updateChequeStatus(mandateID, checkStatus);
		}

		if (StringUtils.isNotEmpty(pd.getBounceCode())) {
			pd.setErrorCode(pd.getBounceCode());
			pd.setErrorDesc(pd.getBounceRemarks());
		} else {
			pd.setErrorCode(null);
			pd.setErrorDesc(null);
		}

		updatePresentmentDetail(pd);

		finMandateService.autoSwapingFromPDC(finID);

		logger.info(Literal.LEAVING);
	}

	private void revertBounceCharges(Long bcReceiptID) {
		ManualAdvise ma = manualAdviseDAO.getBounceChargesByReceiptID(bcReceiptID);

		if (ma == null) {
			logger.info("Bounce Charges are not found for the receipt ID {}", bcReceiptID);
			return;
		}

		BigDecimal amount = ma.getAdviseAmount().subtract((ma.getPaidAmount().add(ma.getWaivedAmount())));

		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			logger.info("Bounce Charges are already Paid for the receipt ID {}", bcReceiptID);
			return;
		}

		manualAdviseDAO.revertBounceCharges(ma.getAdviseID(), amount);
	}

	private void updatePresentmentDetail(PresentmentDetail pd) {
		String errorDesc = pd.getErrorDesc();
		if (StringUtils.trimToNull(errorDesc) != null) {
			errorDesc = (errorDesc.length() >= 1000) ? errorDesc.substring(0, 998) : errorDesc;
		}
		presentmentDetailDAO.updatePresentmentDetail(pd);
	}

	private void holdMandate(PresentmentDetail pd) {
		BounceReason bounceReason = BounceConfigCache.getCacheBounceReason(pd.getBounceCode());

		if (bounceReason == null) {
			return;
		}

		int consecutiveBounceCount = bounceReason.getHoldMarkBounceCount();

		Long mandateId = pd.getMandateId();
		Long bounceId = bounceReason.getBounceID();
		Date schdDate = pd.getSchDate();

		ConsecutiveBounce consecBounces = consecutiveBounceDAO.getBounces(mandateId);

		if (consecBounces == null) {
			consecutiveBounceDAO.create(mandateId, bounceId, schdDate);
		}

		consecBounces = consecutiveBounceDAO.getBounces(mandateId);
		long lastBounceId = consecBounces.getBounceID();
		int bounceCount = consecBounces.getBounceCount();
		Date lastBounceDate = consecBounces.getLastBounceDate();

		if (lastBounceId != bounceId) {
			consecutiveBounceDAO.resetConter(mandateId, bounceId, schdDate);
		}

		if (DateUtil.getMonthsBetween(lastBounceDate, schdDate) == 1) {
			bounceCount = bounceCount + 1;
			consecutiveBounceDAO.update(mandateId, schdDate, bounceCount);
		}

		if (bounceCount < consecutiveBounceCount) {
			return;
		}

		String reason = MandateExtension.CONSECUTIVE_HOLD_REASON;

		mandateDAO.holdMandate(mandateId, reason);

		com.pennant.backend.model.mandate.MandateStatus mandateStatus = new com.pennant.backend.model.mandate.MandateStatus();
		mandateStatus.setMandateID(mandateId);
		mandateStatus.setStatus(MandateStatus.HOLD);
		mandateStatus.setReason(reason);
		mandateStatus.setChangeDate(DateUtil.getSysDate());

		mandateStatusDAO.save(mandateStatus, "");
	}

	private void unHoldMandate(PresentmentDetail pd) {
		long mandateId = pd.getMandateId();
		ConsecutiveBounce consecBounces = consecutiveBounceDAO.getBounces(mandateId);

		if (consecBounces == null) {
			return;
		}

		consecutiveBounceDAO.delete(mandateId);

		mandateDAO.unHoldMandate(mandateId);

		com.pennant.backend.model.mandate.MandateStatus mandateStatus = new com.pennant.backend.model.mandate.MandateStatus();
		mandateStatus.setMandateID(mandateId);
		mandateStatus.setStatus(MandateStatus.APPROVED);
		mandateStatus.setReason("Un-Hold Mandate");
		mandateStatus.setChangeDate(DateUtil.getSysDate());

		mandateStatusDAO.save(mandateStatus, "");
	}

	private boolean processInactiveLoan(ReceiptDTO receiptDTO) {
		logger.info(Literal.ENTERING);

		receiptDTO.setNoReserve(false);
		receiptDTO.setPdDetailsExits(true);
		receiptDTO.setRequestSource(RequestSource.PRMNT_RESP);
		receiptDTO.setCreatePrmntReceipt(!PresentmentExtension.DUE_DATE_RECEIPT_CREATION);

		logger.info("Creating presentment receipt for inactive loan.");

		receiptPaymentService.createReceipt(receiptDTO);

		long receiptID = receiptDTO.getPresentmentDetail().getReceiptID();
		logger.info("Presentment receipt creation completed with the Receipt-ID: {}", receiptID);

		logger.info(Literal.LEAVING);
		return true;
	}

	public ReceiptDTO prepareReceiptDTO(PresentmentDetail pd) {
		logger.info("Loading finance data...");

		String receiptType = RepayConstants.RECEIPTTYPE_PRESENTMENT;
		pd.setExcessMovements(finExcessAmountDAO.getExcessMovementList(pd.getId(), receiptType));

		ReceiptDTO receiptDTO = new ReceiptDTO();

		long finID = pd.getFinID();

		FinanceMain fm = financeMainDAO.getFinMainsForEODByFinRef(finID, pd.isFinisActive());

		if (fm == null) {
			fm = financeMainDAO.getFinMainsForEODByFinRef(finID, false);
		}

		EventProperties eventProperties = pd.getEventProperties();
		if (eventProperties == null) {
			eventProperties = eventPropertiesService.getEventProperties(EventType.EOD);
		}

		fm.setEventProperties(eventProperties);
		pd.setEventProperties(eventProperties);

		FinanceType financeType = FinanceConfigCache.getCacheFinanceType(fm.getFinType());
		Customer customer = customerDAO.getCustomerEOD(fm.getCustID());
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);
		List<FinODDetails> finODDetails = finODDetailsDAO.getFinODBalByFinRef(finID);
		FinanceProfitDetail fpd = financeProfitDetailDAO.getFinProfitDetailsByFinRef(finID);

		receiptDTO.setFinType(financeType);
		receiptDTO.setCustomer(customer);
		receiptDTO.setFinanceMain(fm);
		receiptDTO.setProfitDetail(fpd);
		receiptDTO.setSchedules(schedules);
		receiptDTO.setPresentmentDetail(pd);
		receiptDTO.setOdDetails(finODDetails);

		receiptDTO.setValuedate(pd.getSchDate());
		receiptDTO.setPostDate(pd.getAppDate());

		return receiptDTO;
	}

	private void validateResponse(PresentmentDetail pd) {
		String status = pd.getClearingStatus();
		String bounceCode = StringUtils.trimToEmpty(pd.getBounceCode());
		String bounceRemarks = pd.getBounceRemarks();

		if (StringUtils.trimToNull(status) == null) {
			throw new PresentmentException(PresentmentError.PRMNT501);
		}

		if (PresentmentStatus.BOUNCE.equals(status) && StringUtils.isEmpty(bounceCode)) {
			throw new PresentmentException(PresentmentError.PRMNT502);
		}

		if (PresentmentStatus.BOUNCE.equals(status)) {
			BounceReason br = BounceConfigCache.getCacheBounceReason(bounceCode);
			if (br == null) {
				throw new PresentmentException(PresentmentError.PRMNT503);
			}
		}

		if (PresentmentStatus.BOUNCE.equals(status) && ImplementationConstants.PRESENT_RESP_BOUNCE_REMARKS_MAN) {
			if (StringUtils.isNotEmpty(bounceCode) && StringUtils.trimToNull(bounceRemarks) == null) {
				throw new PresentmentException(PresentmentError.PRMNT504);
			}
			if (bounceRemarks != null && bounceRemarks.length() > 100) {
				throw new PresentmentException(PresentmentError.PRMNT505);
			}
		}

		if (PresentmentStatus.SUCCESS.equals(pd.getStatus()) && PresentmentStatus.SUCCESS.equals(status)) {
			throw new PresentmentException(PresentmentError.PRMNT506);
		} else if (PresentmentStatus.BOUNCE.equals(pd.getStatus()) && "N".equals(pd.getFateCorrection())) {
			throw new PresentmentException(PresentmentError.PRMNT507);
		} else if (PresentmentStatus.BOUNCE.equals(pd.getStatus()) && PresentmentStatus.BOUNCE.equals(status)) {
			throw new PresentmentException(PresentmentError.PRMNT507);
		}

		if (pd.getAppDate().compareTo(pd.getSchDate()) < 0) {
			throw new PresentmentException(PresentmentError.PRMNT508);
		}
	}

	private AEEvent doPresentmentStageAccounting(PresentmentDetail pd) {
		long finID = pd.getFinID();
		String finReference = StringUtils.trimToEmpty(pd.getFinReference());
		Date schDate = pd.getSchDate();

		FinanceMain fm = presentmentDetailService.getDefualtPostingDetails(finID, schDate);

		String finType = fm.getFinType();

		AEEvent aeEvent = new AEEvent();
		aeEvent.setFinID(finID);
		aeEvent.setFinReference(finReference);
		aeEvent.setCustID(fm.getCustID());

		aeEvent.setFinType(finType);
		aeEvent.setBranch(fm.getFinBranch());
		aeEvent.setCcy(fm.getFinCcy());
		aeEvent.setPostingUserBranch(finType);
		aeEvent.setValueDate(pd.getSchDate());
		aeEvent.setPostDate(pd.getAppDate());
		aeEvent.setEntityCode(fm.getEntityCode());
		aeEvent.setAccountingEvent(AccountingEvent.PRSNTRSP);
		aeEvent.setPostRefId(pd.getReceiptID());

		if (PennantConstants.PROCESS_REPRESENTMENT.equals(pd.getPresentmentType())
				&& ImplementationConstants.PENALTY_CALC_ON_REPRESENTATION) {
			aeEvent.setValueDate(pd.getRepresentmentDate());
		}

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		amountCodes = aeEvent.getAeAmountCodes();
		amountCodes.setFinType(finType);
		amountCodes.setPartnerBankAc(fm.getPartnerBankAc());
		amountCodes.setPartnerBankAcType(fm.getPartnerBankAcType());

		BigDecimal presentmentAmt = pd.getPresentmentAmt();
		Map<String, Object> dataMap = aeEvent.getDataMap();
		dataMap = amountCodes.getDeclaredFieldValues(dataMap);
		dataMap.put("ae_presentmentAmt", presentmentAmt);
		aeEvent.setDataMap(dataMap);

		try {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finType, AccountingEvent.PRSNTRSP,
					FinanceConstants.MODULEID_FINTYPE));
			aeEvent.setDataMap(dataMap);
			aeEvent = postingsPreparationUtil.postAccounting(aeEvent);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			aeEvent.setErrorMessage(ErrorUtil.getErrorDetail(new ErrorDetail("Accounting Engine",
					PennantConstants.ERR_UNDEF, "E", "Accounting Engine Failed to Create Postings:" + e.getMessage(),
					new String[] {}, new String[] {})).getMessage());
			return aeEvent;
		}

		if (!aeEvent.isPostingSucess()) {
			throw new AppException("9998", "Presentment response accounting postings failed.");
		}

		return aeEvent;
	}

	private void updateFinanceDetails(ReceiptDTO receiptDTO) {
		logger.debug(Literal.ENTERING);

		PresentmentDetail pd = receiptDTO.getPresentmentDetail();

		Date appDate = pd.getAppDate();

		FinanceMain fm = receiptDTO.getFinanceMain();
		FinanceProfitDetail pftDetail = receiptDTO.getProfitDetail();
		List<FinanceScheduleDetail> schedules = receiptDTO.getSchedules();
		List<FinODDetails> overDueList = receiptDTO.getOdDetails();

		long finID = pd.getFinID();

		try {
			fm = repaymentPostingsUtil.updateStatus(fm, appDate, schedules, pftDetail, overDueList, null, false);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		if (!fm.isFinIsActive()) {
			financeMainDAO.updateMaturity(finID, FinanceConstants.CLOSE_STATUS_MATURED, false, pd.getSchDate());
			financeProfitDetailDAO.updateFinPftMaturity(finID, FinanceConstants.CLOSE_STATUS_MATURED, false);
		}

		if (CollectionUtils.isNotEmpty(overDueList)) {
			FinScheduleData scheduleData = new FinScheduleData();
			scheduleData.setFinanceMain(fm);
			scheduleData.setFinanceScheduleDetails(schedules);
			overDueList = receiptCalculator.calPenalty(scheduleData, null, appDate, overDueList);
			finODDetailsDAO.updateList(overDueList);
		}

		logger.debug(Literal.LEAVING);
	}

	private void updateFinReceiptHeader(FinReceiptHeader rh, Date appDate) {
		logger.info(Literal.ENTERING);

		long receiptID = rh.getReceiptID();
		String excessAdjustTo = rh.getExcessAdjustTo();

		List<FinReceiptDetail> receiptDetails = rh.getReceiptDetails();

		List<FinRepayHeader> repayHeaders = new ArrayList<>();
		for (FinReceiptDetail rd : receiptDetails) {
			repayHeaders.addAll(rd.getRepayHeaders());
		}

		for (FinRepayHeader rph : repayHeaders) {
			if (FinServiceEvent.SCHDRPY.equals(rh.getReceiptPurpose())) {
				if (rph.getExcessAmount().compareTo(BigDecimal.ZERO) > 0) {
					finExcessAmountDAO.updExcessAfterRealize(rph.getFinID(), excessAdjustTo, rph.getExcessAmount(),
							rh.getReceiptID());
				}
			}
		}

		finReceiptHeaderDAO.updateReceiptStatusAndRealizationDate(receiptID, RepayConstants.PAYSTATUS_REALIZED,
				appDate);
		finReceiptDetailDAO.updateReceiptStatusByReceiptId(receiptID, RepayConstants.PAYSTATUS_REALIZED);

		logger.info(Literal.LEAVING);
	}

	private FinReceiptHeader getFinReceiptHeader(long receiptId) {
		FinReceiptHeader rch = finReceiptHeaderDAO.getReceiptHeaderByID(receiptId, "");
		if (rch == null) {
			return rch;
		}

		List<FinReceiptDetail> receiptDetails = finReceiptDetailDAO.getReceiptHeaderByID(receiptId, "");
		rch.setReceiptDetails(receiptDetails);

		for (FinReceiptDetail rd : receiptDetails) {
			FinRepayHeader rph = financeRepaymentsDAO.getFinRepayHeadersByReceipt(rd.getReceiptSeqID(), "");
			if (rph != null) {
				rd.getRepayHeaders().add(rph);
			}
		}

		return rch;
	}

	public void updateResponse(long responseID) {
		presentmentDAO.updateResposeStatus(responseID, null, null, EodConstants.PROGRESS_SUCCESS);
	}

	public void updateResponse(long responseID, Exception e) {
		String errorCode = null;
		String errorDesc = null;

		if (e instanceof PresentmentException) {
			PresentmentException appException = (PresentmentException) e;

			errorCode = appException.code();
			errorDesc = appException.description();
		} else {
			errorDesc = e.getMessage();
		}

		if (StringUtils.trimToNull(errorDesc) != null) {
			errorDesc = (errorDesc.length() >= 2000) ? errorDesc.substring(0, 1998) : errorDesc;
		}

		if (PresentmentError.isValidation(errorCode)) {
			presentmentDAO.updateResposeStatus(responseID, errorCode, errorDesc, EodConstants.PROGRESS_FAILED);
		} else {
			presentmentDAO.updateResposeStatus(responseID, errorCode, errorDesc, EodConstants.PROGRESS_WAIT);
		}

	}

	public PresentmentDetail getPresentmentDetail(Long presentmentID) {
		return presentmentDAO.getPresentmentDetail(presentmentID);
	}

	// ----------------------------------------------------------------------------//
	// --------------------------------Dependencies--------------------------------//
	// ----------------------------------------------------------------------------//

	@Autowired
	public void setDueExtractionConfigDAO(DueExtractionConfigDAO dueExtractionConfigDAO) {
		this.dueExtractionConfigDAO = dueExtractionConfigDAO;
	}

	@Autowired
	public void setPresentmentDAO(PresentmentDAO presentmentDAO) {
		this.presentmentDAO = presentmentDAO;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setChequeDetailDAO(ChequeDetailDAO chequeDetailDAO) {
		this.chequeDetailDAO = chequeDetailDAO;
	}

	@Autowired
	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	@Autowired
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	@Autowired
	public void setFinReceiptDetailDAO(FinReceiptDetailDAO finReceiptDetailDAO) {
		this.finReceiptDetailDAO = finReceiptDetailDAO;
	}

	@Autowired
	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

	@Autowired
	public void setConsecutiveBounceDAO(ConsecutiveBounceDAO consecutiveBounceDAO) {
		this.consecutiveBounceDAO = consecutiveBounceDAO;
	}

	@Autowired
	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	@Autowired
	public void setMandateStatusDAO(MandateStatusDAO mandateStatusDAO) {
		this.mandateStatusDAO = mandateStatusDAO;
	}

	@Autowired
	public void setOverdrafLoanService(OverdrafLoanService overdrafLoanService) {
		this.overdrafLoanService = overdrafLoanService;
	}

	@Autowired
	public void setPresentmentDetailService(PresentmentDetailService presentmentDetailService) {
		this.presentmentDetailService = presentmentDetailService;
	}

	@Autowired
	public void setReceiptPaymentService(ReceiptPaymentService receiptPaymentService) {
		this.receiptPaymentService = receiptPaymentService;
	}

	@Autowired(required = false)
	@Qualifier(value = "externalPresentmentHook")
	public void setExternalPresentmentHook(ExternalPresentmentHook externalPresentmentHook) {
		this.externalPresentmentHook = externalPresentmentHook;
	}

	@Autowired
	public void setDefaultPresentmentRequest(PresentmentRequest defaultPresentmentRequest) {
		this.defaultPresentmentRequest = defaultPresentmentRequest;
	}

	@Autowired(required = false)
	@Qualifier(value = "presentmentRequest")
	public void setPresentmentRequest(PresentmentRequest presentmentRequest) {
		this.presentmentRequest = presentmentRequest;
	}

	@Autowired
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@Autowired
	public void setReceiptCancellationService(ReceiptCancellationService receiptCancellationService) {
		this.receiptCancellationService = receiptCancellationService;
	}

	@Autowired
	public void setRepaymentPostingsUtil(RepaymentPostingsUtil repaymentPostingsUtil) {
		this.repaymentPostingsUtil = repaymentPostingsUtil;
	}

	@Autowired
	public void setReceiptCalculator(ReceiptCalculator receiptCalculator) {
		this.receiptCalculator = receiptCalculator;
	}

	@Autowired(required = false)
	@Qualifier(value = "presentmentImportProcess")
	public void setPresentmentImportProcess(PresentmentImportProcess presentmentImportProcess) {
		this.presentmentImportProcess = presentmentImportProcess;
	}

	@Autowired
	public void setFinMandateService(FinMandateService finMandateService) {
		this.finMandateService = finMandateService;
	}

	private PresentmentRequest getPresentmentRequest() {
		return presentmentRequest == null ? defaultPresentmentRequest : presentmentRequest;
	}

	@Autowired
	public void setEventPropertiesService(EventPropertiesService eventPropertiesService) {
		this.eventPropertiesService = eventPropertiesService;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

}
