import React, {Component, PropTypes} from 'react'
import {Modal, Button, FormGroup, ControlLabel, FormControl} from 'react-bootstrap'
import SearchFilterCont from '../containers/SearchFilterCont'
import Calendar from 'rc-calendar'
import 'rc-calendar/assets/index.css';
import 'rc-time-picker/assets/index.css';
import CalendarLocale from '../utils/calendarRu';
import DatePicker from 'rc-calendar/lib/Picker';
import TimePickerPanel from 'rc-time-picker/lib/Panel';
import moment from 'moment'
import 'moment/locale/ru'

const newName = '';
const newContent = '';

const timeFormat = 'YYYY-MM-DD HH:mm:ss';
const defaultCalendarValue = moment().clone();
const timePickerElement = <TimePickerPanel />

const calendar = (<Calendar
  locale={CalendarLocale}
  style={{ zIndex: 1100 }}
  dateInputPlaceholder="Выберите дату и время..."
  formatter={timeFormat}
  timePicker={timePickerElement}
  showOk={true}
  defaultValue={defaultCalendarValue}
  showDateInput={true}
/>);

class AddTask extends Component {

    constructor(props) {
        super(props);
        this.state = {
            "deadLine": null,
            "name": newName,
            "content": newContent,
            "owner": 'user',
            "open": false
        }
    }

    onChangeName = (event) => {
        this.setState({name: event.target.value});
    }

    onChangeContent = (event) => {
        this.setState({content: event.target.value});
    }

    onChangeDeadline = (momentDate) => {
        this.setState({deadLine: +momentDate});
    }

    onAddClick = (e) => {
        e && e.preventDefault();
        console.log('onAddClick')
        if (!this.state.name || !this.state.content) return;
        this.props.onAddTask(this.state);
        this.setState({open: false});
    }

    open = () => {
        this.setState({open: true, "name": newName, "content": newContent})
    }

    close = () => {
        this.setState({open: false})
    }

    render() {
        const {onAddTask} = this.props;
        const addDisabled = this.state.name && this.state.content
            ? false
            : true;
        return (
            <div>
                <SearchFilterCont searchDelay={250} />
                <div className='add-task-btn'>
                    <Button bsSize="large" block onClick={this.open}>Добавить задачу</Button>
                </div>
                <Modal show={this.state.open} onHide={this.close}>
                    <Modal.Header closeButton>
                        <Modal.Title>Добавление новой задачи</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <div className='add-task-wrapper'>
                            <form onSubmit={this.onAddClick}>
                                <FormGroup controlId="name-input" key={1}>
                                    <ControlLabel>Имя</ControlLabel>
                                    <FormControl type="text" placeholder="Введите имя задачи (1-2 слова)... " value={this.state.name} onChange={this.onChangeName} autoComplete={'off'}/>
                                </FormGroup>
                                <FormGroup controlId="desc-input" key={2}>
                                    <ControlLabel>Описание</ControlLabel>
                                    <FormControl className='desc-input' componentClass="textarea" placeholder="Введите описание..." value={this.state.content} onChange={this.onChangeContent}/>
                                </FormGroup>
                                <FormGroup controlId="deadline-input" key={3}>
                                    <ControlLabel>Срок:</ControlLabel>
                                    <DatePicker
                                      animation="slide-up"
                                      calendar={calendar}
                                      value={this.state.deadLine && moment(this.state.deadLine) || null}
                                      onChange={this.onChangeDeadline}
                                    >{
                                        ({ value }) => {
                                          return (
                                            <FormControl className='deadline-input' placeholder="Выберите срок..." value={value && value.format(timeFormat) || ''} autoComplete={'off'} onChange={(e) => {
                                              e.preventDefault()
                                              console.log(e)
                                            }}/>
                                          );
                                        }
                                      }
                                    </DatePicker>
                                </FormGroup>
                            </form>
                        </div>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button key={1} onClick={this.close}>Закрыть</Button>
                        <Button key={2} onClick={this.onAddClick} disabled={addDisabled}>Добавить</Button>
                    </Modal.Footer>
                </Modal>
            </div>

        )
    }

}

export default AddTask
