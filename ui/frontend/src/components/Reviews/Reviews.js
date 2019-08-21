import React from 'react';
import './Reviews.css';

export default class Reviews extends React.Component {
    render() {
        const {reviews} = this.props;

        if (reviews === null || reviews.length === 0) {
            return null;
        }

        const processedReviews = reviews && reviews.map((reviewObject) => {
            return (
                <div className="a-review" key={reviewObject.title}>
                    <div className="review-section-header">Title:</div>
                    <div>{reviewObject.title}</div>

                    <div className="new-review-section-start review-section-header">Rating:</div>
                    <div>{reviewObject.rating}</div>

                    <div className="new-review-section-start review-section-header">Content:</div>
                    <div>{reviewObject.content}</div>
                </div>
            );
        });

        return (
            <div className="reviews">
                <div className="heading-container">
                    <div className="reviews-section-heading">Reviews</div>
                </div>
                <div>
                    {processedReviews}
                </div>
            </div>
        );
    }
}
