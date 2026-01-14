// Replace `import { defineConfig } from 'vitepress'` so we can use mermaid
import { withMermaid } from "vitepress-plugin-mermaid";
import footnote from "markdown-it-footnote";

// NB:  For more details, see <https://vitepress.dev/reference/site-config>
export default withMermaid({
  title: "CSE 216 QuickStart Tutorial",
  description: "Build an Interactive Website with Vue and Javalin",
  markdown: {
    math: true,
    config: (md) => md.use(footnote),
    // theme: "vitesse-dark"
  },
  outDir: "./dist",
  base: "/cse216/",
  ignoreDeadLinks: true,
  themeConfig: {
    // Use `sidebar` for things that show while you're *in* the docs, but not on
    // the landing page.
    sidebar: [
      {
        text: 'Chapters',
        items: [
          { text: '1. Introduction To Databases', link: '/01_databases/databases' },
          { text: '2. Web Back-Ends With Javalin', link: '/02_javalin/javalin' },
          { text: '3. A Front-End With Vue.js And Pico.css', link: '/03_vue/vue' },
          { text: '4. Deploying To The Cloud', link: '/04_dokku/dokku' },
        ]
      }
    ]
  },
  // Change the folder where the vitepress cache goes, because it doesn't need
  // version control, but it's annoying when child folders need their own
  // .gitignores.
  cacheDir: "./.cache",
})
