import { doc, onSnapshot } from "firebase/firestore"
import { useEffect, useState } from "react"
import Hotel from "../../components/Hotel/index.jsx"
import LoaderUtils from "../../components/Loader/LoaderUtils.jsx"
import Ngo from "../../components/Ngo/index.jsx"
import firestore from "../../firebase/firestore/index.js"

export default function Profile() {
    const [uid, setUid] = useState(window.location.hash.split("=")[1])
    const [userData, setUserData] = useState({})

    useEffect(() => {
        // LoaderUtils.()
        // get data from firestore /users/uid/
        // set data to userData use firebase v9
        if (!uid) return
        const unsub = onSnapshot(doc(firestore, "users", uid), (doc) => {
            setUserData(doc.data())
            console.log(doc.data())
            LoaderUtils.unhalt()
        })

        return () => {
            unsub()
        }
    }, [uid])

    if (!uid || !userData.hasOwnProperty('type')) return <h1>Loading...</h1>
    if (userData.type === "NGO") return <Ngo ngo={userData.ngo} uid={uid} />
    if (userData.type === "HOTEL") return <Hotel hotels={userData.hotels} uid={uid} />

}