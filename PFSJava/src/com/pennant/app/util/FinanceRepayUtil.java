/*package com.pennant.app.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import com.pennant.backend.dao.FinRepayQueue.FinRepayQueueDAO;
import com.pennant.backend.dao.finance.FinanceRepayPriorityDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.finance.FinanceRepayPriority;
import com.pennant.backend.model.finance.FinanceScheduleDetail;

public class FinanceRepayUtil {

	private static FinanceRepayPriorityDAO	financeRepayPriorityDAO;
	private static FinRepayQueueDAO	        finRepayQueueDAO;
	private static FinanceScheduleDetailDAO	financeScheduleDetailDAO;
	private static ArrayList<FinRepayQueue>	ListRepayQueue	= new ArrayList<FinRepayQueue>();

	public static void buildRepayQueue(Date postDate) {
		//get fin types from the finance repay priority
		getListRepayQueue().clear();
		ArrayList<FinanceRepayPriority> finRpyPriorities = new ArrayList<FinanceRepayPriority>(getFinanceRepayPriorityDAO().getFinanceRepayPriorities(""));
		//loop throw the finance types
		for (FinanceRepayPriority finRpyPri : finRpyPriorities) {
			//Process for single finance type 			
			//get schedule details by finance type and post date equal to the defer scheduled  date 
			ArrayList<FinanceScheduleDetail> scheduleDetails = new ArrayList<FinanceScheduleDetail>();
			scheduleDetails.addAll(getFinanceScheduleDetailDAO().getRepayQueueDetails(finRpyPri.getFinType(), postDate, ""));

			if (scheduleDetails.size() > 0) {
				//get repay queue details by finance type and post date equal to the repay date 
				ArrayList<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();
				finRepayQueues.addAll(getFinRepayQueueDAO().getFinRepayQueues(finRpyPri.getFinType(), postDate, ""));
				//if there are no repay queue for fin type and post date then 
				if (finRepayQueues.size() == 0) {
					//Save all
					prepareRepayQueue(scheduleDetails, finRpyPri.getFinType(), finRpyPri.getFinPriority());
					continue;
				} else {
					//loop through the schedule details	
					for (FinanceScheduleDetail finSchdDt : scheduleDetails) {
						isRpySchdExists(finSchdDt, finRpyPri.getFinType(), finRpyPri.getFinPriority(), finRepayQueues);
					}

				}

			}

		}
		if (getListRepayQueue().size() > 0) {
			saveRepayQueue(getListRepayQueue());
			getListRepayQueue().clear();
		}

	}

	private static void isRpySchdExists(FinanceScheduleDetail finSchDetails, String finType, int priority, ArrayList<FinRepayQueue> finRepayQueues) {
		//check whether repay schedule in the repay queue table or not a
		boolean exist = false;
		for (FinRepayQueue finRpyQue : finRepayQueues) {
			if (finRpyQue.getRpyDate().compareTo(finSchDetails.getDefSchdDate()) == 0 && finRpyQue.getFinPriority() == priority && finRpyQue.getFinType().equals(finType)
			        && finRpyQue.getFinReference().equals(finSchDetails.getFinReference())) {
				// if exists go to the next record otherwise
				exist = true;
				break;
			}
		}
		if (!exist) {
			//  save
			prepareRepayQueue(finSchDetails, finType, priority);
		}

	}

	//for list details
	private static void prepareRepayQueue(ArrayList<FinanceScheduleDetail> finSchdDetails, String finType, int priority) {
		for (FinanceScheduleDetail finSchDetails : finSchdDetails) {
			addToRpyQueList(doWriteDataToBean(finSchDetails, finType, priority));
		}

	}

	// for single object	
	private static void prepareRepayQueue(FinanceScheduleDetail finSchdDetails, String finType, int priority) {
		addToRpyQueList(doWriteDataToBean(finSchdDetails, finType, priority));

	}

	private static FinRepayQueue doWriteDataToBean(FinanceScheduleDetail scheduleDetail, String finType, int priority) {
		FinRepayQueue finRepayQueue = new FinRepayQueue();
		finRepayQueue.setRpyDate(scheduleDetail.getDefSchdDate());
		finRepayQueue.setFinPriority(priority);
		finRepayQueue.setFinType(finType);
		finRepayQueue.setFinReference(scheduleDetail.getFinReference());
		finRepayQueue.setSchdPft(scheduleDetail.getProfitSchd());
		finRepayQueue.setSchdPftPaid(scheduleDetail.getSchdPftPaid());
		finRepayQueue.setSchdPftBal(scheduleDetail.getProfitSchd().subtract(scheduleDetail.getSchdPftPaid()));

		finRepayQueue.setSchdPri(scheduleDetail.getPrincipalSchd());
		finRepayQueue.setSchdPriPaid(scheduleDetail.getSchdPriPaid());
		finRepayQueue.setSchdPriBal(scheduleDetail.getPrincipalSchd().subtract(scheduleDetail.getSchdPriPaid()));

		if (finRepayQueue.getSchdPftBal().compareTo(new BigDecimal(0)) == 0) {
			finRepayQueue.setSchdIsPftPaid(true);
		} else {
			finRepayQueue.setSchdIsPftPaid(false);
		}

		if (finRepayQueue.getSchdPriBal().compareTo(new BigDecimal(0)) == 0) {
			finRepayQueue.setSchdIsPriPaid(true);
		} else {
			finRepayQueue.setSchdIsPriPaid(false);
		}
		return finRepayQueue;

	}

	private static void addToRpyQueList(FinRepayQueue repayQueue) {
		getListRepayQueue().add(repayQueue);
	}

	private static void saveRepayQueue(ArrayList<FinRepayQueue> finRepayQueues) {
		for (FinRepayQueue finRepayQueue : finRepayQueues) {
			getFinRepayQueueDAO().save(finRepayQueue, "");
		}

	}

	public void setFinanceRepayPriorityDAO(FinanceRepayPriorityDAO financeRepayPriorityDAO) {
		FinanceRepayUtil.financeRepayPriorityDAO = financeRepayPriorityDAO;
	}

	public static FinanceRepayPriorityDAO getFinanceRepayPriorityDAO() {
		return financeRepayPriorityDAO;
	}

	public void setFinRepayQueueDAO(FinRepayQueueDAO finRepayQueueDAO) {
		FinanceRepayUtil.finRepayQueueDAO = finRepayQueueDAO;
	}

	public static FinRepayQueueDAO getFinRepayQueueDAO() {
		return finRepayQueueDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		FinanceRepayUtil.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public static FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setListRepayQueue(ArrayList<FinRepayQueue> toDoSaveList) {
		FinanceRepayUtil.ListRepayQueue = toDoSaveList;
	}

	public static ArrayList<FinRepayQueue> getListRepayQueue() {
		return ListRepayQueue;
	}

}
*/