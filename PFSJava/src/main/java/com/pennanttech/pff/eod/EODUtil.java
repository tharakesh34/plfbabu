package com.pennanttech.pff.eod;

import java.util.Date;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennanttech.pennapps.core.util.DateUtil;

public class EODUtil {
	public static EventProperties EVENT_PROPS = new EventProperties();
	public static final String EVENT_PROPERTIES = "EVENT_PROPERTIES";
	private static boolean eod = false;
	private static boolean datesReload = true;

	private EODUtil() {
		//
	}

	public static EventProperties getEventProperties(String key, ChunkContext context) {
		ExecutionContext executionContext = checkContext(context);

		if (executionContext == null) {
			return new EventProperties();
		}

		Object object = executionContext.get(key);
		if (object == null) {
			return new EventProperties();
		}

		return (EventProperties) object;
	}

	public static Date getDate(String key, ChunkContext context) {
		ExecutionContext executionContext = checkContext(context);

		if (executionContext == null) {
			return null;
		}

		Object object = executionContext.get(key);
		if (object == null) {
			return SysParamUtil.getValueAsDate(key);
		}

		return (Date) object;
	}

	public static void updateEventProperties(ChunkContext context, EventProperties eventProperties) {
		ExecutionContext executionContext = checkContext(context);

		if (executionContext == null) {
			return;
		}

		Date appValueDate = SysParamUtil.getAppValueDate();
		Date appDate = SysParamUtil.getAppDate();
		Date postDate = SysParamUtil.getPostDate();
		Date nextDate = SysParamUtil.getValueAsDate("APP_NEXT_BUS_DATE");
		Date lastDate = SysParamUtil.getLastBusinessdate();

		executionContext.put("APP_VALUEDATE", appValueDate);
		executionContext.put("APP_DATE", appDate);
		executionContext.put("APP_NEXT_BUS_DATE", nextDate);
		executionContext.put("APP_LAST_BUS_DATE", lastDate);

		Date businessDate = DateUtil.addDays(appDate, 1);
		Date monthEndDate = DateUtil.getMonthEnd(appDate);
		Date monthStartDate = DateUtil.getMonthStart(appDate);
		Date prvMonthEndDate = DateUtil.addDays(monthStartDate, -1);

		eventProperties.setAppDate(appDate);
		eventProperties.setPostDate(postDate);
		eventProperties.setAppValueDate(appValueDate);
		eventProperties.setNextDate(nextDate);
		eventProperties.setLastDate(lastDate);

		eventProperties.setBusinessDate(businessDate);
		eventProperties.setMonthEndDate(monthEndDate);
		eventProperties.setMonthStartDate(monthStartDate);
		eventProperties.setPrvMonthEndDate(prvMonthEndDate);

		executionContext.put(EVENT_PROPERTIES, eventProperties);

		EVENT_PROPS = eventProperties;
	}

	private static ExecutionContext checkContext(ChunkContext context) {
		if (context == null) {
			return null;
		}

		StepContext stepContext = context.getStepContext();

		StepExecution stepExecution = stepContext.getStepExecution();

		JobExecution jobExecution = stepExecution.getJobExecution();

		return jobExecution.getExecutionContext();
	}

	public static void setEventProperties(ChunkContext context, EventProperties eventProperties) {
		ExecutionContext executionContext = checkContext(context);

		if (executionContext == null) {
			return;
		}

		executionContext.put(EVENT_PROPERTIES, eventProperties);
	}

	public static boolean isEod() {
		return eod;
	}

	public static void setEod(boolean eod) {
		EODUtil.eod = eod;
	}

	public static boolean isDatesReload() {
		return datesReload;
	}

	public static void setDatesReload(boolean datesReload) {
		EODUtil.datesReload = datesReload;
	}

}
