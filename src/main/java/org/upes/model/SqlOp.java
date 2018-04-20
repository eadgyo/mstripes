package org.upes.model;

import org.opengis.filter.identity.FeatureId;
import org.upes.Constants;
import org.upes.algo.NodeGrid;

import javax.swing.*;
import java.io.File;
import java.sql.*;
import java.util.*;

public class SqlOp {

    public SqlOp()
    {
        try {
            Class.forName("org.sqlite.JDBC").newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getStackTrace());
        }
    }

    public boolean ifexists()
    {
        File f = new File(Constants.SQLPATH);

        if(f.exists() && !f.isDirectory())
            return true;
        else
            return false;
    }

    public boolean isDbEmpty() {
        String url ="jdbc:sqlite:"+Constants.SQLPATH;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String tableselect = "SELECT count(*) FROM grids ;";
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(tableselect);
                if(result.getInt("count(*)") == 0)
                {
                    return true;
                }
                else
                    return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getStackTrace());
        }
        return true;
    }

    public int ifFeatureCalcRequired(String name, Double score)
    {
        String url ="jdbc:sqlite:"+Constants.SQLPATH;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String tableselect = "SELECT score FROM Features WHERE FeatureID = '"+name+"';";
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(tableselect);
                if(!result.isBeforeFirst())
                {return Constants.NOT_REGISTERED;}
                else if(!(result.getDouble("score") == score))
                {
                    return Constants.SCORE_CHANGED;
                }
                else
                    return Constants.NO_CHANGE;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return Constants.ERROR;
    }

    public Double getNewScore(String name, Double score)
    {
        String url ="jdbc:sqlite:"+Constants.SQLPATH;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String tableselect = "SELECT * FROM Features WHERE FeatureID = '"+name+"';";
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(tableselect);
                Double diff = score - result.getDouble("score");
                return diff;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
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
               String gridTablecreation = "CREATE TABLE IF NOT EXISTS grids (\n"
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
                       + "  score real  DEFAULT 0,"
                       + "  latitude real,"
                       + "  longitude real"
                       + ");";

               Statement stmt = conn.createStatement();
               stmt.execute(gridTablecreation);

               String featuresTablecreation = "CREATE TABLE IF NOT EXISTS Features (\n"
                       + "	FeatureID text PRIMARY KEY,\n"
                       + "  score real  DEFAULT 0,"
                       + "  deleted INTEGER DEFAULT 0,"
                       + "  type VARCHAR"
                       + ");";

               stmt.execute(featuresTablecreation);
           }

       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
   }

    public Double getLatitude(String featureId)
    {
        Double lati = null;
        String url ="jdbc:sqlite:"+Constants.SQLPATH;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String tableselect = "SELECT latitude FROM grids WHERE Grid_ID = '"+featureId+"' ;";
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(tableselect);
                lati = result.getDouble("latitude");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return lati;
    }

    public Double getLongitude(String featureId)
    {
        Double longi = null;
        String url ="jdbc:sqlite:"+Constants.SQLPATH;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String tableselect = "SELECT longitude FROM grids WHERE Grid_ID = '"+featureId+"' ;";
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(tableselect);
                longi = result.getDouble("longitude");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return longi;
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
                           result.getFloat("score")+" "+
                           result.getDouble("latitude")+" "+
                           result.getDouble("longitude")
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
               String tableInsert = "INSERT OR IGNORE INTO grids(Grid_ID,beat_name,N1,N2,N3,N4,N5,N6,N7,N8,latitude,longitude) values(?,?,?,?,?,?,?,?,?,?,?,?);";
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
                   pstmt.setDouble(11,Double.valueOf(gridList.get(9)));
                   pstmt.setDouble(12,Double.valueOf(gridList.get(10)));
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

   public void updateScore(HashMap<FeatureId,Double> set)
   {
       String url ="jdbc:sqlite:"+Constants.SQLPATH;
       try (Connection conn = DriverManager.getConnection(url)) {
           if (conn != null) {
               conn.setAutoCommit(false);
               String scoreUpdate = "UPDATE grids SET score = score + ? WHERE Grid_ID = ? ;";
               PreparedStatement pstmt = conn.prepareStatement(scoreUpdate);

               for(Map.Entry<FeatureId,Double> entry : set.entrySet())
               {
                    pstmt.setDouble(1,entry.getValue());
                    pstmt.setString(2,entry.getKey().getID());
                    pstmt.addBatch();
               }

               pstmt.executeBatch();
               conn.commit();
           }
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
   }

   public Double getScore(String featureId)
   {
       String url ="jdbc:sqlite:"+Constants.SQLPATH;
       try (Connection conn = DriverManager.getConnection(url)) {
           if (conn != null) {
               String select = "SELECT score FROM grids WHERE Grid_ID = '"+featureId +"';";
               Statement stmt = conn.createStatement();
               ResultSet result = stmt.executeQuery(select);
               return result.getDouble("score");
           }
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
       return null;
   }

   public void updateFeatures(HashMap<String,Double> hashMap,String layerType)
   {
       String url ="jdbc:sqlite:"+Constants.SQLPATH;
       try (Connection conn = DriverManager.getConnection(url)) {
           if (conn != null) {
               conn.setAutoCommit(false);
               String featureUpdate = "INSERT OR REPLACE into Features(FeatureID,score,type) VALUES (?,?,?);";
               PreparedStatement pstmt = conn.prepareStatement(featureUpdate);

               for(Map.Entry<String,Double> entry : hashMap.entrySet())
               {
                   pstmt.setString(1,entry.getKey());
                   pstmt.setDouble(2,entry.getValue());
                   pstmt.setString(3,layerType);
                   pstmt.addBatch();
               }

               pstmt.executeBatch();
               conn.commit();
           }
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
   }

    public void TestUpdate()
    {
        String url ="jdbc:sqlite:"+Constants.SQLPATH;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                conn.setAutoCommit(false);
                String scoreUpdate = "UPDATE grids SET score = score + ? WHERE Grid_ID = ?;";
                PreparedStatement pstmt = conn.prepareStatement(scoreUpdate);

                pstmt.setDouble(1,0.0);
                pstmt.setString(2,"kanhaTr1haGrid.120058");
                pstmt.addBatch();

                pstmt.executeBatch();
                conn.commit();
                System.out.println("updated");

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Double> getSortedScores()
    {
        ArrayList<Double> list = new ArrayList<>();
        String url ="jdbc:sqlite:"+Constants.SQLPATH;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String tableselect = "SELECT DISTINCT score FROM grids ORDER BY score DESC;";
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(tableselect);
                while (result.next())
                {
                    list.add(result.getDouble("score"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public ArrayList<String> getGridsFromScore(Double score)
    {
        ArrayList<String> list = new ArrayList<>();
        String url ="jdbc:sqlite:"+Constants.SQLPATH;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String tableselect = "SELECT Grid_ID FROM grids WHERE score = "+score+" ;";
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(tableselect);
                while (result.next())
                {
                    list.add(result.getString("Grid_ID"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public ArrayList<String> getGridsFromScore(Double score,String beatName)
    {
        ArrayList<String> list = new ArrayList<>();
        String url ="jdbc:sqlite:"+Constants.SQLPATH;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String tableselect = "SELECT Grid_ID FROM grids WHERE score = "+score+" AND beat_name = '"+beatName+"' ;";
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(tableselect);
                while (result.next())
                {
                    list.add(result.getString("Grid_ID"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public String getParentBeat(String featureId)
    {
        String url ="jdbc:sqlite:"+Constants.SQLPATH;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String tableselect = "SELECT beat_name FROM grids WHERE Grid_Id = '"+featureId+"' ;";
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(tableselect);
                return result.getString("beat_name");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Set<String> getDeletedFeatures() {

        Set<String> set = new HashSet<>();
        String url ="jdbc:sqlite:"+Constants.SQLPATH;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String tableselect = "SELECT FeatureID FROM Features WHERE deleted = 1 ;";
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(tableselect);
                while (result.next())
                {
                    set.add(result.getString("FeatureID"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return set;
    }

    public void removeDeletedFeatures(Set<String> set)
    {
        if (set.isEmpty())
        {
            return;
        }
        String url ="jdbc:sqlite:"+Constants.SQLPATH;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                conn.setAutoCommit(false);
                String featureDelete = "DELETE FROM Features WHERE FeatureID = ? ;";
                PreparedStatement pstmt = conn.prepareStatement(featureDelete);

                for(String entry : set)
                {
                    pstmt.setString(1,entry);
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                conn.commit();

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<String> getTypes()
    {
        ArrayList<String> list = new ArrayList<>();

        String url ="jdbc:sqlite:"+Constants.SQLPATH;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String tableselect = "SELECT DISTINCT type FROM Features;";
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(tableselect);

                while (result.next())
                {
                    list.add(result.getString("type"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public ArrayList<String> getFeaturesFromType(String type)
    {
        ArrayList<String> list = new ArrayList<>();

        String url ="jdbc:sqlite:"+Constants.SQLPATH;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String tableselect = "SELECT FeatureID FROM Features WHERE type = '"+type+"';";
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(tableselect);

                while (result.next())
                {
                    list.add(result.getString("FeatureID"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public void countQuery()
    {
        String url ="jdbc:sqlite:"+Constants.SQLPATH;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                String tableselect = "SELECT * FROM Features;";
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(tableselect);


//                ResultSetMetaData rm = result.getMetaData();
//                String sArray[] = new String[rm.getColumnCount()];
//                for (int ctr = 1; ctr <= sArray.length; ctr++) {
//                    String s = rm.getColumnName(ctr);
//                    sArray[ctr - 1] = s;
//                }

//               DatabaseMetaData meta = conn.getMetaData();
//               ResultSet result = meta.getSchemas();

                while (result.next())
                {
                    System.out.println(result.getString(1)+"  "+result.getString(2)+"  "+result.getString(3)+"  "+result.getString(4));
//                    System.out.println(result.getInt("count(*)"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void main(String args[])
    {
        SqlOp sqlOp = new SqlOp();
//        sqlOp.countQuery();
//        ArrayList<Double> list= sqlOp.getSortedScores();
//        for (Double score : list)
//        {System.out.println(score);}
        sqlOp.selectAll();
    }

}
