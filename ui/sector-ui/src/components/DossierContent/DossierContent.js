import React from 'react';
import axios from "axios/index";
import CircularProgress from '@material-ui/core/CircularProgress';
import './DossierContent.css';
import PerLineageDossierContent from "../PerLineageDossierContent/PerLineageDossierContent";
import DossierNotes from "../DossierNotes/DossierNotes";
import {Link} from "react-router-dom";

export default class DossierContent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            redactionEnabled: false,
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

        const perLineageDossierContentList = dossiers
            .map(perLineageDossier => <PerLineageDossierContent key={perLineageDossier.id}
                                                                dossierData={perLineageDossier}/>);

        return (
            <div className="dossier-main-section-wrapper">
                <div className="dossier-main-section">
                    <Link to={`/dossier/${dossierData.id}`} className="navigation-link dossier-name-link">
                        <div className="image-and-name">
                            <div>
                                <img src={dossiers[0].image} height={80}/>
                            </div>
                            <div className="dossier-name-wrapper">
                                <div className="dossier-name">{dossiers[0].name}</div>
                            </div>
                        </div>
                    </Link>
                    {perLineageDossierContentList}
                    <DossierNotes dossierID={dossierData.id} notes={dossierData.notes}
                                  refreshData={this.loadEncryptedData.bind(this)}/>
                </div>
            </div>
        );
    }

    loadEncryptedData(dossierID) {
        this.setState({
            loaded: false
        }, () => {
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
        });
    }
}
