import React from 'react'
import Footer from './Footer'
import AddTodo from '../containers/AddTodo'
import VisibleTodoList from '../containers/VisibleTodoList'
import VisibleTaskList from '../containers/VisibleTaskList';

const App = () => (
  <div>
    <VisibleTaskList />
  </div>
)

/*

TODO list:
 - add comments chat
 - fix loading state
 - add common chat on the left/right
 - add websocket support
 - allow screenshots / files attaching
*/

/* <Footer />
<AddTodo />
<VisibleTodoList /> */

export default App
