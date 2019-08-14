import React from 'react';
import {connect} from "react-redux";
import { Link } from "react-router-dom";
import axios from "axios/index";

class Logout extends React.Component {
    componentDidMount() {
        axios.defaults.headers.common['x-authentication'] = null;
        this.props.unSetJWT();
    }

    render() {
        return (
            <div>
                You have been logged out<br/>
                <Link to={`/login`}>Log in</Link>
            </div>
        );
    }
}

const mapStateToProps = (state) => ({});

const mapDispatchToProps = (dispatch) => ({
    unSetJWT: () => dispatch({
        type: 'UN_SET_JWT'
    })
});

export default connect(mapStateToProps, mapDispatchToProps)(Logout);
