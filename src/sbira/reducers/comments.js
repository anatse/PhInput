const comment = (state, action) => {
  switch (action.type) {
    case 'ADD_COMMENT':
      return {
        "owner": null,
        "comment": null,
        "createDate": 1472560923635
      }
    default:
      return state
  }
}

const comments = (state = [], action) => {
  switch (action.type) {
    case 'ADD_COMMENT':
      return [
        ...state,
        comment(undefined, action)
      ]
    default:
      return state
  }
}

export default comments
