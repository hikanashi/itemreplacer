package com.jamasoftware.services.itemreplacer.window;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.JTableHeader;
import javax.swing.text.MaskFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jamasoftware.services.itemreplacer.Jamamodel.JamaItemTableModel;
import com.jamasoftware.services.restclient.JamaConfig;

public class ReplaceTab extends JPanel{
    private static final Logger logger_ = LogManager.getLogger(ReplaceTab.class);
    private static final String LABEL_TEXT_NO_BASE_ITEM = "---- Set Base Item ID ----";
    private static final int MAX_COMBO_ELEMENT = 3;

    private GridBagLayout layout_;
    private JFormattedTextField textBaseItem_;
    private JLabel labelBaseItem_;
    private JComboBox<String> textSearchKey_;
    private DefaultComboBoxModel<String> searchKeyModel_;
    private JComboBox<String> textReplaceKey_;
    private DefaultComboBoxModel<String> replaceKeyModel_;
    private JComboBox<String> textTargetField_;
    private JamaResultTable jamaitemTable_;

    private JamaItemTableModel serachResult_ = new JamaItemTableModel();

    public ReplaceTab() { 
        buildGUI();
    }


    public void setConfig(JamaConfig config) {
        serachResult_.setJamaConfig(config);
    }

    private void buildGUI() {
		layout_ = new GridBagLayout();
		this.setLayout(layout_);

        int gridY = 0;
        gridY = buildBaseItem(gridY);
        gridY = buildTargetField(gridY);
        gridY = buildSerchWord(gridY);
        gridY = buildReplaceWord(gridY);
        gridY = buildSearchButton(gridY); 
        gridY = buildSearchResultTable(gridY);
        gridY = buildReplaceButton(gridY);
    }

    
    private int buildBaseItem(int gridY) {
        int gridheight = 1;

        JButton buttonBaseItem = new JButton("Set BaseItem");
        buttonBaseItem.setHorizontalAlignment(JLabel.TRAILING);
        buttonBaseItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setBaseJamaItem();
            }
        });
        setLayoutConstraints(buttonBaseItem, 0,gridY,1, gridheight);

        try {
            MaskFormatter jamaitemFormat = new MaskFormatter();
            jamaitemFormat.setValidCharacters("0123456789");
            jamaitemFormat.setPlaceholderCharacter(' ');
            textBaseItem_ = new JFormattedTextField();


            //TODO: for debug 
            // textBaseItem_.setText("2210690");

            textBaseItem_.setHorizontalAlignment(JLabel.LEFT);
            textBaseItem_.setPreferredSize(new Dimension(150, 30));
            setLayoutConstraints(textBaseItem_, 1,gridY,1, gridheight);
        } catch (Exception e) {
            logger_.error("MaskFormatter error:", e);
        }

        labelBaseItem_ = new JLabel(LABEL_TEXT_NO_BASE_ITEM);
        setLayoutConstraints(labelBaseItem_, 2,gridY,1, gridheight);

        return gridY + gridheight;
    }

    private int buildTargetField(int gridY) {
        int gridheight = 1;

        JLabel label = new JLabel("Target Field:");
        label.setHorizontalAlignment(JLabel.TRAILING);
        setLayoutConstraints(label, 0,gridY,1, gridheight);
        
        textTargetField_ = new JComboBox<String>(serachResult_.getFieldList());
        textTargetField_.setEditable(false);
        textTargetField_.setPreferredSize(new Dimension(400, 30));
        textTargetField_.setEnabled(false);
        setLayoutConstraints(textTargetField_, 1, gridY,2, gridheight);

        return gridY + gridheight;
    }
    

    private int buildSerchWord(int gridY) {
        int gridheight = 1;

        JLabel label = new JLabel("Before replacement:");
        label.setHorizontalAlignment(JLabel.TRAILING);
        setLayoutConstraints(label, 0,gridY,1, gridheight);
        
        String[] combodata = {""};
        searchKeyModel_ = new DefaultComboBoxModel<String>(combodata);
        textSearchKey_ = new JComboBox<String>(searchKeyModel_);
        textSearchKey_.setEditable(true);
        textSearchKey_.setPreferredSize(new Dimension(400, 30));
        textSearchKey_.setEnabled(false);
        setLayoutConstraints(textSearchKey_, 1, gridY,2, gridheight);

        return gridY + gridheight;
    }

    private int buildReplaceWord(int gridY) {
        int gridheight = 1;

        JLabel label = new JLabel("After replacement:");
        label.setHorizontalAlignment(JLabel.TRAILING);
        setLayoutConstraints(label, 0,gridY,1, gridheight);
        
        String[] combodata = {""};
        replaceKeyModel_ = new DefaultComboBoxModel<String>(combodata);
        textReplaceKey_ = new JComboBox<String>(replaceKeyModel_);
        textReplaceKey_.setEditable(true);
        textReplaceKey_.setPreferredSize(new Dimension(400, 30));
        textReplaceKey_.setEnabled(false);
        textReplaceKey_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeReplaceKey();
            }
        });
        setLayoutConstraints(textReplaceKey_, 1, gridY,2, gridheight);

        return gridY + gridheight;
    }

    private int buildSearchButton(int gridY) {        
        int gridheight = 1;

        JButton buttonSearch = new JButton("Search");
        buttonSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchJamaItem();
            }
        });
        setLayoutConstraints(buttonSearch, 0, gridY,3, gridheight);

        return gridY + gridheight;
    }

    private int buildSearchResultTable(int gridY) {
        int gridheight = 2;
        jamaitemTable_ = new JamaResultTable(serachResult_);
        JTableHeader header = jamaitemTable_.getTableHeader();
        setLayoutConstraints(header, 0, gridY,3, 1);
        setLayoutConstraints(jamaitemTable_, 0, gridY+1,3, 1);

        return gridY + gridheight;
    }

    private int buildReplaceButton(int gridY) {        
        int gridheight = 1;
        JButton buttonReplace = new JButton("Replace");
        buttonReplace.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                replaceJamaItem(true);
            }
        });
        setLayoutConstraints(buttonReplace, 1, gridY,1, gridheight);
        
        JButton buttonReplaceAll = new JButton("Replace All");
        buttonReplaceAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                replaceJamaItem(false);
            }
        });

        setLayoutConstraints(buttonReplaceAll, 2, gridY,1, gridheight);

        return gridY + gridheight;
    }
    
    private void setLayoutConstraints(Component comp, int gridx, int gridy, int gridwidth, int gridheight) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = gridwidth;
        constraints.gridheight = gridheight;
        layout_.setConstraints(comp, constraints);
        this.add(comp);      
    }


    private void changeReplaceKey() {
        String replaceKey = (String)textReplaceKey_.getSelectedItem();
        jamaitemTable_.setReplaceKey(replaceKey);
        jamaitemTable_.clearSelection();
    }


    private void setBaseJamaItem() {
        try {
            String baseitem = textBaseItem_.getText().trim();
            if(baseitem.length() < 1) {
                JOptionPane.showMessageDialog(this, "Please input Base item ID");
                return;
            }

            int baseitem_id = Integer.valueOf(baseitem);
            serachResult_.setRootItem(baseitem_id);
            String rootName = serachResult_.getRootName();

            if(rootName != null) {
                labelBaseItem_.setText(rootName);    
                textSearchKey_.setEnabled(true);
                textReplaceKey_.setEnabled(true);
                textTargetField_.setEnabled(true);
            } else {
                labelBaseItem_.setText(LABEL_TEXT_NO_BASE_ITEM);    
                textSearchKey_.setEnabled(false);
                textReplaceKey_.setEnabled(false);
                textTargetField_.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Not Found Base Item");
            }

        } catch (Exception e) {
            logger_.error("search Base Item faild:", e);
            labelBaseItem_.setText(LABEL_TEXT_NO_BASE_ITEM);    
            JOptionPane.showMessageDialog(this, "Root Item get faild");
        }
    }

    private void searchJamaItem() {
        try {
            String rootName = serachResult_.getRootName();
            if(rootName == null) {
                JOptionPane.showMessageDialog(this, "Please select Base Item");   
                return;
            }

            String fieldName = (String)textTargetField_.getSelectedItem();
            String searchKey = (String)textSearchKey_.getSelectedItem();
            if(fieldName.length() < 1) {
                JOptionPane.showMessageDialog(this, "Please input Fieldname");
                return;
            }

            if(searchKey.length() < 1) {
                JOptionPane.showMessageDialog(this, "Please input search key");
                return;
            }

            addComboElement(searchKeyModel_,searchKey);
            
            serachResult_.searchItem(fieldName, searchKey);
            JOptionPane.showMessageDialog(this, "Search Finish. Result:" + serachResult_.getRowCount());

        } catch (Exception e) {
            logger_.error("can't save setting:", e);
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void replaceJamaItem(boolean selectedonly) {
        try {
            String rootName = serachResult_.getRootName();
            if(rootName == null) {
                JOptionPane.showMessageDialog(this, "Please select Base Item");   
                return;
            }

            String replaceKey = (String)textReplaceKey_.getSelectedItem();
            if(replaceKey.length() < 1) {
                JOptionPane.showMessageDialog(this, "Please input replace key");
                return;
            }

            addComboElement(replaceKeyModel_,replaceKey);

            if(selectedonly) {
                jamaitemTable_.replaceSelectedItem(replaceKey);                
            } else {
                jamaitemTable_.replaceAllItem(replaceKey);
            }

            JOptionPane.showMessageDialog(this, "Replace Finish");
        } catch (Exception e) {
            logger_.error("Replace Faild:", e);
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void addComboElement(DefaultComboBoxModel<String> model, String value) {
        model.removeElement(value);

        if(model.getSize() >= MAX_COMBO_ELEMENT) {
            model.removeElementAt(0);
        }

        model.addElement(value);
        model.setSelectedItem(value);
    }


}
