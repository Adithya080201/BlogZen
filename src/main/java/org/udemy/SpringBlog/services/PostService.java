package org.udemy.SpringBlog.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.udemy.SpringBlog.Models.Post;
import org.udemy.SpringBlog.repositories.PostRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Optional<Post> getById(Long id){
        return postRepository.findById(id);
    }

    public List<Post> findAll(){
        return postRepository.findAll();
    }

    public Page<Post> findAll(int offset, int pageSize, String field){
        return postRepository.findAll(PageRequest.of(offset, pageSize).withSort(Direction.ASC, field));
    }
 

    public void delete(Post post){
        postRepository.delete(post);
    }

    public Post save(Post post){
        if (post.getID() == null){
            post.setCreatedAt(LocalDateTime.now());
        }
        post.setLastUpdated(LocalDateTime.now());
        return postRepository.save(post);
    }
    
}
