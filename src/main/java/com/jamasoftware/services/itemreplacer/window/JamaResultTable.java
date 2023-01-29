package com.jamasoftware.services.itemreplacer.window;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.jamasoftware.services.itemreplacer.Jamamodel.JamaItemTableModel;
import com.jamasoftware.services.restclient.exception.RestClientException;

public class JamaResultTable extends JTable {

  private DifferenceDisplay diffdisplay_;
  private String replaceKey_ = null;

  public JamaResultTable(AbstractTableModel model) {
    setModel(model);
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(this);
    scrollPane.setViewportView(this);

    diffdisplay_ = new DifferenceDisplay();
    diffdisplay_.setVisibleTop(false);

    getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        diffdisplay_.setItem(getSelectedItem(), replaceKey_);
        diffdisplay_.setVisibleTop(true);
      }
    });
  }

  public void setReplaceKey(String value) {
    replaceKey_ = value;
  }

  public void replaceSelectedItem(String replaceKey) throws RestClientException {
    JamaItemTableModel model = (JamaItemTableModel) getModel();
    if (model == null) {
      return;
    }

    int currentRow = getSelectedRow();
    int nextRow = currentRow + 1;
    model.replaceSelectedItem(getSelectedItem(), replaceKey);
    if (nextRow < model.getRowCount()) {
      changeSelection(nextRow, 1, false, false);
    } else {
      clearSelection();
    }

    model.fireTableRowsDeleted(currentRow, currentRow);
    model.fireTableRowsUpdated(nextRow, nextRow);
  }

  public void replaceAllItem(String replaceKey) throws RestClientException {
    JamaItemTableModel model = (JamaItemTableModel) getModel();
    if (model == null) {
      return;
    }

    model.replaceAllItem(replaceKey);
    clearSelection();
    model.fireTableDataChanged();
  }

  private Object getSelectedItem() {
    JamaItemTableModel model = (JamaItemTableModel) getModel();
    if (model == null) {
      return null;
    }

    return model.getValueItem(getSelectedRow());
  }

}
