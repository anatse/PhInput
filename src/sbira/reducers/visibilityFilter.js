import {
    getStatusesAsArray
} from '../utils/taskStatuses'

const initialState = getStatusesAsArray()

const visibilityFilter = (state = initialState, action) => {
    switch (action.type) {
        case 'CLEAR_VISIBILITY_FILTER':
          return [...initialState];
        case 'TOGGLE_VISIBILITY_FILTER':
            const index = state.indexOf(action.filter);
            if (index >= 0) {
                let newArr = [...state];
                newArr.splice(index, 1);
                return newArr
            } else {
                return [...state, action.filter]
            }
        default:
            return state
    }
}

export default visibilityFilter
