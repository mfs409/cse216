import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

// #region import
import '@picocss/pico/css/pico.min.css'
// #endregion import

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
