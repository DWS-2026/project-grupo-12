package es.codeurjc.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

//import the necessary classes to manage the user session and access the user data in the database
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.UserRepository;
import es.codeurjc.web.service.UserSession;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    public RepositoryUserDetailsService userDetailService; 

    @Autowired
    private UserSession userSession;

    @Autowired
    private UserRepository userRepository;

    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


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
        
        http
            .authorizeHttpRequests(authorize -> authorize
            //public routes

            //private routes
            .requestMatchers("/admin/**").hasRole("ADMIN")    
            .requestMatchers("/profile/**").hasAnyRole("USER","ADMIN")
            .requestMatchers("/reserve/delete/**").hasAnyRole("USER","ADMIN")
            .requestMatchers("/reserve/**").hasAnyRole("USER")
            .requestMatchers("/hotel/{id}/review/**").hasAnyRole("USER")
            
            .requestMatchers("/").permitAll()
            .requestMatchers("/login").permitAll()
            .requestMatchers("/register").permitAll()
            .requestMatchers("/hotels").permitAll()
            .requestMatchers("/hotel/**").permitAll() 
            .requestMatchers("/assets/**", "/css/**", "/js/**", "/uploads/**").permitAll()
            
            

            .anyRequest().authenticated()
        )
        
        .formLogin(formLogin -> formLogin
            .loginPage("/login")
            .usernameParameter("email") 
            .failureUrl("/login?error=true")
            //user session after successful login
            //user session after successful login
            .successHandler((request, response, authentication) -> {
                String email = authentication.getName();
                User user = userRepository.findByEmail(email).orElseThrow();
                
                //fill the user session with the logged user's data
                userSession.modifySessionInfo(user);
                
                boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                if (isAdmin) {
                    //if the user is an admin, we send him to the admin dashboard
                    response.sendRedirect("/admin/dashboard");
                } else {
                    //if the user is a normal user, we send him to his profile page
                    response.sendRedirect("/profile");
                }
            })
            .permitAll()
    )
    .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
            .permitAll()
    );
    
        http.csrf(csrf -> csrf.disable());
        return http.build(); 
    }
}