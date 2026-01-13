<template>
    <section>
        <h1>Person Details</h1>
        <label>Email</label>
        <input type="text" v-model="localState.email" disabled placeholder="Loading..." />
        <label>Name</label>
        <input type="text" v-model="localState.name" placeholder="Loading..." :disabled="!currentUser" />
        <p class="grid" v-if="currentUser">
            <button @click="update" :disabled="localState.buttonOff">Update</button>
        </p>
    </section>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';
import { globals } from "@/stores/globals";
import { onBeforeMount, reactive } from 'vue';
import { getCookieByName } from '@/helpers';

/** The Id of the person we're working with */
let id: string = useRoute().params.id as string;

/** Is the logged in user the person being displayed, able to edit? */
let currentUser = false;

/** Two-way binding to the template */
const localState = reactive({
    loaded: false,
    email: "",
    name: "",
    buttonOff: true,
});

/** Get the person's contents and put them in localState, so they'll display */
async function loadOneMessage() {
    let res = await fetch(`/people/${id}`, {
        method: 'GET',
        headers: { 'Content-type': 'application/json; charset=UTF-8' }
    });
    if (!res.ok) {
        globals().showPopup("Error", `The server replied: ${res.status}: ${res.statusText}`);
        return;
    }
    let json = await res.json();
    if (json.status === "ok") {
        localState.email = json.data.email;
        localState.name = json.data.name;
        localState.loaded = true;
        localState.buttonOff = false;
        currentUser = "" + json.data.id === getCookieByName("auth.id");
    } else {
        globals().showPopup("Error", json.message);
    }
};

/** Update the person's name */
async function update() {
    // Validate, then disable the button
    if (!localState.loaded) return;
    if (localState.name === "") {
        globals().showPopup("Error", "The name cannot be blank");
        return;
    }
    localState.buttonOff = true;

    // Now send the request, then re-enable the button
    let res = await fetch(`/people/`, {
        method: 'PUT',
        body: JSON.stringify({ name: localState.name }),
        headers: { 'Content-type': 'application/json; charset=UTF-8' }
    });
    localState.buttonOff = false;
    if (res.ok) {
        let json = await res.json();
        if (json.status === "ok") { globals().showPopup("Info", "Name successfully updated"); }
        else { globals().showPopup("Error", json.message); }
    } else {
        globals().showPopup("Error", `The server replied: ${res.status}\n` + res.statusText);
    }
}

onBeforeMount(loadOneMessage);
</script>