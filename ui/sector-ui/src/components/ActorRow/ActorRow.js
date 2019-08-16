import React from 'react';
import Chip from '@material-ui/core/Chip';
import './ActorRow.css';
import Movie from "../Movie/Movie";

export default class ActorSearch extends React.Component {
    render() {
        const {actor} = this.props;

        const processedAliases = actor.aliases.map(alias => {
            return <Chip label={alias} className="alias-chip" key={alias}/>
        });

        const processedMovies = actor.titles.map(movie => <Movie movie={movie}/>);

        return (
            <div className="actor-card" key={actor.id}>
                <div className="actor-name">{actor.name}</div>
                <div className="aliases">{processedAliases}</div>
                <div className="movies">{processedMovies}</div>
            </div>
        );
    }
}
