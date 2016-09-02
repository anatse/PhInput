import React, {Component, PropTypes} from 'react'
import {Modal, Button, FormGroup, ControlLabel, FormControl} from 'react-bootstrap'

const newName = '';
const newContent = '';

// function FieldGroup({ id, label, help, ...props }) {
//   return (
//     <FormGroup controlId={id}>
//       <ControlLabel>{label}</ControlLabel>
//       <FormControl {...props} />
//       {help && <HelpBlock>{help}</HelpBlock>}
//     </FormGroup>
//   );
// }

class AddTask extends Component {

    constructor(props) {
        super(props);
        this.state = {
            "deadLine": Date.now(),
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

    onAddClick = () => {
        console.log("onAddClick");
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
        return (
            <div>
                <div className='add-task-btn'>
                    <Button bsSize="large" block onClick={this.open}>Добавить задачу</Button>
                </div>
                <Modal show={this.state.open} onHide={this.close}>
                    <Modal.Header closeButton>
                        <Modal.Title>Добавление новой задачи</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <div className='add-wrapper'>
                            <FormGroup controlId="name-input" key={1}>
                                <ControlLabel>Имя</ControlLabel>
                                <FormControl type="text" placeholder="Введите имя задачи (1-2 слова)... " value={this.state.name} onChange={this.onChangeName}/>
                            </FormGroup>
                            <FormGroup controlId="desc-input" key={2}>
                                <ControlLabel>Описание</ControlLabel>
                                <FormControl bsSize="large" componentClass="textarea" placeholder="Введите описание..." value={this.state.content} onChange={this.onChangeContent}/>
                            </FormGroup>
                        </div>
                    </Modal.Body>
                    <Modal.Footer>
                        <Button key={1} onClick={this.close}>Закрыть</Button>
                        <Button key={2} onClick={this.onAddClick}>Добавить</Button>
                    </Modal.Footer>
                </Modal>
            </div>

        )
    }

}

export default AddTask
