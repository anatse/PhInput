import React, {Component, PropTypes} from 'react'

class StatusInformer extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        const {isLoading, lastUpdate} = this.props;
        if (isLoading) {
            return (
                <div className="status-info">
                    <img src={require('img/loading_32_purple_fast.gif')} />
                </div>
            )
        } else {
            return (
                <div className="status-info">
                    Обновлено: <br/>
                  {lastUpdate.toLocaleString()}
                </div>
            )
        }
    }

}

StatusInformer.propTypes = {
    isLoading: PropTypes.bool.isRequired
}

export default StatusInformer
