package com.poc.onlinebank.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.poc.common.AccountType;

public class Account {
	
	@NotEmpty(message="This account number cann't be empty")
	private String accountName;
	private long accountNumber;
	@NotNull
	private AccountType accountType;
	@NotNull(message="Hey description cann't be null")
	private String accountDescription;
	@NotNull
	private String accountOperationMessage;
	private String accountStatus;
	private Date accountOpeningDate;
	private Date accountClosingDate;
	private Date createdDate;
	private Date modifyDate;
	private String createdBy;
	private String modifiedBy;
	private BigDecimal balance;
	private BigDecimal availableBalance;
	
	
	public String getAccountStatus() {
		return accountStatus;
	}
	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}
	public Date getAccountOpeningDate() {
		return accountOpeningDate;
	}
	public void setAccountOpeningDate(Date accountOpeningDate) {
		this.accountOpeningDate = accountOpeningDate;
	}
	public Date getAccountClosingDate() {
		return accountClosingDate;
	}
	public void setAccountClosingDate(Date accountClosingDate) {
		this.accountClosingDate = accountClosingDate;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public long getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}
	public AccountType getAccountType() {
		return accountType;
	}
	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}
	public String getAccountDescription() {
		return accountDescription;
	}
	public void setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
	}
	public String getAccountOperationMessage() {
		return accountOperationMessage;
	}
	public void setAccountOperationMessage(String accountOperationMessage) {
		this.accountOperationMessage = accountOperationMessage;
	}
	
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	public BigDecimal getAvailableBalance() {
		return availableBalance;
	}
	public void setAvailableBalance(BigDecimal availableBalance) {
		this.availableBalance = availableBalance;
	}
	@Override
	public String toString() {
		return "Account [accountName=" + accountName + ", accountNumber="
				+ accountNumber + ", accountType=" + accountType
				+ ", accountDescription=" + accountDescription
				+ ", accountOperationMessage=" + accountOperationMessage
				+ ", accountStatus=" + accountStatus + ", accountOpeningDate="
				+ accountOpeningDate + ", accountClosingDate="
				+ accountClosingDate + ", createdDate=" + createdDate
				+ ", modifyDate=" + modifyDate + ", createdBy=" + createdBy
				+ ", modifiedBy=" + modifiedBy + ", balance=" + balance
				+ ", availableBalance=" + availableBalance + "]";
	}

}
