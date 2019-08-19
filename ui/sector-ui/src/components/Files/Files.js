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
        const processedFiles = this.props.files.map(file => {
            return <div className="a-file"
                     onClick={() => this.downloadFile(file.fileId, file.contentType)}
                     key={file.fileId}>{file.name}</div>
        });

        return (
            <div className="files">
                <div className="files-section-heading">Files</div>

                <div className="existing-files">{processedFiles}</div>

                <div className="input-label">
                    Add a file to this dossier (valid file types are text file and PDF):
                </div>

                <form onSubmit={this.onFormSubmit}>
                    <Input type="file" onChange={this.onChange}/>
                    <button type="submit">Upload</button>
                </form>
            </div>
        );
    }

    onFormSubmit(event){
        event.preventDefault();
        this.uploadFile(this.state.file).then(()=>{
            this.props.refreshData(this.props.dossierID);
        })
    }

    onChange(event) {
        this.setState({
            file: event.target.files[0]
        });
    }

    uploadFile(file){
        const formData = new FormData();

        formData.append('file', file);

        const configuration = {
            headers: {
                'content-type': 'multipart/form-data'
            }
        };

        return axios.post(`/api/files/${this.props.dossierID}`, formData, configuration);
    }

    downloadFile(fileName, contentType) {
        axios.get(`/api/files/${this.props.dossierID}/${fileName}`, {
            headers: {
                'File-Action': 'download'
            }
        }).then(response => {
            console.log(response);
            this.openFileDialog(response.data, fileName, contentType);
        });
    }

    openFileDialog(newBlob, fileName, type) {
        const blob = new Blob([newBlob], {type});
        const a = document.createElement('a');
        a.download = fileName;
        a.href = URL.createObjectURL(blob);
        document.body.appendChild(a);
        a.click();
        setTimeout(() => {
            URL.revokeObjectURL(a.href);
            document.body.removeChild(a);
        }, 100);
    }
}



