package com.lucas.redditclone.service.vote;

import com.lucas.redditclone.dto.request.vote.VoteRequestBody;
import com.lucas.redditclone.entity.Vote;
import com.lucas.redditclone.exception.bad_request.BadRequestException;
import com.lucas.redditclone.mapper.VoteMapper;
import com.lucas.redditclone.repository.PostRepository;
import com.lucas.redditclone.repository.UserRepository;
import com.lucas.redditclone.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.lucas.redditclone.entity.enums.VoteType.UPVOTE;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class VoteServiceImpl implements VoteService {
	private final VoteRepository voteRepository;
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final VoteMapper mapper;

	@Override
	public void vote(VoteRequestBody voteRequestBody) {
		var post = postRepository
				.findById(voteRequestBody.getPostId())
				.orElseThrow(() -> new BadRequestException("No posts found"));
		var user = userRepository.findById(voteRequestBody.getUserId())
				.orElseThrow(() -> new BadRequestException("User not found"));

		Optional<Vote> voteOptional = voteRepository.findTopByPostAndUserOrderByIdDesc(post, user);

		if (voteOptional.isPresent() && voteOptional.get().getVoteType().equals(voteRequestBody.getVoteType())) {
			throw new BadRequestException("You have already " + voteRequestBody.getVoteType().name() + "D this post.");
		}

		if (UPVOTE.equals(voteRequestBody.getVoteType())) {
			post.setVoteCount(post.getVoteCount() + 1);
		} else {
			post.setVoteCount(post.getVoteCount() - 1);
		}

		Vote vote = mapper.toVote(voteRequestBody, post, user);

		voteRepository.save(vote);
		log.info("user name: {}", vote.getUser().getName());
	}
}
