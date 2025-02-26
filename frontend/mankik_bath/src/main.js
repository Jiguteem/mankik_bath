import { createApp } from 'vue'
import App from './App.vue'
//import router from './router'
import Toast from 'vue-toastification'
import 'vue-toastification/dist/index.css'
import VueSlider from 'vue-slider-component'
import 'vue-slider-component/theme/default.css'
import VCalendar from 'v-calendar';
import 'v-calendar/style.css';


import axios from 'axios'

/* import the fontawesome core */
import { library } from '@fortawesome/fontawesome-svg-core'

/* 
   import specific icons 
   원하는 아이콘을 개별적으로 import
*/
import { faUserSecret, faArrowLeft, faBell, faPlay, faStop, faCircleNotch, faPlus, faChevronDown, faXmark,faRotateRight, faWater, faTemperature3, faClock } from '@fortawesome/free-solid-svg-icons'

/* import font awesome 컴포넌트 */
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

/* 위에서 import한 아이콘들을 Core library에 등록 */
library.add(faUserSecret,faArrowLeft, faBell, faPlay, faStop, faCircleNotch, faPlus,faChevronDown, faXmark, faRotateRight, faWater, faTemperature3, faClock)
//http://3.38.251.7:8080/
axios.defaults.baseURL ="http://13.125.231.37:8080/"
const app = createApp(App)
app.component('font-awesome-icon',FontAwesomeIcon)
app.config.productionTip=false
app.config.globalProperties.$http = axios;
app.component('VueSlider', VueSlider)
app.use(Toast).use(VCalendar, {}).mount('#app')