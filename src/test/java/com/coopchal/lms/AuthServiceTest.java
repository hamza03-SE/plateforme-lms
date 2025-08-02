package com.coopchal.lms;

import com.coopchal.lms.dtos.AuthResponse;
import com.coopchal.lms.dtos.LoginRequest;
import com.coopchal.lms.dtos.RegisterRequest;
import com.coopchal.lms.enums.Role;
import com.coopchal.lms.exceptions.EmailAlreadyUsedException;
import com.coopchal.lms.models.Utilisateur;
import com.coopchal.lms.repositories.UtilisateurRepository;
import com.coopchal.lms.security.JwtTokenProvider;
import com.coopchal.lms.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class AuthServiceTest{

    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void registerWithNewEmail(){

        RegisterRequest registerRequest = new RegisterRequest("Erradi","Hamza","h@gmail.com","12345", Role.APPRENANT);
        when(utilisateurRepository.existsByEmail("h@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("12345")).thenReturn("EncodedPassword");
        when(jwtTokenProvider.generateToken("h@gmail.com","APPRENANT")).thenReturn("ABCD.123");


        AuthResponse authResponse = authService.register(registerRequest);


        assertNotNull(authResponse);
        assertEquals("ABCD.123",authResponse.getToken());
        verify(utilisateurRepository,times(1)).save(any(Utilisateur.class));
    }

    @Test
    public void registerWithEmailUsed(){
        RegisterRequest registerRequest = new RegisterRequest("Erradi","Hamza","h@gmail.com","12345", Role.APPRENANT);
        when(utilisateurRepository.existsByEmail("h@gmail.com")).thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class,() -> {
            authService.register(registerRequest);
        });

        verify(utilisateurRepository,never()).save(any());
    }


    @Test
    void login_ShouldReturnToken_WhenCredentialsAreCorrect() {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "password");
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("test@example.com");
        utilisateur.setMotDePasse("encodedPassword");
        utilisateur.setRole(Role.APPRENANT);

        when(utilisateurRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(utilisateur));

        when(passwordEncoder.matches("password", "encodedPassword"))
                .thenReturn(true);

        when(jwtTokenProvider.generateToken("test@example.com", "APPRENANT"))
                .thenReturn("fake-jwt-token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
    }



}
