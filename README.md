# Finance API

**Finance** is a specialized RESTful backend application designed to manage and control personal or business financial transactions.

The application is built to handle the complexities of financial data management by offering a structured way to store, retrieve, and filter operations, accounts, currencies, and users.


### Key Objectives:

    **Centralized Management:** Provides a robust system to Create, Read, Update, and Delete financial operations, accounts, and users within a unified database.

    **Advanced Filtering:** Includes specialized search capabilities to locate operations by account, currency, date range, user, or role.

    **Data Integrity & Security:** Implements a multi-layered architecture (Controller-Service-Repository) and uses Data Transfer Objects (DTOs) to ensure internal database structures are never directly exposed. Role-based access control is enforced via Spring Security (User and Role entities).

    **Code Quality Excellence:** Developed with a focus on industry standards, utilizing static analysis tools like SonarLint and Checkstyle to ensure clean, maintainable, and professional-grade Java code.

### Core Features:

    **Full Financial Lifecycle:** Complete control over accounts, income/expense operations, and multi-currency support.

    **Flexible Filtering:** Optimized query processing to find transactions based on various parameters (currency, account, date, user).

    **Modern Architecture:** Built on Spring Boot 4.0.3 and Java 21, with PostgreSQL as the primary database, ensuring high performance, reliability, and scalability.