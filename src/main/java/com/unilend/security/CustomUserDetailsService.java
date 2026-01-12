//Spring Security doesn't understand what UserRepository is; it only understands UserDetailsService. This file will act as an "interpreter".
package com.unilend.security;

import com.unilend.entity.User;
import com.unilend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. find users in DB by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 2. Returns a UserDetails object that Spring Security understands.
        // (For now, leave the roles empty because we haven't implemented permissions yet - Phase 0)
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>()
        );
    }
}
