package arraysort.project.board.app.user.service;

import arraysort.project.board.app.common.OAuthAttributeFactory;
import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.exception.NotActivatedUserException;
import arraysort.project.board.app.user.domain.OAuthDTO;
import arraysort.project.board.app.user.domain.OAuthVO;
import arraysort.project.board.app.user.domain.attributes.OAuthAttributes;
import arraysort.project.board.app.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static arraysort.project.board.app.common.Constants.ROLE_USER;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserMapper userMapper;

	@Transactional
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();

		// Provider ID : [GOOGLE : google, KAKAO : kakao, NAVER : naver]
		String registrationId = userRequest.getClientRegistration().getRegistrationId();

		// Provider 가 제공하는 클라이언트 ID 의 Key 값 => [GOOGLE : sub, KAKAO : id, NAVER : response]
		String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

		Map<String, Object> attributes = new HashMap<>(oAuth2UserService.loadUser(userRequest).getAttributes());

		// Provider 에 따른 Attributes 반환 : [registrationId : google, kakao, naver]
		OAuthAttributes oAuthAttributes = OAuthAttributeFactory.getAttributeByProvider(attributes, registrationId, userNameAttributeName);

		String userIdentify = oAuthAttributes.getUserIdentify();
		String userName = oAuthAttributes.getUserName();

		OAuthDTO dto = new OAuthDTO(userIdentify, userName, oAuthAttributes.getOAuthProvider());
		String userId = String.valueOf(checkOAuthUser(dto));

		oAuthAttributes.getAttributes().put("userId", userId);

		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(ROLE_USER);

		return new DefaultOAuth2User(
				Collections.singleton(grantedAuthority),
				oAuthAttributes.getAttributes(),
				"userId"
		);
	}

	/**
	 * OAuth 로 로그인을 진행한 유저인지 검사
	 * 최초 로그인 시 OAuth 가입 절차 진행
	 *
	 * @param dto OAuth 제공자로부터 생성된 회원 정보
	 * @return userId
	 */
	private String checkOAuthUser(OAuthDTO dto) {
		Optional<OAuthVO> vo = userMapper.selectOAuthUserByUserId(dto.getUserId());
		OAuthVO user = vo.orElseGet(() -> signupOAuth(dto));

		validateUser(user);

		return user.getUserId();
	}

	/**
	 * OAuth 가입
	 *
	 * @param dto OAuth 제공자로부터 생성된 회원 정보
	 * @return vo DB 에 저장된 회원 정보
	 */
	private OAuthVO signupOAuth(OAuthDTO dto) {
		OAuthVO vo = OAuthVO.of(dto);
		userMapper.insertOAuthUser(vo);
		return vo;
	}


	/**
	 * 로그인 하려는 유저가 비활성화 상태인지, 삭제된 상태인지 검증
	 *
	 * @param vo 로그인 유저의 정보
	 */
	private void validateUser(OAuthVO vo) {
		if (vo.getActivateFlag() == Flag.N) {
			throw new NotActivatedUserException("관리자에 의해 비활성화 된 계정입니다.");
		}

		if (vo.getDeleteFlag() == Flag.Y) {
			throw new UsernameNotFoundException(vo.getUserId());
		}
	}
}
