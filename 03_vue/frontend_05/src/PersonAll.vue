<script setup lang="ts">
import { onBeforeMount, reactive } from "vue";
// #region import
import { globals } from "@/stores/globals";
// #endregion import
// #region import2
import { Routes, router } from "@/router";
// #endregion import2

/** Two-way binding with the template */
const localState = reactive({
    /** A message indicating when the data was fetched */
    when: "(loading...)",
    /** The rows of data to display */
    data: [] as { id: number, name: string }[]
});

/** Clicking a row should take us to the details page for that row */
function click(id: number) {
    // #region popup1
    // window.alert(id);
    globals().showPopup("Info", `${id}`)
    // #endregion popup1
    // #region popup4
    // window.alert(id);
    // globals().showPopup("Info", `${id}`)
    router.replace(Routes.readPersonOne + "/" + id);
    // #endregion popup4
}

/** Get all of the people and put them in localState, so they'll display */
async function fetchAllMessages() {
    let res = await fetch('/people', {
        method: 'GET',
        headers: { 'Content-type': 'application/json; charset=UTF-8' }
    });
    if (!res.ok) {
        // #region popup2
        // window.alert(`The server replied: ${res.status}: ${res.statusText}`);
        globals().showPopup("Error", `The server replied: ${res.status}: ${res.statusText}`);
        // #endregion popup2
        return;
    }
    let json = await res.json();
    if (json.status === "ok") {
        localState.data = json.data;
        localState.when = new Date().toString();
    } else {
        // #region popup3
        // window.alert(json.message);
        globals().showPopup("Error", json.message);
        // #endregion popup3
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

<style scoped>
tr:hover td {
    cursor: pointer;
    background-color: #bbbbcc;
}
</style>
