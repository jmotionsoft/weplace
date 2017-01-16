package towntalk.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import towntalk.controller.ContentsController;
import towntalk.mapper.CommonFilesDao;
import towntalk.model.CommonFile;
import towntalk.module.aws.AwsConnector;
import towntalk.util.AppConfig;

import java.io.File;
import java.util.*;

/**
 * Created by sin31 on 2016-10-04.
 */

@Service
public class S3FileService {
    static final Logger logger = LoggerFactory.getLogger(S3FileService.class);

    private final String COMMON_FILE_PATH_USER = "upload/user";

    @Autowired
    SqlSession sqlSession;
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private AwsConnector awsConnector;

    public S3Object getPublicFileFromS3(int file_no){
        CommonFilesDao commonFilesDao = sqlSession.getMapper(CommonFilesDao.class);
        AmazonS3Client amazonS3Client = awsConnector.getAmazonS3Client();

        CommonFile commonFile = new CommonFile();
        commonFile.setFile_no(file_no);

        commonFile = commonFilesDao.getFile(commonFile);
        if(commonFile == null || commonFile.getSave_path() == null){
            return null;
        }

        try{
            S3Object s3Object = amazonS3Client.getObject(appConfig.getAwsS3bucketWeplace(), commonFile.getSave_path());
            return s3Object;
        }catch (Exception e){
            return null;
        }
    }

    public String saveFiles(MultipartFile[] files){
        if(files == null || files.length == 0){
            return null;
        }

        CommonFilesDao commonFilesDao = sqlSession.getMapper(CommonFilesDao.class);
        AmazonS3Client amazonS3Client = awsConnector.getAmazonS3Client();
        int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String saveFilesNo = "";

        int file_size = files.length;
        for(int i = 0; file_size > i; i++){
            MultipartFile aFile = files[i];

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(aFile.getSize());
            objectMetadata.setContentType(aFile.getContentType());

            Map<String, String> userData = new HashMap<>();
            userData.put("file_name", aFile.getOriginalFilename());
            objectMetadata.setUserMetadata(userData);

            String path = COMMON_FILE_PATH_USER + "/" + user_no + "/" + createDailyPath();
            String saveFileName = String.valueOf(System.currentTimeMillis()) + "_" + String.valueOf(i);
            String savePath = path + saveFileName;

            try {
                PutObjectResult result = amazonS3Client.putObject(appConfig.getAwsS3bucketWeplace(), savePath,
                        aFile.getInputStream(), objectMetadata);
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
                amazonS3Client.deleteObject(appConfig.getAwsS3bucketWeplace(), savePath);
            }
        }

        logger.info("saved file no: "+saveFilesNo);
        return saveFilesNo;
    }

    @Transactional
    public void deleteFiles(String filesNo){
        logger.info("delete file no: "+filesNo);

        if(filesNo == null || filesNo.trim().equals("")){
            return;
        }

        CommonFilesDao commonFilesDao = sqlSession.getMapper(CommonFilesDao.class);
        AmazonS3Client amazonS3Client = awsConnector.getAmazonS3Client();
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
                amazonS3Client.deleteObject(appConfig.getAwsS3bucketWeplace(), commonFile.getSave_path());
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
