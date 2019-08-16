import React from 'react';
import './Dossier.css';
import axios from "axios/index";
import {withRouter} from 'react-router'
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import IconButton from '@material-ui/core/IconButton';
import NavigationMenu from "../NavigationMenu/NavigationMenu";
import {Link} from "react-router-dom";
import DossierContent from "../DossierContent/DossierContent";

class Dossier extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            dossier: {
                name: '',
                summary: '',
                genres: [],
                redactionEnabled: false,
                loaded: false
            }
        };
        this.DOSSIER_ENDPOINT = '/api/dossier';
    }

    componentDidMount() {
        if(!this.props.dossierData) {
            this.getDossierDetails(this.props.match.params.dossierID);
        }
    }

    render() {
        const {dossier} = this.state;
        const {loaded, redactionEnabled} = this.state;

        if(!loaded && !this.props.dossierData) {
            return <div>Loading...</div>;
        }
        return (
            <div className="dossier-page">
                <AppBar position="static">
                    <Toolbar variant="dense" className="search-toolbar">
                        <div className="star-power-text">
                            <Link to="/" className="navigation-link">
                                <span className="star-text">star</span>pwr
                            </Link>
                        </div>
                        <IconButton edge="end" color="inherit" aria-label="menu">
                            <NavigationMenu/>
                        </IconButton>
                    </Toolbar>
                </AppBar>
                <DossierContent dossierData={dossier}
                                redactionEnabled={redactionEnabled}/>
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