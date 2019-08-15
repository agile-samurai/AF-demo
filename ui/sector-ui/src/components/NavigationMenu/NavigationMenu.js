import React from 'react';
import ListItemText from '@material-ui/core/ListItemText';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import MenuIcon from '@material-ui/icons/Menu';
import {Link} from "react-router-dom";

export default class ActorSearch extends React.Component {
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
                    id="customized-menu"
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
                    <Link to="/training-data">
                        <MenuItem>
                            <ListItemText primary="View Training Data" />
                        </MenuItem>
                    </Link>
                    <Link to="/training-data">
                        <MenuItem>
                            <ListItemText primary="Log Out" />
                        </MenuItem>
                    </Link>
                </Menu>
            </div>
        );
    }
}
