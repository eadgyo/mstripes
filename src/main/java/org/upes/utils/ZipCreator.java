package org.upes.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipCreator
{
    List<String> fileList;

    private FileOutputStream fos;
    private ZipOutputStream  zos;

    public ZipCreator(String zipFile) throws FileNotFoundException
    {
        fileList = new ArrayList<String>();

        fos = new FileOutputStream(zipFile);
        zos = new ZipOutputStream(fos);
    }

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

    public void addFile(String fileName, String filePath)
    {
        byte[] buffer = new byte[1024];
        ZipEntry ze = new ZipEntry(fileName);
        try
        {
            zos.putNextEntry(ze);
            FileInputStream in =
                    new FileInputStream( filePath);

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

    public void addObject(String fileName, Object obj)
    {
        ZipEntry ze = new ZipEntry(fileName);
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