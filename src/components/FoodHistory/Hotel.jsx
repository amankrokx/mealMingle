import { Alert, Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, FormControl, FormControlLabel, FormLabel, IconButton, InputLabel, MenuItem, Radio, RadioGroup, Select, Stack, TextField, Typography } from "@mui/material";
import { addDoc, collection, doc, increment, onSnapshot, query, serverTimestamp, updateDoc, where } from "firebase/firestore";
import React, { useEffect, useState } from "react";
import firestore from "../../firebase/firestore/index.js";

/*
description
"Very tasty chola bhatura with chola leftover from morning an dbhatura strong as frisbee. You eat once and never live to eat another day. This way it makes you never hungry again."
(string)
expiresAt
May 29, 2023 at 9:39:45 PM UTC+5:30
hash
"tdr1t"
hotelId
"YXLusT9"
hotelName
"Dominos"
lat
12.903073
lng
77.520209
name
"Chola Bhatura"
nonVeg
false
photoUrl
"https://picsum.photos/seed/food/200/200"
price
0
servings
10
*/

export default function Hotel({ hotels = [], uid }) {
    const [meals, setMeals] = useState([])
    const [mealData, setMealData] = useState({
        open: false,
        nonVeg: false,
        name: "",
        servings: 0,
        description: "",
        price: 0,
        hotelId: hotels[0]?.hotelId,
        hotelName: hotels[0]?.name,
        photoUrl: "https://picsum.photos/seed/food/200/200",
        expiresAt: serverTimestamp(),
    })
    const [error, setError] = useState(null)

    useEffect(() => {
        // gather hotel ids from hotels and find meals associated with them at meals/mealid/{hotelid: hotelid}
        // set meals to state
        const hotelIds = hotels.map(hotel => hotel.hotelId)
        if (hotelIds.length === 0) return console.log("No hotels found")
        // query firebase for meals based on hotelIds
        const q = query(collection(firestore, "meals"), where("hotelId", "in", hotelIds))
        const unsub = onSnapshot(q, (querySnapshot) => {
            const meals = []
            querySnapshot.forEach((doc) => {
                const meal = doc.data()
                meal.mealId = doc.id
                meals.push(meal)
            })
            // sort meals by expiresAt.seconds descending
            meals.sort((a, b) => {
                return b.expiresAt.seconds - a.expiresAt.seconds
            })
            console.log(meals)
            setMeals(meals)
        })
    }, [hotels, uid])

    function modifyServings (mealId, servings) {
        // modify servings of meal in firestore
        // find meal with mealId in meals
        updateDoc(doc(firestore, "meals", mealId), {
            servings: increment(servings)
        })
    }

    function createMeal () {
        setError(null)
        // validate mealData
        if (!mealData.name || !mealData.hotelId || !mealData.servings || !mealData.description || !mealData.price) {
            setError("Please fill all the fields")
            return
        }
        // create meal in firestore
        // add meal to meals and combine hotel info to meal
        const hotel = hotels.find(hotel => hotel.hotelId === mealData.hotelId)
        addDoc(collection(firestore, "meals"), {
            name: mealData.name,
            hotelId: mealData.hotelId,
            hotelName: hotel.hotelName,
            lat: hotel.lat,
            lng: hotel.lng,
            hash: hotel.hash,
            photoUrl: "https://picsum.photos/80",
            servings: parseInt(mealData.servings),
            nonVeg: mealData.nonVeg,
            description: mealData.description,
            expiresAt: serverTimestamp(),
            price: mealData.price,
        }).then(() => {
            setMealData({
                open: false,
            })
        }).catch(err => {
            setError(err.message || err)
        })
    }

    useEffect(() => {
        setError(null)
    }, [mealData])


    return (
        <Box p={1}>
            <nav
                style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                }}
            >
                <Typography variant="h5" color="initial">
                    Meals History
                </Typography>
                <Button variant="contained" color="primary"
                    disabled={hotels.length === 0}
                    onClick={() => {
                        setMealData({
                            open: true,
                        })
                    }}
                >
                    Add New
                </Button>
            </nav>
            <br></br>
            {hotels.length === 0 ? <Alert severity="info">You don't have any hotels yet</Alert> : null}
            <Stack spacing={1}>
                {meals.map((meal, index) => {
                    return (
                        <Box
                            key={index}
                            sx={{
                                borderRadius: "5px",
                                padding: "1rem",
                                display: "flex",
                                justifyContent: "space-between",
                                alignItems: "center",
                                borderBottom: "1px solid lightgrey",
                            }}
                        >
                            <img
                                src={meal.photoUrl}
                                alt={meal.name}
                                style={{
                                    height: "80px",
                                    aspectRatio: "1/1",
                                    marginRight: "1rem",
                                }}
                            />
                            <Box
                                sx={{
                                    marginLeft: 0,
                                    marginRight: "auto",
                                }}
                            >
                                <Typography variant="h6" color="initial">
                                    {meal.name}
                                    <small>
                                        <sup style={{ marginLeft: 8 }}>{meal.nonVeg ? "NonVeg" : "Veg"}</sup>
                                    </small>
                                </Typography>
                                <Typography variant="caption" color="initial">
                                    {meal.expiresAt.toDate().toLocaleString()}
                                </Typography>
                                <Typography variant="body2" color={meal.servings > 3 ? "primary" : "error"}>
                                    Servings Left: {meal.servings}
                                </Typography>
                            </Box>
                            <Box
                                sx={{
                                    marginRight: 0,
                                    float: "right",
                                    display: "flex",
                                    flexDirection: "column",
                                }}
                            >
                                <IconButton color="primary" onClick={() => modifyServings(meal.mealId, 1)}>
                                    <span className="material-icons">expand_less</span>
                                </IconButton>
                                <IconButton color="warning" onClick={() => modifyServings(meal.mealId, -1)}>
                                    <span className="material-icons">expand_more</span>
                                </IconButton>
                            </Box>
                        </Box>
                    )
                })}
            </Stack>
            <Dialog fullScreen open={mealData.open} onClose={() => setMealData(e => ({ ...e, open: false }))}>
                <DialogTitle>Add Meal</DialogTitle>
                <DialogContent>
                    <Stack style={{padding: "8px 0"}} spacing={2}>
                        <TextField
                            label="Meal Name"
                            onChange={(e) => setMealData(h => ({ ...h, name: e.target.value }))}
                            fullWidth
                        />
                        <TextField
                            label="Meal Description"
                            onChange={(e) => setMealData(h => ({ ...h, description: e.target.value }))}
                            rows={3}
                            multiline
                            fullWidth
                        />
                        <TextField
                            label="Meal Price"
                            onChange={(e) => setMealData(h => ({ ...h, price: e.target.value }))}
                            fullWidth
                            type="number"
                            defaultValue={0}
                        />
                        <TextField
                            label="Meal Servings"
                            onChange={(e) => setMealData(h => ({ ...h, servings: e.target.value }))}
                            fullWidth
                            type="number"
                            defaultValue={0}
                        />
                        {/* radio fro ceg or nonveg */}
                        <FormControl component="fieldset">
                            <FormLabel component="legend">Meal Type</FormLabel>
                            <RadioGroup
                                aria-label="meal type"
                                name="meal type"
                                value={mealData?.nonVeg || false}
                                onChange={(e) => setMealData(h => ({ ...h, nonVeg: e.target.value }))}
                            >
                                <FormControlLabel value={false} control={<Radio />} label="Veg" />
                                <FormControlLabel value={true} control={<Radio />} label="Non-Veg" />
                            </RadioGroup>
                        </FormControl>
                        {/* drpodown for choosing hotel */}
                        <FormControl fullWidth>
                            <InputLabel id="demo-simple-select-label">Hotel</InputLabel>
                            <Select
                                labelId="demo-simple-select-label"
                                id="demo-simple-select"
                                value={mealData?.hotelId || ""}
                                label="Hotel"
                                onChange={(e) => setMealData(h => ({ ...h, hotelId: e.target.value }))}
                            >
                                {hotels.map((hotel, index) => {
                                    return (
                                        <MenuItem key={index} value={hotel.hotelId}>{hotel.hotelName}</MenuItem>
                                    )
                                })}
                            </Select>
                        </FormControl>
                    </Stack>
                    {error ? <Alert security="error" severity="error">{error}</Alert> : null}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setMealData(e => ({ ...e, open: false }))} color="warning">Cancel</Button>
                    <Button onClick={() => createMeal()} variant="contained" >Add</Button>
                </DialogActions>
            </Dialog>
        </Box>
    )
}