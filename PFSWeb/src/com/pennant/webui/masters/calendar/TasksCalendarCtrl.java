/*package com.pennant.webui.masters.calendar;
 
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.zkoss.calendar.Calendars;
import org.zkoss.calendar.api.CalendarEvent;
import org.zkoss.calendar.event.CalendarsEvent;
import org.zkoss.calendar.impl.SimpleCalendarEvent;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;

import com.pennant.backend.model.masters.TasksCalendar;
import com.pennant.webui.masters.calendar.model.TasksCalendarModelRenderer;

import de.jollyday.Holiday;
import de.jollyday.HolidayManager;
 
public class TasksCalendarCtrl extends GenericForwardComposer {
    *//**
	 * 
	 *//*
	private static final long serialVersionUID = -3455808868502431564L;
	private static final String CALENDAR_EVENT = "Caneldar_Event";
    private static final String CTRL_EVENT = "Ctrl_Event";
    private static final SimpleDateFormat DefaultDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private static final SimpleDateFormat DefaultMonthFormat = new SimpleDateFormat("MMM / yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private Menuitem currentDate, prevPage, nextPage;
    private Calendars calendars;
    private Window win_Calendar;
    
    private final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");  
	private West menuWest;
	private Groupbox groupboxMenu;

	private Window createEvent;
    private Popup dateChooserPopup;
    private TasksCalendarModelRenderer scm;
    private Calendar dateChooser;
    private Button dateConfirm;
    
    private Menuitem btnClose;
    
    private List<CalendarEvent> calendarEvents = new LinkedList<CalendarEvent>();
    private final SimpleDateFormat DATA_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private Calendar cal = Calendar.getInstance();
    private TasksCalendarService tasksCalendarService;
    Tabpanel tabpanel;
 
    public void onCreate$win_Calendar(Event event) throws Exception {
    
    System.out.println(win_Calendar.getParent()); 
    tabpanel = (Tabpanel) win_Calendar.getParent();
    tabpanel.setVisible(false);
    
         SimpleCalendarEvent simplecalendarevent ;
         getHolidays();
         weekend(); 
         //List allEvents = getTasksCalendarService().getAllCalendarEvents();
         List allEvents=new ArrayList();
         TasksCalendar tskcalendar;
         System.out.println(tabpanel.getParent());
         for(int i=0;i<allEvents.size();i++){
        	 
        	 tskcalendar = (TasksCalendar) allEvents.get(i);
        	 simplecalendarevent = new SimpleCalendarEvent();
        	 simplecalendarevent.setBeginDate(tskcalendar.getStartDate());
        	 Timestamp endDate = new Timestamp(tskcalendar.getStartDate().getTime());
        	 endDate.setHours(tskcalendar.getClosureDate().getHours()+1);
        	 simplecalendarevent.setEndDate(endDate);
        	 
        	 simplecalendarevent.setContent(tskcalendar.getRemarks());
        	 
        	 if(tskcalendar.getStatus().trim().equalsIgnoreCase("CLOSED")){
        		 simplecalendarevent.setContentColor("#004040");	
            	 simplecalendarevent.setHeaderColor("#004040");
        	 }
        	 if(tskcalendar.getStatus().trim().equalsIgnoreCase("HOLD")){
        		 simplecalendarevent.setContentColor("#C0C0C0");	 
            	 simplecalendarevent.setHeaderColor("#C0C0C0");
        	 }
        	 if(tskcalendar.getStatus().trim().equalsIgnoreCase("OPEN")){
        		 simplecalendarevent.setContentColor("#CCCCCC");	 
            	 simplecalendarevent.setHeaderColor("#CCCCCC");
        	 }
          	 if(tskcalendar.getStatus().trim().equalsIgnoreCase("WIP")){
        		 simplecalendarevent.setContentColor("#0055CC");	 
            	 simplecalendarevent.setHeaderColor("#0055CC");
        	 }
          	
             simplecalendarevent.setTitle(String.valueOf(tskcalendar.getTaskReference()));
             simplecalendarevent.setLocked(false);
       
        	 calendarEvents.add(simplecalendarevent);	
        }
        
        scm = new TasksCalendarModelRenderer(calendarEvents);
        calendars.setModel(scm);
        currentDate.setLabel(DefaultMonthFormat.format(new Date()));
      //  timeFormat.setTimeZone(calendars.getDefaultTimeZone());
        
        menuWest = borderlayout.getWest();
		groupboxMenu = (Groupbox) borderlayout.getFellowIfAny("groupbox_menu");
		menuWest.setVisible(false);
		groupboxMenu.setVisible(false);
		win_Calendar.setParent(groupboxMenu.getParent()); 
 
    }
    
    //Get the List Of holidays Of The given Year
    public ArrayList<String> getHolidays()
    	{
    	Calendar calendar=Calendar.getInstance();
    	int iYear=calendar.get(Calendar.YEAR);
		ArrayList<String> oSortedHolidays = new ArrayList<String>();
		try
		{
			HolidayManager oManager = HolidayManager.getInstance("in");

			Set<Holiday> oHolidays = oManager.getHolidays(iYear, "test"); 
			for(Holiday oHoliday: oHolidays)
			{
				oSortedHolidays.add(oHoliday.toString());
			}

			// Sorted holiday dates.
			Collections.sort(oSortedHolidays);
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}

		return oSortedHolidays;
	}
    
    //Get the Weekends
    public int weekend(){
    	Calendar calendar=Calendar.getInstance();
    	int year=calendar.get(Calendar.YEAR);
    	int weekday=calendar.get(Calendar.DAY_OF_WEEK);
    	if (weekday==1 || weekday==7){
    		return weekday;
    	}
    		return 0;
    	}
 
    // Filter Events
    public void onEventFilter(ForwardEvent event) {
        Map args = new HashMap();
        args.put("model", scm);
        args.put("calendars", calendars);
        args.put("target", ((MouseEvent) event.getOrigin()).getTarget());
        Executions.createComponents("/WEB-INF/pages/Masters/Calendar/filter.zul", calendars.getParent(), args);
    }
 
    // Handle Client Event "onMoveDate", triggered by mouse scroll
    public void onMoveDate$calendars(ForwardEvent event) {
        Event mevt = event.getOrigin();
        pageChange(Integer.parseInt(mevt.getData().toString()));
    }
 
    public void onClick$prevPage() {
        pageChange(1);
    }
 
    public void onClick$nextPage() {
        pageChange(-1);
    }
 
    // Edit exists event
    public void onEventEdit$calendars(ForwardEvent event) throws NumberFormatException, Exception {

    	CalendarsEvent evt = (CalendarsEvent) event.getOrigin();
    	CalendarEvent cevt1 = evt.getCalendarEvent();
    	//Get the latest details by passing the primary key to the server
    	
	    //showDetailView(getTasksCalendarService().getCalendarEventById(Long.parseLong(cevt1.getTitle())));
    }
    
   private void onChange$btnClose(Event event){
	
   }
    private void showDetailView(TasksCalendar taskCalendar) throws Exception {
		
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("taskCalendar", taskCalendar);
		
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the invoiceHeaderListbox from the
		 * dialog when we do a delete, edit or insert a invoiceHeader.
		 
		map.put("tasksCalendarCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Masters/Calendar/TaskCalendarDialog.zul", null, map);
		} catch (final Exception e) {
			 
		}
	}

    private void pageChange(int page) {
        if (page > 0)
            calendars.previousPage();
        else
            calendars.nextPage();
        currentDate.setLabel(DefaultMonthFormat.format(calendars.getCurrentDate()));
    }
    public void onClick$dateConfirm(ForwardEvent event){
        dateChooserPopup.close();
    }
    public void onChange$dateChooser(ForwardEvent event) {
        InputEvent ie = (InputEvent) event.getOrigin();
        try {
            calendars.setCurrentDate(DefaultDateFormat.parse(ie.getValue()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public void onClick$btnClose(Event event){
    	menuWest.setVisible(true);
		groupboxMenu.setVisible(true);
		 win_Calendar.onClose();   
	 		Tabpanels tabpanels = (Tabpanels) tabpanel.getParent();
	  		Tabbox tabbox = (Tabbox) tabpanels.getParent();
	  		Tab tab = tabbox.getSelectedTab();
	  	  	 tab.close();
    }
    
    public void onClick$today(Event event){
    	
    	calendars.setCurrentDate(java.util.Calendar.getInstance(calendars.getDefaultTimeZone()).getTime());
        }
    
   public void onClick$day_view(Event event){
        calendars.setMold("default");
	   calendars.setDays(1);
        }
   public void onClick$week_view(Event event){
       calendars.setMold("default");
	   calendars.setDays(7);
       } 
   public void onClick$month_view(Event event){
       calendars.setMold("month");
	   } 
   
    private Calendar setCalendar(Calendar cal, int hod, int min, int sec, int milsec) {
        cal.set(Calendar.HOUR_OF_DAY, hod);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, sec);
        cal.set(Calendar.MILLISECOND, milsec);
        return cal;
    }
 
    // Count popup position prevent out of browser view
    private Integer[] getPopupPosition(CalendarsEvent evt) {
        int left = evt.getX();
        int top = evt.getY();
 
        if (top + 245 > evt.getDesktopHeight())
            top = evt.getDesktopHeight() - 245;
        if (left + 410 > evt.getDesktopWidth())
            left = evt.getDesktopWidth() - 410;
        return new Integer[] { left, top };
    }
    
    
    //Create a holiday event
    public void onEventCreate$calendars(ForwardEvent event) {
		CalendarsEvent evt = (CalendarsEvent) event.getOrigin();
		int left = evt.getX();
		int top = evt.getY();
		if (top + 245 > evt.getDesktopHeight())
			top = evt.getDesktopHeight() - 245;
		if (left + 410 > evt.getDesktopWidth())
			left = evt.getDesktopWidth() - 410;
		createEvent.setLeft(left + "px");
		createEvent.setTop(top + "px");
		SimpleDateFormat create_sdf = new SimpleDateFormat("HH:mm");
		create_sdf.setTimeZone(calendars.getDefaultTimeZone());
		String[] times = create_sdf.format(evt.getBeginDate()).split(":");
		
		createEvent.setVisible(true);
		createEvent.setAttribute("calevent", evt);
		evt.stopClearGhost();
	}
	
	public void onClose$createEvent(ForwardEvent event) {
		event.getOrigin().stopPropagation();
		((CalendarsEvent)createEvent.getAttribute("calevent")).clearGhost();
		createEvent.setVisible(false);
	}
    
    public void weekend(){
	Calendar calendar=Calendar.getInstance();
	int year=calendar.get(Calendar.YEAR);
	int weekday=calendar.get(Calendar.DAY_OF_WEEK);
	if (weekday==1 || weekday==7){
		
	}
	}
    
	public void setTasksCalendarService(TasksCalendarService tasksCalendarService) {
		this.tasksCalendarService = tasksCalendarService;
	}

	public TasksCalendarService getTasksCalendarService() {
		return this.tasksCalendarService;
	}
    
}*/