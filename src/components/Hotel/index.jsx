import { Alert, Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Divider, IconButton, Stack, TextField, useTheme } from '@mui/material';
import Typography from '@mui/material/Typography';
import { arrayRemove, arrayUnion, doc, updateDoc } from 'firebase/firestore';
import 'leaflet';
import 'leaflet/dist/leaflet.css';
import { encode } from 'ngeohash';
import React, { useEffect, useRef, useState } from "react";
import firestore from '../../firebase/firestore/index.js';
import LoaderUtils from '../Loader/LoaderUtils.jsx';
import randomId from '../randomId/index.js';

export default function Hotel({ hotels = [], uid }) {
    const theme = useTheme()
    const [addHotel, setAddHotel] = React.useState({
        open: false,
    })
    const [error, setError] = useState(null)
    const mapRef = useRef(null)

    function mapMove(e) {
        setAddHotel(h => ({...h, location: e.target.getLatLng()}))
    }
    
    useEffect(() => {
        if (addHotel.open === false) return
        setTimeout(() => {
            // check if map is already rendered
            if (mapRef.current._leaflet_id) return
            const map = L.map(mapRef.current).setView([51.505, -0.09], 13)
            setAddHotel(e => ({...e, map}))
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors',
            }).addTo(map);
            // navigator.permissions.query({ name: "geolocation" }).then(result => {
                // console.log(result)
                // if (result.state === "granted") {
                    // navigator.geolocation.getCurrentPosition((position) => {
                        // map.setView([position.coords.latitude, position.coords.longitude], 13)
                        // L.marker([position.coords.latitude, position.coords.longitude], {
                            //     draggable: true,
                            // }).addTo(map).bindPopup("You are here").on("moveend", mapMove)
                            // setAddHotel(e => ({...e, location: [position.coords.latitude, position.coords.longitude]}))
                            // })
                            // }
                            // })
            fetch("https://geo.ipify.org/api/v2/country,city?apiKey=at_0ekDKG5wYch97BNybAJcO4gRhuC0Y")
                .then(res => res.json())
                .then(data => {
                    const location = new L.LatLng(data.location.lat, data.location.lng)
                    map.setView(location, 13)
                    L.marker(location, {
                        draggable: true,
                    })
                        .addTo(map)
                        .bindPopup("You are here")
                        .on("moveend", mapMove)
                    setAddHotel(e => ({ ...e, location: [data.lat, data.lon] }))
                })
                            
            return () => {
                map.off('click', mapClick)
                map.remove()
            }
        }, 100)
    }, [addHotel.open])

    function addHotelSubmit(e) {
        e.preventDefault()
        setError(null)
        if (!addHotel.name || !addHotel.location) {
            setError("Enter Hotel details !")
            return
        }
        LoaderUtils.halt()
        // add hotel to database
        // add hotel to hotels array in fireabase firestore
        /*
            {
                hash: "cfff",
                hotelId: "hotel1",
                hotelName:"Dominos",
                lat: 0.1,
                lng: 0.1
            }
        */
        updateDoc(doc(firestore, "users", uid), {
            hotels: arrayUnion({
                hotelName: addHotel.name,
                hotelId: randomId(7),
                hash: encode(addHotel.location[0], addHotel.location[1], 9),
                lat: addHotel.location[0],
                lng: addHotel.location[1],
            })
        }).then(v => {
            setAddHotel(e => ({...e, open: false}))
        }).catch(e => {
            setError(e.message || e)
        }).finally(() => LoaderUtils.unhalt())

    }

    function deleteHotel(index) {
        LoaderUtils.halt()
        updateDoc(doc(firestore, "users", uid), {
            hotels: arrayRemove(hotels[index])
        }).catch(console.log)
        .finally(() => LoaderUtils.unhalt())
    }

    return (
        <Box
            sx={{
                p: 1,
                backgroundColor: theme.palette.background.main
            }}
        >
            <nav
                style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                }}
            >
                <Typography variant="h5" color="initial">
                    Hotels ( {hotels.length} )
                </Typography>
                <Button variant="contained" color="primary" onClick={() => setAddHotel(e => ({ ...e, open: true }))}>
                    Add{" "}
                    <span className="material-icons" style={{ fontSize: "1.5em", marginLeft: 8 }}>
                        add
                    </span>
                </Button>
            </nav>
            <Divider
                sx={{
                    pt: 2,
                }}
            ></Divider>

            <Stack spacing={1} p={2}>
                {hotels.map((hotel, index) => (
                    <div key={hotel.hotelId}>
                        <Box 
                            sx={{
                                display: "flex",
                                justifyContent: "space-between",
                                flexDirection: "row",
                                // borderBottom: "1px solid lightgrey"
                            }}
                        >
                            <Typography variant="h6" color="initial">
                                {hotel.hotelName}
                            </Typography>
                            <IconButton onClick={() => deleteHotel(index)} color="error">
                                <span className="material-icons">delete</span>
                            </IconButton>
                        </Box>
                        <Divider></Divider>
                    </div>
                ))}
            </Stack>

            <Dialog fullScreen open={addHotel.open} onClose={() => setAddHotel(e => ({ ...e, open: false }))} fullWidth>
                <DialogTitle>Add Hotel</DialogTitle>
                <DialogContent>
                    <form>
                        <TextField autoFocus fullWidth margin="normal" label="Hotel Name" onChange={e => setAddHotel(h => ({ ...h, name: e.target.value }))} />
                        <div
                            ref={mapRef}
                            style={{
                                height: 300,
                                width: "100%",
                            }}
                        ></div>
                    </form>
                    {error ? (
                        <>
                            <br></br>
                            <Alert severity="error">{error}</Alert>
                        </>
                    ) : null}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setAddHotel(e => ({ ...e, open: false }))} variant="text" type="reset" color="warning">
                        Cancel
                    </Button>
                    <Button onClick={addHotelSubmit} variant="contained" type="submit">
                        Add
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    )
}