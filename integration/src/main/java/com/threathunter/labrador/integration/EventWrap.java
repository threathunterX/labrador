package com.threathunter.labrador.integration;

import com.threathunter.labrador.core.env.Env;
import com.threathunter.model.Event;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class EventWrap {
    private String variable;
    private Event event;

    public EventWrap(String variable, Event event) {
        this.variable = variable;
        this.event = event;
    }

    public EventWrap() {
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public static EventWrap valueOf(Row row, RowMeta rowMeta) {
        int currentCellSize = row.getLastCellNum();
        Event event = new Event();
        EventWrap wrap = new EventWrap();
        String source = row.getCell(rowMeta.getNameIndex()).getStringCellValue();
        if (!Env.getEventFieldContainer().containsEventSource(source)) {
            throw new IllegalStateException("event source " + source + " is not valid");
        }
        event.setName(source);
        if (currentCellSize <= rowMeta.getVariableNameIndex()) {
            wrap.setVariable("");
        } else {
            String variable = row.getCell(rowMeta.getVariableNameIndex()).getStringCellValue();
            if (!Env.getVariables().contains(variable)) {
                throw new IllegalStateException("unknow variable name " + variable);
            }
            wrap.setVariable(variable);
        }

        double doubleTs = row.getCell(rowMeta.getTimestampIndex()).getNumericCellValue();
        DecimalFormat df = new DecimalFormat("0");
        event.setTimestamp(Long.valueOf(df.format(doubleTs)));

        Map<String, Object> props = new HashMap<>();

        Map<Integer, String> columnIndexes = rowMeta.getColumnIndexes();

        for (Map.Entry<Integer, String> entry : columnIndexes.entrySet()) {
            String column = entry.getValue();
            String type = Env.getEventFieldContainer().getEventSourceFieldType(source, column);
            int index = entry.getKey();
            if (index >= currentCellSize) {
                continue;
            }

            Object obj = row.getCell(index);

            if (null == obj) {
                continue;
            }

            if (row.getCell(index).getCellTypeEnum() == CellType.NUMERIC) {
                if (type.equals("string")) {
                    obj = new Double(obj.toString()).intValue();
                }
            }


            if (!Env.getEventFieldContainer().containsEventSourceField(source, column)) {
                throw new IllegalStateException("source " + source + " column " + column + " is invalid");
            }

            switch (type) {
                case "string":
                    props.put(column, String.valueOf(obj));
                    break;
                case "long":
                    try {
                        Long longValue = Long.valueOf(String.valueOf(obj));
                        props.put(column, longValue);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new IllegalStateException("data type error, column " + column + " need long, but value is " + obj);
                    }
                case "double":
                    try {
                        double doubleValue = Double.valueOf(String.valueOf(obj));
                        props.put(column, doubleValue);
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new IllegalStateException("data type error, column " + column + " need double, but value is " + obj);
                    }
            }
        }
        event.setPropertyValues(props);
        wrap.setEvent(event);
        return wrap;
    }
}
