import React, {PropTypes} from 'react'
import statuses, {getClassByStatus, getBtnTxtByStatus} from '../utils/taskStatuses'
import CommentList from './CommentList'
import {ButtonToolbar, ButtonGroup, Button, FormGroup, FormControl, ControlLabel} from 'react-bootstrap';

class Task extends React.Component {

    constructor(props) {
        super(props);
    }

    static propTypes = {
        task: PropTypes.object.isRequired,
        onChangeContent: PropTypes.func.isRequired,
        onSaveTask: PropTypes.func.isRequired,
        onDelTask: PropTypes.func.isRequired
    }

    onSetNextStatus = (status) => {
        this.props.onSetNextStatus(status);
    }

    render() {
        const {
            task,
            onChangeContent,
            onSaveTask,
            onDelTask,
            onSetNextStatus,
            possibleStatuses,
            onAddComment
        } = this.props;
        const statusClass = getClassByStatus(task.status);
        return (
            <div className='task'>
                <div className={'status ' + statusClass} title={'Статус:' + task.status}></div>
                <div className='info'>
                    <span className='id'>{task.id}:
                    </span>
                    <span className='name'>{task.name}</span>
                </div>
                <div className='content block'>
                  <FormGroup  >
                    <ControlLabel>Описание</ControlLabel>
                    <FormControl componentClass="textarea" placeholder="Введите описание задачи..." value={task.content} onChange={onChangeContent} />
                  </FormGroup>
                </div>
                <div className='comments block'>
                    <CommentList taskId={task.id} comments={task.comments || []} onAddComment={onAddComment}/>
                </div>
                <div className='recent block'>
                    <div className='changed'>Обновлён:
                        <span>{task.changeDate}</span>
                    </div>
                    <div className='deadline'>Срок:
                        <span>{task.deadLine}</span>
                    </div>
                    <div className='owner'>Ответственный:
                        <span>{task.owner}</span>
                    </div>

                    {(possibleStatuses.length)
                        ? (
                            <div className='progress-toolbar'>
                                <ButtonToolbar>
                                    <ButtonGroup bsSize="small">
                                        {possibleStatuses.map(function(possibleStatus, index) {
                                            return (
                                                <Button key={index} className={getClassByStatus(possibleStatus.name)} onClick={() => {
                                                    onSetNextStatus(possibleStatus.name)
                                                }}>{getBtnTxtByStatus(possibleStatus.name)}</Button>
                                            )
                                        })}
                                    </ButtonGroup>
                                </ButtonToolbar>
                            </div>
                        )
                        : ''}
                    <div className='btn-cont'>
                        <ButtonToolbar>
                            <ButtonGroup>
                                <Button key={1} onClick={onDelTask} bsStyle="danger">Удалить</Button>
                                <Button key={2} onClick={onSaveTask} bsStyle="success">Сохранить</Button>
                            </ButtonGroup>
                        </ButtonToolbar>
                    </div>
                </div>
            </div>
        )
    }

}

export default Task
