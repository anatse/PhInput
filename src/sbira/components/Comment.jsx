import React, {Component, PropTypes} from 'react'
import moment from 'moment'

class Comment extends Component {

    constructor(props) {
        super(props)
    }

    dateFormatter = (date) => {
        moment.locale('ru');
        const momentDate = moment(date);
        const isSameDay = momentDate.isSame(Date.now(), 'day');
        return (isSameDay)
            ? momentDate.format('HH:mm:ss')
            : momentDate.format('HH:mm:ss, Do MMMM YYYY');
    }

    render() {
        const {owner, comment, createDate} = this.props;
        const date = new Date(createDate);
        return (
            <div className='comment'>
                <span className='time'>{this.dateFormatter(date)}</span>
                &nbsp;-&nbsp;
                <span className='author'>{owner}</span>
                &nbsp;:&nbsp;
                <span className="text"> {comment}</span>
            </div>
        )
    }

}

Comment.propTypes = {
    owner: PropTypes.string,
    comment: PropTypes.string,
    createDate: PropTypes.any
}

export default Comment
