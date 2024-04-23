package org.udemy.SpringBlog.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.udemy.SpringBlog.services.PostService;

@RestController
@RequestMapping("/api/v1")
public class HomeRestController {

    @Autowired
    private PostService postService;
    
    // Google spring boot logging to check more about logs
    Logger logger = LoggerFactory.getLogger(HomeRestController.class);

    @GetMapping("/")
    public String home() {
        logger.debug("This is a test error log");
        return "sample response";
    }
}
