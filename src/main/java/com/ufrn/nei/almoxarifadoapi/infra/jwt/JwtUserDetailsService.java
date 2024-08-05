package com.ufrn.nei.almoxarifadoapi.infra.jwt;

import com.ufrn.nei.almoxarifadoapi.entity.UserEntity;
import com.ufrn.nei.almoxarifadoapi.exception.EntityNotFoundException;
import com.ufrn.nei.almoxarifadoapi.repository.UserRepository;
import com.ufrn.nei.almoxarifadoapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(username).orElseThrow(
                () -> new EntityNotFoundException("Usuário não encontrado.")
        );

        if (!user.getActive()) {
            throw new DisabledException("Usuário inativo");
        }

        return new JwtUserDetails(user);
    }

    public JwtToken getTokenAuthenticated(String email) {
        String role = userService.findByEmail(email).getRole().getRole();

        if (role.contains("ROLE_")) {
            role = role.substring("ROLE_".length());
        }

        return JwtUtils.createToken(email, role.toUpperCase());
    }
}
