<template>
    <section>
        <h2>All Messages</h2>
        <table>
            <thead>
                <tr>
                    <th scope="col">Title</th>
                    <th scope="col">Last Update</th>
                </tr>
            </thead>
            <tbody>
                <tr @click="click(elt.id)" v-for="elt in localState.data" :key="elt.id">
                    <td> {{ elt.subject }} </td>
                    <td> {{ elt.as_of }} </td>
                </tr>
            </tbody>
        </table>
        <div>As of {{ localState.when }}</div>
    </section>
</template>

<style scoped>
tr:hover td {
    cursor: pointer;
    background-color: #bbbbcc;
}
</style>

<script setup lang="ts">
import { Routes, router } from "@/router";
import { globals } from "@/stores/globals";
import { onBeforeMount, reactive } from "vue";

/** Two-way binding with the template */
const localState = reactive({
    /** A message indicating when the data was fetched */
    when: "(loading...)",
    /** The rows of data to display */
    data: [] as { id: number, subject: string, as_of: string }[]
});

/** Clicking a row should take us to the details page for that row */
function click(id: number) {
    router.replace(Routes.readMessageOne + "/" + id);
}

/** Get all of the messages and put them in localState, so they'll display */
async function fetchAllMessages() {
    let res = await fetch('/messages', {
        method: 'GET',
        headers: { 'Content-type': 'application/json; charset=UTF-8' }
    });
    if (!res.ok) {
        globals().showPopup("Error", `The server replied: ${res.status}: ${res.statusText}`);
        return;
    }
    let json = await res.json();
    if (json.status === "ok") {
        localState.data = json.data;
        localState.when = new Date().toString();
    } else {
        globals().showPopup("Error", json.message);
    }
};

onBeforeMount(fetchAllMessages);
</script>
