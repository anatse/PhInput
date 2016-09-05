import React, { PropTypes } from 'react'

const Link = ({ active, onClick, className, title }) => {
    return (
    <div title={title} className={className + ' squareCheckbox ' + (active && 'active')} onClick={e => {
      e.preventDefault()
      onClick()
    }}>
      <input type="checkbox" value="none" checked={active} onChange={e => {
        e.preventDefault()
      }} />
      <label htmlFor="squareCheckbox"></label>
    </div>
  )
}

Link.propTypes = {
  active: PropTypes.bool.isRequired,
  onClick: PropTypes.func.isRequired,
  className: PropTypes.string.isRequired,
  title: PropTypes.string.isRequired,
}

export default Link
