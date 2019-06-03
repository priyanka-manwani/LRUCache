package com.lrucache;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;


public class LRUCacheResponse {

    enum ResponseStatus{HIT,MISS,EVICTED,PUT_SUCCESSFUL}

    @JsonIgnore
    ResponseStatus responseStatus;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int key;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int value;

     public LRUCacheResponse(ResponseStatus responseStatus,int key, int value){
        this.responseStatus = responseStatus;
        this.key = key;
        this.value = value;
    }

    public LRUCacheResponse(ResponseStatus responseStatus){
        this.responseStatus = responseStatus;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString(){
        return "Entry[ResposeStatus = "+this.responseStatus+" Key = "+this.key+" Value = "+this.value +"]";
    }
}
