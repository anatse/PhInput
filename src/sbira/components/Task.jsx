import React, {PropTypes} from 'react'
import statuses, { getClassByStatus, getBtnTxtByStatus} from '../utils/taskStatuses'
import CommentList from './CommentList'

class Task extends React.Component {

  constructor(props) {
    super(props);
  }

  static propTypes = {
    task: PropTypes.object.isRequired,
    onChangeContent: PropTypes.func.isRequired,
    onSaveTask: PropTypes.func.isRequired,
    onDelTask: PropTypes.func.isRequired,
  }

  onSetNextStatus = (status) => {
    this.props.onSetNextStatus(status);
  }

  render() {
    const {task, onChangeContent, onSaveTask, onDelTask, onSetNextStatus, possibleStatuses, onAddComment} = this.props;
    const statusClass = getClassByStatus(task.status);
    return (
      <div className='task'>
          <div className={'status ' + statusClass} title={'Статус:' + task.status}></div>
          <div className='label'>
           <span className='id'>{task.id}: </span>
           <span className='name'>{task.name}</span>
          </div>
          <div className='content block'>
            <div>Описание:</div>
            <textarea value={task.content} onChange={onChangeContent}/>
          </div>
          <div className='comments block'>
            <CommentList taskId={task.id} comments={task.comments} onAddComment={onAddComment}/>
          </div>
          <div className='recent block'>
            <div className='changed'>Обновлён: <span>{task.changeDate}</span></div>
            <div className='deadline'>Срок: <span>{task.deadLine}</span></div>
            <div className='owner'>Ответственный:  <span>{task.owner}</span></div>
            { (possibleStatuses.length)?
              (<div className='progress-toolbar'>
                {possibleStatuses.map(function(possibleStatus, index){
                  return (<div key={index} className={'progress-btn ' + getClassByStatus(possibleStatus.name)}  onClick={() => { onSetNextStatus(possibleStatus.name) }} >{getBtnTxtByStatus(possibleStatus.name)}</div>)
                })}
              </div> ) : ''
            }
            <div className='btn-cont'>
              <div className='del-btn btn' onClick={onDelTask}>Удалить</div>
              <div className='save-btn btn' onClick={onSaveTask}>Сохранить</div>
            </div>
          </div>
      </div>
    )
  }

}

export default Task
