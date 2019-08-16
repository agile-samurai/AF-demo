import React from 'react';
import './Dossier.css';
import axios from "axios/index";
import {withRouter} from 'react-router'

class Dossier extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
        };
        this.ACTORS_ENDPOINT = '/api/dossier';
    }

    componentDidMount() {
        this.getDossierDetails(this.props.match.params.dossierID);
    }

    render() {
        return (
            <div>
                Dossier page<br/><br/>
                Add<br/>
                {/*<ShowElementByRole role='ROLE_SUPERVISOR'>*/}
                    Remove
                {/*</ShowElementByRole>*/}
            </div>
        );
    }

    getDossierDetails(dossierID) {
        console.log('Dossier ID is: ', dossierID);
        axios.get(`${this.ACTORS_ENDPOINT}/${dossierID}`, {
            params: {
                cursor: 0
            },
            auth: {  // TODO remove
                username: 'business-user',
                password: 'password'
            }
        })
        .then(response => {
            this.setState({
                actorSearchResults: response.data.content
            });
        });
    }
}

export default withRouter(Dossier);