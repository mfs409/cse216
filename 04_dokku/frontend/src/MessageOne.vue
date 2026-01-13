<template>
    <section>
        <h1>Message Details</h1>
        <label>Subject</label>
        <input type="text" v-model="localState.subject" disabled placeholder="Loading..." />
        <label>Details</label>
        <textarea v-model="localState.details" placeholder="Loading..." :disabled="!creator"></textarea>
        <label>Created By</label>
        <input type="text" v-model="localState.creator" disabled placeholder="Loading..." />
        <label>As Of</label>
        <input type="text" v-model="localState.as_of" disabled placeholder="Loading..." />
        <p class="grid" v-if="creator">
            <button @click="update" :disabled="localState.buttonOff">Update</button>
            <button @click="del" :disabled="localState.buttonOff">Delete</button>
        </p>
    </section>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';
import { globals } from "@/stores/globals";
import { onBeforeMount, reactive } from 'vue';
import { router, Routes } from '@/router';
import { getCookieByName } from '@/helpers';

/** The Id of the message we're working with */
let id: string = useRoute().params.id as string;

/** Is the logged in user the message creator, able to edit/delete? */
let creator = false;

/** Two-way binding to the template */
const localState = reactive({
    loaded: false,
    subject: "",
    details: "",
    creator: "",
    as_of: "",
    buttonOff: true,
});

/** Get the message's contents and put them in localState, so they'll display */
async function loadOneMessage() {
    let res = await fetch(`/messages/${id}`, {
        method: 'GET',
        headers: { 'Content-type': 'application/json; charset=UTF-8' }
    });
    if (!res.ok) {
        globals().showPopup("Error", `The server replied: ${res.status}: ${res.statusText}`);
        return;
    }
    let json = await res.json();
    if (json.status === "ok") {
        localState.subject = json.data.subject;
        localState.details = json.data.details;
        localState.loaded = true;
        localState.buttonOff = false;
        localState.creator = `${json.data.name} (${json.data.email})`;
        localState.as_of = json.data.as_of;
        creator = `${json.data.creatorId}` === getCookieByName("auth.id");
    } else {
        globals().showPopup("Error", json.message);
    }
};

/** Update the message with the new details */
async function update() {
    // Validate, then disable the buttons
    if (!localState.loaded) return;
    if (localState.details === "") {
        globals().showPopup("Error", "The message cannot be blank");
        return;
    }
    localState.buttonOff = true;

    // Now send the request, then re-enable the button
    let res = await fetch(`/messages/${id}`, {
        method: 'PUT',
        body: JSON.stringify({ details: localState.details }),
        headers: { 'Content-type': 'application/json; charset=UTF-8' }
    });
    localState.buttonOff = false;
    if (res.ok) {
        let json = await res.json();
        if (json.status === "ok") { globals().showPopup("Info", "Message successfully updated"); }
        else { globals().showPopup("Error", json.message); }
    } else {
        globals().showPopup("Error", `The server replied: ${res.status}: ${res.statusText}`);
    }
}

/** Delete the message and return to the message listing */
async function del() {
    // Validate, then disable the buttons
    if (!localState.loaded) return;
    localState.buttonOff = true;

    // Send the request, then re-enable the button
    let res = await fetch(`/messages/${id}`, {
        method: 'DELETE',
        headers: { 'Content-type': 'application/json; charset=UTF-8' }
    })
    localState.buttonOff = false;
    if (res.ok) {
        let json = await res.json();
        if (json.status === "ok") {
            globals().showPopup("Info", `Message '${localState.subject}' deleted successfully.`);
            localState.subject = ""
            localState.details = "";
            router.replace(Routes.readMessageAll);
        } else {
            globals().showPopup("Error", json.message);
        }
    } else {
        globals().showPopup("Error", `The server replied: ${res.status}: ${res.statusText}`);
        localState.buttonOff = false;
    }
}

onBeforeMount(loadOneMessage);
</script>
