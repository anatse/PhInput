import {connect} from 'react-redux'

import Comment from '../components/Comment'

const mapStateToProps = (state, ownProps) => {
    const user = state.users.filter(user => {
        return user.login == ownProps.owner
    })[0];
    return {
      ...ownProps,
      owner: user && user.displayName || ownProps.owner
    }
}

const CommentCont = connect(mapStateToProps)(Comment)

export default CommentCont
