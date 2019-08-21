import React from 'react';
import './Movie.css';
import {withRouter} from "react-router";
import Tooltip from '@material-ui/core/Tooltip';

class Movie extends React.Component {
    constructor(props) {
        super(props);

        this.navigateToDossier = this.navigateToDossier.bind(this);
    }

    render() {
        const { movie } = this.props;

        const movieCardContent = (
            <div className="movie-card"
                 onClick={this.navigateToDossier}
                 key={movie.id}>
                <div className="image-wrapper">
                    <img src={movie.image} height={174}/>
                </div>
                <div className="movie-title-wrapper">
                    <div className="movie-title">
                        {movie.name}
                    </div>
                </div>
            </div>
        );

        const processedCard = movie.dossierAvailable ?
            movieCardContent
            :
            (
            <Tooltip title="The dossier for this movie has been deleted" className="dossier-not-available-outer-wrapper">
                <div>
                    <div className="dossier-not-available-inner-wrapper"/>
                    {movieCardContent}
                </div>
            </Tooltip>
        );

        return (
            <div>
                {processedCard}
            </div>
        );
    }

    navigateToDossier() {
        const { movie, history } = this.props;

        if (movie.dossierAvailable) {
            history.push(`/dossier/${movie.id}`);
        }
    }
}

export default withRouter(Movie);