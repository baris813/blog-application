package com.blog.config;

import com.blog.model.Account;
import com.blog.model.Authority;
import com.blog.model.Post;
import com.blog.repository.AuthorityRepository;
import com.blog.service.AccountService;
import com.blog.service.PostService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class TestData implements CommandLineRunner {

    private PostService postService;

    private AccountService accountService;

    private AuthorityRepository authorityRepository;

    public TestData(PostService postService, AccountService accountService, AuthorityRepository authorityRepository) {
        this.postService = postService;
        this.accountService = accountService;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Post> posts = postService.getAllPosts();

        if (posts.size() == 0) {

            Authority user = new Authority();
            user.setName("USER");
            authorityRepository.save(user);

            Authority admin = new Authority();
            admin.setName("ADMIN");
            authorityRepository.save(admin);

            Account a1 = new Account();
            Account a2 = new Account();

            a1.setFirstName("burcak");
            a1.setLastName("sev");
            a1.setEmail("bursev123@gmail.com");
            a1.setPassword("111222");

            Set<Authority> aut1 = new HashSet<>();
            authorityRepository.findById("ADMIN").ifPresent(aut1::add);
            a1.setAuthorities(aut1);

            a2.setFirstName("baris");
            a2.setLastName("boz");
            a2.setEmail("bb@gmail.com");
            a2.setPassword("3333");
            Set<Authority> aut2 = new HashSet<>();
            authorityRepository.findById("USER").ifPresent(aut2::add);
            a2.setAuthorities(aut2);

            accountService.save(a1);
            accountService.save(a2);

            Post p1 = new Post();
            p1.setTitle("The Dire Defect of ‘Multilingual’ AI Content Moderation");
            p1.setBody("This is part of the data recipe for Facebook’s new large language model, which the company claims is able to detect and rein in harmful content in over 100 languages. Bumble uses similar technology to detect rude and unwanted messages in at least 15 languages. Google uses it for everything from translation to filtering newspaper comment sections. All have comparable recipes and the same dominant ingredient: English-language data.");
            p1.setAccount(a1);

            Post p2 = new Post();
            p2.setTitle("Does AI Have a Subconscious?");
            p2.setBody("Sometime in the early 2000s, I came across an essay in which the author argued that no artificial consciousness will ever be believably human unless it can dream. I cannot remember who wrote it or where it was published, though I vividly recall where I was when I read it (the periodicals section of Barbara’s Bookstore, Halsted Street, Chicago) and the general feel of that day (twilight, early spring).");
            p2.setAccount(a2);

            postService.savePost(p1);
            postService.savePost(p2);
        }
    }
}
