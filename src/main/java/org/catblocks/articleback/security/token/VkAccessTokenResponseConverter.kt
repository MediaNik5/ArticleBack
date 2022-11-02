package org.catblocks.articleback.security.token

import org.springframework.core.convert.converter.Converter
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.util.StringUtils


class VkAccessTokenResponseConverter :
    Converter<Map<String, Any>, OAuth2AccessTokenResponse> {
    companion object {
        private val TOKEN_RESPONSE_PARAMETER_NAMES = setOf(
            OAuth2ParameterNames.ACCESS_TOKEN,
            OAuth2ParameterNames.EXPIRES_IN,
            OAuth2ParameterNames.SCOPE
        )
    }

    override fun convert(source: Map<String, Any>): OAuth2AccessTokenResponse {
        val accessToken = getParameterValue(source, OAuth2ParameterNames.ACCESS_TOKEN)
        val expiresIn = getExpiresIn(source)
        val scopes = getScopes(source)
        val refreshToken = getParameterValue(source, OAuth2ParameterNames.REFRESH_TOKEN)
        val additionalParameters: MutableMap<String, Any> = HashMap()
        for ((key, value) in source) {
            if (!TOKEN_RESPONSE_PARAMETER_NAMES.contains(key)) {
                additionalParameters[key] = value
            }
        }
        // @formatter:off
        return OAuth2AccessTokenResponse.withToken(accessToken)
            .tokenType(OAuth2AccessToken.TokenType.BEARER)
            .expiresIn(expiresIn)
            .scopes(scopes)
            .refreshToken(refreshToken)
            .additionalParameters(additionalParameters)
            .build()
    }

    private fun getExpiresIn(tokenResponseParameters: Map<String, Any>): Long {
        return getParameterValue(tokenResponseParameters, OAuth2ParameterNames.EXPIRES_IN, 0L)
    }

    private fun getScopes(tokenResponseParameters: Map<String, Any>): Set<String> {
        if (tokenResponseParameters.containsKey(OAuth2ParameterNames.SCOPE)) {
            val scope = getParameterValue(tokenResponseParameters, OAuth2ParameterNames.SCOPE)
            return setOf(*StringUtils.delimitedListToStringArray(scope, " "))
        }
        return emptySet()
    }

    private fun getParameterValue(tokenResponseParameters: Map<String, Any>, parameterName: String): String? {
        val obj = tokenResponseParameters[parameterName]
        return obj?.toString()
    }

    @Suppress("FoldInitializerAndIfToElvis")
    private fun getParameterValue(
        tokenResponseParameters: Map<String, Any>,
        parameterName: String,
        defaultValue: Long,
    ): Long {
        var parameterValue: Long = defaultValue
        val obj = tokenResponseParameters[parameterName]
        if (obj == null) return parameterValue
        // Final classes Long and Integer do not need to be coerced
        try {
            parameterValue = when (obj) {
                is Long -> obj
                is Int -> obj.toLong()
                else -> {
                    // Attempt to coerce to a long (typically from a String)
                    obj.toString().toLong()
                }
            }
        } catch (ignored: NumberFormatException) {
        }
        return parameterValue
    }
}

