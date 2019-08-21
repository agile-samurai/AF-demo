import React from 'react';
import {TwitterTweetEmbed} from 'react-twitter-embed';
import './Tweets.css';

export default class Tweets extends React.Component {
    render() {
        const {tweets} = this.props;

        if(!tweets) {
            return null;
        }

        const numberOfTweetsToShow = Math.min(tweets.length, 6);

        const processedTweets = tweets
            .slice(0, numberOfTweetsToShow)
            .map(tweet => <TwitterTweetEmbed tweetId={tweet} key={tweet}/>);

        return (
            <div className="tweets">
                {processedTweets}
                <div className="lineage">Lineage: Twitter</div>
            </div>
        );
    }
}
