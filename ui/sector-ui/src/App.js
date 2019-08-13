import React from 'react';
import './App.css';
import {withStyles} from '@material-ui/styles';
import {Redirect, Route, Switch} from 'react-router-dom';

import {createMuiTheme} from "@material-ui/core";
import {green, grey} from "@material-ui/core/colors";
import 'react-typist/dist/Typist.css'
import {withRouter} from 'react-router'
import axios from 'axios';
import {NotFoundPage} from "./components/NotFoundPage";
import {Login} from "./components/Login";
import {Home} from "./components/Home";

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
    state = {
        score: 0,
        loaded: false
    };

    render() {
        const {location} = this.props;

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
                    component={NotFoundPage}
                />
            </Switch>
        );
    }
}

export default withRouter(withStyles(useStyles)(App));
