import { combineReducers } from 'redux'
// import todos from './todos'
import visibilityFilter from './visibilityFilter'
import searchFilter from './searchFilter'
import tasks from './tasks'
import tasksAjaxStatus from './tasksAjaxStatus'
import users from './users'

const rootReducer = combineReducers({
  // todos,
  visibilityFilter,
  searchFilter,
  users,
  tasks: combineReducers({
    tasks,
    status:tasksAjaxStatus
  })
})

export default rootReducer
