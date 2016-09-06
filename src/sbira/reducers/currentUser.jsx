const currentUser = (state = null, action) => {
    switch (action.type) {
        case 'CURRENT_USER_OK':
            return action.user
        default:
            return state
    }
}

export default currentUser
