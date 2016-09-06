import React, {Component, PropTypes} from 'react'
import CommentCont from '../containers/CommentCont'
import CommentCreatorCont from '../containers/CommentCreatorCont'

class CommentList extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        const {taskId, comments, onAddComment} = this.props;
        return (
            <div className='cont'>
                <div className='cont-ins'>
                    {comments.map((comment, index) => {
                        return <CommentCont key={index} {...comment}/>
                    })}
                </div>
                <CommentCreatorCont taskId={taskId} onAddComment={onAddComment}/>
            </div>
        )
    }

}

CommentList.propTypes = {
    taskId: PropTypes.string.isRequired,
    comments: PropTypes.array.isRequired
}

export default CommentList
