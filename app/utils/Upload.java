package utils;

import com.typesafe.config.ConfigException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Upload {
    public static final List<String> IMG_EXT = Arrays.asList(
            ".jpg",
            ".jpeg",
            ".png",
            ".gif"
    );

    private String kind;
    private String id;
    private FileObject uploadPath;
    private String webPath;
    protected FileSystemManager manager;

    public Upload(String kind, String id) {
        this.kind = kind;
        this.id = id;

        try {
            manager = VFS.getManager();
        } catch (FileSystemException e) {
            throw new RuntimeException("VFS Initialization Error");
        }

        try {
            uploadPath = manager.resolveFile(new File("."), getConfig("upload.path"));
        } catch (FileSystemException e) {
            throw new ConfigException.BadValue("upload.path", "upload path VFS parse error ", e);
        }
        webPath = getConfig("upload.url");
    }

    public void removeExisting(){
        try {
            for(FileObject file : uploadPath.getChildren()){
                if(file.getName().getBaseName().startsWith(kind + "_" + id + ".")){
                    file.delete();
                }
            }
        } catch (FileSystemException e) {
            return;
        }
    }

    public String moveUpload(String fileName, File file){
        file = preprocess(file);
        FileObject dest = moveFile(fileName, file);
        return getPublicUrl(dest);
    }

    protected File preprocess(File file){
        return file;
    }

    protected FileObject moveFile(String fileName, File file){
        FileObject dest = getUploadDest(fileName);
        FileObject src;
        try {
            src = manager.toFileObject(file);
        } catch (FileSystemException e) {
            throw new RuntimeException("Input file VFS conversion error");
        }

        try {
            src.moveTo(dest);
        } catch (FileSystemException e) {
            throw new RuntimeException("File move error", e);
        }
        return dest;
    }

    protected FileObject getUploadDest(String fileName){
        String ext;
        if(!fileName.contains(".")){
            ext = ".bin";
        }else {
            ext = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        }
        try {
            return manager.resolveFile(uploadPath, kind + "_" + id + ext);
        } catch (FileSystemException e) {
            throw new RuntimeException("VFS file upload destination resolving error");
        }
    }

    protected String getConfig(String key){
//        String out = play.Play.application().configuration().getString(key + "." + this.kind);
//
//        if(out != null) {
//            return out;
//        }
        String out = play.Play.application().configuration().getString(key);
        if(out == null){
            throw new ConfigException.Missing(key);
        }

        return out;
    }

    public String getPublicUrl(FileObject file){
        return webPath + file.getName().getBaseName();
    }

    public void setManager(FileSystemManager manager) {
        this.manager = manager;
    }
}
