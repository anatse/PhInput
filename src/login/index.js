import 'babel-polyfill'
import React from 'react'
import { render } from 'react-dom'
import App from './components/App'

import stylesCss from './styles/styles.scss';

render(
    <App />,
  document.getElementById('root')
)

module.hot.accept();
