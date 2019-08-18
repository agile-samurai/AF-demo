import React from 'react';
import Chip from '@material-ui/core/Chip';
import Switch from '@material-ui/core/Switch';
import DossierPlotSummary from "../DossierPlotSummary/DossierPlotSummary";
import './PerLineageDossierContent.css';

export default class DossierContent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            redactionEnabled: false,
            dossierData: null
        };
        this.handleToggleRedaction = this.handleToggleRedaction.bind(this);
    }

    // TODO use to conditionally allow Dossier deletion
    /*<ShowElementByRole role='ROLE_SUPERVISOR'>*/
    /*</ShowElementByRole>*/

    render() {
        const {lineage, genres, summary, entityClassifications} = this.props.dossierData;
        const {redactionEnabled} = this.state;

        const processedGenres = genres.map(genreInformation => {
            return <Chip label={genreInformation.genre} className="genre-chip" key={genreInformation.genre}/>
        });

        return (
            <div className="per-lineage-dossier-content">
                <div className="lineage">Lineage: {lineage}</div>
                <div className="genres-and-auto-redaction">
                    <div className="genres">{processedGenres}</div>
                    <div className="auto-redaction-toggle">
                        <div className="auto-redaction-toggle-label">TURN {redactionEnabled ? 'OFF' : 'ON'} AUTO
                            REDACTION
                        </div>
                        <Switch
                            checked={redactionEnabled}
                            onChange={this.handleToggleRedaction}
                            inputProps={{'aria-label': 'secondary checkbox'}}/>
                    </div>
                </div>
                <DossierPlotSummary summary={summary}
                                    entityClassifications={entityClassifications}
                                    redactionEnabled={redactionEnabled}/>
            </div>
        );
    }

    handleToggleRedaction() {
        this.setState({
            redactionEnabled: !this.state.redactionEnabled
        })
    }
}
