package org.upes.utils;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;

public class MapServer
{
    int port;
    HttpServer server = null;

    public MapServer(int port) throws IOException
    {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(null); // creates a default executor
    }


    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public void start()
    {
        server.start();
    }

    public void addContext(String source, HttpHandler handler)
    {
        server.createContext(source, handler);
    }

    public static void sendObject(byte[] read, String filename, HttpExchange t) throws IOException
    {
        Headers h = t.getResponseHeaders();
        h.add("Content-Type", "application/octet-stream");
        h.add("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        t.sendResponseHeaders(200, read.length);
        OutputStream os = t.getResponseBody();
        os.write(read);
        os.close();
    }

    class MyHandlerExample implements HttpHandler
    {
        public void handle(HttpExchange t) throws IOException
        {
            byte [] response = "Welcome Real's HowTo test page".getBytes();
            t.sendResponseHeaders(200, response.length);
            OutputStream os = t.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    class MyHandlerFile implements HttpHandler
    {
        public void handle(HttpExchange t) throws IOException
        {
            String shapeFile = "Basemaps/BEAT.dbf";
            String filename = shapeFile.substring(shapeFile.lastIndexOf("/") + 1);
            byte[] read = readFile(shapeFile);

            System.out.println("Request file " + filename);
            MapServer.sendObject(read, filename, t);
        }
    }

    private byte[] readFile(String aInputFileName)
    {
        File file = new File(aInputFileName);
        byte[] result = new byte[(int)file.length()];
        try
        {
            InputStream input = null;
            try
            {
                int totalBytesRead = 0;
                input = new BufferedInputStream(new FileInputStream(file));
                while(totalBytesRead < result.length)
                {
                    int bytesRemaining = result.length - totalBytesRead;
                    int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
                    if (bytesRead > 0)
                    {
                        totalBytesRead = totalBytesRead + bytesRead;
                    }
                }
            }
            finally {
                input.close();
            }
        }
        catch (IOException ex) {
        }
        return result;
    }


    public static void main(String[] args)
    {
        MapServer mapServer = null;
        try
        {
            mapServer = new MapServer(8080);
            mapServer.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}