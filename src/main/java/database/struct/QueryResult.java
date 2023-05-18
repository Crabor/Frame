package database.struct;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;

public class QueryResult {
    private ArrayList<String> columnNames = null;
    private String[][] data = null;
    private int rowCount = 0;
    private int columnCount = 0;

    public QueryResult(ResultSet rs) {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            columnCount = rsmd.getColumnCount();
            columnNames = new ArrayList<>(columnCount);
            for (int i = 0; i < columnCount; i++) {
                columnNames.add(rsmd.getColumnName(i + 1));
            }
            rs.last();
            rowCount = rs.getRow();
            rs.beforeFirst();
            data = new String[rowCount][columnCount];
            int row = 0;
            while (rs.next()) {
                for (int i = 0; i < columnCount; i++) {
                    data[row][i] = rs.getString(i + 1);
                }
                row++;
            }
        } catch (Exception ignored) {}
    }

    public String[] getColumnNames() {
        return columnNames.toArray(new String[0]);
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public String getOneElement(int rowIndex, int columnIndex) {
        String result = null;
        try {
            result = data[rowIndex][columnIndex];
        } catch (Exception ignored) {}
        return result;
    }

    public String getOneElement(int rowIndex, String columnName) {
        String result = null;
        try {
            int columnIndex = columnNames.indexOf(columnName.toUpperCase());
            if (columnIndex != -1) {
                result = getOneElement(rowIndex, columnIndex);
            }
        } catch (Exception ignored) {}
        return result;
    }

    public String[] getOneRow(int rowIndex) {
        String[] result = null;
        try {
            result = data[rowIndex];
        } catch (Exception ignored) {}
        return result;
    }

    public String[] getOneColumn(int columnIndex) {
        String[] result = null;
        try {
            result = new String[rowCount];
            for (int i = 0; i < rowCount; i++) {
                result[i] = data[i][columnIndex];
            }
        } catch (Exception ignored) {}
        return result;
    }

    public String[] getOneColumn(String columnName) {
        String[] result = null;
        try {
            int columnIndex = columnNames.indexOf(columnName.toUpperCase());
            if (columnIndex != -1) {
                result = getOneColumn(columnIndex);
            }
        } catch (Exception ignored) {}
        return result;
    }

    public String[][] getAll() {
        return data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        //column names
        sb.append(columnNames).append("\n");
        //data
        for (int i = 0; i < rowCount; i++) {
            sb.append(Arrays.toString(data[i])).append("\n");
        }
        //remove last "\n"
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
