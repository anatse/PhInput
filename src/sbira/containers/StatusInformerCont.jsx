import { connect } from 'react-redux'
import StatusInformer from '../components/StatusInformer'

const mapStateToProps = (state, ownProps) => {
  const {lastUpdate, isLoading} = state.tasks.status;
  return {
    lastUpdate,
    isLoading
  }
}


const StatusInformerCont = connect(
  mapStateToProps
)(StatusInformer)

export default StatusInformerCont
