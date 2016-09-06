export default function filterTasks (tasks, visibilityFilters, searchFilter) {
  return tasks.filter(t => {
    return visibilityFilters.some( f =>{
      return t.status == f.name
    })
  }).filter(t => {
    const lowerCasedFilter = searchFilter.toLowerCase();
    return t.name.toLowerCase().indexOf(lowerCasedFilter)>=0 || t.content.toLowerCase().indexOf(lowerCasedFilter)>=0 || t.status.toLowerCase().indexOf(lowerCasedFilter)>=0
  })
}

export function getUserDisplayName (user) {
  const {secondName, firstName} = user
  if (secondName && firstName) {
    return secondName + ' ' + firstName
  }else if (!secondName && firstName) {
    return firstName
  }else if (secondName && !firstName){
    return secondName
  }else {
    return user.login
  }
}
