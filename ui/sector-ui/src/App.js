import React from 'react';
import './App.css';
import {Redirect, Route, Switch} from 'react-router-dom';
import 'react-typist/dist/Typist.css'
import {withRouter} from 'react-router'
import {NotFoundPage} from "./components/NotFoundPage/NotFoundPage";
import Login from "./components/Login/Login";
import {connect} from "react-redux";
import Dossier from "./components/Dossier/Dossier";
import ActorSearch from "./components/ActorSearch/ActorSearch";
import {ThemeProvider, withStyles} from '@material-ui/styles';

import {createMuiTheme} from "@material-ui/core";
import {green, grey} from "@material-ui/core/colors";

const theme = createMuiTheme({
    palette: {
        primary: {500: "rgba(0, 0, 0, .7)"},
        secondary: green,
    },
    status: {
        danger: 'orange',
    },
    MuiDivider: {
        root: {
            marginTop: 1,
        },
    },
});

const useStyles = theme => ({
    root: {
        padding: "2px 4px",
        display: "flex",
        alignItems: "center",
        width: 400

    },
    input: {
        padding: "2px 4px",
        display: "flex",
        marginLeft: 8,
        flex: 1,
        fullWidth: true
    },
    iconButton: {
        padding: 10
    },
    divider: {
        width: '100%',
        maxWidth: '360px',
        backgroundColor: grey,
    },
    sectors: {
        padding: 20,
        elevation: 3
    },
    list: {
        width: '100%',
        maxWidth: 360
    },
    inline: {
        display: 'inline',
    }
});

export class App extends React.Component {
    render() {
        const {location} = this.props;

        // if(!this.props.jwtLoaded) {
        //     return <ThemeProvider theme={theme}><Login/></ThemeProvider>;
        // }

        return (
            <ThemeProvider theme={theme}>
                <Switch location={location}>
                    <Route
                        exact={true}
                        path="/"
                        component={ActorSearch}
                    />
                    <Route
                        path="/login"
                        component={Login}
                    />
                    <Route
                        path="/actor-search"
                        component={ActorSearch}
                    />
                    <Route
                        path="/dossier/:dossierID"
                        component={Dossier}
                    />
                    <Route
                        component={NotFoundPage}
                    />
                </Switch>
            </ThemeProvider>
        );
    }
}

const mapStateToProps = (state) => ({
    jwtLoaded: state.jwtLoaded
});

const mapDispatchToProps = (dispatch) => ({});

export default withStyles(useStyles)(withRouter(connect(mapStateToProps, mapDispatchToProps)(App)));


