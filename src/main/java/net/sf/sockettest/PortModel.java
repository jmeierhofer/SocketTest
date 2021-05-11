package net.sf.sockettest;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;


/**
 *
 * @author Akshathkumar Shetty
 */
public class PortModel extends AbstractTableModel {
    
    private static final long serialVersionUID = 1L;
    
    private String[][] data;
    private StringTokenizer st1,st2;
    private String port=null;
    private String fileContent;
    private String colName[] = {"Port No", "Use", "Description"};
    
    /** Creates a new instance of PortModel */
    public PortModel(String fileName) {
        try	{
            fileContent = Util.readFile(fileName, this);
        } catch (IOException e) {
            fileContent ="80\tWeb\tNothing";
            System.err.println("Error reading : "+fileName+" : "+e);
        }
        
        st1 = new StringTokenizer(fileContent,"\n");
        int count = st1.countTokens();
        data = new String[count][3];
        int col=0;
        
        for (int row=0;st1.hasMoreTokens();row++) {
            st2 = new StringTokenizer(st1.nextToken(),"\t");
            for(col=0;st2.hasMoreTokens();col++) {
                data[row][col] = st2.nextToken();
            }
            while(col < 2) {
                col++;
                data[row][col] = "";
            }
        }
        addTableModelListener(new TML());
    }
    
    // Prints data when table changes:
    class TML implements TableModelListener {
        @Override
        public void tableChanged(TableModelEvent e) {
            int row = e.getFirstRow();
            System.out.println("Row changed : "+row);
            port=data[row][0];
        }
    }
    
    @Override
    public int getColumnCount() { return data[0].length; }
    @Override
    public int getRowCount() { return data.length; }
    @Override
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
    
    @Override
    public String getColumnName(int column) {
        return colName[column];
    }
    
    public String getPort() {
        return port;
    }
    
}
