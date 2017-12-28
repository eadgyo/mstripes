package org.upes.model;

import org.upes.Constants;

import javax.swing.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SqlOp {

    public boolean ifexists()
    {
        File f = new File(Constants.SQLPATH);

        if(f.exists() && !f.isDirectory())
            return true;
        else
            return false;
    }

   public void createDB()
   {
       if(ifexists()== true)
       {
           JOptionPane.showMessageDialog(null,"Database already created","Error",JOptionPane.ERROR_MESSAGE);
           return;
       }

       String url ="jdbc:sqlite:"+Constants.SQLPATH;
       try (Connection conn = DriverManager.getConnection(url)) {
           if (conn != null) {
               String tablecreation = "CREATE TABLE IF NOT EXISTS grids (\n"
                       + "	Grid_ID text PRIMARY KEY,\n"
                       + "	beat_name text NOT NULL,\n"
                       + "	N1 text,\n"
                       + "  N2 text,\n"
                       + "  N3 text,\n"
                       + "  N4 text,\n"
                       + "  N5 text,\n"
                       + "  N6 text,\n"
                       + "  N7 text,\n"
                       + "  N8 text,\n"
                       + "  score real  DEFAULT NULL"
                       + ");";

               Statement stmt = conn.createStatement();
               stmt.execute(tablecreation);
           }

       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
   }

   public void selectAll()
   {
       String url ="jdbc:sqlite:"+Constants.SQLPATH;
       try (Connection conn = DriverManager.getConnection(url)) {
           if (conn != null) {
               String tableselect = "SELECT * FROM grids;";
               Statement stmt = conn.createStatement();
               ResultSet result = stmt.executeQuery(tableselect);
               while (result.next())
               {
                   System.out.println(result.getString("Grid_ID")+"  "+result.getString("beat_name")+" "+
                           result.getString("N1")+" "+
                           result.getString("N2")+" "+
                           result.getString("N3")+" "+
                           result.getString("N4")+" "+
                           result.getString("N5")+" "+
                           result.getString("N6")+" "+
                           result.getString("N7")+" "+
                           result.getString("N8")+" "+
                           result.getFloat("score")
                   );
               }

           }

       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
   }

   public HashMap<String,GridFeature> findFeatures(ArrayList<String> Ids)
   {
       String url ="jdbc:sqlite:"+Constants.SQLPATH;
       try (Connection conn = DriverManager.getConnection(url)) {
           if (conn != null && !Ids.isEmpty()) {
               String query = "SELECT * FROM grids where Grid_ID in ( '"+Ids.get(0);

               for(int i=1;i<Ids.size();i++)
               {
                   query+="' , '"+Ids.get(i);
               }
               query += "' );";

               Statement statement = conn.createStatement();
               ResultSet rs = statement.executeQuery(query);


               HashMap<String,GridFeature> gridFeatures = new HashMap<>();
               while(rs.next())
               {
                   GridFeature temp = new GridFeature();
                   String currGrid = rs.getString("Grid_ID");
                   temp.setCurrFeatureId(currGrid);
                   temp.setParentBeat(rs.getString("beat_name"));
                   ArrayList<String> tempList = new ArrayList<>();
                   for(int i=1;i<=8;i++)
                   {
                       if(!(rs.getString("N"+i) == null))
                            tempList.add(rs.getString("N"+i));
                   }
                   temp.setNeighbourList(tempList);
                   gridFeatures.put(currGrid,temp);
               }

               return gridFeatures;
           }

       } catch (SQLException e) {
           e.printStackTrace();
       }
       return null;
   }

   public void insertNeighbours(String beatName, ArrayList<ArrayList<String>> list)
   {
       String url ="jdbc:sqlite:"+Constants.SQLPATH;
       try (Connection conn = DriverManager.getConnection(url)) {
           if (conn != null) {
               conn.setAutoCommit(false);
               String tableInsert = "INSERT OR IGNORE INTO grids(Grid_ID,beat_name,N1,N2,N3,N4,N5,N6,N7,N8) values(?,?,?,?,?,?,?,?,?,?);";
               PreparedStatement pstmt = conn.prepareStatement(tableInsert);

               for (ArrayList<String> gridList :list) {

                   pstmt.setString(1,gridList.get(0));
                   pstmt.setString(2,beatName);
                   pstmt.setString(3,gridList.get(1));
                   pstmt.setString(4,gridList.get(2));
                   pstmt.setString(5,gridList.get(3));
                   pstmt.setString(6,gridList.get(4));
                   pstmt.setString(7,gridList.get(5));
                   pstmt.setString(8,gridList.get(6));
                   pstmt.setString(9,gridList.get(7));
                   pstmt.setString(10,gridList.get(8));
                   pstmt.addBatch();
//                   pstmt.clearParameters();
               }

               pstmt.executeBatch();
               conn.commit();

//               String tableinsert = "INSERT INTO grids values('"+gridID+"','"+beatName+"','"+N1+"','"+N2+"','"+N3+"','"+N4+"','"+N5+"','"+N6+"','"+N7+"','"+N8+"');";
//               Statement stmt = conn.createStatement();
//               stmt.executeUpdate(tableinsert);

           }
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
   }

   public void countQuery()
   {
       String url ="jdbc:sqlite:"+Constants.SQLPATH;
       try (Connection conn = DriverManager.getConnection(url)) {
           if (conn != null) {
               String tableselect = "SELECT count(Grid_ID) FROM grids;";
               Statement stmt = conn.createStatement();
               ResultSet result = stmt.executeQuery(tableselect);
               while (result.next())
               {
                   System.out.println(result.getInt("count(Grid_ID)"));
               }
           }
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
   }

    public static void main(String args[])
    {
        SqlOp sqlOp = new SqlOp();
        sqlOp.selectAll();
    }
}
