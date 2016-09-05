import React, {Component, PropTypes} from 'react'

class FilterInfo extends Component {

    constructor(props) {
        super(props)
    }
    
    render() {
        const {shownAmount, totalAmount, onClick} = this.props;
        return (
            <div className='info'>
                Выбрано {shownAmount} из {totalAmount} <br/>
                {(shownAmount < totalAmount) &&
                  <a href="#" title='Нажмите, чтобы показать все' onClick={e => {
                      e.preventDefault();
                      onClick()
                  }}>
                      Показать все
                  </a>
                }
            </div>
        )

    }

}
FilterInfo.propTypes = {
    totalAmount: PropTypes.number.isRequired,
    onClick: PropTypes.func.isRequired
}

export default FilterInfo
