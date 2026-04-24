package com.example.demopg.controllers;

import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demopg.models.Tweet;
import com.example.demopg.models.TweetReaction;
import com.example.demopg.models.User;
import com.example.demopg.repository.TweetReactionRepository;
import com.example.demopg.repository.TweetRepository;
import com.example.demopg.models.Reaction;
import com.example.demopg.payload.request.TweetReactionRequest;

import com.example.demopg.repository.UserRepository;
import com.example.demopg.repository.ReactionRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reactions")

public class TweetReactionController {

    @Autowired
    private TweetReactionRepository tweetReactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TweetRepository tweetRepository;
    @Autowired
    private ReactionRepository reactionRepository;


  @GetMapping("/all")
    public Page<TweetReaction> getTweet(Pageable pageable) {
        return tweetReactionRepository.findAll(pageable);
    }
  
  @PostMapping("/create")
  public TweetReaction createReaction(@Valid @RequestBody TweetReactionRequest tweetReaction) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();


        User user = getValidUser(userId);

        TweetReaction myTweetReaction = new TweetReaction();
        Tweet myTweet = getValidTweet(tweetReaction.getTweetId());


        Reaction myReaction = getValidReaction(tweetReaction.getReactionId());

        //myTweetReaction.setUserId(user.getId());
        //myTweetReaction.setTweetId(myTweet.getId());
        //myTweetReaction.setReactionId(myReaction.getId());
        
        myTweetReaction.setUser(user);
        myTweetReaction.setTweet(myTweet);
        myTweetReaction.setReaction(myReaction);


        tweetReactionRepository.save(myTweetReaction);

        return myTweetReaction;
  }

    private User getValidUser(String userId) {
        Optional<User> userOpt = userRepository.findByUsername(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }
        return userOpt.get();
    }

    private Tweet getValidTweet(Long tweetId) {
        Optional<Tweet> tweetOpt = tweetRepository.findById(tweetId);
        if (!tweetOpt.isPresent()) {
            throw new RuntimeException("Tweet not found");
        }
        return tweetOpt.get();
    }

    private Reaction getValidReaction(Long reactionId) {
        Optional<Reaction> reactionOpt = reactionRepository.findById(reactionId);
        if (!reactionOpt.isPresent()) {
            throw new RuntimeException("Reaction not found");
        }
        return reactionOpt.get();
    }

}