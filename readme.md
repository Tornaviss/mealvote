[![Codacy Badge](https://api.codacy.com/project/badge/Grade/41adb8a830ce41c29ee93eb9129a79f7)](https://www.codacy.com/manual/Tornaviss/lunch-vote?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Tornaviss/lunch-vote&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.org/Tornaviss/lunch-vote.svg?branch=master)](https://travis-ci.org/Tornaviss/lunch-vote)

Mealvote Rest API ([Topjava](https://javaops.ru/view/topjava) graduation project)
===============================
 
Simple REST voting system for deciding where to have lunch today.

**Technology stack:**
* Maven
* Spring MVC
* Spring Security
* Spring Data JPA
* Hibernate
* REST(Jackson)
* JUnit & Spring Test

<details>
<summary>Click to see the project task</summary>

Design and implement a REST API using Hibernate/Spring/SpringMVC (or Spring-Boot) without frontend.

The task is:

Build a voting system for deciding where to have lunch.

* 2 types of users: admin and regular users
* Admin can input a restaurant and it's lunch menu of the day (2-5 items usually, just a dish name and price)
* Menu changes each day (admins do the updates)
* Users can vote on which restaurant they want to have lunch at
* Only one vote counted per user
* If user votes again the same day:
    * If it is before 11:00 we asume that he changed his mind.
    * If it is after 11:00 then it is too late, vote can't be changed
    
Each restaurant provides new menu each day.

As a result, provide a link to github repository.

It should contain the code and **README.md with API documentation and curl commands to get data for voting and vote**.

</details>

## Setup steps
_**Prerequisites:** tomcat 9+ and java 11+_

The project uses in-memory database (H2) so the process is quite straightforward:

1. Clone the project:
`git clone https://github.com/Tornaviss/lunch-vote.git`

2. Open the project using an IDE

3. Enter `mvn package` in an IDE console at least once to trigger maven `package` lifecycle event, thus running all of unit tests and generating api usage guide

<details>
<summary>Click if you experience an issue with the step</summary>

There may be a problem related to excessive log output into console. Just append your command just like that:

`mvn clean package > log-file.log` 

Now all the output will be stored in root of the project as separate log file so the problem will be solved.

</details>

4. Run the app with Tomcat Server configuration with context path `mealvote` (DB will be automatically initialized and populated with a test data)

That's it! Feel free to use URI `/docs` to see the api usage guide with examples.

Test available endpoints with curl commands listed below.

<details>
<summary>CURL samples</summary>

Application deployed in application context `mealvote`
> For windows use `Git Bash`

#### get All Users
`curl -s http://localhost:8080/mealvote/admin/users --user admin@gmail.com:admin`

#### get Users 100001
`curl -s http://localhost:8080/mealvote/admin/users/100001 --user admin@gmail.com:admin`

#### get Users by email
`curl -s http://localhost:8080/mealvote/admin/users/by?email=user@yandex.ru --user admin@gmail.com:admin`

#### update Users 100000
`curl -s -X PUT -d '{"name":"updatedUser","email":"updated@gmail.com","password":"updatedPass","roles":["ROLE_USER"]}' -H 'Content-Type: application/json' http://localhost:8080/mealvote/admin/users/100000 --user admin@gmail.com:admin`

#### create Users
`curl -s -X POST -d '{"name":"newUser","email":"newUser@gmail.com","password":"newpass","roles":["ROLE_USER"]}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/mealvote/admin/users --user admin@gmail.com:admin`

#### delete Users
`curl -s -X DELETE http://localhost:8080/mealvote/admin/users/100000 --user admin@gmail.com:admin`
___

#### register Profile
`curl -s -X POST -d '{"name":"newUser","email":"newUser@gmail.com","password":"newpass"}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/mealvote/profile/register`

#### get Profile
`curl -s http://localhost:8080/mealvote/profile --user user@yandex.ru:password`

#### update Profile
`curl -s -X PUT -d '{"name":"updatedUser","email":"updated@gmail.com","password":"updatedPass"}' -H 'Content-Type: application/json' http://localhost:8080/mealvote/profile --user user@yandex.ru:password`

#### delete Profile
`curl -s -X DELETE http://localhost:8080/mealvote/profile --user user@yandex.ru:password`
___

#### get Vote
`curl -s http://localhost:8080/mealvote/profile/vote --user user@yandex.ru:password`

#### create Vote: choose Restaurants 100002
`curl -s -X POST http://localhost:8080/mealvote/profile/vote?restaurantId=100002 --user admin@gmail.com:admin`

#### update Vote: choose Restaurants 100003
`curl -s -X PUT http://localhost:8080/mealvote/profile/vote?restaurantId=100002 --user user@yandex.ru:password`
___

#### create Restaurants
`curl -s -X POST -d '{"name":"Genazvale&Khinkali"}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/mealvote/restaurants --user admin@gmail.com:admin`

#### get Restaurants 100002
`curl -s http://localhost:8080/mealvote/restaurants/100002 --user user@yandex.ru:password`

#### get Restaurants 100002 with Menu
`curl -s http://localhost:8080/mealvote/restaurants/100002?includeMenu=true --user user@yandex.ru:password`

#### get Restaurants 100002 with Votes
`curl -s http://localhost:8080/mealvote/restaurants/100002?includeVotes=true --user user@yandex.ru:password`

#### get Restaurants 100002 with Menu and Votes
`curl -s http://localhost:8080/mealvote/restaurants/100002?includeMenu=true&includeVotes=true --user user@yandex.ru:password`

#### get All Restaurants
`curl -s http://localhost:8080/mealvote/restaurants --user user@yandex.ru:password`

#### get All Restaurants with Menu
`curl -s http://localhost:8080/mealvote/restaurants?includeMenu=true --user user@yandex.ru:password`

#### get All Restaurants with Votes
`curl -s http://localhost:8080/mealvote/restaurants?includeVotes=true --user user@yandex.ru:password`

#### get All Restaurants with Menu and Votes
`curl -s http://localhost:8080/mealvote/restaurants?includeMenu=true&includeVotes=true --user user@yandex.ru:password`

#### update Restaurants 100003
`curl -s -X PUT -d '{"id":100003,"name":"Shaurma"}' -H 'Content-Type: application/json' http://localhost:8080/mealvote/restaurants/100003 --user admin@gmail.com:admin`

#### delete Restaurants 100003
`curl -s -X DELETE http://localhost:8080/mealvote/restaurants/100003 --user admin@gmail.com:admin`
___

#### create Menus for Restaurants 100004
`curl -s -X POST -d '{"dishes":[{"name":"kartoshka","price":"300"},{"name":"kompot","price":100}]}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/mealvote/restaurants/100004/menu --user admin@gmail.com:admin`

#### get Menus 100002
`curl -s http://localhost:8080/mealvote/menus/100002 --user user@yandex.ru:password`

#### update Menus 100002
`curl -s -X PUT -d '{"dishes":[{"name":"baked pork meat","price":"10000"},{"name":"red vine","price":800}]}' -H 'Content-Type: application/json' http://localhost:8080/mealvote/menus/100002 --user admin@gmail.com:admin`

#### delete Menus 100002
`curl -s -X DELETE http://localhost:8080/mealvote/menus/100002 --user admin@gmail.com:admin`

___

#### create Dishes for Menus 100002
`curl -s -X POST -d '{"name":"Toni Papperoni","price":1000}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/mealvote/menus/100002/dishes --user admin@gmail.com:admin`

#### get Dishes 100005
`curl -s http://localhost:8080/mealvote/dishes/100005 --user user@yandex.ru:password`

#### get All Dishes
`curl -s http://localhost:8080/mealvote/dishes --user user@yandex.ru:password`

#### update Dishes 100006
`curl -s -X PUT -d '{"name":"updatedDish","price":100}' -H 'Content-Type: application/json' http://localhost:8080/mealvote/dishes/100006 --user admin@gmail.com:admin`

#### delete Dishes 100006
`curl -s -X DELETE http://localhost:8080/mealvote/dishes/100006 --user admin@gmail.com:admin`

</details>