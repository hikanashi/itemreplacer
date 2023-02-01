package com.jamasoftware.services.itemreplacer.window;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.jamasoftware.services.itemreplacer.Jamamodel.JamaItemTableModel;
public class JamaResultTable extends JTable {

  private DifferenceDisplay diffdisplay_;
  private String replaceKey_ = null;

  public JamaResultTable(AbstractTableModel model) {
    setModel(model);
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    diffdisplay_ = new DifferenceDisplay();
    diffdisplay_.setVisibleTop(false);

    getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        Object item = getSelectedItem();
        if(item == null) {
          return;
        }
        diffdisplay_.setItem(item, replaceKey_);
        diffdisplay_.setVisibleTop(true);
        clearSelection();
      }
    });
  }

  private Object getSelectedItem() {
    JamaItemTableModel model = (JamaItemTableModel) getModel();
    if (model == null) {
      return null;
    }

    return model.getValueItem(getSelectedRow());
  }

  public void setReplaceKey(String value) {
    replaceKey_ = value;
  }

  public void replaceFinished() {
    JamaItemTableModel model = (JamaItemTableModel) getModel();
    if (model == null) {
      return;
    }

    diffdisplay_.setVisibleTop(false);
    clearSelection();
    model.replaceItemFinished();
  }
}
