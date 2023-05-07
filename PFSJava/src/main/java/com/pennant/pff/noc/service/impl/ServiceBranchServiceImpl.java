package com.pennant.pff.noc.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.pff.noc.dao.ServiceBranchDAO;
import com.pennant.pff.noc.model.ServiceBranch;
import com.pennant.pff.noc.model.ServiceBranchesLoanType;
import com.pennant.pff.noc.service.ServiceBranchService;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pff.core.TableType;

public class ServiceBranchServiceImpl extends GenericService<ServiceBranch> implements ServiceBranchService {
	private static Logger logger = LogManager.getLogger(ServiceBranchServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ServiceBranchDAO serviceBranchDAO;

	public ServiceBranchServiceImpl() {
		super();
	}

	@Override
	public ServiceBranch getServiceBranch(long id) {
		return serviceBranchDAO.getServiceBranch(id);
	}

	@Override
	public List<ServiceBranch> getServiceBranches(List<String> roleCodes) {
		return serviceBranchDAO.getServiceBranches(roleCodes);
	}

	@Override
	public List<ReportListDetail> getPrintServices(List<String> roleCodes) {
		return serviceBranchDAO.getPrintServiceBranches(roleCodes);
	}

	@Override
	public List<ServiceBranch> getResult(ISearch searchFilters) {
		return serviceBranchDAO.getResult(searchFilters);
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, PennantConstants.RECORD_TYPE_DEL);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		ServiceBranch code = (ServiceBranch) auditHeader.getAuditDetail().getModelData();
		serviceBranchDAO.delete(code, TableType.MAIN_TAB);
		auditHeader.setAuditDetails(
				getListAuditDetails(listDeletion(code, TableType.MAIN_TAB, auditHeader.getAuditTranType())));
		auditHeaderDAO.addAudit(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader ah) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		ah = businessValidation(ah, PennantConstants.method_saveOrUpdate);

		if (!ah.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return ah;
		}

		ServiceBranch sb = (ServiceBranch) ah.getAuditDetail().getModelData();

		TableType type = TableType.MAIN_TAB;
		if (sb.isWorkflow()) {
			type = TableType.TEMP_TAB;
		}
		if (sb.isNewRecord()) {
			this.serviceBranchDAO.save(sb, type);
			ah.getAuditDetail().setModelData(sb);
		} else {
			this.serviceBranchDAO.update(sb, type);
		}

		List<ServiceBranchesLoanType> list = sb.getServiceBranchLoanTypeList();
		if (CollectionUtils.isNotEmpty(list)) {
			List<AuditDetail> details = sb.getLovDescAuditDetailMap().get("CustomerServiceLoanTypeMapping");
			details = processLoanTypeList(details, type, sb.getId());
			auditDetails.addAll(details);
		}

		ah.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(ah);

		logger.debug(Literal.LEAVING);
		return ah;
	}

	private List<AuditDetail> processLoanTypeList(List<AuditDetail> list, TableType type, long id) {
		for (AuditDetail ad : list) {
			ServiceBranchesLoanType sblt = (ServiceBranchesLoanType) ad.getModelData();

			boolean saveRecord = false;
			boolean updateRecord = false;
			boolean deleteRecord = false;
			boolean approveRec = false;

			String rcdType = "";
			String recordStatus = "";

			sblt.setHeaderId(id);

			if (StringUtils.isEmpty(type.getSuffix())) {
				approveRec = true;
				sblt.setVersion(sblt.getVersion() + 1);
				sblt.setRoleCode("");
				sblt.setNextRoleCode("");
				sblt.setTaskId("");
				sblt.setNextTaskId("");
			}

			sblt.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(sblt.getRecordType())) {
				deleteRecord = true;
			} else if (sblt.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(sblt.getRecordType())) {
					sblt.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(sblt.getRecordType())) {
					sblt.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(sblt.getRecordType())) {
					sblt.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(sblt.getRecordType())) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(sblt.getRecordType())) {
				updateRecord = true;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(sblt.getRecordType())) {
				if (approveRec) {
					deleteRecord = true;
				} else if (sblt.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = sblt.getRecordType();
				recordStatus = sblt.getRecordStatus();
				sblt.setRecordType("");
				sblt.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

			}
			if (saveRecord) {
				serviceBranchDAO.saveLoanType(sblt, type);
			}

			if (updateRecord) {
				serviceBranchDAO.updateLoanType(sblt, type);
			}

			if (deleteRecord) {
				serviceBranchDAO.deleteBranchLoanTypeById(sblt.getId(), type);
			}

			if (approveRec) {
				sblt.setRecordType(rcdType);
				sblt.setRecordStatus(recordStatus);
			}
			ad.setModelData(sblt);
		}

		logger.debug(Literal.LEAVING);
		return list;
	}

	@Override
	public AuditHeader doApprove(AuditHeader ah) {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<>();
		ah = businessValidation(ah, PennantConstants.method_doApprove);

		if (!ah.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return ah;
		}

		ServiceBranch csb = new ServiceBranch();
		BeanUtils.copyProperties(ah.getAuditDetail().getModelData(), csb);

		if (PennantConstants.RECORD_TYPE_DEL.equals(csb.getRecordType())) {
			tranType = PennantConstants.TRAN_DEL;
			List<AuditDetail> listDeletion = listDeletion(csb, TableType.MAIN_TAB, ah.getAuditTranType());
			serviceBranchDAO.delete(csb, TableType.MAIN_TAB);
			auditDetails.addAll(listDeletion);
		} else {
			csb.setRoleCode("");
			csb.setNextRoleCode("");
			csb.setTaskId("");
			csb.setNextTaskId("");
			csb.setWorkflowId(0);

			if (PennantConstants.RECORD_TYPE_NEW.equals(csb.getRecordType())) {
				tranType = PennantConstants.TRAN_ADD;
				csb.setRecordType("");
				this.serviceBranchDAO.save(csb, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				csb.setRecordType("");
				this.serviceBranchDAO.update(csb, TableType.MAIN_TAB);
			}
		}

		List<ServiceBranchesLoanType> list = csb.getServiceBranchLoanTypeList();
		if (CollectionUtils.isNotEmpty(list)) {
			List<AuditDetail> details = csb.getLovDescAuditDetailMap().get("CustomerServiceLoanTypeMapping");
			details = processLoanTypeList(details, TableType.MAIN_TAB, csb.getId());
			auditDetails.addAll(details);
		}

		ah.setAuditDetails(getListAuditDetails(listDeletion(csb, TableType.TEMP_TAB, ah.getAuditTranType())));
		ah.setAuditTranType(tranType);
		ah.getAuditDetail().setAuditTranType(tranType);
		ah.getAuditDetail().setModelData(csb);
		auditHeaderDAO.addAudit(ah);

		this.serviceBranchDAO.delete(csb, TableType.TEMP_TAB);
		ah.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(ah);

		logger.debug(Literal.LEAVING);
		return ah;
	}

	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<>();

		for (AuditDetail ad : list) {
			String transType = "";
			String rcdType = "";

			ServiceBranchesLoanType sblt = (ServiceBranchesLoanType) ad.getModelData();
			rcdType = sblt.getRecordType();

			if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_ADD;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(rcdType)
					|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(rcdType)) {
				transType = PennantConstants.TRAN_DEL;
			} else {
				transType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isNotEmpty(transType)) {
				auditDetails.add(new AuditDetail(transType, ad.getAuditSeq(), sblt.getBefImage(), sblt));
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private List<AuditDetail> listDeletion(ServiceBranch csb, TableType tempTab, String type) {
		List<AuditDetail> auditList = new ArrayList<>();

		if (CollectionUtils.isEmpty(csb.getServiceBranchLoanTypeList())) {
			return auditList;
		}

		for (int i = 0; i < csb.getServiceBranchLoanTypeList().size(); i++) {
			ServiceBranchesLoanType csbt = csb.getServiceBranchLoanTypeList().get(i);
			if (!StringUtils.isEmpty(csbt.getRecordType()) || StringUtils.isEmpty(tempTab.getSuffix())) {
				auditList.add(new AuditDetail(type, i + 1, csbt.getBefImage(), csbt));
			}
		}

		serviceBranchDAO.delete(csb.getServiceBranchLoanTypeList().get(0), tempTab);

		return auditList;
	}

	@Override
	public AuditHeader doReject(AuditHeader ah) {
		logger.debug(Literal.ENTERING);

		ah = businessValidation(ah, PennantConstants.method_doApprove);
		if (!ah.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return ah;
		}

		ServiceBranch code = (ServiceBranch) ah.getAuditDetail().getModelData();

		ah.setAuditTranType(PennantConstants.TRAN_WF);
		ah.setAuditDetails(getListAuditDetails(listDeletion(code, TableType.TEMP_TAB, ah.getAuditTranType())));
		serviceBranchDAO.delete(code, TableType.TEMP_TAB);

		auditHeaderDAO.addAudit(ah);
		logger.debug(Literal.LEAVING);
		return ah;
	}

	private AuditHeader businessValidation(AuditHeader ah, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail ad = validation(ah.getAuditDetail(), ah.getUsrLanguage());
		ah.setAuditDetail(ad);
		ah.setErrorList(ad.getErrorDetails());
		ah = getAuditDetails(ah, method);
		ah = nextProcess(ah);

		logger.debug(Literal.LEAVING);
		return ah;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		List<AuditDetail> auditDetails = new ArrayList<>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();
		ServiceBranch csb = (ServiceBranch) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ((PennantConstants.method_saveOrUpdate.equals(method) || PennantConstants.method_doApprove.equals(method)
				|| PennantConstants.method_doReject.equals(method)) && csb.isWorkflow()) {
			auditTranType = PennantConstants.TRAN_WF;
		}

		if (CollectionUtils.isEmpty(csb.getServiceBranchLoanTypeList())) {
			return auditHeader;
		}

		auditDetailMap.put("CustomerServiceLoanTypeMapping", setLoanTypeAuditData(csb, auditTranType, method));
		auditDetails.addAll(auditDetailMap.get("CustomerServiceLoanTypeMapping"));

		csb.setLovDescAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(csb);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private List<AuditDetail> setLoanTypeAuditData(ServiceBranch csb, String auditTranType, String method) {
		List<AuditDetail> auditDetails = new ArrayList<>();

		for (int i = 0; i < csb.getServiceBranchLoanTypeList().size(); i++) {
			ServiceBranchesLoanType sblt = csb.getServiceBranchLoanTypeList().get(i);

			if (StringUtils.isEmpty(sblt.getRecordType())) {
				continue;
			}

			sblt.setWorkflowId(csb.getWorkflowId());
			sblt.setHeaderId(csb.getId());

			boolean isRcdType = false;

			if (PennantConstants.RCD_ADD.equalsIgnoreCase(sblt.getRecordType())) {
				sblt.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(sblt.getRecordType())) {
				sblt.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (csb.isWorkflow()) {
					isRcdType = true;
				}
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(sblt.getRecordType())) {
				sblt.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if (PennantConstants.method_saveOrUpdate.equals(method) && isRcdType) {
				sblt.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(sblt.getRecordType())) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(sblt.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(sblt.getRecordType())) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			sblt.setRecordStatus(csb.getRecordStatus());
			sblt.setUserDetails(csb.getUserDetails());
			sblt.setLastMntOn(csb.getLastMntOn());
			sblt.setLastMntBy(csb.getLastMntBy());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, sblt.getBefImage(), sblt));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	private AuditDetail validation(AuditDetail ah, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		ServiceBranch csb = (ServiceBranch) ah.getModelData();

		String code = csb.getCode();
		String description = csb.getDescription();

		if (csb.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(csb.getRecordType()) && serviceBranchDAO
				.isDuplicateKey(csb.getCode(), csb.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] valueParm = new String[2];
			valueParm[0] = PennantJavaUtil.getLabel("label_ServiceBranchDialog_Description.value").concat(": ")
					.concat(description);
			valueParm[1] = PennantJavaUtil.getLabel("label_ServiceBranchDialog_Code.value").concat(": ").concat(code);
			ah.setErrorDetail(new ErrorDetail("41018", valueParm));
		}

		List<ServiceBranchesLoanType> list = csb.getServiceBranchLoanTypeList();

		Set<String> uniq = new HashSet<>();
		list.stream().filter(e -> uniq.add(e.getFinType().concat(e.getBranch()))).collect(Collectors.toList());

		if (uniq.size() != list.size()) {
			String[] valueParm = new String[2];
			valueParm[0] = PennantJavaUtil.getLabel("label_ServiceBranchDialogLoanType.value");
			valueParm[1] = PennantJavaUtil.getLabel("label_ServiceBranchDialogBranch.value");

			ah.setErrorDetail(new ErrorDetail("90273", valueParm));
		}

		ah.setErrorDetails(ErrorUtil.getErrorDetails(ah.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return ah;
	}

	@Autowired
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	@Autowired
	public void setServiceBranchDAO(ServiceBranchDAO serviceBranchDAO) {
		this.serviceBranchDAO = serviceBranchDAO;
	}
}
