package es.codeurjc.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//import the necessary classes to manage the user session and access the user data in the database
import es.codeurjc.web.model.User;
import es.codeurjc.web.repository.UserRepository;
import es.codeurjc.web.security.jwt.JwtRequestFilter;
import es.codeurjc.web.security.jwt.UnauthorizedHandlerJwt;
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

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Api filter chain
    @Bean
    @Order(1) // The API must be checked first
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http.securityMatcher("/api/v1/**"); // It only acts on the API URLs

        http.authenticationProvider(authenticationProvider());

        // The API does not use CSRF because it uses a token
        http.csrf(csrf -> csrf.disable());

        // The API is Stateless (no server-side session)
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // If authentication fails, we return a 401 in JSON instead of redirecting to
        // the Login page
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandlerJwt));

        // We add the JWT filter before the default filter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(authorize -> authorize
                // API Public Routes – no authentication required
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/hotels/**").permitAll()   // anyone can browse hotels
                .requestMatchers(HttpMethod.GET, "/api/v1/reviews/**").permitAll()  // anyone can read reviews
                .requestMatchers(HttpMethod.GET, "/api/v1/images/**").permitAll()   // anyone can load images

                // Private API routes – role-based access control
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")                          // admin panel: ADMIN only
                .requestMatchers("/api/v1/reserves/**").hasAnyRole("USER", "ADMIN")            // reserves: logged-in users
                .requestMatchers(HttpMethod.POST, "/api/v1/reviews/**").hasAnyRole("USER", "ADMIN")   // create review: logged-in
                .requestMatchers(HttpMethod.DELETE, "/api/v1/reviews/**").hasAnyRole("USER", "ADMIN") // delete review: logged-in
                .requestMatchers(HttpMethod.POST, "/api/v1/hotels/**").hasRole("ADMIN")        // create hotel: ADMIN only
                .requestMatchers(HttpMethod.PUT, "/api/v1/hotels/**").hasRole("ADMIN")         // edit hotel: ADMIN only
                .requestMatchers(HttpMethod.DELETE, "/api/v1/hotels/**").hasRole("ADMIN")      // delete hotel: ADMIN only

                // Any other API endpoint requires authentication
                .anyRequest().authenticated());

        return http.build();
    }

    // Web filter chain
    @Bean
    @Order(2) // If the URL does not start with /api/v1/, it goes to this configuration
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        // Here CSRF is enabled by default (mandatory for the web)

        http.authorizeHttpRequests(authorize -> authorize
                // Public Web Routes
                .requestMatchers("/").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/register").permitAll()
                .requestMatchers("/hotels").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/hotel/**").permitAll()
                .requestMatchers("/assets/**", "/css/**", "/js/**", "/uploads/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/hotel/image/**", "/profile/avatar").permitAll()
                
                // Swagger (API documentation)
                .requestMatchers("/v3/api-docs*/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()

                // Private Web Routes
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/profile/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/reserve/delete/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/reserve/**").hasAnyRole("USER")

                .anyRequest().authenticated());

        http.formLogin(formLogin -> formLogin
                .loginPage("/login")
                .usernameParameter("email")
                .failureUrl("/login?error=true")
                .successHandler((request, response, authentication) -> {
                    String email = authentication.getName();
                    User user = userRepository.findByEmail(email).orElseThrow();
                    userSession.modifySessionInfo(user);

                    boolean isAdmin = authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    if (isAdmin) {
                        response.sendRedirect("/admin/dashboard");
                    } else {
                        response.sendRedirect("/profile");
                    }
                })
                .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }
}