import { connect } from 'react-redux'
import UserSelect from '../components/UserSelect'
import {postEditedTask} from '../actions'

const mapStateToProps = (state, ownProps) => {
  const options = state.users.map(user => {
    return {
      value: user.login,
      label: user.displayName,
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
      const taskToSave = {...ownProps.task, assignedPerson: selectedOption.value}
      dispatch(postEditedTask(taskToSave));
    }
  }
}

const UserSelectCont = connect(
  mapStateToProps,
  mapDispatchToProps
)(UserSelect)

export default UserSelectCont
