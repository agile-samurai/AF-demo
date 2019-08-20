import React from 'react';
import ListItemText from '@material-ui/core/ListItemText';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import MenuIcon from '@material-ui/icons/Menu';
import {Link} from "react-router-dom";
import './NavigationMenu.css';

export default class NavigationMenu extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            anchorEl: null
        };

        this.handleClick = this.handleClick.bind(this);
        this.handleClose = this.handleClose.bind(this);
    }

    handleClick(event) {
        this.setState({
            anchorEl: event.currentTarget
        });
    }

    handleClose() {
        this.setState({
            anchorEl: null
        });
    }

    render() {
        return (
            <div>
                <MenuIcon aria-controls="customized-menu"
                          aria-haspopup="true"
                          variant="contained"
                          onClick={this.handleClick}/>
                <Menu
                    anchorEl={this.state.anchorEl}
                    keepMounted
                    open={Boolean(this.state.anchorEl)}
                    elevation={0}
                    getContentAnchorEl={null}
                    anchorOrigin={{
                        vertical: 'bottom',
                        horizontal: 'center',
                    }}
                    transformOrigin={{
                        vertical: 'top',
                        horizontal: 'center',
                    }}
                    onClose={this.handleClose}>
                    <Link to="/dossier-list-and-search" className="menu-item-link">
                        <MenuItem>
                            <ListItemText primary="View And Search Dossiers" />
                        </MenuItem>
                    </Link>
                    <Link to="/celebrity-search" className="menu-item-link">
                        <MenuItem>
                            <ListItemText primary="Search Celebrity Profiles" />
                        </MenuItem>
                    </Link>
                    <Link to="/training-data" className="menu-item-link">
                        <MenuItem>
                            <ListItemText primary="View Training Data" />
                        </MenuItem>
                    </Link>
                    <Link to="/logout" className="menu-item-link">
                        <MenuItem>
                            <ListItemText primary="Log Out" />
                        </MenuItem>
                    </Link>
                </Menu>
            </div>
        );
    }
}
