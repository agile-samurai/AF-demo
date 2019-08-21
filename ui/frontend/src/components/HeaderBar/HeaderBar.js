import React from 'react';
import Toolbar from '@material-ui/core/Toolbar';
import IconButton from '@material-ui/core/IconButton';
import NavigationMenu from "../NavigationMenu/NavigationMenu";
import {Link} from "react-router-dom";
import AppBar from '@material-ui/core/AppBar';
import './HeaderBar.css';

export default class HeaderBar extends React.Component {
    render() {
        return (
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
        );
    }
}
