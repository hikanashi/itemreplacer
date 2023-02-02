package com.jamasoftware.services.itemreplacer.window;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.Point;

import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jamasoftware.services.itemreplacer.Jamamodel.JamaTableItem;

public class DifferenceDisplay extends JFrame {

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
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        setTitle("Different View");
        setSize(800, 600);

        textMediaType_ = buildChangeButton();
        container.add(textMediaType_, BorderLayout.NORTH);

        srcPanel_ = buildDataView();
        JScrollPane scrollPaneSrc = new JScrollPane();
        scrollPaneSrc.setViewportView(srcPanel_);
        container.add(scrollPaneSrc, BorderLayout.WEST);

        dstPanel_ = buildDataView();
        JScrollPane scrollPaneDst = new JScrollPane();
        scrollPaneDst.setViewportView(dstPanel_);
        container.add(scrollPaneDst, BorderLayout.EAST);

        ChangeListener scrollChangeLitener = new ChangeListener() {
            private boolean adjflg = false;

            @Override public void stateChanged(ChangeEvent e) {
              JViewport src = null;
              JViewport tgt = null;
              if (e.getSource() == scrollPaneSrc.getViewport()) {
                src = scrollPaneSrc.getViewport();
                tgt = scrollPaneDst.getViewport();
              } else if (e.getSource() == scrollPaneDst.getViewport()) {
                src = scrollPaneDst.getViewport();
                tgt = scrollPaneSrc.getViewport();
              }
              if (adjflg || tgt == null || src == null) {
                return;
              }
              adjflg = true;
              Dimension dim1 = src.getViewSize();
              Dimension siz1 = src.getSize();
              Point pnt1 = src.getViewPosition();
              Dimension dim2 = tgt.getViewSize();
              Dimension siz2 = tgt.getSize();
              // Point pnt2 = tgt.getViewPosition();
              double d;
              d = pnt1.getY() / (dim1.getHeight() - siz1.getHeight())
                              * (dim2.getHeight() - siz2.getHeight());
              pnt1.y = (int) d;
              d = pnt1.getX() / (dim1.getWidth() - siz1.getWidth())
                              * (dim2.getWidth() - siz2.getWidth());
              pnt1.x = (int) d;
              tgt.setViewPosition(pnt1);
              adjflg = false;
            }
          };
          scrollPaneSrc.getViewport().addChangeListener(scrollChangeLitener);
          scrollPaneDst.getViewport().addChangeListener(scrollChangeLitener);
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

    private JEditorPane buildDataView() {

        String type = getMediaType();
        JEditorPane panel = new JEditorPane(type, "");
        panel.setEditable(false);
        return panel;
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
