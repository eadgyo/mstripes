package org.upes.utils;

import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class ZipCreator
{
    protected ZipOutputStream  zos;

    public static byte[] serialize(Object obj) throws IOException
    {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream())
        {
            try (ObjectOutputStream o = new ObjectOutputStream(b))
            {
                o.writeObject(obj);
            }
            return b.toByteArray();
        }
    }

    public void addAllFilesSameName(String pathInZip, String fileName)
    {
        // Get the name of the file
        String fileNameWithOutExt = FilenameUtils.getBaseName(fileName);

        // Get parent path
        File file = new File(fileName);
        String parentPath = file.getParent();

        // Retrieve all files with the same name in same folder
        ArrayList<String> listOfFilesSameName = getListOfFilesSameName(parentPath, fileNameWithOutExt);
        for (int i = 0; i < listOfFilesSameName.size(); i++)
        {
            String path = listOfFilesSameName.get(i);
            String baseName = FilenameUtils.getName(path);
            addFile(pathInZip + "/" + baseName, path);
        }
    }

    public ArrayList<String> getListOfFilesSameName(String path, String name)
    {
        File   folder      = new File(path);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> listOfFilesSameName = new ArrayList<>();

        for (int i = 0; i < listOfFiles.length; i++)
        {
            if (listOfFiles[i].isFile())
            {
                String fileNameWithOutExt = FilenameUtils.removeExtension(listOfFiles[i].getName());
                if (fileNameWithOutExt.equals(name))
                    listOfFilesSameName.add(listOfFiles[i].getPath());
            }
        }
        return listOfFilesSameName;
    }


    public void addFile(String pathInZip, String filePath)
    {
        byte[] buffer = new byte[1024];
        ZipEntry ze = new ZipEntry(pathInZip);
        try
        {
            zos.putNextEntry(ze);
            FileInputStream in =
                    new FileInputStream(filePath);

            int len;
            while ((len = in.read(buffer)) > 0)
            {
                zos.write(buffer, 0, len);
            }

            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void addObject(String pathInZip, Object obj)
    {
        ZipEntry ze = new ZipEntry(pathInZip);
        try
        {
            zos.putNextEntry(ze);
            byte[] in = serialize(obj);
            zos.write(in, 0, in.length);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void close()
    {
        try
        {
            zos.closeEntry();
            zos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}