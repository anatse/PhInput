import axios from 'axios';
import statuses from '../utils/taskStatuses'

/******************************** FILTERING TASKS ********************************/
export const toggleVisibilityFilter = (filter) => {
  return {
    type: 'TOGGLE_VISIBILITY_FILTER',
    filter
  }
}

export const clearVisibilityFilter = () => {
  return {
    type: 'CLEAR_VISIBILITY_FILTER',
  }
}

export const setSearchFilter = (filter) => {
  return {
    type: 'SET_SEARCH_FILTER',
    filter
  }
}

export const clearSearchFilter = () => {
  return {
    type: 'CLEAR_SEARCH_FILTER',
  }
}

/******************************** GETTING USERS ********************************/
export const currentUserOk = (user) => {
  return {
    type: 'CURRENT_USER_OK',
    receivedAt: new Date(),
    user
  }
}

export const getCurrentUser = () => {

  return function (dispatch) {
    let axiosPromise = axios.get('/project/curuser', {params:{antiCache:Date.now()}});
    axiosPromise.then(response => {
        // console.log(response);
        if (response.data && response.data.indexOf && response.data.indexOf('DOCTYPE html')>=0) {
          // redirect to login
          debugger
          window.location.href = '/pub/login.html';
        }
        dispatch(currentUserOk(response.data))
      })
      .catch( error => {
        console.log(error);
        const goLogin = confirm("Не удалось определить пользователя. Перейти на экран входа?");
         // failed to get user. Ask for redirect to login:
        if (goLogin) window.location.href = '/pub/login.html'
        // dispatch(currentUserErr(error))
      });
    return axiosPromise;
  }
}

export const receiveUsersOk = (users) => {
  return {
    type: 'RECEIVE_USERS_OK',
    receivedAt: new Date(),
    users
  }
}

export const receiveUsersErr = (users) => {
  return {
    type: 'RECEIVE_USERS_ERR',
    receivedAt: new Date(),
    users
  }
}
export const getUsersList = () => {

  return function (dispatch) {
    let axiosPromise = axios.get('/user', {params:{antiCache:Date.now()}});
    axiosPromise.then(response => {
        // console.log(response);
        dispatch(receiveUsersOk(response.data))
      })
      .catch( error => {
        console.log(error);
        dispatch(receiveUsersErr(error))
      });
    return axiosPromise;
  }
}

/*************************** REQUESTING AND RECEIVING TASKS ***************************/
export const requestTasks = () => {
  return {
    type: 'REQUEST_TASKS'
  }
}

export const receiveTasksOk = (tasks) => {
  return {
    type: 'RECEIVE_TASKS_OK',
    receivedAt: new Date(),
    tasks
  }
}

export const receiveTasksErr = (tasks) => {
  return {
    type: 'RECEIVE_TASKS_ERR',
    receivedAt: new Date(),
    tasks
  }
}
export const fetchTasks = (param) => {

  return function (dispatch) {

    dispatch(requestTasks(param))

    return refreshTasks(dispatch);
  }
}
const refreshTasks = (dispatch) => {
  const scalaPath = '/task';
  // const scalaPath = '/test/mock/simple.json';
  return axios.get(scalaPath, {params:{antiCache:Date.now()}})
    .then(response => {
      // console.log(response);
      dispatch(receiveTasksOk(response.data))
    })
    .catch( error => {
      console.log(error);
      dispatch(receiveTasksErr(error))
    });
}

/*************************** EDITING TASKS ***************************/
export const editTask = (id, editedTask) => {
  return {
    type: 'EDIT_TASK',
    id,
    editedTask
  }
}
export const saveEditedTask = () => {
 return {
   type: 'SAVE_EDITED_TASK'
 }
}
export const saveEditedTaskOk = (task) => {
  return {
    type: 'SAVE_EDITED_TASK_OK',
    task
  }
}
export const saveEditedTaskErr = (error) => {
  return {
    type: 'SAVE_EDITED_TASK_ERR',
    error
  }
}

export const postEditedTask = (task) => {
  return function(dispatch){

    dispatch(saveEditedTask())
    // update task changeDate:
    task.changeDate = Date.now();

    return axios.put('/task/' + task.id , task)
      .then(function (response) {
        // console.log(response);
        dispatch(saveEditedTaskOk(response.data))
        // refresh
        fetchTasks()(dispatch);
      })
      .catch(function (error) {
        console.error(error);
        dispatch(saveEditedTaskErr(error))
      });
  }
}

/*************************** SAVING TASKS ***************************/

export const saveNewTask = () => {
 return {
   type: 'SAVE_NEW_TASK'
 }
}
export const saveNewTaskOk = (task) => {
  return {
    type: 'SAVE_NEW_TASK_OK',
    task
  }
}
export const saveNewTaskErr = (error) => {
  return {
    type: 'SAVE_NEW_TASK_ERR',
    error
  }
}

export const postNewTask = (task) => {
  return function(dispatch){

    dispatch(saveNewTask())
    const taskToSave = Object.assign({},{
        "deadLine": task.deadLine || new Date(),
        "name": task.name,
        "content": task.content,
        "owner": task.owner,
        "comments": task.comments || [],
        "changeDate": Date.now(),
        "status": statuses.NEW.name
    });

    return axios.post('/task', taskToSave)
      .then(function (response) {
        // console.log(response);
          dispatch(saveNewTaskOk(response.data))
          // refresh
          fetchTasks()(dispatch);
      })
      .catch(function (error) {
        console.error(error);
        dispatch(saveNewTaskErr(error))
      });
  }
}


/*************************** DELETING TASKS ***************************/

export const deleteTask = () => {
 return {
   type: 'DELETE_TASK'
 }
}
export const deleteTaskOk = (task) => {
  return {
    type: 'DELETE_TASK_OK',
    task
  }
}
export const deleteTaskErr = (error) => {
  return {
    type: 'DELETE_TASK_ERR',
    error
  }
}

export const initDeleteTask = (taskId) => {

  return function(dispatch){

    dispatch(deleteTask())

    return axios.delete('/task/' + taskId)
      .then(function (response) {
        // console.log(response);
        dispatch(deleteTaskOk(response.data))
        // refresh
        fetchTasks()(dispatch);
      })
      .catch(function (error) {
        console.error(error);
        dispatch(deleteTaskErr(error))
      });
  }
}

/*************************** COMMENTS ***************************/

export const postComment = () => {
 return {
   type: 'POST_COMMENT'
 }
}
export const postCommentOk = (comment) => {
 return {
   type: 'POST_COMMENT_OK',
   comment
 }
}
export const postCommentErr = (error) => {
 return {
   type: 'POST_COMMENT_ERR',
   error
 }
}

export const saveComment = (comment, taskId) => {
  return function(dispatch){

    dispatch(postComment())

    return axios.post('/task/' + taskId + '/comment', comment)
      .then(function (response) {
        // console.log(response);
        dispatch(postCommentOk(response.data))
        // refresh
        fetchTasks()(dispatch);
      })
      .catch(function (error) {
        console.error(error);
        dispatch(postCommentErr(response.data))
      });
  }
}
