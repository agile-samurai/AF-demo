import React from 'react';
import './App.css';
import {Route, Switch} from 'react-router-dom';
import 'react-typist/dist/Typist.css'
import {withRouter} from 'react-router'
import {connect} from "react-redux";
import {ThemeProvider, withStyles} from '@material-ui/styles';
import {createMuiTheme} from "@material-ui/core";
import {green, grey} from "@material-ui/core/colors";
import Login from "./components/Login/Login";
import Dossier from "./components/Dossier/Dossier";
import NotFoundPage from "./components/NotFoundPage/NotFoundPage";
import CelebritySearch from "./components/CelebritySearch/CelebritySearch";
import TrainingData from "./components/TrainingData/TrainingData";
import DossierList from "./components/DossierList/DossierList";

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
                        component={DossierList}
                    />
                    <Route
                        path="/login"
                        component={Login}
                    />
                    <Route
                        path="/celebrity-search"
                        component={CelebritySearch}
                    />
                    <Route
                        path="/dossier-list-and-search"
                        component={DossierList}
                    />
                    <Route
                        path="/dossier/:dossierID"
                        component={Dossier}
                    />
                    <Route
                        path="/training-data"
                        component={TrainingData}
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


