package com.blog.service;

import com.blog.model.Post;
import com.blog.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Optional<Post> getSinglePost(Long id) {
        return postRepository.findById(id);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post savePost(Post post) {
        if (post.getId() == null) {
            LocalDateTime date = LocalDateTime.now();
            String pattern = "dd.MM.yyyy";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            String formattedDateTime = date.format(formatter);
            post.setCreationDate(formattedDateTime);
        }
        return postRepository.save(post);
    }

    public void delete(Post post) {
        postRepository.delete(post);
    }
}
