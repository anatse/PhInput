import React, {Component, PropTypes} from 'react'

class Author extends Component {

    constructor(props) {
        super(props)
    }

    render() {
        return (
            <div className='owner'>
                Автор:
                <span> {this.props.authorName} </span>
            </div>
        )
    }

}

Author.propTypes = {
    authorName: PropTypes.string.isRequired
}

export default Author
