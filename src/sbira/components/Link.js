import React, { PropTypes } from 'react'

const Link = ({ active, onClick, title , statusClass }) => {
    return (
    <div className={'link ' + statusClass + ' ' + (active && ' active')} onClick={e => {
      e.preventDefault()
      onClick()
    }}>
      <div className='squareCheckbox' >
        <input type="checkbox" value="none" checked={active} onChange={e => {e.preventDefault()}} />
        <label htmlFor="squareCheckbox"></label>
      </div>
      <span className='title'> - {title}</span>
    </div>
  )
}

Link.propTypes = {
  active: PropTypes.bool.isRequired,
  onClick: PropTypes.func.isRequired,
  statusClass: PropTypes.string.isRequired,
  title: PropTypes.string.isRequired,
}

export default Link
