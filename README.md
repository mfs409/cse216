# CSE 216 Software Engineering: Mini Tutorials

This repository hosts the revised (Spring 2026) tutorial for CSE 216: Software Engineering.
This tutorial aims to provide a *quick* on-ramp that covers the basic technologies (the "tech stack") used in CSE 216.

The tech stack covered by this tutorial is sufficient for building a scalable web application.
It consists of a database (initially SQLite, eventually PostgreSQL), a command-line program for managing the database (written in Java), a web server (written in Java, using the Javalin framework), and a web front end (using Vue.js and TypeScript).
These technologies were chosen because they balance pedagogical needs, diverse student backgrounds, and the goals of CSE 216.

In redesigning the tutorial, special emphasis was placed on *removing* background material.
The hope is that students will explore background material as appropriate.

- For some, their existing background is strong, and they need little of the content we were providing.
  We hope this quickstart will be more efficient and rewarding than the prior tutorial.
- For others, the background material was overwhelming.
  We hope that this quickstart makes it easier to digest the essential material that they need in order to be an effective team member in CSE 216.

Regardless of student background, we believe it remains important that students have pointers to deeper dives into key topics.
Often, a link suffices.
When a contextualized discussion is needed, or where the on-line resources are too formal for the understanding the students need, the tutorial favors presenting the material through footnotes.
We hope this prevents the core of each chapter from becoming too cluttered.

## Chapters

There are four chapters:

1. 01_databases: This chapter covers the basics of databases.
   All examples use SQLite.
   Students then use Maven to create a program that interacts with a SQLite database.
   This program they write will be the primary administrative interface to their web app.
2. 02_javalin: This chapter introduces the Javalin framework.
   It shows how to build an RESTful API server that interacts with a database.
   It also manages OAuth (via Google).
   The program students write should be secure, free of concurrency bugs, and free of resource leaks.
3. 03_vue: This chapter discusses how to create a frontend using Vue.js and TypeScript.
   It uses pico.css as its styling engine.
   This chapter also requires some edits to the Javalin program from Chapter 2, so that it can serve files.
4. 04_dokku: This chapter deals with modifying the artifacts from the previous chapters, so that they can run in the cloud.
   This includes transitioning from SQLite to PostgreSQL.
   Unlike the prior chapters, this chapter is heavily tailored to the CSE 216 environment.
   It will probably be useful to non-Lehigh students who are working on Heroku and other PaaS providers, but they are not a focus for now.

## Organization

The final artifact created by students involves three programs:

- Admin: A Java program for one-off database and user management tasks
- Backend: A Java-based web server
- Frontend: A TypeScript-based web front end

The outcome for students is that they should have three sibling folders in their class repository, representing these three programs.

In order to keep the tutorials well-scoped and sane, this repository does not have top-level folders for these artifacts.
Instead, it has a folder for the material relevant to each tutorial: `01_database`, `02_javalin`, `03_vue`, and `04_dokku`.
Each of these may have *multiple* folders for `admin`, `backend`, and `frontend`, so that the tutorial markdown can import code instead of duplicating it.
These folder names employ a numbering scheme, which expresses chronological progression through a chapter.
The folder whose name lacks a number is the final version.

## Contacts

These tutorials were developed by [Michael Spear](spear@lehigh.edu) and [Stephen Lee-Urban](sml3@lehigh.edu).
If you notice any bugs or encounter any issues, please contact both of us, and we'll try to respond as quickly as possible.
