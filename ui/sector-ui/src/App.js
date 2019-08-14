import React from 'react';
import './App.css';
import {Redirect, Route, Switch} from 'react-router-dom';
import 'react-typist/dist/Typist.css'
import {withRouter} from 'react-router'
import {NotFoundPage} from "./components/NotFoundPage";
import Login from "./components/Login";
import Home from "./components/Home";
import Logout from "./components/Logout";
import {connect} from "react-redux";
import Dossier from "./components/Dossier";

export class App extends React.Component {
    render() {
        const {location} = this.props;

        if(!this.props.jwtLoaded) {
            return <Login/>;
        }

        return (
            <Switch location={location}>
                <Route
                    exact={true}
                    path="/"
                    component={Home}
                />
                <Route
                    path="/login"
                    component={Login}
                />
                <Route
                    path="/logout"
                    component={Logout}
                />
                <Route
                    path="/dossier/:dossierID"
                    component={Dossier}
                />
                <Route
                    component={NotFoundPage}
                />
            </Switch>
        );
    }
}

const mapStateToProps = (state) => ({
    jwtLoaded: state.jwtLoaded
});

const mapDispatchToProps = (dispatch) => ({});

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(App));



