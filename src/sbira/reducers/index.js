import { combineReducers } from 'redux'
// import todos from './todos'
import visibilityFilter from './visibilityFilter'
import searchFilter from './searchFilter'
import tasks from './tasks'
import tasksAjaxStatus from './tasksAjaxStatus'
import users from './users'
import currentUser from './currentUser'

const rootReducer = combineReducers({
  // todos,
  visibilityFilter,
  searchFilter,
  users,
  currentUser,
  tasks: combineReducers({
    tasks,
    status:tasksAjaxStatus
  })
})

export default rootReducer
