import React from 'react';
import {TwitterTweetEmbed} from 'react-twitter-embed';

export default class Tweets extends React.Component {
    render() {
        const {tweets} = this.props;

        const processedTweets = tweets && tweets.map(tweet => <TwitterTweetEmbed
            tweetId={tweet}
        />);

        return (
            <div>
                {processedTweets}
            </div>
        );
    }
}
