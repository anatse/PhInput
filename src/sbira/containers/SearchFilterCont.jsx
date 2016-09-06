import { connect } from 'react-redux'
import { setSearchFilter } from '../actions'
import SearchFilter from '../components/SearchFilter'

const mapStateToProps = (state, ownProps) => {
  return {
    val: state.searchFilter
  }
}

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    onChange: (val) => {
      dispatch(setSearchFilter(val))
    }
  }
}

const SearchFilterCont = connect(
  mapStateToProps,
  mapDispatchToProps
)(SearchFilter)

export default SearchFilterCont
