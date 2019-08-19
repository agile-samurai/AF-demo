import React from 'react';
import './Dossier.css';
import {withRouter} from 'react-router'
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import IconButton from '@material-ui/core/IconButton';
import NavigationMenu from "../NavigationMenu/NavigationMenu";
import {Link} from "react-router-dom";
import DossierContent from "../DossierContent/DossierContent";

class Dossier extends React.Component {
    render() {
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
                <DossierContent dossierID={this.props.match.params.dossierID}/>
            </div>
        );
    }
}

export default withRouter(Dossier);