import { combineReducers } from 'redux'
// import todos from './todos'
import visibilityFilter from './visibilityFilter'
import tasks from './tasks'
import tasksAjaxStatus from './tasksAjaxStatus'

const rootReducer = combineReducers({
  // todos,
  visibilityFilter,
  tasks: combineReducers({
    tasks,
    status:tasksAjaxStatus
  })
})

export default rootReducer
