import React from 'react';
import './Characters.css';

export default class Characters extends React.Component {
    render() {
        const {characters} = this.props;

        const processedCharacters = characters.map(character => {
            return (
                <div className="character">
                    <div><img src={character.photo_img}/></div>
                    <div>Character: {character.character}</div>
                    <div>Played by: {character.name}</div>
                </div>
            );
        });

        return (
            <div className="characters">
                <div className="character-heading">Characters:</div>
                {processedCharacters}
            </div>
        );
    }
}
