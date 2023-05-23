package com.pennanttech.pff.provision.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.RuleResult;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.pff.extension.NpaAndProvisionExtension;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.provision.ProvisionBook;
import com.pennanttech.pff.provision.dao.ProvisionDAO;
import com.pennanttech.pff.provision.model.Provision;
import com.pennanttech.pff.provision.model.ProvisionRuleData;
import com.pennanttech.pff.provision.service.ProvisionService;

public class ProvisionServiceImpl implements ProvisionService {
	protected static final Logger logger = LogManager.getLogger(ProvisionServiceImpl.class);

	private static final String RULE_ERR = "%s Provision Rule is should not be blank for Loan Type %s";

	private ProvisionDAO provisionDao;
	private AccountEngineExecution engineExecution;
	private PostingsDAO postingsDAO;
	private AuditHeaderDAO auditHeaderDAO;

	@Override
	public long prepareQueueForSOM() {
		provisionDao.deleteQueue();
		return provisionDao.prepareQueueForSOM();
	}

	@Override
	public long prepareQueueForEOM() {
		provisionDao.deleteQueue();
		return provisionDao.prepareQueueForEOM();
	}

	@Override
	public long getQueueCount() {
		return provisionDao.getQueueCount();
	}

	@Override
	public int updateThreadID(long from, long to, int i) {
		return provisionDao.updateThreadID(from, to, i);
	}

	@Override
	public void updateProgress(long finID, int progressInProcess) {
		provisionDao.updateProgress(finID, progressInProcess);
	}

	@Override
	public Long getLinkedTranId(long finID) {
		return provisionDao.getLinkedTranId(finID);
	}

	@Override
	public void doReversal(long linkedTranId) {
		long newLinkedTranID = postingsDAO.getLinkedTransId();

		List<ReturnDataSet> returnDataSets = postingsDAO.getPostingsByLinkTransId(linkedTranId);

		engineExecution.getReversePostings(returnDataSets, newLinkedTranID);

		postingsDAO.saveBatch(returnDataSets);
	}

	@Override
	public Provision getProvision(long finID, Date appDate, Provision mp) {
		ProvisionRuleData prd = provisionDao.getProvisionData(finID);

		if (prd == null) {
			prd = provisionDao.getProvisionDataForUpload(finID);
		}

		Provision p = provisionDao.getProvision(finID);

		Timestamp timeStamp = new Timestamp(System.currentTimeMillis());

		boolean newRecord = false;

		BigDecimal manProvsnPer = BigDecimal.ZERO;
		boolean manualProvision = false;

		if (p == null) {
			p = new Provision();
			p.setFinID(prd.getFinID());
			p.setFinReference(prd.getFinReference());
			p.setManualProvision(mp.isManualProvision());

			p.setCreatedBy(mp.getLastMntBy());
			p.setCreatedOn(timeStamp);

			manProvsnPer = p.getManProvsnPer();

			newRecord = true;
		} else {
			manProvsnPer = mp.getManProvsnPer();
		}

		if (mp != null) {
			manualProvision = true;
			if (mp.getManualAssetClassID() != null) {
				p.setManualAssetClassID(mp.getManualAssetClassID());
				p.setManualAssetClassCode(mp.getManualAssetClassCode());
			} else {
				p.setManualAssetClassID(prd.getEffNpaClassID());
				p.setManualAssetClassCode(prd.getEffNpaClassCode());
			}

			if (mp.getManualAssetSubClassID() != null) {
				p.setManualAssetSubClassID(mp.getManualAssetSubClassID());
				p.setManualAssetSubClassCode(mp.getManualAssetSubClassCode());
			} else {
				p.setManualAssetSubClassID(prd.getEffNpaSubbClassID());
				p.setManualAssetSubClassCode(prd.getEffNpaSubClassCode());
			}

			p.setOverrideProvision(mp.isOverrideProvision());

			manProvsnPer = mp.getManProvsnPer();
			p.setManProvsnPer(manProvsnPer);
			p.setManualProvision(mp.isManualProvision());
		}

		p.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		if (p.isManualProvision() || mp.isOverrideProvision()) {
			prd.setEffNpaClassCode(p.getManualAssetClassCode());
			prd.setEffNpaSubClassCode(p.getManualAssetSubClassCode());

			executeProvisionRule(prd, p);
			BigDecimal osPrincipal = prd.getOutstandingprincipal();
			p.setManProvsnAmt(osPrincipal.multiply(manProvsnPer.divide(new BigDecimal(100))));

		} else {
			executeProvisionRule(prd, p);
		}

		p.setProvisionDate(appDate);
		p.setPastDueDays(prd.getPastDueDays());
		p.setNpaAging(prd.getNpaAge());
		p.setEffNpaAging(prd.getEffNpaAge());
		p.setNpaPastDueDays(prd.getNpaPastDueDays());
		p.setEffNpaPastDueDays(prd.getEffNpaPastDueDays());
		p.setNpaClassID(prd.getNpaClassID());
		p.setEffNpaClassCode(prd.getEffNpaClassCode());
		p.setEffNpaSubClassCode(prd.getEffNpaSubClassCode());

		if (newRecord || p.getNpaClassID() != prd.getNpaClassID()) {
			p.setNpaClassChng(true);
		} else {
			p.setNpaClassChng(false);
		}

		p.setEffNpaClassID(prd.getEffNpaClassID());
		p.setOsPrincipal(prd.getOutstandingprincipal());
		p.setOsProfit(prd.getOsProfit());
		p.setOdPrincipal(prd.getOdPrincipal());
		p.setOdProfit(prd.getOdProfit());
		p.setTotPftAccrued(prd.getTotPftAccrued());
		p.setTillDateSchdPri(prd.getTillDateSchdPri());

		p.setCustID(prd.getCustID());
		p.setFinBranch(prd.getFinBranch());
		p.setFinCcy(prd.getFinCCY());
		p.setFinType(prd.getFinType());
		p.setEntityCode(prd.getEntityCode());

		if (manualProvision && newRecord) {
			provisionDao.save(p, TableType.MAIN_TAB);
			Provision p1 = provisionDao.getProvision(finID);

			p.setId(p1.getId());

			provisionDao.save(p, TableType.TEMP_TAB);
		}

		return p;
	}

	@Override
	public void doPost(Provision p) {
		String eventCode = AccountingEvent.PROVSN;
		int moduleID = FinanceConstants.MODULEID_FINTYPE;

		Long accountingID = AccountingEngine.getAccountSetID(p.getFinType(), eventCode, moduleID);

		if (accountingID == null || accountingID <= 0) {
			return;
		}

		AEAmountCodes amountCode = new AEAmountCodes();

		amountCode.setFinType(p.getFinType());

		BigDecimal provsnAmt = BigDecimal.ZERO;
		if (p.isManualProvision()) {
			provsnAmt = p.getManProvsnAmt();
		} else if (NpaAndProvisionExtension.PROVISION_BOOKS == ProvisionBook.REGULATORY) {
			provsnAmt = p.getRegProvsnAmt();
		} else {
			provsnAmt = p.getIntProvsnAmt();
		}

		if (provsnAmt.compareTo(BigDecimal.ZERO) < 0) {
			provsnAmt = BigDecimal.ZERO;
		}

		amountCode.setProvsnAmt(provsnAmt);
		amountCode.setNpaClass(p.getEffNpaClassCode());
		amountCode.setNpaSubClass(p.getEffNpaSubClassCode());

		AEEvent aeEvent = new AEEvent();
		aeEvent.setFinID(p.getFinID());
		aeEvent.setFinReference(p.getFinReference());
		aeEvent.setAccountingEvent(eventCode);
		aeEvent.setPostDate(p.getProvisionDate());
		aeEvent.setValueDate(p.getProvisionDate());
		aeEvent.setEntityCode(p.getEntityCode());
		aeEvent.setBranch(p.getFinBranch());
		aeEvent.setCcy(p.getFinCcy());
		aeEvent.setFinType(p.getFinType());
		aeEvent.setCustID(p.getCustID());
		aeEvent.getAcSetIDList().add(accountingID);
		aeEvent.setCustAppDate(p.getProvisionDate());
		aeEvent.setPostingUserBranch(PennantConstants.APP_PHASE_EOD);
		aeEvent.setEOD(true);

		aeEvent.setAeAmountCodes(amountCode);
		aeEvent.setDataMap(amountCode.getDeclaredFieldValues());

		if (aeEvent.getLinkedTranId() <= 0) {
			aeEvent.setLinkedTranId(postingsDAO.getLinkedTransId());
		}

		p.setLinkedTranId(aeEvent.getLinkedTranId());
		engineExecution.getAccEngineExecResults(aeEvent);

		if (CollectionUtils.isEmpty(aeEvent.getReturnDataSet())) {
			return;
		}

		postingsDAO.saveBatch(aeEvent.getReturnDataSet());
	}

	@Override
	public void save(Provision p) {
		provisionDao.save(p, TableType.MAIN_TAB);
	}

	@Override
	public void update(Provision p) {
		provisionDao.update(p, TableType.MAIN_TAB);
	}

	@Override
	public Provision getProvisionDetail(long finID) {
		Provision provision = provisionDao.getProvisionDetail(finID);
		return provision;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		TableType tableType = TableType.MAIN_TAB;
		Provision provision = (Provision) auditHeader.getAuditDetail().getModelData();

		if (provision.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (provision.isNew()) {
			provisionDao.save(provision, tableType);
		} else {
			provisionDao.update(provision, tableType);
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new Provision(), provision.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				provision.getBefImage(), provision));

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		Provision provision = new Provision();
		AuditDetail auditDetail = auditHeader.getAuditDetail();
		BeanUtils.copyProperties((Provision) auditDetail.getModelData(), provision);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(provision.getRecordType())) {
			auditDetail.setBefImage(provisionDao.getProvisionById(provision.getId(), TableType.MAIN_TAB));
		}

		if (PennantConstants.RECORD_TYPE_DEL.equals(provision.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			provisionDao.delete(provision.getFinID(), TableType.MAIN_TAB);
		} else {
			provision.setRoleCode("");
			provision.setNextRoleCode("");
			provision.setTaskId("");
			provision.setNextTaskId("");
			provision.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(provision.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				provision.setRecordType("");
				provisionDao.save(provision, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				provision.setRecordType("");
				provisionDao.update(provision, TableType.MAIN_TAB);
			}
		}

		provisionDao.delete(provision.getFinID(), TableType.TEMP_TAB);

		String[] fields = PennantJavaUtil.getFieldDetails(new Provision(), provision.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				provision.getBefImage(), provision));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditDetail.setAuditTranType(tranType);
		auditDetail.setModelData(provision);

		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		Provision provision = (Provision) auditHeader.getAuditDetail().getModelData();
		provisionDao.delete(provision.getFinID(), TableType.MAIN_TAB);

		String[] fields = PennantJavaUtil.getFieldDetails(new Provision(), provision.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				provision.getBefImage(), provision));

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doReject");

		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		Provision provision = (Provision) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		provisionDao.delete(provision.getFinID(), TableType.TEMP_TAB);

		String[] fields = PennantJavaUtil.getFieldDetails(new Provision(), provision.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				provision.getBefImage(), provision));

		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public void executeProvisionRule(ProvisionRuleData provisionData, Provision p) {
		String regProvsnRule = provisionData.getRegProvsnRule();
		String intProvsnRule = provisionData.getIntProvsnRule();

		String finReference = provisionData.getFinReference();
		String finType = provisionData.getFinType();

		String provisionBook = NpaAndProvisionExtension.PROVISION_BOOKS.description();

		switch (NpaAndProvisionExtension.PROVISION_BOOKS) {
		case REGULATORY:
			if (regProvsnRule == null) {
				throw new AppException(String.format(RULE_ERR, provisionBook, finType));
			}
			break;
		case INTERNAL:
			if (intProvsnRule == null) {
				throw new AppException(String.format(RULE_ERR, provisionBook, finType));
			}
			break;

		default:
			break;
		}

		BigDecimal collateralAmt = getCollateralAmt(finReference, regProvsnRule, intProvsnRule);

		if (collateralAmt.compareTo(BigDecimal.ZERO) > 0) {
			provisionData.setSecured(true);
		}

		p.setCollateralAmt(collateralAmt);
		p.setInsuranceAmt(getVasAmt(finReference, regProvsnRule, intProvsnRule));

		RuleResult regRuleResult = execute(provisionData, regProvsnRule);
		RuleResult intRuleResult = execute(provisionData, intProvsnRule);

		BigDecimal regProvsnPer = getResult(regRuleResult.getProvPercentage());
		BigDecimal regProvsnAmt = getResult(regRuleResult.getProvAmount());
		BigDecimal intProvsnPer = getResult(intRuleResult.getProvPercentage());
		BigDecimal intProvsnAmt = getResult(intRuleResult.getProvAmount());

		if (!NpaAndProvisionExtension.PROVISION_REVERSAL_REQ) {
			regProvsnAmt = regProvsnAmt.subtract(p.getRegProvsnAmt());
			if (regProvsnAmt.compareTo(BigDecimal.ZERO) < 0) {
				regProvsnAmt = BigDecimal.ZERO;
			}

			intProvsnPer = intProvsnPer.subtract(p.getIntProvsnAmt());
			if (intProvsnPer.compareTo(BigDecimal.ZERO) < 0) {
				intProvsnPer = BigDecimal.ZERO;
			}
		}

		p.setRegProvsnPer(regProvsnPer);
		p.setRegProvsnAmt(regProvsnAmt);
		p.setTotRegProvsnAmt(regProvsnAmt);
		p.setIntProvsnPer(intProvsnPer);
		p.setIntProvsnAmt(intProvsnAmt);
		p.setTotIntProvsnAmt(intProvsnAmt);

		if (provisionData.isSecured()) {
			p.setRegSecProvsnPer(regProvsnPer);
			p.setRegSecProvsnAmt(regProvsnAmt);
			p.setIntSecProvsnPer(intProvsnPer);
			p.setIntSecProvsnAmt(intProvsnAmt);
		} else {
			p.setRegUnSecProvsnPer(regProvsnPer);
			p.setRegUnSecProvsnAmt(regProvsnAmt);
			p.setIntUnSecProvsnPer(intProvsnPer);
			p.setIntUnSecProvsnAmt(intProvsnAmt);
		}
	}

	@Override
	public boolean isRecordExists(long finID) {
		return provisionDao.isRecordExists(finID);
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method);
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader nextProcess(AuditHeader ah) {
		boolean nextProcess = true;

		if (ah.getErrorMessage() != null && ah.getErrorMessage().size() > 0 && !ah.isOveride()) {
			nextProcess = false;
		}

		if (nextProcess) {
			if (ah.getOverideMessage() != null && ah.getOverideMessage().size() > 0) {
				nextProcess = true;
				if (ah.getOverideMap() == null) {
					nextProcess = false;
				} else {
					for (int i = 0; i < ah.getOverideMessage().size(); i++) {
						nextProcess = false;
					}
					if (nextProcess) {
						ah.setOveride(true);
					}
				}
			}
		}

		ah.setNextProcess(nextProcess);
		return ah;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<>());
		Provision provision = (Provision) auditDetail.getModelData();

		Provision tempProvision = null;

		if (provision.isWorkflow()) {
			tempProvision = provisionDao.getProvisionById(provision.getId(), TableType.TEMP_TAB);
		}

		Provision befProvision = provisionDao.getProvisionById(provision.getId(), TableType.MAIN_TAB);
		Provision oldProvision = provision.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = provision.getFinReference();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

		if (provision.isNew()) {
			if (!provision.isWorkflow()) {
				if (befProvision != null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else {
				if (provision.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					if (befProvision != null || tempProvision != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			if (!provision.isWorkflow()) {
				if (befProvision == null) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldProvision != null && !oldProvision.getLastMntOn().equals(befProvision.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !provision.isWorkflow()) {
			provision.setBefImage(befProvision);
		}

		return auditDetail;
	}

	private RuleResult execute(ProvisionRuleData ruleData, String rule) {
		if (rule == null) {
			return new RuleResult();
		}

		Map<String, Object> dataMap = ruleData.getDeclaredFields();

		return (RuleResult) RuleExecutionUtil.executeRule(rule, dataMap, ruleData.getFinCCY(), RuleReturnType.OBJECT);
	}

	private BigDecimal getResult(Object result) {
		if (result == null) {
			result = BigDecimal.ZERO;
		}

		try {
			return new BigDecimal(result.toString());
		} catch (Exception e) {
			logger.error("Invalid Result :" + result);
		}

		return BigDecimal.ZERO;
	}

	private BigDecimal getCollateralAmt(String finReference, String regProvsnRule, String intProvsnRule) {
		BigDecimal collateralValue = BigDecimal.ZERO;

		if (regProvsnRule != null) {
			if (regProvsnRule.contains("collateralType") || regProvsnRule.contains("secured")) {
				collateralValue = provisionDao.getCollateralValue(finReference);
			}
		}

		if (intProvsnRule != null && collateralValue.compareTo(BigDecimal.ZERO) <= 0) {
			if (intProvsnRule.contains("collateralType") || intProvsnRule.contains("secured")) {
				collateralValue = provisionDao.getCollateralValue(finReference);
			}
		}

		return collateralValue;
	}

	private BigDecimal getVasAmt(String finReference, String regProvsnRule, String intProvsnRule) {
		BigDecimal vasFee = BigDecimal.ZERO;

		if (regProvsnRule != null) {
			if (regProvsnRule.contains("InsuranceAmount")) {
				vasFee = provisionDao.getVasFee(finReference);
			}
		}

		if (intProvsnRule != null && vasFee.compareTo(BigDecimal.ZERO) <= 0) {
			if (intProvsnRule.contains("InsuranceAmount")) {
				vasFee = provisionDao.getVasFee(finReference);
			}
		}

		return vasFee;
	}

	@Autowired
	public void setProvisionDao(ProvisionDAO provisionDao) {
		this.provisionDao = provisionDao;
	}

	@Autowired
	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	@Autowired
	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}
}
