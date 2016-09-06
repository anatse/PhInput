import React from 'react'
import VisibleTaskList from '../containers/VisibleTaskList';
import FilterList from '../components/FilterList';
import StatusInformerCont from '../containers/StatusInformerCont'
import SearchFilterCont from '../containers/SearchFilterCont'

const App = () => (
  <div>
    <FilterList />
    <StatusInformerCont />
    <VisibleTaskList />
  </div>
)

/*

TODO list:
 - add common chat on the left/right
 - add websocket support
 - allow screenshots / files attaching

*/

export default App
