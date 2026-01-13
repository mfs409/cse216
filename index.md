---
# https://vitepress.dev/reference/default-theme-home-page
layout: home

hero:
  name: "CSE 216 Quick-Start Tutorial"
  text: "Build an Interactive Website with Vue and Javalin"
  actions:
    - theme: brand
      text: Introduction To Databases
      link: /01_databases/databases
    - theme: brand
      text: Web Back-Ends With Javalin
      link: /02_javalin/javalin
    - theme: brand
      text: A Front-End With Vue.js And Pico.css
      link: /03_vue/vue
    - theme: brand
      text: Deploying To The Cloud
      link: /04_dokku/dokku
---

# Welcome

The CSE 216 quick-start tutorial is designed to help you familiarize yourself with the core technologies that are used in the class CSE 216, Software Engineering, at Lehigh University.
Remember that CSE 216 is not a class about building interactive web-based systems.
It is a class about software engineering and the development process.
This tutorial will show you how to build a secure and correct web application, so that you have the right foundation to then learn how to be a productive member of an agile development process.

If you intend to do all of the parts of this tutorial, you will need the following software.
It is all available on the "sunlab" systems in the Lehigh University CSE department.
If you are working on your own computer, you will need to install them yourself.[^docker]

- An editor, such as Visual Studio Code
- The Java Development Kit (jdk) version 17 or later
- Node.js version 21 or later
- The Node Package Manager (npm) version 11 or later
- A handful of command-line tools: bash, git, maven, sqlite3, and postgresql

[^docker]:
    If you are comfortable using Docker, [this Dockerfile](/Dockerfile.txt) is a bare-bones set-up with everything you will need.

As you work through this tutorial, please do not try to *speed-run* the tutorial by copying and pasting code.
You are not expected to type everything by hand, but you should read through all of the code and annotate it with comments that document your understanding.
You do not need to understand every single line, but if you do not develop a good understanding of most of the code, the rest of the semester will be much harder.

You may sometimes think that the code in the tutorial is more complicated than it needs to be.
The tutorials try not to do anything unnecessary.
However, there are subtle issues in programs like this, particularly with regard to security, resource management, and thread safety.
The goal in these tutorials is to do everything the **right way**, so that it's a good foundation for any projects you undertake in the future.
If you notice any bugs, please notify your professor right away.

Hopefully you will discover places where you want to dive deeper, and learn more about how things really work.
When those times arise, please reach out to your professor and TAs.
They're happy to help you learn more!

## Footnotes
