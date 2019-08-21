import React from 'react';

import 'react-typist/dist/Typist.css'
import {withRouter} from 'react-router'
import axios from 'axios';
import {connect} from 'react-redux';
import './Login.css';
import HeaderBar from "../HeaderBar/HeaderBar";
import TextField from '@material-ui/core/TextField';

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

        axios.get(`/api/login`,
            {
                auth: { // TODO remove
                    username: this.state.username,
                    password: this.state.password
                }
            })
            .then(data => {
                const jwt = data.headers['x-authentication'];
                axios.defaults.headers['x-authentication'] = jwt;
                this.props.setJWT(jwt);
                this.props.history.push('/');
            });
    }

    render() {
        return (
            <div className="login">
                <HeaderBar/>
                <div className="login-box-wrapper">
                    <div className="login-box">
                        <div className="login-heading">Login</div>
                        <form onSubmit={this.handleSubmit}>
                            <div className="username-field">
                                <TextField label="username"
                                           margin="dense"
                                           variant="outlined"
                                           type="text"
                                           className="login-form-field"
                                           value={this.state.username}
                                           onChange={this.handleUsernameChange}/>
                            </div>
                            <div className="password-field">
                                <TextField label="username"
                                           margin="dense"
                                           variant="outlined"
                                           type="password"
                                           className="login-form-field"
                                           value={this.state.password}
                                           onChange={this.handlePasswordChange}/>
                            </div>
                            <div className="submit-button-wrapper">
                                <div>
                                    <input type="submit" value="LOGIN" className="submit-button"/>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
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