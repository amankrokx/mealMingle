import { IconButton } from "@mui/material"
import AppBar from "@mui/material/AppBar"
import Container from "@mui/material/Container"
import Slide from "@mui/material/Slide"
import Toolbar from "@mui/material/Toolbar"
import Typography from "@mui/material/Typography"
import useScrollTrigger from "@mui/material/useScrollTrigger"
import React, { useContext } from "react"
import { useNavigate } from "react-router-dom"
import AuthContext from "../../firebase/auth/AuthContext"
import AccountMenu from "../MiniLogin/AccountMenu"

export default function Navbar () {
    const trigger = useScrollTrigger()
    const authContext = useContext(AuthContext)
    const navigate = useNavigate()

    return (
        <>
            <Slide appear={false} direction="down" in={!trigger}>
                <AppBar position="sticky" style={{ zIndex: 5 }}>
                    <Container maxWidth="xl">
                        <Toolbar
                            disableGutters
                            style={{
                                justifyContent: "space-between",
                                flexDirection: "row-reverse",
                            }}
                        >
                            {/* <img
                            src={logo}
                            alt="Logo"
                            className="navIcon"
                            style={{
                                height: matches ? "42px" : "36px",
                                width: matches ? "42px" : "36px",
                                borderRadius: 8,
                                // margin: "0 11px",
                            }}
                        ></img> */}
                            <Typography
                                color="secondary"
                                style={{
                                    fontWeight: "bold",
                                    // margin: "10px",
                                    fontSize: "1.5em",
                                    position: "absolute",
                                    left: "50%",
                                    transform: "translate(-50%, 0)",
                                }}
                            >
                                Video Site
                            </Typography>
                            <div>
                                {authContext?.user?.isCreator && window.location.pathname !== "/upload" && (
                                    <IconButton
                                        onClick={() => {
                                            navigate("/upload")
                                        }}
                                    >
                                        <span className="material-icons">library_add</span>
                                    </IconButton>
                                )}
                                {window.location.pathname !== "/" && authContext.user && (
                                    <IconButton
                                        onClick={() => {
                                            navigate("/")
                                        }}
                                    >
                                        <span className="material-icons">home</span>
                                    </IconButton>
                                )}
                                {authContext.user && window.location.pathname != "/login" ? <AccountMenu authContext={authContext} /> : null}
                            </div>
                        </Toolbar>
                    </Container>
                </AppBar>
            </Slide>
        </>
    )
}
