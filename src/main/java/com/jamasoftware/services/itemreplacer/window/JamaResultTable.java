package com.jamasoftware.services.itemreplacer.window;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import com.jamasoftware.services.itemreplacer.Jamamodel.JamaItemTableModel;
import com.jamasoftware.services.itemreplacer.Jamamodel.JamaTableItem;
public class JamaResultTable extends JTable {

  private DifferenceDisplay diffdisplay_;
  private String replaceKey_ = null;

  public JamaResultTable(AbstractTableModel model) {
    setModel(model);
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    model.addTableModelListener(new DiffViewHandler());

    diffdisplay_ = new DifferenceDisplay();
    diffdisplay_.setVisibleTop(false);

    diffdisplay_.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        JamaTableItem item = diffdisplay_.getItem();
        if(item == null) {
          return;
        }

        JamaItemTableModel model = (JamaItemTableModel) getModel();
        if (model == null) {
          return;
        }

        model.setViewDiff(item, false);
      }
    });
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

  class DiffViewHandler implements TableModelListener {
    
    @Override
    public void tableChanged(TableModelEvent e) {
      if(e.getType() != TableModelEvent.UPDATE) {
        return;
      }

      if(e.getColumn() != JamaItemTableModel.COLUMN_DIFFVIEW) {
        return;
      }

      JamaItemTableModel model = (JamaItemTableModel) getModel();
      if (model == null) {
        return;
      }
      
      JamaTableItem targetItem = model.getValueItem(e.getFirstRow());
      if( targetItem.getViewDiff() ) {
        diffdisplay_.setItem(targetItem, replaceKey_);
        diffdisplay_.setVisibleTop(true);
      } else {
        if(targetItem == diffdisplay_.getItem() ) {
          diffdisplay_.setVisibleTop(false);
        }
      }
    }
  }
}
