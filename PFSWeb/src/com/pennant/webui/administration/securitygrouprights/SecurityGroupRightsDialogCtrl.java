package com.pennant.webui.administration.securitygrouprights;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.administration.SecurityGroupRightsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.webui.administration.securityuserroles.model.SecurityGroupRightModelItemRenderer;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

public class SecurityGroupRightsDialogCtrl extends GFCBaseListCtrl<SecurityRight> implements Serializable{

	/**
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
	 * This is the controller class for the
	 * /WEB-INF/pages/Administration/SecurityGroupRights/SecurityGroupRightsDialog.zul
	 * file.<br>
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
	 * 
	 */

	private static final long serialVersionUID = -7625144242180775016L;
	private final static Logger logger = Logger.getLogger(SecurityGroupRightsDialogCtrl.class);


	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	protected Window       win_SecGroupRightsDialog;                                   // autoWired
	protected Borderlayout borderLayout_SecurityGroupRights;                           // autoWired
	protected Button       btnSave;                                                    // autoWired
	protected Button       btnRefresh;                                                 // autoWired
	protected Button       btnCancel;                                                  // autoWired
	protected Button       btnClose;                                                   // autoWired
	protected Button       btnSelectRights;                                            // autoWired
	protected Button       btnUnSelectRights;                                          // autoWired
	protected Button       btnUnSelectAllRights;                                       // autoWired
	protected Button       btnSearchRights;                                            // autoWired
	protected Listbox      listbox_UnAssignedRights;                                   // autoWired
	protected Listbox      listbox_AssignedRights;                                     // autoWired
	protected Label        label_GroupCode;                                            // autoWired
	protected Label        label_GroupDesc;                                            // autoWired
	protected Listhead     listheader_SelectRight;                                     // autoWired
	protected Listhead     listheader_RightDesc;                                       // autoWired
	protected Button        btnNew;                                                    // autoWired
	protected Button        btnEdit;                                                   // autoWired
	protected Button        btnDelete;                                                 // autoWired
	protected Button        btnNotes;                                                  // autoWired
	@SuppressWarnings("unused")
	private transient ButtonStatusCtrl             btnCtrl;
	private transient final String     btnCtroller_ClassPrefix = "button_SecRoleGroupsDialog_";

	private   SecurityGroupRights          secGroupRights;
	private   List<SecurityRight>               secRightsList = new ArrayList<SecurityRight>();
	private   SecurityGroup               securityGroup;
	private   transient SecurityGroupRightsService securityGroupRightsService;
	private   List<SecurityRight>                 assignedRights = new ArrayList<SecurityRight>();
	private   List<SecurityRight>                 unAssignedRights=new ArrayList<SecurityRight>();
	private List<SecurityRight>               tempUnAssignedRights=new ArrayList<SecurityRight>(); 
	private   Map<Long, SecurityRight> 		      newAssignedMap = new HashMap<Long, SecurityRight>();
	private   Map<Long, SecurityRight> 		      oldAssignedMap = new HashMap<Long, SecurityRight>();
	private   Map<Long, SecurityGroupRights>      selectedMap ;
	private   Map<Long, SecurityGroupRights>      deletedMap  ;
	private   Map<String, SecurityRight> 	     tempUnAsgnRightsMap  =new HashMap<String, SecurityRight>();
	private   Object filters[]=new Object[2];
	/**
	 * default constructor.<br>
	 */
	public SecurityGroupRightsDialogCtrl(){
		super();
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected SecurityGroup object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$win_SecGroupRightsDialog(Event event) throws Exception {

		logger.debug("Entering " + event.toString());
		/* set components visible dependent of the users rights */
		doCheckRights();
		final Map<String, Object> args = getCreationArgsMap(event);
		// get the parameters map that are overHanded by creation.
		if (args.containsKey("securityGroup")) {
			setSecurityGroup((SecurityGroup)args.get("securityGroup"));

		} else {
			setSecurityGroup(null);
		}
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace()
				,this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);
		
		this.borderLayout_SecurityGroupRights.setHeight(getBorderLayoutHeight());
		this.label_GroupCode.setValue(getSecurityGroup().getGrpCode());
		this.label_GroupDesc.setValue(getSecurityGroup().getGrpDesc());
		this.listbox_UnAssignedRights.setItemRenderer(new SecurityGroupRightModelItemRenderer());
		/*get all assigned rights*/
		assignedRights=getSecurityGroupRightsService().getRightsByGroupId(getSecurityGroup().getGrpID(),true);
		/*get all unassigned rights*/
		unAssignedRights=getSecurityGroupRightsService().getRightsByGroupId(getSecurityGroup().getGrpID(),false);
		tempUnAssignedRights=unAssignedRights;
		doShowDialog();
		setDialog(this.win_SecGroupRightsDialog);
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * When user clicks on "cancel" button
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnCancel(Event event) throws Exception {
		logger.debug("Entering " + event.toString());	
		doCancel();
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * When user clicks on "save" button
	 */
	public void onClick$btnSave(Event event) throws Exception{
		logger.debug("Entering " + event.toString());
		doSave();
		logger.debug("Leaving " + event.toString());

	}

	/**
	 * When clicks on "Close" button
	 */
	public void onClick$btnRefresh(Event event) throws Exception{
		logger.debug("Entering " + event.toString());	
		doShowUnAssignedRightsList();
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * When clicks on "Close" button
	 */
	public void onClick$btnClose(Event event) throws Exception{
		logger.debug("Entering " + event.toString());
		try{
			doClose();
		}catch ( final WrongValueException e) {
			logger.debug("Error Occured while closing"+e.toString());
			throw e;
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on "btnSearchRights"
	 * @param event
	 * @throws Exception
	 */

	public  void onClick$btnSearchRights(Event event) throws Exception {

		logger.debug("Entering " + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("dialogCtrl", this);
		map.put("dataMap",tempUnAsgnRightsMap);
		map.put("prevFilters", filters);
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecuritySearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());

	}
	/**
	 * when clicks on "btnSelectRights"
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public  void onClick$btnSelectRights(Event event) throws Exception {	
		logger.debug("Entering" +event.toString());

		if(this.listbox_UnAssignedRights.getSelectedCount()!=0){	

			Listitem li=new Listitem();                          //To read List Item
			Set SeletedSet= new HashSet();                       //To get Selected Items
			SeletedSet=this.listbox_UnAssignedRights.getSelectedItems();
			List list=new ArrayList(SeletedSet);	//Converting Set to ArrayList to Make Concurrent operations	
			System.out.println(SeletedSet.size());
			Iterator iterator=list.iterator();
			while(iterator.hasNext()){
				li=(Listitem) iterator.next();
				final SecurityRight aSecRight= (SecurityRight)li.getAttribute("data");
				System.out.println(li.getLabel());
				Listcell slecteditem=new Listcell();
				List SelectedRowValues=new ArrayList();         //TO get each row Details
				SelectedRowValues=li.getChildren();
				slecteditem=(Listcell)SelectedRowValues.get(0);
				tempUnAsgnRightsMap.remove(String.valueOf(aSecRight.getRightID()));
				getNewAssignedMap().put(Long.valueOf(aSecRight.getRightID()),aSecRight);
				doFillListbox(this.listbox_AssignedRights,slecteditem.getLabel(),aSecRight);
				if(true){
					this.listbox_UnAssignedRights.removeItemAt(li.getIndex());
				}
			}
		}							
	}
	/**
	 * when clicks on "btnUnSelectRights"
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public  void onClick$btnUnSelectRights(Event event) throws Exception {			
		logger.debug(event.toString());	

		if(this.listbox_AssignedRights.getSelectedCount()!=0){	
			// To Remove Selected item from the List Box 
			Listitem li=new Listitem();        //To read List Item
			Set SeletedSet= new HashSet();          //To get Selected Items
			SeletedSet=this.listbox_AssignedRights.getSelectedItems();
			List list=new ArrayList(SeletedSet);	  //Converting Set to ArrayList to Make Concurrent operations	
			System.out.println(SeletedSet.size());
			java.util.Iterator iterator=list.iterator();
			while(iterator.hasNext()){				
				li=(Listitem)iterator.next();
				final SecurityRight aSecRight= (SecurityRight)li.getAttribute("data");
				System.out.println(li.getLabel());
				Listcell slecteditem=new Listcell();
				List SelectedRowValues=new ArrayList();        //TO get each row Details
				SelectedRowValues=li.getChildren();			
				slecteditem=(Listcell)SelectedRowValues.get(0);	
				tempUnAsgnRightsMap.put(String.valueOf(aSecRight.getRightID()), aSecRight);
				getNewAssignedMap().remove(Long.valueOf(aSecRight.getRightID()));
				doFillListbox(this.listbox_UnAssignedRights,slecteditem.getLabel(),aSecRight);	
				if(true){
					this.listbox_AssignedRights.removeItemAt(li.getIndex());
				}
			}
		}
	}
	/**
	 * when clicks on "btnUnSelectAllRights"
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public  void onClick$btnUnSelectAllRights(Event event) throws Exception {			
		logger.debug(event.toString());	
		this.listbox_AssignedRights.selectAll();
		if(this.listbox_AssignedRights.getSelectedCount()!=0){	
			//////// To Remove Selected item from the List Box 
			Listitem li=new Listitem();//To read List Item
			Set SeletedSet= new HashSet();//To get Selected Items
			SeletedSet=this.listbox_AssignedRights.getSelectedItems();
			List list=new ArrayList(SeletedSet);	//Converting Set to ArrayList to Make Concurrent operations	
			System.out.println(SeletedSet.size());
			java.util.Iterator it=list.iterator();
			while(it.hasNext()){				
				li=(Listitem)it.next();
				final SecurityRight aSecRight= (SecurityRight)li.getAttribute("data");
				System.out.println(li.getLabel());
				Listcell slecteditem=new Listcell();
				List SelectedRowValues=new ArrayList();//TO get each row Details
				SelectedRowValues=li.getChildren();			
				slecteditem=(Listcell)SelectedRowValues.get(0);		
				tempUnAsgnRightsMap.put(String.valueOf(aSecRight.getRightID()), aSecRight);
				getNewAssignedMap().remove(Long.valueOf(aSecRight.getRightID()));
				doFillListbox(this.listbox_UnAssignedRights,slecteditem.getLabel(),aSecRight);
				if(true){
					this.listbox_AssignedRights.removeItemAt(li.getIndex());
				}
			}
		}
	}
	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, this.win_SecGroupRightsDialog);
		logger.debug("Leaving" + event.toString());
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		getUserWorkspace().alocateAuthorities("SecurityGroupRightsDialog");
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SecurityGroupRightsDialog_btnSave"));
		this.btnCancel.setVisible(getUserWorkspace().isAllowed("button_SecurityGroupRightsDialog_btnCancel"));
		this.btnSelectRights.setVisible(getUserWorkspace().isAllowed("button_SecurityGroupRightsDialog_btnSelectRights"));
		this.btnUnSelectRights.setVisible(getUserWorkspace().isAllowed("button_SecurityGroupRightsDialog_btnUnSelectRights"));
		this.btnUnSelectAllRights.setVisible(getUserWorkspace().isAllowed("button_SecurityGroupRightsDialog_btnUnSelectAllRights"));
		logger.debug("Leaving ");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * @throws Exception 
	 * 
	 */
	public void  doShowDialog()throws InterruptedException {
		logger.debug("Entering ");
		for(SecurityRight secRight:unAssignedRights){
			tempUnAsgnRightsMap.put(String.valueOf(secRight.getRightID()),secRight);
		}
		doShowUnAssignedRightsList();
		doShowAssignedRightsList();	
		logger.debug("Leaving ");
	}

	/**
	 * Closes the dialog window. <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	public void doClose() throws InterruptedException{
		logger.debug("Entering ");
		boolean close = true;
		// before close check whether data changed.
		if (isdatachanged()) {

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,MultiLineMessageBox.YES
					| MultiLineMessageBox.NO,MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		}
		if(close){
			closeDialog(this.win_SecGroupRightsDialog, "SecurityGroupRights");
		}

		logger.debug("Leaving ");
	}
	/**
	 * This method do the following 
	 *  1) Gets assigned rights list by calling SecurityGroupRightsService's getRightsByGroupId()method
	 *  2) render all the list by calling doFillListbox()
	 */	@SuppressWarnings("unchecked")
	 public void doShowAssignedRightsList(){

		 logger.debug("Entering");
		 this.listbox_AssignedRights.getItems().clear();
		 SecurityRight secRight = new SecurityRight();
		 Comparator<SecurityRight> comp = new BeanComparator("rightName");
		 Collections.sort(assignedRights, comp);
		 for(int i=0;i<assignedRights.size();i++){
			 secRight=(SecurityRight)assignedRights.get(i);
			 oldAssignedMap.put(Long.valueOf(secRight.getRightID()),secRight);
			 doFillListbox(this.listbox_AssignedRights, secRight.getRightName(),secRight);
		 }
		 setOldAssignedMap(oldAssignedMap);
		 getNewAssignedMap().putAll(oldAssignedMap);
	 }
	 /**
	  * This method do the following 
	  *  1) Gets unassigned rights list by calling SecurityGroupRightsService's getRightsByGroupId()method
	  *  2) render all the list by calling doFillListbox()
	  */
	 @SuppressWarnings("unchecked")
	 public void doShowUnAssignedRightsList(){

		 logger.debug("Entering ");
		 this.listbox_UnAssignedRights.getItems().clear();

		 unAssignedRights=new ArrayList<SecurityRight>(tempUnAsgnRightsMap.values());


		 SecurityRight secRight=new SecurityRight();
		 Comparator<SecurityRight> comp = new BeanComparator("rightName");
		 Collections.sort(unAssignedRights, comp);
		 for(int i=0;i<unAssignedRights.size();i++){
			 secRight=(SecurityRight)unAssignedRights.get(i);
			 doFillListbox(this.listbox_UnAssignedRights, secRight.getRightName(),secRight);	

		 }

		 logger.debug("Leaving ");
	 }

	 /**
	  * This method do the following 
	  * 1)compare oldAssigned map and newAssigned map
	  *    a)if rightId not in oldselectedMap and in new selectedMap creates new SecurityGroupRights
	  *     Object, sets data and add it to SecurityGroup LovDescAssignedRights
	  *    b)if rightId  in oldselectedMap and not in new selectedMap gets the SecurityUserRoles
	  *       from back end , sets RecordStatus DELETE and add it to SecurityGroup LovDescAssignedRights
	  */

	 public void doWriteComponentsToBean() throws InterruptedException {
		 logger.debug("Entering");

		 selectedMap = new HashMap<Long, SecurityGroupRights>();
		 deletedMap  = new HashMap<Long, SecurityGroupRights>();
		 //for insert
		 for (Object rightId : getNewAssignedMap().keySet()) {
			 if (!getOldAssignedMap().containsKey(rightId)) {

				 SecurityGroupRights aSecGroupRights=getSecurityGroupRightsService().getSecurityGroupRights(); 
				 aSecGroupRights.setGrpID(getSecurityGroup().getGrpID());
				 aSecGroupRights.setLovDescGrpCode(getSecurityGroup().getGrpCode());
				 aSecGroupRights.setRightID(getNewAssignedMap().get(rightId).getRightID());
				 aSecGroupRights.setLovDescRightName(getNewAssignedMap().get(rightId).getRightName());
				 aSecGroupRights.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				 aSecGroupRights.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
				 aSecGroupRights.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				 aSecGroupRights.setNextRoleCode("");
				 aSecGroupRights.setNextTaskId("");
				 aSecGroupRights.setTaskId("");
				 aSecGroupRights.setRoleCode("");
				 aSecGroupRights.setRecordStatus("");
				 selectedMap.put(Long.valueOf(getNewAssignedMap().get(rightId).getRightID()),aSecGroupRights);
			 }			
		 }
		 //for Delete
		 for (Object rightId : getOldAssignedMap().keySet()) {
			 if (!getNewAssignedMap().containsKey(rightId)) {

				 SecurityGroupRights aSecGroupRights=getSecurityGroupRightsService().getSecurityGroupRights(); 
				 aSecGroupRights.setGrpID(getSecurityGroup().getGrpID());
				 aSecGroupRights.setRightID(getOldAssignedMap().get(rightId).getRightID());	
				 aSecGroupRights.setLovDescGrpCode(getSecurityGroup().getGrpCode());
				 aSecGroupRights.setLovDescRightName(getOldAssignedMap().get(rightId).getRightName());
				 aSecGroupRights.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				 aSecGroupRights.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				 aSecGroupRights.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
				 aSecGroupRights.setNextRoleCode("");
				 aSecGroupRights.setNextTaskId("");
				 aSecGroupRights.setTaskId("");
				 aSecGroupRights.setRoleCode("");
				 aSecGroupRights.setRecordStatus("");
				 deletedMap.put(aSecGroupRights.getRightID(),aSecGroupRights);	
			 }
		 }	
		 logger.debug("Leaving");
	 }

	 // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 // +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	 // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 /**
	  * This method inserts or deletes SecurityGroupRights records to database by calling 
	  * SecurityGroupRightsService's saveOrDelete() method
	  * @throws InterruptedException 
	  */
	 public void doSave() throws InterruptedException{
		 logger.debug("Entering ");
		 doWriteComponentsToBean();
		 try{
			 AuditHeader auditHeader=getAuditHeader(getSecurityGroup(), "");
			 auditHeader.setAuditDetails(getAuditDetails());
			 if(doSaveProcess(auditHeader)){
				 closeDialog(this.win_SecGroupRightsDialog, "SecurityUserRoles");	
			 }	

		 }catch(DataAccessException error){
			 showMessage(error);
		 }
		 logger.debug("Leaving ");
	 }
	 /**	
	  * Get the result after processing DataBase Operations 
	  * 
	  * @param auditHeader (AuditHeader)
	  * 
	  * @param method (String)
	  * 
	  * @return boolean
	  * 
	  */
	 private boolean doSaveProcess(AuditHeader auditHeader) throws InterruptedException{
		 boolean processCompleted=false;
		 int retValue=PennantConstants.porcessOVERIDE;

		 try{
			 while(retValue==PennantConstants.porcessOVERIDE){
				 auditHeader=getSecurityGroupRightsService().save(auditHeader);
				 retValue = ErrorControl.showErrorControl(this.win_SecGroupRightsDialog,auditHeader);

				 if (retValue==PennantConstants.porcessCONTINUE){
					 processCompleted=true;
				 }
				 if (retValue==PennantConstants.porcessOVERIDE){
					 auditHeader.setOveride(true);
					 auditHeader.setErrorMessage(null);
					 auditHeader.setInfoMessage(null);
					 auditHeader.setOverideMessage(null);
				 }	
				 setOverideMap(auditHeader.getOverideMap());
			 }
		 }
		 catch (InterruptedException e) {
			 logger.error(e);
			 e.printStackTrace();
		 }
		 return processCompleted;
	 }
	 // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 // ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	 // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	 private void doCancel() throws InterruptedException{
		 tempUnAsgnRightsMap.clear();
		 newAssignedMap.clear();
		 unAssignedRights=tempUnAssignedRights;
		 doShowDialog();
	 }

	 /**
	  * This method checks whether data changed or not 
	  * @return true If changed ,otherwise false
	  */

	 public boolean isdatachanged() {
		 logger.debug("Entering ");
		 //compare sizes of LovDescSelectedMap and newSelectedMap
		 if (getNewAssignedMap().size() != getOldAssignedMap().size()) {
			 return true;
		 }
		 //Compare all keys are in  LovDescSelectedMap and newSelectedMap same or not
		 if (getNewAssignedMap().size() ==  getOldAssignedMap().size()) {
			 for (Object key : getNewAssignedMap().keySet()) {
				 if (!getOldAssignedMap().containsKey(key)) {
					 return true;
				 }
			 }
		 }
		 logger.debug("Leaving ");
		 return false;
	 }

	 /**
	  * This method shows message box with error message
	  * @param e
	  */
	 private void showMessage(Exception e){
		 logger.debug("Entering ");
		 AuditHeader auditHeader= new AuditHeader();
		 try {
			 auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			 ErrorControl.showErrorControl(this.win_SecGroupRightsDialog, auditHeader);
		 } catch (Exception exp) {
			 logger.error(e);
		 }
		 logger.debug("Leaving ");
	 }

	 /**
	  * This method  creates and returns AuditHeader Object
	  * @param SecurityGroup
	  * @param tranType
	  * @return
	  */
	 private AuditHeader getAuditHeader(SecurityGroup aSecurityGroup, String tranType) {

		 logger.debug("Entering ");
		 AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityGroup.getBefImage()
				 ,aSecurityGroup);   
		 return new AuditHeader(String.valueOf(aSecurityGroup.getId()),null,null,null
				 ,auditDetail,getUserWorkspace().getLoginUserDetails(),getOverideMap());
	 }
	 /**
	  * This method works as item renderer
	  * @param listbox
	  * @param value1
	  * @param securityRight
	  */
	 private void doFillListbox(Listbox listbox,String value1,SecurityRight securityRight){
		 Listitem item=new Listitem(); //To Create List item
		 Listcell lc;
		 lc=new Listcell();	
		 lc.setLabel(value1);
		 lc.setParent(item);
		 item.setAttribute("data", securityRight);
		 listbox.appendChild(item);
	 } 
	 /**
	  * This method invokes from  SecuritySearchDialogCtrl's  doSearch()method .its renders list 
	  * of SecurityRight and sets the filters used in SecuritySearchDialogCtrl.
	  * @param searchResult
	  */
	 @SuppressWarnings("unchecked")
	 public void doShowSearchResult(Object[] searchResult ){
		 logger.debug("Entering ");
		 List<Object> searchResultList=(List<Object>)searchResult[0];
		 /*we get the last used filters from SecuritySearchDialogCtrl and we send these filters to
		  * SecuritySearchDialogCtrl on event  "onClick$btnSearchRights" for set previous search filters */
		 filters=(Object[]) searchResult[1];
		 this.listbox_UnAssignedRights.getItems().clear();
		 Comparator<Object> comp = new BeanComparator("rightName");
		 Collections.sort(searchResultList, comp);
		 for(int i=0;i<searchResultList.size();i++){
			 SecurityRight secRight=(SecurityRight)searchResultList.get(i);
			 doFillListbox(this.listbox_UnAssignedRights, secRight.getRightName(),secRight);	
		 }
		 logger.debug("Leaving ");
	 }
	 /**
	  * This method prepares the audit details list and sets different auditSequence for 
	  * newly inserted records and deleted records
	  * 
	  * @return
	  */
	 private List<AuditDetail> getAuditDetails(){
		 logger.debug("Entering ");
		 List<AuditDetail> auditDetails=new ArrayList<AuditDetail>();;

		 int count = 1;
		 String[] fields = PennantJavaUtil.getFieldDetails(new SecurityGroupRights());

		 if(selectedMap!=null && selectedMap.size()>0){
			 Collection<SecurityGroupRights> collection =  selectedMap.values();

			 for (final  SecurityGroupRights securityGroupRights : collection) {
				 AuditDetail auditDetail =getAuditDetail(securityGroupRights,count,fields);
				 if(auditDetail!=null){
					 auditDetails.add(auditDetail);
					 count++;
				 }
			 }
		 }

		 if(deletedMap!=null && deletedMap.size()>0){
			 count=1;
			 Collection<SecurityGroupRights> collection =  deletedMap.values();
			 for (final  SecurityGroupRights securityGroupRights : collection) {
				 AuditDetail auditDetail =getAuditDetail(securityGroupRights,count,fields);
				 if(auditDetail!=null){
					 auditDetails.add(auditDetail);
					 count++;
				 }
			 }	
		 }
		 logger.debug("Leaving ");
		 return auditDetails;
	 }
	 /**
	  * 
	  * @param securityGroupRights
	  * @param auditSeq
	  * @param fields
	  * @return AuditDetail
	  */
	 private AuditDetail getAuditDetail(SecurityGroupRights securityGroupRights,int auditSeq,String[] fields){
		 logger.debug("Entering ");

		 if(securityGroupRights==null ){
			 return null;	
		 }
		 String auditImage = "";
		 Object befImage=null;
		 if(securityGroupRights.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			 auditImage=PennantConstants.TRAN_ADD;
		 }
		 if(securityGroupRights.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
			 auditImage=PennantConstants.TRAN_DEL;
			 befImage=securityGroupRights;
		 }
		 logger.debug("Leaving ");
		 return new AuditDetail(auditImage, auditSeq, fields[0], fields[1], befImage, securityGroupRights);
	 }
	 // ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	 // ++++++++++++++++++ getter / setter +++++++++++++++++++//
	 // ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	 public SecurityGroup getSecurityGroup() {
		 return securityGroup;
	 }

	 public void setSecurityGroup(SecurityGroup securityGroup) {
		 this.securityGroup = securityGroup;
	 }

	 public SecurityGroupRights getSecGroupRights() {
		 return secGroupRights;
	 }
	 public void setSecGroupRights(SecurityGroupRights secGroupRights) {
		 this.secGroupRights = secGroupRights;
	 }

	 public List<SecurityRight> getSecgroupsList() {
		 return secRightsList;
	 }
	 public void setSecgroupsList(List<SecurityRight> secRightsList) {
		 this.secRightsList = secRightsList;
	 }
	 public SecurityGroupRightsService getSecurityGroupRightsService() {
		 return securityGroupRightsService;
	 }

	 public void setSecurityGroupRightsService(
			 SecurityGroupRightsService securityGroupRightsService) {
		 this.securityGroupRightsService = securityGroupRightsService;
	 }

	 public Map<Long, SecurityRight> getNewAssignedMap() {
		 return newAssignedMap;
	 }

	 public void setNewAssignedMap(Map<Long, SecurityRight> newAssignedMap) {
		 this.newAssignedMap = newAssignedMap;
	 }

	 public Map<Long, SecurityRight> getOldAssignedMap() {
		 return oldAssignedMap;
	 }

	 public void setOldAssignedMap(Map<Long, SecurityRight> oldAssignedMap) {
		 this.oldAssignedMap = oldAssignedMap;
	 }
}
