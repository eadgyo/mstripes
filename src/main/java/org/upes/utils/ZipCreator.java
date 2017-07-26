package org.upes.utils;

import java.io.*;
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