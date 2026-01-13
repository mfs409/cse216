import { createRouter, createWebHistory } from 'vue-router'
import PersonAll from '@/PersonAll.vue'
import MessageAll from '@/MessageAll.vue'
import MessageOne from '@/MessageOne.vue'
import MessageCreate from '@/MessageCreate.vue'
import PersonOne from '@/PersonOne.vue'

/**
 * Routes helps avoid typing strings, so that we don't mis-type these in the
 * code
 */
export const Routes = {
  createMessage: "/cm",
  readMessageAll: "/ma",
  readMessageOne: "/m1",
  readPersonAll: "/pa",
  readPersonOne: "/p1",
  home: "/"
};

/** The router maps from addresses to components */
export const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: Routes.home, name: "Home", component: MessageAll },
    { path: Routes.readPersonAll, name: "ReadPersonAll", component: PersonAll },
    { path: Routes.createMessage, name: "CreateMessage", component: MessageCreate },
    { path: Routes.readMessageAll, name: "ReadMessageAll", component: MessageAll },
    { path: Routes.readMessageOne + '/:id', name: "ReadMessageOne", component: MessageOne, props: true },
    { path: Routes.readPersonOne + '/:id', name: "ReadPersonOne", component: PersonOne, props: true },
  ]
});
