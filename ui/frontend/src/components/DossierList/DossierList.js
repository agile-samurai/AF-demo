import React from 'react';
import TextField from '@material-ui/core/TextField';
import './DossierList.css';
import {InputAdornment} from '@material-ui/core';
import SearchIcon from '@material-ui/icons/Search';
import axios from "axios/index";
import DossierContent from "../DossierContent/DossierContent";
import HeaderBar from "../HeaderBar/HeaderBar";

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
            .map(dossierSearchResult => <DossierContent dossierID={dossierSearchResult.id}
                                                        key={dossierSearchResult.id}
                                                        deleted={!dossierSearchResult.dossierAvailable}/>);

        return (
            <div className='dossier-search'>
                <HeaderBar/>
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
            if ((window.innerHeight + window.scrollY) >= (document.body.offsetHeight - 300)) {
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
            }
        })
        .then(response => {
            this.setState({
                moviePublicSummarySearchResults: this.state.moviePublicSummarySearchResults.concat(response.data),
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
            }
        })
        .then(response => {
            this.setState({
                moviePublicSummarySearchResults: response.data
            });
        });
    }
}
