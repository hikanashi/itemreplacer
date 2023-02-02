package com.jamasoftware.services.itemreplacer.window;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.jamasoftware.services.restclient.JamaConfig;

public class MainGUI extends JFrame implements IJamaConfigEventListener {
	private static final int TABINDEX_SETTING = 0;
	private static final int TABINDEX_REPLACE = 1;

	private JTabbedPane tabbedPane_;
	private SettingConnectionTab settingGUI_;
	private ReplaceTab replaceGUI_;

	public MainGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Jama Connect Replacer");
		setBounds(0, 0, 600, 700);
		buildGui();
	}

	private void buildGui() {

		// JTabbedPane
		tabbedPane_ = new JTabbedPane();
		add(tabbedPane_);

		settingGUI_ = new SettingConnectionTab(this);
		tabbedPane_.addTab("Connection Setting", settingGUI_);

		replaceGUI_ = new ReplaceTab();
		tabbedPane_.addTab("Replace", replaceGUI_);
		SettingChanged(settingGUI_.getConfig());

	}

	@Override
	public void SettingChanged(JamaConfig config) {
		if (settingGUI_ == null) {
			return;
		}

		if (config != null) {
			tabbedPane_.setEnabledAt(TABINDEX_REPLACE, true);
			tabbedPane_.setSelectedIndex(TABINDEX_REPLACE);
			replaceGUI_.setConfig(config);
		} else {
			tabbedPane_.setEnabledAt(TABINDEX_REPLACE, false);
			tabbedPane_.setSelectedIndex(TABINDEX_SETTING);
			replaceGUI_.setConfig(config);
		}
	}

}
