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
            searchTerm: '',
            actorSearchResults: []
        };
        this.handleChange = this.handleChange.bind(this);
    }

    render() {
        const {searchTerm, actorSearchResults} = this.state;

        const formattedActorResults = actorSearchResults.map(actorSearchResult => {
            return (
                <div key={actorSearchResult.id}>
                    Actor name:
                    {actorSearchResult.fullName}
                </div>
            );
        });

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
                        value={searchTerm}
                        className="text-field"
                        onChange={this.handleChange}
                        margin="normal"
                        InputProps={{
                            startAdornment: <InputAdornment position="start">
                                <SearchIcon className='search-icon'/>
                            </InputAdornment>,
                        }}
                    />
                </div>
                <div className='search-results'>
                    {formattedActorResults}
                </div>
            </div>
        );
    }

    handleChange(event) {
        const searchTerm = event.target.value;

        axios.get(`/api/actors`, {
            params: {
                search: searchTerm
            },
            auth: {  // TODO remove
                username: 'business-user',
                password: 'password'
            }
        })
        .then(response => {
            this.setState({
                searchTerm,
                actorSearchResults: response.data.content
            });
        });
    }
}

export default ActorSearch;
