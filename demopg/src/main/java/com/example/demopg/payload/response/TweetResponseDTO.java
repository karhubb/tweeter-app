package com.example.demopg.payload.response;

import com.example.demopg.models.Tweet;

public class TweetResponseDTO {
  private Long id;
  private String tweet;
  private UserResponseDTO postedBy;

  public TweetResponseDTO(Tweet tweet) {
    this.id = tweet.getId();
    this.tweet = tweet.getTweet();
    if (tweet.getPostedBy() != null) {
      this.postedBy = new UserResponseDTO(tweet.getPostedBy());
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTweet() {
    return tweet;
  }

  public void setTweet(String tweet) {
    this.tweet = tweet;
  }

  public UserResponseDTO getPostedBy() {
    return postedBy;
  }

  public void setPostedBy(UserResponseDTO postedBy) {
    this.postedBy = postedBy;
  }
}