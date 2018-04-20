package org.upes.model;

import jdk.nashorn.internal.parser.JSONParser;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.upes.Constants;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonOp {

    static String BEAT_NAME = "";
    static String GRID_NAME="";

    HashMap<String,String> IndiScores = new HashMap<>();

    HashMap<String,String> Scores = new HashMap<>();

    public void setBEAT_NAME(String BEAT_NAME) {
        this.BEAT_NAME = BEAT_NAME;
    }

    public void setGRID_NAME(String GRID_NAME) {
        this.GRID_NAME = GRID_NAME;
    }

    public String getBEAT_NAME() {
        return BEAT_NAME;
    }

    public String getGRID_NAME() {
        return GRID_NAME;
    }

    public HashMap<String, String> getIndiScores() {
        return IndiScores;
    }

    public boolean ifCreated()
    {
        File f = new File(Constants.JSONPATH);

        if(f.exists() && !f.isDirectory())
            return true;
        else
            return false;
    }

    public HashMap<String, String> getScores()
    {
        Scores.clear();
        org.json.simple.parser.JSONParser parser =new org.json.simple.parser.JSONParser();
        try {
            JSONObject jsonObject =(JSONObject) parser.parse(new FileReader(Constants.JSONPATH));
            JSONArray list = (JSONArray) jsonObject.get("List");
            for(Object type : list) {
                JSONObject ftype = (JSONObject) type;
                String fileType = (String) ftype.get("Type");
                String score = (String) ftype.get("Score");
                System.out.println(fileType+"  "+score);
                Scores.put(fileType,score);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return Scores;
    }

    public String extractFileName(String path)
    {
        if ( path == null )
            return null;

        int dotPos = path.lastIndexOf( '.' );
        int slashPos = path.lastIndexOf( '\\' );
        if ( slashPos == -1 )
            slashPos = path.lastIndexOf( '/' );

        if ( dotPos > slashPos )
        {
            return path.substring( slashPos > 0 ? slashPos + 1 : 0,
                    dotPos );
        }

        return path.substring( slashPos > 0 ? slashPos + 1 : 0 );
    }

    public boolean isCalcRequired(org.geotools.map.Layer layer)
    {
        if(IndiScores.get(layer.getTitle()).equals("Nil"))
            return false;
        else
            return true;
    }

    public void setScores(HashMap<String,String> hashMap)
    {
        org.json.simple.parser.JSONParser parser =new org.json.simple.parser.JSONParser();
        try {
            JSONObject jsonObject =(JSONObject) parser.parse(new FileReader(Constants.JSONPATH));
            JSONArray list = (JSONArray) jsonObject.get("List");
            for(Object type : list) {
                JSONObject ftype = (JSONObject) type;
                String fileType = (String) ftype.get("Type");
//                Double score = (Double) ftype.get("Score");
                ftype.replace("Score",hashMap.get(fileType));
            }

            writeJson(jsonObject);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean updateJson(File file,String type) throws IOException {

        ShpFile[] shpFile = new ShpFile[1];
        shpFile[0] = new ShpFile();
        shpFile[0].setFilepath(file.toString());
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        shpFile[0].setFiletype(store.getFeatureSource().getSchema().getGeometryDescriptor().getType().getName().toString());

        ShpFileType shpFileType = new ShpFileType();
        shpFileType.setTypeName(type);
        shpFileType.setFiles(shpFile);

        if(type.equals("Beats"))
        {
            String fileName = extractFileName(file.getName());
            setBEAT_NAME(fileName);
        }
        else if(type.equals("Grid"))
        {
            String fileName = extractFileName(file.getName());
            setGRID_NAME(fileName);
        }
        IndiScores.put(extractFileName(file.getName()),"Nil");


        JSONObject jshpFile = new JSONObject();
        jshpFile.put("FileType",shpFile[0].getFiletype());
        jshpFile.put("FilePath",shpFile[0].getFilepath());

        JSONObject jshpFileType = new JSONObject();
        jshpFileType.put("Type",shpFileType.getTypeName());
        jshpFileType.put("Score","Nil");
        JSONArray files = new JSONArray();

        files.add(jshpFile);
        jshpFileType.put("Files",files);

        if(ifCreated()==false)
        {
            File f = new File(Constants.JSONPATH);
            boolean a = f.getParentFile().mkdirs();
            JSONObject jsonObject = new JSONObject();

            JSONArray jsonArray = new JSONArray();
            jsonArray.add(jshpFileType);
            jsonObject.put("List",jsonArray);

            writeJson(jsonObject);
        }
        else
        {
            org.json.simple.parser.JSONParser parser =new org.json.simple.parser.JSONParser();
            try {
            JSONObject jsonObject =(JSONObject) parser.parse(new FileReader(Constants.JSONPATH));
            JSONArray list = (JSONArray) jsonObject.get("List");
            JSONObject ftype = null;

            for(Object o : list)
            {
                JSONObject filetype = (JSONObject) o;
                 if(filetype.get("Type").equals(type))
                 {
                     ftype = filetype;
                     break;
                 }
            }

            if(ftype == null)
            {
                list.add(jshpFileType);
                writeJson(jsonObject);
                System.out.println("Type not Registered");
                return true;
            }

            JSONArray ffiles = (JSONArray) ftype.get("Files");
            JSONObject shpfile = null;

            for (Object f : ffiles)
            {
                JSONObject tempfile=(JSONObject) f;
                if(tempfile.get("FilePath").equals(file.toString()))
                {
                    JOptionPane.showMessageDialog(null,"File already registered!","Error",JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }
            }

            if(shpfile == null)
            {
              ffiles.add(jshpFile);
            }

            writeJson(jsonObject);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        }
        return  true;
    }

    public ArrayList<String> getFiles()
    {
        ArrayList<String> fileList = new ArrayList<>();
        org.json.simple.parser.JSONParser parser =new org.json.simple.parser.JSONParser();
        try {
            JSONObject jsonObject =(JSONObject) parser.parse(new FileReader(Constants.JSONPATH));
            JSONArray list = (JSONArray) jsonObject.get("List");
            IndiScores.clear();
            for(Object type : list)
            {
                JSONObject ftype = (JSONObject) type;
                String fileType = (String) ftype.get("Type");
                JSONArray files = (JSONArray) ftype.get("Files");
                String score = (String) ftype.get("Score");
                for(Object file : files)
                {
                    JSONObject shpfile = (JSONObject) file;
                    String path = (String) shpfile.get("FilePath");
                    fileList.add(path) ;

                    //adding individual file scores to hashmap.
                    IndiScores.put(extractFileName(path),score);

                    if(fileType.equals("Beats"))
                    {
                     String fileName = extractFileName((String) shpfile.get("FilePath"));
                     setBEAT_NAME(fileName);
                    }
                    else if(fileType.equals("Grid"))
                    {
                        String fileName = extractFileName((String) shpfile.get("FilePath"));
                        setGRID_NAME(fileName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return fileList;
    }

    public void updateIndiScore()
    {
        IndiScores.size();
        org.json.simple.parser.JSONParser parser =new org.json.simple.parser.JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) parser.parse(new FileReader(Constants.JSONPATH));

            JSONArray list = (JSONArray) jsonObject.get("List");
            for(Object type : list) {
                JSONObject ftype = (JSONObject) type;
                JSONArray files = (JSONArray) ftype.get("Files");
                String score = (String) ftype.get("Score");
                for (Object file : files) {
                    JSONObject shpfile = (JSONObject) file;
                    String path = (String) shpfile.get("FilePath");
                    //updating individual file scores.
                    IndiScores.put(extractFileName(path), score);
                    System.out.println("This: "+extractFileName(path)+"   "+IndiScores.get(extractFileName(path))+"  "+score);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void writeJson(JSONObject ob)
    {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(Constants.JSONPATH);
            fileWriter.write(ob.toJSONString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getType(String filepath)
    {
        String type=null;
        org.json.simple.parser.JSONParser parser =new org.json.simple.parser.JSONParser();
        try {
            JSONObject jsonObject =(JSONObject) parser.parse(new FileReader(Constants.JSONPATH));
            JSONArray list = (JSONArray) jsonObject.get("List");
            for(Object item : list)
            {
                JSONObject ftype = (JSONObject) item;
                JSONArray files = (JSONArray) ftype.get("Files");
                for(Object file : files)
                {
                    JSONObject shpfile = (JSONObject) file;
                    if(shpfile.get("FilePath").equals(filepath))
                        return (String) ftype.get("Type");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return type;
    }

    public ArrayList<String> getFilesFromType(String type)
    {
        ArrayList<String> output = new ArrayList<>();

        org.json.simple.parser.JSONParser parser =new org.json.simple.parser.JSONParser();
        try {
            JSONObject jsonObject =(JSONObject) parser.parse(new FileReader(Constants.JSONPATH));
            JSONArray list = (JSONArray) jsonObject.get("List");
            for(Object item : list)
            {
                JSONObject ftype = (JSONObject) item;
                if(ftype.get("Type").equals(type))
                {
                    JSONArray files = (JSONArray) ftype.get("Files");

                    for(Object file : files)
                    {
                        JSONObject shpfile = (JSONObject) file;
                        output.add((String) shpfile.get("FilePath"));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output;
    }

}
