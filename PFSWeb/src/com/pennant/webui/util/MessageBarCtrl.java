package com.pennant.webui.util;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.messages.OfflineUsersMessagesBackup;
import com.pennant.backend.service.messages.MessagesService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.policy.model.UserImpl;

 /**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  MessageBarCtrl.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  8-09-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 8-09-2011	      Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
public class MessageBarCtrl extends   GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 5633232048842356789L;
	private final static Logger logger = Logger.getLogger(SendMessageDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window            winMessageBar; // autoWired
	private Toolbarbutton       btnOpenMsg;
	private Window              msgWindow = null;
	private String              msg = "";
	private String              userName;
	List<SecurityRole>          listSecRoles =null;
	private MessagesService     messagesService;

	public MessageBarCtrl() {
		super();
	}
	
	/**
	 * This method Automatically calls by ZK 	 */
	@SuppressWarnings("unchecked")
	@Override
	public void doAfterCompose(Component window) throws Exception {
		logger.debug("Entering");
		super.doAfterCompose(window);
		try {
			userName=((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
			listSecRoles=((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecurityRole();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Listener for incoming messages ( scope=APPLICATION )
		EventQueues.lookup("messagePopUpEventQueue", EventQueues.APPLICATION, true).subscribe(new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				final Object[] paramertsMap = (Object[]) event.getData();
				HashMap<String, String> messgeMap = (HashMap<String, String>)paramertsMap[1];
				boolean showMessageToUser =false;
				String messageGetKey="";

				if(paramertsMap[0].equals("AllUsers")){          //If Message To All Users 
					showMessageToUser=true;
					messageGetKey ="AllUserAndRoles";
				}else if(paramertsMap[0].equals("ByUsers")){    //If Message To Specific Users 
					showMessageToUser=messgeMap.containsKey(userName);
					messageGetKey=String.valueOf(userName);
				}else if(paramertsMap[0].equals("ByRoles")){   //If Message To Roles 
					if( listSecRoles!=null){
						for(SecurityRole secRole:listSecRoles){
							if(messgeMap.containsKey(secRole.getRoleCd())){
								messageGetKey=secRole.getRoleCd();
								showMessageToUser=true;
								break;
							}
						}
					}
				}
				// Check if empty and Message is Related to User, than do not show incoming message
				if (!showMessageToUser || StringUtils.isEmpty(messgeMap.get(messageGetKey))) {
					return;
				}
				setMsg(messgeMap.get(messageGetKey));
				if (msgWindow == null) {
					//getMsgWindow();
					//((Text box) getMsgWindow().getFellow("tb")).setValue(getMsg()); 
					if(!paramertsMap[2].equals(userName)){
						MessageBarCtrl.this.btnOpenMsg.setImage("/images/icons/incoming_message1_16x16.gif");
					}
				} else {
					((Textbox) getMsgWindow().getFellow("tb")).setValue(getMsg());
				}
			}
		});
		logger.debug("Leaving");
	}

	/**
	 * On Create Window Method for Loading Message bar
	 */
	public void onCreate$winMessageBar(Event event) {
		logger.debug("Entering" + event.toString());

		final Grid grid = new Grid();
		grid.setHeight("100%");
		grid.setWidth("100%");
		grid.setParent(this.winMessageBar);

		final Columns columns = new Columns();
		columns.setSizable(false);
		columns.setParent(grid);

		// Column for the Message buttons
		Column statusBarMessageIndicator = new Column();
		statusBarMessageIndicator.setWidth("20%");
		statusBarMessageIndicator.setStyle("background-color: #D6DCDE; padding: 0px");
		statusBarMessageIndicator.setParent(columns);
		// Column for the Middle Space 
		Column statusBarMiddleSpace = new Column();
		statusBarMiddleSpace.setWidth("57%");
		statusBarMiddleSpace.setStyle("background-color: #D6DCDE; padding: 0px");
		statusBarMiddleSpace.setParent(columns);
		// Column for the Version Details 
		Column statusBarVersionIndicator = new Column();
		statusBarVersionIndicator.setWidth("20%");
		Label versionLabel=new Label(PennantConstants.PROJECT_VERSION);
		statusBarVersionIndicator.appendChild(versionLabel);
		statusBarVersionIndicator.setStyle("background-color: #D6DCDE; padding: 0px");
		statusBarVersionIndicator.setParent(columns);

		// Column for the Host Details 
	    Column hostEnq = new Column();
	    Image hostStatus =  new Image("/images/Pennant/HostUp.jpg");
	    hostStatus.setTooltiptext(Labels.getLabel("label_HostStatus"));
	    hostStatus.setParent(hostEnq);
	    hostEnq.setParent(columns);
	    
		Div div = new Div();
		div.setStyle("padding: 1px;");
		div.setParent(statusBarMessageIndicator);
		div.appendChild(new Label(getMsg()));

		// open message button
		this.btnOpenMsg = new Toolbarbutton();
		this.btnOpenMsg.setWidth("20px");
		this.btnOpenMsg.setHeight("20px");
		this.btnOpenMsg.setImage("/images/icons/message2_16x16.gif");
		this.btnOpenMsg.setTooltiptext(Labels.getLabel("common.Message.Open"));
		this.btnOpenMsg.setParent(div);
		this.btnOpenMsg.addEventListener("onClick", new EventListener<Event>() {
			@Override
			public void onEvent(Event event) throws Exception {
				// 1. Reset to normal image
				btnOpenMsg.setImage("/images/icons/message2_16x16.gif");
				// 2. open the message window
				Window win = getMsgWindow();
				Textbox t = (Textbox) win.getFellow("tb");
				t.setText(getMsg());
				Clients.scrollIntoView(t);

			}
		});

		/*Show Off line Messages when login */
		List<OfflineUsersMessagesBackup> listOfflineMessges=getMessagesService().getOfflineUsersMessagesBackupByUsrId(userName);
		StringBuffer offlineMessage = new StringBuffer();
		if(listOfflineMessges!=null && listOfflineMessges.size()>0){
			for(int i=0 ;i<listOfflineMessges.size();i++){
				offlineMessage.append(listOfflineMessges.get(i).getMessage()+ "\n");
			}
			getMsgWindow();
			this.msg=offlineMessage.toString();
			((Textbox) getMsgWindow().getFellow("tb")).setValue(getMsg()); 
		}
		//Clear Off line messages from OfflineMessagesBackup table
		getMessagesService().deleteOfflineUsersMessages(userName);
	
		logger.debug("Leaving" + event.toString());
	}


	/**
	 * This Method returns new messageWindow
	 * @return
	 */
	public Window getMsgWindow() {
		logger.debug("Entering");
		if (msgWindow == null) {
			msgWindow = new Window();
			msgWindow.setDraggable("false");
			msgWindow.setId("msgWindow");
			msgWindow.setTitle("Messages");
			msgWindow.setSizable(true);
			msgWindow.setClosable(true);
			msgWindow.setWidth("400px");
			msgWindow.setHeight("250px");
			msgWindow.setParent(winMessageBar);
			msgWindow.addEventListener("onClose", new EventListener<Event>() {
				@Override
				public void onEvent(Event event) throws Exception {
					msgWindow.detach();
					msgWindow = null;
				}
			});
			msgWindow.setPosition("bottom, left");
			Textbox tb = new Textbox();
			tb.setId("tb");
			tb.setMultiline(true);
			tb.setRows(10);
			tb.setReadonly(true);
			tb.setHeight("100%");
			tb.setWidth("98%");
			tb.setParent(msgWindow);
			msgWindow.doOverlapped();
		}
		return msgWindow;
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void setMsg(String msg) {
		this.msg = this.msg + "\n" + msg;
	}
	public String getMsg() {
		return msg;
	}

	public void setMsgWindow(Window msgWindow) {
		this.msgWindow = msgWindow;
	}
	
	public void setMessagesService(MessagesService messagesService) {
		this.messagesService = messagesService;
	}

	public MessagesService getMessagesService() {
		return messagesService;
	}
}
