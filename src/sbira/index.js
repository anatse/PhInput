import 'babel-polyfill'
import React from 'react'
import { render } from 'react-dom'
import { Provider } from 'react-redux'
import { createStore, applyMiddleware, compose } from 'redux'
import thunkMiddleware from 'redux-thunk'
import createLogger from 'redux-logger'
import rootReducer from './reducers'
import App from './components/App'
import { fetchTasks } from './actions'

import stylesCss from './styles/styles.scss';

const loggerMiddleware = createLogger()

let store;
if (process.env.NODE_ENV === 'production') {
  store = createStore(
    rootReducer,
      applyMiddleware(
        thunkMiddleware // lets us dispatch() functions
      )
  );
}else {
  store = createStore(
    rootReducer,
    compose(
      applyMiddleware(
        thunkMiddleware, // lets us dispatch() functions
        loggerMiddleware // neat middleware that logs actions
      ),
      // lets us use React Chrome dev tools plugin
      window.devToolsExtension && window.devToolsExtension()
    )
  );
}
if (module.hot) {
  // Enable Webpack hot module replacement for reducers
  module.hot.accept('./reducers', () => {
    const nextRootReducer = require('./reducers/index');
    store.replaceReducer(nextRootReducer);
  });
}

// store.dispatch(fetchTasks()).then(() =>
//   console.log(store.getState())
// )

render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById('root')
)

module.hot.accept();
