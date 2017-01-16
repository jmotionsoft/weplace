package towntalk.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import towntalk.mapper.CommonFilesDao;
import towntalk.model.CommonFile;
import towntalk.util.AppConfig;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/**
 * Created by sin31 on 2017-01-10.
 */

@Service
public class LocalFileService {
    static final Logger logger = LoggerFactory.getLogger(LocalFileService.class);

    private final String COMMON_FILE_PATH_USER = "upload/user";

    @Autowired
    SqlSession sqlSession;
    @Autowired
    private AppConfig appConfig;

    public CommonFile getCommonFile(int file_no){
        CommonFilesDao commonFilesDao = sqlSession.getMapper(CommonFilesDao.class);

        CommonFile commonFile = new CommonFile();
        commonFile.setFile_no(file_no);

        return commonFilesDao.getFile(commonFile);
    }

    public String saveFiles(MultipartFile[] files){
        if(files == null || files.length == 0){
            return null;
        }

        CommonFilesDao commonFilesDao = sqlSession.getMapper(CommonFilesDao.class);
        int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String saveFilesNo = "";

        int file_size = files.length;
        for(int i = 0; file_size > i; i++) {
            MultipartFile aFile = files[i];

            String path = COMMON_FILE_PATH_USER + "/" + user_no + "/" + createDailyPath();
            String saveFileName = String.valueOf(System.currentTimeMillis()) + "_" + String.valueOf(i);
            String savePath = path + saveFileName;

            File file = null;
            try{
                file = new File(savePath);

                if(!file.isDirectory())
                    file.mkdirs();

                byte[] bytes = aFile.getBytes();
                BufferedOutputStream buffStream =
                        new BufferedOutputStream(new FileOutputStream(file));
                buffStream.write(bytes);
                buffStream.close();
            }catch (Exception e){
                e.printStackTrace();
                continue;
            }

            CommonFile commonFile = new CommonFile();
            commonFile.setFile_name(aFile.getOriginalFilename());
            commonFile.setFile_type(aFile.getContentType());
            commonFile.setFile_length(aFile.getSize());
            commonFile.setSave_type(CommonFile.SAVE_TYPE_S3);
            commonFile.setSave_path(savePath);
            commonFile.setUser_no(user_no);

            int add_count = commonFilesDao.addFile(commonFile);
            if(add_count == 1){
                saveFilesNo += String.valueOf(commonFile.getFile_no());
                if(file_size > i + 1){
                    saveFilesNo += ",";
                }
            }else{
                if(file.exists()){
                    file.delete();
                }
            }
        }

        logger.info("saved file no: "+saveFilesNo);
        return saveFilesNo;
    }

    @Transactional
    public void deleteFiles(String filesNo){
        if(filesNo == null || filesNo.trim().equals("")){
            return;
        }

        CommonFilesDao commonFilesDao = sqlSession.getMapper(CommonFilesDao.class);
        int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String[] files = filesNo.split(",");

        for(int i = 0; files.length > i; i++){
            CommonFile commonFile = new CommonFile();
            commonFile.setFile_no(Integer.valueOf(files[i]));

            commonFile = commonFilesDao.getFile(commonFile);
            if(commonFile == null){
                continue;
            }

            commonFile.setUser_no(user_no);

            int delete_count = commonFilesDao.deleteFile(commonFile);
            if(delete_count == 1){
                File file = new File(commonFile.getSave_path());
                if(file.exists())
                    file.delete();
            }
        }
    }

    @Transactional
    public String updateFiles(String oldImagesNo, String newImagesNo, MultipartFile[] addFiles){
        logger.info("updateFiles() => old file no: {}, new file no: {}", oldImagesNo, newImagesNo);

        String addFilesNo = "";
        if(addFiles != null && addFiles.length > 0){
            addFilesNo = saveFiles(addFiles);
        }

        if(oldImagesNo == null || oldImagesNo.trim().equals("")){
            return addFilesNo;
        }

        if(newImagesNo == null || newImagesNo.trim().equals("")){
            deleteFiles(oldImagesNo);
            return addFilesNo;
        }

        if(oldImagesNo.equals(newImagesNo)){
            logger.info("return file no: "+oldImagesNo + (addFilesNo.equals("") ? "" : ","+addFilesNo));
            return oldImagesNo + (addFilesNo.equals("") ? "" : ","+addFilesNo);
        }

        List<String> oleImageList = Arrays.asList(oldImagesNo.split(","));
        List<String> newImageList = Arrays.asList(newImagesNo.split(","));

        Collection<String> removeImageList = CollectionUtils.subtract(oleImageList, newImageList);
        for(String file_no : removeImageList){
            deleteFiles(file_no);
        }

        logger.info("return file no: "+newImagesNo + (addFilesNo.equals("") ? "" : ","+addFilesNo));
        return newImagesNo + (addFilesNo.equals("") ? "" : ","+addFilesNo);
    }

    private String createDailyPath(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return year+"/"+(month + 1)+"/"+day+"/";
    }
}
