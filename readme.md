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

## Setup steps
_**Prerequisites:** tomcat 9+ and java 11+_

The project uses in-memory database (H2) so the process is quite straightforward:

1. Clone the project:
`git clone https://github.com/Tornaviss/lunch-vote.git`

2. Open the project using an IDE

3. Run the app with Tomcat Server configuration with context path `mealvote` (DB will be automatically initialized and populated with a test data)

That's it! Now you are able to test available endpoints with [curl commands](https://github.com/Tornaviss/lunch-vote/blob/master/config/curl.md).

Feel free to use URI `/docs` for api usage guide with examples.