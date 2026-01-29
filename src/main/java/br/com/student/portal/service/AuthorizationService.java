package br.com.student.portal.service;

import br.com.student.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Buscando usuário por email: {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Tentativa de login com email não cadastrado: {}", email);
                    return new UsernameNotFoundException("Usuário não encontrado com email: " + email);
                });
    }
}