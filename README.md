# Smart Library Management System

A comprehensive Java-based Library Management System with GUI that allows efficient management of library resources, user accounts, and book transactions.

## Features

### Admin Features
- Manage librarians (Add/View/Delete)
- View fine reports (Individual and Monthly)
- Generate system reports
- Manage user accounts
- System monitoring

### Librarian Features
- Book management (Add/View/Delete)
- Issue and return books
- Track overdue books
- Manage student records
- Send notifications
- Process book requests

### Student Features
- Borrow and return books
- View borrowing status
- Request new books
- Place holds on books
- Reissue books
- View notifications

## Technical Requirements
- Java JDK 17 or higher
- MySQL Database
- Maven for dependency management

## Setup Instructions
1. Clone the repository
2. Configure the database connection in `src/main/resources/database.properties`
3. Run `mvn clean install` to build the project
4. Execute the main class `com.library.MainApplication`

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── library/
│   │           ├── controllers/
│   │           ├── models/
│   │           ├── views/
│   │           ├── utils/
│   │           └── database/
│   └── resources/
└── test/
    └── java/
        └── com/
            └── library/
```

## Contributing
Please read CONTRIBUTING.md for details on our code of conduct and the process for submitting pull requests.

## License
This project is licensed under the MIT License - see the LICENSE file for details