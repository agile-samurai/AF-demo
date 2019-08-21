import React from 'react';
import TextField from '@material-ui/core/TextField';
import './CelebritySearch.css';
import {InputAdornment} from '@material-ui/core';
import SearchIcon from '@material-ui/icons/Search';
import axios from "axios/index";
import CelebrityRow from "../CelebrityRow/CelebrityRow";
import HeaderBar from "../HeaderBar/HeaderBar";

export default class CelebritySearch extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            searchTerm: '',
            celebritySearchResults: [],
            cursor: 0
        };
        this.CELEBRITIES_ENDPOINT = '/api/celebrity';

        this.setUpInfiniteScroll();

        this.handleInputChange = this.handleInputChange.bind(this);
    }

    render() {
        const {searchTerm, celebritySearchResults} = this.state;

        const celebrityRows = celebritySearchResults
            .map(celebritySearchResult => <CelebrityRow celebrity={celebritySearchResult} key={celebritySearchResult.id} />);

        return (
            <div>
                <HeaderBar/>
                <div className="main-section">
                    <TextField
                        id="standard-name"
                        placeholder="Search for celebrity"
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
                    <div>Search by any combination of celebrity name, celebrity alias, and/or movie name</div>
                </div>

                <div className="search-results-wrapper">
                    <div className="search-results">
                        {celebrityRows}
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

        axios.get(this.CELEBRITIES_ENDPOINT, {
            params: {
                search: searchTerm,
                cursor: cursor + 1
            }
        })
        .then(response => {
            this.setState({
                celebritySearchResults: this.state.celebritySearchResults.concat(response.data.content),
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
        axios.get(this.CELEBRITIES_ENDPOINT, {
            params: {
                search: searchTerm,
                cursor: 0
            }
        })
        .then(response => {
            this.setState({
                celebritySearchResults: response.data.content
            });
        });
    }
}
