package org.tyaa.demo.springboot.simplespa.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.tyaa.demo.springboot.simplespa.dao.UserHibernateDAO;
import org.tyaa.demo.springboot.simplespa.entity.User;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class HibernateWebAuthProvider implements AuthenticationProvider {

    @Autowired
    private UserHibernateDAO userDAO;

    @Override
    @Transactional
    public Authentication authenticate(Authentication a) throws AuthenticationException {

        String name = a.getName();
        String password = a.getCredentials().toString();
        User user = null;
        try {
            user = userDAO.findUserByName(name);
        } catch (Exception ex) {
            Logger.getLogger(HibernateWebAuthProvider.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (user != null && user.getRole() != null && user.getPassword().equals(password)) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
            return new UsernamePasswordAuthenticationToken(name, password, authorities);
        } else {
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> type) {
        return type.equals(UsernamePasswordAuthenticationToken.class);
    }
}
