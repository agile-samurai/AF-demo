import React from 'react';
import axios from "axios/index";
import CircularProgress from '@material-ui/core/CircularProgress';
import './DossierContent.css';
import PerLineageDossierContent from "../PerLineageDossierContent/PerLineageDossierContent";
import DossierNotes from "../DossierNotes/DossierNotes";
import Files from "../Files/Files";
import {Link} from "react-router-dom";
import DeleteIcon from '@material-ui/icons/Delete';
import Fab from '@material-ui/core/Fab';

export default class DossierContent extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            redactionEnabled: false,
            dossierData: null,
            loaded: false,
            deleted: false
        };
        this.DOSSIER_ENDPOINT = '/api/dossier';

        this.handleDelete = this.handleDelete.bind(this);
    }

    componentDidMount() {
        this.loadEncryptedData(this.props.dossierID);
    }

    // TODO use to conditionally allow Dossier deletion
    /*<ShowElementByRole role='ROLE_SUPERVISOR'>*/

    /*</ShowElementByRole>*/

    render() {
        let dossierData;
        if (this.props.dossierID) {
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
            .map((perLineageDossier, index) => <PerLineageDossierContent key={index}
                                                                dossierID={dossierData.id}
                                                                refreshData={this.loadEncryptedData.bind(this)}
                                                                dossierData={perLineageDossier}/>);

        return (
            <div>
                {
                    this.state.deleted ? <div className="dossier-deleted-message">Dossier has been deleted</div> :
                        <div className="dossier-main-section-wrapper">
                            <div className="dossier-main-section">
                                <Link to={`/dossier/${dossierData.id}`} className="navigation-link dossier-name-link">
                                    <div className="image-and-name">
                                        <div>
                                            <img src={dossiers[0].image} height={80}/>
                                        </div>
                                        <div className="dossier-name-wrapper">
                                            <div className="dossier-name">{dossierData.name}</div>
                                        </div>
                                    </div>
                                </Link>
                                <div className="delete-button-wrapper">
                                    <div>
                                        {/*<ShowElementByRole role='ROLE_SUPERVISOR'>*/}
                                        <Fab variant="extended" className="delete-dossier-button" aria-label="delete dossier" onClick={this.handleDelete}>
                                            DELETE DOSSIER
                                            <DeleteIcon/>
                                        </Fab>
                                        {/*</ShowElementByRole>*/}
                                    </div>
                                </div>
                                {perLineageDossierContentList}
                                <DossierNotes dossierID={dossierData.id} notes={dossierData.notes}
                                              refreshData={this.loadEncryptedData.bind(this)}/>
                                <Files dossierID={dossierData.id} files={dossierData.dossierFileInfos}/>
                                <div className="end-of-dossier-indicator"/>
                            </div>
                        </div>
                }
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


    handleDelete() {
        const {dossierID} = this.props;

        axios.delete(`/api/dossier/${dossierID}`
            ,
            {
                auth: {  // TODO remove
                    username: 'business-supervisor',
                    password: 'password'
                }
            })
            .then(() => {
                this.setState({
                    deleted: true
                });
            })
            .catch(error => {
                console.log(error);
            });
    }
}
