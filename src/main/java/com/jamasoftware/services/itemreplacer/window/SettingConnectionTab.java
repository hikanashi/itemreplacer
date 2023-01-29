package com.jamasoftware.services.itemreplacer.window;

import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.GridBagConstraints;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jamasoftware.services.restclient.JamaConfig;


public class SettingConnectionTab extends JPanel {
    private static final String SETTING_FILE_PATH = "jama.properties";
    private static final Logger logger_ = LogManager.getLogger(SettingConnectionTab.class);

    private GridBagLayout layout_;
    private GridBagConstraints constraints_;

    private JTextField textBaseURL_;
    private JTextField textUsername_;
    private JPasswordField  textPassword_;
    private JFormattedTextField textResourseTimeout_;
    private JTextField textApiKey_;
    private JTextField textClientCertPath_;
    private JPasswordField textClientCertPassword_;

    private IJamaConfigEventListener eventlistener_ = null;
    private JamaConfig jamaConfig_ = null;

    public SettingConnectionTab(IJamaConfigEventListener listener) {
        eventlistener_ = listener;
        try {
            Path pathobj = Paths.get(SETTING_FILE_PATH);
            if (Files.exists(pathobj) == true) {
                JamaConfig jamaConfig = new JamaConfig(true, pathobj.toAbsolutePath().toString());
                setConfig(jamaConfig);
            } else {
                setConfig(null);
            }
        } catch (Exception e) {
            logger_.warn("Load Config Error:", e);
            setConfig(null);
        }

        buildGUI();
    }

    public JamaConfig getConfig() {
        return jamaConfig_;
    }
    
    private void setConfig(JamaConfig jamaConfig) {
        jamaConfig_ = jamaConfig;

        if(eventlistener_ != null) {
            if(jamaConfig != null) {
                eventlistener_.SettingChanged(jamaConfig);
            } else {
                eventlistener_.SettingChanged(jamaConfig);
            }
        }
    }

    private void buildGUI() {
		layout_ = new GridBagLayout();
		this.setLayout(layout_);
        constraints_ = new GridBagConstraints();

        int gridY = 0;
        buildURL(gridY++);
        buildUsername(gridY++);
        buildPassword(gridY++);
        buildResourceTimeout(gridY++);
        buildApiKey(gridY++);
        buildClientCertFilePath(gridY++);
        buildClientCertPassword(gridY++);
        buildSaveButton(gridY++);

    }


    private void buildURL(int gridY) {

        JLabel labelBaseURL = new JLabel("BaseURL:");
        labelBaseURL.setHorizontalAlignment(JLabel.TRAILING);
        setLayoutConstraints(labelBaseURL, 0, gridY,1,1);
        
        textBaseURL_ = new JTextField();
        textBaseURL_.setHorizontalAlignment(JLabel.LEFT);
        textBaseURL_.setPreferredSize(new Dimension(400, 30));
        if(jamaConfig_ != null) {
            textBaseURL_.setText(jamaConfig_.getBaseUrl());
        }
        setLayoutConstraints(textBaseURL_, 1, gridY,2,1);
    }

    private void buildUsername(int gridY) {
        JLabel labelUsername = new JLabel("Username:");
        labelUsername.setHorizontalAlignment(JLabel.TRAILING);
        setLayoutConstraints(labelUsername, 0, gridY,1,1);
        
        textUsername_ = new JTextField();
        textUsername_.setHorizontalAlignment(JLabel.LEFT);
        textUsername_.setPreferredSize(new Dimension(200, 30));
        if(jamaConfig_ != null) {
            textUsername_.setText(jamaConfig_.getUsername());
        }
        setLayoutConstraints(textUsername_, 1, gridY,1,1);
    }

    private void buildPassword(int gridY) {
        JLabel labelPassword = new JLabel("Password:");
        labelPassword.setHorizontalAlignment(JLabel.TRAILING);
        setLayoutConstraints(labelPassword, 0, gridY,1,1);
        
        textPassword_ = new JPasswordField ();
        textPassword_.setHorizontalAlignment(JLabel.LEFT);
        textPassword_.setPreferredSize(new Dimension(200, 30));
        if(jamaConfig_ != null) {
            textPassword_.setText(jamaConfig_.getPassword());
        }
        setLayoutConstraints(textPassword_, 1, gridY,1,1);
    }

    private void buildResourceTimeout(int gridY) {
        JLabel labelTimeout = new JLabel("<html><body>Resource Timeout:<br />(seconds)</body></html>");
        labelTimeout.setHorizontalAlignment(JLabel.TRAILING);
        setLayoutConstraints(labelTimeout, 0, gridY,1,1);
        
        try {
            MaskFormatter timeoutFormat = new MaskFormatter("####");
            textResourseTimeout_ = new JFormattedTextField(timeoutFormat);
            if(jamaConfig_ != null) {
                textResourseTimeout_.setText(jamaConfig_.getResourceTimeOut().toString());
            } else {
                textResourseTimeout_.setText("30");
            }
            textResourseTimeout_.setColumns(3);
            textResourseTimeout_.setHorizontalAlignment(JLabel.LEFT);
            setLayoutConstraints(textResourseTimeout_, 1, gridY,1,1);
        } catch (Exception e) {
            logger_.error("MaskFormatter error:", e);
        }
    }

    private void buildApiKey(int gridY) {
        JLabel labelApikey = new JLabel("ApiKey:");
        labelApikey.setHorizontalAlignment(JLabel.TRAILING);
        setLayoutConstraints(labelApikey, 0, gridY,1,1);
        
        textApiKey_ = new JTextField();
        textApiKey_.setHorizontalAlignment(JLabel.LEFT);
        textApiKey_.setPreferredSize(new Dimension(200, 30));
        if(jamaConfig_ != null) {
            textApiKey_.setText(jamaConfig_.getApiKey());
        }
        setLayoutConstraints(textApiKey_, 1, gridY,1,1);
    }

    private void buildClientCertFilePath(int gridY) {
        JLabel labelClientCertPath = new JLabel("Client certificate file path:");
        labelClientCertPath.setHorizontalAlignment(JLabel.TRAILING);
        setLayoutConstraints(labelClientCertPath, 0, gridY,1,1);
        
        textClientCertPath_ = new JTextField();
        textClientCertPath_.setHorizontalAlignment(JLabel.LEFT);
        textClientCertPath_.setPreferredSize(new Dimension(200, 30));
        if(jamaConfig_ != null) {
            textClientCertPath_.setText(jamaConfig_.getClientCertFilePath());
        }
        setLayoutConstraints(textClientCertPath_, 1, gridY,1,1);

        JButton buttonPath = new JButton("Select");
        buttonPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectCertPath();
            }
        });
        setLayoutConstraints(buttonPath, 2, gridY,1,1);
    }

    private void selectCertPath() {
        String lastpath = textClientCertPath_.getText();
        String selectPath = null;

        JFileChooser filechooser = null;
        Path pathobj = Paths.get(lastpath);
        if (Files.exists(pathobj) == true) {
            filechooser = new JFileChooser(lastpath);
        } else {
            filechooser = new JFileChooser();
        }

        filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int selected = filechooser.showOpenDialog(null);
        if (selected == JFileChooser.APPROVE_OPTION) {
            File file = filechooser.getSelectedFile();
            selectPath = file.getAbsolutePath();

            logger_.debug("Approve Selected Target:" + selectPath);
        }

        if(selectPath != null) {
            textClientCertPath_.setText(selectPath);
        }
    }

    private void buildClientCertPassword(int gridY) {
        JLabel labelClientCertPassword = new JLabel("Client certificate file password:");
        labelClientCertPassword.setHorizontalAlignment(JLabel.TRAILING);
        setLayoutConstraints(labelClientCertPassword, 0, gridY,1,1);
        
        textClientCertPassword_ = new JPasswordField ();
        textClientCertPassword_.setHorizontalAlignment(JLabel.LEFT);
        textClientCertPassword_.setPreferredSize(new Dimension(200, 30));
        if(jamaConfig_ != null) {
            textClientCertPassword_.setText(jamaConfig_.getClientCertPassword());
        }
        setLayoutConstraints(textClientCertPassword_, 1, gridY,1,1);
    }


    private void buildSaveButton(int gridY) {        
        JButton buttonSave = new JButton("Save");
        buttonSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SaveSettings();
            }
        });
        setLayoutConstraints(buttonSave, 0, gridY,3,1);
    }

    private void SaveSettings() {
        try {
            JamaConfig jamaConfig = new JamaConfig(false);
            String baseurl = textBaseURL_.getText();
            if(baseurl == null || baseurl.length() < 1) {
                JOptionPane.showMessageDialog(this, "Please input Base URL");
                return;
            }

            jamaConfig.setBaseUrl(baseurl);
            jamaConfig.setUsername(textUsername_.getText());
            String password = new String(textPassword_.getPassword());
            jamaConfig.setPassword(password);
            jamaConfig.setApiKey(textApiKey_.getText());

            Integer resourceTimeout = Integer.valueOf(textResourseTimeout_.getText().trim());
            jamaConfig.setResourceTimeOut(resourceTimeout);

            jamaConfig.setClientCertFilePath(textClientCertPath_.getText());
            String clientcertpassword = new String(textClientCertPassword_.getPassword());
            jamaConfig.setClientCertPassword(clientcertpassword);


            jamaConfig.saveProperties(SETTING_FILE_PATH);
            setConfig(jamaConfig);
            JOptionPane.showMessageDialog(this, "Setting Complete");
        } catch (Exception e) {
            logger_.error("can't save setting:", e);
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void setLayoutConstraints(Component comp, int gridx, int gridy, int gridwidth, int gridheight) {
        constraints_.gridx = gridx;
        constraints_.gridy = gridy;
        constraints_.gridwidth = gridwidth;
        constraints_.gridheight = gridheight;
        layout_.setConstraints(comp, constraints_);
        this.add(comp);      
    }


}
