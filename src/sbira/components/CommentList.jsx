import React, {Component, PropTypes} from 'react'
import Comment from './Comment'
import CommentCreator from './CommentCreator'

class CommentList extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        const {taskId, comments, onAddComment} = this.props;
        return (
          <div className='cont'>
            <div className='list'>
              {comments.map((comment, index)=> {
                return <Comment key={index} {...comment} />
              })}
            </div>
            <CommentCreator taskId={taskId} onAddComment={onAddComment} />
          </div>
        )
    }

}

CommentList.propTypes = {
    taskId: PropTypes.string.isRequired,
    comments: PropTypes.array.isRequired
}

export default CommentList
