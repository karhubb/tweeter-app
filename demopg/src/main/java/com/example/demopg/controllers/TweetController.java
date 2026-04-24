package com.example.demopg.controllers;

import java.util.Optional;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demopg.models.Tweet;
import com.example.demopg.models.User;
import com.example.demopg.payload.response.TweetResponseDTO;
import com.example.demopg.repository.UserRepository;
import com.example.demopg.repository.TweetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tweets")

public class TweetController {

    @Autowired
    private TweetRepository tweetRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/all")
    @Transactional(readOnly = true)
    public Page<TweetResponseDTO> getTweet(Pageable pageable) {
        Page<Tweet> tweets = tweetRepository.findAll(pageable);
        return tweets.map(TweetResponseDTO::new);
    }

    @GetMapping
    @Transactional(readOnly = true)
    public Page<TweetResponseDTO> getTweetForAuthenticatedUsers(Pageable pageable) {
      Page<Tweet> tweets = tweetRepository.findAll(pageable);
      return tweets.map(TweetResponseDTO::new);
    }
  
  @PostMapping("/create")
  public TweetResponseDTO createTweet(@Valid @RequestBody Tweet tweet) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String userId = authentication.getName();


        User user = getValidUser(userId);
        Tweet myTweet = new Tweet(tweet.getTweet());
        myTweet.setPostedBy(user);
        tweetRepository.save(myTweet);

          return new TweetResponseDTO(myTweet);
  }

        @DeleteMapping("/{id}")
        @Transactional
        public ResponseEntity<Void> deleteTweet(@PathVariable Long id) {
          Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          String username = authentication.getName();

          Optional<Tweet> tweetOpt = tweetRepository.findById(id);
          if (!tweetOpt.isPresent()) {
            return ResponseEntity.notFound().build();
          }

          Tweet tweet = tweetOpt.get();
          if (tweet.getPostedBy() == null || !username.equals(tweet.getPostedBy().getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
          }

          tweetRepository.delete(tweet);
          return ResponseEntity.noContent().build();
        }

    private User getValidUser(String userId) {
        Optional<User> userOpt = userRepository.findByUsername(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }
        return userOpt.get();
    }
}