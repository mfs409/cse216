<template>
    <section>
        <h2>Create a Message</h2>
        <label>Subject</label>
        <input type="text" v-model="localState.subject" placeholder="Your subject" />
        <label>Details</label>
        <textarea v-model="localState.message" placeholder="Message details"></textarea>
        <button @click="create" :disabled="localState.buttonOff">Create</button>
        <div>{{ localState.status }}</div>
    </section>
</template>

<script setup lang="ts">

import { router, Routes } from "@/router";
import { globals } from "@/stores/globals";
import { reactive } from "vue";

/** Two-way binding with the template */
const localState = reactive({
    subject: "",
    message: "",
    buttonOff: false,
    status: "",
});

/** Create the message*/
async function create() {
    // Validate, then disable the button and update the status
    if (localState.subject === "") {
        globals().showPopup("Error", "The subject cannot be blank");
        return;
    }
    if (localState.message === "") {
        globals().showPopup("Error", "The message cannot be blank");
        return;
    }
    localState.buttonOff = true;
    localState.status = "Posting message with subject '" + localState.subject + "'";

    // Now send the request, then re-enable the button
    let res = await fetch('/messages', {
        method: 'POST',
        body: JSON.stringify({ subject: localState.subject, details: localState.message }),
        headers: { 'Content-type': 'application/json; charset=UTF-8' }
    });
    localState.status = "";
    localState.buttonOff = false;
    if (res.ok) {
        let json = await res.json();
        if (json.status === "ok") {
            globals().showPopup("Info", `Message '${localState.subject}' created successfully.`);
            router.replace(Routes.readMessageAll);
        }
        else {
            globals().showPopup("Error", json.message);
        }
    } else {
        globals().showPopup("Error", `The server replied: ${res.status}: ${res.statusText}`);
    }
}
</script>
