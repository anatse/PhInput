const tasksAjaxStatus = (state ={isLoading:true, lastUpdate: null}, action) => {
    switch (action.type) {
        case 'REQUEST_TASKS':
        case 'SAVE_EDITED_TASK':
        case 'SAVE_NEW_TASK':
        case 'DELETE_TASK':
        case 'POST_COMMENT':
            return {...state, isLoading: true}
        case 'RECEIVE_TASKS_OK':
        case 'RECEIVE_TASKS_ERR':
        case 'SAVE_EDITED_TASK_OK':
        case 'SAVE_EDITED_TASK_ERR':
        case 'DELETE_TASK_OK':
        case 'DELETE_TASK_ERR':
        case 'POST_COMMENT_OK':
        case 'POST_COMMENT_ERR':
            return {...state, lastUpdate: new Date(), isLoading: false}
        default:
            return state
    }
}

export default tasksAjaxStatus
