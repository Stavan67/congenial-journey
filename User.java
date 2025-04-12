package com.decor.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    private String userId;
    private String businessName;
    private int yearEstablished;
    private String gstNumber;
    private String phone;
    private String email;
    private String address;
    private String licenseDocumentUrl;
    private String referralCode;
    private boolean isApproved;
    private long createdAt;

    // Constructor
    public User(@NonNull String userId, String businessName, int yearEstablished,
                String gstNumber, String phone, String email, String address,
                String licenseDocumentUrl, String referralCode, boolean isApproved, long createdAt) {
        this.userId = userId;
        this.businessName = businessName;
        this.yearEstablished = yearEstablished;
        this.gstNumber = gstNumber;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.licenseDocumentUrl = licenseDocumentUrl;
        this.referralCode = referralCode;
        this.isApproved = isApproved;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public int getYearEstablished() { return yearEstablished; }
    public void setYearEstablished(int yearEstablished) { this.yearEstablished = yearEstablished; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getLicenseDocumentUrl() { return licenseDocumentUrl; }
    public void setLicenseDocumentUrl(String licenseDocumentUrl) { this.licenseDocumentUrl = licenseDocumentUrl; }

    public String getReferralCode() { return referralCode; }
    public void setReferralCode(String referralCode) { this.referralCode = referralCode; }

    public boolean isApproved() { return isApproved; }
    public void setApproved(boolean approved) { isApproved = approved; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}