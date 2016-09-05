import React, {Component, PropTypes} from 'react'
import Select from 'react-select';
import 'react-select/dist/react-select.css';


const noop = function(val) { console.log(val) }

class UserSelect extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        const {value, options, onChange} = this.props;
        return (
          <Select
              name="user-select"
              value={value}
              options={options}
              onChange={onChange || noop}
              clearable={false}
              noResultsText={'Ничего не найдено'}
              placeholder={'Выберите...'}
          />
        )

    }

}
UserSelect.propTypes = {
    value: PropTypes.any,
    options: PropTypes.array.isRequired,
    onChange: PropTypes.func,
    taskId: PropTypes.string
}

export default UserSelect
