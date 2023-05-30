package com.pennant.pff.lien.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.liendetails.LienDetailsDAO;
import com.pennant.backend.dao.lienheader.LienHeaderDAO;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.pff.lien.service.LienService;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.receipt.ClosureType;
import com.pennanttech.model.lien.LienDetails;
import com.pennanttech.model.lien.LienHeader;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.RequestSource;

public class LienServiceImpl implements LienService {
	private static Logger logger = LogManager.getLogger(LienServiceImpl.class);

	private LienHeaderDAO lienHeaderDAO;
	private LienDetailsDAO lienDetailsDAO;
	private MandateService mandateService;

	public LienServiceImpl() {
		super();
	}

	@Override
	public void save(FinanceDetail fd, boolean isMandate) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		Date appDate = SysParamUtil.getAppDate();

		fm.setModuleDefiner(fd.getModuleDefiner());

		FinanceMain fmBef = fm.getBefImage();
		String accNumber = "";
		if (fmBef != null && fmBef.getMandateID() != null) {
			Mandate m = mandateService.getMandate(fmBef.getMandateID());
			if (m != null && !InstrumentType.isNACH(fmBef.getFinRepayMethod())) {
				accNumber = m.getAccNumber();
			} else {
				accNumber = fd.getMandate().getAccNumber();
			}
		} else {
			accNumber = fd.getMandate().getAccNumber();
		}

		LienHeader lh = fd.getLienHeader();

		if (!RequestSource.UPLOAD.name().equals(fm.getFinSourceID())) {
			lh = lienHeaderDAO.getLienByAccAndStatus(accNumber, true);
		}

		long headerID = 0;

		if (lh == null) {
			lh = getLienHeader(fm, fd);
			lh.setDemarking("");
			lh.setDemarkingDate(null);
			Date startDate = fd.getMandate().getStartDate();
			lh.setMarkingDate(startDate == null ? appDate : startDate);

			if (FinServiceEvent.ORG.equals(fm.getModuleDefiner())) {
				lh.setMarkingDate(fm.getFinStartDate());
			}

			if (fm.getFinSourceID().equals(RequestSource.UPLOAD.name())) {
				lh.setLienID(fd.getLienHeader().getLienID());
				lh.setLienReference(fd.getLienHeader().getLienReference());
				lh.setId(fd.getLienHeader().getId());
			}

			headerID = lienHeaderDAO.save(lh);
		} else {
			headerID = lh.getId();
			fmBef = fm.getBefImage();
			if (isMandate && fmBef != null) {
				if (InstrumentType.isSI(fmBef.getFinRepayMethod())) {
					lh.setLienStatus(false);
					lh.setInterfaceStatus(Labels.getLabel("label_Lien_Type_Pending"));
					lh.setDemarking(Labels.getLabel("label_Lien_Type_Auto"));
					if (FinServiceEvent.RPYBASICMAINTAIN.equals(fm.getModuleDefiner())) {
						if (fd.getMandate() != null && fd.getMandate().getSwapEffectiveDate() != null) {
							lh.setDemarkingDate(fd.getMandate().getSwapEffectiveDate());
						} else {
							lh.setDemarkingDate(appDate);
						}
					} else {
						lh.setDemarkingDate(fm.getClosedDate());
					}
					lienHeaderDAO.update(lh);

					LienDetails lu = getLienDetails(lh, fm);

					lu.setLienStatus(false);
					lu.setDemarking(Labels.getLabel("label_Lien_Type_Auto"));
					if (FinServiceEvent.RPYBASICMAINTAIN.equals(fm.getModuleDefiner())) {
						if (fd.getMandate() != null && fd.getMandate().getSwapEffectiveDate() != null) {
							lu.setDemarkingDate(fd.getMandate().getSwapEffectiveDate());
						} else {
							lu.setDemarkingDate(appDate);
						}
					} else {
						lu.setDemarkingDate(fm.getClosedDate());
					}
					setLienDeMarkStatus(lu, fm.getModuleDefiner());
					setLienDeMarkReason(lu, fm.getModuleDefiner());

					lienDetailsDAO.update(lu);
				} else {
					lh.setLienStatus(true);
					lh.setInterfaceStatus(Labels.getLabel("label_Lien_Type_Pending"));
					lh.setMarking(Labels.getLabel("label_Lien_Type_Auto"));
					if (FinServiceEvent.RPYBASICMAINTAIN.equals(fm.getModuleDefiner())) {
						if (fd.getMandate() != null && fd.getMandate().getSwapEffectiveDate() != null) {
							lh.setMarkingDate(fd.getMandate().getSwapEffectiveDate());
						} else {
							lh.setMarkingDate(appDate);
						}
					} else {
						lh.setMarkingDate(fm.getFinStartDate());
					}
					lienHeaderDAO.update(lh);

					LienDetails lu = getLienDetails(lh, fm);

					lu.setLienStatus(true);
					lu.setMarking(Labels.getLabel("label_Lien_Type_Auto"));
					lu.setDemarking("");
					if (FinServiceEvent.RPYBASICMAINTAIN.equals(fm.getModuleDefiner())) {
						if (fd.getMandate() != null && fd.getMandate().getSwapEffectiveDate() != null) {
							lu.setMarkingDate(fd.getMandate().getSwapEffectiveDate());
						} else {
							lu.setMarkingDate(appDate);
						}
					} else {
						lu.setMarkingDate(fm.getFinStartDate());
					}
					setLienMarkStatus(lu, fm.getModuleDefiner());
					lienDetailsDAO.update(lu);

				}
				return;
			} else if (RequestSource.UPLOAD.name().equals(fm.getFinSourceID())) {
				lienHeaderDAO.update(lh);
				return;
			}

		}

		LienDetails lu = getLienDetails(lh, fm);
		lu.setHeaderID(headerID);
		lu.setLienID(lh.getLienID());
		lu.setLienReference(lh.getLienReference());
		Date startDate = fd.getMandate().getStartDate();
		lu.setMarkingDate(startDate == null ? appDate : startDate);
		if (FinServiceEvent.ORG.equals(fm.getModuleDefiner())) {
			lu.setMarkingDate(fm.getFinStartDate());
		}
		lu.setDemarking(lh.getDemarking());
		lu.setDemarkingDate(lh.getDemarkingDate());
		lu.setMarking(lh.getMarking());
		lu.setDemarkingReason("");

		lienDetailsDAO.save(lu);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void update(FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		String accNum = "";
		FinanceMain fmBef = fm.getBefImage();
		fm.setModuleDefiner(fd.getModuleDefiner());

		if (fm.getClosureType() != null) {
			fm.setModuleDefiner(ClosureType.CLOSURE.code());
		}

		Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		if (fmBef != null) {
			if (fmBef.getMandateID() == null) {
				return;
			}

			Mandate m = mandateService.getMandate(fmBef.getMandateID());

			if (m != null) {
				accNum = m.getAccNumber();
			}
		}
		List<LienHeader> lienheader = lienHeaderDAO.getLienHeaderList(fm.getFinReference());

		if (lienheader == null) {
			logger.debug(Literal.LEAVING);
			return;
		}

		for (LienHeader lh : lienheader) {
			if (FinServiceEvent.RPYBASICMAINTAIN.equals(fm.getModuleDefiner())
					&& !lh.getAccountNumber().equals(accNum)) {
				continue;
			}

			if ((Labels.getLabel("label_Lien_Type_Manual")).equals(lh.getMarking())
					&& !FinServiceEvent.CANCELFIN.equals(fm.getModuleDefiner())) {
				continue;
			}

			List<LienDetails> lienDetail = lienDetailsDAO.getLienListByLienId(lh.getLienID());

			boolean isAllInActive = true;
			for (LienDetails lu : lienDetail) {
				if (lu.getReference().equals(fm.getFinReference())) {
					lu.setLienStatus(false);
					lu.setDemarking(Labels.getLabel("label_Lien_Type_Auto"));
					if (FinServiceEvent.RPYBASICMAINTAIN.equals(fm.getModuleDefiner())) {
						if (fd.getMandate() != null && fd.getMandate().getSwapEffectiveDate() != null) {
							lu.setDemarkingDate(fd.getMandate().getSwapEffectiveDate());
						} else {
							lu.setDemarkingDate(currentTime);
						}
					} else {
						lu.setDemarkingDate(fm.getClosedDate());
					}
					setLienDeMarkReason(lu, fm.getModuleDefiner());
					setLienDeMarkStatus(lu, fm.getModuleDefiner());

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
				if (FinServiceEvent.RPYBASICMAINTAIN.equals(fm.getModuleDefiner())) {
					if (fd.getMandate() != null && fd.getMandate().getSwapEffectiveDate() != null) {
						lh.setDemarkingDate(fd.getMandate().getSwapEffectiveDate());
					} else {
						lh.setDemarkingDate(fd.getAppDate());
					}
				} else {
					lh.setDemarkingDate(fm.getClosedDate());
				}

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

		setLienMarkStatus(ld, moduleType);

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

	private void setLienMarkStatus(LienDetails ld, String moduleType) {
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

	private void setLienDeMarkStatus(LienDetails lu, String moduleDefiner) {
		switch (moduleDefiner) {
		case FinServiceEvent.RPYBASICMAINTAIN:
			lu.setDemarkingReason("Repay method changed");
			break;
		case FinServiceEvent.RECEIPT:
			lu.setDemarkingReason("Early settlement");
			break;
		case FinServiceEvent.CANCELFIN:
			lu.setDemarkingReason("Loan cancelled");
			break;
		case "Closure":
			lu.setDemarkingReason("Loan Closed");
			break;
		default:
			lu.setDemarkingReason("");
			break;
		}

	}

	private void setLienDeMarkReason(LienDetails lu, String moduleDefiner) {
		switch (moduleDefiner) {
		case FinServiceEvent.RPYBASICMAINTAIN:
			lu.setDemarkingReason("Repay method changed");
			break;
		case FinServiceEvent.RECEIPT:
			lu.setDemarkingReason("Early settlement");
			break;
		case FinServiceEvent.CANCELFIN:
			lu.setDemarkingReason("Loan cancelled");
			break;

		case "Closure":
			lu.setDemarkingReason("Loan Closed");
			break;
		default:
			lu.setDemarkingReason("");
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

	@Autowired
	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}
}