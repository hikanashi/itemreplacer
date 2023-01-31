package com.jamasoftware.services.itemreplacer.Jamamodel;

import java.util.List;

import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jamasoftware.services.restclient.exception.RestClientException;
import com.jamasoftware.services.restclient.jamadomain.lazyresources.JamaItem;

public class SearchJamaItemWorker extends SwingWorker<Boolean, JamaTableItem> {
    private static final Logger logger_ = LogManager.getLogger(SearchJamaItemWorker.class);
    private IJamaItemEventListener listener_ = null;

    private JamaItem rootItem_ = null;
    private String fieldName_;
    private String searchKey_;

    public SearchJamaItemWorker(JamaItem rootItem, String fieldName, String searchKey, IJamaItemEventListener listener) {
        rootItem_ = rootItem;
        fieldName_ = fieldName;
        searchKey_ = searchKey;
        listener_ = listener;
    }

    @Override
    public Boolean doInBackground() {
        try {
            searchItem(rootItem_, fieldName_, searchKey_);
            return true;
        } catch (Exception e) {
            logger_.warn("search process fail. field:" + fieldName_ + " key:" + searchKey_, e);
            return false;
        }
    }

    private void searchItem(JamaItem jamaParent, String fieldName, String searchKey) throws RestClientException {
        JamaTableItem item = new JamaTableItem(jamaParent);
        boolean matchkey = item.setSearchKey(fieldName, searchKey);
        if (matchkey) {
            publish(item);
        }
    
        try {
          List<JamaItem> childlen = jamaParent.getChildren();
          if (childlen == null) {
            return;
          }
    
          for (JamaItem child : childlen) {
            searchItem(child, fieldName, searchKey);
          }
        } catch (Exception e) {
          logger_.error(e);
        }
      }

    @Override
    protected void process(List<JamaTableItem> results) {
        if(listener_ == null) {
            return;
        }
        listener_.SearchProgress(results);
    }

    @Override
    protected void done() {
        if(listener_ == null) {
            return;
        }

        try {
            listener_.SearchFinished(get().booleanValue());
        } catch (Exception e) {
            logger_.error("Search done faild field:" + fieldName_ + " key:" + searchKey_, e);
        }
    }
}
