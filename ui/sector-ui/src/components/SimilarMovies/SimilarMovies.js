import React from 'react';
import './SimilarMovies.css';
import Movie from "../Movie/Movie";

export default class SimilarMovies extends React.Component {
    render() {
        const {similarMovies} = this.props;

        if(!processedSimilarMovies || processedSimilarMovies.length === 0) {
            return null;
        }

        const processedSimilarMovies = similarMovies
            .map(similarMovie => <Movie movie={similarMovie} key={similarMovie.id}/>);

        return (
            <div>
                <div className="similar-movies-heading">Similar movies</div>
                <div className="similar-movies">{processedSimilarMovies}</div>
            </div>
        );
    }
}
