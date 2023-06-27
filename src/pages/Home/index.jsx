import { Box, useMediaQuery, useTheme } from "@mui/material";
import React from "react";
import "./index.css";


export default function Home() {
    const theme = useTheme();
    const matches = useMediaQuery(theme => theme.breakpoints.down("sm"))

    return <Box>
        jhvhtj
    </Box>
}