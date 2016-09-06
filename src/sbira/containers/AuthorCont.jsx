import { connect } from 'react-redux'
import getUserDisplayName from '../utils/utils'
import Author from '../components/Author'

const mapStateToProps = (state, ownProps) => {
  const user = state.users.filter(user => {
    return user.login == ownProps.authorLogin
  })[0];
  return {
    authorName : user && user.displayName
  }
}

const AuthorCont = connect(
  mapStateToProps
)(Author)

export default AuthorCont
