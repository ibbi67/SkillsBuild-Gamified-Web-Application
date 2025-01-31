# Group 20 - CO2302

## How to Setup your Development Environment

The project is split into two sections: the frontend, and the backend. The frontend is a React
application, and the backend is a Java Spring Boot application.

### Frontend

1. Install (Node.js)[https://nodejs.org/en/download]

    > You may see LTS (long term support) version, it is recommended to install that version. For mac
    > users, it is recommended to install Node.js using nvm (Node Version Manager), though, you can use
    > Homebrew as well. For windows users, you may find it easier to download the installer from the
    > official website.

2. Follow the README.md in the `frontend` directory

3. You're good to go!

### Backend

It is strongly suggested to use IntelliJ IDEA for the backend development.

1. Setup mySQL database with the following credentials: - username: root - password: secret - database: co2302

    > you may find it easier by running this docker command:
    > `docker run -d --name co2302 -p 3306:3306 -e MYSQL_ROOT_PASSWORD=secret -e MYSQL_DATABASE=co2302 mysql:8`

2. You're good to go!

## Tech Stack

### Frontend

- (React)[https://react.dev/], a library for building user interfaces, you should familiarise
  yourself with the general syntax, and key functions, such as `useState`. If you are new to react,
  you should follow the (tutorials)[https://react.dev/learn] on the official website.
- (Next.js)[https://nextjs.org/], a React framework for building static and server-side rendered
  applications, it links with React and makes it easier to build applications.
