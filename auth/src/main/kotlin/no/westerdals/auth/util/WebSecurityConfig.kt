package no.westerdals.auth.util

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter



@Configuration
@EnableWebSecurity
class WebSecurityConfig/*(
        private val dataSource: DataSource,
        private val passwordEncoder: PasswordEncoder
)*/ : WebSecurityConfigurerAdapter() {

/*
    @Bean
    override fun userDetailsServiceBean(): UserDetailsService {
        return super.userDetailsServiceBean()
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }
*/

    override fun configure(http: HttpSecurity) {

        http.csrf().disable()
        http.authorizeRequests()
                /*
                    these rules are matched one at a time, in their order.
                    this is important to keep in mind if different URL templates
                    can match the same URLs
                    commented out hasrole USER for now so we can access everything from /api/
                    at a later date we will choose who can access different pages and resources
                 */
                .antMatchers("/", "/login", "/signup",
                        "/index.html", "/api/movie", "/api/movie/**",
                        "/api/ticket", "/api/ticket/**", "/api/users",
                        "/api/users/**", "/api/test").permitAll()
                .antMatchers("/api/testUser").hasRole("USER")
                .antMatchers("/api/testAdmin").hasRole("ADMIN")
                /*
                    whitelisting: deny everything by default,
                    unless it was explicitly allowed in the rules
                    above.

                    for simplicity we let admin have rights to everything
                 */
                .anyRequest().hasRole("ADMIN")
                .and()
                /*
                    there are many different ways to define
                    how login is done.
                    So here we need to configure it.
                    We start from looking at "Basic" HTTP,
                    which is the simplest form of authentication
                  */
                .httpBasic()
        /*
        .formLogin()
        .loginPage("/login")
        .permitAll()
        .failureUrl("/login?error=true")
        .defaultSuccessUrl("/index")
        .and()
        .logout()
        .logoutSuccessUrl("/index")
        */
        /*
        http.httpBasic()
                .and()
                .logout()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                */
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        //{noop} is just there to say we want the password as plaintext and not encoded
        auth.inMemoryAuthentication()
                .withUser("foo").password("{noop}bar").roles("USER").and()
                .withUser("admin").password("{noop}admin").roles("ADMIN", "USER")
        /*
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("""
                     SELECT username, password, enabled
                     FROM users
                     WHERE username=?
                     """)
                .authoritiesByUsernameQuery("""
                     SELECT x.username, y.roles
                     FROM users x, user_entity_roles y
                     WHERE x.username=? and y.user_entity_username=x.username
                     """)
                .passwordEncoder(passwordEncoder)
        */
    }
}