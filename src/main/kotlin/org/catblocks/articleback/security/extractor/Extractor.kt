package org.catblocks.articleback.security.extractor;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

public interface Extractor<Result, E>{
	boolean canProceed(String registrationId);
	Result extract(E o, OAuth2UserRequest userRequest);
}
