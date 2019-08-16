import React from 'react';
import './Dossier.css';
import axios from "axios/index";
import {withRouter} from 'react-router'
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import IconButton from '@material-ui/core/IconButton';
import NavigationMenu from "../NavigationMenu/NavigationMenu";
import Chip from '@material-ui/core/Chip';
import DossierPlotSummary from "../DossierPlotSummary/DossierPlotSummary";

class Dossier extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            dossier: {
                name: '',
                summary: '',
                genres: [],
                loaded: false
            }
        };
        this.DOSSIER_ENDPOINT = '/api/dossier';
    }

    componentDidMount() {
        this.getDossierDetails(this.props.match.params.dossierID);
    }

    // TODO use to conditionally allow Dossier deletion
    /*<ShowElementByRole role='ROLE_SUPERVISOR'>*/
    /*</ShowElementByRole>*/

    render() {
        const {name, summary, genres, entityClassifications} = this.state.dossier;

        if(!this.state.loaded) {
            return <div>Loading...</div>;
        }

        const processedGenres = genres.map(genreInformation => {
            return <Chip label={genreInformation.genre} className="genre-chip" key={genreInformation.genre}/>
        });

        return (
            <div className="dossier-page">
                <AppBar position="static">
                    <Toolbar variant="dense" className="search-toolbar">
                        <div className="star-power-text">
                            <span className="star-text">star</span>pwr
                        </div>
                        <IconButton edge="end" color="inherit" aria-label="menu">
                            <NavigationMenu/>
                        </IconButton>
                    </Toolbar>
                </AppBar>
                <div className="dossier-main-section-wrapper">
                    <div className="dossier-main-section">
                        <div className="dossier-name">{name}</div>
                        <div className="genres">{processedGenres}</div>
                        <DossierPlotSummary summary={summary} entityClassifications={entityClassifications}/>
                    </div>
                </div>
            </div>
        );
    }

    getDossierDetails(dossierID) {
        axios.get(`${this.DOSSIER_ENDPOINT}/${dossierID}`, {
            auth: {  // TODO remove
                username: 'business-user',
                password: 'password'
            }
        })
        .then(response => {
            console.log(response);
            this.setState({
                dossier: response.data,
                loaded: true
            });
        });
    }
}

export default withRouter(Dossier);