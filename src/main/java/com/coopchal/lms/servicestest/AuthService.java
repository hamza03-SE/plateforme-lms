package com.coopchal.lms.servicestest;

import com.coopchal.lms.dtos.AuthResponse;
import com.coopchal.lms.dtos.LoginRequest;
import com.coopchal.lms.dtos.RegisterRequest;
import com.coopchal.lms.exceptions.EmailAlreadyUsedException;
import com.coopchal.lms.models.Utilisateur;
import com.coopchal.lms.repositories.UtilisateurRepository;
import com.coopchal.lms.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponse register(RegisterRequest request) {


        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyUsedException("L'adresse email est déjà utilisée.");
        }

        Utilisateur user = new Utilisateur();
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setEmail(request.getEmail());
        user.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        user.setRole(request.getRole());
        utilisateurRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        Utilisateur user = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Email invalide"));

        if (!passwordEncoder.matches(request.getMotDePasse(), user.getMotDePasse())) {
            throw new BadCredentialsException("Mot de passe invalide");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token);
    }
}
