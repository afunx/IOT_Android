package com.espressif.iot.db.greenrobot.daoDevice;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table USER__DEVICE.
 */
public class user__device {

    private Long id;
    private Long userId;
    private Long deviceId;
    private Boolean isOwner;
    private String token;

    public user__device() {
    }

    public user__device(Long id) {
        this.id = id;
    }

    public user__device(Long id, Long userId, Long deviceId, Boolean isOwner, String token) {
        this.id = id;
        this.userId = userId;
        this.deviceId = deviceId;
        this.isOwner = isOwner;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Boolean getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(Boolean isOwner) {
        this.isOwner = isOwner;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}