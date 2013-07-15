package com.pennant.webui.administration.securityuser;

// UN USED File NEED TO DELETE ,file moved to  com.pennant.webui.administration.securityusers.changepassword //


/*package com.pennant.webui.administration.securityusers;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.SecUser;
import com.pennant.backend.service.UserService;
import com.pennant.backend.service.administration.SecurityUsersService;
import com.pennant.webui.administration.securityusers.changepassword.ChangePasswordModel;
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

	private  transient SecurityUsersService   securityUsersService;
	private transient UserService    userService;
	private SecUser secUser;
	private   transient SecurityUsersChangePasswordListCtrl securityUsersCpwdListCtrl;
	//private transient SecurityUsersListCtrl securityUsersListCtrl;
	
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
		
		//String pwd= this.password.getValue();
		String newPwd= this.newPassword.getValue().trim();
		String retypePwd= this.retypeNewPassword.getValue().trim();		
		ChangePasswordModel c=new ChangePasswordModel();

		//comparing new password with old password if not same alert
		if(!newPwd.equals(retypePwd)){
			resetLogic();
		    throw new WrongValueException( this.newPassword, Labels.getLabel("label_SecurityUsersDialog_Pwd_not_match.value"));
		    }
		if(c.validate(this.secUser.getUsrLogin(),this.secUser.getUsrPwd(),newPwd )==0){
			   doUpdate();//update password
		}else{
				if(c.validate(this.secUser.getUsrLogin(),this.secUser.getUsrPwd(),newPwd )==1){
						resetLogic();
						throw new WrongValueException( this.newPassword, Labels.getLabel("label_oldpwd_newpwd_notsame"));
				} else{
						resetLogic();
						throw new WrongValueException( this.newPassword, Labels.getLabel("label_invalid_password"));
				}
			
		}

	}
	
	
	
	public void onClick$btnclose(Event event) throws Exception{

	
          closeDialog(this.win_secPwd, "changepassword") ;
	}
			//resetting values 
	
	public void resetLogic(){
	
		this.newPassword.setConstraint("");
		this.retypeNewPassword.setConstraint("");
		
		
		this.newPassword.setValue("");
		this.retypeNewPassword.setValue("");
		
		//set focus
		this.newPassword.setFocus(true);
	}

	//set field properties
	private void doSetFieldProperties() {
		this.userName.setMaxlength(30);
		this.userName.setReadonly(true);
		//this.password.setMaxlength(20);
		this.newPassword.setMaxlength(20);
		this.retypeNewPassword.setMaxlength(20);
		
	}
	//dovalidations
	private void doValidations() {
		
		this.newPassword.setConstraint("NO EMPTY:" + Labels.getLabel("const_NO_EMPTY"));
		this.retypeNewPassword.setConstraint("NO EMPTY:" + Labels.getLabel("const_NO_EMPTY"));
		
	}
	//update pasword to database
	public void doUpdate(){
		try{
		this.secUser=userService.getUserByLogin(this.secUser.getUsrLogin());
	    this.secUser.setUsrPwd(this.newPassword.getValue().trim());
	    this.secUser.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
        this.secUser.setLastMntOn(new Timestamp(System.currentTimeMillis()));
       getSecurityUsersService().changePassword(secUser);
       logger.debug("password changed successfully------------------------>");
	    resetLogic();
		
	    closeDialog(this.win_secPwd, "changepassword") ;
	} catch (Exception e) {
		logger.debug(e.toString());
		
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