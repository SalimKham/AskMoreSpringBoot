package io.khaminfo.ppmtool.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.khaminfo.ppmtool.services.CostumUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)

public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private JwtAuthanticationEntryPoint jwtAuthEntryPoint;
	@Autowired
	private CostumUserDetailsService costumUserDetailsService;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Bean
	public JWTAuthFilter authFilter () { return new JWTAuthFilter();}

	@Override
	protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(costumUserDetailsService)
				.passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	protected AuthenticationManager authenticationManager() throws Exception {
		// TODO Auto-generated method stub
		return super.authenticationManager();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().exceptionHandling().authenticationEntryPoint(jwtAuthEntryPoint).and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().headers()
				.frameOptions().sameOrigin().and().authorizeRequests()
				.antMatchers("/", SecurityConstants.H2_URL, SecurityConstants.SIGN_UP_URLS, "/favicon.ico", "/**/*.png",
						"/**/*.gif", "/**/*.html", "/**/*.jpg", "/**/*.css", "/**/*.js")
				.permitAll().anyRequest().authenticated();
		http.addFilterBefore(authFilter(), UsernamePasswordAuthenticationFilter.class);

	}

}
