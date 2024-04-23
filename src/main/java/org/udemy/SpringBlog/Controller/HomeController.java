package org.udemy.SpringBlog.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.udemy.SpringBlog.Models.Post;
import org.udemy.SpringBlog.services.PostService;

@Controller
public class HomeController {

    @Autowired
    private PostService postService;

    @GetMapping("/")
    public String home(Model model,
            @RequestParam(required = false, name = "sort_by", defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, name = "per_page", defaultValue = "2") String perPage,
            @RequestParam(required = false, name = "page", defaultValue = "1") String page) {

        Page<Post> postsOnPage = postService.findAll(Integer.parseInt(page) - 1, Integer.parseInt(perPage), sortBy);
        int totalPages = postsOnPage.getTotalPages();
        List<Integer> pages = new ArrayList<>();
        
        if (totalPages > 0){
            pages = IntStream.rangeClosed(0, totalPages - 1)
            .boxed().collect(Collectors.toList());
        }

        List<String> links = new ArrayList<>();

        if (pages != null){
            for (int link : pages){
                String active = "";
                if (link == postsOnPage.getNumber()){
                    active = "active";
                }
                String _temp_link = "/?per_page="+perPage+"&page="+(link+1)+"&sort_by="+sortBy;
                links.add("<li class=\"page-item "+active+"\"><a href=\""+_temp_link+"\" class='page-link'>"+(link+1)+"</a></li>");
            }
            model.addAttribute("links", links);
        }

        model.addAttribute("posts", postsOnPage);

        return "home_views/home";
    }
}
