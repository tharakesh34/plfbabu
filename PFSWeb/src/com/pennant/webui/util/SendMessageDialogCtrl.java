
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
 * FileName    		:  CollectionManagerDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-09-2012    														*
 *                                                                  						*
 * Modified Date    :  14-09-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-09-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.util;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SessionUtil;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserRoles;
import com.pennant.backend.model.messages.OfflineUsersMessagesBackup;
import com.pennant.backend.model.messages.UserContactsList;
import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.backend.service.messages.MessagesService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.policy.model.UserImpl;
import com.pennant.search.SearchResult;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.searchdialogs.ExtendedMultipleSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Messages/SendMessageDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SendMessageDialogCtrl  extends  GFCBaseListCtrl<ReportConfiguration>  implements Serializable {

	private static final long serialVersionUID = -7028973478971693678L;
	private final static Logger logger = Logger.getLogger(SendMessageDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	 window_UserDialog;          // autoWired
	protected Space 	 space_UserId; 				 // autoWired
	protected Textbox 	 messageBox; 	             // autoWired
	protected Button     btnsave;                    // autoWired
	protected Groupbox 	 gb_statusDetails;
	protected Groupbox 	 groupboxWf;
	protected South 	 south;

	// ServiceDAOs / Domain Classes
	private   String             searchKey="ByUsers";
	protected Radiogroup         rg_Selection;
	private   String             userName;
	private   Listbox            listBoxOnlineUsers;
	private   Listbox            listBoxAllRoles;
	protected Timer              refreshTimer;
	private   MessagesService    messagesService;
	private   Panel              panel_OnlineUsers;
	private   Panel              panel_Roles;
	// not auto wired variables

	protected Map<String, Object> dataMap = new HashMap<String, Object>();
	protected Map<String, Object> tempdataMap = new HashMap<String, Object>();

	private String customColumns  ="DISTINCT (LovDescUserLogin)";
	/**
	 * default constructor.<br>
	 */
	public SendMessageDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityUser object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_UserDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		try {
			// get the parameters map that are overHanded by creation.
			int delayTime = 1 * 60 * 1000;
			this.refreshTimer.setDelay(delayTime);
			userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
			getBorderLayoutHeight();
			this.listBoxAllRoles.setHeight(calculateBorderLayoutHeight()-275+"px");
			this.listBoxOnlineUsers.setHeight(calculateBorderLayoutHeight()-275+"px");
			
			doFillAllRolesList();
			doFillContactsList(); 
			doShowOnlineUsers();
		} catch (Exception e) {
			logger.error("Error while creating window "+e.toString());
			window_UserDialog.onClose();
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when panel_Roles is opened
	 * @param event
	 */
	public void onOpen$panel_Roles(Event event){
		logger.debug("Entering" + event.toString());
		searchKey="ByRoles";
		doResetListBoxs(this.listBoxOnlineUsers);
		this.panel_OnlineUsers.setOpen(false);
		this.rg_Selection.setSelectedIndex(1);
		logger.debug("Leaving" + event.toString());

	}

	/**
	 * when panel_OnlineUsers is opened
	 * @param event
	 */
	public void onOpen$panel_OnlineUsers(Event event){
		logger.debug("Entering" + event.toString());
		searchKey="ByUsers";
		doResetListBoxs(this.listBoxAllRoles);
		this.panel_Roles.setOpen(false);
		this.rg_Selection.setSelectedIndex(0);
		logger.debug("Leaving" + event.toString());

	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.messageBox.setValue("");
		dataMap.clear();
		logger.debug("Leaving");
	}
	/**
	 *This method resets the list box <br>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void doResetListBoxs(Listbox listBox){
		logger.debug("Entering");
		Set seletedSet=listBox.getSelectedItems();	
		List list=new ArrayList(seletedSet);
		Iterator it=list.iterator();
		while(it.hasNext()){
			Listitem listItem =(Listitem)it.next();	
			listItem.setSelected(false);
		}
		logger.debug("Leaving");
	}


	/**	
	 * This Method/Event for getting the selected level details(code,Name) to showing the values in LOV window  
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onClick$btn_AddUser(Event event){
		logger.debug("Entering" + event.toString());
		StringBuffer contactsList =new StringBuffer("");
		tempdataMap.clear();
		dataMap= (Map<String, Object>) ExtendedMultipleSearchListBox.show(this.window_UserDialog,"SecurityUsers",tempdataMap);

		if (dataMap!= null && dataMap.size()>0) {
			for(String key :dataMap.keySet()) {
				if(this.listBoxOnlineUsers.getFellowIfAny(((SecurityUser) dataMap.get(key)).getUsrLogin())==null
						&& !((SecurityUser) dataMap.get(key)).getUsrLogin().equals(userName)){
					Listitem item=new Listitem(); //To Create List item
					Listcell listCell;
					Label label =new Label(((SecurityUser) dataMap.get(key)).getUsrLogin());
					listCell=new Listcell();
					listCell.appendChild(label);
					listCell.setParent(item);	
					item.setId(String.valueOf(((SecurityUser) dataMap.get(key)).getUsrLogin()));
					this.listBoxOnlineUsers.appendChild(item);

				}
			}
			doSaveContactsList(contactsList);
		}
		doShowOnlineUsers();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When user clicks on "btn_DeleteUser" button
	 * @param event
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onClick$btn_DeleteUser(Event event){
		logger.debug("Entering" + event.toString());
		Set seletedSet=this.listBoxOnlineUsers.getSelectedItems();	
		List list=new ArrayList(seletedSet);
		Iterator it=list.iterator();
		while(it.hasNext()){
			Listitem listItem =(Listitem)it.next();	
			this.listBoxOnlineUsers.removeItemAt(listItem.getIndex()); 
		}
		StringBuffer contactsList =new StringBuffer("");
		doSaveContactsList(contactsList);

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method saves contact list of user
	 * @param contactsList
	 */
	private void doSaveContactsList(StringBuffer contactsList) {
		logger.debug("Entering");
		if(contactsList.equals("")){
			contactsList.deleteCharAt(contactsList.lastIndexOf(","));
		}
		for(int i = 0;i<this.listBoxOnlineUsers.getItems().size();i++){
			Listitem item =(Listitem) this.listBoxOnlineUsers.getItems().get(i);
			contactsList.append(item.getId()+",");
		}
		UserContactsList userContact =new UserContactsList();
		userContact.setUsrID(userName);
		userContact.setType("USERS");
		userContact.setGroupName("");
		userContact.setContactsList(contactsList.toString());
		getMessagesService().deleteUserContactsList(userContact.getUsrID(), userContact.getType());
		getMessagesService().saveUserContactsList(userContact);
		logger.debug("Leaving");
	}

	/**
	 * When user select radio group
	 * @param event
	 */
	public void onCheck$rg_Selection(Event event){
		logger.debug("Entering" + event.toString());
		dataMap.clear();
		if(this.rg_Selection.getSelectedItem().getId().equals("radio_Users")){
			searchKey="ByUsers";
			doResetListBoxs(this.listBoxAllRoles);
			this.panel_Roles.setOpen(false);
			this.panel_OnlineUsers.setOpen(true);
		}else   if(this.rg_Selection.getSelectedItem().getId().equals("radio_Roles")){
			searchKey="ByRoles";
			doResetListBoxs(this.listBoxOnlineUsers);
			this.panel_OnlineUsers.setOpen(false);
			this.panel_Roles.setOpen(true);
		}
		logger.debug("Leaving" + event.toString());		
	}
	/**
	 * When user Click on send button.Publish the message and capture off line users and save message in "OfflineMessagebackup"  table 
	 * for show message when they login
	 * @param event
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onClick$sendMessage(Event event) throws Exception{
		logger.debug("Entering" + event.toString());
		if(!this.messageBox.getValue().trim().equals("")){
			Object parameters[]=  new Object[3];
			parameters[0]=searchKey;
			parameters[2]=userName;
			HashMap<String,String> messageRecivers=new HashMap<String, String>();
			String msg="";
			msg="Date : "+PTDateFormat.getDateFormater().format(new Date())+ "\t"+"\t"+"\t"+"Time : "+PTDateFormat.getTimeLongFormater().format(new Date())+"\n";
			msg = msg +Labels.getLabel("common.From")
			+ " : " + userName + "\n"+ "\n";
			msg = msg +"Message : "+"\n"+"\n";
			msg = msg +this.messageBox.getValue()+ "\n"+ "\n";
			msg = msg  + "*******************   END  ********************* " + "\n";
			

			Set  seletedSet=null;
			boolean isByUsers=false;
			if(this.rg_Selection.getSelectedItem().getId().equals("radio_Users")){
				isByUsers=true;
				seletedSet=this.listBoxOnlineUsers.getSelectedItems();	
			}else{
				seletedSet=this.listBoxAllRoles.getSelectedItems();	
			}
			List list=new ArrayList(seletedSet);
			Iterator it=list.iterator();
			while(it.hasNext()){
				Listitem listItem =(Listitem)it.next();	
				messageRecivers.put(isByUsers==true?listItem.getId():listItem.getId().substring(5),msg);

			}
			parameters[1]=messageRecivers;
			//Publish the message 
			/*This Event is subscribed in  MessageBarCtrl onCreateWindow Event @See MessageBarCtrl.java*/
			EventQueues.lookup("messagePopUpEventQueue", EventQueues.APPLICATION, true)
			.publish(new Event("onMessagePopEventQueue", null, parameters));

			//Save Off line messages
			doClear();
			/*Save Message in Database for off line users */
			Map<String,Object> currentLoginUsers =SessionUtil.getCurrentLoginUsers();
			List<OfflineUsersMessagesBackup> offlineMsgsList =new ArrayList<OfflineUsersMessagesBackup>();
			boolean  isUserOffline =false;
			Boolean  isDeskTopActive =false;
			Map<String, Boolean> onLineAndActiveDesktopUsersMap = getActiveUsersMap();

			//Send message to Users
			if(searchKey.equals("ByUsers")){
				doResetListBoxs(this.listBoxOnlineUsers);
				for(String userLogin: messageRecivers.keySet()){
					OfflineUsersMessagesBackup offlineUsersMessagesBackup =new OfflineUsersMessagesBackup();
					isUserOffline = !currentLoginUsers.containsKey(userLogin);//get current login users 
					isDeskTopActive =onLineAndActiveDesktopUsersMap.get(userLogin);     //get current login users whose desktop is available
					// means session is active and index page is opened 
					if(isUserOffline || ( isDeskTopActive !=null && !isDeskTopActive)){
						offlineUsersMessagesBackup.setFromUsrID(userName);
						offlineUsersMessagesBackup.setToUsrID(userLogin);
						offlineUsersMessagesBackup.setSendTime(DateUtility.today());
						offlineUsersMessagesBackup.setMessage(msg);
						offlineMsgsList.add(offlineUsersMessagesBackup);
					}	
				}
			}else if(searchKey.equals("ByRoles")){	//Send message to Roles
				doResetListBoxs(this.listBoxAllRoles);
				if(messageRecivers.size()>0){
					StringBuffer  roleIDs=new StringBuffer(" in (");
					for(String roleID : messageRecivers.keySet()){
						roleIDs.append(" '"+roleID+"'");
						roleIDs.append(" ,");
					}
					roleIDs=roleIDs.replace( roleIDs.length()-1, roleIDs.length(), " ) " );

					// ++ create the searchObject and init sorting ++//
					JdbcSearchObject<SecurityUserRoles> searchObj = new JdbcSearchObject<SecurityUserRoles>(SecurityUserRoles.class);
					/*searchObj.addSort("usrId", false);*/
					searchObj.addTabelName("UserRoles_AView");
					searchObj.addFields(PennantAppUtil.getCustomColumns(customColumns));
					searchObj.addWhereClause("  LovDescRoleCd  "+ roleIDs.toString() );
					final SearchResult<SecurityUserRoles> searchResult =getPagedListWrapper()
					.getPagedListService().getSRBySearchObject(searchObj);
					for(int i=0;i<searchResult.getResult().size();i++){
						SecurityUserRoles secUserRoles = searchResult.getResult().get(i);
						isUserOffline = !currentLoginUsers.containsKey(secUserRoles.getLovDescUserLogin());
						isDeskTopActive = onLineAndActiveDesktopUsersMap.get(secUserRoles.getLovDescUserLogin());
						if(isUserOffline || ( isDeskTopActive !=null && !isDeskTopActive)){
							OfflineUsersMessagesBackup offlineUsersMessagesBackup =new OfflineUsersMessagesBackup();
							offlineUsersMessagesBackup.setFromUsrID(userName);
							offlineUsersMessagesBackup.setToUsrID(secUserRoles.getLovDescUserLogin());
							offlineUsersMessagesBackup.setSendTime(DateUtility.today());
							offlineUsersMessagesBackup.setMessage(msg);
							offlineMsgsList.add(offlineUsersMessagesBackup);
						}
					}
					searchObj=null;
				}
			}
			if(offlineMsgsList.size()>0){
				doSaveOfflineMessages(offlineMsgsList);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Get Current Active users Map
	 * @return  Map<String, Boolean> actualMap
	 */
	private Map<String, Boolean> getActiveUsersMap() {
		Map<String, Boolean> onLineAndActiveDesktopUsersMap = new HashMap<String, Boolean>();
		for(String userID :SessionUtil.getCurrentLoginUsers().keySet()){
			
			if(SessionUtil.getActiveDeskTopsMap().containsKey(userID)) {
				onLineAndActiveDesktopUsersMap.put(userID,  SessionUtil.getActiveDeskTopsMap().get(userID));
			} else {
				onLineAndActiveDesktopUsersMap.put(userID,  true);
			}
			/*if(!onLineAndActiveDesktopUsersMap.containsKey(userID.substring(0, userID.indexOf("-")))
					||(onLineAndActiveDesktopUsersMap.containsKey(userID.substring(0, userID.indexOf("-")))
							&& onLineAndActiveDesktopUsersMap.get(userID.substring(0, userID.indexOf("-")))==false)){
				onLineAndActiveDesktopUsersMap.put(userID.substring(0, userID.indexOf("-")), SessionUtil.getActiveDeskTopsMap().get(userID));
			}*/


		}
		return onLineAndActiveDesktopUsersMap;
	}

	/**
	 * This method returns All Roles List
	 * @return
	 * @throws Exception
	 */
	private SearchResult<SecurityRole> getSecRolesList() throws Exception {
		JdbcSearchObject<SecurityRole> searchObj = new JdbcSearchObject<SecurityRole>(SecurityRole.class);
		searchObj.addSort("RoleCd", false);
		searchObj.addTabelName("SecRoles");
		final SearchResult<SecurityRole> searchResult =getPagedListWrapper().
		getPagedListService().getSRBySearchObject(searchObj);
		return searchResult;
	}
	/**
	 * Save the Off line message list
	 * @param offlineMsgsList
	 */
	public void doSaveOfflineMessages(List<OfflineUsersMessagesBackup> offlineMsgsList){
		logger.debug("Entering");
		getMessagesService().saveOfflineUsersMessages( offlineMsgsList);
		logger.debug("Leaving");
	}
	/**
	 * This method Fills All Contact Users List
	 */
	private void doFillContactsList(){
		logger.debug("Entering");
		UserContactsList  userContactsList=getMessagesService().getUserContactsList(userName,"USERS");
		if(userContactsList!=null){
			String contacts[] =userContactsList.getContactsList().split(",");
			for(int i=0;i<contacts.length;i++){
				if(!contacts[i].trim().equals("")){
					Listitem item=new Listitem(); //To Create List item
					Listcell listCell;
					Label label =new Label(contacts[i]);
					listCell=new Listcell();
					item.setId(contacts[i]);
					listCell.appendChild(label);
					listCell.setParent(item);	
					this.listBoxOnlineUsers.appendChild(item);
				}
			}
		}
		logger.debug("Leaving");
	}
	/**
	 * This method fills all roles List
	 * @throws Exception
	 */
	private void doFillAllRolesList() throws Exception{
		logger.debug("Entering");
		SearchResult<SecurityRole> secRolesList = getSecRolesList();
		this.listBoxAllRoles.getItems().clear();
		if(secRolesList!=null){
			for(int i=0;i<secRolesList.getResult().size();i++){
				Listitem item=new Listitem(); //To Create List item
				Listcell listCell;
				Label label =new Label(secRolesList.getResult().get(i).getRoleCd());
				listCell=new Listcell();
				//In WOREST case if USERLOGIN AND ROLECD MATCHED  UniqueID issue to avoid that adding  "#RL#_"
				item.setId(String.valueOf("#RL#_"+secRolesList.getResult().get(i).getRoleCd()));
				listCell.appendChild(label);
				listCell.setParent(item);	
				this.listBoxAllRoles.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * This method checks users is in online if yes sets name in green color 
	 */
	private void doShowOnlineUsers(){
		logger.debug("Entering");
		int count=0;
		Map<String, Boolean> actualMap = getActiveUsersMap();
		for(int i=0;i<this.listBoxOnlineUsers.getItems().size();i++){
			Listitem item=(Listitem) this.listBoxOnlineUsers.getItems().get(i);
			Listcell listCell = (Listcell) item.getChildren().get(0);
			Label label = (Label) listCell.getChildren().get(0);
			if( actualMap.containsKey(item.getId()) 
					&& actualMap.get(item.getId())==true){
				label.setStyle("color:Green");
				count=count+1;
			}else{
				label.setStyle("color:Gray");
			}
		}
		panel_OnlineUsers.setTitle("Contacts ("+count+")");
		logger.debug("Leaving");
	}

	/**
	 * This event will raise for every n seconds  and shows online users.
	 * 
	 * @param event
	 */
	public void onTimer$refreshTimer(Event event) {
		logger.debug("Entering " + event.toString());
		doShowOnlineUsers();
		logger.debug("Leaving " + event.toString());

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//


	public void setMessagesService(MessagesService messagesService) {
		this.messagesService = messagesService;
	}

	public MessagesService getMessagesService() {
		return messagesService;
	}
	
	
}
