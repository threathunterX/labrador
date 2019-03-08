package com.threathunter.labrador.integration;

import org.apache.poi.ss.usermodel.Row;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanbaowang on 17/9/8.
 */
public class RowMeta {
    private static final String NAME = "name";
    private static final String VARIABLE_NAME = "variable_name";
    private static final String TIMESTAMP = "timestamp";
    private static int NAME_INDEX = -1;
    private static int VARIABLE_NAME_INDEX = -1;
    private static int TIMESTAMP_INDEX = -1;
    private static Map<Integer, String> columnIndexes = new HashMap<>();

    private static int CELL_SIZE = -1;


    public RowMeta(Row row) {
        CELL_SIZE = row.getLastCellNum();
        for (int i = 0; i < CELL_SIZE; i++) {
            String column = row.getCell(i).getStringCellValue().trim();
            switch (column) {
                case NAME:
                    NAME_INDEX = i;
                    break;
                case VARIABLE_NAME:
                    VARIABLE_NAME_INDEX = i;
                    break;
                case TIMESTAMP:
                    TIMESTAMP_INDEX = i;
                    break;
                default:
                    columnIndexes.put(i, column);
            }
        }
    }

    public int getCellSize() {
        return CELL_SIZE;
    }

    public Map<Integer, String> getColumnIndexes() {
        return columnIndexes;
    }

    public int getNameIndex() {
        return NAME_INDEX;
    }

    public int getVariableNameIndex() {
        return VARIABLE_NAME_INDEX;
    }

    public String getIndexColumn(int index) {
        return columnIndexes.get(index);
    }

    public int getTimestampIndex() {
        return TIMESTAMP_INDEX;
    }

}
