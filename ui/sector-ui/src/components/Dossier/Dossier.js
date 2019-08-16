import React from 'react';
import './Dossier.css';
import axios from "axios/index";
import {withRouter} from 'react-router'
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import IconButton from '@material-ui/core/IconButton';
import NavigationMenu from "../NavigationMenu/NavigationMenu";

class Dossier extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            dossier: {}
        };
        this.ACTORS_ENDPOINT = '/api/dossier';
    }

    componentDidMount() {
        this.getDossierDetails(this.props.match.params.dossierID);
    }

    // TODO use to conditionally allow Dossier deletion
    /*<ShowElementByRole role='ROLE_SUPERVISOR'>*/
    /*</ShowElementByRole>*/

    render() {
        const {name} = this.state.dossier;

        return (
            <div>
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
                <div className="main-section">
                    {name}
                </div>
            </div>
        );
    }

    getDossierDetails(dossierID) {
        axios.get(`${this.ACTORS_ENDPOINT}/${dossierID}`, {
            auth: {  // TODO remove
                username: 'business-user',
                password: 'password'
            }
        })
        .then(response => {
            console.log(response);
            this.setState({
                dossier: response.data
            });
        });
    }
}

export default withRouter(Dossier);