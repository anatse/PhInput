import React, {PropTypes} from 'react'
import statuses, {getStatusByName} from '../utils/taskStatuses'
import CommentList from './CommentList'
import AuthorCont from '../containers/AuthorCont'
import {
    ButtonToolbar,
    ButtonGroup,
    Button,
    FormGroup,
    FormControl,
    ControlLabel,
    ListGroup,
    ListGroupItem
} from 'react-bootstrap';
import moment from 'moment';
import UserSelectCont from '../containers/UserSelectCont';

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

    dateFormatter = (date) => {
        moment.locale('ru');
        const momentDate = moment(date);
        const isSameDay = momentDate.isSame(Date.now(), 'day');
        return (isSameDay)
            ? momentDate.format('Сегодня в HH:mm:ss')
            : momentDate.format('Do MMMM YYYY в HH:mm:ss');
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
        const status = getStatusByName(task.status);
        const statusClass = status.className;
        return (
            <div className='task'>
                <div className={'status ' + statusClass} title={'Статус:' + task.status}></div>
                <div className='info' title={task.id + ': ' + task.name}>
                    <span className='id'>{task.id}:
                    </span>
                    <span className='name'>{task.name}</span>
                </div>
                <div className='content block'>
                    <FormGroup >
                        <ControlLabel>Описание</ControlLabel>
                        <FormControl componentClass="textarea" placeholder="Введите описание задачи..." value={task.content} onChange={onChangeContent}/>
                    </FormGroup>
                </div>
                <div className='comments block'>
                    <CommentList taskId={task.id} comments={task.comments || []} onAddComment={onAddComment}/>
                </div>
                <div className='recent block'>
                    <ListGroup>
                        <ListGroupItem>
                            <div className='changed'>
                                Обновлён:
                                <span>
                                    {this.dateFormatter(task.changeDate)}</span>
                            </div>
                        </ListGroupItem>
                        <ListGroupItem>
                            <div className='deadline'>
                                Срок:
                                <span>
                                    {this.dateFormatter(task.deadLine)}</span>
                            </div>
                        </ListGroupItem>
                        <ListGroupItem>
                            <AuthorCont authorLogin={task.owner} />
                        </ListGroupItem>
                        <ListGroupItem>
                            <div className='assigned'>
                                <div className='assigned-label'>Ответственный:</div>
                                <UserSelectCont value={task.assignedPerson} taskId={task.id}/>
                            </div>
                        </ListGroupItem>
                    </ListGroup>
                    {(possibleStatuses.length)
                        ? (
                            <div className='progress-toolbar'>
                                <ButtonToolbar>
                                    <ButtonGroup bsSize="small">
                                        {possibleStatuses.map(function(possibleStatus, index) {
                                            return (
                                                <Button key={index} className={possibleStatus.className} onClick={() => {
                                                    onSetNextStatus(possibleStatus)
                                                }}>{possibleStatus.btnText}</Button>
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
