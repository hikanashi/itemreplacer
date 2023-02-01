package com.jamasoftware.services.itemreplacer.Jamamodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.jamasoftware.services.restclient.JamaConfig;
// TODO: for debug
// import com.jamasoftware.services.restclient.httpconnection.TestHttpClient;
import com.jamasoftware.services.restclient.jamadomain.core.JamaInstance;
import com.jamasoftware.services.restclient.jamadomain.lazyresources.JamaItem;

public class JamaItemTableModel extends AbstractTableModel {
  private static final String[] tableTitles_ = { "Target", "ID", "Name", "ItemPath", "LockedBy", "LockedDate", "DiffView" };
  private static final String TARGET_FIELD_NAME = "Name";
  private static final String TARGET_FIELD_DESCRIPTION = "Description";
  public static final int COLUMN_DIFFVIEW = 6;

  private JamaConfig jamaConfig_ = null;
  private JamaInstance jamaInstance_ = null;
  private JamaTableItem rootItem_ = null;
  private List<JamaTableItem> models_ = new ArrayList<JamaTableItem>();

  public JamaItemTableModel() {
  }

  public void setJamaConfig(JamaConfig config) {
    jamaConfig_ = config;
    rootItem_ = null;
    clearTable();

    if (jamaConfig_ != null) {
      // TODO: for debug
      // jamaConfig_.setHttpClient(new TestHttpClient());
      jamaInstance_ = new JamaInstance(jamaConfig_);
    } else {
      jamaInstance_ = null;
    }
  }

  public void clearTable() {
    models_.clear();
    fireTableDataChanged();
  }

  @Override
  public int getColumnCount() {
    return tableTitles_.length;
  }

  @Override
  public String getColumnName(int column) {
    return tableTitles_[column];

  }

  @Override
  public int getRowCount() {
    return models_.size();
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    JamaTableItem item = getValueItem(rowIndex);
    if(item == null) {
      return false;
    }

    if(item.isReplaced()) {
      return false;
    }

    if (columnIndex == 0) {
      return true;
    }

    if (columnIndex == COLUMN_DIFFVIEW) {
      return true;
    }

    return false;
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    if (columnIndex == 0) {
      return Boolean.class;
    }

    if (columnIndex == 1) {
      return Integer.class;
    }

    if (columnIndex == 2) {
      return String.class;
    }

    if (columnIndex == 3) {
      return String.class;
    }

    if (columnIndex == 4) {
      return String.class;
    }

    if (columnIndex == 5) {
      return String.class;
    }

    if (columnIndex == COLUMN_DIFFVIEW) {
      return Boolean.class;
    }

    return null;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    JamaTableItem item = getValueItem(rowIndex);

    if (columnIndex == 0) {
      return item.getTarget();
    }

    if (columnIndex == 1) {
      return item.getID();
    }

    if (columnIndex == 2) {
      return item.getName();
    }

    if (columnIndex == 3) {
      return item.getItemPath();
    }

    if (columnIndex == 4) {
      return item.getlockedby();
    }

    if (columnIndex == 5) {
      return item.lastLockedDate();
    }

    if (columnIndex == COLUMN_DIFFVIEW) {
      return item.getViewDiff();
    }

    return null;
  }

  @Override
  public void setValueAt(Object val, int rowIndex, int columnIndex) {
    JamaTableItem item = getValueItem(rowIndex);

    if (columnIndex == 0) {
      Boolean value = (Boolean) val;
      item.setTarget(value.booleanValue());
      fireTableCellUpdated(rowIndex, columnIndex);
      return;
    }

    if (columnIndex == 6) {
      Boolean value = (Boolean) val;
      setViewDiff(item,value);
      return;
    }

    return;
  }

  public JamaTableItem getValueItem(int rowIndex) {
    if (rowIndex < 0 || models_.size() <= rowIndex) {
      return null;
    }

    JamaTableItem item = models_.get(rowIndex);
    return item;
  }

  public String getRootName() {
    if (rootItem_ == null) {
      return null;
    }

    return rootItem_.getName();
  }

  public boolean setRootItem(JamaItem item) {
    if (item != null) {
      rootItem_ = new JamaTableItem(item);
      return true;
    } else {
      rootItem_ = null;
      return false;
    }
  }

  public boolean searchRootItem(int itemid, IJamaItemEventListener listener) {
    if (jamaInstance_ == null) {
      return false;
    }

    if (rootItem_ != null) {
      if (rootItem_.getID() == itemid) {
        return false;
      }
    }

    clearTable();
    rootItem_ = null;

    GetJamaItemWorker getItemWoker = new GetJamaItemWorker(jamaInstance_, itemid, listener);
    getItemWoker.execute();
    return true;
  }

  public boolean searchItem(String fieldName, String searchKey, IJamaItemEventListener listener) {
    if (jamaInstance_ == null) {
      return false;
    }

    if (rootItem_ == null) {
      return false;
    }

    clearTable();
    SearchJamaItemWorker searchItemWorker = new SearchJamaItemWorker(rootItem_.getItem(), fieldName, searchKey, listener);
    searchItemWorker.execute();
    return true;
  }

  public void searchItemProgress(List<JamaTableItem> results) {
    int beforeRow = models_.size();
    for(JamaTableItem item : results) {
      models_.add(item);
    }

    int afterRow = models_.size();
    fireTableRowsInserted(beforeRow, afterRow);
  }

  public void searchItemFinished() {
    fireTableDataChanged();  
  }

  public String[] getFieldList() {
    String[] fields = { TARGET_FIELD_DESCRIPTION, TARGET_FIELD_NAME };
    return fields;
  }

  public boolean replaceTargetItem(String replaceKey, IJamaItemEventListener listener) {
    ReplaceJamaItemWorker searchItemWorker = new ReplaceJamaItemWorker(models_, replaceKey, listener);
    searchItemWorker.execute();
    return true;
  }

  public void replaceItemProgress(List<JamaTableItem> results) {
    if(results.size() < 1) {
      return;
    }

    int firstRow = models_.indexOf(results.get(0));
    int lastRow = models_.indexOf(results.get(results.size()-1));
    fireTableRowsUpdated(firstRow, lastRow);
  }

  public void replaceItemFinished() {
    Iterator<JamaTableItem> itr = models_.iterator();
    while (itr.hasNext()) {
      JamaTableItem item = itr.next();
      if (item.isReplaced() != true) {
        continue;
      }
      itr.remove();
    }
    fireTableDataChanged();  
  }

  public void setViewDiff(JamaTableItem targetitem, boolean viewdiff) {
    if( targetitem == null ) {
      return;
    }


    if(viewdiff == true) {
      for(JamaTableItem item : models_) {
        if(item.getViewDiff() == true ) {
          fireItemViewDiffUpdated(item, viewdiff);          
        }
      }
    }

    fireItemViewDiffUpdated(targetitem, viewdiff);
  }

  private void fireItemViewDiffUpdated(JamaTableItem targetitem, boolean viewdiff) {
    if( targetitem == null ) {
      return;
    }

    targetitem.setViewDiff(viewdiff);
    int indexRow = models_.indexOf(targetitem);
    fireTableCellUpdated(indexRow, COLUMN_DIFFVIEW);  
  }
}
