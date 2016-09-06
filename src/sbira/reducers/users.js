import {
    getUserDisplayName
} from '../utils/utils'

const users = (state = [], action) => {
    switch (action.type) {
        case 'RECEIVE_USERS_OK':
            return action.users.map(user => {
                user.displayName = getUserDisplayName(user)
                return user
            })
        default:
            return state
    }
}

export default users
