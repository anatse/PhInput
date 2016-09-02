import React, {Component, PropTypes} from 'react'

class Comment extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        const {owner, comment, createDate} = this.props;
        const date = new Date(createDate);
        return (
            <div className='comment'>
                <span className='time'>{date.toLocaleDateString()} {date.toLocaleTimeString()}</span>
                &nbsp;-&nbsp;
                <span className='author'>{owner}</span>
                &nbsp;:&nbsp;
                <span className="text">{comment}</span>
            </div>
        )
    }

}

Comment.propTypes = {
    owner: PropTypes.string,
    comment: PropTypes.string,
    createDate: PropTypes.any
}

export default Comment
