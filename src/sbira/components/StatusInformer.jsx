import React, {Component, PropTypes} from 'react'
import moment from 'moment'

class StatusInformer extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        moment.locale('ru')
        const {isLoading, lastUpdate} = this.props;
        if (isLoading) {
            return (
                <div className="status-info">
                    <img src={require('img/loading_32_purple_fast.gif')}/>
                </div>
            )
        } else {
            return (
                <div className="status-info">
                    <a className='logout' href="/logout">Выход</a><br/>
                    Обновлено в {moment(lastUpdate).format('HH:mm:ss')}
                </div>
            )
        }
    }

}

StatusInformer.propTypes = {
    isLoading: PropTypes.bool.isRequired,
    lastUpdate: PropTypes.object,
}

export default StatusInformer
