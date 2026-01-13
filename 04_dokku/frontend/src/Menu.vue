<template>
    <nav>
        <ul>
            <li><a @click="allMessage">Messages</a></li>
            <li><a @click="createMessage">Create Message</a></li>
            <li><a @click="allPeople">People</a></li>
        </ul>
        <ul>
            <li><a @click="logout">Log out</a></li>
        </ul>
    </nav>
</template>

<style>
li {
    cursor: pointer;
}
</style>

<script setup lang="ts">
import { Routes, router } from "@/router";

/** Clicking "People" shows all the people */
function allPeople() {
    // If we're on "People" and click "People", we need a refresh (via go(0))
    if (router.currentRoute.value.path == Routes.readPersonAll) { router.go(0); }
    else { router.replace(Routes.readPersonAll); }
}

/** Clicking "Messages" shows all the messages */
function allMessage() {
    // If we're on "Messages" and click "Messages", we need a refresh (via go(0))
    if (router.currentRoute.value.path == Routes.readMessageAll) { router.go(0); }
    else { router.replace(Routes.readMessageAll); }
}

/** Navigate to the page for creating a message. */
function createMessage() {
    router.replace(Routes.createMessage);
}

/** Log out and then redirect to home, which will force a refresh/login */
async function logout() {
    await fetch("/logout", { method: 'GET' });
    (window.location as any) = "/"; // trigger a refresh
}
</script>
