package org.udemy.SpringBlog.Controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.udemy.SpringBlog.Models.Account;
import org.udemy.SpringBlog.Models.Post;
import org.udemy.SpringBlog.services.AccountService;
import org.udemy.SpringBlog.services.PostService;

import jakarta.validation.Valid;

@Controller
public class PostController {

    @Autowired
    PostService postService;

    @Autowired
    AccountService accountService;

    @GetMapping("/posts/{ID}")
    public String getPost(@PathVariable Long ID, Model model, Principal principal) {
        Optional<Post> optionalPost = postService.getById(ID);
        String authUser = "email";
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            model.addAttribute("post", post);

            if (principal != null) {
                authUser = principal.getName();
            }

            // authUser = SecurityContextHolder.getContext().getAuthentication().getName();

            if (authUser.equals(post.getAccount().getEmail())) {
                model.addAttribute("isOwner", true);
            } else {
                model.addAttribute("isOwner", false);
            }
            return "post_views/post";
        } else {
            return "404";
        }
    }

    @GetMapping("/posts/add")
    @PreAuthorize("isAuthenticated()")
    public String addPost(Model model, Principal principal) {
        String authUser = "email";
        if (principal != null) {
            authUser = principal.getName();
        }

        // authUser = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Account> optionalAccount = accountService.findOneByEmail(authUser);
        if (optionalAccount.isPresent()) {
            Post post = new Post();
            post.setAccount(optionalAccount.get());
            model.addAttribute("post", post);
            return "post_views/post_add";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/posts/add")
    @PreAuthorize("isAuthenticated()")
    public String addPostHandler(@Valid @ModelAttribute Post post, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "post_views/post_add";
        }
        String authUser = "email";

        if (principal != null) {
            authUser = principal.getName();
        }

        // authUser = SecurityContextHolder.getContext().getAuthentication().getName();

        if (post.getAccount().getEmail().compareToIgnoreCase(authUser) < 0) {
            return "redirect:/?error";
        }
        postService.save(post);
        return "redirect:/posts/" + post.getID();
    }

    @GetMapping("/posts/{ID}/edit")
    @PreAuthorize("isAuthenticated()")
    public String editPostHandler(@PathVariable Long ID, Model model) {
        Optional<Post> optionalPost = postService.getById(ID);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            model.addAttribute("post", post);
            return "post_views/post_edit";
        } else {
            return "404";
        }
    }

    @PostMapping("/posts/{ID}/edit")
    @PreAuthorize("isAuthenticated()")
    public String updatePost(@Valid @ModelAttribute Post post, BindingResult bindingResult, @PathVariable Long ID) {
        if (bindingResult.hasErrors()) {
            return "post_views/post_edit";
        }
        Optional<Post> optionalPost = postService.getById(ID);
        if (optionalPost.isPresent()) {
            Post currentPost = optionalPost.get();
            currentPost.setTitle(post.getTitle());
            currentPost.setBody(post.getBody());
            postService.save(currentPost);
        }
        return "redirect:/posts/" + post.getID();
    }

    @GetMapping("/posts/{ID}/delete")
    @PreAuthorize("isAuthenticated()")
    public String deletePost(@PathVariable Long ID) {
        Optional<Post> optionalPost = postService.getById(ID);
        if (optionalPost.isPresent()) {
            Post currentPost = optionalPost.get();
            postService.delete(currentPost);
            return "redirect:/";
        } else {
            return "redirect:/?error";
        }
    }

}
