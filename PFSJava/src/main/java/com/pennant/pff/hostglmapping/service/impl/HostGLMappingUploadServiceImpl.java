package com.pennant.pff.hostglmapping.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.TransactionStatus;

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
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.RequestSource;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.file.UploadTypes;
import com.pennapps.core.util.ObjectUtil;

public class HostGLMappingUploadServiceImpl extends AUploadServiceImpl<HostGLMappingUpload> {
	private static final Logger logger = LogManager.getLogger(HostGLMappingUploadServiceImpl.class);

	private HostGLMappingUploadDAO hostGLMappingUploadDAO;
	private AccountTypeDAO accountTypeDAO;
	private FinanceTypeDAO financeTypeDAO;
	private CostCenterDAO costCenterDAO;
	private ProfitCenterDAO profitCenterDAO;
	private AccountMappingService accountMappingService;

	public HostGLMappingUploadServiceImpl() {
		super();
	}

	@Override
	protected HostGLMappingUpload getDetail(Object object) {
		if (object instanceof HostGLMappingUpload detail) {
			return detail;
		}

		throw new AppException(IN_VALID_OBJECT);
	}

	@Override
	public void doValidate(FileUploadHeader header, Object object) {
		HostGLMappingUpload detail = getDetail(object);

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

		if (!accountTypeDAO.isExsistAccountType(acctype)) {
			setError(detail, HostGLMappingUploadError.HGL03);
			return;
		}

		if (StringUtils.isNotBlank(fintype) && financeTypeDAO.getFinTypeCount(fintype, "") <= 0) {
			setError(detail, HostGLMappingUploadError.HGL04);
			return;
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
			setFailureStatus(detail, "HGL_101", "System GL Code" + ":" + glcode + " " + "already exists.");
			return;
		}

		String hostAccount = detail.getHostGLCode();
		boolean isExistingHostAccount = accountMappingService.isExistingHostAccount(hostAccount);

		if (isExistingHostAccount) {
			setFailureStatus(detail, "HGL_102", "Host GL Code" + ":" + hostAccount + " " + "already exists.");
			return;
		}

		String hostAccountregex = PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_ALPHANUM);
		if (StringUtils.isNotBlank(hostAccount) && !Pattern.compile(hostAccountregex).matcher(hostAccount).matches()) {
			setError(detail, HostGLMappingUploadError.HGL11);
			return;
		}

		String ame = detail.getAllowManualEntries();
		if (StringUtils.isNotBlank(ame) && TransactionType.object(ame) == null) {
			setError(detail, HostGLMappingUploadError.HGL08);
			return;
		}

		String descregex = PennantRegularExpressions.getRegexMapper(PennantRegularExpressions.REGEX_DESCRIPTION);
		if (StringUtils.isNotBlank(detail.getGLDescription())
				&& !Pattern.compile(descregex).matcher(detail.getGLDescription()).matches()) {
			setError(detail, HostGLMappingUploadError.HGL09);
			return;
		}

		detail.setAccountTypeGroup(accountTypeDAO.getGroupCodeByAccType(acctype));

		if (detail.getOpenedDate().compareTo(header.getAppDate()) > 0) {
			setError(detail, HostGLMappingUploadError.HGL10);
			return;
		}

		setSuccesStatus(detail);
	}

	@Override
	public void doApprove(List<FileUploadHeader> headers) {
		new Thread(() -> {
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
						setFailureStatus(detail);
					} else {
						setSuccesStatus(detail);

						LoggedInUser userDetails = header.getUserDetails();

						if (userDetails == null) {
							userDetails = new LoggedInUser();
							userDetails.setLoginUsrID(header.getApprovedBy());
							userDetails.setUserName(header.getApprovedByName());
						}

						detail.setUserDetails(userDetails);
						detail.setCreatedOn(header.getCreatedOn());
						detail.setCreatedBy(header.getCreatedBy());
						process(detail);
					}

					if (detail.getProgress() == EodConstants.PROGRESS_FAILED) {
						failRecords++;
					} else {
						sucessRecords++;
					}

					header.getUploadDetails().add(detail);
				}

				try {
					header.setSuccessRecords(sucessRecords);
					header.setFailureRecords(failRecords);

					hostGLMappingUploadDAO.update(details);

					List<FileUploadHeader> headerList = new ArrayList<>();
					headerList.add(header);
					updateHeader(headers, true);

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		}).start();

	}

	private void process(HostGLMappingUpload detail) {
		AccountMapping ac = new AccountMapping();

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

		AuditHeader auditHeader = getAuditHeader(ac, PennantConstants.TRAN_WF);
		auditHeader.getAuditDetail().setModelData(ac);

		TransactionStatus txStatus = getTransactionStatus();
		try {
			auditHeader = accountMappingService.doApprove(auditHeader);
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

			if (txStatus != null) {
				transactionManager.rollback(txStatus);
			}

			setFailureStatus(detail, e.getMessage());
			return;
		}

		if (auditHeader == null) {
			setFailureStatus(detail, "Audit Header is null.");
			return;
		}

		if (auditHeader.getErrorMessage() != null) {
			setFailureStatus(detail, auditHeader.getErrorMessage().get(0));
		} else {
			setSuccesStatus(detail);
		}

	}

	private AuditHeader getAuditHeader(AccountMapping accountMapping, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, accountMapping.getBefImage(), accountMapping);
		return new AuditHeader(null, null, null, null, auditDetail, accountMapping.getUserDetails(), new HashMap<>());
	}

	@Override
	public void doReject(List<FileUploadHeader> headers) {
		List<Long> headerIdList = headers.stream().map(FileUploadHeader::getId).toList();

		TransactionStatus txStatus = getTransactionStatus();
		try {

			headers.forEach(h1 -> {
				h1.setRemarks(REJECT_DESC);
				h1.getUploadDetails().addAll(hostGLMappingUploadDAO.getDetails(h1.getId()));
			});

			hostGLMappingUploadDAO.update(headerIdList, REJECT_CODE, REJECT_DESC);

			updateHeader(headers, false);

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

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
	public void validate(DataEngineAttributes attributes, MapSqlParameterSource paramSource) throws Exception {
		logger.debug(Literal.ENTERING);

		HostGLMappingUpload detail = (HostGLMappingUpload) ObjectUtil.valueAsObject(paramSource,
				HostGLMappingUpload.class);

		Map<String, Object> parameterMap = attributes.getParameterMap();

		FileUploadHeader header = (FileUploadHeader) parameterMap.get("FILE_UPLOAD_HEADER");

		detail.setHeaderId(header.getId());
		detail.setAppDate(header.getAppDate());

		doValidate(header, detail);

		updateProcess(header, detail, paramSource);

		header.getUploadDetails().add(detail);

		logger.debug(Literal.LEAVING);
	}

	private void setError(HostGLMappingUpload detail, HostGLMappingUploadError error) {
		setFailureStatus(detail, error.name(), error.description());
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