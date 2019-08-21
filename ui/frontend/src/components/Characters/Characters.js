import React from 'react';
import './Characters.css';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';

export default class Characters extends React.Component {
    render() {
        const {characters} = this.props;

        if (characters === null) {
            return null;
        }

        const processedCharacters = characters.map(character => {
            return (
                <Card className="character" key={character.name}>
                    <CardContent>
                        {character.name} as <i>{character.character}</i>
                    </CardContent>
                </Card>
            );
        });

        return (
            <div>
                {
                    processedCharacters.length > 0 && (
                        <div className="characters-container">
                            <div className="character-heading">Characters:</div>
                            <div className="characters">
                                {processedCharacters}
                            </div>
                        </div>
                    )
                }
            </div>
        );
    }
}
