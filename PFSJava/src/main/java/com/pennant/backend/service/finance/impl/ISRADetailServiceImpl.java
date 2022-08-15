package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.dao.ISRADetailDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.isradetail.ISRADetail;
import com.pennant.backend.model.isradetail.ISRALiquidDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.ISRADetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>ISRA Details</b>.<br>
 * 
 */
public class ISRADetailServiceImpl extends GenericService<ISRADetail> implements ISRADetailService {
	private static Logger logger = LogManager.getLogger(ISRADetailServiceImpl.class);

	private ISRADetailDAO israDetailDAO;

	public ISRADetailServiceImpl() {
		super();
	}

	@Override
	public List<AuditDetail> saveOrUpdate(FinanceDetail fd, String type, String tranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		ISRADetail israDetails = fd.getIsraDetail();

		if (israDetails == null) {
			return auditDetails;
		}

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		if (!fm.isIsra()) {
			this.israDetailDAO.deleteIsraLiqDetails(israDetails.getId(), type);
			this.israDetailDAO.delete(fm.getFinReference(), type);

			return auditDetails;
		}

		ISRADetail israd = fd.getIsraDetail();

		israd.setRecordType(fm.getRecordType());
		israd.setWorkflowId(fm.getWorkflowId());
		israd.setVersion(fm.getVersion());
		israd.setRoleCode(fm.getRoleCode());
		israd.setNextRoleCode(fm.getNextRoleCode());
		israd.setTaskId(fm.getTaskId());
		israd.setNextTaskId(fm.getNextTaskId());
		israd.setRecordStatus(fm.getRecordStatus());
		israd.setLastMntBy(fm.getLastMntBy());
		israd.setLastMntOn(fm.getLastMntOn());

		if (israd.isNewRecord() || fm.isNewRecord()) {
			this.israDetailDAO.save(israd, type);
			auditDetails.addAll(proceesLiquidDetailsList(fd, type, israd.getId()));
		} else {
			this.israDetailDAO.update(israd, type);
			auditDetails.addAll(proceesLiquidDetailsList(fd, type, israd.getId()));
		}

		int i = 0;
		for (ISRALiquidDetail isld : israd.getIsraLiquidDetails()) {
			String[] fields = PennantJavaUtil.getFieldDetails(isld, "");
			auditDetails.add(new AuditDetail(tranType, ++i, fields[0], fields[1], isld.getBefImage(), isld));
		}

		String[] fields = PennantJavaUtil.getFieldDetails(israd, israd.getExcludeFields());
		auditDetails.add(new AuditDetail(tranType, 1, fields[0], fields[1], israd.getBefImage(), israd));

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Third Party Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @return
	 */
	private List<AuditDetail> proceesLiquidDetailsList(FinanceDetail fd, String type, long israDetailId) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		List<AuditDetail> auditDetails = fd.getAuditDetailMap().get("ISRALiquidDeatils");

		for (int i = 0; i < auditDetails.size(); i++) {
			ISRALiquidDetail istLiquidDetail = (ISRALiquidDetail) auditDetails.get(i).getModelData();
			istLiquidDetail.setIsraDetailId(israDetailId);
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				istLiquidDetail.setRoleCode("");
				istLiquidDetail.setNextRoleCode("");
				istLiquidDetail.setTaskId("");
				istLiquidDetail.setNextTaskId("");
			}

			istLiquidDetail.setWorkflowId(0);

			if (istLiquidDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (istLiquidDetail.isNewRecord()) {
				saveRecord = true;
				if (istLiquidDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					istLiquidDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (istLiquidDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					istLiquidDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (istLiquidDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					istLiquidDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (istLiquidDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (istLiquidDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (istLiquidDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (istLiquidDetail.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = istLiquidDetail.getRecordType();
				recordStatus = istLiquidDetail.getRecordStatus();
				istLiquidDetail.setRecordType("");
				istLiquidDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				israDetailDAO.save(istLiquidDetail, type);
			}

			if (updateRecord) {
				israDetailDAO.update(istLiquidDetail, type);
			}

			if (deleteRecord) {
				israDetailDAO.delete(istLiquidDetail, type);
			}

			if (approveRec) {
				istLiquidDetail.setRecordType(rcdType);
				istLiquidDetail.setRecordStatus(recordStatus);
			}

			auditDetails.get(i).setModelData(istLiquidDetail);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doApprove(FinanceDetail fd, String type, String tranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();

		ISRADetail israDetails = fd.getIsraDetail();
		if (israDetails == null) {
			return auditDetails;
		}

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		if (!fm.isIsra()) {
			this.israDetailDAO.deleteIsraLiqDetails(israDetails.getId(), type);
			this.israDetailDAO.delete(fm.getFinReference(), type);
			return auditDetails;
		}

		if (fm.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			this.israDetailDAO.delete(fm.getFinReference(), TableType.MAIN_TAB.getSuffix());
		} else {
			israDetails.setRoleCode("");
			israDetails.setNextRoleCode("");
			israDetails.setTaskId("");
			israDetails.setNextTaskId("");
			israDetails.setWorkflowId(0);
			israDetails.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			israDetails.setLastMntBy(fm.getLastMntBy());
			israDetails.setLastMntOn(fm.getLastMntOn());
			israDetails.setVersion(fm.getVersion());
			boolean isExist = israDetailDAO.isDetailExists(israDetails, type);

			if (!isExist) {
				israDetails.setRecordType("");
				this.israDetailDAO.save(israDetails, TableType.MAIN_TAB.getSuffix());
				auditDetails.addAll(proceesLiquidDetailsList(fd, type, israDetails.getId()));
			} else {
				israDetails.setRecordType("");
				this.israDetailDAO.update(israDetails, TableType.MAIN_TAB.getSuffix());
				auditDetails.addAll(proceesLiquidDetailsList(fd, type, israDetails.getId()));
			}
		}

		this.israDetailDAO.deleteIsraLiqDetails(israDetails.getId(), TableType.TEMP_TAB.getSuffix());
		this.israDetailDAO.delete(fm.getFinReference(), TableType.TEMP_TAB.getSuffix());

		String[] fields = PennantJavaUtil.getFieldDetails(new ISRADetail(), new ISRADetail().getExcludeFields());

		auditDetails.add(new AuditDetail(tranType, 1, fields[0], fields[1], israDetails.getBefImage(), israDetails));

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doReject(FinanceDetail fd, String type, String tranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		ISRADetail israDetails = fd.getIsraDetail();
		if (israDetails == null) {
			return auditDetails;
		}

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		if (!fm.isIsra()) {
			this.israDetailDAO.deleteIsraLiqDetails(israDetails.getId(), type);
			this.israDetailDAO.delete(fm.getFinReference(), type);
			return auditDetails;
		}

		this.israDetailDAO.deleteIsraLiqDetails(israDetails.getId(), type);
		this.israDetailDAO.delete(fm.getFinReference(), type);

		String[] fields = PennantJavaUtil.getFieldDetails(new ISRADetail(), new ISRADetail().getExcludeFields());

		auditDetails.add(new AuditDetail(tranType, 1, fields[0], fields[1], israDetails.getBefImage(), israDetails));

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public ISRADetail getIsraDetailsByRef(String finRef, String type) {
		ISRADetail israDetail = israDetailDAO.getISRADetailsByFinRef(finRef, type);

		if (israDetail != null) {
			israDetail.setIsraLiquidDetails(israDetailDAO.getISRALiqDetails(israDetail.getId(), type));
		}
		return israDetail;
	}

	@Override
	public List<AuditDetail> getISRALiquidDeatils(ISRADetail israDetail, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<ISRALiquidDetail> israLiquidDetails = israDetail.getIsraLiquidDetails();

		List<AuditDetail> auditDetails = new ArrayList<>();

		ISRALiquidDetail israLiquidDetail = new ISRALiquidDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(israLiquidDetail, israLiquidDetail.getExcludeFields());

		for (int i = 0; i < israLiquidDetails.size(); i++) {

			ISRALiquidDetail liquidDetail = israLiquidDetails.get(i);
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(liquidDetail.getRecordType()))) {
				continue;
			}
			boolean isRcdType = false;

			if (liquidDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				liquidDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (liquidDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				liquidDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (liquidDetail.isWorkflow()) {
					isRcdType = true;
				}
			} else if (liquidDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				liquidDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				liquidDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (liquidDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (liquidDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| liquidDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], liquidDetail.getBefImage(),
					liquidDetail));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public void setIsraDetailDAO(ISRADetailDAO israDetailDAO) {
		this.israDetailDAO = israDetailDAO;
	}

}
