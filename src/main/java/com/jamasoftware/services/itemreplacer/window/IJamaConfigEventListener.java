package com.jamasoftware.services.itemreplacer.window;

import com.jamasoftware.services.restclient.JamaConfig;

public interface IJamaConfigEventListener {

    void SettingChanged(JamaConfig config);
}
