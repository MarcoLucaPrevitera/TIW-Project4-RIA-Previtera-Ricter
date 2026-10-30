# Tecnologies for Web course project - Banking Web-app

A full-stack Java web application for managing user accounts, performing bank transfers, and tracking transaction history.

## Overview

This project was built as a rich internet application using Java Servlets, JavaScript, HTML/CSS, and MySQL. It demonstrates end-to-end web development, including authentication, database modeling, server-side validation, and dynamic front-end interactions.

## Features

- User registration and login
- Account overview and balance display
- Transfer creation between accounts
- Transaction history and account details
- Address book with autocomplete
- Session-based access control
- AJAX-based UI updates
- MySQL persistence with DAO pattern

## Tech Stack

- Java EE / Servlets
- JavaScript
- HTML / CSS
- MySQL
- Apache Tomcat

## How to Run

1. Start MySQL and make sure the database is available.
2. Import the SQL schema from `money_transfer_db.sql`.
3. Update the database credentials in `src/main/webapp/WEB-INF/web.xml` if needed.
4. Deploy the project to an Apache Tomcat server.
5. Open the application in a browser:
   - `http://localhost:8080/<project-name>/index.html`


## Database Structure Overview

The application uses a MySQL database with four main tables:

| Table | Purpose | Key fields |
| --- | --- | --- |
| `user` | Stores user authentication and profile data | `id`, `username`, `name`, `surname`, `password` |
| `account` | Stores each user's bank account information | `id`, `code`, `balance`, `user` |
| `transfer` | Records every money transfer and balances before/after each transaction | `id`, `account_origin`, `account_dest`, `amount`, `motivation`, `date`, `balance_origin`, `balance_dest` |
| `address_book` | Stores saved contacts for each user | `user`, `contact_account` |

### Relationships

- Each `account` belongs to one `user`.
- Each `transfer` references two `account` records: origin and destination.
- Each `address_book` entry links a user to a contact account.

This schema supports user authentication, multiple accounts per user, account-to-account transfers, and contact management.
