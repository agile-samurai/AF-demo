import React from 'react';
import './Movie.css';
import {Link} from "react-router-dom";

export default class Movie extends React.Component {
    render() {
        const { movie } = this.props;

        return (
            <Link to={`/dossier/${movie.id}`} key={movie.id} className="navigation-link">
                <div className="movie-card">
                    <div className="image-wrapper">
                        <img src={movie.image} height={174}/>
                    </div>
                    <div className="movie-title-wrapper">
                        <div className="movie-title">
                            {movie.name}
                        </div>
                    </div>
                </div>
            </Link>
        );
    }
}
