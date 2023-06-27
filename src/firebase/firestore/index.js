import { getFirestore } from "firebase/firestore";
import { firebaseApp } from "../index.js";

const firestore = getFirestore(firebaseApp);

export default firestore;