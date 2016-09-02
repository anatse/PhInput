import React, {Component, PropTypes} from 'react'
import {Button, Col, FormControl} from 'react-bootstrap'

class CommentCreator extends Component {

    constructor(props) {
        super(props)
        this.state = {
            comment: ''
        }
    }

    onAddComment = (e) => {
        e && e.preventDefault();
        const {taskId} = this.props;
        const comment = {
            owner: 'user',
            comment: this.state.comment,
            createDate: Date.now()
        }
        this.props.onAddComment(comment, taskId);
        this.setState({comment: ''})
    }

    render() {
        const {taskId, comments, onAddComment} = this.props;
        return (
            <form className='create' onSubmit={this.onAddComment}>
                <div className='msg'>
                    <FormControl type="text" placeholder="Введите сообщение..." value={this.state.comment} onChange={(event) => {
                        this.setState({comment: event.target.value});
                    }} autoComplete='off'/>
                </div>
                <div className='add-comment-btn'>
                    <Button block onClick={this.onAddComment}>Добавить</Button>
                </div>
            </form>
        )
    }

}

CommentCreator.propTypes = {
    taskId: PropTypes.string.isRequired

}

export default CommentCreator
