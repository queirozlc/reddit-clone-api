package com.lucas.redditclone.service.comment;

import com.lucas.redditclone.dto.request.comment.CommentRequestBody;
import com.lucas.redditclone.dto.response.MailResponseBody;
import com.lucas.redditclone.dto.response.comment.CommentResponseBody;
import com.lucas.redditclone.entity.Comment;
import com.lucas.redditclone.exception.bad_request.BadRequestException;
import com.lucas.redditclone.exception.not_found.NotFoundException;
import com.lucas.redditclone.mapper.CommentMapper;
import com.lucas.redditclone.repository.CommentRepository;
import com.lucas.redditclone.repository.PostRepository;
import com.lucas.redditclone.repository.UserRepository;
import com.lucas.redditclone.service.impl.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final CommentMapper mapper;
	private final EmailService emailService;
	@Value("${spring.mail.username}")
	private String emailFrom;


	@Override
	public CommentResponseBody createComment(CommentRequestBody commentRequestBody) {
		var post = postRepository.findById(commentRequestBody.getPostId())
				.orElseThrow(() -> new BadRequestException("No posts found."));
		var user = userRepository.findById(commentRequestBody.getParentId())
				.orElseThrow(() -> new BadRequestException("User not found."));
		var comment = mapper.toComment(commentRequestBody, post, user);
		Comment commentSaved = commentRepository.save(comment);
		sendEmail(commentSaved);
		return mapper.toCommentResponseBody(commentSaved);
	}


	@Override
	public Page<CommentResponseBody> getAllCommentsByPost(UUID id, Pageable pageable) {
		var post = postRepository.findById(id).orElseThrow(() -> new BadRequestException("No post found"));
		return commentRepository.findAllByPost(post, pageable).map(mapper::toCommentResponseBody);
	}

	@Override
	public Page<CommentResponseBody> getAllUserComments(String username, Pageable pageable) {
		var user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BadRequestException("User not found."));

		Page<Comment> comments = commentRepository.findAllByUser(user, pageable);

		if (comments.isEmpty()) {
			throw new NotFoundException("User have no comments yet.");
		}

		return comments.map(mapper::toCommentResponseBody);
	}

	@Override
	public void delete(UUID id) {
		Comment comment = commentRepository.findById(id)
				.orElseThrow(() -> new BadRequestException("Comment not found"));
		commentRepository.delete(comment);
	}

	private void sendEmail(Comment commentSaved) {
		var subject = "No reply: New comment on your post. Take a look :)";
		var message = commentSaved.getUser().getUsername() + " made a comment on your post: " + commentSaved
				.getPost().getTitle() + " on RedditClone, take a look: " +
				"\n" + "http://localhost:8080/api/comments/post/" + commentSaved.getPost().getId();

		var mailResponseBody = MailResponseBody
				.builder()
				.emailTo(commentSaved.getPost().getUser().getEmail())
				.emailFrom(emailFrom)
				.ownerId(commentSaved.getPost().getUser().getId())
				.subject(subject)
				.message(message)
				.build();
		emailService.sendEmail(mailResponseBody);
	}
}
