package com.therealazimbek.spring.eventmasterapp.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetailsService {

    UserDetails loadByUsername(String username) throws UsernameNotFoundException;
}
