import React from 'react';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import TextField from '@material-ui/core/TextField';
import './ActorSearch.css';
import {InputAdornment} from '@material-ui/core';
import SearchIcon from '@material-ui/icons/Search';
import axios from "axios/index";
import ActorRow from "../ActorRow/ActorRow";

const toolbarStyles = {
    justifyContent: 'space-between',
    height: '120px',
};

const starPowerStyles = {
    height: '28px',
    width: '71px',
    fontFamily: 'Roboto',
    fontSize: '24px',
    letterSpacing: '-1.65px',
    lineHeight: '28px',
    fontWeight: 'normal'
};

const starStyles = {
    fontWeight: 'bold'
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

        const actorRows = actorSearchResults
            .map(actorSearchResult => <ActorRow actor={actorSearchResult} />);

        return (
            <div className='actor-search'>
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
                            spellCheck: false,
                            startAdornment: <InputAdornment position="start">
                                <SearchIcon className='search-icon'/>
                            </InputAdornment>,
                        }}
                    />
                </div>
                <div className="search-results-wrapper">
                    <div className="search-results">
                        {actorRows}
                    </div>
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
