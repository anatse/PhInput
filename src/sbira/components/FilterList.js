import React, {Component} from 'react'
import FilterLink from '../containers/FilterLink'
import FilterInfoCont from '../containers/FilterInfoCont'
import {getStatusesAsArray} from '../utils/taskStatuses'

class FilterList extends Component {

    constructor(props) {
        super(props)
    }

    render() {
      const statuses = getStatusesAsArray();
      return (
        <div className='filter-cont'>
          <div className='filters'>
           {statuses.map(function(status, index){
              return  <FilterLink filter={status}  key={index} className={'link '+ status.className} title={status.filterHint} />
            })}
          </div>
          <FilterInfoCont/>
        </div>
      )

    }

}
export default FilterList
