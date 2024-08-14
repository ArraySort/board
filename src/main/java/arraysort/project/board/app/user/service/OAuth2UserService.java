package arraysort.project.board.app.user.service;

import arraysort.project.board.app.user.domain.GoogleAttributes;
import arraysort.project.board.app.user.domain.OAuthAttributes;
import arraysort.project.board.app.user.domain.OAuthDTO;
import arraysort.project.board.app.user.domain.OAuthVO;
import arraysort.project.board.app.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserService implements org.springframework.security.oauth2.client.userinfo.OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserMapper userMapper;

	@Transactional
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		org.springframework.security.oauth2.client.userinfo.OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

		Map<String, Object> attribute = new HashMap<>(oAuth2User.getAttributes());

		OAuthAttributes oAuthAttributes = new GoogleAttributes(attribute, userNameAttributeName, registrationId);

		String userIdentify = oAuthAttributes.getUserIdentify();
		String name = oAuthAttributes.getName();

		OAuthDTO oAuthDTO = new OAuthDTO(userIdentify, name, oAuthAttributes.getOAuthProvider());

		String userId = String.valueOf(saveOrUpdate(oAuthDTO));

		oAuthAttributes.getAttributes().put("userId", userId);

		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("USER");

		return new DefaultOAuth2User(
				Collections.singleton(grantedAuthority),
				oAuthAttributes.getAttributes(),
				"userId"
		);
	}

	private String saveOrUpdate(OAuthDTO dto) {
		Optional<OAuthVO> vo = userMapper.selectOAuthUserByUserId(dto.getUserId());
		return vo.map(OAuthVO::getUserId).orElseGet(() -> signupOAuth(dto));
	}

	private String signupOAuth(OAuthDTO dto) {
		OAuthVO vo = OAuthVO.of(dto);
		userMapper.insertOAuthUser(vo);
		return vo.getUserId();
	}
}
