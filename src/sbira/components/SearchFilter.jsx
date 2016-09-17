import React, {Component, PropTypes} from 'react'
import {FormControl} from 'react-bootstrap'
class SearchFilter extends Component {

    constructor(props) {
        super(props)
        this.state={
          timerId: null,
          val: ''
        }
    }

    onChange = (e) => {
        e.preventDefault();
        this.state.timerId && clearTimeout(this.state.timerId)
        const filterValue = e.target.value
        const timerId = setTimeout(() => {this.props.onChange(filterValue)}, this.props.searchDelay)
        this.setState({val:filterValue,timerId})
    }

    componentWillReceiveProps (nextProps){
      this.setState({val:nextProps.val})
    }

    render() {
        return <FormControl className='search' type="text" placeholder="Поиск по имени, описанию и статусу..." value={this.state.val} onChange={this.onChange} autoComplete='off'/>
    }

}

SearchFilter.propTypes = {
    onChange: PropTypes.func.isRequired,
    val: PropTypes.string.isRequired,
    searchDelay: PropTypes.number.isRequired,
}

export default SearchFilter
