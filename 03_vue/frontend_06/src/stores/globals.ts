import { defineStore } from "pinia";
import { ref, type Ref } from "vue";

/**
 * globals is a reactive data store, made with Pinia.  It holds all of the
 * global state of the program.
 */
export const globals = defineStore('globals', () => {
  /** `popup` controls the popup for info and error messages */
  const popup = ref({ msg: "", header: "", element: undefined as undefined | Ref });

  /** show the popup */
  function showPopup(header: string, message: string) {
    popup.value.msg = message;
    popup.value.header = header;
    popup.value.element.showModal();
  }

  /** hide the pop-up */
  function clearPopup() {
    popup.value.msg = "";
    popup.value.header = "";
    popup.value.element.close();
  }

  return { popup, showPopup, clearPopup }
});
