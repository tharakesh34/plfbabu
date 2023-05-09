package com.pennant.pff.lien.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.backend.dao.liendetails.LienDetailsDAO;
import com.pennant.backend.dao.lienheader.LienHeaderDAO;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.lien.service.LienService;
import com.pennant.pff.mandate.InstrumentType;
import com.pennanttech.model.lien.LienDetails;
import com.pennanttech.model.lien.LienHeader;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;

public class LienServiceImpl implements LienService {
	private static Logger logger = LogManager.getLogger(LienServiceImpl.class);

	private LienHeaderDAO lienHeaderDAO;
	private LienDetailsDAO lienDetailsDAO;

	public LienServiceImpl() {
		super();
	}

	@Override
	public void save(FinanceDetail fd, boolean isMandate) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		String accNumber = fd.getMandate().getAccNumber();

		LienHeader lh = lienHeaderDAO.getLienByAcc(accNumber);

		long headerID = 0;

		if (lh == null) {
			lh = getLienHeader(fm, fd);
			lh.setDemarking("");
			lh.setDemarkingDate(null);
			lh.setMarkingDate(fd.getMandate().getStartDate());

			if (fm.getFinSourceID().equals(RequestSource.UPLOAD.name())) {
				lh.setLienID(fd.getLienHeader().getLienID());
				lh.setLienReference(fd.getLienHeader().getLienReference());
				lh.setId(fd.getLienHeader().getId());
			}

			headerID = lienHeaderDAO.save(lh);
		} else {
			headerID = lh.getId();
			if (isMandate) {
				FinanceMain fmBef = fm.getBefImage() != null ? fm.getBefImage() : fm;

				if (fm.getFinRepayMethod().equals(fmBef.getFinRepayMethod())
						&& InstrumentType.isSI(fm.getFinRepayMethod())) {
					lh.setLienStatus(false);
					lh.setInterfaceStatus(Labels.getLabel("label_Lien_Type_Pending"));
					lienHeaderDAO.update(lh);

					LienDetails lu = getLienDetails(lh, fm);

					lu.setLienStatus(false);
					lu.setDemarking(Labels.getLabel("label_Lien_Type_Auto"));
					lu.setDemarkingDate(fm.getClosedDate());
					lu.setDemarkingReason(Labels.getLabel("label_Lien_Type_DemarkReason"));

					lienDetailsDAO.update(lu);
				}
			}
		}

		LienDetails lu = getLienDetails(lh, fm);
		lu.setHeaderID(headerID);
		lu.setLienID(lh.getLienID());
		lu.setLienReference(lh.getLienReference());
		lh.setMarkingDate(fd.getMandate().getStartDate());
		lu.setDemarking("");
		lu.setDemarkingDate(null);
		lu.setDemarkingReason("");

		lienDetailsDAO.save(lu);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void update(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		FinanceMain fmBef = fm.getBefImage() != null ? fm.getBefImage() : fm;
		if (fmBef.getMandateID() == null) {
			return;
		}
		String accNum = fd.getMandate().getAccNumber();
		List<LienHeader> lienheader = lienHeaderDAO.getLienHeaderList(fm.getFinReference());

		if (lienheader == null) {
			logger.debug(Literal.LEAVING);
			return;
		}
		for (LienHeader lh : lienheader) {

			if (!lh.getAccountNumber().equals(accNum)
					&& fm.getModuleDefiner().equals(FinServiceEvent.RPYBASICMAINTAIN)) {
				continue;
			}

			List<LienDetails> lienDetail = lienDetailsDAO.getLienListByLienId(lh.getLienID());

			boolean isAllInActive = true;
			for (LienDetails lu : lienDetail) {
				if (lu.getReference().equals(fm.getFinReference())) {
					lu.setLienStatus(false);
					lu.setDemarking(Labels.getLabel("label_Lien_Type_Auto"));
					lu.setDemarkingDate(fm.getClosedDate());

					setLienStatus(lu, fm.getModuleDefiner());

					lienDetailsDAO.update(lu);
				}

				if (lu.isLienStatus()) {
					isAllInActive = false;
				}
			}

			if (isAllInActive) {
				lh.setLienStatus(false);
				lh.setInterfaceStatus(Labels.getLabel("label_Lien_Type_Pending"));
				lh.setDemarking(Labels.getLabel("label_Lien_Type_Auto"));
				lh.setDemarkingDate(fm.getClosedDate());
				lienHeaderDAO.update(lh);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private LienHeader getLienHeader(FinanceMain fm, FinanceDetail fd) {
		LienHeader lh = new LienHeader();

		if (!RequestSource.UPLOAD.name().equals(fm.getFinSourceID())) {
			lh.setSource("PLF");
		}
		lh.setReference(fm.getFinReference());
		lh.setMarkingDate(fm.getFinStartDate());
		lh.setDemarkingDate(fm.getClosedDate());
		lh.setAccountNumber(fd.getMandate().getAccNumber());
		lh.setInterfaceStatus(Labels.getLabel("label_Lien_Type_Pending"));
		lh.setMarking(Labels.getLabel("label_Lien_Type_Auto"));
		lh.setDemarking(Labels.getLabel("label_Lien_Type_Auto"));
		lh.setLienStatus(true);

		return lh;
	}

	private LienDetails getLienDetails(LienHeader lh, FinanceMain fm) {
		LienDetails ld = new LienDetails();

		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		long userId = 1000;

		String moduleType = fm.getModuleDefiner();

		setLienStatus(ld, moduleType);

		ld.setLienID(lh.getLienID());
		ld.setLienReference(lh.getLienReference());
		if (!RequestSource.UPLOAD.name().equals(fm.getFinSourceID())) {
			ld.setSource("PLF");
		}
		ld.setReference(fm.getFinReference());
		ld.setMarkingDate(fm.getFinStartDate());
		ld.setMarking(Labels.getLabel("label_Lien_Type_Auto"));
		ld.setDemarking(Labels.getLabel("label_Lien_Type_Auto"));
		ld.setDemarkingDate(null);
		ld.setLienStatus(true);
		ld.setVersion(1);
		ld.setCreatedBy(userId);
		ld.setCreatedOn(currentTime);
		ld.setLastMntBy(userId);
		ld.setLastMntOn(currentTime);
		ld.setApprovedOn(currentTime);
		ld.setApprovedBy(userId);

		return ld;
	}

	private void setLienStatus(LienDetails ld, String moduleType) {
		switch (moduleType) {
		case FinServiceEvent.ORG:
			ld.setMarkingReason("Loan Creation");
			break;
		case FinServiceEvent.RPYBASICMAINTAIN:
			ld.setMarkingReason("Repay method changed");
			break;
		case FinServiceEvent.RECEIPT:
			ld.setDemarkingReason("Early settlement");
			break;
		case FinServiceEvent.CANCELFIN:
			ld.setDemarkingReason("Loan cancelled");
			break;
		case "Mandate Creation":
			ld.setMarkingReason("Mandate Creation");
			break;
		default:
			ld.setMarkingReason(" ");
			break;
		}
	}

	@Autowired
	public void setLienHeaderDAO(LienHeaderDAO lienHeaderDAO) {
		this.lienHeaderDAO = lienHeaderDAO;
	}

	@Autowired
	public void setLienDetailsDAO(LienDetailsDAO lienDetailsDAO) {
		this.lienDetailsDAO = lienDetailsDAO;
	}
}