package com.blog.controller;

import com.blog.model.Account;
import com.blog.model.Post;
import com.blog.service.AccountService;
import com.blog.service.PostService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
public class PostController {

    private PostService postService;
    private AccountService accountService;

    public PostController(PostService postService, AccountService accountService) {
        this.postService = postService;
        this.accountService = accountService;
    }

    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable Long id, Model model) {

        Optional<Post> optPost = postService.getSinglePost(id);

        if (optPost.isPresent()) {
            Post p = optPost.get();
            model.addAttribute("post", p);
            return "post";
        }

        return "not-found";
    }

//    @GetMapping("/posts/new")
//    public String createPost(Model model) {
//        Optional<Account> optAccount = accountService.findByEmail();
//
//        if (optAccount.isPresent()) {
//            Post post = new Post();
//            post.setAccount(optAccount.get());
//            model.addAttribute("post",post);
//            return "new-post";
//        }
//        return "not-found";
//    }
//
//    @PostMapping("/posts/new")
//    public String savePost(@ModelAttribute Post post) {
//        postService.savePost(post);
//        return "redirect:/posts/" + post.getId();
//    }

    @GetMapping("/posts/new")
    @PreAuthorize("isAuthenticated()")
    public String createNewPost(Model model, Principal principal) {

        String authUsername = "anonymousUser";
        if (principal != null) {
            authUsername = principal.getName();
        }

        Optional<Account> optionalAccount = accountService.findByEmail(authUsername);
        if (optionalAccount.isPresent()) {
            Post post = new Post();
            post.setAccount(optionalAccount.get());
            model.addAttribute("post", post);
            return "new-post";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/posts/new")
    @PreAuthorize("isAuthenticated()")
    public String createNewPost(@ModelAttribute Post post, Principal principal) {
        String authUsername = "user";
        if (principal != null) {
            authUsername = principal.getName();
        }
        if (post.getAccount().getEmail().compareToIgnoreCase(authUsername) < 0) {
            // TODO: some kind of error?
            // our account email on the Post not equal to current logged in account!
        }
        postService.savePost(post);
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/posts/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public String getEditingPost(@PathVariable Long id, Model model) {
        Optional<Post> optPost = postService.getSinglePost(id);

        if (optPost.isPresent()) {
            Post post = optPost.get();
            model.addAttribute("post", post);
            return "edit-post";
        } else {
            return "not-found";
        }
    }

    @PostMapping("/posts/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updatePost(@PathVariable Long id, Post post, BindingResult result, Model model) {

        Optional<Post> optionalPost = postService.getSinglePost(id);
        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();

            existingPost.setTitle(post.getTitle());
            existingPost.setBody(post.getBody());

            postService.savePost(existingPost);
        }

        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/posts/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletePost(@PathVariable Long id) {

        // find post by id
        Optional<Post> optionalPost = postService.getSinglePost(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            postService.delete(post);
            return "redirect:/";
        } else {
            return "404";
        }
    }
}
