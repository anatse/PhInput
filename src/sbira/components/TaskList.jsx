import React, {Component, PropTypes} from 'react'
import Task from './Task'
import AddTask from './AddTask'
import StatusInformer from './StatusInformer'
import { getNext } from '../utils/taskStatuses'

class TaskList extends Component {

    constructor(props) {
        super(props)
    }

    componentWillMount() {
        const {fetchTasks, getUsersList} = this.props;
        // FIXME: have to init user list first, so we can render selects in task.
        getUsersList().then(fetchTasks());
    }

    render() {
        const {isLoading, lastUpdate, tasks, onChange, onSaveTask, onAddTask, onDelTask, onAddComment} = this.props;
        return (
          <div>
            <StatusInformer isLoading={isLoading} lastUpdate={lastUpdate}/>
            <div className='tasks'>
                <AddTask onAddTask={onAddTask} isLoading={isLoading} />
                {tasks.map(task => <Task
                  key={task.id} task={task}
                  onAddComment={onAddComment}
                  onChangeContent={(event) => {
                    const newValue = event.target.value;
                    onChange(task.id, {content: newValue});
                  }}
                  
                  onSaveTask={() => {
                    onSaveTask(task);
                  }}
                  onDelTask={() => {
                    onDelTask(task.id);
                  }}
                  onSetNextStatus={(status)=>{
                    onChange(task.id, {status: status});
                  }}
                  possibleStatuses={getNext(task.status)}
                  />
                )}
            </div>
          </div>
        )
    }

}

TaskList.propTypes = {
    tasks: PropTypes.array.isRequired,
    isLoading: PropTypes.bool.isRequired,
    getUsersList: PropTypes.func.isRequired,
    fetchTasks: PropTypes.func.isRequired,
}

export default TaskList
