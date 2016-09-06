import React, {Component, PropTypes} from 'react'
import {Button, Col, FormControl, FormGroup} from 'react-bootstrap'

class CommentCreator extends Component {

    constructor(props) {
        super(props)
        this.state = {
            comment: ''
        }
    }

    onAddComment = (e) => {
        e && e.preventDefault();
        if (!this.state.comment) return;
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
        const {taskId, onAddComment} = this.props;
        const addDisabled = this.state.comment
            ? false
            : true;
        return (
            <form className='create' onSubmit={this.onAddComment}>
                <FormGroup>
                    <div className='msg'>
                        <FormControl type="text" placeholder="Введите сообщение..." value={this.state.comment} onChange={(event) => {
                            this.setState({comment: event.target.value});
                        }} autoComplete='off'/>
                    </div>
                    <div className='add-comment-btn'>
                        <Button block onClick={this.onAddComment} disabled={addDisabled}>Добавить</Button>
                    </div>
                </FormGroup>
            </form>
        )
    }

}

CommentCreator.propTypes = {
    taskId: PropTypes.string.isRequired
}

export default CommentCreator
