package com.poc.oas.domain;

import java.util.Date;

public class AddressBean {

	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String country;
	private String zipcode;
	private String primaryPhone;
	private String secondaryPhone;
	private String fax;
	private Date createdDatetime;
	private Date updatedDatetime;
	private String createdBy;
	private String updatedBy;
	private long dataImportHistoryId;
	private String primaryPhoneExt;
	private String secondaryPhoneExt;
	
	
	
	public AddressBean(String addressLine1, String addressLine2, String city,
			String state, String country, String zipcode, String primaryPhone,
			String secondaryPhone, String fax, Date createdDatetime,
			Date updatedDatetime, String createdBy, String updatedBy,
			long dataImportHistoryId, String primaryPhoneExt,
			String secondaryPhoneExt) {
		super();
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.state = state;
		this.country = country;
		this.zipcode = zipcode;
		this.primaryPhone = primaryPhone;
		this.secondaryPhone = secondaryPhone;
		this.fax = fax;
		this.createdDatetime = createdDatetime;
		this.updatedDatetime = updatedDatetime;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.dataImportHistoryId = dataImportHistoryId;
		this.primaryPhoneExt = primaryPhoneExt;
		this.secondaryPhoneExt = secondaryPhoneExt;
	}
	@Override
	public String toString() {
		return "AddressBean [addressLine1=" + addressLine1 + ", addressLine2="
				+ addressLine2 + ", city=" + city + ", state=" + state
				+ ", country=" + country + ", zipcode=" + zipcode
				+ ", primaryPhone=" + primaryPhone + ", secondaryPhone="
				+ secondaryPhone + ", fax=" + fax + ", createdDatetime="
				+ createdDatetime + ", updatedDatetime=" + updatedDatetime
				+ ", createdBy=" + createdBy + ", updatedBy=" + updatedBy
				+ ", dataImportHistoryId=" + dataImportHistoryId
				+ ", primaryPhoneExt=" + primaryPhoneExt
				+ ", secondaryPhoneExt=" + secondaryPhoneExt + "]";
	}
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getAddressLine2() {
		return addressLine2;
	}
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getPrimaryPhone() {
		return primaryPhone;
	}
	public void setPrimaryPhone(String primaryPhone) {
		this.primaryPhone = primaryPhone;
	}
	public String getSecondaryPhone() {
		return secondaryPhone;
	}
	public void setSecondaryPhone(String secondaryPhone) {
		this.secondaryPhone = secondaryPhone;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public Date getCreatedDatetime() {
		return createdDatetime;
	}
	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
	}
	public Date getUpdatedDatetime() {
		return updatedDatetime;
	}
	public void setUpdatedDatetime(Date updatedDatetime) {
		this.updatedDatetime = updatedDatetime;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public long getDataImportHistoryId() {
		return dataImportHistoryId;
	}
	public void setDataImportHistoryId(long dataImportHistoryId) {
		this.dataImportHistoryId = dataImportHistoryId;
	}
	public String getPrimaryPhoneExt() {
		return primaryPhoneExt;
	}
	public void setPrimaryPhoneExt(String primaryPhoneExt) {
		this.primaryPhoneExt = primaryPhoneExt;
	}
	public String getSecondaryPhoneExt() {
		return secondaryPhoneExt;
	}
	public void setSecondaryPhoneExt(String secondaryPhoneExt) {
		this.secondaryPhoneExt = secondaryPhoneExt;
	}
	
	
}
