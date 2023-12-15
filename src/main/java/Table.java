import javax.swing.*;
import javax.swing.table.TableColumnModel;

public class Table
{
    JFrame jFrame;
    JTable jTable;

    Table()
    {
       jFrame=new JFrame();
       jFrame.setTitle("SENTIMENT ANALYSIS");
    }
    public void createTable(String data [][])
    {
       String columnNames[]={"News#","Title","Matches","Polarity"};
       jTable=new JTable(data,columnNames);
       TableColumnModel columnModel=jTable.getColumnModel();
       columnModel.getColumn(0).setPreferredWidth(50);
       columnModel.getColumn(1).setPreferredWidth(300);
       columnModel.getColumn(2).setPreferredWidth(100);
       columnModel.getColumn(3).setPreferredWidth(50);
       JScrollPane jScrollPane=new JScrollPane(jTable);
       jFrame.add(jScrollPane);
       jFrame.setSize(1000,500);
       jFrame.setVisible(true);
    }
}
