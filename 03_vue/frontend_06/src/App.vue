<template>
  <main class="container">
    <Menu />
    <Popup />
    <RouterView />
  </main>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
import Menu from '@/Menu.vue'
import Popup from '@/Popup.vue'
</script>
