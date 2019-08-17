import React from 'react';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import IconButton from '@material-ui/core/IconButton';
import TextField from '@material-ui/core/TextField';
import './DossierList.css';
import {InputAdornment} from '@material-ui/core';
import SearchIcon from '@material-ui/icons/Search';
import axios from "axios/index";
import NavigationMenu from "../NavigationMenu/NavigationMenu";
import {Link} from "react-router-dom";
import DossierContent from "../DossierContent/DossierContent";

export default class DossierList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            searchTerm: '',
            moviePublicSummarySearchResults: [],
            cursor: 0
        };
        this.MOVIE_PUBLIC_SUMMARY_ENDPOINT = '/api/movie-public-summary';

        this.setUpInfiniteScroll();

        this.handleInputChange = this.handleInputChange.bind(this);
    }

    componentDidMount() {
        this.doSearch('');
    }

    render() {
        const {searchTerm, moviePublicSummarySearchResults} = this.state;

        const dossierRows = moviePublicSummarySearchResults
            .map(dossierSearchResult => <DossierContent dossierId={dossierSearchResult.id} key={dossierSearchResult.id}/>);

        return (
            <div className='dossier-search'>
                <AppBar position="static">
                    <Toolbar variant="dense" className="search-toolbar">
                        <div className="star-power-text">
                            <Link to="/" className="navigation-link">
                                <span className="star-text">star</span>pwr
                            </Link>
                        </div>
                        <IconButton edge="end" color="inherit" aria-label="menu">
                            <NavigationMenu/>
                        </IconButton>
                    </Toolbar>
                </AppBar>
                <div className="main-section">
                    <TextField
                        id="standard-name"
                        placeholder="Search for dossier"
                        value={searchTerm}
                        className="text-field"
                        onChange={this.handleInputChange}
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
                        {dossierRows}
                    </div>
                </div>
            </div>
        );
    }

    setUpInfiniteScroll() {
        window.onscroll = () => {
            if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
                this.loadMore();
            }
        };
    }

    loadMore() {
        const {searchTerm, cursor} = this.state;

        axios.get(this.MOVIE_PUBLIC_SUMMARY_ENDPOINT, {
            params: {
                search: searchTerm,
                cursor: cursor + 1
            },
            auth: {  // TODO remove
                username: 'business-user',
                password: 'password'
            }
        })
        .then(response => { // TODO make the call to exchange the Movie for a Dossier, then concat that Dossier with the others on this.state
            this.setState({
                moviePublicSummarySearchResults: this.state.moviePublicSummarySearchResults.concat(response.data.content),
                cursor: cursor + 1
            });
        });
    }

    handleInputChange(event) {
        const searchTerm = event.target.value;

        this.setState({
            searchTerm
        }, () => {
            this.doSearch(searchTerm, this.state.cursor);
        });
    }

    doSearch(searchTerm) {
        axios.get(this.MOVIE_PUBLIC_SUMMARY_ENDPOINT, {
            params: {
                search: searchTerm,
                cursor: 0
            },
            auth: {  // TODO remove
                username: 'business-user',
                password: 'password'
            }
        })
        .then(response => {
            this.setState({
                moviePublicSummarySearchResults: response.data
            });
        });
    }
}
