package org.udemy.SpringBlog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.udemy.SpringBlog.Models.Authority;
import org.udemy.SpringBlog.repositories.AuthorityRepository;

@Service
public class AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    public Authority save(Authority authority){
        return authorityRepository.save(authority);
    }

    public Optional<Authority> findByID(Long id){
        return authorityRepository.findById(id);
    }
    
}
