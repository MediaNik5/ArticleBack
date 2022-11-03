package org.catblocks.articleback.security;

import org.catblocks.articleback.security.token.AccessTokenResponseClient;
import org.catblocks.articleback.security.token.HttpCookieOAuth2AuthorizationRequestRepository;
import org.catblocks.articleback.security.token.OAuth2AuthenticationFailureHandler;
import org.catblocks.articleback.security.token.OAuth2AuthenticationSuccessHandler;
import org.catblocks.articleback.security.token.TokenAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebConfiguration  extends WebSecurityConfigurerAdapter {
    private final HttpCookieOAuth2AuthorizationRequestRepository cookieRepository;
    private final AccessTokenResponseClient accessTokenResponseClient;
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler successHandler;
    private final OAuth2AuthenticationFailureHandler failureHandler;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    public WebConfiguration(
        HttpCookieOAuth2AuthorizationRequestRepository cookieRepository,
        AccessTokenResponseClient accessTokenResponseClient,
        CustomOAuth2UserService oAuth2UserService,
        OAuth2AuthenticationSuccessHandler successHandler,
        OAuth2AuthenticationFailureHandler failureHandler,
        TokenAuthenticationFilter tokenAuthenticationFilter
    ){
        this.cookieRepository = cookieRepository;
        this.accessTokenResponseClient = accessTokenResponseClient;
        this.oAuth2UserService = oAuth2UserService;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
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
                    "/",
                    "/error",
                    "/oauth/**",
                    "/oauth2/**",
                    "/users/{id}"
                ).permitAll()
            .anyRequest()
                .authenticated()
                .and()
            .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
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
            .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
