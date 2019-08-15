import React from 'react';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import TextField from '@material-ui/core/TextField';
import './ActorSearch.css';
import {Icon, InputAdornment} from '@material-ui/core';
import SearchIcon from '@material-ui/icons/Search';
import axios from "axios/index";

const toolbarStyles = {
    justifyContent: 'space-between',
    height: '120px',
    background: '#121212'
};

const starPowerStyles = {
    height: '28px',
    width: '71px',
    color: '#FFF',
    fontFamily: 'Roboto',
    fontSize: '24px',
    letterSpacing: '-1.65px',
    lineHeight: '28px',
    fontWeight: 'normal'
};

const starStyles = {
    fontWeight: 'bold'
};

const actorSearchStyle = {
    height: '100vh',
    background: '#121212'
};

const mainSection = {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'flex-start',
    alignItems: 'center'
};

class ActorSearch extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            actorName: ''
        };
        this.handleChange = this.handleChange.bind(this);
    }

    render() {
        return (
            <div style={actorSearchStyle} className='actor-search'>
                <AppBar position="static">
                    <Toolbar variant="dense" style={toolbarStyles}>
                        <Typography variant="h6" color="inherit" style={starPowerStyles}>
                            <span style={starStyles}>star</span>pwr
                        </Typography>
                        <IconButton edge="end" color="inherit" aria-label="menu">
                            <MenuIcon/>
                        </IconButton>
                    </Toolbar>
                </AppBar>
                <div style={mainSection}>
                    <TextField
                        id="standard-name"
                        placeholder="Search for actor"
                        value={this.state.actorName}
                        className="text-field"
                        onChange={this.handleChange}
                        margin="normal"
                        InputProps={{
                            startAdornment: <InputAdornment position="start">
                                <SearchIcon className='search-icon' fontSize='20'/>
                            </InputAdornment>,
                        }}
                    />
                </div>
                <div>
                    {actorSearchResults}
                </div>
            </div>
        );
    }

    handleChange(event) {
        console.log(event.target.value);
        this.setState({
            actorName: event.target.value
        });

        // axios.get(`/api/processing/stuff`)
        //     .then(data => {
        //         const jwt = data.headers['x-authentication'];
        //         this.props.setJWT(jwt);
        //         this.props.history.push('/');
        //     });
    }
}

export default ActorSearch;
