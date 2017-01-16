package towntalk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import towntalk.model.Comment;
import towntalk.service.CommentService;
import towntalk.util.HttpReturn;

/**
 * Created by sin31 on 2016-06-13.
 */

@RestController
@RequestMapping(value = "/contents")
public class CommentController {
    static final Logger logger = LoggerFactory.getLogger(ContentsController.class);

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = "/{contents_no}/comment", method = RequestMethod.GET)
    public ResponseEntity<?> getCommentList(
            @PathVariable int contents_no){

        Comment comment = new Comment();
        comment.setContents_no(contents_no);

        return HttpReturn.OK(commentService.getCommentList(comment));
    }

    @RequestMapping(value = "/{contents_no}/comment/{comment_no}", method = RequestMethod.GET)
    public ResponseEntity<?> getComment(@PathVariable int contents_no, @PathVariable int comment_no){

        Comment comment = new Comment();
        comment.setContents_no(contents_no);
        comment.setComment_no(comment_no);

        comment = commentService.getComment(comment);
        if(comment == null){
            return HttpReturn.NOT_FOUND();
        }else{
            return HttpReturn.OK(comment);
        }
    }


    @RequestMapping(value = "/{contents_no}/comment", method = RequestMethod.POST)
    public ResponseEntity<?> insertComment(@PathVariable int contents_no, @RequestBody Comment comment){
        int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        comment.setContents_no(contents_no);
        comment.setUser_no(user_no);

        int insert_count = commentService.insertComment(comment);
        if(insert_count == 1){
            return HttpReturn.NO_CONTENT();
        }else{
            return HttpReturn.INTERNAL_SERVER_ERROR();
        }
    }

    @RequestMapping(value = "/{contents_no}/comment", method = RequestMethod.PUT)
    public ResponseEntity<?> editComment(@PathVariable int contents_no, @RequestBody Comment comment){
        int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        comment.setContents_no(contents_no);
        comment.setUser_no(user_no);

        int edit_count = commentService.editComment(comment);
        if(edit_count == 1){
            return HttpReturn.NO_CONTENT();
        }else{
            return HttpReturn.INTERNAL_SERVER_ERROR();
        }
    }

    @RequestMapping(value = "/{contents_no}/comment/{comment_no}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteComment(@PathVariable int contents_no, @PathVariable int comment_no){
        int user_no = (Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Comment comment = new Comment();
        comment.setContents_no(contents_no);
        comment.setComment_no(comment_no);
        comment.setUser_no(user_no);

        int delete_count = commentService.deleteComment(comment);
        if(delete_count == 0){
            return HttpReturn.NOT_FOUND();
        }else if(delete_count > 1){
            return HttpReturn.NOT_MODIFIED();
        }else{
            return HttpReturn.NO_CONTENT();
        }
    }
}
