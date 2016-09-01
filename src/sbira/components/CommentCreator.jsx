import React, {Component, PropTypes} from 'react'

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
        owner:'user',
        comment: this.state.comment,
        createDate: Date.now()
      }
      this.props.onAddComment(comment, taskId);
      this.setState({comment:''})
    }

    render() {
        const {taskId, comments, onAddComment} = this.props;
        return (
          <div className='create'>
            <input value={this.state.comment} onChange={(event)=>{
                this.setState({comment:event.target.value});
              }}
              autoComplete='off' />
            <button onClick={this.onAddComment}>
              Добавить
            </button>
          </div>
        )
    }

}

CommentCreator.propTypes = {
    taskId: PropTypes.string.isRequired

}

export default CommentCreator
