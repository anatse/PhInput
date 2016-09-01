import React, {Component, PropTypes} from 'react'

class AddTask extends Component {

    constructor(props) {
        super(props);
        this.state = {
            "deadLine": '20160901',
            "name": 'Новое имя',
            "content": 'Новое описание',
            "owner": 'user'
        }
    }

    onChangeName = (event) => {
      this.setState({name:event.target.value});
    }

    onChangeContent = (event) => {
      this.setState({content:event.target.value});
    }

    onAddClick = () => {
      console.log("onAddClick");
      this.props.onAddTask(this.state);
    }

    render() {
        const {onAddTask} = this.props;
        return (
            <div className='add-wrapper'>
                <div className='name-input'>
                  Имя:
                  <input type='text' value={this.state.name} onChange={this.onChangeName}  />
                </div>
                <div className='desc-input'>
                  Описание:
                  <textarea value={this.state.content} onChange={this.onChangeContent} />
                </div>
                <div className='add-btn' onClick={this.onAddClick}>Добавить</div>
            </div>
        )
    }

}

export default AddTask
