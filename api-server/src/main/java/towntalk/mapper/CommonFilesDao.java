package towntalk.mapper;

import towntalk.model.CommonFile;

/**
 * Created by sin31 on 2016-10-04.
 */
public interface CommonFilesDao {
    CommonFile getFile(CommonFile commonFile);

    int addFile(CommonFile commonFile);

    int deleteFile(CommonFile commonFile);
}
