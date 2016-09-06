import { connect } from 'react-redux'
import { clearVisibilityFilter, clearSearchFilter } from '../actions'
import FilterInfo from '../components/FilterInfo'
import filterTasks from '../utils/utils'

const mapStateToProps = (state, ownProps) => {

  return {
    shownAmount:  filterTasks(state.tasks.tasks, state.visibilityFilter, state.searchFilter).length,
    totalAmount: state.tasks.tasks.length
  }
}

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    onClick: () => {
      dispatch(clearVisibilityFilter())
      dispatch(clearSearchFilter())
    }
  }
}

const FilterInfoCont = connect(
  mapStateToProps,
  mapDispatchToProps
)(FilterInfo)

export default FilterInfoCont
