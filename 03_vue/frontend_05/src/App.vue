<template>
  <main class="container">
    <Menu />
    <!-- #region template -->
    <Popup />
    <!-- #endregion template -->
    <RouterView />
  </main>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
import Menu from '@/Menu.vue'
// #region script
import Popup from '@/Popup.vue'
// #endregion script
</script>
