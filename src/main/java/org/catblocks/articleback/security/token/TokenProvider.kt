package org.catblocks.articleback.security.token

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import org.catblocks.articleback.security.UserPrincipal
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.security.Key
import java.security.SignatureException
import java.util.*
import javax.crypto.spec.SecretKeySpec


@Service
class TokenProvider(
    @Value("\${auth.tokenSecret}") tokenSecret: String,
    @Value("\${auth.tokenExpirationMsec}") private val tokenExpirationMsec: Long,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(TokenProvider::class.java)
    }

    private val tokenSecret: ByteArray = Decoders.BASE64.decode(tokenSecret)
    private val jwtParser: JwtParser = Jwts.parserBuilder()
        .setSigningKey(tokenSecret)
        .build()

    fun createToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserPrincipal
        val now = Date()
        val expiryDate = Date(now.time + tokenExpirationMsec)
        val key: Key = SecretKeySpec(tokenSecret, SignatureAlgorithm.HS512.jcaName)
        return Jwts.builder()
            .setSubject(userPrincipal.id)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key)
            .compact()
    }

    fun getUserIdFromToken(token: String): String {
        val claims = jwtParser
            .parseClaimsJws(token)
            .body
        return claims.subject
    }

    fun validateToken(authToken: String): Boolean {
        try {
            jwtParser.parseClaimsJws(authToken)
            return true
        } catch (ex: SignatureException) {
            logger.error("Invalid JWT signature")
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token")
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token")
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty.")
        }
        return false
    }
}