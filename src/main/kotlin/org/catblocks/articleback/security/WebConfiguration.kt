package org.catblocks.articleback.security

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.catblocks.articleback.security.token.*
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
@SecurityScheme(name = "Bearer Authentication", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@EnableWebSecurity
class WebConfiguration(
    private val cookieRepository: HttpCookieOAuth2AuthorizationRequestRepository,
    private val accessTokenResponseClient: AccessTokenResponseClient,
    private val oAuth2UserService: CustomOAuth2UserService,
    private val successHandler: OAuth2AuthenticationSuccessHandler,
    private val failureHandler: OAuth2AuthenticationFailureHandler,
    private val tokenAuthenticationFilter: TokenAuthenticationFilter,
    private val setHostFilter: SetHostFilter,
) : WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .cors()
            .and()
            .csrf()
            .disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers(
                "/error",
                "/articles",
                "/oauth/**",
                "/oauth2/**",
                "/users/*",
                "/articles-statistics/*/reactions",
                "/swagger-ui/index.html",
                "/swagger-ui/**",
                "/v3/api-docs/**"
            ).permitAll()
            .antMatchers(HttpMethod.GET, "/articles/{id}")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            .and()
            .oauth2Login()
            .authorizationEndpoint()
            .baseUri("/oauth2/authorize")
            .authorizationRequestRepository(cookieRepository)
            .and()
            .redirectionEndpoint()
            .baseUri("/oauth2/callback/*")
            .and()
            .userInfoEndpoint()
            .userService(oAuth2UserService)
            .and()
            .tokenEndpoint()
            .accessTokenResponseClient(accessTokenResponseClient)
            .and()
            .successHandler(successHandler)
            .failureHandler(failureHandler)
            .and()
            .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(setHostFilter, CorsFilter::class.java)
    }

    @Bean
    fun simpleCorsFilter(): FilterRegistrationBean<CorsFilter>? {
        val config = CorsConfiguration()
        config.setAllowCredentials(true)
        config.setAllowedOrigins(
            listOf(
                "https://comgrid.ru",
                "https://comgrid.ru:444",
                "http://localhost:3000",
                "http://localhost:3001",
                "http://localhost:8080",
                "http://localhost:8000",
                "https://restless-escape-632568.postman.co"
            )
        )
        config.setAllowedMethods(listOf("GET", "HEAD", "POST", "PUT", "DELETE", "PATCH"))
        config.setAllowedHeaders(listOf("Content-Types", "Content-Type", "authorization", "x-auth-token", "Origin"))
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        val bean: FilterRegistrationBean<CorsFilter> = FilterRegistrationBean(CorsFilter(source))
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE)
        return bean
    }

}