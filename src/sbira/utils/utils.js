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
