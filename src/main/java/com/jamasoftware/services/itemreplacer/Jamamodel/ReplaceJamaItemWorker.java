package com.jamasoftware.services.itemreplacer.Jamamodel;

import java.util.Iterator;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jamasoftware.services.restclient.exception.RestClientException;

public class ReplaceJamaItemWorker extends SwingWorker<Boolean, JamaTableItem> {
    private static final Logger logger_ = LogManager.getLogger(ReplaceJamaItemWorker.class);
    private IJamaItemEventListener listener_ = null;
    private List<JamaTableItem> models_;
    private String replaceKey_;

    public ReplaceJamaItemWorker(List<JamaTableItem> models, String replaceKey, IJamaItemEventListener listener) {
        models_ = models;
        replaceKey_ = replaceKey;
        listener_ = listener;
    }

    @Override
    public Boolean doInBackground() {
        try {
            Iterator<JamaTableItem> itr = models_.iterator();
            while (itr.hasNext()) {
                JamaTableItem item = itr.next();
                if (item.getTarget() != true) {
                    continue;
                }
    
                replaceSelectedItem(item, replaceKey_);
                publish(item);
            }
            return true;
        } catch (Exception e) {
            logger_.warn("replace progress fail. replacekey:" + replaceKey_ + " :" + e.getMessage(), e);
            return false;
        }
    }

    private void replaceSelectedItem(JamaTableItem item, String replaceKey) throws RestClientException {
        if (item == null) {
            throw new RestClientException("Target item is not exist");
        }

        item.replaceItem(replaceKey);
    }

    @Override
    protected void process(List<JamaTableItem> results) {
        if (listener_ == null) {
            return;
        }
        listener_.ReplaceProgress(results);
    }

    @Override
    protected void done() {
        if (listener_ == null) {
            return;
        }

        try {
            listener_.ReplaceFinished(get().booleanValue());
        } catch (Exception e) {
            logger_.error("Replace done faild key:" + replaceKey_, e);
        }
    }
}
