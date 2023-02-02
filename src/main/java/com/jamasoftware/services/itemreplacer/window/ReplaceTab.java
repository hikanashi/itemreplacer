package com.jamasoftware.services.itemreplacer.window;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.Dimension;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.JTableHeader;
import javax.swing.text.MaskFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jamasoftware.services.itemreplacer.Jamamodel.IJamaItemEventListener;
import com.jamasoftware.services.itemreplacer.Jamamodel.JamaItemTableModel;
import com.jamasoftware.services.itemreplacer.Jamamodel.JamaTableItem;
import com.jamasoftware.services.restclient.JamaConfig;
import com.jamasoftware.services.restclient.jamadomain.lazyresources.JamaItem;

public class ReplaceTab extends JPanel implements IJamaItemEventListener {
    private static final Logger logger_ = LogManager.getLogger(ReplaceTab.class);
    private static final String LABEL_TEXT_NO_BASE_ITEM = "---- Set Base Item ID ----";
    private static final int MAX_COMBO_ELEMENT = 3;

    private JPanel subPanel_;
    private GridBagLayout sublayout_;

    private JFormattedTextField textBaseItem_;
    private JLabel labelBaseItem_;
    private JComboBox<String> textSearchKey_;
    private DefaultComboBoxModel<String> searchKeyModel_;
    private JComboBox<String> textReplaceKey_;
    private DefaultComboBoxModel<String> replaceKeyModel_;
    private JComboBox<String> textTargetField_;

    private JButton buttonBaseItem_;
    private JButton buttonReplaceChecked_;
    private JButton buttonSearch_;

    private JamaResultTable jamaitemTable_;
    private JamaItemTableModel serachResult_ = new JamaItemTableModel();

    public ReplaceTab() {
        buildGUI();
    }

    public void setConfig(JamaConfig config) {
        serachResult_.setJamaConfig(config);
    }

    private void buildGUI() {
        setLayout(new BorderLayout());

        buildItemPanel();  
        JPanel tablePanel = buildSearchResultTable();
        add(tablePanel, BorderLayout.CENTER);

        setBaseInputEnable(true);
        setInputEnable(false);
    }


    private void buildItemPanel() {
        subPanel_ = new JPanel();
        sublayout_ = new GridBagLayout();
        subPanel_.setLayout(sublayout_);

        int gridY = 0;
        gridY = buildBaseItem(gridY);
        gridY = buildTargetField(gridY);
        gridY = buildSerchWord(gridY);
        gridY = buildReplaceWord(gridY);
        gridY = buildSearchButton(gridY);
        add(subPanel_, BorderLayout.NORTH);
        return;
    }

    private int buildBaseItem(int gridY) {
        int gridheight = 1;

        buttonBaseItem_ = new JButton("Set BaseItem");
        buttonBaseItem_.setHorizontalAlignment(JLabel.TRAILING);
        buttonBaseItem_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setBaseJamaItem();
            }
        });
        setLayoutConstraints(buttonBaseItem_, 0, gridY, 1, gridheight, GridBagConstraints.NONE);

        try {
            MaskFormatter jamaitemFormat = new MaskFormatter();
            jamaitemFormat.setValidCharacters("0123456789");
            jamaitemFormat.setPlaceholderCharacter(' ');
            textBaseItem_ = new JFormattedTextField();

            // TODO: for debug
            // textBaseItem_.setText("2210690");

            textBaseItem_.setHorizontalAlignment(JLabel.LEFT);
            textBaseItem_.setPreferredSize(new Dimension(150, 30));
            setLayoutConstraints(textBaseItem_, 1, gridY, 1, gridheight, GridBagConstraints.HORIZONTAL);
        } catch (Exception e) {
            logger_.error("MaskFormatter error:", e);
        }

        labelBaseItem_ = new JLabel(LABEL_TEXT_NO_BASE_ITEM);
        setLayoutConstraints(labelBaseItem_, 2, gridY, 1, gridheight, GridBagConstraints.HORIZONTAL);

        return gridY + gridheight;
    }

    private int buildTargetField(int gridY) {
        int gridheight = 1;

        JLabel label = new JLabel("Target Field:");
        label.setHorizontalAlignment(JLabel.TRAILING);
        setLayoutConstraints(label, 0, gridY, 1, gridheight, GridBagConstraints.NONE);

        textTargetField_ = new JComboBox<String>(serachResult_.getFieldList());
        textTargetField_.setEditable(false);
        textTargetField_.setPreferredSize(new Dimension(400, 30));
        textTargetField_.setEnabled(false);
        textTargetField_.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                serachResult_.clearTable();
            }
        });
        setLayoutConstraints(textTargetField_, 1, gridY, 2, gridheight, GridBagConstraints.HORIZONTAL);

        return gridY + gridheight;
    }

    private int buildSerchWord(int gridY) {
        int gridheight = 1;

        JLabel label = new JLabel("Before replacement:");
        label.setHorizontalAlignment(JLabel.TRAILING);
        setLayoutConstraints(label, 0, gridY, 1, gridheight, GridBagConstraints.NONE);

        String[] combodata = { "" };
        searchKeyModel_ = new DefaultComboBoxModel<String>(combodata);
        textSearchKey_ = new JComboBox<String>(searchKeyModel_);
        textSearchKey_.setEditable(true);
        textSearchKey_.setPreferredSize(new Dimension(400, 30));
        textSearchKey_.setEnabled(false);
        setLayoutConstraints(textSearchKey_, 1, gridY, 2, gridheight, GridBagConstraints.HORIZONTAL);

        return gridY + gridheight;
    }

    private int buildReplaceWord(int gridY) {
        int gridheight = 1;

        JLabel label = new JLabel("After replacement:");
        label.setHorizontalAlignment(JLabel.TRAILING);
        setLayoutConstraints(label, 0, gridY, 1, gridheight, GridBagConstraints.NONE);

        String[] combodata = { "" };
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
        setLayoutConstraints(textReplaceKey_, 1, gridY, 2, gridheight, GridBagConstraints.HORIZONTAL);

        return gridY + gridheight;
    }

    private int buildSearchButton(int gridY) {
        int gridheight = 1;

        JLabel label = new JLabel("Search Result");
        label.setHorizontalAlignment(JLabel.TRAILING);
        setLayoutConstraints(label, 0, gridY, 1, gridheight, GridBagConstraints.NONE);

        buttonSearch_ = new JButton("Search");
        buttonSearch_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchJamaItem();
            }
        });
        setLayoutConstraints(buttonSearch_, 1, gridY, 1, gridheight, GridBagConstraints.HORIZONTAL);

        buttonReplaceChecked_ = new JButton("Replace checked items");
        buttonReplaceChecked_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                replaceJamaItem();
            }
        });
        setLayoutConstraints(buttonReplaceChecked_, 2, gridY, 1, gridheight, GridBagConstraints.HORIZONTAL);

        return gridY + gridheight;
    }

    private void setLayoutConstraints(Component comp, int gridx, int gridy, int gridwidth, int gridheight,int fill) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = gridwidth;
        constraints.gridheight = gridheight;
        constraints.fill = fill;
        sublayout_.setConstraints(comp, constraints);
        subPanel_.add(comp);
    }

    private JPanel buildSearchResultTable() {
        jamaitemTable_ = new JamaResultTable(serachResult_);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(jamaitemTable_);
        JTableHeader header = jamaitemTable_.getTableHeader();

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.add(header, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.SOUTH);

        return tablePanel;
    }

    private void setBaseInputEnable(boolean enable) {
        textBaseItem_.setEnabled(enable);
        buttonBaseItem_.setEnabled(enable);
    }

    private void setInputEnable(boolean enable) {
        buttonSearch_.setEnabled(enable);
        buttonReplaceChecked_.setEnabled(enable);
        textSearchKey_.setEnabled(enable);
        textReplaceKey_.setEnabled(enable);
        textTargetField_.setEnabled(enable);
    }

    private void changeReplaceKey() {
        String replaceKey = (String) textReplaceKey_.getSelectedItem();
        jamaitemTable_.setReplaceKey(replaceKey);
        jamaitemTable_.clearSelection();
    }

    private void setBaseJamaItem() {
        try {
            String baseitem = textBaseItem_.getText().trim();
            if (baseitem.length() < 1) {
                JOptionPane.showMessageDialog(this, "Please input Base item ID");
                return;
            }

            int baseitem_id = Integer.valueOf(baseitem);

            boolean result = serachResult_.searchRootItem(baseitem_id, this);
            if( result ) {
                setBaseInputEnable(false);
                setInputEnable(false);
            }

        } catch (Exception e) {
            logger_.error("search Base Item faild:", e);
            labelBaseItem_.setText(LABEL_TEXT_NO_BASE_ITEM);
            JOptionPane.showMessageDialog(this, "Root Item get faild");
        }
    }

    @Override
    public void GetFinished(JamaItem item) {
        setBaseInputEnable(true);
        try {
            serachResult_.setRootItem(item);
            String rootName = serachResult_.getRootName();
            if (rootName != null) {
                labelBaseItem_.setText(rootName);
                textSearchKey_.setEnabled(true);
                textReplaceKey_.setEnabled(true);
                textTargetField_.setEnabled(true);
                setInputEnable(true);
            } else {
                labelBaseItem_.setText(LABEL_TEXT_NO_BASE_ITEM);
                textSearchKey_.setEnabled(false);
                textReplaceKey_.setEnabled(false);
                textTargetField_.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Not Found Base Item");
                setInputEnable(false);
            }
        } catch (Exception e) {
            logger_.error("Get Base Item faild:" + e.getMessage(), e);
            labelBaseItem_.setText(LABEL_TEXT_NO_BASE_ITEM);
            setInputEnable(false);
            JOptionPane.showMessageDialog(this, "Root Item get faild");            
        }
    }

    private void searchJamaItem() {
        try {
            String rootName = serachResult_.getRootName();
            if (rootName == null) {
                JOptionPane.showMessageDialog(this, "Please select Base Item");
                return;
            }

            String fieldName = (String) textTargetField_.getSelectedItem();
            if (fieldName.length() < 1) {
                JOptionPane.showMessageDialog(this, "Please input Fieldname");
                return;
            }

            String searchKey = (String) textSearchKey_.getSelectedItem();
            if (searchKey.length() < 1) {
                JOptionPane.showMessageDialog(this, "Please input search key");
                return;
            }

            addComboElement(searchKeyModel_, searchKey);
            boolean result = serachResult_.searchItem(fieldName, searchKey, this);
            if( result ) {
                setBaseInputEnable(false);
                setInputEnable(false);
            }

        } catch (Exception e) {
            logger_.error("Search Exception:"+ e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Search Exception:"+ e.getMessage()); 
        }
    }

    @Override
    public void SearchProgress(List<JamaTableItem> results) {
        serachResult_.searchItemProgress(results);
    }

    @Override
    public void SearchFinished(boolean result) {
        serachResult_.searchItemFinished();
        setInputEnable(true);
        setBaseInputEnable(true);
        if( result == true) {
            JOptionPane.showMessageDialog(this, "Search Finish. Result:" + serachResult_.getRowCount());
        } else {
            JOptionPane.showMessageDialog(this, "Search Faild"); 
        }
    }

    
    private void replaceJamaItem() {
        try {
            String rootName = serachResult_.getRootName();
            if (rootName == null) {
                JOptionPane.showMessageDialog(this, "Please select Base Item");
                return;
            }

            String replaceKey = (String) textReplaceKey_.getSelectedItem();
            if (replaceKey.length() < 1) {
                JOptionPane.showMessageDialog(this, "Please input replace key");
                return;
            }

            addComboElement(replaceKeyModel_, replaceKey);
            boolean result = serachResult_.replaceTargetItem(replaceKey, this);
            if( result ) {
                setBaseInputEnable(false);
                setInputEnable(false);
            }

        } catch (Exception e) {
            logger_.error("Replace Exception:" + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Replace Exception:"+ e.getMessage()); 
        }
    }

    @Override
    public void ReplaceProgress(List<JamaTableItem> results) {
        serachResult_.searchItemProgress(results);
    }

    @Override
    public void ReplaceFinished(boolean result) {
        jamaitemTable_.replaceFinished();
        setInputEnable(true);
        setBaseInputEnable(true);
        if( result == true) {
            JOptionPane.showMessageDialog(this, "Replace Finish");
        } else {
            JOptionPane.showMessageDialog(this, "Replace Faild"); 
        }
    }

    private void addComboElement(DefaultComboBoxModel<String> model, String value) {
        model.removeElement(value);

        if (model.getSize() >= MAX_COMBO_ELEMENT) {
            model.removeElementAt(0);
        }

        model.addElement(value);
        model.setSelectedItem(value);
    }

}
