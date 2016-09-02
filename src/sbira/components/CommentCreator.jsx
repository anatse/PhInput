import React, {Component, PropTypes} from 'react'
import {Button, Col, FormControl} from 'react-bootstrap'

class CommentCreator extends Component {

    constructor(props) {
        super(props)
        this.state = {
            comment: ''
        }
    }

    onAddComment = () => {
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
            <div className='create'>
                <Col sm={8}>
                    <FormControl type="text" placeholder="Введите сообщение..." value={this.state.comment} onChange={(event) => {
                        this.setState({comment: event.target.value});
                    }} autoComplete='off'/>
                </Col>
                <Col sm={4}>
                    <Button onClick={this.onAddComment}>Добавить</Button>
                </Col>
            </div>
        )
    }

}

CommentCreator.propTypes = {
    taskId: PropTypes.string.isRequired

}

export default CommentCreator
