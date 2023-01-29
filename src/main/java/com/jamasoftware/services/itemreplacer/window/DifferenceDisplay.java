package com.jamasoftware.services.itemreplacer.window;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import com.jamasoftware.services.itemreplacer.Jamamodel.JamaTableItem;

public class DifferenceDisplay extends JFrame {

    private GridBagLayout layout_;
    private JEditorPane srcPanel_;
    private JEditorPane dstPanel_;
    private String replaceKey_ = null;
    private JamaTableItem item_ = null;

    public DifferenceDisplay() {

        buildGUI();
    }

    private void buildGUI() {
        layout_ = new GridBagLayout();
        this.setLayout(layout_);

        setTitle("Different View");
        setBounds(0, 0, 810, 810);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        int gridX = 0;
        srcPanel_ = buildDataView();
        setLayoutConstraints(srcPanel_, gridX++, 0, 1, 5);
        JSeparator separator = buildSeparator();
        setLayoutConstraints(separator, gridX++, 0, 1, 1);
        dstPanel_ = buildDataView();
        setLayoutConstraints(dstPanel_, gridX++, 0, 1, 5);
    }

    public void setVisibleTop(boolean visible) {
        if (visible) {
            setAlwaysOnTop(true);
        }

        setEnabled(visible);
        setVisible(visible);
        setAlwaysOnTop(false);

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

        JEditorPane panel = new JEditorPane("text/html", "");
        panel.setEditable(false);
        panel.setPreferredSize(new Dimension(400, 800));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setViewportView(panel);
        return panel;
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

    public void setItem(Object item, String key) {
        String type = "text/html";

        item_ = (JamaTableItem) item;
        if (item_ == null) {
            updateContent(srcPanel_, type, "");
            updateContent(dstPanel_, type, "");
            return;
        }

        setReplaceKey(key);
    }

    private void updateContent(JEditorPane panel, String type, String value) {
        panel.setEditable(true);
        panel.setText("");
        panel.setContentType(type);
        panel.setText(value);
        panel.setEditable(false);
    }

    public void setReplaceKey(String key) {
        replaceKey_ = key;

        if (item_ == null) {
            return;
        }

        String destValue = item_.getReplaceData(replaceKey_);
        srcPanel_.setText(item_.getSource());
        dstPanel_.setText(destValue);
    }
}
