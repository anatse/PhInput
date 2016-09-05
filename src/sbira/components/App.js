import React from 'react'
import VisibleTaskList from '../containers/VisibleTaskList';
import FilterList from '../components/FilterList';

const App = () => (
  <div>
    <FilterList />
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
