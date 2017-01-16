package towntalk.controller;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import towntalk.model.CommonFile;
import towntalk.model.Contents;
import towntalk.model.Member;
import towntalk.service.ContentsService;
import towntalk.service.LocalFileService;
import towntalk.service.MemberService;
import towntalk.util.HttpReturn;

import java.io.File;
import java.io.IOException;

/**
 * Created by sin31 on 2016-06-09.
 */
@RestController
@RequestMapping(value = "/board")
public class ContentsController {
    static final Logger logger = LoggerFactory.getLogger(ContentsController.class);

    @Autowired
    private ContentsService contentsService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private LocalFileService localFileService;

    @RequestMapping(value = "/{board_no}/contents", method = RequestMethod.GET)
    public ResponseEntity<?> getContentsList(
            @PathVariable int board_no,
            @RequestParam(required = false) Integer last_contents_no){

        Contents contents = new Contents();
        contents.setBoard_no(board_no);
        contents.setLast_index_no(last_contents_no);

        return HttpReturn.OK(contentsService.getContentsList(contents));
    }

    @RequestMapping(value = "/{board_no}/contents/{contents_no}", method = RequestMethod.GET)
    public ResponseEntity<?> getContents(@PathVariable int board_no, @PathVariable int contents_no){
        Contents contents = contentsService.getContents(board_no, contents_no);
        if(contents == null){
            return HttpReturn.NOT_FOUND();
        }else{
            return HttpReturn.OK(contents);
        }
    }

    @RequestMapping(value = "/{board_no}/contents", method = RequestMethod.POST)
    public ResponseEntity<?> insertContents(@PathVariable int board_no,
                                            @RequestParam(value = "title") String title,
                                            @RequestParam(value = "body") String body,
                                            @RequestParam(value = "images") MultipartFile[] images){

        int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Member member = memberService.getMember(user_no);
        if(member == null){
            return HttpReturn.FORBIDDEN();
        }

        Contents contents = new Contents();
        contents.setTitle(title);
        contents.setBody(body);
        contents.setBoard_no(board_no);
        contents.setUser_no(user_no);
        contents.setLatitude(member.getLatitude());
        contents.setLongitude(member.getLongitude());
        contents.setNotice_yn("N");
        contents.setImages_no(localFileService.saveFiles(images));

        int insert_count = contentsService.insertContents(contents);
        if(insert_count == 1){
            return HttpReturn.NO_CONTENT();
        }else{
            return HttpReturn.INTERNAL_SERVER_ERROR();
        }
    }

    @RequestMapping(value = "/{board_no}/contents/{contents_no}", method = RequestMethod.POST)
    public ResponseEntity<?> updateContents(
            @PathVariable int board_no,
            @PathVariable int contents_no,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "body") String body,
            @RequestParam(value = "editImages") String editImages,
            @RequestParam(value = "images") MultipartFile[] images){

        int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String images_no = contentsService.getContents(board_no, contents_no).getImages_no();

        Contents contents = new Contents();
        contents.setBoard_no(board_no);
        contents.setContents_no(contents_no);
        contents.setUser_no(user_no);
        contents.setTitle(title);
        contents.setBody(body);
        contents.setBoard_no(board_no);
        contents.setImages_no(localFileService.updateFiles(images_no, editImages, images));

        int update_count = contentsService.updateContents(contents);

        if(update_count == 0) {
            return HttpReturn.NOT_FOUND();
        }else if(update_count > 1){
            return HttpReturn.NOT_MODIFIED();
        }else{
            return HttpReturn.NO_CONTENT();
        }
    }

    @RequestMapping(value = "/{board_no}/contents/{contents_no}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteContents(@PathVariable int board_no, @PathVariable int contents_no) {
        int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Contents contents = new Contents();
        contents.setBoard_no(board_no);
        contents.setContents_no(contents_no);
        contents.setUser_no(user_no);

        String images_no = contentsService.getContents(board_no, contents_no).getImages_no();

        int delete_count = contentsService.deleteContents(contents);
        if(delete_count == 0){
            localFileService.deleteFiles(images_no);
            return HttpReturn.NOT_FOUND();
        }else if(delete_count > 1){
            return HttpReturn.NOT_MODIFIED();
        }else {
            return HttpReturn.NO_CONTENT();
        }
    }

    @RequestMapping(value = "/image/{file_no}", method = RequestMethod.GET)
    public ResponseEntity<?> getImageFile(@PathVariable int file_no, @RequestHeader HttpHeaders headers) throws IOException{
        CommonFile commonFile = localFileService.getCommonFile(file_no);
        if(commonFile == null || commonFile.getSave_path() == null){
            return HttpReturn.NOT_FOUND();
        }

        File file = new File(commonFile.getSave_path());
        if(!file.exists()){
            return HttpReturn.NOT_FOUND();
        }

        InputStreamResource inputStreamResource = new InputStreamResource(FileUtils.openInputStream(file));
        headers.setContentLength(commonFile.getFile_length());
        //headers.setContentDispositionFormData("attachment", imageObject.getObjectMetadata().getUserMetaDataOf("file_name"));
        //headers.setContentType(new MediaType(imageObject.getObjectMetadata().getContentType()));

        return new ResponseEntity(inputStreamResource, headers, HttpStatus.OK);
    }
}
