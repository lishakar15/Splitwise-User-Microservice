package com.splitwise.microservices.user_service.configuration;

import com.splitwise.microservices.user_service.entity.Users;
import com.splitwise.microservices.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserDetailService implements UserDetailsService {
    @Autowired
    UserService userService;
    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        UserDetails usersDetails;
        if(StringUtils.hasLength(emailId))
        {
            //Get the users object for the given name
            Users users = userService.getUserDetailsByEmailId(emailId);
            if (users != null) {
                usersDetails = User.withUsername(users.getEmailId())
                        .password(users.getPassword())
                        .build();
            } else {
                throw new UsernameNotFoundException("User doesn't exist");
            }
        }
        else
        {
            throw new UsernameNotFoundException("Invalid Request");
        }

        return usersDetails;
    }
}
