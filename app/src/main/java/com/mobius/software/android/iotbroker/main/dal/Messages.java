package com.mobius.software.android.iotbroker.main.dal;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "MESSAGES".
 */
public class Messages {

    private Long id;
    private String messageItem;
    private Integer qos;
    private Boolean isIncoming;
    private String topicName;
    private long accountID;

    public Messages() {
    }

    public Messages(Long id) {
        this.id = id;
    }

    public Messages(Long id, String messageItem, Integer qos, Boolean isIncoming, String topicName, long accountID) {
        this.id = id;
        this.messageItem = messageItem;
        this.qos = qos;
        this.isIncoming = isIncoming;
        this.topicName = topicName;
        this.accountID = accountID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageItem() {
        return messageItem;
    }

    public void setMessageItem(String messageItem) {
        this.messageItem = messageItem;
    }

    public Integer getQos() {
        return qos;
    }

    public void setQos(Integer qos) {
        this.qos = qos;
    }

    public Boolean getIsIncoming() {
        return isIncoming;
    }

    public void setIsIncoming(Boolean isIncoming) {
        this.isIncoming = isIncoming;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public long getAccountID() {
        return accountID;
    }

    public void setAccountID(long accountID) {
        this.accountID = accountID;
    }

}
