package com.splitwise.microservices.user_service.configuration;

import com.splitwise.microservices.user_service.entity.User;
import com.splitwise.microservices.user_service.repository.UserRepository;
import com.splitwise.microservices.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserDetailService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        if (StringUtils.hasLength(emailId)) {
            // Get the user object for the given email
            User user = userRepository.findByEmailId(emailId);
            if (user != null) {
                return new CustomUserDetailService(user);
            } else {
                throw new UsernameNotFoundException("User doesn't exist");
            }
        } else {
            throw new UsernameNotFoundException("Invalid Request");
        }
    }
}
