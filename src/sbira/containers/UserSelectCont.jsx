import { connect } from 'react-redux'
import UserSelect from '../components/UserSelect'
import {editTask} from '../actions'

const mapStateToProps = (state, ownProps) => {
  const options = state.users.map(user => {
    return {
      value: user.login,
      label: user.firstName,
    }
  })
  return {
    options,
    value: ownProps.value
  }
}

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    onChange: function(selectedOption)  {
      dispatch(editTask(ownProps.taskId, {assignedPerson: selectedOption.value}))
    }
  }
}

const UserSelectCont = connect(
  mapStateToProps,
  mapDispatchToProps
)(UserSelect)

export default UserSelectCont
