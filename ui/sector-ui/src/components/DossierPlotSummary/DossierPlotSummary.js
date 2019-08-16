import React from 'react';
import './DossierPlotSummary.css';

export default class DossierPlotSummary extends React.Component {
    componentDidMount() {
        // TODO do processing in here, so that it's ready when button is clicked
    }

    render() {
        const {summary, entityClassifications, redactionEnabled} = this.props;

        return (
            <div className="plot-summary">
                <div className="plot-summary-header">PLOT SUMMARY</div>
                <div className="plot-summary-body">{summary}</div>
            </div>
        );
    }
}
