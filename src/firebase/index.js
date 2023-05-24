// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app"
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
const firebaseConfig = {
    apiKey: "AIzaSyAoFl20i5uM5KwllaLLig_8wXv0taSCNWQ",
    authDomain: "mealmingle-dcfc2.firebaseapp.com",
    projectId: "mealmingle-dcfc2",
    storageBucket: "mealmingle-dcfc2.appspot.com",
    messagingSenderId: "692987964873",
    appId: "1:692987964873:web:6b9dcc40477f81b4e14c18",
}

// Initialize Firebase
const firebaseApp = initializeApp(firebaseConfig)

export { firebaseApp }

