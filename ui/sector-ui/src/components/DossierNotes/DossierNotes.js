import React from 'react';
import './DossierNotes.css';
import Fab from '@material-ui/core/Fab';
import AddIcon from '@material-ui/icons/Add';

export default class DossierNotes extends React.Component {
    render() {
        return (
            <div className="dossier-notes">
                <div className="notes-section-heading">Notes</div>
                <div className="add-note-button-wrapper">
                    <Fab variant="extended" className="add-note-button" aria-label="add">
                        <AddIcon />
                        ADD NOTE
                    </Fab>
                </div>
            </div>
        );
    }
}
