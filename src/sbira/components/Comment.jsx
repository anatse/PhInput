import React, {Component, PropTypes} from 'react'

class Comment extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        const {owner, comment, createDate} = this.props;
        const date = new Date(createDate);
        return (
          <div>
            {date.toLocaleDateString()} {date.toLocaleTimeString()} - {owner} : {comment}
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
