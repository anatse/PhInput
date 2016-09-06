import {connect} from 'react-redux'

import CommentCreator from '../components/CommentCreator'

const mapStateToProps = (state, ownProps) => {
    // const user = state.users.filter(user => {
    //     return user.login == state.currentUser.login
    // })[0];
    return {
        taskId: ownProps.taskId,
        onAddComment: (comment, taskId) => {
            comment.owner = state.currentUser.login
            ownProps.onAddComment(comment, taskId)
        }
    }
}

const CommentCreatorCont = connect(mapStateToProps)(CommentCreator)

export default CommentCreatorCont
