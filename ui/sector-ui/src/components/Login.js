import React from 'react';

import 'react-typist/dist/Typist.css'
import {withRouter} from 'react-router'
import axios from 'axios';
import { connect } from 'react-redux'

class Login extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: ''
        };

        this.handleUsernameChange = this.handleUsernameChange.bind(this);
        this.handlePasswordChange = this.handlePasswordChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleUsernameChange(event) {
        this.setState({username: event.target.value});
    }

    handlePasswordChange(event) {
        this.setState({password: event.target.value});
    }

    handleSubmit(event) {
        event.preventDefault();

        axios.get(`/api/processing/stuff`, // TODO add a /login controller endpoint
            {
                auth: {
                    username: this.state.username,
                    password: this.state.password
                }
            })
            .then(data => {
                const jwt = data.headers['x-authentication'];
                this.props.setJWT(jwt);
                this.props.history.push('/');
            });
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit}>
                <label>
                    Username:
                    <input type="text" value={this.state.username} onChange={this.handleUsernameChange} />
                    Password:
                    <input type="text" value={this.state.password} onChange={this.handlePasswordChange} />
                </label>
                <input type="submit" value="Submit" />
            </form>
        );
    }
}

const mapStateToProps = (state) => ({});

const mapDispatchToProps = (dispatch) => ({
    setJWT: (jwt) => dispatch({
        type: 'SET_JWT',
        jwt
    })
});

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Login));