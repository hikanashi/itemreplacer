package com.jamasoftware.services.itemreplacer.Jamamodel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jamasoftware.services.restclient.JamaParent;
import com.jamasoftware.services.restclient.exception.RestClientException;
import com.jamasoftware.services.restclient.jamadomain.lazyresources.JamaItem;
import com.jamasoftware.services.restclient.jamadomain.lazyresources.JamaProject;
import com.jamasoftware.services.restclient.jamadomain.stagingresources.StagingItem;
import com.jamasoftware.services.restclient.jamadomain.values.JamaFieldValue;
import com.jamasoftware.services.restclient.jamadomain.values.*;

public class JamaTableItem {
    private static final Logger logger_ = LogManager.getLogger(JamaTableItem.class);

    private Boolean target_ = false;
    private JamaItem item_ = null;
    private boolean isReplaced_ = false;
    private Boolean viewdiff_ = false;

    private String fieldName_ = null;
    private Matcher matcher_ = null;
    private String sourceValue_ = null;

    public JamaTableItem(JamaItem item) {
        item_ = item;
    }

    public boolean getTarget() {
        return target_;
    }

    public void setTarget(boolean target) {
        target_ = target;
    }

    public boolean getViewDiff() {
        return viewdiff_;
    }

    public void setViewDiff(boolean view) {
        viewdiff_ = view;
    }

    public JamaItem getItem() {
        return item_;
    }

    public String getSource() {
        return sourceValue_;
    }

    public boolean isReplaced() {
        return isReplaced_;
    }

    public boolean setSearchKey(String field, String searchKey) throws RestClientException {
        fieldName_ = field;

        sourceValue_ = getFieldValueByName(fieldName_);
        matcher_ = Pattern.compile(searchKey).matcher(sourceValue_);
        boolean ismatch = matcher_.find();

        logger_.debug("setSearchKey Field:" + fieldName_ + " Value:" + sourceValue_ + " searchKey:" + searchKey
                + " match:" + ismatch);
        return ismatch;
    }

    public String getReplaceData(String replaceKey) {
        if (matcher_ == null) {
            return sourceValue_;
        }

        if (replaceKey == null || replaceKey.length() < 1) {
            return sourceValue_;
        }

        String destValue = matcher_.replaceAll(replaceKey);
        return destValue;
    }

    public int getID() {
        if(item_ == null) {
            return -1;
        }

        Integer id = item_.getId();
        if (id != null) {
            return id.intValue();
        }

        return -1;
    }

    public String getName() {
        if(item_ == null) {
            return null;
        }

        TextFieldValue value = item_.getName();
        if (value == null) {
            return null;
        }

        return value.getValue();
    }

    public String getItemPath() {
        StringBuilder itemPath = new StringBuilder();
        if(item_ == null) {
            return itemPath.toString();
        }

        JamaParent jamaParent = item_.getParent();
        while(jamaParent != null) {
            if(jamaParent.isProject()) {
                JamaProject project = (JamaProject)jamaParent;
                itemPath.append(project.getName() + "/");
                break;
            } else {
                JamaItem item = (JamaItem)jamaParent;
                itemPath.append(item.getName() + "/");
                jamaParent = item.getParent();
            }
        }

        return itemPath.toString();
    }

    public String getlockedby() {
        if(item_ == null) {
            return "";
        }

        if (item_.isLocked() != true) {
            return "";
        }

        return item_.lockedBy().getUsername();
    }

    public String lastLockedDate() {
        if(item_ == null) {
            return "";
        }
        
        if (item_.isLocked() != true) {
            return "";
        }

        return item_.lastLockedDate().toString();
    }

    private JamaFieldValue getFieldValue(JamaItem item, String fieldName) {
        if(fieldName.equalsIgnoreCase("name")) {
            return item.getName();
        }

        for (JamaFieldValue fieldValue : item.getFieldValues()) {
            logger_.debug("GetFieldValue id:" + item.getId() + " FieldName:" + fieldValue.getLabel());
            if (fieldValue.getLabel().equals(fieldName)) {
                return fieldValue;
            }
        }

        return null;
    }

    private String getFieldValueByName(String field) throws RestClientException {
        if (item_ == null) {
            return "";
        }

        JamaFieldValue fieldvalue = getFieldValue(item_, field);
        if (fieldvalue == null) {
            throw new RestClientException("That field is not exist. field:" + fieldName_);
        }

        String value = null;
        if (fieldvalue instanceof TextFieldValue) {
            TextFieldValue textvalue = (TextFieldValue) fieldvalue;
            value = textvalue.getValue();
        } else if (fieldvalue instanceof RichTextFieldValue) {
            RichTextFieldValue textvalue = (RichTextFieldValue) fieldvalue;
            value = textvalue.getValue().getValue();
        } else if (fieldvalue instanceof TextBoxFieldValue) {
            TextBoxFieldValue textvalue = (TextBoxFieldValue) fieldvalue;
            value = textvalue.getValue();
        } else {
            throw new RestClientException("That field type is not supported. field:" + field);
        }

        if (value != null) {
            return value;
        }

        return "";
    }

    public void replaceItem(String replaceKey) throws RestClientException {
        if (item_ == null) {
            return;
        }

        if (item_.isLocked()) {
            throw new RestClientException("Item is locked by " + getlockedby()
                    + " id:" + item_.getId() + " Name:" + item_.getName().getValue());
        }

        StagingItem stageitem = item_.edit();
        if (stageitem == null) {
            throw new RestClientException("JamaItem can't edit. id:" + item_.getId());
        }

        JamaFieldValue fieldvalue = getFieldValue(stageitem, fieldName_);
        if (fieldvalue == null) {
            throw new RestClientException("That field is not exist. field:" + fieldName_);
        }

        String destValue = getReplaceData(replaceKey);

        if (fieldvalue instanceof TextFieldValue) {
            TextFieldValue textvalue = (TextFieldValue) fieldvalue;
            textvalue.setValue(destValue);
        } else if (fieldvalue instanceof RichTextFieldValue) {
            RichTextFieldValue textvalue = (RichTextFieldValue) fieldvalue;
            textvalue.getValue().setValue(destValue);
        } else if (fieldvalue instanceof TextBoxFieldValue) {
            TextBoxFieldValue textvalue = (TextBoxFieldValue) fieldvalue;
            textvalue.setValue(destValue);
        } else {
            throw new RestClientException("That field type is not supported. field:" + fieldName_);
        }

        boolean lockResult = stageitem.acquireLock();
        if(lockResult != true) {
            throw new RestClientException("Lock Faild itemid:"+ stageitem.getId() + " Name:" + stageitem.getName().getValue());
        }

        stageitem.commit();
        stageitem.unlock();

        target_ = false;
        isReplaced_ = true;
    }
}
