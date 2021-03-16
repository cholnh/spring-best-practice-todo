package com.nzzi.guide.todo.domain.oauth.application;

import com.nzzi.guide.todo.domain.user.infrastructure.jpa.UserRepository;
import com.nzzi.guide.todo.domain.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(
        @Lazy UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("INVALID USER"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword().getPasswordValue(),
                AuthorityUtils.createAuthorityList(user.getAuthorities()));
    }
}
