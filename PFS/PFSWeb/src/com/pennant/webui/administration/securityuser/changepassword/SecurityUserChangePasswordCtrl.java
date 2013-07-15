package com.pennant.webui.administration.securityuser.changepassword;

//un used file need to delete SecurityUsersChangePasswordCtrl chaned to SecurityUsersChangePasswordDialogCtrl  

/*package com.pennant.webui.administration.securityusers.changepassword;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.SecUser;
import com.pennant.backend.service.administration.SecurityUsersService;
import com.pennant.webui.util.GFCBaseCtrl;

public class SecurityUsersChangePasswordCtrl  extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = -2314266107249438945L;
	private final static Logger logger = Logger.getLogger(SecurityUsersChangePasswordCtrl.class);
	
	
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends WindowBaseCtrl'.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 
	protected Window  win_secPwd; // autowired
	protected Textbox userName; // autowired
	protected Textbox newPassword; // autowired
	protected Textbox retypeNewPassword; // autowired
	protected Button  btnSubmit;
	protected Button  btnclose;

	private   transient SecurityUsersService   securityUsersService;
	private   SecUser secUser;
	private   transient SecurityUsersChangePasswordListCtrl securityUsersCpwdListCtrl;
	private   transient ChangePasswordModel changePasswordModel=new ChangePasswordModel();
	private   boolean   isCriteriaMatched;
	
	
	*//**
	 * default constructor. <br>
	 *//*

	public SecurityUsersChangePasswordCtrl() {
		super();
	}
	
     
	public void onCreate$win_secPwd(Event event) throws Exception {
			
			logger.debug("entering win_secpassword");
			doSetFieldProperties();
			final Map<String, Object> args = getCreationArgsMap(event);
			this.userName.setReadonly(true);
	    
		// get the params map that are overhanded by creation.
		
		if (args.containsKey("secUser")) {
			this.secUser = (SecUser) args.get("secUser");
			
			setSecUser(this.secUser);
		} else {
			 setSecUser(null);
		}	
	
	    	   
	    //setting user id password Fields 
	    this.userName.setValue(this.secUser.getUsrLogin());

	    logger.debug("leaving win_secpassword");
	    setDialog(this.win_secPwd);
	
			
	}
	
	public void onClick$btnSubmit(Event event) throws Exception{

		doValidations();
	    doUpdate();//update password
			
	
	}
	
	public void onClick$btnclose(Event event) throws Exception{

	
          closeDialog(this.win_secPwd, "changepassword") ;
	}
			//resetting values 
	
	public void resetLogic(){
		this.newPassword.setValue("");
		this.retypeNewPassword.setValue("");
		
		//set focus
		this.newPassword.setFocus(true);
	}

	//set field properties
	private void doSetFieldProperties() {
		this.userName.setReadonly(true);
		this.newPassword.setMaxlength(10);
		this.retypeNewPassword.setMaxlength(10);
		
	}
	
	//dovalidations
	private void doValidations() {
		// doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		this.isCriteriaMatched=changePasswordModel.validate(this.secUser.getUsrLogin(),this.newPassword.getValue() );

		try{
			if(this.newPassword.getValue().equals("")){
				throw new WrongValueException(this.newPassword,Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_newPassword.value")})); 
		      }

		 }catch (WrongValueException we) {
			 wve.add(we);
		
		}
		 try{
				if(this.retypeNewPassword.getValue().equals("")){
					throw new WrongValueException(this.retypeNewPassword,Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_retypePassword.value")})); 
			      }

			 }catch (WrongValueException we) {
				 wve.add(we);
			
			}
			 
		 try{
			 if(this.newPassword.getValue()!="" && this.retypeNewPassword.getValue()!=""){
					 if(!this.newPassword.getValue().equals(this.retypeNewPassword.getValue())){
							resetLogic();
						    throw new WrongValueException( this.newPassword, Labels.getLabel("FIELD_NOT_MATCHED",new String[]{Labels.getLabel("label_newPassword.value"),Labels.getLabel("label_retypePassword.value")}));
			          }
			 }	 
			 }catch (WrongValueException we) {
				 wve.add(we);
			
			}
	
			 try{
				if(isCriteriaMatched==false && this.newPassword.getValue()!=""){
					   resetLogic();
						throw new WrongValueException( this.newPassword, Labels.getLabel("label_invalid_password_edit"));
			     }
					
				 }catch (WrongValueException we) {
					 wve.add(we);
				
				}
			 try{
				 if(this.newPassword.getValue()!=""){
					if(changePasswordModel.IsPaswordsSame(getSecUser().getUsrPwd(), getSecUser().getUsrToken(),this.newPassword.getValue())==true){
						resetLogic();
						throw new WrongValueException( this.newPassword, Labels.getLabel("label_oldpwd_newpwd_same"));
			     	 }
				  }
							
			   }catch (WrongValueException we) {
							 wve.add(we);
						
			}
				   
			   doRemoveValidation();
						if (wve.size()>0) {
						
					
							WrongValueException [] wvea = new WrongValueException[wve.size()];
							for (int i = 0; i < wve.size(); i++) {
								wvea[i] = (WrongValueException) wve.get(i);
							}
							throw new WrongValuesException(wvea);
						}
	}
	
	public void doSetValidation(){
		
		this.retypeNewPassword.setConstraint("NO EMPTY:"    + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_retypePassword.value")}));
		this.newPassword.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_newPassword.value")}));
	}
	
	public void doRemoveValidation(){
		this.retypeNewPassword.setConstraint("");
		this.newPassword.setConstraint("");
	}
	//update pasword 
	public void doUpdate(){
		try{
			
			this.secUser=getSecurityUsersService().getSecUserById(this.secUser.getUsrID());
		    this.secUser.setUsrPwd(this.newPassword.getValue().trim());
		    this.secUser.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
	        this.secUser.setLastMntOn(new Timestamp(System.currentTimeMillis()));
	        this.secUser.setVersion(this.secUser.getVersion()+1);
	       getSecurityUsersService().changePassword(secUser);
	        logger.debug("password changed successfully------------------------>");
		    resetLogic();
		
	    closeDialog(this.win_secPwd, "changepassword") ;
	} catch (Exception e) {
			logger.error(e.toString());
			logger.error("--------> Error occured while updating password ");
			closeDialog(this.win_secPwd, "changepassword") ;
		
	}
	    
        
 }
	
	//Getters and Setters 

	public SecUser getSecUser() {
		return secUser;
	}


	public void setSecUser(SecUser secUser ){
		this.secUser = secUser;
	}
	public SecurityUsersService getSecurityUsersService() {
		return securityUsersService;
	}


	public void setSecurityUsersService(SecurityUsersService securityUsersService) {
		this.securityUsersService = securityUsersService;
	}
	public SecurityUsersChangePasswordListCtrl getSecurityUsersCpwdListCtrl() {
		return securityUsersCpwdListCtrl;
	}


	public void setSecurityUsersCpwdListCtrl(
			SecurityUsersChangePasswordListCtrl securityUsersCpwdListCtrl) {
		this.securityUsersCpwdListCtrl = securityUsersCpwdListCtrl;
	}
	

}
*/