# my-notes-rest-auto
EN: A Java and Spring Boot project for taking notes using the REST API (server and client are implemented in the same application). 

The project implements a REST API with automatically generated endpoints for the Note entity. At the same time: 
1. REST endpoints provide a complete list of data operations (CRUD), including updating via PUT and PATCH requests; 
2. A logical deletion operation has been implemented, accessible by the "Status" link in the list of notes, which changes the value of the IsDeleted field of the note object; 
3. Physical deletion, as one of the CRUD operations, is only available for note objects with the true value of the IsDeleted field; 
4. in the list of notes, the functions of pagination of records and data filtering are implemented, which work consistently; 
5. The function of uploading a list of notes in the form of a report to a PDF file is also implemented (this function uploads data to the report only about those notes that are available according to the filtering and pagination criteria at the time of uploading the report).

P.S.: This version of the application implements both server and client functions. 

/*----------------------------------------------------------------------------------------------*/

RU: Проект на языке Java и Spring Boot по учету заметок с использованием REST API (сервер и клиент реализованы в одном приложении).

Проект реализует REST API с автоматически генерируемыми конечными точками для сущности Note (Заметка). При этом: 
1. конечные точки REST предоставляют полный перечень операций с данными (CRUD), в том числе обновление посредством запросов PUT и PATCH; 
2. реализована операция логического удаления, доступная по ссылке "Статус" в списке заметок, которая изменяет значение поля isDeleted объекта заметки; 
3. физическое удаление, как одна из операций CRUD, доступна только для объектов заметок с истинным значением поля isDeleted; 
4. в списке заметок реализованы функции пострачниного просмотра записей и фильтрации данных, которые работают согласованно; 
5. также реализована функция выгрузки списка заметок в виде отчета во веншний файл формата PDF (данная функция выгружает в отчет данные только о тех заметках, которые доступны в соответствии с критериями фильтрации и постраничного просмотра на момент выгрузки отчета).

P.S.: Данная версия приложения реализует одновременно функции сервера и клиента. 
