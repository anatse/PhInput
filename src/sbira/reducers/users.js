const users = (state = [], action) => {
    switch (action.type) {
        case 'RECEIVE_USERS_OK':
            return action.users
        default:
            return state
    }
}

export default users
