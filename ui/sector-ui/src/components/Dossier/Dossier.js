import React from 'react';
import {withRouter} from "react-router";
import ShowElementByRole from "../ShowElementByRole/ShowElementByRole";

class Home extends React.Component {
    render() {
        console.log(this.props);
        return (
            <div>
                Dossier page<br/><br/>
                Add<br/>
                <ShowElementByRole role='ROLE_SUPERVISOR'>
                    Remove
                </ShowElementByRole>
            </div>
        );
    }
}

export default withRouter(Home);