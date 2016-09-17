import { connect } from 'react-redux'
// import { toggleTodo } from '../actions'
import TaskList from '../components/TaskList'
import { fetchTasks, getUsersList, getCurrentUser, editTask, postEditedTask, postNewTask, initDeleteTask, saveComment} from '../actions'
import filterTasks from '../utils/utils'

const mapStateToProps = (state) => {
  return {
    tasks: filterTasks(state.tasks.tasks, state.visibilityFilter, state.searchFilter),
    isLoading: state.tasks.status.isLoading,
    lastUpdate: state.tasks.status.lastUpdate
  }
}

// const mapDispatchToProps = (dispatch) => {
//   return {
//     onEditTask: (id) => {
//       dispatch(editTask(id))
//     }
//   }
// }

const VisibleTaskList = connect(
  mapStateToProps
  ,
  {
    fetchTasks,
    getUsersList,
    getCurrentUser,
    onChange: (id, editedPart) => {
      return function(dispatch){
          dispatch(editTask(id, editedPart))
      }
    },
    onSaveTask: (taskToSave) => {
      return function(dispatch){
        dispatch(postEditedTask(taskToSave));
      }
    },
    onAddTask: (taskToAdd) => {
        return function(dispatch){
          dispatch(postNewTask(taskToAdd));
        }
    },
    onDelTask: (taskId) => {
      return function(dispatch){
        dispatch(initDeleteTask(taskId));
      }
    },
    onAddComment: (comment, taskId) => {
      return function(dispatch){
        dispatch(saveComment(comment, taskId));
      }
    }

  }
  // mapDispatchToProps
)(TaskList)

export default VisibleTaskList
