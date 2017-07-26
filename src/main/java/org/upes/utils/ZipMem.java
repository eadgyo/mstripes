package org.upes.utils;

import java.io.ByteArrayOutputStream;
import java.util.zip.ZipOutputStream;

public class ZipMem extends ZipCreator
{
    private ByteArrayOutputStream baos = null;

    public ZipMem()
    {
        baos = new ByteArrayOutputStream();
        zos = new ZipOutputStream(baos);
    }

    public byte[] toByteArray()
    {
        return baos.toByteArray();
    }
}
