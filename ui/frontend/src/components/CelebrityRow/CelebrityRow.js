import React from 'react';
import Chip from '@material-ui/core/Chip';
import './CelebrityRow.css';
import Movie from "../Movie/Movie";

export default class CelebrityRow extends React.Component {
    render() {
        const {celebrity} = this.props;

        const processedAliases = celebrity.aliases.map(alias => {
            return <Chip label={alias} className="alias-chip" key={alias}/>
        });

        const processedMovies = celebrity.titles.map(movie => <Movie movie={movie} key={movie.id}/>);

        return (
            <div className="celebrity-card" key={celebrity.id}>
                <div className="celebrity-name">{celebrity.name}</div>
                <div className="aliases">{processedAliases}</div>
                <div className="movies">{processedMovies}</div>
            </div>
        );
    }
}
