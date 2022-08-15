package com.pennant.backend.service.applicationmaster.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.ReportingManagerDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.administration.ReportingManager;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.applicationmaster.ReportingManagerService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class ReportingManagerServiceImpl extends GenericService<ReportingManager> implements ReportingManagerService {

	private static final Logger logger = LogManager.getLogger(ReportingManagerServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ReportingManagerDAO reportingManagerDAO;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public ReportingManagerDAO getReportingManagerDAO() {
		return reportingManagerDAO;
	}

	public void setReportingManagerDAO(ReportingManagerDAO reportingManagerDAO) {
		this.reportingManagerDAO = reportingManagerDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table Clusters/Clusters_Temp by using
	 * ClustersDAO's save method b) Update the Record in the table. based on the module workFlow Configuration. by using
	 * ClustersDAO's update method 3) Audit the record in to AuditHeader and AdtClusters by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ReportingManager reportingManager = (ReportingManager) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (reportingManager.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (reportingManager.isNewRecord()) {
			reportingManager.setId(Long.parseLong(reportingManagerDAO.save(reportingManager, tableType.getSuffix())));
			auditHeader.getAuditDetail().setModelData(reportingManager);
			auditHeader.setAuditReference(String.valueOf(reportingManager.getId()));
		} else {
			reportingManagerDAO.update(reportingManager, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * Clusters by using ClustersDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtClusters by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ReportingManager reportingManager = (ReportingManager) auditHeader.getAuditDetail().getModelData();
		reportingManagerDAO.deleteByUserId(reportingManager.getUserId(), TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getClusters fetch the details by using ClustersDAO's getClustersById method.
	 * 
	 * @param clusterId clusterId of the ReportingManager.
	 * @return Clusters
	 */
	@Override
	public ReportingManager getReportingManager(long Id) {
		return reportingManagerDAO.getReportingManager(Id, "_View");
	}

	/**
	 * getApprovedClustersById fetch the details by using ClustersDAO's getClustersById method . with parameter id and
	 * type as blank. it fetches the approved records from the Clusters.
	 * 
	 * @param clusterId clusterId of the ReportingManager. (String)
	 * @return Clusters
	 */
	public ReportingManager getApprovedReportingManager(long Id) {
		return reportingManagerDAO.getReportingManager(Id, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using reportingManagerDAO.delete with
	 * parameters reportingManager,"" b) NEW Add new record in to main table by using reportingManagerDAO.save with
	 * parameters reportingManager,"" c) EDIT Update record in the main table by using reportingManagerDAO.update with
	 * parameters reportingManager,"" 3) Delete the record from the workFlow table by using reportingManagerDAO.delete
	 * with parameters reportingManager,"_Temp" 4) Audit the record in to AuditHeader and AdtClusters by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtClusters by using
	 * auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ReportingManager reportingManager = new ReportingManager();
		BeanUtils.copyProperties((ReportingManager) auditHeader.getAuditDetail().getModelData(), reportingManager);

		reportingManagerDAO.deleteByUserId(reportingManager.getUserId(), TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(reportingManager.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(reportingManagerDAO.getReportingManager(reportingManager.getId(), ""));
		}

		if (reportingManager.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			reportingManagerDAO.deleteByUserId(reportingManager.getUserId(), TableType.MAIN_TAB);
		} else {
			reportingManager.setRoleCode("");
			reportingManager.setNextRoleCode("");
			reportingManager.setTaskId("");
			reportingManager.setNextTaskId("");
			reportingManager.setWorkflowId(0);

			if (reportingManager.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				reportingManager.setRecordType("");
				reportingManagerDAO.save(reportingManager, tranType);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				reportingManager.setRecordType("");
				reportingManagerDAO.update(reportingManager, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(reportingManager);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using reportingManagerDAO.delete with parameters reportingManager,"_Temp" 3) Audit the record
	 * in to AuditHeader and AdtClusters by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		ReportingManager reportingManager = (ReportingManager) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		reportingManagerDAO.deleteByUserId(reportingManager.getUserId(), TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from reportingManagerDAO.getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	public AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Get the model object.
		ReportingManager entity = (ReportingManager) auditDetail.getModelData();

		// Check the unique keys.
		if (entity.isNewRecord() && reportingManagerDAO.isDuplicateKey(entity,
				entity.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[5];

			parameters[0] = PennantJavaUtil.getLabel("label_UserId") + ":" + entity.getUserId();
			parameters[1] = PennantJavaUtil.getLabel("label_BusinessVertical") + ":" + entity.getBusinessVertical();
			parameters[2] = PennantJavaUtil.getLabel("label_LoanType") + ":" + entity.getFinType();
			parameters[3] = PennantJavaUtil.getLabel("label_Product") + ":" + entity.getProduct();
			parameters[4] = PennantJavaUtil.getLabel("label_Branch") + ":" + entity.getBranch();

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}
