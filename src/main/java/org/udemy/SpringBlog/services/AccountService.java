package org.udemy.SpringBlog.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.udemy.SpringBlog.Models.Account;
import org.udemy.SpringBlog.Models.Authority;
import org.udemy.SpringBlog.repositories.AccountRepository;
import org.udemy.SpringBlog.util.constants.Roles;

@Service
public class AccountService implements UserDetailsService {

    @Value("${spring.mvc.static-path-pattern}")
    private String imagePrefixPath;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Account save(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        if (account.getRole() == null) {
            account.setRole(Roles.USER.getRole());
        }
        if (account.getPhoto() == null) {
            String imagePath = imagePrefixPath.replace("**", "images/person.jpg");
            account.setPhoto(imagePath);
        }

        return accountRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> optionalAccount = accountRepository.findOneByEmailIgnoreCase(email);

        if (!optionalAccount.isPresent()) {
            throw new UsernameNotFoundException("account not found");
        }

        Account account = optionalAccount.get();

        List<GrantedAuthority> grantedAuthority = new ArrayList<>();
        grantedAuthority.add(new SimpleGrantedAuthority(account.getRole()));

        for (Authority _auth : account.getAuthorities()) {
            grantedAuthority.add(new SimpleGrantedAuthority(_auth.getName()));
        }

        return new User(account.getEmail(), account.getPassword(), grantedAuthority);
    }

    public Optional<Account> findOneByEmail(String email) {
        return accountRepository.findOneByEmailIgnoreCase(email);
    }

    public Optional<Account> findOneByID(Long ID) {
        return accountRepository.findById(ID);
    }

    public Optional<Account> findByToken(String token){
        return accountRepository.findByToken(token);
    }
}
