package org.udemy.SpringBlog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.udemy.SpringBlog.Models.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    
} 
