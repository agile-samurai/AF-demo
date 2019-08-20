import React from 'react';
import './Dossier.css';
import {withRouter} from 'react-router'
import DossierContent from "../DossierContent/DossierContent";
import HeaderBar from "../HeaderBar/HeaderBar";

class Dossier extends React.Component {
    render() {
        return (
            <div className="dossier-page">
                <HeaderBar/>
                <DossierContent dossierID={this.props.match.params.dossierID}/>
            </div>
        );
    }
}

export default withRouter(Dossier);