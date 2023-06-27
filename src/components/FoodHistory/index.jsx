import { doc, onSnapshot } from "firebase/firestore";
import React, { useEffect, useState } from "react";
import firestore from "../../firebase/firestore/index.js";
import LoaderUtils from "../Loader/LoaderUtils.jsx";
import Hotel from "./Hotel.jsx";
import Ngo from "./NGO.jsx";

export default function FoodHistory() {
    const [uid, setUid] = useState(window.location.hash.split("=")[1])
    const [userData, setUserData] = useState({})

    useEffect(() => {
        // LoaderUtils.()
        // get data from firestore /users/uid/
        // set data to userData use firebase v9
        if (!uid) return
        const unsub = onSnapshot(doc(firestore, "users", uid), doc => {
            setUserData(doc.data())
            console.log(doc.data())
            LoaderUtils.unhalt()
        })

        return () => {
            unsub()
        }
    }, [uid])

    if (!uid || !userData.hasOwnProperty("type")) return <h1>Loading...</h1>
    if (userData.type === "NGO") return <Ngo ngo={userData.ngo} uid={uid} />
    if (userData.type === "HOTEL") return <Hotel hotels={userData.hotels} uid={uid} />

}