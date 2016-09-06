import React, {Component, PropTypes} from 'react'
import {FormControl} from 'react-bootstrap'
class SearchFilter extends Component {

    constructor(props) {
        super(props)
    }

    onChange = (e) => {
        e.preventDefault();
        this.props.onChange(e.target.value)
    }

    render() {
        return <FormControl className='search' type="text" placeholder="Поиск по имени, описанию и статусу..." value={this.props.val} onChange={this.onChange} autoComplete='off'/>
    }

}

SearchFilter.propTypes = {
    onChange: PropTypes.func.isRequired,
    val: PropTypes.string.isRequired
}

export default SearchFilter
