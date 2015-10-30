package com.poc.oas.domain;

import java.util.Date;

public class UserBean{
	private long userId;
	private String userName;
	private String password;
	private String firstName;
	private String middleName;
	private String lastName;
	private String email;
	private Date passwordExpirationDate;
	private AddressBean address;
	private String activeSession;
	private String timezone;
	
	private String passwordHintQuestion;
	private String passwordHintAnswer;
	private String resetPassword;
	private Date lastLoginDate;
	private String prefix;
	private String suffix;
	private String preferredName;
	private String extSchoolId;
	private long dataImportHistoryId;
	private String extPin1;
	private String extPin2;
	private String extPin3;
	private String activationStatus;
	private String displayUserName;
	private String displayNewMessage;
	
	public UserBean(long userId, String userName, String password,
			String firstName, String middleName, String lastName, String email,
			Date passwordExpirationDate, AddressBean address,
			String activeSession, String timezone, String passwordHintQuestion,
			String passwordHintAnswer, String resetPassword,
			Date lastLoginDate, String prefix, String suffix,
			String preferredName, String extSchoolId, long dataImportHistoryId,
			String extPin1, String extPin2, String extPin3,
			String activationStatus, String displayUserName,
			String displayNewMessage) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.password = password;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.email = email;
		this.passwordExpirationDate = passwordExpirationDate;
		this.address = address;
		this.activeSession = activeSession;
		this.timezone = timezone;
		this.passwordHintQuestion = passwordHintQuestion;
		this.passwordHintAnswer = passwordHintAnswer;
		this.resetPassword = resetPassword;
		this.lastLoginDate = lastLoginDate;
		this.prefix = prefix;
		this.suffix = suffix;
		this.preferredName = preferredName;
		this.extSchoolId = extSchoolId;
		this.dataImportHistoryId = dataImportHistoryId;
		this.extPin1 = extPin1;
		this.extPin2 = extPin2;
		this.extPin3 = extPin3;
		this.activationStatus = activationStatus;
		this.displayUserName = displayUserName;
		this.displayNewMessage = displayNewMessage;
	}

	@Override
	public String toString() {
		return "UserBean [userId=" + userId + ", userName=" + userName
				+ ", password=" + password + ", firstName=" + firstName
				+ ", middleName=" + middleName + ", lastName=" + lastName
				+ ", email=" + email + ", passwordExpirationDate="
				+ passwordExpirationDate + ", address=" + address
				+ ", activeSession=" + activeSession + ", timezone=" + timezone
				+ ", passwordHintQuestion=" + passwordHintQuestion
				+ ", passwordHintAnswer=" + passwordHintAnswer
				+ ", resetPassword=" + resetPassword + ", lastLoginDate="
				+ lastLoginDate + ", prefix=" + prefix + ", suffix=" + suffix
				+ ", preferredName=" + preferredName + ", extSchoolId="
				+ extSchoolId + ", dataImportHistoryId=" + dataImportHistoryId
				+ ", extPin1=" + extPin1 + ", extPin2=" + extPin2
				+ ", extPin3=" + extPin3 + ", activationStatus="
				+ activationStatus + ", displayUserName=" + displayUserName
				+ ", displayNewMessage=" + displayNewMessage + "]";
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getPasswordExpirationDate() {
		return passwordExpirationDate;
	}

	public void setPasswordExpirationDate(Date passwordExpirationDate) {
		this.passwordExpirationDate = passwordExpirationDate;
	}

	public AddressBean getAddress() {
		return address;
	}

	public void setAddress(AddressBean address) {
		this.address = address;
	}

	public String getActiveSession() {
		return activeSession;
	}

	public void setActiveSession(String activeSession) {
		this.activeSession = activeSession;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getPasswordHintQuestion() {
		return passwordHintQuestion;
	}

	public void setPasswordHintQuestion(String passwordHintQuestion) {
		this.passwordHintQuestion = passwordHintQuestion;
	}

	public String getPasswordHintAnswer() {
		return passwordHintAnswer;
	}

	public void setPasswordHintAnswer(String passwordHintAnswer) {
		this.passwordHintAnswer = passwordHintAnswer;
	}

	public String getResetPassword() {
		return resetPassword;
	}

	public void setResetPassword(String resetPassword) {
		this.resetPassword = resetPassword;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getPreferredName() {
		return preferredName;
	}

	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
	}

	public String getExtSchoolId() {
		return extSchoolId;
	}

	public void setExtSchoolId(String extSchoolId) {
		this.extSchoolId = extSchoolId;
	}

	public long getDataImportHistoryId() {
		return dataImportHistoryId;
	}

	public void setDataImportHistoryId(long dataImportHistoryId) {
		this.dataImportHistoryId = dataImportHistoryId;
	}

	public String getExtPin1() {
		return extPin1;
	}

	public void setExtPin1(String extPin1) {
		this.extPin1 = extPin1;
	}

	public String getExtPin2() {
		return extPin2;
	}

	public void setExtPin2(String extPin2) {
		this.extPin2 = extPin2;
	}

	public String getExtPin3() {
		return extPin3;
	}

	public void setExtPin3(String extPin3) {
		this.extPin3 = extPin3;
	}

	public String getActivationStatus() {
		return activationStatus;
	}

	public void setActivationStatus(String activationStatus) {
		this.activationStatus = activationStatus;
	}

	public String getDisplayUserName() {
		return displayUserName;
	}

	public void setDisplayUserName(String displayUserName) {
		this.displayUserName = displayUserName;
	}

	public String getDisplayNewMessage() {
		return displayNewMessage;
	}

	public void setDisplayNewMessage(String displayNewMessage) {
		this.displayNewMessage = displayNewMessage;
	}
	
	
	

}
