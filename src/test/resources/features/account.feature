Feature: Account API


  Scenario Outline: Fail to create an account with invalid password
    Given the API endpoint is available
    When I send a "POST" request to "/Account/v1/User" with the following data
      | userName | <username>     |
      | password | <password> |
    Then the response status should be "<response>"
    And the response should contain "Passwords must have at least one non alphanumeric character, one digit ('0'-'9'), one uppercase ('A'-'Z'), one lowercase ('a'-'z'), one special character and Password must be eight characters or longer."

    Examples:
      | username    | password      | response |
      | invaliduser | short         | 400      |
      | invaliduser | test%         | 400      |
      | invaliduser | 12345678      | 400      |
      | invaliduser | NoSpecial1    | 400      |
      | invaliduser | NoSpecialChar | 400      |
      | invaliduser | nospecial123  | 400      |
      | invaliduser | NOLOWER123$   | 400      |
      | invaliduser | Test1234      | 400      |

  Scenario Outline: Create new user
    Given the API endpoint is available
    When I send a "POST" request to "/Account/v1/User" with the following data
      | userName | <username>     |
      | password | <password> |
    Then the response status should be "<response>"

    Examples:
      | username   | password | response |
      | <username> | Puges09% | 201      |

  Scenario Outline: Create new user - user exist
    Given the API endpoint is available
    When I send a "POST" request to "/Account/v1/User" with the following data
      | userName | <username> |
      | password | <password> |
    Then the response status should be "<response>"
    When I send a "POST" request to "/Account/v1/User" with the following data
      | userName | <username>     |
      | password | <password> |
    Then the response status should be "<user-exist-response>"

    Examples:
      | username   | password | response | user-exist-response|
      | <username> | Puges09% | 201      | 406                |


  Scenario Outline: Generate a token
    Given the API endpoint is available
    When I send a "POST" request to "/Account/v1/User" with the following data
      | userName | <username> |
      | password | <password> |
    Then the response status should be "<create-user-response>"
    When I send a "POST" request to "/Account/v1/GenerateToken" with the following data
      | userName | <username>     |
      | password | <generate-token-password> |
    Then the response status should be "<generate-token-response>"
    And the response should contain "<status>"


    Examples:
      | username   | password | create-user-response | generate-token-password | generate-token-response | status   |
      | <username> | Puges09% | 201                  | Puges09%                | 200                     | Success  |
      | <username> | Puges09% | 201                  | wrongPassword           | 200                     | Failed   |
      | <username> | Puges09% | 201                  |                         | 400                     |          |


  Scenario Outline: Authorize user
    Given the API endpoint is available
    When I send a "POST" request to "/Account/v1/User" with the following data
      | userName | <username>     |
      | password | <password> |
    Then the response status should be "<create-user-response>"
    When I send a "POST" request to "/Account/v1/GenerateToken" with the following data
      | userName | <username>     |
      | password | <generate-token-password> |
    Then the response status should be "<generate-token-response>"
    And the response should contain "<status-token>"
    When I send a "POST" request to "/Account/v1/Authorized" with the following data
      | userName | <authorize-username> |
      | password | <authorize-password> |
    And the response should contain "<status>"

    Examples:
      | username   | password | create-user-response | generate-token-password | generate-token-response | status-token | authorize-username | authorize-password | status |
      | <username> | Puges09% | 201                  | Puges09%                | 200                     | Success      | <username>         |Puges09%           | true   |
      | <username> | Puges09% | 201                  | wrongPassword           | 200                     | Failed       | <username>         |Puges09%           | false  |
      | <username> | Puges09% | 201                  | Puges09%                | 200                     | Success      | <username>         |                   | {\"code\":\"1200\",\"message\":\"UserName and Password required.\"}|
      | <username> | Puges09% | 201                  | Puges09%                | 200                     | Success      | wrongUsername      |Puges09%           | {\"code\":\"1207\",\"message\":\"User not found!\"}|

  Scenario Outline: Get user
    Given the API endpoint is available
    When I send a "POST" request to "/Account/v1/User" with the following data
      | userName | <username>     |
      | password | <password> |
    Then the response status should be "<create-user-response>"
    And the response should contain "<username>"
    When I send a "POST" request to "/Account/v1/GenerateToken" with the following data
      | userName | <username>     |
      | password | <generate-token-password> |
    Then the response status should be "<generate-token-response>"
    And the response should contain "<status-token>"
    When I send a "POST" request to "/Account/v1/Authorized" with the following data
      | userName | <authorize-username> |
      | password | <authorize-password> |
    Then the response should contain "<status>"
    When I send a "GET" request to "/Account/v1/User/%s" with the following data
      | userId | <userID> |
    Then the response status should be "<get-user-status>"
    And the response should contain "<get-user-response>"

    Examples:
      | username   | password | create-user-response | generate-token-password | generate-token-response | status-token | authorize-username | authorize-password | status | get-user-status| get-user-response    | userID      |
      | <username> | Puges09% | 201                  | Puges09%                | 200                     | Success      | <username>         |Puges09%            | true   | 200            |                      |             |
      | <username> | Puges09% | 201                  | Puges09%                | 200                     | Success      | <username>         |Puges09%            | true   | 401            | User not found!      | wrongUserId |


  Scenario Outline: Delete user
    Given the API endpoint is available
    When I send a "POST" request to "/Account/v1/User" with the following data
      | userName | <username>     |
      | password | <password> |
    Then the response status should be "<create-user-response>"
    And the response should contain "<username>"
    When I send a "POST" request to "/Account/v1/GenerateToken" with the following data
      | userName | <username>     |
      | password | <generate-token-password> |
    Then the response status should be "<generate-token-response>"
    And the response should contain "<status-token>"
    When I send a "POST" request to "/Account/v1/Authorized" with the following data
      | userName | <authorize-username> |
      | password | <authorize-password> |
    Then the response should contain "<authorized-status>"
    When I send a "DELETE" request to "/Account/v1/User/%s" with the following data
      | userId | <userID> |
    Then the response status should be "<delete-user-status>"
    And the response should contain "<delete-user-response>"
    And wait for "10" second(s)

    Examples:
      | username   | password | create-user-response | generate-token-password | generate-token-response | status-token | authorize-username | authorize-password | authorized-status | delete-user-status | delete-user-response | userID      |
      | <username> | Puges09% | 201                  | Puges09%                | 200                     | Success      | <username>         |Puges09%            | true              | 200                | User Id not correct! | wrongUserId |
      | <username> | Puges09% | 201                  | Puges09%                | 200                     | Success      | <username>         |Puges09%            | true              | 204                |                      |             |