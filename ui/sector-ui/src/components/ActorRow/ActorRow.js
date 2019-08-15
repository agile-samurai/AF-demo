import React from 'react';
import Chip from '@material-ui/core/Chip';
import './ActorRow.css';

export default class ActorSearch extends React.Component {
    render() {
        const { actor } = this.props;

        const processedAliases = actor.aliases.map(alias => {
            return <Chip label={alias} className="alias-chip" key={alias}/>
        });

        const processedMovies = actor.titles.map(movie => { // TODO move into a separate component
            return (
                <div className="movie-card" key={movie.id}>
                    <div><img src={movie.image} height={174} /></div>
                    <div className="movie-title">{movie.name}</div>
                </div>
            )
        });

        return (
            <div className="actor-card" key={actor.id}>
                <div className="actor-name">{actor.name}</div>
                <div className="aliases">{processedAliases}</div>
                <div className="movies">{processedMovies}</div>
            </div>
        );
    }
}
