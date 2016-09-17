import statuses from '../utils/taskStatuses'

const task = (state, action) => {
    switch (action.type) {
        case 'EDIT_TASK':
            return Object.assign( {}, state, action.editedPart );
        default:
            return state
    }
}

const tasks = (state = [], action) => {
    switch (action.type) {
        case 'RECEIVE_TASKS_OK':
            return action.tasks
        case 'RECEIVE_TASKS_ERR':
            // TODO
        case 'EDIT_TASK':
            return state.map(function(item){
                if ( (item.id) == action.id ) {
                  return task(item, action)
                }else {
                  return item;
                }
              })
        default:
            return state
    }
}

export default tasks
