package com.lucas.redditclone.service.comment;


import com.lucas.redditclone.dto.request.comment.CommentRequestBody;
import com.lucas.redditclone.dto.response.comment.CommentResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CommentService {

	CommentResponseBody createComment(CommentRequestBody commentRequestBody);

	Page<CommentResponseBody> getAllCommentsByPost(UUID id, Pageable pageable);

	Page<CommentResponseBody> getAllUserComments(String username, Pageable pageable);
}
