import React from 'react';

import 'react-typist/dist/Typist.css'
import { connect } from 'react-redux'

class ShowElementByRole extends React.Component {
    render() {
        if(this.doesJWTHaveRole('ROLE_SUPERVISOR')) {
            return this.props.children;
        }

        return null;
    }

    doesJWTHaveRole(roleName) {
        const tokenPayload = JSON.parse(atob(this.props.jwt.split('.')[1]));
        return tokenPayload.roles === roleName;
    }
}

const mapStateToProps = (state) => ({
    jwt: state.jwt
});

const mapDispatchToProps = (dispatch) => ({});

export default connect(mapStateToProps, mapDispatchToProps)(ShowElementByRole);
