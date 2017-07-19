package org.upes.controller;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;

/**
 * Created by This Pc on 19-07-2017.
 */

public class FileSender {

    public void createFTPServer()
    {
        FtpServerFactory serverFactory=new FtpServerFactory();
        FtpServer server=serverFactory.createServer();
        try {
            server.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }
}
