package com.pennanttech.pff.incomeexpensedetail.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.incomeexpensedetail.dao.IncomeExpenseDetailDAO;
import com.pennanttech.pff.incomeexpensedetail.dao.IncomeExpenseHeaderDAO;
import com.pennanttech.pff.organization.IncomeExpenseType;
import com.pennanttech.pff.organization.model.IncomeExpenseDetail;
import com.pennanttech.pff.organization.model.IncomeExpenseHeader;

public class IncomeExpenseDetailServiceImpl extends GenericService<IncomeExpenseDetail>
		implements IncomeExpenseDetailService {
	private static final Logger logger = LogManager.getLogger(IncomeExpenseDetailServiceImpl.class);

	@Autowired
	protected IncomeExpenseHeaderDAO incomeExpenseHeaderDAO;
	@Autowired
	protected IncomeExpenseDetailDAO incomeExpenseDetailDAO;
	@Autowired
	private AuditHeaderDAO auditHeaderDAO;

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		IncomeExpenseHeader incomeExpenseHeader = (IncomeExpenseHeader) auditHeader.getAuditDetail().getModelData();

		List<AuditDetail> auditDetails = new ArrayList<>();
		TableType tableType = TableType.MAIN_TAB;
		if (incomeExpenseHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (incomeExpenseHeader.isNewRecord()) {
			incomeExpenseHeader.setId(incomeExpenseHeaderDAO.save(incomeExpenseHeader, tableType));
			auditHeader.getAuditDetail().setModelData(incomeExpenseHeader);
			auditHeader.setAuditReference(String.valueOf(incomeExpenseHeader.getId()));
		} else {
			incomeExpenseHeaderDAO.update(incomeExpenseHeader, tableType);
		}

		// School Core_Income Details
		if (CollectionUtils.isNotEmpty(incomeExpenseHeader.getCoreIncomeList())) {
			List<AuditDetail> details = incomeExpenseHeader.getAuditDetailMap().get("CoreIncome");
			details = processingCoreIncomeDetails(details, incomeExpenseHeader, tableType.getSuffix());
			auditDetails.addAll(details);
		}

		// School NonCore_Income Details
		if (CollectionUtils.isNotEmpty(incomeExpenseHeader.getNonCoreIncomeList())) {
			List<AuditDetail> details = incomeExpenseHeader.getAuditDetailMap().get("NonCoreIncome");
			details = processingCoreIncomeDetails(details, incomeExpenseHeader, tableType.getSuffix());
			auditDetails.addAll(details);
		}

		// School Expense Details
		if (CollectionUtils.isNotEmpty(incomeExpenseHeader.getExpenseList())) {
			List<AuditDetail> details = incomeExpenseHeader.getAuditDetailMap().get("Expense");
			details = processingCoreIncomeDetails(details, incomeExpenseHeader, tableType.getSuffix());
			auditDetails.addAll(details);
		}

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	private List<AuditDetail> processingCoreIncomeDetails(List<AuditDetail> auditDetails,
			IncomeExpenseHeader incomeExpenseHeader, String type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			IncomeExpenseDetail coreIncomeDetail = (IncomeExpenseDetail) auditDetails.get(i).getModelData();
			coreIncomeDetail.setHeaderId(incomeExpenseHeader.getId());
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;

			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				coreIncomeDetail.setRoleCode("");
				coreIncomeDetail.setNextRoleCode("");
				coreIncomeDetail.setTaskId("");
				coreIncomeDetail.setNextTaskId("");
			}

			if (coreIncomeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (coreIncomeDetail.isNewRecord()) {
				saveRecord = true;
				if (coreIncomeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					coreIncomeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (coreIncomeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					coreIncomeDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (coreIncomeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					coreIncomeDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (coreIncomeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (coreIncomeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (coreIncomeDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (coreIncomeDetail.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}

			if (approveRec) {
				rcdType = coreIncomeDetail.getRecordType();
				recordStatus = coreIncomeDetail.getRecordStatus();
				coreIncomeDetail.setRecordType("");
				coreIncomeDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			if (saveRecord) {
				incomeExpenseDetailDAO.save(coreIncomeDetail, type);
			}

			if (updateRecord) {
				incomeExpenseDetailDAO.update(coreIncomeDetail, type);
			}

			if (deleteRecord) {
				incomeExpenseDetailDAO.delete(coreIncomeDetail.getId(), type);
			}

			if (approveRec) {
				coreIncomeDetail.setRecordType(rcdType);
				coreIncomeDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(coreIncomeDetail);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;

	}

	@Override
	public IncomeExpenseHeader getIncomeExpense(long id, String type) {
		logger.info(Literal.ENTERING);
		IncomeExpenseHeader incomeExpenseHeader = incomeExpenseHeaderDAO.getIncomeExpenseHeader(id, type);
		if (incomeExpenseHeader != null) {
			incomeExpenseHeader.setCoreIncomeList(
					incomeExpenseDetailDAO.getIncomeExpenseList(id, IncomeExpenseType.CORE_INCOME.name(), type));
			incomeExpenseHeader.setNonCoreIncomeList(
					incomeExpenseDetailDAO.getIncomeExpenseList(id, IncomeExpenseType.NON_CORE_INCOME.name(), type));
			incomeExpenseHeader.setExpenseList(
					incomeExpenseDetailDAO.getIncomeExpenseList(id, IncomeExpenseType.EXPENSE.name(), type));
		}
		logger.info(Literal.LEAVING);
		return incomeExpenseHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		getAuditDetails(auditHeader, method);

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<>();

		IncomeExpenseHeader incomeExpenseHeader = (IncomeExpenseHeader) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (incomeExpenseHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Core Income Details
		int sequence = 0;
		if (incomeExpenseHeader.getCoreIncomeList() != null && incomeExpenseHeader.getCoreIncomeList().size() > 0) {
			auditDetailMap.put("CoreIncome", setCoreIncomeAuditData(incomeExpenseHeader,
					incomeExpenseHeader.getCoreIncomeList(), sequence, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CoreIncome"));
		}
		// NonCore Income Details
		sequence = incomeExpenseHeader.getCoreIncomeList().size();
		if (incomeExpenseHeader.getNonCoreIncomeList() != null
				&& incomeExpenseHeader.getNonCoreIncomeList().size() > 0) {
			auditDetailMap.put("NonCoreIncome", setCoreIncomeAuditData(incomeExpenseHeader,
					incomeExpenseHeader.getNonCoreIncomeList(), sequence, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("NonCoreIncome"));
		}

		// Expense Details
		sequence = incomeExpenseHeader.getCoreIncomeList().size() + incomeExpenseHeader.getNonCoreIncomeList().size();
		if (incomeExpenseHeader.getExpenseList() != null && incomeExpenseHeader.getExpenseList().size() > 0) {
			auditDetailMap.put("Expense", setCoreIncomeAuditData(incomeExpenseHeader,
					incomeExpenseHeader.getExpenseList(), sequence, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("Expense"));
		}

		incomeExpenseHeader.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(incomeExpenseHeader);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug("Leaving");
		return auditHeader;
	}

	private List<AuditDetail> setCoreIncomeAuditData(IncomeExpenseHeader incomeExpenseHeader,
			List<IncomeExpenseDetail> incomeExpenseDetails, int sequence, String auditTranType, String method) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		IncomeExpenseDetail incomeExpenseDetail = new IncomeExpenseDetail();

		String[] fields = PennantJavaUtil.getFieldDetails(incomeExpenseDetail, incomeExpenseDetail.getExcludeFields());

		for (int i = 0; i < incomeExpenseDetails.size(); i++) {
			IncomeExpenseDetail coreIncome = incomeExpenseDetails.get(i);
			coreIncome.setWorkflowId(incomeExpenseHeader.getWorkflowId());

			if (StringUtils.isEmpty(coreIncome.getRecordType())) {
				continue;
			}

			boolean isRcdType = false;

			if (coreIncome.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				coreIncome.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (coreIncome.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				coreIncome.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (incomeExpenseHeader.isWorkflow()) {
					isRcdType = true;
				}
			} else if (coreIncome.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				coreIncome.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				coreIncome.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (coreIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (coreIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| coreIncome.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			coreIncome.setRecordStatus(incomeExpenseHeader.getRecordStatus());
			coreIncome.setLoginDetails(incomeExpenseHeader.getUserDetails());
			coreIncome.setLastMntOn(incomeExpenseHeader.getLastMntOn());

			if (StringUtils.isNotEmpty(coreIncome.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, ++sequence, fields[0], fields[1],
						coreIncome.getBefImage(), coreIncome));
			}
		}

		return auditDetails;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		IncomeExpenseHeader incomeExpenseHeader = (IncomeExpenseHeader) auditHeader.getAuditDetail().getModelData();
		auditDetails.addAll(getListAuditDetails(deleteChilds(incomeExpenseHeader, "", auditHeader.getAuditTranType())));

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<>();
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		IncomeExpenseHeader incomeExpenseHeader = new IncomeExpenseHeader();
		BeanUtils.copyProperties((IncomeExpenseHeader) auditHeader.getAuditDetail().getModelData(),
				incomeExpenseHeader);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(incomeExpenseHeader.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(incomeExpenseHeaderDAO.getIncomeExpenseHeader(incomeExpenseHeader.getId(), ""));
		}
		if (incomeExpenseHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			auditDetails.addAll(deleteChilds(incomeExpenseHeader, "", tranType));
			incomeExpenseHeaderDAO.delete(incomeExpenseHeader, TableType.MAIN_TAB);
		} else {
			incomeExpenseHeader.setRoleCode("");
			incomeExpenseHeader.setNextRoleCode("");
			incomeExpenseHeader.setTaskId("");
			incomeExpenseHeader.setNextTaskId("");
			incomeExpenseHeader.setWorkflowId(0);

			if (incomeExpenseHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				incomeExpenseHeader.setRecordType("");
				incomeExpenseHeaderDAO.save(incomeExpenseHeader, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				incomeExpenseHeader.setRecordType("");
				incomeExpenseHeaderDAO.update(incomeExpenseHeader, TableType.MAIN_TAB);
			}

			// Core Income Details
			if (CollectionUtils.isNotEmpty(incomeExpenseHeader.getCoreIncomeList())) {
				List<AuditDetail> details = incomeExpenseHeader.getAuditDetailMap().get("CoreIncome");
				details = processingCoreIncomeDetails(details, incomeExpenseHeader, "");
				auditDetails.addAll(details);
			}

			// Non Core Income Details
			if (CollectionUtils.isNotEmpty(incomeExpenseHeader.getNonCoreIncomeList())) {
				List<AuditDetail> details = incomeExpenseHeader.getAuditDetailMap().get("NonCoreIncome");
				details = processingCoreIncomeDetails(details, incomeExpenseHeader, "");
				auditDetails.addAll(details);
			}

			// Expense Details
			if (CollectionUtils.isNotEmpty(incomeExpenseHeader.getExpenseList())) {
				List<AuditDetail> details = incomeExpenseHeader.getAuditDetailMap().get("Expense");
				details = processingCoreIncomeDetails(details, incomeExpenseHeader, "");
				auditDetails.addAll(details);
			}

		}

		List<AuditDetail> auditDetailList = new ArrayList<>();
		auditDetailList.addAll(deleteChilds(incomeExpenseHeader, "_Temp", auditHeader.getAuditTranType()));
		incomeExpenseHeaderDAO.delete(incomeExpenseHeader, TableType.TEMP_TAB);

		String[] fields = PennantJavaUtil.getFieldDetails(new IncomeExpenseHeader(),
				incomeExpenseHeader.getExcludeFields());
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				incomeExpenseHeader.getBefImage(), incomeExpenseHeader));
		auditHeader.setAuditDetails(auditDetailList);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(incomeExpenseHeader);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				incomeExpenseHeader.getBefImage(), incomeExpenseHeader));
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		List<AuditDetail> auditDetails = new ArrayList<>();
		IncomeExpenseHeader incomeExpenseHeader = (IncomeExpenseHeader) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		String[] fields = PennantJavaUtil.getFieldDetails(new IncomeExpenseHeader(),
				incomeExpenseHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				incomeExpenseHeader.getBefImage(), incomeExpenseHeader));

		auditDetails.addAll(
				getListAuditDetails(deleteChilds(incomeExpenseHeader, "_Temp", auditHeader.getAuditTranType())));
		incomeExpenseHeaderDAO.delete(incomeExpenseHeader, TableType.TEMP_TAB);

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public IncomeExpenseHeader getApprovedIncomeExpense(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<AuditDetail> deleteChilds(IncomeExpenseHeader incomeExpenseHeader, String tableType,
			String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<>();

		// Delete School Core_Income Details.
		List<AuditDetail> coreIncomeDetails = incomeExpenseHeader.getAuditDetailMap().get("CoreIncome");
		int sequence = 0;
		if (coreIncomeDetails != null && !coreIncomeDetails.isEmpty()) {
			IncomeExpenseDetail coreIncome = new IncomeExpenseDetail();
			String[] fields = PennantJavaUtil.getFieldDetails(coreIncome, coreIncome.getExcludeFields());
			for (int i = 0; i < coreIncomeDetails.size(); i++) {
				coreIncome = (IncomeExpenseDetail) coreIncomeDetails.get(i).getModelData();
				auditList.add(new AuditDetail(auditTranType, ++sequence, fields[0], fields[1], coreIncome.getBefImage(),
						coreIncome));
				incomeExpenseDetailDAO.delete(coreIncome.getId(), tableType);
			}
		}

		// Delete School NonCore_Income Details.
		List<AuditDetail> nonCoreIncomeDetails = incomeExpenseHeader.getAuditDetailMap().get("NonCoreIncome");
		sequence = incomeExpenseHeader.getCoreIncomeList().size();
		if (nonCoreIncomeDetails != null && !nonCoreIncomeDetails.isEmpty()) {
			IncomeExpenseDetail coreIncome = new IncomeExpenseDetail();
			String[] fields = PennantJavaUtil.getFieldDetails(coreIncome, coreIncome.getExcludeFields());
			for (int i = 0; i < nonCoreIncomeDetails.size(); i++) {
				coreIncome = (IncomeExpenseDetail) nonCoreIncomeDetails.get(i).getModelData();
				auditList.add(new AuditDetail(auditTranType, ++sequence, fields[0], fields[1], coreIncome.getBefImage(),
						coreIncome));
				incomeExpenseDetailDAO.delete(coreIncome.getId(), tableType);
			}
		}

		// Delete School Expense Details.
		List<AuditDetail> expenseDetailsList = incomeExpenseHeader.getAuditDetailMap().get("Expense");
		sequence = incomeExpenseHeader.getCoreIncomeList().size() + incomeExpenseHeader.getNonCoreIncomeList().size();
		if (expenseDetailsList != null && !expenseDetailsList.isEmpty()) {
			IncomeExpenseDetail coreIncome = new IncomeExpenseDetail();
			String[] fields = PennantJavaUtil.getFieldDetails(coreIncome, coreIncome.getExcludeFields());
			for (int i = 0; i < expenseDetailsList.size(); i++) {
				coreIncome = (IncomeExpenseDetail) expenseDetailsList.get(i).getModelData();
				auditList.add(new AuditDetail(auditTranType, ++sequence, fields[0], fields[1], coreIncome.getBefImage(),
						coreIncome));
				incomeExpenseDetailDAO.delete(coreIncome.getId(), tableType);
			}
		}

		logger.debug(Literal.LEAVING);
		return auditList;
	}

	private List<AuditDetail> getListAuditDetails(List<AuditDetail> list) {
		logger.debug("Entering");

		List<AuditDetail> auditDetailsList = new ArrayList<AuditDetail>();

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				String transType = "";
				String rcdType = "";
				Object object = ((AuditDetail) list.get(i)).getModelData();
				try {

					rcdType = object.getClass().getMethod("getRecordType").invoke(object).toString();

					if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
						transType = PennantConstants.TRAN_ADD;
					} else if (rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
							|| rcdType.equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
						transType = PennantConstants.TRAN_DEL;
					} else {
						transType = PennantConstants.TRAN_UPD;
					}

					if (StringUtils.isNotEmpty(transType)) {

						// check and change below line for Complete code Object
						Object befImg = object.getClass().getMethod("getBefImage", object.getClass().getClasses())
								.invoke(object, object.getClass().getClasses());

						auditDetailsList.add(
								new AuditDetail(transType, ((AuditDetail) list.get(i)).getAuditSeq(), befImg, object));
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
		}

		logger.debug("Leaving");
		return auditDetailsList;
	}

	@Override
	public boolean isExist(String custCif, int financialYear) {
		return incomeExpenseHeaderDAO.isExist(custCif, financialYear, "_view");
	}
}
