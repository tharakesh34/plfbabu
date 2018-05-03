package com.pennant.webui.masters.calendar.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.calendar.api.CalendarEvent;
import org.zkoss.calendar.api.RenderContext;
import org.zkoss.calendar.impl.SimpleCalendarModel;

/**
 * Item renderer for listitems in the listbox.
 */
public class HolidayCalendarModelRenderer extends SimpleCalendarModel implements Serializable {
	private static final long	serialVersionUID	= -2365559110743187158L;
	private static final Logger	logger				= Logger.getLogger(HolidayCalendarModelRenderer.class);

	private String				filterText			= "";

	public HolidayCalendarModelRenderer(List<CalendarEvent> calendarEvents) {
		super(calendarEvents);
	}

	public void setFilterText(String filterText) {
		this.filterText = filterText;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CalendarEvent> get(Date beginDate, Date endDate, RenderContext rc) {
		logger.debug("Entering ");

		List<CalendarEvent> list = new LinkedList<CalendarEvent>();
		long begin = beginDate.getTime();
		long end = endDate.getTime();
		Iterator<CalendarEvent> i$ = _list.iterator();

		do {
			if (!i$.hasNext()) {
				break;
			}
			CalendarEvent ce = (CalendarEvent) i$.next();
			long b = ce.getBeginDate().getTime();
			long e = ce.getEndDate().getTime();
			if (e >= begin && b < end && ce.getContent().toLowerCase().contains(filterText.toLowerCase())) {
				list.add(ce);
			}
		} while (true);

		return list;
	}
}
