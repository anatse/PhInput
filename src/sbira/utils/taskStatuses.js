 var statuses = {
     NEW: {
         name: "Новый",
         className: "new",
         btnText: "Создать",
         next: ['IN_PROGRESS', 'CLOSED'],
         filterHint: 'Вкл/Выкл фильтрацию новых задач',
     },
     IN_PROGRESS: {
         name: "В работе",
         className: "in-progress",
         btnText: "Взять в работу",
         next: ['DONE', 'CLOSED'],
         filterHint: 'Вкл/Выкл фильтрацию задач в работе',
     },
     DONE: {
         name: "Готово",
         className: "done",
         btnText: "Выполнено",
         next: ['REOPENED', 'CLOSED'],
         filterHint: 'Вкл/Выкл фильтрацию готовых задач',
     },
     REOPENED: {
         name: "Переоткрыт",
         className: "reopened",
         btnText: "Переоткрыть",
         next: ['IN_PROGRESS', 'CLOSED'],
         filterHint: 'Вкл/Выкл фильтрацию переоткрытых задач',
     },
     CLOSED: {
         name: "Закрыт",
         className: "closed",
         btnText: "Закрыть",
         next: ['REOPENED'],
         filterHint: 'Вкл/Выкл фильтрацию закрытых задач',
     }
 };
 export function getStatusesAsArray() {
     let arr = [];
     for (let key in statuses) {
         let current = statuses[key];
         current.key = key;
         arr.push(current);
     }
     return arr;
 }
 export function getClassByStatus(statusName) {
     if (!statusName) return;
     for (let key in statuses) {
         const currentKey = statuses[key];
         if (currentKey.name.toLowerCase() == statusName.toLowerCase()) return currentKey.className;
     }
 }
 export function getNext(statusName) {
     if (!statusName) return;
     let found = false;
     for (let key in statuses) {
         const currentKey = statuses[key];
         if (currentKey.name.toLowerCase() == statusName.toLowerCase()) {
             return currentKey.next.map(function(item) {
                 return statuses[item];
             });
         }
     }
 }
 export function getBtnTxtByStatus(statusName) {
     for (let key in statuses) {
         const currentKey = statuses[key];
         if (currentKey.name.toLowerCase() == statusName.toLowerCase()) return currentKey.btnText;
     }
 }
 export default statuses;
