const visibilityFilter = (state = [], action) => {
    switch (action.type) {
        case 'TOGGLE_VISIBILITY_FILTER':
            const index = state.indexOf(filter);
            if (index >= 0) {
              const newArr = [...state].splice(index, index + 1)
              return newArr
            } else {
                return [...state , filter]
            }
        default:
            return state
    }
}

export default visibilityFilter
