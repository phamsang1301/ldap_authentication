package com.asm.ldap.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.asm.ldap.repo.LogService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {	
	
	@Autowired
	private LogService logService;
	
	@Bean
    public SimpleUrlAuthenticationFailureHandler failureHandler() {
        return new SimpleUrlAuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
            	String username = request.getParameter("username");
            	logService.addLog(null,"Login", "Login Failure: Username typed in: " + username);
                response.sendError(401, "Authentication failure");
            }
        };
    }

    @Bean
    public SimpleUrlAuthenticationSuccessHandler successHandler() {
        return new SimpleUrlAuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            	logService.addLog(null,"Login", "Login Successful: User " + authentication.getName());
            	String username = request.getParameter("username");
            	String roles = RoleUtil.getRolesStrByUid(username);
            	response.setStatus(200);
            	response.getWriter().print(roles);
            	response.flushBuffer();
            	response.getWriter().close();
            }
        };
    }
    
    private LogoutSuccessHandler logoutSuccessHandler() {
        return (httpServletRequest, httpServletResponse, authentication) -> 
        {
        	logService.addLog(null,"Logout", "Logout Successful: User " + authentication.getName());
        	httpServletResponse.setStatus(200);
        };
    }
    
    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (httpServletRequest, httpServletResponse, e) -> 
        {
        	logService.addLog(null,"Authentication", "Unauthorized access");
        	httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        };
    }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
    .csrf().disable().cors().configurationSource(new CorsConfigurationSource() {
        @Override
        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.addAllowedOriginPattern("*");
            config.setAllowCredentials(true);
            return config;
        }
      }).and().authorizeRequests()
        .antMatchers("/admin/**").hasRole("ADMIN")
        .antMatchers("/user/**").hasRole("USER")
        .anyRequest().fullyAuthenticated()
      .and()
        .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
      .and()
        .formLogin()
//      	.loginProcessingUrl("/auth")
      	.successHandler(successHandler())
        .failureHandler(failureHandler())
//        .usernameParameter("j_username")
//        .passwordParameter("j_password")
//        .defaultSuccessUrl("/loginSuccessful", true)
      	.permitAll()
      .and()
        .logout()
        .logoutUrl("/logout")
        .logoutSuccessHandler(logoutSuccessHandler())
        .permitAll()
      .and()
      	.httpBasic()
    ;
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
      .ldapAuthentication()
        .userDnPatterns("uid={0},ou=people")
        .userSearchBase("ou=people")
        .userSearchFilter("uid={0}")
        .groupSearchBase("ou=groups")
        .groupSearchFilter("uniqueMember={0}")
        .contextSource()
          .url("ldap://192.168.134.129:389/dc=asm,dc=com")
          .and()
        .passwordCompare()
          .passwordEncoder(new BCryptPasswordEncoder())
          .passwordAttribute("userPassword");
  }
}
