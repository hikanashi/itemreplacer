package com.jamasoftware.services.itemreplacer.Jamamodel;

import java.util.List;

import com.jamasoftware.services.restclient.jamadomain.lazyresources.JamaItem;

public interface IJamaItemEventListener {

    public void GetFinished(JamaItem item);
    public void SearchProgress(List<JamaTableItem> results);
    public void SearchFinished(boolean result);
    public void ReplaceProgress(List<JamaTableItem> results);
    public void ReplaceFinished(boolean result);
}
