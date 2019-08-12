import React from 'react';
import './App.css';
import {ThemeProvider, withStyles} from '@material-ui/styles';

import {createMuiTheme} from "@material-ui/core";
import {green, grey} from "@material-ui/core/colors";
import 'react-typist/dist/Typist.css'
import {withRouter} from 'react-router'

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

    constructor() {
        super()
    }

    componentDidMount() {
        fetch(`/api/hello`)
            .then(response => response.text())
            .then(data => {
                console.log(data);
                this.setState({score: data});
                this.setState({loaded: true})
            });
    }

    render() {

        const {classes} = this.props;

        const {score} = this.state;

        let gridStyle = {
            height: "100%",
            alignContent: "center",
            borderColor: "black",
            borderWidth: 2,
            paddingTop: 170,
            justifyContent: "center",
            width: "100%"
        };
        let chartOptions = {legend: false};

        return (
            <div>
                Welcome to the application!
                {score}
            </div>
        );
    }
}

export default withRouter(withStyles(useStyles)(App));
