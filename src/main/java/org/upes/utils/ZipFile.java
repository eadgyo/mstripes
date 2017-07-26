package org.upes.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.zip.ZipOutputStream;

public class ZipFile extends ZipCreator
{
    private FileOutputStream fos = null;

    public ZipFile(String zipPath) throws FileNotFoundException
    {
        fos = new FileOutputStream(zipPath);
        zos = new ZipOutputStream(fos);
    }

}
