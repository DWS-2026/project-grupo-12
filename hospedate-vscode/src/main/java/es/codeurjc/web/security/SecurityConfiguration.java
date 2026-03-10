package es.codeurjc.web.security;

// Importaciones estándar de Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// Importaciones estrictas de Spring Security
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    //Inyect the service that implements UserDetailsService, we will use it in the authentication provider to load user data from the database
    @Autowired
    public RepositoryUserDetailsService userDetailService; 


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    //We create an authentication provider that uses our user details service and the password encoder, this will be used by Spring Security to 
    //authenticate users
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        authProvider.setUserDetailsService(userDetailService); 
        authProvider.setPasswordEncoder(passwordEncoder()); 
        
        return authProvider;
    }

    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        http.authenticationProvider(authenticationProvider());
        
        http.authorizeHttpRequests(authorize -> authorize
            .anyRequest().permitAll() 
        );
        
        http.csrf(csrf -> csrf.disable());
        
        return http.build(); 
    }
}