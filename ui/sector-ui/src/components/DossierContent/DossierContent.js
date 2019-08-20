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
import Tweets from "../Tweets/Tweets";

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
        if (!this.props.deleted) {
            this.loadEncryptedData(this.props.dossierID);
        }
    }

    render() {
        if (this.state.deleted || this.props.deleted) {
            return <div className="dossier-deleted-message">Dossier has been deleted</div>;
        }

        const dossierData = this.state.dossierData;

        if (!this.state.loaded) {
            return (
                <div className="dossier-loading-container">
                    <CircularProgress className="dossier-loading-image"/>
                    <div className="dossier-loading-text">Decrypting and loading dossier</div>
                </div>
            );
        }

        const {dossiers, tweets} = dossierData;

        const perLineageDossierContentList = dossiers
            .map((perLineageDossier, index) => <PerLineageDossierContent key={index}
                                                                         dossierID={dossierData.id}
                                                                         refreshData={this.loadEncryptedData.bind(this)}
                                                                         dossierData={perLineageDossier}/>);

        return (
            <div>
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
                        <div id={`genreFitChart${dossierData.id}`}/>
                        <div className="delete-button-wrapper">
                            <div>
                                {/*<ShowElementByRole role='ROLE_SUPERVISOR'>*/}
                                <Fab variant="extended" className="delete-dossier-button" aria-label="delete dossier"
                                     onClick={this.handleDelete}>
                                    DELETE DOSSIER
                                    <DeleteIcon/>
                                </Fab>
                                {/*</ShowElementByRole>*/}
                            </div>
                        </div>
                        <Tweets tweets={tweets}/>
                        {perLineageDossierContentList}
                        <DossierNotes dossierID={dossierData.id} notes={dossierData.notes}
                                      refreshData={this.loadEncryptedData.bind(this)}/>
                        <Files dossierID={dossierData.id} files={dossierData.dossierFileInfos}
                               refreshData={this.loadEncryptedData.bind(this)}/>
                        <div className="end-of-dossier-indicator"/>
                    </div>
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
                    }, () => {
                        try{
                            window.Bokeh.embed.embed_item(JSON.parse(this.state.dossierData.distribution),
                                `genreFitChart${this.state.dossierData.id}`);
                        } catch (error) {}
                    });
                })
                .catch(() => {
                    this.setState({
                        deleted: true
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
