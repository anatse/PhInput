 var statuses = {
     NEW: {
         name: "Новый",
         className: "new",
         btnText: "Создать",
         next: ['IN_PROGRESS', 'CLOSED'],
         filterHint: 'Новые',
     },
     IN_PROGRESS: {
         name: "В работе",
         className: "in-progress",
         btnText: "Взять в работу",
         next: ['DONE', 'CLOSED'],
         filterHint: 'В работе',
     },
     DONE: {
         name: "Готово",
         className: "done",
         btnText: "Выполнено",
         next: ['REOPENED', 'CLOSED'],
         filterHint: 'Готовые',
     },
     REOPENED: {
         name: "Переоткрыт",
         className: "reopened",
         btnText: "Переоткрыть",
         next: ['IN_PROGRESS', 'CLOSED'],
         filterHint: 'Переоткрытые',
     },
     CLOSED: {
         name: "Закрыт",
         className: "closed",
         btnText: "Закрыть",
         next: ['REOPENED'],
         filterHint: 'Закрытые',
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
 export function getStatusByName(statusName) {
     if (!statusName) return;
     for (let key in statuses) {
         const currentKey = statuses[key];
         if (currentKey.name.toLowerCase() == statusName.toLowerCase()) return currentKey;
     }
 }
 export default statuses;
