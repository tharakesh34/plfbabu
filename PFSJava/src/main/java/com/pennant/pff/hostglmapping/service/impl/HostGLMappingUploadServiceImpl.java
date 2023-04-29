package com.pennant.pff.hostglmapping.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.CostCenterDAO;
import com.pennant.backend.dao.applicationmaster.ProfitCenterDAO;
import com.pennant.backend.dao.rmtmasters.AccountTypeDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.hostglmapping.upload.HostGLMappingUpload;
import com.pennant.backend.service.applicationmaster.AccountMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.eod.constants.EodConstants;
import com.pennant.pff.accounting.HostAccountStatus;
import com.pennant.pff.accounting.TransactionType;
import com.pennant.pff.hostglmapping.dao.HostGLMappingUploadDAO;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennant.pff.upload.service.impl.AUploadServiceImpl;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class HostGLMappingUploadServiceImpl extends AUploadServiceImpl {
	private static final Logger logger = LogManager.getLogger(HostGLMappingUploadServiceImpl.class);

	private HostGLMappingUploadDAO hostGLMappingUploadDAO;
	private AccountTypeDAO accountTypeDAO;
	private FinanceTypeDAO financeTypeDAO;
	private CostCenterDAO costCenterDAO;
	private ProfitCenterDAO profitCenterDAO;
	private AccountMappingService accountMappingService;

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		HostGLMappingUpload detail = null;

		if (object instanceof HostGLMappingUpload) {
			detail = (HostGLMappingUpload) object;
		}

		if (detail == null) {
			throw new AppException("Invalid Data transferred...");
		}

		String acctype = detail.getAccountType();
		String fintype = detail.getLoanType();

		if (StringUtils.isBlank(acctype)) {
			setError(detail, HostGLMappingUploadError.HGL01);
			return;
		}

		if (StringUtils.isBlank(detail.getHostGLCode())) {
			setError(detail, HostGLMappingUploadError.HGL02);
			return;
		}

		boolean isExsistAccountType = accountTypeDAO.isExsistAccountType(acctype);

		if (!isExsistAccountType) {
			setError(detail, HostGLMappingUploadError.HGL03);
			return;
		}

		if (StringUtils.isNotBlank(fintype)) {
			int FinTypeCount = financeTypeDAO.getFinTypeCount(fintype, "");

			if (FinTypeCount <= 0) {
				setError(detail, HostGLMappingUploadError.HGL04);
				return;
			}
		}

		String costcentre = detail.getCostCentreCode();

		if (StringUtils.isNotBlank(costcentre)) {
			Long cc = costCenterDAO.getCostCenterIDByCode(costcentre);
			if (cc == null) {
				setError(detail, HostGLMappingUploadError.HGL05);
				return;
			}

			detail.setCostCenterID(cc);
		}

		String profitcentre = detail.getProfitCentreCode();

		if (StringUtils.isNotBlank(profitcentre)) {
			Long pc = profitCenterDAO.getPftCenterIDByCode(profitcentre);
			if (pc == null) {
				setError(detail, HostGLMappingUploadError.HGL06);
				return;
			}

			detail.setProfitCenterID(pc);
		}

		String glcode = acctype;

		if (StringUtils.isNotBlank(fintype)) {
			glcode = fintype.concat(glcode);
		}

		detail.setGLCode(glcode);

		if (hostGLMappingUploadDAO.isDuplicateKey(glcode, TableType.BOTH_TAB)) {
			detail.setProgress(EodConstants.PROGRESS_FAILED);
			detail.setErrorCode("HGL_999");
			detail.setErrorDesc("System GL Code" + ":" + glcode + " " + "already exists.");
			return;
		}

		String ame = detail.getAllowManualEntries();
		if (StringUtils.isNotBlank(ame) && TransactionType.object(ame) == null) {
			setError(detail, HostGLMappingUploadError.HGL08);
			return;
		}

		String descregex = PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_ALPHANUM);
		if (StringUtils.isNotBlank(detail.getGLDescription())
				&& !Pattern.compile(descregex).matcher(detail.getGLDescription()).matches()) {
			setError(detail, HostGLMappingUploadError.HGL09);
			return;
		}

		String groupCode = accountTypeDAO.getGroupCodeByAccType(acctype);

		detail.setAccountTypeGroup(groupCode);

		if (detail.getOpenedDate().compareTo(header.getAppDate()) > 0) {
			setError(detail, HostGLMappingUploadError.HGL10);
			return;
		}
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
					TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus txStatus = null;

			Date appDate = SysParamUtil.getAppDate();

			for (FileUploadHeader header : headers) {
				logger.info("Processing the File {}", header.getFileName());

				List<HostGLMappingUpload> details = hostGLMappingUploadDAO.getDetails(header.getId());

				header.setAppDate(appDate);
				header.setTotalRecords(details.size());
				int sucessRecords = 0;
				int failRecords = 0;

				for (HostGLMappingUpload detail : details) {
					doValidate(header, detail);

					if (detail.getErrorCode() != null) {
						detail.setProgress(EodConstants.PROGRESS_FAILED);
					} else {
						detail.setProgress(EodConstants.PROGRESS_SUCCESS);
						detail.setErrorCode("");
						detail.setErrorDesc("");
						detail.setUserDetails(header.getUserDetails());
						detail.setCreatedOn(header.getCreatedOn());
						detail.setCreatedBy(header.getCreatedBy());
						setAccountMapping(detail);
					}

					if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}
				}

				try {
					header.setSuccessRecords(sucessRecords);
					header.setFailureRecords(failRecords);

					StringBuilder remarks = new StringBuilder("Process Completed");

					if (failRecords > 0) {
						remarks.append(" with exceptions, ");
					}

					remarks.append(" Total Records : ").append(header.getTotalRecords());
					remarks.append(" Success Records : ").append(sucessRecords);
					remarks.append(" Failed Records : ").append(failRecords);

					logger.info("Processed the File {}", header.getFileName());

					txStatus = transactionManager.getTransaction(txDef);

					hostGLMappingUploadDAO.update(details);

					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);
					updateHeader(headers, true);

					transactionManager.commit(txStatus);
				} catch (Exception e) {
					logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

					if (txStatus != null) {
						transactionManager.rollback(txStatus);
					}
				} finally {
					txStatus = null;
				}
			}

		}).start();

	}

	private void setAccountMapping(HostGLMappingUpload detail) {
		AccountMapping ac = new AccountMapping();
		AuditHeader auditHeader = null;

		ac.setAccountType(detail.getAccountType());
		ac.setFinType(detail.getLoanType());
		ac.setAccount(detail.getGLCode());
		ac.setAllowedManualEntry(detail.getAllowManualEntries());
		ac.setHostAccount(detail.getHostGLCode());
		ac.setStatus(HostAccountStatus.OPEN.code());
		ac.setOpenedDate(detail.getOpenedDate());
		ac.setClosedDate(null);
		ac.setProfitCenterID(detail.getProfitCenterID());
		ac.setCostCenterID(detail.getCostCenterID());
		ac.setGLDescription(detail.getGLDescription());
		ac.setAccountTypeGroup(detail.getAccountTypeGroup());
		ac.setRequestSource(RequestSource.UPLOAD);
		ac.setUserDetails(detail.getUserDetails());
		ac.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		ac.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		ac.setVersion(ac.getVersion() + 1);
		ac.setCreatedOn(detail.getCreatedOn());
		ac.setCreatedBy(detail.getCreatedBy());

		auditHeader = getAuditHeader(ac, PennantConstants.TRAN_WF);
		auditHeader.getAuditDetail().setModelData(ac);

		AuditHeader ah = accountMappingService.doApprove(auditHeader);

		if (ah.getErrorMessage() != null) {
			detail.setProgress(EodConstants.PROGRESS_FAILED);
			detail.setErrorDesc(ah.getErrorMessage().get(0).getMessage().toString());
			detail.setErrorCode(ah.getErrorMessage().get(0).getCode().toString());
		} else {
			detail.setProgress(EodConstants.PROGRESS_SUCCESS);
			detail.setErrorCode("");
			detail.setErrorDesc("");
		}

	}

	private AuditHeader getAuditHeader(AccountMapping accountMapping, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, accountMapping.getBefImage(), accountMapping);
		return new AuditHeader(null, null, null, null, auditDetail, accountMapping.getUserDetails(), new HashMap<>());
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).collect(Collectors.toList());

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;
		try {
			txStatus = transactionManager.getTransaction(txDef);

			hostGLMappingUploadDAO.update(headerIdList, ERR_CODE, ERR_DESC, EodConstants.PROGRESS_FAILED);

			headers.forEach(h1 -> h1.setRemarks(ERR_DESC));
			updateHeader(headers, false);

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(ERROR_LOG, e.getCause(), e.getMessage(), e.getLocalizedMessage(), e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}
		}
	}

	@Override
	public String getSqlQuery() {
		return hostGLMappingUploadDAO.getSqlQuery();
	}

	@Override
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource record) throws Exception {
		logger.debug(Literal.ENTERING);

		HostGLMappingUpload details = (HostGLMappingUpload) ObjectUtil.valueAsObject(record, HostGLMappingUpload.class);

		details.setReference(ObjectUtil.valueAsString(record.getValue("finReference")));

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		details.setHeaderId(header.getId());
		details.setAppDate(header.getAppDate());

		doValidate(header, details);

		updateProcess(header, details, record);

		logger.debug(Literal.LEAVING);
	}

	private void setError(HostGLMappingUpload detail, HostGLMappingUploadError error) {
		detail.setProgress(EodConstants.PROGRESS_FAILED);
		detail.setErrorCode(error.name());
		detail.setErrorDesc(error.description());
	}

	@Override
	public void uploadProcess() {
		uploadProcess(UploadTypes.HOST_GL.name(), this, "HostGLMappingUploadHeader");
	}

	@Autowired
	public void setHostGLMappingUploadDAO(HostGLMappingUploadDAO hostGLMappingUploadDAO) {
		this.hostGLMappingUploadDAO = hostGLMappingUploadDAO;
	}

	@Autowired
	public void setAccountTypeDAO(AccountTypeDAO accountTypeDAO) {
		this.accountTypeDAO = accountTypeDAO;
	}

	@Autowired
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	@Autowired
	public void setCostCenterDAO(CostCenterDAO costCenterDAO) {
		this.costCenterDAO = costCenterDAO;
	}

	@Autowired
	public void setProfitCenterDAO(ProfitCenterDAO profitCenterDAO) {
		this.profitCenterDAO = profitCenterDAO;
	}

	@Autowired
	public void setAccountMappingService(AccountMappingService accountMappingService) {
		this.accountMappingService = accountMappingService;
	}

}