package org.catblocks.articleback.security

import org.catblocks.articleback.model.User
import org.catblocks.articleback.repository.UserRepository
import org.catblocks.articleback.security.OAuth2UserInfoFactory.getOAuth2UserInfo
import org.catblocks.articleback.security.converter.RequestConverter
import org.catblocks.articleback.security.extractor.UserInfoExtractorService
import org.catblocks.articleback.security.userinfo.OAuth2UserInfo
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.ProviderNotFoundException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2AuthorizationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.UnknownContentTypeException
import java.util.*

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository,
    private val userInfoExtractorService: UserInfoExtractorService,
    private val requestConverter: RequestConverter,
) : DefaultOAuth2UserService() {

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val user = defaultLoadUser(userRequest)
        return try {
            processOAuth2User(userRequest, user)
        } catch (e: OAuth2AuthenticationException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @Throws(OAuth2AuthenticationException::class)
    fun defaultLoadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val userNameAttributeName = getUserNameAttributeName(userRequest)
        val request = this.requestConverter.convert(userRequest)
        val userAttributes = getResponse(userRequest, request)
        val authorities: MutableSet<GrantedAuthority> = LinkedHashSet()
        authorities.add(OAuth2UserAuthority(userAttributes))
        val token = userRequest.accessToken
        for (authority in token.scopes) {
            authorities.add(SimpleGrantedAuthority("SCOPE_$authority"))
        }
        return DefaultOAuth2User(authorities, userAttributes, userNameAttributeName)
    }

    @Throws(OAuth2AuthenticationException::class)
    private fun processOAuth2User(oAuth2UserRequest: OAuth2UserRequest, oAuth2User: OAuth2User): OAuth2User {
        val oAuth2UserInfo = getOAuth2UserInfo(
            oAuth2UserRequest.clientRegistration.registrationId,
            oAuth2User.attributes
        )

        if (oAuth2UserInfo.email.isBlank()) {
            throw AuthenticationCredentialsNotFoundException("Email not found from OAuth2 provider")
        }
        val userOptional = userRepository.findById(oAuth2UserInfo.id).orElse(null)

        val user: User = if (userOptional != null) {
            if (userOptional.provider != Provider.valueOf(
                    oAuth2UserRequest.clientRegistration.registrationId.uppercase(Locale.getDefault()))) {
                throw AuthenticationCredentialsNotFoundException(
                    "Looks like you're signed up with " +
                            userOptional.provider + " account. Please use your " + userOptional.provider +
                            " account to login."
                )
            }
            userOptional
        } else {
            registerNewUser(oAuth2UserRequest, oAuth2UserInfo)
        }
        return UserPrincipal.create(user, oAuth2User.attributes)
    }

    private fun getUserNameAttributeName(userRequest: OAuth2UserRequest): String {
        val client = userRequest.clientRegistration
        client.providerDetails.userInfoEndpoint.uri
        if (client.providerDetails.userInfoEndpoint.uri.isNullOrBlank()) {
            val oauth2Error = OAuth2Error(MISSING_USER_INFO_URI_ERROR_CODE,
                "Missing required UserInfo Uri in UserInfoEndpoint for Client Registration: "
                        + client.registrationId,
                null)
            throw OAuth2AuthenticationException(oauth2Error, oauth2Error.toString())
        }
        val userNameAttributeName = client.providerDetails.userInfoEndpoint.userNameAttributeName
        if (userNameAttributeName.isNullOrBlank()) {
            val oauth2Error = OAuth2Error(MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE,
                "Missing required \"user name\" attribute name in UserInfoEndpoint for Client Registration: "
                        + client.registrationId,
                null)
            throw OAuth2AuthenticationException(oauth2Error, oauth2Error.toString())
        }
        return userNameAttributeName
    }

    fun getResponse(userRequest: OAuth2UserRequest, request: RequestEntity<*>): Map<String, Any?> {
        return try {
            val response: ResponseEntity<Map<String, Any?>> =
                RestTemplate().exchange(request, Any::class.java) as ResponseEntity<Map<String, Any?>>

            userInfoExtractorService.extract(response, userRequest)
        } catch (ex: OAuth2AuthorizationException) {
            throw OAuth2AuthorizationException(ex.error, ex)
        } catch (ex: UnknownContentTypeException) {
            val userInfoEndpoint = userRequest.clientRegistration.providerDetails.userInfoEndpoint.uri
            val registrationId = userRequest.clientRegistration.registrationId
            val oAuth2Error = OAuth2Error(
                INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                "An error occurred while attempting to retrieve the UserInfo Resource from '"
                        + userInfoEndpoint
                        + "': response contains invalid content type '" + ex.contentType + "'. "
                        + "The UserInfo Response should return a JSON object (content type 'application/json') "
                        + "that contains a collection of name and value pairs of the claims about the authenticated End-User. "
                        + "Please ensure the UserInfo Uri in UserInfoEndpoint for Client Registration '"
                        + registrationId + "' conforms to the UserInfo Endpoint, "
                        + "as defined in OpenID Connect 1.0: 'https://openid.net/specs/openid-connect-core-1_0.html#UserInfo'",
                null
            )
            throw OAuth2AuthenticationException(oAuth2Error, oAuth2Error.toString(), ex)
        } catch (ex: RestClientException) {
            val oauth2Error = OAuth2Error(
                INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                "An error occurred while attempting to retrieve the UserInfo Resource: " + ex.message, null
            )
            throw OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex)
        }
    }

    private fun registerNewUser(oAuth2UserRequest: OAuth2UserRequest, oAuth2UserInfo: OAuth2UserInfo): User {
        val user = User(
            oAuth2UserInfo.id,
            oAuth2UserInfo.name,
            oAuth2UserInfo.email,
            oAuth2UserInfo.imageUrl,
            Provider.valueOf(oAuth2UserRequest.clientRegistration.registrationId.uppercase(Locale.getDefault()))
        )
        return userRepository.save(user)
    }

    companion object {
        private const val INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response"
        private const val MISSING_USER_INFO_URI_ERROR_CODE = "missing_user_info_uri"
        private const val MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE = "missing_user_name_attribute"
    }
}

object OAuth2UserInfoFactory {
    private val AUTH_PROVIDERS = listOf(*Provider.values())
    fun getOAuth2UserInfo(registrationId: String, attributes: Map<String, Any?>): OAuth2UserInfo {
        return AUTH_PROVIDERS.stream()
            .filter { authProvider: Provider -> authProvider.toString() == registrationId.uppercase(Locale.getDefault()) }
            .findFirst()
            .orElseThrow { ProviderNotFoundException("We don't support $registrationId yet") }
            .convert(attributes)
    }
}