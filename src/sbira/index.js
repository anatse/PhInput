import 'babel-polyfill'
import React from 'react'
import { render } from 'react-dom'
import { Provider } from 'react-redux'
import App from './components/App'
import {initWebSocket, addOnMessage} from './websocket'
import configureStore from './store/configureStore'
import {fetchTasks} from './actions'

import reactBootstrapCss from './styles/bootstrap.css';
import stylesCss from './styles/styles.scss';

// window.addEventListener("load", initWebSocket, false);

let store = configureStore()

render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById('root')
)

module.hot.accept();

// move this... somewhere
// addOnMessage((evt)=>{
//   console.log("onMsg:", evt.data);
//   let msg = "Получено событие об изменении данных. Обновимся ?";
//   switch (evt.data) {
//       case 'TASKS_UPDATED:DELETED':
//       case 'TASKS_UPDATED:COMMENT_ADDED':
//       case 'TASKS_UPDATED:CHANGED':
//       case 'TASKS_UPDATED:ADDED':
//           // if (confirm(msg)) fetchTasks();
//       default:
//
//   }
// })
