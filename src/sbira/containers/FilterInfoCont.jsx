import { connect } from 'react-redux'
import { clearVisibilityFilter } from '../actions'
import FilterInfo from '../components/FilterInfo'

const filterTasks = (tasks, filters) => {
  return tasks.filter(t => {
    return filters.some( f =>{
      return t.status == f.name
    })
  })
}

const mapStateToProps = (state, ownProps) => {

  return {
    shownAmount:  filterTasks(state.tasks.tasks, state.visibilityFilter).length,
    totalAmount: state.tasks.tasks.length
  }
}

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    onClick: () => {
      dispatch(clearVisibilityFilter())
    }
  }
}

const FilterInfoCont = connect(
  mapStateToProps,
  mapDispatchToProps
)(FilterInfo)

export default FilterInfoCont
