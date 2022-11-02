package org.catblocks.articleback.security.extractor;

import org.catblocks.articleback.security.Provider;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DefaultUserInfoExtractor implements Extractor<Map<String, Object>, Map<String, Object>>{
	private static final Set<String> AUTH_PROVIDERS = Arrays
		.stream(Provider.values())
		.map(Enum::toString)
		.collect(Collectors.toUnmodifiableSet());

	@Override
	public boolean canProceed(String registrationId){
		return AUTH_PROVIDERS.contains(registrationId.toUpperCase(Locale.ROOT));
	}

	@Override
	public Map<String, Object> extract(Map<String, Object> o, OAuth2UserRequest userRequest){
		return o;
	}
}
