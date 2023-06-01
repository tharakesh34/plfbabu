package com.pennanttech.pff.knockoff.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.core.AutoKnockOffService;
import com.pennant.backend.dao.receipts.CrossLoanKnockOffDAO;
import com.pennant.backend.dao.receipts.CrossLoanTransferDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.model.autoknockoff.AutoKnockOffExcessDetails;
import com.pennant.backend.model.customermasters.CustomerCoreBank;
import com.pennant.backend.model.finance.AutoKnockOffExcess;
import com.pennant.backend.model.finance.CrossLoanKnockOff;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.CrossLoanKnockOffService;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.knockoff.ExcessKnockOffUtil;
import com.pennanttech.pff.knockoff.dao.ExcessKnockOffDAO;
import com.pennanttech.pff.knockoff.model.ExcessKnockOff;
import com.pennanttech.pff.knockoff.model.ExcessKnockOffDetails;
import com.pennanttech.pff.knockoff.service.ExcessKnockOffService;

public class ExcessKnockOffServiceImpl implements ExcessKnockOffService {

	private ExcessKnockOffDAO excessKnockOffDAO;
	private CrossLoanKnockOffDAO crossLoanKnockOffDAO;
	private CrossLoanTransferDAO crossLoanTransferDAO;
	private FinExcessAmountDAO finExcessAmountDAO;
	private AutoKnockOffService autoKnockOffService;
	private CrossLoanKnockOffService crossLoanKnockOffService;

	public ExcessKnockOffServiceImpl() {
		super();
	}

	@Override
	public List<ExcessKnockOff> loadData(CustomerCoreBank customerCoreBank) {
		return this.excessKnockOffDAO.loadData(customerCoreBank);
	}

	@Override
	public List<ExcessKnockOffDetails> getStageDataByID(long id) {
		return this.excessKnockOffDAO.getStageDataByID(id);
	}

	@Override
	public List<FinanceMain> getLoansbyCustId(long custId, String coreBankId, long finId) {
		return this.excessKnockOffDAO.getLoansbyCustId(custId, coreBankId, finId);
	}

	@Override
	public void process(ExcessKnockOff ekf, FinanceMain fm) {
		AutoKnockOffExcess ake = ExcessKnockOffUtil.getExcessKnockOff(ekf, fm);

		ake.setCrossLoanAutoKnockOff(true);

		ekf.getExcessKnockOffDetails()
				.forEach(ekod -> ake.getExcessDetails().add(ExcessKnockOffUtil.getKnockOffDetails(ekod)));

		this.autoKnockOffService.process(ake);

		List<AutoKnockOffExcessDetails> excessDetails = ake.getExcessDetails();
		List<CrossLoanKnockOff> clkoList = new ArrayList<>();

		LoggedInUser userDetails = new LoggedInUser();

		for (AutoKnockOffExcessDetails ako : excessDetails) {
			if (ako.getReceiptID() > 0) {
				CrossLoanKnockOff clko = ExcessKnockOffUtil.getCrossLoanKnockOff(ako, ekf, fm);
				clko.setCrossLoanTransfer(ExcessKnockOffUtil.getCrossLoanTransfer(ako, ekf, fm));
				clko.getCrossLoanTransfer().setReceiptId(ako.getReceiptID());
				clko.setReceiptID(ako.getReceiptID());
				clko.getCrossLoanTransfer().setValueDate(ake.getValueDate());
				clko.setValueDate(clko.getCrossLoanTransfer().getValueDate());
				clko.setUserDetails(userDetails);
				clko.getCrossLoanTransfer().setUserDetails(userDetails);

				clko.getCrossLoanTransfer().setExcessValueDate(clko.getCrossLoanTransfer().getValueDate());
				clko.setExcessValueDate(clko.getCrossLoanTransfer().getValueDate());

				if ("E".equals(ake.getAmountType())) {
					FinExcessAmount fea = finExcessAmountDAO.getFinExcessAmountById(ake.getPayableID(), "");

					if (fea != null && fea.getValueDate() != null) {
						clko.getCrossLoanTransfer().setExcessValueDate(fea.getValueDate());
						clko.setExcessValueDate(fea.getValueDate());
					}
				}

				crossLoanKnockOffService.executeAccounting(clko.getCrossLoanTransfer());

				clko.setTransferID(this.crossLoanTransferDAO.save(clko.getCrossLoanTransfer(), ""));
				ekf.setBalanceAmt(ekf.getBalanceAmt().subtract(clko.getReceiptAmount()));
				ekf.setTotalUtilizedAmnt(ekf.getTotalUtilizedAmnt().add(clko.getReceiptAmount()));

				clkoList.add(clko);
			}
		}

		if (CollectionUtils.isNotEmpty(clkoList)) {

			this.crossLoanKnockOffDAO.saveCrossLoanHeader(clkoList, "");

		}

	}

	@Autowired
	public void setExcessKnockOffDAO(ExcessKnockOffDAO excessKnockOffDAO) {
		this.excessKnockOffDAO = excessKnockOffDAO;
	}

	@Autowired
	public void setCrossLoanKnockOffDAO(CrossLoanKnockOffDAO crossLoanKnockOffDAO) {
		this.crossLoanKnockOffDAO = crossLoanKnockOffDAO;
	}

	@Autowired
	public void setCrossLoanTransferDAO(CrossLoanTransferDAO crossLoanTransferDAO) {
		this.crossLoanTransferDAO = crossLoanTransferDAO;
	}

	@Autowired
	public void setAutoKnockOffService(AutoKnockOffService autoKnockOffService) {
		this.autoKnockOffService = autoKnockOffService;
	}

	@Autowired
	public void setCrossLoanKnockOffService(CrossLoanKnockOffService crossLoanKnockOffService) {
		this.crossLoanKnockOffService = crossLoanKnockOffService;
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

}