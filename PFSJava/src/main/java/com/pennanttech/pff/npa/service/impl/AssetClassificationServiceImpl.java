package com.pennanttech.pff.npa.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.core.CustEODEvent;
import com.pennant.app.core.FinEODEvent;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.npa.dao.AssetClassSetupDAO;
import com.pennanttech.pff.npa.dao.AssetClassificationDAO;
import com.pennanttech.pff.npa.model.AssetClassSetupDetail;
import com.pennanttech.pff.npa.model.AssetClassSetupHeader;
import com.pennanttech.pff.npa.model.AssetClassification;
import com.pennanttech.pff.npa.service.AssetClassificationService;
import com.pennanttech.pff.provision.model.NpaProvisionStage;

public class AssetClassificationServiceImpl implements AssetClassificationService {
	private static final Logger logger = LogManager.getLogger(AssetClassificationServiceImpl.class);

	private AssetClassificationDAO assetClassificationDAO;
	private AssetClassSetupDAO assetClassSetupDAO;
	private PostingsPreparationUtil postingsPreparationUtil;

	private static final String ERR_CODE_01 = "Asset Classification Setup configuration is not exists for Entity Code : %s";

	public AssetClassificationServiceImpl() {
		super();
	}

	@Override
	public void clearStage() {
		assetClassificationDAO.clearStage();
	}

	@Override
	public void process(CustEODEvent custEODEvent) {
		Customer customer = custEODEvent.getCustomer();
		long custID = customer.getCustID();
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		Date appDate = custEODEvent.getEodDate();

		List<NpaProvisionStage> list = new ArrayList<>();

		for (FinEODEvent finEODEvent : finEODEvents) {
			FinanceMain fm = finEODEvent.getFinanceMain();

			if (fm.isWriteoffLoan()) {
				continue;
			}

			FinanceProfitDetail pd = finEODEvent.getFinProfitDetail();
			List<FinODDetails> odDetails = finEODEvent.getFinODDetails();

			int pastDueDays = pd.getCurODDays();

			Date pastDueDate = finDpdDate(odDetails);

			NpaProvisionStage item = new NpaProvisionStage();

			item.setEntityCode(fm.getEntityCode());
			item.setEodDate(appDate);
			item.setCustID(custID);
			item.setCustCategoryCode(customer.getCustCtgCode());
			item.setFinType(fm.getFinType());
			item.setProduct(pd.getFinCategory());
			item.setFinCcy(fm.getFinCcy());
			item.setFinBranch(fm.getFinBranch());
			item.setFinID(fm.getFinID());
			item.setFinReference(fm.getFinReference());
			item.setFinAssetValue(fm.getFinAssetValue());
			item.setFinCurrAssetValue(fm.getFinCurrAssetValue());

			item.setOsPrincipal(pd.getTotalPriBal());
			item.setOsProfit(pd.getTotalPftBal());
			item.setFuturePrincipal(pd.getFutureRpyPri());
			item.setOdPrincipal(pd.getODPrincipal());
			item.setOdProfit(pd.getODProfit().add(pd.getTotalPftCpz()));
			item.setTotPriBal(pd.getTotalPriBal());
			item.setTotPriPaid(pd.getTotalPriPaid());
			item.setTotPftPaid(pd.getTotalPftPaid());
			item.setTotPftAccrued(pd.getPftAccrued());
			item.setAmzTillLBDate(pd.getAmzTillLBD());
			item.setTillDateSchdPri(pd.getTdSchdPriBal());

			item.setPastDueDays(pastDueDays);
			item.setPastDueDate(pastDueDate);
			item.setEffFinID(fm.getFinID());
			item.setEffFinReference(fm.getFinReference());
			item.setLinkedLoan(false);

			list.add(item);
		}

		assetClassificationDAO.saveStage(list);
	}

	@Override
	public void createSnapshots(Date appDate) {
		assetClassificationDAO.deleteSnapshots(appDate);
		assetClassificationDAO.createSnapShots(appDate, null, null);
	}

	@Override
	public long prepareQueue() {
		assetClassificationDAO.deleteQueue();
		return assetClassificationDAO.prepareQueue();
	}

	@Override
	public long getQueueCount() {
		return assetClassificationDAO.getQueueCount();
	}

	@Override
	public int updateThreadID(long from, long to, int threadID) {
		return assetClassificationDAO.updateThreadID(from, to, threadID);
	}

	@Override
	public void updateProgress(long finID, int progressInProcess) {
		assetClassificationDAO.updateProgress(finID, progressInProcess);
	}

	@Override
	public AssetClassification getClassification(long finID) {
		return assetClassificationDAO.getClassification(finID);
	}

	@Override
	public void setNpaClassification(AssetClassification ac) {
		Map<String, AssetClassSetupHeader> assetClassSetup = ac.getAssetClassSetup();

		String entityCode = ac.getEntityCode();

		AssetClassSetupHeader assetClassSetupHeader = assetClassSetup.get(entityCode);

		if (assetClassSetupHeader == null) {
			throw new AppException(String.format(ERR_CODE_01, entityCode));
		}

		int pastDueDays = ac.getPastDueDays();
		Date pastDueDate = ac.getPastDueDate();

		int npaPastDueDays = ac.getNpaPastDueDays();

		if (ac.getNpaPastDueDate() == null && pastDueDays > 0) {
			ac.setNpaPastDueDays(pastDueDays);
			ac.setNpaPastDueDate(pastDueDate);
		} else if (ac.isNpaStage()) {
			ac.setNpaPastDueDays(npaPastDueDays + 1);
		} else {
			ac.setNpaPastDueDays(pastDueDays);
			ac.setNpaPastDueDate(pastDueDate);
		}

		AssetClassSetupDetail acsd = getAssetClassSetup(ac.getNpaPastDueDays(), assetClassSetupHeader);

		if (acsd == null) {
			return;
		}

		ac.setNpaStage(acsd.isNpaStage());
		ac.setNpaClassID(acsd.getId());
	}

	@Override
	public List<NpaProvisionStage> getLinkedLoans(AssetClassification ca) {
		List<NpaProvisionStage> linkedLoans = new ArrayList<>();
		List<FinanceMain> list = new ArrayList<>();

		Long finID = ca.getFinID();
		long custID = ca.getCustID();

		switch (ImplementationConstants.NPA_SCOPE) {
		case CUSTOMER:
			list.addAll(getPrimaryLoans(finID, custID));
			break;
		case CO_APPLICANT:
			list.addAll(getPrimaryLoans(finID, custID));
			list.addAll(getCoApplicantLoans(finID));
			break;
		case GUARANTOR:
			list.addAll(getPrimaryLoans(finID, custID));
			list.addAll(getCoApplicantLoans(finID));
			list.addAll(getGuarantorLoans(finID));
			break;
		default:
			break;
		}

		if (list.isEmpty()) {
			return linkedLoans;
		}

		/**
		 * The below code will remove the duplicates with FinReference and EffFinReference
		 */
		Set<NpaProvisionStage> set = getLinkedAssetClassifications(ca, list).stream().collect(
				Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(NpaProvisionStage::getFinReference)
						.thenComparing(NpaProvisionStage::getEffFinReference))));

		List<NpaProvisionStage> filterList = new ArrayList<>();
		filterList.addAll(new ArrayList<>(set));

		/**
		 * The below code will remove the duplicates with FinReference, EffFinReference.
		 */
		set = filterList.stream().collect(Collectors.toCollection(() -> new TreeSet<>(Comparator
				.comparing(NpaProvisionStage::getFinReference).thenComparing(NpaProvisionStage::getEffFinReference))));

		linkedLoans.addAll(new ArrayList<>(set));

		return linkedLoans;
	}

	@Override
	public void saveOrUpdate(AssetClassification ac) {
		ac.setVersion(ac.getVersion() + 1);
		ac.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		if (ac.getId() == null) {
			ac.setCreatedOn(ac.getLastMntOn());

			assetClassificationDAO.save(ac);
		} else {
			assetClassificationDAO.update(ac);
		}
	}

	@Override
	public void saveStage(List<NpaProvisionStage> list) {
		assetClassificationDAO.saveStage(list);
	}

	@Override
	public Map<String, AssetClassSetupHeader> getAssetClassSetups() {
		Map<String, AssetClassSetupHeader> map = new HashMap<>();

		assetClassSetupDAO.getAssetClassSetups().forEach(acsh -> map.put(acsh.getEntityCode(), acsh));

		return map;
	}

	@Override
	public AssetClassification getNpaDetails(long finID) {
		return assetClassificationDAO.getNpaDetails(finID);
	}

	private void setEffNpaDetails(AssetClassification npa, AssetClassification effective) {
		npa.setEffFinID(effective.getFinID());
		npa.setEffFinReference(effective.getFinReference());
		npa.setEffPastDueDays(effective.getPastDueDays());
		npa.setEffPastDueDate(effective.getPastDueDate());
		npa.setEffNpaPastDueDays(effective.getNpaPastDueDays());
		npa.setEffNpaPastDueDate(effective.getNpaPastDueDate());
		npa.setEffNpaClassID(effective.getNpaClassID());
		npa.setEffNpaStage(effective.isNpaStage());
		npa.setFinIsActive(true);
	}

	@Override
	public AssetClassification setEffClassification(AssetClassification npa) {
		Long finID = npa.getFinID();
		List<AssetClassification> list = assetClassificationDAO.getClassifications(finID);

		AssetClassification effective = getEffAssetClassification(list);

		Long effFinID = effective.getEffFinID();

		AssetClassification effNpa = assetClassificationDAO.getNpaClassification(effFinID);

		setEffNpaDetails(npa, effNpa);

		if (npa.getPastDueDays() == 0 && npa.isEffNpaStage()) {
			npa.setNpaPastDueDays(0);
			npa.setNpaPastDueDate(null);
			npa.setNpaClassID(getNpaClassId(npa, list));

			setEffNpaDetails(npa, effNpa);
		}

		int totNpaPastDueDays = list.stream().mapToInt(pd -> pd.getPastDueDays()).sum();

		if (totNpaPastDueDays == 0) {
			npa.setEffFinID(null);
			npa.setEffFinReference(null);
			npa.setPastDueDays(0);
			npa.setPastDueDate(null);
			npa.setNpaStage(false);
			npa.setNpaPastDueDays(0);
			npa.setNpaPastDueDate(null);
			npa.setEffNpaStage(false);
			npa.setLinkedTranID(null);
			npa.setFinIsActive(true);

			npa.setNpaClassID(getNpaClassId(npa, list));

			setEffNpaDetails(npa, npa);
		}

		return npa;
	}

	private Long getNpaClassId(AssetClassification npa, List<AssetClassification> list) {
		String entityCode = getEntityCode(npa.getFinReference(), list);

		AssetClassSetupHeader acsh = npa.getAssetClassSetup().get(entityCode);

		AssetClassSetupDetail acsd = getAssetClassSetup(npa.getNpaPastDueDays(), acsh);

		if (acsd == null) {
			return null;
		}

		return acsd.getId();
	}

	@Override
	public void setLoanInfo(AssetClassification ac) {
		AssetClassification item = assetClassificationDAO.getLoanInfo(ac.getFinID());

		ac.setEodDate(item.getEodDate());
		ac.setEntityCode(item.getEntityCode());
		ac.setCustID(item.getCustID());
		ac.setCustCategoryCode(item.getCustCategoryCode());
		ac.setFinType(item.getFinType());
		ac.setFinCcy(item.getFinCcy());
		ac.setProduct(item.getProduct());
		ac.setFinBranch(item.getFinBranch());
		ac.setFinAssetValue(item.getFinAssetValue());
		ac.setFinCurrAssetValue(item.getFinCurrAssetValue());
		ac.setOsPrincipal(item.getOsPrincipal());
		ac.setOsProfit(item.getOsProfit());
		ac.setFuturePrincipal(item.getFuturePrincipal());
		ac.setOdPrincipal(item.getOdPrincipal());
		ac.setOdProfit(item.getOdProfit());
		ac.setTotPriBal(item.getTotPriBal());
		ac.setTotPriPaid(item.getTotPriPaid());
		ac.setTotPftPaid(item.getTotPftPaid());
		ac.setTotPftAccrued(item.getTotPftAccrued());
		ac.setAmzTillLBDate(item.getAmzTillLBDate());
		ac.setTillDateSchdPri(item.getTillDateSchdPri());

	}

	@Override
	public void doPostNpaChange(AssetClassification npaAc) {
		String eventCode = AccountingEvent.NPACHNG;
		int moduleID = FinanceConstants.MODULEID_FINTYPE;

		Long accountingID = AccountingConfigCache.getCacheAccountSetID(npaAc.getFinType(), eventCode, moduleID);

		if (accountingID == null || accountingID == Long.MIN_VALUE) {
			logger.debug("Accounting Set not found with {} Event and {} Loan Type", eventCode, npaAc.getFinType());
			return;
		}

		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCode = new AEAmountCodes();
		aeEvent.setAeAmountCodes(amountCode);

		aeEvent.setFinReference(npaAc.getFinReference());
		aeEvent.setAccountingEvent(eventCode);
		aeEvent.setPostDate(npaAc.getEodDate());
		aeEvent.setValueDate(npaAc.getEodDate());

		aeEvent.setBranch(npaAc.getFinBranch());
		aeEvent.setCcy(npaAc.getFinCcy());
		aeEvent.setFinType(npaAc.getFinType());
		aeEvent.setCustID(npaAc.getCustID());

		amountCode.setFinType(aeEvent.getFinType());

		amountCode.setInsttot(npaAc.getOdPrincipal().add(npaAc.getOdProfit()));
		amountCode.setdAmz((npaAc.getTotPftPaid().add(npaAc.getTotPftAccrued())));
		amountCode.setTotPriSchd(npaAc.getTotPriBal());
		amountCode.setTdSchdPri(npaAc.getTillDateSchdPri());

		aeEvent.setDataMap(amountCode.getDeclaredFieldValues());
		aeEvent.getAcSetIDList().add(accountingID);
		aeEvent.setCustAppDate(npaAc.getEodDate());

		aeEvent.setPostingUserBranch("EOD");
		aeEvent.setEOD(true);

		postingsPreparationUtil.postAccountingEOD(aeEvent);

		postingsPreparationUtil.saveAccountingEOD(aeEvent.getReturnDataSet());

		npaAc.setLinkedTranID(aeEvent.getLinkedTranId());
	}

	@Override
	public void updateClassification(AssetClassification ac) {
		assetClassificationDAO.updateClassification(ac);
	}

	@Override
	public AssetClassification getAssetClassification(long finID) {
		return assetClassificationDAO.getAssetClassification(finID);
	}

	@Override
	public boolean isEffNpaStage(long finID) {
		return assetClassificationDAO.isEffNpaStage(finID);
	}

	@Override
	public String getNpaRepayHierarchy(long finID) {
		return assetClassificationDAO.getNpaRepayHierarchy(finID);
	}

	@Override
	public void doProcessEarlySettlement(long finID) {
		AssetClassification npa = getNpaDetails(finID);

		if (npa == null) {
			return;
		}

		assetClassificationDAO.createSnapShots(SysParamUtil.getAppDate(), finID, null);

		assetClassificationDAO.updatePastDuesForES(finID);

		Map<String, AssetClassSetupHeader> assetClassSetups = getAssetClassSetups();

		npa.setPastDueDays(0);
		npa.setPastDueDate(null);
		npa.setNpaStage(false);
		npa.setFinIsActive(false);
		npa.setAssetClassSetup(assetClassSetups);
		npa.setEntityCode(assetClassificationDAO.getEntityCodeFromStage(finID));

		setNpaClassification(npa);

		npa.setEffPastDueDays(0);
		npa.setEffPastDueDate(null);
		npa.setEffNpaPastDueDays(0);
		npa.setEffNpaPastDueDate(null);
		npa.setEffNpaStage(npa.isNpaStage());
		npa.setEffNpaClassID(npa.getNpaClassID());
		npa.setFinID(finID);
		npa.setEffFinReference(npa.getFinReference());

		updateClassification(npa);
	}

	private List<NpaProvisionStage> getLinkedAssetClassifications(final AssetClassification item,
			final List<FinanceMain> effLoans) {
		List<NpaProvisionStage> list = new ArrayList<>();

		effLoans.forEach(fm -> {
			NpaProvisionStage linkedAc = new NpaProvisionStage();
			linkedAc.setCustID(item.getCustID());
			linkedAc.setFinID(item.getFinID());
			linkedAc.setFinReference(item.getFinReference());
			linkedAc.setEffFinID(fm.getFinID());
			linkedAc.setEffFinReference(fm.getFinReference());
			linkedAc.setLinkedLoan(true);
			linkedAc.setEntityCode(item.getEntityCode());

			list.add(linkedAc);

			linkedAc = new NpaProvisionStage();
			linkedAc.setCustID(fm.getCustID());
			linkedAc.setFinID(fm.getFinID());
			linkedAc.setFinReference(fm.getFinReference());
			linkedAc.setEffFinID(item.getFinID());
			linkedAc.setEffFinReference(item.getFinReference());
			linkedAc.setLinkedLoan(true);
			linkedAc.setEntityCode(fm.getEntityCode());

			list.add(linkedAc);

		});

		return list;
	}

	private AssetClassification getEffAssetClassification(List<AssetClassification> list) {
		list = list.stream().sorted((ac1, ac2) -> Boolean.compare(ac1.isLinkedLoan(), ac2.isLinkedLoan()))
				.collect(Collectors.toList());

		return list.stream().max(Comparator.comparingInt(AssetClassification::getNpaPastDueDays)).get();
	}

	private AssetClassSetupDetail getAssetClassSetup(int npaPastDueDays, AssetClassSetupHeader assetClassSetupHeader) {
		List<AssetClassSetupDetail> list = assetClassSetupHeader.getDetails();
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		for (AssetClassSetupDetail ac : list) {
			if (npaPastDueDays >= ac.getDpdMin() && npaPastDueDays <= ac.getDpdMax()) {
				return ac;
			}
		}

		AssetClassSetupDetail acsd = list.get(0);
		if (npaPastDueDays >= acsd.getDpdMax()) {
			return acsd;
		}

		return null;
	}

	private Date finDpdDate(List<FinODDetails> odDetails) {
		for (FinODDetails od : odDetails) {
			BigDecimal odAmount = od.getFinCurODPri().add(od.getFinCurODPft());

			if (odAmount.compareTo(BigDecimal.ZERO) > 0) {
				return od.getFinODSchdDate();
			}
		}

		return null;
	}

	private List<FinanceMain> getPrimaryLoans(Long finID, long custID) {
		List<FinanceMain> primaryLoans = new ArrayList<>();
		List<FinanceMain> list = assetClassificationDAO.getPrimaryLoans(custID);

		list.forEach(fm -> {
			if (fm.getFinID() != finID && !fm.isWriteoffLoan()) {
				primaryLoans.add(fm);
			}
		});

		return primaryLoans;
	}

	private List<FinanceMain> getCoApplicantLoans(Long finID) {
		List<FinanceMain> coApplicantLoans = new ArrayList<>();
		List<FinanceMain> list = assetClassificationDAO.getCoApplicantLoans(finID);

		list.forEach(fm -> {
			if (fm.getFinID() != finID && !fm.isWriteoffLoan()) {
				coApplicantLoans.add(fm);
			}
		});

		return coApplicantLoans;
	}

	private List<FinanceMain> getGuarantorLoans(Long finID) {
		List<FinanceMain> guarantorLoans = new ArrayList<>();
		List<FinanceMain> list = assetClassificationDAO.getGuarantorLoans(finID);

		list.forEach(fm -> {
			if (fm.getFinID() != finID && !fm.isWriteoffLoan()) {
				guarantorLoans.add(fm);
			}
		});

		return guarantorLoans;
	}

	private String getEntityCode(String finReference, List<AssetClassification> list) {
		return list.stream().filter(ac -> ac.getFinReference().equals(finReference)).findFirst().get().getEntityCode();
	}

	@Autowired
	public void setAssetClassificationDAO(AssetClassificationDAO assetClassificationDAO) {
		this.assetClassificationDAO = assetClassificationDAO;
	}

	@Autowired
	public void setAssetClassSetupDAO(AssetClassSetupDAO assetClassSetupDAO) {
		this.assetClassSetupDAO = assetClassSetupDAO;
	}

	@Autowired
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}
}