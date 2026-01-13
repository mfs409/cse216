<script setup lang="ts">
import { onBeforeMount, reactive } from "vue";
import { globals } from "@/stores/globals";
import { Routes, router } from "@/router";

/** Two-way binding with the template */
const localState = reactive({
    /** A message indicating when the data was fetched */
    when: "(loading...)",
    /** The rows of data to display */
    data: [] as { id: number, name: string }[]
});

/** Clicking a row should take us to the details page for that row */
function click(id: number) {
    router.replace(Routes.readPersonOne + "/" + id);
}

/** Get all of the people and put them in localState, so they'll display */
async function fetchAllMessages() {
    let res = await fetch('/people', {
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

<template>
    <section>
        <h2>All People</h2>
        <table>
            <thead>
                <tr>
                    <th scope="col">Name</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="elt in localState.data" :key="elt.id" @click="click(elt.id)">
                    <td> {{ elt.name }} </td>
                </tr>
            </tbody>
        </table>
        <div>As of {{ localState.when }}</div>
    </section>
</template>

<style>
tr:hover td {
    cursor: pointer;
    background-color: #bbbbcc;
}
</style>
