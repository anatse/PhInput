
const searchFilter = (state = '', action) => {
    switch (action.type) {
        case 'CLEAR_SEARCH_FILTER':
          return '';
        case 'SET_SEARCH_FILTER':
                return action.filter
        default:
            return state
    }
}

export default searchFilter
