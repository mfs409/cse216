<template>
  <main class="container">
    <!-- #region template -->
    <Menu />
    <!-- #endregion template -->
    <RouterView />
  </main>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
// #region script
import Menu from '@/Menu.vue'
// #endregion script
</script>
