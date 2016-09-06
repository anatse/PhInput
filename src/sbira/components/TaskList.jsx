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
        const {fetchTasks, getUsersList, getCurrentUser} = this.props;
        // FIXME: have to init user list first, so we can render selects in task.
        getUsersList().then(fetchTasks());
        getCurrentUser();
    }

    render() {
        const {tasks, onChange, onSaveTask, onAddTask, onDelTask, onAddComment} = this.props;
        return (
          <div>
            <div className='tasks'>
                <AddTask onAddTask={onAddTask} />
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
                    onChange(task.id, {status: status.name});
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
    getUsersList: PropTypes.func.isRequired,
    fetchTasks: PropTypes.func.isRequired,
}

export default TaskList
