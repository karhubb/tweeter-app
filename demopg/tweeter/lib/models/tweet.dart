class Tweet {
  final int id;
  final String tweet;
  final String? postedByUsername;

  Tweet({
    required this.id,
    required this.tweet,
    this.postedByUsername,
  });

  /// Convert JSON to Tweet object
  factory Tweet.fromJson(Map<String, dynamic> json) {
    final id = json['id'];
    final tweetContent = json['tweet'];
    final postedBy = json['postedBy'];
    String? postedByUsername;

    if (postedBy is Map) {
      final username = postedBy['username'];
      if (username != null) {
        postedByUsername = username.toString();
      }
    }
    
    return Tweet(
      id: id is int ? id : (id is String ? int.tryParse(id) ?? 0 : 0),
      tweet: tweetContent is String ? tweetContent : tweetContent?.toString() ?? '',
      postedByUsername: postedByUsername,
    );
  }

  /// Convert Tweet object to JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'tweet': tweet,
      'postedBy': postedByUsername == null ? null : {'username': postedByUsername},
    };
  }

  @override
  String toString() => 'Tweet(id: $id, tweet: $tweet, postedByUsername: $postedByUsername)';
}