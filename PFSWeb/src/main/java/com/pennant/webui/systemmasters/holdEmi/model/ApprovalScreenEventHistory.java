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
 * FileName    		:  ApprovalScreenEventHistory.java                                     * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  25-03-2021    														*
 *                                                                  						*
 * Modified Date    :  																		*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 25-03-2021       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.holdEmi.model;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.model.FrequencyDetails;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class ApprovalScreenEventHistory {
	public ApprovalScreenEventHistory() {
		super();
	}

	public void renderData(Listbox listBox, FinScheduleData finScheduleData) {
		List<FinServiceInstruction> finServiceInstructionsList = finScheduleData.getFinServiceInstructions();
		if (finServiceInstructionsList.size() > 0) {
			int totalTems = finScheduleData.getFinanceMain().getNumberOfTerms();
			for (int i = 0; i < finServiceInstructionsList.size(); i++) {

				FinServiceInstruction finServiceInstruction = finServiceInstructionsList.get(i);
				String finEvent = finServiceInstruction.getFinEvent();

				if (FinanceConstants.FINSER_EVENT_RATECHG.equals(finEvent)) {
					Listitem item = new Listitem();
					Listcell lc;
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getFromDate()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getToDate()));
					lc.setParent(item);

					if (!StringUtils.equals("", finServiceInstruction.getBaseRate())) {
						RateDetail rateDetail = RateUtil.rates(finServiceInstruction.getBaseRate(),
								finScheduleData.getFinanceMain().getFinCcy(), finServiceInstruction.getSplRate(),
								finServiceInstruction.getMargin(), finScheduleData.getFinanceMain().getRpyMinRate(),
								finScheduleData.getFinanceMain().getRpyMaxRate());
						if (rateDetail.getErrorDetails() == null) {
							lc = new Listcell(
									PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
							lc.setParent(item);
						}
					} else if (finServiceInstruction.getActualRate() != null) {
						lc = new Listcell(String.valueOf(finServiceInstruction.getActualRate()));
						lc.setParent(item);
					}

					lc = new Listcell(PennantStaticListUtil.getlabelDesc(finServiceInstruction.getRecalType(),
							PennantStaticListUtil.getSchCalCodes()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getRecalFromDate()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getRecalToDate()));
					lc.setParent(item);
					lc = new Listcell(finServiceInstruction.getRecordType());
					lc.setParent(item);
					lc = new Listcell(finServiceInstruction.getRecordStatus());
					lc.setParent(item);
					listBox.appendChild(item);
				}

				if (FinanceConstants.FINSER_EVENT_ADDDISB.equals(finEvent)) {
					Listitem item = new Listitem();
					Listcell lc;
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getFromDate()));
					lc.setParent(item);
					lc = new Listcell(PennantAppUtil.amountFormate(finServiceInstruction.getAmount(), 2));
					lc.setParent(item);
					lc = new Listcell(PennantStaticListUtil.getlabelDesc(finServiceInstruction.getRecalType(),
							PennantStaticListUtil.getSchCalCodes()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getRecalFromDate()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getRecalToDate()));
					lc.setParent(item);
					lc = new Listcell(finServiceInstruction.getRecordType());
					lc.setParent(item);
					lc = new Listcell(finServiceInstruction.getRecordStatus());
					lc.setParent(item);
					listBox.appendChild(item);
				}

				if (FinanceConstants.FINSER_EVENT_CANCELDISB.equals(finEvent)) {
					Listcell lc;
					Listitem item = new Listitem();
					lc = new Listcell(
							PennantAppUtil.amountFormate(finScheduleData.getFinanceMain().getFinCurrAssetValue(), 2));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finScheduleData.getFinanceMain().getFirstDisbDate()));
					lc.setParent(item);
					lc = new Listcell(
							PennantAppUtil.amountFormate(finScheduleData.getFinanceMain().getCurDisbursementAmt(), 2));
					lc.setParent(item);
					BigDecimal totalAmount = finScheduleData.getFinanceMain().getFinCurrAssetValue()
							.subtract(finScheduleData.getFinanceMain().getCurDisbursementAmt());
					finScheduleData.getFinanceMain().setCurDisbursementAmt(totalAmount);
					lc = new Listcell(String.valueOf(totalAmount));
					lc.setParent(item);
					listBox.appendChild(item);
				}

				if (FinanceConstants.FINSER_EVENT_ADDTERM.equals(finEvent)) {
					Listitem item = new Listitem();
					Listcell lc;
					// PSD Ticket 130142 (LMS: Add term history is not correctly reflected on approval screen)
					boolean multipleInstallments = false;
					if (finScheduleData.getFinanceMain().getNOInst() > 0) {
						multipleInstallments = true;
					}
					if (multipleInstallments) {
						lc = new Listcell(String.valueOf(finScheduleData.getFinanceMain().getNOInst()));
					} else {
						lc = new Listcell(String.valueOf(finScheduleData.getFinanceMain().getNumberOfTerms()));
					}
					lc.setParent(item);
					lc = new Listcell(String.valueOf(finServiceInstruction.getTerms()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getRecalFromDate()));
					lc.setParent(item);
					if (multipleInstallments) {
						lc = new Listcell(String.valueOf(
								finScheduleData.getFinanceMain().getNOInst() + finServiceInstruction.getTerms()));
					} else {
						totalTems = totalTems + finServiceInstruction.getTerms();
						lc = new Listcell(String.valueOf(totalTems));
					}
					lc.setParent(item);
					listBox.appendChild(item);
				}

				if (FinanceConstants.FINSER_EVENT_CHGFRQ.equals(finEvent)) {
					Listitem item = new Listitem();
					Listcell lc;
					FrequencyDetails freqDetail = FrequencyUtil
							.getFrequencyDetail(finScheduleData.getFinanceMain().getRepayFrq());
					lc = new Listcell(freqDetail.getFrequencyDescription());
					lc.setParent(item);
					FrequencyDetails detail = FrequencyUtil.getFrequencyDetail(finServiceInstruction.getRepayFrq());
					lc = new Listcell(detail.getFrequencyDescription());
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getFromDate()));
					lc.setParent(item);
					listBox.appendChild(item);
				}

				if (FinanceConstants.FINSER_EVENT_HOLDEMI.equals(finEvent)) {
					Listitem item = new Listitem();
					Listcell lc;
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getFromDate()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getToDate()));
					lc.setParent(item);
					listBox.appendChild(item);
				}

				if (FinanceConstants.FINSER_EVENT_POSTPONEMENT.equals(finEvent)) {
					Listitem item = new Listitem();
					Listcell lc;
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getFromDate()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getToDate()));
					lc.setParent(item);
					listBox.appendChild(item);
				}

				if (FinanceConstants.FINSER_EVENT_REAGING.equals(finEvent)) {
					Listitem item = new Listitem();
					Listcell lc;
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getFromDate()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getToDate()));
					lc.setParent(item);
					listBox.appendChild(item);
				}

				if (FinanceConstants.FINSER_EVENT_RMVTERM.equals(finEvent)) {
					Listitem item = new Listitem();
					Listcell lc;
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getFromDate()));
					lc.setParent(item);
					lc = new Listcell(PennantStaticListUtil.getlabelDesc(finServiceInstruction.getRecalType(),
							PennantStaticListUtil.getSchCalCodes()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getRecalFromDate()));
					lc.setParent(item);
					listBox.appendChild(item);
				}

				if (FinanceConstants.FINSER_EVENT_UNPLANEMIH.equals(finEvent)) {
					Listitem item = new Listitem();
					Listcell lc;
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getFromDate()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getToDate()));
					lc.setParent(item);
					lc = new Listcell(PennantStaticListUtil.getlabelDesc(finServiceInstruction.getRecalType(),
							PennantStaticListUtil.getSchCalCodes()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getRecalFromDate()));
					lc.setParent(item);

					if (StringUtils.equals(finServiceInstruction.getRecalType(), CalculationConstants.RPYCHG_TILLMDT)) {
						lc = new Listcell(
								DateUtil.formatToLongDate(finScheduleData.getFinanceMain().getMaturityDate()));

					} else {
						lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getRecalToDate()));
					}
					lc.setParent(item);
					listBox.appendChild(item);
				}

				if (FinanceConstants.FINSER_EVENT_RESCHD.equals(finEvent)) {
					Listitem item = new Listitem();
					Listcell lc;

					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getFromDate()));
					lc.setParent(item);

					FrequencyDetails freqDetail = FrequencyUtil
							.getFrequencyDetail(finScheduleData.getFinanceMain().getRepayFrq());
					lc = new Listcell(freqDetail.getFrequencyDescription());
					lc.setParent(item);

					if (finScheduleData.getFinanceMain().getRepayRvwFrq() != null) {
						freqDetail = FrequencyUtil
								.getFrequencyDetail(finScheduleData.getFinanceMain().getRepayRvwFrq());
						lc = new Listcell(freqDetail.getFrequencyDescription());
					} else {
						lc = new Listcell("");
					}
					lc.setParent(item);

					lc = new Listcell(finScheduleData.getFinanceMain().getRepayBaseRate());
					lc.setParent(item);

					lc = new Listcell(String.valueOf(finScheduleData.getFinanceMain().getRepayProfitRate()));
					lc.setParent(item);

					if (finServiceInstruction.getRepayFrq() != null) {
						freqDetail = FrequencyUtil.getFrequencyDetail(finServiceInstruction.getRepayFrq());
						lc = new Listcell(freqDetail.getFrequencyDescription());
					} else {
						lc = new Listcell("");
					}
					lc.setParent(item);

					if (finServiceInstruction.getRepayPftFrq() != null) {
						freqDetail = FrequencyUtil.getFrequencyDetail(finServiceInstruction.getRepayPftFrq());
						lc = new Listcell(freqDetail.getFrequencyDescription());
					} else {
						lc = new Listcell("");
					}
					lc.setParent(item);

					if (finServiceInstruction.getRepayRvwFrq() != null) {
						freqDetail = FrequencyUtil.getFrequencyDetail(finServiceInstruction.getRepayRvwFrq());
						lc = new Listcell(freqDetail.getFrequencyDescription());
					} else {
						lc = new Listcell("");
					}

					lc.setParent(item);
					if (finServiceInstruction.getRepayCpzFrq() != null) {
						freqDetail = FrequencyUtil.getFrequencyDetail(finServiceInstruction.getRepayCpzFrq());
						lc = new Listcell(freqDetail.getFrequencyDescription());
					} else {
						lc = new Listcell("");
					}
					lc.setParent(item);
					lc = new Listcell(finServiceInstruction.getBaseRate());
					lc.setParent(item);
					lc = new Listcell(String.valueOf(finServiceInstruction.getActualRate()));
					lc.setParent(item);
					listBox.appendChild(item);
				}

				if (FinanceConstants.FINSER_EVENT_CHGRPY.equals(finEvent)) {
					Listitem item = new Listitem();
					Listcell lc;
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getFromDate()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getToDate()));
					lc.setParent(item);
					lc = new Listcell(PennantAppUtil.amountFormate(finServiceInstruction.getAmount(), 2));
					lc.setParent(item);
					lc = new Listcell(PennantStaticListUtil.getlabelDesc(finServiceInstruction.getRecalType(),
							PennantStaticListUtil.getSchCalCodes()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getRecalFromDate()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getRecalToDate()));
					lc.setParent(item);
					listBox.appendChild(item);
				}

				if (FinanceConstants.FINSER_EVENT_CHANGETDS.equals(finEvent)) {
					Listitem item = new Listitem();
					Listcell lc;
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getGrcPeriodEndDate()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getNextGrcRepayDate()));
					lc.setParent(item);
					listBox.appendChild(item);
				}

				if (FinanceConstants.FINSER_EVENT_CHGSCHDMETHOD.equals(finEvent)) {
					Listitem item = new Listitem();
					Listcell lc;
					lc = new Listcell(
							PennantStaticListUtil.getlabelDesc(finScheduleData.getFinanceMain().getScheduleMethod(),
									PennantStaticListUtil.getScheduleMethods()));
					lc.setParent(item);
					lc = new Listcell(PennantStaticListUtil.getlabelDesc(finServiceInstruction.getSchdMethod(),
							PennantStaticListUtil.getScheduleMethods()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getFromDate()));
					lc.setParent(item);
					listBox.appendChild(item);
				}

				if (FinanceConstants.FINSER_EVENT_RECALCULATE.equals(finEvent)) {
					Listitem item = new Listitem();
					Listcell lc;
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getFromDate()));
					lc.setParent(item);
					lc = new Listcell(PennantAppUtil.amountFormate(finServiceInstruction.getAmount(), 2));
					lc.setParent(item);
					lc = new Listcell(PennantStaticListUtil.getlabelDesc(finServiceInstruction.getRecalType(),
							PennantStaticListUtil.getSchCalCodes()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getRecalFromDate()));
					lc.setParent(item);
					lc = new Listcell(DateUtil.formatToLongDate(finServiceInstruction.getRecalToDate()));
					lc.setParent(item);
					lc = new Listcell(finServiceInstruction.getRecordType());
					lc.setParent(item);
					lc = new Listcell(finServiceInstruction.getRecordStatus());
					lc.setParent(item);
					listBox.appendChild(item);
				}

				if (FinanceConstants.FINSER_EVENT_RESTRUCTURE.equals(finEvent)) {
					Listitem item = new Listitem();
					Listcell lc;
					RestructureDetail restructureDetail = finScheduleData.getRestructureDetail();
					if (restructureDetail != null) {
						lc = new Listcell(DateUtil.formatToLongDate(restructureDetail.getAppDate()));
						lc.setParent(item);
						lc = new Listcell(String.valueOf(restructureDetail.getRestructureReason()));
						lc.setParent(item);
						lc = new Listcell(String.valueOf(restructureDetail.getRstTypeDesc()));
						lc.setParent(item);
						lc = new Listcell(DateUtil.formatToLongDate(restructureDetail.getRestructureDate()));
						lc.setParent(item);
						lc = new Listcell(String.valueOf(restructureDetail.getEmiHldPeriod()));
						lc.setParent(item);
						lc = new Listcell(String.valueOf(restructureDetail.getPriHldPeriod()));
						lc.setParent(item);
						lc = new Listcell(String.valueOf(restructureDetail.getEmiPeriods()));
						lc.setParent(item);
						lc = new Listcell(String.valueOf(restructureDetail.getTotNoOfRestructure()));
						lc.setParent(item);
						lc = new Listcell(String.valueOf(restructureDetail.getRecalculationType()));
						lc.setParent(item);
						lc = new Listcell(DateUtil.formatToLongDate(restructureDetail.getOldMaturity()));
						lc.setParent(item);
						listBox.appendChild(item);
					}
				}
			}
		}
	}

	public void renderData(Listbox listBox, FinScheduleData finScheduleData, String moduleDefiner) {

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<FinanceMain> searchObject = new JdbcSearchObject<FinanceMain>(FinanceMain.class);
		searchObject.addTabelName("FinanceMain_AView");
		searchObject.addFilter(
				new Filter("FinReference", finScheduleData.getFinanceMain().getFinReference(), Filter.OP_EQUAL));

		List<FinanceMain> financeMains = pagedListService.getBySearchObject(searchObject);

		if (FinanceConstants.FINSER_EVENT_CHGGRCEND.equals(moduleDefiner)) {
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(DateUtil.formatToLongDate(financeMains.get(0).getGrcPeriodEndDate()));
			lc.setParent(item);
			lc = new Listcell(DateUtil.formatToLongDate(finScheduleData.getFinanceMain().getGrcPeriodEndDate()));
			lc.setParent(item);
			listBox.appendChild(item);
		} else if (FinanceConstants.FINSER_EVENT_RPYBASICMAINTAIN.equals(moduleDefiner)) {
			pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

			JdbcSearchObject<Mandate> mandateSearch = new JdbcSearchObject<Mandate>(Mandate.class);
			mandateSearch.addTabelName("Mandates_AView");
			mandateSearch.addFilterIn("MANDATEID", String.valueOf(financeMains.get(0).getMandateID()),
					String.valueOf(finScheduleData.getFinanceMain().getMandateID()));

			List<Mandate> mandates = pagedListService.getBySearchObject(mandateSearch);
			Listitem item = new Listitem();
			Listcell lc;
			String currentRpyMthd = PennantStaticListUtil.getlabelDesc(financeMains.get(0).getFinRepayMethod(),
					PennantStaticListUtil.getRepayMethods());
			lc = new Listcell(StringUtils.equals(currentRpyMthd, "") ? "N/A" : currentRpyMthd);
			lc.setParent(item);
			lc = new Listcell(String.valueOf(financeMains.get(0).getMandateID()));
			lc.setParent(item);
			lc = new Listcell(PennantStaticListUtil.getlabelDesc(finScheduleData.getFinanceMain().getFinRepayMethod(),
					PennantStaticListUtil.getRepayMethods()));
			lc.setParent(item);
			lc = new Listcell(String.valueOf(finScheduleData.getFinanceMain().getMandateID()));
			lc.setParent(item);
			String oldBankName = "N/A";
			String newBankName = "";
			if (!mandates.isEmpty()) {
				if (mandates.size() > 1) {
					oldBankName = mandates.get(0).getBankName();
					newBankName = mandates.get(1).getBankName();
				} else {
					newBankName = mandates.get(0).getBankName();
				}
			}
			lc = new Listcell(oldBankName);
			lc.setParent(item);
			lc = new Listcell(newBankName);
			lc.setParent(item);
			listBox.appendChild(item);
		}

		if (FinanceConstants.FINSER_EVENT_PLANNEDEMI.equals(moduleDefiner)) {

			pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

			JdbcSearchObject<FinPlanEmiHoliday> holidaySearch = new JdbcSearchObject<FinPlanEmiHoliday>(
					FinPlanEmiHoliday.class);
			StringBuilder oldMonths = new StringBuilder("");
			if (StringUtils.equals(finScheduleData.getFinanceMain().getPlanEMIHMethod(),
					FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				holidaySearch.addTabelName("FinPlanEMIHMonths");
				holidaySearch.addFilterEqual("FinReference", finScheduleData.getFinanceMain().getFinReference());

				List<FinPlanEmiHoliday> holidays = pagedListService.getBySearchObject(holidaySearch);

				for (int j = 0; j < holidays.size(); j++) {
					if (StringUtils.equals("", oldMonths.toString())) {
						oldMonths = oldMonths.append(getMonthForInt(holidays.get(j).getPlanEMIHMonth()));
					} else {
						oldMonths = oldMonths.append(" , ").append(getMonthForInt(holidays.get(j).getPlanEMIHMonth()));
					}
				}

				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(oldMonths.toString());
				lc.setParent(item);

				StringBuilder newMonths = new StringBuilder("");
				for (int k = 0; k < finScheduleData.getPlanEMIHmonths().size(); k++) {
					if (StringUtils.equals("", newMonths.toString())) {
						newMonths = newMonths.append(getMonthForInt(finScheduleData.getPlanEMIHmonths().get(k)));
					} else {
						newMonths = newMonths.append(" , ")
								.append(getMonthForInt(finScheduleData.getPlanEMIHmonths().get(k)));
					}
				}
				lc = new Listcell(newMonths.toString());
				lc.setParent(item);
				listBox.appendChild(item);
			} else if (StringUtils.equals(finScheduleData.getFinanceMain().getPlanEMIHMethod(),
					FinanceConstants.PLANEMIHMETHOD_ADHOC)) {

			}
		}
	}

	public static String getMonthForInt(int m) {
		String month = "invalid";
		DateFormatSymbols dfs = new DateFormatSymbols();
		String[] months = dfs.getMonths();
		m = m - 1;
		if (m >= 0 && m <= 11) {
			month = months[m];
		}
		return month;
	}

}