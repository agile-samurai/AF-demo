import React from 'react';
import axios from "axios/index";

export default class TrainingData extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            trainingData: [],
            cursor: 0
        };
        this.TRAINING_ENDPOINT = '/api/training';

        this.setUpInfiniteScroll();
    }

    componentDidMount() {
        this.loadMore();
    }

    render() {
        const processedTrainingData = this.state.trainingData.map(data => {
            const splitElement = data.key.split(': ')[1].trim();
            return <div><br/><a key={data.key} href={`https://ugroup-rdso-challenge-data.s3.amazonaws.com/${splitElement}`}>
                {`https://ugroup-rdso-challenge-data.s3.amazonaws.com/${splitElement}`}</a><br/></div>;
        });

        return (
            <div>
                {processedTrainingData}
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
        const {cursor} = this.state;

        axios.get(this.TRAINING_ENDPOINT, {
            params: {
                cursor: cursor + 1
            }
        })
            .then(response => {
                this.setState({
                    trainingData: this.state.trainingData.concat(response.data.content),
                    cursor: cursor + 1
                });
            });
    }
}
