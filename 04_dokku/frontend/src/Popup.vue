<template>
    <dialog ref="popup">
        <article>
            <header>
                <button aria-label="Close" rel="prev" @click="globals().clearPopup()"></button>
                <p> <strong>{{ globals().popup.header }}</strong> </p>
            </header>
            <p>{{ globals().popup.msg }}</p>
        </article>
    </dialog>
</template>

<script setup lang="ts">
import { globals } from "@/stores/globals";
import { onMounted, ref } from "vue";

// Get the <dialog> and give it to the globals, so that it can show/hide it
const popup = ref(null);
onMounted(() => globals().popup.element = popup);
</script>
