import { combineReducers } from 'redux'
// import todos from './todos'
import visibilityFilter from './visibilityFilter'
import tasks from './tasks'
import tasksAjaxStatus from './tasksAjaxStatus'
import users from './users'

const rootReducer = combineReducers({
  // todos,
  visibilityFilter,
  users,
  tasks: combineReducers({
    tasks,
    status:tasksAjaxStatus
  })
})

export default rootReducer
