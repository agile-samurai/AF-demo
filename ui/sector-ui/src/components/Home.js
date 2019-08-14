import React from 'react';
import { Link } from "react-router-dom";

export default class Home extends React.Component {
    render() {
        return (
            <div>
                Welcome to the home page for this app<br/>
                <Link to={`/dossier/1`}>Go to Dossier page</Link>
                <Link to={`/logout`}>Logout</Link>
            </div>
        );
    }
}
