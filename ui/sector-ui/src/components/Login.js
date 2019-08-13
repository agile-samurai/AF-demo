import React from 'react';

import 'react-typist/dist/Typist.css'
import {withRouter} from 'react-router'
import axios from 'axios';

export class Login extends React.Component {

    constructor(props) {
        super(props);

        // this.requestUseRejected = this.requestUseRejected.bind(this);
        // this.responseUseRejected = this.responseUseRejected.bind(this);

        axios.defaults.validateStatus = this.validateState;

        axios.defaults.auth = {
            username: '',
            password: ''
        };

        // axios.interceptors.request.use(
        //     this.requestUseFulfilled,
        //     this.requestUseRejected
        // );
        //
        // axios.interceptors.response.use(
        //     this.responseUseFulfilled,
        //     this.responseUseRejected
        // );
    }

    validateState(status) {
        return (status >= 200 && status < 400);
    }

    // requestUseFulfilled(config) {
    //     return CookieInformationHelper.getAccessToken().then((accessToken) => {
    //         if (accessToken != null) {
    //             config.headers.Authorization = 'Bearer ' + accessToken;
    //         }
    //         config.headers.Accept = (!!config.headers.Accept ? `${config.headers.Accept}, ` : '') + 'application/json';
    //         config.headers.ContentType = 'application/json;charset=UTF-8';
    //         return config;
    //     });
    // }
    //
    // requestUseRejected(error) {
    //     const errorMessage = error.response.data.message || 'An error occurred.';
    //     this.props.openGlobalModal(GlobalModalType.INFO_MODAL, '', errorMessage);
    //     return Promise.reject(error);
    // }
    //
    // responseUseFulfilled(config) {
    //     return Promise.resolve(config);
    // }
    //
    // responseUseRejected(error) {
    //     const errorMessage = error.response.data.message || 'An error occurred.';
    //     this.props.openGlobalModal(GlobalModalType.INFO_MODAL, '', errorMessage);
    //     return Promise.reject(error.response);
    // }

    render() {
        return (
            <div>
                Login page
                {/*{score}*/}
            </div>
        );
    }
}

export default withRouter(Login);
