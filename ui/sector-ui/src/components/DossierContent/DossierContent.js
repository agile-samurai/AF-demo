import React from 'react';
import axios from "axios/index";
import CircularProgress from '@material-ui/core/CircularProgress';
import './DossierContent.css';
import PerLineageDossierContent from "../PerLineageDossierContent/PerLineageDossierContent";
import DossierNotes from "../DossierNotes/DossierNotes";

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
    }

    componentDidMount() {
        this.loadEncryptedData(this.props.dossierId);
    }

    // TODO use to conditionally allow Dossier deletion
    /*<ShowElementByRole role='ROLE_SUPERVISOR'>*/
    /*</ShowElementByRole>*/

    render() {
        let dossierData;
        if (this.props.dossierId) {
            dossierData = this.state.dossierData;

            if (!this.state.loaded) {
                return (
                    <div className="dossier-loading-container">
                        <CircularProgress className="dossier-loading-image"/>
                        <div className="dossier-loading-text">Decrypting and loading dossier</div>
                    </div>
                );
            }
        }

        const {dossiers} = dossierData;

        const perLineageDossierContentList = dossiers.map(perLineageDossier => <PerLineageDossierContent
            dossierData={perLineageDossier}/>);

        return (
            <div className="dossier-main-section-wrapper">
                <div className="dossier-main-section">
                    <div className="dossier-name">{dossiers[0].name}</div>
                    {perLineageDossierContentList}
                    <DossierNotes/>
                </div>
            </div>
        );
    }

    loadEncryptedData(dossierID) {
        axios.get(`${this.DOSSIER_ENDPOINT}/${dossierID}`, {
            auth: {  // TODO remove
                username: 'business-user',
                password: 'password'
            }
        })
            .then(response => {
                this.setState({
                    dossierData: response.data,
                    loaded: true
                });
            });
    }
}
