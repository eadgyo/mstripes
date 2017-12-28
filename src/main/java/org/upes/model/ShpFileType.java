package org.upes.model;

public class ShpFileType{

    String TypeName;
    ShpFile[] Files;

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public ShpFile[] getFiles() {
        return Files;
    }

    public void setFiles(ShpFile[] files) {
        Files = files;
    }
}
