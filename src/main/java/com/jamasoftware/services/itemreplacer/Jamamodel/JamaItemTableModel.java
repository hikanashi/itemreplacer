package com.jamasoftware.services.itemreplacer.Jamamodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jamasoftware.services.restclient.JamaConfig;
import com.jamasoftware.services.restclient.exception.RestClientException;
// import com.jamasoftware.services.restclient.httpconnection.TestHttpClient;
import com.jamasoftware.services.restclient.jamadomain.core.JamaInstance;
import com.jamasoftware.services.restclient.jamadomain.lazyresources.JamaItem;

public class JamaItemTableModel extends AbstractTableModel {
  private static final Logger logger_ = LogManager.getLogger(JamaItemTableModel.class);
  private static final String[] tableTitles_ = { "Target", "ID", "Name", "LockedBy", "LockedDate"  };
  private static final String TARGET_FIELD_NAME = "Name";
  private static final String TARGET_FIELD_DESCRIPTION = "Description";


  private JamaConfig jamaConfig_ = null;
  private JamaInstance jamaInstance_ = null;
  private JamaTableItem rootItem_ = null;
  private ArrayList<JamaTableItem> models_ = new ArrayList<JamaTableItem>();

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

  private void clearTable() {
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
    if (columnIndex == 0) {
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
      return item.getlockedby();
    }

    if (columnIndex == 4) {
      return item.lastLockedDate();
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
    }
    return;
  }

  public JamaTableItem getValueItem(int rowIndex) {
    if(rowIndex < 0 || models_.size() <= rowIndex) {
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

  public boolean setRootItem(int itemid) {
    if (jamaInstance_ == null) {
      return false;
    }

    if(rootItem_ != null) {
      if( rootItem_.getID() == itemid) {
        return true;
      }
    }

    clearTable();
    rootItem_ = null;

    try {
      JamaItem root = jamaInstance_.getItem(itemid);
      if(root != null) {
        rootItem_ = new JamaTableItem(root);
        return true;
      } else {
        rootItem_ = null;
        return false;
      }
    } catch (Exception e) {
      logger_.warn("Root JamaItem can't get id:" + itemid, e);
      return false;
    }
  }

  public String[] getFieldList() {
    String[] fields = { TARGET_FIELD_DESCRIPTION, TARGET_FIELD_NAME };
    return fields;
  }

  public boolean searchItem(String fieldName, String searchKey) {
    if (jamaInstance_ == null) {
      return false;
    }

    if (rootItem_ == null) {
      return false;
    }

    try {
      clearTable();
      searchItem (rootItem_.getItem(),fieldName, searchKey);
      fireTableDataChanged();
      return true;
    } catch (Exception e) {
      logger_.warn("search fail. field:" + fieldName + " key:"+ searchKey, e);
      return false;
    }
  }

  private void searchItem(JamaItem jamaParent, String fieldName, String searchKey) throws RestClientException {

    JamaTableItem item = new JamaTableItem(jamaParent);
    boolean matchkey = item.setSearchKey(fieldName, searchKey);
    if(matchkey) {
      models_.add(item);
    }

    try {
      List<JamaItem> childlen = jamaParent.getChildren();
      if(childlen == null) {
        return;
      }

      for (JamaItem child : childlen) {
        searchItem(child, fieldName, searchKey);
      }  
    } catch ( Exception e) {
      logger_.error(e);
    }
  }

  public void replaceSelectedItem(Object value,  String replaceKey) throws RestClientException  {
    JamaTableItem item = (JamaTableItem)value;
    replaceSelectedItemInternal(item, replaceKey);
    models_.remove(item);
  }

  public void replaceAllItem(String replaceKey) throws RestClientException {
    Iterator<JamaTableItem> itr = models_.iterator();
 
    while (itr.hasNext()) {
      JamaTableItem item = itr.next();
        if(item.getTarget() != true) {
          continue;
        }

        replaceSelectedItemInternal(item, replaceKey);
        itr.remove();
    }
  }

  private void replaceSelectedItemInternal(JamaTableItem item,  String replaceKey) throws RestClientException  {
    if (item == null) {
      throw new RestClientException("Target item is not exist");
    }

    item.replaceItem(replaceKey);
  }
}
