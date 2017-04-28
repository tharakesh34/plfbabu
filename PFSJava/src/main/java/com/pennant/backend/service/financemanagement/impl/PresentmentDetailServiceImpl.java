/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  PresentmentDetailServiceImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.service.financemanagement.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.financemanagement.PresentmentDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.RepayConstants;
import com.pennanttech.dbengine.DataEngineDBProcess;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>PresentmentDetail</b>.<br>
 */
public class PresentmentDetailServiceImpl extends GenericService<PresentmentDetail> implements PresentmentDetailService {
	private final static Logger logger = Logger.getLogger(PresentmentDetailServiceImpl.class);

	private PresentmentDetailDAO presentmentDetailDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private DataSource dataSource;

	public void setPresentmentDetailDAO(PresentmentDetailDAO presentmentDetailDAO) {
		this.presentmentDetailDAO = presentmentDetailDAO;
	}

	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public long savePresentmentHeader(PresentmentHeader presentmentHeader) {
		return presentmentDetailDAO.savePresentmentHeader(presentmentHeader);
	}

	@Override
	public void updatePresentmentDetails(long presentmentId, List<Long> detaildList) throws Exception {
		presentmentDetailDAO.updatePresentmentDetailId(presentmentId, detaildList);
	}

	@Override
	public void updatePresentmentDetailHeader(long presentmentId, long extractId) {
		presentmentDetailDAO.updatePresentmentDetailId(presentmentId, extractId);
	}

	/* processPresentmentDetails */
	@Override
	public String savePresentmentDetails(PresentmentHeader header) throws Exception {
		logger.debug(Literal.ENTERING);

		boolean isEmptyRecords = false;
		Map<Date, Long> map = new HashMap<Date, Long>();
		long presentmentId = 0;

		try {
			ResultSet rs = presentmentDetailDAO.getPresentmentDetails(header);
			while (rs.next()) {
				Date schDate = rs.getDate("SCHDATE");
				if (schDate != null) {
					if (!map.containsKey(schDate)) {
						presentmentId = savePresentmentHeaderDetails(header);
						map.put(schDate, presentmentId);
					}
				}
				isEmptyRecords = true;

				PresentmentDetail pDetail = new PresentmentDetail();
				pDetail.setFinReference(rs.getString("FINREFERENCE"));

				pDetail.setPresentmentId(presentmentId);

				pDetail.setSchDate(rs.getDate("SCHDATE"));
				pDetail.setMandateId(rs.getLong("MANDATEID"));

				BigDecimal schAmtDue = rs.getBigDecimal("PROFITSCHD").add(rs.getBigDecimal("PRINCIPALSCHD"))
						.add(rs.getBigDecimal("FEESCHD")).subtract(rs.getBigDecimal("SCHDPRIPAID"))
						.subtract(rs.getBigDecimal("SCHDPFTPAID")).subtract(rs.getBigDecimal("SCHDFEEPAID"));

				pDetail.setSchAmtDue(schAmtDue);
				pDetail.setAdvanceAmt(schAmtDue);
				pDetail.setSchPriDue(rs.getBigDecimal("PRINCIPALSCHD").subtract(rs.getBigDecimal("SCHDPRIPAID")));
				pDetail.setSchPftDue(rs.getBigDecimal("PROFITSCHD").subtract(rs.getBigDecimal("SCHDPFTPAID")));
				pDetail.setSchFeeDue(rs.getBigDecimal("FEESCHD").subtract(rs.getBigDecimal("SCHDFEEPAID")));
				pDetail.setEmiNo(rs.getInt("EMINO"));
				pDetail.setSchInsDue(BigDecimal.ZERO);
				pDetail.setSchPenaltyDue(BigDecimal.ZERO);
				pDetail.setAdviseAmt(BigDecimal.ZERO);
				pDetail.setPresentmentAmt(BigDecimal.ZERO);

				pDetail.setBounceID(presentmentId);
				pDetail.setStatus(RepayConstants.PEXC_EXTRACT);
				pDetail.setExcludeReason(0);
				pDetail.setVersion(0);
				pDetail.setLastMntBy(header.getLastMntBy());
				pDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				pDetail.setRecordStatus("");
				pDetail.setRoleCode("");
				pDetail.setNextRoleCode("");
				pDetail.setTaskId("");
				pDetail.setNextTaskId("");
				pDetail.setRecordType("");
				pDetail.setWorkflowId(0);

				doCalculations(pDetail, header);
				if (pDetail.getExcessID() != null) {
					finExcessAmountDAO.updateExcessAmount(pDetail.getExcessID(), "R", pDetail.getAdvanceAmt());
				}
				presentmentDetailDAO.save(pDetail, TableType.MAIN_TAB);
			}

			if (!isEmptyRecords) {
				return " No records are available to extract, please change the search criteria.";
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
		return " Extracted successfully.";
	}

	private long savePresentmentHeaderDetails(PresentmentHeader header) {

		long id = presentmentDetailDAO.getSeqNumber("SeqPresentmentHeader");
		String reference = StringUtils.leftPad(String.valueOf(id), 10, "0");
		header.setId(id);
		header.setStatus(RepayConstants.PEXC_EXTRACT);
		header.setPresentmentDate(DateUtility.getSysDate());
		header.setReference("PRE".concat(reference));// FIXME the reference preparation
		presentmentDetailDAO.savePresentmentHeader(header);
		return id;

	}

	private void doCalculations(PresentmentDetail presentmentDetail, PresentmentHeader detailHeader) {
		logger.debug(Literal.ENTERING);

		BigDecimal emiInAdvanceAmt;
		String finReference = presentmentDetail.getFinReference();

		// EMI HOLD
		if (DateUtility.compare(presentmentDetail.getDefSchdDate(), detailHeader.getToDate()) > 0) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_EMIHOLD);
			return;
		}

		// Mandate Hold
		if (MandateConstants.STATUS_HOLD.equals(presentmentDetail.getMandateStatus())) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_MANDATE_HOLD);
			return;
		}

		// Mandate Not Approved
		if (!MandateConstants.TYPE_ECS.equals(presentmentDetail.getMandateType())
				&& !MandateConstants.STATUS_APPROVED.equals(presentmentDetail.getMandateStatus())) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_MANDATE_NOTAPPROV);
			return;
		}

		if (MandateConstants.TYPE_ECS.equals(presentmentDetail.getMandateType())
				&& !((MandateConstants.STATUS_APPROVED.equals(presentmentDetail.getMandateStatus()))
						|| (MandateConstants.STATUS_AWAITCON.equals(presentmentDetail.getMandateStatus())) || (MandateConstants.STATUS_NEW
							.equals(presentmentDetail.getMandateStatus())))) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_MANDATE_NOTAPPROV);
			return;
		}

		// Mandate Expired
		if (DateUtility.compare(presentmentDetail.getDefSchdDate(), presentmentDetail.getMandateExpiryDate()) > 0) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_MANDATE_EXPIRY);
			return;
		}

		// EMI IN ADVANCE
		FinExcessAmount finExcessAmount = finExcessAmountDAO.getExcessAmountsByRefAndType(finReference,
				RepayConstants.EXAMOUNTTYPE_EMIINADV);
		if (finExcessAmount != null) {
			emiInAdvanceAmt = finExcessAmount.getBalanceAmt();
			presentmentDetail.setExcessID(finExcessAmount.getExcessID());
		} else {
			emiInAdvanceAmt = BigDecimal.ZERO;
		}

		if (emiInAdvanceAmt.compareTo(presentmentDetail.getSchAmtDue()) >= 0) {
			presentmentDetail.setExcludeReason(RepayConstants.PEXC_EMIINADVANCE);
		} else {
			presentmentDetail.setPresentmentAmt(presentmentDetail.getSchAmtDue().subtract(emiInAdvanceAmt));
			presentmentDetail.setAdvanceAmt(emiInAdvanceAmt);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void processDetails(List<Long> presentmentList) throws Exception {
		logger.debug(Literal.ENTERING);

		StringBuilder ids = new StringBuilder();
		for (Long id : presentmentList) {
			if (ids.length() > 0) {
				ids.append(",");
			} else {
				ids.append(id);
			}
		}
		DataEngineDBProcess proce = new DataEngineDBProcess(dataSource, 1000, App.DATABASE.name());
		try {
			proce.processData("PRESENTMENT_REQUEST", ids.toString());
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<PresentmentDetail> getPresentmentDetailsList(long presentmentId, String type) {
		return presentmentDetailDAO.getPresentmentDetailsList(presentmentId, type);
	}

}