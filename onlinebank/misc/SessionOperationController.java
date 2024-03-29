package sessionOperation;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.beehive.controls.api.bean.Control;
import org.apache.beehive.netui.pageflow.Forward;
import org.apache.beehive.netui.pageflow.PageFlowController;
import org.apache.beehive.netui.pageflow.annotations.Jpf;

import util.BroadcastUtils;
import util.MessageResourceBundle;
import util.RequestUtil;
import util.StudentResponseReportPdfUtils;
import weblogic.utils.StringUtils;

import com.ctb.bean.request.FilterParams;
import com.ctb.bean.request.PageParams;
import com.ctb.bean.request.SortParams;
import com.ctb.bean.request.FilterParams.FilterParam;
import com.ctb.bean.request.FilterParams.FilterType;
import com.ctb.bean.testAdmin.AncestorOrgDetails;
import com.ctb.bean.testAdmin.BulkReportData;
import com.ctb.bean.testAdmin.ClassHierarchy;
import com.ctb.bean.testAdmin.Customer;
import com.ctb.bean.testAdmin.CustomerConfiguration;
import com.ctb.bean.testAdmin.CustomerConfigurationValue;
import com.ctb.bean.testAdmin.CustomerLicense;
import com.ctb.bean.testAdmin.CustomerReport;
import com.ctb.bean.testAdmin.CustomerReportData;
import com.ctb.bean.testAdmin.EditCopyStatus;
import com.ctb.bean.testAdmin.ItemResponseAndScore;
import com.ctb.bean.testAdmin.LASLicenseNode;
import com.ctb.bean.testAdmin.LiteracyProExportData;
import com.ctb.bean.testAdmin.Node;
import com.ctb.bean.testAdmin.OrgNodeCategory;
import com.ctb.bean.testAdmin.OrgNodeLicenseInfo;
import com.ctb.bean.testAdmin.PasswordHintQuestion;
import com.ctb.bean.testAdmin.Program;
import com.ctb.bean.testAdmin.ProgramData;
import com.ctb.bean.testAdmin.RosterElement;
import com.ctb.bean.testAdmin.RosterElementData;
import com.ctb.bean.testAdmin.ScheduledSession;
import com.ctb.bean.testAdmin.ScheduledStudentDetailsWithManifest;
import com.ctb.bean.testAdmin.ScoreDetails;
import com.ctb.bean.testAdmin.SessionStudent;
import com.ctb.bean.testAdmin.SessionStudentData;
import com.ctb.bean.testAdmin.StudentManifest;
import com.ctb.bean.testAdmin.StudentManifestData;
import com.ctb.bean.testAdmin.StudentNode;
import com.ctb.bean.testAdmin.StudentNodeData;
import com.ctb.bean.testAdmin.StudentSessionStatus;
import com.ctb.bean.testAdmin.StudentSessionStatusData;
import com.ctb.bean.testAdmin.StudentTestletInfo;
import com.ctb.bean.testAdmin.TABERecommendedLevel;
import com.ctb.bean.testAdmin.TestElement;
import com.ctb.bean.testAdmin.TestElementData;
import com.ctb.bean.testAdmin.TestProduct;
import com.ctb.bean.testAdmin.TestProductData;
import com.ctb.bean.testAdmin.TestSession;
import com.ctb.bean.testAdmin.TestSessionData;
import com.ctb.bean.testAdmin.TestletLevelForm;
import com.ctb.bean.testAdmin.TimeZones;
import com.ctb.bean.testAdmin.User;
import com.ctb.bean.testAdmin.UserData;
import com.ctb.bean.testAdmin.UserNode;
import com.ctb.bean.testAdmin.UserNodeData;
import com.ctb.bean.testAdmin.ScoreDetails.OrderByItemSetOrder;
import com.ctb.bean.testAdmin.ScoreDetails.ResponseResultDetails;
import com.ctb.bean.testAdmin.ScoreDetails.ResponseResultDetails.OrderByItemOrder;
import com.ctb.control.db.OrgNode;
import com.ctb.exception.CTBBusinessException;
import com.ctb.exception.testAdmin.InsufficientLicenseQuantityException;
import com.ctb.exception.testAdmin.ManifestUpdateFailException;
import com.ctb.exception.testAdmin.TransactionTimeoutException;
import com.ctb.exception.validation.ValidationException;
import com.ctb.testSessionInfo.data.SubtestVO;
import com.ctb.testSessionInfo.dto.Message;
import com.ctb.testSessionInfo.dto.MessageInfo;
import com.ctb.testSessionInfo.dto.PasswordInformation;
import com.ctb.testSessionInfo.dto.ReportManager;
import com.ctb.testSessionInfo.dto.SubtestDetail;
import com.ctb.testSessionInfo.dto.TestRosterFilter;
import com.ctb.testSessionInfo.dto.TestRosterVO;
import com.ctb.testSessionInfo.dto.TestSessionVO;
import com.ctb.testSessionInfo.dto.UserProfileInformation;
import com.ctb.testSessionInfo.utils.Base;
import com.ctb.testSessionInfo.utils.BaseTree;
import com.ctb.testSessionInfo.utils.DateUtils;
import com.ctb.testSessionInfo.utils.FilterSortPageUtils;
import com.ctb.testSessionInfo.utils.Organization;
import com.ctb.testSessionInfo.utils.OrgnizationComparator;
import com.ctb.testSessionInfo.utils.PermissionsUtils;
import com.ctb.testSessionInfo.utils.Row;
import com.ctb.testSessionInfo.utils.SSOSig;
import com.ctb.testSessionInfo.utils.ScheduleTestVo;
import com.ctb.testSessionInfo.utils.ScheduledSavedStudentDetailsVo;
import com.ctb.testSessionInfo.utils.ScheduledSavedTestVo;
import com.ctb.testSessionInfo.utils.TestSessionUtils;
import com.ctb.testSessionInfo.utils.TreeData;
import com.ctb.testSessionInfo.utils.UserOrgHierarchyUtils;
import com.ctb.testSessionInfo.utils.UserPasswordUtils;
import com.ctb.testSessionInfo.utils.WebUtils;
import com.ctb.util.HMACQueryStringEncrypter;
import com.ctb.util.LayoutUtil;
import com.ctb.util.OperationStatus;
import com.ctb.util.SuccessInfo;
import com.ctb.util.ValidationFailedInfo;
import com.ctb.util.testAdmin.TestAdminStatusComputer;
import com.ctb.util.web.sanitizer.SanitizedFormData;
import com.ctb.widgets.bean.ColumnSortEntry;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



@Jpf.Controller()
public class SessionOperationController extends PageFlowController {
	private static final long serialVersionUID = 1L;

    /**
     * @common:control
     */
	 
    @Control()
    private com.ctb.control.testAdmin.TestSessionStatus testSessionStatus;

    @Control()
    private com.ctb.control.userManagement.UserManagement userManagement;
    
    @Control()
    private com.ctb.control.licensing.Licensing licensing;
    
    @Control()
    private com.ctb.control.testAdmin.ScheduleTest scheduleTest;
    
    @Control()
    private com.ctb.control.db.ItemSet itemSet;
    
    @Control()
    private com.ctb.control.db.BroadcastMessageLog message;
        
    @Control()
    private com.ctb.control.db.Users users;

	@Control
	private OrgNode orgNode; 
	
	@Control()
	private com.ctb.control.db.OrgNode orgnode;
    
    /**
     * @common:control
     */
    @org.apache.beehive.controls.api.bean.Control()
    private com.ctb.control.db.CustomerReportBridge ReportBridge;
    
	TestletLevelForm[] forms = null;
	
    //Added for view/monitor test status
   
    protected void onCreate() {
	}

	/**
	 * Callback that is invoked when this controller instance is destroyed.
	 */
	@Override
	protected void onDestroy(HttpSession session) {
	}
	
	private boolean sessionDetailsShowScores = false;
	private boolean subtestValidationAllowed = false;
	private List studentStatusSubtests = null; 
	private boolean showStudentReportButton = false;
	private String genFile = null;
	private List TABETestElements = null;    
	//goto json
	private TestRosterFilter  testRosterFilter = null;
	private ArrayList selectedRosterIds = null;
	public CustomerConfiguration[] customerConfigurations = null;
	private CustomerConfigurationValue[] customerConfigurationsValue = null;
	//private String userName = (String)getSession().getAttribute("userName");
	private Integer sessionId = null;
	private String[] testStatusOptions = {FilterSortPageUtils.FILTERTYPE_SHOWALL, FilterSortPageUtils.FILTERTYPE_COMPLETED, FilterSortPageUtils.FILTERTYPE_INCOMPLETE, 
            FilterSortPageUtils.FILTERTYPE_INPROGRESS, FilterSortPageUtils.FILTERTYPE_NOTTAKEN, FilterSortPageUtils.FILTERTYPE_SCHEDULED, 
            FilterSortPageUtils.FILTERTYPE_STUDENTSTOP, FilterSortPageUtils.FILTERTYPE_SYSTEMSTOP, FilterSortPageUtils.FILTERTYPE_TESTLOCKED,
            FilterSortPageUtils.FILTERTYPE_TESTABANDONED, FilterSortPageUtils.FILTERTYPE_STUDENTPAUSE};
	private String setCustomerFlagToogleButton="false";
	private String[] validationStatusOptions = {FilterSortPageUtils.FILTERTYPE_SHOWALL, FilterSortPageUtils.FILTERTYPE_INVALID, FilterSortPageUtils.FILTERTYPE_VALID};
	
	private String fileName = null;
	private String fileType = null;
	private String userEmail = null;
	private List fileTypeOptions = null;
	public boolean isLasLinkCustomer = false;
	public boolean isOKCustomer = false;
	public boolean isWVCustomer = false;
	public boolean isTABECustomer = false;
	public boolean isTABEAdaptiveCustomer = false;
	public boolean isTASCCustomer = false;
	public boolean isEngradeCustomer = false;
	public boolean isTASCReadinessCustomer = false;
	public boolean isTERRANOVA_Customer = false;
	private boolean forceTestBreak = false;
	private boolean selectGE = false;
	private boolean isTABELocatorOnlyTest = false;
	public String testletSessionEndDate = "01/01/17";
	public boolean isCopySession = false;
	
	//Added for view/monitor test status
    
	private String userName = null;
	private Integer customerId = null;
    private User user = null;
    private List<TestSessionVO> sessionListCUFU = new ArrayList<TestSessionVO>(); 
    private List<TestSessionVO> sessionListPA = new ArrayList<TestSessionVO>(); 
    Map<Integer,Map> sessionListCUFUMap = null;
    Map<Integer,Map> sessionListPAMap = null;
    private boolean hasLicenseConfig = false; 
    public static final String CONTENT_TYPE_JSON = "application/json";
    public LinkedHashMap<String, String> hintQuestionOptions = null;
    public UserProfileInformation userProfile = null; 
	private TestProduct [] tps;
	private static final String ACTION_INIT = "init";
	boolean isPopulatedSuccessfully = false;
	ScheduleTestVo vo = new ScheduleTestVo();
	
	Map<Integer, String> topNodesMap = new LinkedHashMap<Integer, String>();
	
	private List<String> studentGradesForCustomer;
	
    public ReportManager reportManager = null;
    private CustomerReportData customerReportData = null;                
    private UserNodeData userTopNodes = null;
    private ProgramData userPrograms = null;
    public LinkedHashMap timeZoneOptions = null;	 
	
	private List<TestElement> subtestDetails = null;
    private int numberSelectedSubtests = 0;
    private String selectedProductType = "ST";

    private String currentReportUrl = "";
    private String currentTestAdminId = "";
   private String pageSize=null;
   private boolean pageSizeconfig = false;
   private Boolean isLASManageLicense = Boolean.FALSE;
   
   /* Changes for DEX Story - Add intermediate screen : Start */
    private boolean isEOIUser = false;
	private boolean isMappedWith3_8User = false;
	private boolean is3to8Selected = false;
	private boolean isEOISelected = false;
	private boolean isUserLinkSelected = false;
	private boolean disableStudentIndividualAndMultipleTestTicket = false;
   /* Changes for DEX Story - Add intermediate screen : End */
	private boolean hasDefaultTestingWindowConfig = false; //Added for user story : GA � TAS default new session duration to 5 days
	private boolean hasViewResponseResultConf = false; //Added for user story : 
   
	private boolean hasShowRosterAccomAndHierarchy = false;
	private List<ScoreDetails> sdForAllSubtests = null;
	public LinkedHashMap getTimeZoneOptions() {
		return timeZoneOptions;
	}
    
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the customerId
	 */
	public Integer getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	/**
	 * @jpf:action
	 * @jpf:forward name="success" path="organizations.do"
	 */
	@Jpf.Action(forwards = { 
	        @Jpf.Forward(name = "resetPassword", path = "resetPassword.do"), 
	        @Jpf.Forward(name = "setTimeZone", path = "setTimeZone.do"),
			@Jpf.Forward(name = "currentUI", path = "gotoCurrentUI.do"),
			@Jpf.Forward(name = "legacyUI", path = "gotoLegacyUI.do"),
			@Jpf.Forward(name = "switchUserLogin", path = "switchUserLogin.do")
	})
	protected Forward begin()
	{
		String forwardName = "currentUI";
		clearSessionAttributes(); // reset OK SSO related session variables
		//System.out.println("userName from session in test-session module >> "+getSession().getAttribute("userName"));
		/* Changes for DEX Story - Add intermediate screen : Start */
		//getSession().setAttribute("isDexEOILogin", "true");// need to set this value from request parameter from Dex SSO : For logout story		
		//System.out.println("isDexEOILogin set in session [test-session module] >> "+getSession().getAttribute("isDexEOILogin"));
		try {
			this.isEOIUser = this.userManagement.isOKEOIUser(getRequest().getUserPrincipal().toString()); //need to check and populate this flag
			this.isMappedWith3_8User = this.userManagement.isMappedWith3_8User(getRequest().getUserPrincipal().toString()); //need to check and populate this flag
			getSession().setAttribute("isEOIUser",this.isEOIUser);
			getSession().setAttribute("isMappedWith3_8User",this.isMappedWith3_8User);
		} catch (CTBBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				   	
    /* Changes for DEX Story - Add intermediate screen : End */	
		getLoggedInUserPrincipal();		
		getUserDetails();
        
    	CustomerConfiguration [] customerConfigs = getCustomerConfigurations(this.customerId);
		if (accessNewUI(customerConfigs)) {
			setupUserPermission(customerConfigs);
			
			if (isUserPasswordExpired()|| "T".equals(this.user.getResetPassword())) {
	        	forwardName = "resetPassword";
	        }
	        else if (this.user.getTimeZone() == null) {
	        	forwardName = "setTimeZone";
	        }
	        
		}
		else {
			forwardName = "legacyUI";	
		}    		
	 //Changes for DEX Story - Add intermediate screen
		if(!(forwardName.equals("resetPassword") || forwardName.equals("setTimeZone"))) {
			if(this.isEOIUser && this.isMappedWith3_8User){
	    		//getSession().setAttribute(arg0, arg1);
	    		forwardName = "switchUserLogin";
	    	}
		}
    			
		return new Forward(forwardName);
	} 
	
	private void clearSessionAttributes(){
		if(getSession().getAttribute("is3to8Selected") != null)
			getSession().removeAttribute("is3to8Selected");
		if(getSession().getAttribute("isEOISelected") != null)
			getSession().removeAttribute("isEOISelected");
		if(getSession().getAttribute("isUserLinkSelected") != null)
			getSession().removeAttribute("isUserLinkSelected");
	}
	/* Changes for DEX Story - Add intermediate screen : Start */
	@Jpf.Action(forwards = {
			@Jpf.Forward(name = "success", path = "switch_user_login.jsp")
		})
	protected Forward switchUserLogin()
	{
		return new Forward("success");
	}
	
	@Jpf.Action()
	protected Forward switchToLinkSelected()
    {
    	String selectedLink =(String) getRequest().getParameter("selectedLink");
    	
    	if(selectedLink.equals("3-8_Link")) {
    		getSession().setAttribute("is3to8Selected", "true");
    		this.is3to8Selected = true;
    		getSession().setAttribute("isEOISelected", "false");
    		this.isEOISelected = false;
    		getSession().setAttribute("isUserLinkSelected", "false");   
    		this.isUserLinkSelected = false;
    	} else if(selectedLink.equals("EOI_Link")) {
    		getSession().setAttribute("isEOISelected", "true");
    		this.isEOISelected = true;
    		getSession().setAttribute("is3to8Selected", "false");
    		this.is3to8Selected = false;
    		getSession().setAttribute("isUserLinkSelected", "false");   
    		this.isUserLinkSelected = false;
    	} else if(selectedLink.equals("UserLink")){
    		//do nothing: need to switch to Manage User module with EOI login
    		getSession().setAttribute("isUserLinkSelected", "true");   
    		this.isUserLinkSelected = true;
    		getSession().setAttribute("isEOISelected", "false");
    		this.isEOISelected = false;
    		getSession().setAttribute("is3to8Selected", "false");
    		this.is3to8Selected = false;
    	}
    	
    	try
        {
            String url = "/SessionWeb/sessionOperation/beginForSwitchingUserLogin.do";
            getResponse().sendRedirect(url);
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
    }
	
	@Jpf.Action(forwards = { 
	        @Jpf.Forward(name = "resetPassword", path = "resetPassword.do"), 
	        @Jpf.Forward(name = "setTimeZone", path = "setTimeZone.do"),
			@Jpf.Forward(name = "currentUI", path = "gotoCurrentUI.do"),
			@Jpf.Forward(name = "legacyUI", path = "gotoLegacyUI.do"),
			@Jpf.Forward(name = "switchUserLogin", path = "switchUserLogin.do"),
			@Jpf.Forward(name = "redirectToManageUser", path = "redirectToManageUser.do")
	})
	protected Forward beginForSwitchingUserLogin() throws CTBBusinessException
	{
		String forwardName = "currentUI";
		
		if(this.isUserLinkSelected)
			forwardName = "redirectToManageUser";
		
		if(getSession().getAttribute("isEOIUser") != null)
			this.isEOIUser = new Boolean(getSession().getAttribute("isEOIUser").toString()).booleanValue();
		else
			this.isEOIUser = this.userManagement.isOKEOIUser(getRequest().getUserPrincipal().toString()); //need to check and populate this flag

		if(getSession().getAttribute("isMappedWith3_8User") != null)
			this.isMappedWith3_8User = new Boolean(getSession().getAttribute("isMappedWith3_8User").toString()).booleanValue();
		else
			this.isMappedWith3_8User = this.userManagement.isMappedWith3_8User(getRequest().getUserPrincipal().toString()); //need to check and populate this flag
		
		getLoggedInUserPrincipal();		
		getUserDetails();

    	CustomerConfiguration [] customerConfigs = getCustomerConfigurations(this.customerId);
    	
    	if (accessNewUI(customerConfigs)) {
			setupUserPermission(customerConfigs);
			
			/*if (isUserPasswordExpired()|| "T".equals(this.user.getResetPassword())) {
	        	forwardName = "resetPassword";
	        }
	        else if (this.user.getTimeZone() == null) {
	        	forwardName = "setTimeZone";
	        }*/
	        
		}
		else {
			forwardName = "legacyUI";	
		}
		
		return new Forward(forwardName);
	} 
	
	@Jpf.Action()
	protected Forward redirectToManageUser()
    {   	
    	try
        {
            String url = "/UserWeb/userOperation/organizations_manageUsers.do?isUserLinkSelected="+this.isUserLinkSelected;
            getResponse().sendRedirect(url);
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
    }
	/* Changes for DEX Story - Add intermediate screen : End */
	
    @Jpf.Action(forwards = { 
            @Jpf.Forward(name = "success", path = "assessments_sessions.do") 
        }) 
    protected Forward gotoCurrentUI()
    {
    	//System.out.println("gotoCurrentUI......");
		List broadcastMessages = BroadcastUtils.getBroadcastMessages(this.message, this.userName);
        this.getSession().setAttribute("broadcastMessages", new Integer(broadcastMessages.size()));
		    	
        return new Forward("success");
    }
	
    @Jpf.Action()
    protected Forward gotoLegacyUI()
    {
        try
        {
            String url = "/TestSessionInfoWeb/homepage/HomePageController.jpf";
            getResponse().sendRedirect(url);
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
    }
	
    /**
     * @jpf:action
     */
    @Jpf.Action(forwards = { 
            @Jpf.Forward(name = "success", path = "reset_password.jsp")
    })
    protected Forward resetPassword()
    {              
        initHintQuestionOptions();
    	
		try {
			this.user = userManagement.getUser(this.userName, this.userName);
		} catch (CTBBusinessException e) {
			e.printStackTrace();
		}
        this.userProfile = new UserProfileInformation(this.user);   
        
        String title = "Change Password: " + this.userProfile.getFirstName() + " " + this.userProfile.getLastName();
        this.getRequest().setAttribute("pageTitle", title);
        
        return new Forward("success");
    }

    /**
     * @jpf:action
     * @jpf:forward name="success" path="gotoCurrentUI.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success", path = "gotoCurrentUI.do"),
        @Jpf.Forward(name = "error", path = "reset_password.jsp"),
        @Jpf.Forward(name = "switchUserLogin", path = "switchUserLogin.do")
    })
    protected Forward savePassword()
    {
    	String forwardName = "success";
    	
		 String message = "";
		 String requiredFields = null;
		 boolean revalidate = true;
		 boolean validationPassed = true;
		 MessageInfo messageInfo = new MessageInfo();
		 
		 String newPassword = this.userProfile.getUserPassword().getNewPassword();
		 String confirmPassword = this.userProfile.getUserPassword().getConfirmPassword();		 
		 String oldPassword = this.userProfile.getUserPassword().getOldPassword();
		 String hintQuestionId = this.userProfile.getUserPassword().getHintQuestionId();
		 String hintAnswer = this.userProfile.getUserPassword().getHintAnswer();
		 
		 
		 PasswordInformation passwordinfo = new PasswordInformation();
		 passwordinfo.setOldPassword(oldPassword);
		 passwordinfo.setNewPassword(newPassword);
		 passwordinfo.setConfirmPassword(confirmPassword);
		 passwordinfo.setHintQuestionId(hintQuestionId);
		 passwordinfo.setHintAnswer(hintAnswer);
		 
		 requiredFields = UserPasswordUtils.getRequiredPasswordField(passwordinfo);
		 if( requiredFields != null) {
			 revalidate = false;
			 validationPassed = false;
				if ( requiredFields.indexOf(",") > 0){
					message = requiredFields + (" <br/> " + Message.REQUIRED_TEXT_MULTIPLE);
					messageInfo = createMessageInfo(messageInfo, Message.MISSING_REQUIRED_FIELDS, message, Message.ERROR, true, false );
				}
				else {
					message = requiredFields + (" <br/> " + Message.REQUIRED_TEXT);
					messageInfo = createMessageInfo(messageInfo, Message.MISSING_REQUIRED_FIELD, message, Message.ERROR, true, false );
	
				}
		 }
		 else 
		 if (UserPasswordUtils.isPasswordDifferent(this.user.getPassword(), oldPassword)) {
			 validationPassed = false;
		 	 messageInfo = createMessageInfo(messageInfo, Message.CHANGE_PASSWORD_TITLE, Message.WRONG_PASSWORD, Message.ERROR, true, false );
		 }
		 else {
			 	String invalidCharFields = UserPasswordUtils.verifyPasswordInfo(passwordinfo);
			 	String invalidString = "";
	
				if (invalidCharFields != null && invalidCharFields.length() > 0) {
					 
					 if ( invalidCharFields.indexOf(",") > 0){
						 
						 invalidString = invalidCharFields + ("<br/>" + Message.INVALID_DEX_PASSWORD);
						 
					 } else {
						 
						 invalidString = invalidCharFields + ("<br/>" + Message.INVALID_DEX_PASSWORD_SINGLE_LINE);
						 
					 }
	
						
				}
	
				if (invalidString != null && invalidString.length() > 0) {
					 revalidate = false;
					 validationPassed = false;
					 messageInfo = createMessageInfo(messageInfo, Message.INVALID_CHARS_TITLE, invalidString, Message.ERROR, true, false );
				 }
				 
				if (revalidate) {
					 boolean isNewAndConfirmPasswordDifferent = UserPasswordUtils.isNewAndConfirmPasswordDifferent(passwordinfo);
					 
					 if(isNewAndConfirmPasswordDifferent) {
						 validationPassed = false;
					 	 messageInfo = createMessageInfo(messageInfo, Message.CHANGE_PASSWORD_TITLE, Message.PASSWORD_MISMATCH, Message.ERROR, true, false );
					 }
				 }
		 }		 
		 		
		 boolean passwordSaved = false;
		 
		 if (validationPassed) {
			 this.user.setResetPassword("F");
			 this.user.setPasswordHintQuestionId(new Integer(hintQuestionId));
			 this.user.setPasswordHintAnswer(hintAnswer);
			 this.user.setPassword(oldPassword);
			 this.user.setNewPassword(newPassword);
			 passwordSaved = true;
			 
			 try {
				 this.userManagement.updateUser(this.user.getUserName(),this.user);
			 } catch (CTBBusinessException be) {
				 be.printStackTrace();
	             String msg = MessageResourceBundle.getMessage(be.getMessage());
			 	 messageInfo = createMessageInfo(messageInfo, Message.CHANGE_PASSWORD_TITLE, msg, Message.ERROR, true, false );
				 passwordSaved = false;
			 }
			 
		 }
		 
		 if (! passwordSaved) {
			 String title = "Change Password: " + this.userProfile.getFirstName() + " " + this.userProfile.getLastName();
			 this.getRequest().setAttribute("pageTitle", title);

			 this.getRequest().setAttribute("errorMsg", messageInfo.getContent());
        
			 forwardName = "error";
		 }
		 
		 if(forwardName.equals("success")) {
			if(getSession().getAttribute("isEOIUser") != null)
				this.isEOIUser = new Boolean(getSession().getAttribute("isEOIUser").toString()).booleanValue();
				
			if(getSession().getAttribute("isMappedWith3_8User") != null)
				this.isMappedWith3_8User = new Boolean(getSession().getAttribute("isMappedWith3_8User").toString()).booleanValue();
			
			if(this.isEOIUser && this.isMappedWith3_8User){
				//getSession().setAttribute(arg0, arg1);
				forwardName = "switchUserLogin";
			}
		 }
		
        return new Forward(forwardName);
    }    
    
	private MessageInfo createMessageInfo(MessageInfo messageInfo, String messageTitle, String content, String type, boolean errorflag, boolean successFlag){
		messageInfo.setTitle(messageTitle);
		messageInfo.setContent(content);
		messageInfo.setType(type);
		messageInfo.setErrorFlag(errorflag);
		messageInfo.setSuccessFlag(successFlag);
		return messageInfo;
	}
    
    /**
     * @jpf:action
     */
    @Jpf.Action(forwards = { 
            @Jpf.Forward(name = "success", path = "set_timezone.jsp") 
        }) 
    protected Forward setTimeZone()
    {   
        initTimeZoneOptions();
    	
		try {
			this.user = userManagement.getUser(this.userName, this.userName);
		} catch (CTBBusinessException e) {
			e.printStackTrace();
		}
        this.userProfile = new UserProfileInformation(this.user);   
        
        this.getRequest().setAttribute("organizationNodes", this.userProfile.getOrganizationNodes());
        
        return new Forward("success");
    }
    
    
    /**
     * @jpf:action
     * @jpf:forward name="success" path="gotoCurrentUI.do"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success", path = "gotoCurrentUI.do"),
        @Jpf.Forward(name = "error", path = "set_timezone.jsp"),
        @Jpf.Forward(name = "switchUserLogin", path = "switchUserLogin.do")
    })
    protected Forward saveTimeZone()
    {
    	String forwardName = "success";

		String timeZone = this.userProfile.getTimeZone();
		if (timeZone.length() > 0) {
			this.user.setTimeZone(timeZone);
			try {			
		        this.userManagement.updateUser(this.userName, this.user);
			} catch (CTBBusinessException e) {
				e.printStackTrace();
				forwardName = "error";
			}
		}
		else {
			getRequest().setAttribute("errorMsg", "Please select a time zone to continue");
			forwardName = "error";   
		}
				
        this.getRequest().setAttribute("organizationNodes", this.userProfile.getOrganizationNodes());
        
        if(forwardName.equals("success")) {
			if(getSession().getAttribute("isEOIUser") != null)
				this.isEOIUser = new Boolean(getSession().getAttribute("isEOIUser").toString()).booleanValue();
				
			if(getSession().getAttribute("isMappedWith3_8User") != null)
				this.isMappedWith3_8User = new Boolean(getSession().getAttribute("isMappedWith3_8User").toString()).booleanValue();
			
			if(this.isEOIUser && this.isMappedWith3_8User){
				//getSession().setAttribute(arg0, arg1);
				forwardName = "switchUserLogin";
			}
		 }
        
        return new Forward(forwardName);    	
    }
    
    
    /**
     * initTimeZoneOptions	
    */
    private void initTimeZoneOptions()
    {        
        this.timeZoneOptions = new LinkedHashMap();              
        this.timeZoneOptions.put("", Message.SELECT_TIME_ZONE);
          
        try {
            TimeZones[] timeZones = this.userManagement.getTimeZones();
            if (timeZones != null) {
               for (int i = 0 ; i < timeZones.length ; i++) {
                    this.timeZoneOptions.put(timeZones[i].getTimeZone(), 
                            timeZones[i].getTimeZoneDesc());
                } 
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Jpf.Action()
    protected Forward validateEndDateForLaslinkLM()
    {
    	initialize();
    	String jsonData = "";
    	HttpServletResponse resp = getResponse();
    	resp.setCharacterEncoding("UTF-8"); 
    	OutputStream stream = null;    	
    	String selectedOrgIds =this.getRequest().getParameter("selectedOrgIds");
    	LASLicenseNode nonZeroActivePO = null;
    	  try
          {
          	System.out.println("this.isLASManageLicense >> "+this.isLASManageLicense);  	
          	if(this.isLASManageLicense){  
          		nonZeroActivePO = this.scheduleTest.getNonZeroActivePOForSelectedOrg(this.customerId,selectedOrgIds);
          		vo.setNonZeroActivePO(nonZeroActivePO);
          		Gson gson = new Gson();
            	jsonData = gson.toJson(vo);
            	System.out.println(jsonData);
            	
            
            	resp.setContentType(CONTENT_TYPE_JSON);

				stream = resp.getOutputStream();
				stream.write(jsonData.getBytes("UTF-8"));
				resp.flushBuffer();
          }
         }catch (Exception e) {
             try {
				resp.flushBuffer();
			} catch (Exception e1) {
			}
			e.printStackTrace();
		} finally{
			if (stream!=null){
				try {
					stream.close();
				} catch (Exception e) {
				}
			}
		}
    	
    	return null;
    }
    
    
    @Jpf.Action()
    protected Forward selectTest(SessionOperationForm form)
    {
    	initialize();
    	String jsonData = "";
    	HttpServletResponse resp = getResponse();
    	resp.setCharacterEncoding("UTF-8"); 
    	OutputStream stream = null;
        //String currentAction = this.getRequest().getParameter("currentAction");
        String selectedProductId =  null;
        String userTimeZone = null;
        Map<String,ArrayList> classHierarchy = new HashMap<String,ArrayList>();
        /*if(currentAction==null)
        {
        	currentAction=ACTION_INIT;
        } */
        
          try
        {    	  
        	userTimeZone =  userManagement.getUserTimeZone(this.userName);
            if (!isPopulatedSuccessfully || (this.isEOIUser && this.isMappedWith3_8User)){
            	TestProductData testProductData  = this.getTestProductDataForUser();
            	tps = testProductData.getTestProducts();
            	 if( tps!=null ) {
            		//vo.setUserTimeZone(DateUtils.getUITimeZone(userTimeZone));
            		 if(this.isEOIUser && this.isMappedWith3_8User) {
            			 vo=new ScheduleTestVo();
            		 }
            		 if(isLaslinkCustomer(this.customerConfigurations))
            			 vo.setLaslinkCustomer(true);
            		vo.populate(userName, tps, itemSet, scheduleTest);
                 	vo.populateTopOrgnode(this.topNodesMap);
                 	vo.populateLevelOptions();
            	 }
            	 isPopulatedSuccessfully = true;
            }
        	           
           /* if(selectedProductId== null || selectedProductId.trim().length()==0)
            {*/
                if (tps.length > 0 && tps[0] != null)
                {
                     selectedProductId = tps[0].getProductId().toString();
                     vo.populateAccessCode(scheduleTest);
                     vo.setUserTimeZone(DateUtils.getUITimeZone(userTimeZone));
                     vo.populateTimeZone();
                     if(this.hasDefaultTestingWindowConfig){
                    	 Integer days = getDefaultTestingWindowValue();
                    	 if(null != days){
	                    	 if(days.intValue() == 1){
	                    		 vo.populateDefaultDateAndTime(userTimeZone,testletSessionEndDate);
	                    	 }else{
		                    	 vo.populateDefaultDateAndTime(userTimeZone, days);
		                    	 vo.setTestingWindowDefaultDays(days);
	                    	 }
	                     }else{
	                    	 vo.populateDefaultDateAndTime(userTimeZone,testletSessionEndDate);
	                     }
                     }else{
                    	 vo.populateDefaultDateAndTime(userTimeZone,testletSessionEndDate);
                     }
                }
          /* } */
            if(tps.length<=0) {
            	
            	vo.setNoTestExists(true);
            }else {
            	 vo.setNoTestExists(false);
            	 vo.setSelectedProductId(selectedProductId);
                 //vo.setUserTimeZone(DateUtils.getUITimeZone(userTimeZone));
                 
                 int selectedProductIndex = getProductIndexByID(selectedProductId);
           
                            
                 //this.condition.setOffGradeTestingDisabled(Boolean.FALSE);
                
                 
                 String acknowledgmentsURL =  tps[selectedProductIndex].getAcknowledgmentsURL();
                 if (acknowledgmentsURL != null)
                 {
                     acknowledgmentsURL = acknowledgmentsURL.trim();
                     if (!"".equals(acknowledgmentsURL))
                         this.getRequest().setAttribute("acknowledgmentsURL", acknowledgmentsURL);
                 }
            }
            
            if(isAdminUser() && this.isOKCustomer && isTopLevelUser()) {
            	vo.setOkAdmin(true);
            } else {
            	vo.setOkAdmin(false);
            }
            
            if(isAdminUser() && this.isWVCustomer && isTopLevelUser()) {
            	vo.setWVAdmin(true);
            } else {
            	vo.setWVAdmin(false);
            }
    
            vo.setForceTestBreak(this.forceTestBreak);
            if (this.selectGE)
            	vo.setSelectGE(Boolean.TRUE);
            else
            	vo.setSelectGE(null);
            
            if(this.hasShowRosterAccomAndHierarchy){
	    		classHierarchy = this.scheduleTest.getScheduledStudentsClassHierarchy(this.userName);
	    		vo.setHasShowRosterAccomAndHierarchy(this.hasShowRosterAccomAndHierarchy);	    		
	    	}
            vo.setClassHierarchyMap(classHierarchy);
            Gson gson = new Gson();
        	jsonData = gson.toJson(vo);
        	//System.out.println(jsonData);
        	try {

    			resp.setContentType(CONTENT_TYPE_JSON);
    			//resp.flushBuffer();
    			stream = resp.getOutputStream();
    			stream.write(jsonData.getBytes("UTF-8"));
    			resp.flushBuffer();
    		} catch (IOException e) {
    			
    			e.printStackTrace();
    		} 
    	
        } catch (Exception e) {
        	resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
        	try {
				resp.flushBuffer();
			} catch (Exception e1) {
			}
			e.printStackTrace();
		} finally{
			if (stream!=null){
				try {
					stream.close();
				} catch (Exception e) {
				}
			}
		}
    	
    	
		return null;
    	
		
        
    }
    
    	@Jpf.Action()
        protected Forward saveTest(SessionOperationForm form)
        {
    		
    		Integer studentCountAfterSave = 0;
    		Integer testAdminId =null;
    		ValidationFailedInfo validationFailedInfo = new ValidationFailedInfo();
    		SuccessInfo successInfo = new SuccessInfo();
            boolean isAddOperation = true;
            int studentCountBeforeSave =0;
            boolean isValidationFailed = false;
            String jsonData = "";
            OperationStatus status = new OperationStatus();
            HttpServletResponse resp = getResponse();
        	resp.setCharacterEncoding("UTF-8"); 
        	OutputStream stream = null;
        	String testAdminIdString = (RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.TEST_ADMIN_ID, false, null));
        	String currentAction = RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.ACTION, true, "");
        	String checkRestrictedStr = RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.REMOVE_RESTRICTED_STD_AND_SAVE, true, "true");
        	Boolean checkRestricted = Boolean.valueOf(checkRestrictedStr.trim().toLowerCase());       	
        	String includeGEStr = RequestUtil.getValueFromRequest(this.getRequest(), "includeGE", false, null);
        	
        	SessionStudent[] restStudent = null;
        	boolean licenseValidationFailed = false ;
        	
        	ScheduledSavedTestVo vo = new ScheduledSavedTestVo();
        	TestSessionVO testSessionVO = null;
        	if(currentAction.equalsIgnoreCase("EDIT")){
        		isAddOperation = false;
        	} else if(currentAction.equalsIgnoreCase("COPY")){
        		isAddOperation = true;
        	} else if (currentAction.equalsIgnoreCase("ADD")){
        		isAddOperation = true;
        	} else if (testAdminIdString == null) {
        		isAddOperation = false;
        	} else {
        		try{
        			testAdminId = Integer.valueOf(testAdminIdString.trim());
        			isAddOperation = true;
        		} catch (Exception ne){
        			isAddOperation = false;
        		}
        	}
        	
        	
            try
            {
            	ScheduledSession session = populateAndValidateSessionRecord(this.getRequest(), validationFailedInfo, isAddOperation, currentAction); //modified for copy test sesssion
            	
            	if(!validationFailedInfo.isValidationFailed()) {
            		populateFirstLastName(session, this.getRequest()); // Added for WV Customer - Student Password generation
            	}
            	
            	if (includeGEStr != null) {
            		session.getTestSession().setLexingtonVersion(includeGEStr);
            	}
            	
            	if(!validationFailedInfo.isValidationFailed()) {
            		isValidationFailed = false;
            		
            		if(!checkRestricted){
                		removeRestrictedStudentsFromTest(session);
                	} else {
                		restStudent = getRestrictedStudentsForTest(session).getSessionStudents();
                		for(SessionStudent sstd :restStudent){
                			TestSession ts = sstd.getStatus().getPriorSession();
                			ts.setLoginStartDateString(DateUtils.formatDateToDateString(ts.getLoginStartDate() ));
                            ts.setLoginEndDateString(DateUtils.formatDateToDateString(ts.getLoginEndDate() ));
                		}
                	}
            		studentCountBeforeSave = session.getStudents().length;
            		if(restStudent == null || restStudent.length ==0) {
            			
            			/*Checking if the customer has license feature and any license is present is present for that user. If no license is present then
            			 *throw an error and do not allow to save the session */
            			/** To Fix Defect 80527 "productId == 4201" (TABE Testlet) condition is inserted **/
            			
            		                int productId=session.getTestSession().getProductId();
            		                if(!(productId == 4008 || productId == 4013 || productId == 4201) && session.getStudents().length!=0 && isTABECustomer && isAddOperation) {
            		                	
            		                	Set<Integer> orgNodes=new HashSet<Integer>();
            		                	SessionStudent[] students=session.getStudents();
            		                	for(int i=0;i<students.length;i++) {
            		                		orgNodes.add(students[i].getOrgNodeId());
            		                	}
            		                	String orgNodeIDstring ="";
            		                	 for (Iterator<Integer> it = orgNodes.iterator(); it.hasNext(); ) {
            		                		 Integer orgNodeID = it.next();
            		                		 orgNodeIDstring = orgNodeIDstring +orgNodeID.toString()+",";
            		                	 }
            		                	 orgNodeIDstring = orgNodeIDstring.substring(0,(orgNodeIDstring.length()-1));
            		                	 System.out.println(">>>>>>>>>>"+orgNodeIDstring);
            		                     
				            			 licenseValidationFailed = checkLicenseForCustomer(this.customerId,studentCountBeforeSave,orgNodeIDstring,session);
				            			 System.out.println("licenseValidationFailed:"+ licenseValidationFailed);
				            			 if (licenseValidationFailed)
				            				 throw new InsufficientLicenseQuantityException("Not enough license..") ;
            		                }
            			/*end checking license count*/
            		    /*To Fix TABE License Defects 80405 & 80408*/            
            		    if( null != session && null != session.getTestSession() && null != session.getTestSession().getTestAdminId() 
            		    		&& this.hasLicenseConfig && null != this.customerId) {          
            		    	this.scheduleTest.updateLicenseCountEditSessionCatalogChange(session, this.customerId);             
            		    }
            		    /*End Defect Fix*/
            			testAdminId = saveOrUpdateTestSession( session );
                		TestSessionData testSessionData = this.testSessionStatus.getTestSessionDetails(this.userName, testAdminId);
                		RosterElementData red = this.testSessionStatus.getRosterForTestSession(this.userName, testAdminId, null, null, null);
                		testSessionVO = new TestSessionVO(testSessionData.getTestSessions()[0]);
            			testSessionVO.setId(testAdminId);
                		studentCountAfterSave = red.getTotalCount().intValue();  
            		} else {
            			isValidationFailed = true;
            			vo.setRestrictedStudents(restStudent);
            			vo.setTotalStudent(session.getStudents().length);
            			status.setSuccess(false);

            		}

            	} else {
                  	isValidationFailed = true;
                }
                
                                      
                                
            }   
            catch (InsufficientLicenseQuantityException e)
            {
                e.printStackTrace();
                String errorMessageHeader =  MessageResourceBundle.getMessage("SelectSettings.InsufficentLicenseQuantity.E001.Header");
                String errorMessageBody =  MessageResourceBundle.getMessage("SelectSettings.InsufficentLicenseQuantity.E001.Body");
                
                validationFailedInfo.setKey("SelectSettings.InsufficentLicenseQuantity.E001");
                validationFailedInfo.setMessageHeader(errorMessageHeader);
                validationFailedInfo.updateMessage(errorMessageBody);
                isValidationFailed = true;
        
            } 
            //START- Changed for deferred defect 64446
            catch (TransactionTimeoutException e)
            {
                e.printStackTrace();
                String errorMessageHeader = MessageResourceBundle.getMessage("SelectSettings.FailedToSaveTestSessionTransactionTimeOut.Header");
                String errorMessageBody =  MessageResourceBundle.getMessage("SelectSettings.FailedToSaveTestSessionTransactionTimeOut.Body");
                                
                
                validationFailedInfo.setKey("SelectSettings.FailedToSaveTestSessionTransactionTimeOut");
                validationFailedInfo.setMessageHeader(errorMessageHeader);
                validationFailedInfo.updateMessage(errorMessageBody);
                isValidationFailed = true;
            }
            catch (CTBBusinessException e)
            {
                e.printStackTrace();
                isValidationFailed = true;
                
                if(e instanceof ValidationException){
                	String errorMessageHeader =MessageResourceBundle.getMessage("FailedToSaveTestSession.ValidationException.Header");
                	String errorMessageBody =MessageResourceBundle.getMessage("FailedToSaveTestSession.ValidationException.Body");
                	validationFailedInfo.setKey("SYSTEM_EXCEPTION");
                    validationFailedInfo.setMessageHeader(errorMessageHeader);
                    validationFailedInfo.updateMessage(errorMessageBody);
                	
                } else  {
                	 String errorMessageHeader =MessageResourceBundle.getMessage("FailedToSaveTestSession");
                	 String errorMessageBody = MessageResourceBundle.getMessage("FailedToSaveTestSession.Body", e.getMessage());
                     validationFailedInfo.setKey("SYSTEM_EXCEPTION");
                     validationFailedInfo.setMessageHeader(errorMessageHeader);
                     validationFailedInfo.updateMessage(errorMessageBody);
                }

            } 
           if (vo.getRestrictedStudents() == null && !isValidationFailed && studentCountBeforeSave <= studentCountAfterSave) {
        	   
           		String messageHeader = "";
           		if(isAddOperation) {
           			messageHeader = MessageResourceBundle.getMessage("SelectSettings.TestSessionSaved.Header");
           		} else {
           			messageHeader = MessageResourceBundle.getMessage("SelectSettings.TestSessionEdited.Header");
           		}
           		//String messageBody = MessageResourceBundle.getMessage("SelectSettings.TestSessionSaved.Body");
           		successInfo.setKey("TEST_SESSION_SAVED");
           		successInfo.setMessageHeader(messageHeader);
           		//successInfo.updateMessage(messageBody);
        	   	status.setSuccess(true); 
        	   	status.setSuccessInfo(successInfo);
        	   	//idToStudentMap.clear(); // clear map
        	   	
           } else if (vo.getRestrictedStudents() == null && !isValidationFailed) {
                int removedCount = studentCountBeforeSave - studentCountAfterSave;
                String messageHeader = "";
           		if(isAddOperation) {
           			messageHeader = MessageResourceBundle.getMessage("SelectSettings.TestSessionSaved.Header");
           		} else {
           			messageHeader = MessageResourceBundle.getMessage("SelectSettings.TestSessionEdited.Header");
           		}
           		String messageBody = MessageResourceBundle.getMessage("RestrictedStudentsNotSaved", "" +removedCount);
           		successInfo.setKey("TEST_SESSION_SAVED_RES_STD");
           		successInfo.setMessageHeader(messageHeader);
           		successInfo.updateMessage(messageBody);
                status.setSuccess(true);
                status.setSuccessInfo(successInfo);
             } else if(vo.getRestrictedStudents() == null ) {
            	status.setSuccess(false);
            	if("SYSTEM_EXCEPTION".equalsIgnoreCase(validationFailedInfo.getKey())){
            		status.setSystemError(true);
            	} else {
            		status.setSystemError(false);
            	}
            	status.setValidationFailedInfo(validationFailedInfo);
            }
           
          // if(vo.getRestrictedStudents() == null  ){
        	   vo.setOperationStatus(status);
          // }
           
           vo.setTestSession(testSessionVO);
           Gson gson = new Gson();
	       jsonData = gson.toJson(vo);
	  
	       	try {
	   			resp.setContentType(CONTENT_TYPE_JSON);
 	   			stream = resp.getOutputStream();
	   			stream.write(jsonData.getBytes("UTF-8"));
	   			resp.flushBuffer();
	   		} catch (IOException e) {
	   			e.printStackTrace();
   		} 
            return null;
          
        }
    
    	@Jpf.Action()
        protected Forward getUserProductsDetails(SessionOperationForm form) {
    		if((this.isEOIUser && this.isMappedWith3_8User)) {
    			initialize();
    		}
    		String jsonData = "";
    		OutputStream stream = null;
    		String selectedProductId = "";
    		HttpServletResponse resp = getResponse();
    	    resp.setCharacterEncoding("UTF-8"); 
    	    //String testAdminIdString = RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.TEST_ADMIN_ID, false, null);
    	    ScheduledSavedTestVo vo = new ScheduledSavedTestVo();
    	    OperationStatus status = new OperationStatus();
    	    vo.setOperationStatus(status) ;
    	  //added for copy test session
    	    String action = getRequest().getParameter("action"); 
//    	    System.out.println(">>>>>"+action);
    	    //
    	    
    	    try {

    	    	if (!isPopulatedSuccessfully || (this.isEOIUser && this.isMappedWith3_8User)){
                	TestProductData testProductData  = this.getTestProductDataForUser();
                	tps = testProductData.getTestProducts();
                	 if( tps!=null ) {
                		 if(this.isEOIUser && this.isMappedWith3_8User) {
                			 this.vo=new ScheduleTestVo();
                		 }
                		 this.vo.populate(userName, tps, itemSet, scheduleTest);
                		 this.vo.populateTopOrgnode(this.topNodesMap);
                		 this.vo.populateLevelOptions();
                	 }
                	 isPopulatedSuccessfully = true;
                }
            	           
              
                 if (tps.length > 0 && tps[0] != null)
                  {
                	 //productName = tps[0].getProductName();
                     selectedProductId = tps[0].getProductId().toString();
                     this.vo.populateAccessCode(scheduleTest);
                     this.vo.populateDefaultDateAndTime(this.user.getTimeZone(),testletSessionEndDate);
                     }
           
                if(tps.length<=0) {
                	
                	this.vo.setNoTestExists(true);
                }else {
                	this.vo.setNoTestExists(false);
                	this.vo.setSelectedProductId(selectedProductId);
                	this.vo.setUserTimeZone(DateUtils.getUITimeZone(this.user.getTimeZone()));
                     
                     int selectedProductIndex = getProductIndexByID(selectedProductId);
                    String acknowledgmentsURL =  tps[selectedProductIndex].getAcknowledgmentsURL();
                     if (acknowledgmentsURL != null)
                     {
                         acknowledgmentsURL = acknowledgmentsURL.trim();
                         if (!"".equals(acknowledgmentsURL))
                             this.getRequest().setAttribute("acknowledgmentsURL", acknowledgmentsURL);
                     }
                	
                }
                
                vo.setUserProductsDetails(this.vo);

    	    } catch(CTBBusinessException e){
    	    	 e.printStackTrace(); 
    	    	 status.setSystemError(true);
    	    	 ValidationFailedInfo validationFailedInfo = new ValidationFailedInfo();
    	    	 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("SelectSettings.FailedToLoadTestSession"));
    	    	 if(e.getMessage()!=null && e.getMessage().length()>0){
    	    		 validationFailedInfo.updateMessage(e.getMessage()); 
    	    	 }
    			 status.setValidationFailedInfo(validationFailedInfo);
    	    	
    	    } catch(Exception e) {
    	    	e.printStackTrace(); 
    	    	status.setSystemError(true);
    	    	 ValidationFailedInfo validationFailedInfo = new ValidationFailedInfo();
    	    	 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("System.Exception.Header"));
    			 validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("System.Exception.Body"));
    			 status.setValidationFailedInfo(validationFailedInfo);
    	    }
    		
			Gson gson = new Gson();
	
			jsonData = gson.toJson(vo);
			//System.out.println(jsonData);
			try {
				resp.setContentType(CONTENT_TYPE_JSON);
				stream = resp.getOutputStream();
				stream.write(jsonData.getBytes("UTF-8"));
				resp.flushBuffer();
			} catch (IOException e) {
				e.printStackTrace();
			}
	
			return null;
	   }
    	
    	@Jpf.Action()
        protected Forward getTestDetails(SessionOperationForm form) {
    		
    		String jsonData = "";
    		OutputStream stream = null;
    		HttpServletResponse resp = getResponse();
    	    resp.setCharacterEncoding("UTF-8"); 
    	    String testAdminIdString = RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.TEST_ADMIN_ID, false, null);
    	    ScheduledSavedTestVo vo = new ScheduledSavedTestVo();
    	    OperationStatus status = new OperationStatus();
    	    vo.setOperationStatus(status) ;
    	    String userTimeZone = null;
    	  //added for copy test session
    	    String action = getRequest().getParameter("action"); 
    	    String originalTestAdminName = null;
    	    String testAdminNameCopySession = null;
    	    LASLicenseNode oldestNonZeroActivePO = null;
    	    //
    	    try { 
    	    	Integer testAdminId = Integer.valueOf(testAdminIdString);
    	    	userTimeZone =  userManagement.getUserTimeZone(this.userName);
    	    	ScheduledSession scheduledSession = this.scheduleTest.getScheduledSessionDetails(this.userName, testAdminId);
    	    	
    	    	System.out.println("this.isLASManageLicense >> "+this.isLASManageLicense);  	
            	if(this.isLASManageLicense){ 
            		// Added to get the Oldest Non Zero PO for the selected Test in case of EDIT Session.
            		oldestNonZeroActivePO = this.scheduleTest.getNonZeroActivePOForSelectedTest(testAdminId);
            		vo.setNonZeroActivePO(oldestNonZeroActivePO);
            		vo.setTestSessionHasStudents(scheduledSession.isTestSessionHasStudents());
            	}
            	
    	    	this.numberSelectedSubtests = scheduledSession.getScheduledUnits().length;
    	    	this.selectedProductType = scheduledSession.getTestSession().getProductType();
    	    	
    	    	//added for copy test session
	    		this.isCopySession = false;
    	    	if(action != null && action.equals("copySession")){
    	    		scheduledSession.setStudentsLoggedIn(new Integer(0));
    	    		originalTestAdminName = scheduledSession.getTestSession().getTestAdminName();
    	    		testAdminNameCopySession = "Copy of " + originalTestAdminName;
    	    		scheduledSession.getTestSession().setTestAdminName(testAdminNameCopySession);
    	    		vo.setCopySession(true);
    	    		this.isCopySession = true;
    	    	}
    	    	//	
    	    	vo.setProductType(TestSessionUtils.getProductType(scheduledSession.getTestSession().getProductType()));
    	    	if(scheduledSession.getTestSession()!=null && scheduledSession.getTestSession().getIsRandomize()!=null){
    	    		if(scheduledSession.getTestSession().getIsRandomize().equalsIgnoreCase("Y")){
    	    			scheduledSession.getTestSession().setIsRandomize("T");
    	    		} else if (scheduledSession.getTestSession().getIsRandomize().equalsIgnoreCase("N")){
    	    			scheduledSession.getTestSession().setIsRandomize("F");
    	    		}
    	    	}
    	    	if(TestSessionUtils.isTabeBatterySurveyProduct(vo.getProductType()) || TestSessionUtils.isTabeAdaptiveProduct(vo.getProductType())){
    	    		vo.setSavedTestDetailsWithDefaultValue(scheduledSession);
    	    	} else {
    	    		vo.setSavedTestDetails(scheduledSession);
    	    	}
    	    	//added for copy test session
    	    	TestElement selectedTest = this.scheduleTest.getTestElementMinInfoByIds(this.getCustomerId(), 
    	    			scheduledSession.getTestSession().getItemSetId(), scheduledSession.getTestSession().getCreatorOrgNodeId());
    	    	Date ovLoginStart = null;
    	    	Date ovLoginEnd = null;
    	    	if(selectedTest!=null){
    	    		ovLoginStart = selectedTest.getOverrideLoginStartDate();
    	    		ovLoginEnd = selectedTest.getOverrideLoginEndDate();
    	    	}
                if (action != null && action.equals("copySession"))
                { 
                	String timeZoneCopySession = scheduledSession.getTestSession().getTimeZone();
                    Date now = new Date(System.currentTimeMillis());
                    Date today = com.ctb.util.DateUtils.getAdjustedDate(now, TimeZone.getDefault().getID(), timeZoneCopySession, now);
                    Date tomorrow = com.ctb.util.DateUtils.getAdjustedDate(new Date(now.getTime() + (24 * 60 * 60 * 1000)), TimeZone.getDefault().getID(), timeZoneCopySession, now);
                    if(this.hasDefaultTestingWindowConfig){
                    	Integer days = getDefaultTestingWindowValue();
                    	if(null != days){
	                		if(days.intValue() != 1){
	                			tomorrow = com.ctb.util.DateUtils.getAdjustedDate(new Date(now.getTime() + ((24*(days.intValue()-1)) * 60 * 60 * 1000)), TimeZone.getDefault().getID(), timeZoneCopySession, now);
	                		}
	                		vo.setTestingWindowDefaultDays(days);
                    	}
                    }
                    if(ovLoginStart != null && !DateUtils.isBeforeToday(ovLoginStart, this.user.getTimeZone())){
                    	Date loginEndDate = (Date) ovLoginStart.clone();
                    	loginEndDate.setDate(loginEndDate.getDate() + 1);
                    	vo.setStartDate(DateUtils.formatDateToDateString(ovLoginStart));
                    	vo.setEndDate(DateUtils.formatDateToDateString(loginEndDate));
                    }else {
                    	vo.setStartDate(DateUtils.formatDateToDateString(today));
                    	if(scheduledSession.getTestSession().getProductId() == 4201){
                    		vo.setEndDate(testletSessionEndDate);
                    	}else{
                    		vo.setEndDate(DateUtils.formatDateToDateString(tomorrow));
                    	}
    	        	}
                    
                    if(! this.hasDefaultTestingWindowConfig){
	                    if(ovLoginEnd!= null && !(DateUtils.isAfterToday(ovLoginEnd, timeZoneCopySession ))) {
	                    	vo.setStartDate(DateUtils.formatDateToDateString(today)); // setting today as start day
	                    	vo.setEndDate(DateUtils.formatDateToDateString(today));    // setting today as end day
	                    	vo.setMinLoginEndDate(DateUtils.formatDateToDateString(ovLoginEnd));
	                    }else if (ovLoginEnd!= null){
	                    	vo.setMinLoginEndDate(DateUtils.formatDateToDateString(ovLoginEnd));
	                    	
	                    }
                    }
                    
                }else{
	    	    	Date now = new Date(System.currentTimeMillis());
	    	    	Date today = com.ctb.util.DateUtils.getAdjustedDate(now, TimeZone.getDefault().getID(), this.user.getTimeZone(), now);
	    	    	if (ovLoginStart != null && !(DateUtils.isBeforeToday(ovLoginStart , this.user.getTimeZone() ))) {
	    	    		vo.setStartDate(DateUtils.formatDateToDateString(ovLoginStart));
		        	} else {
		        		vo.setStartDate(DateUtils.formatDateToDateString(today));
		        	}
		        	
		        	if(ovLoginEnd!= null ) {
		        		vo.setMinLoginEndDate(DateUtils.formatDateToDateString(ovLoginEnd));
		        		
		        	} 
                }//
    	    	vo.populateLocatorInformation();

    	    	if (this.user == null || topNodesMap.size() ==0 ){
    	    		initialize();
    	    	}
    	    	vo.setUserRole(this.user.getRole().getRoleName());
    	    	vo.setUserTimeZone(DateUtils.getUITimeZone(userTimeZone));
    	    	
    	    	//System.out.println("User time zone " + this.user.getTimeZone());
    	    	TestSession testSession = scheduledSession.getTestSession();
    	    	//String schedulerName = testSession.getCreatedBy();
                //User scheduler = this.scheduleTest.getUserDetails(this.userName, schedulerName);
                
                
                if( testSession.getTestAdminStatus().equals("PA")){
                	vo.setTestSessionExpired(Boolean.TRUE);
                } else {
                	vo.setTestSessionExpired(Boolean.FALSE);
                }
    	    	//added for copy test session
    	    	if(action != null && action.equals("copySession")){
    	    		vo.setTestSessionExpired(Boolean.FALSE);
    	    	}
    	    	//
                if(action != null && action.equals("copySession")) {
                	vo.populateTopOrgnode(topNodesMap);
                } else if(topNodesMap.containsKey(scheduledSession.getTestSession().getCreatorOrgNodeId())){
                	 vo.populateTopOrgnode(topNodesMap);
                } else {
                	Map<Integer, String> tmpNodesMap = new LinkedHashMap<Integer, String>();
                	tmpNodesMap.put(scheduledSession.getTestSession().getCreatorOrgNodeId(), scheduledSession.getTestSession().getCreatorOrgNodeName());
                	 vo.populateTopOrgnode(tmpNodesMap);
                }
               
                vo.populateTimeZone();
                
                status.setSuccess(true);
                
                String timeZone = testSession.getTimeZone();
    	    	//added for copy test session
    	    	if(action != null && action.equals("copySession")){
    	    		testSession.setTimeZone(DateUtils.getUITimeZone(this.user.getTimeZone()));
    	    		testSession.setFormAssignmentMethod(TestSession.FormAssignment.ROUND_ROBIN);
    	    		SuccessInfo successInfo = new SuccessInfo();
    	    		successInfo.setMessageHeader(MessageResourceBundle.getMessage("SelectSettings.CopyTest", originalTestAdminName));
    	    		status.setSuccessInfo(successInfo);
    	    	}else{
    	    		testSession.setTimeZone(DateUtils.getUITimeZone(timeZone));
    	    	}
    	    	//
//                testSession.setTimeZone(DateUtils.getUITimeZone(timeZone)); //commented for copy test session
                testSession.setLoginStartDateString(DateUtils.formatDateToDateString(testSession.getLoginStartDate()));
                testSession.setLoginEndDateString(DateUtils.formatDateToDateString(testSession.getLoginEndDate()));
                testSession.setDailyLoginStartTimeString(DateUtils.formatDateToTimeString(testSession.getDailyLoginStartTime()));
                testSession.setDailyLoginEndTimeString(DateUtils.formatDateToTimeString(testSession.getDailyLoginEndTime()));
                
                if(isAdminUser() && this.isOKCustomer && isTopLevelUser()) {
                	vo.setOkAdmin(true);
                } else {
                	vo.setOkAdmin(false);
                }
                
                if(isAdminUser() && this.isWVCustomer && isTopLevelUser()) {
                	vo.setWVAdmin(true);
                } else {
                	vo.setWVAdmin(false);
                }
                
                vo.setForceTestBreak(this.forceTestBreak);
                if ((testSession.getLexingtonVersion() != null) && testSession.getLexingtonVersion().equals("GE-Yes"))               	
                	vo.setSelectGE(Boolean.TRUE);
                else
                if ((testSession.getLexingtonVersion() != null) && testSession.getLexingtonVersion().equals("GE-No"))               	
                	vo.setSelectGE(Boolean.FALSE);
                else
                	vo.setSelectGE(null);
    	    	
                if(this.isTASCCustomer){   	    	 
	       	    	 boolean isSeletedTestInvalid = this.scheduleTest.checkSelectedTestInvalid(this.getCustomerId(),
	       	    			 			scheduledSession.getTestSession().getItemSetId(), scheduledSession.getTestSession().getCreatorOrgNodeId());
	       	    	 if(isSeletedTestInvalid){
	       	    		 status.setSuccess(false);
	           	    	 status.setSystemError(true);
	           	    	 ValidationFailedInfo validationFailedInfo = new ValidationFailedInfo();
	           	    	 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("SelectSettings.TestForm.Disabled"));
	           			 status.setValidationFailedInfo(validationFailedInfo); 
	           	     }		
                }
                
    	    } catch(CTBBusinessException e){
    	    	 e.printStackTrace(); 
    	    	 status.setSystemError(true);
    	    	 ValidationFailedInfo validationFailedInfo = new ValidationFailedInfo();
    	    	 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("SelectSettings.FailedToLoadTestSession"));
    	    	 if(e.getMessage()!=null && e.getMessage().length()>0){
    	    		 validationFailedInfo.updateMessage(e.getMessage()); 
    	    	 }
    			 status.setValidationFailedInfo(validationFailedInfo);
    	    	
    	    } catch(Exception e) {
    	    	e.printStackTrace(); 
    	    	status.setSystemError(true);
    	    	 ValidationFailedInfo validationFailedInfo = new ValidationFailedInfo();
    	    	 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("System.Exception.Header"));
    			 validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("System.Exception.Body"));
    			 status.setValidationFailedInfo(validationFailedInfo);
    	    }
    		
			Gson gson = new Gson();
	
			jsonData = gson.toJson(vo);
			//System.out.println(jsonData);
			try {
				resp.setContentType(CONTENT_TYPE_JSON);
				stream = resp.getOutputStream();
				stream.write(jsonData.getBytes("UTF-8"));
				resp.flushBuffer();
			} catch (IOException e) {
				e.printStackTrace();
			}
	
			return null;
	   }
    	
    	@Jpf.Action()
        protected Forward getScheduledStudents(SessionOperationForm form) {
    		
    		String jsonData = "";
    		OutputStream stream = null;
    		HttpServletResponse resp = getResponse();
    	    resp.setCharacterEncoding("UTF-8"); 
    	    String testAdminIdString = RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.TEST_ADMIN_ID, false, null);
    	    ScheduledSavedTestVo vo = new ScheduledSavedTestVo();
    	    Map<Integer,Map> accomodationMap = new HashMap<Integer, Map>();
    	    Map<String,ArrayList> classHierarchy = new HashMap<String,ArrayList>();
    	    OperationStatus status = new OperationStatus();
    	    vo.setOperationStatus(status) ;
    	    //added for copy test session
    	    String action = getRequest().getParameter("action"); 
//    	    System.out.println(">>>>>"+action);
    	    //
    	    try {
    	    	Integer testAdminId = Integer.valueOf(testAdminIdString);
    	    	ScheduledSession scheduledSession = this.scheduleTest.getScheduledStudentsMinimalInfoDetails(this.userName, testAdminId);
    	    	SessionStudent[] students =  scheduledSession.getStudents();
    	    	List<SessionStudent> studentsList = null;
    	    	if(action != null && action.equals("copySession")){
           	    	if (this.isTABECustomer || this.isTABEAdaptiveCustomer || this.isTASCCustomer || this.isTASCReadinessCustomer) {
        	    		studentsList = new ArrayList(); 
        	    	}
        	    	else {
        	    		studentsList = buildStudentListForCopySession(students, accomodationMap, action);
        	    	}
    	    	}else{
    	    		studentsList = buildStudentList(students, accomodationMap);
    	    	}
    	    	vo.setSavedStudentsDetails(studentsList);
    	    	if(this.hasShowRosterAccomAndHierarchy){
    	    		classHierarchy = this.scheduleTest.getScheduledStudentsClassHierarchy(this.userName);
    	    		vo.setHasShowRosterAccomAndHierarchy(this.hasShowRosterAccomAndHierarchy);
    	    	}
    	    	vo.setClassHierarchyMap(classHierarchy);
                status.setSuccess(true);
               
                if(isProctorUser() && this.isWVCustomer) {
                	vo.setWVProctor(true);
                } else {
                	vo.setWVProctor(false);
                }
    	    	
    	    } catch(CTBBusinessException e){
    	    	 e.printStackTrace(); 
    	    	 status.setSystemError(true);
    	    	 ValidationFailedInfo validationFailedInfo = new ValidationFailedInfo();
    	    	 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("SelectSettings.FailedToLoadTestSession"));
    	    	 if(e.getMessage()!=null && e.getMessage().length()>0){
    	    		 validationFailedInfo.updateMessage(e.getMessage()); 
    	    	 }
    			 status.setValidationFailedInfo(validationFailedInfo);
    	    	
    	    } catch(Exception e) {
    	    	e.printStackTrace(); 
    	    	status.setSystemError(true);
    	    	 ValidationFailedInfo validationFailedInfo = new ValidationFailedInfo();
    	    	 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("System.Exception.Header"));
    			 validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("System.Exception.Body"));
    			 status.setValidationFailedInfo(validationFailedInfo);
    	    }
    	    vo.setAccomodationMap(accomodationMap);
			Gson gson = new Gson();
			jsonData = gson.toJson(vo);
			//System.out.println(jsonData);
			try {
				resp.setContentType(CONTENT_TYPE_JSON);
				stream = resp.getOutputStream();
				stream.write(jsonData.getBytes("UTF-8"));
				resp.flushBuffer();
			} catch (IOException e) {
				e.printStackTrace();
			}
	
			return null;
	  }



    	@Jpf.Action()
        protected Forward getScheduledStudentsWithTest(SessionOperationForm form) {
    		
    		String jsonData = "";
    		OutputStream stream = null;
    		HttpServletResponse resp = getResponse();
    	    resp.setCharacterEncoding("UTF-8"); 
    	    String testAdminIdString = RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.TEST_ADMIN_ID, false, null);
    	    ScheduledSavedStudentDetailsVo vo = new ScheduledSavedStudentDetailsVo();
    	    Map<Integer,Map> accomodationMap = new HashMap<Integer, Map>();
    	    OperationStatus status = new OperationStatus();
    	    vo.setOperationStatus(status) ;
    	    try {
    	    	TestElement[] locatorSubtestTds = null;
    	    	Integer testAdminId = Integer.valueOf(testAdminIdString);
    	    	ScheduledSession scheduledSession = this.scheduleTest.getScheduledStudentsMinimalInfoDetails(this.userName, testAdminId);
    	    	TestElement[] testSession = this.itemSet.getTestElementByTestAdmin(testAdminId);
    	    	for(int indx=0; indx<testSession.length;indx++){
    	    		if(testSession[indx].getItemSetName().indexOf("Locator") > 0){
    	    			locatorSubtestTds = this.itemSet.getTestElementsForParent(testSession[indx].getItemSetId(), "TD");
    	    			
    	    		}
    	    	}
    	    	SessionStudent[] students =  scheduledSession.getStudents();
    	    	List<SessionStudent> studentsList = buildStudentList(students, accomodationMap);
    	    	vo.setSavedStudentsDetails(studentsList);
    	    	vo.populateTestSession(testSession, locatorSubtestTds);
    	    	vo.populateLevelOptions();
                status.setSuccess(true);
    	    	
    	    } catch(CTBBusinessException e){
    	    	 e.printStackTrace(); 
    	    	 status.setSystemError(true);
    	    	 ValidationFailedInfo validationFailedInfo = new ValidationFailedInfo();
    	    	 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("SelectSettings.FailedToLoadTestSession"));
    	    	 if(e.getMessage()!=null && e.getMessage().length()>0){
    	    		 validationFailedInfo.updateMessage(e.getMessage()); 
    	    	 }
    			 status.setValidationFailedInfo(validationFailedInfo);
    	    	
    	    } catch(Exception e) {
    	    	e.printStackTrace(); 
    	    	status.setSystemError(true);
    	    	 ValidationFailedInfo validationFailedInfo = new ValidationFailedInfo();
    	    	 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("System.Exception.Header"));
    			 validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("System.Exception.Body"));
    			 status.setValidationFailedInfo(validationFailedInfo);
    	    }
    	    vo.setAccomodationMap(accomodationMap);
			Gson gson = new Gson();
			jsonData = gson.toJson(vo);
			//System.out.println(jsonData);
			try {
				resp.setContentType(CONTENT_TYPE_JSON);
				stream = resp.getOutputStream();
				stream.write(jsonData.getBytes("UTF-8"));
				resp.flushBuffer();
			} catch (IOException e) {
				e.printStackTrace();
			}
	
			return null;
	  }
    	
    @Jpf.Action()
    protected Forward getScheduleProctor(SessionOperationForm form) {
		
		String jsonData = "";
		OutputStream stream = null;
		HttpServletResponse resp = getResponse();
	    resp.setCharacterEncoding("UTF-8"); 
	    String testAdminIdString = RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.TEST_ADMIN_ID, false, null);
	    ScheduledSavedTestVo vo = new ScheduledSavedTestVo();
	    OperationStatus status = new OperationStatus(); 
	    vo.setOperationStatus(status) ;
	    //added for copy test session
	    String action = getRequest().getParameter("action");
	    String okQEAdmin = getRequest().getParameter("okQEAdmin");
//	    System.out.println(">>>>>"+action);
	    //
	    try {
	    	Integer testAdminId = Integer.valueOf(testAdminIdString);
	    	ScheduledSession scheduledSession = this.scheduleTest.getScheduledProctorsMinimalInfoDetails(this.userName, testAdminId);
	    	List<UserProfileInformation> proctors = null;
	    	if(action != null && action.equals("copySession")){
	    		proctors = buildProctorListForCopySession(this.user,scheduledSession.getProctors());
	    	}else{
	    		proctors = buildProctorList(scheduledSession.getProctors());
	    	}
	    	 vo.setSavedProctorsDetails(proctors);
            status.setSuccess(true);
           
            
	    	
	    } catch(CTBBusinessException e){
	    	 e.printStackTrace(); 
	    	 status.setSystemError(true);
	    	 ValidationFailedInfo validationFailedInfo = new ValidationFailedInfo();
	    	 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("SelectSettings.FailedToLoadTestSession"));
	    	 if(e.getMessage()!=null && e.getMessage().length()>0){
	    		 validationFailedInfo.updateMessage(e.getMessage()); 
	    	 }
			 status.setValidationFailedInfo(validationFailedInfo);
	    	
	    } catch(Exception e) {
	    	e.printStackTrace(); 
	    	status.setSystemError(true);
	    	 ValidationFailedInfo validationFailedInfo = new ValidationFailedInfo();
	    	 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("System.Exception.Header"));
			 validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("System.Exception.Body"));
			 status.setValidationFailedInfo(validationFailedInfo);
	    }
		
		Gson gson = new Gson();
		jsonData = gson.toJson(vo);
		//System.out.println(jsonData);
		try {
			resp.setContentType(CONTENT_TYPE_JSON);
			stream = resp.getOutputStream();
			stream.write(jsonData.getBytes("UTF-8"));
			resp.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
  }


    @Jpf.Action()
    protected Forward getScheduleStudentsManifestDetails(SessionOperationForm form) {
		
		String jsonData = "";
		OutputStream stream = null;
		HttpServletResponse resp = getResponse();
	    resp.setCharacterEncoding("UTF-8"); 
	    Map<Integer, String> allRecomendedLevel =  new HashMap<Integer, String>();
	    String locatorSessionInfo = "";
	    String testAdminIdString = RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.TEST_ADMIN_ID, false, null);
	    String studentIdString = RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.STUDENT_ID, false, null);
	    
	    ScheduledSavedStudentDetailsVo vo = new ScheduledSavedStudentDetailsVo();
	    //Map<Integer,Map> accomodationMap = new HashMap<Integer, Map>();
	    OperationStatus status = new OperationStatus();
	    vo.setOperationStatus(status) ;
	    try {
	    	Integer testAdminId = Integer.valueOf(testAdminIdString);
	    	Integer studentId = Integer.valueOf(studentIdString);

	    	ScheduledStudentDetailsWithManifest studentDetailsWithManifest =  this.scheduleTest.getScheduledStudentsManifestDetails(this.userName, studentId, testAdminId);
	    	TestElement locatorSubtest = TestSessionUtils.getLocatorSubtest(studentDetailsWithManifest.getAllSchedulableUnit());
	    	TestSession session = studentDetailsWithManifest.getTestSession();
	    	TestElement[] allSubTests = studentDetailsWithManifest.getAllSchedulableUnit();
	    	TABERecommendedLevel[] trls = null;
	    	String productType = TestSessionUtils.getProductType(session.getProductType());
	    	if(locatorSubtest!=null && !TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue()){
	    		trls = scheduleTest.getTABERecommendedLevelForStudent(userName, studentId, session.getItemSetId(), locatorSubtest.getItemSetId());
	    		TestElement[] testAdminItemSet =  studentDetailsWithManifest.getTestAdminItemSet();
	    		for(int ii =0; ii<testAdminItemSet.length; ii++){//copySubtestLevel
	    			allRecomendedLevel.put(testAdminItemSet[ii].getItemSetId(), testAdminItemSet[ii].getItemSetForm());
	    		}
	    		TestSessionUtils.setRecommendedLevelForSession(allSubTests, trls);//setRecommendedLevelForSession
	    		for(int ii =0; ii<allSubTests.length; ii++){
	    			if(allSubTests[ii].getItemSetForm()!=null) {
	    				allRecomendedLevel.put(allSubTests[ii].getItemSetId(), allSubTests[ii].getItemSetForm());
	    			}
	    		}
	    		
	    		//copySubtestLevelIfNull
	    		 TestSessionUtils.copySubtestLevelIfNull(allRecomendedLevel, studentDetailsWithManifest.getStudentManifests());

	    		locatorSessionInfo = TestSessionUtils.getLocatorSessionInfo(studentDetailsWithManifest.getStudentManifests(), trls);
	    		if(locatorSessionInfo!=null && locatorSessionInfo.trim().length()>0){
	    			vo.setLocatorSessionInfo(locatorSessionInfo);
	    		}
	    	}
	    	
	    	
	    	vo.populateManifests(studentDetailsWithManifest.getStudentManifests());
	    	vo.setRecomendedLevelMap(allRecomendedLevel);
            status.setSuccess(true);
	    	
	    } catch(CTBBusinessException e){
	    	 e.printStackTrace(); 
	    	 status.setSystemError(true);
	    	 ValidationFailedInfo validationFailedInfo = new ValidationFailedInfo();
	    	 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("SelectSettings.FailedToLoadTestSession"));
	    	 if(e.getMessage()!=null && e.getMessage().length()>0){
	    		 validationFailedInfo.updateMessage(e.getMessage()); 
	    	 }
			 status.setValidationFailedInfo(validationFailedInfo);
	    	
	    } catch(Exception e) {
	    	e.printStackTrace(); 
	    	status.setSystemError(true);
	    	 ValidationFailedInfo validationFailedInfo = new ValidationFailedInfo();
	    	 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("System.Exception.Header"));
			 validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("System.Exception.Body"));
			 status.setValidationFailedInfo(validationFailedInfo);
	    }
	    //vo.setAccomodationMap(accomodationMap);
		Gson gson = new Gson();
		jsonData = gson.toJson(vo);
		//System.out.println(jsonData);
		try {
			resp.setContentType(CONTENT_TYPE_JSON);
			stream = resp.getOutputStream();
			stream.write(jsonData.getBytes("UTF-8"));
			resp.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
  }
    
    
    //Added for Oklahoma Customer for adding non state admin users
    @Jpf.Action()
    protected Forward getAllBelowLevelUsers(SessionOperationForm form)
    {
    	String jsonData = "";
    	HttpServletResponse resp = getResponse();
    	resp.setCharacterEncoding("UTF-8"); 
    	OutputStream stream = null;
        String stateOKUserId = this.getRequest().getParameter("userId");
        
        try
        {
            Integer schedulerUserId = Integer.parseInt(stateOKUserId);
            
        	try {
        		User[] proctorList = userManagement.belowLevelUserList(schedulerUserId);
    			resp.setContentType(CONTENT_TYPE_JSON);
    			//resp.flushBuffer();
    			Gson gson = new Gson();
            	jsonData = gson.toJson(proctorList);
    			stream = resp.getOutputStream();
    			stream.write(jsonData.getBytes("UTF-8"));
    			resp.flushBuffer();
    		} catch (CTBBusinessException e) {
    			
    			e.printStackTrace();
    		} 
    	
        } catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (stream!=null){
				try {
					stream.close();
				} catch (Exception e) {
				}
			}
		}
    	return null;
    }
    
  //Added for Oklahoma Customer for adding non state admin users
    @Jpf.Action()
    protected Forward checkSameLevelNonProctor(SessionOperationForm form)
    {
    	String jsonData = "";
    	HttpServletResponse resp = getResponse();
    	resp.setCharacterEncoding("UTF-8"); 
    	OutputStream stream = null;
        Integer loggedInUserId = this.user.getUserId();
        String selectedTestAdminId = this.getRequest().getParameter("selectedTestAdminId");
        
        try
        {
            Integer testAdminId = Integer.parseInt(selectedTestAdminId);
            users.checkTopLevelOkAdmin(loggedInUserId, testAdminId);
            
        	try {
        		Integer proctorPermitted = users.checkTopLevelOkAdmin(loggedInUserId, testAdminId);
    			resp.setContentType(CONTENT_TYPE_JSON);
    			//resp.flushBuffer();
    			Gson gson = new Gson();
            	jsonData = gson.toJson(proctorPermitted);
    			stream = resp.getOutputStream();
    			stream.write(jsonData.getBytes("UTF-8"));
    			resp.flushBuffer();
    		} catch (SQLException e) {
    			
    			e.printStackTrace();
    		} 
    	
        } catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (stream!=null){
				try {
					stream.close();
				} catch (Exception e) {
				}
			}
		}
    	return null;
    }
    
    
    
    private void removeRestrictedStudentsFromTest(ScheduledSession session) throws CTBBusinessException {
    	SessionStudent[] students = updateRestrictedStudentsForTest(session.getStudents(),session.getTestSession().getItemSetId(), session.getTestSession().getTestAdminId()  );
    	session.setStudents(students);
		
	}	
    private SessionStudentData getRestrictedStudentsForTest(ScheduledSession session) throws CTBBusinessException{
    	 SortParams sort = FilterSortPageUtils.buildSortParams("LastName",ColumnSortEntry.ASCENDING);
    	SessionStudentData restrictedSSD = this.scheduleTest.getRestrictedStudentsForTest(userName, session.getStudents(),  session.getTestSession().getItemSetId(), session.getTestSession().getTestAdminId(), null, null, sort);
		return restrictedSSD;
	}
    private Integer saveOrUpdateTestSession(ScheduledSession session) throws CTBBusinessException {
    	Integer  newTestAdminId =null;
    	if(session.getTestSession().getTestAdminId()!=null){
			 newTestAdminId = this.scheduleTest.updateTestSession(this.userName, session);  
		 } else {
			 newTestAdminId = this.scheduleTest.createNewTestSession(this.userName, session);  
		 }
		return newTestAdminId;
	}
    
  //------------------------------  
    private boolean checkLicenseForCustomer(Integer customerId,Integer studentCount,String orgNodeIDs,ScheduledSession session) throws CTBBusinessException {
    	
    	boolean hasLicenseFeature = false ;
    	LASLicenseNode licenseInfo = null;
    	Integer licenceCount=0;
    	customerConfigurations = getCustomerConfigurations(customerId);    	
    	 for (int i=0; i < this.customerConfigurations.length; i++) {
        	CustomerConfiguration cc = (CustomerConfiguration)this.customerConfigurations[i];
            if (cc.getCustomerConfigurationName().equalsIgnoreCase("Allow_Subscription") &&	cc.getDefaultValue().equals("T") ) {
            	hasLicenseFeature  = true;
                break;
            } 
	     }    	 
    	 if (hasLicenseFeature) {    		 
    		 licenseInfo = this.scheduleTest.getLicenseInformation( customerId,orgNodeIDs); 
    		 
    		 if (licenseInfo  == null && studentCount > 0){
    			 return true;
    		 }
    		 else if (licenseInfo != null && studentCount > 0){//start 
    			 licenceCount=Integer.parseInt(licenseInfo.getLicenseQuantity());
    			 if(licenseInfo.getSubtestModel().equalsIgnoreCase("F")){
    				 if(licenceCount>=studentCount){
    					 return false;
    				 }
    				 else{
    					 return true;
    				 }
    			 }
    			 else{
    				 int subtestCount = getSubtestCount(session);
    				 if(licenceCount>=(studentCount*subtestCount)){
    					 return false;
    				 }
    				 else{
    					 return true;
    				 }
    			 }
    		 }		//end
    		 else {
    			 return false ;
    		 }
    	 }
    	
    	
		return false;
	}
    //Added for getting subtest count if the customer has license configured as Subtest Model 7/11/2013
    private int getSubtestCount(ScheduledSession session) {
		// TODO Auto-generated method stub

   	 	ArrayList<TestElement> subtests =new ArrayList<TestElement>();
		ArrayList<TestElement> filterSubtest = new ArrayList<TestElement>();
	    HashMap<Integer,String> locatorSubtestTD = null;
        TestElement [] scheduledSubtests = session.getScheduledUnits();
        int subtestCount =0; 
        for(int ii=0;ii<scheduledSubtests.length;ii++) {
            subtests.add(scheduledSubtests[ii]);
        }
	     if (session.getLocatorSubtestTD() != null)
	     	locatorSubtestTD = new HashMap<Integer, String>(session.getLocatorSubtestTD());
	     if(locatorSubtestTD !=null && locatorSubtestTD.size() > 0){
	     	Iterator iterator = locatorSubtestTD.keySet().iterator();
		 	for(TestElement subtestlist: subtests){
	    			if(!subtestlist.getIslocatorChecked().equals("")){
	    				filterSubtest.add(subtestlist);
	    			}	
		 		}
	     }
	     else{
	     	filterSubtest = subtests; 
	     }
	     subtestCount = filterSubtest.size();
	     System.out.println("subtest count>>>"+subtestCount);
	     return subtestCount;
    }
    //--------------------------------------
    private ScheduledSession populateAndValidateSessionRecord(HttpServletRequest httpServletRequest, ValidationFailedInfo validationFailedInfo, boolean isAddOperation, String action) throws CTBBusinessException
    	    {  
    		 //Integer newTestAdminId = null;
    		 ScheduledSession scheduledSession = new ScheduledSession();
    		 ScheduledSession savedSessionMinData = new ScheduledSession();
    		 List<String> accessCodeListForCopy = new ArrayList<String>(); //added for copy test session
    		 if(action != null && action.equalsIgnoreCase("COPY")){
    			 accessCodeListForCopy = populateTestSessionForCopySession(scheduledSession,savedSessionMinData, httpServletRequest, validationFailedInfo , isAddOperation, accessCodeListForCopy);
    		 }else{
    			 populateTestSession(scheduledSession,savedSessionMinData, httpServletRequest, validationFailedInfo , isAddOperation);
    		 }
    		 if(!validationFailedInfo.isValidationFailed()) {
    			 if(action != null && action.equalsIgnoreCase("COPY")){
    				 populateScheduledUnitsForCopySession(scheduledSession, savedSessionMinData, httpServletRequest, validationFailedInfo, isAddOperation, accessCodeListForCopy);
    			 }else{
    				 populateScheduledUnits(scheduledSession, savedSessionMinData, httpServletRequest, validationFailedInfo, isAddOperation );
    			 }
    		 }
    		 if(!validationFailedInfo.isValidationFailed()) {
    			 
    			 if(action != null && action.equalsIgnoreCase("COPY")){
    				 populateSessionStudentForCopySession(scheduledSession, savedSessionMinData, httpServletRequest, validationFailedInfo, isAddOperation );
    			 }else{
    				 populateSessionStudent(scheduledSession, savedSessionMinData, httpServletRequest, validationFailedInfo, isAddOperation );
    			 }
    			 
    		 }
    		 
    		 if(!validationFailedInfo.isValidationFailed()) {
    			 
    			 if(action != null && action.equalsIgnoreCase("COPY")){
    				 populateProctorForCopySession(scheduledSession, httpServletRequest , validationFailedInfo, isAddOperation);
    			 } else {
    				 populateProctor(scheduledSession, httpServletRequest , validationFailedInfo, isAddOperation);
    			 }
    			 
    		 }
    		 
    		/* if(!validationFailedInfo.isValidationFailed()) {
    			 if(scheduledSession.getTestSession().getTestAdminId()!=null){
    				 newTestAdminId = this.scheduleTest.updateTestSession(this.userName, scheduledSession);  
    			 } else {
    				 newTestAdminId = this.scheduleTest.createNewTestSession(this.userName, scheduledSession);  
    			 }
    			 
    		 } */   		 
    	        return scheduledSession;
    }
    
     private void populateScheduledUnits(ScheduledSession scheduledSession, ScheduledSession savedSessionMinData,
				HttpServletRequest request, ValidationFailedInfo validationFailedInfo, boolean isAddOperation) {
    	/* List subtestList = null;*/
	     //boolean sessionHasLocator = false;
    	 try{
    		 String productType				= RequestUtil.getValueFromRequest(request, RequestUtil.PRODUCT_TYPE, true, "");
        	 Integer itemSetId        		= Integer.valueOf(RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_ITEM_SET_ID, false, null));
        	 String hasBreakValue     		= RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_HAS_BREAK, false, null);
        	 String hasBreak          		= (hasBreakValue == null || !(hasBreakValue.trim().equals("T") || hasBreakValue.trim().equals("F"))) ? "F" :  hasBreakValue.trim();
        	 boolean hasBreakBoolean        = (hasBreak.equals("T")) ? true : false;
        	 String[] itemSetIdTDs          = RequestUtil.getValuesFromRequest(request, RequestUtil.TEST_ITEM_SET_ID_TD, true ,  new String [0]);
        	// String[] itemSetIdTDName       = RequestUtil.getValuesFromRequest(request, "itemSetIdTDName", true ,  new String [0]);
        	 String[] locatorTDsForTABE		= RequestUtil.getValuesFromRequest(request, "locatorItemTD", true ,  new String [0]);
        	 String[] accesscodes           = RequestUtil.getValuesFromRequest(request, RequestUtil.TEST_ITEM_IND_ACCESS_CODE, true ,  new String [itemSetIdTDs.length]);
        	 String[] itemSetForms          = RequestUtil.getValuesFromRequest(request, RequestUtil.TEST_ITEM_SET_FORM, true ,  new String [itemSetIdTDs.length]);
        	 String[] itemSetisDefault      = RequestUtil.getValuesFromRequest(request, RequestUtil.TEST_ITEM_IS_SESSION_DEFAULT, true ,  new String [itemSetIdTDs.length]);
        	 String autoLocator				= RequestUtil.getValueFromRequest(request, RequestUtil.HAS_AUTOLOCATOR, true, "false");
        	 String[] islocatorChecked           = RequestUtil.getValuesFromRequest(request, RequestUtil.LOCATOR_CHECKBOX, true ,  new String [itemSetIdTDs.length]);
        	 
        	 //List<SubtestVO>  subtestList   = idToTestMap.get(itemSetId).getSubtests();
        	 List<SubtestVO> subtestTDList = new ArrayList<SubtestVO>();
        	 List<String> locatorTDList = new ArrayList<String>();
        	 Map<Integer,String> locatorItemSetTDMap = new HashMap<Integer,String>();
        	 for(int indx =0; indx<locatorTDsForTABE.length; indx++){
        		 String[] strArr = locatorTDsForTABE[indx].split("~");
        		 Integer TDid = Integer.valueOf(strArr[0].trim());
        		 String testName = strArr[1].trim();
     			 locatorItemSetTDMap.put(TDid, testName);
        	 }
        	 List<SubtestVO>  subtestList   = new ArrayList<SubtestVO>();
        	 for(int ii =0, jj =itemSetIdTDs.length; ii<jj; ii++ ){
        		 SubtestVO subtest = new SubtestVO();
        		 subtest.setId(Integer.valueOf(itemSetIdTDs[ii].trim()));
        		 subtest.setTestAccessCode(accesscodes[ii]);
        		 subtest.setSessionDefault(itemSetisDefault[ii]);
        		 subtest.setIslocatorChecked(islocatorChecked[ii]);
        		 if(itemSetForms[ii] != null && itemSetForms[ii].trim().length()>0){
        			 subtest.setLevel(itemSetForms[ii]);
        		 }
        		 subtestList.add(subtest);
        		 
        	 }
        	                     
    	        if (productType!=null && TestSessionUtils.isTabeProduct(productType).booleanValue())
    	        {

    	        	  if (TestSessionUtils.isTabeBatterySurveyProduct(productType).booleanValue() || TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue())
    	            {

    	        		  if ((autoLocator != null) && autoLocator.equals("true"))
      	                {   
    	        			  Integer lItemSetId   = Integer.valueOf(RequestUtil.getValueFromRequest(request, RequestUtil.LOCATOR_TEST_ITEM_SET_ID_TD, false, null));
    	        			  String lAccesscodes  = RequestUtil.getValueFromRequest(request, RequestUtil.LOCATOR_TEST_ITEM_IND_ACCESS_CODE, true, "");
    	        			  String lItemSetisDefault  = RequestUtil.getValueFromRequest(request, RequestUtil.LOCATOR_TEST_ITEM_IS_SESSION_DEFAULT, false, null);
    	        			  String lItemSetForms      = RequestUtil.getValueFromRequest(request, RequestUtil.LOCATOR_TEST_ITEM_SET_FORM, false, null);
    	        			  SubtestVO locatorSubtest = new SubtestVO();
    	        			  locatorSubtest.setId(lItemSetId);
    	        			  locatorSubtest.setTestAccessCode(lAccesscodes);
    	        			  locatorSubtest.setSessionDefault(lItemSetisDefault);
    	        			  if(lItemSetForms!=null && lItemSetForms.length() >0 ){
    	        				  locatorSubtest.setLevel(lItemSetForms);
    	        			  }
    	        			  subtestList.add(0, locatorSubtest);
    	        			  scheduledSession.setHasLocator(true);
    	        			  scheduledSession.setLocatorSubtestTD(locatorItemSetTDMap);
      	                } else {
      	                	 TestSessionUtils.setDefaultLevels(subtestList, "E");
      	                	
      	                }
    	            } 
    	            else
    	            {
    	                // tabe locator test
    	            	  subtestTDList = subtestList;
    	            	  subtestList.clear();
		            	  Integer lItemSetId   = Integer.valueOf(RequestUtil.getValueFromRequest(request, RequestUtil.LOCATOR_TEST_ITEM_SET_ID_TD, false, null));
	        			  String lAccesscodes  = RequestUtil.getValueFromRequest(request, RequestUtil.LOCATOR_TEST_ITEM_IND_ACCESS_CODE, true, "");
	        			  String lItemSetisDefault  = RequestUtil.getValueFromRequest(request, RequestUtil.LOCATOR_TEST_ITEM_IS_SESSION_DEFAULT, false, null);
	        			  String lItemSetForms      = RequestUtil.getValueFromRequest(request, RequestUtil.LOCATOR_TEST_ITEM_SET_FORM, false, null);
	        			  SubtestVO locatorSubtest = new SubtestVO();
	        			  locatorSubtest.setId(lItemSetId);
	        			  locatorSubtest.setTestAccessCode(lAccesscodes);
	        			  locatorSubtest.setSessionDefault(lItemSetisDefault);
	        			  if(lItemSetForms!=null && lItemSetForms.length() >0 ){
	        				  locatorSubtest.setLevel(lItemSetForms);
	        			  }
	        			  subtestList.add(0, locatorSubtest);
	        			  scheduledSession.setHasLocator(true);
	        			  scheduledSession.setLocatorSubtestTD(locatorItemSetTDMap);
    	            	  TestSessionUtils.setDefaultLevels(subtestList, "1");
    	            }
    	            
    	        }
    	        else
    	        {
    	            // for non-tabe test
    	            subtestList = TestSessionUtils.cloneSubtests(subtestList);
    	        }
    	        
    	        
    	        TestElement [] newTEs = new TestElement[subtestList.size()];
    	        
    	        for (int i=0; i < subtestList.size(); i++)
    	        {
    	            SubtestVO subVO= (SubtestVO)subtestList.get(i);
    	            TestElement te = new TestElement();
    	        
    	            te.setItemSetId(subVO.getId());
    	            
    	            if (TestSessionUtils.isTabeProduct(productType).booleanValue()  || TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue())
    	            {                
    	                String level = subVO.getLevel();
    	                te.setItemSetForm(level);
    	            }
    	            
    	            if (!hasBreakBoolean ) {
    	            	//String accessCode = RequestUtil.getValueFromRequest(request, RequestUtil.ACCESS_CODE, true, "");
    	            	String accessCode = scheduledSession.getTestSession().getAccessCode();
    	            	te.setAccessCode(accessCode);
    	            } else {
    	            	//String accessCode = RequestUtil.getValueFromRequest(request, RequestUtil.ACCESS_CODEB+i, true, "");
    	            	//te.setAccessCode(accessCode);
    	            	te.setAccessCode(subVO.getTestAccessCode());
    	            }
    	               
    	            
    	            te.setSessionDefault(subVO.getSessionDefault());
    	            te.setIslocatorChecked(subVO.getIslocatorChecked());
    	            newTEs[i] = te;
    	        }
    	        
    	        scheduledSession.setScheduledUnits(newTEs);
    	        validateScheduledUnits(scheduledSession, hasBreakBoolean, validationFailedInfo, isAddOperation);
    	        if(TestSessionUtils.isTabeProduct(productType).booleanValue()  || TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue()) {
    	        	//TABESubtestValidation.validation(A, validateLevels, TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue());
    	        	
    	        }
    	        
    	        if(scheduledSession.getTestSession().getTestAdminId()!=null && scheduledSession.getTestSession().getTestAdminId()!=-1){
    	        	if(TestSessionUtils.isTabeProduct(productType).booleanValue()  || TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue()) {
        	        	if(savedSessionMinData.getTestSession().getItemSetId().intValue() == scheduledSession.getTestSession().getItemSetId().intValue()){
    	        			TestElement[] te = TestSessionUtils.setupSessionSubtests( savedSessionMinData.getScheduledUnits(), scheduledSession.getScheduledUnits());
    	        			scheduledSession.setScheduledUnits(te);
    	        			// added code for defect #80372
    	        			if(TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue()){
    	        				TestElement[] testElementList = new TestElement[te.length];
    	        				for(int i=0;i<te.length;i++){
    	        					TestElement testElement = te[i];
    	        					if(testElement.getItemSetForm()!=null && testElement.getItemSetForm().equals("E"))
    	        						testElement.setItemSetForm(null);
    	        					testElementList[i] = testElement;
    	        				}
    	        				scheduledSession.setScheduledUnits(testElementList);
    	        			}
    	        		}
    	        		
        	        }
    	        }
    	        
            
    	       
    	 } catch (Exception e) {
    		 e.printStackTrace();
    		 validationFailedInfo.setKey("SYSTEM_EXCEPTION");
			 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("System.Exception.Header"));
			 validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("System.Exception.Body"));
    	 }
    	 
	        
	        
	        
	        
	        
			
		}

	private void populateProctor(ScheduledSession scheduledSession,
			HttpServletRequest request,
			ValidationFailedInfo validationFailedInfo, boolean isAddOperation) {
		

		try {
			boolean isProcListUpdated = true;
			if(!isAddOperation){
				String isStudentUpdated = RequestUtil.getValueFromRequest(request, RequestUtil.IS_PROCTOR_LIST_UPDATED, true, "true");
				if(isStudentUpdated.equalsIgnoreCase("false"))
					isProcListUpdated = false;
			}
			
			if(isAddOperation || isProcListUpdated) {
				String proctorsData = RequestUtil.getValueFromRequest(request, RequestUtil.PROCTORS, true, "");
				int proctorCount = 0;
				if (proctorsData != null
						&& proctorsData.trim().length() > 1) {
					proctorCount = proctorsData.split(",").length;
				}
				if (proctorCount > 0) {
					ArrayList<User> proctorList = new ArrayList<User>(proctorCount);
					String[] procs = proctorsData.split(",");
					for (String procrec : procs) {
						StringTokenizer st = new StringTokenizer(procrec, ":");
						User us = new User();
						while (st.hasMoreTokens()) {
							StringTokenizer keyVal = new StringTokenizer(st
									.nextToken(), "=");
	
							String key = keyVal.nextToken();
							String val = null;
							if (keyVal.countTokens() > 0) {
								val = keyVal.nextToken();
							}
	
							if (key.equalsIgnoreCase("userId")) {
								us.setUserId(Integer.valueOf(val));
							} else if (key.equalsIgnoreCase("userName")) {
								us.setUserName(val);
							} else if (key.equalsIgnoreCase("copyable")) {
								us.setCopyable(val);
							} 
						}
	
						proctorList.add(us);
					}
	
					scheduledSession.setProctors(proctorList.toArray(new User[proctorList.size()]));
				} else {
					User[] proctorArray = new User[1];
					proctorArray[0]= this.user;
					scheduledSession.setProctors(proctorArray);
				}
			} else {
				ScheduledSession schSession = this.scheduleTest.getScheduledProctorsMinimalInfoDetails(this.userName, scheduledSession.getTestSession().getTestAdminId());
				scheduledSession.setProctors(schSession.getProctors());
			}
		} catch (Exception e) {
			e.printStackTrace();
			validationFailedInfo.setKey("SYSTEM_EXCEPTION");
			validationFailedInfo.setMessageHeader(MessageResourceBundle
					.getMessage("System.Exception.Header"));
			validationFailedInfo.updateMessage(MessageResourceBundle
					.getMessage("System.Exception.Body"));
		}

	}

	private void populateSessionStudent(ScheduledSession scheduledSession,ScheduledSession savedSessionMinData,
				HttpServletRequest httpServletRequest,
			ValidationFailedInfo validationFailedInfo, boolean isAddOperation) {
		
		try {
			
			boolean isStudentListUpdated = true;
			boolean isStudentManifestsExists = false;
			 String productType				= RequestUtil.getValueFromRequest(httpServletRequest, RequestUtil.PRODUCT_TYPE, true, "");
			if(!isAddOperation){
				String isStudentUpdated = RequestUtil.getValueFromRequest(httpServletRequest, RequestUtil.IS_STUDENT_LIST_UPDATED, true, "true");
				if(isStudentUpdated.equalsIgnoreCase("false"))
					isStudentListUpdated = false;
			}
			
			if(isAddOperation || isStudentListUpdated){
	
				String studentsBeforeSave = RequestUtil.getValueFromRequest(httpServletRequest, RequestUtil.STUDENTS, true, "");
				int studentCountBeforeSave = 0;
				if (studentsBeforeSave != null
						&& studentsBeforeSave.trim().length() > 1) {
					studentCountBeforeSave = studentsBeforeSave.split(",").length;
				}
				ArrayList<SessionStudent> sessionStudents = new ArrayList<SessionStudent>(studentCountBeforeSave);
				if (studentCountBeforeSave > 0) {
					String[] studs = studentsBeforeSave.split(",");
					for (String std : studs) {
						StringTokenizer st = new StringTokenizer(std, ":");
						SessionStudent ss = new SessionStudent();
						while (st.hasMoreTokens()) {
							StringTokenizer keyVal = new StringTokenizer(st.nextToken(), "=");
							
							String key = keyVal.nextToken();
							String val = null;
							if(keyVal.countTokens()>0) {
								val= keyVal.nextToken();
							}
	
							if (key.equalsIgnoreCase("studentId")) {
								ss.setStudentId(Integer.valueOf(val));
							} else if (key.equalsIgnoreCase("orgNodeId")) {
								ss.setOrgNodeId(Integer.valueOf(val));
							} else if (key.equalsIgnoreCase("extendedTimeAccom")) {
								ss.setExtendedTimeAccom(val);
							} else if (key.equalsIgnoreCase("statusCopyable")) {
								EditCopyStatus status = new EditCopyStatus();
								status.setCopyable(val);
								ss.setStatus(status);
							} else if (key.equalsIgnoreCase("itemSetForm")) {
								ss.setItemSetForm(val);
							} else if (key.equalsIgnoreCase("isNewStd") && val !=null && val.equalsIgnoreCase("true") ) {
								ss.setNewStudent(true);
							} else if (key.equalsIgnoreCase("extendedTimeFactor") && val != null){
								ss.setExtendedTimeFactor(new Double(val.toString()));
							}
						}
	
						sessionStudents.add(ss);
	
					}
				
			}
				scheduledSession.setStudents(sessionStudents
						.toArray(new SessionStudent[sessionStudents.size()]));
		
		} else {
			ScheduledSession schSession = this.scheduleTest.getScheduledStudentsMinimalInfoDetails(this.userName, scheduledSession.getTestSession().getTestAdminId());
	    	scheduledSession.setStudents(schSession.getStudents());
	    	isStudentManifestsExists = true;
		}
			
		if(scheduledSession.getStudents()!= null && scheduledSession.getStudents().length>0 && !isAddOperation){
			if(TestSessionUtils.isTabeBatterySurveyProduct(productType).booleanValue() || TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue() ||TestSessionUtils.isTabeLocatorProduct(productType).booleanValue()){
				updateStudentstudentManifests(scheduledSession, savedSessionMinData, isStudentManifestsExists );
			}
				
		}
		
	  if(scheduledSession.getStudents()!= null && scheduledSession.getStudents().length>0) {
		  SessionStudent [] sessionStudents = scheduledSession.getStudents();
		  TestElement [] newTEs = scheduledSession.getScheduledUnits();
		  boolean sessionHasLocator = false;
		  if (TestSessionUtils.isTabeBatterySurveyProduct(productType).booleanValue() || TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue()) {
			  String autoLocator =  RequestUtil.getValueFromRequest(httpServletRequest, RequestUtil.HAS_AUTOLOCATOR, true, "false");;
              if ((autoLocator != null) && autoLocator.equals("true")) {            
                  sessionHasLocator = true;
              }
	      }
		  if (TestSessionUtils.isTabeProduct(productType).booleanValue() ||	TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue()) {
			  
			  SubtestVO locSubtest = null ;
			  
			  for(int i=0; i < sessionStudents.length; i++){
				  SessionStudent sessionStudent = sessionStudents[i];
			 

                  // replicate student's manifest if this student has no individual manifest
                  StudentManifest [] studentManifests = sessionStudent.getStudentManifests();
                 if ((studentManifests == null) || (studentManifests.length == 0))
                  {
                      
                      List studentSubtestList = TestSessionUtils.getDefaultSubtests(newTEs);
                      
                      studentManifests = new StudentManifest[studentSubtestList.size()];
                      
                      for (int j=0; j < studentSubtestList.size(); j++)
                      {
                          
                          SubtestVO subtestVO = (SubtestVO)studentSubtestList.get(j);
                          
                          studentManifests[j] = new StudentManifest();
                          
                          studentManifests[j].setItemSetId(subtestVO.getId());
                          studentManifests[j].setItemSetName(subtestVO.getSubtestName());    
                          if (sessionHasLocator)
                              studentManifests[j].setItemSetForm("E");
                          else
                        	  studentManifests[j].setItemSetForm(subtestVO.getLevel());
                          studentManifests[j].setItemSetOrder(new Integer(j + 1));                            
                      }   
                      
                      // set recommended level for this student if there is no locator for this session
                      if (! sessionHasLocator && TestSessionUtils.isTabeProduct(productType).booleanValue())
                      {
                    	  //commented for defect #80363
                          /*Integer studentId = sessionStudent.getStudentId();
                          Integer itemSetId = scheduledSession.getTestSession().getItemSetId() testSession.getItemSetId();
                         // SubtestVO locSubtest = this.locatorSubtest;
                          if (locSubtest == null) {
                              locSubtest = TestSessionUtils.getLocatorSubtest(this.scheduleTest, this.userName, itemSetId); 
                          }
                          if (locSubtest != null) {
                          	Integer locatorItemSetId = locSubtest.getId();
                          	TestSessionUtils.setRecommendedLevelForStudent(this.scheduleTest, this.userName, studentId, itemSetId, locatorItemSetId, studentManifests);
                          }*/
                          Map<Integer, String> subtestItemMap= scheduleTest.getSubtestNames(studentManifests);
                          for (int j=0; j < studentManifests.length; j++)
                          {
                        	  studentManifests[j].setItemSetName(subtestItemMap.get(studentManifests[j].getItemSetId()));
                          }
                      }
                      
                      // set recommended level or null for this student if there is auto locator check box is checked
                      if (sessionHasLocator && TestSessionUtils.isTabeProduct(productType).booleanValue()){
                    	  Map<Integer, String> subtestItemMap= scheduleTest.getSubtestNames(studentManifests);
                    	  Integer studentId = sessionStudent.getStudentId();
                          Integer itemSetId = scheduledSession.getTestSession().getItemSetId() /*testSession.getItemSetId()*/;
                    	  if (locSubtest == null) {
                              locSubtest = TestSessionUtils.getLocatorSubtest(this.scheduleTest, this.userName, itemSetId); 
                          }
                          if (locSubtest != null) {
                          	Integer locatorItemSetId = locSubtest.getId();
                          	TestSessionUtils.setRecommendedLevelForStudent(this.scheduleTest, this.userName, studentId, itemSetId, locatorItemSetId, studentManifests);
                          }
                          
                    	  //StudentManifest [] studentManifestsModified = sessionStudent.getStudentManifests();
                    	  //int count = 0;
                    	  Map<Integer,String> locatorTDMap = scheduledSession.getLocatorSubtestTD();
                    	  if(locatorTDMap!=null){
                    		  for (int j=0; j < studentManifests.length; j++)
                              {
                    			  String subtestName =  subtestItemMap.get(studentManifests[j].getItemSetId());
                    			  if(subtestName.indexOf("Locator") != -1){
                    				  studentManifests[j].setItemSetForm(null);
                    				  studentManifests[j].setItemSetName(subtestName);
                    			  }else{
	                    			  for(Integer ii : locatorTDMap.keySet()){
		                            	  String locatorSubtestName = locatorTDMap.get(ii);
			                			  if(locatorSubtestName != null && locatorSubtestName.contains("Reading")){
			                				  if(subtestName.contains("Reading") || subtestName.contains("Vocabulary")){
			                					  studentManifests[j].setItemSetForm(null);
			                					  break;
			                				  }
			                			  }
			                			  if(locatorSubtestName != null && locatorSubtestName.contains("Language")){
			                				  if(subtestName.contains("Language") ||subtestName.contains("Mechanics") ||subtestName.contains("Spelling")){
			                					  studentManifests[j].setItemSetForm(null);
			                					  break;
			                				  }
			                			  }
			                			  if(locatorSubtestName != null && locatorSubtestName.contains("Computation") || locatorSubtestName != null && locatorSubtestName.contains("Applied")){
			                				  if(subtestName.contains("Computation") ||subtestName.contains("Applied")){
			                					  studentManifests[j].setItemSetForm(null);
			                					  break;
			                				  }
			                			  }
			                		  }
                    			  }
                              }
                    	  }
                      }
                                   
                      sessionStudent.setStudentManifests(studentManifests);
                  }  

	           }
		  }
		  
	  }

			
		} catch (Exception e) {
			e.printStackTrace();
			validationFailedInfo.setKey("SYSTEM_EXCEPTION");
			validationFailedInfo.setMessageHeader(MessageResourceBundle
					.getMessage("System.Exception.Header"));
			validationFailedInfo.updateMessage(MessageResourceBundle
					.getMessage("System.Exception.Body"));
		}

	}
             
	

	private void updateStudentstudentManifests(	ScheduledSession scheduledSession,	ScheduledSession savedSessionMinData, boolean isStudentManifestsExists) throws CTBBusinessException {
		
		if(scheduledSession.getTestSession().getItemSetId().intValue() == savedSessionMinData.getTestSession().getItemSetId().intValue()) {
			if(isStudentManifestsExists){
				return;
			}
			
			ScheduledSession savedSession = this.scheduleTest.getScheduledStudentsMinimalInfoDetails(this.userName, scheduledSession.getTestSession().getTestAdminId());
			SessionStudent[] savedStds = savedSession.getStudents();
			SessionStudent[] scheduledStds = scheduledSession.getStudents();
			Map<Integer, StudentManifest []> stdIdManifestsMap = new TreeMap<Integer, StudentManifest []>();
			Map<Integer, Map<Integer,Integer>> locatorSubtestMap = new TreeMap<Integer, Map<Integer,Integer>>();
			for( SessionStudent std: savedStds) {
				stdIdManifestsMap.put(std.getStudentId(), std.getStudentManifests());
				locatorSubtestMap.put(std.getStudentId(), std.getSavedlocatorTDMap());
			}
			for(SessionStudent std: scheduledStds){
				if(!std.isNewStudent()){
					std.setStudentManifests(stdIdManifestsMap.get(std.getStudentId()) );
					std.setSavedlocatorTDMap(locatorSubtestMap.get(std.getStudentId()));
				}else{
					Map<Integer, Integer> newLocatorMap = new TreeMap<Integer, Integer>(); 
					if (scheduledSession.getLocatorSubtestTD() != null) {
						Set<Integer> keySet = scheduledSession.getLocatorSubtestTD().keySet();
						for (Integer keyVal:keySet) {						
							newLocatorMap.put(keyVal,keyVal);
						}
					}
					std.setSavedlocatorTDMap(newLocatorMap);
				}
			}
			
			
		} else {
			SessionStudent[] scheduledStds = scheduledSession.getStudents();
			for( SessionStudent std: scheduledStds) {
				std.setStudentManifests(null);
			}
			
			
			
		}
		
	}

	private SessionStudent[] updateRestrictedStudentsForTest(SessionStudent[] studentList, Integer testItemSetId, Integer testAdminId) throws CTBBusinessException {
		SessionStudentData restrictedSSD = this.scheduleTest.getRestrictedStudentsForTest(userName, studentList, testItemSetId, testAdminId, null, null, null);
		 SessionStudent [] restStudentNodes = restrictedSSD.getSessionStudents();
		 ArrayList<SessionStudent> sessionStudentList = new ArrayList<SessionStudent>();
		 if (restStudentNodes.length > 0)
         {
			for(int i=0; i<studentList.length; i++){
				boolean found = false;
				for(int j= 0; j<restStudentNodes.length ; j++) {
					if(studentList[i].getStudentId().intValue()== restStudentNodes[j].getStudentId().intValue()){
						found = true;
						break;
					}
					
				}
				if(!found) {
					sessionStudentList.add(studentList[i]);
				}
			}
			return  sessionStudentList.toArray( new SessionStudent [sessionStudentList.size()]);
         } else {
        	return studentList;
         }
	}

	private void populateTestSession(ScheduledSession scheduledSession, ScheduledSession savedSessionMinData, HttpServletRequest request, ValidationFailedInfo validationFailedInfo, boolean isAddOperation) {
		
		 try{
			 TestSession testSession = new TestSession();
			 TestSession existingTestSession = null;
			 Set<Integer> keySet            = this.topNodesMap.keySet();
			 Integer[] topnodeids= (keySet).toArray(new Integer[keySet.size()]);
			 String creatorOrgNodString	    = RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_CREATOR_ORG_NODE, false, null);			
			 Integer itemSetId        		= Integer.valueOf(RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_ITEM_SET_ID, false, null));
			 
			 //TestVO selectedTest = idToTestMap.get(itemSetId);
			 Integer creatorOrgNod = topnodeids[0];
			 if(creatorOrgNodString !=null && creatorOrgNodString.trim().length()>0 ){
				 try{
					 creatorOrgNod = Integer.valueOf(creatorOrgNodString.trim());
				 } catch (Exception e){	 }
			 }
			 
			 Integer productId        			= Integer.valueOf(RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_PRODUCT_ID, true, "-1"));
			 String dailyLoginEndTimeString		=RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_END_TIME, false, null);
			 String dailyLoginStartTimeString	= RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_START_TIME, false, null);
			 String dailyLoginEndDateString		= RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_END_DATE, false, null);
			 String dailyLoginStartDateString	= RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_START_DATE, false, null);

			 Date dailyLoginEndTime   		= DateUtils.getDateFromTimeString(dailyLoginEndTimeString);
			 Date dailyLoginStartTime 		= DateUtils.getDateFromTimeString(dailyLoginStartTimeString);
			 Date dailyLoginEndDate   		= DateUtils.getDateFromDateString(dailyLoginEndDateString);
			 Date dailyLoginStartDate 		= DateUtils.getDateFromDateString(dailyLoginStartDateString);
			 String location          		= RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_LOCATION, false, null);
			 String hasBreakValue     		= RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_HAS_BREAK, false, null);
			 String hasBreak          		= (hasBreakValue == null || !(hasBreakValue.trim().equals("T") || hasBreakValue.trim().equals("F"))) ? "F" :  hasBreakValue.trim();
			 boolean hasBreakBoolean        = (hasBreak.equals("T")) ? true : false;
			 String isRandomize       		= RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_RANDOMIZE, true, "");
			 String timeZone          		= DateUtils.getDBTimeZone( RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_TIME_ZONE, false, null));
			 String sessionName		  		= RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_TEST_NAME, false, null);
			 //String sessionName       		= RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_SESSION_NAME, false, null);
			 String showStdFeedbackVal   	= RequestUtil.getValueFromRequest(request, RequestUtil.SHOW_STUDENT_FEEDBACK, true, "false");
			 String showStdFeedback         = (showStdFeedbackVal==null || !(showStdFeedbackVal.trim().equals("true") || showStdFeedbackVal.trim().equals("false")) )? "F" :(showStdFeedbackVal.trim().equals("true")? "T" : "F");  
			 String productType				= RequestUtil.getValueFromRequest(request, RequestUtil.PRODUCT_TYPE, true, "");
			 String isEndTestSession 		= RequestUtil.getValueFromRequest(request, RequestUtil.TEST_ADMIN_STATUS, true, "");
			 //String formOperand       		= RequestUtil.getValueFromRequest(request, RequestUtil.FORM_OPERAND, true, TestSession.FormAssignment.ROUND_ROBIN);
			 //String overrideFormAssignment 	= RequestUtil.getValueFromRequest(request, RequestUtil.OVERRIDE_FORM_ASSIGNMENT, false, null);
			 //String overrideLoginStartDate    = RequestUtil.getValueFromRequest(request, RequestUtil.OVERRIDE_LOGIN_START_DATE, false, null);
			 /*Date overrideLoginSDate        = null ;
			 if(overrideLoginStartDate!=null)
				 overrideLoginSDate = DateUtils.getDateFromDateString(overrideLoginStartDate);*/
			 //String formAssigned			= RequestUtil.getValueFromRequest(request, RequestUtil.FORM_ASSIGNED, true, "");
			 
			 String testAdminIdString = (RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.TEST_ADMIN_ID, false, null));
			 Integer testAdminId = null;
			 if(!isAddOperation ){
				 testAdminId = Integer.valueOf(testAdminIdString.trim());
				 ScheduledSession dbsavedSessionMinData = scheduleTest.getScheduledSessionDetails(userName, testAdminId);
				 savedSessionMinData.setTestSession(dbsavedSessionMinData.getTestSession());
				 savedSessionMinData.setScheduledUnits(dbsavedSessionMinData.getScheduledUnits());
				 savedSessionMinData.setStudents(dbsavedSessionMinData.getStudents());
				 savedSessionMinData.setCopyable(dbsavedSessionMinData.getCopyable());
				 savedSessionMinData.setStudentsLoggedIn(dbsavedSessionMinData.getStudentsLoggedIn());
				
				 existingTestSession = savedSessionMinData.getTestSession();
			 }
			 String formOperand       		=  TestSession.FormAssignment.ROUND_ROBIN;
			 TestElement selectedTest = scheduleTest.getTestElementMinInfoByIdsAndUserName(this.getCustomerId(), itemSetId, this.userName);
			 if(selectedTest.getOverrideFormAssignmentMethod() != null) {
				 formOperand = selectedTest.getOverrideFormAssignmentMethod();
	           }else if (selectedTest.getForms()!= null && selectedTest.getForms().length > 0 ) {
	        	   formOperand = TestSession.FormAssignment.ROUND_ROBIN;
	            } else {
	            	formOperand = TestSession.FormAssignment.ROUND_ROBIN;
	           }

			 String overrideFormAssignment 	=  selectedTest.getOverrideFormAssignmentMethod();
			 Date overrideLoginSDate  		=  selectedTest.getOverrideLoginStartDate();
			 String formAssigned			=  (selectedTest.getForms() ==null || selectedTest.getForms().length==0)? null: selectedTest.getForms()[0]; 
			 String testName       		    = 	selectedTest.getItemSetName(); 
			 Date overrideLoginEDate  		=  selectedTest.getOverrideLoginEndDate();

			 TestElement tempTestElem = scheduleTest.getTestElementMinInfoByIds(this.getCustomerId(), itemSetId, creatorOrgNod);
			 if (tempTestElem != null) {
				 overrideLoginSDate  		=  tempTestElem.getOverrideLoginStartDate();
			 }
			 
			 // setting default value
			 testSession.setTestAdminId(testAdminId);			 
			 testSession.setLoginEndDate(dailyLoginEndDate);
			 testSession.setDailyLoginEndTime(dailyLoginEndTime);
			 if(testAdminId != null && "true".equalsIgnoreCase(isEndTestSession)){
				 TimeZone defaultTimeZone = TimeZone.getDefault();
				 Date now = new Date(System.currentTimeMillis());
		         now = com.ctb.util.DateUtils.getAdjustedDate(now, defaultTimeZone.getID(), timeZone, now);
	    		 String timeStr = DateUtils.formatDateToTimeString(now);
			     String dateStr = DateUtils.formatDateToDateString(now);
				 //testSession.setTestAdminStatus("PA");
				 testSession.setLoginEndDate(DateUtils.getDateFromDateString(dateStr));
				 testSession.setDailyLoginEndTime(DateUtils.getDateFromTimeString(timeStr));
			 }
	       
	         if(isAddOperation ){
	        	 testSession.setTestAdminType("SE");
	        	 testSession.setActivationStatus("AC"); 
	        	 testSession.setEnforceTimeLimit("T");
	        	 testSession.setCreatedBy(this.userName);
	        	 testSession.setShowStudentFeedback(showStdFeedback);
	        	 testSession.setTestAdminStatus("CU");
	         } else {
	        	 testSession.setTestAdminType(existingTestSession.getTestAdminType());
	        	 testSession.setActivationStatus(existingTestSession.getActivationStatus()); 
	        	 testSession.setEnforceTimeLimit(existingTestSession.getEnforceTimeLimit());
	        	 testSession.setShowStudentFeedback(existingTestSession.getShowStudentFeedback());
	        	 testSession.setSessionNumber(existingTestSession.getSessionNumber());
	        	 testSession.setCreatedBy(existingTestSession.getCreatedBy());
	         }
	         
	         testSession.setCreatorOrgNodeId(creatorOrgNod);
	         testSession.setProductId(productId);	    
	         testSession.setDailyLoginStartTime(dailyLoginStartTime);
	         testSession.setLocation(location);
	         testSession.setEnforceBreak(hasBreak);
	         testSession.setIsRandomize(isRandomize);	         	       
	         testSession.setLoginStartDate(dailyLoginStartDate);
	         testSession.setTimeZone(timeZone);
	         testSession.setTestName(testName);
	         testSession.setTestAdminName(sessionName);

	         if (formOperand.equals(TestSession.FormAssignment.MANUAL))
	             testSession.setFormAssignmentMethod(TestSession.FormAssignment.MANUAL);
	         else if (formOperand.equals(TestSession.FormAssignment.ALL_SAME))
	             testSession.setFormAssignmentMethod(TestSession.FormAssignment.ALL_SAME);
	         else 
	             testSession.setFormAssignmentMethod(TestSession.FormAssignment.ROUND_ROBIN);
	         
	        testSession.setPreferredForm(formAssigned);      
	         
	         testSession.setOverrideFormAssignmentMethod(overrideFormAssignment);
	         testSession.setOverrideLoginStartDate(overrideLoginSDate);
	         testSession.setOverrideLoginEndDate(overrideLoginEDate);
	         
	         testSession.setItemSetId(itemSetId);
	         
	         if (productType!=null && (TestSessionUtils.isTabeProduct(productType).booleanValue()  || TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue()))
	         {
	             testSession.setFormAssignmentMethod(TestSession.FormAssignment.MANUAL);
	             if(overrideFormAssignment!=null)
	            	 testSession.setOverrideFormAssignmentMethod(TestSession.FormAssignment.MANUAL); 
	             
	             testSession.setPreferredForm(null);  
	         }

	         if (hasBreakBoolean)
	         {
	        	String accessCode = RequestUtil.getValuesFromRequest(request, RequestUtil.TEST_ITEM_IND_ACCESS_CODE, true, new String [0])[0];
	         	testSession.setAccessCode(accessCode);    
	         }
	         else
	         {
	        	 String accessCode = RequestUtil.getValueFromRequest(request, RequestUtil.ACCESS_CODE, true, "");
	        	 testSession.setAccessCode(accessCode); 
	         }
	         
	         validateTestSession(testSession, validationFailedInfo);
	         if(!validationFailedInfo.isValidationFailed()) {
	        	validateTestSessionDate(dailyLoginEndDateString,dailyLoginStartDateString, dailyLoginEndTimeString, dailyLoginStartTimeString, timeZone, overrideLoginSDate,overrideLoginEDate, validationFailedInfo, isAddOperation); 
	         }
	         
	         scheduledSession.setTestSession(testSession);
			 
		 } catch (Exception e) {
			 e.printStackTrace();
			 validationFailedInfo.setKey("SYSTEM_EXCEPTION");
			 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("System.Exception.Header"));
			 validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("System.Exception.Body"));
			 
		 }
		 // retrieving data from request
		 
		 
			
		}

     private void validateTestSessionDate(String dailyLoginEndDateString,
			String dailyLoginStartDateString, String dailyLoginEndTimeString,
			String dailyLoginStartTimeString, String timeZonep,
			Date overrideLoginSDate,Date overrideLoginEDate, ValidationFailedInfo validationFailedInfo, boolean isAddOperation) {
    	 if ((DateUtils.validateDateString(dailyLoginStartDateString) == DateUtils.DATE_INVALID) ||( DateUtils.validateDateString(dailyLoginEndDateString)== DateUtils.DATE_INVALID)){
    		 validationFailedInfo.setKey("SaveTest.InvalidDate");
 			 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("SaveTest.InvalidDate.Header"));
 			 validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("SaveTest.InvalidDate.Body"));
    		 
    	 } else{
    		 Date dateStarted = DateUtils.getDateFromDateString(dailyLoginStartDateString);
             Date dateEnded = DateUtils.getDateFromDateString(dailyLoginEndDateString);
             Date timeStarted = DateUtils.getDateFromTimeString(dailyLoginStartTimeString);
             Date timeEnded = DateUtils.getDateFromTimeString(dailyLoginEndTimeString);
             
             String strDateTime = "";
             if (dailyLoginEndDateString != null && dailyLoginEndTimeString != null)
                 strDateTime = dailyLoginEndDateString + " " + dailyLoginEndTimeString;
             Date datetimeEnded = DateUtils.getDateFromDateTimeString(strDateTime);
             String timeZone = timeZonep;
             
             
    		 if( overrideLoginSDate != null && dateStarted.compareTo(overrideLoginSDate ) < 0){
    			 validationFailedInfo.setKey("SaveTest.StartDateBeforeOverrideStartDate");
     			 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("SaveTest.StartDateBeforeOverrideStartDate.Header","" +DateUtils.formatDateToDateString(overrideLoginSDate)));
    		 } else if( overrideLoginEDate != null && dateEnded.compareTo(overrideLoginEDate ) > 0){
    			 validationFailedInfo.setKey("SaveTest.StartDateAfterOverrideEndDate");
     			 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("SaveTest.StartDateAfterOverrideEndDate.Header","" +DateUtils.formatDateToDateString(overrideLoginEDate)));
    		 } else if ( isAddOperation && DateUtils.isBeforeToday(dateStarted, timeZone) ){
    			 validationFailedInfo.setKey("SaveTest.StartDateBeforeOverrideStartDate");
     			 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("SaveTest.StartDateBeforeToday.Header"));
     			 validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("SaveTest.StartDateBeforeToday.Body"));
    		 } else if ( isAddOperation && DateUtils.isBeforeNow(datetimeEnded, timeZone) ) {
    			 validationFailedInfo.setKey("SaveTest.EndDateTimeBeforeNow");
     			 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("SaveTest.EndDateTimeBeforeNow.Header"));
     			 validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("SaveTest.EndDateTimeBeforeNow.Body"));
    		 } else if ( dateStarted.compareTo(dateEnded)>0 ) {
    			 validationFailedInfo.setKey("SaveTest.EndDateBeforeStartDate");
     			 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("SaveTest.EndDateBeforeStartDate"));
    		 } else if( timeStarted.compareTo(timeEnded)>=0 ) {
    			 validationFailedInfo.setKey("SaveTest.EndTimeBeforeStartTime");
     			 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("SaveTest.EndTimeBeforeStartTime"));
    		 } 
    		 
    		 
    	 }
            
		
	}

     private void validateTestSession(TestSession testSession,	ValidationFailedInfo validationFailedInfo) throws Exception {
		String[] TACs = new String[1];
		TACs[0] = testSession.getAccessCode();
		if( testSession.getTestAdminName() == null || testSession.getTestAdminName().trim().length()==0 ) {
			validationFailedInfo.setKey("SaveTest.TestSessionNameRequired");
			validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("SaveTest.TestSessionNameRequired.Header"));
			validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("SaveTest.TestSessionNameRequired.Body"));
		}else if (!WebUtils.validString(testSession.getTestAdminName())) {
			validationFailedInfo.setKey("SelectSettings.TestSessionName.InvalidCharacters");
			validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("SelectSettings.TestSessionName.InvalidCharacters.Header"));
			validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("SelectSettings.TestSessionName.InvalidCharacters.Body"));
		} else if (!WebUtils.validString(testSession.getLocation())) {
			validationFailedInfo.setKey("SelectSettings.TestLocation.InvalidCharacters");
			validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("SelectSettings.TestLocation.InvalidCharacters.Header"));
			validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("SelectSettings.TestLocation.InvalidCharacters.Body"));
		} else if (hasEmptyTAC(TACs)) {
			if (testSession.getEnforceBreak().equals("T")) {
				validationFailedInfo.setKey("TAC.MissingTestAccessCodes");
	 			validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("TAC.MissingTestAccessCodes.Header"));
	 			validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("TAC.MissingTestAccessCodes.Body1"));
	 			validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("TAC.MissingTestAccessCodes.Body2"));
			} else {
				validationFailedInfo.setKey("TAC.MissingTestAccessCode");
				validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("TAC.MissingTestAccessCode.Header"));
				validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("TAC.MissingTestAccessCode.Body"));
			}
		} else if (hasSpecialCharInTAC(TACs)) {
			validationFailedInfo.setKey( "TAC.SpecialCharNotAllowed");
 			validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage( "TAC.SpecialCharNotAllowed.Header"));
 			validationFailedInfo.updateMessage(MessageResourceBundle.getMessage( "TAC.SpecialCharNotAllowed.Body"));
		} else if (hasInvalidateTACLength(TACs)) {
			validationFailedInfo.setKey("TAC.SixChars");
 			validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("TAC.SixChars"));
		}  

	}
     private void validateScheduledUnits(ScheduledSession scheduledSession,	boolean hasBreakBoolean, ValidationFailedInfo validationFailedInfo, boolean isAddOperation) {
    	try{
    		TestElement[] newTEs = scheduledSession.getScheduledUnits();
    		//boolean hasAL = ((form.getAutoLocator() != null) && form.getAutoLocator().equals("true"));
            //if (hasAL)
             //   TACs = new String[this.defaultSubtests.size() + 1];
           // else
       	 
       	 	String[] TACs = null;
       	 	if(!hasBreakBoolean){
	       		 TACs = new String[1];
	             TACs[0] = scheduledSession.getTestSession().getAccessCode();
       	 	} else {
       		 TACs = new String[newTEs.length];
       		 for(int i=0; i<newTEs.length; i++) {
       			 TACs[i] = newTEs[i].getAccessCode();
       		 }
       	 }
       	 
       	 	if (hasBreakBoolean && hasEmptyTAC(TACs)) {
    			validationFailedInfo.setKey("TAC.MissingTestAccessCodes");
    			validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("TAC.MissingTestAccessCodes.Header"));
    			validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("TAC.MissingTestAccessCodes.Body1"));
    			validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("TAC.MissingTestAccessCodes.Body2"));
    			
    		} else if (hasBreakBoolean && hasSpecialCharInTAC(TACs)) {
    			validationFailedInfo.setKey( "TAC.SpecialCharNotAllowed");
    			validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage( "TAC.SpecialCharNotAllowed.Header"));
    			validationFailedInfo.updateMessage(MessageResourceBundle.getMessage( "TAC.SpecialCharNotAllowed.Body"));
    		} else if (hasBreakBoolean && hasInvalidateTACLength(TACs)) {
    			validationFailedInfo.setKey("TAC.SixChars");
    			validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("TAC.SixChars"));
    		} else if (hasBreakBoolean && hasDuplicateTAC(TACs)) {
    			validationFailedInfo.setKey("TAC.IdenticalTestAccessCodes");
    			validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("TAC.IdenticalTestAccessCodes.Header"));
    			validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("TAC.IdenticalTestAccessCodes.Body1"));
    			validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("TAC.IdenticalTestAccessCodes.Body2"));
    		}else if (isValidTAC(scheduledSession, TACs,validationFailedInfo)){
    			// do nothing validationFailedInfo is populated
    		}
    		
    	}catch (Exception e) {
   		 e.printStackTrace();
		 validationFailedInfo.setKey("SYSTEM_EXCEPTION");
		 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("System.Exception.Header"));
		 validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("System.Exception.Body"));
	 }
    	 
    	 
    	
    	 
    	 
 		
 	}
     
     private boolean isValidTAC(ScheduledSession scheduledSession, String[] TACs, ValidationFailedInfo validationFailedInfo) {

         String [] validateResults=null;
         boolean found = false;
         try
         {
             validateResults = this.scheduleTest.validateAccessCodes(this.userName, TACs,scheduledSession.getTestSession().getTestAdminId());
         }
         catch (CTBBusinessException e)
         {
             e.printStackTrace();    
         }
         
         if (validateResults != null)
         {
             Vector<String> tacsInuse = new Vector<String>();
             for (int i=0; i < validateResults.length; i++)
             {
                 if (validateResults[i] != null && validateResults[i].indexOf("exists") >= 0)
                 {
                	 found = true;
                     tacsInuse.add(TACs[i]);
                 }
                 
             }
             if (tacsInuse.size() > 1)
             {
            	 validationFailedInfo.setKey("TAC.InvalidTestAccessCode.Header2");
            	 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("TAC.InvalidTestAccessCode.Header2"));
            	 validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("TAC.InvalidTestAccessCode.InUse2", getTACsInString(tacsInuse)));
             }                    
             else if (tacsInuse.size() == 1)
             {
            	 validationFailedInfo.setKey("TAC.InvalidTestAccessCode.Header");
            	 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("TAC.InvalidTestAccessCode.Header"));
            	 validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("TAC.InvalidTestAccessCode.InUse", getTACsInString(tacsInuse)));
             }
             
             /*if (scheduledSession.getTestSession().getEnforceBreak().equals("T")){
            	 validationFailedInfo.setKey("TAC.InvalidTestAccessCode.Footer.WithBreak");
            	 validationFailedInfo.setMessageHeader("TAC.InvalidTestAccessCode.Footer.WithBreak");
                 //validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("TAC.InvalidTestAccessCode.Footer.WithBreak"));
             } else {
            	 validationFailedInfo.setKey("TAC.InvalidTestAccessCode.Footer.NoBreak");
            	 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("TAC.InvalidTestAccessCode.Footer.NoBreak"));
            	 //validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("TAC.InvalidTestAccessCode.Footer.NoBreak"));
             }*/
            	 
         }
         
         return found;
     
	
	} 
     
	private boolean hasDuplicateTAC(String[] TACs) {
		boolean found = false;
		if (TACs.length <= 1)
			return false;
		for (int i = 0; i < TACs.length && !found; i++) {
			for (int j = i + 1; j < TACs.length && !found; j++) {
				if (TACs[i] != null && TACs[i].equalsIgnoreCase((TACs[j]))) {
					found = true;
					break;
				}

			}
			if (found) {
				break;
			}
		}
		return found;
	}


	private boolean hasInvalidateTACLength(String[] TACs) {
		boolean found = false;
		for (int i = 0; i < TACs.length && !found; i++) {
			if (TACs[i] != null && TACs[i].length() < 6) {
				found = true;
				break;
			}
				
		}
		return found;
	}


	private boolean hasSpecialCharInTAC(String[] TACs) {
		boolean found = false;
		for (int i = 0; i < TACs.length && !found; i++) {
			for (int j = 0; j < TACs[i].length() && !found; j++) {
				char currentChar = TACs[i].charAt(j);
				if (!(currentChar >= 'A' && currentChar <= 'Z'
						|| currentChar >= 'a' && currentChar <= 'z'
						|| currentChar >= '0' && currentChar <= '9' || currentChar == '_')){
					found = true;
				    break;
				}
					
			}
			if(found)
				 break;
		}
		return found;
	}


	private boolean hasEmptyTAC(String[] TACs) {
		boolean found = false;
		for (int i = 0; i < TACs.length && !found; i++) {
			if ("".equals(TACs[i])) {
				found = true;
				break;
			}
				
		}
		return found;
	}


	private void initialize() {
		getLoggedInUserPrincipal();
		if(this.customerConfigurations == null)	getCustomerConfigurations();  
		UserNodeData und = null;

		try {
			if(this.user == null || (this.isEOIUser && this.isMappedWith3_8User) || this.topNodesMap == null || (this.topNodesMap!=null && this.topNodesMap.size()==0 ) ) {
				if(this.user ==null || (this.isEOIUser && this.isMappedWith3_8User))
					this.user =  userManagement.getUser(this.userName, this.userName);
				if((this.isEOIUser && this.isMappedWith3_8User)) {
					this.topNodesMap = new LinkedHashMap<Integer, String>();
	    		}
				und = this.scheduleTest.getTopUserNodesForUser(this.userName, null,
						null, null, null);
		        SortParams sortParams = FilterSortPageUtils.buildSortParams(FilterSortPageUtils.ORGNODE_DEFAULT_SORT, FilterSortPageUtils.ASCENDING, null, null);            
				und.applySorting(sortParams);
				
				UserNode[] nodes = und.getUserNodes();
				for (int i = 0; i < nodes.length; i++) {
					UserNode node = (UserNode) nodes[i];
					if (node != null) {
						this.topNodesMap.put(node.getOrgNodeId(), node
								.getOrgNodeName());
					}

				}
			}
		} catch (CTBBusinessException e) {
			e.printStackTrace();
		}
	}

	private int getProductIndexByID(String selectedProductId) {
    	 int productIndex = -1;
    	 int counter = 0;
    	 if (selectedProductId == null)
             return -1;
    	 int val = 0;
    	 try {
    		 val = Integer.valueOf(selectedProductId);
    	 } catch (NumberFormatException ne) {
    		 return -1;
    	 }
    	 
         for (TestProduct tp :tps) {
        	 
        	 if(tp.getProductId().intValue() == val) {
        		 productIndex =  counter;
        		 break;
        	 }
        		
        	 counter = counter+1;
         }
    	 
    	 return productIndex;
	}
     
     
    

	private TestProductData getTestProductDataForUser() throws CTBBusinessException
    {
        TestProductData tpd = null;                
        SortParams sortParams = FilterSortPageUtils.buildSortParams("ProductName", ColumnSortEntry.ASCENDING, null, null);            
        tpd = this.scheduleTest.getTestProductsForUser(this.userName,null,null,sortParams);
        return tpd;
    }
    
    private boolean isUserPasswordExpired()
    {
    	boolean pwdExpiredStatus = false;    	
    	Date passwordExpirationDate = this.user.getPasswordExpirationDate();
    	Date CurrentDate = new Date();
    	if (CurrentDate.compareTo(passwordExpirationDate)> 0 ){
    		pwdExpiredStatus = true;
    	}
    	return pwdExpiredStatus;
    } 
    
    
    /////////////////////////////////////////////////////////////////////////////////////////////    
    ///////////////////////////// BEGIN OF NEW NAVIGATION ACTIONS ///////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////    
    
    /**
     * ASSESSMENTS actions
     */    
    @Jpf.Action(forwards = { 
            @Jpf.Forward(name = "sessionsLink", path = "assessments_sessions.do"),
            @Jpf.Forward(name = "programStatusLink", path = "assessments_programStatus.do"),
            @Jpf.Forward(name = "studentRegistrationLink", path = "assessments_studentRegistrationLink.do")
        }) 
    protected Forward assessments()
    {
    	String menuId = (String)this.getRequest().getParameter("menuId");    	
    	String forwardName = (menuId != null) ? menuId : "sessionsLink";
    	
        return new Forward(forwardName);
    }

    @Jpf.Action(forwards = { 
            @Jpf.Forward(name = "success", path = "assessments_sessions.jsp") 
        }) 
    protected Forward assessments_sessions()  throws CTBBusinessException
    {	if(getSession().getAttribute("is3to8Selected") == null)
			this.is3to8Selected = (getRequest().getParameter("is3to8Selected") != null && "true".equalsIgnoreCase(getRequest().getParameter("is3to8Selected").toString()))? true: false; 
    	if(getSession().getAttribute("isEOISelected") == null)
    		this.isEOISelected = (getRequest().getParameter("isEOISelected") != null && "true".equalsIgnoreCase(getRequest().getParameter("isEOISelected").toString()))? true: false;
    	if(getSession().getAttribute("isUserLinkSelected") == null)
    		this.isUserLinkSelected = (getRequest().getParameter("isUserLinkSelected") != null && "true".equalsIgnoreCase(getRequest().getParameter("isUserLinkSelected").toString()))? true: false;
    	
    	if(getSession().getAttribute("isEOIUser") != null)
			this.isEOIUser = new Boolean(getSession().getAttribute("isEOIUser").toString()).booleanValue();
		else
			this.isEOIUser = this.userManagement.isOKEOIUser(getRequest().getUserPrincipal().toString()); //need to check and populate this flag

		if(getSession().getAttribute("isMappedWith3_8User") != null)
			this.isMappedWith3_8User = new Boolean(getSession().getAttribute("isMappedWith3_8User").toString()).booleanValue();
		else
			this.isMappedWith3_8User = this.userManagement.isMappedWith3_8User(getRequest().getUserPrincipal().toString()); //need to check and populate this flag
		
    	getLoggedInUserPrincipal();
        getUserDetails();
        CustomerConfiguration [] customerConfigs = getCustomerConfigurations(this.customerId);
        setupUserPermission(customerConfigs);

        return new Forward("success");
    }
    
    @Jpf.Action(forwards={
    		@Jpf.Forward(name = "success", 
					path ="assessments_sessions.jsp")
	})
    protected Forward getSessionForUserHomeGrid(SessionOperationForm form) throws CTBBusinessException{
    	//System.out.println("getSessionForUserHomeGrid START....."+new Date());
		HttpServletRequest req = getRequest();
		HttpServletResponse resp = getResponse();
		OutputStream stream = null;
		String json = "";
		try {
			//System.out.println ("db process time Start:"+new Date());
			
			// get licenses
			CustomerLicense[] customerLicenses =  null;
			if(this.hasLicenseConfig) {
			//Temporary change to hide register student button
			//	customerLicenses = getCustomerLicenses(); 
			}
			/* if ((customerLicenses != null) && (customerLicenses.length > 0))
	        {
	            this.getRequest().setAttribute("customerLicenses", getLicenseQuantitiesByOrg());
	           // this.getSession().setAttribute("hasLicenseConfig", new Boolean(true));
	        }*/
			if(getSession().getAttribute("isEOIUser") != null)
				this.isEOIUser = new Boolean(getSession().getAttribute("isEOIUser").toString()).booleanValue();
			else
				this.isEOIUser = this.userManagement.isOKEOIUser(getRequest().getUserPrincipal().toString()); //need to check and populate this flag

			if(getSession().getAttribute("isMappedWith3_8User") != null)
				this.isMappedWith3_8User = new Boolean(getSession().getAttribute("isMappedWith3_8User").toString()).booleanValue();
			else
				this.isMappedWith3_8User = this.userManagement.isMappedWith3_8User(getRequest().getUserPrincipal().toString()); //need to check and populate this flag
				
			if(this.userName == null || (this.isEOIUser && this.isMappedWith3_8User)) {
				getLoggedInUserPrincipal();		
				getUserDetails();
			}
			OrgNodeCategory orgNodeCategory = UserOrgHierarchyUtils.getCustomerLeafNodeDetail(this.userName,this.customerId,this.userManagement );
	     	
	        // retrieve information for user test sessions
			FilterParams sessionFilter = null;
	        PageParams sessionPage = null;
	        SortParams sessionSort = null;
	        sessionSort = FilterSortPageUtils.buildSortParams(FilterSortPageUtils.TESTSESSION_DEFAULT_SORT, FilterSortPageUtils.ASCENDING);
	        TestSessionData tsd = getTestSessionsForUserHome(sessionFilter, sessionPage, sessionSort);
	        //System.out.println ("db process time End:"+new Date());
	        Base base = new Base();
			base.setPage("1");
			base.setRecords("10");
			base.setTotal("2");
			if ((tsd != null) && (tsd.getFilteredCount().intValue() > 0))
			{
				//System.out.println ("List process time Start:"+new Date());
				base = buildTestSessionList(customerLicenses, tsd, base); 
				//System.out.println ("List process time End:"+new Date());
			} else {
				this.setSessionListCUFU(new ArrayList<TestSessionVO>());
		        this.setSessionListPA(new ArrayList<TestSessionVO>());
		        this.setSessionListCUFUMap(new HashMap<Integer, Map>());
		        this.setSessionListPAMap(new HashMap<Integer, Map>());
		        base.setTestSessionCUFU(sessionListCUFU);
		        base.setTestSessionPA(sessionListPA);
		        base.setSessionListCUFUMap(sessionListCUFUMap);
		        base.setSessionListPAMap(sessionListPAMap);
			}
			base.setOrgNodeCategory(orgNodeCategory);
			
			
			//System.out.println("just b4 gson");	
			Gson gson = new Gson();
			//System.out.println ("Json process time Start:"+new Date());
			
			json = gson.toJson(base);
			//System.out.println ("Json process time End:"+new Date());


			
			try{
				resp.setContentType(CONTENT_TYPE_JSON);
	    		stream = resp.getOutputStream();

	    		String acceptEncoding = req.getHeader("Accept-Encoding");
	    		System.out.println("acceptEncoding..."+acceptEncoding.toString());

	    		if (acceptEncoding != null && acceptEncoding.contains("gzip")) {
	    		    resp.setHeader("Content-Encoding", "gzip");
	    		    stream = new GZIPOutputStream(stream);
	    		}
	    		
				resp.flushBuffer();
	    		stream.write(json.getBytes());				
			}

			finally{
				if (stream!=null){
					stream.close();
				}
			}



		} catch (Exception e) {
			System.err.println("Exception while processing CR response.");
			e.printStackTrace();
		}
		//System.out.println("getSessionForUserHomeGrid END....."+new Date());
		return null;

	}
    
    @Jpf.Action(forwards={
    		@Jpf.Forward(name = "success", 
					path ="assessments_sessions.jsp")
	})
    protected Forward getCompletedSessionForGrid(SessionOperationForm form){
    	System.out.println("completed");
		HttpServletRequest req = getRequest();
		HttpServletResponse resp = getResponse();
		OutputStream stream = null;
		String json = "";
		try {
			Base base = new Base();
			base.setPage("1");
			base.setRecords("10");
			base.setTotal("2");
			base.setTestSessionCUFU(this.sessionListCUFU);
			base.setTestSessionPA(this.sessionListPA);
			base.setSessionListPAMap(this.sessionListPAMap);
			Gson gson = new Gson();
			System.out.println ("completed Tab Json process time Start:"+new Date());
			json = gson.toJson(base);
			//System.out.println ("Json process time End:"+new Date() +".."+json);
			try{
				resp.setContentType(CONTENT_TYPE_JSON);
	    		stream = resp.getOutputStream();

	    		String acceptEncoding = req.getHeader("Accept-Encoding");
	    		System.out.println("acceptEncoding..."+acceptEncoding.toString());

	    		if (acceptEncoding != null && acceptEncoding.contains("gzip")) {
	    		    resp.setHeader("Content-Encoding", "gzip");
	    		    stream = new GZIPOutputStream(stream);
	    		}
	    		
				resp.flushBuffer();
	    		stream.write(json.getBytes());				
				
			}
			finally{
				if (stream!=null){
					stream.close();
				}
			}
		} catch (Exception e) {
			System.err.println("Exception while processing CR response.");
			e.printStackTrace();
		}

		return null;

	}
    
    
    @Jpf.Action(forwards={
    		@Jpf.Forward(name = "success", 
					path ="assessments_sessions.jsp")
	})
    protected Forward getStudentForList(SessionOperationForm form){
    	
		HttpServletResponse resp = getResponse();
		OutputStream stream = null;
		String json = "";
		Map<Integer,Map> accomodationMap = new HashMap<Integer, Map>();
		
		String testId = getRequest().getParameter("selectedTestId");
		String treeOrgNodeId = getRequest().getParameter("stuForOrgNodeId");
		String blockOffGrade = getRequest().getParameter("blockOffGradeTesting");
		String selectedLevel = getRequest().getParameter("selectedLevel");
		String testAdminIdString = getRequest().getParameter("testAdminId");
		String productType = getRequest().getParameter("productType");
		
		Integer selectedOrgNodeId = null;
		Integer selectedTestId = null;
		Integer testAdminId = null;
		
		if(productType.equalsIgnoreCase("genericProductType")){
			this.selectedProductType = "PT";
		}else if(productType.equalsIgnoreCase("tabeLocatorProductType")){
			this.selectedProductType = "TL";
		}else if(productType.equalsIgnoreCase("tabeBatterySurveyProductType")){
			this.selectedProductType = "TB";
		}
		
		if(treeOrgNodeId != null)
			selectedOrgNodeId = Integer.parseInt(treeOrgNodeId);
		if(testId != null)
			selectedTestId = Integer.parseInt(testId);
		try{
			if(testAdminIdString != null){
				testAdminId = Integer.valueOf(testAdminIdString.trim());
			}
		} catch (Exception e){	}
		
		try {
			FilterParams studentFilter = null;
			if(blockOffGrade != null && blockOffGrade.equalsIgnoreCase("true")) { //Changes for block off grade testing
				studentFilter = generateFilterParams(selectedLevel);				
			}
	        PageParams studentPage = null;
	        SortParams studentSort = null;
	        //studentSort = FilterSortPageUtils.buildSortParams(FilterSortPageUtils.STUDENT_DEFAULT_SORT, FilterSortPageUtils.ASCENDING);
	        // get students - getSessionStudents
	        SessionStudentData ssd = getSessionStudents(selectedOrgNodeId, testAdminId, selectedTestId, studentFilter, studentPage, studentSort);
	        
	        // remove status field if this is copy session
        	if (this.isCopySession) {
		        for (int i=0;i<ssd.getSessionStudents().length;i++)
		        {
	        		EditCopyStatus ecs = ssd.getSessionStudents()[i].getStatus();
	        		ecs.setCode("");
	        		ecs.setCopyable("T");
		        }
        	}        
	       
        	if(getRequest().getParameter("productSelected") != null && ssd.getSessionStudents().length >0){
        		
		        ScheduledSession scheduledSession = this.scheduleTest.getInActiveRosteredStudentsForSession(testAdminId);
    	    	SessionStudent[] students =  scheduledSession.getStudents();
    	    	
    	    	for (int j=0;j<students.length;j++){
    	    		for (int i=0;i<ssd.getSessionStudents().length;i++){
    	    			if (ssd.getSessionStudents()[i].getOutOfSchool()!="Yes" && ssd.getSessionStudents()[i].getStudentId().equals(students[j].getStudentId()))
			        	{
			        		ssd.getSessionStudents()[i].setOutOfSchool("Yes");
			        		break;
			        	}			        		
    	    		}
    	    	}
        	}
	        
	        //** Story: TABE Adaptive FT - 06 - Modify TABE Scheduling � Logic
	        //** If we are scheduling a testlet
	        //** 4201 = TABE Adult Common Core Experience 
	        if (getRequest().getParameter("productSelected").equalsIgnoreCase(
					"4201") && ssd.getSessionStudents().length > 0) {
	        	//Changes for Production QC Defect #81134
				StudentTestletInfo[] sti = this.scheduleTest.getStudentCompletedTabe9Or10(ssd, selectedTestId);	
				//End of Changes for Production QC Defect #81134
				
				for (int i = 0; i < ssd.getSessionStudents().length; i++) {
					if (ssd.getSessionStudents()[i].getOutOfSchool() != "Yes") {
						// ** fixed defec#78764
						if (sti.length > 0 && !hasStudentCompletedTabe9Or10(ssd.getSessionStudents()[i].getStudentId(), sti)) {
							ssd.getSessionStudents()[i].setOutOfSchool("Yes");
						} else if (sti.length == 0) {
							ssd.getSessionStudents()[i].setOutOfSchool("Yes");
						}
					}
				}
			}
	        
	        List<SessionStudent> studentNodes = buildStudentList(ssd.getSessionStudents(),accomodationMap);
			Base base = new Base();
			base.setPage("1");
			base.setRecords("10");
			base.setTotal("2");
			base.setStudentNode(studentNodes);
			if(this.studentGradesForCustomer == null)				
				this.studentGradesForCustomer = (List<String>)getSession().getAttribute("studentGradesForCustomer");
			base.setGradeList(this.studentGradesForCustomer);			
			base.setAccomodationMap(accomodationMap);
			
			// get licenses
			boolean licenseProduct = (this.selectedProductType.equals("TL") || this.selectedProductType.equals("PT")) ? false : true;
			if (this.hasLicenseConfig && licenseProduct) {
				CustomerLicense[] customerLicenses = getCustomerLicenses(); 
				Node n = this.orgNode.getOrgNodeById(selectedOrgNodeId);
				if ((customerLicenses != null) && (customerLicenses.length > 0)) {
					CustomerLicense cl = customerLicenses[0];
				    OrgNodeLicenseInfo onli = getLicenseQuantitiesByOrg(selectedOrgNodeId, cl.getProductId(), cl.getSubtestModel());
				    Integer available = (onli.getLicPurchased() != null) ? onli.getLicPurchased() : new Integer(0);
			        List<Row> rowList = new ArrayList<Row>();
					Row row = new Row(0);
					String[] cells = new String[5];
					cells[0] = selectedOrgNodeId.toString();
					cells[1] = n.getOrgNodeName();
					cells[2] = cl.getSubtestModel();
					cells[3] = String.valueOf(this.numberSelectedSubtests);	
					cells[4] = available.toString();
					row.setCell(cells);			
					rowList.add(row);
					base.setRows(rowList);
				}else{
					String subtestModel = getCustomerLicensesModel(); 
					List<Row> rowList = new ArrayList<Row>();
					Row row = new Row(0);
					String[] cells = new String[5];
					cells[0] = selectedOrgNodeId.toString();
					cells[1] = n.getOrgNodeName();
					cells[2] = subtestModel;
					cells[3] = String.valueOf(this.numberSelectedSubtests);	
					cells[4] = new String("0");
					row.setCell(cells);			
					rowList.add(row);
					base.setRows(rowList);
				}
			}
			
			Gson gson = new Gson();
			json = gson.toJson(base);
			try{
				resp.setContentType("application/json");
				stream = resp.getOutputStream();
				resp.flushBuffer();
				stream.write(json.getBytes("UTF-8"));
			}
			finally{
				if (stream!=null){
					stream.close();
				}
			}
		} catch (Exception e) {
			System.err.println("Exception while processing getStudentForList response.");
			e.printStackTrace();
		}

		return null;

	}
    
    /**
     * getLicenseQuantitiesByOrg
     */    
    private OrgNodeLicenseInfo getLicenseQuantitiesByOrg(Integer orgNodeId, Integer productId, String subtestModel) {
        OrgNodeLicenseInfo onli = null;
        try {
            onli = this.licensing.getLicenseQuantitiesByOrgNodeIdAndProductId(this.userName, 
										                    orgNodeId, 
										                    productId, 
										                    subtestModel);
        }    
        catch (CTBBusinessException be) {
            be.printStackTrace();
        }
        return onli;
    }
    
    
    @Jpf.Action(forwards={
			@Jpf.Forward(name = "success", 
					path ="find_user_hierarchy.jsp")
	})
	protected Forward userOrgNodeHierarchyList(SessionOperationForm form){

		String jsonTree = "";
		HttpServletRequest req = getRequest();
		HttpServletResponse resp = getResponse();
		OutputStream stream = null;
		//String contentType = CONTENT_TYPE_JSON;
		try {
			BaseTree baseTree = new BaseTree ();

			ArrayList<Organization> completeOrgNodeList = new ArrayList<Organization>();
			UserNodeData associateNode = UserOrgHierarchyUtils.populateAssociateNode(this.userName,this.userManagement);
			ArrayList<Organization> selectedList  = UserOrgHierarchyUtils.buildassoOrgNodehierarchyList(associateNode);
			Collections.sort(selectedList, new OrgnizationComparator());
			ArrayList <Integer> orgIDList = new ArrayList <Integer>();
			ArrayList<TreeData> data = new ArrayList<TreeData>();

			UserNodeData und = UserOrgHierarchyUtils.OrgNodehierarchy(this.userName, 
					this.userManagement, selectedList.get(0).getOrgNodeId()); 
			ArrayList<Organization> orgNodesList = UserOrgHierarchyUtils.buildOrgNodehierarchyList(und, orgIDList,completeOrgNodeList);	

			//jsonTree = generateTree(orgNodesList,selectedList);

			for (int i= 0; i < selectedList.size(); i++) {

				if (i == 0) {

					preTreeProcess (data,orgNodesList,selectedList);

				} else {

					Integer nodeId = selectedList.get (i).getOrgNodeId();
					if (orgIDList.contains(nodeId)) {
						continue;
					} else if (!selectedList.get (i).getIsAssociate()) {
						
						continue;
						
					} else {

						orgIDList = new ArrayList <Integer>();
						UserNodeData undloop = UserOrgHierarchyUtils.OrgNodehierarchyForValidUser(this.userName, 
								this.userManagement,nodeId);   
						ArrayList<Organization> orgNodesListloop = UserOrgHierarchyUtils.buildOrgNodehierarchyList(undloop, orgIDList, completeOrgNodeList);	
						preTreeProcess (data,orgNodesListloop,selectedList);
					}
				}


			}

			Gson gson = new Gson();
			baseTree.setData(data);
			Collections.sort(baseTree.getData(), new Comparator<TreeData>(){

				public int compare(TreeData t1, TreeData t2) {
					return (t1.getData().toUpperCase().compareTo(t2.getData().toUpperCase()));
				}
					
			});
			jsonTree = gson.toJson(baseTree);
			String pattern = ",\"children\":[]";
			jsonTree = jsonTree.replace(pattern, "");
			//System.out.println(jsonTree);
			try {
				resp.setContentType(CONTENT_TYPE_JSON);
	    		stream = resp.getOutputStream();

	    		String acceptEncoding = req.getHeader("Accept-Encoding");
	    		System.out.println("acceptEncoding..."+acceptEncoding.toString());

	    		if (acceptEncoding != null && acceptEncoding.contains("gzip")) {
	    		    resp.setHeader("Content-Encoding", "gzip");
	    		    stream = new GZIPOutputStream(stream);
	    		}
	    		
				resp.flushBuffer();
	    		stream.write(jsonTree.getBytes());
				
			} finally{
				if (stream!=null){
					stream.close();
				}
			}
		} catch (Exception e) {
			System.err.println("Exception while processing userOrgNodeHierarchyList.");
			e.printStackTrace();
		}

		return null;

	}
    
    
    @Jpf.Action(forwards={
			@Jpf.Forward(name = "success", 
					path ="find_user_hierarchy.jsp")
	})
	protected Forward userTreeOrgNodeHierarchyList(SessionOperationForm form){

		String jsonTree = "";
		HttpServletResponse resp = getResponse();
		OutputStream stream = null;
		//String contentType = CONTENT_TYPE_JSON;
		Integer testAdminId = Integer.valueOf(this.getRequest().getParameter("testAdminId"));
		Integer orgNodeId = Integer.valueOf(this.getRequest().getParameter("orgNodeId"));
		Integer orgNodeIdTemp;
		int studentCount = getRosterForTestSession(testAdminId);
		initializeCustomerConfigurations();
		
		try {
			BaseTree baseTree = new BaseTree ();
			ArrayList<TreeData> data = new ArrayList<TreeData>();
			if(studentCount > 0){
				baseTree.setIsStudentExist("true");		
			
				ArrayList<Organization> completeOrgNodeList = new ArrayList<Organization>();
				ArrayList <Integer> orgIDList = new ArrayList <Integer>();
				
				StudentNodeData snd = this.scheduleTest.getTopTestTicketNodesForPrintTT(this.userName, testAdminId, null, null, null);
				if (snd != null) {
					StudentNode[] nodes = snd.getStudentNodes(); 
			        if(nodes.length > 0){
			            for (int i = 0 ; i < nodes.length ; i++) {
			            	StudentNode node = (StudentNode)nodes[i];
			                if (node != null) {
			                	orgNodeIdTemp = node.getOrgNodeId();
			                	StudentNodeData sndTemp = this.scheduleTest.getTestTicketNodesHaveStudentForParent(this.userName, orgNodeIdTemp, testAdminId, null, null, null);
			                	
			                	ArrayList<Organization> selectedList = new ArrayList<Organization>();
			    				ArrayList<Organization> orgNodesList = UserOrgHierarchyUtils.buildOrgNodeAncestorHierarchyList(sndTemp, orgIDList,completeOrgNodeList);	

			    				preTreeProcessPTT (data,orgNodesList,selectedList, i);
			                }
			            }
				 	}else{
						baseTree.setIsStudentExist("false");
					}
			     }
				//StudentNodeData snd = this.scheduleTest.getTestTicketNodesHaveStudentForParent(this.userName, orgNodeId, testAdminId, null, null, null);
				//ArrayList<Organization> selectedList = new ArrayList<Organization>();

				//ArrayList<Organization> orgNodesList = UserOrgHierarchyUtils.buildOrgNodeAncestorHierarchyList(snd, orgIDList,completeOrgNodeList);	
	
	
	
				//preTreeProcess (data,orgNodesList,selectedList);
			
				
			}else{
				baseTree.setIsStudentExist("false");
			}

			Gson gson = new Gson();
			baseTree.setData(data);
			Collections.sort(baseTree.getData(), new Comparator<TreeData>(){

				public int compare(TreeData t1, TreeData t2) {
					return (t1.getData().toUpperCase().compareTo(t2.getData().toUpperCase()));
				}
					
			});
			baseTree.setShowAccessCode(customerHasAccessCode(testAdminId));
			baseTree.setHasPrintClassName(customerHasPrintClassName());
			baseTree.setWVCustomer(isWVCustomer());
			baseTree.setShowMultipleAccessCode(customerHasMultipleAccessCode());
			baseTree.setHasPrintSessionName(customerHasPrintSessionName());
			jsonTree = gson.toJson(baseTree);
			String pattern = ",\"children\":[]";
			jsonTree = jsonTree.replace(pattern, "");
			//System.out.println(jsonTree);
			try {

				resp.setContentType(CONTENT_TYPE_JSON);
				resp.flushBuffer();
				stream = resp.getOutputStream();
				stream.write(jsonTree.getBytes("UTF-8"));
			} finally{
				if (stream!=null){
					stream.close();
				}
			}
		} catch (Exception e) {
			System.err.println("Exception while processing userTreeOrgNodeHierarchyList.");
			e.printStackTrace();
		}

		return null;

	}
    
    
    @Jpf.Action(forwards={
			@Jpf.Forward(name = "success", 
					path ="assessments_sessions.jsp")
	})
    protected Forward getSessionForSelectedOrgNodeGrid(SessionOperationForm form){
    	System.out.println("selected");
		HttpServletResponse resp = getResponse();
		OutputStream stream = null;
		String treeOrgNodeId = getRequest().getParameter("treeOrgNodeId");
		Integer selectedOrgNodeId = null;
		if(treeOrgNodeId != null)
			selectedOrgNodeId = Integer.parseInt(treeOrgNodeId);
		String json = "";
		try {
			System.out.println ("db process time Start:"+new Date());
			CustomerLicense[] customerLicenses =  null;
			if(this.hasLicenseConfig) {
			 	//Temporary change to hide register student button
				// customerLicenses = getCustomerLicenses();  
			}
	    	// retrieve information for user test sessions
	        //  FilterParams sessionFilter = FilterSortPageUtils.buildFilterParams(FilterSortPageUtils.TESTSESSION_DEFAULT_FILTER_COLUMN, "CU");
	    	FilterParams sessionFilter = null;
	        PageParams sessionPage = null;
	        SortParams sessionSort = null;
	        sessionSort = FilterSortPageUtils.buildSortParams(FilterSortPageUtils.TESTSESSION_DEFAULT_SORT, FilterSortPageUtils.ASCENDING);
	        TestSessionData tsd = getTestSessionsForOrgNode(selectedOrgNodeId, sessionFilter, sessionPage, sessionSort, this.user.getUserId());
	        System.out.println ("db process time End:"+new Date());
	        Base base = new Base();
			base.setPage("1");
			base.setRecords("10");
			base.setTotal("2");
			if ((tsd != null) && (tsd.getFilteredCount().intValue() > 0))
			{
				System.out.println ("List process time Start:"+new Date());
				base = buildTestSessionList(customerLicenses, tsd, base); 
				//String userOrgCategoryName = getTestSessionOrgCategoryName(sessionList);
				System.out.println ("List process time End:"+new Date());
			} else {
				this.setSessionListCUFU(new ArrayList<TestSessionVO>());
		        this.setSessionListPA(new ArrayList<TestSessionVO>());
		        this.setSessionListCUFUMap(new HashMap<Integer, Map>());
		        this.setSessionListPAMap(new HashMap<Integer, Map>());
		        base.setTestSessionCUFU(sessionListCUFU);
		        base.setTestSessionPA(sessionListPA);
		        base.setSessionListCUFUMap(sessionListCUFUMap);
		        base.setSessionListPAMap(sessionListPAMap);
			}
			
			
			//System.out.println("just b4 gson");	
			Gson gson = new Gson();
			System.out.println ("Json process time Start:"+new Date());
			
			json = gson.toJson(base);
			//System.out.println ("Json process time End:"+new Date() +".."+json);


			
			try{
				resp.setContentType("application/json");
				stream = resp.getOutputStream();
				resp.flushBuffer();
				stream.write(json.getBytes("UTF-8"));

			}

			finally{
				if (stream!=null){
					stream.close();
				}
			}



		} catch (Exception e) {
			System.err.println("Exception while processing CR response.");
			e.printStackTrace();
		}

		return null;

	}
    
    @Jpf.Action()
	protected Forward assessments_studentScoring()
	{
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {
        	if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/ScoringWeb/studentScoringOperation/beginStudentScoring.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/ScoringWeb/studentScoringOperation/beginStudentScoring.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/ScoringWeb/studentScoringOperation/beginStudentScoring.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
	            String url = "/ScoringWeb/studentScoringOperation/beginStudentScoring.do";
	            getResponse().sendRedirect(url);
	    	}
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
	}
    
    @Jpf.Action()
    protected Forward assessments_programStatus()
    {
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {
        	if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/SessionWeb/programOperation/assessments_programStatus.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/SessionWeb/programOperation/assessments_programStatus.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/SessionWeb/programOperation/assessments_programStatus.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
	            String url = "/SessionWeb/programOperation/assessments_programStatus.do";
	            getResponse().sendRedirect(url);
	    	}
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
    }
    /**
     * STUDENT REGISTRATION actions
     */
    @Jpf.Action()
    protected Forward assessments_studentRegistrationLink()
    {
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {
        	if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/RegistrationWeb/registrationOperation/beginStudentRegistration.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/RegistrationWeb/registrationOperation/beginStudentRegistration.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/RegistrationWeb/registrationOperation/beginStudentRegistration.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
	        	String url = "/RegistrationWeb/registrationOperation/beginStudentRegistration.do";
	        	getResponse().sendRedirect(url);
	    	}
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
    }
    
    
	/**
	 * ORGANIZATIONS actions
	 */
    @Jpf.Action(forwards = { 
	        @Jpf.Forward(name = "studentsLink", path = "organizations_manageStudents.do"),
	        @Jpf.Forward(name = "usersLink", path = "organizations_manageUsers.do"),
	        @Jpf.Forward(name = "organizationsLink", path = "organizations_manageOrganizations.do"),
	        @Jpf.Forward(name = "bulkAccomLink", path = "organizations_manageBulkAccommodation.do"),
	        @Jpf.Forward(name = "bulkMoveLink", path = "organizations_manageBulkMove.do"),
	        @Jpf.Forward(name = "OOSLink", path = "organizations_manageOutOfSchool.do")
	    }) 
	protected Forward organizations()
	{
		String menuId = (String)this.getRequest().getParameter("menuId");    	
		String forwardName = (menuId != null) ? menuId : "studentsLink";
		
	    return new Forward(forwardName);
	}
	
    @Jpf.Action()
	protected Forward organizations_manageOrganizations()
	{
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {	
			if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/OrganizationWeb/orgOperation/organizations_manageOrganizations.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/OrganizationWeb/orgOperation/organizations_manageOrganizations.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/OrganizationWeb/orgOperation/organizations_manageOrganizations.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
	            String url = "/OrganizationWeb/orgOperation/organizations_manageOrganizations.do";
	            getResponse().sendRedirect(url);
	    	}
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
	}
	
    @Jpf.Action()
	protected Forward organizations_manageStudents()
	{
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {	
			if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/StudentWeb/studentOperation/organizations_manageStudents.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/StudentWeb/studentOperation/organizations_manageStudents.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/StudentWeb/studentOperation/organizations_manageStudents.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
	            String url = "/StudentWeb/studentOperation/organizations_manageStudents.do";
	            getResponse().sendRedirect(url);
	    	}
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
	}
	
    @Jpf.Action()
	protected Forward organizations_manageBulkAccommodation()
	{
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {	
			if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/StudentWeb/bulkOperation/organizations_manageBulkAccommodation.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/StudentWeb/bulkOperation/organizations_manageBulkAccommodation.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/StudentWeb/bulkOperation/organizations_manageBulkAccommodation.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
	            String url = "/StudentWeb/bulkOperation/organizations_manageBulkAccommodation.do";
	            getResponse().sendRedirect(url);
	    	}
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
	}
    
    @Jpf.Action() 
	protected Forward organizations_manageUsers()
	{
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {	
			if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/UserWeb/userOperation/organizations_manageUsers.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/UserWeb/userOperation/organizations_manageUsers.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/UserWeb/userOperation/organizations_manageUsers.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
		        String url = "/UserWeb/userOperation/organizations_manageUsers.do";
		        getResponse().sendRedirect(url);
		    }
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
		
	}

    @Jpf.Action()
	protected Forward organizations_manageBulkMove()
	{
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {	
			if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/StudentWeb/bulkMoveOperation/organizations_manageBulkMove.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/StudentWeb/bulkMoveOperation/organizations_manageBulkMove.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/StudentWeb/bulkMoveOperation/organizations_manageBulkMove.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
	            String url = "/StudentWeb/bulkMoveOperation/organizations_manageBulkMove.do";
	            getResponse().sendRedirect(url);
	    	}
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
	}
    
    @Jpf.Action()
	protected Forward organizations_manageOutOfSchool()
	{
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {	
			if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/StudentWeb/bulkMoveOperation/organizations_manageBulkMove.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/StudentWeb/bulkMoveOperation/organizations_manageBulkMove.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/StudentWeb/bulkMoveOperation/organizations_manageBulkMove.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
	            String url = "/StudentWeb/outOfSchoolOperation/organizations_manageOutOfSchool.do";
	            getResponse().sendRedirect(url);
	    	}
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
	}

    
    /**
     * REPORTS actions
     */    
    @Jpf.Action(forwards = { 
            @Jpf.Forward(name = "turnLeafReport", path = "turnLeafReport.do"),             
            @Jpf.Forward(name = "lasLinksReport", path = "lasLinksReport.do"),             
            @Jpf.Forward(name = "prismReport", path = "prismReport.do")             
        }) 
    protected Forward reports() throws CTBBusinessException
    {	
    	if(getSession().getAttribute("isEOIUser") != null)
			this.isEOIUser = new Boolean(getSession().getAttribute("isEOIUser").toString()).booleanValue();
		else
			this.isEOIUser = this.userManagement.isOKEOIUser(getRequest().getUserPrincipal().toString()); //need to check and populate this flag

		if(getSession().getAttribute("isMappedWith3_8User") != null)
			this.isMappedWith3_8User = new Boolean(getSession().getAttribute("isMappedWith3_8User").toString()).booleanValue();
		else
			this.isMappedWith3_8User = this.userManagement.isMappedWith3_8User(getRequest().getUserPrincipal().toString()); //need to check and populate this flag
		
    	getLoggedInUserPrincipal();
		
		getUserDetails();
		CustomerConfiguration [] customerConfigs = getCustomerConfigurations(this.customerId);
		setupUserPermission(customerConfigs);
		
		if (isTASCCustomer(customerConfigs)) 		
			return new Forward("prismReport");
		else
		if (isLaslinkCustomer(customerConfigs)) 		
			return new Forward("lasLinksReport");
		else
			return new Forward("turnLeafReport");
    }

    @Jpf.Action(forwards = { 
            @Jpf.Forward(name = "success", path = "prism_report_home.jsp") 
        })
    protected Forward prismReport()
    {
        if (this.reportManager == null)
        {
        	initReportManager(true);
        }
    	
        String userOrgIndex = this.getRequest().getParameter("userOrgIndex");
        try{int iuserorgIndex = Integer.parseInt(userOrgIndex);} catch (Exception e){userOrgIndex=null;}
        if (userOrgIndex != null && userOrgIndex.length()>0)
        {
        	this.reportManager.setSelectedOrganization(userOrgIndex);
        }
        else
        	userOrgIndex = "0";
        
        Integer programId = this.reportManager.getSelectedProgramId();
        Integer orgNodeId = this.reportManager.getSelectedOrganizationId();

        List reportList = buildTASCReportList(orgNodeId, programId);
        
        //**[IAA] Proctor users should not see PRISM reports
        if (isProctorUser())
        {
        	for (int i=0; i < reportList.size(); i++) {
        		reportList.remove(i);
        	}
        }
        
        String requestParam = "";
        for (int i=0; i < reportList.size(); i++) {
            CustomerReport cr = (CustomerReport)reportList.get(i);
            if ("Prism".equalsIgnoreCase(cr.getReportName())) {                
                //[IAA]: process SSO and pass correct parameters to PRISM
            	//Story: TASC - 2013 Op - 07 - SSO to Prism parameters (frontend)
            	if (i==0)
            	{
            		HMACQueryStringEncrypter HMACEncrypter = new HMACQueryStringEncrypter(this.user, this.orgNode, cr.getCustomerKey(), orgNodeId);
                	requestParam = HMACEncrypter.encrypt();
                	System.out.println("SSOparams=" + requestParam);
            	}
            	String reportUrl = cr.getReportUrl()+(cr.getReportUrl().endsWith("?")?"":"?")+requestParam;
            	cr.setReportUrl(reportUrl);
            	//String encodedReportURL = URLEncoder.encode(reportUrl);
            	//cr.setReportUrl("/SessionWeb/sessionOperation/prism_report.jsp?rpt="+encodedReportURL);
            }
        }
        
        this.getRequest().setAttribute("reportList", reportList);
        
        this.getRequest().setAttribute("programList", this.reportManager.getProgramNames());
        this.getRequest().setAttribute("program", this.reportManager.getSelectedProgramName());

        this.getRequest().setAttribute("organizationList", this.reportManager.getOrganizationNames());
        this.getRequest().setAttribute("organization", this.reportManager.getSelectedOrganizationName());

        this.getRequest().setAttribute("multipleProgram", this.reportManager.isMultiplePrograms());
        this.getRequest().setAttribute("multipleOrganizations", this.reportManager.isMultipleOrganizations());
        this.getRequest().setAttribute("singleProgOrg", this.reportManager.isSingleProgramAndOrganization());
        
        return new Forward("success");
    }
    
     
    @Jpf.Action(forwards = { 
            @Jpf.Forward(name = "home",
                         path = "turnleaf_report_home.jsp"), 
            @Jpf.Forward(name = "report",
                         path = "turnleaf_reports.jsp")
        })
    protected Forward turnLeafReport()
    {
        if (this.reportManager == null)
        {
        	initReportManager();
        }
        
        String selectedReport = (String)this.getRequest().getParameter("report");
        
        Integer programId = this.reportManager.getSelectedProgramId();
        Integer orgNodeId = this.reportManager.getSelectedOrganizationId();

        List reportList = buildReportList(orgNodeId, programId);
        String reportUrl = buildReportUrl(selectedReport, reportList);        
        
        
        this.getRequest().setAttribute("reportList", reportList);
        this.getRequest().setAttribute("selectedReport", selectedReport);
        this.getRequest().setAttribute("reportUrl", reportUrl);

        this.getRequest().setAttribute("programList", this.reportManager.getProgramNames());
        this.getRequest().setAttribute("program", this.reportManager.getSelectedProgramName());

        this.getRequest().setAttribute("organizationList", this.reportManager.getOrganizationNames());
        this.getRequest().setAttribute("organization", this.reportManager.getSelectedOrganizationName());

        this.getRequest().setAttribute("multipleProgram", this.reportManager.isMultiplePrograms());
        this.getRequest().setAttribute("multipleOrganizations", this.reportManager.isMultipleOrganizations());
        this.getRequest().setAttribute("singleProgOrg", this.reportManager.isSingleProgramAndOrganization());

        if (selectedReport == null) 
            return new Forward("home");
        else 
            return new Forward("report");
    }

    @Jpf.Action(forwards = { 
            @Jpf.Forward(name = "success", path = "immediate_report_home.jsp") 
        })
    protected Forward lasLinksReport()
    {
        if (this.reportManager == null)
        {
        	initReportManager();
        }
    	
        Integer programId = this.reportManager.getSelectedProgramId();
        Integer orgNodeId = this.reportManager.getSelectedOrganizationId();

        List reportList = buildReportList(orgNodeId, programId);

        for (int i=0; i < reportList.size(); i++) {
            CustomerReport cr = (CustomerReport)reportList.get(i);
            if ("ImmediateScores".equalsIgnoreCase(cr.getReportName())) {
            	String reportUrl = "/ImmediateReportingWeb/immediateReportingOperation/ImmediateReportingOperationController.jpf?productId="+cr.getProductId();
            	cr.setReportUrl(reportUrl);
            }
            if ("GroupImmediateScores".equalsIgnoreCase(cr.getReportName())) {
            	String reportUrl = "/ImmediateReportingWeb/immediateReportingOperation/groupImmediateReporting.do?productId="+cr.getProductId();
            	cr.setReportUrl(reportUrl);
            }
            if ("AcademicScores".equalsIgnoreCase(cr.getReportName())) {
            	String AcademicScores = "/ImmediateReportingWeb/immediateReportingOperation/academicScoresReport.do?productId="+cr.getProductId();
            	cr.setReportUrl(AcademicScores);
            	System.out.println("Academic -->>" + cr.getProductId());
            }
        }
        
        this.getRequest().setAttribute("reportList", reportList);
        
        this.getRequest().setAttribute("programList", this.reportManager.getProgramNames());
        this.getRequest().setAttribute("program", this.reportManager.getSelectedProgramName());

        this.getRequest().setAttribute("organizationList", this.reportManager.getOrganizationNames());
        this.getRequest().setAttribute("organization", this.reportManager.getSelectedOrganizationName());

        this.getRequest().setAttribute("multipleProgram", this.reportManager.isMultiplePrograms());
        this.getRequest().setAttribute("multipleOrganizations", this.reportManager.isMultipleOrganizations());
        this.getRequest().setAttribute("singleProgOrg", this.reportManager.isSingleProgramAndOrganization());
        
        return new Forward("success");
    }

    private void initReportManager()
    {
    	initReportManager(false);
    }
    
    private void initReportManager(Boolean isPrismReport)
    {
        try
        {            
            SortParams orgNodeNameSort = FilterSortPageUtils.buildSortParams("OrgNodeName", "asc");                        
            this.userTopNodes = this.testSessionStatus.getTopUserNodesForUser(this.userName, null, null, null, orgNodeNameSort);
            
            //** For users associated with multiple nodes:
            //-	OAS will pick top node
            //-	If multi top nodes, OAS UI will allow to pick one out of many (dropdown)

            if (isPrismReport)
            {
            	int topNodeIndex=0;
            	int OrgNodeCategoryId = 0;
            	boolean multipleDifferentUserOrg = false;
            	UserNode[] userNodes = this.userTopNodes.getUserNodes();                   
                for (int i=0 ; i<userNodes.length ; i++) {
                    UserNode userNode = userNodes[i];
                    if (i==0)
                    {
                    	OrgNodeCategoryId = userNode.getOrgNodeCategoryId();
                    }
                    if (userNode.getOrgNodeCategoryId().compareTo(OrgNodeCategoryId) != 0)
                    {
                    	multipleDifferentUserOrg = true;
                    }
                    
                    if (userNode.getOrgNodeCategoryId() < OrgNodeCategoryId)
            		{        			
            			OrgNodeCategoryId = userNode.getOrgNodeCategoryId();
            			topNodeIndex = i;
            		}
                }
                
                if (multipleDifferentUserOrg)
                {
	                UserNode[] newUserNodes = new UserNode[1];
	                for (int i=0 ; i<userNodes.length ; i++) {
	                    UserNode userNode = userNodes[i];
	                    if (i==topNodeIndex)
	            		{        			
	            			newUserNodes[0] = userNodes[i];
	            		}
	                }
	                this.userTopNodes.setUserNodes(newUserNodes, null);
                }
            }
            
            String[] sortNames = new String[2];
            sortNames[0] = "ProductId"; 
            sortNames[1] = "ProgramStartDate"; 
            String[] sortOrderBys = new String[2];
            sortOrderBys[0] = "asc";
            sortOrderBys[1] = "desc";
            SortParams programNameSort = FilterSortPageUtils.buildSortParams(sortNames, sortOrderBys);    
                               
            this.userPrograms = testSessionStatus.getProgramsForUser(this.userName, null, null, programNameSort);
            
            this.reportManager = new ReportManager();
            this.reportManager.initPrograms(this.userPrograms);
            this.reportManager.initOrganizations(this.userTopNodes);    
        	
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }
    }
    
    /**
     * buildTASCReportList
     */
    private List buildTASCReportList(Integer orgNodeId, Integer programId)
    {
        this.customerReportData = getTASCReportData(orgNodeId, programId);
        
        List reportList = new ArrayList();
        CustomerReport[] crs = this.customerReportData.getCustomerReports();
               
        for (int i=0; i < crs.length; i++)
        {                
            CustomerReport cr = crs[i];
       		reportList.add(cr);
        }           
              
        return reportList; 
    }
    
    /**
     * buildReportList
     */
    private List buildReportList(Integer orgNodeId, Integer programId)
    {
    	
        this.customerReportData = getCustomerReportData(orgNodeId, programId);
        
        List reportList = new ArrayList();
        CustomerReport[] crs = this.customerReportData.getCustomerReports();
        
        boolean isTABEAdaptive = false;
    	CustomerReport ExportIndividualStudentResults = null;
    	CustomerReport GroupList = null;
    	CustomerReport IndividualPortfolio = null;
        
        for (int i=0; i < crs.length; i++)
        {                
            CustomerReport cr = crs[i];
            if (cr.getProductId().intValue() == 8000)
            	isTABEAdaptive = true;
            
            String reportUrl = cr.getReportUrl();
            if (reportUrl.indexOf("http:") == 0) {
            	reportUrl = reportUrl.replaceAll("http:", "https:");
            	cr.setReportUrl(reportUrl);
            }
            if (! cr.getReportName().equals("IndividualProfile")) {
        		reportList.add(cr);
            }
            if (cr.getReportName().equals("ExportIndividualStudentResults")) {
            	ExportIndividualStudentResults = cr;
            }
            if (cr.getReportName().equals("GroupList")) {
            	GroupList = cr;
            }
            if (cr.getReportName().equals("IndividualPortfolio")) {
            	IndividualPortfolio = cr;
            }
        }           
              
        if (isTABEAdaptive) {
            reportList = new ArrayList();
            if (IndividualPortfolio != null)
            	reportList.add(IndividualPortfolio);
            if (GroupList != null)
            	reportList.add(GroupList);            
            if (ExportIndividualStudentResults != null)
            	reportList.add(ExportIndividualStudentResults);
        }
        
        return reportList; 
    }

    /**
     * buildReportUrl
     */
    private String buildReportUrl(String reportName, List reportList)
    {
        String reportUrl = null;
        if (reportName != null)
        {        
            for (int i=0; i < reportList.size(); i++)
            {                
                CustomerReport cr = (CustomerReport)reportList.get(i);
                if (cr.getReportName().equals(reportName))
                {
                    reportUrl = cr.getReportUrl();
                }
            }
        }                    
        
        return reportUrl; 
    }
    
    /**
     * @jpf:action
     * @jpf:forward name="success" path="turnleaf_report_list.jsp"
     */
    @Jpf.Action(forwards = { 
        @Jpf.Forward(name = "success",
                     path = "turnleaf_report_list.jsp")
    })
    protected Forward getReportList()
    {
        String programIndex = this.getRequest().getHeader("programIndex");        
        String organizationIndex = this.getRequest().getHeader("organizationIndex");
        
        Integer programId = this.reportManager.setSelectedProgram(programIndex);
        Integer orgNodeId = this.reportManager.setSelectedOrganization(organizationIndex);
        
        System.out.println("programId=" + programId + " - orgNodeId=" + orgNodeId);
        
        List reportList = buildReportList(orgNodeId, programId);

        this.getRequest().setAttribute("reportList", reportList);

        return new Forward("success");
    }
    
    private CustomerReportData getTASCReportData(Integer orgNodeId, Integer programId) 
    {
        if (orgNodeId == null)
        {
            orgNodeId = this.reportManager.setSelectedOrganization(null);
        }
        if (programId == null)
        {
            programId = this.reportManager.setSelectedProgram(null);
        }
        
        CustomerReportData crd = null;
        try
        {      
            SortParams sort = FilterSortPageUtils.buildSortParams("DisplayName", "asc");            
            crd = this.testSessionStatus.getTASCReportData(this.userName, orgNodeId, programId, null, null, sort);
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }
        return crd;
    }

    private CustomerReportData getCustomerReportData(Integer orgNodeId, Integer programId) 
    {
        if (orgNodeId == null)
        {
            orgNodeId = this.reportManager.setSelectedOrganization(null);
        }
        if (programId == null)
        {
            programId = this.reportManager.setSelectedProgram(null);
        }
        
        CustomerReportData crd = null;
        try
        {      
            SortParams sort = FilterSortPageUtils.buildSortParams("DisplayName", "asc");            
            crd = this.testSessionStatus.getCustomerReportData(this.userName, orgNodeId, programId, null, null, null);
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }
        return crd;
    }
    
    /**
     * SERVICES actions
     */
    @Jpf.Action(forwards = { 
	        @Jpf.Forward(name = "resetTestSessionLink", path = "services_resetTestSession.do"),
	        @Jpf.Forward(name = "manageLicensesLink", path = "services_manageLicenses.do"),
	        @Jpf.Forward(name = "installSoftwareLink", path = "services_installSoftware.do"),
	        @Jpf.Forward(name = "downloadTestLink", path = "services_downloadTest.do"),
	        @Jpf.Forward(name = "uploadDataLink", path = "services_uploadData.do"),
	        @Jpf.Forward(name = "downloadDataLink", path = "services_downloadData.do"),
	        @Jpf.Forward(name = "exportDataLink", path = "services_dataExport.do"),
	        @Jpf.Forward(name = "viewStatusLink", path = "services_viewStatus.do"),
	        //@Jpf.Forward(name = "uploadPrescriptionDataLink", path = "services_uploadPrescriptionData.do"),
	        @Jpf.Forward(name = "showAccountFileDownloadLink", path = "eMetric_user_accounts_detail.do")
	        
	    }) 
	protected Forward services()
	{
		String menuId = (String)this.getRequest().getParameter("menuId");    	
		String forwardName = (menuId != null) ? menuId : "installSoftwareLink";
		
	    return new Forward(forwardName);
	}
    
    /*
    @Jpf.Action()
    protected Forward services_uploadPrescriptionData()
    {
    	try
    	{
    		String url = "/OrganizationWeb/uploadPrescriptionOperation/services_uploadPrescriptionData.do";
    		getResponse().sendRedirect(url);
    	}
    	catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
    	return null;
    }
    */
    
    @Jpf.Action()
    protected Forward services_dataExport()
    {
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {	
			if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/ExportWeb/dataExportOperation/services_dataExport.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/ExportWeb/dataExportOperation/services_dataExport.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/ExportWeb/dataExportOperation/services_dataExport.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
	    		String url = "/ExportWeb/dataExportOperation/services_dataExport.do";
	    		getResponse().sendRedirect(url);
	    	}
    	}
    	catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
    	return null;
    }
	
    @Jpf.Action()
    protected Forward services_resetTestSession()
    {
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {	
			if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/OrganizationWeb/resetOperation/services_resetTestSession.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/OrganizationWeb/resetOperation/services_resetTestSession.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/OrganizationWeb/resetOperation/services_resetTestSession.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
	            String url = "/OrganizationWeb/resetOperation/services_resetTestSession.do";
	            getResponse().sendRedirect(url);
	    	}
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
    }
    
    @Jpf.Action()
    protected Forward services_manageLicenses()
    {
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {	
			if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/OrganizationWeb/licenseOperation/services_manageLicenses.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/OrganizationWeb/licenseOperation/services_manageLicenses.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/OrganizationWeb/licenseOperation/services_manageLicenses.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
	            String url = "/OrganizationWeb/licenseOperation/services_manageLicenses.do";
	            getResponse().sendRedirect(url);
	    	}
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
    }
	
	@Jpf.Action()
	protected Forward services_installSoftware()
	{
		this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {	
			if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/SessionWeb/softwareOperation/begin.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/SessionWeb/softwareOperation/begin.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/SessionWeb/softwareOperation/begin.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
	            String url = "/SessionWeb/softwareOperation/begin.do";
	            getResponse().sendRedirect(url);
	    	}
	        } 
	        catch (IOException ioe)
	        {
	            System.err.print(ioe.getStackTrace());
	        }
	        return null;
	}
	
    @Jpf.Action()
	protected Forward services_downloadTest()
	{
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {	
			if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/SessionWeb/testContentOperation/services_downloadTest.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/SessionWeb/testContentOperation/services_downloadTest.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/SessionWeb/testContentOperation/services_downloadTest.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
	            String url = "/SessionWeb/testContentOperation/services_downloadTest.do";
	            getResponse().sendRedirect(url);
	    	}
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
	}
	
    @Jpf.Action()
	protected Forward services_uploadData()
	{
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {	
			if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/OrganizationWeb/uploadOperation/services_uploadData.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/OrganizationWeb/uploadOperation/services_uploadData.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/OrganizationWeb/uploadOperation/services_uploadData.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
	            String url = "/OrganizationWeb/uploadOperation/services_uploadData.do";
	            getResponse().sendRedirect(url);
	    	}
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
	}
	
    @Jpf.Action()
	protected Forward services_downloadData()
	{
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {	
			if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/OrganizationWeb/downloadOperation/services_downloadData.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/OrganizationWeb/downloadOperation/services_downloadData.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/OrganizationWeb/downloadOperation/services_downloadData.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
	            String url = "/OrganizationWeb/downloadOperation/services_downloadData.do";
	            getResponse().sendRedirect(url);
	    	}
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
	}
    
    
  
    @Jpf.Action()
	protected Forward services_viewStatus()
	{
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {	
			if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/ExportWeb/dataExportOperation/beginViewStatus.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/ExportWeb/dataExportOperation/beginViewStatus.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/ExportWeb/dataExportOperation/beginViewStatus.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
	            String url = "/ExportWeb/dataExportOperation/beginViewStatus.do";
	            getResponse().sendRedirect(url);
	    	}
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
	}
    @Jpf.Action()
	protected Forward eMetric_user_accounts_detail()
	{
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {	
			if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/SessionWeb/userAccountFileOperation/accountFiles.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/SessionWeb/userAccountFileOperation/accountFiles.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/SessionWeb/userAccountFileOperation/accountFiles.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
	            String url = "/SessionWeb/userAccountFileOperation/accountFiles.do";
	            getResponse().sendRedirect(url);
	    	}
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
	}   
    
    
    
	@Jpf.Action()
    protected Forward broadcastMessage() throws CTBBusinessException
    {
        HttpServletRequest req = getRequest();
		HttpServletResponse resp = getResponse();
		OutputStream stream = null;
		
		if(getSession().getAttribute("isEOIUser") != null)
			this.isEOIUser = new Boolean(getSession().getAttribute("isEOIUser").toString()).booleanValue();
		else
			this.isEOIUser = this.userManagement.isOKEOIUser(getRequest().getUserPrincipal().toString()); //need to check and populate this flag

		if(getSession().getAttribute("isMappedWith3_8User") != null)
			this.isMappedWith3_8User = new Boolean(getSession().getAttribute("isMappedWith3_8User").toString()).booleanValue();
		else
			this.isMappedWith3_8User = this.userManagement.isMappedWith3_8User(getRequest().getUserPrincipal().toString()); //need to check and populate this flag
						
		if (this.userName == null || (this.isEOIUser && this.isMappedWith3_8User)) {
			getLoggedInUserPrincipal();
			this.userName = (String)getSession().getAttribute("userName");
		}
		
		List broadcastMessages = BroadcastUtils.getBroadcastMessages(this.message, this.userName);
        String bcmString = BroadcastUtils.buildBroadcastMessages(broadcastMessages);
		
		try{
    		resp.setContentType(CONTENT_TYPE_JSON);
			try {
				stream = resp.getOutputStream();
	    		resp.flushBuffer();
	    		stream.write(bcmString.getBytes());
			} 
			finally {
				if (stream!=null){
					stream.close();
				}
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
        
        return null;
    }

    @Jpf.Action()
    protected Forward myProfile()
    {
        return null;
    }
    
    /**
     * STUDENT SCORING actions
     */    
    @Jpf.Action(forwards = { 
            @Jpf.Forward(name = "studentScoringLink", path = "scoring_studentScoring.do")
        }) 
    protected Forward studentScoring()
    {
    	String menuId = (String)this.getRequest().getParameter("menuId");    	
    	String forwardName = (menuId != null) ? menuId : "studentScoringLink";
    	
        return new Forward(forwardName);
    }
    
    @Jpf.Action()
	protected Forward scoring_studentScoring()
	{
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	this.isEOISelected = (getSession().getAttribute("isEOISelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isEOISelected").toString()))? true: false;
    	this.isUserLinkSelected = (getSession().getAttribute("isUserLinkSelected") != null && "true".equalsIgnoreCase(getSession().getAttribute("isUserLinkSelected").toString()))? true: false;
		try
        {	
			if(this.isEOIUser && this.isMappedWith3_8User && this.is3to8Selected){
	        	String url = "/ScoringWeb/studentScoringOperation/beginStudentScoring.do?is3to8Selected="+this.is3to8Selected;
	        	getResponse().sendRedirect(url);
	        }else if(this.isEOIUser && this.isMappedWith3_8User && this.isEOISelected){
	    		String url = "/ScoringWeb/studentScoringOperation/beginStudentScoring.do?isEOISelected="+this.isEOISelected;
	    		getResponse().sendRedirect(url);
	    	}else if(this.isEOIUser && this.isMappedWith3_8User && this.isUserLinkSelected){
	    		String url = "/ScoringWeb/studentScoringOperation/beginStudentScoring.do?isUserLinkSelected="+this.isUserLinkSelected;
	    		getResponse().sendRedirect(url);
	    	}else{
	            String url = "/ScoringWeb/studentScoringOperation/beginStudentScoring.do";
	            getResponse().sendRedirect(url);
	    	}
        } 
        catch (IOException ioe)
        {
            System.err.print(ioe.getStackTrace());
        }
        return null;
	}
    
    
    /**
	 * This method checks whether customer is configured to display access code in individual 
	 * and multiple testTicket or not.
	 * @return Return Boolean 
	 */
	
	
	private Boolean customerHasAccessCode(Integer testAdminId)
    {               
		Integer customerId = this.user.getCustomer().getCustomerId();
        boolean hasAccessCodeConfigurable = false;
        String hasBreak = "T";
        try
        {  
        	hasBreak = users.hasMultipleAccessCode(testAdminId);
        	
			/*CustomerConfiguration [] customerConfigurations = users.getCustomerConfigurations(customerId.intValue());
			if (customerConfigurations == null || customerConfigurations.length == 0) {
				customerConfigurations = users.getCustomerConfigurations(2);
			}*/
	
	
	        for (int i=0; i < this.customerConfigurations.length; i++)
	        {
	        	CustomerConfiguration cc = (CustomerConfiguration)this.customerConfigurations[i];
	            if (cc.getCustomerConfigurationName().equalsIgnoreCase("Allow_Print_Accesscode") && 
	            		cc.getDefaultValue().equals("T")	) {
	            	hasAccessCodeConfigurable = true;
	                break;
	            } 
	        }
       }
        
        catch (SQLException se) {
        	se.printStackTrace();
		}
       if(hasBreak.equals("F") && hasAccessCodeConfigurable)
    	   return true;
       else
    	   return false;
    }
	
	
	private boolean customerHasMultipleAccessCode(){               
		Integer customerId = this.user.getCustomer().getCustomerId();
        boolean hasMultipleAccessCodeConfigurable = false;        
    	if ((this.customerConfigurations.length == 0) || ( null == this.customerConfigurations))
    		this.customerConfigurations = getCustomerConfigurations(customerId);
    	
    	for (int i=0; i < this.customerConfigurations.length; i++){
        	CustomerConfiguration cc = (CustomerConfiguration)this.customerConfigurations[i];
            if (cc.getCustomerConfigurationName().equalsIgnoreCase("Allow_Print_Multiple_Accesscode") && 
            		cc.getDefaultValue().equals("T")) {
            	hasMultipleAccessCodeConfigurable = true;
                break;
            } 
	    }
    	
      return hasMultipleAccessCodeConfigurable;       
    }
    
	private void buildRosterList(RosterElement[] rosterElementData, Map<Integer, Map> accomodationMap) 
    {
        //List<SessionStudent> studentList = new ArrayList<SessionStudent>();
        Map<String,String> innerMap;
        for (int i=0 ; i<rosterElementData.length; i++) {
        	innerMap = new HashMap<String,String>();
        	RosterElement ss = (RosterElement)rosterElementData[i];
            
            if (ss != null) {                
                StringBuffer buf = new StringBuffer();
                if ("T".equals(ss.getCalculator())) {
                    if ("true".equals(ss.getHasColorFontAccommodations()) ||
                        "T".equals(ss.getScreenReader()) ||
                        "T".equals(ss.getTestPause()) ||
                        "T".equals(ss.getUntimedTest()))
                        buf.append("Calculator, ");
                    else
                        buf.append("Calculator");
                }
                if(ss.getMusicFileId() == null || "".equals(ss.getMusicFileId().trim())){
                	ss.setAuditoryCalming("F");
                }else {
                	ss.setAuditoryCalming("T");
                }
                
                if ("true".equals(ss.getHasColorFontAccommodations())) {
                    if ("T".equals(ss.getScreenReader()) ||
                        "T".equals(ss.getTestPause()) ||
                        "T".equals(ss.getUntimedTest()))
                        buf.append("Color/Font, ");
                    else
                        buf.append("Color/Font");
                }
                if ("T".equals(ss.getScreenReader())) {
                    if ("T".equals(ss.getTestPause()) ||
                        "T".equals(ss.getUntimedTest()))
                        buf.append("ScreenReader, ");
                    else
                        buf.append("ScreenReader");
                }
                if ("T".equals(ss.getTestPause())) {
                    if ("T".equals(ss.getUntimedTest()))
                        buf.append("TestPause, ");
                    else
                        buf.append("TestPause");
                }
                if ("T".equals(ss.getUntimedTest())) {
                    buf.append("UntimedTest");
                }
                buf.append(".");
                ss.setHasColorFontAccommodations(getHasColorFontAccommodations(ss));
                 if(ss.getMiddleName() != null && !ss.getMiddleName().equals(""))
                	ss.setMiddleName( ss.getMiddleName().substring(0,1));
                 
                 innerMap.put("screenMagnifier", ss.getScreenMagnifier());
                 innerMap.put("screenReader", ss.getScreenReader());
                 innerMap.put("calculator", ss.getCalculator());
                 innerMap.put("testPause", ss.getTestPause());
                 innerMap.put("untimedTest", ss.getUntimedTest());
                 innerMap.put("highLighter", ss.getHighLighter());
                 innerMap.put("maskingRular", ss.getMaskingRular());
                 innerMap.put("maskingTool", ss.getMaskingTool());
                 innerMap.put("auditoryCalming", ss.getAuditoryCalming());
                 innerMap.put("magnifyingGlass", ss.getMagnifyingGlass());
                 
                 if("T".equals(ss.getExtendedTimeAccom()) || (ss.getExtendedTimeAccom() != null && !ss.getExtendedTimeAccom().equals("") && !ss.getExtendedTimeAccom().equals("F"))){
                	 innerMap.put("extendedTimeAccom","T");
            	 }else {
            		 innerMap.put("extendedTimeAccom","F");
            	 }
                 innerMap.put("hasColorFontAccommodations",getHasColorFontAccommodations(ss));
                 accomodationMap.put(ss.getStudentId(), innerMap);

            }
        }
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////    
    ///////////////////////////// SETUP USER PERMISSION ///////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////    
    private void getLoggedInUserPrincipal()
    {	
    	/* Changes for DEX Story - Add intermediate screen : Start */
    	this.is3to8Selected = (getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString()))? true: false;
    	if(this.isEOIUser && this.isMappedWith3_8User){
    		//if(getSession().getAttribute("is3to8Selected") != null && "true".equalsIgnoreCase(getSession().getAttribute("is3to8Selected").toString())){
    		if(this.is3to8Selected){
    			try {
					this.userName = this.userManagement.fetchMapped3to8User(getRequest().getUserPrincipal().toString());
				} catch (CTBBusinessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}	
    		else
    			this.userName = getRequest().getUserPrincipal().toString();//principle object will always contain EOI user
    		
    	}
    	else{
    		java.security.Principal principal = getRequest().getUserPrincipal();
            if (principal != null) {
                this.userName = principal.toString();
            } 
    	}          
        getSession().setAttribute("userName", this.userName);
    }
    
    private void getUserDetails()
    {
        Boolean supportAccommodations = Boolean.TRUE;
        //String userTimeZone = "GMT";
             	
        try
        {
        	if(this.user == null || (this.isEOIUser && this.isMappedWith3_8User)){// need to set user object again for the userName against selected administration
        		this.user = userManagement.getUser(this.userName, this.userName);
        	} 
           // this.user = this.testSessionStatus.getUserDetails(this.userName, this.userName);
            Customer customer = this.user.getCustomer();
            this.customerId = customer.getCustomerId();
            getSession().setAttribute("customerId", customerId);
            String hideAccommodations = customer.getHideAccommodations();
	        if ((hideAccommodations != null) && hideAccommodations.equalsIgnoreCase("T"))
	        {
	            supportAccommodations = Boolean.FALSE;
	        }
	        //UserNodeData associateNode = UserOrgHierarchyUtils.populateAssociateNode(this.userName,this.userManagement);
	        //ArrayList<Organization> selectedList  = UserOrgHierarchyUtils.buildassoOrgNodehierarchyList(associateNode);
	        getSession().setAttribute("supportAccommodations", supportAccommodations); 
	        getSession().setAttribute("schedulerFirstName", this.user.getFirstName());
	        getSession().setAttribute("schedulerLastName", this.user.getLastName());
	        getSession().setAttribute("schedulerUserId", this.user.getUserId().toString());
	        getSession().setAttribute("schedulerUserName", this.user.getUserName());
	        //System.out.println("supportAccommodations==>"+supportAccommodations);
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }
    }
    
    /**
     * getCustomerLicenses
     */
    @SuppressWarnings("unused")
	private CustomerLicense[] getCustomerLicenses()
    {
        CustomerLicense[] cls = null;

        try
        {
            cls = this.licensing.getCustomerOrgNodeLicenseData(this.userName, null);
        }    
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }
     
        return cls;
    }
    
    private String getCustomerLicensesModel()
    {
    	String subtestModel = "";
    	try
        {
            subtestModel = this.licensing.getCustomerOrgNodeLicenseModel(this.customerId);
        }    
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }
     
        return subtestModel;
    }
    
    private void setUpAllUserPermission(CustomerConfiguration [] customerConfigurations) {
    	
    	boolean hasBulkStudentConfigurable = false;
    	boolean hasBulkStudentMoveConfigurable = false;
    	boolean hasOOSConfigurable = false;
    	boolean laslinkCustomer = false;
    	boolean tabeCustomer = false;
    	boolean adminUser = isAdminUser();
    	boolean adminCoordinatorUser = isAdminCoordinatotUser();
    	boolean hasUploadConfig = false;
    	boolean hasDownloadConfig = false;
    	boolean hasUploadDownloadConfig = false;
    	boolean hasProgramStatusConfig = false;
    	boolean hasScoringConfigurable = false;
    	boolean hasResetTestSession = false;
    	boolean hasResetTestSessionForAdmin = false;
    	boolean isGACustomer = false;
    	boolean isTopLevelAdmin = new Boolean(isTopLevelUser() && isAdminUser());
    	boolean hasDataExportVisibilityConfig = false;
    	Integer dataExportVisibilityLevel = 1; 
    	boolean hasBlockUserManagement = false;
    	boolean hasSSOHideUserProfile = false;
    	boolean hasSSOBlockUserModifications = false;
    	
		if( customerConfigurations != null ) {
			for (int i=0; i < customerConfigurations.length; i++) {

				CustomerConfiguration cc = (CustomerConfiguration)customerConfigurations[i];
				if (cc.getCustomerConfigurationName() == null) cc.setCustomerConfigurationName("");
				if (cc.getDefaultValue() == null) cc.setDefaultValue("");
				
				// For Bulk Accommodation
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Configurable_Bulk_Accommodation") && 
						cc.getDefaultValue().equals("T")) {
					hasBulkStudentConfigurable = true;
					continue;
				}
				// For Bulk Student Move
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Bulk_Move_Students") && 
						cc.getDefaultValue().equals("T")) {
					hasBulkStudentMoveConfigurable = true;
					continue;
				}
				// For Out Of School Student
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("OOS_Configurable") && 
						cc.getDefaultValue().equals("T")) {
					hasOOSConfigurable = true;
					continue;
				}
				// For LasLink Customer
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Laslink_Customer")) {
	            	laslinkCustomer = true;
	            	isLasLinkCustomer = true;
	            	continue;
	            }
				// For TABE Customer
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("TABE_Customer")) {
	            	tabeCustomer = true;
	            	isTABECustomer = true;
	            	continue;
	            }
				// For TABE ADAPTIVE Customer
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("TABE_Adaptive_Customer")) {
	            	isTABEAdaptiveCustomer = true;
	            	continue;
	            }
				// For TERRANOVA Customer
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("TERRANOVA_Customer")) {
	            	isTERRANOVA_Customer = true;
	            	continue;
	            }
				// For Upload Download
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Allow_Upload")
						&& cc.getDefaultValue().equals("T")) {
					hasUploadConfig = true;
					continue;
	            }
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Allow_Download")
						&& cc.getDefaultValue().equals("T")) {
					hasDownloadConfig = true;
					continue;
	            }
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Allow_Upload_Download")
						&& cc.getDefaultValue().equals("T")) {
					hasUploadDownloadConfig = true;
					continue;
	            }
				// For Program Status
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Program_Status") && 
						cc.getDefaultValue().equals("T")) {
					hasProgramStatusConfig = true;
					continue;
				}
				// For Hand Scoring
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Configurable_Hand_Scoring") && 
	            		cc.getDefaultValue().equals("T")	) {
					hasScoringConfigurable = true;
					continue;
	            }
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Allow_Subscription") && 
	            		cc.getDefaultValue().equals("T")	) {
					this.hasLicenseConfig = true;
					continue;
	            }
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Allow_Reopen_Subtest") && 
	            		cc.getDefaultValue().equals("T")	) {
					hasResetTestSession = true;
					continue;
	            }
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Allow_Reopen_Subtest_For_Admin") && 
	            		cc.getDefaultValue().equals("T")	) {
					hasResetTestSessionForAdmin = true;
					continue;
	            }
				//Added for oklahoma customer
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("OK_Customer")
						&& cc.getDefaultValue().equals("T")) {
	            	this.isOKCustomer = true;
	            	continue;
	            }
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("WV_Customer")
						&& cc.getDefaultValue().equals("T")) {
	            	this.isWVCustomer = true;
	            	continue;
	            }
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("GA_Customer") 
						&& cc.getDefaultValue().equalsIgnoreCase("T")) {
					isGACustomer = true;
					continue;
				}
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Force_Test_Break") && 
	            		cc.getDefaultValue().equals("T")	) {
					this.forceTestBreak = true;
					continue;
	            }
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("GE_Selection") && 
	            		cc.getDefaultValue().equals("T")	) {
					this.selectGE = true;
					continue;
	            }
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Data_Export_Visibility")) {
					hasDataExportVisibilityConfig = true;
					dataExportVisibilityLevel = Integer.parseInt(cc.getDefaultValue());
					continue;
	            }
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("License_Yearly_Expiry")) {
	        		this.isLASManageLicense = Boolean.TRUE;
	            } 
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Block_User_Management_3to8") && 
	            		cc.getDefaultValue().equals("T")) {
	        		hasBlockUserManagement = Boolean.TRUE;
	            }
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("SSO_Hide_User_Profile") && 
	            		cc.getDefaultValue().equals("T")) {
					hasSSOHideUserProfile = Boolean.TRUE;
	            }
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("SSO_Block_User_Modifications") && 
	            		cc.getDefaultValue().equals("T")) {
					hasSSOBlockUserModifications = Boolean.TRUE;
	            }
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("ENGRADE_Customer") && 
	            		cc.getDefaultValue().equals("T")) {
	        		this.isEngradeCustomer = true;
	        		continue;
	            }
				if(cc.getCustomerConfigurationName().equalsIgnoreCase("Show_Roster_Accom_Hierarchy") && 
						cc.getDefaultValue().equals("T")) {
					this.hasShowRosterAccomAndHierarchy = Boolean.TRUE;
				}
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Default_Testing_Window_Days") && 
						null != cc.getDefaultValue() && !"0".equalsIgnoreCase(cc.getDefaultValue())){
					this.hasDefaultTestingWindowConfig = Boolean.TRUE;
				}
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Testlet_Session_End_Date") && 
						!StringUtils.isEmptyString(cc.getDefaultValue())){
					 SimpleDateFormat sdf = new SimpleDateFormat();
				        sdf.applyPattern("MM/dd/yy");
				        try{
				            sdf.parse(cc.getDefaultValue());
				            testletSessionEndDate=cc.getDefaultValue();
				        }
				        catch (Exception e){
				        	//do nothing
				        }
				}
				if(cc.getCustomerConfigurationName().equalsIgnoreCase("View_Response_Result") && 
						null != cc.getDefaultValue() && "T".equals(cc.getDefaultValue())){
					this.hasViewResponseResultConf = true;
					this.getSession().setAttribute("hasViewResponseResultConf", this.hasViewResponseResultConf);
				}
			}
			this.isTASCCustomer = isTASCCustomer(customerConfigurations);		
			this.isTASCReadinessCustomer = isTASCReadinessCustomer(customerConfigurations);
		}
		if (isWVCustomer)
		{
			if(!isWVCustomerTopLevelAdminAndAdminCO())
			{
			hasUploadConfig=false;
			hasUploadDownloadConfig=false;
			}
		}
		if (hasUploadConfig && hasDownloadConfig) {
			hasUploadDownloadConfig = true;
		}
		if (hasUploadDownloadConfig) {
			hasUploadConfig = false;
			hasDownloadConfig = false;
		}
		
		//**[IAA] Proctor users should not see PRISM reports
		boolean TASCProctor = false;
        if (isTASCCustomer(customerConfigurations) && isProctorUser())
        {
        	TASCProctor = true;
        }
        
		//this.getSession().setAttribute("showModifyManifest", new Boolean(userScheduleAndFindSessionPermission() && (tabeCustomer || laslinkCustomer)));
		this.getSession().setAttribute("showModifyManifest", new Boolean(userScheduleAndFindSessionPermission() && tabeCustomer));
		this.getSession().setAttribute("showReportTab", new Boolean((userHasReports().booleanValue()&&!TASCProctor) || laslinkCustomer));
		this.getSession().setAttribute("isBulkAccommodationConfigured",new Boolean(hasBulkStudentConfigurable));
		this.getSession().setAttribute("isBulkMoveConfigured",new Boolean(hasBulkStudentMoveConfigurable));
		this.getSession().setAttribute("isOOSConfigured",new Boolean(hasOOSConfigurable));
		this.getSession().setAttribute("tabeAdultEndDate",testletSessionEndDate);
		if(isWVCustomer)
		{
			this.getSession().setAttribute("hasUploadConfigured",new Boolean(hasUploadConfig));
			this.getSession().setAttribute("hasUploadDownloadConfigured",new Boolean(hasUploadDownloadConfig));
		}
		else
		{
			this.getSession().setAttribute("hasUploadConfigured",new Boolean(hasUploadConfig && adminUser));
			this.getSession().setAttribute("hasUploadDownloadConfigured",new Boolean(hasUploadDownloadConfig && adminUser));
		}
		this.getSession().setAttribute("hasDownloadConfigured",new Boolean(hasDownloadConfig && adminUser));
		this.getSession().setAttribute("hasProgramStatusConfigured",new Boolean(hasProgramStatusConfig && adminUser));
		this.getSession().setAttribute("hasScoringConfigured",new Boolean(hasScoringConfigurable));
		this.getSession().setAttribute("hasLicenseConfigured",new Boolean(this.hasLicenseConfig && adminUser));
		this.getSession().setAttribute("adminUser", new Boolean(adminUser));
		this.getSession().setAttribute("hasRapidRagistrationConfigured", new Boolean(tabeCustomer&&(adminUser || adminCoordinatorUser) ));
		this.getSession().setAttribute("hasResetTestSession", new Boolean((hasResetTestSession && hasResetTestSessionForAdmin) && ((isOKCustomer && isTopLevelAdmin)||(laslinkCustomer && (adminUser||adminCoordinatorUser))||(isGACustomer && adminUser)|| (this.isTASCCustomer && isTopLevelAdmin) || (this.isTASCReadinessCustomer && isTopLevelAdmin))));
		
		this.getSession().setAttribute("isTascCustomer", new Boolean(this.isTASCCustomer));
		this.getSession().setAttribute("isTASCReadinessCustomer", new Boolean(this.isTASCReadinessCustomer));
		this.getSession().setAttribute("isLasLinkCustomer", new Boolean(isLasLinkCustomer));
		this.getSession().setAttribute("isTABECustomer", new Boolean(isTABECustomer));
		
     	//this.getSession().setAttribute("showDataExportTab",laslinkCustomer);
		this.getSession().setAttribute("showDataExportTab",new Boolean((isTopLevelUser() && laslinkCustomer) || (hasDataExportVisibilityConfig && checkUserLevel(dataExportVisibilityLevel))));
		
     	//show Account file download link      	
     	this.getSession().setAttribute("isAccountFileDownloadVisible", new Boolean(laslinkCustomer && isTopLevelAdmin));
     	
     	//Done for 3to8 customer to block user module
     	this.getSession().setAttribute("hasBlockUserManagement", new Boolean(hasBlockUserManagement));
     	
     	//Done for Engrade customer to block admin users from adding/editing/deleting users
     	this.getSession().setAttribute("hasSSOHideUserProfile", new Boolean(hasSSOHideUserProfile));
     	this.getSession().setAttribute("hasSSOBlockUserModifications", new Boolean(hasSSOBlockUserModifications));
     	this.getSession().setAttribute("isEngradeCustomer", new Boolean(this.isEngradeCustomer));
    }
   
    private boolean checkUserLevel(Integer defaultVisibilityLevel){
		boolean isUserLevelMatched = false;
		try {
			isUserLevelMatched = orgnode.matchUserLevelWithDefault(this.userName, defaultVisibilityLevel);	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isUserLevelMatched;
	}
    
	private void setupUserPermission(CustomerConfiguration [] customerConfigs)
	{
        setUpAllUserPermission(customerConfigs);
        
        this.getSession().setAttribute("showStudentReportButton", isTABECustomer(customerConfigs));
       
     	this.getSession().setAttribute("userScheduleAndFindSessionPermission", userScheduleAndFindSessionPermission());   
     	
     	this.getSession().setAttribute("isDeleteSessionEnable", isDeleteSessionEnable());
     	
     	this.getSession().setAttribute("pageSize",pageSizevalue(customerConfigs));
     	
     	this.getSession().setAttribute("pageConfigPresent",pageConfigPresent(customerConfigs));
     	
     	this.getSession().setAttribute("hasBulkStateReportExport", hasBulkStateReportExport(customerConfigs));
     	
     	getConfigStudentLabel(customerConfigs);
     	
     	getStudentGrades(customerConfigs);     	
     	
     	//** Story: TASC - 2014 - View Status Page - Student# should be replaced with TASC ID.doc
     	if (this.isTASCCustomer || this.isTASCReadinessCustomer)
     		getConfigSessionStudentIdLabel(customerConfigs);
     	
		try
		{
			//** [IAA] Story: TerraNova � Program Validity Check (Defect#75548)
			//** Apply to shelf products: TABE/TABE ADAPT, TN, LAS, TASC 
			//** If program status is "IN" or it's expired (end_date<today), don't allow user to schedule new sessions.
			boolean ActiveOrExp = false;
			String expiredOrInactivePrograms = "";
			if (isTABECustomer || isTASCCustomer || isTASCReadinessCustomer || isLasLinkCustomer || isTERRANOVA_Customer)
			{
				//ActiveOrExp = this.scheduleTest.isActiveUserProgramExpired(this.customerId, new Date());
				Program [] expPrograms = this.scheduleTest.getCustomerExpiredPrograms(this.customerId, new Date());
				int expProgramCount=0;
				for (int i=0; expPrograms!=null && i<expPrograms.length;i++)
				{
					//**TABE customers with Testlet access have 2 programs
					//** Defect#80419
					if (isLasLinkCustomer || isTABECustomer)
					{
						ActiveOrExp = true;
						if (expiredOrInactivePrograms.length()>0) expiredOrInactivePrograms += ",";
						expiredOrInactivePrograms += expPrograms[i].getProductId().toString();
						expProgramCount++;
					}
					else
					{
						ActiveOrExp = true;
					}
				}
				if (isLasLinkCustomer || isTABECustomer)
				{
					if (expProgramCount >= 2) expiredOrInactivePrograms = "";
				}
				if (isTABECustomer && expProgramCount == 1)
				{
					//Program [] allPrograms = this.scheduleTest.getCustomerExpiredPrograms(this.customerId, new Date());
					int NumPrograms = 0;
					try {
			        	
						Program [] allPrograms = ReportBridge.getProgramsForUser(userName);
						NumPrograms = allPrograms.length;
			        	
			        } catch (Exception e) {}
			        //NumPrograms=2, TABE customer has testlets access, otherwise TABE only (1 program)
			        if (NumPrograms==1) expiredOrInactivePrograms = "";
				}
			}
			getSession().setAttribute("isActiveProgramExpiredOrInactive",ActiveOrExp);
			getSession().setAttribute("LLExpiredOrInactivePrograms",expiredOrInactivePrograms);
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }
   }
		
	
	private boolean isTopLevelUser(){
		boolean isUserTopLevel = false;
		try {
			isUserTopLevel = orgnode.checkTopOrgNodeUser(this.userName);	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isUserTopLevel;
	}
	
	private void getConfigStudentLabel(CustomerConfiguration[] customerConfigurations) 
	{     
		//boolean isStudentIdConfigurable = false;
		Integer configId=0;
		String []valueForStudentId = new String[8] ;
		valueForStudentId[0] = "Student ID";
		for (int i=0; i < customerConfigurations.length; i++)
		{
			CustomerConfiguration cc = (CustomerConfiguration)customerConfigurations[i];
			if (cc.getCustomerConfigurationName().equalsIgnoreCase("Configurable_Student_ID") && cc.getDefaultValue().equalsIgnoreCase("T"))
			{
				//isStudentIdConfigurable = true; 
				configId = cc.getId();
				CustomerConfigurationValue[] customerConfigurationsValue = customerConfigurationValues(configId);
				//By default there should be 3 entries for customer configurations
				valueForStudentId = new String[8];
				for(int j=0; j<customerConfigurationsValue.length; j++){
					int sortOrder = customerConfigurationsValue[j].getSortOrder();
					valueForStudentId[sortOrder-1] = customerConfigurationsValue[j].getCustomerConfigurationValue();
				}	
				valueForStudentId[0] = valueForStudentId[0]!= null ? valueForStudentId[0] : "Student ID" ;

			}

		}
		this.getSession().setAttribute("studentIdLabelName",valueForStudentId[0]);
		
	}

	/**
     * get Session "Student Test #" Label from customer_configuration.
     * for now, don't use new configuration value, use Configurable_Student_ID
     */
	private void getConfigSessionStudentIdLabel(CustomerConfiguration[] customerConfigurations) 
	{     
		//boolean isStudentIdConfigurable = false;
		Integer configId=0;
		String []valueForStudentId = new String[8] ;
		valueForStudentId[0] = "Student Test #";
		for (int i=0; i < customerConfigurations.length; i++)
		{
			CustomerConfiguration cc = (CustomerConfiguration)customerConfigurations[i];
			if (cc.getCustomerConfigurationName().equalsIgnoreCase("Configurable_Student_ID") && cc.getDefaultValue().equalsIgnoreCase("T"))
			{
				//isStudentIdConfigurable = true; 
				configId = cc.getId();
				CustomerConfigurationValue[] customerConfigurationsValue = customerConfigurationValues(configId);
				//By default there should be 3 entries for customer configurations
				valueForStudentId = new String[8];
				for(int j=0; j<customerConfigurationsValue.length; j++){
					int sortOrder = customerConfigurationsValue[j].getSortOrder();
					valueForStudentId[sortOrder-1] = customerConfigurationsValue[j].getCustomerConfigurationValue();
				}	
				valueForStudentId[0] = valueForStudentId[0]!= null ? valueForStudentId[0] : "Student Test #" ;

			}

		}
		this.getSession().setAttribute("sessionStudentIdLabelName",valueForStudentId[0]);
		
	}
	
	 /**
     * get value for enabling RegisterStudent button.
     */
    private void registerStudentEnable(CustomerLicense[] customerLicenses, TestSessionVO testSessionVo)
    {    
    	if (customerLicenses == null  || (!this.hasLicenseConfig)) {
    		testSessionVo.setIsRegisterStudentEnable("T");  
    		return;
    	}
    	if (customerLicenses != null && customerLicenses.length<=0 && this.hasLicenseConfig) {
    		 testSessionVo.setIsRegisterStudentEnable("F");   
    		 return;
    	}
    	
             boolean flag = false;
            
            if (testSessionVo.getLicenseEnabled().equals("T"))
            {
            
                for (int j=0; j < customerLicenses.length; j++)
                { 
                            
                    if (customerLicenses[j].getProductId().intValue() == testSessionVo.getProductId().intValue() || customerLicenses[j].getProductId().intValue() == testSessionVo.
                        getParentProductId().intValue())
                    {
                        flag = true;      
                       if(customerLicenses[j].isLicenseAvailable()){
                        	testSessionVo.setIsRegisterStudentEnable("T");
                        } else {
                        	 testSessionVo.setIsRegisterStudentEnable("F");
                        }
                      
                      break;
                  }
                }
            } 
            if (!flag) {
                
                testSessionVo.setIsRegisterStudentEnable("T");   
                
            }
    }

    private Boolean userHasReports() 
    {
        boolean hasReports = false;
        try
        {      
            Customer customer = this.user.getCustomer();
            Integer customerId = customer.getCustomerId();   
            hasReports = this.testSessionStatus.userHasReports(this.userName, customerId);
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }        
        return new Boolean(hasReports);         
    }

    private boolean isAdminUser() 
    {               
        String roleName = this.user.getRole().getRoleName();        
        return roleName.equalsIgnoreCase(PermissionsUtils.ROLE_NAME_ADMINISTRATOR); 
    }
    private boolean isAdminCoordinatorUser() //For Student Registration
    {               
        String roleName = this.user.getRole().getRoleName();        
        return roleName.equalsIgnoreCase(PermissionsUtils.ROLE_NAME_ACCOMMODATIONS_COORDINATOR); 
    }
    private boolean isAdminCoordinatotUser() 
    {               
        String roleName = this.user.getRole().getRoleName();        
        return roleName.equalsIgnoreCase(PermissionsUtils.ROLE_NAME_ACCOMMODATIONS_COORDINATOR); 
    }
    
    private boolean isProctorUser() 
    {               
        String roleName = this.user.getRole().getRoleName();        
        return roleName.equalsIgnoreCase(PermissionsUtils.ROLE_NAME_PROCTOR); 
    }
    
    @SuppressWarnings("unused")
	private Boolean canRegisterStudent(CustomerConfiguration [] customerConfigs) 
    {               
        String roleName = this.user.getRole().getRoleName();        
        boolean validCustomer = false; 

        for (int i=0; i < customerConfigs.length; i++)
        {
            CustomerConfiguration cc = (CustomerConfiguration)customerConfigs[i];
            if (cc.getCustomerConfigurationName().equalsIgnoreCase("TABE_Customer"))
            {
                validCustomer = true; 
            }               
        }
        
        boolean validUser = (roleName.equalsIgnoreCase(PermissionsUtils.ROLE_NAME_ADMINISTRATOR) || 
        		roleName.equalsIgnoreCase(PermissionsUtils.ROLE_NAME_ACCOMMODATIONS_COORDINATOR));
        
        return new Boolean(validCustomer && validUser);
    }
    
    /*private Boolean hasLicenseConfiguration(CustomerConfiguration [] customerConfigs)
    {               
    	 boolean hasLicenseConfiguration = false;

        for (int i=0; i < customerConfigs.length; i++)
        {
        	 CustomerConfiguration cc = (CustomerConfiguration)customerConfigs[i];
            if (cc.getCustomerConfigurationName().equalsIgnoreCase("Allow_Subscription") && 
            		cc.getDefaultValue().equals("T")	) {
            	hasLicenseConfiguration = true;
                break;
            } 
        }
       
        return new Boolean(hasLicenseConfiguration);
    }
    
    private Boolean customerHasScoring(CustomerConfiguration [] customerConfigs)
    {               
        //Integer customerId = this.user.getCustomer().getCustomerId();
        boolean hasScoringConfigurable = false;
        
        for (int i=0; i < customerConfigs.length; i++)
        {
        	 CustomerConfiguration cc = (CustomerConfiguration)customerConfigs[i];
            if (cc.getCustomerConfigurationName().equalsIgnoreCase("Configurable_Hand_Scoring") && 
            		cc.getDefaultValue().equals("T")	) {
            	hasScoringConfigurable = true;
            } 
        }
        return new Boolean(hasScoringConfigurable);
    }*/
    private boolean isTASCCustomer(CustomerConfiguration [] customerConfigs)
    {               
        boolean TASCCustomer = false;
        
        for (int i=0; i < customerConfigs.length; i++)
        {
        	 CustomerConfiguration cc = (CustomerConfiguration)customerConfigs[i];
            if (cc.getCustomerConfigurationName().equalsIgnoreCase("TASC_Customer")
					//[IAA]&& cc.getDefaultValue().equals("T")) {
            		){
            	TASCCustomer = true;
            }
        }
        return TASCCustomer;
    }
    private boolean isTASCReadinessCustomer(CustomerConfiguration [] customerConfigs)
    {               
        boolean TASCReadinessCustomer = false;
        
        for (int i=0; i < customerConfigs.length; i++)
        {
        	 CustomerConfiguration cc = (CustomerConfiguration)customerConfigs[i];
            if (cc.getCustomerConfigurationName().equalsIgnoreCase("TASCReadiness_Customer")
					//[IAA]&& cc.getDefaultValue().equals("T")) {
            		){
            	TASCReadinessCustomer = true;
            }
        }
        return TASCReadinessCustomer;
    }
    private boolean isLaslinkCustomer(CustomerConfiguration [] customerConfigs)
    {               
        boolean laslinkCustomer = false;
        
        for (int i=0; i < customerConfigs.length; i++)
        {
        	 CustomerConfiguration cc = (CustomerConfiguration)customerConfigs[i];
            if (cc.getCustomerConfigurationName().equalsIgnoreCase("Laslink_Customer")
					&& cc.getDefaultValue().equals("T")) {
            	laslinkCustomer = true;
            }
        }
        return laslinkCustomer;
    }

    @SuppressWarnings("unused")
	private boolean isTABECustomer(CustomerConfiguration [] customerConfigs)
    {               
        boolean TABECustomer = false;
        
        for (int i=0; i < customerConfigs.length; i++)
        {
        	CustomerConfiguration cc = (CustomerConfiguration)customerConfigs[i];
            if (cc.getCustomerConfigurationName().equalsIgnoreCase("TABE_Customer")) {
            	TABECustomer = true;
            }
        }
        return TABECustomer;
    }
    
    /**
	 * Bulk Accommodation
	 */
	/*private Boolean customerHasBulkAccommodation(CustomerConfiguration[] customerConfigurations) 
	{
		boolean hasBulkStudentConfigurable = false;
		if( customerConfigurations != null ) {
			for (int i=0; i < customerConfigurations.length; i++) {

				CustomerConfiguration cc = (CustomerConfiguration)customerConfigurations[i];
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Configurable_Bulk_Accommodation") && 
						cc.getDefaultValue().equals("T")) {
					hasBulkStudentConfigurable = true; 
					break;
				}
			}
		}
		return new Boolean(hasBulkStudentConfigurable);           
	}*/
	

	/**
	 * Bulk Move
	 */
	/*private Boolean customerHasBulkMove(CustomerConfiguration[] customerConfigurations) 
	{
		boolean hasBulkStudentConfigurable = false;
		if( customerConfigurations != null ) {
			for (int i=0; i < customerConfigurations.length; i++) {

				CustomerConfiguration cc = (CustomerConfiguration)customerConfigurations[i];
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Bulk_Move_Students") && 
						cc.getDefaultValue().equals("T")) {
					hasBulkStudentConfigurable = true; 
					break;
				}
			}
		}
		return new Boolean(hasBulkStudentConfigurable);           
	}*/

	// Changes for Out Of School
	/**
	 * Out Of School
	 */
	/*private Boolean customerHasOOS(CustomerConfiguration[] customerConfigurations) 
	{
		boolean hasOOSConfigurable = false;
		if( customerConfigurations != null ) {
			for (int i=0; i < customerConfigurations.length; i++) {

				CustomerConfiguration cc = (CustomerConfiguration)customerConfigurations[i];
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("OOS_Configurable") && 
						cc.getDefaultValue().equals("T")) {
					hasOOSConfigurable = true; 
					break;
				}
			}
		}
		return new Boolean(hasOOSConfigurable);           
	}*/
    
    private CustomerConfiguration [] getCustomerConfigurations(Integer customerId)
    {               
        CustomerConfiguration [] ccArray = null;
        try
        {      
            ccArray = this.testSessionStatus.getCustomerConfigurations(this.userName, customerId);       
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }        
        return ccArray;
    }
    
    /*
	 * 
	 * this method retrieve CustomerConfigurationsValue for provided customer configuration Id.
	 */
	private CustomerConfigurationValue[] customerConfigurationValues(Integer configId)
	{	
		CustomerConfigurationValue[] customerConfigurationsValue = null;
		try {
			customerConfigurationsValue = this.testSessionStatus.getCustomerConfigurationsValue(configId);

		}
		catch (CTBBusinessException be) {
			be.printStackTrace();
		}
		return customerConfigurationsValue;
	}
    
    /*private Boolean hasUploadDownloadConfig()
    {
        Boolean hasUploadDownloadConfig = Boolean.FALSE;
        try {   
            hasUploadDownloadConfig = this.testSessionStatus.hasUploadDownloadConfig(this.userName);
        } 
        catch (CTBBusinessException be) {
            be.printStackTrace();
        }
        return hasUploadDownloadConfig;
    }

    private Boolean hasProgramStatusConfig(CustomerConfiguration[] customerConfigurations)
    {	    	
    	Boolean hasProgramStatusConfig = Boolean.FALSE;
    	if( customerConfigurations != null ) {
			for (int i=0; i < customerConfigurations.length; i++) {

				CustomerConfiguration cc = (CustomerConfiguration)customerConfigurations[i];
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Program_Status") && 
						cc.getDefaultValue().equals("T")) {
					hasProgramStatusConfig = true; 
					break;
				}
			}
		}
		return new Boolean(hasProgramStatusConfig);   
    }*/
    
    @SuppressWarnings("unused")
	private TestSessionData getTestSessionsForUser(FilterParams filter, PageParams page, SortParams sort) 
    {
        TestSessionData tsd = new TestSessionData();                
        try
        {      
            tsd = this.testSessionStatus.getTestSessionsForUser(this.userName, filter, page, sort);            
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }
        return tsd;
    }
    
    private TestSessionData getTestSessionsForUserHome(FilterParams filter, PageParams page, SortParams sort) 
    {
        TestSessionData tsd = new TestSessionData();                
        try
        {      
            tsd = this.testSessionStatus.getTestSessionsForUserHome(this.userName, filter, page, sort);            
        }
        catch (CTBBusinessException be)
        {
            be.printStackTrace();
        }
        return tsd;
    }
    
    private SessionStudentData getSessionStudents(Integer orgNodeId, Integer testAdminId,Integer selectedTestId, FilterParams filter, PageParams page, SortParams sort)
    {    
        SessionStudentData sd = null;
        try {      
            sd = this.scheduleTest.getSessionStudentsMinimalInfoForOrgNode(this.userName, orgNodeId, testAdminId, selectedTestId, filter, page, sort);
        }
        catch (CTBBusinessException be) {
            be.printStackTrace();
        }
        return sd;
    }

    /*
     * Check if student has completed a TABE 9/10 subtest or has been scheudled all testlet forms for current level
     */
    private Boolean hasStudentCompletedTabe9Or10(Integer studentId, StudentTestletInfo[] sti)
    {
        try {                
            if (sti != null)
            {
            	//** Fixed defect 80268: TABE Testlet Regression:- Multiple testlet session of same subject of same level can be scheduled for the same student even though existing testlet sessions are in scheduled status
            	//** If forms has not been initialized, or when testlet subject changes: initial forms
            	if (forms==null || (forms!=null && !(sti[0].getSubject().equalsIgnoreCase(forms[0].getSubject()))))
            	{
            		forms = this.scheduleTest.getTestletLevelForms(sti[0].getSubject());
            	}
            	String testletForms = "";
            	String TABE9_10_Level = "";
            	boolean studentFound = false;
            	boolean studentTabe910Processed = false;
            	boolean testletScheduled = false;
            	for (int i=0;i<sti.length;i++)
            	{
            		if (sti[i].getStudentId().equals(studentId))
            		{
            			studentFound = true;
	            		int productId = sti[i].getProductId();
	            		//** If student did not have any scheduled TABE9/10, return false
	            		if (!studentTabe910Processed && (productId != 4009 && productId != 4010 && productId != 4011 && productId != 4012))
	            		{
	            			return false;
	            		}
	            		//** TABE 9 Online Complete Battery or TABE 10 Online Complete Battery or TABE 9 Online Survey or TABE 10 Online Survey
	            		if (!studentTabe910Processed && (productId == 4009 || productId == 4010 || productId == 4011 || productId == 4012))
	            		{
	            			studentTabe910Processed = true;
	            			TABE9_10_Level = sti[i].getItemSetLevel();
	            			//** If student has not completed TABE9/10, return false
	            			if (sti[i].getCompletionStatus().compareToIgnoreCase("CO") != 0)
	            			{
	            				return false;
	            			}
	            		}
	            		else
	            		{
	            			if (!(productId == 4009 || productId == 4010 || productId == 4011 || productId == 4012))
	            			{
	            				//** fixed defec#78752
	            				String completionStatus = sti[i].getCompletionStatus();
		            			String testletForm = sti[i].getItemSetForm();
		            			String rosterActivationStatus = sti[i].getRosterActivationStatus();
		            			//** When student moved from one level to another, then goes back to original level
		            			//** Story: TABE testlet - Inactivated testlet roster's forms handling in add student and spiraling logic
		            			//if (!(rosterActivationStatus.compareToIgnoreCase("IN") == 0 && !(sti[i].getTestCompletionStatus().compareToIgnoreCase("SC")==0 || sti[i].getTestCompletionStatus().compareToIgnoreCase("NT")==0)))
		            			//** If roster is inactive and no item in subtest has been viewed, allow student to be re-scheduled a testlet (system may reschedule the same inactive form)
		            			if (!(rosterActivationStatus.compareToIgnoreCase("IN") == 0 && (completionStatus.compareToIgnoreCase("SC") == 0 || completionStatus.compareToIgnoreCase("IN") == 0 || sti[i].getTestCompletionStatus().compareToIgnoreCase("SC")==0 || sti[i].getTestCompletionStatus().compareToIgnoreCase("NT")==0)))
		            			{
			            			if (testletForms.length()>0) testletForms += ",";
			            			testletForms += testletForm;
		            						            			
			            			//** if one of the testlets is still scheduled, don't allow student to be scheduled until testlet completed
			            			//if (completionStatus.compareToIgnoreCase("SC") == 0)
			            			if (completionStatus.compareToIgnoreCase("CO") != 0 && completionStatus.compareToIgnoreCase("NT") != 0 )
			            			{
			            				//** if one of the testlets is still scheduled, and belongs to adifferent level, enable student. This roster will be inactivated in next scheduled testlet
			            				if (doesFormBelongToLatestLevel(testletForm, TABE9_10_Level))
			            				{
			            					testletScheduled = true;
			            				}
			            			}

		            			}
		            			//** If roster is inactive and student has viewed testlet form, consider it taken and not allow same form to be scheduled.
		            			//** Defect # 79544
		            			else if (rosterActivationStatus.compareToIgnoreCase("IN") == 0 && completionStatus.compareToIgnoreCase("IN") == 0)
		            			{
		            				if (testletForms.length()>0) testletForms += ",";
			            			testletForms += testletForm;
		            			}
	            			}
	            		}
            		}
            		else
            		{
            			studentTabe910Processed = false;
            			if (studentFound) break;
            		}
            	}
            	
            	if (!studentFound || testletScheduled) return false;
            	
            	String allLevelForms = "";
            	for (int j=0;j<forms.length;j++)
            	{
            		if (forms[j].getTABELevel().compareToIgnoreCase(TABE9_10_Level)==0)
            		{
            			if (allLevelForms.length()>0) allLevelForms += ",";
            			allLevelForms += forms[j].getTestletForm();	
            		}
            	}
            	String[] allLevelFormsArr = allLevelForms.split(",");
            	boolean allFormsScheduled = true;
            	for (int f=0;f<allLevelFormsArr.length;f++)
            	{
            		String[] testletFormsArr = testletForms.split(",");
            		boolean formScheduled = false;
            		for (int k=0;k<testletFormsArr.length;k++)
            		{
            			if (allLevelFormsArr[f].compareToIgnoreCase(testletFormsArr[k]) == 0 && testletFormsArr[k].length()>0)
            			{
            				formScheduled = true;
            				break;
            			}
            		}            		
            		if (!formScheduled) 
        			{
        				allFormsScheduled = false;
        				break;
        			}
            	}
            	if (allFormsScheduled) return false;
            }
            else
            {
            	return false;
            }
        } catch (CTBBusinessException be) {
            be.printStackTrace();
        }
    	return true;
    }
    
    private Boolean doesFormBelongToLatestLevel(String testletForm, String TABE9_10_Level)
    {
    	String allLevelForms = "";
    	for (int j=0;j<forms.length;j++)
    	{
    		if (forms[j].getTABELevel().compareToIgnoreCase(TABE9_10_Level)==0)
    		{
    			if (allLevelForms.length()>0) allLevelForms += ",";
    			allLevelForms += forms[j].getTestletForm();	
    		}
    	}
    	String[] allLevelFormsArr = allLevelForms.split(",");
    	boolean isLevelForm = false;
    	for (int f=0;f<allLevelFormsArr.length;f++)
    	{
			if (allLevelFormsArr[f].compareToIgnoreCase(testletForm) == 0 && testletForm.length()>0)
			{
				isLevelForm = true;
				break;
			}
    	}
    	
    	return isLevelForm;
    }
    
    private Base buildTestSessionList(CustomerLicense[] customerLicenses, TestSessionData tsd, Base base) 
    {
        List<TestSessionVO> sessionListCUFU = new ArrayList<TestSessionVO>(); 
        List<TestSessionVO> sessionListPA = new ArrayList<TestSessionVO>();        
        TestSession[] testsessions = tsd.getTestSessions(); 
        Map<Integer,Map> sessionListCUFUMap = new HashMap<Integer, Map>();
        Map<Integer,Map> sessionListPAMap = new HashMap<Integer, Map>();
        Map infoMapCUFU = new HashMap();
        Map infoMapPA = new HashMap();
        
        for (int i=0; i < testsessions.length; i++)
        {
            TestSession ts = testsessions[i];
            if (ts != null)
            {	if (ts.getTestAdminStatus().equals("CU") ||ts.getTestAdminStatus().equals("FU") ){
            		TestSessionVO vo = new TestSessionVO(ts);
            		registerStudentEnable(customerLicenses, vo);
            		sessionListCUFU.add(vo);
            		infoMapCUFU.put("isRegisterStudentEnable", vo.getIsRegisterStudentEnable());
            		sessionListCUFUMap.put(vo.getTestAdminId(), infoMapCUFU);
            	} else {
            		TestSessionVO vo = new TestSessionVO(ts);
            		registerStudentEnable(customerLicenses, vo);
            		sessionListPA.add(vo);
            		infoMapPA.put("isRegisterStudentEnable", vo.getIsRegisterStudentEnable());
            		sessionListPAMap.put(vo.getTestAdminId(),infoMapPA);
            	}
         
                
            }
        }
        this.setSessionListCUFU(sessionListCUFU);
        this.setSessionListPA(sessionListPA);
        this.setSessionListCUFUMap(sessionListCUFUMap);
        this.setSessionListPAMap(sessionListPAMap);
        base.setTestSessionCUFU(sessionListCUFU);
        base.setTestSessionPA(sessionListPA);
        base.setSessionListCUFUMap(sessionListCUFUMap);
        base.setSessionListPAMap(sessionListPAMap);
        
        return base;
    }
    
    
    @SuppressWarnings("unused")
	private String getTestSessionOrgCategoryName(List<TestSessionVO> testSessionList)
    {
        String categoryName = "Organization";        
        if (testSessionList.size() > 0)
        {
            TestSessionVO vo = (TestSessionVO)testSessionList.get(0);
            categoryName = vo.getCreatorOrgNodeCategoryName();
            for (int i=1; i < testSessionList.size(); i++)
            {
                vo = (TestSessionVO)testSessionList.get(i);
                if (! vo.getCreatorOrgNodeCategoryName().equals(categoryName))
                {
                    categoryName = "Organization";
                    break;
                }
            }
        }
        return categoryName;        
    }
    
    
    private static void preTreeProcess (ArrayList<TreeData> data,ArrayList<Organization> orgList,ArrayList<Organization> selectedList) {

    	Organization org = orgList.get(0);
		Integer rootCategoryLevel = 0;
		TreeData td = new TreeData ();
		td.setData(org.getOrgName());
		td.getAttr().setId(org.getOrgNodeId().toString());
		td.getAttr().setCid(org.getOrgCategoryLevel().toString());
		rootCategoryLevel = org.getOrgCategoryLevel();
		td.getAttr().setTcl("1");
		org.setTreeLevel(1);
		Map<Integer, Organization> orgMap = new HashMap<Integer, Organization>();
		orgMap.put(org.getOrgNodeId(), org);
		treeProcess (org, orgList, td, selectedList, rootCategoryLevel, orgMap);
		data.add(td);
	}
    
    private static void preTreeProcessPTT (ArrayList<TreeData> data,
    		ArrayList<Organization> orgList,
    		ArrayList<Organization> selectedList,
    		int count) {

    	Organization org = orgList.get(0);
		Integer rootCategoryLevel = 0;
		TreeData td = new TreeData ();
		td.setData(org.getOrgName());
		td.getAttr().setId(org.getOrgNodeId().toString() + "_" + count);
		td.getAttr().setCid(org.getOrgCategoryLevel().toString());
		rootCategoryLevel = org.getOrgCategoryLevel();
		td.getAttr().setTcl("1");
		org.setTreeLevel(1);
		Map<Integer, Organization> orgMap = new HashMap<Integer, Organization>();
		orgMap.put(org.getOrgNodeId(), org);
		treeProcessPTT (org, orgList, td, selectedList, rootCategoryLevel, orgMap, count);
		data.add(td);
	}
	
    private static void treeProcess (Organization org,List<Organization> list,TreeData td, 
    		ArrayList<Organization> selectedList, Integer rootCategoryLevel, 
    		Map<Integer, Organization> orgMap) {

		Integer treeLevel = 0;
		Organization parentOrg = null;
		for (Organization tempOrg : list) {
			if (org.getOrgNodeId().equals(tempOrg.getOrgParentNodeId())) {
				
				if (selectedList.contains(tempOrg)) {
					
					int index = selectedList.indexOf(tempOrg);
					if (index != -1) {
						
						Organization selectedOrg = selectedList.get(index);
						selectedOrg.setIsAssociate(false);
					}
					
				}
				TreeData tempData = new TreeData ();
				tempData.setData(tempOrg.getOrgName());
				tempData.getAttr().setId(tempOrg.getOrgNodeId().toString());
				tempData.getAttr().setCid(tempOrg.getOrgCategoryLevel().toString());
				parentOrg = orgMap.get(tempOrg.getOrgParentNodeId());
				treeLevel = parentOrg.getTreeLevel() + 1;
				tempOrg.setTreeLevel(treeLevel);
				tempData.getAttr().setTcl(treeLevel.toString());
				td.getChildren().add(tempData);
				orgMap.put(tempOrg.getOrgNodeId(), tempOrg);
				treeProcess (tempOrg, list, tempData, selectedList, rootCategoryLevel, orgMap);
			}
		}
	}
    
    private static void treeProcessPTT (Organization org,List<Organization> list,TreeData td, 
    		ArrayList<Organization> selectedList, Integer rootCategoryLevel, 
    		Map<Integer, Organization> orgMap,
    		int count) {

		Integer treeLevel = 0;
		Organization parentOrg = null;
		for (Organization tempOrg : list) {
			if (org.getOrgNodeId().equals(tempOrg.getOrgParentNodeId())) {
				
				if (selectedList.contains(tempOrg)) {
					
					int index = selectedList.indexOf(tempOrg);
					if (index != -1) {
						
						Organization selectedOrg = selectedList.get(index);
						selectedOrg.setIsAssociate(false);
					}
					
				}
				TreeData tempData = new TreeData ();
				tempData.setData(tempOrg.getOrgName());
				tempData.getAttr().setId(tempOrg.getOrgNodeId().toString() + "_" + count);
				tempData.getAttr().setCid(tempOrg.getOrgCategoryLevel().toString());
				parentOrg = orgMap.get(tempOrg.getOrgParentNodeId());
				treeLevel = parentOrg.getTreeLevel() + 1;
				tempOrg.setTreeLevel(treeLevel);
				tempData.getAttr().setTcl(treeLevel.toString());
				td.getChildren().add(tempData);
				orgMap.put(tempOrg.getOrgNodeId(), tempOrg);
				treeProcessPTT (tempOrg, list, tempData, selectedList, rootCategoryLevel, orgMap, count);
			}
		}
	}
	
	private boolean userScheduleAndFindSessionPermission() 
    {               
        String roleName = this.user.getRole().getRoleName();        
        return (roleName.equalsIgnoreCase(PermissionsUtils.ROLE_NAME_ADMINISTRATOR) ||
                roleName.equalsIgnoreCase(PermissionsUtils.ROLE_NAME_ACCOMMODATIONS_COORDINATOR) ||
                roleName.equalsIgnoreCase(PermissionsUtils.ROLE_NAME_COORDINATOR));
    }
	
	 private TestSessionData getTestSessionsForOrgNode(Integer orgNodeId, FilterParams filter, PageParams page, SortParams sort,Integer userId) 
	    {
	        TestSessionData tsd = new TestSessionData();                        
	        try
	        {      
	            tsd = this.testSessionStatus.getTestSessionsForOrgNode(userName, orgNodeId, filter, page, sort, userId);
	        }
	        catch (CTBBusinessException be)
	        {
	            be.printStackTrace();
	        }
	        return tsd;
	    }
	    
	private boolean accessNewUI(CustomerConfiguration [] customerConfigs)
    {            
        boolean accessNewUI = false;
        
        for (int i=0; i < customerConfigs.length; i++)
        {
        	CustomerConfiguration cc = (CustomerConfiguration)customerConfigs[i];
            if (cc.getCustomerConfigurationName().equalsIgnoreCase("TAS_Revised_UI")) {
            	accessNewUI = true;
            	break;
            }
        }
        return accessNewUI;
    }

	/**
     * initHintQuestionOptions
     */
    private void initHintQuestionOptions()
    {                 
        try {
            PasswordHintQuestion[] options = 
                    this.userManagement.getHintQuestions();
            
            this.hintQuestionOptions = new LinkedHashMap<String, String>();
            
            this.hintQuestionOptions.put("", "Select a hint question");
            
            if (options != null) {
                for (int i=0 ; i<options.length ; i++) {
                    this.hintQuestionOptions.put(
                            ((PasswordHintQuestion) options[i])
                            .getPasswordHintQuestionId(),
                            ((PasswordHintQuestion)options[i])
                            .getPasswordHintQuestion());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private List<SessionStudent> buildStudentList(SessionStudent [] sessionStudents, Map<Integer, Map> accomodationMap) 
    {
        List<SessionStudent> studentList = new ArrayList<SessionStudent>();
        Map<String,String> innerMap;
        for (int i=0 ; i<sessionStudents.length; i++) {
        	innerMap = new HashMap<String,String>();
            SessionStudent ss = (SessionStudent)sessionStudents[i];
            if(ss.getStatus()!=null) {
            	ss.setStatusEditable(ss.getStatus().getEditable());
            	 ss.setStatusCopyable(ss.getStatus().getCopyable());
            } else {
            	ss.setStatusEditable("T");
            	ss.setStatusCopyable("T");
            }

           
            
            if (ss != null) {                
                StringBuffer buf = new StringBuffer();
                buf.append(ss.getFirstName()).append(" ").append(ss.getLastName()).append(": ");
                if ("T".equals(ss.getCalculator())) {
                    if ("true".equals(ss.getHasColorFontAccommodations()) ||
                        "T".equals(ss.getScreenReader()) ||
                        "T".equals(ss.getTestPause()) ||
                        "T".equals(ss.getUntimedTest()))
                        buf.append("Calculator, ");
                    else
                        buf.append("Calculator");
                }
                if(ss.getMusicFileId() == null || "".equals(ss.getMusicFileId().trim())){
                	ss.setAuditoryCalming("F");
                }else {
                	ss.setAuditoryCalming("T");
                }
                
                if ("true".equals(ss.getHasColorFontAccommodations())) {
                    if ("T".equals(ss.getScreenReader()) ||
                        "T".equals(ss.getTestPause()) ||
                        "T".equals(ss.getUntimedTest()))
                        buf.append("Color/Font, ");
                    else
                        buf.append("Color/Font");
                }
                if ("T".equals(ss.getScreenReader())) {
                    if ("T".equals(ss.getTestPause()) ||
                        "T".equals(ss.getUntimedTest()))
                        buf.append("ScreenReader, ");
                    else
                        buf.append("ScreenReader");
                }
                if ("T".equals(ss.getTestPause())) {
                    if ("T".equals(ss.getUntimedTest()))
                        buf.append("TestPause, ");
                    else
                        buf.append("TestPause");
                }
                if ("T".equals(ss.getUntimedTest())) {
                    buf.append("UntimedTest");
                }
                buf.append(".");
                ss.setExtPin3(escape(buf.toString()));
                ss.setHasColorFontAccommodations(getHasColorFontAccommodations(ss));
                ss.setHasAccommodations(studentHasAccommodation(ss));
                 if(ss.getMiddleName() != null && !ss.getMiddleName().equals(""))
                	ss.setMiddleName( ss.getMiddleName().substring(0,1));
                 
                 if (ss.getStatus().getCode() == null || ss.getStatus().getCode().equals(""))
                     ss.getStatus().setCode("&nbsp;");
                 if ("Ses".equals(ss.getStatus().getCode()))
                 {
                     StringBuffer buf1 = new StringBuffer();
                     TestSession ts = ss.getStatus().getPriorSession();
                     if (ts != null)
                     {
//                         String timeZone = ts.getTimeZone();
                         TestAdminStatusComputer.adjustSessionTimesToLocalTimeZone(ts);
                         String testAdminName = ts.getTestAdminName();
                         testAdminName = testAdminName.replaceAll("\"", "&quot;");
                         buf1.append("Session Name: ").append(testAdminName);
                         buf1.append("<br/>Start Date: ").append(DateUtils.formatDateToDateString(ts.getLoginStartDate()));
                         buf1.append("<br/>End Date: ").append(DateUtils.formatDateToDateString(ts.getLoginEndDate()));
//                         buf.append("<br/>Start Date: ").append(DateUtils.formatDateToDateString(com.ctb.util.DateUtils.getAdjustedDate(ts.getLoginStartDate(), TimeZone.getDefault().getID(), timeZone, ts.getDailyLoginStartTime())));
//                         buf.append("<br/>End Date: ").append(DateUtils.formatDateToDateString(com.ctb.util.DateUtils.getAdjustedDate(ts.getLoginEndDate(), TimeZone.getDefault().getID(), timeZone, ts.getDailyLoginEndTime())));
                     }
                     ss.setExtPin2(buf1.toString());
                 }
                 
                 innerMap.put("screenMagnifier", ss.getScreenMagnifier());
                 innerMap.put("screenReader", ss.getScreenReader());
                 innerMap.put("calculator", ss.getCalculator());
                 innerMap.put("testPause", ss.getTestPause());
                 innerMap.put("untimedTest", ss.getUntimedTest());
                 innerMap.put("highLighter", ss.getHighLighter());
                 innerMap.put("maskingRular", ss.getMaskingRular());
                 innerMap.put("maskingTool", ss.getMaskingTool());
                 innerMap.put("auditoryCalming", ss.getAuditoryCalming());
                 innerMap.put("magnifyingGlass", ss.getMagnifyingGlass());
                 
                 if("T".equals(ss.getExtendedTimeAccom()) || (ss.getExtendedTimeAccom() != null && !ss.getExtendedTimeAccom().equals("") && !ss.getExtendedTimeAccom().equals("F"))){
                	 innerMap.put("extendedTimeAccom","T");
            	 }else {
            		 innerMap.put("extendedTimeAccom","F");
            	 }
                 innerMap.put("hasColorFontAccommodations",getHasColorFontAccommodations(ss));
                 
                 if(null != ss.getExtendedTimeFactor()){
                	 innerMap.put("extendedTimeFactor", ss.getExtendedTimeFactor().toString());
                 }
                 accomodationMap.put(ss.getStudentId(), innerMap);
                studentList.add(ss);
                //idToStudentMap.put(ss.getStudentId()+":"+ss.getOrgNodeId(), ss);

            }
        }
        return studentList;
    }
    
    
    public String getHasColorFontAccommodations(SessionStudent ss) {
        String result = "F";
        if( ss.getQuestionBackgroundColor() != null ||
        	ss.getQuestionFontColor() != null ||
        	ss.getQuestionFontSize() != null ||
        	ss.getAnswerBackgroundColor() != null ||
        	ss.getAnswerFontColor() != null ||
        	ss.getAnswerFontSize() != null)
            result = "T";
        return result;
    }
    
    public String getHasColorFontAccommodations(RosterElement ss) {
        String result = "F";
        if( ss.getQuestionBackgroundColor() != null ||
        	ss.getQuestionFontColor() != null ||
        	ss.getQuestionFontSize() != null ||
        	ss.getAnswerBackgroundColor() != null ||
        	ss.getAnswerFontColor() != null ||
        	ss.getAnswerFontSize() != null)
            result = "T";
        return result;
    }
    
    public String studentHasAccommodation(SessionStudent  sa){
		 String hasAccommodations = "No";
	        if( "T".equals(sa.getScreenMagnifier()) ||
	            "T".equals(sa.getScreenReader()) ||
	            "T".equals(sa.getCalculator()) ||
	            "T".equals(sa.getTestPause()) ||
	            "T".equals(sa.getUntimedTest()) ||
	            "T".equals(sa.getHighLighter()) ||
	            "T".equals(sa.getExtendedTimeAccom()) ||
	            (sa.getMaskingRular() != null && !sa.getMaskingRular().equals("") && !sa.getMaskingRular().equals("F"))||
	            (sa.getExtendedTimeAccom() != null && !sa.getExtendedTimeAccom().equals("") && !sa.getExtendedTimeAccom().equals("F")) || 
	            (sa.getAuditoryCalming() != null && !sa.getAuditoryCalming().equals("") && !sa.getAuditoryCalming().equals("F")) || 
	            (sa.getMagnifyingGlass() != null && !sa.getMagnifyingGlass().equals("") && !sa.getMagnifyingGlass().equals("F")) || 
	           (sa.getMaskingTool() != null && !sa.getMaskingTool().equals("") && !sa.getMaskingTool().equals("F")) || 
	            sa.getQuestionBackgroundColor() != null ||
	            sa.getQuestionFontColor() != null ||
	            sa.getQuestionFontSize() != null ||
	            sa.getAnswerBackgroundColor() != null ||
	            sa.getAnswerFontColor() != null ||
	            sa.getAnswerFontSize() != null)
	        	hasAccommodations = "Yes";
	   return hasAccommodations;
	}
    
    
    
    private String escape(String str)
    {
        int len = str.length ();

        StringBuffer safe = new StringBuffer (len);

        for (int i = 0; i < len; i++)
        {
            char cur = str.charAt (i);
            if (cur == '\'')
            {
             safe.append ('\\');
             safe.append (cur);
            }
            else
                safe.append (cur);
        }
        return new String (safe);
    }
    private boolean isWVCustomerTopLevelAdminAndAdminCO(){
		boolean isWVCustomerTopLevelAdminAndAdminCO = false;
		boolean isUserTopLevel =false;
		try {
			isUserTopLevel = orgnode.checkTopOrgNodeUser(this.userName);	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (isUserTopLevel &&(isAdminUser() || isAdminCoordinatorUser()))
			isWVCustomerTopLevelAdminAndAdminCO = true;
		return isWVCustomerTopLevelAdminAndAdminCO;
	}
	
    /////////////////////////////////////////////////////////////////////////////////////////////    
    ///////////////////////////// END OF SETUP USER PERMISSION ///////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////    
    /**
	 * FormData get and set methods may be overwritten by the Form Bean editor.
	 */
	public static class SessionOperationForm extends SanitizedFormData
	{

	}
	/**
	 * @return the sessionListCUFU
	 */
	public List<TestSessionVO> getSessionListCUFU() {
		return sessionListCUFU;
	}

	/**
	 * @param sessionListCUFU the sessionListCUFU to set
	 */
	public void setSessionListCUFU(List<TestSessionVO> sessionListCUFU) {
		this.sessionListCUFU = sessionListCUFU;
	}

	/**
	 * @return the sessionListPA
	 */
	public List<TestSessionVO> getSessionListPA() {
		return sessionListPA;
	}

	/**
	 * @param sessionListPA the sessionListPA to set
	 */
	public void setSessionListPA(List<TestSessionVO> sessionListPA) {
		this.sessionListPA = sessionListPA;
	}

	public LinkedHashMap<String, String> getHintQuestionOptions() {
		return hintQuestionOptions;
	}

	public void setHintQuestionOptions(LinkedHashMap<String, String> hintQuestionOptions) {
		this.hintQuestionOptions = hintQuestionOptions;
	}

	public UserProfileInformation getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfileInformation userProfile) {
		this.userProfile = userProfile;
	}	
	
	// Added for Proctor : Start
	
	@Jpf.Action(forwards={
    		@Jpf.Forward(name = "success", 
					path ="assessments_sessions.jsp")
	})
    protected Forward getProctorList(SessionOperationForm form){
    	
		HttpServletResponse resp = getResponse();
		OutputStream stream = null;
		//final String PROCTOR_DEFAULT_SORT = "LastName";
		
		
		String json = "";
		
		//String testId = getRequest().getParameter("selectedTestId");
		String proctorOrgNodeId = getRequest().getParameter("proctorOrgNodeId");
		String isOKEqFormAdmin = getRequest().getParameter("isOKEqFormAdmin");
		Integer selectedOrgNodeId = null;
		if(proctorOrgNodeId != null)
			selectedOrgNodeId = Integer.parseInt(proctorOrgNodeId);
		//if(testId != null)
			//selectedTestId = Integer.parseInt(testId);
		try {
			FilterParams proctorFilter = null;
	        PageParams proctorPage = null;
	        SortParams proctorSort = null;
	        // proctorSort = FilterSortPageUtils.buildSortParams(PROCTOR_DEFAULT_SORT, FilterSortPageUtils.ASCENDING);
	        List<UserProfileInformation> proctorNodes = null;

	        // Get the list of proctors
	        UserData ud = getProctors(selectedOrgNodeId, proctorFilter, proctorPage, proctorSort);
	        if( ud != null) {
	        	proctorNodes = buildProctorList(ud.getUsers());
	        }
			Base base = new Base();
			base.setPage("1");
			base.setRecords("10");
			base.setTotal("2");
			base.setUserProfileInformation(proctorNodes);
			
			Gson gson = new Gson();
			System.out.println ("Json process time Start:"+new Date());
			json = gson.toJson(base);
			//System.out.println ("Json process time End:"+new Date() +".."+json);
			try{
				resp.setContentType("application/json");
				stream = resp.getOutputStream();
				resp.flushBuffer();
				stream.write(json.getBytes("UTF-8"));
			}
			finally{
				if (stream!=null){
					stream.close();
				}
			}
		} catch (Exception e) {
			System.err.println("Exception while processing CR response.");
			e.printStackTrace();
		}

		return null;

	}
	
	
	private UserData getProctors(Integer orgNodeId, FilterParams filter, PageParams page, SortParams sort) {    
        UserData ud = null;
        try {      
            ud = this.scheduleTest.getUsersMinimalInfoForOrgNode(this.userName, orgNodeId, filter, page, sort);
        }
        catch (CTBBusinessException be) {
            be.printStackTrace();
        }
        return ud;
    }

	
	public List<UserProfileInformation> buildProctorList(User[] users) {
        ArrayList<UserProfileInformation> userList = new ArrayList<UserProfileInformation>();
        if (users != null) {
            //User[] users = uData.getUsers();
            if(users != null){
                for (int i=0 ; i<users.length ; i++) {
                    User user = users[i];
                    if (user != null && user.getUserName() != null) {
                        UserProfileInformation userDetail = new UserProfileInformation(user);
                        if(user.getDefaultScheduler().equals("T")){
                        	userDetail.setDefaultScheduler("T");
                        	
                        }else {
                        	userDetail.setDefaultScheduler("F");
                        }
                        userList.add(userDetail);
                    }
                }
            }
        }
        return userList;
    }
    
    // Added for Proctor : End
	 private String getTACsInString(Vector<String> vec) 
	    {
	        Iterator<String> it = vec.iterator();
	        StringBuffer buf = new StringBuffer();
	        while (it.hasNext()) {
	            buf.append((String)it.next());
	            if (it.hasNext())
	                buf.append(", ");
	        }
	        return buf.toString();
	    }
	 
	 private String getMessageResourceBundle(CTBBusinessException e, String msgId) 
	    {
	        String errorMessage = "";
            errorMessage = MessageResourceBundle.getMessage(msgId, e.getMessage());
	        return errorMessage; 
	    }
	 
	 //Added for block off grade
	 private FilterParams generateFilterParams(String selectedLevel) {
		 
		 FilterParams studentFilter = new FilterParams();		 
		String [] arg = new String[1];
		arg[0] = selectedLevel;
		studentFilter = new FilterParams();
		ArrayList<FilterParam> filters = new ArrayList<FilterParam>();
		if(selectedLevel.contains("-")) {
			String [] grades = selectedLevel.split("-");
			if(selectedLevel.contains("K")){
				for(int i=0; i< grades.length;i++){
					String [] args = new String[1];
					args[0] = (String)grades[i];
					filters.add(new FilterParam("StudentGrade", args, FilterType.EQUALS));
					
					//** [IAA]: STORY: OAS � Grade string naming and Filtering (e.g. 9 should also look for 09, etc)
					int iGrade = 0;
					boolean startsWith0 = grades[i].startsWith("0");
					try { iGrade = Integer.parseInt(grades[i]);} catch(Exception e) {iGrade = 0;}
					if (iGrade>0 && iGrade<10)
					{
						args = new String[1];
						args[0] = (startsWith0)?""+iGrade:"0"+iGrade;
						filters.add(new FilterParam("StudentGrade", args, FilterType.EQUALS));
					}
				}
			}else{
				int initVal = Integer.parseInt(grades[0]);
				int finalVal = Integer.parseInt(grades[1]);
				for(int i = initVal; i <= finalVal; i++) {
					String [] args = new String[1];
					args[0] = String.valueOf(i);
					filters.add(new FilterParam("StudentGrade", args, FilterType.EQUALS));
					
					//** [IAA]: STORY: OAS � Grade string naming and Filtering (e.g. 9 should also look for 09, etc)
					if (i>0 && i<10)
					{
						args = new String[1];
						args[0] = String.valueOf("0"+i);
						filters.add(new FilterParam("StudentGrade", args, FilterType.EQUALS));
					}
				}
			}
		} else if(selectedLevel.contains("/")) {
			String [] grades = selectedLevel.split("/");
			String [] args = new String[1];
			args[0] = grades[0];
			filters.add(new FilterParam("StudentGrade", args, FilterType.EQUALS));
			args[0] = grades[1];
			filters.add(new FilterParam("StudentGrade", args, FilterType.EQUALS));
			
			//** [IAA]: STORY: OAS � Grade string naming and Filtering (9 should also look for 09, etc)
			int iGrade = 0;
			boolean startsWith0 = grades[0].startsWith("0");
			try { iGrade = Integer.parseInt(grades[0]);} catch(Exception e) {iGrade = 0;}
			if (iGrade>0 && iGrade<10)
			{
				String [] args2 = new String[1];
				args2[0] = (startsWith0)?""+iGrade:"0"+iGrade;
				filters.add(new FilterParam("StudentGrade", args2, FilterType.EQUALS));
			}
			startsWith0 = grades[1].startsWith("0");
			try { iGrade = Integer.parseInt(grades[1]);} catch(Exception e) {iGrade = 0;}
			if (iGrade>0 && iGrade<10)
			{
				String [] args2 = new String[1];
				args2[0] = (startsWith0)?""+iGrade:"0"+iGrade;
				filters.add(new FilterParam("StudentGrade", args2, FilterType.EQUALS));
			}
		} else {
			filters.add(new FilterParam("StudentGrade", arg, FilterType.EQUALS));
			
			//** [IAA]: OAS � Grade string naming and Filtering (9 should also look for 09, etc)
			int iGrade = 0;
			boolean startsWith0 = arg[0].startsWith("0"); 
			try { iGrade = Integer.parseInt(arg[0]);} catch(Exception e) {iGrade = 0;}
			if (iGrade>0 && iGrade<10)
			{
				String [] args2 = new String[1];
				args2[0] = (startsWith0)?""+iGrade:"0"+iGrade;
				filters.add(new FilterParam("StudentGrade", args2, FilterType.EQUALS));
			}
		}
		studentFilter.setFilterParams((FilterParam[])filters.toArray(new FilterParam[0]));
		 
		 return studentFilter;
		 
	 }
	 
	 private void getStudentGrades(CustomerConfiguration[] customerConfigurations) 
		{     
		 this.studentGradesForCustomer = new ArrayList<String>();
			Integer configId=0;
			for (int i=0; i < customerConfigurations.length; i++)
			{
				CustomerConfiguration cc = (CustomerConfiguration)customerConfigurations[i];
				if (cc.getCustomerConfigurationName() == null) cc.setCustomerConfigurationName("");
				if (cc.getDefaultValue() == null) cc.setDefaultValue("");
				
				if (cc.getCustomerConfigurationName().equalsIgnoreCase("Grade") && cc.getDefaultValue().equalsIgnoreCase("T"))
				{
					configId = cc.getId();
					CustomerConfigurationValue[] customerConfigurationsValue = customerConfigurationValues(configId);
					for(int j=0; j<customerConfigurationsValue.length; j++){
						this.studentGradesForCustomer.add(customerConfigurationsValue[j].getCustomerConfigurationValue());
					}	

				}

			}			
		}
	 
	 private int getRosterForTestSession(Integer testAdminId){
		 int studentCount = 0;
		 //String errorMessage = "";
		 try
	        {
	            
	            RosterElementData red = this.testSessionStatus.getRosterForTestSession(this.userName,
	                            testAdminId, null, null, null);
	            studentCount = red.getTotalCount().intValue(); 
	                 
	        } 
	        //START- Changed for deferred defect 64446 
	        catch (TransactionTimeoutException e)
	        {
	            e.printStackTrace();
	            String errorMessage =MessageResourceBundle.getMessage("SelectSettings.FailedToSaveTestSessionTransactionTimeOut"); 
	            //System.out.println("errorMessage in TransactionTimeoutException");
	            this.getRequest().setAttribute("errorMessage", errorMessage); 
	            return 0;            
	        } 
	        //END- Changed for deferred defect 64446
	        catch (CTBBusinessException e)
	        {
	            e.printStackTrace();
	            String errorMessage = getMessageResourceBundle(e, "SelectSettings.FailedToSaveTestSession"); 
	            this.getRequest().setAttribute("errorMessage", errorMessage); 
	            return 0;            
	        }  
	        return studentCount;
	 }
	 
	//Added for view/monitor test status: Start
	 
	 private void initializeTestSession () {
			
			retrieveInfoFromSession();                        
	        this.testRosterFilter = new TestRosterFilter();            
	        getCustomerConfigurations();  
	        this.sessionDetailsShowScores = isSessionDetailsShowScores();
	        this.subtestValidationAllowed = isSubtestValidationAllowed();
	        this.studentStatusSubtests = new ArrayList();
	        this.showStudentReportButton = showStudentReportButton();
	        this.selectedRosterIds = new ArrayList();
	        genFile = getRequest().getParameter("genFile");
	        if ("generate_report_file".equals(genFile)) {   
	        	initGenerateReportFile();
	        }
	    }
			
	    
	    /**
	     * @jpf:action
	     */ 
	    @Jpf.Action(forwards = { 
	        @Jpf.Forward(name = "success", path = "validate_subtests_detail.jsp")
	    })
	    protected Forward to_validate_subtests_detail()
	    {
	        //String forwardName = handleValidateAction(form);
	    	Integer testRosterID = Integer.valueOf(getRequest().getParameter("testRosterID"));
	    	String[] selectedItemSetIds = getRequest().getParameterValues("selectedItemSetIds");
	        prepareSubtestsDetailInformation(testRosterID, selectedItemSetIds, true);
	        
	        return new Forward("success");
	    }
	    
	    
	    /**
	     * @jpf:action
	     */
		@Jpf.Action(
			forwards = {
				@Jpf.Forward(name = "report", path = "individualReport.do", redirect=true)
		})
	    protected Forward viewIndividualReport()
	    {			
	        try {
	        	if (this.userName == null || (this.isEOIUser && this.isMappedWith3_8User)) {
	    			getLoggedInUserPrincipal();
	    			this.userName = (String)getSession().getAttribute("userName");
	    		}

	        	String accessBy = getRequest().getParameter("accessBy");
	        	String rosterId = getRequest().getParameter("rosterId");
	        	this.currentTestAdminId = getRequest().getParameter("testAdminId");
	        	
	        	if (accessBy.equals("student")) {
        			this.currentReportUrl = this.testSessionStatus.getIndividualReportUrl(this.userName, Integer.valueOf(rosterId));
	        	}
	        	else {
        			this.currentReportUrl = this.testSessionStatus.getIndividualReportUrlForSession(this.userName, Integer.valueOf(this.currentTestAdminId));	        		
	        	}
	        } catch (Exception e) {
	            e.printStackTrace();
	        }	                    
	        return new Forward("report");
	    }
	    
	    
	    /**
	     * @jpf:action
	     */
		@Jpf.Action(
			forwards = {
				@Jpf.Forward(name = "report", path = "turnleaf_reports.jsp")
		})
	    protected Forward individualReport()
	    {			
            this.getRequest().setAttribute("reportUrl", this.currentReportUrl);
            this.getRequest().setAttribute("testAdminId", this.currentTestAdminId);	            
	        return new Forward("report");
	    }
		
		
		private Integer getDefaultTestingWindowValue() {
			
			CustomerConfiguration [] customerConfigs = getCustomerConfigurations(this.customerId);
			for (int i=0; i < customerConfigs.length; i++)
            {
				CustomerConfiguration cc = (CustomerConfiguration)customerConfigs[i];
	            if (cc.getCustomerConfigurationName().equalsIgnoreCase("Default_Testing_Window_Days"))
	            {
	            	Integer value = Integer.valueOf(cc.getDefaultValue());
	            	return (null == value || value.intValue() == 0)?null:value;
	            } 
            }
			return null;
		}
		
	    private void prepareValidateButtons(String[] itemSetIds)
	    {            
	        if ((itemSetIds != null) && (itemSetIds.length > 0) && (itemSetIds[0] != null))
	            this.getRequest().setAttribute("disableToogleButton", "false");
	        else 
	            this.getRequest().setAttribute("disableToogleButton", "true");
	    }
	    
	    private String getTestLevel(List subtestList)
	    {
	        String level = null;
	        for (int i=0; i < subtestList.size(); i++)
	        {
	            SubtestDetail sd = (SubtestDetail)subtestList.get(i);
	            if ((sd.getLevel() != null) && (sd.getLevel() != ""))
	            {
	                return sd.getLevel();    
	            }
	        }
	        return level;
	    }
	    
	    private String getTestGrade(List subtestList)
	    {
	        String grade = null;
	        for (int i=0; i < subtestList.size(); i++)
	        {
	            SubtestDetail sd = (SubtestDetail)subtestList.get(i);
	            if ((sd.getGrade() != null) && (sd.getGrade() != ""))
	            {
	                return sd.getGrade();    
	            }
	        }
	        return grade;
	    }
	    
	    private void addTABESubtest(TestElement te) 
	    {
	        if (this.TABETestElements == null)
	            this.TABETestElements = new ArrayList();
	            
	        boolean found = false;
	        for (int i=0 ; i<this.TABETestElements.size() ; i++) {
	            TestElement tte = (TestElement)this.TABETestElements.get(i);
	            if (tte.getItemSetId().intValue() == te.getItemSetId().intValue()) {
	                found = true;
	                break;
	            }
	        }
	        if (! found) {
	            this.TABETestElements.add(te);
	        }
	    }
	    
	    private TestElement[] getTestElementsForParent(Integer parentItemSetId, String itemSetType) 
	    {
	        TestElement[] tes = null;
	        try
	        {      
	            TestElementData ted = this.testSessionStatus.getTestElementsForParent(this.userName, parentItemSetId, itemSetType, null, null, null);
	            tes = ted.getTestElements();            
	        }
	        catch (CTBBusinessException be)
	        {
	            be.printStackTrace();
	        }
	        return tes;
	    }
	    
	    private TestElement []  orderedSubtestList(TestElement[] subtestelements,Integer studentId,Integer testAdminId)
	    {
	        TestElement [] orderedSubtestElements = new TestElement[subtestelements.length];
	        StudentManifest [] sms = getStudentManifests(studentId,testAdminId);
	        HashMap smHM = new HashMap();
	        for(int i=0;i<sms.length;i++){
	           StudentManifest  sm = sms[i];
	           smHM.put(sm.getItemSetId(),new Integer(i));
	        }  
	        for(int j=0;j<subtestelements.length;j++){
	            TestElement te = subtestelements[j];
	            if(smHM.containsKey(te.getItemSetId())){
	                orderedSubtestElements[((Integer)smHM.get(te.getItemSetId())).intValue()] = te;
	            }
	        }
	        return orderedSubtestElements;
	    }
	    
	    private StudentManifest [] getStudentManifests(Integer studentId,Integer testAdminId)
	    {
	        StudentManifest [] sm = null;
	        try {  
	                StudentManifestData  smd =  this.scheduleTest.getManifestForRoster(this.userName,studentId,testAdminId,null,null,null);
	                sm = smd.getStudentManifests();
	        }catch (CTBBusinessException be) {
	            be.printStackTrace();
	        }   
	        return sm;
	    }
	    
	    private StudentSessionStatus[] getStudentItemSetStatusesForRoster(Integer studentId, Integer testAdminId) 
	    {
	        StudentSessionStatus[] ssss = null;
	        try
	        {
	            SortParams sort = FilterSortPageUtils.buildSortParams("ItemSetOrder", ColumnSortEntry.ASCENDING);
	            StudentSessionStatusData sssData = testSessionStatus.getStudentItemSetStatusesForRoster(this.userName, studentId, testAdminId, null, null, sort);
	            ssss = sssData.getStudentSessionStatuses();            
	        }
	        catch (CTBBusinessException be)
	        {
	            be.printStackTrace();
	        }
	        return ssss;
	    }
	    
	    private boolean isTestSessionCompleted(TestSessionVO testSession)
	    {
	        boolean completed = false;
	        if (testSession.getTestAdminStatus().equalsIgnoreCase("PA")) {
	            completed = true;
	        }
	        return completed;
	    }
	    
	    private void isLasLinkCustomer()
	    {               
	        
	        boolean isLasLinkCustomer = false;

	            
				 for (int i=0; i < this.customerConfigurations.length; i++)
	            {
	            	 CustomerConfiguration cc = (CustomerConfiguration)this.customerConfigurations[i];
	            	//isLasLink customer
	                if (cc.getCustomerConfigurationName().equalsIgnoreCase("LASLINK_Customer") && cc.getDefaultValue().equals("T")	)
	                {
	                	isLasLinkCustomer = true;
	                    break;
	                } 
	            }
	       
	        this.isLasLinkCustomer = isLasLinkCustomer;
	       
	    }
	    
	    private  String pageSizevalue(CustomerConfiguration[] customerConfigurations)
	    {
	    	for (int i=0; i < customerConfigurations.length; i++)
            {
            	 CustomerConfiguration cc = (CustomerConfiguration)customerConfigurations[i];
                
	        	if (cc.getCustomerConfigurationName()!=null && cc.getCustomerConfigurationName().equalsIgnoreCase("Page_Size") )
	            {
	        		pageSize = cc.getDefaultValue();
	            break;
	            }
	        	
            }
	    	return pageSize;
	    }
	    
	    private boolean pageConfigPresent(CustomerConfiguration[] customerConfigurations)
	    {
	    	boolean pageSizeconfig = false;
	    	for (int i=0; i < customerConfigurations.length; i++)
            {
            	 CustomerConfiguration cc = (CustomerConfiguration)customerConfigurations[i];
                
	        	if (cc.getCustomerConfigurationName().equalsIgnoreCase("Page_Sizeconfig") && cc.getDefaultValue().equals("T") )
	            {
	        		pageSizeconfig = true;
	            break;
	            }
	        	
            }
	    	return pageSizeconfig;
	    }
	    
	    private boolean hasBulkStateReportExport(CustomerConfiguration[] customerConfigurations)
	    {
	    	boolean hasBulkStateReportExport = false;
	    	for (int i=0; i < customerConfigurations.length; i++)
            {
            	 CustomerConfiguration cc = (CustomerConfiguration)customerConfigurations[i];
                
	        	if (cc.getCustomerConfigurationName().equalsIgnoreCase("Bulk_State_Report_Export") && cc.getDefaultValue().equals("T") )
	            {
	        		hasBulkStateReportExport = true;
	            break;
	            }
	        	
            }
	    	return hasBulkStateReportExport;
	    }
	    
	    private boolean isTabeLocatorSession(String productType)
	    {
	        if (productType.equalsIgnoreCase("TL"))
	            return true;   
	        else             
	            return false;
	    }
	    
	    private boolean isTabeSession(String productType)
	    {
	        if (productType.equalsIgnoreCase("TB") || productType.equalsIgnoreCase("TL"))
	            return true;   
	        else             
	            return false;
	    }
	    
	    private boolean isTabeCCSSSession(String productType)
	    {
	        if (productType.equalsIgnoreCase("TC"))
	            return true;   
	        else             
	            return false;
	    }
	    
	    private TestProduct getProductForTestAdmin(Integer testAdminId)
	    {
	        TestProduct tp = null;
	        try {      
	            tp = this.testSessionStatus.getProductForTestAdmin(this.userName, testAdminId);
	        }
	        catch (CTBBusinessException be) {
	            be.printStackTrace();
	        }   
	        return tp;
	    }
	    
	    private RosterElement getTestRosterDetails(Integer testRosterId) 
	    {
	        RosterElement re = null;      
	        try
	        {
	            re = this.testSessionStatus.getRoster(testRosterId);
	        }
	        catch (CTBBusinessException be)
	        {
	            be.printStackTrace();
	        }    
	        return re;
	    }  
		
		 private TestSessionVO getTestSessionDetails(Integer sessionId) 
		    {
		        TestSessionVO testSession = null;
		        try
		        {      
		            TestSessionData tsd = this.testSessionStatus.getTestSessionDetails(this.userName, sessionId);
		            TestSession[] testsessions = tsd.getTestSessions();            
		            TestSession ts = testsessions[0];
		            testSession = new TestSessionVO(ts);            
		        }
		        catch (CTBBusinessException be)
		        {
		            be.printStackTrace();
		        }
		        return testSession;
		    }

			
			private boolean isSameAccessCode(List subtestList)
		    {
		        if (subtestList.size() <= 1)
		            return true;
		            
		        boolean sameAccessCode = true;
		        SubtestDetail sd = (SubtestDetail)subtestList.get(0);
		        String accessCode = sd.getAccessCode();
		        for (int i=1; i < subtestList.size(); i++)
		        {
		            sd = (SubtestDetail)subtestList.get(i);
		            if (! sd.getAccessCode().equals(accessCode))
		                sameAccessCode = false;    
		        }
		        return sameAccessCode;
		    }
	    
		 private TestElementData getTestElementsForTestSession(Integer sessionId) 
		    {
		        TestElementData ted = null;
		        try
		        {      
		            ted = this.testSessionStatus.getTestElementsForTestSession(this.userName, sessionId, null, null, null);
		        }
		        catch (CTBBusinessException be)
		        {
		            be.printStackTrace();
		        }
		        return ted;
		    }
		 
		 
		 private boolean getLocatorCompletionStatus(Integer studentId , Integer testAdminId){
			 boolean status = false;
			 try{
				 status = this.testSessionStatus.getLocatorCompletionStatus( studentId , testAdminId );
			 }
			 catch(CTBBusinessException be){
				 be.printStackTrace();
			 }
			 return status;
		 }
		
		private RosterElementData getRosterForViewTestSession(Integer sessionId) 
	    {
	        if (this.testRosterFilter == null)
	            this.testRosterFilter = new TestRosterFilter();            

	       // FilterParams filter = FilterSortPageUtils.buildTestRosterFilterParams(this.testRosterFilter);
	        FilterParams filter = null;
	        PageParams page = null;
	        SortParams sort = FilterSortPageUtils.buildSortParams(FilterSortPageUtils.TESTROSTER_DEFAULT_SORT, FilterSortPageUtils.ASCENDING);
	        
	        RosterElementData red = null;
	        try
	        	{    
	        		if(!this.hasShowRosterAccomAndHierarchy){  
	            		red = this.testSessionStatus.getRosterForTestSession(this.userName, sessionId, filter, page, sort);
	        		}
			     else{
			    	 red = this.testSessionStatus.getRosterForTestSessionWithShowRosterAccom(this.userName, sessionId, filter, page, sort); 	
			     }
	        }
	        catch (CTBBusinessException be)
	        {
	            be.printStackTrace();
	        }        
	        return red;
	    }
		
		private List buildSubtestList(TestElementData ted)
	    {
	        List subtestList = new ArrayList();        
	        TestElement[] subtestelements = ted.getTestElements();  
	        int sequence = 1;
	        for (int i=0; i < subtestelements.length; i++)
	        {
	            TestElement te = subtestelements[i];
	            if (te != null && "T".equals(te.getSessionDefault()))
	            {
	                SubtestDetail sd = new SubtestDetail(te, sequence);
	                subtestList.add(sd);
	                sequence++;
	            }
	        }        
	                
	        return subtestList;
	    }
		
		 private List buildRosterList(RosterElementData red)
		    {
		        List rosterList = new ArrayList();    
		        if (red != null){
			        RosterElement[] rosterElements = red.getRosterElements();
			        for (int i=0; i < rosterElements.length; i++)
			        {
			            RosterElement rosterElt = rosterElements[i];
			            if (rosterElt != null)
			            {
			                TestRosterVO vo = new TestRosterVO(rosterElt);   
			                rosterList.add(vo);
			            }
			        }   
		        }
		        return rosterList;
		    }
		 private void createGson(Base  base){
			 	OutputStream stream = null;
				HttpServletRequest req = getRequest();
				HttpServletResponse resp = getResponse();
				try {
					try {
						Gson gson = new Gson();
						String json = gson.toJson(base);
						//System.out.println("*********************************************************************");
						//System.out.println(json);
						resp.setContentType("application/json");
						resp.flushBuffer();
						stream = resp.getOutputStream();
						stream.write(json.getBytes());

					} finally{
						if (stream!=null){
							stream.close();
						}
					}
					
				}
				catch (Exception e) {
					System.err.println("Exception while retrieving optionList.");
					e.printStackTrace();
				}
			}
		
		/**
		 * Pass Gson object to create json data
		 * @param base
		 * @param gson
		 */
		private void createGson(Base base, Gson gson) {
		OutputStream stream = null;
		HttpServletRequest req = getRequest();
		HttpServletResponse resp = getResponse();
			try {
				try {
					String json = gson.toJson(base);
					resp.setContentType("application/json");
					resp.flushBuffer();
					stream = resp.getOutputStream();
					stream.write(json.getBytes());
	
				} finally {
					if (stream != null) {
						stream.close();
					}
				}
	
			} catch (Exception e) {
				System.err.println("Exception while retrieving json data");
				e.printStackTrace();
			}
		}
		 
		public List getStudentStatusSubtests() {
			return studentStatusSubtests;
		}

		public void setStudentStatusSubtests(List studentStatusSubtests) {
			this.studentStatusSubtests = studentStatusSubtests;
		}

		public boolean isShowStudentReportButton() {
			return showStudentReportButton;
		}

		public void setShowStudentReportButton(boolean showStudentReportButton) {
			this.showStudentReportButton = showStudentReportButton;
		}

		public String getGenFile() {
			return genFile;
		}

		public void setGenFile(String genFile) {
			this.genFile = genFile;
		}

		public TestRosterFilter getTestRosterFilter() {
			return testRosterFilter;
		}

		public void setTestRosterFilter(TestRosterFilter testRosterFilter) {
			this.testRosterFilter = testRosterFilter;
		}

		public ArrayList getSelectedRosterIds() {
			return selectedRosterIds;
		}

		public void setSelectedRosterIds(ArrayList selectedRosterIds) {
			this.selectedRosterIds = selectedRosterIds;
		}

		public CustomerConfigurationValue[] getCustomerConfigurationsValue() {
			return customerConfigurationsValue;
		}

		public void setCustomerConfigurationsValue(
				CustomerConfigurationValue[] customerConfigurationsValue) {
			this.customerConfigurationsValue = customerConfigurationsValue;
		}

		/*public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}*/

		public Integer getSessionId() {
			return sessionId;
		}

		public void setSessionId(Integer sessionId) {
			this.sessionId = sessionId;
		}

		public String[] getTestStatusOptions() {
			return testStatusOptions;
		}

		public void setTestStatusOptions(String[] testStatusOptions) {
			this.testStatusOptions = testStatusOptions;
		}

		public String getSetCustomerFlagToogleButton() {
			return setCustomerFlagToogleButton;
		}

		public void setSetCustomerFlagToogleButton(String setCustomerFlagToogleButton) {
			this.setCustomerFlagToogleButton = setCustomerFlagToogleButton;
		}

		public String[] getValidationStatusOptions() {
			return validationStatusOptions;
		}

		public void setValidationStatusOptions(String[] validationStatusOptions) {
			this.validationStatusOptions = validationStatusOptions;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileType() {
			return fileType;
		}

		public void setFileType(String fileType) {
			this.fileType = fileType;
		}

		public String getUserEmail() {
			return userEmail;
		}

		public void setUserEmail(String userEmail) {
			this.userEmail = userEmail;
		}

		public List getFileTypeOptions() {
			return fileTypeOptions;
		}

		public void setFileTypeOptions(List fileTypeOptions) {
			this.fileTypeOptions = fileTypeOptions;
		}

		public void setSessionDetailsShowScores(boolean sessionDetailsShowScores) {
			this.sessionDetailsShowScores = sessionDetailsShowScores;
		}

		public void setSubtestValidationAllowed(boolean subtestValidationAllowed) {
			this.subtestValidationAllowed = subtestValidationAllowed;
		}

		public void setCustomerConfigurations(
				CustomerConfiguration[] customerConfigurations) {
			this.customerConfigurations = customerConfigurations;
		}

		/**
		 * New method added for CR - GA2011CR001
		 * getCustomerConfigurations
		 */
		private void getCustomerConfigurations()
		{
			try {
					User user = null;
				    if(this.user == null) {
				    	user = this.testSessionStatus.getUserDetails(this.userName, this.userName);
				    } else {
				    	user = this.user;
				    }
					 
					Customer customer = user.getCustomer();
					Integer customerId = customer.getCustomerId();
					this.customerConfigurations = this.testSessionStatus.getCustomerConfigurations(this.userName, customerId);
			}
			catch (CTBBusinessException be) {
				be.printStackTrace();
			}
		}
		
		private boolean retrieveInfoFromSession()
	    {
	        boolean success = true;
	        this.userName = (String)getSession().getAttribute("userName");
	        if (this.userName == null)
	        {
	            success = false;
	        }
	        
	                    
	        if (getSession().getAttribute("sessionId") != null)
	            this.sessionId = new Integer((String)getSession().getAttribute("sessionId")); 
	        else if (getRequest().getParameter("sessionId") != null)
	            this.sessionId = new Integer((String)getRequest().getParameter("sessionId")); 
	        else
	            success = false;

	        String sessionFilterTab = "CU";            
	        if (getSession().getAttribute("sessionFilterTab") != null)
	            sessionFilterTab = (String)getSession().getAttribute("sessionFilterTab"); 
	        setTestStatusOptions(sessionFilterTab); 
	                
	        return success;
	    }
		
		private void setTestStatusOptions(String sessionFilterTab)
	    {
	        if (sessionFilterTab.equals("CU")) {    // current
	            testStatusOptions = new String[7];
	            testStatusOptions[0] = FilterSortPageUtils.FILTERTYPE_SHOWALL;
	            testStatusOptions[1] = FilterSortPageUtils.FILTERTYPE_COMPLETED;
	            testStatusOptions[2] = FilterSortPageUtils.FILTERTYPE_INPROGRESS; 
	            testStatusOptions[3] = FilterSortPageUtils.FILTERTYPE_SCHEDULED; 
	            testStatusOptions[4] = FilterSortPageUtils.FILTERTYPE_STUDENTPAUSE;
	            testStatusOptions[5] = FilterSortPageUtils.FILTERTYPE_STUDENTSTOP; 
	            testStatusOptions[6] = FilterSortPageUtils.FILTERTYPE_SYSTEMSTOP; 
	        }
	        else 
	        if (sessionFilterTab.equals("FU")) {    // future
	            testStatusOptions = new String[2];
	            testStatusOptions[0] = FilterSortPageUtils.FILTERTYPE_SHOWALL;
	            testStatusOptions[1] = FilterSortPageUtils.FILTERTYPE_SCHEDULED; 
	        }
	        else {                                  // completed
	            testStatusOptions = new String[6];
	            testStatusOptions[0] = FilterSortPageUtils.FILTERTYPE_SHOWALL;
	            testStatusOptions[1] = FilterSortPageUtils.FILTERTYPE_COMPLETED;
	            testStatusOptions[2] = FilterSortPageUtils.FILTERTYPE_INPROGRESS; 
	            testStatusOptions[3] = FilterSortPageUtils.FILTERTYPE_INCOMPLETE; 
	            testStatusOptions[4] = FilterSortPageUtils.FILTERTYPE_NOTTAKEN; 
	            testStatusOptions[5] = FilterSortPageUtils.FILTERTYPE_STUDENTPAUSE;
	        }
	    }
		
		private boolean isSessionDetailsShowScores() 
	    {               
	        boolean showScores = false; 

	       	for (int i=0; i < this.customerConfigurations.length; i++)
	            {
	                CustomerConfiguration cc = (CustomerConfiguration)this.customerConfigurations[i];
	                if (cc.getCustomerConfigurationName().equalsIgnoreCase("Session_Details_Show_Scores") && cc.getDefaultValue().equalsIgnoreCase("T"))
	                {
	                    showScores = true; 
	                }
	                if (cc.getCustomerConfigurationName().equalsIgnoreCase("Roster_Status_Flag"))
	                {
	                    this.setCustomerFlagToogleButton = "true";
	                     
	                }
	            }
	            this.getRequest().setAttribute("setCustomerFlagToogleButton", setCustomerFlagToogleButton);  
	                
	      
	        return showScores;
	    }
		
		 private boolean isSubtestValidationAllowed()
		    {
		        boolean isValidationAllowed = false; 
		        try
		        {    
		            isValidationAllowed = this.testSessionStatus.allowSubtestInvalidation(this.userName).booleanValue();
		        }
		        catch (CTBBusinessException be)
		        {
		            be.printStackTrace();
		        }   

		        if (isValidationAllowed)
		        {
		            List options = new ArrayList();
		            options.add(FilterSortPageUtils.FILTERTYPE_SHOWALL);
		            options.add(FilterSortPageUtils.FILTERTYPE_INVALID);
		            options.add(FilterSortPageUtils.FILTERTYPE_PARTIALLY_INVALID);
		            options.add(FilterSortPageUtils.FILTERTYPE_VALID);
		            this.validationStatusOptions = (String[])options.toArray(new String[0]);
		        }

		        return isValidationAllowed;
		    }
		 private boolean showStudentReportButton() 
		    {               
		        boolean showButton = false; 

		        for (int i=0; i < this.customerConfigurations.length; i++)
		            {
		                CustomerConfiguration cc = (CustomerConfiguration)this.customerConfigurations[i];
		                if (cc.getCustomerConfigurationName().equalsIgnoreCase("Session_Status_Student_Reports") && cc.getDefaultValue().equalsIgnoreCase("T"))
		                {
		                    showButton = true; 
		                }
		            }     
		      
		        return showButton;
		    }
		 private boolean isDonotScoreAllowed()  {               
	        for (int i=0; i < this.customerConfigurations.length; i++)  {
                CustomerConfiguration cc = (CustomerConfiguration)this.customerConfigurations[i];
                if (cc.getCustomerConfigurationName().equalsIgnoreCase("Do_Not_Score") && cc.getDefaultValue().equalsIgnoreCase("T")) {
                    return true;
                }
            }     
	        return false;
	     }
		 private void initGenerateReportFile() {
		    	
				try {
					User user = this.testSessionStatus.getUserDetails(this.userName, this.userName);
					Date currentDate = new Date();
					String strDate = DateUtils.formatDateToDateString(currentDate);
					strDate = strDate.replace('/', '_');
					String fileName = user.getUserName() + "_" + strDate + ".zip";
			    	setFileName(fileName);
			    	setUserEmail(user.getEmail());
				} catch (CTBBusinessException e) {
					e.printStackTrace();
				}
		    	
		        this.fileTypeOptions = new ArrayList();
		        this.fileTypeOptions.add("One file for all students");
		        this.fileTypeOptions.add("One file per student");
		    	setFileType((String)this.fileTypeOptions.get(0));
		 }
		 
		 private List getInvalidateReasonList(){
			 List ivrc = null;
			 try{
				 ivrc = this.testSessionStatus.getInvalidationReasonList();
			 }catch (CTBBusinessException e) {
					e.printStackTrace();
			 }
			 return ivrc;			 
		 }
		 
		 private boolean hasAssignFormRosterConfig()  {               
		        for (int i=0; i < this.customerConfigurations.length; i++)  {
	                CustomerConfiguration cc = (CustomerConfiguration)this.customerConfigurations[i];
	                if (cc.getCustomerConfigurationName().equalsIgnoreCase("Assign_Roster_Form") && cc.getDefaultValue().equalsIgnoreCase("T")) {	                
	                	return true;	                    
	                }
	            }     
		        return false;
		 }
		 
		 private boolean hasAssignFormRosterTopLevelConfig()  {               
		        for (int i=0; i < this.customerConfigurations.length; i++)  {
	                CustomerConfiguration cc = (CustomerConfiguration)this.customerConfigurations[i];
	                if (cc.getCustomerConfigurationName().equalsIgnoreCase("Assign_Roster_Form_Top_Level") && cc.getDefaultValue().equalsIgnoreCase("T")) {	                
	                	return true;	                    
	                }
	            }     
		        return false;
		 }
		 
		 private boolean hasTopLevelInvalidationOnlyConfig()  {               
		        for (int i=0; i < this.customerConfigurations.length; i++)  {
	                CustomerConfiguration cc = (CustomerConfiguration)this.customerConfigurations[i];
	                if (cc.getCustomerConfigurationName().equalsIgnoreCase("Top_Level_Invalidation_Only") && cc.getDefaultValue().equalsIgnoreCase("T")) {	                
	                	return true;	                    
	                }
	            }     
		        return false;
		 }
		 
		 private List getRosterFormList(String testAdminId){
			 List formList = null;
			 try{
				 formList = this.testSessionStatus.getRosterFormList(testAdminId);
			 }catch (CTBBusinessException e) {
					e.printStackTrace();
			 }
			 return formList;			 
		 }
		 
		 @Jpf.Action(forwards = { 
			        @Jpf.Forward(name = "success",
			                     path = "view_test_session.jsp")
			    })
		 protected Forward updateRosterForm()
		 {
			 Integer testRosterId = Integer.parseInt(getRequest().getParameter("testRosterId"));
			 String assignedForm = getRequest().getParameter("assignedForm");
			 try {      
		            this.testSessionStatus.updateRosterForm(this.userName, testRosterId, assignedForm);
			 }catch (Exception e) {
		            e.printStackTrace();
		     }
			 return null;
		 }
		 boolean isTabeLocatorProduct = false;
		 
		 @Jpf.Action(forwards = { 
		        @Jpf.Forward(name = "success",
		                     path = "view_test_session.jsp")
		    })
		    protected Forward getRosterDetails()
		    {	
				List rosterList = null;
				List invalidateReasonList= null;
		        String testAdminId = getRequest().getParameter("testAdminId");
		        boolean viewStatusForTABEAdult=false;
		        initializeTestSession();
		        if(testAdminId != null){
			        TestProduct tp = getProductForTestAdmin(Integer.valueOf(testAdminId));
			        if(tp!=null){
			        	this.isTabeLocatorProduct = isTabeSession(tp.getProductType());
			        	if(tp.getProductId() != null && tp.getProductId().equals(4201)){
				        	viewStatusForTABEAdult = true;
				        }
			        }
		        }
		        if (testAdminId != null)
		            this.sessionId = Integer.valueOf(testAdminId);
		        RosterElementData red = getRosterForViewTestSession(this.sessionId);
		        rosterList = buildRosterList(red);   
		        invalidateReasonList = getInvalidateReasonList();
		        populateSubtestDetails(this.sessionId);
		        
		        Base base = new Base();
				base.setPage("1");
				base.setRecords("10");
				base.setTotal("2");
				List <Row> rows = new ArrayList<Row>();
				base.setRosterElement(rosterList);
				base.setSubtestValidationAllowed(this.subtestValidationAllowed);
				base.setDonotScoreAllowed(isDonotScoreAllowed());
				base.setInvalidateReasonList(invalidateReasonList);
				base.setOkCustomer(this.isOKCustomer);
				base.setTopLevelAdmin(isAdminUser() && isTopLevelUser());
				base.setTopLevelAdminCord(isAdminCoordinatotUser() && isTopLevelUser());
				base.setHasAssignFormRosterTopLevelConfig(hasAssignFormRosterTopLevelConfig());
				base.setHasTopLevelInvalidationOnlyConfig(hasTopLevelInvalidationOnlyConfig());
				base.setTopLevelUser(isTopLevelUser());
				base.setHasAssignFormRosterConfig(hasAssignFormRosterConfig());
				base.setViewStatusForTABEAdult(viewStatusForTABEAdult);
				if (hasAssignFormRosterConfig()) {
					base.setAssignFormList(getRosterFormList(testAdminId));
				}
				
			
				if(this.hasShowRosterAccomAndHierarchy){
					Map<Integer,Map> accomodationMap = new HashMap<Integer, Map>();
					HashMap<Integer,ArrayList<ClassHierarchy>> orgNodeIdMap = new HashMap<Integer,ArrayList<ClassHierarchy>>();
					if(null != red){
						buildRosterList(red.getRosterElements(), accomodationMap);
					}
					base.setAccomodationMap(accomodationMap);
					try {
						orgNodeIdMap = this.testSessionStatus.buildOrgNodeIdMap(this.userName,this.sessionId);
					} catch (CTBBusinessException e) {
						e.printStackTrace();
					}
					base.setOrgNodeIdMap(orgNodeIdMap);
					base.setHasShowRosterAccomAndHierarchyConfig(this.hasShowRosterAccomAndHierarchy);
				}
				
				/*Integer breakCount = ted.getBreakCount();
		        if ((breakCount != null) && (breakCount.intValue() > 0)) {
		            if (isSameAccessCode(subtestList)) 
		            	base.setHasBreak("singleAccesscode");
		            else
		            	base.setHasBreak("multiAccesscodes");
		        } else {
		        	base.setHasBreak("false");
		        }
*/
		        TestSessionVO testSession = getTestSessionDetails(this.sessionId);
		        base.setTestSession(testSession);
		        createGson(base);
		        return null;
		    }

		 	
			private void populateSubtestDetails(Integer testAdminId) {
				
				this.subtestDetails = new ArrayList(); 
				try {
					ScheduledSession scheduledSession = this.scheduleTest.getScheduledSessionDetails(this.userName, testAdminId);
					TestElement[] testElements = scheduledSession.getScheduledUnits();
					if(testElements.length == 1 && testElements[0].getItemSetName().toUpperCase().contains("LOCATOR")){
						this.isTABELocatorOnlyTest = true;
					}else{
						this.isTABELocatorOnlyTest = false;
					}
					Integer sequence = Integer.valueOf(1);
			        for (int i=0; i < testElements.length; i++) {
			            TestElement te = testElements[i];
			            if(te.getSessionDefault().equals("T")){
			            	te.setItemSetLevel(sequence.toString());
			            	sequence++;
			            }
			            String duration = "Untimed";
			            if (te.getTimeLimit() != null && te.getTimeLimit().intValue() > 0)
			                duration = String.valueOf(te.getTimeLimit().intValue() / 60) + " mins";
			            te.setMediaPath(duration);
			            String hasLocatorTD = "-";
			           // System.out.println("te.getIslocatorChecked()"+te.getIslocatorChecked());
			            if(te.getIslocatorChecked() != null && "T".equalsIgnoreCase(te.getIslocatorChecked())){
			            	
			            	hasLocatorTD = "Yes";
			            }else if(te.getIslocatorChecked() != null && "F".equalsIgnoreCase(te.getIslocatorChecked())){
			            	
			            	hasLocatorTD = "No";
			            }
			          
			            te.setIslocatorChecked(hasLocatorTD);
			            this.subtestDetails.add(te);	            
			        }
		        }
		        catch (CTBBusinessException e) {
		            e.printStackTrace();
		        }
			}
	
	
		    @Jpf.Action(forwards = { 
		            @Jpf.Forward(name = "success", path = "subtest_details.jsp") 
		        })
		    protected Forward showSubtestDetails()
		    {
		        this.getSession().setAttribute("subtestDetails", this.subtestDetails);
		      
		    	this.getSession().setAttribute("isTABE", this.isTabeLocatorProduct);
		    	this.getSession().setAttribute("tabeLocatorOnlyTest", this.isTABELocatorOnlyTest);
		    	 // System.out.println("getSession().getAttribute"+getSession().getAttribute("isTABE"));
		        return new Forward("success");
		    }			
		    
		    
		    /**
		     * @jpf:action
		     * @jpf:forward name="success" path="view_scores_detail.jsp"
		     */ 
		    @Jpf.Action(forwards = { 
			        @Jpf.Forward(name = "success",
			                     path = "view_score_details.jsp")
			    })
		    protected Forward getScoreDetails() {
		    	Integer testRosterID = Integer.valueOf(getRequest().getParameter("testRosterId"));		    	
		    	Base base = prepareScoreListDetailInformation(testRosterID);
		    	//Create a Gson object with field exclusion strategy:
		    	Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy(){
					@Override
					public boolean shouldSkipClass(Class<?> arg0) {
						return false;
					}
					@Override
					public boolean shouldSkipField(FieldAttributes arg0) {
						return (arg0.getName() == "deliverableUnit" || arg0.getName() == "itemSetIdTD" ||
								arg0.getName() == "itemSetNameTD" || arg0.getName() == "completionStatusTD" || 
								arg0.getName() == "testIdsToBeShown" || arg0.getName() == "pdfResponse");
					}
		    		
		    	}).serializeNulls().create();
		    	createGson(base, gson);
		        return null;
		    }
		    
		    private Base prepareScoreListDetailInformation(Integer testRosterID) {
		    	Base base = new Base();
		        RosterElement re = getTestRosterDetails(testRosterID);
		        this.sdForAllSubtests = buildStudentStatusScore(this.sessionId,re);
		        base.setStudentName(re.getFirstName() + " " + re.getLastName());
		        base.setLoginName(re.getUserName());
		        base.setPassword(re.getPassword());
		        base.setTestStatus(FilterSortPageUtils.testStatus_CodeToString(re.getTestCompletionStatus()));
		        base.setTestName(sdForAllSubtests.get(0).getTestName());
		        base.setTestSessionName(sdForAllSubtests.get(0).getTestSessionName());
	            base.setTestLevel(sdForAllSubtests.get(0).getItemSetLevel());
	            base.setSubtestScoreElements(sdForAllSubtests);
		    	return base;
			}
		    
		    private List<ScoreDetails> buildStudentStatusScore(Integer testAdminId,	RosterElement re) {
				Integer testRosterId = re.getTestRosterId();
				Integer studentId = re.getStudentId();
				List<ScoreDetails> scoreList = new ArrayList<ScoreDetails>();
				
				ScoreDetails[] scoreDetails = getAllItemSetForRoster(this.userName,	testRosterId);
				String itemSetIds = scoreDetails[0].getTestIds();
				String productType = scoreDetails[0].getProductType();
				if(null != itemSetIds && null != productType){
					ItemResponseAndScore[] itemRespone = getScoreElementsForTS(
							this.userName, itemSetIds, testRosterId, studentId,
							testAdminId, productType);
					if (itemRespone != null) {
						for (int i = 0; i < scoreDetails.length; i++) {
							ScoreDetails sd = scoreDetails[i];
							int pointsObtained=0, pointsPossible=0;
							List<ResponseResultDetails> responseList = new ArrayList<ResponseResultDetails>();
							for (int j = 0; j < itemRespone.length; j++) {
								ItemResponseAndScore obj = itemRespone[j];
								if (sd.getItemSetId().intValue() == obj.getItemSetIdTS().intValue()) {
									responseList.add(new ResponseResultDetails(obj));
									if(null != obj.getRawScore() && (!"N/A".equalsIgnoreCase(obj.getRawScore()))){
										pointsObtained+=Integer.valueOf(obj.getRawScore()).intValue();
									}
									if(null != obj.getPossibleScore()){
										pointsPossible+=Integer.valueOf(obj.getPossibleScore()).intValue();
									}
								}
							}
							sd.setResponseStatus(pointsObtained, pointsPossible);
							Collections.sort(responseList, new OrderByItemOrder());
							sd.setResponseList(responseList.toArray(new ResponseResultDetails[responseList.size()]));
							scoreList.add(sd);
						}
					}
				}
				Collections.sort(scoreList, new OrderByItemSetOrder());
				return scoreList;
			}
		

			private ScoreDetails[] getAllItemSetForRoster(String userName, Integer testRosterId) {
				ScoreDetails[] itemSetDetails = null;
				try {
					itemSetDetails = this.testSessionStatus.getAllItemSetForRoster(
							userName, testRosterId);
				} catch (CTBBusinessException be) {
					be.printStackTrace();
				}
				return itemSetDetails;
			}
		
			private ItemResponseAndScore[] getScoreElementsForTS(String userName,
					String parentItemSetId, Integer testRosterId, Integer studentId,
					Integer testAdminId, String productType) {
		
				ItemResponseAndScore[] scoreList = null;
				try {
					scoreList = this.testSessionStatus.getScoreElementsForTS(userName,
							parentItemSetId, testRosterId, studentId, testAdminId, productType);
				} catch (CTBBusinessException be) {
					be.printStackTrace();
				}
				return scoreList;
			}
		    
					    
			/**
		     * @jpf:action
		     * @jpf:forward name="success" path="view_subtests_detail.jsp"
		     */ 
		    @Jpf.Action(forwards = { 
		        @Jpf.Forward(name = "success",
		                     path = "view_subtests_detail.jsp")
		    })
		    protected Forward getSubtestDetails() {
		    	Integer testRosterID = Integer.valueOf(getRequest().getParameter("testRosterId"));
		    	String[] selectedItemSetIds = getRequest().getParameterValues("selectedItemSetIds");
		    	Base base = prepareSubtestsDetailInformation(testRosterID, selectedItemSetIds, this.subtestValidationAllowed);
		    	base.setSubtestValidationAllowed(this.subtestValidationAllowed);
				base.setHasTopLevelInvalidationOnlyConfig(hasTopLevelInvalidationOnlyConfig());
				base.setTopLevelUser(isTopLevelUser());
				base.setTopLevelAdmin(isAdminUser() && isTopLevelUser());
				base.setTopLevelAdminCord(isAdminCoordinatotUser() && isTopLevelUser());
		    	createGson(base);
		        return null;
		    }
		    
		    private boolean isTabeAdaptiveSession(String productType) {
		        if (productType.equalsIgnoreCase("TA"))
		            return true;   
		        else             
		            return false;
		    }
		    
		    protected Base prepareSubtestsDetailInformation(Integer testRosterID, 
		    						String[] selectedItemSetIds, boolean validation) {
		    	Base base = new Base();
		        RosterElement re = getTestRosterDetails(testRosterID);
		        TestSessionVO testSession = getTestSessionDetails(this.sessionId);
		        TestProduct testProduct = getProductForTestAdmin(this.sessionId);
		        boolean isTabeSession = isTabeSession(testProduct.getProductType());
		        boolean isTabeLocatorSession = isTabeLocatorSession(testProduct.getProductType());
		        boolean isTabeAdaptiveSession = isTabeAdaptiveSession(testProduct.getProductType());
		        boolean isTabeCCSSSession = isTabeCCSSSession(testProduct.getProductType());
		        
		        // START- Added for LLO-109 
		        isLasLinkCustomer();
		        boolean isLaslinkSession  = this.isLasLinkCustomer;
		        boolean testSessionCompleted = isTestSessionCompleted(testSession);
		        this.studentStatusSubtests = buildStudentStatusSubtests(re.getStudentId(), this.sessionId, testSessionCompleted, (isTabeSession || isTabeAdaptiveSession), isTabeLocatorSession, isLaslinkSession);       
		       
		        String testGrade = getTestGrade(this.studentStatusSubtests);
		        if (testGrade == null) testGrade = "--";
		        String testLevel = getTestLevel(this.studentStatusSubtests);
		        if (testLevel == null) testLevel = "--";
		        
		        base.setStudentName(re.getFirstName() + " " + re.getLastName());
		        base.setLoginName(re.getUserName());
		        base.setPassword(re.getPassword());
		        base.setTestSession(testSession);
		        base.setTestStatus(FilterSortPageUtils.testStatus_CodeToString(re.getTestCompletionStatus()));
		        base.setTestElement(this.studentStatusSubtests);
		        if ((! isTabeSession) && (! isTabeAdaptiveSession)) {
		            if (!testGrade.equals("--")) {
		            	base.setTestGrade(testGrade);
		            }
		            if (!testLevel.equals("--")) {
		            	base.setTestLevel(testLevel);
		            }
		        }
		        boolean showStudentFeedback = false;
		        if ((testSession.getShowStudentFeedback() != null) 
		        		&& (testSession.getShowStudentFeedback().equalsIgnoreCase("T"))) {
		            showStudentFeedback = true;
		        }
		        boolean isShowScores = this.sessionDetailsShowScores;
		        base.setShowScores(isShowScores);
		        
		        int numberColumn = 4;
		        if (isTabeSession)
		            numberColumn += 1;
		        if (isShowScores)
		            numberColumn += 3;
		        if (isLaslinkSession)
		        	numberColumn += 3;
		        	
		       // END- Added for LLO-109 
		        if (validation) {
		            numberColumn += 1;
		            if (this.setCustomerFlagToogleButton.equals("true"))
		                numberColumn += 1;
		            prepareValidateButtons(selectedItemSetIds);
		        }
		        base.setNumberColumn(numberColumn);
		        base.setSubtestValidationAllowed(this.subtestValidationAllowed);
		        base.setTabeSession(isTabeSession);
		        base.setTabeCCSSSession(isTabeCCSSSession);
		        base.setLaslinkSession(isLaslinkSession);
		        return base;
		    }
		    
		    private List buildStudentStatusSubtests(Integer studentId, Integer testAdminId, boolean testSessionCompleted, boolean isTabeSession, boolean isTabeLocatorSession,boolean isLaslinkSession)
		    {
		    	   
		        String userTimeZone = this.user.getTimeZone();//(String)getSession().getAttribute("userTimeZone"); 
		        List subtestList = new ArrayList();        
		        TestElementData ted = getTestElementsForTestSession(testAdminId);
		        StudentSessionStatus[] ssss = getStudentItemSetStatusesForRoster(studentId, testAdminId);                 
		        TestElement[] subtestelements = ted.getTestElements(); 
		        HashMap recLevelHM = new HashMap();
		        if (isTabeSession) {
		            subtestelements = orderedSubtestList(subtestelements, studentId, testAdminId);
		        }  
		        boolean isAllLocatorCompleted = false ;		       
		        boolean isLocatorTD = false;
		        
		        isAllLocatorCompleted = getLocatorCompletionStatus(studentId, testAdminId);
		        
		        for (int i=0; i < subtestelements.length; i++)
		        {
		            TestElement te = subtestelements[i];          
		           
		            if (te != null)
		            {
		                SubtestDetail sd_TS = new SubtestDetail(te, i + 1);
		          
		                TestElement[] tes = getTestElementsForParent(sd_TS.getItemSetId(), "TD"); 
		                boolean addTS = true;               
		                HashMap subTestHM = new HashMap();
		                for (int j=0; j < ssss.length; j++)
		                {
		                    StudentSessionStatus sss = ssss[j];
		                    
		                    for (int k=0; k < tes.length; k++)
		                    {
		                        TestElement te_TD = tes[k];

		                        if (isTabeSession)
		                        {
		                            addTABESubtest(te_TD);
		                        }
		                        
		                        if (sss.getItemSetId().intValue() == te_TD.getItemSetId().intValue())
		                        {
		                            
		                            SubtestDetail sd_TD = new SubtestDetail(te_TD, -1);
		                            
		                            sd_TD.setValidationStatus(FilterSortPageUtils.validationStatus_CodeToString(sss.getValidationStatus()));
		                            sd_TD.setCustomStatus(FilterSortPageUtils.customStatus_ToString(sss.getCustomerFlagStatus()));
		                            
		                            if (addTS)
		                            {
		                                if (sd_TD.getSubtestName().indexOf("Locator") < 0)
		                                {                                    
		                                    subtestList.add(sd_TS); 
		                                    addTS = false;                                                         
		                                }
		                                else
		                                {  
		                                    isLocatorTD = true;                                      
		                                }                                
		                            }
		                            
		                            if (isTabeSession)
		                            {
		                                String level = te_TD.getItemSetForm();
		                                if ((level == null) || level.equals("1"))
		                                    level = "";
		                                sd_TD.setLevel(level);
		                            }
		                            
		                            
		                            String status = FilterSortPageUtils.testStatus_CodeToString(sss.getCompletionStatus());
		                            if (testSessionCompleted)
		                            {
		                                if (status.equals(FilterSortPageUtils.FILTERTYPE_SCHEDULED))
		                                {
		                                    status = FilterSortPageUtils.FILTERTYPE_NOTTAKEN; 
		                                }
		                                if (status.equals(FilterSortPageUtils.FILTERTYPE_SYSTEMSTOP) || status.equals(FilterSortPageUtils.FILTERTYPE_STUDENTSTOP) || status.equals(FilterSortPageUtils.FILTERTYPE_INPROGRESS))
		                                {
		                                    status = FilterSortPageUtils.FILTERTYPE_INCOMPLETE; 
		                                }
		                            }
		                            sd_TD.setCompletionStatus(status);
		                                                        
		                            if (sss.getStartDateTime() != null)
		                            {                                
		                                Date adjStartDate = com.ctb.util.DateUtils.getAdjustedDate(sss.getStartDateTime(), "GMT", userTimeZone, sss.getStartDateTime());
		                                String startDate = DateUtils.formatDateToDateString(adjStartDate);
		                                String startTime = DateUtils.formatDateToTimeString(adjStartDate);                                
		                                sd_TD.setStartDate(startDate + " " + startTime);
		                            }
		                            
		                            if (sss.getCompletionDateTime() != null)
		                            {
		                                Date adjEndDate = com.ctb.util.DateUtils.getAdjustedDate(sss.getCompletionDateTime(), "GMT", userTimeZone, sss.getCompletionDateTime());
		                                String endDate = DateUtils.formatDateToDateString(adjEndDate);
		                                String endTime = DateUtils.formatDateToTimeString(adjEndDate);                                
		                                sd_TD.setEndDate(endDate + " " + endTime);
		                            }
		                            
		                            sd_TD.setMaxScore(sss.getMaxScore());
		                            sd_TD.setRawScore(sss.getRawScore());
		                            sd_TD.setUnScored(sss.getUnscored());
		                            String tdSubtestName = sd_TD.getSubtestName();
		                            String sn = "&nbsp;&nbsp;" +
		                                        tdSubtestName;
		                            sd_TD.setSubtestName(sn);
		                               
		                            // START- Added for LLO-109 
		                            if(isLaslinkSession)
		                            {
		                            	sd_TD.setInvalidationReason(sss.getInvalidationReason());
		                               	sd_TD.setTestExemptions(sss.getTestExemptions());
		                            	sd_TD.setAbsent(sss.getAbsent());
		                             }
		                             // END- Added for LLO-109 
		                             
		                            if (!isLocatorTD)
		                            {                                
		                                subtestList.add(sd_TD);
		                            }
		                            else
		                            {
		                                if (!subTestHM.containsValue(tdSubtestName))
		                                {
		                                    if (addTS)
		                                    {                                    
		                                        subtestList.add(sd_TS); 
		                                        addTS = false;
		                                    }
		                                    if (status.equals(FilterSortPageUtils.FILTERTYPE_COMPLETED))
		                                    {
		                                        if (sss.getRecommendedLevel() != null)
		                                        {   
		                                            if (tdSubtestName.indexOf("Reading") > 0)                                         
		                                                recLevelHM.put("Reading", sss.getRecommendedLevel());
		                                            else if (tdSubtestName.indexOf("Mathematics Computation") > 0)
		                                                recLevelHM.put("Mathematics Computation", sss.getRecommendedLevel());
		                                            else if (tdSubtestName.indexOf("Applied Mathematics") > 0)
		                                                recLevelHM.put("Applied Mathematics", sss.getRecommendedLevel());
		                                            else if (tdSubtestName.indexOf("Language") > 0)
		                                                recLevelHM.put("Language", sss.getRecommendedLevel());
		                                        }
		                                    }
		                                    else if (null != te.getIslocatorChecked()  &&  ("F").equalsIgnoreCase(te.getIslocatorChecked()) && isAllLocatorCompleted){
		                                    	//Do nothing...
		                                    }
		                                    else
		                                        sd_TD.setLevel("");
		                                    
		                                    String subtestName = tdSubtestName.substring(5, tdSubtestName.length()).trim();
		                                    if (subtestName.indexOf("Sample") > 0)
		                                    {
		                                        int indexOfSample = subtestName.indexOf("Sample");
		                                        subtestName = subtestName.substring(0, indexOfSample).trim();
		                                    }
		                                    if (recLevelHM.size() > 0)
		                                    {                                      
		                                        if (recLevelHM.containsKey(subtestName))
		                                        {
		                                            if (subtestName.indexOf("Mathematics Computation") >= 0 || subtestName.indexOf("Applied Mathematics") >= 0)
		                                            {                                        
		                                                if (recLevelHM.containsKey("Mathematics Computation") && recLevelHM.containsKey("Applied Mathematics"))
		                                                {
		                                                    sd_TD.setLevel(recLevelHM.get(subtestName).toString());
		                                                }                                               
		                                            }
		                                            else
		                                            {
		                                                sd_TD.setLevel(recLevelHM.get(subtestName).toString());
		                                            }
		                                        }
		                                        if (subtestName.indexOf("Vocabulary") >= 0 && recLevelHM.containsKey("Reading"))
		                                            sd_TD.setLevel(recLevelHM.get("Reading").toString());
		                                        if (recLevelHM.containsKey("Language"))
		                                        {
		                                            if (subtestName.indexOf("Language Mechanics") >= 0)
		                                                sd_TD.setLevel(recLevelHM.get("Language").toString());
		                                            else if (subtestName.indexOf("Spelling") >= 0)
		                                                sd_TD.setLevel(recLevelHM.get("Language").toString());
		                                        }                                   
		                                    }
		                                        
		                                    subtestList.add(sd_TD);  
		                                    subTestHM.put(sd_TD, tdSubtestName);
		                                }                              
		                            }
		                            break;
		                            
		                        }
		                    }
		                }
		            }
		        }                                        
		        return subtestList;
		    }
		    
		 @Jpf.Action(forwards = { 
	        @Jpf.Forward(name = "success",
	                     path = "view_subtest_details.jsp")
	     })
		 protected Forward toggleValidationStatus() {
	        Integer testRosterId = Integer.parseInt(getRequest().getParameter("testRosterId"));
			try {      
	            this.testSessionStatus.toggleRosterValidationStatus(this.userName, testRosterId);
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
		}
			
		@Jpf.Action(forwards = { 
		    @Jpf.Forward(name = "success",
	                     path = "view_subtest_details.jsp")
	    })
		 protected Forward toggleSubtestValidationStatus()  {       
			String strItemSetIds = null;
			String[] itemSetIdsList = null;
			String subtestIdAndReasonStr = null;
			String [] subtestIdAndReasonList = null;
			if(getRequest().getParameter("subtestIdAndReasonList") != null){
				subtestIdAndReasonStr = getRequest().getParameter("subtestIdAndReasonList");
				subtestIdAndReasonList = subtestIdAndReasonStr.split("\\|");
			}
			Base base = new Base();
            TestProduct testProduct = getProductForTestAdmin(this.sessionId);
            boolean isTabeSession = isTabeSession(testProduct.getProductType());
            base.setTabeSession(isTabeSession);
            Integer testRosterId = Integer.parseInt(getRequest().getParameter("testRosterId"));
	        if(getRequest().getParameter("itemSetIds") != null){
	        	strItemSetIds = getRequest().getParameter("itemSetIds");
	        	itemSetIdsList = strItemSetIds.split("\\|");
	        }
	        Integer[] itemSetIds = new Integer[itemSetIdsList.length];
	        for(int i=0; i<itemSetIdsList.length; i++){
	        	itemSetIds[i] = Integer.valueOf(itemSetIdsList[i]);
	        }
	        try {
	        	this.testSessionStatus.toggleSubtestValidationStatus(this.userName, testRosterId, subtestIdAndReasonList, "ValidationStatus" );
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
	        createGson(base);
 		    return null;
		}

	//Added for view/monitor test status: End
		
		@Jpf.Action(forwards = { 
	        @Jpf.Forward(name = "success",
	                     path = "view_subtest_details.jsp")
	     })
		 protected Forward toggleDonotScoreStatus() {
	        Integer testRosterId = Integer.parseInt(getRequest().getParameter("testRosterId"));
	        String dnsStatus = getRequest().getParameter("dnsStatus");
	        try {      
	            this.testSessionStatus.updateDonotScore(testRosterId, dnsStatus, this.user.getUserId());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
		}
		
	    private boolean isDeleteSessionEnable() 
	    {               
	        String roleName = this.user.getRole().getRoleName();        
	        return (roleName.equalsIgnoreCase(PermissionsUtils.ROLE_NAME_ADMINISTRATOR) ||
	                roleName.equalsIgnoreCase(PermissionsUtils.ROLE_NAME_ACCOMMODATIONS_COORDINATOR) ||
	                roleName.equalsIgnoreCase(PermissionsUtils.ROLE_NAME_COORDINATOR));
	    }
	    
		@Jpf.Action
	    protected Forward deleteTest(SessionOperationForm form)
	    {
			String jsonData = "";
    		OutputStream stream = null;
    		HttpServletResponse resp = getResponse();
    	    resp.setCharacterEncoding("UTF-8"); 
			Integer testAdminId = Integer.valueOf(getRequest().getParameter("testAdminId"));
			boolean hasStudentLoggedIn = false;
			OperationStatus status = new OperationStatus();
			try {
				ScheduledSession scheduledSession = this.scheduleTest.getScheduledSessionDetails(this.userName, testAdminId);
				int studentsLoggedIn = scheduledSession.getStudentsLoggedIn() == null ? 0 : scheduledSession.getStudentsLoggedIn().intValue();
				if(studentsLoggedIn > 0){
					hasStudentLoggedIn = true;
				}
				if(!hasStudentLoggedIn){
					this.scheduleTest.deleteTestSession(this.userName, testAdminId);
	            	status.setSuccess(true);
				}
	            else{
					status.setSuccess(false);
	            }
	        }
	        catch (CTBBusinessException e) {
	            e.printStackTrace();
	            //this.getRequest().setAttribute("errorMessage",MessageResourceBundle.getMessage("SelectSettings.FailedToDeleteTestSession", e.getMessage()));               
	        }
			Gson gson = new Gson();
			jsonData = gson.toJson(status);
//			System.out.println(jsonData);
			try {
				resp.setContentType(CONTENT_TYPE_JSON);
				stream = resp.getOutputStream();
				stream.write(jsonData.getBytes("UTF-8"));
				resp.flushBuffer();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        return null;
	    }
		
		@Jpf.Action
	    protected Forward updateManifestForRoster(SessionOperationForm form)
	    {
			
			String jsonData = "";
    		OutputStream stream = null;
    		HttpServletResponse resp = getResponse();
    	    resp.setCharacterEncoding("UTF-8"); 
			boolean hasStudentLoggedIn = false;
			OperationStatus status = new OperationStatus();
			SuccessInfo successInfo = new SuccessInfo();
			StudentManifestData manifestData = new StudentManifestData(); 
			ValidationFailedInfo validationFailedInfo = new ValidationFailedInfo();
			try {
				String testAdminIdString = RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.TEST_ADMIN_ID, true, "-1");
				String studentIdString   = RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.STUDENT_ID, true, "-1");
				String studentOrgNodeIdString   = RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.STUDENT_ORG_NODE_ID, true, "-1");
				Integer testAdminId      = Integer.valueOf(testAdminIdString);
				Integer studentId        = Integer.valueOf(studentIdString);	
				Integer studentOrgNodeId = Integer.valueOf(studentOrgNodeIdString);
				
				String[] itemSetIds   = RequestUtil.getValuesFromRequest(this.getRequest(), RequestUtil.TEST_ITEM_SET_ID_TD, true, new String[0]);
				String[] levels       = RequestUtil.getValuesFromRequest(this.getRequest(), RequestUtil.TEST_ITEM_SET_FORM, true, new String[itemSetIds.length]);
				String[] subtestNames = RequestUtil.getValuesFromRequest(this.getRequest(), RequestUtil.SUB_TEST_NAME, true, new String[itemSetIds.length]);
				String autoLocator	  = RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.HAS_AUTOLOCATOR, true, "false");
				String[] locatorSubtestTDs = RequestUtil.getValuesFromRequest(this.getRequest(), "locatorSubtestTDs", true ,  new String [0]); 
				
				/*Map<Integer,String> locatorItemSetTDMap = new HashMap<Integer,String>();
	        	 for(int indx =0; indx<locatorTDsForTABE.length; indx++){
	        		 String[] strArr = locatorTDsForTABE[indx].split("~");
	        		 Integer TDid = Integer.valueOf(strArr[0].trim());
	        		 String testName = strArr[1].trim();
	        		 System.out.println("testNameLocator ::"+testName);
	        		 locatorItemSetTDMap.put(TDid, testName);
	        	 }*/
				int subtestSize       = itemSetIds.length;
				int order             = 0;
				boolean hasAutoLocator = false;
				String [] locatorSubtests = null;
				StudentManifest locatorManifest = null;
				TestProduct tp = scheduleTest.getProductForTestAdmin(this.userName, testAdminId);
				String productType = TestSessionUtils.getProductType(tp.getProductType());
				if(autoLocator.equalsIgnoreCase("true") ){
					Integer locatorItemSetId = Integer.valueOf(RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.LOCATOR_TEST_ITEM_SET_ID_TD, false, null));
					String locatorItemSetName = RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.LOCATOR_SUB_TEST_NAME, false, null);
					locatorManifest = new StudentManifest(); 
					locatorManifest.setItemSetId(locatorItemSetId);
					locatorManifest.setItemSetOrder(0);
					locatorManifest.setItemSetName(locatorItemSetName);
					subtestSize = subtestSize+1;
					order = order+1;
					hasAutoLocator = true;
				}
				
				 StudentManifest [] manifestArray = new StudentManifest[subtestSize];
				 if(hasAutoLocator){
					 manifestArray[0] = locatorManifest;
					 locatorSubtests = locatorSubtestTDs;
				 }
				 for(int ii=0; ii<itemSetIds.length; ii++ ,order++){
					 StudentManifest manifest = new StudentManifest(); 
					 manifest.setItemSetId(Integer.valueOf(itemSetIds[ii]));
					 if(!hasAutoLocator && TestSessionUtils.isTabeProduct(productType)){
						 manifest.setItemSetForm(levels[ii]); 
					 }
					 manifest.setItemSetOrder(order);
					 manifest.setItemSetName(subtestNames[ii]);
					 manifestArray[order]=manifest;
				 }
				 manifestData.setStudentManifests(manifestArray , new Integer(manifestArray.length));
				 try {
					 validateStudentManifest(studentId, testAdminId, manifestData, tp, validationFailedInfo);
					 if(!validationFailedInfo.isValidationFailed()){
						 scheduleTest.updateManifestForRoster(this.userName, studentId, null, testAdminId, manifestData, locatorSubtests);
						 String messageHeader = MessageResourceBundle.getMessage("Modify.Student.Manifest.SaveMessage.Header");
						 successInfo.setKey("MODIFY_STUDENT_MANIFEST_SAVED");
			           	 successInfo.setMessageHeader(messageHeader);
			           	 status.setSuccess(true); 
		        	   	 status.setSuccessInfo(successInfo);
					 }
					
				 } catch(InsufficientLicenseQuantityException e){
					 e.printStackTrace();
					 String errorMessageHeader =  MessageResourceBundle.getMessage("FailedToSaveStudentManifest");
		             String errorMessageBody =  MessageResourceBundle.getMessage("SelectSettings.InsufficentLicenseQuantity.E001.Body");
		             validationFailedInfo.setMessageHeader(errorMessageHeader);
		             validationFailedInfo.updateMessage(errorMessageBody);
		             validationFailedInfo.setKey("SYSTEM_EXCEPTION");
				}catch(ManifestUpdateFailException e){
					 e.printStackTrace();
					 String errorMessageHeader =  MessageResourceBundle.getMessage("FailedToSaveStudentManifest");
		             validationFailedInfo.setMessageHeader(errorMessageHeader);
		             validationFailedInfo.setKey("SYSTEM_EXCEPTION");
				}catch(Exception e){
					 e.printStackTrace();
					 String errorMessageHeader = MessageResourceBundle.getMessage("FailedToSaveStudentManifest");
		             validationFailedInfo.setKey("SYSTEM_EXCEPTION");
		             validationFailedInfo.setMessageHeader(errorMessageHeader);
				 }
				
			}catch ( Exception e){
				e.printStackTrace();
				String errorMessageHeader = MessageResourceBundle.getMessage("FailedToSaveStudentManifest");
	            validationFailedInfo.setKey("SYSTEM_EXCEPTION");
	            validationFailedInfo.setMessageHeader(errorMessageHeader);
			}
			
			if(validationFailedInfo.isValidationFailed()){
				status.setValidationFailedInfo(validationFailedInfo);
				if(validationFailedInfo.getKey().equalsIgnoreCase("SYSTEM_EXCEPTION")){
					status.setSystemError(true);
				}
			}
			
			Gson gson = new Gson();
			jsonData = gson.toJson(status);
			try {
				resp.setContentType(CONTENT_TYPE_JSON);
				stream = resp.getOutputStream();
				stream.write(jsonData.getBytes("UTF-8"));
				resp.flushBuffer();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
	    }
		

		private void validateStudentManifest(Integer studentId,
				Integer testAdminId, StudentManifestData manifestData, TestProduct tp, ValidationFailedInfo validationFailedInfo) {
			if(testAdminId == -1 || studentId == -1 ) {
				String errorMessageHeader = MessageResourceBundle.getMessage("FailedToSaveStudentManifest");
                validationFailedInfo.setKey("SYSTEM_EXCEPTION");
                validationFailedInfo.setMessageHeader(errorMessageHeader);
			} else {
				String productType = TestSessionUtils.getProductType(tp.getProductType());
				if(!TestSessionUtils.isTabeOrTabeAdaptiveProduct(productType) ){
					String errorMessageHeader = MessageResourceBundle.getMessage("FailedToSaveStudentManifest");
	                validationFailedInfo.setKey("SYSTEM_EXCEPTION");
	                validationFailedInfo.setMessageHeader(errorMessageHeader);
				}
					
				
			}
		}

		public Map<Integer, Map> getSessionListCUFUMap() {
			return sessionListCUFUMap;
		}

		public void setSessionListCUFUMap(Map<Integer, Map> sessionListCUFUMap) {
			this.sessionListCUFUMap = sessionListCUFUMap;
		}

		public Map<Integer, Map> getSessionListPAMap() {
			return sessionListPAMap;
		}

		public void setSessionListPAMap(Map<Integer, Map> sessionListPAMap) {
			this.sessionListPAMap = sessionListPAMap;
		}
//**Changes for copy test session
		
		public List<UserProfileInformation> buildProctorListForCopySession(User schedular, User[] users) {
	        ArrayList<UserProfileInformation> userList = new ArrayList<UserProfileInformation>();
	        boolean isDefaultSchedularExists = false;
	        if (users != null) {
	            //User[] users = uData.getUsers();
	            if(users != null){
	                for (int i=0 ; i<users.length ; i++) {
	                    User user = users[i];
	                    if (user != null && user.getUserName() != null) {
	                        UserProfileInformation userDetail = new UserProfileInformation(user);
	                        if(schedular.getUserId().intValue() == user.getUserId().intValue()){
	                        	userDetail.setDefaultScheduler("T");
	                        	isDefaultSchedularExists = true;
	                        	
	                        }else {
	                        	userDetail.setDefaultScheduler("F");
	                        }
	                        if ("T".equals(user.getCopyable())){
	                        	userDetail.setEditable("T");
	                        	userList.add(userDetail);
	                        }
	                        //userList.add(userDetail);
	                    }
	                }
	            }
	        }
	        if(!isDefaultSchedularExists){
	        	 UserProfileInformation userDetail = new UserProfileInformation(this.user);
	        	 userDetail.setDefaultScheduler("T");
	        	 userList.add(userDetail);
	        }
	        
	        return userList;
	    }
		
	    private List<SessionStudent> buildStudentListForCopySession(SessionStudent [] sessionStudents, Map<Integer, Map> accomodationMap, String action) 
	    {
	        List<SessionStudent> studentList = new ArrayList<SessionStudent>();
	        Map<String,String> innerMap;
	        for (int i=0 ; i<sessionStudents.length; i++) {
	        	innerMap = new HashMap<String,String>();
	            SessionStudent ss = (SessionStudent)sessionStudents[i];
	            if(ss.getStatus()!=null) {
	            	if(action != null && action.equals("copySession")){
	            		if ("T".equals(ss.getStatus().getCopyable())){
	            			ss.setStatusEditable("T");
	            			ss.setStatusCopyable(ss.getStatus().getCopyable());
	            		}else{
	            			ss.setStatusEditable(ss.getStatus().getEditable());
	    	            	ss.setStatusCopyable(ss.getStatus().getCopyable());
	            		}
	            	}else{
		            	ss.setStatusEditable(ss.getStatus().getEditable());
		            	ss.setStatusCopyable(ss.getStatus().getCopyable());
	            	}
	            } else {
	            	ss.setStatusEditable("T");
	            	ss.setStatusCopyable("T");
	            }

	           
	            
	            if (ss != null) {                
	                StringBuffer buf = new StringBuffer();
	                buf.append(ss.getFirstName()).append(" ").append(ss.getLastName()).append(": ");
	                if ("T".equals(ss.getCalculator())) {
	                    if ("true".equals(ss.getHasColorFontAccommodations()) ||
	                        "T".equals(ss.getScreenReader()) ||
	                        "T".equals(ss.getTestPause()) ||
	                        "T".equals(ss.getUntimedTest()))
	                        buf.append("Calculator, ");
	                    else
	                        buf.append("Calculator");
	                }
	                if(ss.getMusicFileId() == null || "".equals(ss.getMusicFileId().trim())){
	                	ss.setAuditoryCalming("F");
	                }else {
	                	ss.setAuditoryCalming("T");
	                }
	                
	                if ("true".equals(ss.getHasColorFontAccommodations())) {
	                    if ("T".equals(ss.getScreenReader()) ||
	                        "T".equals(ss.getTestPause()) ||
	                        "T".equals(ss.getUntimedTest()))
	                        buf.append("Color/Font, ");
	                    else
	                        buf.append("Color/Font");
	                }
	                if ("T".equals(ss.getScreenReader())) {
	                    if ("T".equals(ss.getTestPause()) ||
	                        "T".equals(ss.getUntimedTest()))
	                        buf.append("ScreenReader, ");
	                    else
	                        buf.append("ScreenReader");
	                }
	                if ("T".equals(ss.getTestPause())) {
	                    if ("T".equals(ss.getUntimedTest()))
	                        buf.append("TestPause, ");
	                    else
	                        buf.append("TestPause");
	                }
	                if ("T".equals(ss.getUntimedTest())) {
	                    buf.append("UntimedTest");
	                }
	                buf.append(".");
	                ss.setExtPin3(escape(buf.toString()));
	                ss.setHasColorFontAccommodations(getHasColorFontAccommodations(ss));
	                ss.setHasAccommodations(studentHasAccommodation(ss));
	                 if(ss.getMiddleName() != null && !ss.getMiddleName().equals(""))
	                	ss.setMiddleName( ss.getMiddleName().substring(0,1));
	                 
	                 if (ss.getStatus().getCode() == null || ss.getStatus().getCode().equals(""))
	                     ss.getStatus().setCode("&nbsp;");
	                 if ("Ses".equals(ss.getStatus().getCode()))
	                 {
	                     StringBuffer buf1 = new StringBuffer();
	                     TestSession ts = ss.getStatus().getPriorSession();
	                     if (ts != null)
	                     {
//	                         String timeZone = ts.getTimeZone();
	                         TestAdminStatusComputer.adjustSessionTimesToLocalTimeZone(ts);
	                         String testAdminName = ts.getTestAdminName();
	                         testAdminName = testAdminName.replaceAll("\"", "&quot;");
	                         buf1.append("Session Name: ").append(testAdminName);
	                         buf1.append("<br/>Start Date: ").append(DateUtils.formatDateToDateString(ts.getLoginStartDate()));
	                         buf1.append("<br/>End Date: ").append(DateUtils.formatDateToDateString(ts.getLoginEndDate()));
//	                         buf.append("<br/>Start Date: ").append(DateUtils.formatDateToDateString(com.ctb.util.DateUtils.getAdjustedDate(ts.getLoginStartDate(), TimeZone.getDefault().getID(), timeZone, ts.getDailyLoginStartTime())));
//	                         buf.append("<br/>End Date: ").append(DateUtils.formatDateToDateString(com.ctb.util.DateUtils.getAdjustedDate(ts.getLoginEndDate(), TimeZone.getDefault().getID(), timeZone, ts.getDailyLoginEndTime())));
	                     }
	                     ss.setExtPin2(buf1.toString());
	                 }
	                 
	                 innerMap.put("screenMagnifier", ss.getScreenMagnifier());
	                 innerMap.put("screenReader", ss.getScreenReader());
	                 innerMap.put("calculator", ss.getCalculator());
	                 innerMap.put("testPause", ss.getTestPause());
	                 innerMap.put("untimedTest", ss.getUntimedTest());
	                 innerMap.put("highLighter", ss.getHighLighter());
	                 innerMap.put("maskingRular", ss.getMaskingRular());
	                 innerMap.put("maskingTool", ss.getMaskingTool());
	                 innerMap.put("auditoryCalming", ss.getAuditoryCalming());
	                 innerMap.put("magnifyingGlass", ss.getMagnifyingGlass());
	                 
	                 if("T".equals(ss.getExtendedTimeAccom()) || (ss.getExtendedTimeAccom() != null && !ss.getExtendedTimeAccom().equals("") && !ss.getExtendedTimeAccom().equals("F"))){
	                	 innerMap.put("extendedTimeAccom","T");
	            	 }else {
	            		 innerMap.put("extendedTimeAccom","F");
	            	 }
	                 innerMap.put("hasColorFontAccommodations",getHasColorFontAccommodations(ss));
	                 accomodationMap.put(ss.getStudentId(), innerMap);
	                 //added for copy test session
	                 if("T".equals(ss.getStatus().getCopyable())){ 
	                	studentList.add(ss);
	                 }
	                //idToStudentMap.put(ss.getStudentId()+":"+ss.getOrgNodeId(), ss);

	            }
	        }
	        return studentList;
	    }
		
		private List<String> populateTestSessionForCopySession(ScheduledSession scheduledSession, ScheduledSession savedSessionMinData, 
														HttpServletRequest request, ValidationFailedInfo validationFailedInfo, 
														boolean isAddOperation, List<String> accessCodeListForCopy) {
			
			 try{
				 TestSession testSession = new TestSession();
				 TestSession existingTestSession = null;
				 Set<Integer> keySet            = this.topNodesMap.keySet();
				 Integer[] topnodeids= (keySet).toArray(new Integer[keySet.size()]);
				 String creatorOrgNodString	    = RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_CREATOR_ORG_NODE, false, null);			
				 Integer itemSetId        		= Integer.valueOf(RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_ITEM_SET_ID, false, null));
				 
				 //TestVO selectedTest = idToTestMap.get(itemSetId);
				 Integer creatorOrgNod = topnodeids[0];
				 if(creatorOrgNodString !=null && creatorOrgNodString.trim().length()>0 ){
					 try{
						 creatorOrgNod = Integer.valueOf(creatorOrgNodString.trim());
					 } catch (Exception e){	 }
				 }
				 
				 Integer productId        			= Integer.valueOf(RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_PRODUCT_ID, true, "-1"));
				 String dailyLoginEndTimeString		=RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_END_TIME, false, null);
				 String dailyLoginStartTimeString	= RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_START_TIME, false, null);
				 String dailyLoginEndDateString		= RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_END_DATE, false, null);
				 String dailyLoginStartDateString	= RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_START_DATE, false, null);

				 Date dailyLoginEndTime   		= DateUtils.getDateFromTimeString(dailyLoginEndTimeString);
				 Date dailyLoginStartTime 		= DateUtils.getDateFromTimeString(dailyLoginStartTimeString);
				 Date dailyLoginEndDate   		= DateUtils.getDateFromDateString(dailyLoginEndDateString);
				 Date dailyLoginStartDate 		= DateUtils.getDateFromDateString(dailyLoginStartDateString);
				 String location          		= RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_LOCATION, false, null);
				 String hasBreakValue     		= RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_HAS_BREAK, false, null);
				 String hasBreak          		= (hasBreakValue == null || !(hasBreakValue.trim().equals("T") || hasBreakValue.trim().equals("F"))) ? "F" :  hasBreakValue.trim();
				 boolean hasBreakBoolean        = (hasBreak.equals("T")) ? true : false;
				 String isRandomize       		= RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_RANDOMIZE, true, "");
				 String timeZone          		= DateUtils.getDBTimeZone( RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_TIME_ZONE, false, null));
				 String sessionName		  		= RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_TEST_NAME, false, null);
				 //String sessionName       		= RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_SESSION_NAME, false, null);
				 String showStdFeedbackVal   	= RequestUtil.getValueFromRequest(request, RequestUtil.SHOW_STUDENT_FEEDBACK, true, "false");
				 String showStdFeedback         = (showStdFeedbackVal==null || !(showStdFeedbackVal.trim().equals("true") || showStdFeedbackVal.trim().equals("false")) )? "F" :(showStdFeedbackVal.trim().equals("true")? "T" : "F");  
				 String productType				= RequestUtil.getValueFromRequest(request, RequestUtil.PRODUCT_TYPE, true, "");
				 String isEndTestSession 		= RequestUtil.getValueFromRequest(request, RequestUtil.TEST_ADMIN_STATUS, true, "");
				 //String formOperand       		= RequestUtil.getValueFromRequest(request, RequestUtil.FORM_OPERAND, true, TestSession.FormAssignment.ROUND_ROBIN);
				 //String overrideFormAssignment 	= RequestUtil.getValueFromRequest(request, RequestUtil.OVERRIDE_FORM_ASSIGNMENT, false, null);
				 //String overrideLoginStartDate    = RequestUtil.getValueFromRequest(request, RequestUtil.OVERRIDE_LOGIN_START_DATE, false, null);
				 /*Date overrideLoginSDate        = null ;
				 if(overrideLoginStartDate!=null)
					 overrideLoginSDate = DateUtils.getDateFromDateString(overrideLoginStartDate);*/
				 //String formAssigned			= RequestUtil.getValueFromRequest(request, RequestUtil.FORM_ASSIGNED, true, "");
				 
				 String testAdminIdString = (RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.TEST_ADMIN_ID, false, null));
				 Integer testAdminId = null;
				 //added for copy test session : for handling scenario, when select test accordion not clicked
				 boolean isSelectTestUpdatedForCopy = true;
				 String isSelectTestUpdated = request.getParameter("isSelectTestUpdated");
				 if(isSelectTestUpdated.equalsIgnoreCase("false"))
					 isSelectTestUpdatedForCopy = false;
				 //List<String> accessCodeListForCopy = new ArrayList<String>();
				 Integer testAdminIdBeforeCopy = null;
				 if(!isSelectTestUpdatedForCopy){
					 String[] itemSetIdTDs = RequestUtil.getValuesFromRequest(request, RequestUtil.TEST_ITEM_SET_ID_TD, true ,  new String [0]); 
					 if(itemSetIdTDs.length > 0){
						 accessCodeListForCopy = scheduleTest.getFixedNoAccessCode(itemSetIdTDs.length+2);// getting new set of access codes
					 }
					 else {
						 accessCodeListForCopy = scheduleTest.getFixedNoAccessCode(2);						 
					 }
				 }
				 
				 if(!isSelectTestUpdatedForCopy){ // modified for copy test session
					 testAdminIdBeforeCopy = Integer.valueOf(testAdminIdString.trim());
					 ScheduledSession dbsavedSessionMinData = scheduleTest.getScheduledSessionDetails(userName, testAdminIdBeforeCopy);
					 savedSessionMinData.setTestSession(dbsavedSessionMinData.getTestSession());
					 savedSessionMinData.setScheduledUnits(dbsavedSessionMinData.getScheduledUnits());
					 //savedSessionMinData.setStudents(dbsavedSessionMinData.getStudents());
					 //savedSessionMinData.setCopyable(dbsavedSessionMinData.getCopyable());
					 //savedSessionMinData.setStudentsLoggedIn(dbsavedSessionMinData.getStudentsLoggedIn());
					
					 existingTestSession = savedSessionMinData.getTestSession();
					 testSession = existingTestSession;
					 itemSetId = existingTestSession.getItemSetId();
				 }
				 //
				 /*if(!isAddOperation ){
					 testAdminIdBeforeCopy = Integer.valueOf(testAdminIdString.trim());
					 ScheduledSession dbsavedSessionMinData = scheduleTest.getScheduledSessionDetails(userName, testAdminIdBeforeCopy);
					 savedSessionMinData.setTestSession(dbsavedSessionMinData.getTestSession());
					 savedSessionMinData.setScheduledUnits(dbsavedSessionMinData.getScheduledUnits());
					 savedSessionMinData.setStudents(dbsavedSessionMinData.getStudents());
					 savedSessionMinData.setCopyable(dbsavedSessionMinData.getCopyable());
					 savedSessionMinData.setStudentsLoggedIn(dbsavedSessionMinData.getStudentsLoggedIn());
				 }*/ // commented for copy test session
				 
				 String formOperand  =  TestSession.FormAssignment.ROUND_ROBIN;
				 TestElement selectedTest = scheduleTest.getTestElementMinInfoById(this.getCustomerId(), itemSetId); 
				 if(selectedTest.getOverrideFormAssignmentMethod() != null) {
					 formOperand = selectedTest.getOverrideFormAssignmentMethod();
		           }else if (selectedTest.getForms()!= null && selectedTest.getForms().length > 0 ) {
		        	   formOperand = TestSession.FormAssignment.ROUND_ROBIN;
		            } else {
		            	formOperand = TestSession.FormAssignment.ROUND_ROBIN;
		           }

				 String overrideFormAssignment 	=  selectedTest.getOverrideFormAssignmentMethod();
				 Date overrideLoginSDate  		=  dailyLoginStartDate; // selectedTest.getOverrideLoginStartDate();
				 String formAssigned			=  (selectedTest.getForms() ==null || selectedTest.getForms().length==0)? null: selectedTest.getForms()[0]; 
				 String testName       		    = 	selectedTest.getItemSetName(); 
				 Date overrideLoginEDate  		=  dailyLoginEndDate; // selectedTest.getOverrideLoginEndDate();
				 
				 
				 // setting default value
				 testSession.setTestAdminId(testAdminId);			 
				 testSession.setLoginEndDate(dailyLoginEndDate);
				 testSession.setDailyLoginEndTime(dailyLoginEndTime);
				 if(testAdminId != null && "true".equalsIgnoreCase(isEndTestSession)){
					 TimeZone defaultTimeZone = TimeZone.getDefault();
					 Date now = new Date(System.currentTimeMillis());
			         now = com.ctb.util.DateUtils.getAdjustedDate(now, defaultTimeZone.getID(), timeZone, now);
		    		 String timeStr = DateUtils.formatDateToTimeString(now);
				     String dateStr = DateUtils.formatDateToDateString(now);
					 //testSession.setTestAdminStatus("PA");
					 testSession.setLoginEndDate(DateUtils.getDateFromDateString(dateStr));
					 testSession.setDailyLoginEndTime(DateUtils.getDateFromTimeString(timeStr));
				 }
		       
		         if(isAddOperation ){
		        	 testSession.setTestAdminType("SE");
		        	 testSession.setActivationStatus("AC"); 
		        	 testSession.setEnforceTimeLimit("T");
		        	 testSession.setCreatedBy(this.userName);
		        	 testSession.setShowStudentFeedback(showStdFeedback);
		        	 testSession.setTestAdminStatus("CU");
		         } else {
		        	 testSession.setTestAdminType(existingTestSession.getTestAdminType());
		        	 testSession.setActivationStatus(existingTestSession.getActivationStatus()); 
		        	 testSession.setEnforceTimeLimit(existingTestSession.getEnforceTimeLimit());
		        	 testSession.setShowStudentFeedback(existingTestSession.getShowStudentFeedback());
		        	 testSession.setSessionNumber(existingTestSession.getSessionNumber());
		        	 testSession.setCreatedBy(existingTestSession.getCreatedBy());
		         }
		         
		         testSession.setCreatorOrgNodeId(creatorOrgNod);
		         testSession.setProductId(productId);	    
		         testSession.setDailyLoginStartTime(dailyLoginStartTime);
		         testSession.setLocation(location);
		         testSession.setEnforceBreak(hasBreak);
		         testSession.setIsRandomize(isRandomize);	         	       
		         testSession.setLoginStartDate(dailyLoginStartDate);
		         testSession.setTimeZone(timeZone);
		         testSession.setTestName(testName);
		         testSession.setTestAdminName(sessionName);

		         if (formOperand.equals(TestSession.FormAssignment.MANUAL))
		             testSession.setFormAssignmentMethod(TestSession.FormAssignment.MANUAL);
		         else if (formOperand.equals(TestSession.FormAssignment.ALL_SAME))
		             testSession.setFormAssignmentMethod(TestSession.FormAssignment.ALL_SAME);
		         else 
		             testSession.setFormAssignmentMethod(TestSession.FormAssignment.ROUND_ROBIN);
		         
		         testSession.setPreferredForm(formAssigned);      
		         
		         testSession.setOverrideFormAssignmentMethod(overrideFormAssignment);
		         testSession.setOverrideLoginStartDate(overrideLoginSDate);
		         testSession.setOverrideLoginEndDate(overrideLoginEDate);
		         
		         testSession.setItemSetId(itemSetId);
		         
		         if (productType!=null && (TestSessionUtils.isTabeProduct(productType).booleanValue()  || TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue()))
		         {
		             testSession.setFormAssignmentMethod(TestSession.FormAssignment.MANUAL);
		             if(overrideFormAssignment!=null)
		            	 testSession.setOverrideFormAssignmentMethod(TestSession.FormAssignment.MANUAL); 
		             
		             testSession.setPreferredForm(null);  
		         }

		         if (hasBreakBoolean)
		         {
		        	String accessCode;
		        	if(!isSelectTestUpdatedForCopy){
		        		accessCode = accessCodeListForCopy.get(0); //need to set new access code
		        	}else{
		        		accessCode = RequestUtil.getValuesFromRequest(request, RequestUtil.TEST_ITEM_IND_ACCESS_CODE, true, new String [0])[0];
		        	}
		        	
		         	testSession.setAccessCode(accessCode);    
		         }
		         else
		         {
		        	 String accessCode;
			         if(!isSelectTestUpdatedForCopy){
			        	 accessCode = accessCodeListForCopy.get(0);//need to set new access code
			         }else{
			        	 accessCode = RequestUtil.getValueFromRequest(request, RequestUtil.ACCESS_CODE, true, "");
			         }
		        	 testSession.setAccessCode(accessCode); 
		         }
		         
		         validateTestSession(testSession, validationFailedInfo);
		         if(!validationFailedInfo.isValidationFailed()) {
		        	validateTestSessionDate(dailyLoginEndDateString,dailyLoginStartDateString, dailyLoginEndTimeString, dailyLoginStartTimeString, timeZone, overrideLoginSDate,overrideLoginEDate, validationFailedInfo, isAddOperation); 
		         }
		         
		         scheduledSession.setTestSession(testSession);
				 
			 } catch (Exception e) {
				 e.printStackTrace();
				 validationFailedInfo.setKey("SYSTEM_EXCEPTION");
				 validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("System.Exception.Header"));
				 validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("System.Exception.Body"));
				 
			 }
			 // retrieving data from request
			 return accessCodeListForCopy;
				
			}
		
	    private void populateScheduledUnitsForCopySession(ScheduledSession scheduledSession, ScheduledSession savedSessionMinData,
					HttpServletRequest request, ValidationFailedInfo validationFailedInfo, 
					boolean isAddOperation, List<String> accessCodeListForCopy) {
			/* List subtestList = null;*/
			//boolean sessionHasLocator = false;
			try{
			String productType				= RequestUtil.getValueFromRequest(request, RequestUtil.PRODUCT_TYPE, true, "");
			Integer itemSetId        		= Integer.valueOf(RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_ITEM_SET_ID, false, null));
			String hasBreakValue     		= RequestUtil.getValueFromRequest(request, RequestUtil.SESSION_HAS_BREAK, false, null);
			String hasBreak          		= (hasBreakValue == null || !(hasBreakValue.trim().equals("T") || hasBreakValue.trim().equals("F"))) ? "F" :  hasBreakValue.trim();
			boolean hasBreakBoolean        	= (hasBreak.equals("T")) ? true : false;
			String[] itemSetIdTDs          	= RequestUtil.getValuesFromRequest(request, RequestUtil.TEST_ITEM_SET_ID_TD, true ,  new String [0]);
			String[] accesscodes           	= RequestUtil.getValuesFromRequest(request, RequestUtil.TEST_ITEM_IND_ACCESS_CODE, true ,  new String [itemSetIdTDs.length]);
			String[] itemSetForms          	= RequestUtil.getValuesFromRequest(request, RequestUtil.TEST_ITEM_SET_FORM, true ,  new String [itemSetIdTDs.length]);
			String[] itemSetisDefault      	= RequestUtil.getValuesFromRequest(request, RequestUtil.TEST_ITEM_IS_SESSION_DEFAULT, true ,  new String [itemSetIdTDs.length]);
			String autoLocator			   	=  RequestUtil.getValueFromRequest(request, RequestUtil.HAS_AUTOLOCATOR, true, "false");
       	 	String[] islocatorChecked      	= RequestUtil.getValuesFromRequest(request, RequestUtil.LOCATOR_CHECKBOX, true ,  new String [itemSetIdTDs.length]);
       	    String[] locatorTDsForTABE		= RequestUtil.getValuesFromRequest(request, "locatorItemTD", true ,  new String [0]);
			
			//added for copy test session : handling for not selecting select test acco
			boolean isSelectTestUpdatedForCopy = true;	
			Map<Integer,String> locatorItemSetTDMap = new HashMap<Integer,String>();
	       	 for(int indx =0; indx<locatorTDsForTABE.length; indx++){
	       		 String[] strArr = locatorTDsForTABE[indx].split("~");
	       		 Integer TDid = Integer.valueOf(strArr[0].trim());
	       		 String testName = strArr[1].trim();
	    			 locatorItemSetTDMap.put(TDid, testName);
	       	 }
			String isSelectTestUpdated = request.getParameter("isSelectTestUpdated");// RequestUtil.getValueFromRequest(request, RequestUtil.IS_STUDENT_LIST_UPDATED, true, "true");
			if(isSelectTestUpdated.equalsIgnoreCase("false"))
				isSelectTestUpdatedForCopy = false;
			
			//List<SubtestVO>  subtestList   = idToTestMap.get(itemSetId).getSubtests();
			List<SubtestVO>  subtestList   = new ArrayList<SubtestVO>();
			for(int ii =0, jj =itemSetIdTDs.length; ii<jj; ii++ ){
				SubtestVO subtest = new SubtestVO();
				subtest.setId(Integer.valueOf(itemSetIdTDs[ii].trim()));
				if (!isSelectTestUpdatedForCopy) {
					subtest.setTestAccessCode(accessCodeListForCopy.get(ii));
					
					if (productType!=null && TestSessionUtils.isTabeProduct(productType).booleanValue()) {
						TestElement[] tes = savedSessionMinData.getScheduledUnits();
						for(int k=0; k<tes.length; k++){
							if (subtest.getId().intValue() == tes[k].getItemSetId().intValue()) {
								tes[k].getIslocatorChecked();
				       		 	subtest.setIslocatorChecked(tes[k].getIslocatorChecked());
							}
						}
					}
					
				} else {
					subtest.setTestAccessCode(accesscodes[ii]);
					if (productType!=null && TestSessionUtils.isTabeProduct(productType).booleanValue()) {
	       		 		subtest.setIslocatorChecked(islocatorChecked[ii]);
					}
				}
				subtest.setSessionDefault(itemSetisDefault[ii]);
				if(itemSetForms[ii] != null && itemSetForms[ii].trim().length()>0){
					subtest.setLevel(itemSetForms[ii]);
				}
				subtestList.add(subtest);
			
			}
			
			if (productType!=null && TestSessionUtils.isTabeProduct(productType).booleanValue())
			{
			
				if (TestSessionUtils.isTabeBatterySurveyProduct(productType).booleanValue() || TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue())
				{
					
					if ((autoLocator != null) && autoLocator.equals("true"))
					{   
						Integer lItemSetId   = Integer.valueOf(RequestUtil.getValueFromRequest(request, RequestUtil.LOCATOR_TEST_ITEM_SET_ID_TD, false, null));
						String lAccesscodes  = RequestUtil.getValueFromRequest(request, RequestUtil.LOCATOR_TEST_ITEM_IND_ACCESS_CODE, true, "");
						String lItemSetisDefault  = RequestUtil.getValueFromRequest(request, RequestUtil.LOCATOR_TEST_ITEM_IS_SESSION_DEFAULT, false, null);
						String lItemSetForms      = RequestUtil.getValueFromRequest(request, RequestUtil.LOCATOR_TEST_ITEM_SET_FORM, false, null);
						SubtestVO locatorSubtest = new SubtestVO();
						locatorSubtest.setId(lItemSetId);
						List<String> accessCodeLocatorList = scheduleTest.getFixedNoAccessCode(1);
						lAccesscodes = (String)accessCodeLocatorList.get(0);					
						locatorSubtest.setTestAccessCode(lAccesscodes);
						locatorSubtest.setSessionDefault(lItemSetisDefault);
						if(lItemSetForms!=null && lItemSetForms.length() >0 ){
							locatorSubtest.setLevel(lItemSetForms);
						}
						subtestList.add(0, locatorSubtest);
						scheduledSession.setHasLocator(true);
	        			scheduledSession.setLocatorSubtestTD(locatorItemSetTDMap);
					} else {
						TestSessionUtils.setDefaultLevels(subtestList, "E");
					
					}
				} 
				else
				{
					// tabe locator test
					Integer lItemSetId   = Integer.valueOf(RequestUtil.getValueFromRequest(request, RequestUtil.LOCATOR_TEST_ITEM_SET_ID_TD, false, null));
					String lAccesscodes  = RequestUtil.getValueFromRequest(request, RequestUtil.LOCATOR_TEST_ITEM_IND_ACCESS_CODE, true, "");
					String lItemSetisDefault  = RequestUtil.getValueFromRequest(request, RequestUtil.LOCATOR_TEST_ITEM_IS_SESSION_DEFAULT, false, null);
					String lItemSetForms      = RequestUtil.getValueFromRequest(request, RequestUtil.LOCATOR_TEST_ITEM_SET_FORM, false, null);
					SubtestVO locatorSubtest = new SubtestVO();
					locatorSubtest.setId(lItemSetId);
					locatorSubtest.setTestAccessCode(lAccesscodes);
					locatorSubtest.setSessionDefault(lItemSetisDefault);
					if(lItemSetForms!=null && lItemSetForms.length() >0 ){
						locatorSubtest.setLevel(lItemSetForms);
					}
					subtestList.add(0, locatorSubtest);
					
					TestSessionUtils.setDefaultLevels(subtestList, "1");
				}   
			
			}
			else
			{
				// for non-tabe test
				subtestList = TestSessionUtils.cloneSubtests(subtestList);
			}
			
			
			TestElement [] newTEs = new TestElement[subtestList.size()];
			
			for (int i=0; i < subtestList.size(); i++)
			{
				SubtestVO subVO= (SubtestVO)subtestList.get(i);
				TestElement te = new TestElement();
				
				te.setItemSetId(subVO.getId());
				
				if (TestSessionUtils.isTabeProduct(productType).booleanValue()  || TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue())
				{                
					String level = subVO.getLevel();
					te.setItemSetForm(level);
				}
				
				if (!hasBreakBoolean ) {
					//String accessCode = RequestUtil.getValueFromRequest(request, RequestUtil.ACCESS_CODE, true, "");
					String accessCode = scheduledSession.getTestSession().getAccessCode();
					te.setAccessCode(accessCode);
				} else {
					//String accessCode = RequestUtil.getValueFromRequest(request, RequestUtil.ACCESS_CODEB+i, true, "");
					//te.setAccessCode(accessCode);
					te.setAccessCode(subVO.getTestAccessCode());
				}
				
				
				te.setSessionDefault(subVO.getSessionDefault());
	            te.setIslocatorChecked(subVO.getIslocatorChecked());
				
				newTEs[i] = te;
			}
			
			scheduledSession.setScheduledUnits(newTEs);
			validateScheduledUnits(scheduledSession, hasBreakBoolean, validationFailedInfo, isAddOperation);
			if(TestSessionUtils.isTabeProduct(productType).booleanValue()  || TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue()) {
			//TABESubtestValidation.validation(A, validateLevels, TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue());
			
			}
			
			if(scheduledSession.getTestSession().getTestAdminId()!=null && scheduledSession.getTestSession().getTestAdminId()!=-1){
				if(TestSessionUtils.isTabeProduct(productType).booleanValue()  || TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue()) {
					if(savedSessionMinData.getTestSession().getItemSetId().intValue() == scheduledSession.getTestSession().getItemSetId().intValue()){
						TestElement[] te = TestSessionUtils.setupSessionSubtests( savedSessionMinData.getScheduledUnits(), scheduledSession.getScheduledUnits());
						scheduledSession.setScheduledUnits(te);
					}
				
				}
			}
			
			} catch (Exception e) {
				e.printStackTrace();
				validationFailedInfo.setKey("SYSTEM_EXCEPTION");
				validationFailedInfo.setMessageHeader(MessageResourceBundle.getMessage("System.Exception.Header"));
				validationFailedInfo.updateMessage(MessageResourceBundle.getMessage("System.Exception.Body"));
			}

	     }
	     
	 private void populateSessionStudentForCopySession(ScheduledSession scheduledSession,ScheduledSession savedSessionMinData,
				HttpServletRequest httpServletRequest,
			ValidationFailedInfo validationFailedInfo, boolean isAddOperation) {

		try {
			
			//boolean isStudentListUpdated = true;
			boolean isStudentManifestsExists = false;
			 String productType				= RequestUtil.getValueFromRequest(httpServletRequest, RequestUtil.PRODUCT_TYPE, true, "");
			/*if(!isAddOperation){
				String isStudentUpdated = RequestUtil.getValueFromRequest(httpServletRequest, RequestUtil.IS_STUDENT_LIST_UPDATED, true, "true");
				if(isStudentUpdated.equalsIgnoreCase("false"))
					isStudentListUpdated = false;
			}*/
			//commented for copy test session

			boolean isStudentListUpdatedForCopy = true;
			String testAdminIdString = (RequestUtil.getValueFromRequest(this.getRequest(), RequestUtil.TEST_ADMIN_ID, false, null));
			Integer testAdminIdBeforCopy =  Integer.valueOf(testAdminIdString.trim());

			String isStudentUpdatedForCopy = RequestUtil.getValueFromRequest(httpServletRequest, RequestUtil.IS_STUDENT_LIST_UPDATED, true, "true");
			if(isStudentUpdatedForCopy.equalsIgnoreCase("false"))
				isStudentListUpdatedForCopy = false;

				
			if(isAddOperation && isStudentListUpdatedForCopy){

				String studentsBeforeSave = RequestUtil.getValueFromRequest(httpServletRequest, RequestUtil.STUDENTS, true, "");
				int studentCountBeforeSave = 0;
				if (studentsBeforeSave != null
						&& studentsBeforeSave.trim().length() > 1) {
					studentCountBeforeSave = studentsBeforeSave.split(",").length;
				}
				ArrayList<SessionStudent> sessionStudents = new ArrayList<SessionStudent>(studentCountBeforeSave);
				if (studentCountBeforeSave > 0) {
					String[] studs = studentsBeforeSave.split(",");
					for (String std : studs) {
						StringTokenizer st = new StringTokenizer(std, ":");
						SessionStudent ss = new SessionStudent();
						while (st.hasMoreTokens()) {
							StringTokenizer keyVal = new StringTokenizer(st.nextToken(), "=");
							
							String key = keyVal.nextToken();
							String val = null;
							if(keyVal.countTokens()>0) {
								val= keyVal.nextToken();
							}

							if (key.equalsIgnoreCase("studentId")) {
								ss.setStudentId(Integer.valueOf(val));
							} else if (key.equalsIgnoreCase("orgNodeId")) {
								ss.setOrgNodeId(Integer.valueOf(val));
							} else if (key.equalsIgnoreCase("extendedTimeAccom")) {
								ss.setExtendedTimeAccom(val);
							} else if (key.equalsIgnoreCase("statusCopyable")) {
								EditCopyStatus status = new EditCopyStatus();
								status.setCopyable(val);
								ss.setStatus(status);
							} else if (key.equalsIgnoreCase("itemSetForm")) {
								ss.setItemSetForm(val);
							} else if (key.equalsIgnoreCase("isNewStd") && val !=null && val.equalsIgnoreCase("true") ) {
								ss.setNewStudent(true);
							} else if (key.equalsIgnoreCase("extendedTimeFactor") && val != null){
								ss.setExtendedTimeFactor(new Double(val.toString()));
							}
						}

						sessionStudents.add(ss);

					}
				
			}
				scheduledSession.setStudents(sessionStudents
						.toArray(new SessionStudent[sessionStudents.size()]));
		
		} else {
			//fetch student data from db using original test_admin_id
			ScheduledSession schSession = this.scheduleTest.getScheduledStudentsMinimalInfoDetails(this.userName, testAdminIdBeforCopy);
			SessionStudent[] sessionStudents = schSession.getStudents();
			ArrayList<SessionStudent> studentListForCopy = null;
   	    	if (this.isTABECustomer || this.isTABEAdaptiveCustomer || this.isTASCCustomer || this.isTASCReadinessCustomer) {
	    		studentListForCopy = new ArrayList<SessionStudent>(0);
	    	}
	    	else {
				studentListForCopy = new ArrayList<SessionStudent>(sessionStudents.length);
				for(int i=0; i< sessionStudents.length; i++){
					SessionStudent ss = (SessionStudent)sessionStudents[i];
					if(ss != null){
						if(ss.getStatus().getCopyable().equals("T")){
							studentListForCopy.add(ss);
						}
					}
				}
	    	}
			scheduledSession.setStudentsLoggedIn(new Integer(0));
			scheduledSession.setStudents(studentListForCopy.toArray(new SessionStudent[studentListForCopy.size()]));
	    	isStudentManifestsExists = true;
		}
			
		if(scheduledSession.getStudents()!= null && scheduledSession.getStudents().length>0 && !isAddOperation){
			if(TestSessionUtils.isTabeBatterySurveyProduct(productType).booleanValue() || TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue()){
				updateStudentstudentManifests(scheduledSession, savedSessionMinData, isStudentManifestsExists );
			}
				
		}
		
	  if(scheduledSession.getStudents()!= null && scheduledSession.getStudents().length>0) {
		  SessionStudent [] sessionStudents = scheduledSession.getStudents();
		  TestElement [] newTEs = scheduledSession.getScheduledUnits();
		  boolean sessionHasLocator = false;
		  if (TestSessionUtils.isTabeBatterySurveyProduct(productType).booleanValue() || TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue()) {
			  String autoLocator =  RequestUtil.getValueFromRequest(httpServletRequest, RequestUtil.HAS_AUTOLOCATOR, true, "false");;
	          if ((autoLocator != null) && autoLocator.equals("true")) {            
	              sessionHasLocator = true;
	          }
	      }
		  if (TestSessionUtils.isTabeProduct(productType).booleanValue() ||	TestSessionUtils.isTabeAdaptiveProduct(productType).booleanValue()) {
			  
			  SubtestVO locSubtest = null ;
			  
			  for(int i=0; i < sessionStudents.length; i++){
				  SessionStudent sessionStudent = sessionStudents[i];
			 

	              // replicate student's manifest if this student has no individual manifest
	              StudentManifest [] studentManifests = sessionStudent.getStudentManifests();
	             if ((studentManifests == null) || (studentManifests.length == 0))
	              {
	                  
	                  List studentSubtestList = TestSessionUtils.getDefaultSubtests(newTEs);
	                  
	                  studentManifests = new StudentManifest[studentSubtestList.size()];
	                  
	                  for (int j=0; j < studentSubtestList.size(); j++)
	                  {
	                      
	                      SubtestVO subtestVO = (SubtestVO)studentSubtestList.get(j);
	                      
	                      studentManifests[j] = new StudentManifest();
	                      
	                      studentManifests[j].setItemSetId(subtestVO.getId());
	                      studentManifests[j].setItemSetName(subtestVO.getSubtestName());                            
	                      studentManifests[j].setItemSetForm(subtestVO.getLevel());
	                      studentManifests[j].setItemSetOrder(new Integer(j + 1));                            
	                  }   
	                  
	                  // set recommended level for this student if there is no locator for this session
	                  if (! sessionHasLocator && TestSessionUtils.isTabeProduct(productType).booleanValue())
	                  {
	                      Integer studentId = sessionStudent.getStudentId();
	                      Integer itemSetId = scheduledSession.getTestSession().getItemSetId() /*testSession.getItemSetId()*/;
	                     // SubtestVO locSubtest = this.locatorSubtest;
	                      if (locSubtest == null) {
	                          locSubtest = TestSessionUtils.getLocatorSubtest(this.scheduleTest, this.userName, itemSetId); 
	                      }
	                      if (locSubtest != null) {
	                      	Integer locatorItemSetId = locSubtest.getId();
	                      	TestSessionUtils.setRecommendedLevelForStudent(this.scheduleTest, this.userName, studentId, itemSetId, locatorItemSetId, studentManifests);
	                      }
	                  }
	                               
	                  sessionStudent.setStudentManifests(studentManifests);
	              }  

	           }
		  }
		  
	  }

			
		} catch (Exception e) {
			e.printStackTrace();
			validationFailedInfo.setKey("SYSTEM_EXCEPTION");
			validationFailedInfo.setMessageHeader(MessageResourceBundle
					.getMessage("System.Exception.Header"));
			validationFailedInfo.updateMessage(MessageResourceBundle
					.getMessage("System.Exception.Body"));
		}

	}
	 	
		private void populateProctorForCopySession(ScheduledSession scheduledSession,
				HttpServletRequest request,
				ValidationFailedInfo validationFailedInfo, boolean isAddOperation) {
			

			try {
				//boolean isProcListUpdated = true;
				/*if(!isAddOperation){
					String isStudentUpdated = RequestUtil.getValueFromRequest(request, RequestUtil.IS_PROCTOR_LIST_UPDATED, true, "true");
					if(isStudentUpdated.equalsIgnoreCase("false"))
						isProcListUpdated = false;
				}*/
				boolean isProcListUpdatedForCopy = true;
				String testAdminIdString = (RequestUtil.getValueFromRequest(request, RequestUtil.TEST_ADMIN_ID, false, null));
				Integer testAdminIdBeforCopy =  Integer.valueOf(testAdminIdString.trim());

				String isProcUpdatedForCopy = RequestUtil.getValueFromRequest(request, RequestUtil.IS_STUDENT_LIST_UPDATED, true, "true");
				if(isProcUpdatedForCopy.equalsIgnoreCase("false"))
					isProcListUpdatedForCopy = false;
				
				if(isAddOperation && isProcListUpdatedForCopy) {
					String proctorsData = RequestUtil.getValueFromRequest(request, RequestUtil.PROCTORS, true, "");
					int proctorCount = 0;
					if (proctorsData != null
							&& proctorsData.trim().length() > 1) {
						proctorCount = proctorsData.split(",").length;
					}
					if (proctorCount > 0) {
						ArrayList<User> proctorList = new ArrayList<User>(proctorCount);
						String[] procs = proctorsData.split(",");
						for (String procrec : procs) {
							StringTokenizer st = new StringTokenizer(procrec, ":");
							User us = new User();
							while (st.hasMoreTokens()) {
								StringTokenizer keyVal = new StringTokenizer(st
										.nextToken(), "=");
		
								String key = keyVal.nextToken();
								String val = null;
								if (keyVal.countTokens() > 0) {
									val = keyVal.nextToken();
								}
		
								if (key.equalsIgnoreCase("userId")) {
									us.setUserId(Integer.valueOf(val));
								} else if (key.equalsIgnoreCase("userName")) {
									us.setUserName(val);
								} else if (key.equalsIgnoreCase("copyable")) {
									us.setCopyable(val);
								} 
							}
		
							proctorList.add(us);
						}
		
						scheduledSession.setProctors(proctorList.toArray(new User[proctorList.size()]));
					} else {
						ArrayList<User> sessionProctorsForCopy = getProctors(testAdminIdBeforCopy);
						//check if default scheduler value needs to be reset
						scheduledSession.setProctors(sessionProctorsForCopy.toArray(new User[sessionProctorsForCopy.size()]));
					}
				} else {
					ArrayList<User> sessionProctorsForCopy = getProctors(testAdminIdBeforCopy);
					//check if default scheduler value needs to be reset
					scheduledSession.setProctors(sessionProctorsForCopy.toArray(new User[sessionProctorsForCopy.size()]));
				}
			} catch (Exception e) {
				e.printStackTrace();
				validationFailedInfo.setKey("SYSTEM_EXCEPTION");
				validationFailedInfo.setMessageHeader(MessageResourceBundle
						.getMessage("System.Exception.Header"));
				validationFailedInfo.updateMessage(MessageResourceBundle
						.getMessage("System.Exception.Body"));
			}

		}
		
		private ArrayList<User> getProctors(Integer testAdminIdBeforCopy) throws CTBBusinessException{
			ScheduledSession schSession = this.scheduleTest.getScheduledProctorsMinimalInfoDetails(this.userName, testAdminIdBeforCopy);
			User[] sessionProctors = schSession.getProctors();
			ArrayList<User> sessionProctorsForCopy = new ArrayList<User>(sessionProctors.length);
			for(int i=0; i< sessionProctors.length; i++){
				User user = sessionProctors[i];
				if(user.getCopyable().equals("T")){
					sessionProctorsForCopy.add(user); 
				}
			}
			return sessionProctorsForCopy;
		}
		
		@Jpf.Action(forwards = { 
			    @Jpf.Forward(name = "success",
		                     path = "view_subtest_details.jsp")
		    })
			 protected Forward toggleExemtionValidationStatus()  {       
				String strItemSetIds = null;
				String[] itemSetIdsList = null;
				Base base = new Base();
	            Integer testRosterId = Integer.parseInt(getRequest().getParameter("testRosterId"));
		        if(getRequest().getParameter("itemSetIds") != null){
		        	strItemSetIds = getRequest().getParameter("itemSetIds");
		        	itemSetIdsList = strItemSetIds.split("\\|");
		        }
		        String[] itemSetIds = new String[itemSetIdsList.length];
		        for(int i=0; i<itemSetIdsList.length; i++){
		        	itemSetIds[i] = itemSetIdsList[i];
		        }
		        try {
		        	this.testSessionStatus.toggleSubtestValidationStatus(this.userName, testRosterId, itemSetIds, "ExemptionStatus" );
		        }
		        catch (Exception e) {
		            e.printStackTrace();
		        }
		        createGson(base);
	 		    return null;
			}
		
			@Jpf.Action(forwards = { 
			    @Jpf.Forward(name = "success",
		                     path = "view_subtest_details.jsp")
		    })
			 protected Forward toggleAbsentValidationStatus()  {       
				String strItemSetIds = null;
				String[] itemSetIdsList = null;
				Base base = new Base();
	            Integer testRosterId = Integer.parseInt(getRequest().getParameter("testRosterId"));
		        if(getRequest().getParameter("itemSetIds") != null){
		        	strItemSetIds = getRequest().getParameter("itemSetIds");
		        	itemSetIdsList = strItemSetIds.split("\\|");
		        }
		        String[] itemSetIds = new String[itemSetIdsList.length];
		        for(int i=0; i<itemSetIdsList.length; i++){
		        	itemSetIds[i] = itemSetIdsList[i];
		        }
		        try {
		        	this.testSessionStatus.toggleSubtestValidationStatus(this.userName, testRosterId, itemSetIds, "AbsentStatus" );
		        }
		        catch (Exception e) {
		            e.printStackTrace();
		        }
		        createGson(base);
	 		    return null;
			}
////	
			
			@Jpf.Action()
		    protected Forward ssoSig()
		    {
		        HttpServletRequest req = getRequest();
				HttpServletResponse resp = getResponse();
				OutputStream stream = null;
				
				String reportUrl = "";
				if (true)
				{
			        if (this.reportManager == null)
			        {
			        	initReportManager(true);
			        }
			    	
			        String userOrgIndex = this.getRequest().getParameter("userOrgIndex");
			        try{int iuserorgIndex = Integer.parseInt(userOrgIndex);} catch (Exception e){userOrgIndex=null;}
			        if (userOrgIndex != null && userOrgIndex.length()>0)
			        {
			        	this.reportManager.setSelectedOrganization(userOrgIndex);
			        }
			        else
			        	userOrgIndex = "0";
			        
			        Integer programId = this.reportManager.getSelectedProgramId();
			        Integer orgNodeId = this.reportManager.getSelectedOrganizationId();

			        List reportList = buildTASCReportList(orgNodeId, programId);
			        
			        //**[IAA] Proctor users should not see PRISM reports
			        if (isProctorUser())
			        {
			        	for (int i=0; i < reportList.size(); i++) {
			        		reportList.remove(i);
			        	}
			        }
			        
			        String requestParam = "";
			        for (int i=0; i < reportList.size(); i++) {
			            CustomerReport cr = (CustomerReport)reportList.get(i);
			            if ("Prism".equalsIgnoreCase(cr.getReportName())) {                
			                //[IAA]: process SSO and pass correct parameters to PRISM
			            	//Story: TASC - 2013 Op - 07 - SSO to Prism parameters (frontend)
			            	if (i==0)
			            	{
			            		HMACQueryStringEncrypter HMACEncrypter = new HMACQueryStringEncrypter(this.user, this.orgNode, cr.getCustomerKey(), orgNodeId);
			                	requestParam = HMACEncrypter.encrypt();
			                	System.out.println("HMACEncrypter.encrypt()->SSOparams=" + requestParam);
			            	}
			            	reportUrl = cr.getReportUrl()+(cr.getReportUrl().endsWith("?")?"":"?")+requestParam;
			            	//cr.setReportUrl(reportUrl);
			            }
			        }	
				}
				
				SSOSig sig = new SSOSig();	
				sig.ssoParams = reportUrl;

		        Gson gson = new Gson();
		        String jsonData = gson.toJson(sig);		        
		        System.out.println("Gson: "+gson.fromJson(jsonData, SSOSig.class).ssoParams);
		        
				try{
			       	try {
			   			resp.setContentType("application/json");
		 	   			stream = resp.getOutputStream();
			   			stream.write(jsonData.getBytes("UTF-8"));
			   			resp.flushBuffer();
			   		} catch (IOException e) {
			   			e.printStackTrace();
			   		} 
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				
		        return null;
		    }
			
			////	
			
			/**
			 * This method checks whether customer is configured to display class name in individual, multiple 
			 * and summary testTicket or not.
			 * @return Return Boolean 
			 */
			
			private Boolean customerHasPrintClassName()
		    {               
				Integer customerId = this.user.getCustomer().getCustomerId();
		        boolean hasPrintClassName = false;
		        
		        for (int i=0; i < this.customerConfigurations.length; i++)
		        {
		        	CustomerConfiguration cc = (CustomerConfiguration)this.customerConfigurations[i];
		            if (cc.getCustomerConfigurationName().equalsIgnoreCase("Allow_Print_ClassName") && 
		            		cc.getDefaultValue().equals("T")) {
		            	hasPrintClassName = true;
		                break;
		            } 
		        }
		       
		        return hasPrintClassName;
		    }
			
			/**
			 * This method checks whether customer is configured to display Session name in testTicket or not.
			 * @return Return Boolean 
			 */
			
			private Boolean customerHasPrintSessionName()
		    {               
				Integer customerId = this.user.getCustomer().getCustomerId();
		        boolean hasPrintSessionName = false;
		        
		        for (int i=0; i < this.customerConfigurations.length; i++)
		        {
		        	CustomerConfiguration cc = (CustomerConfiguration)this.customerConfigurations[i];
		            if (cc.getCustomerConfigurationName().equalsIgnoreCase("Allow_Print_SessionName") && 
		            		cc.getDefaultValue().equals("T")) {
		            	hasPrintSessionName = true;
		                break;
		            } 
		        }
		       
		        return hasPrintSessionName;
		    }
			
			private boolean isWVCustomer()
		    {               
		        boolean WVCustomer = false;
		        for (int i=0; i < this.customerConfigurations.length; i++)
		        {
		        	CustomerConfiguration cc = (CustomerConfiguration)this.customerConfigurations[i];
		            if (cc.getCustomerConfigurationName().equalsIgnoreCase("WV_Customer") && 
		            		cc.getDefaultValue().equals("T")) {
		            	WVCustomer = true;
		                break;
		            } 
		        }
		        return WVCustomer;
		    }
			
			private void initializeCustomerConfigurations()
			{
				Integer customerId = this.user.getCustomer().getCustomerId();
		        try
		        {  
					this.customerConfigurations = users.getCustomerConfigurations(customerId.intValue());
					if (this.customerConfigurations == null || this.customerConfigurations.length == 0) {
						this.customerConfigurations = users.getCustomerConfigurations(2);
					}
		        }
		        catch (SQLException se) {
		    	   se.printStackTrace();
			    }
			}
			
			private void populateFirstLastName(ScheduledSession session, HttpServletRequest httpServletRequest) {
				String studentsFLName = RequestUtil.getValueFromRequest(httpServletRequest, RequestUtil.FIRST_LAST_NAME, true, "");
				int studentCountBeforeSave = 0;
				if (studentsFLName != null
						&& studentsFLName.trim().length() > 1) {
					studentCountBeforeSave = studentsFLName.split("#").length;
				}
				
				SessionStudent[] sessionStuds=session.getStudents();
				
				if (studentCountBeforeSave > 0) {
					String[] studs = studentsFLName.split("#");
					for (String std : studs) {
						StringTokenizer st = new StringTokenizer(std, ":");
						
						Integer studentId=null;
						String firstName=null;
						String lastName=null;
						
						while (st.hasMoreTokens()) {
							StringTokenizer keyVal = new StringTokenizer(st.nextToken(), "=");
							
							String key = keyVal.nextToken();
							String val = null;
							if(keyVal.countTokens()>0) {
								val= keyVal.nextToken();
							}
	
							if (key.equalsIgnoreCase("studentId")) {
								studentId=Integer.valueOf(val);
							} else if (key.equalsIgnoreCase("firstName")) {
								firstName=val;
							} else if (key.equalsIgnoreCase("lastName")) {
								lastName=val;
							}
						}
						
						for(SessionStudent sstd:sessionStuds) {
							if(sstd.getStudentId().intValue()==studentId.intValue()) {
								sstd.setFirstName(firstName);
								sstd.setLastName(lastName);
								break;
							}
						}
					}
				
				}
			}
			
	/**
     * @jpf:action is blank
     * 
     * This method takes roster id as input from Request Scope, generates Printable
     * Respone Report in PDF Format and makes the PDF available for download
     * 
     * Changes made for OAS-981 & OAS-982 story
     */ 
    @Jpf.Action()
    protected Forward getScoreDetailsGeneratePrintPDF() {
    	try{
	    	Integer testRosterID = null;
	    	if(null != getRequest().getParameter("testRosterId")) {
	    		testRosterID = Integer.valueOf(getRequest().getParameter("testRosterId"));
		        RosterElement re = getTestRosterDetails(testRosterID);
		        if(null == this.sdForAllSubtests) {
		        	this.sdForAllSubtests = buildStudentStatusScore(this.sessionId,re);
		        }
		        StudentResponseReportPdfUtils utils = new StudentResponseReportPdfUtils();
				String fileName = re.getFirstName().replace(" ","") + "_" + re.getLastName().replace(" ","") + "_" + re.getExtPin1() +"_ResponseReport";
				getResponse().setContentType("application/pdf");
		        getResponse().setHeader("Content-Disposition","attachment; filename="+fileName+".pdf");
		        getResponse().setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		        getResponse().setHeader("Pragma", "public");
				utils.setup(getResponse().getOutputStream(), sdForAllSubtests, re.getFirstName() + " " + re.getLastName(), 
						sdForAllSubtests.get(0).getTestSessionName(), re.getExtPin1());
				utils.generateReport();
	    	}
	    	else {
	    		System.out.println("Value of testRosterID is " + testRosterID + " in request scope");
	    	}
    	} catch (CTBBusinessException ce){
			ce.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }
    
    /*
	 * Start of Bulk State Reporting 
	 **/

	@Jpf.Action(forwards = { @Jpf.Forward(name = "success", path = "bulk_state_export_report.jsp") })
	protected Forward tabeBulkStateReporting() {
		System.out.println("Redirected to Bulk State Report Export page...");
		return new Forward("success");
	}

	@Jpf.Action()
	protected Forward getLoginUserOrgHierarchyBulkReport() {
		System.out.println("getLoginUserOrgHierarchyBulkReport");
		OutputStream stream = null;
		HttpServletResponse resp = null;
		try {
			resp = getResponse();
			resp.setCharacterEncoding("UTF-8");
			BulkReportData bulkReportData = new BulkReportData();
			if (this.userName == null) {
				getLoggedInUserPrincipal();
			}
			Node[] associatedNodes = scheduleTest
					.getAllTopLevelNodesForUser(this.userName);
			bulkReportData.setTopLevelNodes(associatedNodes);
			if (associatedNodes != null) {
				populateOrgStructureForReport(
						associatedNodes[0].getOrgNodeId(), bulkReportData);
			}
			Gson gson = new Gson();
			String json = gson.toJson(bulkReportData);
			resp.setContentType(CONTENT_TYPE_JSON);
			resp.flushBuffer();
			stream = resp.getOutputStream();
			stream.write(json.getBytes("UTF-8"));

		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			System.err.println("Exception while getLoginUserOrgHierarchyBulkReport for Bulk Report.");
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;

	}

	@Jpf.Action(forwards = { @Jpf.Forward(name = "success", path = "") })
	protected Forward getSelectedOrgHierarchyBulkReport() {
		System.out.println("getSelectedOrgHierarchyBulkReport");
		OutputStream stream = null;
		HttpServletResponse resp = null;
		try {
			resp = getResponse();
			resp.setCharacterEncoding("UTF-8");
			BulkReportData bulkReportData = new BulkReportData();
			Integer associatedOrgNodeId = Integer.valueOf(this.getRequest()
					.getParameter("selectedOrgId"));
			if (associatedOrgNodeId == null) {
				return null;
			}
			populateOrgStructureForReport(associatedOrgNodeId, bulkReportData);
			Gson gson = new Gson();
			String json = gson.toJson(bulkReportData);
			resp.setContentType(CONTENT_TYPE_JSON);
			resp.flushBuffer();
			stream = resp.getOutputStream();
			stream.write(json.getBytes("UTF-8"));

		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			System.err.println("Exception while getSelectedOrgHierarchyBulkReport for Bulk Report.");
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;

	}

	@Jpf.Action(
		forwards = { 
			@Jpf.Forward(name = "error", path = "bulk_state_export_report.jsp")
		}
	)
	protected Forward downloadBulkReportCSV() {
		System.out.println("downloadBulkReportCSV");

		try {
		    	String dateFlagBulkReport = this.getRequest().getParameter("dateFlagBulkReport");
		    	String startDtBulkReport = this.getRequest().getParameter("startDtBulkReport");
		    	String endDtBulkReport = this.getRequest().getParameter("endDtBulkReport");
		    	String orgArrBulkReport = this.getRequest().getParameter("orgArrBulkReport");
		    	
		    	System.out.println("dateFlagBulkReport = " + dateFlagBulkReport);
		    	System.out.println("startDtBulkReport = " + startDtBulkReport);
		    	System.out.println("endDtBulkReport = " + endDtBulkReport);
		    	System.out.println("orgArrBulkReport = " + orgArrBulkReport);
		    	
		    	// TODO : Timezone change
		    	String userTimeZone =  userManagement.getUserTimeZone(this.userName);
		    	
		    	Date startDate = DateUtils.getDateFromDateString(startDtBulkReport);
		    	Date endDate = DateUtils.getDateFromDateString(endDtBulkReport);

		        Date adjustedStartDate = com.ctb.util.DateUtils.getAdjustedDate(startDate, userTimeZone, "GMT", startDate);
		        Date adjustedEndDate = com.ctb.util.DateUtils.getAdjustedDate(endDate, userTimeZone, "GMT", endDate);
		    	
		    	Map<String, String> orgHierarchyMap = getOrgHierarchyMap(orgArrBulkReport);
		    	
		    	System.out.println("orgHierarchyMap = " + orgHierarchyMap);
		    	
		    	Integer customerId = this.customerId;
		    	System.out.println("customerId = " + customerId);
		    	
		    	Map<String, Object> paramMap = new HashMap<String, Object>();
		    	paramMap.put("dateFlagBulkReport", dateFlagBulkReport);
		    	paramMap.put("startDtBulkReport", com.ctb.util.DateUtils.formatDateToDateString(adjustedStartDate));
		    	paramMap.put("endDtBulkReport", com.ctb.util.DateUtils.formatDateToDateString(adjustedEndDate));
		    	paramMap.put("orgHierarchyMap", orgHierarchyMap);
		    	paramMap.put("customerId", customerId);
		    	
		    	
		    	try {
		    	    	LiteracyProExportData[] tableData = scheduleTest.getBulkReportCSVData(paramMap);
        		    	byte[] data = LayoutUtil.getLiteracyProExportDataBytes(tableData);
        		    	String fileName = "BulkReport.csv";
        		    	HttpServletResponse resp = this.getResponse();        
        		        String bodypart = "attachment; filename=\"" + fileName + "\" ";
        		
        		        resp.setContentType("text/csv");
        		        resp.setHeader("Content-Disposition", bodypart);
        		        resp.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        		        resp.setHeader("Cache-Control", "cache");
        		        resp.setHeader("Pragma", "public");
        		        resp.flushBuffer();
        		
        		        OutputStream stream = resp.getOutputStream();
        		        stream.write(data);
        		        stream.close();
        		        System.out.println("End File Download");
		    	} catch (Exception e) {
    		    		System.err.println("Exception while wtite to stream: downloadBulkReportCSV for Bulk Report.");
    		    		e.printStackTrace();
		    	}

		} catch (Exception e) {
			System.err.println("Exception while downloadBulkReportCSV for Bulk Report.");
			e.printStackTrace();
		} finally {
			System.out.println("Finally block: downloadBulkReportCSV");
		}
		return null;
	}
	
	   /*private void adjustSessionTimesToGMT(TestSession session) {
	        Date originalStartTime = session.getDailyLoginStartTime();
	        Date startOffSetDate = concatinateDateTime(session.getLoginStartDate(), originalStartTime);
	        
	        Date originalEndTime = session.getDailyLoginEndTime();
	        Date endOffSetDate = concatinateDateTime(session.getLoginEndDate(), originalEndTime);
			
			String userTimeZone =  userManagement.getUserTimeZone(this.userName);

	        Date adjustedStartDate = DateUtils.getAdjustedDate(startOffSetDate, userTimeZone, "GMT", startOffSetDate);
	        Date adjustedEndDate = DateUtils.getAdjustedDate(endOffSetDate, userTimeZone, "GMT", endOffSetDate);
	        
	       
	        session.setDailyLoginStartTime(adjustedStartDate);
	        session.setDailyLoginEndTime(adjustedEndDate);
	        session.setLoginStartDate(adjustedStartDate);
	        session.setLoginEndDate(adjustedEndDate);
	    }*/
	
	private Map<String, String> getOrgHierarchyMap(String orgArr) {
	    	Map<String, String> orgHierarchyMap = new LinkedHashMap<String, String>();
        	if (orgArr != null && !orgArr.isEmpty()) {
        	    for (String level_orgNodeIds : orgArr.split(",")) {
        		String[] level_orgNodeId = level_orgNodeIds.split("_");
        		String level = level_orgNodeId[0];
        		String orgNodeId = level_orgNodeId[1];
        		orgHierarchyMap.put(level, orgNodeId);
        	    }
        	}
        	return orgHierarchyMap;
        }

	/**
	 * Populates the org structure for the bulk state reporting
	 * @param orgNodeId
	 */
	private void populateOrgStructureForReport(Integer orgNodeId, BulkReportData bulkReportData) throws Exception {
		OrgNodeCategory[] customerHierarchyStructure = null;
		Node[] parentOrgDetails = null;
		AncestorOrgDetails[] childrenOrgNodes = null;
		AncestorOrgDetails childTopNode = null;
		try {
			customerHierarchyStructure = scheduleTest.getCustomerOrgStructure(orgNodeId);
			parentOrgDetails = scheduleTest.getParentOrgDetails(orgNodeId);
			childrenOrgNodes = scheduleTest.getChildrenOrgDetails(orgNodeId);
			childTopNode = processChildrenNodesList(childrenOrgNodes);
			bulkReportData.setOrgNodeCategoryList(customerHierarchyStructure);
			bulkReportData.setParentHierarchyDetails(parentOrgDetails);
			bulkReportData.setChildLevelNodes(childTopNode);
		} catch (Exception e) {
			System.err.println("Exception while populateOrgStructureForReport for Bulk Report.");
			throw e;
		}
	}

	private AncestorOrgDetails processChildrenNodesList(
			AncestorOrgDetails[] childrenOrgNodes) throws Exception {
		try {
			Map<Integer, AncestorOrgDetails> nodeDetailsMap = new HashMap<Integer, AncestorOrgDetails>();
			for (int index = 0; index < childrenOrgNodes.length; index++) {
				AncestorOrgDetails orgDetails = childrenOrgNodes[index];
				Integer orgNodeId = orgDetails.getOrgNodeId();
				Integer parentId = orgDetails.getParentOrgNodeId();
				nodeDetailsMap.put(orgNodeId, orgDetails);
				if (index != 0) {
					nodeDetailsMap.get(parentId).getChildrenNodes().add(
							orgDetails);
				}
			}
			return childrenOrgNodes[0];
		} catch (Exception e) {
			System.err.println("Exception while processChildrenNodesList for Bulk Report.");
			throw e;
		}
	}

	/*
	 * End of Bulk State Reporting 
	 * */
	
}