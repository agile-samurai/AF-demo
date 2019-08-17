import React from 'react';
import './DossierPlotSummary.css';

export default class DossierPlotSummary extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            redactionProcessedSummary: []
        };
    }

    componentDidMount() {
        this.doRedactionProcessing();
    }

    render() {
        const {redactionEnabled} = this.props;
        const {redactionProcessedSummary} = this.state;

        return (
            <div className={`plot-summary ${redactionEnabled ? 'redaction-enabled': ''}`}>
                <div className="plot-summary-header">PLOT SUMMARY</div>
                <div className="plot-summary-body">{redactionProcessedSummary}</div>
                {
                    redactionEnabled ?
                    <div className="redaction-item-types-legend">
                        <div className="company-entity-type entity-type-label">
                            <div className="color-block"/>
                            <div>&nbsp;- Company</div>
                        </div>
                        <div className="country-entity-type entity-type-label">
                            <div className="color-block"/>
                            <div>&nbsp;- Country</div>
                        </div>
                        <div className="person-entity-type entity-type-label">
                            <div className="color-block"/>
                            <div>&nbsp;- Person</div>
                        </div>
                    </div>
                    : null
                }
            </div>
        );
    }

    doRedactionProcessing() {
        const {summary, entityClassifications} = this.props;

        let redactionProcessedSummary = [];

        summary.split('')
            .forEach((character, index) => {
                const characterWasEntityClassified = entityClassifications.reduce((accumulator, entityClassification, entityClassificationIndex) => {
                    if(index > entityClassification.end) { /* if we've moved past the end of this entity classification,
                        remove this entity classification as an optimization */
                        entityClassifications.splice(entityClassificationIndex, 1);
                    }

                    if((index >= entityClassification.start) && (index < entityClassification.end)) {
                        redactionProcessedSummary.push(<span key={index} className={`${entityClassification.classification}`}>{character}</span>);

                        return true;
                    }

                    return accumulator;
                }, false);

                if(!characterWasEntityClassified) {
                    redactionProcessedSummary.push(<span key={index}>{character}</span>);
                }
            });

        this.setState({
            redactionProcessedSummary
        })
    }
}
