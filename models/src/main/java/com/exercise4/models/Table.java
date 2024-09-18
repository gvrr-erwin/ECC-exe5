package com.exercise4.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Table {
    private List<Map<String, String>> rows = new ArrayList<>();

    public List<Map<String, String>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, String>> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map<String, String> row : rows) {
            for (Map.Entry<String, String> entry : row.entrySet()) {
                sb.append(entry.getKey())
                  .append(":")
                  .append(entry.getValue())
                  .append(" ");
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString().trim();
    }
}
