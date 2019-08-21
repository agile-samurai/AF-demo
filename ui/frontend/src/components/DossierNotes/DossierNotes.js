import React from 'react';
import './DossierNotes.css';
import Fab from '@material-ui/core/Fab';
import AddIcon from '@material-ui/icons/Add';
import Modal from '@material-ui/core/Modal';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import axios from "axios/index";

export default class DossierNotes extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            modalOpen: false,
            noteInputValue: ''
        };

        this.handleOpen = this.handleOpen.bind(this);
        this.handleClose = this.handleClose.bind(this);
        this.handleNoteInputChange = this.handleNoteInputChange.bind(this);
        this.handleAddingNote = this.handleAddingNote.bind(this);
    }

    render() {
        const {modalOpen, noteInputValue} = this.state;

        const processedNotes = this.props.notes.map(noteObject => {
            return (
              <div className="a-note" key={noteObject.timeStamp}>
                  <div className="note-section-header">Content:</div>
                  <div>{noteObject.note}</div>

                  <div className="new-note-section-start note-section-header">Timestamp:</div>
                  <div>{noteObject.timeStamp}</div>

                  <div className="new-note-section-start note-section-header">User:</div>
                  <div>{noteObject.user}</div>
              </div>
            );
        });

        return (
            <div className="dossier-notes">
                <div className="heading-and-add-button">
                    <div className="notes-section-heading">Notes</div>
                    <div className="add-note-button-wrapper">
                        <Fab variant="extended" className="add-note-button" aria-label="add note" onClick={this.handleOpen}>
                            <AddIcon/>
                            ADD NOTE
                        </Fab>
                    </div>
                </div>
                <div>
                    {processedNotes}
                </div>
                <Modal
                    aria-labelledby="notes-entry-modal"
                    aria-describedby="a modal for entering notes about this dossier"
                    open={modalOpen}
                    onClose={this.handleClose}>
                    <div className="modal-content">
                        <div className="add-note-heading">Add Note</div>
                        <div className="note-content">
                            <TextField
                                aria-label="text area for dossier note"
                                placeholder="Enter your note here"
                                multiline
                                rowsMax="100"
                                value={noteInputValue}
                                onChange={this.handleNoteInputChange}
                                margin="normal"
                                className="note-input-textfield"
                            />
                        </div>
                        <div className="add-note-link">
                            <Button size="large" onClick={this.handleAddingNote}>
                                ADD NOTE
                            </Button>
                        </div>
                    </div>
                </Modal>
            </div>
        );
    }

    handleOpen() {
        this.setState({
            modalOpen: true
        })
    }

    handleClose() {
        this.setState({
            modalOpen: false
        })
    }

    handleNoteInputChange(event) {
        this.setState({
            noteInputValue: event.target.value
        })
    }

    handleAddingNote() {
        const {dossierID, refreshData} = this.props;

        axios.post(`/api/dossier/${dossierID}/note`,
            {
                content: this.state.noteInputValue
            })
        .then(() => {
            this.setState({
                noteInputValue: ''
            }, () => {
                refreshData(dossierID);
                this.handleClose();
            });
        });
    }
}
