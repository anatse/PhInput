import { connect } from 'react-redux'
// import { toggleTodo } from '../actions'
import TaskList from '../components/TaskList'
import { fetchTasks, editTask, postEditedTask, postNewTask, initDeleteTask, saveComment} from '../actions'

const filterTasks = (tasks, filters) => {
  return tasks.filter(t => {
    return filters.some( f =>{
      return t.status == f.name
    })
  })
}

const mapStateToProps = (state) => {
  return {
    tasks: filterTasks(state.tasks.tasks, state.visibilityFilter),
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
    onChange: (id, editedTask) => {
      return function(dispatch){
          dispatch(editTask(id, editedTask))
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
