import React, {Component} from 'react'
import FilterLink from '../containers/FilterLink'
import FilterInfoCont from '../containers/FilterInfoCont'
import {getStatusesAsArray} from '../utils/taskStatuses'
import {Popover, OverlayTrigger} from 'react-bootstrap'


class FilterList extends Component {

    constructor(props) {
        super(props)
    }

    popoverHover = (
      <Popover title="Фильтр по статусу" id='filterHint'>
        <div className="txt"> Нажмите на соответствующий цвет для фильтрации задач:</div>
        {getStatusesAsArray().map(function(status, index){
           return  <div className='hint' key={index}><span className={'preview ' + status.className}></span><span className="msg"> - {status.filterHint}</span></div>
         })}
      </Popover>
    );

    render() {
      const statuses = getStatusesAsArray();
      return (
        <div className='filter-cont'>
        <OverlayTrigger trigger={['hover']} placement="left" overlay={this.popoverHover}>
          <div className='filters'>
           {statuses.map(function(status, index){
              return  <FilterLink filter={status}  key={index} className={'link '+ status.className} title={status.filterHint} />
            })}
          </div>
        </OverlayTrigger>

          <FilterInfoCont/>
        </div>
      )

    }

}
export default FilterList
