package com.jamasoftware.services.itemreplacer.Jamamodel;

import javax.swing.SwingWorker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jamasoftware.services.restclient.jamadomain.core.JamaInstance;
import com.jamasoftware.services.restclient.jamadomain.lazyresources.JamaItem;

public class GetJamaItemWorker extends SwingWorker<JamaItem, Object> {
    private static final Logger logger_ = LogManager.getLogger(GetJamaItemWorker.class);
    private IJamaItemEventListener listener_ = null;
    private JamaInstance jamaInstance_ = null;
    private int itemid_ = 0;

    public GetJamaItemWorker(JamaInstance jamaInstance, int itemid, IJamaItemEventListener listener) {
        jamaInstance_ = jamaInstance;
        itemid_ = itemid;
        listener_ = listener;
    }

    @Override
    public JamaItem doInBackground() {
        try {
            JamaItem item = jamaInstance_.getItem(itemid_);
            return item;

        } catch (Exception e) {
            logger_.warn("Root JamaItem can't get id:" + itemid_, e);
            return null;
        }

    }

    @Override
    protected void done() {
        if(listener_ == null) {
            return;
        }
        
        try {
            listener_.GetFinished(get());
        } catch (Exception e) {
            logger_.error("GetJamaItem faild" + itemid_, e);
        }
    }
}
