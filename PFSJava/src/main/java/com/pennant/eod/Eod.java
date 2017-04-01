package com.pennant.eod;

import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.eod.dao.EodDetailDAO;
import com.pennant.eod.model.EodDetail;

public class Eod implements ApplicationContextAware {

	private static Logger			logger		= Logger.getLogger(Eod.class);

	private ApplicationContext		applicationContext;
	private ThreadPoolTaskExecutor	threadPoolTaskExecutor;

	private PreEodService			preEodService;
	private PostEodService			postEodService;
	private CustomerQueuingService	customerQueuingService;

	private EodDetailDAO			eodDetailDAO;

	/**
	 * @throws Exception
	 */
	public void doProcess() throws Exception {
		logger.debug("Entering");

		Date appDate = DateUtility.getAppDate();

		getPreEodService().doProcess(appDate);

		doEod(appDate);

		logger.debug("Leaving");
	}

	/**
	 * @param date
	 * @throws Exception
	 */
	private void doEod(Date date) throws Exception {
		boolean recordslessThanThread = false;
		int threadCount = SysParamUtil.getValueAsInt("EOD_THREAD_COUNT");

		long custIdCount = getCountbyProgress(date, null);

		if (custIdCount != 0) {

			long noOfRows = custIdCount / threadCount;

			if (custIdCount < threadCount) {
				recordslessThanThread = true;
				noOfRows = 1;
			}

			for (int i = 1; i <= threadCount; i++) {
				if (i == threadCount) {
					getCustomerQueuingService().updateAll(date, "Thread" + i);
				} else {
					getCustomerQueuingService().updateNoofRows(date, noOfRows, "Thread" + i);
				}
				startThread(date, "Thread" + i);
				if (recordslessThanThread && i == custIdCount) {
					break;
				}
			}
		}

	}

	/**
	 * @param date
	 * @param threadId
	 * @throws SQLException
	 */
	private void startThread(Date date, String threadId) throws SQLException {

		EodThread eodThread = (EodThread) applicationContext.getBean("eodThread");
		eodThread.setThreadId(threadId);
		eodThread.setEodDate(date);
		threadPoolTaskExecutor.execute(eodThread);
	}

	/**
	 * @param date
	 * @param progress
	 * @return
	 */
	public long getCountbyProgress(Date date, String progress) {
		return getCustomerQueuingService().getCountbyProgress(date, progress);
	}
	
	/**
	 * @param date
	 * @param progress
	 * @return
	 */
	public long getCountByStatus(Date date, String progress) {
		return getCustomerQueuingService().getCountByStatus(date, progress);
	}

	/**
	 * @param date
	 * @return
	 */
	public EodDetail getEodDetailById(Date date) {
		return getEodDetailDAO().getEodDetailById(date);
	}

	/**
	 * @param eodDetail
	 */
	public void save(EodDetail eodDetail) {
		getEodDetailDAO().save(eodDetail);

	}

	public CustomerQueuingService getCustomerQueuingService() {
		return customerQueuingService;
	}

	public void setCustomerQueuingService(CustomerQueuingService customerQueuingService) {
		this.customerQueuingService = customerQueuingService;
	}

	public ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
		return threadPoolTaskExecutor;
	}

	public void setThreadPoolTaskExecutor(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
		this.threadPoolTaskExecutor = threadPoolTaskExecutor;
	}

	private PreEodService getPreEodService() {
		return preEodService;
	}

	public void setPreEodService(PreEodService preEodService) {
		this.preEodService = preEodService;
	}

	public PostEodService getPostEodService() {
		return postEodService;
	}

	public void setPostEodService(PostEodService postEodService) {
		this.postEodService = postEodService;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

	}

	public EodDetailDAO getEodDetailDAO() {
		return eodDetailDAO;
	}

	public void setEodDetailDAO(EodDetailDAO eodDetailDAO) {
		this.eodDetailDAO = eodDetailDAO;
	}

}
