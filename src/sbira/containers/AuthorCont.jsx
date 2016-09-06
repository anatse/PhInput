import { connect } from 'react-redux'
import getUserDisplayName from '../utils/utils'
import Author from '../components/Author'

const mapStateToProps = (state, ownProps) => {
  const authorUser = state.users.filter(user => {
    return user.login == ownProps.authorLogin
  })[0];
  return {
    authorName : authorUser && authorUser.displayName || ownProps.authorLogin
  }
}

const AuthorCont = connect(
  mapStateToProps
)(Author)

export default AuthorCont
