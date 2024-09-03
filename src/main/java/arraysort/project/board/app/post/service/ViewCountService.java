package arraysort.project.board.app.post.service;

import arraysort.project.board.app.exception.IdNotFoundException;
import arraysort.project.board.app.post.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ViewCountService {

	private static final String VIEW_KEY_PREFIX = "post:view:";
	private static final long VIEW_TIMEOUT = 1;

	private final RedisTemplate<String, Object> redisTemplate;

	private final PostMapper postMapper;

	// 조회수 증가
	@Transactional
	public void increaseViewCount(Long postId, String ipAddress) {
		if (!isViewed(postId, ipAddress)) {
			String key = "viewCount";
			redisTemplate.opsForZSet().incrementScore(key, String.valueOf(postId), 1);
			addView(postId, ipAddress);

			// DB 에 조회수 증가 반영
			increaseViews(postId);
		}
	}

	/**
	 * Redis : 조회 여부
	 *
	 * @param postId    조회하는 게시글 ID
	 * @param ipAddress 조회하는 사용자 IP
	 * @return 조회 여부
	 */
	private boolean isViewed(Long postId, String ipAddress) {
		String key = VIEW_KEY_PREFIX + postId;
		return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, ipAddress));
	}


	/**
	 * Redis : 조회 여부 추가
	 *
	 * @param postId    조회하는 게시글 ID
	 * @param ipAddress 조회하는 사용자 IP
	 */
	private void addView(Long postId, String ipAddress) {
		String key = VIEW_KEY_PREFIX + postId;
		// Key = post:view:게시글 ID Value = 접속자 IP 주소
		redisTemplate.opsForSet().add(key, ipAddress);
		// 만료시간 : 1분, 1분 뒤 조회 시 조회수 증가
		redisTemplate.expire(key, VIEW_TIMEOUT, TimeUnit.MINUTES);
	}

	/**
	 * 게시글 세부내용 조회 시 조회수 증가
	 *
	 * @param postId 세부내용 조회 한 게시글 ID
	 */
	private void increaseViews(long postId) {
		if (postMapper.selectExistPostId(postId).isEmpty()) {
			throw new IdNotFoundException();
		}
		postMapper.updateViews(postId);
	}
}
