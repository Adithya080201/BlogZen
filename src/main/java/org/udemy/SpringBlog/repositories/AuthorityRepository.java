package org.udemy.SpringBlog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.udemy.SpringBlog.Models.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long>{

    
} 