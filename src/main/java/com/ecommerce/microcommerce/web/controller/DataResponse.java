package com.ecommerce.microcommerce.web.controller;

import java.util.List;

import com.ecommerce.microcommerce.model.Product;

public class DataResponse {
	
	private Boolean success;
    private Integer count;
    private List<?> data;
   
    public void setSuccess(Boolean bool) {
    	this.success = bool;
    }
    
    public Boolean getSuccess() {
    	return this.success;
    }
    
    public void setCount(Integer count) {
    	this.count = count;
    }
    
    public Integer getCount() {
    	return this.count;
    }
    
    public void setData(List<?> data) {
    	this.data = data;
    }
    
    public List<?> getData() {
    	return this.data;
    }


}
