import { ThemeProvider, createTheme } from "@mui/material"
import 'material-icons/iconfont/material-icons.css'
import { BrowserRouter, Route, Routes } from "react-router-dom"
import Loader from "./components/loader/index.jsx"
import Profile from "./pages/profile/index.jsx"

function App() {
    const theme = createTheme({
        palette: {
            primary: {
                main: "#20c77c",
            },
            secondary: {
                main: "#0061A5",
            }
        },
        components: {
            MuiButton: {
                variants: [
                    {
                        props: { variant: "contained", color: "primary" },
                        style: {
                            color: "white",
                        }
                    }
                ]
            }
        },
        mode: "light",
    })

    return (
        <ThemeProvider theme={theme}>
            <Loader />
            {/* react router */}
            <BrowserRouter>
                <Routes>
                    <Route exact path="/" element={<h1>Home</h1>} />
                    <Route exact path="/profile" element={<Profile />} />
                    <Route exact path="/history" element={<h1>Foods</h1>} />
                </Routes>
            </BrowserRouter>

        </ThemeProvider>
    )
}

export default App
