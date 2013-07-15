package com.pennant.webui.masters.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.zkoss.calendar.Calendars;
import org.zkoss.calendar.api.CalendarEvent;
import org.zkoss.calendar.impl.SimpleCalendarEvent;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;

import com.pennant.app.util.HolidayUtil;
import com.pennant.backend.model.smtmasters.HolidayDetail;
import com.pennant.webui.masters.calendar.model.HolidayCalendarModelRenderer;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Masters/Calendar/HolidayCalendar.zul. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class HolidayCalendarCtrl extends GenericForwardComposer {
	
	private static final long serialVersionUID = -3455808868502431564L;
	private final static Logger logger = Logger.getLogger(HolidayCalendarCtrl.class);
	
	private static final SimpleDateFormat DefaultDateFormat = new SimpleDateFormat("yyyy/MM/dd");
	private static final SimpleDateFormat DefaultMonthFormat = new SimpleDateFormat("MMM / yyyy");
	private Menuitem currentDate;
	private Calendars calendars;
	private Window win_Calendar;

	private final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
	private West menuWest;
	private Groupbox groupboxMenu;
	private Popup dateChooserPopup;
	private HolidayCalendarModelRenderer scm;
	private Set<Integer> holidayYear = new HashSet<Integer>();;

	private List<CalendarEvent> calendarEvents = new LinkedList<CalendarEvent>();

	Tabpanel tabpanel;

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a HolidayCalendar object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$win_Calendar(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		tabpanel = (Tabpanel) win_Calendar.getParent();
		tabpanel.setVisible(false);
		loadEvents();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Loading the events for a HolidayCalendar object
	 * 
	 */
	public void loadEvents() {
		logger.debug("Entering");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(calendars.getCurrentDate());

		if (!holidayYear.contains(calendar.get(Calendar.YEAR))) {
			Object[] details = HolidayUtil.getHolidayList("GEN", calendar.get(Calendar.YEAR)).values().toArray();

			for (int i = 0; i < details.length; i++) {
				HolidayDetail holidayDetail = (HolidayDetail) details[i];
				SimpleCalendarEvent sce = new SimpleCalendarEvent();
				sce.setBeginDate(getDayStart(holidayDetail.getHoliday()));
				sce.setEndDate(getDayEnd(holidayDetail.getHoliday()));

				sce.setContent(holidayDetail.getHolidayDescription());
				if (!sce.equals(holidayDetail.getHoliDayDate())) {
					sce.setContentColor("red");
					sce.setHeaderColor("red");
					calendarEvents.add(sce);
				}
			}

			scm = new HolidayCalendarModelRenderer(calendarEvents);
			calendars.setModel(scm);

			menuWest = borderlayout.getWest();
			groupboxMenu = (Groupbox) borderlayout.getFellowIfAny("groupbox_menu");
			menuWest.setVisible(false);
			groupboxMenu.setVisible(false);
			win_Calendar.setParent(groupboxMenu.getParent());
			holidayYear.add(calendar.get(Calendar.YEAR));
		}
		currentDate.setLabel(DefaultMonthFormat.format(calendar.getTime()));
		
		logger.debug("Leaving ");
	}

	public void onClick$prevPage() {
		pageChange(1);
	}

	public void onClick$nextPage() {
		pageChange(-1);
	}

	private void pageChange(int page) {
		logger.debug("Entering");
		
		if (page > 0)
			calendars.previousPage();
		else
			calendars.nextPage();
		loadEvents();
		
		logger.debug("Leaving");
	}

	public void onClick$dateConfirm(ForwardEvent event) {
		dateChooserPopup.close();
	}

	public void onChange$dateChooser(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		
		InputEvent ie = (InputEvent) event.getOrigin();

		try {
			calendars.setCurrentDate(DefaultDateFormat.parse(ie.getValue()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		loadEvents();
		
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnClose(Event event) {
		logger.debug("Entering" + event.toString());
		menuWest.setVisible(true);
		groupboxMenu.setVisible(true);
		win_Calendar.onClose();
		Tabpanels tabpanels = (Tabpanels) tabpanel.getParent();
		Tabbox tabbox = (Tabbox) tabpanels.getParent();
		Tab tab = tabbox.getSelectedTab();
		tab.close();
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$today(Event event) {
		logger.debug("Entering" + event.toString());
		calendars.setCurrentDate(java.util.Calendar.getInstance(
				calendars.getDefaultTimeZone()).getTime());
		loadEvents();
		logger.debug("Leaving" + event.toString());
	}

	public void onRightClick$calendars(ForwardEvent event) {
	}

	private static Date getDayStart(Calendar date) {
		logger.debug("Entering");
		Calendar calendar = date;
		calendar.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
				date.get(Calendar.DATE), 01, 00, 00);
		logger.debug("Leaving");
		return calendar.getTime();
	}

	private static Date getDayEnd(Calendar date) {
		logger.debug("Entering");
		Calendar calendar = date;
		calendar.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
				date.get(Calendar.DATE), 23, 59, 59);
		logger.debug("Leaving");
		return calendar.getTime();
	}

}
