import React from 'react';
import './Files.css';
import Input from '@material-ui/core/Input';
import axios from "axios/index";

export default class Files extends React.Component {
    constructor(props) {
        super(props);
        this.state ={
            file: null
        };

        this.onFormSubmit = this.onFormSubmit.bind(this);
        this.onChange = this.onChange.bind(this);
    }

    render() {
        return (
            <div className="files">
                <div className="files-section-heading">Files</div>
                <div className="input-label">
                    Add a file to this dossier (valid file types are MS Word Document, text file, and PDF):
                </div>

                <form onSubmit={this.onFormSubmit}>
                    <h1>File Upload</h1>
                    <Input type="file" onChange={this.onChange}/>
                    <button type="submit">Upload</button>
                </form>
            </div>
        );
    }

    onFormSubmit(event){
        event.preventDefault(); // Stop form submit
        this.uploadFile(this.state.file).then((response)=>{
            console.log(response.data);
        })
    }

    onChange(event) {
        this.setState({file:event.target.files[0]});
    }

    uploadFile(file){
        const url = 'http://example.com/file-upload';
        const formData = new FormData();

        formData.append('file', file);

        const configuration = {
            headers: {
                'content-type': 'multipart/form-data'
            }
        };

        return  axios.post(url, formData, configuration)
    }
}



