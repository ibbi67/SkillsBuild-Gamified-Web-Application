# Group 20 - CO2302

## How to Setup your Development Environment

The project is split into two sections: the frontend, and the backend. The frontend is a React
application, and the backend is a Java Spring Boot application.

### Frontend

1. Install [Node.js](https://nodejs.org/en/download)

    > You may see LTS (long term support) version, it is recommended to install that version. For mac
    > users, it is recommended to install Node.js using nvm (Node Version Manager), though, you can use
    > Homebrew as well. For windows users, you may find it easier to download the installer from the
    > official website.

2. run `npm install` (or `npm` as a shortcut) to install the dependencies
    > Ensure commands are run when you are at the frontend directory, not this directory
3. run `npm run dev` to start the development server
    > You can checkout other scripts in the `package.json` file
4. You're good to go!

### Backend

It is strongly suggested to use IntelliJ IDEA for the backend development.

1. Setup mySQL database with the following credentials: - username: root - password: secret - database: co2302
    > you may find it easier by running this docker command:
    > `docker run -d --name co2302 -p 3306:3306 -e MYSQL_ROOT_PASSWORD=secret -e MYSQL_DATABASE=co2302 mysql:8`
2. You're good to go!

## Tech Stack

### Frontend

- [React](https://react.dev/), a library for building user interfaces, you should familiarise
  yourself with the general syntax, and key functions, such as `useState`. If you are new to react,
  you should follow the [tutorials](https://react.dev/learn) on the official website.

- [Next.js](https://nextjs.org/), a React framework for building static and server-side rendered
  applications, it links with React and makes it easier to build applications. The key functionality
  you use is the routing, which exists on the src/app directory. Checkout the docs regarding how to
  [create a new page](https://nextjs.org/docs/basic-features/pages).

- [Tailwind CSS](https://tailwindcss.com/), a utility-first CSS framework, it is used to style the
  application. You should familiarise yourself with the
  [docs](https://tailwindcss.com/docs/styling-with-utility-classes) to understand how to use it. You
  should also use a [cheat sheet](https://nerdcave.com/tailwind-cheat-sheet) if you are new to
  Tailwind CSS.

### REST API

REST API is a way to communicate between the frontend and the backend. This is done using HTTP
methods, such as GET, POST, PUT, DELETE.

### Backend

This is a typical Java Spring Boot application, you should refer to your previous and ongoing
classes.

## Standards to Follow

1. You must create a new branch from `main` for each feature you are working on. The branch name
   should be descriptive to the feature you are working on, it shouldn't have any spaces, and should
   be in lowercase.
2. You must create a pull request to merge your branch into `main`. Ensure that the code is
   reviewed by at least one other person before merging.
3. You must write tests for your code, and ensure that the tests pass before creating a pull
   request.
4. You must write a descriptive commit message, and ensure that the commit message follows the
   [conventional commit](https://www.conventionalcommits.org/en/v1.0.0/) format.
    > Quick examples: - `feat: add new feature` - `fix: fix a bug` - `docs: update README.md`
5. Remember to commit often, and branch often. It is easier to merge small changes than large
   changes.
