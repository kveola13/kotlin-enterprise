package no.westerdals.user.security

import org.springframework.context.annotation.Configuration

@Configuration
@org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
class WebSecurityConfig : org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter() {

    override fun configure(http: org.springframework.security.config.annotation.web.builders.HttpSecurity) {

        http.csrf().disable()
        http.authorizeRequests()
            .antMatchers("/api/userCount").permitAll()
            .antMatchers("/api/user").hasRole("ADMIN")

            .antMatchers("/api/user/{id}/**")
            .hasAnyRole("USER", "ADMIN")
            /*
                the "#" resolves the variable in the path, "{id}" in this case.
                the "@" resolves a current bean.
              */
            //.access("hasRole('USER') and @userSecurity.checkId(authentication, #id)")
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
    }

    override fun configure(auth: org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder) {
        //{noop} is just there to say we want the password as plaintext and not encoded
        auth.inMemoryAuthentication()
            .withUser("foo").password("{noop}bar").roles("USER").and()
            .withUser("admin").password("{noop}admin").roles("ADMIN", "USER")
    }
}
