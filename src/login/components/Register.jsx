import React, {Component, PropTypes} from 'react';
import axios from 'axios';
import {Alert, Modal, Button, FormGroup, ControlLabel, FormControl} from 'react-bootstrap';

class Register extends Component {

    constructor(props) {
        super(props);
        this.state = {
            "open": false,
            login: "",
            password: "",
            email: "",
            firstName: "",
            secondName: ""
        }
    }

    onAddClick = () => {
       this.setState({alertMessage: ""})
       this.createUser(this.state);
    }

    createUser = (user) => {
      let me = this;
      axios.post('/user/register', user)
        .then(function (response) {
          document.forms["registerForm"].submit();
        })
        .catch(function (error) {
          if(error.message.indexOf(409) != -1){
            me.setState({alertMessage: "Пользователь с таким именем или адресом существует"});
          }else{
            me.setState({alertMessage: "Попробуйте в другой раз"});
          }

        });
    }

    open = () => {
        this.setState({open: true, "name": "", "content": ""})
    }

    close = () => {
        this.setState({open: false})
    }

    onChangeLogin = (event) => {
        this.setState({login: event.target.value});
    }

    onChangePw = (event) => {
        this.setState({password: event.target.value});
    }

    onChangeEmail = (event) => {
        this.setState({email: event.target.value});
    }

    onChangeFirstName = (event) => {
        this.setState({firstName: event.target.value});
    }

    onChangeSecondName = (event) => {
        this.setState({secondName: event.target.value});
    }

    render() {


        const addDisabled = this.state.email && this.state.password && this.state.login ? false : true;
        const alert = this.state.alertMessage ? <Alert bsStyle="danger"><strong>Ошибка! </strong>{this.state.alertMessage}</Alert> : "";
        return (
            <div>
                <div className='register-btn'>
                    <Button bsStyle="info" onClick={this.open}>Зарегистрироваться</Button>
                </div>
                <Modal show={this.state.open} onHide={this.close}>
                    <Modal.Header closeButton>
                        <Modal.Title>Регистрация</Modal.Title>
                    </Modal.Header>
                    <Modal.Body>
                        <div className='add-task-wrapper'>
                            {alert}
                            <form onSubmit={this.onAddClick} action="/login" method="post" name="registerForm">
                                <FormGroup controlId="login-input" key={1}>
                                    <ControlLabel>Логин<span className="mandatory"> *</span></ControlLabel>
                                    <FormControl type="text" value={this.state.login} name="uname" onChange={this.onChangeLogin}/>
                                </FormGroup>
                                <FormGroup controlId="password-input" key={2}>
                                    <ControlLabel>Пароль<span className="mandatory"> *</span></ControlLabel>
                                    <FormControl type="password" value={this.state.password} name="upass" onChange={this.onChangePw}/>
                                </FormGroup>
                                <FormGroup controlId="email-input" key={3}>
                                    <ControlLabel>Почта<span className="mandatory"> *</span></ControlLabel>
                                    <FormControl type="text" value={this.state.email} onChange={this.onChangeEmail}/>
                                </FormGroup>
                                <FormGroup controlId="name-input" key={4}>
                                    <ControlLabel>Имя</ControlLabel>
                                    <FormControl type="text" value={this.state.firstName} onChange={this.onChangeFirstName}/>
                                </FormGroup>
                                <FormGroup controlId="surname-input" key={5}>
                                    <ControlLabel>Фамилия</ControlLabel>
                                    <FormControl type="text" value={this.state.secondName} onChange={this.onChangeSecondName}/>
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

export default Register
