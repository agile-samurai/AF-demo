import React from 'react';
import Chip from '@material-ui/core/Chip';
import Switch from '@material-ui/core/Switch';
import DossierPlotSummary from "../DossierPlotSummary/DossierPlotSummary";
import axios from "axios/index";

export default class DossierContent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            redactionEnabled: false,
            encryptedDataLoaded: false,
            dossierData: null,
            loaded: false
        };
        this.DOSSIER_ENDPOINT = '/api/dossier';
        this.handleToggleRedaction = this.handleToggleRedaction.bind(this);
    }

    componentDidMount() {
        const {dossierId} = this.props;

        if (dossierId) {
            this.loadEncryptedData(dossierId);
        }
    }

    // TODO use to conditionally allow Dossier deletion
    /*<ShowElementByRole role='ROLE_SUPERVISOR'>*/
    /*</ShowElementByRole>*/

    render() {
        let dossierData;
        if(this.props.dossierId) {
            dossierData = this.state.dossierData;

            if (!this.state.loaded) {
                return null;
            }
        } else {
            dossierData = this.props.dossierData;
        }

        const {name, genres, summary, entityClassifications} = dossierData;
        const {redactionEnabled} = this.state;

        const processedGenres = genres.map(genreInformation => {
            return <Chip label={genreInformation.genre} className="genre-chip" key={genreInformation.genre}/>
        });

        return (
            <div className="dossier-main-section-wrapper">
                <div className="dossier-main-section">
                    <div className="dossier-name">{name}</div>
                    <div className="genres-and-auto-redaction">
                        <div className="genres">{processedGenres}</div>
                        <div className="auto-redaction-toggle">
                            <div className="auto-redaction-toggle-label">TURN {redactionEnabled ? 'OFF' : 'ON'} AUTO REDACTION</div>
                            <Switch
                                checked={redactionEnabled}
                                onChange={this.handleToggleRedaction}
                                inputProps={{ 'aria-label': 'secondary checkbox' }}/>
                        </div>
                    </div>
                    <DossierPlotSummary summary={summary}
                                        entityClassifications={entityClassifications}
                                        redactionEnabled={redactionEnabled}/>
                </div>
            </div>
        );
    }

    handleToggleRedaction() {
        this.setState({
            redactionEnabled: !this.state.redactionEnabled
        })
    }

    loadEncryptedData(dossierID) {
        axios.get(`${this.DOSSIER_ENDPOINT}/${dossierID}`, {
            auth: {  // TODO remove
                username: 'business-user',
                password: 'password'
            }
        })
        .then(response => {
            console.log(response);
            this.setState({
                dossierData: response.data,
                loaded: true
            });
        });
    }
}
