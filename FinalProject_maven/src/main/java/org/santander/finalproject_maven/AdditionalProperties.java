/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

/**
 *
 * @author sznicci
 */
public class AdditionalProperties {
    
    private String category;
    private String key;
    private String sourceSystemKey;
    private int value;

    public AdditionalProperties() {
    }

    public AdditionalProperties(String category, String key, String sourceSystemKey, int value) {
        this.category = category;
        this.key = key;
        this.sourceSystemKey = sourceSystemKey;
        this.value = value;
    }    
    
    public String getCategory() {
        return category;
    }

    public String getKey() {
        return key;
    }

    public String getSourceSystemKey() {
        return sourceSystemKey;
    }

    public int getValue() {
        return value;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setSourceSystemKey(String sourceSystemKey) {
        this.sourceSystemKey = sourceSystemKey;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    
}
