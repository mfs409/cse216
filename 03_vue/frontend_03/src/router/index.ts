import { createRouter, createWebHistory } from 'vue-router';

import PersonAll from '@/PersonAll.vue';

/**
 * Routes helps avoid typing strings, so that we don't mis-type these in the
 * code
 */
export const Routes = {
  readPersonAll: "/pa",
  home: "/"
};

/** The router maps from addresses to components */
export const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: Routes.home, name: "Home", component: PersonAll },
    { path: Routes.readPersonAll, name: "ReadPersonAll", component: PersonAll },
  ]
});
