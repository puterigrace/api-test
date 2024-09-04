Feature: Book-store API

  Scenario: Get Books
    Given the API endpoint is available
    When I send a "GET" request to "/BookStore/v1/Books"
    Then the response status should be "200"
    And the response should contain "books"

  Scenario Outline: Get Book
    Given the API endpoint is available
    When I send a "GET" request to "/BookStore/v1/Books"
    Then the response status should be "200"
    And the response should contain "books"
    When I send a "GET" request to "/BookStore/v1/Book" with the following data
      | ISBN | <isbn>     |
    Then the response status should be "<get-book-status>"
    And the response should contain "<get-book-response>"
    Examples:
      | isbn    | get-book-status   | get-book-response |
      |         | 200               | isbn              |
      |wrondIsbn| 400               | ISBN supplied is not available in Books Collection!|

  Scenario Outline: Post Books
    Given the API endpoint is available
    When I send a "POST" request to "/Account/v1/User" with the following data
      | userName | <username>     |
      | password | Puges09% |
    Then the response status should be "201"
    And the response should contain "<username>"
    When I send a "POST" request to "/Account/v1/GenerateToken" with the following data
      | userName | <username>     |
      | password | Puges09% |
    Then the response status should be "200"
    And the response should contain "Success"
    When I send a "POST" request to "/Account/v1/Authorized" with the following data
      | userName | <username> |
      | password | Puges09% |
    Then the response should contain "true"
    When I send a "GET" request to "/BookStore/v1/Books"
    Then the response status should be "200"
    And the response should contain "books"
    When I send a "POST" request to "/BookStore/v1/Books" with the following data
      | userId | <userId> |
      | isbn   | <isbn>   |
      | token  | <token>  |
    Then the response status should be "<post-book-status>"
    And the response should contain "<post-book-response>"
    Examples:
      | userId      | isbn    | token    | post-book-status | post-book-response   |
      |             |         |          | 201              | books                |
      | wrongUserId |         |          | 401              | User Id not correct! |
      |             |wrongIsbn|          | 400              | ISBN supplied is not available in Books Collection! |
      |             |         |wrongToken| 401              | User not authorized! |

  Scenario Outline: Delete Books
    Given the API endpoint is available
    When I send a "POST" request to "/Account/v1/User" with the following data
      | userName | <username>     |
      | password | Puges09% |
    Then the response status should be "201"
    And the response should contain "<username>"
    When I send a "POST" request to "/Account/v1/GenerateToken" with the following data
      | userName | <username>     |
      | password | Puges09% |
    Then the response status should be "200"
    And the response should contain "Success"
    When I send a "POST" request to "/Account/v1/Authorized" with the following data
      | userName | <username> |
      | password | Puges09% |
    Then the response should contain "true"
    When I send a "GET" request to "/BookStore/v1/Books"
    Then the response status should be "200"
    And the response should contain "books"
    When I send a "DELETE" request to "/BookStore/v1/Books" with the following data
      | UserId | <userId> |
      | token  | <token>  |
    Then the response status should be "<delete-books-status>"
    And the response should contain "<delete-books-response>"
    Examples:
      | userId       | token      | delete-books-status | delete-books-response   |
      |              |            | 204              | books                |
      | wrongUserId  |            | 401              | User Id not correct! |
      |              | wrongToken | 401              | User not authorized! |


  Scenario Outline: Delete Book
    Given the API endpoint is available
    When I send a "POST" request to "/Account/v1/User" with the following data
      | userName | <username>     |
      | password | Puges09% |
    Then the response status should be "201"
    And the response should contain "<username>"
    When I send a "POST" request to "/Account/v1/GenerateToken" with the following data
      | userName | <username>     |
      | password | Puges09% |
    Then the response status should be "200"
    And the response should contain "Success"
    When I send a "POST" request to "/Account/v1/Authorized" with the following data
      | userName | <username> |
      | password | Puges09% |
    Then the response should contain "true"
    When I send a "GET" request to "/BookStore/v1/Books"
    Then the response status should be "200"
    And the response should contain "books"
    When I send a "POST" request to "/BookStore/v1/Books" with the following data
      | userId |  |
      | isbn   |  |
      | token  |  |
    When I send a "DELETE" request to "/BookStore/v1/Book" with the following data
      | userId | <userId> |
      | isbn   | <isbn>   |
      | token  | <token>  |
    Then the response status should be "<delete-book-status>"
    And the response should contain "<delete-book-response>"
    Examples:
      | userId       | token      | isbn      | delete-book-status  | delete-book-response |
      |              |            |           | 204                 | books                |
      | wrongUserId  |            |           | 401                 | User Id not correct! |
      |              | wrongToken |           | 401                 | User not authorized! |
      |              |            | wrongIsbn | 400                 | ISBN supplied is not available in User's Collection! |

  Scenario Outline: Put Books
    Given the API endpoint is available
    When I send a "POST" request to "/Account/v1/User" with the following data
      | userName | <username>     |
      | password | Puges09% |
    Then the response status should be "201"
    And the response should contain "<username>"
    When I send a "POST" request to "/Account/v1/GenerateToken" with the following data
      | userName | <username>     |
      | password | Puges09% |
    Then the response status should be "200"
    And the response should contain "Success"
    When I send a "POST" request to "/Account/v1/Authorized" with the following data
      | userName | <username> |
      | password | Puges09% |
    Then the response should contain "true"
    When I send a "GET" request to "/BookStore/v1/Books"
    Then the response status should be "200"
    And the response should contain "books"
    When I send a "POST" request to "/BookStore/v1/Books" with the following data
      | userId |  |
      | isbn   |  |
      | token  |  |
    When I send a "PUT" request to "/BookStore/v1/Books" with the following data
      | userId    | <userId>    |
      | newIsbn   | <newIsbn>   |
      | existIsbn | <existIsbn> |
      | token     | <token>     |
    Then the response status should be "<put-books-status>"
    And the response should contain "<put-books-response>"
    Examples:
      | userId       | token      | newIsbn   | existIsbn | put-books-status  | put-books-response   |
      |              |            |           |           | 200               | books                |
      | wrongUserId  |            |           |           | 401               | User Id not correct! |
      |              | wrongToken |           |         | 401               | User not authorized! |
      |              |            |            | wrongIsbn | 400               | ISBN supplied is not available in User's Collection! |
      |              |            | wrongIsbn  |           | 400               | ISBN supplied is not available in Books Collection! |
