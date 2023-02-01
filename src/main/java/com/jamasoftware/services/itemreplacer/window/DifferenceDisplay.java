package com.jamasoftware.services.itemreplacer.window;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.GridBagConstraints;
// import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import com.jamasoftware.services.itemreplacer.Jamamodel.JamaTableItem;

public class DifferenceDisplay extends JFrame {

    private GridBagLayout layout_;
    private JComboBox<String> textMediaType_;
    private JEditorPane srcPanel_;
    private JEditorPane dstPanel_;
    private String replaceKey_ = null;
    private JamaTableItem item_ = null;

    public DifferenceDisplay() {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        buildGUI();
    }

    public JamaTableItem getItem() {
        return item_;
    }
    
    private void buildGUI() {
        layout_ = new GridBagLayout();
        this.setLayout(layout_);

        setTitle("Different View");
        setBounds(0, 0, 400, 600);

        textMediaType_ = buildChangeButton();
        setLayoutConstraints(textMediaType_, 0, 0, 3, 1, GridBagConstraints.NONE);

        int gridX = 0;
        srcPanel_ = buildDataView();
        JScrollPane scrollPaneSrc = new JScrollPane();
        scrollPaneSrc.setViewportView(srcPanel_);
        setLayoutConstraints(scrollPaneSrc, gridX++, 1, 1, 1, GridBagConstraints.HORIZONTAL);

        JSeparator separator = buildSeparator();
        setLayoutConstraints(separator, gridX++, 1, 1, 1, GridBagConstraints.VERTICAL);
        dstPanel_ = buildDataView();
        JScrollPane scrollPaneDst = new JScrollPane();
        scrollPaneDst.setViewportView(srcPanel_);
        setLayoutConstraints(scrollPaneDst, gridX++, 1, 1, 1, GridBagConstraints.HORIZONTAL);
    }



    private JComboBox<String> buildChangeButton() {
        String[] mediatypes = { "text/html", "text/text" };
        JComboBox<String> mediaTypeBox = new JComboBox<String>(mediatypes);
        mediaTypeBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                changeMediaType();
            }
        });
        return mediaTypeBox;
    }

    private void changeMediaType() {
        if(item_ == null ) {
            return;
        }
        
        String destValue = item_.getReplaceData(replaceKey_);
        updateContent(srcPanel_, item_.getSource());
        updateContent(dstPanel_, destValue);
    }

    private String getMediaType() {
        return (String) textMediaType_.getSelectedItem();
    }


    private JSeparator buildSeparator() {
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        // Dimension size = getSize();
        // separator.setPreferredSize(new Dimension(10, size.height));
        Border border = new BevelBorder(BevelBorder.RAISED);
        separator.setBorder(border);

        return separator;
    }

    private JEditorPane buildDataView() {

        String type = getMediaType();
        JEditorPane panel = new JEditorPane(type, "");
        panel.setEditable(false);
        return panel;
    }

    private void setLayoutConstraints(Component comp, int gridx, int gridy, int gridwidth, int gridheight, int fill) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = gridwidth;
        constraints.gridheight = gridheight;
        constraints.fill = fill;
        layout_.setConstraints(comp, constraints);
        this.add(comp);
    }

    public void setItem(Object item, String key) {
        item_ = (JamaTableItem) item;
        if (item_ == null) {
            updateContent(srcPanel_, "");
            updateContent(dstPanel_, "");
            return;
        }

        setReplaceKey(key);
    }

    public void setReplaceKey(String key) {
        replaceKey_ = key;

        if (item_ == null) {
            return;
        }

        String destValue = item_.getReplaceData(replaceKey_);
        updateContent(srcPanel_, item_.getSource());
        updateContent(dstPanel_, destValue);
    }


    private void updateContent(JEditorPane panel, String value) {
        String type = getMediaType();
        panel.setEditable(true);
        panel.setText("");
        panel.setContentType(type);
        panel.setText(value);
        panel.setEditable(false);
    }


    public void setVisibleTop(boolean visible) {
        if (visible) {
            setAlwaysOnTop(true);
        }

        setEnabled(visible);
        setVisible(visible);
        setAlwaysOnTop(false);
    }
}
