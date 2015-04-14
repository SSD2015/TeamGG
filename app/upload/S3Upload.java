package upload;

import com.intridea.io.vfs.operations.Acl;
import com.intridea.io.vfs.operations.IAclGetter;
import com.intridea.io.vfs.operations.IAclSetter;
import com.intridea.io.vfs.operations.IPublicUrlsGetter;
import com.intridea.io.vfs.provider.s3.S3FileProvider;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;

import java.io.File;

public class S3Upload extends Upload {
    public S3Upload(String kind, String id) {
        super(kind, id);
    }

    @Override
    protected FileSystemManager createVFSManager(){
        String id = play.Play.application().configuration().getString("upload.aws.id");
        String key = play.Play.application().configuration().getString("upload.aws.key");
        StaticUserAuthenticator auth = new StaticUserAuthenticator(null, id, key);
        FileSystemOptions opts = S3FileProvider.getDefaultFileSystemOptions();
        try {
            DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, auth);
        } catch (FileSystemException e) {
            throw new RuntimeException("VFS Cannot set auth", e);
        }

        return super.createVFSManager();
    }

    @Override
    protected FileObject moveFile(String fileName, File file) {
        FileObject out = super.moveFile(fileName, file);

        try {
            IAclGetter aclGetter = (IAclGetter) out.getFileOperations().getOperation(IAclGetter.class);
            aclGetter.process();
            Acl fileAcl = aclGetter.getAcl();

            fileAcl.allow(Acl.Group.EVERYONE, Acl.Permission.READ);

            IAclSetter aclSetter = (IAclSetter) out.getFileOperations().getOperation(IAclSetter.class);
            aclSetter.setAcl(fileAcl);
            aclSetter.process();
        } catch (FileSystemException e) {
            throw new RuntimeException("Cannot set file ACL", e);
        }

        return out;
    }

    // XXX: This will return non-https URL
//    @Override
//    public String getPublicUrl(FileObject file){
//        try {
//            IPublicUrlsGetter getter = (IPublicUrlsGetter) file.getFileOperations().getOperation(IPublicUrlsGetter.class);
//            return getter.getHttpUrl();
//        } catch (FileSystemException e) {
//            throw new RuntimeException("Cannot get file public URL", e);
//        }
//    }
}
